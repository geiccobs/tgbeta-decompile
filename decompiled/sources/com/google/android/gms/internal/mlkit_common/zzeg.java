package com.google.android.gms.internal.mlkit_common;

import com.google.android.gms.internal.mlkit_common.zzeg;
import com.google.android.gms.internal.mlkit_common.zzej;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public abstract class zzeg<MessageType extends zzeg<MessageType, BuilderType>, BuilderType extends zzej<MessageType, BuilderType>> implements zzhb {
    protected int zza = 0;

    @Override // com.google.android.gms.internal.mlkit_common.zzhb
    public final zzep zze() {
        try {
            zzex zzc = zzep.zzc(zzj());
            zza(zzc.zzb());
            return zzc.zza();
        } catch (IOException e) {
            String name = getClass().getName();
            StringBuilder sb = new StringBuilder(String.valueOf(name).length() + 62 + String.valueOf("ByteString").length());
            sb.append("Serializing ");
            sb.append(name);
            sb.append(" to a ");
            sb.append("ByteString");
            sb.append(" threw an IOException (should never happen).");
            throw new RuntimeException(sb.toString(), e);
        }
    }

    public final byte[] zzf() {
        try {
            byte[] bArr = new byte[zzj()];
            zzfc zza = zzfc.zza(bArr);
            zza(zza);
            zza.zzb();
            return bArr;
        } catch (IOException e) {
            String name = getClass().getName();
            StringBuilder sb = new StringBuilder(String.valueOf(name).length() + 62 + String.valueOf("byte array").length());
            sb.append("Serializing ");
            sb.append(name);
            sb.append(" to a ");
            sb.append("byte array");
            sb.append(" threw an IOException (should never happen).");
            throw new RuntimeException(sb.toString(), e);
        }
    }

    public int zzg() {
        throw new UnsupportedOperationException();
    }

    public void zza(int i) {
        throw new UnsupportedOperationException();
    }

    public static <T> void zza(Iterable<T> iterable, List<? super T> list) {
        zzfs.zza(iterable);
        if (iterable instanceof zzgi) {
            List<?> zzb = ((zzgi) iterable).zzb();
            zzgi zzgiVar = (zzgi) list;
            int size = list.size();
            for (Object obj : zzb) {
                if (obj == null) {
                    StringBuilder sb = new StringBuilder(37);
                    sb.append("Element at index ");
                    sb.append(zzgiVar.size() - size);
                    sb.append(" is null.");
                    String sb2 = sb.toString();
                    for (int size2 = zzgiVar.size() - 1; size2 >= size; size2--) {
                        zzgiVar.remove(size2);
                    }
                    throw new NullPointerException(sb2);
                } else if (obj instanceof zzep) {
                    zzgiVar.zza((zzep) obj);
                } else {
                    zzgiVar.add((String) obj);
                }
            }
        } else if (iterable instanceof zzhk) {
            list.addAll((Collection) iterable);
        } else {
            if ((list instanceof ArrayList) && (iterable instanceof Collection)) {
                ((ArrayList) list).ensureCapacity(list.size() + ((Collection) iterable).size());
            }
            int size3 = list.size();
            for (T t : iterable) {
                if (t == null) {
                    StringBuilder sb3 = new StringBuilder(37);
                    sb3.append("Element at index ");
                    sb3.append(list.size() - size3);
                    sb3.append(" is null.");
                    String sb4 = sb3.toString();
                    for (int size4 = list.size() - 1; size4 >= size3; size4--) {
                        list.remove(size4);
                    }
                    throw new NullPointerException(sb4);
                }
                list.add(t);
            }
        }
    }
}
