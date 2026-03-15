# Android Studio 使用 JDK 17 修复 JdkImageTransform 报错

当使用 JDK 21 运行 Gradle 时，AGP 7.4.2 的 jlink 会报错：
```
Failed to transform core-for-system-modules.jar ...
Error while executing process .../jlink ...
```

## 解决步骤

### 方式一：在 Android Studio 中设置 Gradle JDK（推荐）

1. 打开 **Android Studio**
2. **Mac**: `Android Studio` → `Settings`（或 `Preferences`）
   **Windows/Linux**: `File` → `Settings`
3. 进入 **Build, Execution, Deployment** → **Build Tools** → **Gradle**
4. 在 **Gradle JDK** 下拉框中选择 **Java 17**
   - 若列表中没有 17，点击 **Download JDK**，选择版本 17（如 Temurin 17）下载
5. 点击 **Apply** → **OK**
6. 执行 **File** → **Invalidate Caches** → **Invalidate and Restart**（可选，若仍报错时尝试）

### 方式二：使用 gradle.properties（命令行构建）

项目已配置 `org.gradle.java.home` 指向 JDK 17。若本机路径不同，请修改 `gradle.properties`：

```properties
org.gradle.java.home=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
```

**注意**：Android Studio 的 Gradle JDK 设置会覆盖此配置，因此需在 IDE 中按方式一设置。

### 清除缓存（若仍报错）

```bash
./gradlew --stop
rm -rf ~/.gradle/caches/transforms-3
```

然后重新 Sync 并构建。
