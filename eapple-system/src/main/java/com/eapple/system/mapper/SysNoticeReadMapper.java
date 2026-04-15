package com.eapple.system.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.eapple.system.domain.SysNotice;
import com.eapple.system.domain.SysNoticeRead;

/**
 * 鍏憡宸茶璁板綍 鏁版嵁灞?
 *
 * @author Eapp1e
 */
public interface SysNoticeReadMapper
{
    /**
     * 鏂板宸茶璁板綍锛堝拷鐣ラ噸澶嶏級
     *
     * @param noticeRead 宸茶璁板綍
     * @return 缁撴灉
     */
    public int insertNoticeRead(SysNoticeRead noticeRead);

    /**
     * 鏌ヨ鏌愮敤鎴锋湭璇诲叕鍛婃暟閲?
     *
     * @param userId 鐢ㄦ埛ID
     * @return 鏈鏁伴噺
     */
    public int selectUnreadCount(@Param("userId") Long userId);

    /**
     * 鏌ヨ鏌愮敤鎴锋槸鍚﹀凡璇绘煇鍏憡
     *
     * @param noticeId 鍏憡ID
     * @param userId   鐢ㄦ埛ID
     * @return 宸茶璁板綍鏁帮紙0鏈 1宸茶锛?
     */
    public int selectIsRead(@Param("noticeId") Long noticeId, @Param("userId") Long userId);

    /**
     * 鎵归噺鏍囪宸茶
     *
     * @param userId    鐢ㄦ埛ID
     * @param noticeIds 鍏憡ID鏁扮粍
     * @return 缁撴灉
     */
    public int insertNoticeReadBatch(@Param("userId") Long userId, @Param("noticeIds") Long[] noticeIds);

    /**
     * 鏌ヨ甯﹀凡璇荤姸鎬佺殑鍏憡鍒楄〃锛圫QL灞傞檺鍒舵潯鏁帮紝涓€娆℃煡璇㈠畬鎴愶級
     *
     * @param userId 鐢ㄦ埛ID
     * @param limit  鏈€澶氳繑鍥炴潯鏁?
     * @return 甯?isRead 鏍囪鐨勫叕鍛婂垪琛?
     */
    public List<SysNotice> selectNoticeListWithReadStatus(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 鏌ヨ宸查槄璇绘煇鍏憡鐨勭敤鎴峰垪琛?
     *
     * @param noticeId 鍏憡ID
     * @param searchValue 鎼滅储鍊?
     * @return 宸茶鐢ㄦ埛鍒楄〃
     */
    public List<Map<String, Object>> selectReadUsersByNoticeId(@Param("noticeId") Long noticeId, @Param("searchValue") String searchValue);

    /**
     * 鍏憡鍒犻櫎鏃舵竻鐞嗗搴斿凡璇昏褰?
     *
     * @param noticeIds 鍏憡ID鏁扮粍
     * @return 缁撴灉
     */
    public int deleteByNoticeIds(@Param("noticeIds") Long[] noticeIds);
}
