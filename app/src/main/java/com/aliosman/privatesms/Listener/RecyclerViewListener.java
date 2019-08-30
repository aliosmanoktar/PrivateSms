/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Listener;

public abstract class RecyclerViewListener<T> {

    public abstract void Onclick(T item);

    public void OnLongClick(T item){};
}
