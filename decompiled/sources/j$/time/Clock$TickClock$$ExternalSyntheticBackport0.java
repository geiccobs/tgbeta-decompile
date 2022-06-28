package j$.time;
/* loaded from: classes2.dex */
public final /* synthetic */ class Clock$TickClock$$ExternalSyntheticBackport0 {
    public static /* synthetic */ long m(long j, long j2) {
        long j3 = j % j2;
        if (j3 == 0) {
            return 0L;
        }
        return (((j ^ j2) >> 63) | 1) > 0 ? j3 : j3 + j2;
    }
}
