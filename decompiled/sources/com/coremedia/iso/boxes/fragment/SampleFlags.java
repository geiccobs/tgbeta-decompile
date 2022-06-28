package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import java.nio.ByteBuffer;
/* loaded from: classes3.dex */
public class SampleFlags {
    private byte is_leading;
    private byte reserved;
    private int sampleDegradationPriority;
    private byte sampleDependsOn;
    private byte sampleHasRedundancy;
    private byte sampleIsDependedOn;
    private boolean sampleIsDifferenceSample;
    private byte samplePaddingValue;

    public SampleFlags() {
    }

    public SampleFlags(ByteBuffer bb) {
        long a = IsoTypeReader.readUInt32(bb);
        this.reserved = (byte) (((-268435456) & a) >> 28);
        this.is_leading = (byte) ((201326592 & a) >> 26);
        this.sampleDependsOn = (byte) ((50331648 & a) >> 24);
        this.sampleIsDependedOn = (byte) ((12582912 & a) >> 22);
        this.sampleHasRedundancy = (byte) ((3145728 & a) >> 20);
        this.samplePaddingValue = (byte) ((917504 & a) >> 17);
        this.sampleIsDifferenceSample = ((65536 & a) >> 16) > 0;
        this.sampleDegradationPriority = (int) (65535 & a);
    }

    public void getContent(ByteBuffer os) {
        long a = 0 | (this.reserved << 28);
        IsoTypeWriter.writeUInt32(os, a | (this.is_leading << 26) | (this.sampleDependsOn << 24) | (this.sampleIsDependedOn << 22) | (this.sampleHasRedundancy << 20) | (this.samplePaddingValue << 17) | ((this.sampleIsDifferenceSample ? 1 : 0) << 16) | this.sampleDegradationPriority);
    }

    public int getReserved() {
        return this.reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = (byte) reserved;
    }

    public int getSampleDependsOn() {
        return this.sampleDependsOn;
    }

    public void setSampleDependsOn(int sampleDependsOn) {
        this.sampleDependsOn = (byte) sampleDependsOn;
    }

    public int getSampleIsDependedOn() {
        return this.sampleIsDependedOn;
    }

    public void setSampleIsDependedOn(int sampleIsDependedOn) {
        this.sampleIsDependedOn = (byte) sampleIsDependedOn;
    }

    public int getSampleHasRedundancy() {
        return this.sampleHasRedundancy;
    }

    public void setSampleHasRedundancy(int sampleHasRedundancy) {
        this.sampleHasRedundancy = (byte) sampleHasRedundancy;
    }

    public int getSamplePaddingValue() {
        return this.samplePaddingValue;
    }

    public void setSamplePaddingValue(int samplePaddingValue) {
        this.samplePaddingValue = (byte) samplePaddingValue;
    }

    public boolean isSampleIsDifferenceSample() {
        return this.sampleIsDifferenceSample;
    }

    public void setSampleIsDifferenceSample(boolean sampleIsDifferenceSample) {
        this.sampleIsDifferenceSample = sampleIsDifferenceSample;
    }

    public int getSampleDegradationPriority() {
        return this.sampleDegradationPriority;
    }

    public void setSampleDegradationPriority(int sampleDegradationPriority) {
        this.sampleDegradationPriority = sampleDegradationPriority;
    }

    public String toString() {
        return "SampleFlags{reserved=" + ((int) this.reserved) + ", isLeading=" + ((int) this.is_leading) + ", depOn=" + ((int) this.sampleDependsOn) + ", isDepOn=" + ((int) this.sampleIsDependedOn) + ", hasRedundancy=" + ((int) this.sampleHasRedundancy) + ", padValue=" + ((int) this.samplePaddingValue) + ", isDiffSample=" + this.sampleIsDifferenceSample + ", degradPrio=" + this.sampleDegradationPriority + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SampleFlags that = (SampleFlags) o;
        if (this.is_leading == that.is_leading && this.reserved == that.reserved && this.sampleDegradationPriority == that.sampleDegradationPriority && this.sampleDependsOn == that.sampleDependsOn && this.sampleHasRedundancy == that.sampleHasRedundancy && this.sampleIsDependedOn == that.sampleIsDependedOn && this.sampleIsDifferenceSample == that.sampleIsDifferenceSample && this.samplePaddingValue == that.samplePaddingValue) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = this.reserved;
        return (((((((((((((result * 31) + this.is_leading) * 31) + this.sampleDependsOn) * 31) + this.sampleIsDependedOn) * 31) + this.sampleHasRedundancy) * 31) + this.samplePaddingValue) * 31) + (this.sampleIsDifferenceSample ? 1 : 0)) * 31) + this.sampleDegradationPriority;
    }
}
