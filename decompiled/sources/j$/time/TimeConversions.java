package j$.time;
/* loaded from: classes2.dex */
public class TimeConversions {
    private TimeConversions() {
    }

    public static java.time.ZonedDateTime convert(ZonedDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return java.time.ZonedDateTime.of(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(), dateTime.getHour(), dateTime.getMinute(), dateTime.getSecond(), dateTime.getNano(), convert(dateTime.getZone()));
    }

    public static ZonedDateTime convert(java.time.ZonedDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return ZonedDateTime.of(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(), dateTime.getHour(), dateTime.getMinute(), dateTime.getSecond(), dateTime.getNano(), convert(dateTime.getZone()));
    }

    public static java.time.ZoneId convert(ZoneId zoneId) {
        if (zoneId == null) {
            return null;
        }
        return java.time.ZoneId.of(zoneId.getId());
    }

    public static ZoneId convert(java.time.ZoneId zoneId) {
        if (zoneId == null) {
            return null;
        }
        return ZoneId.of(zoneId.getId());
    }

    public static java.time.MonthDay convert(MonthDay monthDay) {
        if (monthDay == null) {
            return null;
        }
        return java.time.MonthDay.of(monthDay.getMonthValue(), monthDay.getDayOfMonth());
    }

    public static MonthDay convert(java.time.MonthDay monthDay) {
        if (monthDay == null) {
            return null;
        }
        return MonthDay.of(monthDay.getMonthValue(), monthDay.getDayOfMonth());
    }

    public static java.time.Instant convert(Instant instant) {
        if (instant == null) {
            return null;
        }
        return java.time.Instant.ofEpochSecond(instant.getEpochSecond(), instant.getNano());
    }

    public static Instant convert(java.time.Instant instant) {
        if (instant == null) {
            return null;
        }
        return Instant.ofEpochSecond(instant.getEpochSecond(), instant.getNano());
    }

    public static java.time.LocalDate convert(LocalDate date) {
        if (date == null) {
            return null;
        }
        return java.time.LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    public static LocalDate convert(java.time.LocalDate date) {
        if (date == null) {
            return null;
        }
        return LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    public static java.time.Duration convert(Duration duration) {
        if (duration == null) {
            return null;
        }
        return java.time.Duration.ofSeconds(duration.getSeconds(), duration.getNano());
    }

    public static Duration convert(java.time.Duration duration) {
        if (duration == null) {
            return null;
        }
        return Duration.ofSeconds(duration.getSeconds(), duration.getNano());
    }
}
