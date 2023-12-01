package com.maxvision.mvvm.base.activity

import android.view.View
import androidx.viewbinding.ViewBinding
import com.maxvision.mvvm.ext.inflateBindingWithGeneric

/**
 * 作者　: cl
 * 时间　: 2023/04/12
 * 描述　: V3版本去掉了BaseViewModel继承，使用hilt
 * 需要使用 ViewBinding 的清继承它
 */
abstract class BaseVmVbActivity<VB : ViewBinding> : BaseVmActivity() {

    override fun layoutId(): Int = 0

    lateinit var mViewBind: VB

    /**
     * 创建DataBinding
     */
    override fun initDataBind(): View? {
        mViewBind = inflateBindingWithGeneric(layoutInflater)
        return mViewBind.root

    }
}