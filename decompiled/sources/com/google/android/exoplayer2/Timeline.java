package com.google.android.exoplayer2;

import android.util.Pair;
import com.google.android.exoplayer2.source.ads.AdPlaybackState;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
/* loaded from: classes3.dex */
public abstract class Timeline {
    public static final Timeline EMPTY = new Timeline() { // from class: com.google.android.exoplayer2.Timeline.1
        @Override // com.google.android.exoplayer2.Timeline
        public int getWindowCount() {
            return 0;
        }

        @Override // com.google.android.exoplayer2.Timeline
        public Window getWindow(int windowIndex, Window window, long defaultPositionProjectionUs) {
            throw new IndexOutOfBoundsException();
        }

        @Override // com.google.android.exoplayer2.Timeline
        public int getPeriodCount() {
            return 0;
        }

        @Override // com.google.android.exoplayer2.Timeline
        public Period getPeriod(int periodIndex, Period period, boolean setIds) {
            throw new IndexOutOfBoundsException();
        }

        @Override // com.google.android.exoplayer2.Timeline
        public int getIndexOfPeriod(Object uid) {
            return -1;
        }

        @Override // com.google.android.exoplayer2.Timeline
        public Object getUidOfPeriod(int periodIndex) {
            throw new IndexOutOfBoundsException();
        }
    };

    public abstract int getIndexOfPeriod(Object obj);

    public abstract Period getPeriod(int i, Period period, boolean z);

    public abstract int getPeriodCount();

    public abstract Object getUidOfPeriod(int i);

    public abstract Window getWindow(int i, Window window, long j);

    public abstract int getWindowCount();

    /* loaded from: classes3.dex */
    public static final class Window {
        public static final Object SINGLE_WINDOW_UID = new Object();
        public long defaultPositionUs;
        public long durationUs;
        public int firstPeriodIndex;
        public boolean isDynamic;
        public boolean isLive;
        public boolean isSeekable;
        public int lastPeriodIndex;
        public Object manifest;
        public long positionInFirstPeriodUs;
        public long presentationStartTimeMs;
        public Object tag;
        public Object uid = SINGLE_WINDOW_UID;
        public long windowStartTimeMs;

        public Window set(Object uid, Object tag, Object manifest, long presentationStartTimeMs, long windowStartTimeMs, boolean isSeekable, boolean isDynamic, boolean isLive, long defaultPositionUs, long durationUs, int firstPeriodIndex, int lastPeriodIndex, long positionInFirstPeriodUs) {
            this.uid = uid;
            this.tag = tag;
            this.manifest = manifest;
            this.presentationStartTimeMs = presentationStartTimeMs;
            this.windowStartTimeMs = windowStartTimeMs;
            this.isSeekable = isSeekable;
            this.isDynamic = isDynamic;
            this.isLive = isLive;
            this.defaultPositionUs = defaultPositionUs;
            this.durationUs = durationUs;
            this.firstPeriodIndex = firstPeriodIndex;
            this.lastPeriodIndex = lastPeriodIndex;
            this.positionInFirstPeriodUs = positionInFirstPeriodUs;
            return this;
        }

        public long getDefaultPositionMs() {
            return C.usToMs(this.defaultPositionUs);
        }

        public long getDefaultPositionUs() {
            return this.defaultPositionUs;
        }

        public long getDurationMs() {
            return C.usToMs(this.durationUs);
        }

        public long getDurationUs() {
            return this.durationUs;
        }

        public long getPositionInFirstPeriodMs() {
            return C.usToMs(this.positionInFirstPeriodUs);
        }

        public long getPositionInFirstPeriodUs() {
            return this.positionInFirstPeriodUs;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || !getClass().equals(obj.getClass())) {
                return false;
            }
            Window that = (Window) obj;
            return Util.areEqual(this.uid, that.uid) && Util.areEqual(this.tag, that.tag) && Util.areEqual(this.manifest, that.manifest) && this.presentationStartTimeMs == that.presentationStartTimeMs && this.windowStartTimeMs == that.windowStartTimeMs && this.isSeekable == that.isSeekable && this.isDynamic == that.isDynamic && this.isLive == that.isLive && this.defaultPositionUs == that.defaultPositionUs && this.durationUs == that.durationUs && this.firstPeriodIndex == that.firstPeriodIndex && this.lastPeriodIndex == that.lastPeriodIndex && this.positionInFirstPeriodUs == that.positionInFirstPeriodUs;
        }

        public int hashCode() {
            int result = (7 * 31) + this.uid.hashCode();
            int result2 = result * 31;
            Object obj = this.tag;
            int i = 0;
            int result3 = (result2 + (obj == null ? 0 : obj.hashCode())) * 31;
            Object obj2 = this.manifest;
            if (obj2 != null) {
                i = obj2.hashCode();
            }
            long j = this.presentationStartTimeMs;
            long j2 = this.windowStartTimeMs;
            long j3 = this.defaultPositionUs;
            long j4 = this.durationUs;
            long j5 = this.positionInFirstPeriodUs;
            return ((((((((((((((((((((result3 + i) * 31) + ((int) (j ^ (j >>> 32)))) * 31) + ((int) (j2 ^ (j2 >>> 32)))) * 31) + (this.isSeekable ? 1 : 0)) * 31) + (this.isDynamic ? 1 : 0)) * 31) + (this.isLive ? 1 : 0)) * 31) + ((int) (j3 ^ (j3 >>> 32)))) * 31) + ((int) (j4 ^ (j4 >>> 32)))) * 31) + this.firstPeriodIndex) * 31) + this.lastPeriodIndex) * 31) + ((int) (j5 ^ (j5 >>> 32)));
        }
    }

    /* loaded from: classes3.dex */
    public static final class Period {
        private AdPlaybackState adPlaybackState = AdPlaybackState.NONE;
        public long durationUs;
        public Object id;
        private long positionInWindowUs;
        public Object uid;
        public int windowIndex;

        public Period set(Object id, Object uid, int windowIndex, long durationUs, long positionInWindowUs) {
            return set(id, uid, windowIndex, durationUs, positionInWindowUs, AdPlaybackState.NONE);
        }

        public Period set(Object id, Object uid, int windowIndex, long durationUs, long positionInWindowUs, AdPlaybackState adPlaybackState) {
            this.id = id;
            this.uid = uid;
            this.windowIndex = windowIndex;
            this.durationUs = durationUs;
            this.positionInWindowUs = positionInWindowUs;
            this.adPlaybackState = adPlaybackState;
            return this;
        }

        public long getDurationMs() {
            return C.usToMs(this.durationUs);
        }

        public long getDurationUs() {
            return this.durationUs;
        }

        public long getPositionInWindowMs() {
            return C.usToMs(this.positionInWindowUs);
        }

        public long getPositionInWindowUs() {
            return this.positionInWindowUs;
        }

        public int getAdGroupCount() {
            return this.adPlaybackState.adGroupCount;
        }

        public long getAdGroupTimeUs(int adGroupIndex) {
            return this.adPlaybackState.adGroupTimesUs[adGroupIndex];
        }

        public int getFirstAdIndexToPlay(int adGroupIndex) {
            return this.adPlaybackState.adGroups[adGroupIndex].getFirstAdIndexToPlay();
        }

        public int getNextAdIndexToPlay(int adGroupIndex, int lastPlayedAdIndex) {
            return this.adPlaybackState.adGroups[adGroupIndex].getNextAdIndexToPlay(lastPlayedAdIndex);
        }

        public boolean hasPlayedAdGroup(int adGroupIndex) {
            return !this.adPlaybackState.adGroups[adGroupIndex].hasUnplayedAds();
        }

        public int getAdGroupIndexForPositionUs(long positionUs) {
            return this.adPlaybackState.getAdGroupIndexForPositionUs(positionUs, this.durationUs);
        }

        public int getAdGroupIndexAfterPositionUs(long positionUs) {
            return this.adPlaybackState.getAdGroupIndexAfterPositionUs(positionUs, this.durationUs);
        }

        public int getAdCountInAdGroup(int adGroupIndex) {
            return this.adPlaybackState.adGroups[adGroupIndex].count;
        }

        public boolean isAdAvailable(int adGroupIndex, int adIndexInAdGroup) {
            AdPlaybackState.AdGroup adGroup = this.adPlaybackState.adGroups[adGroupIndex];
            return (adGroup.count == -1 || adGroup.states[adIndexInAdGroup] == 0) ? false : true;
        }

        public long getAdDurationUs(int adGroupIndex, int adIndexInAdGroup) {
            AdPlaybackState.AdGroup adGroup = this.adPlaybackState.adGroups[adGroupIndex];
            return adGroup.count != -1 ? adGroup.durationsUs[adIndexInAdGroup] : C.TIME_UNSET;
        }

        public long getAdResumePositionUs() {
            return this.adPlaybackState.adResumePositionUs;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || !getClass().equals(obj.getClass())) {
                return false;
            }
            Period that = (Period) obj;
            return Util.areEqual(this.id, that.id) && Util.areEqual(this.uid, that.uid) && this.windowIndex == that.windowIndex && this.durationUs == that.durationUs && this.positionInWindowUs == that.positionInWindowUs && Util.areEqual(this.adPlaybackState, that.adPlaybackState);
        }

        public int hashCode() {
            int i = 7 * 31;
            Object obj = this.id;
            int i2 = 0;
            int result = i + (obj == null ? 0 : obj.hashCode());
            int result2 = result * 31;
            Object obj2 = this.uid;
            int hashCode = obj2 == null ? 0 : obj2.hashCode();
            long j = this.durationUs;
            long j2 = this.positionInWindowUs;
            int result3 = (((((((result2 + hashCode) * 31) + this.windowIndex) * 31) + ((int) (j ^ (j >>> 32)))) * 31) + ((int) (j2 ^ (j2 >>> 32)))) * 31;
            AdPlaybackState adPlaybackState = this.adPlaybackState;
            if (adPlaybackState != null) {
                i2 = adPlaybackState.hashCode();
            }
            return result3 + i2;
        }
    }

    public final boolean isEmpty() {
        return getWindowCount() == 0;
    }

    public int getNextWindowIndex(int windowIndex, int repeatMode, boolean shuffleModeEnabled) {
        switch (repeatMode) {
            case 0:
                if (windowIndex != getLastWindowIndex(shuffleModeEnabled)) {
                    return windowIndex + 1;
                }
                return -1;
            case 1:
                return windowIndex;
            case 2:
                return windowIndex == getLastWindowIndex(shuffleModeEnabled) ? getFirstWindowIndex(shuffleModeEnabled) : windowIndex + 1;
            default:
                throw new IllegalStateException();
        }
    }

    public int getPreviousWindowIndex(int windowIndex, int repeatMode, boolean shuffleModeEnabled) {
        switch (repeatMode) {
            case 0:
                if (windowIndex != getFirstWindowIndex(shuffleModeEnabled)) {
                    return windowIndex - 1;
                }
                return -1;
            case 1:
                return windowIndex;
            case 2:
                return windowIndex == getFirstWindowIndex(shuffleModeEnabled) ? getLastWindowIndex(shuffleModeEnabled) : windowIndex - 1;
            default:
                throw new IllegalStateException();
        }
    }

    public int getLastWindowIndex(boolean shuffleModeEnabled) {
        if (isEmpty()) {
            return -1;
        }
        return getWindowCount() - 1;
    }

    public int getFirstWindowIndex(boolean shuffleModeEnabled) {
        return isEmpty() ? -1 : 0;
    }

    public final Window getWindow(int windowIndex, Window window) {
        return getWindow(windowIndex, window, 0L);
    }

    @Deprecated
    public final Window getWindow(int windowIndex, Window window, boolean setTag) {
        return getWindow(windowIndex, window, 0L);
    }

    public final int getNextPeriodIndex(int periodIndex, Period period, Window window, int repeatMode, boolean shuffleModeEnabled) {
        int windowIndex = getPeriod(periodIndex, period).windowIndex;
        if (getWindow(windowIndex, window).lastPeriodIndex == periodIndex) {
            int nextWindowIndex = getNextWindowIndex(windowIndex, repeatMode, shuffleModeEnabled);
            if (nextWindowIndex == -1) {
                return -1;
            }
            return getWindow(nextWindowIndex, window).firstPeriodIndex;
        }
        return periodIndex + 1;
    }

    public final boolean isLastPeriod(int periodIndex, Period period, Window window, int repeatMode, boolean shuffleModeEnabled) {
        return getNextPeriodIndex(periodIndex, period, window, repeatMode, shuffleModeEnabled) == -1;
    }

    public final Pair<Object, Long> getPeriodPosition(Window window, Period period, int windowIndex, long windowPositionUs) {
        return (Pair) Assertions.checkNotNull(getPeriodPosition(window, period, windowIndex, windowPositionUs, 0L));
    }

    public final Pair<Object, Long> getPeriodPosition(Window window, Period period, int windowIndex, long windowPositionUs, long defaultPositionProjectionUs) {
        long windowPositionUs2;
        Assertions.checkIndex(windowIndex, 0, getWindowCount());
        getWindow(windowIndex, window, defaultPositionProjectionUs);
        if (windowPositionUs != C.TIME_UNSET) {
            windowPositionUs2 = windowPositionUs;
        } else {
            windowPositionUs2 = window.getDefaultPositionUs();
            if (windowPositionUs2 == C.TIME_UNSET) {
                return null;
            }
        }
        int periodIndex = window.firstPeriodIndex;
        long periodPositionUs = window.getPositionInFirstPeriodUs() + windowPositionUs2;
        long periodDurationUs = getPeriod(periodIndex, period, true).getDurationUs();
        for (long j = C.TIME_UNSET; periodDurationUs != j && periodPositionUs >= periodDurationUs && periodIndex < window.lastPeriodIndex; j = C.TIME_UNSET) {
            periodPositionUs -= periodDurationUs;
            periodIndex++;
            periodDurationUs = getPeriod(periodIndex, period, true).getDurationUs();
        }
        return Pair.create(Assertions.checkNotNull(period.uid), Long.valueOf(periodPositionUs));
    }

    public Period getPeriodByUid(Object periodUid, Period period) {
        return getPeriod(getIndexOfPeriod(periodUid), period, true);
    }

    public final Period getPeriod(int periodIndex, Period period) {
        return getPeriod(periodIndex, period, false);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Timeline)) {
            return false;
        }
        Timeline other = (Timeline) obj;
        if (other.getWindowCount() != getWindowCount() || other.getPeriodCount() != getPeriodCount()) {
            return false;
        }
        Window window = new Window();
        Period period = new Period();
        Window otherWindow = new Window();
        Period otherPeriod = new Period();
        for (int i = 0; i < getWindowCount(); i++) {
            if (!getWindow(i, window).equals(other.getWindow(i, otherWindow))) {
                return false;
            }
        }
        for (int i2 = 0; i2 < getPeriodCount(); i2++) {
            if (!getPeriod(i2, period, true).equals(other.getPeriod(i2, otherPeriod, true))) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        Window window = new Window();
        Period period = new Period();
        int result = (7 * 31) + getWindowCount();
        for (int i = 0; i < getWindowCount(); i++) {
            result = (result * 31) + getWindow(i, window).hashCode();
        }
        int i2 = result * 31;
        int result2 = i2 + getPeriodCount();
        for (int i3 = 0; i3 < getPeriodCount(); i3++) {
            result2 = (result2 * 31) + getPeriod(i3, period, true).hashCode();
        }
        return result2;
    }
}
