package j$.time.chrono;

import androidx.exifinterface.media.ExifInterface;
import j$.time.DateTimeException;
import j$.time.LocalDate;
import j$.time.Year;
import j$.time.chrono.Era;
import j$.time.format.DateTimeFormatterBuilder;
import j$.time.format.TextStyle;
import j$.time.temporal.ChronoField;
import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQuery;
import j$.time.temporal.ValueRange;
import j$.util.Objects;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;
/* loaded from: classes2.dex */
public final class JapaneseEra implements Era, Serializable {
    static final int ERA_OFFSET = 2;
    public static final JapaneseEra HEISEI;
    private static final JapaneseEra[] KNOWN_ERAS;
    public static final JapaneseEra MEIJI;
    private static final int N_ERA_CONSTANTS;
    private static final JapaneseEra REIWA;
    public static final JapaneseEra SHOWA;
    public static final JapaneseEra TAISHO;
    private static final long serialVersionUID = 1466499369062886794L;
    private final transient String abbreviation;
    private final transient int eraValue;
    private final transient String name;
    private final transient LocalDate since;

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

    static {
        JapaneseEra japaneseEra = new JapaneseEra(-1, LocalDate.of(1868, 1, 1), "Meiji", "M");
        MEIJI = japaneseEra;
        JapaneseEra japaneseEra2 = new JapaneseEra(0, LocalDate.of(1912, 7, 30), "Taisho", ExifInterface.GPS_DIRECTION_TRUE);
        TAISHO = japaneseEra2;
        JapaneseEra japaneseEra3 = new JapaneseEra(1, LocalDate.of(1926, 12, 25), "Showa", ExifInterface.LATITUDE_SOUTH);
        SHOWA = japaneseEra3;
        JapaneseEra japaneseEra4 = new JapaneseEra(2, LocalDate.of(1989, 1, 8), "Heisei", "H");
        HEISEI = japaneseEra4;
        JapaneseEra japaneseEra5 = new JapaneseEra(3, LocalDate.of(2019, 5, 1), "Reiwa", "R");
        REIWA = japaneseEra5;
        int value = japaneseEra5.getValue() + 2;
        N_ERA_CONSTANTS = value;
        JapaneseEra[] japaneseEraArr = new JapaneseEra[value];
        KNOWN_ERAS = japaneseEraArr;
        japaneseEraArr[0] = japaneseEra;
        japaneseEraArr[1] = japaneseEra2;
        japaneseEraArr[2] = japaneseEra3;
        japaneseEraArr[3] = japaneseEra4;
        japaneseEraArr[4] = japaneseEra5;
    }

    public static JapaneseEra getCurrentEra() {
        JapaneseEra[] japaneseEraArr = KNOWN_ERAS;
        return japaneseEraArr[japaneseEraArr.length - 1];
    }

    public static long shortestYearsOfEra() {
        int min = (Year.MAX_VALUE - getCurrentEra().since.getYear()) + 1;
        int lastStartYear = KNOWN_ERAS[0].since.getYear();
        int i = 1;
        while (true) {
            JapaneseEra[] japaneseEraArr = KNOWN_ERAS;
            if (i < japaneseEraArr.length) {
                JapaneseEra era = japaneseEraArr[i];
                int lastYearsOfEra = (era.since.getYear() - lastStartYear) + 1;
                min = Math.min(min, lastYearsOfEra);
                lastStartYear = era.since.getYear();
                i++;
            } else {
                return min;
            }
        }
    }

    public static long shortestDaysOfYear() {
        JapaneseEra[] japaneseEraArr;
        long min = ChronoField.DAY_OF_YEAR.range().getSmallestMaximum();
        for (JapaneseEra era : KNOWN_ERAS) {
            min = Math.min(min, (era.since.lengthOfYear() - era.since.getDayOfYear()) + 1);
            if (era.next() != null) {
                min = Math.min(min, era.next().since.getDayOfYear() - 1);
            }
        }
        return min;
    }

    private JapaneseEra(int eraValue, LocalDate since, String name, String abbreviation) {
        this.eraValue = eraValue;
        this.since = since;
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public LocalDate getSince() {
        return this.since;
    }

    public static JapaneseEra of(int japaneseEra) {
        if (japaneseEra >= MEIJI.eraValue) {
            int i = japaneseEra + 2;
            JapaneseEra[] japaneseEraArr = KNOWN_ERAS;
            if (i <= japaneseEraArr.length) {
                return japaneseEraArr[ordinal(japaneseEra)];
            }
        }
        throw new DateTimeException("Invalid era: " + japaneseEra);
    }

    public static JapaneseEra valueOf(String japaneseEra) {
        JapaneseEra[] japaneseEraArr;
        Objects.requireNonNull(japaneseEra, "japaneseEra");
        for (JapaneseEra era : KNOWN_ERAS) {
            if (era.getName().equals(japaneseEra)) {
                return era;
            }
        }
        throw new IllegalArgumentException("japaneseEra is invalid");
    }

    public static JapaneseEra[] values() {
        JapaneseEra[] japaneseEraArr = KNOWN_ERAS;
        return (JapaneseEra[]) Arrays.copyOf(japaneseEraArr, japaneseEraArr.length);
    }

    @Override // j$.time.chrono.Era
    public String getDisplayName(TextStyle style, Locale locale) {
        String format;
        if (getValue() <= N_ERA_CONSTANTS - 2) {
            format = new DateTimeFormatterBuilder().appendText(ChronoField.ERA, style).toFormatter(locale).format(this);
            return format;
        }
        Objects.requireNonNull(locale, "locale");
        return style.asNormal() == TextStyle.NARROW ? getAbbreviation() : getName();
    }

    public static JapaneseEra from(LocalDate date) {
        if (date.isBefore(JapaneseDate.MEIJI_6_ISODATE)) {
            throw new DateTimeException("JapaneseDate before Meiji 6 are not supported");
        }
        for (int i = KNOWN_ERAS.length - 1; i >= 0; i--) {
            JapaneseEra era = KNOWN_ERAS[i];
            if (date.compareTo((ChronoLocalDate) era.since) >= 0) {
                return era;
            }
        }
        return null;
    }

    private static int ordinal(int eraValue) {
        return (eraValue + 2) - 1;
    }

    @Override // j$.time.chrono.Era
    public int getValue() {
        return this.eraValue;
    }

    @Override // j$.time.chrono.Era, j$.time.temporal.TemporalAccessor
    public ValueRange range(TemporalField field) {
        ValueRange $default$range;
        if (field != ChronoField.ERA) {
            $default$range = TemporalAccessor.CC.$default$range(this, field);
            return $default$range;
        }
        return JapaneseChronology.INSTANCE.range(ChronoField.ERA);
    }

    private String getAbbreviation() {
        return this.abbreviation;
    }

    private String getName() {
        return this.name;
    }

    public JapaneseEra next() {
        if (this == getCurrentEra()) {
            return null;
        }
        return of(this.eraValue + 1);
    }

    public String toString() {
        return getName();
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new Ser((byte) 5, this);
    }

    public void writeExternal(DataOutput out) {
        out.writeByte(getValue());
    }

    public static JapaneseEra readExternal(DataInput in) {
        byte eraValue = in.readByte();
        return of(eraValue);
    }
}
