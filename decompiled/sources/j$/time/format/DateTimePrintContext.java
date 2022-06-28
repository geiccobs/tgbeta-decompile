package j$.time.format;

import j$.time.DateTimeException;
import j$.time.Instant;
import j$.time.ZoneId;
import j$.time.ZoneOffset;
import j$.time.chrono.ChronoLocalDate;
import j$.time.chrono.Chronology;
import j$.time.chrono.IsoChronology;
import j$.time.temporal.ChronoField;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQueries;
import j$.time.temporal.TemporalQuery;
import j$.time.temporal.ValueRange;
import j$.util.Objects;
import java.util.Locale;
/* loaded from: classes2.dex */
public final class DateTimePrintContext {
    private DateTimeFormatter formatter;
    private int optional;
    private TemporalAccessor temporal;

    public DateTimePrintContext(TemporalAccessor temporal, DateTimeFormatter formatter) {
        this.temporal = adjust(temporal, formatter);
        this.formatter = formatter;
    }

    private static TemporalAccessor adjust(final TemporalAccessor temporal, DateTimeFormatter formatter) {
        final ChronoLocalDate effectiveDate;
        ChronoField[] values;
        Chronology overrideChrono = formatter.getChronology();
        ZoneId overrideZone = formatter.getZone();
        if (overrideChrono == null && overrideZone == null) {
            return temporal;
        }
        Chronology temporalChrono = (Chronology) temporal.query(TemporalQueries.chronology());
        ZoneId temporalZone = (ZoneId) temporal.query(TemporalQueries.zoneId());
        if (Objects.equals(overrideChrono, temporalChrono)) {
            overrideChrono = null;
        }
        if (Objects.equals(overrideZone, temporalZone)) {
            overrideZone = null;
        }
        if (overrideChrono == null && overrideZone == null) {
            return temporal;
        }
        final Chronology effectiveChrono = overrideChrono != null ? overrideChrono : temporalChrono;
        if (overrideZone != null) {
            if (temporal.isSupported(ChronoField.INSTANT_SECONDS)) {
                Chronology chrono = effectiveChrono != null ? effectiveChrono : IsoChronology.INSTANCE;
                return chrono.zonedDateTime(Instant.from(temporal), overrideZone);
            } else if ((overrideZone.normalized() instanceof ZoneOffset) && temporal.isSupported(ChronoField.OFFSET_SECONDS) && temporal.get(ChronoField.OFFSET_SECONDS) != overrideZone.getRules().getOffset(Instant.EPOCH).getTotalSeconds()) {
                throw new DateTimeException("Unable to apply override zone '" + overrideZone + "' because the temporal object being formatted has a different offset but does not represent an instant: " + temporal);
            }
        }
        final ZoneId effectiveZone = overrideZone != null ? overrideZone : temporalZone;
        if (overrideChrono != null) {
            if (temporal.isSupported(ChronoField.EPOCH_DAY)) {
                effectiveDate = effectiveChrono.date(temporal);
            } else {
                if (overrideChrono != IsoChronology.INSTANCE || temporalChrono != null) {
                    for (ChronoField f : ChronoField.values()) {
                        if (f.isDateBased() && temporal.isSupported(f)) {
                            throw new DateTimeException("Unable to apply override chronology '" + overrideChrono + "' because the temporal object being formatted contains date fields but does not represent a whole date: " + temporal);
                        }
                    }
                }
                effectiveDate = null;
            }
        } else {
            effectiveDate = null;
        }
        return new TemporalAccessor() { // from class: j$.time.format.DateTimePrintContext.1
            @Override // j$.time.temporal.TemporalAccessor
            public /* synthetic */ int get(TemporalField temporalField) {
                return TemporalAccessor.CC.$default$get(this, temporalField);
            }

            @Override // j$.time.temporal.TemporalAccessor
            public boolean isSupported(TemporalField field) {
                if (effectiveDate != null && field.isDateBased()) {
                    return effectiveDate.isSupported(field);
                }
                return temporal.isSupported(field);
            }

            @Override // j$.time.temporal.TemporalAccessor
            public ValueRange range(TemporalField field) {
                if (effectiveDate != null && field.isDateBased()) {
                    return effectiveDate.range(field);
                }
                return temporal.range(field);
            }

            @Override // j$.time.temporal.TemporalAccessor
            public long getLong(TemporalField field) {
                if (effectiveDate != null && field.isDateBased()) {
                    return effectiveDate.getLong(field);
                }
                return temporal.getLong(field);
            }

            /* JADX WARN: Generic types in debug info not equals: j$.time.temporal.TemporalQuery != java.time.temporal.TemporalQuery<R> */
            @Override // j$.time.temporal.TemporalAccessor
            public <R> R query(TemporalQuery<R> temporalQuery) {
                if (temporalQuery == TemporalQueries.chronology()) {
                    return (R) effectiveChrono;
                }
                if (temporalQuery == TemporalQueries.zoneId()) {
                    return (R) effectiveZone;
                }
                if (temporalQuery == TemporalQueries.precision()) {
                    return (R) temporal.query(temporalQuery);
                }
                return temporalQuery.queryFrom(this);
            }
        };
    }

    public TemporalAccessor getTemporal() {
        return this.temporal;
    }

    public Locale getLocale() {
        return this.formatter.getLocale();
    }

    public DecimalStyle getDecimalStyle() {
        return this.formatter.getDecimalStyle();
    }

    public void startOptional() {
        this.optional++;
    }

    public void endOptional() {
        this.optional--;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.temporal.TemporalQuery != java.time.temporal.TemporalQuery<R> */
    public <R> R getValue(TemporalQuery<R> temporalQuery) {
        R result = (R) this.temporal.query(temporalQuery);
        if (result == null && this.optional == 0) {
            throw new DateTimeException("Unable to extract value: " + this.temporal.getClass());
        }
        return result;
    }

    public Long getValue(TemporalField field) {
        try {
            return Long.valueOf(this.temporal.getLong(field));
        } catch (DateTimeException ex) {
            if (this.optional > 0) {
                return null;
            }
            throw ex;
        }
    }

    public String toString() {
        return this.temporal.toString();
    }
}
