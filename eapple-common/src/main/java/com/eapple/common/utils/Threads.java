package com.eapple.common.utils;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 绾跨▼鐩稿叧宸ュ叿绫?
 * 
 * @author Eapp1e
 */
public class Threads
{
    private static final Logger logger = LoggerFactory.getLogger(Threads.class);

    /**
     * sleep绛夊緟,鍗曚綅涓烘绉?
     */
    public static void sleep(long milliseconds)
    {
        try
        {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException e)
        {
            return;
        }
    }

    /**
     * 鍋滄绾跨▼姹?
     * 鍏堜娇鐢╯hutdown, 鍋滄鎺ユ敹鏂颁换鍔″苟灏濊瘯瀹屾垚鎵€鏈夊凡瀛樺湪浠诲姟.
     * 濡傛灉瓒呮椂, 鍒欒皟鐢╯hutdownNow, 鍙栨秷鍦╳orkQueue涓璓ending鐨勪换鍔?骞朵腑鏂墍鏈夐樆濉炲嚱鏁?
     * 濡傛灉浠嶇劧瓒呮檪锛屽墖寮峰埗閫€鍑?
     * 鍙﹀鍦╯hutdown鏃剁嚎绋嬫湰韬璋冪敤涓柇鍋氫簡澶勭悊.
     */
    public static void shutdownAndAwaitTermination(ExecutorService pool)
    {
        if (pool != null && !pool.isShutdown())
        {
            pool.shutdown();
            try
            {
                if (!pool.awaitTermination(120, TimeUnit.SECONDS))
                {
                    pool.shutdownNow();
                    if (!pool.awaitTermination(120, TimeUnit.SECONDS))
                    {
                        logger.info("Pool did not terminate");
                    }
                }
            }
            catch (InterruptedException ie)
            {
                pool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 鎵撳嵃绾跨▼寮傚父淇℃伅
     */
    public static void printException(Runnable r, Throwable t)
    {
        if (t == null && r instanceof Future<?>)
        {
            try
            {
                Future<?> future = (Future<?>) r;
                if (future.isDone())
                {
                    future.get();
                }
            }
            catch (CancellationException ce)
            {
                t = ce;
            }
            catch (ExecutionException ee)
            {
                t = ee.getCause();
            }
            catch (InterruptedException ie)
            {
                Thread.currentThread().interrupt();
            }
        }
        if (t != null)
        {
            logger.error(t.getMessage(), t);
        }
    }
}
