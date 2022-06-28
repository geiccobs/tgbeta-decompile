package com.google.android.exoplayer2.source;

import android.util.Pair;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.util.Assertions;
/* loaded from: classes3.dex */
abstract class AbstractConcatenatedTimeline extends Timeline {
    private final int childCount;
    private final boolean isAtomic;
    private final ShuffleOrder shuffleOrder;

    protected abstract int getChildIndexByChildUid(Object obj);

    protected abstract int getChildIndexByPeriodIndex(int i);

    protected abstract int getChildIndexByWindowIndex(int i);

    protected abstract Object getChildUidByChildIndex(int i);

    protected abstract int getFirstPeriodIndexByChildIndex(int i);

    protected abstract int getFirstWindowIndexByChildIndex(int i);

    protected abstract Timeline getTimelineByChildIndex(int i);

    public static Object getChildTimelineUidFromConcatenatedUid(Object concatenatedUid) {
        return ((Pair) concatenatedUid).first;
    }

    public static Object getChildPeriodUidFromConcatenatedUid(Object concatenatedUid) {
        return ((Pair) concatenatedUid).second;
    }

    public static Object getConcatenatedUid(Object childTimelineUid, Object childPeriodOrWindowUid) {
        return Pair.create(childTimelineUid, childPeriodOrWindowUid);
    }

    public AbstractConcatenatedTimeline(boolean isAtomic, ShuffleOrder shuffleOrder) {
        this.isAtomic = isAtomic;
        this.shuffleOrder = shuffleOrder;
        this.childCount = shuffleOrder.getLength();
    }

    @Override // com.google.android.exoplayer2.Timeline
    public int getNextWindowIndex(int windowIndex, int repeatMode, boolean shuffleModeEnabled) {
        if (this.isAtomic) {
            repeatMode = repeatMode == 1 ? 2 : repeatMode;
            shuffleModeEnabled = false;
        }
        int childIndex = getChildIndexByWindowIndex(windowIndex);
        int firstWindowIndexInChild = getFirstWindowIndexByChildIndex(childIndex);
        int nextWindowIndexInChild = getTimelineByChildIndex(childIndex).getNextWindowIndex(windowIndex - firstWindowIndexInChild, repeatMode == 2 ? 0 : repeatMode, shuffleModeEnabled);
        if (nextWindowIndexInChild != -1) {
            return firstWindowIndexInChild + nextWindowIndexInChild;
        }
        int nextChildIndex = getNextChildIndex(childIndex, shuffleModeEnabled);
        while (nextChildIndex != -1 && getTimelineByChildIndex(nextChildIndex).isEmpty()) {
            nextChildIndex = getNextChildIndex(nextChildIndex, shuffleModeEnabled);
        }
        if (nextChildIndex != -1) {
            return getFirstWindowIndexByChildIndex(nextChildIndex) + getTimelineByChildIndex(nextChildIndex).getFirstWindowIndex(shuffleModeEnabled);
        }
        if (repeatMode != 2) {
            return -1;
        }
        return getFirstWindowIndex(shuffleModeEnabled);
    }

    @Override // com.google.android.exoplayer2.Timeline
    public int getPreviousWindowIndex(int windowIndex, int repeatMode, boolean shuffleModeEnabled) {
        if (this.isAtomic) {
            repeatMode = repeatMode == 1 ? 2 : repeatMode;
            shuffleModeEnabled = false;
        }
        int childIndex = getChildIndexByWindowIndex(windowIndex);
        int firstWindowIndexInChild = getFirstWindowIndexByChildIndex(childIndex);
        int previousWindowIndexInChild = getTimelineByChildIndex(childIndex).getPreviousWindowIndex(windowIndex - firstWindowIndexInChild, repeatMode == 2 ? 0 : repeatMode, shuffleModeEnabled);
        if (previousWindowIndexInChild != -1) {
            return firstWindowIndexInChild + previousWindowIndexInChild;
        }
        int previousChildIndex = getPreviousChildIndex(childIndex, shuffleModeEnabled);
        while (previousChildIndex != -1 && getTimelineByChildIndex(previousChildIndex).isEmpty()) {
            previousChildIndex = getPreviousChildIndex(previousChildIndex, shuffleModeEnabled);
        }
        if (previousChildIndex != -1) {
            return getFirstWindowIndexByChildIndex(previousChildIndex) + getTimelineByChildIndex(previousChildIndex).getLastWindowIndex(shuffleModeEnabled);
        }
        if (repeatMode != 2) {
            return -1;
        }
        return getLastWindowIndex(shuffleModeEnabled);
    }

    @Override // com.google.android.exoplayer2.Timeline
    public int getLastWindowIndex(boolean shuffleModeEnabled) {
        int i = this.childCount;
        if (i == 0) {
            return -1;
        }
        if (this.isAtomic) {
            shuffleModeEnabled = false;
        }
        int lastChildIndex = shuffleModeEnabled ? this.shuffleOrder.getLastIndex() : i - 1;
        while (getTimelineByChildIndex(lastChildIndex).isEmpty()) {
            lastChildIndex = getPreviousChildIndex(lastChildIndex, shuffleModeEnabled);
            if (lastChildIndex == -1) {
                return -1;
            }
        }
        return getFirstWindowIndexByChildIndex(lastChildIndex) + getTimelineByChildIndex(lastChildIndex).getLastWindowIndex(shuffleModeEnabled);
    }

    @Override // com.google.android.exoplayer2.Timeline
    public int getFirstWindowIndex(boolean shuffleModeEnabled) {
        if (this.childCount == 0) {
            return -1;
        }
        if (this.isAtomic) {
            shuffleModeEnabled = false;
        }
        int firstChildIndex = shuffleModeEnabled ? this.shuffleOrder.getFirstIndex() : 0;
        while (getTimelineByChildIndex(firstChildIndex).isEmpty()) {
            firstChildIndex = getNextChildIndex(firstChildIndex, shuffleModeEnabled);
            if (firstChildIndex == -1) {
                return -1;
            }
        }
        return getFirstWindowIndexByChildIndex(firstChildIndex) + getTimelineByChildIndex(firstChildIndex).getFirstWindowIndex(shuffleModeEnabled);
    }

    @Override // com.google.android.exoplayer2.Timeline
    public final Timeline.Window getWindow(int windowIndex, Timeline.Window window, long defaultPositionProjectionUs) {
        Object obj;
        int childIndex = getChildIndexByWindowIndex(windowIndex);
        int firstWindowIndexInChild = getFirstWindowIndexByChildIndex(childIndex);
        int firstPeriodIndexInChild = getFirstPeriodIndexByChildIndex(childIndex);
        getTimelineByChildIndex(childIndex).getWindow(windowIndex - firstWindowIndexInChild, window, defaultPositionProjectionUs);
        Object childUid = getChildUidByChildIndex(childIndex);
        if (Timeline.Window.SINGLE_WINDOW_UID.equals(window.uid)) {
            obj = childUid;
        } else {
            obj = getConcatenatedUid(childUid, window.uid);
        }
        window.uid = obj;
        window.firstPeriodIndex += firstPeriodIndexInChild;
        window.lastPeriodIndex += firstPeriodIndexInChild;
        return window;
    }

    @Override // com.google.android.exoplayer2.Timeline
    public final Timeline.Period getPeriodByUid(Object uid, Timeline.Period period) {
        Object childUid = getChildTimelineUidFromConcatenatedUid(uid);
        Object periodUid = getChildPeriodUidFromConcatenatedUid(uid);
        int childIndex = getChildIndexByChildUid(childUid);
        int firstWindowIndexInChild = getFirstWindowIndexByChildIndex(childIndex);
        getTimelineByChildIndex(childIndex).getPeriodByUid(periodUid, period);
        period.windowIndex += firstWindowIndexInChild;
        period.uid = uid;
        return period;
    }

    @Override // com.google.android.exoplayer2.Timeline
    public final Timeline.Period getPeriod(int periodIndex, Timeline.Period period, boolean setIds) {
        int childIndex = getChildIndexByPeriodIndex(periodIndex);
        int firstWindowIndexInChild = getFirstWindowIndexByChildIndex(childIndex);
        int firstPeriodIndexInChild = getFirstPeriodIndexByChildIndex(childIndex);
        getTimelineByChildIndex(childIndex).getPeriod(periodIndex - firstPeriodIndexInChild, period, setIds);
        period.windowIndex += firstWindowIndexInChild;
        if (setIds) {
            period.uid = getConcatenatedUid(getChildUidByChildIndex(childIndex), Assertions.checkNotNull(period.uid));
        }
        return period;
    }

    @Override // com.google.android.exoplayer2.Timeline
    public final int getIndexOfPeriod(Object uid) {
        int periodIndexInChild;
        if (!(uid instanceof Pair)) {
            return -1;
        }
        Object childUid = getChildTimelineUidFromConcatenatedUid(uid);
        Object periodUid = getChildPeriodUidFromConcatenatedUid(uid);
        int childIndex = getChildIndexByChildUid(childUid);
        if (childIndex != -1 && (periodIndexInChild = getTimelineByChildIndex(childIndex).getIndexOfPeriod(periodUid)) != -1) {
            return getFirstPeriodIndexByChildIndex(childIndex) + periodIndexInChild;
        }
        return -1;
    }

    @Override // com.google.android.exoplayer2.Timeline
    public final Object getUidOfPeriod(int periodIndex) {
        int childIndex = getChildIndexByPeriodIndex(periodIndex);
        int firstPeriodIndexInChild = getFirstPeriodIndexByChildIndex(childIndex);
        Object periodUidInChild = getTimelineByChildIndex(childIndex).getUidOfPeriod(periodIndex - firstPeriodIndexInChild);
        return getConcatenatedUid(getChildUidByChildIndex(childIndex), periodUidInChild);
    }

    private int getNextChildIndex(int childIndex, boolean shuffleModeEnabled) {
        if (shuffleModeEnabled) {
            return this.shuffleOrder.getNextIndex(childIndex);
        }
        if (childIndex >= this.childCount - 1) {
            return -1;
        }
        return childIndex + 1;
    }

    private int getPreviousChildIndex(int childIndex, boolean shuffleModeEnabled) {
        if (shuffleModeEnabled) {
            return this.shuffleOrder.getPreviousIndex(childIndex);
        }
        if (childIndex <= 0) {
            return -1;
        }
        return childIndex - 1;
    }
}
