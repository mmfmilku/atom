import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.CompilerUtil;
import org.mmfmilku.atom.agent.compiler.SupportSyntaxSample;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.config.AgentProperties;
import org.mmfmilku.atom.agent.config.ClassORDDefine;
import org.mmfmilku.atom.agent.config.Keywords;
import org.mmfmilku.atom.agent.config.MethodORDDefine;
import org.mmfmilku.atom.agent.config.OverrideBodyHolder;
import org.mmfmilku.atom.agent.util.ByteCodeUtils;
import org.mmfmilku.atom.agent.util.OrdUtils;
import org.mmfmilku.atom.agent.util.TestUtil;
import org.mmfmilku.atom.exception.BizException;
import org.mmfmilku.atom.util.FileUtils;
import org.mmfmilku.atom.util.ReflectUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Test1 {


    @Test
    public void testProperties() {

        AgentProperties.loadProperties("base-path=E:/project/atom/atom-agent/src/main/resources/config;test=come;toStringMethod=d.c.t");
        System.out.println(AgentProperties.getInstance());
        
    }

    @Test
    public void testReadFileToString() {

        String s = null;
        try {
            s = FileUtils.readText(System.getProperty("user.dir") + "/src/main/resources/config/agent.conf");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(s);

    }

    @Test
    public void testLoadOr() {

        OverrideBodyHolder.load(System.getProperty("user.dir") + "/src/main/resources/config/");
        Map<String, ClassORDDefine> a = OverrideBodyHolder.getORClassMap();
        
        System.out.println(a);

    }

    @Test
    public void testParseOrdFile() {
        Map<String, ClassORDDefine> defineMap = OverrideBodyHolder.parseOverrideFile(System.getProperty("user.dir") + "/src/main/resources/config/test1.ord");
        System.out.println(defineMap);
        Class[] classes = defineMap.keySet().stream().map(ReflectUtils::forName).toArray(Class[]::new);
        System.out.println(Arrays.toString(classes));
    }

    @Test
    public void testByteCode() {
        String testClassFullName = SupportSyntaxSample.class.getName();
        String javaText = TestUtil.getJavaText(SupportSyntaxSample.class);
        JavaAST javaAST = CompilerUtil.parseAST(javaText);

        ByteCodeUtils.toJavassistCode(javaAST);
        System.out.println(javaAST.getSourceCode());
        Map<String, ClassORDDefine> ordDefineMap = OrdUtils.astToOrd(javaAST);

        ClassORDDefine classOrdDefine = ordDefineMap.get(testClassFullName);
        try {
            ClassPool classPool = ClassPool.getDefault();
            classPool.importPackage("java.util.Map");
            CtClass ctClass = classPool.get(testClassFullName);
            Map<String, MethodORDDefine> methodORDMap = classOrdDefine.getMethodORDMap();
            for (Map.Entry<String, MethodORDDefine> entry : methodORDMap.entrySet()) {
                Map<Keywords, String> srcMap = entry.getValue().getSrcMap();
                if (srcMap.containsKey(Keywords.METHOD)) {
                    CtMethod ctMethod = ctClass.getDeclaredMethod(entry.getKey());
                    System.out.println(entry.getKey() + " 开始---------------");
                    System.out.println(srcMap.get(Keywords.METHOD));
                    ctMethod.setBody(srcMap.get(Keywords.METHOD));
                    System.out.println("成功---------------");
                }
            }
            // 返回字节码，并且detachCtClass对象
            byte[] byteCode = ctClass.toBytecode();
            //detach的意思是将内存中曾经被javassist加载过的对象移除，如果下次有需要在内存中找不到会重新走javassist加载
            ctClass.detach();
        } catch (IOException | CannotCompileException | NotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testJavassist() {
        Map<String, Object> map = new HashMap<>();
        int i1 = 3;
        Object _obj_i1 = i1;
        map.put("i1", _obj_i1);
        int i = (int) map.get("id");
        String testClassFullName = SupportSyntaxSample.class.getName();
        try {
            ClassPool classPool = ClassPool.getDefault();
            classPool.importPackage("java.util.Map");
            CtClass ctClass = classPool.get(testClassFullName);
            CtMethod ctMethod = ctClass.getDeclaredMethod("thisMethodForTestJavassist");
            ctMethod.setBody("{" +
                    "int a = 1;\n" +
                    "Object _obj_a = a;" +
                    "$1.put(\"a\", _obj_a );" +
                    "int i1 = 3;\n" +
                    "Object _obj_i1 = i1;\n" +
                    "$1.put(\"i1\", _obj_i1);" +
                    "}");
            // 返回字节码，并且detachCtClass对象
            byte[] byteCode = ctClass.toBytecode();
            //detach的意思是将内存中曾经被javassist加载过的对象移除，如果下次有需要在内存中找不到会重新走javassist加载
            ctClass.detach();
        } catch (IOException | CannotCompileException | NotFoundException e) {
            e.printStackTrace();
        }
    }
    
}
