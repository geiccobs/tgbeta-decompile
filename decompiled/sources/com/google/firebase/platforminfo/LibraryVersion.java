package com.google.firebase.platforminfo;

import javax.annotation.Nonnull;
/* loaded from: classes.dex */
public abstract class LibraryVersion {
    @Nonnull
    public abstract String getLibraryName();

    @Nonnull
    public abstract String getVersion();

    public static LibraryVersion create(String name, String version) {
        return new AutoValue_LibraryVersion(name, version);
    }
}
