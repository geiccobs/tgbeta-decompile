package com.google.android.gms.wearable;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public class MessageOptions extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Parcelable.Creator<MessageOptions> CREATOR = new zzf();
    public static final int MESSAGE_PRIORITY_HIGH = 1;
    public static final int MESSAGE_PRIORITY_LOW = 0;
    private final int zza;

    public MessageOptions(int priority) {
        this.zza = priority;
    }

    public boolean equals(Object that) {
        return (that instanceof MessageOptions) && this.zza == ((MessageOptions) that).zza;
    }

    public int getPriority() {
        return this.zza;
    }

    public int hashCode() {
        return Objects.hashCode(Integer.valueOf(this.zza));
    }

    public String toString() {
        return "MessageOptions[ priority=" + this.zza + "]";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(dest);
        SafeParcelWriter.writeInt(dest, 2, getPriority());
        SafeParcelWriter.finishObjectHeader(dest, beginObjectHeader);
    }
}
