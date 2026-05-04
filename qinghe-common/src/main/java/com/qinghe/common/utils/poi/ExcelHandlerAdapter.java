package com.qinghe.common.utils.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Excel 数据自定义处理器接口。
 *
 * @author Eapp1e
 */
public interface ExcelHandlerAdapter
{
    /**
     * 格式化单元格值。
     *
     * @param value 原始值
     * @param args Excel 注解中的 args 参数
     * @param cell 当前单元格
     * @param wb 当前工作簿
     * @return 格式化后的值
     */
    Object format(Object value, String[] args, Cell cell, Workbook wb);
}
