package com.coremedia.iso.boxes.fragment;

/* loaded from: classes.dex */
public class SampleFlags {
    private int reserved;
    private int sampleDegradationPriority;
    private int sampleDependsOn;
    private int sampleHasRedundancy;
    private int sampleIsDependedOn;
    private boolean sampleIsDifferenceSample;
    private int samplePaddingValue;

    public String toString() {
        return "SampleFlags{reserved=" + this.reserved + ", sampleDependsOn=" + this.sampleDependsOn + ", sampleHasRedundancy=" + this.sampleHasRedundancy + ", samplePaddingValue=" + this.samplePaddingValue + ", sampleIsDifferenceSample=" + this.sampleIsDifferenceSample + ", sampleDegradationPriority=" + this.sampleDegradationPriority + '}';
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SampleFlags sampleFlags = (SampleFlags) obj;
        return this.reserved == sampleFlags.reserved && this.sampleDegradationPriority == sampleFlags.sampleDegradationPriority && this.sampleDependsOn == sampleFlags.sampleDependsOn && this.sampleHasRedundancy == sampleFlags.sampleHasRedundancy && this.sampleIsDependedOn == sampleFlags.sampleIsDependedOn && this.sampleIsDifferenceSample == sampleFlags.sampleIsDifferenceSample && this.samplePaddingValue == sampleFlags.samplePaddingValue;
    }

    public int hashCode() {
        return (((this.sampleIsDifferenceSample ? 1 : 0) + (((((((((this.reserved * 31) + this.sampleDependsOn) * 31) + this.sampleIsDependedOn) * 31) + this.sampleHasRedundancy) * 31) + this.samplePaddingValue) * 31)) * 31) + this.sampleDegradationPriority;
    }
}
