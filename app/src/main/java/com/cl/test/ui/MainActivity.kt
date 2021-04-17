package com.cl.test.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.setContentView
import com.cl.mvvm.base.BaseMvvmActivity
import com.cl.test.BR
import com.cl.test.R
import com.cl.test.databinding.ActivityMainBinding
import com.cl.test.ui.model.MainModel

class MainActivity : BaseMvvmActivity<ActivityMainBinding, MainModel>() {


    override fun getBinding(inflater: LayoutInflater?, container: ViewGroup?): ActivityMainBinding {
        return setContentView(this, R.layout.activity_main)
    }

    override fun getViewModel(): MainModel {
        return getActivityViewModelProvider(this)[MainModel::class.java]
    }

    override fun getViewModelId(): Int {
        return BR.ViewModel
    }

    override fun bindData() {

    }

    override fun bindEvent() {

    }
}