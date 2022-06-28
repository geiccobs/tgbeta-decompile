package com.google.android.exoplayer2.metadata.scte35;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public final class SpliceInsertCommand extends SpliceCommand {
    public static final Parcelable.Creator<SpliceInsertCommand> CREATOR = new Parcelable.Creator<SpliceInsertCommand>() { // from class: com.google.android.exoplayer2.metadata.scte35.SpliceInsertCommand.1
        @Override // android.os.Parcelable.Creator
        public SpliceInsertCommand createFromParcel(Parcel in) {
            return new SpliceInsertCommand(in);
        }

        @Override // android.os.Parcelable.Creator
        public SpliceInsertCommand[] newArray(int size) {
            return new SpliceInsertCommand[size];
        }
    };
    public final boolean autoReturn;
    public final int availNum;
    public final int availsExpected;
    public final long breakDurationUs;
    public final List<ComponentSplice> componentSpliceList;
    public final boolean outOfNetworkIndicator;
    public final boolean programSpliceFlag;
    public final long programSplicePlaybackPositionUs;
    public final long programSplicePts;
    public final boolean spliceEventCancelIndicator;
    public final long spliceEventId;
    public final boolean spliceImmediateFlag;
    public final int uniqueProgramId;

    private SpliceInsertCommand(long spliceEventId, boolean spliceEventCancelIndicator, boolean outOfNetworkIndicator, boolean programSpliceFlag, boolean spliceImmediateFlag, long programSplicePts, long programSplicePlaybackPositionUs, List<ComponentSplice> componentSpliceList, boolean autoReturn, long breakDurationUs, int uniqueProgramId, int availNum, int availsExpected) {
        this.spliceEventId = spliceEventId;
        this.spliceEventCancelIndicator = spliceEventCancelIndicator;
        this.outOfNetworkIndicator = outOfNetworkIndicator;
        this.programSpliceFlag = programSpliceFlag;
        this.spliceImmediateFlag = spliceImmediateFlag;
        this.programSplicePts = programSplicePts;
        this.programSplicePlaybackPositionUs = programSplicePlaybackPositionUs;
        this.componentSpliceList = Collections.unmodifiableList(componentSpliceList);
        this.autoReturn = autoReturn;
        this.breakDurationUs = breakDurationUs;
        this.uniqueProgramId = uniqueProgramId;
        this.availNum = availNum;
        this.availsExpected = availsExpected;
    }

    private SpliceInsertCommand(Parcel in) {
        this.spliceEventId = in.readLong();
        boolean z = false;
        this.spliceEventCancelIndicator = in.readByte() == 1;
        this.outOfNetworkIndicator = in.readByte() == 1;
        this.programSpliceFlag = in.readByte() == 1;
        this.spliceImmediateFlag = in.readByte() == 1;
        this.programSplicePts = in.readLong();
        this.programSplicePlaybackPositionUs = in.readLong();
        int componentSpliceListSize = in.readInt();
        List<ComponentSplice> componentSpliceList = new ArrayList<>(componentSpliceListSize);
        for (int i = 0; i < componentSpliceListSize; i++) {
            componentSpliceList.add(ComponentSplice.createFromParcel(in));
        }
        this.componentSpliceList = Collections.unmodifiableList(componentSpliceList);
        this.autoReturn = in.readByte() == 1 ? true : z;
        this.breakDurationUs = in.readLong();
        this.uniqueProgramId = in.readInt();
        this.availNum = in.readInt();
        this.availsExpected = in.readInt();
    }

    public static SpliceInsertCommand parseFromSection(ParsableByteArray sectionData, long ptsAdjustment, TimestampAdjuster timestampAdjuster) {
        boolean spliceImmediateFlag;
        boolean programSpliceFlag;
        long breakDurationUs;
        boolean autoReturn;
        int availsExpected;
        int availNum;
        int uniqueProgramId;
        List<ComponentSplice> componentSplices;
        boolean outOfNetworkIndicator;
        long programSplicePts;
        boolean outOfNetworkIndicator2;
        long spliceEventId = sectionData.readUnsignedInt();
        boolean spliceEventCancelIndicator = (sectionData.readUnsignedByte() & 128) != 0;
        long programSplicePts2 = C.TIME_UNSET;
        List<ComponentSplice> componentSplices2 = Collections.emptyList();
        boolean autoReturn2 = false;
        long breakDurationUs2 = C.TIME_UNSET;
        if (spliceEventCancelIndicator) {
            outOfNetworkIndicator = false;
            programSpliceFlag = false;
            spliceImmediateFlag = false;
            componentSplices = componentSplices2;
            uniqueProgramId = 0;
            availNum = 0;
            availsExpected = 0;
            autoReturn = false;
            breakDurationUs = -9223372036854775807L;
            programSplicePts = -9223372036854775807L;
        } else {
            int headerByte = sectionData.readUnsignedByte();
            boolean outOfNetworkIndicator3 = (headerByte & 128) != 0;
            boolean programSpliceFlag2 = (headerByte & 64) != 0;
            boolean durationFlag = (headerByte & 32) != 0;
            boolean spliceImmediateFlag2 = (headerByte & 16) != 0;
            if (programSpliceFlag2 && !spliceImmediateFlag2) {
                programSplicePts2 = TimeSignalCommand.parseSpliceTime(sectionData, ptsAdjustment);
            }
            if (programSpliceFlag2) {
                outOfNetworkIndicator2 = outOfNetworkIndicator3;
                programSpliceFlag = programSpliceFlag2;
                spliceImmediateFlag = spliceImmediateFlag2;
            } else {
                int componentCount = sectionData.readUnsignedByte();
                outOfNetworkIndicator2 = outOfNetworkIndicator3;
                List<ComponentSplice> componentSplices3 = new ArrayList<>(componentCount);
                int i = 0;
                while (i < componentCount) {
                    int componentTag = sectionData.readUnsignedByte();
                    long componentSplicePts = C.TIME_UNSET;
                    if (!spliceImmediateFlag2) {
                        componentSplicePts = TimeSignalCommand.parseSpliceTime(sectionData, ptsAdjustment);
                    }
                    int componentCount2 = componentCount;
                    long componentSplicePts2 = componentSplicePts;
                    componentSplices3.add(new ComponentSplice(componentTag, componentSplicePts2, timestampAdjuster.adjustTsTimestamp(componentSplicePts2)));
                    i++;
                    programSpliceFlag2 = programSpliceFlag2;
                    componentCount = componentCount2;
                    spliceImmediateFlag2 = spliceImmediateFlag2;
                }
                programSpliceFlag = programSpliceFlag2;
                spliceImmediateFlag = spliceImmediateFlag2;
                componentSplices2 = componentSplices3;
            }
            if (durationFlag) {
                long firstByte = sectionData.readUnsignedByte();
                boolean autoReturn3 = (firstByte & 128) != 0;
                long breakDuration90khz = ((firstByte & 1) << 32) | sectionData.readUnsignedInt();
                autoReturn2 = autoReturn3;
                breakDurationUs2 = (1000 * breakDuration90khz) / 90;
            }
            int uniqueProgramId2 = sectionData.readUnsignedShort();
            int availNum2 = sectionData.readUnsignedByte();
            int availsExpected2 = sectionData.readUnsignedByte();
            componentSplices = componentSplices2;
            uniqueProgramId = uniqueProgramId2;
            availNum = availNum2;
            availsExpected = availsExpected2;
            autoReturn = autoReturn2;
            breakDurationUs = breakDurationUs2;
            outOfNetworkIndicator = outOfNetworkIndicator2;
            programSplicePts = programSplicePts2;
        }
        return new SpliceInsertCommand(spliceEventId, spliceEventCancelIndicator, outOfNetworkIndicator, programSpliceFlag, spliceImmediateFlag, programSplicePts, timestampAdjuster.adjustTsTimestamp(programSplicePts), componentSplices, autoReturn, breakDurationUs, uniqueProgramId, availNum, availsExpected);
    }

    /* loaded from: classes3.dex */
    public static final class ComponentSplice {
        public final long componentSplicePlaybackPositionUs;
        public final long componentSplicePts;
        public final int componentTag;

        private ComponentSplice(int componentTag, long componentSplicePts, long componentSplicePlaybackPositionUs) {
            this.componentTag = componentTag;
            this.componentSplicePts = componentSplicePts;
            this.componentSplicePlaybackPositionUs = componentSplicePlaybackPositionUs;
        }

        public void writeToParcel(Parcel dest) {
            dest.writeInt(this.componentTag);
            dest.writeLong(this.componentSplicePts);
            dest.writeLong(this.componentSplicePlaybackPositionUs);
        }

        public static ComponentSplice createFromParcel(Parcel in) {
            return new ComponentSplice(in.readInt(), in.readLong(), in.readLong());
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.spliceEventId);
        dest.writeByte(this.spliceEventCancelIndicator ? (byte) 1 : (byte) 0);
        dest.writeByte(this.outOfNetworkIndicator ? (byte) 1 : (byte) 0);
        dest.writeByte(this.programSpliceFlag ? (byte) 1 : (byte) 0);
        dest.writeByte(this.spliceImmediateFlag ? (byte) 1 : (byte) 0);
        dest.writeLong(this.programSplicePts);
        dest.writeLong(this.programSplicePlaybackPositionUs);
        int componentSpliceListSize = this.componentSpliceList.size();
        dest.writeInt(componentSpliceListSize);
        for (int i = 0; i < componentSpliceListSize; i++) {
            this.componentSpliceList.get(i).writeToParcel(dest);
        }
        dest.writeByte(this.autoReturn ? (byte) 1 : (byte) 0);
        dest.writeLong(this.breakDurationUs);
        dest.writeInt(this.uniqueProgramId);
        dest.writeInt(this.availNum);
        dest.writeInt(this.availsExpected);
    }
}
