package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.RecentlyNonNull;
import androidx.annotation.RecentlyNullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.GmsClientSupervisor;
import com.google.android.gms.common.internal.IGmsCallbacks;
import com.huawei.hms.api.HuaweiApiClientImpl;
import com.huawei.hms.framework.network.grs.GrsBaseInfo;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.concurrent.GuardedBy;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes.dex */
public abstract class BaseGmsClient<T extends IInterface> {
    private static final Feature[] zzd = new Feature[0];
    final Handler zza;
    private volatile String zzaa;
    @RecentlyNonNull
    protected ConnectionProgressReportCallbacks zzb;
    private int zze;
    private long zzf;
    private long zzg;
    private int zzh;
    private long zzi;
    private zzl zzk;
    private final Context zzl;
    private final GmsClientSupervisor zzn;
    @GuardedBy("mServiceBrokerLock")
    private IGmsServiceBroker zzr;
    @GuardedBy("mLock")
    private T zzs;
    @GuardedBy("mLock")
    private zzd zzu;
    private final BaseConnectionCallbacks zzw;
    private final BaseOnConnectionFailedListener zzx;
    private final int zzy;
    private final String zzz;
    private volatile String zzj = null;
    private final Object zzp = new Object();
    private final Object zzq = new Object();
    private final ArrayList<zzc<?>> zzt = new ArrayList<>();
    @GuardedBy("mLock")
    private int zzv = 1;
    private ConnectionResult zzab = null;
    private boolean zzac = false;
    private volatile com.google.android.gms.common.internal.zzc zzad = null;
    @RecentlyNonNull
    protected AtomicInteger zzc = new AtomicInteger(0);

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes.dex */
    public interface BaseConnectionCallbacks {
        void onConnected(Bundle bundle);

        void onConnectionSuspended(int i);
    }

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes.dex */
    public interface BaseOnConnectionFailedListener {
        void onConnectionFailed(@RecentlyNonNull ConnectionResult connectionResult);
    }

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes.dex */
    public interface ConnectionProgressReportCallbacks {
        void onReportServiceBinding(@RecentlyNonNull ConnectionResult connectionResult);
    }

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes.dex */
    protected class LegacyClientCallbackAdapter implements ConnectionProgressReportCallbacks {
        public LegacyClientCallbackAdapter() {
            BaseGmsClient.this = r1;
        }

        @Override // com.google.android.gms.common.internal.BaseGmsClient.ConnectionProgressReportCallbacks
        public void onReportServiceBinding(@RecentlyNonNull ConnectionResult connectionResult) {
            if (connectionResult.isSuccess()) {
                BaseGmsClient baseGmsClient = BaseGmsClient.this;
                baseGmsClient.getRemoteService(null, baseGmsClient.getScopes());
            } else if (BaseGmsClient.this.zzx == null) {
            } else {
                BaseGmsClient.this.zzx.onConnectionFailed(connectionResult);
            }
        }
    }

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes.dex */
    public interface SignOutCallbacks {
        void onSignOutComplete();
    }

    @RecentlyNullable
    protected abstract T createServiceInterface(@RecentlyNonNull IBinder iBinder);

    protected boolean enableLocalFallback() {
        return false;
    }

    @RecentlyNullable
    public Account getAccount() {
        return null;
    }

    @RecentlyNullable
    public Bundle getConnectionHint() {
        return null;
    }

    @RecentlyNullable
    protected String getLocalStartServiceAction() {
        return null;
    }

    protected abstract String getServiceDescriptor();

    protected abstract String getStartServiceAction();

    @RecentlyNonNull
    protected String getStartServicePackage() {
        return "com.google.android.gms";
    }

    protected boolean getUseDynamicLookup() {
        return false;
    }

    public boolean providesSignIn() {
        return false;
    }

    public boolean requiresAccount() {
        return false;
    }

    public boolean requiresGooglePlayServices() {
        return true;
    }

    public boolean requiresSignIn() {
        return false;
    }

    public boolean usesClientTelemetry() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes.dex */
    public final class zzb extends com.google.android.gms.internal.common.zzi {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public zzb(Looper looper) {
            super(looper);
            BaseGmsClient.this = r1;
        }

        @Override // android.os.Handler
        public final void handleMessage(Message message) {
            ConnectionResult connectionResult;
            ConnectionResult connectionResult2;
            if (BaseGmsClient.this.zzc.get() != message.arg1) {
                if (!zzb(message)) {
                    return;
                }
                zza(message);
                return;
            }
            int i = message.what;
            if ((i == 1 || i == 7 || ((i == 4 && !BaseGmsClient.this.enableLocalFallback()) || message.what == 5)) && !BaseGmsClient.this.isConnecting()) {
                zza(message);
                return;
            }
            int i2 = message.what;
            PendingIntent pendingIntent = null;
            if (i2 == 4) {
                BaseGmsClient.this.zzab = new ConnectionResult(message.arg2);
                if (BaseGmsClient.this.zzc() && !BaseGmsClient.this.zzac) {
                    BaseGmsClient.this.zza(3, (int) null);
                    return;
                }
                if (BaseGmsClient.this.zzab == null) {
                    connectionResult2 = new ConnectionResult(8);
                } else {
                    connectionResult2 = BaseGmsClient.this.zzab;
                }
                BaseGmsClient.this.zzb.onReportServiceBinding(connectionResult2);
                BaseGmsClient.this.onConnectionFailed(connectionResult2);
            } else if (i2 == 5) {
                if (BaseGmsClient.this.zzab == null) {
                    connectionResult = new ConnectionResult(8);
                } else {
                    connectionResult = BaseGmsClient.this.zzab;
                }
                BaseGmsClient.this.zzb.onReportServiceBinding(connectionResult);
                BaseGmsClient.this.onConnectionFailed(connectionResult);
            } else if (i2 == 3) {
                Object obj = message.obj;
                if (obj instanceof PendingIntent) {
                    pendingIntent = (PendingIntent) obj;
                }
                ConnectionResult connectionResult3 = new ConnectionResult(message.arg2, pendingIntent);
                BaseGmsClient.this.zzb.onReportServiceBinding(connectionResult3);
                BaseGmsClient.this.onConnectionFailed(connectionResult3);
            } else if (i2 != 6) {
                if (i2 == 2 && !BaseGmsClient.this.isConnected()) {
                    zza(message);
                } else if (zzb(message)) {
                    ((zzc) message.obj).zzc();
                } else {
                    int i3 = message.what;
                    StringBuilder sb = new StringBuilder(45);
                    sb.append("Don't know how to handle message: ");
                    sb.append(i3);
                    Log.wtf("GmsClient", sb.toString(), new Exception());
                }
            } else {
                BaseGmsClient.this.zza(5, (int) null);
                if (BaseGmsClient.this.zzw != null) {
                    BaseGmsClient.this.zzw.onConnectionSuspended(message.arg2);
                }
                BaseGmsClient.this.onConnectionSuspended(message.arg2);
                BaseGmsClient.this.zza(5, 1, (int) null);
            }
        }

        private static void zza(Message message) {
            zzc zzcVar = (zzc) message.obj;
            zzcVar.zzb();
            zzcVar.zzd();
        }

        private static boolean zzb(Message message) {
            int i = message.what;
            return i == 2 || i == 1 || i == 7;
        }
    }

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes.dex */
    public final class zzg extends zza {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public zzg(int i, Bundle bundle) {
            super(i, null);
            BaseGmsClient.this = r1;
        }

        @Override // com.google.android.gms.common.internal.BaseGmsClient.zza
        protected final void zza(ConnectionResult connectionResult) {
            if (!BaseGmsClient.this.enableLocalFallback() || !BaseGmsClient.this.zzc()) {
                BaseGmsClient.this.zzb.onReportServiceBinding(connectionResult);
                BaseGmsClient.this.onConnectionFailed(connectionResult);
                return;
            }
            BaseGmsClient.this.zza(16);
        }

        @Override // com.google.android.gms.common.internal.BaseGmsClient.zza
        protected final boolean zza() {
            BaseGmsClient.this.zzb.onReportServiceBinding(ConnectionResult.RESULT_SUCCESS);
            return true;
        }
    }

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes.dex */
    public abstract class zzc<TListener> {
        private TListener zza;
        private boolean zzb = false;

        public zzc(TListener tlistener) {
            BaseGmsClient.this = r1;
            this.zza = tlistener;
        }

        protected abstract void zza(TListener tlistener);

        protected abstract void zzb();

        public final void zzc() {
            TListener tlistener;
            synchronized (this) {
                tlistener = this.zza;
                if (this.zzb) {
                    String valueOf = String.valueOf(this);
                    StringBuilder sb = new StringBuilder(valueOf.length() + 47);
                    sb.append("Callback proxy ");
                    sb.append(valueOf);
                    sb.append(" being reused. This is not safe.");
                    Log.w("GmsClient", sb.toString());
                }
            }
            if (tlistener != null) {
                try {
                    zza(tlistener);
                } catch (RuntimeException e) {
                    zzb();
                    throw e;
                }
            } else {
                zzb();
            }
            synchronized (this) {
                this.zzb = true;
            }
            zzd();
        }

        public final void zzd() {
            zze();
            synchronized (BaseGmsClient.this.zzt) {
                BaseGmsClient.this.zzt.remove(this);
            }
        }

        public final void zze() {
            synchronized (this) {
                this.zza = null;
            }
        }
    }

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes.dex */
    public final class zzd implements ServiceConnection {
        private final int zza;

        public zzd(int i) {
            BaseGmsClient.this = r1;
            this.zza = i;
        }

        @Override // android.content.ServiceConnection
        public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IGmsServiceBroker iGmsServiceBroker;
            if (iBinder == null) {
                BaseGmsClient.this.zza(16);
                return;
            }
            synchronized (BaseGmsClient.this.zzq) {
                BaseGmsClient baseGmsClient = BaseGmsClient.this;
                IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.common.internal.IGmsServiceBroker");
                if (queryLocalInterface != null && (queryLocalInterface instanceof IGmsServiceBroker)) {
                    iGmsServiceBroker = (IGmsServiceBroker) queryLocalInterface;
                } else {
                    iGmsServiceBroker = new IGmsServiceBroker(iBinder) { // from class: com.google.android.gms.common.internal.IGmsServiceBroker$Stub$zza
                        private final IBinder zza;

                        /* JADX INFO: Access modifiers changed from: package-private */
                        {
                            this.zza = iBinder;
                        }

                        @Override // android.os.IInterface
                        public final IBinder asBinder() {
                            return this.zza;
                        }

                        @Override // com.google.android.gms.common.internal.IGmsServiceBroker
                        public final void getService(IGmsCallbacks iGmsCallbacks, GetServiceRequest getServiceRequest) throws RemoteException {
                            Parcel obtain = Parcel.obtain();
                            Parcel obtain2 = Parcel.obtain();
                            try {
                                obtain.writeInterfaceToken("com.google.android.gms.common.internal.IGmsServiceBroker");
                                obtain.writeStrongBinder(iGmsCallbacks != null ? iGmsCallbacks.asBinder() : null);
                                if (getServiceRequest != null) {
                                    obtain.writeInt(1);
                                    getServiceRequest.writeToParcel(obtain, 0);
                                } else {
                                    obtain.writeInt(0);
                                }
                                this.zza.transact(46, obtain, obtain2, 0);
                                obtain2.readException();
                            } finally {
                                obtain2.recycle();
                                obtain.recycle();
                            }
                        }
                    };
                }
                baseGmsClient.zzr = iGmsServiceBroker;
            }
            BaseGmsClient.this.zza(0, (Bundle) null, this.zza);
        }

        @Override // android.content.ServiceConnection
        public final void onServiceDisconnected(ComponentName componentName) {
            synchronized (BaseGmsClient.this.zzq) {
                BaseGmsClient.this.zzr = null;
            }
            Handler handler = BaseGmsClient.this.zza;
            handler.sendMessage(handler.obtainMessage(6, this.zza, 1));
        }
    }

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes.dex */
    public static final class zze extends IGmsCallbacks.zza {
        private BaseGmsClient zza;
        private final int zzb;

        public zze(BaseGmsClient baseGmsClient, int i) {
            this.zza = baseGmsClient;
            this.zzb = i;
        }

        @Override // com.google.android.gms.common.internal.IGmsCallbacks
        public final void zza(int i, Bundle bundle) {
            Log.wtf("GmsClient", "received deprecated onAccountValidationComplete callback, ignoring", new Exception());
        }

        @Override // com.google.android.gms.common.internal.IGmsCallbacks
        public final void onPostInitComplete(int i, IBinder iBinder, Bundle bundle) {
            Preconditions.checkNotNull(this.zza, "onPostInitComplete can be called only once per call to getRemoteService");
            this.zza.onPostInitHandler(i, iBinder, bundle, this.zzb);
            this.zza = null;
        }

        @Override // com.google.android.gms.common.internal.IGmsCallbacks
        public final void zza(int i, IBinder iBinder, com.google.android.gms.common.internal.zzc zzcVar) {
            BaseGmsClient baseGmsClient = this.zza;
            Preconditions.checkNotNull(baseGmsClient, "onPostInitCompleteWithConnectionInfo can be called only once per call togetRemoteService");
            Preconditions.checkNotNull(zzcVar);
            baseGmsClient.zza(zzcVar);
            onPostInitComplete(i, iBinder, zzcVar.zza);
        }
    }

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes.dex */
    public final class zzf extends zza {
        private final IBinder zza;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public zzf(int i, IBinder iBinder, Bundle bundle) {
            super(i, bundle);
            BaseGmsClient.this = r1;
            this.zza = iBinder;
        }

        @Override // com.google.android.gms.common.internal.BaseGmsClient.zza
        protected final void zza(ConnectionResult connectionResult) {
            if (BaseGmsClient.this.zzx != null) {
                BaseGmsClient.this.zzx.onConnectionFailed(connectionResult);
            }
            BaseGmsClient.this.onConnectionFailed(connectionResult);
        }

        @Override // com.google.android.gms.common.internal.BaseGmsClient.zza
        protected final boolean zza() {
            try {
                String interfaceDescriptor = ((IBinder) Preconditions.checkNotNull(this.zza)).getInterfaceDescriptor();
                if (!BaseGmsClient.this.getServiceDescriptor().equals(interfaceDescriptor)) {
                    String serviceDescriptor = BaseGmsClient.this.getServiceDescriptor();
                    StringBuilder sb = new StringBuilder(String.valueOf(serviceDescriptor).length() + 34 + String.valueOf(interfaceDescriptor).length());
                    sb.append("service descriptor mismatch: ");
                    sb.append(serviceDescriptor);
                    sb.append(" vs. ");
                    sb.append(interfaceDescriptor);
                    Log.e("GmsClient", sb.toString());
                    return false;
                }
                IInterface createServiceInterface = BaseGmsClient.this.createServiceInterface(this.zza);
                if (createServiceInterface == null || (!BaseGmsClient.this.zza(2, 4, (int) createServiceInterface) && !BaseGmsClient.this.zza(3, 4, (int) createServiceInterface))) {
                    return false;
                }
                BaseGmsClient.this.zzab = null;
                Bundle connectionHint = BaseGmsClient.this.getConnectionHint();
                if (BaseGmsClient.this.zzw == null) {
                    return true;
                }
                BaseGmsClient.this.zzw.onConnected(connectionHint);
                return true;
            } catch (RemoteException unused) {
                Log.w("GmsClient", "service probably died");
                return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes.dex */
    public abstract class zza extends zzc<Boolean> {
        private final int zza;
        private final Bundle zzb;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        protected zza(int i, Bundle bundle) {
            super(Boolean.TRUE);
            BaseGmsClient.this = r2;
            this.zza = i;
            this.zzb = bundle;
        }

        protected abstract void zza(ConnectionResult connectionResult);

        protected abstract boolean zza();

        @Override // com.google.android.gms.common.internal.BaseGmsClient.zzc
        protected final void zzb() {
        }

        @Override // com.google.android.gms.common.internal.BaseGmsClient.zzc
        protected final /* synthetic */ void zza(Boolean bool) {
            PendingIntent pendingIntent = null;
            if (bool != null) {
                if (this.zza == 0) {
                    if (zza()) {
                        return;
                    }
                    BaseGmsClient.this.zza(1, (int) null);
                    zza(new ConnectionResult(8, null));
                    return;
                }
                BaseGmsClient.this.zza(1, (int) null);
                Bundle bundle = this.zzb;
                if (bundle != null) {
                    pendingIntent = (PendingIntent) bundle.getParcelable("pendingIntent");
                }
                zza(new ConnectionResult(this.zza, pendingIntent));
                return;
            }
            BaseGmsClient.this.zza(1, (int) null);
        }
    }

    public BaseGmsClient(@RecentlyNonNull Context context, @RecentlyNonNull Looper looper, @RecentlyNonNull GmsClientSupervisor gmsClientSupervisor, @RecentlyNonNull GoogleApiAvailabilityLight googleApiAvailabilityLight, int i, BaseConnectionCallbacks baseConnectionCallbacks, BaseOnConnectionFailedListener baseOnConnectionFailedListener, String str) {
        this.zzl = (Context) Preconditions.checkNotNull(context, "Context must not be null");
        Looper looper2 = (Looper) Preconditions.checkNotNull(looper, "Looper must not be null");
        this.zzn = (GmsClientSupervisor) Preconditions.checkNotNull(gmsClientSupervisor, "Supervisor must not be null");
        GoogleApiAvailabilityLight googleApiAvailabilityLight2 = (GoogleApiAvailabilityLight) Preconditions.checkNotNull(googleApiAvailabilityLight, "API availability must not be null");
        this.zza = new zzb(looper);
        this.zzy = i;
        this.zzw = baseConnectionCallbacks;
        this.zzx = baseOnConnectionFailedListener;
        this.zzz = str;
    }

    private final String zza() {
        String str = this.zzz;
        return str == null ? this.zzl.getClass().getName() : str;
    }

    public void setAttributionTag(@RecentlyNonNull String str) {
        this.zzaa = str;
    }

    public final void zza(com.google.android.gms.common.internal.zzc zzcVar) {
        this.zzad = zzcVar;
        if (usesClientTelemetry()) {
            ConnectionTelemetryConfiguration connectionTelemetryConfiguration = zzcVar.zzc;
            RootTelemetryConfigManager.getInstance().zza(connectionTelemetryConfiguration == null ? null : connectionTelemetryConfiguration.getRootTelemetryConfiguration());
        }
    }

    @RecentlyNullable
    public final Feature[] getAvailableFeatures() {
        com.google.android.gms.common.internal.zzc zzcVar = this.zzad;
        if (zzcVar == null) {
            return null;
        }
        return zzcVar.zzb;
    }

    @RecentlyNullable
    public ConnectionTelemetryConfiguration getTelemetryConfiguration() {
        com.google.android.gms.common.internal.zzc zzcVar = this.zzad;
        if (zzcVar == null) {
            return null;
        }
        return zzcVar.zzc;
    }

    protected void onConnectedLocked(@RecentlyNonNull T t) {
        this.zzg = System.currentTimeMillis();
    }

    protected void onConnectionSuspended(int i) {
        this.zze = i;
        this.zzf = System.currentTimeMillis();
    }

    protected void onConnectionFailed(@RecentlyNonNull ConnectionResult connectionResult) {
        this.zzh = connectionResult.getErrorCode();
        this.zzi = System.currentTimeMillis();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void zza(int i, T t) {
        zzl zzlVar;
        zzl zzlVar2;
        boolean z = false;
        if ((i == 4) == (t != null)) {
            z = true;
        }
        Preconditions.checkArgument(z);
        synchronized (this.zzp) {
            this.zzv = i;
            this.zzs = t;
            if (i == 1) {
                zzd zzdVar = this.zzu;
                if (zzdVar != null) {
                    this.zzn.zza((String) Preconditions.checkNotNull(this.zzk.zza()), this.zzk.zzb(), this.zzk.zzc(), zzdVar, zza(), this.zzk.zzd());
                    this.zzu = null;
                }
            } else if (i == 2 || i == 3) {
                zzd zzdVar2 = this.zzu;
                if (zzdVar2 != null && (zzlVar2 = this.zzk) != null) {
                    String zza2 = zzlVar2.zza();
                    String zzb2 = this.zzk.zzb();
                    StringBuilder sb = new StringBuilder(String.valueOf(zza2).length() + 70 + String.valueOf(zzb2).length());
                    sb.append("Calling connect() while still connected, missing disconnect() for ");
                    sb.append(zza2);
                    sb.append(" on ");
                    sb.append(zzb2);
                    Log.e("GmsClient", sb.toString());
                    this.zzn.zza((String) Preconditions.checkNotNull(this.zzk.zza()), this.zzk.zzb(), this.zzk.zzc(), zzdVar2, zza(), this.zzk.zzd());
                    this.zzc.incrementAndGet();
                }
                zzd zzdVar3 = new zzd(this.zzc.get());
                this.zzu = zzdVar3;
                if (this.zzv == 3 && getLocalStartServiceAction() != null) {
                    zzlVar = new zzl(getContext().getPackageName(), getLocalStartServiceAction(), true, GmsClientSupervisor.getDefaultBindFlags(), false);
                } else {
                    zzlVar = new zzl(getStartServicePackage(), getStartServiceAction(), false, GmsClientSupervisor.getDefaultBindFlags(), getUseDynamicLookup());
                }
                this.zzk = zzlVar;
                if (zzlVar.zzd() && getMinApkVersion() < 17895000) {
                    String valueOf = String.valueOf(this.zzk.zza());
                    throw new IllegalStateException(valueOf.length() != 0 ? "Internal Error, the minimum apk version of this BaseGmsClient is too low to support dynamic lookup. Start service action: ".concat(valueOf) : new String("Internal Error, the minimum apk version of this BaseGmsClient is too low to support dynamic lookup. Start service action: "));
                }
                if (!this.zzn.zza(new GmsClientSupervisor.zza((String) Preconditions.checkNotNull(this.zzk.zza()), this.zzk.zzb(), this.zzk.zzc(), this.zzk.zzd()), zzdVar3, zza())) {
                    String zza3 = this.zzk.zza();
                    String zzb3 = this.zzk.zzb();
                    StringBuilder sb2 = new StringBuilder(String.valueOf(zza3).length() + 34 + String.valueOf(zzb3).length());
                    sb2.append("unable to connect to service: ");
                    sb2.append(zza3);
                    sb2.append(" on ");
                    sb2.append(zzb3);
                    Log.e("GmsClient", sb2.toString());
                    zza(16, (Bundle) null, this.zzc.get());
                }
            } else if (i == 4) {
                onConnectedLocked((IInterface) Preconditions.checkNotNull(t));
            }
        }
    }

    public final boolean zza(int i, int i2, T t) {
        synchronized (this.zzp) {
            if (this.zzv != i) {
                return false;
            }
            zza(i2, (int) t);
            return true;
        }
    }

    public void connect(@RecentlyNonNull ConnectionProgressReportCallbacks connectionProgressReportCallbacks) {
        this.zzb = (ConnectionProgressReportCallbacks) Preconditions.checkNotNull(connectionProgressReportCallbacks, "Connection progress callbacks cannot be null.");
        zza(2, (int) null);
    }

    public boolean isConnected() {
        boolean z;
        synchronized (this.zzp) {
            z = this.zzv == 4;
        }
        return z;
    }

    public boolean isConnecting() {
        boolean z;
        synchronized (this.zzp) {
            int i = this.zzv;
            if (i != 2 && i != 3) {
                z = false;
            }
            z = true;
        }
        return z;
    }

    private final boolean zzb() {
        boolean z;
        synchronized (this.zzp) {
            z = this.zzv == 3;
        }
        return z;
    }

    public void disconnect(@RecentlyNonNull String str) {
        this.zzj = str;
        disconnect();
    }

    public void disconnect() {
        this.zzc.incrementAndGet();
        synchronized (this.zzt) {
            int size = this.zzt.size();
            for (int i = 0; i < size; i++) {
                this.zzt.get(i).zze();
            }
            this.zzt.clear();
        }
        synchronized (this.zzq) {
            this.zzr = null;
        }
        zza(1, (int) null);
    }

    @RecentlyNullable
    public String getLastDisconnectMessage() {
        return this.zzj;
    }

    public void triggerConnectionSuspended(int i) {
        Handler handler = this.zza;
        handler.sendMessage(handler.obtainMessage(6, this.zzc.get(), i));
    }

    public final void zza(int i) {
        int i2;
        if (zzb()) {
            i2 = 5;
            this.zzac = true;
        } else {
            i2 = 4;
        }
        Handler handler = this.zza;
        handler.sendMessage(handler.obtainMessage(i2, this.zzc.get(), 16));
    }

    @RecentlyNonNull
    public final Context getContext() {
        return this.zzl;
    }

    @RecentlyNonNull
    public Feature[] getApiFeatures() {
        return zzd;
    }

    @RecentlyNonNull
    protected Bundle getGetServiceRequestExtraArgs() {
        return new Bundle();
    }

    protected void onPostInitHandler(int i, IBinder iBinder, Bundle bundle, int i2) {
        Handler handler = this.zza;
        handler.sendMessage(handler.obtainMessage(1, i2, -1, new zzf(i, iBinder, bundle)));
    }

    protected final void zza(int i, Bundle bundle, int i2) {
        Handler handler = this.zza;
        handler.sendMessage(handler.obtainMessage(7, i2, -1, new zzg(i, null)));
    }

    public final void checkConnected() {
        if (isConnected()) {
            return;
        }
        throw new IllegalStateException("Not connected. Call connect() and wait for onConnected() to be called.");
    }

    @RecentlyNonNull
    public final T getService() throws DeadObjectException {
        T t;
        synchronized (this.zzp) {
            if (this.zzv == 5) {
                throw new DeadObjectException();
            }
            checkConnected();
            t = (T) Preconditions.checkNotNull(this.zzs, "Client is connected but service is null");
        }
        return t;
    }

    public void getRemoteService(IAccountAccessor iAccountAccessor, @RecentlyNonNull Set<Scope> set) {
        Throwable e;
        Bundle getServiceRequestExtraArgs = getGetServiceRequestExtraArgs();
        GetServiceRequest getServiceRequest = new GetServiceRequest(this.zzy, this.zzaa);
        getServiceRequest.zza = this.zzl.getPackageName();
        getServiceRequest.zzd = getServiceRequestExtraArgs;
        if (set != null) {
            getServiceRequest.zzc = (Scope[]) set.toArray(new Scope[set.size()]);
        }
        if (requiresSignIn()) {
            Account account = getAccount();
            if (account == null) {
                account = new Account(HuaweiApiClientImpl.DEFAULT_ACCOUNT, "com.google");
            }
            getServiceRequest.zze = account;
            if (iAccountAccessor != null) {
                getServiceRequest.zzb = iAccountAccessor.asBinder();
            }
        } else if (requiresAccount()) {
            getServiceRequest.zze = getAccount();
        }
        getServiceRequest.zzf = zzd;
        getServiceRequest.zzg = getApiFeatures();
        if (usesClientTelemetry()) {
            getServiceRequest.zzh = true;
        }
        try {
            synchronized (this.zzq) {
                IGmsServiceBroker iGmsServiceBroker = this.zzr;
                if (iGmsServiceBroker != null) {
                    iGmsServiceBroker.getService(new zze(this, this.zzc.get()), getServiceRequest);
                } else {
                    Log.w("GmsClient", "mServiceBroker is null, client disconnected");
                }
            }
        } catch (DeadObjectException e2) {
            Log.w("GmsClient", "IGmsServiceBroker.getService failed", e2);
            triggerConnectionSuspended(3);
        } catch (RemoteException e3) {
            e = e3;
            Log.w("GmsClient", "IGmsServiceBroker.getService failed", e);
            onPostInitHandler(8, null, null, this.zzc.get());
        } catch (SecurityException e4) {
            throw e4;
        } catch (RuntimeException e5) {
            e = e5;
            Log.w("GmsClient", "IGmsServiceBroker.getService failed", e);
            onPostInitHandler(8, null, null, this.zzc.get());
        }
    }

    public void onUserSignOut(@RecentlyNonNull SignOutCallbacks signOutCallbacks) {
        signOutCallbacks.onSignOutComplete();
    }

    @RecentlyNonNull
    public Intent getSignInIntent() {
        throw new UnsupportedOperationException("Not a sign in API");
    }

    @RecentlyNonNull
    protected Set<Scope> getScopes() {
        return Collections.emptySet();
    }

    public void dump(@RecentlyNonNull String str, @RecentlyNonNull FileDescriptor fileDescriptor, @RecentlyNonNull PrintWriter printWriter, @RecentlyNonNull String[] strArr) {
        int i;
        T t;
        IGmsServiceBroker iGmsServiceBroker;
        synchronized (this.zzp) {
            i = this.zzv;
            t = this.zzs;
        }
        synchronized (this.zzq) {
            iGmsServiceBroker = this.zzr;
        }
        printWriter.append((CharSequence) str).append("mConnectState=");
        if (i == 1) {
            printWriter.print("DISCONNECTED");
        } else if (i == 2) {
            printWriter.print("REMOTE_CONNECTING");
        } else if (i == 3) {
            printWriter.print("LOCAL_CONNECTING");
        } else if (i == 4) {
            printWriter.print("CONNECTED");
        } else if (i == 5) {
            printWriter.print("DISCONNECTING");
        } else {
            printWriter.print(GrsBaseInfo.CountryCodeSource.UNKNOWN);
        }
        printWriter.append(" mService=");
        if (t == null) {
            printWriter.append("null");
        } else {
            printWriter.append((CharSequence) getServiceDescriptor()).append("@").append((CharSequence) Integer.toHexString(System.identityHashCode(t.asBinder())));
        }
        printWriter.append(" mServiceBroker=");
        if (iGmsServiceBroker == null) {
            printWriter.println("null");
        } else {
            printWriter.append("IGmsServiceBroker@").println(Integer.toHexString(System.identityHashCode(iGmsServiceBroker.asBinder())));
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        if (this.zzg > 0) {
            PrintWriter append = printWriter.append((CharSequence) str).append("lastConnectedTime=");
            long j = this.zzg;
            String format = simpleDateFormat.format(new Date(this.zzg));
            StringBuilder sb = new StringBuilder(String.valueOf(format).length() + 21);
            sb.append(j);
            sb.append(" ");
            sb.append(format);
            append.println(sb.toString());
        }
        if (this.zzf > 0) {
            printWriter.append((CharSequence) str).append("lastSuspendedCause=");
            int i2 = this.zze;
            if (i2 == 1) {
                printWriter.append("CAUSE_SERVICE_DISCONNECTED");
            } else if (i2 == 2) {
                printWriter.append("CAUSE_NETWORK_LOST");
            } else if (i2 == 3) {
                printWriter.append("CAUSE_DEAD_OBJECT_EXCEPTION");
            } else {
                printWriter.append((CharSequence) String.valueOf(i2));
            }
            PrintWriter append2 = printWriter.append(" lastSuspendedTime=");
            long j2 = this.zzf;
            String format2 = simpleDateFormat.format(new Date(this.zzf));
            StringBuilder sb2 = new StringBuilder(String.valueOf(format2).length() + 21);
            sb2.append(j2);
            sb2.append(" ");
            sb2.append(format2);
            append2.println(sb2.toString());
        }
        if (this.zzi > 0) {
            printWriter.append((CharSequence) str).append("lastFailedStatus=").append((CharSequence) CommonStatusCodes.getStatusCodeString(this.zzh));
            PrintWriter append3 = printWriter.append(" lastFailedTime=");
            long j3 = this.zzi;
            String format3 = simpleDateFormat.format(new Date(this.zzi));
            StringBuilder sb3 = new StringBuilder(String.valueOf(format3).length() + 21);
            sb3.append(j3);
            sb3.append(" ");
            sb3.append(format3);
            append3.println(sb3.toString());
        }
    }

    public final boolean zzc() {
        if (!this.zzac && !TextUtils.isEmpty(getServiceDescriptor()) && !TextUtils.isEmpty(getLocalStartServiceAction())) {
            try {
                Class.forName(getServiceDescriptor());
                return true;
            } catch (ClassNotFoundException unused) {
                return false;
            }
        }
        return false;
    }

    @RecentlyNonNull
    public String getEndpointPackageName() {
        zzl zzlVar;
        if (isConnected() && (zzlVar = this.zzk) != null) {
            return zzlVar.zzb();
        }
        throw new RuntimeException("Failed to connect when checking package");
    }

    public int getMinApkVersion() {
        return GoogleApiAvailabilityLight.GOOGLE_PLAY_SERVICES_VERSION_CODE;
    }
}
