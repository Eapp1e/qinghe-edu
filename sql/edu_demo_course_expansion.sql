-- 扩展教师与课程演示数据
-- 可在已导入基础平台结构后重复执行

delete from edu_course where course_id between 4 and 18;
delete from sys_user_role where user_id between 120 and 125;
delete from sys_user where user_id between 120 and 125;

insert into sys_user values
(120, 103, 'edu_teacher_math', '陈老师', '00', 'edu_teacher_math@example.com', '13800000020', '0', '', 'science', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台数学教师'),
(121, 103, 'edu_teacher_music', '周老师', '00', 'edu_teacher_music@example.com', '13800000021', '0', '', 'art', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台音乐教师'),
(122, 103, 'edu_teacher_science', '吴老师', '00', 'edu_teacher_science@example.com', '13800000022', '0', '', 'science,computer', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台科学教师'),
(123, 103, 'edu_teacher_language', '赵老师', '00', 'edu_teacher_language@example.com', '13800000023', '0', '', 'humanities,general', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台语言教师'),
(124, 103, 'edu_teacher_sports', '孙老师', '00', 'edu_teacher_sports@example.com', '13800000024', '0', '', 'sports', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台体育教师'),
(125, 103, 'edu_teacher_labor', '何老师', '00', 'edu_teacher_labor@example.com', '13800000025', '0', '', 'general', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台综合实践教师');

insert into sys_user_role values
(120, 101),
(121, 101),
(122, 101),
(123, 101),
(124, 101),
(125, 101);

insert into edu_course (
  course_id, course_name, category, teacher_user_id, teacher_name, campus, week_day, start_time, end_time,
  start_date, end_date, max_capacity, current_capacity, status, description, ai_notice, ai_suggestion, remark, create_by, create_time
) values
(4, '数学思维训练营', '理科拓展', 120, '陈老师', '教学楼A301', '周一', '17:40', '19:00', '2026-04-13', '2026-06-30', 28, 6, '0', '通过趣味题、图形推理和应用题讲解提升数学思维能力。', '', '', '适合中高年级学生', 'edu_teacher_math', sysdate()),
(5, '奥数启蒙小课堂', '理科拓展', 120, '陈老师', '教学楼A302', '周三', '16:00', '17:20', '2026-04-15', '2026-06-30', 26, 4, '0', '从数感、规律和简单逻辑推理入手，帮助学生建立奥数兴趣。', '', '', '低年级启蒙课程', 'edu_teacher_math', sysdate()),
(6, '趣味口算挑战', '理科拓展', 120, '陈老师', '教学楼A203', '周五', '15:40', '17:00', '2026-04-17', '2026-06-30', 30, 8, '0', '结合闯关和游戏机制训练口算速度与准确率。', '', '', '适合一至三年级', 'edu_teacher_math', sysdate()),
(7, '童声合唱社', '艺术素养', 121, '周老师', '音乐教室C201', '周二', '16:10', '17:30', '2026-04-14', '2026-06-30', 32, 10, '0', '训练基础发声、节奏感与合作演唱能力，准备校园展示。', '', '', '校园展示课程', 'edu_teacher_music', sysdate()),
(8, '趣味钢琴启蒙', '艺术素养', 121, '周老师', '音乐教室C203', '周四', '16:00', '17:20', '2026-04-16', '2026-06-30', 18, 5, '0', '从识谱、节拍和手型入门，让学生体验基础键盘演奏。', '', '', '器乐启蒙课程', 'edu_teacher_music', sysdate()),
(9, '科学实验工坊', '理科拓展', 122, '吴老师', '实验室B201', '周一', '16:10', '17:40', '2026-04-13', '2026-06-30', 24, 7, '0', '围绕空气、水、磁力和简单机械开展动手实验。', '', '', '动手实验课程', 'edu_teacher_science', sysdate()),
(10, '机器人创客入门', '计算机编程', 122, '吴老师', '创客教室B305', '周三', '17:30', '19:00', '2026-04-15', '2026-06-30', 22, 6, '0', '结合积木搭建与传感器体验，培养工程思维与协作能力。', '', '', '创客特色课程', 'edu_teacher_science', sysdate()),
(11, '英语绘本悦读', '语言表达', 123, '赵老师', '图书角D101', '周二', '15:50', '17:10', '2026-04-14', '2026-06-30', 30, 9, '0', '通过分级绘本阅读和角色朗读提升英语兴趣与表达能力。', '', '', '英语阅读课程', 'edu_teacher_language', sysdate()),
(12, '小小演说家', '语言表达', 123, '赵老师', '报告厅D202', '周四', '17:30', '18:50', '2026-04-16', '2026-06-30', 26, 8, '0', '围绕自我介绍、故事表达和主题演讲训练语言组织与台风。', '', '', '口语表达课程', 'edu_teacher_language', sysdate()),
(13, '经典诵读与主持', '文科阅读', 123, '赵老师', '综合楼D203', '周五', '16:10', '17:30', '2026-04-17', '2026-06-30', 28, 5, '0', '结合诗词诵读与校园主持练习，提升普通话与舞台表达。', '', '', '传统文化融合课程', 'edu_teacher_language', sysdate()),
(14, '羽毛球基础班', '体育健康', 124, '孙老师', '体育馆A区', '周一', '15:50', '17:10', '2026-04-13', '2026-06-30', 30, 11, '0', '训练握拍、发球、步伐与基本对打能力，适合初学学生。', '', '', '热门运动课程', 'edu_teacher_sports', sysdate()),
(15, '足球技巧提升', '体育健康', 124, '孙老师', '学校足球场', '周三', '16:20', '17:50', '2026-04-15', '2026-06-30', 36, 12, '0', '围绕带球、传球、射门与团队配合设计专项训练。', '', '', '团队运动课程', 'edu_teacher_sports', sysdate()),
(16, '体适能欢乐跑', '体育健康', 124, '孙老师', '操场跑道', '周五', '17:30', '18:40', '2026-04-17', '2026-06-30', 35, 7, '0', '通过协调、柔韧、耐力与趣味接力提升学生综合体能。', '', '', '低门槛体能课程', 'edu_teacher_sports', sysdate()),
(17, '劳动实践小课堂', '综合素养', 125, '何老师', '劳动实践基地', '周二', '16:00', '17:30', '2026-04-14', '2026-06-30', 24, 6, '0', '开展种植、整理、工具使用与生活劳动技能实践。', '', '', '劳动教育课程', 'edu_teacher_labor', sysdate()),
(18, '非遗手作体验', '综合素养', 125, '何老师', '综合实践室E201', '周四', '15:50', '17:20', '2026-04-16', '2026-06-30', 20, 4, '0', '体验剪纸、绳结、布艺等传统手作项目，培养审美与耐心。', '', '', '综合实践特色课程', 'edu_teacher_labor', sysdate());
