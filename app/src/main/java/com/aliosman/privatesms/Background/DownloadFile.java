/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Background;

import android.os.AsyncTask;
import android.util.Log;

import com.aliosman.privatesms.Listener.Interfaces.UpdateListener;

import java.io.File;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class DownloadFile extends AsyncTask<String, Long, String> {

    private BufferedSink sink = null;
    private BufferedSource source = null;
    private File destFile;
    private UpdateListener listener;
    private String TAG = getClass().getName();

    public DownloadFile(File destFile, UpdateListener listener) {
        this.destFile = destFile;
        if (!destFile.exists())
            destFile.mkdirs();
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.start();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            destFile = new File(destFile.getPath() + File.separator + "app.apk");
            destFile.createNewFile();
            Request request = new Request.Builder().url(strings[0]).build();
            Response response = new OkHttpClient.Builder().build().newCall(request).execute();
            ResponseBody body = response.body();
            long contentLength = body.contentLength();
            source = body.source();
            sink = Okio.buffer(Okio.sink(destFile));
            Buffer sinkBuffer = sink.buffer();
            long totalBytesRead = 0;
            int bufferSize = 8 * 1024;
            long bytesRead;
            while ((bytesRead = source.read(sinkBuffer, bufferSize)) != -1) {
                sink.emit();
                totalBytesRead += bytesRead;
                int progress = (int) ((totalBytesRead * 100) / contentLength);
                publishProgress((long) progress, totalBytesRead, contentLength);
            }
            sink.flush();
            Log.e(TAG, "doInBackground: " + destFile.getAbsolutePath());
        } catch (Exception ex) {
            Log.e(TAG, "doInBackground: " + ex.getMessage());
        } finally {
            Util.closeQuietly(sink);
            Util.closeQuietly(source);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        listener.UpdateLoad(values[0], values[1], values[2]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        listener.Finish();
        Log.e(TAG, "onPostExecute: Finish");
    }
}
