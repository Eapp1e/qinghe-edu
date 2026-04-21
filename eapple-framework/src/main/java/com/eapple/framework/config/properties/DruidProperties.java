package com.eapple.framework.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import com.alibaba.druid.pool.DruidDataSource;

/**
 * druid 閰嶇疆灞炴€?
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
        /** 閰嶇疆鍒濆鍖栧ぇ灏忋€佹渶灏忋€佹渶澶?*/
        datasource.setInitialSize(initialSize);
        datasource.setMaxActive(maxActive);
        datasource.setMinIdle(minIdle);

        /** 閰嶇疆鑾峰彇杩炴帴绛夊緟瓒呮椂鐨勬椂闂?*/
        datasource.setMaxWait(maxWait);
        
        /** 閰嶇疆椹卞姩杩炴帴瓒呮椂鏃堕棿锛屾娴嬫暟鎹簱寤虹珛杩炴帴鐨勮秴鏃舵椂闂达紝鍗曚綅鏄绉?*/
        datasource.setConnectTimeout(connectTimeout);
        
        /** 閰嶇疆缃戠粶瓒呮椂鏃堕棿锛岀瓑寰呮暟鎹簱鎿嶄綔瀹屾垚鐨勭綉缁滆秴鏃舵椂闂达紝鍗曚綅鏄绉?*/
        datasource.setSocketTimeout(socketTimeout);

        /** 閰嶇疆闂撮殧澶氫箙鎵嶈繘琛屼竴娆℃娴嬶紝妫€娴嬮渶瑕佸叧闂殑绌洪棽杩炴帴锛屽崟浣嶆槸姣 */
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);

        /** 閰嶇疆涓€涓繛鎺ュ湪姹犱腑鏈€灏忋€佹渶澶х敓瀛樼殑鏃堕棿锛屽崟浣嶆槸姣 */
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);

        /**
         * 鐢ㄦ潵妫€娴嬭繛鎺ユ槸鍚︽湁鏁堢殑sql锛岃姹傛槸涓€涓煡璇㈣鍙ワ紝甯哥敤select 'x'銆傚鏋渧alidationQuery涓簄ull锛宼estOnBorrow銆乼estOnReturn銆乼estWhileIdle閮戒笉浼氳捣浣滅敤銆?
         */
        datasource.setValidationQuery(validationQuery);
        /** 寤鸿閰嶇疆涓簍rue锛屼笉褰卞搷鎬ц兘锛屽苟涓斾繚璇佸畨鍏ㄦ€с€傜敵璇疯繛鎺ョ殑鏃跺€欐娴嬶紝濡傛灉绌洪棽鏃堕棿澶т簬timeBetweenEvictionRunsMillis锛屾墽琛寁alidationQuery妫€娴嬭繛鎺ユ槸鍚︽湁鏁堛€?*/
        datasource.setTestWhileIdle(testWhileIdle);
        /** 鐢宠杩炴帴鏃舵墽琛寁alidationQuery妫€娴嬭繛鎺ユ槸鍚︽湁鏁堬紝鍋氫簡杩欎釜閰嶇疆浼氶檷浣庢€ц兘銆?*/
        datasource.setTestOnBorrow(testOnBorrow);
        /** 褰掕繕杩炴帴鏃舵墽琛寁alidationQuery妫€娴嬭繛鎺ユ槸鍚︽湁鏁堬紝鍋氫簡杩欎釜閰嶇疆浼氶檷浣庢€ц兘銆?*/
        datasource.setTestOnReturn(testOnReturn);
        return datasource;
    }
}
