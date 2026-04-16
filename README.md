# 中小学智能课后服务平台

基于 `Spring Boot + Vue + MyBatis + JWT` 的中小学智能课后服务平台，面向学生、家长、教师和管理员四类角色，支持课后课程管理、报名管理、作业问答、AI 辅助内容生成与平台运营管理。

## 项目简介

本项目以中小学课后服务业务为核心，结合学校课后课程组织、家校协同和智能问答场景，构建一套完整的课后服务管理平台。系统在保留前后端分离架构优势的基础上，融入大模型能力，用于课程通知生成、教学建议生成、作业问题解答和 AI 调用日志管理。

## 核心功能

### 学生端

- 查看课后课程与兴趣班信息
- 报名课程与取消报名
- 提交作业问题并查看 AI 解答
- 查看个人学生档案与相关学习信息

### 家长端

- 为孩子报名课程
- 查看孩子学习记录与报名信息
- 查看 AI 互动历史
- 维护孩子档案信息

### 教师端

- 发布、编辑、删除课程
- 查看报名名单
- 使用 AI 生成课程通知
- 使用 AI 生成教学建议

### 管理员端

- 查看平台首页与统计图表
- 管理平台用户
- 管理平台公告
- 查看 AI 日志与平台运营数据

## 技术栈

### 后端

- Spring Boot
- Spring Security
- MyBatis
- JWT
- MySQL

### 前端

- Vue 2
- Element UI
- Vuex
- Vue Router
- ECharts

### AI 能力

- 大模型 API 接入
- 课程通知生成
- 教学建议生成
- 作业问题智能问答
- AI 调用日志记录

## 角色账号

默认测试账号如下，密码统一为 `admin123`：

| 角色 | 账号 | 说明 |
| --- | --- | --- |
| 管理员 | `edu_admin` | 负责平台整体管理与数据查看 |
| 教师 | `edu_teacher` | 负责课程发布、通知生成与教学建议 |
| 家长 | `edu_parent` | 负责为孩子报名、查看学习记录 |
| 学生 | `edu_student` | 负责课程报名、作业提问与查看 AI 解答 |

## 项目结构

```text
eapple-admin        后端启动模块
eapple-common       公共模块
eapple-framework    安全认证与基础框架模块
eapple-system       业务系统模块
eapple-ui           前端项目
sql                 数据库脚本
doc                 项目说明文档
```

## 数据库脚本

初始化数据库时建议按以下顺序导入：

1. `sql/ry_20260321.sql`
2. `sql/edu_after_school.sql`
3. `sql/edu_demo_data.sql`

如果已经在当前库中完成初始化，可跳过重复导入。

## 运行环境

- JDK 17+
- MySQL 8.x
- Maven 3.8+
- Node.js 16+

## 后端启动

进入项目根目录后，确保 Maven 使用 JDK 17：

```powershell
$env:JAVA_HOME='D:\java\jdk\jdk17'
$env:Path='D:\java\jdk\jdk17\bin;' + $env:Path
mvn -pl eapple-admin -am -DskipTests compile
```

在 IDEA 中启动主类：

`eapple-admin/src/main/java/com/eapple/EduPlatformApplication.java`

默认后端地址：

- [http://localhost:8080/](http://localhost:8080/)

## 前端启动

进入前端目录：

```powershell
cd eapple-ui
npm install
npm run dev
```

默认前端地址：

- [http://localhost:80/](http://localhost:80/)

## AI 配置说明

后端配置文件位于：

- [eapple-admin/src/main/resources/application.yml](/D:/Progects/Codex/bishe/eapple-admin/src/main/resources/application.yml:1)

默认可以使用 Mock 模式演示 AI 能力。若需要接入真实大模型 API，可在配置中补充：

- `enabled`
- `baseUrl`
- `apiKey`
- `model`

## 推荐演示流程

### 管理员

1. 登录平台首页查看统计图表
2. 查看平台用户管理
3. 查看平台公告
4. 查看 AI 日志

### 教师

1. 进入课程中心发布课程
2. 使用 AI 生成课程通知
3. 使用 AI 生成教学建议
4. 查看报名记录

### 家长

1. 查看学生档案
2. 为孩子报名课程
3. 查看报名记录

### 学生

1. 查看课程中心并报名课程
2. 提交作业问题
3. 查看 AI 解答

## 项目亮点

- 面向中小学课后服务业务场景，功能结构完整
- 支持学生、家长、教师、管理员四类角色协同
- 引入 AI 能力，增强通知生成、教学建议和问答体验
- 前后端分离，便于后续二次开发与部署
- 适合作为毕业设计、课程设计和项目实践示例
