package com.google.android.gms.internal.wearable;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzz extends AbstractList<Float> implements RandomAccess, Serializable {
    final float[] zza;
    final int zzb;
    final int zzc;

    public zzz(float[] fArr, int i, int i2) {
        this.zza = fArr;
        this.zzb = i;
        this.zzc = i2;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final boolean contains(Object obj) {
        return (obj instanceof Float) && zzaa.zzb(this.zza, ((Float) obj).floatValue(), this.zzb, this.zzc) != -1;
    }

    @Override // java.util.AbstractList, java.util.Collection, java.util.List
    public final boolean equals(@NullableDecl Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof zzz) {
            zzz zzzVar = (zzz) obj;
            int i = this.zzc - this.zzb;
            if (zzzVar.zzc - zzzVar.zzb != i) {
                return false;
            }
            for (int i2 = 0; i2 < i; i2++) {
                if (this.zza[this.zzb + i2] != zzzVar.zza[zzzVar.zzb + i2]) {
                    return false;
                }
            }
            return true;
        }
        return super.equals(obj);
    }

    @Override // java.util.AbstractList, java.util.List
    public final /* bridge */ /* synthetic */ Object get(int i) {
        zzx.zza(i, this.zzc - this.zzb, "index");
        return Float.valueOf(this.zza[this.zzb + i]);
    }

    @Override // java.util.AbstractList, java.util.Collection, java.util.List
    public final int hashCode() {
        int i = 1;
        for (int i2 = this.zzb; i2 < this.zzc; i2++) {
            i = (i * 31) + Float.valueOf(this.zza[i2]).hashCode();
        }
        return i;
    }

    @Override // java.util.AbstractList, java.util.List
    public final int indexOf(Object obj) {
        int zzb;
        if (!(obj instanceof Float) || (zzb = zzaa.zzb(this.zza, ((Float) obj).floatValue(), this.zzb, this.zzc)) < 0) {
            return -1;
        }
        return zzb - this.zzb;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final boolean isEmpty() {
        return false;
    }

    @Override // java.util.AbstractList, java.util.List
    public final int lastIndexOf(Object obj) {
        if (obj instanceof Float) {
            float[] fArr = this.zza;
            float floatValue = ((Float) obj).floatValue();
            int i = this.zzb;
            int i2 = this.zzc - 1;
            while (true) {
                if (i2 < i) {
                    i2 = -1;
                    break;
                } else if (fArr[i2] == floatValue) {
                    break;
                } else {
                    i2--;
                }
            }
            if (i2 >= 0) {
                return i2 - this.zzb;
            }
        }
        return -1;
    }

    @Override // java.util.AbstractList, java.util.List
    public final /* bridge */ /* synthetic */ Object set(int i, Object obj) {
        Float f = (Float) obj;
        zzx.zza(i, this.zzc - this.zzb, "index");
        float[] fArr = this.zza;
        int i2 = this.zzb + i;
        float f2 = fArr[i2];
        if (f != null) {
            fArr[i2] = f.floatValue();
            return Float.valueOf(f2);
        }
        throw null;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final int size() {
        return this.zzc - this.zzb;
    }

    @Override // java.util.AbstractList, java.util.List
    public final List<Float> subList(int i, int i2) {
        zzx.zzb(i, i2, this.zzc - this.zzb);
        if (i == i2) {
            return Collections.emptyList();
        }
        float[] fArr = this.zza;
        int i3 = this.zzb;
        return new zzz(fArr, i + i3, i3 + i2);
    }

    @Override // java.util.AbstractCollection
    public final String toString() {
        StringBuilder sb = new StringBuilder((this.zzc - this.zzb) * 12);
        sb.append('[');
        sb.append(this.zza[this.zzb]);
        int i = this.zzb;
        while (true) {
            i++;
            if (i < this.zzc) {
                sb.append(", ");
                sb.append(this.zza[i]);
            } else {
                sb.append(']');
                return sb.toString();
            }
        }
    }
}
