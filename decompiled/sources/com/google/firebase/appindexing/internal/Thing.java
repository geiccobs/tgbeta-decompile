package com.google.firebase.appindexing.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.internal.icing.zzbp;
import com.google.firebase.appindexing.Indexable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class Thing extends AbstractSafeParcelable implements Indexable, ReflectedParcelable {
    public static final Parcelable.Creator<Thing> CREATOR = new zzad();
    public final int zza;
    private final Bundle zzb;
    private final zzac zzc;
    private final String zzd;
    private final String zze;

    public Thing(int i, Bundle bundle, zzac zzacVar, String str, String str2) {
        this.zza = i;
        this.zzb = bundle;
        this.zzc = zzacVar;
        this.zzd = str;
        this.zze = str2;
        ClassLoader classLoader = getClass().getClassLoader();
        zzbp.zza(classLoader);
        bundle.setClassLoader(classLoader);
    }

    public static void zzd(Bundle bundle, StringBuilder sb) {
        try {
            Set<String> keySet = bundle.keySet();
            String[] strArr = (String[]) keySet.toArray(new String[keySet.size()]);
            Arrays.sort(strArr, zzab.zza);
            for (String str : strArr) {
                sb.append("{ key: '");
                sb.append(str);
                sb.append("' value: ");
                Object obj = bundle.get(str);
                if (obj == null) {
                    sb.append("<null>");
                } else if (obj.getClass().isArray()) {
                    sb.append("[ ");
                    for (int i = 0; i < Array.getLength(obj); i++) {
                        sb.append("'");
                        sb.append(Array.get(obj, i));
                        sb.append("' ");
                    }
                    sb.append("]");
                } else {
                    sb.append(obj.toString());
                }
                sb.append(" } ");
            }
        } catch (RuntimeException e) {
            sb.append("<error>");
        }
    }

    public static boolean zze(Bundle bundle, Bundle bundle2) {
        if (bundle.size() != bundle2.size()) {
            return false;
        }
        for (String str : bundle.keySet()) {
            Object obj = bundle.get(str);
            Object obj2 = bundle2.get(str);
            if ((obj instanceof Bundle) && (obj2 instanceof Bundle) && !zze((Bundle) obj, (Bundle) obj2)) {
                return false;
            }
            if (obj == null) {
                if (obj2 != null || !bundle2.containsKey(str)) {
                    return false;
                }
                obj2 = null;
            }
            if (obj instanceof boolean[]) {
                if (!(obj2 instanceof boolean[]) || !Arrays.equals((boolean[]) obj, (boolean[]) obj2)) {
                    return false;
                }
            } else if (obj instanceof long[]) {
                if (!(obj2 instanceof long[]) || !Arrays.equals((long[]) obj, (long[]) obj2)) {
                    return false;
                }
            } else if (obj instanceof double[]) {
                if (!(obj2 instanceof double[]) || !Arrays.equals((double[]) obj, (double[]) obj2)) {
                    return false;
                }
            } else if (obj instanceof byte[]) {
                if (!(obj2 instanceof byte[]) || !Arrays.equals((byte[]) obj, (byte[]) obj2)) {
                    return false;
                }
            } else if ((obj instanceof Object[]) && (!(obj2 instanceof Object[]) || !Arrays.equals((Object[]) obj, (Object[]) obj2))) {
                return false;
            }
        }
        return true;
    }

    public static int zzf(Bundle bundle) {
        ArrayList arrayList = new ArrayList(bundle.keySet());
        Collections.sort(arrayList);
        ArrayList arrayList2 = new ArrayList();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            Object obj = bundle.get((String) arrayList.get(i));
            if (obj instanceof boolean[]) {
                arrayList2.add(Integer.valueOf(Arrays.hashCode((boolean[]) obj)));
            } else if (obj instanceof long[]) {
                arrayList2.add(Integer.valueOf(Arrays.hashCode((long[]) obj)));
            } else if (obj instanceof double[]) {
                arrayList2.add(Integer.valueOf(Arrays.hashCode((double[]) obj)));
            } else if (obj instanceof byte[]) {
                arrayList2.add(Integer.valueOf(Arrays.hashCode((byte[]) obj)));
            } else if (obj instanceof Object[]) {
                arrayList2.add(Integer.valueOf(Arrays.hashCode((Object[]) obj)));
            } else {
                arrayList2.add(Integer.valueOf(Objects.hashCode(obj)));
            }
        }
        return Objects.hashCode(arrayList2.toArray());
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Thing)) {
            return false;
        }
        Thing thing = (Thing) obj;
        return Objects.equal(Integer.valueOf(this.zza), Integer.valueOf(thing.zza)) && Objects.equal(this.zzd, thing.zzd) && Objects.equal(this.zze, thing.zze) && Objects.equal(this.zzc, thing.zzc) && zze(this.zzb, thing.zzb);
    }

    public final int hashCode() {
        return Objects.hashCode(Integer.valueOf(this.zza), this.zzd, this.zze, Integer.valueOf(this.zzc.hashCode()), Integer.valueOf(zzf(this.zzb)));
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.zze.equals("Thing") ? "Indexable" : this.zze);
        sb.append(" { { id: ");
        if (this.zzd == null) {
            sb.append("<null>");
        } else {
            sb.append("'");
            sb.append(this.zzd);
            sb.append("'");
        }
        sb.append(" } Properties { ");
        zzd(this.zzb, sb);
        sb.append("} Metadata { ");
        sb.append(this.zzc.toString());
        sb.append(" } }");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeBundle(parcel, 1, this.zzb, false);
        SafeParcelWriter.writeParcelable(parcel, 2, this.zzc, i, false);
        SafeParcelWriter.writeString(parcel, 3, this.zzd, false);
        SafeParcelWriter.writeString(parcel, 4, this.zze, false);
        SafeParcelWriter.writeInt(parcel, 1000, this.zza);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }

    public Thing(Bundle bundle, zzac zzacVar, String str, String str2) {
        this.zza = 10;
        this.zzb = bundle;
        this.zzc = zzacVar;
        this.zzd = str;
        this.zze = str2;
    }
}
