package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractContainerBox;
import java.util.List;
/* loaded from: classes3.dex */
public class TrackBox extends AbstractContainerBox {
    public static final String TYPE = "trak";
    private SampleTableBox sampleTableBox;

    public TrackBox() {
        super(TYPE);
    }

    public TrackHeaderBox getTrackHeaderBox() {
        for (Box box : getBoxes()) {
            if (box instanceof TrackHeaderBox) {
                return (TrackHeaderBox) box;
            }
        }
        return null;
    }

    public SampleTableBox getSampleTableBox() {
        MediaInformationBox minf;
        SampleTableBox sampleTableBox = this.sampleTableBox;
        if (sampleTableBox != null) {
            return sampleTableBox;
        }
        MediaBox mdia = getMediaBox();
        if (mdia != null && (minf = mdia.getMediaInformationBox()) != null) {
            SampleTableBox sampleTableBox2 = minf.getSampleTableBox();
            this.sampleTableBox = sampleTableBox2;
            return sampleTableBox2;
        }
        return null;
    }

    public MediaBox getMediaBox() {
        for (Box box : getBoxes()) {
            if (box instanceof MediaBox) {
                return (MediaBox) box;
            }
        }
        return null;
    }

    @Override // com.googlecode.mp4parser.BasicContainer, com.coremedia.iso.boxes.Container
    public void setBoxes(List<Box> boxes) {
        super.setBoxes(boxes);
        this.sampleTableBox = null;
    }
}
