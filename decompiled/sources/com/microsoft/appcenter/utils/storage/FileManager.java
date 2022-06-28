package com.microsoft.appcenter.utils.storage;

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
/* loaded from: classes3.dex */
public class FileManager {
    private static Context sContext;

    public static synchronized void initialize(Context context) {
        synchronized (FileManager.class) {
            if (sContext == null) {
                sContext = context;
            }
        }
    }

    public static String read(String path) {
        return read(new File(path));
    }

    public static String read(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String lineSeparator = System.getProperty("line.separator");
            StringBuilder contents = new StringBuilder();
            String line = reader.readLine();
            if (line != null) {
                contents.append(line);
                while (true) {
                    String line2 = reader.readLine();
                    if (line2 == null) {
                        break;
                    }
                    contents.append(lineSeparator);
                    contents.append(line2);
                }
            }
            reader.close();
            return contents.toString();
        } catch (IOException e) {
            AppCenterLog.error("AppCenter", "Could not read file " + file.getAbsolutePath(), e);
            return null;
        }
    }

    public static byte[] readBytes(File file) {
        byte[] fileContents = new byte[(int) file.length()];
        try {
            FileInputStream fileStream = new FileInputStream(file);
            DataInputStream dataInputStream = new DataInputStream(fileStream);
            dataInputStream.readFully(fileContents);
            fileStream.close();
            return fileContents;
        } catch (IOException e) {
            AppCenterLog.error("AppCenter", "Could not read file " + file.getAbsolutePath(), e);
            return null;
        }
    }

    public static void write(String path, String contents) throws IOException {
        write(new File(path), contents);
    }

    public static void write(File file, String contents) throws IOException {
        if (TextUtils.isEmpty(contents) || TextUtils.getTrimmedLength(contents) <= 0) {
            return;
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        try {
            writer.write(contents);
        } finally {
            writer.close();
        }
    }

    public static String[] getFilenames(String path, FilenameFilter filter) {
        File dir = new File(path);
        if (dir.exists()) {
            return dir.list(filter);
        }
        return new String[0];
    }

    public static File lastModifiedFile(String path, FilenameFilter filter) {
        return lastModifiedFile(new File(path), filter);
    }

    public static File lastModifiedFile(File dir, FilenameFilter filter) {
        if (dir.exists()) {
            File[] files = dir.listFiles(filter);
            long lastModification = 0;
            File lastModifiedFile = null;
            if (files != null) {
                for (File file : files) {
                    if (file.lastModified() > lastModification) {
                        lastModification = file.lastModified();
                        lastModifiedFile = file;
                    }
                }
                return lastModifiedFile;
            }
            return null;
        }
        return null;
    }

    public static boolean delete(String path) {
        return delete(new File(path));
    }

    public static boolean delete(File file) {
        return file.delete();
    }

    public static boolean deleteDirectory(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDirectory(f);
            }
        }
        return file.delete();
    }

    public static void cleanDirectory(File directory) {
        File[] contents = directory.listFiles();
        if (contents != null) {
            for (File file : contents) {
                deleteDirectory(file);
            }
        }
    }

    public static void mkdir(String path) {
        new File(path).mkdirs();
    }
}
