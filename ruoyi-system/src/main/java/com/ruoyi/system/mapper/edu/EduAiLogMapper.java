package com.ruoyi.system.mapper.edu;

import java.util.List;
import com.ruoyi.system.domain.edu.EduAiLog;

public interface EduAiLogMapper
{
    List<EduAiLog> selectAiLogList(EduAiLog log);

    int insertAiLog(EduAiLog log);

    Long countAiLogs(EduAiLog log);
}
