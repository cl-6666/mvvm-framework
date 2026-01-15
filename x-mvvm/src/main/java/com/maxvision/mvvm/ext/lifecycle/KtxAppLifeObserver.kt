package com.maxvision.mvvm.ext.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 作者　: cl
 * 时间　: 2023/04/12
 * 描述　: App 生命周期观察者（现代化版本）
 * 
 * 优化内容：
 * 1. 使用 StateFlow 替代 BooleanLiveData
 * 2. 使用 DefaultLifecycleObserver 替代 @OnLifecycleEvent（已废弃）
 * 3. 更现代、更安全的实现
 * 
 * 使用示例：
 * ```kotlin
 * // 在 Application 中注册
 * ProcessLifecycleOwner.get().lifecycle.addObserver(KtxAppLifeObserver)
 * 
 * // 在需要的地方监听
 * lifecycleScope.launch {
 *     KtxAppLifeObserver.isForegroundFlow.collect { isForeground ->
 *         if (isForeground) {
 *             // App 在前台
 *         } else {
 *             // App 在后台
 *         }
 *     }
 * }
 * ```
 */
object KtxAppLifeObserver : DefaultLifecycleObserver {

    private val _isForegroundFlow = MutableStateFlow(false)
    
    /**
     * App 是否在前台（StateFlow 版本）
     */
    val isForegroundFlow: StateFlow<Boolean> = _isForegroundFlow.asStateFlow()
    
    /**
     * App 是否在前台（直接访问当前值）
     */
    val isForeground: Boolean
        get() = _isForegroundFlow.value

    /**
     * App 进入前台
     */
    override fun onStart(owner: LifecycleOwner) {
        _isForegroundFlow.value = true
    }

    /**
     * App 进入后台
     */
    override fun onStop(owner: LifecycleOwner) {
        _isForegroundFlow.value = false
    }
}