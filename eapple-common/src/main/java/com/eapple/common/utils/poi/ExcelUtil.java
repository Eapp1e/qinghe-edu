package com.eapple.common.utils.poi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFPictureData;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.eapple.common.annotation.Excel;
import com.eapple.common.annotation.Excel.ColumnType;
import com.eapple.common.annotation.Excel.Type;
import com.eapple.common.annotation.Excels;
import com.eapple.common.config.PlatformConfig;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.common.core.text.Convert;
import com.eapple.common.exception.UtilException;
import com.eapple.common.utils.DateUtils;
import com.eapple.common.utils.DictUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.common.utils.file.FileTypeUtils;
import com.eapple.common.utils.file.FileUtils;
import com.eapple.common.utils.file.ImageUtils;
import com.eapple.common.utils.reflect.ReflectUtils;

/**
 * Excel鐩稿叧澶勭悊
 * 
 * @author Eapp1e
 */
public class ExcelUtil<T>
{
    private static final Logger log = LoggerFactory.getLogger(ExcelUtil.class);

    public static final String SEPARATOR = ",";

    public static final String FORMULA_REGEX_STR = "=|-|\\+|@";

    public static final String[] FORMULA_STR = { "=", "-", "+", "@" };

    /**
     * 鐢ㄤ簬dictType灞炴€ф暟鎹瓨鍌紝閬垮厤閲嶅鏌ョ紦瀛?
     */
    public Map<String, String> sysDictMap = new HashMap<String, String>();

    /**
     * 鍗曞厓鏍兼牱寮忕紦瀛?
     */
    private Map<String, CellStyle> cellStyleCache = new HashMap<String, CellStyle>();

    /**
     * Excel sheet鏈€澶ц鏁帮紝榛樿65536
     */
    public static final int sheetSize = 65536;

    /**
     * 宸ヤ綔琛ㄥ悕绉?
     */
    private String sheetName;

    /**
     * 瀵煎嚭绫诲瀷锛圗XPORT:瀵煎嚭鏁版嵁锛汭MPORT锛氬鍏ユā鏉匡級
     */
    private Type type;

    /**
     * 宸ヤ綔钖勫璞?
     */
    private Workbook wb;

    /**
     * 宸ヤ綔琛ㄥ璞?
     */
    private Sheet sheet;

    /**
     * 鏍峰紡鍒楄〃
     */
    private Map<String, CellStyle> styles;

    /**
     * 瀵煎叆瀵煎嚭鏁版嵁鍒楄〃
     */
    private List<T> list;

    /**
     * 娉ㄨВ鍒楄〃
     */
    private List<Object[]> fields;

    /**
     * 褰撳墠琛屽彿
     */
    private int rownum;

    /**
     * 鏍囬
     */
    private String title;

    /**
     * 鏈€澶ч珮搴?
     */
    private short maxHeight;

    /**
     * 鍚堝苟鍚庢渶鍚庤鏁?
     */
    private int subMergedLastRowNum = 0;

    /**
     * 鍚堝苟鍚庡紑濮嬭鏁?
     */
    private int subMergedFirstRowNum = 1;

    /**
     * 瀵硅薄鐨勫瓙鍒楄〃鏂规硶
     */
    private Map<String, Method> subMethods;

    /**
     * 瀵硅薄鐨勫瓙鍒楄〃灞炴€?
     */
    private Map<String, List<Field>> subFieldsMap;

    /**
     * 缁熻鍒楄〃
     */
    private Map<Integer, Double> statistics = new HashMap<Integer, Double>();

    /**
     * 瀹炰綋瀵硅薄
     */
    public Class<T> clazz;

    /**
     * 闇€瑕佹樉绀哄垪灞炴€?
     */
    public String[] includeFields;

    /**
     * 闇€瑕佹帓闄ゅ垪灞炴€?
     */
    public String[] excludeFields;

    public ExcelUtil(Class<T> clazz)
    {
        this.clazz = clazz;
    }

    /**
     * 浠呭湪Excel涓樉绀哄垪灞炴€?
     *
     * @param fields 鍒楀睘鎬у悕 绀轰緥[鍗曚釜"name"/澶氫釜"id","name"]
     */
    public void showColumn(String... fields)
    {
        this.includeFields = fields;
    }

    /**
     * 闅愯棌Excel涓垪灞炴€?
     *
     * @param fields 鍒楀睘鎬у悕 绀轰緥[鍗曚釜"name"/澶氫釜"id","name"]
     */
    public void hideColumn(String... fields)
    {
        this.excludeFields = fields;
    }

    public void init(List<T> list, String sheetName, String title, Type type)
    {
        if (list == null)
        {
            list = new ArrayList<T>();
        }
        this.list = list;
        this.sheetName = sheetName;
        this.type = type;
        this.title = title;
        createExcelField();
        createWorkbook();
        createTitle();
        createSubHead();
    }

    /**
     * 鍒涘缓excel绗竴琛屾爣棰?
     */
    public void createTitle()
    {
        if (StringUtils.isNotEmpty(title))
        {
            int titleLastCol = this.fields.size() - 1;
            if (isSubList())
            {
                for (List<Field> currentSubFields : subFieldsMap.values())
                {
                    titleLastCol = titleLastCol + currentSubFields.size() - 1;
                }
            }
            Row titleRow = sheet.createRow(rownum == 0 ? rownum++ : 0);
            titleRow.setHeightInPoints(30);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellStyle(styles.get("title"));
            titleCell.setCellValue(title);
            sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(), titleRow.getRowNum(), 0, titleLastCol));
        }
    }

    /**
     * 鍒涘缓瀵硅薄鐨勫瓙鍒楄〃鍚嶇О
     */
    public void createSubHead()
    {
        if (isSubList())
        {
            Row subRow = sheet.createRow(rownum);
            int column = 0;
            for (Object[] objects : fields)
            {
                Field field = (Field) objects[0];
                Excel attr = (Excel) objects[1];
                CellStyle cellStyle = styles.get(StringUtils.format("header_{}_{}", attr.headerColor(), attr.headerBackgroundColor()));
                if (Collection.class.isAssignableFrom(field.getType()))
                {
                    Cell cell = subRow.createCell(column);
                    cell.setCellValue(attr.name());
                    cell.setCellStyle(cellStyle);
                    int subFieldSize = subFieldsMap != null ? subFieldsMap.get(field.getName()).size() : 0;
                    if (subFieldSize > 1)
                    {
                        CellRangeAddress cellAddress = new CellRangeAddress(rownum, rownum, column, column + subFieldSize - 1);
                        sheet.addMergedRegion(cellAddress);
                    }
                    column += subFieldSize;
                }
                else
                {
                    Cell cell = subRow.createCell(column++);
                    cell.setCellValue(attr.name());
                    cell.setCellStyle(cellStyle);
                }
            }
            rownum++;
        }
    }

    /**
     * 瀵筫xcel琛ㄥ崟榛樿绗竴涓储寮曞悕杞崲鎴恖ist
     * 
     * @param is 杈撳叆娴?
     * @return 杞崲鍚庨泦鍚?
     */
    public List<T> importExcel(InputStream is)
    {
        return importExcel(is, 0);
    }

    /**
     * 瀵筫xcel琛ㄥ崟榛樿绗竴涓储寮曞悕杞崲鎴恖ist
     * 
     * @param is 杈撳叆娴?
     * @param titleNum 鏍囬鍗犵敤琛屾暟
     * @return 杞崲鍚庨泦鍚?
     */
    public List<T> importExcel(InputStream is, int titleNum)
    {
        List<T> list = null;
        try
        {
            list = importExcel(StringUtils.EMPTY, is, titleNum);
        }
        catch (Exception e)
        {
            log.error("瀵煎叆Excel寮傚父{}", e.getMessage());
            throw new UtilException(e.getMessage());
        }
        finally
        {
            IOUtils.closeQuietly(is);
        }
        return list;
    }

    /**
     * 瀵筫xcel琛ㄥ崟鎸囧畾琛ㄦ牸绱㈠紩鍚嶈浆鎹㈡垚list
     * 
     * @param sheetName 琛ㄦ牸绱㈠紩鍚?
     * @param titleNum 鏍囬鍗犵敤琛屾暟
     * @param is 杈撳叆娴?
     * @return 杞崲鍚庨泦鍚?
     */
    public List<T> importExcel(String sheetName, InputStream is, int titleNum) throws Exception
    {
        this.type = Type.IMPORT;
        this.wb = WorkbookFactory.create(is);
        List<T> list = new ArrayList<T>();
        // 濡傛灉鎸囧畾sheet鍚?鍒欏彇鎸囧畾sheet涓殑鍐呭 鍚﹀垯榛樿鎸囧悜绗?涓猻heet
        Sheet sheet = StringUtils.isNotEmpty(sheetName) ? wb.getSheet(sheetName) : wb.getSheetAt(0);
        if (sheet == null)
        {
            throw new IOException("鏂囦欢sheet涓嶅瓨鍦?);
        }
        boolean isXSSFWorkbook = !(wb instanceof HSSFWorkbook);
        Map<String, List<PictureData>> pictures = null;
        if (isXSSFWorkbook)
        {
            pictures = getSheetPictures07((XSSFSheet) sheet, (XSSFWorkbook) wb);
        }
        else
        {
            pictures = getSheetPictures03((HSSFSheet) sheet, (HSSFWorkbook) wb);
        }
        // 鑾峰彇鏈€鍚庝竴涓潪绌鸿鐨勮涓嬫爣锛屾瘮濡傛€昏鏁颁负n锛屽垯杩斿洖鐨勪负n-1
        int rows = sheet.getLastRowNum();
        if (rows > 0)
        {
            // 瀹氫箟涓€涓猰ap鐢ㄤ簬瀛樻斁excel鍒楃殑搴忓彿鍜宖ield.
            Map<String, Integer> cellMap = new HashMap<String, Integer>();
            // 鑾峰彇琛ㄥご
            Row heard = sheet.getRow(titleNum);
            if (heard == null)
            {
                throw new UtilException("鏂囦欢鏍囬琛屼负绌猴紝璇锋鏌xcel鏂囦欢鏍煎紡");
            }
            for (int i = 0; i < heard.getLastCellNum(); i++)
            {
                Cell cell = heard.getCell(i);
                if (StringUtils.isNotNull(cell))
                {
                    String value = this.getCellValue(heard, i).toString();
                    cellMap.put(value, i);
                }
            }
            // 鏈夋暟鎹椂鎵嶅鐞?寰楀埌绫荤殑鎵€鏈塮ield.
            List<Object[]> fields = this.getFields();
            Map<Integer, Object[]> fieldsMap = new HashMap<Integer, Object[]>();
            for (Object[] objects : fields)
            {
                Excel attr = (Excel) objects[1];
                Integer column = cellMap.get(attr.name());
                if (column != null)
                {
                    fieldsMap.put(column, objects);
                }
            }
            for (int i = titleNum + 1; i <= rows; i++)
            {
                // 浠庣2琛屽紑濮嬪彇鏁版嵁,榛樿绗竴琛屾槸琛ㄥご.
                Row row = sheet.getRow(i);
                // 鍒ゆ柇褰撳墠琛屾槸鍚︽槸绌鸿
                if (isRowEmpty(row))
                {
                    continue;
                }
                T entity = null;
                for (Map.Entry<Integer, Object[]> entry : fieldsMap.entrySet())
                {
                    Object val = this.getCellValue(row, entry.getKey());

                    // 濡傛灉涓嶅瓨鍦ㄥ疄渚嬪垯鏂板缓.
                    entity = (entity == null ? clazz.getDeclaredConstructor().newInstance() : entity);
                    // 浠巑ap涓緱鍒板搴斿垪鐨刦ield.
                    Field field = (Field) entry.getValue()[0];
                    Excel attr = (Excel) entry.getValue()[1];
                    // 鍙栧緱绫诲瀷,骞舵牴鎹璞＄被鍨嬭缃€?
                    Class<?> fieldType = field.getType();
                    if (String.class == fieldType)
                    {
                        String s = Convert.toStr(val);
                        if (s.matches("^\\d+\\.0$"))
                        {
                            val = StringUtils.substringBefore(s, ".0");
                        }
                        else
                        {
                            String dateFormat = field.getAnnotation(Excel.class).dateFormat();
                            if (StringUtils.isNotEmpty(dateFormat))
                            {
                                val = parseDateToStr(dateFormat, val);
                            }
                            else
                            {
                                val = Convert.toStr(val);
                            }
                        }
                    }
                    else if ((Integer.TYPE == fieldType || Integer.class == fieldType) && StringUtils.isNumeric(Convert.toStr(val)))
                    {
                        val = Convert.toInt(val);
                    }
                    else if ((Long.TYPE == fieldType || Long.class == fieldType) && StringUtils.isNumeric(Convert.toStr(val)))
                    {
                        val = Convert.toLong(val);
                    }
                    else if (Double.TYPE == fieldType || Double.class == fieldType)
                    {
                        val = Convert.toDouble(val);
                    }
                    else if (Float.TYPE == fieldType || Float.class == fieldType)
                    {
                        val = Convert.toFloat(val);
                    }
                    else if (BigDecimal.class == fieldType)
                    {
                        val = Convert.toBigDecimal(val);
                    }
                    else if (Date.class == fieldType)
                    {
                        if (val instanceof String)
                        {
                            val = DateUtils.parseDate(val);
                        }
                        else if (val instanceof Double)
                        {
                            val = DateUtil.getJavaDate((Double) val);
                        }
                    }
                    else if (Boolean.TYPE == fieldType || Boolean.class == fieldType)
                    {
                        val = Convert.toBool(val, false);
                    }
                    if (StringUtils.isNotNull(fieldType))
                    {
                        String propertyName = field.getName();
                        if (StringUtils.isNotEmpty(attr.targetAttr()))
                        {
                            propertyName = field.getName() + "." + attr.targetAttr();
                        }
                        if (StringUtils.isNotEmpty(attr.readConverterExp()))
                        {
                            val = reverseByExp(Convert.toStr(val), attr.readConverterExp(), attr.separator());
                        }
                        else if (StringUtils.isNotEmpty(attr.dictType()))
                        {
                            if (!sysDictMap.containsKey(attr.dictType() + val))
                            {
                                String dictValue = reverseDictByExp(Convert.toStr(val), attr.dictType(), attr.separator());
                                sysDictMap.put(attr.dictType() + val, dictValue);
                            }
                            val = sysDictMap.get(attr.dictType() + val);
                        }
                        else if (!attr.handler().equals(ExcelHandlerAdapter.class))
                        {
                            val = dataFormatHandlerAdapter(val, attr, null);
                        }
                        else if (ColumnType.IMAGE == attr.cellType() && StringUtils.isNotEmpty(pictures))
                        {
                            StringBuilder propertyString = new StringBuilder();
                            List<PictureData> images = pictures.get(row.getRowNum() + "_" + entry.getKey());
                            for (PictureData picture : images)
                            {
                                byte[] data = picture.getData();
                                String fileName = FileUtils.writeImportBytes(data);
                                propertyString.append(fileName).append(SEPARATOR);
                            }
                            val = StringUtils.stripEnd(propertyString.toString(), SEPARATOR);
                        }
                        ReflectUtils.invokeSetter(entity, propertyName, val);
                    }
                }
                list.add(entity);
            }
        }
        return list;
    }

    /**
     * 瀵筶ist鏁版嵁婧愬皢鍏堕噷闈㈢殑鏁版嵁瀵煎叆鍒癳xcel琛ㄥ崟
     * 
     * @param list 瀵煎嚭鏁版嵁闆嗗悎
     * @param sheetName 宸ヤ綔琛ㄧ殑鍚嶇О
     * @return 缁撴灉
     */
    public AjaxResult exportExcel(List<T> list, String sheetName)
    {
        return exportExcel(list, sheetName, StringUtils.EMPTY);
    }

    /**
     * 瀵筶ist鏁版嵁婧愬皢鍏堕噷闈㈢殑鏁版嵁瀵煎叆鍒癳xcel琛ㄥ崟
     * 
     * @param list 瀵煎嚭鏁版嵁闆嗗悎
     * @param sheetName 宸ヤ綔琛ㄧ殑鍚嶇О
     * @param title 鏍囬
     * @return 缁撴灉
     */
    public AjaxResult exportExcel(List<T> list, String sheetName, String title)
    {
        this.init(list, sheetName, title, Type.EXPORT);
        return exportExcel();
    }

    /**
     * 瀵筶ist鏁版嵁婧愬皢鍏堕噷闈㈢殑鏁版嵁瀵煎叆鍒癳xcel琛ㄥ崟
     * 
     * @param response 杩斿洖鏁版嵁
     * @param list 瀵煎嚭鏁版嵁闆嗗悎
     * @param sheetName 宸ヤ綔琛ㄧ殑鍚嶇О
     * @return 缁撴灉
     */
    public void exportExcel(HttpServletResponse response, List<T> list, String sheetName)
    {
        exportExcel(response, list, sheetName, StringUtils.EMPTY);
    }

    /**
     * 瀵筶ist鏁版嵁婧愬皢鍏堕噷闈㈢殑鏁版嵁瀵煎叆鍒癳xcel琛ㄥ崟
     * 
     * @param response 杩斿洖鏁版嵁
     * @param list 瀵煎嚭鏁版嵁闆嗗悎
     * @param sheetName 宸ヤ綔琛ㄧ殑鍚嶇О
     * @param title 鏍囬
     * @return 缁撴灉
     */
    public void exportExcel(HttpServletResponse response, List<T> list, String sheetName, String title)
    {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        this.init(list, sheetName, title, Type.EXPORT);
        exportExcel(response);
    }

    /**
     * 澶?Sheet 瀵煎嚭 鈥斺€?灏嗗涓笉鍚岀被鍨嬬殑鏁版嵁闆嗗悎鍐欏叆鍚屼竴 Excel锛岀洿鎺ヨ緭鍑哄埌 HttpServletResponse
     *
     * @param response HTTP 鍝嶅簲
     * @param sheets   Sheet 鎻忚堪鍒楄〃
     */
    public static void exportMultiSheet(HttpServletResponse response, List<ExcelSheet<?>> sheets)
    {
        if (sheets == null || sheets.isEmpty())
        {
            return;
        }
        SXSSFWorkbook wb = buildWorkbook(sheets);
        try
        {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            wb.write(response.getOutputStream());
        }
        catch (Exception e)
        {
            log.error("澶歋heet瀵煎嚭Excel寮傚父{}", e.getMessage());
        }
        finally
        {
            IOUtils.closeQuietly(wb);
        }
    }

    /**
     * 澶?Sheet 瀵煎嚭 鈥斺€?灏嗗涓笉鍚岀被鍨嬬殑鏁版嵁闆嗗悎鍐欏叆鍚屼竴 Excel锛岀敓鎴愭枃浠跺苟杩斿洖涓嬭浇鍦板潃
     *
     * @param sheets Sheet 鎻忚堪鍒楄〃
     * @return AjaxResult锛堝惈鏂囦欢涓嬭浇鍦板潃锛?
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static AjaxResult exportMultiSheet(List<ExcelSheet<?>> sheets)
    {
        if (sheets == null || sheets.isEmpty())
        {
            return AjaxResult.error("瀵煎嚭鏁版嵁涓嶈兘涓虹┖");
        }
        SXSSFWorkbook wb = buildWorkbook(sheets);
        OutputStream out = null;
        try
        {
            ExcelUtil firstUtil = new ExcelUtil(sheets.get(0).getClazz());
            String filename = firstUtil.encodingFilename(sheets.get(0).getSheetName());
            out = new FileOutputStream(firstUtil.getAbsoluteFile(filename));
            wb.write(out);
            return AjaxResult.success(filename);
        }
        catch (Exception e)
        {
            log.error("澶歋heet瀵煎嚭Excel寮傚父{}", e.getMessage());
            throw new UtilException("瀵煎嚭Excel澶辫触锛岃鑱旂郴缃戠珯绠＄悊鍛橈紒");
        }
        finally
        {
            IOUtils.closeQuietly(wb);
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * 鏋勫缓澶?Sheet Workbook 鈥斺€?鍒涘缓 SXSSFWorkbook 骞跺皢鎵€鏈?Sheet 鏁版嵁鍐欏叆
     *
     * @param sheets Sheet 鎻忚堪鍒楄〃
     * @return 宸插啓鍏ユ墍鏈?Sheet 鏁版嵁鐨?SXSSFWorkbook
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static SXSSFWorkbook buildWorkbook(List<ExcelSheet<?>> sheets)
    {
        SXSSFWorkbook wb = new SXSSFWorkbook(500);
        for (ExcelSheet<?> excelSheet : sheets)
        {
            ExcelUtil util = new ExcelUtil(excelSheet.getClazz());
            util.initWithWorkbook(wb, excelSheet.getList(), excelSheet.getSheetName(), excelSheet.getTitle());
            util.writeSheet();
        }
        return wb;
    }

    /**
     * 浣跨敤澶栭儴浼犲叆鐨?Workbook 鍒濆鍖栵紙澶?Sheet 瀵煎嚭涓撶敤锛?
     * 涓?init() 鐨勫尯鍒細涓嶆柊寤?Workbook锛岃€屾槸鍦ㄥ凡鏈?wb 涓婅拷鍔犳柊 Sheet
     *
     * @param wb        宸叉湁宸ヤ綔绨?
     * @param list      鏁版嵁闆嗗悎
     * @param sheetName Sheet 鍚嶇О
     * @param title     澶ф爣棰橈紙鍙负绌猴級
     */
    public void initWithWorkbook(SXSSFWorkbook wb, List<T> list, String sheetName, String title)
    {
        if (list == null)
        {
            list = new ArrayList<T>();
        }
        this.list      = list;
        this.sheetName = sheetName;
        this.title     = title != null ? title : "";
        this.type      = Type.EXPORT;
        this.rownum    = 0;
        this.wb        = wb;
        this.sheet     = wb.createSheet(sheetName);
        createExcelField();
        this.styles    = createStyles(wb);
        createTitle();
        createSubHead();
    }

    /**
     * 瀵筶ist鏁版嵁婧愬皢鍏堕噷闈㈢殑鏁版嵁瀵煎叆鍒癳xcel琛ㄥ崟
     * 
     * @param sheetName 宸ヤ綔琛ㄧ殑鍚嶇О
     * @return 缁撴灉
     */
    public AjaxResult importTemplateExcel(String sheetName)
    {
        return importTemplateExcel(sheetName, StringUtils.EMPTY);
    }

    /**
     * 瀵筶ist鏁版嵁婧愬皢鍏堕噷闈㈢殑鏁版嵁瀵煎叆鍒癳xcel琛ㄥ崟
     * 
     * @param sheetName 宸ヤ綔琛ㄧ殑鍚嶇О
     * @param title 鏍囬
     * @return 缁撴灉
     */
    public AjaxResult importTemplateExcel(String sheetName, String title)
    {
        this.init(null, sheetName, title, Type.IMPORT);
        return exportExcel();
    }

    /**
     * 瀵筶ist鏁版嵁婧愬皢鍏堕噷闈㈢殑鏁版嵁瀵煎叆鍒癳xcel琛ㄥ崟
     * 
     * @param sheetName 宸ヤ綔琛ㄧ殑鍚嶇О
     * @return 缁撴灉
     */
    public void importTemplateExcel(HttpServletResponse response, String sheetName)
    {
        importTemplateExcel(response, sheetName, StringUtils.EMPTY);
    }

    /**
     * 瀵筶ist鏁版嵁婧愬皢鍏堕噷闈㈢殑鏁版嵁瀵煎叆鍒癳xcel琛ㄥ崟
     * 
     * @param sheetName 宸ヤ綔琛ㄧ殑鍚嶇О
     * @param title 鏍囬
     * @return 缁撴灉
     */
    public void importTemplateExcel(HttpServletResponse response, String sheetName, String title)
    {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        this.init(null, sheetName, title, Type.IMPORT);
        exportExcel(response);
    }

    /**
     * 瀵筶ist鏁版嵁婧愬皢鍏堕噷闈㈢殑鏁版嵁瀵煎叆鍒癳xcel琛ㄥ崟
     * 
     * @return 缁撴灉
     */
    public void exportExcel(HttpServletResponse response)
    {
        try
        {
            writeSheet();
            wb.write(response.getOutputStream());
        }
        catch (Exception e)
        {
            log.error("瀵煎嚭Excel寮傚父{}", e.getMessage());
        }
        finally
        {
            IOUtils.closeQuietly(wb);
        }
    }

    /**
     * 瀵筶ist鏁版嵁婧愬皢鍏堕噷闈㈢殑鏁版嵁瀵煎叆鍒癳xcel琛ㄥ崟
     * 
     * @return 缁撴灉
     */
    public AjaxResult exportExcel()
    {
        OutputStream out = null;
        try
        {
            writeSheet();
            String filename = encodingFilename(sheetName);
            out = new FileOutputStream(getAbsoluteFile(filename));
            wb.write(out);
            return AjaxResult.success(filename);
        }
        catch (Exception e)
        {
            log.error("瀵煎嚭Excel寮傚父{}", e.getMessage());
            throw new UtilException("瀵煎嚭Excel澶辫触锛岃鑱旂郴缃戠珯绠＄悊鍛橈紒");
        }
        finally
        {
            IOUtils.closeQuietly(wb);
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * 鍒涘缓鍐欏叆鏁版嵁鍒癝heet
     */
    public void writeSheet()
    {
        // 鍙栧嚭涓€鍏辨湁澶氬皯涓猻heet.
        int sheetNo = Math.max(1, (int) Math.ceil(list.size() * 1.0 / sheetSize));
        for (int index = 0; index < sheetNo; index++)
        {
            createSheet(sheetNo, index);

            // 浜х敓涓€琛?
            Row row = sheet.createRow(rownum);
            int column = 0;
            // 鍐欏叆鍚勪釜瀛楁鐨勫垪澶村悕绉?
            for (Object[] os : fields)
            {
                Field field = (Field) os[0];
                Excel excel = (Excel) os[1];
                if (Collection.class.isAssignableFrom(field.getType()))
                {
                    List<Field> currentSubFields = subFieldsMap.get(field.getName());
                    for (Field subField : currentSubFields)
                    {
                        Excel subExcel = subField.getAnnotation(Excel.class);
                        this.createHeadCell(subExcel, row, column++);
                    }
                }
                else
                {
                    this.createHeadCell(excel, row, column++);
                }
            }
            if (Type.EXPORT.equals(type))
            {
                fillExcelData(index);
                addStatisticsRow();
            }
        }
    }

    /**
     * 濉厖excel鏁版嵁
     * 
     * @param index 搴忓彿
     */
    @SuppressWarnings("unchecked")
    public void fillExcelData(int index)
    {
        int startNo = index * sheetSize;
        int endNo = Math.min(startNo + sheetSize, list.size());
        int currentRowNum = rownum + 1; // 浠庢爣棰樿鍚庡紑濮?

        for (int i = startNo; i < endNo; i++)
        {
            Row row = sheet.createRow(currentRowNum);
            T vo = (T) list.get(i);
            int column = 0;
            int maxSubListSize = getCurrentMaxSubListSize(vo);
            for (Object[] os : fields)
            {
                Field field = (Field) os[0];
                Excel excel = (Excel) os[1];
                if (Collection.class.isAssignableFrom(field.getType()))
                {
                    try
                    {
                        Collection<?> subList = (Collection<?>) getTargetValue(vo, field, excel);
                        List<Field> currentSubFields = subFieldsMap.get(field.getName());
                        if (subList != null && !subList.isEmpty())
                        {
                            int subIndex = 0;
                            for (Object subVo : subList)
                            {
                                Row subRow = sheet.getRow(currentRowNum + subIndex);
                                if (subRow == null)
                                {
                                    subRow = sheet.createRow(currentRowNum + subIndex);
                                }

                                int subColumn = column;
                                for (Field subField : currentSubFields)
                                {
                                    Excel subExcel = subField.getAnnotation(Excel.class);
                                    addCell(subExcel, subRow, (T) subVo, subField, subColumn++);
                                }
                                subIndex++;
                            }
                        }
                        column += currentSubFields.size();
                    }
                    catch (Exception e)
                    {
                        log.error("濉厖闆嗗悎鏁版嵁澶辫触", e);
                    }
                }
                else
                {
                    // 鍒涘缓鍗曞厓鏍煎苟璁剧疆鍊?
                    addCell(excel, row, vo, field, column);
                    if (maxSubListSize > 1 && excel.needMerge())
                    {
                        sheet.addMergedRegion(new CellRangeAddress(currentRowNum, currentRowNum + maxSubListSize - 1, column, column));
                    }
                    column++;
                }
            }
            currentRowNum += maxSubListSize;
        }
    }

    /**
     * 鑾峰彇瀛愬垪琛ㄦ渶澶ф暟
     */
    private int getCurrentMaxSubListSize(T vo)
    {
        int maxSubListSize = 1;
        for (Object[] os : fields)
        {
            Field field = (Field) os[0];
            if (Collection.class.isAssignableFrom(field.getType()))
            {
                try
                {
                    Collection<?> subList = (Collection<?>) getTargetValue(vo, field, (Excel) os[1]);
                    if (subList != null && !subList.isEmpty())
                    {
                        maxSubListSize = Math.max(maxSubListSize, subList.size());
                    }
                }
                catch (Exception e)
                {
                    log.error("鑾峰彇闆嗗悎澶у皬澶辫触", e);
                }
            }
        }
        return maxSubListSize;
    }

    /**
     * 鍒涘缓琛ㄦ牸鏍峰紡
     * 
     * @param wb 宸ヤ綔钖勫璞?
     * @return 鏍峰紡鍒楄〃
     */
    private Map<String, CellStyle> createStyles(Workbook wb)
    {
        // 鍐欏叆鍚勬潯璁板綍,姣忔潯璁板綍瀵瑰簲excel琛ㄤ腑鐨勪竴琛?
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font titleFont = wb.createFont();
        titleFont.setFontName("Arial");
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBold(true);
        style.setFont(titleFont);
        DataFormat dataFormat = wb.createDataFormat();
        style.setDataFormat(dataFormat.getFormat("@"));
        styles.put("title", style);

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        Font dataFont = wb.createFont();
        dataFont.setFontName("Arial");
        dataFont.setFontHeightInPoints((short) 10);
        style.setFont(dataFont);
        styles.put("data", style);

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setDataFormat(dataFormat.getFormat("######0.00"));
        Font totalFont = wb.createFont();
        totalFont.setFontName("Arial");
        totalFont.setFontHeightInPoints((short) 10);
        style.setFont(totalFont);
        styles.put("total", style);

        styles.putAll(annotationHeaderStyles(wb, styles));

        styles.putAll(annotationDataStyles(wb));

        return styles;
    }

    /**
     * 鏍规嵁Excel娉ㄨВ鍒涘缓琛ㄦ牸澶存牱寮?
     * 
     * @param wb 宸ヤ綔钖勫璞?
     * @return 鑷畾涔夋牱寮忓垪琛?
     */
    private Map<String, CellStyle> annotationHeaderStyles(Workbook wb, Map<String, CellStyle> styles)
    {
        Map<String, CellStyle> headerStyles = new HashMap<String, CellStyle>();
        for (Object[] os : fields)
        {
            Excel excel = (Excel) os[1];
            String key = StringUtils.format("header_{}_{}", excel.headerColor(), excel.headerBackgroundColor());
            if (!headerStyles.containsKey(key))
            {
                CellStyle style = wb.createCellStyle();
                style.cloneStyleFrom(styles.get("data"));
                style.setAlignment(HorizontalAlignment.CENTER);
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                style.setFillForegroundColor(excel.headerBackgroundColor().index);
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                Font headerFont = wb.createFont();
                headerFont.setFontName("Arial");
                headerFont.setFontHeightInPoints((short) 10);
                headerFont.setBold(true);
                headerFont.setColor(excel.headerColor().index);
                style.setFont(headerFont);
                // 璁剧疆琛ㄦ牸澶村崟鍏冩牸鏂囨湰褰㈠紡
                DataFormat dataFormat = wb.createDataFormat();
                style.setDataFormat(dataFormat.getFormat("@"));
                headerStyles.put(key, style);
            }
        }
        return headerStyles;
    }

    /**
     * 鏍规嵁Excel娉ㄨВ鍒涘缓琛ㄦ牸鍒楁牱寮?
     * 
     * @param wb 宸ヤ綔钖勫璞?
     * @return 鑷畾涔夋牱寮忓垪琛?
     */
    private Map<String, CellStyle> annotationDataStyles(Workbook wb)
    {
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        for (Object[] os : fields)
        {
            Field field = (Field) os[0];
            Excel excel = (Excel) os[1];
            if (Collection.class.isAssignableFrom(field.getType()))
            {
                ParameterizedType pt = (ParameterizedType) field.getGenericType();
                Class<?> subClass = (Class<?>) pt.getActualTypeArguments()[0];
                List<Field> subFields = FieldUtils.getFieldsListWithAnnotation(subClass, Excel.class);
                for (Field subField : subFields)
                {
                    Excel subExcel = subField.getAnnotation(Excel.class);
                    annotationDataStyles(styles, subField, subExcel);
                }
            }
            else
            {
                annotationDataStyles(styles, field, excel);
            }
        }
        return styles;
    }

    /**
     * 鏍规嵁Excel娉ㄨВ鍒涘缓琛ㄦ牸鍒楁牱寮?
     * 
     * @param styles 鑷畾涔夋牱寮忓垪琛?
     * @param field  灞炴€у垪淇℃伅
     * @param excel  娉ㄨВ淇℃伅
     */
    public void annotationDataStyles(Map<String, CellStyle> styles, Field field, Excel excel)
    {
        String key = StringUtils.format("data_{}_{}_{}_{}_{}", excel.align(), excel.color(), excel.backgroundColor(), excel.cellType(), excel.wrapText());
        if (!styles.containsKey(key))
        {
            CellStyle style = wb.createCellStyle();
            style.setAlignment(excel.align());
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setBorderRight(BorderStyle.THIN);
            style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style.setBorderLeft(BorderStyle.THIN);
            style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style.setBorderTop(BorderStyle.THIN);
            style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style.setBorderBottom(BorderStyle.THIN);
            style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setFillForegroundColor(excel.backgroundColor().getIndex());
            style.setWrapText(excel.wrapText());
            Font dataFont = wb.createFont();
            dataFont.setFontName("Arial");
            dataFont.setFontHeightInPoints((short) 10);
            dataFont.setColor(excel.color().index);
            style.setFont(dataFont);
            if (ColumnType.TEXT == excel.cellType())
            {
                DataFormat dataFormat = wb.createDataFormat();
                style.setDataFormat(dataFormat.getFormat("@"));
            }
            styles.put(key, style);
        }
    }

    /**
     * 鍒涘缓鍗曞厓鏍?
     */
    public Cell createHeadCell(Excel attr, Row row, int column)
    {
        // 鍒涘缓鍒?
        Cell cell = row.createCell(column);
        // 鍐欏叆鍒椾俊鎭?
        cell.setCellValue(attr.name());
        setDataValidation(attr, row, column);
        cell.setCellStyle(styles.get(StringUtils.format("header_{}_{}", attr.headerColor(), attr.headerBackgroundColor())));
        if (isSubList())
        {
            // 濉厖榛樿鏍峰紡锛岄槻姝㈠悎骞跺崟鍏冩牸鏍峰紡澶辨晥
            sheet.setDefaultColumnStyle(column, styles.get(StringUtils.format("data_{}_{}_{}_{}_{}", attr.align(), attr.color(), attr.backgroundColor(), attr.cellType(), attr.wrapText())));
            if (attr.needMerge())
            {
                sheet.addMergedRegion(new CellRangeAddress(rownum - 1, rownum, column, column));
            }
        }
        return cell;
    }

    /**
     * 璁剧疆鍗曞厓鏍间俊鎭?
     * 
     * @param value 鍗曞厓鏍煎€?
     * @param attr 娉ㄨВ鐩稿叧
     * @param cell 鍗曞厓鏍间俊鎭?
     */
    public void setCellVo(Object value, Excel attr, Cell cell)
    {
        if (ColumnType.STRING == attr.cellType() || ColumnType.TEXT == attr.cellType())
        {
            String cellValue = Convert.toStr(value);
            // 瀵逛簬浠讳綍浠ヨ〃杈惧紡瑙﹀彂瀛楃 =-+@寮€澶寸殑鍗曞厓鏍硷紝鐩存帴浣跨敤tab瀛楃浣滀负鍓嶇紑锛岄槻姝SV娉ㄥ叆銆?
            if (StringUtils.startsWithAny(cellValue, FORMULA_STR))
            {
                cellValue = RegExUtils.replaceFirst(cellValue, FORMULA_REGEX_STR, "\t$0");
            }
            if (value instanceof Collection && StringUtils.equals("[]", cellValue))
            {
                cellValue = StringUtils.EMPTY;
            }
            cell.setCellValue(StringUtils.isNull(cellValue) ? attr.defaultValue() : cellValue + attr.suffix());
        }
        else if (ColumnType.NUMERIC == attr.cellType())
        {
            if (StringUtils.isNotNull(value))
            {
                cell.setCellValue(StringUtils.contains(Convert.toStr(value), ".") ? Convert.toDouble(value) : Convert.toInt(value));
            }
        }
        else if (ColumnType.IMAGE == attr.cellType())
        {
            ClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, (short) cell.getColumnIndex(), cell.getRow().getRowNum(), (short) (cell.getColumnIndex() + 1), cell.getRow().getRowNum() + 1);
            String propertyValue = Convert.toStr(value);
            if (StringUtils.isNotEmpty(propertyValue))
            {
                List<String> imagePaths = StringUtils.str2List(propertyValue, SEPARATOR);
                for (String imagePath : imagePaths)
                {
                    byte[] data = ImageUtils.getImage(imagePath);
                    getDrawingPatriarch(cell.getSheet()).createPicture(anchor, cell.getSheet().getWorkbook().addPicture(data, getImageType(data)));
                }
            }
        }
    }

    /**
     * 鑾峰彇鐢诲竷
     */
    public static Drawing<?> getDrawingPatriarch(Sheet sheet)
    {
        if (sheet.getDrawingPatriarch() == null)
        {
            sheet.createDrawingPatriarch();
        }
        return sheet.getDrawingPatriarch();
    }

    /**
     * 鑾峰彇鍥剧墖绫诲瀷,璁剧疆鍥剧墖鎻掑叆绫诲瀷
     */
    public int getImageType(byte[] value)
    {
        String type = FileTypeUtils.getFileExtendName(value);
        if ("JPG".equalsIgnoreCase(type))
        {
            return Workbook.PICTURE_TYPE_JPEG;
        }
        else if ("PNG".equalsIgnoreCase(type))
        {
            return Workbook.PICTURE_TYPE_PNG;
        }
        return Workbook.PICTURE_TYPE_JPEG;
    }

    /**
     * 鍒涘缓琛ㄦ牸鏍峰紡
     */
    public void setDataValidation(Excel attr, Row row, int column)
    {
        if (attr.name().indexOf("娉細") >= 0)
        {
            sheet.setColumnWidth(column, 6000);
        }
        else
        {
            // 璁剧疆鍒楀
            sheet.setColumnWidth(column, (int) ((attr.width() + 0.72) * 256));
        }
        if (StringUtils.isNotEmpty(attr.prompt()) || attr.combo().length > 0 || attr.comboReadDict())
        {
            String[] comboArray = attr.combo();
            if (attr.comboReadDict())
            {
                if (!sysDictMap.containsKey("combo_" + attr.dictType()))
                {
                    String labels = DictUtils.getDictLabels(attr.dictType());
                    sysDictMap.put("combo_" + attr.dictType(), labels);
                }
                String val = sysDictMap.get("combo_" + attr.dictType());
                comboArray = StringUtils.split(val, DictUtils.SEPARATOR);
            }
            if (comboArray.length > 15 || StringUtils.join(comboArray).length() > 255)
            {
                // 濡傛灉涓嬫媺鏁板ぇ浜?5鎴栧瓧绗︿覆闀垮害澶т簬255锛屽垯浣跨敤涓€涓柊sheet瀛樺偍锛岄伩鍏嶇敓鎴愮殑妯℃澘涓嬫媺鍊艰幏鍙栦笉鍒?
                setXSSFValidationWithHidden(sheet, comboArray, attr.prompt(), 1, 100, column, column);
            }
            else
            {
                // 鎻愮ず淇℃伅鎴栧彧鑳介€夋嫨涓嶈兘杈撳叆鐨勫垪鍐呭.
                setPromptOrValidation(sheet, comboArray, attr.prompt(), 1, 100, column, column);
            }
        }
    }

    /**
     * 娣诲姞鍗曞厓鏍?
     */
    @SuppressWarnings("deprecation")
    public Cell addCell(Excel attr, Row row, T vo, Field field, int column)
    {
        Cell cell = null;
        try
        {
            // 璁剧疆琛岄珮
            row.setHeight(maxHeight);
            // 鏍规嵁Excel涓缃儏鍐靛喅瀹氭槸鍚﹀鍑?鏈変簺鎯呭喌闇€瑕佷繚鎸佷负绌?甯屾湜鐢ㄦ埛濉啓杩欎竴鍒?
            if (attr.isExport())
            {
                // 鍒涘缓cell
                cell = row.createCell(column);
                if (isSubListValue(vo) && getListCellValue(vo) > 1 && attr.needMerge())
                {
                    if (subMergedLastRowNum >= subMergedFirstRowNum)
                    {
                        sheet.addMergedRegion(new CellRangeAddress(subMergedFirstRowNum, subMergedLastRowNum, column, column));
                    }
                }
                cell.setCellStyle(styles.get(StringUtils.format("data_{}_{}_{}_{}_{}", attr.align(), attr.color(), attr.backgroundColor(), attr.cellType(), attr.wrapText())));

                // 鐢ㄤ簬璇诲彇瀵硅薄涓殑灞炴€?
                Object value = getTargetValue(vo, field, attr);
                String dateFormat = attr.dateFormat();
                String readConverterExp = attr.readConverterExp();
                String separator = attr.separator();
                String dictType = attr.dictType();
                if (StringUtils.isNotEmpty(dateFormat) && StringUtils.isNotNull(value))
                {
                    cell.setCellStyle(createCellStyle(cell.getCellStyle(), dateFormat));
                    cell.setCellValue(parseDateToStr(dateFormat, value));
                }
                else if (StringUtils.isNotEmpty(readConverterExp) && StringUtils.isNotNull(value))
                {
                    cell.setCellValue(convertByExp(Convert.toStr(value), readConverterExp, separator));
                }
                else if (StringUtils.isNotEmpty(dictType) && StringUtils.isNotNull(value))
                {
                    if (!sysDictMap.containsKey(dictType + value))
                    {
                        String lable = convertDictByExp(Convert.toStr(value), dictType, separator);
                        sysDictMap.put(dictType + value, lable);
                    }
                    cell.setCellValue(sysDictMap.get(dictType + value));
                }
                else if (value instanceof BigDecimal && -1 != attr.scale())
                {
                    cell.setCellValue((((BigDecimal) value).setScale(attr.scale(), attr.roundingMode())).doubleValue());
                }
                else if (!attr.handler().equals(ExcelHandlerAdapter.class))
                {
                    cell.setCellValue(dataFormatHandlerAdapter(value, attr, cell));
                }
                else
                {
                    // 璁剧疆鍒楃被鍨?
                    setCellVo(value, attr, cell);
                }
                addStatisticsData(column, Convert.toStr(value), attr);
            }
        }
        catch (Exception e)
        {
            log.error("瀵煎嚭Excel澶辫触{}", e);
        }
        return cell;
    }

    /**
     * 浣跨敤鑷畾涔夋牸寮忥紝鍚屾椂閬垮厤鏍峰紡姹℃煋
     * 
     * @param cellStyle 浠庢鏍峰紡澶嶅埗
     * @param format 鏍煎紡鍖归厤鐨勫瓧绗︿覆
     * @return 鏍煎紡鍖栧悗CellStyle瀵硅薄
     */
    private CellStyle createCellStyle(CellStyle cellStyle, String format)
    {
        String key = cellStyle.getIndex() + "|" + format;
        CellStyle cached = cellStyleCache.get(key);
        if (cached != null)
        {
            return cached;
        }
        CellStyle style = wb.createCellStyle();
        style.cloneStyleFrom(cellStyle);
        style.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat(format));
        cellStyleCache.put(key, style);
        return style;
    }

    /**
     * 璁剧疆 POI XSSFSheet 鍗曞厓鏍兼彁绀烘垨閫夋嫨妗?
     * 
     * @param sheet 琛ㄥ崟
     * @param textlist 涓嬫媺妗嗘樉绀虹殑鍐呭
     * @param promptContent 鎻愮ず鍐呭
     * @param firstRow 寮€濮嬭
     * @param endRow 缁撴潫琛?
     * @param firstCol 寮€濮嬪垪
     * @param endCol 缁撴潫鍒?
     */
    public void setPromptOrValidation(Sheet sheet, String[] textlist, String promptContent, int firstRow, int endRow,
            int firstCol, int endCol)
    {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = textlist.length > 0 ? helper.createExplicitListConstraint(textlist) : helper.createCustomConstraint("DD1");
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        DataValidation dataValidation = helper.createValidation(constraint, regions);
        if (StringUtils.isNotEmpty(promptContent))
        {
            // 濡傛灉璁剧疆浜嗘彁绀轰俊鎭垯榧犳爣鏀句笂鍘绘彁绀?
            dataValidation.createPromptBox("", promptContent);
            dataValidation.setShowPromptBox(true);
        }
        // 澶勭悊Excel鍏煎鎬ч棶棰?
        if (dataValidation instanceof XSSFDataValidation)
        {
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.setShowErrorBox(true);
        }
        else
        {
            dataValidation.setSuppressDropDownArrow(false);
        }
        sheet.addValidationData(dataValidation);
    }

    /**
     * 璁剧疆鏌愪簺鍒楃殑鍊煎彧鑳借緭鍏ラ鍒剁殑鏁版嵁,鏄剧ず涓嬫媺妗嗭紙鍏煎瓒呭嚭涓€瀹氭暟閲忕殑涓嬫媺妗嗭級.
     * 
     * @param sheet 瑕佽缃殑sheet.
     * @param textlist 涓嬫媺妗嗘樉绀虹殑鍐呭
     * @param promptContent 鎻愮ず鍐呭
     * @param firstRow 寮€濮嬭
     * @param endRow 缁撴潫琛?
     * @param firstCol 寮€濮嬪垪
     * @param endCol 缁撴潫鍒?
     */
    public void setXSSFValidationWithHidden(Sheet sheet, String[] textlist, String promptContent, int firstRow, int endRow, int firstCol, int endCol)
    {
        String hideSheetName = "combo_" + firstCol + "_" + endCol;
        Sheet hideSheet = null;
        String hideSheetDataName = hideSheetName + "_data";
        Name name = wb.getName(hideSheetDataName);
        if (name != null)
        {
            // 鍚嶇О宸插瓨鍦紝灏濊瘯浠庡悕绉扮殑寮曠敤涓壘鍒皊heet鍚嶇О
            String refersToFormula = name.getRefersToFormula();
            if (StringUtils.isNotEmpty(refersToFormula) && refersToFormula.contains("!"))
            {
                String sheetNameFromFormula = refersToFormula.substring(0, refersToFormula.indexOf("!"));
                hideSheet = wb.getSheet(sheetNameFromFormula);
            }
        }

        if (hideSheet == null)
        {
            hideSheet = wb.createSheet(hideSheetName); // 鐢ㄤ簬瀛樺偍 涓嬫媺鑿滃崟鏁版嵁
            for (int i = 0; i < textlist.length; i++)
            {
                hideSheet.createRow(i).createCell(0).setCellValue(textlist[i]);
            }
            // 鍒涘缓鍚嶇О锛屽彲琚叾浠栧崟鍏冩牸寮曠敤
            name = wb.createName();
            name.setNameName(hideSheetDataName);
            name.setRefersToFormula(hideSheetName + "!$A$1:$A$" + textlist.length);
        }

        DataValidationHelper helper = sheet.getDataValidationHelper();
        // 鍔犺浇涓嬫媺鍒楄〃鍐呭
        DataValidationConstraint constraint = helper.createFormulaListConstraint(hideSheetDataName);
        // 璁剧疆鏁版嵁鏈夋晥鎬у姞杞藉湪鍝釜鍗曞厓鏍间笂,鍥涗釜鍙傛暟鍒嗗埆鏄細璧峰琛屻€佺粓姝㈣銆佽捣濮嬪垪銆佺粓姝㈠垪
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        // 鏁版嵁鏈夋晥鎬у璞?
        DataValidation dataValidation = helper.createValidation(constraint, regions);
        if (StringUtils.isNotEmpty(promptContent))
        {
            // 濡傛灉璁剧疆浜嗘彁绀轰俊鎭垯榧犳爣鏀句笂鍘绘彁绀?
            dataValidation.createPromptBox("", promptContent);
            dataValidation.setShowPromptBox(true);
        }
        // 澶勭悊Excel鍏煎鎬ч棶棰?
        if (dataValidation instanceof XSSFDataValidation)
        {
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.setShowErrorBox(true);
        }
        else
        {
            dataValidation.setSuppressDropDownArrow(false);
        }

        sheet.addValidationData(dataValidation);
        // 璁剧疆hiddenSheet闅愯棌
        wb.setSheetHidden(wb.getSheetIndex(hideSheet), true);
    }

    /**
     * 瑙ｆ瀽瀵煎嚭鍊?0=鐢?1=濂?2=鏈煡
     * 
     * @param propertyValue 鍙傛暟鍊?
     * @param converterExp 缈昏瘧娉ㄨВ
     * @param separator 鍒嗛殧绗?
     * @return 瑙ｆ瀽鍚庡€?
     */
    public static String convertByExp(String propertyValue, String converterExp, String separator)
    {
        StringBuilder propertyString = new StringBuilder();
        String[] convertSource = converterExp.split(SEPARATOR);
        for (String item : convertSource)
        {
            String[] itemArray = item.split("=");
            if (StringUtils.containsAny(propertyValue, separator))
            {
                for (String value : propertyValue.split(separator))
                {
                    if (itemArray[0].equals(value))
                    {
                        propertyString.append(itemArray[1] + separator);
                        break;
                    }
                }
            }
            else
            {
                if (itemArray[0].equals(propertyValue))
                {
                    return itemArray[1];
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator);
    }

    /**
     * 鍙嶅悜瑙ｆ瀽鍊?鐢?0,濂?1,鏈煡=2
     * 
     * @param propertyValue 鍙傛暟鍊?
     * @param converterExp 缈昏瘧娉ㄨВ
     * @param separator 鍒嗛殧绗?
     * @return 瑙ｆ瀽鍚庡€?
     */
    public static String reverseByExp(String propertyValue, String converterExp, String separator)
    {
        StringBuilder propertyString = new StringBuilder();
        String[] convertSource = converterExp.split(SEPARATOR);
        for (String item : convertSource)
        {
            String[] itemArray = item.split("=");
            if (StringUtils.containsAny(propertyValue, separator))
            {
                for (String value : propertyValue.split(separator))
                {
                    if (itemArray[1].equals(value))
                    {
                        propertyString.append(itemArray[0] + separator);
                        break;
                    }
                }
            }
            else
            {
                if (itemArray[1].equals(propertyValue))
                {
                    return itemArray[0];
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator);
    }

    /**
     * 瑙ｆ瀽瀛楀吀鍊?
     * 
     * @param dictValue 瀛楀吀鍊?
     * @param dictType 瀛楀吀绫诲瀷
     * @param separator 鍒嗛殧绗?
     * @return 瀛楀吀鏍囩
     */
    public static String convertDictByExp(String dictValue, String dictType, String separator)
    {
        return DictUtils.getDictLabel(dictType, dictValue, separator);
    }

    /**
     * 鍙嶅悜瑙ｆ瀽鍊煎瓧鍏稿€?
     * 
     * @param dictLabel 瀛楀吀鏍囩
     * @param dictType 瀛楀吀绫诲瀷
     * @param separator 鍒嗛殧绗?
     * @return 瀛楀吀鍊?
     */
    public static String reverseDictByExp(String dictLabel, String dictType, String separator)
    {
        return DictUtils.getDictValue(dictType, dictLabel, separator);
    }

    /**
     * 鏁版嵁澶勭悊鍣?
     * 
     * @param value 鏁版嵁鍊?
     * @param excel 鏁版嵁娉ㄨВ
     * @return
     */
    public String dataFormatHandlerAdapter(Object value, Excel excel, Cell cell)
    {
        try
        {
            Object instance = excel.handler().getDeclaredConstructor().newInstance();
            Method formatMethod = excel.handler().getMethod("format", new Class[] { Object.class, String[].class, Cell.class, Workbook.class });
            value = formatMethod.invoke(instance, value, excel.args(), cell, this.wb);
        }
        catch (Exception e)
        {
            log.error("涓嶈兘鏍煎紡鍖栨暟鎹?" + excel.handler(), e.getMessage());
        }
        return Convert.toStr(value);
    }

    /**
     * 鍚堣缁熻淇℃伅
     */
    private void addStatisticsData(Integer index, String text, Excel entity)
    {
        if (entity != null && entity.isStatistics())
        {
            Double temp = 0D;
            if (!statistics.containsKey(index))
            {
                statistics.put(index, temp);
            }
            try
            {
                temp = Double.valueOf(text);
            }
            catch (NumberFormatException e)
            {
            }
            statistics.put(index, statistics.get(index) + temp);
        }
    }

    /**
     * 鍒涘缓缁熻琛?
     */
    public void addStatisticsRow()
    {
        if (statistics.size() > 0)
        {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            Set<Integer> keys = statistics.keySet();
            Cell cell = row.createCell(0);
            cell.setCellStyle(styles.get("total"));
            cell.setCellValue("鍚堣");

            for (Integer key : keys)
            {
                cell = row.createCell(key);
                cell.setCellStyle(styles.get("total"));
                cell.setCellValue(statistics.get(key));
            }
            statistics.clear();
        }
    }

    /**
     * 缂栫爜鏂囦欢鍚?
     */
    public String encodingFilename(String filename)
    {
        return UUID.randomUUID() + "_" + filename + ".xlsx";
    }

    /**
     * 鑾峰彇涓嬭浇璺緞
     * 
     * @param filename 鏂囦欢鍚嶇О
     */
    public String getAbsoluteFile(String filename)
    {
        String downloadPath = PlatformConfig.getDownloadPath() + filename;
        File desc = new File(downloadPath);
        if (!desc.getParentFile().exists())
        {
            desc.getParentFile().mkdirs();
        }
        return downloadPath;
    }

    /**
     * 鑾峰彇bean涓殑灞炴€у€?
     * 
     * @param vo 瀹炰綋瀵硅薄
     * @param field 瀛楁
     * @param excel 娉ㄨВ
     * @return 鏈€缁堢殑灞炴€у€?
     * @throws Exception
     */
    private Object getTargetValue(T vo, Field field, Excel excel) throws Exception
    {
        field.setAccessible(true);
        Object o = field.get(vo);
        if (StringUtils.isNotEmpty(excel.targetAttr()))
        {
            String target = excel.targetAttr();
            if (target.contains("."))
            {
                String[] targets = target.split("[.]");
                for (String name : targets)
                {
                    o = getValue(o, name);
                }
            }
            else
            {
                o = getValue(o, target);
            }
        }
        return o;
    }

    /**
     * 浠ョ被鐨勫睘鎬х殑get鏂规硶鏂规硶褰㈠紡鑾峰彇鍊?
     * 
     * @param o
     * @param name
     * @return value
     * @throws Exception
     */
    private Object getValue(Object o, String name) throws Exception
    {
        if (StringUtils.isNotNull(o) && StringUtils.isNotEmpty(name))
        {
            Class<?> clazz = o.getClass();
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            o = field.get(o);
        }
        return o;
    }

    /**
     * 寰楀埌鎵€鏈夊畾涔夊瓧娈?
     */
    private void createExcelField()
    {
        this.fields = getFields();
        this.fields = this.fields.stream().sorted(Comparator.comparing(objects -> ((Excel) objects[1]).sort())).collect(Collectors.toList());
        this.maxHeight = getRowHeight();
    }

    /**
     * 鑾峰彇瀛楁娉ㄨВ淇℃伅
     */
    public List<Object[]> getFields()
    {
        List<Object[]> fields = new ArrayList<Object[]>();
        List<Field> tempFields = new ArrayList<>();
        subFieldsMap = new HashMap<>();
        subMethods = new HashMap<>();
        tempFields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
        tempFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (StringUtils.isNotEmpty(includeFields))
        {
            for (Field field : tempFields)
            {
                if (ArrayUtils.contains(this.includeFields, field.getName()) || field.isAnnotationPresent(Excels.class))
                {
                    addField(fields, field);
                }
            }
        }
        else if (StringUtils.isNotEmpty(excludeFields))
        {
            for (Field field : tempFields)
            {
                if (!ArrayUtils.contains(this.excludeFields, field.getName()))
                {
                    addField(fields, field);
                }
            }
        }
        else
        {
            for (Field field : tempFields)
            {
                addField(fields, field);
            }
        }
        return fields;
    }

    /**
     * 娣诲姞瀛楁淇℃伅
     */
    public void addField(List<Object[]> fields, Field field)
    {
        // 鍗曟敞瑙?
        if (field.isAnnotationPresent(Excel.class))
        {
            Excel attr = field.getAnnotation(Excel.class);
            if (attr != null && (attr.type() == Type.ALL || attr.type() == type))
            {
                fields.add(new Object[] { field, attr });
            }
            if (Collection.class.isAssignableFrom(field.getType()))
            {
                String fieldName = field.getName();
                subMethods.put(fieldName, getSubMethod(fieldName, clazz));
                ParameterizedType pt = (ParameterizedType) field.getGenericType();
                Class<?> subClass = (Class<?>) pt.getActualTypeArguments()[0];
                subFieldsMap.put(fieldName, FieldUtils.getFieldsListWithAnnotation(subClass, Excel.class));
            }
        }

        // 澶氭敞瑙?
        if (field.isAnnotationPresent(Excels.class))
        {
            Excels attrs = field.getAnnotation(Excels.class);
            Excel[] excels = attrs.value();
            for (Excel attr : excels)
            {
                if (StringUtils.isNotEmpty(includeFields))
                {
                    if (ArrayUtils.contains(this.includeFields, field.getName() + "." + attr.targetAttr())
                            && (attr != null && (attr.type() == Type.ALL || attr.type() == type)))
                    {
                        fields.add(new Object[] { field, attr });
                    }
                }
                else
                {
                    if (!ArrayUtils.contains(this.excludeFields, field.getName() + "." + attr.targetAttr())
                            && (attr != null && (attr.type() == Type.ALL || attr.type() == type)))
                    {
                        fields.add(new Object[] { field, attr });
                    }
                }
            }
        }
    }

    /**
     * 鏍规嵁娉ㄨВ鑾峰彇鏈€澶ц楂?
     */
    public short getRowHeight()
    {
        double maxHeight = 0;
        for (Object[] os : this.fields)
        {
            Excel excel = (Excel) os[1];
            maxHeight = Math.max(maxHeight, excel.height());
        }
        return (short) (maxHeight * 20);
    }

    /**
     * 鍒涘缓涓€涓伐浣滅翱
     */
    public void createWorkbook()
    {
        this.wb = new SXSSFWorkbook(500);
        this.sheet = wb.createSheet();
        wb.setSheetName(0, sheetName);
        this.styles = createStyles(wb);
    }

    /**
     * 鍒涘缓宸ヤ綔琛?
     * 
     * @param sheetNo sheet鏁伴噺
     * @param index 搴忓彿
     */
    public void createSheet(int sheetNo, int index)
    {
        // 璁剧疆宸ヤ綔琛ㄧ殑鍚嶇О.
        if (sheetNo > 1 && index > 0)
        {
            this.sheet = wb.createSheet();
            this.createTitle();
            int actualIndex = wb.getSheetIndex(this.sheet);
            wb.setSheetName(actualIndex, sheetName + index);
        }
    }

    /**
     * 鑾峰彇鍗曞厓鏍煎€?
     * 
     * @param row 鑾峰彇鐨勮
     * @param column 鑾峰彇鍗曞厓鏍煎垪鍙?
     * @return 鍗曞厓鏍煎€?
     */
    public Object getCellValue(Row row, int column)
    {
        if (row == null)
        {
            return row;
        }
        Object val = "";
        try
        {
            Cell cell = row.getCell(column);
            if (StringUtils.isNotNull(cell))
            {
                if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA)
                {
                    val = cell.getNumericCellValue();
                    if (DateUtil.isCellDateFormatted(cell))
                    {
                        val = DateUtil.getJavaDate((Double) val); // POI Excel 鏃ユ湡鏍煎紡杞崲
                    }
                    else
                    {
                        if ((Double) val % 1 != 0)
                        {
                            val = new BigDecimal(val.toString());
                        }
                        else
                        {
                            val = new DecimalFormat("0").format(val);
                        }
                    }
                }
                else if (cell.getCellType() == CellType.STRING)
                {
                    val = cell.getStringCellValue();
                }
                else if (cell.getCellType() == CellType.BOOLEAN)
                {
                    val = cell.getBooleanCellValue();
                }
                else if (cell.getCellType() == CellType.ERROR)
                {
                    val = cell.getErrorCellValue();
                }

            }
        }
        catch (Exception e)
        {
            return val;
        }
        return val;
    }

    /**
     * 鍒ゆ柇鏄惁鏄┖琛?
     * 
     * @param row 鍒ゆ柇鐨勮
     * @return
     */
    private boolean isRowEmpty(Row row)
    {
        if (row == null)
        {
            return true;
        }
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++)
        {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 鑾峰彇Excel2003鍥剧墖
     *
     * @param sheet 褰撳墠sheet瀵硅薄
     * @param workbook 宸ヤ綔绨垮璞?
     * @return Map key:鍥剧墖鍗曞厓鏍肩储寮曪紙1_1锛塖tring锛寁alue:鍥剧墖娴丳ictureData
     */
    public static Map<String, List<PictureData>> getSheetPictures03(HSSFSheet sheet, HSSFWorkbook workbook)
    {
        Map<String, List<PictureData>> sheetIndexPicMap = new HashMap<>();
        List<HSSFPictureData> pictures = workbook.getAllPictures();
        if (!pictures.isEmpty() && sheet.getDrawingPatriarch() != null)
        {
            for (HSSFShape shape : sheet.getDrawingPatriarch().getChildren())
            {
                if (shape instanceof HSSFPicture)
                {
                    HSSFPicture pic = (HSSFPicture) shape;
                    HSSFClientAnchor anchor = (HSSFClientAnchor) pic.getAnchor();
                    String picIndex = anchor.getRow1() + "_" + anchor.getCol1();
                    sheetIndexPicMap.computeIfAbsent(picIndex, k -> new ArrayList<>()).add(pic.getPictureData());
                }
            }
        }
        return sheetIndexPicMap;
    }

    /**
     * 鑾峰彇Excel2007鍥剧墖
     *
     * @param sheet 褰撳墠sheet瀵硅薄
     * @param workbook 宸ヤ綔绨垮璞?
     * @return Map key:鍥剧墖鍗曞厓鏍肩储寮曪紙1_1锛塖tring锛寁alue:鍥剧墖娴丳ictureData
     */
    public static Map<String, List<PictureData>> getSheetPictures07(XSSFSheet sheet, XSSFWorkbook workbook)
    {
        Map<String, List<PictureData>> sheetIndexPicMap = new HashMap<>();
        for (POIXMLDocumentPart dr : sheet.getRelations())
        {
            if (dr instanceof XSSFDrawing)
            {
                XSSFDrawing drawing = (XSSFDrawing) dr;
                for (XSSFShape shape : drawing.getShapes())
                {
                    if (shape instanceof XSSFPicture)
                    {
                        XSSFPicture pic = (XSSFPicture) shape;
                        XSSFClientAnchor anchor = pic.getPreferredSize();
                        CTMarker ctMarker = anchor.getFrom();
                        String picIndex = ctMarker.getRow() + "_" + ctMarker.getCol();
                        sheetIndexPicMap.computeIfAbsent(picIndex, k -> new ArrayList<>()).add(pic.getPictureData());
                    }
                }
            }
        }
        return sheetIndexPicMap;
    }

    /**
     * 鏍煎紡鍖栦笉鍚岀被鍨嬬殑鏃ユ湡瀵硅薄
     * 
     * @param dateFormat 鏃ユ湡鏍煎紡
     * @param val 琚牸寮忓寲鐨勬棩鏈熷璞?
     * @return 鏍煎紡鍖栧悗鐨勬棩鏈熷瓧绗?
     */
    public String parseDateToStr(String dateFormat, Object val)
    {
        if (val == null)
        {
            return "";
        }
        String str;
        if (val instanceof Date)
        {
            str = DateUtils.parseDateToStr(dateFormat, (Date) val);
        }
        else if (val instanceof LocalDateTime)
        {
            str = DateUtils.parseDateToStr(dateFormat, DateUtils.toDate((LocalDateTime) val));
        }
        else if (val instanceof LocalDate)
        {
            str = DateUtils.parseDateToStr(dateFormat, DateUtils.toDate((LocalDate) val));
        }
        else
        {
            str = val.toString();
        }
        return str;
    }

    /**
     * 鏄惁鏈夊璞＄殑瀛愬垪琛?
     */
    public boolean isSubList()
    {
        return !StringUtils.isEmpty(subFieldsMap);
    }

    /**
     * 鏄惁鏈夊璞＄殑瀛愬垪琛紝闆嗗悎涓嶄负绌?
     */
    public boolean isSubListValue(T vo)
    {
        return !StringUtils.isEmpty(subFieldsMap) && getListCellValue(vo) > 0;
    }

    /**
     * 鑾峰彇闆嗗悎鐨勫€?
     */
    public int getListCellValue(Object obj)
    {
        Collection<?> value;
        int max = 0;
        try
        {
            for (String s : subMethods.keySet())
            {
                value = (Collection<?>) subMethods.get(s).invoke(obj);
                if (value.size() > max)
                {
                    max = value.size();
                }
            }
        }
        catch (Exception e)
        {
            return 0;
        }
        return max;
    }

    /**
     * 鑾峰彇瀵硅薄鐨勫瓙鍒楄〃鏂规硶
     * 
     * @param name 鍚嶇О
     * @param pojoClass 绫诲璞?
     * @return 瀛愬垪琛ㄦ柟娉?
     */
    public Method getSubMethod(String name, Class<?> pojoClass)
    {
        StringBuffer getMethodName = new StringBuffer("get");
        getMethodName.append(name.substring(0, 1).toUpperCase());
        getMethodName.append(name.substring(1));
        Method method = null;
        try
        {
            method = pojoClass.getMethod(getMethodName.toString(), new Class[] {});
        }
        catch (Exception e)
        {
            log.error("鑾峰彇瀵硅薄寮傚父{}", e.getMessage());
        }
        return method;
    }
}

