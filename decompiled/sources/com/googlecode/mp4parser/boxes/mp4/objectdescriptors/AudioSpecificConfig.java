package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.Hex;
import com.coremedia.iso.IsoTypeWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
@Descriptor(objectTypeIndication = 64, tags = {5})
/* loaded from: classes3.dex */
public class AudioSpecificConfig extends BaseDescriptor {
    public boolean aacScalefactorDataResilienceFlag;
    public boolean aacSectionDataResilienceFlag;
    public boolean aacSpectralDataResilienceFlag;
    public int audioObjectType;
    public int channelConfiguration;
    byte[] configBytes;
    public int coreCoderDelay;
    public int dependsOnCoreCoder;
    public int directMapping;
    public ELDSpecificConfig eldSpecificConfig;
    public int epConfig;
    public int erHvxcExtensionFlag;
    public int extensionAudioObjectType;
    public int extensionChannelConfiguration;
    public int extensionFlag;
    public int extensionFlag3;
    public int extensionSamplingFrequency;
    public int extensionSamplingFrequencyIndex;
    public int fillBits;
    public int frameLengthFlag;
    public boolean gaSpecificConfig;
    public int hilnContMode;
    public int hilnEnhaLayer;
    public int hilnEnhaQuantMode;
    public int hilnFrameLength;
    public int hilnMaxNumLine;
    public int hilnQuantMode;
    public int hilnSampleRateCode;
    public int hvxcRateMode;
    public int hvxcVarMode;
    public int isBaseLayer;
    public int layerNr;
    public int layer_length;
    public int numOfSubFrame;
    public int paraExtensionFlag;
    public int paraMode;
    public boolean parametricSpecificConfig;
    public boolean psPresentFlag;
    public int sacPayloadEmbedding;
    public int samplingFrequency;
    public int samplingFrequencyIndex;
    public boolean sbrPresentFlag;
    public int syncExtensionType;
    public int var_ScalableFlag;
    public static Map<Integer, Integer> samplingFrequencyIndexMap = new HashMap();
    public static Map<Integer, String> audioObjectTypeMap = new HashMap();

    static {
        samplingFrequencyIndexMap.put(0, 96000);
        samplingFrequencyIndexMap.put(1, 88200);
        samplingFrequencyIndexMap.put(2, 64000);
        samplingFrequencyIndexMap.put(3, 48000);
        samplingFrequencyIndexMap.put(4, 44100);
        samplingFrequencyIndexMap.put(5, 32000);
        samplingFrequencyIndexMap.put(6, 24000);
        samplingFrequencyIndexMap.put(7, 22050);
        samplingFrequencyIndexMap.put(8, 16000);
        samplingFrequencyIndexMap.put(9, 12000);
        samplingFrequencyIndexMap.put(10, 11025);
        samplingFrequencyIndexMap.put(11, 8000);
        audioObjectTypeMap.put(1, "AAC main");
        audioObjectTypeMap.put(2, "AAC LC");
        audioObjectTypeMap.put(3, "AAC SSR");
        audioObjectTypeMap.put(4, "AAC LTP");
        audioObjectTypeMap.put(5, "SBR");
        audioObjectTypeMap.put(6, "AAC Scalable");
        audioObjectTypeMap.put(7, "TwinVQ");
        audioObjectTypeMap.put(8, "CELP");
        audioObjectTypeMap.put(9, "HVXC");
        audioObjectTypeMap.put(10, "(reserved)");
        audioObjectTypeMap.put(11, "(reserved)");
        audioObjectTypeMap.put(12, "TTSI");
        audioObjectTypeMap.put(13, "Main synthetic");
        audioObjectTypeMap.put(14, "Wavetable synthesis");
        audioObjectTypeMap.put(15, "General MIDI");
        audioObjectTypeMap.put(16, "Algorithmic Synthesis and Audio FX");
        audioObjectTypeMap.put(17, "ER AAC LC");
        audioObjectTypeMap.put(18, "(reserved)");
        audioObjectTypeMap.put(19, "ER AAC LTP");
        audioObjectTypeMap.put(20, "ER AAC Scalable");
        audioObjectTypeMap.put(21, "ER TwinVQ");
        audioObjectTypeMap.put(22, "ER BSAC");
        audioObjectTypeMap.put(23, "ER AAC LD");
        audioObjectTypeMap.put(24, "ER CELP");
        audioObjectTypeMap.put(25, "ER HVXC");
        audioObjectTypeMap.put(26, "ER HILN");
        audioObjectTypeMap.put(27, "ER Parametric");
        audioObjectTypeMap.put(28, "SSC");
        audioObjectTypeMap.put(29, "PS");
        audioObjectTypeMap.put(30, "MPEG Surround");
        audioObjectTypeMap.put(31, "(escape)");
        audioObjectTypeMap.put(32, "Layer-1");
        audioObjectTypeMap.put(33, "Layer-2");
        audioObjectTypeMap.put(34, "Layer-3");
        audioObjectTypeMap.put(35, "DST");
        audioObjectTypeMap.put(36, "ALS");
        audioObjectTypeMap.put(37, "SLS");
        audioObjectTypeMap.put(38, "SLS non-core");
        audioObjectTypeMap.put(39, "ER AAC ELD");
        audioObjectTypeMap.put(40, "SMR Simple");
        audioObjectTypeMap.put(41, "SMR Main");
    }

    @Override // com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BaseDescriptor
    public void parseDetail(ByteBuffer bb) throws IOException {
        ByteBuffer configBytes = bb.slice();
        configBytes.limit(this.sizeOfInstance);
        bb.position(bb.position() + this.sizeOfInstance);
        byte[] bArr = new byte[this.sizeOfInstance];
        this.configBytes = bArr;
        configBytes.get(bArr);
        configBytes.rewind();
        BitReaderBuffer bitReaderBuffer = new BitReaderBuffer(configBytes);
        this.audioObjectType = getAudioObjectType(bitReaderBuffer);
        int readBits = bitReaderBuffer.readBits(4);
        this.samplingFrequencyIndex = readBits;
        if (readBits == 15) {
            this.samplingFrequency = bitReaderBuffer.readBits(24);
        }
        this.channelConfiguration = bitReaderBuffer.readBits(4);
        int i = this.audioObjectType;
        if (i == 5 || i == 29) {
            this.extensionAudioObjectType = 5;
            this.sbrPresentFlag = true;
            if (i == 29) {
                this.psPresentFlag = true;
            }
            int readBits2 = bitReaderBuffer.readBits(4);
            this.extensionSamplingFrequencyIndex = readBits2;
            if (readBits2 == 15) {
                this.extensionSamplingFrequency = bitReaderBuffer.readBits(24);
            }
            int audioObjectType = getAudioObjectType(bitReaderBuffer);
            this.audioObjectType = audioObjectType;
            if (audioObjectType == 22) {
                this.extensionChannelConfiguration = bitReaderBuffer.readBits(4);
            }
        } else {
            this.extensionAudioObjectType = 0;
        }
        int i2 = this.audioObjectType;
        switch (i2) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 6:
            case 7:
            case 17:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
                parseGaSpecificConfig(this.samplingFrequencyIndex, this.channelConfiguration, i2, bitReaderBuffer);
                break;
            case 8:
                throw new UnsupportedOperationException("can't parse CelpSpecificConfig yet");
            case 9:
                throw new UnsupportedOperationException("can't parse HvxcSpecificConfig yet");
            case 12:
                throw new UnsupportedOperationException("can't parse TTSSpecificConfig yet");
            case 13:
            case 14:
            case 15:
            case 16:
                throw new UnsupportedOperationException("can't parse StructuredAudioSpecificConfig yet");
            case 24:
                throw new UnsupportedOperationException("can't parse ErrorResilientCelpSpecificConfig yet");
            case 25:
                throw new UnsupportedOperationException("can't parse ErrorResilientHvxcSpecificConfig yet");
            case 26:
            case 27:
                parseParametricSpecificConfig(this.samplingFrequencyIndex, this.channelConfiguration, i2, bitReaderBuffer);
                break;
            case 28:
                throw new UnsupportedOperationException("can't parse SSCSpecificConfig yet");
            case 30:
                this.sacPayloadEmbedding = bitReaderBuffer.readBits(1);
                throw new UnsupportedOperationException("can't parse SpatialSpecificConfig yet");
            case 32:
            case 33:
            case 34:
                throw new UnsupportedOperationException("can't parse MPEG_1_2_SpecificConfig yet");
            case 35:
                throw new UnsupportedOperationException("can't parse DSTSpecificConfig yet");
            case 36:
                this.fillBits = bitReaderBuffer.readBits(5);
                throw new UnsupportedOperationException("can't parse ALSSpecificConfig yet");
            case 37:
            case 38:
                throw new UnsupportedOperationException("can't parse SLSSpecificConfig yet");
            case 39:
                this.eldSpecificConfig = new ELDSpecificConfig(this.channelConfiguration, bitReaderBuffer);
                break;
            case 40:
            case 41:
                throw new UnsupportedOperationException("can't parse SymbolicMusicSpecificConfig yet");
        }
        switch (this.audioObjectType) {
            case 17:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 39:
                int readBits3 = bitReaderBuffer.readBits(2);
                this.epConfig = readBits3;
                if (readBits3 == 2 || readBits3 == 3) {
                    throw new UnsupportedOperationException("can't parse ErrorProtectionSpecificConfig yet");
                }
                if (readBits3 == 3) {
                    int readBits4 = bitReaderBuffer.readBits(1);
                    this.directMapping = readBits4;
                    if (readBits4 == 0) {
                        throw new RuntimeException("not implemented");
                    }
                }
                break;
        }
        if (this.extensionAudioObjectType != 5 && bitReaderBuffer.remainingBits() >= 16) {
            int readBits5 = bitReaderBuffer.readBits(11);
            this.syncExtensionType = readBits5;
            if (readBits5 == 695) {
                int audioObjectType2 = getAudioObjectType(bitReaderBuffer);
                this.extensionAudioObjectType = audioObjectType2;
                if (audioObjectType2 == 5) {
                    boolean readBool = bitReaderBuffer.readBool();
                    this.sbrPresentFlag = readBool;
                    if (readBool) {
                        int readBits6 = bitReaderBuffer.readBits(4);
                        this.extensionSamplingFrequencyIndex = readBits6;
                        if (readBits6 == 15) {
                            this.extensionSamplingFrequency = bitReaderBuffer.readBits(24);
                        }
                        if (bitReaderBuffer.remainingBits() >= 12) {
                            int readBits7 = bitReaderBuffer.readBits(11);
                            this.syncExtensionType = readBits7;
                            if (readBits7 == 1352) {
                                this.psPresentFlag = bitReaderBuffer.readBool();
                            }
                        }
                    }
                }
                if (this.extensionAudioObjectType == 22) {
                    boolean readBool2 = bitReaderBuffer.readBool();
                    this.sbrPresentFlag = readBool2;
                    if (readBool2) {
                        int readBits8 = bitReaderBuffer.readBits(4);
                        this.extensionSamplingFrequencyIndex = readBits8;
                        if (readBits8 == 15) {
                            this.extensionSamplingFrequency = bitReaderBuffer.readBits(24);
                        }
                    }
                    this.extensionChannelConfiguration = bitReaderBuffer.readBits(4);
                }
            }
        }
    }

    /* loaded from: classes3.dex */
    public class ELDSpecificConfig {
        private static final int ELDEXT_TERM = 0;
        public boolean aacScalefactorDataResilienceFlag;
        public boolean aacSectionDataResilienceFlag;
        public boolean aacSpectralDataResilienceFlag;
        public boolean frameLengthFlag;
        public boolean ldSbrCrcFlag;
        public boolean ldSbrPresentFlag;
        public boolean ldSbrSamplingRate;

        public ELDSpecificConfig(int channelConfiguration, BitReaderBuffer bitReaderBuffer) {
            AudioSpecificConfig.this = r6;
            this.frameLengthFlag = bitReaderBuffer.readBool();
            this.aacSectionDataResilienceFlag = bitReaderBuffer.readBool();
            this.aacScalefactorDataResilienceFlag = bitReaderBuffer.readBool();
            this.aacSpectralDataResilienceFlag = bitReaderBuffer.readBool();
            boolean readBool = bitReaderBuffer.readBool();
            this.ldSbrPresentFlag = readBool;
            if (readBool) {
                this.ldSbrSamplingRate = bitReaderBuffer.readBool();
                this.ldSbrCrcFlag = bitReaderBuffer.readBool();
                ld_sbr_header(channelConfiguration, bitReaderBuffer);
            }
            while (bitReaderBuffer.readBits(4) != 0) {
                int eldExtLen = bitReaderBuffer.readBits(4);
                int len = eldExtLen;
                int eldExtLenAdd = 0;
                if (eldExtLen == 15) {
                    eldExtLenAdd = bitReaderBuffer.readBits(8);
                    len += eldExtLenAdd;
                }
                if (eldExtLenAdd == 255) {
                    int eldExtLenAddAdd = bitReaderBuffer.readBits(16);
                    len += eldExtLenAddAdd;
                }
                for (int cnt = 0; cnt < len; cnt++) {
                    bitReaderBuffer.readBits(8);
                }
            }
        }

        public void ld_sbr_header(int channelConfiguration, BitReaderBuffer bitReaderBuffer) {
            int numSbrHeader;
            switch (channelConfiguration) {
                case 1:
                case 2:
                    numSbrHeader = 1;
                    break;
                case 3:
                    numSbrHeader = 2;
                    break;
                case 4:
                case 5:
                case 6:
                    numSbrHeader = 3;
                    break;
                case 7:
                    numSbrHeader = 4;
                    break;
                default:
                    numSbrHeader = 0;
                    break;
            }
            for (int el = 0; el < numSbrHeader; el++) {
                new sbr_header(bitReaderBuffer);
            }
        }
    }

    /* loaded from: classes3.dex */
    public class sbr_header {
        public boolean bs_alter_scale;
        public boolean bs_amp_res;
        public int bs_freq_scale;
        public boolean bs_header_extra_1;
        public boolean bs_header_extra_2;
        public boolean bs_interpol_freq;
        public int bs_limiter_bands;
        public int bs_limiter_gains;
        public int bs_noise_bands;
        public int bs_reserved;
        public boolean bs_smoothing_mode;
        public int bs_start_freq;
        public int bs_stop_freq;
        public int bs_xover_band;

        public sbr_header(BitReaderBuffer b) {
            AudioSpecificConfig.this = r2;
            this.bs_amp_res = b.readBool();
            this.bs_start_freq = b.readBits(4);
            this.bs_stop_freq = b.readBits(4);
            this.bs_xover_band = b.readBits(3);
            this.bs_reserved = b.readBits(2);
            this.bs_header_extra_1 = b.readBool();
            this.bs_header_extra_2 = b.readBool();
            if (this.bs_header_extra_1) {
                this.bs_freq_scale = b.readBits(2);
                this.bs_alter_scale = b.readBool();
                this.bs_noise_bands = b.readBits(2);
            }
            if (this.bs_header_extra_2) {
                this.bs_limiter_bands = b.readBits(2);
                this.bs_limiter_gains = b.readBits(2);
                this.bs_interpol_freq = b.readBool();
            }
            this.bs_smoothing_mode = b.readBool();
        }
    }

    private int gaSpecificConfigSize() {
        return 0;
    }

    public int serializedSize() {
        if (this.audioObjectType == 2) {
            int out = 4 + gaSpecificConfigSize();
            return out;
        }
        throw new UnsupportedOperationException("can't serialize that yet");
    }

    public ByteBuffer serialize() {
        ByteBuffer out = ByteBuffer.allocate(serializedSize());
        IsoTypeWriter.writeUInt8(out, 5);
        IsoTypeWriter.writeUInt8(out, serializedSize() - 2);
        BitWriterBuffer bwb = new BitWriterBuffer(out);
        bwb.writeBits(this.audioObjectType, 5);
        bwb.writeBits(this.samplingFrequencyIndex, 4);
        if (this.samplingFrequencyIndex == 15) {
            throw new UnsupportedOperationException("can't serialize that yet");
        }
        bwb.writeBits(this.channelConfiguration, 4);
        return out;
    }

    private int getAudioObjectType(BitReaderBuffer in) throws IOException {
        int audioObjectType = in.readBits(5);
        if (audioObjectType == 31) {
            return in.readBits(6) + 32;
        }
        return audioObjectType;
    }

    private void parseGaSpecificConfig(int samplingFrequencyIndex, int channelConfiguration, int audioObjectType, BitReaderBuffer in) throws IOException {
        this.frameLengthFlag = in.readBits(1);
        int readBits = in.readBits(1);
        this.dependsOnCoreCoder = readBits;
        if (readBits == 1) {
            this.coreCoderDelay = in.readBits(14);
        }
        this.extensionFlag = in.readBits(1);
        if (channelConfiguration == 0) {
            throw new UnsupportedOperationException("can't parse program_config_element yet");
        }
        if (audioObjectType == 6 || audioObjectType == 20) {
            this.layerNr = in.readBits(3);
        }
        if (this.extensionFlag == 1) {
            if (audioObjectType == 22) {
                this.numOfSubFrame = in.readBits(5);
                this.layer_length = in.readBits(11);
            }
            if (audioObjectType == 17 || audioObjectType == 19 || audioObjectType == 20 || audioObjectType == 23) {
                this.aacSectionDataResilienceFlag = in.readBool();
                this.aacScalefactorDataResilienceFlag = in.readBool();
                this.aacSpectralDataResilienceFlag = in.readBool();
            }
            this.extensionFlag3 = in.readBits(1);
        }
        this.gaSpecificConfig = true;
    }

    private void parseParametricSpecificConfig(int samplingFrequencyIndex, int channelConfiguration, int audioObjectType, BitReaderBuffer in) throws IOException {
        int readBits = in.readBits(1);
        this.isBaseLayer = readBits;
        if (readBits == 1) {
            parseParaConfig(samplingFrequencyIndex, channelConfiguration, audioObjectType, in);
        } else {
            parseHilnEnexConfig(samplingFrequencyIndex, channelConfiguration, audioObjectType, in);
        }
    }

    private void parseParaConfig(int samplingFrequencyIndex, int channelConfiguration, int audioObjectType, BitReaderBuffer in) throws IOException {
        int readBits = in.readBits(2);
        this.paraMode = readBits;
        if (readBits != 1) {
            parseErHvxcConfig(samplingFrequencyIndex, channelConfiguration, audioObjectType, in);
        }
        if (this.paraMode != 0) {
            parseHilnConfig(samplingFrequencyIndex, channelConfiguration, audioObjectType, in);
        }
        this.paraExtensionFlag = in.readBits(1);
        this.parametricSpecificConfig = true;
    }

    private void parseErHvxcConfig(int samplingFrequencyIndex, int channelConfiguration, int audioObjectType, BitReaderBuffer in) throws IOException {
        this.hvxcVarMode = in.readBits(1);
        this.hvxcRateMode = in.readBits(2);
        int readBits = in.readBits(1);
        this.erHvxcExtensionFlag = readBits;
        if (readBits == 1) {
            this.var_ScalableFlag = in.readBits(1);
        }
    }

    private void parseHilnConfig(int samplingFrequencyIndex, int channelConfiguration, int audioObjectType, BitReaderBuffer in) throws IOException {
        this.hilnQuantMode = in.readBits(1);
        this.hilnMaxNumLine = in.readBits(8);
        this.hilnSampleRateCode = in.readBits(4);
        this.hilnFrameLength = in.readBits(12);
        this.hilnContMode = in.readBits(2);
    }

    private void parseHilnEnexConfig(int samplingFrequencyIndex, int channelConfiguration, int audioObjectType, BitReaderBuffer in) throws IOException {
        int readBits = in.readBits(1);
        this.hilnEnhaLayer = readBits;
        if (readBits == 1) {
            this.hilnEnhaQuantMode = in.readBits(2);
        }
    }

    public byte[] getConfigBytes() {
        return this.configBytes;
    }

    public int getAudioObjectType() {
        return this.audioObjectType;
    }

    public int getExtensionAudioObjectType() {
        return this.extensionAudioObjectType;
    }

    public void setAudioObjectType(int audioObjectType) {
        this.audioObjectType = audioObjectType;
    }

    public void setSamplingFrequencyIndex(int samplingFrequencyIndex) {
        this.samplingFrequencyIndex = samplingFrequencyIndex;
    }

    public void setSamplingFrequency(int samplingFrequency) {
        this.samplingFrequency = samplingFrequency;
    }

    public void setChannelConfiguration(int channelConfiguration) {
        this.channelConfiguration = channelConfiguration;
    }

    @Override // com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BaseDescriptor
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AudioSpecificConfig");
        sb.append("{configBytes=");
        sb.append(Hex.encodeHex(this.configBytes));
        sb.append(", audioObjectType=");
        sb.append(this.audioObjectType);
        sb.append(" (");
        sb.append(audioObjectTypeMap.get(Integer.valueOf(this.audioObjectType)));
        sb.append(")");
        sb.append(", samplingFrequencyIndex=");
        sb.append(this.samplingFrequencyIndex);
        sb.append(" (");
        sb.append(samplingFrequencyIndexMap.get(Integer.valueOf(this.samplingFrequencyIndex)));
        sb.append(")");
        sb.append(", samplingFrequency=");
        sb.append(this.samplingFrequency);
        sb.append(", channelConfiguration=");
        sb.append(this.channelConfiguration);
        if (this.extensionAudioObjectType > 0) {
            sb.append(", extensionAudioObjectType=");
            sb.append(this.extensionAudioObjectType);
            sb.append(" (");
            sb.append(audioObjectTypeMap.get(Integer.valueOf(this.extensionAudioObjectType)));
            sb.append(")");
            sb.append(", sbrPresentFlag=");
            sb.append(this.sbrPresentFlag);
            sb.append(", psPresentFlag=");
            sb.append(this.psPresentFlag);
            sb.append(", extensionSamplingFrequencyIndex=");
            sb.append(this.extensionSamplingFrequencyIndex);
            sb.append(" (");
            sb.append(samplingFrequencyIndexMap.get(Integer.valueOf(this.extensionSamplingFrequencyIndex)));
            sb.append(")");
            sb.append(", extensionSamplingFrequency=");
            sb.append(this.extensionSamplingFrequency);
            sb.append(", extensionChannelConfiguration=");
            sb.append(this.extensionChannelConfiguration);
        }
        sb.append(", syncExtensionType=");
        sb.append(this.syncExtensionType);
        if (this.gaSpecificConfig) {
            sb.append(", frameLengthFlag=");
            sb.append(this.frameLengthFlag);
            sb.append(", dependsOnCoreCoder=");
            sb.append(this.dependsOnCoreCoder);
            sb.append(", coreCoderDelay=");
            sb.append(this.coreCoderDelay);
            sb.append(", extensionFlag=");
            sb.append(this.extensionFlag);
            sb.append(", layerNr=");
            sb.append(this.layerNr);
            sb.append(", numOfSubFrame=");
            sb.append(this.numOfSubFrame);
            sb.append(", layer_length=");
            sb.append(this.layer_length);
            sb.append(", aacSectionDataResilienceFlag=");
            sb.append(this.aacSectionDataResilienceFlag);
            sb.append(", aacScalefactorDataResilienceFlag=");
            sb.append(this.aacScalefactorDataResilienceFlag);
            sb.append(", aacSpectralDataResilienceFlag=");
            sb.append(this.aacSpectralDataResilienceFlag);
            sb.append(", extensionFlag3=");
            sb.append(this.extensionFlag3);
        }
        if (this.parametricSpecificConfig) {
            sb.append(", isBaseLayer=");
            sb.append(this.isBaseLayer);
            sb.append(", paraMode=");
            sb.append(this.paraMode);
            sb.append(", paraExtensionFlag=");
            sb.append(this.paraExtensionFlag);
            sb.append(", hvxcVarMode=");
            sb.append(this.hvxcVarMode);
            sb.append(", hvxcRateMode=");
            sb.append(this.hvxcRateMode);
            sb.append(", erHvxcExtensionFlag=");
            sb.append(this.erHvxcExtensionFlag);
            sb.append(", var_ScalableFlag=");
            sb.append(this.var_ScalableFlag);
            sb.append(", hilnQuantMode=");
            sb.append(this.hilnQuantMode);
            sb.append(", hilnMaxNumLine=");
            sb.append(this.hilnMaxNumLine);
            sb.append(", hilnSampleRateCode=");
            sb.append(this.hilnSampleRateCode);
            sb.append(", hilnFrameLength=");
            sb.append(this.hilnFrameLength);
            sb.append(", hilnContMode=");
            sb.append(this.hilnContMode);
            sb.append(", hilnEnhaLayer=");
            sb.append(this.hilnEnhaLayer);
            sb.append(", hilnEnhaQuantMode=");
            sb.append(this.hilnEnhaQuantMode);
        }
        sb.append('}');
        return sb.toString();
    }

    public int getSamplingFrequency() {
        int i = this.samplingFrequencyIndex;
        return i == 15 ? this.samplingFrequency : samplingFrequencyIndexMap.get(Integer.valueOf(i)).intValue();
    }

    public int getChannelConfiguration() {
        return this.channelConfiguration;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AudioSpecificConfig that = (AudioSpecificConfig) o;
        if (this.aacScalefactorDataResilienceFlag == that.aacScalefactorDataResilienceFlag && this.aacSectionDataResilienceFlag == that.aacSectionDataResilienceFlag && this.aacSpectralDataResilienceFlag == that.aacSpectralDataResilienceFlag && this.audioObjectType == that.audioObjectType && this.channelConfiguration == that.channelConfiguration && this.coreCoderDelay == that.coreCoderDelay && this.dependsOnCoreCoder == that.dependsOnCoreCoder && this.directMapping == that.directMapping && this.epConfig == that.epConfig && this.erHvxcExtensionFlag == that.erHvxcExtensionFlag && this.extensionAudioObjectType == that.extensionAudioObjectType && this.extensionChannelConfiguration == that.extensionChannelConfiguration && this.extensionFlag == that.extensionFlag && this.extensionFlag3 == that.extensionFlag3 && this.extensionSamplingFrequency == that.extensionSamplingFrequency && this.extensionSamplingFrequencyIndex == that.extensionSamplingFrequencyIndex && this.fillBits == that.fillBits && this.frameLengthFlag == that.frameLengthFlag && this.gaSpecificConfig == that.gaSpecificConfig && this.hilnContMode == that.hilnContMode && this.hilnEnhaLayer == that.hilnEnhaLayer && this.hilnEnhaQuantMode == that.hilnEnhaQuantMode && this.hilnFrameLength == that.hilnFrameLength && this.hilnMaxNumLine == that.hilnMaxNumLine && this.hilnQuantMode == that.hilnQuantMode && this.hilnSampleRateCode == that.hilnSampleRateCode && this.hvxcRateMode == that.hvxcRateMode && this.hvxcVarMode == that.hvxcVarMode && this.isBaseLayer == that.isBaseLayer && this.layerNr == that.layerNr && this.layer_length == that.layer_length && this.numOfSubFrame == that.numOfSubFrame && this.paraExtensionFlag == that.paraExtensionFlag && this.paraMode == that.paraMode && this.parametricSpecificConfig == that.parametricSpecificConfig && this.psPresentFlag == that.psPresentFlag && this.sacPayloadEmbedding == that.sacPayloadEmbedding && this.samplingFrequency == that.samplingFrequency && this.samplingFrequencyIndex == that.samplingFrequencyIndex && this.sbrPresentFlag == that.sbrPresentFlag && this.syncExtensionType == that.syncExtensionType && this.var_ScalableFlag == that.var_ScalableFlag && Arrays.equals(this.configBytes, that.configBytes)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        byte[] bArr = this.configBytes;
        int result = bArr != null ? Arrays.hashCode(bArr) : 0;
        return (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((result * 31) + this.audioObjectType) * 31) + this.samplingFrequencyIndex) * 31) + this.samplingFrequency) * 31) + this.channelConfiguration) * 31) + this.extensionAudioObjectType) * 31) + (this.sbrPresentFlag ? 1 : 0)) * 31) + (this.psPresentFlag ? 1 : 0)) * 31) + this.extensionSamplingFrequencyIndex) * 31) + this.extensionSamplingFrequency) * 31) + this.extensionChannelConfiguration) * 31) + this.sacPayloadEmbedding) * 31) + this.fillBits) * 31) + this.epConfig) * 31) + this.directMapping) * 31) + this.syncExtensionType) * 31) + this.frameLengthFlag) * 31) + this.dependsOnCoreCoder) * 31) + this.coreCoderDelay) * 31) + this.extensionFlag) * 31) + this.layerNr) * 31) + this.numOfSubFrame) * 31) + this.layer_length) * 31) + (this.aacSectionDataResilienceFlag ? 1 : 0)) * 31) + (this.aacScalefactorDataResilienceFlag ? 1 : 0)) * 31) + (this.aacSpectralDataResilienceFlag ? 1 : 0)) * 31) + this.extensionFlag3) * 31) + (this.gaSpecificConfig ? 1 : 0)) * 31) + this.isBaseLayer) * 31) + this.paraMode) * 31) + this.paraExtensionFlag) * 31) + this.hvxcVarMode) * 31) + this.hvxcRateMode) * 31) + this.erHvxcExtensionFlag) * 31) + this.var_ScalableFlag) * 31) + this.hilnQuantMode) * 31) + this.hilnMaxNumLine) * 31) + this.hilnSampleRateCode) * 31) + this.hilnFrameLength) * 31) + this.hilnContMode) * 31) + this.hilnEnhaLayer) * 31) + this.hilnEnhaQuantMode) * 31) + (this.parametricSpecificConfig ? 1 : 0);
    }
}
