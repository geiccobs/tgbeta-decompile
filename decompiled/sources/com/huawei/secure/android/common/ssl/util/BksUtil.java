package com.huawei.secure.android.common.ssl.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.huawei.hms.common.PackageConstants;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/* loaded from: classes.dex */
public class BksUtil {
    private static final Uri f = Uri.parse("content://com.huawei.hwid");
    private static final String[] p = {"B92825C2BD5D6D6D1E7F39EECD17843B7D9016F611136B75441BC6F4D3F00F05", "E49D5C2C0E11B3B1B96CA56C6DE2A14EC7DAB5CCC3B5F300D03E5B4DBA44F539"};

    private static void a(InputStream inputStream, Context context) {
        Throwable th;
        FileOutputStream fileOutputStream;
        if (inputStream == null || context == null) {
            return;
        }
        String a = a(context);
        if (!new File(a).exists()) {
            a(a);
        }
        File file = new File(a, "hmsrootcas.bks");
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fileOutputStream2 = null;
        try {
            try {
                g.c("BksUtil", "write output stream ");
                fileOutputStream = new FileOutputStream(file);
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException unused) {
        }
        try {
            byte[] bArr = new byte[2048];
            while (true) {
                int read = inputStream.read(bArr, 0, 2048);
                if (read != -1) {
                    fileOutputStream.write(bArr, 0, read);
                } else {
                    f.a((OutputStream) fileOutputStream);
                    return;
                }
            }
        } catch (IOException unused2) {
            fileOutputStream2 = fileOutputStream;
            g.b("BksUtil", " IOException");
            f.a((OutputStream) fileOutputStream2);
        } catch (Throwable th3) {
            th = th3;
            fileOutputStream2 = fileOutputStream;
            f.a((OutputStream) fileOutputStream2);
            throw th;
        }
    }

    private static String b(Context context) {
        return a(context) + File.separator + "hmsrootcas.bks";
    }

    private static boolean c(Context context) {
        return new File(a(context) + File.separator + "hmsrootcas.bks").exists();
    }

    public static synchronized InputStream getBksFromTss(Context context) {
        Throwable th;
        InputStream inputStream;
        ByteArrayInputStream byteArrayInputStream;
        String a;
        String b;
        synchronized (BksUtil.class) {
            g.c("BksUtil", "get bks from tss begin");
            if (context != null) {
                c.a(context);
            }
            Context a2 = c.a();
            ByteArrayInputStream byteArrayInputStream2 = null;
            if (a2 == null) {
                g.b("BksUtil", "context is null");
                return null;
            } else if (!b(h.a("com.huawei.hwid")) && !b(h.a(PackageConstants.SERVICES_PACKAGE_ALL_SCENE))) {
                g.b("BksUtil", "hms version code is too low : " + h.a("com.huawei.hwid"));
                return null;
            } else if (!c(a2, "com.huawei.hwid") && !b(a2, PackageConstants.SERVICES_PACKAGE_ALL_SCENE)) {
                g.b("BksUtil", "hms sign error");
                return null;
            } else {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    inputStream = a2.getContentResolver().openInputStream(Uri.withAppendedPath(f, "files/hmsrootcas.bks"));
                    try {
                        try {
                            byte[] bArr = new byte[1024];
                            while (true) {
                                int read = inputStream.read(bArr);
                                if (read <= -1) {
                                    break;
                                }
                                byteArrayOutputStream.write(bArr, 0, read);
                            }
                            byteArrayOutputStream.flush();
                            byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                        } catch (Exception unused) {
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                    try {
                        a = i.a("bks_hash", "", a2);
                        b = b(byteArrayOutputStream.toByteArray());
                    } catch (Exception unused2) {
                        byteArrayInputStream2 = byteArrayInputStream;
                        g.b("BksUtil", "Get bks from HMS_VERSION_CODE exception : No content provider");
                        f.a(inputStream);
                        f.a((OutputStream) byteArrayOutputStream);
                        f.a((InputStream) byteArrayInputStream2);
                        return getFilesBksIS(a2);
                    } catch (Throwable th3) {
                        th = th3;
                        byteArrayInputStream2 = byteArrayInputStream;
                        f.a(inputStream);
                        f.a((OutputStream) byteArrayOutputStream);
                        f.a((InputStream) byteArrayInputStream2);
                        throw th;
                    }
                } catch (Exception unused3) {
                    inputStream = null;
                } catch (Throwable th4) {
                    th = th4;
                    inputStream = null;
                }
                if (c(a2) && a.equals(b)) {
                    g.c("BksUtil", "bks not update");
                    f.a(inputStream);
                    f.a((OutputStream) byteArrayOutputStream);
                    f.a((InputStream) byteArrayInputStream);
                    return getFilesBksIS(a2);
                }
                g.c("BksUtil", "update bks and sp");
                a(byteArrayInputStream, a2);
                i.b("bks_hash", b, a2);
                f.a(inputStream);
                f.a((OutputStream) byteArrayOutputStream);
                f.a((InputStream) byteArrayInputStream);
                return getFilesBksIS(a2);
            }
        }
    }

    public static InputStream getFilesBksIS(Context context) {
        if (c(context)) {
            g.c("BksUtil", "getFilesBksIS ");
            try {
                return new FileInputStream(b(context));
            } catch (FileNotFoundException unused) {
                g.b("BksUtil", "FileNotFoundExceptio: ");
                return null;
            }
        }
        return null;
    }

    private static boolean b(String str) {
        int parseInt;
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        g.c("BksUtil", "hms version code is : " + str);
        String[] split = str.split("\\.");
        String[] split2 = "4.0.2.300".split("\\.");
        int length = split.length;
        int length2 = split2.length;
        int max = Math.max(length, length2);
        int i = 0;
        while (i < max) {
            if (i < length) {
                try {
                    parseInt = Integer.parseInt(split[i]);
                } catch (Exception e) {
                    g.b("BksUtil", " exception : " + e.getMessage());
                    return i >= length2;
                }
            } else {
                parseInt = 0;
            }
            int parseInt2 = i < length2 ? Integer.parseInt(split2[i]) : 0;
            if (parseInt < parseInt2) {
                return false;
            }
            if (parseInt > parseInt2) {
                return true;
            }
            i++;
        }
        return true;
    }

    private static boolean c(Context context, String str) {
        byte[] a = a(context, str);
        for (String str2 : p) {
            if (str2.equalsIgnoreCase(c(a))) {
                return true;
            }
        }
        return false;
    }

    private static String c(byte[] bArr) {
        if (bArr == null || bArr.length == 0) {
            return "";
        }
        try {
            return a(MessageDigest.getInstance("SHA-256").digest(bArr));
        } catch (NoSuchAlgorithmException e) {
            Log.e("BksUtil", "NoSuchAlgorithmException" + e.getMessage());
            return "";
        }
    }

    private static boolean b(Context context, String str) {
        return "E49D5C2C0E11B3B1B96CA56C6DE2A14EC7DAB5CCC3B5F300D03E5B4DBA44F539".equalsIgnoreCase(c(a(context, str)));
    }

    private static int a(String str) {
        if (TextUtils.isEmpty(str)) {
            return -1;
        }
        File file = new File(str);
        if (file.exists()) {
            g.e("BksUtil", "The directory  has already exists");
            return 1;
        } else if (file.mkdirs()) {
            g.a("BksUtil", "create directory  success");
            return 0;
        } else {
            g.b("BksUtil", "create directory  failed");
            return -1;
        }
    }

    private static String b(byte[] bArr) {
        if (bArr == null) {
            return "";
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(bArr);
            return a(messageDigest.digest());
        } catch (NoSuchAlgorithmException unused) {
            g.b("BksUtil", "inputstraem exception");
            return "";
        }
    }

    private static String a(Context context) {
        if (Build.VERSION.SDK_INT >= 24) {
            return context.createDeviceProtectedStorageContext().getFilesDir() + File.separator + "aegis";
        }
        return context.getApplicationContext().getFilesDir() + File.separator + "aegis";
    }

    private static byte[] a(Context context, String str) {
        PackageInfo packageInfo;
        if (context != null && !TextUtils.isEmpty(str)) {
            try {
                PackageManager packageManager = context.getPackageManager();
                if (packageManager != null && (packageInfo = packageManager.getPackageInfo(str, 64)) != null) {
                    return packageInfo.signatures[0].toByteArray();
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("BksUtil", "PackageManager.NameNotFoundException : " + e.getMessage());
            } catch (Exception e2) {
                Log.e("BksUtil", "get pm exception : " + e2.getMessage());
            }
            return new byte[0];
        }
        Log.e("BksUtil", "packageName is null or context is null");
        return new byte[0];
    }

    private static String a(byte[] bArr) {
        if (bArr == null || bArr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() == 1) {
                sb.append('0');
            }
            sb.append(hexString);
        }
        return sb.toString();
    }
}
