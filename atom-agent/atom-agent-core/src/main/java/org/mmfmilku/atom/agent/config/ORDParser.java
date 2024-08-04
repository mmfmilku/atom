/********************************************
 * 文件名称: ORDParser.java
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

import org.mmfmilku.atom.agent.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ORDParser
 *
 * @author chenxp
 * @date 2024/6/19:13:35
 */
public class ORDParser {

    public static final String importReg = "\\s*import\\s+[\\w.]+;";
    // import (any words);
    private static final Pattern importPattern = Pattern.compile(importReg + "[\\s\\S]*");

    private static final String methodReg = "(\\s*" + Keywords.toRegStr() + "\\s+\\w+\\s*([\\s\\S]*?)\\s*\\{[\\s\\S]*?\\}\\s*)";
    // method (any) { any code }
    private static final Pattern methodPattern = Pattern.compile(methodReg);

    // class  {  any code  }
    private static final Pattern classPattern = Pattern.compile("(" +
            "(" + importReg + ")*" +
            "\\s*class\\s+[\\w.]+\\s*\\{" + 
                methodReg + 
            "\\}\\s*" +
            ")");

    public static Map<String, ClassORDDefine> parse(String classORDText) {
        // 通过正则获取单个类的覆写部分
        Matcher matcher = classPattern.matcher(classORDText);
        if (!matcher.matches()) {
            throw new RuntimeException("can not load invalid ord file");
        }
        Set<String> importSet = new HashSet<>();
        String ordTestAfterImport = parseImport(classORDText, importSet);
        System.out.println(importSet);
        Map<String, ClassORDDefine> overrideClassMap = new HashMap<>();
        parse(ordTestAfterImport, overrideClassMap);
        dealImport(overrideClassMap, importSet);
        return overrideClassMap;
    }

    private static void dealImport(Map<String, ClassORDDefine> overrideClassMap, Set<String> importSet) {
        if (importSet.size() < 1) {
            return;
        }
        overrideClassMap.forEach((s, classORDDefine) -> {
            classORDDefine.getMethodORDMap().forEach((s1, methodORDDefine) -> {
                Map<Keywords, String> srcMap = methodORDDefine.getSrcMap();
                Map<Keywords, String> importedMap = new HashMap<>(srcMap.size());
                srcMap.forEach((s2, src) -> {
                    String result = src;
                    if (!StringUtils.isEmpty(result) && !isEmptyBlock(result)) {
                        for (String importClass : importSet) {
                            // TODO @chenxp 2024/8/2 防重复替换
                            // TODO @chenxp 2024/8/2 import xxx.xxx.* 的处理
                            // TODO @chenxp 2024/8/2 某些不能替换的qingk
                            String classShortName = StringUtils.rightmost(importClass, ".");
                            result = StringUtils.replaceNoRepeat(result, classShortName, importClass);
                        }
                    }
                    importedMap.put(s2, result);
                });
                methodORDDefine.setSrcMap(importedMap);
            });
        });
    }

    private static String parseImport(String classORDText, Set<String> importSet) {
        Matcher matcher = importPattern.matcher(classORDText);
        if (!matcher.matches()) {
            return classORDText;
        }
        String anImport = classORDText.substring(classORDText.indexOf("import") + 6, classORDText.indexOf(";")).trim();
        importSet.add(anImport);
        return parseImport(classORDText.substring(classORDText.indexOf(";") + 1).trim(), importSet);
    }

    private static void parse(String classORDText, Map<String, ClassORDDefine> overrideClassMap) {
        Matcher matcher = classPattern.matcher(classORDText);
        if (!matcher.matches()) {
            return;
        }
        ClassORDDefine classOrdDefine = new ClassORDDefine();
        Map<String, MethodORDDefine> ordMethod = new HashMap<>();
        classOrdDefine.setMethodORDMap(ordMethod);
        int start = classORDText.indexOf("{");
        String fullName = classORDText.substring(classORDText.indexOf("class") + 5, start).trim();
        classOrdDefine.setName(fullName);
        String codeText = classORDText.substring(start + 1).trim();
        // 截取类名后，加载代码体部分
        String remainText = loadMethodORD(codeText, ordMethod);
        int end = remainText.indexOf("}");
        if (end >= 0) {
            overrideClassMap.put(fullName, classOrdDefine);
            parse(remainText.substring(end + 1), overrideClassMap);
        }
    }

    private static String loadMethodORD(String methodORDText, Map<String, MethodORDDefine> ordMethod) {
        if (!methodPattern.matcher(methodORDText).matches()) {
            return methodORDText;
        }
        Keywords keyword = getKeyword(methodORDText);
        String methodName = getMethodName(methodORDText, keyword);
        MethodORDDefine methodORDDefine = ordMethod.get(methodName);
        if (methodORDDefine == null) {
            methodORDDefine = new MethodORDDefine(methodName);
            ordMethod.put(methodName, methodORDDefine);
        }
        // 保存对应关键字方法参数
        List<String> methodParam = getMethodParam(methodORDText);
        if (methodParam.size() > 0) {
            methodORDDefine.getParamMap().put(keyword, methodParam);
        }
        
        char[] chars = methodORDText.toCharArray();
        // 需要匹配的反括号}次数，方法体中可能存在大括号，故次数可能增加
        int findCount = 1;
        int end = -1;
        int start = methodORDText.indexOf("{");
        for (int i = start + 1; i < chars.length; i++) {
            if (chars[i] == '{') {
                findCount++;
            } else if (chars[i] == '}') {
                findCount--;
            }
            if (findCount == 0) {
                // 匹配了最终的反括号}表示方法结束
                end = i;
                break;
            }
        }
        if (end != -1) {
            methodORDDefine.getSrcMap().put(keyword, "{" + methodORDText.substring(start + 1, end).trim() + "}");
//            ordMethod.put(methodName, "{" + methodORDText.substring(start + 1, end).trim() + "}");
            if (methodORDText.length() > end + 1) {
                // 递归截取加载方法覆写
                return loadMethodORD(methodORDText.substring(end + 1).trim(), ordMethod);
            }
        }
        return "";
    }

    private static Keywords getKeyword(String methodORDText) {
        for (Keywords value : Keywords.values()) {
            String keyword = value.getKeyword();
            if (methodORDText.startsWith(keyword + " ")) {
                return value;
            }
        }
        throw new RuntimeException("can not find keyword ");
    }

    /**
     * 获取关键字后，括号前的部分为方法名 
     * 如：keyword methodName (param1,param2) {}
     * 返回：methodName
     * 
     *
     */
    private static String getMethodName(String methodORDText, Keywords keywords) {
        String keyword = keywords.getKeyword();
        return methodORDText.substring(methodORDText.indexOf(keyword) + keyword.length(), methodORDText.indexOf("(")).trim();
    }

    /**
     * 获取括号直接的部分为参数 
     * 如：keyword methodName (param1,param2) {}
     * 返回：[param1,param2]
     */
    private static List<String> getMethodParam(String methodORDText) {
        String methodDesc = methodORDText.substring(0, methodORDText.indexOf("{"));
        String paramStr = methodDesc.substring(methodDesc.indexOf("(") + 1, methodDesc.lastIndexOf(")"));
        if (paramStr.length() > 0) {
            String[] split = paramStr.split("\\s*,\\s*");
            return Arrays.asList(split);
        }
        return Collections.emptyList();
    }

    /**
     * 是否空代码块 {}
     * */
    public static boolean isEmptyBlock(String src) {
        return src.substring(src.indexOf("{") + 1, src.lastIndexOf("}")).trim().length() == 0;
    }
    
}
