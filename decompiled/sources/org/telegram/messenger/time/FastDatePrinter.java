package org.telegram.messenger.time;

import com.google.android.gms.location.LocationRequest;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentMap;
import org.telegram.ui.Components.UndoView;
/* loaded from: classes4.dex */
public class FastDatePrinter implements DatePrinter, Serializable {
    public static final int FULL = 0;
    public static final int LONG = 1;
    public static final int MEDIUM = 2;
    public static final int SHORT = 3;
    private static final ConcurrentMap<TimeZoneDisplayKey, String> cTimeZoneDisplayCache = new ConcurrentHashMap(7);
    private static final long serialVersionUID = 1;
    private final Locale mLocale;
    private transient int mMaxLengthEstimate;
    private final String mPattern;
    private transient Rule[] mRules;
    private final TimeZone mTimeZone;

    /* loaded from: classes4.dex */
    public interface NumberRule extends Rule {
        void appendTo(StringBuffer stringBuffer, int i);
    }

    /* loaded from: classes4.dex */
    public interface Rule {
        void appendTo(StringBuffer stringBuffer, Calendar calendar);

        int estimateLength();
    }

    public FastDatePrinter(String pattern, TimeZone timeZone, Locale locale) {
        this.mPattern = pattern;
        this.mTimeZone = timeZone;
        this.mLocale = locale;
        init();
    }

    private void init() {
        List<Rule> rulesList = parsePattern();
        Rule[] ruleArr = (Rule[]) rulesList.toArray(new Rule[rulesList.size()]);
        this.mRules = ruleArr;
        int len = 0;
        int i = ruleArr.length;
        while (true) {
            i--;
            if (i >= 0) {
                len += this.mRules[i].estimateLength();
            } else {
                this.mMaxLengthEstimate = len;
                return;
            }
        }
    }

    protected List<Rule> parsePattern() {
        String[] weekdays;
        DateFormatSymbols symbols;
        Rule rule;
        DateFormatSymbols symbols2 = new DateFormatSymbols(this.mLocale);
        List<Rule> rules = new ArrayList<>();
        String[] ERAs = symbols2.getEras();
        String[] months = symbols2.getMonths();
        String[] shortMonths = symbols2.getShortMonths();
        String[] weekdays2 = symbols2.getWeekdays();
        String[] shortWeekdays = symbols2.getShortWeekdays();
        String[] AmPmStrings = symbols2.getAmPmStrings();
        int length = this.mPattern.length();
        int i = 1;
        int[] indexRef = new int[1];
        int i2 = 0;
        while (i2 < length) {
            indexRef[0] = i2;
            String token = parseToken(this.mPattern, indexRef);
            int i3 = indexRef[0];
            int tokenLen = token.length();
            if (tokenLen != 0) {
                char c = token.charAt(0);
                int i4 = 4;
                switch (c) {
                    case '\'':
                        symbols = symbols2;
                        weekdays = weekdays2;
                        String sub = token.substring(1);
                        if (sub.length() == 1) {
                            rule = new CharacterLiteral(sub.charAt(0));
                            break;
                        } else {
                            rule = new StringLiteral(sub);
                            break;
                        }
                    case 'D':
                        symbols = symbols2;
                        weekdays = weekdays2;
                        Rule rule2 = selectNumberRule(6, tokenLen);
                        rule = rule2;
                        break;
                    case 'E':
                        symbols = symbols2;
                        weekdays = weekdays2;
                        Rule rule3 = new TextField(7, tokenLen < 4 ? shortWeekdays : weekdays);
                        rule = rule3;
                        break;
                    case UndoView.ACTION_AUTO_DELETE_ON /* 70 */:
                        symbols = symbols2;
                        weekdays = weekdays2;
                        Rule rule4 = selectNumberRule(8, tokenLen);
                        rule = rule4;
                        break;
                    case 'G':
                        symbols = symbols2;
                        weekdays = weekdays2;
                        Rule rule5 = new TextField(0, ERAs);
                        rule = rule5;
                        break;
                    case 'H':
                        symbols = symbols2;
                        weekdays = weekdays2;
                        Rule rule6 = selectNumberRule(11, tokenLen);
                        rule = rule6;
                        break;
                    case UndoView.ACTION_GIGAGROUP_CANCEL /* 75 */:
                        symbols = symbols2;
                        weekdays = weekdays2;
                        Rule rule7 = selectNumberRule(10, tokenLen);
                        rule = rule7;
                        break;
                    case UndoView.ACTION_GIGAGROUP_SUCCESS /* 76 */:
                        symbols = symbols2;
                        weekdays = weekdays2;
                        if (tokenLen < 4) {
                            if (tokenLen == 3) {
                                rule = new TextField(2, shortMonths);
                                break;
                            } else if (tokenLen == 2) {
                                rule = TwoDigitMonthField.INSTANCE;
                                break;
                            } else {
                                Rule rule8 = UnpaddedMonthField.INSTANCE;
                                rule = rule8;
                                break;
                            }
                        } else {
                            rule = new TextField(2, months);
                            break;
                        }
                    case UndoView.ACTION_PAYMENT_SUCCESS /* 77 */:
                        symbols = symbols2;
                        weekdays = weekdays2;
                        if (tokenLen < 4) {
                            if (tokenLen == 3) {
                                rule = new TextField(2, shortMonths);
                                break;
                            } else if (tokenLen == 2) {
                                rule = TwoDigitMonthField.INSTANCE;
                                break;
                            } else {
                                Rule rule9 = UnpaddedMonthField.INSTANCE;
                                rule = rule9;
                                break;
                            }
                        } else {
                            rule = new TextField(2, months);
                            break;
                        }
                    case 'S':
                        symbols = symbols2;
                        weekdays = weekdays2;
                        Rule rule10 = selectNumberRule(14, tokenLen);
                        rule = rule10;
                        break;
                    case 'W':
                        symbols = symbols2;
                        weekdays = weekdays2;
                        Rule rule11 = selectNumberRule(4, tokenLen);
                        rule = rule11;
                        break;
                    case 'Z':
                        symbols = symbols2;
                        weekdays = weekdays2;
                        if (tokenLen == 1) {
                            rule = TimeZoneNumberRule.INSTANCE_NO_COLON;
                            break;
                        } else {
                            Rule rule12 = TimeZoneNumberRule.INSTANCE_COLON;
                            rule = rule12;
                            break;
                        }
                    case 'a':
                        symbols = symbols2;
                        weekdays = weekdays2;
                        Rule rule13 = new TextField(9, AmPmStrings);
                        rule = rule13;
                        break;
                    case 'd':
                        symbols = symbols2;
                        weekdays = weekdays2;
                        Rule rule14 = selectNumberRule(5, tokenLen);
                        rule = rule14;
                        break;
                    case LocationRequest.PRIORITY_LOW_POWER /* 104 */:
                        symbols = symbols2;
                        weekdays = weekdays2;
                        Rule rule15 = new TwelveHourField(selectNumberRule(10, tokenLen));
                        rule = rule15;
                        break;
                    case 'k':
                        symbols = symbols2;
                        weekdays = weekdays2;
                        Rule rule16 = new TwentyFourHourField(selectNumberRule(11, tokenLen));
                        rule = rule16;
                        break;
                    case 'm':
                        symbols = symbols2;
                        weekdays = weekdays2;
                        Rule rule17 = selectNumberRule(12, tokenLen);
                        rule = rule17;
                        break;
                    case 's':
                        symbols = symbols2;
                        weekdays = weekdays2;
                        Rule rule18 = selectNumberRule(13, tokenLen);
                        rule = rule18;
                        break;
                    case 'w':
                        symbols = symbols2;
                        weekdays = weekdays2;
                        Rule rule19 = selectNumberRule(3, tokenLen);
                        rule = rule19;
                        break;
                    case 'y':
                        symbols = symbols2;
                        weekdays = weekdays2;
                        if (tokenLen == 2) {
                            rule = TwoDigitYearField.INSTANCE;
                            break;
                        } else {
                            if (tokenLen >= 4) {
                                i4 = tokenLen;
                            }
                            Rule rule20 = selectNumberRule(1, i4);
                            rule = rule20;
                            break;
                        }
                    case 'z':
                        if (tokenLen >= 4) {
                            symbols = symbols2;
                            weekdays = weekdays2;
                            rule = new TimeZoneNameRule(this.mTimeZone, this.mLocale, i);
                            break;
                        } else {
                            symbols = symbols2;
                            weekdays = weekdays2;
                            Rule rule21 = new TimeZoneNameRule(this.mTimeZone, this.mLocale, 0);
                            rule = rule21;
                            break;
                        }
                    default:
                        throw new IllegalArgumentException("Illegal pattern component: " + token);
                }
                rules.add(rule);
                i2 = i3 + 1;
                symbols2 = symbols;
                weekdays2 = weekdays;
                i = 1;
            } else {
                return rules;
            }
        }
        return rules;
    }

    protected String parseToken(String pattern, int[] indexRef) {
        StringBuilder buf = new StringBuilder();
        int i = indexRef[0];
        int length = pattern.length();
        char c = pattern.charAt(i);
        if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
            buf.append(c);
            while (i + 1 < length) {
                char peek = pattern.charAt(i + 1);
                if (peek != c) {
                    break;
                }
                buf.append(c);
                i++;
            }
        } else {
            buf.append('\'');
            boolean inLiteral = false;
            while (i < length) {
                char c2 = pattern.charAt(i);
                if (c2 == '\'') {
                    if (i + 1 < length && pattern.charAt(i + 1) == '\'') {
                        i++;
                        buf.append(c2);
                    } else {
                        inLiteral = !inLiteral;
                    }
                } else if (!inLiteral && ((c2 >= 'A' && c2 <= 'Z') || (c2 >= 'a' && c2 <= 'z'))) {
                    i--;
                    break;
                } else {
                    buf.append(c2);
                }
                i++;
            }
        }
        indexRef[0] = i;
        return buf.toString();
    }

    protected NumberRule selectNumberRule(int field, int padding) {
        switch (padding) {
            case 1:
                return new UnpaddedNumberField(field);
            case 2:
                return new TwoDigitNumberField(field);
            default:
                return new PaddedNumberField(field, padding);
        }
    }

    @Override // org.telegram.messenger.time.DatePrinter
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        if (obj instanceof Date) {
            return format((Date) obj, toAppendTo);
        }
        if (obj instanceof Calendar) {
            return format((Calendar) obj, toAppendTo);
        }
        if (obj instanceof Long) {
            return format(((Long) obj).longValue(), toAppendTo);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unknown class: ");
        sb.append(obj == null ? "<null>" : obj.getClass().getName());
        throw new IllegalArgumentException(sb.toString());
    }

    @Override // org.telegram.messenger.time.DatePrinter
    public String format(long millis) {
        Calendar c = newCalendar();
        c.setTimeInMillis(millis);
        return applyRulesToString(c);
    }

    private String applyRulesToString(Calendar c) {
        return applyRules(c, new StringBuffer(this.mMaxLengthEstimate)).toString();
    }

    private GregorianCalendar newCalendar() {
        return new GregorianCalendar(this.mTimeZone, this.mLocale);
    }

    @Override // org.telegram.messenger.time.DatePrinter
    public String format(Date date) {
        Calendar c = newCalendar();
        c.setTime(date);
        return applyRulesToString(c);
    }

    @Override // org.telegram.messenger.time.DatePrinter
    public String format(Calendar calendar) {
        return format(calendar, new StringBuffer(this.mMaxLengthEstimate)).toString();
    }

    @Override // org.telegram.messenger.time.DatePrinter
    public StringBuffer format(long millis, StringBuffer buf) {
        return format(new Date(millis), buf);
    }

    @Override // org.telegram.messenger.time.DatePrinter
    public StringBuffer format(Date date, StringBuffer buf) {
        Calendar c = newCalendar();
        c.setTime(date);
        return applyRules(c, buf);
    }

    @Override // org.telegram.messenger.time.DatePrinter
    public StringBuffer format(Calendar calendar, StringBuffer buf) {
        return applyRules(calendar, buf);
    }

    public StringBuffer applyRules(Calendar calendar, StringBuffer buf) {
        Rule[] ruleArr;
        for (Rule rule : this.mRules) {
            rule.appendTo(buf, calendar);
        }
        return buf;
    }

    @Override // org.telegram.messenger.time.DatePrinter
    public String getPattern() {
        return this.mPattern;
    }

    @Override // org.telegram.messenger.time.DatePrinter
    public TimeZone getTimeZone() {
        return this.mTimeZone;
    }

    @Override // org.telegram.messenger.time.DatePrinter
    public Locale getLocale() {
        return this.mLocale;
    }

    public int getMaxLengthEstimate() {
        return this.mMaxLengthEstimate;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FastDatePrinter)) {
            return false;
        }
        FastDatePrinter other = (FastDatePrinter) obj;
        return this.mPattern.equals(other.mPattern) && this.mTimeZone.equals(other.mTimeZone) && this.mLocale.equals(other.mLocale);
    }

    public int hashCode() {
        return this.mPattern.hashCode() + ((this.mTimeZone.hashCode() + (this.mLocale.hashCode() * 13)) * 13);
    }

    public String toString() {
        return "FastDatePrinter[" + this.mPattern + "," + this.mLocale + "," + this.mTimeZone.getID() + "]";
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        init();
    }

    /* loaded from: classes4.dex */
    public static class CharacterLiteral implements Rule {
        private final char mValue;

        CharacterLiteral(char value) {
            this.mValue = value;
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public int estimateLength() {
            return 1;
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public void appendTo(StringBuffer buffer, Calendar calendar) {
            buffer.append(this.mValue);
        }
    }

    /* loaded from: classes4.dex */
    public static class StringLiteral implements Rule {
        private final String mValue;

        StringLiteral(String value) {
            this.mValue = value;
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public int estimateLength() {
            return this.mValue.length();
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public void appendTo(StringBuffer buffer, Calendar calendar) {
            buffer.append(this.mValue);
        }
    }

    /* loaded from: classes4.dex */
    public static class TextField implements Rule {
        private final int mField;
        private final String[] mValues;

        TextField(int field, String[] values) {
            this.mField = field;
            this.mValues = values;
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public int estimateLength() {
            int max = 0;
            int i = this.mValues.length;
            while (true) {
                i--;
                if (i >= 0) {
                    int len = this.mValues[i].length();
                    if (len > max) {
                        max = len;
                    }
                } else {
                    return max;
                }
            }
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public void appendTo(StringBuffer buffer, Calendar calendar) {
            buffer.append(this.mValues[calendar.get(this.mField)]);
        }
    }

    /* loaded from: classes4.dex */
    public static class UnpaddedNumberField implements NumberRule {
        private final int mField;

        UnpaddedNumberField(int field) {
            this.mField = field;
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public int estimateLength() {
            return 4;
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public void appendTo(StringBuffer buffer, Calendar calendar) {
            appendTo(buffer, calendar.get(this.mField));
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.NumberRule
        public final void appendTo(StringBuffer buffer, int value) {
            if (value < 10) {
                buffer.append((char) (value + 48));
            } else if (value < 100) {
                buffer.append((char) ((value / 10) + 48));
                buffer.append((char) ((value % 10) + 48));
            } else {
                buffer.append(Integer.toString(value));
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class UnpaddedMonthField implements NumberRule {
        static final UnpaddedMonthField INSTANCE = new UnpaddedMonthField();

        UnpaddedMonthField() {
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public int estimateLength() {
            return 2;
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public void appendTo(StringBuffer buffer, Calendar calendar) {
            appendTo(buffer, calendar.get(2) + 1);
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.NumberRule
        public final void appendTo(StringBuffer buffer, int value) {
            if (value < 10) {
                buffer.append((char) (value + 48));
                return;
            }
            buffer.append((char) ((value / 10) + 48));
            buffer.append((char) ((value % 10) + 48));
        }
    }

    /* loaded from: classes4.dex */
    public static class PaddedNumberField implements NumberRule {
        private final int mField;
        private final int mSize;

        PaddedNumberField(int field, int size) {
            if (size < 3) {
                throw new IllegalArgumentException();
            }
            this.mField = field;
            this.mSize = size;
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public int estimateLength() {
            return 4;
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public void appendTo(StringBuffer buffer, Calendar calendar) {
            appendTo(buffer, calendar.get(this.mField));
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.NumberRule
        public final void appendTo(StringBuffer buffer, int value) {
            int digits;
            if (value < 100) {
                int i = this.mSize;
                while (true) {
                    i--;
                    if (i >= 2) {
                        buffer.append('0');
                    } else {
                        int i2 = value / 10;
                        buffer.append((char) (i2 + 48));
                        buffer.append((char) ((value % 10) + 48));
                        return;
                    }
                }
            } else {
                if (value < 1000) {
                    digits = 3;
                } else {
                    digits = Integer.toString(value).length();
                }
                int i3 = this.mSize;
                while (true) {
                    i3--;
                    if (i3 >= digits) {
                        buffer.append('0');
                    } else {
                        buffer.append(Integer.toString(value));
                        return;
                    }
                }
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class TwoDigitNumberField implements NumberRule {
        private final int mField;

        TwoDigitNumberField(int field) {
            this.mField = field;
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public int estimateLength() {
            return 2;
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public void appendTo(StringBuffer buffer, Calendar calendar) {
            appendTo(buffer, calendar.get(this.mField));
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.NumberRule
        public final void appendTo(StringBuffer buffer, int value) {
            if (value < 100) {
                buffer.append((char) ((value / 10) + 48));
                buffer.append((char) ((value % 10) + 48));
                return;
            }
            buffer.append(Integer.toString(value));
        }
    }

    /* loaded from: classes4.dex */
    public static class TwoDigitYearField implements NumberRule {
        static final TwoDigitYearField INSTANCE = new TwoDigitYearField();

        TwoDigitYearField() {
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public int estimateLength() {
            return 2;
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public void appendTo(StringBuffer buffer, Calendar calendar) {
            appendTo(buffer, calendar.get(1) % 100);
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.NumberRule
        public final void appendTo(StringBuffer buffer, int value) {
            buffer.append((char) ((value / 10) + 48));
            buffer.append((char) ((value % 10) + 48));
        }
    }

    /* loaded from: classes4.dex */
    public static class TwoDigitMonthField implements NumberRule {
        static final TwoDigitMonthField INSTANCE = new TwoDigitMonthField();

        TwoDigitMonthField() {
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public int estimateLength() {
            return 2;
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public void appendTo(StringBuffer buffer, Calendar calendar) {
            appendTo(buffer, calendar.get(2) + 1);
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.NumberRule
        public final void appendTo(StringBuffer buffer, int value) {
            buffer.append((char) ((value / 10) + 48));
            buffer.append((char) ((value % 10) + 48));
        }
    }

    /* loaded from: classes4.dex */
    public static class TwelveHourField implements NumberRule {
        private final NumberRule mRule;

        TwelveHourField(NumberRule rule) {
            this.mRule = rule;
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public void appendTo(StringBuffer buffer, Calendar calendar) {
            int value = calendar.get(10);
            if (value == 0) {
                value = calendar.getLeastMaximum(10) + 1;
            }
            this.mRule.appendTo(buffer, value);
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.NumberRule
        public void appendTo(StringBuffer buffer, int value) {
            this.mRule.appendTo(buffer, value);
        }
    }

    /* loaded from: classes4.dex */
    public static class TwentyFourHourField implements NumberRule {
        private final NumberRule mRule;

        TwentyFourHourField(NumberRule rule) {
            this.mRule = rule;
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public void appendTo(StringBuffer buffer, Calendar calendar) {
            int value = calendar.get(11);
            if (value == 0) {
                value = calendar.getMaximum(11) + 1;
            }
            this.mRule.appendTo(buffer, value);
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.NumberRule
        public void appendTo(StringBuffer buffer, int value) {
            this.mRule.appendTo(buffer, value);
        }
    }

    static String getTimeZoneDisplay(TimeZone tz, boolean daylight, int style, Locale locale) {
        TimeZoneDisplayKey key = new TimeZoneDisplayKey(tz, daylight, style, locale);
        ConcurrentMap<TimeZoneDisplayKey, String> concurrentMap = cTimeZoneDisplayCache;
        String value = concurrentMap.get(key);
        if (value == null) {
            String value2 = tz.getDisplayName(daylight, style, locale);
            String prior = concurrentMap.putIfAbsent(key, value2);
            if (prior != null) {
                return prior;
            }
            return value2;
        }
        return value;
    }

    /* loaded from: classes4.dex */
    public static class TimeZoneNameRule implements Rule {
        private final String mDaylight;
        private final Locale mLocale;
        private final String mStandard;
        private final int mStyle;

        TimeZoneNameRule(TimeZone timeZone, Locale locale, int style) {
            this.mLocale = locale;
            this.mStyle = style;
            this.mStandard = FastDatePrinter.getTimeZoneDisplay(timeZone, false, style, locale);
            this.mDaylight = FastDatePrinter.getTimeZoneDisplay(timeZone, true, style, locale);
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public int estimateLength() {
            return Math.max(this.mStandard.length(), this.mDaylight.length());
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public void appendTo(StringBuffer buffer, Calendar calendar) {
            TimeZone zone = calendar.getTimeZone();
            if (zone.useDaylightTime() && calendar.get(16) != 0) {
                buffer.append(FastDatePrinter.getTimeZoneDisplay(zone, true, this.mStyle, this.mLocale));
            } else {
                buffer.append(FastDatePrinter.getTimeZoneDisplay(zone, false, this.mStyle, this.mLocale));
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class TimeZoneNumberRule implements Rule {
        static final TimeZoneNumberRule INSTANCE_COLON = new TimeZoneNumberRule(true);
        static final TimeZoneNumberRule INSTANCE_NO_COLON = new TimeZoneNumberRule(false);
        final boolean mColon;

        TimeZoneNumberRule(boolean colon) {
            this.mColon = colon;
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public int estimateLength() {
            return 5;
        }

        @Override // org.telegram.messenger.time.FastDatePrinter.Rule
        public void appendTo(StringBuffer buffer, Calendar calendar) {
            int offset = calendar.get(15) + calendar.get(16);
            if (offset < 0) {
                buffer.append('-');
                offset = -offset;
            } else {
                buffer.append('+');
            }
            int hours = offset / 3600000;
            buffer.append((char) ((hours / 10) + 48));
            buffer.append((char) ((hours % 10) + 48));
            if (this.mColon) {
                buffer.append(':');
            }
            int minutes = (offset / 60000) - (hours * 60);
            buffer.append((char) ((minutes / 10) + 48));
            buffer.append((char) ((minutes % 10) + 48));
        }
    }

    /* loaded from: classes4.dex */
    public static class TimeZoneDisplayKey {
        private final Locale mLocale;
        private final int mStyle;
        private final TimeZone mTimeZone;

        TimeZoneDisplayKey(TimeZone timeZone, boolean daylight, int style, Locale locale) {
            this.mTimeZone = timeZone;
            if (daylight) {
                this.mStyle = Integer.MIN_VALUE | style;
            } else {
                this.mStyle = style;
            }
            this.mLocale = locale;
        }

        public int hashCode() {
            return (((this.mStyle * 31) + this.mLocale.hashCode()) * 31) + this.mTimeZone.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof TimeZoneDisplayKey)) {
                return false;
            }
            TimeZoneDisplayKey other = (TimeZoneDisplayKey) obj;
            return this.mTimeZone.equals(other.mTimeZone) && this.mStyle == other.mStyle && this.mLocale.equals(other.mLocale);
        }
    }
}
