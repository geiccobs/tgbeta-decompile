package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.util.Assertions;
import java.nio.ShortBuffer;
import java.util.Arrays;
/* loaded from: classes3.dex */
final class Sonic {
    private static final int AMDF_FREQUENCY = 4000;
    private static final int BYTES_PER_SAMPLE = 2;
    private static final int MAXIMUM_PITCH = 400;
    private static final int MINIMUM_PITCH = 65;
    private final int channelCount;
    private final short[] downSampleBuffer;
    private short[] inputBuffer;
    private int inputFrameCount;
    private final int inputSampleRateHz;
    private int maxDiff;
    private final int maxPeriod;
    private final int maxRequiredFrameCount;
    private int minDiff;
    private final int minPeriod;
    private int newRatePosition;
    private int oldRatePosition;
    private short[] outputBuffer;
    private int outputFrameCount;
    private final float pitch;
    private short[] pitchBuffer;
    private int pitchFrameCount;
    private int prevMinDiff;
    private int prevPeriod;
    private final float rate;
    private int remainingInputToCopyFrameCount;
    private final float speed;

    public Sonic(int inputSampleRateHz, int channelCount, float speed, float pitch, int outputSampleRateHz) {
        this.inputSampleRateHz = inputSampleRateHz;
        this.channelCount = channelCount;
        this.speed = speed;
        this.pitch = pitch;
        this.rate = inputSampleRateHz / outputSampleRateHz;
        this.minPeriod = inputSampleRateHz / MAXIMUM_PITCH;
        int i = inputSampleRateHz / 65;
        this.maxPeriod = i;
        int i2 = i * 2;
        this.maxRequiredFrameCount = i2;
        this.downSampleBuffer = new short[i2];
        this.inputBuffer = new short[i2 * channelCount];
        this.outputBuffer = new short[i2 * channelCount];
        this.pitchBuffer = new short[i2 * channelCount];
    }

    public void queueInput(ShortBuffer buffer) {
        int remaining = buffer.remaining();
        int i = this.channelCount;
        int framesToWrite = remaining / i;
        int bytesToWrite = i * framesToWrite * 2;
        short[] ensureSpaceForAdditionalFrames = ensureSpaceForAdditionalFrames(this.inputBuffer, this.inputFrameCount, framesToWrite);
        this.inputBuffer = ensureSpaceForAdditionalFrames;
        buffer.get(ensureSpaceForAdditionalFrames, this.inputFrameCount * this.channelCount, bytesToWrite / 2);
        this.inputFrameCount += framesToWrite;
        processStreamInput();
    }

    public void getOutput(ShortBuffer buffer) {
        int framesToRead = Math.min(buffer.remaining() / this.channelCount, this.outputFrameCount);
        buffer.put(this.outputBuffer, 0, this.channelCount * framesToRead);
        int i = this.outputFrameCount - framesToRead;
        this.outputFrameCount = i;
        short[] sArr = this.outputBuffer;
        int i2 = this.channelCount;
        System.arraycopy(sArr, framesToRead * i2, sArr, 0, i * i2);
    }

    public void queueEndOfStream() {
        int i;
        int remainingFrameCount = this.inputFrameCount;
        float f = this.speed;
        float f2 = this.pitch;
        float s = f / f2;
        float r = this.rate * f2;
        int expectedOutputFrames = this.outputFrameCount + ((int) ((((remainingFrameCount / s) + this.pitchFrameCount) / r) + 0.5f));
        this.inputBuffer = ensureSpaceForAdditionalFrames(this.inputBuffer, this.inputFrameCount, (this.maxRequiredFrameCount * 2) + remainingFrameCount);
        int xSample = 0;
        while (true) {
            i = this.maxRequiredFrameCount;
            int i2 = this.channelCount;
            if (xSample >= i * 2 * i2) {
                break;
            }
            this.inputBuffer[(i2 * remainingFrameCount) + xSample] = 0;
            xSample++;
        }
        int xSample2 = this.inputFrameCount;
        this.inputFrameCount = xSample2 + (i * 2);
        processStreamInput();
        if (this.outputFrameCount > expectedOutputFrames) {
            this.outputFrameCount = expectedOutputFrames;
        }
        this.inputFrameCount = 0;
        this.remainingInputToCopyFrameCount = 0;
        this.pitchFrameCount = 0;
    }

    public void flush() {
        this.inputFrameCount = 0;
        this.outputFrameCount = 0;
        this.pitchFrameCount = 0;
        this.oldRatePosition = 0;
        this.newRatePosition = 0;
        this.remainingInputToCopyFrameCount = 0;
        this.prevPeriod = 0;
        this.prevMinDiff = 0;
        this.minDiff = 0;
        this.maxDiff = 0;
    }

    public int getOutputSize() {
        return this.outputFrameCount * this.channelCount * 2;
    }

    private short[] ensureSpaceForAdditionalFrames(short[] buffer, int frameCount, int additionalFrameCount) {
        int length = buffer.length;
        int i = this.channelCount;
        int currentCapacityFrames = length / i;
        if (frameCount + additionalFrameCount <= currentCapacityFrames) {
            return buffer;
        }
        int newCapacityFrames = ((currentCapacityFrames * 3) / 2) + additionalFrameCount;
        return Arrays.copyOf(buffer, i * newCapacityFrames);
    }

    private void removeProcessedInputFrames(int positionFrames) {
        int remainingFrames = this.inputFrameCount - positionFrames;
        short[] sArr = this.inputBuffer;
        int i = this.channelCount;
        System.arraycopy(sArr, positionFrames * i, sArr, 0, i * remainingFrames);
        this.inputFrameCount = remainingFrames;
    }

    private void copyToOutput(short[] samples, int positionFrames, int frameCount) {
        short[] ensureSpaceForAdditionalFrames = ensureSpaceForAdditionalFrames(this.outputBuffer, this.outputFrameCount, frameCount);
        this.outputBuffer = ensureSpaceForAdditionalFrames;
        int i = this.channelCount;
        System.arraycopy(samples, positionFrames * i, ensureSpaceForAdditionalFrames, this.outputFrameCount * i, i * frameCount);
        this.outputFrameCount += frameCount;
    }

    private int copyInputToOutput(int positionFrames) {
        int frameCount = Math.min(this.maxRequiredFrameCount, this.remainingInputToCopyFrameCount);
        copyToOutput(this.inputBuffer, positionFrames, frameCount);
        this.remainingInputToCopyFrameCount -= frameCount;
        return frameCount;
    }

    private void downSampleInput(short[] samples, int position, int skip) {
        int frameCount = this.maxRequiredFrameCount / skip;
        int i = this.channelCount;
        int samplesPerValue = i * skip;
        int position2 = position * i;
        for (int i2 = 0; i2 < frameCount; i2++) {
            int value = 0;
            for (int j = 0; j < samplesPerValue; j++) {
                value += samples[(i2 * samplesPerValue) + position2 + j];
            }
            this.downSampleBuffer[i2] = (short) (value / samplesPerValue);
        }
    }

    private int findPitchPeriodInRange(short[] samples, int position, int minPeriod, int maxPeriod) {
        int bestPeriod = 0;
        int worstPeriod = 255;
        int minDiff = 1;
        int maxDiff = 0;
        int position2 = position * this.channelCount;
        for (int period = minPeriod; period <= maxPeriod; period++) {
            int diff = 0;
            for (int i = 0; i < period; i++) {
                short sVal = samples[position2 + i];
                short pVal = samples[position2 + period + i];
                diff += Math.abs(sVal - pVal);
            }
            int i2 = diff * bestPeriod;
            if (i2 < minDiff * period) {
                minDiff = diff;
                bestPeriod = period;
            }
            if (diff * worstPeriod > maxDiff * period) {
                maxDiff = diff;
                worstPeriod = period;
            }
        }
        int period2 = minDiff / bestPeriod;
        this.minDiff = period2;
        this.maxDiff = maxDiff / worstPeriod;
        return bestPeriod;
    }

    private boolean previousPeriodBetter(int minDiff, int maxDiff) {
        if (minDiff == 0 || this.prevPeriod == 0 || maxDiff > minDiff * 3 || minDiff * 2 <= this.prevMinDiff * 3) {
            return false;
        }
        return true;
    }

    private int findPitchPeriod(short[] samples, int position) {
        int period;
        int retPeriod;
        int i = this.inputSampleRateHz;
        int skip = i > 4000 ? i / 4000 : 1;
        if (this.channelCount == 1 && skip == 1) {
            period = findPitchPeriodInRange(samples, position, this.minPeriod, this.maxPeriod);
        } else {
            downSampleInput(samples, position, skip);
            period = findPitchPeriodInRange(this.downSampleBuffer, 0, this.minPeriod / skip, this.maxPeriod / skip);
            if (skip != 1) {
                int period2 = period * skip;
                int minP = period2 - (skip * 4);
                int maxP = (skip * 4) + period2;
                if (minP < this.minPeriod) {
                    minP = this.minPeriod;
                }
                if (maxP > this.maxPeriod) {
                    maxP = this.maxPeriod;
                }
                if (this.channelCount == 1) {
                    period = findPitchPeriodInRange(samples, position, minP, maxP);
                } else {
                    downSampleInput(samples, position, 1);
                    period = findPitchPeriodInRange(this.downSampleBuffer, 0, minP, maxP);
                }
            }
        }
        if (previousPeriodBetter(this.minDiff, this.maxDiff)) {
            retPeriod = this.prevPeriod;
        } else {
            retPeriod = period;
        }
        this.prevMinDiff = this.minDiff;
        this.prevPeriod = period;
        return retPeriod;
    }

    private void moveNewSamplesToPitchBuffer(int originalOutputFrameCount) {
        int frameCount = this.outputFrameCount - originalOutputFrameCount;
        short[] ensureSpaceForAdditionalFrames = ensureSpaceForAdditionalFrames(this.pitchBuffer, this.pitchFrameCount, frameCount);
        this.pitchBuffer = ensureSpaceForAdditionalFrames;
        short[] sArr = this.outputBuffer;
        int i = this.channelCount;
        System.arraycopy(sArr, originalOutputFrameCount * i, ensureSpaceForAdditionalFrames, this.pitchFrameCount * i, i * frameCount);
        this.outputFrameCount = originalOutputFrameCount;
        this.pitchFrameCount += frameCount;
    }

    private void removePitchFrames(int frameCount) {
        if (frameCount == 0) {
            return;
        }
        short[] sArr = this.pitchBuffer;
        int i = this.channelCount;
        System.arraycopy(sArr, frameCount * i, sArr, 0, (this.pitchFrameCount - frameCount) * i);
        this.pitchFrameCount -= frameCount;
    }

    private short interpolate(short[] in, int inPos, int oldSampleRate, int newSampleRate) {
        short left = in[inPos];
        short right = in[this.channelCount + inPos];
        int position = this.newRatePosition * oldSampleRate;
        int i = this.oldRatePosition;
        int leftPosition = i * newSampleRate;
        int rightPosition = (i + 1) * newSampleRate;
        int ratio = rightPosition - position;
        int width = rightPosition - leftPosition;
        return (short) (((ratio * left) + ((width - ratio) * right)) / width);
    }

    private void adjustRate(float rate, int originalOutputFrameCount) {
        int i;
        int i2;
        if (this.outputFrameCount == originalOutputFrameCount) {
            return;
        }
        int newSampleRate = (int) (this.inputSampleRateHz / rate);
        int oldSampleRate = this.inputSampleRateHz;
        while (true) {
            if (newSampleRate <= 16384 && oldSampleRate <= 16384) {
                break;
            }
            newSampleRate /= 2;
            oldSampleRate /= 2;
        }
        moveNewSamplesToPitchBuffer(originalOutputFrameCount);
        int position = 0;
        while (true) {
            int i3 = this.pitchFrameCount;
            boolean z = true;
            if (position < i3 - 1) {
                while (true) {
                    i = this.oldRatePosition;
                    int i4 = (i + 1) * newSampleRate;
                    i2 = this.newRatePosition;
                    if (i4 <= i2 * oldSampleRate) {
                        break;
                    }
                    this.outputBuffer = ensureSpaceForAdditionalFrames(this.outputBuffer, this.outputFrameCount, 1);
                    int i5 = 0;
                    while (true) {
                        int i6 = this.channelCount;
                        if (i5 < i6) {
                            this.outputBuffer[(this.outputFrameCount * i6) + i5] = interpolate(this.pitchBuffer, (i6 * position) + i5, oldSampleRate, newSampleRate);
                            i5++;
                        }
                    }
                    int i7 = this.newRatePosition;
                    this.newRatePosition = i7 + 1;
                    this.outputFrameCount++;
                }
                int i8 = i + 1;
                this.oldRatePosition = i8;
                if (i8 == oldSampleRate) {
                    this.oldRatePosition = 0;
                    if (i2 != newSampleRate) {
                        z = false;
                    }
                    Assertions.checkState(z);
                    this.newRatePosition = 0;
                }
                position++;
            } else {
                removePitchFrames(i3 - 1);
                return;
            }
        }
    }

    private int skipPitchPeriod(short[] samples, int position, float speed, int period) {
        int newFrameCount;
        if (speed >= 2.0f) {
            newFrameCount = (int) (period / (speed - 1.0f));
        } else {
            this.remainingInputToCopyFrameCount = (int) ((period * (2.0f - speed)) / (speed - 1.0f));
            newFrameCount = period;
        }
        short[] ensureSpaceForAdditionalFrames = ensureSpaceForAdditionalFrames(this.outputBuffer, this.outputFrameCount, newFrameCount);
        this.outputBuffer = ensureSpaceForAdditionalFrames;
        overlapAdd(newFrameCount, this.channelCount, ensureSpaceForAdditionalFrames, this.outputFrameCount, samples, position, samples, position + period);
        this.outputFrameCount += newFrameCount;
        return newFrameCount;
    }

    private int insertPitchPeriod(short[] samples, int position, float speed, int period) {
        int newFrameCount;
        if (speed < 0.5f) {
            newFrameCount = (int) ((period * speed) / (1.0f - speed));
        } else {
            this.remainingInputToCopyFrameCount = (int) ((period * ((2.0f * speed) - 1.0f)) / (1.0f - speed));
            newFrameCount = period;
        }
        short[] ensureSpaceForAdditionalFrames = ensureSpaceForAdditionalFrames(this.outputBuffer, this.outputFrameCount, period + newFrameCount);
        this.outputBuffer = ensureSpaceForAdditionalFrames;
        int i = this.channelCount;
        System.arraycopy(samples, position * i, ensureSpaceForAdditionalFrames, this.outputFrameCount * i, i * period);
        overlapAdd(newFrameCount, this.channelCount, this.outputBuffer, this.outputFrameCount + period, samples, position + period, samples, position);
        this.outputFrameCount += period + newFrameCount;
        return newFrameCount;
    }

    private void changeSpeed(float speed) {
        if (this.inputFrameCount < this.maxRequiredFrameCount) {
            return;
        }
        int frameCount = this.inputFrameCount;
        int positionFrames = 0;
        do {
            if (this.remainingInputToCopyFrameCount > 0) {
                positionFrames += copyInputToOutput(positionFrames);
            } else {
                int period = findPitchPeriod(this.inputBuffer, positionFrames);
                if (speed > 1.0d) {
                    positionFrames += skipPitchPeriod(this.inputBuffer, positionFrames, speed, period) + period;
                } else {
                    positionFrames += insertPitchPeriod(this.inputBuffer, positionFrames, speed, period);
                }
            }
        } while (this.maxRequiredFrameCount + positionFrames <= frameCount);
        removeProcessedInputFrames(positionFrames);
    }

    private void processStreamInput() {
        int originalOutputFrameCount = this.outputFrameCount;
        float f = this.speed;
        float f2 = this.pitch;
        float s = f / f2;
        float r = this.rate * f2;
        if (s > 1.00001d || s < 0.99999d) {
            changeSpeed(s);
        } else {
            copyToOutput(this.inputBuffer, 0, this.inputFrameCount);
            this.inputFrameCount = 0;
        }
        if (r != 1.0f) {
            adjustRate(r, originalOutputFrameCount);
        }
    }

    private static void overlapAdd(int frameCount, int channelCount, short[] out, int outPosition, short[] rampDown, int rampDownPosition, short[] rampUp, int rampUpPosition) {
        for (int i = 0; i < channelCount; i++) {
            int o = (outPosition * channelCount) + i;
            int u = (rampUpPosition * channelCount) + i;
            int d = (rampDownPosition * channelCount) + i;
            for (int t = 0; t < frameCount; t++) {
                out[o] = (short) (((rampDown[d] * (frameCount - t)) + (rampUp[u] * t)) / frameCount);
                o += channelCount;
                d += channelCount;
                u += channelCount;
            }
        }
    }
}
