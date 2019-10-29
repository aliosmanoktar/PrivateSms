/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Listener.Interfaces;

public interface UpdateListener {
    void start();

    void UpdateSize(String size);

    void UpdateLoad(long... params);

    void Finish();
}