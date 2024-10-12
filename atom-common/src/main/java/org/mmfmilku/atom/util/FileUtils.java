/********************************************
 * 文件名称: FileUtils.java
 * 系统名称: 综合理财管理平台6.0
 * 模块名称:
 * 软件版权: 恒生电子股份有限公司
 * 功能说明:
 * 系统版本: 6.0.0.1
 * 开发人员: chenxp
 * 开发时间: 2024/6/4
 * 审核人员:
 * 相关文档:
 * 修改记录:   修改日期    修改人员    修改单号       版本号                   修改说明
 * V6.0.0.1  20240604-01  chenxp   TXXXXXXXXXXXX    IFMS6.0VXXXXXXXXXXXXX   新增 
 *********************************************/
package org.mmfmilku.atom.util;

import java.io.*;
import java.util.Map;
import java.util.function.Consumer;

/**
 * FileUtils
 *
 * @author chenxp
 * @date 2024/6/4:18:44
 */
public class FileUtils {
    
    public static boolean isFile(String path) {
        File file = new File(path);
        return file.isFile();
    }

    public static String[] childrenPath(String path) {
        File file = new File(path);
        if (file.isFile()) {
            return new String[0];
        }
        File[] children = file.listFiles();
        if (children == null) {
            return new String[0];
        }
        String[] childrenPath = new String[children.length];
        for (int i = 0; i < children.length; i++) {
            childrenPath[i] = children[i].getAbsolutePath();
        }
        return childrenPath;
    }

    public static String readText(String file) throws IOException {
        StringBuilder content = new StringBuilder();
        forEachLine(file, line -> content.append(line).append("\n"));
        return content.toString();
    }

    public static void forEachLine(String file, Consumer<String> consumer) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                consumer.accept(line);
            }
        }
    }
    
}
