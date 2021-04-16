package com.cl.mvvm.binding.command;

/**
 * 没有参数的函数
 * @param <T>
 */
public interface BindingFunction<T> {
    /**
     * Represents a function with zero arguments.
     *
     * @return T
     */
    T call();
}
