package com.cl.mvvm.bus.event;


/**
 * BaseEvent
 * @param <T>
 */
public class BaseEvent<T> {
    public EventType type;
    public T data;
}
