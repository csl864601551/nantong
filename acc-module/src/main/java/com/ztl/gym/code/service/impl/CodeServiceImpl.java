package com.ztl.gym.code.service.impl;

import com.ztl.gym.code.domain.Code;
import com.ztl.gym.code.domain.CodeAttr;
import com.ztl.gym.code.domain.CodeRecord;
import com.ztl.gym.code.domain.CodeSingle;
import com.ztl.gym.code.domain.vo.CodeVo;
import com.ztl.gym.code.mapper.CodeMapper;
import com.ztl.gym.code.mapper.CodeRecordMapper;
import com.ztl.gym.code.mapper.CodeSingleMapper;
import com.ztl.gym.code.service.ICodeAttrService;
import com.ztl.gym.code.service.ICodeService;
import com.ztl.gym.common.annotation.DataSource;
import com.ztl.gym.common.constant.AccConstants;
import com.ztl.gym.common.enums.DataSourceType;
import com.ztl.gym.common.exception.CustomException;
import com.ztl.gym.common.service.CommonService;
import com.ztl.gym.common.utils.CodeRuleUtils;
import com.ztl.gym.common.utils.DateUtils;
import com.ztl.gym.common.utils.SecurityUtils;
import com.ztl.gym.common.utils.StringUtils;
import com.ztl.gym.storage.domain.vo.FlowVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 码 Service业务层处理
 *
 * @author ruoyi
 * @date 2021-04-14
 */
@Service
public class CodeServiceImpl implements ICodeService {
    private static final Logger logger = LoggerFactory.getLogger(CodeServiceImpl.class);

    @Autowired
    private CodeMapper codeMapper;

    @Autowired
    private CodeRecordMapper codeRecordMapper;
    @Autowired
    private CodeSingleMapper codeSingleMapper;

    @Autowired
    private CommonService commonService;

    @Autowired
    private ICodeAttrService codeAttrService;

    @Override
    @DataSource(DataSourceType.SHARDING)
    public Code selectCode(Code code) {
        return codeMapper.selectCode(code);
    }

    /**
     * 查询码
     *
     * @param codeIndex 码 ID
     * @return 码
     */
    @Override
    public Code selectCodeById(Long codeIndex) {
        return codeMapper.selectCodeById(codeIndex);
    }

    /**
     * 查询码 列表
     *
     * @param code 码
     * @return 码
     */
    @Override
    @DataSource(DataSourceType.SHARDING)
    public List<Code> selectCodeList(Code code) {
        return codeMapper.selectCodeList(code);
    }

    /**
     * 新增码
     *
     * @param code 码
     * @return 结果
     */
    @Override
    @DataSource(DataSourceType.SHARDING)
    public int insertCode(Code code) {
        return codeMapper.insertCode(code);
    }

    /**
     * 修改码
     *
     * @param code 码
     * @return 结果
     */
    @Override
    @DataSource(DataSourceType.SHARDING)
    public int updateCode(Code code) {
        return codeMapper.updateCode(code);
    }

    /**
     * 批量删除码
     *
     * @param codeIndexs 需要删除的码 ID
     * @return 结果
     */
    @Override
    public int deleteCodeByIds(Long[] codeIndexs) {
        return codeMapper.deleteCodeByIds(codeIndexs);
    }

    /**
     * 删除码 信息
     *
     * @param codeIndex 码 ID
     * @return 结果
     */
    @Override
    public int deleteCodeById(Long codeIndex) {
        return codeMapper.deleteCodeById(codeIndex);
    }

    /**
     * 生码
     *
     * @param companyId    企业id
     * @param codeRecordId 生码记录id
     * @param codeTotalNum 生码总数
     * @param boxCount     箱数
     * @param userId       用户id
     * @return
     */
    @Override
    @DataSource(DataSourceType.SHARDING)
    @Transactional(rollbackFor = Exception.class)
    public int createCode(Long companyId, Long codeRecordId, Long codeTotalNum, long boxCount, Long userId) {
        int correct = 0;
        List<Code> codeList = new ArrayList<>();
        //企业自增数
        CodeRecord codeRecord = codeRecordMapper.selectCodeRecordById(codeRecordId);
        long codeIndex = codeRecord.getIndexStart();
        if (boxCount > 0) {
            for (int i = 0; i < boxCount; i++) {
                //按箱来创建码属性
                long boxAttrId = saveCodeAttr(companyId, userId, codeRecord.getId(), codeRecord.getIndexStart(), codeRecord.getIndexEnd());

                //箱码
                String pCode = CodeRuleUtils.buildCode(companyId, CodeRuleUtils.CODE_PREFIX_B, codeIndex);
                Code boxCode = new Code();
                boxCode.setCodeIndex(codeIndex);
                boxCode.setCompanyId(companyId);
                boxCode.setCodeType(AccConstants.CODE_TYPE_BOX);
                boxCode.setCode(pCode);
                boxCode.setCodeAttrId(boxAttrId);
                codeList.add(boxCode);
                //单码流水号+1
                codeIndex += 1;

                //单码
                for (int j = 0; j < codeTotalNum; j++) {
                    Code singleCode = new Code();
                    singleCode.setCodeIndex(codeIndex);
                    singleCode.setpCode(pCode);
                    singleCode.setCompanyId(companyId);
                    singleCode.setCodeType(AccConstants.CODE_TYPE_SINGLE);
                    singleCode.setCode(CodeRuleUtils.buildCode(companyId, CodeRuleUtils.CODE_PREFIX_S, singleCode.getCodeIndex()));
                    singleCode.setCodeAttrId(boxAttrId);
                    codeList.add(singleCode);
                    codeIndex += 1;
                }

                //更新自增数
                if (i + 1 == boxCount) {
                    commonService.updateVal(companyId, codeRecord.getIndexEnd());
                }
            }
        } else {
            long attrId = saveCodeAttr(companyId, userId, codeRecord.getId(), codeRecord.getIndexStart(), codeRecord.getIndexEnd());
            for (int i = 0; i < codeTotalNum; i++) {
                Code code = new Code();
                code.setCodeIndex(codeIndex + i);
                code.setpCode(null);
                code.setCompanyId(companyId);
                code.setCodeType(AccConstants.CODE_TYPE_SINGLE);
                code.setCode(CodeRuleUtils.buildCode(companyId, CodeRuleUtils.CODE_PREFIX_S, code.getCodeIndex()));
                code.setCodeAttrId(attrId);
                codeList.add(code);

                //更新自增数
                if (i + 1 == codeTotalNum) {
                    commonService.updateVal(companyId, codeRecord.getIndexEnd());
                }
            }
        }

        int res = codeMapper.insertCodeForBatch(codeList);
        if (res > 0) {
            logger.info("生码记录ID：" + codeRecordId + "生码成功");
            Map<String, Object> params = new HashMap<>();
            params.put("id", codeRecordId);
            params.put("status", AccConstants.CODE_RECORD_STATUS_FINISH);
            codeRecordMapper.insertCodeRecordStatus(params);
        } else {
            logger.error("生码记录ID：" + codeRecordId + "生码异常");
        }
        return correct;
    }
    /**
     * 生码
     *
     * @param companyId    企业id
     * @param codeSingleId 生码记录id
     * @param codeTotalNum 生码总数
     * @param userId       用户id
     * @return
     */
    @Override
    @DataSource(DataSourceType.SHARDING)
    @Transactional(rollbackFor = Exception.class)
    public int createCodeSingle(Long companyId, Long codeSingleId, Long codeTotalNum, Long userId) {
        int correct = 0;
        List<Code> codeList = new ArrayList<>();
        //企业自增数
        CodeSingle codeSingle = codeSingleMapper.selectCodeSingleById(codeSingleId);
        long codeIndex = codeSingle.getIndexStart();


        for (int i = 0; i < codeTotalNum; i++) {
            Code code = new Code();
            code.setCodeIndex(codeIndex + i);
            code.setpCode(null);
            code.setCompanyId(companyId);
            code.setCodeType(AccConstants.CODE_TYPE_SINGLE);
            code.setCode(CodeRuleUtils.buildCode(companyId, CodeRuleUtils.CODE_PREFIX_S, code.getCodeIndex()));
            code.setSingleId(codeSingleId);
            codeList.add(code);

            //更新自增数
            if (i + 1 == codeTotalNum) {
                commonService.updateVal(companyId, codeSingle.getIndexEnd());
            }
        }


        int res = codeMapper.insertCodeForBatch(codeList);
        if (res > 0) {
            logger.info("single生码记录ID：" + codeSingleId + "生码成功");
            Map<String, Object> params = new HashMap<>();
            params.put("id", codeSingleId);
            params.put("status", AccConstants.CODE_RECORD_STATUS_FINISH);
            codeSingleMapper.insertCodeSingleStatus(params);
        } else {
            logger.error("single生码记录ID：" + codeSingleId + "生码异常");
        }
        return correct;
    }

    @Override
    @DataSource(DataSourceType.SHARDING)
    public int updateStatusByAttrId(Long companyId, List<Long> idList, int status) {
        Map<String, Object> params = new HashMap<>();
        params.put("companyId", companyId);
        params.put("idList", idList);
        params.put("status", status);
        return codeMapper.updateStatusByAttrId(params);
    }

    /**
     * 批量新增单码流转明细 【insertProvider形式】
     *
     * @param companyId   企业id
     * @param storageType 流转类型
     * @param list        流转码集合
     * @return
     */
    @Override
    @DataSource(DataSourceType.SHARDING)
    public int insertCodeFlowForBatchSingle(long companyId, int storageType, List<FlowVo> list) {
        //        return codeMapper.insertCodeFlowForBatchSingle(companyId, list);
        if (storageType == AccConstants.STORAGE_TYPE_IN) {
            return codeMapper.insertInCodeFlowForBatchSingleV2(companyId, list);
        } else if (storageType == AccConstants.STORAGE_TYPE_OUT) {
            return codeMapper.insertOutCodeFlowForBatchSingleV2(companyId, list);
        } else if (storageType == AccConstants.STORAGE_TYPE_TRANSFER) {
            return codeMapper.insertTransferCodeFlowForBatchSingleV2(companyId, list);
        } else if (storageType == AccConstants.STORAGE_TYPE_BACK) {
            return codeMapper.insertBackCodeFlowForBatchSingleV2(companyId, list);
        } else {
            throw new CustomException("未知的流转类型");
        }
    }

    /**
     * 根据物流流转信息查询码集合
     *
     * @param companyId
     * @param storageType
     * @param storageRecordId
     * @return
     */
    @Override
    @DataSource(DataSourceType.SHARDING)
    public List<String> selectCodeByStorage(long companyId, int storageType, long storageRecordId) {
        Map<String, Object> params = new HashMap<>();
        params.put("companyId", companyId);
        params.put("storageType", storageType);
        params.put("storageRecordId", storageRecordId);

        List<String> list = null;
        if (storageType == AccConstants.STORAGE_TYPE_IN) {
            list = codeMapper.selectInCodeByStorage(params);
        } else if (storageType == AccConstants.STORAGE_TYPE_OUT) {
            list = codeMapper.selectOutCodeByStorage(params);
        } else if (storageType == AccConstants.STORAGE_TYPE_TRANSFER) {
            list = codeMapper.selectTransferCodeByStorage(params);
        } else if (storageType == AccConstants.STORAGE_TYPE_BACK) {
            list = codeMapper.selectBackCodeByStorage(params);
        }
        return list;
    }

    /**
     * 构建批量插入单码明细sql
     *
     * @param list
     * @return
     */
    public static String buildInsertBatchCodeFlowSql(long companyId, List<FlowVo> list) {
        StringBuffer sqlList = new StringBuffer();
        sqlList.append(" INSERT INTO t_code_flow(company_id,code,storage_type,storage_record_id,create_user,create_time)  VALUES ");
        for (int i = 0; i < list.size(); i++) {
            FlowVo flowVo = list.get(i);
            sqlList.append(" (").
                    append(companyId).append(",")
                    .append("'").append(flowVo.getCode()).append("',")
                    .append(flowVo.getStorageType()).append(",")
                    .append(flowVo.getStorageRecordId()).append(",")
                    .append(flowVo.getCreateUser()).append(",")
                    .append("'").append(flowVo.getCreateTime()).append("'")
                    .append(")");
            if (i < list.size() - 1) {
                sqlList.append(",");
            }
        }
        System.out.println("sql: " + sqlList.toString());
        return sqlList.toString();
    }

    @Override
    @DataSource(DataSourceType.SHARDING)
    public List<Code> selectCodeListByCodeOrIndex(Map<String, Object> map) {
        Code code = new Code();
        if (map.get("code") == null) {
            code.setCode("");
        } else {
            code.setCode(map.get("code").toString());
        }
        code.setCompanyId(Long.valueOf(map.get("companyId").toString()));
        Code temp = codeMapper.selectCode(code);
        if (temp == null) {
            throw new CustomException("未查询到相关码信息，请检查码最新流转状态");
        } else {
            if (temp.getCodeType().equals("box")) {
                return codeMapper.selectCodeListByCodeOrIndex(map);
            } else {
                if (temp.getpCode() == null) {
                    return codeMapper.selectCodeListByCodeOrIndex(map);
                }
                map.put("code", temp.getpCode());
                return codeMapper.selectCodeListByCodeOrIndex(map);
            }
        }
    }

    @Override
    @DataSource(DataSourceType.SHARDING)
    public long getCodeCount(String codeStr) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", codeStr);
        map.put("companyId", Long.valueOf(SecurityUtils.getLoginUserTopCompanyId()));
        List<Code> codeList = selectCodeListByCodeOrIndex(map);
        boolean flag = false;
        for (Code code : codeList) {
//            if (code.getCode().startsWith("P")) {
            if (CodeRuleUtils.getCodeType(code.getCode()).equals(AccConstants.CODE_TYPE_BOX)) {
                flag = true;
            }
        }
        if (flag) {
            return codeList.size() - 1;
        }
        return 1;
    }

    /**
     * 根据生码记录id查询码集合
     *
     * @param recordId
     * @return
     */
    @Override
    @DataSource(DataSourceType.SHARDING)
    public List<Code> selectCodeListByRecord(Long companyId, Long recordId) {
        Map<String, Object> params = new HashMap<>();
        params.put("companyId", companyId);
        params.put("recordId", recordId);
        return codeMapper.selectCodeListByRecord(companyId, recordId);
    }
    /**
     * 根据生码记录id查询码集合
     *
     * @param singleId
     * @return
     */
    @Override
    @DataSource(DataSourceType.SHARDING)
    public List<Code> selectCodeListBySingle(Long companyId, Long singleId) {
        Map<String, Object> params = new HashMap<>();
        params.put("companyId", companyId);
        params.put("singleId", singleId);
        return codeMapper.selectCodeListBySingle(companyId, singleId);
    }


    /**
     * 保存生码属性
     *
     * @param companyId
     * @param codeRecordId
     * @param indexStart
     * @param indexEnd
     */
    private long saveCodeAttr(long companyId, long userId, long codeRecordId, long indexStart, long indexEnd) {
        CodeAttr codeAttr = new CodeAttr();
        codeAttr.setCompanyId(companyId);
        codeAttr.setTenantId(companyId);
        codeAttr.setRecordId(codeRecordId);
        codeAttr.setIndexStart(indexStart);
        codeAttr.setIndexEnd(indexEnd);
        codeAttr.setCreateUser(userId);
        codeAttr.setCreateTime(new Date());
        codeAttr.setUpdateUser(userId);
        codeAttr.setUpdateTime(new Date());
        codeAttrService.insertCodeAttr(codeAttr);
        return codeAttr.getId();
    }

    @Override
    public void updatePCodeByCode(Long companyId,String pCode, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("companyId", companyId);
        params.put("pCode", pCode);
        params.put("code", code);
        codeMapper.updatePCodeByCode(params);
    }

    @Override
    @DataSource(DataSourceType.SHARDING)
    public List<Code> selectCodes(Map<String, Object> codeParam) {
        return codeMapper.selectCodes(codeParam);
    }

    @Override
    @DataSource(DataSourceType.SHARDING)
    public void updateCodeStorageByPCode(Code codeTemp) {
        codeMapper.updateCodeStorageByPCode(codeTemp);
    }

    @Override
    @DataSource(DataSourceType.SHARDING)
    public int updateCodeStorageByCode(Code codeRes) {
        return codeMapper.updateCodeStorageByCode(codeRes);
    }

    @Override
    @DataSource(DataSourceType.SHARDING)
    public void updateCodeAttrIdByPCode(Map<String, Object> param) {
        codeMapper.updateCodeAttrIdByPCode(param);
    }
}
