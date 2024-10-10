/********************************************
 * 文件名称: StringUtils.java
 * 系统名称: 综合理财管理平台6.0
 * 模块名称:
 * 软件版权: 恒生电子股份有限公司
 * 功能说明:
 * 系统版本: 6.0.0.1
 * 开发人员: chenxp
 * 开发时间: 2024/8/2
 * 审核人员:
 * 相关文档:
 * 修改记录:   修改日期    修改人员    修改单号       版本号                   修改说明
 * V6.0.0.1  20240802-01  chenxp   TXXXXXXXXXXXX    IFMS6.0VXXXXXXXXXXXXX   新增 
 *********************************************/
package org.mmfmilku.atom.agent.util;

/**
 * StringUtils
 *
 * @author chenxp
 * @date 2024/8/2:15:33
 */
public class StringUtils {
    
    private static final String EMPTY_STR = "";
    
    /**
     * 获取某字符串右边的字符串
     * */
    public static String right(String str, String forRight) {
        if (isEmpty(str)) {
            return str;
        }
        int i = str.indexOf(forRight);
        if (i < 0) {
            return str;
        }
        return str.substring(i + forRight.length());
    }

    /**
     * 获取某字符串最右边的字符串
     * */
    public static String rightmost(String str, String forRight) {
        if (isEmpty(str)) {
            return str;
        }
        int i = str.lastIndexOf(forRight);
        if (i < 0) {
            return str;
        }
        return str.substring(i + forRight.length());
    }
    
    /**
     * 截取两个字符串中间的字符串
     * */
    public static String between(String str, String startStr, String endStr) {
        if (isEmpty(str)) {
            return EMPTY_STR;
        }
        int i = str.indexOf(startStr);
        int j = str.indexOf(endStr);
        if (i < 0) {
            if (j < 0) {
                return EMPTY_STR;
            }
            // no start
            return str.substring(0, j);
        }

        if (j < 0) {
            // no end
            return str.substring(i + startStr.length());
        }
        //  between start and end
        return str.substring(i + startStr.length(), j);
    }

    /**
     * 获取两个字符串包裹的内容
     * (a22ab33bf, a, b) -> 22ab33
     * */
    public static String surroundedStr(String str, String startStr, String endStr) {
        if (isEmpty(str)) {
            return EMPTY_STR;
        }
        int i = str.indexOf(startStr);
        int j = str.lastIndexOf(endStr);
        if (i < 0 || j < 0) {
            return EMPTY_STR;
        }
        //  between start and end
        return str.substring(i + startStr.length(), j);
    }

    /**
     * 是否空
     * */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 字符串防重复替换，多次调用只会替换一次
     * */
    public static String replaceNoRepeat(String str, String target, String replacement) {
        return str.replaceAll(target, replacement);
    }
    
}
