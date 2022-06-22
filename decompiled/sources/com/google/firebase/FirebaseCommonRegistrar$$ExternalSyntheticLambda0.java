package com.google.firebase;

import android.content.Context;
import com.google.firebase.platforminfo.LibraryVersionComponent;
/* loaded from: classes.dex */
public final /* synthetic */ class FirebaseCommonRegistrar$$ExternalSyntheticLambda0 implements LibraryVersionComponent.VersionExtractor {
    public static final /* synthetic */ FirebaseCommonRegistrar$$ExternalSyntheticLambda0 INSTANCE = new FirebaseCommonRegistrar$$ExternalSyntheticLambda0();

    private /* synthetic */ FirebaseCommonRegistrar$$ExternalSyntheticLambda0() {
    }

    @Override // com.google.firebase.platforminfo.LibraryVersionComponent.VersionExtractor
    public final String extract(Object obj) {
        String lambda$getComponents$3;
        lambda$getComponents$3 = FirebaseCommonRegistrar.lambda$getComponents$3((Context) obj);
        return lambda$getComponents$3;
    }
}
