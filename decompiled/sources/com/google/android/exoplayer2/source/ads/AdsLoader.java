package com.google.android.exoplayer2.source.ads;

import android.view.View;
import android.view.ViewGroup;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import java.io.IOException;
/* loaded from: classes3.dex */
public interface AdsLoader {

    /* loaded from: classes3.dex */
    public interface AdViewProvider {
        View[] getAdOverlayViews();

        ViewGroup getAdViewGroup();
    }

    void handlePrepareError(int i, int i2, IOException iOException);

    void release();

    void setPlayer(Player player);

    void setSupportedContentTypes(int... iArr);

    void start(EventListener eventListener, AdViewProvider adViewProvider);

    void stop();

    /* loaded from: classes3.dex */
    public interface EventListener {
        void onAdClicked();

        void onAdLoadError(AdsMediaSource.AdLoadException adLoadException, DataSpec dataSpec);

        void onAdPlaybackState(AdPlaybackState adPlaybackState);

        void onAdTapped();

        /* renamed from: com.google.android.exoplayer2.source.ads.AdsLoader$EventListener$-CC */
        /* loaded from: classes3.dex */
        public final /* synthetic */ class CC {
            public static void $default$onAdPlaybackState(EventListener _this, AdPlaybackState adPlaybackState) {
            }

            public static void $default$onAdLoadError(EventListener _this, AdsMediaSource.AdLoadException error, DataSpec dataSpec) {
            }

            public static void $default$onAdClicked(EventListener _this) {
            }

            public static void $default$onAdTapped(EventListener _this) {
            }
        }
    }
}
