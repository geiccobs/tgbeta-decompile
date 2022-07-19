package com.huawei.hms.opendevice;

import android.content.Context;
import android.text.TextUtils;
import com.huawei.hms.support.log.HMSLog;
import com.huawei.secure.android.common.ssl.SecureSSLSocketFactory;
import com.huawei.secure.android.common.util.IOUtil;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
/* compiled from: HttpClient.java */
/* loaded from: classes.dex */
public abstract class d {

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: HttpClient.java */
    /* loaded from: classes.dex */
    public enum a {
        GET("GET"),
        POST("POST");
        
        public String d;

        a(String str) {
            this.d = str;
        }

        public String a() {
            return this.d;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v1, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r10v10 */
    /* JADX WARN: Type inference failed for: r10v13 */
    /* JADX WARN: Type inference failed for: r10v19 */
    /* JADX WARN: Type inference failed for: r10v21 */
    /* JADX WARN: Type inference failed for: r10v9 */
    /* JADX WARN: Type inference failed for: r2v0 */
    /* JADX WARN: Type inference failed for: r2v1, types: [java.net.HttpURLConnection] */
    /* JADX WARN: Type inference failed for: r2v3 */
    /* JADX WARN: Type inference failed for: r8v1, types: [java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r8v11 */
    /* JADX WARN: Type inference failed for: r8v12 */
    /* JADX WARN: Type inference failed for: r8v15 */
    /* JADX WARN: Type inference failed for: r8v20 */
    /* JADX WARN: Type inference failed for: r8v21, types: [java.io.OutputStream, java.io.FilterOutputStream, java.io.BufferedOutputStream] */
    /* JADX WARN: Type inference failed for: r8v22 */
    /* JADX WARN: Type inference failed for: r8v23 */
    /* JADX WARN: Type inference failed for: r8v24 */
    /* JADX WARN: Type inference failed for: r8v25 */
    /* JADX WARN: Type inference failed for: r8v26 */
    /* JADX WARN: Type inference failed for: r8v27 */
    /* JADX WARN: Type inference failed for: r8v3 */
    /* JADX WARN: Type inference failed for: r8v31 */
    /* JADX WARN: Type inference failed for: r8v32 */
    /* JADX WARN: Type inference failed for: r8v33 */
    /* JADX WARN: Type inference failed for: r8v4 */
    /* JADX WARN: Type inference failed for: r8v7 */
    /* JADX WARN: Type inference failed for: r8v8 */
    /* JADX WARN: Type inference failed for: r9v1, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r9v12 */
    /* JADX WARN: Type inference failed for: r9v13 */
    /* JADX WARN: Type inference failed for: r9v18 */
    /* JADX WARN: Type inference failed for: r9v29 */
    public static String a(Context context, String str, String str2, Map<String, String> map) {
        ?? r10;
        InputStream inputStream;
        ?? r8;
        Throwable th;
        InputStream inputStream2;
        InputStream inputStream3;
        OutputStream outputStream;
        HttpURLConnection httpURLConnection;
        InputStream inputStream4;
        ?? r82;
        InputStream inputStream5;
        InputStream inputStream6;
        InputStream inputStream7;
        HttpURLConnection httpURLConnection2 = 0;
        if (str2 == null || TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return null;
        }
        int i = -1;
        try {
            try {
                httpURLConnection = a(context, str, map, a.POST.a());
                if (httpURLConnection == null) {
                    IOUtil.closeSecure((OutputStream) null);
                    IOUtil.closeSecure((InputStream) null);
                    IOUtil.closeSecure((InputStream) null);
                    s.a(httpURLConnection);
                    HMSLog.i("PushHttpClient", "close connection");
                    return null;
                }
                try {
                    r82 = new BufferedOutputStream(httpURLConnection.getOutputStream());
                } catch (IOException unused) {
                    inputStream5 = null;
                    inputStream4 = inputStream5;
                    r82 = inputStream5;
                    inputStream2 = inputStream4;
                    StringBuilder sb = new StringBuilder();
                    sb.append("http execute encounter IOException - http code:");
                    sb.append(i);
                    HMSLog.w("PushHttpClient", sb.toString());
                    outputStream = r82;
                    inputStream3 = inputStream4;
                    IOUtil.closeSecure(outputStream);
                    IOUtil.closeSecure(inputStream3);
                    IOUtil.closeSecure(inputStream2);
                    s.a(httpURLConnection);
                    HMSLog.i("PushHttpClient", "close connection");
                    return null;
                } catch (RuntimeException unused2) {
                    inputStream6 = null;
                    inputStream4 = inputStream6;
                    r82 = inputStream6;
                    inputStream2 = inputStream4;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("http execute encounter RuntimeException - http code:");
                    sb2.append(i);
                    HMSLog.w("PushHttpClient", sb2.toString());
                    outputStream = r82;
                    inputStream3 = inputStream4;
                    IOUtil.closeSecure(outputStream);
                    IOUtil.closeSecure(inputStream3);
                    IOUtil.closeSecure(inputStream2);
                    s.a(httpURLConnection);
                    HMSLog.i("PushHttpClient", "close connection");
                    return null;
                } catch (Exception unused3) {
                    inputStream7 = null;
                    inputStream4 = inputStream7;
                    r82 = inputStream7;
                    inputStream2 = inputStream4;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("http execute encounter unknown exception - http code:");
                    sb3.append(i);
                    HMSLog.w("PushHttpClient", sb3.toString());
                    outputStream = r82;
                    inputStream3 = inputStream4;
                    IOUtil.closeSecure(outputStream);
                    IOUtil.closeSecure(inputStream3);
                    IOUtil.closeSecure(inputStream2);
                    s.a(httpURLConnection);
                    HMSLog.i("PushHttpClient", "close connection");
                    return null;
                } catch (Throwable th2) {
                    inputStream = 0;
                    r10 = 0;
                    httpURLConnection2 = httpURLConnection;
                    th = th2;
                    r8 = 0;
                }
                try {
                    r82.write(str2.getBytes("UTF-8"));
                    r82.flush();
                    i = httpURLConnection.getResponseCode();
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("http post response code: ");
                    sb4.append(i);
                    HMSLog.d("PushHttpClient", sb4.toString());
                    if (i >= 400) {
                        inputStream4 = httpURLConnection.getErrorStream();
                    } else {
                        inputStream4 = httpURLConnection.getInputStream();
                    }
                    try {
                        inputStream2 = new BufferedInputStream(inputStream4);
                        try {
                            String a2 = s.a(inputStream2);
                            IOUtil.closeSecure((OutputStream) r82);
                            IOUtil.closeSecure(inputStream4);
                            IOUtil.closeSecure(inputStream2);
                            s.a(httpURLConnection);
                            HMSLog.i("PushHttpClient", "close connection");
                            return a2;
                        } catch (IOException unused4) {
                            StringBuilder sb5 = new StringBuilder();
                            sb5.append("http execute encounter IOException - http code:");
                            sb5.append(i);
                            HMSLog.w("PushHttpClient", sb5.toString());
                            outputStream = r82;
                            inputStream3 = inputStream4;
                            IOUtil.closeSecure(outputStream);
                            IOUtil.closeSecure(inputStream3);
                            IOUtil.closeSecure(inputStream2);
                            s.a(httpURLConnection);
                            HMSLog.i("PushHttpClient", "close connection");
                            return null;
                        } catch (RuntimeException unused5) {
                            StringBuilder sb22 = new StringBuilder();
                            sb22.append("http execute encounter RuntimeException - http code:");
                            sb22.append(i);
                            HMSLog.w("PushHttpClient", sb22.toString());
                            outputStream = r82;
                            inputStream3 = inputStream4;
                            IOUtil.closeSecure(outputStream);
                            IOUtil.closeSecure(inputStream3);
                            IOUtil.closeSecure(inputStream2);
                            s.a(httpURLConnection);
                            HMSLog.i("PushHttpClient", "close connection");
                            return null;
                        } catch (Exception unused6) {
                            StringBuilder sb32 = new StringBuilder();
                            sb32.append("http execute encounter unknown exception - http code:");
                            sb32.append(i);
                            HMSLog.w("PushHttpClient", sb32.toString());
                            outputStream = r82;
                            inputStream3 = inputStream4;
                            IOUtil.closeSecure(outputStream);
                            IOUtil.closeSecure(inputStream3);
                            IOUtil.closeSecure(inputStream2);
                            s.a(httpURLConnection);
                            HMSLog.i("PushHttpClient", "close connection");
                            return null;
                        }
                    } catch (IOException unused7) {
                        inputStream2 = null;
                    } catch (RuntimeException unused8) {
                        inputStream2 = null;
                    } catch (Exception unused9) {
                        inputStream2 = null;
                    } catch (Throwable th3) {
                        httpURLConnection2 = httpURLConnection;
                        th = th3;
                        r10 = 0;
                        r8 = r82;
                        inputStream = inputStream4;
                        IOUtil.closeSecure((OutputStream) r8);
                        IOUtil.closeSecure((InputStream) inputStream);
                        IOUtil.closeSecure((InputStream) r10);
                        s.a((HttpURLConnection) httpURLConnection2);
                        HMSLog.i("PushHttpClient", "close connection");
                        throw th;
                    }
                } catch (IOException unused10) {
                    inputStream4 = null;
                    r82 = r82;
                    inputStream2 = inputStream4;
                    StringBuilder sb52 = new StringBuilder();
                    sb52.append("http execute encounter IOException - http code:");
                    sb52.append(i);
                    HMSLog.w("PushHttpClient", sb52.toString());
                    outputStream = r82;
                    inputStream3 = inputStream4;
                    IOUtil.closeSecure(outputStream);
                    IOUtil.closeSecure(inputStream3);
                    IOUtil.closeSecure(inputStream2);
                    s.a(httpURLConnection);
                    HMSLog.i("PushHttpClient", "close connection");
                    return null;
                } catch (RuntimeException unused11) {
                    inputStream4 = null;
                    r82 = r82;
                    inputStream2 = inputStream4;
                    StringBuilder sb222 = new StringBuilder();
                    sb222.append("http execute encounter RuntimeException - http code:");
                    sb222.append(i);
                    HMSLog.w("PushHttpClient", sb222.toString());
                    outputStream = r82;
                    inputStream3 = inputStream4;
                    IOUtil.closeSecure(outputStream);
                    IOUtil.closeSecure(inputStream3);
                    IOUtil.closeSecure(inputStream2);
                    s.a(httpURLConnection);
                    HMSLog.i("PushHttpClient", "close connection");
                    return null;
                } catch (Exception unused12) {
                    inputStream4 = null;
                    r82 = r82;
                    inputStream2 = inputStream4;
                    StringBuilder sb322 = new StringBuilder();
                    sb322.append("http execute encounter unknown exception - http code:");
                    sb322.append(i);
                    HMSLog.w("PushHttpClient", sb322.toString());
                    outputStream = r82;
                    inputStream3 = inputStream4;
                    IOUtil.closeSecure(outputStream);
                    IOUtil.closeSecure(inputStream3);
                    IOUtil.closeSecure(inputStream2);
                    s.a(httpURLConnection);
                    HMSLog.i("PushHttpClient", "close connection");
                    return null;
                } catch (Throwable th4) {
                    r10 = 0;
                    httpURLConnection2 = httpURLConnection;
                    th = th4;
                    inputStream = 0;
                    r8 = r82;
                }
            } catch (Throwable th5) {
                httpURLConnection2 = context;
                th = th5;
                r8 = str;
                inputStream = str2;
                r10 = map;
            }
        } catch (IOException unused13) {
            httpURLConnection = null;
            inputStream5 = null;
        } catch (RuntimeException unused14) {
            httpURLConnection = null;
            inputStream6 = null;
        } catch (Exception unused15) {
            httpURLConnection = null;
            inputStream7 = null;
        } catch (Throwable th6) {
            th = th6;
            r8 = 0;
            inputStream = 0;
            r10 = 0;
        }
    }

    public static HttpURLConnection a(Context context, String str, Map<String, String> map, String str2) throws Exception {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
        a(context, httpURLConnection);
        httpURLConnection.setRequestMethod(str2);
        httpURLConnection.setConnectTimeout(15000);
        httpURLConnection.setReadTimeout(15000);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setRequestProperty("Content-type", "application/json; charset=UTF-8");
        httpURLConnection.setRequestProperty("Connection", "close");
        if (map != null && map.size() >= 1) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                if (key != null && !TextUtils.isEmpty(key)) {
                    httpURLConnection.setRequestProperty(key, URLEncoder.encode(entry.getValue() == null ? "" : entry.getValue(), "UTF-8"));
                }
            }
        }
        return httpURLConnection;
    }

    public static void a(Context context, HttpURLConnection httpURLConnection) throws Exception {
        if (httpURLConnection instanceof HttpsURLConnection) {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) httpURLConnection;
            SecureSSLSocketFactory secureSSLSocketFactory = null;
            try {
                secureSSLSocketFactory = SecureSSLSocketFactory.getInstance(context);
            } catch (IOException unused) {
                HMSLog.w("PushHttpClient", "Get SocketFactory IO Exception.");
            } catch (IllegalAccessException unused2) {
                HMSLog.w("PushHttpClient", "Get SocketFactory Illegal Access Exception.");
            } catch (IllegalArgumentException unused3) {
                HMSLog.w("PushHttpClient", "Get SocketFactory Illegal Argument Exception.");
            } catch (KeyStoreException unused4) {
                HMSLog.w("PushHttpClient", "Get SocketFactory Key Store exception.");
            } catch (NoSuchAlgorithmException unused5) {
                HMSLog.w("PushHttpClient", "Get SocketFactory Algorithm Exception.");
            } catch (GeneralSecurityException unused6) {
                HMSLog.w("PushHttpClient", "Get SocketFactory General Security Exception.");
            }
            if (secureSSLSocketFactory != null) {
                httpsURLConnection.setSSLSocketFactory(secureSSLSocketFactory);
                httpsURLConnection.setHostnameVerifier(SecureSSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
                return;
            }
            throw new Exception("No ssl socket factory set.");
        }
    }
}
