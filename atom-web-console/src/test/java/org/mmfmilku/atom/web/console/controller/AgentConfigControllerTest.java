package org.mmfmilku.atom.web.console.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mmfmilku.atom.web.console.domain.AgentConfig;
import org.mmfmilku.atom.web.console.domain.OrdFile;
import org.mmfmilku.atom.web.console.domain.OrdRunInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
public class AgentConfigControllerTest {

    @Autowired
    AgentConfigController configController;

    private String appName = "test";

    @BeforeAll
    public static void beforeAll() {
    }

    @AfterAll
    public static void afterAll() {
        System.out.println("all test finish");
    }

    @Test
    public void getConfig() {
        AgentConfig test = configController.getConfig(appName);
        System.out.println(test);
        assertEquals("a94a8fe5ccb19ba61c4c0873d391e987982fbbd3", test.getId());
    }

    @Test
    public void listOrd() {
        System.out.println(configController.listBaseOrd(appName));
    }

    @Test
    public void testCase() {
        List<OrdRunInfo> listOrd = configController.listBaseOrd(appName);
        for (OrdRunInfo ord : listOrd) {
            configController.deleteOrd(appName, ord.getOrdName());
        }
        // 删除后为0
        assertEquals(0, configController.listBaseOrd(appName).size());

        OrdFile ordFile = new OrdFile();
        ordFile.setFileName("a");
        ordFile.setOrdId(configController.getConfig(appName).getId());
        ordFile.setText("this is test ord text1");
        configController.writeOrd(appName, ordFile);

        ordFile.setFileName("b");
        ordFile.setText("this is test ord text2");
        configController.writeOrd(appName, ordFile);

        // 测试添加，查询
        assertEquals(2, configController.listBaseOrd(appName).size());

        // 测试读取
        OrdFile readOrd = configController.readOrd(appName, "a.ord", "BASE_ORD");
        assertEquals("this is test ord text1", readOrd.getText().trim());
        readOrd = configController.readOrd(appName, "b.ord", "BASE_ORD");
        assertEquals("this is test ord text2", readOrd.getText().trim());

        listOrd = configController.listBaseOrd(appName);
        for (OrdRunInfo ord : listOrd) {
            configController.deleteOrd(appName, ord.getOrdName());
        }
        // 测试删除
        assertEquals(0, configController.listBaseOrd(appName).size());
    }

}