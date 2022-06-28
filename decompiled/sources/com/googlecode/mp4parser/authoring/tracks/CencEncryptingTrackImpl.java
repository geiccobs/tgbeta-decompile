package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReaderVariable;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.OriginalFormatBox;
import com.coremedia.iso.boxes.ProtectionSchemeInformationBox;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SchemeInformationBox;
import com.coremedia.iso.boxes.SchemeTypeBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.googlecode.mp4parser.MemoryDataSourceImpl;
import com.googlecode.mp4parser.authoring.Edit;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.cenc.CencEncryptingSampleList;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.CencSampleEncryptionInformationGroupEntry;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.RangeStartMap;
import com.mp4parser.iso14496.part15.AvcConfigurationBox;
import com.mp4parser.iso14496.part15.HevcConfigurationBox;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import com.mp4parser.iso23001.part7.TrackEncryptionBox;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.crypto.SecretKey;
/* loaded from: classes3.dex */
public class CencEncryptingTrackImpl implements CencEncryptedTrack {
    List<CencSampleAuxiliaryDataFormat> cencSampleAuxiliaryData;
    UUID defaultKeyId;
    boolean dummyIvs;
    private final String encryptionAlgo;
    RangeStartMap<Integer, SecretKey> indexToKey;
    Map<UUID, SecretKey> keys;
    Map<GroupEntry, long[]> sampleGroups;
    List<Sample> samples;
    Track source;
    SampleDescriptionBox stsd;
    boolean subSampleEncryption;

    public CencEncryptingTrackImpl(Track source, UUID defaultKeyId, SecretKey key, boolean dummyIvs) {
        this(source, defaultKeyId, Collections.singletonMap(defaultKeyId, key), null, C.CENC_TYPE_cenc, dummyIvs);
    }

    public CencEncryptingTrackImpl(Track source, UUID defaultKeyId, Map<UUID, SecretKey> keys, Map<CencSampleEncryptionInformationGroupEntry, long[]> keyRotation, String encryptionAlgo, boolean dummyIvs) {
        this(source, defaultKeyId, keys, keyRotation, encryptionAlgo, dummyIvs, false);
    }

    public CencEncryptingTrackImpl(Track source, UUID defaultKeyId, Map<UUID, SecretKey> keys, Map<CencSampleEncryptionInformationGroupEntry, long[]> keyRotation, String encryptionAlgo, boolean dummyIvs, boolean encryptButAllClear) {
        byte[] init;
        int clearBytes;
        boolean z;
        Object obj;
        UUID uuid;
        UUID uuid2 = defaultKeyId;
        boolean z2 = dummyIvs;
        this.keys = new HashMap();
        this.dummyIvs = false;
        this.subSampleEncryption = false;
        Object obj2 = null;
        this.stsd = null;
        this.source = source;
        this.keys = keys;
        this.defaultKeyId = uuid2;
        this.dummyIvs = z2;
        this.encryptionAlgo = encryptionAlgo;
        this.sampleGroups = new HashMap();
        for (Map.Entry<GroupEntry, long[]> entry : source.getSampleGroups().entrySet()) {
            UUID uuid3 = uuid2;
            Object obj3 = obj2;
            if (entry.getKey() instanceof CencSampleEncryptionInformationGroupEntry) {
                z2 = dummyIvs;
                uuid2 = uuid3;
                obj2 = obj3;
            } else {
                this.sampleGroups.put(entry.getKey(), entry.getValue());
                z2 = dummyIvs;
                uuid2 = uuid3;
                obj2 = obj3;
            }
        }
        if (keyRotation != null) {
            for (Map.Entry<CencSampleEncryptionInformationGroupEntry, long[]> entry2 : keyRotation.entrySet()) {
                this.sampleGroups.put(entry2.getKey(), entry2.getValue());
            }
        }
        this.sampleGroups = new HashMap<GroupEntry, long[]>(this.sampleGroups) { // from class: com.googlecode.mp4parser.authoring.tracks.CencEncryptingTrackImpl.1
            public long[] put(GroupEntry key, long[] value) {
                if (key instanceof CencSampleEncryptionInformationGroupEntry) {
                    throw new RuntimeException("Please supply CencSampleEncryptionInformationGroupEntries in the constructor");
                }
                return (long[]) super.put((AnonymousClass1) key, (GroupEntry) value);
            }
        };
        this.samples = source.getSamples();
        this.cencSampleAuxiliaryData = new ArrayList();
        BigInteger one = new BigInteger(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE);
        byte[] init2 = new byte[8];
        if (!z2) {
            Random random = new SecureRandom();
            random.nextBytes(init2);
        }
        BigInteger ivInt = new BigInteger(1, init2);
        List<CencSampleEncryptionInformationGroupEntry> groupEntries = new ArrayList<>();
        if (keyRotation != null) {
            groupEntries.addAll(keyRotation.keySet());
        }
        this.indexToKey = new RangeStartMap<>();
        int lastSampleGroupDescriptionIndex = -1;
        int lastSampleGroupDescriptionIndex2 = 0;
        while (lastSampleGroupDescriptionIndex2 < source.getSamples().size()) {
            byte[] init3 = init2;
            int index = 0;
            for (int j = 0; j < groupEntries.size(); j++) {
                CencSampleEncryptionInformationGroupEntry groupEntry = groupEntries.get(j);
                long[] sampleNums = getSampleGroups().get(groupEntry);
                if (Arrays.binarySearch(sampleNums, lastSampleGroupDescriptionIndex2) >= 0) {
                    index = j + 1;
                }
            }
            if (lastSampleGroupDescriptionIndex == index) {
                uuid = defaultKeyId;
                obj = null;
            } else {
                if (index == 0) {
                    uuid = defaultKeyId;
                    this.indexToKey.put((RangeStartMap<Integer, SecretKey>) Integer.valueOf(lastSampleGroupDescriptionIndex2), (Integer) keys.get(uuid));
                    obj = null;
                } else {
                    uuid = defaultKeyId;
                    if (groupEntries.get(index - 1).getKid() != null) {
                        SecretKey sk = keys.get(groupEntries.get(index - 1).getKid());
                        if (sk == null) {
                            throw new RuntimeException("Key " + groupEntries.get(index - 1).getKid() + " was not supplied for decryption");
                        }
                        this.indexToKey.put((RangeStartMap<Integer, SecretKey>) Integer.valueOf(lastSampleGroupDescriptionIndex2), (Integer) sk);
                        obj = null;
                    } else {
                        obj = null;
                        this.indexToKey.put((RangeStartMap<Integer, SecretKey>) Integer.valueOf(lastSampleGroupDescriptionIndex2), (Integer) null);
                    }
                }
                lastSampleGroupDescriptionIndex = index;
            }
            lastSampleGroupDescriptionIndex2++;
            init2 = init3;
        }
        List<Box> boxes = source.getSampleDescriptionBox().getSampleEntry().getBoxes();
        int nalLengthSize = -1;
        for (Box box : boxes) {
            List<Box> boxes2 = boxes;
            byte[] init4 = init2;
            if (!(box instanceof AvcConfigurationBox)) {
                z = true;
            } else {
                AvcConfigurationBox avcC = (AvcConfigurationBox) box;
                z = true;
                this.subSampleEncryption = true;
                nalLengthSize = avcC.getLengthSizeMinusOne() + 1;
            }
            if (box instanceof HevcConfigurationBox) {
                HevcConfigurationBox hvcC = (HevcConfigurationBox) box;
                this.subSampleEncryption = z;
                nalLengthSize = hvcC.getLengthSizeMinusOne() + 1;
                init2 = init4;
                boxes = boxes2;
            } else {
                init2 = init4;
                boxes = boxes2;
            }
        }
        int i = 0;
        while (i < this.samples.size()) {
            Sample origSample = this.samples.get(i);
            CencSampleAuxiliaryDataFormat e = new CencSampleAuxiliaryDataFormat();
            this.cencSampleAuxiliaryData.add(e);
            List<Box> boxes3 = boxes;
            if (this.indexToKey.get(Integer.valueOf(i)) == null) {
                init = init2;
            } else {
                byte[] iv = ivInt.toByteArray();
                byte[] eightByteIv = new byte[8];
                init = init2;
                System.arraycopy(iv, iv.length - 8 > 0 ? iv.length - 8 : 0, eightByteIv, 8 - iv.length < 0 ? 0 : 8 - iv.length, iv.length > 8 ? 8 : iv.length);
                e.iv = eightByteIv;
                ByteBuffer sample = (ByteBuffer) origSample.asByteBuffer().rewind();
                if (this.subSampleEncryption) {
                    if (encryptButAllClear) {
                        e.pairs = new CencSampleAuxiliaryDataFormat.Pair[]{e.createPair(sample.remaining(), 0L)};
                    } else {
                        List<CencSampleAuxiliaryDataFormat.Pair> pairs = new ArrayList<>(5);
                        while (sample.remaining() > 0) {
                            int nalLength = CastUtils.l2i(IsoTypeReaderVariable.read(sample, nalLengthSize));
                            int nalGrossSize = nalLength + nalLengthSize;
                            if (nalGrossSize >= 112) {
                                clearBytes = (nalGrossSize % 16) + 96;
                            } else {
                                clearBytes = nalGrossSize;
                            }
                            pairs.add(e.createPair(clearBytes, nalGrossSize - clearBytes));
                            sample.position(sample.position() + nalLength);
                        }
                        e.pairs = (CencSampleAuxiliaryDataFormat.Pair[]) pairs.toArray(new CencSampleAuxiliaryDataFormat.Pair[pairs.size()]);
                    }
                }
                ivInt = ivInt.add(one);
            }
            i++;
            init2 = init;
            boxes = boxes3;
        }
        System.err.println("");
    }

    @Override // com.googlecode.mp4parser.authoring.tracks.CencEncryptedTrack
    public UUID getDefaultKeyId() {
        return this.defaultKeyId;
    }

    @Override // com.googlecode.mp4parser.authoring.tracks.CencEncryptedTrack
    public boolean hasSubSampleEncryption() {
        return this.subSampleEncryption;
    }

    @Override // com.googlecode.mp4parser.authoring.tracks.CencEncryptedTrack
    public List<CencSampleAuxiliaryDataFormat> getSampleEncryptionEntries() {
        return this.cencSampleAuxiliaryData;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public synchronized SampleDescriptionBox getSampleDescriptionBox() {
        if (this.stsd == null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                this.source.getSampleDescriptionBox().getBox(Channels.newChannel(baos));
                int i = 0;
                this.stsd = (SampleDescriptionBox) new IsoFile(new MemoryDataSourceImpl(baos.toByteArray())).getBoxes().get(0);
                OriginalFormatBox originalFormatBox = new OriginalFormatBox();
                originalFormatBox.setDataFormat(this.stsd.getSampleEntry().getType());
                if (this.stsd.getSampleEntry() instanceof AudioSampleEntry) {
                    ((AudioSampleEntry) this.stsd.getSampleEntry()).setType(AudioSampleEntry.TYPE_ENCRYPTED);
                } else if (this.stsd.getSampleEntry() instanceof VisualSampleEntry) {
                    ((VisualSampleEntry) this.stsd.getSampleEntry()).setType(VisualSampleEntry.TYPE_ENCRYPTED);
                } else {
                    throw new RuntimeException("I don't know how to cenc " + this.stsd.getSampleEntry().getType());
                }
                ProtectionSchemeInformationBox sinf = new ProtectionSchemeInformationBox();
                sinf.addBox(originalFormatBox);
                SchemeTypeBox schm = new SchemeTypeBox();
                schm.setSchemeType(this.encryptionAlgo);
                schm.setSchemeVersion(65536);
                sinf.addBox(schm);
                SchemeInformationBox schi = new SchemeInformationBox();
                TrackEncryptionBox trackEncryptionBox = new TrackEncryptionBox();
                trackEncryptionBox.setDefaultIvSize(this.defaultKeyId == null ? 0 : 8);
                if (this.defaultKeyId != null) {
                    i = 1;
                }
                trackEncryptionBox.setDefaultAlgorithmId(i);
                UUID uuid = this.defaultKeyId;
                if (uuid == null) {
                    uuid = new UUID(0L, 0L);
                }
                trackEncryptionBox.setDefault_KID(uuid);
                schi.addBox(trackEncryptionBox);
                sinf.addBox(schi);
                this.stsd.getSampleEntry().addBox(sinf);
            } catch (IOException e) {
                throw new RuntimeException("Dumping stsd to memory failed");
            }
        }
        return this.stsd;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public long[] getSampleDurations() {
        return this.source.getSampleDurations();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public long getDuration() {
        return this.source.getDuration();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return this.source.getCompositionTimeEntries();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public long[] getSyncSamples() {
        return this.source.getSyncSamples();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return this.source.getSampleDependencies();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public TrackMetaData getTrackMetaData() {
        return this.source.getTrackMetaData();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public String getHandler() {
        return this.source.getHandler();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public List<Sample> getSamples() {
        return new CencEncryptingSampleList(this.indexToKey, this.source.getSamples(), this.cencSampleAuxiliaryData, this.encryptionAlgo);
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public SubSampleInformationBox getSubsampleInformationBox() {
        return this.source.getSubsampleInformationBox();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.source.close();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public String getName() {
        return "enc(" + this.source.getName() + ")";
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public List<Edit> getEdits() {
        return this.source.getEdits();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public Map<GroupEntry, long[]> getSampleGroups() {
        return this.sampleGroups;
    }
}
