package com.maxvision.mvvm.network.manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.maxvision.mvvm.util.NetworkUtil

/**
 * 作者　: cl
 * 时间　: 2023/04/12
 * 描述　: 网络变化接收器（现代化版本）
 * 
 * 优化内容：
 * 1. 使用 NetworkStateManager.postNetworkState 发送状态
 * 2. 简化逻辑，防止重复通知的工作交给 Manager
 * 3. 更清晰的代码结构
 */
class NetworkStateReceive : BroadcastReceiver() {
    
    /**
     * 是否是初始化（跳过第一次广播）
     */
    var isInit = true
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            if (!isInit) {
                // 检查网络是否可用
                val isNetworkAvailable = NetworkUtil.isNetworkAvailable(context)
                
                // 发送网络状态（Manager 会自动防止重复通知）
                NetworkStateManager.instance.postNetworkState(
                    NetState(isSuccess = isNetworkAvailable)
                )
            }
            isInit = false
        }
    }
}