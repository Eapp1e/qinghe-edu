package com.eapple.framework.config;

import com.eapple.common.utils.Threads;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 绾跨▼姹犻厤缃?
 *
 * @author Eapp1e
 **/
@Configuration
public class ThreadPoolConfig
{
    // 鏍稿績绾跨▼姹犲ぇ灏?
    private int corePoolSize = 50;

    // 鏈€澶у彲鍒涘缓鐨勭嚎绋嬫暟
    private int maxPoolSize = 200;

    // 闃熷垪鏈€澶ч暱搴?
    private int queueCapacity = 1000;

    // 绾跨▼姹犵淮鎶ょ嚎绋嬫墍鍏佽鐨勭┖闂叉椂闂?
    private int keepAliveSeconds = 300;

    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor()
    {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(maxPoolSize);
        executor.setCorePoolSize(corePoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        // 绾跨▼姹犲鎷掔粷浠诲姟(鏃犵嚎绋嬪彲鐢?鐨勫鐞嗙瓥鐣?
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    /**
     * 鎵ц鍛ㄦ湡鎬ф垨瀹氭椂浠诲姟
     */
    @Bean(name = "scheduledExecutorService")
    protected ScheduledExecutorService scheduledExecutorService()
    {
        return new ScheduledThreadPoolExecutor(corePoolSize,
            BasicThreadFactory.builder().namingPattern("schedule-pool-%d").daemon(true).build(),
            new ThreadPoolExecutor.CallerRunsPolicy())
        {
            @Override
            protected void afterExecute(Runnable r, Throwable t)
            {
                super.afterExecute(r, t);
                Threads.printException(r, t);
            }
        };
    }
}
