package com.google.android.gms.internal.mlkit_common;

import android.content.Context;
import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.ComponentFactory;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes.dex */
final /* synthetic */ class zzdn implements ComponentFactory {
    static final ComponentFactory zza = new zzdn();

    private zzdn() {
    }

    @Override // com.google.firebase.components.ComponentFactory
    public final Object create(ComponentContainer componentContainer) {
        return new zzdo((Context) componentContainer.get(Context.class));
    }
}
