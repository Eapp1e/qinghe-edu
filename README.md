# 青禾智学课后服务平台

**QINGHE After-school Service Platform v1.0**

青禾智学课后服务平台是一套面向中小学课后服务场景的前后端分离系统，覆盖课程发布、兴趣班报名、学生档案、学习记录、作业问答、平台公告、网课资源与 AI 辅助能力。系统支持学生、家长、教师、管理员四类角色，适合毕业设计展示、课后服务信息化原型演示与后续二次开发。

## 核心功能

- 平台首页：展示平台动态、近期课程、热门选课排行和活跃趋势图表。
- 课程中心：教师发布和维护课后课程，学生与家长查看并报名，管理员统一查看课程运行情况。
- 学生档案：维护学生基础信息、家长关联、年级班级、兴趣标签，并支持 AI 课程推荐。
- 学习记录：查看报名状态、课程归属、教师安排与学习情况，家长可补充学习记录。
- 作业问答：学生提交课后问题，AI 结合课程场景生成解答，教师和管理员可查看与重新解答。
- AI 中心：查看模型选择、近期 AI 使用记录、调用状态和返回内容，按角色展示不同 AI 能力入口。
- 平台公告：支持公告发布、查看、已读记录和管理维护。
- 网课中心：整合官方平台、科普站点和优质免费学习资源，支持 AI 网课推荐。
- 平台用户：管理员统一维护学生、家长、教师和管理员账号。

## 技术栈

后端：
- Spring Boot 4
- Spring Security
- MyBatis
- PageHelper
- JWT
- Druid
- Fastjson2
- MySQL
- Maven

前端：
- Vue 2
- Vue Router
- Vuex
- Element UI
- ECharts
- Axios
- Sass
- SortableJS / vuedraggable

AI 能力：
- SiliconFlow 大模型 API
- AI 问答、课程推荐、通知生成、教学建议、网课资源推荐
- AI 调用日志与安全过滤

## 项目结构

```text
eapple-admin        后端启动模块，包含控制器、配置和应用启动类
eapple-common       通用工具、基础常量、公共封装和异常处理
eapple-framework    安全认证、权限控制、过滤器和系统基础框架
eapple-system       平台业务核心模块
eapple-ui           Vue 前端项目
sql                 数据库初始化、升级和演示数据脚本
doc                 项目文档与补充说明
runtime             本地运行产生的临时文件目录
```

## 数据库脚本

`sql` 目录包含基础表结构、平台业务表、升级脚本和演示数据。常用导入顺序：

1. `sql/qinghe_system_base.sql`
2. `sql/edu_after_school.sql`
3. `sql/edu_platform_bootstrap.sql`
4. `sql/edu_platform_upgrade.sql`
5. `sql/edu_demo_data.sql`
6. 根据展示需要继续导入扩展演示脚本

说明：`qinghe_system_base.sql` 是系统基础权限与菜单表脚本，当前仍作为基础表结构来源保留。

默认数据库名为 `qinghe_edu`，如需连接已有数据库，可通过环境变量 `MYSQL_DATABASE` 覆盖。

## 运行环境

- JDK 17+
- Maven 3.8+
- Node.js 16+
- MySQL 8.x

建议统一使用 UTF-8 编码，并将 Maven 运行 JDK 指向 JDK 17 或更高版本。

## 后端启动

在项目根目录执行：

```powershell
mvn -U clean compile -DskipTests
```

启动类：

```text
eapple-admin/src/main/java/com/eapple/EduPlatformApplication.java
```

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

## AI 配置

AI 配置位于：

```text
eapple-admin/src/main/resources/application.yml
```

主要配置项：

- `edu.ai.enabled`
- `edu.ai.endpoint`
- `edu.ai.apiKey`
- `edu.ai.model`
- `edu.ai.timeoutSeconds`
- `edu.ai.maxPromptLength`

API Key 建议通过环境变量配置：

```text
SILICONFLOW_API_KEY
```

不要把真实 API Key 提交到 Git 仓库。

## 演示角色

- 管理员：查看全局数据、平台用户、公告、AI 日志和统计报表。
- 教师：发布课程、管理报名、处理作业问答、生成通知和教学建议。
- 家长：为学生报名课程、查看学习记录、查看平台公告和 AI 推荐。
- 学生：查看课程、提交作业问题、查看 AI 解答、使用网课中心和 AI 推荐。

## 仓库地址

- GitHub: [https://github.com/Eapp1e/qinghe-edu](https://github.com/Eapp1e/qinghe-edu)
