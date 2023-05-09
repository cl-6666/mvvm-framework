package com.cl.test.ext


import androidx.recyclerview.widget.RecyclerView


/**
 * 作者　: cl
 * 时间　: 2020/2/20
 * 描述　:项目中自定义类的拓展函数
 */


//绑定普通的Recyclerview
fun RecyclerView.init(
    layoutManger: RecyclerView.LayoutManager,
    bindAdapter: RecyclerView.Adapter<*>,
    isScroll: Boolean = true
): RecyclerView {
    layoutManager = layoutManger
    setHasFixedSize(true)
    adapter = bindAdapter
    isNestedScrollingEnabled = isScroll
    return this

}