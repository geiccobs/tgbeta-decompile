package com.google.android.gms.common.internal.safeparcel;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.RecentlyNonNull;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes.dex */
public class SafeParcelWriter {
    private static void zza(Parcel parcel, int i, int i2) {
        if (i2 >= 65535) {
            parcel.writeInt(i | (-65536));
            parcel.writeInt(i2);
            return;
        }
        parcel.writeInt(i | (i2 << 16));
    }

    private static int zza(Parcel parcel, int i) {
        parcel.writeInt(i | (-65536));
        parcel.writeInt(0);
        return parcel.dataPosition();
    }

    private static void zzb(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.setDataPosition(i - 4);
        parcel.writeInt(dataPosition - i);
        parcel.setDataPosition(dataPosition);
    }

    public static int beginObjectHeader(@RecentlyNonNull Parcel parcel) {
        return zza(parcel, 20293);
    }

    public static void finishObjectHeader(@RecentlyNonNull Parcel parcel, int i) {
        zzb(parcel, i);
    }

    public static void writeBoolean(@RecentlyNonNull Parcel parcel, int i, boolean z) {
        zza(parcel, i, 4);
        parcel.writeInt(z ? 1 : 0);
    }

    public static void writeByte(@RecentlyNonNull Parcel parcel, int i, byte b) {
        zza(parcel, i, 4);
        parcel.writeInt(b);
    }

    public static void writeInt(@RecentlyNonNull Parcel parcel, int i, int i2) {
        zza(parcel, i, 4);
        parcel.writeInt(i2);
    }

    public static void writeLong(@RecentlyNonNull Parcel parcel, int i, long j) {
        zza(parcel, i, 8);
        parcel.writeLong(j);
    }

    public static void writeFloat(@RecentlyNonNull Parcel parcel, int i, float f) {
        zza(parcel, i, 4);
        parcel.writeFloat(f);
    }

    public static void writeFloatObject(@RecentlyNonNull Parcel parcel, int i, @RecentlyNonNull Float f, boolean z) {
        if (f != null) {
            zza(parcel, i, 4);
            parcel.writeFloat(f.floatValue());
        } else if (!z) {
        } else {
            zza(parcel, i, 0);
        }
    }

    public static void writeDouble(@RecentlyNonNull Parcel parcel, int i, double d) {
        zza(parcel, i, 8);
        parcel.writeDouble(d);
    }

    public static void writeString(@RecentlyNonNull Parcel parcel, int i, @RecentlyNonNull String str, boolean z) {
        if (str == null) {
            if (!z) {
                return;
            }
            zza(parcel, i, 0);
            return;
        }
        int zza = zza(parcel, i);
        parcel.writeString(str);
        zzb(parcel, zza);
    }

    public static void writeIBinder(@RecentlyNonNull Parcel parcel, int i, @RecentlyNonNull IBinder iBinder, boolean z) {
        if (iBinder == null) {
            if (!z) {
                return;
            }
            zza(parcel, i, 0);
            return;
        }
        int zza = zza(parcel, i);
        parcel.writeStrongBinder(iBinder);
        zzb(parcel, zza);
    }

    public static void writeParcelable(@RecentlyNonNull Parcel parcel, int i, @RecentlyNonNull Parcelable parcelable, int i2, boolean z) {
        if (parcelable == null) {
            if (!z) {
                return;
            }
            zza(parcel, i, 0);
            return;
        }
        int zza = zza(parcel, i);
        parcelable.writeToParcel(parcel, i2);
        zzb(parcel, zza);
    }

    public static void writeBundle(@RecentlyNonNull Parcel parcel, int i, @RecentlyNonNull Bundle bundle, boolean z) {
        if (bundle == null) {
            if (!z) {
                return;
            }
            zza(parcel, i, 0);
            return;
        }
        int zza = zza(parcel, i);
        parcel.writeBundle(bundle);
        zzb(parcel, zza);
    }

    public static void writeByteArray(@RecentlyNonNull Parcel parcel, int i, @RecentlyNonNull byte[] bArr, boolean z) {
        if (bArr == null) {
            if (!z) {
                return;
            }
            zza(parcel, i, 0);
            return;
        }
        int zza = zza(parcel, i);
        parcel.writeByteArray(bArr);
        zzb(parcel, zza);
    }

    public static void writeByteArrayArray(@RecentlyNonNull Parcel parcel, int i, @RecentlyNonNull byte[][] bArr, boolean z) {
        if (bArr == null) {
            if (!z) {
                return;
            }
            zza(parcel, i, 0);
            return;
        }
        int zza = zza(parcel, i);
        parcel.writeInt(bArr.length);
        for (byte[] bArr2 : bArr) {
            parcel.writeByteArray(bArr2);
        }
        zzb(parcel, zza);
    }

    public static void writeIntArray(@RecentlyNonNull Parcel parcel, int i, @RecentlyNonNull int[] iArr, boolean z) {
        if (iArr == null) {
            if (!z) {
                return;
            }
            zza(parcel, i, 0);
            return;
        }
        int zza = zza(parcel, i);
        parcel.writeIntArray(iArr);
        zzb(parcel, zza);
    }

    public static void writeStringArray(@RecentlyNonNull Parcel parcel, int i, @RecentlyNonNull String[] strArr, boolean z) {
        if (strArr == null) {
            if (!z) {
                return;
            }
            zza(parcel, i, 0);
            return;
        }
        int zza = zza(parcel, i);
        parcel.writeStringArray(strArr);
        zzb(parcel, zza);
    }

    public static void writeIntegerList(@RecentlyNonNull Parcel parcel, int i, @RecentlyNonNull List<Integer> list, boolean z) {
        if (list == null) {
            if (!z) {
                return;
            }
            zza(parcel, i, 0);
            return;
        }
        int zza = zza(parcel, i);
        int size = list.size();
        parcel.writeInt(size);
        for (int i2 = 0; i2 < size; i2++) {
            parcel.writeInt(list.get(i2).intValue());
        }
        zzb(parcel, zza);
    }

    public static void writeStringList(@RecentlyNonNull Parcel parcel, int i, @RecentlyNonNull List<String> list, boolean z) {
        if (list == null) {
            if (!z) {
                return;
            }
            zza(parcel, i, 0);
            return;
        }
        int zza = zza(parcel, i);
        parcel.writeStringList(list);
        zzb(parcel, zza);
    }

    public static <T extends Parcelable> void writeTypedArray(@RecentlyNonNull Parcel parcel, int i, @RecentlyNonNull T[] tArr, int i2, boolean z) {
        if (tArr == null) {
            if (!z) {
                return;
            }
            zza(parcel, i, 0);
            return;
        }
        int zza = zza(parcel, i);
        parcel.writeInt(tArr.length);
        for (T t : tArr) {
            if (t == null) {
                parcel.writeInt(0);
            } else {
                zza(parcel, t, i2);
            }
        }
        zzb(parcel, zza);
    }

    public static <T extends Parcelable> void writeTypedList(@RecentlyNonNull Parcel parcel, int i, @RecentlyNonNull List<T> list, boolean z) {
        if (list == null) {
            if (!z) {
                return;
            }
            zza(parcel, i, 0);
            return;
        }
        int zza = zza(parcel, i);
        int size = list.size();
        parcel.writeInt(size);
        for (int i2 = 0; i2 < size; i2++) {
            T t = list.get(i2);
            if (t == null) {
                parcel.writeInt(0);
            } else {
                zza(parcel, t, 0);
            }
        }
        zzb(parcel, zza);
    }

    private static <T extends Parcelable> void zza(Parcel parcel, T t, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(1);
        int dataPosition2 = parcel.dataPosition();
        t.writeToParcel(parcel, i);
        int dataPosition3 = parcel.dataPosition();
        parcel.setDataPosition(dataPosition);
        parcel.writeInt(dataPosition3 - dataPosition2);
        parcel.setDataPosition(dataPosition3);
    }
}
