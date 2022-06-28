package com.google.android.exoplayer2;

import android.os.Looper;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmSession;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MediaClock;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
/* loaded from: classes3.dex */
public abstract class BaseRenderer implements Renderer, RendererCapabilities {
    private RendererConfiguration configuration;
    private int index;
    private int state;
    private SampleStream stream;
    private Format[] streamFormats;
    private boolean streamIsFinal;
    private long streamOffsetUs;
    private boolean throwRendererExceptionIsExecuting;
    private final int trackType;
    private final FormatHolder formatHolder = new FormatHolder();
    private long readingPositionUs = Long.MIN_VALUE;

    @Override // com.google.android.exoplayer2.Renderer
    public /* synthetic */ void setOperatingRate(float f) {
        Renderer.CC.$default$setOperatingRate(this, f);
    }

    public BaseRenderer(int trackType) {
        this.trackType = trackType;
    }

    @Override // com.google.android.exoplayer2.Renderer, com.google.android.exoplayer2.RendererCapabilities
    public final int getTrackType() {
        return this.trackType;
    }

    @Override // com.google.android.exoplayer2.Renderer
    public final RendererCapabilities getCapabilities() {
        return this;
    }

    @Override // com.google.android.exoplayer2.Renderer
    public final void setIndex(int index) {
        this.index = index;
    }

    @Override // com.google.android.exoplayer2.Renderer
    public MediaClock getMediaClock() {
        return null;
    }

    @Override // com.google.android.exoplayer2.Renderer
    public final int getState() {
        return this.state;
    }

    @Override // com.google.android.exoplayer2.Renderer
    public final void enable(RendererConfiguration configuration, Format[] formats, SampleStream stream, long positionUs, boolean joining, long offsetUs) throws ExoPlaybackException {
        Assertions.checkState(this.state == 0);
        this.configuration = configuration;
        this.state = 1;
        onEnabled(joining);
        replaceStream(formats, stream, offsetUs);
        onPositionReset(positionUs, joining);
    }

    @Override // com.google.android.exoplayer2.Renderer
    public final void start() throws ExoPlaybackException {
        boolean z = true;
        if (this.state != 1) {
            z = false;
        }
        Assertions.checkState(z);
        this.state = 2;
        onStarted();
    }

    @Override // com.google.android.exoplayer2.Renderer
    public final void replaceStream(Format[] formats, SampleStream stream, long offsetUs) throws ExoPlaybackException {
        Assertions.checkState(!this.streamIsFinal);
        this.stream = stream;
        this.readingPositionUs = offsetUs;
        this.streamFormats = formats;
        this.streamOffsetUs = offsetUs;
        onStreamChanged(formats, offsetUs);
    }

    @Override // com.google.android.exoplayer2.Renderer
    public final SampleStream getStream() {
        return this.stream;
    }

    @Override // com.google.android.exoplayer2.Renderer
    public final boolean hasReadStreamToEnd() {
        return this.readingPositionUs == Long.MIN_VALUE;
    }

    @Override // com.google.android.exoplayer2.Renderer
    public final long getReadingPositionUs() {
        return this.readingPositionUs;
    }

    @Override // com.google.android.exoplayer2.Renderer
    public final void setCurrentStreamFinal() {
        this.streamIsFinal = true;
    }

    @Override // com.google.android.exoplayer2.Renderer
    public final boolean isCurrentStreamFinal() {
        return this.streamIsFinal;
    }

    @Override // com.google.android.exoplayer2.Renderer
    public final void maybeThrowStreamError() throws IOException {
        this.stream.maybeThrowError();
    }

    @Override // com.google.android.exoplayer2.Renderer
    public final void resetPosition(long positionUs) throws ExoPlaybackException {
        this.streamIsFinal = false;
        this.readingPositionUs = positionUs;
        onPositionReset(positionUs, false);
    }

    @Override // com.google.android.exoplayer2.Renderer
    public final void stop() throws ExoPlaybackException {
        Assertions.checkState(this.state == 2);
        this.state = 1;
        onStopped();
    }

    @Override // com.google.android.exoplayer2.Renderer
    public final void disable() {
        boolean z = true;
        if (this.state != 1) {
            z = false;
        }
        Assertions.checkState(z);
        this.formatHolder.clear();
        this.state = 0;
        this.stream = null;
        this.streamFormats = null;
        this.streamIsFinal = false;
        onDisabled();
    }

    @Override // com.google.android.exoplayer2.Renderer
    public final void reset() {
        Assertions.checkState(this.state == 0);
        this.formatHolder.clear();
        onReset();
    }

    @Override // com.google.android.exoplayer2.RendererCapabilities
    public int supportsMixedMimeTypeAdaptation() throws ExoPlaybackException {
        return 0;
    }

    @Override // com.google.android.exoplayer2.PlayerMessage.Target
    public void handleMessage(int what, Object object) throws ExoPlaybackException {
    }

    protected void onEnabled(boolean joining) throws ExoPlaybackException {
    }

    public void onStreamChanged(Format[] formats, long offsetUs) throws ExoPlaybackException {
    }

    protected void onPositionReset(long positionUs, boolean joining) throws ExoPlaybackException {
    }

    protected void onStarted() throws ExoPlaybackException {
    }

    protected void onStopped() throws ExoPlaybackException {
    }

    protected void onDisabled() {
    }

    protected void onReset() {
    }

    public final FormatHolder getFormatHolder() {
        this.formatHolder.clear();
        return this.formatHolder;
    }

    public final Format[] getStreamFormats() {
        return this.streamFormats;
    }

    public final RendererConfiguration getConfiguration() {
        return this.configuration;
    }

    public final <T extends ExoMediaCrypto> DrmSession<T> getUpdatedSourceDrmSession(Format oldFormat, Format newFormat, DrmSessionManager<T> drmSessionManager, DrmSession<T> existingSourceSession) throws ExoPlaybackException {
        boolean drmInitDataChanged = !Util.areEqual(newFormat.drmInitData, oldFormat == null ? null : oldFormat.drmInitData);
        if (!drmInitDataChanged) {
            return existingSourceSession;
        }
        DrmSession<T> newSourceDrmSession = null;
        if (newFormat.drmInitData != null) {
            if (drmSessionManager == null) {
                throw createRendererException(new IllegalStateException("Media requires a DrmSessionManager"), newFormat);
            }
            newSourceDrmSession = drmSessionManager.acquireSession((Looper) Assertions.checkNotNull(Looper.myLooper()), newFormat.drmInitData);
        }
        if (existingSourceSession != null) {
            existingSourceSession.release();
        }
        return newSourceDrmSession;
    }

    protected final int getIndex() {
        return this.index;
    }

    public final ExoPlaybackException createRendererException(Exception cause, Format format) {
        int formatSupport = 4;
        if (format != null && !this.throwRendererExceptionIsExecuting) {
            this.throwRendererExceptionIsExecuting = true;
            try {
                formatSupport = RendererCapabilities.CC.getFormatSupport(supportsFormat(format));
            } catch (ExoPlaybackException e) {
            } catch (Throwable th) {
                this.throwRendererExceptionIsExecuting = false;
                throw th;
            }
            this.throwRendererExceptionIsExecuting = false;
        }
        return ExoPlaybackException.createForRenderer(cause, getIndex(), format, formatSupport);
    }

    public final int readSource(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired) {
        int result = this.stream.readData(formatHolder, buffer, formatRequired);
        if (result == -4) {
            if (buffer.isEndOfStream()) {
                this.readingPositionUs = Long.MIN_VALUE;
                return this.streamIsFinal ? -4 : -3;
            }
            buffer.timeUs += this.streamOffsetUs;
            this.readingPositionUs = Math.max(this.readingPositionUs, buffer.timeUs);
        } else if (result == -5) {
            Format format = formatHolder.format;
            if (format.subsampleOffsetUs != Long.MAX_VALUE) {
                formatHolder.format = format.copyWithSubsampleOffsetUs(format.subsampleOffsetUs + this.streamOffsetUs);
            }
        }
        return result;
    }

    public int skipSource(long positionUs) {
        return this.stream.skipData(positionUs - this.streamOffsetUs);
    }

    public final boolean isSourceReady() {
        return hasReadStreamToEnd() ? this.streamIsFinal : this.stream.isReady();
    }

    public static boolean supportsFormatDrm(DrmSessionManager<?> drmSessionManager, DrmInitData drmInitData) {
        if (drmInitData == null) {
            return true;
        }
        if (drmSessionManager == null) {
            return false;
        }
        return drmSessionManager.canAcquireSession(drmInitData);
    }
}
