package org.telegram.messenger.audioinfo.mp3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.google.android.exoplayer2.metadata.id3.ApicFrame;
import com.google.android.exoplayer2.metadata.id3.CommentFrame;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.messenger.audioinfo.AudioInfo;
/* loaded from: classes4.dex */
public class ID3v2Info extends AudioInfo {
    static final Logger LOGGER = Logger.getLogger(ID3v2Info.class.getName());
    private byte coverPictureType;
    private final Level debugLevel;

    /* loaded from: classes4.dex */
    public static class AttachedPicture {
        static final byte TYPE_COVER_FRONT = 3;
        static final byte TYPE_OTHER = 0;
        final String description;
        final byte[] imageData;
        final String imageType;
        final byte type;

        public AttachedPicture(byte type, String description, String imageType, byte[] imageData) {
            this.type = type;
            this.description = description;
            this.imageType = imageType;
            this.imageData = imageData;
        }
    }

    /* loaded from: classes4.dex */
    public static class CommentOrUnsynchronizedLyrics {
        final String description;
        final String language;
        final String text;

        public CommentOrUnsynchronizedLyrics(String language, String description, String text) {
            this.language = language;
            this.description = description;
            this.text = text;
        }
    }

    public static boolean isID3v2StartPosition(InputStream input) throws IOException {
        boolean z;
        input.mark(3);
        try {
            if (input.read() == 73 && input.read() == 68) {
                if (input.read() == 51) {
                    z = true;
                    return z;
                }
            }
            z = false;
            return z;
        } finally {
            input.reset();
        }
    }

    public ID3v2Info(InputStream input) throws IOException, ID3v2Exception {
        this(input, Level.FINEST);
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x005d, code lost:
        r1 = org.telegram.messenger.audioinfo.mp3.ID3v2Info.LOGGER;
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0063, code lost:
        if (r1.isLoggable(r14) == false) goto L34;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0065, code lost:
        r1.log(r14, "ID3 frame claims to extend frames area");
     */
    /* JADX WARN: Removed duplicated region for block: B:33:0x00dd  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public ID3v2Info(java.io.InputStream r13, java.util.logging.Level r14) throws java.io.IOException, org.telegram.messenger.audioinfo.mp3.ID3v2Exception {
        /*
            Method dump skipped, instructions count: 271
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.audioinfo.mp3.ID3v2Info.<init>(java.io.InputStream, java.util.logging.Level):void");
    }

    void parseFrame(ID3v2FrameBody frame) throws IOException, ID3v2Exception {
        Logger logger = LOGGER;
        if (logger.isLoggable(this.debugLevel)) {
            logger.log(this.debugLevel, "Parsing frame: " + frame.getFrameHeader().getFrameId());
        }
        String frameId = frame.getFrameHeader().getFrameId();
        char c = 65535;
        switch (frameId.hashCode()) {
            case 66913:
                if (frameId.equals("COM")) {
                    c = 2;
                    break;
                }
                break;
            case 79210:
                if (frameId.equals("PIC")) {
                    c = 0;
                    break;
                }
                break;
            case 82815:
                if (frameId.equals("TAL")) {
                    c = 4;
                    break;
                }
                break;
            case 82878:
                if (frameId.equals("TCM")) {
                    c = '\b';
                    break;
                }
                break;
            case 82880:
                if (frameId.equals("TCO")) {
                    c = '\n';
                    break;
                }
                break;
            case 82881:
                if (frameId.equals("TCP")) {
                    c = 6;
                    break;
                }
                break;
            case 82883:
                if (frameId.equals("TCR")) {
                    c = '\f';
                    break;
                }
                break;
            case 83149:
                if (frameId.equals("TLE")) {
                    c = 15;
                    break;
                }
                break;
            case 83253:
                if (frameId.equals("TP1")) {
                    c = 17;
                    break;
                }
                break;
            case 83254:
                if (frameId.equals("TP2")) {
                    c = 19;
                    break;
                }
                break;
            case 83269:
                if (frameId.equals("TPA")) {
                    c = 21;
                    break;
                }
                break;
            case 83341:
                if (frameId.equals("TRK")) {
                    c = 23;
                    break;
                }
                break;
            case 83377:
                if (frameId.equals("TT1")) {
                    c = 25;
                    break;
                }
                break;
            case 83378:
                if (frameId.equals("TT2")) {
                    c = 27;
                    break;
                }
                break;
            case 83552:
                if (frameId.equals("TYE")) {
                    c = 29;
                    break;
                }
                break;
            case 84125:
                if (frameId.equals("ULT")) {
                    c = 31;
                    break;
                }
                break;
            case 2015625:
                if (frameId.equals(ApicFrame.ID)) {
                    c = 1;
                    break;
                }
                break;
            case 2074380:
                if (frameId.equals(CommentFrame.ID)) {
                    c = 3;
                    break;
                }
                break;
            case 2567331:
                if (frameId.equals("TALB")) {
                    c = 5;
                    break;
                }
                break;
            case 2569298:
                if (frameId.equals("TCMP")) {
                    c = 7;
                    break;
                }
                break;
            case 2569357:
                if (frameId.equals("TCOM")) {
                    c = '\t';
                    break;
                }
                break;
            case 2569358:
                if (frameId.equals("TCON")) {
                    c = 11;
                    break;
                }
                break;
            case 2569360:
                if (frameId.equals("TCOP")) {
                    c = '\r';
                    break;
                }
                break;
            case 2570401:
                if (frameId.equals("TDRC")) {
                    c = 14;
                    break;
                }
                break;
            case 2575250:
                if (frameId.equals("TIT1")) {
                    c = 26;
                    break;
                }
                break;
            case 2575251:
                if (frameId.equals("TIT2")) {
                    c = 28;
                    break;
                }
                break;
            case 2577697:
                if (frameId.equals("TLEN")) {
                    c = 16;
                    break;
                }
                break;
            case 2581512:
                if (frameId.equals("TPE1")) {
                    c = 18;
                    break;
                }
                break;
            case 2581513:
                if (frameId.equals("TPE2")) {
                    c = 20;
                    break;
                }
                break;
            case 2581856:
                if (frameId.equals("TPOS")) {
                    c = 22;
                    break;
                }
                break;
            case 2583398:
                if (frameId.equals("TRCK")) {
                    c = 24;
                    break;
                }
                break;
            case 2590194:
                if (frameId.equals("TYER")) {
                    c = 30;
                    break;
                }
                break;
            case 2614438:
                if (frameId.equals("USLT")) {
                    c = ' ';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 1:
                if (this.cover == null || this.coverPictureType != 3) {
                    AttachedPicture picture = parseAttachedPictureFrame(frame);
                    if (this.cover == null || picture.type == 3 || picture.type == 0) {
                        try {
                            byte[] bytes = picture.imageData;
                            BitmapFactory.Options opts = new BitmapFactory.Options();
                            opts.inJustDecodeBounds = true;
                            opts.inSampleSize = 1;
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
                            if (opts.outWidth > 800 || opts.outHeight > 800) {
                                for (int size = Math.max(opts.outWidth, opts.outHeight); size > 800; size /= 2) {
                                    opts.inSampleSize *= 2;
                                }
                            }
                            opts.inJustDecodeBounds = false;
                            this.cover = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
                            if (this.cover != null) {
                                float scale = Math.max(this.cover.getWidth(), this.cover.getHeight()) / 120.0f;
                                if (scale > 0.0f) {
                                    this.smallCover = Bitmap.createScaledBitmap(this.cover, (int) (this.cover.getWidth() / scale), (int) (this.cover.getHeight() / scale), true);
                                } else {
                                    this.smallCover = this.cover;
                                }
                                if (this.smallCover == null) {
                                    this.smallCover = this.cover;
                                }
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        this.coverPictureType = picture.type;
                        return;
                    }
                    return;
                }
                return;
            case 2:
            case 3:
                CommentOrUnsynchronizedLyrics comm = parseCommentOrUnsynchronizedLyricsFrame(frame);
                if (this.comment == null || comm.description == null || "".equals(comm.description)) {
                    this.comment = comm.text;
                    return;
                }
                return;
            case 4:
            case 5:
                this.album = parseTextFrame(frame);
                return;
            case 6:
            case 7:
                this.compilation = IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE.equals(parseTextFrame(frame));
                return;
            case '\b':
            case '\t':
                this.composer = parseTextFrame(frame);
                return;
            case '\n':
            case 11:
                String tcon = parseTextFrame(frame);
                if (tcon.length() > 0) {
                    this.genre = tcon;
                    ID3v1Genre id3v1Genre = null;
                    try {
                        if (tcon.charAt(0) == '(') {
                            int pos = tcon.indexOf(41);
                            if (pos > 1 && (id3v1Genre = ID3v1Genre.getGenre(Integer.parseInt(tcon.substring(1, pos)))) == null && tcon.length() > pos + 1) {
                                this.genre = tcon.substring(pos + 1);
                            }
                        } else {
                            id3v1Genre = ID3v1Genre.getGenre(Integer.parseInt(tcon));
                        }
                        if (id3v1Genre != null) {
                            this.genre = id3v1Genre.getDescription();
                            return;
                        }
                        return;
                    } catch (NumberFormatException e2) {
                        return;
                    }
                }
                return;
            case '\f':
            case '\r':
                this.copyright = parseTextFrame(frame);
                return;
            case 14:
                String tdrc = parseTextFrame(frame);
                if (tdrc.length() >= 4) {
                    try {
                        this.year = Short.valueOf(tdrc.substring(0, 4)).shortValue();
                        return;
                    } catch (NumberFormatException e3) {
                        Logger logger2 = LOGGER;
                        if (logger2.isLoggable(this.debugLevel)) {
                            logger2.log(this.debugLevel, "Could not parse year from: " + tdrc);
                            return;
                        }
                        return;
                    }
                }
                return;
            case 15:
            case 16:
                String tlen = parseTextFrame(frame);
                try {
                    this.duration = Long.valueOf(tlen).longValue();
                    return;
                } catch (NumberFormatException e4) {
                    Logger logger3 = LOGGER;
                    if (logger3.isLoggable(this.debugLevel)) {
                        logger3.log(this.debugLevel, "Could not parse track duration: " + tlen);
                        return;
                    }
                    return;
                }
            case 17:
            case 18:
                this.artist = parseTextFrame(frame);
                return;
            case 19:
            case 20:
                this.albumArtist = parseTextFrame(frame);
                return;
            case 21:
            case 22:
                String tpos = parseTextFrame(frame);
                if (tpos.length() > 0) {
                    int index = tpos.indexOf(47);
                    if (index < 0) {
                        try {
                            this.disc = Short.valueOf(tpos).shortValue();
                            return;
                        } catch (NumberFormatException e5) {
                            Logger logger4 = LOGGER;
                            if (logger4.isLoggable(this.debugLevel)) {
                                logger4.log(this.debugLevel, "Could not parse disc number: " + tpos);
                                return;
                            }
                            return;
                        }
                    }
                    try {
                        this.disc = Short.valueOf(tpos.substring(0, index)).shortValue();
                    } catch (NumberFormatException e6) {
                        Logger logger5 = LOGGER;
                        if (logger5.isLoggable(this.debugLevel)) {
                            logger5.log(this.debugLevel, "Could not parse disc number: " + tpos);
                        }
                    }
                    try {
                        this.discs = Short.valueOf(tpos.substring(index + 1)).shortValue();
                        return;
                    } catch (NumberFormatException e7) {
                        Logger logger6 = LOGGER;
                        if (logger6.isLoggable(this.debugLevel)) {
                            logger6.log(this.debugLevel, "Could not parse number of discs: " + tpos);
                            return;
                        }
                        return;
                    }
                }
                return;
            case 23:
            case 24:
                String trck = parseTextFrame(frame);
                if (trck.length() > 0) {
                    int index2 = trck.indexOf(47);
                    if (index2 < 0) {
                        try {
                            this.track = Short.valueOf(trck).shortValue();
                            return;
                        } catch (NumberFormatException e8) {
                            Logger logger7 = LOGGER;
                            if (logger7.isLoggable(this.debugLevel)) {
                                logger7.log(this.debugLevel, "Could not parse track number: " + trck);
                                return;
                            }
                            return;
                        }
                    }
                    try {
                        this.track = Short.valueOf(trck.substring(0, index2)).shortValue();
                    } catch (NumberFormatException e9) {
                        Logger logger8 = LOGGER;
                        if (logger8.isLoggable(this.debugLevel)) {
                            logger8.log(this.debugLevel, "Could not parse track number: " + trck);
                        }
                    }
                    try {
                        this.tracks = Short.valueOf(trck.substring(index2 + 1)).shortValue();
                        return;
                    } catch (NumberFormatException e10) {
                        Logger logger9 = LOGGER;
                        if (logger9.isLoggable(this.debugLevel)) {
                            logger9.log(this.debugLevel, "Could not parse number of tracks: " + trck);
                            return;
                        }
                        return;
                    }
                }
                return;
            case 25:
            case 26:
                this.grouping = parseTextFrame(frame);
                return;
            case 27:
            case 28:
                this.title = parseTextFrame(frame);
                return;
            case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
            case 30:
                String tyer = parseTextFrame(frame);
                if (tyer.length() > 0) {
                    try {
                        this.year = Short.valueOf(tyer).shortValue();
                        return;
                    } catch (NumberFormatException e11) {
                        Logger logger10 = LOGGER;
                        if (logger10.isLoggable(this.debugLevel)) {
                            logger10.log(this.debugLevel, "Could not parse year: " + tyer);
                            return;
                        }
                        return;
                    }
                }
                return;
            case 31:
            case ' ':
                if (this.lyrics == null) {
                    this.lyrics = parseCommentOrUnsynchronizedLyricsFrame(frame).text;
                    return;
                }
                return;
            default:
                return;
        }
    }

    String parseTextFrame(ID3v2FrameBody frame) throws IOException, ID3v2Exception {
        ID3v2Encoding encoding = frame.readEncoding();
        return frame.readFixedLengthString((int) frame.getRemainingLength(), encoding);
    }

    CommentOrUnsynchronizedLyrics parseCommentOrUnsynchronizedLyricsFrame(ID3v2FrameBody data) throws IOException, ID3v2Exception {
        ID3v2Encoding encoding = data.readEncoding();
        String language = data.readFixedLengthString(3, ID3v2Encoding.ISO_8859_1);
        String description = data.readZeroTerminatedString(200, encoding);
        String text = data.readFixedLengthString((int) data.getRemainingLength(), encoding);
        return new CommentOrUnsynchronizedLyrics(language, description, text);
    }

    AttachedPicture parseAttachedPictureFrame(ID3v2FrameBody data) throws IOException, ID3v2Exception {
        String imageType;
        ID3v2Encoding encoding = data.readEncoding();
        if (data.getTagHeader().getVersion() == 2) {
            String fileType = data.readFixedLengthString(3, ID3v2Encoding.ISO_8859_1);
            String upperCase = fileType.toUpperCase();
            char c = 65535;
            switch (upperCase.hashCode()) {
                case 73665:
                    if (upperCase.equals("JPG")) {
                        c = 1;
                        break;
                    }
                    break;
                case 79369:
                    if (upperCase.equals("PNG")) {
                        c = 0;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    imageType = "image/png";
                    break;
                case 1:
                    imageType = "image/jpeg";
                    break;
                default:
                    imageType = "image/unknown";
                    break;
            }
        } else {
            imageType = data.readZeroTerminatedString(20, ID3v2Encoding.ISO_8859_1);
        }
        byte pictureType = data.getData().readByte();
        String description = data.readZeroTerminatedString(200, encoding);
        byte[] imageData = data.getData().readFully((int) data.getRemainingLength());
        return new AttachedPicture(pictureType, description, imageType, imageData);
    }
}
