-- 青禾智学课后服务平台演示数据
-- 请先执行：sql/qinghe_system_base.sql 与 sql/edu_after_school.sql
use qinghe_edu;

-- 清理旧演示数据
delete from sys_user_role where user_id in (110,111,112,113);
delete from sys_user where user_id in (110,111,112,113);

delete from edu_ai_log where user_id in (110,111,112,113);
delete from edu_homework_question where question_id in (1,2) or student_user_id in (113) or parent_user_id in (112) or teacher_user_id in (111);
delete from edu_course_enrollment where enrollment_id in (1,2) or course_id in (1,2,3) or student_user_id in (113) or parent_user_id in (112) or teacher_user_id in (111);
delete from edu_course where course_id in (1,2,3) or teacher_user_id in (111);
delete from edu_student_profile where profile_id in (1) or student_user_id in (113);

delete from sys_role_menu where role_id in (100,101,102,103);
insert into sys_role_menu select * from (
  select 100 as role_id, 2000 as menu_id union all
  select 100, 2001 union all select 100, 2002 union all select 100, 2003 union all
  select 100, 2004 union all select 100, 2005 union all select 100, 2006 union all
  select 100, 2010 union all select 100, 2011 union all select 100, 2012 union all
  select 100, 2013 union all select 100, 2014 union all select 100, 2020 union all
  select 100, 2021 union all select 100, 2022 union all select 100, 2023 union all
  select 100, 2030 union all select 100, 2040 union all select 100, 2041 union all
  select 100, 2042 union all select 100, 2043 union all
  select 101, 2000 union all select 101, 2001 union all select 101, 2002 union all
  select 101, 2004 union all select 101, 2005 union all select 101, 2010 union all
  select 101, 2011 union all select 101, 2012 union all select 101, 2030 union all
  select 101, 2040 union all select 101, 2042 union all
  select 102, 2000 union all select 102, 2001 union all select 102, 2002 union all
  select 102, 2003 union all select 102, 2004 union all select 102, 2005 union all
  select 102, 2006 union all select 102, 2010 union all select 102, 2014 union all
  select 102, 2020 union all select 102, 2021 union all select 102, 2022 union all
  select 102, 2040 union all select 102, 2041 union all
  select 103, 2000 union all select 103, 2001 union all select 103, 2002 union all
  select 103, 2004 union all select 103, 2005 union all select 103, 2006 union all
  select 103, 2010 union all select 103, 2014 union all select 103, 2040 union all
  select 103, 2041
) t;

-- 演示账号，密码均为 admin123
insert into sys_user values(110, 103, 'edu_admin',   '管理员', '00', 'edu_admin@example.com',   '13800000010', '1', '', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台管理员');
insert into sys_user values(111, 103, 'edu_teacher', '李老师',     '00', 'edu_teacher@example.com', '13800000011', '0', '', 'computer,art,sports,science', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台教师');
insert into sys_user values(112, 103, 'edu_parent',  '王家长',     '00', 'edu_parent@example.com',  '13800000012', '0', '', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台家长');
insert into sys_user values(113, 103, 'edu_student', '王小明',     '00', 'edu_student@example.com', '13800000013', '1', '', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生');

insert into sys_user_role values (110, 100);
insert into sys_user_role values (111, 101);
insert into sys_user_role values (112, 102);
insert into sys_user_role values (113, 103);

insert into edu_student_profile (profile_id, student_user_id, student_name, parent_user_id, parent_name, grade_name, class_name, gender, interest_tags, status, remark, create_by, create_time)
values
(1, 113, '王小明', 112, '王家长', '五年级', '2班', '男', '篮球,编程,书法', '0', '演示学生档案', 'admin', sysdate());

insert into edu_course (course_id, course_name, category, teacher_user_id, teacher_name, campus, week_day, start_time, end_time, start_date, end_date, max_capacity, current_capacity, status, description, ai_notice, ai_suggestion, remark, create_by, create_time)
values
(1, '少儿趣味编程', '计算机编程', 111, '李老师', '教学楼A201', '周一', '16:00', '17:30', '2026-04-20', '2026-06-30', 30, 1, '0', '通过图形化编程和小游戏设计培养逻辑思维。', '【课后服务通知】本周将开展少儿趣味编程课程，请学生自备笔记本并准时到达教学楼A201。', '课堂建议：采用任务闯关制，先做演示，再让学生分组完成小游戏。', '首页推荐课程', 'edu_teacher', sysdate()),
(2, '创意美术工坊', '艺术素养', 111, '李老师', '艺术教室B103', '周三', '15:40', '17:10', '2026-04-22', '2026-06-30', 25, 1, '0', '结合水彩与手工创作，提升审美与表达能力。', '【课后服务通知】创意美术工坊课程请携带画笔和围裙，课程结束后统一展示作品。', '教学建议：先进行色彩启发，再安排分层创作与作品讲评。', '家长关注度高', 'edu_teacher', sysdate()),
(3, '篮球基础训练', '体育健康', 111, '李老师', '操场东侧篮球场', '周五', '16:10', '17:40', '2026-04-24', '2026-06-30', 35, 0, '0', '面向中高年级学生开展运球、传球、合作训练。', '', '', '学生兴趣课', 'edu_teacher', sysdate());

insert into edu_course_enrollment (enrollment_id, course_id, course_name, student_user_id, student_name, parent_user_id, parent_name, teacher_user_id, teacher_name, status, signup_source, learning_record, interaction_summary, remark, create_by, create_time)
values
(1, 1, '少儿趣味编程', 113, '王小明', 112, '王家长', 111, '李老师', '0', 'parent', '已完成第一周图形化编程体验，能独立完成角色移动任务。', '家长反馈孩子回家后愿意继续练习。', '报名演示数据', 'edu_parent', sysdate()),
(2, 2, '创意美术工坊', 113, '王小明', 112, '王家长', 111, '李老师', '1', 'parent', '完成春日主题画作，色彩搭配较好，课堂参与积极。', '建议家长鼓励孩子多进行观察写生。', '已完成演示数据', 'edu_parent', sysdate());

insert into edu_homework_question (question_id, course_id, course_name, student_user_id, student_name, parent_user_id, teacher_user_id, question_title, question_content, ai_answer, answer_status, safety_flag, remark, create_by, create_time)
values
(1, 1, '少儿趣味编程', 113, '王小明', 112, 111, '循环结构不会用怎么办？', '老师布置了让角色重复移动5次的小练习，我不知道为什么需要用循环。', '问题分析：这道题主要考查重复执行的思想。\n解题思路：如果一段动作要重复很多次，就可以把动作放进循环里，设置重复次数为5。\n鼓励建议：你已经发现了“重复动作”这个关键点，再多练两次就会很熟练。', '1', 'normal', '问答演示数据', 'edu_student', date_sub(sysdate(), interval 1 day)),
(2, 2, '创意美术工坊', 113, '王小明', 112, 111, '水彩颜色总是变脏怎么办？', '我在调色的时候红色和蓝色混在一起后颜色很灰，想知道是不是步骤不对。', '问题分析：颜色发灰通常和调色次数过多、笔刷没洗净有关。\n解题思路：先清洗画笔，再少量多次取色，尽量用两种颜色调配。\n鼓励建议：你已经开始关注绘画细节了，这是很好的进步。', '1', 'normal', '问答演示数据', 'edu_student', sysdate());

insert into edu_ai_log (log_id, business_type, biz_id, user_id, user_name, role_type, prompt_content, response_content, model_name, status, risk_flag, error_message, prompt_tokens, completion_tokens, latency_ms, create_time)
values
(1, 'course_notice', 1, 111, 'edu_teacher', 'teacher', '请根据少儿趣味编程课程生成家长通知', '【课后服务通知】本周将开展少儿趣味编程课程，请学生自备笔记本并准时到达教学楼A201。', 'gpt-4o-mini', 'mock', 'normal', '', 45, 78, 120, date_sub(sysdate(), interval 2 day)),
(2, 'teaching_suggestion', 2, 111, 'edu_teacher', 'teacher', '请为创意美术工坊生成教学建议', '教学建议：先进行色彩启发，再安排分层创作与作品讲评。', 'gpt-4o-mini', 'mock', 'normal', '', 42, 66, 115, date_sub(sysdate(), interval 1 day)),
(3, 'homework_answer', 1, 113, 'edu_student', 'student', '循环结构不会用怎么办？', '问题分析：这道题主要考查重复执行的思想。', 'gpt-4o-mini', 'mock', 'normal', '', 38, 92, 140, sysdate());
