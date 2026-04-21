package com.eapple.system.service;

import java.util.List;
import com.eapple.common.core.domain.entity.SysDictData;
import com.eapple.common.core.domain.entity.SysDictType;

/**
 * 瀛楀吀 涓氬姟灞?
 * 
 * @author Eapp1e
 */
public interface ISysDictTypeService
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
     * 鏍规嵁瀛楀吀绫诲瀷鏌ヨ瀛楀吀鏁版嵁
     * 
     * @param dictType 瀛楀吀绫诲瀷
     * @return 瀛楀吀鏁版嵁闆嗗悎淇℃伅
     */
    public List<SysDictData> selectDictDataByType(String dictType);

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
     * 鎵归噺鍒犻櫎瀛楀吀淇℃伅
     * 
     * @param dictIds 闇€瑕佸垹闄ょ殑瀛楀吀ID
     */
    public void deleteDictTypeByIds(Long[] dictIds);

    /**
     * 鍔犺浇瀛楀吀缂撳瓨鏁版嵁
     */
    public void loadingDictCache();

    /**
     * 娓呯┖瀛楀吀缂撳瓨鏁版嵁
     */
    public void clearDictCache();

    /**
     * 閲嶇疆瀛楀吀缂撳瓨鏁版嵁
     */
    public void resetDictCache();

    /**
     * 鏂板淇濆瓨瀛楀吀绫诲瀷淇℃伅
     * 
     * @param dictType 瀛楀吀绫诲瀷淇℃伅
     * @return 缁撴灉
     */
    public int insertDictType(SysDictType dictType);

    /**
     * 淇敼淇濆瓨瀛楀吀绫诲瀷淇℃伅
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
    public boolean checkDictTypeUnique(SysDictType dictType);
}
