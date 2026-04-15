package com.eapple.system.service;

import java.util.List;
import com.eapple.system.domain.SysConfig;

/**
 * 鍙傛暟閰嶇疆 鏈嶅姟灞?
 * 
 * @author Eapp1e
 */
public interface ISysConfigService
{
    /**
     * 鏌ヨ鍙傛暟閰嶇疆淇℃伅
     * 
     * @param configId 鍙傛暟閰嶇疆ID
     * @return 鍙傛暟閰嶇疆淇℃伅
     */
    public SysConfig selectConfigById(Long configId);

    /**
     * 鏍规嵁閿悕鏌ヨ鍙傛暟閰嶇疆淇℃伅
     * 
     * @param configKey 鍙傛暟閿悕
     * @return 鍙傛暟閿€?
     */
    public String selectConfigByKey(String configKey);

    /**
     * 鑾峰彇楠岃瘉鐮佸紑鍏?
     * 
     * @return true寮€鍚紝false鍏抽棴
     */
    public boolean selectCaptchaEnabled();

    /**
     * 鏌ヨ鍙傛暟閰嶇疆鍒楄〃
     * 
     * @param config 鍙傛暟閰嶇疆淇℃伅
     * @return 鍙傛暟閰嶇疆闆嗗悎
     */
    public List<SysConfig> selectConfigList(SysConfig config);

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
     * 鎵归噺鍒犻櫎鍙傛暟淇℃伅
     * 
     * @param configIds 闇€瑕佸垹闄ょ殑鍙傛暟ID
     */
    public void deleteConfigByIds(Long[] configIds);

    /**
     * 鍔犺浇鍙傛暟缂撳瓨鏁版嵁
     */
    public void loadingConfigCache();

    /**
     * 娓呯┖鍙傛暟缂撳瓨鏁版嵁
     */
    public void clearConfigCache();

    /**
     * 閲嶇疆鍙傛暟缂撳瓨鏁版嵁
     */
    public void resetConfigCache();

    /**
     * 鏍￠獙鍙傛暟閿悕鏄惁鍞竴
     * 
     * @param config 鍙傛暟淇℃伅
     * @return 缁撴灉
     */
    public boolean checkConfigKeyUnique(SysConfig config);
}
