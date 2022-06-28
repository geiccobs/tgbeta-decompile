package com.google.android.gms.internal.play_billing;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import javax.annotation.CheckForNull;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: classes.dex */
public abstract class zzu extends zzr implements List, RandomAccess {
    private static final zzai zza = new zzs(zzaa.zza, 0);

    public static zzu zzi(Object[] objArr) {
        return zzj(objArr, objArr.length);
    }

    static zzu zzj(Object[] objArr, int i) {
        return i == 0 ? zzaa.zza : new zzaa(objArr, i);
    }

    public static zzu zzk(Collection collection) {
        if (collection instanceof zzr) {
            zzu zzd = ((zzr) collection).zzd();
            if (!zzd.zzf()) {
                return zzd;
            }
            Object[] array = zzd.toArray();
            return zzj(array, array.length);
        }
        Object[] array2 = collection.toArray();
        int length = array2.length;
        for (int i = 0; i < length; i++) {
            if (array2[i] == null) {
                throw new NullPointerException("at index " + i);
            }
        }
        return zzj(array2, length);
    }

    public static zzu zzl() {
        return zzaa.zza;
    }

    @Override // java.util.List
    @Deprecated
    public final void add(int i, Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.List
    @Deprecated
    public final boolean addAll(int i, Collection collection) {
        throw new UnsupportedOperationException();
    }

    @Override // com.google.android.gms.internal.play_billing.zzr, java.util.AbstractCollection, java.util.Collection, java.util.Set
    public final boolean contains(@CheckForNull Object obj) {
        return indexOf(obj) >= 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x0042  */
    @Override // java.util.Collection, java.util.List
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final boolean equals(@javax.annotation.CheckForNull java.lang.Object r7) {
        /*
            r6 = this;
            r0 = 1
            r1 = 0
            if (r7 != r6) goto L5
            goto L62
        L5:
            boolean r2 = r7 instanceof java.util.List
            if (r2 != 0) goto Lb
            r0 = 0
            goto L62
        Lb:
            java.util.List r7 = (java.util.List) r7
            int r2 = r6.size()
            int r3 = r7.size()
            if (r2 == r3) goto L19
            r0 = 0
            goto L62
        L19:
            boolean r3 = r7 instanceof java.util.RandomAccess
            if (r3 == 0) goto L34
            r3 = 0
        L1e:
            if (r3 >= r2) goto L33
            java.lang.Object r4 = r6.get(r3)
            java.lang.Object r5 = r7.get(r3)
            boolean r4 = com.google.android.gms.internal.play_billing.zzl.zza(r4, r5)
            if (r4 != 0) goto L30
            r0 = 0
            goto L62
        L30:
            int r3 = r3 + 1
            goto L1e
        L33:
            goto L62
        L34:
            java.util.Iterator r2 = r6.iterator()
            java.util.Iterator r7 = r7.iterator()
        L3c:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L5a
            boolean r3 = r7.hasNext()
            if (r3 != 0) goto L4a
            r0 = 0
            goto L62
        L4a:
            java.lang.Object r3 = r2.next()
            java.lang.Object r4 = r7.next()
            boolean r3 = com.google.android.gms.internal.play_billing.zzl.zza(r3, r4)
            if (r3 != 0) goto L3c
            r0 = 0
            goto L62
        L5a:
            boolean r7 = r7.hasNext()
            if (r7 != 0) goto L61
            goto L62
        L61:
            r0 = 0
        L62:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.play_billing.zzu.equals(java.lang.Object):boolean");
    }

    @Override // java.util.Collection, java.util.List
    public final int hashCode() {
        int size = size();
        int i = 1;
        for (int i2 = 0; i2 < size; i2++) {
            i = (i * 31) + get(i2).hashCode();
        }
        return i;
    }

    @Override // java.util.List
    public final int indexOf(@CheckForNull Object obj) {
        if (obj == null) {
            return -1;
        }
        int size = size();
        for (int i = 0; i < size; i++) {
            if (obj.equals(get(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override // com.google.android.gms.internal.play_billing.zzr, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
    public final /* synthetic */ Iterator iterator() {
        return listIterator(0);
    }

    @Override // java.util.List
    public final int lastIndexOf(@CheckForNull Object obj) {
        if (obj == null) {
            return -1;
        }
        for (int size = size() - 1; size >= 0; size--) {
            if (obj.equals(get(size))) {
                return size;
            }
        }
        return -1;
    }

    @Override // java.util.List
    public final /* synthetic */ ListIterator listIterator() {
        return listIterator(0);
    }

    @Override // java.util.List
    @Deprecated
    public final Object remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.List
    @Deprecated
    public final Object set(int i, Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override // com.google.android.gms.internal.play_billing.zzr
    public int zza(Object[] objArr, int i) {
        int size = size();
        for (int i2 = 0; i2 < size; i2++) {
            objArr[i2] = get(i2);
        }
        return size;
    }

    @Override // com.google.android.gms.internal.play_billing.zzr
    @Deprecated
    public final zzu zzd() {
        return this;
    }

    @Override // com.google.android.gms.internal.play_billing.zzr
    public final zzah zze() {
        return listIterator(0);
    }

    /* renamed from: zzh */
    public zzu subList(int i, int i2) {
        zzm.zzd(i, i2, size());
        int i3 = i2 - i;
        if (i3 == size()) {
            return this;
        }
        if (i3 != 0) {
            return new zzt(this, i, i3);
        }
        return zzaa.zza;
    }

    /* renamed from: zzm */
    public final zzai listIterator(int i) {
        zzm.zzb(i, size(), "index");
        return isEmpty() ? zza : new zzs(this, i);
    }
}
