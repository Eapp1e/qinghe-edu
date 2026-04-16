package com.eapple.system.service.impl.edu;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.system.domain.edu.EduAiLog;
import com.eapple.system.mapper.edu.EduAiLogMapper;
import com.eapple.system.service.edu.IEduAiLogService;

@Service
public class EduAiLogServiceImpl implements IEduAiLogService
{
    @Autowired
    private EduAiLogMapper aiLogMapper;

    @Override
    public List<EduAiLog> selectAiLogList(EduAiLog log)
    {
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
        return aiLogMapper.selectAiLogList(log);
    }

    @Override
    public int insertAiLog(EduAiLog log)
    {
        return aiLogMapper.insertAiLog(log);
    }
}
