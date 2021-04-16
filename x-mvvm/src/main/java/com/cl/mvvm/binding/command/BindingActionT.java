package com.cl.mvvm.binding.command;

/**
 * 项目：mvvm-framework
 * 作者：Arry
 * 创建日期：4/16/21
 * 描述：
 * 修订历史：
 */
public interface BindingActionT<T> {

    /**
     * A one-argument action.
     *
     * @param t
     */
    void call(T t);
}
