package com.eapple.system.service.edu;

import java.util.List;
import com.eapple.system.domain.edu.EduAiLog;

public interface IEduAiLogService
{
    List<EduAiLog> selectAiLogList(EduAiLog log);

    List<EduAiLog> selectCurrentUserLogs(EduAiLog log);

    int insertAiLog(EduAiLog log);
}
