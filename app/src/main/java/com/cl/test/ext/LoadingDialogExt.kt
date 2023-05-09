package com.cl.test.ext

import android.app.Activity
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.cl.test.R
import com.cl.test.util.SettingUtil


//loading框
private var loadingDialog: MaterialDialog? = null

/**
 * 打开等待框
 */
fun AppCompatActivity.showLoadingExt(message: String = "请求网络中") {
    if (!this.isFinishing) {
        if (loadingDialog == null) {
            loadingDialog = MaterialDialog(this)
                .cancelable(true)
                .cancelOnTouchOutside(false)
                .cornerRadius(12f)
                .customView(R.layout.dialog_network_loading)
                .lifecycleOwner(this)
            loadingDialog?.getCustomView()?.run {
                this.findViewById<TextView>(R.id.loading_tips).text = message
                this.findViewById<ProgressBar>(R.id.progressBar).indeterminateTintList =
                    SettingUtil.getOneColorStateList(this@showLoadingExt)
            }
        }
        loadingDialog?.show()
    }
}

/**
 * 打开等待框
 */
fun Fragment.showLoadingExt(message: String = "请求网络中") {
    activity?.let {
        if (!it.isFinishing) {
            if (loadingDialog == null) {
                loadingDialog = MaterialDialog(it)
                    .cancelable(true)
                    .cancelOnTouchOutside(false)
                    .cornerRadius(12f)
                    .customView(R.layout.dialog_network_loading)
                    .lifecycleOwner(this)
                loadingDialog?.getCustomView()?.run {
                    this.findViewById<TextView>(R.id.loading_tips).text = message
                    this.findViewById<ProgressBar>(R.id.progressBar).indeterminateTintList =
                        SettingUtil.getOneColorStateList(it)
                }
            }
            loadingDialog?.show()
        }
    }
}

/**
 * 关闭等待框
 */
fun Activity.dismissLoadingExt() {
    loadingDialog?.dismiss()
    loadingDialog = null
}

/**
 * 关闭等待框
 */
fun Fragment.dismissLoadingExt() {
    loadingDialog?.dismiss()
    loadingDialog = null
}
