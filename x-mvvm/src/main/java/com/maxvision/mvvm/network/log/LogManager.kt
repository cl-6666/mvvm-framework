package com.maxvision.mvvm.network.log

/**
 * name：cl
 * date：2023/6/5
 * desc：
 */
object LogManager {

    private var logProxy: LogProxy? = null

    fun logProxy(logProxy: LogProxy) {
        LogManager.logProxy = logProxy
    }

    fun e(tag:String , msg:String) {
        logProxy?.e(tag,msg)
    }

    fun w(tag:String , msg:String) {
        logProxy?.w(tag,msg)
    }

    fun i(tag:String , msg:String) {
        logProxy?.i(tag,msg)
    }

    fun d(tag:String , msg:String) {
        logProxy?.d(tag,msg)
    }
}