package com.cl.test.ui.fragment

import android.os.Bundle
import com.cl.test.base.BaseFragment
import com.cl.test.databinding.FragmentMyBinding


class MyFragment :  BaseFragment<FragmentMyBinding>(){


    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.myWebView.loadUrl("https://github.com/cl-6666/mvvm-framework")
    }



}