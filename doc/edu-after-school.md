# 基于 SpringBoot 与 Vue 的中小学智能课后服务平台

## 1. 项目定位

本项目基于 `RuoYi-Vue` 二次开发，围绕中小学课后服务平台场景实现学生、家长、教师、管理员四类角色的协同管理，并接入大模型 API 完成作业问答、通知生成和教学建议生成。

## 2. 功能对应关系

### 学生

- 查看课后课程并报名
- 提交作业问题并查看 AI 解答
- 查看个人课程报名和 AI 互动记录

### 家长

- 为孩子维护学生档案
- 为孩子报名课程
- 查看学习记录与 AI 历史

### 教师

- 发布课程、维护课程信息
- 管理报名名单与学习记录
- 使用 AI 生成课程通知、教学建议

### 管理员

- 统一管理课程、学生档案、报名记录、作业问答、AI 调用日志
- 查看平台统计看板

## 3. 后端接口模块

- `/edu/dashboard` 平台统计看板
- `/edu/course` 课程管理与报名
- `/edu/student` 学生档案与家长绑定
- `/edu/enrollment` 报名记录与学习记录
- `/edu/question` 作业问题与 AI 解答
- `/edu/aiLog` AI 调用日志

## 4. 数据库脚本

执行脚本：

- `sql/ry_20260321.sql`
- `sql/edu_after_school.sql`
- `sql/edu_demo_data.sql`

其中 `edu_after_school.sql` 新增以下业务表：

- `edu_student_profile`
- `edu_course`
- `edu_course_enrollment`
- `edu_homework_question`
- `edu_ai_log`

同时初始化了以下角色：

- `edu_admin`
- `edu_teacher`
- `edu_parent`
- `edu_student`

`edu_demo_data.sql` 会补充：

- 4 个演示账号：`edu_admin / edu_teacher / edu_parent / edu_student`
- 3 门演示课程
- 报名记录、作业问答与 AI 调用日志
- 可直接用于首页和答辩演示的数据

## 5. AI 配置

在 `ruoyi-admin/src/main/resources/application.yml` 中新增了：

```yml
edu:
  ai:
    enabled: false
    endpoint: https://api.openai.com/v1/chat/completions
    apiKey:
    model: gpt-4o-mini
```

说明：

- 默认 `enabled: false`，系统会走本地 Mock 结果，方便演示
- 若填写真实 API Key 并开启 `enabled: true`，即可调用真实大模型
- 已增加敏感词过滤与输入长度限制，便于保证响应安全可控

## 6. 前端页面

新增页面如下：

- `edu/dashboard/index` 看板
- `edu/course/index` 课程管理
- `edu/student/index` 学生档案
- `edu/enrollment/index` 报名管理
- `edu/question/index` 作业问答
- `edu/aiLog/index` AI 日志

登录后的 `src/views/index.vue` 已改为演示首页，会自动展示：

- 平台核心统计
- 角色演示流程
- 演示账号说明
- 快速进入课程、问答、AI 日志等业务入口

## 7. 当前注意事项

- 当前环境 Java 版本为 `1.8`，而当前若依主分支要求 `JDK 17+`
- 若要本地编译运行，请先切换到 JDK 17 或更高版本
- 导入 SQL 后，需要在系统用户中手动给不同账号分配 `edu_admin / edu_teacher / edu_parent / edu_student` 角色
