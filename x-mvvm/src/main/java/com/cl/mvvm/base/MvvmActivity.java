package com.cl.mvvm.base;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;

/**
 * 项目：My Application
 * 作者：Arry
 * 创建日期：4/15/21
 * 描述： 基类Activity
 * 修订历史：
 */
public abstract class MvvmActivity <DB extends ViewDataBinding, VM extends BaseViewModel> extends AppCompatActivity implements IBaseView<DB, VM> {


}
