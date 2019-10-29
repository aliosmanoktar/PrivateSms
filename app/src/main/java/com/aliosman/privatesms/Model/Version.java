/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Model;

import java.io.Serializable;

public class Version implements Serializable {
    private int VersionCode;
    private String Version;
    private String Url;

    public Version() {
        VersionCode = 0;
    }

    public Version(int versionCode, String version, String url) {
        this.VersionCode = versionCode;
        Version = version;
        Url = url;
    }

    public int getVersionCode() {
        return VersionCode;
    }

    public void setVersionCode(int versionCode) {
        this.VersionCode = versionCode;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
