set names utf8mb4;

-- 中小学课后服务平台家长与学生演示数据扩展
-- 可在完成基础库初始化后重复执行

delete from edu_course_enrollment where enrollment_id between 100 and 116;
delete from edu_student_profile where student_user_id between 136 and 144;
delete from sys_user_role where user_id between 130 and 144;
delete from sys_user where user_id between 130 and 144;

insert into sys_user (
  user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar,
  password, status, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time,
  update_by, update_time, remark
) values
(130, 103, 'edu_parent_li', '李家长', '00', 'edu_parent_li@example.com', '13800000030', '0', '',
 '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台家长演示账号'),
(131, 103, 'edu_parent_zhang', '张家长', '00', 'edu_parent_zhang@example.com', '13800000031', '1', '',
 '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台家长演示账号'),
(132, 103, 'edu_parent_chen', '陈家长', '00', 'edu_parent_chen@example.com', '13800000032', '0', '',
 '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台家长演示账号'),
(133, 103, 'edu_parent_wu', '吴家长', '00', 'edu_parent_wu@example.com', '13800000033', '1', '',
 '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台家长演示账号'),
(134, 103, 'edu_parent_sun', '孙家长', '00', 'edu_parent_sun@example.com', '13800000034', '0', '',
 '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台家长演示账号'),
(135, 103, 'edu_parent_zhao', '赵家长', '00', 'edu_parent_zhao@example.com', '13800000035', '1', '',
 '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台家长演示账号'),
(136, 103, 'edu_student_li_1', '李沐阳', '00', 'edu_student_li_1@example.com', '13800000036', '1', '',
 '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生演示账号'),
(137, 103, 'edu_student_li_2', '李知夏', '00', 'edu_student_li_2@example.com', '13800000037', '0', '',
 '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生演示账号'),
(138, 103, 'edu_student_zhang', '张可欣', '00', 'edu_student_zhang@example.com', '13800000038', '1', '',
 '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生演示账号'),
(139, 103, 'edu_student_chen', '陈宇航', '00', 'edu_student_chen@example.com', '13800000039', '0', '',
 '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生演示账号'),
(140, 103, 'edu_student_wu_1', '吴思言', '00', 'edu_student_wu_1@example.com', '13800000040', '1', '',
 '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生演示账号'),
(141, 103, 'edu_student_wu_2', '吴星辰', '00', 'edu_student_wu_2@example.com', '13800000041', '0', '',
 '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生演示账号'),
(142, 103, 'edu_student_sun', '孙乐彤', '00', 'edu_student_sun@example.com', '13800000042', '1', '',
 '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生演示账号'),
(143, 103, 'edu_student_zhao_1', '赵子睿', '00', 'edu_student_zhao_1@example.com', '13800000043', '0', '',
 '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生演示账号'),
(144, 103, 'edu_student_zhao_2', '赵雨桐', '00', 'edu_student_zhao_2@example.com', '13800000044', '1', '',
 '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '课后服务平台学生演示账号');

insert into sys_user_role (user_id, role_id) values
(130, 103), (131, 103), (132, 103), (133, 103), (134, 103), (135, 103),
(136, 104), (137, 104), (138, 104), (139, 104), (140, 104), (141, 104), (142, 104), (143, 104), (144, 104);

insert into edu_student_profile (
  profile_id, student_user_id, student_name, parent_user_id, parent_name, grade_name, class_name,
  gender, interest_tags, status, remark, create_by, create_time
) values
(10, 136, '李沐阳', 130, '李家长', '一年级', '1班', '男', '绘本,合唱,手作', '0', '低年级兴趣拓展档案', 'admin', sysdate()),
(11, 137, '李知夏', 130, '李家长', '四年级', '2班', '女', '编程,机器人,数学', '0', '科技与学科提升档案', 'admin', sysdate()),
(12, 138, '张可欣', 131, '张家长', '三年级', '1班', '女', '美术,钢琴,主持', '0', '艺术素养提升档案', 'admin', sysdate()),
(13, 139, '陈宇航', 132, '陈家长', '六年级', '3班', '男', '篮球,足球,体能', '0', '体育健康专项档案', 'admin', sysdate()),
(14, 140, '吴思言', 133, '吴家长', '二年级', '2班', '女', '英语,绘本,诵读', '0', '语言表达启蒙档案', 'admin', sysdate()),
(15, 141, '吴星辰', 133, '吴家长', '五年级', '1班', '男', '实验,编程,羽毛球', '0', '综合能力提升档案', 'admin', sysdate()),
(16, 142, '孙乐彤', 134, '孙家长', '四年级', '3班', '女', '合唱,手作,劳动', '0', '艺术与劳动融合档案', 'admin', sysdate()),
(17, 143, '赵子睿', 135, '赵家长', '三年级', '2班', '男', '主持,口算,足球', '0', '表达与体能并重档案', 'admin', sysdate()),
(18, 144, '赵雨桐', 135, '赵家长', '五年级', '4班', '女', '科学,非遗,英语', '0', '科技与传统文化档案', 'admin', sysdate());

insert into edu_course_enrollment (
  enrollment_id, course_id, course_name, student_user_id, student_name, parent_user_id, parent_name,
  teacher_user_id, teacher_name, status, signup_source, learning_record, interaction_summary, remark, create_by, create_time
) values
(100, 11, '英语绘本悦读', 136, '李沐阳', 130, '李家长', 123, '赵老师', '0', 'parent', '已完成课堂热身，能够跟读简单绘本句型。', '家长反馈孩子愿意主动开口朗读。', '低年级阅读启蒙', 'edu_parent_li', sysdate()),
(101, 7, '童声合唱社', 136, '李沐阳', 130, '李家长', 121, '周老师', '0', 'parent', '能跟随节奏完成合唱分段练习。', '孩子舞台参与感明显提升。', '艺术素养体验', 'edu_parent_li', sysdate()),
(102, 4, '数学思维训练营', 137, '李知夏', 130, '李家长', 120, '陈老师', '0', 'student', '已能完成图形推理和数独入门任务。', '建议继续参加挑战题训练。', '学科提升报名', 'edu_student_li_2', sysdate()),
(103, 10, '机器人创客入门', 137, '李知夏', 130, '李家长', 122, '吴老师', '0', 'parent', '熟悉传感器体验环节，动手积极。', '家长希望后续增加项目制作品展示。', '科技创新报名', 'edu_parent_li', sysdate()),
(104, 2, '创意美术工坊', 138, '张可欣', 131, '张家长', 111, '李老师', '0', 'parent', '完成春季主题拼贴作品，色彩搭配自然。', '孩子偏好手工和视觉创作类活动。', '艺术素养报名', 'edu_parent_zhang', sysdate()),
(105, 8, '趣味钢琴启蒙', 138, '张可欣', 131, '张家长', 121, '周老师', '0', 'student', '掌握基本坐姿与节拍识别。', '可继续巩固单手旋律练习。', '器乐启蒙报名', 'edu_student_zhang', sysdate()),
(106, 15, '足球技巧提升', 139, '陈宇航', 132, '陈家长', 124, '孙老师', '0', 'student', '盘带与传球动作规范，团队协作意识较强。', '适合继续参加对抗小组训练。', '体育专项报名', 'edu_student_chen', sysdate()),
(107, 16, '体适能欢乐跑', 139, '陈宇航', 132, '陈家长', 124, '孙老师', '0', 'parent', '耐力训练保持稳定，课后恢复情况良好。', '家长关注体能与运动习惯培养。', '体能提升报名', 'edu_parent_chen', sysdate()),
(108, 11, '英语绘本悦读', 140, '吴思言', 133, '吴家长', 123, '赵老师', '0', 'parent', '愿意参与角色朗读，课堂专注度较高。', '适合继续阅读与表达双线培养。', '语言表达报名', 'edu_parent_wu', sysdate()),
(109, 13, '经典诵读与主持', 140, '吴思言', 133, '吴家长', 123, '赵老师', '0', 'student', '完成诗词朗读练习，吐字较清晰。', '可逐步增加上台展示机会。', '传统文化报名', 'edu_student_wu_1', sysdate()),
(110, 9, '科学实验工坊', 141, '吴星辰', 133, '吴家长', 122, '吴老师', '0', 'student', '对磁力实验和结构搭建兴趣明显。', '建议后续衔接创客课程。', '科学实践报名', 'edu_student_wu_2', sysdate()),
(111, 14, '羽毛球基础班', 141, '吴星辰', 133, '吴家长', 124, '孙老师', '0', 'parent', '握拍动作已规范，步伐训练完成度较高。', '家长希望提升身体协调与专注力。', '体育健康报名', 'edu_parent_wu', sysdate()),
(112, 17, '劳动实践小课堂', 142, '孙乐彤', 134, '孙家长', 125, '何老师', '0', 'student', '能独立完成种植记录与工具整理。', '孩子对生活技能实践积极主动。', '劳动教育报名', 'edu_student_sun', sysdate()),
(113, 18, '非遗手作体验', 142, '孙乐彤', 134, '孙家长', 125, '何老师', '0', 'parent', '完成剪纸与绳结基础作品，耐心较好。', '家长关注审美与手部精细动作发展。', '传统文化报名', 'edu_parent_sun', sysdate()),
(114, 6, '趣味口算挑战', 143, '赵子睿', 135, '赵家长', 120, '陈老师', '0', 'parent', '参与闯关练习积极，计算速度提升明显。', '适合与主持表达课程搭配。', '学科兴趣报名', 'edu_parent_zhao', sysdate()),
(115, 12, '小小演说家', 143, '赵子睿', 135, '赵家长', 123, '赵老师', '0', 'student', '能完成一分钟自我介绍并保持基本台风。', '建议继续提升语言组织和眼神交流。', '表达能力报名', 'edu_student_zhao_1', sysdate()),
(116, 18, '非遗手作体验', 144, '赵雨桐', 135, '赵家长', 125, '何老师', '0', 'parent', '对传统手作兴趣高，作品完成细致。', '适合搭配科学实践类课程形成综合体验。', '跨学科报名', 'edu_parent_zhao', sysdate());

update edu_course set current_capacity = current_capacity + 1 where course_id in (2,4,6,7,8,9,10,12,13,14,15,16,17);
update edu_course set current_capacity = current_capacity + 2 where course_id in (11,18);
