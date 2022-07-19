package com.google.android.gms.internal.clearcut;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import org.telegram.tgnet.ConnectionsManager;
/* loaded from: classes.dex */
final class zzdc extends zzav<Long> implements zzcn<Long> {
    private int size;
    private long[] zzlx;

    static {
        new zzdc().zzv();
    }

    zzdc() {
        this(new long[10], 0);
    }

    private zzdc(long[] jArr, int i) {
        this.zzlx = jArr;
        this.size = i;
    }

    private final void zzg(int i) {
        if (i < 0 || i >= this.size) {
            throw new IndexOutOfBoundsException(zzh(i));
        }
    }

    private final String zzh(int i) {
        int i2 = this.size;
        StringBuilder sb = new StringBuilder(35);
        sb.append("Index:");
        sb.append(i);
        sb.append(", Size:");
        sb.append(i2);
        return sb.toString();
    }

    private final void zzk(int i, long j) {
        int i2;
        zzw();
        if (i < 0 || i > (i2 = this.size)) {
            throw new IndexOutOfBoundsException(zzh(i));
        }
        long[] jArr = this.zzlx;
        if (i2 < jArr.length) {
            System.arraycopy(jArr, i, jArr, i + 1, i2 - i);
        } else {
            long[] jArr2 = new long[((i2 * 3) / 2) + 1];
            System.arraycopy(jArr, 0, jArr2, 0, i);
            System.arraycopy(this.zzlx, i, jArr2, i + 1, this.size - i);
            this.zzlx = jArr2;
        }
        this.zzlx[i] = j;
        this.size++;
        ((AbstractList) this).modCount++;
    }

    @Override // java.util.AbstractList, java.util.List
    public final /* synthetic */ void add(int i, Object obj) {
        zzk(i, ((Long) obj).longValue());
    }

    @Override // com.google.android.gms.internal.clearcut.zzav, java.util.AbstractCollection, java.util.Collection, java.util.List
    public final boolean addAll(Collection<? extends Long> collection) {
        zzw();
        zzci.checkNotNull(collection);
        if (!(collection instanceof zzdc)) {
            return super.addAll(collection);
        }
        zzdc zzdcVar = (zzdc) collection;
        int i = zzdcVar.size;
        if (i == 0) {
            return false;
        }
        int i2 = this.size;
        if (ConnectionsManager.DEFAULT_DATACENTER_ID - i2 < i) {
            throw new OutOfMemoryError();
        }
        int i3 = i2 + i;
        long[] jArr = this.zzlx;
        if (i3 > jArr.length) {
            this.zzlx = Arrays.copyOf(jArr, i3);
        }
        System.arraycopy(zzdcVar.zzlx, 0, this.zzlx, this.size, zzdcVar.size);
        this.size = i3;
        ((AbstractList) this).modCount++;
        return true;
    }

    @Override // com.google.android.gms.internal.clearcut.zzav, java.util.AbstractList, java.util.Collection, java.util.List
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzdc)) {
            return super.equals(obj);
        }
        zzdc zzdcVar = (zzdc) obj;
        if (this.size != zzdcVar.size) {
            return false;
        }
        long[] jArr = zzdcVar.zzlx;
        for (int i = 0; i < this.size; i++) {
            if (this.zzlx[i] != jArr[i]) {
                return false;
            }
        }
        return true;
    }

    @Override // java.util.AbstractList, java.util.List
    public final /* synthetic */ Object get(int i) {
        return Long.valueOf(getLong(i));
    }

    public final long getLong(int i) {
        zzg(i);
        return this.zzlx[i];
    }

    @Override // com.google.android.gms.internal.clearcut.zzav, java.util.AbstractList, java.util.Collection, java.util.List
    public final int hashCode() {
        int i = 1;
        for (int i2 = 0; i2 < this.size; i2++) {
            i = (i * 31) + zzci.zzl(this.zzlx[i2]);
        }
        return i;
    }

    @Override // java.util.AbstractList, java.util.List
    public final /* synthetic */ Object remove(int i) {
        zzw();
        zzg(i);
        long[] jArr = this.zzlx;
        long j = jArr[i];
        int i2 = this.size;
        if (i < i2 - 1) {
            System.arraycopy(jArr, i + 1, jArr, i, i2 - i);
        }
        this.size--;
        ((AbstractList) this).modCount++;
        return Long.valueOf(j);
    }

    @Override // com.google.android.gms.internal.clearcut.zzav, java.util.AbstractCollection, java.util.Collection, java.util.List
    public final boolean remove(Object obj) {
        zzw();
        for (int i = 0; i < this.size; i++) {
            if (obj.equals(Long.valueOf(this.zzlx[i]))) {
                long[] jArr = this.zzlx;
                System.arraycopy(jArr, i + 1, jArr, i, this.size - i);
                this.size--;
                ((AbstractList) this).modCount++;
                return true;
            }
        }
        return false;
    }

    @Override // java.util.AbstractList
    protected final void removeRange(int i, int i2) {
        zzw();
        if (i2 >= i) {
            long[] jArr = this.zzlx;
            System.arraycopy(jArr, i2, jArr, i, this.size - i2);
            this.size -= i2 - i;
            ((AbstractList) this).modCount++;
            return;
        }
        throw new IndexOutOfBoundsException("toIndex < fromIndex");
    }

    @Override // java.util.AbstractList, java.util.List
    public final /* synthetic */ Object set(int i, Object obj) {
        long longValue = ((Long) obj).longValue();
        zzw();
        zzg(i);
        long[] jArr = this.zzlx;
        long j = jArr[i];
        jArr[i] = longValue;
        return Long.valueOf(j);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final int size() {
        return this.size;
    }

    @Override // com.google.android.gms.internal.clearcut.zzcn
    public final /* synthetic */ zzcn<Long> zzi(int i) {
        if (i >= this.size) {
            return new zzdc(Arrays.copyOf(this.zzlx, i), this.size);
        }
        throw new IllegalArgumentException();
    }

    public final void zzm(long j) {
        zzk(this.size, j);
    }
}
