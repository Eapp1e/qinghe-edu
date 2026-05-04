package com.qinghe.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qinghe.system.domain.SysNotice;
import com.qinghe.system.mapper.SysNoticeMapper;
import com.qinghe.system.service.ISysNoticeService;
import com.qinghe.system.util.EduSchoolScopeUtils;

/**
 * 公告服务实现。
 *
 * @author Eapp1e
 */
@Service
public class SysNoticeServiceImpl implements ISysNoticeService
{
    @Autowired
    private SysNoticeMapper noticeMapper;

    /**
     * 查询公告信息。
     *
     * @param noticeId 公告 ID
     * @return 公告信息
     */
    @Override
    public SysNotice selectNoticeById(Long noticeId)
    {
        return noticeMapper.selectNoticeById(noticeId);
    }

    /**
     * 查询公告列表。
     *
     * @param notice 公告条件
     * @return 公告集合
     */
    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice)
    {
        EduSchoolScopeUtils.applySchoolScope(notice);
        return noticeMapper.selectNoticeList(notice);
    }

    /**
     * 新增公告。
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int insertNotice(SysNotice notice)
    {
        return noticeMapper.insertNotice(notice);
    }

    /**
     * 修改公告。
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int updateNotice(SysNotice notice)
    {
        return noticeMapper.updateNotice(notice);
    }

    /**
     * 删除公告。
     *
     * @param noticeId 公告 ID
     * @return 结果
     */
    @Override
    public int deleteNoticeById(Long noticeId)
    {
        return noticeMapper.deleteNoticeById(noticeId);
    }

    /**
     * 批量删除公告。
     *
     * @param noticeIds 需要删除的公告 ID
     * @return 结果
     */
    @Override
    public int deleteNoticeByIds(Long[] noticeIds)
    {
        return noticeMapper.deleteNoticeByIds(noticeIds);
    }
}
