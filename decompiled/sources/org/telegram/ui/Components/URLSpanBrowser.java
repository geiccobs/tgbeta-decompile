package org.telegram.ui.Components;

import android.net.Uri;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.Components.TextStyleSpan;
/* loaded from: classes5.dex */
public class URLSpanBrowser extends URLSpan {
    private TextStyleSpan.TextStyleRun style;

    public URLSpanBrowser(String url) {
        this(url, null);
    }

    public URLSpanBrowser(String url, TextStyleSpan.TextStyleRun run) {
        super(url != null ? url.replace((char) 8238, ' ') : url);
        this.style = run;
    }

    public TextStyleSpan.TextStyleRun getStyle() {
        return this.style;
    }

    @Override // android.text.style.URLSpan, android.text.style.ClickableSpan
    public void onClick(View widget) {
        Uri uri = Uri.parse(getURL());
        Browser.openUrl(widget.getContext(), uri);
    }

    @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
    public void updateDrawState(TextPaint p) {
        super.updateDrawState(p);
        TextStyleSpan.TextStyleRun textStyleRun = this.style;
        if (textStyleRun != null) {
            textStyleRun.applyStyle(p);
        }
        p.setUnderlineText(true);
    }
}
