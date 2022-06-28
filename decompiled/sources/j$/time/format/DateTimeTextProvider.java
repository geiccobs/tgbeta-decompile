package j$.time.format;

import j$.time.chrono.Chronology;
import j$.time.chrono.IsoChronology;
import j$.time.temporal.ChronoField;
import j$.time.temporal.TemporalField;
import j$.util.concurrent.ConcurrentHashMap;
import java.text.DateFormatSymbols;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
/* loaded from: classes2.dex */
public class DateTimeTextProvider {
    private static final ConcurrentMap<Map.Entry<TemporalField, Locale>, Object> CACHE = new ConcurrentHashMap(16, 0.75f, 2);
    private static final Comparator<Map.Entry<String, Long>> COMPARATOR = new Comparator<Map.Entry<String, Long>>() { // from class: j$.time.format.DateTimeTextProvider.1
        public int compare(Map.Entry<String, Long> obj1, Map.Entry<String, Long> obj2) {
            return obj2.getKey().length() - obj1.getKey().length();
        }
    };

    public static DateTimeTextProvider getInstance() {
        return new DateTimeTextProvider();
    }

    public String getText(TemporalField field, long value, TextStyle style, Locale locale) {
        Object store = findStore(field, locale);
        if (store instanceof LocaleStore) {
            return ((LocaleStore) store).getText(value, style);
        }
        return null;
    }

    public String getText(Chronology chrono, TemporalField field, long value, TextStyle style, Locale locale) {
        if (chrono == IsoChronology.INSTANCE || !(field instanceof ChronoField)) {
            return getText(field, value, style, locale);
        }
        return null;
    }

    public Iterator<Map.Entry<String, Long>> getTextIterator(TemporalField field, TextStyle style, Locale locale) {
        Object store = findStore(field, locale);
        if (store instanceof LocaleStore) {
            return ((LocaleStore) store).getTextIterator(style);
        }
        return null;
    }

    public Iterator<Map.Entry<String, Long>> getTextIterator(Chronology chrono, TemporalField field, TextStyle style, Locale locale) {
        if (chrono == IsoChronology.INSTANCE || !(field instanceof ChronoField)) {
            return getTextIterator(field, style, locale);
        }
        return null;
    }

    private Object findStore(TemporalField field, Locale locale) {
        Map.Entry<TemporalField, Locale> createEntry = createEntry(field, locale);
        ConcurrentMap<Map.Entry<TemporalField, Locale>, Object> concurrentMap = CACHE;
        Object store = concurrentMap.get(createEntry);
        if (store == null) {
            concurrentMap.putIfAbsent(createEntry, createStore(field, locale));
            return concurrentMap.get(createEntry);
        }
        return store;
    }

    private static int toWeekDay(int calWeekDay) {
        if (calWeekDay == 1) {
            return 7;
        }
        return calWeekDay - 1;
    }

    private Object createStore(TemporalField field, Locale locale) {
        Map<java.time.format.TextStyle, Map<Long, String>> styleMap = new HashMap<>();
        if (field != ChronoField.ERA) {
            if (field != ChronoField.MONTH_OF_YEAR) {
                if (field != ChronoField.DAY_OF_WEEK) {
                    if (field == ChronoField.AMPM_OF_DAY) {
                        DateFormatSymbols symbols = DateFormatSymbols.getInstance(locale);
                        Map<Long, String> fullMap = new HashMap<>();
                        Map<Long, String> narrowMap = new HashMap<>();
                        String[] amPmSymbols = symbols.getAmPmStrings();
                        for (int i = 0; i < amPmSymbols.length; i++) {
                            if (!amPmSymbols[i].isEmpty()) {
                                fullMap.put(Long.valueOf(i), amPmSymbols[i]);
                                narrowMap.put(Long.valueOf(i), firstCodePoint(amPmSymbols[i]));
                            }
                        }
                        if (!fullMap.isEmpty()) {
                            styleMap.put(TextStyle.FULL, fullMap);
                            styleMap.put(TextStyle.SHORT, fullMap);
                            styleMap.put(TextStyle.NARROW, narrowMap);
                        }
                        return new LocaleStore(styleMap);
                    }
                    return "";
                }
                DateFormatSymbols symbols2 = DateFormatSymbols.getInstance(locale);
                Map<Long, String> longMap = new HashMap<>();
                String[] longSymbols = symbols2.getWeekdays();
                longMap.put(1L, longSymbols[2]);
                longMap.put(2L, longSymbols[3]);
                longMap.put(3L, longSymbols[4]);
                longMap.put(4L, longSymbols[5]);
                longMap.put(5L, longSymbols[6]);
                longMap.put(6L, longSymbols[7]);
                longMap.put(7L, longSymbols[1]);
                styleMap.put(TextStyle.FULL, longMap);
                Map<Long, String> narrowMap2 = new HashMap<>();
                narrowMap2.put(1L, firstCodePoint(longSymbols[2]));
                narrowMap2.put(2L, firstCodePoint(longSymbols[3]));
                narrowMap2.put(3L, firstCodePoint(longSymbols[4]));
                narrowMap2.put(4L, firstCodePoint(longSymbols[5]));
                narrowMap2.put(5L, firstCodePoint(longSymbols[6]));
                narrowMap2.put(6L, firstCodePoint(longSymbols[7]));
                narrowMap2.put(7L, firstCodePoint(longSymbols[1]));
                styleMap.put(TextStyle.NARROW, narrowMap2);
                Map<Long, String> shortMap = new HashMap<>();
                String[] shortSymbols = symbols2.getShortWeekdays();
                shortMap.put(1L, shortSymbols[2]);
                shortMap.put(2L, shortSymbols[3]);
                shortMap.put(3L, shortSymbols[4]);
                shortMap.put(4L, shortSymbols[5]);
                shortMap.put(5L, shortSymbols[6]);
                shortMap.put(6L, shortSymbols[7]);
                shortMap.put(7L, shortSymbols[1]);
                styleMap.put(TextStyle.SHORT, shortMap);
                return new LocaleStore(styleMap);
            }
            DateFormatSymbols symbols3 = DateFormatSymbols.getInstance(locale);
            Map<Long, String> longMap2 = new HashMap<>();
            Map<Long, String> narrowMap3 = new HashMap<>();
            String[] longMonths = symbols3.getMonths();
            for (int i2 = 0; i2 < longMonths.length; i2++) {
                if (!longMonths[i2].isEmpty()) {
                    longMap2.put(Long.valueOf(i2 + 1), longMonths[i2]);
                    narrowMap3.put(Long.valueOf(i2 + 1), firstCodePoint(longMonths[i2]));
                }
            }
            if (!longMap2.isEmpty()) {
                styleMap.put(TextStyle.FULL, longMap2);
                styleMap.put(TextStyle.NARROW, narrowMap3);
            }
            Map<Long, String> shortMap2 = new HashMap<>();
            String[] shortMonths = symbols3.getShortMonths();
            for (int i3 = 0; i3 < shortMonths.length; i3++) {
                if (!shortMonths[i3].isEmpty()) {
                    shortMap2.put(Long.valueOf(i3 + 1), shortMonths[i3]);
                }
            }
            if (!shortMap2.isEmpty()) {
                styleMap.put(TextStyle.SHORT, shortMap2);
            }
            return new LocaleStore(styleMap);
        }
        DateFormatSymbols symbols4 = DateFormatSymbols.getInstance(locale);
        Map<Long, String> fullMap2 = new HashMap<>();
        Map<Long, String> narrowMap4 = new HashMap<>();
        String[] eraSymbols = symbols4.getEras();
        for (int i4 = 0; i4 < eraSymbols.length; i4++) {
            if (!eraSymbols[i4].isEmpty()) {
                fullMap2.put(Long.valueOf(i4), eraSymbols[i4]);
                narrowMap4.put(Long.valueOf(i4), firstCodePoint(eraSymbols[i4]));
            }
        }
        if (!fullMap2.isEmpty()) {
            styleMap.put(TextStyle.FULL, fullMap2);
            styleMap.put(TextStyle.SHORT, fullMap2);
            styleMap.put(TextStyle.NARROW, narrowMap4);
        }
        return new LocaleStore(styleMap);
    }

    private static String firstCodePoint(String string) {
        return string.substring(0, Character.charCount(string.codePointAt(0)));
    }

    public static <A, B> Map.Entry<A, B> createEntry(A text, B field) {
        return new AbstractMap.SimpleImmutableEntry(text, field);
    }

    /* loaded from: classes2.dex */
    public static final class LocaleStore {
        private final Map<TextStyle, List<Map.Entry<String, Long>>> parsable;
        private final Map<TextStyle, Map<Long, String>> valueTextMap;

        public LocaleStore(Map<TextStyle, Map<Long, String>> map) {
            this.valueTextMap = map;
            HashMap hashMap = new HashMap();
            ArrayList arrayList = new ArrayList();
            for (Map.Entry<TextStyle, Map<Long, String>> entry : map.entrySet()) {
                Map<String, Map.Entry<String, Long>> reverse = new HashMap<>();
                for (Map.Entry<Long, String> entry2 : entry.getValue().entrySet()) {
                    reverse.put(entry2.getValue(), DateTimeTextProvider.createEntry(entry2.getValue(), entry2.getKey()));
                }
                ArrayList arrayList2 = new ArrayList(reverse.values());
                Collections.sort(arrayList2, DateTimeTextProvider.COMPARATOR);
                hashMap.put(entry.getKey(), arrayList2);
                arrayList.addAll(arrayList2);
                hashMap.put(null, arrayList);
            }
            Collections.sort(arrayList, DateTimeTextProvider.COMPARATOR);
            this.parsable = hashMap;
        }

        public String getText(long value, TextStyle style) {
            Map<Long, String> map = this.valueTextMap.get(style);
            if (map != null) {
                return map.get(Long.valueOf(value));
            }
            return null;
        }

        public Iterator<Map.Entry<String, Long>> getTextIterator(TextStyle style) {
            List<Map.Entry<String, Long>> list = this.parsable.get(style);
            if (list != null) {
                return list.iterator();
            }
            return null;
        }
    }
}