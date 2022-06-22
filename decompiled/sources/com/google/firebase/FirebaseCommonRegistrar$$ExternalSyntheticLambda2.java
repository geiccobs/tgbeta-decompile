package com.google.firebase;

import android.content.Context;
import com.google.firebase.platforminfo.LibraryVersionComponent;
/* loaded from: classes.dex */
public final /* synthetic */ class FirebaseCommonRegistrar$$ExternalSyntheticLambda2 implements LibraryVersionComponent.VersionExtractor {
    public static final /* synthetic */ FirebaseCommonRegistrar$$ExternalSyntheticLambda2 INSTANCE = new FirebaseCommonRegistrar$$ExternalSyntheticLambda2();

    private /* synthetic */ FirebaseCommonRegistrar$$ExternalSyntheticLambda2() {
    }

    @Override // com.google.firebase.platforminfo.LibraryVersionComponent.VersionExtractor
    public final String extract(Object obj) {
        String lambda$getComponents$1;
        lambda$getComponents$1 = FirebaseCommonRegistrar.lambda$getComponents$1((Context) obj);
        return lambda$getComponents$1;
    }
}
