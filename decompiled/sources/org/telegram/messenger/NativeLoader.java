package org.telegram.messenger;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
/* loaded from: classes.dex */
public class NativeLoader {
    private static final String LIB_NAME = "tmessages.42";
    private static final String LIB_SO_NAME = "libtmessages.42.so";
    private static final int LIB_VERSION = 42;
    private static final String LOCALE_LIB_SO_NAME = "libtmessages.42loc.so";
    private static volatile boolean nativeLoaded = false;
    private String crashPath = "";

    private static native void init(String str, boolean z);

    private static File getNativeLibraryDir(Context context) {
        File f = null;
        if (context != null) {
            try {
                f = new File((String) ApplicationInfo.class.getField("nativeLibraryDir").get(context.getApplicationInfo()));
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        if (f == null) {
            f = new File(context.getApplicationInfo().dataDir, "lib");
        }
        if (f.isDirectory()) {
            return f;
        }
        return null;
    }

    private static boolean loadFromZip(Context context, File destDir, File destLocalFile, String folder) {
        File[] listFiles;
        try {
            for (File file : destDir.listFiles()) {
                file.delete();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        ZipFile zipFile = null;
        InputStream stream = null;
        try {
            try {
                ZipFile zipFile2 = new ZipFile(context.getApplicationInfo().sourceDir);
                ZipEntry entry = zipFile2.getEntry("lib/" + folder + "/" + LIB_SO_NAME);
                if (entry == null) {
                    throw new Exception("Unable to find file in apk:lib/" + folder + "/" + LIB_NAME);
                }
                InputStream stream2 = zipFile2.getInputStream(entry);
                OutputStream out = new FileOutputStream(destLocalFile);
                byte[] buf = new byte[4096];
                while (true) {
                    int len = stream2.read(buf);
                    if (len <= 0) {
                        break;
                    }
                    Thread.yield();
                    out.write(buf, 0, len);
                }
                out.close();
                destLocalFile.setReadable(true, false);
                destLocalFile.setExecutable(true, false);
                destLocalFile.setWritable(true);
                try {
                    System.load(destLocalFile.getAbsolutePath());
                    nativeLoaded = true;
                } catch (Error e2) {
                    FileLog.e(e2);
                }
                if (stream2 != null) {
                    try {
                        stream2.close();
                    } catch (Exception e3) {
                        FileLog.e(e3);
                    }
                }
                try {
                    zipFile2.close();
                } catch (Exception e4) {
                    FileLog.e(e4);
                }
                return true;
            } catch (Throwable th) {
                if (0 != 0) {
                    try {
                        stream.close();
                    } catch (Exception e5) {
                        FileLog.e(e5);
                    }
                }
                if (0 != 0) {
                    try {
                        zipFile.close();
                    } catch (Exception e6) {
                        FileLog.e(e6);
                    }
                }
                throw th;
            }
        } catch (Exception e7) {
            FileLog.e(e7);
            if (0 != 0) {
                try {
                    stream.close();
                } catch (Exception e8) {
                    FileLog.e(e8);
                }
            }
            if (0 != 0) {
                try {
                    zipFile.close();
                } catch (Exception e9) {
                    FileLog.e(e9);
                }
            }
            return false;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:55:0x00e3 A[Catch: all -> 0x001c, TryCatch #1 {, blocks: (B:4:0x0003, B:59:0x0103, B:61:0x010c, B:9:0x000a, B:11:0x0015, B:15:0x0020, B:16:0x0023, B:19:0x0032, B:22:0x003f, B:25:0x004c, B:28:0x0059, B:31:0x0066, B:34:0x0073, B:36:0x0079, B:38:0x0091, B:39:0x0096, B:41:0x009e, B:44:0x00a9, B:46:0x00c4, B:48:0x00c8, B:49:0x00cd, B:52:0x00d9, B:53:0x00df, B:55:0x00e3, B:56:0x00f7), top: B:66:0x0003, inners: #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:58:0x00fe A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:69:0x0103 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static synchronized void initNativeLibs(android.content.Context r8) {
        /*
            Method dump skipped, instructions count: 276
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NativeLoader.initNativeLibs(android.content.Context):void");
    }
}
