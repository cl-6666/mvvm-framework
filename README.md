# Jetpack MVVM框架
一个灵活、高效的Android Jetpack MVVM开发框架，基于AndroidX开发，傻瓜式使用，适用于所有项目，强烈建议以依赖的方式导入，这样避免代码同步，有问题或建议？请通过博客、qq群联系我们。  

**谷歌 Android 团队 Jetpack 视图模型：**
<img src="https://github.com/cl-6666/mvvm-framework/blob/master/img/img2.png" alt="演示"/>  

**框架UI效果图如下:**  
<img src="https://github.com/cl-6666/mvvm-framework/blob/master/img/img.png" width="350" height="500" alt="演示"/>  

**框架版本更新说明:**  
新版3.x.x已发布 新增Jetpack hilt组件，不熟悉hilt组件的建议使用2.x.x版本  

# Document
- English Writing ...
- [2.0版本 中文](https://github.com/cl-6666/mvvm-framework/wiki/V2-%E6%99%AE%E9%80%9A%E7%89%88%E6%9C%AC%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E)
- [3.0版本 中文](https://github.com/cl-6666/mvvm-framework/wiki/V3-Hilt%E7%89%88%E6%9C%AC%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E)

## 🚀 最新特性 (v3.2.0+)

本框架已进行**现代化重构**，全面遵循 Google 推荐的最新架构指南：

*   **全面去 LiveData 化**：核心状态管理完全迁移至 `StateFlow` 和 `SharedFlow`，性能更优，功能更强。
*   **生命周期安全**：
    *   Loading 状态观察升级为 `Lifecycle.State.CREATED`，确保初始化期间的 Loading 也能正确显示。
    *   Fragment 懒加载改用 `lifecycleScope`，彻底消除 Handler 导致的内存泄漏隐患。
*   **内存安全**：优化了 Context 引用持有方式，修复了 `LoadingDialog` 等组件潜在的内存泄漏问题。
*   **Hilt 支持**：无缝集成 Hilt 依赖注入。

---

### 版本更新历史：  
[![](https://jitpack.io/v/cl-6666/mvvm-framework.svg)](https://jitpack.io/#cl-6666/mvvm-framework)

- v3.1.0：(2024年04月06日) hilt版本
  - 增加coil图片框架
  - 第三方库升级
  - hilt相关组件封装
  - 代码优化

- v2.1.2：(2024年04月06日) 普通版本
  - 增加coil图片框架
  - 第三方库升级
  - 代码优化


### QQ 群：458173716  
<img src="https://github.com/cl-6666/serialPort/blob/master/img/qq2.jpg" width="350" height="560" alt="演示"/>  

### 作者博客地址    
博客地址：https://blog.csdn.net/a214024475/article/details/130625856?spm=1001.2014.3001.5501 
