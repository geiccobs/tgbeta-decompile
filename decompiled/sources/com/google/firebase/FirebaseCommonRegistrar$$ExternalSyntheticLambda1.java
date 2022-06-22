package com.google.firebase;

import android.content.Context;
import com.google.firebase.platforminfo.LibraryVersionComponent;
/* loaded from: classes.dex */
public final /* synthetic */ class FirebaseCommonRegistrar$$ExternalSyntheticLambda1 implements LibraryVersionComponent.VersionExtractor {
    public static final /* synthetic */ FirebaseCommonRegistrar$$ExternalSyntheticLambda1 INSTANCE = new FirebaseCommonRegistrar$$ExternalSyntheticLambda1();

    private /* synthetic */ FirebaseCommonRegistrar$$ExternalSyntheticLambda1() {
    }

    @Override // com.google.firebase.platforminfo.LibraryVersionComponent.VersionExtractor
    public final String extract(Object obj) {
        String lambda$getComponents$0;
        lambda$getComponents$0 = FirebaseCommonRegistrar.lambda$getComponents$0((Context) obj);
        return lambda$getComponents$0;
    }
}
