package org.telegram.messenger.voip;

import android.media.AudioRecord;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;
import android.text.TextUtils;
import java.nio.ByteBuffer;
import java.util.regex.Pattern;
/* loaded from: classes4.dex */
public class AudioRecordJNI {
    private AcousticEchoCanceler aec;
    private AutomaticGainControl agc;
    private AudioRecord audioRecord;
    private ByteBuffer buffer;
    private int bufferSize;
    private long nativeInst;
    private boolean needResampling = false;
    private NoiseSuppressor ns;
    private boolean running;
    private Thread thread;

    private native void nativeCallback(ByteBuffer byteBuffer);

    public AudioRecordJNI(long ptr) {
        this.nativeInst = ptr;
    }

    private int getBufferSize(int min, int sampleRate) {
        return Math.max(AudioRecord.getMinBufferSize(sampleRate, 16, 2), min);
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:24:0x004d -> B:57:0x0052). Please submit an issue!!! */
    public void init(int sampleRate, int bitsPerSample, int channels, int bufferSize) {
        if (this.audioRecord != null) {
            throw new IllegalStateException("already inited");
        }
        this.bufferSize = bufferSize;
        boolean res = tryInit(7, 48000);
        boolean z = true;
        if (!res) {
            res = tryInit(1, 48000);
        }
        if (!res) {
            res = tryInit(7, 44100);
        }
        if (!res) {
            res = tryInit(1, 44100);
        }
        if (!res) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 16) {
            try {
                if (AutomaticGainControl.isAvailable()) {
                    AutomaticGainControl create = AutomaticGainControl.create(this.audioRecord.getAudioSessionId());
                    this.agc = create;
                    if (create != null) {
                        create.setEnabled(false);
                    }
                } else {
                    VLog.w("AutomaticGainControl is not available on this device :(");
                }
            } catch (Throwable x) {
                VLog.e("error creating AutomaticGainControl", x);
            }
            try {
                if (NoiseSuppressor.isAvailable()) {
                    NoiseSuppressor create2 = NoiseSuppressor.create(this.audioRecord.getAudioSessionId());
                    this.ns = create2;
                    if (create2 != null) {
                        create2.setEnabled(Instance.getGlobalServerConfig().useSystemNs && isGoodAudioEffect(this.ns));
                    }
                } else {
                    VLog.w("NoiseSuppressor is not available on this device :(");
                }
            } catch (Throwable x2) {
                VLog.e("error creating NoiseSuppressor", x2);
            }
            try {
                if (AcousticEchoCanceler.isAvailable()) {
                    AcousticEchoCanceler create3 = AcousticEchoCanceler.create(this.audioRecord.getAudioSessionId());
                    this.aec = create3;
                    if (create3 != null) {
                        if (!Instance.getGlobalServerConfig().useSystemAec || !isGoodAudioEffect(this.aec)) {
                            z = false;
                        }
                        create3.setEnabled(z);
                    }
                } else {
                    VLog.w("AcousticEchoCanceler is not available on this device");
                }
            } catch (Throwable x3) {
                VLog.e("error creating AcousticEchoCanceler", x3);
            }
        }
        this.buffer = ByteBuffer.allocateDirect(bufferSize);
    }

    private boolean tryInit(int source, int sampleRate) {
        AudioRecord audioRecord = this.audioRecord;
        if (audioRecord != null) {
            try {
                audioRecord.release();
            } catch (Exception e) {
            }
        }
        VLog.i("Trying to initialize AudioRecord with source=" + source + " and sample rate=" + sampleRate);
        int size = getBufferSize(this.bufferSize, 48000);
        try {
            this.audioRecord = new AudioRecord(source, sampleRate, 16, 2, size);
        } catch (Exception x) {
            VLog.e("AudioRecord init failed!", x);
        }
        this.needResampling = sampleRate != 48000;
        AudioRecord audioRecord2 = this.audioRecord;
        return audioRecord2 != null && audioRecord2.getState() == 1;
    }

    public void stop() {
        try {
            AudioRecord audioRecord = this.audioRecord;
            if (audioRecord != null) {
                audioRecord.stop();
            }
        } catch (Exception e) {
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
        AudioRecord audioRecord = this.audioRecord;
        if (audioRecord != null) {
            audioRecord.release();
            this.audioRecord = null;
        }
        AutomaticGainControl automaticGainControl = this.agc;
        if (automaticGainControl != null) {
            automaticGainControl.release();
            this.agc = null;
        }
        NoiseSuppressor noiseSuppressor = this.ns;
        if (noiseSuppressor != null) {
            noiseSuppressor.release();
            this.ns = null;
        }
        AcousticEchoCanceler acousticEchoCanceler = this.aec;
        if (acousticEchoCanceler != null) {
            acousticEchoCanceler.release();
            this.aec = null;
        }
    }

    public boolean start() {
        AudioRecord audioRecord = this.audioRecord;
        if (audioRecord == null || audioRecord.getState() != 1) {
            return false;
        }
        try {
            if (this.thread == null) {
                AudioRecord audioRecord2 = this.audioRecord;
                if (audioRecord2 == null) {
                    return false;
                }
                audioRecord2.startRecording();
                startThread();
            } else {
                this.audioRecord.startRecording();
            }
            return true;
        } catch (Exception x) {
            VLog.e("Error initializing AudioRecord", x);
            return false;
        }
    }

    private void startThread() {
        if (this.thread != null) {
            throw new IllegalStateException("thread already started");
        }
        this.running = true;
        final ByteBuffer tmpBuf = this.needResampling ? ByteBuffer.allocateDirect(1764) : null;
        Thread thread = new Thread(new Runnable() { // from class: org.telegram.messenger.voip.AudioRecordJNI$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AudioRecordJNI.this.m1266lambda$startThread$0$orgtelegrammessengervoipAudioRecordJNI(tmpBuf);
            }
        });
        this.thread = thread;
        thread.start();
    }

    /* renamed from: lambda$startThread$0$org-telegram-messenger-voip-AudioRecordJNI */
    public /* synthetic */ void m1266lambda$startThread$0$orgtelegrammessengervoipAudioRecordJNI(ByteBuffer tmpBuf) {
        while (this.running) {
            try {
                if (!this.needResampling) {
                    this.audioRecord.read(this.buffer, 1920);
                } else {
                    this.audioRecord.read(tmpBuf, 1764);
                    Resampler.convert44to48(tmpBuf, this.buffer);
                }
            } catch (Exception e) {
                VLog.e(e);
            }
            if (!this.running) {
                this.audioRecord.stop();
                break;
            }
            nativeCallback(this.buffer);
        }
        VLog.i("audiorecord thread exits");
    }

    public int getEnabledEffectsMask() {
        int r = 0;
        AcousticEchoCanceler acousticEchoCanceler = this.aec;
        if (acousticEchoCanceler != null && acousticEchoCanceler.getEnabled()) {
            r = 0 | 1;
        }
        NoiseSuppressor noiseSuppressor = this.ns;
        if (noiseSuppressor != null && noiseSuppressor.getEnabled()) {
            return r | 2;
        }
        return r;
    }

    private static Pattern makeNonEmptyRegex(String configKey) {
        String r = Instance.getGlobalServerConfig().getString(configKey);
        if (!TextUtils.isEmpty(r)) {
            try {
                return Pattern.compile(r);
            } catch (Exception x) {
                VLog.e(x);
                return null;
            }
        }
        return null;
    }

    private static boolean isGoodAudioEffect(AudioEffect effect) {
        Pattern globalImpl = makeNonEmptyRegex("adsp_good_impls");
        Pattern globalName = makeNonEmptyRegex("adsp_good_names");
        AudioEffect.Descriptor desc = effect.getDescriptor();
        VLog.d(effect.getClass().getSimpleName() + ": implementor=" + desc.implementor + ", name=" + desc.name);
        if (globalImpl != null && globalImpl.matcher(desc.implementor).find()) {
            return true;
        }
        if (globalName != null && globalName.matcher(desc.name).find()) {
            return true;
        }
        if (effect instanceof AcousticEchoCanceler) {
            Pattern impl = makeNonEmptyRegex("aaec_good_impls");
            Pattern name = makeNonEmptyRegex("aaec_good_names");
            if (impl != null && impl.matcher(desc.implementor).find()) {
                return true;
            }
            if (name != null && name.matcher(desc.name).find()) {
                return true;
            }
        }
        if (effect instanceof NoiseSuppressor) {
            Pattern impl2 = makeNonEmptyRegex("ans_good_impls");
            Pattern name2 = makeNonEmptyRegex("ans_good_names");
            if (impl2 != null && impl2.matcher(desc.implementor).find()) {
                return true;
            }
            if (name2 != null && name2.matcher(desc.name).find()) {
                return true;
            }
            return false;
        }
        return false;
    }
}
