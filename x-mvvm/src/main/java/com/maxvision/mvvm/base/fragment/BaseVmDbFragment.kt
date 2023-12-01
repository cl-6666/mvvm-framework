package com.maxvision.mvvm.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.maxvision.mvvm.ext.inflateBindingWithGeneric

/**
 * 作者　: cl
 * 时间　: 2023/04/12
 * 描述　: V3版本去掉了BaseViewModel继承,ViewModelFragment基类
 * 需要使用Databind的清继承它
 */
abstract class BaseVmDbFragment<DB : ViewDataBinding> : BaseVmFragment() {

    override fun layoutId() = 0

    //该类绑定的ViewDataBinding
    private var _binding: DB? = null
    val mDatabind: DB get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding  = inflateBindingWithGeneric(inflater,container,false)
        return mDatabind.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}