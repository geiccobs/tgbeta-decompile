package com.google.android.exoplayer2.text.webvtt;

import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import com.google.android.exoplayer2.text.webvtt.WebvttCue;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.Collections;
/* loaded from: classes.dex */
public final class Mp4WebvttDecoder extends SimpleSubtitleDecoder {
    private final ParsableByteArray sampleData = new ParsableByteArray();
    private final WebvttCue.Builder builder = new WebvttCue.Builder();

    public Mp4WebvttDecoder() {
        super("Mp4WebvttDecoder");
    }

    @Override // com.google.android.exoplayer2.text.SimpleSubtitleDecoder
    protected Subtitle decode(byte[] bArr, int i, boolean z) throws SubtitleDecoderException {
        this.sampleData.reset(bArr, i);
        ArrayList arrayList = new ArrayList();
        while (this.sampleData.bytesLeft() > 0) {
            if (this.sampleData.bytesLeft() < 8) {
                throw new SubtitleDecoderException("Incomplete Mp4Webvtt Top Level box header found.");
            }
            int readInt = this.sampleData.readInt();
            if (this.sampleData.readInt() == 1987343459) {
                arrayList.add(parseVttCueBox(this.sampleData, this.builder, readInt - 8));
            } else {
                this.sampleData.skipBytes(readInt - 8);
            }
        }
        return new Mp4WebvttSubtitle(arrayList);
    }

    private static Cue parseVttCueBox(ParsableByteArray parsableByteArray, WebvttCue.Builder builder, int i) throws SubtitleDecoderException {
        builder.reset();
        while (i > 0) {
            if (i < 8) {
                throw new SubtitleDecoderException("Incomplete vtt cue box header found.");
            }
            int readInt = parsableByteArray.readInt();
            int readInt2 = parsableByteArray.readInt();
            int i2 = readInt - 8;
            String fromUtf8Bytes = Util.fromUtf8Bytes(parsableByteArray.data, parsableByteArray.getPosition(), i2);
            parsableByteArray.skipBytes(i2);
            i = (i - 8) - i2;
            if (readInt2 == 1937011815) {
                WebvttCueParser.parseCueSettingsList(fromUtf8Bytes, builder);
            } else if (readInt2 == 1885436268) {
                WebvttCueParser.parseCueText(null, fromUtf8Bytes.trim(), builder, Collections.emptyList());
            }
        }
        return builder.build();
    }
}
