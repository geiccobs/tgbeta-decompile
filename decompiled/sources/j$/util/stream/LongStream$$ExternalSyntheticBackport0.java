package j$.util.stream;
/* loaded from: classes2.dex */
public final /* synthetic */ class LongStream$$ExternalSyntheticBackport0 {
    public static /* synthetic */ long m(long j, long j2) {
        if (j2 < 0) {
            return (j ^ Long.MIN_VALUE) < (j2 ^ Long.MIN_VALUE) ? 0L : 1L;
        } else if (j >= 0) {
            return j / j2;
        } else {
            int i = 1;
            long j3 = ((j >>> 1) / j2) << 1;
            if (((j - (j3 * j2)) ^ Long.MIN_VALUE) < (j2 ^ Long.MIN_VALUE)) {
                i = 0;
            }
            return j3 + i;
        }
    }
}
