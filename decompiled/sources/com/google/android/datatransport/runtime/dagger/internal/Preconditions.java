package com.google.android.datatransport.runtime.dagger.internal;
/* loaded from: classes3.dex */
public final class Preconditions {
    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static <T> T checkNotNull(T reference, String errorMessage) {
        if (reference == null) {
            throw new NullPointerException(errorMessage);
        }
        return reference;
    }

    public static <T> T checkNotNull(T reference, String errorMessageTemplate, Object errorMessageArg) {
        String argString;
        if (reference == null) {
            if (errorMessageTemplate.contains("%s")) {
                if (errorMessageTemplate.indexOf("%s") != errorMessageTemplate.lastIndexOf("%s")) {
                    throw new IllegalArgumentException("errorMessageTemplate has more than one format specifier");
                }
                if (errorMessageArg instanceof Class) {
                    argString = ((Class) errorMessageArg).getCanonicalName();
                } else {
                    argString = String.valueOf(errorMessageArg);
                }
                throw new NullPointerException(errorMessageTemplate.replace("%s", argString));
            }
            throw new IllegalArgumentException("errorMessageTemplate has no format specifiers");
        }
        return reference;
    }

    public static <T> void checkBuilderRequirement(T requirement, Class<T> clazz) {
        if (requirement == null) {
            throw new IllegalStateException(clazz.getCanonicalName() + " must be set");
        }
    }

    private Preconditions() {
    }
}
