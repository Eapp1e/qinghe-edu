package com.eapple.system.mapper;

import java.util.List;
import com.eapple.system.domain.SysNotice;

/**
 * 閫氱煡鍏憡琛?鏁版嵁灞?
 * 
 * @author Eapp1e
 */
public interface SysNoticeMapper
{
    /**
     * 鏌ヨ鍏憡淇℃伅
     * 
     * @param noticeId 鍏憡ID
     * @return 鍏憡淇℃伅
     */
    public SysNotice selectNoticeById(Long noticeId);

    /**
     * 鏌ヨ鍏憡鍒楄〃
     * 
     * @param notice 鍏憡淇℃伅
     * @return 鍏憡闆嗗悎
     */
    public List<SysNotice> selectNoticeList(SysNotice notice);

    /**
     * 鏂板鍏憡
     * 
     * @param notice 鍏憡淇℃伅
     * @return 缁撴灉
     */
    public int insertNotice(SysNotice notice);

    /**
     * 淇敼鍏憡
     * 
     * @param notice 鍏憡淇℃伅
     * @return 缁撴灉
     */
    public int updateNotice(SysNotice notice);

    /**
     * 鎵归噺鍒犻櫎鍏憡
     * 
     * @param noticeId 鍏憡ID
     * @return 缁撴灉
     */
    public int deleteNoticeById(Long noticeId);

    /**
     * 鎵归噺鍒犻櫎鍏憡淇℃伅
     * 
     * @param noticeIds 闇€瑕佸垹闄ょ殑鍏憡ID
     * @return 缁撴灉
     */
    public int deleteNoticeByIds(Long[] noticeIds);
}
