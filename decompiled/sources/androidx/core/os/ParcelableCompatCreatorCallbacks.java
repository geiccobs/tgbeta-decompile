package androidx.core.os;

import android.os.Parcel;
@Deprecated
/* loaded from: classes3.dex */
public interface ParcelableCompatCreatorCallbacks<T> {
    T createFromParcel(Parcel in, ClassLoader loader);

    T[] newArray(int size);
}
