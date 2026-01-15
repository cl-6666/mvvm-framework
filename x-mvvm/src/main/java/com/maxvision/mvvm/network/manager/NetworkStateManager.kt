package com.maxvision.mvvm.network.manager

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 作者　: cl
 * 时间　: 2023/04/12
 * 描述　: 网络变化管理者（现代化版本）
 * 
 * 优化内容：
 * 1. 使用 SharedFlow 替代 EventLiveData
 * 2. 线程安全的网络状态管理
 * 3. 防止重复通知
 * 4. 生命周期安全
 */
class NetworkStateManager private constructor() {

    /**
     * 网络状态 Flow（私有可变）
     */
    private val _networkStateFlow = MutableSharedFlow<NetState>(
        replay = 0,                    // 不保留历史值
        extraBufferCapacity = 1,       // 缓冲 1 个值
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
    )
    
    /**
     * 网络状态 Flow（公开不可变）
     * 
     * 使用示例：
     * ```kotlin
     * // 在 Activity/Fragment 中
     * NetworkStateManager.instance.networkStateFlow.collectEvent(this) { state ->
     *     onNetworkStateChanged(state)
     * }
     * ```
     */
    val networkStateFlow = _networkStateFlow.asSharedFlow()
    
    /**
     * 当前网络状态（用于防止重复通知）
     */
    @Volatile
    private var currentState: NetState? = null
    
    /**
     * 发送网络状态变化
     * 
     * @param state 网络状态
     * @return 是否发送成功
     */
    fun postNetworkState(state: NetState): Boolean {
        // 防止重复通知
        if (currentState?.isSuccess == state.isSuccess) {
            return false
        }
        
        currentState = state
        return _networkStateFlow.tryEmit(state)
    }
    
    /**
     * 获取当前网络状态
     */
    fun getCurrentState(): NetState? = currentState

    companion object {
        val instance: NetworkStateManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkStateManager()
        }
    }
}