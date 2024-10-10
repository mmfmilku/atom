/********************************************
 * 文件名称: IndexController.java
 * 系统名称: 综合理财管理平台6.0
 * 模块名称:
 * 软件版权: 恒生电子股份有限公司
 * 功能说明:
 * 系统版本: 6.0.0.1
 * 开发人员: chenxp
 * 开发时间: 2024/7/26
 * 审核人员:
 * 相关文档:
 * 修改记录:   修改日期    修改人员    修改单号       版本号                   修改说明
 * V6.0.0.1  20240726-01  chenxp   TXXXXXXXXXXXX    IFMS6.0VXXXXXXXXXXXXX   新增 
 *********************************************/
package org.mmfmilku.atom.web.console.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * IndexController
 *
 * @author chenxp
 * @date 2024/7/26:16:17
 */
@Controller
public class IndexController {

    @RequestMapping("hello")
    @ResponseBody
    public String hello() {
        return "welcome to the atom!";
    }

    @RequestMapping("main")
    public String mainPage() {
        return "index.html";
    }
    
}
