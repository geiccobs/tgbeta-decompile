package com.google.android.gms.internal.clearcut;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
/* loaded from: classes.dex */
final class zzbq extends zzav<Double> implements zzcn<Double> {
    private int size;
    private double[] zzgk;

    static {
        new zzbq().zzv();
    }

    zzbq() {
        this(new double[10], 0);
    }

    private zzbq(double[] dArr, int i) {
        this.zzgk = dArr;
        this.size = i;
    }

    private final void zzc(int i, double d) {
        int i2;
        zzw();
        if (i < 0 || i > (i2 = this.size)) {
            throw new IndexOutOfBoundsException(zzh(i));
        }
        double[] dArr = this.zzgk;
        if (i2 < dArr.length) {
            System.arraycopy(dArr, i, dArr, i + 1, i2 - i);
        } else {
            double[] dArr2 = new double[((i2 * 3) / 2) + 1];
            System.arraycopy(dArr, 0, dArr2, 0, i);
            System.arraycopy(this.zzgk, i, dArr2, i + 1, this.size - i);
            this.zzgk = dArr2;
        }
        this.zzgk[i] = d;
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
        zzc(i, ((Double) obj).doubleValue());
    }

    @Override // com.google.android.gms.internal.clearcut.zzav, java.util.AbstractCollection, java.util.Collection, java.util.List
    public final boolean addAll(Collection<? extends Double> collection) {
        zzw();
        zzci.checkNotNull(collection);
        if (!(collection instanceof zzbq)) {
            return super.addAll(collection);
        }
        zzbq zzbqVar = (zzbq) collection;
        int i = zzbqVar.size;
        if (i == 0) {
            return false;
        }
        int i2 = this.size;
        if (Integer.MAX_VALUE - i2 < i) {
            throw new OutOfMemoryError();
        }
        int i3 = i2 + i;
        double[] dArr = this.zzgk;
        if (i3 > dArr.length) {
            this.zzgk = Arrays.copyOf(dArr, i3);
        }
        System.arraycopy(zzbqVar.zzgk, 0, this.zzgk, this.size, zzbqVar.size);
        this.size = i3;
        ((AbstractList) this).modCount++;
        return true;
    }

    @Override // com.google.android.gms.internal.clearcut.zzav, java.util.AbstractList, java.util.Collection, java.util.List
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzbq)) {
            return super.equals(obj);
        }
        zzbq zzbqVar = (zzbq) obj;
        if (this.size != zzbqVar.size) {
            return false;
        }
        double[] dArr = zzbqVar.zzgk;
        for (int i = 0; i < this.size; i++) {
            if (this.zzgk[i] != dArr[i]) {
                return false;
            }
        }
        return true;
    }

    @Override // java.util.AbstractList, java.util.List
    public final /* synthetic */ Object get(int i) {
        zzg(i);
        return Double.valueOf(this.zzgk[i]);
    }

    @Override // com.google.android.gms.internal.clearcut.zzav, java.util.AbstractList, java.util.Collection, java.util.List
    public final int hashCode() {
        int i = 1;
        for (int i2 = 0; i2 < this.size; i2++) {
            i = (i * 31) + zzci.zzl(Double.doubleToLongBits(this.zzgk[i2]));
        }
        return i;
    }

    @Override // java.util.AbstractList, java.util.List
    public final /* synthetic */ Object remove(int i) {
        zzw();
        zzg(i);
        double[] dArr = this.zzgk;
        double d = dArr[i];
        int i2 = this.size;
        if (i < i2 - 1) {
            System.arraycopy(dArr, i + 1, dArr, i, i2 - i);
        }
        this.size--;
        ((AbstractList) this).modCount++;
        return Double.valueOf(d);
    }

    @Override // com.google.android.gms.internal.clearcut.zzav, java.util.AbstractCollection, java.util.Collection, java.util.List
    public final boolean remove(Object obj) {
        zzw();
        for (int i = 0; i < this.size; i++) {
            if (obj.equals(Double.valueOf(this.zzgk[i]))) {
                double[] dArr = this.zzgk;
                System.arraycopy(dArr, i + 1, dArr, i, this.size - i);
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
            double[] dArr = this.zzgk;
            System.arraycopy(dArr, i2, dArr, i, this.size - i2);
            this.size -= i2 - i;
            ((AbstractList) this).modCount++;
            return;
        }
        throw new IndexOutOfBoundsException("toIndex < fromIndex");
    }

    @Override // java.util.AbstractList, java.util.List
    public final /* synthetic */ Object set(int i, Object obj) {
        double doubleValue = ((Double) obj).doubleValue();
        zzw();
        zzg(i);
        double[] dArr = this.zzgk;
        double d = dArr[i];
        dArr[i] = doubleValue;
        return Double.valueOf(d);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final int size() {
        return this.size;
    }

    public final void zzc(double d) {
        zzc(this.size, d);
    }

    @Override // com.google.android.gms.internal.clearcut.zzcn
    public final /* synthetic */ zzcn<Double> zzi(int i) {
        if (i >= this.size) {
            return new zzbq(Arrays.copyOf(this.zzgk, i), this.size);
        }
        throw new IllegalArgumentException();
    }
}
