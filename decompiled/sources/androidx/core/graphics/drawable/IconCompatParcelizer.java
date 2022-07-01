package androidx.core.graphics.drawable;

import android.content.res.ColorStateList;
import android.os.Parcelable;
import androidx.versionedparcelable.VersionedParcel;
/* loaded from: classes.dex */
public class IconCompatParcelizer {
    public static IconCompat read(VersionedParcel parcel) {
        IconCompat iconCompat = new IconCompat();
        iconCompat.mType = parcel.readInt(iconCompat.mType, 1);
        iconCompat.mData = parcel.readByteArray(iconCompat.mData, 2);
        iconCompat.mParcelable = parcel.readParcelable(iconCompat.mParcelable, 3);
        iconCompat.mInt1 = parcel.readInt(iconCompat.mInt1, 4);
        iconCompat.mInt2 = parcel.readInt(iconCompat.mInt2, 5);
        iconCompat.mTintList = (ColorStateList) parcel.readParcelable(iconCompat.mTintList, 6);
        iconCompat.mTintModeStr = parcel.readString(iconCompat.mTintModeStr, 7);
        iconCompat.mString1 = parcel.readString(iconCompat.mString1, 8);
        iconCompat.onPostParceling();
        return iconCompat;
    }

    public static void write(IconCompat obj, VersionedParcel parcel) {
        parcel.setSerializationFlags(true, true);
        obj.onPreParceling(parcel.isStream());
        int i = obj.mType;
        if (-1 != i) {
            parcel.writeInt(i, 1);
        }
        byte[] bArr = obj.mData;
        if (bArr != null) {
            parcel.writeByteArray(bArr, 2);
        }
        Parcelable parcelable = obj.mParcelable;
        if (parcelable != null) {
            parcel.writeParcelable(parcelable, 3);
        }
        int i2 = obj.mInt1;
        if (i2 != 0) {
            parcel.writeInt(i2, 4);
        }
        int i3 = obj.mInt2;
        if (i3 != 0) {
            parcel.writeInt(i3, 5);
        }
        ColorStateList colorStateList = obj.mTintList;
        if (colorStateList != null) {
            parcel.writeParcelable(colorStateList, 6);
        }
        String str = obj.mTintModeStr;
        if (str != null) {
            parcel.writeString(str, 7);
        }
        String str2 = obj.mString1;
        if (str2 != null) {
            parcel.writeString(str2, 8);
        }
    }
}
