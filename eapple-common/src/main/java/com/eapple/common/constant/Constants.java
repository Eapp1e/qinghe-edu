package com.eapple.common.constant;

import java.util.Locale;
import io.jsonwebtoken.Claims;

/**
 * 通用常量信息
 * 
 * @author Eapp1e
 */
public class Constants
{
    /**
     * UTF-8 编码
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 编码
     */
    public static final String GBK = "GBK";

    /**
     * 系统语言
     */
    public static final Locale DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE;

    /**
     * www 主域名
     */
    public static final String WWW = "www.";

    /**
     * http 请求
     */
    public static final String HTTP = "http://";

    /**
     * https 请求
     */
    public static final String HTTPS = "https://";

    /**
     * 操作成功标识
     */
    public static final String SUCCESS = "0";

    /**
     * 操作失败标识
     */
    public static final String FAIL = "1";

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 注册
     */
    public static final String REGISTER = "Register";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * 所有权限标识
     */
    public static final String ALL_PERMISSION = "*:*:*";

    /**
     * 管理员角色权限标识
     */
    public static final String SUPER_ADMIN = "admin";

    /**
     * 角色权限分隔符
     */
    public static final String ROLE_DELIMITER = ",";

    /**
     * 权限标识分隔符
     */
    public static final String PERMISSION_DELIMITER = ",";

    /**
     * 验证码有效期（分钟）
     */
    public static final Integer CAPTCHA_EXPIRATION = 2;

    /**
     * 令牌
     */
    public static final String TOKEN = "token";

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 登录用户标识
     */
    public static final String LOGIN_USER_KEY = "login_user_key";

    /**
     * 用户 ID
     */
    public static final String JWT_USERID = "userid";

    /**
     * 用户名称
     */
    public static final String JWT_USERNAME = Claims.SUBJECT;

    /**
     * 用户头像
     */
    public static final String JWT_AVATAR = "avatar";

    /**
     * 创建时间
     */
    public static final String JWT_CREATED = "created";

    /**
     * 用户权限
     */
    public static final String JWT_AUTHORITIES = "authorities";

    /**
     * 静态资源映射前缀
     */
    public static final String RESOURCE_PREFIX = "/profile";

    /**
     * RMI 协议前缀
     */
    public static final String LOOKUP_RMI = "rmi:";

    /**
     * LDAP 协议前缀
     */
    public static final String LOOKUP_LDAP = "ldap:";

    /**
     * LDAPS 协议前缀
     */
    public static final String LOOKUP_LDAPS = "ldaps:";

    /**
     * 自动识别 JSON 对象白名单配置（仅允许解析的包名，范围越小越安全）
     */
    public static final String[] JSON_WHITELIST_STR = { "com.eapple" };

    /**
     * 定时任务白名单配置（仅允许访问的包名，如有其他需要可自行添加）
     */
    public static final String[] JOB_WHITELIST_STR = { "com.eapple.quartz.task" };

    /**
     * 定时任务违规的关键字
     */
    public static final String[] JOB_ERROR_STR = { "java.net.URL", "javax.naming.InitialContext", "org.yaml.snakeyaml",
            "org.springframework", "org.apache", "com.eapple.common.utils.file", "com.eapple.common.config", "com.eapple.generator" };

    /**
     * 部门数据权限
     */
    public static class Dept
    {
        /**
         * 全部数据权限
         */
        public static final String DATA_SCOPE_ALL = "1";

        /**
         * 自定义数据权限
         */
        public static final String DATA_SCOPE_CUSTOM = "2";

        /**
         * 部门数据权限
         */
        public static final String DATA_SCOPE_DEPT = "3";

        /**
         * 部门及以下数据权限
         */
        public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

        /**
         * 仅本人数据权限
         */
        public static final String DATA_SCOPE_SELF = "5";
    }
}
