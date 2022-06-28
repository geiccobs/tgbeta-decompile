package org.telegram.messenger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.CallLog;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.StateSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.webkit.MimeTypeMap;
import android.widget.EdgeEffect;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.android.internal.telephony.ITelephony;
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.Constants;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.crashes.Crashes;
import com.microsoft.appcenter.crashes.ingestion.models.ErrorAttachmentLog;
import com.microsoft.appcenter.distribute.Distribute;
import com.microsoft.appcenter.distribute.DistributeConstants;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.ForegroundColorSpanThemable;
import org.telegram.ui.Components.ForegroundDetector;
import org.telegram.ui.Components.HideViewAfterAnimation;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.PickerBottomLayout;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.ThemePreviewActivity;
import org.telegram.ui.WallpapersListActivity;
/* loaded from: classes.dex */
public class AndroidUtilities {
    public static Pattern BAD_CHARS_MESSAGE_LONG_PATTERN = null;
    public static Pattern BAD_CHARS_MESSAGE_PATTERN = null;
    public static Pattern BAD_CHARS_PATTERN = null;
    public static final int DARK_STATUS_BAR_OVERLAY = 855638016;
    public static final int FLAG_TAG_ALL = 11;
    public static final int FLAG_TAG_BOLD = 2;
    public static final int FLAG_TAG_BR = 1;
    public static final int FLAG_TAG_COLOR = 4;
    public static final int FLAG_TAG_URL = 8;
    public static final int LIGHT_STATUS_BAR_OVERLAY = 251658240;
    public static final String STICKERS_PLACEHOLDER_PACK_NAME = "tg_placeholders_android";
    public static final String TYPEFACE_ROBOTO_MEDIUM = "fonts/rmedium.ttf";
    public static Pattern WEB_URL;
    private static AccessibilityManager accessibilityManager;
    private static RectF bitmapRect;
    private static CallReceiver callReceiver;
    private static char[] characters;
    private static HashSet<Character> charactersMap;
    private static int[] documentIcons;
    private static int[] documentMediaIcons;
    public static boolean firstConfigurationWas;
    private static WeakReference<BaseFragment> flagSecureFragment;
    private static final HashMap<Window, ArrayList<Long>> flagSecureReasons;
    private static SimpleDateFormat generatingVideoPathFormat;
    private static boolean hasCallPermissions;
    public static boolean incorrectDisplaySizeFix;
    public static boolean isInMultiwindow;
    private static long lastUpdateCheckTime;
    public static int leftBaseline;
    private static Field mAttachInfoField;
    private static Field mStableInsetsField;
    private static HashMap<Window, ValueAnimator> navigationBarColorAnimators;
    public static final String[] numbersSignatureArray;
    public static int roundMessageInset;
    public static int roundMessageSize;
    private static Paint roundPaint;
    public static int roundPlayingMessageSize;
    public static final Linkify.MatchFilter sUrlMatchFilter;
    public static float touchSlop;
    private static Runnable unregisterRunnable;
    public static boolean usingHardwareInput;
    private static Vibrator vibrator;
    private static final Hashtable<String, Typeface> typefaceCache = new Hashtable<>();
    private static int prevOrientation = -10;
    private static boolean waitingForSms = false;
    private static boolean waitingForCall = false;
    private static final Object smsLock = new Object();
    private static final Object callLock = new Object();
    public static int statusBarHeight = 0;
    public static int navigationBarHeight = 0;
    public static float density = 1.0f;
    public static Point displaySize = new Point();
    public static float screenRefreshRate = 60.0f;
    public static Integer photoSize = null;
    public static DisplayMetrics displayMetrics = new DisplayMetrics();
    public static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    public static AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
    public static OvershootInterpolator overshootInterpolator = new OvershootInterpolator();
    private static Boolean isTablet = null;
    private static Boolean isSmallScreen = null;
    private static int adjustOwnerClassGuid = 0;
    private static int altFocusableClassGuid = 0;
    public static final RectF rectTmp = new RectF();
    public static final Rect rectTmp2 = new Rect();
    private static Pattern singleTagPatter = null;

    /* loaded from: classes4.dex */
    public interface IntColorCallback {
        void run(int i);
    }

    static {
        WEB_URL = null;
        BAD_CHARS_PATTERN = null;
        BAD_CHARS_MESSAGE_PATTERN = null;
        BAD_CHARS_MESSAGE_LONG_PATTERN = null;
        try {
            BAD_CHARS_PATTERN = Pattern.compile("[─-◿]");
            BAD_CHARS_MESSAGE_LONG_PATTERN = Pattern.compile("[̀-ͯ\u2066-\u2067]+");
            BAD_CHARS_MESSAGE_PATTERN = Pattern.compile("[\u2066-\u2067]+");
            Pattern IP_ADDRESS = Pattern.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9]))");
            Pattern DOMAIN_NAME = Pattern.compile("(([a-zA-Z0-9 -\ud7ff豈-\ufdcfﷰ-\uffef]([a-zA-Z0-9 -\ud7ff豈-\ufdcfﷰ-\uffef\\-]{0,61}[a-zA-Z0-9 -\ud7ff豈-\ufdcfﷰ-\uffef]){0,1}\\.)+[a-zA-Z -\ud7ff豈-\ufdcfﷰ-\uffef]{2,63}|" + IP_ADDRESS + ")");
            WEB_URL = Pattern.compile("((?:(http|https|Http|Https|ton|tg):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?(?:" + DOMAIN_NAME + ")(?:\\:\\d{1,5})?)(\\/(?:(?:[a-zA-Z0-9 -\ud7ff豈-\ufdcfﷰ-\uffef\\;\\/\\?\\:\\@\\&\\=\\#\\~\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?(?:\\b|$)");
        } catch (Exception e) {
            FileLog.e(e);
        }
        leftBaseline = isTablet() ? 80 : 72;
        checkDisplaySize(ApplicationLoader.applicationContext, null);
        documentIcons = new int[]{org.telegram.messenger.beta.R.drawable.media_doc_blue, org.telegram.messenger.beta.R.drawable.media_doc_green, org.telegram.messenger.beta.R.drawable.media_doc_red, org.telegram.messenger.beta.R.drawable.media_doc_yellow};
        documentMediaIcons = new int[]{org.telegram.messenger.beta.R.drawable.media_doc_blue_b, org.telegram.messenger.beta.R.drawable.media_doc_green_b, org.telegram.messenger.beta.R.drawable.media_doc_red_b, org.telegram.messenger.beta.R.drawable.media_doc_yellow_b};
        sUrlMatchFilter = AndroidUtilities$$ExternalSyntheticLambda8.INSTANCE;
        hasCallPermissions = Build.VERSION.SDK_INT >= 23;
        numbersSignatureArray = new String[]{"", "K", "M", "G", ExifInterface.GPS_DIRECTION_TRUE, "P"};
        flagSecureReasons = new HashMap<>();
        characters = new char[]{160, ' ', '!', '\"', '#', '%', '&', '\'', '(', ')', '*', ',', '-', '.', '/', ':', ';', '?', '@', '[', '\\', ']', '_', '{', '}', 161, 167, 171, 182, 183, 187, 191, 894, 903, 1370, 1371, 1372, 1373, 1374, 1375, 1417, 1418, 1470, 1472, 1475, 1478, 1523, 1524, 1545, 1546, 1548, 1549, 1563, 1566, 1567, 1642, 1643, 1644, 1645, 1748, 1792, 1793, 1794, 1795, 1796, 1797, 1798, 1799, 1800, 1801, 1802, 1803, 1804, 1805, 2039, 2040, 2041, 2096, 2097, 2098, 2099, 2100, 2101, 2102, 2103, 2104, 2105, 2106, 2107, 2108, 2109, 2110, 2142, 2404, 2405, 2416, 2557, 2678, 2800, 3191, 3204, 3572, 3663, 3674, 3675, 3844, 3845, 3846, 3847, 3848, 3849, 3850, 3851, 3852, 3853, 3854, 3855, 3856, 3857, 3858, 3860, 3898, 3899, 3900, 3901, 3973, 4048, 4049, 4050, 4051, 4052, 4057, 4058, 4170, 4171, 4172, 4173, 4174, 4175, 4347, 4960, 4961, 4962, 4963, 4964, 4965, 4966, 4967, 4968, 5120, 5742, 5787, 5788, 5867, 5868, 5869, 5941, 5942, 6100, 6101, 6102, 6104, 6105, 6106, 6144, 6145, 6146, 6147, 6148, 6149, 6150, 6151, 6152, 6153, 6154, 6468, 6469, 6686, 6687, 6816, 6817, 6818, 6819, 6820, 6821, 6822, 6824, 6825, 6826, 6827, 6828, 6829, 7002, 7003, 7004, 7005, 7006, 7007, 7008, 7164, 7165, 7166, 7167, 7227, 7228, 7229, 7230, 7231, 7294, 7295, 7360, 7361, 7362, 7363, 7364, 7365, 7366, 7367, 7379, 8208, 8209, 8210, 8211, 8212, 8213, 8214, 8215, 8216, 8217, 8218, 8219, 8220, 8221, 8222, 8223, 8224, 8225, 8226, 8227, 8228, 8229, 8230, 8231, 8240, 8241, 8242, 8243, 8244, 8245, 8246, 8247, 8248, 8249, 8250, 8251, 8252, 8253, 8254, 8255, 8256, 8257, 8258, 8259, 8261, 8262, 8263, 8264, 8265, 8266, 8267, 8268, 8269, 8270, 8271, 8272, 8273, 8275, 8276, 8277, 8278, 8279, 8280, 8281, 8282, 8283, 8284, 8285, 8286, 8317, 8318, 8333, 8334, 8968, 8969, 8970, 8971, 9001, 9002, 10088, 10089, 10090, 10091, 10092, 10093, 10094, 10095, 10096, 10097, 10098, 10099, 10100, 10101, 10181, 10182, 10214, 10215, 10216, 10217, 10218, 10219, 10220, 10221, 10222, 10223, 10627, 10628, 10629, 10630, 10631, 10632, 10633, 10634, 10635, 10636, 10637, 10638, 10639, 10640, 10641, 10642, 10643, 10644, 10645, 10646, 10647, 10648, 10712, 10713, 10714, 10715, 10748, 10749, 11513, 11514, 11515, 11516, 11518, 11519, 11632, 11776, 11777, 11778, 11779, 11780, 11781, 11782, 11783, 11784, 11785, 11786, 11787, 11788, 11789, 11790, 11791, 11792, 11793, 11794, 11795, 11796, 11797, 11798, 11799, 11800, 11801, 11802, 11803, 11804, 11805, 11806, 11807, 11808, 11809, 11810, 11811, 11812, 11813, 11814, 11815, 11816, 11817, 11818, 11819, 11820, 11821, 11822, 11824, 11825, 11826, 11827, 11828, 11829, 11830, 11831, 11832, 11833, 11834, 11835, 11836, 11837, 11838, 11839, 11840, 11841, 11842, 11843, 11844, 11845, 11846, 11847, 11848, 11849, 11850, 11851, 11852, 11853, 11854, 11855, 12289, 12290, 12291, 12296, 12297, 12298, 12299, 12300, 12301, 12302, 12303, 12304, 12305, 12308, 12309, 12310, 12311, 12312, 12313, 12314, 12315, 12316, 12317, 12318, 12319, 12336, 12349, 12448, 12539, 42238, 42239, 42509, 42510, 42511, 42611, 42622, 42738, 42739, 42740, 42741, 42742, 42743, 43124, 43125, 43126, 43127, 43214, 43215, 43256, 43257, 43258, 43260, 43310, 43311, 43359, 43457, 43458, 43459, 43460, 43461, 43462, 43463, 43464, 43465, 43466, 43467, 43468, 43469, 43486, 43487, 43612, 43613, 43614, 43615, 43742, 43743, 43760, 43761, 44011, 64830, 64831, 65040, 65041, 65042, 65043, 65044, 65045, 65046, 65047, 65048, 65049, 65072, 65073, 65074, 65075, 65076, 65077, 65078, 65079, 65080, 65081, 65082, 65083, 65084, 65085, 65086, 65087, 65088, 65089, 65090, 65091, 65092, 65093, 65094, 65095, 65096, 65097, 65098, 65099, 65100, 65101, 65102, 65103, 65104, 65105, 65106, 65108, 65109, 65110, 65111, 65112, 65113, 65114, 65115, 65116, 65117, 65118, 65119, 65120, 65121, 65123, 65128, 65130, 65131, 65281, 65282, 65283, 65285, 65286, 65287, 65288, 65289, 65290, 65292, 65293, 65294, 65295, 65306, 65307, 65311, 65312, 65339, 65340, 65341, 65343, 65371, 65373, 65375, 65376, 65377, 65378, 65379, 65380, 65381};
    }

    private static boolean containsUnsupportedCharacters(String text) {
        if (!text.contains("\u202c") && !text.contains("\u202d") && !text.contains("\u202e")) {
            try {
                return BAD_CHARS_PATTERN.matcher(text).find();
            } catch (Throwable th) {
                return true;
            }
        }
        return true;
    }

    public static String getSafeString(String str) {
        try {
            return BAD_CHARS_MESSAGE_PATTERN.matcher(str).replaceAll("\u200c");
        } catch (Throwable th) {
            return str;
        }
    }

    public static CharSequence ellipsizeCenterEnd(CharSequence str, String query, int availableWidth, TextPaint textPaint, int maxSymbols) {
        CharSequence str2;
        Exception e;
        CharSequence charSequence;
        int startHighlightedIndex;
        StaticLayout staticLayout;
        float endOfTextX;
        CharSequence sub;
        CharSequence str3;
        try {
            int lastIndex = str.length();
            try {
                int startHighlightedIndex2 = str.toString().toLowerCase().indexOf(query);
                if (lastIndex > maxSymbols) {
                    charSequence = str;
                    try {
                        str3 = charSequence.subSequence(Math.max(0, startHighlightedIndex2 - (maxSymbols / 2)), Math.min(lastIndex, (maxSymbols / 2) + startHighlightedIndex2));
                    } catch (Exception e2) {
                        e = e2;
                        str2 = charSequence;
                        FileLog.e(e);
                        return str2;
                    }
                    try {
                        int startHighlightedIndex3 = startHighlightedIndex2 - Math.max(0, startHighlightedIndex2 - (maxSymbols / 2));
                        str3.length();
                        startHighlightedIndex = startHighlightedIndex3;
                        str2 = str3;
                    } catch (Exception e3) {
                        e = e3;
                        str2 = str3;
                        FileLog.e(e);
                        return str2;
                    }
                } else {
                    startHighlightedIndex = startHighlightedIndex2;
                    str2 = str;
                }
            } catch (Exception e4) {
                e = e4;
                charSequence = str;
            }
        } catch (Exception e5) {
            e = e5;
            charSequence = str;
        }
        try {
            staticLayout = new StaticLayout(str2, textPaint, Integer.MAX_VALUE, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            endOfTextX = staticLayout.getLineWidth(0);
        } catch (Exception e6) {
            e = e6;
            FileLog.e(e);
            return str2;
        }
        if (textPaint.measureText("...") + endOfTextX < availableWidth) {
            return str2;
        }
        int i = startHighlightedIndex + 1;
        while (i < str2.length() - 1 && !Character.isWhitespace(str2.charAt(i))) {
            i++;
        }
        int endHighlightedIndex = i;
        float endOfHighlight = staticLayout.getPrimaryHorizontal(endHighlightedIndex);
        if (staticLayout.isRtlCharAt(endHighlightedIndex)) {
            endOfHighlight = endOfTextX - endOfHighlight;
        }
        if (endOfHighlight >= availableWidth) {
            float x = (endOfHighlight - availableWidth) + (textPaint.measureText("...") * 2.0f) + (availableWidth * 0.1f);
            if (str2.length() - endHighlightedIndex > 20) {
                x += availableWidth * 0.1f;
            }
            if (x > 0.0f) {
                int charOf = staticLayout.getOffsetForHorizontal(0, x);
                int k = 0;
                if (charOf > str2.length() - 1) {
                    charOf = str2.length() - 1;
                }
                while (true) {
                    float endOfTextX2 = endOfTextX;
                    if (Character.isWhitespace(str2.charAt(charOf)) || k >= 10) {
                        break;
                    }
                    k++;
                    charOf++;
                    if (charOf <= str2.length() - 1) {
                        endOfTextX = endOfTextX2;
                    } else {
                        charOf = staticLayout.getOffsetForHorizontal(0, x);
                        break;
                    }
                }
                if (k >= 10) {
                    sub = str2.subSequence(staticLayout.getOffsetForHorizontal(0, staticLayout.getPrimaryHorizontal(startHighlightedIndex + 1) - (availableWidth * 0.3f)), str2.length());
                } else {
                    if (charOf > 0 && charOf < str2.length() - 2 && Character.isWhitespace(str2.charAt(charOf))) {
                        charOf++;
                    }
                    sub = str2.subSequence(charOf, str2.length());
                }
                return SpannableStringBuilder.valueOf("...").append(sub);
            }
            return str2;
        }
        return str2;
    }

    public static CharSequence highlightText(CharSequence str, ArrayList<String> query, Theme.ResourcesProvider resourcesProvider) {
        if (query == null) {
            return null;
        }
        int emptyCount = 0;
        for (int i = 0; i < query.size(); i++) {
            CharSequence strTmp = highlightText(str, query.get(i), resourcesProvider);
            if (strTmp != null) {
                str = strTmp;
            } else {
                emptyCount++;
            }
        }
        int i2 = query.size();
        if (emptyCount == i2) {
            return null;
        }
        return str;
    }

    public static CharSequence highlightText(CharSequence str, String query, Theme.ResourcesProvider resourcesProvider) {
        if (TextUtils.isEmpty(query) || TextUtils.isEmpty(str)) {
            return null;
        }
        String s = str.toString().toLowerCase();
        SpannableStringBuilder spannableStringBuilder = SpannableStringBuilder.valueOf(str);
        int i = s.indexOf(query);
        while (i >= 0) {
            try {
                spannableStringBuilder.setSpan(new ForegroundColorSpanThemable(Theme.key_windowBackgroundWhiteBlueText4, resourcesProvider), i, Math.min(query.length() + i, str.length()), 0);
            } catch (Exception e) {
                FileLog.e(e);
            }
            i = s.indexOf(query, i + 1);
        }
        return spannableStringBuilder;
    }

    public static Activity findActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            return findActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    public static CharSequence replaceSingleTag(String str, final Runnable runnable) {
        int startIndex = str.indexOf("**");
        int endIndex = str.indexOf("**", startIndex + 1);
        String str2 = str.replace("**", "");
        int index = -1;
        int len = 0;
        if (startIndex >= 0 && endIndex >= 0 && endIndex - startIndex > 2) {
            len = (endIndex - startIndex) - 2;
            index = startIndex;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str2);
        if (index >= 0) {
            spannableStringBuilder.setSpan(new ClickableSpan() { // from class: org.telegram.messenger.AndroidUtilities.1
                @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }

                @Override // android.text.style.ClickableSpan
                public void onClick(View view) {
                    runnable.run();
                }
            }, index, index + len, 0);
        }
        return spannableStringBuilder;
    }

    public static void recycleBitmaps(final ArrayList<Bitmap> bitmapToRecycle) {
        if (bitmapToRecycle != null && !bitmapToRecycle.isEmpty()) {
            runOnUIThread(new Runnable() { // from class: org.telegram.messenger.AndroidUtilities$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationCenter.getInstance(UserConfig.selectedAccount).doOnIdle(new Runnable() { // from class: org.telegram.messenger.AndroidUtilities$$ExternalSyntheticLambda13
                        @Override // java.lang.Runnable
                        public final void run() {
                            AndroidUtilities.lambda$recycleBitmaps$0(r1);
                        }
                    });
                }
            }, 36L);
        }
    }

    public static /* synthetic */ void lambda$recycleBitmaps$0(ArrayList bitmapToRecycle) {
        for (int i = 0; i < bitmapToRecycle.size(); i++) {
            Bitmap bitmap = (Bitmap) bitmapToRecycle.get(i);
            if (bitmap != null && !bitmap.isRecycled()) {
                try {
                    bitmap.recycle();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class LinkSpec {
        int end;
        int start;
        String url;

        private LinkSpec() {
        }
    }

    private static String makeUrl(String url, String[] prefixes, Matcher matcher) {
        boolean hasPrefix = false;
        int i = 0;
        while (true) {
            if (i >= prefixes.length) {
                break;
            } else if (!url.regionMatches(true, 0, prefixes[i], 0, prefixes[i].length())) {
                i++;
            } else {
                hasPrefix = true;
                if (!url.regionMatches(false, 0, prefixes[i], 0, prefixes[i].length())) {
                    url = prefixes[i] + url.substring(prefixes[i].length());
                }
            }
        }
        if (!hasPrefix && prefixes.length > 0) {
            return prefixes[0] + url;
        }
        return url;
    }

    private static void gatherLinks(ArrayList<LinkSpec> links, Spannable s, Pattern pattern, String[] schemes, Linkify.MatchFilter matchFilter, boolean internalOnly) {
        if (TextUtils.indexOf((CharSequence) s, (char) 9472) >= 0) {
            s = new SpannableStringBuilder(s.toString().replace((char) 9472, ' '));
        }
        Matcher m = pattern.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            if (matchFilter == null || matchFilter.acceptMatch(s, start, end)) {
                LinkSpec spec = new LinkSpec();
                String url = makeUrl(m.group(0), schemes, m);
                if (!internalOnly || Browser.isInternalUrl(url, true, null)) {
                    spec.url = url;
                    spec.start = start;
                    spec.end = end;
                    links.add(spec);
                }
            }
        }
    }

    public static /* synthetic */ boolean lambda$static$2(CharSequence s, int start, int end) {
        if (start == 0 || s.charAt(start - 1) != '@') {
            return true;
        }
        return false;
    }

    public static boolean addLinks(Spannable text, int mask) {
        return addLinks(text, mask, false);
    }

    public static boolean addLinks(Spannable text, int mask, boolean internalOnly) {
        if (text == null || containsUnsupportedCharacters(text.toString()) || mask == 0) {
            return false;
        }
        URLSpan[] old = (URLSpan[]) text.getSpans(0, text.length(), URLSpan.class);
        for (int i = old.length - 1; i >= 0; i--) {
            text.removeSpan(old[i]);
        }
        ArrayList<LinkSpec> links = new ArrayList<>();
        if (!internalOnly && (mask & 4) != 0) {
            Linkify.addLinks(text, 4);
        }
        if ((mask & 1) != 0) {
            gatherLinks(links, text, LinkifyPort.WEB_URL, new String[]{"http://", "https://", "tg://"}, sUrlMatchFilter, internalOnly);
        }
        pruneOverlaps(links);
        if (links.size() == 0) {
            return false;
        }
        int N = links.size();
        for (int a = 0; a < N; a++) {
            LinkSpec link = links.get(a);
            URLSpan[] oldSpans = (URLSpan[]) text.getSpans(link.start, link.end, URLSpan.class);
            if (oldSpans != null && oldSpans.length > 0) {
                for (URLSpan uRLSpan : oldSpans) {
                    text.removeSpan(uRLSpan);
                }
            }
            text.setSpan(new URLSpan(link.url), link.start, link.end, 33);
        }
        return true;
    }

    private static void pruneOverlaps(ArrayList<LinkSpec> links) {
        Comparator<LinkSpec> c = AndroidUtilities$$ExternalSyntheticLambda2.INSTANCE;
        Collections.sort(links, c);
        int len = links.size();
        int i = 0;
        while (i < len - 1) {
            LinkSpec a = links.get(i);
            LinkSpec b = links.get(i + 1);
            int remove = -1;
            if (a.start <= b.start && a.end > b.start) {
                if (b.end <= a.end) {
                    remove = i + 1;
                } else if (a.end - a.start > b.end - b.start) {
                    remove = i + 1;
                } else if (a.end - a.start < b.end - b.start) {
                    remove = i;
                }
                if (remove != -1) {
                    links.remove(remove);
                    len--;
                }
            }
            i++;
        }
    }

    public static /* synthetic */ int lambda$pruneOverlaps$3(LinkSpec a, LinkSpec b) {
        if (a.start < b.start) {
            return -1;
        }
        if (a.start > b.start || a.end < b.end) {
            return 1;
        }
        return a.end > b.end ? -1 : 0;
    }

    public static void fillStatusBarHeight(Context context) {
        if (context == null || statusBarHeight > 0) {
            return;
        }
        statusBarHeight = getStatusBarHeight(context);
        navigationBarHeight = getNavigationBarHeight(context);
    }

    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private static int getNavigationBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int getThumbForNameOrMime(String name, String mime, boolean media) {
        if (name == null || name.length() == 0) {
            return media ? documentMediaIcons[0] : documentIcons[0];
        }
        int color = -1;
        if (name.contains(".doc") || name.contains(".txt") || name.contains(".psd")) {
            color = 0;
        } else if (name.contains(".xls") || name.contains(".csv")) {
            color = 1;
        } else if (name.contains(".pdf") || name.contains(".ppt") || name.contains(".key")) {
            color = 2;
        } else if (name.contains(".zip") || name.contains(".rar") || name.contains(".ai") || name.contains(DefaultHlsExtractorFactory.MP3_FILE_EXTENSION) || name.contains(".mov") || name.contains(".avi")) {
            color = 3;
        }
        if (color == -1) {
            int idx = name.lastIndexOf(46);
            String ext = idx == -1 ? "" : name.substring(idx + 1);
            if (ext.length() != 0) {
                color = ext.charAt(0) % documentIcons.length;
            } else {
                color = name.charAt(0) % documentIcons.length;
            }
        }
        return media ? documentMediaIcons[color] : documentIcons[color];
    }

    public static int calcBitmapColor(Bitmap bitmap) {
        try {
            Bitmap b = Bitmaps.createScaledBitmap(bitmap, 1, 1, true);
            if (b != null) {
                int bitmapColor = b.getPixel(0, 0);
                if (bitmap != b) {
                    b.recycle();
                }
                return bitmapColor;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return 0;
    }

    public static int[] calcDrawableColor(Drawable drawable) {
        int bitmapColor = -16777216;
        int[] result = new int[4];
        try {
            if (!(drawable instanceof BitmapDrawable)) {
                if (drawable instanceof ColorDrawable) {
                    bitmapColor = ((ColorDrawable) drawable).getColor();
                } else if (drawable instanceof BackgroundGradientDrawable) {
                    int[] colors = ((BackgroundGradientDrawable) drawable).getColorsList();
                    if (colors != null) {
                        if (colors.length > 1) {
                            bitmapColor = getAverageColor(colors[0], colors[1]);
                        } else if (colors.length > 0) {
                            bitmapColor = colors[0];
                        }
                    }
                } else if (drawable instanceof MotionBackgroundDrawable) {
                    int argb = Color.argb(45, 0, 0, 0);
                    result[2] = argb;
                    result[0] = argb;
                    int argb2 = Color.argb(61, 0, 0, 0);
                    result[3] = argb2;
                    result[1] = argb2;
                    return result;
                }
            } else {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                bitmapColor = calcBitmapColor(bitmap);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        double[] hsv = rgbToHsv((bitmapColor >> 16) & 255, (bitmapColor >> 8) & 255, bitmapColor & 255);
        hsv[1] = Math.min(1.0d, hsv[1] + 0.05d + ((1.0d - hsv[1]) * 0.1d));
        double v = Math.max((double) FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, hsv[2] * 0.65d);
        int[] rgb = hsvToRgb(hsv[0], hsv[1], v);
        result[0] = Color.argb(102, rgb[0], rgb[1], rgb[2]);
        result[1] = Color.argb(136, rgb[0], rgb[1], rgb[2]);
        double v2 = Math.max((double) FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, hsv[2] * 0.72d);
        int[] rgb2 = hsvToRgb(hsv[0], hsv[1], v2);
        result[2] = Color.argb(102, rgb2[0], rgb2[1], rgb2[2]);
        result[3] = Color.argb(136, rgb2[0], rgb2[1], rgb2[2]);
        return result;
    }

    public static double[] rgbToHsv(int color) {
        return rgbToHsv(Color.red(color), Color.green(color), Color.blue(color));
    }

    public static double[] rgbToHsv(int r, int g, int b) {
        double h;
        double h2;
        double d = r;
        Double.isNaN(d);
        double rf = d / 255.0d;
        double d2 = g;
        Double.isNaN(d2);
        double gf = d2 / 255.0d;
        double d3 = b;
        Double.isNaN(d3);
        double bf = d3 / 255.0d;
        double max = (rf <= gf || rf <= bf) ? Math.max(gf, bf) : rf;
        double min = (rf >= gf || rf >= bf) ? Math.min(gf, bf) : rf;
        double d4 = max - min;
        double s = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        if (max != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
            s = d4 / max;
        }
        if (max == min) {
            h = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        } else {
            if (rf > gf && rf > bf) {
                double d5 = (gf - bf) / d4;
                double min2 = gf < bf ? 6 : 0;
                Double.isNaN(min2);
                h2 = d5 + min2;
            } else if (gf > bf) {
                h2 = ((bf - rf) / d4) + 2.0d;
            } else {
                h2 = ((rf - gf) / d4) + 4.0d;
            }
            h = h2 / 6.0d;
        }
        return new double[]{h, s, max};
    }

    public static int hsvToColor(double h, double s, double v) {
        int[] rgb = hsvToRgb(h, s, v);
        return Color.argb(255, rgb[0], rgb[1], rgb[2]);
    }

    public static int[] hsvToRgb(double h, double s, double v) {
        double r;
        double g = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        double b = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        double i = (int) Math.floor(h * 6.0d);
        Double.isNaN(i);
        double f = (6.0d * h) - i;
        double p = (1.0d - s) * v;
        double q = (1.0d - (f * s)) * v;
        double t = (1.0d - ((1.0d - f) * s)) * v;
        switch (((int) i) % 6) {
            case 0:
                r = v;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = v;
                b = p;
                break;
            case 2:
                r = p;
                g = v;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = v;
                break;
            case 4:
                r = t;
                g = p;
                b = v;
                break;
            case 5:
                r = v;
                g = p;
                b = q;
                break;
            default:
                r = 0.0d;
                break;
        }
        return new int[]{(int) (r * 255.0d), (int) (g * 255.0d), (int) (b * 255.0d)};
    }

    public static void adjustSaturationColorMatrix(ColorMatrix colorMatrix, float saturation) {
        if (colorMatrix == null) {
            return;
        }
        float x = saturation + 1.0f;
        colorMatrix.postConcat(new ColorMatrix(new float[]{((1.0f - x) * 0.3086f) + x, (1.0f - x) * 0.6094f, (1.0f - x) * 0.082f, 0.0f, 0.0f, (1.0f - x) * 0.3086f, ((1.0f - x) * 0.6094f) + x, (1.0f - x) * 0.082f, 0.0f, 0.0f, (1.0f - x) * 0.3086f, (1.0f - x) * 0.6094f, ((1.0f - x) * 0.082f) + x, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}));
    }

    public static void adjustBrightnessColorMatrix(ColorMatrix colorMatrix, float brightness) {
        if (colorMatrix == null) {
            return;
        }
        float brightness2 = brightness * 255.0f;
        colorMatrix.postConcat(new ColorMatrix(new float[]{1.0f, 0.0f, 0.0f, 0.0f, brightness2, 0.0f, 1.0f, 0.0f, 0.0f, brightness2, 0.0f, 0.0f, 1.0f, 0.0f, brightness2, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}));
    }

    public static void multiplyBrightnessColorMatrix(ColorMatrix colorMatrix, float v) {
        if (colorMatrix == null) {
            return;
        }
        colorMatrix.postConcat(new ColorMatrix(new float[]{v, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, v, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, v, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}));
    }

    public static Bitmap snapshotView(View v) {
        Bitmap bm = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        v.draw(canvas);
        int[] loc = new int[2];
        v.getLocationInWindow(loc);
        snapshotTextureViews(loc[0], loc[1], loc, canvas, v);
        return bm;
    }

    private static void snapshotTextureViews(int rootX, int rootY, int[] loc, Canvas canvas, View v) {
        if (v instanceof TextureView) {
            TextureView tv = (TextureView) v;
            tv.getLocationInWindow(loc);
            Bitmap textureSnapshot = tv.getBitmap();
            if (textureSnapshot != null) {
                canvas.save();
                canvas.drawBitmap(textureSnapshot, loc[0] - rootX, loc[1] - rootY, (Paint) null);
                canvas.restore();
                textureSnapshot.recycle();
            }
        }
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                snapshotTextureViews(rootX, rootY, loc, canvas, vg.getChildAt(i));
            }
        }
    }

    public static void requestAltFocusable(Activity activity, int classGuid) {
        if (activity == null) {
            return;
        }
        activity.getWindow().setFlags(131072, 131072);
        altFocusableClassGuid = classGuid;
    }

    public static void removeAltFocusable(Activity activity, int classGuid) {
        if (activity != null && altFocusableClassGuid == classGuid) {
            activity.getWindow().clearFlags(131072);
        }
    }

    public static void requestAdjustResize(Activity activity, int classGuid) {
        if (activity == null) {
            return;
        }
        requestAdjustResize(activity.getWindow(), classGuid);
    }

    public static void requestAdjustResize(Window window, int classGuid) {
        if (window == null || isTablet()) {
            return;
        }
        window.setSoftInputMode(16);
        adjustOwnerClassGuid = classGuid;
    }

    public static void requestAdjustNothing(Activity activity, int classGuid) {
        if (activity == null || isTablet()) {
            return;
        }
        activity.getWindow().setSoftInputMode(48);
        adjustOwnerClassGuid = classGuid;
    }

    public static void setAdjustResizeToNothing(Activity activity, int classGuid) {
        if (activity == null || isTablet()) {
            return;
        }
        int i = adjustOwnerClassGuid;
        if (i == 0 || i == classGuid) {
            activity.getWindow().setSoftInputMode(48);
        }
    }

    public static void removeAdjustResize(Activity activity, int classGuid) {
        if (activity != null && !isTablet() && adjustOwnerClassGuid == classGuid) {
            activity.getWindow().setSoftInputMode(32);
        }
    }

    public static void createEmptyFile(File f) {
        try {
            if (f.exists()) {
                return;
            }
            FileWriter writer = new FileWriter(f);
            writer.flush();
            writer.close();
        } catch (Throwable e) {
            FileLog.e(e, false);
        }
    }

    public static boolean isGoogleMapsInstalled(final BaseFragment fragment) {
        try {
            ApplicationLoader.applicationContext.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            if (fragment.getParentActivity() == null) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getParentActivity());
            builder.setMessage(LocaleController.getString("InstallGoogleMaps", org.telegram.messenger.beta.R.string.InstallGoogleMaps));
            builder.setPositiveButton(LocaleController.getString("OK", org.telegram.messenger.beta.R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.messenger.AndroidUtilities$$ExternalSyntheticLambda7
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    AndroidUtilities.lambda$isGoogleMapsInstalled$4(BaseFragment.this, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", org.telegram.messenger.beta.R.string.Cancel), null);
            fragment.showDialog(builder.create());
            return false;
        }
    }

    public static /* synthetic */ void lambda$isGoogleMapsInstalled$4(BaseFragment fragment, DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.google.android.apps.maps"));
            fragment.getParentActivity().startActivityForResult(intent, 500);
        } catch (Exception e1) {
            FileLog.e(e1);
        }
    }

    public static int[] toIntArray(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }

    public static boolean isInternalUri(Uri uri) {
        return isInternalUri(uri, 0);
    }

    public static boolean isInternalUri(int fd) {
        return isInternalUri(null, fd);
    }

    private static boolean isInternalUri(Uri uri, int fd) {
        String pathString;
        if (uri != null) {
            pathString = uri.getPath();
            if (pathString == null) {
                return false;
            }
            if (pathString.matches(Pattern.quote(new File(ApplicationLoader.applicationContext.getCacheDir(), "voip_logs").getAbsolutePath()) + "/\\d+\\.log")) {
                return false;
            }
            int tries = 0;
            do {
                if (pathString != null && pathString.length() > 4096) {
                    return true;
                }
                try {
                    String newPath = Utilities.readlink(pathString);
                    if (newPath != null && !newPath.equals(pathString)) {
                        pathString = newPath;
                        tries++;
                    }
                } catch (Throwable th) {
                    return true;
                }
            } while (tries < 10);
            return true;
        }
        pathString = "";
        int tries2 = 0;
        do {
            if (pathString != null && pathString.length() > 4096) {
                return true;
            }
            try {
                String newPath2 = Utilities.readlinkFd(fd);
                if (newPath2 != null && !newPath2.equals(pathString)) {
                    pathString = newPath2;
                    tries2++;
                }
            } catch (Throwable th2) {
                return true;
            }
        } while (tries2 < 10);
        return true;
        if (pathString != null) {
            try {
                String path = new File(pathString).getCanonicalPath();
                if (path != null) {
                    pathString = path;
                }
            } catch (Exception e) {
                pathString.replace("/./", "/");
            }
        }
        if (!pathString.endsWith(".attheme") && pathString != null) {
            String lowerCase = pathString.toLowerCase();
            StringBuilder sb = new StringBuilder();
            sb.append("/data/data/");
            sb.append(ApplicationLoader.applicationContext.getPackageName());
            return lowerCase.contains(sb.toString());
        }
        return false;
    }

    public static void lockOrientation(Activity activity) {
        if (activity == null || prevOrientation != -10) {
            return;
        }
        try {
            prevOrientation = activity.getRequestedOrientation();
            WindowManager manager = (WindowManager) activity.getSystemService("window");
            if (manager != null && manager.getDefaultDisplay() != null) {
                int rotation = manager.getDefaultDisplay().getRotation();
                int orientation = activity.getResources().getConfiguration().orientation;
                if (rotation == 3) {
                    if (orientation == 1) {
                        activity.setRequestedOrientation(1);
                    } else {
                        activity.setRequestedOrientation(8);
                    }
                } else if (rotation == 1) {
                    if (orientation == 1) {
                        activity.setRequestedOrientation(9);
                    } else {
                        activity.setRequestedOrientation(0);
                    }
                } else if (rotation == 0) {
                    if (orientation == 2) {
                        activity.setRequestedOrientation(0);
                    } else {
                        activity.setRequestedOrientation(1);
                    }
                } else if (orientation == 2) {
                    activity.setRequestedOrientation(8);
                } else {
                    activity.setRequestedOrientation(9);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void unlockOrientation(Activity activity) {
        if (activity == null) {
            return;
        }
        try {
            int i = prevOrientation;
            if (i != -10) {
                activity.setRequestedOrientation(i);
                prevOrientation = -10;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class VcardData {
        String name;
        ArrayList<String> phones;
        StringBuilder vcard;

        private VcardData() {
            this.phones = new ArrayList<>();
            this.vcard = new StringBuilder();
        }
    }

    /* loaded from: classes4.dex */
    public static class VcardItem {
        public int type;
        public ArrayList<String> vcardData = new ArrayList<>();
        public String fullData = "";
        public boolean checked = true;

        public String[] getRawValue() {
            byte[] bytes;
            int idx = this.fullData.indexOf(58);
            if (idx >= 0) {
                String valueType = this.fullData.substring(0, idx);
                String value = this.fullData.substring(idx + 1);
                String nameEncoding = null;
                String nameCharset = "UTF-8";
                String[] params = valueType.split(";");
                for (String str : params) {
                    String[] args2 = str.split("=");
                    if (args2.length == 2) {
                        if (args2[0].equals("CHARSET")) {
                            nameCharset = args2[1];
                        } else if (args2[0].equals("ENCODING")) {
                            nameEncoding = args2[1];
                        }
                    }
                }
                String[] args = value.split(";");
                for (int a = 0; a < args.length; a++) {
                    if (!TextUtils.isEmpty(args[a]) && nameEncoding != null && nameEncoding.equalsIgnoreCase("QUOTED-PRINTABLE") && (bytes = AndroidUtilities.decodeQuotedPrintable(AndroidUtilities.getStringBytes(args[a]))) != null && bytes.length != 0) {
                        try {
                            args[a] = new String(bytes, nameCharset);
                        } catch (Exception e) {
                        }
                    }
                }
                return args;
            }
            return new String[0];
        }

        public String getValue(boolean format) {
            byte[] bytes;
            StringBuilder result = new StringBuilder();
            int idx = this.fullData.indexOf(58);
            if (idx < 0) {
                return "";
            }
            if (result.length() > 0) {
                result.append(", ");
            }
            String valueType = this.fullData.substring(0, idx);
            String value = this.fullData.substring(idx + 1);
            String[] params = valueType.split(";");
            String nameEncoding = null;
            String nameCharset = "UTF-8";
            for (String str : params) {
                String[] args2 = str.split("=");
                if (args2.length == 2) {
                    if (args2[0].equals("CHARSET")) {
                        nameCharset = args2[1];
                    } else if (args2[0].equals("ENCODING")) {
                        nameEncoding = args2[1];
                    }
                }
            }
            String[] args = value.split(";");
            boolean added = false;
            for (int a = 0; a < args.length; a++) {
                if (!TextUtils.isEmpty(args[a])) {
                    if (nameEncoding != null && nameEncoding.equalsIgnoreCase("QUOTED-PRINTABLE") && (bytes = AndroidUtilities.decodeQuotedPrintable(AndroidUtilities.getStringBytes(args[a]))) != null && bytes.length != 0) {
                        try {
                            args[a] = new String(bytes, nameCharset);
                        } catch (Exception e) {
                        }
                    }
                    if (added && result.length() > 0) {
                        result.append(" ");
                    }
                    result.append(args[a]);
                    if (!added) {
                        added = args[a].length() > 0;
                    }
                }
            }
            if (format) {
                int i = this.type;
                if (i == 0) {
                    return PhoneFormat.getInstance().format(result.toString());
                }
                if (i == 5) {
                    String[] date = result.toString().split(ExifInterface.GPS_DIRECTION_TRUE);
                    if (date.length > 0) {
                        String[] date2 = date[0].split("-");
                        if (date2.length == 3) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(1, Utilities.parseInt((CharSequence) date2[0]).intValue());
                            calendar.set(2, Utilities.parseInt((CharSequence) date2[1]).intValue() - 1);
                            calendar.set(5, Utilities.parseInt((CharSequence) date2[2]).intValue());
                            return LocaleController.getInstance().formatterYearMax.format(calendar.getTime());
                        }
                    }
                }
            }
            return result.toString();
        }

        public String getRawType(boolean first) {
            int idx = this.fullData.indexOf(58);
            if (idx < 0) {
                return "";
            }
            String value = this.fullData.substring(0, idx);
            if (this.type == 20) {
                String[] args = value.substring(2).split(";");
                if (first) {
                    return args[0];
                }
                if (args.length > 1) {
                    return args[args.length - 1];
                }
                return "";
            }
            String[] args2 = value.split(";");
            for (int a = 0; a < args2.length; a++) {
                if (args2[a].indexOf(61) < 0) {
                    value = args2[a];
                }
            }
            return value;
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Code restructure failed: missing block: B:36:0x0099, code lost:
            if (r2.equals("WORK") != false) goto L50;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public java.lang.String getType() {
            /*
                Method dump skipped, instructions count: 326
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.VcardItem.getType():java.lang.String");
        }
    }

    public static byte[] getStringBytes(String src) {
        try {
            return src.getBytes("UTF-8");
        } catch (Exception e) {
            return new byte[0];
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(13:2|(3:183|4|5)(2:185|7)|8|(6:9|(3:11|(3:191|13|194)(9:187|14|15|(4:17|181|18|(3:20|21|22)(3:23|(3:27|(1:29)(2:30|(1:32)(3:33|(2:40|(1:42)(2:43|(1:45)(2:46|(1:48)(2:49|(2:56|(1:58)(2:59|(1:61)(2:62|(1:64))))))))|67))|(1:71))|72))|(1:(3:78|(1:80)|81)(1:82))|(2:84|85)|86|87|(7:(1:95)|96|(3:98|179|99)(1:100)|101|(1:(1:104)(9:105|106|(2:116|(1:118)(1:119))|120|(3:122|(2:125|(2:127|200)(2:128|(2:130|198)(1:201)))(2:124|199)|131)|197|132|(1:134)(1:135)|(1:143)))(1:144)|145|196)(3:192|91|195))|193)(1:188)|148|149|175|176)|146|177|147|152|(4:155|(2:170|203)(5:(1:160)|161|(2:162|(2:164|(2:206|166)(1:167))(2:205|168))|169|204)|171|153)|202|172|176|(1:(1:180))) */
    /* JADX WARN: Code restructure failed: missing block: B:150:0x031b, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:151:0x031c, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x0149, code lost:
        r0 = new org.telegram.messenger.AndroidUtilities.VcardItem();
        r0.type = 6;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.util.ArrayList<org.telegram.tgnet.TLRPC.User> loadVCardFromStream(android.net.Uri r28, int r29, boolean r30, java.util.ArrayList<org.telegram.messenger.AndroidUtilities.VcardItem> r31, java.lang.String r32) {
        /*
            Method dump skipped, instructions count: 966
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.loadVCardFromStream(android.net.Uri, int, boolean, java.util.ArrayList, java.lang.String):java.util.ArrayList");
    }

    public static Typeface getTypeface(String assetPath) {
        Typeface t;
        Typeface typeface;
        Hashtable<String, Typeface> hashtable = typefaceCache;
        synchronized (hashtable) {
            if (!hashtable.containsKey(assetPath)) {
                try {
                    if (Build.VERSION.SDK_INT >= 26) {
                        Typeface.Builder builder = new Typeface.Builder(ApplicationLoader.applicationContext.getAssets(), assetPath);
                        if (assetPath.contains(Constants.ScionAnalytics.PARAM_MEDIUM)) {
                            builder.setWeight(700);
                        }
                        if (assetPath.contains(TtmlNode.ITALIC)) {
                            builder.setItalic(true);
                        }
                        t = builder.build();
                    } else {
                        t = Typeface.createFromAsset(ApplicationLoader.applicationContext.getAssets(), assetPath);
                    }
                    hashtable.put(assetPath, t);
                } catch (Exception e) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("Could not get typeface '" + assetPath + "' because " + e.getMessage());
                    }
                    return null;
                }
            }
            typeface = hashtable.get(assetPath);
        }
        return typeface;
    }

    public static boolean isWaitingForSms() {
        boolean value;
        synchronized (smsLock) {
            value = waitingForSms;
        }
        return value;
    }

    public static void setWaitingForSms(boolean value) {
        synchronized (smsLock) {
            waitingForSms = value;
            if (value) {
                SmsRetrieverClient client = SmsRetriever.getClient(ApplicationLoader.applicationContext);
                Task<Void> task = client.startSmsRetriever();
                task.addOnSuccessListener(AndroidUtilities$$ExternalSyntheticLambda12.INSTANCE);
            }
        }
    }

    public static /* synthetic */ void lambda$setWaitingForSms$5(Void aVoid) {
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("sms listener registered");
        }
    }

    public static int getShadowHeight() {
        float f = density;
        if (f >= 4.0f) {
            return 3;
        }
        if (f >= 2.0f) {
            return 2;
        }
        return 1;
    }

    public static boolean isWaitingForCall() {
        boolean value;
        synchronized (callLock) {
            value = waitingForCall;
        }
        return value;
    }

    public static void setWaitingForCall(boolean value) {
        synchronized (callLock) {
            try {
                if (value) {
                    if (callReceiver == null) {
                        IntentFilter filter = new IntentFilter("android.intent.action.PHONE_STATE");
                        Context context = ApplicationLoader.applicationContext;
                        CallReceiver callReceiver2 = new CallReceiver();
                        callReceiver = callReceiver2;
                        context.registerReceiver(callReceiver2, filter);
                    }
                } else if (callReceiver != null) {
                    ApplicationLoader.applicationContext.unregisterReceiver(callReceiver);
                    callReceiver = null;
                }
            } catch (Exception e) {
            }
            waitingForCall = value;
        }
    }

    public static boolean showKeyboard(View view) {
        if (view == null) {
            return false;
        }
        try {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService("input_method");
            return inputManager.showSoftInput(view, 1);
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    public static String[] getCurrentKeyboardLanguage() {
        try {
            InputMethodManager inputManager = (InputMethodManager) ApplicationLoader.applicationContext.getSystemService("input_method");
            InputMethodSubtype inputMethodSubtype = inputManager.getCurrentInputMethodSubtype();
            String locale = null;
            if (inputMethodSubtype != null) {
                if (Build.VERSION.SDK_INT >= 24) {
                    locale = inputMethodSubtype.getLanguageTag();
                }
                if (TextUtils.isEmpty(locale)) {
                    locale = inputMethodSubtype.getLocale();
                }
            } else {
                InputMethodSubtype inputMethodSubtype2 = inputManager.getLastInputMethodSubtype();
                if (inputMethodSubtype2 != null) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        locale = inputMethodSubtype2.getLanguageTag();
                    }
                    if (TextUtils.isEmpty(locale)) {
                        locale = inputMethodSubtype2.getLocale();
                    }
                }
            }
            if (!TextUtils.isEmpty(locale)) {
                return new String[]{locale.replace('_', '-')};
            }
            String locale2 = LocaleController.getSystemLocaleStringIso639();
            LocaleController.LocaleInfo localeInfo = LocaleController.getInstance().getCurrentLocaleInfo();
            String locale22 = localeInfo.getBaseLangCode();
            if (TextUtils.isEmpty(locale22)) {
                locale22 = localeInfo.getLangCode();
            }
            if (locale2.contains(locale22) || locale22.contains(locale2)) {
                if (!locale2.contains("en")) {
                    locale22 = "en";
                } else {
                    locale22 = null;
                }
            }
            return !TextUtils.isEmpty(locale22) ? new String[]{locale2.replace('_', '-'), locale22} : new String[]{locale2.replace('_', '-')};
        } catch (Exception e) {
            return new String[]{"en"};
        }
    }

    public static void hideKeyboard(View view) {
        if (view == null) {
            return;
        }
        try {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService("input_method");
            if (!imm.isActive()) {
                return;
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static ArrayList<File> getDataDirs() {
        File[] dirs;
        ArrayList<File> result = null;
        if (Build.VERSION.SDK_INT >= 19 && (dirs = ApplicationLoader.applicationContext.getExternalFilesDirs(null)) != null) {
            for (int a = 0; a < dirs.length; a++) {
                if (dirs[a] != null) {
                    dirs[a].getAbsolutePath();
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(dirs[a]);
                }
            }
        }
        if (result == null) {
            result = new ArrayList<>();
        }
        if (result.isEmpty()) {
            result.add(Environment.getExternalStorageDirectory());
        }
        return result;
    }

    public static ArrayList<File> getRootDirs() {
        File[] dirs;
        String path;
        int idx;
        ArrayList<File> result = null;
        if (Build.VERSION.SDK_INT >= 19 && (dirs = ApplicationLoader.applicationContext.getExternalFilesDirs(null)) != null) {
            for (int a = 0; a < dirs.length; a++) {
                if (dirs[a] != null && (idx = (path = dirs[a].getAbsolutePath()).indexOf("/Android")) >= 0) {
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(new File(path.substring(0, idx)));
                }
            }
        }
        if (result == null) {
            result = new ArrayList<>();
        }
        if (result.isEmpty()) {
            result.add(Environment.getExternalStorageDirectory());
        }
        return result;
    }

    public static File getCacheDir() {
        File file;
        String state = null;
        try {
            state = Environment.getExternalStorageState();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (state == null || state.startsWith("mounted")) {
            try {
                if (Build.VERSION.SDK_INT >= 19) {
                    File[] dirs = ApplicationLoader.applicationContext.getExternalCacheDirs();
                    file = dirs[0];
                    if (!TextUtils.isEmpty(SharedConfig.storageCacheDir)) {
                        int a = 0;
                        while (true) {
                            if (a < dirs.length) {
                                if (dirs[a] == null || !dirs[a].getAbsolutePath().startsWith(SharedConfig.storageCacheDir)) {
                                    a++;
                                } else {
                                    file = dirs[a];
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                } else {
                    file = ApplicationLoader.applicationContext.getExternalCacheDir();
                }
                if (file != null) {
                    return file;
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        try {
            File file2 = ApplicationLoader.applicationContext.getCacheDir();
            if (file2 != null) {
                return file2;
            }
        } catch (Exception e3) {
            FileLog.e(e3);
        }
        return new File("");
    }

    public static int dp(float value) {
        if (value == 0.0f) {
            return 0;
        }
        return (int) Math.ceil(density * value);
    }

    public static int dpr(float value) {
        if (value == 0.0f) {
            return 0;
        }
        return Math.round(density * value);
    }

    public static int dp2(float value) {
        if (value == 0.0f) {
            return 0;
        }
        return (int) Math.floor(density * value);
    }

    public static int compare(int lhs, int rhs) {
        if (lhs == rhs) {
            return 0;
        }
        if (lhs > rhs) {
            return 1;
        }
        return -1;
    }

    public static int compare(long lhs, long rhs) {
        if (lhs == rhs) {
            return 0;
        }
        if (lhs > rhs) {
            return 1;
        }
        return -1;
    }

    public static float dpf2(float value) {
        if (value == 0.0f) {
            return 0.0f;
        }
        return density * value;
    }

    public static void checkDisplaySize(Context context, Configuration newConfiguration) {
        Display display;
        try {
            float oldDensity = density;
            float newDensity = context.getResources().getDisplayMetrics().density;
            density = newDensity;
            if (firstConfigurationWas && Math.abs(oldDensity - newDensity) > 0.001d) {
                Theme.reloadAllResources(context);
            }
            boolean z = true;
            firstConfigurationWas = true;
            Configuration configuration = newConfiguration;
            if (configuration == null) {
                configuration = context.getResources().getConfiguration();
            }
            if (configuration.keyboard == 1 || configuration.hardKeyboardHidden != 1) {
                z = false;
            }
            usingHardwareInput = z;
            WindowManager manager = (WindowManager) context.getSystemService("window");
            if (manager != null && (display = manager.getDefaultDisplay()) != null) {
                display.getMetrics(displayMetrics);
                display.getSize(displaySize);
                screenRefreshRate = display.getRefreshRate();
            }
            if (configuration.screenWidthDp != 0) {
                int newSize = (int) Math.ceil(configuration.screenWidthDp * density);
                if (Math.abs(displaySize.x - newSize) > 3) {
                    displaySize.x = newSize;
                }
            }
            int newSize2 = configuration.screenHeightDp;
            if (newSize2 != 0) {
                int newSize3 = (int) Math.ceil(configuration.screenHeightDp * density);
                if (Math.abs(displaySize.y - newSize3) > 3) {
                    displaySize.y = newSize3;
                }
            }
            int newSize4 = roundMessageSize;
            if (newSize4 == 0) {
                if (isTablet()) {
                    roundMessageSize = (int) (getMinTabletSide() * 0.6f);
                    roundPlayingMessageSize = getMinTabletSide() - dp(28.0f);
                } else {
                    roundMessageSize = (int) (Math.min(displaySize.x, displaySize.y) * 0.6f);
                    roundPlayingMessageSize = Math.min(displaySize.x, displaySize.y) - dp(28.0f);
                }
                roundMessageInset = dp(2.0f);
            }
            if (BuildVars.LOGS_ENABLED) {
                if (statusBarHeight == 0) {
                    fillStatusBarHeight(context);
                }
                FileLog.e("density = " + density + " display size = " + displaySize.x + " " + displaySize.y + " " + displayMetrics.xdpi + "x" + displayMetrics.ydpi + ", screen layout: " + configuration.screenLayout + ", statusbar height: " + statusBarHeight + ", navbar height: " + navigationBarHeight);
            }
            ViewConfiguration vc = ViewConfiguration.get(context);
            touchSlop = vc.getScaledTouchSlop();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static double fixLocationCoord(double value) {
        double d = (long) (value * 1000000.0d);
        Double.isNaN(d);
        return d / 1000000.0d;
    }

    public static String formapMapUrl(int account, double lat, double lon, int width, int height, boolean marker, int zoom, int provider) {
        int provider2;
        int scale = Math.min(2, (int) Math.ceil(density));
        if (provider != -1) {
            provider2 = provider;
        } else {
            provider2 = MessagesController.getInstance(account).mapProvider;
        }
        if (provider2 == 1 || provider2 == 3) {
            String lang = null;
            String[] availableLangs = {"ru_RU", "tr_TR"};
            LocaleController.LocaleInfo localeInfo = LocaleController.getInstance().getCurrentLocaleInfo();
            for (int a = 0; a < availableLangs.length; a++) {
                if (availableLangs[a].toLowerCase().contains(localeInfo.shortName)) {
                    lang = availableLangs[a];
                }
            }
            if (lang == null) {
                lang = "en_US";
            }
            return marker ? String.format(Locale.US, "https://static-maps.yandex.ru/1.x/?ll=%.6f,%.6f&z=%d&size=%d,%d&l=map&scale=%d&pt=%.6f,%.6f,vkbkm&lang=%s", Double.valueOf(lon), Double.valueOf(lat), Integer.valueOf(zoom), Integer.valueOf(width * scale), Integer.valueOf(height * scale), Integer.valueOf(scale), Double.valueOf(lon), Double.valueOf(lat), lang) : String.format(Locale.US, "https://static-maps.yandex.ru/1.x/?ll=%.6f,%.6f&z=%d&size=%d,%d&l=map&scale=%d&lang=%s", Double.valueOf(lon), Double.valueOf(lat), Integer.valueOf(zoom), Integer.valueOf(width * scale), Integer.valueOf(height * scale), Integer.valueOf(scale), lang);
        }
        String k = MessagesController.getInstance(account).mapKey;
        return !TextUtils.isEmpty(k) ? marker ? String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d&markers=color:red%%7Csize:mid%%7C%.6f,%.6f&sensor=false&key=%s", Double.valueOf(lat), Double.valueOf(lon), Integer.valueOf(zoom), Integer.valueOf(width), Integer.valueOf(height), Integer.valueOf(scale), Double.valueOf(lat), Double.valueOf(lon), k) : String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d&key=%s", Double.valueOf(lat), Double.valueOf(lon), Integer.valueOf(zoom), Integer.valueOf(width), Integer.valueOf(height), Integer.valueOf(scale), k) : marker ? String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d&markers=color:red%%7Csize:mid%%7C%.6f,%.6f&sensor=false", Double.valueOf(lat), Double.valueOf(lon), Integer.valueOf(zoom), Integer.valueOf(width), Integer.valueOf(height), Integer.valueOf(scale), Double.valueOf(lat), Double.valueOf(lon)) : String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d", Double.valueOf(lat), Double.valueOf(lon), Integer.valueOf(zoom), Integer.valueOf(width), Integer.valueOf(height), Integer.valueOf(scale));
    }

    public static float getPixelsInCM(float cm, boolean isX) {
        float f = cm / 2.54f;
        DisplayMetrics displayMetrics2 = displayMetrics;
        return f * (isX ? displayMetrics2.xdpi : displayMetrics2.ydpi);
    }

    public static int getMyLayerVersion(int layer) {
        return 65535 & layer;
    }

    public static int getPeerLayerVersion(int layer) {
        return Math.max(73, (layer >> 16) & 65535);
    }

    public static int setMyLayerVersion(int layer, int version) {
        return ((-65536) & layer) | version;
    }

    public static int setPeerLayerVersion(int layer, int version) {
        return (65535 & layer) | (version << 16);
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0L);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (ApplicationLoader.applicationHandler == null) {
            return;
        }
        if (delay == 0) {
            ApplicationLoader.applicationHandler.post(runnable);
        } else {
            ApplicationLoader.applicationHandler.postDelayed(runnable, delay);
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        if (ApplicationLoader.applicationHandler == null) {
            return;
        }
        ApplicationLoader.applicationHandler.removeCallbacks(runnable);
    }

    public static boolean isValidWallChar(char ch) {
        return ch == '-' || ch == '~';
    }

    public static boolean isTablet() {
        if (isTablet == null) {
            isTablet = Boolean.valueOf(ApplicationLoader.applicationContext != null && ApplicationLoader.applicationContext.getResources().getBoolean(org.telegram.messenger.beta.R.bool.isTablet));
        }
        return isTablet.booleanValue();
    }

    public static boolean isSmallScreen() {
        if (isSmallScreen == null) {
            isSmallScreen = Boolean.valueOf(((float) ((Math.max(displaySize.x, displaySize.y) - statusBarHeight) - navigationBarHeight)) / density <= 650.0f);
        }
        return isSmallScreen.booleanValue();
    }

    public static boolean isSmallTablet() {
        float minSide = Math.min(displaySize.x, displaySize.y) / density;
        return minSide <= 690.0f;
    }

    public static int getMinTabletSide() {
        if (!isSmallTablet()) {
            int smallSide = Math.min(displaySize.x, displaySize.y);
            int leftSide = (smallSide * 35) / 100;
            if (leftSide < dp(320.0f)) {
                leftSide = dp(320.0f);
            }
            return smallSide - leftSide;
        }
        int smallSide2 = Math.min(displaySize.x, displaySize.y);
        int maxSide = Math.max(displaySize.x, displaySize.y);
        int leftSide2 = (maxSide * 35) / 100;
        if (leftSide2 < dp(320.0f)) {
            leftSide2 = dp(320.0f);
        }
        return Math.min(smallSide2, maxSide - leftSide2);
    }

    public static int getPhotoSize() {
        if (photoSize == null) {
            photoSize = 1280;
        }
        return photoSize.intValue();
    }

    public static void endIncomingCall() {
        if (!hasCallPermissions) {
            return;
        }
        try {
            TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony", new Class[0]);
            m.setAccessible(true);
            ITelephony iTelephony = (ITelephony) m.invoke(tm, new Object[0]);
            ITelephony telephonyService = (ITelephony) m.invoke(tm, new Object[0]);
            telephonyService.silenceRinger();
            telephonyService.endCall();
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public static String obtainLoginPhoneCall(String pattern) {
        if (!hasCallPermissions) {
            return null;
        }
        try {
            Cursor cursor = ApplicationLoader.applicationContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{"number", "date"}, "type IN (3,1,5)", null, "date DESC LIMIT 5");
            while (cursor.moveToNext()) {
                String number = cursor.getString(0);
                long date = cursor.getLong(1);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("number = " + number);
                }
                if (Math.abs(System.currentTimeMillis() - date) < 3600000 && checkPhonePattern(pattern, number)) {
                    if (cursor != null) {
                        cursor.close();
                    }
                    return number;
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return null;
    }

    public static boolean checkPhonePattern(String pattern, String phone) {
        if (TextUtils.isEmpty(pattern) || pattern.equals("*")) {
            return true;
        }
        String[] args = pattern.split("\\*");
        String phone2 = PhoneFormat.stripExceptNumbers(phone);
        int checkStart = 0;
        for (String arg : args) {
            if (!TextUtils.isEmpty(arg)) {
                int index = phone2.indexOf(arg, checkStart);
                if (index == -1) {
                    return false;
                }
                checkStart = arg.length() + index;
            }
        }
        return true;
    }

    public static int getViewInset(View view) {
        if (view == null || Build.VERSION.SDK_INT < 21 || view.getHeight() == displaySize.y || view.getHeight() == displaySize.y - statusBarHeight) {
            return 0;
        }
        try {
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            WindowInsets insets = view.getRootWindowInsets();
            if (insets == null) {
                return 0;
            }
            return insets.getStableInsetBottom();
        }
        if (mAttachInfoField == null) {
            Field declaredField = View.class.getDeclaredField("mAttachInfo");
            mAttachInfoField = declaredField;
            declaredField.setAccessible(true);
        }
        Object mAttachInfo = mAttachInfoField.get(view);
        if (mAttachInfo != null) {
            if (mStableInsetsField == null) {
                Field declaredField2 = mAttachInfo.getClass().getDeclaredField("mStableInsets");
                mStableInsetsField = declaredField2;
                declaredField2.setAccessible(true);
            }
            return ((Rect) mStableInsetsField.get(mAttachInfo)).bottom;
        }
        return 0;
    }

    public static Point getRealScreenSize() {
        Point size = new Point();
        try {
            WindowManager windowManager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
            if (Build.VERSION.SDK_INT >= 17) {
                windowManager.getDefaultDisplay().getRealSize(size);
            } else {
                try {
                    Method mGetRawW = Display.class.getMethod("getRawWidth", new Class[0]);
                    Method mGetRawH = Display.class.getMethod("getRawHeight", new Class[0]);
                    size.set(((Integer) mGetRawW.invoke(windowManager.getDefaultDisplay(), new Object[0])).intValue(), ((Integer) mGetRawH.invoke(windowManager.getDefaultDisplay(), new Object[0])).intValue());
                } catch (Exception e) {
                    size.set(windowManager.getDefaultDisplay().getWidth(), windowManager.getDefaultDisplay().getHeight());
                    FileLog.e(e);
                }
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        return size;
    }

    public static void setEnabled(View view, boolean enabled) {
        if (view == null) {
            return;
        }
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                setEnabled(viewGroup.getChildAt(i), enabled);
            }
        }
    }

    public static int charSequenceIndexOf(CharSequence cs, CharSequence needle, int fromIndex) {
        for (int i = fromIndex; i < cs.length() - needle.length(); i++) {
            boolean eq = true;
            int j = 0;
            while (true) {
                if (j < needle.length()) {
                    if (needle.charAt(j) == cs.charAt(i + j)) {
                        j++;
                    } else {
                        eq = false;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (eq) {
                return i;
            }
        }
        return -1;
    }

    public static int charSequenceIndexOf(CharSequence cs, CharSequence needle) {
        return charSequenceIndexOf(cs, needle, 0);
    }

    public static boolean charSequenceContains(CharSequence cs, CharSequence needle) {
        return charSequenceIndexOf(cs, needle) != -1;
    }

    public static CharSequence getTrimmedString(CharSequence src) {
        if (src == null || src.length() == 0) {
            return src;
        }
        while (src.length() > 0 && (src.charAt(0) == '\n' || src.charAt(0) == ' ')) {
            src = src.subSequence(1, src.length());
        }
        while (src.length() > 0 && (src.charAt(src.length() - 1) == '\n' || src.charAt(src.length() - 1) == ' ')) {
            src = src.subSequence(0, src.length() - 1);
        }
        return src;
    }

    public static void setViewPagerEdgeEffectColor(ViewPager viewPager, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                Field field = ViewPager.class.getDeclaredField("mLeftEdge");
                field.setAccessible(true);
                EdgeEffect mLeftEdge = (EdgeEffect) field.get(viewPager);
                if (mLeftEdge != null) {
                    mLeftEdge.setColor(color);
                }
                Field field2 = ViewPager.class.getDeclaredField("mRightEdge");
                field2.setAccessible(true);
                EdgeEffect mRightEdge = (EdgeEffect) field2.get(viewPager);
                if (mRightEdge != null) {
                    mRightEdge.setColor(color);
                }
            } catch (Exception e) {
            }
        }
    }

    public static void setScrollViewEdgeEffectColor(HorizontalScrollView scrollView, int color) {
        if (Build.VERSION.SDK_INT >= 29) {
            scrollView.setEdgeEffectColor(color);
        } else if (Build.VERSION.SDK_INT >= 21) {
            try {
                Field field = HorizontalScrollView.class.getDeclaredField("mEdgeGlowLeft");
                field.setAccessible(true);
                EdgeEffect mEdgeGlowTop = (EdgeEffect) field.get(scrollView);
                if (mEdgeGlowTop != null) {
                    mEdgeGlowTop.setColor(color);
                }
                Field field2 = HorizontalScrollView.class.getDeclaredField("mEdgeGlowRight");
                field2.setAccessible(true);
                EdgeEffect mEdgeGlowBottom = (EdgeEffect) field2.get(scrollView);
                if (mEdgeGlowBottom != null) {
                    mEdgeGlowBottom.setColor(color);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static void setScrollViewEdgeEffectColor(ScrollView scrollView, int color) {
        if (Build.VERSION.SDK_INT >= 29) {
            scrollView.setTopEdgeEffectColor(color);
            scrollView.setBottomEdgeEffectColor(color);
        } else if (Build.VERSION.SDK_INT >= 21) {
            try {
                Field field = ScrollView.class.getDeclaredField("mEdgeGlowTop");
                field.setAccessible(true);
                EdgeEffect mEdgeGlowTop = (EdgeEffect) field.get(scrollView);
                if (mEdgeGlowTop != null) {
                    mEdgeGlowTop.setColor(color);
                }
                Field field2 = ScrollView.class.getDeclaredField("mEdgeGlowBottom");
                field2.setAccessible(true);
                EdgeEffect mEdgeGlowBottom = (EdgeEffect) field2.get(scrollView);
                if (mEdgeGlowBottom != null) {
                    mEdgeGlowBottom.setColor(color);
                }
            } catch (Exception e) {
            }
        }
    }

    public static void clearDrawableAnimation(View view) {
        if (Build.VERSION.SDK_INT < 21 || view == null) {
            return;
        }
        if (view instanceof ListView) {
            Drawable drawable = ((ListView) view).getSelector();
            if (drawable != null) {
                drawable.setState(StateSet.NOTHING);
                return;
            }
            return;
        }
        Drawable drawable2 = view.getBackground();
        if (drawable2 != null) {
            drawable2.setState(StateSet.NOTHING);
            drawable2.jumpToCurrentState();
        }
    }

    public static SpannableStringBuilder replaceTags(String str) {
        return replaceTags(str, 11, new Object[0]);
    }

    public static SpannableStringBuilder replaceTags(String str, int flag, Object... args) {
        try {
            StringBuilder stringBuilder = new StringBuilder(str);
            if ((flag & 1) != 0) {
                while (true) {
                    int start = stringBuilder.indexOf("<br>");
                    if (start != -1) {
                        stringBuilder.replace(start, start + 4, "\n");
                    }
                }
                while (true) {
                    int start2 = stringBuilder.indexOf("<br/>");
                    if (start2 == -1) {
                        break;
                    }
                    stringBuilder.replace(start2, start2 + 5, "\n");
                }
            }
            ArrayList<Integer> bolds = new ArrayList<>();
            if ((flag & 2) != 0) {
                while (true) {
                    int start3 = stringBuilder.indexOf("<b>");
                    if (start3 != -1) {
                        stringBuilder.replace(start3, start3 + 3, "");
                        int end = stringBuilder.indexOf("</b>");
                        if (end == -1) {
                            end = stringBuilder.indexOf("<b>");
                        }
                        stringBuilder.replace(end, end + 4, "");
                        bolds.add(Integer.valueOf(start3));
                        bolds.add(Integer.valueOf(end));
                    }
                }
                while (true) {
                    int start4 = stringBuilder.indexOf("**");
                    if (start4 == -1) {
                        break;
                    }
                    stringBuilder.replace(start4, start4 + 2, "");
                    int end2 = stringBuilder.indexOf("**");
                    if (end2 >= 0) {
                        stringBuilder.replace(end2, end2 + 2, "");
                        bolds.add(Integer.valueOf(start4));
                        bolds.add(Integer.valueOf(end2));
                    }
                }
            }
            if ((flag & 8) != 0) {
                while (true) {
                    int start5 = stringBuilder.indexOf("**");
                    if (start5 == -1) {
                        break;
                    }
                    stringBuilder.replace(start5, start5 + 2, "");
                    int end3 = stringBuilder.indexOf("**");
                    if (end3 >= 0) {
                        stringBuilder.replace(end3, end3 + 2, "");
                        bolds.add(Integer.valueOf(start5));
                        bolds.add(Integer.valueOf(end3));
                    }
                }
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuilder);
            for (int a = 0; a < bolds.size() / 2; a++) {
                spannableStringBuilder.setSpan(new TypefaceSpan(getTypeface(TYPEFACE_ROBOTO_MEDIUM)), bolds.get(a * 2).intValue(), bolds.get((a * 2) + 1).intValue(), 33);
            }
            return spannableStringBuilder;
        } catch (Exception e) {
            FileLog.e(e);
            return new SpannableStringBuilder(str);
        }
    }

    /* loaded from: classes4.dex */
    public static class LinkMovementMethodMy extends LinkMovementMethod {
        @Override // android.text.method.LinkMovementMethod, android.text.method.ScrollingMovementMethod, android.text.method.BaseMovementMethod, android.text.method.MovementMethod
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                boolean result = super.onTouchEvent(widget, buffer, event);
                if (event.getAction() == 1 || event.getAction() == 3) {
                    Selection.removeSelection(buffer);
                }
                return result;
            } catch (Exception e) {
                FileLog.e(e);
                return false;
            }
        }
    }

    public static boolean needShowPasscode() {
        return needShowPasscode(false);
    }

    public static boolean needShowPasscode(boolean reset) {
        boolean wasInBackground = ForegroundDetector.getInstance().isWasInBackground(reset);
        if (reset) {
            ForegroundDetector.getInstance().resetBackgroundVar();
        }
        int uptime = (int) (SystemClock.elapsedRealtime() / 1000);
        if (BuildVars.LOGS_ENABLED && reset && SharedConfig.passcodeHash.length() > 0) {
            FileLog.d("wasInBackground = " + wasInBackground + " appLocked = " + SharedConfig.appLocked + " autoLockIn = " + SharedConfig.autoLockIn + " lastPauseTime = " + SharedConfig.lastPauseTime + " uptime = " + uptime);
        }
        return SharedConfig.passcodeHash.length() > 0 && wasInBackground && (SharedConfig.appLocked || ((SharedConfig.autoLockIn != 0 && SharedConfig.lastPauseTime != 0 && !SharedConfig.appLocked && SharedConfig.lastPauseTime + SharedConfig.autoLockIn <= uptime) || uptime + 5 < SharedConfig.lastPauseTime));
    }

    public static void shakeView(final View view, final float x, final int num) {
        if (view == null) {
            return;
        }
        if (num == 6) {
            view.setTranslationX(0.0f);
            return;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(view, "translationX", dp(x)));
        animatorSet.setDuration(50L);
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.messenger.AndroidUtilities.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                View view2 = view;
                int i = num;
                AndroidUtilities.shakeView(view2, i == 5 ? 0.0f : -x, i + 1);
            }
        });
        animatorSet.start();
    }

    public static void shakeViewSpring(View view) {
        shakeViewSpring(view, 10.0f, null);
    }

    public static void shakeViewSpring(View view, float shiftDp) {
        shakeViewSpring(view, shiftDp, null);
    }

    public static void shakeViewSpring(View view, Runnable endCallback) {
        shakeViewSpring(view, 10.0f, endCallback);
    }

    public static void shakeViewSpring(View view, float shiftDp, final Runnable endCallback) {
        int shift = dp(shiftDp);
        new SpringAnimation(view, DynamicAnimation.TRANSLATION_X, 0.0f).setSpring(new SpringForce(0.0f).setStiffness(600.0f)).setStartVelocity((-shift) * 100).addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.messenger.AndroidUtilities$$ExternalSyntheticLambda11
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                AndroidUtilities.lambda$shakeViewSpring$6(endCallback, dynamicAnimation, z, f, f2);
            }
        }).start();
    }

    public static /* synthetic */ void lambda$shakeViewSpring$6(Runnable endCallback, DynamicAnimation animation, boolean canceled, float value, float velocity) {
        if (endCallback != null) {
            endCallback.run();
        }
    }

    public static void startAppCenter(Activity context) {
        if (BuildConfig.DEBUG) {
            return;
        }
        try {
            if (BuildVars.DEBUG_VERSION) {
                Distribute.setEnabledForDebuggableBuild(true);
                AppCenter.start(context.getApplication(), BuildVars.APPCENTER_HASH, Distribute.class, Crashes.class);
                AppCenter.setUserId("uid=" + UserConfig.getInstance(UserConfig.selectedAccount).clientUserId);
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public static void checkForUpdates() {
        try {
            if (!BuildVars.DEBUG_VERSION || SystemClock.elapsedRealtime() - lastUpdateCheckTime < 3600000) {
                return;
            }
            lastUpdateCheckTime = SystemClock.elapsedRealtime();
            Distribute.checkForUpdate();
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public static void appCenterLog(Throwable e) {
        try {
            Crashes.trackError(e);
        } catch (Throwable th) {
        }
    }

    public static boolean shouldShowClipboardToast() {
        return Build.VERSION.SDK_INT < 31 || !OneUIUtilities.hasBuiltInClipboardToasts();
    }

    public static void addToClipboard(CharSequence str) {
        try {
            ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
            ClipData clip = ClipData.newPlainText(Constants.ScionAnalytics.PARAM_LABEL, str);
            clipboard.setPrimaryClip(clip);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void addMediaToGallery(String fromPath) {
        if (fromPath == null) {
            return;
        }
        File f = new File(fromPath);
        addMediaToGallery(f);
    }

    public static void addMediaToGallery(File file) {
        Uri uri = Uri.fromFile(file);
        if (uri == null) {
            return;
        }
        try {
            Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            mediaScanIntent.setData(uri);
            ApplicationLoader.applicationContext.sendBroadcast(mediaScanIntent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private static File getAlbumDir(boolean secretChat) {
        if (secretChat || !BuildVars.NO_SCOPED_STORAGE || (Build.VERSION.SDK_INT >= 23 && ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0)) {
            return FileLoader.getDirectory(0);
        }
        File storageDir = null;
        if ("mounted".equals(Environment.getExternalStorageState())) {
            storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Telegram");
            if (!storageDir.mkdirs() && !storageDir.exists()) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("failed to create directory");
                    return null;
                }
                return null;
            }
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.d("External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static String getPath(Uri uri) {
        try {
            boolean isKitKat = Build.VERSION.SDK_INT >= 19;
            if (isKitKat && DocumentsContract.isDocumentUri(ApplicationLoader.applicationContext, uri)) {
                if (isExternalStorageDocument(uri)) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(com.microsoft.appcenter.Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
                    if ("primary".equalsIgnoreCase(split[0])) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                } else if (isDownloadsDocument(uri)) {
                    String id = DocumentsContract.getDocumentId(uri);
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id).longValue());
                    return getDataColumn(ApplicationLoader.applicationContext, contentUri, null, null);
                } else if (isMediaDocument(uri)) {
                    String docId2 = DocumentsContract.getDocumentId(uri);
                    String[] split2 = docId2.split(com.microsoft.appcenter.Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
                    String type = split2[0];
                    Uri contentUri2 = null;
                    char c = 65535;
                    switch (type.hashCode()) {
                        case 93166550:
                            if (type.equals("audio")) {
                                c = 2;
                                break;
                            }
                            break;
                        case 100313435:
                            if (type.equals(TtmlNode.TAG_IMAGE)) {
                                c = 0;
                                break;
                            }
                            break;
                        case 112202875:
                            if (type.equals("video")) {
                                c = 1;
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                            contentUri2 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            break;
                        case 1:
                            contentUri2 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                            break;
                        case 2:
                            contentUri2 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                            break;
                    }
                    String[] selectionArgs = {split2[1]};
                    return getDataColumn(ApplicationLoader.applicationContext, contentUri2, "_id=?", selectionArgs);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(ApplicationLoader.applicationContext, uri, null, null);
            } else {
                if ("file".equalsIgnoreCase(uri.getScheme())) {
                    return uri.getPath();
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor;
        String[] projection = {"_data"};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
        } catch (Exception e) {
        }
        if (cursor == null || !cursor.moveToFirst()) {
            if (cursor != null) {
                cursor.close();
            }
            return null;
        }
        int column_index = cursor.getColumnIndexOrThrow("_data");
        String value = cursor.getString(column_index);
        if (value.startsWith("content://") || (!value.startsWith("/") && !value.startsWith("file://"))) {
            if (cursor != null) {
                cursor.close();
            }
            return null;
        }
        if (cursor != null) {
            cursor.close();
        }
        return value;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static File generatePicturePath() {
        return generatePicturePath(false, null);
    }

    public static File generatePicturePath(boolean secretChat, String ext) {
        try {
            File publicDir = FileLoader.getDirectory(100);
            if (!secretChat && publicDir != null) {
                return new File(publicDir, generateFileName(0, ext));
            }
            File storageDir = ApplicationLoader.applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            return new File(storageDir, generateFileName(0, ext));
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static String generateFileName(int type, String ext) {
        Date date = new Date();
        date.setTime(System.currentTimeMillis() + Utilities.random.nextInt(1000) + 1);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(date);
        if (type == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("IMG_");
            sb.append(timeStamp);
            sb.append(".");
            sb.append(TextUtils.isEmpty(ext) ? "jpg" : ext);
            return sb.toString();
        }
        return "VID_" + timeStamp + ".mp4";
    }

    public static CharSequence generateSearchName(String name, String name2, String q) {
        if ((name == null && name2 == null) || TextUtils.isEmpty(q)) {
            return "";
        }
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String wholeString = name;
        if (wholeString == null || wholeString.length() == 0) {
            wholeString = name2;
        } else if (name2 != null && name2.length() != 0) {
            wholeString = wholeString + " " + name2;
        }
        String wholeString2 = wholeString.trim();
        String lower = " " + wholeString2.toLowerCase();
        int lastIndex = 0;
        while (true) {
            int index = lower.indexOf(" " + q, lastIndex);
            if (index == -1) {
                break;
            }
            int i = 1;
            int idx = index - (index == 0 ? 0 : 1);
            int length = q.length();
            if (index == 0) {
                i = 0;
            }
            int end = length + i + idx;
            if (lastIndex != 0 && lastIndex != idx + 1) {
                builder.append((CharSequence) wholeString2.substring(lastIndex, idx));
            } else if (lastIndex == 0 && idx != 0) {
                builder.append((CharSequence) wholeString2.substring(0, idx));
            }
            String query = wholeString2.substring(idx, Math.min(wholeString2.length(), end));
            if (query.startsWith(" ")) {
                builder.append((CharSequence) " ");
            }
            String query2 = query.trim();
            int start = builder.length();
            builder.append((CharSequence) query2);
            builder.setSpan(new ForegroundColorSpanThemable(Theme.key_windowBackgroundWhiteBlueText4), start, query2.length() + start, 33);
            lastIndex = end;
        }
        if (lastIndex != -1 && lastIndex < wholeString2.length()) {
            builder.append((CharSequence) wholeString2.substring(lastIndex));
        }
        return builder;
    }

    public static boolean isKeyguardSecure() {
        KeyguardManager km = (KeyguardManager) ApplicationLoader.applicationContext.getSystemService("keyguard");
        return km.isKeyguardSecure();
    }

    public static boolean isSimAvailable() {
        TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
        int state = tm.getSimState();
        return (state == 1 || state == 0 || tm.getPhoneType() == 0 || isAirplaneModeOn()) ? false : true;
    }

    public static boolean isAirplaneModeOn() {
        return Build.VERSION.SDK_INT < 17 ? Settings.System.getInt(ApplicationLoader.applicationContext.getContentResolver(), "airplane_mode_on", 0) != 0 : Settings.Global.getInt(ApplicationLoader.applicationContext.getContentResolver(), "airplane_mode_on", 0) != 0;
    }

    public static File generateVideoPath() {
        return generateVideoPath(false);
    }

    public static File generateVideoPath(boolean secretChat) {
        try {
            File storageDir = getAlbumDir(secretChat);
            Date date = new Date();
            date.setTime(System.currentTimeMillis() + Utilities.random.nextInt(1000) + 1);
            if (generatingVideoPathFormat == null) {
                generatingVideoPathFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US);
            }
            String timeStamp = generatingVideoPathFormat.format(date);
            return new File(storageDir, "VID_" + timeStamp + ".mp4");
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static String formatFileSize(long size) {
        return formatFileSize(size, false);
    }

    public static String formatFileSize(long size, boolean removeZero) {
        if (size < DistributeConstants.KIBIBYTE_IN_BYTES) {
            return String.format("%d B", Long.valueOf(size));
        }
        if (size < 1048576) {
            float value = ((float) size) / 1024.0f;
            return (!removeZero || (value - ((float) ((int) value))) * 10.0f != 0.0f) ? String.format("%.1f KB", Float.valueOf(value)) : String.format("%d KB", Integer.valueOf((int) value));
        } else if (size >= 1073741824) {
            float value2 = ((int) ((size / DistributeConstants.KIBIBYTE_IN_BYTES) / DistributeConstants.KIBIBYTE_IN_BYTES)) / 1000.0f;
            return (!removeZero || (value2 - ((float) ((int) value2))) * 10.0f != 0.0f) ? String.format("%.2f GB", Float.valueOf(value2)) : String.format("%d GB", Integer.valueOf((int) value2));
        } else {
            float value3 = (((float) size) / 1024.0f) / 1024.0f;
            return (!removeZero || (value3 - ((float) ((int) value3))) * 10.0f != 0.0f) ? String.format("%.1f MB", Float.valueOf(value3)) : String.format("%d MB", Integer.valueOf((int) value3));
        }
    }

    public static String formatShortDuration(int duration) {
        return formatDuration(duration, false);
    }

    public static String formatLongDuration(int duration) {
        return formatDuration(duration, true);
    }

    public static String formatDuration(int duration, boolean isLong) {
        int h = duration / 3600;
        int m = (duration / 60) % 60;
        int s = duration % 60;
        return h == 0 ? isLong ? String.format(Locale.US, "%02d:%02d", Integer.valueOf(m), Integer.valueOf(s)) : String.format(Locale.US, "%d:%02d", Integer.valueOf(m), Integer.valueOf(s)) : String.format(Locale.US, "%d:%02d:%02d", Integer.valueOf(h), Integer.valueOf(m), Integer.valueOf(s));
    }

    public static String formatFullDuration(int duration) {
        int h = duration / 3600;
        int m = (duration / 60) % 60;
        int s = duration % 60;
        return duration < 0 ? String.format(Locale.US, "-%02d:%02d:%02d", Integer.valueOf(Math.abs(h)), Integer.valueOf(Math.abs(m)), Integer.valueOf(Math.abs(s))) : String.format(Locale.US, "%02d:%02d:%02d", Integer.valueOf(h), Integer.valueOf(m), Integer.valueOf(s));
    }

    public static String formatDurationNoHours(int duration, boolean isLong) {
        int m = duration / 60;
        int s = duration % 60;
        return isLong ? String.format(Locale.US, "%02d:%02d", Integer.valueOf(m), Integer.valueOf(s)) : String.format(Locale.US, "%d:%02d", Integer.valueOf(m), Integer.valueOf(s));
    }

    public static String formatShortDuration(int progress, int duration) {
        return formatDuration(progress, duration, false);
    }

    public static String formatLongDuration(int progress, int duration) {
        return formatDuration(progress, duration, true);
    }

    public static String formatDuration(int progress, int duration, boolean isLong) {
        int h = duration / 3600;
        int m = (duration / 60) % 60;
        int s = duration % 60;
        int ph = progress / 3600;
        int pm = (progress / 60) % 60;
        int ps = progress % 60;
        return duration == 0 ? ph == 0 ? isLong ? String.format(Locale.US, "%02d:%02d / -:--", Integer.valueOf(pm), Integer.valueOf(ps)) : String.format(Locale.US, "%d:%02d / -:--", Integer.valueOf(pm), Integer.valueOf(ps)) : String.format(Locale.US, "%d:%02d:%02d / -:--", Integer.valueOf(ph), Integer.valueOf(pm), Integer.valueOf(ps)) : (ph == 0 && h == 0) ? isLong ? String.format(Locale.US, "%02d:%02d / %02d:%02d", Integer.valueOf(pm), Integer.valueOf(ps), Integer.valueOf(m), Integer.valueOf(s)) : String.format(Locale.US, "%d:%02d / %d:%02d", Integer.valueOf(pm), Integer.valueOf(ps), Integer.valueOf(m), Integer.valueOf(s)) : String.format(Locale.US, "%d:%02d:%02d / %d:%02d:%02d", Integer.valueOf(ph), Integer.valueOf(pm), Integer.valueOf(ps), Integer.valueOf(h), Integer.valueOf(m), Integer.valueOf(s));
    }

    public static String formatVideoDuration(int progress, int duration) {
        int h = duration / 3600;
        int m = (duration / 60) % 60;
        int s = duration % 60;
        int ph = progress / 3600;
        int pm = (progress / 60) % 60;
        int ps = progress % 60;
        return (ph == 0 && h == 0) ? String.format(Locale.US, "%02d:%02d / %02d:%02d", Integer.valueOf(pm), Integer.valueOf(ps), Integer.valueOf(m), Integer.valueOf(s)) : h == 0 ? String.format(Locale.US, "%d:%02d:%02d / %02d:%02d", Integer.valueOf(ph), Integer.valueOf(pm), Integer.valueOf(ps), Integer.valueOf(m), Integer.valueOf(s)) : ph == 0 ? String.format(Locale.US, "%02d:%02d / %d:%02d:%02d", Integer.valueOf(pm), Integer.valueOf(ps), Integer.valueOf(h), Integer.valueOf(m), Integer.valueOf(s)) : String.format(Locale.US, "%d:%02d:%02d / %d:%02d:%02d", Integer.valueOf(ph), Integer.valueOf(pm), Integer.valueOf(ps), Integer.valueOf(h), Integer.valueOf(m), Integer.valueOf(s));
    }

    public static String formatCount(int count) {
        if (count < 1000) {
            return Integer.toString(count);
        }
        ArrayList<String> strings = new ArrayList<>();
        while (count != 0) {
            int mod = count % 1000;
            count /= 1000;
            if (count > 0) {
                strings.add(String.format(Locale.ENGLISH, "%03d", Integer.valueOf(mod)));
            } else {
                strings.add(Integer.toString(mod));
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = strings.size() - 1; i >= 0; i--) {
            stringBuilder.append(strings.get(i));
            if (i != 0) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    public static String formatWholeNumber(int v, int dif) {
        if (v == 0) {
            return "0";
        }
        float num_ = v;
        int count = 0;
        if (dif == 0) {
            dif = v;
        }
        if (dif >= 1000) {
            while (dif >= 1000 && count < numbersSignatureArray.length - 1) {
                dif /= 1000;
                num_ /= 1000.0f;
                count++;
            }
            if (num_ < 0.1d) {
                return "0";
            }
            return num_ * 10.0f == ((float) ((int) (num_ * 10.0f))) ? String.format(Locale.ENGLISH, "%s%s", formatCount((int) num_), numbersSignatureArray[count]) : String.format(Locale.ENGLISH, "%.1f%s", Float.valueOf(((int) (num_ * 10.0f)) / 10.0f), numbersSignatureArray[count]);
        }
        return formatCount(v);
    }

    public static byte[] decodeQuotedPrintable(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int i = 0;
        while (i < bytes.length) {
            int b = bytes[i];
            if (b == 61) {
                int i2 = i + 1;
                try {
                    int u = Character.digit((char) bytes[i2], 16);
                    i = i2 + 1;
                    int l = Character.digit((char) bytes[i], 16);
                    buffer.write((char) ((u << 4) + l));
                } catch (Exception e) {
                    FileLog.e(e);
                    return null;
                }
            } else {
                buffer.write(b);
            }
            i++;
        }
        byte[] array = buffer.toByteArray();
        try {
            buffer.close();
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        return array;
    }

    public static boolean copyFile(InputStream sourceFile, File destFile) throws IOException {
        return copyFile(sourceFile, new FileOutputStream(destFile));
    }

    public static boolean copyFile(InputStream sourceFile, OutputStream out) throws IOException {
        byte[] buf = new byte[4096];
        while (true) {
            int len = sourceFile.read(buf);
            if (len > 0) {
                Thread.yield();
                out.write(buf, 0, len);
            } else {
                out.close();
                return true;
            }
        }
    }

    public static boolean copyFile(File sourceFile, File destFile) throws IOException {
        if (sourceFile.equals(destFile)) {
            return true;
        }
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        try {
            FileInputStream source = new FileInputStream(sourceFile);
            FileOutputStream destination = new FileOutputStream(destFile);
            try {
                destination.getChannel().transferFrom(source.getChannel(), 0L, source.getChannel().size());
                destination.close();
                source.close();
                return true;
            } catch (Throwable th) {
                try {
                    destination.close();
                } catch (Throwable th2) {
                }
                throw th;
            }
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    public static byte[] calcAuthKeyHash(byte[] auth_key) {
        byte[] sha1 = Utilities.computeSHA1(auth_key);
        byte[] key_hash = new byte[16];
        System.arraycopy(sha1, 0, key_hash, 0, 16);
        return key_hash;
    }

    public static void openDocument(MessageObject message, Activity activity, BaseFragment parentFragment) {
        TLRPC.Document document;
        File f;
        if (message == null || (document = message.getDocument()) == null) {
            return;
        }
        File f2 = null;
        String fileName = message.messageOwner.media != null ? FileLoader.getAttachFileName(document) : "";
        if (message.messageOwner.attachPath != null && message.messageOwner.attachPath.length() != 0) {
            f2 = new File(message.messageOwner.attachPath);
        }
        if (f2 == null || (f2 != null && !f2.exists())) {
            f = FileLoader.getInstance(UserConfig.selectedAccount).getPathToMessage(message.messageOwner);
        } else {
            f = f2;
        }
        if (f == null || !f.exists()) {
            return;
        }
        if (parentFragment != null && f.getName().toLowerCase().endsWith("attheme")) {
            Theme.ThemeInfo themeInfo = Theme.applyThemeFile(f, message.getDocumentName(), null, true);
            if (themeInfo != null) {
                parentFragment.presentFragment(new ThemePreviewActivity(themeInfo));
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(LocaleController.getString("AppName", org.telegram.messenger.beta.R.string.AppName));
            builder.setMessage(LocaleController.getString("IncorrectTheme", org.telegram.messenger.beta.R.string.IncorrectTheme));
            builder.setPositiveButton(LocaleController.getString("OK", org.telegram.messenger.beta.R.string.OK), null);
            parentFragment.showDialog(builder.create());
            return;
        }
        String realMimeType = null;
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setFlags(1);
            MimeTypeMap myMime = MimeTypeMap.getSingleton();
            int idx = fileName.lastIndexOf(46);
            if (idx != -1) {
                String ext = fileName.substring(idx + 1);
                realMimeType = myMime.getMimeTypeFromExtension(ext.toLowerCase());
                if (realMimeType == null && ((realMimeType = document.mime_type) == null || realMimeType.length() == 0)) {
                    realMimeType = null;
                }
            }
            if (Build.VERSION.SDK_INT >= 24) {
                intent.setDataAndType(FileProvider.getUriForFile(activity, "org.telegram.messenger.beta.provider", f), realMimeType != null ? realMimeType : ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN);
            } else {
                intent.setDataAndType(Uri.fromFile(f), realMimeType != null ? realMimeType : ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN);
            }
            if (realMimeType != null) {
                try {
                    activity.startActivityForResult(intent, 500);
                    return;
                } catch (Exception e) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        intent.setDataAndType(FileProvider.getUriForFile(activity, "org.telegram.messenger.beta.provider", f), ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN);
                    } else {
                        intent.setDataAndType(Uri.fromFile(f), ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN);
                    }
                    activity.startActivityForResult(intent, 500);
                    return;
                }
            }
            activity.startActivityForResult(intent, 500);
        } catch (Exception e2) {
            if (activity == null) {
                return;
            }
            AlertDialog.Builder builder2 = new AlertDialog.Builder(activity);
            builder2.setTitle(LocaleController.getString("AppName", org.telegram.messenger.beta.R.string.AppName));
            builder2.setPositiveButton(LocaleController.getString("OK", org.telegram.messenger.beta.R.string.OK), null);
            builder2.setMessage(LocaleController.formatString("NoHandleAppInstalled", org.telegram.messenger.beta.R.string.NoHandleAppInstalled, message.getDocument().mime_type));
            if (parentFragment != null) {
                parentFragment.showDialog(builder2.create());
            } else {
                builder2.show();
            }
        }
    }

    public static boolean openForView(File f, String fileName, String mimeType, Activity activity, Theme.ResourcesProvider resourcesProvider) {
        if (f != null && f.exists()) {
            String realMimeType = null;
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setFlags(1);
            MimeTypeMap myMime = MimeTypeMap.getSingleton();
            int idx = fileName.lastIndexOf(46);
            if (idx != -1) {
                String ext = fileName.substring(idx + 1);
                realMimeType = myMime.getMimeTypeFromExtension(ext.toLowerCase());
                if (realMimeType == null && ((realMimeType = mimeType) == null || realMimeType.length() == 0)) {
                    realMimeType = null;
                }
            }
            if (Build.VERSION.SDK_INT >= 26 && realMimeType != null && realMimeType.equals("application/vnd.android.package-archive") && !ApplicationLoader.applicationContext.getPackageManager().canRequestPackageInstalls()) {
                AlertsCreator.createApkRestrictedDialog(activity, resourcesProvider).show();
                return true;
            }
            if (Build.VERSION.SDK_INT >= 24) {
                intent.setDataAndType(FileProvider.getUriForFile(activity, "org.telegram.messenger.beta.provider", f), realMimeType != null ? realMimeType : ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN);
            } else {
                intent.setDataAndType(Uri.fromFile(f), realMimeType != null ? realMimeType : ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN);
            }
            if (realMimeType != null) {
                try {
                    activity.startActivityForResult(intent, 500);
                } catch (Exception e) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        intent.setDataAndType(FileProvider.getUriForFile(activity, "org.telegram.messenger.beta.provider", f), ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN);
                    } else {
                        intent.setDataAndType(Uri.fromFile(f), ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN);
                    }
                    activity.startActivityForResult(intent, 500);
                }
            } else {
                activity.startActivityForResult(intent, 500);
            }
            return true;
        }
        return false;
    }

    public static boolean openForView(MessageObject message, Activity activity, Theme.ResourcesProvider resourcesProvider) {
        File f = null;
        if (message.messageOwner.attachPath != null && message.messageOwner.attachPath.length() != 0) {
            f = new File(message.messageOwner.attachPath);
        }
        if (f == null || !f.exists()) {
            f = FileLoader.getInstance(message.currentAccount).getPathToMessage(message.messageOwner);
        }
        String mimeType = (message.type == 9 || message.type == 0) ? message.getMimeType() : null;
        return openForView(f, message.getFileName(), mimeType, activity, resourcesProvider);
    }

    public static boolean openForView(TLRPC.Document document, boolean forceCache, Activity activity) {
        String fileName = FileLoader.getAttachFileName(document);
        File f = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(document, true);
        return openForView(f, fileName, document.mime_type, activity, null);
    }

    public static SpannableStringBuilder formatSpannableSimple(String format, CharSequence... cs) {
        return formatSpannable(format, AndroidUtilities$$ExternalSyntheticLambda4.INSTANCE, cs);
    }

    public static /* synthetic */ String lambda$formatSpannableSimple$7(Integer i) {
        return "%s";
    }

    public static SpannableStringBuilder formatSpannable(String format, CharSequence... cs) {
        if (format.contains("%s")) {
            return formatSpannableSimple(format, cs);
        }
        return formatSpannable(format, AndroidUtilities$$ExternalSyntheticLambda3.INSTANCE, cs);
    }

    public static /* synthetic */ String lambda$formatSpannable$8(Integer i) {
        return "%" + (i.intValue() + 1) + "$s";
    }

    public static SpannableStringBuilder formatSpannable(String format, GenericProvider<Integer, String> keysProvider, CharSequence... cs) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(format);
        for (int i = 0; i < cs.length; i++) {
            String key = keysProvider.provide(Integer.valueOf(i));
            int j = format.indexOf(key);
            if (j != -1) {
                stringBuilder.replace(j, key.length() + j, cs[i]);
                format = format.substring(0, j) + cs[i].toString() + format.substring(key.length() + j);
            }
        }
        return stringBuilder;
    }

    public static CharSequence replaceTwoNewLinesToOne(CharSequence original) {
        char[] buf = new char[2];
        if (original instanceof StringBuilder) {
            StringBuilder stringBuilder = (StringBuilder) original;
            int a = 0;
            int N = original.length();
            while (a < N - 2) {
                stringBuilder.getChars(a, a + 2, buf, 0);
                if (buf[0] == '\n' && buf[1] == '\n') {
                    stringBuilder = stringBuilder.replace(a, a + 2, "\n");
                    a--;
                    N--;
                }
                a++;
            }
            return original;
        } else if (original instanceof SpannableStringBuilder) {
            SpannableStringBuilder stringBuilder2 = (SpannableStringBuilder) original;
            int a2 = 0;
            int N2 = original.length();
            while (a2 < N2 - 2) {
                stringBuilder2.getChars(a2, a2 + 2, buf, 0);
                if (buf[0] == '\n' && buf[1] == '\n') {
                    stringBuilder2 = stringBuilder2.replace(a2, a2 + 2, (CharSequence) "\n");
                    a2--;
                    N2--;
                }
                a2++;
            }
            return original;
        } else {
            return original.toString().replace("\n\n", "\n");
        }
    }

    public static CharSequence replaceNewLines(CharSequence original) {
        if (original instanceof StringBuilder) {
            StringBuilder stringBuilder = (StringBuilder) original;
            int N = original.length();
            for (int a = 0; a < N; a++) {
                if (original.charAt(a) == '\n') {
                    stringBuilder.setCharAt(a, ' ');
                }
            }
            return original;
        } else if (original instanceof SpannableStringBuilder) {
            SpannableStringBuilder stringBuilder2 = (SpannableStringBuilder) original;
            int N2 = original.length();
            for (int a2 = 0; a2 < N2; a2++) {
                if (original.charAt(a2) == '\n') {
                    stringBuilder2.replace(a2, a2 + 1, (CharSequence) " ");
                }
            }
            return original;
        } else {
            return original.toString().replace('\n', ' ');
        }
    }

    public static boolean openForView(TLObject media, Activity activity) {
        if (media == null || activity == null) {
            return false;
        }
        String fileName = FileLoader.getAttachFileName(media);
        File f = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(media, true);
        if (f == null || !f.exists()) {
            return false;
        }
        String realMimeType = null;
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setFlags(1);
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        int idx = fileName.lastIndexOf(46);
        if (idx != -1) {
            String ext = fileName.substring(idx + 1);
            realMimeType = myMime.getMimeTypeFromExtension(ext.toLowerCase());
            if (realMimeType == null) {
                if (media instanceof TLRPC.TL_document) {
                    realMimeType = ((TLRPC.TL_document) media).mime_type;
                }
                if (realMimeType == null || realMimeType.length() == 0) {
                    realMimeType = null;
                }
            }
        }
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setDataAndType(FileProvider.getUriForFile(activity, "org.telegram.messenger.beta.provider", f), realMimeType != null ? realMimeType : ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN);
        } else {
            intent.setDataAndType(Uri.fromFile(f), realMimeType != null ? realMimeType : ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN);
        }
        if (realMimeType != null) {
            try {
                activity.startActivityForResult(intent, 500);
            } catch (Exception e) {
                if (Build.VERSION.SDK_INT >= 24) {
                    intent.setDataAndType(FileProvider.getUriForFile(activity, "org.telegram.messenger.beta.provider", f), ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN);
                } else {
                    intent.setDataAndType(Uri.fromFile(f), ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN);
                }
                activity.startActivityForResult(intent, 500);
            }
        } else {
            activity.startActivityForResult(intent, 500);
        }
        return true;
    }

    public static boolean isBannedForever(TLRPC.TL_chatBannedRights rights) {
        return rights == null || Math.abs(((long) rights.until_date) - (System.currentTimeMillis() / 1000)) > 157680000;
    }

    public static void setRectToRect(Matrix matrix, RectF src, RectF dst, int rotation, boolean translate) {
        float sy;
        float sx;
        float ty;
        float tx;
        float diff;
        boolean xLarger = false;
        if (rotation == 90 || rotation == 270) {
            sx = dst.height() / src.width();
            sy = dst.width() / src.height();
        } else {
            sx = dst.width() / src.width();
            sy = dst.height() / src.height();
        }
        if (sx < sy) {
            sx = sy;
            xLarger = true;
        } else {
            sy = sx;
        }
        if (translate) {
            matrix.setTranslate(dst.left, dst.top);
        }
        if (rotation == 90) {
            matrix.preRotate(90.0f);
            matrix.preTranslate(0.0f, -dst.width());
        } else if (rotation == 180) {
            matrix.preRotate(180.0f);
            matrix.preTranslate(-dst.width(), -dst.height());
        } else if (rotation == 270) {
            matrix.preRotate(270.0f);
            matrix.preTranslate(-dst.height(), 0.0f);
        }
        if (translate) {
            tx = (-src.left) * sx;
            ty = (-src.top) * sy;
        } else {
            tx = dst.left - (src.left * sx);
            ty = dst.top - (src.top * sy);
        }
        if (xLarger) {
            diff = dst.width() - (src.width() * sy);
        } else {
            float diff2 = dst.height();
            diff = diff2 - (src.height() * sy);
        }
        float diff3 = diff / 2.0f;
        if (xLarger) {
            tx += diff3;
        } else {
            ty += diff3;
        }
        matrix.preScale(sx, sy);
        if (translate) {
            matrix.preTranslate(tx, ty);
        }
    }

    public static Vibrator getVibrator() {
        if (vibrator == null) {
            vibrator = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
        }
        return vibrator;
    }

    public static boolean isAccessibilityTouchExplorationEnabled() {
        if (accessibilityManager == null) {
            accessibilityManager = (AccessibilityManager) ApplicationLoader.applicationContext.getSystemService("accessibility");
        }
        return accessibilityManager.isEnabled() && accessibilityManager.isTouchExplorationEnabled();
    }

    /* JADX WARN: Removed duplicated region for block: B:59:0x012a A[Catch: Exception -> 0x0148, TRY_LEAVE, TryCatch #0 {Exception -> 0x0148, blocks: (B:5:0x0008, B:8:0x0012, B:10:0x0018, B:12:0x0023, B:15:0x0035, B:18:0x0043, B:20:0x004b, B:23:0x005f, B:25:0x0065, B:27:0x006b, B:29:0x0071, B:31:0x0090, B:33:0x0099, B:36:0x00b9, B:38:0x00c9, B:40:0x00d1, B:42:0x00d9, B:44:0x00df, B:46:0x00e7, B:48:0x00ef, B:50:0x00f9, B:52:0x0102, B:57:0x0124, B:59:0x012a, B:69:0x0142), top: B:73:0x0008 }] */
    /* JADX WARN: Removed duplicated region for block: B:76:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean handleProxyIntent(android.app.Activity r19, android.content.Intent r20) {
        /*
            Method dump skipped, instructions count: 331
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.handleProxyIntent(android.app.Activity, android.content.Intent):boolean");
    }

    public static boolean shouldEnableAnimation() {
        if (Build.VERSION.SDK_INT < 26 || Build.VERSION.SDK_INT >= 28) {
            return true;
        }
        PowerManager powerManager = (PowerManager) ApplicationLoader.applicationContext.getSystemService("power");
        if (powerManager.isPowerSaveMode()) {
            return false;
        }
        float scale = Settings.Global.getFloat(ApplicationLoader.applicationContext.getContentResolver(), "animator_duration_scale", 1.0f);
        return scale > 0.0f;
    }

    public static void showProxyAlert(Activity activity, final String address, final String port, final String user, final String password, final String secret) {
        BottomSheet.Builder builder = new BottomSheet.Builder(activity);
        final Runnable dismissRunnable = builder.getDismissRunnable();
        builder.setApplyTopPadding(false);
        builder.setApplyBottomPadding(false);
        LinearLayout linearLayout = new LinearLayout(activity);
        builder.setCustomView(linearLayout);
        boolean z = true;
        linearLayout.setOrientation(1);
        if (!TextUtils.isEmpty(secret)) {
            TextView titleTextView = new TextView(activity);
            titleTextView.setText(LocaleController.getString("UseProxyTelegramInfo2", org.telegram.messenger.beta.R.string.UseProxyTelegramInfo2));
            titleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray4));
            titleTextView.setTextSize(1, 14.0f);
            titleTextView.setGravity(49);
            linearLayout.addView(titleTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 17, 8, 17, 8));
            View lineView = new View(activity);
            lineView.setBackgroundColor(Theme.getColor(Theme.key_divider));
            linearLayout.addView(lineView, new LinearLayout.LayoutParams(-1, 1));
        }
        int a = 0;
        for (int i = 5; a < i; i = 5) {
            String text = null;
            String detail = null;
            if (a == 0) {
                text = address;
                detail = LocaleController.getString("UseProxyAddress", org.telegram.messenger.beta.R.string.UseProxyAddress);
            } else if (a != z) {
                if (a == 2) {
                    text = secret;
                    detail = LocaleController.getString("UseProxySecret", org.telegram.messenger.beta.R.string.UseProxySecret);
                } else if (a == 3) {
                    text = user;
                    detail = LocaleController.getString("UseProxyUsername", org.telegram.messenger.beta.R.string.UseProxyUsername);
                } else if (a == 4) {
                    text = password;
                    detail = LocaleController.getString("UseProxyPassword", org.telegram.messenger.beta.R.string.UseProxyPassword);
                }
            } else {
                text = "" + port;
                detail = LocaleController.getString("UseProxyPort", org.telegram.messenger.beta.R.string.UseProxyPort);
            }
            if (!TextUtils.isEmpty(text)) {
                TextDetailSettingsCell cell = new TextDetailSettingsCell(activity);
                cell.setTextAndValue(text, detail, z);
                cell.getTextView().setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                cell.getValueTextView().setTextColor(Theme.getColor(Theme.key_dialogTextGray3));
                linearLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
                if (a == 2) {
                    break;
                }
            }
            a++;
            z = true;
        }
        PickerBottomLayout pickerBottomLayout = new PickerBottomLayout(activity, false);
        pickerBottomLayout.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        linearLayout.addView(pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        pickerBottomLayout.cancelButton.setPadding(dp(18.0f), 0, dp(18.0f), 0);
        pickerBottomLayout.cancelButton.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        pickerBottomLayout.cancelButton.setText(LocaleController.getString("Cancel", org.telegram.messenger.beta.R.string.Cancel).toUpperCase());
        pickerBottomLayout.cancelButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.messenger.AndroidUtilities$$ExternalSyntheticLambda9
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                dismissRunnable.run();
            }
        });
        pickerBottomLayout.doneButtonTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        pickerBottomLayout.doneButton.setPadding(dp(18.0f), 0, dp(18.0f), 0);
        pickerBottomLayout.doneButtonBadgeTextView.setVisibility(8);
        pickerBottomLayout.doneButtonTextView.setText(LocaleController.getString("ConnectingConnectProxy", org.telegram.messenger.beta.R.string.ConnectingConnectProxy).toUpperCase());
        pickerBottomLayout.doneButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.messenger.AndroidUtilities$$ExternalSyntheticLambda10
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AndroidUtilities.lambda$showProxyAlert$10(address, port, secret, password, user, dismissRunnable, view);
            }
        });
        builder.show();
    }

    public static /* synthetic */ void lambda$showProxyAlert$10(String address, String port, String secret, String password, String user, Runnable dismissRunnable, View v) {
        SharedConfig.ProxyInfo info;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("proxy_enabled", true);
        editor.putString("proxy_ip", address);
        int p = Utilities.parseInt((CharSequence) port).intValue();
        editor.putInt("proxy_port", p);
        if (TextUtils.isEmpty(secret)) {
            editor.remove("proxy_secret");
            if (TextUtils.isEmpty(password)) {
                editor.remove("proxy_pass");
            } else {
                editor.putString("proxy_pass", password);
            }
            if (TextUtils.isEmpty(user)) {
                editor.remove("proxy_user");
            } else {
                editor.putString("proxy_user", user);
            }
            info = new SharedConfig.ProxyInfo(address, p, user, password, "");
        } else {
            editor.remove("proxy_pass");
            editor.remove("proxy_user");
            editor.putString("proxy_secret", secret);
            info = new SharedConfig.ProxyInfo(address, p, "", "", secret);
        }
        editor.commit();
        SharedConfig.currentProxy = SharedConfig.addProxy(info);
        ConnectionsManager.setProxySettings(true, address, p, user, password, secret);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
        dismissRunnable.run();
    }

    public static String getSystemProperty(String key) {
        try {
            Class props = Class.forName("android.os.SystemProperties");
            return (String) props.getMethod("get", String.class).invoke(null, key);
        } catch (Exception e) {
            return null;
        }
    }

    public static void fixGoogleMapsBug() {
        SharedPreferences googleBug = ApplicationLoader.applicationContext.getSharedPreferences("google_bug_154855417", 0);
        if (!googleBug.contains("fixed")) {
            File corruptedZoomTables = new File(ApplicationLoader.getFilesDirFixed(), "ZoomTables.data");
            corruptedZoomTables.delete();
            googleBug.edit().putBoolean("fixed", true).apply();
        }
    }

    public static CharSequence concat(CharSequence... text) {
        if (text.length == 0) {
            return "";
        }
        int i = 0;
        if (text.length == 1) {
            return text[0];
        }
        boolean spanned = false;
        int length = text.length;
        int i2 = 0;
        while (true) {
            if (i2 >= length) {
                break;
            }
            if (!(text[i2] instanceof Spanned)) {
                i2++;
            } else {
                spanned = true;
                break;
            }
        }
        if (spanned) {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            int length2 = text.length;
            while (i < length2) {
                CharSequence piece = text[i];
                ssb.append(piece == null ? "null" : piece);
                i++;
            }
            return new SpannedString(ssb);
        }
        StringBuilder sb = new StringBuilder();
        int length3 = text.length;
        while (i < length3) {
            sb.append(text[i]);
            i++;
        }
        return sb.toString();
    }

    public static float[] RGBtoHSB(int r, int g, int b) {
        float saturation;
        float hue;
        float hue2;
        float[] hsbvals = new float[3];
        int cmax = Math.max(r, g);
        if (b > cmax) {
            cmax = b;
        }
        int cmin = Math.min(r, g);
        if (b < cmin) {
            cmin = b;
        }
        float brightness = cmax / 255.0f;
        if (cmax != 0) {
            saturation = (cmax - cmin) / cmax;
        } else {
            saturation = 0.0f;
        }
        if (saturation == 0.0f) {
            hue = 0.0f;
        } else {
            float redc = (cmax - r) / (cmax - cmin);
            float greenc = (cmax - g) / (cmax - cmin);
            float bluec = (cmax - b) / (cmax - cmin);
            if (r == cmax) {
                hue2 = bluec - greenc;
            } else if (g == cmax) {
                hue2 = (2.0f + redc) - bluec;
            } else {
                hue2 = (4.0f + greenc) - redc;
            }
            float hue3 = hue2 / 6.0f;
            if (hue3 >= 0.0f) {
                hue = hue3;
            } else {
                hue = 1.0f + hue3;
            }
        }
        hsbvals[0] = hue;
        hsbvals[1] = saturation;
        hsbvals[2] = brightness;
        return hsbvals;
    }

    public static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0;
        int g = 0;
        int b = 0;
        if (saturation == 0.0f) {
            int i = (int) ((255.0f * brightness) + 0.5f);
            b = i;
            g = i;
            r = i;
        } else {
            float h = (hue - ((float) Math.floor(hue))) * 6.0f;
            float f = h - ((float) Math.floor(h));
            float p = (1.0f - saturation) * brightness;
            float q = (1.0f - (saturation * f)) * brightness;
            float t = (1.0f - ((1.0f - f) * saturation)) * brightness;
            switch ((int) h) {
                case 0:
                    r = (int) ((brightness * 255.0f) + 0.5f);
                    g = (int) ((t * 255.0f) + 0.5f);
                    b = (int) ((255.0f * p) + 0.5f);
                    break;
                case 1:
                    r = (int) ((q * 255.0f) + 0.5f);
                    g = (int) ((brightness * 255.0f) + 0.5f);
                    b = (int) ((255.0f * p) + 0.5f);
                    break;
                case 2:
                    r = (int) ((p * 255.0f) + 0.5f);
                    g = (int) ((brightness * 255.0f) + 0.5f);
                    b = (int) ((255.0f * t) + 0.5f);
                    break;
                case 3:
                    r = (int) ((p * 255.0f) + 0.5f);
                    g = (int) ((q * 255.0f) + 0.5f);
                    b = (int) ((255.0f * brightness) + 0.5f);
                    break;
                case 4:
                    r = (int) ((t * 255.0f) + 0.5f);
                    g = (int) ((p * 255.0f) + 0.5f);
                    b = (int) ((255.0f * brightness) + 0.5f);
                    break;
                case 5:
                    r = (int) ((brightness * 255.0f) + 0.5f);
                    g = (int) ((p * 255.0f) + 0.5f);
                    b = (int) ((255.0f * q) + 0.5f);
                    break;
            }
        }
        return (-16777216) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
    }

    public static float computePerceivedBrightness(int color) {
        return (((Color.red(color) * 0.2126f) + (Color.green(color) * 0.7152f)) + (Color.blue(color) * 0.0722f)) / 255.0f;
    }

    public static int getPatternColor(int color) {
        return getPatternColor(color, false);
    }

    public static int getPatternColor(int color, boolean alwaysDark) {
        float[] hsb = RGBtoHSB(Color.red(color), Color.green(color), Color.blue(color));
        if (hsb[1] > 0.0f || (hsb[2] < 1.0f && hsb[2] > 0.0f)) {
            hsb[1] = Math.min(1.0f, hsb[1] + (alwaysDark ? 0.15f : 0.05f) + ((1.0f - hsb[1]) * 0.1f));
        }
        if (alwaysDark || hsb[2] > 0.5f) {
            hsb[2] = Math.max(0.0f, hsb[2] * 0.65f);
        } else {
            hsb[2] = Math.max(0.0f, Math.min(1.0f, 1.0f - (hsb[2] * 0.65f)));
        }
        return HSBtoRGB(hsb[0], hsb[1], hsb[2]) & (alwaysDark ? -1711276033 : 1728053247);
    }

    public static int getPatternSideColor(int color) {
        float[] hsb = RGBtoHSB(Color.red(color), Color.green(color), Color.blue(color));
        hsb[1] = Math.min(1.0f, hsb[1] + 0.05f);
        if (hsb[2] > 0.5f) {
            hsb[2] = Math.max(0.0f, hsb[2] * 0.9f);
        } else {
            hsb[2] = Math.max(0.0f, hsb[2] * 0.9f);
        }
        return HSBtoRGB(hsb[0], hsb[1], hsb[2]) | (-16777216);
    }

    public static int getWallpaperRotation(int angle, boolean iOS) {
        int angle2;
        if (iOS) {
            angle2 = angle + 180;
        } else {
            angle2 = angle - 180;
        }
        while (angle2 >= 360) {
            angle2 -= 360;
        }
        while (angle2 < 0) {
            angle2 += 360;
        }
        return angle2;
    }

    public static String getWallPaperUrl(Object object) {
        if (!(object instanceof TLRPC.TL_wallPaper)) {
            if (object instanceof WallpapersListActivity.ColorWallpaper) {
                return ((WallpapersListActivity.ColorWallpaper) object).getUrl();
            }
            return null;
        }
        TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) object;
        String link = "https://" + MessagesController.getInstance(UserConfig.selectedAccount).linkPrefix + "/bg/" + wallPaper.slug;
        StringBuilder modes = new StringBuilder();
        if (wallPaper.settings != null) {
            if (wallPaper.settings.blur) {
                modes.append("blur");
            }
            if (wallPaper.settings.motion) {
                if (modes.length() > 0) {
                    modes.append("+");
                }
                modes.append("motion");
            }
        }
        if (modes.length() > 0) {
            return link + "?mode=" + modes.toString();
        }
        return link;
    }

    public static float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((f - 0.5f) * 0.47123894f);
    }

    public static void makeAccessibilityAnnouncement(CharSequence what) {
        AccessibilityManager am = (AccessibilityManager) ApplicationLoader.applicationContext.getSystemService("accessibility");
        if (am.isEnabled()) {
            AccessibilityEvent ev = AccessibilityEvent.obtain();
            ev.setEventType(16384);
            ev.getText().add(what);
            am.sendAccessibilityEvent(ev);
        }
    }

    public static int getOffsetColor(int color1, int color2, float offset, float alpha) {
        int rF = Color.red(color2);
        int gF = Color.green(color2);
        int bF = Color.blue(color2);
        int aF = Color.alpha(color2);
        int rS = Color.red(color1);
        int gS = Color.green(color1);
        int bS = Color.blue(color1);
        int aS = Color.alpha(color1);
        return Color.argb((int) ((aS + ((aF - aS) * offset)) * alpha), (int) (rS + ((rF - rS) * offset)), (int) (gS + ((gF - gS) * offset)), (int) (bS + ((bF - bS) * offset)));
    }

    public static int indexOfIgnoreCase(String origin, String searchStr) {
        if (searchStr.isEmpty() || origin.isEmpty()) {
            return origin.indexOf(searchStr);
        }
        for (int i = 0; i < origin.length() && searchStr.length() + i <= origin.length(); i++) {
            int j = 0;
            for (int ii = i; ii < origin.length() && j < searchStr.length(); ii++) {
                char c = Character.toLowerCase(origin.charAt(ii));
                char c2 = Character.toLowerCase(searchStr.charAt(j));
                if (c != c2) {
                    break;
                }
                j++;
            }
            if (j == searchStr.length()) {
                return i;
            }
        }
        return -1;
    }

    public static int lerp(int a, int b, float f) {
        return (int) (a + ((b - a) * f));
    }

    public static float lerp(float a, float b, float f) {
        return ((b - a) * f) + a;
    }

    public static float lerp(float[] ab, float f) {
        return lerp(ab[0], ab[1], f);
    }

    public static int lerpColor(int a, int b, float f) {
        return Color.argb(lerp(Color.alpha(a), Color.alpha(b), f), lerp(Color.red(a), Color.red(b), f), lerp(Color.green(a), Color.green(b), f), lerp(Color.blue(a), Color.blue(b), f));
    }

    public static void lerp(RectF a, RectF b, float f, RectF to) {
        if (to != null) {
            to.set(lerp(a.left, b.left, f), lerp(a.top, b.top, f), lerp(a.right, b.right, f), lerp(a.bottom, b.bottom, f));
        }
    }

    public static void lerp(Rect a, Rect b, float f, Rect to) {
        if (to != null) {
            to.set(lerp(a.left, b.left, f), lerp(a.top, b.top, f), lerp(a.right, b.right, f), lerp(a.bottom, b.bottom, f));
        }
    }

    public static float computeDampingRatio(float tension, float friction, float mass) {
        return friction / (((float) Math.sqrt(mass * tension)) * 2.0f);
    }

    public static boolean hasFlagSecureFragment() {
        return flagSecureFragment != null;
    }

    public static void setFlagSecure(BaseFragment parentFragment, boolean set) {
        if (parentFragment == null || parentFragment.getParentActivity() == null) {
            return;
        }
        if (set) {
            try {
                parentFragment.getParentActivity().getWindow().setFlags(8192, 8192);
                flagSecureFragment = new WeakReference<>(parentFragment);
                return;
            } catch (Exception e) {
                return;
            }
        }
        WeakReference<BaseFragment> weakReference = flagSecureFragment;
        if (weakReference != null && weakReference.get() == parentFragment) {
            try {
                parentFragment.getParentActivity().getWindow().clearFlags(8192);
            } catch (Exception e2) {
            }
            flagSecureFragment = null;
        }
    }

    public static Runnable registerFlagSecure(final Window window) {
        final ArrayList<Long> reasonIds;
        final long reasonId = (long) (Math.random() * 9.99999999E8d);
        HashMap<Window, ArrayList<Long>> hashMap = flagSecureReasons;
        if (hashMap.containsKey(window)) {
            reasonIds = hashMap.get(window);
        } else {
            ArrayList<Long> reasonIds2 = new ArrayList<>();
            hashMap.put(window, reasonIds2);
            reasonIds = reasonIds2;
        }
        reasonIds.add(Long.valueOf(reasonId));
        updateFlagSecure(window);
        return new Runnable() { // from class: org.telegram.messenger.AndroidUtilities$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                AndroidUtilities.lambda$registerFlagSecure$11(reasonIds, reasonId, window);
            }
        };
    }

    public static /* synthetic */ void lambda$registerFlagSecure$11(ArrayList reasonIds, long reasonId, Window window) {
        reasonIds.remove(Long.valueOf(reasonId));
        updateFlagSecure(window);
    }

    private static void updateFlagSecure(Window window) {
        if (Build.VERSION.SDK_INT < 23 || window == null) {
            return;
        }
        HashMap<Window, ArrayList<Long>> hashMap = flagSecureReasons;
        boolean value = hashMap.containsKey(window) && hashMap.get(window).size() > 0;
        try {
            if (value) {
                window.addFlags(8192);
            } else {
                window.clearFlags(8192);
            }
        } catch (Exception e) {
        }
    }

    public static void openSharing(BaseFragment fragment, String url) {
        if (fragment == null || fragment.getParentActivity() == null) {
            return;
        }
        fragment.showDialog(new ShareAlert(fragment.getParentActivity(), null, url, false, url, false));
    }

    public static boolean allowScreenCapture() {
        return SharedConfig.passcodeHash.length() == 0 || SharedConfig.allowScreenCapture;
    }

    public static File getSharingDirectory() {
        return new File(FileLoader.getDirectory(4), "sharing/");
    }

    public static String getCertificateSHA256Fingerprint() {
        PackageManager pm = ApplicationLoader.applicationContext.getPackageManager();
        String packageName = ApplicationLoader.applicationContext.getPackageName();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 64);
            Signature[] signatures = packageInfo.signatures;
            byte[] cert = signatures[0].toByteArray();
            InputStream input = new ByteArrayInputStream(cert);
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            X509Certificate c = (X509Certificate) cf.generateCertificate(input);
            return Utilities.bytesToHex(Utilities.computeSHA256(c.getEncoded()));
        } catch (Throwable th) {
            return "";
        }
    }

    public static boolean isPunctuationCharacter(char ch) {
        if (charactersMap == null) {
            charactersMap = new HashSet<>();
            int a = 0;
            while (true) {
                char[] cArr = characters;
                if (a >= cArr.length) {
                    break;
                }
                charactersMap.add(Character.valueOf(cArr[a]));
                a++;
            }
        }
        return charactersMap.contains(Character.valueOf(ch));
    }

    public static int getColorDistance(int color1, int color2) {
        int r1 = Color.red(color1);
        int g1 = Color.green(color1);
        int b1 = Color.blue(color1);
        int r2 = Color.red(color2);
        int g2 = Color.green(color2);
        int b2 = Color.blue(color2);
        int rMean = (r1 + r2) / 2;
        int r = r1 - r2;
        int g = g1 - g2;
        int b = b1 - b2;
        return ((((rMean + 512) * r) * r) >> 8) + (g * 4 * g) + ((((767 - rMean) * b) * b) >> 8);
    }

    public static int getAverageColor(int color1, int color2) {
        int r1 = Color.red(color1);
        int r2 = Color.red(color2);
        int g1 = Color.green(color1);
        int g2 = Color.green(color2);
        int b1 = Color.blue(color1);
        int b2 = Color.blue(color2);
        return Color.argb(255, (r1 / 2) + (r2 / 2), (g1 / 2) + (g2 / 2), (b1 / 2) + (b2 / 2));
    }

    public static void setLightStatusBar(Window window, boolean enable) {
        setLightStatusBar(window, enable, false);
    }

    public static void setLightStatusBar(Window window, boolean enable, boolean forceTransparentStatusbar) {
        if (Build.VERSION.SDK_INT >= 23) {
            View decorView = window.getDecorView();
            int flags = decorView.getSystemUiVisibility();
            if (enable) {
                if ((flags & 8192) == 0) {
                    decorView.setSystemUiVisibility(flags | 8192);
                }
                if (!SharedConfig.noStatusBar && !forceTransparentStatusbar) {
                    window.setStatusBarColor(LIGHT_STATUS_BAR_OVERLAY);
                    return;
                } else {
                    window.setStatusBarColor(0);
                    return;
                }
            }
            if ((flags & 8192) != 0) {
                decorView.setSystemUiVisibility(flags & (-8193));
            }
            if (!SharedConfig.noStatusBar && !forceTransparentStatusbar) {
                window.setStatusBarColor(DARK_STATUS_BAR_OVERLAY);
            } else {
                window.setStatusBarColor(0);
            }
        }
    }

    public static boolean getLightNavigationBar(Window window) {
        if (Build.VERSION.SDK_INT >= 26) {
            View decorView = window.getDecorView();
            int flags = decorView.getSystemUiVisibility();
            return (flags & 16) > 0;
        }
        return false;
    }

    public static void setLightNavigationBar(View view, boolean enable) {
        int flags;
        if (view != null && Build.VERSION.SDK_INT >= 26) {
            int flags2 = view.getSystemUiVisibility();
            if (enable) {
                flags = flags2 | 16;
            } else {
                flags = flags2 & (-17);
            }
            view.setSystemUiVisibility(flags);
        }
    }

    public static void setLightNavigationBar(Window window, boolean enable) {
        if (window != null) {
            setLightNavigationBar(window.getDecorView(), enable);
        }
    }

    public static void setNavigationBarColor(Window window, int color) {
        setNavigationBarColor(window, color, true);
    }

    public static void setNavigationBarColor(Window window, int color, boolean animated) {
        setNavigationBarColor(window, color, animated, null);
    }

    public static void setNavigationBarColor(final Window window, int color, boolean animated, final IntColorCallback onUpdate) {
        ValueAnimator animator;
        if (Build.VERSION.SDK_INT >= 21) {
            HashMap<Window, ValueAnimator> hashMap = navigationBarColorAnimators;
            if (hashMap != null && (animator = hashMap.get(window)) != null) {
                animator.cancel();
                navigationBarColorAnimators.remove(window);
            }
            if (!animated) {
                if (onUpdate != null) {
                    onUpdate.run(color);
                }
                try {
                    window.setNavigationBarColor(color);
                    return;
                } catch (Exception e) {
                    return;
                }
            }
            ValueAnimator animator2 = ValueAnimator.ofArgb(window.getNavigationBarColor(), color);
            animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.messenger.AndroidUtilities$$ExternalSyntheticLambda6
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    AndroidUtilities.lambda$setNavigationBarColor$12(AndroidUtilities.IntColorCallback.this, window, valueAnimator);
                }
            });
            animator2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.messenger.AndroidUtilities.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (AndroidUtilities.navigationBarColorAnimators != null) {
                        AndroidUtilities.navigationBarColorAnimators.remove(window);
                    }
                }
            });
            animator2.setDuration(200L);
            animator2.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animator2.start();
            if (navigationBarColorAnimators == null) {
                navigationBarColorAnimators = new HashMap<>();
            }
            navigationBarColorAnimators.put(window, animator2);
        }
    }

    public static /* synthetic */ void lambda$setNavigationBarColor$12(IntColorCallback onUpdate, Window window, ValueAnimator a) {
        int tcolor = ((Integer) a.getAnimatedValue()).intValue();
        if (onUpdate != null) {
            onUpdate.run(tcolor);
        }
        try {
            window.setNavigationBarColor(tcolor);
        } catch (Exception e) {
        }
    }

    public static boolean checkHostForPunycode(String url) {
        if (url == null) {
            return false;
        }
        boolean hasLatin = false;
        boolean hasNonLatin = false;
        try {
            int N = url.length();
            for (int a = 0; a < N; a++) {
                char ch = url.charAt(a);
                if (ch != '.' && ch != '-' && ch != '/' && ch != '+' && (ch < '0' || ch > '9')) {
                    if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                        hasLatin = true;
                    } else {
                        hasNonLatin = true;
                    }
                    if (hasLatin && hasNonLatin) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return hasLatin && hasNonLatin;
    }

    public static boolean shouldShowUrlInAlert(String url) {
        try {
            Uri uri = Uri.parse(url);
            String url2 = uri.getHost();
            return checkHostForPunycode(url2);
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    public static void scrollToFragmentRow(ActionBarLayout parentLayout, final String rowName) {
        if (parentLayout == null || rowName == null) {
            return;
        }
        final BaseFragment openingFragment = parentLayout.fragmentsStack.get(parentLayout.fragmentsStack.size() - 1);
        try {
            Field listViewField = openingFragment.getClass().getDeclaredField("listView");
            listViewField.setAccessible(true);
            final RecyclerListView listView = (RecyclerListView) listViewField.get(openingFragment);
            RecyclerListView.IntReturnCallback callback = new RecyclerListView.IntReturnCallback() { // from class: org.telegram.messenger.AndroidUtilities$$ExternalSyntheticLambda5
                @Override // org.telegram.ui.Components.RecyclerListView.IntReturnCallback
                public final int run() {
                    return AndroidUtilities.lambda$scrollToFragmentRow$13(BaseFragment.this, rowName, listView);
                }
            };
            listView.highlightRow(callback);
            listViewField.setAccessible(false);
        } catch (Throwable th) {
        }
    }

    public static /* synthetic */ int lambda$scrollToFragmentRow$13(BaseFragment openingFragment, String rowName, RecyclerListView listView) {
        int position = -1;
        try {
            Field rowField = openingFragment.getClass().getDeclaredField(rowName);
            rowField.setAccessible(true);
            LinearLayoutManager layoutManager = (LinearLayoutManager) listView.getLayoutManager();
            position = rowField.getInt(openingFragment);
            layoutManager.scrollToPositionWithOffset(position, dp(60.0f));
            rowField.setAccessible(false);
            return position;
        } catch (Throwable th) {
            return position;
        }
    }

    public static boolean checkInlinePermissions(Context context) {
        return Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(context);
    }

    public static void updateVisibleRows(RecyclerListView listView) {
        RecyclerView.Adapter adapter;
        RecyclerView.ViewHolder holder;
        if (listView == null || (adapter = listView.getAdapter()) == null) {
            return;
        }
        for (int i = 0; i < listView.getChildCount(); i++) {
            View child = listView.getChildAt(i);
            int p = listView.getChildAdapterPosition(child);
            if (p >= 0 && (holder = listView.getChildViewHolder(child)) != null && !holder.shouldIgnore()) {
                adapter.onBindViewHolder(holder, p);
            }
        }
    }

    public static void updateImageViewImageAnimated(ImageView imageView, int newIcon) {
        updateImageViewImageAnimated(imageView, ContextCompat.getDrawable(imageView.getContext(), newIcon));
    }

    public static void updateImageViewImageAnimated(final ImageView imageView, final Drawable newIcon) {
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(150L);
        final AtomicBoolean changed = new AtomicBoolean();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.messenger.AndroidUtilities$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                AndroidUtilities.lambda$updateImageViewImageAnimated$14(imageView, changed, newIcon, valueAnimator);
            }
        });
        animator.start();
    }

    public static /* synthetic */ void lambda$updateImageViewImageAnimated$14(ImageView imageView, AtomicBoolean changed, Drawable newIcon, ValueAnimator animation) {
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        float scale = Math.abs(val - 0.5f) + 0.5f;
        imageView.setScaleX(scale);
        imageView.setScaleY(scale);
        if (val >= 0.5f && !changed.get()) {
            changed.set(true);
            imageView.setImageDrawable(newIcon);
        }
    }

    public static void updateViewVisibilityAnimated(View view, boolean show) {
        updateViewVisibilityAnimated(view, show, 1.0f, true);
    }

    public static void updateViewVisibilityAnimated(View view, boolean show, float scaleFactor, boolean animated) {
        if (view == null) {
            return;
        }
        if (view.getParent() == null) {
            animated = false;
        }
        int i = 0;
        Integer num = null;
        if (!animated) {
            view.animate().setListener(null).cancel();
            if (!show) {
                i = 8;
            }
            view.setVisibility(i);
            if (show) {
                num = 1;
            }
            view.setTag(num);
            view.setAlpha(1.0f);
            view.setScaleX(1.0f);
            view.setScaleY(1.0f);
        } else if (show && view.getTag() == null) {
            view.animate().setListener(null).cancel();
            if (view.getVisibility() != 0) {
                view.setVisibility(0);
                view.setAlpha(0.0f);
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            }
            view.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).setDuration(150L).start();
            view.setTag(1);
        } else if (!show && view.getTag() != null) {
            view.animate().setListener(null).cancel();
            view.animate().alpha(0.0f).scaleY(scaleFactor).scaleX(scaleFactor).setListener(new HideViewAfterAnimation(view)).setDuration(150L).start();
            view.setTag(null);
        }
    }

    public static long getPrefIntOrLong(SharedPreferences preferences, String key, long defaultValue) {
        try {
            return preferences.getLong(key, defaultValue);
        } catch (Exception e) {
            return preferences.getInt(key, (int) defaultValue);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x005c A[Catch: all -> 0x0087, TryCatch #4 {Exception -> 0x0081, blocks: (B:30:0x007d, B:3:0x0002, B:5:0x000c, B:6:0x0010, B:7:0x0021, B:9:0x0025, B:13:0x002d, B:16:0x0036, B:18:0x004a, B:20:0x0057, B:22:0x005c, B:23:0x0061), top: B:36:0x0002 }] */
    /* JADX WARN: Removed duplicated region for block: B:23:0x0061 A[Catch: all -> 0x0087, TRY_LEAVE, TryCatch #4 {Exception -> 0x0081, blocks: (B:30:0x007d, B:3:0x0002, B:5:0x000c, B:6:0x0010, B:7:0x0021, B:9:0x0025, B:13:0x002d, B:16:0x0036, B:18:0x004a, B:20:0x0057, B:22:0x005c, B:23:0x0061), top: B:36:0x0002 }] */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0070 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static android.graphics.Bitmap getScaledBitmap(float r7, float r8, java.lang.String r9, java.lang.String r10, int r11) {
        /*
            r0 = 0
            r1 = 0
            android.graphics.BitmapFactory$Options r2 = new android.graphics.BitmapFactory$Options     // Catch: java.lang.Throwable -> L87
            r2.<init>()     // Catch: java.lang.Throwable -> L87
            r3 = 1
            r2.inJustDecodeBounds = r3     // Catch: java.lang.Throwable -> L87
            if (r9 == 0) goto L10
            android.graphics.BitmapFactory.decodeFile(r9, r2)     // Catch: java.lang.Throwable -> L87
            goto L21
        L10:
            java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L87
            r4.<init>(r10)     // Catch: java.lang.Throwable -> L87
            r0 = r4
            java.nio.channels.FileChannel r4 = r0.getChannel()     // Catch: java.lang.Throwable -> L87
            long r5 = (long) r11     // Catch: java.lang.Throwable -> L87
            r4.position(r5)     // Catch: java.lang.Throwable -> L87
            android.graphics.BitmapFactory.decodeStream(r0, r1, r2)     // Catch: java.lang.Throwable -> L87
        L21:
            int r4 = r2.outWidth     // Catch: java.lang.Throwable -> L87
            if (r4 <= 0) goto L7b
            int r4 = r2.outHeight     // Catch: java.lang.Throwable -> L87
            if (r4 <= 0) goto L7b
            int r4 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r4 <= 0) goto L36
            int r4 = r2.outWidth     // Catch: java.lang.Throwable -> L87
            int r5 = r2.outHeight     // Catch: java.lang.Throwable -> L87
            if (r4 >= r5) goto L36
            r4 = r7
            r7 = r8
            r8 = r4
        L36:
            int r4 = r2.outWidth     // Catch: java.lang.Throwable -> L87
            float r4 = (float) r4     // Catch: java.lang.Throwable -> L87
            float r4 = r4 / r7
            int r5 = r2.outHeight     // Catch: java.lang.Throwable -> L87
            float r5 = (float) r5     // Catch: java.lang.Throwable -> L87
            float r5 = r5 / r8
            float r4 = java.lang.Math.min(r4, r5)     // Catch: java.lang.Throwable -> L87
            r2.inSampleSize = r3     // Catch: java.lang.Throwable -> L87
            r3 = 1065353216(0x3f800000, float:1.0)
            int r3 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r3 <= 0) goto L57
        L4a:
            int r3 = r2.inSampleSize     // Catch: java.lang.Throwable -> L87
            int r3 = r3 * 2
            r2.inSampleSize = r3     // Catch: java.lang.Throwable -> L87
            int r3 = r2.inSampleSize     // Catch: java.lang.Throwable -> L87
            float r3 = (float) r3     // Catch: java.lang.Throwable -> L87
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 < 0) goto L4a
        L57:
            r3 = 0
            r2.inJustDecodeBounds = r3     // Catch: java.lang.Throwable -> L87
            if (r9 == 0) goto L61
            android.graphics.Bitmap r1 = android.graphics.BitmapFactory.decodeFile(r9, r2)     // Catch: java.lang.Throwable -> L87
            goto L6d
        L61:
            java.nio.channels.FileChannel r3 = r0.getChannel()     // Catch: java.lang.Throwable -> L87
            long r5 = (long) r11     // Catch: java.lang.Throwable -> L87
            r3.position(r5)     // Catch: java.lang.Throwable -> L87
            android.graphics.Bitmap r1 = android.graphics.BitmapFactory.decodeStream(r0, r1, r2)     // Catch: java.lang.Throwable -> L87
        L6d:
            if (r0 == 0) goto L79
            r0.close()     // Catch: java.lang.Exception -> L74
            goto L79
        L74:
            r3 = move-exception
            org.telegram.messenger.FileLog.e(r3)
            goto L7a
        L79:
        L7a:
            return r1
        L7b:
            if (r0 == 0) goto L86
            r0.close()     // Catch: java.lang.Exception -> L81
            goto L86
        L81:
            r2 = move-exception
            org.telegram.messenger.FileLog.e(r2)
            goto L91
        L86:
            goto L91
        L87:
            r2 = move-exception
            org.telegram.messenger.FileLog.e(r2)     // Catch: java.lang.Throwable -> L92
            if (r0 == 0) goto L86
            r0.close()     // Catch: java.lang.Exception -> L81
            goto L86
        L91:
            return r1
        L92:
            r1 = move-exception
            if (r0 == 0) goto L9e
            r0.close()     // Catch: java.lang.Exception -> L99
            goto L9e
        L99:
            r2 = move-exception
            org.telegram.messenger.FileLog.e(r2)
            goto L9f
        L9e:
        L9f:
            goto La1
        La0:
            throw r1
        La1:
            goto La0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.getScaledBitmap(float, float, java.lang.String, java.lang.String, int):android.graphics.Bitmap");
    }

    public static Uri getBitmapShareUri(Bitmap bitmap, String fileName, Bitmap.CompressFormat format) {
        File cachePath = getCacheDir();
        if (!cachePath.isDirectory()) {
            try {
                cachePath.mkdirs();
            } catch (Exception e) {
                FileLog.e(e);
                return null;
            }
        }
        File file = new File(cachePath, fileName);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(format, 100, out);
            out.close();
            Uri uriForFile = FileProvider.getUriForFile(ApplicationLoader.applicationContext, "org.telegram.messenger.beta.provider", file);
            out.close();
            return uriForFile;
        } catch (Exception e2) {
            FileLog.e(e2);
            return null;
        }
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isAccessibilityScreenReaderEnabled() {
        return false;
    }
}
