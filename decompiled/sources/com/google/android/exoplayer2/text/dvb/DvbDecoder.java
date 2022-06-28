package com.google.android.exoplayer2.text.dvb;

import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.List;
/* loaded from: classes3.dex */
public final class DvbDecoder extends SimpleSubtitleDecoder {
    private final DvbParser parser;

    public DvbDecoder(List<byte[]> initializationData) {
        super("DvbDecoder");
        ParsableByteArray data = new ParsableByteArray(initializationData.get(0));
        int subtitleCompositionPage = data.readUnsignedShort();
        int subtitleAncillaryPage = data.readUnsignedShort();
        this.parser = new DvbParser(subtitleCompositionPage, subtitleAncillaryPage);
    }

    @Override // com.google.android.exoplayer2.text.SimpleSubtitleDecoder
    protected Subtitle decode(byte[] data, int length, boolean reset) {
        if (reset) {
            this.parser.reset();
        }
        return new DvbSubtitle(this.parser.decode(data, length));
    }
}
