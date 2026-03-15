#!/bin/bash
# 从 GitHub Actions 下载 APK 包
# 需要先安装 gh: brew install gh
# 并登录: gh auth login

RUN_ID="${1:-23104771890}"
OUTPUT_DIR="${2:-./ci-apks}"

mkdir -p "$OUTPUT_DIR"
cd "$OUTPUT_DIR"

echo "正在下载 run $RUN_ID 的 artifacts..."
gh run download "$RUN_ID" --repo casy-code/wordmemo-android

echo "完成。APK 已保存到 $OUTPUT_DIR"
