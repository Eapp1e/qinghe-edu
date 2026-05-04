# 青禾智学课后服务平台 v1.2 开发环境搭建手册

本文档用于说明 QINGHE After-school Service Platform v1.2 的本地开发环境、数据库初始化、前后端启动、AI 私有配置和常见问题处理。项目功能介绍以根目录 `README.md` 为准，本文档聚焦开发与运行配置。

## 1. 环境要求

后端环境：

- JDK 17 或更高版本
- Maven 3.8.x 或更高版本
- MySQL 8.x
- IDEA 2023+ 或其他支持 Maven 多模块项目的 IDE

前端环境：

- Node.js 16.x
- npm 8.x 左右版本
- Chrome / Edge 最新稳定版

编码要求：

- IDEA File Encoding 统一设置为 UTF-8
- Maven 运行 JDK 指向 JDK 17
- PowerShell 或终端建议使用 UTF-8 输出，避免中文日志显示异常

## 2. Maven 与 JDK 配置

在命令行检查 Maven 当前使用的 JDK：

```powershell
mvn -v
```

正确结果应包含类似内容：

```text
Java version: 17
```

如果仍显示 JDK 1.8，需要将系统环境变量 `JAVA_HOME` 指向 JDK 17，例如：

```text
D:\java\jdk\jdk17
```

IDEA 中同步检查：

- `File > Project Structure > Project SDK` 选择 JDK 17
- `Settings > Build Tools > Maven > Runner` 的 JRE 选择 JDK 17
- Maven home 选择本地 Maven 3.8.x 或更高版本

## 3. 数据库初始化

默认数据库名：

```text
qinghe_edu
```

后端连接配置位于：

```text
qinghe-admin/src/main/resources/application-druid.yml
```

默认 JDBC URL 已带 `createDatabaseIfNotExist=true`，如果 MySQL 账号有建库权限，启动时会自动创建 `qinghe_edu`。首次运行仍需要导入表结构和演示数据。

推荐按以下顺序执行 SQL：

```text
sql/qinghe_system_base.sql
sql/edu_after_school.sql
sql/edu_platform_bootstrap.sql
sql/edu_platform_upgrade.sql
sql/edu_demo_data.sql
sql/edu_demo_course_expansion.sql
sql/edu_demo_family_expansion.sql
sql/edu_demo_question_expansion.sql
sql/edu_notice_refresh.sql
```

如果本机已有旧库，不建议继续使用旧库名。更规范的做法是新建或迁移到 `qinghe_edu`，这样项目配置、文档和展示名称保持一致。

## 4. 后端启动

在项目根目录执行编译：

```powershell
mvn clean compile -DskipTests
```

后端启动类：

```text
qinghe-admin/src/main/java/com/qinghe/EduPlatformApplication.java
```

启动成功后默认访问：

```text
http://localhost:8080/
```

如果启动时报 `Unknown database 'qinghe_edu'`：

- 确认 MySQL 已启动
- 确认数据库账号密码与本地配置一致
- 确认该账号有创建数据库权限
- 手动执行 `create database if not exists qinghe_edu default character set utf8mb4 collate utf8mb4_general_ci;`
- 重新导入上述 SQL 脚本

## 5. 前端启动

进入前端目录：

```powershell
cd qinghe-ui
```

安装依赖：

```powershell
npm install
```

启动开发服务：

```powershell
npm run dev
```

默认访问地址：

```text
http://localhost/
```

## 6. AI 私有配置

平台 AI 默认配置位于：

```text
qinghe-admin/src/main/resources/application.yml
```

项目支持按工作树维护本地私有配置文件：

```text
qinghe-admin/src/main/resources/application-private.yml
```

该文件已在 `.gitignore` 中忽略，适合为 `qinghe-edu` 单独配置 API Key，避免写入 Windows 用户级环境变量，也避免提交到 Gitee 或 GitHub。示例：

```yaml
edu:
  ai:
    apiKey: sk-xxxx
```

当前 AI 中心提供三个代表性模型：

- `deepseek-ai/DeepSeek-V3.2`：默认模型，综合生成质量和通用问答能力较好。
- `Qwen/Qwen2.5-72B-Instruct`：适合稳定中文问答、教学建议和内容生成。
- `THUDM/GLM-4-32B-0414`：适合作为通用对话与教育场景的补充模型。

提交前建议检查真实密钥未进入仓库：

```powershell
rg -n "sk-[A-Za-z0-9]{20,}" . -g "!**/target/**" -g "!**/dist/**" -g "!**/node_modules/**" -g "!qinghe-admin/src/main/resources/application-private.yml"
git check-ignore -v qinghe-admin/src/main/resources/application-private.yml
```

## 7. 常见问题

### 后端提示无效目标发行版 17

原因是 Maven 或 IDEA 实际运行的 JDK 仍是 1.8。将 `JAVA_HOME`、IDEA Project SDK 和 Maven Runner JRE 都切换为 JDK 17 后重新导入 Maven 项目。

### 前端依赖安装慢

可以临时切换 npm 镜像：

```powershell
npm config set registry https://registry.npmmirror.com
```

### 中文显示乱码

优先检查文件是否为 UTF-8 编码；如果文件正常但 PowerShell 显示乱码，可使用：

```powershell
chcp 65001
```

### 数据库表不存在

通常是 SQL 导入顺序不完整。先导入 `qinghe_system_base.sql`，再导入教育业务表、升级脚本和演示数据脚本。

## 8. 建议开发流程

1. 启动 MySQL。
2. 导入数据库脚本。
3. 使用 JDK 17 执行 `mvn clean compile -DskipTests`。
4. 启动 `EduPlatformApplication`。
5. 进入 `qinghe-ui` 执行 `npm run dev`。
6. 浏览器访问 `http://localhost/`。
