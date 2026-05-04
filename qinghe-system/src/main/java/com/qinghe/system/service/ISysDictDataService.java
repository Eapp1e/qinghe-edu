package com.qinghe.system.service;

import java.util.List;
import com.qinghe.common.core.domain.entity.SysDictData;

/**
 * 字典数据服务接口
 * 
 * @author Eapp1e
 */
public interface ISysDictDataService
{
    /**
     * 根据条件分页查询字典数据
     * 
     * @param dictData 字典数据信息
     * @return 字典数据集合
     */
    public List<SysDictData> selectDictDataList(SysDictData dictData);

    /**
     * 根据字典类型和值查询字典标签
     * 
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return 字典标签
     */
    public String selectDictLabel(String dictType, String dictValue);

    /**
     * 根据字典数据 ID 查询信息
     * 
     * @param dictCode 字典数据 ID
     * @return 字典数据
     */
    public SysDictData selectDictDataById(Long dictCode);

    /**
     * 批量删除字典数据信息
     * 
     * @param dictCodes 需要删除的字典数据 ID
     */
    public void deleteDictDataByIds(Long[] dictCodes);

    /**
     * 新增字典数据信息
     * 
     * @param dictData 字典数据信息
     * @return 结果
     */
    public int insertDictData(SysDictData dictData);

    /**
     * 修改字典数据信息
     * 
     * @param dictData 字典数据信息
     * @return 结果
     */
    public int updateDictData(SysDictData dictData);
}
