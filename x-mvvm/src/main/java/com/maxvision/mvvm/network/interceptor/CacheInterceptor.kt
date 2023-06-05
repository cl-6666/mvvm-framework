package com.maxvision.mvvm.network.interceptor

import com.maxvision.mvvm.base.appContext
import com.maxvision.mvvm.util.NetworkUtil
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 作者　: cl
 * 时间　: 2023/04/12
 * 描述　: 缓存拦截器
 * @param day 缓存天数 默认7天
 */
class CacheInterceptor(var day: Int = 7) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (!NetworkUtil.isNetworkAvailable(appContext)) {
            request = request.newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()
        }
        val response = chain.proceed(request)
        if (!NetworkUtil.isNetworkAvailable(appContext)) {
            val maxAge = 60 * 60
            response.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, max-age=$maxAge")
                .build()
        } else {
            val maxStale = 60 * 60 * 24 * day // tolerate 4-weeks stale
            response.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                .build()
        }
        return response
    }
}