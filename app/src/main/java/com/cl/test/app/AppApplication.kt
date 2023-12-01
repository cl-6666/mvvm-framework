package com.cl.test.app

import android.app.Application
import com.hjq.toast.Toaster
import com.maxvision.mvvm.base.BaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppApplication : BaseApp() {

    override fun onCreate() {
        super.onCreate()

        // 初始化 Toast 框架
        Toaster.init(this)
    }

    companion object {
        private val sInstance: Application? = null
    }
}