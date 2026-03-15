# Task 001: 创建 Android 项目骨架

**Project**: 间隔重复记单词 Android App  
**Task ID**: task-001  
**Assigned to**: Forge  
**Phase**: Phase 1 - 项目基础设置  
**Estimated Time**: 1 hour  
**Status**: assigned

---

## 📋 Task Description

创建一个新的 Android 项目，配置必要的依赖和项目结构，为后续的功能开发做准备。

## 🎯 Objectives

1. 创建新的 Android 项目（使用 Android Studio 或命令行）
2. 配置 Gradle 构建系统
3. 添加必要的依赖库
4. 创建基础项目结构
5. 确保项目可以编译和运行

## 📝 Detailed Steps

### Step 1: 创建 Android 项目

使用 Android Studio 创建新项目，或使用命令行：

```bash
# 使用 Android Studio 创建项目
# 或使用 gradle 命令行创建
```

**项目配置**:
- **App Name**: WordMemo
- **Package Name**: com.wordmemo
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **Language**: Kotlin

### Step 2: 配置 build.gradle

编辑 `build.gradle.kts` (Module: app)，添加以下依赖：

```kotlin
dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // Material Design 3
    implementation("com.google.android.material:material:1.11.0")
    
    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
```

### Step 3: 创建项目目录结构

在 `app/src/main/java/com/wordmemo/` 下创建以下目录结构：

```
com/wordmemo/
├── data/
│   ├── db/
│   │   ├── AppDatabase.kt
│   │   └── dao/
│   │       ├── WordDao.kt
│   │       ├── WordListDao.kt
│   │       └── LearningRecordDao.kt
│   ├── entity/
│   │   ├── Word.kt
│   │   ├── WordList.kt
│   │   ├── WordListItem.kt
│   │   └── LearningRecord.kt
│   └── repository/
│       └── WordRepository.kt
├── domain/
│   ├── model/
│   └── usecase/
├── ui/
│   ├── activity/
│   │   └── MainActivity.kt
│   ├── fragment/
│   │   ├── LearningFragment.kt
│   │   ├── ReviewFragment.kt
│   │   ├── StatisticsFragment.kt
│   │   └── SettingsFragment.kt
│   └── viewmodel/
│       └── LearningViewModel.kt
└── MainActivity.kt
```

### Step 4: 创建基础 MainActivity

创建 `MainActivity.kt`：

```kotlin
package com.wordmemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
    }
}
```

### Step 5: 创建基础布局文件

创建 `res/layout/activity_main.xml`：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <FrameLayout
        android:id="@+id/nav_host_fragment"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/nav_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="?attr/colorSurface"
        app:menu="@menu/bottom_nav_menu" />

</LinearLayout>
```

### Step 6: 验证编译

运行以下命令验证项目可以编译：

```bash
./gradlew build
```

预期输出：`BUILD SUCCESSFUL`

### Step 7: 运行项目

在模拟器或真机上运行项目：

```bash
./gradlew installDebug
```

或在 Android Studio 中点击 "Run" 按钮。

## 📦 Deliverables

- ✅ 可编译的 Android 项目
- ✅ 完整的 `build.gradle.kts` 配置
- ✅ 基础项目目录结构
- ✅ 基础 MainActivity 和布局文件
- ✅ 项目可以在模拟器/真机上运行

## ✅ Acceptance Criteria

- [ ] 项目可以编译成功（`./gradlew build` 返回 BUILD SUCCESSFUL）
- [ ] 项目可以在 Android 模拟器或真机上运行
- [ ] 所有依赖都正确配置
- [ ] 项目结构符合 MVVM 架构
- [ ] 代码没有编译错误或警告

## 📝 Notes

- 使用 Kotlin 作为主要编程语言
- 遵循 Android 官方最佳实践
- 使用 Material Design 3 设计规范
- 确保代码可读性和可维护性

## 🔗 References

- [Android Developer Guide](https://developer.android.com/)
- [Room Database Documentation](https://developer.android.com/training/data-storage/room)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Material Design 3](https://m3.material.io/)

---

**Status**: Ready to start  
**Last Updated**: 2026-03-14T22:40:00Z
