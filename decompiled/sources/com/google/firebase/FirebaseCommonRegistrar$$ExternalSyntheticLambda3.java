package com.google.firebase;

import android.content.Context;
import com.google.firebase.platforminfo.LibraryVersionComponent;
/* loaded from: classes.dex */
public final /* synthetic */ class FirebaseCommonRegistrar$$ExternalSyntheticLambda3 implements LibraryVersionComponent.VersionExtractor {
    public static final /* synthetic */ FirebaseCommonRegistrar$$ExternalSyntheticLambda3 INSTANCE = new FirebaseCommonRegistrar$$ExternalSyntheticLambda3();

    private /* synthetic */ FirebaseCommonRegistrar$$ExternalSyntheticLambda3() {
    }

    @Override // com.google.firebase.platforminfo.LibraryVersionComponent.VersionExtractor
    public final String extract(Object obj) {
        String lambda$getComponents$2;
        lambda$getComponents$2 = FirebaseCommonRegistrar.lambda$getComponents$2((Context) obj);
        return lambda$getComponents$2;
    }
}
