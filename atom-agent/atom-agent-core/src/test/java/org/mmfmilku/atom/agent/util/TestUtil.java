package org.mmfmilku.atom.agent.util;

import org.junit.Test;

import java.io.File;
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

    @Test
    public void test() throws IOException {
        String javaText = getJavaText(this.getClass());
        String s = FileUtils.readText(System.getProperty("user.dir") + "\\src\\test\\java\\org\\mmfmilku\\atom\\agent\\util\\TestUtil.java");
        assertEquals(javaText, s);
        System.out.println(javaText);
    }

}
