package com.google.android.exoplayer2.text.cea;

import android.graphics.Color;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import com.google.android.exoplayer2.text.SubtitleInputBuffer;
import com.google.android.exoplayer2.text.SubtitleOutputBuffer;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public final class Cea708Decoder extends CeaDecoder {
    private static final int CC_VALID_FLAG = 4;
    private static final int CHARACTER_BIG_CARONS = 42;
    private static final int CHARACTER_BIG_OE = 44;
    private static final int CHARACTER_BOLD_BULLET = 53;
    private static final int CHARACTER_CLOSE_DOUBLE_QUOTE = 52;
    private static final int CHARACTER_CLOSE_SINGLE_QUOTE = 50;
    private static final int CHARACTER_DIAERESIS_Y = 63;
    private static final int CHARACTER_ELLIPSIS = 37;
    private static final int CHARACTER_FIVE_EIGHTHS = 120;
    private static final int CHARACTER_HORIZONTAL_BORDER = 125;
    private static final int CHARACTER_LOWER_LEFT_BORDER = 124;
    private static final int CHARACTER_LOWER_RIGHT_BORDER = 126;
    private static final int CHARACTER_MN = 127;
    private static final int CHARACTER_NBTSP = 33;
    private static final int CHARACTER_ONE_EIGHTH = 118;
    private static final int CHARACTER_OPEN_DOUBLE_QUOTE = 51;
    private static final int CHARACTER_OPEN_SINGLE_QUOTE = 49;
    private static final int CHARACTER_SEVEN_EIGHTHS = 121;
    private static final int CHARACTER_SM = 61;
    private static final int CHARACTER_SMALL_CARONS = 58;
    private static final int CHARACTER_SMALL_OE = 60;
    private static final int CHARACTER_SOLID_BLOCK = 48;
    private static final int CHARACTER_THREE_EIGHTHS = 119;
    private static final int CHARACTER_TM = 57;
    private static final int CHARACTER_TSP = 32;
    private static final int CHARACTER_UPPER_LEFT_BORDER = 127;
    private static final int CHARACTER_UPPER_RIGHT_BORDER = 123;
    private static final int CHARACTER_VERTICAL_BORDER = 122;
    private static final int COMMAND_BS = 8;
    private static final int COMMAND_CLW = 136;
    private static final int COMMAND_CR = 13;
    private static final int COMMAND_CW0 = 128;
    private static final int COMMAND_CW1 = 129;
    private static final int COMMAND_CW2 = 130;
    private static final int COMMAND_CW3 = 131;
    private static final int COMMAND_CW4 = 132;
    private static final int COMMAND_CW5 = 133;
    private static final int COMMAND_CW6 = 134;
    private static final int COMMAND_CW7 = 135;
    private static final int COMMAND_DF0 = 152;
    private static final int COMMAND_DF1 = 153;
    private static final int COMMAND_DF2 = 154;
    private static final int COMMAND_DF3 = 155;
    private static final int COMMAND_DF4 = 156;
    private static final int COMMAND_DF5 = 157;
    private static final int COMMAND_DF6 = 158;
    private static final int COMMAND_DF7 = 159;
    private static final int COMMAND_DLC = 142;
    private static final int COMMAND_DLW = 140;
    private static final int COMMAND_DLY = 141;
    private static final int COMMAND_DSW = 137;
    private static final int COMMAND_ETX = 3;
    private static final int COMMAND_EXT1 = 16;
    private static final int COMMAND_EXT1_END = 23;
    private static final int COMMAND_EXT1_START = 17;
    private static final int COMMAND_FF = 12;
    private static final int COMMAND_HCR = 14;
    private static final int COMMAND_HDW = 138;
    private static final int COMMAND_NUL = 0;
    private static final int COMMAND_P16_END = 31;
    private static final int COMMAND_P16_START = 24;
    private static final int COMMAND_RST = 143;
    private static final int COMMAND_SPA = 144;
    private static final int COMMAND_SPC = 145;
    private static final int COMMAND_SPL = 146;
    private static final int COMMAND_SWA = 151;
    private static final int COMMAND_TGW = 139;
    private static final int DTVCC_PACKET_DATA = 2;
    private static final int DTVCC_PACKET_START = 3;
    private static final int GROUP_C0_END = 31;
    private static final int GROUP_C1_END = 159;
    private static final int GROUP_C2_END = 31;
    private static final int GROUP_C3_END = 159;
    private static final int GROUP_G0_END = 127;
    private static final int GROUP_G1_END = 255;
    private static final int GROUP_G2_END = 127;
    private static final int GROUP_G3_END = 255;
    private static final int NUM_WINDOWS = 8;
    private static final String TAG = "Cea708Decoder";
    private final CueBuilder[] cueBuilders;
    private List<Cue> cues;
    private CueBuilder currentCueBuilder;
    private DtvCcPacket currentDtvCcPacket;
    private int currentWindow;
    private List<Cue> lastCues;
    private final int selectedServiceNumber;
    private final ParsableByteArray ccData = new ParsableByteArray();
    private final ParsableBitArray serviceBlockPacket = new ParsableBitArray();

    @Override // com.google.android.exoplayer2.text.cea.CeaDecoder, com.google.android.exoplayer2.decoder.Decoder
    public /* bridge */ /* synthetic */ SubtitleInputBuffer dequeueInputBuffer() throws SubtitleDecoderException {
        return super.dequeueInputBuffer();
    }

    @Override // com.google.android.exoplayer2.text.cea.CeaDecoder, com.google.android.exoplayer2.decoder.Decoder
    public /* bridge */ /* synthetic */ SubtitleOutputBuffer dequeueOutputBuffer() throws SubtitleDecoderException {
        return super.dequeueOutputBuffer();
    }

    @Override // com.google.android.exoplayer2.text.cea.CeaDecoder
    public /* bridge */ /* synthetic */ void queueInputBuffer(SubtitleInputBuffer subtitleInputBuffer) throws SubtitleDecoderException {
        super.queueInputBuffer(subtitleInputBuffer);
    }

    @Override // com.google.android.exoplayer2.text.cea.CeaDecoder, com.google.android.exoplayer2.decoder.Decoder
    public /* bridge */ /* synthetic */ void release() {
        super.release();
    }

    @Override // com.google.android.exoplayer2.text.cea.CeaDecoder, com.google.android.exoplayer2.text.SubtitleDecoder
    public /* bridge */ /* synthetic */ void setPositionUs(long j) {
        super.setPositionUs(j);
    }

    public Cea708Decoder(int accessibilityChannel, List<byte[]> initializationData) {
        this.selectedServiceNumber = accessibilityChannel == -1 ? 1 : accessibilityChannel;
        this.cueBuilders = new CueBuilder[8];
        for (int i = 0; i < 8; i++) {
            this.cueBuilders[i] = new CueBuilder();
        }
        this.currentCueBuilder = this.cueBuilders[0];
        resetCueBuilders();
    }

    @Override // com.google.android.exoplayer2.text.cea.CeaDecoder, com.google.android.exoplayer2.decoder.Decoder
    public String getName() {
        return TAG;
    }

    @Override // com.google.android.exoplayer2.text.cea.CeaDecoder, com.google.android.exoplayer2.decoder.Decoder
    public void flush() {
        super.flush();
        this.cues = null;
        this.lastCues = null;
        this.currentWindow = 0;
        this.currentCueBuilder = this.cueBuilders[0];
        resetCueBuilders();
        this.currentDtvCcPacket = null;
    }

    @Override // com.google.android.exoplayer2.text.cea.CeaDecoder
    protected boolean isNewSubtitleDataAvailable() {
        return this.cues != this.lastCues;
    }

    @Override // com.google.android.exoplayer2.text.cea.CeaDecoder
    protected Subtitle createSubtitle() {
        this.lastCues = this.cues;
        return new CeaSubtitle(this.cues);
    }

    @Override // com.google.android.exoplayer2.text.cea.CeaDecoder
    protected void decode(SubtitleInputBuffer inputBuffer) {
        byte[] inputBufferData = inputBuffer.data.array();
        this.ccData.reset(inputBufferData, inputBuffer.data.limit());
        while (this.ccData.bytesLeft() >= 3) {
            int ccTypeAndValid = this.ccData.readUnsignedByte() & 7;
            int ccType = ccTypeAndValid & 3;
            boolean z = false;
            boolean ccValid = (ccTypeAndValid & 4) == 4;
            byte ccData1 = (byte) this.ccData.readUnsignedByte();
            byte ccData2 = (byte) this.ccData.readUnsignedByte();
            if (ccType == 2 || ccType == 3) {
                if (ccValid) {
                    if (ccType == 3) {
                        finalizeCurrentPacket();
                        int sequenceNumber = (ccData1 & 192) >> 6;
                        int packetSize = ccData1 & 63;
                        if (packetSize == 0) {
                            packetSize = 64;
                        }
                        DtvCcPacket dtvCcPacket = new DtvCcPacket(sequenceNumber, packetSize);
                        this.currentDtvCcPacket = dtvCcPacket;
                        byte[] bArr = dtvCcPacket.packetData;
                        DtvCcPacket dtvCcPacket2 = this.currentDtvCcPacket;
                        int i = dtvCcPacket2.currentIndex;
                        dtvCcPacket2.currentIndex = i + 1;
                        bArr[i] = ccData2;
                    } else {
                        if (ccType == 2) {
                            z = true;
                        }
                        Assertions.checkArgument(z);
                        DtvCcPacket dtvCcPacket3 = this.currentDtvCcPacket;
                        if (dtvCcPacket3 == null) {
                            Log.e(TAG, "Encountered DTVCC_PACKET_DATA before DTVCC_PACKET_START");
                        } else {
                            byte[] bArr2 = dtvCcPacket3.packetData;
                            DtvCcPacket dtvCcPacket4 = this.currentDtvCcPacket;
                            int i2 = dtvCcPacket4.currentIndex;
                            dtvCcPacket4.currentIndex = i2 + 1;
                            bArr2[i2] = ccData1;
                            byte[] bArr3 = this.currentDtvCcPacket.packetData;
                            DtvCcPacket dtvCcPacket5 = this.currentDtvCcPacket;
                            int i3 = dtvCcPacket5.currentIndex;
                            dtvCcPacket5.currentIndex = i3 + 1;
                            bArr3[i3] = ccData2;
                        }
                    }
                    if (this.currentDtvCcPacket.currentIndex == (this.currentDtvCcPacket.packetSize * 2) - 1) {
                        finalizeCurrentPacket();
                    }
                }
            }
        }
    }

    private void finalizeCurrentPacket() {
        if (this.currentDtvCcPacket == null) {
            return;
        }
        processCurrentPacket();
        this.currentDtvCcPacket = null;
    }

    private void processCurrentPacket() {
        if (this.currentDtvCcPacket.currentIndex != (this.currentDtvCcPacket.packetSize * 2) - 1) {
            StringBuilder sb = new StringBuilder();
            sb.append("DtvCcPacket ended prematurely; size is ");
            sb.append((this.currentDtvCcPacket.packetSize * 2) - 1);
            sb.append(", but current index is ");
            sb.append(this.currentDtvCcPacket.currentIndex);
            sb.append(" (sequence number ");
            sb.append(this.currentDtvCcPacket.sequenceNumber);
            sb.append("); ignoring packet");
            Log.w(TAG, sb.toString());
            return;
        }
        this.serviceBlockPacket.reset(this.currentDtvCcPacket.packetData, this.currentDtvCcPacket.currentIndex);
        int serviceNumber = this.serviceBlockPacket.readBits(3);
        int blockSize = this.serviceBlockPacket.readBits(5);
        if (serviceNumber == 7) {
            this.serviceBlockPacket.skipBits(2);
            serviceNumber = this.serviceBlockPacket.readBits(6);
            if (serviceNumber < 7) {
                Log.w(TAG, "Invalid extended service number: " + serviceNumber);
            }
        }
        if (blockSize == 0) {
            if (serviceNumber != 0) {
                Log.w(TAG, "serviceNumber is non-zero (" + serviceNumber + ") when blockSize is 0");
            }
        } else if (serviceNumber != this.selectedServiceNumber) {
        } else {
            boolean cuesNeedUpdate = false;
            while (this.serviceBlockPacket.bitsLeft() > 0) {
                int command = this.serviceBlockPacket.readBits(8);
                if (command != 16) {
                    if (command <= 31) {
                        handleC0Command(command);
                    } else if (command <= 127) {
                        handleG0Character(command);
                        cuesNeedUpdate = true;
                    } else if (command <= 159) {
                        handleC1Command(command);
                        cuesNeedUpdate = true;
                    } else if (command <= 255) {
                        handleG1Character(command);
                        cuesNeedUpdate = true;
                    } else {
                        Log.w(TAG, "Invalid base command: " + command);
                    }
                } else {
                    int command2 = this.serviceBlockPacket.readBits(8);
                    if (command2 <= 31) {
                        handleC2Command(command2);
                    } else if (command2 <= 127) {
                        handleG2Character(command2);
                        cuesNeedUpdate = true;
                    } else if (command2 <= 159) {
                        handleC3Command(command2);
                    } else if (command2 <= 255) {
                        handleG3Character(command2);
                        cuesNeedUpdate = true;
                    } else {
                        Log.w(TAG, "Invalid extended command: " + command2);
                    }
                }
            }
            if (cuesNeedUpdate) {
                this.cues = getDisplayCues();
            }
        }
    }

    private void handleC0Command(int command) {
        switch (command) {
            case 0:
            case 14:
                return;
            case 3:
                this.cues = getDisplayCues();
                return;
            case 8:
                this.currentCueBuilder.backspace();
                return;
            case 12:
                resetCueBuilders();
                return;
            case 13:
                this.currentCueBuilder.append('\n');
                return;
            default:
                if (command >= 17 && command <= 23) {
                    Log.w(TAG, "Currently unsupported COMMAND_EXT1 Command: " + command);
                    this.serviceBlockPacket.skipBits(8);
                    return;
                } else if (command >= 24 && command <= 31) {
                    Log.w(TAG, "Currently unsupported COMMAND_P16 Command: " + command);
                    this.serviceBlockPacket.skipBits(16);
                    return;
                } else {
                    Log.w(TAG, "Invalid C0 command: " + command);
                    return;
                }
        }
    }

    private void handleC1Command(int command) {
        switch (command) {
            case 128:
            case 129:
            case 130:
            case COMMAND_CW3 /* 131 */:
            case COMMAND_CW4 /* 132 */:
            case COMMAND_CW5 /* 133 */:
            case 134:
            case 135:
                int window = command - 128;
                if (this.currentWindow != window) {
                    this.currentWindow = window;
                    this.currentCueBuilder = this.cueBuilders[window];
                    return;
                }
                return;
            case COMMAND_CLW /* 136 */:
                for (int i = 1; i <= 8; i++) {
                    if (this.serviceBlockPacket.readBit()) {
                        this.cueBuilders[8 - i].clear();
                    }
                }
                return;
            case COMMAND_DSW /* 137 */:
                for (int i2 = 1; i2 <= 8; i2++) {
                    if (this.serviceBlockPacket.readBit()) {
                        this.cueBuilders[8 - i2].setVisibility(true);
                    }
                }
                return;
            case 138:
                for (int i3 = 1; i3 <= 8; i3++) {
                    if (this.serviceBlockPacket.readBit()) {
                        this.cueBuilders[8 - i3].setVisibility(false);
                    }
                }
                return;
            case COMMAND_TGW /* 139 */:
                for (int i4 = 1; i4 <= 8; i4++) {
                    if (this.serviceBlockPacket.readBit()) {
                        CueBuilder cueBuilder = this.cueBuilders[8 - i4];
                        cueBuilder.setVisibility(!cueBuilder.isVisible());
                    }
                }
                return;
            case COMMAND_DLW /* 140 */:
                for (int i5 = 1; i5 <= 8; i5++) {
                    if (this.serviceBlockPacket.readBit()) {
                        this.cueBuilders[8 - i5].reset();
                    }
                }
                return;
            case COMMAND_DLY /* 141 */:
                this.serviceBlockPacket.skipBits(8);
                return;
            case COMMAND_DLC /* 142 */:
                return;
            case 143:
                resetCueBuilders();
                return;
            case COMMAND_SPA /* 144 */:
                if (!this.currentCueBuilder.isDefined()) {
                    this.serviceBlockPacket.skipBits(16);
                    return;
                } else {
                    handleSetPenAttributes();
                    return;
                }
            case COMMAND_SPC /* 145 */:
                if (!this.currentCueBuilder.isDefined()) {
                    this.serviceBlockPacket.skipBits(24);
                    return;
                } else {
                    handleSetPenColor();
                    return;
                }
            case COMMAND_SPL /* 146 */:
                if (!this.currentCueBuilder.isDefined()) {
                    this.serviceBlockPacket.skipBits(16);
                    return;
                } else {
                    handleSetPenLocation();
                    return;
                }
            case 147:
            case 148:
            case 149:
            case 150:
            default:
                Log.w(TAG, "Invalid C1 command: " + command);
                return;
            case 151:
                if (!this.currentCueBuilder.isDefined()) {
                    this.serviceBlockPacket.skipBits(32);
                    return;
                } else {
                    handleSetWindowAttributes();
                    return;
                }
            case COMMAND_DF0 /* 152 */:
            case COMMAND_DF1 /* 153 */:
            case COMMAND_DF2 /* 154 */:
            case COMMAND_DF3 /* 155 */:
            case COMMAND_DF4 /* 156 */:
            case COMMAND_DF5 /* 157 */:
            case COMMAND_DF6 /* 158 */:
            case 159:
                int window2 = command - 152;
                handleDefineWindow(window2);
                if (this.currentWindow != window2) {
                    this.currentWindow = window2;
                    this.currentCueBuilder = this.cueBuilders[window2];
                    return;
                }
                return;
        }
    }

    private void handleC2Command(int command) {
        if (command > 7) {
            if (command <= 15) {
                this.serviceBlockPacket.skipBits(8);
            } else if (command <= 23) {
                this.serviceBlockPacket.skipBits(16);
            } else if (command <= 31) {
                this.serviceBlockPacket.skipBits(24);
            }
        }
    }

    private void handleC3Command(int command) {
        if (command <= 135) {
            this.serviceBlockPacket.skipBits(32);
        } else if (command <= 143) {
            this.serviceBlockPacket.skipBits(40);
        } else if (command <= 159) {
            this.serviceBlockPacket.skipBits(2);
            int length = this.serviceBlockPacket.readBits(6);
            this.serviceBlockPacket.skipBits(length * 8);
        }
    }

    private void handleG0Character(int characterCode) {
        if (characterCode == 127) {
            this.currentCueBuilder.append((char) 9835);
        } else {
            this.currentCueBuilder.append((char) (characterCode & 255));
        }
    }

    private void handleG1Character(int characterCode) {
        this.currentCueBuilder.append((char) (characterCode & 255));
    }

    private void handleG2Character(int characterCode) {
        switch (characterCode) {
            case 32:
                this.currentCueBuilder.append(' ');
                return;
            case 33:
                this.currentCueBuilder.append((char) 160);
                return;
            case 37:
                this.currentCueBuilder.append((char) 8230);
                return;
            case 42:
                this.currentCueBuilder.append((char) 352);
                return;
            case 44:
                this.currentCueBuilder.append((char) 338);
                return;
            case 48:
                this.currentCueBuilder.append((char) 9608);
                return;
            case 49:
                this.currentCueBuilder.append((char) 8216);
                return;
            case 50:
                this.currentCueBuilder.append((char) 8217);
                return;
            case 51:
                this.currentCueBuilder.append((char) 8220);
                return;
            case 52:
                this.currentCueBuilder.append((char) 8221);
                return;
            case 53:
                this.currentCueBuilder.append((char) 8226);
                return;
            case 57:
                this.currentCueBuilder.append((char) 8482);
                return;
            case 58:
                this.currentCueBuilder.append((char) 353);
                return;
            case 60:
                this.currentCueBuilder.append((char) 339);
                return;
            case 61:
                this.currentCueBuilder.append((char) 8480);
                return;
            case 63:
                this.currentCueBuilder.append((char) 376);
                return;
            case CHARACTER_ONE_EIGHTH /* 118 */:
                this.currentCueBuilder.append((char) 8539);
                return;
            case CHARACTER_THREE_EIGHTHS /* 119 */:
                this.currentCueBuilder.append((char) 8540);
                return;
            case CHARACTER_FIVE_EIGHTHS /* 120 */:
                this.currentCueBuilder.append((char) 8541);
                return;
            case CHARACTER_SEVEN_EIGHTHS /* 121 */:
                this.currentCueBuilder.append((char) 8542);
                return;
            case CHARACTER_VERTICAL_BORDER /* 122 */:
                this.currentCueBuilder.append((char) 9474);
                return;
            case CHARACTER_UPPER_RIGHT_BORDER /* 123 */:
                this.currentCueBuilder.append((char) 9488);
                return;
            case CHARACTER_LOWER_LEFT_BORDER /* 124 */:
                this.currentCueBuilder.append((char) 9492);
                return;
            case 125:
                this.currentCueBuilder.append((char) 9472);
                return;
            case CHARACTER_LOWER_RIGHT_BORDER /* 126 */:
                this.currentCueBuilder.append((char) 9496);
                return;
            case 127:
                this.currentCueBuilder.append((char) 9484);
                return;
            default:
                Log.w(TAG, "Invalid G2 character: " + characterCode);
                return;
        }
    }

    private void handleG3Character(int characterCode) {
        if (characterCode == 160) {
            this.currentCueBuilder.append((char) 13252);
            return;
        }
        Log.w(TAG, "Invalid G3 character: " + characterCode);
        this.currentCueBuilder.append('_');
    }

    private void handleSetPenAttributes() {
        int textTag = this.serviceBlockPacket.readBits(4);
        int offset = this.serviceBlockPacket.readBits(2);
        int penSize = this.serviceBlockPacket.readBits(2);
        boolean italicsToggle = this.serviceBlockPacket.readBit();
        boolean underlineToggle = this.serviceBlockPacket.readBit();
        int edgeType = this.serviceBlockPacket.readBits(3);
        int fontStyle = this.serviceBlockPacket.readBits(3);
        this.currentCueBuilder.setPenAttributes(textTag, offset, penSize, italicsToggle, underlineToggle, edgeType, fontStyle);
    }

    private void handleSetPenColor() {
        int foregroundO = this.serviceBlockPacket.readBits(2);
        int foregroundR = this.serviceBlockPacket.readBits(2);
        int foregroundG = this.serviceBlockPacket.readBits(2);
        int foregroundB = this.serviceBlockPacket.readBits(2);
        int foregroundColor = CueBuilder.getArgbColorFromCeaColor(foregroundR, foregroundG, foregroundB, foregroundO);
        int backgroundO = this.serviceBlockPacket.readBits(2);
        int backgroundR = this.serviceBlockPacket.readBits(2);
        int backgroundG = this.serviceBlockPacket.readBits(2);
        int backgroundB = this.serviceBlockPacket.readBits(2);
        int backgroundColor = CueBuilder.getArgbColorFromCeaColor(backgroundR, backgroundG, backgroundB, backgroundO);
        this.serviceBlockPacket.skipBits(2);
        int edgeR = this.serviceBlockPacket.readBits(2);
        int edgeG = this.serviceBlockPacket.readBits(2);
        int edgeB = this.serviceBlockPacket.readBits(2);
        int edgeColor = CueBuilder.getArgbColorFromCeaColor(edgeR, edgeG, edgeB);
        this.currentCueBuilder.setPenColor(foregroundColor, backgroundColor, edgeColor);
    }

    private void handleSetPenLocation() {
        this.serviceBlockPacket.skipBits(4);
        int row = this.serviceBlockPacket.readBits(4);
        this.serviceBlockPacket.skipBits(2);
        int column = this.serviceBlockPacket.readBits(6);
        this.currentCueBuilder.setPenLocation(row, column);
    }

    private void handleSetWindowAttributes() {
        int borderType;
        int fillO = this.serviceBlockPacket.readBits(2);
        int fillR = this.serviceBlockPacket.readBits(2);
        int fillG = this.serviceBlockPacket.readBits(2);
        int fillB = this.serviceBlockPacket.readBits(2);
        int fillColor = CueBuilder.getArgbColorFromCeaColor(fillR, fillG, fillB, fillO);
        int borderType2 = this.serviceBlockPacket.readBits(2);
        int borderR = this.serviceBlockPacket.readBits(2);
        int borderG = this.serviceBlockPacket.readBits(2);
        int borderB = this.serviceBlockPacket.readBits(2);
        int borderColor = CueBuilder.getArgbColorFromCeaColor(borderR, borderG, borderB);
        if (!this.serviceBlockPacket.readBit()) {
            borderType = borderType2;
        } else {
            borderType = borderType2 | 4;
        }
        boolean wordWrapToggle = this.serviceBlockPacket.readBit();
        int printDirection = this.serviceBlockPacket.readBits(2);
        int scrollDirection = this.serviceBlockPacket.readBits(2);
        int justification = this.serviceBlockPacket.readBits(2);
        this.serviceBlockPacket.skipBits(8);
        this.currentCueBuilder.setWindowAttributes(fillColor, borderColor, wordWrapToggle, borderType, printDirection, scrollDirection, justification);
    }

    private void handleDefineWindow(int window) {
        CueBuilder cueBuilder = this.cueBuilders[window];
        this.serviceBlockPacket.skipBits(2);
        boolean visible = this.serviceBlockPacket.readBit();
        boolean rowLock = this.serviceBlockPacket.readBit();
        boolean columnLock = this.serviceBlockPacket.readBit();
        int priority = this.serviceBlockPacket.readBits(3);
        boolean relativePositioning = this.serviceBlockPacket.readBit();
        int verticalAnchor = this.serviceBlockPacket.readBits(7);
        int horizontalAnchor = this.serviceBlockPacket.readBits(8);
        int anchorId = this.serviceBlockPacket.readBits(4);
        int rowCount = this.serviceBlockPacket.readBits(4);
        this.serviceBlockPacket.skipBits(2);
        int columnCount = this.serviceBlockPacket.readBits(6);
        this.serviceBlockPacket.skipBits(2);
        int windowStyle = this.serviceBlockPacket.readBits(3);
        int penStyle = this.serviceBlockPacket.readBits(3);
        cueBuilder.defineWindow(visible, rowLock, columnLock, priority, relativePositioning, verticalAnchor, horizontalAnchor, rowCount, columnCount, anchorId, windowStyle, penStyle);
    }

    private List<Cue> getDisplayCues() {
        List<Cea708Cue> displayCues = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            if (!this.cueBuilders[i].isEmpty() && this.cueBuilders[i].isVisible()) {
                displayCues.add(this.cueBuilders[i].build());
            }
        }
        Collections.sort(displayCues);
        return Collections.unmodifiableList(displayCues);
    }

    private void resetCueBuilders() {
        for (int i = 0; i < 8; i++) {
            this.cueBuilders[i].reset();
        }
    }

    /* loaded from: classes3.dex */
    public static final class DtvCcPacket {
        int currentIndex = 0;
        public final byte[] packetData;
        public final int packetSize;
        public final int sequenceNumber;

        public DtvCcPacket(int sequenceNumber, int packetSize) {
            this.sequenceNumber = sequenceNumber;
            this.packetSize = packetSize;
            this.packetData = new byte[(packetSize * 2) - 1];
        }
    }

    /* loaded from: classes3.dex */
    public static final class CueBuilder {
        private static final int BORDER_AND_EDGE_TYPE_NONE = 0;
        private static final int BORDER_AND_EDGE_TYPE_UNIFORM = 3;
        public static final int COLOR_SOLID_BLACK;
        public static final int COLOR_TRANSPARENT;
        private static final int DEFAULT_PRIORITY = 4;
        private static final int DIRECTION_BOTTOM_TO_TOP = 3;
        private static final int DIRECTION_LEFT_TO_RIGHT = 0;
        private static final int DIRECTION_RIGHT_TO_LEFT = 1;
        private static final int DIRECTION_TOP_TO_BOTTOM = 2;
        private static final int HORIZONTAL_SIZE = 209;
        private static final int JUSTIFICATION_CENTER = 2;
        private static final int JUSTIFICATION_FULL = 3;
        private static final int JUSTIFICATION_LEFT = 0;
        private static final int JUSTIFICATION_RIGHT = 1;
        private static final int MAXIMUM_ROW_COUNT = 15;
        private static final int PEN_FONT_STYLE_DEFAULT = 0;
        private static final int PEN_FONT_STYLE_MONOSPACED_WITHOUT_SERIFS = 3;
        private static final int PEN_FONT_STYLE_MONOSPACED_WITH_SERIFS = 1;
        private static final int PEN_FONT_STYLE_PROPORTIONALLY_SPACED_WITHOUT_SERIFS = 4;
        private static final int PEN_FONT_STYLE_PROPORTIONALLY_SPACED_WITH_SERIFS = 2;
        private static final int PEN_OFFSET_NORMAL = 1;
        private static final int PEN_SIZE_STANDARD = 1;
        private static final int[] PEN_STYLE_BACKGROUND;
        private static final int RELATIVE_CUE_SIZE = 99;
        private static final int VERTICAL_SIZE = 74;
        private static final int[] WINDOW_STYLE_FILL;
        private int anchorId;
        private int backgroundColor;
        private int backgroundColorStartPosition;
        private boolean defined;
        private int foregroundColor;
        private int foregroundColorStartPosition;
        private int horizontalAnchor;
        private int italicsStartPosition;
        private int justification;
        private int penStyleId;
        private int priority;
        private boolean relativePositioning;
        private int row;
        private int rowCount;
        private boolean rowLock;
        private int underlineStartPosition;
        private int verticalAnchor;
        private boolean visible;
        private int windowFillColor;
        private int windowStyleId;
        public static final int COLOR_SOLID_WHITE = getArgbColorFromCeaColor(2, 2, 2, 0);
        private static final int[] WINDOW_STYLE_JUSTIFICATION = {0, 0, 0, 0, 0, 2, 0};
        private static final int[] WINDOW_STYLE_PRINT_DIRECTION = {0, 0, 0, 0, 0, 0, 2};
        private static final int[] WINDOW_STYLE_SCROLL_DIRECTION = {3, 3, 3, 3, 3, 3, 1};
        private static final boolean[] WINDOW_STYLE_WORD_WRAP = {false, false, false, true, true, true, false};
        private static final int[] PEN_STYLE_FONT_STYLE = {0, 1, 2, 3, 4, 3, 4};
        private static final int[] PEN_STYLE_EDGE_TYPE = {0, 0, 0, 0, 0, 3, 3};
        private final List<SpannableString> rolledUpCaptions = new ArrayList();
        private final SpannableStringBuilder captionStringBuilder = new SpannableStringBuilder();

        static {
            int argbColorFromCeaColor = getArgbColorFromCeaColor(0, 0, 0, 0);
            COLOR_SOLID_BLACK = argbColorFromCeaColor;
            int argbColorFromCeaColor2 = getArgbColorFromCeaColor(0, 0, 0, 3);
            COLOR_TRANSPARENT = argbColorFromCeaColor2;
            WINDOW_STYLE_FILL = new int[]{argbColorFromCeaColor, argbColorFromCeaColor2, argbColorFromCeaColor, argbColorFromCeaColor, argbColorFromCeaColor2, argbColorFromCeaColor, argbColorFromCeaColor};
            PEN_STYLE_BACKGROUND = new int[]{argbColorFromCeaColor, argbColorFromCeaColor, argbColorFromCeaColor, argbColorFromCeaColor, argbColorFromCeaColor, argbColorFromCeaColor2, argbColorFromCeaColor2};
        }

        public CueBuilder() {
            reset();
        }

        public boolean isEmpty() {
            return !isDefined() || (this.rolledUpCaptions.isEmpty() && this.captionStringBuilder.length() == 0);
        }

        public void reset() {
            clear();
            this.defined = false;
            this.visible = false;
            this.priority = 4;
            this.relativePositioning = false;
            this.verticalAnchor = 0;
            this.horizontalAnchor = 0;
            this.anchorId = 0;
            this.rowCount = 15;
            this.rowLock = true;
            this.justification = 0;
            this.windowStyleId = 0;
            this.penStyleId = 0;
            int i = COLOR_SOLID_BLACK;
            this.windowFillColor = i;
            this.foregroundColor = COLOR_SOLID_WHITE;
            this.backgroundColor = i;
        }

        public void clear() {
            this.rolledUpCaptions.clear();
            this.captionStringBuilder.clear();
            this.italicsStartPosition = -1;
            this.underlineStartPosition = -1;
            this.foregroundColorStartPosition = -1;
            this.backgroundColorStartPosition = -1;
            this.row = 0;
        }

        public boolean isDefined() {
            return this.defined;
        }

        public void setVisibility(boolean visible) {
            this.visible = visible;
        }

        public boolean isVisible() {
            return this.visible;
        }

        public void defineWindow(boolean visible, boolean rowLock, boolean columnLock, int priority, boolean relativePositioning, int verticalAnchor, int horizontalAnchor, int rowCount, int columnCount, int anchorId, int windowStyleId, int penStyleId) {
            this.defined = true;
            this.visible = visible;
            this.rowLock = rowLock;
            this.priority = priority;
            this.relativePositioning = relativePositioning;
            this.verticalAnchor = verticalAnchor;
            this.horizontalAnchor = horizontalAnchor;
            this.anchorId = anchorId;
            if (this.rowCount != rowCount + 1) {
                this.rowCount = rowCount + 1;
                while (true) {
                    if ((!rowLock || this.rolledUpCaptions.size() < this.rowCount) && this.rolledUpCaptions.size() < 15) {
                        break;
                    }
                    this.rolledUpCaptions.remove(0);
                }
            }
            if (windowStyleId != 0 && this.windowStyleId != windowStyleId) {
                this.windowStyleId = windowStyleId;
                int windowStyleIdIndex = windowStyleId - 1;
                setWindowAttributes(WINDOW_STYLE_FILL[windowStyleIdIndex], COLOR_TRANSPARENT, WINDOW_STYLE_WORD_WRAP[windowStyleIdIndex], 0, WINDOW_STYLE_PRINT_DIRECTION[windowStyleIdIndex], WINDOW_STYLE_SCROLL_DIRECTION[windowStyleIdIndex], WINDOW_STYLE_JUSTIFICATION[windowStyleIdIndex]);
            }
            if (penStyleId != 0 && this.penStyleId != penStyleId) {
                this.penStyleId = penStyleId;
                int penStyleIdIndex = penStyleId - 1;
                setPenAttributes(0, 1, 1, false, false, PEN_STYLE_EDGE_TYPE[penStyleIdIndex], PEN_STYLE_FONT_STYLE[penStyleIdIndex]);
                setPenColor(COLOR_SOLID_WHITE, PEN_STYLE_BACKGROUND[penStyleIdIndex], COLOR_SOLID_BLACK);
            }
        }

        public void setWindowAttributes(int fillColor, int borderColor, boolean wordWrapToggle, int borderType, int printDirection, int scrollDirection, int justification) {
            this.windowFillColor = fillColor;
            this.justification = justification;
        }

        public void setPenAttributes(int textTag, int offset, int penSize, boolean italicsToggle, boolean underlineToggle, int edgeType, int fontStyle) {
            if (this.italicsStartPosition != -1) {
                if (!italicsToggle) {
                    this.captionStringBuilder.setSpan(new StyleSpan(2), this.italicsStartPosition, this.captionStringBuilder.length(), 33);
                    this.italicsStartPosition = -1;
                }
            } else if (italicsToggle) {
                this.italicsStartPosition = this.captionStringBuilder.length();
            }
            if (this.underlineStartPosition != -1) {
                if (!underlineToggle) {
                    this.captionStringBuilder.setSpan(new UnderlineSpan(), this.underlineStartPosition, this.captionStringBuilder.length(), 33);
                    this.underlineStartPosition = -1;
                }
            } else if (underlineToggle) {
                this.underlineStartPosition = this.captionStringBuilder.length();
            }
        }

        public void setPenColor(int foregroundColor, int backgroundColor, int edgeColor) {
            if (this.foregroundColorStartPosition != -1 && this.foregroundColor != foregroundColor) {
                this.captionStringBuilder.setSpan(new ForegroundColorSpan(this.foregroundColor), this.foregroundColorStartPosition, this.captionStringBuilder.length(), 33);
            }
            if (foregroundColor != COLOR_SOLID_WHITE) {
                this.foregroundColorStartPosition = this.captionStringBuilder.length();
                this.foregroundColor = foregroundColor;
            }
            if (this.backgroundColorStartPosition != -1 && this.backgroundColor != backgroundColor) {
                this.captionStringBuilder.setSpan(new BackgroundColorSpan(this.backgroundColor), this.backgroundColorStartPosition, this.captionStringBuilder.length(), 33);
            }
            if (backgroundColor != COLOR_SOLID_BLACK) {
                this.backgroundColorStartPosition = this.captionStringBuilder.length();
                this.backgroundColor = backgroundColor;
            }
        }

        public void setPenLocation(int row, int column) {
            if (this.row != row) {
                append('\n');
            }
            this.row = row;
        }

        public void backspace() {
            int length = this.captionStringBuilder.length();
            if (length > 0) {
                this.captionStringBuilder.delete(length - 1, length);
            }
        }

        public void append(char text) {
            if (text == '\n') {
                this.rolledUpCaptions.add(buildSpannableString());
                this.captionStringBuilder.clear();
                if (this.italicsStartPosition != -1) {
                    this.italicsStartPosition = 0;
                }
                if (this.underlineStartPosition != -1) {
                    this.underlineStartPosition = 0;
                }
                if (this.foregroundColorStartPosition != -1) {
                    this.foregroundColorStartPosition = 0;
                }
                if (this.backgroundColorStartPosition != -1) {
                    this.backgroundColorStartPosition = 0;
                }
                while (true) {
                    if ((this.rowLock && this.rolledUpCaptions.size() >= this.rowCount) || this.rolledUpCaptions.size() >= 15) {
                        this.rolledUpCaptions.remove(0);
                    } else {
                        return;
                    }
                }
            } else {
                this.captionStringBuilder.append(text);
            }
        }

        public SpannableString buildSpannableString() {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.captionStringBuilder);
            int length = spannableStringBuilder.length();
            if (length > 0) {
                if (this.italicsStartPosition != -1) {
                    spannableStringBuilder.setSpan(new StyleSpan(2), this.italicsStartPosition, length, 33);
                }
                if (this.underlineStartPosition != -1) {
                    spannableStringBuilder.setSpan(new UnderlineSpan(), this.underlineStartPosition, length, 33);
                }
                if (this.foregroundColorStartPosition != -1) {
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(this.foregroundColor), this.foregroundColorStartPosition, length, 33);
                }
                if (this.backgroundColorStartPosition != -1) {
                    spannableStringBuilder.setSpan(new BackgroundColorSpan(this.backgroundColor), this.backgroundColorStartPosition, length, 33);
                }
            }
            return new SpannableString(spannableStringBuilder);
        }

        public Cea708Cue build() {
            Layout.Alignment alignment;
            float line;
            float position;
            int verticalAnchorType;
            int horizontalAnchorType;
            if (isEmpty()) {
                return null;
            }
            SpannableStringBuilder cueString = new SpannableStringBuilder();
            for (int i = 0; i < this.rolledUpCaptions.size(); i++) {
                cueString.append((CharSequence) this.rolledUpCaptions.get(i));
                cueString.append('\n');
            }
            cueString.append((CharSequence) buildSpannableString());
            switch (this.justification) {
                case 0:
                case 3:
                    Layout.Alignment alignment2 = Layout.Alignment.ALIGN_NORMAL;
                    alignment = alignment2;
                    break;
                case 1:
                    Layout.Alignment alignment3 = Layout.Alignment.ALIGN_OPPOSITE;
                    alignment = alignment3;
                    break;
                case 2:
                    Layout.Alignment alignment4 = Layout.Alignment.ALIGN_CENTER;
                    alignment = alignment4;
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected justification value: " + this.justification);
            }
            if (this.relativePositioning) {
                position = this.horizontalAnchor / 99.0f;
                line = this.verticalAnchor / 99.0f;
            } else {
                position = this.horizontalAnchor / 209.0f;
                line = this.verticalAnchor / 74.0f;
            }
            float position2 = (position * 0.9f) + 0.05f;
            float line2 = (0.9f * line) + 0.05f;
            int horizontalAnchorType2 = this.anchorId;
            if (horizontalAnchorType2 % 3 == 0) {
                verticalAnchorType = 0;
            } else {
                int verticalAnchorType2 = horizontalAnchorType2 % 3;
                if (verticalAnchorType2 == 1) {
                    verticalAnchorType = 1;
                } else {
                    verticalAnchorType = 2;
                }
            }
            if (horizontalAnchorType2 / 3 == 0) {
                horizontalAnchorType = 0;
            } else if (horizontalAnchorType2 / 3 == 1) {
                horizontalAnchorType = 1;
            } else {
                horizontalAnchorType = 2;
            }
            boolean windowColorSet = this.windowFillColor != COLOR_SOLID_BLACK;
            return new Cea708Cue(cueString, alignment, line2, 0, verticalAnchorType, position2, horizontalAnchorType, -3.4028235E38f, windowColorSet, this.windowFillColor, this.priority);
        }

        public static int getArgbColorFromCeaColor(int red, int green, int blue) {
            return getArgbColorFromCeaColor(red, green, blue, 0);
        }

        public static int getArgbColorFromCeaColor(int red, int green, int blue, int opacity) {
            int alpha;
            int i = 0;
            Assertions.checkIndex(red, 0, 4);
            Assertions.checkIndex(green, 0, 4);
            Assertions.checkIndex(blue, 0, 4);
            Assertions.checkIndex(opacity, 0, 4);
            switch (opacity) {
                case 0:
                case 1:
                    alpha = 255;
                    break;
                case 2:
                    alpha = 127;
                    break;
                case 3:
                    alpha = 0;
                    break;
                default:
                    alpha = 255;
                    break;
            }
            int i2 = red > 1 ? 255 : 0;
            int i3 = green > 1 ? 255 : 0;
            if (blue > 1) {
                i = 255;
            }
            return Color.argb(alpha, i2, i3, i);
        }
    }
}
