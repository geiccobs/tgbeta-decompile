package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Xml;
import com.huawei.hms.adapter.internal.AvailableCode;
import com.huawei.hms.android.HwBuildEx;
import com.huawei.hms.framework.common.ContainerUtils;
import com.huawei.hms.opendevice.c;
import com.huawei.hms.opendevice.i;
import com.huawei.hms.push.constant.RemoteMessageConst;
import com.huawei.hms.push.e;
import com.huawei.hms.support.hianalytics.HiAnalyticsConstant;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$LangPackString;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_langPackDifference;
import org.telegram.tgnet.TLRPC$TL_langPackLanguage;
import org.telegram.tgnet.TLRPC$TL_langPackString;
import org.telegram.tgnet.TLRPC$TL_langPackStringDeleted;
import org.telegram.tgnet.TLRPC$TL_langPackStringPluralized;
import org.telegram.tgnet.TLRPC$TL_langpack_getDifference;
import org.telegram.tgnet.TLRPC$TL_langpack_getLangPack;
import org.telegram.tgnet.TLRPC$TL_userEmpty;
import org.telegram.tgnet.TLRPC$TL_userStatusLastMonth;
import org.telegram.tgnet.TLRPC$TL_userStatusLastWeek;
import org.telegram.tgnet.TLRPC$TL_userStatusRecently;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.tgnet.TLRPC$Vector;
import org.xmlpull.v1.XmlPullParser;
/* loaded from: classes.dex */
public class LocaleController {
    static final int QUANTITY_FEW = 8;
    static final int QUANTITY_MANY = 16;
    static final int QUANTITY_ONE = 2;
    static final int QUANTITY_OTHER = 0;
    static final int QUANTITY_TWO = 4;
    static final int QUANTITY_ZERO = 1;
    public static boolean is24HourFormat = false;
    public static boolean isRTL = false;
    public static int nameDisplayOrder = 1;
    private static Boolean useImperialSystemType;
    public FastDateFormat chatDate;
    public FastDateFormat chatFullDate;
    private HashMap<String, String> currencyValues;
    private Locale currentLocale;
    private LocaleInfo currentLocaleInfo;
    private PluralRules currentPluralRules;
    private String currentSystemLocale;
    public FastDateFormat formatterBannedUntil;
    public FastDateFormat formatterBannedUntilThisYear;
    public FastDateFormat formatterDay;
    public FastDateFormat formatterDayMonth;
    public FastDateFormat formatterMonthYear;
    public FastDateFormat formatterScheduleDay;
    public FastDateFormat formatterScheduleYear;
    public FastDateFormat formatterStats;
    public FastDateFormat formatterWeek;
    public FastDateFormat formatterWeekLong;
    public FastDateFormat formatterYear;
    public FastDateFormat formatterYearMax;
    private String languageOverride;
    private boolean loadingRemoteLanguages;
    private boolean reloadLastFile;
    private HashMap<String, String> ruTranslitChars;
    private Locale systemDefaultLocale;
    private HashMap<String, String> translitChars;
    private static HashMap<Integer, String> resourcesCacheMap = new HashMap<>();
    private static volatile LocaleController Instance = null;
    private static char[] defaultNumbers = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static char[][] otherNumbers = {new char[]{1632, 1633, 1634, 1635, 1636, 1637, 1638, 1639, 1640, 1641}, new char[]{1776, 1777, 1778, 1779, 1780, 1781, 1782, 1783, 1784, 1785}, new char[]{2406, 2407, 2408, 2409, 2410, 2411, 2412, 2413, 2414, 2415}, new char[]{2790, 2791, 2792, 2793, 2794, 2795, 2796, 2797, 2798, 2799}, new char[]{2662, 2663, 2664, 2665, 2666, 2667, 2668, 2669, 2670, 2671}, new char[]{2534, 2535, 2536, 2537, 2538, 2539, 2540, 2541, 2542, 2543}, new char[]{3302, 3303, 3304, 3305, 3306, 3307, 3308, 3309, 3310, 3311}, new char[]{2918, 2919, 2920, 2921, 2922, 2923, 2924, 2925, 2926, 2927}, new char[]{3430, 3431, 3432, 3433, 3434, 3435, 3436, 3437, 3438, 3439}, new char[]{3046, 3047, 3048, 3049, 3050, 3051, 3052, 3053, 3054, 3055}, new char[]{3174, 3175, 3176, 3177, 3178, 3179, 3180, 3181, 3182, 3183}, new char[]{4160, 4161, 4162, 4163, 4164, 4165, 4166, 4167, 4168, 4169}, new char[]{3872, 3873, 3874, 3875, 3876, 3877, 3878, 3879, 3880, 3881}, new char[]{6160, 6161, 6162, 6163, 6164, 6165, 6166, 6167, 6168, 6169}, new char[]{6112, 6113, 6114, 6115, 6116, 6117, 6118, 6119, 6120, 6121}, new char[]{3664, 3665, 3666, 3667, 3668, 3669, 3670, 3671, 3672, 3673}, new char[]{3792, 3793, 3794, 3795, 3796, 3797, 3798, 3799, 3800, 3801}, new char[]{43472, 43473, 43474, 43475, 43476, 43477, 43478, 43479, 43480, 43481}};
    public FastDateFormat[] formatterScheduleSend = new FastDateFormat[15];
    private HashMap<String, PluralRules> allRules = new HashMap<>();
    private HashMap<String, String> localeValues = new HashMap<>();
    private boolean changingConfiguration = false;
    public ArrayList<LocaleInfo> languages = new ArrayList<>();
    public ArrayList<LocaleInfo> unofficialLanguages = new ArrayList<>();
    public ArrayList<LocaleInfo> remoteLanguages = new ArrayList<>();
    public HashMap<String, LocaleInfo> remoteLanguagesDict = new HashMap<>();
    public HashMap<String, LocaleInfo> languagesDict = new HashMap<>();
    private ArrayList<LocaleInfo> otherLanguages = new ArrayList<>();

    /* loaded from: classes.dex */
    public static abstract class PluralRules {
        abstract int quantityForNumber(int i);
    }

    /* loaded from: classes.dex */
    public static class PluralRules_Breton extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int i) {
            if (i == 0) {
                return 1;
            }
            if (i == 1) {
                return 2;
            }
            if (i == 2) {
                return 4;
            }
            if (i == 3) {
                return 8;
            }
            return i == 6 ? 16 : 0;
        }
    }

    /* loaded from: classes.dex */
    public static class PluralRules_Czech extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int i) {
            if (i == 1) {
                return 2;
            }
            return (i < 2 || i > 4) ? 0 : 8;
        }
    }

    /* loaded from: classes.dex */
    public static class PluralRules_French extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int i) {
            return (i < 0 || i >= 2) ? 0 : 2;
        }
    }

    /* loaded from: classes.dex */
    public static class PluralRules_Langi extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int i) {
            if (i == 0) {
                return 1;
            }
            return i == 1 ? 2 : 0;
        }
    }

    /* loaded from: classes.dex */
    public static class PluralRules_None extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int i) {
            return 0;
        }
    }

    /* loaded from: classes.dex */
    public static class PluralRules_One extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int i) {
            return i == 1 ? 2 : 0;
        }
    }

    /* loaded from: classes.dex */
    public static class PluralRules_Tachelhit extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int i) {
            if (i < 0 || i > 1) {
                return (i < 2 || i > 10) ? 0 : 8;
            }
            return 2;
        }
    }

    /* loaded from: classes.dex */
    public static class PluralRules_Two extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int i) {
            if (i == 1) {
                return 2;
            }
            return i == 2 ? 4 : 0;
        }
    }

    /* loaded from: classes.dex */
    public static class PluralRules_Welsh extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int i) {
            if (i == 0) {
                return 1;
            }
            if (i == 1) {
                return 2;
            }
            if (i == 2) {
                return 4;
            }
            if (i == 3) {
                return 8;
            }
            return i == 6 ? 16 : 0;
        }
    }

    /* loaded from: classes.dex */
    public static class PluralRules_Zero extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int i) {
            return (i == 0 || i == 1) ? 2 : 0;
        }
    }

    private String stringForQuantity(int i) {
        return i != 1 ? i != 2 ? i != 4 ? i != 8 ? i != 16 ? "other" : "many" : "few" : "two" : "one" : "zero";
    }

    /* loaded from: classes.dex */
    public class TimeZoneChangedReceiver extends BroadcastReceiver {
        private TimeZoneChangedReceiver() {
            LocaleController.this = r1;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            ApplicationLoader.applicationHandler.post(new Runnable() { // from class: org.telegram.messenger.LocaleController$TimeZoneChangedReceiver$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    LocaleController.TimeZoneChangedReceiver.this.lambda$onReceive$0();
                }
            });
        }

        public /* synthetic */ void lambda$onReceive$0() {
            if (!LocaleController.this.formatterDayMonth.getTimeZone().equals(TimeZone.getDefault())) {
                LocaleController.getInstance().recreateFormatters();
            }
        }
    }

    /* loaded from: classes.dex */
    public static class LocaleInfo {
        public String baseLangCode;
        public int baseVersion;
        public boolean builtIn;
        public boolean isRtl;
        public String name;
        public String nameEnglish;
        public String pathToFile;
        public String pluralLangCode;
        public int serverIndex;
        public String shortName;
        public int version;

        public String getSaveString() {
            String str = this.baseLangCode;
            if (str == null) {
                str = "";
            }
            TextUtils.isEmpty(this.pluralLangCode);
            return this.name + HiAnalyticsConstant.REPORT_VAL_SEPARATOR + this.nameEnglish + HiAnalyticsConstant.REPORT_VAL_SEPARATOR + this.shortName + HiAnalyticsConstant.REPORT_VAL_SEPARATOR + this.pathToFile + HiAnalyticsConstant.REPORT_VAL_SEPARATOR + this.version + HiAnalyticsConstant.REPORT_VAL_SEPARATOR + str + HiAnalyticsConstant.REPORT_VAL_SEPARATOR + this.pluralLangCode + HiAnalyticsConstant.REPORT_VAL_SEPARATOR + (this.isRtl ? 1 : 0) + HiAnalyticsConstant.REPORT_VAL_SEPARATOR + this.baseVersion + HiAnalyticsConstant.REPORT_VAL_SEPARATOR + this.serverIndex;
        }

        public static LocaleInfo createWithString(String str) {
            LocaleInfo localeInfo = null;
            if (str != null && str.length() != 0) {
                String[] split = str.split("\\|");
                if (split.length >= 4) {
                    localeInfo = new LocaleInfo();
                    boolean z = false;
                    localeInfo.name = split[0];
                    localeInfo.nameEnglish = split[1];
                    localeInfo.shortName = split[2].toLowerCase();
                    localeInfo.pathToFile = split[3];
                    if (split.length >= 5) {
                        localeInfo.version = Utilities.parseInt((CharSequence) split[4]).intValue();
                    }
                    localeInfo.baseLangCode = split.length >= 6 ? split[5] : "";
                    localeInfo.pluralLangCode = split.length >= 7 ? split[6] : localeInfo.shortName;
                    if (split.length >= 8) {
                        if (Utilities.parseInt((CharSequence) split[7]).intValue() == 1) {
                            z = true;
                        }
                        localeInfo.isRtl = z;
                    }
                    if (split.length >= 9) {
                        localeInfo.baseVersion = Utilities.parseInt((CharSequence) split[8]).intValue();
                    }
                    if (split.length >= 10) {
                        localeInfo.serverIndex = Utilities.parseInt((CharSequence) split[9]).intValue();
                    } else {
                        localeInfo.serverIndex = Integer.MAX_VALUE;
                    }
                    if (!TextUtils.isEmpty(localeInfo.baseLangCode)) {
                        localeInfo.baseLangCode = localeInfo.baseLangCode.replace("-", "_");
                    }
                }
            }
            return localeInfo;
        }

        public File getPathToFile() {
            if (isRemote()) {
                File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                return new File(filesDirFixed, "remote_" + this.shortName + ".xml");
            } else if (isUnofficial()) {
                File filesDirFixed2 = ApplicationLoader.getFilesDirFixed();
                return new File(filesDirFixed2, "unofficial_" + this.shortName + ".xml");
            } else if (TextUtils.isEmpty(this.pathToFile)) {
                return null;
            } else {
                return new File(this.pathToFile);
            }
        }

        public File getPathToBaseFile() {
            if (isUnofficial()) {
                File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                return new File(filesDirFixed, "unofficial_base_" + this.shortName + ".xml");
            }
            return null;
        }

        public String getKey() {
            if (this.pathToFile != null && !isRemote() && !isUnofficial()) {
                return "local_" + this.shortName;
            } else if (isUnofficial()) {
                return "unofficial_" + this.shortName;
            } else {
                return this.shortName;
            }
        }

        public boolean hasBaseLang() {
            return isUnofficial() && !TextUtils.isEmpty(this.baseLangCode) && !this.baseLangCode.equals(this.shortName);
        }

        public boolean isRemote() {
            return "remote".equals(this.pathToFile);
        }

        public boolean isUnofficial() {
            return "unofficial".equals(this.pathToFile);
        }

        public boolean isLocal() {
            return !TextUtils.isEmpty(this.pathToFile) && !isRemote() && !isUnofficial();
        }

        public boolean isBuiltIn() {
            return this.builtIn;
        }

        public String getLangCode() {
            return this.shortName.replace("_", "-");
        }

        public String getBaseLangCode() {
            String str = this.baseLangCode;
            return str == null ? "" : str.replace("_", "-");
        }
    }

    public static LocaleController getInstance() {
        LocaleController localeController = Instance;
        if (localeController == null) {
            synchronized (LocaleController.class) {
                localeController = Instance;
                if (localeController == null) {
                    localeController = new LocaleController();
                    Instance = localeController;
                }
            }
        }
        return localeController;
    }

    public LocaleController() {
        LocaleInfo localeInfo;
        boolean z = false;
        addRules(new String[]{"bem", "brx", "da", "de", "el", "en", "eo", "es", "et", "fi", "fo", "gl", "he", "iw", "it", "nb", "nl", "nn", "no", "sv", "af", "bg", "bn", "ca", "eu", "fur", "fy", "gu", "ha", "is", "ku", "lb", "ml", "mr", "nah", "ne", "om", "or", "pa", "pap", "ps", "so", "sq", "sw", "ta", "te", "tk", "ur", "zu", "mn", "gsw", "chr", "rm", "pt", "an", "ast"}, new PluralRules_One());
        addRules(new String[]{"cs", "sk"}, new PluralRules_Czech());
        addRules(new String[]{"ff", "fr", "kab"}, new PluralRules_French());
        addRules(new String[]{"ru", "uk", "be"}, new PluralRules_Balkan());
        addRules(new String[]{"sr", "hr", "bs", "sh"}, new PluralRules_Serbian());
        addRules(new String[]{"lv"}, new PluralRules_Latvian());
        addRules(new String[]{"lt"}, new PluralRules_Lithuanian());
        addRules(new String[]{"pl"}, new PluralRules_Polish());
        addRules(new String[]{"ro", "mo"}, new PluralRules_Romanian());
        addRules(new String[]{"sl"}, new PluralRules_Slovenian());
        addRules(new String[]{"ar"}, new PluralRules_Arabic());
        addRules(new String[]{"mk"}, new PluralRules_Macedonian());
        addRules(new String[]{"cy"}, new PluralRules_Welsh());
        addRules(new String[]{"br"}, new PluralRules_Breton());
        addRules(new String[]{"lag"}, new PluralRules_Langi());
        addRules(new String[]{"shi"}, new PluralRules_Tachelhit());
        addRules(new String[]{"mt"}, new PluralRules_Maltese());
        addRules(new String[]{"ga", "se", "sma", "smi", "smj", "smn", "sms"}, new PluralRules_Two());
        addRules(new String[]{"ak", "am", "bh", "fil", "tl", "guw", "hi", "ln", "mg", "nso", "ti", "wa"}, new PluralRules_Zero());
        addRules(new String[]{"az", "bm", "fa", "ig", "hu", "ja", "kde", "kea", "ko", "my", "ses", "sg", RemoteMessageConst.TO, "tr", "vi", "wo", "yo", "zh", "bo", "dz", "id", "jv", "jw", "ka", "km", "kn", "ms", "th", "in"}, new PluralRules_None());
        LocaleInfo localeInfo2 = new LocaleInfo();
        localeInfo2.name = "English";
        localeInfo2.nameEnglish = "English";
        localeInfo2.pluralLangCode = "en";
        localeInfo2.shortName = "en";
        localeInfo2.pathToFile = null;
        localeInfo2.builtIn = true;
        this.languages.add(localeInfo2);
        this.languagesDict.put(localeInfo2.shortName, localeInfo2);
        LocaleInfo localeInfo3 = new LocaleInfo();
        localeInfo3.name = "Italiano";
        localeInfo3.nameEnglish = "Italian";
        localeInfo3.pluralLangCode = "it";
        localeInfo3.shortName = "it";
        localeInfo3.pathToFile = null;
        localeInfo3.builtIn = true;
        this.languages.add(localeInfo3);
        this.languagesDict.put(localeInfo3.shortName, localeInfo3);
        LocaleInfo localeInfo4 = new LocaleInfo();
        localeInfo4.name = "Espa??ol";
        localeInfo4.nameEnglish = "Spanish";
        localeInfo4.pluralLangCode = "es";
        localeInfo4.shortName = "es";
        localeInfo4.builtIn = true;
        this.languages.add(localeInfo4);
        this.languagesDict.put(localeInfo4.shortName, localeInfo4);
        LocaleInfo localeInfo5 = new LocaleInfo();
        localeInfo5.name = "Deutsch";
        localeInfo5.nameEnglish = "German";
        localeInfo5.pluralLangCode = "de";
        localeInfo5.shortName = "de";
        localeInfo5.pathToFile = null;
        localeInfo5.builtIn = true;
        this.languages.add(localeInfo5);
        this.languagesDict.put(localeInfo5.shortName, localeInfo5);
        LocaleInfo localeInfo6 = new LocaleInfo();
        localeInfo6.name = "Nederlands";
        localeInfo6.nameEnglish = "Dutch";
        localeInfo6.pluralLangCode = "nl";
        localeInfo6.shortName = "nl";
        localeInfo6.pathToFile = null;
        localeInfo6.builtIn = true;
        this.languages.add(localeInfo6);
        this.languagesDict.put(localeInfo6.shortName, localeInfo6);
        LocaleInfo localeInfo7 = new LocaleInfo();
        localeInfo7.name = "??????????????";
        localeInfo7.nameEnglish = "Arabic";
        localeInfo7.pluralLangCode = "ar";
        localeInfo7.shortName = "ar";
        localeInfo7.pathToFile = null;
        localeInfo7.builtIn = true;
        localeInfo7.isRtl = true;
        this.languages.add(localeInfo7);
        this.languagesDict.put(localeInfo7.shortName, localeInfo7);
        LocaleInfo localeInfo8 = new LocaleInfo();
        localeInfo8.name = "Portugu??s (Brasil)";
        localeInfo8.nameEnglish = "Portuguese (Brazil)";
        localeInfo8.pluralLangCode = "pt_br";
        localeInfo8.shortName = "pt_br";
        localeInfo8.pathToFile = null;
        localeInfo8.builtIn = true;
        this.languages.add(localeInfo8);
        this.languagesDict.put(localeInfo8.shortName, localeInfo8);
        LocaleInfo localeInfo9 = new LocaleInfo();
        localeInfo9.name = "?????????";
        localeInfo9.nameEnglish = "Korean";
        localeInfo9.pluralLangCode = "ko";
        localeInfo9.shortName = "ko";
        localeInfo9.pathToFile = null;
        localeInfo9.builtIn = true;
        this.languages.add(localeInfo9);
        this.languagesDict.put(localeInfo9.shortName, localeInfo9);
        loadOtherLanguages();
        if (this.remoteLanguages.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    LocaleController.this.lambda$new$0();
                }
            });
        }
        for (int i = 0; i < this.otherLanguages.size(); i++) {
            LocaleInfo localeInfo10 = this.otherLanguages.get(i);
            this.languages.add(localeInfo10);
            this.languagesDict.put(localeInfo10.getKey(), localeInfo10);
        }
        for (int i2 = 0; i2 < this.remoteLanguages.size(); i2++) {
            LocaleInfo localeInfo11 = this.remoteLanguages.get(i2);
            LocaleInfo languageFromDict = getLanguageFromDict(localeInfo11.getKey());
            if (languageFromDict != null) {
                languageFromDict.pathToFile = localeInfo11.pathToFile;
                languageFromDict.version = localeInfo11.version;
                languageFromDict.baseVersion = localeInfo11.baseVersion;
                languageFromDict.serverIndex = localeInfo11.serverIndex;
                this.remoteLanguages.set(i2, languageFromDict);
            } else {
                this.languages.add(localeInfo11);
                this.languagesDict.put(localeInfo11.getKey(), localeInfo11);
            }
        }
        for (int i3 = 0; i3 < this.unofficialLanguages.size(); i3++) {
            LocaleInfo localeInfo12 = this.unofficialLanguages.get(i3);
            LocaleInfo languageFromDict2 = getLanguageFromDict(localeInfo12.getKey());
            if (languageFromDict2 != null) {
                languageFromDict2.pathToFile = localeInfo12.pathToFile;
                languageFromDict2.version = localeInfo12.version;
                languageFromDict2.baseVersion = localeInfo12.baseVersion;
                languageFromDict2.serverIndex = localeInfo12.serverIndex;
                this.unofficialLanguages.set(i3, languageFromDict2);
            } else {
                this.languagesDict.put(localeInfo12.getKey(), localeInfo12);
            }
        }
        this.systemDefaultLocale = Locale.getDefault();
        is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
        try {
            String string = MessagesController.getGlobalMainSettings().getString("language", null);
            if (string != null) {
                localeInfo = getLanguageFromDict(string);
                if (localeInfo != null) {
                    z = true;
                }
            } else {
                localeInfo = null;
            }
            if (localeInfo == null && this.systemDefaultLocale.getLanguage() != null) {
                localeInfo = getLanguageFromDict(this.systemDefaultLocale.getLanguage());
            }
            if (localeInfo == null && (localeInfo = getLanguageFromDict(getLocaleString(this.systemDefaultLocale))) == null) {
                localeInfo = getLanguageFromDict("en");
            }
            applyLanguage(localeInfo, z, true, UserConfig.selectedAccount);
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            ApplicationLoader.applicationContext.registerReceiver(new TimeZoneChangedReceiver(), new IntentFilter("android.intent.action.TIMEZONE_CHANGED"));
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                LocaleController.this.lambda$new$1();
            }
        });
    }

    public /* synthetic */ void lambda$new$0() {
        loadRemoteLanguages(UserConfig.selectedAccount);
    }

    public /* synthetic */ void lambda$new$1() {
        this.currentSystemLocale = getSystemLocaleStringIso639();
    }

    public static String getLanguageFlag(String str) {
        if (str.length() != 2 || str.equals("YL")) {
            return null;
        }
        if (str.equals("XG")) {
            return "????";
        }
        if (str.equals("XV")) {
            return "????";
        }
        char[] charArray = str.toCharArray();
        return new String(new char[]{CharacterCompat.highSurrogate(127397), CharacterCompat.lowSurrogate(charArray[0] + 61861), CharacterCompat.highSurrogate(127397), CharacterCompat.lowSurrogate(charArray[1] + 61861)});
    }

    public LocaleInfo getLanguageFromDict(String str) {
        if (str == null) {
            return null;
        }
        return this.languagesDict.get(str.toLowerCase().replace("-", "_"));
    }

    public LocaleInfo getBuiltinLanguageByPlural(String str) {
        String str2;
        for (LocaleInfo localeInfo : this.languagesDict.values()) {
            String str3 = localeInfo.pathToFile;
            if (str3 != null && str3.equals("remote") && (str2 = localeInfo.pluralLangCode) != null && str2.equals(str)) {
                return localeInfo;
            }
        }
        return null;
    }

    private void addRules(String[] strArr, PluralRules pluralRules) {
        for (String str : strArr) {
            this.allRules.put(str, pluralRules);
        }
    }

    public Locale getSystemDefaultLocale() {
        return this.systemDefaultLocale;
    }

    public boolean isCurrentLocalLocale() {
        return this.currentLocaleInfo.isLocal();
    }

    public void reloadCurrentRemoteLocale(int i, String str, boolean z) {
        if (str != null) {
            str = str.replace("-", "_");
        }
        if (str != null) {
            LocaleInfo localeInfo = this.currentLocaleInfo;
            if (localeInfo == null) {
                return;
            }
            if (!str.equals(localeInfo.shortName) && !str.equals(this.currentLocaleInfo.baseLangCode)) {
                return;
            }
        }
        applyRemoteLanguage(this.currentLocaleInfo, str, z, i);
    }

    public void checkUpdateForCurrentRemoteLocale(int i, int i2, int i3) {
        LocaleInfo localeInfo = this.currentLocaleInfo;
        if (localeInfo != null) {
            if (!localeInfo.isRemote() && !this.currentLocaleInfo.isUnofficial()) {
                return;
            }
            if (this.currentLocaleInfo.hasBaseLang()) {
                LocaleInfo localeInfo2 = this.currentLocaleInfo;
                if (localeInfo2.baseVersion < i3) {
                    applyRemoteLanguage(localeInfo2, localeInfo2.baseLangCode, false, i);
                }
            }
            LocaleInfo localeInfo3 = this.currentLocaleInfo;
            if (localeInfo3.version >= i2) {
                return;
            }
            applyRemoteLanguage(localeInfo3, localeInfo3.shortName, false, i);
        }
    }

    private String getLocaleString(Locale locale) {
        if (locale == null) {
            return "en";
        }
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        if (language.length() == 0 && country.length() == 0) {
            return "en";
        }
        StringBuilder sb = new StringBuilder(11);
        sb.append(language);
        if (country.length() > 0 || variant.length() > 0) {
            sb.append('_');
        }
        sb.append(country);
        if (variant.length() > 0) {
            sb.append('_');
        }
        sb.append(variant);
        return sb.toString();
    }

    public static String getSystemLocaleStringIso639() {
        Locale systemDefaultLocale = getInstance().getSystemDefaultLocale();
        if (systemDefaultLocale == null) {
            return "en";
        }
        String language = systemDefaultLocale.getLanguage();
        String country = systemDefaultLocale.getCountry();
        String variant = systemDefaultLocale.getVariant();
        if (language.length() == 0 && country.length() == 0) {
            return "en";
        }
        StringBuilder sb = new StringBuilder(11);
        sb.append(language);
        if (country.length() > 0 || variant.length() > 0) {
            sb.append('-');
        }
        sb.append(country);
        if (variant.length() > 0) {
            sb.append('_');
        }
        sb.append(variant);
        return sb.toString();
    }

    public static String getLocaleStringIso639() {
        LocaleInfo localeInfo = getInstance().currentLocaleInfo;
        if (localeInfo != null) {
            return localeInfo.getLangCode();
        }
        Locale locale = getInstance().currentLocale;
        if (locale == null) {
            return "en";
        }
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        if (language.length() == 0 && country.length() == 0) {
            return "en";
        }
        StringBuilder sb = new StringBuilder(11);
        sb.append(language);
        if (country.length() > 0 || variant.length() > 0) {
            sb.append('-');
        }
        sb.append(country);
        if (variant.length() > 0) {
            sb.append('_');
        }
        sb.append(variant);
        return sb.toString();
    }

    public static String getLocaleAlias(String str) {
        if (str == null) {
            return null;
        }
        char c = 65535;
        switch (str.hashCode()) {
            case 3325:
                if (str.equals("he")) {
                    c = 0;
                    break;
                }
                break;
            case 3355:
                if (str.equals("id")) {
                    c = 1;
                    break;
                }
                break;
            case 3365:
                if (str.equals("in")) {
                    c = 2;
                    break;
                }
                break;
            case 3374:
                if (str.equals("iw")) {
                    c = 3;
                    break;
                }
                break;
            case 3391:
                if (str.equals("ji")) {
                    c = 4;
                    break;
                }
                break;
            case 3404:
                if (str.equals("jv")) {
                    c = 5;
                    break;
                }
                break;
            case 3405:
                if (str.equals("jw")) {
                    c = 6;
                    break;
                }
                break;
            case 3508:
                if (str.equals("nb")) {
                    c = 7;
                    break;
                }
                break;
            case 3521:
                if (str.equals("no")) {
                    c = '\b';
                    break;
                }
                break;
            case 3704:
                if (str.equals("tl")) {
                    c = '\t';
                    break;
                }
                break;
            case 3856:
                if (str.equals("yi")) {
                    c = '\n';
                    break;
                }
                break;
            case 101385:
                if (str.equals("fil")) {
                    c = 11;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return "iw";
            case 1:
                return "in";
            case 2:
                return "id";
            case 3:
                return "he";
            case 4:
                return "yi";
            case 5:
                return "jw";
            case 6:
                return "jv";
            case 7:
                return "no";
            case '\b':
                return "nb";
            case '\t':
                return "fil";
            case '\n':
                return "ji";
            case 11:
                return "tl";
            default:
                return null;
        }
    }

    public boolean applyLanguageFile(File file, int i) {
        try {
            HashMap<String, String> localeFileStrings = getLocaleFileStrings(file);
            String str = localeFileStrings.get("LanguageName");
            String str2 = localeFileStrings.get("LanguageNameInEnglish");
            String str3 = localeFileStrings.get("LanguageCode");
            if (str != null && str.length() > 0 && str2 != null && str2.length() > 0 && str3 != null && str3.length() > 0 && !str.contains(ContainerUtils.FIELD_DELIMITER) && !str.contains(HiAnalyticsConstant.REPORT_VAL_SEPARATOR) && !str2.contains(ContainerUtils.FIELD_DELIMITER) && !str2.contains(HiAnalyticsConstant.REPORT_VAL_SEPARATOR) && !str3.contains(ContainerUtils.FIELD_DELIMITER) && !str3.contains(HiAnalyticsConstant.REPORT_VAL_SEPARATOR) && !str3.contains("/") && !str3.contains("\\")) {
                File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                File file2 = new File(filesDirFixed, str3 + ".xml");
                if (!AndroidUtilities.copyFile(file, file2)) {
                    return false;
                }
                LocaleInfo languageFromDict = getLanguageFromDict("local_" + str3.toLowerCase());
                if (languageFromDict == null) {
                    languageFromDict = new LocaleInfo();
                    languageFromDict.name = str;
                    languageFromDict.nameEnglish = str2;
                    String lowerCase = str3.toLowerCase();
                    languageFromDict.shortName = lowerCase;
                    languageFromDict.pluralLangCode = lowerCase;
                    languageFromDict.pathToFile = file2.getAbsolutePath();
                    this.languages.add(languageFromDict);
                    this.languagesDict.put(languageFromDict.getKey(), languageFromDict);
                    this.otherLanguages.add(languageFromDict);
                    saveOtherLanguages();
                }
                this.localeValues = localeFileStrings;
                applyLanguage(languageFromDict, true, false, true, false, i);
                return true;
            }
            return false;
        } catch (Exception e) {
            FileLog.e(e);
        }
        return false;
    }

    private void saveOtherLanguages() {
        SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0).edit();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.otherLanguages.size(); i++) {
            String saveString = this.otherLanguages.get(i).getSaveString();
            if (saveString != null) {
                if (sb.length() != 0) {
                    sb.append(ContainerUtils.FIELD_DELIMITER);
                }
                sb.append(saveString);
            }
        }
        edit.putString("locales", sb.toString());
        sb.setLength(0);
        for (int i2 = 0; i2 < this.remoteLanguages.size(); i2++) {
            String saveString2 = this.remoteLanguages.get(i2).getSaveString();
            if (saveString2 != null) {
                if (sb.length() != 0) {
                    sb.append(ContainerUtils.FIELD_DELIMITER);
                }
                sb.append(saveString2);
            }
        }
        edit.putString("remote", sb.toString());
        sb.setLength(0);
        for (int i3 = 0; i3 < this.unofficialLanguages.size(); i3++) {
            String saveString3 = this.unofficialLanguages.get(i3).getSaveString();
            if (saveString3 != null) {
                if (sb.length() != 0) {
                    sb.append(ContainerUtils.FIELD_DELIMITER);
                }
                sb.append(saveString3);
            }
        }
        edit.putString("unofficial", sb.toString());
        edit.commit();
    }

    public boolean deleteLanguage(LocaleInfo localeInfo, int i) {
        if (localeInfo.pathToFile == null || (localeInfo.isRemote() && localeInfo.serverIndex != Integer.MAX_VALUE)) {
            return false;
        }
        if (this.currentLocaleInfo == localeInfo) {
            LocaleInfo localeInfo2 = null;
            if (this.systemDefaultLocale.getLanguage() != null) {
                localeInfo2 = getLanguageFromDict(this.systemDefaultLocale.getLanguage());
            }
            if (localeInfo2 == null) {
                localeInfo2 = getLanguageFromDict(getLocaleString(this.systemDefaultLocale));
            }
            if (localeInfo2 == null) {
                localeInfo2 = getLanguageFromDict("en");
            }
            applyLanguage(localeInfo2, true, false, i);
        }
        this.unofficialLanguages.remove(localeInfo);
        this.remoteLanguages.remove(localeInfo);
        this.remoteLanguagesDict.remove(localeInfo.getKey());
        this.otherLanguages.remove(localeInfo);
        this.languages.remove(localeInfo);
        this.languagesDict.remove(localeInfo.getKey());
        new File(localeInfo.pathToFile).delete();
        saveOtherLanguages();
        return true;
    }

    private void loadOtherLanguages() {
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0);
        String string = sharedPreferences.getString("locales", null);
        if (!TextUtils.isEmpty(string)) {
            for (String str : string.split(ContainerUtils.FIELD_DELIMITER)) {
                LocaleInfo createWithString = LocaleInfo.createWithString(str);
                if (createWithString != null) {
                    this.otherLanguages.add(createWithString);
                }
            }
        }
        String string2 = sharedPreferences.getString("remote", null);
        if (!TextUtils.isEmpty(string2)) {
            for (String str2 : string2.split(ContainerUtils.FIELD_DELIMITER)) {
                LocaleInfo createWithString2 = LocaleInfo.createWithString(str2);
                createWithString2.shortName = createWithString2.shortName.replace("-", "_");
                if (!this.remoteLanguagesDict.containsKey(createWithString2.getKey())) {
                    this.remoteLanguages.add(createWithString2);
                    this.remoteLanguagesDict.put(createWithString2.getKey(), createWithString2);
                }
            }
        }
        String string3 = sharedPreferences.getString("unofficial", null);
        if (!TextUtils.isEmpty(string3)) {
            for (String str3 : string3.split(ContainerUtils.FIELD_DELIMITER)) {
                LocaleInfo createWithString3 = LocaleInfo.createWithString(str3);
                if (createWithString3 != null) {
                    createWithString3.shortName = createWithString3.shortName.replace("-", "_");
                    this.unofficialLanguages.add(createWithString3);
                }
            }
        }
    }

    private HashMap<String, String> getLocaleFileStrings(File file) {
        return getLocaleFileStrings(file, false);
    }

    private HashMap<String, String> getLocaleFileStrings(File file, boolean z) {
        Exception e;
        this.reloadLastFile = false;
        FileInputStream fileInputStream = null;
        try {
            try {
                if (!file.exists()) {
                    return new HashMap<>();
                }
                HashMap<String, String> hashMap = new HashMap<>();
                XmlPullParser newPullParser = Xml.newPullParser();
                FileInputStream fileInputStream2 = new FileInputStream(file);
                try {
                    newPullParser.setInput(fileInputStream2, "UTF-8");
                    String str = null;
                    String str2 = null;
                    String str3 = null;
                    for (int eventType = newPullParser.getEventType(); eventType != 1; eventType = newPullParser.next()) {
                        if (eventType == 2) {
                            str2 = newPullParser.getName();
                            if (newPullParser.getAttributeCount() > 0) {
                                str = newPullParser.getAttributeValue(0);
                            }
                        } else if (eventType == 4) {
                            if (str != null && (str3 = newPullParser.getText()) != null) {
                                String trim = str3.trim();
                                if (z) {
                                    str3 = trim.replace("<", "&lt;").replace(">", "&gt;").replace("'", "\\'").replace("& ", "&amp; ");
                                } else {
                                    String replace = trim.replace("\\n", "\n").replace("\\", "");
                                    str3 = replace.replace("&lt;", "<");
                                    if (!this.reloadLastFile && !str3.equals(replace)) {
                                        this.reloadLastFile = true;
                                    }
                                }
                            }
                        } else if (eventType == 3) {
                            str = null;
                            str2 = null;
                            str3 = null;
                        }
                        if (str2 != null && str2.equals("string") && str3 != null && str != null && str3.length() != 0 && str.length() != 0) {
                            hashMap.put(str, str3);
                            str = null;
                            str2 = null;
                            str3 = null;
                        }
                    }
                    try {
                        fileInputStream2.close();
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                    return hashMap;
                } catch (Exception e3) {
                    e = e3;
                    fileInputStream = fileInputStream2;
                    FileLog.e(e);
                    this.reloadLastFile = true;
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (Exception e4) {
                            FileLog.e(e4);
                        }
                    }
                    return new HashMap<>();
                } catch (Throwable th) {
                    th = th;
                    fileInputStream = fileInputStream2;
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (Exception e5) {
                            FileLog.e(e5);
                        }
                    }
                    throw th;
                }
            } catch (Exception e6) {
                e = e6;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public void applyLanguage(LocaleInfo localeInfo, boolean z, boolean z2, int i) {
        applyLanguage(localeInfo, z, z2, false, false, i);
    }

    public void applyLanguage(final LocaleInfo localeInfo, boolean z, boolean z2, boolean z3, final boolean z4, final int i) {
        boolean z5;
        String[] strArr;
        Locale locale;
        if (localeInfo == null) {
            return;
        }
        boolean hasBaseLang = localeInfo.hasBaseLang();
        File pathToFile = localeInfo.getPathToFile();
        File pathToBaseFile = localeInfo.getPathToBaseFile();
        if (!z2) {
            ConnectionsManager.setLangCode(localeInfo.getLangCode());
        }
        if (getLanguageFromDict(localeInfo.getKey()) == null) {
            if (localeInfo.isRemote()) {
                this.remoteLanguages.add(localeInfo);
                this.remoteLanguagesDict.put(localeInfo.getKey(), localeInfo);
                this.languages.add(localeInfo);
                this.languagesDict.put(localeInfo.getKey(), localeInfo);
                saveOtherLanguages();
            } else if (localeInfo.isUnofficial()) {
                this.unofficialLanguages.add(localeInfo);
                this.languagesDict.put(localeInfo.getKey(), localeInfo);
                saveOtherLanguages();
            }
        }
        if ((localeInfo.isRemote() || localeInfo.isUnofficial()) && (z4 || !pathToFile.exists() || (hasBaseLang && !pathToBaseFile.exists()))) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("reload locale because one of file doesn't exist" + pathToFile + " " + pathToBaseFile);
            }
            if (z2) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        LocaleController.this.lambda$applyLanguage$2(localeInfo, i);
                    }
                });
            } else {
                applyRemoteLanguage(localeInfo, null, true, i);
            }
            z5 = true;
        } else {
            z5 = false;
        }
        try {
            if (!TextUtils.isEmpty(localeInfo.pluralLangCode)) {
                strArr = localeInfo.pluralLangCode.split("_");
            } else if (!TextUtils.isEmpty(localeInfo.baseLangCode)) {
                strArr = localeInfo.baseLangCode.split("_");
            } else {
                strArr = localeInfo.shortName.split("_");
            }
            if (strArr.length == 1) {
                locale = new Locale(strArr[0]);
            } else {
                locale = new Locale(strArr[0], strArr[1]);
            }
            if (z) {
                this.languageOverride = localeInfo.shortName;
                SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
                edit.putString("language", localeInfo.getKey());
                edit.commit();
            }
            if (pathToFile == null) {
                this.localeValues.clear();
            } else if (!z3) {
                HashMap<String, String> localeFileStrings = getLocaleFileStrings(hasBaseLang ? localeInfo.getPathToBaseFile() : localeInfo.getPathToFile());
                this.localeValues = localeFileStrings;
                if (hasBaseLang) {
                    localeFileStrings.putAll(getLocaleFileStrings(localeInfo.getPathToFile()));
                }
            }
            this.currentLocale = locale;
            this.currentLocaleInfo = localeInfo;
            if (!TextUtils.isEmpty(localeInfo.pluralLangCode)) {
                this.currentPluralRules = this.allRules.get(this.currentLocaleInfo.pluralLangCode);
            }
            if (this.currentPluralRules == null) {
                PluralRules pluralRules = this.allRules.get(strArr[0]);
                this.currentPluralRules = pluralRules;
                if (pluralRules == null) {
                    PluralRules pluralRules2 = this.allRules.get(this.currentLocale.getLanguage());
                    this.currentPluralRules = pluralRules2;
                    if (pluralRules2 == null) {
                        this.currentPluralRules = new PluralRules_None();
                    }
                }
            }
            this.changingConfiguration = true;
            Locale.setDefault(this.currentLocale);
            Configuration configuration = new Configuration();
            configuration.locale = this.currentLocale;
            ApplicationLoader.applicationContext.getResources().updateConfiguration(configuration, ApplicationLoader.applicationContext.getResources().getDisplayMetrics());
            this.changingConfiguration = false;
            if (this.reloadLastFile) {
                if (z2) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            LocaleController.this.lambda$applyLanguage$3(i, z4);
                        }
                    });
                } else {
                    reloadCurrentRemoteLocale(i, null, z4);
                }
                this.reloadLastFile = false;
            }
            if (!z5) {
                if (z2) {
                    AndroidUtilities.runOnUIThread(LocaleController$$ExternalSyntheticLambda10.INSTANCE);
                } else {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.reloadInterface, new Object[0]);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
            this.changingConfiguration = false;
        }
        recreateFormatters();
        if (!z4) {
            return;
        }
        MediaDataController.getInstance(i).loadAttachMenuBots(false, true);
    }

    public /* synthetic */ void lambda$applyLanguage$2(LocaleInfo localeInfo, int i) {
        applyRemoteLanguage(localeInfo, null, true, i);
    }

    public /* synthetic */ void lambda$applyLanguage$3(int i, boolean z) {
        reloadCurrentRemoteLocale(i, null, z);
    }

    public static /* synthetic */ void lambda$applyLanguage$4() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.reloadInterface, new Object[0]);
    }

    public LocaleInfo getCurrentLocaleInfo() {
        return this.currentLocaleInfo;
    }

    public Locale getCurrentLocale() {
        return this.currentLocale;
    }

    public static String getCurrentLanguageName() {
        LocaleInfo localeInfo = getInstance().currentLocaleInfo;
        return (localeInfo == null || TextUtils.isEmpty(localeInfo.name)) ? getString("LanguageName", org.telegram.messenger.beta.R.string.LanguageName) : localeInfo.name;
    }

    private String getStringInternal(String str, int i) {
        return getStringInternal(str, null, i);
    }

    private String getStringInternal(String str, String str2, int i) {
        String str3 = BuildVars.USE_CLOUD_STRINGS ? this.localeValues.get(str) : null;
        if (str3 == null) {
            if (BuildVars.USE_CLOUD_STRINGS && str2 != null) {
                str3 = this.localeValues.get(str2);
            }
            if (str3 == null) {
                try {
                    str3 = ApplicationLoader.applicationContext.getString(i);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
        if (str3 == null) {
            return "LOC_ERR:" + str;
        }
        return str3;
    }

    public static String getServerString(String str) {
        int identifier;
        String str2 = getInstance().localeValues.get(str);
        return (str2 != null || (identifier = ApplicationLoader.applicationContext.getResources().getIdentifier(str, "string", ApplicationLoader.applicationContext.getPackageName())) == 0) ? str2 : ApplicationLoader.applicationContext.getString(identifier);
    }

    public static String getString(int i) {
        String str = resourcesCacheMap.get(Integer.valueOf(i));
        if (str == null) {
            HashMap<Integer, String> hashMap = resourcesCacheMap;
            Integer valueOf = Integer.valueOf(i);
            String resourceEntryName = ApplicationLoader.applicationContext.getResources().getResourceEntryName(i);
            hashMap.put(valueOf, resourceEntryName);
            str = resourceEntryName;
        }
        return getString(str, i);
    }

    public static String getString(String str, int i) {
        return getInstance().getStringInternal(str, i);
    }

    public static String getString(String str, String str2, int i) {
        return getInstance().getStringInternal(str, str2, i);
    }

    public static String getString(String str) {
        if (TextUtils.isEmpty(str)) {
            return "LOC_ERR:" + str;
        }
        int identifier = ApplicationLoader.applicationContext.getResources().getIdentifier(str, "string", ApplicationLoader.applicationContext.getPackageName());
        if (identifier != 0) {
            return getString(str, identifier);
        }
        return getServerString(str);
    }

    public static String getPluralString(String str, int i) {
        if (str == null || str.length() == 0 || getInstance().currentPluralRules == null) {
            return "LOC_ERR:" + str;
        }
        String str2 = str + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(i));
        return getString(str2, str + "_other", ApplicationLoader.applicationContext.getResources().getIdentifier(str2, "string", ApplicationLoader.applicationContext.getPackageName()));
    }

    public static String formatPluralString(String str, int i, Object... objArr) {
        if (str == null || str.length() == 0 || getInstance().currentPluralRules == null) {
            return "LOC_ERR:" + str;
        }
        String str2 = str + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(i));
        int identifier = ApplicationLoader.applicationContext.getResources().getIdentifier(str2, "string", ApplicationLoader.applicationContext.getPackageName());
        Object[] objArr2 = new Object[objArr.length + 1];
        objArr2[0] = Integer.valueOf(i);
        System.arraycopy(objArr, 0, objArr2, 1, objArr.length);
        return formatString(str2, str + "_other", identifier, objArr2);
    }

    public static String formatPluralStringComma(String str, int i) {
        if (str != null) {
            try {
                if (str.length() != 0 && getInstance().currentPluralRules != null) {
                    String str2 = str + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(i));
                    StringBuilder sb = new StringBuilder(String.format(Locale.US, "%d", Integer.valueOf(i)));
                    for (int length = sb.length() - 3; length > 0; length -= 3) {
                        sb.insert(length, ',');
                    }
                    String str3 = null;
                    String str4 = BuildVars.USE_CLOUD_STRINGS ? getInstance().localeValues.get(str2) : null;
                    if (str4 == null) {
                        if (BuildVars.USE_CLOUD_STRINGS) {
                            str3 = getInstance().localeValues.get(str + "_other");
                        }
                        str4 = str3;
                    }
                    if (str4 == null) {
                        str4 = ApplicationLoader.applicationContext.getString(ApplicationLoader.applicationContext.getResources().getIdentifier(str2, "string", ApplicationLoader.applicationContext.getPackageName()));
                    }
                    String replace = str4.replace("%1$d", "%1$s");
                    return getInstance().currentLocale != null ? String.format(getInstance().currentLocale, replace, sb) : String.format(replace, sb);
                }
            } catch (Exception e) {
                FileLog.e(e);
                return "LOC_ERR: " + str;
            }
        }
        return "LOC_ERR:" + str;
    }

    public static String formatString(int i, Object... objArr) {
        String str = resourcesCacheMap.get(Integer.valueOf(i));
        if (str == null) {
            HashMap<Integer, String> hashMap = resourcesCacheMap;
            Integer valueOf = Integer.valueOf(i);
            String resourceEntryName = ApplicationLoader.applicationContext.getResources().getResourceEntryName(i);
            hashMap.put(valueOf, resourceEntryName);
            str = resourceEntryName;
        }
        return formatString(str, i, objArr);
    }

    public static String formatString(String str, int i, Object... objArr) {
        return formatString(str, null, i, objArr);
    }

    public static String formatString(String str, String str2, int i, Object... objArr) {
        try {
            String str3 = BuildVars.USE_CLOUD_STRINGS ? getInstance().localeValues.get(str) : null;
            if (str3 == null) {
                if (BuildVars.USE_CLOUD_STRINGS && str2 != null) {
                    str3 = getInstance().localeValues.get(str2);
                }
                if (str3 == null) {
                    str3 = ApplicationLoader.applicationContext.getString(i);
                }
            }
            if (getInstance().currentLocale != null) {
                return String.format(getInstance().currentLocale, str3, objArr);
            }
            return String.format(str3, objArr);
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR: " + str;
        }
    }

    public static String formatTTLString(int i) {
        if (i < 60) {
            return formatPluralString("Seconds", i, new Object[0]);
        }
        if (i < 3600) {
            return formatPluralString("Minutes", i / 60, new Object[0]);
        }
        if (i < 86400) {
            return formatPluralString("Hours", (i / 60) / 60, new Object[0]);
        }
        if (i < 604800) {
            return formatPluralString("Days", ((i / 60) / 60) / 24, new Object[0]);
        }
        if (i < 2678400) {
            int i2 = ((i / 60) / 60) / 24;
            return i % 7 == 0 ? formatPluralString("Weeks", i2 / 7, new Object[0]) : String.format("%s %s", formatPluralString("Weeks", i2 / 7, new Object[0]), formatPluralString("Days", i2 % 7, new Object[0]));
        }
        return formatPluralString("Months", (((i / 60) / 60) / 24) / 30, new Object[0]);
    }

    public static String fixNumbers(CharSequence charSequence) {
        StringBuilder sb = new StringBuilder(charSequence);
        int length = sb.length();
        for (int i = 0; i < length; i++) {
            char charAt = sb.charAt(i);
            if ((charAt < '0' || charAt > '9') && charAt != '.' && charAt != ',') {
                int i2 = 0;
                while (i2 < otherNumbers.length) {
                    int i3 = 0;
                    while (true) {
                        char[][] cArr = otherNumbers;
                        if (i3 >= cArr[i2].length) {
                            break;
                        } else if (charAt == cArr[i2][i3]) {
                            sb.setCharAt(i, defaultNumbers[i3]);
                            i2 = otherNumbers.length;
                            break;
                        } else {
                            i3++;
                        }
                    }
                    i2++;
                }
            }
        }
        return sb.toString();
    }

    public String formatCurrencyString(long j, String str) {
        return formatCurrencyString(j, true, true, false, str);
    }

    public String formatCurrencyString(long j, boolean z, boolean z2, boolean z3, String str) {
        double d;
        int length;
        String upperCase = str.toUpperCase();
        boolean z4 = j < 0;
        long abs = Math.abs(j);
        Currency currency = Currency.getInstance(upperCase);
        upperCase.hashCode();
        char c = 65535;
        switch (upperCase.hashCode()) {
            case 65726:
                if (upperCase.equals("BHD")) {
                    c = 0;
                    break;
                }
                break;
            case 65759:
                if (upperCase.equals("BIF")) {
                    c = 1;
                    break;
                }
                break;
            case 66267:
                if (upperCase.equals("BYR")) {
                    c = 2;
                    break;
                }
                break;
            case 66813:
                if (upperCase.equals("CLF")) {
                    c = 3;
                    break;
                }
                break;
            case 66823:
                if (upperCase.equals("CLP")) {
                    c = 4;
                    break;
                }
                break;
            case 67122:
                if (upperCase.equals("CVE")) {
                    c = 5;
                    break;
                }
                break;
            case 67712:
                if (upperCase.equals("DJF")) {
                    c = 6;
                    break;
                }
                break;
            case 70719:
                if (upperCase.equals("GNF")) {
                    c = 7;
                    break;
                }
                break;
            case 72732:
                if (upperCase.equals("IQD")) {
                    c = '\b';
                    break;
                }
                break;
            case 72777:
                if (upperCase.equals("IRR")) {
                    c = '\t';
                    break;
                }
                break;
            case 72801:
                if (upperCase.equals("ISK")) {
                    c = '\n';
                    break;
                }
                break;
            case 73631:
                if (upperCase.equals("JOD")) {
                    c = 11;
                    break;
                }
                break;
            case 73683:
                if (upperCase.equals("JPY")) {
                    c = '\f';
                    break;
                }
                break;
            case 74532:
                if (upperCase.equals("KMF")) {
                    c = '\r';
                    break;
                }
                break;
            case 74704:
                if (upperCase.equals("KRW")) {
                    c = 14;
                    break;
                }
                break;
            case 74840:
                if (upperCase.equals("KWD")) {
                    c = 15;
                    break;
                }
                break;
            case 75863:
                if (upperCase.equals("LYD")) {
                    c = 16;
                    break;
                }
                break;
            case 76263:
                if (upperCase.equals("MGA")) {
                    c = 17;
                    break;
                }
                break;
            case 76618:
                if (upperCase.equals("MRO")) {
                    c = 18;
                    break;
                }
                break;
            case 78388:
                if (upperCase.equals("OMR")) {
                    c = 19;
                    break;
                }
                break;
            case 79710:
                if (upperCase.equals("PYG")) {
                    c = 20;
                    break;
                }
                break;
            case 81569:
                if (upperCase.equals("RWF")) {
                    c = 21;
                    break;
                }
                break;
            case 83210:
                if (upperCase.equals("TND")) {
                    c = 22;
                    break;
                }
                break;
            case 83974:
                if (upperCase.equals("UGX")) {
                    c = 23;
                    break;
                }
                break;
            case 84517:
                if (upperCase.equals("UYI")) {
                    c = 24;
                    break;
                }
                break;
            case 85132:
                if (upperCase.equals("VND")) {
                    c = 25;
                    break;
                }
                break;
            case 85367:
                if (upperCase.equals("VUV")) {
                    c = 26;
                    break;
                }
                break;
            case 86653:
                if (upperCase.equals("XAF")) {
                    c = 27;
                    break;
                }
                break;
            case 87087:
                if (upperCase.equals("XOF")) {
                    c = 28;
                    break;
                }
                break;
            case 87118:
                if (upperCase.equals("XPF")) {
                    c = 29;
                    break;
                }
                break;
        }
        String str2 = " %.2f";
        String str3 = " %.0f";
        switch (c) {
            case 0:
            case '\b':
            case 11:
            case 15:
            case 16:
            case 19:
            case R.styleable.MapAttrs_useViewLifecycle /* 22 */:
                double d2 = abs;
                Double.isNaN(d2);
                d = d2 / 1000.0d;
                str2 = " %.3f";
                break;
            case 1:
            case 2:
            case 4:
            case 5:
            case 6:
            case 7:
            case '\n':
            case '\f':
            case '\r':
            case 14:
            case 17:
            case R.styleable.MapAttrs_uiZoomControls /* 20 */:
            case 21:
            case R.styleable.MapAttrs_zOrderOnTop /* 23 */:
            case 24:
            case AvailableCode.ERROR_ON_ACTIVITY_RESULT /* 25 */:
            case AvailableCode.ERROR_NO_ACTIVITY /* 26 */:
            case AvailableCode.USER_IGNORE_PREVIOUS_POPUP /* 27 */:
            case AvailableCode.APP_IS_BACKGROUND_OR_LOCKED /* 28 */:
            case AvailableCode.HMS_IS_SPOOF /* 29 */:
                d = abs;
                str2 = str3;
                break;
            case 3:
                double d3 = abs;
                Double.isNaN(d3);
                d = d3 / 10000.0d;
                str2 = " %.4f";
                break;
            case '\t':
                double d4 = ((float) abs) / 100.0f;
                if (z && abs % 100 == 0) {
                    str2 = str3;
                }
                d = d4;
                break;
            case 18:
                double d5 = abs;
                Double.isNaN(d5);
                d = d5 / 10.0d;
                str2 = " %.1f";
                break;
            default:
                double d6 = abs;
                Double.isNaN(d6);
                d = d6 / 100.0d;
                break;
        }
        if (z2) {
            str3 = str2;
        }
        String str4 = "-";
        if (currency != null) {
            Locale locale = this.currentLocale;
            if (locale == null) {
                locale = this.systemDefaultLocale;
            }
            NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(locale);
            currencyInstance.setCurrency(currency);
            if (z3) {
                currencyInstance.setGroupingUsed(false);
            }
            if (!z2 || (z && upperCase.equals("IRR"))) {
                currencyInstance.setMaximumFractionDigits(0);
            }
            StringBuilder sb = new StringBuilder();
            if (!z4) {
                str4 = "";
            }
            sb.append(str4);
            sb.append(currencyInstance.format(d));
            String sb2 = sb.toString();
            int indexOf = sb2.indexOf(upperCase);
            if (indexOf < 0 || (length = indexOf + upperCase.length()) >= sb2.length() || sb2.charAt(length) == ' ') {
                return sb2;
            }
            return sb2.substring(0, length) + " " + sb2.substring(length);
        }
        StringBuilder sb3 = new StringBuilder();
        if (!z4) {
            str4 = "";
        }
        sb3.append(str4);
        sb3.append(String.format(Locale.US, upperCase + str3, Double.valueOf(d)));
        return sb3.toString();
    }

    public static int getCurrencyExpDivider(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case 65726:
                if (str.equals("BHD")) {
                    c = 0;
                    break;
                }
                break;
            case 65759:
                if (str.equals("BIF")) {
                    c = 1;
                    break;
                }
                break;
            case 66267:
                if (str.equals("BYR")) {
                    c = 2;
                    break;
                }
                break;
            case 66813:
                if (str.equals("CLF")) {
                    c = 3;
                    break;
                }
                break;
            case 66823:
                if (str.equals("CLP")) {
                    c = 4;
                    break;
                }
                break;
            case 67122:
                if (str.equals("CVE")) {
                    c = 5;
                    break;
                }
                break;
            case 67712:
                if (str.equals("DJF")) {
                    c = 6;
                    break;
                }
                break;
            case 70719:
                if (str.equals("GNF")) {
                    c = 7;
                    break;
                }
                break;
            case 72732:
                if (str.equals("IQD")) {
                    c = '\b';
                    break;
                }
                break;
            case 72801:
                if (str.equals("ISK")) {
                    c = '\t';
                    break;
                }
                break;
            case 73631:
                if (str.equals("JOD")) {
                    c = '\n';
                    break;
                }
                break;
            case 73683:
                if (str.equals("JPY")) {
                    c = 11;
                    break;
                }
                break;
            case 74532:
                if (str.equals("KMF")) {
                    c = '\f';
                    break;
                }
                break;
            case 74704:
                if (str.equals("KRW")) {
                    c = '\r';
                    break;
                }
                break;
            case 74840:
                if (str.equals("KWD")) {
                    c = 14;
                    break;
                }
                break;
            case 75863:
                if (str.equals("LYD")) {
                    c = 15;
                    break;
                }
                break;
            case 76263:
                if (str.equals("MGA")) {
                    c = 16;
                    break;
                }
                break;
            case 76618:
                if (str.equals("MRO")) {
                    c = 17;
                    break;
                }
                break;
            case 78388:
                if (str.equals("OMR")) {
                    c = 18;
                    break;
                }
                break;
            case 79710:
                if (str.equals("PYG")) {
                    c = 19;
                    break;
                }
                break;
            case 81569:
                if (str.equals("RWF")) {
                    c = 20;
                    break;
                }
                break;
            case 83210:
                if (str.equals("TND")) {
                    c = 21;
                    break;
                }
                break;
            case 83974:
                if (str.equals("UGX")) {
                    c = 22;
                    break;
                }
                break;
            case 84517:
                if (str.equals("UYI")) {
                    c = 23;
                    break;
                }
                break;
            case 85132:
                if (str.equals("VND")) {
                    c = 24;
                    break;
                }
                break;
            case 85367:
                if (str.equals("VUV")) {
                    c = 25;
                    break;
                }
                break;
            case 86653:
                if (str.equals("XAF")) {
                    c = 26;
                    break;
                }
                break;
            case 87087:
                if (str.equals("XOF")) {
                    c = 27;
                    break;
                }
                break;
            case 87118:
                if (str.equals("XPF")) {
                    c = 28;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case '\b':
            case '\n':
            case 14:
            case 15:
            case 18:
            case 21:
                return 1000;
            case 1:
            case 2:
            case 4:
            case 5:
            case 6:
            case 7:
            case '\t':
            case 11:
            case '\f':
            case '\r':
            case 16:
            case 19:
            case R.styleable.MapAttrs_uiZoomControls /* 20 */:
            case R.styleable.MapAttrs_useViewLifecycle /* 22 */:
            case R.styleable.MapAttrs_zOrderOnTop /* 23 */:
            case 24:
            case AvailableCode.ERROR_ON_ACTIVITY_RESULT /* 25 */:
            case AvailableCode.ERROR_NO_ACTIVITY /* 26 */:
            case AvailableCode.USER_IGNORE_PREVIOUS_POPUP /* 27 */:
            case AvailableCode.APP_IS_BACKGROUND_OR_LOCKED /* 28 */:
                return 1;
            case 3:
                return HwBuildEx.VersionCodes.CUR_DEVELOPMENT;
            case 17:
                return 10;
            default:
                return 100;
        }
    }

    public String formatCurrencyDecimalString(long j, String str, boolean z) {
        double d;
        String upperCase = str.toUpperCase();
        long abs = Math.abs(j);
        upperCase.hashCode();
        char c = 65535;
        switch (upperCase.hashCode()) {
            case 65726:
                if (upperCase.equals("BHD")) {
                    c = 0;
                    break;
                }
                break;
            case 65759:
                if (upperCase.equals("BIF")) {
                    c = 1;
                    break;
                }
                break;
            case 66267:
                if (upperCase.equals("BYR")) {
                    c = 2;
                    break;
                }
                break;
            case 66813:
                if (upperCase.equals("CLF")) {
                    c = 3;
                    break;
                }
                break;
            case 66823:
                if (upperCase.equals("CLP")) {
                    c = 4;
                    break;
                }
                break;
            case 67122:
                if (upperCase.equals("CVE")) {
                    c = 5;
                    break;
                }
                break;
            case 67712:
                if (upperCase.equals("DJF")) {
                    c = 6;
                    break;
                }
                break;
            case 70719:
                if (upperCase.equals("GNF")) {
                    c = 7;
                    break;
                }
                break;
            case 72732:
                if (upperCase.equals("IQD")) {
                    c = '\b';
                    break;
                }
                break;
            case 72777:
                if (upperCase.equals("IRR")) {
                    c = '\t';
                    break;
                }
                break;
            case 72801:
                if (upperCase.equals("ISK")) {
                    c = '\n';
                    break;
                }
                break;
            case 73631:
                if (upperCase.equals("JOD")) {
                    c = 11;
                    break;
                }
                break;
            case 73683:
                if (upperCase.equals("JPY")) {
                    c = '\f';
                    break;
                }
                break;
            case 74532:
                if (upperCase.equals("KMF")) {
                    c = '\r';
                    break;
                }
                break;
            case 74704:
                if (upperCase.equals("KRW")) {
                    c = 14;
                    break;
                }
                break;
            case 74840:
                if (upperCase.equals("KWD")) {
                    c = 15;
                    break;
                }
                break;
            case 75863:
                if (upperCase.equals("LYD")) {
                    c = 16;
                    break;
                }
                break;
            case 76263:
                if (upperCase.equals("MGA")) {
                    c = 17;
                    break;
                }
                break;
            case 76618:
                if (upperCase.equals("MRO")) {
                    c = 18;
                    break;
                }
                break;
            case 78388:
                if (upperCase.equals("OMR")) {
                    c = 19;
                    break;
                }
                break;
            case 79710:
                if (upperCase.equals("PYG")) {
                    c = 20;
                    break;
                }
                break;
            case 81569:
                if (upperCase.equals("RWF")) {
                    c = 21;
                    break;
                }
                break;
            case 83210:
                if (upperCase.equals("TND")) {
                    c = 22;
                    break;
                }
                break;
            case 83974:
                if (upperCase.equals("UGX")) {
                    c = 23;
                    break;
                }
                break;
            case 84517:
                if (upperCase.equals("UYI")) {
                    c = 24;
                    break;
                }
                break;
            case 85132:
                if (upperCase.equals("VND")) {
                    c = 25;
                    break;
                }
                break;
            case 85367:
                if (upperCase.equals("VUV")) {
                    c = 26;
                    break;
                }
                break;
            case 86653:
                if (upperCase.equals("XAF")) {
                    c = 27;
                    break;
                }
                break;
            case 87087:
                if (upperCase.equals("XOF")) {
                    c = 28;
                    break;
                }
                break;
            case 87118:
                if (upperCase.equals("XPF")) {
                    c = 29;
                    break;
                }
                break;
        }
        String str2 = " %.2f";
        switch (c) {
            case 0:
            case '\b':
            case 11:
            case 15:
            case 16:
            case 19:
            case R.styleable.MapAttrs_useViewLifecycle /* 22 */:
                double d2 = abs;
                Double.isNaN(d2);
                d = d2 / 1000.0d;
                str2 = " %.3f";
                break;
            case 1:
            case 2:
            case 4:
            case 5:
            case 6:
            case 7:
            case '\n':
            case '\f':
            case '\r':
            case 14:
            case 17:
            case R.styleable.MapAttrs_uiZoomControls /* 20 */:
            case 21:
            case R.styleable.MapAttrs_zOrderOnTop /* 23 */:
            case 24:
            case AvailableCode.ERROR_ON_ACTIVITY_RESULT /* 25 */:
            case AvailableCode.ERROR_NO_ACTIVITY /* 26 */:
            case AvailableCode.USER_IGNORE_PREVIOUS_POPUP /* 27 */:
            case AvailableCode.APP_IS_BACKGROUND_OR_LOCKED /* 28 */:
            case AvailableCode.HMS_IS_SPOOF /* 29 */:
                d = abs;
                str2 = " %.0f";
                break;
            case 3:
                double d3 = abs;
                Double.isNaN(d3);
                d = d3 / 10000.0d;
                str2 = " %.4f";
                break;
            case '\t':
                double d4 = ((float) abs) / 100.0f;
                if (abs % 100 == 0) {
                    str2 = " %.0f";
                }
                d = d4;
                break;
            case 18:
                double d5 = abs;
                Double.isNaN(d5);
                d = d5 / 10.0d;
                str2 = " %.1f";
                break;
            default:
                double d6 = abs;
                Double.isNaN(d6);
                d = d6 / 100.0d;
                break;
        }
        Locale locale = Locale.US;
        if (!z) {
            upperCase = "" + str2;
        }
        return String.format(locale, upperCase, Double.valueOf(d)).trim();
    }

    public static String formatStringSimple(String str, Object... objArr) {
        try {
            if (getInstance().currentLocale != null) {
                return String.format(getInstance().currentLocale, str, objArr);
            }
            return String.format(str, objArr);
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR: " + str;
        }
    }

    public static String formatDuration(int i) {
        if (i <= 0) {
            return formatPluralString("Seconds", 0, new Object[0]);
        }
        int i2 = i / 3600;
        int i3 = (i / 60) % 60;
        int i4 = i % 60;
        StringBuilder sb = new StringBuilder();
        if (i2 > 0) {
            sb.append(formatPluralString("Hours", i2, new Object[0]));
        }
        if (i3 > 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(formatPluralString("Minutes", i3, new Object[0]));
        }
        if (i4 > 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(formatPluralString("Seconds", i4, new Object[0]));
        }
        return sb.toString();
    }

    public static String formatCallDuration(int i) {
        if (i <= 3600) {
            if (i > 60) {
                return formatPluralString("Minutes", i / 60, new Object[0]);
            }
            return formatPluralString("Seconds", i, new Object[0]);
        }
        String formatPluralString = formatPluralString("Hours", i / 3600, new Object[0]);
        int i2 = (i % 3600) / 60;
        if (i2 <= 0) {
            return formatPluralString;
        }
        return formatPluralString + ", " + formatPluralString("Minutes", i2, new Object[0]);
    }

    public void onDeviceConfigurationChange(Configuration configuration) {
        if (this.changingConfiguration) {
            return;
        }
        is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
        Locale locale = configuration.locale;
        this.systemDefaultLocale = locale;
        if (this.languageOverride != null) {
            LocaleInfo localeInfo = this.currentLocaleInfo;
            this.currentLocaleInfo = null;
            applyLanguage(localeInfo, false, false, UserConfig.selectedAccount);
        } else if (locale != null) {
            String displayName = locale.getDisplayName();
            String displayName2 = this.currentLocale.getDisplayName();
            if (displayName != null && displayName2 != null && !displayName.equals(displayName2)) {
                recreateFormatters();
            }
            this.currentLocale = locale;
            LocaleInfo localeInfo2 = this.currentLocaleInfo;
            if (localeInfo2 != null && !TextUtils.isEmpty(localeInfo2.pluralLangCode)) {
                this.currentPluralRules = this.allRules.get(this.currentLocaleInfo.pluralLangCode);
            }
            if (this.currentPluralRules == null) {
                PluralRules pluralRules = this.allRules.get(this.currentLocale.getLanguage());
                this.currentPluralRules = pluralRules;
                if (pluralRules == null) {
                    this.currentPluralRules = this.allRules.get("en");
                }
            }
        }
        String systemLocaleStringIso639 = getSystemLocaleStringIso639();
        String str = this.currentSystemLocale;
        if (str == null || systemLocaleStringIso639.equals(str)) {
            return;
        }
        this.currentSystemLocale = systemLocaleStringIso639;
        ConnectionsManager.setSystemLangCode(systemLocaleStringIso639);
    }

    public static String formatDateChat(long j) {
        return formatDateChat(j, false);
    }

    public static String formatDateChat(long j, boolean z) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int i = calendar.get(1);
            long j2 = j * 1000;
            calendar.setTimeInMillis(j2);
            if ((z && i == calendar.get(1)) || (!z && Math.abs(System.currentTimeMillis() - j2) < 31536000000L)) {
                return getInstance().chatDate.format(j2);
            }
            return getInstance().chatFullDate.format(j2);
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR: formatDateChat";
        }
    }

    public static String formatDate(long j) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(6);
            int i2 = calendar.get(1);
            calendar.setTimeInMillis(j2);
            int i3 = calendar.get(6);
            int i4 = calendar.get(1);
            if (i3 == i && i2 == i4) {
                return getInstance().formatterDay.format(new Date(j2));
            }
            if (i3 + 1 == i && i2 == i4) {
                return getString("Yesterday", org.telegram.messenger.beta.R.string.Yesterday);
            }
            if (Math.abs(System.currentTimeMillis() - j2) < 31536000000L) {
                return getInstance().formatterDayMonth.format(new Date(j2));
            }
            return getInstance().formatterYear.format(new Date(j2));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR: formatDate";
        }
    }

    public static String formatDateAudio(long j, boolean z) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(6);
            int i2 = calendar.get(1);
            calendar.setTimeInMillis(j2);
            int i3 = calendar.get(6);
            int i4 = calendar.get(1);
            return (i3 == i && i2 == i4) ? z ? formatString("TodayAtFormatted", org.telegram.messenger.beta.R.string.TodayAtFormatted, getInstance().formatterDay.format(new Date(j2))) : formatString("TodayAtFormattedWithToday", org.telegram.messenger.beta.R.string.TodayAtFormattedWithToday, getInstance().formatterDay.format(new Date(j2))) : (i3 + 1 == i && i2 == i4) ? formatString("YesterdayAtFormatted", org.telegram.messenger.beta.R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(j2))) : Math.abs(System.currentTimeMillis() - j2) < 31536000000L ? formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().formatterDayMonth.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2))) : formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().formatterYear.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2)));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatDateCallLog(long j) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(6);
            int i2 = calendar.get(1);
            calendar.setTimeInMillis(j2);
            int i3 = calendar.get(6);
            int i4 = calendar.get(1);
            if (i3 == i && i2 == i4) {
                return getInstance().formatterDay.format(new Date(j2));
            }
            return (i3 + 1 == i && i2 == i4) ? formatString("YesterdayAtFormatted", org.telegram.messenger.beta.R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(j2))) : Math.abs(System.currentTimeMillis() - j2) < 31536000000L ? formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().chatDate.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2))) : formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().chatFullDate.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2)));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatDateTime(long j) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(6);
            int i2 = calendar.get(1);
            calendar.setTimeInMillis(j2);
            int i3 = calendar.get(6);
            int i4 = calendar.get(1);
            return (i3 == i && i2 == i4) ? formatString("TodayAtFormattedWithToday", org.telegram.messenger.beta.R.string.TodayAtFormattedWithToday, getInstance().formatterDay.format(new Date(j2))) : (i3 + 1 == i && i2 == i4) ? formatString("YesterdayAtFormatted", org.telegram.messenger.beta.R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(j2))) : Math.abs(System.currentTimeMillis() - j2) < 31536000000L ? formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().chatDate.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2))) : formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().chatFullDate.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2)));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatLocationUpdateDate(long j) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(6);
            int i2 = calendar.get(1);
            calendar.setTimeInMillis(j2);
            int i3 = calendar.get(6);
            int i4 = calendar.get(1);
            if (i3 == i && i2 == i4) {
                int currentTime = ((int) (ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime() - (j2 / 1000))) / 60;
                if (currentTime < 1) {
                    return getString("LocationUpdatedJustNow", org.telegram.messenger.beta.R.string.LocationUpdatedJustNow);
                }
                return currentTime < 60 ? formatPluralString("UpdatedMinutes", currentTime, new Object[0]) : formatString("LocationUpdatedFormatted", org.telegram.messenger.beta.R.string.LocationUpdatedFormatted, formatString("TodayAtFormatted", org.telegram.messenger.beta.R.string.TodayAtFormatted, getInstance().formatterDay.format(new Date(j2))));
            } else if (i3 + 1 == i && i2 == i4) {
                return formatString("LocationUpdatedFormatted", org.telegram.messenger.beta.R.string.LocationUpdatedFormatted, formatString("YesterdayAtFormatted", org.telegram.messenger.beta.R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(j2))));
            } else {
                if (Math.abs(System.currentTimeMillis() - j2) < 31536000000L) {
                    return formatString("LocationUpdatedFormatted", org.telegram.messenger.beta.R.string.LocationUpdatedFormatted, formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().formatterDayMonth.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2))));
                }
                return formatString("LocationUpdatedFormatted", org.telegram.messenger.beta.R.string.LocationUpdatedFormatted, formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().formatterYear.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2))));
            }
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatLocationLeftTime(int i) {
        int i2 = (i / 60) / 60;
        int i3 = i - ((i2 * 60) * 60);
        int i4 = i3 / 60;
        int i5 = i3 - (i4 * 60);
        int i6 = 1;
        if (i2 != 0) {
            Object[] objArr = new Object[1];
            if (i4 <= 30) {
                i6 = 0;
            }
            objArr[0] = Integer.valueOf(i2 + i6);
            return String.format("%dh", objArr);
        } else if (i4 == 0) {
            return String.format("%d", Integer.valueOf(i5));
        } else {
            Object[] objArr2 = new Object[1];
            if (i5 <= 30) {
                i6 = 0;
            }
            objArr2[0] = Integer.valueOf(i4 + i6);
            return String.format("%d", objArr2);
        }
    }

    public static String formatDateOnline(long j, boolean[] zArr) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(6);
            int i2 = calendar.get(1);
            int i3 = calendar.get(11);
            calendar.setTimeInMillis(j2);
            int i4 = calendar.get(6);
            int i5 = calendar.get(1);
            int i6 = calendar.get(11);
            if (i4 == i && i2 == i5) {
                return formatString("LastSeenFormatted", org.telegram.messenger.beta.R.string.LastSeenFormatted, formatString("TodayAtFormatted", org.telegram.messenger.beta.R.string.TodayAtFormatted, getInstance().formatterDay.format(new Date(j2))));
            }
            if (i4 + 1 != i || i2 != i5) {
                if (Math.abs(System.currentTimeMillis() - j2) < 31536000000L) {
                    return formatString("LastSeenDateFormatted", org.telegram.messenger.beta.R.string.LastSeenDateFormatted, formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().formatterDayMonth.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2))));
                }
                return formatString("LastSeenDateFormatted", org.telegram.messenger.beta.R.string.LastSeenDateFormatted, formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().formatterYear.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2))));
            } else if (zArr == null) {
                return formatString("LastSeenFormatted", org.telegram.messenger.beta.R.string.LastSeenFormatted, formatString("YesterdayAtFormatted", org.telegram.messenger.beta.R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(j2))));
            } else {
                zArr[0] = true;
                return (i3 > 6 || i6 <= 18 || !is24HourFormat) ? formatString("YesterdayAtFormatted", org.telegram.messenger.beta.R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(j2))) : formatString("LastSeenFormatted", org.telegram.messenger.beta.R.string.LastSeenFormatted, getInstance().formatterDay.format(new Date(j2)));
            }
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    private FastDateFormat createFormatter(Locale locale, String str, String str2) {
        if (str == null || str.length() == 0) {
            str = str2;
        }
        try {
            return FastDateFormat.getInstance(str, locale);
        } catch (Exception unused) {
            return FastDateFormat.getInstance(str2, locale);
        }
    }

    public void recreateFormatters() {
        String str;
        int i;
        String str2;
        int i2;
        String str3;
        int i3;
        String str4;
        int i4;
        LocaleInfo localeInfo;
        Locale locale = this.currentLocale;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String language = locale.getLanguage();
        if (language == null) {
            language = "en";
        }
        String lowerCase = language.toLowerCase();
        isRTL = (lowerCase.length() == 2 && (lowerCase.equals("ar") || lowerCase.equals("fa") || lowerCase.equals("he") || lowerCase.equals("iw"))) || lowerCase.startsWith("ar_") || lowerCase.startsWith("fa_") || lowerCase.startsWith("he_") || lowerCase.startsWith("iw_") || ((localeInfo = this.currentLocaleInfo) != null && localeInfo.isRtl);
        nameDisplayOrder = lowerCase.equals("ko") ? 2 : 1;
        this.formatterMonthYear = createFormatter(locale, getStringInternal("formatterMonthYear", org.telegram.messenger.beta.R.string.formatterMonthYear), "MMM yyyy");
        this.formatterDayMonth = createFormatter(locale, getStringInternal("formatterMonth", org.telegram.messenger.beta.R.string.formatterMonth), "dd MMM");
        this.formatterYear = createFormatter(locale, getStringInternal("formatterYear", org.telegram.messenger.beta.R.string.formatterYear), "dd.MM.yy");
        this.formatterYearMax = createFormatter(locale, getStringInternal("formatterYearMax", org.telegram.messenger.beta.R.string.formatterYearMax), "dd.MM.yyyy");
        this.chatDate = createFormatter(locale, getStringInternal("chatDate", org.telegram.messenger.beta.R.string.chatDate), "d MMMM");
        this.chatFullDate = createFormatter(locale, getStringInternal("chatFullDate", org.telegram.messenger.beta.R.string.chatFullDate), "d MMMM yyyy");
        this.formatterWeek = createFormatter(locale, getStringInternal("formatterWeek", org.telegram.messenger.beta.R.string.formatterWeek), "EEE");
        this.formatterWeekLong = createFormatter(locale, getStringInternal("formatterWeekLong", org.telegram.messenger.beta.R.string.formatterWeekLong), "EEEE");
        this.formatterScheduleDay = createFormatter(locale, getStringInternal("formatDateSchedule", org.telegram.messenger.beta.R.string.formatDateSchedule), "MMM d");
        this.formatterScheduleYear = createFormatter(locale, getStringInternal("formatDateScheduleYear", org.telegram.messenger.beta.R.string.formatDateScheduleYear), "MMM d yyyy");
        Locale locale2 = (lowerCase.toLowerCase().equals("ar") || lowerCase.toLowerCase().equals("ko")) ? locale : Locale.US;
        if (is24HourFormat) {
            i = org.telegram.messenger.beta.R.string.formatterDay24H;
            str = "formatterDay24H";
        } else {
            i = org.telegram.messenger.beta.R.string.formatterDay12H;
            str = "formatterDay12H";
        }
        this.formatterDay = createFormatter(locale2, getStringInternal(str, i), is24HourFormat ? "HH:mm" : "h:mm a");
        if (is24HourFormat) {
            i2 = org.telegram.messenger.beta.R.string.formatterStats24H;
            str2 = "formatterStats24H";
        } else {
            i2 = org.telegram.messenger.beta.R.string.formatterStats12H;
            str2 = "formatterStats12H";
        }
        String str5 = "MMM dd yyyy, HH:mm";
        this.formatterStats = createFormatter(locale, getStringInternal(str2, i2), is24HourFormat ? str5 : "MMM dd yyyy, h:mm a");
        if (is24HourFormat) {
            i3 = org.telegram.messenger.beta.R.string.formatterBannedUntil24H;
            str3 = "formatterBannedUntil24H";
        } else {
            i3 = org.telegram.messenger.beta.R.string.formatterBannedUntil12H;
            str3 = "formatterBannedUntil12H";
        }
        String stringInternal = getStringInternal(str3, i3);
        if (!is24HourFormat) {
            str5 = "MMM dd yyyy, h:mm a";
        }
        this.formatterBannedUntil = createFormatter(locale, stringInternal, str5);
        if (is24HourFormat) {
            i4 = org.telegram.messenger.beta.R.string.formatterBannedUntilThisYear24H;
            str4 = "formatterBannedUntilThisYear24H";
        } else {
            i4 = org.telegram.messenger.beta.R.string.formatterBannedUntilThisYear12H;
            str4 = "formatterBannedUntilThisYear12H";
        }
        this.formatterBannedUntilThisYear = createFormatter(locale, getStringInternal(str4, i4), is24HourFormat ? "MMM dd, HH:mm" : "MMM dd, h:mm a");
        this.formatterScheduleSend[0] = createFormatter(locale, getStringInternal("SendTodayAt", org.telegram.messenger.beta.R.string.SendTodayAt), "'Send today at' HH:mm");
        this.formatterScheduleSend[1] = createFormatter(locale, getStringInternal("SendDayAt", org.telegram.messenger.beta.R.string.SendDayAt), "'Send on' MMM d 'at' HH:mm");
        this.formatterScheduleSend[2] = createFormatter(locale, getStringInternal("SendDayYearAt", org.telegram.messenger.beta.R.string.SendDayYearAt), "'Send on' MMM d yyyy 'at' HH:mm");
        this.formatterScheduleSend[3] = createFormatter(locale, getStringInternal("RemindTodayAt", org.telegram.messenger.beta.R.string.RemindTodayAt), "'Remind today at' HH:mm");
        this.formatterScheduleSend[4] = createFormatter(locale, getStringInternal("RemindDayAt", org.telegram.messenger.beta.R.string.RemindDayAt), "'Remind on' MMM d 'at' HH:mm");
        this.formatterScheduleSend[5] = createFormatter(locale, getStringInternal("RemindDayYearAt", org.telegram.messenger.beta.R.string.RemindDayYearAt), "'Remind on' MMM d yyyy 'at' HH:mm");
        this.formatterScheduleSend[6] = createFormatter(locale, getStringInternal("StartTodayAt", org.telegram.messenger.beta.R.string.StartTodayAt), "'Start today at' HH:mm");
        this.formatterScheduleSend[7] = createFormatter(locale, getStringInternal("StartDayAt", org.telegram.messenger.beta.R.string.StartDayAt), "'Start on' MMM d 'at' HH:mm");
        this.formatterScheduleSend[8] = createFormatter(locale, getStringInternal("StartDayYearAt", org.telegram.messenger.beta.R.string.StartDayYearAt), "'Start on' MMM d yyyy 'at' HH:mm");
        this.formatterScheduleSend[9] = createFormatter(locale, getStringInternal("StartShortTodayAt", org.telegram.messenger.beta.R.string.StartShortTodayAt), "'Today,' HH:mm");
        this.formatterScheduleSend[10] = createFormatter(locale, getStringInternal("StartShortDayAt", org.telegram.messenger.beta.R.string.StartShortDayAt), "MMM d',' HH:mm");
        this.formatterScheduleSend[11] = createFormatter(locale, getStringInternal("StartShortDayYearAt", org.telegram.messenger.beta.R.string.StartShortDayYearAt), "MMM d yyyy, HH:mm");
        this.formatterScheduleSend[12] = createFormatter(locale, getStringInternal("StartsTodayAt", org.telegram.messenger.beta.R.string.StartsTodayAt), "'Starts today at' HH:mm");
        this.formatterScheduleSend[13] = createFormatter(locale, getStringInternal("StartsDayAt", org.telegram.messenger.beta.R.string.StartsDayAt), "'Starts on' MMM d 'at' HH:mm");
        this.formatterScheduleSend[14] = createFormatter(locale, getStringInternal("StartsDayYearAt", org.telegram.messenger.beta.R.string.StartsDayYearAt), "'Starts on' MMM d yyyy 'at' HH:mm");
    }

    public static boolean isRTLCharacter(char c) {
        return Character.getDirectionality(c) == 1 || Character.getDirectionality(c) == 2 || Character.getDirectionality(c) == 16 || Character.getDirectionality(c) == 17;
    }

    public static String formatStartsTime(long j, int i) {
        return formatStartsTime(j, i, true);
    }

    public static String formatStartsTime(long j, int i, boolean z) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int i2 = calendar.get(1);
        int i3 = calendar.get(6);
        calendar.setTimeInMillis(j * 1000);
        int i4 = i2 == calendar.get(1) ? (!z || calendar.get(6) != i3) ? 1 : 0 : 2;
        if (i == 1) {
            i4 += 3;
        } else if (i == 2) {
            i4 += 6;
        } else if (i == 3) {
            i4 += 9;
        } else if (i == 4) {
            i4 += 12;
        }
        return getInstance().formatterScheduleSend[i4].format(calendar.getTimeInMillis());
    }

    public static String formatSectionDate(long j) {
        return formatYearMont(j, false);
    }

    public static String formatYearMont(long j, boolean z) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(1);
            calendar.setTimeInMillis(j2);
            int i2 = calendar.get(1);
            int i3 = calendar.get(2);
            String[] strArr = {getString("January", org.telegram.messenger.beta.R.string.January), getString("February", org.telegram.messenger.beta.R.string.February), getString("March", org.telegram.messenger.beta.R.string.March), getString("April", org.telegram.messenger.beta.R.string.April), getString("May", org.telegram.messenger.beta.R.string.May), getString("June", org.telegram.messenger.beta.R.string.June), getString("July", org.telegram.messenger.beta.R.string.July), getString("August", org.telegram.messenger.beta.R.string.August), getString("September", org.telegram.messenger.beta.R.string.September), getString("October", org.telegram.messenger.beta.R.string.October), getString("November", org.telegram.messenger.beta.R.string.November), getString("December", org.telegram.messenger.beta.R.string.December)};
            if (i == i2 && !z) {
                return strArr[i3];
            }
            return strArr[i3] + " " + i2;
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatDateForBan(long j) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(1);
            calendar.setTimeInMillis(j2);
            if (i == calendar.get(1)) {
                return getInstance().formatterBannedUntilThisYear.format(new Date(j2));
            }
            return getInstance().formatterBannedUntil.format(new Date(j2));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String stringForMessageListDate(long j) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(6);
            calendar.setTimeInMillis(j2);
            int i2 = calendar.get(6);
            if (Math.abs(System.currentTimeMillis() - j2) >= 31536000000L) {
                return getInstance().formatterYear.format(new Date(j2));
            }
            int i3 = i2 - i;
            if (i3 != 0 && (i3 != -1 || System.currentTimeMillis() - j2 >= 28800000)) {
                if (i3 > -7 && i3 <= -1) {
                    return getInstance().formatterWeek.format(new Date(j2));
                }
                return getInstance().formatterDayMonth.format(new Date(j2));
            }
            return getInstance().formatterDay.format(new Date(j2));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatShortNumber(int i, int[] iArr) {
        StringBuilder sb = new StringBuilder();
        int i2 = 0;
        while (true) {
            int i3 = i / 1000;
            if (i3 <= 0) {
                break;
            }
            sb.append("K");
            i2 = (i % 1000) / 100;
            i = i3;
        }
        if (iArr != null) {
            double d = i;
            double d2 = i2;
            Double.isNaN(d2);
            Double.isNaN(d);
            double d3 = d + (d2 / 10.0d);
            for (int i4 = 0; i4 < sb.length(); i4++) {
                d3 *= 1000.0d;
            }
            iArr[0] = (int) d3;
        }
        return (i2 == 0 || sb.length() <= 0) ? sb.length() == 2 ? String.format(Locale.US, "%dM", Integer.valueOf(i)) : String.format(Locale.US, "%d%s", Integer.valueOf(i), sb.toString()) : sb.length() == 2 ? String.format(Locale.US, "%d.%dM", Integer.valueOf(i), Integer.valueOf(i2)) : String.format(Locale.US, "%d.%d%s", Integer.valueOf(i), Integer.valueOf(i2), sb.toString());
    }

    public static String formatUserStatus(int i, TLRPC$User tLRPC$User) {
        return formatUserStatus(i, tLRPC$User, null);
    }

    public static String formatJoined(long j) {
        long j2 = j * 1000;
        try {
            return formatString("ChannelOtherSubscriberJoined", org.telegram.messenger.beta.R.string.ChannelOtherSubscriberJoined, Math.abs(System.currentTimeMillis() - j2) < 31536000000L ? formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().formatterDayMonth.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2))) : formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().formatterYear.format(new Date(j2)), getInstance().formatterDay.format(new Date(j2))));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatImportedDate(long j) {
        try {
            Date date = new Date(j * 1000);
            return String.format("%1$s, %2$s", getInstance().formatterYear.format(date), getInstance().formatterDay.format(date));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatUserStatus(int i, TLRPC$User tLRPC$User, boolean[] zArr) {
        return formatUserStatus(i, tLRPC$User, zArr, null);
    }

    public static String formatUserStatus(int i, TLRPC$User tLRPC$User, boolean[] zArr, boolean[] zArr2) {
        TLRPC$UserStatus tLRPC$UserStatus;
        TLRPC$UserStatus tLRPC$UserStatus2;
        TLRPC$UserStatus tLRPC$UserStatus3;
        if (tLRPC$User != null && (tLRPC$UserStatus3 = tLRPC$User.status) != null && tLRPC$UserStatus3.expires == 0) {
            if (tLRPC$UserStatus3 instanceof TLRPC$TL_userStatusRecently) {
                tLRPC$UserStatus3.expires = -100;
            } else if (tLRPC$UserStatus3 instanceof TLRPC$TL_userStatusLastWeek) {
                tLRPC$UserStatus3.expires = -101;
            } else if (tLRPC$UserStatus3 instanceof TLRPC$TL_userStatusLastMonth) {
                tLRPC$UserStatus3.expires = -102;
            }
        }
        if (tLRPC$User != null && (tLRPC$UserStatus2 = tLRPC$User.status) != null && tLRPC$UserStatus2.expires <= 0 && MessagesController.getInstance(i).onlinePrivacy.containsKey(Long.valueOf(tLRPC$User.id))) {
            if (zArr != null) {
                zArr[0] = true;
            }
            return getString("Online", org.telegram.messenger.beta.R.string.Online);
        } else if (tLRPC$User == null || (tLRPC$UserStatus = tLRPC$User.status) == null || tLRPC$UserStatus.expires == 0 || UserObject.isDeleted(tLRPC$User) || (tLRPC$User instanceof TLRPC$TL_userEmpty)) {
            return getString("ALongTimeAgo", org.telegram.messenger.beta.R.string.ALongTimeAgo);
        } else {
            int currentTime = ConnectionsManager.getInstance(i).getCurrentTime();
            int i2 = tLRPC$User.status.expires;
            if (i2 > currentTime) {
                if (zArr != null) {
                    zArr[0] = true;
                }
                return getString("Online", org.telegram.messenger.beta.R.string.Online);
            } else if (i2 == -1) {
                return getString("Invisible", org.telegram.messenger.beta.R.string.Invisible);
            } else {
                if (i2 == -100) {
                    return getString("Lately", org.telegram.messenger.beta.R.string.Lately);
                }
                if (i2 == -101) {
                    return getString("WithinAWeek", org.telegram.messenger.beta.R.string.WithinAWeek);
                }
                if (i2 == -102) {
                    return getString("WithinAMonth", org.telegram.messenger.beta.R.string.WithinAMonth);
                }
                return formatDateOnline(i2, zArr2);
            }
        }
    }

    private String escapeString(String str) {
        return str.contains("[CDATA") ? str : str.replace("<", "&lt;").replace(">", "&gt;").replace("& ", "&amp; ");
    }

    public void saveRemoteLocaleStringsForCurrentLocale(TLRPC$TL_langPackDifference tLRPC$TL_langPackDifference, int i) {
        if (this.currentLocaleInfo == null) {
            return;
        }
        String lowerCase = tLRPC$TL_langPackDifference.lang_code.replace('-', '_').toLowerCase();
        if (!lowerCase.equals(this.currentLocaleInfo.shortName) && !lowerCase.equals(this.currentLocaleInfo.baseLangCode)) {
            return;
        }
        lambda$applyRemoteLanguage$8(this.currentLocaleInfo, tLRPC$TL_langPackDifference, i);
    }

    /* renamed from: saveRemoteLocaleStrings */
    public void lambda$applyRemoteLanguage$8(final LocaleInfo localeInfo, final TLRPC$TL_langPackDifference tLRPC$TL_langPackDifference, int i) {
        final int i2;
        File file;
        HashMap<String, String> hashMap;
        if (tLRPC$TL_langPackDifference == null || tLRPC$TL_langPackDifference.strings.isEmpty() || localeInfo == null || localeInfo.isLocal()) {
            return;
        }
        String lowerCase = tLRPC$TL_langPackDifference.lang_code.replace('-', '_').toLowerCase();
        if (lowerCase.equals(localeInfo.shortName)) {
            i2 = 0;
        } else {
            i2 = lowerCase.equals(localeInfo.baseLangCode) ? 1 : -1;
        }
        if (i2 == -1) {
            return;
        }
        if (i2 == 0) {
            file = localeInfo.getPathToFile();
        } else {
            file = localeInfo.getPathToBaseFile();
        }
        try {
            if (tLRPC$TL_langPackDifference.from_version == 0) {
                hashMap = new HashMap<>();
            } else {
                hashMap = getLocaleFileStrings(file, true);
            }
            for (int i3 = 0; i3 < tLRPC$TL_langPackDifference.strings.size(); i3++) {
                TLRPC$LangPackString tLRPC$LangPackString = tLRPC$TL_langPackDifference.strings.get(i3);
                if (tLRPC$LangPackString instanceof TLRPC$TL_langPackString) {
                    hashMap.put(tLRPC$LangPackString.key, escapeString(tLRPC$LangPackString.value));
                } else if (tLRPC$LangPackString instanceof TLRPC$TL_langPackStringPluralized) {
                    String str = tLRPC$LangPackString.key + "_zero";
                    String str2 = tLRPC$LangPackString.zero_value;
                    String str3 = "";
                    hashMap.put(str, str2 != null ? escapeString(str2) : str3);
                    String str4 = tLRPC$LangPackString.key + "_one";
                    String str5 = tLRPC$LangPackString.one_value;
                    hashMap.put(str4, str5 != null ? escapeString(str5) : str3);
                    String str6 = tLRPC$LangPackString.key + "_two";
                    String str7 = tLRPC$LangPackString.two_value;
                    hashMap.put(str6, str7 != null ? escapeString(str7) : str3);
                    String str8 = tLRPC$LangPackString.key + "_few";
                    String str9 = tLRPC$LangPackString.few_value;
                    hashMap.put(str8, str9 != null ? escapeString(str9) : str3);
                    String str10 = tLRPC$LangPackString.key + "_many";
                    String str11 = tLRPC$LangPackString.many_value;
                    hashMap.put(str10, str11 != null ? escapeString(str11) : str3);
                    String str12 = tLRPC$LangPackString.key + "_other";
                    String str13 = tLRPC$LangPackString.other_value;
                    if (str13 != null) {
                        str3 = escapeString(str13);
                    }
                    hashMap.put(str12, str3);
                } else if (tLRPC$LangPackString instanceof TLRPC$TL_langPackStringDeleted) {
                    hashMap.remove(tLRPC$LangPackString.key);
                }
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("save locale file to " + file);
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            bufferedWriter.write("<resources>\n");
            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                bufferedWriter.write(String.format("<string name=\"%1$s\">%2$s</string>\n", entry.getKey(), entry.getValue()));
            }
            bufferedWriter.write("</resources>");
            bufferedWriter.close();
            boolean hasBaseLang = localeInfo.hasBaseLang();
            final HashMap<String, String> localeFileStrings = getLocaleFileStrings(hasBaseLang ? localeInfo.getPathToBaseFile() : localeInfo.getPathToFile());
            if (hasBaseLang) {
                localeFileStrings.putAll(getLocaleFileStrings(localeInfo.getPathToFile()));
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    LocaleController.this.lambda$saveRemoteLocaleStrings$5(i2, localeInfo, tLRPC$TL_langPackDifference, localeFileStrings);
                }
            });
        } catch (Exception unused) {
        }
    }

    public /* synthetic */ void lambda$saveRemoteLocaleStrings$5(int i, LocaleInfo localeInfo, TLRPC$TL_langPackDifference tLRPC$TL_langPackDifference, HashMap hashMap) {
        String[] strArr;
        Locale locale;
        if (i == 0) {
            localeInfo.version = tLRPC$TL_langPackDifference.version;
        } else {
            localeInfo.baseVersion = tLRPC$TL_langPackDifference.version;
        }
        saveOtherLanguages();
        try {
            if (this.currentLocaleInfo == localeInfo) {
                if (!TextUtils.isEmpty(localeInfo.pluralLangCode)) {
                    strArr = localeInfo.pluralLangCode.split("_");
                } else if (!TextUtils.isEmpty(localeInfo.baseLangCode)) {
                    strArr = localeInfo.baseLangCode.split("_");
                } else {
                    strArr = localeInfo.shortName.split("_");
                }
                if (strArr.length == 1) {
                    locale = new Locale(strArr[0]);
                } else {
                    locale = new Locale(strArr[0], strArr[1]);
                }
                this.languageOverride = localeInfo.shortName;
                SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
                edit.putString("language", localeInfo.getKey());
                edit.commit();
                this.localeValues = hashMap;
                this.currentLocale = locale;
                this.currentLocaleInfo = localeInfo;
                if (!TextUtils.isEmpty(localeInfo.pluralLangCode)) {
                    this.currentPluralRules = this.allRules.get(this.currentLocaleInfo.pluralLangCode);
                }
                if (this.currentPluralRules == null) {
                    PluralRules pluralRules = this.allRules.get(this.currentLocale.getLanguage());
                    this.currentPluralRules = pluralRules;
                    if (pluralRules == null) {
                        this.currentPluralRules = this.allRules.get("en");
                    }
                }
                this.changingConfiguration = true;
                Locale.setDefault(this.currentLocale);
                Configuration configuration = new Configuration();
                configuration.locale = this.currentLocale;
                ApplicationLoader.applicationContext.getResources().updateConfiguration(configuration, ApplicationLoader.applicationContext.getResources().getDisplayMetrics());
                this.changingConfiguration = false;
            }
        } catch (Exception e) {
            FileLog.e(e);
            this.changingConfiguration = false;
        }
        recreateFormatters();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.reloadInterface, new Object[0]);
    }

    public void loadRemoteLanguages(int i) {
        loadRemoteLanguages(i, true);
    }

    public void loadRemoteLanguages(final int i, final boolean z) {
        if (this.loadingRemoteLanguages) {
            return;
        }
        this.loadingRemoteLanguages = true;
        ConnectionsManager.getInstance(i).sendRequest(new TLObject() { // from class: org.telegram.tgnet.TLRPC$TL_langpack_getLanguages
            public static int constructor = -2146445955;

            @Override // org.telegram.tgnet.TLObject
            public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i2, boolean z2) {
                TLRPC$Vector tLRPC$Vector = new TLRPC$Vector();
                int readInt32 = abstractSerializedData.readInt32(z2);
                for (int i3 = 0; i3 < readInt32; i3++) {
                    TLRPC$TL_langPackLanguage TLdeserialize = TLRPC$TL_langPackLanguage.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z2), z2);
                    if (TLdeserialize == null) {
                        return tLRPC$Vector;
                    }
                    tLRPC$Vector.objects.add(TLdeserialize);
                }
                return tLRPC$Vector;
            }

            @Override // org.telegram.tgnet.TLObject
            public void serializeToStream(AbstractSerializedData abstractSerializedData) {
                abstractSerializedData.writeInt32(constructor);
            }
        }, new RequestDelegate() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda15
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                LocaleController.this.lambda$loadRemoteLanguages$7(z, i, tLObject, tLRPC$TL_error);
            }
        }, 8);
    }

    public /* synthetic */ void lambda$loadRemoteLanguages$7(final boolean z, final int i, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    LocaleController.this.lambda$loadRemoteLanguages$6(tLObject, z, i);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadRemoteLanguages$6(TLObject tLObject, boolean z, int i) {
        this.loadingRemoteLanguages = false;
        TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
        int size = this.remoteLanguages.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.remoteLanguages.get(i2).serverIndex = Integer.MAX_VALUE;
        }
        int size2 = tLRPC$Vector.objects.size();
        for (int i3 = 0; i3 < size2; i3++) {
            TLRPC$TL_langPackLanguage tLRPC$TL_langPackLanguage = (TLRPC$TL_langPackLanguage) tLRPC$Vector.objects.get(i3);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("loaded lang " + tLRPC$TL_langPackLanguage.name);
            }
            LocaleInfo localeInfo = new LocaleInfo();
            localeInfo.nameEnglish = tLRPC$TL_langPackLanguage.name;
            localeInfo.name = tLRPC$TL_langPackLanguage.native_name;
            localeInfo.shortName = tLRPC$TL_langPackLanguage.lang_code.replace('-', '_').toLowerCase();
            String str = tLRPC$TL_langPackLanguage.base_lang_code;
            if (str != null) {
                localeInfo.baseLangCode = str.replace('-', '_').toLowerCase();
            } else {
                localeInfo.baseLangCode = "";
            }
            localeInfo.pluralLangCode = tLRPC$TL_langPackLanguage.plural_code.replace('-', '_').toLowerCase();
            localeInfo.isRtl = tLRPC$TL_langPackLanguage.rtl;
            localeInfo.pathToFile = "remote";
            localeInfo.serverIndex = i3;
            LocaleInfo languageFromDict = getLanguageFromDict(localeInfo.getKey());
            if (languageFromDict == null) {
                this.languages.add(localeInfo);
                this.languagesDict.put(localeInfo.getKey(), localeInfo);
            } else {
                languageFromDict.nameEnglish = localeInfo.nameEnglish;
                languageFromDict.name = localeInfo.name;
                languageFromDict.baseLangCode = localeInfo.baseLangCode;
                languageFromDict.pluralLangCode = localeInfo.pluralLangCode;
                languageFromDict.pathToFile = localeInfo.pathToFile;
                languageFromDict.serverIndex = localeInfo.serverIndex;
                localeInfo = languageFromDict;
            }
            if (!this.remoteLanguagesDict.containsKey(localeInfo.getKey())) {
                this.remoteLanguages.add(localeInfo);
                this.remoteLanguagesDict.put(localeInfo.getKey(), localeInfo);
            }
        }
        int i4 = 0;
        while (i4 < this.remoteLanguages.size()) {
            LocaleInfo localeInfo2 = this.remoteLanguages.get(i4);
            if (localeInfo2.serverIndex == Integer.MAX_VALUE && localeInfo2 != this.currentLocaleInfo) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("remove lang " + localeInfo2.getKey());
                }
                this.remoteLanguages.remove(i4);
                this.remoteLanguagesDict.remove(localeInfo2.getKey());
                this.languages.remove(localeInfo2);
                this.languagesDict.remove(localeInfo2.getKey());
                i4--;
            }
            i4++;
        }
        saveOtherLanguages();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.suggestedLangpack, new Object[0]);
        if (z) {
            applyLanguage(this.currentLocaleInfo, true, false, i);
        }
    }

    private void applyRemoteLanguage(final LocaleInfo localeInfo, String str, boolean z, final int i) {
        if (localeInfo != null) {
            if (!localeInfo.isRemote() && !localeInfo.isUnofficial()) {
                return;
            }
            if (localeInfo.hasBaseLang() && (str == null || str.equals(localeInfo.baseLangCode))) {
                if (localeInfo.baseVersion != 0 && !z) {
                    if (localeInfo.hasBaseLang()) {
                        TLRPC$TL_langpack_getDifference tLRPC$TL_langpack_getDifference = new TLRPC$TL_langpack_getDifference();
                        tLRPC$TL_langpack_getDifference.from_version = localeInfo.baseVersion;
                        tLRPC$TL_langpack_getDifference.lang_code = localeInfo.getBaseLangCode();
                        tLRPC$TL_langpack_getDifference.lang_pack = "";
                        ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_langpack_getDifference, new RequestDelegate() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda11
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                LocaleController.this.lambda$applyRemoteLanguage$9(localeInfo, i, tLObject, tLRPC$TL_error);
                            }
                        }, 8);
                    }
                } else {
                    TLRPC$TL_langpack_getLangPack tLRPC$TL_langpack_getLangPack = new TLRPC$TL_langpack_getLangPack();
                    tLRPC$TL_langpack_getLangPack.lang_code = localeInfo.getBaseLangCode();
                    ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_langpack_getLangPack, new RequestDelegate() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda12
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            LocaleController.this.lambda$applyRemoteLanguage$11(localeInfo, i, tLObject, tLRPC$TL_error);
                        }
                    }, 8);
                }
            }
            if (str != null && !str.equals(localeInfo.shortName)) {
                return;
            }
            if (localeInfo.version != 0 && !z) {
                TLRPC$TL_langpack_getDifference tLRPC$TL_langpack_getDifference2 = new TLRPC$TL_langpack_getDifference();
                tLRPC$TL_langpack_getDifference2.from_version = localeInfo.version;
                tLRPC$TL_langpack_getDifference2.lang_code = localeInfo.getLangCode();
                tLRPC$TL_langpack_getDifference2.lang_pack = "";
                ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_langpack_getDifference2, new RequestDelegate() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda14
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        LocaleController.this.lambda$applyRemoteLanguage$13(localeInfo, i, tLObject, tLRPC$TL_error);
                    }
                }, 8);
                return;
            }
            for (int i2 = 0; i2 < 4; i2++) {
                ConnectionsManager.setLangCode(localeInfo.getLangCode());
            }
            TLRPC$TL_langpack_getLangPack tLRPC$TL_langpack_getLangPack2 = new TLRPC$TL_langpack_getLangPack();
            tLRPC$TL_langpack_getLangPack2.lang_code = localeInfo.getLangCode();
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_langpack_getLangPack2, new RequestDelegate() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda13
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    LocaleController.this.lambda$applyRemoteLanguage$15(localeInfo, i, tLObject, tLRPC$TL_error);
                }
            }, 8);
        }
    }

    public /* synthetic */ void lambda$applyRemoteLanguage$9(final LocaleInfo localeInfo, final int i, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    LocaleController.this.lambda$applyRemoteLanguage$8(localeInfo, tLObject, i);
                }
            });
        }
    }

    public /* synthetic */ void lambda$applyRemoteLanguage$11(final LocaleInfo localeInfo, final int i, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    LocaleController.this.lambda$applyRemoteLanguage$10(localeInfo, tLObject, i);
                }
            });
        }
    }

    public /* synthetic */ void lambda$applyRemoteLanguage$13(final LocaleInfo localeInfo, final int i, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    LocaleController.this.lambda$applyRemoteLanguage$12(localeInfo, tLObject, i);
                }
            });
        }
    }

    public /* synthetic */ void lambda$applyRemoteLanguage$15(final LocaleInfo localeInfo, final int i, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    LocaleController.this.lambda$applyRemoteLanguage$14(localeInfo, tLObject, i);
                }
            });
        }
    }

    public String getTranslitString(String str) {
        return getTranslitString(str, true, false);
    }

    public String getTranslitString(String str, boolean z) {
        return getTranslitString(str, true, z);
    }

    public String getTranslitString(String str, boolean z, boolean z2) {
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        String str7;
        String str8;
        String str9;
        String str10;
        if (str == null) {
            return null;
        }
        if (this.ruTranslitChars == null) {
            HashMap<String, String> hashMap = new HashMap<>(33);
            this.ruTranslitChars = hashMap;
            hashMap.put("??", "a");
            this.ruTranslitChars.put("??", "b");
            this.ruTranslitChars.put("??", "v");
            this.ruTranslitChars.put("??", ImageLoader.AUTOPLAY_FILTER);
            this.ruTranslitChars.put("??", "d");
            this.ruTranslitChars.put("??", e.a);
            HashMap<String, String> hashMap2 = this.ruTranslitChars;
            str2 = ImageLoader.AUTOPLAY_FILTER;
            hashMap2.put("??", "yo");
            this.ruTranslitChars.put("??", "zh");
            this.ruTranslitChars.put("??", "z");
            this.ruTranslitChars.put("??", i.TAG);
            this.ruTranslitChars.put("??", i.TAG);
            this.ruTranslitChars.put("??", "k");
            this.ruTranslitChars.put("??", "l");
            this.ruTranslitChars.put("??", "m");
            this.ruTranslitChars.put("??", "n");
            this.ruTranslitChars.put("??", "o");
            this.ruTranslitChars.put("??", "p");
            str3 = "m";
            str10 = "r";
            this.ruTranslitChars.put("??", str10);
            str4 = "z";
            this.ruTranslitChars.put("??", "s");
            str5 = "v";
            this.ruTranslitChars.put("??", "t");
            str9 = "u";
            this.ruTranslitChars.put("??", str9);
            str6 = "s";
            this.ruTranslitChars.put("??", "f");
            str8 = "h";
            this.ruTranslitChars.put("??", str8);
            str7 = "p";
            this.ruTranslitChars.put("??", "ts");
            this.ruTranslitChars.put("??", "ch");
            this.ruTranslitChars.put("??", "sh");
            this.ruTranslitChars.put("??", "sch");
            this.ruTranslitChars.put("??", i.TAG);
            this.ruTranslitChars.put("??", "");
            this.ruTranslitChars.put("??", "");
            this.ruTranslitChars.put("??", e.a);
            this.ruTranslitChars.put("??", "yu");
            this.ruTranslitChars.put("??", "ya");
        } else {
            str3 = "m";
            str2 = ImageLoader.AUTOPLAY_FILTER;
            str10 = "r";
            str4 = "z";
            str8 = "h";
            str7 = "p";
            str5 = "v";
            str9 = "u";
            str6 = "s";
        }
        if (this.translitChars == null) {
            HashMap<String, String> hashMap3 = new HashMap<>(487);
            this.translitChars = hashMap3;
            hashMap3.put("??", c.a);
            this.translitChars.put("???", "n");
            this.translitChars.put("??", "d");
            this.translitChars.put("???", "y");
            this.translitChars.put("???", "o");
            this.translitChars.put("??", "o");
            this.translitChars.put("???", "a");
            this.translitChars.put("??", str8);
            this.translitChars.put("??", "y");
            this.translitChars.put("??", "k");
            this.translitChars.put("???", str9);
            String str11 = str9;
            this.translitChars.put("???", "aa");
            this.translitChars.put("??", "ij");
            this.translitChars.put("???", "l");
            this.translitChars.put("??", i.TAG);
            this.translitChars.put("???", "b");
            this.translitChars.put("??", str10);
            this.translitChars.put("??", e.a);
            this.translitChars.put("???", "ffi");
            this.translitChars.put("??", "o");
            this.translitChars.put("???", str10);
            this.translitChars.put("???", "o");
            this.translitChars.put("??", i.TAG);
            String str12 = str7;
            this.translitChars.put("???", str12);
            this.translitChars.put("??", "y");
            this.translitChars.put("???", e.a);
            this.translitChars.put("???", "o");
            this.translitChars.put("???", "a");
            this.translitChars.put("??", "b");
            this.translitChars.put("???", e.a);
            this.translitChars.put("??", c.a);
            this.translitChars.put("??", str8);
            this.translitChars.put("???", "b");
            String str13 = str8;
            String str14 = str6;
            this.translitChars.put("???", str14);
            this.translitChars.put("??", "d");
            this.translitChars.put("???", "o");
            this.translitChars.put("??", "j");
            this.translitChars.put("???", "a");
            this.translitChars.put("??", "y");
            this.translitChars.put("??", str5);
            this.translitChars.put("???", str12);
            this.translitChars.put("???", "fi");
            this.translitChars.put("???", "k");
            this.translitChars.put("???", "d");
            this.translitChars.put("???", "l");
            this.translitChars.put("??", e.a);
            this.translitChars.put("???", "k");
            this.translitChars.put("??", c.a);
            this.translitChars.put("??", str10);
            this.translitChars.put("??", "hv");
            this.translitChars.put("??", "b");
            this.translitChars.put("???", "o");
            this.translitChars.put("??", "ou");
            this.translitChars.put("??", "j");
            String str15 = str2;
            this.translitChars.put("???", str15);
            this.translitChars.put("???", "n");
            this.translitChars.put("??", "j");
            this.translitChars.put("??", str15);
            this.translitChars.put("??", "dz");
            String str16 = str4;
            this.translitChars.put("??", str16);
            this.translitChars.put("???", "au");
            this.translitChars.put("??", str11);
            this.translitChars.put("???", str15);
            this.translitChars.put("??", "o");
            this.translitChars.put("??", "a");
            this.translitChars.put("??", "a");
            this.translitChars.put("??", "o");
            this.translitChars.put("??", str10);
            this.translitChars.put("???", "o");
            this.translitChars.put("??", "a");
            this.translitChars.put("??", "l");
            this.translitChars.put("??", str14);
            this.translitChars.put("???", "fl");
            this.translitChars.put("??", i.TAG);
            this.translitChars.put("???", e.a);
            this.translitChars.put("???", "n");
            this.translitChars.put("??", i.TAG);
            this.translitChars.put("??", "n");
            this.translitChars.put("???", i.TAG);
            this.translitChars.put("??", "t");
            this.translitChars.put("???", str16);
            this.translitChars.put("???", "y");
            this.translitChars.put("??", "y");
            this.translitChars.put("???", str14);
            this.translitChars.put("??", str10);
            this.translitChars.put("??", str15);
            this.translitChars.put("???", str11);
            this.translitChars.put("???", "k");
            this.translitChars.put("???", "et");
            this.translitChars.put("??", i.TAG);
            this.translitChars.put("??", "t");
            this.translitChars.put("???", c.a);
            this.translitChars.put("??", "l");
            this.translitChars.put("???", "av");
            this.translitChars.put("??", str11);
            this.translitChars.put("??", "ae");
            this.translitChars.put("??", "a");
            this.translitChars.put("??", str11);
            this.translitChars.put("???", str14);
            this.translitChars.put("???", str10);
            this.translitChars.put("???", "a");
            this.translitChars.put("??", "b");
            this.translitChars.put("???", str13);
            this.translitChars.put("???", str14);
            this.translitChars.put("???", e.a);
            this.translitChars.put("??", str13);
            this.translitChars.put("???", "x");
            this.translitChars.put("???", "k");
            this.translitChars.put("???", "d");
            this.translitChars.put("??", "oi");
            this.translitChars.put("???", str12);
            this.translitChars.put("??", str13);
            String str17 = str5;
            this.translitChars.put("???", str17);
            this.translitChars.put("???", "w");
            this.translitChars.put("??", "n");
            String str18 = str3;
            this.translitChars.put("??", str18);
            this.translitChars.put("??", str15);
            this.translitChars.put("??", "n");
            this.translitChars.put("???", str12);
            this.translitChars.put("???", str17);
            this.translitChars.put("??", str11);
            this.translitChars.put("???", "b");
            this.translitChars.put("???", str12);
            this.translitChars.put("??", "a");
            this.translitChars.put("??", c.a);
            this.translitChars.put("???", "o");
            this.translitChars.put("???", "a");
            this.translitChars.put("??", "f");
            this.translitChars.put("??", "ae");
            this.translitChars.put("???", "vy");
            this.translitChars.put("???", "ff");
            this.translitChars.put("???", str10);
            this.translitChars.put("??", "o");
            this.translitChars.put("??", "o");
            this.translitChars.put("???", str11);
            this.translitChars.put("??", str16);
            this.translitChars.put("???", "f");
            this.translitChars.put("???", "d");
            this.translitChars.put("??", e.a);
            this.translitChars.put("??", str11);
            this.translitChars.put("??", "n");
            this.translitChars.put("??", "q");
            this.translitChars.put("???", "a");
            this.translitChars.put("??", "k");
            this.translitChars.put("??", i.TAG);
            this.translitChars.put("???", str11);
            this.translitChars.put("??", "t");
            this.translitChars.put("??", str10);
            this.translitChars.put("??", "k");
            this.translitChars.put("???", "t");
            this.translitChars.put("???", "q");
            this.translitChars.put("???", "a");
            this.translitChars.put("??", "j");
            this.translitChars.put("??", "l");
            this.translitChars.put("???", "f");
            this.translitChars.put("???", str14);
            this.translitChars.put("???", str10);
            this.translitChars.put("???", str17);
            this.translitChars.put("??", "o");
            this.translitChars.put("???", c.a);
            this.translitChars.put("???", str11);
            this.translitChars.put("???", str16);
            this.translitChars.put("???", str11);
            this.translitChars.put("??", "n");
            this.translitChars.put("??", "w");
            this.translitChars.put("???", "a");
            this.translitChars.put("??", "lj");
            this.translitChars.put("??", "b");
            this.translitChars.put("??", str10);
            this.translitChars.put("??", "o");
            this.translitChars.put("???", "w");
            this.translitChars.put("??", "d");
            this.translitChars.put("???", "ay");
            this.translitChars.put("??", str11);
            this.translitChars.put("???", "b");
            this.translitChars.put("??", str11);
            this.translitChars.put("???", e.a);
            this.translitChars.put("??", "a");
            this.translitChars.put("??", str13);
            this.translitChars.put("???", "o");
            this.translitChars.put("??", str11);
            this.translitChars.put("??", "y");
            this.translitChars.put("??", "o");
            this.translitChars.put("???", e.a);
            this.translitChars.put("???", e.a);
            this.translitChars.put("??", i.TAG);
            this.translitChars.put("???", e.a);
            this.translitChars.put("???", "t");
            this.translitChars.put("???", "d");
            this.translitChars.put("???", str13);
            this.translitChars.put("???", str14);
            this.translitChars.put("??", e.a);
            this.translitChars.put("???", str18);
            this.translitChars.put("??", "o");
            this.translitChars.put("??", e.a);
            this.translitChars.put("??", i.TAG);
            this.translitChars.put("??", "d");
            this.translitChars.put("???", str18);
            this.translitChars.put("???", "y");
            this.translitChars.put("??", "w");
            this.translitChars.put("???", e.a);
            this.translitChars.put("???", str11);
            this.translitChars.put("??", str16);
            this.translitChars.put("??", "j");
            this.translitChars.put("???", "d");
            this.translitChars.put("??", str11);
            this.translitChars.put("??", "j");
            this.translitChars.put("??", e.a);
            this.translitChars.put("??", str11);
            this.translitChars.put("??", str15);
            this.translitChars.put("???", str10);
            this.translitChars.put("??", "n");
            this.translitChars.put("???", e.a);
            this.translitChars.put("???", str14);
            this.translitChars.put("???", "d");
            this.translitChars.put("??", "k");
            this.translitChars.put("???", "ae");
            this.translitChars.put("??", e.a);
            this.translitChars.put("???", "o");
            this.translitChars.put("???", str18);
            this.translitChars.put("???", "f");
            this.translitChars.put("???", "a");
            this.translitChars.put("???", "oo");
            this.translitChars.put("???", str18);
            this.translitChars.put("???", str12);
            this.translitChars.put("???", str11);
            this.translitChars.put("???", "k");
            this.translitChars.put("???", str13);
            this.translitChars.put("??", "t");
            this.translitChars.put("???", str12);
            this.translitChars.put("???", str18);
            this.translitChars.put("??", "a");
            this.translitChars.put("???", "n");
            this.translitChars.put("???", str17);
            this.translitChars.put("??", e.a);
            this.translitChars.put("???", str16);
            this.translitChars.put("???", "d");
            this.translitChars.put("???", str12);
            this.translitChars.put("??", "l");
            this.translitChars.put("???", str16);
            this.translitChars.put("??", str18);
            this.translitChars.put("???", str10);
            this.translitChars.put("???", str17);
            this.translitChars.put("??", str11);
            this.translitChars.put("??", "ss");
            this.translitChars.put("??", str13);
            this.translitChars.put("???", "t");
            this.translitChars.put("??", str16);
            this.translitChars.put("???", str10);
            this.translitChars.put("??", "n");
            this.translitChars.put("??", "a");
            this.translitChars.put("???", "y");
            this.translitChars.put("???", "y");
            this.translitChars.put("???", "oe");
            this.translitChars.put("???", "x");
            this.translitChars.put("??", str11);
            this.translitChars.put("???", "j");
            this.translitChars.put("???", "a");
            this.translitChars.put("??", str16);
            this.translitChars.put("???", str14);
            this.translitChars.put("???", i.TAG);
            this.translitChars.put("???", "ao");
            this.translitChars.put("??", str16);
            this.translitChars.put("??", "y");
            this.translitChars.put("??", e.a);
            this.translitChars.put("??", "o");
            this.translitChars.put("???", "d");
            this.translitChars.put("???", "l");
            this.translitChars.put("??", str11);
            this.translitChars.put("???", "a");
            this.translitChars.put("???", "b");
            this.translitChars.put("???", str11);
            this.translitChars.put("???", "a");
            this.translitChars.put("???", "t");
            this.translitChars.put("??", "y");
            this.translitChars.put("???", "t");
            this.translitChars.put("???", "l");
            this.translitChars.put("??", "j");
            this.translitChars.put("???", str16);
            this.translitChars.put("???", str13);
            this.translitChars.put("???", "w");
            this.translitChars.put("???", "k");
            this.translitChars.put("???", "o");
            this.translitChars.put("??", i.TAG);
            this.translitChars.put("??", str15);
            this.translitChars.put("??", e.a);
            this.translitChars.put("??", "a");
            this.translitChars.put("???", "a");
            this.translitChars.put("??", "q");
            this.translitChars.put("???", "t");
            this.translitChars.put("???", "um");
            this.translitChars.put("???", c.a);
            this.translitChars.put("???", "x");
            this.translitChars.put("???", str11);
            this.translitChars.put("???", i.TAG);
            this.translitChars.put("???", str10);
            this.translitChars.put("??", str14);
            this.translitChars.put("???", "o");
            this.translitChars.put("???", "y");
            this.translitChars.put("???", str14);
            this.translitChars.put("??", "nj");
            this.translitChars.put("??", "a");
            this.translitChars.put("???", "t");
            this.translitChars.put("??", "l");
            this.translitChars.put("??", str16);
            this.translitChars.put("???", "th");
            this.translitChars.put("??", "d");
            this.translitChars.put("??", str14);
            this.translitChars.put("??", str14);
            this.translitChars.put("???", str11);
            this.translitChars.put("???", e.a);
            this.translitChars.put("???", str14);
            this.translitChars.put("??", e.a);
            this.translitChars.put("???", str11);
            this.translitChars.put("???", "o");
            this.translitChars.put("??", str14);
            this.translitChars.put("???", str17);
            this.translitChars.put("???", "is");
            this.translitChars.put("???", "o");
            this.translitChars.put("??", e.a);
            this.translitChars.put("??", "a");
            this.translitChars.put("???", "ffl");
            this.translitChars.put("???", "o");
            this.translitChars.put("??", i.TAG);
            this.translitChars.put("???", "ue");
            this.translitChars.put("??", "d");
            this.translitChars.put("???", str16);
            this.translitChars.put("???", "w");
            this.translitChars.put("???", "a");
            this.translitChars.put("???", "t");
            this.translitChars.put("??", str15);
            this.translitChars.put("??", "n");
            this.translitChars.put("??", str15);
            this.translitChars.put("???", str11);
            this.translitChars.put("???", "a");
            this.translitChars.put("???", "n");
            this.translitChars.put("??", i.TAG);
            this.translitChars.put("???", str10);
            this.translitChars.put("??", "a");
            this.translitChars.put("??", str14);
            this.translitChars.put("??", "o");
            this.translitChars.put("??", str10);
            this.translitChars.put("??", "t");
            this.translitChars.put("???", i.TAG);
            this.translitChars.put("??", "ae");
            this.translitChars.put("???", str17);
            this.translitChars.put("??", "oe");
            this.translitChars.put("???", str18);
            this.translitChars.put("??", str16);
            this.translitChars.put("??", e.a);
            this.translitChars.put("???", "av");
            this.translitChars.put("???", "o");
            this.translitChars.put("???", e.a);
            this.translitChars.put("??", "l");
            this.translitChars.put("???", i.TAG);
            this.translitChars.put("???", "d");
            this.translitChars.put("???", "st");
            this.translitChars.put("???", "l");
            this.translitChars.put("??", str10);
            this.translitChars.put("???", "ou");
            this.translitChars.put("??", "t");
            this.translitChars.put("??", "a");
            this.translitChars.put("???", e.a);
            this.translitChars.put("???", "o");
            this.translitChars.put("??", c.a);
            this.translitChars.put("???", str14);
            this.translitChars.put("???", "a");
            this.translitChars.put("??", str11);
            this.translitChars.put("???", "a");
            this.translitChars.put("??", str15);
            this.translitChars.put("???", "k");
            this.translitChars.put("???", str16);
            this.translitChars.put("??", str14);
            this.translitChars.put("???", e.a);
            this.translitChars.put("??", str15);
            this.translitChars.put("???", "l");
            this.translitChars.put("???", "f");
            this.translitChars.put("???", "x");
            this.translitChars.put("??", "o");
            this.translitChars.put("??", e.a);
            this.translitChars.put("???", "o");
            this.translitChars.put("??", "t");
            this.translitChars.put("??", "o");
            this.translitChars.put("i??", i.TAG);
            this.translitChars.put("???", "n");
            this.translitChars.put("??", c.a);
            this.translitChars.put("???", str15);
            this.translitChars.put("???", "w");
            this.translitChars.put("???", "d");
            this.translitChars.put("???", "l");
            this.translitChars.put("??", "oe");
            this.translitChars.put("???", str10);
            this.translitChars.put("??", "l");
            this.translitChars.put("??", str10);
            this.translitChars.put("??", "o");
            this.translitChars.put("???", "n");
            this.translitChars.put("???", "ae");
            this.translitChars.put("??", "l");
            this.translitChars.put("??", "a");
            this.translitChars.put("??", str12);
            this.translitChars.put("???", "o");
            this.translitChars.put("??", i.TAG);
            this.translitChars.put("??", str10);
            this.translitChars.put("??", "dz");
            this.translitChars.put("???", str15);
            this.translitChars.put("???", str11);
            this.translitChars.put("??", "o");
            this.translitChars.put("??", "l");
            this.translitChars.put("???", "w");
            this.translitChars.put("??", "t");
            this.translitChars.put("??", "n");
            this.translitChars.put("??", str10);
            this.translitChars.put("??", "a");
            this.translitChars.put("??", str11);
            this.translitChars.put("???", "l");
            this.translitChars.put("???", "o");
            this.translitChars.put("???", "o");
            this.translitChars.put("???", "b");
            this.translitChars.put("??", str10);
            this.translitChars.put("???", str10);
            this.translitChars.put("??", "y");
            this.translitChars.put("???", "f");
            this.translitChars.put("???", str13);
            this.translitChars.put("??", "o");
            this.translitChars.put("??", str11);
            this.translitChars.put("???", str10);
            this.translitChars.put("??", str13);
            this.translitChars.put("??", "o");
            this.translitChars.put("??", str11);
            this.translitChars.put("???", "o");
            this.translitChars.put("???", str12);
            this.translitChars.put("???", i.TAG);
            this.translitChars.put("???", str11);
            this.translitChars.put("??", "a");
            this.translitChars.put("???", i.TAG);
            this.translitChars.put("???", "t");
            this.translitChars.put("???", e.a);
            this.translitChars.put("???", str11);
            this.translitChars.put("??", i.TAG);
            this.translitChars.put("??", "o");
            this.translitChars.put("??", str10);
            this.translitChars.put("??", str15);
            this.translitChars.put("??", str10);
            this.translitChars.put("???", str13);
            this.translitChars.put("??", str11);
            this.translitChars.put("??", "o");
            this.translitChars.put("???", "l");
            this.translitChars.put("???", str13);
            this.translitChars.put("??", "t");
            this.translitChars.put("??", "n");
            this.translitChars.put("???", e.a);
            this.translitChars.put("??", i.TAG);
            this.translitChars.put("???", "w");
            this.translitChars.put("??", e.a);
            this.translitChars.put("???", e.a);
            this.translitChars.put("??", "l");
            this.translitChars.put("???", "o");
            this.translitChars.put("??", "l");
            this.translitChars.put("???", "y");
            this.translitChars.put("???", "j");
            this.translitChars.put("???", "k");
            this.translitChars.put("???", str17);
            this.translitChars.put("??", e.a);
            this.translitChars.put("??", "a");
            this.translitChars.put("??", str14);
            this.translitChars.put("??", str10);
            this.translitChars.put("??", str17);
            this.translitChars.put("???", "a");
            this.translitChars.put("???", c.a);
            this.translitChars.put("???", e.a);
            this.translitChars.put("??", str18);
            this.translitChars.put("???", "w");
            this.translitChars.put("??", "o");
            this.translitChars.put("??", c.a);
            this.translitChars.put("??", str15);
            this.translitChars.put("??", c.a);
            this.translitChars.put("???", "o");
            this.translitChars.put("???", "k");
            this.translitChars.put("???", "q");
            this.translitChars.put("???", "o");
            this.translitChars.put("???", str14);
            this.translitChars.put("???", "o");
            this.translitChars.put("??", str13);
            this.translitChars.put("??", "o");
            this.translitChars.put("???", "tz");
            this.translitChars.put("???", e.a);
        }
        StringBuilder sb = new StringBuilder(str.length());
        int length = str.length();
        boolean z3 = false;
        int i = 0;
        while (i < length) {
            int i2 = i + 1;
            String substring = str.substring(i, i2);
            if (z2) {
                String lowerCase = substring.toLowerCase();
                boolean z4 = !substring.equals(lowerCase);
                substring = lowerCase;
                z3 = z4;
            }
            String str19 = this.translitChars.get(substring);
            if (str19 == null && z) {
                str19 = this.ruTranslitChars.get(substring);
            }
            if (str19 != null) {
                if (z2 && z3) {
                    if (str19.length() > 1) {
                        str19 = str19.substring(0, 1).toUpperCase() + str19.substring(1);
                    } else {
                        str19 = str19.toUpperCase();
                    }
                }
                sb.append(str19);
            } else {
                if (z2) {
                    char charAt = substring.charAt(0);
                    if ((charAt < 'a' || charAt > 'z' || charAt < '0' || charAt > '9') && charAt != ' ' && charAt != '\'' && charAt != ',' && charAt != '.' && charAt != '&' && charAt != '-' && charAt != '/') {
                        return null;
                    }
                    if (z3) {
                        substring = substring.toUpperCase();
                    }
                }
                sb.append(substring);
            }
            i = i2;
        }
        return sb.toString();
    }

    /* loaded from: classes.dex */
    public static class PluralRules_Slovenian extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            if (i2 == 1) {
                return 2;
            }
            if (i2 == 2) {
                return 4;
            }
            return (i2 < 3 || i2 > 4) ? 0 : 8;
        }
    }

    /* loaded from: classes.dex */
    public static class PluralRules_Romanian extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            if (i == 1) {
                return 2;
            }
            if (i == 0) {
                return 8;
            }
            return (i2 < 1 || i2 > 19) ? 0 : 8;
        }
    }

    /* loaded from: classes.dex */
    public static class PluralRules_Polish extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            int i3 = i % 10;
            if (i == 1) {
                return 2;
            }
            if (i3 >= 2 && i3 <= 4 && (i2 < 12 || i2 > 14)) {
                return 8;
            }
            if (i3 >= 0 && i3 <= 1) {
                return 16;
            }
            if (i3 >= 5 && i3 <= 9) {
                return 16;
            }
            return (i2 < 12 || i2 > 14) ? 0 : 16;
        }
    }

    /* loaded from: classes.dex */
    public static class PluralRules_Maltese extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            if (i == 1) {
                return 2;
            }
            if (i == 0) {
                return 8;
            }
            if (i2 >= 2 && i2 <= 10) {
                return 8;
            }
            return (i2 < 11 || i2 > 19) ? 0 : 16;
        }
    }

    /* loaded from: classes.dex */
    public static class PluralRules_Macedonian extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int i) {
            return (i % 10 != 1 || i == 11) ? 0 : 2;
        }
    }

    /* loaded from: classes.dex */
    public static class PluralRules_Lithuanian extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            int i3 = i % 10;
            if (i3 != 1 || (i2 >= 11 && i2 <= 19)) {
                if (i3 < 2 || i3 > 9) {
                    return 0;
                }
                return (i2 < 11 || i2 > 19) ? 8 : 0;
            }
            return 2;
        }
    }

    /* loaded from: classes.dex */
    public static class PluralRules_Latvian extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int i) {
            if (i == 0) {
                return 1;
            }
            return (i % 10 != 1 || i % 100 == 11) ? 0 : 2;
        }
    }

    /* loaded from: classes.dex */
    public static class PluralRules_Balkan extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            int i3 = i % 10;
            if (i3 != 1 || i2 == 11) {
                if (i3 >= 2 && i3 <= 4 && (i2 < 12 || i2 > 14)) {
                    return 8;
                }
                if (i3 == 0) {
                    return 16;
                }
                if (i3 >= 5 && i3 <= 9) {
                    return 16;
                }
                return (i2 < 11 || i2 > 14) ? 0 : 16;
            }
            return 2;
        }
    }

    /* loaded from: classes.dex */
    public static class PluralRules_Serbian extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            int i3 = i % 10;
            if (i3 != 1 || i2 == 11) {
                if (i3 < 2 || i3 > 4) {
                    return 0;
                }
                return (i2 < 12 || i2 > 14) ? 8 : 0;
            }
            return 2;
        }
    }

    /* loaded from: classes.dex */
    public static class PluralRules_Arabic extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            if (i == 0) {
                return 1;
            }
            if (i == 1) {
                return 2;
            }
            if (i == 2) {
                return 4;
            }
            if (i2 >= 3 && i2 <= 10) {
                return 8;
            }
            return (i2 < 11 || i2 > 99) ? 0 : 16;
        }
    }

    public static String addNbsp(String str) {
        return str.replace(' ', (char) 160);
    }

    public static void resetImperialSystemType() {
        useImperialSystemType = null;
    }

    public static boolean getUseImperialSystemType() {
        ensureImperialSystemInit();
        return useImperialSystemType.booleanValue();
    }

    public static void ensureImperialSystemInit() {
        if (useImperialSystemType != null) {
            return;
        }
        int i = SharedConfig.distanceSystemType;
        boolean z = true;
        if (i == 0) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (telephonyManager == null) {
                    return;
                }
                String upperCase = telephonyManager.getSimCountryIso().toUpperCase();
                if (!"US".equals(upperCase) && !"GB".equals(upperCase) && !"MM".equals(upperCase) && !"LR".equals(upperCase)) {
                    z = false;
                }
                useImperialSystemType = Boolean.valueOf(z);
                return;
            } catch (Exception e) {
                useImperialSystemType = Boolean.FALSE;
                FileLog.e(e);
                return;
            }
        }
        if (i != 2) {
            z = false;
        }
        useImperialSystemType = Boolean.valueOf(z);
    }

    public static String formatDistance(float f, int i) {
        return formatDistance(f, i, null);
    }

    public static String formatDistance(float f, int i, Boolean bool) {
        ensureImperialSystemInit();
        if (!((bool != null && bool.booleanValue()) || (bool == null && useImperialSystemType.booleanValue()))) {
            if (f < 1000.0f) {
                return i != 0 ? i != 1 ? formatString("MetersShort", org.telegram.messenger.beta.R.string.MetersShort, String.format("%d", Integer.valueOf((int) Math.max(1.0f, f)))) : formatString("MetersFromYou2", org.telegram.messenger.beta.R.string.MetersFromYou2, String.format("%d", Integer.valueOf((int) Math.max(1.0f, f)))) : formatString("MetersAway2", org.telegram.messenger.beta.R.string.MetersAway2, String.format("%d", Integer.valueOf((int) Math.max(1.0f, f))));
            }
            String format = f % 1000.0f == 0.0f ? String.format("%d", Integer.valueOf((int) (f / 1000.0f))) : String.format("%.2f", Float.valueOf(f / 1000.0f));
            if (i == 0) {
                return formatString("KMetersAway2", org.telegram.messenger.beta.R.string.KMetersAway2, format);
            }
            if (i == 1) {
                return formatString("KMetersFromYou2", org.telegram.messenger.beta.R.string.KMetersFromYou2, format);
            }
            return formatString("KMetersShort", org.telegram.messenger.beta.R.string.KMetersShort, format);
        }
        float f2 = f * 3.28084f;
        if (f2 < 1000.0f) {
            return i != 0 ? i != 1 ? formatString("FootsShort", org.telegram.messenger.beta.R.string.FootsShort, String.format("%d", Integer.valueOf((int) Math.max(1.0f, f2)))) : formatString("FootsFromYou", org.telegram.messenger.beta.R.string.FootsFromYou, String.format("%d", Integer.valueOf((int) Math.max(1.0f, f2)))) : formatString("FootsAway", org.telegram.messenger.beta.R.string.FootsAway, String.format("%d", Integer.valueOf((int) Math.max(1.0f, f2))));
        }
        String format2 = f2 % 5280.0f == 0.0f ? String.format("%d", Integer.valueOf((int) (f2 / 5280.0f))) : String.format("%.2f", Float.valueOf(f2 / 5280.0f));
        if (i == 0) {
            return formatString("MilesAway", org.telegram.messenger.beta.R.string.MilesAway, format2);
        }
        if (i == 1) {
            return formatString("MilesFromYou", org.telegram.messenger.beta.R.string.MilesFromYou, format2);
        }
        return formatString("MilesShort", org.telegram.messenger.beta.R.string.MilesShort, format2);
    }
}
