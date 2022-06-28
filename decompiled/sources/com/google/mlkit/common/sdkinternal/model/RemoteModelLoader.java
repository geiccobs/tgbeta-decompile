package com.google.mlkit.common.sdkinternal.model;

import com.google.android.gms.common.internal.GmsLogger;
import com.google.android.gms.internal.mlkit_common.zzdx;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.common.model.RemoteModel;
import com.google.mlkit.common.sdkinternal.MlKitContext;
import com.google.mlkit.common.sdkinternal.ModelType;
import com.google.mlkit.common.sdkinternal.SharedPrefManager;
import java.io.File;
import java.nio.MappedByteBuffer;
import java.util.HashMap;
import java.util.Map;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public class RemoteModelLoader {
    private static final GmsLogger zza = new GmsLogger("RemoteModelLoader", "");
    private static final Map<String, RemoteModelLoader> zzb = new HashMap();
    private final MlKitContext zzc;
    private final RemoteModel zzd;
    private final RemoteModelDownloadManager zze;
    private final RemoteModelFileManager zzf;
    private final zzdx zzg;
    private final RemoteModelLoaderHelper zzh;
    private final ModelType zzi;
    private boolean zzj = true;

    private RemoteModelLoader(MlKitContext mlKitContext, RemoteModel remoteModel, ModelValidator modelValidator, RemoteModelLoaderHelper remoteModelLoaderHelper, RemoteModelFileMover remoteModelFileMover) {
        RemoteModelFileManager remoteModelFileManager = new RemoteModelFileManager(mlKitContext, remoteModel, modelValidator, new ModelFileHelper(mlKitContext), remoteModelFileMover);
        this.zzf = remoteModelFileManager;
        this.zzg = ((zzdx.zza) MlKitContext.getInstance().get(zzdx.zza.class)).get(remoteModel);
        this.zze = RemoteModelDownloadManager.getInstance(mlKitContext, remoteModel, new ModelFileHelper(mlKitContext), remoteModelFileManager, (ModelInfoRetrieverInterop) mlKitContext.get(ModelInfoRetrieverInterop.class));
        this.zzh = remoteModelLoaderHelper;
        this.zzc = mlKitContext;
        this.zzd = remoteModel;
        this.zzi = remoteModel.getModelType();
    }

    public static synchronized RemoteModelLoader getInstance(MlKitContext mlKitContext, RemoteModel remoteModel, ModelValidator modelValidator, RemoteModelLoaderHelper remoteModelLoaderHelper, RemoteModelFileMover remoteModelFileMover) {
        RemoteModelLoader remoteModelLoader;
        synchronized (RemoteModelLoader.class) {
            String uniqueModelNameForPersist = remoteModel.getUniqueModelNameForPersist();
            Map<String, RemoteModelLoader> map = zzb;
            if (!map.containsKey(uniqueModelNameForPersist)) {
                map.put(uniqueModelNameForPersist, new RemoteModelLoader(mlKitContext, remoteModel, modelValidator, remoteModelLoaderHelper, remoteModelFileMover));
            }
            remoteModelLoader = map.get(uniqueModelNameForPersist);
        }
        return remoteModelLoader;
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x00d6 A[Catch: all -> 0x00e3, TRY_LEAVE, TryCatch #0 {, blocks: (B:3:0x0001, B:7:0x001e, B:9:0x0027, B:10:0x002e, B:12:0x005a, B:14:0x0062, B:16:0x0078, B:17:0x007d, B:18:0x0083, B:20:0x008d, B:22:0x0095, B:24:0x00a7, B:26:0x00af, B:27:0x00c4, B:30:0x00d6), top: B:35:0x0001 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized java.nio.MappedByteBuffer load() throws com.google.mlkit.common.MlKitException {
        /*
            r9 = this;
            monitor-enter(r9)
            com.google.android.gms.common.internal.GmsLogger r0 = com.google.mlkit.common.sdkinternal.model.RemoteModelLoader.zza     // Catch: java.lang.Throwable -> Le3
            java.lang.String r1 = "RemoteModelLoader"
            java.lang.String r2 = "Try to load newly downloaded model file."
            r0.d(r1, r2)     // Catch: java.lang.Throwable -> Le3
            boolean r1 = r9.zzj     // Catch: java.lang.Throwable -> Le3
            com.google.mlkit.common.sdkinternal.model.RemoteModelDownloadManager r2 = r9.zze     // Catch: java.lang.Throwable -> Le3
            java.lang.Long r2 = r2.getDownloadingId()     // Catch: java.lang.Throwable -> Le3
            com.google.mlkit.common.sdkinternal.model.RemoteModelDownloadManager r3 = r9.zze     // Catch: java.lang.Throwable -> Le3
            java.lang.String r3 = r3.getDownloadingModelHash()     // Catch: java.lang.Throwable -> Le3
            if (r2 == 0) goto Lc4
            if (r3 != 0) goto L1e
            goto Lc4
        L1e:
            com.google.mlkit.common.sdkinternal.model.RemoteModelDownloadManager r4 = r9.zze     // Catch: java.lang.Throwable -> Le3
            java.lang.Integer r4 = r4.getDownloadingModelStatusCode()     // Catch: java.lang.Throwable -> Le3
            if (r4 != 0) goto L2e
        L27:
            com.google.mlkit.common.sdkinternal.model.RemoteModelDownloadManager r1 = r9.zze     // Catch: java.lang.Throwable -> Le3
            r1.removeOrCancelDownload()     // Catch: java.lang.Throwable -> Le3
            goto Ld2
        L2e:
            java.lang.String r5 = "RemoteModelLoader"
            java.lang.String r6 = java.lang.String.valueOf(r4)     // Catch: java.lang.Throwable -> Le3
            java.lang.String r7 = java.lang.String.valueOf(r6)     // Catch: java.lang.Throwable -> Le3
            int r7 = r7.length()     // Catch: java.lang.Throwable -> Le3
            int r7 = r7 + 22
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Le3
            r8.<init>(r7)     // Catch: java.lang.Throwable -> Le3
            java.lang.String r7 = "Download Status code: "
            r8.append(r7)     // Catch: java.lang.Throwable -> Le3
            r8.append(r6)     // Catch: java.lang.Throwable -> Le3
            java.lang.String r6 = r8.toString()     // Catch: java.lang.Throwable -> Le3
            r0.d(r5, r6)     // Catch: java.lang.Throwable -> Le3
            int r5 = r4.intValue()     // Catch: java.lang.Throwable -> Le3
            r6 = 8
            if (r5 != r6) goto La7
            com.google.mlkit.common.sdkinternal.model.RemoteModelDownloadManager r2 = r9.zze     // Catch: java.lang.Throwable -> Le3
            java.io.File r2 = r2.zza(r3)     // Catch: java.lang.Throwable -> Le3
            if (r2 == 0) goto Ld2
            java.nio.MappedByteBuffer r4 = r9.zza(r2)     // Catch: java.lang.Throwable -> Le3
            java.lang.String r5 = "RemoteModelLoader"
            java.lang.String r6 = "Moved the downloaded model to private folder successfully: "
            java.lang.String r7 = r2.getParent()     // Catch: java.lang.Throwable -> Le3
            java.lang.String r7 = java.lang.String.valueOf(r7)     // Catch: java.lang.Throwable -> Le3
            int r8 = r7.length()     // Catch: java.lang.Throwable -> Le3
            if (r8 == 0) goto L7d
            java.lang.String r6 = r6.concat(r7)     // Catch: java.lang.Throwable -> Le3
            goto L83
        L7d:
            java.lang.String r7 = new java.lang.String     // Catch: java.lang.Throwable -> Le3
            r7.<init>(r6)     // Catch: java.lang.Throwable -> Le3
            r6 = r7
        L83:
            r0.d(r5, r6)     // Catch: java.lang.Throwable -> Le3
            com.google.mlkit.common.sdkinternal.model.RemoteModelDownloadManager r5 = r9.zze     // Catch: java.lang.Throwable -> Le3
            r5.updateLatestModelHashAndType(r3)     // Catch: java.lang.Throwable -> Le3
            if (r1 == 0) goto La6
            com.google.mlkit.common.sdkinternal.model.RemoteModelFileManager r1 = r9.zzf     // Catch: java.lang.Throwable -> Le3
            boolean r1 = r1.zza(r2)     // Catch: java.lang.Throwable -> Le3
            if (r1 == 0) goto La6
            java.lang.String r1 = "RemoteModelLoader"
            java.lang.String r3 = "All old models are deleted."
            r0.d(r1, r3)     // Catch: java.lang.Throwable -> Le3
            com.google.mlkit.common.sdkinternal.model.RemoteModelFileManager r1 = r9.zzf     // Catch: java.lang.Throwable -> Le3
            java.io.File r1 = r1.zzc(r2)     // Catch: java.lang.Throwable -> Le3
            java.nio.MappedByteBuffer r4 = r9.zza(r1)     // Catch: java.lang.Throwable -> Le3
        La6:
            goto Ld3
        La7:
            int r1 = r4.intValue()     // Catch: java.lang.Throwable -> Le3
            r3 = 16
            if (r1 != r3) goto Ld2
            com.google.android.gms.internal.mlkit_common.zzdx r1 = r9.zzg     // Catch: java.lang.Throwable -> Le3
            r3 = 0
            com.google.mlkit.common.sdkinternal.ModelType r4 = r9.zzi     // Catch: java.lang.Throwable -> Le3
            com.google.mlkit.common.sdkinternal.model.RemoteModelDownloadManager r5 = r9.zze     // Catch: java.lang.Throwable -> Le3
            int r2 = r5.getFailureReason(r2)     // Catch: java.lang.Throwable -> Le3
            r1.zza(r3, r4, r2)     // Catch: java.lang.Throwable -> Le3
            com.google.mlkit.common.sdkinternal.model.RemoteModelDownloadManager r1 = r9.zze     // Catch: java.lang.Throwable -> Le3
            r1.removeOrCancelDownload()     // Catch: java.lang.Throwable -> Le3
            goto Ld2
        Lc4:
            java.lang.String r1 = "RemoteModelLoader"
            java.lang.String r2 = "No new model is downloading."
            r0.d(r1, r2)     // Catch: java.lang.Throwable -> Le3
            com.google.mlkit.common.sdkinternal.model.RemoteModelDownloadManager r1 = r9.zze     // Catch: java.lang.Throwable -> Le3
            r1.removeOrCancelDownload()     // Catch: java.lang.Throwable -> Le3
        Ld2:
            r4 = 0
        Ld3:
            if (r4 != 0) goto Le1
            java.lang.String r1 = "RemoteModelLoader"
            java.lang.String r2 = "Loading existing model file."
            r0.d(r1, r2)     // Catch: java.lang.Throwable -> Le3
            java.nio.MappedByteBuffer r4 = r9.zza()     // Catch: java.lang.Throwable -> Le3
        Le1:
            monitor-exit(r9)
            return r4
        Le3:
            r0 = move-exception
            monitor-exit(r9)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.mlkit.common.sdkinternal.model.RemoteModelLoader.load():java.nio.MappedByteBuffer");
    }

    public RemoteModel getRemoteModel() {
        return this.zzd;
    }

    private final MappedByteBuffer zza(File file) throws MlKitException {
        try {
            return zza(file.getAbsolutePath());
        } catch (Exception e) {
            this.zzf.zzb(file);
            throw new MlKitException("Failed to load newly downloaded model.", 14, e);
        }
    }

    private final MappedByteBuffer zza() throws MlKitException {
        String zza2 = this.zzf.zza();
        if (zza2 == null) {
            zza.d("RemoteModelLoader", "No existing model file");
            return null;
        }
        try {
            return zza(zza2);
        } catch (Exception e) {
            this.zzf.zzb(new File(zza2));
            SharedPrefManager.getInstance(this.zzc).clearLatestModelHash(this.zzd);
            throw new MlKitException("Failed to load an already downloaded model.", 14, e);
        }
    }

    private final MappedByteBuffer zza(String str) throws MlKitException {
        return this.zzh.loadModelAtPath(str);
    }
}
