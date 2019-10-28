/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Fragment;


import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyProperties;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliosman.privatesms.R;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class FragmentFingerLock extends BottomSheetDialogFragment implements FingerprintUiHelper.Callback {
    private TextView status;
    private ImageView icon;
    private FingerprintManager.CryptoObject mCryptoObject;
    private FingerprintUiHelper mFingerprintUiHelper;
    private Cipher defaultCipher;
    static FingerprintUiHelper.Callback mcallback;

    public static FragmentFingerLock newInstance(FingerprintUiHelper.Callback callback) {
        mcallback = callback;
        return new FragmentFingerLock();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finger_lock, container, false);
        status = view.findViewById(R.id.fragment_finger_lock_description);
        icon = view.findViewById(R.id.fragment_finger_lock_icon);
        try {
            defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        setStyle(R.style.AppBottomSheetDialogTheme, getTheme());
        mFingerprintUiHelper = new FingerprintUiHelper(
                getActivity().getSystemService(FingerprintManager.class), this);
        mFingerprintUiHelper.startListening(mCryptoObject);
        Button cancel = view.findViewById(R.id.fragment_finger_lock_cancel);
        cancel.setOnClickListener(v -> mcallback.cancel());
        setCancelable(false);
        view.setBackgroundResource(R.drawable.bottom_sheet_background);
        return view;
    }

    @Override
    public void cancel() {
        icon.setImageResource(R.drawable.ic_fingerprint_error);
        status.setText("Çok Fazla deneme yapıldı!!!");
        new Handler().postDelayed(() -> {
            dismiss();
            mcallback.cancel();
        }, 1600);
    }


    @Override
    public void onAuthenticated() {
        icon.setImageResource(R.drawable.ic_fingerprint_success);
        status.setText("Parmak izi doğrulandı");
        new Handler().postDelayed(() -> dismiss(), 1300);
    }

    @Override
    public void onError() {
        Log.e("OnError", "OnError");
        icon.setImageResource(R.drawable.ic_fingerprint_error);
        status.setText("Parmak izi doğrulanamadı");
        new Handler().postDelayed(() -> {
            icon.setImageResource(R.drawable.ic_fp_40px);
            status.setText("Sensöre Dokunun!!!");
            mFingerprintUiHelper.startListening(mCryptoObject);
        }, 1600);
    }
}
