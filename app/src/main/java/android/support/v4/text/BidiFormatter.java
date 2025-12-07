package android.support.v4.text;

import android.support.v4.app.FrameMetricsAggregator;
import android.text.SpannableStringBuilder;
import java.util.Locale;

/* loaded from: classes.dex */
public final class BidiFormatter {
    private final TextDirectionHeuristicCompat mDefaultTextDirectionHeuristicCompat;
    private final int mFlags;
    private final boolean mIsRtlContext;
    private static TextDirectionHeuristicCompat DEFAULT_TEXT_DIRECTION_HEURISTIC = TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR;
    private static final String LRM_STRING = Character.toString('\u200e');
    private static final String RLM_STRING = Character.toString('\u200f');
    private static final BidiFormatter DEFAULT_LTR_INSTANCE = new BidiFormatter(false, 2, DEFAULT_TEXT_DIRECTION_HEURISTIC);
    private static final BidiFormatter DEFAULT_RTL_INSTANCE = new BidiFormatter(true, 2, DEFAULT_TEXT_DIRECTION_HEURISTIC);

    /* synthetic */ BidiFormatter(boolean z, int i, TextDirectionHeuristicCompat textDirectionHeuristicCompat, BidiFormatter bidiFormatter) {
        this(z, i, textDirectionHeuristicCompat);
    }

    public static final class Builder {
        private int mFlags;
        private boolean mIsRtlContext;
        private TextDirectionHeuristicCompat mTextDirectionHeuristicCompat;

        public Builder() {
            initialize(BidiFormatter.isRtlLocale(Locale.getDefault()));
        }

        private void initialize(boolean z) {
            this.mIsRtlContext = z;
            this.mTextDirectionHeuristicCompat = BidiFormatter.DEFAULT_TEXT_DIRECTION_HEURISTIC;
            this.mFlags = 2;
        }

        private static BidiFormatter getDefaultInstanceFromContext(boolean z) {
            return z ? BidiFormatter.DEFAULT_RTL_INSTANCE : BidiFormatter.DEFAULT_LTR_INSTANCE;
        }

        public BidiFormatter build() {
            if (this.mFlags == 2 && this.mTextDirectionHeuristicCompat == BidiFormatter.DEFAULT_TEXT_DIRECTION_HEURISTIC) {
                return getDefaultInstanceFromContext(this.mIsRtlContext);
            }
            return new BidiFormatter(this.mIsRtlContext, this.mFlags, this.mTextDirectionHeuristicCompat, null);
        }
    }

    public static BidiFormatter getInstance() {
        return new Builder().build();
    }

    private BidiFormatter(boolean z, int i, TextDirectionHeuristicCompat textDirectionHeuristicCompat) {
        this.mIsRtlContext = z;
        this.mFlags = i;
        this.mDefaultTextDirectionHeuristicCompat = textDirectionHeuristicCompat;
    }

    public boolean getStereoReset() {
        return (this.mFlags & 2) != 0;
    }

    private String markAfter(CharSequence charSequence, TextDirectionHeuristicCompat textDirectionHeuristicCompat) {
        boolean zIsRtl = textDirectionHeuristicCompat.isRtl(charSequence, 0, charSequence.length());
        if (!this.mIsRtlContext && (zIsRtl || getExitDir(charSequence) == 1)) {
            return LRM_STRING;
        }
        if (!this.mIsRtlContext) {
            return "";
        }
        if (!zIsRtl || getExitDir(charSequence) == -1) {
            return RLM_STRING;
        }
        return "";
    }

    private String markBefore(CharSequence charSequence, TextDirectionHeuristicCompat textDirectionHeuristicCompat) {
        boolean zIsRtl = textDirectionHeuristicCompat.isRtl(charSequence, 0, charSequence.length());
        if (!this.mIsRtlContext && (zIsRtl || getEntryDir(charSequence) == 1)) {
            return LRM_STRING;
        }
        if (!this.mIsRtlContext) {
            return "";
        }
        if (!zIsRtl || getEntryDir(charSequence) == -1) {
            return RLM_STRING;
        }
        return "";
    }

    public CharSequence unicodeWrap(CharSequence charSequence, TextDirectionHeuristicCompat textDirectionHeuristicCompat, boolean z) {
        if (charSequence == null) {
            return null;
        }
        boolean zIsRtl = textDirectionHeuristicCompat.isRtl(charSequence, 0, charSequence.length());
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (getStereoReset() && z) {
            spannableStringBuilder.append((CharSequence) markBefore(charSequence, zIsRtl ? TextDirectionHeuristicsCompat.RTL : TextDirectionHeuristicsCompat.LTR));
        }
        if (zIsRtl != this.mIsRtlContext) {
            spannableStringBuilder.append(zIsRtl ? '\u202b' : '\u202a');
            spannableStringBuilder.append(charSequence);
            spannableStringBuilder.append('\u202c');
        } else {
            spannableStringBuilder.append(charSequence);
        }
        if (z) {
            spannableStringBuilder.append((CharSequence) markAfter(charSequence, zIsRtl ? TextDirectionHeuristicsCompat.RTL : TextDirectionHeuristicsCompat.LTR));
        }
        return spannableStringBuilder;
    }

    public CharSequence unicodeWrap(CharSequence charSequence) {
        return unicodeWrap(charSequence, this.mDefaultTextDirectionHeuristicCompat, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isRtlLocale(Locale locale) {
        return TextUtilsCompat.getLayoutDirectionFromLocale(locale) == 1;
    }

    private static int getExitDir(CharSequence charSequence) {
        return new DirectionalityEstimator(charSequence, false).getExitDir();
    }

    private static int getEntryDir(CharSequence charSequence) {
        return new DirectionalityEstimator(charSequence, false).getEntryDir();
    }

    private static class DirectionalityEstimator {
        private static final byte[] DIR_TYPE_CACHE = new byte[1792];
        private int charIndex;
        private final boolean isHtml;
        private char lastChar;
        private final int length;
        private final CharSequence text;

        static {
            for (int i = 0; i < 1792; i++) {
                DIR_TYPE_CACHE[i] = Character.getDirectionality(i);
            }
        }

        DirectionalityEstimator(CharSequence charSequence, boolean z) {
            this.text = charSequence;
            this.isHtml = z;
            this.length = charSequence.length();
        }

        int getEntryDir() {
            this.charIndex = 0;
            int i = 0;
            int i2 = 0;
            int i3 = 0;
            while (this.charIndex < this.length && i == 0) {
                switch (dirTypeForward()) {
                    case 0:
                        if (i3 != 0) {
                            i = i3;
                            break;
                        } else {
                            return -1;
                        }
                    case 1:
                    case 2:
                        if (i3 != 0) {
                            i = i3;
                            break;
                        } else {
                            return 1;
                        }
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                    case 8:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    default:
                        i = i3;
                        break;
                    case 9:
                        break;
                    case 14:
                    case 15:
                        i3++;
                        i2 = -1;
                        break;
                    case 16:
                    case 17:
                        i3++;
                        i2 = 1;
                        break;
                    case 18:
                        i3--;
                        i2 = 0;
                        break;
                }
            }
            if (i == 0) {
                return 0;
            }
            if (i2 != 0) {
                return i2;
            }
            while (this.charIndex > 0) {
                switch (dirTypeBackward()) {
                    case 14:
                    case 15:
                        if (i != i3) {
                            i3--;
                            break;
                        } else {
                            return -1;
                        }
                    case 16:
                    case 17:
                        if (i != i3) {
                            i3--;
                            break;
                        } else {
                            return 1;
                        }
                    case 18:
                        i3++;
                        break;
                }
            }
            return 0;
        }

        int getExitDir() {
            this.charIndex = this.length;
            int i = 0;
            int i2 = 0;
            while (this.charIndex > 0) {
                switch (dirTypeBackward()) {
                    case 0:
                        if (i2 != 0) {
                            if (i != 0) {
                                break;
                            } else {
                                i = i2;
                                break;
                            }
                        } else {
                            return -1;
                        }
                    case 1:
                    case 2:
                        if (i2 != 0) {
                            if (i != 0) {
                                break;
                            } else {
                                i = i2;
                                break;
                            }
                        } else {
                            return 1;
                        }
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                    case 8:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    default:
                        if (i != 0) {
                            break;
                        } else {
                            i = i2;
                            break;
                        }
                    case 9:
                        break;
                    case 14:
                    case 15:
                        if (i != i2) {
                            i2--;
                            break;
                        } else {
                            return -1;
                        }
                    case 16:
                    case 17:
                        if (i != i2) {
                            i2--;
                            break;
                        } else {
                            return 1;
                        }
                    case 18:
                        i2++;
                        break;
                }
            }
            return 0;
        }

        private static byte getCachedDirectionality(char c) {
            return c < '\u0700' ? DIR_TYPE_CACHE[c] : Character.getDirectionality(c);
        }

        byte dirTypeForward() {
            this.lastChar = this.text.charAt(this.charIndex);
            if (Character.isHighSurrogate(this.lastChar)) {
                int iCodePointAt = Character.codePointAt(this.text, this.charIndex);
                this.charIndex += Character.charCount(iCodePointAt);
                return Character.getDirectionality(iCodePointAt);
            }
            this.charIndex++;
            byte cachedDirectionality = getCachedDirectionality(this.lastChar);
            if (this.isHtml) {
                if (this.lastChar == '<') {
                    return skipTagForward();
                }
                if (this.lastChar == '&') {
                    return skipEntityForward();
                }
                return cachedDirectionality;
            }
            return cachedDirectionality;
        }

        byte dirTypeBackward() {
            this.lastChar = this.text.charAt(this.charIndex - 1);
            if (Character.isLowSurrogate(this.lastChar)) {
                int iCodePointBefore = Character.codePointBefore(this.text, this.charIndex);
                this.charIndex -= Character.charCount(iCodePointBefore);
                return Character.getDirectionality(iCodePointBefore);
            }
            this.charIndex--;
            byte cachedDirectionality = getCachedDirectionality(this.lastChar);
            if (this.isHtml) {
                if (this.lastChar == '>') {
                    return skipTagBackward();
                }
                if (this.lastChar == ';') {
                    return skipEntityBackward();
                }
                return cachedDirectionality;
            }
            return cachedDirectionality;
        }

        private byte skipTagForward() {
            int i = this.charIndex;
            while (this.charIndex < this.length) {
                CharSequence charSequence = this.text;
                int i2 = this.charIndex;
                this.charIndex = i2 + 1;
                this.lastChar = charSequence.charAt(i2);
                if (this.lastChar == '>') {
                    return (byte) 12;
                }
                if (this.lastChar == '\"' || this.lastChar == '\'') {
                    char c = this.lastChar;
                    while (this.charIndex < this.length) {
                        CharSequence charSequence2 = this.text;
                        int i3 = this.charIndex;
                        this.charIndex = i3 + 1;
                        char cCharAt = charSequence2.charAt(i3);
                        this.lastChar = cCharAt;
                        if (cCharAt != c) {
                        }
                    }
                }
            }
            this.charIndex = i;
            this.lastChar = '<';
            return (byte) 13;
        }

        private byte skipTagBackward() {
            int i = this.charIndex;
            while (this.charIndex > 0) {
                CharSequence charSequence = this.text;
                int i2 = this.charIndex - 1;
                this.charIndex = i2;
                this.lastChar = charSequence.charAt(i2);
                if (this.lastChar == '<') {
                    return (byte) 12;
                }
                if (this.lastChar == '>') {
                    break;
                }
                if (this.lastChar == '\"' || this.lastChar == '\'') {
                    char c = this.lastChar;
                    while (this.charIndex > 0) {
                        CharSequence charSequence2 = this.text;
                        int i3 = this.charIndex - 1;
                        this.charIndex = i3;
                        char cCharAt = charSequence2.charAt(i3);
                        this.lastChar = cCharAt;
                        if (cCharAt != c) {
                        }
                    }
                }
            }
            this.charIndex = i;
            this.lastChar = '>';
            return (byte) 13;
        }

        private byte skipEntityForward() {
            while (this.charIndex < this.length) {
                CharSequence charSequence = this.text;
                int i = this.charIndex;
                this.charIndex = i + 1;
                char cCharAt = charSequence.charAt(i);
                this.lastChar = cCharAt;
                if (cCharAt == ';') {
                    return (byte) 12;
                }
            }
            return (byte) 12;
        }

        private byte skipEntityBackward() {
            int i = this.charIndex;
            while (this.charIndex > 0) {
                CharSequence charSequence = this.text;
                int i2 = this.charIndex - 1;
                this.charIndex = i2;
                this.lastChar = charSequence.charAt(i2);
                if (this.lastChar == '&') {
                    return (byte) 12;
                }
                if (this.lastChar == ';') {
                    break;
                }
            }
            this.charIndex = i;
            this.lastChar = ';';
            return (byte) 13;
        }
    }
}
