package j$.time.chrono;

import j$.time.DateTimeException;
import j$.time.chrono.Era;
import j$.time.format.DateTimeFormatterBuilder;
import j$.time.format.TextStyle;
import j$.time.temporal.ChronoField;
import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQuery;
import j$.time.temporal.ValueRange;
import java.util.Locale;
/* loaded from: classes2.dex */
public enum HijrahEra implements Era {
    AH;

    @Override // j$.time.chrono.Era, j$.time.temporal.TemporalAdjuster
    public /* synthetic */ Temporal adjustInto(Temporal temporal) {
        Temporal with;
        with = temporal.with(ChronoField.ERA, getValue());
        return with;
    }

    @Override // j$.time.chrono.Era, j$.time.temporal.TemporalAccessor
    public /* synthetic */ int get(TemporalField temporalField) {
        return Era.CC.$default$get(this, temporalField);
    }

    @Override // j$.time.chrono.Era
    public /* synthetic */ String getDisplayName(TextStyle textStyle, Locale locale) {
        String format;
        format = new DateTimeFormatterBuilder().appendText(ChronoField.ERA, textStyle).toFormatter(locale).format(this);
        return format;
    }

    @Override // j$.time.chrono.Era, j$.time.temporal.TemporalAccessor
    public /* synthetic */ long getLong(TemporalField temporalField) {
        return Era.CC.$default$getLong(this, temporalField);
    }

    @Override // j$.time.chrono.Era, j$.time.temporal.TemporalAccessor
    public /* synthetic */ boolean isSupported(TemporalField temporalField) {
        return Era.CC.$default$isSupported(this, temporalField);
    }

    @Override // j$.time.chrono.Era, j$.time.temporal.TemporalAccessor
    public /* synthetic */ Object query(TemporalQuery temporalQuery) {
        return Era.CC.$default$query(this, temporalQuery);
    }

    public static HijrahEra of(int hijrahEra) {
        if (hijrahEra == 1) {
            return AH;
        }
        throw new DateTimeException("Invalid era: " + hijrahEra);
    }

    @Override // j$.time.chrono.Era
    public int getValue() {
        return 1;
    }

    @Override // j$.time.chrono.Era, j$.time.temporal.TemporalAccessor
    public ValueRange range(TemporalField field) {
        ValueRange $default$range;
        if (field == ChronoField.ERA) {
            return ValueRange.of(1L, 1L);
        }
        $default$range = TemporalAccessor.CC.$default$range(this, field);
        return $default$range;
    }
}
