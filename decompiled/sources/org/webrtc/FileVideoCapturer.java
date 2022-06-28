package org.webrtc;

import android.content.Context;
import android.os.SystemClock;
import com.google.android.exoplayer2.C;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
/* loaded from: classes5.dex */
public class FileVideoCapturer implements VideoCapturer {
    private static final String TAG = "FileVideoCapturer";
    private CapturerObserver capturerObserver;
    private final VideoReader videoReader;
    private final Timer timer = new Timer();
    private final TimerTask tickTask = new TimerTask() { // from class: org.webrtc.FileVideoCapturer.1
        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            FileVideoCapturer.this.tick();
        }
    };

    /* loaded from: classes5.dex */
    public interface VideoReader {
        void close();

        VideoFrame getNextFrame();
    }

    /* loaded from: classes5.dex */
    private static class VideoReaderY4M implements VideoReader {
        private static final String TAG = "VideoReaderY4M";
        private final int frameHeight;
        private final int frameWidth;
        private final RandomAccessFile mediaFile;
        private final FileChannel mediaFileChannel;
        private final long videoStart;
        private static final String Y4M_FRAME_DELIMETER = "FRAME";
        private static final int FRAME_DELIMETER_LENGTH = Y4M_FRAME_DELIMETER.length() + 1;

        public VideoReaderY4M(String file) throws IOException {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            this.mediaFile = randomAccessFile;
            this.mediaFileChannel = randomAccessFile.getChannel();
            StringBuilder builder = new StringBuilder();
            while (true) {
                int c = this.mediaFile.read();
                if (c == -1) {
                    throw new RuntimeException("Found end of file before end of header for file: " + file);
                } else if (c != 10) {
                    builder.append((char) c);
                } else {
                    this.videoStart = this.mediaFileChannel.position();
                    String header = builder.toString();
                    String[] headerTokens = header.split("[ ]");
                    int w = 0;
                    int h = 0;
                    String colorSpace = "";
                    for (String tok : headerTokens) {
                        switch (tok.charAt(0)) {
                            case 'C':
                                colorSpace = tok.substring(1);
                                break;
                            case 'H':
                                h = Integer.parseInt(tok.substring(1));
                                break;
                            case 'W':
                                w = Integer.parseInt(tok.substring(1));
                                break;
                        }
                    }
                    Logging.d(TAG, "Color space: " + colorSpace);
                    if (!colorSpace.equals("420") && !colorSpace.equals("420mpeg2")) {
                        throw new IllegalArgumentException("Does not support any other color space than I420 or I420mpeg2");
                    }
                    if (w % 2 == 1 || h % 2 == 1) {
                        throw new IllegalArgumentException("Does not support odd width or height");
                    }
                    this.frameWidth = w;
                    this.frameHeight = h;
                    Logging.d(TAG, "frame dim: (" + w + ", " + h + ")");
                    return;
                }
            }
        }

        @Override // org.webrtc.FileVideoCapturer.VideoReader
        public VideoFrame getNextFrame() {
            long captureTimeNs = TimeUnit.MILLISECONDS.toNanos(SystemClock.elapsedRealtime());
            JavaI420Buffer buffer = JavaI420Buffer.allocate(this.frameWidth, this.frameHeight);
            ByteBuffer dataY = buffer.getDataY();
            ByteBuffer dataU = buffer.getDataU();
            ByteBuffer dataV = buffer.getDataV();
            int i = this.frameHeight;
            int chromaHeight = (i + 1) / 2;
            int strideY = i * buffer.getStrideY();
            int strideU = buffer.getStrideU() * chromaHeight;
            int strideV = buffer.getStrideV() * chromaHeight;
            try {
                int i2 = FRAME_DELIMETER_LENGTH;
                ByteBuffer frameDelim = ByteBuffer.allocate(i2);
                if (this.mediaFileChannel.read(frameDelim) < i2) {
                    this.mediaFileChannel.position(this.videoStart);
                    if (this.mediaFileChannel.read(frameDelim) < i2) {
                        throw new RuntimeException("Error looping video");
                    }
                }
                String frameDelimStr = new String(frameDelim.array(), Charset.forName(C.ASCII_NAME));
                if (!frameDelimStr.equals("FRAME\n")) {
                    throw new RuntimeException("Frames should be delimited by FRAME plus newline, found delimter was: '" + frameDelimStr + "'");
                }
                this.mediaFileChannel.read(dataY);
                this.mediaFileChannel.read(dataU);
                this.mediaFileChannel.read(dataV);
                return new VideoFrame(buffer, 0, captureTimeNs);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override // org.webrtc.FileVideoCapturer.VideoReader
        public void close() {
            try {
                this.mediaFile.close();
            } catch (IOException e) {
                Logging.e(TAG, "Problem closing file", e);
            }
        }
    }

    public FileVideoCapturer(String inputFile) throws IOException {
        try {
            this.videoReader = new VideoReaderY4M(inputFile);
        } catch (IOException e) {
            Logging.d(TAG, "Could not open video file: " + inputFile);
            throw e;
        }
    }

    public void tick() {
        VideoFrame videoFrame = this.videoReader.getNextFrame();
        this.capturerObserver.onFrameCaptured(videoFrame);
        videoFrame.release();
    }

    @Override // org.webrtc.VideoCapturer
    public void initialize(SurfaceTextureHelper surfaceTextureHelper, Context applicationContext, CapturerObserver capturerObserver) {
        this.capturerObserver = capturerObserver;
    }

    @Override // org.webrtc.VideoCapturer
    public void startCapture(int width, int height, int framerate) {
        this.timer.schedule(this.tickTask, 0L, 1000 / framerate);
    }

    @Override // org.webrtc.VideoCapturer
    public void stopCapture() throws InterruptedException {
        this.timer.cancel();
    }

    @Override // org.webrtc.VideoCapturer
    public void changeCaptureFormat(int width, int height, int framerate) {
    }

    @Override // org.webrtc.VideoCapturer
    public void dispose() {
        this.videoReader.close();
    }

    @Override // org.webrtc.VideoCapturer
    public boolean isScreencast() {
        return false;
    }
}
