package org.telegram.ui.Components;

import android.net.Uri;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.LaunchActivity;
/* loaded from: classes5.dex */
public class URLSpanReplacement extends URLSpan {
    private boolean navigateToPremiumBot;
    private TextStyleSpan.TextStyleRun style;

    public URLSpanReplacement(String url) {
        this(url, null);
    }

    public URLSpanReplacement(String url, TextStyleSpan.TextStyleRun run) {
        super(url != null ? url.replace((char) 8238, ' ') : url);
        this.style = run;
    }

    public void setNavigateToPremiumBot(boolean navigateToPremiumBot) {
        this.navigateToPremiumBot = navigateToPremiumBot;
    }

    public TextStyleSpan.TextStyleRun getTextStyleRun() {
        return this.style;
    }

    @Override // android.text.style.URLSpan, android.text.style.ClickableSpan
    public void onClick(View widget) {
        if (this.navigateToPremiumBot && (widget.getContext() instanceof LaunchActivity)) {
            ((LaunchActivity) widget.getContext()).setNavigateToPremiumBot(true);
        }
        Uri uri = Uri.parse(getURL());
        Browser.openUrl(widget.getContext(), uri);
    }

    @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
    public void updateDrawState(TextPaint p) {
        int color = p.getColor();
        super.updateDrawState(p);
        TextStyleSpan.TextStyleRun textStyleRun = this.style;
        if (textStyleRun != null) {
            textStyleRun.applyStyle(p);
            p.setUnderlineText(p.linkColor == color);
        }
    }
}
