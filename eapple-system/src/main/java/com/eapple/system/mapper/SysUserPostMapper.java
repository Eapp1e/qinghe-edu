package com.eapple.system.mapper;

import java.util.List;
import com.eapple.system.domain.SysUserPost;

/**
 * 鐢ㄦ埛涓庡矖浣嶅叧鑱旇〃 鏁版嵁灞?
 * 
 * @author Eapp1e
 */
public interface SysUserPostMapper
{
    /**
     * 閫氳繃鐢ㄦ埛ID鍒犻櫎鐢ㄦ埛鍜屽矖浣嶅叧鑱?
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 缁撴灉
     */
    public int deleteUserPostByUserId(Long userId);

    /**
     * 閫氳繃宀椾綅ID鏌ヨ宀椾綅浣跨敤鏁伴噺
     * 
     * @param postId 宀椾綅ID
     * @return 缁撴灉
     */
    public int countUserPostById(Long postId);

    /**
     * 鎵归噺鍒犻櫎鐢ㄦ埛鍜屽矖浣嶅叧鑱?
     * 
     * @param ids 闇€瑕佸垹闄ょ殑鏁版嵁ID
     * @return 缁撴灉
     */
    public int deleteUserPost(Long[] ids);

    /**
     * 鎵归噺鏂板鐢ㄦ埛宀椾綅淇℃伅
     * 
     * @param userPostList 鐢ㄦ埛宀椾綅鍒楄〃
     * @return 缁撴灉
     */
    public int batchUserPost(List<SysUserPost> userPostList);
}
