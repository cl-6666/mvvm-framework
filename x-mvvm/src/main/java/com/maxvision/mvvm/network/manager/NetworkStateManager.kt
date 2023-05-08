package com.maxvision.mvvm.network.manager

import com.maxvision.mvvm.callback.livedata.event.EventLiveData

/**
 * 作者　: cl
 * 时间　: 2023/04/12
 * 描述　: 网络变化管理者
 */
class NetworkStateManager private constructor() {

    val mNetworkStateCallback = EventLiveData<NetState>()

    companion object {
        val instance: NetworkStateManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkStateManager()
        }
    }

}