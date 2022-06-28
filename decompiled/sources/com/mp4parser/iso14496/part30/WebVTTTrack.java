package com.mp4parser.iso14496.part30;

import com.coremedia.iso.Utf8;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.util.CastUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
public class WebVTTTrack extends AbstractTrack {
    WebVTTSampleEntry sampleEntry;
    List<Sample> samples = new ArrayList();
    String[] subs;

    public WebVTTTrack(DataSource dataSource) throws IOException {
        super(dataSource.toString());
        WebVTTSampleEntry webVTTSampleEntry = new WebVTTSampleEntry();
        this.sampleEntry = webVTTSampleEntry;
        webVTTSampleEntry.addBox(new WebVTTConfigurationBox());
        this.sampleEntry.addBox(new WebVTTSourceLabelBox());
        ByteBuffer bb = dataSource.map(0L, CastUtils.l2i(dataSource.size()));
        byte[] content = new byte[CastUtils.l2i(dataSource.size())];
        bb.get(content);
        this.subs = Utf8.convert(content).split("\\r?\\n");
        String config = "";
        int i = 0;
        while (i < this.subs.length) {
            config = String.valueOf(config) + this.subs[i] + "\n";
            if (this.subs[i + 1].isEmpty() && this.subs[i + 2].isEmpty()) {
                break;
            }
            i++;
        }
        while (true) {
            String[] strArr = this.subs;
            if (i < strArr.length && strArr[i].isEmpty()) {
                i++;
            } else {
                return;
            }
        }
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public SampleDescriptionBox getSampleDescriptionBox() {
        return null;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public long[] getSampleDurations() {
        return new long[0];
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public TrackMetaData getTrackMetaData() {
        return null;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public String getHandler() {
        return null;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public List<Sample> getSamples() {
        return null;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
    }
}
