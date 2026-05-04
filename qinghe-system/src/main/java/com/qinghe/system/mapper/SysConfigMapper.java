package com.qinghe.system.mapper;

import java.util.List;
import com.qinghe.system.domain.SysConfig;

/**
 * 参数配置数据层。
 * 
 * @author Eapp1e
 */
public interface SysConfigMapper
{
    /**
     * 查询参数配置信息。
     * 
     * @param config 参数配置对象
     * @return 参数配置信息
     */
    public SysConfig selectConfig(SysConfig config);

    /**
     * 根据 ID 查询参数配置。
     * 
     * @param configId 参数配置 ID
     * @return 参数配置对象
     */
    public SysConfig selectConfigById(Long configId);

    /**
     * 查询参数配置列表。
     * 
     * @param config 参数配置对象
     * @return 参数配置集合
     */
    public List<SysConfig> selectConfigList(SysConfig config);

    /**
     * 根据参数键名查询配置。
     * 
     * @param configKey 参数键名
     * @return 参数配置对象
     */
    public SysConfig checkConfigKeyUnique(String configKey);

    /**
     * 新增参数配置。
     * 
     * @param config 参数配置对象
     * @return 结果
     */
    public int insertConfig(SysConfig config);

    /**
     * 修改参数配置。
     * 
     * @param config 参数配置对象
     * @return 结果
     */
    public int updateConfig(SysConfig config);

    /**
     * 根据 ID 删除参数配置。
     * 
     * @param configId 参数配置 ID
     * @return 结果
     */
    public int deleteConfigById(Long configId);

    /**
     * 批量删除参数配置。
     * 
     * @param configIds 参数配置 ID 数组
     * @return 结果
     */
    public int deleteConfigByIds(Long[] configIds);
}
