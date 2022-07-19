package com.google.android.gms.internal.clearcut;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
/* loaded from: classes.dex */
final class zzaz extends zzav<Boolean> implements zzcn<Boolean> {
    private int size;
    private boolean[] zzfh;

    static {
        new zzaz().zzv();
    }

    zzaz() {
        this(new boolean[10], 0);
    }

    private zzaz(boolean[] zArr, int i) {
        this.zzfh = zArr;
        this.size = i;
    }

    private final void zza(int i, boolean z) {
        int i2;
        zzw();
        if (i < 0 || i > (i2 = this.size)) {
            throw new IndexOutOfBoundsException(zzh(i));
        }
        boolean[] zArr = this.zzfh;
        if (i2 < zArr.length) {
            System.arraycopy(zArr, i, zArr, i + 1, i2 - i);
        } else {
            boolean[] zArr2 = new boolean[((i2 * 3) / 2) + 1];
            System.arraycopy(zArr, 0, zArr2, 0, i);
            System.arraycopy(this.zzfh, i, zArr2, i + 1, this.size - i);
            this.zzfh = zArr2;
        }
        this.zzfh[i] = z;
        this.size++;
        ((AbstractList) this).modCount++;
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

    @Override // java.util.AbstractList, java.util.List
    public final /* synthetic */ void add(int i, Object obj) {
        zza(i, ((Boolean) obj).booleanValue());
    }

    @Override // com.google.android.gms.internal.clearcut.zzav, java.util.AbstractCollection, java.util.Collection, java.util.List
    public final boolean addAll(Collection<? extends Boolean> collection) {
        zzw();
        zzci.checkNotNull(collection);
        if (!(collection instanceof zzaz)) {
            return super.addAll(collection);
        }
        zzaz zzazVar = (zzaz) collection;
        int i = zzazVar.size;
        if (i == 0) {
            return false;
        }
        int i2 = this.size;
        if (Integer.MAX_VALUE - i2 < i) {
            throw new OutOfMemoryError();
        }
        int i3 = i2 + i;
        boolean[] zArr = this.zzfh;
        if (i3 > zArr.length) {
            this.zzfh = Arrays.copyOf(zArr, i3);
        }
        System.arraycopy(zzazVar.zzfh, 0, this.zzfh, this.size, zzazVar.size);
        this.size = i3;
        ((AbstractList) this).modCount++;
        return true;
    }

    public final void addBoolean(boolean z) {
        zza(this.size, z);
    }

    @Override // com.google.android.gms.internal.clearcut.zzav, java.util.AbstractList, java.util.Collection, java.util.List
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzaz)) {
            return super.equals(obj);
        }
        zzaz zzazVar = (zzaz) obj;
        if (this.size != zzazVar.size) {
            return false;
        }
        boolean[] zArr = zzazVar.zzfh;
        for (int i = 0; i < this.size; i++) {
            if (this.zzfh[i] != zArr[i]) {
                return false;
            }
        }
        return true;
    }

    @Override // java.util.AbstractList, java.util.List
    public final /* synthetic */ Object get(int i) {
        zzg(i);
        return Boolean.valueOf(this.zzfh[i]);
    }

    @Override // com.google.android.gms.internal.clearcut.zzav, java.util.AbstractList, java.util.Collection, java.util.List
    public final int hashCode() {
        int i = 1;
        for (int i2 = 0; i2 < this.size; i2++) {
            i = (i * 31) + zzci.zzc(this.zzfh[i2]);
        }
        return i;
    }

    @Override // java.util.AbstractList, java.util.List
    public final /* synthetic */ Object remove(int i) {
        zzw();
        zzg(i);
        boolean[] zArr = this.zzfh;
        boolean z = zArr[i];
        int i2 = this.size;
        if (i < i2 - 1) {
            System.arraycopy(zArr, i + 1, zArr, i, i2 - i);
        }
        this.size--;
        ((AbstractList) this).modCount++;
        return Boolean.valueOf(z);
    }

    @Override // com.google.android.gms.internal.clearcut.zzav, java.util.AbstractCollection, java.util.Collection, java.util.List
    public final boolean remove(Object obj) {
        zzw();
        for (int i = 0; i < this.size; i++) {
            if (obj.equals(Boolean.valueOf(this.zzfh[i]))) {
                boolean[] zArr = this.zzfh;
                System.arraycopy(zArr, i + 1, zArr, i, this.size - i);
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
            boolean[] zArr = this.zzfh;
            System.arraycopy(zArr, i2, zArr, i, this.size - i2);
            this.size -= i2 - i;
            ((AbstractList) this).modCount++;
            return;
        }
        throw new IndexOutOfBoundsException("toIndex < fromIndex");
    }

    @Override // java.util.AbstractList, java.util.List
    public final /* synthetic */ Object set(int i, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        zzw();
        zzg(i);
        boolean[] zArr = this.zzfh;
        boolean z = zArr[i];
        zArr[i] = booleanValue;
        return Boolean.valueOf(z);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final int size() {
        return this.size;
    }

    @Override // com.google.android.gms.internal.clearcut.zzcn
    public final /* synthetic */ zzcn<Boolean> zzi(int i) {
        if (i >= this.size) {
            return new zzaz(Arrays.copyOf(this.zzfh, i), this.size);
        }
        throw new IllegalArgumentException();
    }
}
