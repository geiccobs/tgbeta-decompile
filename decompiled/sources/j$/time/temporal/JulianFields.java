package j$.time.temporal;

import j$.time.DateTimeException;
import j$.time.Instant$$ExternalSyntheticBackport0;
import j$.time.chrono.ChronoLocalDate;
import j$.time.chrono.Chronology;
import j$.time.format.ResolverStyle;
import j$.util.Objects;
import java.util.Locale;
import java.util.Map;
/* loaded from: classes2.dex */
public final class JulianFields {
    private static final long JULIAN_DAY_OFFSET = 2440588;
    public static final TemporalField JULIAN_DAY = Field.JULIAN_DAY;
    public static final TemporalField MODIFIED_JULIAN_DAY = Field.MODIFIED_JULIAN_DAY;
    public static final TemporalField RATA_DIE = Field.RATA_DIE;

    private JulianFields() {
        throw new AssertionError("Not instantiable");
    }

    /* loaded from: classes2.dex */
    private enum Field implements TemporalField {
        JULIAN_DAY("JulianDay", ChronoUnit.DAYS, ChronoUnit.FOREVER, JulianFields.JULIAN_DAY_OFFSET),
        MODIFIED_JULIAN_DAY("ModifiedJulianDay", ChronoUnit.DAYS, ChronoUnit.FOREVER, 40587),
        RATA_DIE("RataDie", ChronoUnit.DAYS, ChronoUnit.FOREVER, 719163);
        
        private static final long serialVersionUID = -7501623920830201812L;
        private final transient TemporalUnit baseUnit;
        private final transient String name;
        private final transient long offset;
        private final transient ValueRange range;
        private final transient TemporalUnit rangeUnit;

        @Override // j$.time.temporal.TemporalField
        public /* synthetic */ String getDisplayName(Locale locale) {
            return Objects.requireNonNull(locale, "locale");
        }

        Field(String name, TemporalUnit baseUnit, TemporalUnit rangeUnit, long offset) {
            this.name = name;
            this.baseUnit = baseUnit;
            this.rangeUnit = rangeUnit;
            this.range = ValueRange.of((-365243219162L) + offset, 365241780471L + offset);
            this.offset = offset;
        }

        @Override // j$.time.temporal.TemporalField
        public TemporalUnit getBaseUnit() {
            return this.baseUnit;
        }

        @Override // j$.time.temporal.TemporalField
        public TemporalUnit getRangeUnit() {
            return this.rangeUnit;
        }

        @Override // j$.time.temporal.TemporalField
        public boolean isDateBased() {
            return true;
        }

        @Override // j$.time.temporal.TemporalField
        public boolean isTimeBased() {
            return false;
        }

        @Override // j$.time.temporal.TemporalField
        public ValueRange range() {
            return this.range;
        }

        @Override // j$.time.temporal.TemporalField
        public boolean isSupportedBy(TemporalAccessor temporal) {
            return temporal.isSupported(ChronoField.EPOCH_DAY);
        }

        @Override // j$.time.temporal.TemporalField
        public ValueRange rangeRefinedBy(TemporalAccessor temporal) {
            if (!isSupportedBy(temporal)) {
                throw new DateTimeException("Unsupported field: " + this);
            }
            return range();
        }

        @Override // j$.time.temporal.TemporalField
        public long getFrom(TemporalAccessor temporal) {
            return temporal.getLong(ChronoField.EPOCH_DAY) + this.offset;
        }

        @Override // j$.time.temporal.TemporalField
        public <R extends Temporal> R adjustInto(R temporal, long newValue) {
            if (!range().isValidValue(newValue)) {
                throw new DateTimeException("Invalid value: " + this.name + " " + newValue);
            }
            return (R) temporal.with(ChronoField.EPOCH_DAY, Instant$$ExternalSyntheticBackport0.m(newValue, this.offset));
        }

        @Override // j$.time.temporal.TemporalField
        public ChronoLocalDate resolve(Map<TemporalField, Long> map, TemporalAccessor partialTemporal, ResolverStyle resolverStyle) {
            long value = map.remove(this).longValue();
            Chronology chrono = Chronology.CC.from(partialTemporal);
            if (resolverStyle == ResolverStyle.LENIENT) {
                return chrono.dateEpochDay(Instant$$ExternalSyntheticBackport0.m(value, this.offset));
            }
            range().checkValidValue(value, this);
            return chrono.dateEpochDay(value - this.offset);
        }

        @Override // java.lang.Enum, j$.time.temporal.TemporalField
        public String toString() {
            return this.name;
        }
    }
}
