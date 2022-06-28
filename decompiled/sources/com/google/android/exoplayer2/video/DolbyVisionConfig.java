package com.google.android.exoplayer2.video;

import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.google.android.exoplayer2.util.ParsableByteArray;
/* loaded from: classes3.dex */
public final class DolbyVisionConfig {
    public final String codecs;
    public final int level;
    public final int profile;

    public static DolbyVisionConfig parse(ParsableByteArray data) {
        String codecsPrefix;
        data.skipBytes(2);
        int profileData = data.readUnsignedByte();
        int dvProfile = profileData >> 1;
        int dvLevel = ((profileData & 1) << 5) | ((data.readUnsignedByte() >> 3) & 31);
        if (dvProfile == 4 || dvProfile == 5 || dvProfile == 7) {
            codecsPrefix = "dvhe";
        } else if (dvProfile == 8) {
            codecsPrefix = VisualSampleEntry.TYPE7;
        } else if (dvProfile == 9) {
            codecsPrefix = VisualSampleEntry.TYPE4;
        } else {
            return null;
        }
        String codecs = codecsPrefix + ".0" + dvProfile + ".0" + dvLevel;
        return new DolbyVisionConfig(dvProfile, dvLevel, codecs);
    }

    private DolbyVisionConfig(int profile, int level, String codecs) {
        this.profile = profile;
        this.level = level;
        this.codecs = codecs;
    }
}
