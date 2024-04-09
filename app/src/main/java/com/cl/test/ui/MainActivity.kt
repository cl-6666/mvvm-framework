package com.cl.test.ui

import android.os.Bundle
import com.cl.test.base.BaseActivity
import com.cl.test.databinding.ActivityMainBinding
import com.cl.test.ui.adapter.MainTabAdapter
import com.cl.test.ui.fragment.HomeFragment
import com.cl.test.ui.fragment.MyFragment
import com.cl.test.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel,ActivityMainBinding>() {

    private lateinit var fragmentAdapter: MainTabAdapter


    override fun initView(savedInstanceState: Bundle?) {
        fragmentAdapter = MainTabAdapter(this)
        fragmentAdapter?.addFragment(HomeFragment())
        fragmentAdapter?.addFragment(MyFragment())
        mDatabind.mainVp.adapter = fragmentAdapter
        mDatabind.mainVp.isUserInputEnabled = false
        mDatabind.mainBnv.setOnItemSelectedListener {
            when (it.itemId) {
                com.cl.test.R.id.menu_home -> {
                    mDatabind.mainVp.setCurrentItem(0, false)
                }
                com.cl.test.R.id.menu_mine -> {
                    mDatabind.mainVp.setCurrentItem(1, false)
                }
            }
            true
        }
    }

}