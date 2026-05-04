package com.qinghe.system.service.impl.edu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qinghe.common.utils.SecurityUtils;
import com.qinghe.system.domain.edu.EduAiLog;
import com.qinghe.system.mapper.edu.EduAiLogMapper;
import com.qinghe.system.service.edu.IEduAiLogService;
import com.qinghe.system.util.EduSchoolScopeUtils;

@Service
public class EduAiLogServiceImpl implements IEduAiLogService
{
    @Autowired
    private EduAiLogMapper aiLogMapper;

    @Override
    public List<EduAiLog> selectAiLogList(EduAiLog log)
    {
        EduSchoolScopeUtils.applySchoolScope(log);
        return aiLogMapper.selectAiLogList(log);
    }

    @Override
    public List<EduAiLog> selectCurrentUserLogs(EduAiLog log)
    {
        if (log == null)
        {
            log = new EduAiLog();
        }
        log.setUserId(SecurityUtils.getUserId());
        EduSchoolScopeUtils.applySchoolScope(log);
        return aiLogMapper.selectAiLogList(log);
    }

    @Override
    public Map<String, Long> getAiLogSummary(EduAiLog log)
    {
        return buildSummary(log);
    }

    @Override
    public Map<String, Long> getCurrentUserAiLogSummary(EduAiLog log)
    {
        if (log == null)
        {
            log = new EduAiLog();
        }
        log.setUserId(SecurityUtils.getUserId());
        return buildSummary(log);
    }

    @Override
    public int insertAiLog(EduAiLog log)
    {
        return aiLogMapper.insertAiLog(log);
    }

    @Override
    public int deleteAiLogByIds(Long[] logIds)
    {
        return aiLogMapper.deleteAiLogByIds(logIds);
    }

    @Override
    public int deleteCurrentUserAiLogByIds(Long[] logIds)
    {
        return aiLogMapper.deleteCurrentUserAiLogByIds(SecurityUtils.getUserId(), logIds);
    }

    private Map<String, Long> buildSummary(EduAiLog baseLog)
    {
        EduAiLog totalQuery = copyLog(baseLog);
        totalQuery.setStatus(null);
        EduSchoolScopeUtils.applySchoolScope(totalQuery);

        EduAiLog successQuery = copyLog(baseLog);
        successQuery.setStatus("success");
        EduSchoolScopeUtils.applySchoolScope(successQuery);

        EduAiLog failedQuery = copyLog(baseLog);
        failedQuery.setStatus("failed");
        EduSchoolScopeUtils.applySchoolScope(failedQuery);

        Map<String, Long> summary = new HashMap<>(4);
        summary.put("total", aiLogMapper.countAiLogs(totalQuery));
        summary.put("success", aiLogMapper.countAiLogs(successQuery));
        summary.put("failed", aiLogMapper.countAiLogs(failedQuery));
        return summary;
    }

    private EduAiLog copyLog(EduAiLog source)
    {
        EduAiLog target = new EduAiLog();
        if (source == null)
        {
            return target;
        }
        target.setBusinessType(source.getBusinessType());
        target.setUserId(source.getUserId());
        return target;
    }
}
