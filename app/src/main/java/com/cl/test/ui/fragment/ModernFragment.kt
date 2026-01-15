package com.cl.test.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cl.test.base.BaseFragment
import com.cl.test.databinding.FragmentHomeBinding
import com.cl.test.ext.init
import com.cl.test.ui.adapter.ArticleListAdapter
import com.cl.test.ui.viewmodel.ModernViewModel
import com.maxvision.mvvm.base.state.UiState
import com.maxvision.mvvm.event.ShowErrorEvent
import com.maxvision.mvvm.event.ShowToastEvent
import com.maxvision.mvvm.event.UiEvent
import com.maxvision.mvvm.event.collectEvent
import com.maxvision.mvvm.ext.collectSuccess
import dagger.hilt.android.AndroidEntryPoint

/**
 * 现代化 Fragment 示例（推荐）
 * 
 * 展示最新的使用方式：
 * 1. 使用 collectEvent 收集 Flow
 * 2. 使用 UiState 处理 UI 状态
 * 3. 自动管理生命周期
 * 4. 更简洁的代码
 * 
 * @author cl
 * @since 3.2.0
 */
@AndroidEntryPoint
class ModernFragment : BaseFragment<ModernViewModel, FragmentHomeBinding>() {

    private val mArticleListAdapter: ArticleListAdapter by lazy { 
        ArticleListAdapter(arrayListOf()) 
    }

    override fun initView(savedInstanceState: Bundle?) {
        // 初始化 RecyclerView
        mDatabind.rvArticleList.init(
            LinearLayoutManager(activity), 
            mArticleListAdapter, 
            false
        )
        
        // 加载数据
        mViewModel.loadArticleList()
//        mViewModel.loadUserDetail("2323")
    }

    override fun createObserver() {
        super.createObserver()
        
        // ==================== 方式 1：使用便捷的 collectSuccess（推荐）====================
        
        // 只需要处理成功和错误情况
        // 注意：BaseVmFragment 已经自动处理了 Loading 弹窗（通过监听 uiEvent）
        mViewModel.articleListState.collectSuccess(
            owner = viewLifecycleOwner,
            onSuccess = { data ->
                // 更新 UI
                mArticleListAdapter.submitList(data.datas)
            },
            onError = { error ->
                // 显示错误
                toast("加载失败：${error.errorMsg}")
            },
            onLoading = { message ->
                // 可选：自定义 Loading 处理
                // 注意：BaseVmFragment 已经通过 uiEvent 自动显示了 Loading 弹窗
                // 这里可以添加额外的 Loading UI，比如下拉刷新的转圈
            },
            onEmpty = { message ->
                // 可选：处理空状态
                toast(message)
            }
        )
        
        // ==================== 方式 2：完整的状态处理（更灵活）====================
        // 注意：以下代码仅作为示例，实际使用时请注释掉方式 1，只使用一种方式
        
        /*
        // 如果需要处理所有状态，使用 collectEvent
        mViewModel.articleListState.collectEvent(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Idle -> {
                    // 空闲状态
                }
                is UiState.Loading -> {
                    // 加载中（BaseViewModel 已自动显示 Loading 弹窗）
                    // 如果需要自定义 Loading UI，可以在这里处理
                }
                is UiState.Success -> {
                    // 成功：更新 UI
                    mArticleListAdapter.submitList(state.data.datas)
                }
                is UiState.Error -> {
                    // 错误：显示错误信息
                    toast("加载失败：${state.exception.errorMsg}")
                }
                is UiState.Empty -> {
                    // 空状态
                    mArticleListAdapter.submitList(emptyList())
                    toast(state.message)
                }
            }
        }
        */
        
        // ==================== UI 事件处理（Toast、Error 等）====================
        // 注意：BaseVmFragment 已经自动处理了 ShowLoadingEvent 和 DismissLoadingEvent
        // 这里只需要处理其他自定义事件
        
        /*
        // 可选：如果需要处理额外的 UI 事件，可以取消注释
        mViewModel.uiEvent.collectEvent(viewLifecycleOwner) { event ->
            when (event) {
                is ShowToastEvent -> {
                    toast(event.message)
                }
                is ShowErrorEvent -> {
                    toast("错误：${event.message}")
                }
                // 其他自定义事件...
            }
        }
        */
    }
    
    /**
     * 重写 handleUiEvent 处理自定义事件
     */
    override fun handleUiEvent(event: UiEvent) {
        super.handleUiEvent(event)
        
        when (event) {
            is ShowToastEvent -> toast(event.message)
            is ShowErrorEvent -> toast("错误：${event.message}")
            // 处理其他自定义事件
        }
    }
}
