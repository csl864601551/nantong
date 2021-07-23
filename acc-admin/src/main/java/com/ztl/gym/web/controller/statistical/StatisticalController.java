package com.ztl.gym.web.controller.statistical;

import cn.hutool.core.date.DateUtil;
import com.ztl.gym.code.service.ICodeRecordService;
import com.ztl.gym.common.constant.AccConstants;
import com.ztl.gym.common.core.domain.AjaxResult;
import com.ztl.gym.common.utils.SecurityUtils;
import com.ztl.gym.mix.service.IMixRecordService;
import com.ztl.gym.product.service.IProductService;
import com.ztl.gym.storage.domain.ScanRecord;
import com.ztl.gym.storage.service.IScanRecordService;
import com.ztl.gym.storage.service.IStorageOutService;
import com.ztl.gym.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author shenz
 * @create 2021-07-10 9:17
 */
@RestController
@RequestMapping("/statistical/statistical")
public class StatisticalController {


    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private IStorageOutService storageOutService;

    @Autowired
    private IMixRecordService mixRecordService;

    @Autowired
    private ICodeRecordService codeRecordService;

    @Autowired
    private IProductService tProductService;

    @Autowired
    private IScanRecordService scanRecordService;

    /**
     * 查询首页信息
     */
    @PostMapping("/indexSj")
    public AjaxResult selectIndexSj(@RequestBody Map<String, Object> map) {
        try {
            boolean isadmin = false;
            Long topdeptId = 0L;
            //获取用户部门信息
            Long deptId = SecurityUtils.getLoginUserCompany().getDeptId();
            if (deptId.equals(AccConstants.ADMIN_DEPT_ID) || deptId.equals(AccConstants.XTADMIN_DEPT_ID) || deptId > 200) {
                isadmin = true;
            } else {
                topdeptId = SecurityUtils.getLoginUserTopCompanyId();
                isadmin = false;
            }
            Map<String, Object> query = new HashMap<String, Object>();
            //平台管理员
            int qyNum = 0;
            int jxsNum = 0;
            int cpchlNum = 0;
            int chzlNum = 0;
            int smzlNum = 0;
            int ljczfyNum = 0;
            //产品总览
            int yxjNum = 0;
            int ysjNum = 0;
            int kcjzNum = 0;
            int qbcpNum = 0;
            //经销商总览
            int jrxzNum = 0;
            int zrxzNum = 0;
            int byxzNum = 0;
            int jxszs = 0;
            //扫码数据
            int jrsmNum = 0;
            int ljsmNum = 0;
            int jrsmZl = 0;
            int ljsmZl = 0;
            int jrcyNum = 0;
            int ljcyNum = 0;

            //平台方
            if (isadmin) {
                //企业总数
                query.put("type", 5);
                query.put("topdeptId", AccConstants.ADMIN_DEPT_ID);
                qyNum = deptService.selectCountBydept(query);
                //产品出货总量
                cpchlNum = storageOutService.selectCountByDept(query);
                //窜货总量
                chzlNum = mixRecordService.selectmixnum(query);
                //生码总量
                smzlNum = codeRecordService.selectcodenum(query);
                //累计充值费用

                //产品总览
                //已下架
                query.put("type", 1);
                yxjNum = tProductService.selectProductNum(query);
                //已上架
                query.put("type", 2);
                ysjNum = tProductService.selectProductNum(query);
                //库存紧张
                query.put("type", 3);
                kcjzNum = tProductService.selectProductNum(query);
                //全部产品
                query.put("type", 4);
                qbcpNum = tProductService.selectProductNum(query);

                //经销商总览
                //今日新增
                Date today = DateUtil.date();
                Date beginTime = DateUtil.beginOfDay(today);
                Date endTime = DateUtil.endOfDay(today);
                query.put("type", 1);
                query.put("beginTime", beginTime);
                query.put("endTime", endTime);
                jrxzNum = deptService.selectCountBydept(query);
                //昨日新增
                Date yesterday = DateUtil.yesterday();
                beginTime = DateUtil.beginOfDay(yesterday);
                endTime = DateUtil.endOfDay(yesterday);
                query.put("type", 2);
                query.put("beginTime", beginTime);
                query.put("endTime", endTime);
                zrxzNum = deptService.selectCountBydept(query);
                //本月新增
                beginTime = DateUtil.beginOfMonth(today);
                endTime = DateUtil.endOfMonth(today);
                query.put("type", 3);
                query.put("beginTime", beginTime);
                query.put("endTime", endTime);
                byxzNum = deptService.selectCountBydept(query);
                //经销商总数
                query.put("type", 3);
                jxszs = deptService.selectCountBydept(query);

            } else {
                query.put("deptId", deptId);
                //经销商总数
                query.put("type", 3);
                jxsNum = deptService.selectCountBydept(query);
                //产品出货总数
                cpchlNum = storageOutService.selectCountByDept(query);
                //窜货总数
                chzlNum = mixRecordService.selectmixnum(query);
                //剩余码量
                //已经使用的码量
                smzlNum = codeRecordService.selectcodenum(query);
                //剩余费用

                //产品总览
                //已下架
                query.put("type", 1);
                yxjNum = tProductService.selectProductNum(query);
                //已上架
                query.put("type", 2);
                ysjNum = tProductService.selectProductNum(query);
                //库存紧张
                query.put("type", 3);
                kcjzNum = tProductService.selectProductNum(query);
                //全部产品
                query.put("type", 4);
                qbcpNum = tProductService.selectProductNum(query);

                //经销商总览
                //今日新增
                Date today = DateUtil.date();
                Date beginTime = DateUtil.beginOfDay(today);
                Date endTime = DateUtil.endOfDay(today);
                query.put("type", 1);
                query.put("beginTime", beginTime);
                query.put("endTime", endTime);
                jrxzNum = deptService.selectCountBydept(query);
                //昨日新增
                Date yesterday = DateUtil.yesterday();
                beginTime = DateUtil.beginOfDay(yesterday);
                endTime = DateUtil.endOfDay(yesterday);
                query.put("type", 2);
                query.put("beginTime", beginTime);
                query.put("endTime", endTime);
                zrxzNum = deptService.selectCountBydept(query);
                //本月新增
                beginTime = DateUtil.beginOfMonth(today);
                endTime = DateUtil.endOfMonth(today);
                query.put("type", 3);
                query.put("beginTime", beginTime);
                query.put("endTime", endTime);
                byxzNum = deptService.selectCountBydept(query);
                //经销商总数
                query.put("type", 3);
                jxszs = deptService.selectCountBydept(query);
            }
            AjaxResult ajax = AjaxResult.success();
            ajax.put("qyNum", qyNum);
            ajax.put("jxsNum", jxsNum);
            ajax.put("cpchlNum", cpchlNum);
            ajax.put("chzlNum", chzlNum);
            ajax.put("smzlNum", smzlNum);
            ajax.put("ljczfyNum", ljczfyNum);
            ajax.put("yxjNum", yxjNum);
            ajax.put("ysjNum", ysjNum);
            ajax.put("kcjzNum", kcjzNum);
            ajax.put("qbcpNum", qbcpNum);
            ajax.put("jrxzNum", jrxzNum);
            ajax.put("zrxzNum", zrxzNum);
            ajax.put("byxzNum", byxzNum);
            ajax.put("jxszs", jxszs);
            return ajax;
        } catch (Exception e) {
            AjaxResult ajax = AjaxResult.error("查询信息错误！！！");
            return ajax;
        }
    }


    /**
     * 查询出货数量信息
     */
    @PostMapping("/selectOutNum")
    public AjaxResult selectOutNum(@RequestBody Map<String, Object> map) {
        try {
            boolean isadmin = false;
            //获取用户部门信息
            Long deptId = SecurityUtils.getLoginUserCompany().getDeptId();
            if (deptId.equals(AccConstants.ADMIN_DEPT_ID) || deptId.equals(AccConstants.XTADMIN_DEPT_ID) || deptId > 200) {
                isadmin = true;
            } else {
                isadmin = false;
            }
            Map<String, Object> query = new HashMap<String, Object>();
            //本月出货总数
            int bychzsNum = 0;
            //同比上月
            int sgyNum = 0;
            //差额
            double tbsy = 0l;
            //本周出货总数
            int bzchslNum = 0;
            //同比上周
            int tbszNum = 0;
            //差额
            double tbsz = 0l;
            //折线图数据
            List<Object> Xlist = new  ArrayList<Object>();
            String xCode = null;
            List<Object> Ylist = new  ArrayList<Object>();
            int yNum =0;

            //平台方
            if (isadmin) {
                //本月出货总数
                Date today = DateUtil.date();
                Date beginTime = DateUtil.beginOfMonth(today);
                Date endTime = DateUtil.endOfMonth(today);
                query.put("type", 1);
                query.put("beginTime", beginTime);
                query.put("endTime", endTime);
                bychzsNum = storageOutService.selectCountByDept(query);
                //同比上月
                Date lastMonth =DateUtil.lastMonth();
                beginTime = DateUtil.beginOfMonth(lastMonth);
                endTime = DateUtil.endOfMonth(lastMonth);
                query.put("beginTime", beginTime);
                query.put("endTime", endTime);
                sgyNum = storageOutService.selectCountByDept(query);
                if(sgyNum>0){
                    tbsy = new BigDecimal((float)bychzsNum/sgyNum).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                }else{
                    tbsy = 100l;
                }
                //本周出货总数
                beginTime = DateUtil.beginOfWeek(today);
                endTime = DateUtil.endOfWeek(today);
                query.put("beginTime", beginTime);
                query.put("endTime", endTime);
                bzchslNum = storageOutService.selectCountByDept(query);
                //同比上周
                Date lastWeek =DateUtil.lastWeek();
                beginTime = DateUtil.beginOfWeek(lastWeek);
                endTime = DateUtil.endOfWeek(lastWeek);
                query.put("beginTime", beginTime);
                query.put("endTime", endTime);
                tbszNum = storageOutService.selectCountByDept(query);
                if(tbszNum>0){
                    tbsz = new BigDecimal((float)bzchslNum/tbszNum).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                }else{
                    tbsz =100l;
                }
                //统计图
                if(map.get("tjType")!=null){
                    //本周
                    if(map.get("tjType").equals("1")){
                        query.put("type","1");
                        List<Map<String,Object>> weekList = storageOutService.selectCountByWeek(query);
                        if(weekList.size()>0){
                            for(int i=0;i<weekList.size();i++){
                                Map<String,Object> weekmap = new HashMap<String,Object>();
                                weekmap = weekList.get(i);
                                if(weekmap.get("num")==null){
                                    Ylist.add(0);
                                    Xlist.add(weekmap.get("date"));
                                }else{
                                    Ylist.add(weekmap.get("num"));
                                    Xlist.add(weekmap.get("date"));
                                }
                            }
                        }
                        System.out.println("Xlist=="+Xlist+"---------"+"Ylist=="+Ylist);
                    }else if(map.get("tjType").equals("2")){

                    }else if(map.get("tjType").equals("3")){

                    }
                }

            } else {
                query.put("deptId", deptId);
                //本月出货总数
                Date today = DateUtil.date();
                Date beginTime = DateUtil.beginOfMonth(today);
                Date endTime = DateUtil.endOfMonth(today);
                query.put("type", 1);
                query.put("beginTime", beginTime);
                query.put("endTime", endTime);
                bychzsNum = storageOutService.selectCountByDept(query);
                //同比上月
                Date lastMonth =DateUtil.lastMonth();
                beginTime = DateUtil.beginOfMonth(lastMonth);
                endTime = DateUtil.endOfMonth(lastMonth);
                query.put("beginTime", beginTime);
                query.put("endTime", endTime);
                sgyNum = storageOutService.selectCountByDept(query);
                if(sgyNum>0){
                    tbsy = new BigDecimal((float)bychzsNum/sgyNum).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                }else{
                    tbsy = 100l;
                }
                //本周出货总数
                beginTime = DateUtil.beginOfWeek(today);
                endTime = DateUtil.endOfWeek(today);
                query.put("beginTime", beginTime);
                query.put("endTime", endTime);
                bzchslNum = storageOutService.selectCountByDept(query);
                //同比上周
                Date lastWeek =DateUtil.lastWeek();
                beginTime = DateUtil.beginOfWeek(lastWeek);
                endTime = DateUtil.endOfWeek(lastWeek);
                query.put("beginTime", beginTime);
                query.put("endTime", endTime);
                tbszNum = storageOutService.selectCountByDept(query);
                if(tbszNum>0){
                    tbsz = new BigDecimal((float)bzchslNum/tbszNum).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                }else{
                    tbsz =100l;
                }

                //统计图
                if(map.get("tjType")!=null){
                    //本周
                    if(map.get("tjType").equals("1")){
                        query.put("type","1");
                        List<Map<String,Object>> weekList = storageOutService.selectCountByWeek(query);
                        if(weekList.size()>0){
                            for(int i=0;i<weekList.size();i++){
                                Map<String,Object> weekmap = new HashMap<String,Object>();
                                weekmap = weekList.get(i);
                                if(weekmap.get("num")==null){
                                    Ylist.add(0);
                                    Xlist.add(weekmap.get("date"));
                                }else{
                                    Ylist.add(weekmap.get("num"));
                                    Xlist.add(weekmap.get("date"));
                                }
                            }
                        }
                        System.out.println("Xlist=="+Xlist+"---------"+"Ylist=="+Ylist);
                    }else if(map.get("tjType").equals("2")){

                    }else if(map.get("tjType").equals("3")){

                    }
                }

            }
            AjaxResult ajax = AjaxResult.success();
            ajax.put("bychzsNum",bychzsNum);
            ajax.put("sgyNum",sgyNum);
            ajax.put("tbsy",tbsy);
            ajax.put("bzchslNum",bzchslNum);
            ajax.put("tbszNum",tbszNum);
            ajax.put("tbsz",tbsz);
            ajax.put("Xlist",Xlist);
            ajax.put("Ylist",Ylist);
            return ajax;
        } catch (Exception e) {
            AjaxResult ajax = AjaxResult.error("查询信息错误！！！");
            return ajax;
        }
    }




    /**
     * 查询统计趋势
     */
    @PostMapping("/selectTjqsNum")
    public AjaxResult selectTjqsNum(@RequestBody Map<String, Object> map) {
        try {
            boolean isadmin = false;
            //获取用户部门信息
            Long deptId = SecurityUtils.getLoginUserCompany().getDeptId();
            if (deptId.equals(AccConstants.ADMIN_DEPT_ID) || deptId.equals(AccConstants.XTADMIN_DEPT_ID) || deptId > 200) {
                isadmin = true;
            } else {
                isadmin = false;
            }
            Map<String, Object> query = new HashMap<String, Object>();
            //本月出货总数
            int bychzsNum = 0;
            //同比上月
            int sgyNum = 0;
            //差额
            double tbsy = 0l;
            //本周出货总数
            int bzchslNum = 0;
            //同比上周
            int tbszNum = 0;
            //差额
            double tbsz = 0l;
            //折线图数据
            List<Object> Xlist = new  ArrayList<Object>();
            String xCode = null;
            List<Object> Ylist = new  ArrayList<Object>();
            int yNum =0;

            //平台方
            if (isadmin) {
                //本月出货总数
                Date today = DateUtil.date();
                Date beginTime = DateUtil.beginOfMonth(today);
                Date endTime = DateUtil.endOfMonth(today);
                query.put("type", 1);
                query.put("beginTime", beginTime);
                query.put("endTime", endTime);
                bychzsNum = storageOutService.selectCountByDept(query);
                //同比上月
                Date lastMonth =DateUtil.lastMonth();
                beginTime = DateUtil.beginOfMonth(lastMonth);
                endTime = DateUtil.endOfMonth(lastMonth);
                query.put("beginTime", beginTime);
                query.put("endTime", endTime);
                sgyNum = storageOutService.selectCountByDept(query);
                if(sgyNum>0){
                    tbsy = new BigDecimal((float)bychzsNum/sgyNum).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                }else{
                    tbsy = 100l;
                }
                //本周出货总数
                beginTime = DateUtil.beginOfWeek(today);
                endTime = DateUtil.endOfWeek(today);
                query.put("beginTime", beginTime);
                query.put("endTime", endTime);
                bzchslNum = storageOutService.selectCountByDept(query);
                //同比上周
                Date lastWeek =DateUtil.lastWeek();
                beginTime = DateUtil.beginOfWeek(lastWeek);
                endTime = DateUtil.endOfWeek(lastWeek);
                query.put("beginTime", beginTime);
                query.put("endTime", endTime);
                tbszNum = storageOutService.selectCountByDept(query);
                if(tbszNum>0){
                    tbsz = new BigDecimal((float)bzchslNum/tbszNum).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                }else{
                    tbsz =100l;
                }
                //统计图
                if(map.get("tjType")!=null){
                    //本周
                    if(map.get("tjType").equals("1")){
                        query.put("type","1");
                        List<Map<String,Object>> weekList = storageOutService.selectCountByWeek(query);
                        if(weekList.size()>0){
                            for(int i=0;i<weekList.size();i++){
                                Map<String,Object> weekmap = new HashMap<String,Object>();
                                weekmap = weekList.get(i);
                                if(weekmap.get("num")==null){
                                    Ylist.add(0);
                                    Xlist.add(weekmap.get("date"));
                                }else{
                                    Ylist.add(weekmap.get("num"));
                                    Xlist.add(weekmap.get("date"));
                                }
                            }
                        }
                        System.out.println("Xlist=="+Xlist+"---------"+"Ylist=="+Ylist);
                    }else if(map.get("tjType").equals("2")){

                    }else if(map.get("tjType").equals("3")){

                    }
                }

            } else {
                query.put("deptId", deptId);
                //本月出货总数
                Date today = DateUtil.date();
                Date beginTime = DateUtil.beginOfMonth(today);
                Date endTime = DateUtil.endOfMonth(today);
                query.put("type", 1);
                query.put("beginTime", beginTime);
                query.put("endTime", endTime);
                bychzsNum = storageOutService.selectCountByDept(query);
                //同比上月
                Date lastMonth =DateUtil.lastMonth();
                beginTime = DateUtil.beginOfMonth(lastMonth);
                endTime = DateUtil.endOfMonth(lastMonth);
                query.put("beginTime", beginTime);
                query.put("endTime", endTime);
                sgyNum = storageOutService.selectCountByDept(query);
                if(sgyNum>0){
                    tbsy = new BigDecimal((float)bychzsNum/sgyNum).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                }else{
                    tbsy = 100l;
                }
                //本周出货总数
                beginTime = DateUtil.beginOfWeek(today);
                endTime = DateUtil.endOfWeek(today);
                query.put("beginTime", beginTime);
                query.put("endTime", endTime);
                bzchslNum = storageOutService.selectCountByDept(query);
                //同比上周
                Date lastWeek =DateUtil.lastWeek();
                beginTime = DateUtil.beginOfWeek(lastWeek);
                endTime = DateUtil.endOfWeek(lastWeek);
                query.put("beginTime", beginTime);
                query.put("endTime", endTime);
                tbszNum = storageOutService.selectCountByDept(query);
                if(tbszNum>0){
                    tbsz = new BigDecimal((float)bzchslNum/tbszNum).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                }else{
                    tbsz =100l;
                }

                //统计图
                if(map.get("tjType")!=null){
                    //本周
                    if(map.get("tjType").equals("1")){
                        query.put("type","1");
                        List<Map<String,Object>> weekList = storageOutService.selectCountByWeek(query);
                        if(weekList.size()>0){
                            for(int i=0;i<weekList.size();i++){
                                Map<String,Object> weekmap = new HashMap<String,Object>();
                                weekmap = weekList.get(i);
                                if(weekmap.get("num")==null){
                                    Ylist.add(0);
                                    Xlist.add(weekmap.get("date"));
                                }else{
                                    Ylist.add(weekmap.get("num"));
                                    Xlist.add(weekmap.get("date"));
                                }
                            }
                        }
                        System.out.println("Xlist=="+Xlist+"---------"+"Ylist=="+Ylist);
                    }else if(map.get("tjType").equals("2")){

                    }else if(map.get("tjType").equals("3")){

                    }
                }

            }
            AjaxResult ajax = AjaxResult.success();
            ajax.put("bychzsNum",bychzsNum);
            ajax.put("sgyNum",sgyNum);
            ajax.put("tbsy",tbsy);
            ajax.put("bzchslNum",bzchslNum);
            ajax.put("tbszNum",tbszNum);
            ajax.put("tbsz",tbsz);
            ajax.put("Xlist",Xlist);
            ajax.put("Ylist",Ylist);
            return ajax;
        } catch (Exception e) {
            AjaxResult ajax = AjaxResult.error("查询信息错误！！！");
            return ajax;
        }
    }


    /**
     * 热力图扫码信息
     */
    //@Log(title = "热力图扫码信息", businessType = BusinessType.INSERT)
    @PostMapping("/getRltXx")
    public AjaxResult getRltXx(@RequestBody Map<String, Object> map) {
        try {
            List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
            //查询热力图数据
            List<ScanRecord> list = scanRecordService.selectRLTList(map);
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    Map<String, Object> rltmap = new HashMap<String, Object>();
                    ScanRecord scanRecord = list.get(i);
                    rltmap.put("lng", scanRecord.getLongitude());
                    rltmap.put("lat", scanRecord.getLatitude());
                    rltmap.put("count", i);
//                    rltmap.put("code",scanRecord.getCode());
//                    rltmap.put("id",scanRecord.getId());
                    lists.add(rltmap);
                }
            }
            AjaxResult ajax = AjaxResult.success();
            ajax.put("rltjson", lists);
            return ajax;
        } catch (Exception e) {
            AjaxResult ajax = AjaxResult.error("查询信息错误！！！");
            return ajax;
        }
    }


    /**
     * 点聚合扫码信息
     */
    //@Log(title = "点聚合扫码信息", businessType = BusinessType.INSERT)
    @RequestMapping(value = "getDjhXx", method = {RequestMethod.GET, RequestMethod.POST})
    public AjaxResult getDjhXx(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
            Map<String, Object> map = new HashMap<String, Object>();
            //查询热力图数据
            List<ScanRecord> list = scanRecordService.selectRLTList(map);
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    Map<String, Object> rltmap = new HashMap<String, Object>();
                    List<String> list1 = new ArrayList<String>();
                    ScanRecord scanRecord = list.get(i);
                    list1.add(scanRecord.getLongitude());
                    list1.add(scanRecord.getLatitude());
                    rltmap.put("lnglat", list1);
//                    rltmap.put("code",scanRecord.getCode());
//                    rltmap.put("id",scanRecord.getId());
                    lists.add(rltmap);
                }
            }
            AjaxResult ajax = AjaxResult.success();
            ajax.put("rltjson", lists);
            return ajax;
        } catch (Exception e) {
            AjaxResult ajax = AjaxResult.error("查询信息错误！！！");
            return ajax;
        }
    }


    /**
     * 根据码号查询相关产品和码信息
     */
    @RequestMapping(value = "getDjhXxs", method = {RequestMethod.GET, RequestMethod.POST})
    public AjaxResult getDjhXxs(HttpServletRequest request, HttpServletResponse response) {
        //String code = request.getParameter("code");
        //System.out.println("扫码详情进入成功  code=="+code);
//        String compant_id = WxUtil.splitData(code,"-","-");
//        long  companyId = Integer.parseInt(compant_id)/5;
        String temp = "";
        List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
        AjaxResult ajax = AjaxResult.success();
        ajax.put("rltjson", lists);
        return ajax;
    }


}
