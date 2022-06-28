package com.google.android.gms.wearable;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import androidx.core.util.Preconditions;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.wearable.internal.zzaf;
import com.google.android.gms.wearable.internal.zzam;
import com.google.android.gms.wearable.internal.zzav;
import com.google.android.gms.wearable.internal.zzbw;
import com.google.android.gms.wearable.internal.zzcj;
import com.google.android.gms.wearable.internal.zzcw;
import com.google.android.gms.wearable.internal.zzfc;
import com.google.android.gms.wearable.internal.zzfi;
import com.google.android.gms.wearable.internal.zzfp;
import com.google.android.gms.wearable.internal.zzfv;
import com.google.android.gms.wearable.internal.zzgu;
import com.google.android.gms.wearable.internal.zzhv;
import com.google.android.gms.wearable.internal.zzic;
import com.google.android.gms.wearable.internal.zzz;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public class Wearable {
    @Deprecated
    public static final Api<WearableOptions> API;
    private static final Api.ClientKey<zzhv> zzf;
    private static final Api.AbstractClientBuilder<zzhv, WearableOptions> zzg;
    @Deprecated
    public static final DataApi DataApi = new zzcj();
    @Deprecated
    public static final CapabilityApi CapabilityApi = new zzz();
    @Deprecated
    public static final MessageApi MessageApi = new zzfc();
    @Deprecated
    public static final NodeApi NodeApi = new zzfp();
    @Deprecated
    public static final ChannelApi ChannelApi = new zzam();
    @Deprecated
    public static final com.google.android.gms.wearable.internal.zzk zza = new com.google.android.gms.wearable.internal.zzk();
    @Deprecated
    public static final com.google.android.gms.wearable.internal.zzh zzb = new com.google.android.gms.wearable.internal.zzh();
    @Deprecated
    public static final zzbw zzc = new zzbw();
    @Deprecated
    public static final zzgu zzd = new zzgu();
    @Deprecated
    public static final zzic zze = new zzic();

    /* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
    /* loaded from: classes3.dex */
    public static final class WearableOptions implements Api.ApiOptions.Optional {
        static final WearableOptions zza = new WearableOptions(new Builder());
        private final Looper zzb;

        /* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
        /* loaded from: classes3.dex */
        public static class Builder {
            private Looper zza;

            public WearableOptions build() {
                return new WearableOptions(this, null);
            }

            public Builder setLooper(Looper looper) {
                this.zza = looper;
                return this;
            }
        }

        private WearableOptions(Builder builder) {
            this.zzb = builder.zza;
        }

        /* synthetic */ WearableOptions(Builder builder, zzh zzhVar) {
            this(builder);
        }

        static /* synthetic */ GoogleApi.Settings zza(WearableOptions wearableOptions) {
            if (wearableOptions.zzb != null) {
                return new GoogleApi.Settings.Builder().setLooper(wearableOptions.zzb).build();
            }
            return GoogleApi.Settings.DEFAULT_SETTINGS;
        }

        public boolean equals(Object object) {
            return object instanceof WearableOptions;
        }

        public int hashCode() {
            return Objects.hashCode(getClass());
        }
    }

    static {
        Api.ClientKey<zzhv> clientKey = new Api.ClientKey<>();
        zzf = clientKey;
        zzh zzhVar = new zzh();
        zzg = zzhVar;
        API = new Api<>("Wearable.API", zzhVar, clientKey);
    }

    private Wearable() {
    }

    public static CapabilityClient getCapabilityClient(Activity activity) {
        return new zzaf(activity, GoogleApi.Settings.DEFAULT_SETTINGS);
    }

    public static ChannelClient getChannelClient(Activity activity) {
        return new zzav(activity, GoogleApi.Settings.DEFAULT_SETTINGS);
    }

    public static DataClient getDataClient(Activity activity) {
        return new zzcw(activity, GoogleApi.Settings.DEFAULT_SETTINGS);
    }

    public static MessageClient getMessageClient(Activity activity) {
        return new zzfi(activity, GoogleApi.Settings.DEFAULT_SETTINGS);
    }

    public static NodeClient getNodeClient(Activity activity) {
        return new zzfv(activity, GoogleApi.Settings.DEFAULT_SETTINGS);
    }

    public static CapabilityClient getCapabilityClient(Activity activity, WearableOptions options) {
        Preconditions.checkNotNull(options, "options must not be null");
        return new zzaf(activity, WearableOptions.zza(options));
    }

    public static ChannelClient getChannelClient(Activity activity, WearableOptions options) {
        Preconditions.checkNotNull(options, "options must not be null");
        return new zzav(activity, WearableOptions.zza(options));
    }

    public static DataClient getDataClient(Activity activity, WearableOptions options) {
        Preconditions.checkNotNull(options, "options must not be null");
        return new zzcw(activity, WearableOptions.zza(options));
    }

    public static MessageClient getMessageClient(Activity activity, WearableOptions options) {
        Preconditions.checkNotNull(options, "options must not be null");
        return new zzfi(activity, WearableOptions.zza(options));
    }

    public static NodeClient getNodeClient(Activity activity, WearableOptions options) {
        Preconditions.checkNotNull(options, "options must not be null");
        return new zzfv(activity, WearableOptions.zza(options));
    }

    public static CapabilityClient getCapabilityClient(Context context) {
        return new zzaf(context, GoogleApi.Settings.DEFAULT_SETTINGS);
    }

    public static ChannelClient getChannelClient(Context context) {
        return new zzav(context, GoogleApi.Settings.DEFAULT_SETTINGS);
    }

    public static DataClient getDataClient(Context context) {
        return new zzcw(context, GoogleApi.Settings.DEFAULT_SETTINGS);
    }

    public static MessageClient getMessageClient(Context context) {
        return new zzfi(context, GoogleApi.Settings.DEFAULT_SETTINGS);
    }

    public static NodeClient getNodeClient(Context context) {
        return new zzfv(context, GoogleApi.Settings.DEFAULT_SETTINGS);
    }

    public static CapabilityClient getCapabilityClient(Context context, WearableOptions options) {
        Preconditions.checkNotNull(options, "options must not be null");
        return new zzaf(context, WearableOptions.zza(options));
    }

    public static ChannelClient getChannelClient(Context context, WearableOptions options) {
        Preconditions.checkNotNull(options, "options must not be null");
        return new zzav(context, WearableOptions.zza(options));
    }

    public static DataClient getDataClient(Context context, WearableOptions options) {
        Preconditions.checkNotNull(options, "options must not be null");
        return new zzcw(context, WearableOptions.zza(options));
    }

    public static MessageClient getMessageClient(Context context, WearableOptions options) {
        Preconditions.checkNotNull(options, "options must not be null");
        return new zzfi(context, WearableOptions.zza(options));
    }

    public static NodeClient getNodeClient(Context context, WearableOptions options) {
        Preconditions.checkNotNull(options, "options must not be null");
        return new zzfv(context, WearableOptions.zza(options));
    }
}
