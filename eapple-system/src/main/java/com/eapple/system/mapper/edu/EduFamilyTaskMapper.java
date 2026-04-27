package com.eapple.system.mapper.edu;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.eapple.system.domain.edu.EduFamilyTask;

public interface EduFamilyTaskMapper
{
    EduFamilyTask selectTaskById(Long taskId);

    List<EduFamilyTask> selectTaskList(EduFamilyTask task);

    Long countTasks(EduFamilyTask task);

    int insertTask(EduFamilyTask task);

    int updateTask(EduFamilyTask task);

    int deleteTaskByIds(Long[] taskIds);

    Integer sumApprovedPoints(@Param("studentUserId") Long studentUserId, @Param("parentUserId") Long parentUserId);
}
