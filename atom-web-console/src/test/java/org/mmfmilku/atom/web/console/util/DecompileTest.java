package org.mmfmilku.atom.web.console.util;

import org.junit.Test;
import org.mmfmilku.atom.consts.CodeConst;
import org.mmfmilku.atom.util.CodeUtils;

import static org.junit.Assert.*;

public class DecompileTest {

    @Test
    public void decompile() {
        try {
            Decompile.decompile(this.getClass().getClassLoader()
                            .getResource("")
                            .getFile() +
                            CodeUtils.toFileName(this.getClass().getName()) +
                            CodeConst.CLASS_FILE_SUFFIX,
                    System.getProperty("user.dir") + "\\src\\main\\resources\\test");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}