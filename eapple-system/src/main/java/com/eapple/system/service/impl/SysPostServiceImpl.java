package com.eapple.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eapple.common.constant.UserConstants;
import com.eapple.common.exception.ServiceException;
import com.eapple.common.utils.StringUtils;
import com.eapple.system.domain.SysPost;
import com.eapple.system.mapper.SysPostMapper;
import com.eapple.system.mapper.SysUserPostMapper;
import com.eapple.system.service.ISysPostService;

/**
 * 宀椾綅淇℃伅 鏈嶅姟灞傚鐞?
 * 
 * @author Eapp1e
 */
@Service
public class SysPostServiceImpl implements ISysPostService
{
    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysUserPostMapper userPostMapper;

    /**
     * 鏌ヨ宀椾綅淇℃伅闆嗗悎
     * 
     * @param post 宀椾綅淇℃伅
     * @return 宀椾綅淇℃伅闆嗗悎
     */
    @Override
    public List<SysPost> selectPostList(SysPost post)
    {
        return postMapper.selectPostList(post);
    }

    /**
     * 鏌ヨ鎵€鏈夊矖浣?
     * 
     * @return 宀椾綅鍒楄〃
     */
    @Override
    public List<SysPost> selectPostAll()
    {
        return postMapper.selectPostAll();
    }

    /**
     * 閫氳繃宀椾綅ID鏌ヨ宀椾綅淇℃伅
     * 
     * @param postId 宀椾綅ID
     * @return 瑙掕壊瀵硅薄淇℃伅
     */
    @Override
    public SysPost selectPostById(Long postId)
    {
        return postMapper.selectPostById(postId);
    }

    /**
     * 鏍规嵁鐢ㄦ埛ID鑾峰彇宀椾綅閫夋嫨妗嗗垪琛?
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 閫変腑宀椾綅ID鍒楄〃
     */
    @Override
    public List<Long> selectPostListByUserId(Long userId)
    {
        return postMapper.selectPostListByUserId(userId);
    }

    /**
     * 鏍￠獙宀椾綅鍚嶇О鏄惁鍞竴
     * 
     * @param post 宀椾綅淇℃伅
     * @return 缁撴灉
     */
    @Override
    public boolean checkPostNameUnique(SysPost post)
    {
        Long postId = StringUtils.isNull(post.getPostId()) ? -1L : post.getPostId();
        SysPost info = postMapper.checkPostNameUnique(post.getPostName());
        if (StringUtils.isNotNull(info) && info.getPostId().longValue() != postId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 鏍￠獙宀椾綅缂栫爜鏄惁鍞竴
     * 
     * @param post 宀椾綅淇℃伅
     * @return 缁撴灉
     */
    @Override
    public boolean checkPostCodeUnique(SysPost post)
    {
        Long postId = StringUtils.isNull(post.getPostId()) ? -1L : post.getPostId();
        SysPost info = postMapper.checkPostCodeUnique(post.getPostCode());
        if (StringUtils.isNotNull(info) && info.getPostId().longValue() != postId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 閫氳繃宀椾綅ID鏌ヨ宀椾綅浣跨敤鏁伴噺
     * 
     * @param postId 宀椾綅ID
     * @return 缁撴灉
     */
    @Override
    public int countUserPostById(Long postId)
    {
        return userPostMapper.countUserPostById(postId);
    }

    /**
     * 鍒犻櫎宀椾綅淇℃伅
     * 
     * @param postId 宀椾綅ID
     * @return 缁撴灉
     */
    @Override
    public int deletePostById(Long postId)
    {
        return postMapper.deletePostById(postId);
    }

    /**
     * 鎵归噺鍒犻櫎宀椾綅淇℃伅
     * 
     * @param postIds 闇€瑕佸垹闄ょ殑宀椾綅ID
     * @return 缁撴灉
     */
    @Override
    public int deletePostByIds(Long[] postIds)
    {
        for (Long postId : postIds)
        {
            SysPost post = selectPostById(postId);
            if (countUserPostById(postId) > 0)
            {
                throw new ServiceException(String.format("%1$s宸插垎閰?涓嶈兘鍒犻櫎", post.getPostName()));
            }
        }
        return postMapper.deletePostByIds(postIds);
    }

    /**
     * 鏂板淇濆瓨宀椾綅淇℃伅
     * 
     * @param post 宀椾綅淇℃伅
     * @return 缁撴灉
     */
    @Override
    public int insertPost(SysPost post)
    {
        return postMapper.insertPost(post);
    }

    /**
     * 淇敼淇濆瓨宀椾綅淇℃伅
     * 
     * @param post 宀椾綅淇℃伅
     * @return 缁撴灉
     */
    @Override
    public int updatePost(SysPost post)
    {
        return postMapper.updatePost(post);
    }
}
