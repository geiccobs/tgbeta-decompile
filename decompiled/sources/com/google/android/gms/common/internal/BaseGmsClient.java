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
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.GmsClientSupervisor;
import com.google.android.gms.common.internal.IGmsCallbacks;
import com.google.android.gms.common.internal.IGmsServiceBroker;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes3.dex */
public abstract class BaseGmsClient<T extends IInterface> {
    public static final int CONNECT_STATE_CONNECTED = 4;
    public static final int CONNECT_STATE_DISCONNECTED = 1;
    public static final int CONNECT_STATE_DISCONNECTING = 5;
    public static final String DEFAULT_ACCOUNT = "<<default account>>";
    public static final String KEY_PENDING_INTENT = "pendingIntent";
    final Handler zza;
    private volatile String zzaa;
    private ConnectionResult zzab;
    private boolean zzac;
    private volatile com.google.android.gms.common.internal.zzc zzad;
    protected ConnectionProgressReportCallbacks zzb;
    protected AtomicInteger zzc;
    private int zze;
    private long zzf;
    private long zzg;
    private int zzh;
    private long zzi;
    private volatile String zzj;
    private zzl zzk;
    private final Context zzl;
    private final Looper zzm;
    private final GmsClientSupervisor zzn;
    private final GoogleApiAvailabilityLight zzo;
    private final Object zzp;
    private final Object zzq;
    private IGmsServiceBroker zzr;
    private T zzs;
    private final ArrayList<zzc<?>> zzt;
    private zzd zzu;
    private int zzv;
    private final BaseConnectionCallbacks zzw;
    private final BaseOnConnectionFailedListener zzx;
    private final int zzy;
    private final String zzz;
    private static final Feature[] zzd = new Feature[0];
    public static final String[] GOOGLE_PLUS_REQUIRED_FEATURES = {"service_esmobile", "service_googleme"};

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes3.dex */
    public interface BaseConnectionCallbacks {
        public static final int CAUSE_DEAD_OBJECT_EXCEPTION = 3;
        public static final int CAUSE_SERVICE_DISCONNECTED = 1;

        void onConnected(Bundle bundle);

        void onConnectionSuspended(int i);
    }

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes3.dex */
    public interface BaseOnConnectionFailedListener {
        void onConnectionFailed(ConnectionResult connectionResult);
    }

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes3.dex */
    public interface ConnectionProgressReportCallbacks {
        void onReportServiceBinding(ConnectionResult connectionResult);
    }

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes3.dex */
    protected class LegacyClientCallbackAdapter implements ConnectionProgressReportCallbacks {
        public LegacyClientCallbackAdapter() {
            BaseGmsClient.this = r1;
        }

        @Override // com.google.android.gms.common.internal.BaseGmsClient.ConnectionProgressReportCallbacks
        public void onReportServiceBinding(ConnectionResult connectionResult) {
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
    /* loaded from: classes3.dex */
    public interface SignOutCallbacks {
        void onSignOutComplete();
    }

    protected BaseGmsClient(Context context, Looper looper, int i, BaseConnectionCallbacks baseConnectionCallbacks, BaseOnConnectionFailedListener baseOnConnectionFailedListener, String str) {
        this(context, looper, GmsClientSupervisor.getInstance(context), GoogleApiAvailabilityLight.getInstance(), i, (BaseConnectionCallbacks) Preconditions.checkNotNull(baseConnectionCallbacks), (BaseOnConnectionFailedListener) Preconditions.checkNotNull(baseOnConnectionFailedListener), str);
    }

    protected abstract T createServiceInterface(IBinder iBinder);

    protected abstract String getServiceDescriptor();

    protected abstract String getStartServiceAction();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes3.dex */
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
                if (zzb(message)) {
                    zza(message);
                }
            } else if ((message.what == 1 || message.what == 7 || ((message.what == 4 && !BaseGmsClient.this.enableLocalFallback()) || message.what == 5)) && !BaseGmsClient.this.isConnecting()) {
                zza(message);
            } else {
                PendingIntent pendingIntent = null;
                if (message.what == 4) {
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
                } else if (message.what != 5) {
                    if (message.what == 3) {
                        if (message.obj instanceof PendingIntent) {
                            pendingIntent = (PendingIntent) message.obj;
                        }
                        ConnectionResult connectionResult3 = new ConnectionResult(message.arg2, pendingIntent);
                        BaseGmsClient.this.zzb.onReportServiceBinding(connectionResult3);
                        BaseGmsClient.this.onConnectionFailed(connectionResult3);
                    } else if (message.what != 6) {
                        if (message.what == 2 && !BaseGmsClient.this.isConnected()) {
                            zza(message);
                        } else if (zzb(message)) {
                            ((zzc) message.obj).zzc();
                        } else {
                            int i = message.what;
                            StringBuilder sb = new StringBuilder(45);
                            sb.append("Don't know how to handle message: ");
                            sb.append(i);
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
                } else {
                    if (BaseGmsClient.this.zzab == null) {
                        connectionResult = new ConnectionResult(8);
                    } else {
                        connectionResult = BaseGmsClient.this.zzab;
                    }
                    BaseGmsClient.this.zzb.onReportServiceBinding(connectionResult);
                    BaseGmsClient.this.onConnectionFailed(connectionResult);
                }
            }
        }

        private static void zza(Message message) {
            zzc zzcVar = (zzc) message.obj;
            zzcVar.zzb();
            zzcVar.zzd();
        }

        private static boolean zzb(Message message) {
            return message.what == 2 || message.what == 1 || message.what == 7;
        }
    }

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes3.dex */
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
                    iGmsServiceBroker = new IGmsServiceBroker.Stub.zza(iBinder);
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
            BaseGmsClient.this.zza.sendMessage(BaseGmsClient.this.zza.obtainMessage(6, this.zza, 1));
        }
    }

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes3.dex */
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
    /* loaded from: classes3.dex */
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
                    StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 47);
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
    /* loaded from: classes3.dex */
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
    /* loaded from: classes3.dex */
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
            } catch (RemoteException e) {
                Log.w("GmsClient", "service probably died");
                return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes3.dex */
    public abstract class zza extends zzc<Boolean> {
        private final int zza;
        private final Bundle zzb;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        protected zza(int i, Bundle bundle) {
            super(true);
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
                    pendingIntent = (PendingIntent) bundle.getParcelable(BaseGmsClient.KEY_PENDING_INTENT);
                }
                zza(new ConnectionResult(this.zza, pendingIntent));
                return;
            }
            BaseGmsClient.this.zza(1, (int) null);
        }
    }

    public BaseGmsClient(Context context, Looper looper, GmsClientSupervisor gmsClientSupervisor, GoogleApiAvailabilityLight googleApiAvailabilityLight, int i, BaseConnectionCallbacks baseConnectionCallbacks, BaseOnConnectionFailedListener baseOnConnectionFailedListener, String str) {
        this.zzj = null;
        this.zzp = new Object();
        this.zzq = new Object();
        this.zzt = new ArrayList<>();
        this.zzv = 1;
        this.zzab = null;
        this.zzac = false;
        this.zzad = null;
        this.zzc = new AtomicInteger(0);
        this.zzl = (Context) Preconditions.checkNotNull(context, "Context must not be null");
        this.zzm = (Looper) Preconditions.checkNotNull(looper, "Looper must not be null");
        this.zzn = (GmsClientSupervisor) Preconditions.checkNotNull(gmsClientSupervisor, "Supervisor must not be null");
        this.zzo = (GoogleApiAvailabilityLight) Preconditions.checkNotNull(googleApiAvailabilityLight, "API availability must not be null");
        this.zza = new zzb(looper);
        this.zzy = i;
        this.zzw = baseConnectionCallbacks;
        this.zzx = baseOnConnectionFailedListener;
        this.zzz = str;
    }

    public BaseGmsClient(Context context, Handler handler, GmsClientSupervisor gmsClientSupervisor, GoogleApiAvailabilityLight googleApiAvailabilityLight, int i, BaseConnectionCallbacks baseConnectionCallbacks, BaseOnConnectionFailedListener baseOnConnectionFailedListener) {
        this.zzj = null;
        this.zzp = new Object();
        this.zzq = new Object();
        this.zzt = new ArrayList<>();
        this.zzv = 1;
        this.zzab = null;
        this.zzac = false;
        this.zzad = null;
        this.zzc = new AtomicInteger(0);
        this.zzl = (Context) Preconditions.checkNotNull(context, "Context must not be null");
        this.zza = (Handler) Preconditions.checkNotNull(handler, "Handler must not be null");
        this.zzm = handler.getLooper();
        this.zzn = (GmsClientSupervisor) Preconditions.checkNotNull(gmsClientSupervisor, "Supervisor must not be null");
        this.zzo = (GoogleApiAvailabilityLight) Preconditions.checkNotNull(googleApiAvailabilityLight, "API availability must not be null");
        this.zzy = i;
        this.zzw = baseConnectionCallbacks;
        this.zzx = baseOnConnectionFailedListener;
        this.zzz = null;
    }

    protected String getStartServicePackage() {
        return "com.google.android.gms";
    }

    private final String zza() {
        String str = this.zzz;
        return str == null ? this.zzl.getClass().getName() : str;
    }

    public void setAttributionTag(String str) {
        this.zzaa = str;
    }

    protected String getLocalStartServiceAction() {
        return null;
    }

    public final void zza(com.google.android.gms.common.internal.zzc zzcVar) {
        RootTelemetryConfiguration rootTelemetryConfiguration;
        this.zzad = zzcVar;
        if (usesClientTelemetry()) {
            ConnectionTelemetryConfiguration connectionTelemetryConfiguration = zzcVar.zzc;
            RootTelemetryConfigManager rootTelemetryConfigManager = RootTelemetryConfigManager.getInstance();
            if (connectionTelemetryConfiguration == null) {
                rootTelemetryConfiguration = null;
            } else {
                rootTelemetryConfiguration = connectionTelemetryConfiguration.getRootTelemetryConfiguration();
            }
            rootTelemetryConfigManager.zza(rootTelemetryConfiguration);
        }
    }

    public final Feature[] getAvailableFeatures() {
        com.google.android.gms.common.internal.zzc zzcVar = this.zzad;
        if (zzcVar == null) {
            return null;
        }
        return zzcVar.zzb;
    }

    public ConnectionTelemetryConfiguration getTelemetryConfiguration() {
        com.google.android.gms.common.internal.zzc zzcVar = this.zzad;
        if (zzcVar == null) {
            return null;
        }
        return zzcVar.zzc;
    }

    protected void onConnectedLocked(T t) {
        this.zzg = System.currentTimeMillis();
    }

    protected void onConnectionSuspended(int i) {
        this.zze = i;
        this.zzf = System.currentTimeMillis();
    }

    protected void onConnectionFailed(ConnectionResult connectionResult) {
        this.zzh = connectionResult.getErrorCode();
        this.zzi = System.currentTimeMillis();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void zza(int i, T t) {
        zzl zzlVar;
        zzl zzlVar2;
        boolean z = true;
        if ((i == 4) != (t != null)) {
            z = false;
        }
        Preconditions.checkArgument(z);
        synchronized (this.zzp) {
            this.zzv = i;
            this.zzs = t;
            switch (i) {
                case 1:
                    zzd zzdVar = this.zzu;
                    if (zzdVar != null) {
                        this.zzn.zza((String) Preconditions.checkNotNull(this.zzk.zza()), this.zzk.zzb(), this.zzk.zzc(), zzdVar, zza(), this.zzk.zzd());
                        this.zzu = null;
                        break;
                    }
                    break;
                case 2:
                case 3:
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
                    break;
                    break;
                case 4:
                    onConnectedLocked((IInterface) Preconditions.checkNotNull(t));
                    break;
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

    public void checkAvailabilityAndConnect() {
        int isGooglePlayServicesAvailable = this.zzo.isGooglePlayServicesAvailable(this.zzl, getMinApkVersion());
        if (isGooglePlayServicesAvailable != 0) {
            zza(1, (int) null);
            triggerNotAvailable(new LegacyClientCallbackAdapter(), isGooglePlayServicesAvailable, null);
            return;
        }
        connect(new LegacyClientCallbackAdapter());
    }

    public void connect(ConnectionProgressReportCallbacks connectionProgressReportCallbacks) {
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

    public void disconnect(String str) {
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

    public void triggerNotAvailable(ConnectionProgressReportCallbacks connectionProgressReportCallbacks, int i, PendingIntent pendingIntent) {
        this.zzb = (ConnectionProgressReportCallbacks) Preconditions.checkNotNull(connectionProgressReportCallbacks, "Connection progress callbacks cannot be null.");
        Handler handler = this.zza;
        handler.sendMessage(handler.obtainMessage(3, this.zzc.get(), i, pendingIntent));
    }

    public final Context getContext() {
        return this.zzl;
    }

    public final Looper getLooper() {
        return this.zzm;
    }

    public Account getAccount() {
        return null;
    }

    public Feature[] getApiFeatures() {
        return zzd;
    }

    protected Bundle getGetServiceRequestExtraArgs() {
        return new Bundle();
    }

    public void onPostInitHandler(int i, IBinder iBinder, Bundle bundle, int i2) {
        Handler handler = this.zza;
        handler.sendMessage(handler.obtainMessage(1, i2, -1, new zzf(i, iBinder, bundle)));
    }

    protected final void zza(int i, Bundle bundle, int i2) {
        Handler handler = this.zza;
        handler.sendMessage(handler.obtainMessage(7, i2, -1, new zzg(i, null)));
    }

    public final void checkConnected() {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected. Call connect() and wait for onConnected() to be called.");
        }
    }

    public Bundle getConnectionHint() {
        return null;
    }

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

    public void getRemoteService(IAccountAccessor iAccountAccessor, Set<Scope> set) {
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
                account = new Account("<<default account>>", "com.google");
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

    protected boolean enableLocalFallback() {
        return false;
    }

    public boolean requiresSignIn() {
        return false;
    }

    public void onUserSignOut(SignOutCallbacks signOutCallbacks) {
        signOutCallbacks.onSignOutComplete();
    }

    public boolean requiresAccount() {
        return false;
    }

    public boolean requiresGooglePlayServices() {
        return true;
    }

    public boolean providesSignIn() {
        return false;
    }

    public Intent getSignInIntent() {
        throw new UnsupportedOperationException("Not a sign in API");
    }

    protected Set<Scope> getScopes() {
        return Collections.emptySet();
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
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
        switch (i) {
            case 1:
                printWriter.print("DISCONNECTED");
                break;
            case 2:
                printWriter.print("REMOTE_CONNECTING");
                break;
            case 3:
                printWriter.print("LOCAL_CONNECTING");
                break;
            case 4:
                printWriter.print("CONNECTED");
                break;
            case 5:
                printWriter.print("DISCONNECTING");
                break;
            default:
                printWriter.print("UNKNOWN");
                break;
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
            switch (i2) {
                case 1:
                    printWriter.append("CAUSE_SERVICE_DISCONNECTED");
                    break;
                case 2:
                    printWriter.append("CAUSE_NETWORK_LOST");
                    break;
                case 3:
                    printWriter.append("CAUSE_DEAD_OBJECT_EXCEPTION");
                    break;
                default:
                    printWriter.append((CharSequence) String.valueOf(i2));
                    break;
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

    public IBinder getServiceBrokerBinder() {
        synchronized (this.zzq) {
            IGmsServiceBroker iGmsServiceBroker = this.zzr;
            if (iGmsServiceBroker == null) {
                return null;
            }
            return iGmsServiceBroker.asBinder();
        }
    }

    public final boolean zzc() {
        if (!this.zzac && !TextUtils.isEmpty(getServiceDescriptor()) && !TextUtils.isEmpty(getLocalStartServiceAction())) {
            try {
                Class.forName(getServiceDescriptor());
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            }
        }
        return false;
    }

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

    protected boolean getUseDynamicLookup() {
        return false;
    }

    public boolean usesClientTelemetry() {
        return false;
    }
}
