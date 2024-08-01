/********************************************
 * 文件名称: OrdFileOperation.java
 * 系统名称: 综合理财管理平台6.0
 * 模块名称:
 * 软件版权: 恒生电子股份有限公司
 * 功能说明:
 * 系统版本: 6.0.0.1
 * 开发人员: chenxp
 * 开发时间: 2024/7/31
 * 审核人员:
 * 相关文档:
 * 修改记录:   修改日期    修改人员    修改单号       版本号                   修改说明
 * V6.0.0.1  20240731-01  chenxp   TXXXXXXXXXXXX    IFMS6.0VXXXXXXXXXXXXX   新增 
 *********************************************/
package org.mmfmilku.atom.web.console.service;

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
 * @author chenxp
 * @date 2024/7/31:13:17
 */
@Service
public class OrdFileOperation implements IOrdFileOperation {
    
    public static final String ORD_DIR = System.getProperty("user.dir") + File.separator + "ord";

    private static MessageDigest sha1;

    static {
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getOrdId(String appName) {
        sha1.update(appName.getBytes(StandardCharsets.UTF_8));
        byte[] digest = sha1.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @Override
    public String getDir(String ordId) {
        File file = new File(ORD_DIR, ordId);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            if (!mkdirs) {
                throw new RuntimeException("create ord dir fail:" + ordId);
            }
        }
        return file.getAbsolutePath();
    }

    @Override
    public List<String> listDirs() {
        return Arrays.asList(Objects.requireNonNull(new File(ORD_DIR).list()));
    }

    @Override
    public List<String> listFiles(String ordId) {
        String dir = getDir(ordId);
        return Arrays.asList(Objects.requireNonNull(new File(dir).list()));
    }
    
    private File getFile(OrdFile ordFile) {
        String dir = getDir(ordFile.getOrdId());
        return new File(dir, ordFile.getFileName());
    }

    @Override
    public String getText(OrdFile ordFile) {
        String dir = getDir(ordFile.getOrdId());
        File file = new File(dir, ordFile.getFileName());
        if (!file.exists()) {
            return "";
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
        return sb.toString();
    }

    @Override
    public void setText(OrdFile ordFile) {

        String ordFileText = ordFile.getText();
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(getFile(ordFile)))) {
            out.write(ordFileText.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("setText fail:" + ordFile.getOrdId());
        }
    }

    @Override
    public void delete(OrdFile ordFile) {
        File file = getFile(ordFile);
        if (file.exists() && !file.delete()) {
            throw new RuntimeException("delete ord dir fail:" + ordFile);
        }
    }
}
