package com.microsoft.appcenter.utils.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
/* loaded from: classes.dex */
public class FileManager {
    @SuppressLint({"StaticFieldLeak"})
    private static Context sContext;

    public static synchronized void initialize(Context context) {
        synchronized (FileManager.class) {
            if (sContext == null) {
                sContext = context;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v0, types: [java.io.File] */
    /* JADX WARN: Type inference failed for: r4v1, types: [java.io.File] */
    /* JADX WARN: Type inference failed for: r4v5, types: [java.lang.String] */
    public static String read(File file) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader((File) file));
            String property = System.getProperty("line.separator");
            StringBuilder sb = new StringBuilder();
            String readLine = bufferedReader.readLine();
            if (readLine != null) {
                sb.append(readLine);
                while (true) {
                    String readLine2 = bufferedReader.readLine();
                    if (readLine2 == null) {
                        break;
                    }
                    sb.append(property);
                    sb.append(readLine2);
                }
            }
            bufferedReader.close();
            file = sb.toString();
            return file;
        } catch (IOException e) {
            AppCenterLog.error("AppCenter", "Could not read file " + file.getAbsolutePath(), e);
            return null;
        }
    }

    public static byte[] readBytes(File file) {
        byte[] bArr = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            new DataInputStream(fileInputStream).readFully(bArr);
            fileInputStream.close();
            return bArr;
        } catch (IOException e) {
            AppCenterLog.error("AppCenter", "Could not read file " + file.getAbsolutePath(), e);
            return null;
        }
    }

    public static void write(File file, String str) throws IOException {
        if (TextUtils.isEmpty(str) || TextUtils.getTrimmedLength(str) <= 0) {
            return;
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        try {
            bufferedWriter.write(str);
        } finally {
            bufferedWriter.close();
        }
    }

    public static File lastModifiedFile(File file, FilenameFilter filenameFilter) {
        File file2 = null;
        if (file.exists()) {
            File[] listFiles = file.listFiles(filenameFilter);
            long j = 0;
            if (listFiles != null) {
                for (File file3 : listFiles) {
                    if (file3.lastModified() > j) {
                        j = file3.lastModified();
                        file2 = file3;
                    }
                }
            }
        }
        return file2;
    }

    public static boolean delete(File file) {
        return file.delete();
    }

    public static boolean deleteDirectory(File file) {
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File file2 : listFiles) {
                deleteDirectory(file2);
            }
        }
        return file.delete();
    }

    public static void cleanDirectory(File file) {
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File file2 : listFiles) {
                deleteDirectory(file2);
            }
        }
    }

    public static void mkdir(String str) {
        new File(str).mkdirs();
    }
}
