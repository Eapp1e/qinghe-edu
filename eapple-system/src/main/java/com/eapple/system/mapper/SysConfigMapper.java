package com.eapple.system.mapper;

import java.util.List;
import com.eapple.system.domain.SysConfig;

/**
 * 鍙傛暟閰嶇疆 鏁版嵁灞?
 * 
 * @author Eapp1e
 */
public interface SysConfigMapper
{
    /**
     * 鏌ヨ鍙傛暟閰嶇疆淇℃伅
     * 
     * @param config 鍙傛暟閰嶇疆淇℃伅
     * @return 鍙傛暟閰嶇疆淇℃伅
     */
    public SysConfig selectConfig(SysConfig config);

    /**
     * 閫氳繃ID鏌ヨ閰嶇疆
     * 
     * @param configId 鍙傛暟ID
     * @return 鍙傛暟閰嶇疆淇℃伅
     */
    public SysConfig selectConfigById(Long configId);

    /**
     * 鏌ヨ鍙傛暟閰嶇疆鍒楄〃
     * 
     * @param config 鍙傛暟閰嶇疆淇℃伅
     * @return 鍙傛暟閰嶇疆闆嗗悎
     */
    public List<SysConfig> selectConfigList(SysConfig config);

    /**
     * 鏍规嵁閿悕鏌ヨ鍙傛暟閰嶇疆淇℃伅
     * 
     * @param configKey 鍙傛暟閿悕
     * @return 鍙傛暟閰嶇疆淇℃伅
     */
    public SysConfig checkConfigKeyUnique(String configKey);

    /**
     * 鏂板鍙傛暟閰嶇疆
     * 
     * @param config 鍙傛暟閰嶇疆淇℃伅
     * @return 缁撴灉
     */
    public int insertConfig(SysConfig config);

    /**
     * 淇敼鍙傛暟閰嶇疆
     * 
     * @param config 鍙傛暟閰嶇疆淇℃伅
     * @return 缁撴灉
     */
    public int updateConfig(SysConfig config);

    /**
     * 鍒犻櫎鍙傛暟閰嶇疆
     * 
     * @param configId 鍙傛暟ID
     * @return 缁撴灉
     */
    public int deleteConfigById(Long configId);

    /**
     * 鎵归噺鍒犻櫎鍙傛暟淇℃伅
     * 
     * @param configIds 闇€瑕佸垹闄ょ殑鍙傛暟ID
     * @return 缁撴灉
     */
    public int deleteConfigByIds(Long[] configIds);
}
