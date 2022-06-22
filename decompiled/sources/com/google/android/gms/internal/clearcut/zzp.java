package com.google.android.gms.internal.clearcut;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.clearcut.ClearcutLogger;
import com.google.android.gms.common.wrappers.Wrappers;
import com.google.android.gms.internal.clearcut.zzgw$zza;
import com.google.android.gms.phenotype.Phenotype;
import j$.util.concurrent.ConcurrentHashMap;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
/* loaded from: classes.dex */
public final class zzp implements ClearcutLogger.zza {
    private static final zzao zzaq;
    private static final zzae<Boolean> zzaw;
    private final Context zzh;
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final zzao zzar = new zzao(Phenotype.getContentProviderUri("com.google.android.gms.clearcut.public")).zzc("gms:playlog:service:sampling_").zzd("LogSampling__");
    private static final ConcurrentHashMap<String, zzae<zzgw$zza>> zzas = new ConcurrentHashMap<>();
    private static final HashMap<String, zzae<String>> zzat = new HashMap<>();
    private static Boolean zzau = null;
    private static Long zzav = null;

    static {
        zzao zzd = new zzao(Phenotype.getContentProviderUri("com.google.android.gms.clearcut.public")).zzc("gms:playlog:service:samplingrules_").zzd("LogSamplingRules__");
        zzaq = zzd;
        zzaw = zzd.zzc("enable_log_sampling_rules", false);
    }

    public zzp(Context context) {
        this.zzh = context;
        if (context != null) {
            zzae.maybeInit(context);
        }
    }

    private static long zza(String str, long j) {
        if (str == null || str.isEmpty()) {
            return zzk.zza(ByteBuffer.allocate(8).putLong(j).array());
        }
        byte[] bytes = str.getBytes(UTF_8);
        ByteBuffer allocate = ByteBuffer.allocate(bytes.length + 8);
        allocate.put(bytes);
        allocate.putLong(j);
        return zzk.zza(allocate.array());
    }

    private static zzgw$zza.zzb zza(String str) {
        String str2;
        if (str == null) {
            return null;
        }
        int indexOf = str.indexOf(44);
        int i = 0;
        if (indexOf >= 0) {
            i = indexOf + 1;
            str2 = str.substring(0, indexOf);
        } else {
            str2 = "";
        }
        int indexOf2 = str.indexOf(47, i);
        if (indexOf2 <= 0) {
            Log.e("LogSamplerImpl", str.length() != 0 ? "Failed to parse the rule: ".concat(str) : new String("Failed to parse the rule: "));
            return null;
        }
        try {
            long parseLong = Long.parseLong(str.substring(i, indexOf2));
            long parseLong2 = Long.parseLong(str.substring(indexOf2 + 1));
            if (parseLong >= 0 && parseLong2 >= 0) {
                return zzgw$zza.zzb.zzfz().zzn(str2).zzr(parseLong).zzs(parseLong2).zzbh();
            }
            StringBuilder sb = new StringBuilder(72);
            sb.append("negative values not supported: ");
            sb.append(parseLong);
            sb.append("/");
            sb.append(parseLong2);
            Log.e("LogSamplerImpl", sb.toString());
            return null;
        } catch (NumberFormatException e) {
            Log.e("LogSamplerImpl", str.length() != 0 ? "parseLong() failed while parsing: ".concat(str) : new String("parseLong() failed while parsing: "), e);
            return null;
        }
    }

    private static boolean zzb(long j, long j2, long j3) {
        if (j2 < 0 || j3 <= 0) {
            return true;
        }
        return ((j > 0L ? 1 : (j == 0L ? 0 : -1)) >= 0 ? j % j3 : (((Long.MAX_VALUE % j3) + 1) + ((j & Long.MAX_VALUE) % j3)) % j3) < j2;
    }

    private static boolean zzc(Context context) {
        if (zzau == null) {
            zzau = Boolean.valueOf(Wrappers.packageManager(context).checkCallingOrSelfPermission("com.google.android.providers.gsf.permission.READ_GSERVICES") == 0);
        }
        return zzau.booleanValue();
    }

    private static long zzd(Context context) {
        if (zzav == null) {
            long j = 0;
            if (context == null) {
                return 0L;
            }
            if (zzc(context)) {
                j = zzy.getLong(context.getContentResolver(), "android_id", 0L);
            }
            zzav = Long.valueOf(j);
        }
        return zzav.longValue();
    }

    @Override // com.google.android.gms.clearcut.ClearcutLogger.zza
    public final boolean zza(com.google.android.gms.clearcut.zze zzeVar) {
        List<zzgw$zza.zzb> list;
        zzae<zzgw$zza> putIfAbsent;
        zzr zzrVar = zzeVar.zzag;
        String str = zzrVar.zzj;
        int i = zzrVar.zzk;
        zzha zzhaVar = zzeVar.zzaa;
        int i2 = zzhaVar != null ? zzhaVar.zzbji : 0;
        String str2 = null;
        if (!zzaw.get().booleanValue()) {
            if (str == null || str.isEmpty()) {
                str = i >= 0 ? String.valueOf(i) : null;
            }
            if (str == null) {
                return true;
            }
            Context context = this.zzh;
            if (context != null && zzc(context)) {
                HashMap<String, zzae<String>> hashMap = zzat;
                zzae<String> zzaeVar = hashMap.get(str);
                if (zzaeVar == null) {
                    zzaeVar = zzar.zza(str, null);
                    hashMap.put(str, zzaeVar);
                }
                str2 = zzaeVar.get();
            }
            zzgw$zza.zzb zza = zza(str2);
            if (zza == null) {
                return true;
            }
            return zzb(zza(zza.zzfw(), zzd(this.zzh)), zza.zzfx(), zza.zzfy());
        }
        if (str == null || str.isEmpty()) {
            str = i >= 0 ? String.valueOf(i) : null;
        }
        if (str == null) {
            return true;
        }
        if (this.zzh == null) {
            list = Collections.emptyList();
        } else {
            ConcurrentHashMap<String, zzae<zzgw$zza>> concurrentHashMap = zzas;
            zzae<zzgw$zza> zzaeVar2 = concurrentHashMap.get(str);
            if (zzaeVar2 == null && (putIfAbsent = concurrentHashMap.putIfAbsent(str, (zzaeVar2 = zzaq.zza(str, zzgw$zza.zzft(), zzq.zzax)))) != null) {
                zzaeVar2 = putIfAbsent;
            }
            list = zzaeVar2.get().zzfs();
        }
        for (zzgw$zza.zzb zzbVar : list) {
            if (!zzbVar.zzfv() || zzbVar.getEventCode() == 0 || zzbVar.getEventCode() == i2) {
                if (!zzb(zza(zzbVar.zzfw(), zzd(this.zzh)), zzbVar.zzfx(), zzbVar.zzfy())) {
                    return false;
                }
            }
        }
        return true;
    }
}
