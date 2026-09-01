# TaskMate

![TaskMate logo](app/src/main/app_logo-playstore.png)

TaskMate 是一款基于 Android 的轻量任务管理与日常工具应用。用户可以注册并登录本地账号，创建和管理任务，设置闹钟，也可以使用手电筒和前置摄像头镜像功能。

## 功能

- 用户注册、登录、退出登录
- 本地修改密码和账号信息查看
- 创建、编辑、删除任务
- 为任务设置截止日期和进度状态：`Not Started`、`In Progress`、`Finished`
- 通过复选框快速完成任务
- 任务列表支持左右滑动：左滑编辑，右滑删除
- 创建和删除闹钟，并在触发时播放系统铃声、震动和显示通知
- 手电筒开关
- 前置摄像头镜像预览

## 技术栈

- Java
- Android SDK 34
- AndroidX AppCompat、ConstraintLayout、Material Components
- Room Database：本地保存用户、任务和闹钟数据
- ViewModel、LiveData：观察任务数据变化
- RecyclerView：展示任务和闹钟列表
- CameraX：实现前置摄像头预览
- Gradle Wrapper 8.7

## 环境要求

- Android Studio
- JDK 17
- Android SDK 34
- Android 设备或模拟器，最低支持 Android 7.0（API 24）

## 开始使用

### 使用 Android Studio

1. 克隆仓库并使用 Android Studio 打开项目根目录。
2. 等待 Gradle 同步完成。
3. 连接 Android 设备或启动模拟器。
4. 点击 **Run**，运行 `app` 模块。

### 使用命令行

在项目根目录执行：

```bash
# macOS / Linux
chmod +x gradlew
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug
```

构建成功后，Debug APK 位于：

```text
app/build/outputs/apk/debug/app-debug.apk
```

运行单元测试：

```bash
./gradlew test
```

## 使用流程

1. 首次打开应用后进入注册页面，使用用户名和密码创建本地账号。
2. 返回登录页面并登录。
3. 在主页面点击右下角的 `+` 创建任务。
4. 点击任务复选框可将任务标记为已完成。
5. 左滑任务可以编辑，右滑任务可以删除。
6. 打开侧边栏可以进入账号、闹钟、手电筒、镜像和退出登录功能。

## 项目结构

```text
TaskMate/
├── app/
│   └── src/main/
│       ├── java/com/example/TaskMate/
│       │   ├── Adapter/       # RecyclerView 适配器
│       │   ├── Model/         # 用户、任务和闹钟实体
│       │   ├── Repository/    # 任务数据访问封装
│       │   ├── ViewModel/     # ViewModel 和设备工具页面
│       │   └── database/      # Room Database 和 DAO
│       └── res/               # 布局、颜色、图标和主题资源
├── build.gradle              # 根项目构建配置
├── settings.gradle           # 模块配置
├── gradlew                   # Gradle Wrapper（macOS / Linux）
└── gradlew.bat               # Gradle Wrapper（Windows）
```

## 权限说明

应用会根据功能申请以下权限：

- 摄像头：镜像功能
- 闪光灯：手电筒功能
- 精确闹钟、通知和震动：闹钟提醒功能
- 网络状态和网络访问：当前 Manifest 中声明，应用核心数据仍保存在本地

使用镜像、手电筒或闹钟功能时，请在系统提示出现后授予相应权限。Android 12 及以上版本可能还需要在系统设置中允许精确闹钟。

## 当前实现说明

TaskMate 目前是本地演示型应用，适合学习 Android Activity、Room、LiveData 和设备能力调用。使用前请注意：

- 用户名和密码保存在本地 Room 数据库中，当前未接入服务器，也未进行密码加密；请勿使用真实账号密码。
- 闹钟页面中的当前用户 ID 和日期生成逻辑仍是示例实现，正式使用前需要改为读取实际登录用户，并完善日期处理。
- 闹钟、通知和摄像头功能依赖具体设备及 Android 系统版本，部分模拟器可能无法完整支持。

## 推送到 GitHub

先在 GitHub 创建一个空仓库，然后在项目根目录执行：

```bash
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/<你的用户名>/<仓库名>.git
git push -u origin main
```

项目中的 `local.properties`、构建产物和 IDE 临时文件已配置为忽略，不应提交到 GitHub。推送前建议检查：

```bash
git status
```

## License

当前项目尚未声明开源许可证。如果准备长期公开或接受外部贡献，建议根据项目用途补充合适的 License，例如 MIT License。
