package com.aliosman.privatesms.Listener;

public abstract class RecyclerViewListener<T> {

    abstract void Onclick(T item);

    public void OnLongClick(T item){}
}
