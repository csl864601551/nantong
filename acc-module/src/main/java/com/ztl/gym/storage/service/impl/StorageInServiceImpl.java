package com.ztl.gym.storage.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
//import com.ztl.gym.common.annotation.Curcompany;
//import com.ztl.gym.common.annotation.DataScope;
import com.ztl.gym.common.annotation.DataSource;
import com.ztl.gym.common.constant.AccConstants;
import com.ztl.gym.common.enums.DataSourceType;
import com.ztl.gym.common.utils.DateUtils;
import com.ztl.gym.common.utils.SecurityUtils;
import com.ztl.gym.storage.service.IStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ztl.gym.storage.mapper.StorageInMapper;
import com.ztl.gym.storage.domain.StorageIn;
import com.ztl.gym.storage.service.IStorageInService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 入库Service业务层处理
 *
 * @author ruoyi
 * @date 2021-04-09
 */
@Service
public class StorageInServiceImpl implements IStorageInService
{
    @Autowired
    private StorageInMapper storageInMapper;
    @Autowired
    private IStorageService storageService;

    /**
     * 查询入库
     *
     * @param id 入库ID
     * @return 入库
     */
    @Override
    public StorageIn selectStorageInById(Long id)
    {
        return storageInMapper.selectStorageInById(id);
    }

    /**
     * 查询入库列表
     *
     * @param storageIn 入库
     * @return 入库
     */
    @Override
    public List<StorageIn> selectStorageInList(StorageIn storageIn)
    {
        return storageInMapper.selectStorageInList(storageIn);
    }

    /**
     * 新增入库
     *
     * @param map 入库
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class})
    @DataSource(DataSourceType.SHARDING)
    public int insertStorageIn(Map<String, Object> map)
    {
        map.put("createTime",DateUtils.getNowDate());
        map.put("inTime",DateUtils.getNowDate());
        map.put("createUser",SecurityUtils.getLoginUser().getUser().getUserId());
        int result=storageInMapper.insertStorageIn(map);//新增t_storage_in入库表
        Long id=Long.valueOf(map.get("id").toString());
        //storageService.addCodeFlow(AccConstants.STORAGE_TYPE_IN, id ,map.get("code").toString());//转移到PDA执行
        //storageInMapper.updateProductStock(map);//TODO 更新t_product_stock库存统计表
        return result;
    }

    /**
     * 修改入库
     *
     * @param storageIn 入库
     * @return 结果
     */
    @Override
    public int updateStorageIn(StorageIn storageIn)
    {
        storageIn.setUpdateTime(DateUtils.getNowDate());
        return storageInMapper.updateStorageIn(storageIn);
    }

    /**
     * 批量删除入库
     *
     * @param ids 需要删除的入库ID
     * @return 结果
     */
    @Override
    public int deleteStorageInByIds(Long[] ids)
    {
        return storageInMapper.deleteStorageInByIds(ids);
    }

    /**
     * 删除入库信息
     *
     * @param id 入库ID
     * @return 结果
     */
    @Override
    public int deleteStorageInById(Long id)
    {
        return storageInMapper.deleteStorageInById(id);
    }

    @Override
    public Map<String, Object> getCodeInfo(String code) {
        Map<String, Object> map=new HashMap<>();
        map=storageInMapper.getCodeInfo(code);//获取码产品信息
        List<Map<String, Object>> listMap=storageInMapper.getCodeDetail(code);//获取码产品明细
        map.put("listMap",listMap);
        return map;
    }
}
