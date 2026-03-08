# SmartBrowser Core Library (smtbcore)

[![](https://jitpack.io/v/your-username/smtbcore.svg)](https://jitpack.io/#your-username/smtbcore)

核心浏览器功能库，提供 WebView 增强、分析追踪、配置管理等功能。

## 功能特性

- 🌐 增强型 WebView 组件
- 📊 集成分析引擎（AppsFlyer、Adjust）
- 🔐 加密和安全功能
- 🎯 设备和区域检测
- 🔄 配置动态加载
- 📱 JavaScript Bridge 通信
- 🎨 UI 组件（浮动按钮、对话框等）

## 安装

### 方式 1：本地依赖（开发阶段）

在 `app/build.gradle` 中：

```gradle
dependencies {
    implementation project(':smtbcore')
}
```

### 方式 2：远程依赖（生产环境）

在项目根目录的 `settings.gradle` 中添加 JitPack 仓库：

```gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

在 `app/build.gradle` 中添加依赖：

```gradle
dependencies {
    implementation 'com.github.your-username:smtbcore:1.0.0'
}
```

## 基本使用

```java
public class MainActivity extends AppCompatActivity {
    
    private SmartWebView webView;
    private BrowserController controller;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 初始化控制器
        controller = new ViewModelProvider(this).get(BrowserController.class);
        webView = findViewById(R.id.webview);
        
        // 观察状态
        controller.state.observe(this, state -> 
            state.handle(
                this::onLoading,
                this::onSuccess,
                this::onError,
                this::onFallback
            )
        );
        
        // 启动
        controller.start();
    }
    
    private void onSuccess(BrowserState.NavigationData data) {
        webView.setup(
            this,
            data.configuration,
            data.url,
            mediaLauncher,
            url -> {
                // 页面加载完成
            }
        );
    }
}
```

## 核心组件

### SmartWebView
增强型 WebView 组件，支持文件选择、JavaScript Bridge、浮动按钮等。

### BrowserController
浏览器控制器，负责配置获取、状态管理、区域检测等。

### AnalyticsEngine
分析引擎，集成 AppsFlyer 和 Adjust。

## 版本历史

### 1.0.0 (2024-03-08)
- 初始版本
- 核心 WebView 功能
- 分析引擎集成
- 配置管理系统

## 依赖

- AndroidX AppCompat
- AndroidX Lifecycle
- Gson
- OkHttp
- Retrofit
- AppsFlyer SDK
- Adjust SDK

## 许可证

Apache License 2.0
