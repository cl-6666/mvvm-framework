package com.cl.test.ext

import android.app.Activity
import android.content.Context
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.cl.test.R
import com.cl.test.util.SettingUtil

/**
 * Loading Dialog 管理（重构版）
 * 
 * 移除了静态 WeakHashMap，避免内存泄漏。
 * 使用 Activity 的 Window DecorView 或 Fragment 的 View 的 Tag 来存储 Dialog 实例。
 */

private const val LOADING_DIALOG_TAG = "LoadingDialog"

/**
 * 打开等待框
 */
fun AppCompatActivity.showLoadingExt(message: String = "请求网络中") {
    if (!this.isFinishing) {
        val dialog = getLoadingDialog(this)
        dialog.show()
        updateDialogMessage(dialog, message)
    }
}

/**
 * 打开等待框
 */
fun Fragment.showLoadingExt(message: String = "请求网络中") {
    activity?.let { activity ->
        if (!activity.isFinishing) {
            val dialog = getLoadingDialog(activity)
            dialog.show()
            updateDialogMessage(dialog, message)
        }
    }
}

/**
 * 关闭等待框
 */
fun Activity.dismissLoadingExt() {
    val dialog = findLoadingDialog(this)
    dialog?.dismiss()
}

/**
 * 关闭等待框
 */
fun Fragment.dismissLoadingExt() {
    activity?.dismissLoadingExt()
}

/**
 * 获取或创建 LoadingDialog
 * 
 * 使用 window.decorView.tag 来存储 Dialog 实例，生命周期绑定到 Activity
 */
private fun getLoadingDialog(activity: FragmentActivity): MaterialDialog {
    val decorView = activity.window.decorView
    var dialog = decorView.getTag(R.id.tag_loading_dialog) as? MaterialDialog
    
    if (dialog == null) {
        dialog = MaterialDialog(activity)
            .cancelable(true)
            .cancelOnTouchOutside(false)
            .cornerRadius(12f)
            .customView(R.layout.dialog_network_loading)
            .lifecycleOwner(activity)
        
        decorView.setTag(R.id.tag_loading_dialog, dialog)
    }
    return dialog
}

/**
 * 查找 LoadingDialog
 */
private fun findLoadingDialog(activity: Activity): MaterialDialog? {
    val decorView = activity.window.decorView
    return decorView.getTag(R.id.tag_loading_dialog) as? MaterialDialog
}

/**
 * 更新 Dialog 消息
 */
private fun updateDialogMessage(dialog: MaterialDialog, message: String) {
    dialog.getCustomView()?.run {
        this.findViewById<TextView>(R.id.loading_tips).text = message
        this.findViewById<ProgressBar>(R.id.progressBar).indeterminateTintList =
            SettingUtil.getOneColorStateList(dialog.context)
    }
}