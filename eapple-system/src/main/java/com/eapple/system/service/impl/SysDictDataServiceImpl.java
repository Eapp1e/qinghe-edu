package com.eapple.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eapple.common.core.domain.entity.SysDictData;
import com.eapple.common.utils.DictUtils;
import com.eapple.system.mapper.SysDictDataMapper;
import com.eapple.system.service.ISysDictDataService;

/**
 * 瀛楀吀 涓氬姟灞傚鐞?
 * 
 * @author Eapp1e
 */
@Service
public class SysDictDataServiceImpl implements ISysDictDataService
{
    @Autowired
    private SysDictDataMapper dictDataMapper;

    /**
     * 鏍规嵁鏉′欢鍒嗛〉鏌ヨ瀛楀吀鏁版嵁
     * 
     * @param dictData 瀛楀吀鏁版嵁淇℃伅
     * @return 瀛楀吀鏁版嵁闆嗗悎淇℃伅
     */
    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData)
    {
        return dictDataMapper.selectDictDataList(dictData);
    }

    /**
     * 鏍规嵁瀛楀吀绫诲瀷鍜屽瓧鍏搁敭鍊兼煡璇㈠瓧鍏告暟鎹俊鎭?
     * 
     * @param dictType 瀛楀吀绫诲瀷
     * @param dictValue 瀛楀吀閿€?
     * @return 瀛楀吀鏍囩
     */
    @Override
    public String selectDictLabel(String dictType, String dictValue)
    {
        return dictDataMapper.selectDictLabel(dictType, dictValue);
    }

    /**
     * 鏍规嵁瀛楀吀鏁版嵁ID鏌ヨ淇℃伅
     * 
     * @param dictCode 瀛楀吀鏁版嵁ID
     * @return 瀛楀吀鏁版嵁
     */
    @Override
    public SysDictData selectDictDataById(Long dictCode)
    {
        return dictDataMapper.selectDictDataById(dictCode);
    }

    /**
     * 鎵归噺鍒犻櫎瀛楀吀鏁版嵁淇℃伅
     * 
     * @param dictCodes 闇€瑕佸垹闄ょ殑瀛楀吀鏁版嵁ID
     */
    @Override
    public void deleteDictDataByIds(Long[] dictCodes)
    {
        for (Long dictCode : dictCodes)
        {
            SysDictData data = selectDictDataById(dictCode);
            dictDataMapper.deleteDictDataById(dictCode);
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(data.getDictType());
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
    }

    /**
     * 鏂板淇濆瓨瀛楀吀鏁版嵁淇℃伅
     * 
     * @param data 瀛楀吀鏁版嵁淇℃伅
     * @return 缁撴灉
     */
    @Override
    public int insertDictData(SysDictData data)
    {
        int row = dictDataMapper.insertDictData(data);
        if (row > 0)
        {
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(data.getDictType());
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
        return row;
    }

    /**
     * 淇敼淇濆瓨瀛楀吀鏁版嵁淇℃伅
     * 
     * @param data 瀛楀吀鏁版嵁淇℃伅
     * @return 缁撴灉
     */
    @Override
    public int updateDictData(SysDictData data)
    {
        int row = dictDataMapper.updateDictData(data);
        if (row > 0)
        {
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(data.getDictType());
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
        return row;
    }
}
