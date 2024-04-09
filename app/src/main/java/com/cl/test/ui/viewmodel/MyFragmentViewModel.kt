package com.cl.test.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cl.test.bean.Data
import com.cl.test.net.ApiService
import com.maxvision.mvvm.base.viewmodel.BaseViewModel
import com.maxvision.mvvm.ext.download.DownLoadManager
import com.maxvision.mvvm.ext.download.OnDownLoadListener
import com.maxvision.mvvm.ext.request
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyFragmentViewModel @Inject constructor(
    private val apiService: ApiService
): BaseViewModel() {

    private var articleListData: MutableLiveData<Data> = MutableLiveData()

    /**
     * 网络请求
     */
    fun apiArticleListData() {
        request({ apiService.getEntryAndExitData()},{
            articleListData.value=it
        },{
            //失败
        },true)

    }

    /**
     * 获取数据
     */
    fun getArticleListData(): MutableLiveData<Data> {
        return articleListData
    }


    /**
     * 下载演示
     */
    suspend fun downloadPictures(){
        DownLoadManager.downLoad("TAG", "", "", "", reDownload = false, whetherHttps = false,
            object : OnDownLoadListener {
                override fun onDownLoadPrepare(key: String) {
                    TODO("Not yet implemented")
                }

                override fun onDownLoadError(key: String, throwable: Throwable) {
                    TODO("Not yet implemented")
                }

                override fun onDownLoadSuccess(key: String, path: String, size: Long) {
                    TODO("Not yet implemented")
                }

                override fun onDownLoadPause(key: String) {
                    TODO("Not yet implemented")
                }

                override fun onUpdate(
                    key: String,
                    progress: Int,
                    read: Long,
                    count: Long,
                    done: Boolean
                ) {
                    TODO("Not yet implemented")
                }
            })
    }

}