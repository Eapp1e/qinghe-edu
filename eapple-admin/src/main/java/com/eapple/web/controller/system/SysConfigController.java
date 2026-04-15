package com.eapple.web.controller.system;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.eapple.common.annotation.Log;
import com.eapple.common.core.controller.BaseController;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.common.core.page.TableDataInfo;
import com.eapple.common.enums.BusinessType;
import com.eapple.common.utils.poi.ExcelUtil;
import com.eapple.system.domain.SysConfig;
import com.eapple.system.service.ISysConfigService;

/**
 * 鍙傛暟閰嶇疆 淇℃伅鎿嶄綔澶勭悊
 * 
 * @author Eapp1e
 */
@RestController
@RequestMapping("/system/config")
public class SysConfigController extends BaseController
{
    @Autowired
    private ISysConfigService configService;

    /**
     * 鑾峰彇鍙傛暟閰嶇疆鍒楄〃
     */
    @PreAuthorize("@ss.hasPermi('system:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysConfig config)
    {
        startPage();
        List<SysConfig> list = configService.selectConfigList(config);
        return getDataTable(list);
    }

    @Log(title = "鍙傛暟绠＄悊", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:config:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysConfig config)
    {
        List<SysConfig> list = configService.selectConfigList(config);
        ExcelUtil<SysConfig> util = new ExcelUtil<SysConfig>(SysConfig.class);
        util.exportExcel(response, list, "鍙傛暟鏁版嵁");
    }

    /**
     * 鏍规嵁鍙傛暟缂栧彿鑾峰彇璇︾粏淇℃伅
     */
    @PreAuthorize("@ss.hasPermi('system:config:query')")
    @GetMapping(value = "/{configId}")
    public AjaxResult getInfo(@PathVariable Long configId)
    {
        return success(configService.selectConfigById(configId));
    }

    /**
     * 鏍规嵁鍙傛暟閿悕鏌ヨ鍙傛暟鍊?
     */
    @GetMapping(value = "/configKey/{configKey}")
    public AjaxResult getConfigKey(@PathVariable String configKey)
    {
        return success(configService.selectConfigByKey(configKey));
    }

    /**
     * 鏂板鍙傛暟閰嶇疆
     */
    @PreAuthorize("@ss.hasPermi('system:config:add')")
    @Log(title = "鍙傛暟绠＄悊", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysConfig config)
    {
        if (!configService.checkConfigKeyUnique(config))
        {
            return error("鏂板鍙傛暟'" + config.getConfigName() + "'澶辫触锛屽弬鏁伴敭鍚嶅凡瀛樺湪");
        }
        config.setCreateBy(getUsername());
        return toAjax(configService.insertConfig(config));
    }

    /**
     * 淇敼鍙傛暟閰嶇疆
     */
    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @Log(title = "鍙傛暟绠＄悊", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysConfig config)
    {
        if (!configService.checkConfigKeyUnique(config))
        {
            return error("淇敼鍙傛暟'" + config.getConfigName() + "'澶辫触锛屽弬鏁伴敭鍚嶅凡瀛樺湪");
        }
        config.setUpdateBy(getUsername());
        return toAjax(configService.updateConfig(config));
    }

    /**
     * 鍒犻櫎鍙傛暟閰嶇疆
     */
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "鍙傛暟绠＄悊", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Long[] configIds)
    {
        configService.deleteConfigByIds(configIds);
        return success();
    }

    /**
     * 鍒锋柊鍙傛暟缂撳瓨
     */
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "鍙傛暟绠＄悊", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public AjaxResult refreshCache()
    {
        configService.resetConfigCache();
        return success();
    }
}
