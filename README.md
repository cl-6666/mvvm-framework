# Jetpack MVVM框架
Android Jetpack MVVM框架开发，基于AndroidX开发，傻瓜式使用，适用于所有项目


## 版本更新历史：  
[![](https://jitpack.io/v/cl-6666/mvvm-framework.svg)](https://jitpack.io/#cl-6666/mvvm-framework)

- v2.1.0：(2023年06月04日)
  - 升级Jetpack库版本到2.6.1
  - 增加可配置的网络显示日志库，支持多样配置
  - 代码优化

- v2.0.16：(2023年05月11日)
  - 增加下载模块适配https忽略
  - 增加网络httpLoggingInterceptor日志显示  

- v2.0.12：(2023年05月11日)
  - 增加网络缓存拦截器、公共heads、添加缓存拦截器等演示
  - 代码优化  

- v1.0.1：(2023年05月01日)
  - 上线第一个版本，满足日常项目开发 
  - 框架内部使用kotlin代码编写  
  - 代码优化  


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
  implementation 'com.github.cl-6666:mvvm-framework:v2.1.0'
}
```  

## 使用介绍  
### 网络请求相关使用介绍
#### 网络日志使用介绍

------  
  api功能支持列表 | 是否支持 |
--------|------|
支持http request、response的数据格式化的输出 | 支持  |
当请求为Post时，支持Form表单的打印 | 支持  |
支持超长日志的打印，解决了 Logcat 4K 字符截断的问题 | 支持  |
支持格式化时去掉竖线边框显示日志。方便将网络请求复制到Postman之类的工具 | 支持 |
支持日志级别 | 支持  |
支持显示当前的线程名称 | 支持  |
支持排除一些接口的日志显示 | 支持  |
------  

* 效果图
<img src="https://github.com/cl-6666/mvvm-framework/blob/master/img/wired.png" alt="演示"/>  
<img src="https://github.com/cl-6666/mvvm-framework/blob/master/img/wireless.png" alt="演示"/>  
<img src="https://github.com/cl-6666/mvvm-framework/blob/master/img/default.png" alt="演示"/>  

* 自定义网络日志
``` kotlin
//框架内部默认实现方法
object AndroidLoggingInterceptor {
    @JvmOverloads
    @JvmStatic
    fun build(isDebug:Boolean = true, hideVerticalLine:Boolean = false, requestTag:String = "Request", responseTag:String = "Response"): LoggingInterceptor {
        init()
        return if (hideVerticalLine) {
            LoggingInterceptor.Builder()
                    .loggable(isDebug) // TODO: 发布到生产环境需要改成false
                    .androidPlatform()
                    .request()
                    .requestTag(requestTag)
                    .response()
                    .responseTag(responseTag)
                    .hideVerticalLine()// 隐藏竖线边框
                    .build()
        } else {
            LoggingInterceptor.Builder()
                    .loggable(isDebug) // TODO: 发布到生产环境需要改成false
                    .androidPlatform()
                    .request()
                    .requestTag(requestTag)
                    .response()
                    .responseTag(responseTag)
//                    .hideVerticalLine()// 隐藏竖线边框
                    .build()
        }
    }
}

```

``` kotlin
     //普通网络日志显示写法
        val httpLoggingInterceptor = HttpLoggingInterceptor { message ->
            Log.e(
                "网络日志", message
            )
        }
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        //框架内部封装日志显示写法 hideVerticalLine = true代表隐藏横线
        val loggingInterceptor = AndroidLoggingInterceptor.build(hideVerticalLine = true)
	//需要显示日志带横线
	val loggingInterceptor = AndroidLoggingInterceptor.build()

``` 

#### 网络配置相关
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
            /** 演示日志拦截器 您也可以自定义网络日志 */
            addInterceptor(loggingInterceptor)
            /** 超时时间 连接、读、写 */
            connectTimeout(10, TimeUnit.SECONDS)
            readTimeout(5, TimeUnit.SECONDS)
            writeTimeout(5, TimeUnit.SECONDS)
        }
        return builder
    }
```  
* 网络下载模块介绍
``` kotlin
             //生成apk名字时间戳
            val apkName = "inspection" + System.currentTimeMillis() + ".apk"
            downLoad(
                "TAG",
                url,
                FileUtil.getInstance().pluginRootPath,
                apkName,
                true,
		true,  //是否开启忽略https   true-开启   默认不开启
                object : OnDownLoadListener {
                    override fun onDownLoadPrepare(key: String) {
                        Log.i("TAG", "准备下载")
                    }

                    override fun onDownLoadError(
                        key: String,
                        throwable: Throwable
                    ) {
                        Log.i("TAG", "下载失败")
                    }

                    override fun onDownLoadSuccess(
                        key: String,
                        path: String,
                        size: Long
                    ) {
                        Log.i("TAG", "下载成功"+apkName)
                    }

                    override fun onDownLoadPause(key: String) {
                        Log.i("TAG", "下载暂停")
                    }

                    override fun onUpdate(
                        key: String,
                        progress: Int,
                        read: Long,
                        count: Long,
                        done: Boolean
                    ) {
                        Log.i("TAG", "下载中")
                    }
                })

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

* 网络返回格式  
``` kotlin
{
    "data": ...,
    "errorCode": 0,
    "errorMsg": ""
}
```  
该示例格式是 [玩Android Api](https://www.wanandroid.com/blog/show/2)返回的数据格式，如果errorCode等于0 请求成功，否则请求失败
作为开发者的角度来说，我们主要是想得到脱壳数据-data，且不想每次都判断errorCode==0请求是否成功或失败
这时我们可以在服务器返回数据基类中继承BaseResponse，实现相关方法：  

``` kotlin
data class ApiResponse<T>(var errorCode: Int, var errorMsg: String, var data: T) : BaseResponse<T>() {
    // 这里是示例，wanandroid 网站返回的 错误码为 0 就代表请求成功，请你根据自己的业务需求来编写
    override fun isSucces() = errorCode == 0
    override fun getResponseCode() = errorCode
    override fun getResponseData() = data
    override fun getResponseMsg() = errorMsg
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
