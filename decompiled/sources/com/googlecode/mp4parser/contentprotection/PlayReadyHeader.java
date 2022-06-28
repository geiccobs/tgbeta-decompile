package com.googlecode.mp4parser.contentprotection;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.google.android.exoplayer2.C;
import com.googlecode.mp4parser.boxes.piff.ProtectionSpecificHeader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
/* loaded from: classes3.dex */
public class PlayReadyHeader extends ProtectionSpecificHeader {
    public static UUID PROTECTION_SYSTEM_ID = UUID.fromString("9A04F079-9840-4286-AB92-E65BE0885F95");
    private long length;
    private List<PlayReadyRecord> records;

    static {
        ProtectionSpecificHeader.uuidRegistry.put(PROTECTION_SYSTEM_ID, PlayReadyHeader.class);
    }

    @Override // com.googlecode.mp4parser.boxes.piff.ProtectionSpecificHeader
    public UUID getSystemId() {
        return PROTECTION_SYSTEM_ID;
    }

    @Override // com.googlecode.mp4parser.boxes.piff.ProtectionSpecificHeader
    public void parse(ByteBuffer byteBuffer) {
        this.length = IsoTypeReader.readUInt32BE(byteBuffer);
        int recordCount = IsoTypeReader.readUInt16BE(byteBuffer);
        this.records = PlayReadyRecord.createFor(byteBuffer, recordCount);
    }

    @Override // com.googlecode.mp4parser.boxes.piff.ProtectionSpecificHeader
    public ByteBuffer getData() {
        int size = 6;
        for (PlayReadyRecord record : this.records) {
            size = size + 4 + record.getValue().rewind().limit();
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        IsoTypeWriter.writeUInt32BE(byteBuffer, size);
        IsoTypeWriter.writeUInt16BE(byteBuffer, this.records.size());
        for (PlayReadyRecord record2 : this.records) {
            IsoTypeWriter.writeUInt16BE(byteBuffer, record2.type);
            IsoTypeWriter.writeUInt16BE(byteBuffer, record2.getValue().limit());
            ByteBuffer tmp4debug = record2.getValue();
            byteBuffer.put(tmp4debug);
        }
        return byteBuffer;
    }

    public void setRecords(List<PlayReadyRecord> records) {
        this.records = records;
    }

    public List<PlayReadyRecord> getRecords() {
        return Collections.unmodifiableList(this.records);
    }

    @Override // com.googlecode.mp4parser.boxes.piff.ProtectionSpecificHeader
    public String toString() {
        return "PlayReadyHeader{length=" + this.length + ", recordCount=" + this.records.size() + ", records=" + this.records + '}';
    }

    /* loaded from: classes3.dex */
    public static abstract class PlayReadyRecord {
        int type;

        public abstract ByteBuffer getValue();

        public abstract void parse(ByteBuffer byteBuffer);

        public PlayReadyRecord(int type) {
            this.type = type;
        }

        public static List<PlayReadyRecord> createFor(ByteBuffer byteBuffer, int recordCount) {
            PlayReadyRecord record;
            List<PlayReadyRecord> records = new ArrayList<>(recordCount);
            for (int i = 0; i < recordCount; i++) {
                int type = IsoTypeReader.readUInt16BE(byteBuffer);
                int length = IsoTypeReader.readUInt16BE(byteBuffer);
                switch (type) {
                    case 1:
                        record = new RMHeader();
                        break;
                    case 2:
                        record = new DefaulPlayReadyRecord(2);
                        break;
                    case 3:
                        record = new EmeddedLicenseStore();
                        break;
                    default:
                        record = new DefaulPlayReadyRecord(type);
                        break;
                }
                record.parse((ByteBuffer) byteBuffer.slice().limit(length));
                byteBuffer.position(byteBuffer.position() + length);
                records.add(record);
            }
            return records;
        }

        public String toString() {
            return "PlayReadyRecord{type=" + this.type + ", length=" + getValue().limit() + '}';
        }

        /* loaded from: classes3.dex */
        public static class RMHeader extends PlayReadyRecord {
            String header;

            public RMHeader() {
                super(1);
            }

            @Override // com.googlecode.mp4parser.contentprotection.PlayReadyHeader.PlayReadyRecord
            public void parse(ByteBuffer bytes) {
                try {
                    byte[] str = new byte[bytes.slice().limit()];
                    bytes.get(str);
                    this.header = new String(str, C.UTF16LE_NAME);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override // com.googlecode.mp4parser.contentprotection.PlayReadyHeader.PlayReadyRecord
            public ByteBuffer getValue() {
                try {
                    byte[] headerBytes = this.header.getBytes(C.UTF16LE_NAME);
                    return ByteBuffer.wrap(headerBytes);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }

            public void setHeader(String header) {
                this.header = header;
            }

            public String getHeader() {
                return this.header;
            }

            @Override // com.googlecode.mp4parser.contentprotection.PlayReadyHeader.PlayReadyRecord
            public String toString() {
                return "RMHeader{length=" + getValue().limit() + ", header='" + this.header + "'}";
            }
        }

        /* loaded from: classes3.dex */
        public static class EmeddedLicenseStore extends PlayReadyRecord {
            ByteBuffer value;

            public EmeddedLicenseStore() {
                super(3);
            }

            @Override // com.googlecode.mp4parser.contentprotection.PlayReadyHeader.PlayReadyRecord
            public void parse(ByteBuffer bytes) {
                this.value = bytes.duplicate();
            }

            @Override // com.googlecode.mp4parser.contentprotection.PlayReadyHeader.PlayReadyRecord
            public ByteBuffer getValue() {
                return this.value;
            }

            @Override // com.googlecode.mp4parser.contentprotection.PlayReadyHeader.PlayReadyRecord
            public String toString() {
                return "EmeddedLicenseStore{length=" + getValue().limit() + '}';
            }
        }

        /* loaded from: classes3.dex */
        public static class DefaulPlayReadyRecord extends PlayReadyRecord {
            ByteBuffer value;

            public DefaulPlayReadyRecord(int type) {
                super(type);
            }

            @Override // com.googlecode.mp4parser.contentprotection.PlayReadyHeader.PlayReadyRecord
            public void parse(ByteBuffer bytes) {
                this.value = bytes.duplicate();
            }

            @Override // com.googlecode.mp4parser.contentprotection.PlayReadyHeader.PlayReadyRecord
            public ByteBuffer getValue() {
                return this.value;
            }
        }
    }
}
