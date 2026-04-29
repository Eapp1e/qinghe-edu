-- 青禾智学课后服务平台菜单重构脚本
-- 用途：
-- 1. 在保留基础系统能力的前提下，隐藏与课题无关的默认菜单
-- 2. 新增教育平台专属菜单与角色
-- 3. 让管理员、教师、家长、学生看到更贴近业务的导航结构

-- 隐藏默认系统菜单与当前库中无关的菜单
alter table edu_course modify column week_day varchar(128) default '' comment '上课星期/时段';
set @add_grade_scope_sql = (
  select if(count(*) = 0,
    'alter table edu_course add column grade_scope varchar(128) default '''' comment ''开课年级范围'' after category',
    'select 1')
  from information_schema.columns
  where table_schema = database() and table_name = 'edu_course' and column_name = 'grade_scope'
);
prepare add_grade_scope_stmt from @add_grade_scope_sql;
execute add_grade_scope_stmt;
deallocate prepare add_grade_scope_stmt;

set @add_teacher_type_sql = (
  select if(count(*) = 0,
    'alter table sys_user add column teacher_type varchar(32) default '''' comment ''教师类型'' after avatar',
    'select 1')
  from information_schema.columns
  where table_schema = database() and table_name = 'sys_user' and column_name = 'teacher_type'
);
prepare add_teacher_type_stmt from @add_teacher_type_sql;
execute add_teacher_type_stmt;
deallocate prepare add_teacher_type_stmt;

set @add_course_code_sql = (
  select if(
    count(*) = 0,
    'alter table edu_course add column course_code varchar(32) default null comment ''课程编号'' after course_id',
    'select 1'
  )
  from information_schema.columns
  where table_schema = database() and table_name = 'edu_course' and column_name = 'course_code'
);
prepare add_course_code_stmt from @add_course_code_sql;
execute add_course_code_stmt;
deallocate prepare add_course_code_stmt;

update sys_user set teacher_type = 'computer' where user_id in (111);
update sys_user set teacher_type = 'science' where user_id in (120);
update sys_user set teacher_type = 'science' where user_id in (122);
update sys_user set teacher_type = 'computer' where user_id in (128);
update sys_user set teacher_type = 'humanities' where user_id in (123, 129);
update sys_user set teacher_type = 'art' where user_id in (121);
update sys_user set teacher_type = 'art' where user_id in (127);
update sys_user set teacher_type = 'sports' where user_id in (124);
update sys_user set teacher_type = 'sports' where user_id in (126);
update sys_user set teacher_type = 'general' where user_id in (125);
update edu_course c join sys_user u on u.user_id = c.teacher_user_id set c.teacher_name = u.nick_name where c.teacher_name <> u.nick_name;
update edu_course_enrollment e join edu_course c on c.course_id = e.course_id
set e.teacher_user_id = c.teacher_user_id, e.teacher_name = c.teacher_name
where e.teacher_user_id <> c.teacher_user_id or e.teacher_name <> c.teacher_name;

update edu_course
set category = case
    when category like '%?%' or category is null or category = '' then
        case
            when course_name regexp '体育|足球|篮球|羽毛球|体能' then '体育健康'
            when course_name regexp '编程|Python|机器人|算法' then '计算机编程'
            when course_name regexp '物理|科学|数学|实验' then '理科拓展'
            when course_name regexp '阅读|写作|英语|表达' then '文科阅读'
            when course_name regexp '舞蹈|美术|陶艺|艺术' then '美育实践'
            else '综合素养'
        end
    when category in ('科技创新', '编程思维') then '计算机编程'
    when category in ('艺术素养') then '美育实践'
    when category in ('科学实践', '思维拓展') then '理科拓展'
    when category in ('语言表达') then '文科阅读'
    when category in ('成长支持') then '综合素养'
    else category
end;

update edu_course
set grade_scope = case
    when course_name regexp '七年级' then '七年级'
    when course_name regexp '八年级' then '八年级'
    when course_name regexp '九年级|中考' then '九年级'
    when course_name regexp '初中' or description regexp '七至九年级|七年级|八年级|九年级|初中|中考' then '七年级,八年级,九年级'
    when course_name regexp '少儿|启蒙|小小|绘本|舞蹈|美术|陶艺' then '一年级,二年级,三年级,四年级,五年级,六年级'
    when course_name regexp '篮球|足球|羽毛球|阅读|写作|编程|机器人|科学' then '三年级,四年级,五年级,六年级,七年级,八年级,九年级'
    else '一年级,二年级,三年级,四年级,五年级,六年级,七年级,八年级,九年级'
end
where grade_scope is null or grade_scope = '';

update sys_menu
set visible = '1', update_by = 'edu_platform', update_time = sysdate()
where menu_id in (1, 2, 3, 4, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 500, 501, 2000, 2006, 2008, 2009);

-- 新增教育平台角色
insert into sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark)
select * from (
  select 100 as role_id, '管理员' as role_name, 'edu_admin' as role_key, 100 as role_sort, 1 as data_scope, 1 as menu_check_strictly, 1 as dept_check_strictly, '0' as status, '0' as del_flag, 'edu_platform' as create_by, sysdate() as create_time, '' as update_by, null as update_time, '青禾智学课后服务平台管理员' as remark
  union all
  select 101, '课后服务教师', 'edu_teacher', 101, 2, 1, 1, '0', '0', 'edu_platform', sysdate(), '', null, '青禾智学课后服务平台教师'
  union all
  select 102, '课后服务家长', 'edu_parent', 102, 2, 1, 1, '0', '0', 'edu_platform', sysdate(), '', null, '青禾智学课后服务平台家长'
  union all
  select 103, '课后服务学生', 'edu_student', 103, 2, 1, 1, '0', '0', 'edu_platform', sysdate(), '', null, '青禾智学课后服务平台学生'
) t
where not exists (select 1 from sys_role r where r.role_id = t.role_id or r.role_key = t.role_key);

-- 新增平台主菜单与页面菜单
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select * from (
  select 3000 as menu_id, '课后服务平台' as menu_name, 0 as parent_id, 1 as order_num, 'edu-platform' as path, '' as component, '' as `query`, '' as route_name, 1 as is_frame, 0 as is_cache, 'M' as menu_type, '0' as visible, '0' as status, '' as perms, 'education' as icon, 'edu_platform' as create_by, sysdate() as create_time, '' as update_by, null as update_time, '中小学智能课后服务平台目录' as remark
  union all
  select 3001, '平台首页', 3000, 1, 'dashboard', 'edu/dashboard/index', '', '', 1, 0, 'C', '0', '0', 'edu:dashboard:view', 'dashboard', 'edu_platform', sysdate(), '', null, '平台首页'
  union all
  select 3002, '课程中心', 3000, 2, 'course', 'edu/course/index', '', '', 1, 0, 'C', '0', '0', 'edu:course:list', 'guide', 'edu_platform', sysdate(), '', null, '课程中心'
  union all
  select 3003, '学生档案', 3000, 3, 'student', 'edu/student/index', '', '', 1, 0, 'C', '0', '0', 'edu:student:list', 'peoples', 'edu_platform', sysdate(), '', null, '学生档案'
  union all
  select 3004, '上课记录', 3000, 4, 'enrollment', 'edu/enrollment/index', '', '', 1, 0, 'C', '0', '0', 'edu:enrollment:list', 'form', 'edu_platform', sysdate(), '', null, '课程上课与学习记录'
  union all
  select 3005, '作业问答', 3000, 5, 'question', 'edu/question/index', '', '', 1, 0, 'C', '0', '0', 'edu:question:list', 'message', 'edu_platform', sysdate(), '', null, '作业问题与 AI 解答'
  union all
  select 3006, 'AI中心', 3000, 6, 'ai-center', 'edu/aiCenter/index', '', '', 1, 0, 'C', '0', '0', 'edu:ai:list', 'skill', 'edu_platform', sysdate(), '', null, 'AI 模型与调用中心'
  union all
  select 3007, '平台用户', 3000, 7, 'platform-user', 'edu/platformUser/index', '', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'edu_platform', sysdate(), '', null, '平台用户管理'
  union all
  select 3008, '平台公告', 3000, 8, 'platform-notice', 'edu/platformNotice/index', '', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'message', 'edu_platform', sysdate(), '', null, '平台公告管理'
  union all
  select 3010, '课程查询', 3002, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:course:query', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3011, '课程新增', 3002, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:course:add', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3012, '课程修改', 3002, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:course:edit', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3013, '课程删除', 3002, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:course:remove', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3014, '课程报名', 3002, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:course:enroll', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3020, '学生查询', 3003, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:student:query', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3021, '学生新增', 3003, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:student:add', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3022, '学生修改', 3003, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:student:edit', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3023, '学生删除', 3003, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:student:remove', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3030, '报名编辑', 3004, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:enrollment:edit', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3040, '问题查询', 3005, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:question:query', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3041, '问题新增', 3005, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:question:add', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3042, '问题修改', 3005, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:question:edit', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3043, '问题删除', 3005, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:question:remove', '#', 'edu_platform', sysdate(), '', null, ''
) t
where not exists (select 1 from sys_menu m where m.menu_id = t.menu_id);

-- 重置教育角色菜单授权
delete from sys_role_menu where role_id in (100, 101, 102, 103, 104);

insert ignore into sys_role_menu (role_id, menu_id)
select * from (
  select 1, 3000 union all
  select 1, 3001 union all
  select 1, 3002 union all
  select 1, 3003 union all
  select 1, 3004 union all
  select 1, 3005 union all
  select 1, 3006 union all
  select 1, 3007 union all
  select 1, 3008 union all
  select 1, 3010 union all
  select 1, 3011 union all
  select 1, 3012 union all
  select 1, 3013 union all
  select 1, 3014 union all
  select 1, 3020 union all
  select 1, 3021 union all
  select 1, 3022 union all
  select 1, 3023 union all
  select 1, 3030 union all
  select 1, 3040 union all
  select 1, 3041 union all
  select 1, 3042 union all
  select 1, 3043 union all
  select 100, 3000 union all
  select 100, 3001 union all
  select 100, 3002 union all
  select 100, 3003 union all
  select 100, 3004 union all
  select 100, 3005 union all
  select 100, 3006 union all
  select 100, 3007 union all
  select 100, 3008 union all
  select 100, 1000 union all
  select 100, 1001 union all
  select 100, 1002 union all
  select 100, 1003 union all
  select 100, 1006 union all
  select 100, 101 union all
  select 100, 1007 union all
  select 100, 3010 union all
  select 100, 3011 union all
  select 100, 3012 union all
  select 100, 3013 union all
  select 100, 3014 union all
  select 100, 3020 union all
  select 100, 3021 union all
  select 100, 3022 union all
  select 100, 3023 union all
  select 100, 3030 union all
  select 100, 3040 union all
  select 100, 3041 union all
  select 100, 3042 union all
  select 100, 3043 union all
  select 101, 3000 union all
  select 101, 3001 union all
  select 101, 3002 union all
  select 101, 3004 union all
  select 101, 3005 union all
  select 101, 3006 union all
  select 101, 3010 union all
  select 101, 3011 union all
  select 101, 3012 union all
  select 101, 3014 union all
  select 101, 3030 union all
  select 101, 3040 union all
  select 101, 3041 union all
  select 102, 3000 union all
  select 102, 3001 union all
  select 102, 3002 union all
  select 102, 3004 union all
  select 102, 3005 union all
  select 102, 3006 union all
  select 102, 3010 union all
  select 102, 3014 union all
  select 102, 3040 union all
  select 102, 3041 union all
  select 103, 3000 union all
  select 103, 3001 union all
  select 103, 3002 union all
  select 103, 3003 union all
  select 103, 3004 union all
  select 103, 3005 union all
  select 103, 3006 union all
  select 103, 3010 union all
  select 103, 3014 union all
  select 103, 3020 union all
  select 103, 3040 union all
  select 103, 3041
) t;

-- 如存在演示账号，则绑定教育角色
delete ur
from sys_user_role ur
inner join sys_user u on ur.user_id = u.user_id
where u.user_name in ('edu_admin', 'edu_teacher', 'edu_parent', 'edu_student')
  and ur.role_id in (100, 101, 102, 103, 104);

insert into sys_user_role (user_id, role_id)
select u.user_id, r.role_id
from sys_user u
join sys_role r on (
  (u.user_name = 'edu_admin' and r.role_key = 'edu_admin') or
  (u.user_name = 'edu_teacher' and r.role_key = 'edu_teacher') or
  (u.user_name = 'edu_parent' and r.role_key = 'edu_parent') or
  (u.user_name = 'edu_student' and r.role_key = 'edu_student')
)
where not exists (
  select 1 from sys_user_role ur where ur.user_id = u.user_id and ur.role_id = r.role_id
);

-- 将平台菜单拍平成一级菜单，避免侧栏先展开目录再进入页面
update sys_menu set parent_id = 0, order_num = 1, path = 'index', route_name = 'Index', is_frame = 1, visible = '0', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3001;
update sys_menu set parent_id = 0, order_num = 2, path = 'edu/course', route_name = 'EduCourse', is_frame = 1, visible = '0', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3002;
update sys_menu set parent_id = 0, order_num = 3, path = 'edu/student', route_name = 'EduStudent', is_frame = 1, visible = '0', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3003;
update sys_menu set parent_id = 0, order_num = 4, path = 'edu/enrollment', route_name = 'EduEnrollment', is_frame = 1, visible = '0', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3004;
update sys_menu set menu_name = '上课记录', remark = '课程上课与学习记录', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3004;
update sys_menu set parent_id = 0, order_num = 5, path = 'edu/question', route_name = 'EduQuestion', is_frame = 1, visible = '0', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3005;
update sys_menu set menu_name = 'AI中心', parent_id = 0, order_num = 6, path = 'edu/ai-center', component = 'edu/aiCenter/index', route_name = 'EduAiCenter', is_frame = 1, visible = '0', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3006;
update sys_menu set parent_id = 0, order_num = 7, path = 'edu/platform-user', route_name = 'EduPlatformUser', is_frame = 1, visible = '0', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3007;
update sys_menu set parent_id = 0, order_num = 8, path = 'edu/platform-notice', route_name = 'EduPlatformNotice', is_frame = 1, visible = '0', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3008;

insert ignore into sys_role_menu(role_id, menu_id) values (101, 3008);
insert ignore into sys_role_menu(role_id, menu_id) values (102, 3008);
insert ignore into sys_role_menu(role_id, menu_id) values (103, 3008);
insert ignore into sys_role_menu(role_id, menu_id) values (101, 3003);
insert ignore into sys_role_menu(role_id, menu_id) values (101, 3020);
insert ignore into sys_role_menu(role_id, menu_id) values (102, 3003);
insert ignore into sys_role_menu(role_id, menu_id) values (102, 3020);
update sys_menu set icon = 'chart', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3001;
update sys_menu set icon = 'education', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3002;
update sys_menu set icon = 'user', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3003;
update sys_menu set icon = 'clipboard', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3004;
update sys_menu set icon = 'question', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3005;
update sys_menu set icon = 'skill', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3006;
update sys_menu set icon = 'peoples', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3007;
update sys_menu set icon = 'bell', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3008;
delete from sys_role_menu where menu_id = 3000;
delete from sys_menu where menu_id = 3000;

-- 学生端新增网课中心菜单
insert ignore into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values (3009, '网课中心', 0, 4, 'edu/online-course', 'edu/onlineCourse/index', '', 'EduOnlineCourse', 1, 0, 'C', '0', '0', '', 'monitor', 'edu_platform', sysdate(), '', null, '学生端网课学习资源入口');

insert ignore into sys_role_menu(role_id, menu_id) values (103, 3009);
update sys_menu set icon = 'monitor', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3009;

-- 家长端新增家庭陪学菜单
insert ignore into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values (3091, '家庭陪学', 0, 4, 'edu/parent-companion', 'edu/parentCompanion/index', '', 'EduParentCompanion', 1, 0, 'C', '0', '0', '', 'peoples', 'edu_platform', sysdate(), '', null, '家长端陪学总览入口');

insert ignore into sys_role_menu(role_id, menu_id) values (102, 3091);

-- 演示账号角色归一化，避免教师/家长/学生账号误绑角色
delete from sys_user_role where user_id between 120 and 125;
insert ignore into sys_user_role (user_id, role_id) values
(120, 101), (121, 101), (122, 101), (123, 101), (124, 101), (125, 101);

delete from sys_user_role where user_id between 130 and 135;
insert ignore into sys_user_role (user_id, role_id) values
(130, 102), (131, 102), (132, 102), (133, 102), (134, 102), (135, 102);

delete from sys_user_role where user_id between 136 and 144;
insert ignore into sys_user_role (user_id, role_id) values
(136, 103), (137, 103), (138, 103), (139, 103), (140, 103), (141, 103), (142, 103), (143, 103), (144, 103);

-- 家庭任务/约定：家长发布，学生提交，家长确认积分
create table if not exists edu_family_task (
  task_id bigint(20) not null auto_increment comment '任务ID',
  parent_user_id bigint(20) not null comment '家长用户ID',
  parent_name varchar(64) default '' comment '家长姓名',
  student_user_id bigint(20) not null comment '学生用户ID',
  student_name varchar(64) default '' comment '学生姓名',
  task_title varchar(120) not null comment '任务标题',
  task_type varchar(30) default 'habit' comment '任务类型',
  task_content varchar(1000) default '' comment '任务说明',
  reward_points int(11) default 0 comment '积分奖励',
  reward_text varchar(255) default '' comment '现实奖励',
  due_date varchar(20) default '' comment '截止日期',
  proof_images varchar(1000) default '' comment '完成图片',
  student_feedback varchar(1000) default '' comment '学生反馈',
  parent_comment varchar(1000) default '' comment '家长评语',
  status char(1) default '0' comment '状态 0待完成 1待确认 2已完成 3已退回',
  remark varchar(500) default '' comment '备注',
  create_by varchar(64) default '' comment '创建者',
  create_time datetime comment '创建时间',
  update_by varchar(64) default '' comment '更新者',
  update_time datetime comment '更新时间',
  primary key (task_id),
  key idx_family_task_parent (parent_user_id),
  key idx_family_task_student (student_user_id)
) engine=innodb default charset=utf8mb4 comment='家庭任务表';

insert ignore into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values (3092, '家庭任务', 0, 5, 'edu/family-task', 'edu/familyTask/index', '', 'EduFamilyTask', 1, 0, 'C', '0', '0', '', 'form', 'edu_platform', sysdate(), '', null, '家长发布与学生完成家庭任务入口');

insert ignore into sys_role_menu(role_id, menu_id) values (102, 3092);
insert ignore into sys_role_menu(role_id, menu_id) values (103, 3092);
insert ignore into sys_role_menu(role_id, menu_id) values (100, 3091);
insert ignore into sys_role_menu(role_id, menu_id) values (100, 3092);

insert ignore into edu_family_task
(task_id, parent_user_id, parent_name, student_user_id, student_name, task_title, task_type, task_content, reward_points, reward_text, due_date, status, remark, create_by, create_time)
values
(1, 112, '王家长', 113, '王小明', '阅读 30 分钟', 'reading', '选择一本喜欢的书阅读 30 分钟，并拍下阅读角或书页记录。', 10, '周末多看半小时电视', date_format(date_add(sysdate(), interval 3 day), '%Y-%m-%d'), '0', '家庭任务演示数据', 'edu_parent', sysdate()),
(2, 112, '王家长', 113, '王小明', '整理自己的书桌', 'chore', '把书桌上的学习用品分类摆好，完成后上传整理前后照片。', 8, '周末一起吃一顿喜欢的晚餐', date_format(date_add(sysdate(), interval 2 day), '%Y-%m-%d'), '0', '家庭任务演示数据', 'edu_parent', sysdate());

delete from edu_family_task where task_id between 400 and 419;
insert ignore into edu_family_task
(task_id, parent_user_id, parent_name, student_user_id, student_name, task_title, task_type, task_content, reward_points, reward_text, due_date, proof_images, student_feedback, parent_comment, status, remark, create_by, create_time, update_by, update_time)
values
(400, 130, '李家长', 136, '李沐阳', '餐后整理餐桌', 'chore', '晚饭后擦桌子、摆好椅子，并把餐垫收回原位。', 8, '周末选择一次家庭电影', date_format(date_add(sysdate(), interval 1 day), '%Y-%m-%d'), '/profile/upload/family/table-clean.jpg', '已经整理好餐桌，也把椅子推进去了。', '动作很完整，继续保持主动参与家务。', '2', '家庭任务扩展演示', 'edu_parent_li', date_sub(sysdate(), interval 5 day), 'edu_parent_li', date_sub(sysdate(), interval 4 day)),
(401, 130, '李家长', 137, '李知夏', '睡前亲子共读 20 分钟', 'reading', '选择绘本或桥梁书读 20 分钟，读完讲一个最喜欢的片段。', 10, '兑换一次睡前故事点播权', date_format(date_add(sysdate(), interval 2 day), '%Y-%m-%d'), '', '', '', '0', '家庭任务扩展演示', 'edu_parent_li', date_sub(sysdate(), interval 2 day), '', null),
(402, 131, '张家长', 138, '张可欣', '钢琴音阶练习', 'art', '完成 C 大调音阶与一首短曲练习，注意节奏稳定。', 12, '周末去一次书店', date_format(date_add(sysdate(), interval 2 day), '%Y-%m-%d'), '/profile/upload/family/piano-practice.jpg', '今天练了三遍，第二遍开始节奏更稳。', '', '1', '家庭任务扩展演示', 'edu_parent_zhang', date_sub(sysdate(), interval 3 day), 'edu_student_zhang', date_sub(sysdate(), interval 1 day)),
(403, 132, '陈家长', 139, '陈宇航', '跳绳 300 个', 'sport', '分 3 组完成跳绳，每组结束后记录次数，注意热身和拉伸。', 10, '兑换一次亲子篮球时间', date_format(date_add(sysdate(), interval 1 day), '%Y-%m-%d'), '/profile/upload/family/jump-rope.jpg', '完成了 320 个，最后一组有点累。', '数量达标，注意运动后拉伸。', '2', '家庭任务扩展演示', 'edu_parent_chen', date_sub(sysdate(), interval 4 day), 'edu_parent_chen', date_sub(sysdate(), interval 2 day)),
(404, 133, '吴家长', 140, '吴思言', '整理书包和错题本', 'habit', '按课程分类整理书包，把本周错题贴上标签。', 8, '兑换一次自选早餐', date_format(date_add(sysdate(), interval 3 day), '%Y-%m-%d'), '', '错题本整理好了，但书包还差美术袋。', '请补充整理美术袋后再提交一次。', '3', '家庭任务扩展演示', 'edu_parent_wu', date_sub(sysdate(), interval 3 day), 'edu_parent_wu', date_sub(sysdate(), interval 1 day)),
(405, 133, '吴家长', 141, '吴星辰', '照顾阳台绿植', 'chore', '给两盆绿植浇水，观察叶子状态并记录一句变化。', 6, '兑换一次家庭桌游', date_format(date_add(sysdate(), interval 4 day), '%Y-%m-%d'), '', '', '', '0', '家庭任务扩展演示', 'edu_parent_wu', date_sub(sysdate(), interval 1 day), '', null),
(406, 134, '孙家长', 142, '孙乐彤', '非遗手作收尾', 'art', '把剪纸作品完成边缘修整，拍照上传并说说最满意的地方。', 12, '兑换一次手工材料包', date_format(date_add(sysdate(), interval 2 day), '%Y-%m-%d'), '/profile/upload/family/paper-cut.jpg', '边缘修好了，我最喜欢中间的花纹。', '作品很细致，耐心值得表扬。', '2', '家庭任务扩展演示', 'edu_parent_sun', date_sub(sysdate(), interval 5 day), 'edu_parent_sun', date_sub(sysdate(), interval 4 day)),
(407, 135, '赵家长', 143, '赵子睿', '口算闯关 15 分钟', 'habit', '完成 15 分钟口算练习，记录正确率，不追求速度过快。', 9, '兑换一次周末亲子运动', date_format(date_add(sysdate(), interval 1 day), '%Y-%m-%d'), '/profile/upload/family/math-practice.jpg', '正确率 92%，有两道进位算错了。', '', '1', '家庭任务扩展演示', 'edu_parent_zhao', date_sub(sysdate(), interval 2 day), 'edu_student_zhao_1', date_sub(sysdate(), interval 0 day)),
(408, 135, '赵家长', 144, '赵雨桐', '周末房间归位计划', 'chore', '把书、玩具、衣物分别放回固定位置，完成后拍一张整体照片。', 10, '兑换一次大餐点菜权', date_format(date_add(sysdate(), interval 5 day), '%Y-%m-%d'), '', '', '', '0', '家庭任务扩展演示', 'edu_parent_zhao', date_sub(sysdate(), interval 1 day), '', null);

delete from edu_ai_log where status = 'mock';
delete from edu_ai_log where log_id between 500 and 515;

-- 课程排课逻辑优化：支持一门课多周次、多上课日，前端按当前日期计算最近一次上课与结课状态
update edu_course set week_day = '周一 16:00-17:30；周三 16:00-17:30', start_date = '2026-04-20', end_date = '2026-06-26', start_time = '16:00', end_time = '17:30', remark = '每周2次，共20次左右' where course_id = 1;
update edu_course set week_day = '周二 15:40-17:10；周四 15:40-17:10', start_date = '2026-04-21', end_date = '2026-06-25', start_time = '15:40', end_time = '17:10', remark = '每周2次，作品项目制' where course_id = 2;
update edu_course set week_day = '周五 16:10-17:40', start_date = '2026-04-24', end_date = '2026-06-26', start_time = '16:10', end_time = '17:40', remark = '每周1次，专项训练' where course_id = 3;
update edu_course set week_day = '周一 16:00-17:20；周四 16:00-17:20', start_date = '2026-04-13', end_date = '2026-06-25', start_time = '16:00', end_time = '17:20', remark = '每周2次，思维专题课' where course_id = 4;
update edu_course set week_day = '周三 15:50-17:00', start_date = '2026-04-15', end_date = '2026-05-20', start_time = '15:50', end_time = '17:00', remark = '每周1次，短期启蒙课' where course_id = 5;
update edu_course set week_day = '周二 16:20-17:20；周五 16:20-17:20', start_date = '2026-04-17', end_date = '2026-06-30', start_time = '16:20', end_time = '17:20', remark = '每周2次，口算训练' where course_id = 6;
update edu_course set week_day = '周二 15:50-17:10；周四 15:50-17:10', start_date = '2026-04-14', end_date = '2026-06-25', start_time = '15:50', end_time = '17:10', remark = '每周2次，校园展示排练' where course_id = 7;
update edu_course set week_day = '周日 15:40-17:10', start_date = '2026-03-01', end_date = '2026-04-26', start_time = '15:40', end_time = '17:10', status = '0', remark = '课程已按计划完成，可用于结课后记录与反馈测试' where course_id = 8;
update edu_course set week_day = '周一 16:00-17:30；周三 16:00-17:30', start_date = '2026-05-04', end_date = '2026-06-24', start_time = '16:00', end_time = '17:30', remark = '下周开课，用于未开始状态演示' where course_id = 9;
update edu_course set week_day = '周三 16:10-17:40；周五 16:10-17:40', start_date = '2026-04-15', end_date = '2026-06-26', start_time = '16:10', end_time = '17:40', remark = '每周2次，创客项目课' where course_id = 10;
update edu_course set week_day = '周一 15:40-16:50；周三 15:40-16:50', start_date = '2026-04-20', end_date = '2026-06-24', start_time = '15:40', end_time = '16:50', remark = '每周2次，英语绘本阅读' where course_id = 11;
update edu_course set week_day = '周二 16:00-17:10', start_date = '2026-04-21', end_date = '2026-06-23', start_time = '16:00', end_time = '17:10', remark = '每周1次，表达训练课' where course_id = 12;
update edu_course set week_day = '周五 15:50-17:20', start_date = '2026-04-17', end_date = '2026-06-26', start_time = '15:50', end_time = '17:20', remark = '每周1次，科学实践课' where course_id = 13;
update edu_course set week_day = '周一 16:10-17:30；周四 16:10-17:30', start_date = '2026-04-16', end_date = '2026-06-25', start_time = '16:10', end_time = '17:30', remark = '每周2次，体能训练课' where course_id = 14;
update edu_course set week_day = '周二 15:40-17:00；周五 15:40-17:00', start_date = '2026-04-17', end_date = '2026-06-30', start_time = '15:40', end_time = '17:00', remark = '每周2次，机器人项目课' where course_id = 15;
update edu_course set week_day = '周三 16:00-17:20', start_date = '2026-04-22', end_date = '2026-06-24', start_time = '16:00', end_time = '17:20', remark = '每周1次，书法审美课' where course_id = 16;
update edu_course set week_day = '周四 16:00-17:20', start_date = '2026-04-16', end_date = '2026-06-25', start_time = '16:00', end_time = '17:20', remark = '每周1次，合唱排练课' where course_id = 17;
update edu_course set week_day = '周一 15:50-17:10；周三 15:50-17:10', start_date = '2026-04-20', end_date = '2026-06-24', start_time = '15:50', end_time = '17:10', remark = '每周2次，综合素养课' where course_id = 18;

-- 学习记录与教师反馈补充：按课次保存结构化内容，前端可选择具体上课时间查看和填写。
update edu_course_enrollment
set learning_record = '{"version":1,"fallback":"","sessions":{"2026-04-20-16:00":{"label":"第 1 次课 · 2026-04-20 周一","time":"16:00-17:30","student":"完成图形化编程入门练习，能独立让角色移动和转向。"},"2026-04-22-16:00":{"label":"第 2 次课 · 2026-04-22 周三","time":"16:00-17:30","student":"尝试使用循环积木，知道重复动作可以用循环简化。"}}}',
    interaction_summary = '{"version":1,"fallback":"","sessions":{"2026-04-20-16:00":{"label":"第 1 次课 · 2026-04-20 周一","time":"16:00-17:30","teacher":"课堂参与积极，能跟随示范完成角色移动任务。"},"2026-04-22-16:00":{"label":"第 2 次课 · 2026-04-22 周三","time":"16:00-17:30","teacher":"开始理解循环结构，建议课后用小任务继续练习重复动作。"}}}',
    remark = '课程仍在进行中，结课后将由教师补充整体成长反馈。'
where enrollment_id = 1;

update edu_course_enrollment
set learning_record = '{"version":1,"fallback":"","sessions":{"2026-03-01-15:40":{"label":"第 1 次课 · 2026-03-01 周日","time":"15:40-17:10","student":"完成运球和基础脚步练习，体能消耗较大但能坚持。"},"2026-03-08-15:40":{"label":"第 2 次课 · 2026-03-08 周日","time":"15:40-17:10","student":"投篮姿势比第一次稳定，知道出手后要保持跟随动作。"},"2026-04-26-15:40":{"label":"第 9 次课 · 2026-04-26 周日","time":"15:40-17:10","student":"完成结课小测和分组对抗，能够主动给队友传球。"}}}',
    interaction_summary = '{"version":1,"fallback":"","sessions":{"2026-03-01-15:40":{"label":"第 1 次课 · 2026-03-01 周日","time":"15:40-17:10","teacher":"基础协调性较好，需继续加强核心稳定和脚步节奏。"},"2026-03-08-15:40":{"label":"第 2 次课 · 2026-03-08 周日","time":"15:40-17:10","teacher":"投篮动作有改善，课堂配合度较高。"},"2026-04-26-15:40":{"label":"第 9 次课 · 2026-04-26 周日","time":"15:40-17:10","teacher":"结课对抗表现积极，能理解基础跑位和团队协作。"}}}',
    remark = '本期课程已完成。学生在体能、运球稳定性和团队配合上有明显进步，建议后续继续参加球类专项训练。'
where course_id = 8;

update edu_course_enrollment
set learning_record = ifnull(nullif(learning_record, ''), '{"version":1,"fallback":"","sessions":{}}'),
    interaction_summary = ifnull(nullif(interaction_summary, ''), '{"version":1,"fallback":"","sessions":{}}'),
    remark = ifnull(nullif(remark, ''), '课程进行中，结课后由教师补充整体成长反馈。')
where learning_record is null or learning_record = '' or interaction_summary is null or interaction_summary = '' or remark is null or remark = '';

-- 课程报名演示数据补全：确保 18 门课程均有较充分报名、课中记录与教师反馈，管理端可查看完整链路。
delete from edu_course_enrollment where enrollment_id between 7010 and 7184;

insert ignore into edu_course_enrollment
(enrollment_id, course_id, course_name, student_user_id, student_name, parent_user_id, parent_name,
 teacher_user_id, teacher_name, status, signup_source, learning_record, interaction_summary, remark, create_by, create_time)
select
  7000 + c.course_id * 10 + seed.seq,
  c.course_id,
  c.course_name,
  seed.student_user_id,
  seed.student_name,
  seed.parent_user_id,
  seed.parent_name,
  c.teacher_user_id,
  c.teacher_name,
  '0',
  seed.signup_source,
  seed.learning_record,
  seed.interaction_summary,
  seed.remark,
  seed.create_by,
  date_sub(sysdate(), interval seed.days_ago day)
from edu_course c
join (
  select 0 seq, 136 student_user_id, '李沐阳' student_name, 130 parent_user_id, '李家长' parent_name, 'parent' signup_source, 'edu_parent_li' create_by,
         '课堂参与稳定，能按要求完成当次练习，并主动记录一个收获点。' learning_record,
         '课堂状态积极，基础动作或知识点掌握较稳，可以继续保持。' interaction_summary,
         '真实报名扩展数据，已补充课中记录和教师反馈。' remark, 4 days_ago
  union all
  select 1, 137, '李知夏', 130, '李家长', 'student', 'edu_student_li_2',
         '完成本次核心任务，遇到问题能先尝试再求助。',
         '能主动参与互动，建议家长关注课后复盘习惯。',
         '真实报名扩展数据，已补充课中记录和教师反馈。', 5
  union all
  select 2, 138, '张可欣', 131, '张家长', 'parent', 'edu_parent_zhang',
         '能跟上课堂节奏，作品或练习完成度较好。',
         '本节课完成度较好，下一阶段可加强表达和展示。',
         '真实报名扩展数据，已补充课中记录和教师反馈。', 6
  union all
  select 3, 139, '陈宇航', 132, '陈家长', 'student', 'edu_student_chen',
         '本次课专注度较好，建议课后用 10 分钟做一次复盘。',
         '学习过程认真，建议保持固定练习频率。',
         '真实报名扩展数据，已补充课中记录和教师反馈。', 7
  union all
  select 4, 140, '吴思言', 133, '吴家长', 'parent', 'edu_parent_wu',
         '能够按时完成课中任务，并愿意把课堂成果带回家分享。',
         '课堂表现稳定，建议继续用固定练习时间巩固学习成果。',
         '真实报名扩展数据，已补充课中记录和教师反馈。', 8
) seed
where c.course_id between 1 and 18;

update edu_course_enrollment
set learning_record = '{"version":1,"fallback":"","courseSummary":"本期课程已完成全部课次。学生能够坚持参与课堂训练，能结合个人兴趣完成结课展示，并能说出自己最明显的进步点。","sessions":{"2026-03-01-15:40":{"label":"第 1 次课 · 2026-03-01 周日","time":"15:40-17:10","student":"完成基础热身和入门练习，能跟随老师完成主要动作。"},"2026-03-08-15:40":{"label":"第 2 次课 · 2026-03-08 周日","time":"15:40-17:10","student":"动作或作品完成度较第一次更稳定，能主动记录一个练习要点。"},"2026-04-26-15:40":{"label":"第 9 次课 · 2026-04-26 周日","time":"15:40-17:10","student":"完成结课展示和课堂复盘，能清楚表达本期课程的收获。"}}}',
    interaction_summary = '{"version":1,"fallback":"","courseSummary":"教师结课反馈：学生本期整体参与度较高，在课堂习惯、专项技能和合作表达上均有进步。建议结课后每周保持 1-2 次短时练习，并鼓励孩子用作品或运动记录进行自我复盘。","sessions":{"2026-03-01-15:40":{"label":"第 1 次课 · 2026-03-01 周日","time":"15:40-17:10","teacher":"能够适应课堂节奏，基础动作或任务完成较顺利。"},"2026-03-08-15:40":{"label":"第 2 次课 · 2026-03-08 周日","time":"15:40-17:10","teacher":"本次课参与积极，能在提醒下修正细节。"},"2026-04-26-15:40":{"label":"第 9 次课 · 2026-04-26 周日","time":"15:40-17:10","teacher":"结课展示完成度较好，能体现持续练习后的进步。"}}}',
    remark = '本期课程已结课，学生课程总结与教师结课反馈均已补充。'
where course_id = 8;

update edu_course c
set current_capacity = (
  select count(1) from edu_course_enrollment e where e.course_id = c.course_id and e.status = '0'
)
where c.course_id between 1 and 18;

-- 产品化演示数据补强：新增教师、家长、学生、课程，并按真实热度差异生成报名、问答、亲子任务与家长建议记录。
insert ignore into sys_user (
  user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar,
  password, status, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time,
  update_by, update_time, remark
) values
(126, 103, 'edu_teacher_chen', '陈老师', '00', 'edu_teacher_chen@example.com', '13800000126', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台教师演示账号'),
(127, 103, 'edu_teacher_lin', '林老师', '00', 'edu_teacher_lin@example.com', '13800000127', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台教师演示账号'),
(128, 103, 'edu_teacher_gao', '高老师', '00', 'edu_teacher_gao@example.com', '13800000128', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台教师演示账号'),
(129, 103, 'edu_teacher_hu', '胡老师', '00', 'edu_teacher_hu@example.com', '13800000129', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台教师演示账号'),
(150, 103, 'edu_parent_luo', '罗家长', '00', 'edu_parent_luo@example.com', '13800000150', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台家长演示账号'),
(151, 103, 'edu_parent_zhou', '周家长', '00', 'edu_parent_zhou@example.com', '13800000151', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台家长演示账号'),
(152, 103, 'edu_parent_huang', '黄家长', '00', 'edu_parent_huang@example.com', '13800000152', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台家长演示账号'),
(153, 103, 'edu_parent_xu', '徐家长', '00', 'edu_parent_xu@example.com', '13800000153', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台家长演示账号'),
(154, 103, 'edu_parent_guo', '郭家长', '00', 'edu_parent_guo@example.com', '13800000154', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台家长演示账号'),
(155, 103, 'edu_parent_ma', '马家长', '00', 'edu_parent_ma@example.com', '13800000155', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台家长演示账号'),
(160, 103, 'edu_student_luo_1', '罗一诺', '00', 'edu_student_luo_1@example.com', '13800000160', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生演示账号'),
(161, 103, 'edu_student_luo_2', '罗星澜', '00', 'edu_student_luo_2@example.com', '13800000161', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生演示账号'),
(162, 103, 'edu_student_zhou_1', '周梓涵', '00', 'edu_student_zhou_1@example.com', '13800000162', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生演示账号'),
(163, 103, 'edu_student_zhou_2', '周若辰', '00', 'edu_student_zhou_2@example.com', '13800000163', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生演示账号'),
(164, 103, 'edu_student_huang_1', '黄一鸣', '00', 'edu_student_huang_1@example.com', '13800000164', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生演示账号'),
(165, 103, 'edu_student_huang_2', '黄安琪', '00', 'edu_student_huang_2@example.com', '13800000165', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生演示账号'),
(166, 103, 'edu_student_xu_1', '徐子墨', '00', 'edu_student_xu_1@example.com', '13800000166', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生演示账号'),
(167, 103, 'edu_student_xu_2', '徐若溪', '00', 'edu_student_xu_2@example.com', '13800000167', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生演示账号'),
(168, 103, 'edu_student_guo_1', '郭嘉佑', '00', 'edu_student_guo_1@example.com', '13800000168', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生演示账号'),
(169, 103, 'edu_student_guo_2', '郭芮宁', '00', 'edu_student_guo_2@example.com', '13800000169', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生演示账号'),
(170, 103, 'edu_student_ma_1', '马思远', '00', 'edu_student_ma_1@example.com', '13800000170', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生演示账号'),
(171, 103, 'edu_student_ma_2', '马小禾', '00', 'edu_student_ma_2@example.com', '13800000171', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生演示账号');

insert ignore into sys_user_role (user_id, role_id) values
(126, 101), (127, 101), (128, 101), (129, 101),
(150, 102), (151, 102), (152, 102), (153, 102), (154, 102), (155, 102),
(160, 103), (161, 103), (162, 103), (163, 103), (164, 103), (165, 103), (166, 103), (167, 103), (168, 103), (169, 103), (170, 103), (171, 103);

insert ignore into edu_student_profile (profile_id, student_user_id, student_name, parent_user_id, parent_name, grade_name, class_name, gender, interest_tags, status, remark, create_by, create_time) values
(50, 160, '罗一诺', 150, '罗家长', '二年级', '1班', '女', '舞蹈,阅读,手工', '0', '低年级艺术表达与阅读习惯培养', 'admin', sysdate()),
(51, 161, '罗星澜', 150, '罗家长', '四年级', '3班', '男', '足球,编程,围棋', '0', '逻辑思维与体能并重', 'admin', sysdate()),
(52, 162, '周梓涵', 151, '周家长', '三年级', '2班', '女', '美术,写作,陶艺', '0', '审美表达与写作提升', 'admin', sysdate()),
(53, 163, '周若辰', 151, '周家长', '五年级', '1班', '男', '篮球,科学,机器人', '0', '科学实践和运动专项提升', 'admin', sysdate()),
(54, 164, '黄一鸣', 152, '黄家长', '六年级', '4班', '男', '奥数,围棋,阅读', '0', '升学衔接与思维训练', 'admin', sysdate()),
(55, 165, '黄安琪', 152, '黄家长', '一年级', '2班', '女', '合唱,舞蹈,亲子阅读', '0', '低年级兴趣启蒙', 'admin', sysdate()),
(56, 166, '徐子墨', 153, '徐家长', '四年级', '1班', '男', '编程,足球,演讲', '0', '科技项目与表达训练', 'admin', sysdate()),
(57, 167, '徐若溪', 153, '徐家长', '三年级', '3班', '女', '钢琴,书法,美术', '0', '艺术素养持续培养', 'admin', sysdate()),
(58, 168, '郭嘉佑', 154, '郭家长', '五年级', '2班', '男', '机器人,篮球,科学', '0', '动手实践与团队协作', 'admin', sysdate()),
(59, 169, '郭芮宁', 154, '郭家长', '二年级', '4班', '女', '阅读,合唱,陶艺', '0', '语言表达和审美启蒙', 'admin', sysdate()),
(60, 170, '马思远', 155, '马家长', '六年级', '1班', '男', '足球,体能,奥数', '0', '体能训练与学科挑战', 'admin', sysdate()),
(61, 171, '马小禾', 155, '马家长', '一年级', '1班', '女', '手工,绘本,舞蹈', '0', '生活习惯和艺术兴趣启蒙', 'admin', sysdate());

insert ignore into edu_course (course_id, course_name, category, teacher_user_id, teacher_name, campus, week_day, start_time, end_time, start_date, end_date, max_capacity, current_capacity, status, description, ai_notice, ai_suggestion, remark, create_by, create_time) values
(19, '围棋启蒙与思维', '思维拓展', 111, '孙老师', '明德楼 204', '周二 16:00-17:20；周五 16:00-17:20', '16:00', '17:20', '2026-04-21', '2026-06-26', 24, 0, '0', '通过围棋入门培养规则意识、空间判断和复盘习惯。', '', '', '每周2次，适合低中年级思维启蒙', 'admin', sysdate()),
(20, '校园足球联赛训练', '体育健康', 126, '陈老师', '操场东区', '周一 16:20-17:40；周四 16:20-17:40', '16:20', '17:40', '2026-04-20', '2026-06-25', 30, 0, '0', '结合校内联赛进行带球、传接球和基础战术训练。', '', '', '每周2次，偏团队对抗', 'admin', sysdate()),
(21, '少儿舞蹈形体', '艺术素养', 127, '林老师', '艺术楼 302', '周三 15:50-17:10', '15:50', '17:10', '2026-04-22', '2026-06-24', 22, 0, '0', '提升身体协调、节奏感和舞台表现力。', '', '', '每周1次，作品展示制', 'admin', sysdate()),
(22, 'Python 创意编程', '计算机编程', 128, '高老师', '创客教室 1', '周二 16:10-17:40；周六 10:00-11:30', '16:10', '17:40', '2026-04-25', '2026-07-04', 26, 0, '0', '用 Python 完成小游戏、数据可视化和自动化小项目。', '', '', '每周2次，含周末项目课', 'admin', sysdate()),
(23, '阅读写作提升班', '语言表达', 129, '胡老师', '博雅楼 105', '周一 15:50-17:10；周三 15:50-17:10', '15:50', '17:10', '2026-05-06', '2026-06-24', 28, 0, '0', '通过整本书阅读、素材积累和短文训练提升表达能力。', '', '', '五一后开课，用于待开课测试', 'admin', sysdate()),
(24, '陶艺与生活美学', '艺术素养', 127, '林老师', '艺术楼 101', '周五 16:00-17:30', '16:00', '17:30', '2026-04-17', '2026-06-26', 20, 0, '0', '体验泥塑、釉色和生活器物制作，培养审美和耐心。', '', '', '每周1次，小班制', 'admin', sysdate());

delete from edu_course_enrollment where enrollment_id between 800000 and 802499;
insert ignore into edu_course_enrollment
(enrollment_id, course_id, course_name, student_user_id, student_name, parent_user_id, parent_name, teacher_user_id, teacher_name, status, signup_source, learning_record, interaction_summary, remark, create_by, create_time)
select 800000 + t.course_id * 100 + p.pos, c.course_id, c.course_name, p.student_user_id, p.student_name, p.parent_user_id, p.parent_name,
       c.teacher_user_id, c.teacher_name, '0', if(mod(p.pos + t.course_id, 2) = 0, 'parent', 'student'),
       p.learning_record, p.interaction_summary,
       if(c.course_id = 8, '已结课课程报名数据，包含课程总结与教师结课反馈。', '真实报名扩展数据，覆盖不同学生和课程热度。'),
       p.create_by, date_sub(sysdate(), interval (p.pos + t.course_id) day)
from edu_course c
join (
  select 1 course_id, 9 extra_count union all select 2, 3 union all select 3, 1 union all select 4, 12 union all select 5, 0 union all select 6, 5 union all
  select 7, 2 union all select 8, 3 union all select 9, 10 union all select 10, 1 union all select 11, 7 union all select 12, 0 union all
  select 13, 4 union all select 14, 6 union all select 15, 11 union all select 16, 1 union all select 17, 3 union all select 18, 8 union all
  select 19, 10 union all select 20, 14 union all select 21, 4 union all select 22, 8 union all select 23, 6 union all select 24, 12
) t on t.course_id = c.course_id
join (
  select 1 pos, 113 student_user_id, '王小明' student_name, 112 parent_user_id, '王家长' parent_name, 'edu_student' create_by, '我完成了本次课堂练习，课后还想再做一个小项目。' learning_record, '课堂参与积极，能主动提问并按要求完成练习。' interaction_summary union all
  select 2, 136, '李沐阳', 130, '李家长', 'edu_student_li_1', '能跟上课堂节奏，愿意把课堂内容讲给家长听。', '专注度较好，建议继续保持固定复盘。' union all
  select 3, 137, '李知夏', 130, '李家长', 'edu_student_li_2', '今天遇到难题时先尝试了两种办法。', '有独立思考过程，适合增加挑战任务。' union all
  select 4, 138, '张可欣', 131, '张家长', 'edu_student_zhang', '完成作品后能说出自己的设计想法。', '表达意愿较强，建议多给展示机会。' union all
  select 5, 139, '陈宇航', 132, '陈家长', 'edu_student_chen', '训练量比较大，但我坚持完成了。', '体能和执行力不错，注意运动后拉伸。' union all
  select 6, 140, '吴思言', 133, '吴家长', 'edu_student_wu_1', '课堂上我主动回答了一次问题。', '互动状态良好，语言表达更自然。' union all
  select 7, 141, '吴星辰', 133, '吴家长', 'edu_student_wu_2', '实验环节很有意思，我记录了观察结果。', '科学探究兴趣明显，记录习惯有进步。' union all
  select 8, 142, '孙乐彤', 134, '孙家长', 'edu_student_sun', '我完成了手工作品，并整理了材料。', '耐心和整理意识较好，可以继续鼓励。' union all
  select 9, 143, '赵子睿', 135, '赵家长', 'edu_student_zhao_1', '本次练习速度更快，错误也少了一些。', '练习质量提升，建议关注准确率稳定。' union all
  select 10, 144, '赵雨桐', 135, '赵家长', 'edu_student_zhao_2', '我能按步骤完成任务，也愿意帮同学。', '合作意识较好，适合参与小组展示。' union all
  select 11, 160, '罗一诺', 150, '罗家长', 'edu_student_luo_1', '今天学会了一个新的动作，回家还练了两遍。', '节奏感较好，动作细节还可继续打磨。' union all
  select 12, 161, '罗星澜', 150, '罗家长', 'edu_student_luo_2', '我能把步骤写下来，调试时没有马上放弃。', '逻辑表达清楚，遇到错误能逐步定位。' union all
  select 13, 162, '周梓涵', 151, '周家长', 'edu_student_zhou_1', '作品完成后我给它起了名字。', '审美表达投入，建议多描述创作理由。' union all
  select 14, 163, '周若辰', 151, '周家长', 'edu_student_zhou_2', '分组合作时我负责记录和汇报。', '团队角色意识较强，汇报表达可继续练习。' union all
  select 15, 164, '黄一鸣', 152, '黄家长', 'edu_student_huang_1', '我完成了挑战题，但最后一步有点急。', '思维能力较好，建议放慢审题节奏。' union all
  select 16, 165, '黄安琪', 152, '黄家长', 'edu_student_huang_2', '我敢上台展示了，虽然还有点紧张。', '舞台适应能力提升，继续鼓励表达。' union all
  select 17, 166, '徐子墨', 153, '徐家长', 'edu_student_xu_1', '今天的项目我能独立完成主要功能。', '动手能力强，适合加入拓展任务。' union all
  select 18, 167, '徐若溪', 153, '徐家长', 'edu_student_xu_2', '我认真完成了练习，也记录了需要改进的地方。', '学习态度稳定，细节意识较好。' union all
  select 19, 168, '郭嘉佑', 154, '郭家长', 'edu_student_guo_1', '小组任务里我负责搭建和测试。', '执行力较好，团队沟通还可加强。' union all
  select 20, 169, '郭芮宁', 154, '郭家长', 'edu_student_guo_2', '阅读后我能讲出最喜欢的一段。', '表达自然，建议增加完整复述训练。' union all
  select 21, 170, '马思远', 155, '马家长', 'edu_student_ma_1', '今天训练比较累，但完成了目标。', '坚持度较高，注意强度和恢复。' union all
  select 22, 171, '马小禾', 155, '马家长', 'edu_student_ma_2', '我把材料收拾好了，也完成了作品。', '生活习惯和动手能力都有提升。'
) p on mod(p.pos + t.course_id * 3, 22) < t.extra_count;

update edu_course c
set current_capacity = (select count(1) from edu_course_enrollment e where e.course_id = c.course_id and e.status = '0')
where c.course_id between 1 and 24;

-- 产品化演示数据补充：中学生、已结课课程、真实结课记录与家长陪学建议。
insert ignore into sys_user (
  user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar,
  password, status, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time,
  update_by, update_time, remark
) values
(172, 103, 'edu_student_luo_3', '罗星辰', '00', 'edu_student_luo_3@example.com', '13800000172', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '初中学生演示账号'),
(173, 103, 'edu_student_zhou_3', '周云朵', '00', 'edu_student_zhou_3@example.com', '13800000173', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '初中学生演示账号'),
(174, 103, 'edu_student_huang_3', '黄嘉树', '00', 'edu_student_huang_3@example.com', '13800000174', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '初中学生演示账号'),
(175, 103, 'edu_student_xu_3', '徐沐阳', '00', 'edu_student_xu_3@example.com', '13800000175', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '初中学生演示账号'),
(176, 103, 'edu_student_guo_3', '郭予安', '00', 'edu_student_guo_3@example.com', '13800000176', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '初中学生演示账号'),
(177, 103, 'edu_student_ma_3', '马景行', '00', 'edu_student_ma_3@example.com', '13800000177', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '初中学生演示账号');

insert ignore into sys_user_role (user_id, role_id) values
(172, 103), (173, 103), (174, 103), (175, 103), (176, 103), (177, 103);

insert ignore into edu_student_profile
(profile_id, student_user_id, student_name, parent_user_id, parent_name, grade_name, class_name, gender, interest_tags, status, remark, create_by, create_time)
values
(62, 172, '罗星辰', 150, '罗家长', '七年级', '3班', '男', 'Python,数学建模,篮球', '0', '初中阶段关注逻辑表达与自主管理', 'admin', sysdate()),
(63, 173, '周云朵', 151, '周家长', '八年级', '2班', '女', '写作,戏剧,英语阅读', '0', '初中阶段关注表达自信与阅读输出', 'admin', sysdate()),
(64, 174, '黄嘉树', 152, '黄家长', '九年级', '1班', '男', '中考体育,物理实验,奥数', '0', '毕业年级关注体能节奏与学科复盘', 'admin', sysdate()),
(65, 175, '徐沐阳', 153, '徐家长', '七年级', '4班', '男', '机器人,编程,足球', '0', '初中阶段关注项目整理和团队协作', 'admin', sysdate()),
(66, 176, '郭予安', 154, '郭家长', '八年级', '1班', '女', '篮球,演讲,书法', '0', '初中阶段关注表达训练与运动习惯', 'admin', sysdate()),
(67, 177, '马景行', 155, '马家长', '九年级', '2班', '男', '阅读,劳动实践,心理调适', '0', '毕业年级关注压力管理与习惯稳定', 'admin', sysdate());

insert ignore into edu_course
(course_id, course_name, category, teacher_user_id, teacher_name, campus, week_day, start_time, end_time, start_date, end_date, max_capacity, current_capacity, status, description, ai_notice, ai_suggestion, remark, create_by, create_time)
values
(25, '中考体育体能冲刺', '运动健康', 111, '何老师', '体育馆A区', '周一 17:00-18:20；周三 17:00-18:20', '17:00', '18:20', '2026-03-09', '2026-04-20', 28, 0, '0', '面向九年级学生，围绕耐力跑、跳绳、核心力量和考前恢复建立训练节奏。', '', '', '已按计划结课，方便测试 edu_teacher 的结课记录。', 'edu_teacher', date_sub(sysdate(), interval 48 day)),
(26, '初中数学思维专题', '学科拓展', 126, '陈老师', '明理楼204', '周二 17:10-18:30', '17:10', '18:30', '2026-03-10', '2026-04-21', 24, 0, '0', '面向七八年级学生，训练数形结合、分类讨论和审题表达。', '', '', '已结课，用于管理端查看结课总结。', 'edu_teacher_chen', date_sub(sysdate(), interval 47 day)),
(27, '七年级阅读写作营', '阅读写作', 129, '胡老师', '书香教室', '周四 17:00-18:20', '17:00', '18:20', '2026-03-12', '2026-04-23', 26, 0, '0', '面向初一学生，围绕素材积累、段落组织和现场表达完成写作训练。', '', '', '已结课，用于家长端和管理端查看学习总结。', 'edu_teacher_hu', date_sub(sysdate(), interval 45 day));

update sys_user set nick_name = '罗星辰', remark = '初中学生演示账号' where user_id = 172;
update sys_user set nick_name = '周云朵', remark = '初中学生演示账号' where user_id = 173;
update sys_user set nick_name = '黄嘉树', remark = '初中学生演示账号' where user_id = 174;
update sys_user set nick_name = '徐沐阳', remark = '初中学生演示账号' where user_id = 175;
update sys_user set nick_name = '郭予安', remark = '初中学生演示账号' where user_id = 176;
update sys_user set nick_name = '马景行', remark = '初中学生演示账号' where user_id = 177;

update edu_student_profile set student_name = '罗星辰', parent_name = '罗家长', grade_name = '七年级', class_name = '3班', gender = '男', interest_tags = 'Python,数学建模,篮球', remark = '初中阶段关注逻辑表达与自主管理' where profile_id = 62;
update edu_student_profile set student_name = '周云朵', parent_name = '周家长', grade_name = '八年级', class_name = '2班', gender = '女', interest_tags = '写作,戏剧,英语阅读', remark = '初中阶段关注表达自信与阅读输出' where profile_id = 63;
update edu_student_profile set student_name = '黄嘉树', parent_name = '黄家长', grade_name = '九年级', class_name = '1班', gender = '男', interest_tags = '中考体育,物理实验,奥数', remark = '毕业年级关注体能节奏与学科复盘' where profile_id = 64;
update edu_student_profile set student_name = '徐沐阳', parent_name = '徐家长', grade_name = '七年级', class_name = '4班', gender = '男', interest_tags = '机器人,编程,足球', remark = '初中阶段关注项目整理和团队协作' where profile_id = 65;
update edu_student_profile set student_name = '郭予安', parent_name = '郭家长', grade_name = '八年级', class_name = '1班', gender = '女', interest_tags = '篮球,演讲,书法', remark = '初中阶段关注表达训练与运动习惯' where profile_id = 66;
update edu_student_profile set student_name = '马景行', parent_name = '马家长', grade_name = '九年级', class_name = '2班', gender = '男', interest_tags = '阅读,劳动实践,心理调适', remark = '毕业年级关注压力管理与习惯稳定' where profile_id = 67;

update edu_course set course_name = '中考体育体能冲刺', teacher_name = '何老师', campus = '体育馆A区', week_day = '周一 17:00-18:20；周三 17:00-18:20', description = '面向九年级学生，围绕耐力跑、跳绳、核心力量和考前恢复建立训练节奏。', remark = '已按计划结课，方便测试 edu_teacher 的结课记录。' where course_id = 25;
update edu_course set course_name = '初中数学思维专题', teacher_name = '陈老师', campus = '明理楼204', week_day = '周二 17:10-18:30', description = '面向七八年级学生，训练数形结合、分类讨论和审题表达。', remark = '已结课，用于管理端查看结课总结。' where course_id = 26;
update edu_course set course_name = '七年级阅读写作营', teacher_name = '胡老师', campus = '书香教室', week_day = '周四 17:00-18:20', description = '面向初一学生，围绕素材积累、段落组织和现场表达完成写作训练。', remark = '已结课，用于家长端和管理端查看学习总结。' where course_id = 27;

delete from edu_course_enrollment where enrollment_id between 7250 and 7279;
insert ignore into edu_course_enrollment
(enrollment_id, course_id, course_name, student_user_id, student_name, parent_user_id, parent_name, teacher_user_id, teacher_name, status, signup_source, learning_record, interaction_summary, remark, create_by, create_time)
values
(7250, 25, '中考体育体能冲刺', 113, '王小明', 112, '王家长', 111, '何老师', '0', 'parent',
'{"version":1,"fallback":"","courseSummary":"这门课让我更清楚每周训练要有节奏，耐力跑不再只靠硬撑，跳绳也能按组数安排。","sessions":{"2026-03-09-17:00":{"label":"第 1 次课 · 2026-03-09 周一","time":"17:00-18:20","student":"完成基础体测，耐力跑后半程有些吃力，但能坚持到最后。"},"2026-03-23-17:00":{"label":"第 5 次课 · 2026-03-23 周一","time":"17:00-18:20","student":"跳绳连续次数提升，知道训练后要拉伸小腿。"},"2026-04-20-17:00":{"label":"第 13 次课 · 2026-04-20 周一","time":"17:00-18:20","student":"完成结课测试，耐力跑配速比第一次稳定。"}}}',
'{"version":1,"fallback":"","sessions":{"2026-03-09-17:00":{"label":"第 1 次课 · 2026-03-09 周一","time":"17:00-18:20","teacher":"体能基础尚可，后程配速需要建立分段意识。"},"2026-03-23-17:00":{"label":"第 5 次课 · 2026-03-23 周一","time":"17:00-18:20","teacher":"跳绳节奏明显改善，核心训练完成度较好。"},"2026-04-20-17:00":{"label":"第 13 次课 · 2026-04-20 周一","time":"17:00-18:20","teacher":"结课测试完成认真，配速和恢复意识都有进步。"}}}',
'本期课程已结课。学生在耐力、跳绳和训练恢复意识上均有提升，建议后续保持每周三次短时训练。', 'edu_parent', date_sub(sysdate(), interval 44 day)),
(7251, 25, '中考体育体能冲刺', 174, '黄嘉树', 152, '黄家长', 111, '何老师', '0', 'student',
'{"version":1,"fallback":"","courseSummary":"我最明显的进步是学会了先控制速度，再逐步加速，考前不会一开始就跑太快。","sessions":{"2026-03-09-17:00":{"label":"第 1 次课 · 2026-03-09 周一","time":"17:00-18:20","student":"第一次测试前半程冲太快，后面掉速明显。"},"2026-04-01-17:00":{"label":"第 8 次课 · 2026-04-01 周三","time":"17:00-18:20","student":"按老师给的分段节奏跑，最后一圈还能提速。"},"2026-04-20-17:00":{"label":"第 13 次课 · 2026-04-20 周一","time":"17:00-18:20","student":"结课测试完成，跳绳和耐力跑都比开课稳定。"}}}',
'{"version":1,"fallback":"","sessions":{"2026-03-09-17:00":{"label":"第 1 次课 · 2026-03-09 周一","time":"17:00-18:20","teacher":"爆发力较好，但体能分配需要调整。"},"2026-04-01-17:00":{"label":"第 8 次课 · 2026-04-01 周三","time":"17:00-18:20","teacher":"能够执行分段配速，训练自控力提升。"},"2026-04-20-17:00":{"label":"第 13 次课 · 2026-04-20 周一","time":"17:00-18:20","teacher":"考前训练状态稳定，可保持轻量巩固。"}}}',
'本期课程已结课。学生执行训练计划的能力增强，建议考前以保持状态和恢复为主。', 'edu_student_huang_3', date_sub(sysdate(), interval 42 day)),
(7260, 26, '初中数学思维专题', 172, '罗星辰', 150, '罗家长', 126, '陈老师', '0', 'parent',
'{"version":1,"fallback":"","courseSummary":"我学会了先把条件画出来，再判断要不要分类讨论，做题时比以前不容易漏条件。","sessions":{"2026-03-10-17:10":{"label":"第 1 次课 · 2026-03-10 周二","time":"17:10-18:30","student":"数形结合题一开始画图不完整，后来按老师提示补了条件。"},"2026-03-31-17:10":{"label":"第 4 次课 · 2026-03-31 周二","time":"17:10-18:30","student":"能把题目分成两种情况讨论，但表达还要更清楚。"},"2026-04-21-17:10":{"label":"第 7 次课 · 2026-04-21 周二","time":"17:10-18:30","student":"完成结课综合题，知道先列条件表。"}}}',
'{"version":1,"fallback":"","sessions":{"2026-03-10-17:10":{"label":"第 1 次课 · 2026-03-10 周二","time":"17:10-18:30","teacher":"逻辑兴趣较强，但需要把思考过程写完整。"},"2026-03-31-17:10":{"label":"第 4 次课 · 2026-03-31 周二","time":"17:10-18:30","teacher":"分类讨论意识建立，书写规范仍需巩固。"},"2026-04-21-17:10":{"label":"第 7 次课 · 2026-04-21 周二","time":"17:10-18:30","teacher":"结课综合题完成较好，能主动检查条件遗漏。"}}}',
'本期课程已结课。学生在建模意识和条件整理方面进步明显，建议继续保持错题复盘。', 'edu_parent_luo', date_sub(sysdate(), interval 40 day)),
(7270, 27, '七年级阅读写作营', 173, '周云朵', 151, '周家长', 129, '胡老师', '0', 'parent',
'{"version":1,"fallback":"","courseSummary":"我现在写作文会先列素材，再确定最想表达的感受，段落之间比以前顺。","sessions":{"2026-03-12-17:00":{"label":"第 1 次课 · 2026-03-12 周四","time":"17:00-18:20","student":"素材很多但不知道怎么选，课堂上用了三格素材卡。"},"2026-04-02-17:00":{"label":"第 4 次课 · 2026-04-02 周四","time":"17:00-18:20","student":"完成一段景物描写，老师提醒我加入动作细节。"},"2026-04-23-17:00":{"label":"第 7 次课 · 2026-04-23 周四","time":"17:00-18:20","student":"结课现场写作完成，能先列提纲再写。"}}}',
'{"version":1,"fallback":"","sessions":{"2026-03-12-17:00":{"label":"第 1 次课 · 2026-03-12 周四","time":"17:00-18:20","teacher":"素材积累较丰富，需训练取舍和主题聚焦。"},"2026-04-02-17:00":{"label":"第 4 次课 · 2026-04-02 周四","time":"17:00-18:20","teacher":"细节描写有进步，可以继续加入感官体验。"},"2026-04-23-17:00":{"label":"第 7 次课 · 2026-04-23 周四","time":"17:00-18:20","teacher":"结课写作结构清晰，能围绕主题组织材料。"}}}',
'本期课程已结课。学生在素材筛选和段落组织上明显提升，建议每周保留一篇观察短记。', 'edu_parent_zhou', date_sub(sysdate(), interval 39 day));

delete from edu_ai_log where log_id between 9312 and 9325;
insert ignore into edu_ai_log
(log_id, business_type, biz_id, user_id, user_name, role_type, prompt_content, response_content, model_name, status, risk_flag, error_message, prompt_tokens, completion_tokens, latency_ms, create_time)
values
(9312, 'parent_diagnosis', 172, 150, 'edu_parent_luo', 'parent', '学生：罗星辰；近期关注：初中后作业量变大，做数学题会拖到很晚。', '孩子当前状态：进入初中后任务切换成本增加，拖延更多来自计划不清而不是单纯不愿意。\n家长可以怎么做：把作业按“必做、可后置、需求助”三类写在纸上，先完成最小可推进任务。\n本周家庭陪伴小约定：连续三天记录开始时间和卡住点，家长只做复盘不临时批评。', 'deepseek-ai/DeepSeek-V3', 'success', 'normal', '', 142, 198, 171, date_sub(sysdate(), interval 9 hour)),
(9313, 'parent_diagnosis', 173, 151, 'edu_parent_zhou', 'parent', '学生：周云朵；近期关注：作文想法很多，但写出来总是散。', '孩子当前状态：素材储备丰富，但主题聚焦和段落顺序还需要支架。\n家长可以怎么做：写作前只问三个问题：最想表达什么、哪两个细节最能说明、最后一句要留下什么感受。\n本周家庭陪伴小约定：每次写作前用 5 分钟完成三句话提纲。', 'Qwen/Qwen2.5-7B-Instruct', 'success', 'normal', '', 126, 182, 144, date_sub(sysdate(), interval 19 hour)),
(9314, 'parent_diagnosis', 174, 152, 'edu_parent_huang', 'parent', '学生：黄嘉树；近期关注：临近中考，训练和学习都紧张，晚上容易烦躁。', '孩子当前状态：压力水平上升，最需要稳定节奏和可完成目标。\n家长可以怎么做：晚上不临时追加任务，把复盘控制在 10 分钟内，先确认当天完成了什么。\n本周家庭陪伴小约定：睡前只做“明天三件事”清单，避免反复讨论成绩。', 'deepseek-ai/DeepSeek-V3', 'success', 'normal', '', 138, 191, 163, date_sub(sysdate(), interval 2 day)),
(9315, 'parent_diagnosis', 175, 153, 'edu_parent_xu', 'parent', '学生：徐沐阳；近期关注：机器人项目喜欢动手，但不愿意写过程记录。', '孩子当前状态：实践兴趣强，记录意识还没有和成就感绑定。\n家长可以怎么做：不用要求长篇总结，先让孩子拍一张图并写一句“我改了哪里”。\n本周家庭陪伴小约定：每次项目结束只留一张照片、一句改动、一句下次想试。', 'Qwen/Qwen2.5-7B-Instruct', 'success', 'normal', '', 120, 176, 139, date_sub(sysdate(), interval 3 day)),
(9316, 'parent_diagnosis', 176, 154, 'edu_parent_guo', 'parent', '学生：郭予安；近期关注：篮球训练后精神很好，但写作业坐不住。', '孩子当前状态：运动后兴奋度较高，需要过渡仪式帮助切换到学习状态。\n家长可以怎么做：训练回家后安排固定的洗漱、补水、整理书包 15 分钟，再进入短时学习。\n本周家庭陪伴小约定：运动日只设置 20 分钟学习启动任务，完成后再休息。', 'Qwen/Qwen2.5-7B-Instruct', 'success', 'normal', '', 128, 181, 150, date_sub(sysdate(), interval 4 day)),
(9317, 'parent_diagnosis', 177, 155, 'edu_parent_ma', 'parent', '学生：马景行；近期关注：九年级后不太愿意和家长聊学校里的压力。', '孩子当前状态：自主性增强，直接追问容易被理解为控制。\n家长可以怎么做：把问题从“你怎么了”换成“今天有没有一件事需要我配合”，给孩子选择是否展开。\n本周家庭陪伴小约定：每晚只留 10 分钟安静聊天窗口，孩子不说也不追问。', 'deepseek-ai/DeepSeek-V3', 'success', 'normal', '', 135, 190, 158, date_sub(sysdate(), interval 6 day));

update edu_course c
set current_capacity = (select count(1) from edu_course_enrollment e where e.course_id = c.course_id and e.status = '0')
where c.course_id between 1 and 27;

delete from edu_homework_question where question_id between 9100 and 9129;
insert ignore into edu_homework_question
(question_id, course_id, course_name, student_user_id, student_name, parent_user_id, teacher_user_id, question_title, question_content, ai_answer, answer_status, safety_flag, remark, create_by, create_time)
select 9100 + q.seq, e.course_id, e.course_name, e.student_user_id, e.student_name, e.parent_user_id, e.teacher_user_id,
       q.question_title, q.question_content, q.ai_answer, q.answer_status, q.safety_flag, '真实作业问答演示数据', q.create_by, date_sub(sysdate(), interval q.days_ago day)
from (
  select 1 seq, 113 student_user_id, 1 course_id, '循环积木为什么会停不下来？' question_title, '我在编程课里用了重复执行，角色一直移动到边界外，怎么控制次数？' question_content, '可以把“重复执行”改成“重复执行 10 次”，或在循环里加入碰到边缘就停止的判断。先明确想重复几次，再选择计数循环或条件循环。' ai_answer, '1' answer_status, 'normal' safety_flag, 'edu_student' create_by, 1 days_ago union all
  select 2, 113, 2, '水彩颜色混在一起变脏怎么办？', '我画天空时蓝色和黄色混在一起变成灰灰的，怎么避免？', '先等上一层颜色半干后再叠第二层，调色时减少互补色直接混合。可以先在草稿纸试色，再上到作品。', '1', 'normal', 'edu_student', 2 union all
  select 3, 113, 19, '围棋打吃和提子有什么区别？', '我知道要把对方棋子围住，但什么时候算打吃，什么时候能提子？', '打吃是对方只剩最后一口气的提醒状态，提子是你下一步把最后一口气占住后，把对方棋子拿走。', '1', 'normal', 'edu_parent', 3 union all
  select 4, 136, 11, '绘本复述总是漏细节怎么办？', '孩子能读完故事，但复述时只说开头和结尾，中间细节记不住。', '可以用“三格复述法”：发生了什么、遇到什么问题、怎么解决。先不追求完整，重点让孩子抓住故事转折。', '1', 'normal', 'edu_parent_li', 4 union all
  select 5, 137, 22, 'Python 缩进报错怎么看？', '代码看起来没错，但是运行提示 indentation error。', 'Python 用缩进表示代码块。检查 if、for、while 后面的下一行是否统一缩进，建议统一使用 4 个空格。', '1', 'normal', 'edu_student_li_2', 1 union all
  select 6, 138, 24, '陶艺作品开裂是什么原因？', '杯子边缘晾干后有小裂纹，是泥太干了吗？', '常见原因是厚薄不均或干燥太快。制作时保持厚度接近，晾干时避免强风直吹。', '1', 'normal', 'edu_student_zhang', 5 union all
  select 7, 139, 20, '足球传球总是偏怎么办？', '孩子传球方向不稳，力量也忽大忽小，课后怎么练？', '先练固定距离脚内侧推传，目标放在 3-5 米，重点看支撑脚方向和触球部位，不急着加力量。', '1', 'normal', 'edu_parent_chen', 6 union all
  select 8, 140, 23, '作文开头不会写', '写观察日记时总是第一句想很久，有没有简单办法？', '可以用“时间+地点+看到的变化”开头，例如“傍晚，我在阳台发现绿萝的叶子垂下来了”。先写清楚画面。', '1', 'normal', 'edu_student_wu_1', 2 union all
  select 9, 141, 13, '主持时忘词怎么办？', '上台时一紧张就忘词，怎么练更有效？', '把稿子拆成关键词卡片，用关键词串起内容。练习时先看卡片说，再逐步减少提示。', '1', 'normal', 'edu_student_wu_2', 8 union all
  select 10, 142, 17, '劳动课工具整理清单', '孩子做完手工常忘记收剪刀胶水，有没有清单？', '可以设置“工具归位三步”：尖锐工具先归盒，胶水盖紧，桌面碎纸入袋。完成后由孩子自己打勾。', '1', 'normal', 'edu_parent_sun', 3 union all
  select 11, 143, 6, '口算正确率不稳定', '孩子速度快但容易漏进位，要不要降低速度？', '先把目标从速度调为正确率，连续 3 天正确率超过 90% 后再逐步加速。', '1', 'normal', 'edu_parent_zhao', 4 union all
  select 12, 144, 18, '非遗手作怎么写心得？', '孩子做完作品不知道心得写什么。', '可以写三点：我用了什么材料、最难的一步是什么、下次想改进哪里。', '1', 'normal', 'edu_student_zhao_2', 7 union all
  select 13, 160, 21, '舞蹈动作跟不上节拍', '我动作记住了，但是音乐一快就乱。', '先用慢速数拍练习，把动作卡在 1、3、5、7 拍上，熟悉后再跟音乐。', '1', 'normal', 'edu_student_luo_1', 1 union all
  select 14, 161, 22, '变量名能用中文吗？', 'Python 里变量名可以写中文吗，老师建议用英文是为什么？', '技术上可以，但建议用简短英文或拼音，方便阅读、协作和避免编码问题。', '1', 'normal', 'edu_student_luo_2', 2 union all
  select 15, 162, 2, '美术作业构图太空', '画面上半部分很空，怎么补充不显乱？', '可以加背景层次，比如远处树影、天空纹理或小物件，但颜色要比主体浅，避免抢重点。', '1', 'normal', 'edu_parent_zhou', 5 union all
  select 16, 163, 10, '机器人传感器不灵敏', '巡线时小车总是冲出去，是传感器问题吗？', '先检查传感器距离地面是否合适，再校准黑白阈值。速度过快也会导致来不及识别。', '1', 'normal', 'edu_student_zhou_2', 6 union all
  select 17, 164, 5, '奥数题看不懂条件', '题目条件很多时，孩子不知道从哪一句开始。', '先圈出数量关系词，再把条件画成线段图或表格。不要急着算，先把信息整理出来。', '1', 'normal', 'edu_parent_huang', 3 union all
  select 18, 165, 7, '合唱声音太小', '孩子在合唱里不敢唱出声，怕自己跑调。', '可以先在家跟伴奏轻声唱，再逐步提高音量。家长重点鼓励“敢开口”，不要先纠正每个音。', '1', 'normal', 'edu_parent_huang', 4 union all
  select 19, 166, 15, '足球绕杆怎么提速？', '绕杆时球总离脚很远，速度上不去。', '先降低速度保持球在一步以内，再练外脚背轻触。稳定后再缩短杆距。', '1', 'normal', 'edu_student_xu_1', 2 union all
  select 20, 167, 16, '书法横画总歪', '横画写着写着就往右下斜。', '注意起笔前坐姿和纸张角度，运笔时看终点位置，不要只盯笔尖。', '1', 'normal', 'edu_student_xu_2', 7
) q
join edu_course_enrollment e on e.student_user_id = q.student_user_id and e.course_id = q.course_id;

delete from edu_family_task where task_id between 9200 and 9224;
insert ignore into edu_family_task
(task_id, parent_user_id, parent_name, student_user_id, student_name, task_title, task_type, task_content, reward_points, reward_text, due_date, proof_images, student_feedback, parent_comment, status, remark, create_by, create_time, update_by, update_time)
values
(9200, 112, '王家长', 113, '王小明', '编程课后复盘 15 分钟', 'study', '把今天编程课遇到的一个报错写下来，并说出自己怎么解决。', 12, '周末选择一个亲子小游戏', date_format(date_add(sysdate(), interval 2 day), '%Y-%m-%d'), '', '', '', '0', '真实亲子任务演示', 'edu_parent', date_sub(sysdate(), interval 1 day), '', null),
(9201, 112, '王家长', 113, '王小明', '整理书桌和错题夹', 'chore', '把书桌分成学习用品区、课外书区和错题夹区，整理后上传照片。', 10, '兑换半小时电视时间', date_format(date_add(sysdate(), interval 1 day), '%Y-%m-%d'), '/profile/upload/family/default-proof.svg', '已经整理好，错题夹放在右上角。', '', '1', '真实亲子任务演示', 'edu_parent', date_sub(sysdate(), interval 3 day), 'edu_student', date_sub(sysdate(), interval 1 day)),
(9202, 112, '王家长', 113, '王小明', '晚饭后洗自己的碗', 'chore', '晚饭后把自己的碗筷洗干净并放回沥水架。', 8, '兑换一次饭后散步路线选择权', date_format(date_add(sysdate(), interval -1 day), '%Y-%m-%d'), '/profile/upload/family/default-proof.svg', '我洗完了，也把桌面擦了一下。', '完成得很主动，动作也比上周熟练。', '2', '真实亲子任务演示', 'edu_parent', date_sub(sysdate(), interval 5 day), 'edu_parent', date_sub(sysdate(), interval 2 day)),
(9203, 150, '罗家长', 160, '罗一诺', '舞蹈动作打卡', 'art', '练习课堂动作 10 分钟，录一段最熟悉的动作。', 9, '兑换一次舞蹈服配饰选择权', date_format(date_add(sysdate(), interval 3 day), '%Y-%m-%d'), '', '', '', '0', '真实亲子任务演示', 'edu_parent_luo', date_sub(sysdate(), interval 1 day), '', null),
(9204, 150, '罗家长', 161, '罗星澜', '围棋复盘一盘棋', 'habit', '用棋盘摆出今天最关键的 5 手棋，说说为什么。', 12, '兑换一次周末下棋挑战', date_format(date_add(sysdate(), interval 2 day), '%Y-%m-%d'), '/profile/upload/family/default-proof.svg', '我摆了最后一段，发现自己漏看了一口气。', '复盘很认真，下次可以再说说对手的选择。', '2', '真实亲子任务演示', 'edu_parent_luo', date_sub(sysdate(), interval 4 day), 'edu_parent_luo', date_sub(sysdate(), interval 2 day)),
(9205, 151, '周家长', 162, '周梓涵', '观察日记素材收集', 'reading', '观察家里一件物品，写下颜色、形状、用途三个词。', 7, '兑换一次睡前故事点播', date_format(date_add(sysdate(), interval 1 day), '%Y-%m-%d'), '', '我写了陶瓷杯：白色、圆形、喝水。', '词语准确，再补一个触感会更完整。', '3', '真实亲子任务演示', 'edu_parent_zhou', date_sub(sysdate(), interval 2 day), 'edu_parent_zhou', date_sub(sysdate(), interval 1 day)),
(9206, 152, '黄家长', 164, '黄一鸣', '奥数错题讲给家长听', 'study', '选一道本周错题，只讲思路不追求重新算很多题。', 10, '兑换一次户外运动安排权', date_format(date_add(sysdate(), interval 2 day), '%Y-%m-%d'), '', '', '', '0', '真实亲子任务演示', 'edu_parent_huang', date_sub(sysdate(), interval 1 day), '', null),
(9207, 153, '徐家长', 166, '徐子墨', 'Python 小项目命名整理', 'habit', '把项目文件按日期和主题命名，并写一句用途说明。', 8, '兑换一次编程项目展示时间', date_format(date_add(sysdate(), interval 2 day), '%Y-%m-%d'), '/profile/upload/family/default-proof.svg', '我把小游戏文件改名了，也写了说明。', '', '1', '真实亲子任务演示', 'edu_parent_xu', date_sub(sysdate(), interval 3 day), 'edu_student_xu_1', date_sub(sysdate(), interval 1 day)),
(9208, 154, '郭家长', 168, '郭嘉佑', '运动后拉伸打卡', 'sport', '足球或篮球训练后完成 5 分钟拉伸，并说出哪个动作最放松。', 6, '兑换一次亲子投篮赛', date_format(date_add(sysdate(), interval 1 day), '%Y-%m-%d'), '/profile/upload/family/default-proof.svg', '我拉伸了小腿，感觉跑完没那么酸。', '动作到位，继续保持训练后恢复。', '2', '真实亲子任务演示', 'edu_parent_guo', date_sub(sysdate(), interval 5 day), 'edu_parent_guo', date_sub(sysdate(), interval 3 day)),
(9209, 155, '马家长', 171, '马小禾', '睡前绘本共读', 'reading', '睡前共读 15 分钟，孩子选一页讲最喜欢的画面。', 6, '兑换一次绘本选择权', date_format(date_add(sysdate(), interval 3 day), '%Y-%m-%d'), '', '', '', '0', '真实亲子任务演示', 'edu_parent_ma', date_sub(sysdate(), interval 1 day), '', null);

delete from edu_ai_log where log_id between 9300 and 9311;
insert ignore into edu_ai_log
(log_id, business_type, biz_id, user_id, user_name, role_type, prompt_content, response_content, model_name, status, risk_flag, error_message, prompt_tokens, completion_tokens, latency_ms, create_time)
values
(9300, 'parent_diagnosis', 113, 112, 'edu_parent', 'parent', '学生：王小明；家长补充关注：编程兴趣很高，但遇到报错容易急。', '孩子当前状态：对编程探索有兴趣，说明学习动机较好；情绪急躁主要来自“错误=失败”的理解。\n家长可以怎么做：把报错当作线索，陪孩子先读现象，再找最近一次修改。\n本周家庭陪伴小约定：每次练习只记录一个报错和一个解决办法。', 'Qwen/Qwen2.5-7B-Instruct', 'success', 'normal', '', 132, 186, 146, date_sub(sysdate(), interval 2 hour)),
(9301, 'parent_diagnosis', 113, 112, 'edu_parent', 'parent', '学生：王小明；家长补充关注：作业启动慢，放学后总想先玩。', '孩子当前状态：任务启动需要外部结构，但不是没有责任感。\n家长可以怎么做：把“先写作业”改成“先完成 15 分钟最小任务”，完成后给短休息。\n本周家庭陪伴小约定：连续三天记录开始时间，只看是否准时启动。', 'deepseek-ai/DeepSeek-V3', 'success', 'normal', '', 126, 178, 152, date_sub(sysdate(), interval 1 day)),
(9302, 'parent_diagnosis', 160, 150, 'edu_parent_luo', 'parent', '学生：罗一诺；家长补充关注：舞蹈课开心，但在家练习怕动作不好看。', '孩子当前状态：愿意表达但对评价敏感。\n家长可以怎么做：先夸“敢练习”和“能坚持”，再只选一个动作细节反馈。\n本周家庭陪伴小约定：每天练 8 分钟，只记录一个最自然的动作。', 'Qwen/Qwen2.5-7B-Instruct', 'success', 'normal', '', 118, 165, 138, date_sub(sysdate(), interval 3 day)),
(9303, 'parent_diagnosis', 161, 150, 'edu_parent_luo', 'parent', '学生：罗星澜；家长补充关注：围棋复盘不愿意承认自己下错。', '孩子当前状态：胜负心较强，复盘时容易把错误理解成否定。\n家长可以怎么做：先问“这一步还有没有别的选择”，避免直接说错。\n本周家庭陪伴小约定：每盘只复盘一手棋，并说出两个可选方案。', 'Qwen/Qwen2.5-7B-Instruct', 'success', 'normal', '', 121, 174, 142, date_sub(sysdate(), interval 5 day)),
(9304, 'parent_diagnosis', 164, 152, 'edu_parent_huang', 'parent', '学生：黄一鸣；家长补充关注：奥数题能做难题，但基础题容易粗心。', '孩子当前状态：思维挑战动机强，但检查习惯还没有稳定。\n家长可以怎么做：不要增加题量，改为每次挑两题做“反向检查”。\n本周家庭陪伴小约定：每天只复盘一道粗心题，写出漏看的条件。', 'deepseek-ai/DeepSeek-V3', 'success', 'normal', '', 130, 184, 155, date_sub(sysdate(), interval 4 day)),
(9305, 'parent_diagnosis', 166, 153, 'edu_parent_xu', 'parent', '学生：徐子墨；家长补充关注：项目做得快，但文件和资料很乱。', '孩子当前状态：执行和创造力较强，整理习惯需要工具化支持。\n家长可以怎么做：用固定命名模板替代口头提醒。\n本周家庭陪伴小约定：每个项目文件名包含日期、主题和版本号。', 'Qwen/Qwen2.5-7B-Instruct', 'success', 'normal', '', 116, 168, 134, date_sub(sysdate(), interval 6 day));

-- 产品化数据治理：修正教师排课冲突、清理学生时间冲突报名，并补充中学段未结课课程。
update edu_course set week_day = '周六 10:00-11:30', start_time = '10:00', end_time = '11:30', description = '面向七至九年级学生，围绕传感器、结构搭建和项目调试完成机器人实践。', remark = '每周1次，周末项目实践课，避免与教师其他课程冲突。' where course_id = 10;
update edu_course set week_day = '周六 14:00-15:20', start_time = '14:00', end_time = '15:20', remark = '每周1次，围棋思维训练，周末开课。' where course_id = 19;
update edu_course set week_day = '周六 09:00-10:20', start_time = '09:00', end_time = '10:20', remark = '已按计划结课，安排在周末上午，方便测试 edu_teacher 的结课记录。' where course_id = 25;

delete from edu_course_enrollment where enrollment_id in (120,121,123,124,800701,802201,802301,802401,7010,7020,7040,7050,7060,7090,7120,7130,7140,7150,7160,7170,7180,802202,802302,802402,7011,7021,7061,7071,7091,7111,7131,7141,7151,7161,7171,7181,802103,802203,802403,7042,7052,7062,7072,7092,7112,7122,7132,7142,7152,7162,7172,7182,802104,802204,802404,7013,7023,7033,7053,7063,7073,7093,7113,7123,7133,7143,7173,7183,802105,802205,802405,7014,7034,7044,7054,7064,7074,7094,7124,7144);
delete from edu_course_enrollment where enrollment_id in (7154,7164,7174,7184,802006,802106,802206,111,801307,801507,802007,802207,801308,801508,802008,115,801509,800410,802010,801111,802011,801112,801812,802012,801113,801813,802013,801114,801814,802014,801115,801715,801815,802015,800416,801716,801816,802016,800417,801117,801717,801817,802017,800418,801618,801818,802018,800419,800919,801819,802019,802319,800420,800920,802320,800421,800921,802321,802421,800922,802222,802322,802422);

insert ignore into edu_course
(course_id, course_name, category, teacher_user_id, teacher_name, campus, week_day, start_time, end_time, start_date, end_date, max_capacity, current_capacity, status, description, ai_notice, ai_suggestion, remark, create_by, create_time)
values
(28, '初中物理实验探究', '科学实践', 128, '高老师', '科学实验室B', '周六 15:40-17:10', '15:40', '17:10', '2026-04-25', '2026-06-20', 24, 0, '0', '面向七至九年级学生，通过电路、力学与光学小实验训练观察、记录和解释能力。', '', '', '中学段开设中课程，周末实验实践。', 'edu_teacher_gao', date_sub(sysdate(), interval 5 day)),
(29, '七年级 Python 算法启蒙', '编程思维', 126, '陈老师', '机房2', '周日 09:30-11:00', '09:30', '11:00', '2026-04-26', '2026-06-28', 22, 0, '0', '面向七年级学生，从变量、条件判断和简单算法任务入手，建立初中编程思维。', '', '', '中学段开设中课程，周日上午。', 'edu_teacher_chen', date_sub(sysdate(), interval 4 day)),
(30, '八年级英语戏剧表达', '语言表达', 127, '林老师', '多功能教室', '周三 17:30-18:40', '17:30', '18:40', '2026-05-06', '2026-06-24', 20, 0, '0', '面向八年级学生，通过短剧台词、情境表达和小组合作提升英语输出信心。', '', '', '中学段待开课课程，方便测试待开课状态。', 'edu_teacher_lin', date_sub(sysdate(), interval 2 day)),
(31, '九年级心理减压与时间管理', '成长支持', 129, '胡老师', '心理活动室', '周五 18:20-19:20', '18:20', '19:20', '2026-04-24', '2026-05-29', 18, 0, '0', '面向九年级学生，围绕考前压力、任务拆解和家庭沟通建立稳定节奏。', '', '', '中学段开设中课程，覆盖成长支持场景。', 'edu_teacher_hu', date_sub(sysdate(), interval 3 day));

delete from edu_course_enrollment where enrollment_id between 8280 and 8323;
insert ignore into edu_course_enrollment
(enrollment_id, course_id, course_name, student_user_id, student_name, parent_user_id, parent_name, teacher_user_id, teacher_name, status, signup_source, learning_record, interaction_summary, remark, create_by, create_time)
values
(8280, 28, '初中物理实验探究', 174, '黄嘉树', 152, '黄家长', 128, '高老师', '0', 'parent', '{"version":1,"fallback":"","sessions":{"2026-04-25-15:40":{"label":"第 1 次课 · 2026-04-25 周六","time":"15:40-17:10","student":"完成电路连接实验，知道先检查电源和开关再记录现象。"}}}', '{"version":1,"fallback":"","sessions":{"2026-04-25-15:40":{"label":"第 1 次课 · 2026-04-25 周六","time":"15:40-17:10","teacher":"实验操作认真，记录表需要补充变量控制说明。"}}}', '课程开设中，结课后补充整体实验能力反馈。', 'edu_parent_huang', date_sub(sysdate(), interval 2 day)),
(8281, 28, '初中物理实验探究', 175, '徐沐阳', 153, '徐家长', 128, '高老师', '0', 'student', '{"version":1,"fallback":"","sessions":{"2026-04-25-15:40":{"label":"第 1 次课 · 2026-04-25 周六","time":"15:40-17:10","student":"小组里负责记录数据，发现同一电路接法不同会影响灯泡亮度。"}}}', '{"version":1,"fallback":"","sessions":{"2026-04-25-15:40":{"label":"第 1 次课 · 2026-04-25 周六","time":"15:40-17:10","teacher":"能主动提出现象差异，建议下次记录更完整的实验步骤。"}}}', '课程开设中，结课后补充整体实验能力反馈。', 'edu_student_xu_3', date_sub(sysdate(), interval 2 day)),
(8290, 29, '七年级 Python 算法启蒙', 172, '罗星辰', 150, '罗家长', 126, '陈老师', '0', 'parent', '{"version":1,"fallback":"","sessions":{"2026-04-26-09:30":{"label":"第 1 次课 · 2026-04-26 周日","time":"09:30-11:00","student":"完成变量和输入输出练习，能用代码计算简单总分。"}}}', '{"version":1,"fallback":"","sessions":{"2026-04-26-09:30":{"label":"第 1 次课 · 2026-04-26 周日","time":"09:30-11:00","teacher":"逻辑理解较快，建议课后整理变量命名规则。"}}}', '课程开设中，结课后补充算法学习反馈。', 'edu_parent_luo', date_sub(sysdate(), interval 1 day)),
(8291, 29, '七年级 Python 算法启蒙', 175, '徐沐阳', 153, '徐家长', 126, '陈老师', '0', 'student', '{"version":1,"fallback":"","sessions":{"2026-04-26-09:30":{"label":"第 1 次课 · 2026-04-26 周日","time":"09:30-11:00","student":"学会用 input 获取内容，但字符串和数字转换还容易混。"}}}', '{"version":1,"fallback":"","sessions":{"2026-04-26-09:30":{"label":"第 1 次课 · 2026-04-26 周日","time":"09:30-11:00","teacher":"动手积极，需重点巩固数据类型转换。"}}}', '课程开设中，结课后补充算法学习反馈。', 'edu_student_xu_3', date_sub(sysdate(), interval 1 day)),
(8300, 30, '八年级英语戏剧表达', 173, '周云朵', 151, '周家长', 127, '林老师', '0', 'parent', '{"version":1,"fallback":"","sessions":{}}', '{"version":1,"fallback":"","sessions":{}}', '课程待开课，开课后记录课堂表达表现。', 'edu_parent_zhou', date_sub(sysdate(), interval 1 day)),
(8301, 30, '八年级英语戏剧表达', 176, '郭予安', 154, '郭家长', 127, '林老师', '0', 'parent', '{"version":1,"fallback":"","sessions":{}}', '{"version":1,"fallback":"","sessions":{}}', '课程待开课，开课后记录课堂表达表现。', 'edu_parent_guo', date_sub(sysdate(), interval 1 day)),
(8310, 31, '九年级心理减压与时间管理', 174, '黄嘉树', 152, '黄家长', 129, '胡老师', '0', 'parent', '{"version":1,"fallback":"","sessions":{"2026-04-24-18:20":{"label":"第 1 次课 · 2026-04-24 周五","time":"18:20-19:20","student":"完成压力温度计记录，发现自己晚上复盘太久会影响睡眠。"}}}', '{"version":1,"fallback":"","sessions":{"2026-04-24-18:20":{"label":"第 1 次课 · 2026-04-24 周五","time":"18:20-19:20","teacher":"能准确识别压力来源，建议家庭沟通聚焦可执行计划。"}}}', '课程开设中，结课后补充压力管理反馈。', 'edu_parent_huang', date_sub(sysdate(), interval 2 day)),
(8311, 31, '九年级心理减压与时间管理', 177, '马景行', 155, '马家长', 129, '胡老师', '0', 'parent', '{"version":1,"fallback":"","sessions":{"2026-04-24-18:20":{"label":"第 1 次课 · 2026-04-24 周五","time":"18:20-19:20","student":"把周末任务拆成三类，感觉没有之前那么乱。"}}}', '{"version":1,"fallback":"","sessions":{"2026-04-24-18:20":{"label":"第 1 次课 · 2026-04-24 周五","time":"18:20-19:20","teacher":"任务拆解意识较好，建议继续用短清单管理复习节奏。"}}}', '课程开设中，结课后补充压力管理反馈。', 'edu_parent_ma', date_sub(sysdate(), interval 2 day)),
(8320, 14, '羽毛球基础班', 177, '马景行', 155, '马家长', 124, '孙老师', '0', 'parent', '{"version":1,"fallback":"","sessions":{"2026-04-27-16:10":{"label":"第 1 次课 · 2026-04-27 周一","time":"16:10-17:30","student":"掌握正手握拍和高远球准备动作，步伐衔接还需要练习。"}}}', '{"version":1,"fallback":"","sessions":{"2026-04-27-16:10":{"label":"第 1 次课 · 2026-04-27 周一","time":"16:10-17:30","teacher":"体能基础较好，建议下次重点练习启动步和击球点。"}}}', '课程开设中，结课后补充运动能力反馈。', 'edu_parent_ma', date_sub(sysdate(), interval 1 day)),
(8321, 21, '少儿舞蹈形体', 160, '罗一诺', 150, '罗家长', 127, '林老师', '0', 'parent', '{"version":1,"fallback":"","sessions":{"2026-04-22-15:50":{"label":"第 1 次课 · 2026-04-22 周三","time":"15:50-17:10","student":"跟随音乐完成基础站姿和手位练习，节奏感比上次更稳定。"}}}', '{"version":1,"fallback":"","sessions":{"2026-04-22-15:50":{"label":"第 1 次课 · 2026-04-22 周三","time":"15:50-17:10","teacher":"课堂投入度高，动作舒展性可以继续提升。"}}}', '课程开设中，结课后补充形体表现反馈。', 'edu_parent_luo', date_sub(sysdate(), interval 4 day)),
(8322, 22, 'Python 创意编程', 172, '罗星辰', 150, '罗家长', 128, '高老师', '0', 'student', '{"version":1,"fallback":"","sessions":{"2026-04-25-10:00":{"label":"第 1 次课 · 2026-04-25 周六","time":"10:00-11:30","student":"完成猜数字小游戏主体逻辑，能主动调试输入错误。"}}}', '{"version":1,"fallback":"","sessions":{"2026-04-25-10:00":{"label":"第 1 次课 · 2026-04-25 周六","time":"10:00-11:30","teacher":"代码思路清晰，建议继续补充异常输入处理。"}}}', '课程开设中，结课后补充项目学习反馈。', 'edu_student_luo_3', date_sub(sysdate(), interval 3 day)),
(8323, 23, '阅读写作提升班', 173, '周云朵', 151, '周家长', 129, '胡老师', '0', 'parent', '{"version":1,"fallback":"","sessions":{}}', '{"version":1,"fallback":"","sessions":{}}', '课程待开课，开课后记录阅读表达表现。', 'edu_parent_zhou', date_sub(sysdate(), interval 1 day));

update edu_course
set category = case
    when category like '%?%' or category is null or category = '' then
        case
            when course_name regexp '体育|足球|篮球|羽毛球|体能' then '体育健康'
            when course_name regexp '编程|Python|机器人|算法' then '计算机编程'
            when course_name regexp '物理|科学|数学|实验' then '理科拓展'
            when course_name regexp '阅读|写作|英语|表达' then '文科阅读'
            when course_name regexp '舞蹈|美术|陶艺|艺术' then '美育实践'
            else '综合素养'
        end
    when category in ('科技创新', '编程思维') then '计算机编程'
    when category in ('艺术素养') then '美育实践'
    when category in ('科学实践', '思维拓展') then '理科拓展'
    when category in ('语言表达') then '文科阅读'
    when category in ('成长支持') then '综合素养'
    else category
end;

update edu_course
set grade_scope = case
    when course_name regexp '七年级' then '七年级'
    when course_name regexp '八年级' then '八年级'
    when course_name regexp '九年级|中考' then '九年级'
    when course_name regexp '初中' or description regexp '七至九年级|七年级|八年级|九年级|初中|中考' then '七年级,八年级,九年级'
    when course_name regexp '少儿|启蒙|小小|绘本|舞蹈|美术|陶艺' then '一年级,二年级,三年级,四年级,五年级,六年级'
    when course_name regexp '篮球|足球|羽毛球|阅读|写作|编程|机器人|科学' then '三年级,四年级,五年级,六年级,七年级,八年级,九年级'
    else '一年级,二年级,三年级,四年级,五年级,六年级,七年级,八年级,九年级'
end
where grade_scope is null or grade_scope = '';

update edu_course c set current_capacity = (select count(1) from edu_course_enrollment e where e.course_id = c.course_id and e.status = '0');

update edu_course set course_code = concat('QH-C', lpad(course_id, 4, '0')) where course_code is null or course_code = '';
set @add_course_code_index_sql = (
  select if(
    count(*) = 0,
    'alter table edu_course add unique key uk_course_code (course_code)',
    'select 1'
  )
  from information_schema.statistics
  where table_schema = database() and table_name = 'edu_course' and index_name = 'uk_course_code'
);
prepare add_course_code_index_stmt from @add_course_code_index_sql;
execute add_course_code_index_stmt;
deallocate prepare add_course_code_index_stmt;
update edu_course set teacher_user_id = 127, teacher_name = '林老师', week_day = '周六 09:00-10:30', start_time = '09:00', end_time = '10:30' where course_id = 2;
update edu_course set teacher_user_id = 126, teacher_name = '陈老师' where course_id in (3, 25);
update edu_course set teacher_user_id = 120, teacher_name = '陈老师', week_day = '周日 10:40-12:00', start_time = '10:40', end_time = '12:00' where course_id = 19;
update edu_course set teacher_user_id = 111, teacher_name = '李老师' where course_id = 10;
update edu_course set teacher_user_id = 122, teacher_name = '吴老师' where course_id = 26;
update edu_course set teacher_user_id = 128, teacher_name = '高老师' where course_id = 29;
update edu_course set teacher_user_id = 129, teacher_name = '胡老师' where course_id = 30;
update edu_course set teacher_user_id = 125, teacher_name = '何老师' where course_id = 31;
update edu_course set teacher_user_id = 120, teacher_name = '陈老师', week_day = '周六 14:00-15:30', start_time = '14:00', end_time = '15:30' where course_id = 28;
update edu_course set week_day = '周日 09:00-10:20', start_time = '09:00', end_time = '10:20' where course_id = 7;
update edu_course set week_day = '周六 10:40-12:00', start_time = '10:40', end_time = '12:00' where course_id = 9;
update edu_course_enrollment e join edu_course c on c.course_id = e.course_id
set e.course_name = c.course_name, e.teacher_user_id = c.teacher_user_id, e.teacher_name = c.teacher_name;

delete from edu_ai_log where log_id between 9400 and 9407;
insert ignore into edu_ai_log
(log_id, business_type, biz_id, user_id, user_name, role_type, prompt_content, response_content, model_name, status, risk_flag, error_message, prompt_tokens, completion_tokens, latency_ms, create_time)
values
(9400, 'parent_diagnosis', 113, 112, 'edu_parent', 'parent', '学生：王小明\n家长补充关注：编程课很有兴趣，但遇到报错容易急躁。', '孩子当前状态：王小明对编程探索有明显兴趣，急躁主要来自把“报错”理解成失败。\n家长可以怎么做：陪孩子先读报错现象，再一起定位最近一次修改，不直接给答案。\n本周家庭陪伴小约定：每次练习只记录一个报错和一个解决办法，连续三次后给一次项目展示时间。', 'Qwen/Qwen2.5-7B-Instruct', 'success', 'normal', '', 120, 168, 132, date_sub(sysdate(), interval 1 hour)),
(9401, 'parent_diagnosis', 113, 112, 'edu_parent', 'parent', '学生：王小明\n家长补充关注：作业启动慢，放学后总想先玩。', '孩子当前状态：王小明需要从休息状态切换到任务状态的过渡台阶，不是单纯拖延。\n家长可以怎么做：把“开始写作业”改成“先做15分钟最小任务”，只提醒开始和收尾。\n本周家庭陪伴小约定：连续五天记录开始时间，达成后兑换一次周末自主活动。', 'deepseek-ai/DeepSeek-V3', 'success', 'normal', '', 126, 174, 145, date_sub(sysdate(), interval 1 day)),
(9402, 'parent_diagnosis', 160, 150, 'edu_parent_luo', 'parent', '学生：罗一诺\n家长补充关注：舞蹈课开心，但在家练习怕动作不好看。', '孩子当前状态：罗一诺愿意表达，但对评价比较敏感，需要安全的练习氛围。\n家长可以怎么做：先肯定敢练习和能坚持，再只选择一个动作细节反馈。\n本周家庭陪伴小约定：每天练8分钟，只记录一个最自然的动作。', 'Qwen/Qwen2.5-7B-Instruct', 'success', 'normal', '', 112, 156, 128, date_sub(sysdate(), interval 2 day)),
(9403, 'parent_diagnosis', 161, 150, 'edu_parent_luo', 'parent', '学生：罗星澜\n家长补充关注：围棋复盘时不愿意承认自己下错。', '孩子当前状态：罗星澜胜负心较强，复盘时容易把错误理解成否定。\n家长可以怎么做：先问“这一步还有没有别的选择”，避免直接说错。\n本周家庭陪伴小约定：每盘只复盘一手棋，并说出两个可选方案。', 'Qwen/Qwen2.5-7B-Instruct', 'success', 'normal', '', 118, 170, 136, date_sub(sysdate(), interval 3 day)),
(9404, 'parent_diagnosis', 164, 152, 'edu_parent_huang', 'parent', '学生：黄一鸣\n家长补充关注：奥数题能做难题，但基础题容易粗心。', '孩子当前状态：黄一鸣思维挑战动机强，但检查习惯还没有稳定。\n家长可以怎么做：不要增加题量，改为每次挑两题做反向检查。\n本周家庭陪伴小约定：每天只复盘一道粗心题，写出漏看的条件。', 'deepseek-ai/DeepSeek-V3', 'success', 'normal', '', 122, 176, 150, date_sub(sysdate(), interval 4 day)),
(9405, 'parent_diagnosis', 166, 153, 'edu_parent_xu', 'parent', '学生：徐子墨\n家长补充关注：项目做得快，但文件和资料很乱。', '孩子当前状态：徐子墨执行力和创造力较强，整理习惯需要工具化支持。\n家长可以怎么做：用固定命名模板替代口头提醒，减少临时催促。\n本周家庭陪伴小约定：每个项目文件名包含日期、主题和版本号。', 'Qwen/Qwen2.5-7B-Instruct', 'success', 'normal', '', 116, 166, 131, date_sub(sysdate(), interval 5 day)),
(9406, 'parent_diagnosis', 173, 151, 'edu_parent_zhou', 'parent', '学生：周云朵\n家长补充关注：作文想法很多，但写出来总觉得散。', '孩子当前状态：周云朵素材储备丰富，但组织顺序需要更清晰的结构支架。\n家长可以怎么做：写作前只问三个问题：最想表达什么、哪个细节最能说明、最后一句要留下什么感受。\n本周家庭陪伴小约定：每次写作前用5分钟完成三句提纲。', 'deepseek-ai/DeepSeek-V3', 'success', 'normal', '', 124, 180, 144, date_sub(sysdate(), interval 6 day)),
(9407, 'parent_diagnosis', 177, 155, 'edu_parent_ma', 'parent', '学生：马景行\n家长补充关注：九年级任务多，晚上容易越复盘越焦虑。', '孩子当前状态：马景行任务压力较集中，需要把复盘从情绪消耗变成可执行清单。\n家长可以怎么做：睡前只确认明天前三件事，不讨论长期排名。\n本周家庭陪伴小约定：每天晚间写三项明日清单，超过十分钟就停止。', 'Qwen/Qwen2.5-7B-Instruct', 'success', 'normal', '', 128, 184, 152, date_sub(sysdate(), interval 7 day));

update sys_user set nick_name = '系统管理员', remark = '系统管理员账号，负责平台管理员账号维护与全局权限管理' where user_name = 'admin';
update sys_role set role_name = '管理员', remark = '青禾智学课后服务平台管理员' where role_key = 'edu_admin';

insert into edu_course (course_id, course_code, course_name, category, grade_scope, teacher_user_id, teacher_name, campus, week_day, start_time, end_time, start_date, end_date, max_capacity, current_capacity, status, description, ai_notice, ai_suggestion, remark, create_by, create_time)
values
(32, 'QH-C0032', 'Scratch 动画创作进阶', '计算机编程', '三年级,四年级,五年级,六年级', 111, '李老师', '机房A202', '周四 18:00-19:20', '18:00', '19:20', '2026-05-07', '2026-06-25', 24, 0, '0', '面向小学中高年级，围绕角色动画、场景切换和简单交互制作完整动画作品。', '', '', '李老师新增计算机课程，周四晚间，不与已有课程冲突。', 'edu_teacher', sysdate()),
(33, 'QH-C0033', '少儿网页设计启蒙', '计算机编程', '五年级,六年级,七年级', 111, '李老师', '机房A203', '周日 14:00-15:30', '14:00', '15:30', '2026-05-10', '2026-06-28', 22, 0, '0', '通过图文排版、色彩搭配和基础 HTML 结构，让学生体验网页作品搭建。', '', '', '李老师新增计算机课程，周日下午，不与已有课程冲突。', 'edu_teacher', sysdate()),
(34, 'QH-C0034', 'Python 趣味算法小课', '计算机编程', '六年级,七年级,八年级', 111, '李老师', '机房A204', '周二 18:00-19:20', '18:00', '19:20', '2026-05-12', '2026-06-30', 20, 0, '0', '用生活问题引入排序、条件判断和循环思维，帮助高年级学生完成小型 Python 算法练习。', '', '', '李老师新增计算机课程，周二晚间，与已有课程错峰开设。', 'edu_teacher', sysdate())
on duplicate key update course_name = values(course_name), category = values(category), grade_scope = values(grade_scope), teacher_user_id = values(teacher_user_id), teacher_name = values(teacher_name), campus = values(campus), week_day = values(week_day), start_time = values(start_time), end_time = values(end_time), start_date = values(start_date), end_date = values(end_date), max_capacity = values(max_capacity), description = values(description), remark = values(remark);

delete from edu_ai_log where log_id between 9408 and 9419;
insert ignore into edu_ai_log
(log_id, business_type, biz_id, user_id, user_name, role_type, prompt_content, response_content, model_name, status, risk_flag, error_message, prompt_tokens, completion_tokens, latency_ms, create_time)
values
(9408, 'parent_diagnosis', 164, 152, 'edu_parent_huang', 'parent', '学生：黄一鸣\n家长补充关注：科学实验后很兴奋，但实验记录经常漏步骤。', '孩子当前状态：黄一鸣动手兴趣强，记录环节容易被实验现象吸引而跳步。\n家长可以怎么做：实验后先让孩子复述“材料、步骤、现象、结论”四项，不急着纠错。\n本周家庭陪伴小约定：每次实验类作业只补齐一张四格记录卡。', 'Qwen/Qwen2.5-7B-Instruct', 'success', 'normal', '', 126, 182, 139, date_sub(sysdate(), interval 8 day)),
(9409, 'parent_diagnosis', 165, 152, 'edu_parent_huang', 'parent', '学生：黄嘉树\n家长补充关注：九年级复习任务多，容易因为一次测验失误否定自己。', '孩子当前状态：黄嘉树处在升学压力集中期，情绪波动多来自把单次结果扩大化。\n家长可以怎么做：讨论试卷时先分“会但失分、不会、时间不够”三类，再安排下一步。\n本周家庭陪伴小约定：每次复盘只确定两个可执行改进点，避免长时间追问排名。', 'deepseek-ai/DeepSeek-V3', 'success', 'normal', '', 132, 190, 158, date_sub(sysdate(), interval 9 day)),
(9410, 'parent_diagnosis', 168, 153, 'edu_parent_xu', 'parent', '学生：徐若溪\n家长补充关注：美术作品很有想法，但交作品前反复修改到很晚。', '孩子当前状态：徐若溪审美要求高，但作品收尾边界不清，容易拖延睡眠。\n家长可以怎么做：提前约定“草稿、完善、停止”三个节点，到点只做拍照留存。\n本周家庭陪伴小约定：作品提交前一天晚上九点后不再大改，只允许整理工具。', 'Qwen/Qwen2.5-7B-Instruct', 'success', 'normal', '', 118, 174, 141, date_sub(sysdate(), interval 10 day)),
(9411, 'parent_diagnosis', 169, 153, 'edu_parent_xu', 'parent', '学生：徐沐阳\n家长补充关注：初中课程增多，周末安排容易和兴趣课冲突。', '孩子当前状态：徐沐阳兴趣面广，时间管理还需要从“想做很多”过渡到“先排优先级”。\n家长可以怎么做：每周五一起画一张周末时间表，把固定课程、作业和休息分开。\n本周家庭陪伴小约定：周末每天最多安排两个重点任务，其余作为弹性任务。', 'deepseek-ai/DeepSeek-V3', 'success', 'normal', '', 124, 180, 147, date_sub(sysdate(), interval 11 day)),
(9412, 'parent_diagnosis', 170, 154, 'edu_parent_guo', 'parent', '学生：郭予宁\n家长补充关注：阅读速度慢，看到长文章容易先说不会。', '孩子当前状态：郭予宁面对长文本有畏难感，核心不是理解力不足，而是缺少切分方法。\n家长可以怎么做：先陪孩子找标题、人物和关键词，再分段读，不要求一次读完。\n本周家庭陪伴小约定：每天读一篇文章只标三个关键词，并说出一句中心意思。', 'Qwen/Qwen2.5-7B-Instruct', 'success', 'normal', '', 120, 176, 136, date_sub(sysdate(), interval 12 day)),
(9413, 'parent_diagnosis', 171, 154, 'edu_parent_guo', 'parent', '学生：郭予安\n家长补充关注：机器人课很积极，但小组合作时容易抢着操作。', '孩子当前状态：郭予安参与热情高，但协作边界需要练习，容易把负责等同于亲自操作。\n家长可以怎么做：在家用“操作员、记录员、检查员”角色轮换练习合作表达。\n本周家庭陪伴小约定：每次亲子搭建活动必须先说清楚自己负责哪一步。', 'Qwen/Qwen2.5-7B-Instruct', 'success', 'normal', '', 122, 178, 143, date_sub(sysdate(), interval 13 day)),
(9414, 'parent_diagnosis', 172, 150, 'edu_parent_luo', 'parent', '学生：罗星辰\n家长补充关注：Python 代码能跟上，但课后不愿意整理错题和报错。', '孩子当前状态：罗星辰课堂吸收较快，但复盘习惯不足，导致同类报错重复出现。\n家长可以怎么做：不要要求长篇笔记，只保留“错误提示、原因、改法”三行。\n本周家庭陪伴小约定：每次编程练习结束前用三分钟写一条报错卡。', 'deepseek-ai/DeepSeek-V3', 'success', 'normal', '', 130, 186, 151, date_sub(sysdate(), interval 14 day)),
(9415, 'parent_diagnosis', 173, 151, 'edu_parent_zhou', 'parent', '学生：周云朵\n家长补充关注：英语表达有点害羞，课堂展示前会紧张。', '孩子当前状态：周云朵有表达基础，但公开展示前需要更稳定的准备感。\n家长可以怎么做：先在家做一分钟低压力演练，只反馈声音和停顿，不评价对错。\n本周家庭陪伴小约定：每晚用英文说三句话，家长只复述亮点。', 'Qwen/Qwen2.5-7B-Instruct', 'success', 'normal', '', 116, 172, 134, date_sub(sysdate(), interval 15 day)),
(9416, 'parent_diagnosis', 176, 154, 'edu_parent_guo', 'parent', '学生：郭予安\n家长补充关注：作业完成后经常忘记检查书包和第二天用品。', '孩子当前状态：郭予安完成任务后容易快速切换到放松，收尾检查没有固定流程。\n家长可以怎么做：把提醒改成清单，由孩子自己勾选书本、文具、运动用品。\n本周家庭陪伴小约定：睡前五分钟完成第二天物品清单，家长只做最后确认。', 'deepseek-ai/DeepSeek-V3', 'success', 'normal', '', 112, 168, 129, date_sub(sysdate(), interval 16 day)),
(9417, 'parent_diagnosis', 177, 155, 'edu_parent_ma', 'parent', '学生：马景行\n家长补充关注：体育训练后很累，晚上复习效率下降。', '孩子当前状态：马景行运动投入度高，训练日需要调整学习任务强度，避免硬扛。\n家长可以怎么做：训练日安排轻复盘和背诵，非训练日再做复杂题。\n本周家庭陪伴小约定：运动日晚上只完成两项轻任务，并保证睡前放松。', 'Qwen/Qwen2.5-7B-Instruct', 'success', 'normal', '', 126, 184, 146, date_sub(sysdate(), interval 17 day)),
(9418, 'parent_diagnosis', 160, 150, 'edu_parent_luo', 'parent', '学生：罗一诺\n家长补充关注：阅读打卡能坚持，但经常只追求读完页数。', '孩子当前状态：罗一诺坚持性不错，但阅读目标偏数量，需要增加理解表达。\n家长可以怎么做：读完后只聊一个喜欢的角色或一个不理解的地方。\n本周家庭陪伴小约定：每天阅读后说一句“我最想记住的是”。', 'deepseek-ai/DeepSeek-V3', 'success', 'normal', '', 114, 166, 132, date_sub(sysdate(), interval 18 day)),
(9419, 'parent_diagnosis', 113, 112, 'edu_parent', 'parent', '学生：王小明\n家长补充关注：报名了新课程后很期待，但容易忽略课前准备。', '孩子当前状态：王小明对新课程有期待感，课前准备还没有形成固定习惯。\n家长可以怎么做：上课前一天晚上一起确认地点、材料和上课时间，不临时催促。\n本周家庭陪伴小约定：每次课前完成三项准备清单，课后分享一个课堂收获。', 'Qwen/Qwen2.5-7B-Instruct', 'success', 'normal', '', 110, 164, 127, date_sub(sysdate(), interval 19 day));

delete from edu_course_enrollment where enrollment_id in (103,7030,7100,7102,7103,7104,7250,7270,801014,801522);
insert ignore into edu_course_enrollment
(enrollment_id, course_id, course_name, student_user_id, student_name, parent_user_id, parent_name, teacher_user_id, teacher_name, status, signup_source, learning_record, interaction_summary, remark, create_by, create_time)
values
(7105, 10, '机器人创客入门', 175, '徐沐阳', 153, '徐家长', 122, '吴老师', '0', 'student', '完成传感器灯光控制练习，能主动调试线路连接。', '建议继续引导孩子用流程图记录调试步骤。', '中学创客课程报名', 'edu_student_xu_middle', sysdate()),
(7106, 10, '机器人创客入门', 176, '郭予安', 154, '郭家长', 122, '吴老师', '0', 'parent', '能按小组分工完成结构搭建，对机器人运动控制感兴趣。', '课堂协作意识较好，下次可尝试承担记录员角色。', '家长协助报名', 'edu_parent_guo', sysdate());
delete from edu_ai_log where log_id between 9300 and 9311;
update edu_course c set current_capacity = (select count(1) from edu_course_enrollment e where e.course_id = c.course_id and e.status = '0');
