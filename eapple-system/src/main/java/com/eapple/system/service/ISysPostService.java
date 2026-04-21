package com.eapple.system.service;

import java.util.List;
import com.eapple.system.domain.SysPost;

/**
 * 宀椾綅淇℃伅 鏈嶅姟灞?
 * 
 * @author Eapp1e
 */
public interface ISysPostService
{
    /**
     * 鏌ヨ宀椾綅淇℃伅闆嗗悎
     * 
     * @param post 宀椾綅淇℃伅
     * @return 宀椾綅鍒楄〃
     */
    public List<SysPost> selectPostList(SysPost post);

    /**
     * 鏌ヨ鎵€鏈夊矖浣?
     * 
     * @return 宀椾綅鍒楄〃
     */
    public List<SysPost> selectPostAll();

    /**
     * 閫氳繃宀椾綅ID鏌ヨ宀椾綅淇℃伅
     * 
     * @param postId 宀椾綅ID
     * @return 瑙掕壊瀵硅薄淇℃伅
     */
    public SysPost selectPostById(Long postId);

    /**
     * 鏍规嵁鐢ㄦ埛ID鑾峰彇宀椾綅閫夋嫨妗嗗垪琛?
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 閫変腑宀椾綅ID鍒楄〃
     */
    public List<Long> selectPostListByUserId(Long userId);

    /**
     * 鏍￠獙宀椾綅鍚嶇О
     * 
     * @param post 宀椾綅淇℃伅
     * @return 缁撴灉
     */
    public boolean checkPostNameUnique(SysPost post);

    /**
     * 鏍￠獙宀椾綅缂栫爜
     * 
     * @param post 宀椾綅淇℃伅
     * @return 缁撴灉
     */
    public boolean checkPostCodeUnique(SysPost post);

    /**
     * 閫氳繃宀椾綅ID鏌ヨ宀椾綅浣跨敤鏁伴噺
     * 
     * @param postId 宀椾綅ID
     * @return 缁撴灉
     */
    public int countUserPostById(Long postId);

    /**
     * 鍒犻櫎宀椾綅淇℃伅
     * 
     * @param postId 宀椾綅ID
     * @return 缁撴灉
     */
    public int deletePostById(Long postId);

    /**
     * 鎵归噺鍒犻櫎宀椾綅淇℃伅
     * 
     * @param postIds 闇€瑕佸垹闄ょ殑宀椾綅ID
     * @return 缁撴灉
     */
    public int deletePostByIds(Long[] postIds);

    /**
     * 鏂板淇濆瓨宀椾綅淇℃伅
     * 
     * @param post 宀椾綅淇℃伅
     * @return 缁撴灉
     */
    public int insertPost(SysPost post);

    /**
     * 淇敼淇濆瓨宀椾綅淇℃伅
     * 
     * @param post 宀椾綅淇℃伅
     * @return 缁撴灉
     */
    public int updatePost(SysPost post);
}
