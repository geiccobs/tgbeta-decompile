package com.google.android.gms.internal.icing;

import com.google.android.gms.internal.icing.zzcx;
import com.google.android.gms.internal.icing.zzda;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public abstract class zzda<MessageType extends zzda<MessageType, BuilderType>, BuilderType extends zzcx<MessageType, BuilderType>> extends zzbs<MessageType, BuilderType> {
    private static final Map<Object, zzda<?, ?>> zzb = new ConcurrentHashMap();
    protected zzfe zzc = zzfe.zza();
    protected int zzd = -1;

    public static <T extends zzda> T zzp(Class<T> cls) {
        Map<Object, zzda<?, ?>> map = zzb;
        zzda<?, ?> zzdaVar = map.get(cls);
        if (zzdaVar == null) {
            try {
                Class.forName(cls.getName(), true, cls.getClassLoader());
                zzdaVar = map.get(cls);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Class initialization cannot fail.", e);
            }
        }
        if (zzdaVar == null) {
            zzdaVar = (zzda) ((zzda) zzfn.zzc(cls)).zzf(6, null, null);
            if (zzdaVar == null) {
                throw new IllegalStateException();
            }
            map.put(cls, zzdaVar);
        }
        return zzdaVar;
    }

    public static <T extends zzda> void zzq(Class<T> cls, T t) {
        zzb.put(cls, t);
    }

    public static Object zzr(zzee zzeeVar, String str, Object[] objArr) {
        return new zzeo(zzeeVar, str, objArr);
    }

    public static Object zzs(Method method, Object obj, Object... objArr) {
        try {
            return method.invoke(obj, objArr);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Couldn't use Java reflection to implement protocol message reflection.", e);
        } catch (InvocationTargetException e2) {
            Throwable cause = e2.getCause();
            if (cause instanceof RuntimeException) {
                throw ((RuntimeException) cause);
            }
            if (!(cause instanceof Error)) {
                throw new RuntimeException("Unexpected exception thrown by generated accessor method.", cause);
            }
            throw ((Error) cause);
        }
    }

    public static zzdf zzt() {
        return zzdt.zzd();
    }

    public static zzdd zzu() {
        return zzco.zzd();
    }

    public static zzdc zzv() {
        return zzbv.zzd();
    }

    public static <E> zzdg<E> zzw() {
        return zzen.zzd();
    }

    public static <E> zzdg<E> zzx(zzdg<E> zzdgVar) {
        int size = zzdgVar.size();
        return zzdgVar.zze(size == 0 ? 10 : size + size);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return zzem.zza().zzb(getClass()).zza(this, (zzda) obj);
    }

    public final int hashCode() {
        int i = this.zza;
        if (i != 0) {
            return i;
        }
        int zzb2 = zzem.zza().zzb(getClass()).zzb(this);
        this.zza = zzb2;
        return zzb2;
    }

    public final String toString() {
        return zzeg.zza(this, super.toString());
    }

    public abstract Object zzf(int i, Object obj, Object obj2);

    @Override // com.google.android.gms.internal.icing.zzbs
    public final int zzi() {
        return this.zzd;
    }

    @Override // com.google.android.gms.internal.icing.zzbs
    public final void zzj(int i) {
        this.zzd = i;
    }

    public final <MessageType extends zzda<MessageType, BuilderType>, BuilderType extends zzcx<MessageType, BuilderType>> BuilderType zzl() {
        return (BuilderType) zzf(5, null, null);
    }

    @Override // com.google.android.gms.internal.icing.zzef
    public final /* bridge */ /* synthetic */ zzee zzm() {
        return (zzda) zzf(6, null, null);
    }

    @Override // com.google.android.gms.internal.icing.zzee
    public final void zzn(zzcm zzcmVar) throws IOException {
        zzem.zza().zzb(getClass()).zzi(this, zzcn.zza(zzcmVar));
    }

    @Override // com.google.android.gms.internal.icing.zzee
    public final int zzo() {
        int i = this.zzd;
        if (i == -1) {
            int zzd = zzem.zza().zzb(getClass()).zzd(this);
            this.zzd = zzd;
            return zzd;
        }
        return i;
    }

    @Override // com.google.android.gms.internal.icing.zzee
    public final /* bridge */ /* synthetic */ zzed zzy() {
        zzcx zzcxVar = (zzcx) zzf(5, null, null);
        zzcxVar.zzk(this);
        return zzcxVar;
    }
}
