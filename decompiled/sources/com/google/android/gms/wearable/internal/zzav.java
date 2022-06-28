package com.google.android.gms.wearable.internal;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Looper;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.ListenerHolder;
import com.google.android.gms.common.api.internal.ListenerHolders;
import com.google.android.gms.common.api.internal.RegistrationMethods;
import com.google.android.gms.common.api.internal.RemoteCall;
import com.google.android.gms.common.internal.PendingResultUtil;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.ChannelApi;
import com.google.android.gms.wearable.ChannelClient;
import java.io.InputStream;
import java.io.OutputStream;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzav extends ChannelClient {
    public static final /* synthetic */ int zza = 0;
    private final zzam zzb = new zzam();

    public zzav(Activity activity, GoogleApi.Settings settings) {
        super(activity, settings);
    }

    public static /* synthetic */ zzbi zzb(Channel channel) {
        return zzd(channel);
    }

    private static zzbi zzc(ChannelClient.Channel channel) {
        Preconditions.checkNotNull(channel, "channel must not be null");
        return (zzbi) channel;
    }

    public static zzbi zzd(Channel channel) {
        Preconditions.checkNotNull(channel, "channel must not be null");
        return (zzbi) channel;
    }

    @Override // com.google.android.gms.wearable.ChannelClient
    public final Task<Void> close(ChannelClient.Channel channel) {
        zzbi zzc = zzc(channel);
        GoogleApiClient asGoogleApiClient = asGoogleApiClient();
        return PendingResultUtil.toVoidTask(asGoogleApiClient.enqueue(new zzaz(zzc, asGoogleApiClient)));
    }

    @Override // com.google.android.gms.wearable.ChannelClient
    public final Task<InputStream> getInputStream(ChannelClient.Channel channel) {
        zzbi zzc = zzc(channel);
        GoogleApiClient asGoogleApiClient = asGoogleApiClient();
        return PendingResultUtil.toTask(asGoogleApiClient.enqueue(new zzbb(zzc, asGoogleApiClient)), zzaq.zza);
    }

    @Override // com.google.android.gms.wearable.ChannelClient
    public final Task<OutputStream> getOutputStream(ChannelClient.Channel channel) {
        zzbi zzc = zzc(channel);
        GoogleApiClient asGoogleApiClient = asGoogleApiClient();
        return PendingResultUtil.toTask(asGoogleApiClient.enqueue(new zzbc(zzc, asGoogleApiClient)), zzar.zza);
    }

    @Override // com.google.android.gms.wearable.ChannelClient
    public final Task<ChannelClient.Channel> openChannel(String str, String str2) {
        zzam zzamVar = this.zzb;
        GoogleApiClient asGoogleApiClient = asGoogleApiClient();
        Preconditions.checkNotNull(asGoogleApiClient, "client is null");
        Preconditions.checkNotNull(str, "nodeId is null");
        Preconditions.checkNotNull(str2, "path is null");
        return PendingResultUtil.toTask(asGoogleApiClient.enqueue(new zzai(zzamVar, asGoogleApiClient, str, str2)), zzan.zza);
    }

    @Override // com.google.android.gms.wearable.ChannelClient
    public final Task<Void> receiveFile(ChannelClient.Channel channel, Uri uri, boolean z) {
        zzbi zzc = zzc(channel);
        GoogleApiClient asGoogleApiClient = asGoogleApiClient();
        Preconditions.checkNotNull(asGoogleApiClient, "client is null");
        Preconditions.checkNotNull(uri, "uri is null");
        return PendingResultUtil.toVoidTask(asGoogleApiClient.enqueue(new zzbd(zzc, asGoogleApiClient, uri, z)));
    }

    @Override // com.google.android.gms.wearable.ChannelClient
    public final Task<Void> registerChannelCallback(ChannelClient.Channel channel, ChannelClient.ChannelCallback channelCallback) {
        String zza2 = ((zzbi) channel).zza();
        Preconditions.checkNotNull(channelCallback, "listener is null");
        Looper looper = getLooper();
        String valueOf = String.valueOf(zza2);
        ListenerHolder createListenerHolder = ListenerHolders.createListenerHolder(channelCallback, looper, valueOf.length() != 0 ? "ChannelListener:".concat(valueOf) : new String("ChannelListener:"));
        IntentFilter[] intentFilterArr = {zzgv.zza("com.google.android.gms.wearable.CHANNEL_EVENT")};
        zzau zzauVar = new zzau(channelCallback);
        return doRegisterEventListener(RegistrationMethods.builder().withHolder(createListenerHolder).register(new RemoteCall(zzauVar, ListenerHolders.createListenerHolder(zzauVar, getLooper(), "ChannelListener"), zza2, intentFilterArr) { // from class: com.google.android.gms.wearable.internal.zzas
            private final zzau zza;
            private final ListenerHolder zzb;
            private final String zzc;
            private final IntentFilter[] zzd;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = zzauVar;
                this.zzb = r2;
                this.zzc = zza2;
                this.zzd = intentFilterArr;
            }

            @Override // com.google.android.gms.common.api.internal.RemoteCall
            public final void accept(Object obj, Object obj2) {
                zzau zzauVar2 = this.zza;
                ListenerHolder<? extends ChannelApi.ChannelListener> listenerHolder = this.zzb;
                String str = this.zzc;
                IntentFilter[] intentFilterArr2 = this.zzd;
                zzhv zzhvVar = (zzhv) obj;
                int i = zzav.zza;
                zzhvVar.zzw(new zzgt((TaskCompletionSource) obj2), zzauVar2, listenerHolder, str, intentFilterArr2);
            }
        }).unregister(new RemoteCall(zzauVar, zza2) { // from class: com.google.android.gms.wearable.internal.zzat
            private final zzau zza;
            private final String zzb;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = zzauVar;
                this.zzb = zza2;
            }

            @Override // com.google.android.gms.common.api.internal.RemoteCall
            public final void accept(Object obj, Object obj2) {
                zzau zzauVar2 = this.zza;
                String str = this.zzb;
                int i = zzav.zza;
                ((zzhv) obj).zzA(new zzgs((TaskCompletionSource) obj2), zzauVar2, str);
            }
        }).setMethodKey(24014).build());
    }

    @Override // com.google.android.gms.wearable.ChannelClient
    public final Task<Void> sendFile(ChannelClient.Channel channel, Uri uri) {
        return PendingResultUtil.toVoidTask(zzc(channel).sendFile(asGoogleApiClient(), uri, 0L, -1L));
    }

    @Override // com.google.android.gms.wearable.ChannelClient
    public final Task<Boolean> unregisterChannelCallback(ChannelClient.Channel channel, ChannelClient.ChannelCallback channelCallback) {
        String zza2 = zzc(channel).zza();
        Looper looper = getLooper();
        String valueOf = String.valueOf(zza2);
        return doUnregisterEventListener((ListenerHolder.ListenerKey) Preconditions.checkNotNull(ListenerHolders.createListenerHolder(channelCallback, looper, valueOf.length() != 0 ? "ChannelListener:".concat(valueOf) : new String("ChannelListener:")).getListenerKey(), "Key must not be null"), 24004);
    }

    public zzav(Context context, GoogleApi.Settings settings) {
        super(context, settings);
    }

    @Override // com.google.android.gms.wearable.ChannelClient
    public final Task<Void> sendFile(ChannelClient.Channel channel, Uri uri, long j, long j2) {
        return PendingResultUtil.toVoidTask(zzc(channel).sendFile(asGoogleApiClient(), uri, j, j2));
    }

    @Override // com.google.android.gms.wearable.ChannelClient
    public final Task<Void> close(ChannelClient.Channel channel, int i) {
        zzbi zzc = zzc(channel);
        GoogleApiClient asGoogleApiClient = asGoogleApiClient();
        return PendingResultUtil.toVoidTask(asGoogleApiClient.enqueue(new zzba(zzc, asGoogleApiClient, i)));
    }

    @Override // com.google.android.gms.wearable.ChannelClient
    public final Task<Boolean> unregisterChannelCallback(ChannelClient.ChannelCallback channelCallback) {
        return doUnregisterEventListener((ListenerHolder.ListenerKey) Preconditions.checkNotNull(ListenerHolders.createListenerHolder(channelCallback, getLooper(), "ChannelListener").getListenerKey(), "Key must not be null"), 24004);
    }

    @Override // com.google.android.gms.wearable.ChannelClient
    public final Task<Void> registerChannelCallback(ChannelClient.ChannelCallback channelCallback) {
        Preconditions.checkNotNull(channelCallback, "listener is null");
        ListenerHolder createListenerHolder = ListenerHolders.createListenerHolder(channelCallback, getLooper(), "ChannelListener");
        IntentFilter[] intentFilterArr = {zzgv.zza("com.google.android.gms.wearable.CHANNEL_EVENT")};
        zzau zzauVar = new zzau(channelCallback);
        return doRegisterEventListener(RegistrationMethods.builder().withHolder(createListenerHolder).register(new RemoteCall(zzauVar, ListenerHolders.createListenerHolder(zzauVar, getLooper(), "ChannelListener"), intentFilterArr) { // from class: com.google.android.gms.wearable.internal.zzao
            private final zzau zza;
            private final ListenerHolder zzb;
            private final IntentFilter[] zzc;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = zzauVar;
                this.zzb = r2;
                this.zzc = intentFilterArr;
            }

            @Override // com.google.android.gms.common.api.internal.RemoteCall
            public final void accept(Object obj, Object obj2) {
                zzau zzauVar2 = this.zza;
                ListenerHolder<? extends ChannelApi.ChannelListener> listenerHolder = this.zzb;
                IntentFilter[] intentFilterArr2 = this.zzc;
                zzhv zzhvVar = (zzhv) obj;
                int i = zzav.zza;
                zzhvVar.zzw(new zzgt((TaskCompletionSource) obj2), zzauVar2, listenerHolder, null, intentFilterArr2);
            }
        }).unregister(new RemoteCall(zzauVar) { // from class: com.google.android.gms.wearable.internal.zzap
            private final zzau zza;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = zzauVar;
            }

            @Override // com.google.android.gms.common.api.internal.RemoteCall
            public final void accept(Object obj, Object obj2) {
                zzau zzauVar2 = this.zza;
                int i = zzav.zza;
                ((zzhv) obj).zzA(new zzgs((TaskCompletionSource) obj2), zzauVar2, null);
            }
        }).setMethodKey(24014).build());
    }
}
