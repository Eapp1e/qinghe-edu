package com.eapple.system.mapper;

import java.util.List;
import com.eapple.common.core.domain.entity.SysDictType;

/**
 * 瀛楀吀琛?鏁版嵁灞?
 * 
 * @author Eapp1e
 */
public interface SysDictTypeMapper
{
    /**
     * 鏍规嵁鏉′欢鍒嗛〉鏌ヨ瀛楀吀绫诲瀷
     * 
     * @param dictType 瀛楀吀绫诲瀷淇℃伅
     * @return 瀛楀吀绫诲瀷闆嗗悎淇℃伅
     */
    public List<SysDictType> selectDictTypeList(SysDictType dictType);

    /**
     * 鏍规嵁鎵€鏈夊瓧鍏哥被鍨?
     * 
     * @return 瀛楀吀绫诲瀷闆嗗悎淇℃伅
     */
    public List<SysDictType> selectDictTypeAll();

    /**
     * 鏍规嵁瀛楀吀绫诲瀷ID鏌ヨ淇℃伅
     * 
     * @param dictId 瀛楀吀绫诲瀷ID
     * @return 瀛楀吀绫诲瀷
     */
    public SysDictType selectDictTypeById(Long dictId);

    /**
     * 鏍规嵁瀛楀吀绫诲瀷鏌ヨ淇℃伅
     * 
     * @param dictType 瀛楀吀绫诲瀷
     * @return 瀛楀吀绫诲瀷
     */
    public SysDictType selectDictTypeByType(String dictType);

    /**
     * 閫氳繃瀛楀吀ID鍒犻櫎瀛楀吀淇℃伅
     * 
     * @param dictId 瀛楀吀ID
     * @return 缁撴灉
     */
    public int deleteDictTypeById(Long dictId);

    /**
     * 鎵归噺鍒犻櫎瀛楀吀绫诲瀷淇℃伅
     * 
     * @param dictIds 闇€瑕佸垹闄ょ殑瀛楀吀ID
     * @return 缁撴灉
     */
    public int deleteDictTypeByIds(Long[] dictIds);

    /**
     * 鏂板瀛楀吀绫诲瀷淇℃伅
     * 
     * @param dictType 瀛楀吀绫诲瀷淇℃伅
     * @return 缁撴灉
     */
    public int insertDictType(SysDictType dictType);

    /**
     * 淇敼瀛楀吀绫诲瀷淇℃伅
     * 
     * @param dictType 瀛楀吀绫诲瀷淇℃伅
     * @return 缁撴灉
     */
    public int updateDictType(SysDictType dictType);

    /**
     * 鏍￠獙瀛楀吀绫诲瀷绉版槸鍚﹀敮涓€
     * 
     * @param dictType 瀛楀吀绫诲瀷
     * @return 缁撴灉
     */
    public SysDictType checkDictTypeUnique(String dictType);
}
