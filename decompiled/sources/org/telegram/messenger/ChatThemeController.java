package org.telegram.messenger;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.Pair;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.NotificationBadge;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.EmojiThemes;
/* loaded from: classes.dex */
public class ChatThemeController extends BaseController {
    private static List<EmojiThemes> allChatThemes = null;
    private static volatile long lastReloadTimeMs = 0;
    private static final long reloadTimeoutMs = 7200000;
    private static volatile long themesHash;
    private final LongSparseArray<String> dialogEmoticonsMap = new LongSparseArray<>();
    public static volatile DispatchQueue chatThemeQueue = new DispatchQueue("chatThemeQueue");
    private static final HashMap<Long, Bitmap> themeIdWallpaperThumbMap = new HashMap<>();
    private static final ChatThemeController[] instances = new ChatThemeController[4];

    public static void init() {
        SharedPreferences preferences = getSharedPreferences();
        themesHash = 0L;
        lastReloadTimeMs = 0L;
        try {
            themesHash = preferences.getLong("hash", 0L);
            lastReloadTimeMs = preferences.getLong("lastReload", 0L);
        } catch (Exception e) {
            FileLog.e(e);
        }
        allChatThemes = getAllChatThemesFromPrefs();
        preloadSticker("❌");
        if (!allChatThemes.isEmpty()) {
            for (EmojiThemes chatTheme : allChatThemes) {
                preloadSticker(chatTheme.getEmoticon());
            }
        }
    }

    private static void preloadSticker(String emojicon) {
        ImageReceiver imageReceiver = new ImageReceiver();
        TLRPC.Document document = MediaDataController.getInstance(UserConfig.selectedAccount).getEmojiAnimatedSticker(emojicon);
        imageReceiver.setImage(ImageLocation.getForDocument(document), "50_50", null, null, null, 0);
        Emoji.preloadEmoji(emojicon);
    }

    public static void requestAllChatThemes(final ResultCallback<List<EmojiThemes>> callback, final boolean withDefault) {
        if (themesHash == 0 || lastReloadTimeMs == 0) {
            init();
        }
        boolean needReload = System.currentTimeMillis() - lastReloadTimeMs > reloadTimeoutMs;
        List<EmojiThemes> list = allChatThemes;
        if (list == null || list.isEmpty() || needReload) {
            TLRPC.TL_account_getChatThemes request = new TLRPC.TL_account_getChatThemes();
            request.hash = themesHash;
            ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(request, new RequestDelegate() { // from class: org.telegram.messenger.ChatThemeController$$ExternalSyntheticLambda6
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChatThemeController.chatThemeQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ChatThemeController$$ExternalSyntheticLambda5
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChatThemeController.lambda$requestAllChatThemes$2(TLObject.this, r2, tL_error, r4);
                        }
                    });
                }
            });
            return;
        }
        List<EmojiThemes> chatThemes = new ArrayList<>(allChatThemes);
        if (withDefault && !chatThemes.get(0).showAsDefaultStub) {
            chatThemes.add(0, EmojiThemes.createChatThemesDefault());
        }
        for (EmojiThemes theme : chatThemes) {
            theme.initColors();
        }
        callback.onComplete(chatThemes);
    }

    public static /* synthetic */ void lambda$requestAllChatThemes$2(TLObject response, final ResultCallback callback, final TLRPC.TL_error error, boolean withDefault) {
        final List<EmojiThemes> chatThemes;
        boolean isError = false;
        if (response instanceof TLRPC.TL_account_themes) {
            TLRPC.TL_account_themes resp = (TLRPC.TL_account_themes) response;
            themesHash = resp.hash;
            lastReloadTimeMs = System.currentTimeMillis();
            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.clear();
            editor.putLong("hash", themesHash);
            editor.putLong("lastReload", lastReloadTimeMs);
            editor.putInt(NotificationBadge.NewHtcHomeBadger.COUNT, resp.themes.size());
            chatThemes = new ArrayList<>(resp.themes.size());
            for (int i = 0; i < resp.themes.size(); i++) {
                TLRPC.TL_theme tlChatTheme = resp.themes.get(i);
                Emoji.preloadEmoji(tlChatTheme.emoticon);
                SerializedData data = new SerializedData(tlChatTheme.getObjectSize());
                tlChatTheme.serializeToStream(data);
                editor.putString("theme_" + i, Utilities.bytesToHex(data.toByteArray()));
                EmojiThemes chatTheme = new EmojiThemes(tlChatTheme, false);
                chatTheme.preloadWallpaper();
                chatThemes.add(chatTheme);
            }
            editor.apply();
        } else if (response instanceof TLRPC.TL_account_themesNotModified) {
            chatThemes = getAllChatThemesFromPrefs();
        } else {
            chatThemes = null;
            isError = true;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ChatThemeController$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    ResultCallback.this.onError(error);
                }
            });
        }
        if (!isError) {
            if (withDefault && !chatThemes.get(0).showAsDefaultStub) {
                chatThemes.add(0, EmojiThemes.createChatThemesDefault());
            }
            for (EmojiThemes theme : chatThemes) {
                theme.initColors();
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ChatThemeController$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ChatThemeController.lambda$requestAllChatThemes$1(chatThemes, callback);
                }
            });
        }
    }

    public static /* synthetic */ void lambda$requestAllChatThemes$1(List chatThemes, ResultCallback callback) {
        allChatThemes = new ArrayList(chatThemes);
        callback.onComplete(chatThemes);
    }

    private static SharedPreferences getSharedPreferences() {
        return ApplicationLoader.applicationContext.getSharedPreferences("chatthemeconfig", 0);
    }

    private static SharedPreferences getEmojiSharedPreferences() {
        return ApplicationLoader.applicationContext.getSharedPreferences("chatthemeconfig_emoji", 0);
    }

    private static List<EmojiThemes> getAllChatThemesFromPrefs() {
        SharedPreferences preferences = getSharedPreferences();
        int count = preferences.getInt(NotificationBadge.NewHtcHomeBadger.COUNT, 0);
        List<EmojiThemes> themes = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String value = preferences.getString("theme_" + i, "");
            SerializedData serializedData = new SerializedData(Utilities.hexToBytes(value));
            try {
                TLRPC.TL_theme chatTheme = TLRPC.Theme.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                if (chatTheme != null) {
                    themes.add(new EmojiThemes(chatTheme, false));
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        return themes;
    }

    public static void requestChatTheme(final String emoticon, final ResultCallback<EmojiThemes> callback) {
        if (TextUtils.isEmpty(emoticon)) {
            callback.onComplete(null);
        } else {
            requestAllChatThemes(new ResultCallback<List<EmojiThemes>>() { // from class: org.telegram.messenger.ChatThemeController.1
                @Override // org.telegram.tgnet.ResultCallback
                public /* synthetic */ void onError(Throwable th) {
                    ResultCallback.CC.$default$onError(this, th);
                }

                public void onComplete(List<EmojiThemes> result) {
                    for (EmojiThemes theme : result) {
                        if (emoticon.equals(theme.getEmoticon())) {
                            theme.initColors();
                            callback.onComplete(theme);
                            return;
                        }
                    }
                }

                @Override // org.telegram.tgnet.ResultCallback
                public void onError(TLRPC.TL_error error) {
                    callback.onComplete(null);
                }
            }, false);
        }
    }

    public static ChatThemeController getInstance(int accountNum) {
        ChatThemeController[] chatThemeControllerArr = instances;
        ChatThemeController local = chatThemeControllerArr[accountNum];
        if (local == null) {
            synchronized (ChatThemeController.class) {
                local = chatThemeControllerArr[accountNum];
                if (local == null) {
                    local = new ChatThemeController(accountNum);
                    chatThemeControllerArr[accountNum] = local;
                }
            }
        }
        return local;
    }

    public ChatThemeController(int num) {
        super(num);
    }

    public void setDialogTheme(long dialogId, String emoticon, boolean sendRequest) {
        String oldEmoticon = this.dialogEmoticonsMap.get(dialogId);
        if (TextUtils.equals(oldEmoticon, emoticon)) {
            return;
        }
        if (emoticon == null) {
            this.dialogEmoticonsMap.delete(dialogId);
        } else {
            this.dialogEmoticonsMap.put(dialogId, emoticon);
        }
        SharedPreferences.Editor edit = getEmojiSharedPreferences().edit();
        edit.putString("chatTheme_" + this.currentAccount + "_" + dialogId, emoticon).apply();
        if (sendRequest) {
            TLRPC.TL_messages_setChatTheme request = new TLRPC.TL_messages_setChatTheme();
            request.emoticon = emoticon != null ? emoticon : "";
            request.peer = getMessagesController().getInputPeer(dialogId);
            getConnectionsManager().sendRequest(request, null);
        }
    }

    public EmojiThemes getDialogTheme(long dialogId) {
        String emoticon = this.dialogEmoticonsMap.get(dialogId);
        if (emoticon == null) {
            SharedPreferences emojiSharedPreferences = getEmojiSharedPreferences();
            emoticon = emojiSharedPreferences.getString("chatTheme_" + this.currentAccount + "_" + dialogId, null);
            this.dialogEmoticonsMap.put(dialogId, emoticon);
        }
        if (emoticon != null) {
            for (EmojiThemes theme : allChatThemes) {
                if (emoticon.equals(theme.getEmoticon())) {
                    return theme;
                }
            }
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void preloadAllWallpaperImages(boolean isDark) {
        for (EmojiThemes chatTheme : allChatThemes) {
            TLRPC.TL_theme theme = chatTheme.getTlTheme(isDark ? 1 : 0);
            if (theme != null) {
                long themeId = theme.id;
                if (!getPatternFile(themeId).exists()) {
                    chatTheme.loadWallpaper(isDark, null);
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void preloadAllWallpaperThumbs(boolean isDark) {
        for (EmojiThemes chatTheme : allChatThemes) {
            TLRPC.TL_theme theme = chatTheme.getTlTheme(isDark ? 1 : 0);
            if (theme != null) {
                long themeId = theme.id;
                if (!themeIdWallpaperThumbMap.containsKey(Long.valueOf(themeId))) {
                    chatTheme.loadWallpaperThumb(isDark, ChatThemeController$$ExternalSyntheticLambda7.INSTANCE);
                }
            }
        }
    }

    public static /* synthetic */ void lambda$preloadAllWallpaperThumbs$4(Pair result) {
        if (result != null) {
            themeIdWallpaperThumbMap.put((Long) result.first, (Bitmap) result.second);
        }
    }

    public static void clearWallpaperImages() {
    }

    public static void clearWallpaperThumbImages() {
        themeIdWallpaperThumbMap.clear();
    }

    public static void getWallpaperBitmap(long themeId, final ResultCallback<Bitmap> callback) {
        if (themesHash == 0) {
            callback.onComplete(null);
            return;
        }
        final File file = getPatternFile(themeId);
        chatThemeQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ChatThemeController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ChatThemeController.lambda$getWallpaperBitmap$6(file, callback);
            }
        });
    }

    public static /* synthetic */ void lambda$getWallpaperBitmap$6(File file, final ResultCallback callback) {
        Bitmap bitmap = null;
        try {
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (callback != null) {
            final Bitmap finalBitmap = bitmap;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ChatThemeController$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ResultCallback.this.onComplete(finalBitmap);
                }
            });
        }
    }

    private static File getPatternFile(long themeId) {
        return new File(ApplicationLoader.getFilesDirFixed(), String.format(Locale.US, "%d_%d.jpg", Long.valueOf(themeId), Long.valueOf(themesHash)));
    }

    public static void saveWallpaperBitmap(final Bitmap bitmap, long themeId) {
        final File file = getPatternFile(themeId);
        chatThemeQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ChatThemeController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ChatThemeController.lambda$saveWallpaperBitmap$7(file, bitmap);
            }
        });
    }

    public static /* synthetic */ void lambda$saveWallpaperBitmap$7(File file, Bitmap bitmap) {
        try {
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 87, stream);
            stream.close();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static Bitmap getWallpaperThumbBitmap(long themeId) {
        return themeIdWallpaperThumbMap.get(Long.valueOf(themeId));
    }

    public void clearCache() {
        themesHash = 0L;
        lastReloadTimeMs = 0L;
        getSharedPreferences().edit().clear().apply();
    }
}
