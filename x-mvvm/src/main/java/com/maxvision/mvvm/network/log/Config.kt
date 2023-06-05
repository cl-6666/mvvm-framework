package com.maxvision.mvvm.network.log

import android.util.Log



/**
 * name：cl
 * date：2023/6/5
 * desc：在使用日志拦截器之前
 * 必须要先实现 LogProxy ，否则无法打印网络请求的 request 、response
 * 所以，先调用这个方法
 */
fun init() {

    LogManager.logProxy(object : LogProxy {
        override fun e(tag: String, msg: String) {
            Log.e(tag, msg)
        }

        override fun w(tag: String, msg: String) {
            Log.w(tag, msg)
        }

        override fun i(tag: String, msg: String) {
            Log.i(tag, msg)
        }

        override fun d(tag: String, msg: String) {
            Log.d(tag, msg)
        }
    })
}