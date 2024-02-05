
## 版本更新历史：  
- v3.0.0：(2024年02月06日)
  - 增加Hilt组件支持
  - 代码优化

## 项目依赖

``` Gradle
allprojects {
   repositories {
	 maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency

``` Gradle
dependencies {
   implementation 'com.github.cl-6666:mvvm-framework:v3.0.0'
}
```
Step 3. Add hilt
``` Gradle
plugins {
    id 'com.google.dagger.hilt.android'
    id 'kotlin-kapt'
}

   //hilt官方地址：https://developer.android.google.cn/training/dependency-injection/hilt-android?hl=zh-cn
    def hilt_version = "2.48.1"
    api "com.google.dagger:hilt-android:$hilt_version"
    kapt "com.google.dagger:hilt-android-compiler:$hilt_version"
```

## 使用介绍

### 项目使用的三方库及其简单示例和资料

* [Kotlin](https://github.com/JetBrains/kotlin)
* Jetpack
  - [Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle): 观察 Android 生命周期并根据生命周期变化处理 UI 状态
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel): 管理与 UI 相关的数据持有者和生命周期感知。 允许数据在配置更改（例如屏幕旋转）中保存下来。
  - [ViewBinding](https://developer.android.com/topic/libraries/view-binding): 使用声明性格式而不是以编程方式将布局中的 UI 组件绑定到应用程序中的数据源。
  - [Room](https://developer.android.google.cn/jetpack/androidx/releases/room?hl=zh-cn): 通过在 SQLite 上提供抽象层来构建数据库，以允许流畅的数据库访问。
  - [Hilt](https://dagger.dev/hilt/): 用于依赖注入。
* [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
* [ksp](https://github.com/google/ksp): Kotlin 符号处理 API。
* [Glide](https://github.com/bumptech/glide), [GlidePalette](https://github.com/florent37/GlidePalette): 从网络加载图像。
* [Timber](https://github.com/JakeWharton/timber): 日志框架
* [OkHttp](https://github.com/square/okhttp)：网络请求
* [PersistentCookieJar](https://github.com/franmontiel/PersistentCookieJar)：持久CookieJar实现
* [logging-interceptor](https://github.com/square/okhttp)：网络日志
* [Retrofit](https://github.com/square/retrofit)：网络请求

### 网络请求相关使用介绍
```Kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule : BaseNetworkApi(){
    /**
     * 实现重写父类的setHttpClientBuilder方法，
     * 在这里可以添加拦截器，可以对 OkHttpClient.Builder 做任意操作
     */
    override fun setHttpClientBuilder(builder: OkHttpClient.Builder): OkHttpClient.Builder {
       //普通网络日志显示写法
        val httpLoggingInterceptor = HttpLoggingInterceptor { message ->
            Timber.i(
                "网络日志", message
            )
        }
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        //框架内部封装日志显示写法 hideVerticalLine = true代表隐藏横线
        val loggingInterceptor = AndroidLoggingInterceptor.build(hideVerticalLine = true)
        builder.apply {
            /** 设置缓存配置 缓存最大10M */
            cache(Cache(File(appContext.cacheDir, "cxk_cache"), 10 * 1024 * 1024))
            /** 添加Cookies自动持久化 */
//            cookieJar(cookieJar)
            /** 演示添加缓存拦截器 可传入缓存天数，不传默认7天 */
            addInterceptor(CacheInterceptor())
            /** 演示添加公共heads 注意要设置在日志拦截器之前，不然Log中会不显示head信息 */
            addInterceptor(MyHeadInterceptor())
            /** 演示添加缓存拦截器 可传入缓存天数，不传默认7天 */
            addInterceptor(CacheInterceptor())
            /** 演示token过期拦截器演示 */
            addInterceptor(TokenOutInterceptor())
            /** 演示日志拦截器 */
            addInterceptor(loggingInterceptor)
            /** 超时时间 连接、读、写 */
            connectTimeout(10, TimeUnit.SECONDS)
            readTimeout(5, TimeUnit.SECONDS)
            writeTimeout(5, TimeUnit.SECONDS)
        }
        return builder
    }
    /**
     * 实现重写父类的setRetrofitBuilder方法，
     * 在这里可以对Retrofit.Builder做任意操作，比如添加GSON解析器，protobuf等
     */
    override fun setRetrofitBuilder(builder: Retrofit.Builder): Retrofit.Builder {
        return builder.apply {
            addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        }
    }
    @Provides
    fun provideApiService(): ApiService {
        return NetworkModule.getApi(ApiService::class.java, Constants.BASE_URL, false)
    }
}
```
在ViewModel里面使用介绍

```Kotlin
@HiltViewModel
class MainViewModel @Inject constructor(
    private val apiService: ApiService
): BaseViewModel() {
    private var articleListData: MutableLiveData<Data> = MutableLiveData()
    /**
     * 网络请求
     */
    fun apiArticleListData() {
        request({ apiService.getEntryAndExitData()},{
            articleListData.value=it
        },{
            //失败
        },true)

    }
    /**
     * 获取数据
     */
    fun getArticleListData(): MutableLiveData<Data> {
        return articleListData
    }
}
```
HomeFragment相关代码

```Kotlin
@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(){

    private val mViewModel: MainViewModel by viewModels()

    private val mArticleListAdapter: ArticleListAdapter by lazy { ArticleListAdapter(arrayListOf()) }

    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.apiArticleListData()
        mDatabind.rvArticleList.init(LinearLayoutManager(activity), mArticleListAdapter, false)
        mViewModel.getArticleListData().observe(this) {
            mArticleListAdapter.submitList(it.datas)
        }

    }
}
```

## 常见问题
* 当gradle-7.0以上，部分设备glide加载不出图片解决方法  
``` xml
    <application
        ........
        android:requestLegacyExternalStorage="true"
        ........
	>
```
#### QQ 群：458173716  
<img src="https://github.com/cl-6666/serialPort/blob/master/qq2.jpg" width="350" height="560" alt="演示"/>  

## 作者博客地址    
博客地址：https://blog.csdn.net/a214024475/article/details/130625856?spm=1001.2014.3001.5501 
