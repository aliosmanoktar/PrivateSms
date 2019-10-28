/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Fragment;

import android.hardware.fingerprint.FingerprintManager;
import android.util.Log;

import com.aliosman.privatesms.Listener.Interfaces.PasswordListener;

public class FingerprintUiHelper extends FingerprintManager.AuthenticationCallback {
    private final FingerprintManager mFingerprintManager;
    private final PasswordListener mCallback;
    private final String TAG = getClass().getName();

    FingerprintUiHelper(FingerprintManager fingerprintManager, PasswordListener callback) {
        mFingerprintManager = fingerprintManager;
        mCallback = callback;
    }

    public boolean isFingerprintAuthAvailable() {
        return mFingerprintManager.isHardwareDetected()
                /**Kayıtlı parmak izi olup olmadığını kontrol et ve ona göre uyarı göster**/
                && mFingerprintManager.hasEnrolledFingerprints();
    }

    public void startListening(FingerprintManager.CryptoObject cryptoObject) {
        if (!isFingerprintAuthAvailable()) {
            return;
        }
        mFingerprintManager
                .authenticate(cryptoObject, null, 0 /* flags */, this, null);
    }


    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        Log.e("Error" + errMsgId, errString.toString());
        if (errMsgId == 7)
            mCallback.cancel();
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {

    }

    @Override
    public void onAuthenticationFailed() {
        Log.e("FingerPrintUiHelper", "OnFailed");
        mCallback.onError();
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        mCallback.onAuthenticated();
        Log.e("FingerPrintUiHelper", "Tanımlandı");
        mCallback.onAuthenticated();
    }
}