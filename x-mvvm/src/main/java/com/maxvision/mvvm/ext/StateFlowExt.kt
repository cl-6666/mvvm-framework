package com.maxvision.mvvm.ext

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.maxvision.mvvm.base.state.UiState
import com.maxvision.mvvm.network.state.ResultState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * StateFlow 扩展函数（现代化方案）
 * 
 * 提供便捷的 StateFlow/SharedFlow 操作
 * 
 * @author cl
 * @since 3.2.0
 */

// ==================== ResultState -> UiState 转换 ====================

/**
 * 将 ResultState 转换为 UiState
 * 
 * 使用示例：
 * ```kotlin
 * // ViewModel
 * private val _uiState = MutableStateFlow<UiState<ArticleList>>(UiState.Idle)
 * val uiState = _uiState.asStateFlow()
 * 
 * fun loadArticles() {
 *     viewModelScope.launch {
 *         requestFlow { apiService.getArticles() }
 *             .toUiState()
 *             .collect { _uiState.value = it }
 *     }
 * }
 * ```
 */
fun <T> Flow<ResultState<T>>.toUiState(): Flow<UiState<T>> = map { resultState ->
    when (resultState) {
        is ResultState.Loading -> UiState.loading(resultState.loadingMessage)
        is ResultState.Success -> UiState.success(resultState.data)
        is ResultState.Error -> UiState.error(resultState.error)
    }
}

// ==================== 自动更新 StateFlow ====================

/**
 * 自动将 Flow 结果更新到 MutableStateFlow
 * 
 * 使用示例：
 * ```kotlin
 * // ViewModel
 * fun loadArticles() {
 *     requestFlow { apiService.getArticles() }
 *         .toUiState()
 *         .collectTo(_uiState)  // 自动更新到 _uiState
 * }
 * ```
 */
fun <T> Flow<T>.collectTo(
    stateFlow: MutableStateFlow<T>,
    lifecycleOwner: LifecycleOwner? = null,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
) {
    if (lifecycleOwner != null) {
        // 生命周期安全的收集
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(minActiveState) {
                collect { stateFlow.value = it }
            }
        }
    } else {
        // 直接收集（通常在 ViewModel 中使用）
        throw IllegalArgumentException("请在协程作用域中手动 collect，例如：viewModelScope.launch { flow.collect { _state.value = it } }")
    }
}

// ==================== 简化的 collect ====================

/**
 * 简化的 collect，只处理成功和错误
 * 
 * 使用示例：
 * ```kotlin
 * viewModel.uiState.collectSuccess(this,
 *     onSuccess = { data -> updateUI(data) },
 *     onError = { error -> showError(error) }
 * )
 * ```
 */
fun <T> StateFlow<UiState<T>>.collectSuccess(
    owner: LifecycleOwner,
    onSuccess: (T) -> Unit,
    onError: ((com.maxvision.mvvm.network.AppException) -> Unit)? = null,
    onLoading: ((String) -> Unit)? = null,
    onEmpty: ((String) -> Unit)? = null
) {
    owner.lifecycleScope.launch {
        owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collect { state ->
                when (state) {
                    is UiState.Loading -> onLoading?.invoke(state.message)
                    is UiState.Success -> onSuccess(state.data)
                    is UiState.Error -> onError?.invoke(state.exception)
                    is UiState.Empty -> onEmpty?.invoke(state.message)
                    else -> {}
                }
            }
        }
    }
}

/**
 * ResultState 版本的简化 collect
 */
fun <T> StateFlow<ResultState<T>>.collectSuccess(
    owner: LifecycleOwner,
    onSuccess: (T) -> Unit,
    onError: ((com.maxvision.mvvm.network.AppException) -> Unit)? = null,
    onLoading: ((String) -> Unit)? = null
) {
    owner.lifecycleScope.launch {
        owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collect { state ->
                when (state) {
                    is ResultState.Loading -> onLoading?.invoke(state.loadingMessage)
                    is ResultState.Success -> onSuccess(state.data)
                    is ResultState.Error -> onError?.invoke(state.error)
                }
            }
        }
    }
}

// ==================== Flow 操作符 ====================

/**
 * 防抖
 * 
 * 使用示例：
 * ```kotlin
 * searchQueryFlow
 *     .debounceInput(500) // 500ms 防抖
 *     .collect { query -> search(query) }
 * ```
 */
fun <T> Flow<T>.debounceInput(timeoutMillis: Long = 300): Flow<T> = 
    debounce(timeoutMillis)

/**
 * 去重
 */
fun <T> Flow<T>.distinctInput(): Flow<T> = 
    distinctUntilChanged()

/**
 * 过滤空字符串
 */
fun Flow<String>.filterNotEmpty(): Flow<String> = 
    filter { it.isNotEmpty() }

/**
 * 过滤空值
 */
fun <T : Any> Flow<T?>.filterNotNull(): Flow<T> = 
    mapNotNull { it }

// ==================== 组合多个 Flow ====================

/**
 * 组合两个 Flow 的最新值
 * 
 * 使用示例：
 * ```kotlin
 * val combinedFlow = userFlow.combineLatest(settingsFlow) { user, settings ->
 *     UserWithSettings(user, settings)
 * }
 * ```
 */
fun <T1, T2, R> Flow<T1>.combineLatest(
    other: Flow<T2>,
    transform: suspend (T1, T2) -> R
): Flow<R> = combine(other, transform)

/**
 * 组合三个 Flow 的最新值
 */
fun <T1, T2, T3, R> combineLatest(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    transform: suspend (T1, T2, T3) -> R
): Flow<R> = combine(flow1, flow2, flow3, transform)

// ==================== StateFlow 工具 ====================

/**
 * 创建 MutableStateFlow 的便捷函数
 */
fun <T> mutableStateFlowOf(initialValue: T): MutableStateFlow<T> = 
    MutableStateFlow(initialValue)

/**
 * 创建 MutableSharedFlow 的便捷函数（用于事件）
 */
fun <T> mutableSharedFlowOf(
    replay: Int = 0,
    extraBufferCapacity: Int = 1
): MutableSharedFlow<T> = MutableSharedFlow(
    replay = replay,
    extraBufferCapacity = extraBufferCapacity,
    onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
)

// ==================== 错误处理 ====================

/**
 * 统一的错误处理
 * 
 * 使用示例：
 * ```kotlin
 * requestFlow { apiService.getArticles() }
 *     .onErrorReturn { UiState.error(it) }
 *     .collect { _uiState.value = it }
 * ```
 */
fun <T> Flow<UiState<T>>.onErrorReturn(
    fallback: (com.maxvision.mvvm.network.AppException) -> UiState<T>
): Flow<UiState<T>> = catch { e ->
    val exception = com.maxvision.mvvm.network.ExceptionHandle.handleException(e)
    emit(fallback(exception))
}


