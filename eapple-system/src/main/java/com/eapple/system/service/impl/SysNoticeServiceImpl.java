package com.eapple.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eapple.system.domain.SysNotice;
import com.eapple.system.mapper.SysNoticeMapper;
import com.eapple.system.service.ISysNoticeService;

/**
 * 鍏憡 鏈嶅姟灞傚疄鐜?
 * 
 * @author Eapp1e
 */
@Service
public class SysNoticeServiceImpl implements ISysNoticeService
{
    @Autowired
    private SysNoticeMapper noticeMapper;

    /**
     * 鏌ヨ鍏憡淇℃伅
     * 
     * @param noticeId 鍏憡ID
     * @return 鍏憡淇℃伅
     */
    @Override
    public SysNotice selectNoticeById(Long noticeId)
    {
        return noticeMapper.selectNoticeById(noticeId);
    }

    /**
     * 鏌ヨ鍏憡鍒楄〃
     * 
     * @param notice 鍏憡淇℃伅
     * @return 鍏憡闆嗗悎
     */
    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice)
    {
        return noticeMapper.selectNoticeList(notice);
    }

    /**
     * 鏂板鍏憡
     * 
     * @param notice 鍏憡淇℃伅
     * @return 缁撴灉
     */
    @Override
    public int insertNotice(SysNotice notice)
    {
        return noticeMapper.insertNotice(notice);
    }

    /**
     * 淇敼鍏憡
     * 
     * @param notice 鍏憡淇℃伅
     * @return 缁撴灉
     */
    @Override
    public int updateNotice(SysNotice notice)
    {
        return noticeMapper.updateNotice(notice);
    }

    /**
     * 鍒犻櫎鍏憡瀵硅薄
     * 
     * @param noticeId 鍏憡ID
     * @return 缁撴灉
     */
    @Override
    public int deleteNoticeById(Long noticeId)
    {
        return noticeMapper.deleteNoticeById(noticeId);
    }

    /**
     * 鎵归噺鍒犻櫎鍏憡淇℃伅
     * 
     * @param noticeIds 闇€瑕佸垹闄ょ殑鍏憡ID
     * @return 缁撴灉
     */
    @Override
    public int deleteNoticeByIds(Long[] noticeIds)
    {
        return noticeMapper.deleteNoticeByIds(noticeIds);
    }
}
