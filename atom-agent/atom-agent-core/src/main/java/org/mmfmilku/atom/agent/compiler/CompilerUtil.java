package org.mmfmilku.atom.agent.compiler;

import org.mmfmilku.atom.agent.AtomAgentStarter;
import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.parser.ParserDispatcher;
import org.mmfmilku.atom.agent.compiler.parser.syntax.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Package;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.AccessPrivilege;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.Modifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.VarDefineStatement;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.Class;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CompilerUtil {

    /**
     * 解析源码为java语法树
     * */
    public static JavaAST parseAST(String code) {
        Lexer lexer = new Lexer(code);
        lexer.execute();
        ParserDispatcher parser = new ParserDispatcher(lexer);
        return parser.execute();
    }

    /**
     * 解析表达式
     * */
    public static Expression parseExpression(String text) {
        Lexer lexer = new Lexer(text);
        lexer.execute();
        return new ParserDispatcher(lexer).getExpression();
    }

    public static JavaAST newEmptyJavaAST(Class<?> nativeClass) {
        JavaAST javaAST = new JavaAST();
        javaAST.setImports(Collections.emptyList());

        Package aPackage = new Package();
        aPackage.setValue(nativeClass.getPackage().getName());
        javaAST.setPackageNode(aPackage);

        List<Method> methods = new ArrayList<>();
        for (java.lang.reflect.Method nativeClassMethod : nativeClass.getMethods()) {
            if (nativeClassMethod.getDeclaringClass() != nativeClass) {
                // 跳过父类方法
                continue;
            }
            Method method = new Method();
            method.setMethodName(nativeClassMethod.getName());
            Modifier modifier = getModifier(nativeClassMethod.getModifiers());
            method.setModifier(modifier);
            // TODO 注解先写死
            method.setAnnotations(Collections.emptyList());
            // 方法参数
            List<VarDefineStatement> methodParams = new ArrayList<>();
            for (Parameter parameter : nativeClassMethod.getParameters()) {
                Class<?> parameterType = parameter.getType();
                String typeName = parameterType.getName();
                if (parameterType.isArray()) {
                    typeName = parameterType.getComponentType().getName() + "[]";
                }
                VarDefineStatement varDefineStatement = new VarDefineStatement(
                        typeName, parameter.getName());
                methodParams.add(varDefineStatement);
            }
            method.setMethodParams(methodParams);
            // TODO 返回类型先写死
            method.setReturnType(Object.class.getSimpleName());

            CodeBlock codeBlock = new CodeBlock();
            codeBlock.setStatements(Collections.emptyList());
            method.setCodeBlock(codeBlock);

            methods.add(method);
        }

        org.mmfmilku.atom.agent.compiler.parser.syntax.Class aClass = new org.mmfmilku.atom.agent.compiler.parser.syntax.Class(nativeClass.getSimpleName());
        aClass.setClassFullName(nativeClass.getName());
        aClass.setMethods(methods);
        Modifier modifier = getModifier(nativeClass.getModifiers());
        aClass.setModifier(modifier);
        aClass.setAnnotations(Collections.emptyList());
        aClass.setMembers(Collections.emptyList());
        aClass.setConstructors(Collections.emptyList());

        javaAST.setClassList(Collections.singletonList(aClass));

        javaAST.buildLinkedNode();

        return javaAST;
    }

    private static Modifier getModifier(int reflectModifiers) {
        AccessPrivilege accessPrivilege =
                java.lang.reflect.Modifier.isPublic(reflectModifiers) ? AccessPrivilege.PUBLIC :
                java.lang.reflect.Modifier.isProtected(reflectModifiers) ? AccessPrivilege.PROTECTED :
                java.lang.reflect.Modifier.isPrivate(reflectModifiers) ? AccessPrivilege.PRIVATE :
                AccessPrivilege.DEFAULT
                ;
        Modifier modifier = new Modifier();
        modifier.setAccessPrivilege(accessPrivilege);
        return modifier;
    }

    /**
     * TODO 临时实现
     * */
    public static byte[] compile(String className, String sourceCode) {

        // 1. 创建一个内存中的源代码对象
        SimpleJavaFileObject srcObject = new SimpleJavaFileObject(
//                URI.create("string:///" + CodeUtils.toJavaFilePath(className)),
                URI.create("string:///" + className.replace(".", "/")
                        + JavaFileObject.Kind.SOURCE.extension),
                JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                // 返回要编译的Java源代码字符串
                return sourceCode;
            }
        };

        // 2. 创建一个用于接收字节码的内存文件对象
        SimpleJavaFileObject clsObject = new SimpleJavaFileObject(
//                URI.create("byte:///" + CodeUtils.toClassFilePath(className)),
                URI.create("byte:///" + className.replace(".", "/")
                        + JavaFileObject.Kind.CLASS.extension),
                JavaFileObject.Kind.CLASS) {
            private ByteArrayOutputStream baos = new ByteArrayOutputStream();

            @Override
            public OutputStream openOutputStream() {
                return baos;
            }

            // 这个方法用于之后获取编译好的字节码数组
            public byte[] getBytes() {
                return baos.toByteArray();
            }
        };

        ForwardingJavaFileManager<StandardJavaFileManager> fileManager =
                new ForwardingJavaFileManager<StandardJavaFileManager>(
                        ToolProvider.getSystemJavaCompiler()
                                .getStandardFileManager(null, null, null)) {
                    @Override
                    public JavaFileObject getJavaFileForOutput(Location location,
                                                               String className,
                                                               JavaFileObject.Kind kind,
                                                               FileObject sibling) {
                        // 当编译器要输出.class文件时，我们返回之前创建的clsObject
                        return clsObject;
                    }

//                    @Override
//                    public ClassLoader getClassLoader(Location location) {
//                        // 此处返回的类加载器，编译后会被关闭
//                        return new ClassLoader(CompilerUtil.class.getClassLoader()) {
//
//                        };
////                        return new URLClassLoader("", CompilerUtil.class.getClassLoader());
//                    }
                };

        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();

        // TODO 临时写法
        String classPath = buildAllClassPath();

        List<String> options = new ArrayList<>();
        if (!classPath.isEmpty()) {
            options.add("-classpath");
            options.add(classPath);
        }
        options.add("-Xlint:unchecked");
        options.add("-g");

//        System.out.println("编译参数：\n" + options);

        Collection<JavaFileObject> compilationUnits = new ArrayList<>();
        compilationUnits.add(srcObject);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        JavaCompiler.CompilationTask task = compiler.getTask(
                null,           // 输出流 (null 表示 System.err)
                fileManager,    // 文件管理器
                collector,           // 诊断监听器 (null 表示默认)
                options,           // 编译选项
                null,           // 需要编译的类名
                compilationUnits // 源代码文件对象列表
        );

        // 5. 执行编译
        List<Diagnostic<? extends JavaFileObject>> errors = new ArrayList<>();
        List<Diagnostic<? extends JavaFileObject>> warnings = new ArrayList<>();

        boolean success = task.call();
        if (!success || collector.getDiagnostics().size() > 0) {

            for (Diagnostic<? extends JavaFileObject> diagnostic : collector.getDiagnostics()) {
                switch (diagnostic.getKind()) {
                    case NOTE:
                    case MANDATORY_WARNING:
                    case WARNING:
                        warnings.add(diagnostic);
                        break;
                    case OTHER:
                    case ERROR:
                    default:
                        errors.add(diagnostic);
                        break;
                }

            }

            if (!errors.isEmpty()) {
                throw new RuntimeException("Compilation Error" +
                        errors.stream().map(Object::toString).collect(Collectors.toList()));
            }
        }

        if (success) {
            System.out.println("编译成功！");
            // 现在可以通过 clsObject.getBytes() 获取编译后的字节码
            try {
                byte[] bytecode = ((ByteArrayOutputStream) clsObject.openOutputStream()).toByteArray();
                return bytecode;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // 接下来可以使用自定义类加载器加载这个byte数组
        } else {
            System.out.println("编译失败！");
        }

        return null;
    }

    private static String buildAllClassPath() {
        // TODO 目前不支持自定义类加载器加载的依赖，如springboot的jar中jar

        // 追加 atom-agent-core 依赖
        String agentCorePath = AtomAgentStarter.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        // 尝试通过java.class.path系统属性获取
        String classPath = System.getProperty("java.class.path");
        if (classPath != null) {
            return classPath + File.pathSeparator + agentCorePath;
        }
        return agentCorePath;
    }

}
