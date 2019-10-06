/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliosman.privatesms.Model.Contact;

public class AvatarView extends FrameLayout {

    private Contact contact = null;

    public AvatarView(@NonNull Context context) {
        this(context, null);
        Init(context, null);
    }

    public AvatarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        //Init(context,attrs);
    }

    public AvatarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs);
    }

    private void Init(Context context, AttributeSet atts) {
        inflate(context, R.layout.avatar_view, this);
        //UpdateView();
    }

    private void UpdateView() {
        Drawable d = getResources().getDrawable(R.drawable.circle);
        d.setTint(getResources().getColor(R.color.tools_theme));
        setBackground(d);
        TextView txt = findViewById(R.id.avatar_view_initial);
        txt.setTextColor(getResources().getColor(R.color.white));
        ImageView icon = findViewById(R.id.avatatar_view_icon);
        if (contact.getLookupKey().isEmpty()) {
            txt.setVisibility(GONE);
            icon.setVisibility(VISIBLE);
        } else {
            txt.setText(contact.getNameText().substring(0, 1).toUpperCase());
            txt.setVisibility(VISIBLE);
            icon.setVisibility(GONE);
        }
    }

    public void SetUser(Contact contact) {
        this.contact = contact;
        UpdateView();
    }
}
