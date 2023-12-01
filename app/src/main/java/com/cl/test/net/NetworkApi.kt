//package com.cl.test.net
//
//import android.util.Log
//import com.cl.test.Constants.Companion.BASE_URL
//import com.franmontiel.persistentcookiejar.PersistentCookieJar
//import com.franmontiel.persistentcookiejar.cache.SetCookieCache
//import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
//import com.google.gson.GsonBuilder
//import com.maxvision.mvvm.base.appContext
//import com.maxvision.mvvm.network.BaseNetworkApi
//import com.maxvision.mvvm.network.interceptor.CacheInterceptor
//import com.maxvision.mvvm.network.log.AndroidLoggingInterceptor
//import okhttp3.Cache
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import java.io.File
//import java.util.concurrent.TimeUnit
//
//
///**
// * 作者　: cl
// * 描述　: 网络请求构建器，继承BasenetworkApi 并实现setHttpClientBuilder/setRetrofitBuilder方法，
// * 在这里可以添加拦截器，设置构造器可以对Builder做任意操作
// */
//
////双重校验锁式-单例 封装NetApiService 方便直接快速调用简单的接口
//val apiService: ApiService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
//    //type 为false 表示不使用htpps忽略证书验证
//    NetworkApi.INSTANCE.getApi(ApiService::class.java, BASE_URL, false)
//}
//
//open class NetworkApi : BaseNetworkApi() {
//
//    companion object {
//        val INSTANCE: NetworkApi by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
//            NetworkApi()
//        }
//    }
//
//    /**
//     * 实现重写父类的setHttpClientBuilder方法，
//     * 在这里可以添加拦截器，可以对 OkHttpClient.Builder 做任意操作
//     */
//    override fun setHttpClientBuilder(builder: OkHttpClient.Builder): OkHttpClient.Builder {
//
//
//        //普通网络日志显示写法
//        val httpLoggingInterceptor = HttpLoggingInterceptor { message ->
//            Log.e(
//                "网络日志", message
//            )
//        }
//        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//
//        //框架内部封装日志显示写法 hideVerticalLine = true代表隐藏横线
//        val loggingInterceptor = AndroidLoggingInterceptor.build(hideVerticalLine = true)
//
//        builder.apply {
//            /** 设置缓存配置 缓存最大10M */
//            cache(Cache(File(appContext.cacheDir, "cxk_cache"), 10 * 1024 * 1024))
//            /** 添加Cookies自动持久化 */
//            cookieJar(cookieJar)
//            /** 演示添加缓存拦截器 可传入缓存天数，不传默认7天 */
//            addInterceptor(CacheInterceptor())
//            /** 演示添加公共heads 注意要设置在日志拦截器之前，不然Log中会不显示head信息 */
//            addInterceptor(MyHeadInterceptor())
//            /** 演示添加缓存拦截器 可传入缓存天数，不传默认7天 */
//            addInterceptor(CacheInterceptor())
//            /** 演示token过期拦截器演示 */
//            addInterceptor(TokenOutInterceptor())
//            /** 演示日志拦截器 */
//            addInterceptor(loggingInterceptor)
//            /** 超时时间 连接、读、写 */
//            connectTimeout(10, TimeUnit.SECONDS)
//            readTimeout(5, TimeUnit.SECONDS)
//            writeTimeout(5, TimeUnit.SECONDS)
//        }
//        return builder
//    }
//
//    /**
//     * 实现重写父类的setRetrofitBuilder方法，
//     * 在这里可以对Retrofit.Builder做任意操作，比如添加GSON解析器，protobuf等
//     */
//    override fun setRetrofitBuilder(builder: Retrofit.Builder): Retrofit.Builder {
//        return builder.apply {
//            addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
//        }
//    }
//
//    val cookieJar: PersistentCookieJar by lazy {
//        PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(appContext))
//    }
//
//}
//
//
//
