package com.cl.mvvm.base;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.ViewDataBinding;


public interface IBaseView<DB extends ViewDataBinding, VM extends BaseViewModel> {

    /**
     * 获取页面的DataBinding
     *
     * @return DataBinding
     */
    DB getBinding(LayoutInflater inflater, ViewGroup container);

    /**
     * 获取页面的ViewModel
     *
     * @return ViewModel
     */
    VM getViewModel();

    /**
     * 获取页面的ViewModelId
     *
     * @return xml
     */
    int getViewModelId();

    /**
     * 绑定数据
     */
    void bindData();

    /**
     * 绑定事件
     */
    void bindEvent();

}
