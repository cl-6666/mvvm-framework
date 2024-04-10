package com.cl.test.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cl.test.bean.Data
import com.cl.test.net.ApiResponse
import com.cl.test.net.apiService
import com.maxvision.mvvm.base.viewmodel.BaseViewModel
import com.maxvision.mvvm.ext.download.DownLoadManager
import com.maxvision.mvvm.ext.download.OnDownLoadListener
import com.maxvision.mvvm.ext.request
import com.maxvision.mvvm.ext.requestNoCheck
import com.maxvision.mvvm.network.state.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainViewModel : BaseViewModel() {

    /**
     *自动脱壳过滤处理请求结果，自动判断结果是否成功
     */
    var articleListResult = MutableLiveData<ResultState<Data>>()

    /**
     * 不用框架帮脱壳
     */
    var articleListResult2 = MutableLiveData<ResultState<ApiResponse<Data>>>()

    /**
     * 网络请求
     */
    fun apiArticleListData() {
        //1.在 Activity/Fragment的监听回调中拿到已脱壳的数据（项目有基类的可以用）
        request(
            { apiService.getEntryAndExitData() }, //请求体
            articleListResult,//请求的结果接收者，请求成功与否都会改变该值，在Activity或fragment中监听回调结果，具体可看loginActivity中的回调
            true,//是否显示等待框，，默认false不显示 可以默认不传
            "数据请求中..."//等待框内容，可以默认不填请求网络中...
        )
        //2.在Activity/Fragment中的监听拿到未脱壳的数据，你可以自己根据code做业务需求操作（项目没有基类的可以用）
        requestNoCheck(
            { apiService.getEntryAndExitData() },
            articleListResult2,
            true,
            "数据请求中..."
        )
    }

    fun downloadPictureSSs() {
        viewModelScope.launch(Dispatchers.IO) {
            downloadPictures()
        }
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