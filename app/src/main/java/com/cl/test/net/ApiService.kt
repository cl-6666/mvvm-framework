package com.cl.test.net

import com.cl.test.bean.Course
import com.cl.test.bean.CourseResponse
import com.cl.test.bean.Data
import retrofit2.http.*

/**
 * 网络 API 接口定义
 * 
 * @author cl
 * @since 3.2.0
 */
interface ApiService {

    /**
     * 首页文章列表（原有接口保留）
     */
    @GET("article/list/0/json")
    suspend fun getEntryAndExitData(): ApiResponse<Data>
    
    // ==================== 课程相关接口 ====================
    
    /**
     * 获取课程列表
     * 
     * 接口地址：https://www.wanandroid.com/chapter/547/sublist/json
     * 
     * @return 课程列表
     */
    @GET("chapter/547/sublist/json")
    suspend fun getCourseList(): CourseResponse
    
    // ==================== 用户相关接口（演示） ====================

}