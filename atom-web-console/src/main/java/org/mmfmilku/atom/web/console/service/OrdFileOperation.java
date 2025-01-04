package org.mmfmilku.atom.web.console.service;

import org.mmfmilku.atom.web.console.domain.AgentConfig;
import org.mmfmilku.atom.web.console.domain.OrdFile;
import org.mmfmilku.atom.web.console.interfaces.IOrdFileOperation;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * OrdFileOperation
 *
 * @author mmfmilku
 * @date 2024/7/31:13:17
 */
@Service
public class OrdFileOperation implements IOrdFileOperation {
    
    @Override
    public List<String> listFiles(AgentConfig config) {
        return Arrays.asList(Objects.requireNonNull(new File(config.getOrdDir()).list()));
    }
    
    private File getFile(AgentConfig config, OrdFile ordFile) {
        return new File(config.getOrdDir(), ordFile.getFileName());
    }

    @Override
    public OrdFile getOrd(AgentConfig config, String ordName) {
        OrdFile ordFile = new OrdFile();
        ordFile.setFileName(ordName);
        ordFile.setOrdId(config.getId());
        File file = new File(config.getOrdDir(), ordFile.getFileName());
        if (!file.exists()) {
//            ordFile.setText("");
            return ordFile;
        }
        // TODO @chenxp 2024/7/31 添加util
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("getText fail:" + ordFile.getOrdId() + "," + file.getAbsolutePath());
        }
        ordFile.setText(sb.toString());
        return ordFile;
    }

    @Override
    public void setText(AgentConfig config, OrdFile ordFile) {

        String ordFileText = ordFile.getText();
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(getFile(config, ordFile)))) {
            out.write(ordFileText.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("setText fail:" + ordFile.getOrdId());
        }
    }

    @Override
    public void delete(AgentConfig config, OrdFile ordFile) {
        File file = getFile(config, ordFile);
        if (file.exists() && !file.delete()) {
            throw new RuntimeException("delete ord dir fail:" + ordFile);
        }
    }
}
