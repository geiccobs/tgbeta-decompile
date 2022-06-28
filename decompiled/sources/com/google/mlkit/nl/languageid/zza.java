package com.google.mlkit.nl.languageid;

import com.google.android.gms.internal.mlkit_language_id.zzcv;
import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.ComponentFactory;
import com.google.mlkit.common.sdkinternal.ExecutorSelector;
import com.google.mlkit.nl.languageid.LanguageIdentifierImpl;
import com.google.mlkit.nl.languageid.internal.LanguageIdentificationJni;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
final /* synthetic */ class zza implements ComponentFactory {
    static final ComponentFactory zza = new zza();

    private zza() {
    }

    @Override // com.google.firebase.components.ComponentFactory
    public final Object create(ComponentContainer componentContainer) {
        return new LanguageIdentifierImpl.Factory((zzcv) componentContainer.get(zzcv.class), (LanguageIdentificationJni) componentContainer.get(LanguageIdentificationJni.class), (ExecutorSelector) componentContainer.get(ExecutorSelector.class));
    }
}
