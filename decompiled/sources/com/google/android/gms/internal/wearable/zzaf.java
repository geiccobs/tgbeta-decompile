package com.google.android.gms.internal.wearable;

import com.google.android.gms.internal.wearable.zzae;
import com.google.android.gms.internal.wearable.zzaf;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public abstract class zzaf<MessageType extends zzaf<MessageType, BuilderType>, BuilderType extends zzae<MessageType, BuilderType>> implements zzcx {
    protected int zza = 0;

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> void zzL(Iterable<T> iterable, List<? super T> list) {
        zzca.zza(iterable);
        if (iterable instanceof zzch) {
            List<?> zzh = ((zzch) iterable).zzh();
            zzch zzchVar = (zzch) list;
            int size = list.size();
            for (Object obj : zzh) {
                if (obj != null) {
                    if (obj instanceof zzau) {
                        zzchVar.zzf((zzau) obj);
                    } else {
                        zzchVar.add((String) obj);
                    }
                } else {
                    int size2 = zzchVar.size();
                    StringBuilder sb = new StringBuilder(37);
                    sb.append("Element at index ");
                    sb.append(size2 - size);
                    sb.append(" is null.");
                    String sb2 = sb.toString();
                    for (int size3 = zzchVar.size() - 1; size3 >= size; size3--) {
                        zzchVar.remove(size3);
                    }
                    throw new NullPointerException(sb2);
                }
            }
        } else if (!(iterable instanceof zzde)) {
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

    @Override // com.google.android.gms.internal.wearable.zzcx
    public final zzau zzH() {
        try {
            int zzP = zzP();
            zzau zzauVar = zzau.zzb;
            byte[] bArr = new byte[zzP];
            zzbb zzt = zzbb.zzt(bArr);
            zzO(zzt);
            zzt.zzC();
            return new zzas(bArr);
        } catch (IOException e) {
            String name = getClass().getName();
            StringBuilder sb = new StringBuilder(String.valueOf(name).length() + 72);
            sb.append("Serializing ");
            sb.append(name);
            sb.append(" to a ByteString threw an IOException (should never happen).");
            throw new RuntimeException(sb.toString(), e);
        }
    }

    public final byte[] zzI() {
        try {
            byte[] bArr = new byte[zzP()];
            zzbb zzt = zzbb.zzt(bArr);
            zzO(zzt);
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

    public int zzJ() {
        throw null;
    }

    public void zzK(int i) {
        throw null;
    }
}
