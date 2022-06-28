package j$.time;

import com.google.android.exoplayer2.C;
import j$.time.temporal.TemporalAmount;
import j$.util.Objects;
import java.io.Serializable;
/* loaded from: classes2.dex */
public abstract class Clock {
    public abstract ZoneId getZone();

    public abstract Instant instant();

    public abstract Clock withZone(ZoneId zoneId);

    public static Clock systemUTC() {
        return new SystemClock(ZoneOffset.UTC);
    }

    public static Clock systemDefaultZone() {
        return new SystemClock(ZoneId.systemDefault());
    }

    public static Clock system(ZoneId zone) {
        Objects.requireNonNull(zone, "zone");
        return new SystemClock(zone);
    }

    public static Clock tickSeconds(ZoneId zone) {
        return new TickClock(system(zone), C.NANOS_PER_SECOND);
    }

    public static Clock tickMinutes(ZoneId zone) {
        return new TickClock(system(zone), 60000000000L);
    }

    public static Clock tick(Clock baseClock, Duration tickDuration) {
        Objects.requireNonNull(baseClock, "baseClock");
        Objects.requireNonNull(tickDuration, "tickDuration");
        if (tickDuration.isNegative()) {
            throw new IllegalArgumentException("Tick duration must not be negative");
        }
        long tickNanos = tickDuration.toNanos();
        if (tickNanos % 1000000 != 0 && C.NANOS_PER_SECOND % tickNanos != 0) {
            throw new IllegalArgumentException("Invalid tick duration");
        }
        if (tickNanos <= 1) {
            return baseClock;
        }
        return new TickClock(baseClock, tickNanos);
    }

    public static Clock fixed(Instant fixedInstant, ZoneId zone) {
        Objects.requireNonNull(fixedInstant, "fixedInstant");
        Objects.requireNonNull(zone, "zone");
        return new FixedClock(fixedInstant, zone);
    }

    public static Clock offset(Clock baseClock, Duration offsetDuration) {
        Objects.requireNonNull(baseClock, "baseClock");
        Objects.requireNonNull(offsetDuration, "offsetDuration");
        if (offsetDuration.equals(Duration.ZERO)) {
            return baseClock;
        }
        return new OffsetClock(baseClock, offsetDuration);
    }

    protected Clock() {
    }

    public long millis() {
        return instant().toEpochMilli();
    }

    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public int hashCode() {
        return super.hashCode();
    }

    /* loaded from: classes2.dex */
    public static final class SystemClock extends Clock implements Serializable {
        private static final long serialVersionUID = 6740630888130243051L;
        private final ZoneId zone;

        SystemClock(ZoneId zone) {
            this.zone = zone;
        }

        @Override // j$.time.Clock
        public ZoneId getZone() {
            return this.zone;
        }

        @Override // j$.time.Clock
        public Clock withZone(ZoneId zone) {
            if (zone.equals(this.zone)) {
                return this;
            }
            return new SystemClock(zone);
        }

        @Override // j$.time.Clock
        public long millis() {
            return System.currentTimeMillis();
        }

        @Override // j$.time.Clock
        public Instant instant() {
            return Instant.ofEpochMilli(millis());
        }

        @Override // j$.time.Clock
        public boolean equals(Object obj) {
            if (obj instanceof SystemClock) {
                return this.zone.equals(((SystemClock) obj).zone);
            }
            return false;
        }

        @Override // j$.time.Clock
        public int hashCode() {
            return this.zone.hashCode() + 1;
        }

        public String toString() {
            return "SystemClock[" + this.zone + "]";
        }
    }

    /* loaded from: classes2.dex */
    static final class FixedClock extends Clock implements Serializable {
        private static final long serialVersionUID = 7430389292664866958L;
        private final Instant instant;
        private final ZoneId zone;

        FixedClock(Instant fixedInstant, ZoneId zone) {
            this.instant = fixedInstant;
            this.zone = zone;
        }

        @Override // j$.time.Clock
        public ZoneId getZone() {
            return this.zone;
        }

        @Override // j$.time.Clock
        public Clock withZone(ZoneId zone) {
            if (zone.equals(this.zone)) {
                return this;
            }
            return new FixedClock(this.instant, zone);
        }

        @Override // j$.time.Clock
        public long millis() {
            return this.instant.toEpochMilli();
        }

        @Override // j$.time.Clock
        public Instant instant() {
            return this.instant;
        }

        @Override // j$.time.Clock
        public boolean equals(Object obj) {
            if (obj instanceof FixedClock) {
                FixedClock other = (FixedClock) obj;
                return this.instant.equals(other.instant) && this.zone.equals(other.zone);
            }
            return false;
        }

        @Override // j$.time.Clock
        public int hashCode() {
            return this.instant.hashCode() ^ this.zone.hashCode();
        }

        public String toString() {
            return "FixedClock[" + this.instant + "," + this.zone + "]";
        }
    }

    /* loaded from: classes2.dex */
    static final class OffsetClock extends Clock implements Serializable {
        private static final long serialVersionUID = 2007484719125426256L;
        private final Clock baseClock;
        private final Duration offset;

        OffsetClock(Clock baseClock, Duration offset) {
            this.baseClock = baseClock;
            this.offset = offset;
        }

        @Override // j$.time.Clock
        public ZoneId getZone() {
            return this.baseClock.getZone();
        }

        @Override // j$.time.Clock
        public Clock withZone(ZoneId zone) {
            if (zone.equals(this.baseClock.getZone())) {
                return this;
            }
            return new OffsetClock(this.baseClock.withZone(zone), this.offset);
        }

        @Override // j$.time.Clock
        public long millis() {
            return Clock$OffsetClock$$ExternalSyntheticBackport0.m(this.baseClock.millis(), this.offset.toMillis());
        }

        @Override // j$.time.Clock
        public Instant instant() {
            return this.baseClock.instant().plus((TemporalAmount) this.offset);
        }

        @Override // j$.time.Clock
        public boolean equals(Object obj) {
            if (obj instanceof OffsetClock) {
                OffsetClock other = (OffsetClock) obj;
                return this.baseClock.equals(other.baseClock) && this.offset.equals(other.offset);
            }
            return false;
        }

        @Override // j$.time.Clock
        public int hashCode() {
            return this.baseClock.hashCode() ^ this.offset.hashCode();
        }

        public String toString() {
            return "OffsetClock[" + this.baseClock + "," + this.offset + "]";
        }
    }

    /* loaded from: classes2.dex */
    static final class TickClock extends Clock implements Serializable {
        private static final long serialVersionUID = 6504659149906368850L;
        private final Clock baseClock;
        private final long tickNanos;

        TickClock(Clock baseClock, long tickNanos) {
            this.baseClock = baseClock;
            this.tickNanos = tickNanos;
        }

        @Override // j$.time.Clock
        public ZoneId getZone() {
            return this.baseClock.getZone();
        }

        @Override // j$.time.Clock
        public Clock withZone(ZoneId zone) {
            if (zone.equals(this.baseClock.getZone())) {
                return this;
            }
            return new TickClock(this.baseClock.withZone(zone), this.tickNanos);
        }

        @Override // j$.time.Clock
        public long millis() {
            long millis = this.baseClock.millis();
            return millis - Clock$TickClock$$ExternalSyntheticBackport0.m(millis, this.tickNanos / 1000000);
        }

        @Override // j$.time.Clock
        public Instant instant() {
            if (this.tickNanos % 1000000 == 0) {
                long millis = this.baseClock.millis();
                return Instant.ofEpochMilli(millis - Clock$TickClock$$ExternalSyntheticBackport0.m(millis, this.tickNanos / 1000000));
            }
            Instant instant = this.baseClock.instant();
            long nanos = instant.getNano();
            long adjust = Clock$TickClock$$ExternalSyntheticBackport0.m(nanos, this.tickNanos);
            return instant.minusNanos(adjust);
        }

        @Override // j$.time.Clock
        public boolean equals(Object obj) {
            if (obj instanceof TickClock) {
                TickClock other = (TickClock) obj;
                return this.baseClock.equals(other.baseClock) && this.tickNanos == other.tickNanos;
            }
            return false;
        }

        @Override // j$.time.Clock
        public int hashCode() {
            int hashCode = this.baseClock.hashCode();
            long j = this.tickNanos;
            return hashCode ^ ((int) (j ^ (j >>> 32)));
        }

        public String toString() {
            return "TickClock[" + this.baseClock + "," + Duration.ofNanos(this.tickNanos) + "]";
        }
    }
}
