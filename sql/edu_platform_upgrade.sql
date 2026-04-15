-- 涓皬瀛︽櫤鑳借鍚庢湇鍔″钩鍙拌彍鍗曢噸鏋勮剼鏈?-- 鐢ㄩ€旓細
-- 1. 鍦ㄤ繚鐣欒嫢渚濆簳灞傝兘鍔涚殑鍓嶆彁涓嬶紝闅愯棌涓庤棰樻棤鍏崇殑榛樿澶ц彍鍗?-- 2. 鏂板鏁欒偛骞冲彴涓撳睘鑿滃崟涓庤鑹?-- 3. 璁╃鐞嗗憳/鏁欏笀/瀹堕暱/瀛︾敓鐪嬪埌鏇磋创杩戣棰樼殑涓氬姟瀵艰埅

-- 闅愯棌鑻ヤ緷榛樿鑿滃崟涓庡綋鍓嶅簱閲屽叾浠栨棤鍏充笟鍔¤彍鍗?update sys_menu
set visible = '1', update_by = 'edu_platform', update_time = sysdate()
where menu_id in (1, 2, 3, 4, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 500, 501, 2000, 2006, 2008, 2009);

-- 鏂板鏁欒偛骞冲彴瑙掕壊
insert into sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark)
select * from (
  select 101 as role_id, '璇惧悗鏈嶅姟绠＄悊鍛? as role_name, 'edu_admin' as role_key, 101 as role_sort, 1 as data_scope, 1 as menu_check_strictly, 1 as dept_check_strictly, '0' as status, '0' as del_flag, 'edu_platform' as create_by, sysdate() as create_time, '' as update_by, null as update_time, '涓皬瀛︽櫤鑳借鍚庢湇鍔″钩鍙扮鐞嗗憳' as remark
  union all
  select 102, '璇惧悗鏈嶅姟鏁欏笀', 'edu_teacher', 102, 2, 1, 1, '0', '0', 'edu_platform', sysdate(), '', null, '涓皬瀛︽櫤鑳借鍚庢湇鍔″钩鍙版暀甯?
  union all
  select 103, '璇惧悗鏈嶅姟瀹堕暱', 'edu_parent', 103, 2, 1, 1, '0', '0', 'edu_platform', sysdate(), '', null, '涓皬瀛︽櫤鑳借鍚庢湇鍔″钩鍙板闀?
  union all
  select 104, '璇惧悗鏈嶅姟瀛︾敓', 'edu_student', 104, 2, 1, 1, '0', '0', 'edu_platform', sysdate(), '', null, '涓皬瀛︽櫤鑳借鍚庢湇鍔″钩鍙板鐢?
) t
where not exists (select 1 from sys_role r where r.role_id = t.role_id or r.role_key = t.role_key);

-- 鏂板骞冲彴涓昏彍鍗曚笌椤甸潰鑿滃崟
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select * from (
  select 3000 as menu_id, '璇惧悗鏈嶅姟骞冲彴' as menu_name, 0 as parent_id, 1 as order_num, 'edu-platform' as path, '' as component, '' as `query`, '' as route_name, 1 as is_frame, 0 as is_cache, 'M' as menu_type, '0' as visible, '0' as status, '' as perms, 'education' as icon, 'edu_platform' as create_by, sysdate() as create_time, '' as update_by, null as update_time, '涓皬瀛︽櫤鑳借鍚庢湇鍔″钩鍙扮洰褰? as remark
  union all
  select 3001, '骞冲彴棣栭〉', 3000, 1, 'dashboard', 'edu/dashboard/index', '', '', 1, 0, 'C', '0', '0', 'edu:dashboard:view', 'dashboard', 'edu_platform', sysdate(), '', null, '骞冲彴棣栭〉'
  union all
  select 3002, '璇剧▼涓績', 3000, 2, 'course', 'edu/course/index', '', '', 1, 0, 'C', '0', '0', 'edu:course:list', 'guide', 'edu_platform', sysdate(), '', null, '璇剧▼涓績'
  union all
  select 3003, '瀛︾敓妗ｆ', 3000, 3, 'student', 'edu/student/index', '', '', 1, 0, 'C', '0', '0', 'edu:student:list', 'peoples', 'edu_platform', sysdate(), '', null, '瀛︾敓妗ｆ'
  union all
  select 3004, '鎶ュ悕璁板綍', 3000, 4, 'enrollment', 'edu/enrollment/index', '', '', 1, 0, 'C', '0', '0', 'edu:enrollment:list', 'form', 'edu_platform', sysdate(), '', null, '璇剧▼鎶ュ悕璁板綍'
  union all
  select 3005, '浣滀笟闂瓟', 3000, 5, 'question', 'edu/question/index', '', '', 1, 0, 'C', '0', '0', 'edu:question:list', 'message', 'edu_platform', sysdate(), '', null, '浣滀笟闂涓嶢I瑙ｇ瓟'
  union all
  select 3006, 'AI鏃ュ織', 3000, 6, 'ai-log', 'edu/aiLog/index', '', '', 1, 0, 'C', '0', '0', 'edu:ai:list', 'redis', 'edu_platform', sysdate(), '', null, 'AI璋冪敤鏃ュ織'
  union all
  select 3007, '骞冲彴鐢ㄦ埛', 3000, 7, 'platform-user', 'edu/platformUser/index', '', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'edu_platform', sysdate(), '', null, '骞冲彴鐢ㄦ埛绠＄悊'
  union all
  select 3008, '骞冲彴鍏憡', 3000, 8, 'platform-notice', 'edu/platformNotice/index', '', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'message', 'edu_platform', sysdate(), '', null, '骞冲彴鍏憡绠＄悊'
  union all
  select 3010, '璇剧▼鏌ヨ', 3002, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:course:query', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3011, '璇剧▼鏂板', 3002, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:course:add', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3012, '璇剧▼淇敼', 3002, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:course:edit', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3013, '璇剧▼鍒犻櫎', 3002, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:course:remove', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3014, '璇剧▼鎶ュ悕', 3002, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:course:enroll', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3020, '瀛︾敓鏌ヨ', 3003, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:student:query', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3021, '瀛︾敓鏂板', 3003, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:student:add', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3022, '瀛︾敓淇敼', 3003, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:student:edit', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3023, '瀛︾敓鍒犻櫎', 3003, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:student:remove', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3030, '鎶ュ悕缂栬緫', 3004, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:enrollment:edit', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3040, '闂鏌ヨ', 3005, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:question:query', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3041, '闂鏂板', 3005, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:question:add', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3042, '闂淇敼', 3005, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:question:edit', '#', 'edu_platform', sysdate(), '', null, ''
  union all
  select 3043, '闂鍒犻櫎', 3005, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'edu:question:remove', '#', 'edu_platform', sysdate(), '', null, ''
) t
where not exists (select 1 from sys_menu m where m.menu_id = t.menu_id);

-- 閲嶇疆鏁欒偛瑙掕壊鑿滃崟鎺堟潈
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
  select 102, 3010 union all
  select 102, 3014 union all
  select 102, 3030 union all
  select 102, 3040 union all
  select 102, 3041 union all
  select 103, 3000 union all
  select 103, 3001 union all
  select 103, 3002 union all
  select 103, 3004 union all
  select 103, 3005 union all
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
  select 104, 3010 union all
  select 104, 3014 union all
  select 104, 3020 union all
  select 104, 3040 union all
  select 104, 3041
) t;

-- 濡傚瓨鍦ㄦ紨绀鸿处鍙凤紝鍒欑粦瀹氭暀鑲茶鑹?delete ur
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

-- 缁熶竴灏嗗钩鍙拌彍鍗曟媿骞虫垚涓€绾ц彍鍗曪紝閬垮厤渚ф爮闇€瑕佸厛灞曞紑鐩綍鍚庡啀杩涘叆椤甸潰
update sys_menu set parent_id = 0, order_num = 1, path = 'index', route_name = 'Index', is_frame = 1, visible = '0', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3001;
update sys_menu set parent_id = 0, order_num = 2, path = 'edu/course', route_name = 'EduCourse', is_frame = 1, visible = '0', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3002;
update sys_menu set parent_id = 0, order_num = 3, path = 'edu/student', route_name = 'EduStudent', is_frame = 1, visible = '0', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3003;
update sys_menu set parent_id = 0, order_num = 4, path = 'edu/enrollment', route_name = 'EduEnrollment', is_frame = 1, visible = '0', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3004;
update sys_menu set parent_id = 0, order_num = 5, path = 'edu/question', route_name = 'EduQuestion', is_frame = 1, visible = '0', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3005;
update sys_menu set parent_id = 0, order_num = 6, path = 'edu/ai-log', route_name = 'EduAiLog', is_frame = 1, visible = '0', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3006;
update sys_menu set parent_id = 0, order_num = 7, path = 'edu/platform-user', route_name = 'EduPlatformUser', is_frame = 1, visible = '0', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3007;
update sys_menu set parent_id = 0, order_num = 8, path = 'edu/platform-notice', route_name = 'EduPlatformNotice', is_frame = 1, visible = '0', update_by = 'edu_platform', update_time = sysdate() where menu_id = 3008;
delete from sys_role_menu where menu_id = 3000;
delete from sys_menu where menu_id = 3000;


