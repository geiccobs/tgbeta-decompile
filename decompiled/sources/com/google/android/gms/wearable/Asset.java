package com.google.android.gms.wearable;

import android.net.Uri;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.util.Arrays;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public class Asset extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Parcelable.Creator<Asset> CREATOR = new zzc();
    public ParcelFileDescriptor zza;
    public Uri zzb;
    private byte[] zzc;
    private String zzd;

    public Asset(byte[] bArr, String str, ParcelFileDescriptor parcelFileDescriptor, Uri uri) {
        this.zzc = bArr;
        this.zzd = str;
        this.zza = parcelFileDescriptor;
        this.zzb = uri;
    }

    public static Asset createFromBytes(byte[] assetData) {
        Preconditions.checkNotNull(assetData);
        return new Asset(assetData, null, null, null);
    }

    public static Asset createFromFd(ParcelFileDescriptor fd) {
        Preconditions.checkNotNull(fd);
        return new Asset(null, null, fd, null);
    }

    public static Asset createFromRef(String digest) {
        Preconditions.checkNotNull(digest);
        return new Asset(null, digest, null, null);
    }

    public static Asset createFromUri(Uri uri) {
        Preconditions.checkNotNull(uri);
        return new Asset(null, null, null, uri);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Asset)) {
            return false;
        }
        Asset asset = (Asset) o;
        return Arrays.equals(this.zzc, asset.zzc) && Objects.equal(this.zzd, asset.zzd) && Objects.equal(this.zza, asset.zza) && Objects.equal(this.zzb, asset.zzb);
    }

    public String getDigest() {
        return this.zzd;
    }

    public ParcelFileDescriptor getFd() {
        return this.zza;
    }

    public Uri getUri() {
        return this.zzb;
    }

    public int hashCode() {
        return Arrays.deepHashCode(new Object[]{this.zzc, this.zzd, this.zza, this.zzb});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Asset[@");
        sb.append(Integer.toHexString(hashCode()));
        if (this.zzd == null) {
            sb.append(", nodigest");
        } else {
            sb.append(", ");
            sb.append(this.zzd);
        }
        if (this.zzc != null) {
            sb.append(", size=");
            sb.append(((byte[]) Preconditions.checkNotNull(this.zzc)).length);
        }
        if (this.zza != null) {
            sb.append(", fd=");
            sb.append(this.zza);
        }
        if (this.zzb != null) {
            sb.append(", uri=");
            sb.append(this.zzb);
        }
        sb.append("]");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        Preconditions.checkNotNull(dest);
        int i = flags | 1;
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(dest);
        SafeParcelWriter.writeByteArray(dest, 2, this.zzc, false);
        SafeParcelWriter.writeString(dest, 3, getDigest(), false);
        SafeParcelWriter.writeParcelable(dest, 4, this.zza, i, false);
        SafeParcelWriter.writeParcelable(dest, 5, this.zzb, i, false);
        SafeParcelWriter.finishObjectHeader(dest, beginObjectHeader);
    }

    public final byte[] zza() {
        return this.zzc;
    }
}
