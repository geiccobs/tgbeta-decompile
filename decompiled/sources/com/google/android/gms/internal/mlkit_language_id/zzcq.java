package com.google.android.gms.internal.mlkit_language_id;

import android.content.Context;
import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.ComponentFactory;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
final /* synthetic */ class zzcq implements ComponentFactory {
    static final ComponentFactory zza = new zzcq();

    private zzcq() {
    }

    @Override // com.google.firebase.components.ComponentFactory
    public final Object create(ComponentContainer componentContainer) {
        return new zzcr((Context) componentContainer.get(Context.class));
    }
}
