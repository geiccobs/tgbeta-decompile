package com.google.android.exoplayer2.util;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.security.NetworkSecurityPolicy;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.microsoft.appcenter.crashes.utils.ErrorLogHelper;
import j$.util.DesugarTimeZone;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.BasePermissionsActivity;
import org.telegram.ui.Components.CustomPhoneKeyboardView;
import org.telegram.ui.Components.SearchViewPager;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.voip.GroupCallGridCell;
/* loaded from: classes3.dex */
public final class Util {
    public static final String DEVICE;
    public static final String DEVICE_DEBUG_INFO;
    public static final String MANUFACTURER;
    public static final String MODEL;
    public static final int SDK_INT;
    private static final String TAG = "Util";
    private static HashMap<String, String> languageTagReplacementMap;
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static final Pattern XS_DATE_TIME_PATTERN = Pattern.compile("(\\d\\d\\d\\d)\\-(\\d\\d)\\-(\\d\\d)[Tt](\\d\\d):(\\d\\d):(\\d\\d)([\\.,](\\d+))?([Zz]|((\\+|\\-)(\\d?\\d):?(\\d\\d)))?");
    private static final Pattern XS_DURATION_PATTERN = Pattern.compile("^(-)?P(([0-9]*)Y)?(([0-9]*)M)?(([0-9]*)D)?(T(([0-9]*)H)?(([0-9]*)M)?(([0-9.]*)S)?)?$");
    private static final Pattern ESCAPED_CHARACTER_PATTERN = Pattern.compile("%([A-Fa-f0-9]{2})");
    private static final String[] additionalIsoLanguageReplacements = {"alb", "sq", "arm", "hy", "baq", "eu", "bur", "my", "tib", "bo", "chi", "zh", "cze", "cs", "dut", "nl", "ger", "de", "gre", "el", "fre", "fr", "geo", "ka", "ice", "is", "mac", "mk", "mao", "mi", "may", "ms", "per", "fa", "rum", "ro", "scc", "hbs-srp", "slo", "sk", "wel", "cy", "id", "ms-ind", "iw", "he", "heb", "he", "ji", "yi", "in", "ms-ind", "ind", "ms-ind", "nb", "no-nob", "nob", "no-nob", "nn", "no-nno", "nno", "no-nno", "tw", "ak-twi", "twi", "ak-twi", "bs", "hbs-bos", "bos", "hbs-bos", "hr", "hbs-hrv", "hrv", "hbs-hrv", "sr", "hbs-srp", "srp", "hbs-srp", "cmn", "zh-cmn", "hak", "zh-hak", "nan", "zh-nan", "hsn", "zh-hsn"};
    private static final String[] isoGrandfatheredTagReplacements = {"i-lux", "lb", "i-hak", "zh-hak", "i-navajo", "nv", "no-bok", "no-nob", "no-nyn", "no-nno", "zh-guoyu", "zh-cmn", "zh-hakka", "zh-hak", "zh-min-nan", "zh-nan", "zh-xiang", "zh-hsn"};
    private static final int[] CRC32_BYTES_MSBF = {0, 79764919, 159529838, 222504665, 319059676, 398814059, 445009330, 507990021, 638119352, 583659535, 797628118, 726387553, 890018660, 835552979, 1015980042, 944750013, 1276238704, 1221641927, 1167319070, 1095957929, 1595256236, 1540665371, 1452775106, 1381403509, 1780037320, 1859660671, 1671105958, 1733955601, 2031960084, 2111593891, 1889500026, 1952343757, -1742489888, -1662866601, -1851683442, -1788833735, -1960329156, -1880695413, -2103051438, -2040207643, -1104454824, -1159051537, -1213636554, -1284997759, -1389417084, -1444007885, -1532160278, -1603531939, -734892656, -789352409, -575645954, -646886583, -952755380, -1007220997, -827056094, -898286187, -231047128, -151282273, -71779514, -8804623, -515967244, -436212925, -390279782, -327299027, 881225847, 809987520, 1023691545, 969234094, 662832811, 591600412, 771767749, 717299826, 311336399, 374308984, 453813921, 533576470, 25881363, 88864420, 134795389, 214552010, 2023205639, 2086057648, 1897238633, 1976864222, 1804852699, 1867694188, 1645340341, 1724971778, 1587496639, 1516133128, 1461550545, 1406951526, 1302016099, 1230646740, 1142491917, 1087903418, -1398421865, -1469785312, -1524105735, -1578704818, -1079922613, -1151291908, -1239184603, -1293773166, -1968362705, -1905510760, -2094067647, -2014441994, -1716953613, -1654112188, -1876203875, -1796572374, -525066777, -462094256, -382327159, -302564546, -206542021, -143559028, -97365931, -17609246, -960696225, -1031934488, -817968335, -872425850, -709327229, -780559564, -600130067, -654598054, 1762451694, 1842216281, 1619975040, 1682949687, 2047383090, 2127137669, 1938468188, 2001449195, 1325665622, 1271206113, 1183200824, 1111960463, 1543535498, 1489069629, 1434599652, 1363369299, 622672798, 568075817, 748617968, 677256519, 907627842, 853037301, 1067152940, 995781531, 51762726, 131386257, 177728840, 240578815, 269590778, 349224269, 429104020, 491947555, -248556018, -168932423, -122852000, -60002089, -500490030, -420856475, -341238852, -278395381, -685261898, -739858943, -559578920, -630940305, -1004286614, -1058877219, -845023740, -916395085, -1119974018, -1174433591, -1262701040, -1333941337, -1371866206, -1426332139, -1481064244, -1552294533, -1690935098, -1611170447, -1833673816, -1770699233, -2009983462, -1930228819, -2119160460, -2056179517, 1569362073, 1498123566, 1409854455, 1355396672, 1317987909, 1246755826, 1192025387, 1137557660, 2072149281, 2135122070, 1912620623, 1992383480, 1753615357, 1816598090, 1627664531, 1707420964, 295390185, 358241886, 404320391, 483945776, 43990325, 106832002, 186451547, 266083308, 932423249, 861060070, 1041341759, 986742920, 613929101, 542559546, 756411363, 701822548, -978770311, -1050133554, -869589737, -924188512, -693284699, -764654318, -550540341, -605129092, -475935807, -413084042, -366743377, -287118056, -257573603, -194731862, -114850189, -35218492, -1984365303, -1921392450, -2143631769, -2063868976, -1698919467, -1635936670, -1824608069, -1744851700, -1347415887, -1418654458, -1506661409, -1561119128, -1129027987, -1200260134, -1254728445, -1309196108};
    private static final int[] CRC8_BYTES_MSBF = {0, 7, 14, 9, 28, 27, 18, 21, 56, 63, 54, 49, 36, 35, 42, 45, 112, 119, 126, 121, 108, 107, 98, 101, 72, 79, 70, 65, 84, 83, 90, 93, 224, 231, 238, 233, 252, 251, 242, 245, 216, 223, 214, 209, 196, 195, SearchViewPager.deleteItemId, 205, 144, BasePermissionsActivity.REQUEST_CODE_EXTERNAL_STORAGE_FOR_AVATAR, 158, 153, 140, 139, TsExtractor.TS_STREAM_TYPE_HDMV_DTS, 133, 168, 175, 166, 161, 180, 179, 186, PsExtractor.PRIVATE_STREAM_1, 199, PsExtractor.AUDIO_STREAM, SearchViewPager.forwardItemId, 206, 219, 220, 213, 210, 255, 248, 241, 246, 227, 228, 237, 234, 183, 176, 185, 190, 171, TsExtractor.TS_STREAM_TYPE_AC4, GroupCallGridCell.CELL_HEIGHT, 162, TLRPC.LAYER, 136, TsExtractor.TS_STREAM_TYPE_AC3, TsExtractor.TS_STREAM_TYPE_SPLICE_INFO, 147, 148, 157, 154, 39, 32, 41, 46, 59, 60, 53, 50, 31, 24, 17, 22, 3, 4, 13, 10, 87, 80, 89, 94, 75, 76, 69, 66, 111, LocationRequest.PRIORITY_LOW_POWER, 97, 102, 115, 116, ErrorLogHelper.MAX_PROPERTY_ITEM_LENGTH, 122, 137, 142, TsExtractor.TS_STREAM_TYPE_E_AC3, 128, 149, 146, 155, 156, 177, 182, 191, 184, 173, 170, 163, 164, 249, 254, 247, PsExtractor.VIDEO_STREAM_MASK, 229, 226, 235, 236, 193, 198, 207, 200, 221, 218, 211, 212, LocationRequest.PRIORITY_NO_POWER, 110, 103, 96, 117, 114, 123, 124, 81, 86, 95, 88, 77, 74, 67, 68, 25, 30, 23, 16, 5, 2, 11, 12, 33, 38, 47, 40, 61, 58, 51, 52, 78, 73, 64, 71, 82, 85, 92, 91, 118, 113, 120, 127, 106, 109, 100, 99, 62, 57, 48, 55, 34, 37, 44, 43, 6, 1, 8, 15, 26, 29, 20, 19, 174, 169, 160, 167, 178, 181, TsExtractor.TS_PACKET_SIZE, 187, 150, 145, 152, 159, TsExtractor.TS_STREAM_TYPE_DTS, 141, 132, 131, 222, 217, 208, 215, 194, 197, 204, 203, CustomPhoneKeyboardView.KEYBOARD_HEIGHT_DP, 225, 232, 239, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 253, 244, 243};

    static {
        int i = Build.VERSION.SDK_INT;
        SDK_INT = i;
        String str = Build.DEVICE;
        DEVICE = str;
        String str2 = Build.MANUFACTURER;
        MANUFACTURER = str2;
        String str3 = Build.MODEL;
        MODEL = str3;
        DEVICE_DEBUG_INFO = str + ", " + str3 + ", " + str2 + ", " + i;
    }

    private Util() {
    }

    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[4096];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        while (true) {
            int bytesRead = inputStream.read(buffer);
            if (bytesRead != -1) {
                outputStream.write(buffer, 0, bytesRead);
            } else {
                return outputStream.toByteArray();
            }
        }
    }

    public static ComponentName startForegroundService(Context context, Intent intent) {
        if (SDK_INT >= 26) {
            return context.startForegroundService(intent);
        }
        return context.startService(intent);
    }

    public static boolean maybeRequestReadExternalStoragePermission(Activity activity, Uri... uris) {
        if (SDK_INT < 23) {
            return false;
        }
        int length = uris.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            Uri uri = uris[i];
            if (!isLocalFileUri(uri)) {
                i++;
            } else if (activity.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                activity.requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 0);
                return true;
            }
        }
        return false;
    }

    public static boolean checkCleartextTrafficPermitted(Uri... uris) {
        if (SDK_INT < 24) {
            return true;
        }
        for (Uri uri : uris) {
            if ("http".equals(uri.getScheme()) && !NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted((String) Assertions.checkNotNull(uri.getHost()))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isLocalFileUri(Uri uri) {
        String scheme = uri.getScheme();
        return TextUtils.isEmpty(scheme) || "file".equals(scheme);
    }

    public static boolean areEqual(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    public static boolean contains(Object[] items, Object item) {
        for (Object arrayItem : items) {
            if (areEqual(arrayItem, item)) {
                return true;
            }
        }
        return false;
    }

    public static <T> void removeRange(List<T> list, int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > list.size() || fromIndex > toIndex) {
            throw new IllegalArgumentException();
        }
        if (fromIndex != toIndex) {
            list.subList(fromIndex, toIndex).clear();
        }
    }

    @EnsuresNonNull({"#1"})
    public static <T> T castNonNull(T value) {
        return value;
    }

    @EnsuresNonNull({"#1"})
    public static <T> T[] castNonNullTypeArray(T[] value) {
        return value;
    }

    public static <T> T[] nullSafeArrayCopy(T[] input, int length) {
        Assertions.checkArgument(length <= input.length);
        return (T[]) Arrays.copyOf(input, length);
    }

    public static <T> T[] nullSafeArrayCopyOfRange(T[] input, int from, int to) {
        boolean z = true;
        Assertions.checkArgument(from >= 0);
        if (to > input.length) {
            z = false;
        }
        Assertions.checkArgument(z);
        return (T[]) Arrays.copyOfRange(input, from, to);
    }

    public static <T> T[] nullSafeArrayAppend(T[] original, T newElement) {
        Object[] copyOf = Arrays.copyOf(original, original.length + 1);
        copyOf[original.length] = newElement;
        return (T[]) castNonNullTypeArray(copyOf);
    }

    public static <T> T[] nullSafeArrayConcatenation(T[] first, T[] second) {
        T[] concatenation = (T[]) Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, concatenation, first.length, second.length);
        return concatenation;
    }

    public static Handler createHandler(Handler.Callback callback) {
        return createHandler(getLooper(), callback);
    }

    public static Handler createHandler(Looper looper, Handler.Callback callback) {
        return new Handler(looper, callback);
    }

    public static Looper getLooper() {
        Looper myLooper = Looper.myLooper();
        return myLooper != null ? myLooper : Looper.getMainLooper();
    }

    public static /* synthetic */ Thread lambda$newSingleThreadExecutor$0(String threadName, Runnable runnable) {
        return new Thread(runnable, threadName);
    }

    public static ExecutorService newSingleThreadExecutor(final String threadName) {
        return Executors.newSingleThreadExecutor(new ThreadFactory() { // from class: com.google.android.exoplayer2.util.Util$$ExternalSyntheticLambda2
            @Override // java.util.concurrent.ThreadFactory
            public final Thread newThread(Runnable runnable) {
                return Util.lambda$newSingleThreadExecutor$0(threadName, runnable);
            }
        });
    }

    public static void closeQuietly(DataSource dataSource) {
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (IOException e) {
            }
        }
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

    public static boolean readBoolean(Parcel parcel) {
        return parcel.readInt() != 0;
    }

    public static void writeBoolean(Parcel parcel, boolean value) {
        parcel.writeInt(value ? 1 : 0);
    }

    public static String getLocaleLanguageTag(Locale locale) {
        return SDK_INT >= 21 ? getLocaleLanguageTagV21(locale) : locale.toString();
    }

    public static String normalizeLanguageCode(String language) {
        if (language == null) {
            return null;
        }
        String normalizedTag = language.replace('_', '-');
        if (normalizedTag.isEmpty() || "und".equals(normalizedTag)) {
            normalizedTag = language;
        }
        String normalizedTag2 = toLowerInvariant(normalizedTag);
        String mainLanguage = splitAtFirst(normalizedTag2, "-")[0];
        if (languageTagReplacementMap == null) {
            languageTagReplacementMap = createIsoLanguageReplacementMap();
        }
        String replacedLanguage = languageTagReplacementMap.get(mainLanguage);
        if (replacedLanguage != null) {
            normalizedTag2 = replacedLanguage + normalizedTag2.substring(mainLanguage.length());
            mainLanguage = replacedLanguage;
        }
        if ("no".equals(mainLanguage) || "i".equals(mainLanguage) || "zh".equals(mainLanguage)) {
            return maybeReplaceGrandfatheredLanguageTags(normalizedTag2);
        }
        return normalizedTag2;
    }

    public static String fromUtf8Bytes(byte[] bytes) {
        return new String(bytes, Charset.forName("UTF-8"));
    }

    public static String fromUtf8Bytes(byte[] bytes, int offset, int length) {
        return new String(bytes, offset, length, Charset.forName("UTF-8"));
    }

    public static byte[] getUtf8Bytes(String value) {
        return value.getBytes(Charset.forName("UTF-8"));
    }

    public static String[] split(String value, String regex) {
        return value.split(regex, -1);
    }

    public static String[] splitAtFirst(String value, String regex) {
        return value.split(regex, 2);
    }

    public static boolean isLinebreak(int c) {
        return c == 10 || c == 13;
    }

    public static String toLowerInvariant(String text) {
        return text == null ? text : text.toLowerCase(Locale.US);
    }

    public static String toUpperInvariant(String text) {
        return text == null ? text : text.toUpperCase(Locale.US);
    }

    public static String formatInvariant(String format, Object... args) {
        return String.format(Locale.US, format, args);
    }

    public static int ceilDivide(int numerator, int denominator) {
        return ((numerator + denominator) - 1) / denominator;
    }

    public static long ceilDivide(long numerator, long denominator) {
        return ((numerator + denominator) - 1) / denominator;
    }

    public static int constrainValue(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    public static long constrainValue(long value, long min, long max) {
        return Math.max(min, Math.min(value, max));
    }

    public static float constrainValue(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }

    public static long addWithOverflowDefault(long x, long y, long overflowResult) {
        long result = x + y;
        if (((x ^ result) & (y ^ result)) < 0) {
            return overflowResult;
        }
        return result;
    }

    public static long subtractWithOverflowDefault(long x, long y, long overflowResult) {
        long result = x - y;
        if (((x ^ y) & (x ^ result)) < 0) {
            return overflowResult;
        }
        return result;
    }

    public static int linearSearch(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public static int linearSearch(long[] array, long value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public static int binarySearchFloor(int[] array, int value, boolean inclusive, boolean stayInBounds) {
        int index = Arrays.binarySearch(array, value);
        if (index < 0) {
            index = -(index + 2);
        } else {
            do {
                index--;
                if (index < 0) {
                    break;
                }
            } while (array[index] == value);
            if (inclusive) {
                index++;
            }
        }
        return stayInBounds ? Math.max(0, index) : index;
    }

    public static int binarySearchFloor(long[] array, long value, boolean inclusive, boolean stayInBounds) {
        int index = Arrays.binarySearch(array, value);
        if (index < 0) {
            index = -(index + 2);
        } else {
            do {
                index--;
                if (index < 0) {
                    break;
                }
            } while (array[index] == value);
            if (inclusive) {
                index++;
            }
        }
        return stayInBounds ? Math.max(0, index) : index;
    }

    public static <T extends Comparable<? super T>> int binarySearchFloor(List<? extends Comparable<? super T>> list, T value, boolean inclusive, boolean stayInBounds) {
        int index = Collections.binarySearch(list, value);
        if (index < 0) {
            index = -(index + 2);
        } else {
            do {
                index--;
                if (index < 0) {
                    break;
                }
            } while (list.get(index).compareTo(value) == 0);
            if (inclusive) {
                index++;
            }
        }
        return stayInBounds ? Math.max(0, index) : index;
    }

    public static int binarySearchCeil(int[] array, int value, boolean inclusive, boolean stayInBounds) {
        int index = Arrays.binarySearch(array, value);
        if (index < 0) {
            index ^= -1;
        } else {
            do {
                index++;
                if (index >= array.length) {
                    break;
                }
            } while (array[index] == value);
            if (inclusive) {
                index--;
            }
        }
        return stayInBounds ? Math.min(array.length - 1, index) : index;
    }

    public static int binarySearchCeil(long[] array, long value, boolean inclusive, boolean stayInBounds) {
        int index = Arrays.binarySearch(array, value);
        if (index < 0) {
            index ^= -1;
        } else {
            do {
                index++;
                if (index >= array.length) {
                    break;
                }
            } while (array[index] == value);
            if (inclusive) {
                index--;
            }
        }
        return stayInBounds ? Math.min(array.length - 1, index) : index;
    }

    public static <T extends Comparable<? super T>> int binarySearchCeil(List<? extends Comparable<? super T>> list, T value, boolean inclusive, boolean stayInBounds) {
        int index = Collections.binarySearch(list, value);
        if (index < 0) {
            index ^= -1;
        } else {
            int listSize = list.size();
            do {
                index++;
                if (index >= listSize) {
                    break;
                }
            } while (list.get(index).compareTo(value) == 0);
            if (inclusive) {
                index--;
            }
        }
        return stayInBounds ? Math.min(list.size() - 1, index) : index;
    }

    public static int compareLong(long left, long right) {
        if (left < right) {
            return -1;
        }
        return left == right ? 0 : 1;
    }

    public static long parseXsDuration(String value) {
        Matcher matcher = XS_DURATION_PATTERN.matcher(value);
        if (matcher.matches()) {
            boolean negated = true ^ TextUtils.isEmpty(matcher.group(1));
            String years = matcher.group(3);
            double d = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            double durationSeconds = years != null ? Double.parseDouble(years) * 3.1556908E7d : 0.0d;
            String months = matcher.group(5);
            double durationSeconds2 = durationSeconds + (months != null ? Double.parseDouble(months) * 2629739.0d : 0.0d);
            String days = matcher.group(7);
            double durationSeconds3 = durationSeconds2 + (days != null ? Double.parseDouble(days) * 86400.0d : 0.0d);
            String hours = matcher.group(10);
            double durationSeconds4 = durationSeconds3 + (hours != null ? Double.parseDouble(hours) * 3600.0d : 0.0d);
            String minutes = matcher.group(12);
            double durationSeconds5 = durationSeconds4 + (minutes != null ? Double.parseDouble(minutes) * 60.0d : 0.0d);
            String seconds = matcher.group(14);
            if (seconds != null) {
                d = Double.parseDouble(seconds);
            }
            long durationMillis = (long) (1000.0d * (durationSeconds5 + d));
            return negated ? -durationMillis : durationMillis;
        }
        return (long) (Double.parseDouble(value) * 3600.0d * 1000.0d);
    }

    public static long parseXsDateTime(String value) throws ParserException {
        int timezoneShift;
        Matcher matcher = XS_DATE_TIME_PATTERN.matcher(value);
        if (!matcher.matches()) {
            throw new ParserException("Invalid date/time format: " + value);
        }
        if (matcher.group(9) == null) {
            timezoneShift = 0;
        } else if (matcher.group(9).equalsIgnoreCase("Z")) {
            timezoneShift = 0;
        } else {
            timezoneShift = (Integer.parseInt(matcher.group(12)) * 60) + Integer.parseInt(matcher.group(13));
            if ("-".equals(matcher.group(11))) {
                timezoneShift *= -1;
            }
        }
        Calendar dateTime = new GregorianCalendar(DesugarTimeZone.getTimeZone("GMT"));
        dateTime.clear();
        dateTime.set(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)) - 1, Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)), Integer.parseInt(matcher.group(5)), Integer.parseInt(matcher.group(6)));
        if (!TextUtils.isEmpty(matcher.group(8))) {
            BigDecimal bd = new BigDecimal("0." + matcher.group(8));
            dateTime.set(14, bd.movePointRight(3).intValue());
        }
        long time = dateTime.getTimeInMillis();
        if (timezoneShift != 0) {
            return time - (60000 * timezoneShift);
        }
        return time;
    }

    public static long scaleLargeTimestamp(long timestamp, long multiplier, long divisor) {
        if (divisor >= multiplier && divisor % multiplier == 0) {
            long divisionFactor = divisor / multiplier;
            return timestamp / divisionFactor;
        } else if (divisor < multiplier && multiplier % divisor == 0) {
            long multiplicationFactor = multiplier / divisor;
            return timestamp * multiplicationFactor;
        } else {
            double d = multiplier;
            double d2 = divisor;
            Double.isNaN(d);
            Double.isNaN(d2);
            double multiplicationFactor2 = d / d2;
            double d3 = timestamp;
            Double.isNaN(d3);
            return (long) (d3 * multiplicationFactor2);
        }
    }

    public static long[] scaleLargeTimestamps(List<Long> timestamps, long multiplier, long divisor) {
        long[] scaledTimestamps = new long[timestamps.size()];
        if (divisor < multiplier || divisor % multiplier != 0) {
            if (divisor < multiplier && multiplier % divisor == 0) {
                long multiplicationFactor = multiplier / divisor;
                for (int i = 0; i < scaledTimestamps.length; i++) {
                    scaledTimestamps[i] = timestamps.get(i).longValue() * multiplicationFactor;
                }
            } else {
                double d = multiplier;
                double d2 = divisor;
                Double.isNaN(d);
                Double.isNaN(d2);
                double multiplicationFactor2 = d / d2;
                for (int i2 = 0; i2 < scaledTimestamps.length; i2++) {
                    double longValue = timestamps.get(i2).longValue();
                    Double.isNaN(longValue);
                    scaledTimestamps[i2] = (long) (longValue * multiplicationFactor2);
                }
            }
        } else {
            long divisionFactor = divisor / multiplier;
            for (int i3 = 0; i3 < scaledTimestamps.length; i3++) {
                scaledTimestamps[i3] = timestamps.get(i3).longValue() / divisionFactor;
            }
        }
        return scaledTimestamps;
    }

    public static void scaleLargeTimestampsInPlace(long[] timestamps, long multiplier, long divisor) {
        if (divisor < multiplier || divisor % multiplier != 0) {
            if (divisor < multiplier && multiplier % divisor == 0) {
                long multiplicationFactor = multiplier / divisor;
                for (int i = 0; i < timestamps.length; i++) {
                    timestamps[i] = timestamps[i] * multiplicationFactor;
                }
                return;
            }
            double d = multiplier;
            double d2 = divisor;
            Double.isNaN(d);
            Double.isNaN(d2);
            double multiplicationFactor2 = d / d2;
            for (int i2 = 0; i2 < timestamps.length; i2++) {
                double d3 = timestamps[i2];
                Double.isNaN(d3);
                timestamps[i2] = (long) (d3 * multiplicationFactor2);
            }
            return;
        }
        long divisionFactor = divisor / multiplier;
        for (int i3 = 0; i3 < timestamps.length; i3++) {
            timestamps[i3] = timestamps[i3] / divisionFactor;
        }
    }

    public static long getMediaDurationForPlayoutDuration(long playoutDuration, float speed) {
        if (speed == 1.0f) {
            return playoutDuration;
        }
        double d = playoutDuration;
        double d2 = speed;
        Double.isNaN(d);
        Double.isNaN(d2);
        return Math.round(d * d2);
    }

    public static long getPlayoutDurationForMediaDuration(long mediaDuration, float speed) {
        if (speed == 1.0f) {
            return mediaDuration;
        }
        double d = mediaDuration;
        double d2 = speed;
        Double.isNaN(d);
        Double.isNaN(d2);
        return Math.round(d / d2);
    }

    public static long resolveSeekPositionUs(long positionUs, SeekParameters seekParameters, long firstSyncUs, long secondSyncUs) {
        if (SeekParameters.EXACT.equals(seekParameters)) {
            return positionUs;
        }
        long minPositionUs = subtractWithOverflowDefault(positionUs, seekParameters.toleranceBeforeUs, Long.MIN_VALUE);
        long maxPositionUs = addWithOverflowDefault(positionUs, seekParameters.toleranceAfterUs, Long.MAX_VALUE);
        boolean secondSyncPositionValid = true;
        boolean firstSyncPositionValid = minPositionUs <= firstSyncUs && firstSyncUs <= maxPositionUs;
        if (minPositionUs > secondSyncUs || secondSyncUs > maxPositionUs) {
            secondSyncPositionValid = false;
        }
        if (firstSyncPositionValid && secondSyncPositionValid) {
            if (Math.abs(firstSyncUs - positionUs) <= Math.abs(secondSyncUs - positionUs)) {
                return firstSyncUs;
            }
            return secondSyncUs;
        } else if (firstSyncPositionValid) {
            return firstSyncUs;
        } else {
            if (secondSyncPositionValid) {
                return secondSyncUs;
            }
            return minPositionUs;
        }
    }

    public static int[] toArray(List<Integer> list) {
        if (list == null) {
            return null;
        }
        int length = list.size();
        int[] intArray = new int[length];
        for (int i = 0; i < length; i++) {
            intArray[i] = list.get(i).intValue();
        }
        return intArray;
    }

    public static int getIntegerCodeForString(String string) {
        int length = string.length();
        Assertions.checkArgument(length <= 4);
        int result = 0;
        for (int i = 0; i < length; i++) {
            result = (result << 8) | string.charAt(i);
        }
        return result;
    }

    public static long toUnsignedLong(int x) {
        return x & 4294967295L;
    }

    public static long toLong(int mostSignificantBits, int leastSignificantBits) {
        return (toUnsignedLong(mostSignificantBits) << 32) | toUnsignedLong(leastSignificantBits);
    }

    public static byte[] getBytesFromHexString(String hexString) {
        byte[] data = new byte[hexString.length() / 2];
        for (int i = 0; i < data.length; i++) {
            int stringOffset = i * 2;
            data[i] = (byte) ((Character.digit(hexString.charAt(stringOffset), 16) << 4) + Character.digit(hexString.charAt(stringOffset + 1), 16));
        }
        return data;
    }

    public static String getCommaDelimitedSimpleClassNames(Object[] objects) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < objects.length; i++) {
            stringBuilder.append(objects[i].getClass().getSimpleName());
            if (i < objects.length - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    public static String getUserAgent(Context context, String applicationName) {
        String packageName;
        try {
            String packageName2 = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName2, 0);
            packageName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            packageName = "?";
        }
        return applicationName + "/" + packageName + " (Linux;Android " + Build.VERSION.RELEASE + ") " + ExoPlayerLibraryInfo.VERSION_SLASHY;
    }

    public static String getCodecsOfType(String codecs, int trackType) {
        String[] codecArray = splitCodecs(codecs);
        if (codecArray.length == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (String codec : codecArray) {
            if (trackType == MimeTypes.getTrackTypeOfCodec(codec)) {
                if (builder.length() > 0) {
                    builder.append(",");
                }
                builder.append(codec);
            }
        }
        if (builder.length() <= 0) {
            return null;
        }
        return builder.toString();
    }

    public static String[] splitCodecs(String codecs) {
        if (TextUtils.isEmpty(codecs)) {
            return new String[0];
        }
        return split(codecs.trim(), "(\\s*,\\s*)");
    }

    public static int getPcmEncoding(int bitDepth) {
        switch (bitDepth) {
            case 8:
                return 3;
            case 16:
                return 2;
            case 24:
                return 536870912;
            case 32:
                return C.ENCODING_PCM_32BIT;
            default:
                return 0;
        }
    }

    public static boolean isEncodingLinearPcm(int encoding) {
        return encoding == 3 || encoding == 2 || encoding == 268435456 || encoding == 536870912 || encoding == 805306368 || encoding == 4;
    }

    public static boolean isEncodingHighResolutionPcm(int encoding) {
        return encoding == 536870912 || encoding == 805306368 || encoding == 4;
    }

    public static int getAudioTrackChannelConfig(int channelCount) {
        switch (channelCount) {
            case 1:
                return 4;
            case 2:
                return 12;
            case 3:
                return 28;
            case 4:
                return 204;
            case 5:
                return 220;
            case 6:
                return 252;
            case 7:
                return 1276;
            case 8:
                int i = SDK_INT;
                return (i < 23 && i < 21) ? 0 : 6396;
            default:
                return 0;
        }
    }

    public static int getPcmFrameSize(int pcmEncoding, int channelCount) {
        switch (pcmEncoding) {
            case 2:
            case 268435456:
                return channelCount * 2;
            case 3:
                return channelCount;
            case 4:
            case C.ENCODING_PCM_32BIT /* 805306368 */:
                return channelCount * 4;
            case 536870912:
                return channelCount * 3;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static int getAudioUsageForStreamType(int streamType) {
        switch (streamType) {
            case 0:
                return 2;
            case 1:
                return 13;
            case 2:
                return 6;
            case 3:
            case 6:
            case 7:
            default:
                return 1;
            case 4:
                return 4;
            case 5:
                return 5;
            case 8:
                return 3;
        }
    }

    public static int getAudioContentTypeForStreamType(int streamType) {
        switch (streamType) {
            case 0:
                return 1;
            case 1:
            case 2:
            case 4:
            case 5:
            case 8:
                return 4;
            case 3:
            case 6:
            case 7:
            default:
                return 2;
        }
    }

    public static int getStreamTypeForAudioUsage(int usage) {
        switch (usage) {
            case 1:
            case 12:
            case 14:
                return 3;
            case 2:
                return 0;
            case 3:
                return 8;
            case 4:
                return 4;
            case 5:
            case 7:
            case 8:
            case 9:
            case 10:
                return 5;
            case 6:
                return 2;
            case 11:
            default:
                return 3;
            case 13:
                return 1;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static UUID getDrmUuid(String drmScheme) {
        char c;
        String lowerInvariant = toLowerInvariant(drmScheme);
        switch (lowerInvariant.hashCode()) {
            case -1860423953:
                if (lowerInvariant.equals("playready")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -1400551171:
                if (lowerInvariant.equals("widevine")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 790309106:
                if (lowerInvariant.equals("clearkey")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return C.WIDEVINE_UUID;
            case 1:
                return C.PLAYREADY_UUID;
            case 2:
                return C.CLEARKEY_UUID;
            default:
                try {
                    return UUID.fromString(drmScheme);
                } catch (RuntimeException e) {
                    return null;
                }
        }
    }

    public static int inferContentType(Uri uri, String overrideExtension) {
        if (TextUtils.isEmpty(overrideExtension)) {
            return inferContentType(uri);
        }
        return inferContentType("." + overrideExtension);
    }

    public static int inferContentType(Uri uri) {
        String path = uri.getPath();
        if (path == null) {
            return 3;
        }
        return inferContentType(path);
    }

    public static int inferContentType(String fileName) {
        String fileName2 = toLowerInvariant(fileName);
        if (fileName2.endsWith(".mpd")) {
            return 0;
        }
        if (fileName2.endsWith(".m3u8")) {
            return 2;
        }
        if (fileName2.matches(".*\\.ism(l)?(/manifest(\\(.+\\))?)?")) {
            return 1;
        }
        return 3;
    }

    public static String getStringForTime(StringBuilder builder, Formatter formatter, long timeMs) {
        long timeMs2;
        if (timeMs != C.TIME_UNSET) {
            timeMs2 = timeMs;
        } else {
            timeMs2 = 0;
        }
        long totalSeconds = (500 + timeMs2) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        builder.setLength(0);
        return hours > 0 ? formatter.format("%d:%02d:%02d", Long.valueOf(hours), Long.valueOf(minutes), Long.valueOf(seconds)).toString() : formatter.format("%02d:%02d", Long.valueOf(minutes), Long.valueOf(seconds)).toString();
    }

    public static String escapeFileName(String fileName) {
        int length = fileName.length();
        int charactersToEscapeCount = 0;
        for (int i = 0; i < length; i++) {
            if (shouldEscapeCharacter(fileName.charAt(i))) {
                charactersToEscapeCount++;
            }
        }
        if (charactersToEscapeCount == 0) {
            return fileName;
        }
        int i2 = 0;
        StringBuilder builder = new StringBuilder((charactersToEscapeCount * 2) + length);
        while (charactersToEscapeCount > 0) {
            int i3 = i2 + 1;
            char c = fileName.charAt(i2);
            if (shouldEscapeCharacter(c)) {
                builder.append('%');
                builder.append(Integer.toHexString(c));
                charactersToEscapeCount--;
            } else {
                builder.append(c);
            }
            i2 = i3;
        }
        if (i2 < length) {
            builder.append((CharSequence) fileName, i2, length);
        }
        return builder.toString();
    }

    private static boolean shouldEscapeCharacter(char c) {
        switch (c) {
            case '\"':
            case '%':
            case '*':
            case '/':
            case UndoView.ACTION_TEXT_COPIED /* 58 */:
            case UndoView.ACTION_PHONE_COPIED /* 60 */:
            case '>':
            case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
            case '\\':
            case '|':
                return true;
            default:
                return false;
        }
    }

    public static String unescapeFileName(String fileName) {
        int length = fileName.length();
        int percentCharacterCount = 0;
        for (int i = 0; i < length; i++) {
            if (fileName.charAt(i) == '%') {
                percentCharacterCount++;
            }
        }
        if (percentCharacterCount == 0) {
            return fileName;
        }
        int expectedLength = length - (percentCharacterCount * 2);
        StringBuilder builder = new StringBuilder(expectedLength);
        Matcher matcher = ESCAPED_CHARACTER_PATTERN.matcher(fileName);
        int startOfNotEscaped = 0;
        while (percentCharacterCount > 0 && matcher.find()) {
            char unescapedCharacter = (char) Integer.parseInt(matcher.group(1), 16);
            builder.append((CharSequence) fileName, startOfNotEscaped, matcher.start());
            builder.append(unescapedCharacter);
            startOfNotEscaped = matcher.end();
            percentCharacterCount--;
        }
        if (startOfNotEscaped < length) {
            builder.append((CharSequence) fileName, startOfNotEscaped, length);
        }
        if (builder.length() != expectedLength) {
            return null;
        }
        return builder.toString();
    }

    public static void sneakyThrow(Throwable t) {
        sneakyThrowInternal(t);
    }

    private static <T extends Throwable> void sneakyThrowInternal(Throwable t) throws Throwable {
        throw t;
    }

    public static void recursiveDelete(File fileOrDirectory) {
        File[] directoryFiles = fileOrDirectory.listFiles();
        if (directoryFiles != null) {
            for (File child : directoryFiles) {
                recursiveDelete(child);
            }
        }
        fileOrDirectory.delete();
    }

    public static File createTempDirectory(Context context, String prefix) throws IOException {
        File tempFile = createTempFile(context, prefix);
        tempFile.delete();
        tempFile.mkdir();
        return tempFile;
    }

    public static File createTempFile(Context context, String prefix) throws IOException {
        return File.createTempFile(prefix, null, context.getCacheDir());
    }

    public static int crc32(byte[] bytes, int start, int end, int initialValue) {
        for (int i = start; i < end; i++) {
            initialValue = (initialValue << 8) ^ CRC32_BYTES_MSBF[((initialValue >>> 24) ^ (bytes[i] & 255)) & 255];
        }
        return initialValue;
    }

    public static int crc8(byte[] bytes, int start, int end, int initialValue) {
        for (int i = start; i < end; i++) {
            initialValue = CRC8_BYTES_MSBF[(bytes[i] & 255) ^ initialValue];
        }
        return initialValue;
    }

    public static int getNetworkType(Context context) {
        ConnectivityManager connectivityManager;
        if (context == null || (connectivityManager = (ConnectivityManager) context.getSystemService("connectivity")) == null) {
            return 0;
        }
        try {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                return 1;
            }
            switch (networkInfo.getType()) {
                case 0:
                case 4:
                case 5:
                    return getMobileNetworkType(networkInfo);
                case 1:
                    return 2;
                case 2:
                case 3:
                case 7:
                case 8:
                default:
                    return 8;
                case 6:
                    return 5;
                case 9:
                    return 7;
            }
        } catch (SecurityException e) {
            return 0;
        }
    }

    public static String getCountryCode(Context context) {
        TelephonyManager telephonyManager;
        if (context != null && (telephonyManager = (TelephonyManager) context.getSystemService("phone")) != null) {
            String countryCode = telephonyManager.getNetworkCountryIso();
            if (!TextUtils.isEmpty(countryCode)) {
                return toUpperInvariant(countryCode);
            }
        }
        return toUpperInvariant(Locale.getDefault().getCountry());
    }

    public static String[] getSystemLanguageCodes() {
        String[] systemLocales = getSystemLocales();
        for (int i = 0; i < systemLocales.length; i++) {
            systemLocales[i] = normalizeLanguageCode(systemLocales[i]);
        }
        return systemLocales;
    }

    /* JADX WARN: Code restructure failed: missing block: B:26:0x0061, code lost:
        return false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean inflate(com.google.android.exoplayer2.util.ParsableByteArray r5, com.google.android.exoplayer2.util.ParsableByteArray r6, java.util.zip.Inflater r7) {
        /*
            int r0 = r5.bytesLeft()
            r1 = 0
            if (r0 > 0) goto L8
            return r1
        L8:
            byte[] r0 = r6.data
            int r2 = r0.length
            int r3 = r5.bytesLeft()
            if (r2 >= r3) goto L19
            int r2 = r5.bytesLeft()
            int r2 = r2 * 2
            byte[] r0 = new byte[r2]
        L19:
            if (r7 != 0) goto L21
            java.util.zip.Inflater r2 = new java.util.zip.Inflater
            r2.<init>()
            r7 = r2
        L21:
            byte[] r2 = r5.data
            int r3 = r5.getPosition()
            int r4 = r5.bytesLeft()
            r7.setInput(r2, r3, r4)
            r2 = 0
        L2f:
            int r3 = r0.length     // Catch: java.lang.Throwable -> L62 java.util.zip.DataFormatException -> L67
            int r3 = r3 - r2
            int r3 = r7.inflate(r0, r2, r3)     // Catch: java.lang.Throwable -> L62 java.util.zip.DataFormatException -> L67
            int r2 = r2 + r3
            boolean r3 = r7.finished()     // Catch: java.lang.Throwable -> L62 java.util.zip.DataFormatException -> L67
            if (r3 == 0) goto L44
            r6.reset(r0, r2)     // Catch: java.lang.Throwable -> L62 java.util.zip.DataFormatException -> L67
            r1 = 1
            r7.reset()
            return r1
        L44:
            boolean r3 = r7.needsDictionary()     // Catch: java.lang.Throwable -> L62 java.util.zip.DataFormatException -> L67
            if (r3 != 0) goto L5d
            boolean r3 = r7.needsInput()     // Catch: java.lang.Throwable -> L62 java.util.zip.DataFormatException -> L67
            if (r3 == 0) goto L51
            goto L5d
        L51:
            int r3 = r0.length     // Catch: java.lang.Throwable -> L62 java.util.zip.DataFormatException -> L67
            if (r2 != r3) goto L2f
            int r3 = r0.length     // Catch: java.lang.Throwable -> L62 java.util.zip.DataFormatException -> L67
            int r3 = r3 * 2
            byte[] r3 = java.util.Arrays.copyOf(r0, r3)     // Catch: java.lang.Throwable -> L62 java.util.zip.DataFormatException -> L67
            r0 = r3
            goto L2f
        L5d:
            r7.reset()
            return r1
        L62:
            r1 = move-exception
            r7.reset()
            throw r1
        L67:
            r2 = move-exception
            r7.reset()
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.util.Util.inflate(com.google.android.exoplayer2.util.ParsableByteArray, com.google.android.exoplayer2.util.ParsableByteArray, java.util.zip.Inflater):boolean");
    }

    public static boolean isTv(Context context) {
        UiModeManager uiModeManager = (UiModeManager) context.getApplicationContext().getSystemService("uimode");
        return uiModeManager != null && uiModeManager.getCurrentModeType() == 4;
    }

    public static Point getCurrentDisplayModeSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        return getCurrentDisplayModeSize(context, windowManager.getDefaultDisplay());
    }

    public static Point getCurrentDisplayModeSize(Context context, Display display) {
        String displaySize;
        int i = SDK_INT;
        if (i <= 29 && display.getDisplayId() == 0 && isTv(context)) {
            if ("Sony".equals(MANUFACTURER) && MODEL.startsWith("BRAVIA") && context.getPackageManager().hasSystemFeature("com.sony.dtv.hardware.panel.qfhd")) {
                return new Point(3840, 2160);
            }
            if (i < 28) {
                displaySize = getSystemProperty("sys.display-size");
            } else {
                displaySize = getSystemProperty("vendor.display-size");
            }
            if (!TextUtils.isEmpty(displaySize)) {
                try {
                    String[] displaySizeParts = split(displaySize.trim(), "x");
                    if (displaySizeParts.length == 2) {
                        int width = Integer.parseInt(displaySizeParts[0]);
                        int height = Integer.parseInt(displaySizeParts[1]);
                        if (width > 0 && height > 0) {
                            return new Point(width, height);
                        }
                    }
                } catch (NumberFormatException e) {
                }
                Log.e(TAG, "Invalid display size: " + displaySize);
            }
        }
        Point displaySize2 = new Point();
        int i2 = SDK_INT;
        if (i2 >= 23) {
            getDisplaySizeV23(display, displaySize2);
        } else if (i2 >= 17) {
            getDisplaySizeV17(display, displaySize2);
        } else {
            getDisplaySizeV16(display, displaySize2);
        }
        return displaySize2;
    }

    public static RendererCapabilities[] getRendererCapabilities(RenderersFactory renderersFactory) {
        Renderer[] renderers = renderersFactory.createRenderers(new Handler(), new VideoRendererEventListener() { // from class: com.google.android.exoplayer2.util.Util.1
            @Override // com.google.android.exoplayer2.video.VideoRendererEventListener
            public /* synthetic */ void onDroppedFrames(int i, long j) {
                VideoRendererEventListener.CC.$default$onDroppedFrames(this, i, j);
            }

            @Override // com.google.android.exoplayer2.video.VideoRendererEventListener
            public /* synthetic */ void onRenderedFirstFrame(Surface surface) {
                VideoRendererEventListener.CC.$default$onRenderedFirstFrame(this, surface);
            }

            @Override // com.google.android.exoplayer2.video.VideoRendererEventListener
            public /* synthetic */ void onVideoDecoderInitialized(String str, long j, long j2) {
                VideoRendererEventListener.CC.$default$onVideoDecoderInitialized(this, str, j, j2);
            }

            @Override // com.google.android.exoplayer2.video.VideoRendererEventListener
            public /* synthetic */ void onVideoDisabled(DecoderCounters decoderCounters) {
                VideoRendererEventListener.CC.$default$onVideoDisabled(this, decoderCounters);
            }

            @Override // com.google.android.exoplayer2.video.VideoRendererEventListener
            public /* synthetic */ void onVideoEnabled(DecoderCounters decoderCounters) {
                VideoRendererEventListener.CC.$default$onVideoEnabled(this, decoderCounters);
            }

            @Override // com.google.android.exoplayer2.video.VideoRendererEventListener
            public /* synthetic */ void onVideoInputFormatChanged(Format format) {
                VideoRendererEventListener.CC.$default$onVideoInputFormatChanged(this, format);
            }

            @Override // com.google.android.exoplayer2.video.VideoRendererEventListener
            public /* synthetic */ void onVideoSizeChanged(int i, int i2, int i3, float f) {
                VideoRendererEventListener.CC.$default$onVideoSizeChanged(this, i, i2, i3, f);
            }
        }, new AudioRendererEventListener() { // from class: com.google.android.exoplayer2.util.Util.2
            @Override // com.google.android.exoplayer2.audio.AudioRendererEventListener
            public /* synthetic */ void onAudioDecoderInitialized(String str, long j, long j2) {
                AudioRendererEventListener.CC.$default$onAudioDecoderInitialized(this, str, j, j2);
            }

            @Override // com.google.android.exoplayer2.audio.AudioRendererEventListener
            public /* synthetic */ void onAudioDisabled(DecoderCounters decoderCounters) {
                AudioRendererEventListener.CC.$default$onAudioDisabled(this, decoderCounters);
            }

            @Override // com.google.android.exoplayer2.audio.AudioRendererEventListener
            public /* synthetic */ void onAudioEnabled(DecoderCounters decoderCounters) {
                AudioRendererEventListener.CC.$default$onAudioEnabled(this, decoderCounters);
            }

            @Override // com.google.android.exoplayer2.audio.AudioRendererEventListener
            public /* synthetic */ void onAudioInputFormatChanged(Format format) {
                AudioRendererEventListener.CC.$default$onAudioInputFormatChanged(this, format);
            }

            @Override // com.google.android.exoplayer2.audio.AudioRendererEventListener
            public /* synthetic */ void onAudioSessionId(int i) {
                AudioRendererEventListener.CC.$default$onAudioSessionId(this, i);
            }

            @Override // com.google.android.exoplayer2.audio.AudioRendererEventListener
            public /* synthetic */ void onAudioSinkUnderrun(int i, long j, long j2) {
                AudioRendererEventListener.CC.$default$onAudioSinkUnderrun(this, i, j, j2);
            }
        }, Util$$ExternalSyntheticLambda1.INSTANCE, Util$$ExternalSyntheticLambda0.INSTANCE, null);
        RendererCapabilities[] capabilities = new RendererCapabilities[renderers.length];
        for (int i = 0; i < renderers.length; i++) {
            capabilities[i] = renderers[i].getCapabilities();
        }
        return capabilities;
    }

    public static /* synthetic */ void lambda$getRendererCapabilities$1(List cues) {
    }

    public static /* synthetic */ void lambda$getRendererCapabilities$2(Metadata metadata) {
    }

    public static String getTrackTypeString(int trackType) {
        switch (trackType) {
            case 0:
                return "default";
            case 1:
                return "audio";
            case 2:
                return "video";
            case 3:
                return "text";
            case 4:
                return TtmlNode.TAG_METADATA;
            case 5:
                return "camera motion";
            case 6:
                return "none";
            default:
                if (trackType < 10000) {
                    return "?";
                }
                return "custom (" + trackType + ")";
        }
    }

    private static String getSystemProperty(String name) {
        try {
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            Method getMethod = systemProperties.getMethod("get", String.class);
            return (String) getMethod.invoke(systemProperties, name);
        } catch (Exception e) {
            Log.e(TAG, "Failed to read system property " + name, e);
            return null;
        }
    }

    private static void getDisplaySizeV23(Display display, Point outSize) {
        Display.Mode mode = display.getMode();
        outSize.x = mode.getPhysicalWidth();
        outSize.y = mode.getPhysicalHeight();
    }

    private static void getDisplaySizeV17(Display display, Point outSize) {
        display.getRealSize(outSize);
    }

    private static void getDisplaySizeV16(Display display, Point outSize) {
        display.getSize(outSize);
    }

    private static String[] getSystemLocales() {
        Configuration config = Resources.getSystem().getConfiguration();
        return SDK_INT >= 24 ? getSystemLocalesV24(config) : new String[]{getLocaleLanguageTag(config.locale)};
    }

    private static String[] getSystemLocalesV24(Configuration config) {
        return split(config.getLocales().toLanguageTags(), ",");
    }

    private static String getLocaleLanguageTagV21(Locale locale) {
        return locale.toLanguageTag();
    }

    private static int getMobileNetworkType(NetworkInfo networkInfo) {
        switch (networkInfo.getSubtype()) {
            case 1:
            case 2:
                return 3;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 14:
            case 15:
            case 17:
                return 4;
            case 13:
                return 5;
            case 16:
            case 19:
            default:
                return 6;
            case 18:
                return 2;
            case 20:
                return 9;
        }
    }

    private static HashMap<String, String> createIsoLanguageReplacementMap() {
        String[] iso2Languages = Locale.getISOLanguages();
        HashMap<String, String> replacedLanguages = new HashMap<>(iso2Languages.length + additionalIsoLanguageReplacements.length);
        for (String iso2 : iso2Languages) {
            try {
                String iso3 = new Locale(iso2).getISO3Language();
                if (!TextUtils.isEmpty(iso3)) {
                    replacedLanguages.put(iso3, iso2);
                }
            } catch (MissingResourceException e) {
            }
        }
        int i = 0;
        while (true) {
            String[] strArr = additionalIsoLanguageReplacements;
            if (i < strArr.length) {
                replacedLanguages.put(strArr[i], strArr[i + 1]);
                i += 2;
            } else {
                return replacedLanguages;
            }
        }
    }

    private static String maybeReplaceGrandfatheredLanguageTags(String languageTag) {
        int i = 0;
        while (true) {
            String[] strArr = isoGrandfatheredTagReplacements;
            if (i < strArr.length) {
                if (!languageTag.startsWith(strArr[i])) {
                    i += 2;
                } else {
                    return strArr[i + 1] + languageTag.substring(strArr[i].length());
                }
            } else {
                return languageTag;
            }
        }
    }
}
