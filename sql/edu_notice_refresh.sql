SET NAMES utf8mb4;

UPDATE sys_notice
SET
    notice_title = '平台说明：中小学智能课后服务平台正式启用',
    notice_type = '1',
    notice_content = '<p>中小学智能课后服务平台现已正式启用，围绕学生、家长、教师与管理员四类角色提供统一服务入口。</p><p>平台支持课后课程发布、兴趣班报名、学习记录查看、作业问答、AI 辅助答疑、公告通知与平台统计分析等核心功能，适用于日常演示与毕业设计展示。</p><p>如需完善个人信息，请前往对应模块进行维护。</p>',
    status = '0',
    update_by = 'admin',
    update_time = NOW()
WHERE notice_id = 3;

UPDATE sys_notice
SET
    notice_title = '运维通知：本周平台数据将进行例行校验',
    notice_type = '1',
    notice_content = '<p>为保障平台演示数据的完整性与一致性，系统将在本周内进行一次例行数据校验与结构巡检。</p><p>校验期间，课程报名、平台公告、学生档案与 AI 日志等功能均可正常访问；如出现短时加载波动，请稍后刷新重试。</p>',
    status = '0',
    update_by = 'admin',
    update_time = NOW()
WHERE notice_id = 2;

UPDATE sys_notice
SET
    notice_title = '平台通知：春季课后课程报名通道已开放',
    notice_type = '2',
    notice_content = '<p>春季课后服务课程现已开放报名，涵盖科技创新、艺术素养、体育健康、语言表达与综合实践等多个方向。</p><p>学生和家长可进入课程中心查看课程简介、授课教师、开课时间与容量信息，并按需完成报名或取消报名操作。</p>',
    status = '0',
    update_by = 'admin',
    update_time = NOW()
WHERE notice_id = 1;

INSERT INTO sys_notice (
    notice_title,
    notice_type,
    notice_content,
    status,
    create_by,
    create_time,
    update_by,
    update_time,
    remark
) VALUES
(
    '教学安排：本周新增人工智能启蒙与创意表达课程',
    '2',
    '<p>本周平台新增“人工智能启蒙实验室”“创意表达与演讲训练”等课后课程，进一步丰富科技与素养拓展方向。</p><p>请学生结合个人兴趣标签与学习目标合理选课，教师可同步查看报名情况并生成课程通知。</p>',
    '0',
    'admin',
    NOW(),
    'admin',
    NOW(),
    '平台演示公告'
),
(
    '家校协同：请及时完善学生档案与家长绑定信息',
    '1',
    '<p>为便于课程推荐、学习记录展示与家长端协同查看，请学生及时完善年级、班级、兴趣标签等档案信息，并确认家长账号绑定状态。</p><p>完成档案维护后，系统可结合画像与课程数据提供更准确的智能推荐结果。</p>',
    '0',
    'admin',
    NOW(),
    'admin',
    NOW(),
    '平台演示公告'
),
(
    'AI 服务提示：智能问答与内容生成功能已开放体验',
    '2',
    '<p>平台现已接入大模型能力，可在作业问答、教学建议、课程通知与课程推荐等业务场景中提供智能辅助。</p><p>如遇生成时间较长，请耐心等待；管理员可在 AI 中心统一查看模型选择、调用日志与运行概览。</p>',
    '0',
    'admin',
    NOW(),
    'admin',
    NOW(),
    '平台演示公告'
),
(
    '安全提醒：请勿在演示环境中录入真实敏感信息',
    '1',
    '<p>当前系统为教学演示与毕业设计展示环境，请勿录入真实身份证号、家庭住址、银行卡等敏感信息。</p><p>如需展示业务流程，建议使用平台内置测试账号与测试数据完成操作。</p>',
    '0',
    'admin',
    NOW(),
    'admin',
    NOW(),
    '平台演示公告'
);
