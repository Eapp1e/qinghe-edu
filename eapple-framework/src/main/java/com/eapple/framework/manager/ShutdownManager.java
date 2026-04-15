package com.eapple.framework.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import jakarta.annotation.PreDestroy;

/**
 * 纭繚搴旂敤閫€鍑烘椂鑳藉叧闂悗鍙扮嚎绋?
 *
 * @author Eapp1e
 */
@Component
public class ShutdownManager
{
    private static final Logger logger = LoggerFactory.getLogger("sys-user");

    @PreDestroy
    public void destroy()
    {
        shutdownAsyncManager();
    }

    /**
     * 鍋滄寮傛鎵ц浠诲姟
     */
    private void shutdownAsyncManager()
    {
        try
        {
            logger.info("====鍏抽棴鍚庡彴浠诲姟浠诲姟绾跨▼姹?===");
            AsyncManager.me().shutdown();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }
}
