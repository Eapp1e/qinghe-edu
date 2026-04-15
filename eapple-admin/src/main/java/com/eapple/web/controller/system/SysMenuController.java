package com.eapple.web.controller.system;

import java.util.List;
import java.util.Map;
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
import com.eapple.common.constant.UserConstants;
import com.eapple.common.core.controller.BaseController;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.common.core.domain.entity.SysMenu;
import com.eapple.common.enums.BusinessType;
import com.eapple.common.utils.StringUtils;
import com.eapple.system.service.ISysMenuService;

/**
 * 鑿滃崟淇℃伅
 * 
 * @author Eapp1e
 */
@RestController
@RequestMapping("/system/menu")
public class SysMenuController extends BaseController
{
    @Autowired
    private ISysMenuService menuService;

    /**
     * 鑾峰彇鑿滃崟鍒楄〃
     */
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/list")
    public AjaxResult list(SysMenu menu)
    {
        List<SysMenu> menus = menuService.selectMenuList(menu, getUserId());
        return success(menus);
    }

    /**
     * 鏍规嵁鑿滃崟缂栧彿鑾峰彇璇︾粏淇℃伅
     */
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping(value = "/{menuId}")
    public AjaxResult getInfo(@PathVariable Long menuId)
    {
        return success(menuService.selectMenuById(menuId));
    }

    /**
     * 鑾峰彇鑿滃崟涓嬫媺鏍戝垪琛?
     */
    @GetMapping("/treeselect")
    public AjaxResult treeselect(SysMenu menu)
    {
        List<SysMenu> menus = menuService.selectMenuList(menu, getUserId());
        return success(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 鍔犺浇瀵瑰簲瑙掕壊鑿滃崟鍒楄〃鏍?
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public AjaxResult roleMenuTreeselect(@PathVariable("roleId") Long roleId)
    {
        List<SysMenu> menus = menuService.selectMenuList(getUserId());
        AjaxResult ajax = AjaxResult.success();
        ajax.put("checkedKeys", menuService.selectMenuListByRoleId(roleId));
        ajax.put("menus", menuService.buildMenuTreeSelect(menus));
        return ajax;
    }

    /**
     * 鏂板鑿滃崟
     */
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @Log(title = "鑿滃崟绠＄悊", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysMenu menu)
    {
        if (!menuService.checkMenuNameUnique(menu))
        {
            return error("鏂板鑿滃崟'" + menu.getMenuName() + "'澶辫触锛岃彍鍗曞悕绉板凡瀛樺湪");
        }
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath()))
        {
            return error("鏂板鑿滃崟'" + menu.getMenuName() + "'澶辫触锛屽湴鍧€蹇呴』浠ttp(s)://寮€澶?);
        }
        else if (!menuService.checkRouteConfigUnique(menu))
        {
            return error("鏂板鑿滃崟'" + menu.getMenuName() + "'澶辫触锛岃矾鐢卞悕绉版垨鍦板潃宸插瓨鍦?);
        }
        menu.setCreateBy(getUsername());
        return toAjax(menuService.insertMenu(menu));
    }

    /**
     * 淇敼鑿滃崟
     */
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @Log(title = "鑿滃崟绠＄悊", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysMenu menu)
    {
        if (!menuService.checkMenuNameUnique(menu))
        {
            return error("淇敼鑿滃崟'" + menu.getMenuName() + "'澶辫触锛岃彍鍗曞悕绉板凡瀛樺湪");
        }
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath()))
        {
            return error("淇敼鑿滃崟'" + menu.getMenuName() + "'澶辫触锛屽湴鍧€蹇呴』浠ttp(s)://寮€澶?);
        }
        else if (menu.getMenuId().equals(menu.getParentId()))
        {
            return error("淇敼鑿滃崟'" + menu.getMenuName() + "'澶辫触锛屼笂绾ц彍鍗曚笉鑳介€夋嫨鑷繁");
        }
        else if (!menuService.checkRouteConfigUnique(menu))
        {
            return error("淇敼鑿滃崟'" + menu.getMenuName() + "'澶辫触锛岃矾鐢卞悕绉版垨鍦板潃宸插瓨鍦?);
        }
        menu.setUpdateBy(getUsername());
        return toAjax(menuService.updateMenu(menu));
    }

    /**
     * 淇濆瓨鑿滃崟鎺掑簭
     */
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @Log(title = "淇濆瓨鑿滃崟鎺掑簭", businessType = BusinessType.UPDATE)
    @PutMapping("/updateSort")
    public AjaxResult updateSort(@RequestBody Map<String, String> params)
    {
        String[] menuIds = params.get("menuIds").split(",");
        String[] orderNums = params.get("orderNums").split(",");
        menuService.updateMenuSort(menuIds, orderNums);
        return success();
    }

    /**
     * 鍒犻櫎鑿滃崟
     */
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @Log(title = "鑿滃崟绠＄悊", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public AjaxResult remove(@PathVariable("menuId") Long menuId)
    {
        if (menuService.hasChildByMenuId(menuId))
        {
            return warn("瀛樺湪瀛愯彍鍗?涓嶅厑璁稿垹闄?);
        }
        if (menuService.checkMenuExistRole(menuId))
        {
            return warn("鑿滃崟宸插垎閰?涓嶅厑璁稿垹闄?);
        }
        return toAjax(menuService.deleteMenuById(menuId));
    }
}