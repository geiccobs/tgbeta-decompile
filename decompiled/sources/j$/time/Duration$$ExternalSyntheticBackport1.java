package j$.time;
/* loaded from: classes2.dex */
public final /* synthetic */ class Duration$$ExternalSyntheticBackport1 {
    public static /* synthetic */ long m(long j, long j2) {
        int numberOfLeadingZeros = Long.numberOfLeadingZeros(j) + Long.numberOfLeadingZeros(j ^ (-1)) + Long.numberOfLeadingZeros(j2) + Long.numberOfLeadingZeros((-1) ^ j2);
        if (numberOfLeadingZeros > 65) {
            return j * j2;
        }
        if (numberOfLeadingZeros >= 64) {
            boolean z = true;
            int i = (j > 0L ? 1 : (j == 0L ? 0 : -1));
            boolean z2 = i >= 0;
            if (j2 == Long.MIN_VALUE) {
                z = false;
            }
            if (z2 | z) {
                long j3 = j * j2;
                if (i == 0 || j3 / j == j2) {
                    return j3;
                }
            }
        }
        throw new ArithmeticException();
    }
}