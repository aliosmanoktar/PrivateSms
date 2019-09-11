/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Listener.Interfaces;

import java.util.List;

public interface RecylerSelectedListener<T> {
    void Selected(int count,int position);
    void SelectedEnded(List<T> items);
    void SelectedStart();
}
