package com.google.android.gms.internal.vision;

import com.google.android.gms.internal.vision.zzjb;
import com.google.android.gms.internal.vision.zzjb.zzb;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public abstract class zzjb<MessageType extends zzjb<MessageType, BuilderType>, BuilderType extends zzb<MessageType, BuilderType>> extends zzhf<MessageType, BuilderType> {
    private static Map<Object, zzjb<?, ?>> zzd = new ConcurrentHashMap();
    protected zzlx zzb = zzlx.zza();
    private int zzc = -1;

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    protected static class zza<T extends zzjb<T, ?>> extends zzhg<T> {
        private final T zza;

        public zza(T t) {
            this.zza = t;
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static final class zzg {
        public static final int zza = 1;
        public static final int zzb = 2;
        public static final int zzc = 3;
        public static final int zzd = 4;
        public static final int zze = 5;
        public static final int zzf = 6;
        public static final int zzg = 7;
        private static final /* synthetic */ int[] zzh = {1, 2, 3, 4, 5, 6, 7};

        public static int[] zza() {
            return (int[]) zzh.clone();
        }
    }

    public abstract Object zza(int i, Object obj, Object obj2);

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static abstract class zzc<MessageType extends zzc<MessageType, BuilderType>, BuilderType extends zzd<MessageType, BuilderType>> extends zzjb<MessageType, BuilderType> implements zzkm {
        protected zziu<zzf> zzc = zziu.zza();

        public final zziu<zzf> zza() {
            if (this.zzc.zzc()) {
                this.zzc = (zziu) this.zzc.clone();
            }
            return this.zzc;
        }

        /* JADX WARN: Type inference failed for: r1v8, types: [Type, java.util.List, java.util.ArrayList] */
        public final <Type> Type zzb(zzim<MessageType, Type> zzimVar) {
            zze zzb = zzjb.zzb(zzimVar);
            if (zzb.zza != ((zzjb) zzr())) {
                throw new IllegalArgumentException("This extension is for a different message type.  Please make sure that you are not suppressing any generics type warnings.");
            }
            Type type = (Type) this.zzc.zza((zziu<zzf>) zzb.zzd);
            if (type == null) {
                return zzb.zzb;
            }
            if (zzb.zzd.zzd) {
                if (zzb.zzd.zzc.zza() == zzmo.ENUM) {
                    ?? r1 = (Type) new ArrayList();
                    for (Object obj : (List) type) {
                        r1.add(zzb.zza(obj));
                    }
                    return r1;
                }
                return type;
            }
            return (Type) zzb.zza(type);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static abstract class zzd<MessageType extends zzc<MessageType, BuilderType>, BuilderType extends zzd<MessageType, BuilderType>> extends zzb<MessageType, BuilderType> implements zzkm {
        protected zzd(MessageType messagetype) {
            super(messagetype);
        }

        @Override // com.google.android.gms.internal.vision.zzjb.zzb
        public void zzb() {
            super.zzb();
            ((zzc) this.zza).zzc = (zziu) ((zzc) this.zza).zzc.clone();
        }

        @Override // com.google.android.gms.internal.vision.zzjb.zzb
        public /* synthetic */ zzjb zzc() {
            return (zzc) zze();
        }

        @Override // com.google.android.gms.internal.vision.zzjb.zzb, com.google.android.gms.internal.vision.zzkn
        public /* synthetic */ zzkk zze() {
            if (this.zzb) {
                return (zzc) this.zza;
            }
            ((zzc) this.zza).zzc.zzb();
            return (zzc) super.zze();
        }
    }

    public String toString() {
        return zzkp.zza(this, super.toString());
    }

    public int hashCode() {
        if (this.zza != 0) {
            return this.zza;
        }
        this.zza = zzky.zza().zza((zzky) this).zza(this);
        return this.zza;
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static abstract class zzb<MessageType extends zzjb<MessageType, BuilderType>, BuilderType extends zzb<MessageType, BuilderType>> extends zzhe<MessageType, BuilderType> {
        protected MessageType zza;
        protected boolean zzb = false;
        private final MessageType zzc;

        public zzb(MessageType messagetype) {
            this.zzc = messagetype;
            this.zza = (MessageType) messagetype.zza(zzg.zzd, null, null);
        }

        public void zzb() {
            MessageType messagetype = (MessageType) this.zza.zza(zzg.zzd, null, null);
            zza(messagetype, this.zza);
            this.zza = messagetype;
        }

        @Override // com.google.android.gms.internal.vision.zzkm
        public final boolean zzk() {
            return zzjb.zza(this.zza, false);
        }

        /* renamed from: zzc */
        public MessageType zze() {
            if (this.zzb) {
                return this.zza;
            }
            MessageType messagetype = this.zza;
            zzky.zza().zza((zzky) messagetype).zzc(messagetype);
            this.zzb = true;
            return this.zza;
        }

        /* renamed from: zzd */
        public final MessageType zzf() {
            MessageType messagetype = (MessageType) zze();
            if (!messagetype.zzk()) {
                throw new zzlv(messagetype);
            }
            return messagetype;
        }

        public final BuilderType zza(MessageType messagetype) {
            if (this.zzb) {
                zzb();
                this.zzb = false;
            }
            zza(this.zza, messagetype);
            return this;
        }

        private static void zza(MessageType messagetype, MessageType messagetype2) {
            zzky.zza().zza((zzky) messagetype).zzb(messagetype, messagetype2);
        }

        private final BuilderType zzb(byte[] bArr, int i, int i2, zzio zzioVar) throws zzjk {
            if (this.zzb) {
                zzb();
                this.zzb = false;
            }
            try {
                zzky.zza().zza((zzky) this.zza).zza(this.zza, bArr, 0, i2, new zzhn(zzioVar));
                return this;
            } catch (zzjk e) {
                throw e;
            } catch (IOException e2) {
                throw new RuntimeException("Reading from byte array should not throw IOException.", e2);
            } catch (IndexOutOfBoundsException e3) {
                throw zzjk.zza();
            }
        }

        /* renamed from: zzb */
        public final BuilderType zza(zzif zzifVar, zzio zzioVar) throws IOException {
            if (this.zzb) {
                zzb();
                this.zzb = false;
            }
            try {
                zzky.zza().zza((zzky) this.zza).zza(this.zza, zzig.zza(zzifVar), zzioVar);
                return this;
            } catch (RuntimeException e) {
                if (e.getCause() instanceof IOException) {
                    throw ((IOException) e.getCause());
                }
                throw e;
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.google.android.gms.internal.vision.zzhe
        protected final /* synthetic */ zzhe zza(zzhf zzhfVar) {
            return zza((zzb<MessageType, BuilderType>) ((zzjb) zzhfVar));
        }

        @Override // com.google.android.gms.internal.vision.zzhe
        public final /* synthetic */ zzhe zza(byte[] bArr, int i, int i2, zzio zzioVar) throws zzjk {
            return zzb(bArr, 0, i2, zzioVar);
        }

        @Override // com.google.android.gms.internal.vision.zzhe
        public final /* synthetic */ zzhe zza() {
            return (zzb) clone();
        }

        @Override // com.google.android.gms.internal.vision.zzkm
        public final /* synthetic */ zzkk zzr() {
            return this.zzc;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.google.android.gms.internal.vision.zzhe
        public /* synthetic */ Object clone() throws CloneNotSupportedException {
            zzb zzbVar = (zzb) this.zzc.zza(zzg.zze, null, null);
            zzbVar.zza((zzb) ((zzjb) zze()));
            return zzbVar;
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static final class zzf implements zziw<zzf> {
        final zzml zzc;
        final zzjh<?> zza = null;
        final int zzb = 202056002;
        final boolean zzd = true;
        final boolean zze = false;

        zzf(zzjh<?> zzjhVar, int i, zzml zzmlVar, boolean z, boolean z2) {
            this.zzc = zzmlVar;
        }

        @Override // com.google.android.gms.internal.vision.zziw
        public final int zza() {
            return this.zzb;
        }

        @Override // com.google.android.gms.internal.vision.zziw
        public final zzml zzb() {
            return this.zzc;
        }

        @Override // com.google.android.gms.internal.vision.zziw
        public final zzmo zzc() {
            return this.zzc.zza();
        }

        @Override // com.google.android.gms.internal.vision.zziw
        public final boolean zzd() {
            return this.zzd;
        }

        @Override // com.google.android.gms.internal.vision.zziw
        public final boolean zze() {
            return false;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.google.android.gms.internal.vision.zziw
        public final zzkn zza(zzkn zzknVar, zzkk zzkkVar) {
            return ((zzb) zzknVar).zza((zzb) ((zzjb) zzkkVar));
        }

        @Override // com.google.android.gms.internal.vision.zziw
        public final zzkt zza(zzkt zzktVar, zzkt zzktVar2) {
            throw new UnsupportedOperationException();
        }

        @Override // java.lang.Comparable
        public final /* synthetic */ int compareTo(Object obj) {
            return this.zzb - ((zzf) obj).zzb;
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return zzky.zza().zza((zzky) this).zza(this, (zzjb) obj);
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static class zze<ContainingType extends zzkk, Type> extends zzim<ContainingType, Type> {
        final ContainingType zza;
        final Type zzb;
        final zzkk zzc;
        final zzf zzd;

        zze(ContainingType containingtype, Type type, zzkk zzkkVar, zzf zzfVar, Class cls) {
            if (containingtype == null) {
                throw new IllegalArgumentException("Null containingTypeDefaultInstance");
            }
            if (zzfVar.zzc == zzml.MESSAGE && zzkkVar == null) {
                throw new IllegalArgumentException("Null messageDefaultInstance");
            }
            this.zza = containingtype;
            this.zzb = type;
            this.zzc = zzkkVar;
            this.zzd = zzfVar;
        }

        final Object zza(Object obj) {
            if (this.zzd.zzc.zza() == zzmo.ENUM) {
                zzjh zzjhVar = null;
                return zzjhVar.zza(((Integer) obj).intValue());
            }
            return obj;
        }
    }

    public final <MessageType extends zzjb<MessageType, BuilderType>, BuilderType extends zzb<MessageType, BuilderType>> BuilderType zzj() {
        return (BuilderType) zza(zzg.zze, (Object) null, (Object) null);
    }

    @Override // com.google.android.gms.internal.vision.zzkm
    public final boolean zzk() {
        return zza(this, Boolean.TRUE.booleanValue());
    }

    public final BuilderType zzl() {
        BuilderType buildertype = (BuilderType) zza(zzg.zze, (Object) null, (Object) null);
        buildertype.zza(this);
        return buildertype;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.android.gms.internal.vision.zzhf
    public final int zzi() {
        return this.zzc;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.android.gms.internal.vision.zzhf
    public final void zzb(int i) {
        this.zzc = i;
    }

    @Override // com.google.android.gms.internal.vision.zzkk
    public final void zza(zzii zziiVar) throws IOException {
        zzky.zza().zza((zzky) this).zza((zzlc) this, (zzmr) zzil.zza(zziiVar));
    }

    @Override // com.google.android.gms.internal.vision.zzkk
    public final int zzm() {
        if (this.zzc == -1) {
            this.zzc = zzky.zza().zza((zzky) this).zzb(this);
        }
        return this.zzc;
    }

    public static <T extends zzjb<?, ?>> T zza(Class<T> cls) {
        T t = zzd.get(cls);
        if (t == null) {
            try {
                Class.forName(cls.getName(), true, cls.getClassLoader());
                t = zzd.get(cls);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Class initialization cannot fail.", e);
            }
        }
        if (t == null) {
            t = (T) ((zzjb) zzma.zza(cls)).zza(zzg.zzf, (Object) null, (Object) null);
            if (t == null) {
                throw new IllegalStateException();
            }
            zzd.put(cls, t);
        }
        return (T) t;
    }

    public static <T extends zzjb<?, ?>> void zza(Class<T> cls, T t) {
        zzd.put(cls, t);
    }

    public static Object zza(zzkk zzkkVar, String str, Object[] objArr) {
        return new zzla(zzkkVar, str, objArr);
    }

    public static <ContainingType extends zzkk, Type> zze<ContainingType, Type> zza(ContainingType containingtype, zzkk zzkkVar, zzjh<?> zzjhVar, int i, zzml zzmlVar, boolean z, Class cls) {
        return new zze<>(containingtype, Collections.emptyList(), zzkkVar, new zzf(null, 202056002, zzmlVar, true, false), cls);
    }

    public static Object zza(Method method, Object obj, Object... objArr) {
        try {
            return method.invoke(obj, objArr);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Couldn't use Java reflection to implement protocol message reflection.", e);
        } catch (InvocationTargetException e2) {
            Throwable cause = e2.getCause();
            if (cause instanceof RuntimeException) {
                throw ((RuntimeException) cause);
            }
            if (cause instanceof Error) {
                throw ((Error) cause);
            }
            throw new RuntimeException("Unexpected exception thrown by generated accessor method.", cause);
        }
    }

    public static <MessageType extends zzc<MessageType, BuilderType>, BuilderType extends zzd<MessageType, BuilderType>, T> zze<MessageType, T> zzb(zzim<MessageType, T> zzimVar) {
        return (zze) zzimVar;
    }

    protected static final <T extends zzjb<T, ?>> boolean zza(T t, boolean z) {
        byte byteValue = ((Byte) t.zza(zzg.zza, null, null)).byteValue();
        if (byteValue == 1) {
            return true;
        }
        if (byteValue == 0) {
            return false;
        }
        boolean zzd2 = zzky.zza().zza((zzky) t).zzd(t);
        if (z) {
            t.zza(zzg.zzb, zzd2 ? t : null, null);
        }
        return zzd2;
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.google.android.gms.internal.vision.zzjj, com.google.android.gms.internal.vision.zzjd] */
    public static zzjj zzn() {
        return zzjd.zzd();
    }

    public static <E> zzjl<E> zzo() {
        return zzlb.zzd();
    }

    public static <E> zzjl<E> zza(zzjl<E> zzjlVar) {
        int size = zzjlVar.size();
        return zzjlVar.zza(size == 0 ? 10 : size << 1);
    }

    private static <T extends zzjb<T, ?>> T zza(T t, byte[] bArr, int i, int i2, zzio zzioVar) throws zzjk {
        T t2 = (T) t.zza(zzg.zzd, null, null);
        try {
            zzlc zza2 = zzky.zza().zza((zzky) t2);
            zza2.zza(t2, bArr, 0, i2, new zzhn(zzioVar));
            zza2.zzc(t2);
            if (t2.zza != 0) {
                throw new RuntimeException();
            }
            return t2;
        } catch (IOException e) {
            if (e.getCause() instanceof zzjk) {
                throw ((zzjk) e.getCause());
            }
            throw new zzjk(e.getMessage()).zza(t2);
        } catch (IndexOutOfBoundsException e2) {
            throw zzjk.zza().zza(t2);
        }
    }

    private static <T extends zzjb<T, ?>> T zza(T t) throws zzjk {
        if (t != null && !t.zzk()) {
            throw new zzjk(new zzlv(t).getMessage()).zza(t);
        }
        return t;
    }

    protected static <T extends zzjb<T, ?>> T zza(T t, byte[] bArr) throws zzjk {
        return (T) zza(zza(t, bArr, 0, bArr.length, zzio.zzb()));
    }

    protected static <T extends zzjb<T, ?>> T zza(T t, byte[] bArr, zzio zzioVar) throws zzjk {
        return (T) zza(zza(t, bArr, 0, bArr.length, zzioVar));
    }

    @Override // com.google.android.gms.internal.vision.zzkk
    public final /* synthetic */ zzkn zzp() {
        zzb zzbVar = (zzb) zza(zzg.zze, (Object) null, (Object) null);
        zzbVar.zza((zzb) this);
        return zzbVar;
    }

    @Override // com.google.android.gms.internal.vision.zzkk
    public final /* synthetic */ zzkn zzq() {
        return (zzb) zza(zzg.zze, (Object) null, (Object) null);
    }

    @Override // com.google.android.gms.internal.vision.zzkm
    public final /* synthetic */ zzkk zzr() {
        return (zzjb) zza(zzg.zzf, (Object) null, (Object) null);
    }
}
