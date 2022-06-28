package j$.time.format;

import com.google.android.gms.actions.SearchIntents;
import j$.time.DateTimeException;
import j$.time.Period;
import j$.time.ZoneId;
import j$.time.chrono.Chronology;
import j$.time.chrono.IsoChronology;
import j$.time.format.DateTimeFormatterBuilder;
import j$.time.temporal.ChronoField;
import j$.time.temporal.IsoFields;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQuery;
import j$.util.Objects;
import java.io.IOException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
/* loaded from: classes2.dex */
public final class DateTimeFormatter {
    public static final DateTimeFormatter ISO_DATE;
    public static final DateTimeFormatter ISO_DATE_TIME;
    public static final DateTimeFormatter ISO_LOCAL_DATE;
    public static final DateTimeFormatter ISO_LOCAL_DATE_TIME;
    public static final DateTimeFormatter ISO_LOCAL_TIME;
    public static final DateTimeFormatter ISO_OFFSET_DATE;
    public static final DateTimeFormatter ISO_OFFSET_DATE_TIME;
    public static final DateTimeFormatter ISO_OFFSET_TIME;
    public static final DateTimeFormatter ISO_TIME;
    public static final DateTimeFormatter ISO_ZONED_DATE_TIME;
    public static final DateTimeFormatter RFC_1123_DATE_TIME;
    private final Chronology chrono;
    private final DecimalStyle decimalStyle;
    private final Locale locale;
    private final DateTimeFormatterBuilder.CompositePrinterParser printerParser;
    private final Set<TemporalField> resolverFields;
    private final ResolverStyle resolverStyle;
    private final ZoneId zone;
    public static final DateTimeFormatter ISO_ORDINAL_DATE = new DateTimeFormatterBuilder().parseCaseInsensitive().appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.DAY_OF_YEAR, 3).optionalStart().appendOffsetId().toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
    public static final DateTimeFormatter ISO_WEEK_DATE = new DateTimeFormatterBuilder().parseCaseInsensitive().appendValue(IsoFields.WEEK_BASED_YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral("-W").appendValue(IsoFields.WEEK_OF_WEEK_BASED_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_WEEK, 1).optionalStart().appendOffsetId().toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
    public static final DateTimeFormatter ISO_INSTANT = new DateTimeFormatterBuilder().parseCaseInsensitive().appendInstant().toFormatter(ResolverStyle.STRICT, null);
    public static final DateTimeFormatter BASIC_ISO_DATE = new DateTimeFormatterBuilder().parseCaseInsensitive().appendValue(ChronoField.YEAR, 4).appendValue(ChronoField.MONTH_OF_YEAR, 2).appendValue(ChronoField.DAY_OF_MONTH, 2).optionalStart().appendOffset("+HHMMss", "Z").toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
    private static final TemporalQuery<Period> PARSED_EXCESS_DAYS = DateTimeFormatter$$ExternalSyntheticLambda0.INSTANCE;
    private static final TemporalQuery<Boolean> PARSED_LEAP_SECOND = DateTimeFormatter$$ExternalSyntheticLambda1.INSTANCE;

    public static DateTimeFormatter ofPattern(String pattern) {
        return new DateTimeFormatterBuilder().appendPattern(pattern).toFormatter();
    }

    public static DateTimeFormatter ofPattern(String pattern, Locale locale) {
        return new DateTimeFormatterBuilder().appendPattern(pattern).toFormatter(locale);
    }

    public static DateTimeFormatter ofLocalizedDate(FormatStyle dateStyle) {
        Objects.requireNonNull(dateStyle, "dateStyle");
        return new DateTimeFormatterBuilder().appendLocalized(dateStyle, null).toFormatter(ResolverStyle.SMART, IsoChronology.INSTANCE);
    }

    public static DateTimeFormatter ofLocalizedTime(FormatStyle timeStyle) {
        Objects.requireNonNull(timeStyle, "timeStyle");
        return new DateTimeFormatterBuilder().appendLocalized(null, timeStyle).toFormatter(ResolverStyle.SMART, IsoChronology.INSTANCE);
    }

    public static DateTimeFormatter ofLocalizedDateTime(FormatStyle dateTimeStyle) {
        Objects.requireNonNull(dateTimeStyle, "dateTimeStyle");
        return new DateTimeFormatterBuilder().appendLocalized(dateTimeStyle, dateTimeStyle).toFormatter(ResolverStyle.SMART, IsoChronology.INSTANCE);
    }

    public static DateTimeFormatter ofLocalizedDateTime(FormatStyle dateStyle, FormatStyle timeStyle) {
        Objects.requireNonNull(dateStyle, "dateStyle");
        Objects.requireNonNull(timeStyle, "timeStyle");
        return new DateTimeFormatterBuilder().appendLocalized(dateStyle, timeStyle).toFormatter(ResolverStyle.SMART, IsoChronology.INSTANCE);
    }

    static {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
        ISO_LOCAL_DATE = formatter;
        ISO_OFFSET_DATE = new DateTimeFormatterBuilder().parseCaseInsensitive().append(formatter).appendOffsetId().toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
        ISO_DATE = new DateTimeFormatterBuilder().parseCaseInsensitive().append(formatter).optionalStart().appendOffsetId().toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
        DateTimeFormatter formatter2 = new DateTimeFormatterBuilder().appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR, 2).optionalStart().appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE, 2).optionalStart().appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter(ResolverStyle.STRICT, null);
        ISO_LOCAL_TIME = formatter2;
        ISO_OFFSET_TIME = new DateTimeFormatterBuilder().parseCaseInsensitive().append(formatter2).appendOffsetId().toFormatter(ResolverStyle.STRICT, null);
        ISO_TIME = new DateTimeFormatterBuilder().parseCaseInsensitive().append(formatter2).optionalStart().appendOffsetId().toFormatter(ResolverStyle.STRICT, null);
        DateTimeFormatter formatter3 = new DateTimeFormatterBuilder().parseCaseInsensitive().append(formatter).appendLiteral('T').append(formatter2).toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
        ISO_LOCAL_DATE_TIME = formatter3;
        DateTimeFormatter formatter4 = new DateTimeFormatterBuilder().parseCaseInsensitive().append(formatter3).appendOffsetId().toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
        ISO_OFFSET_DATE_TIME = formatter4;
        ISO_ZONED_DATE_TIME = new DateTimeFormatterBuilder().append(formatter4).optionalStart().appendLiteral('[').parseCaseSensitive().appendZoneRegionId().appendLiteral(']').toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
        ISO_DATE_TIME = new DateTimeFormatterBuilder().append(formatter3).optionalStart().appendOffsetId().optionalStart().appendLiteral('[').parseCaseSensitive().appendZoneRegionId().appendLiteral(']').toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
        Map<Long, String> dow = new HashMap<>();
        dow.put(1L, "Mon");
        dow.put(2L, "Tue");
        dow.put(3L, "Wed");
        dow.put(4L, "Thu");
        dow.put(5L, "Fri");
        dow.put(6L, "Sat");
        dow.put(7L, "Sun");
        Map<Long, String> moy = new HashMap<>();
        moy.put(1L, "Jan");
        moy.put(2L, "Feb");
        moy.put(3L, "Mar");
        moy.put(4L, "Apr");
        moy.put(5L, "May");
        moy.put(6L, "Jun");
        moy.put(7L, "Jul");
        moy.put(8L, "Aug");
        moy.put(9L, "Sep");
        moy.put(10L, "Oct");
        moy.put(11L, "Nov");
        moy.put(12L, "Dec");
        RFC_1123_DATE_TIME = new DateTimeFormatterBuilder().parseCaseInsensitive().parseLenient().optionalStart().appendText(ChronoField.DAY_OF_WEEK, dow).appendLiteral(", ").optionalEnd().appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE).appendLiteral(' ').appendText(ChronoField.MONTH_OF_YEAR, moy).appendLiteral(' ').appendValue(ChronoField.YEAR, 4).appendLiteral(' ').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR, 2).optionalStart().appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE, 2).optionalEnd().appendLiteral(' ').appendOffset("+HHMM", "GMT").toFormatter(ResolverStyle.SMART, IsoChronology.INSTANCE);
    }

    public static final TemporalQuery<Period> parsedExcessDays() {
        return PARSED_EXCESS_DAYS;
    }

    public static /* synthetic */ Period lambda$static$0(TemporalAccessor t) {
        if (t instanceof Parsed) {
            return ((Parsed) t).excessDays;
        }
        return Period.ZERO;
    }

    public static final TemporalQuery<Boolean> parsedLeapSecond() {
        return PARSED_LEAP_SECOND;
    }

    public static /* synthetic */ Boolean lambda$static$1(TemporalAccessor t) {
        if (t instanceof Parsed) {
            return Boolean.valueOf(((Parsed) t).leapSecond);
        }
        return Boolean.FALSE;
    }

    public DateTimeFormatter(DateTimeFormatterBuilder.CompositePrinterParser printerParser, Locale locale, DecimalStyle decimalStyle, ResolverStyle resolverStyle, Set<TemporalField> set, Chronology chrono, ZoneId zone) {
        this.printerParser = (DateTimeFormatterBuilder.CompositePrinterParser) Objects.requireNonNull(printerParser, "printerParser");
        this.resolverFields = set;
        this.locale = (Locale) Objects.requireNonNull(locale, "locale");
        this.decimalStyle = (DecimalStyle) Objects.requireNonNull(decimalStyle, "decimalStyle");
        this.resolverStyle = (ResolverStyle) Objects.requireNonNull(resolverStyle, "resolverStyle");
        this.chrono = chrono;
        this.zone = zone;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public DateTimeFormatter withLocale(Locale locale) {
        if (this.locale.equals(locale)) {
            return this;
        }
        return new DateTimeFormatter(this.printerParser, locale, this.decimalStyle, this.resolverStyle, this.resolverFields, this.chrono, this.zone);
    }

    public DecimalStyle getDecimalStyle() {
        return this.decimalStyle;
    }

    public DateTimeFormatter withDecimalStyle(DecimalStyle decimalStyle) {
        if (this.decimalStyle.equals(decimalStyle)) {
            return this;
        }
        return new DateTimeFormatter(this.printerParser, this.locale, decimalStyle, this.resolverStyle, this.resolverFields, this.chrono, this.zone);
    }

    public Chronology getChronology() {
        return this.chrono;
    }

    public DateTimeFormatter withChronology(Chronology chrono) {
        if (Objects.equals(this.chrono, chrono)) {
            return this;
        }
        return new DateTimeFormatter(this.printerParser, this.locale, this.decimalStyle, this.resolverStyle, this.resolverFields, chrono, this.zone);
    }

    public ZoneId getZone() {
        return this.zone;
    }

    public DateTimeFormatter withZone(ZoneId zone) {
        if (Objects.equals(this.zone, zone)) {
            return this;
        }
        return new DateTimeFormatter(this.printerParser, this.locale, this.decimalStyle, this.resolverStyle, this.resolverFields, this.chrono, zone);
    }

    public ResolverStyle getResolverStyle() {
        return this.resolverStyle;
    }

    public DateTimeFormatter withResolverStyle(ResolverStyle resolverStyle) {
        Objects.requireNonNull(resolverStyle, "resolverStyle");
        if (Objects.equals(this.resolverStyle, resolverStyle)) {
            return this;
        }
        return new DateTimeFormatter(this.printerParser, this.locale, this.decimalStyle, resolverStyle, this.resolverFields, this.chrono, this.zone);
    }

    public Set<TemporalField> getResolverFields() {
        return this.resolverFields;
    }

    public DateTimeFormatter withResolverFields(TemporalField... resolverFields) {
        Set<java.time.temporal.TemporalField> fields = null;
        if (resolverFields != null) {
            fields = Collections.unmodifiableSet(new HashSet(Arrays.asList(resolverFields)));
        }
        if (Objects.equals(this.resolverFields, fields)) {
            return this;
        }
        return new DateTimeFormatter(this.printerParser, this.locale, this.decimalStyle, this.resolverStyle, fields, this.chrono, this.zone);
    }

    public DateTimeFormatter withResolverFields(Set<TemporalField> set) {
        if (Objects.equals(this.resolverFields, set)) {
            return this;
        }
        if (set != null) {
            set = Collections.unmodifiableSet(new HashSet(set));
        }
        return new DateTimeFormatter(this.printerParser, this.locale, this.decimalStyle, this.resolverStyle, set, this.chrono, this.zone);
    }

    public String format(TemporalAccessor temporal) {
        StringBuilder buf = new StringBuilder(32);
        formatTo(temporal, buf);
        return buf.toString();
    }

    public void formatTo(TemporalAccessor temporal, Appendable appendable) {
        Objects.requireNonNull(temporal, "temporal");
        Objects.requireNonNull(appendable, "appendable");
        try {
            DateTimePrintContext context = new DateTimePrintContext(temporal, this);
            if (appendable instanceof StringBuilder) {
                this.printerParser.format(context, (StringBuilder) appendable);
                return;
            }
            StringBuilder buf = new StringBuilder(32);
            this.printerParser.format(context, buf);
            appendable.append(buf);
        } catch (IOException ex) {
            throw new DateTimeException(ex.getMessage(), ex);
        }
    }

    public TemporalAccessor parse(CharSequence text) {
        Objects.requireNonNull(text, "text");
        try {
            return parseResolved0(text, null);
        } catch (DateTimeParseException ex) {
            throw ex;
        } catch (RuntimeException ex2) {
            throw createError(text, ex2);
        }
    }

    public TemporalAccessor parse(CharSequence text, ParsePosition position) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(position, "position");
        try {
            return parseResolved0(text, position);
        } catch (DateTimeParseException ex) {
            throw ex;
        } catch (IndexOutOfBoundsException ex2) {
            throw ex2;
        } catch (RuntimeException ex3) {
            throw createError(text, ex3);
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.temporal.TemporalQuery != java.time.temporal.TemporalQuery<T> */
    public <T> T parse(CharSequence text, TemporalQuery<T> temporalQuery) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(temporalQuery, SearchIntents.EXTRA_QUERY);
        try {
            return (T) parseResolved0(text, null).query(temporalQuery);
        } catch (DateTimeParseException ex) {
            throw ex;
        } catch (RuntimeException ex2) {
            throw createError(text, ex2);
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.temporal.TemporalQuery != java.time.temporal.TemporalQuery<?> */
    /* JADX WARN: Generic types in debug info not equals: j$.time.temporal.TemporalQuery[] != java.time.temporal.TemporalQuery<?>[] */
    public TemporalAccessor parseBest(CharSequence text, TemporalQuery<?>... temporalQueryArr) {
        DateTimeParseException ex;
        DateTimeFormatter dateTimeFormatter;
        RuntimeException ex2;
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(temporalQueryArr, "queries");
        if (temporalQueryArr.length < 2) {
            throw new IllegalArgumentException("At least two queries must be specified");
        }
        try {
            TemporalAccessor resolved = parseResolved0(text, null);
            for (TemporalQuery<?> temporalQuery : temporalQueryArr) {
                try {
                    try {
                        return (TemporalAccessor) resolved.query(temporalQuery);
                    } catch (RuntimeException e) {
                    }
                } catch (DateTimeParseException e2) {
                    ex = e2;
                    throw ex;
                } catch (RuntimeException e3) {
                    ex2 = e3;
                    dateTimeFormatter = this;
                    throw dateTimeFormatter.createError(text, ex2);
                }
            }
            try {
                throw new DateTimeException("Unable to convert parsed text using any of the specified queries");
            } catch (DateTimeParseException e4) {
                ex = e4;
                throw ex;
            } catch (RuntimeException e5) {
                ex2 = e5;
                dateTimeFormatter = this;
                throw dateTimeFormatter.createError(text, ex2);
            }
        } catch (DateTimeParseException e6) {
            ex = e6;
        } catch (RuntimeException e7) {
            ex2 = e7;
            dateTimeFormatter = this;
        }
    }

    private DateTimeParseException createError(CharSequence text, RuntimeException ex) {
        String abbr;
        if (text.length() > 64) {
            abbr = text.subSequence(0, 64).toString() + "...";
        } else {
            abbr = text.toString();
        }
        return new DateTimeParseException("Text '" + abbr + "' could not be parsed: " + ex.getMessage(), text, 0, ex);
    }

    public TemporalAccessor parseResolved0(CharSequence text, ParsePosition position) {
        String abbr;
        ParsePosition pos = position != null ? position : new ParsePosition(0);
        DateTimeParseContext context = parseUnresolved0(text, pos);
        if (context == null || pos.getErrorIndex() >= 0 || (position == null && pos.getIndex() < text.length())) {
            if (text.length() > 64) {
                abbr = text.subSequence(0, 64).toString() + "...";
            } else {
                abbr = text.toString();
            }
            if (pos.getErrorIndex() >= 0) {
                throw new DateTimeParseException("Text '" + abbr + "' could not be parsed at index " + pos.getErrorIndex(), text, pos.getErrorIndex());
            }
            throw new DateTimeParseException("Text '" + abbr + "' could not be parsed, unparsed text found at index " + pos.getIndex(), text, pos.getIndex());
        }
        return context.toResolved(this.resolverStyle, this.resolverFields);
    }

    public TemporalAccessor parseUnresolved(CharSequence text, ParsePosition position) {
        DateTimeParseContext context = parseUnresolved0(text, position);
        if (context == null) {
            return null;
        }
        return context.toUnresolved();
    }

    public DateTimeParseContext parseUnresolved0(CharSequence text, ParsePosition position) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(position, "position");
        DateTimeParseContext context = new DateTimeParseContext(this);
        int pos = this.printerParser.parse(context, text, position.getIndex());
        if (pos < 0) {
            position.setErrorIndex(pos ^ (-1));
            return null;
        }
        position.setIndex(pos);
        return context;
    }

    public DateTimeFormatterBuilder.CompositePrinterParser toPrinterParser(boolean optional) {
        return this.printerParser.withOptional(optional);
    }

    public Format toFormat() {
        return new ClassicFormat(this, null);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.temporal.TemporalQuery != java.time.temporal.TemporalQuery<?> */
    public Format toFormat(TemporalQuery<?> temporalQuery) {
        Objects.requireNonNull(temporalQuery, "parseQuery");
        return new ClassicFormat(this, temporalQuery);
    }

    public String toString() {
        String pattern = this.printerParser.toString();
        return pattern.startsWith("[") ? pattern : pattern.substring(1, pattern.length() - 1);
    }

    /* loaded from: classes2.dex */
    static class ClassicFormat extends Format {
        private final DateTimeFormatter formatter;
        private final TemporalQuery<?> parseType;

        /* JADX WARN: Generic types in debug info not equals: j$.time.temporal.TemporalQuery != java.time.temporal.TemporalQuery<?> */
        public ClassicFormat(DateTimeFormatter formatter, TemporalQuery<?> temporalQuery) {
            this.formatter = formatter;
            this.parseType = temporalQuery;
        }

        @Override // java.text.Format
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            Objects.requireNonNull(obj, "obj");
            Objects.requireNonNull(toAppendTo, "toAppendTo");
            Objects.requireNonNull(pos, "pos");
            if (!(obj instanceof TemporalAccessor)) {
                throw new IllegalArgumentException("Format target must implement TemporalAccessor");
            }
            pos.setBeginIndex(0);
            pos.setEndIndex(0);
            try {
                this.formatter.formatTo((TemporalAccessor) obj, toAppendTo);
                return toAppendTo;
            } catch (RuntimeException ex) {
                throw new IllegalArgumentException(ex.getMessage(), ex);
            }
        }

        @Override // java.text.Format
        public Object parseObject(String text) {
            Objects.requireNonNull(text, "text");
            try {
                TemporalQuery<?> temporalQuery = this.parseType;
                if (temporalQuery == null) {
                    return this.formatter.parseResolved0(text, null);
                }
                return this.formatter.parse(text, temporalQuery);
            } catch (DateTimeParseException ex) {
                throw new ParseException(ex.getMessage(), ex.getErrorIndex());
            } catch (RuntimeException ex2) {
                throw ((ParseException) new ParseException(ex2.getMessage(), 0).initCause(ex2));
            }
        }

        @Override // java.text.Format
        public Object parseObject(String text, ParsePosition pos) {
            Objects.requireNonNull(text, "text");
            try {
                DateTimeParseContext context = this.formatter.parseUnresolved0(text, pos);
                if (context != null) {
                    try {
                        TemporalAccessor resolved = context.toResolved(this.formatter.resolverStyle, this.formatter.resolverFields);
                        TemporalQuery<?> temporalQuery = this.parseType;
                        if (temporalQuery == null) {
                            return resolved;
                        }
                        return resolved.query(temporalQuery);
                    } catch (RuntimeException e) {
                        pos.setErrorIndex(0);
                        return null;
                    }
                }
                if (pos.getErrorIndex() < 0) {
                    pos.setErrorIndex(0);
                }
                return null;
            } catch (IndexOutOfBoundsException e2) {
                if (pos.getErrorIndex() < 0) {
                    pos.setErrorIndex(0);
                }
                return null;
            }
        }
    }
}
