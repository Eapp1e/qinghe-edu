-- 中小学智能课后服务平台菜单重构脚本
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
  select 101 as role_id, '课后服务管理员' as role_name, 'edu_admin' as role_key, 101 as role_sort, 1 as data_scope, 1 as menu_check_strictly, 1 as dept_check_strictly, '0' as status, '0' as del_flag, 'edu_platform' as create_by, sysdate() as create_time, '' as update_by, null as update_time, '中小学智能课后服务平台管理员' as remark
  union all
  select 102, '课后服务教师', 'edu_teacher', 102, 2, 1, 1, '0', '0', 'edu_platform', sysdate(), '', null, '中小学智能课后服务平台教师'
  union all
  select 103, '课后服务家长', 'edu_parent', 103, 2, 1, 1, '0', '0', 'edu_platform', sysdate(), '', null, '中小学智能课后服务平台家长'
  union all
  select 104, '课后服务学生', 'edu_student', 104, 2, 1, 1, '0', '0', 'edu_platform', sysdate(), '', null, '中小学智能课后服务平台学生'
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
delete from sys_role_menu where role_id in (101, 102, 103, 104);

insert into sys_role_menu (role_id, menu_id)
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
  select 101, 3000 union all
  select 101, 3001 union all
  select 101, 3002 union all
  select 101, 3003 union all
  select 101, 3004 union all
  select 101, 3005 union all
  select 101, 3006 union all
  select 101, 3007 union all
  select 101, 3008 union all
  select 101, 1000 union all
  select 101, 1001 union all
  select 101, 1002 union all
  select 101, 1003 union all
  select 101, 1006 union all
  select 101, 101 union all
  select 101, 1007 union all
  select 101, 3010 union all
  select 101, 3011 union all
  select 101, 3012 union all
  select 101, 3013 union all
  select 101, 3014 union all
  select 101, 3020 union all
  select 101, 3021 union all
  select 101, 3022 union all
  select 101, 3023 union all
  select 101, 3030 union all
  select 101, 3040 union all
  select 101, 3041 union all
  select 101, 3042 union all
  select 101, 3043 union all
  select 102, 3000 union all
  select 102, 3001 union all
  select 102, 3002 union all
  select 102, 3004 union all
  select 102, 3005 union all
  select 102, 3006 union all
  select 102, 3010 union all
  select 102, 3011 union all
  select 102, 3012 union all
  select 102, 3014 union all
  select 102, 3030 union all
  select 102, 3040 union all
  select 102, 3041 union all
  select 103, 3000 union all
  select 103, 3001 union all
  select 103, 3002 union all
  select 103, 3004 union all
  select 103, 3005 union all
  select 103, 3006 union all
  select 103, 3010 union all
  select 103, 3014 union all
  select 103, 3040 union all
  select 103, 3041 union all
  select 104, 3000 union all
  select 104, 3001 union all
  select 104, 3002 union all
  select 104, 3003 union all
  select 104, 3004 union all
  select 104, 3005 union all
  select 104, 3006 union all
  select 104, 3010 union all
  select 104, 3014 union all
  select 104, 3020 union all
  select 104, 3040 union all
  select 104, 3041
) t;

-- 如存在演示账号，则绑定教育角色
delete ur
from sys_user_role ur
inner join sys_user u on ur.user_id = u.user_id
where u.user_name in ('edu_admin', 'edu_teacher', 'edu_parent', 'edu_student')
  and ur.role_id in (101, 102, 103, 104);

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

insert ignore into sys_role_menu(role_id, menu_id) values (102, 3008);
insert ignore into sys_role_menu(role_id, menu_id) values (103, 3008);
insert ignore into sys_role_menu(role_id, menu_id) values (104, 3008);
insert ignore into sys_role_menu(role_id, menu_id) values (102, 3003);
insert ignore into sys_role_menu(role_id, menu_id) values (102, 3020);
insert ignore into sys_role_menu(role_id, menu_id) values (103, 3003);
insert ignore into sys_role_menu(role_id, menu_id) values (103, 3020);
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
values (3009, '网课中心', 0, 4, 'edu/online-course', 'edu/onlineCourse/index', '', 'EduOnlineCourse', 1, 0, 'C', '0', '0', '', 'online', 'edu_platform', sysdate(), '', null, '学生端网课学习资源入口');

insert ignore into sys_role_menu(role_id, menu_id) values (104, 3009);
update sys_menu set icon = 'online', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3009;
