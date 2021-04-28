package com.ztl.gym.storage.mapper;

import java.util.List;
import com.ztl.gym.storage.domain.ScanRecord;

/**
 * 扫码记录Mapper接口
 * 
 * @author ruoyi
 * @date 2021-04-28
 */
public interface ScanRecordMapper 
{
    /**
     * 查询扫码记录
     * 
     * @param id 扫码记录ID
     * @return 扫码记录
     */
    public ScanRecord selectScanRecordById(Long id);

    /**
     * 查询扫码记录列表
     * 
     * @param scanRecord 扫码记录
     * @return 扫码记录集合
     */
    public List<ScanRecord> selectScanRecordList(ScanRecord scanRecord);

    /**
     * 新增扫码记录
     * 
     * @param scanRecord 扫码记录
     * @return 结果
     */
    public int insertScanRecord(ScanRecord scanRecord);

    /**
     * 修改扫码记录
     * 
     * @param scanRecord 扫码记录
     * @return 结果
     */
    public int updateScanRecord(ScanRecord scanRecord);

    /**
     * 删除扫码记录
     * 
     * @param id 扫码记录ID
     * @return 结果
     */
    public int deleteScanRecordById(Long id);

    /**
     * 批量删除扫码记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteScanRecordByIds(Long[] ids);
}