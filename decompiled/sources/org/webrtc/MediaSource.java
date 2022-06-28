package org.webrtc;
/* loaded from: classes5.dex */
public class MediaSource {
    private long nativeSource;
    private final RefCountDelegate refCountDelegate;

    private static native State nativeGetState(long j);

    /* loaded from: classes5.dex */
    public enum State {
        INITIALIZING,
        LIVE,
        ENDED,
        MUTED;

        static State fromNativeIndex(int nativeIndex) {
            return values()[nativeIndex];
        }
    }

    public MediaSource(final long nativeSource) {
        this.refCountDelegate = new RefCountDelegate(new Runnable() { // from class: org.webrtc.MediaSource$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                JniCommon.nativeReleaseRef(nativeSource);
            }
        });
        this.nativeSource = nativeSource;
    }

    public State state() {
        checkMediaSourceExists();
        return nativeGetState(this.nativeSource);
    }

    public void dispose() {
        checkMediaSourceExists();
        this.refCountDelegate.release();
        this.nativeSource = 0L;
    }

    public long getNativeMediaSource() {
        checkMediaSourceExists();
        return this.nativeSource;
    }

    public void runWithReference(Runnable runnable) {
        if (this.refCountDelegate.safeRetain()) {
            try {
                runnable.run();
            } finally {
                this.refCountDelegate.release();
            }
        }
    }

    private void checkMediaSourceExists() {
        if (this.nativeSource == 0) {
            throw new IllegalStateException("MediaSource has been disposed.");
        }
    }
}
