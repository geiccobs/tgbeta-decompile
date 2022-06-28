package j$.time.chrono;

import j$.time.Clock;
import j$.time.Instant;
import j$.time.LocalDate;
import j$.time.ZoneId;
import j$.time.format.ResolverStyle;
import j$.time.temporal.ChronoField;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalField;
import j$.time.temporal.ValueRange;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes2.dex */
public final class ThaiBuddhistChronology extends AbstractChronology implements Serializable {
    private static final HashMap<String, String[]> ERA_FULL_NAMES;
    private static final HashMap<String, String[]> ERA_NARROW_NAMES;
    private static final HashMap<String, String[]> ERA_SHORT_NAMES;
    private static final String FALLBACK_LANGUAGE = "en";
    public static final ThaiBuddhistChronology INSTANCE = new ThaiBuddhistChronology();
    private static final String TARGET_LANGUAGE = "th";
    static final int YEARS_DIFFERENCE = 543;
    private static final long serialVersionUID = 2775954514031616474L;

    static {
        HashMap<String, String[]> hashMap = new HashMap<>();
        ERA_NARROW_NAMES = hashMap;
        HashMap<String, String[]> hashMap2 = new HashMap<>();
        ERA_SHORT_NAMES = hashMap2;
        HashMap<String, String[]> hashMap3 = new HashMap<>();
        ERA_FULL_NAMES = hashMap3;
        hashMap.put(FALLBACK_LANGUAGE, new String[]{"BB", "BE"});
        hashMap.put(TARGET_LANGUAGE, new String[]{"BB", "BE"});
        hashMap2.put(FALLBACK_LANGUAGE, new String[]{"B.B.", "B.E."});
        hashMap2.put(TARGET_LANGUAGE, new String[]{"พ.ศ.", "ปีก่อนคริสต์กาลที่"});
        hashMap3.put(FALLBACK_LANGUAGE, new String[]{"Before Buddhist", "Budhhist Era"});
        hashMap3.put(TARGET_LANGUAGE, new String[]{"พุทธศักราช", "ปีก่อนคริสต์กาลที่"});
    }

    private ThaiBuddhistChronology() {
    }

    @Override // j$.time.chrono.Chronology
    public String getId() {
        return "ThaiBuddhist";
    }

    @Override // j$.time.chrono.Chronology
    public String getCalendarType() {
        return "buddhist";
    }

    @Override // j$.time.chrono.AbstractChronology, j$.time.chrono.Chronology
    public ThaiBuddhistDate date(Era era, int yearOfEra, int month, int dayOfMonth) {
        return date(prolepticYear(era, yearOfEra), month, dayOfMonth);
    }

    @Override // j$.time.chrono.Chronology
    public ThaiBuddhistDate date(int prolepticYear, int month, int dayOfMonth) {
        return new ThaiBuddhistDate(LocalDate.of(prolepticYear - 543, month, dayOfMonth));
    }

    @Override // j$.time.chrono.AbstractChronology, j$.time.chrono.Chronology
    public ThaiBuddhistDate dateYearDay(Era era, int yearOfEra, int dayOfYear) {
        return dateYearDay(prolepticYear(era, yearOfEra), dayOfYear);
    }

    @Override // j$.time.chrono.Chronology
    public ThaiBuddhistDate dateYearDay(int prolepticYear, int dayOfYear) {
        return new ThaiBuddhistDate(LocalDate.ofYearDay(prolepticYear - 543, dayOfYear));
    }

    @Override // j$.time.chrono.Chronology
    public ThaiBuddhistDate dateEpochDay(long epochDay) {
        return new ThaiBuddhistDate(LocalDate.ofEpochDay(epochDay));
    }

    @Override // j$.time.chrono.AbstractChronology, j$.time.chrono.Chronology
    public ThaiBuddhistDate dateNow() {
        return dateNow(Clock.systemDefaultZone());
    }

    @Override // j$.time.chrono.AbstractChronology, j$.time.chrono.Chronology
    public ThaiBuddhistDate dateNow(ZoneId zone) {
        return dateNow(Clock.system(zone));
    }

    @Override // j$.time.chrono.AbstractChronology, j$.time.chrono.Chronology
    public ThaiBuddhistDate dateNow(Clock clock) {
        return date((TemporalAccessor) LocalDate.now(clock));
    }

    @Override // j$.time.chrono.Chronology
    public ThaiBuddhistDate date(TemporalAccessor temporal) {
        if (temporal instanceof ThaiBuddhistDate) {
            return (ThaiBuddhistDate) temporal;
        }
        return new ThaiBuddhistDate(LocalDate.from(temporal));
    }

    @Override // j$.time.chrono.AbstractChronology, j$.time.chrono.Chronology
    public ChronoLocalDateTime<ThaiBuddhistDate> localDateTime(TemporalAccessor temporal) {
        return super.localDateTime(temporal);
    }

    @Override // j$.time.chrono.AbstractChronology, j$.time.chrono.Chronology
    public ChronoZonedDateTime<ThaiBuddhistDate> zonedDateTime(TemporalAccessor temporal) {
        return super.zonedDateTime(temporal);
    }

    @Override // j$.time.chrono.AbstractChronology, j$.time.chrono.Chronology
    public ChronoZonedDateTime<ThaiBuddhistDate> zonedDateTime(Instant instant, ZoneId zone) {
        return super.zonedDateTime(instant, zone);
    }

    @Override // j$.time.chrono.Chronology
    public boolean isLeapYear(long prolepticYear) {
        return IsoChronology.INSTANCE.isLeapYear(prolepticYear - 543);
    }

    @Override // j$.time.chrono.Chronology
    public int prolepticYear(Era era, int yearOfEra) {
        if (era instanceof ThaiBuddhistEra) {
            return era == ThaiBuddhistEra.BE ? yearOfEra : 1 - yearOfEra;
        }
        throw new ClassCastException("Era must be BuddhistEra");
    }

    @Override // j$.time.chrono.Chronology
    public ThaiBuddhistEra eraOf(int eraValue) {
        return ThaiBuddhistEra.of(eraValue);
    }

    @Override // j$.time.chrono.Chronology
    public List<Era> eras() {
        return Arrays.asList(ThaiBuddhistEra.values());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: j$.time.chrono.ThaiBuddhistChronology$1 */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoField;

        static {
            int[] iArr = new int[ChronoField.values().length];
            $SwitchMap$java$time$temporal$ChronoField = iArr;
            try {
                iArr[ChronoField.PROLEPTIC_MONTH.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.YEAR_OF_ERA.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.YEAR.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    @Override // j$.time.chrono.Chronology
    public ValueRange range(ChronoField field) {
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[field.ordinal()]) {
            case 1:
                ValueRange range = ChronoField.PROLEPTIC_MONTH.range();
                return ValueRange.of(range.getMinimum() + 6516, range.getMaximum() + 6516);
            case 2:
                ValueRange range2 = ChronoField.YEAR.range();
                return ValueRange.of(1L, (-(range2.getMinimum() + 543)) + 1, range2.getMaximum() + 543);
            case 3:
                ValueRange range3 = ChronoField.YEAR.range();
                return ValueRange.of(range3.getMinimum() + 543, range3.getMaximum() + 543);
            default:
                return field.range();
        }
    }

    @Override // j$.time.chrono.AbstractChronology, j$.time.chrono.Chronology
    public ThaiBuddhistDate resolveDate(Map<TemporalField, Long> map, ResolverStyle resolverStyle) {
        return (ThaiBuddhistDate) super.resolveDate(map, resolverStyle);
    }

    @Override // j$.time.chrono.AbstractChronology
    public Object writeReplace() {
        return super.writeReplace();
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }
}
