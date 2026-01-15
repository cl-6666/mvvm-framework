package com.maxvision.mvvm.event

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.maxvision.mvvm.base.viewmodel.BaseViewModel

/**
 * UI 事件处理扩展（便捷方法）
 * 
 * 简化 Activity/Fragment 中的 UI 事件处理
 * 
 * 使用示例：
 * ```kotlin
 * // Activity 中
 * class MyActivity : BaseVmVbActivity<MyViewModel, ActivityBinding>() {
 *     override fun initView(savedInstanceState: Bundle?) {
 *         // 自动处理常见的 UI 事件
 *         collectCommonUiEvents(mViewModel)
 *     }
 *     
 *     // 也可以自定义处理
 *     override fun handleUiEvent(event: UiEvent) {
 *         when (event) {
 *             is ShowToastEvent -> showToast(event.message)
 *             is NavigateEvent -> navigateTo(event.route)
 *             // ...
 *         }
 *     }
 * }
 * ```
 * 
 * @author cl
 * @since 3.2.0
 */

// ==================== Activity 扩展 ====================

/**
 * 收集常见的 UI 事件（Toast、Loading、Error）
 * 
 * 这是最简单的使用方式，自动处理常见事件
 */
fun AppCompatActivity.collectCommonUiEvents(
    viewModel: BaseViewModel,
    showToast: (String, Int) -> Unit = { message, duration -> 
        Toast.makeText(this, message, duration).show()
    },
    showLoading: (String) -> Unit = {},
    dismissLoading: () -> Unit = {},
    showError: (String, Throwable?) -> Unit = { message, _ ->
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
) {
    viewModel.uiEvent.collectEvent(this) { event ->
        when (event) {
            is ShowToastEvent -> showToast(event.message, event.duration)
            is ShowLoadingEvent -> showLoading(event.message)
            is DismissLoadingEvent -> dismissLoading()
            is ShowErrorEvent -> showError(event.message, event.throwable)
            else -> {
                // 其他事件不处理
            }
        }
    }
}

/**
 * 收集所有 UI 事件（包括导航、完成等）
 * 
 * 需要提供所有事件的处理器
 */
fun AppCompatActivity.collectAllUiEvents(
    viewModel: BaseViewModel,
    onShowToast: (ShowToastEvent) -> Unit = { event ->
        Toast.makeText(this, event.message, event.duration).show()
    },
    onShowLoading: (ShowLoadingEvent) -> Unit = {},
    onDismissLoading: () -> Unit = {},
    onShowError: (ShowErrorEvent) -> Unit = { event ->
        Toast.makeText(this, event.message, Toast.LENGTH_LONG).show()
    },
    onNavigate: (NavigateEvent) -> Unit = {},
    onNavigateBack: () -> Unit = { finish() },
    onFinish: (FinishEvent) -> Unit = { finish() },
    onOther: (UiEvent) -> Unit = {}
) {
    viewModel.uiEvent.collectEvent(this) { event ->
        when (event) {
            is ShowToastEvent -> onShowToast(event)
            is ShowLoadingEvent -> onShowLoading(event)
            is DismissLoadingEvent -> onDismissLoading()
            is ShowErrorEvent -> onShowError(event)
            is NavigateEvent -> onNavigate(event)
            is NavigateBackEvent -> onNavigateBack()
            is FinishEvent -> onFinish(event)
            else -> onOther(event)
        }
    }
}

// ==================== Fragment 扩展 ====================

/**
 * 收集常见的 UI 事件（Toast、Loading、Error）
 * 
 * Fragment 版本
 */
fun Fragment.collectCommonUiEvents(
    viewModel: BaseViewModel,
    showToast: (String, Int) -> Unit = { message, duration -> 
        Toast.makeText(requireContext(), message, duration).show()
    },
    showLoading: (String) -> Unit = {},
    dismissLoading: () -> Unit = {},
    showError: (String, Throwable?) -> Unit = { message, _ ->
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
) {
    viewModel.uiEvent.collectEvent(viewLifecycleOwner) { event ->
        when (event) {
            is ShowToastEvent -> showToast(event.message, event.duration)
            is ShowLoadingEvent -> showLoading(event.message)
            is DismissLoadingEvent -> dismissLoading()
            is ShowErrorEvent -> showError(event.message, event.throwable)
            else -> {
                // 其他事件不处理
            }
        }
    }
}

/**
 * 收集所有 UI 事件（包括导航、完成等）
 * 
 * Fragment 版本
 */
fun Fragment.collectAllUiEvents(
    viewModel: BaseViewModel,
    onShowToast: (ShowToastEvent) -> Unit = { event ->
        Toast.makeText(requireContext(), event.message, event.duration).show()
    },
    onShowLoading: (ShowLoadingEvent) -> Unit = {},
    onDismissLoading: () -> Unit = {},
    onShowError: (ShowErrorEvent) -> Unit = { event ->
        Toast.makeText(requireContext(), event.message, Toast.LENGTH_LONG).show()
    },
    onNavigate: (NavigateEvent) -> Unit = {},
    onNavigateBack: () -> Unit = { requireActivity().finish() },
    onFinish: (FinishEvent) -> Unit = { requireActivity().finish() },
    onOther: (UiEvent) -> Unit = {}
) {
    viewModel.uiEvent.collectEvent(viewLifecycleOwner) { event ->
        when (event) {
            is ShowToastEvent -> onShowToast(event)
            is ShowLoadingEvent -> onShowLoading(event)
            is DismissLoadingEvent -> onDismissLoading()
            is ShowErrorEvent -> onShowError(event)
            is NavigateEvent -> onNavigate(event)
            is NavigateBackEvent -> onNavigateBack()
            is FinishEvent -> onFinish(event)
            else -> onOther(event)
        }
    }
}

// ==================== 批量观察多个 ViewModel ====================

/**
 * 批量收集多个 ViewModel 的 UI 事件
 * 
 * Activity 版本
 */
fun AppCompatActivity.collectMultipleViewModels(
    vararg viewModels: BaseViewModel,
    handler: (UiEvent) -> Unit
) {
    viewModels.forEach { viewModel ->
        viewModel.uiEvent.collectEvent(this, action = handler)
    }
}

/**
 * 批量收集多个 ViewModel 的 UI 事件
 * 
 * Fragment 版本
 */
fun Fragment.collectMultipleViewModels(
    vararg viewModels: BaseViewModel,
    handler: (UiEvent) -> Unit
) {
    viewModels.forEach { viewModel ->
        viewModel.uiEvent.collectEvent(viewLifecycleOwner, action = handler)
    }
}
