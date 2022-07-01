package com.coremedia.iso.boxes.sampleentry;

import com.googlecode.mp4parser.AbstractContainerBox;
/* loaded from: classes.dex */
public abstract class AbstractSampleEntry extends AbstractContainerBox {
    protected int dataReferenceIndex = 1;

    public AbstractSampleEntry(String str) {
        super(str);
    }

    public void setDataReferenceIndex(int i) {
        this.dataReferenceIndex = i;
    }
}
