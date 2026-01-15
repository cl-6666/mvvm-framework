package com.cl.test.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.cl.test.bean.Course
import com.cl.test.net.ApiService
import com.maxvision.mvvm.base.viewmodel.BaseViewModel
import com.maxvision.mvvm.ext.simpleRequest
import com.maxvision.mvvm.ext.toUiState
import com.maxvision.mvvm.log.logD
import com.maxvision.mvvm.base.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 课程 ViewModel（现代化方案）
 * 
 * 展示最新的 MVVM 架构最佳实践：
 * 1. 使用 StateFlow 管理状态
 * 2. 使用 UiState 统一状态管理
 * 3. 自动处理 Loading
 * 
 * @author cl
 * @since 2026-01-14
 */
@HiltViewModel
class CourseViewModel @Inject constructor(
    private val apiService: ApiService
) : BaseViewModel() {

    // ==================== UI 状态管理 ====================
    
    /**
     * 课程列表 UI 状态
     */
    private val _courseListState = MutableStateFlow<UiState<List<Course>>>(UiState.idle())
    val courseListState: StateFlow<UiState<List<Course>>> = _courseListState.asStateFlow()
    
    /**
     * 下拉刷新状态
     */
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    // ==================== 业务逻辑 ====================
    
    /**
     * 加载课程列表
     */
    fun loadCourseList() {
        logD("开始加载课程列表")

        viewModelScope.launch {
            simpleRequest("数据请求中...") {
                apiService.getCourseList()
            }
            .toUiState()
            .collect {
                _courseListState.value = it
            }
        }
    }

    /**
     * 下拉刷新
     */
    fun refresh() {
        // 标记为刷新中，触发 StateFlow 更新，确保状态同步
        _isRefreshing.value = true
        
        // 下拉刷新不显示 loading dialog
        viewModelScope.launch {
            simpleRequest("数据请求中...") {
                apiService.getCourseList()
            }
            .toUiState()
            .collect {
                _courseListState.value = it
                // 请求结束，停止刷新动画
                _isRefreshing.value = false
            }
        }
    }

    /**
     * 点击课程
     */
    fun onCourseClick(course: Course) {
        logD("点击课程: ${course.name}")
    }
}