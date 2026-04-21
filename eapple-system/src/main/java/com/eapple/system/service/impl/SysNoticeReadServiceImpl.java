package com.eapple.system.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eapple.system.domain.SysNotice;
import com.eapple.system.domain.SysNoticeRead;
import com.eapple.system.mapper.SysNoticeReadMapper;
import com.eapple.system.service.ISysNoticeReadService;

/**
 * 鍏憡宸茶璁板綍 鏈嶅姟灞傚疄鐜?
 *
 * @author Eapp1e
 */
@Service
public class SysNoticeReadServiceImpl implements ISysNoticeReadService
{
    @Autowired
    private SysNoticeReadMapper noticeReadMapper;

    /**
     * 鏍囪宸茶
     */
    @Override
    public void markRead(Long noticeId, Long userId)
    {
        SysNoticeRead record = new SysNoticeRead();
        record.setNoticeId(noticeId);
        record.setUserId(userId);
        noticeReadMapper.insertNoticeRead(record);
    }

    /**
     * 鏌ヨ鏌愮敤鎴锋湭璇诲叕鍛婃暟閲?
     */
    @Override
    public int selectUnreadCount(Long userId)
    {
        return noticeReadMapper.selectUnreadCount(userId);
    }

    /**
     * 鏌ヨ鍏憡鍒楄〃骞舵爣璁板綋鍓嶇敤鎴峰凡璇荤姸鎬?
     */
    @Override
    public List<SysNotice> selectNoticeListWithReadStatus(Long userId, int limit)
    {
        return noticeReadMapper.selectNoticeListWithReadStatus(userId, limit);
    }

    /**
     * 鎵归噺鏍囪宸茶
     */
    @Override
    public void markReadBatch(Long userId, Long[] noticeIds)
    {
        if (noticeIds == null || noticeIds.length == 0)
        {
            return;
        }
        noticeReadMapper.insertNoticeReadBatch(userId, noticeIds);
    }

    /**
     * 鏌ヨ宸查槄璇绘煇鍏憡鐨勭敤鎴峰垪琛?
     */
    @Override
    public List<Map<String, Object>> selectReadUsersByNoticeId(Long noticeId, String searchValue)
    {
        return noticeReadMapper.selectReadUsersByNoticeId(noticeId, searchValue);
    }

    /**
     * 鍒犻櫎鍏憡鏃舵竻鐞嗗搴斿凡璇昏褰?
     */
    @Override
    public void deleteByNoticeIds(Long[] noticeIds)
    {
        noticeReadMapper.deleteByNoticeIds(noticeIds);
    }
}
