package com.google.firebase.installations;

import com.google.firebase.installations.local.PersistedInstallationEntry;
/* loaded from: classes3.dex */
interface StateListener {
    boolean onException(Exception exc);

    boolean onStateReached(PersistedInstallationEntry persistedInstallationEntry);
}
