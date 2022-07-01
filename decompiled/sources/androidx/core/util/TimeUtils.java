package androidx.core.util;

import java.io.PrintWriter;
/* loaded from: classes.dex */
public final class TimeUtils {
    private static final Object sFormatSync = new Object();
    private static char[] sFormatStr = new char[24];

    private static int accumField(int amt, int suffix, boolean always, int zeropad) {
        if (amt > 99 || (always && zeropad >= 3)) {
            return suffix + 3;
        }
        if (amt > 9 || (always && zeropad >= 2)) {
            return suffix + 2;
        }
        if (!always && amt <= 0) {
            return 0;
        }
        return suffix + 1;
    }

    private static int printField(char[] formatStr, int amt, char suffix, int pos, boolean always, int zeropad) {
        int i;
        if (always || amt > 0) {
            if ((!always || zeropad < 3) && amt <= 99) {
                i = pos;
            } else {
                int i2 = amt / 100;
                formatStr[pos] = (char) (i2 + 48);
                i = pos + 1;
                amt -= i2 * 100;
            }
            if ((always && zeropad >= 2) || amt > 9 || pos != i) {
                int i3 = amt / 10;
                formatStr[i] = (char) (i3 + 48);
                i++;
                amt -= i3 * 10;
            }
            formatStr[i] = (char) (amt + 48);
            int i4 = i + 1;
            formatStr[i4] = suffix;
            return i4 + 1;
        }
        return pos;
    }

    private static int formatDurationLocked(long duration, int fieldLen) {
        char c;
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        long j = duration;
        if (sFormatStr.length < fieldLen) {
            sFormatStr = new char[fieldLen];
        }
        char[] cArr = sFormatStr;
        if (j == 0) {
            int i6 = fieldLen - 1;
            while (i6 > 0) {
                cArr[0] = ' ';
            }
            cArr[0] = '0';
            return 1;
        }
        if (j > 0) {
            c = '+';
        } else {
            c = '-';
            j = -j;
        }
        int i7 = (int) (j % 1000);
        int floor = (int) Math.floor(j / 1000);
        if (floor > 86400) {
            i = floor / 86400;
            floor -= 86400 * i;
        } else {
            i = 0;
        }
        if (floor > 3600) {
            i2 = floor / 3600;
            floor -= i2 * 3600;
        } else {
            i2 = 0;
        }
        if (floor > 60) {
            int i8 = floor / 60;
            i3 = floor - (i8 * 60);
            i4 = i8;
        } else {
            i3 = floor;
            i4 = 0;
        }
        if (fieldLen != 0) {
            int accumField = accumField(i, 1, false, 0);
            int accumField2 = accumField + accumField(i2, 1, accumField > 0, 2);
            int accumField3 = accumField2 + accumField(i4, 1, accumField2 > 0, 2);
            int accumField4 = accumField3 + accumField(i3, 1, accumField3 > 0, 2);
            i5 = 0;
            for (int accumField5 = accumField4 + accumField(i7, 2, true, accumField4 > 0 ? 3 : 0) + 1; accumField5 < fieldLen; accumField5++) {
                cArr[i5] = ' ';
                i5++;
            }
        } else {
            i5 = 0;
        }
        cArr[i5] = c;
        int i9 = i5 + 1;
        boolean z = fieldLen != 0;
        int printField = printField(cArr, i, 'd', i9, false, 0);
        int printField2 = printField(cArr, i2, 'h', printField, printField != i9, z ? 2 : 0);
        int printField3 = printField(cArr, i4, 'm', printField2, printField2 != i9, z ? 2 : 0);
        int printField4 = printField(cArr, i3, 's', printField3, printField3 != i9, z ? 2 : 0);
        int printField5 = printField(cArr, i7, 'm', printField4, true, (!z || printField4 == i9) ? 0 : 3);
        cArr[printField5] = 's';
        return printField5 + 1;
    }

    public static void formatDuration(long duration, PrintWriter pw, int fieldLen) {
        synchronized (sFormatSync) {
            pw.print(new String(sFormatStr, 0, formatDurationLocked(duration, fieldLen)));
        }
    }

    public static void formatDuration(long duration, PrintWriter pw) {
        formatDuration(duration, pw, 0);
    }

    public static void formatDuration(long time, long now, PrintWriter pw) {
        if (time == 0) {
            pw.print("--");
        } else {
            formatDuration(time - now, pw, 0);
        }
    }
}
