package com.maxvision.mvvm.ext

import com.maxvision.mvvm.base.viewmodel.BaseViewModel
import com.maxvision.mvvm.network.AppException
import com.maxvision.mvvm.network.BaseResponse
import com.maxvision.mvvm.network.ExceptionHandle
import com.maxvision.mvvm.network.state.ResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.catch

/**
 * Flow 版本的网络请求扩展（现代化方案）
 * 
 * 推荐使用 Flow 替代 LiveData 进行网络请求
 * 
 * 优势：
 * 1. 更灵活的操作符支持
 * 2. 冷流，按需订阅
 * 3. 更好的生命周期管理
 * 4. 支持背压
 * 
 * @author cl
 * @since 3.2.0
 */

// ==================== Flow 版本 - 推荐使用 ====================

/**
 * Flow 版本：自动脱壳的网络请求
 * 
 * 使用示例：
 * ```kotlin
 * // ViewModel
 * fun getArticleList() = requestFlow(
 *     isShowDialog = true,
 *     loadingMessage = "加载中..."
 * ) {
 *     apiService.getArticleList()
 * }
 * 
 * // Activity/Fragment
 * viewModel.getArticleList().collectState(this) { resultState ->
 *     when (resultState) {
 *         is ResultState.Loading -> showLoading(resultState.loadingMessage)
 *         is ResultState.Success -> updateUI(resultState.data)
 *         is ResultState.Error -> showError(resultState.error.errorMsg)
 *     }
 * }
 * ```
 * 
 * @param isShowDialog 是否显示 Loading
 * @param loadingMessage Loading 提示文本
 * @param block 网络请求体
 * @return Flow<ResultState<T>>
 */
fun <T> BaseViewModel.requestFlow(
    isShowDialog: Boolean = false,
    loadingMessage: String = "请求网络中...",
    block: suspend () -> BaseResponse<T>
): Flow<ResultState<T>> = flow {
    // 执行请求
    val response = block()
    
    // 判断结果
    if (response.isSucces()) {
        emit(ResultState.onAppSuccess(response.getResponseData()))
    } else {
        emit(ResultState.onAppError(
            AppException(
                response.getResponseCode(),
                response.getResponseMsg(),
                response.getResponseMsg()
            )
        ))
    }
    
    // 请求完成，隐藏 Loading
    if (isShowDialog) {
        internalDismissLoading()
    }
}.onStart {
    // 显示 Loading
    if (isShowDialog) {
        internalShowLoading(loadingMessage)
        emit(ResultState.onAppLoading(loadingMessage))
    }
}.catch { e ->
    // 隐藏 Loading
    if (isShowDialog) {
        internalDismissLoading()
    }
    // 发射错误
    emit(ResultState.onAppError(ExceptionHandle.handleException(e)))
}

/**
 * Flow 版本：不脱壳的网络请求
 * 
 * 使用示例：
 * ```kotlin
 * // ViewModel
 * fun getArticleList() = requestFlowNoCheck(
 *     isShowDialog = true
 * ) {
 *     apiService.getArticleList()
 * }
 * 
 * // Activity/Fragment
 * viewModel.getArticleList().collectState(this) { resultState ->
 *     when (resultState) {
 *         is ResultState.Success -> {
 *             val response = resultState.data
 *             if (response.errorCode == 0) {
 *                 // 成功
 *             } else {
 *                 // 业务错误
 *             }
 *         }
 *         // ...
 *     }
 * }
 * ```
 */
fun <T> BaseViewModel.requestFlowNoCheck(
    isShowDialog: Boolean = false,
    loadingMessage: String = "请求网络中...",
    block: suspend () -> T
): Flow<ResultState<T>> = flow {
    // 执行请求
    val result = block()
    
    // 直接返回结果
    emit(ResultState.onAppSuccess(result))
    
    // 请求完成，隐藏 Loading
    if (isShowDialog) {
        internalDismissLoading()
    }
}.onStart {
    // 显示 Loading
    if (isShowDialog) {
        internalShowLoading(loadingMessage)
        emit(ResultState.onAppLoading(loadingMessage))
    }
}.catch { e ->
    // 隐藏 Loading
    if (isShowDialog) {
        internalDismissLoading()
    }
    // 发射错误
    emit(ResultState.onAppError(ExceptionHandle.handleException(e)))
}

// ==================== 简化版本（直接返回数据，自动处理 Loading）====================

/**
 * 简化版：自动脱壳 + 自动显示/隐藏 Loading
 * 
 * 使用示例：
 * ```kotlin
 * // ViewModel
 * fun getArticleList() = simpleRequest("加载文章列表...") {
 *     apiService.getArticleList()
 * }
 * 
 * // Activity/Fragment
 * viewModel.getArticleList().collectState(this) { resultState ->
 *     when (resultState) {
 *         is ResultState.Success -> updateUI(resultState.data)
 *         is ResultState.Error -> showError(resultState.error.errorMsg)
 *         else -> {}
 *     }
 * }
 * ```
 */
fun <T> BaseViewModel.simpleRequest(
    loadingMessage: String = "加载中...",
    block: suspend () -> BaseResponse<T>
): Flow<ResultState<T>> = requestFlow(
    isShowDialog = true,
    loadingMessage = loadingMessage,
    block = block
)

/**
 * 简化版：不脱壳 + 自动显示/隐藏 Loading
 */
fun <T> BaseViewModel.simpleRequestNoCheck(
    loadingMessage: String = "加载中...",
    block: suspend () -> T
): Flow<ResultState<T>> = requestFlowNoCheck(
    isShowDialog = true,
    loadingMessage = loadingMessage,
    block = block
)

// ==================== 批量请求 ====================

/**
 * 批量请求（并行执行）
 * 
 * 使用示例：
 * ```kotlin
 * // ViewModel
 * fun loadAllData() = batchRequest {
 *     val user = async { apiService.getUser() }
 *     val article = async { apiService.getArticles() }
 *     Pair(user.await(), article.await())
 * }
 * ```
 */
fun <T> BaseViewModel.batchRequest(
    loadingMessage: String = "加载中...",
    block: suspend () -> T
): Flow<ResultState<T>> = requestFlowNoCheck(
    isShowDialog = true,
    loadingMessage = loadingMessage,
    block = block
)
