/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Fragment;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aliosman.privatesms.AppContents;
import com.aliosman.privatesms.R;

import java.io.Serializable;

public class FragmentPasswordSelect extends BottomSheetDialogFragment {
    private CallBack callBack;
    private Button btn_pattern, btn_pin, btn_fingerprint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password_select, container, false);
        btn_pattern = view.findViewById(R.id.fragment_password_select_pattern);
        btn_pin = view.findViewById(R.id.fragment_password_select_pin);
        btn_fingerprint = view.findViewById(R.id.fragment_password_select_fingerprint);
        callBack = (CallBack) getArguments().getSerializable(AppContents.Password_view_extras_listener);
        btn_fingerprint.setOnClickListener(password_select);
        btn_pin.setOnClickListener(password_select);
        btn_pattern.setOnClickListener(password_select);
        setCancelable(false);
        return view;
    }

    View.OnClickListener password_select = v -> {
        int tag = Integer.parseInt(v.getTag().toString());
        callBack.onSelect(tag);
        dismissAllowingStateLoss();
    };

    public interface CallBack extends Serializable {
        void onSelect(int id);
    }
}
