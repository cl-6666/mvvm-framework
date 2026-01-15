package com.maxvision.mvvm.base.state

import com.maxvision.mvvm.network.AppException

/**
 * UI 状态封装
 * 
 * 统一管理 UI 的各种状态，提供更清晰的状态处理机制
 * 
 * 状态类型：
 * - Idle：空闲状态（初始状态）
 * - Loading：加载中
 * - Success：成功（包含数据）
 * - Error：错误（包含异常信息）
 * - Empty：空数据（成功但无数据）
 * 
 * 使用示例：
 * ```kotlin
 * // ViewModel 中
 * private val _userState = MutableStateFlow<UiState<User>>(UiState.idle())
 * val userState: StateFlow<UiState<User>> = _userState.asStateFlow()
 * 
 * // Activity/Fragment 中
 * lifecycleScope.launch {
 *     viewModel.userState.collect { state ->
 *         state.onLoading { showLoading() }
 *             .onSuccess { user -> updateUI(user) }
 *             .onError { error -> showError(error) }
 *             .onEmpty { showEmptyView() }
 *     }
 * }
 * ```
 * 
 * @author cl
 * @since 3.2.0
 */
sealed class UiState<out T> {
    
    /**
     * 空闲状态（初始状态）
     */
    object Idle : UiState<Nothing>()
    
    /**
     * 加载中
     * @property message 加载提示信息
     */
    data class Loading(val message: String = "加载中...") : UiState<Nothing>()
    
    /**
     * 成功
     * @property data 数据
     */
    data class Success<T>(val data: T) : UiState<T>()
    
    /**
     * 错误
     * @property exception 异常信息
     */
    data class Error(val exception: AppException) : UiState<Nothing>()
    
    /**
     * 空数据（成功但无数据）
     * @property message 空数据提示信息
     */
    data class Empty(val message: String = "暂无数据") : UiState<Nothing>()
    
    // ==================== 便捷属性 ====================
    
    /**
     * 是否空闲
     */
    val isIdle: Boolean get() = this is Idle
    
    /**
     * 是否加载中
     */
    val isLoading: Boolean get() = this is Loading
    
    /**
     * 是否成功
     */
    val isSuccess: Boolean get() = this is Success
    
    /**
     * 是否错误
     */
    val isError: Boolean get() = this is Error
    
    /**
     * 是否空数据
     */
    val isEmpty: Boolean get() = this is Empty
    
    // ==================== 便捷方法 ====================
    
    /**
     * 获取数据（如果是成功状态）
     * @return 数据或 null
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    /**
     * 获取异常（如果是错误状态）
     * @return 异常或 null
     */
    fun exceptionOrNull(): AppException? = when (this) {
        is Error -> exception
        else -> null
    }
    
    companion object {
        /**
         * 创建空闲状态
         */
        fun <T> idle(): UiState<T> = Idle
        
        /**
         * 创建加载状态
         */
        fun <T> loading(message: String = "加载中..."): UiState<T> = Loading(message)
        
        /**
         * 创建成功状态
         */
        fun <T> success(data: T): UiState<T> = Success(data)
        
        /**
         * 创建错误状态
         */
        fun <T> error(exception: AppException): UiState<T> = Error(exception)
        
        /**
         * 创建空数据状态
         */
        fun <T> empty(message: String = "暂无数据"): UiState<T> = Empty(message)
    }
}

// ==================== 扩展函数 ====================

/**
 * 获取数据或默认值
 * @param default 默认值
 * @return 数据或默认值
 */
fun <T> UiState<T>.getOrDefault(default: T): T = when (this) {
    is UiState.Success -> data
    else -> default
}


/**
 * 空闲状态回调
 */
inline fun <T> UiState<T>.onIdle(action: () -> Unit): UiState<T> {
    if (this is UiState.Idle) action()
    return this
}

/**
 * 加载状态回调
 */
inline fun <T> UiState<T>.onLoading(action: (String) -> Unit): UiState<T> {
    if (this is UiState.Loading) action(message)
    return this
}

/**
 * 成功状态回调
 */
inline fun <T> UiState<T>.onSuccess(action: (T) -> Unit): UiState<T> {
    if (this is UiState.Success) action(data)
    return this
}

/**
 * 错误状态回调
 */
inline fun <T> UiState<T>.onError(action: (AppException) -> Unit): UiState<T> {
    if (this is UiState.Error) action(exception)
    return this
}

/**
 * 空数据状态回调
 */
inline fun <T> UiState<T>.onEmpty(action: (String) -> Unit): UiState<T> {
    if (this is UiState.Empty) action(message)
    return this
}

/**
 * 数据转换
 */
inline fun <T, R> UiState<T>.map(transform: (T) -> R): UiState<R> {
    return when (this) {
        is UiState.Success -> UiState.success(transform(data))
        is UiState.Loading -> UiState.loading(message)
        is UiState.Error -> UiState.error(exception)
        is UiState.Empty -> UiState.empty(message)
        is UiState.Idle -> UiState.idle()
    }
}
