package org.mmfmilku.atom.agent.compiler.parser;

import org.mmfmilku.atom.util.JavaUtil;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class QuickCode {

    private String test() {
        int[] a;
        a = new int[3];
        a[0] += 1;
        return "";
    }

}
