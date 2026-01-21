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
import com.maxvision.mvvm.log.logD
import com.maxvision.mvvm.log.logE
import com.maxvision.mvvm.log.logI
import com.maxvision.mvvm.log.logJson
import com.maxvision.mvvm.log.logW
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

        // ==================== 日志演示 ====================
        
        // 1. 基础用法 (自动使用类名 MainActivity 作为 TAG)
        logD("这是一条 Debug 日志")
        logI("这是一条 Info 日志")
        logW("这是一条 Warn 日志")
        logE("这是一条 Error 日志")

        // 2. 自定义 TAG 用法 (用户指定 TAG)
        logI("TAGDsdsdsd", "擦擦手牌")
        logD("CustomTAG", "Debug with custom tag")
        logE("ErrorTAG", "Error with custom tag")

        // 3. 格式化日志 (自动 TAG)
        logI("User info: name=%s, age=%d", "ZhangSan", 25)
        logD("System status: %b", true)

        // 4. 格式化日志 + 自定义 TAG
        logI("FormatTAG", "Score: %.2f", 98.5)
        logE("ApiError", "Code: %d, Msg: %s", 404, "Not Found")

        // 5. 异常日志
//        try {
//            val a = 1 / 0
//        } catch (e: Exception) {
//            logE("ArithmeticException", "Calculation failed", e) // 自定义 TAG + 异常
//            logW(e.toString()) // 仅异常 (自动 TAG)
//            logE("Something went wrong", e) // 消息 + 异常 (自动 TAG)
//        }

        // 6. JSON 日志
        val jsonStr = """
            {
                "code": 200,
                "message": "success",
                "data": {
                    "id": 1001,
                    "name": "Android MVVM"
                }
            }
        """.trimIndent()
        logJson("ApiJson", jsonStr) // 自定义 TAG
        logJson(jsonStr) // 自动 TAG

    }

    override fun onNetworkStateChanged(netState: NetState) {
        super.onNetworkStateChanged(netState)
        logI("网络状态：${netState.isSuccess}")
    }

}