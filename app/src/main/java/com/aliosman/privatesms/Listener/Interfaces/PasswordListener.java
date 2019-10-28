/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Listener.Interfaces;

import java.io.Serializable;

public interface PasswordListener extends Serializable {

    void onAuthenticated();

    void onError();

    void cancel();
}
