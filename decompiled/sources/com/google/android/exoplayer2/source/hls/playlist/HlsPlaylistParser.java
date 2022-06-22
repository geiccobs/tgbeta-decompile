package com.google.android.exoplayer2.source.hls.playlist;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.extractor.mp4.PsshAtomUtil;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.UnrecognizedInputFormatException;
import com.google.android.exoplayer2.source.hls.HlsTrackMetadataEntry;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.UriUtil;
import com.google.android.exoplayer2.util.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
/* loaded from: classes.dex */
public final class HlsPlaylistParser implements ParsingLoadable.Parser<HlsPlaylist> {
    private final HlsMasterPlaylist masterPlaylist;
    private static final Pattern REGEX_AVERAGE_BANDWIDTH = Pattern.compile("AVERAGE-BANDWIDTH=(\\d+)\\b");
    private static final Pattern REGEX_VIDEO = Pattern.compile("VIDEO=\"(.+?)\"");
    private static final Pattern REGEX_AUDIO = Pattern.compile("AUDIO=\"(.+?)\"");
    private static final Pattern REGEX_SUBTITLES = Pattern.compile("SUBTITLES=\"(.+?)\"");
    private static final Pattern REGEX_CLOSED_CAPTIONS = Pattern.compile("CLOSED-CAPTIONS=\"(.+?)\"");
    private static final Pattern REGEX_BANDWIDTH = Pattern.compile("[^-]BANDWIDTH=(\\d+)\\b");
    private static final Pattern REGEX_CHANNELS = Pattern.compile("CHANNELS=\"(.+?)\"");
    private static final Pattern REGEX_CODECS = Pattern.compile("CODECS=\"(.+?)\"");
    private static final Pattern REGEX_RESOLUTION = Pattern.compile("RESOLUTION=(\\d+x\\d+)");
    private static final Pattern REGEX_FRAME_RATE = Pattern.compile("FRAME-RATE=([\\d\\.]+)\\b");
    private static final Pattern REGEX_TARGET_DURATION = Pattern.compile("#EXT-X-TARGETDURATION:(\\d+)\\b");
    private static final Pattern REGEX_VERSION = Pattern.compile("#EXT-X-VERSION:(\\d+)\\b");
    private static final Pattern REGEX_PLAYLIST_TYPE = Pattern.compile("#EXT-X-PLAYLIST-TYPE:(.+)\\b");
    private static final Pattern REGEX_MEDIA_SEQUENCE = Pattern.compile("#EXT-X-MEDIA-SEQUENCE:(\\d+)\\b");
    private static final Pattern REGEX_MEDIA_DURATION = Pattern.compile("#EXTINF:([\\d\\.]+)\\b");
    private static final Pattern REGEX_MEDIA_TITLE = Pattern.compile("#EXTINF:[\\d\\.]+\\b,(.+)");
    private static final Pattern REGEX_TIME_OFFSET = Pattern.compile("TIME-OFFSET=(-?[\\d\\.]+)\\b");
    private static final Pattern REGEX_BYTERANGE = Pattern.compile("#EXT-X-BYTERANGE:(\\d+(?:@\\d+)?)\\b");
    private static final Pattern REGEX_ATTR_BYTERANGE = Pattern.compile("BYTERANGE=\"(\\d+(?:@\\d+)?)\\b\"");
    private static final Pattern REGEX_METHOD = Pattern.compile("METHOD=(NONE|AES-128|SAMPLE-AES|SAMPLE-AES-CENC|SAMPLE-AES-CTR)\\s*(?:,|$)");
    private static final Pattern REGEX_KEYFORMAT = Pattern.compile("KEYFORMAT=\"(.+?)\"");
    private static final Pattern REGEX_KEYFORMATVERSIONS = Pattern.compile("KEYFORMATVERSIONS=\"(.+?)\"");
    private static final Pattern REGEX_URI = Pattern.compile("URI=\"(.+?)\"");
    private static final Pattern REGEX_IV = Pattern.compile("IV=([^,.*]+)");
    private static final Pattern REGEX_TYPE = Pattern.compile("TYPE=(AUDIO|VIDEO|SUBTITLES|CLOSED-CAPTIONS)");
    private static final Pattern REGEX_LANGUAGE = Pattern.compile("LANGUAGE=\"(.+?)\"");
    private static final Pattern REGEX_NAME = Pattern.compile("NAME=\"(.+?)\"");
    private static final Pattern REGEX_GROUP_ID = Pattern.compile("GROUP-ID=\"(.+?)\"");
    private static final Pattern REGEX_CHARACTERISTICS = Pattern.compile("CHARACTERISTICS=\"(.+?)\"");
    private static final Pattern REGEX_INSTREAM_ID = Pattern.compile("INSTREAM-ID=\"((?:CC|SERVICE)\\d+)\"");
    private static final Pattern REGEX_AUTOSELECT = compileBooleanAttrPattern("AUTOSELECT");
    private static final Pattern REGEX_DEFAULT = compileBooleanAttrPattern("DEFAULT");
    private static final Pattern REGEX_FORCED = compileBooleanAttrPattern("FORCED");
    private static final Pattern REGEX_VALUE = Pattern.compile("VALUE=\"(.+?)\"");
    private static final Pattern REGEX_IMPORT = Pattern.compile("IMPORT=\"(.+?)\"");
    private static final Pattern REGEX_VARIABLE_REFERENCE = Pattern.compile("\\{\\$([a-zA-Z0-9\\-_]+)\\}");

    public HlsPlaylistParser() {
        this(HlsMasterPlaylist.EMPTY);
    }

    public HlsPlaylistParser(HlsMasterPlaylist hlsMasterPlaylist) {
        this.masterPlaylist = hlsMasterPlaylist;
    }

    @Override // com.google.android.exoplayer2.upstream.ParsingLoadable.Parser
    public HlsPlaylist parse(Uri uri, InputStream inputStream) throws IOException {
        String trim;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        ArrayDeque arrayDeque = new ArrayDeque();
        try {
            if (!checkPlaylistHeader(bufferedReader)) {
                throw new UnrecognizedInputFormatException("Input does not start with the #EXTM3U header.", uri);
            }
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    trim = readLine.trim();
                    if (!trim.isEmpty()) {
                        if (trim.startsWith("#EXT-X-STREAM-INF")) {
                            arrayDeque.add(trim);
                            return parseMasterPlaylist(new LineIterator(arrayDeque, bufferedReader), uri.toString());
                        } else if (trim.startsWith("#EXT-X-TARGETDURATION") || trim.startsWith("#EXT-X-MEDIA-SEQUENCE") || trim.startsWith("#EXTINF") || trim.startsWith("#EXT-X-KEY") || trim.startsWith("#EXT-X-BYTERANGE") || trim.equals("#EXT-X-DISCONTINUITY") || trim.equals("#EXT-X-DISCONTINUITY-SEQUENCE") || trim.equals("#EXT-X-ENDLIST")) {
                            break;
                        } else {
                            arrayDeque.add(trim);
                        }
                    }
                } else {
                    Util.closeQuietly(bufferedReader);
                    throw new ParserException("Failed to parse the playlist, could not identify any tags.");
                }
            }
            arrayDeque.add(trim);
            return parseMediaPlaylist(this.masterPlaylist, new LineIterator(arrayDeque, bufferedReader), uri.toString());
        } finally {
            Util.closeQuietly(bufferedReader);
        }
    }

    private static boolean checkPlaylistHeader(BufferedReader bufferedReader) throws IOException {
        int read = bufferedReader.read();
        if (read == 239) {
            if (bufferedReader.read() != 187 || bufferedReader.read() != 191) {
                return false;
            }
            read = bufferedReader.read();
        }
        int skipIgnorableWhitespace = skipIgnorableWhitespace(bufferedReader, true, read);
        for (int i = 0; i < 7; i++) {
            if (skipIgnorableWhitespace != "#EXTM3U".charAt(i)) {
                return false;
            }
            skipIgnorableWhitespace = bufferedReader.read();
        }
        return Util.isLinebreak(skipIgnorableWhitespace(bufferedReader, false, skipIgnorableWhitespace));
    }

    private static int skipIgnorableWhitespace(BufferedReader bufferedReader, boolean z, int i) throws IOException {
        while (i != -1 && Character.isWhitespace(i) && (z || !Util.isLinebreak(i))) {
            i = bufferedReader.read();
        }
        return i;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static HlsMasterPlaylist parseMasterPlaylist(LineIterator lineIterator, String str) throws IOException {
        char c;
        String str2;
        String str3;
        String str4;
        int i;
        int i2;
        String str5;
        float f;
        int i3;
        int i4;
        String str6;
        HashSet hashSet;
        HashMap hashMap;
        ArrayList arrayList;
        ArrayList arrayList2;
        ArrayList arrayList3;
        boolean z;
        int i5;
        int i6;
        String str7 = str;
        HashMap hashMap2 = new HashMap();
        HashMap hashMap3 = new HashMap();
        ArrayList arrayList4 = new ArrayList();
        ArrayList arrayList5 = new ArrayList();
        ArrayList arrayList6 = new ArrayList();
        ArrayList arrayList7 = new ArrayList();
        ArrayList arrayList8 = new ArrayList();
        ArrayList arrayList9 = new ArrayList();
        ArrayList arrayList10 = new ArrayList();
        ArrayList arrayList11 = new ArrayList();
        boolean z2 = false;
        boolean z3 = false;
        while (lineIterator.hasNext()) {
            String next = lineIterator.next();
            if (next.startsWith("#EXT")) {
                arrayList11.add(next);
            }
            if (next.startsWith("#EXT-X-DEFINE")) {
                hashMap3.put(parseStringAttr(next, REGEX_NAME, hashMap3), parseStringAttr(next, REGEX_VALUE, hashMap3));
            } else if (next.equals("#EXT-X-INDEPENDENT-SEGMENTS")) {
                z2 = true;
            } else if (next.startsWith("#EXT-X-MEDIA")) {
                arrayList9.add(next);
            } else {
                if (next.startsWith("#EXT-X-SESSION-KEY")) {
                    DrmInitData.SchemeData parseDrmSchemeData = parseDrmSchemeData(next, parseOptionalStringAttr(next, REGEX_KEYFORMAT, "identity", hashMap3), hashMap3);
                    if (parseDrmSchemeData != null) {
                        arrayList3 = arrayList8;
                        z = z2;
                        arrayList10.add(new DrmInitData(parseEncryptionScheme(parseStringAttr(next, REGEX_METHOD, hashMap3)), parseDrmSchemeData));
                    } else {
                        arrayList3 = arrayList8;
                        z = z2;
                    }
                } else {
                    arrayList3 = arrayList8;
                    z = z2;
                    if (next.startsWith("#EXT-X-STREAM-INF")) {
                        boolean contains = z3 | next.contains("CLOSED-CAPTIONS=NONE");
                        int parseIntAttr = parseIntAttr(next, REGEX_BANDWIDTH);
                        parseOptionalIntAttr(next, REGEX_AVERAGE_BANDWIDTH, -1);
                        String parseOptionalStringAttr = parseOptionalStringAttr(next, REGEX_CODECS, hashMap3);
                        String parseOptionalStringAttr2 = parseOptionalStringAttr(next, REGEX_RESOLUTION, hashMap3);
                        if (parseOptionalStringAttr2 != null) {
                            String[] split = parseOptionalStringAttr2.split("x");
                            int parseInt = Integer.parseInt(split[0]);
                            int parseInt2 = Integer.parseInt(split[1]);
                            if (parseInt <= 0 || parseInt2 <= 0) {
                                parseInt2 = -1;
                                parseInt = -1;
                            }
                            i5 = parseInt2;
                            i6 = parseInt;
                        } else {
                            i6 = -1;
                            i5 = -1;
                        }
                        String parseOptionalStringAttr3 = parseOptionalStringAttr(next, REGEX_FRAME_RATE, hashMap3);
                        float parseFloat = parseOptionalStringAttr3 != null ? Float.parseFloat(parseOptionalStringAttr3) : -1.0f;
                        String parseOptionalStringAttr4 = parseOptionalStringAttr(next, REGEX_VIDEO, hashMap3);
                        String parseOptionalStringAttr5 = parseOptionalStringAttr(next, REGEX_AUDIO, hashMap3);
                        String parseOptionalStringAttr6 = parseOptionalStringAttr(next, REGEX_SUBTITLES, hashMap3);
                        String parseOptionalStringAttr7 = parseOptionalStringAttr(next, REGEX_CLOSED_CAPTIONS, hashMap3);
                        if (!lineIterator.hasNext()) {
                            throw new ParserException("#EXT-X-STREAM-INF tag must be followed by another line");
                        }
                        Uri resolveToUri = UriUtil.resolveToUri(str7, replaceVariableReferences(lineIterator.next(), hashMap3));
                        arrayList2 = arrayList10;
                        arrayList4.add(new HlsMasterPlaylist.Variant(resolveToUri, Format.createVideoContainerFormat(Integer.toString(arrayList4.size()), null, "application/x-mpegURL", null, parseOptionalStringAttr, null, parseIntAttr, i6, i5, parseFloat, null, 0, 0), parseOptionalStringAttr4, parseOptionalStringAttr5, parseOptionalStringAttr6, parseOptionalStringAttr7));
                        ArrayList arrayList12 = (ArrayList) hashMap2.get(resolveToUri);
                        if (arrayList12 == null) {
                            arrayList12 = new ArrayList();
                            hashMap2.put(resolveToUri, arrayList12);
                        }
                        arrayList = arrayList11;
                        arrayList12.add(new HlsTrackMetadataEntry.VariantInfo(parseIntAttr, parseOptionalStringAttr4, parseOptionalStringAttr5, parseOptionalStringAttr6, parseOptionalStringAttr7));
                        z3 = contains;
                        z2 = z;
                        arrayList8 = arrayList3;
                        arrayList10 = arrayList2;
                        arrayList11 = arrayList;
                    }
                }
                arrayList = arrayList11;
                arrayList2 = arrayList10;
                z2 = z;
                arrayList8 = arrayList3;
                arrayList10 = arrayList2;
                arrayList11 = arrayList;
            }
            arrayList3 = arrayList8;
            arrayList = arrayList11;
            arrayList2 = arrayList10;
            z = z2;
            z2 = z;
            arrayList8 = arrayList3;
            arrayList10 = arrayList2;
            arrayList11 = arrayList;
        }
        ArrayList arrayList13 = arrayList8;
        ArrayList arrayList14 = arrayList11;
        ArrayList arrayList15 = arrayList10;
        boolean z4 = z2;
        ArrayList arrayList16 = new ArrayList();
        HashSet hashSet2 = new HashSet();
        int i7 = 0;
        while (i7 < arrayList4.size()) {
            HlsMasterPlaylist.Variant variant = (HlsMasterPlaylist.Variant) arrayList4.get(i7);
            if (hashSet2.add(variant.url)) {
                Assertions.checkState(variant.format.metadata == null);
                hashMap = hashMap2;
                hashSet = hashSet2;
                arrayList16.add(variant.copyWithFormat(variant.format.copyWithMetadata(new Metadata(new HlsTrackMetadataEntry(null, null, (List) Assertions.checkNotNull((ArrayList) hashMap2.get(variant.url)))))));
            } else {
                hashMap = hashMap2;
                hashSet = hashSet2;
            }
            i7++;
            hashSet2 = hashSet;
            hashMap2 = hashMap;
        }
        List list = null;
        Format format = null;
        int i8 = 0;
        while (i8 < arrayList9.size()) {
            String str8 = (String) arrayList9.get(i8);
            String parseStringAttr = parseStringAttr(str8, REGEX_GROUP_ID, hashMap3);
            String parseStringAttr2 = parseStringAttr(str8, REGEX_NAME, hashMap3);
            String parseOptionalStringAttr8 = parseOptionalStringAttr(str8, REGEX_URI, hashMap3);
            Uri resolveToUri2 = parseOptionalStringAttr8 == null ? null : UriUtil.resolveToUri(str7, parseOptionalStringAttr8);
            String parseOptionalStringAttr9 = parseOptionalStringAttr(str8, REGEX_LANGUAGE, hashMap3);
            int parseSelectionFlags = parseSelectionFlags(str8);
            int parseRoleFlags = parseRoleFlags(str8, hashMap3);
            ArrayList arrayList17 = arrayList9;
            StringBuilder sb = new StringBuilder();
            sb.append(parseStringAttr);
            Format format2 = format;
            sb.append(":");
            sb.append(parseStringAttr2);
            String sb2 = sb.toString();
            ArrayList arrayList18 = arrayList16;
            boolean z5 = z3;
            Metadata metadata = new Metadata(new HlsTrackMetadataEntry(parseStringAttr, parseStringAttr2, Collections.emptyList()));
            String parseStringAttr3 = parseStringAttr(str8, REGEX_TYPE, hashMap3);
            parseStringAttr3.hashCode();
            switch (parseStringAttr3.hashCode()) {
                case -959297733:
                    if (parseStringAttr3.equals("SUBTITLES")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case -333210994:
                    if (parseStringAttr3.equals("CLOSED-CAPTIONS")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 62628790:
                    if (parseStringAttr3.equals("AUDIO")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 81665115:
                    if (parseStringAttr3.equals("VIDEO")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    HlsMasterPlaylist.Variant variantWithSubtitleGroup = getVariantWithSubtitleGroup(arrayList4, parseStringAttr);
                    if (variantWithSubtitleGroup != null) {
                        String codecsOfType = Util.getCodecsOfType(variantWithSubtitleGroup.format.codecs, 3);
                        str3 = MimeTypes.getMediaMimeType(codecsOfType);
                        str2 = codecsOfType;
                    } else {
                        str3 = null;
                        str2 = null;
                    }
                    arrayList7.add(new HlsMasterPlaylist.Rendition(resolveToUri2, Format.createTextContainerFormat(sb2, parseStringAttr2, "application/x-mpegURL", str3 == null ? "text/vtt" : str3, str2, -1, parseSelectionFlags, parseRoleFlags, parseOptionalStringAttr9).copyWithMetadata(metadata), parseStringAttr, parseStringAttr2));
                    break;
                case 1:
                    String parseStringAttr4 = parseStringAttr(str8, REGEX_INSTREAM_ID, hashMap3);
                    if (parseStringAttr4.startsWith("CC")) {
                        i = Integer.parseInt(parseStringAttr4.substring(2));
                        str4 = "application/cea-608";
                    } else {
                        i = Integer.parseInt(parseStringAttr4.substring(7));
                        str4 = "application/cea-708";
                    }
                    int i9 = i;
                    String str9 = str4;
                    if (list == null) {
                        list = new ArrayList();
                    }
                    list.add(Format.createTextContainerFormat(sb2, parseStringAttr2, null, str9, null, -1, parseSelectionFlags, parseRoleFlags, parseOptionalStringAttr9, i9));
                    break;
                case 2:
                    HlsMasterPlaylist.Variant variantWithAudioGroup = getVariantWithAudioGroup(arrayList4, parseStringAttr);
                    String codecsOfType2 = variantWithAudioGroup != null ? Util.getCodecsOfType(variantWithAudioGroup.format.codecs, 1) : null;
                    String mediaMimeType = codecsOfType2 != null ? MimeTypes.getMediaMimeType(codecsOfType2) : null;
                    String parseOptionalStringAttr10 = parseOptionalStringAttr(str8, REGEX_CHANNELS, hashMap3);
                    if (parseOptionalStringAttr10 != null) {
                        int parseInt3 = Integer.parseInt(Util.splitAtFirst(parseOptionalStringAttr10, "/")[0]);
                        if ("audio/eac3".equals(mediaMimeType) && parseOptionalStringAttr10.endsWith("/JOC")) {
                            mediaMimeType = "audio/eac3-joc";
                        }
                        str5 = mediaMimeType;
                        i2 = parseInt3;
                    } else {
                        str5 = mediaMimeType;
                        i2 = -1;
                    }
                    Format createAudioContainerFormat = Format.createAudioContainerFormat(sb2, parseStringAttr2, "application/x-mpegURL", str5, codecsOfType2, null, -1, i2, -1, null, parseSelectionFlags, parseRoleFlags, parseOptionalStringAttr9);
                    if (resolveToUri2 != null) {
                        arrayList6.add(new HlsMasterPlaylist.Rendition(resolveToUri2, createAudioContainerFormat.copyWithMetadata(metadata), parseStringAttr, parseStringAttr2));
                        break;
                    } else {
                        format = createAudioContainerFormat;
                        continue;
                        i8++;
                        str7 = str;
                        arrayList9 = arrayList17;
                        arrayList16 = arrayList18;
                        z3 = z5;
                    }
                    break;
                case 3:
                    HlsMasterPlaylist.Variant variantWithVideoGroup = getVariantWithVideoGroup(arrayList4, parseStringAttr);
                    if (variantWithVideoGroup != null) {
                        Format format3 = variantWithVideoGroup.format;
                        String codecsOfType3 = Util.getCodecsOfType(format3.codecs, 2);
                        int i10 = format3.width;
                        int i11 = format3.height;
                        f = format3.frameRate;
                        str6 = codecsOfType3;
                        i4 = i10;
                        i3 = i11;
                    } else {
                        str6 = null;
                        i4 = -1;
                        i3 = -1;
                        f = -1.0f;
                    }
                    Format copyWithMetadata = Format.createVideoContainerFormat(sb2, parseStringAttr2, "application/x-mpegURL", str6 != null ? MimeTypes.getMediaMimeType(str6) : null, str6, null, -1, i4, i3, f, null, parseSelectionFlags, parseRoleFlags).copyWithMetadata(metadata);
                    if (resolveToUri2 != null) {
                        arrayList5.add(new HlsMasterPlaylist.Rendition(resolveToUri2, copyWithMetadata, parseStringAttr, parseStringAttr2));
                    }
            }
            format = format2;
            i8++;
            str7 = str;
            arrayList9 = arrayList17;
            arrayList16 = arrayList18;
            z3 = z5;
        }
        ArrayList arrayList19 = arrayList16;
        Format format4 = format;
        if (z3) {
            list = Collections.emptyList();
        }
        return new HlsMasterPlaylist(str, arrayList14, arrayList19, arrayList5, arrayList6, arrayList7, arrayList13, format4, list, z4, hashMap3, arrayList15);
    }

    private static HlsMasterPlaylist.Variant getVariantWithAudioGroup(ArrayList<HlsMasterPlaylist.Variant> arrayList, String str) {
        for (int i = 0; i < arrayList.size(); i++) {
            HlsMasterPlaylist.Variant variant = arrayList.get(i);
            if (str.equals(variant.audioGroupId)) {
                return variant;
            }
        }
        return null;
    }

    private static HlsMasterPlaylist.Variant getVariantWithVideoGroup(ArrayList<HlsMasterPlaylist.Variant> arrayList, String str) {
        for (int i = 0; i < arrayList.size(); i++) {
            HlsMasterPlaylist.Variant variant = arrayList.get(i);
            if (str.equals(variant.videoGroupId)) {
                return variant;
            }
        }
        return null;
    }

    private static HlsMasterPlaylist.Variant getVariantWithSubtitleGroup(ArrayList<HlsMasterPlaylist.Variant> arrayList, String str) {
        for (int i = 0; i < arrayList.size(); i++) {
            HlsMasterPlaylist.Variant variant = arrayList.get(i);
            if (str.equals(variant.subtitleGroupId)) {
                return variant;
            }
        }
        return null;
    }

    private static HlsMediaPlaylist parseMediaPlaylist(HlsMasterPlaylist hlsMasterPlaylist, LineIterator lineIterator, String str) throws IOException {
        long j;
        long j2;
        String str2;
        String str3;
        TreeMap treeMap;
        DrmInitData drmInitData;
        HlsMasterPlaylist hlsMasterPlaylist2 = hlsMasterPlaylist;
        boolean z = hlsMasterPlaylist2.hasIndependentSegments;
        HashMap hashMap = new HashMap();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        TreeMap treeMap2 = new TreeMap();
        String str4 = "";
        char c = 0;
        int i = 1;
        boolean z2 = z;
        long j3 = -9223372036854775807L;
        long j4 = -9223372036854775807L;
        String str5 = str4;
        boolean z3 = false;
        int i2 = 0;
        String str6 = null;
        String str7 = null;
        long j5 = 0;
        int i3 = 0;
        long j6 = 0;
        int i4 = 1;
        boolean z4 = false;
        DrmInitData drmInitData2 = null;
        long j7 = 0;
        long j8 = 0;
        DrmInitData drmInitData3 = null;
        boolean z5 = false;
        String str8 = null;
        long j9 = -1;
        int i5 = 0;
        long j10 = 0;
        HlsMediaPlaylist.Segment segment = null;
        while (true) {
            long j11 = 0;
            while (lineIterator.hasNext()) {
                String next = lineIterator.next();
                if (next.startsWith("#EXT")) {
                    arrayList2.add(next);
                }
                if (next.startsWith("#EXT-X-PLAYLIST-TYPE")) {
                    String parseStringAttr = parseStringAttr(next, REGEX_PLAYLIST_TYPE, hashMap);
                    if ("VOD".equals(parseStringAttr)) {
                        i2 = 1;
                    } else if ("EVENT".equals(parseStringAttr)) {
                        i2 = 2;
                    }
                } else if (next.startsWith("#EXT-X-START")) {
                    j3 = (long) (parseDoubleAttr(next, REGEX_TIME_OFFSET) * 1000000.0d);
                } else if (next.startsWith("#EXT-X-MAP")) {
                    String parseStringAttr2 = parseStringAttr(next, REGEX_URI, hashMap);
                    String parseOptionalStringAttr = parseOptionalStringAttr(next, REGEX_ATTR_BYTERANGE, hashMap);
                    if (parseOptionalStringAttr != null) {
                        String[] split = parseOptionalStringAttr.split("@");
                        long parseLong = Long.parseLong(split[c]);
                        if (split.length > i) {
                            j7 = Long.parseLong(split[i]);
                        }
                        j = parseLong;
                        j2 = j7;
                    } else {
                        j2 = j7;
                        j = j9;
                    }
                    if (str6 != null && str8 == null) {
                        throw new ParserException("The encryption IV attribute must be present when an initialization segment is encrypted with METHOD=AES-128.");
                    }
                    segment = new HlsMediaPlaylist.Segment(parseStringAttr2, j2, j, str6, str8);
                    c = 0;
                    j7 = 0;
                    j9 = -1;
                } else {
                    if (next.startsWith("#EXT-X-TARGETDURATION")) {
                        j4 = parseIntAttr(next, REGEX_TARGET_DURATION) * 1000000;
                    } else if (next.startsWith("#EXT-X-MEDIA-SEQUENCE")) {
                        j8 = parseLongAttr(next, REGEX_MEDIA_SEQUENCE);
                        j6 = j8;
                    } else if (next.startsWith("#EXT-X-VERSION")) {
                        i4 = parseIntAttr(next, REGEX_VERSION);
                    } else {
                        if (next.startsWith("#EXT-X-DEFINE")) {
                            String parseOptionalStringAttr2 = parseOptionalStringAttr(next, REGEX_IMPORT, hashMap);
                            if (parseOptionalStringAttr2 != null) {
                                String str9 = hlsMasterPlaylist2.variableDefinitions.get(parseOptionalStringAttr2);
                                if (str9 != null) {
                                    hashMap.put(parseOptionalStringAttr2, str9);
                                }
                            } else {
                                hashMap.put(parseStringAttr(next, REGEX_NAME, hashMap), parseStringAttr(next, REGEX_VALUE, hashMap));
                            }
                        } else if (next.startsWith("#EXTINF")) {
                            str5 = parseOptionalStringAttr(next, REGEX_MEDIA_TITLE, str4, hashMap);
                            j11 = (long) (parseDoubleAttr(next, REGEX_MEDIA_DURATION) * 1000000.0d);
                        } else if (next.startsWith("#EXT-X-KEY")) {
                            String parseStringAttr3 = parseStringAttr(next, REGEX_METHOD, hashMap);
                            String parseOptionalStringAttr3 = parseOptionalStringAttr(next, REGEX_KEYFORMAT, "identity", hashMap);
                            if ("NONE".equals(parseStringAttr3)) {
                                treeMap2.clear();
                                str6 = null;
                                drmInitData3 = null;
                                str8 = null;
                            } else {
                                String parseOptionalStringAttr4 = parseOptionalStringAttr(next, REGEX_IV, hashMap);
                                if ("identity".equals(parseOptionalStringAttr3)) {
                                    if ("AES-128".equals(parseStringAttr3)) {
                                        str6 = parseStringAttr(next, REGEX_URI, hashMap);
                                        str8 = parseOptionalStringAttr4;
                                    }
                                    str8 = parseOptionalStringAttr4;
                                    str6 = null;
                                } else {
                                    if (str7 == null) {
                                        str7 = parseEncryptionScheme(parseStringAttr3);
                                    }
                                    DrmInitData.SchemeData parseDrmSchemeData = parseDrmSchemeData(next, parseOptionalStringAttr3, hashMap);
                                    if (parseDrmSchemeData != null) {
                                        treeMap2.put(parseOptionalStringAttr3, parseDrmSchemeData);
                                        str8 = parseOptionalStringAttr4;
                                        str6 = null;
                                        drmInitData3 = null;
                                    }
                                    str8 = parseOptionalStringAttr4;
                                    str6 = null;
                                }
                            }
                        } else if (next.startsWith("#EXT-X-BYTERANGE")) {
                            String[] split2 = parseStringAttr(next, REGEX_BYTERANGE, hashMap).split("@");
                            j9 = Long.parseLong(split2[0]);
                            if (split2.length > i) {
                                j7 = Long.parseLong(split2[i]);
                            }
                        } else if (next.startsWith("#EXT-X-DISCONTINUITY-SEQUENCE")) {
                            i3 = Integer.parseInt(next.substring(next.indexOf(58) + i));
                            z3 = true;
                        } else if (next.equals("#EXT-X-DISCONTINUITY")) {
                            i5++;
                        } else if (next.startsWith("#EXT-X-PROGRAM-DATE-TIME")) {
                            if (j5 == 0) {
                                j5 = C.msToUs(Util.parseXsDateTime(next.substring(next.indexOf(58) + i))) - j10;
                            }
                        } else if (next.equals("#EXT-X-GAP")) {
                            c = 0;
                            z5 = true;
                        } else if (next.equals("#EXT-X-INDEPENDENT-SEGMENTS")) {
                            c = 0;
                            z2 = true;
                        } else if (next.equals("#EXT-X-ENDLIST")) {
                            c = 0;
                            z4 = true;
                        } else if (!next.startsWith("#")) {
                            if (str6 == null) {
                                str2 = null;
                            } else {
                                str2 = str8 != null ? str8 : Long.toHexString(j8);
                            }
                            long j12 = j8 + 1;
                            long j13 = j9 == -1 ? 0L : j7;
                            if (drmInitData3 != null || treeMap2.isEmpty()) {
                                treeMap = treeMap2;
                                str3 = str4;
                                drmInitData = drmInitData3;
                            } else {
                                DrmInitData.SchemeData[] schemeDataArr = (DrmInitData.SchemeData[]) treeMap2.values().toArray(new DrmInitData.SchemeData[0]);
                                drmInitData = new DrmInitData(str7, schemeDataArr);
                                if (drmInitData2 == null) {
                                    DrmInitData.SchemeData[] schemeDataArr2 = new DrmInitData.SchemeData[schemeDataArr.length];
                                    treeMap = treeMap2;
                                    str3 = str4;
                                    int i6 = 0;
                                    while (i6 < schemeDataArr.length) {
                                        schemeDataArr2[i6] = schemeDataArr[i6].copyWithData(null);
                                        i6++;
                                        schemeDataArr = schemeDataArr;
                                    }
                                    drmInitData2 = new DrmInitData(str7, schemeDataArr2);
                                } else {
                                    treeMap = treeMap2;
                                    str3 = str4;
                                }
                            }
                            arrayList.add(new HlsMediaPlaylist.Segment(replaceVariableReferences(next, hashMap), segment, str5, j11, i5, j10, drmInitData, str6, str2, j13, j9, z5));
                            j10 += j11;
                            if (j9 != -1) {
                                j13 += j9;
                            }
                            j7 = j13;
                            hlsMasterPlaylist2 = hlsMasterPlaylist;
                            j9 = -1;
                            j8 = j12;
                            drmInitData3 = drmInitData;
                            treeMap2 = treeMap;
                            str4 = str3;
                            str5 = str4;
                            c = 0;
                            i = 1;
                            z5 = false;
                        }
                        hlsMasterPlaylist2 = hlsMasterPlaylist;
                        treeMap2 = treeMap2;
                        str4 = str4;
                        c = 0;
                        i = 1;
                    }
                    c = 0;
                }
            }
            return new HlsMediaPlaylist(i2, str, arrayList2, j3, j5, z3, i3, j6, i4, j4, z2, z4, j5 != 0, drmInitData2, arrayList);
        }
    }

    private static int parseSelectionFlags(String str) {
        boolean parseOptionalBooleanAttribute = parseOptionalBooleanAttribute(str, REGEX_DEFAULT, false);
        if (parseOptionalBooleanAttribute(str, REGEX_FORCED, false)) {
            parseOptionalBooleanAttribute = (parseOptionalBooleanAttribute ? 1 : 0) | true;
        }
        if (parseOptionalBooleanAttribute(str, REGEX_AUTOSELECT, false)) {
            return (parseOptionalBooleanAttribute ? 1 : 0) | 4;
        }
        int i = parseOptionalBooleanAttribute ? 1 : 0;
        int i2 = parseOptionalBooleanAttribute ? 1 : 0;
        return i;
    }

    private static int parseRoleFlags(String str, Map<String, String> map) {
        String parseOptionalStringAttr = parseOptionalStringAttr(str, REGEX_CHARACTERISTICS, map);
        int i = 0;
        if (TextUtils.isEmpty(parseOptionalStringAttr)) {
            return 0;
        }
        String[] split = Util.split(parseOptionalStringAttr, ",");
        if (Util.contains(split, "public.accessibility.describes-video")) {
            i = 512;
        }
        if (Util.contains(split, "public.accessibility.transcribes-spoken-dialog")) {
            i |= 4096;
        }
        if (Util.contains(split, "public.accessibility.describes-music-and-sound")) {
            i |= 1024;
        }
        return Util.contains(split, "public.easy-to-read") ? i | 8192 : i;
    }

    private static DrmInitData.SchemeData parseDrmSchemeData(String str, String str2, Map<String, String> map) throws ParserException {
        String parseOptionalStringAttr = parseOptionalStringAttr(str, REGEX_KEYFORMATVERSIONS, "1", map);
        if ("urn:uuid:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed".equals(str2)) {
            String parseStringAttr = parseStringAttr(str, REGEX_URI, map);
            return new DrmInitData.SchemeData(C.WIDEVINE_UUID, "video/mp4", Base64.decode(parseStringAttr.substring(parseStringAttr.indexOf(44)), 0));
        } else if ("com.widevine".equals(str2)) {
            return new DrmInitData.SchemeData(C.WIDEVINE_UUID, "hls", Util.getUtf8Bytes(str));
        } else {
            if (!"com.microsoft.playready".equals(str2) || !"1".equals(parseOptionalStringAttr)) {
                return null;
            }
            String parseStringAttr2 = parseStringAttr(str, REGEX_URI, map);
            byte[] decode = Base64.decode(parseStringAttr2.substring(parseStringAttr2.indexOf(44)), 0);
            UUID uuid = C.PLAYREADY_UUID;
            return new DrmInitData.SchemeData(uuid, "video/mp4", PsshAtomUtil.buildPsshAtom(uuid, decode));
        }
    }

    private static String parseEncryptionScheme(String str) {
        return ("SAMPLE-AES-CENC".equals(str) || "SAMPLE-AES-CTR".equals(str)) ? "cenc" : "cbcs";
    }

    private static int parseIntAttr(String str, Pattern pattern) throws ParserException {
        return Integer.parseInt(parseStringAttr(str, pattern, Collections.emptyMap()));
    }

    private static int parseOptionalIntAttr(String str, Pattern pattern, int i) {
        Matcher matcher = pattern.matcher(str);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : i;
    }

    private static long parseLongAttr(String str, Pattern pattern) throws ParserException {
        return Long.parseLong(parseStringAttr(str, pattern, Collections.emptyMap()));
    }

    private static double parseDoubleAttr(String str, Pattern pattern) throws ParserException {
        return Double.parseDouble(parseStringAttr(str, pattern, Collections.emptyMap()));
    }

    private static String parseStringAttr(String str, Pattern pattern, Map<String, String> map) throws ParserException {
        String parseOptionalStringAttr = parseOptionalStringAttr(str, pattern, map);
        if (parseOptionalStringAttr != null) {
            return parseOptionalStringAttr;
        }
        throw new ParserException("Couldn't match " + pattern.pattern() + " in " + str);
    }

    private static String parseOptionalStringAttr(String str, Pattern pattern, Map<String, String> map) {
        return parseOptionalStringAttr(str, pattern, null, map);
    }

    private static String parseOptionalStringAttr(String str, Pattern pattern, String str2, Map<String, String> map) {
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            str2 = matcher.group(1);
        }
        return (map.isEmpty() || str2 == null) ? str2 : replaceVariableReferences(str2, map);
    }

    private static String replaceVariableReferences(String str, Map<String, String> map) {
        Matcher matcher = REGEX_VARIABLE_REFERENCE.matcher(str);
        StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()) {
            String group = matcher.group(1);
            if (map.containsKey(group)) {
                matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(map.get(group)));
            }
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private static boolean parseOptionalBooleanAttribute(String str, Pattern pattern, boolean z) {
        Matcher matcher = pattern.matcher(str);
        return matcher.find() ? matcher.group(1).equals("YES") : z;
    }

    private static Pattern compileBooleanAttrPattern(String str) {
        return Pattern.compile(str + "=(NO|YES)");
    }

    /* loaded from: classes.dex */
    public static class LineIterator {
        private final Queue<String> extraLines;
        private String next;
        private final BufferedReader reader;

        public LineIterator(Queue<String> queue, BufferedReader bufferedReader) {
            this.extraLines = queue;
            this.reader = bufferedReader;
        }

        @EnsuresNonNullIf(expression = {"next"}, result = true)
        public boolean hasNext() throws IOException {
            String trim;
            if (this.next != null) {
                return true;
            }
            if (!this.extraLines.isEmpty()) {
                this.next = (String) Assertions.checkNotNull(this.extraLines.poll());
                return true;
            }
            do {
                String readLine = this.reader.readLine();
                this.next = readLine;
                if (readLine == null) {
                    return false;
                }
                trim = readLine.trim();
                this.next = trim;
            } while (trim.isEmpty());
            return true;
        }

        public String next() throws IOException {
            if (hasNext()) {
                String str = this.next;
                this.next = null;
                return str;
            }
            throw new NoSuchElementException();
        }
    }
}
