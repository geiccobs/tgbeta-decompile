package com.google.android.exoplayer2.extractor.mp4;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.GaplessInfoHolder;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.id3.ApicFrame;
import com.google.android.exoplayer2.metadata.id3.CommentFrame;
import com.google.android.exoplayer2.metadata.id3.Id3Frame;
import com.google.android.exoplayer2.metadata.id3.InternalFrame;
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
/* loaded from: classes3.dex */
public final class MetadataUtil {
    private static final String LANGUAGE_UNDEFINED = "und";
    private static final String MDTA_KEY_ANDROID_CAPTURE_FPS = "com.android.capture.fps";
    private static final int PICTURE_TYPE_FRONT_COVER = 3;
    private static final int SHORT_TYPE_ALBUM = 6384738;
    private static final int SHORT_TYPE_ARTIST = 4280916;
    private static final int SHORT_TYPE_COMMENT = 6516084;
    private static final int SHORT_TYPE_COMPOSER_1 = 6516589;
    private static final int SHORT_TYPE_COMPOSER_2 = 7828084;
    private static final int SHORT_TYPE_ENCODER = 7630703;
    private static final int SHORT_TYPE_GENRE = 6776174;
    private static final int SHORT_TYPE_LYRICS = 7108978;
    private static final int SHORT_TYPE_NAME_1 = 7233901;
    private static final int SHORT_TYPE_NAME_2 = 7631467;
    private static final int SHORT_TYPE_YEAR = 6578553;
    static final String[] STANDARD_GENRES = {"Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age", "Oldies", "Other", "Pop", "R&B", "Rap", "Reggae", "Rock", "Techno", "Industrial", "Alternative", "Ska", "Death Metal", "Pranks", "Soundtrack", "Euro-Techno", "Ambient", "Trip-Hop", "Vocal", "Jazz+Funk", "Fusion", "Trance", "Classical", "Instrumental", "Acid", "House", "Game", "Sound Clip", "Gospel", "Noise", "AlternRock", "Bass", "Soul", "Punk", "Space", "Meditative", "Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic", "Darkwave", "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream", "Southern Rock", "Comedy", "Cult", "Gangsta", "Top 40", "Christian Rap", "Pop/Funk", "Jungle", "Native American", "Cabaret", "New Wave", "Psychadelic", "Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical", "Rock & Roll", "Hard Rock", "Folk", "Folk-Rock", "National Folk", "Swing", "Fast Fusion", "Bebob", "Latin", "Revival", "Celtic", "Bluegrass", "Avantgarde", "Gothic Rock", "Progressive Rock", "Psychedelic Rock", "Symphonic Rock", "Slow Rock", "Big Band", "Chorus", "Easy Listening", "Acoustic", "Humour", "Speech", "Chanson", "Opera", "Chamber Music", "Sonata", "Symphony", "Booty Bass", "Primus", "Porn Groove", "Satire", "Slow Jam", "Club", "Tango", "Samba", "Folklore", "Ballad", "Power Ballad", "Rhythmic Soul", "Freestyle", "Duet", "Punk Rock", "Drum Solo", "A capella", "Euro-House", "Dance Hall", "Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror", "Indie", "BritPop", "Afro-Punk", "Polsk Punk", "Beat", "Christian Gangsta Rap", "Heavy Metal", "Black Metal", "Crossover", "Contemporary Christian", "Christian Rock", "Merengue", "Salsa", "Thrash Metal", "Anime", "Jpop", "Synthpop", "Abstract", "Art Rock", "Baroque", "Bhangra", "Big beat", "Breakbeat", "Chillout", "Downtempo", "Dub", "EBM", "Eclectic", "Electro", "Electroclash", "Emo", "Experimental", "Garage", "Global", "IDM", "Illbient", "Industro-Goth", "Jam Band", "Krautrock", "Leftfield", "Lounge", "Math Rock", "New Romantic", "Nu-Breakz", "Post-Punk", "Post-Rock", "Psytrance", "Shoegaze", "Space Rock", "Trop Rock", "World Music", "Neoclassical", "Audiobook", "Audio theatre", "Neue Deutsche Welle", "Podcast", "Indie-Rock", "G-Funk", "Dubstep", "Garage Rock", "Psybient"};
    private static final String TAG = "MetadataUtil";
    private static final int TYPE_ALBUM_ARTIST = 1631670868;
    private static final int TYPE_COMPILATION = 1668311404;
    private static final int TYPE_COVER_ART = 1668249202;
    private static final int TYPE_DISK_NUMBER = 1684632427;
    private static final int TYPE_GAPLESS_ALBUM = 1885823344;
    private static final int TYPE_GENRE = 1735291493;
    private static final int TYPE_GROUPING = 6779504;
    private static final int TYPE_INTERNAL = 757935405;
    private static final int TYPE_RATING = 1920233063;
    private static final int TYPE_SORT_ALBUM = 1936679276;
    private static final int TYPE_SORT_ALBUM_ARTIST = 1936679265;
    private static final int TYPE_SORT_ARTIST = 1936679282;
    private static final int TYPE_SORT_COMPOSER = 1936679791;
    private static final int TYPE_SORT_TRACK_NAME = 1936682605;
    private static final int TYPE_TEMPO = 1953329263;
    private static final int TYPE_TOP_BYTE_COPYRIGHT = 169;
    private static final int TYPE_TOP_BYTE_REPLACEMENT = 253;
    private static final int TYPE_TRACK_NUMBER = 1953655662;
    private static final int TYPE_TV_SHOW = 1953919848;
    private static final int TYPE_TV_SORT_SHOW = 1936683886;

    private MetadataUtil() {
    }

    public static Format getFormatWithMetadata(int trackType, Format format, Metadata udtaMetadata, Metadata mdtaMetadata, GaplessInfoHolder gaplessInfoHolder) {
        if (trackType == 1) {
            if (gaplessInfoHolder.hasGaplessInfo()) {
                format = format.copyWithGaplessInfo(gaplessInfoHolder.encoderDelay, gaplessInfoHolder.encoderPadding);
            }
            if (udtaMetadata != null) {
                return format.copyWithMetadata(udtaMetadata);
            }
            return format;
        } else if (trackType == 2 && mdtaMetadata != null) {
            for (int i = 0; i < mdtaMetadata.length(); i++) {
                Metadata.Entry entry = mdtaMetadata.get(i);
                if (entry instanceof MdtaMetadataEntry) {
                    MdtaMetadataEntry mdtaMetadataEntry = (MdtaMetadataEntry) entry;
                    if (MDTA_KEY_ANDROID_CAPTURE_FPS.equals(mdtaMetadataEntry.key)) {
                        format = format.copyWithMetadata(new Metadata(mdtaMetadataEntry));
                    }
                }
            }
            return format;
        } else {
            return format;
        }
    }

    public static Metadata.Entry parseIlstElement(ParsableByteArray ilst) {
        int position = ilst.getPosition();
        int endPosition = ilst.readInt() + position;
        int type = ilst.readInt();
        int typeTopByte = (type >> 24) & 255;
        try {
            if (typeTopByte == TYPE_TOP_BYTE_COPYRIGHT || typeTopByte == TYPE_TOP_BYTE_REPLACEMENT) {
                int shortType = 16777215 & type;
                if (shortType == SHORT_TYPE_COMMENT) {
                    return parseCommentAttribute(type, ilst);
                }
                if (shortType == SHORT_TYPE_NAME_1 || shortType == SHORT_TYPE_NAME_2) {
                    return parseTextAttribute(type, "TIT2", ilst);
                }
                if (shortType == SHORT_TYPE_COMPOSER_1 || shortType == SHORT_TYPE_COMPOSER_2) {
                    return parseTextAttribute(type, "TCOM", ilst);
                }
                if (shortType == SHORT_TYPE_YEAR) {
                    return parseTextAttribute(type, "TDRC", ilst);
                }
                if (shortType == SHORT_TYPE_ARTIST) {
                    return parseTextAttribute(type, "TPE1", ilst);
                }
                if (shortType == SHORT_TYPE_ENCODER) {
                    return parseTextAttribute(type, "TSSE", ilst);
                }
                if (shortType == SHORT_TYPE_ALBUM) {
                    return parseTextAttribute(type, "TALB", ilst);
                }
                if (shortType == SHORT_TYPE_LYRICS) {
                    return parseTextAttribute(type, "USLT", ilst);
                }
                if (shortType == SHORT_TYPE_GENRE) {
                    return parseTextAttribute(type, "TCON", ilst);
                }
                if (shortType == TYPE_GROUPING) {
                    return parseTextAttribute(type, "TIT1", ilst);
                }
            } else if (type == TYPE_GENRE) {
                return parseStandardGenreAttribute(ilst);
            } else {
                if (type == TYPE_DISK_NUMBER) {
                    return parseIndexAndCountAttribute(type, "TPOS", ilst);
                }
                if (type == TYPE_TRACK_NUMBER) {
                    return parseIndexAndCountAttribute(type, "TRCK", ilst);
                }
                if (type == TYPE_TEMPO) {
                    return parseUint8Attribute(type, "TBPM", ilst, true, false);
                }
                if (type == TYPE_COMPILATION) {
                    return parseUint8Attribute(type, "TCMP", ilst, true, true);
                }
                if (type == TYPE_COVER_ART) {
                    return parseCoverArt(ilst);
                }
                if (type == TYPE_ALBUM_ARTIST) {
                    return parseTextAttribute(type, "TPE2", ilst);
                }
                if (type == TYPE_SORT_TRACK_NAME) {
                    return parseTextAttribute(type, "TSOT", ilst);
                }
                if (type == TYPE_SORT_ALBUM) {
                    return parseTextAttribute(type, "TSO2", ilst);
                }
                if (type == TYPE_SORT_ARTIST) {
                    return parseTextAttribute(type, "TSOA", ilst);
                }
                if (type == TYPE_SORT_ALBUM_ARTIST) {
                    return parseTextAttribute(type, "TSOP", ilst);
                }
                if (type == TYPE_SORT_COMPOSER) {
                    return parseTextAttribute(type, "TSOC", ilst);
                }
                if (type == TYPE_RATING) {
                    return parseUint8Attribute(type, "ITUNESADVISORY", ilst, false, false);
                }
                if (type == TYPE_GAPLESS_ALBUM) {
                    return parseUint8Attribute(type, "ITUNESGAPLESS", ilst, false, true);
                }
                if (type == TYPE_TV_SORT_SHOW) {
                    return parseTextAttribute(type, "TVSHOWSORT", ilst);
                }
                if (type == TYPE_TV_SHOW) {
                    return parseTextAttribute(type, "TVSHOW", ilst);
                }
                if (type == TYPE_INTERNAL) {
                    return parseInternalAttribute(ilst, endPosition);
                }
            }
            Log.d(TAG, "Skipped unknown metadata entry: " + Atom.getAtomTypeString(type));
            return null;
        } finally {
            ilst.setPosition(endPosition);
        }
    }

    public static MdtaMetadataEntry parseMdtaMetadataEntryFromIlst(ParsableByteArray ilst, int endPosition, String key) {
        while (true) {
            int atomPosition = ilst.getPosition();
            if (atomPosition < endPosition) {
                int atomSize = ilst.readInt();
                int atomType = ilst.readInt();
                if (atomType == 1684108385) {
                    int typeIndicator = ilst.readInt();
                    int localeIndicator = ilst.readInt();
                    int dataSize = atomSize - 16;
                    byte[] value = new byte[dataSize];
                    ilst.readBytes(value, 0, dataSize);
                    return new MdtaMetadataEntry(key, value, localeIndicator, typeIndicator);
                }
                int typeIndicator2 = atomPosition + atomSize;
                ilst.setPosition(typeIndicator2);
            } else {
                return null;
            }
        }
    }

    private static TextInformationFrame parseTextAttribute(int type, String id, ParsableByteArray data) {
        int atomSize = data.readInt();
        int atomType = data.readInt();
        if (atomType == 1684108385) {
            data.skipBytes(8);
            String value = data.readNullTerminatedString(atomSize - 16);
            return new TextInformationFrame(id, null, value);
        }
        Log.w(TAG, "Failed to parse text attribute: " + Atom.getAtomTypeString(type));
        return null;
    }

    private static CommentFrame parseCommentAttribute(int type, ParsableByteArray data) {
        int atomSize = data.readInt();
        int atomType = data.readInt();
        if (atomType == 1684108385) {
            data.skipBytes(8);
            String value = data.readNullTerminatedString(atomSize - 16);
            return new CommentFrame("und", value, value);
        }
        Log.w(TAG, "Failed to parse comment attribute: " + Atom.getAtomTypeString(type));
        return null;
    }

    private static Id3Frame parseUint8Attribute(int type, String id, ParsableByteArray data, boolean isTextInformationFrame, boolean isBoolean) {
        int value = parseUint8AttributeValue(data);
        if (isBoolean) {
            value = Math.min(1, value);
        }
        if (value >= 0) {
            if (isTextInformationFrame) {
                return new TextInformationFrame(id, null, Integer.toString(value));
            }
            return new CommentFrame("und", id, Integer.toString(value));
        }
        Log.w(TAG, "Failed to parse uint8 attribute: " + Atom.getAtomTypeString(type));
        return null;
    }

    private static TextInformationFrame parseIndexAndCountAttribute(int type, String attributeName, ParsableByteArray data) {
        int atomSize = data.readInt();
        int atomType = data.readInt();
        if (atomType == 1684108385 && atomSize >= 22) {
            data.skipBytes(10);
            int index = data.readUnsignedShort();
            if (index > 0) {
                String value = "" + index;
                int count = data.readUnsignedShort();
                if (count > 0) {
                    value = value + "/" + count;
                }
                return new TextInformationFrame(attributeName, null, value);
            }
        }
        Log.w(TAG, "Failed to parse index/count attribute: " + Atom.getAtomTypeString(type));
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x001c  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x0014  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static com.google.android.exoplayer2.metadata.id3.TextInformationFrame parseStandardGenreAttribute(com.google.android.exoplayer2.util.ParsableByteArray r5) {
        /*
            int r0 = parseUint8AttributeValue(r5)
            r1 = 0
            if (r0 <= 0) goto L11
            java.lang.String[] r2 = com.google.android.exoplayer2.extractor.mp4.MetadataUtil.STANDARD_GENRES
            int r3 = r2.length
            if (r0 > r3) goto L11
            int r3 = r0 + (-1)
            r2 = r2[r3]
            goto L12
        L11:
            r2 = r1
        L12:
            if (r2 == 0) goto L1c
            com.google.android.exoplayer2.metadata.id3.TextInformationFrame r3 = new com.google.android.exoplayer2.metadata.id3.TextInformationFrame
            java.lang.String r4 = "TCON"
            r3.<init>(r4, r1, r2)
            return r3
        L1c:
            java.lang.String r3 = "MetadataUtil"
            java.lang.String r4 = "Failed to parse standard genre code"
            com.google.android.exoplayer2.util.Log.w(r3, r4)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mp4.MetadataUtil.parseStandardGenreAttribute(com.google.android.exoplayer2.util.ParsableByteArray):com.google.android.exoplayer2.metadata.id3.TextInformationFrame");
    }

    private static ApicFrame parseCoverArt(ParsableByteArray data) {
        int atomSize = data.readInt();
        int atomType = data.readInt();
        if (atomType != 1684108385) {
            Log.w(TAG, "Failed to parse cover art attribute");
            return null;
        }
        int fullVersionInt = data.readInt();
        int flags = Atom.parseFullAtomFlags(fullVersionInt);
        String mimeType = flags == 13 ? "image/jpeg" : flags == 14 ? "image/png" : null;
        if (mimeType == null) {
            Log.w(TAG, "Unrecognized cover art flags: " + flags);
            return null;
        }
        data.skipBytes(4);
        byte[] pictureData = new byte[atomSize - 16];
        data.readBytes(pictureData, 0, pictureData.length);
        return new ApicFrame(mimeType, null, 3, pictureData);
    }

    private static Id3Frame parseInternalAttribute(ParsableByteArray data, int endPosition) {
        String domain = null;
        String name = null;
        int dataAtomPosition = -1;
        int dataAtomSize = -1;
        while (data.getPosition() < endPosition) {
            int atomPosition = data.getPosition();
            int atomSize = data.readInt();
            int atomType = data.readInt();
            data.skipBytes(4);
            if (atomType == 1835360622) {
                domain = data.readNullTerminatedString(atomSize - 12);
            } else if (atomType == 1851878757) {
                name = data.readNullTerminatedString(atomSize - 12);
            } else {
                if (atomType == 1684108385) {
                    dataAtomPosition = atomPosition;
                    dataAtomSize = atomSize;
                }
                data.skipBytes(atomSize - 12);
            }
        }
        if (domain == null || name == null || dataAtomPosition == -1) {
            return null;
        }
        data.setPosition(dataAtomPosition);
        data.skipBytes(16);
        String value = data.readNullTerminatedString(dataAtomSize - 16);
        return new InternalFrame(domain, name, value);
    }

    private static int parseUint8AttributeValue(ParsableByteArray data) {
        data.skipBytes(4);
        int atomType = data.readInt();
        if (atomType == 1684108385) {
            data.skipBytes(8);
            return data.readUnsignedByte();
        }
        Log.w(TAG, "Failed to parse uint8 attribute value");
        return -1;
    }
}
