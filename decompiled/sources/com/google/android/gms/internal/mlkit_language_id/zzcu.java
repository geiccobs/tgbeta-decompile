package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.common.internal.LibraryVersion;
import java.util.concurrent.Callable;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final /* synthetic */ class zzcu implements Callable {
    static final Callable zza = new zzcu();

    private zzcu() {
    }

    @Override // java.util.concurrent.Callable
    public final Object call() {
        String version;
        version = LibraryVersion.getInstance().getVersion("language-id");
        return version;
    }
}
