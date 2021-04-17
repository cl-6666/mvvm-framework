package com.cl.test.mian;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.cl.mvvm.base.BaseViewModel;
import com.cl.mvvm.bus.RxBus;
import com.cl.mvvm.bus.RxSubscriptions;
import com.cl.mvvm.bus.event.BaseEvent;
import com.cl.mvvm.bus.event.EventType;

import io.reactivex.disposables.Disposable;

/**
 * 项目：mvvm-framework
 * 作者：Arry
 * 创建日期：4/16/21
 * 描述：
 * 修订历史：
 */
public class MainViewModel extends BaseViewModel {


    private Disposable mSubscription;

    public MutableLiveData<Boolean> enabled = new MutableLiveData<>(true);

    public MutableLiveData<String> name = new MutableLiveData<>("你好呀。。。");


    public MainViewModel(@NonNull Application application) {
        super(application);
    }



    @Override
    public void injectRxBus() {
        super.injectRxBus();
        mSubscription = RxBus.getDefault()
                .observable(BaseEvent.class)
                .subscribe(data -> {
                    if (data.type == EventType.LOGOUT) {

                        Log.e("TAG","执行了。。。。。。");
                    }
                });
        RxSubscriptions.add(mSubscription);
    }

    @Override
    public void removeRxBus() {
        super.removeRxBus();
        RxSubscriptions.remove(mSubscription);
    }
}
