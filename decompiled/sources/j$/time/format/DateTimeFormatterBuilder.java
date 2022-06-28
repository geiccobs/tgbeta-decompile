package j$.time.format;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import com.google.android.gms.location.LocationRequest;
import com.microsoft.appcenter.Constants;
import j$.time.Clock$TickClock$$ExternalSyntheticBackport0;
import j$.time.DateTimeException;
import j$.time.Duration$$ExternalSyntheticBackport0;
import j$.time.Duration$$ExternalSyntheticBackport1;
import j$.time.Instant;
import j$.time.LocalDate;
import j$.time.LocalDate$$ExternalSyntheticBackport0;
import j$.time.LocalDateTime;
import j$.time.ZoneId;
import j$.time.ZoneOffset;
import j$.time.chrono.ChronoLocalDate;
import j$.time.chrono.Chronology;
import j$.time.chrono.IsoChronology;
import j$.time.format.DateTimeFormatterBuilder;
import j$.time.format.DateTimeTextProvider;
import j$.time.temporal.ChronoField;
import j$.time.temporal.IsoFields;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQueries;
import j$.time.temporal.TemporalQuery;
import j$.time.temporal.ValueRange;
import j$.time.temporal.WeekFields;
import j$.time.zone.ZoneRulesProvider;
import j$.util.Objects;
import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.Consumer;
import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import org.telegram.ui.Components.UndoView;
/* loaded from: classes2.dex */
public final class DateTimeFormatterBuilder {
    private static final Map<Character, TemporalField> FIELD_MAP;
    private DateTimeFormatterBuilder active;
    private final boolean optional;
    private char padNextChar;
    private int padNextWidth;
    private final DateTimeFormatterBuilder parent;
    private final List<DateTimePrinterParser> printerParsers;
    private int valueParserIndex;
    private static final TemporalQuery<ZoneId> QUERY_REGION_ONLY = DateTimeFormatterBuilder$$ExternalSyntheticLambda0.INSTANCE;
    static final Comparator<String> LENGTH_SORT = new Comparator<String>() { // from class: j$.time.format.DateTimeFormatterBuilder.2
        public int compare(String str1, String str2) {
            return str1.length() == str2.length() ? str1.compareTo(str2) : str1.length() - str2.length();
        }
    };

    /* loaded from: classes2.dex */
    public interface DateTimePrinterParser {
        boolean format(DateTimePrintContext dateTimePrintContext, StringBuilder sb);

        int parse(DateTimeParseContext dateTimeParseContext, CharSequence charSequence, int i);
    }

    static {
        HashMap hashMap = new HashMap();
        FIELD_MAP = hashMap;
        hashMap.put('G', ChronoField.ERA);
        hashMap.put('y', ChronoField.YEAR_OF_ERA);
        hashMap.put('u', ChronoField.YEAR);
        hashMap.put('Q', IsoFields.QUARTER_OF_YEAR);
        hashMap.put('q', IsoFields.QUARTER_OF_YEAR);
        hashMap.put('M', ChronoField.MONTH_OF_YEAR);
        hashMap.put('L', ChronoField.MONTH_OF_YEAR);
        hashMap.put('D', ChronoField.DAY_OF_YEAR);
        hashMap.put('d', ChronoField.DAY_OF_MONTH);
        hashMap.put('F', ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH);
        hashMap.put('E', ChronoField.DAY_OF_WEEK);
        hashMap.put('c', ChronoField.DAY_OF_WEEK);
        hashMap.put('e', ChronoField.DAY_OF_WEEK);
        hashMap.put('a', ChronoField.AMPM_OF_DAY);
        hashMap.put('H', ChronoField.HOUR_OF_DAY);
        hashMap.put('k', ChronoField.CLOCK_HOUR_OF_DAY);
        hashMap.put('K', ChronoField.HOUR_OF_AMPM);
        hashMap.put('h', ChronoField.CLOCK_HOUR_OF_AMPM);
        hashMap.put('m', ChronoField.MINUTE_OF_HOUR);
        hashMap.put('s', ChronoField.SECOND_OF_MINUTE);
        hashMap.put('S', ChronoField.NANO_OF_SECOND);
        hashMap.put('A', ChronoField.MILLI_OF_DAY);
        hashMap.put('n', ChronoField.NANO_OF_SECOND);
        hashMap.put('N', ChronoField.NANO_OF_DAY);
    }

    public static /* synthetic */ ZoneId lambda$static$0(TemporalAccessor temporal) {
        ZoneId zone = (ZoneId) temporal.query(TemporalQueries.zoneId());
        if (zone == null || (zone instanceof ZoneOffset)) {
            return null;
        }
        return zone;
    }

    public static String getLocalizedDateTimePattern(FormatStyle dateStyle, FormatStyle timeStyle, Chronology chrono, Locale locale) {
        DateFormat format;
        Objects.requireNonNull(locale, "locale");
        Objects.requireNonNull(chrono, "chrono");
        if (dateStyle == null && timeStyle == null) {
            throw new IllegalArgumentException("Either dateStyle or timeStyle must be non-null");
        }
        if (timeStyle == null) {
            format = DateFormat.getDateInstance(dateStyle.ordinal(), locale);
        } else if (dateStyle == null) {
            format = DateFormat.getTimeInstance(timeStyle.ordinal(), locale);
        } else {
            format = DateFormat.getDateTimeInstance(dateStyle.ordinal(), timeStyle.ordinal(), locale);
        }
        if (format instanceof SimpleDateFormat) {
            String pattern = ((SimpleDateFormat) format).toPattern();
            return DateTimeFormatterBuilderHelper.transformAndroidJavaTextDateTimePattern(pattern);
        }
        throw new UnsupportedOperationException("Can't determine pattern from " + format);
    }

    private static int convertStyle(FormatStyle style) {
        if (style == null) {
            return -1;
        }
        return style.ordinal();
    }

    public DateTimeFormatterBuilder() {
        this.active = this;
        this.printerParsers = new ArrayList();
        this.valueParserIndex = -1;
        this.parent = null;
        this.optional = false;
    }

    private DateTimeFormatterBuilder(DateTimeFormatterBuilder parent, boolean optional) {
        this.active = this;
        this.printerParsers = new ArrayList();
        this.valueParserIndex = -1;
        this.parent = parent;
        this.optional = optional;
    }

    public DateTimeFormatterBuilder parseCaseSensitive() {
        appendInternal(SettingsParser.SENSITIVE);
        return this;
    }

    public DateTimeFormatterBuilder parseCaseInsensitive() {
        appendInternal(SettingsParser.INSENSITIVE);
        return this;
    }

    public DateTimeFormatterBuilder parseStrict() {
        appendInternal(SettingsParser.STRICT);
        return this;
    }

    public DateTimeFormatterBuilder parseLenient() {
        appendInternal(SettingsParser.LENIENT);
        return this;
    }

    public DateTimeFormatterBuilder parseDefaulting(TemporalField field, long value) {
        Objects.requireNonNull(field, "field");
        appendInternal(new DefaultValueParser(field, value));
        return this;
    }

    public DateTimeFormatterBuilder appendValue(TemporalField field) {
        Objects.requireNonNull(field, "field");
        appendValue(new NumberPrinterParser(field, 1, 19, SignStyle.NORMAL));
        return this;
    }

    public DateTimeFormatterBuilder appendValue(TemporalField field, int width) {
        Objects.requireNonNull(field, "field");
        if (width < 1 || width > 19) {
            throw new IllegalArgumentException("The width must be from 1 to 19 inclusive but was " + width);
        }
        NumberPrinterParser pp = new NumberPrinterParser(field, width, width, SignStyle.NOT_NEGATIVE);
        appendValue(pp);
        return this;
    }

    public DateTimeFormatterBuilder appendValue(TemporalField field, int minWidth, int maxWidth, SignStyle signStyle) {
        if (minWidth == maxWidth && signStyle == SignStyle.NOT_NEGATIVE) {
            return appendValue(field, maxWidth);
        }
        Objects.requireNonNull(field, "field");
        Objects.requireNonNull(signStyle, "signStyle");
        if (minWidth < 1 || minWidth > 19) {
            throw new IllegalArgumentException("The minimum width must be from 1 to 19 inclusive but was " + minWidth);
        } else if (maxWidth < 1 || maxWidth > 19) {
            throw new IllegalArgumentException("The maximum width must be from 1 to 19 inclusive but was " + maxWidth);
        } else if (maxWidth < minWidth) {
            throw new IllegalArgumentException("The maximum width must exceed or equal the minimum width but " + maxWidth + " < " + minWidth);
        } else {
            NumberPrinterParser pp = new NumberPrinterParser(field, minWidth, maxWidth, signStyle);
            appendValue(pp);
            return this;
        }
    }

    public DateTimeFormatterBuilder appendValueReduced(TemporalField field, int width, int maxWidth, int baseValue) {
        Objects.requireNonNull(field, "field");
        ReducedPrinterParser pp = new ReducedPrinterParser(field, width, maxWidth, baseValue, null);
        appendValue(pp);
        return this;
    }

    public DateTimeFormatterBuilder appendValueReduced(TemporalField field, int width, int maxWidth, ChronoLocalDate baseDate) {
        Objects.requireNonNull(field, "field");
        Objects.requireNonNull(baseDate, "baseDate");
        ReducedPrinterParser pp = new ReducedPrinterParser(field, width, maxWidth, 0, baseDate);
        appendValue(pp);
        return this;
    }

    private DateTimeFormatterBuilder appendValue(NumberPrinterParser pp) {
        NumberPrinterParser basePP;
        DateTimeFormatterBuilder dateTimeFormatterBuilder = this.active;
        if (dateTimeFormatterBuilder.valueParserIndex >= 0) {
            int activeValueParser = dateTimeFormatterBuilder.valueParserIndex;
            NumberPrinterParser basePP2 = (NumberPrinterParser) dateTimeFormatterBuilder.printerParsers.get(activeValueParser);
            if (pp.minWidth == pp.maxWidth && pp.signStyle == SignStyle.NOT_NEGATIVE) {
                basePP = basePP2.withSubsequentWidth(pp.maxWidth);
                appendInternal(pp.withFixedWidth());
                this.active.valueParserIndex = activeValueParser;
            } else {
                basePP = basePP2.withFixedWidth();
                this.active.valueParserIndex = appendInternal(pp);
            }
            this.active.printerParsers.set(activeValueParser, basePP);
        } else {
            dateTimeFormatterBuilder.valueParserIndex = appendInternal(pp);
        }
        return this;
    }

    public DateTimeFormatterBuilder appendFraction(TemporalField field, int minWidth, int maxWidth, boolean decimalPoint) {
        appendInternal(new FractionPrinterParser(field, minWidth, maxWidth, decimalPoint));
        return this;
    }

    public DateTimeFormatterBuilder appendText(TemporalField field) {
        return appendText(field, TextStyle.FULL);
    }

    public DateTimeFormatterBuilder appendText(TemporalField field, TextStyle textStyle) {
        Objects.requireNonNull(field, "field");
        Objects.requireNonNull(textStyle, "textStyle");
        appendInternal(new TextPrinterParser(field, textStyle, DateTimeTextProvider.getInstance()));
        return this;
    }

    public DateTimeFormatterBuilder appendText(TemporalField field, Map<Long, String> textLookup) {
        Objects.requireNonNull(field, "field");
        Objects.requireNonNull(textLookup, "textLookup");
        Map<Long, String> copy = new LinkedHashMap<>(textLookup);
        Map<java.time.format.TextStyle, Map<Long, String>> map = Collections.singletonMap(TextStyle.FULL, copy);
        final DateTimeTextProvider.LocaleStore store = new DateTimeTextProvider.LocaleStore(map);
        DateTimeTextProvider provider = new DateTimeTextProvider() { // from class: j$.time.format.DateTimeFormatterBuilder.1
            @Override // j$.time.format.DateTimeTextProvider
            public String getText(TemporalField field2, long value, TextStyle style, Locale locale) {
                return store.getText(value, style);
            }

            @Override // j$.time.format.DateTimeTextProvider
            public Iterator<Map.Entry<String, Long>> getTextIterator(TemporalField field2, TextStyle style, Locale locale) {
                return store.getTextIterator(style);
            }
        };
        appendInternal(new TextPrinterParser(field, TextStyle.FULL, provider));
        return this;
    }

    public DateTimeFormatterBuilder appendInstant() {
        appendInternal(new InstantPrinterParser(-2));
        return this;
    }

    public DateTimeFormatterBuilder appendInstant(int fractionalDigits) {
        if (fractionalDigits < -1 || fractionalDigits > 9) {
            throw new IllegalArgumentException("The fractional digits must be from -1 to 9 inclusive but was " + fractionalDigits);
        }
        appendInternal(new InstantPrinterParser(fractionalDigits));
        return this;
    }

    public DateTimeFormatterBuilder appendOffsetId() {
        appendInternal(OffsetIdPrinterParser.INSTANCE_ID_Z);
        return this;
    }

    public DateTimeFormatterBuilder appendOffset(String pattern, String noOffsetText) {
        appendInternal(new OffsetIdPrinterParser(pattern, noOffsetText));
        return this;
    }

    public DateTimeFormatterBuilder appendLocalizedOffset(TextStyle style) {
        Objects.requireNonNull(style, TtmlNode.TAG_STYLE);
        if (style != TextStyle.FULL && style != TextStyle.SHORT) {
            throw new IllegalArgumentException("Style must be either full or short");
        }
        appendInternal(new LocalizedOffsetIdPrinterParser(style));
        return this;
    }

    public DateTimeFormatterBuilder appendZoneId() {
        appendInternal(new ZoneIdPrinterParser(TemporalQueries.zoneId(), "ZoneId()"));
        return this;
    }

    public DateTimeFormatterBuilder appendZoneRegionId() {
        appendInternal(new ZoneIdPrinterParser(QUERY_REGION_ONLY, "ZoneRegionId()"));
        return this;
    }

    public DateTimeFormatterBuilder appendZoneOrOffsetId() {
        appendInternal(new ZoneIdPrinterParser(TemporalQueries.zone(), "ZoneOrOffsetId()"));
        return this;
    }

    public DateTimeFormatterBuilder appendZoneText(TextStyle textStyle) {
        appendInternal(new ZoneTextPrinterParser(textStyle, null));
        return this;
    }

    public DateTimeFormatterBuilder appendZoneText(TextStyle textStyle, Set<ZoneId> set) {
        Objects.requireNonNull(set, "preferredZones");
        appendInternal(new ZoneTextPrinterParser(textStyle, set));
        return this;
    }

    public DateTimeFormatterBuilder appendChronologyId() {
        appendInternal(new ChronoPrinterParser(null));
        return this;
    }

    public DateTimeFormatterBuilder appendChronologyText(TextStyle textStyle) {
        Objects.requireNonNull(textStyle, "textStyle");
        appendInternal(new ChronoPrinterParser(textStyle));
        return this;
    }

    public DateTimeFormatterBuilder appendLocalized(FormatStyle dateStyle, FormatStyle timeStyle) {
        if (dateStyle == null && timeStyle == null) {
            throw new IllegalArgumentException("Either the date or time style must be non-null");
        }
        appendInternal(new LocalizedPrinterParser(dateStyle, timeStyle));
        return this;
    }

    public DateTimeFormatterBuilder appendLiteral(char literal) {
        appendInternal(new CharLiteralPrinterParser(literal));
        return this;
    }

    public DateTimeFormatterBuilder appendLiteral(String literal) {
        Objects.requireNonNull(literal, "literal");
        if (literal.length() > 0) {
            if (literal.length() == 1) {
                appendInternal(new CharLiteralPrinterParser(literal.charAt(0)));
            } else {
                appendInternal(new StringLiteralPrinterParser(literal));
            }
        }
        return this;
    }

    public DateTimeFormatterBuilder append(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        appendInternal(formatter.toPrinterParser(false));
        return this;
    }

    public DateTimeFormatterBuilder appendOptional(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        appendInternal(formatter.toPrinterParser(true));
        return this;
    }

    public DateTimeFormatterBuilder appendPattern(String pattern) {
        Objects.requireNonNull(pattern, "pattern");
        parsePattern(pattern);
        return this;
    }

    private void parsePattern(String pattern) {
        int start = 0;
        while (start < pattern.length()) {
            char cur = pattern.charAt(start);
            if ((cur >= 'A' && cur <= 'Z') || (cur >= 'a' && cur <= 'z')) {
                int pos = start + 1;
                while (pos < pattern.length() && pattern.charAt(pos) == cur) {
                    pos++;
                }
                int count = pos - start;
                if (cur == 'p') {
                    int pad = 0;
                    if (pos < pattern.length() && (((cur = pattern.charAt(pos)) >= 'A' && cur <= 'Z') || (cur >= 'a' && cur <= 'z'))) {
                        pad = count;
                        int pos2 = pos + 1;
                        int start2 = pos;
                        while (pos2 < pattern.length() && pattern.charAt(pos2) == cur) {
                            pos2++;
                        }
                        pos = pos2;
                        count = pos2 - start2;
                    }
                    if (pad == 0) {
                        throw new IllegalArgumentException("Pad letter 'p' must be followed by valid pad pattern: " + pattern);
                    }
                    padNext(pad);
                }
                TemporalField field = FIELD_MAP.get(Character.valueOf(cur));
                if (field != null) {
                    parseField(cur, count, field);
                } else if (cur == 'z') {
                    if (count > 4) {
                        throw new IllegalArgumentException("Too many pattern letters: " + cur);
                    } else if (count == 4) {
                        appendZoneText(TextStyle.FULL);
                    } else {
                        appendZoneText(TextStyle.SHORT);
                    }
                } else if (cur != 'V') {
                    String str = "+0000";
                    if (cur == 'Z') {
                        if (count < 4) {
                            appendOffset("+HHMM", str);
                        } else if (count == 4) {
                            appendLocalizedOffset(TextStyle.FULL);
                        } else if (count == 5) {
                            appendOffset("+HH:MM:ss", "Z");
                        } else {
                            throw new IllegalArgumentException("Too many pattern letters: " + cur);
                        }
                    } else if (cur == 'O') {
                        if (count == 1) {
                            appendLocalizedOffset(TextStyle.SHORT);
                        } else if (count == 4) {
                            appendLocalizedOffset(TextStyle.FULL);
                        } else {
                            throw new IllegalArgumentException("Pattern letter count must be 1 or 4: " + cur);
                        }
                    } else {
                        int i = 0;
                        if (cur == 'X') {
                            if (count > 5) {
                                throw new IllegalArgumentException("Too many pattern letters: " + cur);
                            }
                            String[] strArr = OffsetIdPrinterParser.PATTERNS;
                            if (count != 1) {
                                i = 1;
                            }
                            appendOffset(strArr[i + count], "Z");
                        } else if (cur == 'x') {
                            if (count > 5) {
                                throw new IllegalArgumentException("Too many pattern letters: " + cur);
                            }
                            if (count == 1) {
                                str = "+00";
                            } else if (count % 2 != 0) {
                                str = "+00:00";
                            }
                            String zero = str;
                            String[] strArr2 = OffsetIdPrinterParser.PATTERNS;
                            if (count != 1) {
                                i = 1;
                            }
                            appendOffset(strArr2[i + count], zero);
                        } else if (cur == 'W') {
                            if (count > 1) {
                                throw new IllegalArgumentException("Too many pattern letters: " + cur);
                            }
                            appendInternal(new WeekBasedFieldPrinterParser(cur, count));
                        } else if (cur == 'w') {
                            if (count > 2) {
                                throw new IllegalArgumentException("Too many pattern letters: " + cur);
                            }
                            appendInternal(new WeekBasedFieldPrinterParser(cur, count));
                        } else if (cur == 'Y') {
                            appendInternal(new WeekBasedFieldPrinterParser(cur, count));
                        } else {
                            throw new IllegalArgumentException("Unknown pattern letter: " + cur);
                        }
                    }
                } else if (count != 2) {
                    throw new IllegalArgumentException("Pattern letter count must be 2: " + cur);
                } else {
                    appendZoneId();
                }
                start = pos - 1;
            } else if (cur == '\'') {
                int pos3 = start + 1;
                while (pos3 < pattern.length()) {
                    if (pattern.charAt(pos3) == '\'') {
                        if (pos3 + 1 >= pattern.length() || pattern.charAt(pos3 + 1) != '\'') {
                            break;
                        }
                        pos3++;
                    }
                    pos3++;
                }
                if (pos3 >= pattern.length()) {
                    throw new IllegalArgumentException("Pattern ends with an incomplete string literal: " + pattern);
                }
                String str2 = pattern.substring(start + 1, pos3);
                if (str2.length() != 0) {
                    appendLiteral(str2.replace("''", "'"));
                } else {
                    appendLiteral('\'');
                }
                start = pos3;
            } else if (cur == '[') {
                optionalStart();
            } else if (cur == ']') {
                if (this.active.parent == null) {
                    throw new IllegalArgumentException("Pattern invalid as it contains ] without previous [");
                }
                optionalEnd();
            } else if (cur == '{' || cur == '}' || cur == '#') {
                throw new IllegalArgumentException("Pattern includes reserved character: '" + cur + "'");
            } else {
                appendLiteral(cur);
            }
            start++;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private void parseField(char cur, int count, TemporalField field) {
        boolean standalone = false;
        switch (cur) {
            case 'D':
                if (count == 1) {
                    appendValue(field);
                    return;
                } else if (count > 3) {
                    throw new IllegalArgumentException("Too many pattern letters: " + cur);
                } else {
                    appendValue(field, count);
                    return;
                }
            case 'E':
            case UndoView.ACTION_PAYMENT_SUCCESS /* 77 */:
            case UndoView.ACTION_CLEAR_DATES /* 81 */:
            case 'e':
                break;
            case UndoView.ACTION_AUTO_DELETE_ON /* 70 */:
                if (count != 1) {
                    throw new IllegalArgumentException("Too many pattern letters: " + cur);
                }
                appendValue(field);
                return;
            case 'G':
                switch (count) {
                    case 1:
                    case 2:
                    case 3:
                        appendText(field, TextStyle.SHORT);
                        return;
                    case 4:
                        appendText(field, TextStyle.FULL);
                        return;
                    case 5:
                        appendText(field, TextStyle.NARROW);
                        return;
                    default:
                        throw new IllegalArgumentException("Too many pattern letters: " + cur);
                }
            case 'H':
            case UndoView.ACTION_GIGAGROUP_CANCEL /* 75 */:
            case 'd':
            case LocationRequest.PRIORITY_LOW_POWER /* 104 */:
            case 'k':
            case 'm':
            case 's':
                if (count == 1) {
                    appendValue(field);
                    return;
                } else if (count != 2) {
                    throw new IllegalArgumentException("Too many pattern letters: " + cur);
                } else {
                    appendValue(field, count);
                    return;
                }
            case UndoView.ACTION_GIGAGROUP_SUCCESS /* 76 */:
            case 'q':
                standalone = true;
                break;
            case 'S':
                appendFraction(ChronoField.NANO_OF_SECOND, count, count, false);
                return;
            case 'a':
                if (count != 1) {
                    throw new IllegalArgumentException("Too many pattern letters: " + cur);
                }
                appendText(field, TextStyle.SHORT);
                return;
            case 'c':
                if (count == 2) {
                    throw new IllegalArgumentException("Invalid pattern \"cc\"");
                }
                standalone = true;
                break;
            case 'u':
            case 'y':
                if (count == 2) {
                    appendValueReduced(field, 2, 2, ReducedPrinterParser.BASE_DATE);
                    return;
                } else if (count < 4) {
                    appendValue(field, count, 19, SignStyle.NORMAL);
                    return;
                } else {
                    appendValue(field, count, 19, SignStyle.EXCEEDS_PAD);
                    return;
                }
            default:
                if (count == 1) {
                    appendValue(field);
                    return;
                } else {
                    appendValue(field, count);
                    return;
                }
        }
        switch (count) {
            case 1:
            case 2:
                if (cur == 'c' || cur == 'e') {
                    appendInternal(new WeekBasedFieldPrinterParser(cur, count));
                    return;
                } else if (cur == 'E') {
                    appendText(field, TextStyle.SHORT);
                    return;
                } else if (count == 1) {
                    appendValue(field);
                    return;
                } else {
                    appendValue(field, 2);
                    return;
                }
            case 3:
                appendText(field, standalone ? TextStyle.SHORT_STANDALONE : TextStyle.SHORT);
                return;
            case 4:
                appendText(field, standalone ? TextStyle.FULL_STANDALONE : TextStyle.FULL);
                return;
            case 5:
                appendText(field, standalone ? TextStyle.NARROW_STANDALONE : TextStyle.NARROW);
                return;
            default:
                throw new IllegalArgumentException("Too many pattern letters: " + cur);
        }
    }

    public DateTimeFormatterBuilder padNext(int padWidth) {
        return padNext(padWidth, ' ');
    }

    public DateTimeFormatterBuilder padNext(int padWidth, char padChar) {
        if (padWidth < 1) {
            throw new IllegalArgumentException("The pad width must be at least one but was " + padWidth);
        }
        DateTimeFormatterBuilder dateTimeFormatterBuilder = this.active;
        dateTimeFormatterBuilder.padNextWidth = padWidth;
        dateTimeFormatterBuilder.padNextChar = padChar;
        dateTimeFormatterBuilder.valueParserIndex = -1;
        return this;
    }

    public DateTimeFormatterBuilder optionalStart() {
        this.active.valueParserIndex = -1;
        this.active = new DateTimeFormatterBuilder(this.active, true);
        return this;
    }

    public DateTimeFormatterBuilder optionalEnd() {
        DateTimeFormatterBuilder dateTimeFormatterBuilder = this.active;
        if (dateTimeFormatterBuilder.parent == null) {
            throw new IllegalStateException("Cannot call optionalEnd() as there was no previous call to optionalStart()");
        }
        if (dateTimeFormatterBuilder.printerParsers.size() > 0) {
            DateTimeFormatterBuilder dateTimeFormatterBuilder2 = this.active;
            CompositePrinterParser cpp = new CompositePrinterParser(dateTimeFormatterBuilder2.printerParsers, dateTimeFormatterBuilder2.optional);
            this.active = this.active.parent;
            appendInternal(cpp);
        } else {
            this.active = this.active.parent;
        }
        return this;
    }

    private int appendInternal(DateTimePrinterParser pp) {
        Objects.requireNonNull(pp, "pp");
        DateTimeFormatterBuilder dateTimeFormatterBuilder = this.active;
        int i = dateTimeFormatterBuilder.padNextWidth;
        if (i > 0) {
            if (pp != null) {
                pp = new PadPrinterParserDecorator(pp, i, dateTimeFormatterBuilder.padNextChar);
            }
            DateTimeFormatterBuilder dateTimeFormatterBuilder2 = this.active;
            dateTimeFormatterBuilder2.padNextWidth = 0;
            dateTimeFormatterBuilder2.padNextChar = (char) 0;
        }
        this.active.printerParsers.add(pp);
        DateTimeFormatterBuilder dateTimeFormatterBuilder3 = this.active;
        dateTimeFormatterBuilder3.valueParserIndex = -1;
        return dateTimeFormatterBuilder3.printerParsers.size() - 1;
    }

    public DateTimeFormatter toFormatter() {
        return toFormatter(Locale.getDefault());
    }

    public DateTimeFormatter toFormatter(Locale locale) {
        return toFormatter(locale, ResolverStyle.SMART, null);
    }

    public DateTimeFormatter toFormatter(ResolverStyle resolverStyle, Chronology chrono) {
        return toFormatter(Locale.getDefault(), resolverStyle, chrono);
    }

    private DateTimeFormatter toFormatter(Locale locale, ResolverStyle resolverStyle, Chronology chrono) {
        Objects.requireNonNull(locale, "locale");
        while (this.active.parent != null) {
            optionalEnd();
        }
        CompositePrinterParser pp = new CompositePrinterParser(this.printerParsers, false);
        return new DateTimeFormatter(pp, locale, DecimalStyle.STANDARD, resolverStyle, null, chrono, null);
    }

    /* loaded from: classes2.dex */
    public static final class CompositePrinterParser implements DateTimePrinterParser {
        private final boolean optional;
        private final DateTimePrinterParser[] printerParsers;

        CompositePrinterParser(List<DateTimePrinterParser> list, boolean optional) {
            this((DateTimePrinterParser[]) list.toArray(new DateTimePrinterParser[list.size()]), optional);
        }

        CompositePrinterParser(DateTimePrinterParser[] printerParsers, boolean optional) {
            this.printerParsers = printerParsers;
            this.optional = optional;
        }

        public CompositePrinterParser withOptional(boolean optional) {
            if (optional == this.optional) {
                return this;
            }
            return new CompositePrinterParser(this.printerParsers, optional);
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            DateTimePrinterParser[] dateTimePrinterParserArr;
            int length = buf.length();
            if (this.optional) {
                context.startOptional();
            }
            try {
                for (DateTimePrinterParser pp : this.printerParsers) {
                    if (!pp.format(context, buf)) {
                        buf.setLength(length);
                        return true;
                    }
                }
                if (this.optional) {
                    context.endOptional();
                }
                return true;
            } finally {
                if (this.optional) {
                    context.endOptional();
                }
            }
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            DateTimePrinterParser[] dateTimePrinterParserArr;
            DateTimePrinterParser[] dateTimePrinterParserArr2;
            if (this.optional) {
                context.startOptional();
                int pos = position;
                for (DateTimePrinterParser pp : this.printerParsers) {
                    pos = pp.parse(context, text, pos);
                    if (pos < 0) {
                        context.endOptional(false);
                        return position;
                    }
                }
                context.endOptional(true);
                return pos;
            }
            for (DateTimePrinterParser pp2 : this.printerParsers) {
                position = pp2.parse(context, text, position);
                if (position < 0) {
                    break;
                }
            }
            return position;
        }

        public String toString() {
            DateTimePrinterParser[] dateTimePrinterParserArr;
            StringBuilder buf = new StringBuilder();
            if (this.printerParsers != null) {
                buf.append(this.optional ? "[" : "(");
                for (DateTimePrinterParser pp : this.printerParsers) {
                    buf.append(pp);
                }
                buf.append(this.optional ? "]" : ")");
            }
            return buf.toString();
        }
    }

    /* loaded from: classes2.dex */
    public static final class PadPrinterParserDecorator implements DateTimePrinterParser {
        private final char padChar;
        private final int padWidth;
        private final DateTimePrinterParser printerParser;

        PadPrinterParserDecorator(DateTimePrinterParser printerParser, int padWidth, char padChar) {
            this.printerParser = printerParser;
            this.padWidth = padWidth;
            this.padChar = padChar;
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            int preLen = buf.length();
            if (!this.printerParser.format(context, buf)) {
                return false;
            }
            int len = buf.length() - preLen;
            if (len > this.padWidth) {
                throw new DateTimeException("Cannot print as output of " + len + " characters exceeds pad width of " + this.padWidth);
            }
            for (int i = 0; i < this.padWidth - len; i++) {
                buf.insert(preLen, this.padChar);
            }
            return true;
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            boolean strict = context.isStrict();
            if (position > text.length()) {
                throw new IndexOutOfBoundsException();
            }
            if (position == text.length()) {
                return position ^ (-1);
            }
            int endPos = this.padWidth + position;
            if (endPos > text.length()) {
                if (strict) {
                    return position ^ (-1);
                }
                endPos = text.length();
            }
            int pos = position;
            while (pos < endPos && context.charEquals(text.charAt(pos), this.padChar)) {
                pos++;
            }
            int resultPos = this.printerParser.parse(context, text.subSequence(0, endPos), pos);
            if (resultPos != endPos && strict) {
                return (position + pos) ^ (-1);
            }
            return resultPos;
        }

        public String toString() {
            String str;
            StringBuilder sb = new StringBuilder();
            sb.append("Pad(");
            sb.append(this.printerParser);
            sb.append(",");
            sb.append(this.padWidth);
            if (this.padChar == ' ') {
                str = ")";
            } else {
                str = ",'" + this.padChar + "')";
            }
            sb.append(str);
            return sb.toString();
        }
    }

    /* loaded from: classes2.dex */
    public enum SettingsParser implements DateTimePrinterParser {
        SENSITIVE,
        INSENSITIVE,
        STRICT,
        LENIENT;

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            return true;
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            switch (ordinal()) {
                case 0:
                    context.setCaseSensitive(true);
                    break;
                case 1:
                    context.setCaseSensitive(false);
                    break;
                case 2:
                    context.setStrict(true);
                    break;
                case 3:
                    context.setStrict(false);
                    break;
            }
            return position;
        }

        @Override // java.lang.Enum
        public String toString() {
            switch (ordinal()) {
                case 0:
                    return "ParseCaseSensitive(true)";
                case 1:
                    return "ParseCaseSensitive(false)";
                case 2:
                    return "ParseStrict(true)";
                case 3:
                    return "ParseStrict(false)";
                default:
                    throw new IllegalStateException("Unreachable");
            }
        }
    }

    /* loaded from: classes2.dex */
    static class DefaultValueParser implements DateTimePrinterParser {
        private final TemporalField field;
        private final long value;

        DefaultValueParser(TemporalField field, long value) {
            this.field = field;
            this.value = value;
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            return true;
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            if (context.getParsed(this.field) == null) {
                context.setParsedField(this.field, this.value, position, position);
            }
            return position;
        }
    }

    /* loaded from: classes2.dex */
    public static final class CharLiteralPrinterParser implements DateTimePrinterParser {
        private final char literal;

        CharLiteralPrinterParser(char literal) {
            this.literal = literal;
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            buf.append(this.literal);
            return true;
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            int length = text.length();
            if (position == length) {
                return position ^ (-1);
            }
            char ch = text.charAt(position);
            if (ch != this.literal && (context.isCaseSensitive() || (Character.toUpperCase(ch) != Character.toUpperCase(this.literal) && Character.toLowerCase(ch) != Character.toLowerCase(this.literal)))) {
                return position ^ (-1);
            }
            return position + 1;
        }

        public String toString() {
            if (this.literal == '\'') {
                return "''";
            }
            return "'" + this.literal + "'";
        }
    }

    /* loaded from: classes2.dex */
    public static final class StringLiteralPrinterParser implements DateTimePrinterParser {
        private final String literal;

        StringLiteralPrinterParser(String literal) {
            this.literal = literal;
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            buf.append(this.literal);
            return true;
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            int length = text.length();
            if (position > length || position < 0) {
                throw new IndexOutOfBoundsException();
            }
            String str = this.literal;
            if (!context.subSequenceEquals(text, position, str, 0, str.length())) {
                return position ^ (-1);
            }
            return this.literal.length() + position;
        }

        public String toString() {
            String converted = this.literal.replace("'", "''");
            return "'" + converted + "'";
        }
    }

    /* loaded from: classes2.dex */
    public static class NumberPrinterParser implements DateTimePrinterParser {
        static final long[] EXCEED_POINTS = {0, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, C.NANOS_PER_SECOND, 10000000000L};
        final TemporalField field;
        final int maxWidth;
        final int minWidth;
        private final SignStyle signStyle;
        final int subsequentWidth;

        NumberPrinterParser(TemporalField field, int minWidth, int maxWidth, SignStyle signStyle) {
            this.field = field;
            this.minWidth = minWidth;
            this.maxWidth = maxWidth;
            this.signStyle = signStyle;
            this.subsequentWidth = 0;
        }

        protected NumberPrinterParser(TemporalField field, int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
            this.field = field;
            this.minWidth = minWidth;
            this.maxWidth = maxWidth;
            this.signStyle = signStyle;
            this.subsequentWidth = subsequentWidth;
        }

        NumberPrinterParser withFixedWidth() {
            if (this.subsequentWidth == -1) {
                return this;
            }
            return new NumberPrinterParser(this.field, this.minWidth, this.maxWidth, this.signStyle, -1);
        }

        NumberPrinterParser withSubsequentWidth(int subsequentWidth) {
            return new NumberPrinterParser(this.field, this.minWidth, this.maxWidth, this.signStyle, this.subsequentWidth + subsequentWidth);
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            Long valueLong = context.getValue(this.field);
            if (valueLong == null) {
                return false;
            }
            long value = getValue(context, valueLong.longValue());
            DecimalStyle decimalStyle = context.getDecimalStyle();
            String str = value == Long.MIN_VALUE ? "9223372036854775808" : Long.toString(Math.abs(value));
            if (str.length() > this.maxWidth) {
                throw new DateTimeException("Field " + this.field + " cannot be printed as the value " + value + " exceeds the maximum print width of " + this.maxWidth);
            }
            String str2 = decimalStyle.convertNumberToI18N(str);
            if (value >= 0) {
                switch (AnonymousClass3.$SwitchMap$java$time$format$SignStyle[this.signStyle.ordinal()]) {
                    case 1:
                        int i = this.minWidth;
                        if (i < 19 && value >= EXCEED_POINTS[i]) {
                            buf.append(decimalStyle.getPositiveSign());
                            break;
                        }
                        break;
                    case 2:
                        buf.append(decimalStyle.getPositiveSign());
                        break;
                }
            } else {
                switch (AnonymousClass3.$SwitchMap$java$time$format$SignStyle[this.signStyle.ordinal()]) {
                    case 4:
                        throw new DateTimeException("Field " + this.field + " cannot be printed as the value " + value + " cannot be negative according to the SignStyle");
                    case 1:
                    case 2:
                    case 3:
                        buf.append(decimalStyle.getNegativeSign());
                        break;
                }
            }
            for (int i2 = 0; i2 < this.minWidth - str2.length(); i2++) {
                buf.append(decimalStyle.getZeroDigit());
            }
            buf.append(str2);
            return true;
        }

        long getValue(DateTimePrintContext context, long value) {
            return value;
        }

        boolean isFixedWidth(DateTimeParseContext context) {
            int i = this.subsequentWidth;
            return i == -1 || (i > 0 && this.minWidth == this.maxWidth && this.signStyle == SignStyle.NOT_NEGATIVE);
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            boolean positive;
            boolean negative;
            int position2;
            int pos;
            long total;
            BigInteger totalBig;
            long total2;
            int length;
            char sign;
            char sign2;
            int length2 = text.length();
            if (position == length2) {
                return position ^ (-1);
            }
            char sign3 = text.charAt(position);
            int i = 1;
            if (sign3 == context.getDecimalStyle().getPositiveSign()) {
                if (!this.signStyle.parse(true, context.isStrict(), this.minWidth == this.maxWidth)) {
                    return position ^ (-1);
                }
                position2 = position + 1;
                negative = false;
                positive = true;
            } else if (sign3 == context.getDecimalStyle().getNegativeSign()) {
                if (!this.signStyle.parse(false, context.isStrict(), this.minWidth == this.maxWidth)) {
                    return position ^ (-1);
                }
                position2 = position + 1;
                negative = true;
                positive = false;
            } else if (this.signStyle == SignStyle.ALWAYS && context.isStrict()) {
                return position ^ (-1);
            } else {
                position2 = position;
                negative = false;
                positive = false;
            }
            if (context.isStrict() || isFixedWidth(context)) {
                i = this.minWidth;
            }
            int effMinWidth = i;
            int minEndPos = position2 + effMinWidth;
            if (minEndPos <= length2) {
                int effMaxWidth = ((context.isStrict() || isFixedWidth(context)) ? this.maxWidth : 9) + Math.max(this.subsequentWidth, 0);
                long total3 = 0;
                BigInteger totalBig2 = null;
                int pos2 = position2;
                int pass = 0;
                int effMaxWidth2 = effMaxWidth;
                while (pass < 2) {
                    int maxEndPos = Math.min(pos2 + effMaxWidth2, length2);
                    while (true) {
                        if (pos2 >= maxEndPos) {
                            total2 = total3;
                            length = length2;
                            sign = sign3;
                            break;
                        }
                        int pos3 = pos2 + 1;
                        length = length2;
                        char ch = text.charAt(pos2);
                        int maxEndPos2 = maxEndPos;
                        int digit = context.getDecimalStyle().convertToDigit(ch);
                        if (digit < 0) {
                            int pos4 = pos3 - 1;
                            if (pos4 >= minEndPos) {
                                pos2 = pos4;
                                total2 = total3;
                                sign = sign3;
                            } else {
                                return position2 ^ (-1);
                            }
                        } else {
                            if (pos3 - position2 > 18) {
                                if (totalBig2 == null) {
                                    totalBig2 = BigInteger.valueOf(total3);
                                }
                                sign2 = sign3;
                                totalBig2 = totalBig2.multiply(BigInteger.TEN).add(BigInteger.valueOf(digit));
                            } else {
                                sign2 = sign3;
                                long j = 10 * total3;
                                long total4 = digit;
                                total3 = total4 + j;
                            }
                            pos2 = pos3;
                            maxEndPos = maxEndPos2;
                            length2 = length;
                            sign3 = sign2;
                        }
                    }
                    int maxEndPos3 = this.subsequentWidth;
                    if (maxEndPos3 > 0 && pass == 0) {
                        effMaxWidth2 = Math.max(effMinWidth, (pos2 - position2) - maxEndPos3);
                        pos2 = position2;
                        totalBig2 = null;
                        pass++;
                        total3 = 0;
                        length2 = length;
                        sign3 = sign;
                    } else {
                        pos = pos2;
                        total3 = total2;
                        break;
                    }
                }
                pos = pos2;
                if (negative) {
                    if (totalBig2 != null) {
                        if (totalBig2.equals(BigInteger.ZERO) && context.isStrict()) {
                            return (position2 - 1) ^ (-1);
                        }
                        total = total3;
                        totalBig = totalBig2.negate();
                    } else if (total3 == 0 && context.isStrict()) {
                        return (position2 - 1) ^ (-1);
                    } else {
                        total = -total3;
                        totalBig = totalBig2;
                    }
                } else {
                    if (this.signStyle == SignStyle.EXCEEDS_PAD && context.isStrict()) {
                        int parseLen = pos - position2;
                        if (positive) {
                            if (parseLen <= this.minWidth) {
                                return (position2 - 1) ^ (-1);
                            }
                        } else if (parseLen > this.minWidth) {
                            return position2 ^ (-1);
                        }
                    }
                    total = total3;
                    totalBig = totalBig2;
                }
                if (totalBig != null) {
                    if (totalBig.bitLength() > 63) {
                        totalBig = totalBig.divide(BigInteger.TEN);
                        pos--;
                    }
                    return setValue(context, totalBig.longValue(), position2, pos);
                }
                return setValue(context, total, position2, pos);
            }
            return position2 ^ (-1);
        }

        int setValue(DateTimeParseContext context, long value, int errorPos, int successPos) {
            return context.setParsedField(this.field, value, errorPos, successPos);
        }

        public String toString() {
            if (this.minWidth == 1 && this.maxWidth == 19 && this.signStyle == SignStyle.NORMAL) {
                return "Value(" + this.field + ")";
            } else if (this.minWidth == this.maxWidth && this.signStyle == SignStyle.NOT_NEGATIVE) {
                return "Value(" + this.field + "," + this.minWidth + ")";
            } else {
                return "Value(" + this.field + "," + this.minWidth + "," + this.maxWidth + "," + this.signStyle + ")";
            }
        }
    }

    /* renamed from: j$.time.format.DateTimeFormatterBuilder$3 */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$java$time$format$SignStyle;

        static {
            int[] iArr = new int[SignStyle.values().length];
            $SwitchMap$java$time$format$SignStyle = iArr;
            try {
                iArr[SignStyle.EXCEEDS_PAD.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$time$format$SignStyle[SignStyle.ALWAYS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$time$format$SignStyle[SignStyle.NORMAL.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$java$time$format$SignStyle[SignStyle.NOT_NEGATIVE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class ReducedPrinterParser extends NumberPrinterParser {
        static final LocalDate BASE_DATE = LocalDate.of(2000, 1, 1);
        private final ChronoLocalDate baseDate;
        private final int baseValue;

        ReducedPrinterParser(TemporalField field, int minWidth, int maxWidth, int baseValue, ChronoLocalDate baseDate) {
            this(field, minWidth, maxWidth, baseValue, baseDate, 0);
            if (minWidth < 1 || minWidth > 10) {
                throw new IllegalArgumentException("The minWidth must be from 1 to 10 inclusive but was " + minWidth);
            } else if (maxWidth < 1 || maxWidth > 10) {
                throw new IllegalArgumentException("The maxWidth must be from 1 to 10 inclusive but was " + minWidth);
            } else if (maxWidth < minWidth) {
                throw new IllegalArgumentException("Maximum width must exceed or equal the minimum width but " + maxWidth + " < " + minWidth);
            } else if (baseDate == null) {
                if (!field.range().isValidValue(baseValue)) {
                    throw new IllegalArgumentException("The base value must be within the range of the field");
                }
                if (baseValue + EXCEED_POINTS[maxWidth] > 2147483647L) {
                    throw new DateTimeException("Unable to add printer-parser as the range exceeds the capacity of an int");
                }
            }
        }

        private ReducedPrinterParser(TemporalField field, int minWidth, int maxWidth, int baseValue, ChronoLocalDate baseDate, int subsequentWidth) {
            super(field, minWidth, maxWidth, SignStyle.NOT_NEGATIVE, subsequentWidth);
            this.baseValue = baseValue;
            this.baseDate = baseDate;
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.NumberPrinterParser
        long getValue(DateTimePrintContext context, long value) {
            long absValue = Math.abs(value);
            int baseValue = this.baseValue;
            if (this.baseDate != null) {
                Chronology chrono = Chronology.CC.from(context.getTemporal());
                baseValue = chrono.date(this.baseDate).get(this.field);
            }
            if (value >= baseValue && value < baseValue + EXCEED_POINTS[this.minWidth]) {
                return absValue % EXCEED_POINTS[this.minWidth];
            }
            return absValue % EXCEED_POINTS[this.maxWidth];
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.NumberPrinterParser
        int setValue(final DateTimeParseContext context, final long value, final int errorPos, final int successPos) {
            long value2;
            long value3;
            int baseValue = this.baseValue;
            if (this.baseDate != null) {
                Chronology chrono = context.getEffectiveChronology();
                int baseValue2 = chrono.date(this.baseDate).get(this.field);
                context.addChronoChangedListener(new Consumer() { // from class: j$.time.format.DateTimeFormatterBuilder$ReducedPrinterParser$$ExternalSyntheticLambda0
                    @Override // j$.util.function.Consumer
                    public final void accept(Object obj) {
                        DateTimeFormatterBuilder.ReducedPrinterParser.this.m102xdf3a601e(context, value, errorPos, successPos, (Chronology) obj);
                    }

                    @Override // j$.util.function.Consumer
                    public /* synthetic */ Consumer andThen(Consumer consumer) {
                        return consumer.getClass();
                    }
                });
                baseValue = baseValue2;
            }
            int parseLen = successPos - errorPos;
            if (parseLen == this.minWidth && value >= 0) {
                long range = EXCEED_POINTS[this.minWidth];
                long lastPart = baseValue % range;
                long basePart = baseValue - lastPart;
                if (baseValue > 0) {
                    value3 = basePart + value;
                } else {
                    value3 = basePart - value;
                }
                if (value3 >= baseValue) {
                    value2 = value3;
                } else {
                    value2 = value3 + range;
                }
            } else {
                value2 = value;
            }
            return context.setParsedField(this.field, value2, errorPos, successPos);
        }

        /* renamed from: lambda$setValue$0$java-time-format-DateTimeFormatterBuilder$ReducedPrinterParser */
        public /* synthetic */ void m102xdf3a601e(DateTimeParseContext context, long initialValue, int errorPos, int successPos, Chronology _unused) {
            setValue(context, initialValue, errorPos, successPos);
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.NumberPrinterParser
        public ReducedPrinterParser withFixedWidth() {
            if (this.subsequentWidth == -1) {
                return this;
            }
            return new ReducedPrinterParser(this.field, this.minWidth, this.maxWidth, this.baseValue, this.baseDate, -1);
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.NumberPrinterParser
        public ReducedPrinterParser withSubsequentWidth(int subsequentWidth) {
            return new ReducedPrinterParser(this.field, this.minWidth, this.maxWidth, this.baseValue, this.baseDate, this.subsequentWidth + subsequentWidth);
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.NumberPrinterParser
        boolean isFixedWidth(DateTimeParseContext context) {
            if (!context.isStrict()) {
                return false;
            }
            return super.isFixedWidth(context);
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.NumberPrinterParser
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ReducedValue(");
            sb.append(this.field);
            sb.append(",");
            sb.append(this.minWidth);
            sb.append(",");
            sb.append(this.maxWidth);
            sb.append(",");
            Object obj = this.baseDate;
            if (obj == null) {
                obj = Integer.valueOf(this.baseValue);
            }
            sb.append(obj);
            sb.append(")");
            return sb.toString();
        }
    }

    /* loaded from: classes2.dex */
    public static final class FractionPrinterParser implements DateTimePrinterParser {
        private final boolean decimalPoint;
        private final TemporalField field;
        private final int maxWidth;
        private final int minWidth;

        FractionPrinterParser(TemporalField field, int minWidth, int maxWidth, boolean decimalPoint) {
            Objects.requireNonNull(field, "field");
            if (!field.range().isFixed()) {
                throw new IllegalArgumentException("Field must have a fixed set of values: " + field);
            } else if (minWidth < 0 || minWidth > 9) {
                throw new IllegalArgumentException("Minimum width must be from 0 to 9 inclusive but was " + minWidth);
            } else if (maxWidth < 1 || maxWidth > 9) {
                throw new IllegalArgumentException("Maximum width must be from 1 to 9 inclusive but was " + maxWidth);
            } else if (maxWidth < minWidth) {
                throw new IllegalArgumentException("Maximum width must exceed or equal the minimum width but " + maxWidth + " < " + minWidth);
            } else {
                this.field = field;
                this.minWidth = minWidth;
                this.maxWidth = maxWidth;
                this.decimalPoint = decimalPoint;
            }
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            Long value = context.getValue(this.field);
            if (value == null) {
                return false;
            }
            DecimalStyle decimalStyle = context.getDecimalStyle();
            BigDecimal fraction = convertToFraction(value.longValue());
            if (fraction.scale() == 0) {
                if (this.minWidth > 0) {
                    if (this.decimalPoint) {
                        buf.append(decimalStyle.getDecimalSeparator());
                    }
                    for (int i = 0; i < this.minWidth; i++) {
                        buf.append(decimalStyle.getZeroDigit());
                    }
                    return true;
                }
                return true;
            }
            int outputScale = Math.min(Math.max(fraction.scale(), this.minWidth), this.maxWidth);
            String str = fraction.setScale(outputScale, RoundingMode.FLOOR).toPlainString().substring(2);
            String str2 = decimalStyle.convertNumberToI18N(str);
            if (this.decimalPoint) {
                buf.append(decimalStyle.getDecimalSeparator());
            }
            buf.append(str2);
            return true;
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            int pos;
            int position2 = position;
            int effectiveMin = context.isStrict() ? this.minWidth : 0;
            int effectiveMax = context.isStrict() ? this.maxWidth : 9;
            int length = text.length();
            if (position2 == length) {
                return effectiveMin > 0 ? position2 ^ (-1) : position2;
            }
            if (this.decimalPoint) {
                if (text.charAt(position) != context.getDecimalStyle().getDecimalSeparator()) {
                    return effectiveMin > 0 ? position2 ^ (-1) : position2;
                }
                position2++;
            }
            int minEndPos = position2 + effectiveMin;
            if (minEndPos > length) {
                return position2 ^ (-1);
            }
            int maxEndPos = Math.min(position2 + effectiveMax, length);
            int pos2 = position2;
            int total = 0;
            while (true) {
                if (pos2 >= maxEndPos) {
                    pos = pos2;
                    break;
                }
                int pos3 = pos2 + 1;
                char ch = text.charAt(pos2);
                int digit = context.getDecimalStyle().convertToDigit(ch);
                if (digit < 0) {
                    if (pos3 < minEndPos) {
                        return position2 ^ (-1);
                    }
                    pos = pos3 - 1;
                } else {
                    total = (total * 10) + digit;
                    pos2 = pos3;
                }
            }
            BigDecimal fraction = new BigDecimal(total).movePointLeft(pos - position2);
            long value = convertFromFraction(fraction);
            return context.setParsedField(this.field, value, position2, pos);
        }

        private BigDecimal convertToFraction(long value) {
            ValueRange range = this.field.range();
            range.checkValidValue(value, this.field);
            BigDecimal minBD = BigDecimal.valueOf(range.getMinimum());
            BigDecimal rangeBD = BigDecimal.valueOf(range.getMaximum()).subtract(minBD).add(BigDecimal.ONE);
            BigDecimal valueBD = BigDecimal.valueOf(value).subtract(minBD);
            BigDecimal fraction = valueBD.divide(rangeBD, 9, RoundingMode.FLOOR);
            return fraction.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : fraction.stripTrailingZeros();
        }

        private long convertFromFraction(BigDecimal fraction) {
            ValueRange range = this.field.range();
            BigDecimal minBD = BigDecimal.valueOf(range.getMinimum());
            BigDecimal rangeBD = BigDecimal.valueOf(range.getMaximum()).subtract(minBD).add(BigDecimal.ONE);
            BigDecimal valueBD = fraction.multiply(rangeBD).setScale(0, RoundingMode.FLOOR).add(minBD);
            return valueBD.longValueExact();
        }

        public String toString() {
            String decimal = this.decimalPoint ? ",DecimalPoint" : "";
            return "Fraction(" + this.field + "," + this.minWidth + "," + this.maxWidth + decimal + ")";
        }
    }

    /* loaded from: classes2.dex */
    public static final class TextPrinterParser implements DateTimePrinterParser {
        private final TemporalField field;
        private volatile NumberPrinterParser numberPrinterParser;
        private final DateTimeTextProvider provider;
        private final TextStyle textStyle;

        TextPrinterParser(TemporalField field, TextStyle textStyle, DateTimeTextProvider provider) {
            this.field = field;
            this.textStyle = textStyle;
            this.provider = provider;
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            String text;
            Long value = context.getValue(this.field);
            if (value == null) {
                return false;
            }
            Chronology chrono = (Chronology) context.getTemporal().query(TemporalQueries.chronology());
            if (chrono == null || chrono == IsoChronology.INSTANCE) {
                text = this.provider.getText(this.field, value.longValue(), this.textStyle, context.getLocale());
            } else {
                text = this.provider.getText(chrono, this.field, value.longValue(), this.textStyle, context.getLocale());
            }
            if (text == null) {
                return numberPrinterParser().format(context, buf);
            }
            buf.append(text);
            return true;
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public int parse(DateTimeParseContext context, CharSequence parseText, int position) {
            int length = parseText.length();
            if (position < 0 || position > length) {
                throw new IndexOutOfBoundsException();
            }
            TextStyle style = context.isStrict() ? this.textStyle : null;
            Chronology chrono = context.getEffectiveChronology();
            Iterator<Map.Entry<String, Long>> it = (chrono == null || chrono == IsoChronology.INSTANCE) ? this.provider.getTextIterator(this.field, style, context.getLocale()) : this.provider.getTextIterator(chrono, this.field, style, context.getLocale());
            if (it != null) {
                while (it.hasNext()) {
                    Map.Entry<String, Long> entry = it.next();
                    String itText = entry.getKey();
                    if (context.subSequenceEquals(itText, 0, parseText, position, itText.length())) {
                        return context.setParsedField(this.field, entry.getValue().longValue(), position, position + itText.length());
                    }
                }
                if (context.isStrict()) {
                    return position ^ (-1);
                }
            }
            return numberPrinterParser().parse(context, parseText, position);
        }

        private NumberPrinterParser numberPrinterParser() {
            if (this.numberPrinterParser == null) {
                this.numberPrinterParser = new NumberPrinterParser(this.field, 1, 19, SignStyle.NORMAL);
            }
            return this.numberPrinterParser;
        }

        public String toString() {
            if (this.textStyle == TextStyle.FULL) {
                return "Text(" + this.field + ")";
            }
            return "Text(" + this.field + "," + this.textStyle + ")";
        }
    }

    /* loaded from: classes2.dex */
    public static final class InstantPrinterParser implements DateTimePrinterParser {
        private static final long SECONDS_0000_TO_1970 = 62167219200L;
        private static final long SECONDS_PER_10000_YEARS = 315569520000L;
        private final int fractionalDigits;

        InstantPrinterParser(int fractionalDigits) {
            this.fractionalDigits = fractionalDigits;
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            Long inSecs = context.getValue(ChronoField.INSTANT_SECONDS);
            Long inNanos = null;
            if (context.getTemporal().isSupported(ChronoField.NANO_OF_SECOND)) {
                inNanos = Long.valueOf(context.getTemporal().getLong(ChronoField.NANO_OF_SECOND));
            }
            if (inSecs == null) {
                return false;
            }
            long inSec = inSecs.longValue();
            int inNano = ChronoField.NANO_OF_SECOND.checkValidIntValue(inNanos != null ? inNanos.longValue() : 0L);
            if (inSec >= -62167219200L) {
                long zeroSecs = (inSec - SECONDS_PER_10000_YEARS) + SECONDS_0000_TO_1970;
                long hi = Duration$$ExternalSyntheticBackport0.m(zeroSecs, SECONDS_PER_10000_YEARS) + 1;
                LocalDateTime ldt = LocalDateTime.ofEpochSecond(Clock$TickClock$$ExternalSyntheticBackport0.m(zeroSecs, SECONDS_PER_10000_YEARS) - SECONDS_0000_TO_1970, 0, ZoneOffset.UTC);
                if (hi > 0) {
                    buf.append('+');
                    buf.append(hi);
                }
                buf.append(ldt);
                if (ldt.getSecond() == 0) {
                    buf.append(":00");
                }
            } else {
                long zeroSecs2 = inSec + SECONDS_0000_TO_1970;
                long hi2 = zeroSecs2 / SECONDS_PER_10000_YEARS;
                long lo = zeroSecs2 % SECONDS_PER_10000_YEARS;
                LocalDateTime ldt2 = LocalDateTime.ofEpochSecond(lo - SECONDS_0000_TO_1970, 0, ZoneOffset.UTC);
                int pos = buf.length();
                buf.append(ldt2);
                if (ldt2.getSecond() == 0) {
                    buf.append(":00");
                }
                if (hi2 < 0) {
                    if (ldt2.getYear() == -10000) {
                        buf.replace(pos, pos + 2, Long.toString(hi2 - 1));
                    } else if (lo == 0) {
                        buf.insert(pos, hi2);
                    } else {
                        buf.insert(pos + 1, Math.abs(hi2));
                    }
                }
            }
            int i = this.fractionalDigits;
            if ((i < 0 && inNano > 0) || i > 0) {
                buf.append('.');
                int div = 100000000;
                int i2 = 0;
                while (true) {
                    int i3 = this.fractionalDigits;
                    if ((i3 != -1 || inNano <= 0) && ((i3 != -2 || (inNano <= 0 && i2 % 3 == 0)) && i2 >= i3)) {
                        break;
                    }
                    int digit = inNano / div;
                    buf.append((char) (digit + 48));
                    inNano -= digit * div;
                    div /= 10;
                    i2++;
                }
            }
            buf.append('Z');
            return true;
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            int sec;
            int hour;
            int sec2;
            int i = this.fractionalDigits;
            int nano = 0;
            int minDigits = i < 0 ? 0 : i;
            if (i < 0) {
                i = 9;
            }
            int maxDigits = i;
            CompositePrinterParser parser = new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_LOCAL_DATE).appendLiteral('T').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE, 2).appendFraction(ChronoField.NANO_OF_SECOND, minDigits, maxDigits, true).appendLiteral('Z').toFormatter().toPrinterParser(false);
            DateTimeParseContext newContext = context.copy();
            int pos = parser.parse(newContext, text, position);
            if (pos < 0) {
                return pos;
            }
            long yearParsed = newContext.getParsed(ChronoField.YEAR).longValue();
            int month = newContext.getParsed(ChronoField.MONTH_OF_YEAR).intValue();
            int day = newContext.getParsed(ChronoField.DAY_OF_MONTH).intValue();
            int hour2 = newContext.getParsed(ChronoField.HOUR_OF_DAY).intValue();
            int min = newContext.getParsed(ChronoField.MINUTE_OF_HOUR).intValue();
            Long secVal = newContext.getParsed(ChronoField.SECOND_OF_MINUTE);
            Long nanoVal = newContext.getParsed(ChronoField.NANO_OF_SECOND);
            int sec3 = secVal != null ? secVal.intValue() : 0;
            if (nanoVal != null) {
                nano = nanoVal.intValue();
            }
            if (hour2 == 24 && min == 0 && sec3 == 0 && nano == 0) {
                hour = 0;
                sec = sec3;
                sec2 = 1;
            } else if (hour2 == 23 && min == 59 && sec3 == 60) {
                context.setParsedLeapSecond();
                hour = hour2;
                sec = 59;
                sec2 = 0;
            } else {
                hour = hour2;
                sec = sec3;
                sec2 = 0;
            }
            int year = ((int) yearParsed) % 10000;
            try {
                try {
                    LocalDateTime ldt = LocalDateTime.of(year, month, day, hour, min, sec, 0).plusDays(sec2);
                    long instantSecs = ldt.toEpochSecond(ZoneOffset.UTC);
                    int nano2 = nano;
                    try {
                        int successPos = context.setParsedField(ChronoField.INSTANT_SECONDS, instantSecs + Duration$$ExternalSyntheticBackport1.m(yearParsed / 10000, SECONDS_PER_10000_YEARS), position, pos);
                        return context.setParsedField(ChronoField.NANO_OF_SECOND, nano2, position, successPos);
                    } catch (RuntimeException e) {
                        int nano3 = position ^ (-1);
                        return nano3;
                    }
                } catch (RuntimeException e2) {
                }
            } catch (RuntimeException e3) {
            }
        }

        public String toString() {
            return "Instant()";
        }
    }

    /* loaded from: classes2.dex */
    public static final class OffsetIdPrinterParser implements DateTimePrinterParser {
        private final String noOffsetText;
        private final int type;
        static final String[] PATTERNS = {"+HH", "+HHmm", "+HH:mm", "+HHMM", "+HH:MM", "+HHMMss", "+HH:MM:ss", "+HHMMSS", "+HH:MM:SS"};
        static final OffsetIdPrinterParser INSTANCE_ID_Z = new OffsetIdPrinterParser("+HH:MM:ss", "Z");
        static final OffsetIdPrinterParser INSTANCE_ID_ZERO = new OffsetIdPrinterParser("+HH:MM:ss", "0");

        OffsetIdPrinterParser(String pattern, String noOffsetText) {
            Objects.requireNonNull(pattern, "pattern");
            Objects.requireNonNull(noOffsetText, "noOffsetText");
            this.type = checkPattern(pattern);
            this.noOffsetText = noOffsetText;
        }

        private int checkPattern(String pattern) {
            int i = 0;
            while (true) {
                String[] strArr = PATTERNS;
                if (i < strArr.length) {
                    if (!strArr[i].equals(pattern)) {
                        i++;
                    } else {
                        return i;
                    }
                } else {
                    throw new IllegalArgumentException("Invalid zone offset pattern: " + pattern);
                }
            }
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            Long offsetSecs = context.getValue(ChronoField.OFFSET_SECONDS);
            if (offsetSecs == null) {
                return false;
            }
            int totalSecs = LocalDate$$ExternalSyntheticBackport0.m(offsetSecs.longValue());
            if (totalSecs == 0) {
                buf.append(this.noOffsetText);
            } else {
                int absHours = Math.abs((totalSecs / 3600) % 100);
                int absMinutes = Math.abs((totalSecs / 60) % 60);
                int absSeconds = Math.abs(totalSecs % 60);
                int bufPos = buf.length();
                int output = absHours;
                buf.append(totalSecs < 0 ? "-" : "+");
                buf.append((char) ((absHours / 10) + 48));
                buf.append((char) ((absHours % 10) + 48));
                int i = this.type;
                if (i >= 3 || (i >= 1 && absMinutes > 0)) {
                    int i2 = i % 2;
                    String str = Constants.COMMON_SCHEMA_PREFIX_SEPARATOR;
                    buf.append(i2 == 0 ? str : "");
                    buf.append((char) ((absMinutes / 10) + 48));
                    buf.append((char) ((absMinutes % 10) + 48));
                    output += absMinutes;
                    int i3 = this.type;
                    if (i3 >= 7 || (i3 >= 5 && absSeconds > 0)) {
                        if (i3 % 2 != 0) {
                            str = "";
                        }
                        buf.append(str);
                        buf.append((char) ((absSeconds / 10) + 48));
                        buf.append((char) ((absSeconds % 10) + 48));
                        output += absSeconds;
                    }
                }
                if (output == 0) {
                    buf.setLength(bufPos);
                    buf.append(this.noOffsetText);
                }
            }
            return true;
        }

        /* JADX WARN: Removed duplicated region for block: B:34:0x0083  */
        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public int parse(j$.time.format.DateTimeParseContext r19, java.lang.CharSequence r20, int r21) {
            /*
                r18 = this;
                r0 = r18
                r7 = r20
                r8 = r21
                int r9 = r20.length()
                java.lang.String r1 = r0.noOffsetText
                int r10 = r1.length()
                if (r10 != 0) goto L23
                if (r8 != r9) goto L47
                j$.time.temporal.ChronoField r2 = j$.time.temporal.ChronoField.OFFSET_SECONDS
                r3 = 0
                r1 = r19
                r5 = r21
                r6 = r21
                int r1 = r1.setParsedField(r2, r3, r5, r6)
                return r1
            L23:
                if (r8 != r9) goto L28
                r1 = r8 ^ (-1)
                return r1
            L28:
                java.lang.String r4 = r0.noOffsetText
                r5 = 0
                r1 = r19
                r2 = r20
                r3 = r21
                r6 = r10
                boolean r1 = r1.subSequenceEquals(r2, r3, r4, r5, r6)
                if (r1 == 0) goto L47
                j$.time.temporal.ChronoField r2 = j$.time.temporal.ChronoField.OFFSET_SECONDS
                int r6 = r8 + r10
                r3 = 0
                r1 = r19
                r5 = r21
                int r1 = r1.setParsedField(r2, r3, r5, r6)
                return r1
            L47:
                char r11 = r20.charAt(r21)
                r1 = 43
                r2 = 45
                if (r11 == r1) goto L53
                if (r11 != r2) goto La8
            L53:
                r1 = 1
                if (r11 != r2) goto L58
                r2 = -1
                goto L59
            L58:
                r2 = 1
            L59:
                r12 = r2
                r2 = 4
                int[] r13 = new int[r2]
                int r2 = r8 + 1
                r3 = 0
                r13[r3] = r2
                boolean r2 = r0.parseNumber(r13, r1, r7, r1)
                r4 = 2
                r5 = 3
                if (r2 != 0) goto L80
                int r2 = r0.type
                if (r2 < r5) goto L70
                r2 = 1
                goto L71
            L70:
                r2 = 0
            L71:
                boolean r2 = r0.parseNumber(r13, r4, r7, r2)
                if (r2 != 0) goto L80
                boolean r2 = r0.parseNumber(r13, r5, r7, r3)
                if (r2 == 0) goto L7e
                goto L80
            L7e:
                r2 = 0
                goto L81
            L80:
                r2 = 1
            L81:
                if (r2 != 0) goto La8
                long r14 = (long) r12
                r1 = r13[r1]
                long r1 = (long) r1
                r16 = 3600(0xe10, double:1.7786E-320)
                long r1 = r1 * r16
                r4 = r13[r4]
                long r3 = (long) r4
                r16 = 60
                long r3 = r3 * r16
                long r1 = r1 + r3
                r3 = r13[r5]
                long r3 = (long) r3
                long r1 = r1 + r3
                long r14 = r14 * r1
                j$.time.temporal.ChronoField r2 = j$.time.temporal.ChronoField.OFFSET_SECONDS
                r1 = 0
                r6 = r13[r1]
                r1 = r19
                r3 = r14
                r5 = r21
                int r1 = r1.setParsedField(r2, r3, r5, r6)
                return r1
            La8:
                if (r10 != 0) goto Lb9
                j$.time.temporal.ChronoField r2 = j$.time.temporal.ChronoField.OFFSET_SECONDS
                int r6 = r8 + r10
                r3 = 0
                r1 = r19
                r5 = r21
                int r1 = r1.setParsedField(r2, r3, r5, r6)
                return r1
            Lb9:
                r1 = r8 ^ (-1)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.time.format.DateTimeFormatterBuilder.OffsetIdPrinterParser.parse(j$.time.format.DateTimeParseContext, java.lang.CharSequence, int):int");
        }

        private boolean parseNumber(int[] array, int arrayIndex, CharSequence parseText, boolean required) {
            int i = this.type;
            if ((i + 3) / 2 < arrayIndex) {
                return false;
            }
            int pos = array[0];
            if (i % 2 == 0 && arrayIndex > 1) {
                if (pos + 1 > parseText.length() || parseText.charAt(pos) != ':') {
                    return required;
                }
                pos++;
            }
            if (pos + 2 > parseText.length()) {
                return required;
            }
            int pos2 = pos + 1;
            char ch1 = parseText.charAt(pos);
            int pos3 = pos2 + 1;
            char ch2 = parseText.charAt(pos2);
            if (ch1 < '0' || ch1 > '9' || ch2 < '0' || ch2 > '9') {
                return required;
            }
            int value = ((ch1 - '0') * 10) + (ch2 - '0');
            if (value < 0 || value > 59) {
                return required;
            }
            array[arrayIndex] = value;
            array[0] = pos3;
            return false;
        }

        public String toString() {
            String converted = this.noOffsetText.replace("'", "''");
            return "Offset(" + PATTERNS[this.type] + ",'" + converted + "')";
        }
    }

    /* loaded from: classes2.dex */
    public static final class LocalizedOffsetIdPrinterParser implements DateTimePrinterParser {
        private final TextStyle style;

        LocalizedOffsetIdPrinterParser(TextStyle style) {
            this.style = style;
        }

        private static StringBuilder appendHMS(StringBuilder buf, int t) {
            buf.append((char) ((t / 10) + 48));
            buf.append((char) ((t % 10) + 48));
            return buf;
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            Long offsetSecs = context.getValue(ChronoField.OFFSET_SECONDS);
            if (offsetSecs == null) {
                return false;
            }
            buf.append("GMT");
            int totalSecs = LocalDate$$ExternalSyntheticBackport0.m(offsetSecs.longValue());
            if (totalSecs != 0) {
                int absHours = Math.abs((totalSecs / 3600) % 100);
                int absMinutes = Math.abs((totalSecs / 60) % 60);
                int absSeconds = Math.abs(totalSecs % 60);
                buf.append(totalSecs < 0 ? "-" : "+");
                if (this.style == TextStyle.FULL) {
                    appendHMS(buf, absHours);
                    buf.append(':');
                    appendHMS(buf, absMinutes);
                    if (absSeconds != 0) {
                        buf.append(':');
                        appendHMS(buf, absSeconds);
                        return true;
                    }
                    return true;
                }
                if (absHours >= 10) {
                    buf.append((char) ((absHours / 10) + 48));
                }
                buf.append((char) ((absHours % 10) + 48));
                if (absMinutes != 0 || absSeconds != 0) {
                    buf.append(':');
                    appendHMS(buf, absMinutes);
                    if (absSeconds != 0) {
                        buf.append(':');
                        appendHMS(buf, absSeconds);
                        return true;
                    }
                    return true;
                }
                return true;
            }
            return true;
        }

        int getDigit(CharSequence text, int position) {
            char c = text.charAt(position);
            if (c < '0' || c > '9') {
                return -1;
            }
            return c - '0';
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            int negative;
            int pos;
            int pos2;
            int m1;
            int h;
            int end = position + text.length();
            if (!context.subSequenceEquals(text, position, "GMT", 0, "GMT".length())) {
                return position ^ (-1);
            }
            int pos3 = position + "GMT".length();
            if (pos3 == end) {
                return context.setParsedField(ChronoField.OFFSET_SECONDS, 0L, position, pos3);
            }
            char sign = text.charAt(pos3);
            if (sign == '+') {
                negative = 1;
            } else if (sign == '-') {
                negative = -1;
            } else {
                return context.setParsedField(ChronoField.OFFSET_SECONDS, 0L, position, pos3);
            }
            int pos4 = pos3 + 1;
            int m = 0;
            int s = 0;
            if (this.style == TextStyle.FULL) {
                int pos5 = pos4 + 1;
                int h1 = getDigit(text, pos4);
                int pos6 = pos5 + 1;
                int h2 = getDigit(text, pos5);
                if (h1 >= 0 && h2 >= 0) {
                    int pos7 = pos6 + 1;
                    if (text.charAt(pos6) == 58) {
                        int h3 = (h1 * 10) + h2;
                        int h4 = pos7 + 1;
                        int m12 = getDigit(text, pos7);
                        int pos8 = h4 + 1;
                        int m2 = getDigit(text, h4);
                        if (m12 < 0 || m2 < 0) {
                            return position ^ (-1);
                        }
                        int m3 = (m12 * 10) + m2;
                        if (pos8 + 2 < end && text.charAt(pos8) == ':') {
                            int s1 = getDigit(text, pos8 + 1);
                            int s2 = getDigit(text, pos8 + 2);
                            if (s1 >= 0 && s2 >= 0) {
                                s = (s1 * 10) + s2;
                                pos8 += 3;
                            }
                        }
                        m1 = s;
                        pos = pos8;
                        pos2 = h3;
                        h = m3;
                    }
                }
                return position ^ (-1);
            }
            int pos9 = pos4 + 1;
            int h5 = getDigit(text, pos4);
            if (h5 < 0) {
                return position ^ (-1);
            }
            if (pos9 >= end) {
                m1 = 0;
                pos = pos9;
                pos2 = h5;
                h = 0;
            } else {
                int h22 = getDigit(text, pos9);
                if (h22 >= 0) {
                    pos9++;
                    h5 = (h5 * 10) + h22;
                }
                int h6 = pos9 + 2;
                if (h6 < end && text.charAt(pos9) == ':' && pos9 + 2 < end && text.charAt(pos9) == ':') {
                    int m13 = getDigit(text, pos9 + 1);
                    int m22 = getDigit(text, pos9 + 2);
                    if (m13 >= 0 && m22 >= 0) {
                        m = (m13 * 10) + m22;
                        pos9 += 3;
                        if (pos9 + 2 < end && text.charAt(pos9) == ':') {
                            int s12 = getDigit(text, pos9 + 1);
                            int s22 = getDigit(text, pos9 + 2);
                            if (s12 >= 0 && s22 >= 0) {
                                m1 = (s12 * 10) + s22;
                                pos = pos9 + 3;
                                pos2 = h5;
                                h = m;
                            }
                        }
                    }
                }
                m1 = 0;
                pos = pos9;
                pos2 = h5;
                h = m;
            }
            long offsetSecs = negative * ((pos2 * 3600) + (h * 60) + m1);
            return context.setParsedField(ChronoField.OFFSET_SECONDS, offsetSecs, position, pos);
        }

        public String toString() {
            return "LocalizedOffset(" + this.style + ")";
        }
    }

    /* loaded from: classes2.dex */
    public static final class ZoneTextPrinterParser extends ZoneIdPrinterParser {
        private static final int DST = 1;
        private static final int GENERIC = 2;
        private static final int STD = 0;
        private static final Map<String, SoftReference<Map<Locale, String[]>>> cache = new ConcurrentHashMap();
        private final Map<Locale, Map.Entry<Integer, SoftReference<PrefixTree>>> cachedTree = new HashMap();
        private final Map<Locale, Map.Entry<Integer, SoftReference<PrefixTree>>> cachedTreeCI = new HashMap();
        private Set<String> preferredZones;
        private final TextStyle textStyle;

        /* JADX WARN: Illegal instructions before constructor call */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        ZoneTextPrinterParser(j$.time.format.TextStyle r5, java.util.Set<j$.time.ZoneId> r6) {
            /*
                r4 = this;
                j$.time.temporal.TemporalQuery r0 = j$.time.temporal.TemporalQueries.zone()
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "ZoneText("
                r1.append(r2)
                r1.append(r5)
                java.lang.String r2 = ")"
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r4.<init>(r0, r1)
                java.util.HashMap r0 = new java.util.HashMap
                r0.<init>()
                r4.cachedTree = r0
                java.util.HashMap r0 = new java.util.HashMap
                r0.<init>()
                r4.cachedTreeCI = r0
                java.lang.String r0 = "textStyle"
                java.lang.Object r0 = j$.util.Objects.requireNonNull(r5, r0)
                j$.time.format.TextStyle r0 = (j$.time.format.TextStyle) r0
                r4.textStyle = r0
                if (r6 == 0) goto L5e
                int r0 = r6.size()
                if (r0 == 0) goto L5e
                java.util.HashSet r0 = new java.util.HashSet
                r0.<init>()
                r4.preferredZones = r0
                java.util.Iterator r0 = r6.iterator()
            L48:
                boolean r1 = r0.hasNext()
                if (r1 == 0) goto L5e
                java.lang.Object r1 = r0.next()
                j$.time.ZoneId r1 = (j$.time.ZoneId) r1
                java.util.Set<java.lang.String> r2 = r4.preferredZones
                java.lang.String r3 = r1.getId()
                r2.add(r3)
                goto L48
            L5e:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.time.format.DateTimeFormatterBuilder.ZoneTextPrinterParser.<init>(j$.time.format.TextStyle, java.util.Set):void");
        }

        /* JADX WARN: Code restructure failed: missing block: B:10:0x0026, code lost:
            if (r6 == null) goto L11;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private java.lang.String getDisplayName(java.lang.String r12, int r13, java.util.Locale r14) {
            /*
                r11 = this;
                j$.time.format.TextStyle r0 = r11.textStyle
                j$.time.format.TextStyle r1 = j$.time.format.TextStyle.NARROW
                if (r0 != r1) goto L8
                r0 = 0
                return r0
            L8:
                java.util.Map<java.lang.String, java.lang.ref.SoftReference<java.util.Map<java.util.Locale, java.lang.String[]>>> r0 = j$.time.format.DateTimeFormatterBuilder.ZoneTextPrinterParser.cache
                java.lang.Object r1 = r0.get(r12)
                java.lang.ref.SoftReference r1 = (java.lang.ref.SoftReference) r1
                r2 = 0
                r3 = 5
                r4 = 3
                r5 = 1
                if (r1 == 0) goto L28
                java.lang.Object r6 = r1.get()
                java.util.Map r6 = (java.util.Map) r6
                r2 = r6
                if (r6 == 0) goto L28
                java.lang.Object r6 = r2.get(r14)
                java.lang.String[] r6 = (java.lang.String[]) r6
                r7 = r6
                if (r6 != 0) goto L64
            L28:
                java.util.TimeZone r6 = java.util.TimeZone.getTimeZone(r12)
                r7 = 7
                java.lang.String[] r7 = new java.lang.String[r7]
                r8 = 0
                r7[r8] = r12
                java.lang.String r9 = r6.getDisplayName(r8, r5, r14)
                r7[r5] = r9
                java.lang.String r9 = r6.getDisplayName(r8, r8, r14)
                r10 = 2
                r7[r10] = r9
                java.lang.String r9 = r6.getDisplayName(r5, r5, r14)
                r7[r4] = r9
                java.lang.String r8 = r6.getDisplayName(r5, r8, r14)
                r9 = 4
                r7[r9] = r8
                r7[r3] = r12
                r8 = 6
                r7[r8] = r12
                if (r2 != 0) goto L59
                j$.util.concurrent.ConcurrentHashMap r8 = new j$.util.concurrent.ConcurrentHashMap
                r8.<init>()
                r2 = r8
            L59:
                r2.put(r14, r7)
                java.lang.ref.SoftReference r8 = new java.lang.ref.SoftReference
                r8.<init>(r2)
                r0.put(r12, r8)
            L64:
                switch(r13) {
                    case 0: goto L7b;
                    case 1: goto L71;
                    default: goto L67;
                }
            L67:
                j$.time.format.TextStyle r0 = r11.textStyle
                int r0 = r0.zoneNameStyleIndex()
                int r0 = r0 + r3
                r0 = r7[r0]
                return r0
            L71:
                j$.time.format.TextStyle r0 = r11.textStyle
                int r0 = r0.zoneNameStyleIndex()
                int r0 = r0 + r4
                r0 = r7[r0]
                return r0
            L7b:
                j$.time.format.TextStyle r0 = r11.textStyle
                int r0 = r0.zoneNameStyleIndex()
                int r0 = r0 + r5
                r0 = r7[r0]
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.time.format.DateTimeFormatterBuilder.ZoneTextPrinterParser.getDisplayName(java.lang.String, int, java.util.Locale):java.lang.String");
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.ZoneIdPrinterParser, j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            ZoneId zone = (ZoneId) context.getValue(TemporalQueries.zoneId());
            int i = 0;
            if (zone == null) {
                return false;
            }
            String zname = zone.getId();
            if (!(zone instanceof ZoneOffset)) {
                TemporalAccessor dt = context.getTemporal();
                if (dt.isSupported(ChronoField.INSTANT_SECONDS)) {
                    if (zone.getRules().isDaylightSavings(Instant.from(dt))) {
                        i = 1;
                    }
                } else {
                    i = 2;
                }
                String name = getDisplayName(zname, i, context.getLocale());
                if (name != null) {
                    zname = name;
                }
            }
            buf.append(zname);
            return true;
        }

        /* JADX WARN: Code restructure failed: missing block: B:15:0x0049, code lost:
            if (r9 == null) goto L16;
         */
        @Override // j$.time.format.DateTimeFormatterBuilder.ZoneIdPrinterParser
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        protected j$.time.format.DateTimeFormatterBuilder.PrefixTree getTree(j$.time.format.DateTimeParseContext r18) {
            /*
                r17 = this;
                r0 = r17
                j$.time.format.TextStyle r1 = r0.textStyle
                j$.time.format.TextStyle r2 = j$.time.format.TextStyle.NARROW
                if (r1 != r2) goto Ld
                j$.time.format.DateTimeFormatterBuilder$PrefixTree r1 = super.getTree(r18)
                return r1
            Ld:
                java.util.Locale r1 = r18.getLocale()
                boolean r2 = r18.isCaseSensitive()
                java.util.Set r3 = j$.time.zone.ZoneRulesProvider.getAvailableZoneIds()
                int r4 = r3.size()
                if (r2 == 0) goto L22
                java.util.Map<java.util.Locale, java.util.Map$Entry<java.lang.Integer, java.lang.ref.SoftReference<j$.time.format.DateTimeFormatterBuilder$PrefixTree>>> r5 = r0.cachedTree
                goto L24
            L22:
                java.util.Map<java.util.Locale, java.util.Map$Entry<java.lang.Integer, java.lang.ref.SoftReference<j$.time.format.DateTimeFormatterBuilder$PrefixTree>>> r5 = r0.cachedTreeCI
            L24:
                r6 = 0
                r7 = 0
                r8 = 0
                java.lang.Object r9 = r5.get(r1)
                java.util.Map$Entry r9 = (java.util.Map.Entry) r9
                r6 = r9
                if (r9 == 0) goto L4b
                java.lang.Object r9 = r6.getKey()
                java.lang.Integer r9 = (java.lang.Integer) r9
                int r9 = r9.intValue()
                if (r9 != r4) goto L4b
                java.lang.Object r9 = r6.getValue()
                java.lang.ref.SoftReference r9 = (java.lang.ref.SoftReference) r9
                java.lang.Object r9 = r9.get()
                j$.time.format.DateTimeFormatterBuilder$PrefixTree r9 = (j$.time.format.DateTimeFormatterBuilder.PrefixTree) r9
                r7 = r9
                if (r9 != 0) goto Lc8
            L4b:
                j$.time.format.DateTimeFormatterBuilder$PrefixTree r7 = j$.time.format.DateTimeFormatterBuilder.PrefixTree.newTree(r18)
                java.text.DateFormatSymbols r9 = java.text.DateFormatSymbols.getInstance(r1)
                java.lang.String[][] r8 = r9.getZoneStrings()
                int r9 = r8.length
                r10 = 0
                r11 = 0
            L5a:
                if (r11 >= r9) goto L85
                r14 = r8[r11]
                r15 = r14[r10]
                boolean r16 = r3.contains(r15)
                if (r16 != 0) goto L67
                goto L82
            L67:
                r7.add(r15, r15)
                java.lang.String r15 = j$.time.format.ZoneName.toZid(r15, r1)
                j$.time.format.TextStyle r12 = r0.textStyle
                j$.time.format.TextStyle r13 = j$.time.format.TextStyle.FULL
                if (r12 != r13) goto L76
                r12 = 1
                goto L77
            L76:
                r12 = 2
            L77:
                int r13 = r14.length
                if (r12 >= r13) goto L82
                r13 = r14[r12]
                r7.add(r13, r15)
                int r12 = r12 + 2
                goto L77
            L82:
                int r11 = r11 + 1
                goto L5a
            L85:
                java.util.Set<java.lang.String> r9 = r0.preferredZones
                if (r9 == 0) goto Lb7
                int r9 = r8.length
                r11 = 0
            L8b:
                if (r11 >= r9) goto Lb7
                r12 = r8[r11]
                r13 = r12[r10]
                java.util.Set<java.lang.String> r14 = r0.preferredZones
                boolean r14 = r14.contains(r13)
                if (r14 == 0) goto Lb4
                boolean r14 = r3.contains(r13)
                if (r14 != 0) goto La0
                goto Lb4
            La0:
                j$.time.format.TextStyle r14 = r0.textStyle
                j$.time.format.TextStyle r15 = j$.time.format.TextStyle.FULL
                if (r14 != r15) goto La8
                r14 = 1
                goto La9
            La8:
                r14 = 2
            La9:
                int r15 = r12.length
                if (r14 >= r15) goto Lb4
                r15 = r12[r14]
                r7.add(r15, r13)
                int r14 = r14 + 2
                goto La9
            Lb4:
                int r11 = r11 + 1
                goto L8b
            Lb7:
                java.util.AbstractMap$SimpleImmutableEntry r9 = new java.util.AbstractMap$SimpleImmutableEntry
                java.lang.Integer r10 = java.lang.Integer.valueOf(r4)
                java.lang.ref.SoftReference r11 = new java.lang.ref.SoftReference
                r11.<init>(r7)
                r9.<init>(r10, r11)
                r5.put(r1, r9)
            Lc8:
                return r7
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.time.format.DateTimeFormatterBuilder.ZoneTextPrinterParser.getTree(j$.time.format.DateTimeParseContext):j$.time.format.DateTimeFormatterBuilder$PrefixTree");
        }
    }

    /* loaded from: classes2.dex */
    public static class ZoneIdPrinterParser implements DateTimePrinterParser {
        private static volatile Map.Entry<Integer, PrefixTree> cachedPrefixTree;
        private static volatile Map.Entry<Integer, PrefixTree> cachedPrefixTreeCI;
        private final String description;
        private final TemporalQuery<ZoneId> query;

        /* JADX WARN: Generic types in debug info not equals: j$.time.temporal.TemporalQuery != java.time.temporal.TemporalQuery<java.time.ZoneId> */
        ZoneIdPrinterParser(TemporalQuery<ZoneId> temporalQuery, String description) {
            this.query = temporalQuery;
            this.description = description;
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            ZoneId zone = (ZoneId) context.getValue(this.query);
            if (zone == null) {
                return false;
            }
            buf.append(zone.getId());
            return true;
        }

        protected PrefixTree getTree(DateTimeParseContext context) {
            Set<String> regionIds = ZoneRulesProvider.getAvailableZoneIds();
            int regionIdsSize = regionIds.size();
            AbstractMap.SimpleImmutableEntry simpleImmutableEntry = context.isCaseSensitive() ? cachedPrefixTree : cachedPrefixTreeCI;
            if (simpleImmutableEntry == null || simpleImmutableEntry.getKey().intValue() != regionIdsSize) {
                synchronized (this) {
                    simpleImmutableEntry = context.isCaseSensitive() ? cachedPrefixTree : cachedPrefixTreeCI;
                    if (simpleImmutableEntry == null || simpleImmutableEntry.getKey().intValue() != regionIdsSize) {
                        simpleImmutableEntry = new AbstractMap.SimpleImmutableEntry(Integer.valueOf(regionIdsSize), PrefixTree.newTree(regionIds, context));
                        if (context.isCaseSensitive()) {
                            cachedPrefixTree = simpleImmutableEntry;
                        } else {
                            cachedPrefixTreeCI = simpleImmutableEntry;
                        }
                    }
                }
            }
            return simpleImmutableEntry.getValue();
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            int length = text.length();
            if (position > length) {
                throw new IndexOutOfBoundsException();
            }
            if (position == length) {
                return position ^ (-1);
            }
            char nextChar = text.charAt(position);
            if (nextChar == '+' || nextChar == '-') {
                return parseOffsetBased(context, text, position, position, OffsetIdPrinterParser.INSTANCE_ID_Z);
            }
            if (length >= position + 2) {
                char nextNextChar = text.charAt(position + 1);
                if (context.charEquals(nextChar, 'U') && context.charEquals(nextNextChar, 'T')) {
                    if (length >= position + 3 && context.charEquals(text.charAt(position + 2), 'C')) {
                        return parseOffsetBased(context, text, position, position + 3, OffsetIdPrinterParser.INSTANCE_ID_ZERO);
                    }
                    return parseOffsetBased(context, text, position, position + 2, OffsetIdPrinterParser.INSTANCE_ID_ZERO);
                } else if (context.charEquals(nextChar, 'G') && length >= position + 3 && context.charEquals(nextNextChar, 'M') && context.charEquals(text.charAt(position + 2), 'T')) {
                    return parseOffsetBased(context, text, position, position + 3, OffsetIdPrinterParser.INSTANCE_ID_ZERO);
                }
            }
            PrefixTree tree = getTree(context);
            ParsePosition ppos = new ParsePosition(position);
            String parsedZoneId = tree.match(text, ppos);
            if (parsedZoneId == null) {
                if (context.charEquals(nextChar, 'Z')) {
                    context.setParsed(ZoneOffset.UTC);
                    return position + 1;
                }
                return position ^ (-1);
            }
            context.setParsed(ZoneId.of(parsedZoneId));
            return ppos.getIndex();
        }

        private int parseOffsetBased(DateTimeParseContext context, CharSequence text, int prefixPos, int position, OffsetIdPrinterParser parser) {
            String prefix = text.toString().substring(prefixPos, position).toUpperCase();
            if (position >= text.length()) {
                context.setParsed(ZoneId.of(prefix));
                return position;
            } else if (text.charAt(position) == '0' || context.charEquals(text.charAt(position), 'Z')) {
                context.setParsed(ZoneId.of(prefix));
                return position;
            } else {
                DateTimeParseContext newContext = context.copy();
                int endPos = parser.parse(newContext, text, position);
                try {
                    if (endPos < 0) {
                        if (parser == OffsetIdPrinterParser.INSTANCE_ID_Z) {
                            return prefixPos ^ (-1);
                        }
                        context.setParsed(ZoneId.of(prefix));
                        return position;
                    }
                    int offset = (int) newContext.getParsed(ChronoField.OFFSET_SECONDS).longValue();
                    ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(offset);
                    context.setParsed(ZoneId.ofOffset(prefix, zoneOffset));
                    return endPos;
                } catch (DateTimeException e) {
                    return prefixPos ^ (-1);
                }
            }
        }

        public String toString() {
            return this.description;
        }
    }

    /* loaded from: classes2.dex */
    public static class PrefixTree {
        protected char c0;
        protected PrefixTree child;
        protected String key;
        protected PrefixTree sibling;
        protected String value;

        private PrefixTree(String k, String v, PrefixTree child) {
            this.key = k;
            this.value = v;
            this.child = child;
            if (k.length() == 0) {
                this.c0 = (char) 65535;
            } else {
                this.c0 = this.key.charAt(0);
            }
        }

        public static PrefixTree newTree(DateTimeParseContext context) {
            if (context.isCaseSensitive()) {
                return new PrefixTree("", null, null);
            }
            return new CI("", null, null);
        }

        public static PrefixTree newTree(Set<String> keys, DateTimeParseContext context) {
            PrefixTree tree = newTree(context);
            for (String k : keys) {
                tree.add0(k, k);
            }
            return tree;
        }

        public PrefixTree copyTree() {
            PrefixTree copy = new PrefixTree(this.key, this.value, null);
            PrefixTree prefixTree = this.child;
            if (prefixTree != null) {
                copy.child = prefixTree.copyTree();
            }
            PrefixTree prefixTree2 = this.sibling;
            if (prefixTree2 != null) {
                copy.sibling = prefixTree2.copyTree();
            }
            return copy;
        }

        public boolean add(String k, String v) {
            return add0(k, v);
        }

        private boolean add0(String k, String v) {
            String k2 = toKey(k);
            int prefixLen = prefixLength(k2);
            if (prefixLen == this.key.length()) {
                if (prefixLen < k2.length()) {
                    String subKey = k2.substring(prefixLen);
                    for (PrefixTree c = this.child; c != null; c = c.sibling) {
                        if (isEqual(c.c0, subKey.charAt(0))) {
                            return c.add0(subKey, v);
                        }
                    }
                    PrefixTree c2 = newNode(subKey, v, null);
                    c2.sibling = this.child;
                    this.child = c2;
                    return true;
                }
                this.value = v;
                return true;
            }
            PrefixTree n1 = newNode(this.key.substring(prefixLen), this.value, this.child);
            this.key = k2.substring(0, prefixLen);
            this.child = n1;
            if (prefixLen < k2.length()) {
                PrefixTree n2 = newNode(k2.substring(prefixLen), v, null);
                this.child.sibling = n2;
                this.value = null;
            } else {
                this.value = v;
            }
            return true;
        }

        public String match(CharSequence text, int off, int end) {
            int off2;
            if (!prefixOf(text, off, end)) {
                return null;
            }
            if (this.child != null && (off2 = this.key.length() + off) != end) {
                PrefixTree c = this.child;
                while (!isEqual(c.c0, text.charAt(off2))) {
                    c = c.sibling;
                    if (c == null) {
                        return this.value;
                    }
                }
                String found = c.match(text, off2, end);
                if (found != null) {
                    return found;
                }
                return this.value;
            }
            return this.value;
        }

        public String match(CharSequence text, ParsePosition pos) {
            int off = pos.getIndex();
            int end = text.length();
            if (!prefixOf(text, off, end)) {
                return null;
            }
            int off2 = off + this.key.length();
            if (this.child != null && off2 != end) {
                PrefixTree c = this.child;
                while (true) {
                    if (isEqual(c.c0, text.charAt(off2))) {
                        pos.setIndex(off2);
                        String found = c.match(text, pos);
                        if (found != null) {
                            return found;
                        }
                    } else {
                        c = c.sibling;
                        if (c == null) {
                            break;
                        }
                    }
                }
            }
            pos.setIndex(off2);
            return this.value;
        }

        protected String toKey(String k) {
            return k;
        }

        protected PrefixTree newNode(String k, String v, PrefixTree child) {
            return new PrefixTree(k, v, child);
        }

        protected boolean isEqual(char c1, char c2) {
            return c1 == c2;
        }

        protected boolean prefixOf(CharSequence text, int off, int end) {
            if (text instanceof String) {
                return ((String) text).startsWith(this.key, off);
            }
            int off2 = this.key.length();
            if (off2 > end - off) {
                return false;
            }
            int len = 0;
            while (true) {
                int len2 = off2 - 1;
                if (off2 > 0) {
                    int off0 = len + 1;
                    char charAt = this.key.charAt(len);
                    int off3 = off + 1;
                    if (!isEqual(charAt, text.charAt(off))) {
                        return false;
                    }
                    len = off0;
                    off = off3;
                    off2 = len2;
                } else {
                    return true;
                }
            }
        }

        private int prefixLength(String k) {
            int off = 0;
            while (off < k.length() && off < this.key.length()) {
                if (!isEqual(k.charAt(off), this.key.charAt(off))) {
                    return off;
                }
                off++;
            }
            return off;
        }

        /* loaded from: classes2.dex */
        public static class CI extends PrefixTree {
            private CI(String k, String v, PrefixTree child) {
                super(k, v, child);
            }

            @Override // j$.time.format.DateTimeFormatterBuilder.PrefixTree
            public CI newNode(String k, String v, PrefixTree child) {
                return new CI(k, v, child);
            }

            @Override // j$.time.format.DateTimeFormatterBuilder.PrefixTree
            protected boolean isEqual(char c1, char c2) {
                return DateTimeParseContext.charEqualsIgnoreCase(c1, c2);
            }

            @Override // j$.time.format.DateTimeFormatterBuilder.PrefixTree
            protected boolean prefixOf(CharSequence text, int off, int end) {
                int off2 = this.key.length();
                if (off2 > end - off) {
                    return false;
                }
                int len = 0;
                while (true) {
                    int len2 = off2 - 1;
                    if (off2 > 0) {
                        int off0 = len + 1;
                        char charAt = this.key.charAt(len);
                        int off3 = off + 1;
                        if (!isEqual(charAt, text.charAt(off))) {
                            return false;
                        }
                        len = off0;
                        off = off3;
                        off2 = len2;
                    } else {
                        return true;
                    }
                }
            }
        }

        /* loaded from: classes2.dex */
        private static class LENIENT extends CI {
            private LENIENT(String k, String v, PrefixTree child) {
                super(k, v, child);
            }

            @Override // j$.time.format.DateTimeFormatterBuilder.PrefixTree.CI, j$.time.format.DateTimeFormatterBuilder.PrefixTree
            public CI newNode(String k, String v, PrefixTree child) {
                return new LENIENT(k, v, child);
            }

            private boolean isLenientChar(char c) {
                return c == ' ' || c == '_' || c == '/';
            }

            @Override // j$.time.format.DateTimeFormatterBuilder.PrefixTree
            protected String toKey(String k) {
                int i = 0;
                while (i < k.length()) {
                    if (!isLenientChar(k.charAt(i))) {
                        i++;
                    } else {
                        StringBuilder sb = new StringBuilder(k.length());
                        sb.append((CharSequence) k, 0, i);
                        while (true) {
                            i++;
                            if (i < k.length()) {
                                if (!isLenientChar(k.charAt(i))) {
                                    sb.append(k.charAt(i));
                                }
                            } else {
                                return sb.toString();
                            }
                        }
                    }
                }
                return k;
            }

            @Override // j$.time.format.DateTimeFormatterBuilder.PrefixTree
            public String match(CharSequence text, ParsePosition pos) {
                int off = pos.getIndex();
                int end = text.length();
                int len = this.key.length();
                int koff = 0;
                while (koff < len && off < end) {
                    if (isLenientChar(text.charAt(off))) {
                        off++;
                    } else {
                        int koff2 = koff + 1;
                        int off2 = off + 1;
                        if (!isEqual(this.key.charAt(koff), text.charAt(off))) {
                            return null;
                        }
                        koff = koff2;
                        off = off2;
                    }
                }
                if (koff != len) {
                    return null;
                }
                if (this.child != null && off != end) {
                    int off0 = off;
                    while (off0 < end && isLenientChar(text.charAt(off0))) {
                        off0++;
                    }
                    if (off0 < end) {
                        PrefixTree c = this.child;
                        while (true) {
                            if (isEqual(c.c0, text.charAt(off0))) {
                                pos.setIndex(off0);
                                String found = c.match(text, pos);
                                if (found != null) {
                                    return found;
                                }
                            } else {
                                c = c.sibling;
                                if (c == null) {
                                    break;
                                }
                            }
                        }
                    }
                }
                pos.setIndex(off);
                return this.value;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static final class ChronoPrinterParser implements DateTimePrinterParser {
        private final TextStyle textStyle;

        ChronoPrinterParser(TextStyle textStyle) {
            this.textStyle = textStyle;
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            Chronology chrono = (Chronology) context.getValue(TemporalQueries.chronology());
            if (chrono == null) {
                return false;
            }
            if (this.textStyle == null) {
                buf.append(chrono.getId());
                return true;
            }
            buf.append(getChronologyName(chrono, context.getLocale()));
            return true;
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            String name;
            if (position < 0 || position > text.length()) {
                throw new IndexOutOfBoundsException();
            }
            Chronology bestMatch = null;
            int matchLen = -1;
            for (Chronology chrono : Chronology.CC.getAvailableChronologies()) {
                if (this.textStyle == null) {
                    name = chrono.getId();
                } else {
                    name = getChronologyName(chrono, context.getLocale());
                }
                int nameLen = name.length();
                if (nameLen > matchLen && context.subSequenceEquals(text, position, name, 0, nameLen)) {
                    bestMatch = chrono;
                    matchLen = nameLen;
                }
            }
            if (bestMatch == null) {
                return position ^ (-1);
            }
            context.setParsed(bestMatch);
            return position + matchLen;
        }

        private String getChronologyName(Chronology chrono, Locale locale) {
            return chrono.getId();
        }
    }

    /* loaded from: classes2.dex */
    public static final class LocalizedPrinterParser implements DateTimePrinterParser {
        private static final ConcurrentMap<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap(16, 0.75f, 2);
        private final FormatStyle dateStyle;
        private final FormatStyle timeStyle;

        LocalizedPrinterParser(FormatStyle dateStyle, FormatStyle timeStyle) {
            this.dateStyle = dateStyle;
            this.timeStyle = timeStyle;
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            Chronology chrono = Chronology.CC.from(context.getTemporal());
            return formatter(context.getLocale(), chrono).toPrinterParser(false).format(context, buf);
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            Chronology chrono = context.getEffectiveChronology();
            return formatter(context.getLocale(), chrono).toPrinterParser(false).parse(context, text, position);
        }

        private DateTimeFormatter formatter(Locale locale, Chronology chrono) {
            String key = chrono.getId() + '|' + locale.toString() + '|' + this.dateStyle + this.timeStyle;
            ConcurrentMap<String, DateTimeFormatter> concurrentMap = FORMATTER_CACHE;
            DateTimeFormatter formatter = concurrentMap.get(key);
            if (formatter == null) {
                String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(this.dateStyle, this.timeStyle, chrono, locale);
                DateTimeFormatter formatter2 = new DateTimeFormatterBuilder().appendPattern(pattern).toFormatter(locale);
                DateTimeFormatter old = concurrentMap.putIfAbsent(key, formatter2);
                if (old != null) {
                    return old;
                }
                return formatter2;
            }
            return formatter;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Localized(");
            Object obj = this.dateStyle;
            Object obj2 = "";
            if (obj == null) {
                obj = obj2;
            }
            sb.append(obj);
            sb.append(",");
            FormatStyle formatStyle = this.timeStyle;
            if (formatStyle != null) {
                obj2 = formatStyle;
            }
            sb.append(obj2);
            sb.append(")");
            return sb.toString();
        }
    }

    /* loaded from: classes2.dex */
    public static final class WeekBasedFieldPrinterParser implements DateTimePrinterParser {
        private char chr;
        private int count;

        WeekBasedFieldPrinterParser(char chr, int count) {
            this.chr = chr;
            this.count = count;
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public boolean format(DateTimePrintContext context, StringBuilder buf) {
            return printerParser(context.getLocale()).format(context, buf);
        }

        @Override // j$.time.format.DateTimeFormatterBuilder.DateTimePrinterParser
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            return printerParser(context.getLocale()).parse(context, text, position);
        }

        private DateTimePrinterParser printerParser(Locale locale) {
            TemporalField field;
            WeekFields weekDef = WeekFields.of(locale);
            switch (this.chr) {
                case 'W':
                    field = weekDef.weekOfMonth();
                    break;
                case TsExtractor.TS_STREAM_TYPE_DVBSUBS /* 89 */:
                    TemporalField field2 = weekDef.weekBasedYear();
                    if (this.count == 2) {
                        return new ReducedPrinterParser(field2, 2, 2, 0, ReducedPrinterParser.BASE_DATE, 0);
                    }
                    int i = this.count;
                    return new NumberPrinterParser(field2, i, 19, i < 4 ? SignStyle.NORMAL : SignStyle.EXCEEDS_PAD, -1);
                case 'c':
                case 'e':
                    field = weekDef.dayOfWeek();
                    break;
                case 'w':
                    field = weekDef.weekOfWeekBasedYear();
                    break;
                default:
                    throw new IllegalStateException("unreachable");
            }
            return new NumberPrinterParser(field, this.count == 2 ? 2 : 1, 2, SignStyle.NOT_NEGATIVE);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(30);
            sb.append("Localized(");
            char c = this.chr;
            if (c == 'Y') {
                int i = this.count;
                if (i == 1) {
                    sb.append("WeekBasedYear");
                } else if (i == 2) {
                    sb.append("ReducedValue(WeekBasedYear,2,2,2000-01-01)");
                } else {
                    sb.append("WeekBasedYear,");
                    sb.append(this.count);
                    sb.append(",");
                    sb.append(19);
                    sb.append(",");
                    sb.append(this.count < 4 ? SignStyle.NORMAL : SignStyle.EXCEEDS_PAD);
                }
            } else {
                switch (c) {
                    case 'W':
                        sb.append("WeekOfMonth");
                        break;
                    case 'c':
                    case 'e':
                        sb.append("DayOfWeek");
                        break;
                    case 'w':
                        sb.append("WeekOfWeekBasedYear");
                        break;
                }
                sb.append(",");
                sb.append(this.count);
            }
            sb.append(")");
            return sb.toString();
        }
    }
}
