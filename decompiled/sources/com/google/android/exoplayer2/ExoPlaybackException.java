package com.google.android.exoplayer2;

import android.os.SystemClock;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public final class ExoPlaybackException extends Exception {
    public static final int TYPE_OUT_OF_MEMORY = 4;
    public static final int TYPE_REMOTE = 3;
    public static final int TYPE_RENDERER = 1;
    public static final int TYPE_SOURCE = 0;
    public static final int TYPE_UNEXPECTED = 2;
    private final Throwable cause;
    public final Format rendererFormat;
    public final int rendererFormatSupport;
    public final int rendererIndex;
    public final long timestampMs;
    public final int type;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Type {
    }

    public static ExoPlaybackException createForSource(IOException cause) {
        return new ExoPlaybackException(0, cause);
    }

    public static ExoPlaybackException createForRenderer(Exception cause, int rendererIndex, Format rendererFormat, int rendererFormatSupport) {
        return new ExoPlaybackException(1, cause, rendererIndex, rendererFormat, rendererFormat == null ? 4 : rendererFormatSupport);
    }

    public static ExoPlaybackException createForUnexpected(RuntimeException cause) {
        return new ExoPlaybackException(2, cause);
    }

    public static ExoPlaybackException createForRemote(String message) {
        return new ExoPlaybackException(3, message);
    }

    public static ExoPlaybackException createForOutOfMemoryError(OutOfMemoryError cause) {
        return new ExoPlaybackException(4, cause);
    }

    private ExoPlaybackException(int type, Throwable cause) {
        this(type, cause, -1, null, 4);
    }

    private ExoPlaybackException(int type, Throwable cause, int rendererIndex, Format rendererFormat, int rendererFormatSupport) {
        super(cause);
        this.type = type;
        this.cause = cause;
        this.rendererIndex = rendererIndex;
        this.rendererFormat = rendererFormat;
        this.rendererFormatSupport = rendererFormatSupport;
        this.timestampMs = SystemClock.elapsedRealtime();
    }

    private ExoPlaybackException(int type, String message) {
        super(message);
        this.type = type;
        this.rendererIndex = -1;
        this.rendererFormat = null;
        this.rendererFormatSupport = 0;
        this.cause = null;
        this.timestampMs = SystemClock.elapsedRealtime();
    }

    public IOException getSourceException() {
        Assertions.checkState(this.type == 0);
        return (IOException) Assertions.checkNotNull(this.cause);
    }

    public Exception getRendererException() {
        boolean z = true;
        if (this.type != 1) {
            z = false;
        }
        Assertions.checkState(z);
        return (Exception) Assertions.checkNotNull(this.cause);
    }

    public RuntimeException getUnexpectedException() {
        Assertions.checkState(this.type == 2);
        return (RuntimeException) Assertions.checkNotNull(this.cause);
    }

    public OutOfMemoryError getOutOfMemoryError() {
        Assertions.checkState(this.type == 4);
        return (OutOfMemoryError) Assertions.checkNotNull(this.cause);
    }
}
