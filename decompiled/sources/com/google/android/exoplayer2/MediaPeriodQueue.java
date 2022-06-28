package com.google.android.exoplayer2;

import android.util.Pair;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectorResult;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.util.Assertions;
/* loaded from: classes3.dex */
final class MediaPeriodQueue {
    private static final int MAXIMUM_BUFFER_AHEAD_PERIODS = 100;
    private int length;
    private MediaPeriodHolder loading;
    private long nextWindowSequenceNumber;
    private Object oldFrontPeriodUid;
    private long oldFrontPeriodWindowSequenceNumber;
    private MediaPeriodHolder playing;
    private MediaPeriodHolder reading;
    private int repeatMode;
    private boolean shuffleModeEnabled;
    private final Timeline.Period period = new Timeline.Period();
    private final Timeline.Window window = new Timeline.Window();
    private Timeline timeline = Timeline.EMPTY;

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public boolean updateRepeatMode(int repeatMode) {
        this.repeatMode = repeatMode;
        return updateForPlaybackModeChange();
    }

    public boolean updateShuffleModeEnabled(boolean shuffleModeEnabled) {
        this.shuffleModeEnabled = shuffleModeEnabled;
        return updateForPlaybackModeChange();
    }

    public boolean isLoading(MediaPeriod mediaPeriod) {
        MediaPeriodHolder mediaPeriodHolder = this.loading;
        return mediaPeriodHolder != null && mediaPeriodHolder.mediaPeriod == mediaPeriod;
    }

    public void reevaluateBuffer(long rendererPositionUs) {
        MediaPeriodHolder mediaPeriodHolder = this.loading;
        if (mediaPeriodHolder != null) {
            mediaPeriodHolder.reevaluateBuffer(rendererPositionUs);
        }
    }

    public boolean shouldLoadNextMediaPeriod() {
        MediaPeriodHolder mediaPeriodHolder = this.loading;
        return mediaPeriodHolder == null || (!mediaPeriodHolder.info.isFinal && this.loading.isFullyBuffered() && this.loading.info.durationUs != C.TIME_UNSET && this.length < 100);
    }

    public MediaPeriodInfo getNextMediaPeriodInfo(long rendererPositionUs, PlaybackInfo playbackInfo) {
        MediaPeriodHolder mediaPeriodHolder = this.loading;
        if (mediaPeriodHolder == null) {
            return getFirstMediaPeriodInfo(playbackInfo);
        }
        return getFollowingMediaPeriodInfo(mediaPeriodHolder, rendererPositionUs);
    }

    public MediaPeriodHolder enqueueNextMediaPeriodHolder(RendererCapabilities[] rendererCapabilities, TrackSelector trackSelector, Allocator allocator, MediaSource mediaSource, MediaPeriodInfo info, TrackSelectorResult emptyTrackSelectorResult) {
        long rendererPositionOffsetUs;
        MediaPeriodHolder mediaPeriodHolder = this.loading;
        if (mediaPeriodHolder != null) {
            rendererPositionOffsetUs = (mediaPeriodHolder.getRendererOffset() + this.loading.info.durationUs) - info.startPositionUs;
        } else if (info.id.isAd() && info.contentPositionUs != C.TIME_UNSET) {
            rendererPositionOffsetUs = info.contentPositionUs;
        } else {
            rendererPositionOffsetUs = 0;
        }
        MediaPeriodHolder newPeriodHolder = new MediaPeriodHolder(rendererCapabilities, rendererPositionOffsetUs, trackSelector, allocator, mediaSource, info, emptyTrackSelectorResult);
        MediaPeriodHolder mediaPeriodHolder2 = this.loading;
        if (mediaPeriodHolder2 != null) {
            mediaPeriodHolder2.setNext(newPeriodHolder);
        } else {
            this.playing = newPeriodHolder;
            this.reading = newPeriodHolder;
        }
        this.oldFrontPeriodUid = null;
        this.loading = newPeriodHolder;
        this.length++;
        return newPeriodHolder;
    }

    public MediaPeriodHolder getLoadingPeriod() {
        return this.loading;
    }

    public MediaPeriodHolder getPlayingPeriod() {
        return this.playing;
    }

    public MediaPeriodHolder getReadingPeriod() {
        return this.reading;
    }

    public MediaPeriodHolder advanceReadingPeriod() {
        MediaPeriodHolder mediaPeriodHolder = this.reading;
        Assertions.checkState((mediaPeriodHolder == null || mediaPeriodHolder.getNext() == null) ? false : true);
        MediaPeriodHolder next = this.reading.getNext();
        this.reading = next;
        return next;
    }

    public MediaPeriodHolder advancePlayingPeriod() {
        MediaPeriodHolder mediaPeriodHolder = this.playing;
        if (mediaPeriodHolder == null) {
            return null;
        }
        if (mediaPeriodHolder == this.reading) {
            this.reading = mediaPeriodHolder.getNext();
        }
        this.playing.release();
        int i = this.length - 1;
        this.length = i;
        if (i == 0) {
            this.loading = null;
            this.oldFrontPeriodUid = this.playing.uid;
            this.oldFrontPeriodWindowSequenceNumber = this.playing.info.id.windowSequenceNumber;
        }
        MediaPeriodHolder next = this.playing.getNext();
        this.playing = next;
        return next;
    }

    public boolean removeAfter(MediaPeriodHolder mediaPeriodHolder) {
        Assertions.checkState(mediaPeriodHolder != null);
        boolean removedReading = false;
        this.loading = mediaPeriodHolder;
        while (mediaPeriodHolder.getNext() != null) {
            mediaPeriodHolder = mediaPeriodHolder.getNext();
            if (mediaPeriodHolder == this.reading) {
                this.reading = this.playing;
                removedReading = true;
            }
            mediaPeriodHolder.release();
            this.length--;
        }
        this.loading.setNext(null);
        return removedReading;
    }

    public void clear(boolean keepFrontPeriodUid) {
        MediaPeriodHolder front = this.playing;
        if (front != null) {
            this.oldFrontPeriodUid = keepFrontPeriodUid ? front.uid : null;
            this.oldFrontPeriodWindowSequenceNumber = front.info.id.windowSequenceNumber;
            removeAfter(front);
            front.release();
        } else if (!keepFrontPeriodUid) {
            this.oldFrontPeriodUid = null;
        }
        this.playing = null;
        this.loading = null;
        this.reading = null;
        this.length = 0;
    }

    public boolean updateQueuedPeriods(long rendererPositionUs, long maxRendererReadPositionUs) {
        MediaPeriodInfo newPeriodInfo;
        long newDurationInRendererTime;
        MediaPeriodHolder previousPeriodHolder = null;
        MediaPeriodHolder periodHolder = this.playing;
        while (periodHolder != null) {
            MediaPeriodInfo oldPeriodInfo = periodHolder.info;
            if (previousPeriodHolder == null) {
                newPeriodInfo = getUpdatedMediaPeriodInfo(oldPeriodInfo);
            } else {
                newPeriodInfo = getFollowingMediaPeriodInfo(previousPeriodHolder, rendererPositionUs);
                if (newPeriodInfo == null) {
                    return true ^ removeAfter(previousPeriodHolder);
                }
                if (!canKeepMediaPeriodHolder(oldPeriodInfo, newPeriodInfo)) {
                    return true ^ removeAfter(previousPeriodHolder);
                }
            }
            periodHolder.info = newPeriodInfo.copyWithContentPositionUs(oldPeriodInfo.contentPositionUs);
            if (!areDurationsCompatible(oldPeriodInfo.durationUs, newPeriodInfo.durationUs)) {
                if (newPeriodInfo.durationUs == C.TIME_UNSET) {
                    newDurationInRendererTime = Long.MAX_VALUE;
                } else {
                    newDurationInRendererTime = periodHolder.toRendererTime(newPeriodInfo.durationUs);
                }
                boolean isReadingAndReadBeyondNewDuration = periodHolder == this.reading && (maxRendererReadPositionUs == Long.MIN_VALUE || maxRendererReadPositionUs >= newDurationInRendererTime);
                boolean readingPeriodRemoved = removeAfter(periodHolder);
                return !readingPeriodRemoved && !isReadingAndReadBeyondNewDuration;
            }
            previousPeriodHolder = periodHolder;
            periodHolder = periodHolder.getNext();
        }
        return true;
    }

    public MediaPeriodInfo getUpdatedMediaPeriodInfo(MediaPeriodInfo info) {
        long durationUs;
        MediaSource.MediaPeriodId id = info.id;
        boolean isLastInPeriod = isLastInPeriod(id);
        boolean isLastInTimeline = isLastInTimeline(id, isLastInPeriod);
        this.timeline.getPeriodByUid(info.id.periodUid, this.period);
        if (id.isAd()) {
            durationUs = this.period.getAdDurationUs(id.adGroupIndex, id.adIndexInAdGroup);
        } else if (info.endPositionUs == C.TIME_UNSET || info.endPositionUs == Long.MIN_VALUE) {
            durationUs = this.period.getDurationUs();
        } else {
            durationUs = info.endPositionUs;
        }
        return new MediaPeriodInfo(id, info.startPositionUs, info.contentPositionUs, info.endPositionUs, durationUs, isLastInPeriod, isLastInTimeline);
    }

    public MediaSource.MediaPeriodId resolveMediaPeriodIdForAds(Object periodUid, long positionUs) {
        long windowSequenceNumber = resolvePeriodIndexToWindowSequenceNumber(periodUid);
        return resolveMediaPeriodIdForAds(periodUid, positionUs, windowSequenceNumber);
    }

    private MediaSource.MediaPeriodId resolveMediaPeriodIdForAds(Object periodUid, long positionUs, long windowSequenceNumber) {
        this.timeline.getPeriodByUid(periodUid, this.period);
        int adGroupIndex = this.period.getAdGroupIndexForPositionUs(positionUs);
        if (adGroupIndex == -1) {
            int nextAdGroupIndex = this.period.getAdGroupIndexAfterPositionUs(positionUs);
            return new MediaSource.MediaPeriodId(periodUid, windowSequenceNumber, nextAdGroupIndex);
        }
        int adIndexInAdGroup = this.period.getFirstAdIndexToPlay(adGroupIndex);
        return new MediaSource.MediaPeriodId(periodUid, adGroupIndex, adIndexInAdGroup, windowSequenceNumber);
    }

    private long resolvePeriodIndexToWindowSequenceNumber(Object periodUid) {
        int oldFrontPeriodIndex;
        int windowIndex = this.timeline.getPeriodByUid(periodUid, this.period).windowIndex;
        Object obj = this.oldFrontPeriodUid;
        if (obj != null && (oldFrontPeriodIndex = this.timeline.getIndexOfPeriod(obj)) != -1) {
            int oldFrontWindowIndex = this.timeline.getPeriod(oldFrontPeriodIndex, this.period).windowIndex;
            if (oldFrontWindowIndex == windowIndex) {
                return this.oldFrontPeriodWindowSequenceNumber;
            }
        }
        for (MediaPeriodHolder mediaPeriodHolder = this.playing; mediaPeriodHolder != null; mediaPeriodHolder = mediaPeriodHolder.getNext()) {
            if (mediaPeriodHolder.uid.equals(periodUid)) {
                return mediaPeriodHolder.info.id.windowSequenceNumber;
            }
        }
        for (MediaPeriodHolder mediaPeriodHolder2 = this.playing; mediaPeriodHolder2 != null; mediaPeriodHolder2 = mediaPeriodHolder2.getNext()) {
            int indexOfHolderInTimeline = this.timeline.getIndexOfPeriod(mediaPeriodHolder2.uid);
            if (indexOfHolderInTimeline != -1) {
                int holderWindowIndex = this.timeline.getPeriod(indexOfHolderInTimeline, this.period).windowIndex;
                if (holderWindowIndex == windowIndex) {
                    return mediaPeriodHolder2.info.id.windowSequenceNumber;
                }
            }
        }
        long windowSequenceNumber = this.nextWindowSequenceNumber;
        this.nextWindowSequenceNumber = 1 + windowSequenceNumber;
        if (this.playing == null) {
            this.oldFrontPeriodUid = periodUid;
            this.oldFrontPeriodWindowSequenceNumber = windowSequenceNumber;
        }
        return windowSequenceNumber;
    }

    private boolean canKeepMediaPeriodHolder(MediaPeriodInfo oldInfo, MediaPeriodInfo newInfo) {
        return oldInfo.startPositionUs == newInfo.startPositionUs && oldInfo.id.equals(newInfo.id);
    }

    private boolean areDurationsCompatible(long previousDurationUs, long newDurationUs) {
        return previousDurationUs == C.TIME_UNSET || previousDurationUs == newDurationUs;
    }

    private boolean updateForPlaybackModeChange() {
        MediaPeriodHolder lastValidPeriodHolder = this.playing;
        if (lastValidPeriodHolder == null) {
            return true;
        }
        int nextPeriodIndex = this.timeline.getIndexOfPeriod(lastValidPeriodHolder.uid);
        while (true) {
            int currentPeriodIndex = nextPeriodIndex;
            nextPeriodIndex = this.timeline.getNextPeriodIndex(currentPeriodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled);
            while (lastValidPeriodHolder.getNext() != null && !lastValidPeriodHolder.info.isLastInTimelinePeriod) {
                lastValidPeriodHolder = lastValidPeriodHolder.getNext();
            }
            MediaPeriodHolder nextMediaPeriodHolder = lastValidPeriodHolder.getNext();
            if (nextPeriodIndex == -1 || nextMediaPeriodHolder == null) {
                break;
            }
            int nextPeriodHolderPeriodIndex = this.timeline.getIndexOfPeriod(nextMediaPeriodHolder.uid);
            if (nextPeriodHolderPeriodIndex != nextPeriodIndex) {
                break;
            }
            lastValidPeriodHolder = nextMediaPeriodHolder;
        }
        boolean readingPeriodRemoved = removeAfter(lastValidPeriodHolder);
        lastValidPeriodHolder.info = getUpdatedMediaPeriodInfo(lastValidPeriodHolder.info);
        return !readingPeriodRemoved;
    }

    private MediaPeriodInfo getFirstMediaPeriodInfo(PlaybackInfo playbackInfo) {
        return getMediaPeriodInfo(playbackInfo.periodId, playbackInfo.contentPositionUs, playbackInfo.startPositionUs);
    }

    private MediaPeriodInfo getFollowingMediaPeriodInfo(MediaPeriodHolder mediaPeriodHolder, long rendererPositionUs) {
        long startPositionUs;
        long contentPositionUs;
        long windowSequenceNumber;
        Object nextPeriodUid;
        long startPositionUs2;
        long windowSequenceNumber2;
        MediaPeriodInfo mediaPeriodInfo = mediaPeriodHolder.info;
        long bufferedDurationUs = (mediaPeriodHolder.getRendererOffset() + mediaPeriodInfo.durationUs) - rendererPositionUs;
        if (mediaPeriodInfo.isLastInTimelinePeriod) {
            int currentPeriodIndex = this.timeline.getIndexOfPeriod(mediaPeriodInfo.id.periodUid);
            int nextPeriodIndex = this.timeline.getNextPeriodIndex(currentPeriodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled);
            if (nextPeriodIndex == -1) {
                return null;
            }
            int nextWindowIndex = this.timeline.getPeriod(nextPeriodIndex, this.period, true).windowIndex;
            Object nextPeriodUid2 = this.period.uid;
            long windowSequenceNumber3 = mediaPeriodInfo.id.windowSequenceNumber;
            if (this.timeline.getWindow(nextWindowIndex, this.window).firstPeriodIndex == nextPeriodIndex) {
                contentPositionUs = C.TIME_UNSET;
                Pair<Object, Long> defaultPosition = this.timeline.getPeriodPosition(this.window, this.period, nextWindowIndex, C.TIME_UNSET, Math.max(0L, bufferedDurationUs));
                if (defaultPosition == null) {
                    return null;
                }
                Object nextPeriodUid3 = defaultPosition.first;
                startPositionUs2 = ((Long) defaultPosition.second).longValue();
                MediaPeriodHolder nextMediaPeriodHolder = mediaPeriodHolder.getNext();
                if (nextMediaPeriodHolder != null && nextMediaPeriodHolder.uid.equals(nextPeriodUid3)) {
                    long windowSequenceNumber4 = nextMediaPeriodHolder.info.id.windowSequenceNumber;
                    nextPeriodUid = nextPeriodUid3;
                    windowSequenceNumber2 = windowSequenceNumber4;
                } else {
                    nextPeriodUid = nextPeriodUid3;
                    windowSequenceNumber2 = this.nextWindowSequenceNumber;
                    this.nextWindowSequenceNumber = windowSequenceNumber2 + 1;
                }
                windowSequenceNumber = windowSequenceNumber2;
            } else {
                windowSequenceNumber = windowSequenceNumber3;
                startPositionUs2 = 0;
                contentPositionUs = 0;
                nextPeriodUid = nextPeriodUid2;
            }
            MediaSource.MediaPeriodId periodId = resolveMediaPeriodIdForAds(nextPeriodUid, startPositionUs2, windowSequenceNumber);
            return getMediaPeriodInfo(periodId, contentPositionUs, startPositionUs2);
        }
        MediaSource.MediaPeriodId currentPeriodId = mediaPeriodInfo.id;
        this.timeline.getPeriodByUid(currentPeriodId.periodUid, this.period);
        if (!currentPeriodId.isAd()) {
            int nextAdGroupIndex = this.period.getAdGroupIndexForPositionUs(mediaPeriodInfo.endPositionUs);
            if (nextAdGroupIndex == -1) {
                return getMediaPeriodInfoForContent(currentPeriodId.periodUid, mediaPeriodInfo.durationUs, currentPeriodId.windowSequenceNumber);
            }
            int adIndexInAdGroup = this.period.getFirstAdIndexToPlay(nextAdGroupIndex);
            if (!this.period.isAdAvailable(nextAdGroupIndex, adIndexInAdGroup)) {
                return null;
            }
            return getMediaPeriodInfoForAd(currentPeriodId.periodUid, nextAdGroupIndex, adIndexInAdGroup, mediaPeriodInfo.durationUs, currentPeriodId.windowSequenceNumber);
        }
        int adGroupIndex = currentPeriodId.adGroupIndex;
        int adCountInCurrentAdGroup = this.period.getAdCountInAdGroup(adGroupIndex);
        if (adCountInCurrentAdGroup == -1) {
            return null;
        }
        int nextAdIndexInAdGroup = this.period.getNextAdIndexToPlay(adGroupIndex, currentPeriodId.adIndexInAdGroup);
        if (nextAdIndexInAdGroup < adCountInCurrentAdGroup) {
            if (this.period.isAdAvailable(adGroupIndex, nextAdIndexInAdGroup)) {
                return getMediaPeriodInfoForAd(currentPeriodId.periodUid, adGroupIndex, nextAdIndexInAdGroup, mediaPeriodInfo.contentPositionUs, currentPeriodId.windowSequenceNumber);
            }
            return null;
        }
        long startPositionUs3 = mediaPeriodInfo.contentPositionUs;
        if (startPositionUs3 != C.TIME_UNSET) {
            startPositionUs = startPositionUs3;
        } else {
            Timeline timeline = this.timeline;
            Timeline.Window window = this.window;
            Timeline.Period period = this.period;
            Pair<Object, Long> defaultPosition2 = timeline.getPeriodPosition(window, period, period.windowIndex, C.TIME_UNSET, Math.max(0L, bufferedDurationUs));
            if (defaultPosition2 == null) {
                return null;
            }
            startPositionUs = ((Long) defaultPosition2.second).longValue();
        }
        return getMediaPeriodInfoForContent(currentPeriodId.periodUid, startPositionUs, currentPeriodId.windowSequenceNumber);
    }

    private MediaPeriodInfo getMediaPeriodInfo(MediaSource.MediaPeriodId id, long contentPositionUs, long startPositionUs) {
        this.timeline.getPeriodByUid(id.periodUid, this.period);
        if (id.isAd()) {
            if (!this.period.isAdAvailable(id.adGroupIndex, id.adIndexInAdGroup)) {
                return null;
            }
            return getMediaPeriodInfoForAd(id.periodUid, id.adGroupIndex, id.adIndexInAdGroup, contentPositionUs, id.windowSequenceNumber);
        }
        return getMediaPeriodInfoForContent(id.periodUid, startPositionUs, id.windowSequenceNumber);
    }

    private MediaPeriodInfo getMediaPeriodInfoForAd(Object periodUid, int adGroupIndex, int adIndexInAdGroup, long contentPositionUs, long windowSequenceNumber) {
        long startPositionUs;
        MediaSource.MediaPeriodId id = new MediaSource.MediaPeriodId(periodUid, adGroupIndex, adIndexInAdGroup, windowSequenceNumber);
        long durationUs = this.timeline.getPeriodByUid(id.periodUid, this.period).getAdDurationUs(id.adGroupIndex, id.adIndexInAdGroup);
        if (adIndexInAdGroup == this.period.getFirstAdIndexToPlay(adGroupIndex)) {
            startPositionUs = this.period.getAdResumePositionUs();
        } else {
            startPositionUs = 0;
        }
        return new MediaPeriodInfo(id, startPositionUs, contentPositionUs, C.TIME_UNSET, durationUs, false, false);
    }

    private MediaPeriodInfo getMediaPeriodInfoForContent(Object periodUid, long startPositionUs, long windowSequenceNumber) {
        long j;
        long durationUs;
        int nextAdGroupIndex = this.period.getAdGroupIndexAfterPositionUs(startPositionUs);
        MediaSource.MediaPeriodId id = new MediaSource.MediaPeriodId(periodUid, windowSequenceNumber, nextAdGroupIndex);
        boolean isLastInPeriod = isLastInPeriod(id);
        boolean isLastInTimeline = isLastInTimeline(id, isLastInPeriod);
        if (nextAdGroupIndex != -1) {
            j = this.period.getAdGroupTimeUs(nextAdGroupIndex);
        } else {
            j = -9223372036854775807L;
        }
        long endPositionUs = j;
        if (endPositionUs == C.TIME_UNSET || endPositionUs == Long.MIN_VALUE) {
            durationUs = this.period.durationUs;
        } else {
            durationUs = endPositionUs;
        }
        return new MediaPeriodInfo(id, startPositionUs, C.TIME_UNSET, endPositionUs, durationUs, isLastInPeriod, isLastInTimeline);
    }

    private boolean isLastInPeriod(MediaSource.MediaPeriodId id) {
        return !id.isAd() && id.nextAdGroupIndex == -1;
    }

    private boolean isLastInTimeline(MediaSource.MediaPeriodId id, boolean isLastMediaPeriodInPeriod) {
        int periodIndex = this.timeline.getIndexOfPeriod(id.periodUid);
        int windowIndex = this.timeline.getPeriod(periodIndex, this.period).windowIndex;
        return !this.timeline.getWindow(windowIndex, this.window).isDynamic && this.timeline.isLastPeriod(periodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled) && isLastMediaPeriodInPeriod;
    }
}
