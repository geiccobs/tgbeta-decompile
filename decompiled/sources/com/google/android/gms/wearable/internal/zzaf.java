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
import com.google.android.gms.common.internal.Asserts;
import com.google.android.gms.common.internal.PendingResultUtil;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import java.util.Map;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzaf extends CapabilityClient {
    public static final /* synthetic */ int zza = 0;
    private final CapabilityApi zzb = new zzz();

    public zzaf(Activity activity, GoogleApi.Settings settings) {
        super(activity, settings);
    }

    private final Task<Void> zza(ListenerHolder<CapabilityClient.OnCapabilityChangedListener> listenerHolder, CapabilityClient.OnCapabilityChangedListener onCapabilityChangedListener, IntentFilter[] intentFilterArr) {
        return doRegisterEventListener(RegistrationMethods.builder().withHolder(listenerHolder).register(new RemoteCall(onCapabilityChangedListener, listenerHolder, intentFilterArr) { // from class: com.google.android.gms.wearable.internal.zzac
            private final CapabilityClient.OnCapabilityChangedListener zza;
            private final ListenerHolder zzb;
            private final IntentFilter[] zzc;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = onCapabilityChangedListener;
                this.zzb = listenerHolder;
                this.zzc = intentFilterArr;
            }

            @Override // com.google.android.gms.common.api.internal.RemoteCall
            public final void accept(Object obj, Object obj2) {
                CapabilityClient.OnCapabilityChangedListener onCapabilityChangedListener2 = this.zza;
                ListenerHolder<? extends CapabilityApi.CapabilityListener> listenerHolder2 = this.zzb;
                IntentFilter[] intentFilterArr2 = this.zzc;
                int i = zzaf.zza;
                ((zzhv) obj).zzv(new zzgt((TaskCompletionSource) obj2), onCapabilityChangedListener2, listenerHolder2, intentFilterArr2);
            }
        }).unregister(new RemoteCall(onCapabilityChangedListener) { // from class: com.google.android.gms.wearable.internal.zzad
            private final CapabilityClient.OnCapabilityChangedListener zza;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = onCapabilityChangedListener;
            }

            @Override // com.google.android.gms.common.api.internal.RemoteCall
            public final void accept(Object obj, Object obj2) {
                CapabilityClient.OnCapabilityChangedListener onCapabilityChangedListener2 = this.zza;
                int i = zzaf.zza;
                ((zzhv) obj).zzz(new zzgs((TaskCompletionSource) obj2), onCapabilityChangedListener2);
            }
        }).setMethodKey(24013).build());
    }

    @Override // com.google.android.gms.wearable.CapabilityClient
    public final Task<Void> addListener(CapabilityClient.OnCapabilityChangedListener onCapabilityChangedListener, Uri uri, int i) {
        boolean z;
        Asserts.checkNotNull(onCapabilityChangedListener, "listener must not be null");
        Asserts.checkNotNull(uri, "uri must not be null");
        if (i == 0) {
            z = true;
        } else if (i == 1) {
            i = 1;
            z = true;
        } else {
            z = false;
        }
        Preconditions.checkArgument(z, "invalid filter type");
        return zza(ListenerHolders.createListenerHolder(onCapabilityChangedListener, getLooper(), "CapabilityListener"), onCapabilityChangedListener, new IntentFilter[]{zzgv.zzb("com.google.android.gms.wearable.CAPABILITY_CHANGED", uri, i)});
    }

    @Override // com.google.android.gms.wearable.CapabilityClient
    public final Task<Void> addLocalCapability(String str) {
        Asserts.checkNotNull(str, "capability must not be null");
        CapabilityApi capabilityApi = this.zzb;
        GoogleApiClient asGoogleApiClient = asGoogleApiClient();
        return PendingResultUtil.toVoidTask(asGoogleApiClient.enqueue(new zzq((zzz) capabilityApi, asGoogleApiClient, str)));
    }

    @Override // com.google.android.gms.wearable.CapabilityClient
    public final Task<Map<String, CapabilityInfo>> getAllCapabilities(int i) {
        CapabilityApi capabilityApi = this.zzb;
        GoogleApiClient asGoogleApiClient = asGoogleApiClient();
        boolean z = true;
        if (i != 0) {
            if (i == 1) {
                i = 1;
            } else {
                z = false;
            }
        }
        Preconditions.checkArgument(z);
        return PendingResultUtil.toTask(asGoogleApiClient.enqueue(new zzp((zzz) capabilityApi, asGoogleApiClient, i)), zzab.zza);
    }

    @Override // com.google.android.gms.wearable.CapabilityClient
    public final Task<CapabilityInfo> getCapability(String str, int i) {
        Asserts.checkNotNull(str, "capability must not be null");
        CapabilityApi capabilityApi = this.zzb;
        GoogleApiClient asGoogleApiClient = asGoogleApiClient();
        boolean z = true;
        if (i != 0) {
            if (i == 1) {
                i = 1;
            } else {
                z = false;
            }
        }
        Preconditions.checkArgument(z);
        return PendingResultUtil.toTask(asGoogleApiClient.enqueue(new zzo((zzz) capabilityApi, asGoogleApiClient, str, i)), zzaa.zza);
    }

    @Override // com.google.android.gms.wearable.CapabilityClient
    public final Task<Boolean> removeListener(CapabilityClient.OnCapabilityChangedListener onCapabilityChangedListener) {
        Asserts.checkNotNull(onCapabilityChangedListener, "listener must not be null");
        return doUnregisterEventListener((ListenerHolder.ListenerKey) Preconditions.checkNotNull(ListenerHolders.createListenerHolder(onCapabilityChangedListener, getLooper(), "CapabilityListener").getListenerKey(), "Key must not be null"), 24003);
    }

    @Override // com.google.android.gms.wearable.CapabilityClient
    public final Task<Void> removeLocalCapability(String str) {
        Asserts.checkNotNull(str, "capability must not be null");
        CapabilityApi capabilityApi = this.zzb;
        GoogleApiClient asGoogleApiClient = asGoogleApiClient();
        return PendingResultUtil.toVoidTask(asGoogleApiClient.enqueue(new zzr((zzz) capabilityApi, asGoogleApiClient, str)));
    }

    public zzaf(Context context, GoogleApi.Settings settings) {
        super(context, settings);
    }

    @Override // com.google.android.gms.wearable.CapabilityClient
    public final Task<Boolean> removeListener(CapabilityClient.OnCapabilityChangedListener onCapabilityChangedListener, String str) {
        Asserts.checkNotNull(onCapabilityChangedListener, "listener must not be null");
        Asserts.checkNotNull(str, "capability must not be null");
        if (!str.startsWith("/")) {
            String valueOf = String.valueOf(str);
            if (valueOf.length() != 0) {
                str = "/".concat(valueOf);
            } else {
                str = new String("/");
            }
        }
        Looper looper = getLooper();
        String valueOf2 = String.valueOf(str);
        return doUnregisterEventListener((ListenerHolder.ListenerKey) Preconditions.checkNotNull(ListenerHolders.createListenerHolder(onCapabilityChangedListener, looper, valueOf2.length() != 0 ? "CapabilityListener:".concat(valueOf2) : new String("CapabilityListener:")).getListenerKey(), "Key must not be null"), 24003);
    }

    @Override // com.google.android.gms.wearable.CapabilityClient
    public final Task<Void> addListener(CapabilityClient.OnCapabilityChangedListener onCapabilityChangedListener, String str) {
        Asserts.checkNotNull(onCapabilityChangedListener, "listener must not be null");
        Asserts.checkNotNull(str, "capability must not be null");
        IntentFilter zza2 = zzgv.zza("com.google.android.gms.wearable.CAPABILITY_CHANGED");
        if (!str.startsWith("/")) {
            String valueOf = String.valueOf(str);
            if (valueOf.length() != 0) {
                str = "/".concat(valueOf);
            } else {
                str = new String("/");
            }
        }
        zza2.addDataPath(str, 0);
        IntentFilter[] intentFilterArr = {zza2};
        Looper looper = getLooper();
        String valueOf2 = String.valueOf(str);
        return zza(ListenerHolders.createListenerHolder(onCapabilityChangedListener, looper, valueOf2.length() != 0 ? "CapabilityListener:".concat(valueOf2) : new String("CapabilityListener:")), new zzae(onCapabilityChangedListener, str), intentFilterArr);
    }
}
