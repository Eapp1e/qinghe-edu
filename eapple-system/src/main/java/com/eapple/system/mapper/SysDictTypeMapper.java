package com.eapple.system.mapper;

import java.util.List;
import com.eapple.common.core.domain.entity.SysDictType;

/**
 * 字典类型数据层。
 * 
 * @author Eapp1e
 */
public interface SysDictTypeMapper
{
    /**
     * 根据条件分页查询字典类型。
     * 
     * @param dictType 字典类型信息
     * @return 字典类型集合
     */
    public List<SysDictType> selectDictTypeList(SysDictType dictType);

    /**
     * 查询所有字典类型。
     * 
     * @return 字典类型集合
     */
    public List<SysDictType> selectDictTypeAll();

    /**
     * 根据字典类型 ID 查询信息。
     * 
     * @param dictId 字典类型 ID
     * @return 字典类型信息
     */
    public SysDictType selectDictTypeById(Long dictId);

    /**
     * 根据字典类型查询信息。
     * 
     * @param dictType 字典类型
     * @return 字典类型信息
     */
    public SysDictType selectDictTypeByType(String dictType);

    /**
     * 通过字典类型 ID 删除字典类型。
     * 
     * @param dictId 字典类型 ID
     * @return 结果
     */
    public int deleteDictTypeById(Long dictId);

    /**
     * 批量删除字典类型。
     * 
     * @param dictIds 字典类型 ID 数组
     * @return 结果
     */
    public int deleteDictTypeByIds(Long[] dictIds);

    /**
     * 新增字典类型信息。
     * 
     * @param dictType 字典类型信息
     * @return 结果
     */
    public int insertDictType(SysDictType dictType);

    /**
     * 修改字典类型信息。
     * 
     * @param dictType 字典类型信息
     * @return 结果
     */
    public int updateDictType(SysDictType dictType);

    /**
     * 校验字典类型是否唯一。
     * 
     * @param dictType 字典类型
     * @return 结果
     */
    public SysDictType checkDictTypeUnique(String dictType);
}
