package com.huawei.hms.device;

import android.content.Context;
import android.text.TextUtils;
import com.huawei.hms.framework.common.ContainerUtils;
import com.huawei.hms.support.log.HMSLog;
import com.huawei.hms.support.log.common.Base64;
import com.huawei.hms.utils.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
/* compiled from: X509CertUtil.java */
/* loaded from: classes.dex */
public class a {
    public static boolean a(X509Certificate x509Certificate, List<X509Certificate> list) {
        if (list == null || list.size() == 0) {
            return false;
        }
        if (x509Certificate == null) {
            HMSLog.e("X509CertUtil", "rootCert is null,verify failed ");
            return false;
        }
        PublicKey publicKey = x509Certificate.getPublicKey();
        for (X509Certificate x509Certificate2 : list) {
            if (x509Certificate2 != null) {
                try {
                    x509Certificate2.checkValidity();
                    x509Certificate2.verify(publicKey);
                    publicKey = x509Certificate2.getPublicKey();
                } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException | CertificateException e) {
                    HMSLog.e("X509CertUtil", "verify failed " + e.getMessage());
                }
            }
            return false;
        }
        return a(list);
    }

    public static List<X509Certificate> b(List<String> list) {
        ArrayList arrayList = new ArrayList(list.size());
        for (String str : list) {
            arrayList.add(a(str));
        }
        return arrayList;
    }

    public static List<String> c(String str) {
        try {
            JSONArray jSONArray = new JSONArray(str);
            if (jSONArray.length() <= 1) {
                return Collections.emptyList();
            }
            ArrayList arrayList = new ArrayList(jSONArray.length());
            for (int i = 0; i < jSONArray.length(); i++) {
                arrayList.add(jSONArray.getString(i));
            }
            return arrayList;
        } catch (JSONException e) {
            HMSLog.e("X509CertUtil", "Failed to getCertChain: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public static List<X509Certificate> b(String str) {
        return b(c(str));
    }

    public static boolean b(X509Certificate x509Certificate, String str, String str2) {
        if (x509Certificate == null || TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return false;
        }
        return str2.equals(a(x509Certificate.getSubjectDN().getName(), str));
    }

    public static boolean b(X509Certificate x509Certificate, String str) {
        return b(x509Certificate, "OU", str);
    }

    public static X509Certificate a(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return a(Base64.decode(str));
        } catch (IllegalArgumentException e) {
            HMSLog.e("X509CertUtil", "getCert failed : " + e.getMessage());
            return null;
        }
    }

    public static X509Certificate a(byte[] bArr) {
        try {
            return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(bArr));
        } catch (CertificateException e) {
            HMSLog.e("X509CertUtil", "Failed to get cert: " + e.getMessage());
            return null;
        }
    }

    public static String a(String str, String str2) {
        String upperCase = str.toUpperCase(Locale.getDefault());
        int indexOf = upperCase.indexOf(str2 + ContainerUtils.KEY_VALUE_DELIMITER);
        if (indexOf == -1) {
            return null;
        }
        int indexOf2 = str.indexOf(",", indexOf);
        if (indexOf2 != -1) {
            return str.substring(indexOf + str2.length() + 1, indexOf2);
        }
        return str.substring(indexOf + str2.length() + 1);
    }

    public static boolean a(X509Certificate x509Certificate) {
        if (x509Certificate == null || x509Certificate.getBasicConstraints() == -1) {
            return false;
        }
        boolean[] keyUsage = x509Certificate.getKeyUsage();
        if (5 >= keyUsage.length) {
            return false;
        }
        return keyUsage[5];
    }

    public static boolean a(List<X509Certificate> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (!a(list.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean a(X509Certificate x509Certificate, String str) {
        return b(x509Certificate, "CN", str);
    }

    public static boolean a(X509Certificate x509Certificate, String str, String str2) {
        try {
            return a(x509Certificate, str.getBytes("UTF-8"), Base64.decode(str2));
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            HMSLog.e("X509CertUtil", " plainText exception: " + e.getMessage());
            return false;
        }
    }

    public static boolean a(X509Certificate x509Certificate, byte[] bArr, byte[] bArr2) {
        try {
            Signature signature = Signature.getInstance(x509Certificate.getSigAlgName());
            signature.initVerify(x509Certificate.getPublicKey());
            signature.update(bArr);
            return signature.verify(bArr2);
        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
            HMSLog.e("X509CertUtil", "failed checkSignature : " + e.getMessage());
            return false;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v0 */
    /* JADX WARN: Type inference failed for: r1v1, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r1v2 */
    public static X509Certificate a(Context context, String str) {
        Throwable th;
        Exception e;
        InputStream inputStream;
        KeyStore keyStore;
        ?? r1 = 0;
        if (context != null) {
            try {
                if (!TextUtils.isEmpty(str)) {
                    try {
                        keyStore = KeyStore.getInstance("bks");
                        inputStream = context.getAssets().open("hmsrootcas.bks");
                    } catch (IOException e2) {
                        e = e2;
                        inputStream = null;
                        HMSLog.e("X509CertUtil", "exception:" + e.getMessage());
                        IOUtils.closeQuietly(inputStream);
                        return null;
                    } catch (KeyStoreException e3) {
                        e = e3;
                        inputStream = null;
                        HMSLog.e("X509CertUtil", "exception:" + e.getMessage());
                        IOUtils.closeQuietly(inputStream);
                        return null;
                    } catch (NoSuchAlgorithmException e4) {
                        e = e4;
                        inputStream = null;
                        HMSLog.e("X509CertUtil", "exception:" + e.getMessage());
                        IOUtils.closeQuietly(inputStream);
                        return null;
                    } catch (CertificateException e5) {
                        e = e5;
                        inputStream = null;
                        HMSLog.e("X509CertUtil", "exception:" + e.getMessage());
                        IOUtils.closeQuietly(inputStream);
                        return null;
                    } catch (Throwable th2) {
                        th = th2;
                        IOUtils.closeQuietly((InputStream) r1);
                        throw th;
                    }
                    try {
                        keyStore.load(inputStream, "".toCharArray());
                    } catch (IOException e6) {
                        e = e6;
                        HMSLog.e("X509CertUtil", "exception:" + e.getMessage());
                        IOUtils.closeQuietly(inputStream);
                        return null;
                    } catch (KeyStoreException e7) {
                        e = e7;
                        HMSLog.e("X509CertUtil", "exception:" + e.getMessage());
                        IOUtils.closeQuietly(inputStream);
                        return null;
                    } catch (NoSuchAlgorithmException e8) {
                        e = e8;
                        HMSLog.e("X509CertUtil", "exception:" + e.getMessage());
                        IOUtils.closeQuietly(inputStream);
                        return null;
                    } catch (CertificateException e9) {
                        e = e9;
                        HMSLog.e("X509CertUtil", "exception:" + e.getMessage());
                        IOUtils.closeQuietly(inputStream);
                        return null;
                    }
                    if (!keyStore.containsAlias(str)) {
                        HMSLog.e("X509CertUtil", "Not include alias " + str);
                        IOUtils.closeQuietly(inputStream);
                        return null;
                    }
                    Certificate certificate = keyStore.getCertificate(str);
                    if (!(certificate instanceof X509Certificate)) {
                        IOUtils.closeQuietly(inputStream);
                        return null;
                    }
                    X509Certificate x509Certificate = (X509Certificate) certificate;
                    x509Certificate.checkValidity();
                    IOUtils.closeQuietly(inputStream);
                    return x509Certificate;
                }
            } catch (Throwable th3) {
                th = th3;
                r1 = context;
            }
        }
        HMSLog.e("X509CertUtil", "args are error");
        return null;
    }

    public static X509Certificate a(Context context) {
        return a(context, "052root");
    }
}
