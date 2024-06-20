package com.cl.test.util

import android.content.Context
import android.widget.ImageView
import coil.ImageLoader
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation

/**
 * name：cl
 * date：2023/4/13
 * desc：图片加载工具类
 */
object ImageLoadingUtils {

    private lateinit var imageLoader: ImageLoader

    /**
     * 初始化ImageLoader
     * 在Application类中调用
     */
    fun init(context: Context) {
        imageLoader = ImageLoader.Builder(context)
            .crossfade(true) // 渐变动画
            .build()
    }

    /**
     * 加载图片
     * @param url 图片URL
     * @param imageView ImageView控件
     */
    fun loadImage(url: String, imageView: ImageView) {
        val request = ImageRequest.Builder(imageView.context)
            .data(url)
            .target(imageView)
            .build()
        imageLoader.enqueue(request)
    }

    /**
     * 加载带有占位符和错误占位符的图片
     * @param url 图片URL
     * @param imageView ImageView控件
     * @param placeholder 占位符资源ID
     * @param error 错误占位符资源ID
     */
    fun loadImageWithPlaceholder(url: String, imageView: ImageView, placeholder: Int, error: Int) {
        val request = ImageRequest.Builder(imageView.context)
            .data(url)
            .placeholder(placeholder)
            .error(error)
            .target(imageView)
            .build()
        imageLoader.enqueue(request)
    }

    /**
     * 加载圆形图片
     * @param url 图片URL
     * @param imageView ImageView控件
     * @param placeholder 占位符资源ID
     * @param error 错误占位符资源ID
     */
    fun loadCircularImage(url: String, imageView: ImageView, placeholder: Int, error: Int) {
        val request = ImageRequest.Builder(imageView.context)
            .data(url)
            .placeholder(placeholder)
            .error(error)
            .transformations(CircleCropTransformation())
            .target(imageView)
            .build()
        imageLoader.enqueue(request)
    }
}