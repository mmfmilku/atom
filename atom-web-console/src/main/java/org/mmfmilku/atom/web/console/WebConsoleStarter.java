package org.mmfmilku.atom.web.console; /********************************************
 * 文件名称: ConsoleStarter.java
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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ConsoleStarter
 *
 * @author chenxp
 * @date 2024/7/26:13:53
 */
//@ServletComponentScan
@SpringBootApplication(scanBasePackages = "org.mmfmilku.atom.web.console")
public class WebConsoleStarter {

    public static void main(String[] args) {
        SpringApplication.run(WebConsoleStarter.class, args);
    }
    
}
