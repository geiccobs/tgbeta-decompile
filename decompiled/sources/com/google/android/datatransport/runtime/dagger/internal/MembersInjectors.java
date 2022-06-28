package com.google.android.datatransport.runtime.dagger.internal;

import com.google.android.datatransport.runtime.dagger.MembersInjector;
/* loaded from: classes3.dex */
public final class MembersInjectors {
    public static <T> MembersInjector<T> noOp() {
        return NoOpMembersInjector.INSTANCE;
    }

    /* loaded from: classes3.dex */
    private enum NoOpMembersInjector implements MembersInjector<Object> {
        INSTANCE;

        @Override // com.google.android.datatransport.runtime.dagger.MembersInjector
        public void injectMembers(Object instance) {
            Preconditions.checkNotNull(instance, "Cannot inject members into a null reference");
        }
    }

    private MembersInjectors() {
    }
}
