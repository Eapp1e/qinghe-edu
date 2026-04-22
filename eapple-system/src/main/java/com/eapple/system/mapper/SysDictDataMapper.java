package com.eapple.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.eapple.common.core.domain.entity.SysDictData;

/**
 * 字典数据数据层。
 * 
 * @author Eapp1e
 */
public interface SysDictDataMapper
{
    /**
     * 根据条件分页查询字典数据。
     * 
     * @param dictData 字典数据信息
     * @return 字典数据集合
     */
    public List<SysDictData> selectDictDataList(SysDictData dictData);

    /**
     * 根据字典类型查询字典数据。
     * 
     * @param dictType 字典类型
     * @return 字典数据集合
     */
    public List<SysDictData> selectDictDataByType(String dictType);

    /**
     * 根据字典类型和字典键值查询字典标签。
     * 
     * @param dictType 字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    public String selectDictLabel(@Param("dictType") String dictType, @Param("dictValue") String dictValue);

    /**
     * 根据字典数据 ID 查询信息。
     * 
     * @param dictCode 字典数据 ID
     * @return 字典数据
     */
    public SysDictData selectDictDataById(Long dictCode);

    /**
     * 根据字典类型统计数据数量。
     * 
     * @param dictType 字典类型
     * @return 结果
     */
    public int countDictDataByType(String dictType);

    /**
     * 通过字典数据 ID 删除字典数据。
     * 
     * @param dictCode 字典数据 ID
     * @return 结果
     */
    public int deleteDictDataById(Long dictCode);

    /**
     * 批量删除字典数据。
     * 
     * @param dictCodes 字典数据 ID 数组
     * @return 结果
     */
    public int deleteDictDataByIds(Long[] dictCodes);

    /**
     * 新增字典数据。
     * 
     * @param dictData 字典数据信息
     * @return 结果
     */
    public int insertDictData(SysDictData dictData);

    /**
     * 修改字典数据。
     * 
     * @param dictData 字典数据信息
     * @return 结果
     */
    public int updateDictData(SysDictData dictData);

    /**
     * 修改字典类型。
     * 
     * @param oldDictType 旧字典类型
     * @param newDictType 新字典类型
     * @return 结果
     */
    public int updateDictDataType(@Param("oldDictType") String oldDictType, @Param("newDictType") String newDictType);
}
