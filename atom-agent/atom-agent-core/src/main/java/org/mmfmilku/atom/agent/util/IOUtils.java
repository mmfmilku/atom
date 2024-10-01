package org.mmfmilku.atom.agent.util;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {

    public static void closeStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
