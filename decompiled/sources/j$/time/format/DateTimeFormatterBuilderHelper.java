package j$.time.format;
/* loaded from: classes2.dex */
final class DateTimeFormatterBuilderHelper {
    private DateTimeFormatterBuilderHelper() {
    }

    public static String transformAndroidJavaTextDateTimePattern(String pattern) {
        if (pattern == null) {
            return null;
        }
        boolean containsCharb = true;
        boolean containsCharB = pattern.indexOf(66) != -1;
        if (pattern.indexOf(98) == -1) {
            containsCharb = false;
        }
        if (containsCharB || containsCharb) {
            return rewriteIcuDateTimePattern(pattern);
        }
        return pattern;
    }

    private static String rewriteIcuDateTimePattern(String pattern) {
        StringBuilder sb = new StringBuilder(pattern.length());
        char prev = ' ';
        for (int i = 0; i < pattern.length(); i++) {
            char curr = pattern.charAt(i);
            switch (curr) {
                case ' ':
                    if (i == 0 || (prev != 'B' && prev != 'b')) {
                        sb.append(curr);
                        break;
                    }
                    break;
                case 'B':
                case 'b':
                    break;
                default:
                    sb.append(curr);
                    break;
            }
            prev = curr;
        }
        int i2 = sb.length();
        int lastIndex = i2 - 1;
        if (lastIndex >= 0 && sb.charAt(lastIndex) == ' ') {
            sb.deleteCharAt(lastIndex);
        }
        return sb.toString();
    }
}
