package org.telegram.messenger.voip;

import android.media.AudioTrack;
import java.nio.ByteBuffer;
/* loaded from: classes4.dex */
public class AudioTrackJNI {
    private AudioTrack audioTrack;
    private byte[] buffer = new byte[1920];
    private long nativeInst;
    private boolean needResampling;
    private boolean running;
    private Thread thread;

    private native void nativeCallback(byte[] bArr);

    public AudioTrackJNI(long ptr) {
        this.nativeInst = ptr;
    }

    private int getBufferSize(int min, int sampleRate) {
        return Math.max(AudioTrack.getMinBufferSize(sampleRate, 4, 2), min);
    }

    public void init(int sampleRate, int bitsPerSample, int channels, int bufferSize) {
        if (this.audioTrack == null) {
            AudioTrack audioTrack = new AudioTrack(0, 48000, channels == 1 ? 4 : 12, 2, getBufferSize(bufferSize, 48000), 1);
            this.audioTrack = audioTrack;
            if (audioTrack.getState() != 1) {
                VLog.w("Error initializing AudioTrack with 48k, trying 44.1k with resampling");
                try {
                    this.audioTrack.release();
                } catch (Throwable th) {
                }
                int size = getBufferSize(bufferSize * 6, 44100);
                VLog.d("buffer size: " + size);
                this.audioTrack = new AudioTrack(0, 44100, channels == 1 ? 4 : 12, 2, size, 1);
                this.needResampling = true;
                return;
            }
            return;
        }
        throw new IllegalStateException("already inited");
    }

    public void stop() {
        AudioTrack audioTrack = this.audioTrack;
        if (audioTrack != null) {
            try {
                audioTrack.stop();
            } catch (Exception e) {
            }
        }
    }

    public void release() {
        this.running = false;
        Thread thread = this.thread;
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                VLog.e(e);
            }
            this.thread = null;
        }
        AudioTrack audioTrack = this.audioTrack;
        if (audioTrack != null) {
            audioTrack.release();
            this.audioTrack = null;
        }
    }

    public void start() {
        if (this.thread == null) {
            startThread();
        } else {
            this.audioTrack.play();
        }
    }

    private void startThread() {
        if (this.thread != null) {
            throw new IllegalStateException("thread already started");
        }
        this.running = true;
        Thread thread = new Thread(new Runnable() { // from class: org.telegram.messenger.voip.AudioTrackJNI$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AudioTrackJNI.this.m1267lambda$startThread$0$orgtelegrammessengervoipAudioTrackJNI();
            }
        });
        this.thread = thread;
        thread.start();
    }

    /* renamed from: lambda$startThread$0$org-telegram-messenger-voip-AudioTrackJNI */
    public /* synthetic */ void m1267lambda$startThread$0$orgtelegrammessengervoipAudioTrackJNI() {
        try {
            this.audioTrack.play();
            ByteBuffer tmp44 = null;
            ByteBuffer tmp48 = this.needResampling ? ByteBuffer.allocateDirect(1920) : null;
            if (this.needResampling) {
                tmp44 = ByteBuffer.allocateDirect(1764);
            }
            while (this.running) {
                try {
                    if (this.needResampling) {
                        nativeCallback(this.buffer);
                        tmp48.rewind();
                        tmp48.put(this.buffer);
                        Resampler.convert48to44(tmp48, tmp44);
                        tmp44.rewind();
                        tmp44.get(this.buffer, 0, 1764);
                        this.audioTrack.write(this.buffer, 0, 1764);
                    } else {
                        nativeCallback(this.buffer);
                        this.audioTrack.write(this.buffer, 0, 1920);
                    }
                } catch (Exception e) {
                    VLog.e(e);
                }
                if (!this.running) {
                    this.audioTrack.stop();
                    break;
                }
                continue;
            }
            VLog.i("audiotrack thread exits");
        } catch (Exception x) {
            VLog.e("error starting AudioTrack", x);
        }
    }
}
