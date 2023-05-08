package com.maxvision.mvvm.base.activity

import android.view.View
import androidx.viewbinding.ViewBinding
import com.maxvision.mvvm.base.viewmodel.BaseViewModel
import com.maxvision.mvvm.ext.inflateBindingWithGeneric

/**
 * 作者　: cl
 * 时间　: 2023/04/12
 * 描述　: 包含 ViewModel 和 ViewBinding ViewModelActivity基类，把ViewModel 和 ViewBinding 注入进来了
 * 需要使用 ViewBinding 的清继承它
 */
abstract class BaseVmVbActivity<VM : BaseViewModel, VB : ViewBinding> : BaseVmActivity<VM>() {

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