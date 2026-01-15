package com.maxvision.mvvm.base.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.maxvision.mvvm.base.viewmodel.BaseViewModel
import com.maxvision.mvvm.event.*
import com.maxvision.mvvm.ext.util.notNull
import com.maxvision.mvvm.network.manager.NetState
import com.maxvision.mvvm.network.manager.NetworkStateManager
import java.lang.reflect.ParameterizedType

/**
 * 作者　: cl
 * 时间　: 2023/04/12
 * 描述　: ViewModelActivity基类，把ViewModel注入进来了
 */
abstract class BaseVmActivity<VM : BaseViewModel> : AppCompatActivity() {

    lateinit var mViewModel: VM

    abstract fun layoutId(): Int

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun showLoading(message: String = "请求网络中...")

    abstract fun dismissLoading()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBind().notNull({
            setContentView(it)
        }, {
            setContentView(layoutId())
        })
        init(savedInstanceState)
    }

    private fun init(savedInstanceState: Bundle?) {
        mViewModel = obtainViewModel(getVMClass())
        registerUiChange()
        initView(savedInstanceState)
        createObserver()
        
        // 监听网络状态变化（现代化方案）
        NetworkStateManager.instance.networkStateFlow.collectEvent(this) { netState ->
            onNetworkStateChanged(netState)
        }
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
     * 创建LiveData数据观察者
     */
    abstract fun createObserver()

    /**
     * 注册UI 事件（现代化方案）
     */
    private fun registerUiChange() {
        // 收集 ViewModel 的 UI 事件
        mViewModel.uiEvent.collectEvent(this) { event ->
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
        // 使用 collectState 并指定 minActiveState 为 CREATED，以覆盖 onCreate 中发起的请求
        mViewModel.loadingFlow.collectState(this, Lifecycle.State.CREATED) { state ->
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
     * 将非该Activity绑定的ViewModel添加 loading回调 防止出现请求时不显示 loading 弹窗bug
     * （现代化方案）
     * 
     * @param viewModels Array<out BaseViewModel>
     */
    protected fun addLoadingObserve(vararg viewModels: BaseViewModel) {
        viewModels.forEach { viewModel ->
            // 收集额外 ViewModel 的 UI 事件
            viewModel.uiEvent.collectEvent(this) { event ->
                when (event) {
                    is ShowLoadingEvent -> showLoading(event.message)
                    is DismissLoadingEvent -> dismissLoading()
                    else -> {
                        // 其他事件也可以处理
                        handleUiEvent(event)
                    }
                }
            }
        }
    }

    /**
     * 供子类BaseVmDbActivity 初始化Databinding操作
     */
    open fun initDataBind(): View? {
        return null
    }
}