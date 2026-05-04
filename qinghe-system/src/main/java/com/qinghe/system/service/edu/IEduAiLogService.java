package com.qinghe.system.service.edu;

import java.util.List;
import java.util.Map;
import com.qinghe.system.domain.edu.EduAiLog;

public interface IEduAiLogService
{
    List<EduAiLog> selectAiLogList(EduAiLog log);

    List<EduAiLog> selectCurrentUserLogs(EduAiLog log);

    Map<String, Long> getAiLogSummary(EduAiLog log);

    Map<String, Long> getCurrentUserAiLogSummary(EduAiLog log);

    int insertAiLog(EduAiLog log);

    int deleteAiLogByIds(Long[] logIds);

    int deleteCurrentUserAiLogByIds(Long[] logIds);
}
