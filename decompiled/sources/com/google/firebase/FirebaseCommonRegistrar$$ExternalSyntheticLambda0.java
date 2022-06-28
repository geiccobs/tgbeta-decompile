package com.google.firebase;

import android.content.Context;
import com.google.firebase.platforminfo.LibraryVersionComponent;
/* loaded from: classes3.dex */
public final /* synthetic */ class FirebaseCommonRegistrar$$ExternalSyntheticLambda0 implements LibraryVersionComponent.VersionExtractor {
    public static final /* synthetic */ FirebaseCommonRegistrar$$ExternalSyntheticLambda0 INSTANCE = new FirebaseCommonRegistrar$$ExternalSyntheticLambda0();

    private /* synthetic */ FirebaseCommonRegistrar$$ExternalSyntheticLambda0() {
    }

    @Override // com.google.firebase.platforminfo.LibraryVersionComponent.VersionExtractor
    public final String extract(Object obj) {
        return FirebaseCommonRegistrar.lambda$getComponents$0((Context) obj);
    }
}
