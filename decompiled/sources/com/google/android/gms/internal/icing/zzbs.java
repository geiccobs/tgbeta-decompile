package com.google.android.gms.internal.icing;

import com.google.android.gms.internal.icing.zzbr;
import com.google.android.gms.internal.icing.zzbs;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public abstract class zzbs<MessageType extends zzbs<MessageType, BuilderType>, BuilderType extends zzbr<MessageType, BuilderType>> implements zzee {
    protected int zza = 0;

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> void zzk(Iterable<T> iterable, List<? super T> list) {
        zzdh.zza(iterable);
        if (iterable instanceof zzdo) {
            List<?> zzh = ((zzdo) iterable).zzh();
            zzdo zzdoVar = (zzdo) list;
            int size = list.size();
            for (Object obj : zzh) {
                if (obj != null) {
                    if (obj instanceof zzcf) {
                        zzdoVar.zzf((zzcf) obj);
                    } else {
                        zzdoVar.add((String) obj);
                    }
                } else {
                    int size2 = zzdoVar.size();
                    StringBuilder sb = new StringBuilder(37);
                    sb.append("Element at index ");
                    sb.append(size2 - size);
                    sb.append(" is null.");
                    String sb2 = sb.toString();
                    for (int size3 = zzdoVar.size() - 1; size3 >= size; size3--) {
                        zzdoVar.remove(size3);
                    }
                    throw new NullPointerException(sb2);
                }
            }
        } else if (!(iterable instanceof zzel)) {
            if ((list instanceof ArrayList) && (iterable instanceof Collection)) {
                ((ArrayList) list).ensureCapacity(list.size() + iterable.size());
            }
            int size4 = list.size();
            for (T t : iterable) {
                if (t != 0) {
                    list.add(t);
                } else {
                    int size5 = list.size();
                    StringBuilder sb3 = new StringBuilder(37);
                    sb3.append("Element at index ");
                    sb3.append(size5 - size4);
                    sb3.append(" is null.");
                    String sb4 = sb3.toString();
                    for (int size6 = list.size() - 1; size6 >= size4; size6--) {
                        list.remove(size6);
                    }
                    throw new NullPointerException(sb4);
                }
            }
        } else {
            list.addAll(iterable);
        }
    }

    @Override // com.google.android.gms.internal.icing.zzee
    public final zzcf zzg() {
        try {
            int zzo = zzo();
            zzcf zzcfVar = zzcf.zzb;
            byte[] bArr = new byte[zzo];
            zzcm zzt = zzcm.zzt(bArr);
            zzn(zzt);
            zzt.zzC();
            return new zzcd(bArr);
        } catch (IOException e) {
            String name = getClass().getName();
            StringBuilder sb = new StringBuilder(String.valueOf(name).length() + 72);
            sb.append("Serializing ");
            sb.append(name);
            sb.append(" to a ByteString threw an IOException (should never happen).");
            throw new RuntimeException(sb.toString(), e);
        }
    }

    public final byte[] zzh() {
        try {
            byte[] bArr = new byte[zzo()];
            zzcm zzt = zzcm.zzt(bArr);
            zzn(zzt);
            zzt.zzC();
            return bArr;
        } catch (IOException e) {
            String name = getClass().getName();
            StringBuilder sb = new StringBuilder(String.valueOf(name).length() + 72);
            sb.append("Serializing ");
            sb.append(name);
            sb.append(" to a byte array threw an IOException (should never happen).");
            throw new RuntimeException(sb.toString(), e);
        }
    }

    public int zzi() {
        throw null;
    }

    public void zzj(int i) {
        throw null;
    }
}
