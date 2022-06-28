package com.google.mlkit.nl.languageid;

import com.google.android.gms.common.internal.Preconditions;
import com.google.mlkit.common.sdkinternal.MlKitContext;
import com.google.mlkit.nl.languageid.LanguageIdentifierImpl;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
public class LanguageIdentification {
    private LanguageIdentification() {
    }

    public static LanguageIdentifier getClient() {
        return ((LanguageIdentifierImpl.Factory) MlKitContext.getInstance().get(LanguageIdentifierImpl.Factory.class)).create(LanguageIdentificationOptions.zza);
    }

    public static LanguageIdentifier getClient(LanguageIdentificationOptions languageIdentificationOptions) {
        Preconditions.checkNotNull(languageIdentificationOptions, "LanguageIdentificationOptions can not be null");
        return ((LanguageIdentifierImpl.Factory) MlKitContext.getInstance().get(LanguageIdentifierImpl.Factory.class)).create(languageIdentificationOptions);
    }
}
