package com.cl.test

import android.os.Bundle
import com.cl.mvvm.base.MvvmActivity
import com.cl.mvvm.jetpack.databinding.ui.page.DataBindingConfig

class MainActivity : MvvmActivity() {


    override fun getDataBindingConfig(): DataBindingConfig {
        TODO("Not yet implemented")
    }


    override fun initViewModel() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}