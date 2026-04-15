package com.eapple.common.utils.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Excel鏁版嵁鏍煎紡澶勭悊閫傞厤鍣?
 * 
 * @author Eapp1e
 */
public interface ExcelHandlerAdapter
{
    /**
     * 鏍煎紡鍖?
     * 
     * @param value 鍗曞厓鏍兼暟鎹€?
     * @param args excel娉ㄨВargs鍙傛暟缁?
     * @param cell 鍗曞厓鏍煎璞?
     * @param wb 宸ヤ綔绨垮璞?
     *
     * @return 澶勭悊鍚庣殑鍊?
     */
    Object format(Object value, String[] args, Cell cell, Workbook wb);
}
