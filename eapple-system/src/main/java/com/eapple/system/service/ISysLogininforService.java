package com.eapple.system.service;

import java.util.List;
import com.eapple.system.domain.SysLogininfor;

/**
 * 绯荤粺璁块棶鏃ュ織鎯呭喌淇℃伅 鏈嶅姟灞?
 * 
 * @author Eapp1e
 */
public interface ISysLogininforService
{
    /**
     * 鏂板绯荤粺鐧诲綍鏃ュ織
     * 
     * @param logininfor 璁块棶鏃ュ織瀵硅薄
     */
    public void insertLogininfor(SysLogininfor logininfor);

    /**
     * 鏌ヨ绯荤粺鐧诲綍鏃ュ織闆嗗悎
     * 
     * @param logininfor 璁块棶鏃ュ織瀵硅薄
     * @return 鐧诲綍璁板綍闆嗗悎
     */
    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor);

    /**
     * 鎵归噺鍒犻櫎绯荤粺鐧诲綍鏃ュ織
     * 
     * @param infoIds 闇€瑕佸垹闄ょ殑鐧诲綍鏃ュ織ID
     * @return 缁撴灉
     */
    public int deleteLogininforByIds(Long[] infoIds);

    /**
     * 娓呯┖绯荤粺鐧诲綍鏃ュ織
     */
    public void cleanLogininfor();
}
