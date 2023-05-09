package com.cl.test.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
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
//            var strategy = DiskCacheStrategy.NONE
//            val options =
//                RequestOptions().centerCrop().placeholder(R.color.white).error(R.color.white)
//                    .dontAnimate().diskCacheStrategy(strategy)
//            Glide.with(context).asGif().load(res).apply(options).into(imageView)
            Glide.with(context)
                .asGif()
                .load(res)
                .error(errorImage)//异常时候显示的图片
                //              .placeholder(R.mipmap.img_default)//加载成功前显示的图片
                //              .fallback(R.mipmap.img_default)//url为空的时候,显示的图片
                //              .skipMemoryCache(SKIP_MEMORY_CACHE)
                .into(imageView!!)
        }


        /**
         * 加载图片(String地址)
         */
        fun loadImage(context: Context, url: String, imageView: ImageView) {
            var strategy = DiskCacheStrategy.NONE
            if (url.startsWith("http://") || url.startsWith("https://")) {
                strategy = DiskCacheStrategy.RESOURCE
            }
            val options =
                RequestOptions().centerCrop().placeholder(R.color.white).error(R.color.white)
                    .dontAnimate().diskCacheStrategy(strategy)
            Glide.with(context).load(url).apply(options).into(imageView)

        }

        /**
         * 加载图片(int资源地址)
         */
        fun loadImage(context: Context, res: Int, imageView: ImageView) {
            var strategy = DiskCacheStrategy.NONE
            val options =
                RequestOptions().centerCrop().placeholder(R.color.white).error(R.color.white)
                    .dontAnimate().diskCacheStrategy(strategy)
            Glide.with(context).load(res).apply(options).into(imageView)
        }

        /**
         * 加载图片(uri)
         */
        fun loadImage(context: Context, uri: Uri, imageView: ImageView) {
            var strategy = DiskCacheStrategy.RESOURCE
            val options =
                RequestOptions().centerCrop().placeholder(R.color.white).error(R.color.white)
                    .dontAnimate().diskCacheStrategy(strategy)
            Glide.with(context).load(uri).apply(options).into(imageView)
        }

        /**
         * 加载图片(bitmap)
         */
        fun loadImage(context: Context, bitmap: Bitmap, imageView: ImageView) {
            var strategy = DiskCacheStrategy.RESOURCE
            val options =
                RequestOptions().centerCrop().placeholder(R.color.white).error(R.color.white)
                    .dontAnimate().diskCacheStrategy(strategy)
            Glide.with(context).load(bitmap).apply(options).into(imageView)
        }

        /**
         * 加载图片(String地址)---指定宽高
         */
        fun loadImage(
            context: Context,
            url: String,
            imageView: ImageView,
            width: Int,
            height: Int
        ) {
            var strategy = DiskCacheStrategy.NONE
            if (url.startsWith("http://") || url.startsWith("https://")) {
                strategy = DiskCacheStrategy.RESOURCE
            }
            val options =
                RequestOptions().centerCrop().placeholder(R.color.white).error(R.color.white)
                    .dontAnimate().override(width, height).diskCacheStrategy(strategy)
            Glide.with(context).load(url).apply(options).into(imageView)
        }

        /**
         * 加载圆形图片
         */
        fun loadCircleImage(context: Context, url: String, imageView: ImageView) {
            var strategy = DiskCacheStrategy.NONE
            if (url.startsWith("http://") || url.startsWith("https://")) {
                strategy = DiskCacheStrategy.RESOURCE
            }
            val options = RequestOptions().centerCrop().circleCrop().placeholder(R.color.white)
                .error(R.color.white).dontAnimate().diskCacheStrategy(strategy)
            Glide.with(context).load(url).apply(options).into(imageView)

        }
    }
}