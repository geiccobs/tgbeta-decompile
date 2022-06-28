package com.google.firebase.appindexing.internal;

import android.content.Context;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseAppIndexingInvalidArgumentException;
import com.google.firebase.appindexing.Indexable;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class zzp extends FirebaseAppIndex {
    final zzo zza;
    private final GoogleApi<?> zzb;
    private final Context zzc;

    public zzp(Context context) {
        zzj zzjVar = new zzj(context);
        this.zzb = zzjVar;
        this.zzc = context;
        this.zza = new zzo(zzjVar);
    }

    @Override // com.google.firebase.appindexing.FirebaseAppIndex
    public final Task<Void> remove(String... strArr) {
        return this.zza.zza(new zzz(3, null, strArr, null, null, null, null));
    }

    @Override // com.google.firebase.appindexing.FirebaseAppIndex
    public final Task<Void> removeAll() {
        return this.zza.zza(new zzz(4, null, null, null, null, null, null));
    }

    @Override // com.google.firebase.appindexing.FirebaseAppIndex
    public final Task<Void> update(Indexable... indexableArr) {
        Thing[] thingArr;
        if (indexableArr == null) {
            thingArr = null;
        } else {
            try {
                int length = indexableArr.length;
                Thing[] thingArr2 = new Thing[length];
                System.arraycopy(indexableArr, 0, thingArr2, 0, length);
                thingArr = thingArr2;
            } catch (ArrayStoreException e) {
                return Tasks.forException(new FirebaseAppIndexingInvalidArgumentException("Custom Indexable-objects are not allowed. Please use the 'Indexables'-class for creating the objects."));
            }
        }
        if (thingArr != null) {
            return this.zza.zza(new zzz(1, thingArr, null, null, null, null, null));
        }
        return Tasks.forException(new FirebaseAppIndexingInvalidArgumentException("Indexables cannot be null."));
    }
}
