package com.maxvision.mvvm.network.log



/**
 * name：cl
 * date：2023/6/5
 * desc：
 */
object AndroidLoggingInterceptor {

    @JvmOverloads
    @JvmStatic
    fun build(isDebug:Boolean = true, hideVerticalLine:Boolean = false, requestTag:String = "Request", responseTag:String = "Response"): LoggingInterceptor {
        init()
        return if (hideVerticalLine) {
            LoggingInterceptor.Builder()
                    .loggable(isDebug) // TODO: 发布到生产环境需要改成false
                    .androidPlatform()
                    .request()
                    .requestTag(requestTag)
                    .response()
                    .responseTag(responseTag)
                    .hideVerticalLine()// 隐藏竖线边框
                    .build()
        } else {
            LoggingInterceptor.Builder()
                    .loggable(isDebug) // TODO: 发布到生产环境需要改成false
                    .androidPlatform()
                    .request()
                    .requestTag(requestTag)
                    .response()
                    .responseTag(responseTag)
//                    .hideVerticalLine()// 隐藏竖线边框
                    .build()
        }
    }
}
