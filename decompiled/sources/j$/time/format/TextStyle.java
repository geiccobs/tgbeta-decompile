package j$.time.format;

import j$.util.DesugarCalendar;
/* loaded from: classes2.dex */
public enum TextStyle {
    FULL(2, 0),
    FULL_STANDALONE(DesugarCalendar.LONG_STANDALONE, 0),
    SHORT(1, 1),
    SHORT_STANDALONE(DesugarCalendar.SHORT_STANDALONE, 1),
    NARROW(4, 1),
    NARROW_STANDALONE(DesugarCalendar.NARROW_STANDALONE, 1);
    
    private final int calendarStyle;
    private final int zoneNameStyleIndex;

    TextStyle(int calendarStyle, int zoneNameStyleIndex) {
        this.calendarStyle = calendarStyle;
        this.zoneNameStyleIndex = zoneNameStyleIndex;
    }

    public boolean isStandalone() {
        return (ordinal() & 1) == 1;
    }

    public TextStyle asStandalone() {
        return values()[ordinal() | 1];
    }

    public TextStyle asNormal() {
        return values()[ordinal() & (-2)];
    }

    int toCalendarStyle() {
        return this.calendarStyle;
    }

    public int zoneNameStyleIndex() {
        return this.zoneNameStyleIndex;
    }
}
