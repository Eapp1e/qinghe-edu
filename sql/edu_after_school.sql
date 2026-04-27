-- 青禾智学课后服务平台初始化脚本
create database if not exists qinghe_edu default character set utf8mb4 collate utf8mb4_general_ci;
use qinghe_edu;

drop table if exists edu_ai_log;
drop table if exists edu_homework_question;
drop table if exists edu_course_enrollment;
drop table if exists edu_course;
drop table if exists edu_student_profile;

create table edu_student_profile (
  profile_id bigint(20) not null auto_increment comment '档案ID',
  student_user_id bigint(20) not null comment '学生用户ID',
  student_name varchar(64) not null comment '学生姓名',
  parent_user_id bigint(20) default null comment '家长用户ID',
  parent_name varchar(64) default '' comment '家长姓名',
  grade_name varchar(32) default '' comment '年级',
  class_name varchar(32) default '' comment '班级',
  gender varchar(16) default '' comment '性别',
  interest_tags varchar(255) default '' comment '兴趣标签',
  status char(1) default '0' comment '状态(0正常 1停用)',
  remark varchar(500) default '' comment '备注',
  create_by varchar(64) default '' comment '创建者',
  create_time datetime comment '创建时间',
  update_by varchar(64) default '' comment '更新者',
  update_time datetime comment '更新时间',
  primary key (profile_id),
  unique key uk_student_user_id (student_user_id)
) engine=innodb default charset=utf8mb4 comment='学生档案表';

create table edu_course (
  course_id bigint(20) not null auto_increment comment '课程ID',
  course_name varchar(100) not null comment '课程名称',
  category varchar(50) default '' comment '课程分类',
  teacher_user_id bigint(20) not null comment '教师用户ID',
  teacher_name varchar(64) default '' comment '教师姓名',
  campus varchar(100) default '' comment '校区/地点',
  week_day varchar(16) default '' comment '上课星期',
  start_time varchar(16) default '' comment '开始时间',
  end_time varchar(16) default '' comment '结束时间',
  start_date date comment '开始日期',
  end_date date comment '结束日期',
  max_capacity int(11) default 30 comment '最大容量',
  current_capacity int(11) default 0 comment '当前人数',
  status char(1) default '0' comment '状态(0开放 1关闭)',
  description varchar(1000) default '' comment '课程简介',
  ai_notice text comment 'AI通知',
  ai_suggestion text comment 'AI教学建议',
  remark varchar(500) default '' comment '备注',
  create_by varchar(64) default '' comment '创建者',
  create_time datetime comment '创建时间',
  update_by varchar(64) default '' comment '更新者',
  update_time datetime comment '更新时间',
  primary key (course_id)
) engine=innodb default charset=utf8mb4 comment='课后课程表';

create table edu_course_enrollment (
  enrollment_id bigint(20) not null auto_increment comment '报名ID',
  course_id bigint(20) not null comment '课程ID',
  course_name varchar(100) default '' comment '课程名称',
  student_user_id bigint(20) not null comment '学生用户ID',
  student_name varchar(64) default '' comment '学生姓名',
  parent_user_id bigint(20) default null comment '家长用户ID',
  parent_name varchar(64) default '' comment '家长姓名',
  teacher_user_id bigint(20) default null comment '教师用户ID',
  teacher_name varchar(64) default '' comment '教师姓名',
  status char(1) default '0' comment '状态(0已报名 1已完成)',
  signup_source varchar(20) default '' comment '报名来源',
  learning_record varchar(1000) default '' comment '学习记录',
  interaction_summary varchar(1000) default '' comment '互动总结',
  remark varchar(500) default '' comment '备注',
  create_by varchar(64) default '' comment '创建者',
  create_time datetime comment '创建时间',
  update_by varchar(64) default '' comment '更新者',
  update_time datetime comment '更新时间',
  primary key (enrollment_id),
  unique key uk_course_student (course_id, student_user_id)
) engine=innodb default charset=utf8mb4 comment='课程报名表';

create table edu_homework_question (
  question_id bigint(20) not null auto_increment comment '问题ID',
  course_id bigint(20) default null comment '课程ID',
  course_name varchar(100) default '' comment '课程名称',
  student_user_id bigint(20) not null comment '学生用户ID',
  student_name varchar(64) default '' comment '学生姓名',
  parent_user_id bigint(20) default null comment '家长用户ID',
  teacher_user_id bigint(20) default null comment '教师用户ID',
  question_title varchar(200) not null comment '问题标题',
  question_content varchar(2000) not null comment '问题内容',
  ai_answer text comment 'AI解答',
  answer_status char(1) default '0' comment '答疑状态(0待解答 1已解答)',
  safety_flag varchar(20) default 'normal' comment '安全标记',
  remark varchar(500) default '' comment '备注',
  create_by varchar(64) default '' comment '创建者',
  create_time datetime comment '创建时间',
  update_by varchar(64) default '' comment '更新者',
  update_time datetime comment '更新时间',
  primary key (question_id)
) engine=innodb default charset=utf8mb4 comment='作业问题表';

create table edu_ai_log (
  log_id bigint(20) not null auto_increment comment '日志ID',
  business_type varchar(50) default '' comment '业务类型',
  biz_id bigint(20) default null comment '业务ID',
  user_id bigint(20) default null comment '调用用户ID',
  user_name varchar(64) default '' comment '调用用户',
  role_type varchar(20) default '' comment '角色类型',
  prompt_content text comment '提示词',
  response_content text comment '响应内容',
  model_name varchar(100) default '' comment '模型名称',
  status varchar(20) default '' comment '调用状态',
  risk_flag varchar(20) default 'normal' comment '风险标记',
  error_message varchar(1000) default '' comment '错误信息',
  prompt_tokens int(11) default 0 comment '提示词Token',
  completion_tokens int(11) default 0 comment '输出Token',
  latency_ms bigint(20) default 0 comment '耗时毫秒',
  create_time datetime comment '创建时间',
  primary key (log_id)
) engine=innodb default charset=utf8mb4 comment='AI调用日志表';

-- 角色初始化
insert into sys_role values ('100', '课后平台管理员', 'edu_admin', 100, 1, 1, 1, '0', '0', 'admin', sysdate(), '', null, '课后服务平台管理员');
insert into sys_role values ('101', '课后平台教师', 'edu_teacher', 101, 2, 1, 1, '0', '0', 'admin', sysdate(), '', null, '课后服务平台教师');
insert into sys_role values ('102', '课后平台家长', 'edu_parent', 102, 2, 1, 1, '0', '0', 'admin', sysdate(), '', null, '课后服务平台家长');
insert into sys_role values ('103', '课后平台学生', 'edu_student', 103, 2, 1, 1, '0', '0', 'admin', sysdate(), '', null, '课后服务平台学生');

-- 菜单初始化
insert into sys_menu values('2000', '课后服务平台', '0', '5', 'edu', '', '', '', 1, 0, 'M', '0', '0', '', 'education', 'admin', sysdate(), '', null, '课后服务平台目录');
insert into sys_menu values('2001', '平台看板', '2000', '1', 'dashboard', 'edu/dashboard/index', '', '', 1, 0, 'C', '0', '0', 'edu:dashboard:view', 'dashboard', 'admin', sysdate(), '', null, '平台看板菜单');
insert into sys_menu values('2002', '课程管理', '2000', '2', 'course', 'edu/course/index', '', '', 1, 0, 'C', '0', '0', 'edu:course:list', 'guide', 'admin', sysdate(), '', null, '课程管理菜单');
insert into sys_menu values('2003', '学生档案', '2000', '3', 'student', 'edu/student/index', '', '', 1, 0, 'C', '0', '0', 'edu:student:list', 'peoples', 'admin', sysdate(), '', null, '学生档案菜单');
insert into sys_menu values('2004', '上课记录', '2000', '4', 'enrollment', 'edu/enrollment/index', '', '', 1, 0, 'C', '0', '0', 'edu:enrollment:list', 'form', 'admin', sysdate(), '', null, '课程上课与学习记录菜单');
insert into sys_menu values('2005', '作业问答', '2000', '5', 'question', 'edu/question/index', '', '', 1, 0, 'C', '0', '0', 'edu:question:list', 'message', 'admin', sysdate(), '', null, '作业问答菜单');
insert into sys_menu values('2006', 'AI日志', '2000', '6', 'aiLog', 'edu/aiLog/index', '', '', 1, 0, 'C', '0', '0', 'edu:ai:list', 'redis', 'admin', sysdate(), '', null, 'AI日志菜单');

insert into sys_menu values('2010', '课程查询', '2002', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'edu:course:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2011', '课程新增', '2002', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'edu:course:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2012', '课程修改', '2002', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'edu:course:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2013', '课程删除', '2002', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'edu:course:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2014', '课程报名', '2002', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'edu:course:enroll', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu values('2020', '学生查询', '2003', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'edu:student:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2021', '学生新增', '2003', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'edu:student:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2022', '学生修改', '2003', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'edu:student:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2023', '学生删除', '2003', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'edu:student:remove', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu values('2030', '报名编辑', '2004', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'edu:enrollment:edit', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu values('2040', '问题查询', '2005', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'edu:question:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2041', '问题新增', '2005', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'edu:question:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2042', '问题编辑', '2005', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'edu:question:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2043', '问题删除', '2005', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'edu:question:remove', '#', 'admin', sysdate(), '', null, '');

-- 角色菜单关联
-- 平台管理员
insert into sys_role_menu values ('100', '2000');
insert into sys_role_menu values ('100', '2001');
insert into sys_role_menu values ('100', '2002');
insert into sys_role_menu values ('100', '2003');
insert into sys_role_menu values ('100', '2004');
insert into sys_role_menu values ('100', '2005');
insert into sys_role_menu values ('100', '2006');
insert into sys_role_menu values ('100', '2010');
insert into sys_role_menu values ('100', '2011');
insert into sys_role_menu values ('100', '2012');
insert into sys_role_menu values ('100', '2013');
insert into sys_role_menu values ('100', '2014');
insert into sys_role_menu values ('100', '2020');
insert into sys_role_menu values ('100', '2021');
insert into sys_role_menu values ('100', '2022');
insert into sys_role_menu values ('100', '2023');
insert into sys_role_menu values ('100', '2030');
insert into sys_role_menu values ('100', '2040');
insert into sys_role_menu values ('100', '2041');
insert into sys_role_menu values ('100', '2042');
insert into sys_role_menu values ('100', '2043');

-- 教师
insert into sys_role_menu values ('101', '2000');
insert into sys_role_menu values ('101', '2001');
insert into sys_role_menu values ('101', '2002');
insert into sys_role_menu values ('101', '2004');
insert into sys_role_menu values ('101', '2005');
insert into sys_role_menu values ('101', '2010');
insert into sys_role_menu values ('101', '2011');
insert into sys_role_menu values ('101', '2012');
insert into sys_role_menu values ('101', '2040');
insert into sys_role_menu values ('101', '2042');
insert into sys_role_menu values ('101', '2030');

-- 家长
insert into sys_role_menu values ('102', '2000');
insert into sys_role_menu values ('102', '2001');
insert into sys_role_menu values ('102', '2002');
insert into sys_role_menu values ('102', '2003');
insert into sys_role_menu values ('102', '2004');
insert into sys_role_menu values ('102', '2005');
insert into sys_role_menu values ('102', '2006');
insert into sys_role_menu values ('102', '2010');
insert into sys_role_menu values ('102', '2014');
insert into sys_role_menu values ('102', '2020');
insert into sys_role_menu values ('102', '2021');
insert into sys_role_menu values ('102', '2022');
insert into sys_role_menu values ('102', '2040');
insert into sys_role_menu values ('102', '2041');

-- 学生
insert into sys_role_menu values ('103', '2000');
insert into sys_role_menu values ('103', '2001');
insert into sys_role_menu values ('103', '2002');
insert into sys_role_menu values ('103', '2004');
insert into sys_role_menu values ('103', '2005');
insert into sys_role_menu values ('103', '2006');
insert into sys_role_menu values ('103', '2010');
insert into sys_role_menu values ('103', '2014');
insert into sys_role_menu values ('103', '2040');
insert into sys_role_menu values ('103', '2041');

-- 可选示例数据
insert into edu_student_profile (student_user_id, student_name, parent_user_id, parent_name, grade_name, class_name, gender, interest_tags, status, create_by, create_time)
values (2, '示例学生', 1, '示例家长', '三年级', '1班', '男', '书法,篮球', '0', 'admin', sysdate());
