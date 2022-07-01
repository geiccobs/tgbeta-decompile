package com.google.android.gms.internal.mlkit_common;

import com.google.android.gms.common.internal.LibraryVersion;
import java.util.concurrent.Callable;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes.dex */
public final /* synthetic */ class zzdr implements Callable {
    static final Callable zza = new zzdr();

    private zzdr() {
    }

    @Override // java.util.concurrent.Callable
    public final Object call() {
        String version;
        version = LibraryVersion.getInstance().getVersion("common");
        return version;
    }
}
