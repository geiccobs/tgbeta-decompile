package com.google.android.exoplayer2.text.ssa;

import android.text.TextUtils;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
/* loaded from: classes3.dex */
final class SsaDialogueFormat {
    public final int endTimeIndex;
    public final int length;
    public final int startTimeIndex;
    public final int styleIndex;
    public final int textIndex;

    private SsaDialogueFormat(int startTimeIndex, int endTimeIndex, int styleIndex, int textIndex, int length) {
        this.startTimeIndex = startTimeIndex;
        this.endTimeIndex = endTimeIndex;
        this.styleIndex = styleIndex;
        this.textIndex = textIndex;
        this.length = length;
    }

    public static SsaDialogueFormat fromFormatLine(String formatLine) {
        int startTimeIndex = -1;
        int endTimeIndex = -1;
        int styleIndex = -1;
        int textIndex = -1;
        Assertions.checkArgument(formatLine.startsWith("Format:"));
        String[] keys = TextUtils.split(formatLine.substring("Format:".length()), ",");
        int i = 0;
        while (true) {
            char c = 65535;
            if (i < keys.length) {
                String lowerInvariant = Util.toLowerInvariant(keys[i].trim());
                switch (lowerInvariant.hashCode()) {
                    case 100571:
                        if (lowerInvariant.equals(TtmlNode.END)) {
                            c = 1;
                            break;
                        }
                        break;
                    case 3556653:
                        if (lowerInvariant.equals("text")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 109757538:
                        if (lowerInvariant.equals(TtmlNode.START)) {
                            c = 0;
                            break;
                        }
                        break;
                    case 109780401:
                        if (lowerInvariant.equals(TtmlNode.TAG_STYLE)) {
                            c = 2;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        startTimeIndex = i;
                        break;
                    case 1:
                        endTimeIndex = i;
                        break;
                    case 2:
                        styleIndex = i;
                        break;
                    case 3:
                        textIndex = i;
                        break;
                }
                i++;
            } else if (startTimeIndex != -1 && endTimeIndex != -1) {
                return new SsaDialogueFormat(startTimeIndex, endTimeIndex, styleIndex, textIndex, keys.length);
            } else {
                return null;
            }
        }
    }
}
