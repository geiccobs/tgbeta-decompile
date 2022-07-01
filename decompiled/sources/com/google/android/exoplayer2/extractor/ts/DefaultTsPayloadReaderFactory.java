package com.google.android.exoplayer2.extractor.ts;

import android.util.SparseArray;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader;
import com.google.android.exoplayer2.text.cea.Cea708InitializationData;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.tgnet.ConnectionsManager;
/* loaded from: classes.dex */
public final class DefaultTsPayloadReaderFactory implements TsPayloadReader.Factory {
    private final List<Format> closedCaptionFormats;
    private final int flags;

    public DefaultTsPayloadReaderFactory(int i) {
        this(i, Collections.singletonList(Format.createTextSampleFormat(null, "application/cea-608", 0, null)));
    }

    public DefaultTsPayloadReaderFactory(int i, List<Format> list) {
        this.flags = i;
        this.closedCaptionFormats = list;
    }

    @Override // com.google.android.exoplayer2.extractor.ts.TsPayloadReader.Factory
    public SparseArray<TsPayloadReader> createInitialPayloadReaders() {
        return new SparseArray<>();
    }

    @Override // com.google.android.exoplayer2.extractor.ts.TsPayloadReader.Factory
    public TsPayloadReader createPayloadReader(int i, TsPayloadReader.EsInfo esInfo) {
        if (i != 2) {
            if (i == 3 || i == 4) {
                return new PesReader(new MpegAudioReader(esInfo.language));
            }
            if (i == 15) {
                if (!isSet(2)) {
                    return new PesReader(new AdtsReader(false, esInfo.language));
                }
                return null;
            } else if (i == 17) {
                if (!isSet(2)) {
                    return new PesReader(new LatmReader(esInfo.language));
                }
                return null;
            } else if (i == 21) {
                return new PesReader(new Id3Reader());
            } else {
                if (i == 27) {
                    if (!isSet(4)) {
                        return new PesReader(new H264Reader(buildSeiReader(esInfo), isSet(1), isSet(8)));
                    }
                    return null;
                } else if (i == 36) {
                    return new PesReader(new H265Reader(buildSeiReader(esInfo)));
                } else {
                    if (i != 89) {
                        if (i != 138) {
                            if (i != 172) {
                                if (i != 129) {
                                    if (i != 130) {
                                        if (i == 134) {
                                            if (!isSet(16)) {
                                                return new SectionReader(new SpliceInfoSectionReader());
                                            }
                                            return null;
                                        } else if (i != 135) {
                                            return null;
                                        }
                                    } else if (!isSet(64)) {
                                        return null;
                                    }
                                }
                                return new PesReader(new Ac3Reader(esInfo.language));
                            }
                            return new PesReader(new Ac4Reader(esInfo.language));
                        }
                        return new PesReader(new DtsReader(esInfo.language));
                    }
                    return new PesReader(new DvbSubtitleReader(esInfo.dvbSubtitleInfos));
                }
            }
        }
        return new PesReader(new H262Reader(buildUserDataReader(esInfo)));
    }

    private SeiReader buildSeiReader(TsPayloadReader.EsInfo esInfo) {
        return new SeiReader(getClosedCaptionFormats(esInfo));
    }

    private UserDataReader buildUserDataReader(TsPayloadReader.EsInfo esInfo) {
        return new UserDataReader(getClosedCaptionFormats(esInfo));
    }

    private List<Format> getClosedCaptionFormats(TsPayloadReader.EsInfo esInfo) {
        int i;
        String str;
        List<byte[]> list;
        if (isSet(32)) {
            return this.closedCaptionFormats;
        }
        ParsableByteArray parsableByteArray = new ParsableByteArray(esInfo.descriptorBytes);
        List<Format> list2 = this.closedCaptionFormats;
        while (parsableByteArray.bytesLeft() > 0) {
            int readUnsignedByte = parsableByteArray.readUnsignedByte();
            int position = parsableByteArray.getPosition() + parsableByteArray.readUnsignedByte();
            if (readUnsignedByte == 134) {
                list2 = new ArrayList<>();
                int readUnsignedByte2 = parsableByteArray.readUnsignedByte() & 31;
                for (int i2 = 0; i2 < readUnsignedByte2; i2++) {
                    String readString = parsableByteArray.readString(3);
                    int readUnsignedByte3 = parsableByteArray.readUnsignedByte();
                    boolean z = true;
                    boolean z2 = (readUnsignedByte3 & ConnectionsManager.RequestFlagNeedQuickAck) != 0;
                    if (z2) {
                        i = readUnsignedByte3 & 63;
                        str = "application/cea-708";
                    } else {
                        str = "application/cea-608";
                        i = 1;
                    }
                    byte readUnsignedByte4 = (byte) parsableByteArray.readUnsignedByte();
                    parsableByteArray.skipBytes(1);
                    if (z2) {
                        if ((readUnsignedByte4 & 64) == 0) {
                            z = false;
                        }
                        list = Cea708InitializationData.buildData(z);
                    } else {
                        list = null;
                    }
                    list2.add(Format.createTextSampleFormat(null, str, null, -1, 0, readString, i, null, Long.MAX_VALUE, list));
                }
            }
            parsableByteArray.setPosition(position);
        }
        return list2;
    }

    private boolean isSet(int i) {
        return (i & this.flags) != 0;
    }
}
