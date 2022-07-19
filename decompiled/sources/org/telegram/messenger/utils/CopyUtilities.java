package org.telegram.messenger.utils;

import android.graphics.Paint;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaDataController;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$TL_messageEntityBold;
import org.telegram.tgnet.TLRPC$TL_messageEntityCustomEmoji;
import org.telegram.tgnet.TLRPC$TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC$TL_messageEntitySpoiler;
import org.telegram.tgnet.TLRPC$TL_messageEntityStrike;
import org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC$TL_messageEntityUnderline;
import org.telegram.tgnet.TLRPC$TL_messageEntityUrl;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.xml.sax.XMLReader;
/* loaded from: classes.dex */
public class CopyUtilities {
    private static Pattern animatedEmojiTagPattern;

    public static Spannable fromHTML(String str) {
        Spanned spanned;
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                spanned = Html.fromHtml(str, 63, null, new HTMLTagHandler());
            } else {
                spanned = Html.fromHtml(str, null, new HTMLTagHandler());
            }
            if (spanned == null) {
                return null;
            }
            Object[] spans = spanned.getSpans(0, spanned.length(), Object.class);
            ArrayList arrayList = new ArrayList(spans.length);
            for (Object obj : spans) {
                int spanStart = spanned.getSpanStart(obj);
                int spanEnd = spanned.getSpanEnd(obj);
                if (obj instanceof StyleSpan) {
                    int style = ((StyleSpan) obj).getStyle();
                    if ((style & 1) > 0) {
                        arrayList.add(setEntityStartEnd(new TLRPC$TL_messageEntityBold(), spanStart, spanEnd));
                    }
                    if ((style & 2) > 0) {
                        arrayList.add(setEntityStartEnd(new TLRPC$TL_messageEntityItalic(), spanStart, spanEnd));
                    }
                } else if (obj instanceof UnderlineSpan) {
                    arrayList.add(setEntityStartEnd(new TLRPC$TL_messageEntityUnderline(), spanStart, spanEnd));
                } else if (obj instanceof StrikethroughSpan) {
                    arrayList.add(setEntityStartEnd(new TLRPC$TL_messageEntityStrike(), spanStart, spanEnd));
                } else if (obj instanceof ParsedSpoilerSpan) {
                    arrayList.add(setEntityStartEnd(new TLRPC$TL_messageEntitySpoiler(), spanStart, spanEnd));
                } else if (obj instanceof URLSpan) {
                    String charSequence = spanned.subSequence(spanStart, spanEnd).toString();
                    String url = ((URLSpan) obj).getURL();
                    if (charSequence.equals(url)) {
                        arrayList.add(setEntityStartEnd(new TLRPC$TL_messageEntityUrl(), spanStart, spanEnd));
                    } else {
                        TLRPC$TL_messageEntityTextUrl tLRPC$TL_messageEntityTextUrl = new TLRPC$TL_messageEntityTextUrl();
                        tLRPC$TL_messageEntityTextUrl.url = url;
                        arrayList.add(setEntityStartEnd(tLRPC$TL_messageEntityTextUrl, spanStart, spanEnd));
                    }
                } else if (obj instanceof AnimatedEmojiSpan) {
                    TLRPC$TL_messageEntityCustomEmoji tLRPC$TL_messageEntityCustomEmoji = new TLRPC$TL_messageEntityCustomEmoji();
                    tLRPC$TL_messageEntityCustomEmoji.document_id = ((AnimatedEmojiSpan) obj).documentId;
                    arrayList.add(setEntityStartEnd(tLRPC$TL_messageEntityCustomEmoji, spanStart, spanEnd));
                }
            }
            SpannableString spannableString = new SpannableString(spanned.toString());
            MediaDataController.addTextStyleRuns(arrayList, spannableString, spannableString);
            MediaDataController.addAnimatedEmojiSpans(arrayList, spannableString, null);
            return spannableString;
        } catch (Exception e) {
            FileLog.e("Html.fromHtml", e);
            return null;
        }
    }

    private static TLRPC$MessageEntity setEntityStartEnd(TLRPC$MessageEntity tLRPC$MessageEntity, int i, int i2) {
        tLRPC$MessageEntity.offset = i;
        tLRPC$MessageEntity.length = i2 - i;
        return tLRPC$MessageEntity;
    }

    /* loaded from: classes.dex */
    public static class ParsedSpoilerSpan {
        private ParsedSpoilerSpan() {
        }
    }

    /* loaded from: classes.dex */
    public static class HTMLTagHandler implements Html.TagHandler {
        private HTMLTagHandler() {
        }

        @Override // android.text.Html.TagHandler
        public void handleTag(boolean z, String str, Editable editable, XMLReader xMLReader) {
            if (str.contains("animated_emoji")) {
                if (z) {
                    if (CopyUtilities.animatedEmojiTagPattern == null) {
                        Pattern unused = CopyUtilities.animatedEmojiTagPattern = Pattern.compile("animated_emoji_documentId([0-9]*)");
                    }
                    Matcher matcher = CopyUtilities.animatedEmojiTagPattern.matcher(str);
                    if (matcher.matches()) {
                        editable.setSpan(new AnimatedEmojiSpan(Long.parseLong(matcher.group(1)), (Paint.FontMetricsInt) null), editable.length(), editable.length(), 17);
                    }
                } else {
                    AnimatedEmojiSpan animatedEmojiSpan = (AnimatedEmojiSpan) getLast(editable, AnimatedEmojiSpan.class);
                    if (animatedEmojiSpan != null) {
                        int spanStart = editable.getSpanStart(animatedEmojiSpan);
                        editable.removeSpan(animatedEmojiSpan);
                        if (spanStart != editable.length()) {
                            editable.setSpan(animatedEmojiSpan, spanStart, editable.length(), 33);
                        }
                    }
                }
            }
            if (str.equals("spoiler")) {
                if (z) {
                    editable.setSpan(new ParsedSpoilerSpan(), editable.length(), editable.length(), 17);
                    return;
                }
                ParsedSpoilerSpan parsedSpoilerSpan = (ParsedSpoilerSpan) getLast(editable, ParsedSpoilerSpan.class);
                if (parsedSpoilerSpan == null) {
                    return;
                }
                int spanStart2 = editable.getSpanStart(parsedSpoilerSpan);
                editable.removeSpan(parsedSpoilerSpan);
                if (spanStart2 == editable.length()) {
                    return;
                }
                editable.setSpan(parsedSpoilerSpan, spanStart2, editable.length(), 33);
            }
        }

        private <T> T getLast(Editable editable, Class<T> cls) {
            Object[] spans = editable.getSpans(0, editable.length(), cls);
            if (spans.length == 0) {
                return null;
            }
            for (int length = spans.length; length > 0; length--) {
                int i = length - 1;
                if (editable.getSpanFlags(spans[i]) == 17) {
                    return (T) spans[i];
                }
            }
            return null;
        }
    }
}
