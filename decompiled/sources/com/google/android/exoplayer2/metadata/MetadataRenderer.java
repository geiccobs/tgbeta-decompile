package com.google.android.exoplayer2.metadata;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes3.dex */
public final class MetadataRenderer extends BaseRenderer implements Handler.Callback {
    private static final int MAX_PENDING_METADATA_COUNT = 5;
    private static final int MSG_INVOKE_RENDERER = 0;
    private final MetadataInputBuffer buffer;
    private MetadataDecoder decoder;
    private final MetadataDecoderFactory decoderFactory;
    private boolean inputStreamEnded;
    private final MetadataOutput output;
    private final Handler outputHandler;
    private final Metadata[] pendingMetadata;
    private int pendingMetadataCount;
    private int pendingMetadataIndex;
    private final long[] pendingMetadataTimestamps;
    private long subsampleOffsetUs;

    public MetadataRenderer(MetadataOutput output, Looper outputLooper) {
        this(output, outputLooper, MetadataDecoderFactory.DEFAULT);
    }

    public MetadataRenderer(MetadataOutput output, Looper outputLooper, MetadataDecoderFactory decoderFactory) {
        super(4);
        this.output = (MetadataOutput) Assertions.checkNotNull(output);
        this.outputHandler = outputLooper == null ? null : Util.createHandler(outputLooper, this);
        this.decoderFactory = (MetadataDecoderFactory) Assertions.checkNotNull(decoderFactory);
        this.buffer = new MetadataInputBuffer();
        this.pendingMetadata = new Metadata[5];
        this.pendingMetadataTimestamps = new long[5];
    }

    @Override // com.google.android.exoplayer2.RendererCapabilities
    public int supportsFormat(Format format) {
        if (this.decoderFactory.supportsFormat(format)) {
            return RendererCapabilities.CC.create(supportsFormatDrm(null, format.drmInitData) ? 4 : 2);
        }
        return RendererCapabilities.CC.create(0);
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    public void onStreamChanged(Format[] formats, long offsetUs) {
        this.decoder = this.decoderFactory.createDecoder(formats[0]);
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    protected void onPositionReset(long positionUs, boolean joining) {
        flushPendingMetadata();
        this.inputStreamEnded = false;
    }

    @Override // com.google.android.exoplayer2.Renderer
    public void render(long positionUs, long elapsedRealtimeUs) {
        if (!this.inputStreamEnded && this.pendingMetadataCount < 5) {
            this.buffer.clear();
            FormatHolder formatHolder = getFormatHolder();
            int result = readSource(formatHolder, this.buffer, false);
            if (result == -4) {
                if (this.buffer.isEndOfStream()) {
                    this.inputStreamEnded = true;
                } else if (!this.buffer.isDecodeOnly()) {
                    this.buffer.subsampleOffsetUs = this.subsampleOffsetUs;
                    this.buffer.flip();
                    Metadata metadata = ((MetadataDecoder) Util.castNonNull(this.decoder)).decode(this.buffer);
                    if (metadata != null) {
                        List<Metadata.Entry> entries = new ArrayList<>(metadata.length());
                        decodeWrappedMetadata(metadata, entries);
                        if (!entries.isEmpty()) {
                            Metadata expandedMetadata = new Metadata(entries);
                            int index = (this.pendingMetadataIndex + this.pendingMetadataCount) % 5;
                            this.pendingMetadata[index] = expandedMetadata;
                            this.pendingMetadataTimestamps[index] = this.buffer.timeUs;
                            this.pendingMetadataCount++;
                        }
                    }
                }
            } else if (result == -5) {
                this.subsampleOffsetUs = ((Format) Assertions.checkNotNull(formatHolder.format)).subsampleOffsetUs;
            }
        }
        if (this.pendingMetadataCount > 0) {
            long[] jArr = this.pendingMetadataTimestamps;
            int i = this.pendingMetadataIndex;
            if (jArr[i] <= positionUs) {
                invokeRenderer((Metadata) Util.castNonNull(this.pendingMetadata[i]));
                Metadata[] metadataArr = this.pendingMetadata;
                int i2 = this.pendingMetadataIndex;
                metadataArr[i2] = null;
                this.pendingMetadataIndex = (i2 + 1) % 5;
                this.pendingMetadataCount--;
            }
        }
    }

    private void decodeWrappedMetadata(Metadata metadata, List<Metadata.Entry> decodedEntries) {
        for (int i = 0; i < metadata.length(); i++) {
            Format wrappedMetadataFormat = metadata.get(i).getWrappedMetadataFormat();
            if (wrappedMetadataFormat != null && this.decoderFactory.supportsFormat(wrappedMetadataFormat)) {
                MetadataDecoder wrappedMetadataDecoder = this.decoderFactory.createDecoder(wrappedMetadataFormat);
                byte[] wrappedMetadataBytes = (byte[]) Assertions.checkNotNull(metadata.get(i).getWrappedMetadataBytes());
                this.buffer.clear();
                this.buffer.ensureSpaceForWrite(wrappedMetadataBytes.length);
                ((ByteBuffer) Util.castNonNull(this.buffer.data)).put(wrappedMetadataBytes);
                this.buffer.flip();
                Metadata innerMetadata = wrappedMetadataDecoder.decode(this.buffer);
                if (innerMetadata != null) {
                    decodeWrappedMetadata(innerMetadata, decodedEntries);
                }
            } else {
                decodedEntries.add(metadata.get(i));
            }
        }
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    protected void onDisabled() {
        flushPendingMetadata();
        this.decoder = null;
    }

    @Override // com.google.android.exoplayer2.Renderer
    public boolean isEnded() {
        return this.inputStreamEnded;
    }

    @Override // com.google.android.exoplayer2.Renderer
    public boolean isReady() {
        return true;
    }

    private void invokeRenderer(Metadata metadata) {
        Handler handler = this.outputHandler;
        if (handler != null) {
            handler.obtainMessage(0, metadata).sendToTarget();
        } else {
            invokeRendererInternal(metadata);
        }
    }

    private void flushPendingMetadata() {
        Arrays.fill(this.pendingMetadata, (Object) null);
        this.pendingMetadataIndex = 0;
        this.pendingMetadataCount = 0;
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                invokeRendererInternal((Metadata) msg.obj);
                return true;
            default:
                throw new IllegalStateException();
        }
    }

    private void invokeRendererInternal(Metadata metadata) {
        this.output.onMetadata(metadata);
    }
}
