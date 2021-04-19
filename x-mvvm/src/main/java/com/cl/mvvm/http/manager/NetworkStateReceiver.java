package com.cl.mvvm.http.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;


import com.cl.mvvm.utils.NetworkUtils;
import com.cl.mvvm.utils.ToastUtils;
import com.cl.mvvm.utils.Utils;

import java.util.Objects;

/**
 * 网络变化监听接收者
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), ConnectivityManager.CONNECTIVITY_ACTION)) {
            if (!NetworkUtils.isConnected(Utils.getContext())) {
                ToastUtils.showShort("网络异常");
                Log.e("TAG","网络异常。。。。");
            }else {
                Log.e("TAG","网络正常。。。。");
            }
        }
    }

}
