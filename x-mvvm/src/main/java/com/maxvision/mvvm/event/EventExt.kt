package com.maxvision.mvvm.event

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * 事件扩展函数（现代化方案）
 * 
 * 提供便捷的事件收集和发送方法
 * 
 * @author cl
 * @since 3.2.0
 */

// ==================== 事件发送 ====================

/**
 * 发送事件（挂起函数）
 * 
 * 使用示例：
 * ```kotlin
 * viewModelScope.launch {
 *     _eventFlow.sendEvent(ShowToastEvent("操作成功"))
 * }
 * ```
 */
suspend fun <T> MutableSharedFlow<T>.sendEvent(event: T) {
    emit(event)
}

/**
 * 发送事件（非挂起函数）
 * 
 * 使用示例：
 * ```kotlin
 * _eventFlow.trySendEvent(ShowToastEvent("操作成功"))
 * ```
 */
fun <T> MutableSharedFlow<T>.trySendEvent(event: T) {
    tryEmit(event)
}

// ==================== 事件收集（生命周期安全）====================

/**
 * 在生命周期安全的情况下收集事件
 * 
 * 自动在 STARTED 状态时开始收集，STOPPED 时停止
 * 防止在后台时继续收集事件导致的问题
 * 
 * 使用示例：
 * ```kotlin
 * // Activity/Fragment 中
 * viewModel.eventFlow.collectEvent(this) { event ->
 *     when (event) {
 *         is ShowToastEvent -> showToast(event.message)
 *         is NavigateEvent -> navigate(event.route)
 *     }
 * }
 * ```
 * 
 * @param owner LifecycleOwner（Activity/Fragment）
 * @param minActiveState 最小活跃状态，默认 STARTED
 * @param action 事件处理回调
 */
fun <T> Flow<T>.collectEvent(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    action: suspend (T) -> Unit
) {
    owner.lifecycleScope.launch {
        owner.repeatOnLifecycle(minActiveState) {
            collect(action)
        }
    }
}

/**
 * 在生命周期安全的情况下收集 StateFlow
 * 
 * 与 collectEvent 类似，但专门用于 StateFlow
 * 
 * 使用示例：
 * ```kotlin
 * viewModel.uiState.collectState(this) { state ->
 *     updateUI(state)
 * }
 * ```
 */
fun <T> StateFlow<T>.collectState(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    action: suspend (T) -> Unit
) {
    owner.lifecycleScope.launch {
        owner.repeatOnLifecycle(minActiveState) {
            collect(action)
        }
    }
}

/**
 * 在生命周期安全的情况下收集 SharedFlow
 */
fun <T> SharedFlow<T>.collectEvent(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    action: suspend (T) -> Unit
) {
    owner.lifecycleScope.launch {
        owner.repeatOnLifecycle(minActiveState) {
            collect(action)
        }
    }
}

// ==================== 创建事件 Flow ====================

/**
 * 创建单次事件 Flow（用于一次性事件，如 Toast、导航等）
 * 
 * 特点：
 * - 不保留历史值
 * - 只有活跃的订阅者才能收到事件
 * - 自动处理配置变更（如屏幕旋转）
 * 
 * 使用示例：
 * ```kotlin
 * class MyViewModel : BaseViewModel() {
 *     private val _toastEvent = createEventFlow<String>()
 *     val toastEvent = _toastEvent.asSharedFlow()
 *     
 *     fun showToast(message: String) {
 *         viewModelScope.launch {
 *             _toastEvent.emit(message)
 *         }
 *     }
 * }
 * ```
 */
fun <T> createEventFlow(): MutableSharedFlow<T> {
    return MutableSharedFlow(
        replay = 0,                  // 不保留历史值
        extraBufferCapacity = 64,    // 增加缓冲容量，确保事件不丢失
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
    )
}

/**
 * 创建状态 Flow（用于 UI 状态，会保留最新值）
 * 
 * 特点：
 * - 保留最新值
 * - 新订阅者会立即收到当前值
 * - 自动处理配置变更
 * 
 * 使用示例：
 * ```kotlin
 * class MyViewModel : BaseViewModel() {
 *     private val _uiState = createStateFlow(UiState.Idle)
 *     val uiState = _uiState.asStateFlow()
 *     
 *     fun updateState(newState: UiState) {
 *         _uiState.value = newState
 *     }
 * }
 * ```
 */
fun <T> createStateFlow(initialValue: T): MutableStateFlow<T> {
    return MutableStateFlow(initialValue)
}

// ==================== UI 事件处理器 ====================

/**
 * UI 事件处理器基类
 * 
 * 简化常见 UI 事件的处理
 * 
 * 使用示例：
 * ```kotlin
 * // Activity/Fragment 中
 * viewModel.uiEvent.collectEvent(this) { event ->
 *     handleUiEvent(event)
 * }
 * 
 * private fun handleUiEvent(event: UiEvent) {
 *     when (event) {
 *         is ShowToastEvent -> Toast.makeText(this, event.message, event.duration).show()
 *         is ShowLoadingEvent -> showLoading(event.message)
 *         is DismissLoadingEvent -> dismissLoading()
 *         is NavigateEvent -> navigate(event.route, event.args)
 *         is NavigateBackEvent -> finish()
 *         is ShowErrorEvent -> showError(event.message)
 *         is FinishEvent -> finish()
 *     }
 * }
 * ```
 */
interface UiEventHandler {
    fun handleUiEvent(event: UiEvent) {
        when (event) {
            is ShowToastEvent -> onShowToast(event.message, event.duration)
            is ShowLoadingEvent -> onShowLoading(event.message)
            is DismissLoadingEvent -> onDismissLoading()
            is NavigateEvent -> onNavigate(event.route, event.args)
            is NavigateBackEvent -> onNavigateBack()
            is ShowErrorEvent -> onShowError(event.message, event.throwable)
            is FinishEvent -> onFinish(event.result)
            else -> onUnhandledEvent(event)
        }
    }
    
    fun onShowToast(message: String, duration: Int) {}
    fun onShowLoading(message: String) {}
    fun onDismissLoading() {}
    fun onNavigate(route: String, args: Map<String, Any>?) {}
    fun onNavigateBack() {}
    fun onShowError(message: String, throwable: Throwable?) {}
    fun onFinish(result: Any?) {}
    fun onUnhandledEvent(event: UiEvent) {}
}
