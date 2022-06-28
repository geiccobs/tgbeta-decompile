package com.googlecode.mp4parser.authoring;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ChunkOffsetBox;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.SchemeTypeBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.fragment.MovieExtendsBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox;
import com.google.android.exoplayer2.C;
import com.googlecode.mp4parser.AbstractContainerBox;
import com.googlecode.mp4parser.authoring.tracks.CencEncryptedTrack;
import com.googlecode.mp4parser.util.Path;
import com.mp4parser.iso14496.part12.SampleAuxiliaryInformationOffsetsBox;
import com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import com.mp4parser.iso23001.part7.TrackEncryptionBox;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
/* loaded from: classes3.dex */
public class CencMp4TrackImplImpl extends Mp4TrackImpl implements CencEncryptedTrack {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private UUID defaultKeyId;
    private List<CencSampleAuxiliaryDataFormat> sampleEncryptionEntries;

    public CencMp4TrackImplImpl(String name, TrackBox trackBox, IsoFile... fragments) throws IOException {
        super(name, trackBox, fragments);
        SampleAuxiliaryInformationOffsetsBox saio;
        FindSaioSaizPair saizSaioPair;
        long size;
        long size2;
        int sizeInTotal;
        long baseOffset;
        Container base;
        List<TrackFragmentBox> trafs;
        TrackFragmentBox traf;
        Container base2;
        SchemeTypeBox schm = (SchemeTypeBox) Path.getPath((AbstractContainerBox) trackBox, "mdia[0]/minf[0]/stbl[0]/stsd[0]/enc.[0]/sinf[0]/schm[0]");
        if (schm != null && (schm.getSchemeType().equals(C.CENC_TYPE_cenc) || schm.getSchemeType().equals(C.CENC_TYPE_cbc1))) {
            this.sampleEncryptionEntries = new ArrayList();
            long trackId = trackBox.getTrackHeaderBox().getTrackId();
            if (trackBox.getParent().getBoxes(MovieExtendsBox.class).size() <= 0) {
                TrackEncryptionBox tenc = (TrackEncryptionBox) Path.getPath((AbstractContainerBox) trackBox, "mdia[0]/minf[0]/stbl[0]/stsd[0]/enc.[0]/sinf[0]/schi[0]/tenc[0]");
                this.defaultKeyId = tenc.getDefault_KID();
                ChunkOffsetBox chunkOffsetBox = (ChunkOffsetBox) Path.getPath((AbstractContainerBox) trackBox, "mdia[0]/minf[0]/stbl[0]/stco[0]");
                chunkOffsetBox = chunkOffsetBox == null ? (ChunkOffsetBox) Path.getPath((AbstractContainerBox) trackBox, "mdia[0]/minf[0]/stbl[0]/co64[0]") : chunkOffsetBox;
                long[] chunkSizes = trackBox.getSampleTableBox().getSampleToChunkBox().blowup(chunkOffsetBox.getChunkOffsets().length);
                FindSaioSaizPair saizSaioPair2 = new FindSaioSaizPair((Container) Path.getPath((AbstractContainerBox) trackBox, "mdia[0]/minf[0]/stbl[0]")).invoke();
                SampleAuxiliaryInformationOffsetsBox saio2 = saizSaioPair2.saio;
                SampleAuxiliaryInformationSizesBox saiz = saizSaioPair2.saiz;
                Container topLevel = ((MovieBox) trackBox.getParent()).getParent();
                if (saio2.getOffsets().length == 1) {
                    long offset = saio2.getOffsets()[0];
                    int sizeInTotal2 = 0;
                    if (saiz.getDefaultSampleInfoSize() > 0) {
                        int sizeInTotal3 = 0 + (saiz.getSampleCount() * saiz.getDefaultSampleInfoSize());
                        sizeInTotal = sizeInTotal3;
                    } else {
                        for (int i = 0; i < saiz.getSampleCount(); i++) {
                            sizeInTotal2 += saiz.getSampleInfoSizes()[i];
                        }
                        sizeInTotal = sizeInTotal2;
                    }
                    ByteBuffer chunksCencSampleAuxData = topLevel.getByteBuffer(offset, sizeInTotal);
                    int i2 = 0;
                    while (i2 < saiz.getSampleCount()) {
                        this.sampleEncryptionEntries.add(parseCencAuxDataFormat(tenc.getDefaultIvSize(), chunksCencSampleAuxData, saiz.getSize(i2)));
                        i2++;
                        offset = offset;
                    }
                    return;
                } else if (saio2.getOffsets().length != chunkSizes.length) {
                    throw new RuntimeException("Number of saio offsets must be either 1 or number of chunks");
                } else {
                    int currentSampleNo = 0;
                    int i3 = 0;
                    while (i3 < chunkSizes.length) {
                        long offset2 = saio2.getOffsets()[i3];
                        long size3 = 0;
                        if (saiz.getDefaultSampleInfoSize() > 0) {
                            saizSaioPair = saizSaioPair2;
                            saio = saio2;
                            size = 0 + (saiz.getSampleCount() * chunkSizes[i3]);
                        } else {
                            saizSaioPair = saizSaioPair2;
                            saio = saio2;
                            int j = 0;
                            while (true) {
                                size2 = size3;
                                long size4 = j;
                                if (size4 >= chunkSizes[i3]) {
                                    break;
                                }
                                size3 = size2 + saiz.getSize(currentSampleNo + j);
                                j++;
                                offset2 = offset2;
                                chunkOffsetBox = chunkOffsetBox;
                            }
                            size = size2;
                        }
                        ByteBuffer chunksCencSampleAuxData2 = topLevel.getByteBuffer(offset2, size);
                        int j2 = 0;
                        while (true) {
                            long offset3 = offset2;
                            if (j2 >= chunkSizes[i3]) {
                                break;
                            }
                            long auxInfoSize = saiz.getSize(currentSampleNo + j2);
                            this.sampleEncryptionEntries.add(parseCencAuxDataFormat(tenc.getDefaultIvSize(), chunksCencSampleAuxData2, auxInfoSize));
                            j2++;
                            offset2 = offset3;
                            chunkOffsetBox = chunkOffsetBox;
                        }
                        currentSampleNo = (int) (currentSampleNo + chunkSizes[i3]);
                        i3++;
                        saizSaioPair2 = saizSaioPair;
                        saio2 = saio;
                    }
                    return;
                }
            }
            Iterator it = ((Box) trackBox.getParent()).getParent().getBoxes(MovieFragmentBox.class).iterator();
            while (it.hasNext()) {
                MovieFragmentBox movieFragmentBox = (MovieFragmentBox) it.next();
                List<TrackFragmentBox> trafs2 = movieFragmentBox.getBoxes(TrackFragmentBox.class);
                Iterator<TrackFragmentBox> it2 = trafs2.iterator();
                while (it2.hasNext()) {
                    TrackFragmentBox traf2 = it2.next();
                    if (traf2.getTrackFragmentHeaderBox().getTrackId() == trackId) {
                        TrackEncryptionBox tenc2 = (TrackEncryptionBox) Path.getPath((AbstractContainerBox) trackBox, "mdia[0]/minf[0]/stbl[0]/stsd[0]/enc.[0]/sinf[0]/schi[0]/tenc[0]");
                        this.defaultKeyId = tenc2.getDefault_KID();
                        if (traf2.getTrackFragmentHeaderBox().hasBaseDataOffset()) {
                            base = ((Box) trackBox.getParent()).getParent();
                            baseOffset = traf2.getTrackFragmentHeaderBox().getBaseDataOffset();
                        } else {
                            base = movieFragmentBox;
                            baseOffset = 0;
                        }
                        FindSaioSaizPair saizSaioPair3 = new FindSaioSaizPair(traf2).invoke();
                        SampleAuxiliaryInformationOffsetsBox saio3 = saizSaioPair3.getSaio();
                        SchemeTypeBox schm2 = schm;
                        SampleAuxiliaryInformationSizesBox saiz2 = saizSaioPair3.getSaiz();
                        if (saio3 == null) {
                            throw new AssertionError();
                        }
                        long trackId2 = trackId;
                        long[] saioOffsets = saio3.getOffsets();
                        Iterator it3 = it;
                        if (saioOffsets.length != traf2.getBoxes(TrackRunBox.class).size()) {
                            throw new AssertionError();
                        }
                        if (saiz2 == null) {
                            throw new AssertionError();
                        }
                        List<TrackRunBox> truns = traf2.getBoxes(TrackRunBox.class);
                        int sampleNo = 0;
                        MovieFragmentBox movieFragmentBox2 = movieFragmentBox;
                        int i4 = 0;
                        while (true) {
                            trafs = trafs2;
                            if (i4 >= saioOffsets.length) {
                                break;
                            }
                            int numSamples = truns.get(i4).getEntries().size();
                            long offset4 = saioOffsets[i4];
                            long[] saioOffsets2 = saioOffsets;
                            List<TrackRunBox> truns2 = truns;
                            long length = 0;
                            Iterator<TrackFragmentBox> it4 = it2;
                            int j3 = sampleNo;
                            while (true) {
                                traf = traf2;
                                if (j3 >= sampleNo + numSamples) {
                                    break;
                                }
                                int sampleNo2 = sampleNo;
                                int sampleNo3 = saiz2.getSize(j3);
                                length += sampleNo3;
                                j3++;
                                traf2 = traf;
                                base = base;
                                sampleNo = sampleNo2;
                                baseOffset = baseOffset;
                            }
                            ByteBuffer trunsCencSampleAuxData = base.getByteBuffer(baseOffset + offset4, length);
                            int j4 = sampleNo;
                            while (true) {
                                base2 = base;
                                if (j4 >= sampleNo + numSamples) {
                                    break;
                                }
                                int auxInfoSize2 = saiz2.getSize(j4);
                                int sampleNo4 = sampleNo;
                                List<CencSampleAuxiliaryDataFormat> list = this.sampleEncryptionEntries;
                                int i5 = i4;
                                int i6 = tenc2.getDefaultIvSize();
                                long baseOffset2 = baseOffset;
                                long baseOffset3 = auxInfoSize2;
                                list.add(parseCencAuxDataFormat(i6, trunsCencSampleAuxData, baseOffset3));
                                j4++;
                                base = base2;
                                sampleNo = sampleNo4;
                                i4 = i5;
                                baseOffset = baseOffset2;
                            }
                            sampleNo += numSamples;
                            i4++;
                            trafs2 = trafs;
                            it2 = it4;
                            traf2 = traf;
                            base = base2;
                            saioOffsets = saioOffsets2;
                            truns = truns2;
                        }
                        schm = schm2;
                        trackId = trackId2;
                        it = it3;
                        movieFragmentBox = movieFragmentBox2;
                        trafs2 = trafs;
                    }
                }
            }
            return;
        }
        throw new AssertionError("Track must be CENC (cenc or cbc1) encrypted");
    }

    private CencSampleAuxiliaryDataFormat parseCencAuxDataFormat(int ivSize, ByteBuffer chunksCencSampleAuxData, long auxInfoSize) {
        CencSampleAuxiliaryDataFormat cadf = new CencSampleAuxiliaryDataFormat();
        if (auxInfoSize > 0) {
            cadf.iv = new byte[ivSize];
            chunksCencSampleAuxData.get(cadf.iv);
            if (auxInfoSize > ivSize) {
                int numOfPairs = IsoTypeReader.readUInt16(chunksCencSampleAuxData);
                cadf.pairs = new CencSampleAuxiliaryDataFormat.Pair[numOfPairs];
                for (int i = 0; i < cadf.pairs.length; i++) {
                    cadf.pairs[i] = cadf.createPair(IsoTypeReader.readUInt16(chunksCencSampleAuxData), IsoTypeReader.readUInt32(chunksCencSampleAuxData));
                }
            }
        }
        return cadf;
    }

    @Override // com.googlecode.mp4parser.authoring.tracks.CencEncryptedTrack
    public UUID getDefaultKeyId() {
        return this.defaultKeyId;
    }

    @Override // com.googlecode.mp4parser.authoring.tracks.CencEncryptedTrack
    public boolean hasSubSampleEncryption() {
        return false;
    }

    @Override // com.googlecode.mp4parser.authoring.tracks.CencEncryptedTrack
    public List<CencSampleAuxiliaryDataFormat> getSampleEncryptionEntries() {
        return this.sampleEncryptionEntries;
    }

    public String toString() {
        return "CencMp4TrackImpl{handler='" + getHandler() + "'}";
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public String getName() {
        return "enc(" + super.getName() + ")";
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class FindSaioSaizPair {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private Container container;
        private SampleAuxiliaryInformationOffsetsBox saio;
        private SampleAuxiliaryInformationSizesBox saiz;

        public FindSaioSaizPair(Container container) {
            CencMp4TrackImplImpl.this = r1;
            this.container = container;
        }

        public SampleAuxiliaryInformationSizesBox getSaiz() {
            return this.saiz;
        }

        public SampleAuxiliaryInformationOffsetsBox getSaio() {
            return this.saio;
        }

        public FindSaioSaizPair invoke() {
            List<SampleAuxiliaryInformationSizesBox> saizs = this.container.getBoxes(SampleAuxiliaryInformationSizesBox.class);
            List<SampleAuxiliaryInformationOffsetsBox> saios = this.container.getBoxes(SampleAuxiliaryInformationOffsetsBox.class);
            if (saizs.size() != saios.size()) {
                throw new AssertionError();
            }
            this.saiz = null;
            this.saio = null;
            for (int i = 0; i < saizs.size(); i++) {
                if (!(this.saiz == null && saizs.get(i).getAuxInfoType() == null) && !C.CENC_TYPE_cenc.equals(saizs.get(i).getAuxInfoType())) {
                    SampleAuxiliaryInformationSizesBox sampleAuxiliaryInformationSizesBox = this.saiz;
                    if (sampleAuxiliaryInformationSizesBox != null && sampleAuxiliaryInformationSizesBox.getAuxInfoType() == null && C.CENC_TYPE_cenc.equals(saizs.get(i).getAuxInfoType())) {
                        this.saiz = saizs.get(i);
                    } else {
                        throw new RuntimeException("Are there two cenc labeled saiz?");
                    }
                } else {
                    this.saiz = saizs.get(i);
                }
                if (!(this.saio == null && saios.get(i).getAuxInfoType() == null) && !C.CENC_TYPE_cenc.equals(saios.get(i).getAuxInfoType())) {
                    SampleAuxiliaryInformationOffsetsBox sampleAuxiliaryInformationOffsetsBox = this.saio;
                    if (sampleAuxiliaryInformationOffsetsBox != null && sampleAuxiliaryInformationOffsetsBox.getAuxInfoType() == null && C.CENC_TYPE_cenc.equals(saios.get(i).getAuxInfoType())) {
                        this.saio = saios.get(i);
                    } else {
                        throw new RuntimeException("Are there two cenc labeled saio?");
                    }
                } else {
                    this.saio = saios.get(i);
                }
            }
            return this;
        }
    }
}
