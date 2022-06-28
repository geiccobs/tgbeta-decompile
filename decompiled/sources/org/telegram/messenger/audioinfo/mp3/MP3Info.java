package org.telegram.messenger.audioinfo.mp3;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.audioinfo.mp3.MP3Frame;
/* loaded from: classes4.dex */
public class MP3Info extends AudioInfo {
    static final Logger LOGGER = Logger.getLogger(MP3Info.class.getName());

    /* loaded from: classes4.dex */
    public interface StopReadCondition {
        boolean stopRead(MP3Input mP3Input) throws IOException;
    }

    public MP3Info(InputStream input, long fileLength) throws IOException, ID3v2Exception, MP3Exception {
        this(input, fileLength, Level.FINEST);
    }

    public MP3Info(InputStream input, long fileLength, Level debugLevel) throws IOException, ID3v2Exception, MP3Exception {
        this.brand = "MP3";
        this.version = "0";
        MP3Input data = new MP3Input(input);
        if (ID3v2Info.isID3v2StartPosition(data)) {
            ID3v2Info info = new ID3v2Info(data, debugLevel);
            this.album = info.getAlbum();
            this.albumArtist = info.getAlbumArtist();
            this.artist = info.getArtist();
            this.comment = info.getComment();
            this.cover = info.getCover();
            this.smallCover = info.getSmallCover();
            this.compilation = info.isCompilation();
            this.composer = info.getComposer();
            this.copyright = info.getCopyright();
            this.disc = info.getDisc();
            this.discs = info.getDiscs();
            this.duration = info.getDuration();
            this.genre = info.getGenre();
            this.grouping = info.getGrouping();
            this.lyrics = info.getLyrics();
            this.title = info.getTitle();
            this.track = info.getTrack();
            this.tracks = info.getTracks();
            this.year = info.getYear();
        }
        if (this.duration <= 0 || this.duration >= 3600000) {
            try {
                this.duration = calculateDuration(data, fileLength, new StopReadCondition(fileLength) { // from class: org.telegram.messenger.audioinfo.mp3.MP3Info.1
                    final long stopPosition;
                    final /* synthetic */ long val$fileLength;

                    {
                        MP3Info.this = this;
                        this.val$fileLength = fileLength;
                        this.stopPosition = fileLength - 128;
                    }

                    @Override // org.telegram.messenger.audioinfo.mp3.MP3Info.StopReadCondition
                    public boolean stopRead(MP3Input data2) throws IOException {
                        return data2.getPosition() == this.stopPosition && ID3v1Info.isID3v1StartPosition(data2);
                    }
                });
            } catch (MP3Exception e) {
                Logger logger = LOGGER;
                if (logger.isLoggable(debugLevel)) {
                    logger.log(debugLevel, "Could not determine MP3 duration", (Throwable) e);
                }
            }
        }
        if ((this.title == null || this.album == null || this.artist == null) && data.getPosition() <= fileLength - 128) {
            data.skipFully((fileLength - 128) - data.getPosition());
            if (ID3v1Info.isID3v1StartPosition(input)) {
                ID3v1Info info2 = new ID3v1Info(input);
                if (this.album == null) {
                    this.album = info2.getAlbum();
                }
                if (this.artist == null) {
                    this.artist = info2.getArtist();
                }
                if (this.comment == null) {
                    this.comment = info2.getComment();
                }
                if (this.genre == null) {
                    this.genre = info2.getGenre();
                }
                if (this.title == null) {
                    this.title = info2.getTitle();
                }
                if (this.track == 0) {
                    this.track = info2.getTrack();
                }
                if (this.year == 0) {
                    this.year = info2.getYear();
                }
            }
        }
    }

    MP3Frame readFirstFrame(MP3Input data, StopReadCondition stopCondition) throws IOException {
        int b1;
        int b12 = stopCondition.stopRead(data) ? -1 : data.read();
        int b0 = 0;
        for (byte b = -1; b12 != b; b = -1) {
            if (b0 == 255 && (b12 & 224) == 224) {
                data.mark(2);
                int b2 = stopCondition.stopRead(data) ? -1 : data.read();
                if (b2 != b) {
                    int b3 = stopCondition.stopRead(data) ? -1 : data.read();
                    if (b3 != b) {
                        MP3Frame.Header header = null;
                        try {
                            header = new MP3Frame.Header(b12, b2, b3);
                        } catch (MP3Exception e) {
                        }
                        if (header == null) {
                            b1 = b12;
                        } else {
                            data.reset();
                            data.mark(header.getFrameSize() + 2);
                            byte[] frameBytes = new byte[header.getFrameSize()];
                            frameBytes[0] = b;
                            frameBytes[1] = (byte) b12;
                            try {
                                data.readFully(frameBytes, 2, frameBytes.length - 2);
                                MP3Frame frame = new MP3Frame(header, frameBytes);
                                if (frame.isChecksumError()) {
                                    b1 = b12;
                                } else {
                                    int nextB0 = stopCondition.stopRead(data) ? -1 : data.read();
                                    int nextB1 = stopCondition.stopRead(data) ? -1 : data.read();
                                    if (nextB0 != b && nextB1 != b) {
                                        if (nextB0 != 255 || (nextB1 & 254) != (b12 & 254)) {
                                            b1 = b12;
                                        } else {
                                            int nextB2 = stopCondition.stopRead(data) ? -1 : data.read();
                                            int nextB3 = stopCondition.stopRead(data) ? -1 : data.read();
                                            if (nextB2 != b && nextB3 != b) {
                                                try {
                                                    if (new MP3Frame.Header(nextB1, nextB2, nextB3).isCompatible(header)) {
                                                        data.reset();
                                                        b1 = b12;
                                                        try {
                                                            data.skipFully(frameBytes.length - 2);
                                                            return frame;
                                                        } catch (MP3Exception e2) {
                                                        }
                                                    } else {
                                                        b1 = b12;
                                                    }
                                                } catch (MP3Exception e3) {
                                                    b1 = b12;
                                                }
                                            }
                                            return frame;
                                        }
                                    }
                                    return frame;
                                }
                            } catch (EOFException e4) {
                                return null;
                            }
                        }
                        data.reset();
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                b1 = b12;
            }
            b0 = b1;
            b12 = stopCondition.stopRead(data) ? -1 : data.read();
        }
        return null;
    }

    MP3Frame readNextFrame(MP3Input data, StopReadCondition stopCondition, MP3Frame previousFrame) throws IOException {
        MP3Frame.Header previousHeader = previousFrame.getHeader();
        data.mark(4);
        int b0 = stopCondition.stopRead(data) ? -1 : data.read();
        int b1 = stopCondition.stopRead(data) ? -1 : data.read();
        if (b0 == -1 || b1 == -1) {
            return null;
        }
        if (b0 == 255 && (b1 & 224) == 224) {
            int b2 = stopCondition.stopRead(data) ? -1 : data.read();
            int b3 = stopCondition.stopRead(data) ? -1 : data.read();
            if (b2 == -1 || b3 == -1) {
                return null;
            }
            MP3Frame.Header nextHeader = null;
            try {
                nextHeader = new MP3Frame.Header(b1, b2, b3);
            } catch (MP3Exception e) {
            }
            if (nextHeader != null && nextHeader.isCompatible(previousHeader)) {
                byte[] frameBytes = new byte[nextHeader.getFrameSize()];
                frameBytes[0] = (byte) b0;
                frameBytes[1] = (byte) b1;
                frameBytes[2] = (byte) b2;
                frameBytes[3] = (byte) b3;
                try {
                    data.readFully(frameBytes, 4, frameBytes.length - 4);
                    return new MP3Frame(nextHeader, frameBytes);
                } catch (EOFException e2) {
                    return null;
                }
            }
        }
        data.reset();
        return null;
    }

    long calculateDuration(MP3Input data, long totalLength, StopReadCondition stopCondition) throws IOException, MP3Exception {
        MP3Info mP3Info = this;
        MP3Input mP3Input = data;
        MP3Frame frame = mP3Info.readFirstFrame(mP3Input, stopCondition);
        if (frame != null) {
            int numberOfFrames = frame.getNumberOfFrames();
            if (numberOfFrames > 0) {
                return frame.getHeader().getTotalDuration(frame.getSize() * numberOfFrames);
            }
            int numberOfFrames2 = 1;
            long firstFramePosition = data.getPosition() - frame.getSize();
            long frameSizeSum = frame.getSize();
            int firstFrameBitrate = frame.getHeader().getBitrate();
            long bitrateSum = firstFrameBitrate;
            boolean vbr = false;
            int cbrThreshold = 10000 / frame.getHeader().getDuration();
            while (true) {
                if (numberOfFrames2 != cbrThreshold || vbr || totalLength <= 0) {
                    boolean vbr2 = vbr;
                    int cbrThreshold2 = cbrThreshold;
                    MP3Frame readNextFrame = mP3Info.readNextFrame(mP3Input, stopCondition, frame);
                    frame = readNextFrame;
                    if (readNextFrame != null) {
                        int bitrate = frame.getHeader().getBitrate();
                        if (bitrate == firstFrameBitrate) {
                            vbr = vbr2;
                        } else {
                            vbr = true;
                        }
                        bitrateSum += bitrate;
                        frameSizeSum += frame.getSize();
                        numberOfFrames2++;
                        mP3Info = this;
                        mP3Input = data;
                        cbrThreshold = cbrThreshold2;
                    } else {
                        return (((1000 * frameSizeSum) * numberOfFrames2) * 8) / bitrateSum;
                    }
                } else {
                    return frame.getHeader().getTotalDuration(totalLength - firstFramePosition);
                }
            }
        } else {
            throw new MP3Exception("No audio frame");
        }
    }
}
