package com.googlecode.mp4parser.util;

import android.util.Log;
/* loaded from: classes.dex */
public class AndroidLogger extends Logger {
    String name;

    public AndroidLogger(String str) {
        this.name = str;
    }

    @Override // com.googlecode.mp4parser.util.Logger
    public void logDebug(String str) {
        Log.d("isoparser", String.valueOf(this.name) + ":" + str);
    }
}
