package j$.time.chrono;

import j$.time.format.TextStyle;
import j$.time.temporal.ChronoField;
import j$.time.temporal.ChronoUnit;
import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalAdjuster;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQueries;
import j$.time.temporal.TemporalQuery;
import j$.time.temporal.UnsupportedTemporalTypeException;
import j$.time.temporal.ValueRange;
import java.util.Locale;
/* loaded from: classes2.dex */
public interface Era extends TemporalAccessor, TemporalAdjuster {
    @Override // j$.time.temporal.TemporalAdjuster
    Temporal adjustInto(Temporal temporal);

    @Override // j$.time.temporal.TemporalAccessor
    int get(TemporalField temporalField);

    String getDisplayName(TextStyle textStyle, Locale locale);

    @Override // j$.time.temporal.TemporalAccessor
    long getLong(TemporalField temporalField);

    int getValue();

    @Override // j$.time.temporal.TemporalAccessor
    boolean isSupported(TemporalField temporalField);

    @Override // j$.time.temporal.TemporalAccessor
    <R> R query(TemporalQuery<R> temporalQuery);

    @Override // j$.time.temporal.TemporalAccessor
    ValueRange range(TemporalField temporalField);

    /* renamed from: j$.time.chrono.Era$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static boolean $default$isSupported(Era _this, TemporalField field) {
            return field instanceof ChronoField ? field == ChronoField.ERA : field != null && field.isSupportedBy(_this);
        }

        public static int $default$get(Era _this, TemporalField field) {
            if (field == ChronoField.ERA) {
                return _this.getValue();
            }
            return TemporalAccessor.CC.$default$get(_this, field);
        }

        public static long $default$getLong(Era _this, TemporalField field) {
            if (field == ChronoField.ERA) {
                return _this.getValue();
            }
            if (field instanceof ChronoField) {
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            }
            return field.getFrom(_this);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.time.temporal.TemporalQuery != java.time.temporal.TemporalQuery<R> */
        public static Object $default$query(Era _this, TemporalQuery temporalQuery) {
            if (temporalQuery == TemporalQueries.precision()) {
                return ChronoUnit.ERAS;
            }
            return TemporalAccessor.CC.$default$query(_this, temporalQuery);
        }
    }
}
