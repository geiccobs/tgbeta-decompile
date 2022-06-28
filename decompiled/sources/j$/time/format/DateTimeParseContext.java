package j$.time.format;

import j$.time.ZoneId;
import j$.time.chrono.Chronology;
import j$.time.chrono.IsoChronology;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalField;
import j$.util.Objects;
import j$.util.function.Consumer;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
/* loaded from: classes2.dex */
public final class DateTimeParseContext {
    private DateTimeFormatter formatter;
    private final ArrayList<Parsed> parsed;
    private boolean caseSensitive = true;
    private boolean strict = true;
    private ArrayList<Consumer<Chronology>> chronoListeners = null;

    public DateTimeParseContext(DateTimeFormatter formatter) {
        ArrayList<Parsed> arrayList = new ArrayList<>();
        this.parsed = arrayList;
        this.formatter = formatter;
        arrayList.add(new Parsed());
    }

    public DateTimeParseContext copy() {
        DateTimeParseContext newContext = new DateTimeParseContext(this.formatter);
        newContext.caseSensitive = this.caseSensitive;
        newContext.strict = this.strict;
        return newContext;
    }

    public Locale getLocale() {
        return this.formatter.getLocale();
    }

    public DecimalStyle getDecimalStyle() {
        return this.formatter.getDecimalStyle();
    }

    public Chronology getEffectiveChronology() {
        Chronology chrono = currentParsed().chrono;
        if (chrono == null) {
            Chronology chrono2 = this.formatter.getChronology();
            if (chrono2 == null) {
                return IsoChronology.INSTANCE;
            }
            return chrono2;
        }
        return chrono;
    }

    public boolean isCaseSensitive() {
        return this.caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public boolean subSequenceEquals(CharSequence cs1, int offset1, CharSequence cs2, int offset2, int length) {
        if (offset1 + length > cs1.length() || offset2 + length > cs2.length()) {
            return false;
        }
        if (isCaseSensitive()) {
            for (int i = 0; i < length; i++) {
                if (cs1.charAt(offset1 + i) != cs2.charAt(offset2 + i)) {
                    return false;
                }
            }
            return true;
        }
        for (int i2 = 0; i2 < length; i2++) {
            char ch1 = cs1.charAt(offset1 + i2);
            char ch2 = cs2.charAt(offset2 + i2);
            if (ch1 != ch2 && Character.toUpperCase(ch1) != Character.toUpperCase(ch2) && Character.toLowerCase(ch1) != Character.toLowerCase(ch2)) {
                return false;
            }
        }
        return true;
    }

    public boolean charEquals(char ch1, char ch2) {
        if (isCaseSensitive()) {
            return ch1 == ch2;
        }
        return charEqualsIgnoreCase(ch1, ch2);
    }

    public static boolean charEqualsIgnoreCase(char c1, char c2) {
        return c1 == c2 || Character.toUpperCase(c1) == Character.toUpperCase(c2) || Character.toLowerCase(c1) == Character.toLowerCase(c2);
    }

    public boolean isStrict() {
        return this.strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public void startOptional() {
        this.parsed.add(currentParsed().copy());
    }

    public void endOptional(boolean successful) {
        if (successful) {
            ArrayList<Parsed> arrayList = this.parsed;
            arrayList.remove(arrayList.size() - 2);
            return;
        }
        ArrayList<Parsed> arrayList2 = this.parsed;
        arrayList2.remove(arrayList2.size() - 1);
    }

    private Parsed currentParsed() {
        ArrayList<Parsed> arrayList = this.parsed;
        return arrayList.get(arrayList.size() - 1);
    }

    public Parsed toUnresolved() {
        return currentParsed();
    }

    public TemporalAccessor toResolved(ResolverStyle resolverStyle, Set<TemporalField> set) {
        Parsed parsed = currentParsed();
        parsed.chrono = getEffectiveChronology();
        parsed.zone = parsed.zone != null ? parsed.zone : this.formatter.getZone();
        return parsed.resolve(resolverStyle, set);
    }

    public Long getParsed(TemporalField field) {
        return currentParsed().fieldValues.get(field);
    }

    public int setParsedField(TemporalField field, long value, int errorPos, int successPos) {
        Objects.requireNonNull(field, "field");
        Long old = currentParsed().fieldValues.put(field, Long.valueOf(value));
        return (old == null || old.longValue() == value) ? successPos : errorPos ^ (-1);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<java.time.chrono.Chronology> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer[] != java.util.function.Consumer<java.time.chrono.Chronology>[] */
    public void setParsed(Chronology chrono) {
        Objects.requireNonNull(chrono, "chrono");
        currentParsed().chrono = chrono;
        ArrayList<Consumer<Chronology>> arrayList = this.chronoListeners;
        if (arrayList != null && !arrayList.isEmpty()) {
            Consumer[] consumerArr = (Consumer[]) this.chronoListeners.toArray(new Consumer[1]);
            this.chronoListeners.clear();
            for (Consumer consumer : consumerArr) {
                consumer.accept(chrono);
            }
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<java.time.chrono.Chronology> */
    public void addChronoChangedListener(Consumer<Chronology> consumer) {
        if (this.chronoListeners == null) {
            this.chronoListeners = new ArrayList<>();
        }
        this.chronoListeners.add(consumer);
    }

    public void setParsed(ZoneId zone) {
        Objects.requireNonNull(zone, "zone");
        currentParsed().zone = zone;
    }

    public void setParsedLeapSecond() {
        currentParsed().leapSecond = true;
    }

    public String toString() {
        return currentParsed().toString();
    }
}
