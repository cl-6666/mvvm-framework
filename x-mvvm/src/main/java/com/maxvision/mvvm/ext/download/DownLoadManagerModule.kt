package com.maxvision.mvvm.ext.download

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * name：cl
 * date：2024/4/10
 * desc：注入下载
 */
@Module
@InstallIn(SingletonComponent::class)
object DownLoadManagerModule {
    @Singleton
    @Provides
    fun provideDownLoadManager(): DownLoadManager {
        return DownLoadManager()
    }

}