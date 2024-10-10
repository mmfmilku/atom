/********************************************
 * 文件名称: MethodORDDefine.java
 * 系统名称: 综合理财管理平台6.0
 * 模块名称:
 * 软件版权: 恒生电子股份有限公司
 * 功能说明:
 * 系统版本: 6.0.0.1
 * 开发人员: chenxp
 * 开发时间: 2024/6/19
 * 审核人员:
 * 相关文档:
 * 修改记录:   修改日期    修改人员    修改单号       版本号                   修改说明
 * V6.0.0.1  20240619-01  chenxp   TXXXXXXXXXXXX    IFMS6.0VXXXXXXXXXXXXX   新增 
 *********************************************/
package org.mmfmilku.atom.agent.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MethodORDDefine
 *
 * @author chenxp
 * @date 2024/6/19:9:19
 */
public class MethodORDDefine {

    private String methodName;

    private Map<Keywords, String> srcMap;
    
    private Map<Keywords, List<String>> paramMap;

    public MethodORDDefine(String methodName) {
        this.methodName = methodName;
        this.srcMap = new HashMap<>();
        this.paramMap = new HashMap<>();
    }

    public Map<Keywords, List<String>> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<Keywords, List<String>> paramMap) {
        this.paramMap = paramMap;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Map<Keywords, String> getSrcMap() {
        return srcMap;
    }

    public void setSrcMap(Map<Keywords, String> srcMap) {
        this.srcMap = srcMap;
    }

    @Override
    public String toString() {
        return "MethodORDDefine{" +
                "methodName='" + methodName + '\'' +
                ", srcMap=" + srcMap +
                ", paramMap=" + paramMap +
                '}';
    }
}
