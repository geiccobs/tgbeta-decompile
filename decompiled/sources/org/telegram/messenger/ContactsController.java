package org.telegram.messenger;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.SparseArray;
import androidx.collection.LongSparseArray;
import androidx.exifinterface.media.ExifInterface;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes.dex */
public class ContactsController extends BaseController {
    private static volatile ContactsController[] Instance = new ContactsController[4];
    public static final int PRIVACY_RULES_TYPE_ADDED_BY_PHONE = 7;
    public static final int PRIVACY_RULES_TYPE_CALLS = 2;
    public static final int PRIVACY_RULES_TYPE_COUNT = 8;
    public static final int PRIVACY_RULES_TYPE_FORWARDS = 5;
    public static final int PRIVACY_RULES_TYPE_INVITE = 1;
    public static final int PRIVACY_RULES_TYPE_LASTSEEN = 0;
    public static final int PRIVACY_RULES_TYPE_P2P = 3;
    public static final int PRIVACY_RULES_TYPE_PHONE = 6;
    public static final int PRIVACY_RULES_TYPE_PHOTO = 4;
    private ArrayList<TLRPC.PrivacyRule> addedByPhonePrivacyRules;
    private ArrayList<TLRPC.PrivacyRule> callPrivacyRules;
    private int completedRequestsCount;
    private boolean contactsBookLoaded;
    public boolean contactsLoaded;
    private boolean contactsSyncInProgress;
    private int deleteAccountTTL;
    public boolean doneLoadingContacts;
    private ArrayList<TLRPC.PrivacyRule> forwardsPrivacyRules;
    private TLRPC.TL_globalPrivacySettings globalPrivacySettings;
    private ArrayList<TLRPC.PrivacyRule> groupPrivacyRules;
    private boolean ignoreChanges;
    private String inviteLink;
    private ArrayList<TLRPC.PrivacyRule> lastseenPrivacyRules;
    private boolean loadingContacts;
    private int loadingDeleteInfo;
    private int loadingGlobalSettings;
    private boolean migratingContacts;
    private ArrayList<TLRPC.PrivacyRule> p2pPrivacyRules;
    private ArrayList<TLRPC.PrivacyRule> phonePrivacyRules;
    private ArrayList<TLRPC.PrivacyRule> profilePhotoPrivacyRules;
    private Account systemAccount;
    private boolean updatingInviteLink;
    private final Object loadContactsSync = new Object();
    private final Object observerLock = new Object();
    private String lastContactsVersions = "";
    private ArrayList<Long> delayedContactsUpdate = new ArrayList<>();
    private HashMap<String, String> sectionsToReplace = new HashMap<>();
    private int[] loadingPrivacyInfo = new int[8];
    private String[] projectionPhones = {"lookup", "data1", "data2", "data3", "display_name", "account_type"};
    private String[] projectionNames = {"lookup", "data2", "data3", "data5"};
    public HashMap<String, Contact> contactsBook = new HashMap<>();
    public HashMap<String, Contact> contactsBookSPhones = new HashMap<>();
    public ArrayList<Contact> phoneBookContacts = new ArrayList<>();
    public HashMap<String, ArrayList<Object>> phoneBookSectionsDict = new HashMap<>();
    public ArrayList<String> phoneBookSectionsArray = new ArrayList<>();
    public ArrayList<TLRPC.TL_contact> contacts = new ArrayList<>();
    public ConcurrentHashMap<Long, TLRPC.TL_contact> contactsDict = new ConcurrentHashMap<>(20, 1.0f, 2);
    public HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = new HashMap<>();
    public ArrayList<String> sortedUsersSectionsArray = new ArrayList<>();
    public HashMap<String, ArrayList<TLRPC.TL_contact>> usersMutualSectionsDict = new HashMap<>();
    public ArrayList<String> sortedUsersMutualSectionsArray = new ArrayList<>();
    public HashMap<String, TLRPC.TL_contact> contactsByPhone = new HashMap<>();
    public HashMap<String, TLRPC.TL_contact> contactsByShortPhone = new HashMap<>();

    /* loaded from: classes4.dex */
    public class MyContentObserver extends ContentObserver {
        private Runnable checkRunnable = ContactsController$MyContentObserver$$ExternalSyntheticLambda0.INSTANCE;

        public static /* synthetic */ void lambda$new$0() {
            for (int a = 0; a < 4; a++) {
                if (UserConfig.getInstance(a).isClientActivated()) {
                    ConnectionsManager.getInstance(a).resumeNetworkMaybe();
                    ContactsController.getInstance(a).checkContacts();
                }
            }
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public MyContentObserver() {
            super(null);
            ContactsController.this = r1;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            synchronized (ContactsController.this.observerLock) {
                if (ContactsController.this.ignoreChanges) {
                    return;
                }
                Utilities.globalQueue.cancelRunnable(this.checkRunnable);
                Utilities.globalQueue.postRunnable(this.checkRunnable, 500L);
            }
        }

        @Override // android.database.ContentObserver
        public boolean deliverSelfNotifications() {
            return false;
        }
    }

    /* loaded from: classes4.dex */
    public static class Contact {
        public int contact_id;
        public String first_name;
        public int imported;
        public boolean isGoodProvider;
        public String key;
        public String last_name;
        public boolean namesFilled;
        public String provider;
        public TLRPC.User user;
        public ArrayList<String> phones = new ArrayList<>(4);
        public ArrayList<String> phoneTypes = new ArrayList<>(4);
        public ArrayList<String> shortPhones = new ArrayList<>(4);
        public ArrayList<Integer> phoneDeleted = new ArrayList<>(4);

        public String getLetter() {
            return getLetter(this.first_name, this.last_name);
        }

        public static String getLetter(String first_name, String last_name) {
            if (!TextUtils.isEmpty(first_name)) {
                return first_name.substring(0, 1);
            }
            if (!TextUtils.isEmpty(last_name)) {
                return last_name.substring(0, 1);
            }
            return "#";
        }
    }

    public static ContactsController getInstance(int num) {
        ContactsController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (ContactsController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    ContactsController[] contactsControllerArr = Instance;
                    ContactsController contactsController = new ContactsController(num);
                    localInstance = contactsController;
                    contactsControllerArr[num] = contactsController;
                }
            }
        }
        return localInstance;
    }

    public ContactsController(int instance) {
        super(instance);
        SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
        if (preferences.getBoolean("needGetStatuses", false)) {
            reloadContactsStatuses();
        }
        this.sectionsToReplace.put("À", ExifInterface.GPS_MEASUREMENT_IN_PROGRESS);
        this.sectionsToReplace.put("Á", ExifInterface.GPS_MEASUREMENT_IN_PROGRESS);
        this.sectionsToReplace.put("Ä", ExifInterface.GPS_MEASUREMENT_IN_PROGRESS);
        this.sectionsToReplace.put("Ù", "U");
        this.sectionsToReplace.put("Ú", "U");
        this.sectionsToReplace.put("Ü", "U");
        this.sectionsToReplace.put("Ì", "I");
        this.sectionsToReplace.put("Í", "I");
        this.sectionsToReplace.put("Ï", "I");
        this.sectionsToReplace.put("È", ExifInterface.LONGITUDE_EAST);
        this.sectionsToReplace.put("É", ExifInterface.LONGITUDE_EAST);
        this.sectionsToReplace.put("Ê", ExifInterface.LONGITUDE_EAST);
        this.sectionsToReplace.put("Ë", ExifInterface.LONGITUDE_EAST);
        this.sectionsToReplace.put("Ò", "O");
        this.sectionsToReplace.put("Ó", "O");
        this.sectionsToReplace.put("Ö", "O");
        this.sectionsToReplace.put("Ç", "C");
        this.sectionsToReplace.put("Ñ", "N");
        this.sectionsToReplace.put("Ÿ", "Y");
        this.sectionsToReplace.put("Ý", "Y");
        this.sectionsToReplace.put("Ţ", "Y");
        if (instance == 0) {
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda60
                @Override // java.lang.Runnable
                public final void run() {
                    ContactsController.this.m156lambda$new$0$orgtelegrammessengerContactsController();
                }
            });
        }
    }

    /* renamed from: lambda$new$0$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m156lambda$new$0$orgtelegrammessengerContactsController() {
        try {
            if (hasContactsPermission()) {
                ApplicationLoader.applicationContext.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, new MyContentObserver());
            }
        } catch (Throwable th) {
        }
    }

    public void cleanup() {
        this.contactsBook.clear();
        this.contactsBookSPhones.clear();
        this.phoneBookContacts.clear();
        this.contacts.clear();
        this.contactsDict.clear();
        this.usersSectionsDict.clear();
        this.usersMutualSectionsDict.clear();
        this.sortedUsersSectionsArray.clear();
        this.sortedUsersMutualSectionsArray.clear();
        this.delayedContactsUpdate.clear();
        this.contactsByPhone.clear();
        this.contactsByShortPhone.clear();
        this.phoneBookSectionsDict.clear();
        this.phoneBookSectionsArray.clear();
        this.loadingContacts = false;
        this.contactsSyncInProgress = false;
        this.doneLoadingContacts = false;
        this.contactsLoaded = false;
        this.contactsBookLoaded = false;
        this.lastContactsVersions = "";
        this.loadingGlobalSettings = 0;
        this.loadingDeleteInfo = 0;
        this.deleteAccountTTL = 0;
        Arrays.fill(this.loadingPrivacyInfo, 0);
        this.lastseenPrivacyRules = null;
        this.groupPrivacyRules = null;
        this.callPrivacyRules = null;
        this.p2pPrivacyRules = null;
        this.profilePhotoPrivacyRules = null;
        this.forwardsPrivacyRules = null;
        this.phonePrivacyRules = null;
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda33
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m138lambda$cleanup$1$orgtelegrammessengerContactsController();
            }
        });
    }

    /* renamed from: lambda$cleanup$1$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m138lambda$cleanup$1$orgtelegrammessengerContactsController() {
        this.migratingContacts = false;
        this.completedRequestsCount = 0;
    }

    public void checkInviteText() {
        SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
        this.inviteLink = preferences.getString("invitelink", null);
        int time = preferences.getInt("invitelinktime", 0);
        if (!this.updatingInviteLink) {
            if (this.inviteLink == null || Math.abs((System.currentTimeMillis() / 1000) - time) >= 86400) {
                this.updatingInviteLink = true;
                TLRPC.TL_help_getInviteText req = new TLRPC.TL_help_getInviteText();
                getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda48
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ContactsController.this.m137x699e1f58(tLObject, tL_error);
                    }
                }, 2);
            }
        }
    }

    /* renamed from: lambda$checkInviteText$3$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m137x699e1f58(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            final TLRPC.TL_help_inviteText res = (TLRPC.TL_help_inviteText) response;
            if (res.message.length() != 0) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda35
                    @Override // java.lang.Runnable
                    public final void run() {
                        ContactsController.this.m136x845cb097(res);
                    }
                });
            }
        }
    }

    /* renamed from: lambda$checkInviteText$2$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m136x845cb097(TLRPC.TL_help_inviteText res) {
        this.updatingInviteLink = false;
        SharedPreferences preferences1 = MessagesController.getMainSettings(this.currentAccount);
        SharedPreferences.Editor editor = preferences1.edit();
        String str = res.message;
        this.inviteLink = str;
        editor.putString("invitelink", str);
        editor.putInt("invitelinktime", (int) (System.currentTimeMillis() / 1000));
        editor.commit();
    }

    public String getInviteText(int contacts) {
        String link = this.inviteLink;
        if (link == null) {
            link = "https://telegram.org/dl";
        }
        if (contacts <= 1) {
            return LocaleController.formatString("InviteText2", org.telegram.messenger.beta.R.string.InviteText2, link);
        }
        try {
            return String.format(LocaleController.getPluralString("InviteTextNum", contacts), Integer.valueOf(contacts), link);
        } catch (Exception e) {
            return LocaleController.formatString("InviteText2", org.telegram.messenger.beta.R.string.InviteText2, link);
        }
    }

    public void checkAppAccount() {
        AccountManager am = AccountManager.get(ApplicationLoader.applicationContext);
        try {
            Account[] accounts = am.getAccountsByType("org.telegram.messenger");
            this.systemAccount = null;
            for (int a = 0; a < accounts.length; a++) {
                Account acc = accounts[a];
                boolean found = false;
                int b = 0;
                while (true) {
                    if (b >= 4) {
                        break;
                    }
                    TLRPC.User user = UserConfig.getInstance(b).getCurrentUser();
                    if (user != null) {
                        String str = acc.name;
                        if (str.equals("" + user.id)) {
                            if (b == this.currentAccount) {
                                this.systemAccount = acc;
                            }
                            found = true;
                        }
                    }
                    b++;
                }
                if (!found) {
                    try {
                        am.removeAccount(accounts[a], null, null);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Throwable th) {
        }
        if (getUserConfig().isClientActivated()) {
            readContacts();
            if (this.systemAccount == null) {
                try {
                    Account account = new Account("" + getUserConfig().getClientUserId(), "org.telegram.messenger");
                    this.systemAccount = account;
                    am.addAccountExplicitly(account, "", null);
                } catch (Exception e2) {
                }
            }
        }
    }

    public void deleteUnknownAppAccounts() {
        try {
            this.systemAccount = null;
            AccountManager am = AccountManager.get(ApplicationLoader.applicationContext);
            Account[] accounts = am.getAccountsByType("org.telegram.messenger");
            for (int a = 0; a < accounts.length; a++) {
                Account acc = accounts[a];
                boolean found = false;
                int b = 0;
                while (true) {
                    if (b >= 4) {
                        break;
                    }
                    TLRPC.User user = UserConfig.getInstance(b).getCurrentUser();
                    if (user != null) {
                        String str = acc.name;
                        if (str.equals("" + user.id)) {
                            found = true;
                            break;
                        }
                    }
                    b++;
                }
                if (!found) {
                    try {
                        am.removeAccount(accounts[a], null, null);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void checkContacts() {
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda22
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m135lambda$checkContacts$4$orgtelegrammessengerContactsController();
            }
        });
    }

    /* renamed from: lambda$checkContacts$4$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m135lambda$checkContacts$4$orgtelegrammessengerContactsController() {
        if (checkContactsInternal()) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("detected contacts change");
            }
            performSyncPhoneBook(getContactsCopy(this.contactsBook), true, false, true, false, true, false);
        }
    }

    public void forceImportContacts() {
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda44
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m144xc2563fbf();
            }
        });
    }

    /* renamed from: lambda$forceImportContacts$5$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m144xc2563fbf() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("force import contacts");
        }
        performSyncPhoneBook(new HashMap<>(), true, true, true, true, false, false);
    }

    public void syncPhoneBookByAlert(final HashMap<String, Contact> contacts, final boolean first, final boolean schedule, final boolean cancel) {
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda29
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m181xe9e28340(contacts, first, schedule, cancel);
            }
        });
    }

    /* renamed from: lambda$syncPhoneBookByAlert$6$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m181xe9e28340(HashMap contacts, boolean first, boolean schedule, boolean cancel) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("sync contacts by alert");
        }
        performSyncPhoneBook(contacts, true, first, schedule, false, false, cancel);
    }

    public void deleteAllContacts(final Runnable runnable) {
        resetImportedContacts();
        TLRPC.TL_contacts_deleteContacts req = new TLRPC.TL_contacts_deleteContacts();
        int size = this.contacts.size();
        for (int a = 0; a < size; a++) {
            TLRPC.TL_contact contact = this.contacts.get(a);
            req.id.add(getMessagesController().getInputUser(contact.user_id));
        }
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda54
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ContactsController.this.m140x3b83ab48(runnable, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$deleteAllContacts$8$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m140x3b83ab48(final Runnable runnable, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            this.contactsBookSPhones.clear();
            this.contactsBook.clear();
            this.completedRequestsCount = 0;
            this.migratingContacts = false;
            this.contactsSyncInProgress = false;
            this.contactsLoaded = false;
            this.loadingContacts = false;
            this.contactsBookLoaded = false;
            this.lastContactsVersions = "";
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    ContactsController.this.m139x56423c87(runnable);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(runnable);
    }

    /* renamed from: lambda$deleteAllContacts$7$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m139x56423c87(Runnable runnable) {
        AccountManager am = AccountManager.get(ApplicationLoader.applicationContext);
        try {
            Account[] accounts = am.getAccountsByType("org.telegram.messenger");
            this.systemAccount = null;
            for (Account acc : accounts) {
                int b = 0;
                while (true) {
                    if (b >= 4) {
                        break;
                    }
                    TLRPC.User user = UserConfig.getInstance(b).getCurrentUser();
                    if (user != null) {
                        if (acc.name.equals("" + user.id)) {
                            am.removeAccount(acc, null, null);
                            break;
                        }
                    }
                    b++;
                }
            }
        } catch (Throwable th) {
        }
        try {
            Account account = new Account("" + getUserConfig().getClientUserId(), "org.telegram.messenger");
            this.systemAccount = account;
            am.addAccountExplicitly(account, "", null);
        } catch (Exception e) {
        }
        getMessagesStorage().putCachedPhoneBook(new HashMap<>(), false, true);
        getMessagesStorage().putContacts(new ArrayList<>(), true);
        this.phoneBookContacts.clear();
        this.contacts.clear();
        this.contactsDict.clear();
        this.usersSectionsDict.clear();
        this.usersMutualSectionsDict.clear();
        this.sortedUsersSectionsArray.clear();
        this.phoneBookSectionsDict.clear();
        this.phoneBookSectionsArray.clear();
        this.delayedContactsUpdate.clear();
        this.sortedUsersMutualSectionsArray.clear();
        this.contactsByPhone.clear();
        this.contactsByShortPhone.clear();
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        loadContacts(false, 0L);
        runnable.run();
    }

    public void resetImportedContacts() {
        TLRPC.TL_contacts_resetSaved req = new TLRPC.TL_contacts_resetSaved();
        getConnectionsManager().sendRequest(req, ContactsController$$ExternalSyntheticLambda59.INSTANCE);
    }

    public static /* synthetic */ void lambda$resetImportedContacts$9(TLObject response, TLRPC.TL_error error) {
    }

    private boolean checkContactsInternal() {
        boolean reload = false;
        try {
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (!hasContactsPermission()) {
            return false;
        }
        ContentResolver cr = ApplicationLoader.applicationContext.getContentResolver();
        try {
            Cursor pCur = cr.query(ContactsContract.RawContacts.CONTENT_URI, new String[]{"version"}, null, null, null);
            if (pCur != null) {
                StringBuilder currentVersion = new StringBuilder();
                while (pCur.moveToNext()) {
                    currentVersion.append(pCur.getString(pCur.getColumnIndex("version")));
                }
                String newContactsVersion = currentVersion.toString();
                if (this.lastContactsVersions.length() != 0 && !this.lastContactsVersions.equals(newContactsVersion)) {
                    reload = true;
                }
                this.lastContactsVersions = newContactsVersion;
            }
            if (pCur != null) {
                pCur.close();
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        return reload;
    }

    public void readContacts() {
        synchronized (this.loadContactsSync) {
            if (this.loadingContacts) {
                return;
            }
            this.loadingContacts = true;
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ContactsController.this.m178lambda$readContacts$10$orgtelegrammessengerContactsController();
                }
            });
        }
    }

    /* renamed from: lambda$readContacts$10$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m178lambda$readContacts$10$orgtelegrammessengerContactsController() {
        if (!this.contacts.isEmpty() || this.contactsLoaded) {
            synchronized (this.loadContactsSync) {
                this.loadingContacts = false;
            }
            return;
        }
        loadContacts(true, 0L);
    }

    private boolean isNotValidNameString(String src) {
        if (TextUtils.isEmpty(src)) {
            return true;
        }
        int count = 0;
        int len = src.length();
        for (int a = 0; a < len; a++) {
            char c = src.charAt(a);
            if (c >= '0' && c <= '9') {
                count++;
            }
        }
        return count > 3;
    }

    /* JADX WARN: Removed duplicated region for block: B:177:0x038d A[Catch: all -> 0x03a0, TRY_LEAVE, TryCatch #0 {all -> 0x03a0, blocks: (B:175:0x0388, B:177:0x038d), top: B:191:0x0388 }] */
    /* JADX WARN: Removed duplicated region for block: B:179:0x0392 A[Catch: Exception -> 0x0378, TRY_ENTER, TRY_LEAVE, TryCatch #7 {Exception -> 0x0378, blocks: (B:167:0x0374, B:179:0x0392), top: B:205:0x0030 }] */
    /* JADX WARN: Removed duplicated region for block: B:181:0x0398  */
    /* JADX WARN: Removed duplicated region for block: B:182:0x039a  */
    /* JADX WARN: Type inference failed for: r10v1 */
    /* JADX WARN: Type inference failed for: r10v2, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r10v3 */
    /* JADX WARN: Type inference failed for: r10v4 */
    /* JADX WARN: Type inference failed for: r10v6 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.util.HashMap<java.lang.String, org.telegram.messenger.ContactsController.Contact> readContactsFromPhoneBook() {
        /*
            Method dump skipped, instructions count: 947
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.readContactsFromPhoneBook():java.util.HashMap");
    }

    public HashMap<String, Contact> getContactsCopy(HashMap<String, Contact> original) {
        HashMap<String, Contact> ret = new HashMap<>();
        for (Map.Entry<String, Contact> entry : original.entrySet()) {
            Contact copyContact = new Contact();
            Contact originalContact = entry.getValue();
            copyContact.phoneDeleted.addAll(originalContact.phoneDeleted);
            copyContact.phones.addAll(originalContact.phones);
            copyContact.phoneTypes.addAll(originalContact.phoneTypes);
            copyContact.shortPhones.addAll(originalContact.shortPhones);
            copyContact.first_name = originalContact.first_name;
            copyContact.last_name = originalContact.last_name;
            copyContact.contact_id = originalContact.contact_id;
            copyContact.key = originalContact.key;
            ret.put(copyContact.key, copyContact);
        }
        return ret;
    }

    public void migratePhoneBookToV7(final SparseArray<Contact> contactHashMap) {
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m155x39f42a07(contactHashMap);
            }
        });
    }

    /* renamed from: lambda$migratePhoneBookToV7$11$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m155x39f42a07(SparseArray contactHashMap) {
        if (this.migratingContacts) {
            return;
        }
        this.migratingContacts = true;
        HashMap<String, Contact> migratedMap = new HashMap<>();
        HashMap<String, Contact> contactsMap = readContactsFromPhoneBook();
        HashMap<String, String> contactsBookShort = new HashMap<>();
        for (Map.Entry<String, Contact> entry : contactsMap.entrySet()) {
            Contact value = entry.getValue();
            for (int a = 0; a < value.shortPhones.size(); a++) {
                contactsBookShort.put(value.shortPhones.get(a), value.key);
            }
        }
        for (int b = 0; b < contactHashMap.size(); b++) {
            Contact value2 = (Contact) contactHashMap.valueAt(b);
            int a2 = 0;
            while (true) {
                if (a2 < value2.shortPhones.size()) {
                    String sphone = value2.shortPhones.get(a2);
                    String key = contactsBookShort.get(sphone);
                    if (key == null) {
                        a2++;
                    } else {
                        value2.key = key;
                        migratedMap.put(key, value2);
                        break;
                    }
                }
            }
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("migrated contacts " + migratedMap.size() + " of " + contactHashMap.size());
        }
        getMessagesStorage().putCachedPhoneBook(migratedMap, true, false);
    }

    public void performSyncPhoneBook(final HashMap<String, Contact> contactHashMap, final boolean request, final boolean first, final boolean schedule, final boolean force, final boolean checkCount, final boolean canceled) {
        if (!first && !this.contactsBookLoaded) {
            return;
        }
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda30
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m169x3cf1c3d6(contactHashMap, schedule, request, first, force, checkCount, canceled);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:46:0x0163, code lost:
        if (r2.first_name.equals(r1.first_name) == false) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x0178, code lost:
        if (r2.last_name.equals(r1.last_name) == false) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x017a, code lost:
        r0 = true;
     */
    /* JADX WARN: Removed duplicated region for block: B:113:0x02d6  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x0236  */
    /* renamed from: lambda$performSyncPhoneBook$24$org-telegram-messenger-ContactsController */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m169x3cf1c3d6(final java.util.HashMap r35, final boolean r36, boolean r37, final boolean r38, boolean r39, boolean r40, boolean r41) {
        /*
            Method dump skipped, instructions count: 1844
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.m169x3cf1c3d6(java.util.HashMap, boolean, boolean, boolean, boolean, boolean, boolean):void");
    }

    /* renamed from: lambda$performSyncPhoneBook$12$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m157xaf827cf5(HashMap contactHashMap) {
        ArrayList<TLRPC.User> toDelete = new ArrayList<>();
        if (contactHashMap != null && !contactHashMap.isEmpty()) {
            try {
                HashMap<String, TLRPC.User> contactsPhonesShort = new HashMap<>();
                for (int a = 0; a < this.contacts.size(); a++) {
                    TLRPC.TL_contact value = this.contacts.get(a);
                    TLRPC.User user = getMessagesController().getUser(Long.valueOf(value.user_id));
                    if (user != null && !TextUtils.isEmpty(user.phone)) {
                        contactsPhonesShort.put(user.phone, user);
                    }
                }
                int removed = 0;
                for (Map.Entry<String, Contact> entry : contactHashMap.entrySet()) {
                    Contact contact = entry.getValue();
                    boolean was = false;
                    int a2 = 0;
                    while (a2 < contact.shortPhones.size()) {
                        String phone = contact.shortPhones.get(a2);
                        TLRPC.User user2 = contactsPhonesShort.get(phone);
                        if (user2 != null) {
                            was = true;
                            toDelete.add(user2);
                            contact.shortPhones.remove(a2);
                            a2--;
                        }
                        a2++;
                    }
                    if (!was || contact.shortPhones.size() == 0) {
                        removed++;
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        if (!toDelete.isEmpty()) {
            deleteContact(toDelete, false);
        }
    }

    /* renamed from: lambda$performSyncPhoneBook$13$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m158x94c3ebb6(int checkType, HashMap contactHashMap, boolean first, boolean schedule) {
        getNotificationCenter().postNotificationName(NotificationCenter.hasNewContactsToImport, Integer.valueOf(checkType), contactHashMap, Boolean.valueOf(first), Boolean.valueOf(schedule));
    }

    /* renamed from: lambda$performSyncPhoneBook$15$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m160x5f46c938(HashMap contactsBookShort, HashMap contactsMap, boolean first, final HashMap phoneBookSectionsDictFinal, final ArrayList phoneBookSectionsArrayFinal, final HashMap phoneBookByShortPhonesFinal) {
        this.contactsBookSPhones = contactsBookShort;
        this.contactsBook = contactsMap;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (first) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, null, null, null);
            this.delayedContactsUpdate.clear();
        }
        getMessagesStorage().putCachedPhoneBook(contactsMap, false, false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m159x7a055a77(phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal);
            }
        });
    }

    /* renamed from: lambda$performSyncPhoneBook$14$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m159x7a055a77(HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal) {
        m167x726ee654(phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal);
        updateUnregisteredContacts();
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.contactsImported, new Object[0]);
    }

    /* renamed from: lambda$performSyncPhoneBook$19$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m164xf44c843c(HashMap contactsMapToSave, SparseArray contactIdToKey, final boolean[] hasErrors, final HashMap contactsMap, TLRPC.TL_contacts_importContacts req, int count, final HashMap contactsBookShort, final boolean first, final HashMap phoneBookSectionsDictFinal, final ArrayList phoneBookSectionsArrayFinal, final HashMap phoneBookByShortPhonesFinal, TLObject response, TLRPC.TL_error error) {
        this.completedRequestsCount++;
        if (error == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("contacts imported");
            }
            TLRPC.TL_contacts_importedContacts res = (TLRPC.TL_contacts_importedContacts) response;
            if (!res.retry_contacts.isEmpty()) {
                for (int a1 = 0; a1 < res.retry_contacts.size(); a1++) {
                    long id = res.retry_contacts.get(a1).longValue();
                    contactsMapToSave.remove(contactIdToKey.get((int) id));
                }
                hasErrors[0] = true;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("result has retry contacts");
                }
            }
            for (int a12 = 0; a12 < res.popular_invites.size(); a12++) {
                TLRPC.TL_popularContact popularContact = res.popular_invites.get(a12);
                Contact contact = (Contact) contactsMap.get(contactIdToKey.get((int) popularContact.client_id));
                if (contact != null) {
                    contact.imported = popularContact.importers;
                }
            }
            getMessagesStorage().putUsersAndChats(res.users, null, true, true);
            ArrayList<TLRPC.TL_contact> cArr = new ArrayList<>();
            for (int a13 = 0; a13 < res.imported.size(); a13++) {
                TLRPC.TL_contact contact2 = new TLRPC.TL_contact();
                contact2.user_id = res.imported.get(a13).user_id;
                cArr.add(contact2);
            }
            processLoadedContacts(cArr, res.users, 2);
        } else {
            for (int a14 = 0; a14 < req.contacts.size(); a14++) {
                contactsMapToSave.remove(contactIdToKey.get((int) req.contacts.get(a14).client_id));
            }
            hasErrors[0] = true;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("import contacts error " + error.text);
            }
        }
        if (this.completedRequestsCount == count) {
            if (!contactsMapToSave.isEmpty()) {
                getMessagesStorage().putCachedPhoneBook(contactsMapToSave, false, false);
            }
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda28
                @Override // java.lang.Runnable
                public final void run() {
                    ContactsController.this.m163xf0b157b(contactsBookShort, contactsMap, first, phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal, hasErrors);
                }
            });
        }
    }

    /* renamed from: lambda$performSyncPhoneBook$18$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m163xf0b157b(HashMap contactsBookShort, HashMap contactsMap, boolean first, final HashMap phoneBookSectionsDictFinal, final ArrayList phoneBookSectionsArrayFinal, final HashMap phoneBookByShortPhonesFinal, boolean[] hasErrors) {
        this.contactsBookSPhones = contactsBookShort;
        this.contactsBook = contactsMap;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (first) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, null, null, null);
            this.delayedContactsUpdate.clear();
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda19
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m161x448837f9(phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal);
            }
        });
        if (hasErrors[0]) {
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda61
                @Override // java.lang.Runnable
                public final void run() {
                    ContactsController.this.m162x29c9a6ba();
                }
            }, 300000L);
        }
    }

    /* renamed from: lambda$performSyncPhoneBook$16$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m161x448837f9(HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal) {
        m167x726ee654(phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal);
        getNotificationCenter().postNotificationName(NotificationCenter.contactsImported, new Object[0]);
    }

    /* renamed from: lambda$performSyncPhoneBook$17$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m162x29c9a6ba() {
        getMessagesStorage().getCachedPhoneBook(true);
    }

    /* renamed from: lambda$performSyncPhoneBook$21$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m166x8d2d7793(HashMap contactsBookShort, HashMap contactsMap, boolean first, final HashMap phoneBookSectionsDictFinal, final ArrayList phoneBookSectionsArrayFinal, final HashMap phoneBookByShortPhonesFinal) {
        this.contactsBookSPhones = contactsBookShort;
        this.contactsBook = contactsMap;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (first) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, null, null, null);
            this.delayedContactsUpdate.clear();
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m165xa7ec08d2(phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal);
            }
        });
    }

    /* renamed from: lambda$performSyncPhoneBook$20$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m165xa7ec08d2(HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal) {
        m167x726ee654(phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal);
        updateUnregisteredContacts();
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.contactsImported, new Object[0]);
    }

    /* renamed from: lambda$performSyncPhoneBook$23$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m168x57b05515(HashMap contactsBookShort, HashMap contactsMap, boolean first, final HashMap phoneBookSectionsDictFinal, final ArrayList phoneBookSectionsArrayFinal, final HashMap phoneBookByShortPhonesFinal) {
        this.contactsBookSPhones = contactsBookShort;
        this.contactsBook = contactsMap;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (first) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, null, null, null);
            this.delayedContactsUpdate.clear();
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda21
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m167x726ee654(phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal);
            }
        });
    }

    public boolean isLoadingContacts() {
        boolean z;
        synchronized (this.loadContactsSync) {
            z = this.loadingContacts;
        }
        return z;
    }

    private long getContactsHash(ArrayList<TLRPC.TL_contact> contacts) {
        long acc = 0;
        ArrayList<TLRPC.TL_contact> contacts2 = new ArrayList<>(contacts);
        Collections.sort(contacts2, ContactsController$$ExternalSyntheticLambda46.INSTANCE);
        int count = contacts2.size();
        for (int a = -1; a < count; a++) {
            if (a == -1) {
                acc = MediaDataController.calcHash(acc, getUserConfig().contactsSavedCount);
            } else {
                TLRPC.TL_contact set = contacts2.get(a);
                acc = MediaDataController.calcHash(acc, set.user_id);
            }
        }
        return acc;
    }

    public static /* synthetic */ int lambda$getContactsHash$25(TLRPC.TL_contact tl_contact, TLRPC.TL_contact tl_contact2) {
        if (tl_contact.user_id > tl_contact2.user_id) {
            return 1;
        }
        if (tl_contact.user_id < tl_contact2.user_id) {
            return -1;
        }
        return 0;
    }

    public void loadContacts(boolean fromCache, final long hash) {
        synchronized (this.loadContactsSync) {
            this.loadingContacts = true;
        }
        if (fromCache) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("load contacts from cache");
            }
            getMessagesStorage().getContacts();
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("load contacts from server");
        }
        TLRPC.TL_contacts_getContacts req = new TLRPC.TL_contacts_getContacts();
        req.hash = hash;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda52
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ContactsController.this.m146lambda$loadContacts$27$orgtelegrammessengerContactsController(hash, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadContacts$27$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m146lambda$loadContacts$27$orgtelegrammessengerContactsController(long hash, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.contacts_Contacts res = (TLRPC.contacts_Contacts) response;
            if (hash != 0 && (res instanceof TLRPC.TL_contacts_contactsNotModified)) {
                this.contactsLoaded = true;
                if (!this.delayedContactsUpdate.isEmpty() && this.contactsBookLoaded) {
                    applyContactsUpdates(this.delayedContactsUpdate, null, null, null);
                    this.delayedContactsUpdate.clear();
                }
                getUserConfig().lastContactsSyncTime = (int) (System.currentTimeMillis() / 1000);
                getUserConfig().saveConfig(false);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda55
                    @Override // java.lang.Runnable
                    public final void run() {
                        ContactsController.this.m145lambda$loadContacts$26$orgtelegrammessengerContactsController();
                    }
                });
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("load contacts don't change");
                    return;
                }
                return;
            }
            getUserConfig().contactsSavedCount = res.saved_count;
            getUserConfig().saveConfig(false);
            processLoadedContacts(res.contacts, res.users, 0);
        }
    }

    /* renamed from: lambda$loadContacts$26$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m145lambda$loadContacts$26$orgtelegrammessengerContactsController() {
        synchronized (this.loadContactsSync) {
            this.loadingContacts = false;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    public void processLoadedContacts(final ArrayList<TLRPC.TL_contact> contactsArr, final ArrayList<TLRPC.User> usersArr, final int from) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m177x3bbda006(usersArr, from, contactsArr);
            }
        });
    }

    /* renamed from: lambda$processLoadedContacts$37$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m177x3bbda006(final ArrayList usersArr, final int from, final ArrayList contactsArr) {
        getMessagesController().putUsers(usersArr, from == 1);
        final LongSparseArray<TLRPC.User> usersDict = new LongSparseArray<>();
        final boolean isEmpty = contactsArr.isEmpty();
        if (from == 2 && !this.contacts.isEmpty()) {
            int a = 0;
            while (a < contactsArr.size()) {
                TLRPC.TL_contact contact = (TLRPC.TL_contact) contactsArr.get(a);
                if (this.contactsDict.get(Long.valueOf(contact.user_id)) != null) {
                    contactsArr.remove(a);
                    a--;
                }
                a++;
            }
            contactsArr.addAll(this.contacts);
        }
        for (int a2 = 0; a2 < contactsArr.size(); a2++) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(((TLRPC.TL_contact) contactsArr.get(a2)).user_id));
            if (user != null) {
                usersDict.put(user.id, user);
            }
        }
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m176x567c3145(from, contactsArr, usersDict, usersArr, isEmpty);
            }
        });
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.concurrent.ConcurrentHashMap != java.util.concurrent.ConcurrentHashMap<java.lang.Long, org.telegram.tgnet.TLRPC$TL_contact> */
    /* renamed from: lambda$processLoadedContacts$36$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m176x567c3145(final int from, final ArrayList contactsArr, final LongSparseArray usersDict, ArrayList usersArr, final boolean isEmpty) {
        HashMap<String, TLRPC.TL_contact> contactsByPhonesDict;
        HashMap<String, TLRPC.TL_contact> contactsByPhonesDict2;
        HashMap<String, TLRPC.TL_contact> contactsByPhonesShortDict;
        HashMap<String, TLRPC.TL_contact> contactsByPhonesDict3;
        String key;
        ArrayList<TLRPC.TL_contact> arr;
        ArrayList arrayList = contactsArr;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("done loading contacts");
        }
        if (from == 1 && (contactsArr.isEmpty() || Math.abs((System.currentTimeMillis() / 1000) - getUserConfig().lastContactsSyncTime) >= 86400)) {
            loadContacts(false, getContactsHash(arrayList));
            if (contactsArr.isEmpty()) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda62
                    @Override // java.lang.Runnable
                    public final void run() {
                        ContactsController.this.m171x5e12a568();
                    }
                });
                return;
            }
        }
        if (from == 0) {
            getUserConfig().lastContactsSyncTime = (int) (System.currentTimeMillis() / 1000);
            getUserConfig().saveConfig(false);
        }
        for (int a = 0; a < contactsArr.size(); a++) {
            TLRPC.TL_contact contact = arrayList.get(a);
            if (usersDict.get(contact.user_id) == null && contact.user_id != getUserConfig().getClientUserId()) {
                loadContacts(false, 0L);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("contacts are broken, load from server");
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda63
                    @Override // java.lang.Runnable
                    public final void run() {
                        ContactsController.this.m172x43541429();
                    }
                });
                return;
            }
        }
        if (from != 1) {
            getMessagesStorage().putUsersAndChats(usersArr, null, true, true);
            getMessagesStorage().putContacts(arrayList, from != 2);
        }
        Collections.sort(arrayList, new Comparator() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda38
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return ContactsController.lambda$processLoadedContacts$30(LongSparseArray.this, (TLRPC.TL_contact) obj, (TLRPC.TL_contact) obj2);
            }
        });
        final ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap(20, 1.0f, 2);
        final HashMap<String, ArrayList<TLRPC.TL_contact>> sectionsDict = new HashMap<>();
        final HashMap<String, ArrayList<TLRPC.TL_contact>> sectionsDictMutual = new HashMap<>();
        final ArrayList<String> sortedSectionsArray = new ArrayList<>();
        final ArrayList<String> sortedSectionsArrayMutual = new ArrayList<>();
        HashMap<String, TLRPC.TL_contact> contactsByPhonesShortDict2 = null;
        if (this.contactsBookLoaded) {
            contactsByPhonesDict = null;
        } else {
            HashMap<String, TLRPC.TL_contact> contactsByPhonesDict4 = new HashMap<>();
            contactsByPhonesShortDict2 = new HashMap<>();
            contactsByPhonesDict = contactsByPhonesDict4;
        }
        HashMap<String, TLRPC.TL_contact> contactsByPhonesDictFinal = contactsByPhonesDict;
        final HashMap<String, TLRPC.TL_contact> contactsByPhonesShortDictFinal = contactsByPhonesShortDict2;
        int a2 = 0;
        while (a2 < contactsArr.size()) {
            TLRPC.TL_contact value = arrayList.get(a2);
            HashMap<String, TLRPC.TL_contact> contactsByPhonesDict5 = contactsByPhonesDict;
            TLRPC.User user = (TLRPC.User) usersDict.get(value.user_id);
            if (user == null) {
                contactsByPhonesShortDict = contactsByPhonesShortDict2;
                contactsByPhonesDict3 = contactsByPhonesDict5;
                contactsByPhonesDict2 = contactsByPhonesDictFinal;
            } else {
                concurrentHashMap.put(Long.valueOf(value.user_id), value);
                if (contactsByPhonesDict5 == null || TextUtils.isEmpty(user.phone)) {
                    contactsByPhonesDict3 = contactsByPhonesDict5;
                    contactsByPhonesDict2 = contactsByPhonesDictFinal;
                } else {
                    contactsByPhonesDict3 = contactsByPhonesDict5;
                    contactsByPhonesDict3.put(user.phone, value);
                    contactsByPhonesDict2 = contactsByPhonesDictFinal;
                    contactsByPhonesShortDict2.put(user.phone.substring(Math.max(0, user.phone.length() - 7)), value);
                }
                String key2 = UserObject.getFirstName(user);
                if (key2.length() > 1) {
                    key2 = key2.substring(0, 1);
                }
                if (key2.length() == 0) {
                    key = "#";
                } else {
                    key = key2.toUpperCase();
                }
                String replace = this.sectionsToReplace.get(key);
                if (replace != null) {
                    key = replace;
                }
                ArrayList<TLRPC.TL_contact> arr2 = sectionsDict.get(key);
                if (arr2 != null) {
                    arr = arr2;
                } else {
                    arr = new ArrayList<>();
                    sectionsDict.put(key, arr);
                    sortedSectionsArray.add(key);
                }
                arr.add(value);
                contactsByPhonesShortDict = contactsByPhonesShortDict2;
                if (user.mutual_contact) {
                    ArrayList<TLRPC.TL_contact> arr3 = sectionsDictMutual.get(key);
                    if (arr3 == null) {
                        arr3 = new ArrayList<>();
                        sectionsDictMutual.put(key, arr3);
                        sortedSectionsArrayMutual.add(key);
                    }
                    arr3.add(value);
                }
            }
            a2++;
            arrayList = contactsArr;
            contactsByPhonesDict = contactsByPhonesDict3;
            contactsByPhonesShortDict2 = contactsByPhonesShortDict;
            contactsByPhonesDictFinal = contactsByPhonesDict2;
        }
        final HashMap<String, TLRPC.TL_contact> contactsByPhonesDictFinal2 = contactsByPhonesDictFinal;
        Collections.sort(sortedSectionsArray, ContactsController$$ExternalSyntheticLambda42.INSTANCE);
        Collections.sort(sortedSectionsArrayMutual, ContactsController$$ExternalSyntheticLambda43.INSTANCE);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m173xa6b7e502(contactsArr, concurrentHashMap, sectionsDict, sectionsDictMutual, sortedSectionsArray, sortedSectionsArrayMutual, from, isEmpty);
            }
        });
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded && this.contactsBookLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, null, null, null);
            this.delayedContactsUpdate.clear();
        }
        if (contactsByPhonesDictFinal2 != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda24
                @Override // java.lang.Runnable
                public final void run() {
                    ContactsController.this.m175x713ac284(contactsByPhonesDictFinal2, contactsByPhonesShortDictFinal);
                }
            });
        } else {
            this.contactsLoaded = true;
        }
    }

    /* renamed from: lambda$processLoadedContacts$28$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m171x5e12a568() {
        this.doneLoadingContacts = true;
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    /* renamed from: lambda$processLoadedContacts$29$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m172x43541429() {
        this.doneLoadingContacts = true;
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    public static /* synthetic */ int lambda$processLoadedContacts$30(LongSparseArray usersDict, TLRPC.TL_contact tl_contact, TLRPC.TL_contact tl_contact2) {
        TLRPC.User user1 = (TLRPC.User) usersDict.get(tl_contact.user_id);
        TLRPC.User user2 = (TLRPC.User) usersDict.get(tl_contact2.user_id);
        String name1 = UserObject.getFirstName(user1);
        String name2 = UserObject.getFirstName(user2);
        return name1.compareTo(name2);
    }

    public static /* synthetic */ int lambda$processLoadedContacts$31(String s, String s2) {
        char cv1 = s.charAt(0);
        char cv2 = s2.charAt(0);
        if (cv1 == '#') {
            return 1;
        }
        if (cv2 == '#') {
            return -1;
        }
        return s.compareTo(s2);
    }

    public static /* synthetic */ int lambda$processLoadedContacts$32(String s, String s2) {
        char cv1 = s.charAt(0);
        char cv2 = s2.charAt(0);
        if (cv1 == '#') {
            return 1;
        }
        if (cv2 == '#') {
            return -1;
        }
        return s.compareTo(s2);
    }

    /* renamed from: lambda$processLoadedContacts$33$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m173xa6b7e502(ArrayList contactsArr, ConcurrentHashMap contactsDictionary, HashMap sectionsDict, HashMap sectionsDictMutual, ArrayList sortedSectionsArray, ArrayList sortedSectionsArrayMutual, int from, boolean isEmpty) {
        this.contacts = contactsArr;
        this.contactsDict = contactsDictionary;
        this.usersSectionsDict = sectionsDict;
        this.usersMutualSectionsDict = sectionsDictMutual;
        this.sortedUsersSectionsArray = sortedSectionsArray;
        this.sortedUsersMutualSectionsArray = sortedSectionsArrayMutual;
        this.doneLoadingContacts = true;
        if (from != 2) {
            synchronized (this.loadContactsSync) {
                this.loadingContacts = false;
            }
        }
        performWriteContactsToPhoneBook();
        updateUnregisteredContacts();
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        if (from != 1 && !isEmpty) {
            saveContactsLoadTime();
        } else {
            reloadContactsStatusesMaybe();
        }
    }

    /* renamed from: lambda$processLoadedContacts$35$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m175x713ac284(final HashMap contactsByPhonesDictFinal, final HashMap contactsByPhonesShortDictFinal) {
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda23
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m174x8bf953c3(contactsByPhonesDictFinal, contactsByPhonesShortDictFinal);
            }
        });
        if (this.contactsSyncInProgress) {
            return;
        }
        this.contactsSyncInProgress = true;
        getMessagesStorage().getCachedPhoneBook(false);
    }

    /* renamed from: lambda$processLoadedContacts$34$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m174x8bf953c3(HashMap contactsByPhonesDictFinal, HashMap contactsByPhonesShortDictFinal) {
        this.contactsByPhone = contactsByPhonesDictFinal;
        this.contactsByShortPhone = contactsByPhonesShortDictFinal;
    }

    public boolean isContact(long userId) {
        return this.contactsDict.get(Long.valueOf(userId)) != null;
    }

    public void reloadContactsStatusesMaybe() {
        try {
            SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
            long lastReloadStatusTime = preferences.getLong("lastReloadStatusTime", 0L);
            if (lastReloadStatusTime < System.currentTimeMillis() - 10800000) {
                reloadContactsStatuses();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void saveContactsLoadTime() {
        try {
            SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
            preferences.edit().putLong("lastReloadStatusTime", System.currentTimeMillis()).commit();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: mergePhonebookAndTelegramContacts */
    public void m167x726ee654(final HashMap<String, ArrayList<Object>> phoneBookSectionsDictFinal, final ArrayList<String> phoneBookSectionsArrayFinal, final HashMap<String, Contact> phoneBookByShortPhonesFinal) {
        final ArrayList<TLRPC.TL_contact> contactsCopy = new ArrayList<>(this.contacts);
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m154xcadb3f5a(contactsCopy, phoneBookByShortPhonesFinal, phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal);
            }
        });
    }

    /* renamed from: lambda$mergePhonebookAndTelegramContacts$41$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m154xcadb3f5a(ArrayList contactsCopy, HashMap phoneBookByShortPhonesFinal, final HashMap phoneBookSectionsDictFinal, final ArrayList phoneBookSectionsArrayFinal) {
        int size = contactsCopy.size();
        for (int a = 0; a < size; a++) {
            TLRPC.TL_contact value = (TLRPC.TL_contact) contactsCopy.get(a);
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(value.user_id));
            if (user != null && !TextUtils.isEmpty(user.phone)) {
                String phone = user.phone.substring(Math.max(0, user.phone.length() - 7));
                Contact contact = (Contact) phoneBookByShortPhonesFinal.get(phone);
                if (contact != null) {
                    if (contact.user == null) {
                        contact.user = user;
                    }
                } else {
                    String key = Contact.getLetter(user.first_name, user.last_name);
                    ArrayList<Object> arrayList = (ArrayList) phoneBookSectionsDictFinal.get(key);
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                        phoneBookSectionsDictFinal.put(key, arrayList);
                        phoneBookSectionsArrayFinal.add(key);
                    }
                    arrayList.add(user);
                }
            }
        }
        for (ArrayList<Object> arrayList2 : phoneBookSectionsDictFinal.values()) {
            Collections.sort(arrayList2, ContactsController$$ExternalSyntheticLambda47.INSTANCE);
        }
        Collections.sort(phoneBookSectionsArrayFinal, ContactsController$$ExternalSyntheticLambda41.INSTANCE);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m153xe599d099(phoneBookSectionsArrayFinal, phoneBookSectionsDictFinal);
            }
        });
    }

    public static /* synthetic */ int lambda$mergePhonebookAndTelegramContacts$38(Object o1, Object o2) {
        String name1;
        String name2;
        if (o1 instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) o1;
            name1 = formatName(user.first_name, user.last_name);
        } else if (o1 instanceof Contact) {
            Contact contact = (Contact) o1;
            if (contact.user != null) {
                name1 = formatName(contact.user.first_name, contact.user.last_name);
            } else {
                String name12 = contact.first_name;
                name1 = formatName(name12, contact.last_name);
            }
        } else {
            name1 = "";
        }
        if (o2 instanceof TLRPC.User) {
            TLRPC.User user2 = (TLRPC.User) o2;
            name2 = formatName(user2.first_name, user2.last_name);
        } else if (o2 instanceof Contact) {
            Contact contact2 = (Contact) o2;
            if (contact2.user != null) {
                name2 = formatName(contact2.user.first_name, contact2.user.last_name);
            } else {
                String name22 = contact2.first_name;
                name2 = formatName(name22, contact2.last_name);
            }
        } else {
            name2 = "";
        }
        return name1.compareTo(name2);
    }

    public static /* synthetic */ int lambda$mergePhonebookAndTelegramContacts$39(String s, String s2) {
        char cv1 = s.charAt(0);
        char cv2 = s2.charAt(0);
        if (cv1 == '#') {
            return 1;
        }
        if (cv2 == '#') {
            return -1;
        }
        return s.compareTo(s2);
    }

    /* renamed from: lambda$mergePhonebookAndTelegramContacts$40$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m153xe599d099(ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookSectionsDictFinal) {
        this.phoneBookSectionsArray = phoneBookSectionsArrayFinal;
        this.phoneBookSectionsDict = phoneBookSectionsDictFinal;
    }

    private void updateUnregisteredContacts() {
        HashMap<String, TLRPC.TL_contact> contactsPhonesShort = new HashMap<>();
        int size = this.contacts.size();
        for (int a = 0; a < size; a++) {
            TLRPC.TL_contact value = this.contacts.get(a);
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(value.user_id));
            if (user != null && !TextUtils.isEmpty(user.phone)) {
                contactsPhonesShort.put(user.phone, value);
            }
        }
        ArrayList<Contact> sortedPhoneBookContacts = new ArrayList<>();
        for (Map.Entry<String, Contact> pair : this.contactsBook.entrySet()) {
            Contact value2 = pair.getValue();
            boolean skip = false;
            for (int a2 = 0; a2 < value2.phones.size(); a2++) {
                String sphone = value2.shortPhones.get(a2);
                if (contactsPhonesShort.containsKey(sphone) || value2.phoneDeleted.get(a2).intValue() == 1) {
                    skip = true;
                    break;
                }
            }
            if (!skip) {
                sortedPhoneBookContacts.add(value2);
            }
        }
        Collections.sort(sortedPhoneBookContacts, ContactsController$$ExternalSyntheticLambda45.INSTANCE);
        this.phoneBookContacts = sortedPhoneBookContacts;
    }

    public static /* synthetic */ int lambda$updateUnregisteredContacts$42(Contact contact, Contact contact2) {
        String toComapre1 = contact.first_name;
        if (toComapre1.length() == 0) {
            toComapre1 = contact.last_name;
        }
        String toComapre2 = contact2.first_name;
        if (toComapre2.length() == 0) {
            toComapre2 = contact2.last_name;
        }
        return toComapre1.compareTo(toComapre2);
    }

    private void buildContactsSectionsArrays(boolean sort) {
        String key;
        if (sort) {
            Collections.sort(this.contacts, new Comparator() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda39
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return ContactsController.this.m134x277ef5f((TLRPC.TL_contact) obj, (TLRPC.TL_contact) obj2);
                }
            });
        }
        HashMap<String, ArrayList<TLRPC.TL_contact>> sectionsDict = new HashMap<>();
        ArrayList<String> sortedSectionsArray = new ArrayList<>();
        for (int a = 0; a < this.contacts.size(); a++) {
            TLRPC.TL_contact value = this.contacts.get(a);
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(value.user_id));
            if (user != null) {
                String key2 = UserObject.getFirstName(user);
                if (key2.length() > 1) {
                    key2 = key2.substring(0, 1);
                }
                if (key2.length() == 0) {
                    key = "#";
                } else {
                    key = key2.toUpperCase();
                }
                String replace = this.sectionsToReplace.get(key);
                if (replace != null) {
                    key = replace;
                }
                ArrayList<TLRPC.TL_contact> arr = sectionsDict.get(key);
                if (arr == null) {
                    arr = new ArrayList<>();
                    sectionsDict.put(key, arr);
                    sortedSectionsArray.add(key);
                }
                arr.add(value);
            }
        }
        Collections.sort(sortedSectionsArray, ContactsController$$ExternalSyntheticLambda40.INSTANCE);
        this.usersSectionsDict = sectionsDict;
        this.sortedUsersSectionsArray = sortedSectionsArray;
    }

    /* renamed from: lambda$buildContactsSectionsArrays$43$org-telegram-messenger-ContactsController */
    public /* synthetic */ int m134x277ef5f(TLRPC.TL_contact tl_contact, TLRPC.TL_contact tl_contact2) {
        TLRPC.User user1 = getMessagesController().getUser(Long.valueOf(tl_contact.user_id));
        TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(tl_contact2.user_id));
        String name1 = UserObject.getFirstName(user1);
        String name2 = UserObject.getFirstName(user2);
        return name1.compareTo(name2);
    }

    public static /* synthetic */ int lambda$buildContactsSectionsArrays$44(String s, String s2) {
        char cv1 = s.charAt(0);
        char cv2 = s2.charAt(0);
        if (cv1 == '#') {
            return 1;
        }
        if (cv2 == '#') {
            return -1;
        }
        return s.compareTo(s2);
    }

    private boolean hasContactsPermission() {
        Cursor cursor;
        if (Build.VERSION.SDK_INT >= 23) {
            return ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_CONTACTS") == 0;
        }
        try {
            ContentResolver cr = ApplicationLoader.applicationContext.getContentResolver();
            cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, this.projectionPhones, null, null, null);
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (cursor != null && cursor.getCount() != 0) {
            if (cursor != null) {
                cursor.close();
            }
            return true;
        }
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        return false;
    }

    /* renamed from: performWriteContactsToPhoneBookInternal */
    public void m170x8296d0df(ArrayList<TLRPC.TL_contact> contactsArray) {
        Cursor cursor = null;
        try {
            try {
            } catch (Exception e) {
                FileLog.e(e);
                if (cursor == null) {
                    return;
                }
            }
            if (!hasContactsPermission()) {
                if (0 == 0) {
                    return;
                }
                cursor.close();
                return;
            }
            SharedPreferences settings = MessagesController.getMainSettings(this.currentAccount);
            boolean forceUpdate = !settings.getBoolean("contacts_updated_v7", false);
            if (forceUpdate) {
                settings.edit().putBoolean("contacts_updated_v7", true).commit();
            }
            ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
            Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build();
            cursor = contentResolver.query(rawContactUri, new String[]{"_id", "sync2"}, null, null, null);
            LongSparseArray<Long> bookContacts = new LongSparseArray<>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    bookContacts.put(cursor.getLong(1), Long.valueOf(cursor.getLong(0)));
                }
                cursor.close();
                cursor = null;
                for (int a = 0; a < contactsArray.size(); a++) {
                    TLRPC.TL_contact u = contactsArray.get(a);
                    if (forceUpdate || bookContacts.indexOfKey(u.user_id) < 0) {
                        addContactToPhoneBook(getMessagesController().getUser(Long.valueOf(u.user_id)), forceUpdate);
                    }
                }
            }
            if (cursor == null) {
                return;
            }
            cursor.close();
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    private void performWriteContactsToPhoneBook() {
        final ArrayList<TLRPC.TL_contact> contactsArray = new ArrayList<>(this.contacts);
        Utilities.phoneBookQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m170x8296d0df(contactsArray);
            }
        });
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.concurrent.ConcurrentHashMap != java.util.concurrent.ConcurrentHashMap<java.lang.Long, org.telegram.tgnet.TLRPC$User> */
    private void applyContactsUpdates(ArrayList<Long> ids, ConcurrentHashMap<Long, TLRPC.User> concurrentHashMap, ArrayList<TLRPC.TL_contact> newC, ArrayList<Long> contactsTD) {
        ArrayList<Long> contactsTD2;
        ArrayList<TLRPC.TL_contact> newC2;
        int i;
        boolean z;
        int index;
        int index2;
        if (newC == null || contactsTD == null) {
            newC2 = new ArrayList<>();
            contactsTD2 = new ArrayList<>();
            for (int a = 0; a < ids.size(); a++) {
                Long uid = ids.get(a);
                if (uid.longValue() > 0) {
                    TLRPC.TL_contact contact = new TLRPC.TL_contact();
                    contact.user_id = uid.longValue();
                    newC2.add(contact);
                } else if (uid.longValue() < 0) {
                    contactsTD2.add(Long.valueOf(-uid.longValue()));
                }
            }
        } else {
            newC2 = newC;
            contactsTD2 = contactsTD;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("process update - contacts add = " + newC2.size() + " delete = " + contactsTD2.size());
        }
        StringBuilder toAdd = new StringBuilder();
        StringBuilder toDelete = new StringBuilder();
        boolean reloadContacts = false;
        int a2 = 0;
        while (true) {
            i = -1;
            z = true;
            if (a2 >= newC2.size()) {
                break;
            }
            TLRPC.TL_contact newContact = newC2.get(a2);
            TLRPC.User user = null;
            if (concurrentHashMap != null) {
                TLRPC.User user2 = concurrentHashMap.get(Long.valueOf(newContact.user_id));
                user = user2;
            }
            if (user == null) {
                user = getMessagesController().getUser(Long.valueOf(newContact.user_id));
            } else {
                getMessagesController().putUser(user, true);
            }
            if (user == null || TextUtils.isEmpty(user.phone)) {
                reloadContacts = true;
            } else {
                Contact contact2 = this.contactsBookSPhones.get(user.phone);
                if (contact2 != null && (index2 = contact2.shortPhones.indexOf(user.phone)) != -1) {
                    contact2.phoneDeleted.set(index2, 0);
                }
                if (toAdd.length() != 0) {
                    toAdd.append(",");
                }
                toAdd.append(user.phone);
            }
            a2++;
        }
        int a3 = 0;
        while (a3 < contactsTD2.size()) {
            final Long uid2 = contactsTD2.get(a3);
            Utilities.phoneBookQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    ContactsController.this.m131x52aad668(uid2);
                }
            });
            TLRPC.User user3 = null;
            if (concurrentHashMap != null) {
                TLRPC.User user4 = concurrentHashMap.get(uid2);
                user3 = user4;
            }
            if (user3 == null) {
                user3 = getMessagesController().getUser(uid2);
            } else {
                getMessagesController().putUser(user3, z);
            }
            if (user3 == null) {
                reloadContacts = true;
            } else if (!TextUtils.isEmpty(user3.phone)) {
                Contact contact3 = this.contactsBookSPhones.get(user3.phone);
                if (contact3 != null && (index = contact3.shortPhones.indexOf(user3.phone)) != i) {
                    contact3.phoneDeleted.set(index, 1);
                }
                if (toDelete.length() != 0) {
                    toDelete.append(",");
                }
                toDelete.append(user3.phone);
            }
            a3++;
            i = -1;
            z = true;
        }
        int a4 = toAdd.length();
        if (a4 != 0 || toDelete.length() != 0) {
            getMessagesStorage().applyPhoneBookUpdates(toAdd.toString(), toDelete.toString());
        }
        if (reloadContacts) {
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    ContactsController.this.m132x37ec4529();
                }
            });
            return;
        }
        final ArrayList<TLRPC.TL_contact> newContacts = newC2;
        final ArrayList<Long> contactsToDelete = contactsTD2;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m133x1d2db3ea(newContacts, contactsToDelete);
            }
        });
    }

    /* renamed from: lambda$applyContactsUpdates$46$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m131x52aad668(Long uid) {
        deleteContactFromPhoneBook(uid.longValue());
    }

    /* renamed from: lambda$applyContactsUpdates$47$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m132x37ec4529() {
        loadContacts(false, 0L);
    }

    /* renamed from: lambda$applyContactsUpdates$48$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m133x1d2db3ea(ArrayList newContacts, ArrayList contactsToDelete) {
        for (int a = 0; a < newContacts.size(); a++) {
            TLRPC.TL_contact contact = (TLRPC.TL_contact) newContacts.get(a);
            if (this.contactsDict.get(Long.valueOf(contact.user_id)) == null) {
                this.contacts.add(contact);
                this.contactsDict.put(Long.valueOf(contact.user_id), contact);
            }
        }
        for (int a2 = 0; a2 < contactsToDelete.size(); a2++) {
            Long uid = (Long) contactsToDelete.get(a2);
            TLRPC.TL_contact contact2 = this.contactsDict.get(uid);
            if (contact2 != null) {
                this.contacts.remove(contact2);
                this.contactsDict.remove(uid);
            }
        }
        if (!newContacts.isEmpty()) {
            updateUnregisteredContacts();
            performWriteContactsToPhoneBook();
        }
        performSyncPhoneBook(getContactsCopy(this.contactsBook), false, false, false, false, true, false);
        buildContactsSectionsArrays(!newContacts.isEmpty());
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.concurrent.ConcurrentHashMap != java.util.concurrent.ConcurrentHashMap<java.lang.Long, org.telegram.tgnet.TLRPC$User> */
    public void processContactsUpdates(ArrayList<Long> ids, ConcurrentHashMap<Long, TLRPC.User> concurrentHashMap) {
        int idx;
        int idx2;
        ArrayList<TLRPC.TL_contact> newContacts = new ArrayList<>();
        ArrayList<Long> contactsToDelete = new ArrayList<>();
        Iterator<Long> it = ids.iterator();
        while (it.hasNext()) {
            Long uid = it.next();
            if (uid.longValue() > 0) {
                TLRPC.TL_contact contact = new TLRPC.TL_contact();
                contact.user_id = uid.longValue();
                newContacts.add(contact);
                if (!this.delayedContactsUpdate.isEmpty() && (idx = this.delayedContactsUpdate.indexOf(Long.valueOf(-uid.longValue()))) != -1) {
                    this.delayedContactsUpdate.remove(idx);
                }
            } else if (uid.longValue() < 0) {
                contactsToDelete.add(Long.valueOf(-uid.longValue()));
                if (!this.delayedContactsUpdate.isEmpty() && (idx2 = this.delayedContactsUpdate.indexOf(Long.valueOf(-uid.longValue()))) != -1) {
                    this.delayedContactsUpdate.remove(idx2);
                }
            }
        }
        if (!contactsToDelete.isEmpty()) {
            getMessagesStorage().deleteContacts(contactsToDelete);
        }
        if (!newContacts.isEmpty()) {
            getMessagesStorage().putContacts(newContacts, false);
        }
        if (!this.contactsLoaded || !this.contactsBookLoaded) {
            this.delayedContactsUpdate.addAll(ids);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("delay update - contacts add = " + newContacts.size() + " delete = " + contactsToDelete.size());
                return;
            }
            return;
        }
        applyContactsUpdates(ids, concurrentHashMap, newContacts, contactsToDelete);
    }

    public long addContactToPhoneBook(TLRPC.User user, boolean check) {
        String phoneOrName;
        long res;
        if (this.systemAccount == null || user == null || !hasContactsPermission()) {
            return -1L;
        }
        long res2 = -1;
        synchronized (this.observerLock) {
            this.ignoreChanges = true;
        }
        ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
        if (check) {
            try {
                Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build();
                contentResolver.delete(rawContactUri, "sync2 = " + user.id, null);
            } catch (Exception e) {
            }
        }
        ArrayList<ContentProviderOperation> query = new ArrayList<>();
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI);
        builder.withValue("account_name", this.systemAccount.name);
        builder.withValue("account_type", this.systemAccount.type);
        builder.withValue("sync1", TextUtils.isEmpty(user.phone) ? "" : user.phone);
        builder.withValue("sync2", Long.valueOf(user.id));
        query.add(builder.build());
        ContentProviderOperation.Builder builder2 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder2.withValueBackReference("raw_contact_id", 0);
        builder2.withValue("mimetype", "vnd.android.cursor.item/name");
        builder2.withValue("data2", user.first_name);
        builder2.withValue("data3", user.last_name);
        query.add(builder2.build());
        if (TextUtils.isEmpty(user.phone)) {
            phoneOrName = formatName(user.first_name, user.last_name);
        } else {
            phoneOrName = "+" + user.phone;
        }
        ContentProviderOperation.Builder builder3 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder3.withValueBackReference("raw_contact_id", 0);
        builder3.withValue("mimetype", "vnd.android.cursor.item/vnd.org.telegram.messenger.android.profile");
        builder3.withValue("data1", Long.valueOf(user.id));
        builder3.withValue("data2", "Telegram Profile");
        builder3.withValue("data3", LocaleController.formatString("ContactShortcutMessage", org.telegram.messenger.beta.R.string.ContactShortcutMessage, phoneOrName));
        builder3.withValue("data4", Long.valueOf(user.id));
        query.add(builder3.build());
        ContentProviderOperation.Builder builder4 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder4.withValueBackReference("raw_contact_id", 0);
        builder4.withValue("mimetype", "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call");
        builder4.withValue("data1", Long.valueOf(user.id));
        builder4.withValue("data2", "Telegram Voice Call");
        builder4.withValue("data3", LocaleController.formatString("ContactShortcutVoiceCall", org.telegram.messenger.beta.R.string.ContactShortcutVoiceCall, phoneOrName));
        builder4.withValue("data4", Long.valueOf(user.id));
        query.add(builder4.build());
        ContentProviderOperation.Builder builder5 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder5.withValueBackReference("raw_contact_id", 0);
        builder5.withValue("mimetype", "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call.video");
        builder5.withValue("data1", Long.valueOf(user.id));
        builder5.withValue("data2", "Telegram Video Call");
        builder5.withValue("data3", LocaleController.formatString("ContactShortcutVideoCall", org.telegram.messenger.beta.R.string.ContactShortcutVideoCall, phoneOrName));
        builder5.withValue("data4", Long.valueOf(user.id));
        query.add(builder5.build());
        try {
            ContentProviderResult[] result = contentResolver.applyBatch("com.android.contacts", query);
            if (result != null && result.length > 0 && result[0].uri != null) {
                res2 = Long.parseLong(result[0].uri.getLastPathSegment());
            }
            res = res2;
        } catch (Exception e2) {
            res = -1;
        }
        synchronized (this.observerLock) {
            this.ignoreChanges = false;
        }
        return res;
    }

    private void deleteContactFromPhoneBook(long uid) {
        if (!hasContactsPermission()) {
            return;
        }
        synchronized (this.observerLock) {
            this.ignoreChanges = true;
        }
        try {
            ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
            Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build();
            contentResolver.delete(rawContactUri, "sync2 = " + uid, null);
        } catch (Exception e) {
            FileLog.e(e);
        }
        synchronized (this.observerLock) {
            this.ignoreChanges = false;
        }
    }

    public void markAsContacted(final String contactId) {
        if (contactId == null) {
            return;
        }
        Utilities.phoneBookQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.lambda$markAsContacted$49(contactId);
            }
        });
    }

    public static /* synthetic */ void lambda$markAsContacted$49(String contactId) {
        Uri uri = Uri.parse(contactId);
        ContentValues values = new ContentValues();
        values.put("last_time_contacted", Long.valueOf(System.currentTimeMillis()));
        ContentResolver cr = ApplicationLoader.applicationContext.getContentResolver();
        cr.update(uri, values, null, null);
    }

    public void addContact(final TLRPC.User user, boolean exception) {
        if (user == null) {
            return;
        }
        TLRPC.TL_contacts_addContact req = new TLRPC.TL_contacts_addContact();
        req.id = getMessagesController().getInputUser(user);
        req.first_name = user.first_name;
        req.last_name = user.last_name;
        req.phone = user.phone;
        req.add_phone_privacy_exception = exception;
        if (req.phone == null) {
            req.phone = "";
        } else if (req.phone.length() > 0 && !req.phone.startsWith("+")) {
            req.phone = "+" + req.phone;
        }
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda58
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ContactsController.this.m130lambda$addContact$52$orgtelegrammessengerContactsController(user, tLObject, tL_error);
            }
        }, 6);
    }

    /* renamed from: lambda$addContact$52$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m130lambda$addContact$52$orgtelegrammessengerContactsController(TLRPC.User user, TLObject response, TLRPC.TL_error error) {
        int index;
        if (error != null) {
            return;
        }
        final TLRPC.Updates res = (TLRPC.Updates) response;
        getMessagesController().processUpdates(res, false);
        for (int a = 0; a < res.users.size(); a++) {
            final TLRPC.User u = res.users.get(a);
            if (u.id == user.id) {
                Utilities.phoneBookQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda37
                    @Override // java.lang.Runnable
                    public final void run() {
                        ContactsController.this.m128lambda$addContact$50$orgtelegrammessengerContactsController(u);
                    }
                });
                TLRPC.TL_contact newContact = new TLRPC.TL_contact();
                newContact.user_id = u.id;
                ArrayList<TLRPC.TL_contact> arrayList = new ArrayList<>();
                arrayList.add(newContact);
                getMessagesStorage().putContacts(arrayList, false);
                if (!TextUtils.isEmpty(u.phone)) {
                    formatName(u.first_name, u.last_name);
                    getMessagesStorage().applyPhoneBookUpdates(u.phone, "");
                    Contact contact = this.contactsBookSPhones.get(u.phone);
                    if (contact != null && (index = contact.shortPhones.indexOf(u.phone)) != -1) {
                        contact.phoneDeleted.set(index, 0);
                    }
                }
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda36
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m129lambda$addContact$51$orgtelegrammessengerContactsController(res);
            }
        });
    }

    /* renamed from: lambda$addContact$50$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m128lambda$addContact$50$orgtelegrammessengerContactsController(TLRPC.User u) {
        addContactToPhoneBook(u, true);
    }

    /* renamed from: lambda$addContact$51$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m129lambda$addContact$51$orgtelegrammessengerContactsController(TLRPC.Updates res) {
        for (int a = 0; a < res.users.size(); a++) {
            TLRPC.User u = res.users.get(a);
            if (u.contact && this.contactsDict.get(Long.valueOf(u.id)) == null) {
                TLRPC.TL_contact newContact = new TLRPC.TL_contact();
                newContact.user_id = u.id;
                this.contacts.add(newContact);
                this.contactsDict.put(Long.valueOf(newContact.user_id), newContact);
            }
        }
        buildContactsSectionsArrays(true);
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    public void deleteContact(final ArrayList<TLRPC.User> users, final boolean showBulletin) {
        if (users == null || users.isEmpty()) {
            return;
        }
        TLRPC.TL_contacts_deleteContacts req = new TLRPC.TL_contacts_deleteContacts();
        final ArrayList<Long> uids = new ArrayList<>();
        int N = users.size();
        for (int a = 0; a < N; a++) {
            TLRPC.User user = users.get(a);
            TLRPC.InputUser inputUser = getMessagesController().getInputUser(user);
            if (inputUser != null) {
                user.contact = false;
                uids.add(Long.valueOf(user.id));
                req.id.add(inputUser);
            }
        }
        final String userName = users.get(0).first_name;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda56
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ContactsController.this.m143x6de2e0d4(uids, users, showBulletin, userName, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$deleteContact$55$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m143x6de2e0d4(ArrayList uids, final ArrayList users, final boolean showBulletin, final String userName, TLObject response, TLRPC.TL_error error) {
        int index;
        if (error != null) {
            return;
        }
        getMessagesController().processUpdates((TLRPC.Updates) response, false);
        getMessagesStorage().deleteContacts(uids);
        Utilities.phoneBookQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m141xa3600352(users);
            }
        });
        for (int a = 0; a < users.size(); a++) {
            TLRPC.User user = (TLRPC.User) users.get(a);
            if (!TextUtils.isEmpty(user.phone)) {
                getMessagesStorage().applyPhoneBookUpdates(user.phone, "");
                Contact contact = this.contactsBookSPhones.get(user.phone);
                if (contact != null && (index = contact.shortPhones.indexOf(user.phone)) != -1) {
                    contact.phoneDeleted.set(index, 1);
                }
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m142x88a17213(users, showBulletin, userName);
            }
        });
    }

    /* renamed from: lambda$deleteContact$53$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m141xa3600352(ArrayList users) {
        Iterator it = users.iterator();
        while (it.hasNext()) {
            TLRPC.User user = (TLRPC.User) it.next();
            deleteContactFromPhoneBook(user.id);
        }
    }

    /* renamed from: lambda$deleteContact$54$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m142x88a17213(ArrayList users, boolean showBulletin, String userName) {
        boolean remove = false;
        Iterator it = users.iterator();
        while (it.hasNext()) {
            TLRPC.User user = (TLRPC.User) it.next();
            TLRPC.TL_contact contact = this.contactsDict.get(Long.valueOf(user.id));
            if (contact != null) {
                remove = true;
                this.contacts.remove(contact);
                this.contactsDict.remove(Long.valueOf(user.id));
            }
        }
        if (remove) {
            buildContactsSectionsArrays(false);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_NAME));
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        if (showBulletin) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 1, LocaleController.formatString("DeletedFromYourContacts", org.telegram.messenger.beta.R.string.DeletedFromYourContacts, userName));
        }
    }

    private void reloadContactsStatuses() {
        saveContactsLoadTime();
        getMessagesController().clearFullUsers();
        SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("needGetStatuses", true).commit();
        TLRPC.TL_contacts_getStatuses req = new TLRPC.TL_contacts_getStatuses();
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda53
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ContactsController.this.m180xfc19e105(editor, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$reloadContactsStatuses$57$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m180xfc19e105(final SharedPreferences.Editor editor, final TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    ContactsController.this.m179x16d87244(editor, response);
                }
            });
        }
    }

    /* renamed from: lambda$reloadContactsStatuses$56$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m179x16d87244(SharedPreferences.Editor editor, TLObject response) {
        editor.remove("needGetStatuses").commit();
        TLRPC.Vector vector = (TLRPC.Vector) response;
        if (!vector.objects.isEmpty()) {
            ArrayList<TLRPC.User> dbUsersStatus = new ArrayList<>();
            Iterator<Object> it = vector.objects.iterator();
            while (it.hasNext()) {
                Object object = it.next();
                TLRPC.User toDbUser = new TLRPC.TL_user();
                TLRPC.TL_contactStatus status = (TLRPC.TL_contactStatus) object;
                if (status != null) {
                    if (status.status instanceof TLRPC.TL_userStatusRecently) {
                        status.status.expires = -100;
                    } else if (status.status instanceof TLRPC.TL_userStatusLastWeek) {
                        status.status.expires = -101;
                    } else if (status.status instanceof TLRPC.TL_userStatusLastMonth) {
                        status.status.expires = -102;
                    }
                    TLRPC.User user = getMessagesController().getUser(Long.valueOf(status.user_id));
                    if (user != null) {
                        user.status = status.status;
                    }
                    toDbUser.status = status.status;
                    dbUsersStatus.add(toDbUser);
                }
            }
            getMessagesStorage().updateUsers(dbUsersStatus, true, true, true);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_STATUS));
    }

    public void loadPrivacySettings() {
        if (this.loadingDeleteInfo == 0) {
            this.loadingDeleteInfo = 1;
            getConnectionsManager().sendRequest(new TLRPC.TL_account_getAccountTTL(), new RequestDelegate() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda49
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ContactsController.this.m148x4746ce08(tLObject, tL_error);
                }
            });
        }
        if (this.loadingGlobalSettings == 0) {
            this.loadingGlobalSettings = 1;
            getConnectionsManager().sendRequest(new TLRPC.TL_account_getGlobalPrivacySettings(), new RequestDelegate() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda50
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ContactsController.this.m150xe027c15f(tLObject, tL_error);
                }
            });
        }
        int a = 0;
        while (true) {
            int[] iArr = this.loadingPrivacyInfo;
            if (a < iArr.length) {
                if (iArr[a] == 0) {
                    iArr[a] = 1;
                    final int num = a;
                    TLRPC.TL_account_getPrivacy req = new TLRPC.TL_account_getPrivacy();
                    switch (num) {
                        case 0:
                            req.key = new TLRPC.TL_inputPrivacyKeyStatusTimestamp();
                            break;
                        case 1:
                            req.key = new TLRPC.TL_inputPrivacyKeyChatInvite();
                            break;
                        case 2:
                            req.key = new TLRPC.TL_inputPrivacyKeyPhoneCall();
                            break;
                        case 3:
                            req.key = new TLRPC.TL_inputPrivacyKeyPhoneP2P();
                            break;
                        case 4:
                            req.key = new TLRPC.TL_inputPrivacyKeyProfilePhoto();
                            break;
                        case 5:
                            req.key = new TLRPC.TL_inputPrivacyKeyForwards();
                            break;
                        case 6:
                            req.key = new TLRPC.TL_inputPrivacyKeyPhoneNumber();
                            break;
                        default:
                            req.key = new TLRPC.TL_inputPrivacyKeyAddedByPhone();
                            break;
                    }
                    getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda51
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            ContactsController.this.m152xaaaa9ee1(num, tLObject, tL_error);
                        }
                    });
                }
                a++;
            } else {
                getNotificationCenter().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                return;
            }
        }
    }

    /* renamed from: lambda$loadPrivacySettings$59$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m148x4746ce08(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda31
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m147x62055f47(error, response);
            }
        });
    }

    /* renamed from: lambda$loadPrivacySettings$58$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m147x62055f47(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            TLRPC.TL_accountDaysTTL ttl = (TLRPC.TL_accountDaysTTL) response;
            this.deleteAccountTTL = ttl.days;
            this.loadingDeleteInfo = 2;
        } else {
            this.loadingDeleteInfo = 0;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
    }

    /* renamed from: lambda$loadPrivacySettings$61$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m150xe027c15f(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda32
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m149xfae6529e(error, response);
            }
        });
    }

    /* renamed from: lambda$loadPrivacySettings$60$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m149xfae6529e(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            this.globalPrivacySettings = (TLRPC.TL_globalPrivacySettings) response;
            this.loadingGlobalSettings = 2;
        } else {
            this.loadingGlobalSettings = 0;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
    }

    /* renamed from: lambda$loadPrivacySettings$63$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m152xaaaa9ee1(final int num, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ContactsController$$ExternalSyntheticLambda34
            @Override // java.lang.Runnable
            public final void run() {
                ContactsController.this.m151xc5693020(error, response, num);
            }
        });
    }

    /* renamed from: lambda$loadPrivacySettings$62$org-telegram-messenger-ContactsController */
    public /* synthetic */ void m151xc5693020(TLRPC.TL_error error, TLObject response, int num) {
        if (error != null) {
            this.loadingPrivacyInfo[num] = 0;
        } else {
            TLRPC.TL_account_privacyRules rules = (TLRPC.TL_account_privacyRules) response;
            getMessagesController().putUsers(rules.users, false);
            getMessagesController().putChats(rules.chats, false);
            switch (num) {
                case 0:
                    this.lastseenPrivacyRules = rules.rules;
                    break;
                case 1:
                    this.groupPrivacyRules = rules.rules;
                    break;
                case 2:
                    this.callPrivacyRules = rules.rules;
                    break;
                case 3:
                    this.p2pPrivacyRules = rules.rules;
                    break;
                case 4:
                    this.profilePhotoPrivacyRules = rules.rules;
                    break;
                case 5:
                    this.forwardsPrivacyRules = rules.rules;
                    break;
                case 6:
                    this.phonePrivacyRules = rules.rules;
                    break;
                default:
                    this.addedByPhonePrivacyRules = rules.rules;
                    break;
            }
            this.loadingPrivacyInfo[num] = 2;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
    }

    public void setDeleteAccountTTL(int ttl) {
        this.deleteAccountTTL = ttl;
    }

    public int getDeleteAccountTTL() {
        return this.deleteAccountTTL;
    }

    public boolean getLoadingDeleteInfo() {
        return this.loadingDeleteInfo != 2;
    }

    public boolean getLoadingGlobalSettings() {
        return this.loadingGlobalSettings != 2;
    }

    public boolean getLoadingPrivicyInfo(int type) {
        return this.loadingPrivacyInfo[type] != 2;
    }

    public TLRPC.TL_globalPrivacySettings getGlobalPrivacySettings() {
        return this.globalPrivacySettings;
    }

    public ArrayList<TLRPC.PrivacyRule> getPrivacyRules(int type) {
        switch (type) {
            case 0:
                return this.lastseenPrivacyRules;
            case 1:
                return this.groupPrivacyRules;
            case 2:
                return this.callPrivacyRules;
            case 3:
                return this.p2pPrivacyRules;
            case 4:
                return this.profilePhotoPrivacyRules;
            case 5:
                return this.forwardsPrivacyRules;
            case 6:
                return this.phonePrivacyRules;
            case 7:
                return this.addedByPhonePrivacyRules;
            default:
                return null;
        }
    }

    public void setPrivacyRules(ArrayList<TLRPC.PrivacyRule> rules, int type) {
        switch (type) {
            case 0:
                this.lastseenPrivacyRules = rules;
                break;
            case 1:
                this.groupPrivacyRules = rules;
                break;
            case 2:
                this.callPrivacyRules = rules;
                break;
            case 3:
                this.p2pPrivacyRules = rules;
                break;
            case 4:
                this.profilePhotoPrivacyRules = rules;
                break;
            case 5:
                this.forwardsPrivacyRules = rules;
                break;
            case 6:
                this.phonePrivacyRules = rules;
                break;
            case 7:
                this.addedByPhonePrivacyRules = rules;
                break;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
        reloadContactsStatuses();
    }

    /* JADX WARN: Removed duplicated region for block: B:37:0x029c A[Catch: Exception -> 0x02a8, TryCatch #4 {Exception -> 0x02a8, blocks: (B:35:0x0203, B:37:0x029c, B:38:0x029f), top: B:58:0x0203 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void createOrUpdateConnectionServiceContact(long r26, java.lang.String r28, java.lang.String r29) {
        /*
            Method dump skipped, instructions count: 707
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.createOrUpdateConnectionServiceContact(long, java.lang.String, java.lang.String):void");
    }

    public void deleteConnectionServiceContact() {
        if (!hasContactsPermission()) {
            return;
        }
        try {
            ContentResolver resolver = ApplicationLoader.applicationContext.getContentResolver();
            Cursor cursor = resolver.query(ContactsContract.Groups.CONTENT_URI, new String[]{"_id"}, "title=? AND account_type=? AND account_name=?", new String[]{"TelegramConnectionService", this.systemAccount.type, this.systemAccount.name}, null);
            if (cursor != null && cursor.moveToFirst()) {
                int groupID = cursor.getInt(0);
                cursor.close();
                Cursor cursor2 = resolver.query(ContactsContract.Data.CONTENT_URI, new String[]{"raw_contact_id"}, "mimetype=? AND data1=?", new String[]{"vnd.android.cursor.item/group_membership", groupID + ""}, null);
                if (cursor2 != null && cursor2.moveToFirst()) {
                    int contactID = cursor2.getInt(0);
                    cursor2.close();
                    Uri uri = ContactsContract.RawContacts.CONTENT_URI;
                    resolver.delete(uri, "_id=?", new String[]{contactID + ""});
                } else if (cursor2 != null) {
                    cursor2.close();
                }
            } else if (cursor != null) {
                cursor.close();
            }
        } catch (Exception x) {
            FileLog.e(x);
        }
    }

    public static String formatName(String firstName, String lastName) {
        return formatName(firstName, lastName, 0);
    }

    public static String formatName(String firstName, String lastName, int maxLength) {
        if (firstName != null) {
            firstName = firstName.trim();
        }
        if (lastName != null) {
            lastName = lastName.trim();
        }
        StringBuilder result = new StringBuilder((firstName != null ? firstName.length() : 0) + (lastName != null ? lastName.length() : 0) + 1);
        if (LocaleController.nameDisplayOrder == 1) {
            if (firstName != null && firstName.length() > 0) {
                if (maxLength > 0 && firstName.length() > maxLength + 2) {
                    return firstName.substring(0, maxLength);
                }
                result.append(firstName);
                if (lastName != null && lastName.length() > 0) {
                    result.append(" ");
                    if (maxLength > 0 && result.length() + lastName.length() > maxLength) {
                        result.append(lastName.charAt(0));
                    } else {
                        result.append(lastName);
                    }
                }
            } else if (lastName != null && lastName.length() > 0) {
                if (maxLength > 0 && lastName.length() > maxLength + 2) {
                    return lastName.substring(0, maxLength);
                }
                result.append(lastName);
            }
        } else if (lastName != null && lastName.length() > 0) {
            if (maxLength > 0 && lastName.length() > maxLength + 2) {
                return lastName.substring(0, maxLength);
            }
            result.append(lastName);
            if (firstName != null && firstName.length() > 0) {
                result.append(" ");
                if (maxLength > 0 && result.length() + firstName.length() > maxLength) {
                    result.append(firstName.charAt(0));
                } else {
                    result.append(firstName);
                }
            }
        } else if (firstName != null && firstName.length() > 0) {
            if (maxLength > 0 && firstName.length() > maxLength + 2) {
                return firstName.substring(0, maxLength);
            }
            result.append(firstName);
        }
        return result.toString();
    }
}
