/********************************************
 * 文件名称: AgentConfigService.java
 * 系统名称: 综合理财管理平台6.0
 * 模块名称:
 * 软件版权: 恒生电子股份有限公司
 * 功能说明:
 * 系统版本: 6.0.0.1
 * 开发人员: chenxp
 * 开发时间: 2024/7/30
 * 审核人员:
 * 相关文档:
 * 修改记录:   修改日期    修改人员    修改单号       版本号                   修改说明
 * V6.0.0.1  20240730-01  chenxp   TXXXXXXXXXXXX    IFMS6.0VXXXXXXXXXXXXX   新增 
 *********************************************/
package org.mmfmilku.atom.web.console.service;

import org.mmfmilku.atom.web.console.domain.AgentConfig;
import org.mmfmilku.atom.web.console.domain.OrdFile;
import org.mmfmilku.atom.web.console.interfaces.IOrdConfigService;
import org.mmfmilku.atom.web.console.interfaces.IOrdFileOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * AgentConfigService
 *
 * @author chenxp
 * @date 2024/7/30:19:46
 */
@Service
public class OrdConfigService implements IOrdConfigService {

    private static ConcurrentMap<String, AgentConfig> configMap = new ConcurrentHashMap<>();
    
    public static final String ORD_SUFFIX = ".ord";
    
    @Autowired
    private IOrdFileOperation ordFileOperation;

    @Override
    public Collection<AgentConfig> list() {
        return configMap.values();
    }

    @Override
    public AgentConfig getConfig(String appName) {
        if (configMap.containsKey(appName)) {
            return configMap.get(appName);
        }
        String ordId = ordFileOperation.getOrdId(appName);
        AgentConfig agentConfig = new AgentConfig();
        agentConfig.setOrdId(ordId);
        configMap.put(appName, agentConfig);
        return agentConfig;
    }

    @Override
    public List<String> listOrd(String appName) {
        AgentConfig config = getConfig(appName);
        return ordFileOperation.listFiles(config.getOrdId());
    }

    @Override
    public void deleteOrd(String appName, String ordFileName) {
        AgentConfig config = getConfig(appName);
        OrdFile ordFile = new OrdFile();
        ordFile.setFileName(ordFileName);
        ordFile.setOrdId(config.getOrdId());
        ordFileOperation.delete(ordFile);
    }

    @Override
    public OrdFile readOrd(String appName, String ordFileName) {
        AgentConfig config = getConfig(appName);
        OrdFile ordFile = new OrdFile();
        ordFile.setFileName(ordFileName);
        ordFile.setOrdId(config.getOrdId());
        String text = ordFileOperation.getText(ordFile);
        ordFile.setText(text);
        return ordFile;
    }

    @Override
    public void writeOrd(String appName, OrdFile ordFile) {
        AgentConfig config = getConfig(appName);
        ordFile.setOrdId(config.getOrdId());
        ordFile.setFileName(ordFileNameFormat(ordFile.getFileName()));
        ordFileOperation.setText(ordFile);
    }
    
    private String ordFileNameFormat(String ordFileName) {
        if (ordFileName.endsWith(ORD_SUFFIX)) {
            return ordFileName;
        }
        if (ordFileName.endsWith(".")) {
            return ordFileName + ORD_SUFFIX.substring(1);
        }
        return ordFileName + ORD_SUFFIX;
    }

}
