package com.eapple.system.service;

import java.util.List;
import java.util.Map;
import com.eapple.system.domain.SysNotice;

/**
 * 公告已读记录服务接口
 *
 * @author Eapp1e
 */
public interface ISysNoticeReadService
{
    /**
     * 标记已读（幂等，重复调用不报错）
     *
     * @param noticeId 公告 ID
     * @param userId 用户 ID
     */
    public void markRead(Long noticeId, Long userId);

    /**
     * 查询某用户未读公告数量
     *
     * @param userId 用户 ID
     * @return 未读数量
     */
    public int selectUnreadCount(Long userId);

    /**
     * 查询公告列表并标记当前用户是否已读（用于首页展示）
     *
     * @param userId 用户 ID
     * @param limit 展示条数限制
     * @return 带 isRead 标识的公告列表
     */
    public List<SysNotice> selectNoticeListWithReadStatus(Long userId, int limit);

    /**
     * 批量标记已读
     *
     * @param userId 用户 ID
     * @param noticeIds 公告 ID 数组
     */
    public void markReadBatch(Long userId, Long[] noticeIds);

    /**
     * 查询某公告的已读用户列表
     *
     * @param noticeId 公告 ID
     * @param searchValue 搜索关键字
     * @return 已读用户列表
     */
    public List<Map<String, Object>> selectReadUsersByNoticeId(Long noticeId, String searchValue);

    /**
     * 删除公告时清理对应的已读记录
     *
     * @param noticeIds 公告 ID 数组
     */
    public void deleteByNoticeIds(Long[] noticeIds);
}
