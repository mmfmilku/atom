package org.mmfmilku.atom.agent.util;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.CompilerUtil;
import org.mmfmilku.atom.agent.compiler.parser.aa.TestFile1;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.util.FileUtils;

import java.io.IOException;

import static org.junit.Assert.*;

public class TestUtil {

    public static String getJavaText(Class clazz) {
        String s = null;
        try {
            s = FileUtils.readText(System.getProperty("user.dir") +
                    "\\src\\test\\java\\" +
                    clazz.getName().replaceAll("\\.", "\\\\") +
                    ".java");
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return s;
    }

    public static void compareParsedText(Class clazz) {
        String javaText = TestUtil.getJavaText(clazz);
        JavaAST javaAST = CompilerUtil.parseAST(javaText);
        String sourceCode = javaAST.getSourceCode();
        System.out.println(sourceCode);
        assertEquals(javaText, sourceCode);
    }

    @Test
    public void test() throws IOException {
        String javaText = getJavaText(this.getClass());
        String s = FileUtils.readText(System.getProperty("user.dir") + "\\src\\test\\java\\org\\mmfmilku\\atom\\agent\\util\\TestUtil.java");
        assertEquals(javaText, s);
        System.out.println(javaText);
    }

}
