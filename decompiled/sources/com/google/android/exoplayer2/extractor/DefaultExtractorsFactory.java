package com.google.android.exoplayer2.extractor;

import com.google.android.exoplayer2.extractor.amr.AmrExtractor;
import com.google.android.exoplayer2.extractor.flac.FlacExtractor;
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
/* loaded from: classes3.dex */
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
        Constructor<? extends Extractor> flacExtensionExtractorConstructor = null;
        if (1 != 0) {
            try {
                flacExtensionExtractorConstructor = Class.forName("com.google.android.exoplayer2.ext.flac.FlacExtractor").asSubclass(Extractor.class).getConstructor(new Class[0]);
            } catch (ClassNotFoundException e) {
            } catch (Exception e2) {
                throw new RuntimeException("Error instantiating FLAC extension", e2);
            }
        }
        FLAC_EXTENSION_EXTRACTOR_CONSTRUCTOR = flacExtensionExtractorConstructor;
    }

    public synchronized DefaultExtractorsFactory setConstantBitrateSeekingEnabled(boolean constantBitrateSeekingEnabled) {
        this.constantBitrateSeekingEnabled = constantBitrateSeekingEnabled;
        return this;
    }

    public synchronized DefaultExtractorsFactory setAdtsExtractorFlags(int flags) {
        this.adtsFlags = flags;
        return this;
    }

    public synchronized DefaultExtractorsFactory setAmrExtractorFlags(int flags) {
        this.amrFlags = flags;
        return this;
    }

    public synchronized DefaultExtractorsFactory setMatroskaExtractorFlags(int flags) {
        this.matroskaFlags = flags;
        return this;
    }

    public synchronized DefaultExtractorsFactory setMp4ExtractorFlags(int flags) {
        this.mp4Flags = flags;
        return this;
    }

    public synchronized DefaultExtractorsFactory setFragmentedMp4ExtractorFlags(int flags) {
        this.fragmentedMp4Flags = flags;
        return this;
    }

    public synchronized DefaultExtractorsFactory setMp3ExtractorFlags(int flags) {
        this.mp3Flags = flags;
        return this;
    }

    public synchronized DefaultExtractorsFactory setTsExtractorMode(int mode) {
        this.tsMode = mode;
        return this;
    }

    public synchronized DefaultExtractorsFactory setTsExtractorFlags(int flags) {
        this.tsFlags = flags;
        return this;
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorsFactory
    public synchronized Extractor[] createExtractors() {
        Extractor[] extractors;
        int i;
        int i2;
        extractors = new Extractor[14];
        extractors[0] = new MatroskaExtractor(this.matroskaFlags);
        int i3 = 1;
        extractors[1] = new FragmentedMp4Extractor(this.fragmentedMp4Flags);
        extractors[2] = new Mp4Extractor(this.mp4Flags);
        extractors[3] = new OggExtractor();
        int i4 = this.mp3Flags;
        if (this.constantBitrateSeekingEnabled) {
            i = 1;
        } else {
            i = 0;
        }
        extractors[4] = new Mp3Extractor(i4 | i);
        int i5 = this.adtsFlags;
        if (this.constantBitrateSeekingEnabled) {
            i2 = 1;
        } else {
            i2 = 0;
        }
        extractors[5] = new AdtsExtractor(i5 | i2);
        extractors[6] = new Ac3Extractor();
        extractors[7] = new TsExtractor(this.tsMode, this.tsFlags);
        extractors[8] = new FlvExtractor();
        extractors[9] = new PsExtractor();
        extractors[10] = new WavExtractor();
        int i6 = this.amrFlags;
        if (!this.constantBitrateSeekingEnabled) {
            i3 = 0;
        }
        extractors[11] = new AmrExtractor(i3 | i6);
        extractors[12] = new Ac4Extractor();
        Constructor<? extends Extractor> constructor = FLAC_EXTENSION_EXTRACTOR_CONSTRUCTOR;
        if (constructor != null) {
            try {
                extractors[13] = constructor.newInstance(new Object[0]);
            } catch (Exception e) {
                throw new IllegalStateException("Unexpected error creating FLAC extractor", e);
            }
        } else {
            extractors[13] = new FlacExtractor();
        }
        return extractors;
    }
}
