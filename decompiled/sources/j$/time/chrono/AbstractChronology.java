package j$.time.chrono;

import androidx.exifinterface.media.ExifInterface;
import j$.time.Clock;
import j$.time.DateTimeException;
import j$.time.DayOfWeek;
import j$.time.Instant;
import j$.time.Instant$$ExternalSyntheticBackport0;
import j$.time.LocalDate$$ExternalSyntheticBackport0;
import j$.time.ZoneId;
import j$.time.chrono.Chronology;
import j$.time.format.ResolverStyle;
import j$.time.format.TextStyle;
import j$.time.temporal.ChronoField;
import j$.time.temporal.ChronoUnit;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalAdjusters;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalUnit;
import j$.time.temporal.ValueRange;
import j$.util.Objects;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
/* loaded from: classes2.dex */
public abstract class AbstractChronology implements Chronology {
    static final Comparator<ChronoLocalDate> DATE_ORDER = AbstractChronology$$ExternalSyntheticLambda0.INSTANCE;
    static final Comparator<ChronoLocalDateTime<? extends ChronoLocalDate>> DATE_TIME_ORDER = AbstractChronology$$ExternalSyntheticLambda1.INSTANCE;
    static final Comparator<ChronoZonedDateTime<?>> INSTANT_ORDER = AbstractChronology$$ExternalSyntheticLambda2.INSTANCE;
    private static final ConcurrentHashMap<String, Chronology> CHRONOS_BY_ID = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Chronology> CHRONOS_BY_TYPE = new ConcurrentHashMap<>();
    private static final Locale JAPANESE_CALENDAR_LOCALE = new Locale("ja", "JP", "JP");

    @Override // j$.time.chrono.Chronology
    public /* synthetic */ ChronoLocalDate date(Era era, int i, int i2, int i3) {
        ChronoLocalDate date;
        date = date(prolepticYear(era, i), i2, i3);
        return date;
    }

    @Override // j$.time.chrono.Chronology
    public /* synthetic */ ChronoLocalDate dateNow() {
        ChronoLocalDate dateNow;
        dateNow = dateNow(Clock.systemDefaultZone());
        return dateNow;
    }

    @Override // j$.time.chrono.Chronology
    public /* synthetic */ ChronoLocalDate dateNow(Clock clock) {
        return Objects.requireNonNull(clock, "clock");
    }

    @Override // j$.time.chrono.Chronology
    public /* synthetic */ ChronoLocalDate dateNow(ZoneId zoneId) {
        ChronoLocalDate dateNow;
        dateNow = dateNow(Clock.system(zoneId));
        return dateNow;
    }

    @Override // j$.time.chrono.Chronology
    public /* synthetic */ ChronoLocalDate dateYearDay(Era era, int i, int i2) {
        ChronoLocalDate dateYearDay;
        dateYearDay = dateYearDay(prolepticYear(era, i), i2);
        return dateYearDay;
    }

    @Override // j$.time.chrono.Chronology
    public /* synthetic */ String getDisplayName(TextStyle textStyle, Locale locale) {
        return Chronology.CC.$default$getDisplayName(this, textStyle, locale);
    }

    @Override // j$.time.chrono.Chronology
    public /* synthetic */ ChronoLocalDateTime localDateTime(TemporalAccessor temporalAccessor) {
        return Chronology.CC.$default$localDateTime(this, temporalAccessor);
    }

    @Override // j$.time.chrono.Chronology
    public /* synthetic */ ChronoPeriod period(int i, int i2, int i3) {
        return Chronology.CC.$default$period(this, i, i2, i3);
    }

    @Override // j$.time.chrono.Chronology
    public /* synthetic */ ChronoZonedDateTime zonedDateTime(Instant instant, ZoneId zoneId) {
        ChronoZonedDateTime ofInstant;
        ofInstant = ChronoZonedDateTimeImpl.ofInstant(this, instant, zoneId);
        return ofInstant;
    }

    @Override // j$.time.chrono.Chronology
    public /* synthetic */ ChronoZonedDateTime zonedDateTime(TemporalAccessor temporalAccessor) {
        return Chronology.CC.$default$zonedDateTime(this, temporalAccessor);
    }

    public static /* synthetic */ int lambda$static$7f2d2d5b$1(ChronoLocalDate date1, ChronoLocalDate date2) {
        return (date1.toEpochDay() > date2.toEpochDay() ? 1 : (date1.toEpochDay() == date2.toEpochDay() ? 0 : -1));
    }

    public static /* synthetic */ int lambda$static$b5a61975$1(ChronoLocalDateTime dateTime1, ChronoLocalDateTime dateTime2) {
        int cmp = (dateTime1.toLocalDate().toEpochDay() > dateTime2.toLocalDate().toEpochDay() ? 1 : (dateTime1.toLocalDate().toEpochDay() == dateTime2.toLocalDate().toEpochDay() ? 0 : -1));
        if (cmp == 0) {
            return (dateTime1.toLocalTime().toNanoOfDay() > dateTime2.toLocalTime().toNanoOfDay() ? 1 : (dateTime1.toLocalTime().toNanoOfDay() == dateTime2.toLocalTime().toNanoOfDay() ? 0 : -1));
        }
        return cmp;
    }

    public static /* synthetic */ int lambda$static$2241c452$1(ChronoZonedDateTime dateTime1, ChronoZonedDateTime dateTime2) {
        int cmp = (dateTime1.toEpochSecond() > dateTime2.toEpochSecond() ? 1 : (dateTime1.toEpochSecond() == dateTime2.toEpochSecond() ? 0 : -1));
        if (cmp == 0) {
            return (dateTime1.toLocalTime().getNano() > dateTime2.toLocalTime().getNano() ? 1 : (dateTime1.toLocalTime().getNano() == dateTime2.toLocalTime().getNano() ? 0 : -1));
        }
        return cmp;
    }

    static Chronology registerChrono(Chronology chrono) {
        return registerChrono(chrono, chrono.getId());
    }

    public static Chronology registerChrono(Chronology chrono, String id) {
        String type;
        Chronology prev = CHRONOS_BY_ID.putIfAbsent(id, chrono);
        if (prev == null && (type = chrono.getCalendarType()) != null) {
            CHRONOS_BY_TYPE.putIfAbsent(type, chrono);
        }
        return prev;
    }

    private static boolean initCache() {
        if (CHRONOS_BY_ID.get(ExifInterface.TAG_RW2_ISO) == null) {
            registerChrono(HijrahChronology.INSTANCE);
            registerChrono(JapaneseChronology.INSTANCE);
            registerChrono(MinguoChronology.INSTANCE);
            registerChrono(ThaiBuddhistChronology.INSTANCE);
            ServiceLoader<java.time.chrono.AbstractChronology> loader = ServiceLoader.load(AbstractChronology.class, null);
            Iterator<java.time.chrono.AbstractChronology> it = loader.iterator();
            while (it.hasNext()) {
                AbstractChronology chrono = (AbstractChronology) it.next();
                String id = chrono.getId();
                if (!id.equals(ExifInterface.TAG_RW2_ISO)) {
                    registerChrono(chrono);
                }
            }
            registerChrono(IsoChronology.INSTANCE);
            return true;
        }
        return false;
    }

    public static Chronology ofLocale(Locale locale) {
        Objects.requireNonNull(locale, "locale");
        String type = getCalendarType(locale);
        if (type == null || "iso".equals(type) || "iso8601".equals(type)) {
            return IsoChronology.INSTANCE;
        }
        do {
            Chronology chrono = CHRONOS_BY_TYPE.get(type);
            if (chrono != null) {
                return chrono;
            }
        } while (initCache());
        ServiceLoader<java.time.chrono.Chronology> loader = ServiceLoader.load(Chronology.class);
        Iterator<java.time.chrono.Chronology> it = loader.iterator();
        while (it.hasNext()) {
            Chronology chrono2 = (Chronology) it.next();
            if (type.equals(chrono2.getCalendarType())) {
                return chrono2;
            }
        }
        throw new DateTimeException("Unknown calendar system: " + type);
    }

    private static String getCalendarType(Locale locale) {
        String type = locale.getUnicodeLocaleType("ca");
        if (type != null) {
            return type;
        }
        if (locale.equals(JAPANESE_CALENDAR_LOCALE)) {
            return "japanese";
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0022  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static j$.time.chrono.Chronology of(java.lang.String r4) {
        /*
            java.lang.String r0 = "id"
            j$.util.Objects.requireNonNull(r4, r0)
        L5:
            j$.time.chrono.Chronology r0 = of0(r4)
            if (r0 == 0) goto Lc
            return r0
        Lc:
            boolean r0 = initCache()
            if (r0 != 0) goto L56
            java.lang.Class<j$.time.chrono.Chronology> r0 = j$.time.chrono.Chronology.class
            java.util.ServiceLoader r0 = java.util.ServiceLoader.load(r0)
            java.util.Iterator r1 = r0.iterator()
        L1c:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L3f
            java.lang.Object r2 = r1.next()
            j$.time.chrono.Chronology r2 = (j$.time.chrono.Chronology) r2
            java.lang.String r3 = r2.getId()
            boolean r3 = r4.equals(r3)
            if (r3 != 0) goto L3e
            java.lang.String r3 = r2.getCalendarType()
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L3d
            goto L3e
        L3d:
            goto L1c
        L3e:
            return r2
        L3f:
            j$.time.DateTimeException r1 = new j$.time.DateTimeException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Unknown chronology: "
            r2.append(r3)
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        L56:
            goto L5
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.time.chrono.AbstractChronology.of(java.lang.String):j$.time.chrono.Chronology");
    }

    private static Chronology of0(String id) {
        Chronology chrono = CHRONOS_BY_ID.get(id);
        if (chrono == null) {
            return CHRONOS_BY_TYPE.get(id);
        }
        return chrono;
    }

    public static Set<Chronology> getAvailableChronologies() {
        initCache();
        HashSet<java.time.chrono.Chronology> chronos = new HashSet<>(CHRONOS_BY_ID.values());
        ServiceLoader<java.time.chrono.Chronology> loader = ServiceLoader.load(Chronology.class);
        Iterator<java.time.chrono.Chronology> it = loader.iterator();
        while (it.hasNext()) {
            Chronology chrono = (Chronology) it.next();
            chronos.add(chrono);
        }
        return chronos;
    }

    @Override // j$.time.chrono.Chronology
    public ChronoLocalDate resolveDate(Map<TemporalField, Long> map, ResolverStyle resolverStyle) {
        if (map.containsKey(ChronoField.EPOCH_DAY)) {
            return dateEpochDay(map.remove(ChronoField.EPOCH_DAY).longValue());
        }
        resolveProlepticMonth(map, resolverStyle);
        ChronoLocalDate resolved = resolveYearOfEra(map, resolverStyle);
        if (resolved != null) {
            return resolved;
        }
        if (map.containsKey(ChronoField.YEAR)) {
            if (map.containsKey(ChronoField.MONTH_OF_YEAR)) {
                if (map.containsKey(ChronoField.DAY_OF_MONTH)) {
                    return resolveYMD(map, resolverStyle);
                }
                if (map.containsKey(ChronoField.ALIGNED_WEEK_OF_MONTH)) {
                    if (map.containsKey(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH)) {
                        return resolveYMAA(map, resolverStyle);
                    }
                    if (map.containsKey(ChronoField.DAY_OF_WEEK)) {
                        return resolveYMAD(map, resolverStyle);
                    }
                }
            }
            if (map.containsKey(ChronoField.DAY_OF_YEAR)) {
                return resolveYD(map, resolverStyle);
            }
            if (map.containsKey(ChronoField.ALIGNED_WEEK_OF_YEAR)) {
                if (map.containsKey(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR)) {
                    return resolveYAA(map, resolverStyle);
                }
                if (map.containsKey(ChronoField.DAY_OF_WEEK)) {
                    return resolveYAD(map, resolverStyle);
                }
                return null;
            }
            return null;
        }
        return null;
    }

    void resolveProlepticMonth(Map<TemporalField, Long> map, ResolverStyle resolverStyle) {
        Long pMonth = map.remove(ChronoField.PROLEPTIC_MONTH);
        if (pMonth != null) {
            if (resolverStyle != ResolverStyle.LENIENT) {
                ChronoField.PROLEPTIC_MONTH.checkValidValue(pMonth.longValue());
            }
            ChronoLocalDate chronoDate = dateNow().with((TemporalField) ChronoField.DAY_OF_MONTH, 1L).with((TemporalField) ChronoField.PROLEPTIC_MONTH, pMonth.longValue());
            addFieldValue(map, ChronoField.MONTH_OF_YEAR, chronoDate.get(ChronoField.MONTH_OF_YEAR));
            addFieldValue(map, ChronoField.YEAR, chronoDate.get(ChronoField.YEAR));
        }
    }

    ChronoLocalDate resolveYearOfEra(Map<TemporalField, Long> map, ResolverStyle resolverStyle) {
        int yoe;
        Long yoeLong = map.remove(ChronoField.YEAR_OF_ERA);
        if (yoeLong != null) {
            Long eraLong = map.remove(ChronoField.ERA);
            if (resolverStyle != ResolverStyle.LENIENT) {
                yoe = range(ChronoField.YEAR_OF_ERA).checkValidIntValue(yoeLong.longValue(), ChronoField.YEAR_OF_ERA);
            } else {
                yoe = LocalDate$$ExternalSyntheticBackport0.m(yoeLong.longValue());
            }
            if (eraLong != null) {
                Era eraObj = eraOf(range(ChronoField.ERA).checkValidIntValue(eraLong.longValue(), ChronoField.ERA));
                addFieldValue(map, ChronoField.YEAR, prolepticYear(eraObj, yoe));
                return null;
            } else if (map.containsKey(ChronoField.YEAR)) {
                int year = range(ChronoField.YEAR).checkValidIntValue(map.get(ChronoField.YEAR).longValue(), ChronoField.YEAR);
                ChronoLocalDate chronoDate = dateYearDay(year, 1);
                addFieldValue(map, ChronoField.YEAR, prolepticYear(chronoDate.getEra(), yoe));
                return null;
            } else if (resolverStyle == ResolverStyle.STRICT) {
                map.put(ChronoField.YEAR_OF_ERA, yoeLong);
                return null;
            } else {
                List<Era> eras = eras();
                if (eras.isEmpty()) {
                    addFieldValue(map, ChronoField.YEAR, yoe);
                    return null;
                }
                Era eraObj2 = eras.get(eras.size() - 1);
                addFieldValue(map, ChronoField.YEAR, prolepticYear(eraObj2, yoe));
                return null;
            }
        } else if (map.containsKey(ChronoField.ERA)) {
            range(ChronoField.ERA).checkValidValue(map.get(ChronoField.ERA).longValue(), ChronoField.ERA);
            return null;
        } else {
            return null;
        }
    }

    ChronoLocalDate resolveYMD(Map<TemporalField, Long> map, ResolverStyle resolverStyle) {
        int y = range(ChronoField.YEAR).checkValidIntValue(map.remove(ChronoField.YEAR).longValue(), ChronoField.YEAR);
        if (resolverStyle == ResolverStyle.LENIENT) {
            long months = Instant$$ExternalSyntheticBackport0.m(map.remove(ChronoField.MONTH_OF_YEAR).longValue(), 1L);
            long days = Instant$$ExternalSyntheticBackport0.m(map.remove(ChronoField.DAY_OF_MONTH).longValue(), 1L);
            return date(y, 1, 1).plus(months, (TemporalUnit) ChronoUnit.MONTHS).plus(days, (TemporalUnit) ChronoUnit.DAYS);
        }
        int moy = range(ChronoField.MONTH_OF_YEAR).checkValidIntValue(map.remove(ChronoField.MONTH_OF_YEAR).longValue(), ChronoField.MONTH_OF_YEAR);
        ValueRange domRange = range(ChronoField.DAY_OF_MONTH);
        int dom = domRange.checkValidIntValue(map.remove(ChronoField.DAY_OF_MONTH).longValue(), ChronoField.DAY_OF_MONTH);
        if (resolverStyle == ResolverStyle.SMART) {
            try {
                return date(y, moy, dom);
            } catch (DateTimeException e) {
                return date(y, moy, 1).with(TemporalAdjusters.lastDayOfMonth());
            }
        }
        return date(y, moy, dom);
    }

    ChronoLocalDate resolveYD(Map<TemporalField, Long> map, ResolverStyle resolverStyle) {
        int y = range(ChronoField.YEAR).checkValidIntValue(map.remove(ChronoField.YEAR).longValue(), ChronoField.YEAR);
        if (resolverStyle == ResolverStyle.LENIENT) {
            long days = Instant$$ExternalSyntheticBackport0.m(map.remove(ChronoField.DAY_OF_YEAR).longValue(), 1L);
            return dateYearDay(y, 1).plus(days, (TemporalUnit) ChronoUnit.DAYS);
        }
        int doy = range(ChronoField.DAY_OF_YEAR).checkValidIntValue(map.remove(ChronoField.DAY_OF_YEAR).longValue(), ChronoField.DAY_OF_YEAR);
        return dateYearDay(y, doy);
    }

    ChronoLocalDate resolveYMAA(Map<TemporalField, Long> map, ResolverStyle resolverStyle) {
        int y = range(ChronoField.YEAR).checkValidIntValue(map.remove(ChronoField.YEAR).longValue(), ChronoField.YEAR);
        if (resolverStyle == ResolverStyle.LENIENT) {
            long months = Instant$$ExternalSyntheticBackport0.m(map.remove(ChronoField.MONTH_OF_YEAR).longValue(), 1L);
            long weeks = Instant$$ExternalSyntheticBackport0.m(map.remove(ChronoField.ALIGNED_WEEK_OF_MONTH).longValue(), 1L);
            long days = Instant$$ExternalSyntheticBackport0.m(map.remove(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH).longValue(), 1L);
            return date(y, 1, 1).plus(months, (TemporalUnit) ChronoUnit.MONTHS).plus(weeks, (TemporalUnit) ChronoUnit.WEEKS).plus(days, (TemporalUnit) ChronoUnit.DAYS);
        }
        int moy = range(ChronoField.MONTH_OF_YEAR).checkValidIntValue(map.remove(ChronoField.MONTH_OF_YEAR).longValue(), ChronoField.MONTH_OF_YEAR);
        int aw = range(ChronoField.ALIGNED_WEEK_OF_MONTH).checkValidIntValue(map.remove(ChronoField.ALIGNED_WEEK_OF_MONTH).longValue(), ChronoField.ALIGNED_WEEK_OF_MONTH);
        int ad = range(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH).checkValidIntValue(map.remove(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH).longValue(), ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH);
        ChronoLocalDate date = date(y, moy, 1).plus(((aw - 1) * 7) + (ad - 1), (TemporalUnit) ChronoUnit.DAYS);
        if (resolverStyle == ResolverStyle.STRICT && date.get(ChronoField.MONTH_OF_YEAR) != moy) {
            throw new DateTimeException("Strict mode rejected resolved date as it is in a different month");
        }
        return date;
    }

    ChronoLocalDate resolveYMAD(Map<TemporalField, Long> map, ResolverStyle resolverStyle) {
        int y = range(ChronoField.YEAR).checkValidIntValue(map.remove(ChronoField.YEAR).longValue(), ChronoField.YEAR);
        if (resolverStyle != ResolverStyle.LENIENT) {
            int moy = range(ChronoField.MONTH_OF_YEAR).checkValidIntValue(map.remove(ChronoField.MONTH_OF_YEAR).longValue(), ChronoField.MONTH_OF_YEAR);
            int aw = range(ChronoField.ALIGNED_WEEK_OF_MONTH).checkValidIntValue(map.remove(ChronoField.ALIGNED_WEEK_OF_MONTH).longValue(), ChronoField.ALIGNED_WEEK_OF_MONTH);
            int dow = range(ChronoField.DAY_OF_WEEK).checkValidIntValue(map.remove(ChronoField.DAY_OF_WEEK).longValue(), ChronoField.DAY_OF_WEEK);
            ChronoLocalDate date = date(y, moy, 1).plus((aw - 1) * 7, (TemporalUnit) ChronoUnit.DAYS).with(TemporalAdjusters.nextOrSame(DayOfWeek.of(dow)));
            if (resolverStyle == ResolverStyle.STRICT && date.get(ChronoField.MONTH_OF_YEAR) != moy) {
                throw new DateTimeException("Strict mode rejected resolved date as it is in a different month");
            }
            return date;
        }
        long months = Instant$$ExternalSyntheticBackport0.m(map.remove(ChronoField.MONTH_OF_YEAR).longValue(), 1L);
        long weeks = Instant$$ExternalSyntheticBackport0.m(map.remove(ChronoField.ALIGNED_WEEK_OF_MONTH).longValue(), 1L);
        long dow2 = Instant$$ExternalSyntheticBackport0.m(map.remove(ChronoField.DAY_OF_WEEK).longValue(), 1L);
        return resolveAligned(date(y, 1, 1), months, weeks, dow2);
    }

    ChronoLocalDate resolveYAA(Map<TemporalField, Long> map, ResolverStyle resolverStyle) {
        int y = range(ChronoField.YEAR).checkValidIntValue(map.remove(ChronoField.YEAR).longValue(), ChronoField.YEAR);
        if (resolverStyle == ResolverStyle.LENIENT) {
            long weeks = Instant$$ExternalSyntheticBackport0.m(map.remove(ChronoField.ALIGNED_WEEK_OF_YEAR).longValue(), 1L);
            long days = Instant$$ExternalSyntheticBackport0.m(map.remove(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR).longValue(), 1L);
            return dateYearDay(y, 1).plus(weeks, (TemporalUnit) ChronoUnit.WEEKS).plus(days, (TemporalUnit) ChronoUnit.DAYS);
        }
        int aw = range(ChronoField.ALIGNED_WEEK_OF_YEAR).checkValidIntValue(map.remove(ChronoField.ALIGNED_WEEK_OF_YEAR).longValue(), ChronoField.ALIGNED_WEEK_OF_YEAR);
        int ad = range(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR).checkValidIntValue(map.remove(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR).longValue(), ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR);
        ChronoLocalDate date = dateYearDay(y, 1).plus(((aw - 1) * 7) + (ad - 1), (TemporalUnit) ChronoUnit.DAYS);
        if (resolverStyle == ResolverStyle.STRICT && date.get(ChronoField.YEAR) != y) {
            throw new DateTimeException("Strict mode rejected resolved date as it is in a different year");
        }
        return date;
    }

    ChronoLocalDate resolveYAD(Map<TemporalField, Long> map, ResolverStyle resolverStyle) {
        int y = range(ChronoField.YEAR).checkValidIntValue(map.remove(ChronoField.YEAR).longValue(), ChronoField.YEAR);
        if (resolverStyle != ResolverStyle.LENIENT) {
            int aw = range(ChronoField.ALIGNED_WEEK_OF_YEAR).checkValidIntValue(map.remove(ChronoField.ALIGNED_WEEK_OF_YEAR).longValue(), ChronoField.ALIGNED_WEEK_OF_YEAR);
            int dow = range(ChronoField.DAY_OF_WEEK).checkValidIntValue(map.remove(ChronoField.DAY_OF_WEEK).longValue(), ChronoField.DAY_OF_WEEK);
            ChronoLocalDate date = dateYearDay(y, 1).plus((aw - 1) * 7, (TemporalUnit) ChronoUnit.DAYS).with(TemporalAdjusters.nextOrSame(DayOfWeek.of(dow)));
            if (resolverStyle == ResolverStyle.STRICT && date.get(ChronoField.YEAR) != y) {
                throw new DateTimeException("Strict mode rejected resolved date as it is in a different year");
            }
            return date;
        }
        long weeks = Instant$$ExternalSyntheticBackport0.m(map.remove(ChronoField.ALIGNED_WEEK_OF_YEAR).longValue(), 1L);
        long dow2 = Instant$$ExternalSyntheticBackport0.m(map.remove(ChronoField.DAY_OF_WEEK).longValue(), 1L);
        return resolveAligned(dateYearDay(y, 1), 0L, weeks, dow2);
    }

    ChronoLocalDate resolveAligned(ChronoLocalDate base, long months, long weeks, long dow) {
        ChronoLocalDate date = base.plus(months, (TemporalUnit) ChronoUnit.MONTHS).plus(weeks, (TemporalUnit) ChronoUnit.WEEKS);
        if (dow > 7) {
            date = date.plus((dow - 1) / 7, (TemporalUnit) ChronoUnit.WEEKS);
            dow = ((dow - 1) % 7) + 1;
        } else if (dow < 1) {
            date = date.plus(Instant$$ExternalSyntheticBackport0.m(dow, 7L) / 7, (TemporalUnit) ChronoUnit.WEEKS);
            dow = ((6 + dow) % 7) + 1;
        }
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.of((int) dow)));
    }

    public void addFieldValue(Map<TemporalField, Long> map, ChronoField field, long value) {
        Long old = map.get(field);
        if (old != null && old.longValue() != value) {
            throw new DateTimeException("Conflict found: " + field + " " + old + " differs from " + field + " " + value);
        }
        map.put(field, Long.valueOf(value));
    }

    @Override // j$.time.chrono.Chronology
    public int compareTo(Chronology other) {
        return getId().compareTo(other.getId());
    }

    @Override // j$.time.chrono.Chronology
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof AbstractChronology) && compareTo((Chronology) ((AbstractChronology) obj)) == 0;
    }

    @Override // j$.time.chrono.Chronology
    public int hashCode() {
        return getClass().hashCode() ^ getId().hashCode();
    }

    @Override // j$.time.chrono.Chronology
    public String toString() {
        return getId();
    }

    public Object writeReplace() {
        return new Ser((byte) 1, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    public void writeExternal(DataOutput out) {
        out.writeUTF(getId());
    }

    public static Chronology readExternal(DataInput in) {
        String id = in.readUTF();
        return Chronology.CC.of(id);
    }
}
