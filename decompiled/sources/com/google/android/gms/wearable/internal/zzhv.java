package com.google.android.gms.wearable.internal;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.BaseImplementation;
import com.google.android.gms.common.api.internal.ListenerHolder;
import com.google.android.gms.common.internal.BaseGmsClient;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.GmsClient;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.ChannelApi;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.WearableStatusCodes;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes.dex */
public final class zzhv extends GmsClient<zzeu> {
    private final ExecutorService zze;
    private final zzex zzf = new zzex();
    private final zzex zzg = new zzex();
    private final zzex<ChannelApi.ChannelListener> zzh = new zzex<>();
    private final zzex<DataApi.DataListener> zzi = new zzex<>();
    private final zzex<MessageApi.MessageListener> zzj = new zzex<>();
    private final zzex zzk = new zzex();
    private final zzex zzl = new zzex();
    private final zzex zzm = new zzex();
    private final zzex<CapabilityApi.CapabilityListener> zzn = new zzex<>();
    private final zzib zzo;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public zzhv(Context context, Looper looper, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener, ClientSettings clientSettings) {
        super(context, looper, 14, clientSettings, connectionCallbacks, onConnectionFailedListener);
        com.google.android.gms.internal.wearable.zzh.zza();
        ExecutorService unconfigurableExecutorService = Executors.unconfigurableExecutorService(Executors.newCachedThreadPool());
        zzib zza = zzib.zza(context);
        this.zze = (ExecutorService) Preconditions.checkNotNull(unconfigurableExecutorService);
        this.zzo = zza;
    }

    @Override // com.google.android.gms.common.internal.BaseGmsClient, com.google.android.gms.common.api.Api.Client
    public final void connect(BaseGmsClient.ConnectionProgressReportCallbacks connectionProgressReportCallbacks) {
        int i;
        if (!requiresGooglePlayServices()) {
            try {
                Bundle bundle = getContext().getPackageManager().getApplicationInfo("com.google.android.wearable.app.cn", 128).metaData;
                if (bundle != null) {
                    i = bundle.getInt("com.google.android.wearable.api.version", 0);
                } else {
                    i = 0;
                }
                if (i < 8600000) {
                    StringBuilder sb = new StringBuilder(82);
                    sb.append("The Wear OS app is out of date. Requires API version 8600000 but found ");
                    sb.append(i);
                    Log.w("WearableClient", sb.toString());
                    Context context = getContext();
                    Context context2 = getContext();
                    Intent intent = new Intent("com.google.android.wearable.app.cn.UPDATE_ANDROID_WEAR").setPackage("com.google.android.wearable.app.cn");
                    if (context2.getPackageManager().resolveActivity(intent, 65536) == null) {
                        intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details").buildUpon().appendQueryParameter("id", "com.google.android.wearable.app.cn").build());
                    }
                    triggerNotAvailable(connectionProgressReportCallbacks, 6, com.google.android.gms.internal.wearable.zzd.zza(context, 0, intent, com.google.android.gms.internal.wearable.zzd.zza));
                    return;
                }
            } catch (PackageManager.NameNotFoundException e) {
                triggerNotAvailable(connectionProgressReportCallbacks, 16, null);
                return;
            }
        }
        super.connect(connectionProgressReportCallbacks);
    }

    @Override // com.google.android.gms.common.internal.BaseGmsClient
    protected final /* bridge */ /* synthetic */ IInterface createServiceInterface(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.wearable.internal.IWearableService");
        if (queryLocalInterface instanceof zzeu) {
            return (zzeu) queryLocalInterface;
        }
        return new zzeu(iBinder);
    }

    @Override // com.google.android.gms.common.internal.BaseGmsClient
    public final Feature[] getApiFeatures() {
        return com.google.android.gms.wearable.zze.zze;
    }

    @Override // com.google.android.gms.common.internal.BaseGmsClient, com.google.android.gms.common.api.Api.Client
    public final int getMinApkVersion() {
        return 8600000;
    }

    @Override // com.google.android.gms.common.internal.BaseGmsClient
    protected final String getServiceDescriptor() {
        return "com.google.android.gms.wearable.internal.IWearableService";
    }

    @Override // com.google.android.gms.common.internal.BaseGmsClient
    protected final String getStartServiceAction() {
        return "com.google.android.gms.wearable.BIND";
    }

    @Override // com.google.android.gms.common.internal.BaseGmsClient
    protected final String getStartServicePackage() {
        return this.zzo.zzb("com.google.android.wearable.app.cn") ? "com.google.android.wearable.app.cn" : "com.google.android.gms";
    }

    @Override // com.google.android.gms.common.internal.BaseGmsClient
    public final void onPostInitHandler(int i, IBinder iBinder, Bundle bundle, int i2) {
        if (Log.isLoggable("WearableClient", 2)) {
            StringBuilder sb = new StringBuilder(41);
            sb.append("onPostInitHandler: statusCode ");
            sb.append(i);
            Log.v("WearableClient", sb.toString());
        }
        if (i == 0) {
            this.zzf.zza(iBinder);
            this.zzg.zza(iBinder);
            this.zzh.zza(iBinder);
            this.zzi.zza(iBinder);
            this.zzj.zza(iBinder);
            this.zzk.zza(iBinder);
            this.zzl.zza(iBinder);
            this.zzm.zza(iBinder);
            this.zzn.zza(iBinder);
            i = 0;
        }
        super.onPostInitHandler(i, iBinder, bundle, i2);
    }

    @Override // com.google.android.gms.common.internal.BaseGmsClient, com.google.android.gms.common.api.Api.Client
    public final boolean requiresGooglePlayServices() {
        return !this.zzo.zzb("com.google.android.wearable.app.cn");
    }

    @Override // com.google.android.gms.common.internal.BaseGmsClient
    public final boolean usesClientTelemetry() {
        return true;
    }

    public final void zzA(BaseImplementation.ResultHolder<Status> resultHolder, ChannelApi.ChannelListener channelListener, String str) throws RemoteException {
        if (str == null) {
            this.zzh.zzc(this, resultHolder, channelListener);
            return;
        }
        this.zzh.zzc(this, resultHolder, new zzgo(str, channelListener));
    }

    public final void zzp(BaseImplementation.ResultHolder<DataApi.DataItemResult> resultHolder, PutDataRequest putDataRequest) throws RemoteException {
        for (Map.Entry<String, Asset> entry : putDataRequest.getAssets().entrySet()) {
            Asset value = entry.getValue();
            if (value.zza() == null && value.getDigest() == null && value.getFd() == null && value.getUri() == null) {
                String valueOf = String.valueOf(putDataRequest.getUri());
                String valueOf2 = String.valueOf(value);
                StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 33 + String.valueOf(valueOf2).length());
                sb.append("Put for ");
                sb.append(valueOf);
                sb.append(" contains invalid asset: ");
                sb.append(valueOf2);
                throw new IllegalArgumentException(sb.toString());
            }
        }
        PutDataRequest zza = PutDataRequest.zza(putDataRequest.getUri());
        zza.setData(putDataRequest.getData());
        if (putDataRequest.isUrgent()) {
            zza.setUrgent();
        }
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<String, Asset> entry2 : putDataRequest.getAssets().entrySet()) {
            Asset value2 = entry2.getValue();
            if (value2.zza() != null) {
                try {
                    ParcelFileDescriptor[] createPipe = ParcelFileDescriptor.createPipe();
                    if (Log.isLoggable("WearableClient", 3)) {
                        String valueOf3 = String.valueOf(value2);
                        String valueOf4 = String.valueOf(createPipe[0]);
                        String valueOf5 = String.valueOf(createPipe[1]);
                        int length = String.valueOf(valueOf3).length();
                        StringBuilder sb2 = new StringBuilder(length + 61 + String.valueOf(valueOf4).length() + String.valueOf(valueOf5).length());
                        sb2.append("processAssets: replacing data with FD in asset: ");
                        sb2.append(valueOf3);
                        sb2.append(" read:");
                        sb2.append(valueOf4);
                        sb2.append(" write:");
                        sb2.append(valueOf5);
                        Log.d("WearableClient", sb2.toString());
                    }
                    zza.putAsset(entry2.getKey(), Asset.createFromFd(createPipe[0]));
                    FutureTask futureTask = new FutureTask(new zzhs(this, createPipe[1], value2.zza()));
                    arrayList.add(futureTask);
                    this.zze.submit(futureTask);
                } catch (IOException e) {
                    String valueOf6 = String.valueOf(putDataRequest);
                    StringBuilder sb3 = new StringBuilder(String.valueOf(valueOf6).length() + 60);
                    sb3.append("Unable to create ParcelFileDescriptor for asset in request: ");
                    sb3.append(valueOf6);
                    throw new IllegalStateException(sb3.toString(), e);
                }
            } else if (value2.getUri() != null) {
                try {
                    zza.putAsset(entry2.getKey(), Asset.createFromFd(getContext().getContentResolver().openFileDescriptor(value2.getUri(), "r")));
                } catch (FileNotFoundException e2) {
                    new zzhn(resultHolder, arrayList).zzg(new zzge(WearableStatusCodes.ASSET_UNAVAILABLE, null));
                    String valueOf7 = String.valueOf(value2.getUri());
                    StringBuilder sb4 = new StringBuilder(String.valueOf(valueOf7).length() + 28);
                    sb4.append("Couldn't resolve asset URI: ");
                    sb4.append(valueOf7);
                    Log.w("WearableClient", sb4.toString());
                    return;
                }
            } else {
                zza.putAsset(entry2.getKey(), value2);
            }
        }
        ((zzeu) getService()).zzd(new zzhn(resultHolder, arrayList), zza);
    }

    public final void zzq(BaseImplementation.ResultHolder<DataApi.GetFdForAssetResult> resultHolder, Asset asset) throws RemoteException {
        ((zzeu) getService()).zzk(new zzhj(resultHolder), asset);
    }

    public final void zzr(BaseImplementation.ResultHolder<Status> resultHolder, String str, Uri uri, boolean z) {
        try {
            ExecutorService executorService = this.zze;
            Preconditions.checkNotNull(resultHolder);
            Preconditions.checkNotNull(str);
            Preconditions.checkNotNull(uri);
            executorService.execute(new zzht(this, uri, resultHolder, z, str));
        } catch (RuntimeException e) {
            resultHolder.setFailedResult(new Status(8));
            throw e;
        }
    }

    public final void zzs(BaseImplementation.ResultHolder<Status> resultHolder, String str, Uri uri, long j, long j2) {
        try {
            ExecutorService executorService = this.zze;
            Preconditions.checkNotNull(resultHolder);
            Preconditions.checkNotNull(str);
            Preconditions.checkNotNull(uri);
            Preconditions.checkArgument(j >= 0, "startOffset is negative: %s", Long.valueOf(j));
            Preconditions.checkArgument(j2 >= -1, "invalid length: %s", Long.valueOf(j2));
            executorService.execute(new zzhu(this, uri, resultHolder, str, j, j2));
        } catch (RuntimeException e) {
            resultHolder.setFailedResult(new Status(8));
            throw e;
        }
    }

    public final void zzt(BaseImplementation.ResultHolder<Status> resultHolder, DataApi.DataListener dataListener, ListenerHolder<? extends DataApi.DataListener> listenerHolder, IntentFilter[] intentFilterArr) throws RemoteException {
        this.zzi.zzb(this, resultHolder, dataListener, zzia.zzl(listenerHolder, intentFilterArr));
    }

    public final void zzu(BaseImplementation.ResultHolder<Status> resultHolder, MessageApi.MessageListener messageListener, ListenerHolder<? extends MessageApi.MessageListener> listenerHolder, IntentFilter[] intentFilterArr) throws RemoteException {
        this.zzj.zzb(this, resultHolder, messageListener, zzia.zzm(listenerHolder, intentFilterArr));
    }

    public final void zzv(BaseImplementation.ResultHolder<Status> resultHolder, CapabilityApi.CapabilityListener capabilityListener, ListenerHolder<? extends CapabilityApi.CapabilityListener> listenerHolder, IntentFilter[] intentFilterArr) throws RemoteException {
        this.zzn.zzb(this, resultHolder, capabilityListener, zzia.zzp(listenerHolder, intentFilterArr));
    }

    public final void zzw(BaseImplementation.ResultHolder<Status> resultHolder, ChannelApi.ChannelListener channelListener, ListenerHolder<? extends ChannelApi.ChannelListener> listenerHolder, @Nullable String str, IntentFilter[] intentFilterArr) throws RemoteException {
        if (str == null) {
            this.zzh.zzb(this, resultHolder, channelListener, zzia.zzn(listenerHolder, intentFilterArr));
            return;
        }
        this.zzh.zzb(this, resultHolder, new zzgo(str, channelListener), zzia.zzo(listenerHolder, str, intentFilterArr));
    }

    public final void zzx(BaseImplementation.ResultHolder<Status> resultHolder, DataApi.DataListener dataListener) throws RemoteException {
        this.zzi.zzc(this, resultHolder, dataListener);
    }

    public final void zzy(BaseImplementation.ResultHolder<Status> resultHolder, MessageApi.MessageListener messageListener) throws RemoteException {
        this.zzj.zzc(this, resultHolder, messageListener);
    }

    public final void zzz(BaseImplementation.ResultHolder<Status> resultHolder, CapabilityApi.CapabilityListener capabilityListener) throws RemoteException {
        this.zzn.zzc(this, resultHolder, capabilityListener);
    }
}
