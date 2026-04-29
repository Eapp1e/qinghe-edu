package com.eapple.system.service.impl.edu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.eapple.common.exception.ServiceException;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.system.domain.edu.EduFamilyTask;
import com.eapple.system.domain.edu.EduStudentProfile;
import com.eapple.system.mapper.edu.EduFamilyTaskMapper;
import com.eapple.system.mapper.edu.EduStudentProfileMapper;
import com.eapple.system.service.edu.IEduFamilyTaskService;

@Service
public class EduFamilyTaskServiceImpl implements IEduFamilyTaskService, InitializingBean
{
    @Autowired
    private EduFamilyTaskMapper taskMapper;

    @Autowired
    private EduStudentProfileMapper profileMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void afterPropertiesSet()
    {
        ensureTaskTable();
    }

    @Override
    public List<EduFamilyTask> selectTaskList(EduFamilyTask task)
    {
        ensureTaskTable();
        applyRoleScope(task);
        return filterByBoundStudents(taskMapper.selectTaskList(task));
    }

    @Override
    public EduFamilyTask selectTaskById(Long taskId)
    {
        ensureTaskTable();
        EduFamilyTask task = taskMapper.selectTaskById(taskId);
        ensureTaskAccessible(task);
        return task;
    }

    @Override
    public int insertTask(EduFamilyTask task)
    {
        ensureTaskTable();
        if (!SecurityUtils.hasExactRole("edu_parent"))
        {
            throw new ServiceException("只有家长可以发布家庭任务");
        }
        EduStudentProfile profile = profileMapper.selectProfileByStudentUserId(task.getStudentUserId());
        ensureParentOwnsProfile(profile);
        task.setParentUserId(profile.getParentUserId());
        task.setParentName(profile.getParentName());
        task.setStudentUserId(profile.getStudentUserId());
        task.setStudentName(profile.getStudentName());
        task.setStatus("0");
        task.setCreateBy(SecurityUtils.getUsername());
        normalizeTask(task);
        return taskMapper.insertTask(task);
    }

    @Override
    public int updateTask(EduFamilyTask task)
    {
        ensureTaskTable();
        EduFamilyTask current = selectTaskById(task.getTaskId());
        if (!SecurityUtils.hasExactRole("edu_parent"))
        {
            throw new ServiceException("只有家长可以维护家庭任务");
        }
        if ("1".equals(current.getStatus()) || "2".equals(current.getStatus()))
        {
            throw new ServiceException("孩子已提交或任务已确认，不能再修改任务内容");
        }
        task.setStudentUserId(null);
        task.setParentUserId(null);
        task.setStatus(current.getStatus());
        task.setUpdateBy(SecurityUtils.getUsername());
        normalizeTask(task);
        return taskMapper.updateTask(task);
    }

    @Override
    public int submitTask(EduFamilyTask task)
    {
        ensureTaskTable();
        EduFamilyTask current = selectTaskById(task.getTaskId());
        if (!SecurityUtils.hasExactRole("edu_student") || !SecurityUtils.getUserId().equals(current.getStudentUserId()))
        {
            throw new ServiceException("只能提交自己的家庭任务");
        }
        if ("2".equals(current.getStatus()))
        {
            throw new ServiceException("任务已确认完成，不能重复提交");
        }
        EduFamilyTask update = new EduFamilyTask();
        update.setTaskId(task.getTaskId());
        update.setProofImages(task.getProofImages());
        update.setStudentFeedback(task.getStudentFeedback());
        update.setStatus("1");
        update.setUpdateBy(SecurityUtils.getUsername());
        return taskMapper.updateTask(update);
    }

    @Override
    public int reviewTask(EduFamilyTask task)
    {
        ensureTaskTable();
        EduFamilyTask current = selectTaskById(task.getTaskId());
        if (!SecurityUtils.hasExactRole("edu_parent"))
        {
            throw new ServiceException("只有家长可以确认家庭任务");
        }
        if (!"2".equals(task.getStatus()) && !"3".equals(task.getStatus()))
        {
            throw new ServiceException("请选择确认完成或退回重做");
        }
        EduFamilyTask update = new EduFamilyTask();
        update.setTaskId(current.getTaskId());
        update.setStatus(task.getStatus());
        update.setParentComment(task.getParentComment());
        update.setUpdateBy(SecurityUtils.getUsername());
        return taskMapper.updateTask(update);
    }

    @Override
    public int deleteTaskByIds(Long[] taskIds)
    {
        ensureTaskTable();
        for (Long taskId : taskIds)
        {
            EduFamilyTask task = selectTaskById(taskId);
            if ("2".equals(task.getStatus()))
            {
                throw new ServiceException("已确认完成的任务不能删除");
            }
        }
        return taskMapper.deleteTaskByIds(taskIds);
    }

    @Override
    public Map<String, Object> getTaskSummary()
    {
        ensureTaskTable();
        EduFamilyTask query = new EduFamilyTask();
        applyRoleScope(query);
        List<EduFamilyTask> tasks = filterByBoundStudents(taskMapper.selectTaskList(query));
        Map<String, Object> summary = new HashMap<>();
        summary.put("total", tasks.size());
        summary.put("pending", tasks.stream().filter(item -> "0".equals(item.getStatus())).count());
        summary.put("submitted", tasks.stream().filter(item -> "1".equals(item.getStatus())).count());
        summary.put("approved", tasks.stream().filter(item -> "2".equals(item.getStatus())).count());
        summary.put("points", tasks.stream()
                .filter(item -> "2".equals(item.getStatus()))
                .mapToInt(item -> item.getRewardPoints() == null ? 0 : item.getRewardPoints())
                .sum());
        return summary;
    }

    private void applyRoleScope(EduFamilyTask task)
    {
        if (SecurityUtils.hasExactRole("edu_parent"))
        {
            task.setParentUserId(SecurityUtils.getUserId());
            task.setBoundStudentUserIds(getBoundStudentUserIds().stream().collect(Collectors.toList()));
        }
        if (SecurityUtils.hasExactRole("edu_student"))
        {
            task.setStudentUserId(SecurityUtils.getUserId());
        }
    }

    private void ensureTaskAccessible(EduFamilyTask task)
    {
        if (task == null)
        {
            throw new ServiceException("家庭任务不存在");
        }
        if (SecurityUtils.hasExactRole("edu_parent") && !getBoundStudentUserIds().contains(task.getStudentUserId()))
        {
            throw new ServiceException("只能管理自己绑定孩子的家庭任务");
        }
        if (SecurityUtils.hasExactRole("edu_student") && !SecurityUtils.getUserId().equals(task.getStudentUserId()))
        {
            throw new ServiceException("只能查看自己的家庭任务");
        }
    }

    private List<EduFamilyTask> filterByBoundStudents(List<EduFamilyTask> tasks)
    {
        if (!SecurityUtils.hasExactRole("edu_parent"))
        {
            return tasks;
        }
        Set<Long> boundStudentIds = getBoundStudentUserIds();
        return tasks.stream()
                .filter(task -> boundStudentIds.contains(task.getStudentUserId()))
                .collect(Collectors.toList());
    }

    private Set<Long> getBoundStudentUserIds()
    {
        EduStudentProfile query = new EduStudentProfile();
        query.setParentUserId(SecurityUtils.getUserId());
        return profileMapper.selectProfileList(query).stream()
                .map(EduStudentProfile::getStudentUserId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
    }

    private void ensureParentOwnsProfile(EduStudentProfile profile)
    {
        if (profile == null)
        {
            throw new ServiceException("请选择已绑定的学生");
        }
        if (!SecurityUtils.getUserId().equals(profile.getParentUserId()))
        {
            throw new ServiceException("只能给自己绑定的孩子发布任务");
        }
    }

    private void normalizeTask(EduFamilyTask task)
    {
        if (StringUtils.isEmpty(task.getTaskTitle()))
        {
            throw new ServiceException("请填写任务标题");
        }
        if (StringUtils.isEmpty(task.getTaskType()))
        {
            task.setTaskType("habit");
        }
        if (task.getRewardPoints() == null || task.getRewardPoints() < 0)
        {
            task.setRewardPoints(0);
        }
    }

    private void ensureTaskTable()
    {
        jdbcTemplate.execute("create table if not exists edu_family_task ("
                + "task_id bigint(20) not null auto_increment comment 'task id',"
                + "parent_user_id bigint(20) not null comment 'parent user id',"
                + "parent_name varchar(64) default '' comment 'parent name',"
                + "student_user_id bigint(20) not null comment 'student user id',"
                + "student_name varchar(64) default '' comment 'student name',"
                + "task_title varchar(120) not null comment 'task title',"
                + "task_type varchar(30) default 'habit' comment 'task type',"
                + "task_content varchar(1000) default '' comment 'task content',"
                + "reward_points int(11) default 0 comment 'reward points',"
                + "reward_text varchar(255) default '' comment 'reward text',"
                + "due_date varchar(20) default '' comment 'due date',"
                + "proof_images varchar(1000) default '' comment 'proof images',"
                + "student_feedback varchar(1000) default '' comment 'student feedback',"
                + "parent_comment varchar(1000) default '' comment 'parent comment',"
                + "status char(1) default '0' comment 'status',"
                + "remark varchar(500) default '' comment 'remark',"
                + "create_by varchar(64) default '' comment 'create by',"
                + "create_time datetime comment 'create time',"
                + "update_by varchar(64) default '' comment 'update by',"
                + "update_time datetime comment 'update time',"
                + "primary key (task_id),"
                + "key idx_family_task_parent (parent_user_id),"
                + "key idx_family_task_student (student_user_id)"
                + ") engine=innodb default charset=utf8mb4 comment='family task'");
        seedDemoTasksIfNecessary();
    }

    private void seedDemoTasksIfNecessary()
    {
        Long count = jdbcTemplate.queryForObject("select count(1) from edu_family_task", Long.class);
        if (count != null && count > 0)
        {
            seedDefaultStudentTasksIfNecessary();
            return;
        }
        String sql = "insert into edu_family_task "
                + "(parent_user_id, parent_name, student_user_id, student_name, task_title, task_type, task_content, reward_points, reward_text, due_date, proof_images, student_feedback, parent_comment, status, remark, create_by, create_time, update_by, update_time) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate(), ?, sysdate())";
        jdbcTemplate.update(sql, 112L, "陈女士", 101L, "林小禾", "晚餐后整理餐桌", "chore", "晚餐后主动收拾碗筷、擦干净餐桌，并把餐椅归位。", 8, "周末多看半小时纪录片", "2026-04-28", "/profile/upload/2026/04/table-clean.jpg", "已经擦好桌子，也把椅子摆齐了。", "完成质量很好，继续保持主动性。", "2", "演示数据：家务劳动", "system", "system");
        jdbcTemplate.update(sql, 112L, "陈女士", 101L, "林小禾", "亲子阅读30分钟", "reading", "选择一本喜欢的课外书，连续阅读30分钟，结束后用一句话说出最喜欢的情节。", 10, "兑换一次亲子电影夜", "2026-04-29", "/profile/upload/2026/04/reading-card.jpg", "读了《昆虫记》，最喜欢蝉出土的部分。", "表达很具体，下次可以再补充一个问题。", "2", "演示数据：阅读习惯", "system", "system");
        jdbcTemplate.update(sql, 112L, "陈女士", 102L, "周一诺", "睡前整理书包", "habit", "按照清单整理第二天课程用品，拍照确认桌面和书包。", 6, "周五自选早餐", "2026-04-30", "/profile/upload/2026/04/schoolbag.jpg", "我把语文书和水彩笔都放好了。", "", "1", "演示数据：生活习惯", "system", "system");
        jdbcTemplate.update(sql, 113L, "王先生", 103L, "许晴川", "跳绳练习15分钟", "sport", "晚饭后跳绳15分钟，可以分3组完成，注意热身和补水。", 7, "周末公园骑行", "2026-04-27", "/profile/upload/2026/04/rope.jpg", "跳了三组，一共410个。", "运动量合适，注意拉伸。", "2", "演示数据：运动体能", "system", "system");
        jdbcTemplate.update(sql, 113L, "王先生", 103L, "许晴川", "钢琴分手练习", "art", "练习新曲左右手各10分钟，录一段最顺畅的小节。", 9, "兑换一次喜欢的甜品", "2026-05-01", "", "", "", "0", "演示数据：艺术练习", "system", "system");
        jdbcTemplate.update(sql, 130L, "刘妈妈", 136L, "刘星辰", "给绿植浇水并记录", "chore", "观察家里三盆绿植土壤湿度，适量浇水并记录变化。", 5, "家庭积分5分", "2026-04-28", "/profile/upload/2026/04/plant.jpg", "绿萝土有点干，已经少量浇水。", "观察很细致。", "2", "演示数据：责任意识", "system", "system");
        jdbcTemplate.update(sql, 131L, "赵爸爸", 137L, "赵远航", "一周错题复盘", "study", "整理本周数学错题3道，说清楚错因和下一次检查方法。", 12, "周末多玩20分钟桌游", "2026-04-30", "/profile/upload/2026/04/mistakes.jpg", "有一道是单位换算忘了检查。", "", "1", "演示数据：学习复盘", "system", "system");
        jdbcTemplate.update(sql, 132L, "孙女士", 138L, "孙若溪", "主动问候长辈", "habit", "给爷爷奶奶打一次电话，分享今天学校里一件开心的事。", 6, "兑换一次家庭火锅", "2026-05-02", "", "", "", "0", "演示数据：家庭沟通", "system", "system");
        jdbcTemplate.update(sql, 133L, "何先生", 139L, "何知夏", "房间五分钟归位", "chore", "用5分钟把书桌、床边和玩具区恢复整洁，保留前后对比照片。", 8, "周末选择一次户外路线", "2026-04-29", "/profile/upload/2026/04/room.jpg", "我把积木收到了盒子里。", "可以再加上书桌分类。", "3", "演示数据：退回重做", "system", "system");
        seedDefaultStudentTasksIfNecessary();
    }

    private void seedDefaultStudentTasksIfNecessary()
    {
        Long count = jdbcTemplate.queryForObject("select count(1) from edu_family_task where student_user_id = 113", Long.class);
        if (count != null && count > 0)
        {
            return;
        }
        String sql = "insert into edu_family_task "
                + "(parent_user_id, parent_name, student_user_id, student_name, task_title, task_type, task_content, reward_points, reward_text, due_date, proof_images, student_feedback, parent_comment, status, remark, create_by, create_time, update_by, update_time) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate(), ?, sysdate())";
        jdbcTemplate.update(sql, 112L, "王家长", 113L, "王小明", "周三整理书桌和书包", "habit", "睡前按照清单整理书桌、书包和第二天课程用品，完成后拍照提交。", 8, "周末多看半小时纪录片", "2026-04-29", "", "", "", "0", "学生端演示任务：待完成", "edu_parent", "edu_parent");
        jdbcTemplate.update(sql, 112L, "王家长", 113L, "王小明", "阅读30分钟并摘录一句话", "reading", "选择一本课外书阅读30分钟，摘录一句喜欢的话并写下原因。", 10, "兑换一次亲子电影夜", "2026-04-30", "/profile/upload/family/wang-reading.jpg", "我读了《昆虫记》，摘录了关于蝉的句子。", "", "1", "学生端演示任务：已提交待确认", "edu_parent", "edu_student");
        jdbcTemplate.update(sql, 112L, "王家长", 113L, "王小明", "跳绳练习15分钟", "sport", "晚饭后完成15分钟跳绳练习，可以分组完成，注意热身和拉伸。", 7, "周末公园骑行", "2026-04-28", "/profile/upload/family/wang-rope.jpg", "今天跳了三组，一共360个。", "完成得很认真，继续保持。", "2", "学生端演示任务：已完成", "edu_parent", "edu_parent");
    }
}
