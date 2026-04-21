# 中小学智能课后服务平台说明文档

## 1. 项目定位

中小学智能课后服务平台面向学校课后延时服务场景，围绕学生、家长、教师和管理员四类角色，构建课程管理、报名管理、作业问答、AI 辅助生成和平台运营管理能力。

项目目标包括：

- 提升课后课程组织与报名管理效率
- 方便家长及时了解孩子的学习情况
- 帮助教师借助 AI 完成课程通知和教学建议生成
- 为管理员提供统一的平台管理与数据统计能力

## 2. 功能模块

### 2.1 平台首页

- 展示平台总体统计数据
- 展示热门选课排行
- 展示业务图表与运营概览

### 2.2 课程中心

- 查看课程列表
- 发布课程
- 编辑课程
- 报名课程
- 取消报名
- 生成 AI 通知与教学建议

### 2.3 学生档案

- 查看学生档案
- 家长维护孩子档案
- 管理员查看学生基础信息

### 2.4 报名记录

- 查看课程报名记录
- 查看学生、家长、教师对应的报名情况

### 2.5 作业问答

- 学生提交作业问题
- 调用 AI 生成解答
- 查看问答记录

### 2.6 AI 日志

- 记录 AI 调用类型
- 记录调用状态与返回结果
- 便于管理员查看平台智能能力使用情况

### 2.7 平台用户

- 管理平台账号
- 维护角色权限
- 支持四类角色统一管理

### 2.8 平台公告

- 发布平台通知
- 维护公告内容
- 查看公告状态

## 3. 技术方案

### 后端技术

- Spring Boot
- Spring Security
- MyBatis
- JWT
- MySQL

### 前端技术

- Vue 2
- Element UI
- Vuex
- Vue Router
- ECharts

### 智能能力

- 大模型 API 接入
- AI 问答
- AI 通知生成
- AI 教学建议生成
- AI 调用日志记录

## 4. 数据库脚本

数据库初始化建议按顺序执行：

1. `sql/ry_20260321.sql`
2. `sql/edu_after_school.sql`
3. `sql/edu_demo_data.sql`

其中：

- `ry_20260321.sql` 为基础系统表
- `edu_after_school.sql` 为教育业务表
- `edu_demo_data.sql` 为演示数据

## 5. 默认账号

默认账号密码如下，密码统一为 `admin123`：

- 管理员：`edu_admin`
- 教师：`edu_teacher`
- 家长：`edu_parent`
- 学生：`edu_student`

## 6. 启动方式

### 后端

启动主类：

- `eapple-admin/src/main/java/com/eapple/EduPlatformApplication.java`

默认地址：

- [http://localhost:8080/](http://localhost:8080/)

### 前端

进入 `eapple-ui` 目录后执行：

```powershell
npm install
npm run dev
```

默认地址：

- [http://localhost:80/](http://localhost:80/)

## 7. AI 配置

AI 配置位于：

- [eapple-admin/src/main/resources/application.yml](/D:/Progects/Codex/bishe/eapple-admin/src/main/resources/application.yml:1)

如果需要使用真实模型接口，可补充：

- `enabled`
- `baseUrl`
- `apiKey`
- `model`

若仅用于本地演示，可保留 Mock 配置。

## 8. 适用场景

本项目适用于：

- 毕业设计系统实现
- 课程设计项目展示
- 中小学课后服务管理原型
- 教育信息化平台原型开发

## 9. 当前说明

当前版本已经完成：

- 平台整体前后端改造
- 角色化登录
- 教育业务模块实现
- 课程报名与取消报名
- AI 日志与可视化看板
- 平台 UI 统一优化
