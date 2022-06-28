package com.google.android.exoplayer2.analytics;

import android.util.Base64;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.analytics.PlaybackSessionManager;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
/* loaded from: classes3.dex */
public final class DefaultPlaybackSessionManager implements PlaybackSessionManager {
    private static final Random RANDOM = new Random();
    private static final int SESSION_ID_LENGTH = 12;
    private String currentSessionId;
    private PlaybackSessionManager.Listener listener;
    private final Timeline.Window window = new Timeline.Window();
    private final Timeline.Period period = new Timeline.Period();
    private final HashMap<String, SessionDescriptor> sessions = new HashMap<>();
    private Timeline currentTimeline = Timeline.EMPTY;

    @Override // com.google.android.exoplayer2.analytics.PlaybackSessionManager
    public void setListener(PlaybackSessionManager.Listener listener) {
        this.listener = listener;
    }

    @Override // com.google.android.exoplayer2.analytics.PlaybackSessionManager
    public synchronized String getSessionForMediaPeriodId(Timeline timeline, MediaSource.MediaPeriodId mediaPeriodId) {
        int windowIndex;
        windowIndex = timeline.getPeriodByUid(mediaPeriodId.periodUid, this.period).windowIndex;
        return getOrAddSession(windowIndex, mediaPeriodId).sessionId;
    }

    @Override // com.google.android.exoplayer2.analytics.PlaybackSessionManager
    public synchronized boolean belongsToSession(AnalyticsListener.EventTime eventTime, String sessionId) {
        SessionDescriptor sessionDescriptor = this.sessions.get(sessionId);
        if (sessionDescriptor == null) {
            return false;
        }
        sessionDescriptor.maybeSetWindowSequenceNumber(eventTime.windowIndex, eventTime.mediaPeriodId);
        return sessionDescriptor.belongsToSession(eventTime.windowIndex, eventTime.mediaPeriodId);
    }

    @Override // com.google.android.exoplayer2.analytics.PlaybackSessionManager
    public synchronized void updateSessions(AnalyticsListener.EventTime eventTime) {
        Assertions.checkNotNull(this.listener);
        SessionDescriptor currentSession = this.sessions.get(this.currentSessionId);
        if (eventTime.mediaPeriodId != null && currentSession != null) {
            boolean isAlreadyFinished = false;
            if (currentSession.windowSequenceNumber == -1) {
                if (currentSession.windowIndex != eventTime.windowIndex) {
                    isAlreadyFinished = true;
                }
            } else if (eventTime.mediaPeriodId.windowSequenceNumber < currentSession.windowSequenceNumber) {
                isAlreadyFinished = true;
            }
            if (isAlreadyFinished) {
                return;
            }
        }
        SessionDescriptor eventSession = getOrAddSession(eventTime.windowIndex, eventTime.mediaPeriodId);
        if (this.currentSessionId == null) {
            this.currentSessionId = eventSession.sessionId;
        }
        if (!eventSession.isCreated) {
            eventSession.isCreated = true;
            this.listener.onSessionCreated(eventTime, eventSession.sessionId);
        }
        if (eventSession.sessionId.equals(this.currentSessionId) && !eventSession.isActive) {
            eventSession.isActive = true;
            this.listener.onSessionActive(eventTime, eventSession.sessionId);
        }
    }

    @Override // com.google.android.exoplayer2.analytics.PlaybackSessionManager
    public synchronized void handleTimelineUpdate(AnalyticsListener.EventTime eventTime) {
        Assertions.checkNotNull(this.listener);
        Timeline previousTimeline = this.currentTimeline;
        this.currentTimeline = eventTime.timeline;
        Iterator<SessionDescriptor> iterator = this.sessions.values().iterator();
        while (iterator.hasNext()) {
            SessionDescriptor session = iterator.next();
            if (!session.tryResolvingToNewTimeline(previousTimeline, this.currentTimeline)) {
                iterator.remove();
                if (session.isCreated) {
                    if (session.sessionId.equals(this.currentSessionId)) {
                        this.currentSessionId = null;
                    }
                    this.listener.onSessionFinished(eventTime, session.sessionId, false);
                }
            }
        }
        handlePositionDiscontinuity(eventTime, 4);
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0021 A[Catch: all -> 0x00da, TryCatch #0 {, blocks: (B:3:0x0001, B:10:0x0011, B:11:0x001b, B:13:0x0021, B:15:0x002d, B:17:0x0036, B:20:0x0044, B:25:0x004f, B:26:0x0052, B:28:0x005c, B:30:0x0078, B:33:0x0082, B:35:0x008e, B:37:0x0094, B:39:0x00a0, B:41:0x00ac, B:43:0x00c5, B:45:0x00cb), top: B:50:0x0001 }] */
    @Override // com.google.android.exoplayer2.analytics.PlaybackSessionManager
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized void handlePositionDiscontinuity(com.google.android.exoplayer2.analytics.AnalyticsListener.EventTime r10, int r11) {
        /*
            r9 = this;
            monitor-enter(r9)
            com.google.android.exoplayer2.analytics.PlaybackSessionManager$Listener r0 = r9.listener     // Catch: java.lang.Throwable -> Lda
            com.google.android.exoplayer2.util.Assertions.checkNotNull(r0)     // Catch: java.lang.Throwable -> Lda
            r0 = 0
            r1 = 1
            if (r11 == 0) goto L10
            r2 = 3
            if (r11 != r2) goto Le
            goto L10
        Le:
            r2 = 0
            goto L11
        L10:
            r2 = 1
        L11:
            java.util.HashMap<java.lang.String, com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager$SessionDescriptor> r3 = r9.sessions     // Catch: java.lang.Throwable -> Lda
            java.util.Collection r3 = r3.values()     // Catch: java.lang.Throwable -> Lda
            java.util.Iterator r3 = r3.iterator()     // Catch: java.lang.Throwable -> Lda
        L1b:
            boolean r4 = r3.hasNext()     // Catch: java.lang.Throwable -> Lda
            if (r4 == 0) goto L5c
            java.lang.Object r4 = r3.next()     // Catch: java.lang.Throwable -> Lda
            com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager$SessionDescriptor r4 = (com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor) r4     // Catch: java.lang.Throwable -> Lda
            boolean r5 = r4.isFinishedAtEventTime(r10)     // Catch: java.lang.Throwable -> Lda
            if (r5 == 0) goto L5b
            r3.remove()     // Catch: java.lang.Throwable -> Lda
            boolean r5 = com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor.access$300(r4)     // Catch: java.lang.Throwable -> Lda
            if (r5 == 0) goto L5b
            java.lang.String r5 = com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor.access$000(r4)     // Catch: java.lang.Throwable -> Lda
            java.lang.String r6 = r9.currentSessionId     // Catch: java.lang.Throwable -> Lda
            boolean r5 = r5.equals(r6)     // Catch: java.lang.Throwable -> Lda
            if (r2 == 0) goto L4c
            if (r5 == 0) goto L4c
            boolean r6 = com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor.access$400(r4)     // Catch: java.lang.Throwable -> Lda
            if (r6 == 0) goto L4c
            r6 = 1
            goto L4d
        L4c:
            r6 = 0
        L4d:
            if (r5 == 0) goto L52
            r7 = 0
            r9.currentSessionId = r7     // Catch: java.lang.Throwable -> Lda
        L52:
            com.google.android.exoplayer2.analytics.PlaybackSessionManager$Listener r7 = r9.listener     // Catch: java.lang.Throwable -> Lda
            java.lang.String r8 = com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor.access$000(r4)     // Catch: java.lang.Throwable -> Lda
            r7.onSessionFinished(r10, r8, r6)     // Catch: java.lang.Throwable -> Lda
        L5b:
            goto L1b
        L5c:
            java.util.HashMap<java.lang.String, com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager$SessionDescriptor> r0 = r9.sessions     // Catch: java.lang.Throwable -> Lda
            java.lang.String r1 = r9.currentSessionId     // Catch: java.lang.Throwable -> Lda
            java.lang.Object r0 = r0.get(r1)     // Catch: java.lang.Throwable -> Lda
            com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager$SessionDescriptor r0 = (com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor) r0     // Catch: java.lang.Throwable -> Lda
            int r1 = r10.windowIndex     // Catch: java.lang.Throwable -> Lda
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r4 = r10.mediaPeriodId     // Catch: java.lang.Throwable -> Lda
            com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager$SessionDescriptor r1 = r9.getOrAddSession(r1, r4)     // Catch: java.lang.Throwable -> Lda
            java.lang.String r4 = com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor.access$000(r1)     // Catch: java.lang.Throwable -> Lda
            r9.currentSessionId = r4     // Catch: java.lang.Throwable -> Lda
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r4 = r10.mediaPeriodId     // Catch: java.lang.Throwable -> Lda
            if (r4 == 0) goto Ld8
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r4 = r10.mediaPeriodId     // Catch: java.lang.Throwable -> Lda
            boolean r4 = r4.isAd()     // Catch: java.lang.Throwable -> Lda
            if (r4 == 0) goto Ld8
            if (r0 == 0) goto Lac
            long r4 = com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor.access$100(r0)     // Catch: java.lang.Throwable -> Lda
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r6 = r10.mediaPeriodId     // Catch: java.lang.Throwable -> Lda
            long r6 = r6.windowSequenceNumber     // Catch: java.lang.Throwable -> Lda
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 != 0) goto Lac
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r4 = com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor.access$500(r0)     // Catch: java.lang.Throwable -> Lda
            if (r4 == 0) goto Lac
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r4 = com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor.access$500(r0)     // Catch: java.lang.Throwable -> Lda
            int r4 = r4.adGroupIndex     // Catch: java.lang.Throwable -> Lda
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r5 = r10.mediaPeriodId     // Catch: java.lang.Throwable -> Lda
            int r5 = r5.adGroupIndex     // Catch: java.lang.Throwable -> Lda
            if (r4 != r5) goto Lac
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r4 = com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor.access$500(r0)     // Catch: java.lang.Throwable -> Lda
            int r4 = r4.adIndexInAdGroup     // Catch: java.lang.Throwable -> Lda
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r5 = r10.mediaPeriodId     // Catch: java.lang.Throwable -> Lda
            int r5 = r5.adIndexInAdGroup     // Catch: java.lang.Throwable -> Lda
            if (r4 == r5) goto Ld8
        Lac:
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r4 = new com.google.android.exoplayer2.source.MediaSource$MediaPeriodId     // Catch: java.lang.Throwable -> Lda
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r5 = r10.mediaPeriodId     // Catch: java.lang.Throwable -> Lda
            java.lang.Object r5 = r5.periodUid     // Catch: java.lang.Throwable -> Lda
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r6 = r10.mediaPeriodId     // Catch: java.lang.Throwable -> Lda
            long r6 = r6.windowSequenceNumber     // Catch: java.lang.Throwable -> Lda
            r4.<init>(r5, r6)     // Catch: java.lang.Throwable -> Lda
            int r5 = r10.windowIndex     // Catch: java.lang.Throwable -> Lda
            com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager$SessionDescriptor r5 = r9.getOrAddSession(r5, r4)     // Catch: java.lang.Throwable -> Lda
            boolean r6 = com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor.access$300(r5)     // Catch: java.lang.Throwable -> Lda
            if (r6 == 0) goto Ld8
            boolean r6 = com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor.access$300(r1)     // Catch: java.lang.Throwable -> Lda
            if (r6 == 0) goto Ld8
            com.google.android.exoplayer2.analytics.PlaybackSessionManager$Listener r6 = r9.listener     // Catch: java.lang.Throwable -> Lda
            java.lang.String r7 = com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor.access$000(r5)     // Catch: java.lang.Throwable -> Lda
            java.lang.String r8 = com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor.access$000(r1)     // Catch: java.lang.Throwable -> Lda
            r6.onAdPlaybackStarted(r10, r7, r8)     // Catch: java.lang.Throwable -> Lda
        Ld8:
            monitor-exit(r9)
            return
        Lda:
            r10 = move-exception
            monitor-exit(r9)
            goto Lde
        Ldd:
            throw r10
        Lde:
            goto Ldd
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.handlePositionDiscontinuity(com.google.android.exoplayer2.analytics.AnalyticsListener$EventTime, int):void");
    }

    @Override // com.google.android.exoplayer2.analytics.PlaybackSessionManager
    public void finishAllSessions(AnalyticsListener.EventTime eventTime) {
        PlaybackSessionManager.Listener listener;
        this.currentSessionId = null;
        Iterator<SessionDescriptor> iterator = this.sessions.values().iterator();
        while (iterator.hasNext()) {
            SessionDescriptor session = iterator.next();
            iterator.remove();
            if (session.isCreated && (listener = this.listener) != null) {
                listener.onSessionFinished(eventTime, session.sessionId, false);
            }
        }
    }

    private SessionDescriptor getOrAddSession(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
        SessionDescriptor bestMatch = null;
        long bestMatchWindowSequenceNumber = Long.MAX_VALUE;
        for (SessionDescriptor sessionDescriptor : this.sessions.values()) {
            sessionDescriptor.maybeSetWindowSequenceNumber(windowIndex, mediaPeriodId);
            if (sessionDescriptor.belongsToSession(windowIndex, mediaPeriodId)) {
                long windowSequenceNumber = sessionDescriptor.windowSequenceNumber;
                if (windowSequenceNumber == -1 || windowSequenceNumber < bestMatchWindowSequenceNumber) {
                    bestMatch = sessionDescriptor;
                    bestMatchWindowSequenceNumber = windowSequenceNumber;
                } else if (windowSequenceNumber == bestMatchWindowSequenceNumber && ((SessionDescriptor) Util.castNonNull(bestMatch)).adMediaPeriodId != null && sessionDescriptor.adMediaPeriodId != null) {
                    bestMatch = sessionDescriptor;
                }
            }
        }
        if (bestMatch == null) {
            String sessionId = generateSessionId();
            SessionDescriptor bestMatch2 = new SessionDescriptor(sessionId, windowIndex, mediaPeriodId);
            this.sessions.put(sessionId, bestMatch2);
            return bestMatch2;
        }
        return bestMatch;
    }

    private static String generateSessionId() {
        byte[] randomBytes = new byte[12];
        RANDOM.nextBytes(randomBytes);
        return Base64.encodeToString(randomBytes, 10);
    }

    /* loaded from: classes3.dex */
    public final class SessionDescriptor {
        private MediaSource.MediaPeriodId adMediaPeriodId;
        private boolean isActive;
        private boolean isCreated;
        private final String sessionId;
        private int windowIndex;
        private long windowSequenceNumber;

        public SessionDescriptor(String sessionId, int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
            DefaultPlaybackSessionManager.this = r3;
            this.sessionId = sessionId;
            this.windowIndex = windowIndex;
            this.windowSequenceNumber = mediaPeriodId == null ? -1L : mediaPeriodId.windowSequenceNumber;
            if (mediaPeriodId != null && mediaPeriodId.isAd()) {
                this.adMediaPeriodId = mediaPeriodId;
            }
        }

        public boolean tryResolvingToNewTimeline(Timeline oldTimeline, Timeline newTimeline) {
            int resolveWindowIndexToNewTimeline = resolveWindowIndexToNewTimeline(oldTimeline, newTimeline, this.windowIndex);
            this.windowIndex = resolveWindowIndexToNewTimeline;
            if (resolveWindowIndexToNewTimeline == -1) {
                return false;
            }
            MediaSource.MediaPeriodId mediaPeriodId = this.adMediaPeriodId;
            if (mediaPeriodId == null) {
                return true;
            }
            int newPeriodIndex = newTimeline.getIndexOfPeriod(mediaPeriodId.periodUid);
            return newPeriodIndex != -1;
        }

        public boolean belongsToSession(int eventWindowIndex, MediaSource.MediaPeriodId eventMediaPeriodId) {
            return eventMediaPeriodId == null ? eventWindowIndex == this.windowIndex : this.adMediaPeriodId == null ? !eventMediaPeriodId.isAd() && eventMediaPeriodId.windowSequenceNumber == this.windowSequenceNumber : eventMediaPeriodId.windowSequenceNumber == this.adMediaPeriodId.windowSequenceNumber && eventMediaPeriodId.adGroupIndex == this.adMediaPeriodId.adGroupIndex && eventMediaPeriodId.adIndexInAdGroup == this.adMediaPeriodId.adIndexInAdGroup;
        }

        public void maybeSetWindowSequenceNumber(int eventWindowIndex, MediaSource.MediaPeriodId eventMediaPeriodId) {
            if (this.windowSequenceNumber == -1 && eventWindowIndex == this.windowIndex && eventMediaPeriodId != null) {
                this.windowSequenceNumber = eventMediaPeriodId.windowSequenceNumber;
            }
        }

        public boolean isFinishedAtEventTime(AnalyticsListener.EventTime eventTime) {
            if (this.windowSequenceNumber == -1) {
                return false;
            }
            if (eventTime.mediaPeriodId == null) {
                return this.windowIndex != eventTime.windowIndex;
            } else if (eventTime.mediaPeriodId.windowSequenceNumber > this.windowSequenceNumber) {
                return true;
            } else {
                if (this.adMediaPeriodId == null) {
                    return false;
                }
                int eventPeriodIndex = eventTime.timeline.getIndexOfPeriod(eventTime.mediaPeriodId.periodUid);
                int adPeriodIndex = eventTime.timeline.getIndexOfPeriod(this.adMediaPeriodId.periodUid);
                if (eventTime.mediaPeriodId.windowSequenceNumber < this.adMediaPeriodId.windowSequenceNumber || eventPeriodIndex < adPeriodIndex) {
                    return false;
                }
                if (eventPeriodIndex > adPeriodIndex) {
                    return true;
                }
                if (!eventTime.mediaPeriodId.isAd()) {
                    return eventTime.mediaPeriodId.nextAdGroupIndex == -1 || eventTime.mediaPeriodId.nextAdGroupIndex > this.adMediaPeriodId.adGroupIndex;
                }
                int eventAdGroup = eventTime.mediaPeriodId.adGroupIndex;
                int eventAdIndex = eventTime.mediaPeriodId.adIndexInAdGroup;
                return eventAdGroup > this.adMediaPeriodId.adGroupIndex || (eventAdGroup == this.adMediaPeriodId.adGroupIndex && eventAdIndex > this.adMediaPeriodId.adIndexInAdGroup);
            }
        }

        private int resolveWindowIndexToNewTimeline(Timeline oldTimeline, Timeline newTimeline, int windowIndex) {
            if (windowIndex < oldTimeline.getWindowCount()) {
                oldTimeline.getWindow(windowIndex, DefaultPlaybackSessionManager.this.window);
                for (int periodIndex = DefaultPlaybackSessionManager.this.window.firstPeriodIndex; periodIndex <= DefaultPlaybackSessionManager.this.window.lastPeriodIndex; periodIndex++) {
                    Object periodUid = oldTimeline.getUidOfPeriod(periodIndex);
                    int newPeriodIndex = newTimeline.getIndexOfPeriod(periodUid);
                    if (newPeriodIndex != -1) {
                        return newTimeline.getPeriod(newPeriodIndex, DefaultPlaybackSessionManager.this.period).windowIndex;
                    }
                }
                return -1;
            } else if (windowIndex < newTimeline.getWindowCount()) {
                return windowIndex;
            } else {
                return -1;
            }
        }
    }
}
