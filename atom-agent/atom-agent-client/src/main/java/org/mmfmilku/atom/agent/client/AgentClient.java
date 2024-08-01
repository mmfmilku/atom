package org.mmfmilku.atom.agent.client;

import com.sun.tools.attach.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * org.mmfmilku.atom.agent.client.AgentClient
 *
 * @author chenxp
 * @date 2024/6/6:19:23
 */
public class AgentClient {

    public static void main(String[] args) throws Exception {
        doAttach(args);
    }

    private static void doAttach(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        // -Xbootclasspath/a:D:\Program Files\Java\jdk1.8.0_221\lib\tools.jar
        //获取当前系统中所有 运行中的 虚拟机
        System.out.println("running org.mmfmilku.atom.agent.client.AgentClient start ");
        System.out.println("args=" + args.length + ";" + Arrays.toString(args));


        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        System.out.println(list.stream().map(VirtualMachineDescriptor::displayName).collect(Collectors.joining("\n")));
        for (VirtualMachineDescriptor vmd : list) {
            //如果虚拟机的名称为 xxx 则 该虚拟机为目标虚拟机，获取该虚拟机的 pid
            //然后加载 agent.jar 发送给该虚拟机
            if (args.length > 0 && args[0].equals(vmd.displayName())) {
                System.out.println("--------------start attach " + vmd.displayName() + "---------");
                loadAgent(vmd.id());
            }
            if (vmd.displayName().startsWith("lcpt-web-manager-dxfund-bootstrap-4.0.0-SNAPSHOT.jar")) {
                System.out.println("--------------start attach " + vmd.displayName() + "---------");
                loadAgent(vmd.id());
            }
        }


//        List<VirtualMachineDescriptor> list = VirtualMachine.list();
//        System.out.println(list);
//        for (VirtualMachineDescriptor vmd : list) {
//            //如果虚拟机的名称为 xxx 则 该虚拟机为目标虚拟机，获取该虚拟机的 pid
//            //然后加载 agent.jar 发送给该虚拟机
//            System.out.println(vmd.displayName());
//            if (vmd.displayName().endsWith("com.hundsun.lcpt.dxfund.pub.online.ServerStarter")) {
//                System.out.println("--------------start attach---------");
//                VirtualMachine virtualMachine = VirtualMachine.attach(vmd.id());
//                virtualMachine.loadAgent("E:\\project\\atom\\atom-agent\\target\\atom-agent-jar-with-dependencies.jar",
//                        "base-path=E:/project/atom/atom-agent/src/main/resources/config/;to-string-method=com.alibaba.fastjson.JSON.toJSONString");
//                virtualMachine.detach();
//            }
//        }
    }

    public static List<VirtualMachineDescriptor> listVM() {
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        return list;
    }
    
    public static void loadAgent(String id) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        loadAgent(id, 
                "base-path=E:/project/atom/atom-agent/src/main/resources/config/;to-string-method=com.alibaba.fastjson.JSON.toJSONString");
    }

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

}
