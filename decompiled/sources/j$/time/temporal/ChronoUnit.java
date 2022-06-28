package j$.time.temporal;

import j$.time.Duration;
/* loaded from: classes2.dex */
public enum ChronoUnit implements TemporalUnit {
    NANOS("Nanos", Duration.ofNanos(1)),
    MICROS("Micros", Duration.ofNanos(1000)),
    MILLIS("Millis", Duration.ofNanos(1000000)),
    SECONDS("Seconds", Duration.ofSeconds(1)),
    MINUTES("Minutes", Duration.ofSeconds(60)),
    HOURS("Hours", Duration.ofSeconds(3600)),
    HALF_DAYS("HalfDays", Duration.ofSeconds(43200)),
    DAYS("Days", Duration.ofSeconds(86400)),
    WEEKS("Weeks", Duration.ofSeconds(604800)),
    MONTHS("Months", Duration.ofSeconds(2629746)),
    YEARS("Years", Duration.ofSeconds(31556952)),
    DECADES("Decades", Duration.ofSeconds(315569520)),
    CENTURIES("Centuries", Duration.ofSeconds(3155695200L)),
    MILLENNIA("Millennia", Duration.ofSeconds(31556952000L)),
    ERAS("Eras", Duration.ofSeconds(31556952000000000L)),
    FOREVER("Forever", Duration.ofSeconds(Long.MAX_VALUE, 999999999));
    
    private final Duration duration;
    private final String name;

    ChronoUnit(String name, Duration estimatedDuration) {
        this.name = name;
        this.duration = estimatedDuration;
    }

    @Override // j$.time.temporal.TemporalUnit
    public Duration getDuration() {
        return this.duration;
    }

    @Override // j$.time.temporal.TemporalUnit
    public boolean isDurationEstimated() {
        return compareTo(DAYS) >= 0;
    }

    @Override // j$.time.temporal.TemporalUnit
    public boolean isDateBased() {
        return compareTo(DAYS) >= 0 && this != FOREVER;
    }

    @Override // j$.time.temporal.TemporalUnit
    public boolean isTimeBased() {
        return compareTo(DAYS) < 0;
    }

    @Override // j$.time.temporal.TemporalUnit
    public boolean isSupportedBy(Temporal temporal) {
        return temporal.isSupported(this);
    }

    @Override // j$.time.temporal.TemporalUnit
    public <R extends Temporal> R addTo(R temporal, long amount) {
        return (R) temporal.plus(amount, this);
    }

    @Override // j$.time.temporal.TemporalUnit
    public long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive) {
        return temporal1Inclusive.until(temporal2Exclusive, this);
    }

    @Override // java.lang.Enum, j$.time.temporal.TemporalUnit
    public String toString() {
        return this.name;
    }
}
