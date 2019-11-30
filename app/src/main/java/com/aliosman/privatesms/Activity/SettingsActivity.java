/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.aliosman.privatesms.AppContents;
import com.aliosman.privatesms.R;
import com.aliosman.privatesms.Settings;

public class SettingsActivity extends AppCompatActivity {
    private boolean isPrivate = false;
    private View hidden_visibilty;
    private TextView reset_password;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        try {
            isPrivate = getIntent().getExtras().getBoolean(AppContents.isPrivate, false);
        } catch (NullPointerException ex) {
            isPrivate = false;
        }
        InitViews();
        InitListener();
    }

    private void InitViews() {
        toolbar = findViewById(R.id.setting_activity_toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        setSupportActionBar(toolbar);
        hidden_visibilty = findViewById(R.id.activity_settings_private_layout);
        reset_password = findViewById(R.id.activity_settings_reset_password);
        if (isPrivate)
            hidden_visibilty.setVisibility(View.VISIBLE);
    }

    private void InitListener() {
        reset_password.setOnClickListener(reset_password_listener);
        toolbar.setNavigationOnClickListener(back_listener);
    }

    private View.OnClickListener back_listener = v -> finish();
    private View.OnClickListener reset_password_listener = v -> {
        Settings settings = new Settings(this);
        settings.setInt(settings.password_type, -1);
        Intent i = new Intent(this, LockActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(AppContents.only_select_password, true);
        i.putExtras(bundle);
        startActivity(i);
    };
}
