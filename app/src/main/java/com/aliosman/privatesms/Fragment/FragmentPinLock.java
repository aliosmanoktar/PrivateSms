/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliosman.privatesms.AppContents;
import com.aliosman.privatesms.Listener.Interfaces.PasswordListener;
import com.aliosman.privatesms.R;
import com.aliosman.privatesms.Settings;

public class FragmentPinLock extends Fragment {
    private String TAG = getClass().getName();
    private String PASSWORD_NUMBER_SYMBOL = "●";
    private StringBuilder password_builder = new StringBuilder();
    private TextView password_text;
    private TextView num_1, num_2, num_3, num_4, num_5, num_6, num_7, num_8, num_9, num_0;
    private ImageView backspace, save;
    private Settings settings;
    private String password;
    private boolean anim = false;
    private Animation animation;
    private PasswordListener passwordListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pin_lock, container, false);
        settings = new Settings(getContext());
        password = settings.getString(settings.pin_password);
        password_text = view.findViewById(R.id.fragment_pin_lock_Text);
        num_0 = view.findViewById(R.id.fragment_pin_lock_number_0);
        num_1 = view.findViewById(R.id.fragment_pin_lock_number_1);
        num_2 = view.findViewById(R.id.fragment_pin_lock_number_2);
        num_3 = view.findViewById(R.id.fragment_pin_lock_number_3);
        num_4 = view.findViewById(R.id.fragment_pin_lock_number_4);
        num_5 = view.findViewById(R.id.fragment_pin_lock_number_5);
        num_6 = view.findViewById(R.id.fragment_pin_lock_number_6);
        num_7 = view.findViewById(R.id.fragment_pin_lock_number_7);
        num_8 = view.findViewById(R.id.fragment_pin_lock_number_8);
        num_9 = view.findViewById(R.id.fragment_pin_lock_number_9);
        backspace = view.findViewById(R.id.fragment_pin_lock_ic_backspace);
        save = view.findViewById(R.id.fragment_pin_lock_save);

        num_0.setOnClickListener(number_click_listener);
        num_1.setOnClickListener(number_click_listener);
        num_2.setOnClickListener(number_click_listener);
        num_3.setOnClickListener(number_click_listener);
        num_4.setOnClickListener(number_click_listener);
        num_5.setOnClickListener(number_click_listener);
        num_6.setOnClickListener(number_click_listener);
        num_7.setOnClickListener(number_click_listener);
        num_8.setOnClickListener(number_click_listener);
        num_9.setOnClickListener(number_click_listener);
        backspace.setOnClickListener(backspace_click_listener);
        if (password != null)
            save.setVisibility(View.INVISIBLE);
        else
            save.setOnClickListener(save_click_listener);
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        animation.setAnimationListener(listener);
        passwordListener = (PasswordListener) getArguments().getSerializable(AppContents.Password_view_extras_listener);
        return view;
    }

    private Animation.AnimationListener listener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            anim = false;
            password_text.setText("");
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    private View.OnClickListener number_click_listener = v -> {
        if (anim)
            return;
        password_builder.append(((TextView) v).getText());
        password_text.setText(getPasswordTex());
        if (password != null && password.length() <= password_builder.length()) {
            if (password_builder.toString().equals(password)) {
                Log.e(TAG, ": Şifre Doğru");
                passwordListener.onAuthenticated();
            } else {
                password_builder.setLength(0);
                Log.e(TAG, ": Şifre Yalnış");
                anim = true;
                password_text.startAnimation(animation);
                Titret();
            }
        }
    };

    private View.OnClickListener backspace_click_listener = v -> {
        if (anim)
            return;
        if (password_builder.length() == 0) {
            Titret();
            return;
        }
        password_builder.deleteCharAt(password_builder.length() - 1);
        password_text.setText(getPasswordTex());
    };

    private View.OnClickListener save_click_listener = v -> {

        if (password_builder.length() < 4) {
            Titret();
            return;
        } else {
            settings.setString(settings.pin_password, password_builder.toString());
            password = settings.getString(settings.pin_password);
            save.setVisibility(View.INVISIBLE);
            password_text.setText("");
        }
    };

    private void Titret() {

    }

    private String getPasswordTex() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < password_builder.length(); i++)
            s.append(PASSWORD_NUMBER_SYMBOL);
        return s.toString();
    }
}
