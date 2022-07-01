package com.google.mlkit.nl.languageid;

import android.content.Context;
import com.google.android.gms.internal.mlkit_language_id.zzcr;
import com.google.android.gms.internal.mlkit_language_id.zzct;
import com.google.android.gms.internal.mlkit_language_id.zzcv;
import com.google.android.gms.internal.mlkit_language_id.zzcz;
import com.google.android.gms.internal.mlkit_language_id.zzk;
import com.google.firebase.components.Component;
import com.google.firebase.components.ComponentRegistrar;
import com.google.firebase.components.Dependency;
import com.google.mlkit.common.sdkinternal.ExecutorSelector;
import com.google.mlkit.nl.languageid.LanguageIdentifierImpl;
import com.google.mlkit.nl.languageid.internal.LanguageIdentificationJni;
import java.util.List;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public class LanguageIdRegistrar implements ComponentRegistrar {
    @Override // com.google.firebase.components.ComponentRegistrar
    public List<Component<?>> getComponents() {
        return zzk.zza(zzcv.zza, zzcz.zza, zzct.zza, zzcr.zza, Component.builder(LanguageIdentificationJni.class).add(Dependency.required(Context.class)).add(Dependency.required(zzcv.class)).factory(zzb.zza).build(), Component.builder(LanguageIdentifierImpl.Factory.class).add(Dependency.required(zzcv.class)).add(Dependency.required(LanguageIdentificationJni.class)).add(Dependency.required(ExecutorSelector.class)).factory(zza.zza).build());
    }
}
