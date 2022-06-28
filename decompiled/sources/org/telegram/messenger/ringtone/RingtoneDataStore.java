package org.telegram.messenger.ringtone;

import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.android.exoplayer2.util.MimeTypes;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.NotificationBadge;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public class RingtoneDataStore {
    private static volatile long lastReloadTimeMs = 0;
    private static volatile long queryHash = 0;
    private static final long reloadTimeoutMs = 86400000;
    public static final HashSet<String> ringtoneSupportedMimeType = new HashSet<>(Arrays.asList("audio/mpeg3", MimeTypes.AUDIO_MPEG, "audio/ogg", "audio/m4a"));
    private final long clientUserId;
    private final int currentAccount;
    private boolean loaded;
    private int localIds;
    String prefName = null;
    public final ArrayList<CachedTone> userRingtones = new ArrayList<>();

    public RingtoneDataStore(int currentAccount) {
        this.currentAccount = currentAccount;
        this.clientUserId = UserConfig.getInstance(currentAccount).clientUserId;
        SharedPreferences preferences = getSharedPreferences();
        try {
            queryHash = preferences.getLong("hash", 0L);
            lastReloadTimeMs = preferences.getLong("lastReload", 0L);
        } catch (Exception e) {
            FileLog.e(e);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ringtone.RingtoneDataStore$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                RingtoneDataStore.this.m1261lambda$new$0$orgtelegrammessengerringtoneRingtoneDataStore();
            }
        });
    }

    /* renamed from: loadUserRingtones */
    public void m1261lambda$new$0$orgtelegrammessengerringtoneRingtoneDataStore() {
        boolean needReload = System.currentTimeMillis() - lastReloadTimeMs > reloadTimeoutMs;
        TLRPC.TL_account_getSavedRingtones req = new TLRPC.TL_account_getSavedRingtones();
        req.hash = queryHash;
        if (needReload) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.ringtone.RingtoneDataStore$$ExternalSyntheticLambda5
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    RingtoneDataStore.this.m1260x9e2ce1f4(tLObject, tL_error);
                }
            });
            return;
        }
        if (!this.loaded) {
            loadFromPrefs(true);
            this.loaded = true;
        }
        checkRingtoneSoundsLoaded();
    }

    /* renamed from: lambda$loadUserRingtones$2$org-telegram-messenger-ringtone-RingtoneDataStore */
    public /* synthetic */ void m1260x9e2ce1f4(final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ringtone.RingtoneDataStore$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                RingtoneDataStore.this.m1259x5aa1c433(response);
            }
        });
    }

    /* renamed from: lambda$loadUserRingtones$1$org-telegram-messenger-ringtone-RingtoneDataStore */
    public /* synthetic */ void m1259x5aa1c433(TLObject response) {
        if (response != null) {
            if (response instanceof TLRPC.TL_account_savedRingtonesNotModified) {
                loadFromPrefs(true);
            } else if (response instanceof TLRPC.TL_account_savedRingtones) {
                TLRPC.TL_account_savedRingtones res = (TLRPC.TL_account_savedRingtones) response;
                saveTones(res.ringtones);
                SharedPreferences.Editor edit = getSharedPreferences().edit();
                long j = res.hash;
                queryHash = j;
                SharedPreferences.Editor putLong = edit.putLong("hash", j);
                long currentTimeMillis = System.currentTimeMillis();
                lastReloadTimeMs = currentTimeMillis;
                putLong.putLong("lastReload", currentTimeMillis).apply();
            }
            checkRingtoneSoundsLoaded();
        }
    }

    private void loadFromPrefs(boolean notify) {
        boolean z;
        SharedPreferences preferences = getSharedPreferences();
        int count = preferences.getInt(NotificationBadge.NewHtcHomeBadger.COUNT, 0);
        this.userRingtones.clear();
        for (int i = 0; i < count; i++) {
            String value = preferences.getString("tone_document" + i, "");
            String localPath = preferences.getString("tone_local_path" + i, "");
            SerializedData serializedData = new SerializedData(Utilities.hexToBytes(value));
            try {
                TLRPC.Document document = TLRPC.Document.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                CachedTone tone = new CachedTone();
                tone.document = document;
                tone.localUri = localPath;
                int i2 = this.localIds;
                this.localIds = i2 + 1;
                tone.localId = i2;
                this.userRingtones.add(tone);
            } finally {
                if (!z) {
                }
            }
        }
        if (notify) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ringtone.RingtoneDataStore$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RingtoneDataStore.this.m1258xdfb099d5();
                }
            });
        }
    }

    /* renamed from: lambda$loadFromPrefs$3$org-telegram-messenger-ringtone-RingtoneDataStore */
    public /* synthetic */ void m1258xdfb099d5() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onUserRingtonesUpdated, new Object[0]);
    }

    private void saveTones(ArrayList<TLRPC.Document> ringtones) {
        if (!this.loaded) {
            loadFromPrefs(false);
            this.loaded = true;
        }
        HashMap<Long, String> documentIdToLocalFilePath = new HashMap<>();
        Iterator<CachedTone> it = this.userRingtones.iterator();
        while (it.hasNext()) {
            CachedTone cachedTone = it.next();
            if (cachedTone.localUri != null && cachedTone.document != null) {
                documentIdToLocalFilePath.put(Long.valueOf(cachedTone.document.id), cachedTone.localUri);
            }
        }
        this.userRingtones.clear();
        SharedPreferences preferences = getSharedPreferences();
        preferences.edit().clear().apply();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(NotificationBadge.NewHtcHomeBadger.COUNT, ringtones.size());
        for (int i = 0; i < ringtones.size(); i++) {
            TLRPC.Document document = ringtones.get(i);
            String localPath = documentIdToLocalFilePath.get(Long.valueOf(document.id));
            SerializedData data = new SerializedData(document.getObjectSize());
            document.serializeToStream(data);
            editor.putString("tone_document" + i, Utilities.bytesToHex(data.toByteArray()));
            if (localPath != null) {
                editor.putString("tone_local_path" + i, localPath);
            }
            CachedTone tone = new CachedTone();
            tone.document = document;
            tone.localUri = localPath;
            int i2 = this.localIds;
            this.localIds = i2 + 1;
            tone.localId = i2;
            this.userRingtones.add(tone);
        }
        editor.apply();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onUserRingtonesUpdated, new Object[0]);
    }

    public void saveTones() {
        SharedPreferences preferences = getSharedPreferences();
        preferences.edit().clear().apply();
        SharedPreferences.Editor editor = preferences.edit();
        int count = 0;
        for (int i = 0; i < this.userRingtones.size(); i++) {
            if (!this.userRingtones.get(i).uploading) {
                count++;
                TLRPC.Document document = this.userRingtones.get(i).document;
                String localPath = this.userRingtones.get(i).localUri;
                SerializedData data = new SerializedData(document.getObjectSize());
                document.serializeToStream(data);
                editor.putString("tone_document" + i, Utilities.bytesToHex(data.toByteArray()));
                if (localPath != null) {
                    editor.putString("tone_local_path" + i, localPath);
                }
            }
        }
        editor.putInt(NotificationBadge.NewHtcHomeBadger.COUNT, count);
        editor.apply();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onUserRingtonesUpdated, new Object[0]);
    }

    private SharedPreferences getSharedPreferences() {
        if (this.prefName == null) {
            this.prefName = "ringtones_pref_" + this.clientUserId;
        }
        return ApplicationLoader.applicationContext.getSharedPreferences(this.prefName, 0);
    }

    public void addUploadingTone(String filePath) {
        CachedTone cachedTone = new CachedTone();
        cachedTone.localUri = filePath;
        int i = this.localIds;
        this.localIds = i + 1;
        cachedTone.localId = i;
        cachedTone.uploading = true;
        this.userRingtones.add(cachedTone);
    }

    public void onRingtoneUploaded(String filePath, TLRPC.Document document, boolean error) {
        boolean changed = false;
        if (error) {
            int i = 0;
            while (true) {
                if (i < this.userRingtones.size()) {
                    if (!this.userRingtones.get(i).uploading || !filePath.equals(this.userRingtones.get(i).localUri)) {
                        i++;
                    } else {
                        this.userRingtones.remove(i);
                        changed = true;
                        break;
                    }
                } else {
                    break;
                }
            }
        } else {
            int i2 = 0;
            while (true) {
                if (i2 < this.userRingtones.size()) {
                    if (this.userRingtones.get(i2).uploading && filePath.equals(this.userRingtones.get(i2).localUri)) {
                        this.userRingtones.get(i2).uploading = false;
                        this.userRingtones.get(i2).document = document;
                        changed = true;
                        break;
                    }
                    i2++;
                } else {
                    break;
                }
            }
            if (changed) {
                saveTones();
            }
        }
        if (changed) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onUserRingtonesUpdated, new Object[0]);
        }
    }

    public String getSoundPath(long id) {
        if (!this.loaded) {
            loadFromPrefs(true);
            this.loaded = true;
        }
        for (int i = 0; i < this.userRingtones.size(); i++) {
            if (this.userRingtones.get(i).document != null && this.userRingtones.get(i).document.id == id) {
                if (!TextUtils.isEmpty(this.userRingtones.get(i).localUri)) {
                    return this.userRingtones.get(i).localUri;
                } else {
                    return FileLoader.getInstance(this.currentAccount).getPathToAttach(this.userRingtones.get(i).document).toString();
                }
            }
        }
        return "NoSound";
    }

    public void checkRingtoneSoundsLoaded() {
        if (!this.loaded) {
            loadFromPrefs(true);
            this.loaded = true;
        }
        final ArrayList<CachedTone> cachedTones = new ArrayList<>(this.userRingtones);
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ringtone.RingtoneDataStore$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                RingtoneDataStore.this.m1257x495026ca(cachedTones);
            }
        });
    }

    /* renamed from: lambda$checkRingtoneSoundsLoaded$5$org-telegram-messenger-ringtone-RingtoneDataStore */
    public /* synthetic */ void m1257x495026ca(ArrayList cachedTones) {
        final TLRPC.Document document;
        File file;
        for (int i = 0; i < cachedTones.size(); i++) {
            CachedTone tone = (CachedTone) cachedTones.get(i);
            if (tone != null && ((TextUtils.isEmpty(tone.localUri) || !new File(tone.localUri).exists()) && tone.document != null && ((file = FileLoader.getInstance(this.currentAccount).getPathToAttach((document = tone.document))) == null || !file.exists()))) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ringtone.RingtoneDataStore$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        RingtoneDataStore.this.m1256x5c50909(document);
                    }
                });
            }
        }
    }

    /* renamed from: lambda$checkRingtoneSoundsLoaded$4$org-telegram-messenger-ringtone-RingtoneDataStore */
    public /* synthetic */ void m1256x5c50909(TLRPC.Document document) {
        FileLoader.getInstance(this.currentAccount).loadFile(document, document, 0, 0);
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public void remove(TLRPC.Document document) {
        if (document == null) {
            return;
        }
        if (!this.loaded) {
            loadFromPrefs(true);
            this.loaded = true;
        }
        for (int i = 0; i < this.userRingtones.size(); i++) {
            if (this.userRingtones.get(i).document != null && this.userRingtones.get(i).document.id == document.id) {
                this.userRingtones.remove(i);
                return;
            }
        }
    }

    public boolean contains(long id) {
        return getDocument(id) != null;
    }

    public void addTone(TLRPC.Document document) {
        if (document == null || contains(document.id)) {
            return;
        }
        CachedTone cachedTone = new CachedTone();
        cachedTone.document = document;
        int i = this.localIds;
        this.localIds = i + 1;
        cachedTone.localId = i;
        cachedTone.uploading = false;
        this.userRingtones.add(cachedTone);
        saveTones();
    }

    public TLRPC.Document getDocument(long id) {
        if (!this.loaded) {
            loadFromPrefs(true);
            this.loaded = true;
        }
        for (int i = 0; i < this.userRingtones.size(); i++) {
            try {
                if (this.userRingtones.get(i) != null && this.userRingtones.get(i).document != null && this.userRingtones.get(i).document.id == id) {
                    return this.userRingtones.get(i).document;
                }
            } catch (Exception e) {
                FileLog.e(e);
                return null;
            }
        }
        return null;
    }

    /* loaded from: classes4.dex */
    public class CachedTone {
        public TLRPC.Document document;
        public int localId;
        public String localUri;
        public boolean uploading;

        public CachedTone() {
            RingtoneDataStore.this = this$0;
        }
    }
}
