package com.ztl.gym.code.service.impl;

import com.ztl.gym.code.domain.Code;
import com.ztl.gym.code.domain.CodeSingle;
import com.ztl.gym.code.mapper.CodeMapper;
import com.ztl.gym.code.mapper.CodeSingleMapper;
import com.ztl.gym.code.service.ICodeAttrService;
import com.ztl.gym.code.service.ICodeSingleService;
import com.ztl.gym.code.service.ICodeService;
import com.ztl.gym.common.constant.AccConstants;
import com.ztl.gym.common.constant.HttpStatus;
import com.ztl.gym.common.exception.CustomException;
import com.ztl.gym.common.service.CommonService;
import com.ztl.gym.common.utils.CodeRuleUtils;
import com.ztl.gym.common.utils.DateUtils;
import com.ztl.gym.common.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;


/**
 * 生码记录Service业务层处理
 *
 * @author ruoyi
 * @date 2021-04-13
 */
@Service
public class CodeSingleServiceImpl implements ICodeSingleService {
    private static final Logger log = LoggerFactory.getLogger(CodeSingleServiceImpl.class);

    @Autowired
    private ICodeService codeService;

    @Autowired
    private ICodeAttrService codeAttrService;

    @Autowired
    private CodeSingleMapper codeSingleMapper;

    @Autowired
    private CodeMapper codeMapper;

    @Autowired
    private CommonService commonService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 查询生码记录
     *
     * @param id 生码记录ID
     * @return 生码记录
     */
    @Override
    public CodeSingle selectCodeSingleById(Long id) {
        return codeSingleMapper.selectCodeSingleById(id);
    }


    /**
     * 查询生码记录
     *
     * @param codeIndex 生码记录ID
     * @return 生码记录
     */
    @Override
    public CodeSingle selectCodeSingleByIndex(long codeIndex, long companyId) {
        return codeSingleMapper.selectCodeSingleByIndex(codeIndex,companyId);
    }

    /**
     * 查询生码记录列表
     *
     * @param codeSingle 生码记录
     * @return 生码记录
     */
    @Override
    public List<CodeSingle> selectCodeSingleList(CodeSingle codeSingle) {
        return codeSingleMapper.selectCodeSingleList(codeSingle);
    }

    /**
     * 新增生码记录
     *
     * @param codeSingle 生码记录
     * @return 结果
     */
    @Override
    public int insertCodeSingle(CodeSingle codeSingle) {
        codeSingle.setCreateTime(DateUtils.getNowDate());
        return codeSingleMapper.insertCodeSingle(codeSingle);
    }

    /**
     * 修改生码记录
     *
     * @param codeSingle 生码记录
     * @return 结果
     */
    @Override
    public int updateCodeSingle(CodeSingle codeSingle) {
        codeSingle.setUpdateTime(DateUtils.getNowDate());
        return codeSingleMapper.updateCodeSingle(codeSingle);
    }

    /**
     * 批量删除生码记录
     *
     * @param ids 需要删除的生码记录ID
     * @return 结果
     */
    @Override
    public int deleteCodeSingleByIds(Long[] ids) {
        return codeSingleMapper.deleteCodeSingleByIds(ids);
    }

    /**
     * 删除生码记录信息
     *
     * @param id 生码记录ID
     * @return 结果
     */
    @Override
    public int deleteCodeSingleById(Long id) {
        return codeSingleMapper.deleteCodeSingleById(id);
    }

    /**
     * 生码-普通单码
     *
     * @param companyId 企业id
     * @param num       生码数量
     * @param remark    备注详情
     * @return
     */
    @Override
    public int createCodeSingle(long companyId, long num, String remark) {
        CodeSingle codeSingle = buildCodeSingle(companyId, AccConstants.GEN_CODE_TYPE_SINGLE, 0, num, remark);
        int res = codeSingleMapper.insertCodeSingle(codeSingle);
        if (res > 0) {
            //生码属性
//            long codeAttrId = saveCodeAttr(companyId, CodeSingle.getId(), CodeSingle.getIndexStart(), CodeSingle.getIndexEnd());
            long userId = SecurityUtils.getLoginUser().getUser().getUserId();

            //获取IP
            String localIp = "";
            try {
                InetAddress ip4 = Inet4Address.getLocalHost();
                localIp = ip4.getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            //异步生码
            String message = userId + "^" + codeSingle.getId() + "^" + companyId + "^" + num + "^" + localIp;
            stringRedisTemplate.convertAndSend("acc.code.genSingle", message);
        }
        return res;
    }
    /**
     * 生码-防伪码
     *
     * @param companyId 企业id
     * @param num       生码数量
     * @param remark    备注详情
     * @return
     */
    @Override
    public int createAccCodeSingle(long companyId, long num, String remark) {
        CodeSingle codeSingle = buildCodeSingle(companyId, AccConstants.GEN_CODE_TYPE_ACC, 0, num, remark);
        codeSingle.setStatus(AccConstants.CODE_RECORD_STATUS_EVA);
        int res = codeSingleMapper.insertCodeSingle(codeSingle);
        if (res > 0) {
            List<Map> codeList = new ArrayList<>();
            Date createTime=DateUtils.getNowDate();
            for (int i = 0; i < num; i++) {
                Map<String,Object> code=new HashMap();
                code.put("companyId",companyId);
                code.put("codeAcc",CodeRuleUtils.buildAccCode(companyId));
                code.put("singleId",codeSingle.getId());
                code.put("createTime",createTime);
                codeList.add(code);

            }
            res = codeMapper.insertAccCodeForBatch(codeList);
        }
        return res;
    }



    /**
     * redis生码监听
     *
     * @param codeGenMessage
     */
    public void onPublishCodeSingle(String codeGenMessage) {
        //获取IP
        String localIp = "";
        try {
            InetAddress ip4 = Inet4Address.getLocalHost();
            localIp = ip4.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        System.out.println(codeGenMessage);
        log.info("onPublishCodeSingle {}", codeGenMessage);
        try {
            String[] codeGenMsgs = codeGenMessage.split("\\^");
            //生码属性id
            long userId = Long.parseLong(codeGenMsgs[0]);
            //生码记录id
            long CodeSingleId = Long.parseLong(codeGenMsgs[1]);
            //企业id
            long companyId = Long.parseLong(codeGenMsgs[2]);
            //生码总数
            long codeTotalNum = Long.parseLong(codeGenMsgs[3]);
            //ip
            String ip = codeGenMsgs[4];
            if (localIp.equals(ip)) {
                codeService.createCodeSingle(companyId, CodeSingleId, codeTotalNum,  userId);
            }
        } catch (Exception e) {
            throw new CustomException("接收数据异常，请检查码数据格式！", HttpStatus.ERROR);
        }

    }

    /**
     * 构建生码记录
     *
     * @param companyId
     * @param type
     * @param num
     * @param remark
     * @return
     */
    public static CodeSingle buildCodeSingle(long companyId, int type, long boxCount, long num, String remark) {
        CodeSingle CodeSingle = new CodeSingle();
        //获取并更新生码记录流水号
        String codePrefix = null;
        if (type == AccConstants.GEN_CODE_TYPE_SINGLE) {
            codePrefix = CodeRuleUtils.CODE_PREFIX_S;
        } else if (type == AccConstants.GEN_CODE_TYPE_BOX) {
            codePrefix = CodeRuleUtils.CODE_PREFIX_B;
        }
        if(codePrefix != null){
            String codeNoStr = CodeRuleUtils.getCodeIndex(companyId, boxCount, num, codePrefix);
            String[] codeIndexs = codeNoStr.split("-");
            CodeSingle.setIndexStart(Long.parseLong(codeIndexs[0]) + 1);
            CodeSingle.setIndexEnd(Long.parseLong(codeIndexs[1]));
        }

        //基础属性
        CodeSingle.setCompanyId(companyId);
        long totalNum = num;
        if (boxCount > 0) {
            totalNum = boxCount * totalNum + boxCount;
        }
        CodeSingle.setCount(totalNum);
        CodeSingle.setType(type);
        CodeSingle.setStatus(AccConstants.CODE_RECORD_STATUS_WAIT);
        CodeSingle.setRemark(remark);
        CodeSingle.setCreateUser(SecurityUtils.getLoginUser().getUser().getUserId());
        CodeSingle.setUpdateUser(SecurityUtils.getLoginUser().getUser().getUserId());
        CodeSingle.setCreateTime(new Date());
        CodeSingle.setUpdateTime(new Date());
        return CodeSingle;
    }
}
