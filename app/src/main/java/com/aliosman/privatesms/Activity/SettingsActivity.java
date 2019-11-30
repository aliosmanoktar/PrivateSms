/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.aliosman.privatesms.AppContents;
import com.aliosman.privatesms.R;

public class SettingsActivity extends AppCompatActivity {
    private boolean isPrivate = false;
    private View hidden_visibilty;
    private TextView reset_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        try {
            isPrivate = getIntent().getExtras().getBoolean(AppContents.isPrivate, false);
        } catch (NullPointerException ex) {
            isPrivate = false;
        }
        hidden_visibilty = findViewById(R.id.activity_settings_private_layout);
        if (isPrivate)
            hidden_visibilty.setVisibility(View.VISIBLE);
    }
}
