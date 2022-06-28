package com.google.firebase.appindexing.builders;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class StickerPackBuilder extends IndexableBuilder<StickerPackBuilder> {
    public StickerPackBuilder() {
        super("StickerPack");
    }

    public StickerPackBuilder setHasSticker(StickerBuilder... stickers) {
        put("hasSticker", stickers);
        return this;
    }
}
