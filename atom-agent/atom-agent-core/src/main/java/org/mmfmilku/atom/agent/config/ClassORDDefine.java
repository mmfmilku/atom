/********************************************
 * 文件名称: ORDDefine.java
 * 系统名称: 综合理财管理平台6.0
 * 模块名称:
 * 软件版权: 恒生电子股份有限公司
 * 功能说明:
 * 系统版本: 6.0.0.1
 * 开发人员: chenxp
 * 开发时间: 2024/6/5
 * 审核人员:
 * 相关文档:
 * 修改记录:   修改日期    修改人员    修改单号       版本号                   修改说明
 * V6.0.0.1  20240605-01  chenxp   TXXXXXXXXXXXX    IFMS6.0VXXXXXXXXXXXXX   新增 
 *********************************************/
package org.mmfmilku.atom.agent.config;

import java.util.Map;

/**
 * ORDDefine
 *
 * @author chenxp
 * @date 2024/6/5:14:27
 */
public class ClassORDDefine {
    
    private String name;
    
    private Map<String, MethodORDDefine> methodORDMap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, MethodORDDefine> getMethodORDMap() {
        return methodORDMap;
    }

    public void setMethodORDMap(Map<String, MethodORDDefine> methodORDMap) {
        this.methodORDMap = methodORDMap;
    }

    @Override
    public String toString() {
        return "ClassORDDefine{" +
                "name='" + name + '\'' +
                ", methodORDMap=" + methodORDMap +
                '}';
    }
}
