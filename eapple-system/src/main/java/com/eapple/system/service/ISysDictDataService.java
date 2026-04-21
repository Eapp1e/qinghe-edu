package com.eapple.system.service;

import java.util.List;
import com.eapple.common.core.domain.entity.SysDictData;

/**
 * 瀛楀吀 涓氬姟灞?
 * 
 * @author Eapp1e
 */
public interface ISysDictDataService
{
    /**
     * 鏍规嵁鏉′欢鍒嗛〉鏌ヨ瀛楀吀鏁版嵁
     * 
     * @param dictData 瀛楀吀鏁版嵁淇℃伅
     * @return 瀛楀吀鏁版嵁闆嗗悎淇℃伅
     */
    public List<SysDictData> selectDictDataList(SysDictData dictData);

    /**
     * 鏍规嵁瀛楀吀绫诲瀷鍜屽瓧鍏搁敭鍊兼煡璇㈠瓧鍏告暟鎹俊鎭?
     * 
     * @param dictType 瀛楀吀绫诲瀷
     * @param dictValue 瀛楀吀閿€?
     * @return 瀛楀吀鏍囩
     */
    public String selectDictLabel(String dictType, String dictValue);

    /**
     * 鏍规嵁瀛楀吀鏁版嵁ID鏌ヨ淇℃伅
     * 
     * @param dictCode 瀛楀吀鏁版嵁ID
     * @return 瀛楀吀鏁版嵁
     */
    public SysDictData selectDictDataById(Long dictCode);

    /**
     * 鎵归噺鍒犻櫎瀛楀吀鏁版嵁淇℃伅
     * 
     * @param dictCodes 闇€瑕佸垹闄ょ殑瀛楀吀鏁版嵁ID
     */
    public void deleteDictDataByIds(Long[] dictCodes);

    /**
     * 鏂板淇濆瓨瀛楀吀鏁版嵁淇℃伅
     * 
     * @param dictData 瀛楀吀鏁版嵁淇℃伅
     * @return 缁撴灉
     */
    public int insertDictData(SysDictData dictData);

    /**
     * 淇敼淇濆瓨瀛楀吀鏁版嵁淇℃伅
     * 
     * @param dictData 瀛楀吀鏁版嵁淇℃伅
     * @return 缁撴灉
     */
    public int updateDictData(SysDictData dictData);
}
