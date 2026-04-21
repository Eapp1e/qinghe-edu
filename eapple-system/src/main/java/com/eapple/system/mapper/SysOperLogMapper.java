package com.eapple.system.mapper;

import java.util.List;
import com.eapple.system.domain.SysOperLog;

/**
 * 鎿嶄綔鏃ュ織 鏁版嵁灞?
 * 
 * @author Eapp1e
 */
public interface SysOperLogMapper
{
    /**
     * 鏂板鎿嶄綔鏃ュ織
     * 
     * @param operLog 鎿嶄綔鏃ュ織瀵硅薄
     */
    public void insertOperlog(SysOperLog operLog);

    /**
     * 鏌ヨ绯荤粺鎿嶄綔鏃ュ織闆嗗悎
     * 
     * @param operLog 鎿嶄綔鏃ュ織瀵硅薄
     * @return 鎿嶄綔鏃ュ織闆嗗悎
     */
    public List<SysOperLog> selectOperLogList(SysOperLog operLog);

    /**
     * 鎵归噺鍒犻櫎绯荤粺鎿嶄綔鏃ュ織
     * 
     * @param operIds 闇€瑕佸垹闄ょ殑鎿嶄綔鏃ュ織ID
     * @return 缁撴灉
     */
    public int deleteOperLogByIds(Long[] operIds);

    /**
     * 鏌ヨ鎿嶄綔鏃ュ織璇︾粏
     * 
     * @param operId 鎿嶄綔ID
     * @return 鎿嶄綔鏃ュ織瀵硅薄
     */
    public SysOperLog selectOperLogById(Long operId);

    /**
     * 娓呯┖鎿嶄綔鏃ュ織
     */
    public void cleanOperLog();
}
