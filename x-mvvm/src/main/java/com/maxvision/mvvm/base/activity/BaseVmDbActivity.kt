package com.maxvision.mvvm.base.activity

import android.view.View
import androidx.databinding.ViewDataBinding
import com.maxvision.mvvm.ext.inflateBindingWithGeneric

/**
 * 作者　: cl
 * 时间　: 2023/04/12
 * 描述　: V3版本去掉了BaseViewModel继承，使用hilt
 * 需要使用Databind的清继承它
 */
abstract class BaseVmDbActivity<DB : ViewDataBinding> : BaseVmActivity() {

    override fun layoutId() = 0

    lateinit var mDatabind: DB

    /**
     * 创建DataBinding
     */
    override fun initDataBind(): View? {
        mDatabind = inflateBindingWithGeneric(layoutInflater)
        return mDatabind.root
    }
}