#!/usr/bin/env bash
# 本地运行仪器化测试 (connectedDebugAndroidTest)
# 需要：模拟器已启动 或 真机已连接

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$PROJECT_DIR"

# 1. 设置 Android 环境
if [ -z "$ANDROID_HOME" ]; then
  # 常见路径
  for path in \
    "$HOME/Library/Android/sdk" \
    "/opt/homebrew/share/android-commandlinetools" \
    "/opt/homebrew/share/android-sdk" \
    "$HOME/Android/Sdk"
  do
    if [ -d "$path" ]; then
      export ANDROID_HOME="$path"
      break
    fi
  done
fi

if [ -z "$ANDROID_HOME" ]; then
  echo "错误: 未找到 Android SDK，请设置 ANDROID_HOME"
  echo "  例如: export ANDROID_HOME=\$HOME/Library/Android/sdk"
  echo "  或安装 Android Studio 后会自动配置"
  exit 1
fi

# 添加 platform-tools 到 PATH（adb）
export PATH="$ANDROID_HOME/platform-tools:$PATH"

# 2. 检查设备
echo "检查已连接设备..."
DEVICES=$(adb devices | grep -v "List" | grep "device$" | wc -l | tr -d ' ')
if [ "$DEVICES" -eq 0 ]; then
  echo ""
  echo "未检测到设备或模拟器。请先："
  echo "  方式一：启动 Android 模拟器"
  echo "    - 打开 Android Studio -> Device Manager -> 启动模拟器"
  echo "    - 或命令行: emulator -avd <AVD名称>"
  echo ""
  echo "  方式二：连接真机"
  echo "    - 开启 USB 调试"
  echo "    - 用数据线连接电脑"
  echo ""
  echo "  验证: adb devices"
  exit 1
fi

echo "已检测到 $DEVICES 个设备"
adb devices

# 3. 运行仪器化测试
echo ""
echo "运行仪器化测试..."
./gradlew connectedDebugAndroidTest --stacktrace

echo ""
echo "测试完成！报告位置: app/build/reports/androidTests/connected/index.html"
