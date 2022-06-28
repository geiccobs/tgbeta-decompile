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
public enum MinguoEra implements Era {
    BEFORE_ROC,
    ROC;

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

    @Override // j$.time.chrono.Era, j$.time.temporal.TemporalAccessor
    public /* synthetic */ ValueRange range(TemporalField temporalField) {
        ValueRange $default$range;
        $default$range = TemporalAccessor.CC.$default$range(this, temporalField);
        return $default$range;
    }

    public static MinguoEra of(int minguoEra) {
        switch (minguoEra) {
            case 0:
                return BEFORE_ROC;
            case 1:
                return ROC;
            default:
                throw new DateTimeException("Invalid era: " + minguoEra);
        }
    }

    @Override // j$.time.chrono.Era
    public int getValue() {
        return ordinal();
    }
}
