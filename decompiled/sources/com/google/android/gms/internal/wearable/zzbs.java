package com.google.android.gms.internal.wearable;

import com.google.android.gms.internal.wearable.zzbp;
import com.google.android.gms.internal.wearable.zzbs;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public abstract class zzbs<MessageType extends zzbs<MessageType, BuilderType>, BuilderType extends zzbp<MessageType, BuilderType>> extends zzaf<MessageType, BuilderType> {
    private static final Map<Object, zzbs<?, ?>> zzb = new ConcurrentHashMap();
    protected zzdx zzc = zzdx.zza();
    protected int zzd = -1;

    public static <T extends zzbs> T zzQ(Class<T> cls) {
        Map<Object, zzbs<?, ?>> map = zzb;
        zzbs<?, ?> zzbsVar = map.get(cls);
        if (zzbsVar == null) {
            try {
                Class.forName(cls.getName(), true, cls.getClassLoader());
                zzbsVar = map.get(cls);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Class initialization cannot fail.", e);
            }
        }
        if (zzbsVar == null) {
            zzbsVar = (zzbs) ((zzbs) zzeg.zzc(cls)).zzG(6, null, null);
            if (zzbsVar == null) {
                throw new IllegalStateException();
            }
            map.put(cls, zzbsVar);
        }
        return zzbsVar;
    }

    public static <T extends zzbs> void zzR(Class<T> cls, T t) {
        zzb.put(cls, t);
    }

    public static Object zzS(zzcx zzcxVar, String str, Object[] objArr) {
        return new zzdh(zzcxVar, str, objArr);
    }

    public static Object zzT(Method method, Object obj, Object... objArr) {
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

    public static zzby zzU() {
        return zzcm.zzd();
    }

    public static zzbx zzV() {
        return zzbn.zzd();
    }

    public static <E> zzbz<E> zzW() {
        return zzdg.zzd();
    }

    public static <E> zzbz<E> zzX(zzbz<E> zzbzVar) {
        int size = zzbzVar.size();
        return zzbzVar.zze(size == 0 ? 10 : size + size);
    }

    static <T extends zzbs<T, ?>> T zzY(T t, byte[] bArr, int i, int i2, zzbg zzbgVar) throws zzcc {
        T t2 = (T) t.zzG(4, null, null);
        try {
            zzdi zzb2 = zzdf.zza().zzb(t2.getClass());
            zzb2.zzh(t2, bArr, 0, i2, new zzai(zzbgVar));
            zzb2.zzi(t2);
            if (t2.zza == 0) {
                return t2;
            }
            throw new RuntimeException();
        } catch (zzcc e) {
            e.zza(t2);
            throw e;
        } catch (IOException e2) {
            if (e2.getCause() instanceof zzcc) {
                throw ((zzcc) e2.getCause());
            }
            zzcc zzccVar = new zzcc(e2);
            zzccVar.zza(t2);
            throw zzccVar;
        } catch (IndexOutOfBoundsException e3) {
            zzcc zzb3 = zzcc.zzb();
            zzb3.zza(t2);
            throw zzb3;
        }
    }

    public static <T extends zzbs<T, ?>> T zzZ(T t, byte[] bArr) throws zzcc {
        T t2 = (T) zzY(t, bArr, 0, bArr.length, zzbg.zza());
        if (t2 == null || t2.zzN()) {
            return t2;
        }
        zzcc zzccVar = new zzcc(new zzdv(t2).getMessage());
        zzccVar.zza(t2);
        throw zzccVar;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return zzdf.zza().zzb(getClass()).zzb(this, (zzbs) obj);
    }

    public final int hashCode() {
        int i = this.zza;
        if (i != 0) {
            return i;
        }
        int zzc = zzdf.zza().zzb(getClass()).zzc(this);
        this.zza = zzc;
        return zzc;
    }

    public final String toString() {
        return zzcz.zza(this, super.toString());
    }

    public abstract Object zzG(int i, Object obj, Object obj2);

    @Override // com.google.android.gms.internal.wearable.zzaf
    public final int zzJ() {
        return this.zzd;
    }

    @Override // com.google.android.gms.internal.wearable.zzaf
    public final void zzK(int i) {
        this.zzd = i;
    }

    public final <MessageType extends zzbs<MessageType, BuilderType>, BuilderType extends zzbp<MessageType, BuilderType>> BuilderType zzM() {
        return (BuilderType) zzG(5, null, null);
    }

    public final boolean zzN() {
        boolean booleanValue = Boolean.TRUE.booleanValue();
        byte byteValue = ((Byte) zzG(1, null, null)).byteValue();
        if (byteValue == 1) {
            return true;
        }
        if (byteValue == 0) {
            return false;
        }
        boolean zzj = zzdf.zza().zzb(getClass()).zzj(this);
        if (!booleanValue) {
            return zzj;
        }
        zzG(2, true != zzj ? null : this, null);
        return zzj;
    }

    @Override // com.google.android.gms.internal.wearable.zzcx
    public final void zzO(zzbb zzbbVar) throws IOException {
        zzdf.zza().zzb(getClass()).zzm(this, zzbc.zza(zzbbVar));
    }

    @Override // com.google.android.gms.internal.wearable.zzcx
    public final int zzP() {
        int i = this.zzd;
        if (i == -1) {
            int zze = zzdf.zza().zzb(getClass()).zze(this);
            this.zzd = zze;
            return zze;
        }
        return i;
    }

    @Override // com.google.android.gms.internal.wearable.zzcx
    public final /* bridge */ /* synthetic */ zzcw zzaa() {
        zzbp zzbpVar = (zzbp) zzG(5, null, null);
        zzbpVar.zzv(this);
        return zzbpVar;
    }

    @Override // com.google.android.gms.internal.wearable.zzcx
    public final /* bridge */ /* synthetic */ zzcw zzab() {
        return (zzbp) zzG(5, null, null);
    }

    @Override // com.google.android.gms.internal.wearable.zzcy
    public final /* bridge */ /* synthetic */ zzcx zzac() {
        return (zzbs) zzG(6, null, null);
    }
}
