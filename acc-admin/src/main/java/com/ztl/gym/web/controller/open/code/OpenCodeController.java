package com.ztl.gym.web.controller.open.code;

import com.ztl.gym.code.domain.Code;
import com.ztl.gym.code.domain.vo.CRMInfoVo;
import com.ztl.gym.code.service.ICodeService;
import com.ztl.gym.common.constant.AccConstants;
import com.ztl.gym.common.constant.HttpStatus;
import com.ztl.gym.common.core.domain.AjaxResult;
import com.ztl.gym.common.exception.CustomException;
import com.ztl.gym.common.service.CommonService;
import com.ztl.gym.common.utils.CodeRuleUtils;
import com.ztl.gym.common.utils.DateUtils;
import com.ztl.gym.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/open/code")
public class OpenCodeController {

    @Autowired
    private ICodeService codeService;
    @Autowired
    private CommonService commonService;

    @Value("${ruoyi.preFixUrl}")
    private String preFixUrl;

    /**
     * 获取所有码
     *
     * @return 获取所有码
     */
    @PostMapping("getCodes")
    public AjaxResult getCodes(@RequestBody Map<String, Object> map) {
        Long companyId=Long.valueOf(SecurityUtils.getLoginUserTopCompanyId());
        map.put("companyId", companyId);
        //2022-05-26新增需求，扫码单码直接解绑箱码
        if (map.get("code") == null) {
            throw new CustomException("未获取到码信息！", HttpStatus.ERROR);
        } else {
            String code = map.get("code").toString();
            String codeType = CodeRuleUtils.getCodeType(code);
            //判定单码执行解绑箱码操作
            if (AccConstants.CODE_TYPE_SINGLE.equals(codeType)) {
                codeService.deletePCodeBycode(code,companyId);
            }
        }


        List<Code> codeList = codeService.selectCodeListByCodeOrIndex(map);
        List<Map<String, Object>> res = new ArrayList<>();
        Map<String, Object> temp = new HashMap<>();
        if (codeList.size() > 0) {
            String code = codeList.get(0).getCode();
            if (!commonService.judgeStorageIsIllegalByValue(Long.valueOf(SecurityUtils.getLoginUserTopCompanyId()), Integer.valueOf(map.get("storageType").toString()), code)) {
                throw new CustomException("该码不在当前流转节点！", HttpStatus.ERROR);
            }
            for (Code codes : codeList) {
                String typeName = "未知";
                if (CodeRuleUtils.getCodeType(codes.getCode()).equals(AccConstants.CODE_TYPE_BOX)) {
                    typeName = "箱码";
                } else {
                    typeName = "单码";
                }
                codes.setCodeTypeName(typeName);
                temp = new HashMap<>();
                temp.put("codeIndex", codes.getCodeIndex());
                temp.put("companyId", codes.getCompanyId());
                temp.put("code", codes.getCode());
                temp.put("codeType", codes.getCodeType());
                temp.put("pCode", codes.getpCode());
                temp.put("codeTypeName", typeName);
                if (codes.getCodeAttr() != null) {
                    temp.put("productId", codes.getCodeAttr().getProductId());
                    temp.put("productName", codes.getCodeAttr().getProductName());
                    temp.put("productNo", codes.getCodeAttr().getProductNo());
                } else {
                    temp.put("productId", "0");
                    temp.put("productName", "");
                    temp.put("productNo", "");
                }
                res.add(temp);
            }
            return AjaxResult.success(res);
        } else {
            throw new CustomException("未查询到码数据！", HttpStatus.ERROR);
        }

    }

    /**
     * 扫码解绑
     *
     * @return
     */
    @PostMapping("unBindCodes")
    public AjaxResult unBindCodes(@RequestBody Map<String, Object> map) {
        return AjaxResult.success(codeService.unBindCodes(map));
    }
    /**
     * 接口解绑
     *
     * @return
     */
    @PostMapping("unBindCodesByPCodes")
    public AjaxResult unBindCodesByPCodes(@RequestBody Map<String, Object> map) {
        return AjaxResult.success(codeService.unBindCodesByPCodes(map));
    }

    /**
     * 对接CRM开放接口
     *
     * @return
     */
    @GetMapping("getCRMInfo")
    public AjaxResult getCRMInfo(@RequestBody Map<String, Object> map) {
        Date beginTime = null;
        Date endTime = null;
        try {
            beginTime = DateUtils.parseDate(map.get("beginTime").toString());
            endTime = DateUtils.parseDate(map.get("endTime").toString());
            if (map.get("dayFlag") == null) {
                String str = DateUtils.getDatePoor(endTime, beginTime);
                Integer dayNum = Integer.parseInt(str.substring(0, str.lastIndexOf("天")));
                if (dayNum > 32) {
                    throw new CustomException("时间范围限制31天，请输入重新输入！", HttpStatus.ERROR);
                }
            }
        } catch (Exception e) {
            throw new CustomException("请输入正确时间范围！", HttpStatus.ERROR);
        }
        List<CRMInfoVo> crmInfo = codeService.getCRMInfo(preFixUrl, beginTime, endTime);
        if (crmInfo.size() > 0) {
            return AjaxResult.success(crmInfo);
        } else {
            throw new CustomException("该时间段无相关数据！", HttpStatus.ERROR);
        }
    }

    /**
     * 获取所有码
     *
     * @return 获取所有码
     */
    @PostMapping("getQualityCodes")
    public AjaxResult getQualityCodes(@RequestBody Map<String, Object> map) {
        Long companyId=Long.valueOf(SecurityUtils.getLoginUserTopCompanyId());
        map.put("companyId", companyId);
        //2022-05-26新增需求，扫码单码直接解绑箱码
        if (map.get("code") == null) {
            throw new CustomException("未获取到码信息！", HttpStatus.ERROR);
        }
        List<Code> codeList = codeService.selectCodeListByCodeOrIndex(map);
        List<Map<String, Object>> res = new ArrayList<>();
        Map<String, Object> temp = new HashMap<>();
        if (codeList.size() > 0) {
            for (Code codes : codeList) {
                String typeName = "未知";
                if (CodeRuleUtils.getCodeType(codes.getCode()).equals(AccConstants.CODE_TYPE_BOX)) {
                    typeName = "箱码";
                } else {
                    typeName = "单码";
                }
                codes.setCodeTypeName(typeName);
                temp = new HashMap<>();
                temp.put("codeIndex", codes.getCodeIndex());
                temp.put("companyId", codes.getCompanyId());
                temp.put("code", codes.getCode());
                temp.put("codeType", codes.getCodeType());
                temp.put("pCode", codes.getpCode());
                temp.put("codeTypeName", typeName);
                if (codes.getCodeAttr() != null) {
                    temp.put("productId", codes.getCodeAttr().getProductId());
                    temp.put("productName", codes.getCodeAttr().getProductName());
                    temp.put("productNo", codes.getCodeAttr().getProductNo());
                } else {
                    temp.put("productId", "0");
                    temp.put("productName", "");
                    temp.put("productNo", "");
                }
                res.add(temp);
            }
            return AjaxResult.success(res);
        } else {
            throw new CustomException("未查询到码数据！", HttpStatus.ERROR);
        }

    }

}
