package com.google.android.gms.auth.api.signin;

import com.google.android.gms.common.api.Scope;
import java.util.Comparator;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public final /* synthetic */ class zaa implements Comparator {
    static final Comparator zaa = new zaa();

    private zaa() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int compareTo;
        compareTo = ((Scope) obj).getScopeUri().compareTo(((Scope) obj2).getScopeUri());
        return compareTo;
    }
}
