package com.google.android.gms.common.data;

import com.google.android.gms.common.internal.Preconditions;
import java.util.ArrayList;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public abstract class EntityBuffer<T> extends AbstractDataBuffer<T> {
    private boolean zaa = false;
    private ArrayList<Integer> zab;

    public EntityBuffer(DataHolder dataHolder) {
        super(dataHolder);
    }

    protected abstract T getEntry(int i, int i2);

    protected abstract String getPrimaryDataMarkerColumn();

    /* JADX WARN: Code restructure failed: missing block: B:14:0x0071, code lost:
        if (r6.mDataHolder.getString(r4, r7, r3) == null) goto L16;
     */
    @Override // com.google.android.gms.common.data.AbstractDataBuffer, com.google.android.gms.common.data.DataBuffer
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final T get(int r7) {
        /*
            r6 = this;
            r6.zaa()
            int r0 = r6.zaa(r7)
            r1 = 0
            if (r7 < 0) goto L76
            java.util.ArrayList<java.lang.Integer> r2 = r6.zab
            int r2 = r2.size()
            if (r7 != r2) goto L13
            goto L76
        L13:
            java.util.ArrayList<java.lang.Integer> r2 = r6.zab
            int r2 = r2.size()
            r3 = 1
            int r2 = r2 - r3
            if (r7 != r2) goto L38
            com.google.android.gms.common.data.DataHolder r2 = r6.mDataHolder
            java.lang.Object r2 = com.google.android.gms.common.internal.Preconditions.checkNotNull(r2)
            com.google.android.gms.common.data.DataHolder r2 = (com.google.android.gms.common.data.DataHolder) r2
            int r2 = r2.getCount()
            java.util.ArrayList<java.lang.Integer> r4 = r6.zab
            java.lang.Object r4 = r4.get(r7)
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r4 = r4.intValue()
            int r2 = r2 - r4
            goto L53
        L38:
            java.util.ArrayList<java.lang.Integer> r2 = r6.zab
            int r4 = r7 + 1
            java.lang.Object r2 = r2.get(r4)
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            java.util.ArrayList<java.lang.Integer> r4 = r6.zab
            java.lang.Object r4 = r4.get(r7)
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r4 = r4.intValue()
            int r2 = r2 - r4
        L53:
            if (r2 != r3) goto L74
            int r7 = r6.zaa(r7)
            com.google.android.gms.common.data.DataHolder r3 = r6.mDataHolder
            java.lang.Object r3 = com.google.android.gms.common.internal.Preconditions.checkNotNull(r3)
            com.google.android.gms.common.data.DataHolder r3 = (com.google.android.gms.common.data.DataHolder) r3
            int r3 = r3.getWindowIndex(r7)
            java.lang.String r4 = r6.getChildDataMarkerColumn()
            if (r4 == 0) goto L74
            com.google.android.gms.common.data.DataHolder r5 = r6.mDataHolder
            java.lang.String r7 = r5.getString(r4, r7, r3)
            if (r7 != 0) goto L74
            goto L77
        L74:
            r1 = r2
            goto L77
        L76:
        L77:
            java.lang.Object r7 = r6.getEntry(r0, r1)
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.common.data.EntityBuffer.get(int):java.lang.Object");
    }

    @Override // com.google.android.gms.common.data.AbstractDataBuffer, com.google.android.gms.common.data.DataBuffer
    public int getCount() {
        zaa();
        return this.zab.size();
    }

    private final void zaa() {
        synchronized (this) {
            if (!this.zaa) {
                int count = ((DataHolder) Preconditions.checkNotNull(this.mDataHolder)).getCount();
                ArrayList<Integer> arrayList = new ArrayList<>();
                this.zab = arrayList;
                if (count > 0) {
                    arrayList.add(0);
                    String primaryDataMarkerColumn = getPrimaryDataMarkerColumn();
                    String string = this.mDataHolder.getString(primaryDataMarkerColumn, 0, this.mDataHolder.getWindowIndex(0));
                    for (int i = 1; i < count; i++) {
                        int windowIndex = this.mDataHolder.getWindowIndex(i);
                        String string2 = this.mDataHolder.getString(primaryDataMarkerColumn, i, windowIndex);
                        if (string2 == null) {
                            StringBuilder sb = new StringBuilder(String.valueOf(primaryDataMarkerColumn).length() + 78);
                            sb.append("Missing value for markerColumn: ");
                            sb.append(primaryDataMarkerColumn);
                            sb.append(", at row: ");
                            sb.append(i);
                            sb.append(", for window: ");
                            sb.append(windowIndex);
                            throw new NullPointerException(sb.toString());
                        }
                        if (!string2.equals(string)) {
                            this.zab.add(Integer.valueOf(i));
                            string = string2;
                        }
                    }
                }
                this.zaa = true;
            }
        }
    }

    private final int zaa(int i) {
        if (i < 0 || i >= this.zab.size()) {
            StringBuilder sb = new StringBuilder(53);
            sb.append("Position ");
            sb.append(i);
            sb.append(" is out of bounds for this buffer");
            throw new IllegalArgumentException(sb.toString());
        }
        return this.zab.get(i).intValue();
    }

    protected String getChildDataMarkerColumn() {
        return null;
    }
}
