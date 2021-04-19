package com.cl.test.mian

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.setContentView
import com.cl.mvvm.base.BaseMvvmActivity
import com.cl.mvvm.utils.NetworkUtils
import com.cl.test.BR
import com.cl.test.R
import com.cl.test.databinding.ActivityMainBinding


class MainActivity : BaseMvvmActivity<ActivityMainBinding, MainViewModel>() {


    override fun getBinding(inflater: LayoutInflater?, container: ViewGroup?): ActivityMainBinding {
        return setContentView(this, R.layout.activity_main)
    }

    override fun getViewModel(): MainViewModel {
        return getActivityViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun getViewModelId(): Int {
        return BR.viewModel
    }

    override fun bindData() {


    }

    override fun bindEvent() {

        mBinding?.btnDianj?.setOnClickListener {
            Log.i("TAG","点击了。。。。。")
        }
    }
}