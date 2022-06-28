package org.telegram.messenger.video;

import android.media.MediaCodec;
import android.media.MediaFormat;
import com.coremedia.iso.boxes.AbstractMediaHeaderBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SoundMediaHeaderBox;
import com.coremedia.iso.boxes.VideoMediaHeaderBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.location.LocationRequest;
import com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.AudioSpecificConfig;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.DecoderConfigDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ESDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.SLConfigDescriptor;
import com.mp4parser.iso14496.part15.AvcConfigurationBox;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
/* loaded from: classes4.dex */
public class Track {
    private static Map<Integer, Integer> samplingFrequencyIndexMap;
    private String handler;
    private AbstractMediaHeaderBox headerBox;
    private int height;
    private boolean isAudio;
    private int[] sampleCompositions;
    private SampleDescriptionBox sampleDescriptionBox;
    private long[] sampleDurations;
    private LinkedList<Integer> syncSamples;
    private int timeScale;
    private long trackId;
    private float volume;
    private int width;
    private ArrayList<Sample> samples = new ArrayList<>();
    private long duration = 0;
    private Date creationTime = new Date();
    private ArrayList<SamplePresentationTime> samplePresentationTimes = new ArrayList<>();
    private boolean first = true;

    /* loaded from: classes4.dex */
    public static class SamplePresentationTime {
        private long dt;
        private int index;
        private long presentationTime;

        public SamplePresentationTime(int idx, long time) {
            this.index = idx;
            this.presentationTime = time;
        }
    }

    static {
        HashMap hashMap = new HashMap();
        samplingFrequencyIndexMap = hashMap;
        hashMap.put(96000, 0);
        samplingFrequencyIndexMap.put(88200, 1);
        samplingFrequencyIndexMap.put(64000, 2);
        samplingFrequencyIndexMap.put(48000, 3);
        samplingFrequencyIndexMap.put(44100, 4);
        samplingFrequencyIndexMap.put(32000, 5);
        samplingFrequencyIndexMap.put(24000, 6);
        samplingFrequencyIndexMap.put(22050, 7);
        samplingFrequencyIndexMap.put(16000, 8);
        samplingFrequencyIndexMap.put(12000, 9);
        samplingFrequencyIndexMap.put(11025, 10);
        samplingFrequencyIndexMap.put(8000, 11);
    }

    public Track(int id, MediaFormat format, boolean audio) {
        String mime;
        this.syncSamples = null;
        this.volume = 0.0f;
        this.trackId = id;
        this.isAudio = audio;
        if (!audio) {
            this.width = format.getInteger("width");
            this.height = format.getInteger("height");
            this.timeScale = 90000;
            this.syncSamples = new LinkedList<>();
            this.handler = "vide";
            this.headerBox = new VideoMediaHeaderBox();
            this.sampleDescriptionBox = new SampleDescriptionBox();
            String mime2 = format.getString("mime");
            if (mime2.equals("video/avc")) {
                VisualSampleEntry visualSampleEntry = new VisualSampleEntry(VisualSampleEntry.TYPE3);
                visualSampleEntry.setDataReferenceIndex(1);
                visualSampleEntry.setDepth(24);
                visualSampleEntry.setFrameCount(1);
                visualSampleEntry.setHorizresolution(72.0d);
                visualSampleEntry.setVertresolution(72.0d);
                visualSampleEntry.setWidth(this.width);
                visualSampleEntry.setHeight(this.height);
                AvcConfigurationBox avcConfigurationBox = new AvcConfigurationBox();
                if (format.getByteBuffer("csd-0") != null) {
                    ArrayList<byte[]> spsArray = new ArrayList<>();
                    ByteBuffer spsBuff = format.getByteBuffer("csd-0");
                    spsBuff.position(4);
                    byte[] spsBytes = new byte[spsBuff.remaining()];
                    spsBuff.get(spsBytes);
                    spsArray.add(spsBytes);
                    ArrayList<byte[]> ppsArray = new ArrayList<>();
                    ByteBuffer ppsBuff = format.getByteBuffer("csd-1");
                    ppsBuff.position(4);
                    byte[] ppsBytes = new byte[ppsBuff.remaining()];
                    ppsBuff.get(ppsBytes);
                    ppsArray.add(ppsBytes);
                    avcConfigurationBox.setSequenceParameterSets(spsArray);
                    avcConfigurationBox.setPictureParameterSets(ppsArray);
                }
                if (format.containsKey("level")) {
                    int level = format.getInteger("level");
                    if (level == 1) {
                        avcConfigurationBox.setAvcLevelIndication(1);
                    } else if (level == 32) {
                        avcConfigurationBox.setAvcLevelIndication(2);
                    } else if (level == 4) {
                        avcConfigurationBox.setAvcLevelIndication(11);
                    } else if (level == 8) {
                        avcConfigurationBox.setAvcLevelIndication(12);
                    } else if (level == 16) {
                        avcConfigurationBox.setAvcLevelIndication(13);
                    } else if (level == 64) {
                        avcConfigurationBox.setAvcLevelIndication(21);
                    } else if (level == 128) {
                        avcConfigurationBox.setAvcLevelIndication(22);
                    } else if (level == 256) {
                        avcConfigurationBox.setAvcLevelIndication(3);
                    } else if (level == 512) {
                        avcConfigurationBox.setAvcLevelIndication(31);
                    } else if (level == 1024) {
                        avcConfigurationBox.setAvcLevelIndication(32);
                    } else if (level == 2048) {
                        avcConfigurationBox.setAvcLevelIndication(4);
                    } else if (level == 4096) {
                        avcConfigurationBox.setAvcLevelIndication(41);
                    } else if (level == 8192) {
                        avcConfigurationBox.setAvcLevelIndication(42);
                    } else if (level == 16384) {
                        avcConfigurationBox.setAvcLevelIndication(5);
                    } else if (level == 32768) {
                        avcConfigurationBox.setAvcLevelIndication(51);
                    } else if (level == 65536) {
                        avcConfigurationBox.setAvcLevelIndication(52);
                    } else if (level == 2) {
                        avcConfigurationBox.setAvcLevelIndication(27);
                    }
                } else {
                    avcConfigurationBox.setAvcLevelIndication(13);
                }
                if (format.containsKey(Scopes.PROFILE)) {
                    int profile = format.getInteger(Scopes.PROFILE);
                    if (profile == 1) {
                        avcConfigurationBox.setAvcProfileIndication(66);
                    } else if (profile == 2) {
                        avcConfigurationBox.setAvcProfileIndication(77);
                    } else if (profile == 4) {
                        avcConfigurationBox.setAvcProfileIndication(88);
                    } else if (profile == 8) {
                        avcConfigurationBox.setAvcProfileIndication(100);
                    } else if (profile == 16) {
                        avcConfigurationBox.setAvcProfileIndication(110);
                    } else if (profile == 32) {
                        avcConfigurationBox.setAvcProfileIndication(122);
                    } else if (profile == 64) {
                        avcConfigurationBox.setAvcProfileIndication(244);
                    }
                } else {
                    avcConfigurationBox.setAvcProfileIndication(100);
                }
                avcConfigurationBox.setBitDepthLumaMinus8(-1);
                avcConfigurationBox.setBitDepthChromaMinus8(-1);
                avcConfigurationBox.setChromaFormat(-1);
                avcConfigurationBox.setConfigurationVersion(1);
                avcConfigurationBox.setLengthSizeMinusOne(3);
                avcConfigurationBox.setProfileCompatibility(0);
                visualSampleEntry.addBox(avcConfigurationBox);
                this.sampleDescriptionBox.addBox(visualSampleEntry);
                return;
            } else if (mime2.equals("video/mp4v")) {
                VisualSampleEntry visualSampleEntry2 = new VisualSampleEntry(VisualSampleEntry.TYPE1);
                visualSampleEntry2.setDataReferenceIndex(1);
                visualSampleEntry2.setDepth(24);
                visualSampleEntry2.setFrameCount(1);
                visualSampleEntry2.setHorizresolution(72.0d);
                visualSampleEntry2.setVertresolution(72.0d);
                visualSampleEntry2.setWidth(this.width);
                visualSampleEntry2.setHeight(this.height);
                this.sampleDescriptionBox.addBox(visualSampleEntry2);
                return;
            } else {
                return;
            }
        }
        this.volume = 1.0f;
        this.timeScale = format.getInteger("sample-rate");
        this.handler = "soun";
        this.headerBox = new SoundMediaHeaderBox();
        this.sampleDescriptionBox = new SampleDescriptionBox();
        AudioSampleEntry audioSampleEntry = new AudioSampleEntry(AudioSampleEntry.TYPE3);
        audioSampleEntry.setChannelCount(format.getInteger("channel-count"));
        audioSampleEntry.setSampleRate(format.getInteger("sample-rate"));
        audioSampleEntry.setDataReferenceIndex(1);
        audioSampleEntry.setSampleSize(16);
        ESDescriptorBox esds = new ESDescriptorBox();
        ESDescriptor descriptor = new ESDescriptor();
        descriptor.setEsId(0);
        SLConfigDescriptor slConfigDescriptor = new SLConfigDescriptor();
        slConfigDescriptor.setPredefined(2);
        descriptor.setSlConfigDescriptor(slConfigDescriptor);
        if (format.containsKey("mime")) {
            mime = format.getString("mime");
        } else {
            mime = "audio/mp4-latm";
        }
        DecoderConfigDescriptor decoderConfigDescriptor = new DecoderConfigDescriptor();
        if (MimeTypes.AUDIO_MPEG.equals(mime)) {
            decoderConfigDescriptor.setObjectTypeIndication(LocationRequest.PRIORITY_NO_POWER);
        } else {
            decoderConfigDescriptor.setObjectTypeIndication(64);
        }
        decoderConfigDescriptor.setStreamType(5);
        decoderConfigDescriptor.setBufferSizeDB(1536);
        if (format.containsKey("max-bitrate")) {
            decoderConfigDescriptor.setMaxBitRate(format.getInteger("max-bitrate"));
        } else {
            decoderConfigDescriptor.setMaxBitRate(96000L);
        }
        decoderConfigDescriptor.setAvgBitRate(this.timeScale);
        AudioSpecificConfig audioSpecificConfig = new AudioSpecificConfig();
        audioSpecificConfig.setAudioObjectType(2);
        audioSpecificConfig.setSamplingFrequencyIndex(samplingFrequencyIndexMap.get(Integer.valueOf((int) audioSampleEntry.getSampleRate())).intValue());
        audioSpecificConfig.setChannelConfiguration(audioSampleEntry.getChannelCount());
        decoderConfigDescriptor.setAudioSpecificInfo(audioSpecificConfig);
        descriptor.setDecoderConfigDescriptor(decoderConfigDescriptor);
        ByteBuffer data = descriptor.serialize();
        esds.setEsDescriptor(descriptor);
        esds.setData(data);
        audioSampleEntry.addBox(esds);
        this.sampleDescriptionBox.addBox(audioSampleEntry);
    }

    public long getTrackId() {
        return this.trackId;
    }

    public void addSample(long offset, MediaCodec.BufferInfo bufferInfo) {
        boolean z = true;
        if (this.isAudio || (bufferInfo.flags & 1) == 0) {
            z = false;
        }
        boolean isSyncFrame = z;
        this.samples.add(new Sample(offset, bufferInfo.size));
        LinkedList<Integer> linkedList = this.syncSamples;
        if (linkedList != null && isSyncFrame) {
            linkedList.add(Integer.valueOf(this.samples.size()));
        }
        this.samplePresentationTimes.add(new SamplePresentationTime(this.samplePresentationTimes.size(), ((bufferInfo.presentationTimeUs * this.timeScale) + 500000) / 1000000));
    }

    public void prepare() {
        ArrayList<SamplePresentationTime> original = new ArrayList<>(this.samplePresentationTimes);
        Collections.sort(this.samplePresentationTimes, Track$$ExternalSyntheticLambda0.INSTANCE);
        long lastPresentationTimeUs = 0;
        this.sampleDurations = new long[this.samplePresentationTimes.size()];
        long minDelta = Long.MAX_VALUE;
        boolean outOfOrder = false;
        for (int a = 0; a < this.samplePresentationTimes.size(); a++) {
            SamplePresentationTime presentationTime = this.samplePresentationTimes.get(a);
            long delta = presentationTime.presentationTime - lastPresentationTimeUs;
            lastPresentationTimeUs = presentationTime.presentationTime;
            this.sampleDurations[presentationTime.index] = delta;
            if (presentationTime.index != 0) {
                this.duration += delta;
            }
            if (delta > 0 && delta < 2147483647L) {
                minDelta = Math.min(minDelta, delta);
            }
            if (presentationTime.index != a) {
                outOfOrder = true;
            }
        }
        long[] jArr = this.sampleDurations;
        if (jArr.length > 0) {
            jArr[0] = minDelta;
            this.duration += minDelta;
        }
        for (int a2 = 1; a2 < original.size(); a2++) {
            original.get(a2).dt = this.sampleDurations[a2] + original.get(a2 - 1).dt;
        }
        if (outOfOrder) {
            this.sampleCompositions = new int[this.samplePresentationTimes.size()];
            for (int a3 = 0; a3 < this.samplePresentationTimes.size(); a3++) {
                SamplePresentationTime presentationTime2 = this.samplePresentationTimes.get(a3);
                this.sampleCompositions[presentationTime2.index] = (int) (presentationTime2.presentationTime - presentationTime2.dt);
            }
        }
    }

    public static /* synthetic */ int lambda$prepare$0(SamplePresentationTime o1, SamplePresentationTime o2) {
        if (o1.presentationTime <= o2.presentationTime) {
            if (o1.presentationTime < o2.presentationTime) {
                return -1;
            }
            return 0;
        }
        return 1;
    }

    public ArrayList<Sample> getSamples() {
        return this.samples;
    }

    public long getLastFrameTimestamp() {
        long j = this.duration;
        long[] jArr = this.sampleDurations;
        return (((j - jArr[jArr.length - 1]) * 1000000) - 500000) / this.timeScale;
    }

    public long getDuration() {
        return this.duration;
    }

    public String getHandler() {
        return this.handler;
    }

    public AbstractMediaHeaderBox getMediaHeaderBox() {
        return this.headerBox;
    }

    public int[] getSampleCompositions() {
        return this.sampleCompositions;
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.sampleDescriptionBox;
    }

    public long[] getSyncSamples() {
        LinkedList<Integer> linkedList = this.syncSamples;
        if (linkedList == null || linkedList.isEmpty()) {
            return null;
        }
        long[] returns = new long[this.syncSamples.size()];
        for (int i = 0; i < this.syncSamples.size(); i++) {
            returns[i] = this.syncSamples.get(i).intValue();
        }
        return returns;
    }

    public int getTimeScale() {
        return this.timeScale;
    }

    public Date getCreationTime() {
        return this.creationTime;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public float getVolume() {
        return this.volume;
    }

    public long[] getSampleDurations() {
        return this.sampleDurations;
    }

    public boolean isAudio() {
        return this.isAudio;
    }
}
