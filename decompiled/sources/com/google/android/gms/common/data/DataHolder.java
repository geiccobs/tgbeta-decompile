package com.google.android.gms.common.data;

import android.content.ContentValues;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.CursorWindow;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.android.gms.common.internal.Asserts;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.sqlite.CursorWrapper;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public final class DataHolder extends AbstractSafeParcelable implements Closeable {
    public static final Parcelable.Creator<DataHolder> CREATOR = new zac();
    private static final Builder zak = new zab(new String[0], null);
    private final int zaa;
    private final String[] zab;
    private Bundle zac;
    private final CursorWindow[] zad;
    private final int zae;
    private final Bundle zaf;
    private int[] zag;
    private int zah;
    private boolean zai;
    private boolean zaj;

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes3.dex */
    public static class zaa extends RuntimeException {
        public zaa(String str) {
            super(str);
        }
    }

    public DataHolder(int i, String[] strArr, CursorWindow[] cursorWindowArr, int i2, Bundle bundle) {
        this.zai = false;
        this.zaj = true;
        this.zaa = i;
        this.zab = strArr;
        this.zad = cursorWindowArr;
        this.zae = i2;
        this.zaf = bundle;
    }

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes3.dex */
    public static class Builder {
        private final String[] zaa;
        private final ArrayList<HashMap<String, Object>> zab;
        private final String zac;
        private final HashMap<Object, Integer> zad;
        private boolean zae;
        private String zaf;

        private Builder(String[] strArr, String str) {
            this.zaa = (String[]) Preconditions.checkNotNull(strArr);
            this.zab = new ArrayList<>();
            this.zac = null;
            this.zad = new HashMap<>();
            this.zae = false;
            this.zaf = null;
        }

        public Builder zaa(HashMap<String, Object> hashMap) {
            int i;
            Asserts.checkNotNull(hashMap);
            String str = this.zac;
            if (str == null) {
                i = -1;
            } else {
                Object obj = hashMap.get(str);
                if (obj == null) {
                    i = -1;
                } else {
                    Integer num = this.zad.get(obj);
                    if (num == null) {
                        this.zad.put(obj, Integer.valueOf(this.zab.size()));
                        i = -1;
                    } else {
                        i = num.intValue();
                    }
                }
            }
            if (i == -1) {
                this.zab.add(hashMap);
            } else {
                this.zab.remove(i);
                this.zab.add(i, hashMap);
            }
            this.zae = false;
            return this;
        }

        public Builder withRow(ContentValues contentValues) {
            Asserts.checkNotNull(contentValues);
            HashMap<String, Object> hashMap = new HashMap<>(contentValues.size());
            for (Map.Entry<String, Object> entry : contentValues.valueSet()) {
                hashMap.put(entry.getKey(), entry.getValue());
            }
            return zaa(hashMap);
        }

        public DataHolder build(int i) {
            return new DataHolder(this, i, (Bundle) null, (zab) null);
        }

        public DataHolder build(int i, Bundle bundle) {
            return new DataHolder(this, i, bundle, -1, (zab) null);
        }

        public /* synthetic */ Builder(String[] strArr, String str, zab zabVar) {
            this(strArr, null);
        }
    }

    public DataHolder(String[] strArr, CursorWindow[] cursorWindowArr, int i, Bundle bundle) {
        this.zai = false;
        this.zaj = true;
        this.zaa = 1;
        this.zab = (String[]) Preconditions.checkNotNull(strArr);
        this.zad = (CursorWindow[]) Preconditions.checkNotNull(cursorWindowArr);
        this.zae = i;
        this.zaf = bundle;
        zaa();
    }

    private DataHolder(CursorWrapper cursorWrapper, int i, Bundle bundle) {
        this(cursorWrapper.getColumnNames(), zaa(cursorWrapper), i, bundle);
    }

    public DataHolder(Cursor cursor, int i, Bundle bundle) {
        this(new CursorWrapper(cursor), i, bundle);
    }

    private DataHolder(Builder builder, int i, Bundle bundle) {
        this(builder.zaa, zaa(builder, -1), i, (Bundle) null);
    }

    private DataHolder(Builder builder, int i, Bundle bundle, int i2) {
        this(builder.zaa, zaa(builder, -1), i, bundle);
    }

    public final void zaa() {
        this.zac = new Bundle();
        int i = 0;
        int i2 = 0;
        while (true) {
            String[] strArr = this.zab;
            if (i2 >= strArr.length) {
                break;
            }
            this.zac.putInt(strArr[i2], i2);
            i2++;
        }
        this.zag = new int[this.zad.length];
        int i3 = 0;
        while (true) {
            CursorWindow[] cursorWindowArr = this.zad;
            if (i < cursorWindowArr.length) {
                this.zag[i] = i3;
                i3 += this.zad[i].getNumRows() - (i3 - cursorWindowArr[i].getStartPosition());
                i++;
            } else {
                this.zah = i3;
                return;
            }
        }
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeStringArray(parcel, 1, this.zab, false);
        SafeParcelWriter.writeTypedArray(parcel, 2, this.zad, i, false);
        SafeParcelWriter.writeInt(parcel, 3, getStatusCode());
        SafeParcelWriter.writeBundle(parcel, 4, getMetadata(), false);
        SafeParcelWriter.writeInt(parcel, 1000, this.zaa);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
        if ((i & 1) != 0) {
            close();
        }
    }

    public final int getStatusCode() {
        return this.zae;
    }

    public final Bundle getMetadata() {
        return this.zaf;
    }

    private static CursorWindow[] zaa(CursorWrapper cursorWrapper) {
        int i;
        ArrayList arrayList = new ArrayList();
        try {
            int count = cursorWrapper.getCount();
            CursorWindow window = cursorWrapper.getWindow();
            if (window != null && window.getStartPosition() == 0) {
                window.acquireReference();
                cursorWrapper.setWindow(null);
                arrayList.add(window);
                i = window.getNumRows();
            } else {
                i = 0;
            }
            while (i < count) {
                if (cursorWrapper.moveToPosition(i)) {
                    CursorWindow window2 = cursorWrapper.getWindow();
                    if (window2 != null) {
                        window2.acquireReference();
                        cursorWrapper.setWindow(null);
                    } else {
                        window2 = new CursorWindow(false);
                        window2.setStartPosition(i);
                        cursorWrapper.fillWindow(i, window2);
                    }
                    if (window2.getNumRows() == 0) {
                        break;
                    }
                    arrayList.add(window2);
                    i = window2.getStartPosition() + window2.getNumRows();
                } else {
                    break;
                }
            }
            cursorWrapper.close();
            return (CursorWindow[]) arrayList.toArray(new CursorWindow[arrayList.size()]);
        } catch (Throwable th) {
            cursorWrapper.close();
            throw th;
        }
    }

    private static CursorWindow[] zaa(Builder builder, int i) {
        if (builder.zaa.length != 0) {
            ArrayList arrayList = builder.zab;
            int size = arrayList.size();
            CursorWindow cursorWindow = new CursorWindow(false);
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(cursorWindow);
            cursorWindow.setNumColumns(builder.zaa.length);
            int i2 = 0;
            boolean z = false;
            while (i2 < size) {
                try {
                    if (!cursorWindow.allocRow()) {
                        StringBuilder sb = new StringBuilder(72);
                        sb.append("Allocating additional cursor window for large data set (row ");
                        sb.append(i2);
                        sb.append(")");
                        Log.d("DataHolder", sb.toString());
                        cursorWindow = new CursorWindow(false);
                        cursorWindow.setStartPosition(i2);
                        cursorWindow.setNumColumns(builder.zaa.length);
                        arrayList2.add(cursorWindow);
                        if (!cursorWindow.allocRow()) {
                            Log.e("DataHolder", "Unable to allocate row to hold data.");
                            arrayList2.remove(cursorWindow);
                            return (CursorWindow[]) arrayList2.toArray(new CursorWindow[arrayList2.size()]);
                        }
                    }
                    Map map = (Map) arrayList.get(i2);
                    boolean z2 = true;
                    for (int i3 = 0; i3 < builder.zaa.length && z2; i3++) {
                        String str = builder.zaa[i3];
                        Object obj = map.get(str);
                        if (obj == null) {
                            z2 = cursorWindow.putNull(i2, i3);
                        } else if (obj instanceof String) {
                            z2 = cursorWindow.putString((String) obj, i2, i3);
                        } else if (obj instanceof Long) {
                            z2 = cursorWindow.putLong(((Long) obj).longValue(), i2, i3);
                        } else if (obj instanceof Integer) {
                            z2 = cursorWindow.putLong(((Integer) obj).intValue(), i2, i3);
                        } else if (obj instanceof Boolean) {
                            z2 = cursorWindow.putLong(((Boolean) obj).booleanValue() ? 1L : 0L, i2, i3);
                        } else if (obj instanceof byte[]) {
                            z2 = cursorWindow.putBlob((byte[]) obj, i2, i3);
                        } else if (obj instanceof Double) {
                            z2 = cursorWindow.putDouble(((Double) obj).doubleValue(), i2, i3);
                        } else if (obj instanceof Float) {
                            z2 = cursorWindow.putDouble(((Float) obj).floatValue(), i2, i3);
                        } else {
                            String valueOf = String.valueOf(obj);
                            StringBuilder sb2 = new StringBuilder(String.valueOf(str).length() + 32 + String.valueOf(valueOf).length());
                            sb2.append("Unsupported object for column ");
                            sb2.append(str);
                            sb2.append(": ");
                            sb2.append(valueOf);
                            throw new IllegalArgumentException(sb2.toString());
                        }
                    }
                    if (!z2) {
                        if (z) {
                            throw new zaa("Could not add the value to a new CursorWindow. The size of value may be larger than what a CursorWindow can handle.");
                        }
                        StringBuilder sb3 = new StringBuilder(74);
                        sb3.append("Couldn't populate window data for row ");
                        sb3.append(i2);
                        sb3.append(" - allocating new window.");
                        Log.d("DataHolder", sb3.toString());
                        cursorWindow.freeLastRow();
                        cursorWindow = new CursorWindow(false);
                        cursorWindow.setStartPosition(i2);
                        cursorWindow.setNumColumns(builder.zaa.length);
                        arrayList2.add(cursorWindow);
                        i2--;
                        z = true;
                    } else {
                        z = false;
                    }
                    i2++;
                } catch (RuntimeException e) {
                    int size2 = arrayList2.size();
                    for (int i4 = 0; i4 < size2; i4++) {
                        ((CursorWindow) arrayList2.get(i4)).close();
                    }
                    throw e;
                }
            }
            return (CursorWindow[]) arrayList2.toArray(new CursorWindow[arrayList2.size()]);
        }
        return new CursorWindow[0];
    }

    private final void zaa(String str, int i) {
        Bundle bundle = this.zac;
        if (bundle == null || !bundle.containsKey(str)) {
            String valueOf = String.valueOf(str);
            throw new IllegalArgumentException(valueOf.length() != 0 ? "No such column: ".concat(valueOf) : new String("No such column: "));
        } else if (isClosed()) {
            throw new IllegalArgumentException("Buffer is closed.");
        } else {
            if (i < 0 || i >= this.zah) {
                throw new CursorIndexOutOfBoundsException(i, this.zah);
            }
        }
    }

    public final boolean hasColumn(String str) {
        return this.zac.containsKey(str);
    }

    public final long getLong(String str, int i, int i2) {
        zaa(str, i);
        return this.zad[i2].getLong(i, this.zac.getInt(str));
    }

    public final int getInteger(String str, int i, int i2) {
        zaa(str, i);
        return this.zad[i2].getInt(i, this.zac.getInt(str));
    }

    public final String getString(String str, int i, int i2) {
        zaa(str, i);
        return this.zad[i2].getString(i, this.zac.getInt(str));
    }

    public final boolean getBoolean(String str, int i, int i2) {
        zaa(str, i);
        return Long.valueOf(this.zad[i2].getLong(i, this.zac.getInt(str))).longValue() == 1;
    }

    public final float zaa(String str, int i, int i2) {
        zaa(str, i);
        return this.zad[i2].getFloat(i, this.zac.getInt(str));
    }

    public final double zab(String str, int i, int i2) {
        zaa(str, i);
        return this.zad[i2].getDouble(i, this.zac.getInt(str));
    }

    public final byte[] getByteArray(String str, int i, int i2) {
        zaa(str, i);
        return this.zad[i2].getBlob(i, this.zac.getInt(str));
    }

    public final void zaa(String str, int i, int i2, CharArrayBuffer charArrayBuffer) {
        zaa(str, i);
        this.zad[i2].copyStringToBuffer(i, this.zac.getInt(str), charArrayBuffer);
    }

    public final boolean hasNull(String str, int i, int i2) {
        zaa(str, i);
        return this.zad[i2].isNull(i, this.zac.getInt(str));
    }

    public final int getCount() {
        return this.zah;
    }

    public final int getWindowIndex(int i) {
        int[] iArr;
        int i2 = 0;
        Preconditions.checkState(i >= 0 && i < this.zah);
        while (true) {
            iArr = this.zag;
            if (i2 >= iArr.length) {
                break;
            } else if (i < iArr[i2]) {
                i2--;
                break;
            } else {
                i2++;
            }
        }
        if (i2 == iArr.length) {
            return i2 - 1;
        }
        return i2;
    }

    public final boolean isClosed() {
        boolean z;
        synchronized (this) {
            z = this.zai;
        }
        return z;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public final void close() {
        synchronized (this) {
            if (!this.zai) {
                this.zai = true;
                int i = 0;
                while (true) {
                    CursorWindow[] cursorWindowArr = this.zad;
                    if (i >= cursorWindowArr.length) {
                        break;
                    }
                    cursorWindowArr[i].close();
                    i++;
                }
            }
        }
    }

    protected final void finalize() throws Throwable {
        try {
            if (this.zaj && this.zad.length > 0 && !isClosed()) {
                close();
                String obj = toString();
                StringBuilder sb = new StringBuilder(String.valueOf(obj).length() + 178);
                sb.append("Internal data leak within a DataBuffer object detected!  Be sure to explicitly call release() on all DataBuffer extending objects when you are done with them. (internal object: ");
                sb.append(obj);
                sb.append(")");
                Log.e("DataBuffer", sb.toString());
            }
        } finally {
            super.finalize();
        }
    }

    public static Builder builder(String[] strArr) {
        return new Builder(strArr, null, null);
    }

    public static DataHolder empty(int i) {
        return new DataHolder(zak, i, (Bundle) null);
    }

    /* synthetic */ DataHolder(Builder builder, int i, Bundle bundle, zab zabVar) {
        this(builder, i, (Bundle) null);
    }

    /* synthetic */ DataHolder(Builder builder, int i, Bundle bundle, int i2, zab zabVar) {
        this(builder, i, bundle, -1);
    }
}
