package com.cl.mvvm.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

import com.cl.mvvm.BaseApplication;
import com.cl.mvvm.http.manager.NetworkStateManager;
import com.cl.mvvm.widget.loading.LoadingDialog;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 项目：My Application
 * 作者：Arry
 * 创建日期：4/15/21
 * 描述： 基类Activity
 * 修订历史：
 */
public abstract class BaseMvvmActivity<DB extends ViewDataBinding, VM extends BaseViewModel> extends AppCompatActivity implements IBaseView<DB, VM> {

    protected LoadingDialog mLoadingDialog;
    private ViewModelProvider mActivityProvider;
    @Nullable
    protected VM mViewModel;
    @Nullable
    protected DB mBinding;
    private int mViewModelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindView();
        bindLiveData();
        getLifecycle().addObserver(NetworkStateManager.getInstance());
        bindData();
        bindEvent();
    }

    private void bindView() {
        mBinding = getBinding(null, null);
        mViewModelId = getViewModelId();
        mViewModel = getViewModel();
        if (mViewModel == null) {
            Class modelClass;
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
            } else {
                //如果没有指定泛型参数，则默认使用BaseViewModel
                modelClass = BaseViewModel.class;
            }
            mViewModel = (VM) getActivityViewModelProvider(this).get(modelClass);
        }
        //关联ViewModel
        mBinding.setVariable(mViewModelId, mViewModel);
        //支持LiveData绑定xml，数据改变，UI自动会更新
        mBinding.setLifecycleOwner(this);
        //让ViewModel拥有View的生命周期感应
        getLifecycle().addObserver(mViewModel);
        //注入RxLifecycle生命周期
        mViewModel.injectLifecycleOwner(this);
    }

    protected void bindLiveData() {
        //加载对话框显示
        mViewModel.getBaseLiveData().getShowDialogEvent().observe(this, this::showLoadingDialog);
        //加载对话框消失
        mViewModel.getBaseLiveData().getHideDialogEvent().observe(this, v -> hideLoadingDialog());
        //关闭界面
        mViewModel.getBaseLiveData().getFinishEvent().observe(this, v -> finish());
        //关闭上一层
        mViewModel.getBaseLiveData().getOnBackEvent().observe(this, v -> onBackPressed());
    }

    protected void showLoadingDialog(String str) {
        mLoadingDialog = new LoadingDialog.Builder(this)
                .setIconType(LoadingDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(str)
                .create();

        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    protected void hideLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    protected ViewModelProvider getApplicationViewModelProvider() {
        return ((BaseApplication) getApplicationContext()).getAppViewModelProvider(this);
    }

    protected ViewModelProvider getActivityViewModelProvider(AppCompatActivity activity) {
        if (mActivityProvider == null) {
            mActivityProvider = new ViewModelProvider(activity);
        }
        return mActivityProvider;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }


}
