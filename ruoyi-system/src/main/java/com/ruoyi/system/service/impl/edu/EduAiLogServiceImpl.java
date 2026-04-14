package com.ruoyi.system.service.impl.edu;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.edu.EduAiLog;
import com.ruoyi.system.mapper.edu.EduAiLogMapper;
import com.ruoyi.system.service.edu.IEduAiLogService;

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
    public List<EduAiLog> selectCurrentUserLogs()
    {
        EduAiLog log = new EduAiLog();
        log.setUserId(SecurityUtils.getUserId());
        return aiLogMapper.selectAiLogList(log);
    }

    @Override
    public int insertAiLog(EduAiLog log)
    {
        return aiLogMapper.insertAiLog(log);
    }
}
