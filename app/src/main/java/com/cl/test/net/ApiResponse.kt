package com.cl.test.net

import com.maxvision.mvvm.network.BaseResponse

/**
 * 作者　: cl
 * 时间　: 2019/12/23
 * 描述　:服务器返回数据的基类
 */
data class ApiResponse<T>(val errorCode: Int, val errorMsg: String, val data: T) : BaseResponse<T>() {

    override fun isSucces() = errorCode == 0

    override fun getResponseCode() = errorCode

    override fun getResponseData() = data

    override fun getResponseMsg() = errorMsg

}