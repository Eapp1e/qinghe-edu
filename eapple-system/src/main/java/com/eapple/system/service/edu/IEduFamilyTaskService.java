package com.eapple.system.service.edu;

import java.util.List;
import java.util.Map;
import com.eapple.system.domain.edu.EduFamilyTask;

public interface IEduFamilyTaskService
{
    List<EduFamilyTask> selectTaskList(EduFamilyTask task);

    EduFamilyTask selectTaskById(Long taskId);

    int insertTask(EduFamilyTask task);

    int updateTask(EduFamilyTask task);

    int submitTask(EduFamilyTask task);

    int reviewTask(EduFamilyTask task);

    int deleteTaskByIds(Long[] taskIds);

    Map<String, Object> getTaskSummary();
}
