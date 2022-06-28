package com.googlecode.mp4parser.authoring.tracks.h265;

import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import java.io.IOException;
import java.io.PrintStream;
/* loaded from: classes3.dex */
public class SEIMessage {
    public SEIMessage(BitReaderBuffer bsr) throws IOException {
        int payloadType = 0;
        while (bsr.readBits(8) == 255) {
            payloadType += 255;
        }
        int last_payload_type_byte = bsr.readBits(8);
        int payloadType2 = payloadType + last_payload_type_byte;
        do {
        } while (bsr.readBits(8) == 255);
        bsr.readBits(8);
        PrintStream printStream = System.err;
        printStream.println("payloadType " + payloadType2);
    }
}
