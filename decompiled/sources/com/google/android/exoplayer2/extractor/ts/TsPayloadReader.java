package com.google.android.exoplayer2.extractor.ts;

import android.util.SparseArray;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public interface TsPayloadReader {
    public static final int FLAG_DATA_ALIGNMENT_INDICATOR = 4;
    public static final int FLAG_PAYLOAD_UNIT_START_INDICATOR = 1;
    public static final int FLAG_RANDOM_ACCESS_INDICATOR = 2;

    /* loaded from: classes3.dex */
    public interface Factory {
        SparseArray<TsPayloadReader> createInitialPayloadReaders();

        TsPayloadReader createPayloadReader(int i, EsInfo esInfo);
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Flags {
    }

    void consume(ParsableByteArray parsableByteArray, int i) throws ParserException;

    void init(TimestampAdjuster timestampAdjuster, ExtractorOutput extractorOutput, TrackIdGenerator trackIdGenerator);

    void seek();

    /* loaded from: classes3.dex */
    public static final class EsInfo {
        public final byte[] descriptorBytes;
        public final List<DvbSubtitleInfo> dvbSubtitleInfos;
        public final String language;
        public final int streamType;

        public EsInfo(int streamType, String language, List<DvbSubtitleInfo> dvbSubtitleInfos, byte[] descriptorBytes) {
            List<DvbSubtitleInfo> list;
            this.streamType = streamType;
            this.language = language;
            if (dvbSubtitleInfos == null) {
                list = Collections.emptyList();
            } else {
                list = Collections.unmodifiableList(dvbSubtitleInfos);
            }
            this.dvbSubtitleInfos = list;
            this.descriptorBytes = descriptorBytes;
        }
    }

    /* loaded from: classes3.dex */
    public static final class DvbSubtitleInfo {
        public final byte[] initializationData;
        public final String language;
        public final int type;

        public DvbSubtitleInfo(String language, int type, byte[] initializationData) {
            this.language = language;
            this.type = type;
            this.initializationData = initializationData;
        }
    }

    /* loaded from: classes3.dex */
    public static final class TrackIdGenerator {
        private static final int ID_UNSET = Integer.MIN_VALUE;
        private final int firstTrackId;
        private String formatId;
        private final String formatIdPrefix;
        private int trackId;
        private final int trackIdIncrement;

        public TrackIdGenerator(int firstTrackId, int trackIdIncrement) {
            this(Integer.MIN_VALUE, firstTrackId, trackIdIncrement);
        }

        public TrackIdGenerator(int programNumber, int firstTrackId, int trackIdIncrement) {
            String str;
            if (programNumber != Integer.MIN_VALUE) {
                str = programNumber + "/";
            } else {
                str = "";
            }
            this.formatIdPrefix = str;
            this.firstTrackId = firstTrackId;
            this.trackIdIncrement = trackIdIncrement;
            this.trackId = Integer.MIN_VALUE;
        }

        public void generateNewId() {
            int i = this.trackId;
            this.trackId = i == Integer.MIN_VALUE ? this.firstTrackId : i + this.trackIdIncrement;
            this.formatId = this.formatIdPrefix + this.trackId;
        }

        public int getTrackId() {
            maybeThrowUninitializedError();
            return this.trackId;
        }

        public String getFormatId() {
            maybeThrowUninitializedError();
            return this.formatId;
        }

        private void maybeThrowUninitializedError() {
            if (this.trackId == Integer.MIN_VALUE) {
                throw new IllegalStateException("generateNewId() must be called before retrieving ids.");
            }
        }
    }
}
