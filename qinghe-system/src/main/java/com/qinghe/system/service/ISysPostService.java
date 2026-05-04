package com.qinghe.system.service;

import java.util.List;
import com.qinghe.system.domain.SysPost;

/**
 * 岗位信息服务接口。
 *
 * @author Eapp1e
 */
public interface ISysPostService
{
    /**
     * 查询岗位信息集合。
     *
     * @param post 岗位条件
     * @return 岗位集合
     */
    public List<SysPost> selectPostList(SysPost post);

    /**
     * 查询所有岗位。
     *
     * @return 岗位集合
     */
    public List<SysPost> selectPostAll();

    /**
     * 根据岗位 ID 查询岗位信息。
     *
     * @param postId 岗位 ID
     * @return 岗位对象
     */
    public SysPost selectPostById(Long postId);

    /**
     * 根据用户 ID 获取岗位选择框列表。
     *
     * @param userId 用户 ID
     * @return 岗位 ID 列表
     */
    public List<Long> selectPostListByUserId(Long userId);

    /**
     * 校验岗位名称是否唯一。
     *
     * @param post 岗位信息
     * @return 校验结果
     */
    public boolean checkPostNameUnique(SysPost post);

    /**
     * 校验岗位编码是否唯一。
     *
     * @param post 岗位信息
     * @return 校验结果
     */
    public boolean checkPostCodeUnique(SysPost post);

    /**
     * 根据岗位 ID 查询岗位使用数量。
     *
     * @param postId 岗位 ID
     * @return 结果
     */
    public int countUserPostById(Long postId);

    /**
     * 删除岗位信息。
     *
     * @param postId 岗位 ID
     * @return 结果
     */
    public int deletePostById(Long postId);

    /**
     * 批量删除岗位信息。
     *
     * @param postIds 需要删除的岗位 ID
     * @return 结果
     */
    public int deletePostByIds(Long[] postIds);

    /**
     * 新增岗位信息。
     *
     * @param post 岗位信息
     * @return 结果
     */
    public int insertPost(SysPost post);

    /**
     * 修改岗位信息。
     *
     * @param post 岗位信息
     * @return 结果
     */
    public int updatePost(SysPost post);
}
