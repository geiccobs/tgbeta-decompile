package com.google.firebase.appindexing.internal;

import android.os.Parcelable;
import java.util.Comparator;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final /* synthetic */ class zzab implements Comparator {
    static final Comparator zza = new zzab();

    private zzab() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        String str = (String) obj;
        String str2 = (String) obj2;
        Parcelable.Creator<Thing> creator = Thing.CREATOR;
        if (str == null) {
            return str2 != null ? -1 : 0;
        } else if (str2 != null) {
            return str.compareTo(str2);
        } else {
            return 1;
        }
    }
}
