package com.google.android.exoplayer2.text.ttml;
/* loaded from: classes3.dex */
final class TtmlRegion {
    public final float height;
    public final String id;
    public final float line;
    public final int lineAnchor;
    public final int lineType;
    public final float position;
    public final float textSize;
    public final int textSizeType;
    public final float width;

    public TtmlRegion(String id) {
        this(id, -3.4028235E38f, -3.4028235E38f, Integer.MIN_VALUE, Integer.MIN_VALUE, -3.4028235E38f, -3.4028235E38f, Integer.MIN_VALUE, -3.4028235E38f);
    }

    public TtmlRegion(String id, float position, float line, int lineType, int lineAnchor, float width, float height, int textSizeType, float textSize) {
        this.id = id;
        this.position = position;
        this.line = line;
        this.lineType = lineType;
        this.lineAnchor = lineAnchor;
        this.width = width;
        this.height = height;
        this.textSizeType = textSizeType;
        this.textSize = textSize;
    }
}
