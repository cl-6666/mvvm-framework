package com.maxvision.mvvm.util

import android.text.TextUtils
import android.util.Log
import com.maxvision.mvvm.ext.util.jetpackMvvmLog
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * 作者　: cl
 * 时间　: 2023/04/12
 * 描述　:
 */
object LogUtils {
    private const val DEFAULT_TAG = "JetpackMvvm"

    /** 创建线程池来打印日志，解决出现大日志阻塞线程的情况  */
    private val EXECUTOR = ThreadPoolExecutor(
        1, 1,
        0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue(),
        Executors.defaultThreadFactory(), ThreadPoolExecutor.DiscardPolicy()
    )

    fun debugInfo(tag: String?, msg: String?) {
        if (!jetpackMvvmLog || TextUtils.isEmpty(msg)) {
            return
        }
        EXECUTOR.execute {
            Log.d(tag, msg!!)
        }
    }

    fun debugInfo(msg: String?) {
        debugInfo(
            DEFAULT_TAG,
            msg
        )
    }

    fun warnInfo(tag: String?, msg: String?) {
        if (!jetpackMvvmLog || TextUtils.isEmpty(msg)) {
            return
        }
        Log.w(tag, msg!!)
    }

    fun warnInfo(msg: String?) {
        warnInfo(
            DEFAULT_TAG,
            msg
        )
    }

    /**
     * 这里使用自己分节的方式来输出足够长度的 message
     *
     * @param tag 标签
     * @param msg 日志内容
     */
    fun debugLongInfo(tag: String?, msg: String) {
        var msg = msg
        if (!jetpackMvvmLog || TextUtils.isEmpty(msg)) {
            return
        }
        msg = msg.trim { it <= ' ' }
        var index = 0
        val maxLength = 3500
        var sub: String
        while (index < msg.length) {
            sub = if (msg.length <= index + maxLength) {
                msg.substring(index)
            } else {
                msg.substring(index, index + maxLength)
            }
            index += maxLength
            Log.d(tag, sub.trim { it <= ' ' })
        }
    }

    fun debugLongInfo(msg: String) {
        debugLongInfo(
            DEFAULT_TAG,
            msg
        )
    }

}