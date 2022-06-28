package com.google.android.gms.wearable.internal;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import androidx.core.util.Preconditions;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.ListenerHolder;
import com.google.android.gms.common.api.internal.ListenerHolders;
import com.google.android.gms.common.api.internal.RegistrationMethods;
import com.google.android.gms.common.api.internal.RemoteCall;
import com.google.android.gms.common.api.internal.TaskApiCall;
import com.google.android.gms.common.internal.PendingResultUtil;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageOptions;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzfi extends MessageClient {
    public static final /* synthetic */ int zzb = 0;
    final zzfc zza = new zzfc();

    public zzfi(Activity activity, GoogleApi.Settings settings) {
        super(activity, settings);
    }

    private final Task<Void> zza(MessageClient.OnMessageReceivedListener onMessageReceivedListener, IntentFilter[] intentFilterArr) {
        ListenerHolder createListenerHolder = ListenerHolders.createListenerHolder(onMessageReceivedListener, getLooper(), "MessageListener");
        return doRegisterEventListener(RegistrationMethods.builder().withHolder(createListenerHolder).register(new RemoteCall(onMessageReceivedListener, createListenerHolder, intentFilterArr) { // from class: com.google.android.gms.wearable.internal.zzff
            private final MessageClient.OnMessageReceivedListener zza;
            private final ListenerHolder zzb;
            private final IntentFilter[] zzc;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = onMessageReceivedListener;
                this.zzb = createListenerHolder;
                this.zzc = intentFilterArr;
            }

            @Override // com.google.android.gms.common.api.internal.RemoteCall
            public final void accept(Object obj, Object obj2) {
                MessageClient.OnMessageReceivedListener onMessageReceivedListener2 = this.zza;
                ListenerHolder<? extends MessageApi.MessageListener> listenerHolder = this.zzb;
                IntentFilter[] intentFilterArr2 = this.zzc;
                int i = zzfi.zzb;
                ((zzhv) obj).zzu(new zzgt((TaskCompletionSource) obj2), onMessageReceivedListener2, listenerHolder, intentFilterArr2);
            }
        }).unregister(new RemoteCall(onMessageReceivedListener) { // from class: com.google.android.gms.wearable.internal.zzfg
            private final MessageClient.OnMessageReceivedListener zza;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = onMessageReceivedListener;
            }

            @Override // com.google.android.gms.common.api.internal.RemoteCall
            public final void accept(Object obj, Object obj2) {
                MessageClient.OnMessageReceivedListener onMessageReceivedListener2 = this.zza;
                int i = zzfi.zzb;
                ((zzhv) obj).zzy(new zzgs((TaskCompletionSource) obj2), onMessageReceivedListener2);
            }
        }).setMethodKey(24016).build());
    }

    @Override // com.google.android.gms.wearable.MessageClient
    public final Task<Void> addListener(MessageClient.OnMessageReceivedListener onMessageReceivedListener) {
        return zza(onMessageReceivedListener, new IntentFilter[]{zzgv.zza("com.google.android.gms.wearable.MESSAGE_RECEIVED")});
    }

    @Override // com.google.android.gms.wearable.MessageClient
    public final Task<Boolean> removeListener(MessageClient.OnMessageReceivedListener onMessageReceivedListener) {
        return doUnregisterEventListener((ListenerHolder.ListenerKey) Preconditions.checkNotNull(ListenerHolders.createListenerHolder(onMessageReceivedListener, getLooper(), "MessageListener").getListenerKey(), "Key must not be null"), 24007);
    }

    @Override // com.google.android.gms.wearable.MessageClient
    public final Task<Integer> sendMessage(String str, String str2, byte[] bArr) {
        zzfc zzfcVar = this.zza;
        GoogleApiClient asGoogleApiClient = asGoogleApiClient();
        return PendingResultUtil.toTask(asGoogleApiClient.enqueue(new zzey(zzfcVar, asGoogleApiClient, str, str2, bArr)), zzfd.zza);
    }

    public zzfi(Context context, GoogleApi.Settings settings) {
        super(context, settings);
    }

    @Override // com.google.android.gms.wearable.MessageClient
    public final Task<Void> addListener(MessageClient.OnMessageReceivedListener onMessageReceivedListener, Uri uri, int i) {
        boolean z;
        Preconditions.checkNotNull(uri, "uri must not be null");
        if (i == 0) {
            z = true;
        } else if (i == 1) {
            i = 1;
            z = true;
        } else {
            z = false;
        }
        com.google.android.gms.common.internal.Preconditions.checkArgument(z, "invalid filter type");
        return zza(onMessageReceivedListener, new IntentFilter[]{zzgv.zzb("com.google.android.gms.wearable.MESSAGE_RECEIVED", uri, i)});
    }

    @Override // com.google.android.gms.wearable.MessageClient
    public final Task<Integer> sendMessage(String str, String str2, byte[] bArr, MessageOptions messageOptions) {
        return doRead(TaskApiCall.builder().run(new RemoteCall(this, str, str2, bArr, messageOptions) { // from class: com.google.android.gms.wearable.internal.zzfe
            private final zzfi zza;
            private final String zzb;
            private final String zzc;
            private final byte[] zzd;
            private final MessageOptions zze;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = this;
                this.zzb = str;
                this.zzc = str2;
                this.zzd = bArr;
                this.zze = messageOptions;
            }

            @Override // com.google.android.gms.common.api.internal.RemoteCall
            public final void accept(Object obj, Object obj2) {
                zzfi zzfiVar = this.zza;
                ((zzeu) ((zzhv) obj).getService()).zzj(new zzhq(new zzfh(zzfiVar, (TaskCompletionSource) obj2)), this.zzb, this.zzc, this.zzd, this.zze);
            }
        }).setMethodKey(24020).setFeatures(com.google.android.gms.wearable.zze.zza).build());
    }
}
