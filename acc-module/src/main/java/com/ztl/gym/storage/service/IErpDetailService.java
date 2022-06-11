package com.ztl.gym.storage.service;

import java.util.List;
import com.ztl.gym.storage.domain.ErpDetail;

/**
 * 对接ERP明细Service接口
 *
 * @author ruoyi
 * @date 2022-06-11
 */
public interface IErpDetailService
{
    /**
     * 查询对接ERP明细
     *
     * @param id 对接ERP明细ID
     * @return 对接ERP明细
     */
    public ErpDetail selectErpDetailById(Long id);

    /**
     * 查询对接ERP明细列表
     *
     * @param erpDetail 对接ERP明细
     * @return 对接ERP明细集合
     */
    public List<ErpDetail> selectErpDetailList(ErpDetail erpDetail);

    /**
     * 新增对接ERP明细
     *
     * @param erpDetail 对接ERP明细
     * @return 结果
     */
    public int insertErpDetail(ErpDetail erpDetail);

    /**
     * 修改对接ERP明细
     *
     * @param erpDetail 对接ERP明细
     * @return 结果
     */
    public int updateErpDetail(ErpDetail erpDetail);

    /**
     * 批量删除对接ERP明细
     *
     * @param ids 需要删除的对接ERP明细ID
     * @return 结果
     */
    public int deleteErpDetailByIds(Long[] ids);

    /**
     * 删除对接ERP明细信息
     *
     * @param id 对接ERP明细ID
     * @return 结果
     */
    public int deleteErpDetailById(Long id);
}
