package com.ruoyi.system.service.edu;

import java.util.List;
import com.ruoyi.system.domain.edu.EduAiLog;

public interface IEduAiLogService
{
    List<EduAiLog> selectAiLogList(EduAiLog log);

    List<EduAiLog> selectCurrentUserLogs();

    int insertAiLog(EduAiLog log);
}
