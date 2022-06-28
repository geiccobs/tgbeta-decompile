package com.google.android.gms.clearcut;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResults;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.common.util.DefaultClock;
import com.google.android.gms.internal.clearcut.zzaa;
import com.google.android.gms.internal.clearcut.zzge;
import com.google.android.gms.internal.clearcut.zzha;
import com.google.android.gms.internal.clearcut.zzj;
import com.google.android.gms.internal.clearcut.zzp;
import com.google.android.gms.internal.clearcut.zzr;
import com.google.android.gms.phenotype.ExperimentTokens;
import java.util.ArrayList;
import java.util.TimeZone;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public final class ClearcutLogger {
    @Deprecated
    public static final Api<Api.ApiOptions.NoOptions> API;
    private static final Api.AbstractClientBuilder<zzj, Api.ApiOptions.NoOptions> CLIENT_BUILDER;
    private static final Api.ClientKey<zzj> CLIENT_KEY;
    private static final ExperimentTokens[] zze = new ExperimentTokens[0];
    private static final String[] zzf = new String[0];
    private static final byte[][] zzg = new byte[0];
    private final String packageName;
    private final Context zzh;
    private final int zzi;
    private String zzj;
    private int zzk;
    private String zzl;
    private String zzm;
    private final boolean zzn;
    private zzge.zzv.zzb zzo;
    private final com.google.android.gms.clearcut.zzb zzp;
    private final Clock zzq;
    private zzc zzr;
    private final zza zzs;

    /* loaded from: classes3.dex */
    public class LogEventBuilder {
        private final zzha zzaa;
        private boolean zzab;
        private String zzj;
        private int zzk;
        private String zzl;
        private String zzm;
        private zzge.zzv.zzb zzo;
        private final zzb zzt;
        private ArrayList<Integer> zzu;
        private ArrayList<String> zzv;
        private ArrayList<Integer> zzw;
        private ArrayList<ExperimentTokens> zzx;
        private ArrayList<byte[]> zzy;
        private boolean zzz;

        private LogEventBuilder(ClearcutLogger clearcutLogger, byte[] bArr) {
            this(bArr, (zzb) null);
        }

        private LogEventBuilder(byte[] bArr, zzb zzbVar) {
            ClearcutLogger.this = r4;
            this.zzk = r4.zzk;
            this.zzj = r4.zzj;
            this.zzl = r4.zzl;
            this.zzm = null;
            this.zzo = r4.zzo;
            this.zzu = null;
            this.zzv = null;
            this.zzw = null;
            this.zzx = null;
            this.zzy = null;
            this.zzz = true;
            zzha zzhaVar = new zzha();
            this.zzaa = zzhaVar;
            this.zzab = false;
            this.zzl = r4.zzl;
            this.zzm = null;
            zzhaVar.zzbkc = zzaa.zze(r4.zzh);
            zzhaVar.zzbjf = r4.zzq.currentTimeMillis();
            zzhaVar.zzbjg = r4.zzq.elapsedRealtime();
            zzc unused = r4.zzr;
            zzhaVar.zzbju = TimeZone.getDefault().getOffset(zzhaVar.zzbjf) / 1000;
            if (bArr != null) {
                zzhaVar.zzbjp = bArr;
            }
            this.zzt = null;
        }

        /* synthetic */ LogEventBuilder(ClearcutLogger clearcutLogger, byte[] bArr, com.google.android.gms.clearcut.zza zzaVar) {
            this(clearcutLogger, bArr);
        }

        public void log() {
            if (!this.zzab) {
                this.zzab = true;
                zze zzeVar = new zze(new zzr(ClearcutLogger.this.packageName, ClearcutLogger.this.zzi, this.zzk, this.zzj, this.zzl, this.zzm, ClearcutLogger.this.zzn, this.zzo), this.zzaa, null, null, ClearcutLogger.zza((ArrayList<Integer>) null), null, ClearcutLogger.zza((ArrayList<Integer>) null), null, null, this.zzz);
                if (ClearcutLogger.this.zzs.zza(zzeVar)) {
                    ClearcutLogger.this.zzp.zzb(zzeVar);
                    return;
                } else {
                    PendingResults.immediatePendingResult(Status.RESULT_SUCCESS, (GoogleApiClient) null);
                    return;
                }
            }
            throw new IllegalStateException("do not reuse LogEventBuilder");
        }

        public LogEventBuilder setEventCode(int i) {
            this.zzaa.zzbji = i;
            return this;
        }
    }

    /* loaded from: classes3.dex */
    public interface zza {
        boolean zza(zze zzeVar);
    }

    /* loaded from: classes3.dex */
    public interface zzb {
        byte[] zza();
    }

    /* loaded from: classes3.dex */
    public static class zzc {
    }

    static {
        Api.ClientKey<zzj> clientKey = new Api.ClientKey<>();
        CLIENT_KEY = clientKey;
        com.google.android.gms.clearcut.zza zzaVar = new com.google.android.gms.clearcut.zza();
        CLIENT_BUILDER = zzaVar;
        API = new Api<>("ClearcutLogger.API", zzaVar, clientKey);
    }

    private ClearcutLogger(Context context, int i, String str, String str2, String str3, boolean z, com.google.android.gms.clearcut.zzb zzbVar, Clock clock, zzc zzcVar, zza zzaVar) {
        this.zzk = -1;
        this.zzo = zzge.zzv.zzb.DEFAULT;
        this.zzh = context;
        this.packageName = context.getPackageName();
        this.zzi = zza(context);
        this.zzk = -1;
        this.zzj = str;
        this.zzl = str2;
        this.zzm = null;
        this.zzn = z;
        this.zzp = zzbVar;
        this.zzq = clock;
        this.zzr = new zzc();
        this.zzo = zzge.zzv.zzb.DEFAULT;
        this.zzs = zzaVar;
        if (z) {
            Preconditions.checkArgument(str2 == null, "can't be anonymous with an upload account");
        }
    }

    public ClearcutLogger(Context context, String str, @Nullable String str2) {
        this(context, -1, str, str2, null, false, com.google.android.gms.internal.clearcut.zze.zzb(context), DefaultClock.getInstance(), null, new zzp(context));
    }

    public static ClearcutLogger anonymousLogger(Context context, String str) {
        return new ClearcutLogger(context, -1, str, null, null, true, com.google.android.gms.internal.clearcut.zze.zzb(context), DefaultClock.getInstance(), null, new zzp(context));
    }

    private static int zza(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.wtf("ClearcutLogger", "This can't happen.", e);
            return 0;
        }
    }

    public static int[] zza(ArrayList<Integer> arrayList) {
        if (arrayList == null) {
            return null;
        }
        int[] iArr = new int[arrayList.size()];
        ArrayList<Integer> arrayList2 = arrayList;
        int size = arrayList2.size();
        int i = 0;
        int i2 = 0;
        while (i < size) {
            Integer num = arrayList2.get(i);
            i++;
            iArr[i2] = num.intValue();
            i2++;
        }
        return iArr;
    }

    public final LogEventBuilder newEvent(@Nullable byte[] bArr) {
        return new LogEventBuilder(this, bArr, (com.google.android.gms.clearcut.zza) null);
    }
}
