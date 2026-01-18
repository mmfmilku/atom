package org.mmfmilku.atom.agent.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 屏幕展示日志
 **/
public class ScreenLogger {

    private static final List<String> logs = new ArrayList<>();

    public static void print(String str) {
        logs.add(str);
    }

    public static void warn(String msg) {
        logs.add(msg);
    }

    public static void error(String msg) {
        logs.add(msg);
    }

    public static void error(Throwable throwable, String msg) {
        logs.add(msg);
        logs.add(Arrays.stream(throwable.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"))
        );
    }

    public static List<String> getAllLogs() {
        return logs;
    }

}
