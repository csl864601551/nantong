package com.ztl.gym.web.controller.mix;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ztl.gym.common.annotation.Log;
import com.ztl.gym.common.core.controller.BaseController;
import com.ztl.gym.common.core.domain.AjaxResult;
import com.ztl.gym.common.enums.BusinessType;
import com.ztl.gym.mix.domain.MixRecord;
import com.ztl.gym.mix.service.IMixRecordService;
import com.ztl.gym.common.utils.poi.ExcelUtil;
import com.ztl.gym.common.core.page.TableDataInfo;

/**
 * 窜货记录Controller
 *
 * @author ruoyi
 * @date 2021-04-28
 */
@RestController
@RequestMapping("/mix/record")
public class MixRecordController extends BaseController
{
    @Autowired
    private IMixRecordService mixRecordService;

    /**
     * 查询窜货记录列表
     */
    @PreAuthorize("@ss.hasPermi('product:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(MixRecord mixRecord)
    {
        startPage();
        List<MixRecord> list = mixRecordService.selectMixRecordList(mixRecord);
        return getDataTable(list);
    }

    /**
     * 导出窜货记录列表
     */
    @Log(title = "窜货记录", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(MixRecord mixRecord)
    {
        List<MixRecord> list = mixRecordService.selectMixRecordList(mixRecord);
        ExcelUtil<MixRecord> util = new ExcelUtil<MixRecord>(MixRecord.class);
        return util.exportExcel(list, "record");
    }

    /**
     * 获取窜货记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('product:record:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(mixRecordService.selectMixRecordById(id));
    }

    /**
     * 新增窜货记录
     */
    @Log(title = "窜货记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MixRecord mixRecord)
    {
        return toAjax(mixRecordService.insertMixRecord(mixRecord));
    }

    /**
     * 修改窜货记录
     */
    @Log(title = "窜货记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MixRecord mixRecord)
    {
        return toAjax(mixRecordService.updateMixRecord(mixRecord));
    }

    /**
     * 删除窜货记录
     */
    @Log(title = "窜货记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(mixRecordService.deleteMixRecordByIds(ids));
    }
}