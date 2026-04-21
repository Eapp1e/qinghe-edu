package com.eapple.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.eapple.common.core.domain.entity.SysDictData;

/**
 * 瀛楀吀琛?鏁版嵁灞?
 * 
 * @author Eapp1e
 */
public interface SysDictDataMapper
{
    /**
     * 鏍规嵁鏉′欢鍒嗛〉鏌ヨ瀛楀吀鏁版嵁
     * 
     * @param dictData 瀛楀吀鏁版嵁淇℃伅
     * @return 瀛楀吀鏁版嵁闆嗗悎淇℃伅
     */
    public List<SysDictData> selectDictDataList(SysDictData dictData);

    /**
     * 鏍规嵁瀛楀吀绫诲瀷鏌ヨ瀛楀吀鏁版嵁
     * 
     * @param dictType 瀛楀吀绫诲瀷
     * @return 瀛楀吀鏁版嵁闆嗗悎淇℃伅
     */
    public List<SysDictData> selectDictDataByType(String dictType);

    /**
     * 鏍规嵁瀛楀吀绫诲瀷鍜屽瓧鍏搁敭鍊兼煡璇㈠瓧鍏告暟鎹俊鎭?
     * 
     * @param dictType 瀛楀吀绫诲瀷
     * @param dictValue 瀛楀吀閿€?
     * @return 瀛楀吀鏍囩
     */
    public String selectDictLabel(@Param("dictType") String dictType, @Param("dictValue") String dictValue);

    /**
     * 鏍规嵁瀛楀吀鏁版嵁ID鏌ヨ淇℃伅
     * 
     * @param dictCode 瀛楀吀鏁版嵁ID
     * @return 瀛楀吀鏁版嵁
     */
    public SysDictData selectDictDataById(Long dictCode);

    /**
     * 鏌ヨ瀛楀吀鏁版嵁
     * 
     * @param dictType 瀛楀吀绫诲瀷
     * @return 瀛楀吀鏁版嵁
     */
    public int countDictDataByType(String dictType);

    /**
     * 閫氳繃瀛楀吀ID鍒犻櫎瀛楀吀鏁版嵁淇℃伅
     * 
     * @param dictCode 瀛楀吀鏁版嵁ID
     * @return 缁撴灉
     */
    public int deleteDictDataById(Long dictCode);

    /**
     * 鎵归噺鍒犻櫎瀛楀吀鏁版嵁淇℃伅
     * 
     * @param dictCodes 闇€瑕佸垹闄ょ殑瀛楀吀鏁版嵁ID
     * @return 缁撴灉
     */
    public int deleteDictDataByIds(Long[] dictCodes);

    /**
     * 鏂板瀛楀吀鏁版嵁淇℃伅
     * 
     * @param dictData 瀛楀吀鏁版嵁淇℃伅
     * @return 缁撴灉
     */
    public int insertDictData(SysDictData dictData);

    /**
     * 淇敼瀛楀吀鏁版嵁淇℃伅
     * 
     * @param dictData 瀛楀吀鏁版嵁淇℃伅
     * @return 缁撴灉
     */
    public int updateDictData(SysDictData dictData);

    /**
     * 鍚屾淇敼瀛楀吀绫诲瀷
     * 
     * @param oldDictType 鏃у瓧鍏哥被鍨?
     * @param newDictType 鏂版棫瀛楀吀绫诲瀷
     * @return 缁撴灉
     */
    public int updateDictDataType(@Param("oldDictType") String oldDictType, @Param("newDictType") String newDictType);
}
