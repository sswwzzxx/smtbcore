#!/bin/bash

# smtbcore 发布脚本

set -e

echo "========================================="
echo "smtbcore 发布工具"
echo "========================================="
echo ""

# 检查是否在 smtbcore 目录
if [ ! -f "build.gradle" ]; then
    echo "❌ 错误：请在 smtbcore 目录下运行此脚本"
    exit 1
fi

# 检查是否有未提交的更改
if [[ -n $(git status -s 2>/dev/null) ]]; then
    echo "⚠️  警告：有未提交的更改"
    git status -s
    echo ""
    read -p "是否继续？(y/n) " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# 获取当前版本
echo "请输入新版本号（例如：1.0.1）："
read VERSION

if [[ -z "$VERSION" ]]; then
    echo "❌ 版本号不能为空"
    exit 1
fi

echo ""
echo "准备发布版本: v$VERSION"
echo ""

# 更新版本号
echo "📝 更新版本号..."
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    sed -i '' "s/versionName \".*\"/versionName \"$VERSION\"/" build.gradle
else
    # Linux
    sed -i "s/versionName \".*\"/versionName \"$VERSION\"/" build.gradle
fi

# 提交更改
echo "💾 提交更改..."
git add .
git commit -m "Release version $VERSION"

# 创建标签
echo "🏷️  创建标签..."
git tag -a "v$VERSION" -m "Release version $VERSION"

# 推送到远程
echo "🚀 推送到 GitHub..."
read -p "是否推送到远程仓库？(y/n) " -n 1 -r
echo ""
if [[ $REPLY =~ ^[Yy]$ ]]; then
    git push origin main
    git push origin "v$VERSION"
    echo ""
    echo "✅ 发布成功！"
    echo ""
    echo "下一步："
    echo "1. 访问 GitHub 创建 Release: https://github.com/your-username/smtbcore/releases/new"
    echo "2. 选择标签: v$VERSION"
    echo "3. 填写 Release 说明"
    echo "4. 发布 Release"
    echo ""
    echo "5. 访问 JitPack 构建: https://jitpack.io/#your-username/smtbcore"
    echo "6. 点击 'Get it' 开始构建"
    echo ""
    echo "7. 在 app/build.gradle 中更新依赖："
    echo "   implementation 'com.github.your-username:smtbcore:$VERSION'"
else
    echo ""
    echo "⏸️  已取消推送"
    echo ""
    echo "手动推送命令："
    echo "  git push origin main"
    echo "  git push origin v$VERSION"
fi

echo ""
echo "========================================="
