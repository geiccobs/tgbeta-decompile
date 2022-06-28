package j$.time.format;

import com.google.android.exoplayer2.C;
import j$.time.Clock$OffsetClock$$ExternalSyntheticBackport0;
import j$.time.Clock$TickClock$$ExternalSyntheticBackport0;
import j$.time.DateTimeException;
import j$.time.Duration$$ExternalSyntheticBackport0;
import j$.time.Duration$$ExternalSyntheticBackport1;
import j$.time.Instant;
import j$.time.LocalDate;
import j$.time.LocalTime;
import j$.time.Period;
import j$.time.ZoneId;
import j$.time.ZoneOffset;
import j$.time.chrono.ChronoLocalDate;
import j$.time.chrono.ChronoLocalDateTime;
import j$.time.chrono.ChronoZonedDateTime;
import j$.time.chrono.Chronology;
import j$.time.temporal.ChronoField;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalAmount;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQueries;
import j$.time.temporal.TemporalQuery;
import j$.time.temporal.UnsupportedTemporalTypeException;
import j$.time.temporal.ValueRange;
import j$.util.Objects;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/* loaded from: classes2.dex */
public final class Parsed implements TemporalAccessor {
    Chronology chrono;
    private ChronoLocalDate date;
    boolean leapSecond;
    private ResolverStyle resolverStyle;
    private LocalTime time;
    ZoneId zone;
    final Map<TemporalField, Long> fieldValues = new HashMap();
    Period excessDays = Period.ZERO;

    @Override // j$.time.temporal.TemporalAccessor
    public /* synthetic */ int get(TemporalField temporalField) {
        return TemporalAccessor.CC.$default$get(this, temporalField);
    }

    @Override // j$.time.temporal.TemporalAccessor
    public /* synthetic */ ValueRange range(TemporalField temporalField) {
        return TemporalAccessor.CC.$default$range(this, temporalField);
    }

    public Parsed copy() {
        Parsed cloned = new Parsed();
        cloned.fieldValues.putAll(this.fieldValues);
        cloned.zone = this.zone;
        cloned.chrono = this.chrono;
        cloned.leapSecond = this.leapSecond;
        return cloned;
    }

    @Override // j$.time.temporal.TemporalAccessor
    public boolean isSupported(TemporalField field) {
        ChronoLocalDate chronoLocalDate;
        LocalTime localTime;
        if (this.fieldValues.containsKey(field) || (((chronoLocalDate = this.date) != null && chronoLocalDate.isSupported(field)) || ((localTime = this.time) != null && localTime.isSupported(field)))) {
            return true;
        }
        return field != null && !(field instanceof ChronoField) && field.isSupportedBy(this);
    }

    @Override // j$.time.temporal.TemporalAccessor
    public long getLong(TemporalField field) {
        Objects.requireNonNull(field, "field");
        Long value = this.fieldValues.get(field);
        if (value != null) {
            return value.longValue();
        }
        ChronoLocalDate chronoLocalDate = this.date;
        if (chronoLocalDate != null && chronoLocalDate.isSupported(field)) {
            return this.date.getLong(field);
        }
        LocalTime localTime = this.time;
        if (localTime != null && localTime.isSupported(field)) {
            return this.time.getLong(field);
        }
        if (field instanceof ChronoField) {
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return field.getFrom(this);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.temporal.TemporalQuery != java.time.temporal.TemporalQuery<R> */
    @Override // j$.time.temporal.TemporalAccessor
    public <R> R query(TemporalQuery<R> temporalQuery) {
        if (temporalQuery == TemporalQueries.zoneId()) {
            return (R) this.zone;
        }
        if (temporalQuery == TemporalQueries.chronology()) {
            return (R) this.chrono;
        }
        if (temporalQuery == TemporalQueries.localDate()) {
            ChronoLocalDate chronoLocalDate = this.date;
            if (chronoLocalDate == null) {
                return null;
            }
            return (R) LocalDate.from(chronoLocalDate);
        } else if (temporalQuery == TemporalQueries.localTime()) {
            return (R) this.time;
        } else {
            if (temporalQuery == TemporalQueries.zone() || temporalQuery == TemporalQueries.offset()) {
                return temporalQuery.queryFrom(this);
            }
            if (temporalQuery != TemporalQueries.precision()) {
                return temporalQuery.queryFrom(this);
            }
            return null;
        }
    }

    public TemporalAccessor resolve(ResolverStyle resolverStyle, Set<TemporalField> set) {
        if (set != null) {
            this.fieldValues.keySet().retainAll(set);
        }
        this.resolverStyle = resolverStyle;
        resolveFields();
        resolveTimeLenient();
        crossCheck();
        resolvePeriod();
        resolveFractional();
        resolveInstant();
        return this;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTime != java.time.chrono.ChronoLocalDateTime<?> */
    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoZonedDateTime != java.time.chrono.ChronoZonedDateTime<?> */
    private void resolveFields() {
        resolveInstantFields();
        resolveDateFields();
        resolveTimeFields();
        if (this.fieldValues.size() > 0) {
            int changedCount = 0;
            loop0: while (changedCount < 50) {
                for (Map.Entry<TemporalField, Long> entry : this.fieldValues.entrySet()) {
                    TemporalField targetField = entry.getKey();
                    TemporalAccessor resolvedObject = targetField.resolve(this.fieldValues, this, this.resolverStyle);
                    if (resolvedObject != null) {
                        if (resolvedObject instanceof ChronoZonedDateTime) {
                            ChronoZonedDateTime chronoZonedDateTime = (ChronoZonedDateTime) resolvedObject;
                            ZoneId zoneId = this.zone;
                            if (zoneId == null) {
                                this.zone = chronoZonedDateTime.getZone();
                            } else if (!zoneId.equals(chronoZonedDateTime.getZone())) {
                                throw new DateTimeException("ChronoZonedDateTime must use the effective parsed zone: " + this.zone);
                            }
                            resolvedObject = chronoZonedDateTime.toLocalDateTime();
                        }
                        if (resolvedObject instanceof ChronoLocalDateTime) {
                            ChronoLocalDateTime chronoLocalDateTime = (ChronoLocalDateTime) resolvedObject;
                            updateCheckConflict(chronoLocalDateTime.toLocalTime(), Period.ZERO);
                            updateCheckConflict(chronoLocalDateTime.toLocalDate());
                            changedCount++;
                        } else if (resolvedObject instanceof ChronoLocalDate) {
                            updateCheckConflict(resolvedObject);
                            changedCount++;
                        } else if (resolvedObject instanceof LocalTime) {
                            updateCheckConflict(resolvedObject, Period.ZERO);
                            changedCount++;
                        } else {
                            throw new DateTimeException("Method resolve() can only return ChronoZonedDateTime, ChronoLocalDateTime, ChronoLocalDate or LocalTime");
                        }
                    } else if (!this.fieldValues.containsKey(targetField)) {
                        changedCount++;
                    }
                }
            }
            if (changedCount == 50) {
                throw new DateTimeException("One of the parsed fields has an incorrectly implemented resolve method");
            }
            if (changedCount > 0) {
                resolveInstantFields();
                resolveDateFields();
                resolveTimeFields();
            }
        }
    }

    private void updateCheckConflict(TemporalField targetField, TemporalField changeField, Long changeValue) {
        Long old = this.fieldValues.put(changeField, changeValue);
        if (old != null && old.longValue() != changeValue.longValue()) {
            throw new DateTimeException("Conflict found: " + changeField + " " + old + " differs from " + changeField + " " + changeValue + " while resolving  " + targetField);
        }
    }

    private void resolveInstantFields() {
        if (this.fieldValues.containsKey(ChronoField.INSTANT_SECONDS)) {
            ZoneId zoneId = this.zone;
            if (zoneId != null) {
                resolveInstantFields0(zoneId);
                return;
            }
            Long offsetSecs = this.fieldValues.get(ChronoField.OFFSET_SECONDS);
            if (offsetSecs != null) {
                ZoneOffset offset = ZoneOffset.ofTotalSeconds(offsetSecs.intValue());
                resolveInstantFields0(offset);
            }
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoZonedDateTime != java.time.chrono.ChronoZonedDateTime<?> */
    private void resolveInstantFields0(ZoneId selectedZone) {
        Instant instant = Instant.ofEpochSecond(this.fieldValues.remove(ChronoField.INSTANT_SECONDS).longValue());
        ChronoZonedDateTime<? extends ChronoLocalDate> zonedDateTime = this.chrono.zonedDateTime(instant, selectedZone);
        updateCheckConflict(zonedDateTime.toLocalDate());
        updateCheckConflict(ChronoField.INSTANT_SECONDS, ChronoField.SECOND_OF_DAY, Long.valueOf(zonedDateTime.toLocalTime().toSecondOfDay()));
    }

    private void resolveDateFields() {
        updateCheckConflict(this.chrono.resolveDate(this.fieldValues, this.resolverStyle));
    }

    private void updateCheckConflict(ChronoLocalDate cld) {
        ChronoLocalDate chronoLocalDate = this.date;
        if (chronoLocalDate != null) {
            if (cld != null && !chronoLocalDate.equals(cld)) {
                throw new DateTimeException("Conflict found: Fields resolved to two different dates: " + this.date + " " + cld);
            }
        } else if (cld != null) {
            if (!this.chrono.equals(cld.getChronology())) {
                throw new DateTimeException("ChronoLocalDate must use the effective parsed chronology: " + this.chrono);
            }
            this.date = cld;
        }
    }

    private void resolveTimeFields() {
        long j = 0;
        if (this.fieldValues.containsKey(ChronoField.CLOCK_HOUR_OF_DAY)) {
            long ch = this.fieldValues.remove(ChronoField.CLOCK_HOUR_OF_DAY).longValue();
            if (this.resolverStyle == ResolverStyle.STRICT || (this.resolverStyle == ResolverStyle.SMART && ch != 0)) {
                ChronoField.CLOCK_HOUR_OF_DAY.checkValidValue(ch);
            }
            updateCheckConflict(ChronoField.CLOCK_HOUR_OF_DAY, ChronoField.HOUR_OF_DAY, Long.valueOf(ch == 24 ? 0L : ch));
        }
        if (this.fieldValues.containsKey(ChronoField.CLOCK_HOUR_OF_AMPM)) {
            long ch2 = this.fieldValues.remove(ChronoField.CLOCK_HOUR_OF_AMPM).longValue();
            if (this.resolverStyle == ResolverStyle.STRICT || (this.resolverStyle == ResolverStyle.SMART && ch2 != 0)) {
                ChronoField.CLOCK_HOUR_OF_AMPM.checkValidValue(ch2);
            }
            ChronoField chronoField = ChronoField.CLOCK_HOUR_OF_AMPM;
            ChronoField chronoField2 = ChronoField.HOUR_OF_AMPM;
            if (ch2 != 12) {
                j = ch2;
            }
            updateCheckConflict(chronoField, chronoField2, Long.valueOf(j));
        }
        if (this.fieldValues.containsKey(ChronoField.AMPM_OF_DAY) && this.fieldValues.containsKey(ChronoField.HOUR_OF_AMPM)) {
            long ap = this.fieldValues.remove(ChronoField.AMPM_OF_DAY).longValue();
            long hap = this.fieldValues.remove(ChronoField.HOUR_OF_AMPM).longValue();
            if (this.resolverStyle == ResolverStyle.LENIENT) {
                updateCheckConflict(ChronoField.AMPM_OF_DAY, ChronoField.HOUR_OF_DAY, Long.valueOf(Clock$OffsetClock$$ExternalSyntheticBackport0.m(Duration$$ExternalSyntheticBackport1.m(ap, 12L), hap)));
            } else {
                ChronoField.AMPM_OF_DAY.checkValidValue(ap);
                ChronoField.HOUR_OF_AMPM.checkValidValue(ap);
                updateCheckConflict(ChronoField.AMPM_OF_DAY, ChronoField.HOUR_OF_DAY, Long.valueOf((12 * ap) + hap));
            }
        }
        if (this.fieldValues.containsKey(ChronoField.NANO_OF_DAY)) {
            long nod = this.fieldValues.remove(ChronoField.NANO_OF_DAY).longValue();
            if (this.resolverStyle != ResolverStyle.LENIENT) {
                ChronoField.NANO_OF_DAY.checkValidValue(nod);
            }
            updateCheckConflict(ChronoField.NANO_OF_DAY, ChronoField.HOUR_OF_DAY, Long.valueOf(nod / 3600000000000L));
            updateCheckConflict(ChronoField.NANO_OF_DAY, ChronoField.MINUTE_OF_HOUR, Long.valueOf((nod / 60000000000L) % 60));
            updateCheckConflict(ChronoField.NANO_OF_DAY, ChronoField.SECOND_OF_MINUTE, Long.valueOf((nod / C.NANOS_PER_SECOND) % 60));
            updateCheckConflict(ChronoField.NANO_OF_DAY, ChronoField.NANO_OF_SECOND, Long.valueOf(nod % C.NANOS_PER_SECOND));
        }
        if (this.fieldValues.containsKey(ChronoField.MICRO_OF_DAY)) {
            long cod = this.fieldValues.remove(ChronoField.MICRO_OF_DAY).longValue();
            if (this.resolverStyle != ResolverStyle.LENIENT) {
                ChronoField.MICRO_OF_DAY.checkValidValue(cod);
            }
            updateCheckConflict(ChronoField.MICRO_OF_DAY, ChronoField.SECOND_OF_DAY, Long.valueOf(cod / 1000000));
            updateCheckConflict(ChronoField.MICRO_OF_DAY, ChronoField.MICRO_OF_SECOND, Long.valueOf(cod % 1000000));
        }
        if (this.fieldValues.containsKey(ChronoField.MILLI_OF_DAY)) {
            long lod = this.fieldValues.remove(ChronoField.MILLI_OF_DAY).longValue();
            if (this.resolverStyle != ResolverStyle.LENIENT) {
                ChronoField.MILLI_OF_DAY.checkValidValue(lod);
            }
            updateCheckConflict(ChronoField.MILLI_OF_DAY, ChronoField.SECOND_OF_DAY, Long.valueOf(lod / 1000));
            updateCheckConflict(ChronoField.MILLI_OF_DAY, ChronoField.MILLI_OF_SECOND, Long.valueOf(lod % 1000));
        }
        if (this.fieldValues.containsKey(ChronoField.SECOND_OF_DAY)) {
            long sod = this.fieldValues.remove(ChronoField.SECOND_OF_DAY).longValue();
            if (this.resolverStyle != ResolverStyle.LENIENT) {
                ChronoField.SECOND_OF_DAY.checkValidValue(sod);
            }
            updateCheckConflict(ChronoField.SECOND_OF_DAY, ChronoField.HOUR_OF_DAY, Long.valueOf(sod / 3600));
            updateCheckConflict(ChronoField.SECOND_OF_DAY, ChronoField.MINUTE_OF_HOUR, Long.valueOf((sod / 60) % 60));
            updateCheckConflict(ChronoField.SECOND_OF_DAY, ChronoField.SECOND_OF_MINUTE, Long.valueOf(sod % 60));
        }
        if (this.fieldValues.containsKey(ChronoField.MINUTE_OF_DAY)) {
            long mod = this.fieldValues.remove(ChronoField.MINUTE_OF_DAY).longValue();
            if (this.resolverStyle != ResolverStyle.LENIENT) {
                ChronoField.MINUTE_OF_DAY.checkValidValue(mod);
            }
            updateCheckConflict(ChronoField.MINUTE_OF_DAY, ChronoField.HOUR_OF_DAY, Long.valueOf(mod / 60));
            updateCheckConflict(ChronoField.MINUTE_OF_DAY, ChronoField.MINUTE_OF_HOUR, Long.valueOf(mod % 60));
        }
        if (this.fieldValues.containsKey(ChronoField.NANO_OF_SECOND)) {
            long nos = this.fieldValues.get(ChronoField.NANO_OF_SECOND).longValue();
            if (this.resolverStyle != ResolverStyle.LENIENT) {
                ChronoField.NANO_OF_SECOND.checkValidValue(nos);
            }
            if (this.fieldValues.containsKey(ChronoField.MICRO_OF_SECOND)) {
                long cos = this.fieldValues.remove(ChronoField.MICRO_OF_SECOND).longValue();
                if (this.resolverStyle != ResolverStyle.LENIENT) {
                    ChronoField.MICRO_OF_SECOND.checkValidValue(cos);
                }
                nos = (cos * 1000) + (nos % 1000);
                updateCheckConflict(ChronoField.MICRO_OF_SECOND, ChronoField.NANO_OF_SECOND, Long.valueOf(nos));
            }
            if (this.fieldValues.containsKey(ChronoField.MILLI_OF_SECOND)) {
                long los = this.fieldValues.remove(ChronoField.MILLI_OF_SECOND).longValue();
                if (this.resolverStyle != ResolverStyle.LENIENT) {
                    ChronoField.MILLI_OF_SECOND.checkValidValue(los);
                }
                updateCheckConflict(ChronoField.MILLI_OF_SECOND, ChronoField.NANO_OF_SECOND, Long.valueOf((los * 1000000) + (nos % 1000000)));
            }
        }
        if (this.fieldValues.containsKey(ChronoField.HOUR_OF_DAY) && this.fieldValues.containsKey(ChronoField.MINUTE_OF_HOUR) && this.fieldValues.containsKey(ChronoField.SECOND_OF_MINUTE) && this.fieldValues.containsKey(ChronoField.NANO_OF_SECOND)) {
            long hod = this.fieldValues.remove(ChronoField.HOUR_OF_DAY).longValue();
            long moh = this.fieldValues.remove(ChronoField.MINUTE_OF_HOUR).longValue();
            long som = this.fieldValues.remove(ChronoField.SECOND_OF_MINUTE).longValue();
            resolveTime(hod, moh, som, this.fieldValues.remove(ChronoField.NANO_OF_SECOND).longValue());
        }
    }

    private void resolveTimeLenient() {
        if (this.time == null) {
            if (this.fieldValues.containsKey(ChronoField.MILLI_OF_SECOND)) {
                long los = this.fieldValues.remove(ChronoField.MILLI_OF_SECOND).longValue();
                if (this.fieldValues.containsKey(ChronoField.MICRO_OF_SECOND)) {
                    long cos = (los * 1000) + (this.fieldValues.get(ChronoField.MICRO_OF_SECOND).longValue() % 1000);
                    updateCheckConflict(ChronoField.MILLI_OF_SECOND, ChronoField.MICRO_OF_SECOND, Long.valueOf(cos));
                    this.fieldValues.remove(ChronoField.MICRO_OF_SECOND);
                    this.fieldValues.put(ChronoField.NANO_OF_SECOND, Long.valueOf(1000 * cos));
                } else {
                    this.fieldValues.put(ChronoField.NANO_OF_SECOND, Long.valueOf(1000000 * los));
                }
            } else if (this.fieldValues.containsKey(ChronoField.MICRO_OF_SECOND)) {
                this.fieldValues.put(ChronoField.NANO_OF_SECOND, Long.valueOf(1000 * this.fieldValues.remove(ChronoField.MICRO_OF_SECOND).longValue()));
            }
            Long hod = this.fieldValues.get(ChronoField.HOUR_OF_DAY);
            if (hod != null) {
                Long moh = this.fieldValues.get(ChronoField.MINUTE_OF_HOUR);
                Long som = this.fieldValues.get(ChronoField.SECOND_OF_MINUTE);
                Long nos = this.fieldValues.get(ChronoField.NANO_OF_SECOND);
                if (moh == null && (som != null || nos != null)) {
                    return;
                }
                if (moh != null && som == null && nos != null) {
                    return;
                }
                long j = 0;
                long mohVal = moh != null ? moh.longValue() : 0L;
                long somVal = som != null ? som.longValue() : 0L;
                if (nos != null) {
                    j = nos.longValue();
                }
                long nosVal = j;
                resolveTime(hod.longValue(), mohVal, somVal, nosVal);
                this.fieldValues.remove(ChronoField.HOUR_OF_DAY);
                this.fieldValues.remove(ChronoField.MINUTE_OF_HOUR);
                this.fieldValues.remove(ChronoField.SECOND_OF_MINUTE);
                this.fieldValues.remove(ChronoField.NANO_OF_SECOND);
            }
        }
        if (this.resolverStyle != ResolverStyle.LENIENT && this.fieldValues.size() > 0) {
            for (Map.Entry<TemporalField, Long> entry : this.fieldValues.entrySet()) {
                TemporalField field = entry.getKey();
                if ((field instanceof ChronoField) && field.isTimeBased()) {
                    ((ChronoField) field).checkValidValue(entry.getValue().longValue());
                }
            }
        }
    }

    private void resolveTime(long hod, long moh, long som, long nos) {
        if (this.resolverStyle == ResolverStyle.LENIENT) {
            long totalNanos = Clock$OffsetClock$$ExternalSyntheticBackport0.m(Clock$OffsetClock$$ExternalSyntheticBackport0.m(Clock$OffsetClock$$ExternalSyntheticBackport0.m(Duration$$ExternalSyntheticBackport1.m(hod, 3600000000000L), Duration$$ExternalSyntheticBackport1.m(moh, 60000000000L)), Duration$$ExternalSyntheticBackport1.m(som, C.NANOS_PER_SECOND)), nos);
            int excessDays = (int) Duration$$ExternalSyntheticBackport0.m(totalNanos, 86400000000000L);
            long nod = Clock$TickClock$$ExternalSyntheticBackport0.m(totalNanos, 86400000000000L);
            updateCheckConflict(LocalTime.ofNanoOfDay(nod), Period.ofDays(excessDays));
            return;
        }
        int mohVal = ChronoField.MINUTE_OF_HOUR.checkValidIntValue(moh);
        int nosVal = ChronoField.NANO_OF_SECOND.checkValidIntValue(nos);
        if (this.resolverStyle == ResolverStyle.SMART && hod == 24 && mohVal == 0 && som == 0 && nosVal == 0) {
            updateCheckConflict(LocalTime.MIDNIGHT, Period.ofDays(1));
            return;
        }
        int hodVal = ChronoField.HOUR_OF_DAY.checkValidIntValue(hod);
        int somVal = ChronoField.SECOND_OF_MINUTE.checkValidIntValue(som);
        updateCheckConflict(LocalTime.of(hodVal, mohVal, somVal, nosVal), Period.ZERO);
    }

    private void resolvePeriod() {
        if (this.date != null && this.time != null && !this.excessDays.isZero()) {
            this.date = this.date.plus((TemporalAmount) this.excessDays);
            this.excessDays = Period.ZERO;
        }
    }

    private void resolveFractional() {
        if (this.time == null) {
            if (this.fieldValues.containsKey(ChronoField.INSTANT_SECONDS) || this.fieldValues.containsKey(ChronoField.SECOND_OF_DAY) || this.fieldValues.containsKey(ChronoField.SECOND_OF_MINUTE)) {
                if (this.fieldValues.containsKey(ChronoField.NANO_OF_SECOND)) {
                    long nos = this.fieldValues.get(ChronoField.NANO_OF_SECOND).longValue();
                    this.fieldValues.put(ChronoField.MICRO_OF_SECOND, Long.valueOf(nos / 1000));
                    this.fieldValues.put(ChronoField.MILLI_OF_SECOND, Long.valueOf(nos / 1000000));
                    return;
                }
                this.fieldValues.put(ChronoField.NANO_OF_SECOND, 0L);
                this.fieldValues.put(ChronoField.MICRO_OF_SECOND, 0L);
                this.fieldValues.put(ChronoField.MILLI_OF_SECOND, 0L);
            }
        }
    }

    private void resolveInstant() {
        LocalTime localTime;
        ChronoLocalDate chronoLocalDate = this.date;
        if (chronoLocalDate != null && (localTime = this.time) != null) {
            if (this.zone != null) {
                long instant = chronoLocalDate.atTime(localTime).atZone(this.zone).getLong(ChronoField.INSTANT_SECONDS);
                this.fieldValues.put(ChronoField.INSTANT_SECONDS, Long.valueOf(instant));
                return;
            }
            Long offsetSecs = this.fieldValues.get(ChronoField.OFFSET_SECONDS);
            if (offsetSecs != null) {
                ZoneOffset offset = ZoneOffset.ofTotalSeconds(offsetSecs.intValue());
                long instant2 = this.date.atTime(this.time).atZone(offset).getLong(ChronoField.INSTANT_SECONDS);
                this.fieldValues.put(ChronoField.INSTANT_SECONDS, Long.valueOf(instant2));
            }
        }
    }

    private void updateCheckConflict(LocalTime timeToSet, Period periodToSet) {
        LocalTime localTime = this.time;
        if (localTime != null) {
            if (!localTime.equals(timeToSet)) {
                throw new DateTimeException("Conflict found: Fields resolved to different times: " + this.time + " " + timeToSet);
            } else if (!this.excessDays.isZero() && !periodToSet.isZero() && !this.excessDays.equals(periodToSet)) {
                throw new DateTimeException("Conflict found: Fields resolved to different excess periods: " + this.excessDays + " " + periodToSet);
            } else {
                this.excessDays = periodToSet;
                return;
            }
        }
        this.time = timeToSet;
        this.excessDays = periodToSet;
    }

    private void crossCheck() {
        ChronoLocalDate chronoLocalDate = this.date;
        if (chronoLocalDate != null) {
            crossCheck(chronoLocalDate);
        }
        LocalTime localTime = this.time;
        if (localTime != null) {
            crossCheck(localTime);
            if (this.date != null && this.fieldValues.size() > 0) {
                crossCheck(this.date.atTime(this.time));
            }
        }
    }

    private void crossCheck(TemporalAccessor target) {
        Iterator<Map.Entry<TemporalField, Long>> it = this.fieldValues.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<TemporalField, Long> next = it.next();
            TemporalField field = next.getKey();
            if (target.isSupported(field)) {
                try {
                    long val1 = target.getLong(field);
                    long val2 = next.getValue().longValue();
                    if (val1 != val2) {
                        throw new DateTimeException("Conflict found: Field " + field + " " + val1 + " differs from " + field + " " + val2 + " derived from " + target);
                    }
                    it.remove();
                } catch (RuntimeException e) {
                }
            }
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(64);
        buf.append(this.fieldValues);
        buf.append(',');
        buf.append(this.chrono);
        if (this.zone != null) {
            buf.append(',');
            buf.append(this.zone);
        }
        if (this.date != null || this.time != null) {
            buf.append(" resolved to ");
            ChronoLocalDate chronoLocalDate = this.date;
            if (chronoLocalDate != null) {
                buf.append(chronoLocalDate);
                if (this.time != null) {
                    buf.append('T');
                    buf.append(this.time);
                }
            } else {
                buf.append(this.time);
            }
        }
        return buf.toString();
    }
}
