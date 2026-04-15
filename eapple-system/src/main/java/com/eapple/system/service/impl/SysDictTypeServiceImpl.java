package com.eapple.system.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.eapple.common.constant.UserConstants;
import com.eapple.common.core.domain.entity.SysDictData;
import com.eapple.common.core.domain.entity.SysDictType;
import com.eapple.common.exception.ServiceException;
import com.eapple.common.utils.DictUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.system.mapper.SysDictDataMapper;
import com.eapple.system.mapper.SysDictTypeMapper;
import com.eapple.system.service.ISysDictTypeService;

/**
 * 瀛楀吀 涓氬姟灞傚鐞?
 * 
 * @author Eapp1e
 */
@Service
public class SysDictTypeServiceImpl implements ISysDictTypeService
{
    @Autowired
    private SysDictTypeMapper dictTypeMapper;

    @Autowired
    private SysDictDataMapper dictDataMapper;

    /**
     * 椤圭洰鍚姩鏃讹紝鍒濆鍖栧瓧鍏稿埌缂撳瓨
     */
    @PostConstruct
    public void init()
    {
        loadingDictCache();
    }

    /**
     * 鏍规嵁鏉′欢鍒嗛〉鏌ヨ瀛楀吀绫诲瀷
     * 
     * @param dictType 瀛楀吀绫诲瀷淇℃伅
     * @return 瀛楀吀绫诲瀷闆嗗悎淇℃伅
     */
    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType)
    {
        return dictTypeMapper.selectDictTypeList(dictType);
    }

    /**
     * 鏍规嵁鎵€鏈夊瓧鍏哥被鍨?
     * 
     * @return 瀛楀吀绫诲瀷闆嗗悎淇℃伅
     */
    @Override
    public List<SysDictType> selectDictTypeAll()
    {
        return dictTypeMapper.selectDictTypeAll();
    }

    /**
     * 鏍规嵁瀛楀吀绫诲瀷鏌ヨ瀛楀吀鏁版嵁
     * 
     * @param dictType 瀛楀吀绫诲瀷
     * @return 瀛楀吀鏁版嵁闆嗗悎淇℃伅
     */
    @Override
    public List<SysDictData> selectDictDataByType(String dictType)
    {
        List<SysDictData> dictDatas = DictUtils.getDictCache(dictType);
        if (StringUtils.isNotEmpty(dictDatas))
        {
            return dictDatas;
        }
        dictDatas = dictDataMapper.selectDictDataByType(dictType);
        if (StringUtils.isNotEmpty(dictDatas))
        {
            DictUtils.setDictCache(dictType, dictDatas);
            return dictDatas;
        }
        return null;
    }

    /**
     * 鏍规嵁瀛楀吀绫诲瀷ID鏌ヨ淇℃伅
     * 
     * @param dictId 瀛楀吀绫诲瀷ID
     * @return 瀛楀吀绫诲瀷
     */
    @Override
    public SysDictType selectDictTypeById(Long dictId)
    {
        return dictTypeMapper.selectDictTypeById(dictId);
    }

    /**
     * 鏍规嵁瀛楀吀绫诲瀷鏌ヨ淇℃伅
     * 
     * @param dictType 瀛楀吀绫诲瀷
     * @return 瀛楀吀绫诲瀷
     */
    @Override
    public SysDictType selectDictTypeByType(String dictType)
    {
        return dictTypeMapper.selectDictTypeByType(dictType);
    }

    /**
     * 鎵归噺鍒犻櫎瀛楀吀绫诲瀷淇℃伅
     * 
     * @param dictIds 闇€瑕佸垹闄ょ殑瀛楀吀ID
     */
    @Override
    public void deleteDictTypeByIds(Long[] dictIds)
    {
        for (Long dictId : dictIds)
        {
            SysDictType dictType = selectDictTypeById(dictId);
            if (dictDataMapper.countDictDataByType(dictType.getDictType()) > 0)
            {
                throw new ServiceException(String.format("%1$s宸插垎閰?涓嶈兘鍒犻櫎", dictType.getDictName()));
            }
            dictTypeMapper.deleteDictTypeById(dictId);
            DictUtils.removeDictCache(dictType.getDictType());
        }
    }

    /**
     * 鍔犺浇瀛楀吀缂撳瓨鏁版嵁
     */
    @Override
    public void loadingDictCache()
    {
        SysDictData dictData = new SysDictData();
        dictData.setStatus("0");
        Map<String, List<SysDictData>> dictDataMap = dictDataMapper.selectDictDataList(dictData).stream().collect(Collectors.groupingBy(SysDictData::getDictType));
        for (Map.Entry<String, List<SysDictData>> entry : dictDataMap.entrySet())
        {
            DictUtils.setDictCache(entry.getKey(), entry.getValue().stream().sorted(Comparator.comparing(SysDictData::getDictSort)).collect(Collectors.toList()));
        }
    }

    /**
     * 娓呯┖瀛楀吀缂撳瓨鏁版嵁
     */
    @Override
    public void clearDictCache()
    {
        DictUtils.clearDictCache();
    }

    /**
     * 閲嶇疆瀛楀吀缂撳瓨鏁版嵁
     */
    @Override
    public void resetDictCache()
    {
        clearDictCache();
        loadingDictCache();
    }

    /**
     * 鏂板淇濆瓨瀛楀吀绫诲瀷淇℃伅
     * 
     * @param dict 瀛楀吀绫诲瀷淇℃伅
     * @return 缁撴灉
     */
    @Override
    public int insertDictType(SysDictType dict)
    {
        int row = dictTypeMapper.insertDictType(dict);
        if (row > 0)
        {
            DictUtils.setDictCache(dict.getDictType(), null);
        }
        return row;
    }

    /**
     * 淇敼淇濆瓨瀛楀吀绫诲瀷淇℃伅
     * 
     * @param dict 瀛楀吀绫诲瀷淇℃伅
     * @return 缁撴灉
     */
    @Override
    @Transactional
    public int updateDictType(SysDictType dict)
    {
        SysDictType oldDict = dictTypeMapper.selectDictTypeById(dict.getDictId());
        dictDataMapper.updateDictDataType(oldDict.getDictType(), dict.getDictType());
        int row = dictTypeMapper.updateDictType(dict);
        if (row > 0)
        {
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(dict.getDictType());
            DictUtils.setDictCache(dict.getDictType(), dictDatas);
        }
        return row;
    }

    /**
     * 鏍￠獙瀛楀吀绫诲瀷绉版槸鍚﹀敮涓€
     * 
     * @param dict 瀛楀吀绫诲瀷
     * @return 缁撴灉
     */
    @Override
    public boolean checkDictTypeUnique(SysDictType dict)
    {
        Long dictId = StringUtils.isNull(dict.getDictId()) ? -1L : dict.getDictId();
        SysDictType dictType = dictTypeMapper.checkDictTypeUnique(dict.getDictType());
        if (StringUtils.isNotNull(dictType) && dictType.getDictId().longValue() != dictId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }
}
