package com.cl.test.bean

import com.google.gson.annotations.SerializedName

import com.maxvision.mvvm.network.BaseResponse

/**
 * 课程数据模型
 * 
 * 数据来源：https://www.wanandroid.com/chapter/547/sublist/json
 * 
 * @author cl
 * @since 2026-01-14
 */
data class CourseResponse(
    @SerializedName("data")
    val data: List<Course>,
    @SerializedName("errorCode")
    val errorCode: Int,
    @SerializedName("errorMsg")
    val errorMsg: String
) : BaseResponse<List<Course>>() {
    override fun isSucces() = errorCode == 0
    override fun getResponseCode() = errorCode
    override fun getResponseData() = data
    override fun getResponseMsg() = errorMsg
}

data class Course(
    @SerializedName("articleList")
    val articleList: List<Any>,
    @SerializedName("author")
    val author: String,
    @SerializedName("children")
    val children: List<Any>,
    @SerializedName("courseId")
    val courseId: Int,
    @SerializedName("cover")
    val cover: String,
    @SerializedName("desc")
    val desc: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("lisense")
    val lisense: String,
    @SerializedName("lisenseLink")
    val lisenseLink: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("order")
    val order: Int,
    @SerializedName("parentChapterId")
    val parentChapterId: Int,
    @SerializedName("type")
    val type: Int,
    @SerializedName("userControlSetTop")
    val userControlSetTop: Boolean,
    @SerializedName("visible")
    val visible: Int
) {
    /**
     * 是否可见
     */
    fun isVisible(): Boolean = visible == 1
    
    /**
     * 课程显示标题（带作者）
     */
    fun getDisplayTitle(): String = "$name - $author"
    
    /**
     * 获取简短描述（限制长度）
     */
    fun getShortDesc(maxLength: Int = 100): String {
        return if (desc.length > maxLength) {
            "${desc.substring(0, maxLength)}..."
        } else {
            desc
        }
    }
}
