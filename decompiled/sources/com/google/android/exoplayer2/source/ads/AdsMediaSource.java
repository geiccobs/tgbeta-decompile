package com.google.android.exoplayer2.source.ads;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.CompositeMediaSource;
import com.google.android.exoplayer2.source.MaskingMediaPeriod;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.ads.AdsLoader;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public final class AdsMediaSource extends CompositeMediaSource<MediaSource.MediaPeriodId> {
    private static final MediaSource.MediaPeriodId DUMMY_CONTENT_MEDIA_PERIOD_ID = new MediaSource.MediaPeriodId(new Object());
    private final MediaSourceFactory adMediaSourceFactory;
    private AdMediaSourceHolder[][] adMediaSourceHolders;
    private AdPlaybackState adPlaybackState;
    private final AdsLoader.AdViewProvider adViewProvider;
    private final AdsLoader adsLoader;
    private ComponentListener componentListener;
    private final MediaSource contentMediaSource;
    private Timeline contentTimeline;
    private final Handler mainHandler;
    private final Timeline.Period period;

    /* loaded from: classes3.dex */
    public static final class AdLoadException extends IOException {
        public static final int TYPE_AD = 0;
        public static final int TYPE_AD_GROUP = 1;
        public static final int TYPE_ALL_ADS = 2;
        public static final int TYPE_UNEXPECTED = 3;
        public final int type;

        @Documented
        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface Type {
        }

        public static AdLoadException createForAd(Exception error) {
            return new AdLoadException(0, error);
        }

        public static AdLoadException createForAdGroup(Exception error, int adGroupIndex) {
            return new AdLoadException(1, new IOException("Failed to load ad group " + adGroupIndex, error));
        }

        public static AdLoadException createForAllAds(Exception error) {
            return new AdLoadException(2, error);
        }

        public static AdLoadException createForUnexpected(RuntimeException error) {
            return new AdLoadException(3, error);
        }

        private AdLoadException(int type, Exception cause) {
            super(cause);
            this.type = type;
        }

        public RuntimeException getRuntimeExceptionForUnexpected() {
            Assertions.checkState(this.type == 3);
            return (RuntimeException) Assertions.checkNotNull(getCause());
        }
    }

    public AdsMediaSource(MediaSource contentMediaSource, DataSource.Factory dataSourceFactory, AdsLoader adsLoader, AdsLoader.AdViewProvider adViewProvider) {
        this(contentMediaSource, new ProgressiveMediaSource.Factory(dataSourceFactory), adsLoader, adViewProvider);
    }

    public AdsMediaSource(MediaSource contentMediaSource, MediaSourceFactory adMediaSourceFactory, AdsLoader adsLoader, AdsLoader.AdViewProvider adViewProvider) {
        this.contentMediaSource = contentMediaSource;
        this.adMediaSourceFactory = adMediaSourceFactory;
        this.adsLoader = adsLoader;
        this.adViewProvider = adViewProvider;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.period = new Timeline.Period();
        this.adMediaSourceHolders = new AdMediaSourceHolder[0];
        adsLoader.setSupportedContentTypes(adMediaSourceFactory.getSupportedTypes());
    }

    @Override // com.google.android.exoplayer2.source.BaseMediaSource, com.google.android.exoplayer2.source.MediaSource
    public Object getTag() {
        return this.contentMediaSource.getTag();
    }

    @Override // com.google.android.exoplayer2.source.CompositeMediaSource, com.google.android.exoplayer2.source.BaseMediaSource
    public void prepareSourceInternal(TransferListener mediaTransferListener) {
        super.prepareSourceInternal(mediaTransferListener);
        final ComponentListener componentListener = new ComponentListener();
        this.componentListener = componentListener;
        prepareChildSource(DUMMY_CONTENT_MEDIA_PERIOD_ID, this.contentMediaSource);
        this.mainHandler.post(new Runnable() { // from class: com.google.android.exoplayer2.source.ads.AdsMediaSource$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                AdsMediaSource.this.m68x4dfad2b6(componentListener);
            }
        });
    }

    /* renamed from: lambda$prepareSourceInternal$0$com-google-android-exoplayer2-source-ads-AdsMediaSource */
    public /* synthetic */ void m68x4dfad2b6(ComponentListener componentListener) {
        this.adsLoader.start(componentListener, this.adViewProvider);
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public MediaPeriod createPeriod(MediaSource.MediaPeriodId id, Allocator allocator, long startPositionUs) {
        AdMediaSourceHolder adMediaSourceHolder;
        AdPlaybackState adPlaybackState = (AdPlaybackState) Assertions.checkNotNull(this.adPlaybackState);
        if (adPlaybackState.adGroupCount > 0 && id.isAd()) {
            int adGroupIndex = id.adGroupIndex;
            int adIndexInAdGroup = id.adIndexInAdGroup;
            Uri adUri = (Uri) Assertions.checkNotNull(adPlaybackState.adGroups[adGroupIndex].uris[adIndexInAdGroup]);
            AdMediaSourceHolder[][] adMediaSourceHolderArr = this.adMediaSourceHolders;
            if (adMediaSourceHolderArr[adGroupIndex].length <= adIndexInAdGroup) {
                int adCount = adIndexInAdGroup + 1;
                adMediaSourceHolderArr[adGroupIndex] = (AdMediaSourceHolder[]) Arrays.copyOf(adMediaSourceHolderArr[adGroupIndex], adCount);
            }
            AdMediaSourceHolder adMediaSourceHolder2 = this.adMediaSourceHolders[adGroupIndex][adIndexInAdGroup];
            if (adMediaSourceHolder2 != null) {
                adMediaSourceHolder = adMediaSourceHolder2;
            } else {
                MediaSource adMediaSource = this.adMediaSourceFactory.createMediaSource(adUri);
                AdMediaSourceHolder adMediaSourceHolder3 = new AdMediaSourceHolder(adMediaSource);
                this.adMediaSourceHolders[adGroupIndex][adIndexInAdGroup] = adMediaSourceHolder3;
                prepareChildSource(id, adMediaSource);
                adMediaSourceHolder = adMediaSourceHolder3;
            }
            return adMediaSourceHolder.createMediaPeriod(adUri, id, allocator, startPositionUs);
        }
        MaskingMediaPeriod mediaPeriod = new MaskingMediaPeriod(this.contentMediaSource, id, allocator, startPositionUs);
        mediaPeriod.createPeriod(id);
        return mediaPeriod;
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public void releasePeriod(MediaPeriod mediaPeriod) {
        MaskingMediaPeriod maskingMediaPeriod = (MaskingMediaPeriod) mediaPeriod;
        MediaSource.MediaPeriodId id = maskingMediaPeriod.id;
        if (id.isAd()) {
            AdMediaSourceHolder adMediaSourceHolder = (AdMediaSourceHolder) Assertions.checkNotNull(this.adMediaSourceHolders[id.adGroupIndex][id.adIndexInAdGroup]);
            adMediaSourceHolder.releaseMediaPeriod(maskingMediaPeriod);
            if (adMediaSourceHolder.isInactive()) {
                releaseChildSource(id);
                this.adMediaSourceHolders[id.adGroupIndex][id.adIndexInAdGroup] = null;
                return;
            }
            return;
        }
        maskingMediaPeriod.releasePeriod();
    }

    @Override // com.google.android.exoplayer2.source.CompositeMediaSource, com.google.android.exoplayer2.source.BaseMediaSource
    public void releaseSourceInternal() {
        super.releaseSourceInternal();
        ((ComponentListener) Assertions.checkNotNull(this.componentListener)).release();
        this.componentListener = null;
        this.contentTimeline = null;
        this.adPlaybackState = null;
        this.adMediaSourceHolders = new AdMediaSourceHolder[0];
        Handler handler = this.mainHandler;
        final AdsLoader adsLoader = this.adsLoader;
        adsLoader.getClass();
        handler.post(new Runnable() { // from class: com.google.android.exoplayer2.source.ads.AdsMediaSource$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AdsLoader.this.stop();
            }
        });
    }

    public void onChildSourceInfoRefreshed(MediaSource.MediaPeriodId mediaPeriodId, MediaSource mediaSource, Timeline timeline) {
        if (mediaPeriodId.isAd()) {
            int adGroupIndex = mediaPeriodId.adGroupIndex;
            int adIndexInAdGroup = mediaPeriodId.adIndexInAdGroup;
            ((AdMediaSourceHolder) Assertions.checkNotNull(this.adMediaSourceHolders[adGroupIndex][adIndexInAdGroup])).handleSourceInfoRefresh(timeline);
        } else {
            boolean z = true;
            if (timeline.getPeriodCount() != 1) {
                z = false;
            }
            Assertions.checkArgument(z);
            this.contentTimeline = timeline;
        }
        maybeUpdateSourceInfo();
    }

    public MediaSource.MediaPeriodId getMediaPeriodIdForChildMediaPeriodId(MediaSource.MediaPeriodId childId, MediaSource.MediaPeriodId mediaPeriodId) {
        return childId.isAd() ? childId : mediaPeriodId;
    }

    public void onAdPlaybackState(AdPlaybackState adPlaybackState) {
        if (this.adPlaybackState == null) {
            AdMediaSourceHolder[][] adMediaSourceHolderArr = new AdMediaSourceHolder[adPlaybackState.adGroupCount];
            this.adMediaSourceHolders = adMediaSourceHolderArr;
            Arrays.fill(adMediaSourceHolderArr, new AdMediaSourceHolder[0]);
        }
        this.adPlaybackState = adPlaybackState;
        maybeUpdateSourceInfo();
    }

    private void maybeUpdateSourceInfo() {
        Timeline timeline;
        Timeline contentTimeline = this.contentTimeline;
        AdPlaybackState adPlaybackState = this.adPlaybackState;
        if (adPlaybackState != null && contentTimeline != null) {
            AdPlaybackState withAdDurationsUs = adPlaybackState.withAdDurationsUs(getAdDurationsUs());
            this.adPlaybackState = withAdDurationsUs;
            if (withAdDurationsUs.adGroupCount == 0) {
                timeline = contentTimeline;
            } else {
                timeline = new SinglePeriodAdTimeline(contentTimeline, this.adPlaybackState);
            }
            refreshSourceInfo(timeline);
        }
    }

    private long[][] getAdDurationsUs() {
        long[][] adDurationsUs = new long[this.adMediaSourceHolders.length];
        int i = 0;
        while (true) {
            AdMediaSourceHolder[][] adMediaSourceHolderArr = this.adMediaSourceHolders;
            if (i < adMediaSourceHolderArr.length) {
                adDurationsUs[i] = new long[adMediaSourceHolderArr[i].length];
                int j = 0;
                while (true) {
                    AdMediaSourceHolder[][] adMediaSourceHolderArr2 = this.adMediaSourceHolders;
                    if (j < adMediaSourceHolderArr2[i].length) {
                        AdMediaSourceHolder holder = adMediaSourceHolderArr2[i][j];
                        adDurationsUs[i][j] = holder == null ? C.TIME_UNSET : holder.getDurationUs();
                        j++;
                    }
                }
                i++;
            } else {
                return adDurationsUs;
            }
        }
    }

    /* loaded from: classes3.dex */
    public final class ComponentListener implements AdsLoader.EventListener {
        private final Handler playerHandler = new Handler();
        private volatile boolean released;

        @Override // com.google.android.exoplayer2.source.ads.AdsLoader.EventListener
        public /* synthetic */ void onAdClicked() {
            AdsLoader.EventListener.CC.$default$onAdClicked(this);
        }

        @Override // com.google.android.exoplayer2.source.ads.AdsLoader.EventListener
        public /* synthetic */ void onAdTapped() {
            AdsLoader.EventListener.CC.$default$onAdTapped(this);
        }

        public ComponentListener() {
            AdsMediaSource.this = r1;
        }

        public void release() {
            this.released = true;
            this.playerHandler.removeCallbacksAndMessages(null);
        }

        @Override // com.google.android.exoplayer2.source.ads.AdsLoader.EventListener
        public void onAdPlaybackState(final AdPlaybackState adPlaybackState) {
            if (this.released) {
                return;
            }
            this.playerHandler.post(new Runnable() { // from class: com.google.android.exoplayer2.source.ads.AdsMediaSource$ComponentListener$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AdsMediaSource.ComponentListener.this.m70x9c1f3358(adPlaybackState);
                }
            });
        }

        /* renamed from: lambda$onAdPlaybackState$0$com-google-android-exoplayer2-source-ads-AdsMediaSource$ComponentListener */
        public /* synthetic */ void m70x9c1f3358(AdPlaybackState adPlaybackState) {
            if (!this.released) {
                AdsMediaSource.this.onAdPlaybackState(adPlaybackState);
            }
        }

        @Override // com.google.android.exoplayer2.source.ads.AdsLoader.EventListener
        public void onAdLoadError(AdLoadException error, DataSpec dataSpec) {
            if (this.released) {
                return;
            }
            AdsMediaSource.this.createEventDispatcher(null).loadError(dataSpec, dataSpec.uri, Collections.emptyMap(), 6, SystemClock.elapsedRealtime(), 0L, 0L, error, true);
        }
    }

    /* loaded from: classes3.dex */
    public final class AdPrepareErrorListener implements MaskingMediaPeriod.PrepareErrorListener {
        private final int adGroupIndex;
        private final int adIndexInAdGroup;
        private final Uri adUri;

        public AdPrepareErrorListener(Uri adUri, int adGroupIndex, int adIndexInAdGroup) {
            AdsMediaSource.this = r1;
            this.adUri = adUri;
            this.adGroupIndex = adGroupIndex;
            this.adIndexInAdGroup = adIndexInAdGroup;
        }

        @Override // com.google.android.exoplayer2.source.MaskingMediaPeriod.PrepareErrorListener
        public void onPrepareError(MediaSource.MediaPeriodId mediaPeriodId, final IOException exception) {
            AdsMediaSource.this.createEventDispatcher(mediaPeriodId).loadError(new DataSpec(this.adUri), this.adUri, Collections.emptyMap(), 6, -1L, 0L, 0L, AdLoadException.createForAd(exception), true);
            AdsMediaSource.this.mainHandler.post(new Runnable() { // from class: com.google.android.exoplayer2.source.ads.AdsMediaSource$AdPrepareErrorListener$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AdsMediaSource.AdPrepareErrorListener.this.m69xf7f1398b(exception);
                }
            });
        }

        /* renamed from: lambda$onPrepareError$0$com-google-android-exoplayer2-source-ads-AdsMediaSource$AdPrepareErrorListener */
        public /* synthetic */ void m69xf7f1398b(IOException exception) {
            AdsMediaSource.this.adsLoader.handlePrepareError(this.adGroupIndex, this.adIndexInAdGroup, exception);
        }
    }

    /* loaded from: classes3.dex */
    public final class AdMediaSourceHolder {
        private final List<MaskingMediaPeriod> activeMediaPeriods = new ArrayList();
        private final MediaSource adMediaSource;
        private Timeline timeline;

        public AdMediaSourceHolder(MediaSource adMediaSource) {
            AdsMediaSource.this = r1;
            this.adMediaSource = adMediaSource;
        }

        public MediaPeriod createMediaPeriod(Uri adUri, MediaSource.MediaPeriodId id, Allocator allocator, long startPositionUs) {
            MaskingMediaPeriod maskingMediaPeriod = new MaskingMediaPeriod(this.adMediaSource, id, allocator, startPositionUs);
            maskingMediaPeriod.setPrepareErrorListener(new AdPrepareErrorListener(adUri, id.adGroupIndex, id.adIndexInAdGroup));
            this.activeMediaPeriods.add(maskingMediaPeriod);
            Timeline timeline = this.timeline;
            if (timeline != null) {
                Object periodUid = timeline.getUidOfPeriod(0);
                MediaSource.MediaPeriodId adSourceMediaPeriodId = new MediaSource.MediaPeriodId(periodUid, id.windowSequenceNumber);
                maskingMediaPeriod.createPeriod(adSourceMediaPeriodId);
            }
            return maskingMediaPeriod;
        }

        public void handleSourceInfoRefresh(Timeline timeline) {
            boolean z = true;
            if (timeline.getPeriodCount() != 1) {
                z = false;
            }
            Assertions.checkArgument(z);
            if (this.timeline == null) {
                Object periodUid = timeline.getUidOfPeriod(0);
                for (int i = 0; i < this.activeMediaPeriods.size(); i++) {
                    MaskingMediaPeriod mediaPeriod = this.activeMediaPeriods.get(i);
                    MediaSource.MediaPeriodId adSourceMediaPeriodId = new MediaSource.MediaPeriodId(periodUid, mediaPeriod.id.windowSequenceNumber);
                    mediaPeriod.createPeriod(adSourceMediaPeriodId);
                }
            }
            this.timeline = timeline;
        }

        public long getDurationUs() {
            Timeline timeline = this.timeline;
            if (timeline == null) {
                return C.TIME_UNSET;
            }
            return timeline.getPeriod(0, AdsMediaSource.this.period).getDurationUs();
        }

        public void releaseMediaPeriod(MaskingMediaPeriod maskingMediaPeriod) {
            this.activeMediaPeriods.remove(maskingMediaPeriod);
            maskingMediaPeriod.releasePeriod();
        }

        public boolean isInactive() {
            return this.activeMediaPeriods.isEmpty();
        }
    }
}
