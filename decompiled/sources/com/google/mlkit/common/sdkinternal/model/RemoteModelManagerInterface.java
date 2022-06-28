package com.google.mlkit.common.sdkinternal.model;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModel;
import java.util.Set;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public interface RemoteModelManagerInterface<RemoteT extends RemoteModel> {
    Task<Void> deleteDownloadedModel(RemoteT remotet);

    Task<Void> download(RemoteT remotet, DownloadConditions downloadConditions);

    Task<Set<RemoteT>> getDownloadedModels();

    Task<Boolean> isModelDownloaded(RemoteT remotet);
}
