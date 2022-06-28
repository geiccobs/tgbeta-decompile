package j$.time.format;
/* loaded from: classes2.dex */
public enum SignStyle {
    NORMAL,
    ALWAYS,
    NEVER,
    NOT_NEGATIVE,
    EXCEEDS_PAD;

    public boolean parse(boolean positive, boolean strict, boolean fixedWidth) {
        switch (ordinal()) {
            case 0:
                return !positive || !strict;
            case 1:
            case 4:
                return true;
            case 2:
            case 3:
            default:
                return !strict && !fixedWidth;
        }
    }
}
