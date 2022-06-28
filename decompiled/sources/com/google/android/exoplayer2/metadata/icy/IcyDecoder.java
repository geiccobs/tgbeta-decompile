package com.google.android.exoplayer2.metadata.icy;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataDecoder;
import com.google.android.exoplayer2.metadata.MetadataInputBuffer;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes3.dex */
public final class IcyDecoder implements MetadataDecoder {
    private static final Pattern METADATA_ELEMENT = Pattern.compile("(.+?)='(.*?)';", 32);
    private static final String STREAM_KEY_NAME = "streamtitle";
    private static final String STREAM_KEY_URL = "streamurl";
    private final CharsetDecoder utf8Decoder = Charset.forName("UTF-8").newDecoder();
    private final CharsetDecoder iso88591Decoder = Charset.forName(C.ISO88591_NAME).newDecoder();

    @Override // com.google.android.exoplayer2.metadata.MetadataDecoder
    public Metadata decode(MetadataInputBuffer inputBuffer) {
        ByteBuffer buffer = (ByteBuffer) Assertions.checkNotNull(inputBuffer.data);
        String icyString = decodeToString(buffer);
        byte[] icyBytes = new byte[buffer.limit()];
        buffer.get(icyBytes);
        if (icyString == null) {
            return new Metadata(new IcyInfo(icyBytes, null, null));
        }
        String name = null;
        String url = null;
        Matcher matcher = METADATA_ELEMENT.matcher(icyString);
        for (int index = 0; matcher.find(index); index = matcher.end()) {
            String key = Util.toLowerInvariant(matcher.group(1));
            String value = matcher.group(2);
            char c = 65535;
            switch (key.hashCode()) {
                case -315603473:
                    if (key.equals(STREAM_KEY_URL)) {
                        c = 1;
                        break;
                    }
                    break;
                case 1646559960:
                    if (key.equals(STREAM_KEY_NAME)) {
                        c = 0;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    name = value;
                    break;
                case 1:
                    url = value;
                    break;
            }
        }
        return new Metadata(new IcyInfo(icyBytes, name, url));
    }

    private String decodeToString(ByteBuffer data) {
        try {
            return this.utf8Decoder.decode(data).toString();
        } catch (CharacterCodingException e) {
            try {
                return this.iso88591Decoder.decode(data).toString();
            } catch (CharacterCodingException e2) {
                return null;
            } finally {
                this.iso88591Decoder.reset();
                data.rewind();
            }
        } finally {
            this.utf8Decoder.reset();
            data.rewind();
        }
    }
}
