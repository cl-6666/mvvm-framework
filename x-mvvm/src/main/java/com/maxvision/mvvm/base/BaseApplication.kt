package com.maxvision.mvvm.base

import android.app.Application
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.lifecycle.ProcessLifecycleOwner
import com.maxvision.mvvm.ext.lifecycle.KtxAppLifeObserver
import com.maxvision.mvvm.ext.lifecycle.KtxLifeCycleCallBack
import com.maxvision.mvvm.network.manager.NetworkStateReceive

/**
 * mvvm-framework 框架基于 Google 官方的 JetPack 构建，在使用 mvvm-framework 时，需遵循一些规范：
 *
 * 你需要参照如下方式添加 @HiltAndroidApp 注解
 *
 * ```
 * // 示例
 * @HiltAndroidApp
 * class MyApplication : BaseApplication() {
 *
 * }
 * ```
 * PS：如果由于某种原因，导致你不能继承[BaseApplication]；你也可以在你自定义的Application的onCreate函
 * 数中通过调用[BaseApplication.initAppConfig]来进行初始化
 *
 */
open class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initAppConfig(this)
    }

    companion object {
        lateinit var app: Application
        private var mNetworkStateReceive: NetworkStateReceive? = null
        private var watchActivityLife = true
        private var watchAppLife = true

        /**
         * 初始化App配置
         */
        @JvmStatic
        fun initAppConfig(application: Application) {
            // 初始化Toaster
            install(application)
        }
        private fun install(application: Application) {
            app = application
            mNetworkStateReceive = NetworkStateReceive()
            app.registerReceiver(
                mNetworkStateReceive,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
            if (watchActivityLife) application.registerActivityLifecycleCallbacks(
                KtxLifeCycleCallBack()
            )
            if (watchAppLife) ProcessLifecycleOwner.get().lifecycle.addObserver(KtxAppLifeObserver)
        }
    }
}
