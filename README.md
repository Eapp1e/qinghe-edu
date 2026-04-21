# 青禾智学课后服务平台

基于 `Spring Boot + Vue 2 + MyBatis + JWT + MySQL` 的中小学智能课后服务平台，面向学生、家长、教师与管理员四类角色，支持课后课程管理、报名记录管理、学生档案维护、作业问答、网课资源整合与 AI 辅助能力接入。

## 项目简介

青禾智学课后服务平台聚焦中小学课后延时服务场景，结合课程发布、兴趣班报名、家校协同、学生成长档案与智能问答需求，构建了一套可直接演示、可继续二次开发的前后端分离系统。

项目保留了成熟后台管理框架的工程化能力，同时结合教育业务进行重构，形成了适用于毕业设计、课程设计和教学演示的完整平台：

- 学生端：查看课程、报名课程、提问作业问题、获取 AI 解答、浏览网课资源
- 家长端：查看孩子档案、代报名课程、填写学习记录、查看 AI 互动历史
- 教师端：发布课程、启停课程、查看报名情况、使用 AI 生成通知与教学建议
- 管理端：统一管理平台用户、课程、公告、AI 日志、统计图表与网课资源

## 核心功能

### 1. 平台首页与数据看板

- 首页展示平台动态、近期课程安排、热门选课排行与活跃趋势图表
- 基于 ECharts 实现可视化统计展示
- 支持不同角色查看统一的平台公共信息

### 2. 课程中心

- 教师可发布课程、编辑课程、调整课程状态
- 学生和家长可查看课程列表并完成报名
- 管理员可统一查看全部课程与运行状态

### 3. 学生档案

- 学生与家长可维护本人或孩子的档案信息
- 教师与管理员可查看全量学生档案
- 档案支持兴趣标签、年级班级、家长关联等信息维护
- 可结合学生档案进行 AI 课程推荐

### 4. 报名记录 / 学习记录

- 学生、家长可查看课程报名结果与学习情况
- 家长端可补充学习记录
- 教师和管理员可查看课程参与情况与过程数据

### 5. 作业问答

- 学生可提交作业问题并查看 AI 解答
- 教师和管理员可查看问答记录，并支持重新生成解答
- 问答记录与 AI 中心联动展示

### 6. AI 中心

- 支持查看模型选项、最近 AI 使用记录、调用状态与结果
- 支持不同角色展示差异化 AI 使用入口
- 已接入硅基流动模型接口，支持问答、通知生成、教学建议、资源推荐等场景

### 7. 平台公告

- 公告支持发布、查看、已读记录管理
- 学生和家长侧重查看
- 教师和管理员可进行更完整的运营管理

### 8. 网课中心

- 整合官方平台、科普站点与优质免费视频资源
- 学生端可进行 AI 网课推荐
- 管理端支持维护和新增网课资源

### 9. 平台用户管理

- 管理员可统一查看平台用户
- 支持学生、家长、教师账号注册与角色区分
- 支持账号状态控制、重置密码和基础信息维护

## 技术栈

### 后端

- Spring Boot 4
- Spring Security
- MyBatis
- PageHelper
- JWT
- Druid
- Fastjson2
- MySQL
- Maven

### 前端

- Vue 2
- Vue Router
- Vuex
- Element UI
- ECharts
- Axios
- Sass
- SortableJS / vuedraggable

### AI 与平台能力

- SiliconFlow 大模型接口接入
- AI 安全关键词过滤与调用日志记录
- 教育业务场景下的问答、推荐、内容生成能力

## 项目结构

```text
eapple-admin        后端启动模块，包含控制器与应用启动类
eapple-common       通用工具、基础常量、公共封装
eapple-framework    安全认证、权限控制、系统基础框架
eapple-system       教育业务核心模块
eapple-ui           前端项目
sql                 数据库初始化与演示数据脚本
doc                 项目文档与补充说明
runtime             本地运行生成目录
```

## 主要业务模块

### 后端实体与业务对象

当前教育业务核心对象主要包括：

- `EduCourse`：课后课程
- `EduCourseEnrollment`：课程报名记录
- `EduHomeworkQuestion`：作业问答
- `EduStudentProfile`：学生档案
- `EduAiLog`：AI 调用日志
- `EduAiModelOption`：AI 模型选项
- `EduDashboardVo`：平台首页统计对象

### 前端页面模块

`eapple-ui/src/views/edu` 下主要包含：

- `dashboard`：平台首页
- `course`：课程中心
- `student`：学生档案
- `enrollment`：报名记录 / 学习记录
- `question`：作业问答
- `aiCenter`：AI 中心
- `aiLog`：AI 日志
- `platformUser`：平台用户
- `platformNotice`：平台公告
- `onlineCourse`：网课中心

## 数据库脚本说明

`sql` 目录下已包含基础表结构、平台升级脚本与演示数据脚本，当前常用脚本包括：

- `ry_20260321.sql`
- `edu_after_school.sql`
- `edu_platform_bootstrap.sql`
- `edu_platform_upgrade.sql`
- `edu_demo_data.sql`
- `edu_demo_course_expansion.sql`
- `edu_demo_family_expansion.sql`
- `edu_demo_question_expansion.sql`
- `edu_notice_refresh.sql`
- `quartz.sql`

推荐初始化顺序：

1. `sql/ry_20260321.sql`
2. `sql/edu_after_school.sql`
3. `sql/edu_platform_bootstrap.sql`
4. `sql/edu_platform_upgrade.sql`
5. `sql/edu_demo_data.sql`
6. 根据需要补充导入扩展演示脚本

## 运行环境

- JDK 17+
- Maven 3.8+
- Node.js 16+
- MySQL 8.x

## 后端启动

在项目根目录执行：

```powershell
mvn -pl eapple-admin -am -DskipTests compile
```

启动主类：

`eapple-admin/src/main/java/com/eapple/EduPlatformApplication.java`

默认后端地址：

- [http://localhost:8080/](http://localhost:8080/)

## 前端启动

进入前端目录后执行：

```powershell
cd eapple-ui
npm install
npm run dev
```

默认前端地址：

- [http://localhost/](http://localhost/)

## AI 配置说明

AI 相关配置位于：

[application.yml](/D:/Progects/Codex/bishe/eapple-admin/src/main/resources/application.yml:1)

当前项目已支持：

- AI 问答
- AI 教学建议生成
- AI 课程通知生成
- AI 网课推荐
- AI 调用日志记录

如需切换模型或更换平台，可在配置中调整：

- `edu.ai.enabled`
- `edu.ai.endpoint`
- `edu.ai.apiKey`
- `edu.ai.model`
- `edu.ai.timeoutSeconds`
- `edu.ai.maxPromptLength`

## 演示账号建议

项目已内置多组演示账号，并支持学生、家长、教师的自助注册。建议演示时使用四类典型角色：

- 管理员：查看平台全局数据、平台用户、公告与 AI 日志
- 教师：发布课程、处理报名、生成通知与教学建议
- 家长：查看学生档案、报名课程、填写学习记录
- 学生：报名课程、提交作业问题、使用网课中心与 AI 推荐

## 项目亮点

- 面向中小学课后服务场景，功能结构完整，角色划分清晰
- 基于前后端分离架构，适合继续扩展和二次开发
- 融合 AI 问答、推荐与内容生成能力，具备展示性和实用性
- 包含完整演示数据、网课资源、图表首页与多角色流程
- 界面风格已针对教育平台场景进行统一美化和品牌重构

## 适用场景

- 毕业设计
- 课程设计
- 教育信息化系统原型展示
- Spring Boot 与 Vue 前后端分离项目练习

## 仓库地址

- GitHub: [https://github.com/Eapp1e/qinghe-edu](https://github.com/Eapp1e/qinghe-edu)
