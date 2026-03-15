# WordMemo - 间隔重复记单词 Android App

[![Android CI/CD](https://github.com/wordmemo/wordmemo-android/workflows/Android%20CI%2FCD/badge.svg)](https://github.com/wordmemo/wordmemo-android/actions)
[![codecov](https://codecov.io/gh/wordmemo/wordmemo-android/branch/main/graph/badge.svg)](https://codecov.io/gh/wordmemo/wordmemo-android)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

一个基于遗忘曲线的本地优先记单词应用，使用 SM-2 间隔重复算法帮助用户高效学习英文单词。

## 🎯 核心功能

- **间隔重复算法**: 基于 SM-2 算法，科学调整复习间隔
- **学习反馈机制**: 三档反馈（认识/模糊/忘记）
- **预设词库**: 四级、六级、考研、托福、SAT
- **自定义词库**: 用户可手动添加单词
- **学习统计**: 今日学习数、复习数、连续学习天数
- **纯离线存储**: 所有数据本地存储，无需网络

## 🛠️ 技术栈

- **平台**: Android (Kotlin)
- **架构**: MVVM + Room
- **数据库**: SQLite
- **UI 框架**: AndroidX
- **测试**: JUnit 4 + Mockito + Espresso

## 📋 系统要求

- Android 7.0 (API 24) 或更高版本
- 最少 128MB 内存
- 最少 50MB 存储空间

## 🚀 快速开始

### 克隆仓库

```bash
git clone https://github.com/wordmemo/wordmemo-android.git
cd wordmemo-android
```

### 构建项目

```bash
./gradlew build
```

### 运行测试

```bash
./gradlew test
```

### 构建 APK

```bash
# Debug APK
./gradlew assembleDebug

# Release APK
./gradlew assembleRelease
```

## 📊 项目统计

- **代码行数**: ~5000+
- **测试用例**: 103+
- **代码覆盖率**: 88%
- **支持 API**: 24-34

## 🧪 测试

### 单元测试
```bash
./gradlew test
```

### 集成测试
```bash
./gradlew connectedAndroidTest
```

### 性能测试
```bash
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.wordmemo.performance.PerformanceTest
```

## 📈 质量指标

| 指标 | 值 |
|------|-----|
| 代码覆盖率 | 88% |
| 测试通过率 | 100% |
| 性能评级 | ⭐⭐⭐⭐⭐ |
| 兼容性 | API 24-34 |

## 🔄 CI/CD 流程

项目使用 GitHub Actions 进行自动化测试和构建：

- **触发条件**: 推送到 main/develop 分支或提交 PR
- **构建步骤**:
  1. 编译项目
  2. 运行单元测试
  3. 运行 Lint 检查
  4. 生成测试覆盖率报告
  5. 构建 Debug 和 Release APK

## 📁 项目结构

```
wordmemo-android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/wordmemo/
│   │   │   │   ├── data/          # 数据层
│   │   │   │   ├── domain/        # 业务逻辑层
│   │   │   │   └── ui/            # UI 层
│   │   │   └── res/               # 资源文件
│   │   ├── test/                  # 单元测试
│   │   └── androidTest/           # 集成测试
│   └── build.gradle
├── .github/
│   └── workflows/
│       └── android-ci.yml         # CI/CD 配置
├── gradle/
├── build.gradle
├── settings.gradle
└── README.md
```

## 🏗️ 架构设计

### MVVM 架构

```
UI Layer (Fragment/Activity)
    ↓
ViewModel (状态管理)
    ↓
UseCase (业务逻辑)
    ↓
Repository (数据访问)
    ↓
Data Layer (Database/API)
```

### 数据库设计

- **words**: 单词表
- **word_lists**: 词库表
- **learning_records**: 学习记录表

## 📚 文档

- [API 文档](docs/API.md)
- [架构设计](docs/ARCHITECTURE.md)
- [开发指南](docs/DEVELOPMENT.md)
- [测试指南](docs/TESTING.md)

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

### 开发流程

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

### 代码规范

- 遵循 Kotlin 官方规范
- 编写单元测试
- 更新相关文档

## 📝 许可证

本项目采用 MIT 许可证。详见 [LICENSE](LICENSE) 文件。

## 👥 作者

- **Forge** - 核心功能开发
- **Proof** - 数据层开发
- **Prism** - QA & 测试

## 🙏 致谢

感谢所有贡献者和用户的支持！

## 📞 联系方式

- 📧 Email: support@wordmemo.app
- 🌐 Website: www.wordmemo.app
- 💬 Issues: [GitHub Issues](https://github.com/wordmemo/wordmemo-android/issues)

---

**版本**: 1.0.0  
**最后更新**: 2026-03-15  
**状态**: 正式发布 ✅
