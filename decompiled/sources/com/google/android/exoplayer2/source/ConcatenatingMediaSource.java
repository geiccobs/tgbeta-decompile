package com.google.android.exoplayer2.source;

import android.os.Handler;
import android.os.Message;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ShuffleOrder;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
/* loaded from: classes3.dex */
public final class ConcatenatingMediaSource extends CompositeMediaSource<MediaSourceHolder> {
    private static final int MSG_ADD = 0;
    private static final int MSG_MOVE = 2;
    private static final int MSG_ON_COMPLETION = 5;
    private static final int MSG_REMOVE = 1;
    private static final int MSG_SET_SHUFFLE_ORDER = 3;
    private static final int MSG_UPDATE_TIMELINE = 4;
    private final Set<MediaSourceHolder> enabledMediaSourceHolders;
    private final boolean isAtomic;
    private final Map<MediaPeriod, MediaSourceHolder> mediaSourceByMediaPeriod;
    private final Map<Object, MediaSourceHolder> mediaSourceByUid;
    private final List<MediaSourceHolder> mediaSourceHolders;
    private final List<MediaSourceHolder> mediaSourcesPublic;
    private Set<HandlerAndRunnable> nextTimelineUpdateOnCompletionActions;
    private final Set<HandlerAndRunnable> pendingOnCompletionActions;
    private Handler playbackThreadHandler;
    private ShuffleOrder shuffleOrder;
    private boolean timelineUpdateScheduled;
    private final boolean useLazyPreparation;

    public ConcatenatingMediaSource(MediaSource... mediaSources) {
        this(false, mediaSources);
    }

    public ConcatenatingMediaSource(boolean isAtomic, MediaSource... mediaSources) {
        this(isAtomic, new ShuffleOrder.DefaultShuffleOrder(0), mediaSources);
    }

    public ConcatenatingMediaSource(boolean isAtomic, ShuffleOrder shuffleOrder, MediaSource... mediaSources) {
        this(isAtomic, false, shuffleOrder, mediaSources);
    }

    public ConcatenatingMediaSource(boolean isAtomic, boolean useLazyPreparation, ShuffleOrder shuffleOrder, MediaSource... mediaSources) {
        for (MediaSource mediaSource : mediaSources) {
            Assertions.checkNotNull(mediaSource);
        }
        this.shuffleOrder = shuffleOrder.getLength() > 0 ? shuffleOrder.cloneAndClear() : shuffleOrder;
        this.mediaSourceByMediaPeriod = new IdentityHashMap();
        this.mediaSourceByUid = new HashMap();
        this.mediaSourcesPublic = new ArrayList();
        this.mediaSourceHolders = new ArrayList();
        this.nextTimelineUpdateOnCompletionActions = new HashSet();
        this.pendingOnCompletionActions = new HashSet();
        this.enabledMediaSourceHolders = new HashSet();
        this.isAtomic = isAtomic;
        this.useLazyPreparation = useLazyPreparation;
        addMediaSources(Arrays.asList(mediaSources));
    }

    public synchronized void addMediaSource(MediaSource mediaSource) {
        addMediaSource(this.mediaSourcesPublic.size(), mediaSource);
    }

    public synchronized void addMediaSource(MediaSource mediaSource, Handler handler, Runnable onCompletionAction) {
        addMediaSource(this.mediaSourcesPublic.size(), mediaSource, handler, onCompletionAction);
    }

    public synchronized void addMediaSource(int index, MediaSource mediaSource) {
        addPublicMediaSources(index, Collections.singletonList(mediaSource), null, null);
    }

    public synchronized void addMediaSource(int index, MediaSource mediaSource, Handler handler, Runnable onCompletionAction) {
        addPublicMediaSources(index, Collections.singletonList(mediaSource), handler, onCompletionAction);
    }

    public synchronized void addMediaSources(Collection<MediaSource> mediaSources) {
        addPublicMediaSources(this.mediaSourcesPublic.size(), mediaSources, null, null);
    }

    public synchronized void addMediaSources(Collection<MediaSource> mediaSources, Handler handler, Runnable onCompletionAction) {
        addPublicMediaSources(this.mediaSourcesPublic.size(), mediaSources, handler, onCompletionAction);
    }

    public synchronized void addMediaSources(int index, Collection<MediaSource> mediaSources) {
        addPublicMediaSources(index, mediaSources, null, null);
    }

    public synchronized void addMediaSources(int index, Collection<MediaSource> mediaSources, Handler handler, Runnable onCompletionAction) {
        addPublicMediaSources(index, mediaSources, handler, onCompletionAction);
    }

    public synchronized MediaSource removeMediaSource(int index) {
        MediaSource removedMediaSource;
        removedMediaSource = getMediaSource(index);
        removePublicMediaSources(index, index + 1, null, null);
        return removedMediaSource;
    }

    public synchronized MediaSource removeMediaSource(int index, Handler handler, Runnable onCompletionAction) {
        MediaSource removedMediaSource;
        removedMediaSource = getMediaSource(index);
        removePublicMediaSources(index, index + 1, handler, onCompletionAction);
        return removedMediaSource;
    }

    public synchronized void removeMediaSourceRange(int fromIndex, int toIndex) {
        removePublicMediaSources(fromIndex, toIndex, null, null);
    }

    public synchronized void removeMediaSourceRange(int fromIndex, int toIndex, Handler handler, Runnable onCompletionAction) {
        removePublicMediaSources(fromIndex, toIndex, handler, onCompletionAction);
    }

    public synchronized void moveMediaSource(int currentIndex, int newIndex) {
        movePublicMediaSource(currentIndex, newIndex, null, null);
    }

    public synchronized void moveMediaSource(int currentIndex, int newIndex, Handler handler, Runnable onCompletionAction) {
        movePublicMediaSource(currentIndex, newIndex, handler, onCompletionAction);
    }

    public synchronized void clear() {
        removeMediaSourceRange(0, getSize());
    }

    public synchronized void clear(Handler handler, Runnable onCompletionAction) {
        removeMediaSourceRange(0, getSize(), handler, onCompletionAction);
    }

    public synchronized int getSize() {
        return this.mediaSourcesPublic.size();
    }

    public synchronized MediaSource getMediaSource(int index) {
        return this.mediaSourcesPublic.get(index).mediaSource;
    }

    public synchronized void setShuffleOrder(ShuffleOrder shuffleOrder) {
        setPublicShuffleOrder(shuffleOrder, null, null);
    }

    public synchronized void setShuffleOrder(ShuffleOrder shuffleOrder, Handler handler, Runnable onCompletionAction) {
        setPublicShuffleOrder(shuffleOrder, handler, onCompletionAction);
    }

    @Override // com.google.android.exoplayer2.source.BaseMediaSource, com.google.android.exoplayer2.source.MediaSource
    public Object getTag() {
        return null;
    }

    @Override // com.google.android.exoplayer2.source.CompositeMediaSource, com.google.android.exoplayer2.source.BaseMediaSource
    public synchronized void prepareSourceInternal(TransferListener mediaTransferListener) {
        super.prepareSourceInternal(mediaTransferListener);
        this.playbackThreadHandler = new Handler(new Handler.Callback() { // from class: com.google.android.exoplayer2.source.ConcatenatingMediaSource$$ExternalSyntheticLambda0
            @Override // android.os.Handler.Callback
            public final boolean handleMessage(Message message) {
                boolean handleMessage;
                handleMessage = ConcatenatingMediaSource.this.handleMessage(message);
                return handleMessage;
            }
        });
        if (this.mediaSourcesPublic.isEmpty()) {
            updateTimelineAndScheduleOnCompletionActions();
        } else {
            this.shuffleOrder = this.shuffleOrder.cloneAndInsert(0, this.mediaSourcesPublic.size());
            addMediaSourcesInternal(0, this.mediaSourcesPublic);
            scheduleTimelineUpdate();
        }
    }

    @Override // com.google.android.exoplayer2.source.CompositeMediaSource, com.google.android.exoplayer2.source.BaseMediaSource
    protected void enableInternal() {
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public MediaPeriod createPeriod(MediaSource.MediaPeriodId id, Allocator allocator, long startPositionUs) {
        Object mediaSourceHolderUid = getMediaSourceHolderUid(id.periodUid);
        MediaSource.MediaPeriodId childMediaPeriodId = id.copyWithPeriodUid(getChildPeriodUid(id.periodUid));
        MediaSourceHolder holder = this.mediaSourceByUid.get(mediaSourceHolderUid);
        if (holder == null) {
            holder = new MediaSourceHolder(new DummyMediaSource(), this.useLazyPreparation);
            holder.isRemoved = true;
            prepareChildSource(holder, holder.mediaSource);
        }
        enableMediaSource(holder);
        holder.activeMediaPeriodIds.add(childMediaPeriodId);
        MediaPeriod mediaPeriod = holder.mediaSource.createPeriod(childMediaPeriodId, allocator, startPositionUs);
        this.mediaSourceByMediaPeriod.put(mediaPeriod, holder);
        disableUnusedMediaSources();
        return mediaPeriod;
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public void releasePeriod(MediaPeriod mediaPeriod) {
        MediaSourceHolder holder = (MediaSourceHolder) Assertions.checkNotNull(this.mediaSourceByMediaPeriod.remove(mediaPeriod));
        holder.mediaSource.releasePeriod(mediaPeriod);
        holder.activeMediaPeriodIds.remove(((MaskingMediaPeriod) mediaPeriod).id);
        if (!this.mediaSourceByMediaPeriod.isEmpty()) {
            disableUnusedMediaSources();
        }
        maybeReleaseChildSource(holder);
    }

    @Override // com.google.android.exoplayer2.source.CompositeMediaSource, com.google.android.exoplayer2.source.BaseMediaSource
    public void disableInternal() {
        super.disableInternal();
        this.enabledMediaSourceHolders.clear();
    }

    @Override // com.google.android.exoplayer2.source.CompositeMediaSource, com.google.android.exoplayer2.source.BaseMediaSource
    public synchronized void releaseSourceInternal() {
        super.releaseSourceInternal();
        this.mediaSourceHolders.clear();
        this.enabledMediaSourceHolders.clear();
        this.mediaSourceByUid.clear();
        this.shuffleOrder = this.shuffleOrder.cloneAndClear();
        Handler handler = this.playbackThreadHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            this.playbackThreadHandler = null;
        }
        this.timelineUpdateScheduled = false;
        this.nextTimelineUpdateOnCompletionActions.clear();
        dispatchOnCompletionActions(this.pendingOnCompletionActions);
    }

    public void onChildSourceInfoRefreshed(MediaSourceHolder mediaSourceHolder, MediaSource mediaSource, Timeline timeline) {
        updateMediaSourceInternal(mediaSourceHolder, timeline);
    }

    public MediaSource.MediaPeriodId getMediaPeriodIdForChildMediaPeriodId(MediaSourceHolder mediaSourceHolder, MediaSource.MediaPeriodId mediaPeriodId) {
        for (int i = 0; i < mediaSourceHolder.activeMediaPeriodIds.size(); i++) {
            if (mediaSourceHolder.activeMediaPeriodIds.get(i).windowSequenceNumber == mediaPeriodId.windowSequenceNumber) {
                Object periodUid = getPeriodUid(mediaSourceHolder, mediaPeriodId.periodUid);
                return mediaPeriodId.copyWithPeriodUid(periodUid);
            }
        }
        return null;
    }

    public int getWindowIndexForChildWindowIndex(MediaSourceHolder mediaSourceHolder, int windowIndex) {
        return mediaSourceHolder.firstWindowIndexInChild + windowIndex;
    }

    private void addPublicMediaSources(int index, Collection<MediaSource> mediaSources, Handler handler, Runnable onCompletionAction) {
        boolean z = true;
        if ((handler == null) != (onCompletionAction == null)) {
            z = false;
        }
        Assertions.checkArgument(z);
        Handler playbackThreadHandler = this.playbackThreadHandler;
        for (MediaSource mediaSource : mediaSources) {
            Assertions.checkNotNull(mediaSource);
        }
        List<MediaSourceHolder> mediaSourceHolders = new ArrayList<>(mediaSources.size());
        for (MediaSource mediaSource2 : mediaSources) {
            mediaSourceHolders.add(new MediaSourceHolder(mediaSource2, this.useLazyPreparation));
        }
        this.mediaSourcesPublic.addAll(index, mediaSourceHolders);
        if (playbackThreadHandler != null && !mediaSources.isEmpty()) {
            HandlerAndRunnable callbackAction = createOnCompletionAction(handler, onCompletionAction);
            playbackThreadHandler.obtainMessage(0, new MessageData(index, mediaSourceHolders, callbackAction)).sendToTarget();
        } else if (onCompletionAction != null && handler != null) {
            handler.post(onCompletionAction);
        }
    }

    private void removePublicMediaSources(int fromIndex, int toIndex, Handler handler, Runnable onCompletionAction) {
        boolean z = false;
        if ((handler == null) == (onCompletionAction == null)) {
            z = true;
        }
        Assertions.checkArgument(z);
        Handler playbackThreadHandler = this.playbackThreadHandler;
        Util.removeRange(this.mediaSourcesPublic, fromIndex, toIndex);
        if (playbackThreadHandler != null) {
            HandlerAndRunnable callbackAction = createOnCompletionAction(handler, onCompletionAction);
            playbackThreadHandler.obtainMessage(1, new MessageData(fromIndex, Integer.valueOf(toIndex), callbackAction)).sendToTarget();
        } else if (onCompletionAction != null && handler != null) {
            handler.post(onCompletionAction);
        }
    }

    private void movePublicMediaSource(int currentIndex, int newIndex, Handler handler, Runnable onCompletionAction) {
        boolean z = true;
        if ((handler == null) != (onCompletionAction == null)) {
            z = false;
        }
        Assertions.checkArgument(z);
        Handler playbackThreadHandler = this.playbackThreadHandler;
        List<MediaSourceHolder> list = this.mediaSourcesPublic;
        list.add(newIndex, list.remove(currentIndex));
        if (playbackThreadHandler != null) {
            HandlerAndRunnable callbackAction = createOnCompletionAction(handler, onCompletionAction);
            playbackThreadHandler.obtainMessage(2, new MessageData(currentIndex, Integer.valueOf(newIndex), callbackAction)).sendToTarget();
        } else if (onCompletionAction != null && handler != null) {
            handler.post(onCompletionAction);
        }
    }

    private void setPublicShuffleOrder(ShuffleOrder shuffleOrder, Handler handler, Runnable onCompletionAction) {
        boolean z = true;
        if ((handler == null) != (onCompletionAction == null)) {
            z = false;
        }
        Assertions.checkArgument(z);
        Handler playbackThreadHandler = this.playbackThreadHandler;
        if (playbackThreadHandler != null) {
            int size = getSize();
            if (shuffleOrder.getLength() != size) {
                shuffleOrder = shuffleOrder.cloneAndClear().cloneAndInsert(0, size);
            }
            HandlerAndRunnable callbackAction = createOnCompletionAction(handler, onCompletionAction);
            playbackThreadHandler.obtainMessage(3, new MessageData(0, shuffleOrder, callbackAction)).sendToTarget();
            return;
        }
        this.shuffleOrder = shuffleOrder.getLength() > 0 ? shuffleOrder.cloneAndClear() : shuffleOrder;
        if (onCompletionAction != null && handler != null) {
            handler.post(onCompletionAction);
        }
    }

    private HandlerAndRunnable createOnCompletionAction(Handler handler, Runnable runnable) {
        if (handler == null || runnable == null) {
            return null;
        }
        HandlerAndRunnable handlerAndRunnable = new HandlerAndRunnable(handler, runnable);
        this.pendingOnCompletionActions.add(handlerAndRunnable);
        return handlerAndRunnable;
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                MessageData<Collection<MediaSourceHolder>> addMessage = (MessageData) Util.castNonNull(msg.obj);
                this.shuffleOrder = this.shuffleOrder.cloneAndInsert(addMessage.index, ((Collection) addMessage.customData).size());
                addMediaSourcesInternal(addMessage.index, (Collection) addMessage.customData);
                scheduleTimelineUpdate(addMessage.onCompletionAction);
                break;
            case 1:
                MessageData<Integer> removeMessage = (MessageData) Util.castNonNull(msg.obj);
                int fromIndex = removeMessage.index;
                int toIndex = ((Integer) removeMessage.customData).intValue();
                if (fromIndex == 0 && toIndex == this.shuffleOrder.getLength()) {
                    this.shuffleOrder = this.shuffleOrder.cloneAndClear();
                } else {
                    this.shuffleOrder = this.shuffleOrder.cloneAndRemove(fromIndex, toIndex);
                }
                for (int index = toIndex - 1; index >= fromIndex; index--) {
                    removeMediaSourceInternal(index);
                }
                scheduleTimelineUpdate(removeMessage.onCompletionAction);
                break;
            case 2:
                MessageData<Integer> moveMessage = (MessageData) Util.castNonNull(msg.obj);
                ShuffleOrder cloneAndRemove = this.shuffleOrder.cloneAndRemove(moveMessage.index, moveMessage.index + 1);
                this.shuffleOrder = cloneAndRemove;
                this.shuffleOrder = cloneAndRemove.cloneAndInsert(((Integer) moveMessage.customData).intValue(), 1);
                moveMediaSourceInternal(moveMessage.index, ((Integer) moveMessage.customData).intValue());
                scheduleTimelineUpdate(moveMessage.onCompletionAction);
                break;
            case 3:
                MessageData<ShuffleOrder> shuffleOrderMessage = (MessageData) Util.castNonNull(msg.obj);
                this.shuffleOrder = (ShuffleOrder) shuffleOrderMessage.customData;
                scheduleTimelineUpdate(shuffleOrderMessage.onCompletionAction);
                break;
            case 4:
                updateTimelineAndScheduleOnCompletionActions();
                break;
            case 5:
                Set<HandlerAndRunnable> actions = (Set) Util.castNonNull(msg.obj);
                dispatchOnCompletionActions(actions);
                break;
            default:
                throw new IllegalStateException();
        }
        return true;
    }

    private void scheduleTimelineUpdate() {
        scheduleTimelineUpdate(null);
    }

    private void scheduleTimelineUpdate(HandlerAndRunnable onCompletionAction) {
        if (!this.timelineUpdateScheduled) {
            getPlaybackThreadHandlerOnPlaybackThread().obtainMessage(4).sendToTarget();
            this.timelineUpdateScheduled = true;
        }
        if (onCompletionAction != null) {
            this.nextTimelineUpdateOnCompletionActions.add(onCompletionAction);
        }
    }

    private void updateTimelineAndScheduleOnCompletionActions() {
        this.timelineUpdateScheduled = false;
        Set<HandlerAndRunnable> onCompletionActions = this.nextTimelineUpdateOnCompletionActions;
        this.nextTimelineUpdateOnCompletionActions = new HashSet();
        refreshSourceInfo(new ConcatenatedTimeline(this.mediaSourceHolders, this.shuffleOrder, this.isAtomic));
        getPlaybackThreadHandlerOnPlaybackThread().obtainMessage(5, onCompletionActions).sendToTarget();
    }

    private Handler getPlaybackThreadHandlerOnPlaybackThread() {
        return (Handler) Assertions.checkNotNull(this.playbackThreadHandler);
    }

    private synchronized void dispatchOnCompletionActions(Set<HandlerAndRunnable> onCompletionActions) {
        for (HandlerAndRunnable pendingAction : onCompletionActions) {
            pendingAction.dispatch();
        }
        this.pendingOnCompletionActions.removeAll(onCompletionActions);
    }

    private void addMediaSourcesInternal(int index, Collection<MediaSourceHolder> mediaSourceHolders) {
        for (MediaSourceHolder mediaSourceHolder : mediaSourceHolders) {
            addMediaSourceInternal(index, mediaSourceHolder);
            index++;
        }
    }

    private void addMediaSourceInternal(int newIndex, MediaSourceHolder newMediaSourceHolder) {
        if (newIndex > 0) {
            MediaSourceHolder previousHolder = this.mediaSourceHolders.get(newIndex - 1);
            Timeline previousTimeline = previousHolder.mediaSource.getTimeline();
            newMediaSourceHolder.reset(newIndex, previousHolder.firstWindowIndexInChild + previousTimeline.getWindowCount());
        } else {
            newMediaSourceHolder.reset(newIndex, 0);
        }
        Timeline newTimeline = newMediaSourceHolder.mediaSource.getTimeline();
        correctOffsets(newIndex, 1, newTimeline.getWindowCount());
        this.mediaSourceHolders.add(newIndex, newMediaSourceHolder);
        this.mediaSourceByUid.put(newMediaSourceHolder.uid, newMediaSourceHolder);
        prepareChildSource(newMediaSourceHolder, newMediaSourceHolder.mediaSource);
        if (isEnabled() && this.mediaSourceByMediaPeriod.isEmpty()) {
            this.enabledMediaSourceHolders.add(newMediaSourceHolder);
        } else {
            disableChildSource(newMediaSourceHolder);
        }
    }

    private void updateMediaSourceInternal(MediaSourceHolder mediaSourceHolder, Timeline timeline) {
        if (mediaSourceHolder == null) {
            throw new IllegalArgumentException();
        }
        if (mediaSourceHolder.childIndex + 1 < this.mediaSourceHolders.size()) {
            MediaSourceHolder nextHolder = this.mediaSourceHolders.get(mediaSourceHolder.childIndex + 1);
            int windowOffsetUpdate = timeline.getWindowCount() - (nextHolder.firstWindowIndexInChild - mediaSourceHolder.firstWindowIndexInChild);
            if (windowOffsetUpdate != 0) {
                correctOffsets(mediaSourceHolder.childIndex + 1, 0, windowOffsetUpdate);
            }
        }
        scheduleTimelineUpdate();
    }

    private void removeMediaSourceInternal(int index) {
        MediaSourceHolder holder = this.mediaSourceHolders.remove(index);
        this.mediaSourceByUid.remove(holder.uid);
        Timeline oldTimeline = holder.mediaSource.getTimeline();
        correctOffsets(index, -1, -oldTimeline.getWindowCount());
        holder.isRemoved = true;
        maybeReleaseChildSource(holder);
    }

    private void moveMediaSourceInternal(int currentIndex, int newIndex) {
        int startIndex = Math.min(currentIndex, newIndex);
        int endIndex = Math.max(currentIndex, newIndex);
        int windowOffset = this.mediaSourceHolders.get(startIndex).firstWindowIndexInChild;
        List<MediaSourceHolder> list = this.mediaSourceHolders;
        list.add(newIndex, list.remove(currentIndex));
        for (int i = startIndex; i <= endIndex; i++) {
            MediaSourceHolder holder = this.mediaSourceHolders.get(i);
            holder.childIndex = i;
            holder.firstWindowIndexInChild = windowOffset;
            windowOffset += holder.mediaSource.getTimeline().getWindowCount();
        }
    }

    private void correctOffsets(int startIndex, int childIndexUpdate, int windowOffsetUpdate) {
        for (int i = startIndex; i < this.mediaSourceHolders.size(); i++) {
            MediaSourceHolder holder = this.mediaSourceHolders.get(i);
            holder.childIndex += childIndexUpdate;
            holder.firstWindowIndexInChild += windowOffsetUpdate;
        }
    }

    private void maybeReleaseChildSource(MediaSourceHolder mediaSourceHolder) {
        if (mediaSourceHolder.isRemoved && mediaSourceHolder.activeMediaPeriodIds.isEmpty()) {
            this.enabledMediaSourceHolders.remove(mediaSourceHolder);
            releaseChildSource(mediaSourceHolder);
        }
    }

    private void enableMediaSource(MediaSourceHolder mediaSourceHolder) {
        this.enabledMediaSourceHolders.add(mediaSourceHolder);
        enableChildSource(mediaSourceHolder);
    }

    private void disableUnusedMediaSources() {
        Iterator<MediaSourceHolder> iterator = this.enabledMediaSourceHolders.iterator();
        while (iterator.hasNext()) {
            MediaSourceHolder holder = iterator.next();
            if (holder.activeMediaPeriodIds.isEmpty()) {
                disableChildSource(holder);
                iterator.remove();
            }
        }
    }

    private static Object getMediaSourceHolderUid(Object periodUid) {
        return ConcatenatedTimeline.getChildTimelineUidFromConcatenatedUid(periodUid);
    }

    private static Object getChildPeriodUid(Object periodUid) {
        return ConcatenatedTimeline.getChildPeriodUidFromConcatenatedUid(periodUid);
    }

    private static Object getPeriodUid(MediaSourceHolder holder, Object childPeriodUid) {
        return ConcatenatedTimeline.getConcatenatedUid(holder.uid, childPeriodUid);
    }

    /* loaded from: classes3.dex */
    public static final class MediaSourceHolder {
        public int childIndex;
        public int firstWindowIndexInChild;
        public boolean isRemoved;
        public final MaskingMediaSource mediaSource;
        public final List<MediaSource.MediaPeriodId> activeMediaPeriodIds = new ArrayList();
        public final Object uid = new Object();

        public MediaSourceHolder(MediaSource mediaSource, boolean useLazyPreparation) {
            this.mediaSource = new MaskingMediaSource(mediaSource, useLazyPreparation);
        }

        public void reset(int childIndex, int firstWindowIndexInChild) {
            this.childIndex = childIndex;
            this.firstWindowIndexInChild = firstWindowIndexInChild;
            this.isRemoved = false;
            this.activeMediaPeriodIds.clear();
        }
    }

    /* loaded from: classes3.dex */
    public static final class MessageData<T> {
        public final T customData;
        public final int index;
        public final HandlerAndRunnable onCompletionAction;

        public MessageData(int index, T customData, HandlerAndRunnable onCompletionAction) {
            this.index = index;
            this.customData = customData;
            this.onCompletionAction = onCompletionAction;
        }
    }

    /* loaded from: classes3.dex */
    public static final class ConcatenatedTimeline extends AbstractConcatenatedTimeline {
        private final HashMap<Object, Integer> childIndexByUid = new HashMap<>();
        private final int[] firstPeriodInChildIndices;
        private final int[] firstWindowInChildIndices;
        private final int periodCount;
        private final Timeline[] timelines;
        private final Object[] uids;
        private final int windowCount;

        public ConcatenatedTimeline(Collection<MediaSourceHolder> mediaSourceHolders, ShuffleOrder shuffleOrder, boolean isAtomic) {
            super(isAtomic, shuffleOrder);
            int childCount = mediaSourceHolders.size();
            this.firstPeriodInChildIndices = new int[childCount];
            this.firstWindowInChildIndices = new int[childCount];
            this.timelines = new Timeline[childCount];
            this.uids = new Object[childCount];
            int index = 0;
            int windowCount = 0;
            int periodCount = 0;
            for (MediaSourceHolder mediaSourceHolder : mediaSourceHolders) {
                this.timelines[index] = mediaSourceHolder.mediaSource.getTimeline();
                this.firstWindowInChildIndices[index] = windowCount;
                this.firstPeriodInChildIndices[index] = periodCount;
                windowCount += this.timelines[index].getWindowCount();
                periodCount += this.timelines[index].getPeriodCount();
                this.uids[index] = mediaSourceHolder.uid;
                this.childIndexByUid.put(this.uids[index], Integer.valueOf(index));
                index++;
            }
            this.windowCount = windowCount;
            this.periodCount = periodCount;
        }

        @Override // com.google.android.exoplayer2.source.AbstractConcatenatedTimeline
        protected int getChildIndexByPeriodIndex(int periodIndex) {
            return Util.binarySearchFloor(this.firstPeriodInChildIndices, periodIndex + 1, false, false);
        }

        @Override // com.google.android.exoplayer2.source.AbstractConcatenatedTimeline
        protected int getChildIndexByWindowIndex(int windowIndex) {
            return Util.binarySearchFloor(this.firstWindowInChildIndices, windowIndex + 1, false, false);
        }

        @Override // com.google.android.exoplayer2.source.AbstractConcatenatedTimeline
        protected int getChildIndexByChildUid(Object childUid) {
            Integer index = this.childIndexByUid.get(childUid);
            if (index == null) {
                return -1;
            }
            return index.intValue();
        }

        @Override // com.google.android.exoplayer2.source.AbstractConcatenatedTimeline
        protected Timeline getTimelineByChildIndex(int childIndex) {
            return this.timelines[childIndex];
        }

        @Override // com.google.android.exoplayer2.source.AbstractConcatenatedTimeline
        protected int getFirstPeriodIndexByChildIndex(int childIndex) {
            return this.firstPeriodInChildIndices[childIndex];
        }

        @Override // com.google.android.exoplayer2.source.AbstractConcatenatedTimeline
        protected int getFirstWindowIndexByChildIndex(int childIndex) {
            return this.firstWindowInChildIndices[childIndex];
        }

        @Override // com.google.android.exoplayer2.source.AbstractConcatenatedTimeline
        protected Object getChildUidByChildIndex(int childIndex) {
            return this.uids[childIndex];
        }

        @Override // com.google.android.exoplayer2.Timeline
        public int getWindowCount() {
            return this.windowCount;
        }

        @Override // com.google.android.exoplayer2.Timeline
        public int getPeriodCount() {
            return this.periodCount;
        }
    }

    /* loaded from: classes3.dex */
    private static final class DummyMediaSource extends BaseMediaSource {
        private DummyMediaSource() {
        }

        @Override // com.google.android.exoplayer2.source.BaseMediaSource
        protected void prepareSourceInternal(TransferListener mediaTransferListener) {
        }

        @Override // com.google.android.exoplayer2.source.BaseMediaSource, com.google.android.exoplayer2.source.MediaSource
        public Object getTag() {
            return null;
        }

        @Override // com.google.android.exoplayer2.source.BaseMediaSource
        protected void releaseSourceInternal() {
        }

        @Override // com.google.android.exoplayer2.source.MediaSource
        public void maybeThrowSourceInfoRefreshError() throws IOException {
        }

        @Override // com.google.android.exoplayer2.source.MediaSource
        public MediaPeriod createPeriod(MediaSource.MediaPeriodId id, Allocator allocator, long startPositionUs) {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.android.exoplayer2.source.MediaSource
        public void releasePeriod(MediaPeriod mediaPeriod) {
        }
    }

    /* loaded from: classes3.dex */
    public static final class HandlerAndRunnable {
        private final Handler handler;
        private final Runnable runnable;

        public HandlerAndRunnable(Handler handler, Runnable runnable) {
            this.handler = handler;
            this.runnable = runnable;
        }

        public void dispatch() {
            this.handler.post(this.runnable);
        }
    }
}
