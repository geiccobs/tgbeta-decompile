package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeWriterVariable;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.AbstractContainerBox;
import com.googlecode.mp4parser.MemoryDataSourceImpl;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.WrappingTrack;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.Path;
import com.mp4parser.iso14496.part15.AvcConfigurationBox;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes3.dex */
public class Avc1ToAvc3TrackImpl extends WrappingTrack {
    AvcConfigurationBox avcC;
    List<Sample> samples;
    SampleDescriptionBox stsd;

    public Avc1ToAvc3TrackImpl(Track parent) throws IOException {
        super(parent);
        if (!VisualSampleEntry.TYPE3.equals(parent.getSampleDescriptionBox().getSampleEntry().getType())) {
            throw new RuntimeException("Only avc1 tracks can be converted to avc3 tracks");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        parent.getSampleDescriptionBox().getBox(Channels.newChannel(baos));
        IsoFile isoFile = new IsoFile(new MemoryDataSourceImpl(baos.toByteArray()));
        SampleDescriptionBox sampleDescriptionBox = (SampleDescriptionBox) Path.getPath(isoFile, SampleDescriptionBox.TYPE);
        this.stsd = sampleDescriptionBox;
        ((VisualSampleEntry) sampleDescriptionBox.getSampleEntry()).setType(VisualSampleEntry.TYPE4);
        this.avcC = (AvcConfigurationBox) Path.getPath((AbstractContainerBox) this.stsd, "avc./avcC");
        this.samples = new ReplaceSyncSamplesList(parent.getSamples());
    }

    @Override // com.googlecode.mp4parser.authoring.WrappingTrack, com.googlecode.mp4parser.authoring.Track
    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.stsd;
    }

    @Override // com.googlecode.mp4parser.authoring.WrappingTrack, com.googlecode.mp4parser.authoring.Track
    public List<Sample> getSamples() {
        return this.samples;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ReplaceSyncSamplesList extends AbstractList<Sample> {
        List<Sample> parentSamples;

        public ReplaceSyncSamplesList(List<Sample> parentSamples) {
            Avc1ToAvc3TrackImpl.this = r1;
            this.parentSamples = parentSamples;
        }

        @Override // java.util.AbstractList, java.util.List
        public Sample get(int index) {
            if (Arrays.binarySearch(Avc1ToAvc3TrackImpl.this.getSyncSamples(), index + 1) >= 0) {
                final int len = Avc1ToAvc3TrackImpl.this.avcC.getLengthSizeMinusOne() + 1;
                final ByteBuffer buf = ByteBuffer.allocate(len);
                final Sample orignalSample = this.parentSamples.get(index);
                return new Sample() { // from class: com.googlecode.mp4parser.authoring.tracks.Avc1ToAvc3TrackImpl.ReplaceSyncSamplesList.1
                    @Override // com.googlecode.mp4parser.authoring.Sample
                    public void writeTo(WritableByteChannel channel) throws IOException {
                        for (byte[] bytes : Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSets()) {
                            IsoTypeWriterVariable.write(bytes.length, (ByteBuffer) buf.rewind(), len);
                            channel.write((ByteBuffer) buf.rewind());
                            channel.write(ByteBuffer.wrap(bytes));
                        }
                        for (byte[] bytes2 : Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSetExts()) {
                            IsoTypeWriterVariable.write(bytes2.length, (ByteBuffer) buf.rewind(), len);
                            channel.write((ByteBuffer) buf.rewind());
                            channel.write(ByteBuffer.wrap(bytes2));
                        }
                        for (byte[] bytes3 : Avc1ToAvc3TrackImpl.this.avcC.getPictureParameterSets()) {
                            IsoTypeWriterVariable.write(bytes3.length, (ByteBuffer) buf.rewind(), len);
                            channel.write((ByteBuffer) buf.rewind());
                            channel.write(ByteBuffer.wrap(bytes3));
                        }
                        orignalSample.writeTo(channel);
                    }

                    @Override // com.googlecode.mp4parser.authoring.Sample
                    public long getSize() {
                        int spsPpsSize = 0;
                        for (byte[] bytes : Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSets()) {
                            spsPpsSize += len + bytes.length;
                        }
                        for (byte[] bytes2 : Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSetExts()) {
                            spsPpsSize += len + bytes2.length;
                        }
                        for (byte[] bytes3 : Avc1ToAvc3TrackImpl.this.avcC.getPictureParameterSets()) {
                            spsPpsSize += len + bytes3.length;
                        }
                        return orignalSample.getSize() + spsPpsSize;
                    }

                    @Override // com.googlecode.mp4parser.authoring.Sample
                    public ByteBuffer asByteBuffer() {
                        int spsPpsSize = 0;
                        for (byte[] bytes : Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSets()) {
                            spsPpsSize += len + bytes.length;
                        }
                        for (byte[] bytes2 : Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSetExts()) {
                            spsPpsSize += len + bytes2.length;
                        }
                        for (byte[] bytes3 : Avc1ToAvc3TrackImpl.this.avcC.getPictureParameterSets()) {
                            spsPpsSize += len + bytes3.length;
                        }
                        ByteBuffer data = ByteBuffer.allocate(CastUtils.l2i(orignalSample.getSize()) + spsPpsSize);
                        for (byte[] bytes4 : Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSets()) {
                            IsoTypeWriterVariable.write(bytes4.length, data, len);
                            data.put(bytes4);
                        }
                        for (byte[] bytes5 : Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSetExts()) {
                            IsoTypeWriterVariable.write(bytes5.length, data, len);
                            data.put(bytes5);
                        }
                        for (byte[] bytes6 : Avc1ToAvc3TrackImpl.this.avcC.getPictureParameterSets()) {
                            IsoTypeWriterVariable.write(bytes6.length, data, len);
                            data.put(bytes6);
                        }
                        data.put(orignalSample.asByteBuffer());
                        return (ByteBuffer) data.rewind();
                    }
                };
            }
            return this.parentSamples.get(index);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.parentSamples.size();
        }
    }
}
