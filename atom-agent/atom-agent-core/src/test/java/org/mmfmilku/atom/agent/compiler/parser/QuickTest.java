package org.mmfmilku.atom.agent.compiler.parser;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.CompilerUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.util.ByteCodeUtils;
import org.mmfmilku.atom.agent.util.TestUtil;

public class QuickTest {

    @Test
    public void quickTest() {
        String javaText = TestUtil.getJavaText(QuickCode.class);
        JavaAST javaAST = CompilerUtil.parseAST(javaText);
        javaAST.useImport();
        System.out.println(javaAST.getSourceCode());
    }

    @Test
    public void quickText() {
        String code = "/*\n" +
                " * Decompiled with CFR 0.152.\n" +
                " * \n" +
                " * Could not load the following classes:\n" +
                " *  com.google.common.collect.Maps\n" +
                " *  com.hundsun.tbsp.assetpool.basic.common.dict.G20032\n" +
                " *  com.hundsun.tbsp.assetpool.basic.common.dict.G20061\n" +
                " *  com.hundsun.tbsp.assetpool.basic.common.enums.ApErrorEnum\n" +
                " *  com.hundsun.tbsp.assetpool.basic.common.enums.CheckLevelEnum\n" +
                " *  com.hundsun.tbsp.assetpool.basic.component.ApCreditDealCmpt\n" +
                " *  com.hundsun.tbsp.assetpool.basic.component.ApCustInfoCmpt\n" +
                " *  com.hundsun.tbsp.assetpool.basic.component.bo.ApCustInfoBO\n" +
                " *  com.hundsun.tbsp.assetpool.basic.dao.entity.ApQuotaDtl\n" +
                " *  com.hundsun.tbsp.assetpool.basic.dao.entity.ApQuotaDtlExample\n" +
                " *  com.hundsun.tbsp.assetpool.basic.dao.inter.ApQuotaDtlMapper\n" +
                " *  com.hundsun.tbsp.assetpool.financing.common.enums.ApFinancingTrcodeEnum\n" +
                " *  com.hundsun.tbsp.assetpool.financing.component.ApFinancingCmpt\n" +
                " *  com.hundsun.tbsp.assetpool.financing.dao.entity.ApFinancingApplyInfo\n" +
                " *  com.hundsun.tbsp.assetpool.financing.dao.entity.ApFinancingTradeFile\n" +
                " *  com.hundsun.tbsp.assetpool.financing.dao.entity.ApFinancingTradeFileExample\n" +
                " *  com.hundsun.tbsp.assetpool.financing.dao.entity.ApPackLoanApply\n" +
                " *  com.hundsun.tbsp.assetpool.financing.dao.inter.ApFinancingApplyInfoMapper\n" +
                " *  com.hundsun.tbsp.assetpool.financing.dao.inter.ApFinancingTradeFileMapper\n" +
                " *  com.hundsun.tbsp.assetpool.financing.dao.inter.ApPackLoanApplyMapper\n" +
                " *  com.hundsun.tbsp.assetpool.financing.remote.adapter.bo.trade.ApplyAiLcPackLoanReqBO\n" +
                " *  com.hundsun.tbsp.assetpool.financing.remote.adapter.bo.trade.ApplyAiLcPackLoanRespBO\n" +
                " *  com.hundsun.tbsp.assetpool.financing.remote.adapter.bo.trade.BuildLcPackLoanContractReqBO\n" +
                " *  com.hundsun.tbsp.assetpool.financing.remote.adapter.bo.trade.BuildLcPackLoanContractRespBO\n" +
                " *  com.hundsun.tbsp.assetpool.financing.remote.adapter.bo.trade.CancelLcPackLoanContractReqBO\n" +
                " *  com.hundsun.tbsp.assetpool.financing.remote.adapter.bo.trade.UploadFileBO\n" +
                " *  com.hundsun.tbsp.assetpool.financing.remote.adapter.bo.trade.UploadFileReqBO\n" +
                " *  com.hundsun.tbsp.assetpool.financing.remote.adapter.inter.AiFinancingRemoteService\n" +
                " *  com.hundsun.tbsp.assetpool.financing.service.entity.forfaiting.dto.FileDTO\n" +
                " *  com.hundsun.tbsp.assetpool.financing.service.entity.packloan.req.SignPackLoanApplyReq\n" +
                " *  com.hundsun.tbsp.assetpool.financing.service.entity.packloan.resp.SignPackLoanApplyResp\n" +
                " *  com.hundsun.tbsp.commbiz.BizTemplate\n" +
                " *  com.hundsun.tbsp.common.common.base.BaseResponse\n" +
                " *  com.hundsun.tbsp.common.common.base.TbspRequest\n" +
                " *  com.hundsun.tbsp.common.common.dict.G00009\n" +
                " *  com.hundsun.tbsp.common.common.dict.G00259\n" +
                " *  com.hundsun.tbsp.common.common.dict.G00260\n" +
                " *  com.hundsun.tbsp.common.common.dict.G00264\n" +
                " *  com.hundsun.tbsp.common.common.enums.ResponseEnum\n" +
                " *  com.hundsun.tbsp.common.utils.TbspExcepUtil\n" +
                " *  com.hundsun.tbsp.common.utils.TbspNumberUtil\n" +
                " *  com.hundsun.tbsp.common.utils.TbspStringUtil\n" +
                " *  com.hundsun.tbsp.common.utils.TbspUtil\n" +
                " *  org.springframework.beans.factory.annotation.Autowired\n" +
                " *  org.springframework.stereotype.Component\n" +
                " *  org.springframework.util.CollectionUtils\n" +
                " *  org.springframework.util.ObjectUtils\n" +
                " *  org.springframework.util.StringUtils\n" +
                " */\n" +
                "package com.hundsun.tbsp.assetpool.financing.service.impl.packloan;\n" +
                "\n" +
                "import com.google.common.collect.Maps;\n" +
                "import com.hundsun.tbsp.assetpool.basic.common.dict.G20032;\n" +
                "import com.hundsun.tbsp.assetpool.basic.common.dict.G20061;\n" +
                "import com.hundsun.tbsp.assetpool.basic.common.enums.ApErrorEnum;\n" +
                "import com.hundsun.tbsp.assetpool.basic.common.enums.CheckLevelEnum;\n" +
                "import com.hundsun.tbsp.assetpool.basic.component.ApCreditDealCmpt;\n" +
                "import com.hundsun.tbsp.assetpool.basic.component.ApCustInfoCmpt;\n" +
                "import com.hundsun.tbsp.assetpool.basic.component.bo.ApCustInfoBO;\n" +
                "import com.hundsun.tbsp.assetpool.basic.dao.entity.ApQuotaDtl;\n" +
                "import com.hundsun.tbsp.assetpool.basic.dao.entity.ApQuotaDtlExample;\n" +
                "import com.hundsun.tbsp.assetpool.basic.dao.inter.ApQuotaDtlMapper;\n" +
                "import com.hundsun.tbsp.assetpool.financing.common.enums.ApFinancingTrcodeEnum;\n" +
                "import com.hundsun.tbsp.assetpool.financing.component.ApFinancingCmpt;\n" +
                "import com.hundsun.tbsp.assetpool.financing.dao.entity.ApFinancingApplyInfo;\n" +
                "import com.hundsun.tbsp.assetpool.financing.dao.entity.ApFinancingTradeFile;\n" +
                "import com.hundsun.tbsp.assetpool.financing.dao.entity.ApFinancingTradeFileExample;\n" +
                "import com.hundsun.tbsp.assetpool.financing.dao.entity.ApPackLoanApply;\n" +
                "import com.hundsun.tbsp.assetpool.financing.dao.inter.ApFinancingApplyInfoMapper;\n" +
                "import com.hundsun.tbsp.assetpool.financing.dao.inter.ApFinancingTradeFileMapper;\n" +
                "import com.hundsun.tbsp.assetpool.financing.dao.inter.ApPackLoanApplyMapper;\n" +
                "import com.hundsun.tbsp.assetpool.financing.remote.adapter.bo.trade.ApplyAiLcPackLoanReqBO;\n" +
                "import com.hundsun.tbsp.assetpool.financing.remote.adapter.bo.trade.ApplyAiLcPackLoanRespBO;\n" +
                "import com.hundsun.tbsp.assetpool.financing.remote.adapter.bo.trade.BuildLcPackLoanContractReqBO;\n" +
                "import com.hundsun.tbsp.assetpool.financing.remote.adapter.bo.trade.BuildLcPackLoanContractRespBO;\n" +
                "import com.hundsun.tbsp.assetpool.financing.remote.adapter.bo.trade.CancelLcPackLoanContractReqBO;\n" +
                "import com.hundsun.tbsp.assetpool.financing.remote.adapter.bo.trade.UploadFileBO;\n" +
                "import com.hundsun.tbsp.assetpool.financing.remote.adapter.bo.trade.UploadFileReqBO;\n" +
                "import com.hundsun.tbsp.assetpool.financing.remote.adapter.inter.AiFinancingRemoteService;\n" +
                "import com.hundsun.tbsp.assetpool.financing.service.entity.forfaiting.dto.FileDTO;\n" +
                "import com.hundsun.tbsp.assetpool.financing.service.entity.packloan.req.SignPackLoanApplyReq;\n" +
                "import com.hundsun.tbsp.assetpool.financing.service.entity.packloan.resp.SignPackLoanApplyResp;\n" +
                "import com.hundsun.tbsp.commbiz.BizTemplate;\n" +
                "import com.hundsun.tbsp.common.common.base.BaseResponse;\n" +
                "import com.hundsun.tbsp.common.common.base.TbspRequest;\n" +
                "import com.hundsun.tbsp.common.common.dict.G00009;\n" +
                "import com.hundsun.tbsp.common.common.dict.G00259;\n" +
                "import com.hundsun.tbsp.common.common.dict.G00260;\n" +
                "import com.hundsun.tbsp.common.common.dict.G00264;\n" +
                "import com.hundsun.tbsp.common.common.enums.ResponseEnum;\n" +
                "import com.hundsun.tbsp.common.utils.TbspExcepUtil;\n" +
                "import com.hundsun.tbsp.common.utils.TbspNumberUtil;\n" +
                "import com.hundsun.tbsp.common.utils.TbspStringUtil;\n" +
                "import com.hundsun.tbsp.common.utils.TbspUtil;\n" +
                "import java.math.BigDecimal;\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.Arrays;\n" +
                "import java.util.Collection;\n" +
                "import java.util.List;\n" +
                "import java.util.Map;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.stereotype.Component;\n" +
                "import org.springframework.util.CollectionUtils;\n" +
                "import org.springframework.util.ObjectUtils;\n" +
                "import org.springframework.util.StringUtils;\n" +
                "\n" +
                "@Component\n" +
                "public class SignPackLoanApplyImpl\n" +
                "implements BizTemplate<SignPackLoanApplyReq, SignPackLoanApplyResp> {\n" +
                "    private static final String AP_DOM_FORFATING_APPLY = \"apPackLoanApply\";\n" +
                "    @Autowired\n" +
                "    private ApPackLoanApplyMapper apPackLoanApplyMapper;\n" +
                "    @Autowired\n" +
                "    private ApCustInfoCmpt apCustInfoCmpt;\n" +
                "    @Autowired\n" +
                "    private AiFinancingRemoteService aiFinancingRemoteService;\n" +
                "    @Autowired\n" +
                "    private ApFinancingTradeFileMapper apFinancingTradeFileMapper;\n" +
                "    @Autowired\n" +
                "    private ApQuotaDtlMapper apQuotaDtlMapper;\n" +
                "    @Autowired\n" +
                "    private ApCreditDealCmpt apCreditDealCmpt;\n" +
                "    @Autowired\n" +
                "    private ApFinancingApplyInfoMapper apFinancingApplyInfoMapper;\n" +
                "    @Autowired\n" +
                "    private ApFinancingCmpt apFinancingCmpt;\n" +
                "\n" +
                "    public String getTrCode() {\n" +
                "        return ApFinancingTrcodeEnum.APPACKLOANSERVICE_SIGNPACKLOANAPPLY.getCode();\n" +
                "    }\n" +
                "\n" +
                "    public void checkBusiness(SignPackLoanApplyReq req, SignPackLoanApplyResp resp, Map<String, Object> map) {\n" +
                "        ApPackLoanApply lpPackLoanApply = this.apPackLoanApplyMapper.selectByPrimaryKey(req.getFinancingId(), req.getTenantId());\n" +
                "        if (ObjectUtils.isEmpty((Object)lpPackLoanApply) || !G20061.CONTRACT_PENDING_SIGNATURE.getCode().equals(lpPackLoanApply.getFinancingStat())) {\n" +
                "            throw TbspExcepUtil.createTbspBaseException((String)ApErrorEnum.APPLY_STAT_NOT_CONTRACT_PENDING_SIGNATURE_ERROR.getCode());\n" +
                "        }\n" +
                "        ApCustInfoBO apCustInfoBO = this.apCustInfoCmpt.getApCustInfo(req.getTenantId(), lpPackLoanApply.getCustNo(), CheckLevelEnum.ONE);\n" +
                "        map.put(\"custInfo\", apCustInfoBO);\n" +
                "        map.put(AP_DOM_FORFATING_APPLY, lpPackLoanApply);\n" +
                "    }\n" +
                "\n" +
                "    public void handle(SignPackLoanApplyReq req, SignPackLoanApplyResp resp, Map<String, Object> map) {\n" +
                "        List<String> signStapFileTypes = Arrays.asList(G20032.CKDDRZHT.getCode(), G20032.CKXYZDBDJHT.getCode(), G20032.MYRZJKPZ.getCode());\n" +
                "        ApCustInfoBO apCustInfoBO = (ApCustInfoBO)map.get(\"custInfo\");\n" +
                "        ApPackLoanApply lpPackLoanApply = (ApPackLoanApply)map.get(AP_DOM_FORFATING_APPLY);\n" +
                "        ArrayList<ApFinancingTradeFile> lpFinancingTradeFiles = new ArrayList<ApFinancingTradeFile>();\n" +
                "        ArrayList<String> LpFinancingTradeFileIds = new ArrayList<String>();\n" +
                "        if (!CollectionUtils.isEmpty((Collection)req.getFileDTOs())) {\n" +
                "            UploadFileReqBO uploadFileReqBO = new UploadFileReqBO();\n" +
                "            TbspUtil.setRequestFromRequest((TbspRequest)req, (TbspRequest)uploadFileReqBO);\n" +
                "            uploadFileReqBO.setDocId(lpPackLoanApply.getImageId());\n" +
                "            ArrayList<UploadFileBO> uploadFileBOS = new ArrayList<UploadFileBO>();\n" +
                "            for (FileDTO fileDTO : req.getFileDTOs()) {\n" +
                "                String filePath = fileDTO.getFilePath();\n" +
                "                if (signStapFileTypes.contains(fileDTO.getFileType())) {\n" +
                "                    filePath = this.apFinancingCmpt.signBankStamp((TbspRequest)req, apCustInfoBO, fileDTO.getFilePath(), \"356\");\n" +
                "                }\n" +
                "                UploadFileBO uploadFileBO = new UploadFileBO();\n" +
                "                uploadFileBO.setFileName(fileDTO.getFileName());\n" +
                "                uploadFileBO.setFilePath(filePath);\n" +
                "                uploadFileBO.setFileType(fileDTO.getFileType());\n" +
                "                uploadFileBO.setFileSuffix(\"pdf\");\n" +
                "                uploadFileBOS.add(uploadFileBO);\n" +
                "                ApFinancingTradeFile ApFinancingTradeFile2 = new ApFinancingTradeFile();\n" +
                "                ApFinancingTradeFile2.setFileId(TbspStringUtil.getUUID());\n" +
                "                ApFinancingTradeFile2.setTenantId(req.getTenantId());\n" +
                "                ApFinancingTradeFile2.setStat(G00009.EFFCTIVE.getCode());\n" +
                "                ApFinancingTradeFile2.setBusiType(G00259.PACKING_CREDIT.getCode());\n" +
                "                ApFinancingTradeFile2.setTradeApplyId(lpPackLoanApply.getFinancingId());\n" +
                "                ApFinancingTradeFile2.setFileType(fileDTO.getFileType());\n" +
                "                ApFinancingTradeFile2.setFileName(fileDTO.getFileName());\n" +
                "                ApFinancingTradeFile2.setFilePath(filePath);\n" +
                "                lpFinancingTradeFiles.add(ApFinancingTradeFile2);\n" +
                "                LpFinancingTradeFileIds.add(ApFinancingTradeFile2.getFileId());\n" +
                "            }\n" +
                "            uploadFileReqBO.setFileDtos(uploadFileBOS);\n" +
                "            this.aiFinancingRemoteService.uploadFile(uploadFileReqBO);\n" +
                "        }\n" +
                "        if (!CollectionUtils.isEmpty(lpFinancingTradeFiles)) {\n" +
                "            this.apFinancingTradeFileMapper.insertBatch(lpFinancingTradeFiles);\n" +
                "        }\n" +
                "        BuildLcPackLoanContractReqBO reqBO = new BuildLcPackLoanContractReqBO();\n" +
                "        TbspUtil.setRequestFromRequest((TbspRequest)req, (TbspRequest)reqBO);\n" +
                "        reqBO.setCustNo(apCustInfoBO.getHostCustNo());\n" +
                "        reqBO.setPoolRiskLevel(lpPackLoanApply.getPoolRiskLevel());\n" +
                "        reqBO.setApplyCustName(lpPackLoanApply.getApplyCustName());\n" +
                "        reqBO.setPackLoanType(lpPackLoanApply.getPackLoanType());\n" +
                "        reqBO.setCurrency(lpPackLoanApply.getCurrency());\n" +
                "        reqBO.setAmt(TbspNumberUtil.bigDecimal2Str((BigDecimal)lpPackLoanApply.getAmt(), (int)2));\n" +
                "        reqBO.setAssetNo(lpPackLoanApply.getAssetNo());\n" +
                "        reqBO.setFinDuration(TbspNumberUtil.bigDecimal2Str((BigDecimal)lpPackLoanApply.getFinDuration()));\n" +
                "        reqBO.setPaymentType(lpPackLoanApply.getPaymentType());\n" +
                "        reqBO.setPurpose(lpPackLoanApply.getPurpose());\n" +
                "        reqBO.setEntrustedAcctNo(lpPackLoanApply.getEntrustedAcctNo());\n" +
                "        reqBO.setEntrustedAcctName(lpPackLoanApply.getEntrustedAcctName());\n" +
                "        reqBO.setEntrustedBankNo(lpPackLoanApply.getEntrustedBankNo());\n" +
                "        reqBO.setEntrustedBankName(lpPackLoanApply.getEntrustedBankName());\n" +
                "        reqBO.setBankFlag(lpPackLoanApply.getBankFlag());\n" +
                "        reqBO.setLcNo(lpPackLoanApply.getLcNo());\n" +
                "        reqBO.setBpNo(lpPackLoanApply.getBpNo());\n" +
                "        reqBO.setBpCurrency(lpPackLoanApply.getBpCurrency());\n" +
                "        reqBO.setBpAmt(TbspNumberUtil.bigDecimal2Str((BigDecimal)lpPackLoanApply.getBpAmt(), (int)2));\n" +
                "        reqBO.setDeferPayType(lpPackLoanApply.getDeferPayType());\n" +
                "        reqBO.setForwardDays(TbspNumberUtil.bigDecimal2Str((BigDecimal)lpPackLoanApply.getForwardDays()));\n" +
                "        reqBO.setIssueDate(lpPackLoanApply.getIssueDate());\n" +
                "        reqBO.setOpenBankName(lpPackLoanApply.getOpenBankName());\n" +
                "        reqBO.setOpenCustName(lpPackLoanApply.getOpenCustName());\n" +
                "        reqBO.setBenefNameAddr(lpPackLoanApply.getBenefNameAddr());\n" +
                "        reqBO.setLastShipDate(lpPackLoanApply.getLastShipDate());\n" +
                "        reqBO.setImageId(lpPackLoanApply.getImageId());\n" +
                "        reqBO.setFinancingId(lpPackLoanApply.getFinancingId());\n" +
                "        reqBO.setCrossBorderType(lpPackLoanApply.getCrossBorderType());\n" +
                "        reqBO.setPurposeCreditType(lpPackLoanApply.getPurposeCreditType());\n" +
                "        reqBO.setArguedetailType(lpPackLoanApply.getArguedetailType());\n" +
                "        reqBO.setArbitrateOrg(lpPackLoanApply.getArbitrateOrg());\n" +
                "        reqBO.setEntEnvCreditRate(lpPackLoanApply.getEntEnvCreditRate());\n" +
                "        reqBO.setEntEnvCreditDate(lpPackLoanApply.getEntEnvCreditDate());\n" +
                "        reqBO.setPricingType(lpPackLoanApply.getPricingType());\n" +
                "        if (lpPackLoanApply.getRate() != null) {\n" +
                "            reqBO.setRate(TbspNumberUtil.bigDecimal2Str((BigDecimal)lpPackLoanApply.getRate(), (int)5));\n" +
                "        }\n" +
                "        if (lpPackLoanApply.getLprRate() != null) {\n" +
                "            reqBO.setLprRate(TbspNumberUtil.bigDecimal2Str((BigDecimal)lpPackLoanApply.getLprRate().add(lpPackLoanApply.getLprRateDel()), (int)5));\n" +
                "        }\n" +
                "        reqBO.setLpRepayMode(lpPackLoanApply.getLpRepayMode());\n" +
                "        reqBO.setCleanRateType(lpPackLoanApply.getCleanRateType());\n" +
                "        reqBO.setRemark(lpPackLoanApply.getRemark());\n" +
                "        reqBO.setOverDueFloatRate(TbspNumberUtil.bigDecimal2Str((BigDecimal)lpPackLoanApply.getOverDueFloatRate(), (int)5));\n" +
                "        reqBO.setOverDueFloatType(lpPackLoanApply.getOverDueFloatType());\n" +
                "        reqBO.setDirectionName(lpPackLoanApply.getDirectionName());\n" +
                "        reqBO.setIsHelppoor(lpPackLoanApply.getIsHelppoor());\n" +
                "        reqBO.setHelppoorDetail(lpPackLoanApply.getHelppoorDetail());\n" +
                "        reqBO.setIsTenStrong(lpPackLoanApply.getIsTenStrong());\n" +
                "        reqBO.setTenStrongDetail(lpPackLoanApply.getTenStrongDetail());\n" +
                "        reqBO.setIsVillage(lpPackLoanApply.getIsVillage());\n" +
                "        reqBO.setVillageDetail(lpPackLoanApply.getVillageDetail());\n" +
                "        reqBO.setVouchFlag(lpPackLoanApply.getVouchFlag());\n" +
                "        reqBO.setVouchType(lpPackLoanApply.getVouchType());\n" +
                "        reqBO.setOtherVouchType(lpPackLoanApply.getOtherVouchType());\n" +
                "        reqBO.setIsTaxFree(lpPackLoanApply.getIsTaxFree());\n" +
                "        reqBO.setIsGreenLoan(lpPackLoanApply.getIsGreenLoan());\n" +
                "        reqBO.setGreenLoanType(lpPackLoanApply.getGreenLoanType());\n" +
                "        reqBO.setIsGreenLoanBank(lpPackLoanApply.getIsGreenLoanBank());\n" +
                "        reqBO.setGreenLoanTypeBank(lpPackLoanApply.getGreenLoanTypeBank());\n" +
                "        reqBO.setIsGreenFinanceCount(lpPackLoanApply.getIsGreenFinanceCount());\n" +
                "        reqBO.setGreenFinanceCount(lpPackLoanApply.getGreenFinanceCount());\n" +
                "        reqBO.setIsIron(lpPackLoanApply.getIsIron());\n" +
                "        reqBO.setOtherArealLoan(lpPackLoanApply.getOtherArealLoan());\n" +
                "        reqBO.setInvolvingAgriculture(lpPackLoanApply.getInvolvingAgriculture());\n" +
                "        reqBO.setIsCountyUrbanArea(lpPackLoanApply.getIsCountyUrbanArea());\n" +
                "        reqBO.setAgricultureType(lpPackLoanApply.getAgricultureType());\n" +
                "        reqBO.setContractNo(lpPackLoanApply.getContractNo());\n" +
                "        reqBO.setContractCurrency(lpPackLoanApply.getContractCurrency());\n" +
                "        reqBO.setContractAmt(TbspNumberUtil.bigDecimal2Str((BigDecimal)lpPackLoanApply.getContractAmt(), (int)2));\n" +
                "        reqBO.setProductName(lpPackLoanApply.getProductName());\n" +
                "        reqBO.setSettlementMethod(lpPackLoanApply.getSettlementMethod());\n" +
                "        reqBO.setContractSignDate(lpPackLoanApply.getContractSignDate());\n" +
                "        reqBO.setContractSetDate(lpPackLoanApply.getContractSetDate());\n" +
                "        reqBO.setContractExpireDate(lpPackLoanApply.getContractExpireDate());\n" +
                "        reqBO.setContractPayDate(lpPackLoanApply.getContractPayDate());\n" +
                "        reqBO.setBuyCode(lpPackLoanApply.getBuyCode());\n" +
                "        reqBO.setBuyName(lpPackLoanApply.getBuyName());\n" +
                "        reqBO.setCountryName(lpPackLoanApply.getCountryName());\n" +
                "        reqBO.setCountryCode(lpPackLoanApply.getCountryCode());\n" +
                "        reqBO.setInvoiceNo(lpPackLoanApply.getInvoiceNo());\n" +
                "        reqBO.setNoteMsg(lpPackLoanApply.getNoteMsg());\n" +
                "        reqBO.setBusinessNo(lpPackLoanApply.getBusinessNo());\n" +
                "        reqBO.setAdvanceAmt(TbspNumberUtil.bigDecimal2Str((BigDecimal)lpPackLoanApply.getAdvanceAmt(), (int)2));\n" +
                "        reqBO.setLcSetDate(lpPackLoanApply.getLcSetDate());\n" +
                "        reqBO.setLcExpireDate(lpPackLoanApply.getLcExpireDate());\n" +
                "        reqBO.setAllPutFlag(lpPackLoanApply.getAllPutFlag());\n" +
                "        reqBO.setAuditMsg(lpPackLoanApply.getAuditMind());\n" +
                "        reqBO.setManagerNo(apCustInfoBO.getManagerNo());\n" +
                "        reqBO.setManagerName(apCustInfoBO.getManagerName());\n" +
                "        reqBO.setExpiryDate(lpPackLoanApply.getExpiryDate());\n" +
                "        BuildLcPackLoanContractRespBO respBO = this.aiFinancingRemoteService.buildLcPackLoanContract(reqBO);\n" +
                "        ApPackLoanApply record = new ApPackLoanApply();\n" +
                "        record.setFinancingId(lpPackLoanApply.getFinancingId());\n" +
                "        record.setTenantId(req.getTenantId());\n" +
                "        if (ResponseEnum.FAIL.getCode().equals(respBO.getRespType())) {\n" +
                "            record.setFinancingStat(G20061.CONTRACT_PENDING_SIGNATURE.getCode());\n" +
                "            record.setErrCode(respBO.getRespCode());\n" +
                "            record.setErrMsg(respBO.getRespMsg());\n" +
                "            this.apPackLoanApplyMapper.updateByPrimaryKeySelective(record);\n" +
                "            ApFinancingTradeFileExample tradeFileExample = new ApFinancingTradeFileExample();\n" +
                "            tradeFileExample.createCriteria().andTenantIdEqualTo(req.getTenantId()).andStatEqualTo(G00009.EFFCTIVE.getCode()).andFileIdIn(LpFinancingTradeFileIds);\n" +
                "            ApFinancingTradeFile updTradeFile = new ApFinancingTradeFile();\n" +
                "            updTradeFile.setStat(G00009.INVALID.getCode());\n" +
                "            this.apFinancingTradeFileMapper.updateByExampleSelective(updTradeFile, tradeFileExample);\n" +
                "            resp.setRespType(ResponseEnum.FAIL.getCode());\n" +
                "            resp.setRespCode(respBO.getRespCode());\n" +
                "            resp.setRespMsg(respBO.getRespMsg());\n" +
                "        } else if (ResponseEnum.SUCCESS.getCode().equals(respBO.getRespType())) {\n" +
                "            this.releaseQuota(req, lpPackLoanApply);\n" +
                "            if (!StringUtils.isEmpty((Object)respBO.getCoreContractId())) {\n" +
                "                record.setCoreContractId(respBO.getCoreContractId());\n" +
                "            }\n" +
                "            if (!StringUtils.isEmpty((Object)respBO.getCreditContractId())) {\n" +
                "                record.setCreditContractId(respBO.getCreditContractId());\n" +
                "            }\n" +
                "            if (!StringUtils.isEmpty((Object)respBO.getDebtNo())) {\n" +
                "                record.setDebtNo(respBO.getDebtNo());\n" +
                "            }\n" +
                "            if (!StringUtils.isEmpty((Object)respBO.getLoanAcctNo())) {\n" +
                "                record.setLoanAcctNo(respBO.getLoanAcctNo());\n" +
                "            }\n" +
                "            ApplyAiLcPackLoanReqBO loanReqBO = new ApplyAiLcPackLoanReqBO();\n" +
                "            TbspUtil.setRequestFromRequest((TbspRequest)req, (TbspRequest)loanReqBO);\n" +
                "            loanReqBO.setCustNo(apCustInfoBO.getHostCustNo());\n" +
                "            loanReqBO.setPoolRiskLevel(lpPackLoanApply.getPoolRiskLevel());\n" +
                "            loanReqBO.setApplyCustName(lpPackLoanApply.getApplyCustName());\n" +
                "            loanReqBO.setPackLoanType(lpPackLoanApply.getPackLoanType());\n" +
                "            loanReqBO.setCurrency(lpPackLoanApply.getCurrency());\n" +
                "            loanReqBO.setAmt(TbspNumberUtil.bigDecimal2Str((BigDecimal)lpPackLoanApply.getAmt(), (int)2));\n" +
                "            loanReqBO.setAssetNo(lpPackLoanApply.getAssetNo());\n" +
                "            loanReqBO.setFinDuration(TbspNumberUtil.bigDecimal2Str((BigDecimal)lpPackLoanApply.getFinDuration()));\n" +
                "            loanReqBO.setPaymentType(lpPackLoanApply.getPaymentType());\n" +
                "            loanReqBO.setPurpose(lpPackLoanApply.getPurpose());\n" +
                "            loanReqBO.setEntrustedAcctNo(lpPackLoanApply.getEntrustedAcctNo());\n" +
                "            loanReqBO.setEntrustedAcctName(lpPackLoanApply.getEntrustedAcctName());\n" +
                "            loanReqBO.setEntrustedBankNo(lpPackLoanApply.getEntrustedBankNo());\n" +
                "            loanReqBO.setEntrustedBankName(lpPackLoanApply.getEntrustedBankName());\n" +
                "            loanReqBO.setBankFlag(lpPackLoanApply.getBankFlag());\n" +
                "            loanReqBO.setLcNo(lpPackLoanApply.getLcNo());\n" +
                "            loanReqBO.setBpNo(lpPackLoanApply.getBpNo());\n" +
                "            loanReqBO.setBpCurrency(lpPackLoanApply.getBpCurrency());\n" +
                "            loanReqBO.setBpAmt(TbspNumberUtil.bigDecimal2Str((BigDecimal)lpPackLoanApply.getBpAmt(), (int)2));\n" +
                "            loanReqBO.setDeferPayType(lpPackLoanApply.getDeferPayType());\n" +
                "            loanReqBO.setForwardDays(TbspNumberUtil.bigDecimal2Str((BigDecimal)lpPackLoanApply.getForwardDays()));\n" +
                "            loanReqBO.setIssueDate(lpPackLoanApply.getIssueDate());\n" +
                "            loanReqBO.setOpenBankName(lpPackLoanApply.getOpenBankName());\n" +
                "            loanReqBO.setOpenCustName(lpPackLoanApply.getOpenCustName());\n" +
                "            loanReqBO.setBenefNameAddr(lpPackLoanApply.getBenefNameAddr());\n" +
                "            loanReqBO.setLastShipDate(lpPackLoanApply.getLastShipDate());\n" +
                "            loanReqBO.setImageId(lpPackLoanApply.getImageId());\n" +
                "            loanReqBO.setFinancingId(lpPackLoanApply.getFinancingId());\n" +
                "            loanReqBO.setCrossBorderType(lpPackLoanApply.getCrossBorderType());\n" +
                "            loanReqBO.setPurposeCreditType(lpPackLoanApply.getPurposeCreditType());\n" +
                "            loanReqBO.setArguedetailType(lpPackLoanApply.getArguedetailType());\n" +
                "            loanReqBO.setArbitrateOrg(lpPackLoanApply.getArbitrateOrg());\n" +
                "            loanReqBO.setEntEnvCreditRate(lpPackLoanApply.getEntEnvCreditRate());\n" +
                "            loanReqBO.setEntEnvCreditDate(lpPackLoanApply.getEntEnvCreditDate());\n" +
                "            loanReqBO.setPricingType(lpPackLoanApply.getPricingType());\n" +
                "            loanReqBO.setRate(TbspNumberUtil.bigDecimal2Str((BigDecimal)lpPackLoanApply.getRate(), (int)5));\n" +
                "            loanReqBO.setLprRate(TbspNumberUtil.bigDecimal2Str((BigDecimal)lpPackLoanApply.getLprRate().add(lpPackLoanApply.getLprRateDel()), (int)5));\n" +
                "            loanReqBO.setLpRepayMode(lpPackLoanApply.getLpRepayMode());\n" +
                "            loanReqBO.setCleanRateType(lpPackLoanApply.getCleanRateType());\n" +
                "            loanReqBO.setRemark(lpPackLoanApply.getRemark());\n" +
                "            loanReqBO.setOverDueFloatRate(TbspNumberUtil.bigDecimal2Str((BigDecimal)lpPackLoanApply.getOverDueFloatRate(), (int)5));\n" +
                "            loanReqBO.setOverDueFloatType(lpPackLoanApply.getOverDueFloatType());\n" +
                "            loanReqBO.setExpiryDate(lpPackLoanApply.getExpiryDate());\n" +
                "            loanReqBO.setCreditContractId(respBO.getCreditContractId());\n" +
                "            loanReqBO.setDebtNo(respBO.getDebtNo());\n" +
                "            loanReqBO.setCoreContractId(respBO.getCoreContractId());\n" +
                "            ApplyAiLcPackLoanRespBO applyAiLcPackLoanRespBO = this.aiFinancingRemoteService.applyAiLcPackLoan(loanReqBO);\n" +
                "            if (!TbspUtil.isSuccess((BaseResponse)applyAiLcPackLoanRespBO)) {\n" +
                "                CancelLcPackLoanContractReqBO cancelCreditContractReqBO = new CancelLcPackLoanContractReqBO();\n" +
                "                cancelCreditContractReqBO.setApplySerialNo(lpPackLoanApply.getFinancingId());\n" +
                "                cancelCreditContractReqBO.setCreditContractId(respBO.getCreditContractId());\n" +
                "                TbspUtil.setRequestFromRequest((TbspRequest)req, (TbspRequest)cancelCreditContractReqBO);\n" +
                "                this.aiFinancingRemoteService.cancelLcPackLoanContract(cancelCreditContractReqBO);\n" +
                "                record.setFinancingStat(G20061.FAILURE.getCode());\n" +
                "                record.setErrCode(applyAiLcPackLoanRespBO.getRespCode());\n" +
                "                record.setErrMsg(applyAiLcPackLoanRespBO.getRespMsg());\n" +
                "                this.apPackLoanApplyMapper.updateByPrimaryKeySelective(record);\n" +
                "                ApFinancingApplyInfo apFinancingApplyInfo = new ApFinancingApplyInfo();\n" +
                "                apFinancingApplyInfo.setTenantId(req.getTenantId());\n" +
                "                apFinancingApplyInfo.setFinancingId(lpPackLoanApply.getFinancingId());\n" +
                "                apFinancingApplyInfo.setFinancingStat(G00264.REFUSED.getCode());\n" +
                "                apFinancingApplyInfo.setErrMsg(TbspStringUtil.subStr((String)applyAiLcPackLoanRespBO.getRespMsg(), (int)256));\n" +
                "                this.apFinancingApplyInfoMapper.updateByPrimaryKeySelective(apFinancingApplyInfo);\n" +
                "                resp.setRespType(ResponseEnum.FAIL.getCode());\n" +
                "                resp.setRespCode(applyAiLcPackLoanRespBO.getRespCode());\n" +
                "                resp.setRespMsg(applyAiLcPackLoanRespBO.getRespMsg());\n" +
                "                return;\n" +
                "            }\n" +
                "            record.setFinancingStat(G20061.PENDING_NATIONAL_SETTLEMENT_DISBURSEMENT.getCode());\n" +
                "            this.apPackLoanApplyMapper.updateByPrimaryKeySelective(record);\n" +
                "            ApFinancingApplyInfo apFinancingApplyInfo = new ApFinancingApplyInfo();\n" +
                "            apFinancingApplyInfo.setTenantId(req.getTenantId());\n" +
                "            apFinancingApplyInfo.setFinancingId(lpPackLoanApply.getFinancingId());\n" +
                "            apFinancingApplyInfo.setFinancingStat(G00264.UNDER_DISBURSEMENT.getCode());\n" +
                "            this.apFinancingApplyInfoMapper.updateByPrimaryKeySelective(apFinancingApplyInfo);\n" +
                "        } else {\n" +
                "            CancelLcPackLoanContractReqBO cancelLcPackLoanContractReqBO = new CancelLcPackLoanContractReqBO();\n" +
                "            cancelLcPackLoanContractReqBO.setApplySerialNo(lpPackLoanApply.getFinancingId());\n" +
                "            cancelLcPackLoanContractReqBO.setCreditContractId(respBO.getCreditContractId());\n" +
                "            TbspUtil.setRequestFromRequest((TbspRequest)req, (TbspRequest)cancelLcPackLoanContractReqBO);\n" +
                "            this.aiFinancingRemoteService.cancelLcPackLoanContract(cancelLcPackLoanContractReqBO);\n" +
                "            record.setFinancingStat(G20061.FAILURE.getCode());\n" +
                "            record.setErrCode(respBO.getRespCode());\n" +
                "            record.setErrMsg(respBO.getRespMsg());\n" +
                "            this.apPackLoanApplyMapper.updateByPrimaryKeySelective(record);\n" +
                "            ApFinancingTradeFileExample tradeFileExample = new ApFinancingTradeFileExample();\n" +
                "            tradeFileExample.createCriteria().andTenantIdEqualTo(req.getTenantId()).andStatEqualTo(G00009.EFFCTIVE.getCode()).andFileIdIn(LpFinancingTradeFileIds);\n" +
                "            ApFinancingTradeFile updTradeFile = new ApFinancingTradeFile();\n" +
                "            updTradeFile.setStat(G00009.INVALID.getCode());\n" +
                "            this.apFinancingTradeFileMapper.updateByExampleSelective(updTradeFile, tradeFileExample);\n" +
                "            resp.setRespType(ResponseEnum.FAIL.getCode());\n" +
                "            resp.setRespCode(respBO.getRespCode());\n" +
                "            resp.setRespMsg(respBO.getRespMsg());\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private void releaseQuota(SignPackLoanApplyReq req, ApPackLoanApply lpPackLoanApply) {\n" +
                "        List<ApQuotaDtl> occupyQuotaInfos = this.getOccupyQuotaInfos(lpPackLoanApply.getFinancingId(), lpPackLoanApply.getCustNo(), lpPackLoanApply.getPoolRiskLevel(), req.getTenantId());\n" +
                "        for (ApQuotaDtl occupyQuotaInfo : occupyQuotaInfos) {\n" +
                "            this.apCreditDealCmpt.updateLocalQuota((TbspRequest)req, lpPackLoanApply.getCustNo(), null, null, lpPackLoanApply.getFinancingId(), TbspNumberUtil.bigDecimal2Str((BigDecimal)occupyQuotaInfo.getRemainderQuota()), null, (Map)Maps.newHashMap());\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private List<ApQuotaDtl> getOccupyQuotaInfos(String financingId, String custNo, String poolRiskLevel, String tenantId) {\n" +
                "        ApQuotaDtlExample example = new ApQuotaDtlExample();\n" +
                "        example.createCriteria().andTrNoEqualTo(financingId).andTenantIdEqualTo(tenantId).andCustNoEqualTo(custNo).andPoolRiskLevelEqualTo(poolRiskLevel).andTrTypeEqualTo(G00260.USE.getCode()).andStatEqualTo(G00009.EFFCTIVE.getCode());\n" +
                "        return this.apQuotaDtlMapper.selectByExample(example);\n" +
                "    }\n" +
                "}\n";

        JavaAST javaAST = CompilerUtil.parseAST(code);
        ByteCodeUtils.toJavassistCode(javaAST);
        System.out.println(javaAST.getSourceCode());
    }

}


