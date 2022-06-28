package com.google.android.exoplayer2.audio;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.ConditionVariable;
import android.os.SystemClock;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.audio.AudioTrackPositionTracker;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.wearable.WearableStatusCodes;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
/* loaded from: classes3.dex */
public final class DefaultAudioSink implements AudioSink {
    private static final int AC3_BUFFER_MULTIPLICATION_FACTOR = 2;
    private static final int BUFFER_MULTIPLICATION_FACTOR = 4;
    private static final int ERROR_BAD_VALUE = -2;
    private static final long MAX_BUFFER_DURATION_US = 750000;
    private static final long MIN_BUFFER_DURATION_US = 250000;
    private static final int MODE_STATIC = 0;
    private static final int MODE_STREAM = 1;
    private static final long PASSTHROUGH_BUFFER_DURATION_US = 250000;
    private static final int START_IN_SYNC = 1;
    private static final int START_NEED_SYNC = 2;
    private static final int START_NOT_SET = 0;
    private static final int STATE_INITIALIZED = 1;
    private static final String TAG = "AudioTrack";
    private static final int WRITE_NON_BLOCKING = 1;
    public static boolean enablePreV21AudioSessionWorkaround = false;
    public static boolean failOnSpuriousAudioTimestamp = false;
    private AudioProcessor[] activeAudioProcessors;
    private PlaybackParameters afterDrainPlaybackParameters;
    private AudioAttributes audioAttributes;
    private final AudioCapabilities audioCapabilities;
    private final AudioProcessorChain audioProcessorChain;
    private int audioSessionId;
    private AudioTrack audioTrack;
    private final AudioTrackPositionTracker audioTrackPositionTracker;
    private AuxEffectInfo auxEffectInfo;
    private ByteBuffer avSyncHeader;
    private int bytesUntilNextAvSync;
    private final ChannelMappingAudioProcessor channelMappingAudioProcessor;
    private Configuration configuration;
    private int drainingAudioProcessorIndex;
    private final boolean enableFloatOutput;
    private int framesPerEncodedSample;
    private boolean handledEndOfStream;
    private ByteBuffer inputBuffer;
    private AudioTrack keepSessionIdAudioTrack;
    private long lastFeedElapsedRealtimeMs;
    private AudioSink.Listener listener;
    private ByteBuffer outputBuffer;
    private ByteBuffer[] outputBuffers;
    private Configuration pendingConfiguration;
    private PlaybackParameters playbackParameters;
    private final ArrayDeque<PlaybackParametersCheckpoint> playbackParametersCheckpoints;
    private long playbackParametersOffsetUs;
    private long playbackParametersPositionUs;
    private boolean playing;
    private byte[] preV21OutputBuffer;
    private int preV21OutputBufferOffset;
    private final ConditionVariable releasingConditionVariable;
    private int startMediaTimeState;
    private long startMediaTimeUs;
    private boolean stoppedAudioTrack;
    private long submittedEncodedFrames;
    private long submittedPcmBytes;
    private final AudioProcessor[] toFloatPcmAvailableAudioProcessors;
    private final AudioProcessor[] toIntPcmAvailableAudioProcessors;
    private final TrimmingAudioProcessor trimmingAudioProcessor;
    private boolean tunneling;
    private float volume;
    private long writtenEncodedFrames;
    private long writtenPcmBytes;

    /* loaded from: classes3.dex */
    public interface AudioProcessorChain {
        PlaybackParameters applyPlaybackParameters(PlaybackParameters playbackParameters);

        AudioProcessor[] getAudioProcessors();

        long getMediaDuration(long j);

        long getSkippedOutputFrameCount();
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface StartMediaTimeState {
    }

    /* loaded from: classes3.dex */
    public static final class InvalidAudioTrackTimestampException extends RuntimeException {
        private InvalidAudioTrackTimestampException(String message) {
            super(message);
        }
    }

    /* loaded from: classes3.dex */
    public static class DefaultAudioProcessorChain implements AudioProcessorChain {
        private final AudioProcessor[] audioProcessors;
        private final SilenceSkippingAudioProcessor silenceSkippingAudioProcessor;
        private final SonicAudioProcessor sonicAudioProcessor;

        public DefaultAudioProcessorChain(AudioProcessor... audioProcessors) {
            this(audioProcessors, new SilenceSkippingAudioProcessor(), new SonicAudioProcessor());
        }

        public DefaultAudioProcessorChain(AudioProcessor[] audioProcessors, SilenceSkippingAudioProcessor silenceSkippingAudioProcessor, SonicAudioProcessor sonicAudioProcessor) {
            AudioProcessor[] audioProcessorArr = new AudioProcessor[audioProcessors.length + 2];
            this.audioProcessors = audioProcessorArr;
            System.arraycopy(audioProcessors, 0, audioProcessorArr, 0, audioProcessors.length);
            this.silenceSkippingAudioProcessor = silenceSkippingAudioProcessor;
            this.sonicAudioProcessor = sonicAudioProcessor;
            audioProcessorArr[audioProcessors.length] = silenceSkippingAudioProcessor;
            audioProcessorArr[audioProcessors.length + 1] = sonicAudioProcessor;
        }

        @Override // com.google.android.exoplayer2.audio.DefaultAudioSink.AudioProcessorChain
        public AudioProcessor[] getAudioProcessors() {
            return this.audioProcessors;
        }

        @Override // com.google.android.exoplayer2.audio.DefaultAudioSink.AudioProcessorChain
        public PlaybackParameters applyPlaybackParameters(PlaybackParameters playbackParameters) {
            this.silenceSkippingAudioProcessor.setEnabled(playbackParameters.skipSilence);
            return new PlaybackParameters(this.sonicAudioProcessor.setSpeed(playbackParameters.speed), this.sonicAudioProcessor.setPitch(playbackParameters.pitch), playbackParameters.skipSilence);
        }

        @Override // com.google.android.exoplayer2.audio.DefaultAudioSink.AudioProcessorChain
        public long getMediaDuration(long playoutDuration) {
            return this.sonicAudioProcessor.scaleDurationForSpeedup(playoutDuration);
        }

        @Override // com.google.android.exoplayer2.audio.DefaultAudioSink.AudioProcessorChain
        public long getSkippedOutputFrameCount() {
            return this.silenceSkippingAudioProcessor.getSkippedFrames();
        }
    }

    public DefaultAudioSink(AudioCapabilities audioCapabilities, AudioProcessor[] audioProcessors) {
        this(audioCapabilities, audioProcessors, false);
    }

    public DefaultAudioSink(AudioCapabilities audioCapabilities, AudioProcessor[] audioProcessors, boolean enableFloatOutput) {
        this(audioCapabilities, new DefaultAudioProcessorChain(audioProcessors), enableFloatOutput);
    }

    public DefaultAudioSink(AudioCapabilities audioCapabilities, AudioProcessorChain audioProcessorChain, boolean enableFloatOutput) {
        this.audioCapabilities = audioCapabilities;
        this.audioProcessorChain = (AudioProcessorChain) Assertions.checkNotNull(audioProcessorChain);
        this.enableFloatOutput = enableFloatOutput;
        this.releasingConditionVariable = new ConditionVariable(true);
        this.audioTrackPositionTracker = new AudioTrackPositionTracker(new PositionTrackerListener());
        ChannelMappingAudioProcessor channelMappingAudioProcessor = new ChannelMappingAudioProcessor();
        this.channelMappingAudioProcessor = channelMappingAudioProcessor;
        TrimmingAudioProcessor trimmingAudioProcessor = new TrimmingAudioProcessor();
        this.trimmingAudioProcessor = trimmingAudioProcessor;
        ArrayList<AudioProcessor> toIntPcmAudioProcessors = new ArrayList<>();
        Collections.addAll(toIntPcmAudioProcessors, new ResamplingAudioProcessor(), channelMappingAudioProcessor, trimmingAudioProcessor);
        Collections.addAll(toIntPcmAudioProcessors, audioProcessorChain.getAudioProcessors());
        this.toIntPcmAvailableAudioProcessors = (AudioProcessor[]) toIntPcmAudioProcessors.toArray(new AudioProcessor[0]);
        this.toFloatPcmAvailableAudioProcessors = new AudioProcessor[]{new FloatResamplingAudioProcessor()};
        this.volume = 1.0f;
        this.startMediaTimeState = 0;
        this.audioAttributes = AudioAttributes.DEFAULT;
        this.audioSessionId = 0;
        this.auxEffectInfo = new AuxEffectInfo(0, 0.0f);
        this.playbackParameters = PlaybackParameters.DEFAULT;
        this.drainingAudioProcessorIndex = -1;
        this.activeAudioProcessors = new AudioProcessor[0];
        this.outputBuffers = new ByteBuffer[0];
        this.playbackParametersCheckpoints = new ArrayDeque<>();
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void setListener(AudioSink.Listener listener) {
        this.listener = listener;
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public boolean supportsOutput(int channelCount, int encoding) {
        if (Util.isEncodingLinearPcm(encoding)) {
            return encoding != 4 || Util.SDK_INT >= 21;
        }
        AudioCapabilities audioCapabilities = this.audioCapabilities;
        return audioCapabilities != null && audioCapabilities.supportsEncoding(encoding) && (channelCount == -1 || channelCount <= this.audioCapabilities.getMaxChannelCount());
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public long getCurrentPositionUs(boolean sourceEnded) {
        if (!isInitialized() || this.startMediaTimeState == 0) {
            return Long.MIN_VALUE;
        }
        long positionUs = this.audioTrackPositionTracker.getCurrentPositionUs(sourceEnded);
        return this.startMediaTimeUs + applySkipping(applySpeedup(Math.min(positionUs, this.configuration.framesToDurationUs(getWrittenFrames()))));
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void configure(int inputEncoding, int inputChannelCount, int inputSampleRate, int specifiedBufferSize, int[] outputChannels, int trimStartFrames, int trimEndFrames) throws AudioSink.ConfigurationException {
        int[] outputChannels2;
        int channelCount;
        int channelCount2;
        int sampleRate;
        if (Util.SDK_INT < 21 && inputChannelCount == 8 && outputChannels == null) {
            int[] outputChannels3 = new int[6];
            for (int i = 0; i < outputChannels3.length; i++) {
                outputChannels3[i] = i;
            }
            outputChannels2 = outputChannels3;
        } else {
            outputChannels2 = outputChannels;
        }
        boolean isInputPcm = Util.isEncodingLinearPcm(inputEncoding);
        boolean useFloatOutput = this.enableFloatOutput && supportsOutput(inputChannelCount, 4) && Util.isEncodingHighResolutionPcm(inputEncoding);
        AudioProcessor[] availableAudioProcessors = useFloatOutput ? this.toFloatPcmAvailableAudioProcessors : this.toIntPcmAvailableAudioProcessors;
        if (!isInputPcm) {
            sampleRate = inputSampleRate;
            channelCount = inputChannelCount;
            channelCount2 = inputEncoding;
        } else {
            this.trimmingAudioProcessor.setTrimFrameCount(trimStartFrames, trimEndFrames);
            this.channelMappingAudioProcessor.setChannelMap(outputChannels2);
            AudioProcessor.AudioFormat outputFormat = new AudioProcessor.AudioFormat(inputSampleRate, inputChannelCount, inputEncoding);
            AudioProcessor.AudioFormat outputFormat2 = outputFormat;
            for (AudioProcessor audioProcessor : availableAudioProcessors) {
                try {
                    AudioProcessor.AudioFormat nextFormat = audioProcessor.configure(outputFormat2);
                    if (audioProcessor.isActive()) {
                        outputFormat2 = nextFormat;
                    }
                } catch (AudioProcessor.UnhandledAudioFormatException e) {
                    throw new AudioSink.ConfigurationException(e);
                }
            }
            int sampleRate2 = outputFormat2.sampleRate;
            int channelCount3 = outputFormat2.channelCount;
            int encoding = outputFormat2.encoding;
            sampleRate = sampleRate2;
            channelCount = channelCount3;
            channelCount2 = encoding;
        }
        int outputChannelConfig = getChannelConfig(channelCount, isInputPcm);
        if (outputChannelConfig == 0) {
            int sampleRate3 = channelCount;
            throw new AudioSink.ConfigurationException("Unsupported channel count: " + sampleRate3);
        }
        int inputPcmFrameSize = isInputPcm ? Util.getPcmFrameSize(inputEncoding, inputChannelCount) : -1;
        int outputPcmFrameSize = isInputPcm ? Util.getPcmFrameSize(channelCount2, channelCount) : -1;
        boolean canApplyPlaybackParameters = isInputPcm && !useFloatOutput;
        int encoding2 = channelCount2;
        Configuration pendingConfiguration = new Configuration(isInputPcm, inputPcmFrameSize, inputSampleRate, outputPcmFrameSize, sampleRate, outputChannelConfig, encoding2, specifiedBufferSize, isInputPcm, canApplyPlaybackParameters, availableAudioProcessors);
        if (isInitialized()) {
            this.pendingConfiguration = pendingConfiguration;
        } else {
            this.configuration = pendingConfiguration;
        }
    }

    private void setupAudioProcessors() {
        AudioProcessor[] audioProcessors = this.configuration.availableAudioProcessors;
        ArrayList<AudioProcessor> newAudioProcessors = new ArrayList<>();
        for (AudioProcessor audioProcessor : audioProcessors) {
            if (audioProcessor.isActive()) {
                newAudioProcessors.add(audioProcessor);
            } else {
                audioProcessor.flush();
            }
        }
        int count = newAudioProcessors.size();
        this.activeAudioProcessors = (AudioProcessor[]) newAudioProcessors.toArray(new AudioProcessor[count]);
        this.outputBuffers = new ByteBuffer[count];
        flushAudioProcessors();
    }

    private void flushAudioProcessors() {
        int i = 0;
        while (true) {
            AudioProcessor[] audioProcessorArr = this.activeAudioProcessors;
            if (i < audioProcessorArr.length) {
                AudioProcessor audioProcessor = audioProcessorArr[i];
                audioProcessor.flush();
                this.outputBuffers[i] = audioProcessor.getOutput();
                i++;
            } else {
                return;
            }
        }
    }

    private void initialize(long presentationTimeUs) throws AudioSink.InitializationException {
        this.releasingConditionVariable.block();
        AudioTrack buildAudioTrack = ((Configuration) Assertions.checkNotNull(this.configuration)).buildAudioTrack(this.tunneling, this.audioAttributes, this.audioSessionId);
        this.audioTrack = buildAudioTrack;
        int audioSessionId = buildAudioTrack.getAudioSessionId();
        if (enablePreV21AudioSessionWorkaround && Util.SDK_INT < 21) {
            AudioTrack audioTrack = this.keepSessionIdAudioTrack;
            if (audioTrack != null && audioSessionId != audioTrack.getAudioSessionId()) {
                releaseKeepSessionIdAudioTrack();
            }
            if (this.keepSessionIdAudioTrack == null) {
                this.keepSessionIdAudioTrack = initializeKeepSessionIdAudioTrack(audioSessionId);
            }
        }
        if (this.audioSessionId != audioSessionId) {
            this.audioSessionId = audioSessionId;
            AudioSink.Listener listener = this.listener;
            if (listener != null) {
                listener.onAudioSessionId(audioSessionId);
            }
        }
        applyPlaybackParameters(this.playbackParameters, presentationTimeUs);
        this.audioTrackPositionTracker.setAudioTrack(this.audioTrack, this.configuration.outputEncoding, this.configuration.outputPcmFrameSize, this.configuration.bufferSize);
        setVolumeInternal();
        if (this.auxEffectInfo.effectId != 0) {
            this.audioTrack.attachAuxEffect(this.auxEffectInfo.effectId);
            this.audioTrack.setAuxEffectSendLevel(this.auxEffectInfo.sendLevel);
        }
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void play() {
        this.playing = true;
        if (isInitialized()) {
            this.audioTrackPositionTracker.start();
            this.audioTrack.play();
        }
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void handleDiscontinuity() {
        if (this.startMediaTimeState == 1) {
            this.startMediaTimeState = 2;
        }
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public boolean handleBuffer(ByteBuffer buffer, long presentationTimeUs) throws AudioSink.InitializationException, AudioSink.WriteException {
        String str;
        ByteBuffer byteBuffer = this.inputBuffer;
        Assertions.checkArgument(byteBuffer == null || buffer == byteBuffer);
        if (this.pendingConfiguration != null) {
            if (!drainAudioProcessorsToEndOfStream()) {
                return false;
            }
            if (this.pendingConfiguration.canReuseAudioTrack(this.configuration)) {
                this.configuration = this.pendingConfiguration;
                this.pendingConfiguration = null;
            } else {
                playPendingData();
                if (hasPendingData()) {
                    return false;
                }
                flush();
            }
            applyPlaybackParameters(this.playbackParameters, presentationTimeUs);
        }
        if (!isInitialized()) {
            initialize(presentationTimeUs);
            if (this.playing) {
                play();
            }
        }
        if (!this.audioTrackPositionTracker.mayHandleBuffer(getWrittenFrames())) {
            return false;
        }
        if (this.inputBuffer != null) {
            str = TAG;
        } else if (!buffer.hasRemaining()) {
            return true;
        } else {
            if (!this.configuration.isInputPcm && this.framesPerEncodedSample == 0) {
                int framesPerEncodedSample = getFramesPerEncodedSample(this.configuration.outputEncoding, buffer);
                this.framesPerEncodedSample = framesPerEncodedSample;
                if (framesPerEncodedSample == 0) {
                    return true;
                }
            }
            if (this.afterDrainPlaybackParameters != null) {
                if (!drainAudioProcessorsToEndOfStream()) {
                    return false;
                }
                PlaybackParameters newPlaybackParameters = this.afterDrainPlaybackParameters;
                this.afterDrainPlaybackParameters = null;
                applyPlaybackParameters(newPlaybackParameters, presentationTimeUs);
            }
            if (this.startMediaTimeState == 0) {
                this.startMediaTimeUs = Math.max(0L, presentationTimeUs);
                this.startMediaTimeState = 1;
                str = TAG;
            } else {
                long expectedPresentationTimeUs = this.startMediaTimeUs + this.configuration.inputFramesToDurationUs(getSubmittedFrames() - this.trimmingAudioProcessor.getTrimmedFrameCount());
                if (this.startMediaTimeState == 1 && Math.abs(expectedPresentationTimeUs - presentationTimeUs) > 200000) {
                    Log.e(TAG, "Discontinuity detected [expected " + expectedPresentationTimeUs + ", got " + presentationTimeUs + "]");
                    this.startMediaTimeState = 2;
                }
                if (this.startMediaTimeState != 2) {
                    str = TAG;
                } else {
                    long adjustmentUs = presentationTimeUs - expectedPresentationTimeUs;
                    str = TAG;
                    this.startMediaTimeUs += adjustmentUs;
                    this.startMediaTimeState = 1;
                    AudioSink.Listener listener = this.listener;
                    if (listener != null && adjustmentUs != 0) {
                        listener.onPositionDiscontinuity();
                    }
                }
            }
            if (this.configuration.isInputPcm) {
                this.submittedPcmBytes += buffer.remaining();
            } else {
                this.submittedEncodedFrames += this.framesPerEncodedSample;
            }
            this.inputBuffer = buffer;
        }
        if (!this.configuration.processingEnabled) {
            writeBuffer(this.inputBuffer, presentationTimeUs);
        } else {
            processBuffers(presentationTimeUs);
        }
        if (!this.inputBuffer.hasRemaining()) {
            this.inputBuffer = null;
            return true;
        } else if (!this.audioTrackPositionTracker.isStalled(getWrittenFrames())) {
            return false;
        } else {
            Log.w(str, "Resetting stalled audio track");
            flush();
            return true;
        }
    }

    private void processBuffers(long avSyncPresentationTimeUs) throws AudioSink.WriteException {
        ByteBuffer input;
        int count = this.activeAudioProcessors.length;
        int index = count;
        while (index >= 0) {
            if (index > 0) {
                input = this.outputBuffers[index - 1];
            } else {
                input = this.inputBuffer;
                if (input == null) {
                    input = AudioProcessor.EMPTY_BUFFER;
                }
            }
            if (index == count) {
                writeBuffer(input, avSyncPresentationTimeUs);
            } else {
                AudioProcessor audioProcessor = this.activeAudioProcessors[index];
                audioProcessor.queueInput(input);
                ByteBuffer output = audioProcessor.getOutput();
                this.outputBuffers[index] = output;
                if (output.hasRemaining()) {
                    index++;
                }
            }
            if (input.hasRemaining()) {
                return;
            }
            index--;
        }
    }

    private void writeBuffer(ByteBuffer buffer, long avSyncPresentationTimeUs) throws AudioSink.WriteException {
        if (!buffer.hasRemaining()) {
            return;
        }
        ByteBuffer byteBuffer = this.outputBuffer;
        boolean z = true;
        if (byteBuffer != null) {
            Assertions.checkArgument(byteBuffer == buffer);
        } else {
            this.outputBuffer = buffer;
            if (Util.SDK_INT < 21) {
                int bytesRemaining = buffer.remaining();
                byte[] bArr = this.preV21OutputBuffer;
                if (bArr == null || bArr.length < bytesRemaining) {
                    this.preV21OutputBuffer = new byte[bytesRemaining];
                }
                int originalPosition = buffer.position();
                buffer.get(this.preV21OutputBuffer, 0, bytesRemaining);
                buffer.position(originalPosition);
                this.preV21OutputBufferOffset = 0;
            }
        }
        int bytesRemaining2 = buffer.remaining();
        int bytesWritten = 0;
        if (Util.SDK_INT < 21) {
            int bytesToWrite = this.audioTrackPositionTracker.getAvailableBufferSize(this.writtenPcmBytes);
            if (bytesToWrite > 0) {
                bytesWritten = this.audioTrack.write(this.preV21OutputBuffer, this.preV21OutputBufferOffset, Math.min(bytesRemaining2, bytesToWrite));
                if (bytesWritten > 0) {
                    this.preV21OutputBufferOffset += bytesWritten;
                    buffer.position(buffer.position() + bytesWritten);
                }
            }
        } else if (this.tunneling) {
            if (avSyncPresentationTimeUs == C.TIME_UNSET) {
                z = false;
            }
            Assertions.checkState(z);
            bytesWritten = writeNonBlockingWithAvSyncV21(this.audioTrack, buffer, bytesRemaining2, avSyncPresentationTimeUs);
        } else {
            bytesWritten = writeNonBlockingV21(this.audioTrack, buffer, bytesRemaining2);
        }
        this.lastFeedElapsedRealtimeMs = SystemClock.elapsedRealtime();
        if (bytesWritten < 0) {
            throw new AudioSink.WriteException(bytesWritten);
        }
        if (this.configuration.isInputPcm) {
            this.writtenPcmBytes += bytesWritten;
        }
        if (bytesWritten == bytesRemaining2) {
            if (!this.configuration.isInputPcm) {
                this.writtenEncodedFrames += this.framesPerEncodedSample;
            }
            this.outputBuffer = null;
        }
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void playToEndOfStream() throws AudioSink.WriteException {
        if (!this.handledEndOfStream && isInitialized() && drainAudioProcessorsToEndOfStream()) {
            playPendingData();
            this.handledEndOfStream = true;
        }
    }

    private boolean drainAudioProcessorsToEndOfStream() throws AudioSink.WriteException {
        boolean audioProcessorNeedsEndOfStream = false;
        if (this.drainingAudioProcessorIndex == -1) {
            this.drainingAudioProcessorIndex = this.configuration.processingEnabled ? 0 : this.activeAudioProcessors.length;
            audioProcessorNeedsEndOfStream = true;
        }
        while (true) {
            int i = this.drainingAudioProcessorIndex;
            AudioProcessor[] audioProcessorArr = this.activeAudioProcessors;
            if (i < audioProcessorArr.length) {
                AudioProcessor audioProcessor = audioProcessorArr[i];
                if (audioProcessorNeedsEndOfStream) {
                    audioProcessor.queueEndOfStream();
                }
                processBuffers(C.TIME_UNSET);
                if (!audioProcessor.isEnded()) {
                    return false;
                }
                audioProcessorNeedsEndOfStream = true;
                this.drainingAudioProcessorIndex++;
            } else {
                ByteBuffer byteBuffer = this.outputBuffer;
                if (byteBuffer != null) {
                    writeBuffer(byteBuffer, C.TIME_UNSET);
                    if (this.outputBuffer != null) {
                        return false;
                    }
                }
                this.drainingAudioProcessorIndex = -1;
                return true;
            }
        }
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public boolean isEnded() {
        return !isInitialized() || (this.handledEndOfStream && !hasPendingData());
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public boolean hasPendingData() {
        return isInitialized() && this.audioTrackPositionTracker.hasPendingData(getWrittenFrames());
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void setPlaybackParameters(PlaybackParameters playbackParameters) {
        Configuration configuration = this.configuration;
        if (configuration != null && !configuration.canApplyPlaybackParameters) {
            this.playbackParameters = PlaybackParameters.DEFAULT;
            return;
        }
        PlaybackParameters lastSetPlaybackParameters = getPlaybackParameters();
        if (!playbackParameters.equals(lastSetPlaybackParameters)) {
            if (isInitialized()) {
                this.afterDrainPlaybackParameters = playbackParameters;
            } else {
                this.playbackParameters = playbackParameters;
            }
        }
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public PlaybackParameters getPlaybackParameters() {
        PlaybackParameters playbackParameters = this.afterDrainPlaybackParameters;
        if (playbackParameters != null) {
            return playbackParameters;
        }
        if (this.playbackParametersCheckpoints.isEmpty()) {
            return this.playbackParameters;
        }
        return this.playbackParametersCheckpoints.getLast().playbackParameters;
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void setAudioAttributes(AudioAttributes audioAttributes) {
        if (this.audioAttributes.equals(audioAttributes)) {
            return;
        }
        this.audioAttributes = audioAttributes;
        if (this.tunneling) {
            return;
        }
        flush();
        this.audioSessionId = 0;
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void setAudioSessionId(int audioSessionId) {
        if (this.audioSessionId != audioSessionId) {
            this.audioSessionId = audioSessionId;
            flush();
        }
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void setAuxEffectInfo(AuxEffectInfo auxEffectInfo) {
        if (this.auxEffectInfo.equals(auxEffectInfo)) {
            return;
        }
        int effectId = auxEffectInfo.effectId;
        float sendLevel = auxEffectInfo.sendLevel;
        if (this.audioTrack != null) {
            if (this.auxEffectInfo.effectId != effectId) {
                this.audioTrack.attachAuxEffect(effectId);
            }
            if (effectId != 0) {
                this.audioTrack.setAuxEffectSendLevel(sendLevel);
            }
        }
        this.auxEffectInfo = auxEffectInfo;
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void enableTunnelingV21(int tunnelingAudioSessionId) {
        Assertions.checkState(Util.SDK_INT >= 21);
        if (!this.tunneling || this.audioSessionId != tunnelingAudioSessionId) {
            this.tunneling = true;
            this.audioSessionId = tunnelingAudioSessionId;
            flush();
        }
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void disableTunneling() {
        if (this.tunneling) {
            this.tunneling = false;
            this.audioSessionId = 0;
            flush();
        }
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void setVolume(float volume) {
        if (this.volume != volume) {
            this.volume = volume;
            setVolumeInternal();
        }
    }

    private void setVolumeInternal() {
        if (isInitialized()) {
            if (Util.SDK_INT >= 21) {
                setVolumeInternalV21(this.audioTrack, this.volume);
            } else {
                setVolumeInternalV3(this.audioTrack, this.volume);
            }
        }
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void pause() {
        this.playing = false;
        if (isInitialized() && this.audioTrackPositionTracker.pause()) {
            this.audioTrack.pause();
        }
    }

    /* JADX WARN: Type inference failed for: r1v3, types: [com.google.android.exoplayer2.audio.DefaultAudioSink$1] */
    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void flush() {
        if (isInitialized()) {
            this.submittedPcmBytes = 0L;
            this.submittedEncodedFrames = 0L;
            this.writtenPcmBytes = 0L;
            this.writtenEncodedFrames = 0L;
            this.framesPerEncodedSample = 0;
            PlaybackParameters playbackParameters = this.afterDrainPlaybackParameters;
            if (playbackParameters != null) {
                this.playbackParameters = playbackParameters;
                this.afterDrainPlaybackParameters = null;
            } else if (!this.playbackParametersCheckpoints.isEmpty()) {
                this.playbackParameters = this.playbackParametersCheckpoints.getLast().playbackParameters;
            }
            this.playbackParametersCheckpoints.clear();
            this.playbackParametersOffsetUs = 0L;
            this.playbackParametersPositionUs = 0L;
            this.trimmingAudioProcessor.resetTrimmedFrameCount();
            flushAudioProcessors();
            this.inputBuffer = null;
            this.outputBuffer = null;
            this.stoppedAudioTrack = false;
            this.handledEndOfStream = false;
            this.drainingAudioProcessorIndex = -1;
            this.avSyncHeader = null;
            this.bytesUntilNextAvSync = 0;
            this.startMediaTimeState = 0;
            if (this.audioTrackPositionTracker.isPlaying()) {
                this.audioTrack.pause();
            }
            final AudioTrack toRelease = this.audioTrack;
            this.audioTrack = null;
            Configuration configuration = this.pendingConfiguration;
            if (configuration != null) {
                this.configuration = configuration;
                this.pendingConfiguration = null;
            }
            this.audioTrackPositionTracker.reset();
            this.releasingConditionVariable.close();
            new Thread() { // from class: com.google.android.exoplayer2.audio.DefaultAudioSink.1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    try {
                        toRelease.flush();
                        toRelease.release();
                    } finally {
                        DefaultAudioSink.this.releasingConditionVariable.open();
                    }
                }
            }.start();
        }
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void reset() {
        AudioProcessor[] audioProcessorArr;
        AudioProcessor[] audioProcessorArr2;
        flush();
        releaseKeepSessionIdAudioTrack();
        for (AudioProcessor audioProcessor : this.toIntPcmAvailableAudioProcessors) {
            audioProcessor.reset();
        }
        for (AudioProcessor audioProcessor2 : this.toFloatPcmAvailableAudioProcessors) {
            audioProcessor2.reset();
        }
        this.audioSessionId = 0;
        this.playing = false;
    }

    /* JADX WARN: Type inference failed for: r1v1, types: [com.google.android.exoplayer2.audio.DefaultAudioSink$2] */
    private void releaseKeepSessionIdAudioTrack() {
        if (this.keepSessionIdAudioTrack == null) {
            return;
        }
        final AudioTrack toRelease = this.keepSessionIdAudioTrack;
        this.keepSessionIdAudioTrack = null;
        new Thread() { // from class: com.google.android.exoplayer2.audio.DefaultAudioSink.2
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                toRelease.release();
            }
        }.start();
    }

    private void applyPlaybackParameters(PlaybackParameters playbackParameters, long presentationTimeUs) {
        PlaybackParameters newPlaybackParameters;
        if (this.configuration.canApplyPlaybackParameters) {
            newPlaybackParameters = this.audioProcessorChain.applyPlaybackParameters(playbackParameters);
        } else {
            newPlaybackParameters = PlaybackParameters.DEFAULT;
        }
        this.playbackParametersCheckpoints.add(new PlaybackParametersCheckpoint(newPlaybackParameters, Math.max(0L, presentationTimeUs), this.configuration.framesToDurationUs(getWrittenFrames())));
        setupAudioProcessors();
    }

    private long applySpeedup(long positionUs) {
        PlaybackParametersCheckpoint checkpoint = null;
        while (!this.playbackParametersCheckpoints.isEmpty() && positionUs >= this.playbackParametersCheckpoints.getFirst().positionUs) {
            PlaybackParametersCheckpoint checkpoint2 = this.playbackParametersCheckpoints.remove();
            checkpoint = checkpoint2;
        }
        if (checkpoint != null) {
            this.playbackParameters = checkpoint.playbackParameters;
            this.playbackParametersPositionUs = checkpoint.positionUs;
            this.playbackParametersOffsetUs = checkpoint.mediaTimeUs - this.startMediaTimeUs;
        }
        if (this.playbackParameters.speed == 1.0f) {
            return (this.playbackParametersOffsetUs + positionUs) - this.playbackParametersPositionUs;
        }
        if (this.playbackParametersCheckpoints.isEmpty()) {
            return this.playbackParametersOffsetUs + this.audioProcessorChain.getMediaDuration(positionUs - this.playbackParametersPositionUs);
        }
        return this.playbackParametersOffsetUs + Util.getMediaDurationForPlayoutDuration(positionUs - this.playbackParametersPositionUs, this.playbackParameters.speed);
    }

    private long applySkipping(long positionUs) {
        return this.configuration.framesToDurationUs(this.audioProcessorChain.getSkippedOutputFrameCount()) + positionUs;
    }

    private boolean isInitialized() {
        return this.audioTrack != null;
    }

    public long getSubmittedFrames() {
        if (this.configuration.isInputPcm) {
            return this.submittedPcmBytes / this.configuration.inputPcmFrameSize;
        }
        return this.submittedEncodedFrames;
    }

    public long getWrittenFrames() {
        if (this.configuration.isInputPcm) {
            return this.writtenPcmBytes / this.configuration.outputPcmFrameSize;
        }
        return this.writtenEncodedFrames;
    }

    private static AudioTrack initializeKeepSessionIdAudioTrack(int audioSessionId) {
        return new AudioTrack(3, WearableStatusCodes.TARGET_NODE_NOT_CONNECTED, 4, 2, 2, 0, audioSessionId);
    }

    private static int getChannelConfig(int channelCount, boolean isInputPcm) {
        if (Util.SDK_INT <= 28 && !isInputPcm) {
            if (channelCount == 7) {
                channelCount = 8;
            } else if (channelCount == 3 || channelCount == 4 || channelCount == 5) {
                channelCount = 6;
            }
        }
        if (Util.SDK_INT <= 26 && "fugu".equals(Util.DEVICE) && !isInputPcm && channelCount == 1) {
            channelCount = 2;
        }
        return Util.getAudioTrackChannelConfig(channelCount);
    }

    public static int getMaximumEncodedRateBytesPerSecond(int encoding) {
        switch (encoding) {
            case 5:
                return 80000;
            case 6:
            case 18:
                return 768000;
            case 7:
                return 192000;
            case 8:
                return 2250000;
            case 14:
                return 3062500;
            case 17:
                return 336000;
            default:
                throw new IllegalArgumentException();
        }
    }

    private static int getFramesPerEncodedSample(int encoding, ByteBuffer buffer) {
        switch (encoding) {
            case 5:
            case 6:
            case 18:
                return Ac3Util.parseAc3SyncframeAudioSampleCount(buffer);
            case 7:
            case 8:
                return DtsUtil.parseDtsAudioSampleCount(buffer);
            case 9:
                return MpegAudioHeader.getFrameSampleCount(buffer.get(buffer.position()));
            case 10:
            case 11:
            case 12:
            case 13:
            case 15:
            case 16:
            default:
                throw new IllegalStateException("Unexpected audio encoding: " + encoding);
            case 14:
                int syncframeOffset = Ac3Util.findTrueHdSyncframeOffset(buffer);
                if (syncframeOffset == -1) {
                    return 0;
                }
                return Ac3Util.parseTrueHdSyncframeAudioSampleCount(buffer, syncframeOffset) * 16;
            case 17:
                return Ac4Util.parseAc4SyncframeAudioSampleCount(buffer);
        }
    }

    private static int writeNonBlockingV21(AudioTrack audioTrack, ByteBuffer buffer, int size) {
        return audioTrack.write(buffer, size, 1);
    }

    private int writeNonBlockingWithAvSyncV21(AudioTrack audioTrack, ByteBuffer buffer, int size, long presentationTimeUs) {
        if (Util.SDK_INT >= 26) {
            return audioTrack.write(buffer, size, 1, presentationTimeUs * 1000);
        }
        if (this.avSyncHeader == null) {
            ByteBuffer allocate = ByteBuffer.allocate(16);
            this.avSyncHeader = allocate;
            allocate.order(ByteOrder.BIG_ENDIAN);
            this.avSyncHeader.putInt(1431633921);
        }
        if (this.bytesUntilNextAvSync == 0) {
            this.avSyncHeader.putInt(4, size);
            this.avSyncHeader.putLong(8, 1000 * presentationTimeUs);
            this.avSyncHeader.position(0);
            this.bytesUntilNextAvSync = size;
        }
        int avSyncHeaderBytesRemaining = this.avSyncHeader.remaining();
        if (avSyncHeaderBytesRemaining > 0) {
            int result = audioTrack.write(this.avSyncHeader, avSyncHeaderBytesRemaining, 1);
            if (result < 0) {
                this.bytesUntilNextAvSync = 0;
                return result;
            } else if (result < avSyncHeaderBytesRemaining) {
                return 0;
            }
        }
        int result2 = writeNonBlockingV21(audioTrack, buffer, size);
        if (result2 < 0) {
            this.bytesUntilNextAvSync = 0;
            return result2;
        }
        this.bytesUntilNextAvSync -= result2;
        return result2;
    }

    private static void setVolumeInternalV21(AudioTrack audioTrack, float volume) {
        audioTrack.setVolume(volume);
    }

    private static void setVolumeInternalV3(AudioTrack audioTrack, float volume) {
        audioTrack.setStereoVolume(volume, volume);
    }

    private void playPendingData() {
        if (!this.stoppedAudioTrack) {
            this.stoppedAudioTrack = true;
            this.audioTrackPositionTracker.handleEndOfStream(getWrittenFrames());
            this.audioTrack.stop();
            this.bytesUntilNextAvSync = 0;
        }
    }

    /* loaded from: classes3.dex */
    public static final class PlaybackParametersCheckpoint {
        private final long mediaTimeUs;
        private final PlaybackParameters playbackParameters;
        private final long positionUs;

        private PlaybackParametersCheckpoint(PlaybackParameters playbackParameters, long mediaTimeUs, long positionUs) {
            this.playbackParameters = playbackParameters;
            this.mediaTimeUs = mediaTimeUs;
            this.positionUs = positionUs;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public final class PositionTrackerListener implements AudioTrackPositionTracker.Listener {
        private PositionTrackerListener() {
            DefaultAudioSink.this = r1;
        }

        @Override // com.google.android.exoplayer2.audio.AudioTrackPositionTracker.Listener
        public void onPositionFramesMismatch(long audioTimestampPositionFrames, long audioTimestampSystemTimeUs, long systemTimeUs, long playbackPositionUs) {
            String message = "Spurious audio timestamp (frame position mismatch): " + audioTimestampPositionFrames + ", " + audioTimestampSystemTimeUs + ", " + systemTimeUs + ", " + playbackPositionUs + ", " + DefaultAudioSink.this.getSubmittedFrames() + ", " + DefaultAudioSink.this.getWrittenFrames();
            if (DefaultAudioSink.failOnSpuriousAudioTimestamp) {
                throw new InvalidAudioTrackTimestampException(message);
            }
            Log.w(DefaultAudioSink.TAG, message);
        }

        @Override // com.google.android.exoplayer2.audio.AudioTrackPositionTracker.Listener
        public void onSystemTimeUsMismatch(long audioTimestampPositionFrames, long audioTimestampSystemTimeUs, long systemTimeUs, long playbackPositionUs) {
            String message = "Spurious audio timestamp (system clock mismatch): " + audioTimestampPositionFrames + ", " + audioTimestampSystemTimeUs + ", " + systemTimeUs + ", " + playbackPositionUs + ", " + DefaultAudioSink.this.getSubmittedFrames() + ", " + DefaultAudioSink.this.getWrittenFrames();
            if (DefaultAudioSink.failOnSpuriousAudioTimestamp) {
                throw new InvalidAudioTrackTimestampException(message);
            }
            Log.w(DefaultAudioSink.TAG, message);
        }

        @Override // com.google.android.exoplayer2.audio.AudioTrackPositionTracker.Listener
        public void onInvalidLatency(long latencyUs) {
            Log.w(DefaultAudioSink.TAG, "Ignoring impossibly large audio latency: " + latencyUs);
        }

        @Override // com.google.android.exoplayer2.audio.AudioTrackPositionTracker.Listener
        public void onUnderrun(int bufferSize, long bufferSizeMs) {
            if (DefaultAudioSink.this.listener != null) {
                long elapsedSinceLastFeedMs = SystemClock.elapsedRealtime() - DefaultAudioSink.this.lastFeedElapsedRealtimeMs;
                DefaultAudioSink.this.listener.onUnderrun(bufferSize, bufferSizeMs, elapsedSinceLastFeedMs);
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class Configuration {
        public final AudioProcessor[] availableAudioProcessors;
        public final int bufferSize;
        public final boolean canApplyPlaybackParameters;
        public final int inputPcmFrameSize;
        public final int inputSampleRate;
        public final boolean isInputPcm;
        public final int outputChannelConfig;
        public final int outputEncoding;
        public final int outputPcmFrameSize;
        public final int outputSampleRate;
        public final boolean processingEnabled;

        public Configuration(boolean isInputPcm, int inputPcmFrameSize, int inputSampleRate, int outputPcmFrameSize, int outputSampleRate, int outputChannelConfig, int outputEncoding, int specifiedBufferSize, boolean processingEnabled, boolean canApplyPlaybackParameters, AudioProcessor[] availableAudioProcessors) {
            this.isInputPcm = isInputPcm;
            this.inputPcmFrameSize = inputPcmFrameSize;
            this.inputSampleRate = inputSampleRate;
            this.outputPcmFrameSize = outputPcmFrameSize;
            this.outputSampleRate = outputSampleRate;
            this.outputChannelConfig = outputChannelConfig;
            this.outputEncoding = outputEncoding;
            this.bufferSize = specifiedBufferSize != 0 ? specifiedBufferSize : getDefaultBufferSize();
            this.processingEnabled = processingEnabled;
            this.canApplyPlaybackParameters = canApplyPlaybackParameters;
            this.availableAudioProcessors = availableAudioProcessors;
        }

        public boolean canReuseAudioTrack(Configuration audioTrackConfiguration) {
            return audioTrackConfiguration.outputEncoding == this.outputEncoding && audioTrackConfiguration.outputSampleRate == this.outputSampleRate && audioTrackConfiguration.outputChannelConfig == this.outputChannelConfig;
        }

        public long inputFramesToDurationUs(long frameCount) {
            return (1000000 * frameCount) / this.inputSampleRate;
        }

        public long framesToDurationUs(long frameCount) {
            return (1000000 * frameCount) / this.outputSampleRate;
        }

        public long durationUsToFrames(long durationUs) {
            return (this.outputSampleRate * durationUs) / 1000000;
        }

        public AudioTrack buildAudioTrack(boolean tunneling, AudioAttributes audioAttributes, int audioSessionId) throws AudioSink.InitializationException {
            AudioTrack audioTrack;
            if (Util.SDK_INT >= 21) {
                audioTrack = createAudioTrackV21(tunneling, audioAttributes, audioSessionId);
            } else {
                int streamType = Util.getStreamTypeForAudioUsage(audioAttributes.usage);
                if (audioSessionId == 0) {
                    audioTrack = new AudioTrack(streamType, this.outputSampleRate, this.outputChannelConfig, this.outputEncoding, this.bufferSize, 1);
                } else {
                    audioTrack = new AudioTrack(streamType, this.outputSampleRate, this.outputChannelConfig, this.outputEncoding, this.bufferSize, 1, audioSessionId);
                }
            }
            int state = audioTrack.getState();
            if (state != 1) {
                try {
                    audioTrack.release();
                } catch (Exception e) {
                }
                throw new AudioSink.InitializationException(state, this.outputSampleRate, this.outputChannelConfig, this.bufferSize);
            }
            return audioTrack;
        }

        private AudioTrack createAudioTrackV21(boolean tunneling, AudioAttributes audioAttributes, int audioSessionId) {
            android.media.AudioAttributes attributes;
            if (tunneling) {
                attributes = new AudioAttributes.Builder().setContentType(3).setFlags(16).setUsage(1).build();
            } else {
                attributes = audioAttributes.getAudioAttributesV21();
            }
            AudioFormat format = new AudioFormat.Builder().setChannelMask(this.outputChannelConfig).setEncoding(this.outputEncoding).setSampleRate(this.outputSampleRate).build();
            return new AudioTrack(attributes, format, this.bufferSize, 1, audioSessionId != 0 ? audioSessionId : 0);
        }

        private int getDefaultBufferSize() {
            if (!this.isInputPcm) {
                int rate = DefaultAudioSink.getMaximumEncodedRateBytesPerSecond(this.outputEncoding);
                if (this.outputEncoding == 5) {
                    rate *= 2;
                }
                return (int) ((rate * 250000) / 1000000);
            }
            int minBufferSize = AudioTrack.getMinBufferSize(this.outputSampleRate, this.outputChannelConfig, this.outputEncoding);
            Assertions.checkState(minBufferSize != -2);
            int multipliedBufferSize = minBufferSize * 4;
            int minAppBufferSize = ((int) durationUsToFrames(250000L)) * this.outputPcmFrameSize;
            int maxAppBufferSize = (int) Math.max(minBufferSize, durationUsToFrames(DefaultAudioSink.MAX_BUFFER_DURATION_US) * this.outputPcmFrameSize);
            return Util.constrainValue(multipliedBufferSize, minAppBufferSize, maxAppBufferSize);
        }
    }
}
