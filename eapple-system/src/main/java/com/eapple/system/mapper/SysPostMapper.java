package com.eapple.system.mapper;

import java.util.List;
import com.eapple.system.domain.SysPost;

/**
 * 宀椾綅淇℃伅 鏁版嵁灞?
 * 
 * @author Eapp1e
 */
public interface SysPostMapper
{
    /**
     * 鏌ヨ宀椾綅鏁版嵁闆嗗悎
     * 
     * @param post 宀椾綅淇℃伅
     * @return 宀椾綅鏁版嵁闆嗗悎
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
     * 鏌ヨ鐢ㄦ埛鎵€灞炲矖浣嶇粍
     * 
     * @param userName 鐢ㄦ埛鍚?
     * @return 缁撴灉
     */
    public List<SysPost> selectPostsByUserName(String userName);

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
     * 淇敼宀椾綅淇℃伅
     * 
     * @param post 宀椾綅淇℃伅
     * @return 缁撴灉
     */
    public int updatePost(SysPost post);

    /**
     * 鏂板宀椾綅淇℃伅
     * 
     * @param post 宀椾綅淇℃伅
     * @return 缁撴灉
     */
    public int insertPost(SysPost post);

    /**
     * 鏍￠獙宀椾綅鍚嶇О
     * 
     * @param postName 宀椾綅鍚嶇О
     * @return 缁撴灉
     */
    public SysPost checkPostNameUnique(String postName);

    /**
     * 鏍￠獙宀椾綅缂栫爜
     * 
     * @param postCode 宀椾綅缂栫爜
     * @return 缁撴灉
     */
    public SysPost checkPostCodeUnique(String postCode);
}
