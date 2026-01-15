package com.maxvision.mvvm.event

/**
 * UI 事件封装（现代化方案）
 * 
 * 替代原有的 EventLiveData/UnPeekLiveData
 * 使用 Channel/SharedFlow 的现代化方案，解决"数据倒灌"问题
 * 
 * 优势：
 * 1. 更简单：无需复杂的 ViewModelStore 判断
 * 2. 更安全：使用 Kotlin 协程，生命周期自动管理
 * 3. 更现代：Google 官方推荐方案（2024-2026）
 * 4. 更灵活：支持多种事件分发策略
 * 
 * 使用示例：
 * ```kotlin
 * // ViewModel 中
 * private val _showToastEvent = MutableSharedFlow<String>()
 * val showToastEvent = _showToastEvent.asSharedFlow()
 * 
 * fun showMessage(message: String) {
 *     viewModelScope.launch {
 *         _showToastEvent.emit(message)
 *     }
 * }
 * 
 * // Activity/Fragment 中
 * lifecycleScope.launch {
 *     repeatOnLifecycle(Lifecycle.State.STARTED) {
 *         viewModel.showToastEvent.collect { message ->
 *             Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
 *         }
 *     }
 * }
 * ```
 * 
 * @author cl
 * @since 3.2.0
 */

/**
 * UI 事件基类
 * 
 * 所有 UI 事件都应该继承此类，便于统一管理
 */
open class UiEvent

/**
 * 显示 Toast 事件
 */
data class ShowToastEvent(val message: String, val duration: Int = 0) : UiEvent()

/**
 * 显示 Loading 事件
 */
data class ShowLoadingEvent(val message: String = "加载中...") : UiEvent()

/**
 * 隐藏 Loading 事件
 */
object DismissLoadingEvent : UiEvent()

/**
 * 导航事件
 */
data class NavigateEvent(val route: String, val args: Map<String, Any>? = null) : UiEvent()

/**
 * 返回事件
 */
object NavigateBackEvent : UiEvent()

/**
 * 显示错误事件
 */
data class ShowErrorEvent(val message: String, val throwable: Throwable? = null) : UiEvent()

/**
 * 完成事件（通常用于关闭页面）
 */
data class FinishEvent(val result: Any? = null) : UiEvent()
