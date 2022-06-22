package androidx.core.util;

import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
/* loaded from: classes.dex */
public class AtomicFile {
    private final File mBaseName;
    private final File mLegacyBackupName;
    private final File mNewName;

    public AtomicFile(File baseName) {
        this.mBaseName = baseName;
        this.mNewName = new File(baseName.getPath() + ".new");
        this.mLegacyBackupName = new File(baseName.getPath() + ".bak");
    }

    public File getBaseFile() {
        return this.mBaseName;
    }

    public FileOutputStream startWrite() throws IOException {
        if (this.mLegacyBackupName.exists()) {
            rename(this.mLegacyBackupName, this.mBaseName);
        }
        try {
            return new FileOutputStream(this.mNewName);
        } catch (FileNotFoundException unused) {
            if (!this.mNewName.getParentFile().mkdirs()) {
                throw new IOException("Failed to create directory for " + this.mNewName);
            }
            try {
                return new FileOutputStream(this.mNewName);
            } catch (FileNotFoundException e) {
                throw new IOException("Failed to create new file " + this.mNewName, e);
            }
        }
    }

    public void finishWrite(FileOutputStream str) {
        if (str == null) {
            return;
        }
        if (!sync(str)) {
            Log.e("AtomicFile", "Failed to sync file output stream");
        }
        try {
            str.close();
        } catch (IOException e) {
            Log.e("AtomicFile", "Failed to close file output stream", e);
        }
        rename(this.mNewName, this.mBaseName);
    }

    public void failWrite(FileOutputStream str) {
        if (str == null) {
            return;
        }
        if (!sync(str)) {
            Log.e("AtomicFile", "Failed to sync file output stream");
        }
        try {
            str.close();
        } catch (IOException e) {
            Log.e("AtomicFile", "Failed to close file output stream", e);
        }
        if (this.mNewName.delete()) {
            return;
        }
        Log.e("AtomicFile", "Failed to delete new file " + this.mNewName);
    }

    private static boolean sync(FileOutputStream stream) {
        try {
            stream.getFD().sync();
            return true;
        } catch (IOException unused) {
            return false;
        }
    }

    private static void rename(File source, File target) {
        if (target.isDirectory() && !target.delete()) {
            Log.e("AtomicFile", "Failed to delete file which is a directory " + target);
        }
        if (!source.renameTo(target)) {
            Log.e("AtomicFile", "Failed to rename " + source + " to " + target);
        }
    }
}
