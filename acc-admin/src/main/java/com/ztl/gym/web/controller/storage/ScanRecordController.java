package com.ztl.gym.web.controller.storage;

import cn.hutool.core.util.StrUtil;
import com.ztl.gym.area.domain.CompanyArea;
import com.ztl.gym.code.domain.Code;
import com.ztl.gym.code.domain.CodeRecord;
import com.ztl.gym.code.service.ICodeRecordService;
import com.ztl.gym.code.service.ICodeService;
import com.ztl.gym.common.annotation.Log;
import com.ztl.gym.common.core.controller.BaseController;
import com.ztl.gym.common.core.domain.AjaxResult;
import com.ztl.gym.common.core.page.TableDataInfo;
import com.ztl.gym.common.enums.BusinessType;
import com.ztl.gym.common.utils.CodeRuleUtils;
import com.ztl.gym.common.utils.poi.ExcelUtil;
import com.ztl.gym.product.domain.Product;
import com.ztl.gym.product.service.IProductService;
import com.ztl.gym.storage.domain.ScanRecord;
import com.ztl.gym.storage.service.IScanRecordService;
import com.ztl.gym.storage.service.IStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 扫码记录Controller
 *
 * @author ruoyi
 * @date 2021-04-28
 */
@RestController
@RequestMapping("/storage/record")
public class ScanRecordController extends BaseController {
    @Autowired
    private IScanRecordService scanRecordService;
    @Autowired
    private IStorageService storageService;
    @Autowired
    private ICodeService codeService;
    @Autowired
    private ICodeRecordService codeRecordService;
    @Autowired
    private IProductService tProductService;

    /**
     * 查询扫码记录列表
     */
    @PreAuthorize("@ss.hasPermi('storage:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScanRecord scanRecord) {
        startPage();
        List<ScanRecord> list = scanRecordService.selectScanRecordList(scanRecord);
        return getDataTable(list);
    }

    /**
     * 导出扫码记录列表
     */
    @Log(title = "扫码记录", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(ScanRecord scanRecord) {
        List<ScanRecord> list = scanRecordService.selectScanRecordList(scanRecord);
        ExcelUtil<ScanRecord> util = new ExcelUtil<ScanRecord>(ScanRecord.class);
        return util.exportExcel(list, "record");
    }

    /**
     * 获取扫码记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('storage:record:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(scanRecordService.selectScanRecordById(id));
    }

    /**
     * 新增扫码记录
     */
    @Log(title = "扫码记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ScanRecord scanRecord) {
        return toAjax(scanRecordService.insertScanRecord(scanRecord));
    }

    /**
     * 修改扫码记录
     */
    @Log(title = "扫码记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ScanRecord scanRecord) {
        return toAjax(scanRecordService.updateScanRecord(scanRecord));
    }

    /**
     * 删除扫码记录
     */
    @Log(title = "扫码记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(scanRecordService.deleteScanRecordByIds(ids));
    }

    /**
     * 根据码号查询相关产品和码信息
     */
    @GetMapping(value = "/getScanRecordByCode")
    public AjaxResult getScanRecordByCode(@RequestParam("code") String code) {
        return AjaxResult.success(scanRecordService.getScanRecordByCode(CodeRuleUtils.getCompanyIdByCode(code.trim()), code.trim()));
    }


    /**
     * 判断是否窜货
     */
    @GetMapping("/getIsMixInfo")
    public AjaxResult getIsMixInfo(CompanyArea area) {
        CompanyArea res = scanRecordService.getIsMixInfo(area);
        return AjaxResult.success(res);
    }

    /**
     * 根据码号查询相关产品和码信息
     */
    @GetMapping(value = "/cxspxqBycode")
    public AjaxResult cxspxqBycode(@RequestParam("code") String code) {
        String temp ="";
        if(StrUtil.isNotEmpty(code)){
            long companyId = CodeRuleUtils.getCompanyIdByCode(code.trim());
            Code codequery = new Code();
            codequery.setCompanyId(companyId);
            codequery.setCode(code.trim());
            Code codeEntity = codeService.selectCode(codequery);
            if(codeEntity!=null){
                long codeIndex = codeEntity.getCodeIndex();
                CodeRecord codeRecord = codeRecordService.selectCodeRecordByIndex(codeIndex);
                if(codeRecord!=null){
                    long productId = codeRecord.getProductId();
                    Product product = tProductService.selectTProductById(productId);
                    if(product!=null){
                        String productDetailPc = product.getProductDetailPc();
                        if(StrUtil.isNotEmpty(productDetailPc)){
                            temp = productDetailPc;
                        }
                    }
                }
            }
        }
        return AjaxResult.success(temp);
    }
}
