package com.eapple.common.utils.poi;

import java.util.ArrayList;
import java.util.List;

/**
 * 澶?Sheet 瀵煎嚭鏃剁殑鏁版嵁淇℃伅
 *
 * 浣跨敤绀轰緥锛?
 * <pre>
 *   List<ExcelSheet<?>> sheets = new ArrayList<>();
 *   sheets.add(new ExcelSheet<>("鍙傛暟鏁版嵁", configList, Config.class, "鍙傛暟淇℃伅"));
 *   sheets.add(new ExcelSheet<>("宀椾綅鏁版嵁", postList, Post.class, "宀椾綅淇℃伅"));
 *   return ExcelUtil.exportMultiSheet(sheets);
 * </pre>
 * 
 * @author Eapp1e
 */
public class ExcelSheet<T>
{
    /** Sheet 鍚嶇О */
    private String sheetName;

    /** 瀵煎嚭鏁版嵁闆嗗悎 */
    private List<T> list;

    /** 鏁版嵁瀵瑰簲鐨勫疄浣?Class */
    private Class<T> clazz;

    /** Sheet 椤堕儴澶ф爣棰橈紙鍙负绌猴級 */
    private String title;

    public ExcelSheet(String sheetName, List<T> list, Class<T> clazz)
    {
        this(sheetName, list, clazz, "");
    }

    public ExcelSheet(String sheetName, List<T> list, Class<T> clazz, String title)
    {
        this.sheetName = sheetName;
        this.list = list != null ? list : new ArrayList<>();
        this.clazz = clazz;
        this.title = title != null ? title : "";
    }

    public String getSheetName()
    {
        return sheetName;
    }

    public List<T> getList()
    {
        return list;
    }

    public Class<T> getClazz()
    {
        return clazz;
    }

    public String getTitle()
    {
        return title;
    }

    public void setSheetName(String sheetName)
    {
        this.sheetName = sheetName;
    }

    public void setList(List<T> list)
    {
        this.list = list;
    }

    public void setClazz(Class<T> clazz)
    {
        this.clazz = clazz;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
}
