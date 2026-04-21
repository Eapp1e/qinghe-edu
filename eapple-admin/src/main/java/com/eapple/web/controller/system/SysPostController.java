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
import com.eapple.system.domain.SysPost;
import com.eapple.system.service.ISysPostService;

/**
 * 宀椾綅淇℃伅鎿嶄綔澶勭悊
 * 
 * @author Eapp1e
 */
@RestController
@RequestMapping("/system/post")
public class SysPostController extends BaseController
{
    @Autowired
    private ISysPostService postService;

    /**
     * 鑾峰彇宀椾綅鍒楄〃
     */
    @PreAuthorize("@ss.hasPermi('system:post:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysPost post)
    {
        startPage();
        List<SysPost> list = postService.selectPostList(post);
        return getDataTable(list);
    }
    
    @Log(title = "宀椾綅绠＄悊", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:post:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysPost post)
    {
        List<SysPost> list = postService.selectPostList(post);
        ExcelUtil<SysPost> util = new ExcelUtil<SysPost>(SysPost.class);
        util.exportExcel(response, list, "宀椾綅鏁版嵁");
    }

    /**
     * 鏍规嵁宀椾綅缂栧彿鑾峰彇璇︾粏淇℃伅
     */
    @PreAuthorize("@ss.hasPermi('system:post:query')")
    @GetMapping(value = "/{postId}")
    public AjaxResult getInfo(@PathVariable Long postId)
    {
        return success(postService.selectPostById(postId));
    }

    /**
     * 鏂板宀椾綅
     */
    @PreAuthorize("@ss.hasPermi('system:post:add')")
    @Log(title = "宀椾綅绠＄悊", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysPost post)
    {
        if (!postService.checkPostNameUnique(post))
        {
            return error("鏂板宀椾綅'" + post.getPostName() + "'澶辫触锛屽矖浣嶅悕绉板凡瀛樺湪");
        }
        else if (!postService.checkPostCodeUnique(post))
        {
            return error("鏂板宀椾綅'" + post.getPostName() + "'澶辫触锛屽矖浣嶇紪鐮佸凡瀛樺湪");
        }
        post.setCreateBy(getUsername());
        return toAjax(postService.insertPost(post));
    }

    /**
     * 淇敼宀椾綅
     */
    @PreAuthorize("@ss.hasPermi('system:post:edit')")
    @Log(title = "宀椾綅绠＄悊", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysPost post)
    {
        if (!postService.checkPostNameUnique(post))
        {
            return error("淇敼宀椾綅'" + post.getPostName() + "'澶辫触锛屽矖浣嶅悕绉板凡瀛樺湪");
        }
        else if (!postService.checkPostCodeUnique(post))
        {
            return error("淇敼宀椾綅'" + post.getPostName() + "'澶辫触锛屽矖浣嶇紪鐮佸凡瀛樺湪");
        }
        post.setUpdateBy(getUsername());
        return toAjax(postService.updatePost(post));
    }

    /**
     * 鍒犻櫎宀椾綅
     */
    @PreAuthorize("@ss.hasPermi('system:post:remove')")
    @Log(title = "宀椾綅绠＄悊", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public AjaxResult remove(@PathVariable Long[] postIds)
    {
        return toAjax(postService.deletePostByIds(postIds));
    }

    /**
     * 鑾峰彇宀椾綅閫夋嫨妗嗗垪琛?
     */
    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        List<SysPost> posts = postService.selectPostAll();
        return success(posts);
    }
}
