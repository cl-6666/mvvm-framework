package com.cl.mvvm;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.cl.mvvm.utils.Utils;


public class BaseApplication extends Application implements ViewModelStoreOwner {

    private ViewModelStore mAppViewModelStore;
    private ViewModelProvider.Factory mAppViewModelFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppViewModelStore = new ViewModelStore();
        //初始化工具类
        Utils.init(this);
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return mAppViewModelStore;
    }

    public ViewModelProvider getAppViewModelProvider(Activity activity) {
        return new ViewModelProvider((BaseApplication) activity.getApplicationContext(),
                ((BaseApplication) activity.getApplicationContext()).getAppViewModelFactory(activity));
    }

    private ViewModelProvider.Factory getAppViewModelFactory(Activity activity) {
        Application application = getApplication(activity);
        if (mAppViewModelFactory == null) {
            mAppViewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(application);
        }
        return mAppViewModelFactory;
    }

    private Application getApplication(Activity activity) {
        Application application = activity.getApplication();
        if (application == null) {
            throw new IllegalStateException("Your activity/fragment is not yet attached to "
                    + "Application. You can't request ViewModel before onCreate call.");
        }
        return application;
    }

    private Activity getActivity(Fragment fragment) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            throw new IllegalStateException("Can't create ViewModelProvider for detached fragment");
        }
        return activity;
    }
}
