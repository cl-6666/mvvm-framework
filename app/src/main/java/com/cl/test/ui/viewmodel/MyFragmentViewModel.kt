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

    @Inject
    lateinit var downLoadManager: DownLoadManager

}