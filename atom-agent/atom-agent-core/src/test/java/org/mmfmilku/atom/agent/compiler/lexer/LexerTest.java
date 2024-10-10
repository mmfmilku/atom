package org.mmfmilku.atom.agent.compiler.lexer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.junit.Test;
import org.mmfmilku.atom.agent.util.FileUtils;

import java.io.IOException;

import static org.junit.Assert.*;

public class LexerTest {

    @Test
    public void testLexer() throws IOException {
//        String s = FileUtils.readText("F:\\dev\\project\\atom\\atom-agent\\atom-agent-core\\src\\main\\java\\org\\mmfmilku\\atom\\agent\\config\\ORDParser.java");
        String s = "/********************************************\n" +
                " * 文件名称: ORDParser.java\n" +
                " * 系统名称: 综合理财管理平台6.0\n" +
                " * 模块名称:\n" +
                " * 软件版权: 恒生电子股份有限公司\n" +
                " * 功能说明:\n" +
                " * 系统版本: 6.0.0.1\n" +
                " * 开发人员: chenxp\n" +
                " * 开发时间: 2024/6/19\n" +
                " * 审核人员:\n" +
                " * 相关文档:\n" +
                " * 修改记录:   修改日期    修改人员    修改单号       版本号                   修改说明\n" +
                " * V6.0.0.1  20240619-01  chenxp   TXXXXXXXXXXXX    IFMS6.0VXXXXXXXXXXXXX   新增 \n" +
                " *********************************************/\n" +
                "package org.mmfmilku.atom.agent.config;\n" +
                "\n" +
                "import org.mmfmilku.atom.agent.util.StringUtils;\n" +
                "\n" +
                "import java.util.*;\n" +
                "import java.util.regex.Matcher;\n" +
                "import java.util.regex.Pattern;\n" +
                "\n" +
                "/**\n" +
                " * ORDParser\n" +
                " *\n" +
                " * @author chenxp\n" +
                " * @date 2024/6/19:13:35\n" +
                " */\n" +
                "public class ORDParser {\n" +
                "\n" +
                "    public static final String importReg = \"\\\\s*import\\\\s+[\\\\w.]+;\";\n" +
                "    // import (any words);\n" +
                "    private static final Pattern importPattern = Pattern.compile(importReg + \"[\\\\s\\\\S]*\");\n" +
                "\n" +
                "    private static final String methodReg = \"(\\\\s*\" + Keywords.toRegStr() + \"\\\\s+\\\\w+\\\\s*([\\\\s\\\\S]*?)\\\\s*\\\\{[\\\\s\\\\S]*?\\\\}\\\\s*)\";\n" +
                "    // method (any) { any code }\n" +
                "    private static final Pattern methodPattern = Pattern.compile(methodReg);\n" +
                "\n" +
                "    // class  {  any code  }\n" +
                "    private static final Pattern classPattern = Pattern.compile(\"(\" +\n" +
                "            \"(\" + importReg + \")*\" +\n" +
                "            \"\\\\s*class\\\\s+[\\\\w.]+\\\\s*\\\\{\" + \n" +
                "                methodReg + \n" +
                "            \"\\\\}\\\\s*\" +\n" +
                "            \")\");\n" +
                "\n" +
                "    public static Map<String, ClassORDDefine> parse(String classORDText) {\n" +
                "        // 通过正则获取单个类的覆写部分\n" +
                "        Matcher matcher = classPattern.matcher(classORDText);\n" +
                "        if (!matcher.matches()) {\n" +
                "            throw new RuntimeException(\"can not load invalid ord file\");\n" +
                "        }\n" +
                "        Set<String> importSet = new HashSet<>();\n" +
                "        String ordTestAfterImport = parseImport(classORDText, importSet);\n" +
                "        System.out.println(importSet);\n" +
                "        Map<String, ClassORDDefine> overrideClassMap = new HashMap<>();\n" +
                "        parse(ordTestAfterImport, overrideClassMap);\n" +
                "        dealImport(overrideClassMap, importSet);\n" +
                "        return overrideClassMap;\n" +
                "    }\n" +
                "\n" +
                "    private static void dealImport(Map<String, ClassORDDefine> overrideClassMap, Set<String> importSet) {\n" +
                "        if (importSet.size() < 1) {\n" +
                "            return;\n" +
                "        }\n" +
                "        overrideClassMap.forEach((s, classORDDefine) -> {\n" +
                "            classORDDefine.getMethodORDMap().forEach((s1, methodORDDefine) -> {\n" +
                "                Map<Keywords, String> srcMap = methodORDDefine.getSrcMap();\n" +
                "                Map<Keywords, String> importedMap = new HashMap<>(srcMap.size());\n" +
                "                srcMap.forEach((s2, src) -> {\n" +
                "                    String result = src;\n" +
                "                    if (!StringUtils.isEmpty(result) && !isEmptyBlock(result)) {\n" +
                "                        for (String importClass : importSet) {\n" +
                "                            // TODO @chenxp 2024/8/2 防重复替换\n" +
                "                            // TODO @chenxp 2024/8/2 import xxx.xxx.* 的处理\n" +
                "                            // TODO @chenxp 2024/8/2 某些不能替换的qingk\n" +
                "                            String classShortName = StringUtils.rightmost(importClass, \".\");\n" +
                "                            result = StringUtils.replaceNoRepeat(result, classShortName, importClass);\n" +
                "                        }\n" +
                "                    }\n" +
                "                    importedMap.put(s2, result);\n" +
                "                });\n" +
                "                methodORDDefine.setSrcMap(importedMap);\n" +
                "            });\n" +
                "        });\n" +
                "    }\n" +
                "\n" +
                "    private static String parseImport(String classORDText, Set<String> importSet) {\n" +
                "        Matcher matcher = importPattern.matcher(classORDText);\n" +
                "        if (!matcher.matches()) {\n" +
                "            return classORDText;\n" +
                "        }\n" +
                "        String anImport = classORDText.substring(classORDText.indexOf(\"import\") + 6, classORDText.indexOf(\";\")).trim();\n" +
                "        importSet.add(anImport);\n" +
                "        return parseImport(classORDText.substring(classORDText.indexOf(\";\") + 1).trim(), importSet);\n" +
                "    }\n" +
                "\n" +
                "    private static void parse(String classORDText, Map<String, ClassORDDefine> overrideClassMap) {\n" +
                "        Matcher matcher = classPattern.matcher(classORDText);\n" +
                "        if (!matcher.matches()) {\n" +
                "            return;\n" +
                "        }\n" +
                "        ClassORDDefine classOrdDefine = new ClassORDDefine();\n" +
                "        Map<String, MethodORDDefine> ordMethod = new HashMap<>();\n" +
                "        classOrdDefine.setMethodORDMap(ordMethod);\n" +
                "        int start = classORDText.indexOf(\"{\");\n" +
                "        String fullName = classORDText.substring(classORDText.indexOf(\"class\") + 5, start).trim();\n" +
                "        classOrdDefine.setName(fullName);\n" +
                "        String codeText = classORDText.substring(start + 1).trim();\n" +
                "        // 截取类名后，加载代码体部分\n" +
                "        String remainText = loadMethodORD(codeText, ordMethod);\n" +
                "        int end = remainText.indexOf(\"}\");\n" +
                "        if (end >= 0) {\n" +
                "            overrideClassMap.put(fullName, classOrdDefine);\n" +
                "            parse(remainText.substring(end + 1), overrideClassMap);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private static String loadMethodORD(String methodORDText, Map<String, MethodORDDefine> ordMethod) {\n" +
                "        if (!methodPattern.matcher(methodORDText).matches()) {\n" +
                "            return methodORDText;\n" +
                "        }\n" +
                "        Keywords keyword = getKeyword(methodORDText);\n" +
                "        String methodName = getMethodName(methodORDText, keyword);\n" +
                "        MethodORDDefine methodORDDefine = ordMethod.get(methodName);\n" +
                "        if (methodORDDefine == null) {\n" +
                "            methodORDDefine = new MethodORDDefine(methodName);\n" +
                "            ordMethod.put(methodName, methodORDDefine);\n" +
                "        }\n" +
                "        // 保存对应关键字方法参数\n" +
                "        List<String> methodParam = getMethodParam(methodORDText);\n" +
                "        if (methodParam.size() > 0) {\n" +
                "            methodORDDefine.getParamMap().put(keyword, methodParam);\n" +
                "        }\n" +
                "        \n" +
                "        char[] chars = methodORDText.toCharArray();\n" +
                "        // 需要匹配的反括号}次数，方法体中可能存在大括号，故次数可能增加\n" +
                "        int findCount = 1;\n" +
                "        int end = -1;\n" +
                "        int start = methodORDText.indexOf(\"{\");\n" +
                "        for (int i = start + 1; i < chars.length; i++) {\n" +
                "            if (chars[i] == '{') {\n" +
                "                findCount++;\n" +
                "            } else if (chars[i] == '}') {\n" +
                "                findCount--;\n" +
                "            }\n" +
                "            if (findCount == 0) {\n" +
                "                // 匹配了最终的反括号}表示方法结束\n" +
                "                end = i;\n" +
                "                break;\n" +
                "            }\n" +
                "        }\n" +
                "        if (end != -1) {\n" +
                "            methodORDDefine.getSrcMap().put(keyword, \"{\" + methodORDText.substring(start + 1, end).trim() + \"}\");\n" +
                "//            ordMethod.put(methodName, \"{\" + methodORDText.substring(start + 1, end).trim() + \"}\");\n" +
                "            if (methodORDText.length() > end + 1) {\n" +
                "                // 递归截取加载方法覆写\n" +
                "                return loadMethodORD(methodORDText.substring(end + 1).trim(), ordMethod);\n" +
                "            }\n" +
                "        }\n" +
                "        return \"\";\n" +
                "    }\n" +
                "\n" +
                "    private static Keywords getKeyword(String methodORDText) {\n" +
                "        for (Keywords value : Keywords.values()) {\n" +
                "            String keyword = value.getKeyword();\n" +
                "            if (methodORDText.startsWith(keyword + \" \")) {\n" +
                "                return value;\n" +
                "            }\n" +
                "        }\n" +
                "        throw new RuntimeException(\"can not find keyword \");\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 获取关键字后，括号前的部分为方法名 \n" +
                "     * 如：keyword methodName (param1,param2) {}\n" +
                "     * 返回：methodName\n" +
                "     * \n" +
                "     *\n" +
                "     */\n" +
                "    private static String getMethodName(String methodORDText, Keywords keywords) {\n" +
                "        String keyword = keywords.getKeyword();\n" +
                "        return methodORDText.substring(methodORDText.indexOf(keyword) + keyword.length(), methodORDText.indexOf(\"(\")).trim();\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 获取括号直接的部分为参数 \n" +
                "     * 如：keyword methodName (param1,param2) {}\n" +
                "     * 返回：[param1,param2]\n" +
                "     */\n" +
                "    private static List<String> getMethodParam(String methodORDText) {\n" +
                "        String methodDesc = methodORDText.substring(0, methodORDText.indexOf(\"{\"));\n" +
                "        String paramStr = methodDesc.substring(methodDesc.indexOf(\"(\") + 1, methodDesc.lastIndexOf(\")\"));\n" +
                "        if (paramStr.length() > 0) {\n" +
                "            String[] split = paramStr.split(\"\\\\s*,\\\\s*\");\n" +
                "            return Arrays.asList(split);\n" +
                "        }\n" +
                "        return Collections.emptyList();\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 是否空代码块 {}\n" +
                "     * */\n" +
                "    public static boolean isEmptyBlock(String src) {\n" +
                "        return src.substring(src.indexOf(\"{\") + 1, src.lastIndexOf(\"}\")).trim().length() == 0;\n" +
                "    }\n" +
                "    \n" +
                "}\n";
        Lexer lexer = new Lexer(s);
        lexer.execute();
//        System.out.println(s);
        System.out.println("------------------------");
        System.out.println(lexer.showCode());
        System.out.println("------------------------");
//        System.out.println(lexer.toString());
    }

}