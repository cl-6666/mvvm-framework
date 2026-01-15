package com.cl.test.ui.fragment

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cl.test.R
import com.cl.test.base.BaseFragment
import com.cl.test.bean.Course
import com.cl.test.databinding.FragmentCourseBinding
import com.cl.test.ext.dismissLoadingExt
import com.cl.test.ext.showLoadingExt
import com.cl.test.ui.adapter.CourseAdapter
import com.cl.test.ui.viewmodel.CourseViewModel
import com.maxvision.mvvm.base.fragment.BaseVmVbFragment
import com.maxvision.mvvm.ext.collectSuccess
import com.maxvision.mvvm.log.logD
import com.maxvision.mvvm.log.logI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * 课程 Fragment（现代化方案）
 * 
 * 展示最新的 MVVM 架构最佳实践：
 * 1. 使用 ViewBinding
 * 2. 使用 StateFlow + collectSuccess 观察数据
 * 3. 自动处理 Loading/Error/Empty 状态
 * 4. 下拉刷新
 * 5. RecyclerView 适配器
 * 
 * @author cl
 * @since 2026-01-14
 */
@AndroidEntryPoint
class CourseFragment : BaseFragment<CourseViewModel, FragmentCourseBinding>() {

    private val courseAdapter by lazy {
        CourseAdapter().apply {
            setOnItemClickListener { course ->
                mViewModel.onCourseClick(course)
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        logD("初始化课程列表页面")
        
        // 配置 RecyclerView
        mDatabind.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = courseAdapter
        }
        
        // 配置下拉刷新
        mDatabind.swipeRefreshLayout.setOnRefreshListener {
            logI("用户触发下拉刷新")
            mViewModel.refresh()
        }
        
        // 配置空状态点击重试
        mDatabind.layoutEmpty.setOnClickListener {
            logI("点击重试按钮")
            mViewModel.loadCourseList()
        }
    }

    override fun createObserver() {
        logD("注册数据观察者")
        
        // 观察课程列表状态
        mViewModel.courseListState.collectSuccess(
            owner = viewLifecycleOwner,
            onSuccess = { courseList ->
                logI("收到课程列表数据，共 ${courseList.size} 门课程")
                courseAdapter.submitList(courseList)
            },
            onError = { error ->
                logI("请求失败: ${error.errorMsg}")
            }
        )
        
        // 观察下拉刷新状态
        viewLifecycleOwner.lifecycleScope.launch {
            mViewModel.isRefreshing.collect { isRefreshing ->
                mDatabind.swipeRefreshLayout.isRefreshing = isRefreshing
            }
        }
    }

    override fun initData() {
        logD("开始加载课程数据")
        mViewModel.loadCourseList()
    }

    override fun lazyLoadData() {
        // 由于在 initData 中已加载，这里不需要重复加载
    }
}
