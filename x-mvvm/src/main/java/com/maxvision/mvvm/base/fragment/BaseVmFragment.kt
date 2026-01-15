package com.maxvision.mvvm.base.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.maxvision.mvvm.base.viewmodel.BaseViewModel
import com.maxvision.mvvm.event.DismissLoadingEvent
import com.maxvision.mvvm.event.ShowLoadingEvent
import com.maxvision.mvvm.event.UiEvent
import com.maxvision.mvvm.event.collectEvent
import com.maxvision.mvvm.event.collectState
import com.maxvision.mvvm.network.manager.NetState
import com.maxvision.mvvm.network.manager.NetworkStateManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.reflect.ParameterizedType

/**
 * ViewModelFragment 基类
 * 
 * 优化内容：
 * 1. 使用 lifecycleScope 替代 Handler，防止内存泄漏
 * 2. 优化懒加载逻辑
 * 3. 支持更灵活的生命周期管理
 * 
 * @author cl
 * @since 3.2.0
 */
abstract class BaseVmFragment<VM : BaseViewModel> : Fragment() {

    // 是否第一次加载
    private var isFirst: Boolean = true

    lateinit var mViewModel: VM

    lateinit var mActivity: AppCompatActivity

    /**
     * Fragment 懒加载默认延迟时间（毫秒）
     * 防止切换动画未完成时数据已加载导致的卡顿
     */
    private val DEFAULT_LAZY_LOAD_DELAY = 300L

    /**
     * 当前Fragment绑定的视图布局
     */
    abstract fun layoutId(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId(), container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isFirst = true
        mViewModel = obtainViewModel(getVMClass())
        
        // 必须先注册 UI 事件监听，否则在 initView 中发起的请求无法显示 Loading
        registorDefUIChange()
        
        initView(savedInstanceState)
        createObserver()
        initData()
    }

    /**
     * 网络变化监听 子类重写
     */
    open fun onNetworkStateChanged(netState: NetState) {}


    /**
     * 获取[ViewModel]
     */
    private fun <T : ViewModel> obtainViewModel(vmClass: Class<T>): T {
        return ViewModelProvider(
            viewModelStore,
            defaultViewModelProviderFactory,
            defaultViewModelCreationExtras
        )[vmClass]
    }

    /**
     * 获取Activity持有的[ViewModel]
     */
    fun <T : ViewModel> obtainActivityViewModel(vmClass: Class<T>): T {
        return ViewModelProvider(
            requireActivity().viewModelStore,
            requireActivity().defaultViewModelProviderFactory,
            requireActivity().defaultViewModelCreationExtras
        )[vmClass]
    }

    /**
     * 获取泛型VM对应的类
     */
    @Suppress("UNCHECKED_CAST")
    private fun getVMClass(): Class<VM> {
        var cls: Class<*>? = javaClass
        var vmClass: Class<VM>? = null
        while (vmClass == null && cls != null) {
            vmClass = getVMClass(cls)
            cls = cls.superclass
        }
        if (vmClass == null) {
            vmClass = BaseViewModel::class.java as Class<VM>
        }
        return vmClass
    }

    /**
     * 根据传入的 cls 获取泛型VM对应的类
     */
    @Suppress("UNCHECKED_CAST")
    private fun getVMClass(cls: Class<*>): Class<VM>? {
        val type = cls.genericSuperclass
        if (type is ParameterizedType) {
            val types = type.actualTypeArguments
            for (t in types) {
                if (t is Class<*>) {
                    if (BaseViewModel::class.java.isAssignableFrom(t)) {
                        return t as? Class<VM>
                    }
                } else if (t is ParameterizedType) {
                    val rawType = t.rawType
                    if (rawType is Class<*>) {
                        if (BaseViewModel::class.java.isAssignableFrom(rawType)) {
                            return rawType as? Class<VM>
                        }
                    }
                }
            }
        }
        return null
    }
    /**
     * 初始化view
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 懒加载
     */
    abstract fun lazyLoadData()

    /**
     * 创建观察者
     */
    abstract fun createObserver()

    override fun onResume() {
        super.onResume()
        onVisible()
    }

    /**
     * Fragment 可见时执行懒加载
     * 
     * 优化：使用 lifecycleScope 替代 Handler，自动管理生命周期
     */
    private fun onVisible() {
        if (lifecycle.currentState == Lifecycle.State.STARTED && isFirst) {
            // 使用 lifecycleScope 延迟加载，自动管理生命周期
            viewLifecycleOwner.lifecycleScope.launch {
                // 延迟加载，防止动画卡顿
                delay(lazyLoadTime())
                
                // 执行懒加载
                lazyLoadData()
                
                isFirst = false
            }
            
            // 监听网络状态变化（现代化方案）
            NetworkStateManager.instance.networkStateFlow.collectEvent(viewLifecycleOwner) { netState ->
                if (!isFirst) {
                    onNetworkStateChanged(netState)
                }
            }
        }
    }

    /**
     * Fragment执行onCreate后触发的方法
     */
    open fun initData() {}

    abstract fun showLoading(message: String = "请求网络中...")

    abstract fun dismissLoading()

    /**
     * 注册 UI 事件（现代化方案）
     */
    private fun registorDefUIChange() {
        // 收集 ViewModel 的 UI 事件
        mViewModel.uiEvent.collectEvent(viewLifecycleOwner) { event ->
            when (event) {
                is ShowLoadingEvent -> showLoading(event.message)
                is DismissLoadingEvent -> dismissLoading()
                else -> {
                    // 其他事件由子类处理
                    handleUiEvent(event)
                }
            }
        }
        
        // 收集 Loading 状态（解决时序问题）
        // 使用 collectState 并指定 minActiveState 为 CREATED，以覆盖 onViewCreated 中发起的请求
        mViewModel.loadingFlow.collectState(viewLifecycleOwner, Lifecycle.State.CREATED) { state ->
            if (state.isLoading) {
                showLoading(state.message)
            } else {
                dismissLoading()
            }
        }
    }
    
    /**
     * 处理 UI 事件（子类可重写）
     */
    protected open fun handleUiEvent(event: UiEvent) {
        // 子类可以重写此方法来处理其他 UI 事件
        // 如 ShowToastEvent, NavigateEvent 等
    }

    /**
     * 将非该Fragment绑定的ViewModel添加 loading回调 防止出现请求时不显示 loading 弹窗bug
     * （现代化方案）
     * 
     * @param viewModels Array<out BaseViewModel>
     */
    protected fun addLoadingObserve(vararg viewModels: BaseViewModel) {
        viewModels.forEach { viewModel ->
            // 收集额外 ViewModel 的 UI 事件
            viewModel.uiEvent.collectEvent(viewLifecycleOwner) { event ->
                when (event) {
                    is ShowLoadingEvent -> showLoading(event.message)
                    is DismissLoadingEvent -> dismissLoading()
                    else -> {
                        // 其他事件也可以处理
                        handleUiEvent(event)
                    }
                }
            }
            
            // 收集额外 ViewModel 的 Loading 状态（解决时序问题）
            viewModel.loadingFlow.collectState(viewLifecycleOwner, Lifecycle.State.CREATED) { state ->
                if (state.isLoading) {
                    showLoading(state.message)
                } else {
                    dismissLoading()
                }
            }
        }
    }

    /**
     * 懒加载延迟时间（毫秒）
     * 
     * 防止切换动画还没执行完毕时数据就已经加载好了，这时页面会有渲染卡顿
     * 可以根据动画时长调整，默认使用框架配置的延迟时间
     * 
     * @return 延迟时间（毫秒）
     */
    open fun lazyLoadTime(): Long = DEFAULT_LAZY_LOAD_DELAY

}