-- 青禾智学课后服务平台菜单重构脚本
-- 用途：
-- 1. 在保留基础系统能力的前提下，隐藏与课题无关的默认菜单
-- 2. 新增教育平台专属菜单与角色
-- 3. 让管理员、教师、家长、学生看到更贴近业务的导航结构

-- 隐藏默认系统菜单与当前库中无关的菜单
update sys_menu
set visible = '1', update_by = 'edu_platform', update_time = sysdate()
where menu_id in (1, 2, 3, 4, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 500, 501, 2000, 2006, 2008, 2009);

-- 新增教育平台角色
insert into sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark)
select * from (
  select 100 as role_id, '课后服务管理员' as role_name, 'edu_admin' as role_key, 100 as role_sort, 1 as data_scope, 1 as menu_check_strictly, 1 as dept_check_strictly, '0' as status, '0' as del_flag, 'edu_platform' as create_by, sysdate() as create_time, '' as update_by, null as update_time, '青禾智学课后服务平台管理员' as remark
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
  select 3004, '报名记录', 3000, 4, 'enrollment', 'edu/enrollment/index', '', '', 1, 0, 'C', '0', '0', 'edu:enrollment:list', 'form', 'edu_platform', sysdate(), '', null, '课程报名记录'
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

delete from edu_ai_log where log_id between 500 and 509;
insert ignore into edu_ai_log
(log_id, business_type, biz_id, user_id, user_name, role_type, prompt_content, response_content, model_name, status, risk_flag, error_message, prompt_tokens, completion_tokens, latency_ms, create_time)
values
(500, 'parent_diagnosis', 136, 130, 'edu_parent_li', 'parent', '关注孩子低年级阅读坚持不稳定，想知道怎么陪伴。', '孩子当前状态：阅读兴趣有基础，但持续时间容易受环境影响。\n家长可以怎么做：把目标从“读完多少页”改成“每天固定共读20分钟”，读后只问一个开放问题。\n本周家庭陪伴小约定：睡前共读三次，每次由孩子选书并讲一个喜欢的画面。', 'Qwen/Qwen2.5-7B-Instruct', 'mock', 'normal', '', 120, 180, 140, date_sub(sysdate(), interval 6 day)),
(501, 'parent_diagnosis', 138, 131, 'edu_parent_zhang', 'parent', '孩子艺术课积极，但作业拖延，沟通容易急。', '孩子当前状态：创作表达意愿强，但面对固定任务时启动较慢。\n家长可以怎么做：先肯定作品投入，再把作业拆成两个可完成的小段，中间给一次选择权。\n本周家庭陪伴小约定：每天放学后先完成15分钟核心任务，再安排10分钟自由创作。', 'Qwen/Qwen2.5-7B-Instruct', 'mock', 'normal', '', 130, 190, 152, date_sub(sysdate(), interval 5 day)),
(502, 'parent_diagnosis', 139, 132, 'edu_parent_chen', 'parent', '孩子运动积极，但学习遇到难题容易放弃。', '孩子当前状态：体能任务完成度高，说明执行力不错；学习难题上的挫败感需要被看见。\n家长可以怎么做：先让孩子说出卡住的第一步，再陪他找一个最小突破口，不直接给答案。\n本周家庭陪伴小约定：每天复盘一道错题，只讲思路不追求数量。', 'deepseek-ai/DeepSeek-V3', 'mock', 'normal', '', 126, 188, 168, date_sub(sysdate(), interval 4 day)),
(503, 'parent_diagnosis', 140, 133, 'edu_parent_wu', 'parent', '孩子整理习惯不好，经常忘带材料。', '孩子当前状态：学习参与稳定，但物品管理还依赖提醒。\n家长可以怎么做：用可视化清单替代反复催促，把“整理书包”固定到睡前流程。\n本周家庭陪伴小约定：连续三天按清单自查书包，完成后由孩子自己打勾。', 'Qwen/Qwen2.5-7B-Instruct', 'mock', 'normal', '', 118, 174, 131, date_sub(sysdate(), interval 3 day)),
(504, 'parent_diagnosis', 142, 134, 'edu_parent_sun', 'parent', '孩子做手工有耐心，但表达自己想法较少。', '孩子当前状态：精细动作和专注力较好，口头表达还需要安全的练习场景。\n家长可以怎么做：不要急着评价作品好坏，先请孩子介绍一个细节和一个选择理由。\n本周家庭陪伴小约定：每次作品完成后录一段30秒介绍，家长只做倾听和复述。', 'deepseek-ai/DeepSeek-V3', 'mock', 'normal', '', 122, 182, 149, date_sub(sysdate(), interval 2 day)),
(505, 'parent_diagnosis', 143, 135, 'edu_parent_zhao', 'parent', '孩子口算速度有提升，但容易因为小错误沮丧。', '孩子当前状态：计算能力正在进步，但对错误的容忍度偏低。\n家长可以怎么做：把错题看作“提醒卡”，先找规律，再决定是否需要重复练习。\n本周家庭陪伴小约定：每天只挑2道错题讲清楚原因，完成后记录一个进步点。', 'Qwen/Qwen2.5-7B-Instruct', 'mock', 'normal', '', 124, 176, 137, date_sub(sysdate(), interval 1 day));
