package com.google.android.gms.internal.clearcut;

import com.google.android.gms.internal.clearcut.zzcg;
import com.google.android.gms.internal.clearcut.zzcg.zza;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
/* loaded from: classes3.dex */
public abstract class zzcg<MessageType extends zzcg<MessageType, BuilderType>, BuilderType extends zza<MessageType, BuilderType>> extends zzas<MessageType, BuilderType> {
    private static Map<Object, zzcg<?, ?>> zzjr = new ConcurrentHashMap();
    protected zzey zzjp = zzey.zzea();
    private int zzjq = -1;

    /* loaded from: classes3.dex */
    public static abstract class zza<MessageType extends zzcg<MessageType, BuilderType>, BuilderType extends zza<MessageType, BuilderType>> extends zzat<MessageType, BuilderType> {
        private final MessageType zzjs;
        protected MessageType zzjt;
        protected boolean zzju = false;

        public zza(MessageType messagetype) {
            this.zzjs = messagetype;
            this.zzjt = (MessageType) messagetype.zza(zzg.zzkg, null, null);
        }

        private static void zza(MessageType messagetype, MessageType messagetype2) {
            zzea.zzcm().zzp(messagetype).zzc(messagetype, messagetype2);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.google.android.gms.internal.clearcut.zzat
        public /* synthetic */ Object clone() throws CloneNotSupportedException {
            zza zzaVar = (zza) this.zzjs.zza(zzg.zzkh, null, null);
            zzaVar.zza((zza) ((zzcg) zzbi()));
            return zzaVar;
        }

        @Override // com.google.android.gms.internal.clearcut.zzdq
        public final boolean isInitialized() {
            return zzcg.zza(this.zzjt, false);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.google.android.gms.internal.clearcut.zzat
        protected final /* synthetic */ zzat zza(zzas zzasVar) {
            return zza((zza<MessageType, BuilderType>) ((zzcg) zzasVar));
        }

        public final BuilderType zza(MessageType messagetype) {
            zzbf();
            zza(this.zzjt, messagetype);
            return this;
        }

        @Override // com.google.android.gms.internal.clearcut.zzdq
        public final /* synthetic */ zzdo zzbe() {
            return this.zzjs;
        }

        public void zzbf() {
            if (this.zzju) {
                MessageType messagetype = (MessageType) this.zzjt.zza(zzg.zzkg, null, null);
                zza(messagetype, this.zzjt);
                this.zzjt = messagetype;
                this.zzju = false;
            }
        }

        /* renamed from: zzbg */
        public MessageType zzbi() {
            if (this.zzju) {
                return this.zzjt;
            }
            MessageType messagetype = this.zzjt;
            zzea.zzcm().zzp(messagetype).zzc(messagetype);
            this.zzju = true;
            return this.zzjt;
        }

        public final MessageType zzbh() {
            MessageType messagetype = (MessageType) zzbi();
            boolean booleanValue = Boolean.TRUE.booleanValue();
            byte byteValue = ((Byte) messagetype.zza(zzg.zzkd, null, null)).byteValue();
            boolean z = true;
            if (byteValue != 1) {
                if (byteValue == 0) {
                    z = false;
                } else {
                    z = zzea.zzcm().zzp(messagetype).zzo(messagetype);
                    if (booleanValue) {
                        messagetype.zza(zzg.zzke, z ? messagetype : null, null);
                    }
                }
            }
            if (z) {
                return messagetype;
            }
            throw new zzew(messagetype);
        }

        @Override // com.google.android.gms.internal.clearcut.zzdp
        public final /* synthetic */ zzdo zzbj() {
            zzcg zzcgVar = (zzcg) zzbi();
            boolean booleanValue = Boolean.TRUE.booleanValue();
            byte byteValue = ((Byte) zzcgVar.zza(zzg.zzkd, (Object) null, (Object) null)).byteValue();
            boolean z = true;
            if (byteValue != 1) {
                if (byteValue == 0) {
                    z = false;
                } else {
                    z = zzea.zzcm().zzp(zzcgVar).zzo(zzcgVar);
                    if (booleanValue) {
                        zzcgVar.zza(zzg.zzke, z ? zzcgVar : null, (Object) null);
                    }
                }
            }
            if (z) {
                return zzcgVar;
            }
            throw new zzew(zzcgVar);
        }

        @Override // com.google.android.gms.internal.clearcut.zzat
        public final /* synthetic */ zzat zzt() {
            return (zza) clone();
        }
    }

    /* loaded from: classes3.dex */
    public static class zzb<T extends zzcg<T, ?>> extends zzau<T> {
        private T zzjs;

        public zzb(T t) {
            this.zzjs = t;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class zzc<MessageType extends zzd<MessageType, BuilderType>, BuilderType extends zzc<MessageType, BuilderType>> extends zza<MessageType, BuilderType> implements zzdq {
        public zzc(MessageType messagetype) {
            super(messagetype);
        }

        @Override // com.google.android.gms.internal.clearcut.zzcg.zza
        public final void zzbf() {
            if (!this.zzju) {
                return;
            }
            super.zzbf();
            ((zzd) this.zzjt).zzjv = (zzby) ((zzd) this.zzjt).zzjv.clone();
        }

        @Override // com.google.android.gms.internal.clearcut.zzcg.zza
        public final /* synthetic */ zzcg zzbg() {
            return (zzd) zzbi();
        }

        @Override // com.google.android.gms.internal.clearcut.zzcg.zza, com.google.android.gms.internal.clearcut.zzdp
        public final /* synthetic */ zzdo zzbi() {
            MessageType messagetype;
            if (this.zzju) {
                messagetype = this.zzjt;
            } else {
                ((zzd) this.zzjt).zzjv.zzv();
                messagetype = (MessageType) super.zzbi();
            }
            return (zzd) messagetype;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class zzd<MessageType extends zzd<MessageType, BuilderType>, BuilderType extends zzc<MessageType, BuilderType>> extends zzcg<MessageType, BuilderType> implements zzdq {
        protected zzby<zze> zzjv = zzby.zzar();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static final class zze implements zzca<zze> {
        final zzfl zzjx;
        private final zzck<?> zzjw = null;
        final int number = 66321687;
        final boolean zzjy = false;
        final boolean zzjz = false;

        zze(zzck<?> zzckVar, int i, zzfl zzflVar, boolean z, boolean z2) {
            this.zzjx = zzflVar;
        }

        @Override // java.lang.Comparable
        public final /* synthetic */ int compareTo(Object obj) {
            return this.number - ((zze) obj).number;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.google.android.gms.internal.clearcut.zzca
        public final zzdp zza(zzdp zzdpVar, zzdo zzdoVar) {
            return ((zza) zzdpVar).zza((zza) ((zzcg) zzdoVar));
        }

        @Override // com.google.android.gms.internal.clearcut.zzca
        public final zzdv zza(zzdv zzdvVar, zzdv zzdvVar2) {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.android.gms.internal.clearcut.zzca
        public final zzfl zzau() {
            return this.zzjx;
        }

        @Override // com.google.android.gms.internal.clearcut.zzca
        public final zzfq zzav() {
            return this.zzjx.zzek();
        }

        @Override // com.google.android.gms.internal.clearcut.zzca
        public final boolean zzaw() {
            return false;
        }

        @Override // com.google.android.gms.internal.clearcut.zzca
        public final boolean zzax() {
            return false;
        }

        @Override // com.google.android.gms.internal.clearcut.zzca
        public final int zzc() {
            return this.number;
        }
    }

    /* loaded from: classes3.dex */
    public static class zzf<ContainingType extends zzdo, Type> extends zzbr<ContainingType, Type> {
        private final Type zzdu;
        private final ContainingType zzka;
        private final zzdo zzkb;
        private final zze zzkc;

        zzf(ContainingType containingtype, Type type, zzdo zzdoVar, zze zzeVar, Class cls) {
            if (containingtype != null) {
                if (zzeVar.zzjx == zzfl.MESSAGE && zzdoVar == null) {
                    throw new IllegalArgumentException("Null messageDefaultInstance");
                }
                this.zzka = containingtype;
                this.zzdu = type;
                this.zzkb = zzdoVar;
                this.zzkc = zzeVar;
                return;
            }
            throw new IllegalArgumentException("Null containingTypeDefaultInstance");
        }
    }

    /* loaded from: classes3.dex */
    public static final class zzg {
        public static final int zzkd = 1;
        public static final int zzke = 2;
        public static final int zzkf = 3;
        public static final int zzkg = 4;
        public static final int zzkh = 5;
        public static final int zzki = 6;
        public static final int zzkj = 7;
        private static final /* synthetic */ int[] zzkk = {1, 2, 3, 4, 5, 6, 7};
        public static final int zzkl = 1;
        public static final int zzkm = 2;
        private static final /* synthetic */ int[] zzkn = {1, 2};
        public static final int zzko = 1;
        public static final int zzkp = 2;
        private static final /* synthetic */ int[] zzkq = {1, 2};

        public static int[] values$50KLMJ33DTMIUPRFDTJMOP9FE1P6UT3FC9QMCBQ7CLN6ASJ1EHIM8JB5EDPM2PR59HKN8P949LIN8Q3FCHA6UIBEEPNMMP9R0() {
            return (int[]) zzkk.clone();
        }
    }

    public static <ContainingType extends zzdo, Type> zzf<ContainingType, Type> zza(ContainingType containingtype, Type type, zzdo zzdoVar, zzck<?> zzckVar, int i, zzfl zzflVar, Class cls) {
        return new zzf<>(containingtype, type, zzdoVar, new zze(null, 66321687, zzflVar, false, false), cls);
    }

    private static <T extends zzcg<T, ?>> T zza(T t, byte[] bArr) throws zzco {
        T t2 = (T) t.zza(zzg.zzkg, null, null);
        try {
            zzea.zzcm().zzp(t2).zza(t2, bArr, 0, bArr.length, new zzay());
            zzea.zzcm().zzp(t2).zzc(t2);
            if (t2.zzex == 0) {
                return t2;
            }
            throw new RuntimeException();
        } catch (IOException e) {
            if (!(e.getCause() instanceof zzco)) {
                throw new zzco(e.getMessage()).zzg(t2);
            }
            throw ((zzco) e.getCause());
        } catch (IndexOutOfBoundsException e2) {
            throw zzco.zzbl().zzg(t2);
        }
    }

    public static Object zza(zzdo zzdoVar, String str, Object[] objArr) {
        return new zzec(zzdoVar, str, objArr);
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
            if (!(cause instanceof Error)) {
                throw new RuntimeException("Unexpected exception thrown by generated accessor method.", cause);
            }
            throw ((Error) cause);
        }
    }

    public static <T extends zzcg<?, ?>> void zza(Class<T> cls, T t) {
        zzjr.put(cls, t);
    }

    protected static final <T extends zzcg<T, ?>> boolean zza(T t, boolean z) {
        byte byteValue = ((Byte) t.zza(zzg.zzkd, null, null)).byteValue();
        if (byteValue == 1) {
            return true;
        }
        if (byteValue != 0) {
            return zzea.zzcm().zzp(t).zzo(t);
        }
        return false;
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.google.android.gms.internal.clearcut.zzch, com.google.android.gms.internal.clearcut.zzcl] */
    public static zzcl zzaz() {
        return zzch.zzbk();
    }

    public static <T extends zzcg<T, ?>> T zzb(T t, byte[] bArr) throws zzco {
        T t2 = (T) zza(t, bArr);
        if (t2 != null) {
            boolean booleanValue = Boolean.TRUE.booleanValue();
            byte byteValue = ((Byte) t2.zza(zzg.zzkd, null, null)).byteValue();
            boolean z = true;
            if (byteValue != 1) {
                if (byteValue == 0) {
                    z = false;
                } else {
                    z = zzea.zzcm().zzp(t2).zzo(t2);
                    if (booleanValue) {
                        t2.zza(zzg.zzke, z ? t2 : null, null);
                    }
                }
            }
            if (!z) {
                throw new zzco(new zzew(t2).getMessage()).zzg(t2);
            }
        }
        return t2;
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.google.android.gms.internal.clearcut.zzcm, com.google.android.gms.internal.clearcut.zzdc] */
    public static zzcm zzba() {
        return zzdc.zzbx();
    }

    public static <E> zzcn<E> zzbb() {
        return zzeb.zzcn();
    }

    public static <T extends zzcg<?, ?>> T zzc(Class<T> cls) {
        T t = (T) zzjr.get(cls);
        if (t == null) {
            try {
                Class.forName(cls.getName(), true, cls.getClassLoader());
                t = (T) zzjr.get(cls);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Class initialization cannot fail.", e);
            }
        }
        if (t == null) {
            String valueOf = String.valueOf(cls.getName());
            throw new IllegalStateException(valueOf.length() != 0 ? "Unable to get default instance for: ".concat(valueOf) : new String("Unable to get default instance for: "));
        }
        return t;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (((zzcg) zza(zzg.zzki, (Object) null, (Object) null)).getClass().isInstance(obj)) {
            return zzea.zzcm().zzp(this).equals(this, (zzcg) obj);
        }
        return false;
    }

    public int hashCode() {
        if (this.zzex != 0) {
            return this.zzex;
        }
        this.zzex = zzea.zzcm().zzp(this).hashCode(this);
        return this.zzex;
    }

    @Override // com.google.android.gms.internal.clearcut.zzdq
    public final boolean isInitialized() {
        boolean booleanValue = Boolean.TRUE.booleanValue();
        byte byteValue = ((Byte) zza(zzg.zzkd, (Object) null, (Object) null)).byteValue();
        if (byteValue == 1) {
            return true;
        }
        if (byteValue == 0) {
            return false;
        }
        boolean zzo = zzea.zzcm().zzp(this).zzo(this);
        if (booleanValue) {
            zza(zzg.zzke, zzo ? this : null, (Object) null);
        }
        return zzo;
    }

    public String toString() {
        return zzdr.zza(this, super.toString());
    }

    public abstract Object zza(int i, Object obj, Object obj2);

    @Override // com.google.android.gms.internal.clearcut.zzdo
    public final int zzas() {
        if (this.zzjq == -1) {
            this.zzjq = zzea.zzcm().zzp(this).zzm(this);
        }
        return this.zzjq;
    }

    @Override // com.google.android.gms.internal.clearcut.zzdo
    public final void zzb(zzbn zzbnVar) throws IOException {
        zzea.zzcm().zze(getClass()).zza(this, zzbp.zza(zzbnVar));
    }

    @Override // com.google.android.gms.internal.clearcut.zzdo
    public final /* synthetic */ zzdp zzbc() {
        zza zzaVar = (zza) zza(zzg.zzkh, (Object) null, (Object) null);
        zzaVar.zza((zza) this);
        return zzaVar;
    }

    @Override // com.google.android.gms.internal.clearcut.zzdo
    public final /* synthetic */ zzdp zzbd() {
        return (zza) zza(zzg.zzkh, (Object) null, (Object) null);
    }

    @Override // com.google.android.gms.internal.clearcut.zzdq
    public final /* synthetic */ zzdo zzbe() {
        return (zzcg) zza(zzg.zzki, (Object) null, (Object) null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.android.gms.internal.clearcut.zzas
    public final void zzf(int i) {
        this.zzjq = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.android.gms.internal.clearcut.zzas
    public final int zzs() {
        return this.zzjq;
    }
}
