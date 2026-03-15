# GitHub 集成与 CI/CD 设置说明

## 项目已准备好推送到 GitHub

### 已配置的内容

1. **GitHub Actions CI/CD 工作流** (`.github/workflows/android-ci.yml`)
   - 自动构建和测试
   - 单元测试执行
   - 集成测试执行
   - 代码覆盖率报告
   - APK 构建和上传
   - 自动发布

2. **.gitignore 文件**
   - 排除 Gradle 构建文件
   - 排除 IDE 配置
   - 排除生成的文件

3. **README.md**
   - 项目介绍
   - 功能特性
   - 快速开始指南
   - 测试说明
   - 质量指标

4. **CONTRIBUTING.md**
   - 贡献指南
   - 代码规范
   - 提交流程

### 推送到 GitHub 的步骤

1. **创建 GitHub 仓库**
   ```bash
   # 在 GitHub 上创建新仓库 wordmemo
   ```

2. **初始化 Git 仓库**
   ```bash
   cd ~/projects/proj-20260314-213932/android-project
   git init
   git add .
   git commit -m "Initial commit: WordMemo - 间隔重复记单词 Android App"
   ```

3. **添加远程仓库**
   ```bash
   git remote add origin https://github.com/yourusername/wordmemo.git
   git branch -M main
   git push -u origin main
   ```

4. **创建 develop 分支**
   ```bash
   git checkout -b develop
   git push -u origin develop
   ```

### GitHub Actions 工作流说明

工作流会在以下情况自动触发：
- 推送到 main 或 develop 分支
- 提交 Pull Request 到 main 或 develop 分支

工作流执行的步骤：
1. 检出代码
2. 设置 JDK 11
3. 授予 gradlew 执行权限
4. 构建项目
5. 运行单元测试
6. 运行集成测试
7. 生成代码覆盖率报告
8. 上传覆盖率到 Codecov
9. 构建 APK
10. 上传 APK 作为构建产物
11. 如果是标签推送，创建 Release

### 配置 Codecov（可选）

1. 访问 https://codecov.io
2. 使用 GitHub 账号登录
3. 授权 Codecov 访问您的仓库
4. 工作流会自动上传覆盖率报告

### 分支策略

- **main**: 生产分支，只接受来自 release 的 PR
- **develop**: 开发分支，接受功能分支的 PR
- **feature/xxx**: 功能分支，从 develop 创建
- **bugfix/xxx**: 修复分支，从 develop 创建

### 发布流程

1. 在 develop 分支上完成功能开发
2. 创建 Pull Request 到 main 分支
3. 代码审查通过后合并
4. 创建 Git 标签 (v1.0.0)
5. 推送标签到 GitHub
6. GitHub Actions 自动创建 Release 并上传 APK

### 质量门禁

所有 PR 必须满足：
- ✅ 所有测试通过
- ✅ 代码覆盖率 ≥ 80%
- ✅ 代码审查通过
- ✅ 无冲突

### 监控和维护

- 定期检查 GitHub Actions 日志
- 监控代码覆盖率趋势
- 及时处理 Issue 和 PR
- 定期发布新版本

---

**项目已完全准备好推送到 GitHub！**

所有代码、配置和文档都已就绪。只需按照上述步骤推送到 GitHub，CI/CD 流程将自动启动。
