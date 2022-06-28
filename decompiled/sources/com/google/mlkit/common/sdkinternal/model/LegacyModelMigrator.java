package com.google.mlkit.common.sdkinternal.model;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.google.android.gms.common.util.Base64Utils;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.mlkit.common.sdkinternal.MLTaskExecutor;
import java.io.File;
import java.util.concurrent.Executor;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public abstract class LegacyModelMigrator {
    protected final ModelFileHelper modelFileHelper;
    private final Context zzb;
    private final TaskCompletionSource<Void> zza = new TaskCompletionSource<>();
    private final Executor zzc = MLTaskExecutor.workerThreadExecutor();

    protected LegacyModelMigrator(Context context, ModelFileHelper modelFileHelper) {
        this.zzb = context;
        this.modelFileHelper = modelFileHelper;
    }

    protected abstract String getLegacyModelDirName();

    protected abstract void migrateAllModelDirs(File file);

    public void start() {
        this.zzc.execute(new Runnable(this) { // from class: com.google.mlkit.common.sdkinternal.model.zza
            private final LegacyModelMigrator zza;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = this;
            }

            @Override // java.lang.Runnable
            public final void run() {
                this.zza.zza();
            }
        });
    }

    public Task<Void> getMigrationTask() {
        return this.zza.getTask();
    }

    public static void migrateFile(File file, File file2) {
        if (!file.exists()) {
            return;
        }
        if (!file2.exists() && !file.renameTo(file2)) {
            String valueOf = String.valueOf(file);
            String valueOf2 = String.valueOf(file2);
            StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 28 + String.valueOf(valueOf2).length());
            sb.append("Error moving model file ");
            sb.append(valueOf);
            sb.append(" to ");
            sb.append(valueOf2);
            Log.e("MlKitLegacyMigration", sb.toString());
        }
        if (file.exists() && !file.delete()) {
            String valueOf3 = String.valueOf(file);
            StringBuilder sb2 = new StringBuilder(String.valueOf(valueOf3).length() + 26);
            sb2.append("Error deleting model file ");
            sb2.append(valueOf3);
            Log.e("MlKitLegacyMigration", sb2.toString());
        }
    }

    public File getLegacyRootDir() {
        String legacyModelDirName = getLegacyModelDirName();
        if (Build.VERSION.SDK_INT >= 21) {
            return new File(this.zzb.getNoBackupFilesDir(), legacyModelDirName);
        }
        return this.zzb.getApplicationContext().getDir(legacyModelDirName, 0);
    }

    protected static boolean isValidFirebasePersistenceKey(String str) {
        String[] split = str.split("\\+", -1);
        if (split.length != 2) {
            return false;
        }
        try {
            Base64Utils.decodeUrlSafeNoPadding(split[0]);
            try {
                Base64Utils.decodeUrlSafeNoPadding(split[1]);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        } catch (IllegalArgumentException e2) {
            return false;
        }
    }

    protected static void deleteIfEmpty(File file) {
        if ((file.listFiles() == null || file.listFiles().length == 0) && !file.delete()) {
            String valueOf = String.valueOf(file);
            StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 31);
            sb.append("Error deleting model directory ");
            sb.append(valueOf);
            Log.e("MlKitLegacyMigration", sb.toString());
        }
    }

    public final /* synthetic */ void zza() {
        File legacyRootDir = getLegacyRootDir();
        File[] listFiles = legacyRootDir.listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                migrateAllModelDirs(file);
            }
            deleteIfEmpty(legacyRootDir);
        }
        this.zza.setResult(null);
    }
}
