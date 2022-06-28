package com.google.android.gms.internal.clearcut;

import j$.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/* loaded from: classes3.dex */
public final class zzea {
    private static final zzea zznc = new zzea();
    private final zzeg zznd;
    private final ConcurrentMap<Class<?>, zzef<?>> zzne = new ConcurrentHashMap();

    private zzea() {
        String[] strArr = {"com.google.protobuf.AndroidProto3SchemaFactory"};
        zzeg zzegVar = null;
        for (int i = 0; i <= 0; i++) {
            zzegVar = zzk(strArr[0]);
            if (zzegVar != null) {
                break;
            }
        }
        this.zznd = zzegVar == null ? new zzdd() : zzegVar;
    }

    public static zzea zzcm() {
        return zznc;
    }

    private static zzeg zzk(String str) {
        try {
            return (zzeg) Class.forName(str).getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Throwable th) {
            return null;
        }
    }

    public final <T> zzef<T> zze(Class<T> cls) {
        zzci.zza(cls, "messageType");
        zzef<T> zzefVar = (zzef<T>) this.zzne.get(cls);
        if (zzefVar == null) {
            zzef<T> zzd = this.zznd.zzd(cls);
            zzci.zza(cls, "messageType");
            zzci.zza(zzd, "schema");
            zzef<T> zzefVar2 = (zzef<T>) this.zzne.putIfAbsent(cls, zzd);
            return zzefVar2 != null ? zzefVar2 : zzd;
        }
        return zzefVar;
    }

    public final <T> zzef<T> zzp(T t) {
        return zze(t.getClass());
    }
}
