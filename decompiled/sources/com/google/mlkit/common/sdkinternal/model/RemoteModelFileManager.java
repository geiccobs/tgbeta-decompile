package com.google.mlkit.common.sdkinternal.model;

import com.google.android.gms.common.internal.GmsLogger;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.common.model.RemoteModel;
import com.google.mlkit.common.sdkinternal.MlKitContext;
import com.google.mlkit.common.sdkinternal.ModelType;
import com.google.mlkit.common.sdkinternal.SharedPrefManager;
import java.io.File;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public class RemoteModelFileManager {
    private static final GmsLogger zza = new GmsLogger("RemoteModelFileManager", "");
    private final MlKitContext zzb;
    private final String zzc;
    private final ModelType zzd;
    private final ModelValidator zze;
    private final RemoteModelFileMover zzf;
    private final SharedPrefManager zzg;
    private final ModelFileHelper zzh;

    public RemoteModelFileManager(MlKitContext mlKitContext, RemoteModel remoteModel, ModelValidator modelValidator, ModelFileHelper modelFileHelper, RemoteModelFileMover remoteModelFileMover) {
        String str;
        this.zzb = mlKitContext;
        ModelType modelType = remoteModel.getModelType();
        this.zzd = modelType;
        if (modelType == ModelType.TRANSLATE) {
            str = remoteModel.getModelNameForBackend();
        } else {
            str = remoteModel.getUniqueModelNameForPersist();
        }
        this.zzc = str;
        this.zze = modelValidator;
        this.zzg = SharedPrefManager.getInstance(mlKitContext);
        this.zzh = modelFileHelper;
        this.zzf = remoteModelFileMover;
    }

    /* JADX WARN: Code restructure failed: missing block: B:34:0x00be, code lost:
        r9 = com.google.mlkit.common.sdkinternal.model.RemoteModelFileManager.zza;
        r10 = java.lang.String.valueOf(r10);
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x00cc, code lost:
        if (r10.length() == 0) goto L37;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x00ce, code lost:
        r10 = "Hash does not match with expected: ".concat(r10);
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x00d3, code lost:
        r10 = new java.lang.String("Hash does not match with expected: ");
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x00d8, code lost:
        r9.d("RemoteModelFileManager", r10);
        r0.zza(25, true, r8.zzd, 6);
        r9 = new com.google.mlkit.common.MlKitException("Hash does not match with expected", 102);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized java.io.File moveModelToPrivateFolder(android.os.ParcelFileDescriptor r9, java.lang.String r10, com.google.mlkit.common.model.RemoteModel r11) throws com.google.mlkit.common.MlKitException {
        /*
            Method dump skipped, instructions count: 355
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.mlkit.common.sdkinternal.model.RemoteModelFileManager.moveModelToPrivateFolder(android.os.ParcelFileDescriptor, java.lang.String, com.google.mlkit.common.model.RemoteModel):java.io.File");
    }

    public final synchronized boolean zza(File file) throws MlKitException {
        File[] listFiles;
        File modelDir = this.zzh.getModelDir(this.zzc, this.zzd);
        if (!modelDir.exists()) {
            return false;
        }
        boolean z = true;
        for (File file2 : modelDir.listFiles()) {
            if (!file2.equals(file) && !this.zzh.deleteRecursively(file2)) {
                z = false;
            }
        }
        return z;
    }

    public final synchronized void zzb(File file) {
        File modelDirUnsafe = getModelDirUnsafe(false);
        if (!modelDirUnsafe.exists()) {
            return;
        }
        for (File file2 : modelDirUnsafe.listFiles()) {
            if (file2.equals(file)) {
                this.zzh.deleteRecursively(file);
                return;
            }
        }
    }

    public final synchronized File zzc(File file) throws MlKitException {
        File file2 = new File(String.valueOf(this.zzh.getModelDir(this.zzc, this.zzd).getAbsolutePath()).concat("/0"));
        return file2.exists() ? file : file.renameTo(file2) ? file2 : file;
    }

    public final synchronized String zza() throws MlKitException {
        return this.zzh.zza(this.zzc, this.zzd);
    }

    public File getModelDirUnsafe(boolean z) {
        return this.zzh.getModelDirUnsafe(this.zzc, this.zzd, z);
    }
}
