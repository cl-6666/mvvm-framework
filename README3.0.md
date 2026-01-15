
## 版本更新历史：  
- v3.2.0：(最新)
  - **架构现代化**：全面迁移至 Kotlin Flow (StateFlow/SharedFlow)
  - **生命周期优化**：修复 Loading 显示时序，优化 Fragment 懒加载
  - **内存安全**：修复 LoadingDialog 等组件的内存泄漏
- v3.0.0：(2024年02月06日)
  - 增加Hilt组件支持
  - 代码优化

## 🚀 快速开始

本教程将引导您使用 **Hilt** + **Flow** + **MVVM** 快速搭建一个现代化 Android 应用。

### 第一步：添加依赖

1. 在项目根目录的 `build.gradle` (或 `settings.gradle`) 中配置 JitPack 仓库：
```gradle
allprojects {
   repositories {
	 maven { url 'https://jitpack.io' }
    }
}
```

2. 在 app 模块的 `build.gradle` 中添加依赖：
```gradle
plugins {
    id 'com.android.application'
    id 'kotlin-android'
    id 'kotlin-kapt' // 必须添加
    id 'com.google.dagger.hilt.android' // 必须添加
}

dependencies {
   // 1. MVVM 框架依赖 (请使用最新版本)
   implementation 'com.github.cl-6666:mvvm-framework:v3.2.0'
   
   // 2. Hilt 依赖注入 (推荐使用 2.44 或更高)
   implementation "com.google.dagger:hilt-android:2.44"
   kapt "com.google.dagger:hilt-android-compiler:2.44"
}
```

### 第二步：初始化 Hilt

1. 创建您的 Application 类，并添加 `@HiltAndroidApp` 注解：
```kotlin
@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 这里可以进行其他初始化
    }
}
```

2. 别忘了在 `AndroidManifest.xml` 中注册：
```xml
<application
    android:name=".MyApplication"
    ... >
```

3. 在您的 Activity/Fragment 上添加 `@AndroidEntryPoint` 注解：
```kotlin
@AndroidEntryPoint
class MainActivity : BaseVmActivity<MainViewModel>() { ... }
```

### 第三步：配置网络请求 (NetworkModule)

创建一个 Hilt 模块来提供全局的网络配置，例如 BaseURL、拦截器等。

```Kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule : BaseNetworkApi() {

    // 配置 OkHttp (添加拦截器、超时等)
    override fun setHttpClientBuilder(builder: OkHttpClient.Builder): OkHttpClient.Builder {
        return builder.apply {
            // 日志拦截器 (框架内置)
            addInterceptor(AndroidLoggingInterceptor.build(hideVerticalLine = true))
            // 超时设置
            connectTimeout(10, TimeUnit.SECONDS)
            readTimeout(10, TimeUnit.SECONDS)
        }
    }

    // 配置 Retrofit (添加 Converter 等)
    override fun setRetrofitBuilder(builder: Retrofit.Builder): Retrofit.Builder {
        return builder.addConverterFactory(GsonConverterFactory.create())
    }

    // 提供 ApiService 实例
    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return getApi(ApiService::class.java, "https://www.wanandroid.com/", false)
    }
}
```

### 第四步：编写业务代码 (ViewModel + UI)

#### 1. 定义 ViewModel

继承 `BaseViewModel`，使用 `MutableStateFlow` 管理状态，`UiState` 包装数据。

```Kotlin
@HiltViewModel
class MainViewModel @Inject constructor(
    private val apiService: ApiService
): BaseViewModel() {

    // 定义 UI 状态 (UiState 包含: loading, success, error, empty)
    private val _articleListState = MutableStateFlow<UiState<Data>>(UiState.idle())
    val articleListState = _articleListState.asStateFlow()

    /**
     * 发起网络请求
     */
    fun loadArticleList() {
        viewModelScope.launch {
            // simpleRequest: 框架提供的扩展函数
            // - isShowDialog: 是否显示 Loading 弹窗 (默认 true)
            // - loadingMessage: 弹窗文字
            simpleRequest(
                loadingMessage = "正在加载...",
                isShowDialog = true
            ) {
                apiService.getEntryAndExitData() // 挂起函数
            }
            .toUiState() // 自动转换为 UiState 流
            .collect { state ->
                _articleListState.value = state
            }
        }
    }
}
```

#### 2. 编写 Fragment

继承 `BaseFragment` (或 `BaseVmDbFragment` 支持 DataBinding)，使用 `collectSuccess` 接收数据。

```Kotlin
@AndroidEntryPoint
class HomeFragment : BaseFragment<MainViewModel, FragmentHomeBinding>() {

    private val mAdapter by lazy { ArticleListAdapter() }

    // 初始化视图
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.recyclerView.adapter = mAdapter
        
        // 触发请求
        mViewModel.loadArticleList()
    }
    
    // 注册观察者
    override fun createObserver() {
        // 使用 collectSuccess 扩展函数
        // 框架会自动处理 Loading 和 Error 状态，你只需要关注成功的数据
        mViewModel.articleListState.collectSuccess(this) { data ->
            // 请求成功且数据不为空时回调
            mAdapter.submitList(data.datas)
        }
        
        // 如果需要手动处理失败逻辑，可以这样写：
        /*
        mViewModel.articleListState.collectState(this) { state ->
            when(state) {
                is UiState.Success -> { ... }
                is UiState.Error -> { Toast.makeText(context, state.errorMsg, Toast.LENGTH_SHORT).show() }
                // Loading 由框架自动处理，通常不需要手动写
            }
        }
        */
    }
}
```

---

## 📚 进阶知识 (Hilt 组件生命周期)

了解 Hilt 组件的生命周期有助于您正确地管理依赖项的作用域。

|	生成的组件	|	创建时机	|	销毁时机|	说明 |
|	---		|	---		|	---		| --- |
|	SingletonComponent	|	Application#onCreate()	|	Application 已销毁 | 全局单例，如 NetworkModule |		
|	ActivityRetainedComponent |	Activity#onCreate()|	Activity#onDestroy() | 跨配置变化保持 (如屏幕旋转) |
|	ViewModelComponent |	ViewModel 已创建	|	ViewModel 已销毁| 注入到 ViewModel 的依赖 |	
|	ActivityComponent	|	Activity#onCreate()	|	Activity#onDestroy()| 注入到 Activity 的依赖 |	
|	FragmentComponent	|	Fragment#onAttach()	|	Fragment#onDestroy()| 注入到 Fragment 的依赖 |

---

## 🛠 常用三方库参考

* [Kotlin Flow](https://developer.android.com/kotlin/flow): 替代 LiveData 的现代化响应式流。
* [Hilt](https://dagger.dev/hilt/): Google 官方推荐的依赖注入库。
* [Retrofit](https://github.com/square/retrofit) + [OkHttp](https://github.com/square/okhttp): 网络请求黄金搭档。
* [Glide](https://github.com/bumptech/glide): 图片加载。

---

## 常见问题
* **Glide 加载不出图片**：Android 10+ 需要在 Manifest 中开启 `requestLegacyExternalStorage="true"`。
* **Hilt 编译报错**：请确保 `kapt` 插件已正确应用，且注解处理器版本与 Hilt 库版本一致。

#### QQ 群：458173716  
<img src="https://github.com/cl-6666/serialPort/blob/master/qq2.jpg" width="350" height="560" alt="QQ群"/>  

## 作者博客地址    
博客地址：https://blog.csdn.net/a214024475/article/details/130625856?spm=1001.2014.3001.5501 
