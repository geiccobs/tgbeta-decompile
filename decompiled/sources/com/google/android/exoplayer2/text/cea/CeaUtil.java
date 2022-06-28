package com.google.android.exoplayer2.text.cea;

import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
/* loaded from: classes3.dex */
public final class CeaUtil {
    private static final int COUNTRY_CODE = 181;
    private static final int PAYLOAD_TYPE_CC = 4;
    private static final int PROVIDER_CODE_ATSC = 49;
    private static final int PROVIDER_CODE_DIRECTV = 47;
    private static final String TAG = "CeaUtil";
    public static final int USER_DATA_IDENTIFIER_GA94 = 1195456820;
    public static final int USER_DATA_TYPE_CODE_MPEG_CC = 3;

    public static void consume(long presentationTimeUs, ParsableByteArray seiBuffer, TrackOutput[] outputs) {
        while (true) {
            boolean z = true;
            if (seiBuffer.bytesLeft() > 1) {
                int payloadType = readNon255TerminatedValue(seiBuffer);
                int payloadSize = readNon255TerminatedValue(seiBuffer);
                int nextPayloadPosition = seiBuffer.getPosition() + payloadSize;
                if (payloadSize == -1 || payloadSize > seiBuffer.bytesLeft()) {
                    Log.w(TAG, "Skipping remainder of malformed SEI NAL unit.");
                    nextPayloadPosition = seiBuffer.limit();
                } else if (payloadType == 4 && payloadSize >= 8) {
                    int countryCode = seiBuffer.readUnsignedByte();
                    int providerCode = seiBuffer.readUnsignedShort();
                    int userIdentifier = 0;
                    if (providerCode == 49) {
                        userIdentifier = seiBuffer.readInt();
                    }
                    int userDataTypeCode = seiBuffer.readUnsignedByte();
                    if (providerCode == 47) {
                        seiBuffer.skipBytes(1);
                    }
                    boolean messageIsSupportedCeaCaption = countryCode == COUNTRY_CODE && (providerCode == 49 || providerCode == 47) && userDataTypeCode == 3;
                    if (providerCode == 49) {
                        if (userIdentifier != 1195456820) {
                            z = false;
                        }
                        messageIsSupportedCeaCaption &= z;
                    }
                    if (messageIsSupportedCeaCaption) {
                        consumeCcData(presentationTimeUs, seiBuffer, outputs);
                    }
                }
                seiBuffer.setPosition(nextPayloadPosition);
            } else {
                return;
            }
        }
    }

    public static void consumeCcData(long presentationTimeUs, ParsableByteArray ccDataBuffer, TrackOutput[] outputs) {
        int firstByte = ccDataBuffer.readUnsignedByte();
        boolean processCcDataFlag = (firstByte & 64) != 0;
        if (!processCcDataFlag) {
            return;
        }
        int ccCount = firstByte & 31;
        ccDataBuffer.skipBytes(1);
        int sampleLength = ccCount * 3;
        int sampleStartPosition = ccDataBuffer.getPosition();
        for (TrackOutput output : outputs) {
            ccDataBuffer.setPosition(sampleStartPosition);
            output.sampleData(ccDataBuffer, sampleLength);
            output.sampleMetadata(presentationTimeUs, 1, sampleLength, 0, null);
        }
    }

    private static int readNon255TerminatedValue(ParsableByteArray buffer) {
        int value = 0;
        while (buffer.bytesLeft() != 0) {
            int b = buffer.readUnsignedByte();
            value += b;
            if (b != 255) {
                return value;
            }
        }
        return -1;
    }

    private CeaUtil() {
    }
}
