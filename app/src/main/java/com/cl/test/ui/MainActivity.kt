package com.cl.test.ui

import android.os.Bundle
import com.cl.test.base.BaseActivity
import com.cl.test.databinding.ActivityMainBinding
import com.cl.test.ui.adapter.MainTabAdapter
import com.cl.test.ui.fragment.CourseFragment
import com.cl.test.ui.fragment.HomeFragment
import com.cl.test.ui.fragment.ModernFragment
import com.cl.test.ui.fragment.MyFragment
import com.cl.test.ui.viewmodel.MainViewModel
import com.maxvision.mvvm.log.logI
import com.maxvision.mvvm.network.manager.NetState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel,ActivityMainBinding>() {

    private lateinit var fragmentAdapter: MainTabAdapter


    override fun initView(savedInstanceState: Bundle?) {
        fragmentAdapter = MainTabAdapter(this)
        fragmentAdapter?.addFragment(HomeFragment())
        fragmentAdapter?.addFragment(CourseFragment())
        fragmentAdapter?.addFragment(MyFragment())
        mDatabind.mainVp.adapter = fragmentAdapter
        mDatabind.mainVp.isUserInputEnabled = false
        mDatabind.mainVp.offscreenPageLimit = 3
        mDatabind.mainBnv.setOnItemSelectedListener {
            when (it.itemId) {
                com.cl.test.R.id.menu_home -> {
                    mDatabind.mainVp.setCurrentItem(0, false)
                }
                com.cl.test.R.id.menu_course -> {
                    mDatabind.mainVp.setCurrentItem(1, false)
                }
                com.cl.test.R.id.menu_mine -> {
                    mDatabind.mainVp.setCurrentItem(2, false)
                }
            }
            true
        }
    }

    override fun onNetworkStateChanged(netState: NetState) {
        super.onNetworkStateChanged(netState)
        logI("网络状态：${netState.isSuccess}")
    }

}