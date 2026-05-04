package com.qinghe.system.mapper.edu;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.qinghe.system.domain.edu.EduAiLog;

public interface EduAiLogMapper
{
    List<EduAiLog> selectAiLogList(EduAiLog log);

    int insertAiLog(EduAiLog log);

    int deleteAiLogByIds(Long[] logIds);

    int deleteCurrentUserAiLogByIds(@Param("userId") Long userId, @Param("logIds") Long[] logIds);

    Long countAiLogs(EduAiLog log);
}
