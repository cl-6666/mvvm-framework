# Jetpack MVVM框架
Android Jetpack MVVM框架开发，基于AndroidX开发，傻瓜式使用，适用于所有项目


## 版本更新历史：  
[![](https://jitpack.io/v/cl-6666/mvvm-framework.svg)](https://jitpack.io/#cl-6666/mvvm-framework)
- v2.0.12：(2023年05月11日)
  - 增加网络缓存拦截器、公共heads、添加缓存拦截器等演示
  - 代码优化  

- v2.0.11：(2023年05月01日)
  - 框架内部使用kotlin代码编写  
  - 代码优化  

- v1.0.0：(2021年4月15日)
  - 上线第一个版本，满足日常项目开发  

## 效果图
<img src="https://github.com/cl-6666/mvvm-framework/blob/master/img/img.png" alt="演示"/>  

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
      implementation 'com.github.cl-6666:mvvm-framework:v2.0.12'
}
```  

## 使用介绍  

### 网络相关使用介绍
* 是否需要打开https忽略证书模式
``` kotlin
//双重校验锁式-单例 封装NetApiService 方便直接快速调用简单的接口
val apiService: ApiService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    //type 为false 表示不使用htpps忽略证书验证
    NetworkApi.INSTANCE.getApi(ApiService::class.java, BASE_URL, false)
}
```
* 网络拦截器相关配置介绍
``` kotlin
   /**
     * 实现重写父类的setHttpClientBuilder方法，
     * 在这里可以添加拦截器，可以对 OkHttpClient.Builder 做任意操作
     */
    override fun setHttpClientBuilder(builder: OkHttpClient.Builder): OkHttpClient.Builder {
        builder.apply {
            /** 设置缓存配置 缓存最大10M */
            cache(Cache(File(appContext.cacheDir, "cxk_cache"), 10 * 1024 * 1024))
            /** 添加Cookies自动持久化 */
            cookieJar(cookieJar)
            /** 演示添加缓存拦截器 可传入缓存天数，不传默认7天 */
            addInterceptor(CacheInterceptor())
            /** 演示添加公共heads 注意要设置在日志拦截器之前，不然Log中会不显示head信息 */
            addInterceptor(MyHeadInterceptor())
            /** 演示添加缓存拦截器 可传入缓存天数，不传默认7天 */
            addInterceptor(CacheInterceptor())
            addInterceptor(TokenOutInterceptor())
            /** 演示日志拦截器 */
            addInterceptor(LogInterceptor())
            /** 超时时间 连接、读、写 */
            connectTimeout(10, TimeUnit.SECONDS)
            readTimeout(5, TimeUnit.SECONDS)
            writeTimeout(5, TimeUnit.SECONDS)
        }
        return builder
    }
```  

* api接口定义
``` kotlin
interface ApiService {

    /**
     * 首页文章列表
     */
    @GET("article/list/0/json")
    suspend fun getEntryAndExitData(): ApiResponse<Data>

}
```  

### 谷歌Jetpack框架使用介绍  

* 在你的项目，需要在 `build.gradle` 文件中加入
``` gradle
 buildFeatures {
        viewBinding = true
        dataBinding = true
    }
```  

* Activity请求玩安卓api案例

``` kotlin
  class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    private val mArticleListAdapter: ArticleListAdapter by lazy { ArticleListAdapter(arrayListOf()) }

    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.apiArticleListData()
        initRv()
    }

    private fun initRv() {
        mDatabind.rvArticleList.init(LinearLayoutManager(this), mArticleListAdapter, false)

        mViewModel.getArticleListData().observe(this) {
            mArticleListAdapter.submitList(it.datas)
            mArticleListAdapter.notifyDataSetChanged()
        }
    }
}
```
* ViewModel简单使用介绍如下 

``` kotlin
class MainViewModel : BaseViewModel() {

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
## 常见问题
* 当gradle-7.0以上，部分设备glide加载不出图片解决方法  
``` xml
    <application
        ........
        android:requestLegacyExternalStorage="true"
        ........
	>
```
