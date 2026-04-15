package com.eapple.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eapple.system.domain.SysOperLog;
import com.eapple.system.mapper.SysOperLogMapper;
import com.eapple.system.service.ISysOperLogService;

/**
 * 鎿嶄綔鏃ュ織 鏈嶅姟灞傚鐞?
 * 
 * @author Eapp1e
 */
@Service
public class SysOperLogServiceImpl implements ISysOperLogService
{
    @Autowired
    private SysOperLogMapper operLogMapper;

    /**
     * 鏂板鎿嶄綔鏃ュ織
     * 
     * @param operLog 鎿嶄綔鏃ュ織瀵硅薄
     */
    @Override
    public void insertOperlog(SysOperLog operLog)
    {
        operLogMapper.insertOperlog(operLog);
    }

    /**
     * 鏌ヨ绯荤粺鎿嶄綔鏃ュ織闆嗗悎
     * 
     * @param operLog 鎿嶄綔鏃ュ織瀵硅薄
     * @return 鎿嶄綔鏃ュ織闆嗗悎
     */
    @Override
    public List<SysOperLog> selectOperLogList(SysOperLog operLog)
    {
        return operLogMapper.selectOperLogList(operLog);
    }

    /**
     * 鎵归噺鍒犻櫎绯荤粺鎿嶄綔鏃ュ織
     * 
     * @param operIds 闇€瑕佸垹闄ょ殑鎿嶄綔鏃ュ織ID
     * @return 缁撴灉
     */
    @Override
    public int deleteOperLogByIds(Long[] operIds)
    {
        return operLogMapper.deleteOperLogByIds(operIds);
    }

    /**
     * 鏌ヨ鎿嶄綔鏃ュ織璇︾粏
     * 
     * @param operId 鎿嶄綔ID
     * @return 鎿嶄綔鏃ュ織瀵硅薄
     */
    @Override
    public SysOperLog selectOperLogById(Long operId)
    {
        return operLogMapper.selectOperLogById(operId);
    }

    /**
     * 娓呯┖鎿嶄綔鏃ュ織
     */
    @Override
    public void cleanOperLog()
    {
        operLogMapper.cleanOperLog();
    }
}
