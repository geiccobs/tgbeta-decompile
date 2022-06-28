package com.google.android.gms.common.api.internal;

import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.util.Pair;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultTransform;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.TransformedResult;
import com.google.android.gms.common.internal.ICancelToken;
import com.google.android.gms.common.internal.Preconditions;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public abstract class BasePendingResult<R extends Result> extends PendingResult<R> {
    static final ThreadLocal<Boolean> zaa = new zao();
    private zaa mResultGuardian;
    private final Object zab;
    private final CallbackHandler<R> zac;
    private final WeakReference<GoogleApiClient> zad;
    private final CountDownLatch zae;
    private final ArrayList<PendingResult.StatusListener> zaf;
    private ResultCallback<? super R> zag;
    private final AtomicReference<zacq> zah;
    private R zai;
    private Status zaj;
    private volatile boolean zak;
    private boolean zal;
    private boolean zam;
    private ICancelToken zan;
    private volatile zacn<R> zao;
    private boolean zap;

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes3.dex */
    public final class zaa {
        private zaa() {
            BasePendingResult.this = r1;
        }

        protected final void finalize() throws Throwable {
            BasePendingResult.zaa(BasePendingResult.this.zai);
            super.finalize();
        }

        /* synthetic */ zaa(BasePendingResult basePendingResult, zao zaoVar) {
            this();
        }
    }

    @Deprecated
    BasePendingResult() {
        this.zab = new Object();
        this.zae = new CountDownLatch(1);
        this.zaf = new ArrayList<>();
        this.zah = new AtomicReference<>();
        this.zap = false;
        this.zac = new CallbackHandler<>(Looper.getMainLooper());
        this.zad = new WeakReference<>(null);
    }

    public abstract R createFailedResult(Status status);

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes3.dex */
    public static class CallbackHandler<R extends Result> extends com.google.android.gms.internal.base.zas {
        public CallbackHandler() {
            this(Looper.getMainLooper());
        }

        public CallbackHandler(Looper looper) {
            super(looper);
        }

        public final void zaa(ResultCallback<? super R> resultCallback, R r) {
            sendMessage(obtainMessage(1, new Pair((ResultCallback) Preconditions.checkNotNull(BasePendingResult.zab(resultCallback)), r)));
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    Pair pair = (Pair) message.obj;
                    ResultCallback resultCallback = (ResultCallback) pair.first;
                    Result result = (Result) pair.second;
                    try {
                        resultCallback.onResult(result);
                        return;
                    } catch (RuntimeException e) {
                        BasePendingResult.zaa(result);
                        throw e;
                    }
                case 2:
                    ((BasePendingResult) message.obj).forceFailureUnlessReady(Status.RESULT_TIMEOUT);
                    return;
                default:
                    int i = message.what;
                    StringBuilder sb = new StringBuilder(45);
                    sb.append("Don't know how to handle message: ");
                    sb.append(i);
                    Log.wtf("BasePendingResult", sb.toString(), new Exception());
                    return;
            }
        }
    }

    public BasePendingResult(GoogleApiClient googleApiClient) {
        this.zab = new Object();
        this.zae = new CountDownLatch(1);
        this.zaf = new ArrayList<>();
        this.zah = new AtomicReference<>();
        this.zap = false;
        this.zac = new CallbackHandler<>(googleApiClient != null ? googleApiClient.getLooper() : Looper.getMainLooper());
        this.zad = new WeakReference<>(googleApiClient);
    }

    @Deprecated
    public BasePendingResult(Looper looper) {
        this.zab = new Object();
        this.zae = new CountDownLatch(1);
        this.zaf = new ArrayList<>();
        this.zah = new AtomicReference<>();
        this.zap = false;
        this.zac = new CallbackHandler<>(looper);
        this.zad = new WeakReference<>(null);
    }

    public BasePendingResult(CallbackHandler<R> callbackHandler) {
        this.zab = new Object();
        this.zae = new CountDownLatch(1);
        this.zaf = new ArrayList<>();
        this.zah = new AtomicReference<>();
        this.zap = false;
        this.zac = (CallbackHandler) Preconditions.checkNotNull(callbackHandler, "CallbackHandler must not be null");
        this.zad = new WeakReference<>(null);
    }

    public final boolean isReady() {
        return this.zae.getCount() == 0;
    }

    @Override // com.google.android.gms.common.api.PendingResult
    public final R await() {
        Preconditions.checkNotMainThread("await must not be called on the UI thread");
        boolean z = true;
        Preconditions.checkState(!this.zak, "Result has already been consumed");
        if (this.zao != null) {
            z = false;
        }
        Preconditions.checkState(z, "Cannot await if then() has been called.");
        try {
            this.zae.await();
        } catch (InterruptedException e) {
            forceFailureUnlessReady(Status.RESULT_INTERRUPTED);
        }
        Preconditions.checkState(isReady(), "Result is not ready.");
        return zac();
    }

    @Override // com.google.android.gms.common.api.PendingResult
    public final R await(long j, TimeUnit timeUnit) {
        if (j > 0) {
            Preconditions.checkNotMainThread("await must not be called on the UI thread when time is greater than zero.");
        }
        boolean z = true;
        Preconditions.checkState(!this.zak, "Result has already been consumed.");
        if (this.zao != null) {
            z = false;
        }
        Preconditions.checkState(z, "Cannot await if then() has been called.");
        try {
            if (!this.zae.await(j, timeUnit)) {
                forceFailureUnlessReady(Status.RESULT_TIMEOUT);
            }
        } catch (InterruptedException e) {
            forceFailureUnlessReady(Status.RESULT_INTERRUPTED);
        }
        Preconditions.checkState(isReady(), "Result is not ready.");
        return zac();
    }

    @Override // com.google.android.gms.common.api.PendingResult
    public final void setResultCallback(ResultCallback<? super R> resultCallback) {
        synchronized (this.zab) {
            if (resultCallback == null) {
                this.zag = null;
                return;
            }
            boolean z = true;
            Preconditions.checkState(!this.zak, "Result has already been consumed.");
            if (this.zao != null) {
                z = false;
            }
            Preconditions.checkState(z, "Cannot set callbacks if then() has been called.");
            if (isCanceled()) {
                return;
            }
            if (isReady()) {
                this.zac.zaa(resultCallback, zac());
            } else {
                this.zag = resultCallback;
            }
        }
    }

    @Override // com.google.android.gms.common.api.PendingResult
    public final void setResultCallback(ResultCallback<? super R> resultCallback, long j, TimeUnit timeUnit) {
        synchronized (this.zab) {
            if (resultCallback == null) {
                this.zag = null;
                return;
            }
            boolean z = true;
            Preconditions.checkState(!this.zak, "Result has already been consumed.");
            if (this.zao != null) {
                z = false;
            }
            Preconditions.checkState(z, "Cannot set callbacks if then() has been called.");
            if (isCanceled()) {
                return;
            }
            if (isReady()) {
                this.zac.zaa(resultCallback, zac());
            } else {
                this.zag = resultCallback;
                CallbackHandler<R> callbackHandler = this.zac;
                callbackHandler.sendMessageDelayed(callbackHandler.obtainMessage(2, this), timeUnit.toMillis(j));
            }
        }
    }

    public static <R extends Result> ResultCallback<R> zab(ResultCallback<R> resultCallback) {
        return resultCallback;
    }

    @Override // com.google.android.gms.common.api.PendingResult
    public final void addStatusListener(PendingResult.StatusListener statusListener) {
        Preconditions.checkArgument(statusListener != null, "Callback cannot be null.");
        synchronized (this.zab) {
            if (isReady()) {
                statusListener.onComplete(this.zaj);
            } else {
                this.zaf.add(statusListener);
            }
        }
    }

    @Override // com.google.android.gms.common.api.PendingResult
    public void cancel() {
        synchronized (this.zab) {
            if (!this.zal && !this.zak) {
                ICancelToken iCancelToken = this.zan;
                if (iCancelToken != null) {
                    try {
                        iCancelToken.cancel();
                    } catch (RemoteException e) {
                    }
                }
                zaa(this.zai);
                this.zal = true;
                zab((BasePendingResult<R>) createFailedResult(Status.RESULT_CANCELED));
            }
        }
    }

    public final boolean zaa() {
        boolean isCanceled;
        synchronized (this.zab) {
            if (this.zad.get() == null || !this.zap) {
                cancel();
            }
            isCanceled = isCanceled();
        }
        return isCanceled;
    }

    @Override // com.google.android.gms.common.api.PendingResult
    public boolean isCanceled() {
        boolean z;
        synchronized (this.zab) {
            z = this.zal;
        }
        return z;
    }

    @Override // com.google.android.gms.common.api.PendingResult
    public <S extends Result> TransformedResult<S> then(ResultTransform<? super R, ? extends S> resultTransform) {
        TransformedResult<S> then;
        Preconditions.checkState(!this.zak, "Result has already been consumed.");
        synchronized (this.zab) {
            boolean z = false;
            Preconditions.checkState(this.zao == null, "Cannot call then() twice.");
            Preconditions.checkState(this.zag == null, "Cannot call then() if callbacks are set.");
            if (!this.zal) {
                z = true;
            }
            Preconditions.checkState(z, "Cannot call then() if result was canceled.");
            this.zap = true;
            this.zao = new zacn<>(this.zad);
            then = this.zao.then(resultTransform);
            if (isReady()) {
                this.zac.zaa(this.zao, zac());
            } else {
                this.zag = this.zao;
            }
        }
        return then;
    }

    public final void setResult(R r) {
        synchronized (this.zab) {
            if (this.zam || this.zal) {
                zaa(r);
                return;
            }
            isReady();
            boolean z = true;
            Preconditions.checkState(!isReady(), "Results have already been set");
            if (this.zak) {
                z = false;
            }
            Preconditions.checkState(z, "Result has already been consumed");
            zab((BasePendingResult<R>) r);
        }
    }

    @Deprecated
    public final void forceFailureUnlessReady(Status status) {
        synchronized (this.zab) {
            if (!isReady()) {
                setResult(createFailedResult(status));
                this.zam = true;
            }
        }
    }

    public final void zaa(zacq zacqVar) {
        this.zah.set(zacqVar);
    }

    protected final void setCancelToken(ICancelToken iCancelToken) {
        synchronized (this.zab) {
            this.zan = iCancelToken;
        }
    }

    public final void zab() {
        this.zap = this.zap || zaa.get().booleanValue();
    }

    private final R zac() {
        R r;
        synchronized (this.zab) {
            Preconditions.checkState(!this.zak, "Result has already been consumed.");
            Preconditions.checkState(isReady(), "Result is not ready.");
            r = this.zai;
            this.zai = null;
            this.zag = null;
            this.zak = true;
        }
        zacq andSet = this.zah.getAndSet(null);
        if (andSet != null) {
            andSet.zaa(this);
        }
        return (R) Preconditions.checkNotNull(r);
    }

    private final void zab(R r) {
        this.zai = r;
        this.zaj = r.getStatus();
        this.zan = null;
        this.zae.countDown();
        if (this.zal) {
            this.zag = null;
        } else {
            ResultCallback<? super R> resultCallback = this.zag;
            if (resultCallback == null) {
                if (this.zai instanceof Releasable) {
                    this.mResultGuardian = new zaa(this, null);
                }
            } else {
                this.zac.removeMessages(2);
                this.zac.zaa(resultCallback, zac());
            }
        }
        ArrayList<PendingResult.StatusListener> arrayList = this.zaf;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            PendingResult.StatusListener statusListener = arrayList.get(i);
            i++;
            statusListener.onComplete(this.zaj);
        }
        this.zaf.clear();
    }

    public static void zaa(Result result) {
        if (result instanceof Releasable) {
            try {
                ((Releasable) result).release();
            } catch (RuntimeException e) {
                String valueOf = String.valueOf(result);
                StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 18);
                sb.append("Unable to release ");
                sb.append(valueOf);
                Log.w("BasePendingResult", sb.toString(), e);
            }
        }
    }
}
