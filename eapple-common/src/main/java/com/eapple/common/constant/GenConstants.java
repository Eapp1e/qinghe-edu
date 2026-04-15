package com.eapple.common.constant;

/**
 * 浠ｇ爜鐢熸垚閫氱敤甯搁噺
 * 
 * @author Eapp1e
 */
public class GenConstants
{
    /** 鍗曡〃锛堝鍒犳敼鏌ワ級 */
    public static final String TPL_CRUD = "crud";

    /** 鏍戣〃锛堝鍒犳敼鏌ワ級 */
    public static final String TPL_TREE = "tree";

    /** 涓诲瓙琛紙澧炲垹鏀规煡锛?*/
    public static final String TPL_SUB = "sub";

    /** 鏍戠紪鐮佸瓧娈?*/
    public static final String TREE_CODE = "treeCode";

    /** 鏍戠埗缂栫爜瀛楁 */
    public static final String TREE_PARENT_CODE = "treeParentCode";

    /** 鏍戝悕绉板瓧娈?*/
    public static final String TREE_NAME = "treeName";

    /** 涓婄骇鑿滃崟ID瀛楁 */
    public static final String PARENT_MENU_ID = "parentMenuId";

    /** 涓婄骇鑿滃崟鍚嶇О瀛楁 */
    public static final String PARENT_MENU_NAME = "parentMenuName";

    /** 鐢熸垚璇︽儏椤靛紑鍏?*/
    public static final String GEN_VIEW = "genView";

    /** 鏁版嵁搴撳瓧绗︿覆绫诲瀷 */
    public static final String[] COLUMNTYPE_STR = { "char", "varchar", "nvarchar", "varchar2" };

    /** 鏁版嵁搴撴枃鏈被鍨?*/
    public static final String[] COLUMNTYPE_TEXT = { "tinytext", "text", "mediumtext", "longtext" };

    /** 鏁版嵁搴撴椂闂寸被鍨?*/
    public static final String[] COLUMNTYPE_TIME = { "datetime", "time", "date", "timestamp" };

    /** 鏁版嵁搴撴暟瀛楃被鍨?*/
    public static final String[] COLUMNTYPE_NUMBER = { "tinyint", "smallint", "mediumint", "int", "number", "integer",
            "bit", "bigint", "float", "double", "decimal" };

    /** 椤甸潰涓嶉渶瑕佺紪杈戝瓧娈?*/
    public static final String[] COLUMNNAME_NOT_EDIT = { "id", "create_by", "create_time", "del_flag" };

    /** 椤甸潰涓嶉渶瑕佹樉绀虹殑鍒楄〃瀛楁 */
    public static final String[] COLUMNNAME_NOT_LIST = { "id", "create_by", "create_time", "del_flag", "update_by",
            "update_time" };

    /** 椤甸潰涓嶉渶瑕佹煡璇㈠瓧娈?*/
    public static final String[] COLUMNNAME_NOT_QUERY = { "id", "create_by", "create_time", "del_flag", "update_by",
            "update_time", "remark" };

    /** Entity鍩虹被瀛楁 */
    public static final String[] BASE_ENTITY = { "createBy", "createTime", "updateBy", "updateTime", "remark" };

    /** Tree鍩虹被瀛楁 */
    public static final String[] TREE_ENTITY = { "parentName", "parentId", "orderNum", "ancestors", "children" };

    /** 鏂囨湰妗?*/
    public static final String HTML_INPUT = "input";

    /** 鏂囨湰鍩?*/
    public static final String HTML_TEXTAREA = "textarea";

    /** 涓嬫媺妗?*/
    public static final String HTML_SELECT = "select";

    /** 鍗曢€夋 */
    public static final String HTML_RADIO = "radio";

    /** 澶嶉€夋 */
    public static final String HTML_CHECKBOX = "checkbox";

    /** 鏃ユ湡鎺т欢 */
    public static final String HTML_DATETIME = "datetime";

    /** 鍥剧墖涓婁紶鎺т欢 */
    public static final String HTML_IMAGE_UPLOAD = "imageUpload";

    /** 鏂囦欢涓婁紶鎺т欢 */
    public static final String HTML_FILE_UPLOAD = "fileUpload";

    /** 瀵屾枃鏈帶浠?*/
    public static final String HTML_EDITOR = "editor";

    /** 瀛楃涓茬被鍨?*/
    public static final String TYPE_STRING = "String";

    /** 鏁村瀷 */
    public static final String TYPE_INTEGER = "Integer";

    /** 闀挎暣鍨?*/
    public static final String TYPE_LONG = "Long";

    /** 娴偣鍨?*/
    public static final String TYPE_DOUBLE = "Double";

    /** 楂樼簿搴﹁绠楃被鍨?*/
    public static final String TYPE_BIGDECIMAL = "BigDecimal";

    /** 鏃堕棿绫诲瀷 */
    public static final String TYPE_DATE = "Date";

    /** 妯＄硦鏌ヨ */
    public static final String QUERY_LIKE = "LIKE";

    /** 鐩哥瓑鏌ヨ */
    public static final String QUERY_EQ = "EQ";

    /** 闇€瑕?*/
    public static final String REQUIRE = "1";
}
