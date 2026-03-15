# 贡献指南

感谢您对 WordMemo 项目的兴趣！本文档将指导您如何为项目做出贡献。

## 🚀 快速开始

### 1. Fork 项目

点击 GitHub 页面右上角的 "Fork" 按钮。

### 2. 克隆您的 Fork

```bash
git clone https://github.com/YOUR_USERNAME/wordmemo-android.git
cd wordmemo-android
```

### 3. 添加上游仓库

```bash
git remote add upstream https://github.com/wordmemo/wordmemo-android.git
```

### 4. 创建特性分支

```bash
git checkout -b feature/your-feature-name
```

### 5. 提交更改

```bash
git commit -m "Add your feature description"
```

### 6. 推送到您的 Fork

```bash
git push origin feature/your-feature-name
```

### 7. 创建 Pull Request

在 GitHub 上创建 Pull Request，描述您的更改。

## 📋 代码规范

### Kotlin 规范

- 遵循 [Kotlin 官方风格指南](https://kotlinlang.org/docs/coding-conventions.html)
- 使用 4 个空格缩进
- 最大行长 120 字符
- 使用有意义的变量名

### 命名规范

- 类名: PascalCase (例: `LearningManager`)
- 函数名: camelCase (例: `recordLearningFeedback`)
- 常量: UPPER_SNAKE_CASE (例: `MAX_EASE_FACTOR`)
- 私有成员: 前缀 `_` (例: `_learningRecords`)

### 代码注释

```kotlin
/**
 * 记录学习反馈
 * 
 * @param wordId 单词 ID
 * @param listId 词库 ID
 * @param quality 质量评分 (0-5)
 */
fun recordLearningFeedback(wordId: Int, listId: Int, quality: Int) {
    // 实现代码
}
```

## 🧪 测试要求

### 单元测试

- 为新功能编写单元测试
- 测试覆盖率 ≥ 80%
- 使用 JUnit 4 + Mockito

```kotlin
@Test
fun testNewFeature() {
    // Arrange
    val input = "test"
    
    // Act
    val result = newFeature(input)
    
    // Assert
    assertEquals("expected", result)
}
```

### 集成测试

- 为关键功能编写集成测试
- 使用 AndroidX Test + Espresso

```kotlin
@RunWith(AndroidJUnit4::class)
class NewFeatureIntegrationTest {
    @Test
    fun testNewFeatureIntegration() {
        // 测试代码
    }
}
```

## 📝 提交消息规范

使用清晰、简洁的提交消息：

```
feat: 添加新功能描述
fix: 修复 bug 描述
docs: 更新文档
test: 添加测试
refactor: 重构代码
style: 代码风格调整
chore: 构建或依赖更新
```

示例：
```
feat: 实现 SM-2 算法优化
fix: 修复学习记录查询 bug
docs: 更新 API 文档
test: 添加性能测试用例
```

## 🔍 Pull Request 检查清单

提交 PR 前，请确保：

- [ ] 代码遵循项目规范
- [ ] 添加了相应的测试
- [ ] 所有测试通过
- [ ] 更新了相关文档
- [ ] 提交消息清晰明确
- [ ] 没有合并冲突

## 🐛 报告 Bug

### 创建 Issue

1. 点击 "Issues" 标签
2. 点击 "New Issue"
3. 选择 "Bug report" 模板
4. 填写详细信息

### Bug 报告模板

```markdown
## 描述
清晰简洁地描述 bug。

## 复现步骤
1. 打开应用
2. 点击...
3. 看到错误

## 预期行为
应该发生什么

## 实际行为
实际发生了什么

## 环境
- Android 版本: 
- 设备型号: 
- 应用版本: 

## 日志
粘贴相关的错误日志
```

## 💡 功能建议

### 创建 Feature Request

1. 点击 "Issues" 标签
2. 点击 "New Issue"
3. 选择 "Feature request" 模板
4. 描述您的想法

### 功能建议模板

```markdown
## 描述
清晰简洁地描述您的想法。

## 为什么需要这个功能
解释为什么这个功能有用。

## 可能的实现方式
描述如何实现这个功能。

## 其他信息
任何其他相关信息。
```

## 📚 开发指南

### 设置开发环境

1. 安装 Android Studio
2. 克隆项目
3. 打开项目
4. 等待 Gradle 同步完成

### 构建和运行

```bash
# 构建项目
./gradlew build

# 运行测试
./gradlew test

# 在模拟器上运行
./gradlew installDebug
```

### 调试

- 使用 Android Studio 的调试器
- 设置断点
- 使用 Logcat 查看日志

## 🎓 学习资源

- [Android 官方文档](https://developer.android.com/)
- [Kotlin 官方文档](https://kotlinlang.org/docs/)
- [MVVM 架构指南](https://developer.android.com/jetpack/guide)
- [Room 数据库指南](https://developer.android.com/training/data-storage/room)

## 📞 获取帮助

- 查看 [Issues](https://github.com/wordmemo/wordmemo-android/issues)
- 查看 [Discussions](https://github.com/wordmemo/wordmemo-android/discussions)
- 发送邮件: support@wordmemo.app

## 🙏 致谢

感谢您的贡献！您的帮助使 WordMemo 变得更好。

---

**最后更新**: 2026-03-15
