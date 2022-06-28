package com.google.android.exoplayer2.metadata.scte35;

import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataDecoder;
import com.google.android.exoplayer2.metadata.MetadataInputBuffer;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import java.nio.ByteBuffer;
/* loaded from: classes3.dex */
public final class SpliceInfoDecoder implements MetadataDecoder {
    private static final int TYPE_PRIVATE_COMMAND = 255;
    private static final int TYPE_SPLICE_INSERT = 5;
    private static final int TYPE_SPLICE_NULL = 0;
    private static final int TYPE_SPLICE_SCHEDULE = 4;
    private static final int TYPE_TIME_SIGNAL = 6;
    private final ParsableByteArray sectionData = new ParsableByteArray();
    private final ParsableBitArray sectionHeader = new ParsableBitArray();
    private TimestampAdjuster timestampAdjuster;

    @Override // com.google.android.exoplayer2.metadata.MetadataDecoder
    public Metadata decode(MetadataInputBuffer inputBuffer) {
        ByteBuffer buffer = (ByteBuffer) Assertions.checkNotNull(inputBuffer.data);
        if (this.timestampAdjuster == null || inputBuffer.subsampleOffsetUs != this.timestampAdjuster.getTimestampOffsetUs()) {
            TimestampAdjuster timestampAdjuster = new TimestampAdjuster(inputBuffer.timeUs);
            this.timestampAdjuster = timestampAdjuster;
            timestampAdjuster.adjustSampleTimestamp(inputBuffer.timeUs - inputBuffer.subsampleOffsetUs);
        }
        byte[] data = buffer.array();
        int size = buffer.limit();
        this.sectionData.reset(data, size);
        this.sectionHeader.reset(data, size);
        this.sectionHeader.skipBits(39);
        long ptsAdjustment = (this.sectionHeader.readBits(1) << 32) | this.sectionHeader.readBits(32);
        this.sectionHeader.skipBits(20);
        int spliceCommandLength = this.sectionHeader.readBits(12);
        int spliceCommandType = this.sectionHeader.readBits(8);
        SpliceCommand command = null;
        this.sectionData.skipBytes(14);
        switch (spliceCommandType) {
            case 0:
                command = new SpliceNullCommand();
                break;
            case 4:
                command = SpliceScheduleCommand.parseFromSection(this.sectionData);
                break;
            case 5:
                command = SpliceInsertCommand.parseFromSection(this.sectionData, ptsAdjustment, this.timestampAdjuster);
                break;
            case 6:
                command = TimeSignalCommand.parseFromSection(this.sectionData, ptsAdjustment, this.timestampAdjuster);
                break;
            case 255:
                command = PrivateCommand.parseFromSection(this.sectionData, spliceCommandLength, ptsAdjustment);
                break;
        }
        return command == null ? new Metadata(new Metadata.Entry[0]) : new Metadata(command);
    }
}
