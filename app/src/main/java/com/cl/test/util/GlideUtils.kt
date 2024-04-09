package com.cl.test.util

import android.content.Context
import android.widget.ImageView
import coil.load
import com.cl.test.R

/**
 * name：cl
 * date：2023/4/13
 * desc：图片加载工具类
 */
class GlideUtils {

    companion object {

        //占位图
//    var placeholderImage: Int = R.drawable.ic_default_image

        //*** 错误图
        var errorImage: Int = R.mipmap.ic_launcher


        fun loadImageGif(context: Context, res: Int, imageView: ImageView) {
        }


        /**
         * 加载图片(String地址)
         */
        fun loadImage(context: Context, url: String, imageView: ImageView) {
            imageView.load(url) {
                placeholder(errorImage)
                error(R.mipmap.ic_launcher)
            }
        }
    }
}