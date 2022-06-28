package com.microsoft.appcenter.distribute.download.http;

import android.os.AsyncTask;
import java.io.File;
/* loaded from: classes3.dex */
class HttpConnectionRemoveFileTask extends AsyncTask<Void, Void, Void> {
    private final File mFile;

    public HttpConnectionRemoveFileTask(File file) {
        this.mFile = file;
    }

    public Void doInBackground(Void... params) {
        this.mFile.delete();
        return null;
    }
}
