package com.eapple.system.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.eapple.system.domain.SysNotice;
import com.eapple.system.domain.SysNoticeRead;

/**
 * 公告已读记录数据层。
 *
 * @author Eapp1e
 */
public interface SysNoticeReadMapper
{
    /**
     * 新增公告已读记录。
     *
     * @param noticeRead 已读记录对象
     * @return 结果
     */
    public int insertNoticeRead(SysNoticeRead noticeRead);

    /**
     * 查询用户未读公告数量。
     *
     * @param userId 用户 ID
     * @return 未读数量
     */
    public int selectUnreadCount(@Param("userId") Long userId);

    /**
     * 查询指定公告是否已读。
     *
     * @param noticeId 公告 ID
     * @param userId 用户 ID
     * @return 是否已读，1 表示已读
     */
    public int selectIsRead(@Param("noticeId") Long noticeId, @Param("userId") Long userId);

    /**
     * 批量新增公告已读记录。
     *
     * @param userId 用户 ID
     * @param noticeIds 公告 ID 数组
     * @return 结果
     */
    public int insertNoticeReadBatch(@Param("userId") Long userId, @Param("noticeIds") Long[] noticeIds);

    /**
     * 查询公告列表及已读状态。
     *
     * @param userId 用户 ID
     * @param limit 查询条数限制
     * @return 公告列表
     */
    public List<SysNotice> selectNoticeListWithReadStatus(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 查询指定公告的已读用户列表。
     *
     * @param noticeId 公告 ID
     * @param searchValue 搜索关键词
     * @return 已读用户列表
     */
    public List<Map<String, Object>> selectReadUsersByNoticeId(@Param("noticeId") Long noticeId, @Param("searchValue") String searchValue);

    /**
     * 根据公告 ID 批量删除已读记录。
     *
     * @param noticeIds 公告 ID 数组
     * @return 结果
     */
    public int deleteByNoticeIds(@Param("noticeIds") Long[] noticeIds);
}
