package com.cl.test.net

import android.util.Log
import com.cl.test.Constants
import com.google.gson.GsonBuilder
import com.maxvision.mvvm.network.BaseNetworkApi
import com.maxvision.mvvm.network.log.AndroidLoggingInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * name：cl
 * date：2023/11/23
 * desc：
 */
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
            Log.i(
                "网络日志", message
            )
        }
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        //框架内部封装日志显示写法 hideVerticalLine = true代表隐藏横线
        val loggingInterceptor = AndroidLoggingInterceptor.build(hideVerticalLine = true)

        builder.apply {
            /** 设置缓存配置 缓存最大10M */
//            cache(Cache(File(app.cacheDir, "cxk_cache"), 10 * 1024 * 1024))
            /** 添加Cookies自动持久化 */
//            cookieJar(cookieJar)
            /** 演示添加缓存拦截器 可传入缓存天数，不传默认7天 */
//            addInterceptor(CacheInterceptor())
            /** 演示添加公共heads 注意要设置在日志拦截器之前，不然Log中会不显示head信息 */
            addInterceptor(MyHeadInterceptor())
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
        return NetworkModule.getApi(ApiService::class.java, Constants.BASE_URL, true)
    }


}