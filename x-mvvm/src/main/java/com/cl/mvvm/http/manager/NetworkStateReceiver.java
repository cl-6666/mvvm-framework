package com.cl.mvvm.http.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;


import com.cl.mvvm.utils.ToastUtils;

import java.util.Objects;

/**
 * 网络变化监听接收者
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), ConnectivityManager.CONNECTIVITY_ACTION)) {
//            if (!NetworkUtils.isConnected()) {
//                ToastUtils.showShort("网络异常");
//            }
        }
    }

}
