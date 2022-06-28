package j$.wrappers;

import j$.time.Clock;
import j$.time.Instant;
import j$.time.TimeConversions;
import j$.time.ZoneId;
/* renamed from: j$.wrappers.$r8$wrapper$java$time$Clock$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$time$Clock$VWRP extends Clock {
    final /* synthetic */ java.time.Clock wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$time$Clock$VWRP(java.time.Clock clock) {
        this.wrappedValue = clock;
    }

    public static /* synthetic */ Clock convert(java.time.Clock clock) {
        if (clock == null) {
            return null;
        }
        return clock instanceof C$r8$wrapper$java$time$Clock$WRP ? ((C$r8$wrapper$java$time$Clock$WRP) clock).wrappedValue : new C$r8$wrapper$java$time$Clock$VWRP(clock);
    }

    @Override // j$.time.Clock
    public /* synthetic */ boolean equals(Object obj) {
        return this.wrappedValue.equals(obj);
    }

    @Override // j$.time.Clock
    public /* synthetic */ ZoneId getZone() {
        return TimeConversions.convert(this.wrappedValue.getZone());
    }

    @Override // j$.time.Clock
    public /* synthetic */ int hashCode() {
        return this.wrappedValue.hashCode();
    }

    @Override // j$.time.Clock
    public /* synthetic */ Instant instant() {
        return TimeConversions.convert(this.wrappedValue.instant());
    }

    @Override // j$.time.Clock
    public /* synthetic */ long millis() {
        return this.wrappedValue.millis();
    }

    @Override // j$.time.Clock
    public /* synthetic */ Clock withZone(ZoneId zoneId) {
        return convert(this.wrappedValue.withZone(TimeConversions.convert(zoneId)));
    }
}
