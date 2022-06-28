package com.google.android.exoplayer2.upstream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.EventDispatcher;
import com.google.android.exoplayer2.util.SlidingPercentile;
import com.google.android.exoplayer2.util.Util;
import com.microsoft.appcenter.distribute.DistributeConstants;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes3.dex */
public final class DefaultBandwidthMeter implements BandwidthMeter, TransferListener {
    private static final int BYTES_TRANSFERRED_FOR_ESTIMATE = 524288;
    public static final long DEFAULT_INITIAL_BITRATE_ESTIMATE = 1000000;
    public static final int DEFAULT_SLIDING_WINDOW_MAX_WEIGHT = 2000;
    private static final int ELAPSED_MILLIS_FOR_ESTIMATE = 2000;
    private static DefaultBandwidthMeter singletonInstance;
    private long bitrateEstimate;
    private final Clock clock;
    private final Context context;
    private final EventDispatcher<BandwidthMeter.EventListener> eventDispatcher;
    private final SparseArray<Long> initialBitrateEstimates;
    private long lastReportedBitrateEstimate;
    private int networkType;
    private int networkTypeOverride;
    private boolean networkTypeOverrideSet;
    private long sampleBytesTransferred;
    private long sampleStartTimeMs;
    private final SlidingPercentile slidingPercentile;
    private int streamCount;
    private long totalBytesTransferred;
    private long totalElapsedTimeMs;
    public static final Map<String, int[]> DEFAULT_INITIAL_BITRATE_COUNTRY_GROUPS = createInitialBitrateCountryGroupAssignment();
    public static final long[] DEFAULT_INITIAL_BITRATE_ESTIMATES_WIFI = {5800000, 3500000, 1900000, 1000000, 520000};
    public static final long[] DEFAULT_INITIAL_BITRATE_ESTIMATES_2G = {204000, 154000, 139000, 122000, 102000};
    public static final long[] DEFAULT_INITIAL_BITRATE_ESTIMATES_3G = {2200000, 1150000, 810000, 640000, 450000};
    public static final long[] DEFAULT_INITIAL_BITRATE_ESTIMATES_4G = {4900000, 2300000, 1500000, 970000, 540000};

    /* loaded from: classes3.dex */
    public static final class Builder {
        private Clock clock;
        private final Context context;
        private SparseArray<Long> initialBitrateEstimates;
        private boolean resetOnNetworkTypeChange;
        private int slidingWindowMaxWeight;

        public Builder(Context context) {
            this.context = context == null ? null : context.getApplicationContext();
            this.initialBitrateEstimates = getInitialBitrateEstimatesForCountry(Util.getCountryCode(context));
            this.slidingWindowMaxWeight = 2000;
            this.clock = Clock.DEFAULT;
            this.resetOnNetworkTypeChange = true;
        }

        public Builder setSlidingWindowMaxWeight(int slidingWindowMaxWeight) {
            this.slidingWindowMaxWeight = slidingWindowMaxWeight;
            return this;
        }

        public Builder setInitialBitrateEstimate(long initialBitrateEstimate) {
            for (int i = 0; i < this.initialBitrateEstimates.size(); i++) {
                this.initialBitrateEstimates.setValueAt(i, Long.valueOf(initialBitrateEstimate));
            }
            return this;
        }

        public Builder setInitialBitrateEstimate(int networkType, long initialBitrateEstimate) {
            this.initialBitrateEstimates.put(networkType, Long.valueOf(initialBitrateEstimate));
            return this;
        }

        public Builder setInitialBitrateEstimate(String countryCode) {
            this.initialBitrateEstimates = getInitialBitrateEstimatesForCountry(Util.toUpperInvariant(countryCode));
            return this;
        }

        public Builder setClock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public Builder setResetOnNetworkTypeChange(boolean resetOnNetworkTypeChange) {
            this.resetOnNetworkTypeChange = resetOnNetworkTypeChange;
            return this;
        }

        public DefaultBandwidthMeter build() {
            return new DefaultBandwidthMeter(this.context, this.initialBitrateEstimates, this.slidingWindowMaxWeight, this.clock, this.resetOnNetworkTypeChange);
        }

        private static SparseArray<Long> getInitialBitrateEstimatesForCountry(String countryCode) {
            int[] groupIndices = getCountryGroupIndices(countryCode);
            SparseArray<Long> result = new SparseArray<>(6);
            result.append(0, 1000000L);
            result.append(2, Long.valueOf(DefaultBandwidthMeter.DEFAULT_INITIAL_BITRATE_ESTIMATES_WIFI[groupIndices[0]]));
            result.append(3, Long.valueOf(DefaultBandwidthMeter.DEFAULT_INITIAL_BITRATE_ESTIMATES_2G[groupIndices[1]]));
            result.append(4, Long.valueOf(DefaultBandwidthMeter.DEFAULT_INITIAL_BITRATE_ESTIMATES_3G[groupIndices[2]]));
            result.append(5, Long.valueOf(DefaultBandwidthMeter.DEFAULT_INITIAL_BITRATE_ESTIMATES_4G[groupIndices[3]]));
            result.append(7, Long.valueOf(DefaultBandwidthMeter.DEFAULT_INITIAL_BITRATE_ESTIMATES_WIFI[groupIndices[0]]));
            result.append(9, Long.valueOf(DefaultBandwidthMeter.DEFAULT_INITIAL_BITRATE_ESTIMATES_4G[groupIndices[3]]));
            return result;
        }

        private static int[] getCountryGroupIndices(String countryCode) {
            int[] groupIndices = DefaultBandwidthMeter.DEFAULT_INITIAL_BITRATE_COUNTRY_GROUPS.get(countryCode);
            return groupIndices == null ? new int[]{2, 2, 2, 2} : groupIndices;
        }
    }

    public static synchronized DefaultBandwidthMeter getSingletonInstance(Context context) {
        DefaultBandwidthMeter defaultBandwidthMeter;
        synchronized (DefaultBandwidthMeter.class) {
            if (singletonInstance == null) {
                singletonInstance = new Builder(context).build();
            }
            defaultBandwidthMeter = singletonInstance;
        }
        return defaultBandwidthMeter;
    }

    @Deprecated
    public DefaultBandwidthMeter() {
        this(null, new SparseArray(), 2000, Clock.DEFAULT, false);
    }

    private DefaultBandwidthMeter(Context context, SparseArray<Long> initialBitrateEstimates, int maxWeight, Clock clock, boolean resetOnNetworkTypeChange) {
        this.context = context == null ? null : context.getApplicationContext();
        this.initialBitrateEstimates = initialBitrateEstimates;
        this.eventDispatcher = new EventDispatcher<>();
        this.slidingPercentile = new SlidingPercentile(maxWeight);
        this.clock = clock;
        int networkType = context == null ? 0 : Util.getNetworkType(context);
        this.networkType = networkType;
        this.bitrateEstimate = getInitialBitrateEstimateForNetworkType(networkType);
        if (context != null && resetOnNetworkTypeChange) {
            ConnectivityActionReceiver connectivityActionReceiver = ConnectivityActionReceiver.getInstance(context);
            connectivityActionReceiver.register(this);
        }
    }

    public synchronized void setNetworkTypeOverride(int networkType) {
        this.networkTypeOverride = networkType;
        this.networkTypeOverrideSet = true;
        onConnectivityAction();
    }

    @Override // com.google.android.exoplayer2.upstream.BandwidthMeter
    public synchronized long getBitrateEstimate() {
        return this.bitrateEstimate;
    }

    @Override // com.google.android.exoplayer2.upstream.BandwidthMeter
    public TransferListener getTransferListener() {
        return this;
    }

    @Override // com.google.android.exoplayer2.upstream.BandwidthMeter
    public void addEventListener(Handler eventHandler, BandwidthMeter.EventListener eventListener) {
        this.eventDispatcher.addListener(eventHandler, eventListener);
    }

    @Override // com.google.android.exoplayer2.upstream.BandwidthMeter
    public void removeEventListener(BandwidthMeter.EventListener eventListener) {
        this.eventDispatcher.removeListener(eventListener);
    }

    @Override // com.google.android.exoplayer2.upstream.TransferListener
    public void onTransferInitializing(DataSource source, DataSpec dataSpec, boolean isNetwork) {
    }

    @Override // com.google.android.exoplayer2.upstream.TransferListener
    public synchronized void onTransferStart(DataSource source, DataSpec dataSpec, boolean isNetwork) {
        if (!isNetwork) {
            return;
        }
        if (this.streamCount == 0) {
            this.sampleStartTimeMs = this.clock.elapsedRealtime();
        }
        this.streamCount++;
    }

    @Override // com.google.android.exoplayer2.upstream.TransferListener
    public synchronized void onBytesTransferred(DataSource source, DataSpec dataSpec, boolean isNetwork, int bytes) {
        if (!isNetwork) {
            return;
        }
        this.sampleBytesTransferred += bytes;
    }

    @Override // com.google.android.exoplayer2.upstream.TransferListener
    public synchronized void onTransferEnd(DataSource source, DataSpec dataSpec, boolean isNetwork) {
        if (!isNetwork) {
            return;
        }
        Assertions.checkState(this.streamCount > 0);
        long nowMs = this.clock.elapsedRealtime();
        int sampleElapsedTimeMs = (int) (nowMs - this.sampleStartTimeMs);
        this.totalElapsedTimeMs += sampleElapsedTimeMs;
        long j = this.totalBytesTransferred;
        long j2 = this.sampleBytesTransferred;
        this.totalBytesTransferred = j + j2;
        if (sampleElapsedTimeMs > 0) {
            float bitsPerSecond = (((float) j2) * 8000.0f) / sampleElapsedTimeMs;
            this.slidingPercentile.addSample((int) Math.sqrt(j2), bitsPerSecond);
            if (this.totalElapsedTimeMs >= AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS || this.totalBytesTransferred >= DistributeConstants.UPDATE_PROGRESS_BYTES_THRESHOLD) {
                this.bitrateEstimate = this.slidingPercentile.getPercentile(0.5f);
            }
            maybeNotifyBandwidthSample(sampleElapsedTimeMs, this.sampleBytesTransferred, this.bitrateEstimate);
            this.sampleStartTimeMs = nowMs;
            this.sampleBytesTransferred = 0L;
        }
        this.streamCount--;
    }

    public synchronized void onConnectivityAction() {
        int networkType;
        if (this.networkTypeOverrideSet) {
            networkType = this.networkTypeOverride;
        } else {
            Context context = this.context;
            networkType = context == null ? 0 : Util.getNetworkType(context);
        }
        if (this.networkType == networkType) {
            return;
        }
        this.networkType = networkType;
        if (networkType != 1 && networkType != 0 && networkType != 8) {
            this.bitrateEstimate = getInitialBitrateEstimateForNetworkType(networkType);
            long nowMs = this.clock.elapsedRealtime();
            int sampleElapsedTimeMs = this.streamCount > 0 ? (int) (nowMs - this.sampleStartTimeMs) : 0;
            maybeNotifyBandwidthSample(sampleElapsedTimeMs, this.sampleBytesTransferred, this.bitrateEstimate);
            this.sampleStartTimeMs = nowMs;
            this.sampleBytesTransferred = 0L;
            this.totalBytesTransferred = 0L;
            this.totalElapsedTimeMs = 0L;
            this.slidingPercentile.reset();
        }
    }

    private void maybeNotifyBandwidthSample(final int elapsedMs, final long bytesTransferred, final long bitrateEstimate) {
        if (elapsedMs == 0 && bytesTransferred == 0 && bitrateEstimate == this.lastReportedBitrateEstimate) {
            return;
        }
        this.lastReportedBitrateEstimate = bitrateEstimate;
        this.eventDispatcher.dispatch(new EventDispatcher.Event() { // from class: com.google.android.exoplayer2.upstream.DefaultBandwidthMeter$$ExternalSyntheticLambda0
            @Override // com.google.android.exoplayer2.util.EventDispatcher.Event
            public final void sendTo(Object obj) {
                ((BandwidthMeter.EventListener) obj).onBandwidthSample(elapsedMs, bytesTransferred, bitrateEstimate);
            }
        });
    }

    private long getInitialBitrateEstimateForNetworkType(int networkType) {
        Long initialBitrateEstimate = this.initialBitrateEstimates.get(networkType);
        if (initialBitrateEstimate == null) {
            initialBitrateEstimate = this.initialBitrateEstimates.get(0);
        }
        if (initialBitrateEstimate == null) {
            initialBitrateEstimate = 1000000L;
        }
        return initialBitrateEstimate.longValue();
    }

    /* loaded from: classes3.dex */
    public static class ConnectivityActionReceiver extends BroadcastReceiver {
        private static ConnectivityActionReceiver staticInstance;
        private final Handler mainHandler = new Handler(Looper.getMainLooper());
        private final ArrayList<WeakReference<DefaultBandwidthMeter>> bandwidthMeters = new ArrayList<>();

        public static synchronized ConnectivityActionReceiver getInstance(Context context) {
            ConnectivityActionReceiver connectivityActionReceiver;
            synchronized (ConnectivityActionReceiver.class) {
                if (staticInstance == null) {
                    staticInstance = new ConnectivityActionReceiver();
                    IntentFilter filter = new IntentFilter();
                    filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                    context.registerReceiver(staticInstance, filter);
                }
                connectivityActionReceiver = staticInstance;
            }
            return connectivityActionReceiver;
        }

        private ConnectivityActionReceiver() {
        }

        public synchronized void register(final DefaultBandwidthMeter bandwidthMeter) {
            removeClearedReferences();
            this.bandwidthMeters.add(new WeakReference<>(bandwidthMeter));
            this.mainHandler.post(new Runnable() { // from class: com.google.android.exoplayer2.upstream.DefaultBandwidthMeter$ConnectivityActionReceiver$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DefaultBandwidthMeter.ConnectivityActionReceiver.this.m76xb89d478f(bandwidthMeter);
                }
            });
        }

        @Override // android.content.BroadcastReceiver
        public synchronized void onReceive(Context context, Intent intent) {
            if (isInitialStickyBroadcast()) {
                return;
            }
            removeClearedReferences();
            for (int i = 0; i < this.bandwidthMeters.size(); i++) {
                WeakReference<DefaultBandwidthMeter> bandwidthMeterReference = this.bandwidthMeters.get(i);
                DefaultBandwidthMeter bandwidthMeter = bandwidthMeterReference.get();
                if (bandwidthMeter != null) {
                    m76xb89d478f(bandwidthMeter);
                }
            }
        }

        /* renamed from: updateBandwidthMeter */
        public void m76xb89d478f(DefaultBandwidthMeter bandwidthMeter) {
            bandwidthMeter.onConnectivityAction();
        }

        private void removeClearedReferences() {
            for (int i = this.bandwidthMeters.size() - 1; i >= 0; i--) {
                WeakReference<DefaultBandwidthMeter> bandwidthMeterReference = this.bandwidthMeters.get(i);
                DefaultBandwidthMeter bandwidthMeter = bandwidthMeterReference.get();
                if (bandwidthMeter == null) {
                    this.bandwidthMeters.remove(i);
                }
            }
        }
    }

    private static Map<String, int[]> createInitialBitrateCountryGroupAssignment() {
        HashMap<String, int[]> countryGroupAssignment = new HashMap<>();
        countryGroupAssignment.put("AD", new int[]{0, 2, 0, 0});
        countryGroupAssignment.put("AE", new int[]{2, 4, 4, 4});
        countryGroupAssignment.put("AF", new int[]{4, 4, 3, 3});
        countryGroupAssignment.put("AG", new int[]{4, 2, 2, 3});
        countryGroupAssignment.put("AI", new int[]{0, 3, 2, 4});
        countryGroupAssignment.put("AL", new int[]{1, 2, 0, 1});
        countryGroupAssignment.put("AM", new int[]{2, 2, 1, 2});
        countryGroupAssignment.put("AO", new int[]{3, 4, 3, 1});
        countryGroupAssignment.put("AQ", new int[]{4, 2, 2, 2});
        countryGroupAssignment.put("AR", new int[]{2, 3, 1, 2});
        countryGroupAssignment.put("AS", new int[]{2, 2, 4, 2});
        countryGroupAssignment.put("AT", new int[]{0, 3, 0, 0});
        countryGroupAssignment.put("AU", new int[]{0, 2, 0, 1});
        countryGroupAssignment.put("AW", new int[]{1, 1, 2, 4});
        countryGroupAssignment.put("AX", new int[]{0, 1, 0, 0});
        countryGroupAssignment.put("AZ", new int[]{3, 3, 3, 3});
        countryGroupAssignment.put("BA", new int[]{1, 1, 0, 1});
        countryGroupAssignment.put("BB", new int[]{0, 3, 0, 0});
        countryGroupAssignment.put("BD", new int[]{2, 0, 4, 3});
        countryGroupAssignment.put("BE", new int[]{0, 1, 2, 3});
        countryGroupAssignment.put("BF", new int[]{4, 4, 4, 1});
        countryGroupAssignment.put("BG", new int[]{0, 1, 0, 0});
        countryGroupAssignment.put("BH", new int[]{1, 0, 3, 4});
        countryGroupAssignment.put("BI", new int[]{4, 4, 4, 4});
        countryGroupAssignment.put("BJ", new int[]{4, 4, 3, 4});
        countryGroupAssignment.put("BL", new int[]{1, 0, 4, 3});
        countryGroupAssignment.put("BM", new int[]{0, 1, 0, 0});
        countryGroupAssignment.put("BN", new int[]{4, 0, 2, 4});
        countryGroupAssignment.put("BO", new int[]{1, 3, 3, 3});
        countryGroupAssignment.put("BQ", new int[]{1, 0, 1, 0});
        countryGroupAssignment.put("BR", new int[]{2, 4, 3, 1});
        countryGroupAssignment.put("BS", new int[]{3, 1, 1, 3});
        countryGroupAssignment.put("BT", new int[]{3, 0, 3, 1});
        countryGroupAssignment.put("BW", new int[]{3, 4, 3, 3});
        countryGroupAssignment.put("BY", new int[]{0, 1, 1, 1});
        countryGroupAssignment.put("BZ", new int[]{1, 3, 2, 1});
        countryGroupAssignment.put("CA", new int[]{0, 3, 2, 2});
        countryGroupAssignment.put("CD", new int[]{3, 4, 2, 2});
        countryGroupAssignment.put("CF", new int[]{4, 3, 2, 2});
        countryGroupAssignment.put("CG", new int[]{3, 4, 1, 1});
        countryGroupAssignment.put("CH", new int[]{0, 0, 0, 0});
        countryGroupAssignment.put("CI", new int[]{3, 4, 3, 3});
        countryGroupAssignment.put("CK", new int[]{2, 0, 1, 0});
        countryGroupAssignment.put("CL", new int[]{1, 2, 2, 3});
        countryGroupAssignment.put("CM", new int[]{3, 4, 3, 2});
        countryGroupAssignment.put("CN", new int[]{1, 0, 1, 1});
        countryGroupAssignment.put("CO", new int[]{2, 3, 3, 2});
        countryGroupAssignment.put("CR", new int[]{2, 2, 4, 4});
        countryGroupAssignment.put("CU", new int[]{4, 4, 2, 1});
        countryGroupAssignment.put("CV", new int[]{2, 3, 3, 2});
        countryGroupAssignment.put("CW", new int[]{1, 1, 0, 0});
        countryGroupAssignment.put("CY", new int[]{1, 1, 0, 0});
        countryGroupAssignment.put("CZ", new int[]{0, 1, 0, 0});
        countryGroupAssignment.put("DE", new int[]{0, 1, 2, 3});
        countryGroupAssignment.put("DJ", new int[]{4, 2, 4, 4});
        countryGroupAssignment.put("DK", new int[]{0, 0, 1, 0});
        countryGroupAssignment.put("DM", new int[]{1, 1, 0, 2});
        countryGroupAssignment.put("DO", new int[]{3, 3, 4, 4});
        countryGroupAssignment.put("DZ", new int[]{3, 3, 4, 4});
        countryGroupAssignment.put("EC", new int[]{2, 3, 4, 2});
        countryGroupAssignment.put("EE", new int[]{0, 0, 0, 0});
        countryGroupAssignment.put("EG", new int[]{3, 4, 2, 1});
        countryGroupAssignment.put("EH", new int[]{2, 0, 3, 1});
        countryGroupAssignment.put("ER", new int[]{4, 2, 4, 4});
        countryGroupAssignment.put("ES", new int[]{0, 1, 1, 1});
        countryGroupAssignment.put("ET", new int[]{4, 4, 4, 1});
        countryGroupAssignment.put("FI", new int[]{0, 0, 1, 0});
        countryGroupAssignment.put("FJ", new int[]{3, 0, 4, 4});
        countryGroupAssignment.put("FK", new int[]{2, 2, 2, 1});
        countryGroupAssignment.put("FM", new int[]{3, 2, 4, 1});
        countryGroupAssignment.put("FO", new int[]{1, 1, 0, 0});
        countryGroupAssignment.put("FR", new int[]{1, 1, 1, 1});
        countryGroupAssignment.put("GA", new int[]{3, 2, 2, 2});
        countryGroupAssignment.put("GB", new int[]{0, 1, 1, 1});
        countryGroupAssignment.put("GD", new int[]{1, 1, 3, 1});
        countryGroupAssignment.put("GE", new int[]{1, 0, 1, 4});
        countryGroupAssignment.put("GF", new int[]{2, 0, 1, 3});
        countryGroupAssignment.put("GG", new int[]{1, 0, 0, 0});
        countryGroupAssignment.put("GH", new int[]{3, 3, 3, 3});
        countryGroupAssignment.put("GI", new int[]{4, 4, 0, 0});
        countryGroupAssignment.put("GL", new int[]{2, 1, 1, 2});
        countryGroupAssignment.put("GM", new int[]{4, 3, 2, 4});
        countryGroupAssignment.put("GN", new int[]{3, 4, 4, 2});
        countryGroupAssignment.put("GP", new int[]{2, 1, 3, 4});
        countryGroupAssignment.put("GQ", new int[]{4, 4, 4, 0});
        countryGroupAssignment.put("GR", new int[]{1, 1, 0, 1});
        countryGroupAssignment.put("GT", new int[]{3, 2, 2, 2});
        countryGroupAssignment.put("GU", new int[]{1, 0, 2, 2});
        countryGroupAssignment.put("GW", new int[]{3, 4, 4, 3});
        countryGroupAssignment.put("GY", new int[]{3, 2, 1, 1});
        countryGroupAssignment.put("HK", new int[]{0, 2, 3, 4});
        countryGroupAssignment.put("HN", new int[]{3, 1, 3, 3});
        countryGroupAssignment.put("HR", new int[]{1, 1, 0, 1});
        countryGroupAssignment.put("HT", new int[]{4, 4, 4, 4});
        countryGroupAssignment.put("HU", new int[]{0, 1, 0, 0});
        countryGroupAssignment.put("ID", new int[]{2, 2, 2, 3});
        countryGroupAssignment.put("IE", new int[]{1, 0, 1, 1});
        countryGroupAssignment.put("IL", new int[]{1, 0, 2, 3});
        countryGroupAssignment.put("IM", new int[]{0, 0, 0, 1});
        countryGroupAssignment.put("IN", new int[]{2, 2, 4, 3});
        countryGroupAssignment.put("IO", new int[]{4, 4, 2, 3});
        countryGroupAssignment.put("IQ", new int[]{3, 3, 4, 2});
        countryGroupAssignment.put("IR", new int[]{3, 0, 2, 1});
        countryGroupAssignment.put("IS", new int[]{0, 1, 0, 0});
        countryGroupAssignment.put("IT", new int[]{1, 1, 1, 2});
        countryGroupAssignment.put("JE", new int[]{1, 0, 0, 1});
        countryGroupAssignment.put("JM", new int[]{3, 3, 3, 4});
        countryGroupAssignment.put("JO", new int[]{1, 2, 1, 1});
        countryGroupAssignment.put("JP", new int[]{0, 2, 0, 0});
        countryGroupAssignment.put("KE", new int[]{3, 4, 3, 3});
        countryGroupAssignment.put("KG", new int[]{2, 0, 2, 2});
        countryGroupAssignment.put("KH", new int[]{1, 0, 4, 3});
        countryGroupAssignment.put("KI", new int[]{4, 4, 4, 0});
        countryGroupAssignment.put("KM", new int[]{4, 3, 2, 4});
        countryGroupAssignment.put("KN", new int[]{1, 0, 2, 4});
        countryGroupAssignment.put("KP", new int[]{4, 2, 0, 2});
        countryGroupAssignment.put("KR", new int[]{0, 1, 0, 1});
        countryGroupAssignment.put("KW", new int[]{2, 3, 1, 2});
        countryGroupAssignment.put("KY", new int[]{3, 1, 2, 3});
        countryGroupAssignment.put("KZ", new int[]{1, 2, 2, 2});
        countryGroupAssignment.put("LA", new int[]{2, 2, 1, 1});
        countryGroupAssignment.put("LB", new int[]{3, 2, 0, 0});
        countryGroupAssignment.put("LC", new int[]{1, 1, 0, 0});
        countryGroupAssignment.put("LI", new int[]{0, 0, 1, 1});
        countryGroupAssignment.put("LK", new int[]{2, 0, 2, 3});
        countryGroupAssignment.put("LR", new int[]{3, 4, 4, 2});
        countryGroupAssignment.put("LS", new int[]{3, 3, 2, 2});
        countryGroupAssignment.put("LT", new int[]{0, 0, 0, 0});
        countryGroupAssignment.put("LU", new int[]{0, 0, 0, 0});
        countryGroupAssignment.put("LV", new int[]{0, 0, 0, 0});
        countryGroupAssignment.put("LY", new int[]{3, 3, 4, 3});
        countryGroupAssignment.put("MA", new int[]{3, 2, 3, 2});
        countryGroupAssignment.put("MC", new int[]{0, 4, 0, 0});
        countryGroupAssignment.put("MD", new int[]{1, 1, 0, 0});
        countryGroupAssignment.put("ME", new int[]{1, 3, 1, 2});
        countryGroupAssignment.put("MF", new int[]{2, 3, 1, 1});
        countryGroupAssignment.put("MG", new int[]{3, 4, 2, 3});
        countryGroupAssignment.put("MH", new int[]{4, 0, 2, 4});
        countryGroupAssignment.put("MK", new int[]{1, 0, 0, 0});
        countryGroupAssignment.put("ML", new int[]{4, 4, 2, 0});
        countryGroupAssignment.put("MM", new int[]{3, 3, 2, 2});
        countryGroupAssignment.put("MN", new int[]{2, 3, 1, 1});
        countryGroupAssignment.put("MO", new int[]{0, 0, 4, 4});
        countryGroupAssignment.put("MP", new int[]{0, 2, 1, 2});
        countryGroupAssignment.put("MQ", new int[]{2, 1, 1, 3});
        countryGroupAssignment.put("MR", new int[]{4, 2, 4, 4});
        countryGroupAssignment.put("MS", new int[]{1, 4, 3, 4});
        countryGroupAssignment.put("MT", new int[]{0, 0, 0, 0});
        countryGroupAssignment.put("MU", new int[]{2, 2, 4, 4});
        countryGroupAssignment.put("MV", new int[]{4, 3, 2, 4});
        countryGroupAssignment.put("MW", new int[]{3, 1, 1, 1});
        countryGroupAssignment.put("MX", new int[]{2, 4, 3, 3});
        countryGroupAssignment.put("MY", new int[]{2, 1, 3, 3});
        countryGroupAssignment.put("MZ", new int[]{3, 3, 3, 3});
        countryGroupAssignment.put("NA", new int[]{4, 3, 3, 3});
        countryGroupAssignment.put("NC", new int[]{2, 0, 4, 4});
        countryGroupAssignment.put("NE", new int[]{4, 4, 4, 4});
        countryGroupAssignment.put("NF", new int[]{1, 2, 2, 0});
        countryGroupAssignment.put("NG", new int[]{3, 3, 2, 2});
        countryGroupAssignment.put("NI", new int[]{3, 2, 4, 3});
        countryGroupAssignment.put("NL", new int[]{0, 2, 3, 2});
        countryGroupAssignment.put("NO", new int[]{0, 2, 1, 0});
        countryGroupAssignment.put("NP", new int[]{2, 2, 2, 2});
        countryGroupAssignment.put("NR", new int[]{4, 0, 3, 2});
        countryGroupAssignment.put("NZ", new int[]{0, 0, 1, 2});
        countryGroupAssignment.put("OM", new int[]{2, 3, 0, 2});
        countryGroupAssignment.put("PA", new int[]{1, 3, 3, 3});
        countryGroupAssignment.put("PE", new int[]{2, 4, 4, 4});
        countryGroupAssignment.put("PF", new int[]{2, 1, 1, 1});
        countryGroupAssignment.put("PG", new int[]{4, 3, 3, 2});
        countryGroupAssignment.put("PH", new int[]{3, 0, 3, 4});
        countryGroupAssignment.put("PK", new int[]{3, 2, 3, 2});
        countryGroupAssignment.put("PL", new int[]{1, 0, 1, 2});
        countryGroupAssignment.put("PM", new int[]{0, 2, 2, 0});
        countryGroupAssignment.put("PR", new int[]{2, 2, 2, 2});
        countryGroupAssignment.put("PS", new int[]{3, 3, 1, 4});
        countryGroupAssignment.put("PT", new int[]{1, 1, 0, 0});
        countryGroupAssignment.put("PW", new int[]{1, 1, 3, 0});
        countryGroupAssignment.put("PY", new int[]{2, 0, 3, 3});
        countryGroupAssignment.put("QA", new int[]{2, 3, 1, 1});
        countryGroupAssignment.put("RE", new int[]{1, 0, 2, 2});
        countryGroupAssignment.put("RO", new int[]{0, 1, 1, 2});
        countryGroupAssignment.put("RS", new int[]{1, 2, 0, 0});
        countryGroupAssignment.put("RU", new int[]{0, 1, 0, 1});
        countryGroupAssignment.put("RW", new int[]{4, 4, 4, 4});
        countryGroupAssignment.put("SA", new int[]{2, 2, 2, 1});
        countryGroupAssignment.put("SB", new int[]{4, 4, 4, 1});
        countryGroupAssignment.put("SC", new int[]{4, 2, 0, 1});
        countryGroupAssignment.put("SD", new int[]{4, 4, 4, 4});
        countryGroupAssignment.put("SE", new int[]{0, 1, 0, 0});
        countryGroupAssignment.put("SG", new int[]{1, 0, 3, 3});
        countryGroupAssignment.put("SH", new int[]{4, 2, 2, 2});
        countryGroupAssignment.put("SI", new int[]{0, 1, 0, 0});
        countryGroupAssignment.put("SJ", new int[]{2, 2, 2, 4});
        countryGroupAssignment.put("SK", new int[]{0, 1, 0, 0});
        countryGroupAssignment.put("SL", new int[]{4, 3, 3, 1});
        countryGroupAssignment.put("SM", new int[]{0, 0, 1, 2});
        countryGroupAssignment.put("SN", new int[]{4, 4, 4, 3});
        countryGroupAssignment.put("SO", new int[]{3, 4, 3, 4});
        countryGroupAssignment.put("SR", new int[]{2, 2, 2, 1});
        countryGroupAssignment.put("SS", new int[]{4, 4, 4, 4});
        countryGroupAssignment.put("ST", new int[]{2, 3, 1, 2});
        countryGroupAssignment.put("SV", new int[]{2, 2, 4, 4});
        countryGroupAssignment.put("SX", new int[]{2, 4, 1, 0});
        countryGroupAssignment.put("SY", new int[]{4, 3, 1, 1});
        countryGroupAssignment.put("SZ", new int[]{4, 4, 3, 4});
        countryGroupAssignment.put("TC", new int[]{1, 2, 1, 0});
        countryGroupAssignment.put("TD", new int[]{4, 4, 4, 3});
        countryGroupAssignment.put("TG", new int[]{3, 2, 1, 0});
        countryGroupAssignment.put("TH", new int[]{1, 3, 3, 3});
        countryGroupAssignment.put("TJ", new int[]{4, 4, 4, 4});
        countryGroupAssignment.put("TL", new int[]{4, 2, 4, 4});
        countryGroupAssignment.put("TM", new int[]{4, 2, 2, 2});
        countryGroupAssignment.put("TN", new int[]{2, 1, 1, 1});
        countryGroupAssignment.put("TO", new int[]{4, 3, 4, 4});
        countryGroupAssignment.put("TR", new int[]{1, 2, 1, 1});
        countryGroupAssignment.put("TT", new int[]{1, 3, 2, 4});
        countryGroupAssignment.put("TV", new int[]{4, 2, 3, 4});
        countryGroupAssignment.put("TW", new int[]{0, 0, 0, 0});
        countryGroupAssignment.put("TZ", new int[]{3, 4, 3, 3});
        countryGroupAssignment.put("UA", new int[]{0, 3, 1, 1});
        countryGroupAssignment.put("UG", new int[]{3, 2, 2, 3});
        countryGroupAssignment.put("US", new int[]{0, 1, 2, 2});
        countryGroupAssignment.put("UY", new int[]{2, 1, 2, 2});
        countryGroupAssignment.put("UZ", new int[]{2, 2, 3, 2});
        countryGroupAssignment.put("VA", new int[]{0, 2, 2, 2});
        countryGroupAssignment.put("VC", new int[]{2, 3, 0, 2});
        countryGroupAssignment.put("VE", new int[]{4, 4, 4, 4});
        countryGroupAssignment.put("VG", new int[]{3, 1, 2, 4});
        countryGroupAssignment.put("VI", new int[]{1, 4, 4, 3});
        countryGroupAssignment.put("VN", new int[]{0, 1, 3, 4});
        countryGroupAssignment.put("VU", new int[]{4, 0, 3, 3});
        countryGroupAssignment.put("WS", new int[]{3, 2, 4, 3});
        countryGroupAssignment.put("XK", new int[]{1, 2, 1, 0});
        countryGroupAssignment.put("YE", new int[]{4, 4, 4, 3});
        countryGroupAssignment.put("YT", new int[]{2, 2, 2, 3});
        countryGroupAssignment.put("ZA", new int[]{2, 3, 2, 2});
        countryGroupAssignment.put("ZM", new int[]{3, 2, 3, 3});
        countryGroupAssignment.put("ZW", new int[]{3, 3, 2, 3});
        return Collections.unmodifiableMap(countryGroupAssignment);
    }
}
