package com.google.android.gms.common.api;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.IntentSender;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.RecentlyNonNull;
import androidx.annotation.RecentlyNullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes.dex */
public final class Status extends AbstractSafeParcelable implements Result, ReflectedParcelable {
    private final int zzb;
    private final int zzc;
    private final String zzd;
    private final PendingIntent zze;
    private final ConnectionResult zzf;
    @RecentlyNonNull
    public static final Status RESULT_SUCCESS = new Status(0);
    @RecentlyNonNull
    public static final Status RESULT_INTERRUPTED = new Status(14);
    @RecentlyNonNull
    public static final Status RESULT_INTERNAL_ERROR = new Status(8);
    @RecentlyNonNull
    public static final Status RESULT_TIMEOUT = new Status(15);
    @RecentlyNonNull
    public static final Status RESULT_CANCELED = new Status(16);
    @RecentlyNonNull
    public static final Parcelable.Creator<Status> CREATOR = new zzb();

    Status(int i, int i2, String str, PendingIntent pendingIntent) {
        this(i, i2, str, pendingIntent, null);
    }

    @Override // com.google.android.gms.common.api.Result
    @RecentlyNonNull
    public final Status getStatus() {
        return this;
    }

    public Status(int i, int i2, String str, PendingIntent pendingIntent, ConnectionResult connectionResult) {
        this.zzb = i;
        this.zzc = i2;
        this.zzd = str;
        this.zze = pendingIntent;
        this.zzf = connectionResult;
    }

    public Status(int i) {
        this(i, (String) null);
    }

    public Status(int i, String str) {
        this(1, i, str, null);
    }

    public Status(int i, String str, PendingIntent pendingIntent) {
        this(1, i, str, pendingIntent);
    }

    public Status(@RecentlyNonNull ConnectionResult connectionResult, @RecentlyNonNull String str) {
        this(connectionResult, str, 17);
    }

    @Deprecated
    public Status(@RecentlyNonNull ConnectionResult connectionResult, @RecentlyNonNull String str, int i) {
        this(1, i, str, connectionResult.getResolution(), connectionResult);
    }

    public final void startResolutionForResult(@RecentlyNonNull Activity activity, int i) throws IntentSender.SendIntentException {
        if (!hasResolution()) {
            return;
        }
        activity.startIntentSenderForResult(((PendingIntent) Preconditions.checkNotNull(this.zze)).getIntentSender(), i, null, 0, 0, 0);
    }

    @RecentlyNullable
    public final String getStatusMessage() {
        return this.zzd;
    }

    @RecentlyNullable
    public final ConnectionResult getConnectionResult() {
        return this.zzf;
    }

    public final boolean hasResolution() {
        return this.zze != null;
    }

    public final boolean isSuccess() {
        return this.zzc <= 0;
    }

    public final int getStatusCode() {
        return this.zzc;
    }

    public final int hashCode() {
        return Objects.hashCode(Integer.valueOf(this.zzb), Integer.valueOf(this.zzc), this.zzd, this.zze, this.zzf);
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof Status)) {
            return false;
        }
        Status status = (Status) obj;
        return this.zzb == status.zzb && this.zzc == status.zzc && Objects.equal(this.zzd, status.zzd) && Objects.equal(this.zze, status.zze) && Objects.equal(this.zzf, status.zzf);
    }

    @RecentlyNonNull
    public final String zza() {
        String str = this.zzd;
        return str != null ? str : CommonStatusCodes.getStatusCodeString(this.zzc);
    }

    @RecentlyNonNull
    public final String toString() {
        return Objects.toStringHelper(this).add("statusCode", zza()).add("resolution", this.zze).toString();
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(@RecentlyNonNull Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeInt(parcel, 1, getStatusCode());
        SafeParcelWriter.writeString(parcel, 2, getStatusMessage(), false);
        SafeParcelWriter.writeParcelable(parcel, 3, this.zze, i, false);
        SafeParcelWriter.writeParcelable(parcel, 4, getConnectionResult(), i, false);
        SafeParcelWriter.writeInt(parcel, 1000, this.zzb);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }

    static {
        new Status(17);
        new Status(18);
    }
}
