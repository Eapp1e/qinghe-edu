package com.eapple.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eapple.system.domain.SysLogininfor;
import com.eapple.system.mapper.SysLogininforMapper;
import com.eapple.system.service.ISysLogininforService;

/**
 * 绯荤粺璁块棶鏃ュ織鎯呭喌淇℃伅 鏈嶅姟灞傚鐞?
 * 
 * @author Eapp1e
 */
@Service
public class SysLogininforServiceImpl implements ISysLogininforService
{

    @Autowired
    private SysLogininforMapper logininforMapper;

    /**
     * 鏂板绯荤粺鐧诲綍鏃ュ織
     * 
     * @param logininfor 璁块棶鏃ュ織瀵硅薄
     */
    @Override
    public void insertLogininfor(SysLogininfor logininfor)
    {
        logininforMapper.insertLogininfor(logininfor);
    }

    /**
     * 鏌ヨ绯荤粺鐧诲綍鏃ュ織闆嗗悎
     * 
     * @param logininfor 璁块棶鏃ュ織瀵硅薄
     * @return 鐧诲綍璁板綍闆嗗悎
     */
    @Override
    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor)
    {
        return logininforMapper.selectLogininforList(logininfor);
    }

    /**
     * 鎵归噺鍒犻櫎绯荤粺鐧诲綍鏃ュ織
     * 
     * @param infoIds 闇€瑕佸垹闄ょ殑鐧诲綍鏃ュ織ID
     * @return 缁撴灉
     */
    @Override
    public int deleteLogininforByIds(Long[] infoIds)
    {
        return logininforMapper.deleteLogininforByIds(infoIds);
    }

    /**
     * 娓呯┖绯荤粺鐧诲綍鏃ュ織
     */
    @Override
    public void cleanLogininfor()
    {
        logininforMapper.cleanLogininfor();
    }
}
