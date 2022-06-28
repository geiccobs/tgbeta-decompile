package j$.time.temporal;

import j$.time.DayOfWeek;
import j$.time.LocalDate;
import j$.util.Objects;
import j$.util.function.UnaryOperator;
/* loaded from: classes2.dex */
public final class TemporalAdjusters {
    private TemporalAdjusters() {
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.UnaryOperator != java.util.function.UnaryOperator<java.time.LocalDate> */
    public static TemporalAdjuster ofDateAdjuster(final UnaryOperator<LocalDate> unaryOperator) {
        Objects.requireNonNull(unaryOperator, "dateBasedAdjuster");
        return new TemporalAdjuster() { // from class: j$.time.temporal.TemporalAdjusters$$ExternalSyntheticLambda6
            @Override // j$.time.temporal.TemporalAdjuster
            public final Temporal adjustInto(Temporal temporal) {
                return TemporalAdjusters.lambda$ofDateAdjuster$0(UnaryOperator.this, temporal);
            }
        };
    }

    public static /* synthetic */ Temporal lambda$ofDateAdjuster$0(UnaryOperator dateBasedAdjuster, Temporal temporal) {
        LocalDate input = LocalDate.from(temporal);
        LocalDate output = (LocalDate) dateBasedAdjuster.apply(input);
        return temporal.with(output);
    }

    public static TemporalAdjuster firstDayOfMonth() {
        return TemporalAdjusters$$ExternalSyntheticLambda7.INSTANCE;
    }

    public static TemporalAdjuster lastDayOfMonth() {
        return TemporalAdjusters$$ExternalSyntheticLambda11.INSTANCE;
    }

    public static TemporalAdjuster firstDayOfNextMonth() {
        return TemporalAdjusters$$ExternalSyntheticLambda8.INSTANCE;
    }

    public static TemporalAdjuster firstDayOfYear() {
        return TemporalAdjusters$$ExternalSyntheticLambda10.INSTANCE;
    }

    public static TemporalAdjuster lastDayOfYear() {
        return TemporalAdjusters$$ExternalSyntheticLambda12.INSTANCE;
    }

    public static TemporalAdjuster firstDayOfNextYear() {
        return TemporalAdjusters$$ExternalSyntheticLambda9.INSTANCE;
    }

    public static TemporalAdjuster firstInMonth(DayOfWeek dayOfWeek) {
        return dayOfWeekInMonth(1, dayOfWeek);
    }

    public static TemporalAdjuster lastInMonth(DayOfWeek dayOfWeek) {
        return dayOfWeekInMonth(-1, dayOfWeek);
    }

    public static TemporalAdjuster dayOfWeekInMonth(final int ordinal, DayOfWeek dayOfWeek) {
        Objects.requireNonNull(dayOfWeek, "dayOfWeek");
        final int dowValue = dayOfWeek.getValue();
        if (ordinal >= 0) {
            return new TemporalAdjuster() { // from class: j$.time.temporal.TemporalAdjusters$$ExternalSyntheticLambda4
                @Override // j$.time.temporal.TemporalAdjuster
                public final Temporal adjustInto(Temporal temporal) {
                    return TemporalAdjusters.lambda$dayOfWeekInMonth$7(dowValue, ordinal, temporal);
                }
            };
        }
        return new TemporalAdjuster() { // from class: j$.time.temporal.TemporalAdjusters$$ExternalSyntheticLambda5
            @Override // j$.time.temporal.TemporalAdjuster
            public final Temporal adjustInto(Temporal temporal) {
                return TemporalAdjusters.lambda$dayOfWeekInMonth$8(dowValue, ordinal, temporal);
            }
        };
    }

    public static /* synthetic */ Temporal lambda$dayOfWeekInMonth$7(int dowValue, int ordinal, Temporal temporal) {
        Temporal temp = temporal.with(ChronoField.DAY_OF_MONTH, 1L);
        int curDow = temp.get(ChronoField.DAY_OF_WEEK);
        int dowDiff = ((dowValue - curDow) + 7) % 7;
        return temp.plus((int) (dowDiff + ((ordinal - 1) * 7)), ChronoUnit.DAYS);
    }

    public static /* synthetic */ Temporal lambda$dayOfWeekInMonth$8(int dowValue, int ordinal, Temporal temporal) {
        Temporal temp = temporal.with(ChronoField.DAY_OF_MONTH, temporal.range(ChronoField.DAY_OF_MONTH).getMaximum());
        int curDow = temp.get(ChronoField.DAY_OF_WEEK);
        int daysDiff = dowValue - curDow;
        return temp.plus((int) ((daysDiff == 0 ? 0 : daysDiff > 0 ? daysDiff - 7 : daysDiff) - (((-ordinal) - 1) * 7)), ChronoUnit.DAYS);
    }

    public static TemporalAdjuster next(DayOfWeek dayOfWeek) {
        final int dowValue = dayOfWeek.getValue();
        return new TemporalAdjuster() { // from class: j$.time.temporal.TemporalAdjusters$$ExternalSyntheticLambda0
            @Override // j$.time.temporal.TemporalAdjuster
            public final Temporal adjustInto(Temporal temporal) {
                return TemporalAdjusters.lambda$next$9(dowValue, temporal);
            }
        };
    }

    public static /* synthetic */ Temporal lambda$next$9(int dowValue, Temporal temporal) {
        int calDow = temporal.get(ChronoField.DAY_OF_WEEK);
        int daysDiff = calDow - dowValue;
        return temporal.plus(daysDiff >= 0 ? 7 - daysDiff : -daysDiff, ChronoUnit.DAYS);
    }

    public static TemporalAdjuster nextOrSame(DayOfWeek dayOfWeek) {
        final int dowValue = dayOfWeek.getValue();
        return new TemporalAdjuster() { // from class: j$.time.temporal.TemporalAdjusters$$ExternalSyntheticLambda1
            @Override // j$.time.temporal.TemporalAdjuster
            public final Temporal adjustInto(Temporal temporal) {
                return TemporalAdjusters.lambda$nextOrSame$10(dowValue, temporal);
            }
        };
    }

    public static /* synthetic */ Temporal lambda$nextOrSame$10(int dowValue, Temporal temporal) {
        int calDow = temporal.get(ChronoField.DAY_OF_WEEK);
        if (calDow == dowValue) {
            return temporal;
        }
        int daysDiff = calDow - dowValue;
        return temporal.plus(daysDiff >= 0 ? 7 - daysDiff : -daysDiff, ChronoUnit.DAYS);
    }

    public static TemporalAdjuster previous(DayOfWeek dayOfWeek) {
        final int dowValue = dayOfWeek.getValue();
        return new TemporalAdjuster() { // from class: j$.time.temporal.TemporalAdjusters$$ExternalSyntheticLambda2
            @Override // j$.time.temporal.TemporalAdjuster
            public final Temporal adjustInto(Temporal temporal) {
                return TemporalAdjusters.lambda$previous$11(dowValue, temporal);
            }
        };
    }

    public static /* synthetic */ Temporal lambda$previous$11(int dowValue, Temporal temporal) {
        int calDow = temporal.get(ChronoField.DAY_OF_WEEK);
        int daysDiff = dowValue - calDow;
        return temporal.minus(daysDiff >= 0 ? 7 - daysDiff : -daysDiff, ChronoUnit.DAYS);
    }

    public static TemporalAdjuster previousOrSame(DayOfWeek dayOfWeek) {
        final int dowValue = dayOfWeek.getValue();
        return new TemporalAdjuster() { // from class: j$.time.temporal.TemporalAdjusters$$ExternalSyntheticLambda3
            @Override // j$.time.temporal.TemporalAdjuster
            public final Temporal adjustInto(Temporal temporal) {
                return TemporalAdjusters.lambda$previousOrSame$12(dowValue, temporal);
            }
        };
    }

    public static /* synthetic */ Temporal lambda$previousOrSame$12(int dowValue, Temporal temporal) {
        int calDow = temporal.get(ChronoField.DAY_OF_WEEK);
        if (calDow == dowValue) {
            return temporal;
        }
        int daysDiff = dowValue - calDow;
        return temporal.minus(daysDiff >= 0 ? 7 - daysDiff : -daysDiff, ChronoUnit.DAYS);
    }
}
