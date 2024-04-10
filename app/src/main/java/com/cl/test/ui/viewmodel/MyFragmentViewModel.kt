package com.cl.test.ui.viewmodel

import com.cl.test.net.apiService
import com.maxvision.mvvm.base.viewmodel.BaseViewModel
import com.maxvision.mvvm.ext.request
import com.maxvision.mvvm.ext.requestNoCheck

/**
 * name：cl
 * date：2024/4/10
 * desc：类备注
 */
class MyFragmentViewModel: BaseViewModel() {

    fun testData() {
        //1.拿到已脱壳的数据（项目有基类的可以用）
        request({ apiService.getEntryAndExitData() }, {
            //请求成功 已自动处理了 请求结果是否正常
        }, {
            //请求失败 网络异常，或者请求结果码错误都会回调在这里
        }, true, "正在登录中...")

        //2.拿到未脱壳的数据，你可以自己根据code做业务需求操作（项目没有基类或者不想框架帮忙脱壳的可以用）
        requestNoCheck({ apiService.getEntryAndExitData() }, {
            //请求成功 自己拿到数据做业务需求操作
            if (it.errorCode == 0) {
                //结果正确
            } else {
                //结果错误
            }
        }, {
            //请求失败 网络异常回调在这里
        }, true, "正在登录中...")
    }
}