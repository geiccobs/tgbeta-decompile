package com.google.mlkit.common.sdkinternal.model;

import android.os.Build;
import com.google.android.gms.common.internal.GmsLogger;
import com.google.android.gms.common.internal.Preconditions;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.common.sdkinternal.Constants;
import com.google.mlkit.common.sdkinternal.MlKitContext;
import com.google.mlkit.common.sdkinternal.ModelType;
import java.io.File;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public class ModelFileHelper {
    public static final int INVALID_INDEX = -1;
    private static final GmsLogger zza = new GmsLogger("ModelFileHelper", "");
    private static final String zzb = String.format("com.google.mlkit.%s.models", "automl");
    private static final String zzc = String.format("com.google.mlkit.%s.models", "translate");
    private static final String zzd = String.format("com.google.mlkit.%s.models", "base");
    private final MlKitContext zze;

    public ModelFileHelper(MlKitContext mlKitContext) {
        this.zze = mlKitContext;
    }

    public final String zza(String str, ModelType modelType) throws MlKitException {
        File modelDir = getModelDir(str, modelType);
        int latestCachedModelVersion = getLatestCachedModelVersion(modelDir);
        if (latestCachedModelVersion == -1) {
            return null;
        }
        String absolutePath = modelDir.getAbsolutePath();
        StringBuilder sb = new StringBuilder(String.valueOf(absolutePath).length() + 12);
        sb.append(absolutePath);
        sb.append("/");
        sb.append(latestCachedModelVersion);
        return sb.toString();
    }

    public void deleteTempFilesInPrivateFolder(String str, ModelType modelType) throws MlKitException {
        File zza2 = zza(str, modelType, true);
        if (!deleteRecursively(zza2)) {
            GmsLogger gmsLogger = zza;
            String valueOf = String.valueOf(zza2 != null ? zza2.getAbsolutePath() : null);
            gmsLogger.e("ModelFileHelper", valueOf.length() != 0 ? "Failed to delete the temp labels file directory: ".concat(valueOf) : new String("Failed to delete the temp labels file directory: "));
        }
    }

    public boolean modelExistsLocally(String str, ModelType modelType) throws MlKitException {
        String zza2;
        if (modelType == ModelType.UNKNOWN || (zza2 = zza(str, modelType)) == null) {
            return false;
        }
        File file = new File(zza2);
        if (!file.exists()) {
            return false;
        }
        File file2 = new File(file, Constants.MODEL_FILE_NAME);
        if (modelType != ModelType.AUTOML) {
            return file2.exists();
        }
        if (!file2.exists()) {
            return false;
        }
        return new File(file, Constants.AUTOML_IMAGE_LABELING_LABELS_FILE_NAME).exists() && new File(file, Constants.AUTOML_IMAGE_LABELING_MANIFEST_JSON_FILE_NAME).exists();
    }

    public File getModelDir(String str, ModelType modelType) throws MlKitException {
        return zza(str, modelType, false);
    }

    public File getModelTempDir(String str, ModelType modelType) throws MlKitException {
        return zza(str, modelType, true);
    }

    private final File zza(String str, ModelType modelType, boolean z) throws MlKitException {
        File modelDirUnsafe = getModelDirUnsafe(str, modelType, z);
        if (!modelDirUnsafe.exists()) {
            GmsLogger gmsLogger = zza;
            String valueOf = String.valueOf(modelDirUnsafe.getAbsolutePath());
            gmsLogger.d("ModelFileHelper", valueOf.length() != 0 ? "model folder does not exist, creating one: ".concat(valueOf) : new String("model folder does not exist, creating one: "));
            if (!modelDirUnsafe.mkdirs()) {
                String valueOf2 = String.valueOf(modelDirUnsafe);
                StringBuilder sb = new StringBuilder(String.valueOf(valueOf2).length() + 31);
                sb.append("Failed to create model folder: ");
                sb.append(valueOf2);
                throw new MlKitException(sb.toString(), 13);
            }
        } else if (!modelDirUnsafe.isDirectory()) {
            String valueOf3 = String.valueOf(modelDirUnsafe);
            StringBuilder sb2 = new StringBuilder(String.valueOf(valueOf3).length() + 71);
            sb2.append("Can not create model folder, since an existing file has the same name: ");
            sb2.append(valueOf3);
            throw new MlKitException(sb2.toString(), 6);
        }
        return modelDirUnsafe;
    }

    public File getModelDirUnsafe(String str, ModelType modelType, boolean z) {
        String str2;
        File file;
        switch (zzb.zza[modelType.ordinal()]) {
            case 1:
                str2 = zzd;
                break;
            case 2:
                str2 = zzb;
                break;
            case 3:
                str2 = zzc;
                break;
            default:
                String name = modelType.name();
                StringBuilder sb = new StringBuilder(String.valueOf(name).length() + 69);
                sb.append("Unknown model type ");
                sb.append(name);
                sb.append(". Cannot find a dir to store the downloaded model.");
                throw new IllegalArgumentException(sb.toString());
        }
        if (Build.VERSION.SDK_INT >= 21) {
            file = new File(this.zze.getApplicationContext().getNoBackupFilesDir(), str2);
        } else {
            file = this.zze.getApplicationContext().getDir(str2, 0);
        }
        if (z) {
            file = new File(file, "temp");
        }
        return new File(file, str);
    }

    public synchronized void deleteAllModels(ModelType modelType, String str) {
        deleteRecursively(getModelDirUnsafe(str, modelType, false));
        deleteRecursively(getModelDirUnsafe(str, modelType, true));
    }

    public final File zzb(String str, ModelType modelType) throws MlKitException {
        return zza(str, modelType, true);
    }

    public File getTempFileInPrivateFolder(String str, ModelType modelType, String str2) throws MlKitException {
        File zza2 = zza(str, modelType, true);
        if (zza2.exists() && zza2.isFile() && !zza2.delete()) {
            String valueOf = String.valueOf(zza2.getAbsolutePath());
            throw new MlKitException(valueOf.length() != 0 ? "Failed to delete the temp labels file: ".concat(valueOf) : new String("Failed to delete the temp labels file: "), 13);
        }
        if (!zza2.exists()) {
            GmsLogger gmsLogger = zza;
            String valueOf2 = String.valueOf(zza2.getAbsolutePath());
            gmsLogger.d("ModelFileHelper", valueOf2.length() != 0 ? "Temp labels folder does not exist, creating one: ".concat(valueOf2) : new String("Temp labels folder does not exist, creating one: "));
            if (!zza2.mkdirs()) {
                throw new MlKitException("Failed to create a directory to hold the AutoML model's labels file.", 13);
            }
        }
        return new File(zza2, str2);
    }

    public int getLatestCachedModelVersion(File file) {
        File[] listFiles = file.listFiles();
        int i = -1;
        if (listFiles == null || listFiles.length == 0) {
            return -1;
        }
        for (File file2 : listFiles) {
            try {
                i = Math.max(i, Integer.parseInt(file2.getName()));
            } catch (NumberFormatException e) {
                GmsLogger gmsLogger = zza;
                String valueOf = String.valueOf(file2.getName());
                gmsLogger.d("ModelFileHelper", valueOf.length() != 0 ? "Contains non-integer file name ".concat(valueOf) : new String("Contains non-integer file name "));
            }
        }
        return i;
    }

    public boolean deleteRecursively(File file) {
        boolean z;
        if (file == null) {
            return false;
        }
        if (!file.isDirectory()) {
            z = true;
        } else {
            z = true;
            for (File file2 : (File[]) Preconditions.checkNotNull(file.listFiles())) {
                z = z && deleteRecursively(file2);
            }
        }
        return z && file.delete();
    }
}
