package com.google.firebase.remoteconfig.internal;

import android.content.Context;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class ConfigStorageClient {
    private static final String JSON_STRING_ENCODING = "UTF-8";
    private static final Map<String, ConfigStorageClient> clientInstances = new HashMap();
    private final Context context;
    private final String fileName;

    private ConfigStorageClient(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    public synchronized Void write(ConfigContainer container) throws IOException {
        FileOutputStream outputStream = this.context.openFileOutput(this.fileName, 0);
        outputStream.write(container.toString().getBytes("UTF-8"));
        outputStream.close();
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x003a A[Catch: all -> 0x003e, TRY_LEAVE, TryCatch #3 {, blocks: (B:6:0x0028, B:11:0x0030, B:12:0x0033, B:18:0x003a, B:4:0x0002), top: B:23:0x0002, inners: #3 }] */
    @javax.annotation.Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized com.google.firebase.remoteconfig.internal.ConfigContainer read() throws java.io.IOException {
        /*
            r5 = this;
            monitor-enter(r5)
            r0 = 0
            android.content.Context r1 = r5.context     // Catch: java.lang.Throwable -> L2d java.io.FileNotFoundException -> L34 org.json.JSONException -> L36
            java.lang.String r2 = r5.fileName     // Catch: java.lang.Throwable -> L2d java.io.FileNotFoundException -> L34 org.json.JSONException -> L36
            java.io.FileInputStream r1 = r1.openFileInput(r2)     // Catch: java.lang.Throwable -> L2d java.io.FileNotFoundException -> L34 org.json.JSONException -> L36
            r0 = r1
            int r1 = r0.available()     // Catch: java.lang.Throwable -> L2d java.io.FileNotFoundException -> L34 org.json.JSONException -> L36
            byte[] r1 = new byte[r1]     // Catch: java.lang.Throwable -> L2d java.io.FileNotFoundException -> L34 org.json.JSONException -> L36
            r2 = 0
            int r3 = r1.length     // Catch: java.lang.Throwable -> L2d java.io.FileNotFoundException -> L34 org.json.JSONException -> L36
            r0.read(r1, r2, r3)     // Catch: java.lang.Throwable -> L2d java.io.FileNotFoundException -> L34 org.json.JSONException -> L36
            java.lang.String r2 = new java.lang.String     // Catch: java.lang.Throwable -> L2d java.io.FileNotFoundException -> L34 org.json.JSONException -> L36
            java.lang.String r3 = "UTF-8"
            r2.<init>(r1, r3)     // Catch: java.lang.Throwable -> L2d java.io.FileNotFoundException -> L34 org.json.JSONException -> L36
            org.json.JSONObject r3 = new org.json.JSONObject     // Catch: java.lang.Throwable -> L2d java.io.FileNotFoundException -> L34 org.json.JSONException -> L36
            r3.<init>(r2)     // Catch: java.lang.Throwable -> L2d java.io.FileNotFoundException -> L34 org.json.JSONException -> L36
            com.google.firebase.remoteconfig.internal.ConfigContainer r4 = com.google.firebase.remoteconfig.internal.ConfigContainer.copyOf(r3)     // Catch: java.lang.Throwable -> L2d java.io.FileNotFoundException -> L34 org.json.JSONException -> L36
            if (r0 == 0) goto L2b
            r0.close()     // Catch: java.lang.Throwable -> L3e
        L2b:
            monitor-exit(r5)
            return r4
        L2d:
            r1 = move-exception
            if (r0 == 0) goto L33
            r0.close()     // Catch: java.lang.Throwable -> L3e
        L33:
            throw r1     // Catch: java.lang.Throwable -> L3e
        L34:
            r1 = move-exception
            goto L37
        L36:
            r1 = move-exception
        L37:
            r2 = 0
            if (r0 == 0) goto L41
            r0.close()     // Catch: java.lang.Throwable -> L3e
            goto L41
        L3e:
            r0 = move-exception
            monitor-exit(r5)
            throw r0
        L41:
            monitor-exit(r5)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.firebase.remoteconfig.internal.ConfigStorageClient.read():com.google.firebase.remoteconfig.internal.ConfigContainer");
    }

    public synchronized Void clear() {
        this.context.deleteFile(this.fileName);
        return null;
    }

    public static synchronized ConfigStorageClient getInstance(Context context, String fileName) {
        ConfigStorageClient configStorageClient;
        synchronized (ConfigStorageClient.class) {
            Map<String, ConfigStorageClient> map = clientInstances;
            if (!map.containsKey(fileName)) {
                map.put(fileName, new ConfigStorageClient(context, fileName));
            }
            configStorageClient = map.get(fileName);
        }
        return configStorageClient;
    }

    public static synchronized void clearInstancesForTest() {
        synchronized (ConfigStorageClient.class) {
            clientInstances.clear();
        }
    }

    public String getFileName() {
        return this.fileName;
    }
}
