package com.eapple.system.service;

import java.util.List;
import java.util.Map;
import com.eapple.system.domain.SysNotice;

/**
 * 鍏憡宸茶璁板綍 鏈嶅姟灞?
 *
 * @author Eapp1e
 */
public interface ISysNoticeReadService
{
    /**
     * 鏍囪宸茶锛堝箓绛夛紝閲嶅璋冪敤涓嶆姤閿欙級
     *
     * @param noticeId 鍏憡ID
     * @param userId   鐢ㄦ埛ID
     */
    public void markRead(Long noticeId, Long userId);

    /**
     * 鏌ヨ鏌愮敤鎴锋湭璇诲叕鍛婃暟閲?
     *
     * @param userId 鐢ㄦ埛ID
     * @return 鏈鏁伴噺
     */
    public int selectUnreadCount(Long userId);

    /**
     * 鏌ヨ鍏憡鍒楄〃骞舵爣璁板綋鍓嶇敤鎴峰凡璇荤姸鎬侊紙鐢ㄤ簬棣栭〉灞曠ず锛?
     *
     * @param userId 鐢ㄦ埛ID
     * @param limit  鏈€澶氳繑鍥炴潯鏁?
     * @return 甯?isRead 鏍囪鐨勫叕鍛婂垪琛?
     */
    public List<SysNotice> selectNoticeListWithReadStatus(Long userId, int limit);

    /**
     * 鎵归噺鏍囪宸茶
     *
     * @param userId    鐢ㄦ埛ID
     * @param noticeIds 鍏憡ID鏁扮粍
     */
    public void markReadBatch(Long userId, Long[] noticeIds);

    /**
     * 鏌ヨ宸查槄璇绘煇鍏憡鐨勭敤鎴峰垪琛?
     *
     * @param noticeId  鍏憡ID
     * @param searchValue 鎼滅储鍊?
     * @return 宸茶鐢ㄦ埛鍒楄〃
     */
    public List<Map<String, Object>> selectReadUsersByNoticeId(Long noticeId, String searchValue);

    /**
     * 鍒犻櫎鍏憡鏃舵竻鐞嗗搴斿凡璇昏褰?
     *
     * @param noticeIds 鍏憡ID鏁扮粍
     */
    public void deleteByNoticeIds(Long[] noticeIds);
}
