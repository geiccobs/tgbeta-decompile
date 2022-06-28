package org.telegram.ui.Components;

import android.net.Uri;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.Components.TextStyleSpan;
/* loaded from: classes5.dex */
public class URLSpanNoUnderline extends URLSpan {
    private boolean forceNoUnderline;
    public String label;
    private TLObject object;
    private TextStyleSpan.TextStyleRun style;

    public URLSpanNoUnderline(String url) {
        this(url, (TextStyleSpan.TextStyleRun) null);
    }

    public URLSpanNoUnderline(String url, boolean forceNoUnderline) {
        this(url, (TextStyleSpan.TextStyleRun) null);
        this.forceNoUnderline = forceNoUnderline;
    }

    public URLSpanNoUnderline(String url, TextStyleSpan.TextStyleRun run) {
        super(url != null ? url.replace((char) 8238, ' ') : url);
        this.forceNoUnderline = false;
        this.style = run;
    }

    @Override // android.text.style.URLSpan, android.text.style.ClickableSpan
    public void onClick(View widget) {
        String url = getURL();
        if (url.startsWith("@")) {
            Uri uri = Uri.parse("https://t.me/" + url.substring(1));
            Browser.openUrl(widget.getContext(), uri);
            return;
        }
        Browser.openUrl(widget.getContext(), url);
    }

    @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
    public void updateDrawState(TextPaint p) {
        int l = p.linkColor;
        int c = p.getColor();
        super.updateDrawState(p);
        TextStyleSpan.TextStyleRun textStyleRun = this.style;
        if (textStyleRun != null) {
            textStyleRun.applyStyle(p);
        }
        p.setUnderlineText(l == c && !this.forceNoUnderline);
    }

    public void setObject(TLObject spanObject) {
        this.object = spanObject;
    }

    public TLObject getObject() {
        return this.object;
    }
}
