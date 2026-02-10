package org.mmfmilku.atom.web.console.config;

import org.mmfmilku.atom.web.console.interceptor.ProxyInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web配置
 **/
@Component
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ProxyInterceptor proxyInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 配置拦截器路径
        registry.addInterceptor(proxyInterceptor)
                // 排除主节点请求
                .excludePathPatterns("/master/**")
                .excludePathPatterns("/main/**")
                // 设置执行顺序
                .order(1);
    }


}
