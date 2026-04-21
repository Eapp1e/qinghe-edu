package com.eapple.system.service.impl;

import java.util.Collection;
import java.util.List;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eapple.common.constant.CacheConstants;
import com.eapple.common.constant.UserConstants;
import com.eapple.common.core.redis.RedisCache;
import com.eapple.common.core.text.Convert;
import com.eapple.common.exception.ServiceException;
import com.eapple.common.utils.StringUtils;
import com.eapple.system.domain.SysConfig;
import com.eapple.system.mapper.SysConfigMapper;
import com.eapple.system.service.ISysConfigService;

/**
 * 鍙傛暟閰嶇疆 鏈嶅姟灞傚疄鐜?
 * 
 * @author Eapp1e
 */
@Service
public class SysConfigServiceImpl implements ISysConfigService
{
    @Autowired
    private SysConfigMapper configMapper;

    @Autowired
    private RedisCache redisCache;

    /**
     * 椤圭洰鍚姩鏃讹紝鍒濆鍖栧弬鏁板埌缂撳瓨
     */
    @PostConstruct
    public void init()
    {
        loadingConfigCache();
    }

    /**
     * 鏌ヨ鍙傛暟閰嶇疆淇℃伅
     * 
     * @param configId 鍙傛暟閰嶇疆ID
     * @return 鍙傛暟閰嶇疆淇℃伅
     */
    @Override
    public SysConfig selectConfigById(Long configId)
    {
        SysConfig config = new SysConfig();
        config.setConfigId(configId);
        return configMapper.selectConfig(config);
    }

    /**
     * 鏍规嵁閿悕鏌ヨ鍙傛暟閰嶇疆淇℃伅
     * 
     * @param configKey 鍙傛暟key
     * @return 鍙傛暟閿€?
     */
    @Override
    public String selectConfigByKey(String configKey)
    {
        String configValue = Convert.toStr(redisCache.getCacheObject(getCacheKey(configKey)));
        if (StringUtils.isNotEmpty(configValue))
        {
            return configValue;
        }
        SysConfig config = new SysConfig();
        config.setConfigKey(configKey);
        SysConfig retConfig = configMapper.selectConfig(config);
        if (StringUtils.isNotNull(retConfig))
        {
            redisCache.setCacheObject(getCacheKey(configKey), retConfig.getConfigValue());
            return retConfig.getConfigValue();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 鑾峰彇楠岃瘉鐮佸紑鍏?
     * 
     * @return true寮€鍚紝false鍏抽棴
     */
    @Override
    public boolean selectCaptchaEnabled()
    {
        String captchaEnabled = selectConfigByKey("sys.account.captchaEnabled");
        if (StringUtils.isEmpty(captchaEnabled))
        {
            return true;
        }
        return Convert.toBool(captchaEnabled);
    }

    /**
     * 鏌ヨ鍙傛暟閰嶇疆鍒楄〃
     * 
     * @param config 鍙傛暟閰嶇疆淇℃伅
     * @return 鍙傛暟閰嶇疆闆嗗悎
     */
    @Override
    public List<SysConfig> selectConfigList(SysConfig config)
    {
        return configMapper.selectConfigList(config);
    }

    /**
     * 鏂板鍙傛暟閰嶇疆
     * 
     * @param config 鍙傛暟閰嶇疆淇℃伅
     * @return 缁撴灉
     */
    @Override
    public int insertConfig(SysConfig config)
    {
        int row = configMapper.insertConfig(config);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 淇敼鍙傛暟閰嶇疆
     * 
     * @param config 鍙傛暟閰嶇疆淇℃伅
     * @return 缁撴灉
     */
    @Override
    public int updateConfig(SysConfig config)
    {
        SysConfig temp = configMapper.selectConfigById(config.getConfigId());
        if (!StringUtils.equals(temp.getConfigKey(), config.getConfigKey()))
        {
            redisCache.deleteObject(getCacheKey(temp.getConfigKey()));
        }

        int row = configMapper.updateConfig(config);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 鎵归噺鍒犻櫎鍙傛暟淇℃伅
     * 
     * @param configIds 闇€瑕佸垹闄ょ殑鍙傛暟ID
     */
    @Override
    public void deleteConfigByIds(Long[] configIds)
    {
        for (Long configId : configIds)
        {
            SysConfig config = selectConfigById(configId);
            if (StringUtils.equals(UserConstants.YES, config.getConfigType()))
            {
                throw new ServiceException(String.format("鍐呯疆鍙傛暟銆?1$s銆戜笉鑳藉垹闄?", config.getConfigKey()));
            }
            configMapper.deleteConfigById(configId);
            redisCache.deleteObject(getCacheKey(config.getConfigKey()));
        }
    }

    /**
     * 鍔犺浇鍙傛暟缂撳瓨鏁版嵁
     */
    @Override
    public void loadingConfigCache()
    {
        List<SysConfig> configsList = configMapper.selectConfigList(new SysConfig());
        for (SysConfig config : configsList)
        {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
    }

    /**
     * 娓呯┖鍙傛暟缂撳瓨鏁版嵁
     */
    @Override
    public void clearConfigCache()
    {
        Collection<String> keys = redisCache.keys(CacheConstants.SYS_CONFIG_KEY + "*");
        redisCache.deleteObject(keys);
    }

    /**
     * 閲嶇疆鍙傛暟缂撳瓨鏁版嵁
     */
    @Override
    public void resetConfigCache()
    {
        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 鏍￠獙鍙傛暟閿悕鏄惁鍞竴
     * 
     * @param config 鍙傛暟閰嶇疆淇℃伅
     * @return 缁撴灉
     */
    @Override
    public boolean checkConfigKeyUnique(SysConfig config)
    {
        Long configId = StringUtils.isNull(config.getConfigId()) ? -1L : config.getConfigId();
        SysConfig info = configMapper.checkConfigKeyUnique(config.getConfigKey());
        if (StringUtils.isNotNull(info) && info.getConfigId().longValue() != configId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 璁剧疆cache key
     * 
     * @param configKey 鍙傛暟閿?
     * @return 缂撳瓨閿甼ey
     */
    private String getCacheKey(String configKey)
    {
        return CacheConstants.SYS_CONFIG_KEY + configKey;
    }
}
