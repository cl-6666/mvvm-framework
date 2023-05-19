package com.cl.test.net
import com.cl.test.bean.Data
import retrofit2.http.*

/**
 * 作者　: cl
 * 时间　: 2023/04/14
 * 描述　: 网络API
 */
interface ApiService {

    /**
     * 首页文章列表
     */
    @GET("article/list/0/json")
    suspend fun getEntryAndExitData(): ApiResponse<Data>

}