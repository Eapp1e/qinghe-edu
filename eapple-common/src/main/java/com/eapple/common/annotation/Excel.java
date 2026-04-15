package com.eapple.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import com.eapple.common.utils.poi.ExcelHandlerAdapter;

/**
 * 鑷畾涔夊鍑篍xcel鏁版嵁娉ㄨВ
 * 
 * @author Eapp1e
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Excel
{
    /**
     * 瀵煎嚭鏃跺湪excel涓帓搴?
     */
    public int sort() default Integer.MAX_VALUE;

    /**
     * 瀵煎嚭鍒癊xcel涓殑鍚嶅瓧.
     */
    public String name() default "";

    /**
     * 鏃ユ湡鏍煎紡, 濡? yyyy-MM-dd
     */
    public String dateFormat() default "";

    /**
     * 濡傛灉鏄瓧鍏哥被鍨嬶紝璇疯缃瓧鍏哥殑type鍊?(濡? sys_user_sex)
     */
    public String dictType() default "";

    /**
     * 璇诲彇鍐呭杞〃杈惧紡 (濡? 0=鐢?1=濂?2=鏈煡)
     */
    public String readConverterExp() default "";

    /**
     * 鍒嗛殧绗︼紝璇诲彇瀛楃涓茬粍鍐呭
     */
    public String separator() default ",";

    /**
     * BigDecimal 绮惧害 榛樿:-1(榛樿涓嶅紑鍚疊igDecimal鏍煎紡鍖?
     */
    public int scale() default -1;

    /**
     * BigDecimal 鑸嶅叆瑙勫垯 榛樿:BigDecimal.ROUND_HALF_EVEN
     */
    @SuppressWarnings("deprecation")
    public int roundingMode() default BigDecimal.ROUND_HALF_EVEN;

    /**
     * 瀵煎嚭鏃跺湪excel涓瘡涓垪鐨勯珮搴?
     */
    public double height() default 14;

    /**
     * 瀵煎嚭鏃跺湪excel涓瘡涓垪鐨勫搴?
     */
    public double width() default 16;

    /**
     * 鏂囧瓧鍚庣紑,濡? 90 鍙樻垚90%
     */
    public String suffix() default "";

    /**
     * 褰撳€间负绌烘椂,瀛楁鐨勯粯璁ゅ€?
     */
    public String defaultValue() default "";

    /**
     * 鎻愮ず淇℃伅
     */
    public String prompt() default "";

    /**
     * 鏄惁鍏佽鍐呭鎹㈣ 
     */
    public boolean wrapText() default false;

    /**
     * 璁剧疆鍙兘閫夋嫨涓嶈兘杈撳叆鐨勫垪鍐呭.
     */
    public String[] combo() default {};

    /**
     * 鏄惁浠庡瓧鍏歌鏁版嵁鍒癱ombo,榛樿涓嶈鍙?濡傝鍙栭渶瑕佽缃甦ictType娉ㄨВ.
     */
    public boolean comboReadDict() default false;

    /**
     * 鏄惁闇€瑕佺旱鍚戝悎骞跺崟鍏冩牸,搴斿闇€姹?鍚湁list闆嗗悎鍗曞厓鏍?
     */
    public boolean needMerge() default false;

    /**
     * 鏄惁瀵煎嚭鏁版嵁,搴斿闇€姹?鏈夋椂鎴戜滑闇€瑕佸鍑轰竴浠芥ā鏉?杩欐槸鏍囬闇€瑕佷絾鍐呭闇€瑕佺敤鎴锋墜宸ュ～鍐?
     */
    public boolean isExport() default true;

    /**
     * 鍙︿竴涓被涓殑灞炴€у悕绉?鏀寔澶氱骇鑾峰彇,浠ュ皬鏁扮偣闅斿紑
     */
    public String targetAttr() default "";

    /**
     * 鏄惁鑷姩缁熻鏁版嵁,鍦ㄦ渶鍚庤拷鍔犱竴琛岀粺璁℃暟鎹€诲拰
     */
    public boolean isStatistics() default false;

    /**
     * 瀵煎嚭绫诲瀷锛?鏁板瓧 1瀛楃涓?2鍥剧墖锛?
     */
    public ColumnType cellType() default ColumnType.STRING;

    /**
     * 瀵煎嚭鍒楀ご鑳屾櫙棰滆壊
     */
    public IndexedColors headerBackgroundColor() default IndexedColors.GREY_50_PERCENT;

    /**
     * 瀵煎嚭鍒楀ご瀛椾綋棰滆壊
     */
    public IndexedColors headerColor() default IndexedColors.WHITE;

    /**
     * 瀵煎嚭鍗曞厓鏍艰儗鏅鑹?
     */
    public IndexedColors backgroundColor() default IndexedColors.WHITE;

    /**
     * 瀵煎嚭鍗曞厓鏍煎瓧浣撻鑹?
     */
    public IndexedColors color() default IndexedColors.BLACK;

    /**
     * 瀵煎嚭瀛楁瀵归綈鏂瑰紡
     */
    public HorizontalAlignment align() default HorizontalAlignment.CENTER;

    /**
     * 鑷畾涔夋暟鎹鐞嗗櫒
     */
    public Class<?> handler() default ExcelHandlerAdapter.class;

    /**
     * 鑷畾涔夋暟鎹鐞嗗櫒鍙傛暟
     */
    public String[] args() default {};

    /**
     * 瀛楁绫诲瀷锛?锛氬鍑哄鍏ワ紱1锛氫粎瀵煎嚭锛?锛氫粎瀵煎叆锛?
     */
    Type type() default Type.ALL;

    public enum Type
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

    public enum ColumnType
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