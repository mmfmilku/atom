package org.mmfmilku.atom.agent.client;

import com.sun.tools.attach.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * org.mmfmilku.atom.agent.client.AgentClient
 *
 * @author mmfmilku
 * @date 2024/6/6:19:23
 */
public class AgentClient {

    public static List<VirtualMachineDescriptor> listVM() {
        return VirtualMachine.list();
    }
    
    public static List<Map<String, String>> listVMMap() {
        return listVM().stream().map(vm -> {
            Map<String, String> map = new HashMap<>();
            map.put("vmId", vm.id());
            map.put("displayName", vm.displayName());
            return map;
        }).collect(Collectors.toList());
    }

    @Deprecated
    public static void loadAgent(String id, String args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        String userDir = System.getProperty("user.dir");
        System.out.println("userDir=" + userDir);
        File agentJar = new File(userDir, "atom-agent-jar-with-dependencies.jar");
        if (!agentJar.exists()) {
            System.out.println(agentJar.getAbsolutePath() + " not exists");
            agentJar = new File(userDir, "atom-agent/target/atom-agent-jar-with-dependencies.jar");
        }
        if (!agentJar.exists()) {
            System.out.println(agentJar.getAbsolutePath() + " not exists");
            throw new RuntimeException("can not find agentJar");
        }

        VirtualMachine virtualMachine = VirtualMachine.attach(id);
        virtualMachine.loadAgent(agentJar.getAbsolutePath(), args);
        virtualMachine.detach();
    }

    public static void loadAgent(String id, String agentJarPath, String args) {
        File agentJar = new File(agentJarPath);
        if (!agentJar.exists()) {
            System.out.println(agentJar.getAbsolutePath() + " not exists");
            throw new RuntimeException("can not find agentJar");
        }

        VirtualMachine virtualMachine = null;
        try {
            virtualMachine = VirtualMachine.attach(id);
            virtualMachine.loadAgent(agentJar.getAbsolutePath(), args);
        } catch (AttachNotSupportedException | IOException | AgentLoadException
                | AgentInitializationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (virtualMachine != null) {
                try {
                    virtualMachine.detach();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String FOUND_JAVA_HOME = null;

    public static String findJavaHome() {
        if (FOUND_JAVA_HOME != null) {
            return FOUND_JAVA_HOME;
        }

        String javaHome = System.getProperty("java.home");

        if (true) {
            File toolsJar = new File(javaHome, "lib/tools.jar");
            if (!toolsJar.exists()) {
                toolsJar = new File(javaHome, "../lib/tools.jar");
            }
            if (!toolsJar.exists()) {
                // maybe jre
                toolsJar = new File(javaHome, "../../lib/tools.jar");
            }

            if (toolsJar.exists()) {
                FOUND_JAVA_HOME = javaHome;
                return FOUND_JAVA_HOME;
            }

            if (!toolsJar.exists()) {
                System.out.println("Can not find tools.jar under java.home: " + javaHome);
                String javaHomeEnv = System.getenv("JAVA_HOME");
                if (javaHomeEnv != null && !javaHomeEnv.isEmpty()) {
                    System.out.println("Try to find tools.jar in System Env JAVA_HOME: " + javaHomeEnv);
                    // $JAVA_HOME/lib/tools.jar
                    toolsJar = new File(javaHomeEnv, "lib/tools.jar");
                    if (!toolsJar.exists()) {
                        // maybe jre
                        toolsJar = new File(javaHomeEnv, "../lib/tools.jar");
                    }
                }

                if (toolsJar.exists()) {
                    System.out.println("Found java home from System Env JAVA_HOME: " + javaHomeEnv);
                    FOUND_JAVA_HOME = javaHomeEnv;
                    return FOUND_JAVA_HOME;
                }

                throw new IllegalArgumentException("Can not find tools.jar under java home: " + javaHome
                        + ", please try to start arthas-boot with full path java. Such as /opt/jdk/bin/java -jar arthas-boot.jar");
            }
        } else {
            FOUND_JAVA_HOME = javaHome;
        }
        return FOUND_JAVA_HOME;
    }

    private static File findToolsJar(String javaHome) {
//        if (JavaVersionUtils.isGreaterThanJava8()) {
//            return null;
//        }

        File toolsJar = new File(javaHome, "lib/tools.jar");
        if (!toolsJar.exists()) {
            toolsJar = new File(javaHome, "../lib/tools.jar");
        }
        if (!toolsJar.exists()) {
            // maybe jre
            toolsJar = new File(javaHome, "../../lib/tools.jar");
        }

        if (!toolsJar.exists()) {
            throw new IllegalArgumentException("Can not find tools.jar under java home: " + javaHome);
        }

        System.out.println("Found tools.jar: " + toolsJar.getAbsolutePath());
        return toolsJar;
    }

    static {
        System.out.println(AgentClient.class.getClassLoader());
        System.out.println(Thread.currentThread().getContextClassLoader());
        System.out.println(Thread.currentThread().getContextClassLoader());
        System.out.println(findJavaHome());
        System.out.println(findToolsJar(findJavaHome()));

        File toolsJar = findToolsJar(findJavaHome());
        try {
            ClassLoader classLoader = AgentClient.class.getClassLoader();
            Class<?> loaderClass = Class.forName("java.net.URLClassLoader");
            Method method = loaderClass.getDeclaredMethod("addURL", URL.class);
            URL url = toolsJar.toURI().toURL();
            method.setAccessible(true);
            method.invoke(classLoader, url);
        } catch (MalformedURLException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
