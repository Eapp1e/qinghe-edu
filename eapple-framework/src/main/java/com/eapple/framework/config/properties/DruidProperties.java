package com.eapple.framework.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import com.alibaba.druid.pool.DruidDataSource;

/**
 * Druid 数据源属性配置。
 * 
 * @author Eapp1e
 */
@Configuration
public class DruidProperties
{
    @Value("${spring.datasource.druid.initialSize}")
    private int initialSize;

    @Value("${spring.datasource.druid.minIdle}")
    private int minIdle;

    @Value("${spring.datasource.druid.maxActive}")
    private int maxActive;

    @Value("${spring.datasource.druid.maxWait}")
    private int maxWait;

    @Value("${spring.datasource.druid.connectTimeout}")
    private int connectTimeout;

    @Value("${spring.datasource.druid.socketTimeout}")
    private int socketTimeout;

    @Value("${spring.datasource.druid.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.druid.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.druid.maxEvictableIdleTimeMillis}")
    private int maxEvictableIdleTimeMillis;

    @Value("${spring.datasource.druid.validationQuery}")
    private String validationQuery;

    @Value("${spring.datasource.druid.testWhileIdle}")
    private boolean testWhileIdle;

    @Value("${spring.datasource.druid.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${spring.datasource.druid.testOnReturn}")
    private boolean testOnReturn;

    public DruidDataSource dataSource(DruidDataSource datasource)
    {
        /** 配置连接池初始化与容量参数 */
        datasource.setInitialSize(initialSize);
        datasource.setMaxActive(maxActive);
        datasource.setMinIdle(minIdle);

        /** 配置获取连接等待超时 */
        datasource.setMaxWait(maxWait);
        
        /** 配置建立连接的超时时间，单位毫秒 */
        datasource.setConnectTimeout(connectTimeout);
        
        /** 配置网络读写超时时间，单位毫秒 */
        datasource.setSocketTimeout(socketTimeout);

        /** 配置空闲连接检测间隔，单位毫秒 */
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);

        /** 配置连接最小和最大空闲存活时间，单位毫秒 */
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);

        /**
         * 用于检测连接是否有效的 SQL，通常使用 select 'x'。
         * 如果 validationQuery 为空，则 testOnBorrow、testOnReturn、testWhileIdle 不会生效。
         */
        datasource.setValidationQuery(validationQuery);
        /** 建议配置为 true，在空闲检测时校验连接有效性 */
        datasource.setTestWhileIdle(testWhileIdle);
        /** 获取连接时是否校验有效性，开启后会略微降低性能 */
        datasource.setTestOnBorrow(testOnBorrow);
        /** 归还连接时是否校验有效性，开启后会略微降低性能 */
        datasource.setTestOnReturn(testOnReturn);
        return datasource;
    }
}
