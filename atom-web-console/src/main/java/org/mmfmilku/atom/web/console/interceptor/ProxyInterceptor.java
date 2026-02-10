package org.mmfmilku.atom.web.console.interceptor;

import com.alibaba.fastjson.TypeReference;
import org.mmfmilku.atom.util.StringUtils;
import org.mmfmilku.atom.web.console.controller.MasterController;
import org.mmfmilku.atom.web.console.domain.EnvVO;
import org.mmfmilku.atom.web.console.interfaces.IPersistService;
import org.mmfmilku.atom.web.console.service.PersistDataOpt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * 请求转发
 **/
@Component
public class ProxyInterceptor implements HandlerInterceptor {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${console.run.mode:1}")
    private String runMode;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("2".equals(runMode)) {
            // 子节点模式不经过代理
            return true;
        }

        try {
            // 1. 判断是否需要转发
            if (shouldProxy(request)) {
                // 2. 构建目标URL
                String targetBaseUrl = getTargetBaseUrl(request);
                String targetUrl = buildTargetUrl(request, targetBaseUrl);

                // 3. 执行转发
                try {
                    ResponseEntity<byte[]> proxyResponse = forwardRequest(request, targetUrl);

                    // 4. 将响应写回客户端
                    writeResponse(response, proxyResponse);

                    // 拦截请求，不再继续处理
                    return false;

                } catch (Exception e) {
                    handleProxyError(response, e);
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write("{\"error\": \"internal err\", \"message\": \"internal err\"}");
            return false;
        }

        // 继续处理请求
        return true;
    }

    private boolean shouldProxy(HttpServletRequest request) {
        String proxyEnv = request.getHeader("ATOM-PROXY-ENV");
        if (StringUtils.isEmpty(proxyEnv) || "local".equals(proxyEnv)) {
            return false;
        }
        return HttpMethod.POST.matches(request.getMethod());
    }

    @Autowired
    PersistDataOpt dataOpt;

    /**
     * 获取目标服务基础URL
     */
    private String getTargetBaseUrl(HttpServletRequest request) {
        Map<String, EnvVO> envMap = dataOpt.getEnvMap();
        String proxyEnv = request.getHeader("ATOM-PROXY-ENV");
        EnvVO envVO = envMap.get(proxyEnv);
        return String.format("http://%s:%s", envVO.getHost(), envVO.getPort());
    }

    /**
     * 构建完整的目标URL
     */
    private String buildTargetUrl(HttpServletRequest request, String baseUrl) {
        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();

        // 路径处理 TODO
        String path = requestUri;

        // 构建完整URL
        String targetUrl = baseUrl + path;

        if (queryString != null && !queryString.isEmpty()) {
            targetUrl += "?" + queryString;
        }

        return targetUrl;
    }

    /**
     * 转发请求
     */
    private ResponseEntity<byte[]> forwardRequest(HttpServletRequest request, String targetUrl)
            throws IOException {

        // 1. 获取请求方法
        HttpMethod method = HttpMethod.resolve(request.getMethod());

        // 2. 构建请求头
        HttpHeaders headers = extractHeaders(request);

        // 3. 获取请求体
        byte[] requestBody = getRequestBody(request);

        // 4. 构建请求实体
        HttpEntity<byte[]> entity = new HttpEntity<>(requestBody, headers);

        // 5. 执行转发请求
        System.out.println("执行转发" + targetUrl);
        return restTemplate.exchange(
                targetUrl,
                method,
                entity,
                byte[].class
        );
    }

    /**
     * 提取请求头
     */
    private HttpHeaders extractHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);

            // 排除不需要转发的头
            if (!shouldExcludeHeader(headerName)) {
                headers.add(headerName, headerValue);
            }
        }

        // 可以添加额外的头
        headers.add("X-Forwarded-For", request.getRemoteAddr());
        headers.add("X-Forwarded-Host", request.getServerName());
        headers.add("X-Forwarded-Proto", request.getScheme());

        return headers;
    }

    /**
     * 判断是否排除某个请求头
     */
    private boolean shouldExcludeHeader(String headerName) {
        List<String> excludedHeaders = Arrays.asList(
                "host", "connection", "content-length",
                "accept-encoding", "user-agent"
        );

        return excludedHeaders.contains(headerName.toLowerCase());
    }

    /**
     * 获取请求体
     */
    private byte[] getRequestBody(HttpServletRequest request) throws IOException {
        if ("GET".equalsIgnoreCase(request.getMethod()) ||
                "HEAD".equalsIgnoreCase(request.getMethod())) {
            return null;
        }

        return StreamUtils.copyToByteArray(request.getInputStream());
    }

    /**
     * 将代理响应写回客户端
     */
    private void writeResponse(HttpServletResponse response, ResponseEntity<byte[]> proxyResponse)
            throws IOException {

        // 1. 设置状态码
        response.setStatus(proxyResponse.getStatusCodeValue());

        // 2. 设置响应头
        proxyResponse.getHeaders().forEach((headerName, headerValues) -> {
            if (!shouldExcludeResponseHeader(headerName)) {
                headerValues.forEach(headerValue ->
                        response.addHeader(headerName, headerValue));
            }
        });

        // 3. 设置响应体
        if (proxyResponse.getBody() != null) {
            System.out.println("代理响应body:" + new String(proxyResponse.getBody()));
            response.getOutputStream().write(proxyResponse.getBody());
        }
    }

    /**
     * 判断是否排除某个响应头
     */
    private boolean shouldExcludeResponseHeader(String headerName) {
        List<String> excludedHeaders = Arrays.asList(
                "connection", "content-length",
                "transfer-encoding", "keep-alive"
        );

        return excludedHeaders.contains(headerName.toLowerCase());
    }

    /**
     * 处理转发错误
     */
    private void handleProxyError(HttpServletResponse response, Exception e)
            throws IOException {

        if (e instanceof HttpClientErrorException) {
            // 4xx 错误
            HttpClientErrorException clientError = (HttpClientErrorException) e;
            response.setStatus(clientError.getStatusCode().value());
            response.getWriter().write(clientError.getResponseBodyAsString());

        } else if (e instanceof HttpServerErrorException) {
            // 5xx 错误
            HttpServerErrorException serverError = (HttpServerErrorException) e;
            response.setStatus(serverError.getStatusCode().value());
            response.getWriter().write(serverError.getResponseBodyAsString());

        } else {
            // 其他错误（超时、连接失败等）
            response.setStatus(HttpStatus.BAD_GATEWAY.value());
            response.getWriter().write("{\"error\": \"request env fail\", \"message\": \"" +
                    e.getMessage() + "\"}");
        }
    }
}
