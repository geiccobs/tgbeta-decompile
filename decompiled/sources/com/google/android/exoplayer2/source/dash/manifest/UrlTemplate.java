package com.google.android.exoplayer2.source.dash.manifest;

import java.util.Locale;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes3.dex */
public final class UrlTemplate {
    private static final String BANDWIDTH = "Bandwidth";
    private static final int BANDWIDTH_ID = 3;
    private static final String DEFAULT_FORMAT_TAG = "%01d";
    private static final String ESCAPED_DOLLAR = "$$";
    private static final String NUMBER = "Number";
    private static final int NUMBER_ID = 2;
    private static final String REPRESENTATION = "RepresentationID";
    private static final int REPRESENTATION_ID = 1;
    private static final String TIME = "Time";
    private static final int TIME_ID = 4;
    private final int identifierCount;
    private final String[] identifierFormatTags;
    private final int[] identifiers;
    private final String[] urlPieces;

    public static UrlTemplate compile(String template) {
        String[] urlPieces = new String[5];
        int[] identifiers = new int[4];
        String[] identifierFormatTags = new String[4];
        int identifierCount = parseTemplate(template, urlPieces, identifiers, identifierFormatTags);
        return new UrlTemplate(urlPieces, identifiers, identifierFormatTags, identifierCount);
    }

    private UrlTemplate(String[] urlPieces, int[] identifiers, String[] identifierFormatTags, int identifierCount) {
        this.urlPieces = urlPieces;
        this.identifiers = identifiers;
        this.identifierFormatTags = identifierFormatTags;
        this.identifierCount = identifierCount;
    }

    public String buildUri(String representationId, long segmentNumber, int bandwidth, long time) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (true) {
            int i2 = this.identifierCount;
            if (i < i2) {
                builder.append(this.urlPieces[i]);
                int[] iArr = this.identifiers;
                if (iArr[i] == 1) {
                    builder.append(representationId);
                } else if (iArr[i] == 2) {
                    builder.append(String.format(Locale.US, this.identifierFormatTags[i], Long.valueOf(segmentNumber)));
                } else if (iArr[i] == 3) {
                    builder.append(String.format(Locale.US, this.identifierFormatTags[i], Integer.valueOf(bandwidth)));
                } else if (iArr[i] == 4) {
                    builder.append(String.format(Locale.US, this.identifierFormatTags[i], Long.valueOf(time)));
                }
                i++;
            } else {
                builder.append(this.urlPieces[i2]);
                return builder.toString();
            }
        }
    }

    private static int parseTemplate(String template, String[] urlPieces, int[] identifiers, String[] identifierFormatTags) {
        urlPieces[0] = "";
        int templateIndex = 0;
        int identifierCount = 0;
        while (templateIndex < template.length()) {
            int dollarIndex = template.indexOf("$", templateIndex);
            char c = 65535;
            if (dollarIndex == -1) {
                urlPieces[identifierCount] = urlPieces[identifierCount] + template.substring(templateIndex);
                templateIndex = template.length();
            } else if (dollarIndex != templateIndex) {
                urlPieces[identifierCount] = urlPieces[identifierCount] + template.substring(templateIndex, dollarIndex);
                templateIndex = dollarIndex;
            } else if (!template.startsWith(ESCAPED_DOLLAR, templateIndex)) {
                int secondIndex = template.indexOf("$", templateIndex + 1);
                String identifier = template.substring(templateIndex + 1, secondIndex);
                if (identifier.equals(REPRESENTATION)) {
                    identifiers[identifierCount] = 1;
                } else {
                    int formatTagIndex = identifier.indexOf("%0");
                    String formatTag = DEFAULT_FORMAT_TAG;
                    if (formatTagIndex != -1) {
                        formatTag = identifier.substring(formatTagIndex);
                        if (!formatTag.endsWith(Theme.DEFAULT_BACKGROUND_SLUG) && !formatTag.endsWith("x")) {
                            formatTag = formatTag + Theme.DEFAULT_BACKGROUND_SLUG;
                        }
                        identifier = identifier.substring(0, formatTagIndex);
                    }
                    switch (identifier.hashCode()) {
                        case -1950496919:
                            if (identifier.equals(NUMBER)) {
                                c = 0;
                                break;
                            }
                            break;
                        case 2606829:
                            if (identifier.equals(TIME)) {
                                c = 2;
                                break;
                            }
                            break;
                        case 38199441:
                            if (identifier.equals(BANDWIDTH)) {
                                c = 1;
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                            identifiers[identifierCount] = 2;
                            break;
                        case 1:
                            identifiers[identifierCount] = 3;
                            break;
                        case 2:
                            identifiers[identifierCount] = 4;
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid template: " + template);
                    }
                    identifierFormatTags[identifierCount] = formatTag;
                }
                identifierCount++;
                urlPieces[identifierCount] = "";
                templateIndex = secondIndex + 1;
            } else {
                urlPieces[identifierCount] = urlPieces[identifierCount] + "$";
                templateIndex += 2;
            }
        }
        return identifierCount;
    }
}
