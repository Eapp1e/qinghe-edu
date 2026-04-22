package com.eapple.system.mapper;

import java.util.List;
import com.eapple.system.domain.SysNotice;

/**
 * 公告管理数据层。
 * 
 * @author Eapp1e
 */
public interface SysNoticeMapper
{
    /**
     * 查询公告信息。
     * 
     * @param noticeId 公告 ID
     * @return 公告对象
     */
    public SysNotice selectNoticeById(Long noticeId);

    /**
     * 查询公告列表。
     * 
     * @param notice 公告对象
     * @return 公告集合
     */
    public List<SysNotice> selectNoticeList(SysNotice notice);

    /**
     * 新增公告。
     * 
     * @param notice 公告对象
     * @return 结果
     */
    public int insertNotice(SysNotice notice);

    /**
     * 修改公告。
     * 
     * @param notice 公告对象
     * @return 结果
     */
    public int updateNotice(SysNotice notice);

    /**
     * 根据 ID 删除公告。
     * 
     * @param noticeId 公告 ID
     * @return 结果
     */
    public int deleteNoticeById(Long noticeId);

    /**
     * 批量删除公告。
     * 
     * @param noticeIds 公告 ID 数组
     * @return 结果
     */
    public int deleteNoticeByIds(Long[] noticeIds);
}
