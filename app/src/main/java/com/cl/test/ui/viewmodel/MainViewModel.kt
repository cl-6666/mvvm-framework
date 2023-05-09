package com.cl.test.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cl.test.bean.Data
import com.cl.test.net.apiService
import com.maxvision.mvvm.base.viewmodel.BaseViewModel
import com.maxvision.mvvm.ext.request


class MainViewModel : BaseViewModel() {


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


}