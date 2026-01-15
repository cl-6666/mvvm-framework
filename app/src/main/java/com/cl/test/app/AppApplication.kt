package com.cl.test.app

import android.app.Application
import com.cl.test.BuildConfig
import com.cl.test.util.ImageLoadingUtils
import com.hjq.toast.Toaster
import com.maxvision.mvvm.base.BaseApplication
import com.maxvision.mvvm.log.AliWrapperLog
import dagger.hilt.android.HiltAndroidApp

/**
 * Application 类
 * 
 * @author cl
 * @since 3.2.0
 */
@HiltAndroidApp
class AppApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        
        // 初始化日志系统（必须最先初始化）
        initLog()
        
        // 初始化 Toast 框架
        Toaster.init(this)
        AliWrapperLog.d("APP", "Toast 框架初始化完成")
        
        // 初始化图片加载
        ImageLoadingUtils.init(this)
        AliWrapperLog.d("APP", "图片加载框架初始化完成")
        
        AliWrapperLog.i("APP", "Application 初始化完成")
    }
    
    /**
     * 初始化日志系统
     */
    private fun initLog() {
        AliWrapperLog.init(
            /* application = */ this,
            /* enableLog = */ BuildConfig.DEBUG,  // Debug 模式才启用文件日志
            /* customTag = */ "MVVM",             // 自定义 TAG
            /* folderPath = */ null,              // 使用默认路径
            /* retentionDays = */ 7               // 日志保留 7 天
        )
    }

    companion object {
        private val sInstance: Application? = null
    }
}