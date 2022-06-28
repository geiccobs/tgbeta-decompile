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
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.microsoft.appcenter.ingestion.models.properties.StringTypedProperty;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.xmlpull.v1.XmlPullParser;
/* loaded from: classes.dex */
public class LocaleController {
    static final int QUANTITY_FEW = 8;
    static final int QUANTITY_MANY = 16;
    static final int QUANTITY_ONE = 2;
    static final int QUANTITY_OTHER = 0;
    static final int QUANTITY_TWO = 4;
    static final int QUANTITY_ZERO = 1;
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
    public static boolean isRTL = false;
    public static int nameDisplayOrder = 1;
    public static boolean is24HourFormat = false;
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

    /* loaded from: classes4.dex */
    public static abstract class PluralRules {
        abstract int quantityForNumber(int i);
    }

    /* loaded from: classes4.dex */
    public class TimeZoneChangedReceiver extends BroadcastReceiver {
        private TimeZoneChangedReceiver() {
            LocaleController.this = r1;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            ApplicationLoader.applicationHandler.post(new Runnable() { // from class: org.telegram.messenger.LocaleController$TimeZoneChangedReceiver$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    LocaleController.TimeZoneChangedReceiver.this.m335xb3806a48();
                }
            });
        }

        /* renamed from: lambda$onReceive$0$org-telegram-messenger-LocaleController$TimeZoneChangedReceiver */
        public /* synthetic */ void m335xb3806a48() {
            if (!LocaleController.this.formatterDayMonth.getTimeZone().equals(TimeZone.getDefault())) {
                LocaleController.getInstance().recreateFormatters();
            }
        }
    }

    /* loaded from: classes4.dex */
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
            String langCode = this.baseLangCode;
            if (langCode == null) {
                langCode = "";
            }
            if (TextUtils.isEmpty(this.pluralLangCode)) {
                String str = this.shortName;
            } else {
                String str2 = this.pluralLangCode;
            }
            return this.name + "|" + this.nameEnglish + "|" + this.shortName + "|" + this.pathToFile + "|" + this.version + "|" + langCode + "|" + this.pluralLangCode + "|" + (this.isRtl ? 1 : 0) + "|" + this.baseVersion + "|" + this.serverIndex;
        }

        public static LocaleInfo createWithString(String string) {
            if (string == null || string.length() == 0) {
                return null;
            }
            String[] args = string.split("\\|");
            LocaleInfo localeInfo = null;
            if (args.length >= 4) {
                localeInfo = new LocaleInfo();
                boolean z = false;
                localeInfo.name = args[0];
                localeInfo.nameEnglish = args[1];
                localeInfo.shortName = args[2].toLowerCase();
                localeInfo.pathToFile = args[3];
                if (args.length >= 5) {
                    localeInfo.version = Utilities.parseInt((CharSequence) args[4]).intValue();
                }
                localeInfo.baseLangCode = args.length >= 6 ? args[5] : "";
                localeInfo.pluralLangCode = args.length >= 7 ? args[6] : localeInfo.shortName;
                if (args.length >= 8) {
                    if (Utilities.parseInt((CharSequence) args[7]).intValue() == 1) {
                        z = true;
                    }
                    localeInfo.isRtl = z;
                }
                if (args.length >= 9) {
                    localeInfo.baseVersion = Utilities.parseInt((CharSequence) args[8]).intValue();
                }
                if (args.length >= 10) {
                    localeInfo.serverIndex = Utilities.parseInt((CharSequence) args[9]).intValue();
                } else {
                    localeInfo.serverIndex = Integer.MAX_VALUE;
                }
                if (!TextUtils.isEmpty(localeInfo.baseLangCode)) {
                    localeInfo.baseLangCode = localeInfo.baseLangCode.replace("-", "_");
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
        LocaleController localInstance = Instance;
        if (localInstance == null) {
            synchronized (LocaleController.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    LocaleController localeController = new LocaleController();
                    localInstance = localeController;
                    Instance = localeController;
                }
            }
        }
        return localInstance;
    }

    public LocaleController() {
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
        addRules(new String[]{TtmlNode.TAG_BR}, new PluralRules_Breton());
        addRules(new String[]{"lag"}, new PluralRules_Langi());
        addRules(new String[]{"shi"}, new PluralRules_Tachelhit());
        addRules(new String[]{"mt"}, new PluralRules_Maltese());
        addRules(new String[]{"ga", "se", "sma", "smi", "smj", "smn", "sms"}, new PluralRules_Two());
        addRules(new String[]{"ak", "am", "bh", "fil", "tl", "guw", "hi", "ln", "mg", "nso", "ti", "wa"}, new PluralRules_Zero());
        addRules(new String[]{"az", "bm", "fa", "ig", "hu", "ja", "kde", "kea", "ko", "my", "ses", "sg", "to", "tr", "vi", "wo", "yo", "zh", "bo", "dz", "id", "jv", "jw", "ka", "km", "kn", "ms", "th", "in"}, new PluralRules_None());
        LocaleInfo localeInfo = new LocaleInfo();
        localeInfo.name = "English";
        localeInfo.nameEnglish = "English";
        localeInfo.pluralLangCode = "en";
        localeInfo.shortName = "en";
        localeInfo.pathToFile = null;
        localeInfo.builtIn = true;
        this.languages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        LocaleInfo localeInfo2 = new LocaleInfo();
        localeInfo2.name = "Italiano";
        localeInfo2.nameEnglish = "Italian";
        localeInfo2.pluralLangCode = "it";
        localeInfo2.shortName = "it";
        localeInfo2.pathToFile = null;
        localeInfo2.builtIn = true;
        this.languages.add(localeInfo2);
        this.languagesDict.put(localeInfo2.shortName, localeInfo2);
        LocaleInfo localeInfo3 = new LocaleInfo();
        localeInfo3.name = "Español";
        localeInfo3.nameEnglish = "Spanish";
        localeInfo3.pluralLangCode = "es";
        localeInfo3.shortName = "es";
        localeInfo3.builtIn = true;
        this.languages.add(localeInfo3);
        this.languagesDict.put(localeInfo3.shortName, localeInfo3);
        LocaleInfo localeInfo4 = new LocaleInfo();
        localeInfo4.name = "Deutsch";
        localeInfo4.nameEnglish = "German";
        localeInfo4.pluralLangCode = "de";
        localeInfo4.shortName = "de";
        localeInfo4.pathToFile = null;
        localeInfo4.builtIn = true;
        this.languages.add(localeInfo4);
        this.languagesDict.put(localeInfo4.shortName, localeInfo4);
        LocaleInfo localeInfo5 = new LocaleInfo();
        localeInfo5.name = "Nederlands";
        localeInfo5.nameEnglish = "Dutch";
        localeInfo5.pluralLangCode = "nl";
        localeInfo5.shortName = "nl";
        localeInfo5.pathToFile = null;
        localeInfo5.builtIn = true;
        this.languages.add(localeInfo5);
        this.languagesDict.put(localeInfo5.shortName, localeInfo5);
        LocaleInfo localeInfo6 = new LocaleInfo();
        localeInfo6.name = "العربية";
        localeInfo6.nameEnglish = "Arabic";
        localeInfo6.pluralLangCode = "ar";
        localeInfo6.shortName = "ar";
        localeInfo6.pathToFile = null;
        localeInfo6.builtIn = true;
        localeInfo6.isRtl = true;
        this.languages.add(localeInfo6);
        this.languagesDict.put(localeInfo6.shortName, localeInfo6);
        LocaleInfo localeInfo7 = new LocaleInfo();
        localeInfo7.name = "Português (Brasil)";
        localeInfo7.nameEnglish = "Portuguese (Brazil)";
        localeInfo7.pluralLangCode = "pt_br";
        localeInfo7.shortName = "pt_br";
        localeInfo7.pathToFile = null;
        localeInfo7.builtIn = true;
        this.languages.add(localeInfo7);
        this.languagesDict.put(localeInfo7.shortName, localeInfo7);
        LocaleInfo localeInfo8 = new LocaleInfo();
        localeInfo8.name = "한국어";
        localeInfo8.nameEnglish = "Korean";
        localeInfo8.pluralLangCode = "ko";
        localeInfo8.shortName = "ko";
        localeInfo8.pathToFile = null;
        localeInfo8.builtIn = true;
        this.languages.add(localeInfo8);
        this.languagesDict.put(localeInfo8.shortName, localeInfo8);
        loadOtherLanguages();
        if (this.remoteLanguages.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    LocaleController.this.m332lambda$new$0$orgtelegrammessengerLocaleController();
                }
            });
        }
        for (int a = 0; a < this.otherLanguages.size(); a++) {
            LocaleInfo locale = this.otherLanguages.get(a);
            this.languages.add(locale);
            this.languagesDict.put(locale.getKey(), locale);
        }
        for (int a2 = 0; a2 < this.remoteLanguages.size(); a2++) {
            LocaleInfo locale2 = this.remoteLanguages.get(a2);
            LocaleInfo existingLocale = getLanguageFromDict(locale2.getKey());
            if (existingLocale != null) {
                existingLocale.pathToFile = locale2.pathToFile;
                existingLocale.version = locale2.version;
                existingLocale.baseVersion = locale2.baseVersion;
                existingLocale.serverIndex = locale2.serverIndex;
                this.remoteLanguages.set(a2, existingLocale);
            } else {
                this.languages.add(locale2);
                this.languagesDict.put(locale2.getKey(), locale2);
            }
        }
        for (int a3 = 0; a3 < this.unofficialLanguages.size(); a3++) {
            LocaleInfo locale3 = this.unofficialLanguages.get(a3);
            LocaleInfo existingLocale2 = getLanguageFromDict(locale3.getKey());
            if (existingLocale2 != null) {
                existingLocale2.pathToFile = locale3.pathToFile;
                existingLocale2.version = locale3.version;
                existingLocale2.baseVersion = locale3.baseVersion;
                existingLocale2.serverIndex = locale3.serverIndex;
                this.unofficialLanguages.set(a3, existingLocale2);
            } else {
                this.languagesDict.put(locale3.getKey(), locale3);
            }
        }
        this.systemDefaultLocale = Locale.getDefault();
        is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
        LocaleInfo currentInfo = null;
        boolean override = false;
        try {
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            String lang = preferences.getString("language", null);
            if (lang != null && (currentInfo = getLanguageFromDict(lang)) != null) {
                override = true;
            }
            if (currentInfo == null && this.systemDefaultLocale.getLanguage() != null) {
                currentInfo = getLanguageFromDict(this.systemDefaultLocale.getLanguage());
            }
            if (currentInfo == null && (currentInfo = getLanguageFromDict(getLocaleString(this.systemDefaultLocale))) == null) {
                currentInfo = getLanguageFromDict("en");
            }
            applyLanguage(currentInfo, override, true, UserConfig.selectedAccount);
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            IntentFilter timezoneFilter = new IntentFilter("android.intent.action.TIMEZONE_CHANGED");
            ApplicationLoader.applicationContext.registerReceiver(new TimeZoneChangedReceiver(), timezoneFilter);
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                LocaleController.this.m333lambda$new$1$orgtelegrammessengerLocaleController();
            }
        });
    }

    /* renamed from: lambda$new$0$org-telegram-messenger-LocaleController */
    public /* synthetic */ void m332lambda$new$0$orgtelegrammessengerLocaleController() {
        loadRemoteLanguages(UserConfig.selectedAccount);
    }

    /* renamed from: lambda$new$1$org-telegram-messenger-LocaleController */
    public /* synthetic */ void m333lambda$new$1$orgtelegrammessengerLocaleController() {
        this.currentSystemLocale = getSystemLocaleStringIso639();
    }

    public static String getLanguageFlag(String countryCode) {
        if (countryCode.length() != 2 || countryCode.equals("YL")) {
            return null;
        }
        if (countryCode.equals("XG")) {
            return "🛰";
        }
        if (countryCode.equals("XV")) {
            return "🌍";
        }
        char[] chars = countryCode.toCharArray();
        char[] emoji = {CharacterCompat.highSurrogate(127397), CharacterCompat.lowSurrogate(chars[0] + 61861), CharacterCompat.highSurrogate(127397), CharacterCompat.lowSurrogate(chars[1] + 61861)};
        return new String(emoji);
    }

    public LocaleInfo getLanguageFromDict(String key) {
        if (key == null) {
            return null;
        }
        return this.languagesDict.get(key.toLowerCase().replace("-", "_"));
    }

    public LocaleInfo getBuiltinLanguageByPlural(String plural) {
        Collection<LocaleInfo> values = this.languagesDict.values();
        for (LocaleInfo l : values) {
            if (l.pathToFile != null && l.pathToFile.equals("remote") && l.pluralLangCode != null && l.pluralLangCode.equals(plural)) {
                return l;
            }
        }
        return null;
    }

    private void addRules(String[] languages, PluralRules rules) {
        for (String language : languages) {
            this.allRules.put(language, rules);
        }
    }

    private String stringForQuantity(int quantity) {
        switch (quantity) {
            case 1:
                return "zero";
            case 2:
                return "one";
            case 4:
                return "two";
            case 8:
                return "few";
            case 16:
                return "many";
            default:
                return "other";
        }
    }

    public Locale getSystemDefaultLocale() {
        return this.systemDefaultLocale;
    }

    public boolean isCurrentLocalLocale() {
        return this.currentLocaleInfo.isLocal();
    }

    public void reloadCurrentRemoteLocale(int currentAccount, String langCode, boolean force) {
        if (langCode != null) {
            langCode = langCode.replace("-", "_");
        }
        if (langCode != null) {
            LocaleInfo localeInfo = this.currentLocaleInfo;
            if (localeInfo == null) {
                return;
            }
            if (!langCode.equals(localeInfo.shortName) && !langCode.equals(this.currentLocaleInfo.baseLangCode)) {
                return;
            }
        }
        applyRemoteLanguage(this.currentLocaleInfo, langCode, force, currentAccount);
    }

    public void checkUpdateForCurrentRemoteLocale(int currentAccount, int version, int baseVersion) {
        LocaleInfo localeInfo = this.currentLocaleInfo;
        if (localeInfo != null) {
            if (!localeInfo.isRemote() && !this.currentLocaleInfo.isUnofficial()) {
                return;
            }
            if (this.currentLocaleInfo.hasBaseLang() && this.currentLocaleInfo.baseVersion < baseVersion) {
                LocaleInfo localeInfo2 = this.currentLocaleInfo;
                applyRemoteLanguage(localeInfo2, localeInfo2.baseLangCode, false, currentAccount);
            }
            if (this.currentLocaleInfo.version < version) {
                LocaleInfo localeInfo3 = this.currentLocaleInfo;
                applyRemoteLanguage(localeInfo3, localeInfo3.shortName, false, currentAccount);
            }
        }
    }

    private String getLocaleString(Locale locale) {
        if (locale == null) {
            return "en";
        }
        String languageCode = locale.getLanguage();
        String countryCode = locale.getCountry();
        String variantCode = locale.getVariant();
        if (languageCode.length() == 0 && countryCode.length() == 0) {
            return "en";
        }
        StringBuilder result = new StringBuilder(11);
        result.append(languageCode);
        if (countryCode.length() > 0 || variantCode.length() > 0) {
            result.append('_');
        }
        result.append(countryCode);
        if (variantCode.length() > 0) {
            result.append('_');
        }
        result.append(variantCode);
        return result.toString();
    }

    public static String getSystemLocaleStringIso639() {
        Locale locale = getInstance().getSystemDefaultLocale();
        if (locale == null) {
            return "en";
        }
        String languageCode = locale.getLanguage();
        String countryCode = locale.getCountry();
        String variantCode = locale.getVariant();
        if (languageCode.length() == 0 && countryCode.length() == 0) {
            return "en";
        }
        StringBuilder result = new StringBuilder(11);
        result.append(languageCode);
        if (countryCode.length() > 0 || variantCode.length() > 0) {
            result.append('-');
        }
        result.append(countryCode);
        if (variantCode.length() > 0) {
            result.append('_');
        }
        result.append(variantCode);
        return result.toString();
    }

    public static String getLocaleStringIso639() {
        LocaleInfo info = getInstance().currentLocaleInfo;
        if (info != null) {
            return info.getLangCode();
        }
        Locale locale = getInstance().currentLocale;
        if (locale == null) {
            return "en";
        }
        String languageCode = locale.getLanguage();
        String countryCode = locale.getCountry();
        String variantCode = locale.getVariant();
        if (languageCode.length() == 0 && countryCode.length() == 0) {
            return "en";
        }
        StringBuilder result = new StringBuilder(11);
        result.append(languageCode);
        if (countryCode.length() > 0 || variantCode.length() > 0) {
            result.append('-');
        }
        result.append(countryCode);
        if (variantCode.length() > 0) {
            result.append('_');
        }
        result.append(variantCode);
        return result.toString();
    }

    public static String getLocaleAlias(String code) {
        if (code == null) {
            return null;
        }
        char c = 65535;
        switch (code.hashCode()) {
            case 3325:
                if (code.equals("he")) {
                    c = 7;
                    break;
                }
                break;
            case 3355:
                if (code.equals("id")) {
                    c = 6;
                    break;
                }
                break;
            case 3365:
                if (code.equals("in")) {
                    c = 0;
                    break;
                }
                break;
            case 3374:
                if (code.equals("iw")) {
                    c = 1;
                    break;
                }
                break;
            case 3391:
                if (code.equals("ji")) {
                    c = 5;
                    break;
                }
                break;
            case 3404:
                if (code.equals("jv")) {
                    c = '\b';
                    break;
                }
                break;
            case 3405:
                if (code.equals("jw")) {
                    c = 2;
                    break;
                }
                break;
            case 3508:
                if (code.equals("nb")) {
                    c = '\t';
                    break;
                }
                break;
            case 3521:
                if (code.equals("no")) {
                    c = 3;
                    break;
                }
                break;
            case 3704:
                if (code.equals("tl")) {
                    c = 4;
                    break;
                }
                break;
            case 3856:
                if (code.equals("yi")) {
                    c = 11;
                    break;
                }
                break;
            case 101385:
                if (code.equals("fil")) {
                    c = '\n';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return "id";
            case 1:
                return "he";
            case 2:
                return "jv";
            case 3:
                return "nb";
            case 4:
                return "fil";
            case 5:
                return "yi";
            case 6:
                return "in";
            case 7:
                return "iw";
            case '\b':
                return "jw";
            case '\t':
                return "no";
            case '\n':
                return "tl";
            case 11:
                return "ji";
            default:
                return null;
        }
    }

    public boolean applyLanguageFile(File file, int currentAccount) {
        Exception e;
        LocaleInfo localeInfo;
        try {
            HashMap<String, String> stringMap = getLocaleFileStrings(file);
            String languageName = stringMap.get("LanguageName");
            String languageNameInEnglish = stringMap.get("LanguageNameInEnglish");
            String languageCode = stringMap.get("LanguageCode");
            if (languageName != null && languageName.length() > 0 && languageNameInEnglish != null) {
                if (languageNameInEnglish.length() > 0 && languageCode != null) {
                    if (languageCode.length() > 0 && !languageName.contains("&") && !languageName.contains("|") && !languageNameInEnglish.contains("&") && !languageNameInEnglish.contains("|")) {
                        if (!languageCode.contains("&") && !languageCode.contains("|") && !languageCode.contains("/")) {
                            if (!languageCode.contains("\\")) {
                                File finalFile = new File(ApplicationLoader.getFilesDirFixed(), languageCode + ".xml");
                                try {
                                    if (!AndroidUtilities.copyFile(file, finalFile)) {
                                        return false;
                                    }
                                    String key = "local_" + languageCode.toLowerCase();
                                    LocaleInfo localeInfo2 = getLanguageFromDict(key);
                                    if (localeInfo2 != null) {
                                        localeInfo = localeInfo2;
                                    } else {
                                        LocaleInfo localeInfo3 = new LocaleInfo();
                                        localeInfo3.name = languageName;
                                        localeInfo3.nameEnglish = languageNameInEnglish;
                                        localeInfo3.shortName = languageCode.toLowerCase();
                                        localeInfo3.pluralLangCode = localeInfo3.shortName;
                                        localeInfo3.pathToFile = finalFile.getAbsolutePath();
                                        this.languages.add(localeInfo3);
                                        this.languagesDict.put(localeInfo3.getKey(), localeInfo3);
                                        this.otherLanguages.add(localeInfo3);
                                        saveOtherLanguages();
                                        localeInfo = localeInfo3;
                                    }
                                    this.localeValues = stringMap;
                                    applyLanguage(localeInfo, true, false, true, false, currentAccount);
                                    return true;
                                } catch (Exception e2) {
                                    e = e2;
                                    FileLog.e(e);
                                    return false;
                                }
                            }
                        }
                        return false;
                    }
                    return false;
                }
            }
        } catch (Exception e3) {
            e = e3;
        }
        return false;
    }

    private void saveOtherLanguages() {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0);
        SharedPreferences.Editor editor = preferences.edit();
        StringBuilder stringBuilder = new StringBuilder();
        for (int a = 0; a < this.otherLanguages.size(); a++) {
            LocaleInfo localeInfo = this.otherLanguages.get(a);
            String loc = localeInfo.getSaveString();
            if (loc != null) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("&");
                }
                stringBuilder.append(loc);
            }
        }
        editor.putString("locales", stringBuilder.toString());
        stringBuilder.setLength(0);
        for (int a2 = 0; a2 < this.remoteLanguages.size(); a2++) {
            LocaleInfo localeInfo2 = this.remoteLanguages.get(a2);
            String loc2 = localeInfo2.getSaveString();
            if (loc2 != null) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("&");
                }
                stringBuilder.append(loc2);
            }
        }
        editor.putString("remote", stringBuilder.toString());
        stringBuilder.setLength(0);
        for (int a3 = 0; a3 < this.unofficialLanguages.size(); a3++) {
            LocaleInfo localeInfo3 = this.unofficialLanguages.get(a3);
            String loc3 = localeInfo3.getSaveString();
            if (loc3 != null) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("&");
                }
                stringBuilder.append(loc3);
            }
        }
        editor.putString("unofficial", stringBuilder.toString());
        editor.commit();
    }

    public boolean deleteLanguage(LocaleInfo localeInfo, int currentAccount) {
        if (localeInfo.pathToFile == null || (localeInfo.isRemote() && localeInfo.serverIndex != Integer.MAX_VALUE)) {
            return false;
        }
        if (this.currentLocaleInfo == localeInfo) {
            LocaleInfo info = null;
            if (this.systemDefaultLocale.getLanguage() != null) {
                info = getLanguageFromDict(this.systemDefaultLocale.getLanguage());
            }
            if (info == null) {
                info = getLanguageFromDict(getLocaleString(this.systemDefaultLocale));
            }
            if (info == null) {
                info = getLanguageFromDict("en");
            }
            applyLanguage(info, true, false, currentAccount);
        }
        this.unofficialLanguages.remove(localeInfo);
        this.remoteLanguages.remove(localeInfo);
        this.remoteLanguagesDict.remove(localeInfo.getKey());
        this.otherLanguages.remove(localeInfo);
        this.languages.remove(localeInfo);
        this.languagesDict.remove(localeInfo.getKey());
        File file = new File(localeInfo.pathToFile);
        file.delete();
        saveOtherLanguages();
        return true;
    }

    private void loadOtherLanguages() {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0);
        String locales = preferences.getString("locales", null);
        if (!TextUtils.isEmpty(locales)) {
            String[] localesArr = locales.split("&");
            for (String locale : localesArr) {
                LocaleInfo localeInfo = LocaleInfo.createWithString(locale);
                if (localeInfo != null) {
                    this.otherLanguages.add(localeInfo);
                }
            }
        }
        String locales2 = preferences.getString("remote", null);
        if (!TextUtils.isEmpty(locales2)) {
            String[] localesArr2 = locales2.split("&");
            for (String locale2 : localesArr2) {
                LocaleInfo localeInfo2 = LocaleInfo.createWithString(locale2);
                localeInfo2.shortName = localeInfo2.shortName.replace("-", "_");
                if (!this.remoteLanguagesDict.containsKey(localeInfo2.getKey())) {
                    this.remoteLanguages.add(localeInfo2);
                    this.remoteLanguagesDict.put(localeInfo2.getKey(), localeInfo2);
                }
            }
        }
        String locales3 = preferences.getString("unofficial", null);
        if (!TextUtils.isEmpty(locales3)) {
            String[] localesArr3 = locales3.split("&");
            for (String locale3 : localesArr3) {
                LocaleInfo localeInfo3 = LocaleInfo.createWithString(locale3);
                if (localeInfo3 != null) {
                    localeInfo3.shortName = localeInfo3.shortName.replace("-", "_");
                    this.unofficialLanguages.add(localeInfo3);
                }
            }
        }
    }

    private HashMap<String, String> getLocaleFileStrings(File file) {
        return getLocaleFileStrings(file, false);
    }

    private HashMap<String, String> getLocaleFileStrings(File file, boolean preserveEscapes) {
        FileInputStream stream = null;
        this.reloadLastFile = false;
        try {
            try {
                if (!file.exists()) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    if (0 != 0) {
                        try {
                            stream.close();
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                    return hashMap;
                }
                HashMap<String, String> stringMap = new HashMap<>();
                XmlPullParser parser = Xml.newPullParser();
                FileInputStream stream2 = new FileInputStream(file);
                parser.setInput(stream2, "UTF-8");
                String name = null;
                String value = null;
                String attrName = null;
                for (int eventType = parser.getEventType(); eventType != 1; eventType = parser.next()) {
                    if (eventType == 2) {
                        name = parser.getName();
                        int c = parser.getAttributeCount();
                        if (c > 0) {
                            attrName = parser.getAttributeValue(0);
                        }
                    } else if (eventType == 4) {
                        if (attrName != null && (value = parser.getText()) != null) {
                            String value2 = value.trim();
                            if (preserveEscapes) {
                                value = value2.replace("<", "&lt;").replace(">", "&gt;").replace("'", "\\'").replace("& ", "&amp; ");
                            } else {
                                String old = value2.replace("\\n", "\n").replace("\\", "");
                                value = old.replace("&lt;", "<");
                                if (!this.reloadLastFile && !value.equals(old)) {
                                    this.reloadLastFile = true;
                                }
                            }
                        }
                    } else if (eventType == 3) {
                        value = null;
                        attrName = null;
                        name = null;
                    }
                    if (name != null && name.equals(StringTypedProperty.TYPE) && value != null && attrName != null && value.length() != 0 && attrName.length() != 0) {
                        stringMap.put(attrName, value);
                        name = null;
                        value = null;
                        attrName = null;
                    }
                }
                try {
                    stream2.close();
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
                return stringMap;
            } catch (Throwable th) {
                if (0 != 0) {
                    try {
                        stream.close();
                    } catch (Exception e3) {
                        FileLog.e(e3);
                    }
                }
                throw th;
            }
        } catch (Exception e4) {
            FileLog.e(e4);
            this.reloadLastFile = true;
            if (0 != 0) {
                try {
                    stream.close();
                } catch (Exception e5) {
                    FileLog.e(e5);
                }
            }
            return new HashMap<>();
        }
    }

    public void applyLanguage(LocaleInfo localeInfo, boolean override, boolean init, int currentAccount) {
        applyLanguage(localeInfo, override, init, false, false, currentAccount);
    }

    public void applyLanguage(final LocaleInfo localeInfo, boolean override, boolean init, boolean fromFile, final boolean force, final int currentAccount) {
        boolean z;
        String[] args;
        Locale newLocale;
        if (localeInfo == null) {
            return;
        }
        boolean hasBase = localeInfo.hasBaseLang();
        File pathToFile = localeInfo.getPathToFile();
        File pathToBaseFile = localeInfo.getPathToBaseFile();
        String str = localeInfo.shortName;
        if (!init) {
            ConnectionsManager.setLangCode(localeInfo.getLangCode());
        }
        LocaleInfo existingInfo = getLanguageFromDict(localeInfo.getKey());
        if (existingInfo == null) {
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
        boolean isLoadingRemote = false;
        if ((localeInfo.isRemote() || localeInfo.isUnofficial()) && (force || !pathToFile.exists() || (hasBase && !pathToBaseFile.exists()))) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("reload locale because one of file doesn't exist" + pathToFile + " " + pathToBaseFile);
            }
            isLoadingRemote = true;
            if (init) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda10
                    @Override // java.lang.Runnable
                    public final void run() {
                        LocaleController.this.m320lambda$applyLanguage$2$orgtelegrammessengerLocaleController(localeInfo, currentAccount);
                    }
                });
            } else {
                applyRemoteLanguage(localeInfo, null, true, currentAccount);
            }
        }
        boolean isLoadingRemote2 = isLoadingRemote;
        try {
            if (!TextUtils.isEmpty(localeInfo.pluralLangCode)) {
                args = localeInfo.pluralLangCode.split("_");
            } else if (!TextUtils.isEmpty(localeInfo.baseLangCode)) {
                args = localeInfo.baseLangCode.split("_");
            } else {
                args = localeInfo.shortName.split("_");
            }
            if (args.length == 1) {
                newLocale = new Locale(args[0]);
            } else {
                newLocale = new Locale(args[0], args[1]);
            }
            if (override) {
                this.languageOverride = localeInfo.shortName;
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("language", localeInfo.getKey());
                editor.commit();
            }
            if (pathToFile == null) {
                this.localeValues.clear();
            } else if (!fromFile) {
                HashMap<String, String> localeFileStrings = getLocaleFileStrings(hasBase ? localeInfo.getPathToBaseFile() : localeInfo.getPathToFile());
                this.localeValues = localeFileStrings;
                if (hasBase) {
                    localeFileStrings.putAll(getLocaleFileStrings(localeInfo.getPathToFile()));
                }
            }
            this.currentLocale = newLocale;
            this.currentLocaleInfo = localeInfo;
            if (!TextUtils.isEmpty(localeInfo.pluralLangCode)) {
                this.currentPluralRules = this.allRules.get(this.currentLocaleInfo.pluralLangCode);
            }
            if (this.currentPluralRules == null) {
                PluralRules pluralRules = this.allRules.get(args[0]);
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
            Configuration config = new Configuration();
            config.locale = this.currentLocale;
            ApplicationLoader.applicationContext.getResources().updateConfiguration(config, ApplicationLoader.applicationContext.getResources().getDisplayMetrics());
            this.changingConfiguration = false;
            if (this.reloadLastFile) {
                if (init) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda9
                        @Override // java.lang.Runnable
                        public final void run() {
                            LocaleController.this.m321lambda$applyLanguage$3$orgtelegrammessengerLocaleController(currentAccount, force);
                        }
                    });
                } else {
                    reloadCurrentRemoteLocale(currentAccount, null, force);
                }
                this.reloadLastFile = false;
            }
            if (!isLoadingRemote2) {
                if (init) {
                    AndroidUtilities.runOnUIThread(LocaleController$$ExternalSyntheticLambda1.INSTANCE);
                } else {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.reloadInterface, new Object[0]);
                }
            }
            z = false;
        } catch (Exception e) {
            FileLog.e(e);
            z = false;
            this.changingConfiguration = false;
        }
        recreateFormatters();
        if (force) {
            MediaDataController.getInstance(currentAccount).loadAttachMenuBots(z, true);
        }
    }

    /* renamed from: lambda$applyLanguage$2$org-telegram-messenger-LocaleController */
    public /* synthetic */ void m320lambda$applyLanguage$2$orgtelegrammessengerLocaleController(LocaleInfo localeInfo, int currentAccount) {
        applyRemoteLanguage(localeInfo, null, true, currentAccount);
    }

    /* renamed from: lambda$applyLanguage$3$org-telegram-messenger-LocaleController */
    public /* synthetic */ void m321lambda$applyLanguage$3$orgtelegrammessengerLocaleController(int currentAccount, boolean force) {
        reloadCurrentRemoteLocale(currentAccount, null, force);
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

    private String getStringInternal(String key, int res) {
        return getStringInternal(key, null, res);
    }

    private String getStringInternal(String key, String fallback, int res) {
        String value = BuildVars.USE_CLOUD_STRINGS ? this.localeValues.get(key) : null;
        if (value == null) {
            if (BuildVars.USE_CLOUD_STRINGS && fallback != null) {
                value = this.localeValues.get(fallback);
            }
            if (value == null) {
                try {
                    value = ApplicationLoader.applicationContext.getString(res);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
        if (value == null) {
            return "LOC_ERR:" + key;
        }
        return value;
    }

    public static String getServerString(String key) {
        int resourceId;
        String value = getInstance().localeValues.get(key);
        if (value == null && (resourceId = ApplicationLoader.applicationContext.getResources().getIdentifier(key, StringTypedProperty.TYPE, ApplicationLoader.applicationContext.getPackageName())) != 0) {
            return ApplicationLoader.applicationContext.getString(resourceId);
        }
        return value;
    }

    public static String getString(int res) {
        String key = resourcesCacheMap.get(Integer.valueOf(res));
        if (key == null) {
            HashMap<Integer, String> hashMap = resourcesCacheMap;
            Integer valueOf = Integer.valueOf(res);
            String resourceEntryName = ApplicationLoader.applicationContext.getResources().getResourceEntryName(res);
            key = resourceEntryName;
            hashMap.put(valueOf, resourceEntryName);
        }
        return getString(key, res);
    }

    public static String getString(String key, int res) {
        return getInstance().getStringInternal(key, res);
    }

    public static String getString(String key, String fallback, int res) {
        return getInstance().getStringInternal(key, fallback, res);
    }

    public static String getString(String key) {
        if (TextUtils.isEmpty(key)) {
            return "LOC_ERR:" + key;
        }
        int resourceId = ApplicationLoader.applicationContext.getResources().getIdentifier(key, StringTypedProperty.TYPE, ApplicationLoader.applicationContext.getPackageName());
        if (resourceId != 0) {
            return getString(key, resourceId);
        }
        return getServerString(key);
    }

    public static String getPluralString(String key, int plural) {
        if (key == null || key.length() == 0 || getInstance().currentPluralRules == null) {
            return "LOC_ERR:" + key;
        }
        String param = key + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(plural));
        int resourceId = ApplicationLoader.applicationContext.getResources().getIdentifier(param, StringTypedProperty.TYPE, ApplicationLoader.applicationContext.getPackageName());
        return getString(param, key + "_other", resourceId);
    }

    public static String formatPluralString(String key, int plural, Object... args) {
        if (key == null || key.length() == 0 || getInstance().currentPluralRules == null) {
            return "LOC_ERR:" + key;
        }
        String param = key + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(plural));
        int resourceId = ApplicationLoader.applicationContext.getResources().getIdentifier(param, StringTypedProperty.TYPE, ApplicationLoader.applicationContext.getPackageName());
        Object[] argsWithPlural = new Object[args.length + 1];
        argsWithPlural[0] = Integer.valueOf(plural);
        System.arraycopy(args, 0, argsWithPlural, 1, args.length);
        return formatString(param, key + "_other", resourceId, argsWithPlural);
    }

    public static String formatPluralStringComma(String key, int plural) {
        if (key != null) {
            try {
                if (key.length() != 0 && getInstance().currentPluralRules != null) {
                    String param = key + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(plural));
                    StringBuilder stringBuilder = new StringBuilder(String.format(Locale.US, "%d", Integer.valueOf(plural)));
                    for (int a = stringBuilder.length() - 3; a > 0; a -= 3) {
                        stringBuilder.insert(a, ',');
                    }
                    String str = null;
                    String value = BuildVars.USE_CLOUD_STRINGS ? getInstance().localeValues.get(param) : null;
                    if (value == null) {
                        if (BuildVars.USE_CLOUD_STRINGS) {
                            str = getInstance().localeValues.get(key + "_other");
                        }
                        value = str;
                    }
                    if (value == null) {
                        int resourceId = ApplicationLoader.applicationContext.getResources().getIdentifier(param, StringTypedProperty.TYPE, ApplicationLoader.applicationContext.getPackageName());
                        value = ApplicationLoader.applicationContext.getString(resourceId);
                    }
                    String value2 = value.replace("%1$d", "%1$s");
                    return getInstance().currentLocale != null ? String.format(getInstance().currentLocale, value2, stringBuilder) : String.format(value2, stringBuilder);
                }
            } catch (Exception e) {
                FileLog.e(e);
                return "LOC_ERR: " + key;
            }
        }
        return "LOC_ERR:" + key;
    }

    public static String formatString(int res, Object... args) {
        String key = resourcesCacheMap.get(Integer.valueOf(res));
        if (key == null) {
            HashMap<Integer, String> hashMap = resourcesCacheMap;
            Integer valueOf = Integer.valueOf(res);
            String resourceEntryName = ApplicationLoader.applicationContext.getResources().getResourceEntryName(res);
            key = resourceEntryName;
            hashMap.put(valueOf, resourceEntryName);
        }
        return formatString(key, res, args);
    }

    public static String formatString(String key, int res, Object... args) {
        return formatString(key, null, res, args);
    }

    public static String formatString(String key, String fallback, int res, Object... args) {
        try {
            String value = BuildVars.USE_CLOUD_STRINGS ? getInstance().localeValues.get(key) : null;
            if (value == null) {
                if (BuildVars.USE_CLOUD_STRINGS && fallback != null) {
                    value = getInstance().localeValues.get(fallback);
                }
                if (value == null) {
                    value = ApplicationLoader.applicationContext.getString(res);
                }
            }
            if (getInstance().currentLocale != null) {
                return String.format(getInstance().currentLocale, value, args);
            }
            return String.format(value, args);
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR: " + key;
        }
    }

    public static String formatTTLString(int ttl) {
        if (ttl < 60) {
            return formatPluralString("Seconds", ttl, new Object[0]);
        }
        if (ttl < 3600) {
            return formatPluralString("Minutes", ttl / 60, new Object[0]);
        }
        if (ttl < 86400) {
            return formatPluralString("Hours", (ttl / 60) / 60, new Object[0]);
        }
        if (ttl < 604800) {
            return formatPluralString("Days", ((ttl / 60) / 60) / 24, new Object[0]);
        }
        if (ttl >= 2678400) {
            return formatPluralString("Months", (((ttl / 60) / 60) / 24) / 30, new Object[0]);
        }
        int days = ((ttl / 60) / 60) / 24;
        return ttl % 7 == 0 ? formatPluralString("Weeks", days / 7, new Object[0]) : String.format("%s %s", formatPluralString("Weeks", days / 7, new Object[0]), formatPluralString("Days", days % 7, new Object[0]));
    }

    public static String fixNumbers(CharSequence numbers) {
        StringBuilder builder = new StringBuilder(numbers);
        int N = builder.length();
        for (int c = 0; c < N; c++) {
            char ch = builder.charAt(c);
            if ((ch < '0' || ch > '9') && ch != '.' && ch != ',') {
                int a = 0;
                while (a < otherNumbers.length) {
                    int b = 0;
                    while (true) {
                        char[][] cArr = otherNumbers;
                        if (b < cArr[a].length) {
                            if (ch != cArr[a][b]) {
                                b++;
                            } else {
                                builder.setCharAt(c, defaultNumbers[b]);
                                a = otherNumbers.length;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    a++;
                }
            }
        }
        return builder.toString();
    }

    public String formatCurrencyString(long amount, String type) {
        return formatCurrencyString(amount, true, true, false, type);
    }

    public String formatCurrencyString(long amount, boolean fixAnything, boolean withExp, boolean editText, String type) {
        double doubleAmount;
        String customFormat;
        int idx;
        String type2 = type.toUpperCase();
        boolean discount = amount < 0;
        long amount2 = Math.abs(amount);
        Currency currency = Currency.getInstance(type2);
        char c = 65535;
        switch (type2.hashCode()) {
            case 65726:
                if (type2.equals("BHD")) {
                    c = 2;
                    break;
                }
                break;
            case 65759:
                if (type2.equals("BIF")) {
                    c = '\t';
                    break;
                }
                break;
            case 66267:
                if (type2.equals("BYR")) {
                    c = '\n';
                    break;
                }
                break;
            case 66813:
                if (type2.equals("CLF")) {
                    c = 0;
                    break;
                }
                break;
            case 66823:
                if (type2.equals("CLP")) {
                    c = 11;
                    break;
                }
                break;
            case 67122:
                if (type2.equals("CVE")) {
                    c = '\f';
                    break;
                }
                break;
            case 67712:
                if (type2.equals("DJF")) {
                    c = '\r';
                    break;
                }
                break;
            case 70719:
                if (type2.equals("GNF")) {
                    c = 14;
                    break;
                }
                break;
            case 72732:
                if (type2.equals("IQD")) {
                    c = 3;
                    break;
                }
                break;
            case 72777:
                if (type2.equals("IRR")) {
                    c = 1;
                    break;
                }
                break;
            case 72801:
                if (type2.equals("ISK")) {
                    c = 15;
                    break;
                }
                break;
            case 73631:
                if (type2.equals("JOD")) {
                    c = 4;
                    break;
                }
                break;
            case 73683:
                if (type2.equals("JPY")) {
                    c = 16;
                    break;
                }
                break;
            case 74532:
                if (type2.equals("KMF")) {
                    c = 17;
                    break;
                }
                break;
            case 74704:
                if (type2.equals("KRW")) {
                    c = 18;
                    break;
                }
                break;
            case 74840:
                if (type2.equals("KWD")) {
                    c = 5;
                    break;
                }
                break;
            case 75863:
                if (type2.equals("LYD")) {
                    c = 6;
                    break;
                }
                break;
            case 76263:
                if (type2.equals("MGA")) {
                    c = 19;
                    break;
                }
                break;
            case 76618:
                if (type2.equals("MRO")) {
                    c = 29;
                    break;
                }
                break;
            case 78388:
                if (type2.equals("OMR")) {
                    c = 7;
                    break;
                }
                break;
            case 79710:
                if (type2.equals("PYG")) {
                    c = 20;
                    break;
                }
                break;
            case 81569:
                if (type2.equals("RWF")) {
                    c = 21;
                    break;
                }
                break;
            case 83210:
                if (type2.equals("TND")) {
                    c = '\b';
                    break;
                }
                break;
            case 83974:
                if (type2.equals("UGX")) {
                    c = 22;
                    break;
                }
                break;
            case 84517:
                if (type2.equals("UYI")) {
                    c = 23;
                    break;
                }
                break;
            case 85132:
                if (type2.equals("VND")) {
                    c = 24;
                    break;
                }
                break;
            case 85367:
                if (type2.equals("VUV")) {
                    c = 25;
                    break;
                }
                break;
            case 86653:
                if (type2.equals("XAF")) {
                    c = 26;
                    break;
                }
                break;
            case 87087:
                if (type2.equals("XOF")) {
                    c = 27;
                    break;
                }
                break;
            case 87118:
                if (type2.equals("XPF")) {
                    c = 28;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                customFormat = " %.4f";
                double d = amount2;
                Double.isNaN(d);
                doubleAmount = d / 10000.0d;
                break;
            case 1:
                doubleAmount = ((float) amount2) / 100.0f;
                if (fixAnything && amount2 % 100 == 0) {
                    customFormat = " %.0f";
                    break;
                } else {
                    customFormat = " %.2f";
                    break;
                }
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case '\b':
                customFormat = " %.3f";
                double d2 = amount2;
                Double.isNaN(d2);
                doubleAmount = d2 / 1000.0d;
                break;
            case '\t':
            case '\n':
            case 11:
            case '\f':
            case '\r':
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
                customFormat = " %.0f";
                doubleAmount = amount2;
                break;
            case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                customFormat = " %.1f";
                double d3 = amount2;
                Double.isNaN(d3);
                doubleAmount = d3 / 10.0d;
                break;
            default:
                customFormat = " %.2f";
                double d4 = amount2;
                Double.isNaN(d4);
                doubleAmount = d4 / 100.0d;
                break;
        }
        if (!withExp) {
            customFormat = " %.0f";
        }
        String str = "-";
        if (currency != null) {
            Locale locale = this.currentLocale;
            if (locale == null) {
                locale = this.systemDefaultLocale;
            }
            NumberFormat format = NumberFormat.getCurrencyInstance(locale);
            format.setCurrency(currency);
            if (editText) {
                format.setGroupingUsed(false);
            }
            if (!withExp || (fixAnything && type2.equals("IRR"))) {
                format.setMaximumFractionDigits(0);
            }
            StringBuilder sb = new StringBuilder();
            if (!discount) {
                str = "";
            }
            sb.append(str);
            sb.append(format.format(doubleAmount));
            String result = sb.toString();
            int idx2 = result.indexOf(type2);
            if (idx2 >= 0 && (idx = idx2 + type2.length()) < result.length() && result.charAt(idx) != ' ') {
                return result.substring(0, idx) + " " + result.substring(idx);
            }
            return result;
        }
        StringBuilder sb2 = new StringBuilder();
        if (!discount) {
            str = "";
        }
        sb2.append(str);
        sb2.append(String.format(Locale.US, type2 + customFormat, Double.valueOf(doubleAmount)));
        return sb2.toString();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int getCurrencyExpDivider(String type) {
        char c;
        switch (type.hashCode()) {
            case 65726:
                if (type.equals("BHD")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 65759:
                if (type.equals("BIF")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 66267:
                if (type.equals("BYR")) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case 66813:
                if (type.equals("CLF")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 66823:
                if (type.equals("CLP")) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case 67122:
                if (type.equals("CVE")) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case 67712:
                if (type.equals("DJF")) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case 70719:
                if (type.equals("GNF")) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case 72732:
                if (type.equals("IQD")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 72801:
                if (type.equals("ISK")) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case 73631:
                if (type.equals("JOD")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 73683:
                if (type.equals("JPY")) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case 74532:
                if (type.equals("KMF")) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case 74704:
                if (type.equals("KRW")) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case 74840:
                if (type.equals("KWD")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 75863:
                if (type.equals("LYD")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 76263:
                if (type.equals("MGA")) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case 76618:
                if (type.equals("MRO")) {
                    c = 28;
                    break;
                }
                c = 65535;
                break;
            case 78388:
                if (type.equals("OMR")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 79710:
                if (type.equals("PYG")) {
                    c = 19;
                    break;
                }
                c = 65535;
                break;
            case 81569:
                if (type.equals("RWF")) {
                    c = 20;
                    break;
                }
                c = 65535;
                break;
            case 83210:
                if (type.equals("TND")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 83974:
                if (type.equals("UGX")) {
                    c = 21;
                    break;
                }
                c = 65535;
                break;
            case 84517:
                if (type.equals("UYI")) {
                    c = 22;
                    break;
                }
                c = 65535;
                break;
            case 85132:
                if (type.equals("VND")) {
                    c = 23;
                    break;
                }
                c = 65535;
                break;
            case 85367:
                if (type.equals("VUV")) {
                    c = 24;
                    break;
                }
                c = 65535;
                break;
            case 86653:
                if (type.equals("XAF")) {
                    c = 25;
                    break;
                }
                c = 65535;
                break;
            case 87087:
                if (type.equals("XOF")) {
                    c = 26;
                    break;
                }
                c = 65535;
                break;
            case 87118:
                if (type.equals("XPF")) {
                    c = 27;
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
                return 10000;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                return 1000;
            case '\b':
            case '\t':
            case '\n':
            case 11:
            case '\f':
            case '\r':
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
                return 1;
            case 28:
                return 10;
            default:
                return 100;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public String formatCurrencyDecimalString(long amount, String type, boolean inludeType) {
        char c;
        double doubleAmount;
        String customFormat;
        String str;
        String type2 = type.toUpperCase();
        long amount2 = Math.abs(amount);
        switch (type2.hashCode()) {
            case 65726:
                if (type2.equals("BHD")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 65759:
                if (type2.equals("BIF")) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case 66267:
                if (type2.equals("BYR")) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case 66813:
                if (type2.equals("CLF")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 66823:
                if (type2.equals("CLP")) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case 67122:
                if (type2.equals("CVE")) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case 67712:
                if (type2.equals("DJF")) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case 70719:
                if (type2.equals("GNF")) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case 72732:
                if (type2.equals("IQD")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 72777:
                if (type2.equals("IRR")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 72801:
                if (type2.equals("ISK")) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case 73631:
                if (type2.equals("JOD")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 73683:
                if (type2.equals("JPY")) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case 74532:
                if (type2.equals("KMF")) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case 74704:
                if (type2.equals("KRW")) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case 74840:
                if (type2.equals("KWD")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 75863:
                if (type2.equals("LYD")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 76263:
                if (type2.equals("MGA")) {
                    c = 19;
                    break;
                }
                c = 65535;
                break;
            case 76618:
                if (type2.equals("MRO")) {
                    c = 29;
                    break;
                }
                c = 65535;
                break;
            case 78388:
                if (type2.equals("OMR")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 79710:
                if (type2.equals("PYG")) {
                    c = 20;
                    break;
                }
                c = 65535;
                break;
            case 81569:
                if (type2.equals("RWF")) {
                    c = 21;
                    break;
                }
                c = 65535;
                break;
            case 83210:
                if (type2.equals("TND")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 83974:
                if (type2.equals("UGX")) {
                    c = 22;
                    break;
                }
                c = 65535;
                break;
            case 84517:
                if (type2.equals("UYI")) {
                    c = 23;
                    break;
                }
                c = 65535;
                break;
            case 85132:
                if (type2.equals("VND")) {
                    c = 24;
                    break;
                }
                c = 65535;
                break;
            case 85367:
                if (type2.equals("VUV")) {
                    c = 25;
                    break;
                }
                c = 65535;
                break;
            case 86653:
                if (type2.equals("XAF")) {
                    c = 26;
                    break;
                }
                c = 65535;
                break;
            case 87087:
                if (type2.equals("XOF")) {
                    c = 27;
                    break;
                }
                c = 65535;
                break;
            case 87118:
                if (type2.equals("XPF")) {
                    c = 28;
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
                customFormat = " %.4f";
                double d = amount2;
                Double.isNaN(d);
                doubleAmount = d / 10000.0d;
                break;
            case 1:
                doubleAmount = ((float) amount2) / 100.0f;
                if (amount2 % 100 == 0) {
                    customFormat = " %.0f";
                    break;
                } else {
                    customFormat = " %.2f";
                    break;
                }
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case '\b':
                customFormat = " %.3f";
                double d2 = amount2;
                Double.isNaN(d2);
                doubleAmount = d2 / 1000.0d;
                break;
            case '\t':
            case '\n':
            case 11:
            case '\f':
            case '\r':
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
                customFormat = " %.0f";
                doubleAmount = amount2;
                break;
            case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                customFormat = " %.1f";
                double d3 = amount2;
                Double.isNaN(d3);
                doubleAmount = d3 / 10.0d;
                break;
            default:
                customFormat = " %.2f";
                double d4 = amount2;
                Double.isNaN(d4);
                doubleAmount = d4 / 100.0d;
                break;
        }
        Locale locale = Locale.US;
        if (inludeType) {
            str = type2;
        } else {
            str = "" + customFormat;
        }
        return String.format(locale, str, Double.valueOf(doubleAmount)).trim();
    }

    public static String formatStringSimple(String string, Object... args) {
        try {
            if (getInstance().currentLocale != null) {
                return String.format(getInstance().currentLocale, string, args);
            }
            return String.format(string, args);
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR: " + string;
        }
    }

    public static String formatDuration(int duration) {
        if (duration <= 0) {
            return formatPluralString("Seconds", 0, new Object[0]);
        }
        int hours = duration / 3600;
        int minutes = (duration / 60) % 60;
        int seconds = duration % 60;
        StringBuilder stringBuilder = new StringBuilder();
        if (hours > 0) {
            stringBuilder.append(formatPluralString("Hours", hours, new Object[0]));
        }
        if (minutes > 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(' ');
            }
            stringBuilder.append(formatPluralString("Minutes", minutes, new Object[0]));
        }
        if (seconds > 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(' ');
            }
            stringBuilder.append(formatPluralString("Seconds", seconds, new Object[0]));
        }
        return stringBuilder.toString();
    }

    public static String formatCallDuration(int duration) {
        if (duration > 3600) {
            String result = formatPluralString("Hours", duration / 3600, new Object[0]);
            int minutes = (duration % 3600) / 60;
            if (minutes > 0) {
                return result + ", " + formatPluralString("Minutes", minutes, new Object[0]);
            }
            return result;
        } else if (duration > 60) {
            return formatPluralString("Minutes", duration / 60, new Object[0]);
        } else {
            return formatPluralString("Seconds", duration, new Object[0]);
        }
    }

    public void onDeviceConfigurationChange(Configuration newConfig) {
        if (this.changingConfiguration) {
            return;
        }
        is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
        this.systemDefaultLocale = newConfig.locale;
        if (this.languageOverride != null) {
            LocaleInfo toSet = this.currentLocaleInfo;
            this.currentLocaleInfo = null;
            applyLanguage(toSet, false, false, UserConfig.selectedAccount);
        } else {
            Locale newLocale = newConfig.locale;
            if (newLocale != null) {
                String d1 = newLocale.getDisplayName();
                String d2 = this.currentLocale.getDisplayName();
                if (d1 != null && d2 != null && !d1.equals(d2)) {
                    recreateFormatters();
                }
                this.currentLocale = newLocale;
                LocaleInfo localeInfo = this.currentLocaleInfo;
                if (localeInfo != null && !TextUtils.isEmpty(localeInfo.pluralLangCode)) {
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
        }
        String newSystemLocale = getSystemLocaleStringIso639();
        String str = this.currentSystemLocale;
        if (str != null && !newSystemLocale.equals(str)) {
            this.currentSystemLocale = newSystemLocale;
            ConnectionsManager.setSystemLangCode(newSystemLocale);
        }
    }

    public static String formatDateChat(long date) {
        return formatDateChat(date, false);
    }

    public static String formatDateChat(long date, boolean checkYear) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int currentYear = calendar.get(1);
            long date2 = date * 1000;
            calendar.setTimeInMillis(date2);
            if ((checkYear && currentYear == calendar.get(1)) || (!checkYear && Math.abs(System.currentTimeMillis() - date2) < 31536000000L)) {
                return getInstance().chatDate.format(date2);
            }
            return getInstance().chatFullDate.format(date2);
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR: formatDateChat";
        }
    }

    public static String formatDate(long date) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date2);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            if (dateDay == day && year == dateYear) {
                return getInstance().formatterDay.format(new Date(date2));
            }
            if (dateDay + 1 == day && year == dateYear) {
                return getString("Yesterday", org.telegram.messenger.beta.R.string.Yesterday);
            }
            if (Math.abs(System.currentTimeMillis() - date2) < 31536000000L) {
                return getInstance().formatterDayMonth.format(new Date(date2));
            }
            return getInstance().formatterYear.format(new Date(date2));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR: formatDate";
        }
    }

    public static String formatDateAudio(long date, boolean shortFormat) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date2);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            if (dateDay == day && year == dateYear) {
                if (shortFormat) {
                    return formatString("TodayAtFormatted", org.telegram.messenger.beta.R.string.TodayAtFormatted, getInstance().formatterDay.format(new Date(date2)));
                }
                return formatString("TodayAtFormattedWithToday", org.telegram.messenger.beta.R.string.TodayAtFormattedWithToday, getInstance().formatterDay.format(new Date(date2)));
            } else if (dateDay + 1 == day && year == dateYear) {
                return formatString("YesterdayAtFormatted", org.telegram.messenger.beta.R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(date2)));
            } else {
                return Math.abs(System.currentTimeMillis() - date2) < 31536000000L ? formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().formatterDayMonth.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2))) : formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().formatterYear.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2)));
            }
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatDateCallLog(long date) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date2);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            if (dateDay == day && year == dateYear) {
                return getInstance().formatterDay.format(new Date(date2));
            }
            if (dateDay + 1 == day && year == dateYear) {
                return formatString("YesterdayAtFormatted", org.telegram.messenger.beta.R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(date2)));
            }
            return Math.abs(System.currentTimeMillis() - date2) < 31536000000L ? formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().chatDate.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2))) : formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().chatFullDate.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2)));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatDateTime(long date) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date2);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            if (dateDay == day && year == dateYear) {
                return formatString("TodayAtFormattedWithToday", org.telegram.messenger.beta.R.string.TodayAtFormattedWithToday, getInstance().formatterDay.format(new Date(date2)));
            }
            if (dateDay + 1 == day && year == dateYear) {
                return formatString("YesterdayAtFormatted", org.telegram.messenger.beta.R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(date2)));
            }
            return Math.abs(System.currentTimeMillis() - date2) < 31536000000L ? formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().chatDate.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2))) : formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().chatFullDate.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2)));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatLocationUpdateDate(long date) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date2);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            if (dateDay == day && year == dateYear) {
                int diff = ((int) (ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime() - (date2 / 1000))) / 60;
                if (diff < 1) {
                    return getString("LocationUpdatedJustNow", org.telegram.messenger.beta.R.string.LocationUpdatedJustNow);
                }
                if (diff >= 60) {
                    return formatString("LocationUpdatedFormatted", org.telegram.messenger.beta.R.string.LocationUpdatedFormatted, formatString("TodayAtFormatted", org.telegram.messenger.beta.R.string.TodayAtFormatted, getInstance().formatterDay.format(new Date(date2))));
                }
                return formatPluralString("UpdatedMinutes", diff, new Object[0]);
            } else if (dateDay + 1 == day && year == dateYear) {
                return formatString("LocationUpdatedFormatted", org.telegram.messenger.beta.R.string.LocationUpdatedFormatted, formatString("YesterdayAtFormatted", org.telegram.messenger.beta.R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(date2))));
            } else {
                if (Math.abs(System.currentTimeMillis() - date2) < 31536000000L) {
                    String format = formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().formatterDayMonth.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2)));
                    return formatString("LocationUpdatedFormatted", org.telegram.messenger.beta.R.string.LocationUpdatedFormatted, format);
                }
                String format2 = formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().formatterYear.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2)));
                return formatString("LocationUpdatedFormatted", org.telegram.messenger.beta.R.string.LocationUpdatedFormatted, format2);
            }
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatLocationLeftTime(int time) {
        int hours = (time / 60) / 60;
        int time2 = time - ((hours * 60) * 60);
        int minutes = time2 / 60;
        int time3 = time2 - (minutes * 60);
        int i = 1;
        if (hours != 0) {
            Object[] objArr = new Object[1];
            if (minutes <= 30) {
                i = 0;
            }
            objArr[0] = Integer.valueOf(i + hours);
            String text = String.format("%dh", objArr);
            return text;
        } else if (minutes == 0) {
            String text2 = String.format("%d", Integer.valueOf(time3));
            return text2;
        } else {
            Object[] objArr2 = new Object[1];
            if (time3 <= 30) {
                i = 0;
            }
            objArr2[0] = Integer.valueOf(i + minutes);
            String text3 = String.format("%d", objArr2);
            return text3;
        }
    }

    public static String formatDateOnline(long date, boolean[] madeShorter) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            int hour = rightNow.get(11);
            rightNow.setTimeInMillis(date2);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            int dateHour = rightNow.get(11);
            if (dateDay == day && year == dateYear) {
                return formatString("LastSeenFormatted", org.telegram.messenger.beta.R.string.LastSeenFormatted, formatString("TodayAtFormatted", org.telegram.messenger.beta.R.string.TodayAtFormatted, getInstance().formatterDay.format(new Date(date2))));
            }
            if (dateDay + 1 != day || year != dateYear) {
                if (Math.abs(System.currentTimeMillis() - date2) < 31536000000L) {
                    String format = formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().formatterDayMonth.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2)));
                    return formatString("LastSeenDateFormatted", org.telegram.messenger.beta.R.string.LastSeenDateFormatted, format);
                }
                String format2 = formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().formatterYear.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2)));
                return formatString("LastSeenDateFormatted", org.telegram.messenger.beta.R.string.LastSeenDateFormatted, format2);
            } else if (madeShorter == null) {
                return formatString("LastSeenFormatted", org.telegram.messenger.beta.R.string.LastSeenFormatted, formatString("YesterdayAtFormatted", org.telegram.messenger.beta.R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(date2))));
            } else {
                madeShorter[0] = true;
                if (hour <= 6 && dateHour > 18 && is24HourFormat) {
                    return formatString("LastSeenFormatted", org.telegram.messenger.beta.R.string.LastSeenFormatted, getInstance().formatterDay.format(new Date(date2)));
                }
                return formatString("YesterdayAtFormatted", org.telegram.messenger.beta.R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(date2)));
            }
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    private FastDateFormat createFormatter(Locale locale, String format, String defaultFormat) {
        if (format == null || format.length() == 0) {
            format = defaultFormat;
        }
        try {
            FastDateFormat formatter = FastDateFormat.getInstance(format, locale);
            return formatter;
        } catch (Exception e) {
            FastDateFormat formatter2 = FastDateFormat.getInstance(defaultFormat, locale);
            return formatter2;
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
        String lang = locale.getLanguage();
        if (lang == null) {
            lang = "en";
        }
        String lang2 = lang.toLowerCase();
        isRTL = (lang2.length() == 2 && (lang2.equals("ar") || lang2.equals("fa") || lang2.equals("he") || lang2.equals("iw"))) || lang2.startsWith("ar_") || lang2.startsWith("fa_") || lang2.startsWith("he_") || lang2.startsWith("iw_") || ((localeInfo = this.currentLocaleInfo) != null && localeInfo.isRtl);
        nameDisplayOrder = lang2.equals("ko") ? 2 : 1;
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
        Locale locale2 = (lang2.toLowerCase().equals("ar") || lang2.toLowerCase().equals("ko")) ? locale : Locale.US;
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

    public static boolean isRTLCharacter(char ch) {
        return Character.getDirectionality(ch) == 1 || Character.getDirectionality(ch) == 2 || Character.getDirectionality(ch) == 16 || Character.getDirectionality(ch) == 17;
    }

    public static String formatStartsTime(long date, int type) {
        return formatStartsTime(date, type, true);
    }

    public static String formatStartsTime(long date, int type, boolean needToday) {
        int num;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentYear = calendar.get(1);
        int currentDay = calendar.get(6);
        calendar.setTimeInMillis(1000 * date);
        int selectedYear = calendar.get(1);
        int selectedDay = calendar.get(6);
        if (currentYear == selectedYear) {
            if (needToday && selectedDay == currentDay) {
                num = 0;
            } else {
                num = 1;
            }
        } else {
            num = 2;
        }
        if (type == 1) {
            num += 3;
        } else if (type == 2) {
            num += 6;
        } else if (type == 3) {
            num += 9;
        } else if (type == 4) {
            num += 12;
        }
        return getInstance().formatterScheduleSend[num].format(calendar.getTimeInMillis());
    }

    public static String formatSectionDate(long date) {
        return formatYearMont(date, false);
    }

    public static String formatYearMont(long date, boolean alwaysShowYear) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date2);
            int dateYear = rightNow.get(1);
            int month = rightNow.get(2);
            String[] months = {getString("January", org.telegram.messenger.beta.R.string.January), getString("February", org.telegram.messenger.beta.R.string.February), getString("March", org.telegram.messenger.beta.R.string.March), getString("April", org.telegram.messenger.beta.R.string.April), getString("May", org.telegram.messenger.beta.R.string.May), getString("June", org.telegram.messenger.beta.R.string.June), getString("July", org.telegram.messenger.beta.R.string.July), getString("August", org.telegram.messenger.beta.R.string.August), getString("September", org.telegram.messenger.beta.R.string.September), getString("October", org.telegram.messenger.beta.R.string.October), getString("November", org.telegram.messenger.beta.R.string.November), getString("December", org.telegram.messenger.beta.R.string.December)};
            if (year == dateYear && !alwaysShowYear) {
                return months[month];
            }
            return months[month] + " " + dateYear;
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatDateForBan(long date) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date2);
            int dateYear = rightNow.get(1);
            if (year == dateYear) {
                return getInstance().formatterBannedUntilThisYear.format(new Date(date2));
            }
            return getInstance().formatterBannedUntil.format(new Date(date2));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String stringForMessageListDate(long date) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            rightNow.setTimeInMillis(date2);
            int dateDay = rightNow.get(6);
            if (Math.abs(System.currentTimeMillis() - date2) >= 31536000000L) {
                return getInstance().formatterYear.format(new Date(date2));
            }
            int dayDiff = dateDay - day;
            if (dayDiff != 0 && (dayDiff != -1 || System.currentTimeMillis() - date2 >= 28800000)) {
                if (dayDiff > -7 && dayDiff <= -1) {
                    return getInstance().formatterWeek.format(new Date(date2));
                }
                return getInstance().formatterDayMonth.format(new Date(date2));
            }
            return getInstance().formatterDay.format(new Date(date2));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatShortNumber(int number, int[] rounded) {
        StringBuilder K = new StringBuilder();
        int lastDec = 0;
        while (number / 1000 > 0) {
            K.append("K");
            lastDec = (number % 1000) / 100;
            number /= 1000;
        }
        if (rounded != null) {
            double d = number;
            double d2 = lastDec;
            Double.isNaN(d2);
            Double.isNaN(d);
            double value = d + (d2 / 10.0d);
            for (int a = 0; a < K.length(); a++) {
                value *= 1000.0d;
            }
            int a2 = (int) value;
            rounded[0] = a2;
        }
        return (lastDec == 0 || K.length() <= 0) ? K.length() == 2 ? String.format(Locale.US, "%dM", Integer.valueOf(number)) : String.format(Locale.US, "%d%s", Integer.valueOf(number), K.toString()) : K.length() == 2 ? String.format(Locale.US, "%d.%dM", Integer.valueOf(number), Integer.valueOf(lastDec)) : String.format(Locale.US, "%d.%d%s", Integer.valueOf(number), Integer.valueOf(lastDec), K.toString());
    }

    public static String formatUserStatus(int currentAccount, TLRPC.User user) {
        return formatUserStatus(currentAccount, user, null);
    }

    public static String formatJoined(long date) {
        long date2 = date * 1000;
        try {
            String format = Math.abs(System.currentTimeMillis() - date2) < 31536000000L ? formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().formatterDayMonth.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2))) : formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, getInstance().formatterYear.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2)));
            return formatString("ChannelOtherSubscriberJoined", org.telegram.messenger.beta.R.string.ChannelOtherSubscriberJoined, format);
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatImportedDate(long date) {
        try {
            Date dt = new Date(date * 1000);
            return String.format("%1$s, %2$s", getInstance().formatterYear.format(dt), getInstance().formatterDay.format(dt));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatUserStatus(int currentAccount, TLRPC.User user, boolean[] isOnline) {
        return formatUserStatus(currentAccount, user, isOnline, null);
    }

    public static String formatUserStatus(int currentAccount, TLRPC.User user, boolean[] isOnline, boolean[] madeShorter) {
        if (user != null && user.status != null && user.status.expires == 0) {
            if (user.status instanceof TLRPC.TL_userStatusRecently) {
                user.status.expires = -100;
            } else if (user.status instanceof TLRPC.TL_userStatusLastWeek) {
                user.status.expires = -101;
            } else if (user.status instanceof TLRPC.TL_userStatusLastMonth) {
                user.status.expires = -102;
            }
        }
        if (user != null && user.status != null && user.status.expires <= 0 && MessagesController.getInstance(currentAccount).onlinePrivacy.containsKey(Long.valueOf(user.id))) {
            if (isOnline != null) {
                isOnline[0] = true;
            }
            return getString("Online", org.telegram.messenger.beta.R.string.Online);
        } else if (user == null || user.status == null || user.status.expires == 0 || UserObject.isDeleted(user) || (user instanceof TLRPC.TL_userEmpty)) {
            return getString("ALongTimeAgo", org.telegram.messenger.beta.R.string.ALongTimeAgo);
        } else {
            int currentTime = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
            if (user.status.expires > currentTime) {
                if (isOnline != null) {
                    isOnline[0] = true;
                }
                return getString("Online", org.telegram.messenger.beta.R.string.Online);
            } else if (user.status.expires == -1) {
                return getString("Invisible", org.telegram.messenger.beta.R.string.Invisible);
            } else {
                if (user.status.expires == -100) {
                    return getString("Lately", org.telegram.messenger.beta.R.string.Lately);
                }
                if (user.status.expires != -101) {
                    if (user.status.expires == -102) {
                        return getString("WithinAMonth", org.telegram.messenger.beta.R.string.WithinAMonth);
                    }
                    return formatDateOnline(user.status.expires, madeShorter);
                }
                return getString("WithinAWeek", org.telegram.messenger.beta.R.string.WithinAWeek);
            }
        }
    }

    private String escapeString(String str) {
        if (str.contains("[CDATA")) {
            return str;
        }
        return str.replace("<", "&lt;").replace(">", "&gt;").replace("& ", "&amp; ");
    }

    public void saveRemoteLocaleStringsForCurrentLocale(TLRPC.TL_langPackDifference difference, int currentAccount) {
        if (this.currentLocaleInfo == null) {
            return;
        }
        String langCode = difference.lang_code.replace('-', '_').toLowerCase();
        if (!langCode.equals(this.currentLocaleInfo.shortName) && !langCode.equals(this.currentLocaleInfo.baseLangCode)) {
            return;
        }
        m328x926058b2(this.currentLocaleInfo, difference, currentAccount);
    }

    /* renamed from: saveRemoteLocaleStrings */
    public void m328x926058b2(final LocaleInfo localeInfo, final TLRPC.TL_langPackDifference difference, int currentAccount) {
        int type;
        File finalFile;
        HashMap<String, String> values;
        if (difference == null || difference.strings.isEmpty() || localeInfo == null || localeInfo.isLocal()) {
            return;
        }
        String langCode = difference.lang_code.replace('-', '_').toLowerCase();
        if (langCode.equals(localeInfo.shortName)) {
            type = 0;
        } else if (langCode.equals(localeInfo.baseLangCode)) {
            type = 1;
        } else {
            type = -1;
        }
        if (type == -1) {
            return;
        }
        if (type == 0) {
            finalFile = localeInfo.getPathToFile();
        } else {
            finalFile = localeInfo.getPathToBaseFile();
        }
        try {
            if (difference.from_version == 0) {
                values = new HashMap<>();
            } else {
                values = getLocaleFileStrings(finalFile, true);
            }
            for (int a = 0; a < difference.strings.size(); a++) {
                TLRPC.LangPackString string = difference.strings.get(a);
                if (string instanceof TLRPC.TL_langPackString) {
                    values.put(string.key, escapeString(string.value));
                } else if (string instanceof TLRPC.TL_langPackStringPluralized) {
                    String str = "";
                    values.put(string.key + "_zero", string.zero_value != null ? escapeString(string.zero_value) : str);
                    values.put(string.key + "_one", string.one_value != null ? escapeString(string.one_value) : str);
                    values.put(string.key + "_two", string.two_value != null ? escapeString(string.two_value) : str);
                    values.put(string.key + "_few", string.few_value != null ? escapeString(string.few_value) : str);
                    values.put(string.key + "_many", string.many_value != null ? escapeString(string.many_value) : str);
                    String str2 = string.key + "_other";
                    if (string.other_value != null) {
                        str = escapeString(string.other_value);
                    }
                    values.put(str2, str);
                } else if (string instanceof TLRPC.TL_langPackStringDeleted) {
                    values.remove(string.key);
                }
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("save locale file to " + finalFile);
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(finalFile));
            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            writer.write("<resources>\n");
            for (Map.Entry<String, String> entry : values.entrySet()) {
                writer.write(String.format("<string name=\"%1$s\">%2$s</string>\n", entry.getKey(), entry.getValue()));
            }
            writer.write("</resources>");
            writer.close();
            boolean hasBase = localeInfo.hasBaseLang();
            final HashMap<String, String> valuesToSet = getLocaleFileStrings(hasBase ? localeInfo.getPathToBaseFile() : localeInfo.getPathToFile());
            if (hasBase) {
                valuesToSet.putAll(getLocaleFileStrings(localeInfo.getPathToFile()));
            }
            final int i = type;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    LocaleController.this.m334xa697d88(i, localeInfo, difference, valuesToSet);
                }
            });
        } catch (Exception e) {
        }
    }

    /* renamed from: lambda$saveRemoteLocaleStrings$5$org-telegram-messenger-LocaleController */
    public /* synthetic */ void m334xa697d88(int type, LocaleInfo localeInfo, TLRPC.TL_langPackDifference difference, HashMap valuesToSet) {
        String[] args;
        Locale newLocale;
        if (type == 0) {
            localeInfo.version = difference.version;
        } else {
            localeInfo.baseVersion = difference.version;
        }
        saveOtherLanguages();
        try {
            if (this.currentLocaleInfo == localeInfo) {
                if (!TextUtils.isEmpty(localeInfo.pluralLangCode)) {
                    args = localeInfo.pluralLangCode.split("_");
                } else if (!TextUtils.isEmpty(localeInfo.baseLangCode)) {
                    args = localeInfo.baseLangCode.split("_");
                } else {
                    args = localeInfo.shortName.split("_");
                }
                if (args.length == 1) {
                    newLocale = new Locale(args[0]);
                } else {
                    newLocale = new Locale(args[0], args[1]);
                }
                this.languageOverride = localeInfo.shortName;
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("language", localeInfo.getKey());
                editor.commit();
                this.localeValues = valuesToSet;
                this.currentLocale = newLocale;
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
                Configuration config = new Configuration();
                config.locale = this.currentLocale;
                ApplicationLoader.applicationContext.getResources().updateConfiguration(config, ApplicationLoader.applicationContext.getResources().getDisplayMetrics());
                this.changingConfiguration = false;
            }
        } catch (Exception e) {
            FileLog.e(e);
            this.changingConfiguration = false;
        }
        recreateFormatters();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.reloadInterface, new Object[0]);
    }

    public void loadRemoteLanguages(int currentAccount) {
        loadRemoteLanguages(currentAccount, true);
    }

    public void loadRemoteLanguages(final int currentAccount, final boolean applyCurrent) {
        if (this.loadingRemoteLanguages) {
            return;
        }
        this.loadingRemoteLanguages = true;
        TLRPC.TL_langpack_getLanguages req = new TLRPC.TL_langpack_getLanguages();
        ConnectionsManager.getInstance(currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda6
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LocaleController.this.m331xc22159d4(applyCurrent, currentAccount, tLObject, tL_error);
            }
        }, 8);
    }

    /* renamed from: lambda$loadRemoteLanguages$7$org-telegram-messenger-LocaleController */
    public /* synthetic */ void m331xc22159d4(final boolean applyCurrent, final int currentAccount, final TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    LocaleController.this.m330x35812ed3(response, applyCurrent, currentAccount);
                }
            });
        }
    }

    /* renamed from: lambda$loadRemoteLanguages$6$org-telegram-messenger-LocaleController */
    public /* synthetic */ void m330x35812ed3(TLObject response, boolean applyCurrent, int currentAccount) {
        this.loadingRemoteLanguages = false;
        TLRPC.Vector res = (TLRPC.Vector) response;
        int size = this.remoteLanguages.size();
        for (int a = 0; a < size; a++) {
            this.remoteLanguages.get(a).serverIndex = Integer.MAX_VALUE;
        }
        int size2 = res.objects.size();
        for (int a2 = 0; a2 < size2; a2++) {
            TLRPC.TL_langPackLanguage language = (TLRPC.TL_langPackLanguage) res.objects.get(a2);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("loaded lang " + language.name);
            }
            LocaleInfo localeInfo = new LocaleInfo();
            localeInfo.nameEnglish = language.name;
            localeInfo.name = language.native_name;
            localeInfo.shortName = language.lang_code.replace('-', '_').toLowerCase();
            if (language.base_lang_code != null) {
                localeInfo.baseLangCode = language.base_lang_code.replace('-', '_').toLowerCase();
            } else {
                localeInfo.baseLangCode = "";
            }
            localeInfo.pluralLangCode = language.plural_code.replace('-', '_').toLowerCase();
            localeInfo.isRtl = language.rtl;
            localeInfo.pathToFile = "remote";
            localeInfo.serverIndex = a2;
            LocaleInfo existing = getLanguageFromDict(localeInfo.getKey());
            if (existing == null) {
                this.languages.add(localeInfo);
                this.languagesDict.put(localeInfo.getKey(), localeInfo);
            } else {
                existing.nameEnglish = localeInfo.nameEnglish;
                existing.name = localeInfo.name;
                existing.baseLangCode = localeInfo.baseLangCode;
                existing.pluralLangCode = localeInfo.pluralLangCode;
                existing.pathToFile = localeInfo.pathToFile;
                existing.serverIndex = localeInfo.serverIndex;
                localeInfo = existing;
            }
            if (!this.remoteLanguagesDict.containsKey(localeInfo.getKey())) {
                this.remoteLanguages.add(localeInfo);
                this.remoteLanguagesDict.put(localeInfo.getKey(), localeInfo);
            }
        }
        int a3 = 0;
        while (a3 < this.remoteLanguages.size()) {
            LocaleInfo info = this.remoteLanguages.get(a3);
            if (info.serverIndex == Integer.MAX_VALUE && info != this.currentLocaleInfo) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("remove lang " + info.getKey());
                }
                this.remoteLanguages.remove(a3);
                this.remoteLanguagesDict.remove(info.getKey());
                this.languages.remove(info);
                this.languagesDict.remove(info.getKey());
                a3--;
            }
            a3++;
        }
        saveOtherLanguages();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.suggestedLangpack, new Object[0]);
        if (applyCurrent) {
            applyLanguage(this.currentLocaleInfo, true, false, currentAccount);
        }
    }

    private void applyRemoteLanguage(final LocaleInfo localeInfo, String langCode, boolean force, final int currentAccount) {
        if (localeInfo != null) {
            if (!localeInfo.isRemote() && !localeInfo.isUnofficial()) {
                return;
            }
            if (localeInfo.hasBaseLang() && (langCode == null || langCode.equals(localeInfo.baseLangCode))) {
                if (localeInfo.baseVersion != 0 && !force) {
                    if (localeInfo.hasBaseLang()) {
                        TLRPC.TL_langpack_getDifference req = new TLRPC.TL_langpack_getDifference();
                        req.from_version = localeInfo.baseVersion;
                        req.lang_code = localeInfo.getBaseLangCode();
                        req.lang_pack = "";
                        ConnectionsManager.getInstance(currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda5
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                LocaleController.this.m329x1f0083b3(localeInfo, currentAccount, tLObject, tL_error);
                            }
                        }, 8);
                    }
                } else {
                    TLRPC.TL_langpack_getLangPack req2 = new TLRPC.TL_langpack_getLangPack();
                    req2.lang_code = localeInfo.getBaseLangCode();
                    ConnectionsManager.getInstance(currentAccount).sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda2
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            LocaleController.this.m323x8b2f7be4(localeInfo, currentAccount, tLObject, tL_error);
                        }
                    }, 8);
                }
            }
            if (langCode == null || langCode.equals(localeInfo.shortName)) {
                if (localeInfo.version != 0 && !force) {
                    TLRPC.TL_langpack_getDifference req3 = new TLRPC.TL_langpack_getDifference();
                    req3.from_version = localeInfo.version;
                    req3.lang_code = localeInfo.getLangCode();
                    req3.lang_pack = "";
                    ConnectionsManager.getInstance(currentAccount).sendRequest(req3, new RequestDelegate() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda3
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            LocaleController.this.m325xa46fd1e6(localeInfo, currentAccount, tLObject, tL_error);
                        }
                    }, 8);
                    return;
                }
                for (int a = 0; a < 4; a++) {
                    ConnectionsManager.setLangCode(localeInfo.getLangCode());
                }
                TLRPC.TL_langpack_getLangPack req4 = new TLRPC.TL_langpack_getLangPack();
                req4.lang_code = localeInfo.getLangCode();
                ConnectionsManager.getInstance(currentAccount).sendRequest(req4, new RequestDelegate() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda4
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        LocaleController.this.m327xbdb027e8(localeInfo, currentAccount, tLObject, tL_error);
                    }
                }, 8);
            }
        }
    }

    /* renamed from: lambda$applyRemoteLanguage$9$org-telegram-messenger-LocaleController */
    public /* synthetic */ void m329x1f0083b3(final LocaleInfo localeInfo, final int currentAccount, final TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    LocaleController.this.m328x926058b2(localeInfo, response, currentAccount);
                }
            });
        }
    }

    /* renamed from: lambda$applyRemoteLanguage$11$org-telegram-messenger-LocaleController */
    public /* synthetic */ void m323x8b2f7be4(final LocaleInfo localeInfo, final int currentAccount, final TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    LocaleController.this.m322xfe8f50e3(localeInfo, response, currentAccount);
                }
            });
        }
    }

    /* renamed from: lambda$applyRemoteLanguage$13$org-telegram-messenger-LocaleController */
    public /* synthetic */ void m325xa46fd1e6(final LocaleInfo localeInfo, final int currentAccount, final TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    LocaleController.this.m324x17cfa6e5(localeInfo, response, currentAccount);
                }
            });
        }
    }

    /* renamed from: lambda$applyRemoteLanguage$15$org-telegram-messenger-LocaleController */
    public /* synthetic */ void m327xbdb027e8(final LocaleInfo localeInfo, final int currentAccount, final TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.LocaleController$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    LocaleController.this.m326x310ffce7(localeInfo, response, currentAccount);
                }
            });
        }
    }

    public String getTranslitString(String src) {
        return getTranslitString(src, true, false);
    }

    public String getTranslitString(String src, boolean onlyEnglish) {
        return getTranslitString(src, true, onlyEnglish);
    }

    public String getTranslitString(String src, boolean ru, boolean onlyEnglish) {
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        String str7;
        String str8;
        String str9;
        if (src == null) {
            return null;
        }
        if (this.ruTranslitChars != null) {
            str2 = "m";
            str = ImageLoader.AUTOPLAY_FILTER;
            str9 = "r";
            str3 = "z";
            str7 = "h";
            str6 = TtmlNode.TAG_P;
            str4 = "v";
            str8 = "u";
            str5 = "s";
        } else {
            HashMap<String, String> hashMap = new HashMap<>(33);
            this.ruTranslitChars = hashMap;
            hashMap.put("а", "a");
            this.ruTranslitChars.put("б", "b");
            this.ruTranslitChars.put("в", "v");
            this.ruTranslitChars.put("г", ImageLoader.AUTOPLAY_FILTER);
            this.ruTranslitChars.put("д", Theme.DEFAULT_BACKGROUND_SLUG);
            this.ruTranslitChars.put("е", "e");
            HashMap<String, String> hashMap2 = this.ruTranslitChars;
            str = ImageLoader.AUTOPLAY_FILTER;
            hashMap2.put("ё", "yo");
            this.ruTranslitChars.put("ж", "zh");
            this.ruTranslitChars.put("з", "z");
            this.ruTranslitChars.put("и", "i");
            this.ruTranslitChars.put("й", "i");
            this.ruTranslitChars.put("к", "k");
            this.ruTranslitChars.put("л", "l");
            this.ruTranslitChars.put("м", "m");
            this.ruTranslitChars.put("н", "n");
            this.ruTranslitChars.put("о", "o");
            this.ruTranslitChars.put("п", TtmlNode.TAG_P);
            str2 = "m";
            str9 = "r";
            this.ruTranslitChars.put("р", str9);
            str3 = "z";
            this.ruTranslitChars.put("с", "s");
            str4 = "v";
            this.ruTranslitChars.put("т", Theme.THEME_BACKGROUND_SLUG);
            str8 = "u";
            this.ruTranslitChars.put("у", str8);
            str5 = "s";
            this.ruTranslitChars.put("ф", "f");
            str7 = "h";
            this.ruTranslitChars.put("х", str7);
            HashMap<String, String> hashMap3 = this.ruTranslitChars;
            str6 = TtmlNode.TAG_P;
            hashMap3.put("ц", "ts");
            this.ruTranslitChars.put("ч", "ch");
            this.ruTranslitChars.put("ш", "sh");
            this.ruTranslitChars.put("щ", "sch");
            this.ruTranslitChars.put("ы", "i");
            this.ruTranslitChars.put("ь", "");
            this.ruTranslitChars.put("ъ", "");
            this.ruTranslitChars.put("э", "e");
            this.ruTranslitChars.put("ю", "yu");
            this.ruTranslitChars.put("я", "ya");
        }
        if (this.translitChars == null) {
            HashMap<String, String> hashMap4 = new HashMap<>(487);
            this.translitChars = hashMap4;
            hashMap4.put("ȼ", Theme.COLOR_BACKGROUND_SLUG);
            this.translitChars.put("ᶇ", "n");
            this.translitChars.put("ɖ", Theme.DEFAULT_BACKGROUND_SLUG);
            this.translitChars.put("ỿ", "y");
            this.translitChars.put("ᴓ", "o");
            this.translitChars.put("ø", "o");
            this.translitChars.put("ḁ", "a");
            this.translitChars.put("ʯ", str7);
            this.translitChars.put("ŷ", "y");
            this.translitChars.put("ʞ", "k");
            this.translitChars.put("ừ", str8);
            String str10 = str8;
            this.translitChars.put("ꜳ", "aa");
            this.translitChars.put("ĳ", "ij");
            this.translitChars.put("ḽ", "l");
            this.translitChars.put("ɪ", "i");
            this.translitChars.put("ḇ", "b");
            this.translitChars.put("ʀ", str9);
            this.translitChars.put("ě", "e");
            this.translitChars.put("ﬃ", "ffi");
            this.translitChars.put("ơ", "o");
            this.translitChars.put("ⱹ", str9);
            this.translitChars.put("ồ", "o");
            this.translitChars.put("ǐ", "i");
            String str11 = str6;
            this.translitChars.put("ꝕ", str11);
            this.translitChars.put("ý", "y");
            this.translitChars.put("ḝ", "e");
            this.translitChars.put("ₒ", "o");
            this.translitChars.put("ⱥ", "a");
            this.translitChars.put("ʙ", "b");
            this.translitChars.put("ḛ", "e");
            this.translitChars.put("ƈ", Theme.COLOR_BACKGROUND_SLUG);
            this.translitChars.put("ɦ", str7);
            this.translitChars.put("ᵬ", "b");
            String str12 = str7;
            String str13 = str5;
            this.translitChars.put("ṣ", str13);
            this.translitChars.put("đ", Theme.DEFAULT_BACKGROUND_SLUG);
            this.translitChars.put("ỗ", "o");
            this.translitChars.put("ɟ", "j");
            this.translitChars.put("ẚ", "a");
            this.translitChars.put("ɏ", "y");
            this.translitChars.put("ʌ", str4);
            this.translitChars.put("ꝓ", str11);
            this.translitChars.put("ﬁ", "fi");
            this.translitChars.put("ᶄ", "k");
            this.translitChars.put("ḏ", Theme.DEFAULT_BACKGROUND_SLUG);
            this.translitChars.put("ᴌ", "l");
            this.translitChars.put("ė", "e");
            this.translitChars.put("ᴋ", "k");
            this.translitChars.put("ċ", Theme.COLOR_BACKGROUND_SLUG);
            this.translitChars.put("ʁ", str9);
            this.translitChars.put("ƕ", "hv");
            this.translitChars.put("ƀ", "b");
            this.translitChars.put("ṍ", "o");
            this.translitChars.put("ȣ", "ou");
            this.translitChars.put("ǰ", "j");
            String str14 = str;
            this.translitChars.put("ᶃ", str14);
            this.translitChars.put("ṋ", "n");
            this.translitChars.put("ɉ", "j");
            this.translitChars.put("ǧ", str14);
            this.translitChars.put("ǳ", "dz");
            String str15 = str3;
            this.translitChars.put("ź", str15);
            this.translitChars.put("ꜷ", "au");
            this.translitChars.put("ǖ", str10);
            this.translitChars.put("ᵹ", str14);
            this.translitChars.put("ȯ", "o");
            this.translitChars.put("ɐ", "a");
            this.translitChars.put("ą", "a");
            this.translitChars.put("õ", "o");
            this.translitChars.put("ɻ", str9);
            this.translitChars.put("ꝍ", "o");
            this.translitChars.put("ǟ", "a");
            this.translitChars.put("ȴ", "l");
            this.translitChars.put("ʂ", str13);
            this.translitChars.put("ﬂ", "fl");
            this.translitChars.put("ȉ", "i");
            this.translitChars.put("ⱻ", "e");
            this.translitChars.put("ṉ", "n");
            this.translitChars.put("ï", "i");
            this.translitChars.put("ñ", "n");
            this.translitChars.put("ᴉ", "i");
            this.translitChars.put("ʇ", Theme.THEME_BACKGROUND_SLUG);
            this.translitChars.put("ẓ", str15);
            this.translitChars.put("ỷ", "y");
            this.translitChars.put("ȳ", "y");
            this.translitChars.put("ṩ", str13);
            this.translitChars.put("ɽ", str9);
            this.translitChars.put("ĝ", str14);
            this.translitChars.put("ᴝ", str10);
            this.translitChars.put("ḳ", "k");
            this.translitChars.put("ꝫ", "et");
            this.translitChars.put("ī", "i");
            this.translitChars.put("ť", Theme.THEME_BACKGROUND_SLUG);
            this.translitChars.put("ꜿ", Theme.COLOR_BACKGROUND_SLUG);
            this.translitChars.put("ʟ", "l");
            this.translitChars.put("ꜹ", "av");
            this.translitChars.put("û", str10);
            this.translitChars.put("æ", "ae");
            this.translitChars.put("ă", "a");
            this.translitChars.put("ǘ", str10);
            this.translitChars.put("ꞅ", str13);
            this.translitChars.put("ᵣ", str9);
            this.translitChars.put("ᴀ", "a");
            this.translitChars.put("ƃ", "b");
            this.translitChars.put("ḩ", str12);
            this.translitChars.put("ṧ", str13);
            this.translitChars.put("ₑ", "e");
            this.translitChars.put("ʜ", str12);
            this.translitChars.put("ẋ", "x");
            this.translitChars.put("ꝅ", "k");
            this.translitChars.put("ḋ", Theme.DEFAULT_BACKGROUND_SLUG);
            this.translitChars.put("ƣ", "oi");
            this.translitChars.put("ꝑ", str11);
            this.translitChars.put("ħ", str12);
            String str16 = str4;
            this.translitChars.put("ⱴ", str16);
            this.translitChars.put("ẇ", "w");
            this.translitChars.put("ǹ", "n");
            String str17 = str2;
            this.translitChars.put("ɯ", str17);
            this.translitChars.put("ɡ", str14);
            this.translitChars.put("ɴ", "n");
            this.translitChars.put("ᴘ", str11);
            this.translitChars.put("ᵥ", str16);
            this.translitChars.put("ū", str10);
            this.translitChars.put("ḃ", "b");
            this.translitChars.put("ṗ", str11);
            this.translitChars.put("å", "a");
            this.translitChars.put("ɕ", Theme.COLOR_BACKGROUND_SLUG);
            this.translitChars.put("ọ", "o");
            this.translitChars.put("ắ", "a");
            this.translitChars.put("ƒ", "f");
            this.translitChars.put("ǣ", "ae");
            this.translitChars.put("ꝡ", "vy");
            this.translitChars.put("ﬀ", "ff");
            this.translitChars.put("ᶉ", str9);
            this.translitChars.put("ô", "o");
            this.translitChars.put("ǿ", "o");
            this.translitChars.put("ṳ", str10);
            this.translitChars.put("ȥ", str15);
            this.translitChars.put("ḟ", "f");
            this.translitChars.put("ḓ", Theme.DEFAULT_BACKGROUND_SLUG);
            this.translitChars.put("ȇ", "e");
            this.translitChars.put("ȕ", str10);
            this.translitChars.put("ȵ", "n");
            this.translitChars.put("ʠ", "q");
            this.translitChars.put("ấ", "a");
            this.translitChars.put("ǩ", "k");
            this.translitChars.put("ĩ", "i");
            this.translitChars.put("ṵ", str10);
            this.translitChars.put("ŧ", Theme.THEME_BACKGROUND_SLUG);
            this.translitChars.put("ɾ", str9);
            this.translitChars.put("ƙ", "k");
            this.translitChars.put("ṫ", Theme.THEME_BACKGROUND_SLUG);
            this.translitChars.put("ꝗ", "q");
            this.translitChars.put("ậ", "a");
            this.translitChars.put("ʄ", "j");
            this.translitChars.put("ƚ", "l");
            this.translitChars.put("ᶂ", "f");
            this.translitChars.put("ᵴ", str13);
            this.translitChars.put("ꞃ", str9);
            this.translitChars.put("ᶌ", str16);
            this.translitChars.put("ɵ", "o");
            this.translitChars.put("ḉ", Theme.COLOR_BACKGROUND_SLUG);
            this.translitChars.put("ᵤ", str10);
            this.translitChars.put("ẑ", str15);
            this.translitChars.put("ṹ", str10);
            this.translitChars.put("ň", "n");
            this.translitChars.put("ʍ", "w");
            this.translitChars.put("ầ", "a");
            this.translitChars.put("ǉ", "lj");
            this.translitChars.put("ɓ", "b");
            this.translitChars.put("ɼ", str9);
            this.translitChars.put("ò", "o");
            this.translitChars.put("ẘ", "w");
            this.translitChars.put("ɗ", Theme.DEFAULT_BACKGROUND_SLUG);
            this.translitChars.put("ꜽ", "ay");
            this.translitChars.put("ư", str10);
            this.translitChars.put("ᶀ", "b");
            this.translitChars.put("ǜ", str10);
            this.translitChars.put("ẹ", "e");
            this.translitChars.put("ǡ", "a");
            this.translitChars.put("ɥ", str12);
            this.translitChars.put("ṏ", "o");
            this.translitChars.put("ǔ", str10);
            this.translitChars.put("ʎ", "y");
            this.translitChars.put("ȱ", "o");
            this.translitChars.put("ệ", "e");
            this.translitChars.put("ế", "e");
            this.translitChars.put("ĭ", "i");
            this.translitChars.put("ⱸ", "e");
            this.translitChars.put("ṯ", Theme.THEME_BACKGROUND_SLUG);
            this.translitChars.put("ᶑ", Theme.DEFAULT_BACKGROUND_SLUG);
            this.translitChars.put("ḧ", str12);
            this.translitChars.put("ṥ", str13);
            this.translitChars.put("ë", "e");
            this.translitChars.put("ᴍ", str17);
            this.translitChars.put("ö", "o");
            this.translitChars.put("é", "e");
            this.translitChars.put("ı", "i");
            this.translitChars.put("ď", Theme.DEFAULT_BACKGROUND_SLUG);
            this.translitChars.put("ᵯ", str17);
            this.translitChars.put("ỵ", "y");
            this.translitChars.put("ŵ", "w");
            this.translitChars.put("ề", "e");
            this.translitChars.put("ứ", str10);
            this.translitChars.put("ƶ", str15);
            this.translitChars.put("ĵ", "j");
            this.translitChars.put("ḍ", Theme.DEFAULT_BACKGROUND_SLUG);
            this.translitChars.put("ŭ", str10);
            this.translitChars.put("ʝ", "j");
            this.translitChars.put("ê", "e");
            this.translitChars.put("ǚ", str10);
            this.translitChars.put("ġ", str14);
            this.translitChars.put("ṙ", str9);
            this.translitChars.put("ƞ", "n");
            this.translitChars.put("ḗ", "e");
            this.translitChars.put("ẝ", str13);
            this.translitChars.put("ᶁ", Theme.DEFAULT_BACKGROUND_SLUG);
            this.translitChars.put("ķ", "k");
            this.translitChars.put("ᴂ", "ae");
            this.translitChars.put("ɘ", "e");
            this.translitChars.put("ợ", "o");
            this.translitChars.put("ḿ", str17);
            this.translitChars.put("ꜰ", "f");
            this.translitChars.put("ẵ", "a");
            this.translitChars.put("ꝏ", "oo");
            this.translitChars.put("ᶆ", str17);
            this.translitChars.put("ᵽ", str11);
            this.translitChars.put("ữ", str10);
            this.translitChars.put("ⱪ", "k");
            this.translitChars.put("ḥ", str12);
            this.translitChars.put("ţ", Theme.THEME_BACKGROUND_SLUG);
            this.translitChars.put("ᵱ", str11);
            this.translitChars.put("ṁ", str17);
            this.translitChars.put("á", "a");
            this.translitChars.put("ᴎ", "n");
            this.translitChars.put("ꝟ", str16);
            this.translitChars.put("è", "e");
            this.translitChars.put("ᶎ", str15);
            this.translitChars.put("ꝺ", Theme.DEFAULT_BACKGROUND_SLUG);
            this.translitChars.put("ᶈ", str11);
            this.translitChars.put("ɫ", "l");
            this.translitChars.put("ᴢ", str15);
            this.translitChars.put("ɱ", str17);
            this.translitChars.put("ṝ", str9);
            this.translitChars.put("ṽ", str16);
            this.translitChars.put("ũ", str10);
            this.translitChars.put("ß", DownloadRequest.TYPE_SS);
            this.translitChars.put("ĥ", str12);
            this.translitChars.put("ᵵ", Theme.THEME_BACKGROUND_SLUG);
            this.translitChars.put("ʐ", str15);
            this.translitChars.put("ṟ", str9);
            this.translitChars.put("ɲ", "n");
            this.translitChars.put("à", "a");
            this.translitChars.put("ẙ", "y");
            this.translitChars.put("ỳ", "y");
            this.translitChars.put("ᴔ", "oe");
            this.translitChars.put("ₓ", "x");
            this.translitChars.put("ȗ", str10);
            this.translitChars.put("ⱼ", "j");
            this.translitChars.put("ẫ", "a");
            this.translitChars.put("ʑ", str15);
            this.translitChars.put("ẛ", str13);
            this.translitChars.put("ḭ", "i");
            this.translitChars.put("ꜵ", "ao");
            this.translitChars.put("ɀ", str15);
            this.translitChars.put("ÿ", "y");
            this.translitChars.put("ǝ", "e");
            this.translitChars.put("ǭ", "o");
            this.translitChars.put("ᴅ", Theme.DEFAULT_BACKGROUND_SLUG);
            this.translitChars.put("ᶅ", "l");
            this.translitChars.put("ù", str10);
            this.translitChars.put("ạ", "a");
            this.translitChars.put("ḅ", "b");
            this.translitChars.put("ụ", str10);
            this.translitChars.put("ằ", "a");
            this.translitChars.put("ᴛ", Theme.THEME_BACKGROUND_SLUG);
            this.translitChars.put("ƴ", "y");
            this.translitChars.put("ⱦ", Theme.THEME_BACKGROUND_SLUG);
            this.translitChars.put("ⱡ", "l");
            this.translitChars.put("ȷ", "j");
            this.translitChars.put("ᵶ", str15);
            this.translitChars.put("ḫ", str12);
            this.translitChars.put("ⱳ", "w");
            this.translitChars.put("ḵ", "k");
            this.translitChars.put("ờ", "o");
            this.translitChars.put("î", "i");
            this.translitChars.put("ģ", str14);
            this.translitChars.put("ȅ", "e");
            this.translitChars.put("ȧ", "a");
            this.translitChars.put("ẳ", "a");
            this.translitChars.put("ɋ", "q");
            this.translitChars.put("ṭ", Theme.THEME_BACKGROUND_SLUG);
            this.translitChars.put("ꝸ", "um");
            this.translitChars.put("ᴄ", Theme.COLOR_BACKGROUND_SLUG);
            this.translitChars.put("ẍ", "x");
            this.translitChars.put("ủ", str10);
            this.translitChars.put("ỉ", "i");
            this.translitChars.put("ᴚ", str9);
            this.translitChars.put("ś", str13);
            this.translitChars.put("ꝋ", "o");
            this.translitChars.put("ỹ", "y");
            this.translitChars.put("ṡ", str13);
            this.translitChars.put("ǌ", "nj");
            this.translitChars.put("ȁ", "a");
            this.translitChars.put("ẗ", Theme.THEME_BACKGROUND_SLUG);
            this.translitChars.put("ĺ", "l");
            this.translitChars.put("ž", str15);
            this.translitChars.put("ᵺ", "th");
            this.translitChars.put("ƌ", Theme.DEFAULT_BACKGROUND_SLUG);
            this.translitChars.put("ș", str13);
            this.translitChars.put("š", str13);
            this.translitChars.put("ᶙ", str10);
            this.translitChars.put("ẽ", "e");
            this.translitChars.put("ẜ", str13);
            this.translitChars.put("ɇ", "e");
            this.translitChars.put("ṷ", str10);
            this.translitChars.put("ố", "o");
            this.translitChars.put("ȿ", str13);
            this.translitChars.put("ᴠ", str16);
            this.translitChars.put("ꝭ", "is");
            this.translitChars.put("ᴏ", "o");
            this.translitChars.put("ɛ", "e");
            this.translitChars.put("ǻ", "a");
            this.translitChars.put("ﬄ", "ffl");
            this.translitChars.put("ⱺ", "o");
            this.translitChars.put("ȋ", "i");
            this.translitChars.put("ᵫ", "ue");
            this.translitChars.put("ȡ", Theme.DEFAULT_BACKGROUND_SLUG);
            this.translitChars.put("ⱬ", str15);
            this.translitChars.put("ẁ", "w");
            this.translitChars.put("ᶏ", "a");
            this.translitChars.put("ꞇ", Theme.THEME_BACKGROUND_SLUG);
            this.translitChars.put("ğ", str14);
            this.translitChars.put("ɳ", "n");
            this.translitChars.put("ʛ", str14);
            this.translitChars.put("ᴜ", str10);
            this.translitChars.put("ẩ", "a");
            this.translitChars.put("ṅ", "n");
            this.translitChars.put("ɨ", "i");
            this.translitChars.put("ᴙ", str9);
            this.translitChars.put("ǎ", "a");
            this.translitChars.put("ſ", str13);
            this.translitChars.put("ȫ", "o");
            this.translitChars.put("ɿ", str9);
            this.translitChars.put("ƭ", Theme.THEME_BACKGROUND_SLUG);
            this.translitChars.put("ḯ", "i");
            this.translitChars.put("ǽ", "ae");
            this.translitChars.put("ⱱ", str16);
            this.translitChars.put("ɶ", "oe");
            this.translitChars.put("ṃ", str17);
            this.translitChars.put("ż", str15);
            this.translitChars.put("ĕ", "e");
            this.translitChars.put("ꜻ", "av");
            this.translitChars.put("ở", "o");
            this.translitChars.put("ễ", "e");
            this.translitChars.put("ɬ", "l");
            this.translitChars.put("ị", "i");
            this.translitChars.put("ᵭ", Theme.DEFAULT_BACKGROUND_SLUG);
            this.translitChars.put("ﬆ", "st");
            this.translitChars.put("ḷ", "l");
            this.translitChars.put("ŕ", str9);
            this.translitChars.put("ᴕ", "ou");
            this.translitChars.put("ʈ", Theme.THEME_BACKGROUND_SLUG);
            this.translitChars.put("ā", "a");
            this.translitChars.put("ḙ", "e");
            this.translitChars.put("ᴑ", "o");
            this.translitChars.put("ç", Theme.COLOR_BACKGROUND_SLUG);
            this.translitChars.put("ᶊ", str13);
            this.translitChars.put("ặ", "a");
            this.translitChars.put("ų", str10);
            this.translitChars.put("ả", "a");
            this.translitChars.put("ǥ", str14);
            this.translitChars.put("ꝁ", "k");
            this.translitChars.put("ẕ", str15);
            this.translitChars.put("ŝ", str13);
            this.translitChars.put("ḕ", "e");
            this.translitChars.put("ɠ", str14);
            this.translitChars.put("ꝉ", "l");
            this.translitChars.put("ꝼ", "f");
            this.translitChars.put("ᶍ", "x");
            this.translitChars.put("ǒ", "o");
            this.translitChars.put("ę", "e");
            this.translitChars.put("ổ", "o");
            this.translitChars.put("ƫ", Theme.THEME_BACKGROUND_SLUG);
            this.translitChars.put("ǫ", "o");
            this.translitChars.put("i̇", "i");
            this.translitChars.put("ṇ", "n");
            this.translitChars.put("ć", Theme.COLOR_BACKGROUND_SLUG);
            this.translitChars.put("ᵷ", str14);
            this.translitChars.put("ẅ", "w");
            this.translitChars.put("ḑ", Theme.DEFAULT_BACKGROUND_SLUG);
            this.translitChars.put("ḹ", "l");
            this.translitChars.put("œ", "oe");
            this.translitChars.put("ᵳ", str9);
            this.translitChars.put("ļ", "l");
            this.translitChars.put("ȑ", str9);
            this.translitChars.put("ȭ", "o");
            this.translitChars.put("ᵰ", "n");
            this.translitChars.put("ᴁ", "ae");
            this.translitChars.put("ŀ", "l");
            this.translitChars.put("ä", "a");
            this.translitChars.put("ƥ", str11);
            this.translitChars.put("ỏ", "o");
            this.translitChars.put("į", "i");
            this.translitChars.put("ȓ", str9);
            this.translitChars.put("ǆ", "dz");
            this.translitChars.put("ḡ", str14);
            this.translitChars.put("ṻ", str10);
            this.translitChars.put("ō", "o");
            this.translitChars.put("ľ", "l");
            this.translitChars.put("ẃ", "w");
            this.translitChars.put("ț", Theme.THEME_BACKGROUND_SLUG);
            this.translitChars.put("ń", "n");
            this.translitChars.put("ɍ", str9);
            this.translitChars.put("ȃ", "a");
            this.translitChars.put("ü", str10);
            this.translitChars.put("ꞁ", "l");
            this.translitChars.put("ᴐ", "o");
            this.translitChars.put("ớ", "o");
            this.translitChars.put("ᴃ", "b");
            this.translitChars.put("ɹ", str9);
            this.translitChars.put("ᵲ", str9);
            this.translitChars.put("ʏ", "y");
            this.translitChars.put("ᵮ", "f");
            this.translitChars.put("ⱨ", str12);
            this.translitChars.put("ŏ", "o");
            this.translitChars.put("ú", str10);
            this.translitChars.put("ṛ", str9);
            this.translitChars.put("ʮ", str12);
            this.translitChars.put("ó", "o");
            this.translitChars.put("ů", str10);
            this.translitChars.put("ỡ", "o");
            this.translitChars.put("ṕ", str11);
            this.translitChars.put("ᶖ", "i");
            this.translitChars.put("ự", str10);
            this.translitChars.put("ã", "a");
            this.translitChars.put("ᵢ", "i");
            this.translitChars.put("ṱ", Theme.THEME_BACKGROUND_SLUG);
            this.translitChars.put("ể", "e");
            this.translitChars.put("ử", str10);
            this.translitChars.put("í", "i");
            this.translitChars.put("ɔ", "o");
            this.translitChars.put("ɺ", str9);
            this.translitChars.put("ɢ", str14);
            this.translitChars.put("ř", str9);
            this.translitChars.put("ẖ", str12);
            this.translitChars.put("ű", str10);
            this.translitChars.put("ȍ", "o");
            this.translitChars.put("ḻ", "l");
            this.translitChars.put("ḣ", str12);
            this.translitChars.put("ȶ", Theme.THEME_BACKGROUND_SLUG);
            this.translitChars.put("ņ", "n");
            this.translitChars.put("ᶒ", "e");
            this.translitChars.put("ì", "i");
            this.translitChars.put("ẉ", "w");
            this.translitChars.put("ē", "e");
            this.translitChars.put("ᴇ", "e");
            this.translitChars.put("ł", "l");
            this.translitChars.put("ộ", "o");
            this.translitChars.put("ɭ", "l");
            this.translitChars.put("ẏ", "y");
            this.translitChars.put("ᴊ", "j");
            this.translitChars.put("ḱ", "k");
            this.translitChars.put("ṿ", str16);
            this.translitChars.put("ȩ", "e");
            this.translitChars.put("â", "a");
            this.translitChars.put("ş", str13);
            this.translitChars.put("ŗ", str9);
            this.translitChars.put("ʋ", str16);
            this.translitChars.put("ₐ", "a");
            this.translitChars.put("ↄ", Theme.COLOR_BACKGROUND_SLUG);
            this.translitChars.put("ᶓ", "e");
            this.translitChars.put("ɰ", str17);
            this.translitChars.put("ᴡ", "w");
            this.translitChars.put("ȏ", "o");
            this.translitChars.put("č", Theme.COLOR_BACKGROUND_SLUG);
            this.translitChars.put("ǵ", str14);
            this.translitChars.put("ĉ", Theme.COLOR_BACKGROUND_SLUG);
            this.translitChars.put("ᶗ", "o");
            this.translitChars.put("ꝃ", "k");
            this.translitChars.put("ꝙ", "q");
            this.translitChars.put("ṑ", "o");
            this.translitChars.put("ꜱ", str13);
            this.translitChars.put("ṓ", "o");
            this.translitChars.put("ȟ", str12);
            this.translitChars.put("ő", "o");
            this.translitChars.put("ꜩ", "tz");
            this.translitChars.put("ẻ", "e");
        }
        StringBuilder dst = new StringBuilder(src.length());
        int len = src.length();
        boolean upperCase = false;
        for (int a = 0; a < len; a++) {
            String ch = src.substring(a, a + 1);
            if (onlyEnglish) {
                String lower = ch.toLowerCase();
                upperCase = !ch.equals(lower);
                ch = lower;
            }
            String tch = this.translitChars.get(ch);
            if (tch == null && ru) {
                tch = this.ruTranslitChars.get(ch);
            }
            if (tch != null) {
                if (onlyEnglish && upperCase) {
                    tch = tch.length() > 1 ? tch.substring(0, 1).toUpperCase() + tch.substring(1) : tch.toUpperCase();
                }
                dst.append(tch);
            } else {
                if (onlyEnglish) {
                    char c = ch.charAt(0);
                    if ((c < 'a' || c > 'z' || c < '0' || c > '9') && c != ' ' && c != '\'' && c != ',' && c != '.' && c != '&' && c != '-' && c != '/') {
                        return null;
                    }
                    if (upperCase) {
                        ch = ch.toUpperCase();
                    }
                }
                dst.append(ch);
            }
        }
        return dst.toString();
    }

    /* loaded from: classes4.dex */
    public static class PluralRules_Zero extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int count) {
            if (count == 0 || count == 1) {
                return 2;
            }
            return 0;
        }
    }

    /* loaded from: classes4.dex */
    public static class PluralRules_Welsh extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int count) {
            if (count == 0) {
                return 1;
            }
            if (count == 1) {
                return 2;
            }
            if (count == 2) {
                return 4;
            }
            if (count == 3) {
                return 8;
            }
            if (count == 6) {
                return 16;
            }
            return 0;
        }
    }

    /* loaded from: classes4.dex */
    public static class PluralRules_Two extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int count) {
            if (count == 1) {
                return 2;
            }
            if (count == 2) {
                return 4;
            }
            return 0;
        }
    }

    /* loaded from: classes4.dex */
    public static class PluralRules_Tachelhit extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int count) {
            if (count >= 0 && count <= 1) {
                return 2;
            }
            if (count >= 2 && count <= 10) {
                return 8;
            }
            return 0;
        }
    }

    /* loaded from: classes4.dex */
    public static class PluralRules_Slovenian extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            if (rem100 == 1) {
                return 2;
            }
            if (rem100 == 2) {
                return 4;
            }
            if (rem100 >= 3 && rem100 <= 4) {
                return 8;
            }
            return 0;
        }
    }

    /* loaded from: classes4.dex */
    public static class PluralRules_Romanian extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            if (count == 1) {
                return 2;
            }
            if (count != 0) {
                if (rem100 >= 1 && rem100 <= 19) {
                    return 8;
                }
                return 0;
            }
            return 8;
        }
    }

    /* loaded from: classes4.dex */
    public static class PluralRules_Polish extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            int rem10 = count % 10;
            if (count == 1) {
                return 2;
            }
            if (rem10 >= 2 && rem10 <= 4 && (rem100 < 12 || rem100 > 14)) {
                return 8;
            }
            if (rem10 >= 0 && rem10 <= 1) {
                return 16;
            }
            if (rem10 < 5 || rem10 > 9) {
                if (rem100 >= 12 && rem100 <= 14) {
                    return 16;
                }
                return 0;
            }
            return 16;
        }
    }

    /* loaded from: classes4.dex */
    public static class PluralRules_One extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int count) {
            return count == 1 ? 2 : 0;
        }
    }

    /* loaded from: classes4.dex */
    public static class PluralRules_None extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int count) {
            return 0;
        }
    }

    /* loaded from: classes4.dex */
    public static class PluralRules_Maltese extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            if (count == 1) {
                return 2;
            }
            if (count != 0) {
                if (rem100 >= 2 && rem100 <= 10) {
                    return 8;
                }
                if (rem100 >= 11 && rem100 <= 19) {
                    return 16;
                }
                return 0;
            }
            return 8;
        }
    }

    /* loaded from: classes4.dex */
    public static class PluralRules_Macedonian extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int count) {
            if (count % 10 == 1 && count != 11) {
                return 2;
            }
            return 0;
        }
    }

    /* loaded from: classes4.dex */
    public static class PluralRules_Lithuanian extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            int rem10 = count % 10;
            if (rem10 != 1 || (rem100 >= 11 && rem100 <= 19)) {
                if (rem10 >= 2 && rem10 <= 9) {
                    if (rem100 < 11 || rem100 > 19) {
                        return 8;
                    }
                    return 0;
                }
                return 0;
            }
            return 2;
        }
    }

    /* loaded from: classes4.dex */
    public static class PluralRules_Latvian extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int count) {
            if (count == 0) {
                return 1;
            }
            if (count % 10 == 1 && count % 100 != 11) {
                return 2;
            }
            return 0;
        }
    }

    /* loaded from: classes4.dex */
    public static class PluralRules_Langi extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int count) {
            if (count == 0) {
                return 1;
            }
            if (count == 1) {
                return 2;
            }
            return 0;
        }
    }

    /* loaded from: classes4.dex */
    public static class PluralRules_French extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int count) {
            return (count < 0 || count >= 2) ? 0 : 2;
        }
    }

    /* loaded from: classes4.dex */
    public static class PluralRules_Czech extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int count) {
            if (count == 1) {
                return 2;
            }
            if (count >= 2 && count <= 4) {
                return 8;
            }
            return 0;
        }
    }

    /* loaded from: classes4.dex */
    public static class PluralRules_Breton extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int count) {
            if (count == 0) {
                return 1;
            }
            if (count == 1) {
                return 2;
            }
            if (count == 2) {
                return 4;
            }
            if (count == 3) {
                return 8;
            }
            if (count == 6) {
                return 16;
            }
            return 0;
        }
    }

    /* loaded from: classes4.dex */
    public static class PluralRules_Balkan extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            int rem10 = count % 10;
            if (rem10 != 1 || rem100 == 11) {
                if (rem10 >= 2 && rem10 <= 4 && (rem100 < 12 || rem100 > 14)) {
                    return 8;
                }
                if (rem10 == 0) {
                    return 16;
                }
                if (rem10 < 5 || rem10 > 9) {
                    if (rem100 >= 11 && rem100 <= 14) {
                        return 16;
                    }
                    return 0;
                }
                return 16;
            }
            return 2;
        }
    }

    /* loaded from: classes4.dex */
    public static class PluralRules_Serbian extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            int rem10 = count % 10;
            if (rem10 == 1 && rem100 != 11) {
                return 2;
            }
            if (rem10 < 2 || rem10 > 4) {
                return 0;
            }
            if (rem100 < 12 || rem100 > 14) {
                return 8;
            }
            return 0;
        }
    }

    /* loaded from: classes4.dex */
    public static class PluralRules_Arabic extends PluralRules {
        @Override // org.telegram.messenger.LocaleController.PluralRules
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            if (count == 0) {
                return 1;
            }
            if (count == 1) {
                return 2;
            }
            if (count == 2) {
                return 4;
            }
            if (rem100 >= 3 && rem100 <= 10) {
                return 8;
            }
            if (rem100 >= 11 && rem100 <= 99) {
                return 16;
            }
            return 0;
        }
    }

    public static String addNbsp(String src) {
        return src.replace(' ', (char) 160);
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
        boolean z = true;
        if (SharedConfig.distanceSystemType == 0) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (telephonyManager != null) {
                    String country = telephonyManager.getSimCountryIso().toUpperCase();
                    if (!"US".equals(country) && !"GB".equals(country) && !"MM".equals(country) && !"LR".equals(country)) {
                        z = false;
                    }
                    useImperialSystemType = Boolean.valueOf(z);
                    return;
                }
                return;
            } catch (Exception e) {
                useImperialSystemType = false;
                FileLog.e(e);
                return;
            }
        }
        if (SharedConfig.distanceSystemType != 2) {
            z = false;
        }
        useImperialSystemType = Boolean.valueOf(z);
    }

    public static String formatDistance(float distance, int type) {
        return formatDistance(distance, type, null);
    }

    public static String formatDistance(float distance, int type, Boolean useImperial) {
        ensureImperialSystemInit();
        boolean imperial = (useImperial != null && useImperial.booleanValue()) || (useImperial == null && useImperialSystemType.booleanValue());
        if (imperial) {
            float distance2 = distance * 3.28084f;
            if (distance2 < 1000.0f) {
                switch (type) {
                    case 0:
                        return formatString("FootsAway", org.telegram.messenger.beta.R.string.FootsAway, String.format("%d", Integer.valueOf((int) Math.max(1.0f, distance2))));
                    case 1:
                        return formatString("FootsFromYou", org.telegram.messenger.beta.R.string.FootsFromYou, String.format("%d", Integer.valueOf((int) Math.max(1.0f, distance2))));
                    default:
                        return formatString("FootsShort", org.telegram.messenger.beta.R.string.FootsShort, String.format("%d", Integer.valueOf((int) Math.max(1.0f, distance2))));
                }
            }
            String arg = distance2 % 5280.0f == 0.0f ? String.format("%d", Integer.valueOf((int) (distance2 / 5280.0f))) : String.format("%.2f", Float.valueOf(distance2 / 5280.0f));
            switch (type) {
                case 0:
                    return formatString("MilesAway", org.telegram.messenger.beta.R.string.MilesAway, arg);
                case 1:
                    return formatString("MilesFromYou", org.telegram.messenger.beta.R.string.MilesFromYou, arg);
                default:
                    return formatString("MilesShort", org.telegram.messenger.beta.R.string.MilesShort, arg);
            }
        } else if (distance < 1000.0f) {
            switch (type) {
                case 0:
                    return formatString("MetersAway2", org.telegram.messenger.beta.R.string.MetersAway2, String.format("%d", Integer.valueOf((int) Math.max(1.0f, distance))));
                case 1:
                    return formatString("MetersFromYou2", org.telegram.messenger.beta.R.string.MetersFromYou2, String.format("%d", Integer.valueOf((int) Math.max(1.0f, distance))));
                default:
                    return formatString("MetersShort", org.telegram.messenger.beta.R.string.MetersShort, String.format("%d", Integer.valueOf((int) Math.max(1.0f, distance))));
            }
        } else {
            String arg2 = distance % 1000.0f == 0.0f ? String.format("%d", Integer.valueOf((int) (distance / 1000.0f))) : String.format("%.2f", Float.valueOf(distance / 1000.0f));
            switch (type) {
                case 0:
                    return formatString("KMetersAway2", org.telegram.messenger.beta.R.string.KMetersAway2, arg2);
                case 1:
                    return formatString("KMetersFromYou2", org.telegram.messenger.beta.R.string.KMetersFromYou2, arg2);
                default:
                    return formatString("KMetersShort", org.telegram.messenger.beta.R.string.KMetersShort, arg2);
            }
        }
    }
}
