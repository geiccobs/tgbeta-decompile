package j$.time.temporal;

import j$.time.DateTimeException;
import j$.util.Objects;
/* loaded from: classes2.dex */
public interface TemporalAccessor {
    int get(TemporalField temporalField);

    long getLong(TemporalField temporalField);

    boolean isSupported(TemporalField temporalField);

    <R> R query(TemporalQuery<R> temporalQuery);

    ValueRange range(TemporalField temporalField);

    /* renamed from: j$.time.temporal.TemporalAccessor$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static ValueRange $default$range(TemporalAccessor _this, TemporalField field) {
            if (field instanceof ChronoField) {
                if (_this.isSupported(field)) {
                    return field.range();
                }
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            }
            Objects.requireNonNull(field, "field");
            return field.rangeRefinedBy(_this);
        }

        public static int $default$get(TemporalAccessor _this, TemporalField field) {
            ValueRange range = _this.range(field);
            if (!range.isIntValue()) {
                throw new UnsupportedTemporalTypeException("Invalid field " + field + " for get() method, use getLong() instead");
            }
            long value = _this.getLong(field);
            if (!range.isValidValue(value)) {
                throw new DateTimeException("Invalid value for " + field + " (valid values " + range + "): " + value);
            }
            return (int) value;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.time.temporal.TemporalQuery != java.time.temporal.TemporalQuery<R> */
        public static Object $default$query(TemporalAccessor _this, TemporalQuery temporalQuery) {
            if (temporalQuery == TemporalQueries.zoneId() || temporalQuery == TemporalQueries.chronology() || temporalQuery == TemporalQueries.precision()) {
                return null;
            }
            return temporalQuery.queryFrom(_this);
        }
    }
}
