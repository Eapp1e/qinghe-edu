package com.eapple.system.mapper;

import java.util.List;
import com.eapple.system.domain.SysUserPost;

/**
 * 用户与岗位关联数据层。
 * 
 * @author Eapp1e
 */
public interface SysUserPostMapper
{
    /**
     * 根据用户 ID 删除用户与岗位关联。
     * 
     * @param userId 用户 ID
     * @return 结果
     */
    public int deleteUserPostByUserId(Long userId);

    /**
     * 根据岗位 ID 统计用户岗位关联数量。
     * 
     * @param postId 岗位 ID
     * @return 结果
     */
    public int countUserPostById(Long postId);

    /**
     * 批量删除用户与岗位关联。
     * 
     * @param ids 关联 ID 数组
     * @return 结果
     */
    public int deleteUserPost(Long[] ids);

    /**
     * 批量新增用户与岗位关联。
     * 
     * @param userPostList 用户岗位关联列表
     * @return 结果
     */
    public int batchUserPost(List<SysUserPost> userPostList);
}
