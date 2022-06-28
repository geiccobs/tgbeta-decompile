package com.google.android.exoplayer2.extractor.ts;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.ParsableByteArray;
/* loaded from: classes3.dex */
public final class TsUtil {
    public static int findSyncBytePosition(byte[] data, int startPosition, int limitPosition) {
        int position = startPosition;
        while (position < limitPosition && data[position] != 71) {
            position++;
        }
        return position;
    }

    public static long readPcrFromPacket(ParsableByteArray packetBuffer, int startOfPacket, int pcrPid) {
        packetBuffer.setPosition(startOfPacket);
        if (packetBuffer.bytesLeft() < 5) {
            return C.TIME_UNSET;
        }
        int tsPacketHeader = packetBuffer.readInt();
        if ((8388608 & tsPacketHeader) != 0) {
            return C.TIME_UNSET;
        }
        int pid = (2096896 & tsPacketHeader) >> 8;
        if (pid != pcrPid) {
            return C.TIME_UNSET;
        }
        boolean pcrFlagSet = true;
        boolean adaptationFieldExists = (tsPacketHeader & 32) != 0;
        if (!adaptationFieldExists) {
            return C.TIME_UNSET;
        }
        int adaptationFieldLength = packetBuffer.readUnsignedByte();
        if (adaptationFieldLength >= 7 && packetBuffer.bytesLeft() >= 7) {
            int flags = packetBuffer.readUnsignedByte();
            if ((flags & 16) != 16) {
                pcrFlagSet = false;
            }
            if (pcrFlagSet) {
                byte[] pcrBytes = new byte[6];
                packetBuffer.readBytes(pcrBytes, 0, pcrBytes.length);
                return readPcrValueFromPcrBytes(pcrBytes);
            }
        }
        return C.TIME_UNSET;
    }

    private static long readPcrValueFromPcrBytes(byte[] pcrBytes) {
        return ((pcrBytes[0] & 255) << 25) | ((pcrBytes[1] & 255) << 17) | ((pcrBytes[2] & 255) << 9) | ((pcrBytes[3] & 255) << 1) | ((255 & pcrBytes[4]) >> 7);
    }

    private TsUtil() {
    }
}
