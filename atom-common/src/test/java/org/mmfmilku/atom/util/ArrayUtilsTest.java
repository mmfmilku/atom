package org.mmfmilku.atom.util;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ArrayUtilsTest {

    @Test
    public void assemblyTest() {
        String s = "abc-def";
        byte[] bytes = s.getBytes();
        byte[] b1 = Arrays.copyOfRange(bytes, 0, 3);
        byte[] b2 = Arrays.copyOfRange(bytes, 3, 4);
        byte[] b3 = Arrays.copyOfRange(bytes, 4, 7);
        byte[] assembly = ArrayUtils.assembly(b1, b2, b3);
        assertArrayEquals(bytes, assembly);
        assertEquals(s, new String(assembly));
    }

}