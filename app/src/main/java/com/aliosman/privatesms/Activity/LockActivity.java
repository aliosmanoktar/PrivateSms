/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.aliosman.privatesms.AppContents;
import com.aliosman.privatesms.Fragment.FragmentFingerLock;
import com.aliosman.privatesms.Fragment.FragmentPasswordSelect;
import com.aliosman.privatesms.Fragment.FragmentPatternLock;
import com.aliosman.privatesms.Fragment.FragmentPinLock;
import com.aliosman.privatesms.Listener.Interfaces.PasswordListener;
import com.aliosman.privatesms.R;
import com.aliosman.privatesms.Settings;

import static com.aliosman.privatesms.Fragment.FragmentPasswordSelect.CallBack;

public class LockActivity extends AppCompatActivity {
    private String TAG = getClass().getName();
    private Boolean close = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        SetupScreen(new Settings(this));
    }

    private void SetupScreen(Settings settings) {
        Settings.PasswordType passwordType = Settings.PasswordType.getValue(settings.getInt(settings.password_type));
        switch (passwordType) {
            case Pin:
                ReplaceTransaction(new FragmentPinLock());
                break;
            case Pattern:
                ReplaceTransaction(new FragmentPatternLock());
                break;
            case FingerPrint:
                ShowFingerPrint();
                break;
            case none:
                ShowSelect();
                break;
        }
    }

    private void ShowSelect() {
        BottomSheetDialogFragment fr = new FragmentPasswordSelect();
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppContents.Password_view_extras_listener, (CallBack) id -> {
            Settings settings = new Settings(getBaseContext());
            settings.setInt(settings.password_type, id);
            SetupScreen(settings);
        });
        fr.setArguments(bundle);
        fr.show(getSupportFragmentManager(), "tag");
    }

    private void ShowFingerPrint() {
        BottomSheetDialogFragment fr = new FragmentFingerLock();
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppContents.Password_view_extras_listener, passwordListener);
        fr.setArguments(bundle);
        fr.show(getSupportFragmentManager(), "tag");
    }

    private PasswordListener passwordListener = new PasswordListener() {
        @Override
        public void onAuthenticated() {
            Log.e(TAG, "onAuthenticated: ");
            close = true;
            startActivity(new Intent(getBaseContext(), PrivateActivity.class));
            runOnUiThread(() -> {
                for (Fragment fragment : getSupportFragmentManager().getFragments())
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            });
        }

        @Override
        public void onError() {

        }

        @Override
        public void cancel() {
            finish();
        }
    };

    private void ReplaceTransaction(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppContents.Password_view_extras_listener, passwordListener);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_lock_framelayout, fragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (close)
            finish();
    }
}