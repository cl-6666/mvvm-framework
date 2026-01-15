package com.cl.test.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.cl.test.bean.Data
import com.cl.test.net.ApiService
import com.maxvision.mvvm.base.state.UiState
import com.maxvision.mvvm.base.viewmodel.BaseViewModel
import com.maxvision.mvvm.ext.requestFlow
import com.maxvision.mvvm.ext.simpleRequest
import com.maxvision.mvvm.ext.toUiState
import com.maxvision.mvvm.log.logD
import com.maxvision.mvvm.log.logE
import com.maxvision.mvvm.log.logI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 现代化 ViewModel 示例（推荐）
 * 
 * 展示最新的 MVVM 架构最佳实践：
 * 1. 使用 StateFlow 替代 LiveData
 * 2. 使用 UiState 统一状态管理
 * 3. 使用 Flow 版本的网络请求
 * 4. 集成日志系统
 * 5. 自动处理 Loading 状态
 * 
 * @author cl
 * @since 3.2.0
 */
@HiltViewModel
class ModernViewModel @Inject constructor(
    private val apiService: ApiService
) : BaseViewModel() {

    // ==================== UI 状态管理（推荐方式 1：使用 UiState）====================
    
    /**
     * 文章列表 UI 状态
     * 
     * 优势：统一的状态管理（Idle、Loading、Success、Error、Empty）
     */
    private val _articleListState = MutableStateFlow<UiState<Data>>(UiState.idle())
    val articleListState: StateFlow<UiState<Data>> = _articleListState.asStateFlow()
    
    /**
     * 加载文章列表（方式 1：手动管理状态）
     */
    fun loadArticleList() {
        logD("开始加载文章列表")
        
        viewModelScope.launch {
            // 方式 1：手动管理每个状态
            requestFlow(
                isShowDialog = true,  // 自动显示/隐藏 Loading 弹窗
                loadingMessage = "加载文章列表..."
            ) {
                apiService.getEntryAndExitData()
            }
            .toUiState()  // 转换为 UiState
            .collect { state ->
                _articleListState.value = state
                
                // 记录日志
                when (state) {
                    is UiState.Loading -> logD("加载中...")
                    is UiState.Success -> logI("加载成功，共 ${state.data.datas.size} 条数据")
                    is UiState.Error -> logE("加载失败：${state.exception.errorMsg}")
                    else -> {}
                }
            }
        }
    }
    
    /**
     * 加载文章列表（方式 2：使用简化版 API）
     */
    fun loadArticleListSimple() {
        logD("开始加载文章列表（简化版）")
        
        viewModelScope.launch {
            // 方式 2：使用 simpleRequest 更简洁
            simpleRequest("加载文章列表...") {
                apiService.getEntryAndExitData()
            }
            .toUiState()
            .collect { _articleListState.value = it }
        }
    }
    
    // ==================== UI 状态管理（方式 2：直接使用 ResultState）====================
    
    /**
     * 用户详情状态（直接使用 ResultState）
     * 
     * 适用于简单场景，不需要 Empty 状态
     */
    private val _userDetailState = MutableStateFlow<com.maxvision.mvvm.network.state.ResultState<Data>>(
        com.maxvision.mvvm.network.state.ResultState.Loading("初始化...")
    )
    val userDetailState = _userDetailState.asStateFlow()
    
    /**
     * 加载用户详情
     */
    fun loadUserDetail(userId: String) {
        logD("开始加载用户详情：$userId")
        
        viewModelScope.launch {
            requestFlow(
                isShowDialog = false  // 不显示弹窗
            ) {
                apiService.getEntryAndExitData()  // 实际应该是 getUserDetail(userId)
            }.collect { resultState ->
                _userDetailState.value = resultState
            }
        }
    }
    
    // ==================== 下拉刷新 ====================
    
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    /**
     * 下拉刷新
     */
    fun refresh() {
        logD("下拉刷新")
        _isRefreshing.value = true
        
        viewModelScope.launch {
            requestFlow(isShowDialog = false) {
                apiService.getEntryAndExitData()
            }
            .toUiState()
            .collect { state ->
                _articleListState.value = state
                _isRefreshing.value = false
            }
        }
    }
    
    // ==================== 加载更多 ====================
    
    private var currentPage = 0
    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()
    
    /**
     * 加载更多
     */
    fun loadMore() {
        if (!_hasMore.value) {
            showToast("没有更多数据了")
            return
        }
        
        logD("加载更多，当前页：$currentPage")
        
        viewModelScope.launch {
            requestFlow(isShowDialog = false) {
                apiService.getEntryAndExitData()  // 实际应该传入 page 参数
            }.collect { resultState ->
                when (resultState) {
                    is com.maxvision.mvvm.network.state.ResultState.Success -> {
                        val newData = resultState.data
                        
                        // 追加数据到现有列表
                        val currentState = _articleListState.value
                        if (currentState is UiState.Success) {
                            val mergedData = currentState.data.copy(
                                datas = currentState.data.datas + newData.datas
                            )
                            _articleListState.value = UiState.success(mergedData)
                            
                            currentPage++
                            _hasMore.value = newData.datas.isNotEmpty()
                            
                            logI("加载更多成功，新增 ${newData.datas.size} 条")
                        }
                    }
                    is com.maxvision.mvvm.network.state.ResultState.Error -> {
                        showError("加载更多失败：${resultState.error.errorMsg}")
                    }
                    else -> {}
                }
            }
        }
    }
    
    // ==================== 搜索功能 ====================
    
    private val _searchState = MutableStateFlow<UiState<Data>>(UiState.idle())
    val searchState: StateFlow<UiState<Data>> = _searchState.asStateFlow()
    
    /**
     * 搜索文章
     */
    fun searchArticles(keyword: String) {
        if (keyword.isBlank()) {
            showToast("请输入搜索关键词")
            return
        }
        
        logD("搜索文章：$keyword")
        
        viewModelScope.launch {
            simpleRequest("搜索中...") {
                apiService.getEntryAndExitData()  // 实际应该是 searchArticles(keyword)
            }
            .toUiState()
            .collect { state ->
                _searchState.value = state
                
                // 如果搜索结果为空，显示 Empty 状态
                if (state is UiState.Success && state.data.datas.isEmpty()) {
                    _searchState.value = UiState.empty("未找到相关内容")
                }
            }
        }
    }
    
    // ==================== 复杂业务逻辑示例 ====================
    
    /**
     * 收藏文章
     */
    fun collectArticle(articleId: Int) {
        logD("收藏文章：$articleId")
        
        viewModelScope.launch {
            try {
                showLoading("收藏中...")
                
                // 模拟网络请求
                val response = apiService.getEntryAndExitData()  // 实际应该是 collectArticle(articleId)
                
                dismissLoading()
                
                if (response.isSucces()) {
                    showToast("收藏成功")
                    logI("文章 $articleId 收藏成功")
                } else {
                    showError("收藏失败：${response.getResponseMsg()}")
                }
            } catch (e: Exception) {
                dismissLoading()
                showError("收藏失败", e)
                logE("收藏失败", e)
            }
        }
    }
    
    /**
     * 取消收藏
     */
    fun uncollectArticle(articleId: Int) {
        logD("取消收藏：$articleId")
        
        // 使用便捷方法
        viewModelScope.launch {
            try {
                showLoading("取消收藏...")
                
                val response = apiService.getEntryAndExitData()
                
                dismissLoading()
                
                if (response.isSucces()) {
                    showToast("已取消收藏")
                } else {
                    showError("操作失败")
                }
            } catch (e: Exception) {
                dismissLoading()
                showError("操作失败", e)
            }
        }
    }
}
