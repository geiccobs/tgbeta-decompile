package org.telegram.ui.Components;

import android.text.TextPaint;
/* loaded from: classes5.dex */
public class URLSpanUserMentionPhotoViewer extends URLSpanUserMention {
    public URLSpanUserMentionPhotoViewer(String url, boolean isOutOwner) {
        super(url, 2);
    }

    @Override // org.telegram.ui.Components.URLSpanUserMention, org.telegram.ui.Components.URLSpanNoUnderline, android.text.style.ClickableSpan, android.text.style.CharacterStyle
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(-1);
        ds.setUnderlineText(false);
    }
}
