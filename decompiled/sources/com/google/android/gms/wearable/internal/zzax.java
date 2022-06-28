package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.wearable.ChannelApi;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzax extends AbstractSafeParcelable {
    public static final Parcelable.Creator<zzax> CREATOR = new zzay();
    final zzbi zza;
    final int zzb;
    final int zzc;
    final int zzd;

    public zzax(zzbi zzbiVar, int i, int i2, int i3) {
        this.zza = zzbiVar;
        this.zzb = i;
        this.zzc = i2;
        this.zzd = i3;
    }

    public final String toString() {
        String str;
        String str2;
        String valueOf = String.valueOf(this.zza);
        int i = this.zzb;
        switch (i) {
            case 1:
                str = "CHANNEL_OPENED";
                break;
            case 2:
                str = "CHANNEL_CLOSED";
                break;
            case 3:
                str = "INPUT_CLOSED";
                break;
            case 4:
                str = "OUTPUT_CLOSED";
                break;
            default:
                str = Integer.toString(i);
                break;
        }
        int i2 = this.zzc;
        switch (i2) {
            case 0:
                str2 = "CLOSE_REASON_NORMAL";
                break;
            case 1:
                str2 = "CLOSE_REASON_DISCONNECTED";
                break;
            case 2:
                str2 = "CLOSE_REASON_REMOTE_CLOSE";
                break;
            case 3:
                str2 = "CLOSE_REASON_LOCAL_CLOSE";
                break;
            default:
                str2 = Integer.toString(i2);
                break;
        }
        int i3 = this.zzd;
        int length = String.valueOf(valueOf).length();
        StringBuilder sb = new StringBuilder(length + 81 + String.valueOf(str).length() + String.valueOf(str2).length());
        sb.append("ChannelEventParcelable[, channel=");
        sb.append(valueOf);
        sb.append(", type=");
        sb.append(str);
        sb.append(", closeReason=");
        sb.append(str2);
        sb.append(", appErrorCode=");
        sb.append(i3);
        sb.append("]");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeParcelable(parcel, 2, this.zza, i, false);
        SafeParcelWriter.writeInt(parcel, 3, this.zzb);
        SafeParcelWriter.writeInt(parcel, 4, this.zzc);
        SafeParcelWriter.writeInt(parcel, 5, this.zzd);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }

    public final void zza(ChannelApi.ChannelListener channelListener) {
        int i = this.zzb;
        switch (i) {
            case 1:
                channelListener.onChannelOpened(this.zza);
                return;
            case 2:
                channelListener.onChannelClosed(this.zza, this.zzc, this.zzd);
                return;
            case 3:
                channelListener.onInputClosed(this.zza, this.zzc, this.zzd);
                return;
            case 4:
                channelListener.onOutputClosed(this.zza, this.zzc, this.zzd);
                return;
            default:
                StringBuilder sb = new StringBuilder(25);
                sb.append("Unknown type: ");
                sb.append(i);
                Log.w("ChannelEventParcelable", sb.toString());
                return;
        }
    }
}
