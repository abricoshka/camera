package com.mediatek.camera.p005v2.exif;

/* loaded from: classes.dex */
public class Rational {
    private final long mDenominator;
    private final long mNumerator;

    public Rational(long j, long j2) {
        this.mNumerator = j;
        this.mDenominator = j2;
    }

    public long getNumerator() {
        return this.mNumerator;
    }

    public long getDenominator() {
        return this.mDenominator;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Rational)) {
            return false;
        }
        Rational rational = (Rational) obj;
        return this.mNumerator == rational.mNumerator && this.mDenominator == rational.mDenominator;
    }

    public String toString() {
        return this.mNumerator + "/" + this.mDenominator;
    }
}
