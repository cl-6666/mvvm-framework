package com.maxvision.mvvm.ext.download

import androidx.lifecycle.MutableLiveData
import com.maxvision.mvvm.ext.download.DownloadResultState
import com.maxvision.mvvm.ext.download.OnDownLoadListener

/**
 * 作者　: cl
 * 时间　: 2023/04/12
 *
 */

fun downLoadExt(downloadResultState: MutableLiveData<DownloadResultState>): OnDownLoadListener {
    return object : OnDownLoadListener {
        override fun onDownLoadPrepare(key: String) {
            //开始下载
            downloadResultState.postValue(DownloadResultState.onPending())
        }

        override fun onDownLoadError(key: String, throwable: Throwable) {
            //下载错误
            downloadResultState.postValue(DownloadResultState.onError(throwable.message ?: "下载错误"))
        }

        override fun onDownLoadSuccess(key: String, path: String, size: Long) {
            //下载成功
            downloadResultState.postValue(DownloadResultState.onSuccess(path, size))
        }

        override fun onDownLoadPause(key: String) {
            //下载暂停
            downloadResultState.postValue(DownloadResultState.onPause())
        }

        override fun onUpdate(key: String, progress: Int, read: Long, count: Long, done: Boolean) {
            //正在下载
            downloadResultState.postValue(DownloadResultState.onProgress(read, count, progress))
        }
    }
}


