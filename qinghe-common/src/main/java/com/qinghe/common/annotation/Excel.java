package com.qinghe.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import com.qinghe.common.utils.poi.ExcelHandlerAdapter;

/**
 * 自定义 Excel 导入导出注解。
 *
 * @author Eapp1e
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Excel
{
    /**
     * 导出时在 Excel 中的排序。
     */
    int sort() default Integer.MAX_VALUE;

    /**
     * 导出到 Excel 中的列名。
     */
    String name() default "";

    /**
     * 日期格式，例如 yyyy-MM-dd。
     */
    String dateFormat() default "";

    /**
     * 如果是字典类型，请设置字典的 type 值，例如 sys_user_sex。
     */
    String dictType() default "";

    /**
     * 读取内容转换表达式，例如 0=男,1=女,2=未知。
     */
    String readConverterExp() default "";

    /**
     * 分隔符，用于读取字符串组内容。
     */
    String separator() default ",";

    /**
     * BigDecimal 精度，默认 -1 表示不开启格式化。
     */
    int scale() default -1;

    /**
     * BigDecimal 舍入规则，默认 BigDecimal.ROUND_HALF_EVEN。
     */
    @SuppressWarnings("deprecation")
    int roundingMode() default BigDecimal.ROUND_HALF_EVEN;

    /**
     * 导出时单元格高度。
     */
    double height() default 14;

    /**
     * 导出时单元格宽度。
     */
    double width() default 16;

    /**
     * 文本后缀，例如 90 会显示为 90%。
     */
    String suffix() default "";

    /**
     * 默认值。
     */
    String defaultValue() default "";

    /**
     * 提示信息。
     */
    String prompt() default "";

    /**
     * 是否自动换行。
     */
    boolean wrapText() default false;

    /**
     * 下拉选项内容。
     */
    String[] combo() default {};

    /**
     * 是否从字典读取下拉选项，启用时需配置 dictType。
     */
    boolean comboReadDict() default false;

    /**
     * 是否需要纵向合并单元格。
     */
    boolean needMerge() default false;

    /**
     * 是否导出该字段。
     */
    boolean isExport() default true;

    /**
     * 另一个类中的目标属性名，支持多级获取，以小数点分隔。
     */
    String targetAttr() default "";

    /**
     * 是否在导出末尾追加统计行。
     */
    boolean isStatistics() default false;

    /**
     * 单元格类型。
     */
    ColumnType cellType() default ColumnType.STRING;

    /**
     * 表头背景色。
     */
    IndexedColors headerBackgroundColor() default IndexedColors.GREY_50_PERCENT;

    /**
     * 表头字体颜色。
     */
    IndexedColors headerColor() default IndexedColors.WHITE;

    /**
     * 单元格背景色。
     */
    IndexedColors backgroundColor() default IndexedColors.WHITE;

    /**
     * 单元格字体颜色。
     */
    IndexedColors color() default IndexedColors.BLACK;

    /**
     * 对齐方式。
     */
    HorizontalAlignment align() default HorizontalAlignment.CENTER;

    /**
     * 自定义处理器。
     */
    Class<?> handler() default ExcelHandlerAdapter.class;

    /**
     * 自定义处理器参数。
     */
    String[] args() default {};

    /**
     * 字段类型：全部、仅导出、仅导入。
     */
    Type type() default Type.ALL;

    enum Type
    {
        ALL(0), EXPORT(1), IMPORT(2);
        private final int value;

        Type(int value)
        {
            this.value = value;
        }

        public int value()
        {
            return this.value;
        }
    }

    enum ColumnType
    {
        NUMERIC(0), STRING(1), IMAGE(2), TEXT(3);
        private final int value;

        ColumnType(int value)
        {
            this.value = value;
        }

        public int value()
        {
            return this.value;
        }
    }
}