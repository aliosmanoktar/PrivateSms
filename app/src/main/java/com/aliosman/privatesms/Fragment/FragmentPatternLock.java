/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliosman.privatesms.R;
import com.aliosman.privatesms.Settings;
import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

public class FragmentPatternLock extends Fragment {
    private Settings settings;
    private PatternLockView lockView;
    private String patternPassword;
    private String TAG = getClass().getName();
    private TextView title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pattern_lock, container, false);
        lockView = view.findViewById(R.id.pattern_lock_activity_pattern_view);
        title = view.findViewById(R.id.pattern_lock_activity_title);
        lockView.addPatternLockListener(patternLockViewListener);
        settings = new Settings(getContext());
        patternPassword = settings.getString(settings.pattern_password);
        if (patternPassword == null)
            title.setText("Yeni Şifre Belirle");
        return view;
    }

    private PatternLockViewListener patternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {

        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {

        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            String patternString = PatternLockUtils.patternToString(lockView, pattern);
            if (patternPassword == null) {
                Log.e(TAG, "onComplete: pasword kaydedildi");
                settings.setString(settings.pattern_password, patternString);
                patternPassword = settings.getString(settings.pattern_password);
                lockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
            } else {
                if (patternString.equals(patternPassword)) {
                    Log.e(TAG, "onComplete: password Doğru");
                    lockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                } else {
                    Log.e(TAG, "onComplete: password Hatalı");
                    lockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                }
            }
            ClearPattern();
        }

        @Override
        public void onCleared() {
            Log.e(TAG, "onCleared: ");
        }
    };

    private void ClearPattern() {
        new Handler().postDelayed(() -> lockView.clearPattern(), 700);
    }
}
