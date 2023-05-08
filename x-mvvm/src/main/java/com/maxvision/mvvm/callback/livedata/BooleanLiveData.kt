package com.maxvision.mvvm.callback.livedata

import androidx.lifecycle.MutableLiveData


/**
 * 作者　: cl
 * 时间　: 2023/04/12
 * 描述　:自定义的Boolean类型 MutableLiveData 提供了默认值，避免取值的时候还要判空
 */
class BooleanLiveData : MutableLiveData<Boolean>() {

    override fun getValue(): Boolean {
        return super.getValue() ?: false
    }
}

