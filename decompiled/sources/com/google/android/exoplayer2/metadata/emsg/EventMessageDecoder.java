package com.google.android.exoplayer2.metadata.emsg;

import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataDecoder;
import com.google.android.exoplayer2.metadata.MetadataInputBuffer;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.nio.ByteBuffer;
import java.util.Arrays;
/* loaded from: classes3.dex */
public final class EventMessageDecoder implements MetadataDecoder {
    @Override // com.google.android.exoplayer2.metadata.MetadataDecoder
    public Metadata decode(MetadataInputBuffer inputBuffer) {
        ByteBuffer buffer = (ByteBuffer) Assertions.checkNotNull(inputBuffer.data);
        byte[] data = buffer.array();
        int size = buffer.limit();
        return new Metadata(decode(new ParsableByteArray(data, size)));
    }

    public EventMessage decode(ParsableByteArray emsgData) {
        String schemeIdUri = (String) Assertions.checkNotNull(emsgData.readNullTerminatedString());
        String value = (String) Assertions.checkNotNull(emsgData.readNullTerminatedString());
        long durationMs = emsgData.readUnsignedInt();
        long id = emsgData.readUnsignedInt();
        byte[] messageData = Arrays.copyOfRange(emsgData.data, emsgData.getPosition(), emsgData.limit());
        return new EventMessage(schemeIdUri, value, durationMs, id, messageData);
    }
}
