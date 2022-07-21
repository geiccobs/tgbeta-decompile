package com.huawei.hms.utils;

import android.content.Context;
import com.huawei.hms.support.hianalytics.HiAnalyticsConstant;
import com.huawei.hms.support.log.HMSLog;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
/* loaded from: classes.dex */
public abstract class FileUtil {
    public static final String LOCAL_REPORT_FILE = "hms/HwMobileServiceReport.txt";
    public static final String LOCAL_REPORT_FILE_CONFIG = "hms/config.txt";
    public static final long LOCAL_REPORT_FILE_MAX_SIZE = 10240;
    public static boolean a = false;
    public static ScheduledExecutorService b = Executors.newSingleThreadScheduledExecutor();

    /* loaded from: classes.dex */
    public static class a implements Runnable {
        public final /* synthetic */ File a;
        public final /* synthetic */ long b;
        public final /* synthetic */ String c;

        public a(File file, long j, String str) {
            this.a = file;
            this.b = j;
            this.c = str;
        }

        @Override // java.lang.Runnable
        public void run() {
            Throwable th;
            IOException e;
            File file = this.a;
            if (file == null) {
                HMSLog.e("FileUtil", "In writeFile Failed to get local file.");
                return;
            }
            File parentFile = file.getParentFile();
            if (parentFile != null && (parentFile.mkdirs() || parentFile.isDirectory())) {
                RandomAccessFile randomAccessFile = null;
                try {
                    try {
                        long length = this.a.length();
                        if (length > this.b) {
                            String canonicalPath = this.a.getCanonicalPath();
                            if (!this.a.delete()) {
                                HMSLog.e("FileUtil", "last file delete failed.");
                            }
                            randomAccessFile = new RandomAccessFile(new File(canonicalPath), "rw");
                        } else {
                            RandomAccessFile randomAccessFile2 = new RandomAccessFile(this.a, "rw");
                            try {
                                randomAccessFile2.seek(length);
                                randomAccessFile = randomAccessFile2;
                            } catch (IOException e2) {
                                e = e2;
                                randomAccessFile = randomAccessFile2;
                                HMSLog.e("FileUtil", "writeFile exception:", e);
                                IOUtils.closeQuietly(randomAccessFile);
                                return;
                            } catch (Throwable th2) {
                                th = th2;
                                randomAccessFile = randomAccessFile2;
                                IOUtils.closeQuietly(randomAccessFile);
                                throw th;
                            }
                        }
                        randomAccessFile.writeBytes(this.c + System.getProperty("line.separator"));
                    } catch (Throwable th3) {
                        th = th3;
                    }
                } catch (IOException e3) {
                    e = e3;
                }
                IOUtils.closeQuietly(randomAccessFile);
                return;
            }
            HMSLog.e("FileUtil", "In writeFile, Failed to create directory.");
        }
    }

    public static boolean verifyHash(String str, File file) {
        byte[] digest = SHA256.digest(file);
        return digest != null && HEX.encodeHexString(digest, true).equalsIgnoreCase(str);
    }

    public static void writeFile(File file, String str, long j) {
        b.execute(new a(file, j, str));
    }

    public static void writeFileReport(Context context, File file, File file2, String str, long j, int i) {
        if (file == null || !file.isFile() || !file.exists()) {
            return;
        }
        if (!a) {
            if (file2 != null && file2.exists() && !file2.delete()) {
                HMSLog.e("FileUtil", "file delete failed.");
            }
            a = true;
        }
        writeFile(file2, str + HiAnalyticsConstant.REPORT_VAL_SEPARATOR + j + HiAnalyticsConstant.REPORT_VAL_SEPARATOR + i, LOCAL_REPORT_FILE_MAX_SIZE);
    }
}
