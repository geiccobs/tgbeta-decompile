package com.google.android.gms.maps.model;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.util.IOUtils;
import java.io.IOException;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public final class MapStyleOptions extends AbstractSafeParcelable {
    private String zzb;
    private static final String zza = MapStyleOptions.class.getSimpleName();
    public static final Parcelable.Creator<MapStyleOptions> CREATOR = new zzh();

    public MapStyleOptions(String json) {
        Preconditions.checkNotNull(json, "json must not be null");
        this.zzb = json;
    }

    public static MapStyleOptions loadRawResourceStyle(Context clientContext, int resourceId) throws Resources.NotFoundException {
        try {
            return new MapStyleOptions(new String(IOUtils.readInputStreamFully(clientContext.getResources().openRawResource(resourceId)), "UTF-8"));
        } catch (IOException e) {
            String valueOf = String.valueOf(e);
            StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 37);
            sb.append("Failed to read resource ");
            sb.append(resourceId);
            sb.append(": ");
            sb.append(valueOf);
            throw new Resources.NotFoundException(sb.toString());
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(out);
        SafeParcelWriter.writeString(out, 2, this.zzb, false);
        SafeParcelWriter.finishObjectHeader(out, beginObjectHeader);
    }
}
