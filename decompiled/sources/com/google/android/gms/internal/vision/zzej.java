package com.google.android.gms.internal.vision;

import com.google.android.exoplayer2.C;
import java.util.Iterator;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public abstract class zzej<E> extends zzeb<E> implements Set<E> {
    @NullableDecl
    private transient zzee<E> zza;

    public static int zza(int i) {
        int max = Math.max(i, 2);
        boolean z = true;
        if (max < 751619276) {
            int highestOneBit = Integer.highestOneBit(max - 1) << 1;
            while (true) {
                double d = highestOneBit;
                Double.isNaN(d);
                if (d * 0.7d < max) {
                    highestOneBit <<= 1;
                } else {
                    return highestOneBit;
                }
            }
        } else {
            if (max >= 1073741824) {
                z = false;
            }
            zzde.zza(z, "collection too large");
            return C.BUFFER_FLAG_ENCRYPTED;
        }
    }

    boolean zzg() {
        return false;
    }

    @Override // java.util.Collection, java.util.Set
    public boolean equals(@NullableDecl Object obj) {
        if (obj == this) {
            return true;
        }
        if ((obj instanceof zzej) && zzg() && ((zzej) obj).zzg() && hashCode() != obj.hashCode()) {
            return false;
        }
        return zzey.zza(this, obj);
    }

    @Override // java.util.Collection, java.util.Set
    public int hashCode() {
        return zzey.zza(this);
    }

    @Override // com.google.android.gms.internal.vision.zzeb
    public zzee<E> zze() {
        zzee<E> zzeeVar = this.zza;
        if (zzeeVar == null) {
            zzee<E> zzh = zzh();
            this.zza = zzh;
            return zzh;
        }
        return zzeeVar;
    }

    zzee<E> zzh() {
        return zzee.zza(toArray());
    }

    @Override // com.google.android.gms.internal.vision.zzeb, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public /* synthetic */ Iterator iterator() {
        return iterator();
    }
}
