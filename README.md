# 基于 SpringBoot 与 Vue 的中小学智能课后服务平台

## 项目简介

本项目是一个面向中小学场景的智能课后服务平台，基于 `RuoYi-Vue` 二次开发完成，结合 `SpringBoot + Vue + MyBatis + JWT` 前后端分离架构，并集成大模型能力实现智能问答与内容生成。

本课题围绕中小学课后服务业务展开，重点服务以下四类角色：

- 学生：查看课后课程、报名兴趣班、提交作业问题并查看 AI 解答
- 家长：为孩子报名课程、查看学习记录与 AI 互动历史
- 教师：发布课程、管理报名名单、使用 AI 生成通知或教学建议
- 管理员：统一管理用户、课程、AI 调用日志，并查看统计报表

## 课题名称

基于 SpringBoot 与 Vue 的中小学智能课后服务平台的设计与实现

## 技术栈

### 后端

- Spring Boot
- Spring Security
- JWT
- MyBatis
- Druid
- PageHelper
- Fastjson2

### 前端

- Vue 2
- Vuex
- Vue Router
- Element UI
- Axios

### AI 能力

- 支持接入大模型 API
- 支持作业问题智能解答
- 支持教师通知生成
- 支持教学建议生成
- 支持 AI 调用日志记录与安全限制

## 系统功能设计

### 1. 学生端

- 查看课后服务课程
- 报名兴趣班
- 提交作业问题
- 查看 AI 解答内容
- 查看个人学习记录与 AI 历史

### 2. 家长端

- 维护孩子档案
- 为孩子报名课程
- 查看课程报名状态
- 查看学习记录与互动总结
- 查看孩子的 AI 互动历史

### 3. 教师端

- 发布课后课程
- 编辑课程信息
- 查看与管理报名名单
- 记录学生学习情况
- 使用 AI 生成课程通知
- 使用 AI 生成教学建议

### 4. 管理员端

- 查看平台统计看板
- 统一管理课程、报名、学生档案
- 查看作业问答记录
- 查看 AI 调用日志
- 进行平台演示数据管理

## 项目结构

```text
bishe
├─ ruoyi-admin           后端启动模块
├─ ruoyi-framework       权限与框架配置
├─ ruoyi-system          业务模块
├─ ruoyi-common          通用模块
├─ ruoyi-ui              前端 Vue 项目
├─ sql                   数据库脚本
└─ doc                   项目说明文档
```

## 本项目新增模块

### 后端教育业务模块

- `com.ruoyi.web.controller.edu`
- `com.ruoyi.system.domain.edu`
- `com.ruoyi.system.mapper.edu`
- `com.ruoyi.system.service.edu`
- `com.ruoyi.system.service.impl.edu`

### 前端教育业务页面

- `ruoyi-ui/src/views/edu/dashboard`
- `ruoyi-ui/src/views/edu/course`
- `ruoyi-ui/src/views/edu/student`
- `ruoyi-ui/src/views/edu/enrollment`
- `ruoyi-ui/src/views/edu/question`
- `ruoyi-ui/src/views/edu/aiLog`

### 首页演示流程

登录后的首页已经改造成“毕业设计演示首页”，会自动展示：

- 角色演示建议流程
- 平台统计数据
- 演示账号信息
- 快速业务入口
- 最近平台动态

## 数据库脚本说明

请按以下顺序执行数据库脚本：

```sql
sql/ry_20260321.sql
sql/edu_after_school.sql
sql/edu_demo_data.sql
```

### 脚本用途

- `ry_20260321.sql`
  初始化若依基础表结构与系统数据

- `edu_after_school.sql`
  初始化课后服务平台业务表、菜单、权限、角色

- `edu_demo_data.sql`
  初始化演示账号、课程、报名记录、作业问答、AI 日志

## 演示账号

导入演示数据后可直接使用以下账号，密码统一为：

```text
admin123
```

| 角色 | 账号 | 用途 |
| --- | --- | --- |
| 管理员 | `edu_admin` | 查看统计看板、课程管理、AI 日志 |
| 教师 | `edu_teacher` | 发布课程、查看报名、生成 AI 通知 |
| 家长 | `edu_parent` | 维护孩子档案、为孩子报名 |
| 学生 | `edu_student` | 提交作业问题、查看 AI 解答 |

## 运行环境

### 推荐环境

- JDK 17 及以上
- MySQL 8.x
- Redis
- Node.js 16 及以上
- Maven 3.8 及以上

### 当前注意事项

当前若依主分支为 Spring Boot 4.x，对应需要：

```text
JDK 17+
```

如果本机仍是 JDK 8，将无法正常编译运行。

## 后端启动

### 1. 修改数据库配置

编辑文件：

```text
ruoyi-admin/src/main/resources/application-druid.yml
```

配置自己的 MySQL 账号密码。

### 2. 修改 Redis 配置

编辑文件：

```text
ruoyi-admin/src/main/resources/application.yml
```

配置 Redis 地址、端口和密码。

### 3. 启动后端

在项目根目录执行：

```bash
mvn clean install
mvn -pl ruoyi-admin -am spring-boot:run
```

## 前端启动

进入前端目录：

```bash
cd ruoyi-ui
```

安装依赖：

```bash
npm install
```

启动前端：

```bash
npm run dev
```

默认访问地址：

```text
http://localhost:80
```

## AI 配置说明

配置文件位置：

```text
ruoyi-admin/src/main/resources/application.yml
```

已新增如下配置：

```yml
edu:
  ai:
    enabled: false
    endpoint: https://api.openai.com/v1/chat/completions
    apiKey:
    model: gpt-4o-mini
    timeoutSeconds: 30
    maxPromptLength: 1200
```

### 使用说明

- 默认 `enabled: false`，系统返回内置 Mock 结果，适合答辩演示
- 填写真实 `apiKey` 并启用后，可调用真实大模型接口
- 已增加敏感词限制和长度限制，用于保障响应安全可控

## 推荐演示流程

### 管理员演示

1. 登录 `edu_admin`
2. 查看首页统计看板
3. 进入 AI 日志页面查看调用记录
4. 查看课程、报名和问答数据

### 教师演示

1. 登录 `edu_teacher`
2. 进入课程中心发布或编辑课程
3. 点击 AI 通知/AI 建议生成教学内容
4. 进入报名管理维护学习记录

### 家长演示

1. 登录 `edu_parent`
2. 查看学生档案
3. 到课程中心为孩子报名
4. 查看报名记录与学习总结

### 学生演示

1. 登录 `edu_student`
2. 查看开放课程
3. 提交作业问题
4. 查看 AI 返回的解题思路与建议

## 项目亮点

- 结合真实教育场景设计四类角色协同流程
- 基于若依权限体系快速完成菜单、角色、权限控制
- 集成大模型能力，提升系统智能化水平
- 提供演示首页与演示数据，适合毕业设计答辩
- 支持后续继续扩展通知公告、成绩分析、成长档案等模块

## 文档补充

详细补充说明可查看：

```text
doc/edu-after-school.md
```

## 开源说明

本项目基于若依框架进行二次开发，原框架版权归原作者所有；本仓库中的教育业务模块、演示数据与说明文档为本课题扩展内容。
