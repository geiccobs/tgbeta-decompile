package com.google.mlkit.common.sdkinternal.model;

import com.google.android.gms.common.internal.GmsLogger;
import com.google.android.gms.common.internal.Preconditions;
import com.google.mlkit.common.MlKitException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public class ModelLoader {
    private static final GmsLogger zza = new GmsLogger("ModelLoader", "");
    public final LocalModelLoader localModelLoader;
    protected ModelLoadingState modelLoadingState = ModelLoadingState.NO_MODEL_LOADED;
    public final RemoteModelLoader remoteModelLoader;
    private final ModelLoadingLogger zzb;

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public interface ModelContentHandler {
        void constructModel(MappedByteBuffer mappedByteBuffer) throws MlKitException;
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public interface ModelLoadingLogger {
        void logErrorCodes(List<Integer> list);
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    protected enum ModelLoadingState {
        NO_MODEL_LOADED,
        REMOTE_MODEL_LOADED,
        LOCAL_MODEL_LOADED
    }

    public ModelLoader(RemoteModelLoader remoteModelLoader, LocalModelLoader localModelLoader, ModelLoadingLogger modelLoadingLogger) {
        Preconditions.checkArgument((remoteModelLoader == null && localModelLoader == null) ? false : true, "At least one of RemoteModelLoader or LocalModelLoader must be non-null.");
        Preconditions.checkNotNull(modelLoadingLogger);
        this.remoteModelLoader = remoteModelLoader;
        this.localModelLoader = localModelLoader;
        this.zzb = modelLoadingLogger;
    }

    public synchronized void loadWithModelContentHandler(ModelContentHandler modelContentHandler) throws MlKitException {
        Exception exc;
        boolean z;
        ArrayList arrayList = new ArrayList();
        boolean z2 = false;
        Exception e = null;
        try {
            z = zza(modelContentHandler, arrayList);
            exc = null;
        } catch (Exception e2) {
            exc = e2;
            z = false;
        }
        if (z) {
            this.zzb.logErrorCodes(arrayList);
            this.modelLoadingState = ModelLoadingState.REMOTE_MODEL_LOADED;
            return;
        }
        try {
            z2 = zzb(modelContentHandler, arrayList);
        } catch (Exception e3) {
            e = e3;
        }
        if (z2) {
            this.zzb.logErrorCodes(arrayList);
            this.modelLoadingState = ModelLoadingState.LOCAL_MODEL_LOADED;
            return;
        }
        arrayList.add(17);
        this.zzb.logErrorCodes(arrayList);
        this.modelLoadingState = ModelLoadingState.NO_MODEL_LOADED;
        if (exc != null) {
            String valueOf = String.valueOf(zza());
            throw new MlKitException(valueOf.length() != 0 ? "Remote model load failed with the model options: ".concat(valueOf) : new String("Remote model load failed with the model options: "), 14, exc);
        } else if (e != null) {
            String valueOf2 = String.valueOf(zza());
            throw new MlKitException(valueOf2.length() != 0 ? "Local model load failed with the model options: ".concat(valueOf2) : new String("Local model load failed with the model options: "), 14, e);
        } else {
            String valueOf3 = String.valueOf(zza());
            throw new MlKitException(valueOf3.length() != 0 ? "Cannot load any model with the model options: ".concat(valueOf3) : new String("Cannot load any model with the model options: "), 14);
        }
    }

    public synchronized boolean isRemoteModelLoaded() {
        return this.modelLoadingState == ModelLoadingState.REMOTE_MODEL_LOADED;
    }

    private final synchronized boolean zza(ModelContentHandler modelContentHandler, List<Integer> list) throws MlKitException {
        RemoteModelLoader remoteModelLoader = this.remoteModelLoader;
        if (remoteModelLoader != null) {
            try {
                MappedByteBuffer load = remoteModelLoader.load();
                if (load != null) {
                    try {
                        modelContentHandler.constructModel(load);
                        zza.d("ModelLoader", "Remote model source is loaded successfully");
                        return true;
                    } catch (RuntimeException e) {
                        list.add(19);
                        throw e;
                    }
                }
                zza.d("ModelLoader", "Remote model source can NOT be loaded, try local model.");
                list.add(21);
            } catch (MlKitException e2) {
                zza.d("ModelLoader", "Remote model source can NOT be loaded, try local model.");
                list.add(20);
                throw e2;
            }
        }
        return false;
    }

    private final synchronized boolean zzb(ModelContentHandler modelContentHandler, List<Integer> list) throws MlKitException {
        MappedByteBuffer load;
        LocalModelLoader localModelLoader = this.localModelLoader;
        if (localModelLoader != null && (load = localModelLoader.load()) != null) {
            try {
                modelContentHandler.constructModel(load);
                zza.d("ModelLoader", "Local model source is loaded successfully");
                return true;
            } catch (RuntimeException e) {
                list.add(18);
                throw e;
            }
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0036  */
    /* JADX WARN: Removed duplicated region for block: B:14:0x0039  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private final java.lang.String zza() {
        /*
            r4 = this;
            com.google.mlkit.common.sdkinternal.model.LocalModelLoader r0 = r4.localModelLoader
            if (r0 == 0) goto L31
            com.google.mlkit.common.model.LocalModel r0 = r0.getLocalModel()
            java.lang.String r0 = r0.getAssetFilePath()
            if (r0 == 0) goto L1a
            com.google.mlkit.common.sdkinternal.model.LocalModelLoader r0 = r4.localModelLoader
            com.google.mlkit.common.model.LocalModel r0 = r0.getLocalModel()
            java.lang.String r0 = r0.getAssetFilePath()
            goto L32
        L1a:
            com.google.mlkit.common.sdkinternal.model.LocalModelLoader r0 = r4.localModelLoader
            com.google.mlkit.common.model.LocalModel r0 = r0.getLocalModel()
            java.lang.String r0 = r0.getAbsoluteFilePath()
            if (r0 == 0) goto L31
            com.google.mlkit.common.sdkinternal.model.LocalModelLoader r0 = r4.localModelLoader
            com.google.mlkit.common.model.LocalModel r0 = r0.getLocalModel()
            java.lang.String r0 = r0.getAbsoluteFilePath()
            goto L32
        L31:
            r0 = 0
        L32:
            com.google.mlkit.common.sdkinternal.model.RemoteModelLoader r1 = r4.remoteModelLoader
            if (r1 != 0) goto L39
            java.lang.String r1 = "unspecified"
            goto L41
        L39:
            com.google.mlkit.common.model.RemoteModel r1 = r1.getRemoteModel()
            java.lang.String r1 = r1.getUniqueModelNameForPersist()
        L41:
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r0
            r0 = 1
            r2[r0] = r1
            java.lang.String r0 = "Local model path: %s. Remote model name: %s. "
            java.lang.String r0 = java.lang.String.format(r0, r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.mlkit.common.sdkinternal.model.ModelLoader.zza():java.lang.String");
    }
}
