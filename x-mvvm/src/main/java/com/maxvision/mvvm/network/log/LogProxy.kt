package com.maxvision.mvvm.network.log

/**
 * name：cl
 * date：2023/6/5
 * desc：
 */
interface LogProxy {

    fun e(tag:String , msg:String)

    fun w(tag:String , msg:String)

    fun i(tag:String , msg:String)

    fun d(tag:String , msg:String)
}