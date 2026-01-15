package com.maxvision.mvvm.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxvision.mvvm.event.*
import com.maxvision.mvvm.log.AliWrapperLog
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel 基类（现代化版本）
 * 
 * 优化内容：
 * 1. 使用 SharedFlow 替代 LiveData（现代化方案）
 * 2. 集成 AliWrapperLog 日志系统
 * 3. 提供便捷的事件发送方法
 * 4. 自动记录生命周期
 * 5. 支持协程作用域
 * 
 * @author cl
 * @since 3.2.0
 */
open class BaseViewModel : ViewModel() {

    // ==================== UI 事件管理（现代化方案）====================
    
    /**
     * UI 事件 Flow（替代原有的 EventLiveData）
     * 
     * 使用 SharedFlow 实现，解决"数据倒灌"问题
     */
    private val _uiEvent = createEventFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    
    // ==================== Loading 管理（StateFlow 版本，解决生命周期时序问题） ====================
    
    data class LoadingState(val isLoading: Boolean, val message: String = "")
    
    private val _loadingFlow = MutableStateFlow(LoadingState(false))
    val loadingFlow = _loadingFlow.asStateFlow()
    
    /**
     * 显示 Loading
     */
    protected fun showLoading(message: String = "加载中...") {
        _loadingFlow.value = LoadingState(true, message)
        // 兼容旧的事件监听
        viewModelScope.launch {
            _uiEvent.emit(ShowLoadingEvent(message))
        }
    }
    
    /**
     * 隐藏 Loading
     */
    protected fun dismissLoading() {
        _loadingFlow.value = LoadingState(false)
        // 兼容旧的事件监听
        viewModelScope.launch {
            _uiEvent.emit(DismissLoadingEvent)
        }
    }
    
    /**
     * 显示 Loading（Internal 版本，供扩展函数使用）
     */
    @JvmSynthetic
    internal suspend fun internalShowLoading(message: String = "加载中...") {
        _loadingFlow.value = LoadingState(true, message)
        _uiEvent.emit(ShowLoadingEvent(message))
    }
    
    /**
     * 隐藏 Loading（Internal 版本，供扩展函数使用）
     */
    @JvmSynthetic
    internal suspend fun internalDismissLoading() {
        _loadingFlow.value = LoadingState(false)
        _uiEvent.emit(DismissLoadingEvent)
    }
    
    // ==================== Toast 管理 ====================
    
    /**
     * 显示 Toast
     */
    protected fun showToast(message: String, duration: Int = 0) {
        viewModelScope.launch {
            _uiEvent.emit(ShowToastEvent(message, duration))
        }
    }
    
    // ==================== 错误处理 ====================
    
    /**
     * 显示错误信息
     */
    protected fun showError(message: String, throwable: Throwable? = null) {
        viewModelScope.launch {
            _uiEvent.emit(ShowErrorEvent(message, throwable))
        }
    }
}