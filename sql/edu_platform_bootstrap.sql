-- 中小学智能课后服务平台业务表与演示数据初始化

create table if not exists edu_student_profile (
  profile_id bigint(20) not null auto_increment comment '档案ID',
  student_user_id bigint(20) not null comment '学生用户ID',
  student_name varchar(64) not null comment '学生姓名',
  parent_user_id bigint(20) not null comment '家长用户ID',
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

create table if not exists edu_course (
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

create table if not exists edu_course_enrollment (
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

create table if not exists edu_homework_question (
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

create table if not exists edu_ai_log (
  log_id bigint(20) not null auto_increment comment '日志ID',
  business_type varchar(50) default '' comment '业务类型',
  biz_id bigint(20) default null comment '业务ID',
  user_id bigint(20) default null comment '调用用户ID',
  user_name varchar(64) default '' comment '调用用户名',
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

insert ignore into sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time, update_by, update_time, remark) values
(110, 103, 'edu_admin', '平台管理员', '00', 'edu_admin@example.com', '13800000010', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台管理员'),
(111, 103, 'edu_teacher', '李老师', '00', 'edu_teacher@example.com', '13800000011', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台教师'),
(112, 103, 'edu_parent', '王家长', '00', 'edu_parent@example.com', '13800000012', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台家长'),
(113, 103, 'edu_student', '王小明', '00', 'edu_student@example.com', '13800000013', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生');

delete from sys_user_role where user_id in (110, 111, 112, 113) and role_id in (100, 101, 102, 103, 104);
insert ignore into sys_user_role (user_id, role_id) values
(110, 100),
(111, 101),
(112, 102),
(113, 103);

insert ignore into edu_student_profile (profile_id, student_user_id, student_name, parent_user_id, parent_name, grade_name, class_name, gender, interest_tags, status, remark, create_by, create_time) values
(1, 113, '王小明', 112, '王家长', '五年级', '2班', '男', '篮球,编程,书法', '0', '演示学生档案', 'admin', sysdate());

insert ignore into edu_course (course_id, course_name, category, teacher_user_id, teacher_name, campus, week_day, start_time, end_time, start_date, end_date, max_capacity, current_capacity, status, description, ai_notice, ai_suggestion, remark, create_by, create_time) values
(1, '少儿趣味编程', '科技创新', 111, '李老师', '教学楼A201', '周一', '16:00', '17:30', '2026-04-20', '2026-06-30', 30, 1, '0', '通过图形化编程和小游戏设计培养逻辑思维。', '【课后服务通知】本周将开展少儿趣味编程课程，请学生自备笔记本并准时到达教学楼A201。', '教学建议：采用任务闯关制，先做演示，再让学生分组完成小游戏。', '首页推荐课程', 'edu_teacher', sysdate()),
(2, '创意美术工坊', '艺术素养', 111, '李老师', '艺术教室B103', '周三', '15:40', '17:10', '2026-04-22', '2026-06-30', 25, 1, '0', '结合水彩与手工创作，提升审美与表达能力。', '【课后服务通知】创意美术工坊课程请携带画笔和围裙，课程结束后统一展示作品。', '教学建议：先进行色彩启发，再安排分层创作与作品讲评。', '家长关注度高', 'edu_teacher', sysdate()),
(3, '篮球基础训练', '体育健康', 111, '李老师', '操场东侧篮球场', '周五', '16:10', '17:40', '2026-04-24', '2026-06-30', 35, 0, '0', '面向中高年级学生开展运球、传球、合作训练。', '', '', '学生兴趣课', 'edu_teacher', sysdate());

insert ignore into edu_course_enrollment (enrollment_id, course_id, course_name, student_user_id, student_name, parent_user_id, parent_name, teacher_user_id, teacher_name, status, signup_source, learning_record, interaction_summary, remark, create_by, create_time) values
(1, 1, '少儿趣味编程', 113, '王小明', 112, '王家长', 111, '李老师', '0', 'parent', '已完成第一周图形化编程体验，能独立完成角色移动任务。', '家长反馈孩子回家后愿意继续练习。', '报名演示数据', 'edu_parent', sysdate()),
(2, 2, '创意美术工坊', 113, '王小明', 112, '王家长', 111, '李老师', '1', 'parent', '完成春日主题画作，色彩搭配较好，课堂参与积极。', '建议家长鼓励孩子多进行观察写生。', '已完成演示数据', 'edu_parent', sysdate());

insert ignore into edu_homework_question (question_id, course_id, course_name, student_user_id, student_name, parent_user_id, teacher_user_id, question_title, question_content, ai_answer, answer_status, safety_flag, remark, create_by, create_time) values
(1, 1, '少儿趣味编程', 113, '王小明', 112, 111, '循环结构不会用怎么办？', '老师布置了让角色重复移动5次的小练习，我不知道为什么需要用循环。', '问题分析：这道题主要考查重复执行的思想。解题思路：如果一段动作要重复很多次，就可以把动作放进循环里，设置重复次数为5。鼓励建议：你已经发现了重复动作这个关键点，再多练两次就会很熟练。', '1', 'normal', '问答演示数据', 'edu_student', date_sub(sysdate(), interval 1 day)),
(2, 2, '创意美术工坊', 113, '王小明', 112, 111, '水彩颜色总是变脏怎么办？', '我在调色的时候红色和蓝色混在一起后颜色很灰，想知道是不是步骤不对。', '问题分析：颜色发灰通常和调色次数过多、笔刷没洗净有关。解题思路：先清洗画笔，再少量多次取色，尽量用两种颜色调配。鼓励建议：你已经开始关注绘画细节了，这是很好的进步。', '1', 'normal', '问答演示数据', 'edu_student', sysdate());

insert ignore into edu_ai_log (log_id, business_type, biz_id, user_id, user_name, role_type, prompt_content, response_content, model_name, status, risk_flag, error_message, prompt_tokens, completion_tokens, latency_ms, create_time) values
(1, 'course_notice', 1, 111, 'edu_teacher', 'teacher', '请根据少儿趣味编程课程生成家长通知', '【课后服务通知】本周将开展少儿趣味编程课程，请学生自备笔记本并准时到达教学楼A201。', 'gpt-4o-mini', 'mock', 'normal', '', 45, 78, 120, date_sub(sysdate(), interval 2 day)),
(2, 'teaching_suggestion', 2, 111, 'edu_teacher', 'teacher', '请为创意美术工坊生成教学建议', '教学建议：先进行色彩启发，再安排分层创作与作品讲评。', 'gpt-4o-mini', 'mock', 'normal', '', 42, 66, 115, date_sub(sysdate(), interval 1 day)),
(3, 'homework_answer', 1, 113, 'edu_student', 'student', '循环结构不会用怎么办？', '问题分析：这道题主要考查重复执行的思想。', 'gpt-4o-mini', 'mock', 'normal', '', 38, 92, 140, sysdate());
