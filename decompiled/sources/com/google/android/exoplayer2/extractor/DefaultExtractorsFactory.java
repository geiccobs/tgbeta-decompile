package com.google.android.exoplayer2.extractor;

import com.google.android.exoplayer2.ext.flac.FlacExtractor;
import com.google.android.exoplayer2.extractor.amr.AmrExtractor;
import com.google.android.exoplayer2.extractor.flv.FlvExtractor;
import com.google.android.exoplayer2.extractor.mkv.MatroskaExtractor;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import com.google.android.exoplayer2.extractor.mp4.Mp4Extractor;
import com.google.android.exoplayer2.extractor.ogg.OggExtractor;
import com.google.android.exoplayer2.extractor.ts.Ac3Extractor;
import com.google.android.exoplayer2.extractor.ts.Ac4Extractor;
import com.google.android.exoplayer2.extractor.ts.AdtsExtractor;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.extractor.wav.WavExtractor;
import java.lang.reflect.Constructor;
/* loaded from: classes.dex */
public final class DefaultExtractorsFactory implements ExtractorsFactory {
    private static final Constructor<? extends Extractor> FLAC_EXTENSION_EXTRACTOR_CONSTRUCTOR;
    private int adtsFlags;
    private int amrFlags;
    private int fragmentedMp4Flags;
    private int matroskaFlags;
    private int mp3Flags;
    private int mp4Flags;
    private int tsFlags;
    private boolean constantBitrateSeekingEnabled = true;
    private int tsMode = 1;

    static {
        Constructor<? extends Extractor> constructor;
        try {
            ExtractorsFactory extractorsFactory = FlacExtractor.FACTORY;
            constructor = FlacExtractor.class.asSubclass(Extractor.class).getConstructor(new Class[0]);
        } catch (ClassNotFoundException unused) {
            constructor = null;
        } catch (Exception e) {
            throw new RuntimeException("Error instantiating FLAC extension", e);
        }
        FLAC_EXTENSION_EXTRACTOR_CONSTRUCTOR = constructor;
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorsFactory
    public synchronized Extractor[] createExtractors() {
        Extractor[] extractorArr;
        extractorArr = new Extractor[14];
        extractorArr[0] = new MatroskaExtractor(this.matroskaFlags);
        int i = 1;
        extractorArr[1] = new FragmentedMp4Extractor(this.fragmentedMp4Flags);
        extractorArr[2] = new Mp4Extractor(this.mp4Flags);
        extractorArr[3] = new OggExtractor();
        extractorArr[4] = new Mp3Extractor(this.mp3Flags | (this.constantBitrateSeekingEnabled ? 1 : 0));
        extractorArr[5] = new AdtsExtractor(this.adtsFlags | (this.constantBitrateSeekingEnabled ? 1 : 0));
        extractorArr[6] = new Ac3Extractor();
        extractorArr[7] = new TsExtractor(this.tsMode, this.tsFlags);
        extractorArr[8] = new FlvExtractor();
        extractorArr[9] = new PsExtractor();
        extractorArr[10] = new WavExtractor();
        int i2 = this.amrFlags;
        if (!this.constantBitrateSeekingEnabled) {
            i = 0;
        }
        extractorArr[11] = new AmrExtractor(i | i2);
        extractorArr[12] = new Ac4Extractor();
        Constructor<? extends Extractor> constructor = FLAC_EXTENSION_EXTRACTOR_CONSTRUCTOR;
        if (constructor != null) {
            try {
                extractorArr[13] = constructor.newInstance(new Object[0]);
            } catch (Exception e) {
                throw new IllegalStateException("Unexpected error creating FLAC extractor", e);
            }
        } else {
            extractorArr[13] = new com.google.android.exoplayer2.extractor.flac.FlacExtractor();
        }
        return extractorArr;
    }
}
