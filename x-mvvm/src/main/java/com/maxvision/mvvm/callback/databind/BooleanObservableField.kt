package com.maxvision.mvvm.callback.databind

import androidx.databinding.ObservableField

/**
 * 作者　: cl
 * 时间　: 2023/04/12
 * 描述　: 自定义的Boolean类型ObservableField 提供了默认值，避免取值的时候还要判空
 */
class BooleanObservableField(value: Boolean = false) : ObservableField<Boolean>(value) {
    override fun get(): Boolean {
        return super.get()!!
    }

}