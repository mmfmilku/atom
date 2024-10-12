package org.mmfmilku.atom.agent.client;

import com.sun.tools.attach.*;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class AgentClientTest {

    @Test
    public void testCaseOne() {
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        System.out.println(list.stream().map(VirtualMachineDescriptor::displayName).collect(Collectors.joining("\n")));
        for (VirtualMachineDescriptor vmd : list) {
            //如果虚拟机的名称为 xxx 则 该虚拟机为目标虚拟机，获取该虚拟机的 pid
            //然后加载 agent.jar 发送给该虚拟机
            if (vmd.displayName().startsWith("test-app.jar")) {
                System.out.println("--------------start attach " + vmd.displayName() + "---------");
                try {
                    AgentClient.loadAgent(vmd.id(), "", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}