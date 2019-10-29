/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aliosman.privatesms.AppContents;
import com.aliosman.privatesms.Background.DownloadFile;
import com.aliosman.privatesms.Listener.Interfaces.UpdateListener;
import com.aliosman.privatesms.Model.Version;
import com.aliosman.privatesms.R;

import java.io.Serializable;

public class DialogUpdate extends DialogFragment {

    private ProgressBar progressBar;
    private TextView txt_progress, txt_size;
    private CallBack callBack;
    private Version version;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_update_version, container, false);
        callBack = (CallBack) getArguments().getSerializable(AppContents.Update_View_extras_listener);
        version = (Version) getArguments().getSerializable(AppContents.Update_View_extas_version);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //UpdateVersion("https://firebasestorage.googleapis.com/v0/b/privatesms-aeb3c.appspot.com/o/WhatsApp.apk?alt=media&token=d8538beb-88b1-48bf-9b86-c79f435a89e4");
        UpdateVersion(version.getUrl());
    }

    private void UpdateVersion(String url) {
        View view = getView();
        progressBar = view.findViewById(R.id.update_dialog_progress_bar);
        txt_progress = view.findViewById(R.id.update_dialog_progress_txt);
        txt_size = view.findViewById(R.id.update_dialog_size_txt);
        progressBar.setIndeterminate(true);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        new DownloadFile(getContext().getExternalCacheDir(), listener).execute(url);
    }

    UpdateListener listener = new UpdateListener() {
        String size = "";

        @Override
        public void start() {
        }

        @Override
        public void UpdateSize(final String size) {
            this.size = size;
        }

        @Override
        public void UpdateLoad(final long... params) {
            if (progressBar.isIndeterminate())
                progressBar.setIndeterminate(false);
            txt_progress.setText(params[0] + "%");
            progressBar.setProgress(((int) params[0]));
            txt_size.setText(format(params[1], 2) + " / " + format(params[2], 2));
        }

        @Override
        public void Finish() {
            callBack.Execute();
        }
    };

    public String format(double bytes, int digits) {
        String[] dictionary = {"bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        int index = 0;
        for (index = 0; index < dictionary.length; index++) {
            if (bytes < 1024) {
                break;
            }
            bytes = bytes / 1024;
        }
        return String.format("%." + digits + "f", bytes) + " " + dictionary[index];
    }

    public interface CallBack extends Serializable {
        void Execute();
    }
}
