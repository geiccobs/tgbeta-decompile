package com.google.mlkit.nl.languageid;

import android.content.Context;
import com.google.android.gms.internal.mlkit_language_id.zzcv;
import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.ComponentFactory;
import com.google.mlkit.nl.languageid.internal.LanguageIdentificationJni;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
final /* synthetic */ class zzb implements ComponentFactory {
    static final ComponentFactory zza = new zzb();

    private zzb() {
    }

    @Override // com.google.firebase.components.ComponentFactory
    public final Object create(ComponentContainer componentContainer) {
        return new LanguageIdentificationJni((Context) componentContainer.get(Context.class), (zzcv) componentContainer.get(zzcv.class));
    }
}
