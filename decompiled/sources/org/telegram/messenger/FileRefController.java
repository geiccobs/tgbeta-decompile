package org.telegram.messenger;

import android.os.SystemClock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.FileLoadOperation;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public class FileRefController extends BaseController {
    private static volatile FileRefController[] Instance = new FileRefController[4];
    private HashMap<String, ArrayList<Requester>> locationRequester = new HashMap<>();
    private HashMap<String, ArrayList<Requester>> parentRequester = new HashMap<>();
    private HashMap<String, CachedResult> responseCache = new HashMap<>();
    private HashMap<TLRPC.TL_messages_sendMultiMedia, Object[]> multiMediaCache = new HashMap<>();
    private long lastCleanupTime = SystemClock.elapsedRealtime();
    private ArrayList<Waiter> wallpaperWaiters = new ArrayList<>();
    private ArrayList<Waiter> savedGifsWaiters = new ArrayList<>();
    private ArrayList<Waiter> recentStickersWaiter = new ArrayList<>();
    private ArrayList<Waiter> favStickersWaiter = new ArrayList<>();

    /* loaded from: classes4.dex */
    public static class Requester {
        private Object[] args;
        private boolean completed;
        private TLRPC.InputFileLocation location;
        private String locationKey;

        private Requester() {
        }
    }

    /* loaded from: classes4.dex */
    public static class CachedResult {
        private long firstQueryTime;
        private long lastQueryTime;
        private TLObject response;

        private CachedResult() {
        }
    }

    /* loaded from: classes4.dex */
    public static class Waiter {
        private String locationKey;
        private String parentKey;

        public Waiter(String loc, String parent) {
            this.locationKey = loc;
            this.parentKey = parent;
        }
    }

    public static FileRefController getInstance(int num) {
        FileRefController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (FileRefController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    FileRefController[] fileRefControllerArr = Instance;
                    FileRefController fileRefController = new FileRefController(num);
                    localInstance = fileRefController;
                    fileRefControllerArr[num] = fileRefController;
                }
            }
        }
        return localInstance;
    }

    public FileRefController(int instance) {
        super(instance);
    }

    public static String getKeyForParentObject(Object parentObject) {
        if (parentObject instanceof TLRPC.TL_availableReaction) {
            return "available_reaction_" + ((TLRPC.TL_availableReaction) parentObject).reaction;
        } else if (parentObject instanceof TLRPC.BotInfo) {
            TLRPC.BotInfo botInfo = (TLRPC.BotInfo) parentObject;
            return "bot_info_" + botInfo.user_id;
        } else if (parentObject instanceof TLRPC.TL_attachMenuBot) {
            TLRPC.TL_attachMenuBot bot = (TLRPC.TL_attachMenuBot) parentObject;
            long botId = bot.bot_id;
            return "attach_menu_bot_" + botId;
        } else if (parentObject instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) parentObject;
            long channelId = messageObject.getChannelId();
            return "message" + messageObject.getRealId() + "_" + channelId + "_" + messageObject.scheduled;
        } else if (parentObject instanceof TLRPC.Message) {
            TLRPC.Message message = (TLRPC.Message) parentObject;
            long channelId2 = message.peer_id != null ? message.peer_id.channel_id : 0L;
            return "message" + message.id + "_" + channelId2 + "_" + message.from_scheduled;
        } else if (parentObject instanceof TLRPC.WebPage) {
            TLRPC.WebPage webPage = (TLRPC.WebPage) parentObject;
            return "webpage" + webPage.id;
        } else if (parentObject instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) parentObject;
            return "user" + user.id;
        } else if (parentObject instanceof TLRPC.Chat) {
            TLRPC.Chat chat = (TLRPC.Chat) parentObject;
            return "chat" + chat.id;
        } else if (parentObject instanceof String) {
            String string = (String) parentObject;
            return "str" + string;
        } else if (parentObject instanceof TLRPC.TL_messages_stickerSet) {
            TLRPC.TL_messages_stickerSet stickerSet = (TLRPC.TL_messages_stickerSet) parentObject;
            return "set" + stickerSet.set.id;
        } else if (parentObject instanceof TLRPC.StickerSetCovered) {
            TLRPC.StickerSetCovered stickerSet2 = (TLRPC.StickerSetCovered) parentObject;
            return "set" + stickerSet2.set.id;
        } else if (parentObject instanceof TLRPC.InputStickerSet) {
            TLRPC.InputStickerSet inputStickerSet = (TLRPC.InputStickerSet) parentObject;
            return "set" + inputStickerSet.id;
        } else if (parentObject instanceof TLRPC.TL_wallPaper) {
            TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) parentObject;
            return "wallpaper" + wallPaper.id;
        } else if (parentObject instanceof TLRPC.TL_theme) {
            TLRPC.TL_theme theme = (TLRPC.TL_theme) parentObject;
            return "theme" + theme.id;
        } else if (parentObject == null) {
            return null;
        } else {
            return "" + parentObject;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:121:0x03ca  */
    /* JADX WARN: Removed duplicated region for block: B:125:0x03e1  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x032a  */
    /* JADX WARN: Removed duplicated region for block: B:91:0x032e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void requestReference(java.lang.Object r18, java.lang.Object... r19) {
        /*
            Method dump skipped, instructions count: 1030
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileRefController.requestReference(java.lang.Object, java.lang.Object[]):void");
    }

    private void broadcastWaitersData(ArrayList<Waiter> waiters, TLObject response) {
        int a = 0;
        int N = waiters.size();
        while (a < N) {
            Waiter waiter = waiters.get(a);
            onRequestComplete(waiter.locationKey, waiter.parentKey, response, a == N + (-1), false);
            a++;
        }
        waiters.clear();
    }

    private void requestReferenceFromServer(Object parentObject, final String locationKey, final String parentKey, Object[] args) {
        if (parentObject instanceof TLRPC.TL_availableReaction) {
            TLRPC.TL_messages_getAvailableReactions req = new TLRPC.TL_messages_getAvailableReactions();
            req.hash = 0;
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda5
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileRefController.this.m243xedaf0ec8(locationKey, parentKey, tLObject, tL_error);
                }
            });
        } else if (parentObject instanceof TLRPC.BotInfo) {
            TLRPC.BotInfo botInfo = (TLRPC.BotInfo) parentObject;
            TLRPC.TL_users_getFullUser req2 = new TLRPC.TL_users_getFullUser();
            req2.id = getMessagesController().getInputUser(botInfo.user_id);
            getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda6
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileRefController.this.m244xf51443e7(locationKey, parentKey, tLObject, tL_error);
                }
            });
        } else if (parentObject instanceof TLRPC.TL_attachMenuBot) {
            TLRPC.TL_attachMenuBot bot = (TLRPC.TL_attachMenuBot) parentObject;
            TLRPC.TL_messages_getAttachMenuBot req3 = new TLRPC.TL_messages_getAttachMenuBot();
            req3.bot = getMessagesController().getInputUser(bot.bot_id);
            getConnectionsManager().sendRequest(req3, new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda14
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileRefController.this.m255xfc797906(locationKey, parentKey, tLObject, tL_error);
                }
            });
        } else if (parentObject instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) parentObject;
            long channelId = messageObject.getChannelId();
            if (messageObject.scheduled) {
                TLRPC.TL_messages_getScheduledMessages req4 = new TLRPC.TL_messages_getScheduledMessages();
                req4.peer = getMessagesController().getInputPeer(messageObject.getDialogId());
                req4.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(req4, new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda19
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.m260x3deae25(locationKey, parentKey, tLObject, tL_error);
                    }
                });
            } else if (channelId != 0) {
                TLRPC.TL_channels_getMessages req5 = new TLRPC.TL_channels_getMessages();
                req5.channel = getMessagesController().getInputChannel(channelId);
                req5.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(req5, new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda20
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.m261xb43e344(locationKey, parentKey, tLObject, tL_error);
                    }
                });
            } else {
                TLRPC.TL_messages_getMessages req6 = new TLRPC.TL_messages_getMessages();
                req6.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(req6, new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda21
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.m262x12a91863(locationKey, parentKey, tLObject, tL_error);
                    }
                });
            }
        } else if (parentObject instanceof TLRPC.TL_wallPaper) {
            TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) parentObject;
            TLRPC.TL_account_getWallPaper req7 = new TLRPC.TL_account_getWallPaper();
            TLRPC.TL_inputWallPaper inputWallPaper = new TLRPC.TL_inputWallPaper();
            inputWallPaper.id = wallPaper.id;
            inputWallPaper.access_hash = wallPaper.access_hash;
            req7.wallpaper = inputWallPaper;
            getConnectionsManager().sendRequest(req7, new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda23
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileRefController.this.m263x1a0e4d82(locationKey, parentKey, tLObject, tL_error);
                }
            });
        } else if (parentObject instanceof TLRPC.TL_theme) {
            TLRPC.TL_theme theme = (TLRPC.TL_theme) parentObject;
            TLRPC.TL_account_getTheme req8 = new TLRPC.TL_account_getTheme();
            TLRPC.TL_inputTheme inputTheme = new TLRPC.TL_inputTheme();
            inputTheme.id = theme.id;
            inputTheme.access_hash = theme.access_hash;
            req8.theme = inputTheme;
            req8.format = "android";
            getConnectionsManager().sendRequest(req8, new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda24
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileRefController.this.m264x217382a1(locationKey, parentKey, tLObject, tL_error);
                }
            });
        } else if (parentObject instanceof TLRPC.WebPage) {
            TLRPC.WebPage webPage = (TLRPC.WebPage) parentObject;
            TLRPC.TL_messages_getWebPage req9 = new TLRPC.TL_messages_getWebPage();
            req9.url = webPage.url;
            req9.hash = 0;
            getConnectionsManager().sendRequest(req9, new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda25
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileRefController.this.m265x28d8b7c0(locationKey, parentKey, tLObject, tL_error);
                }
            });
        } else if (parentObject instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) parentObject;
            TLRPC.TL_users_getUsers req10 = new TLRPC.TL_users_getUsers();
            req10.id.add(getMessagesController().getInputUser(user));
            getConnectionsManager().sendRequest(req10, new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda26
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileRefController.this.m266x303decdf(locationKey, parentKey, tLObject, tL_error);
                }
            });
        } else if (parentObject instanceof TLRPC.Chat) {
            TLRPC.Chat chat = (TLRPC.Chat) parentObject;
            if (chat instanceof TLRPC.TL_chat) {
                TLRPC.TL_messages_getChats req11 = new TLRPC.TL_messages_getChats();
                req11.id.add(Long.valueOf(chat.id));
                getConnectionsManager().sendRequest(req11, new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda7
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.m245x3c3ad4fd(locationKey, parentKey, tLObject, tL_error);
                    }
                });
            } else if (chat instanceof TLRPC.TL_channel) {
                TLRPC.TL_channels_getChannels req12 = new TLRPC.TL_channels_getChannels();
                req12.id.add(MessagesController.getInputChannel(chat));
                getConnectionsManager().sendRequest(req12, new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda8
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.m246x43a00a1c(locationKey, parentKey, tLObject, tL_error);
                    }
                });
            }
        } else if (!(parentObject instanceof String)) {
            if (parentObject instanceof TLRPC.TL_messages_stickerSet) {
                TLRPC.TL_messages_stickerSet stickerSet = (TLRPC.TL_messages_stickerSet) parentObject;
                TLRPC.TL_messages_getStickerSet req13 = new TLRPC.TL_messages_getStickerSet();
                req13.stickerset = new TLRPC.TL_inputStickerSetID();
                req13.stickerset.id = stickerSet.set.id;
                req13.stickerset.access_hash = stickerSet.set.access_hash;
                getConnectionsManager().sendRequest(req13, new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda16
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.m257x28e178dd(locationKey, parentKey, tLObject, tL_error);
                    }
                });
            } else if (!(parentObject instanceof TLRPC.StickerSetCovered)) {
                if (parentObject instanceof TLRPC.InputStickerSet) {
                    TLRPC.TL_messages_getStickerSet req14 = new TLRPC.TL_messages_getStickerSet();
                    req14.stickerset = (TLRPC.InputStickerSet) parentObject;
                    getConnectionsManager().sendRequest(req14, new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda18
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            FileRefController.this.m259x37abe31b(locationKey, parentKey, tLObject, tL_error);
                        }
                    });
                    return;
                }
                sendErrorToObject(args, 0);
            } else {
                TLRPC.StickerSetCovered stickerSet2 = (TLRPC.StickerSetCovered) parentObject;
                TLRPC.TL_messages_getStickerSet req15 = new TLRPC.TL_messages_getStickerSet();
                req15.stickerset = new TLRPC.TL_inputStickerSetID();
                req15.stickerset.id = stickerSet2.set.id;
                req15.stickerset.access_hash = stickerSet2.set.access_hash;
                getConnectionsManager().sendRequest(req15, new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda17
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.m258x3046adfc(locationKey, parentKey, tLObject, tL_error);
                    }
                });
            }
        } else {
            String string = (String) parentObject;
            if ("wallpaper".equals(string)) {
                if (this.wallpaperWaiters.isEmpty()) {
                    getConnectionsManager().sendRequest(new TLRPC.TL_account_getWallPapers(), new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda1
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            FileRefController.this.m247x4b053f3b(tLObject, tL_error);
                        }
                    });
                }
                this.wallpaperWaiters.add(new Waiter(locationKey, parentKey));
            } else if (string.startsWith("gif")) {
                if (this.savedGifsWaiters.isEmpty()) {
                    getConnectionsManager().sendRequest(new TLRPC.TL_messages_getSavedGifs(), new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda2
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            FileRefController.this.m248x526a745a(tLObject, tL_error);
                        }
                    });
                }
                this.savedGifsWaiters.add(new Waiter(locationKey, parentKey));
            } else if ("recent".equals(string)) {
                if (this.recentStickersWaiter.isEmpty()) {
                    getConnectionsManager().sendRequest(new TLRPC.TL_messages_getRecentStickers(), new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda3
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            FileRefController.this.m249x59cfa979(tLObject, tL_error);
                        }
                    });
                }
                this.recentStickersWaiter.add(new Waiter(locationKey, parentKey));
            } else if ("fav".equals(string)) {
                if (this.favStickersWaiter.isEmpty()) {
                    getConnectionsManager().sendRequest(new TLRPC.TL_messages_getFavedStickers(), new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda4
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            FileRefController.this.m250x6134de98(tLObject, tL_error);
                        }
                    });
                }
                this.favStickersWaiter.add(new Waiter(locationKey, parentKey));
            } else if ("update".equals(string)) {
                TLRPC.TL_help_getAppUpdate req16 = new TLRPC.TL_help_getAppUpdate();
                try {
                    req16.source = ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName());
                } catch (Exception e) {
                }
                if (req16.source == null) {
                    req16.source = "";
                }
                getConnectionsManager().sendRequest(req16, new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda9
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.m251x689a13b7(locationKey, parentKey, tLObject, tL_error);
                    }
                });
            } else if (string.startsWith("avatar_")) {
                long id = Utilities.parseLong(string).longValue();
                if (id > 0) {
                    TLRPC.TL_photos_getUserPhotos req17 = new TLRPC.TL_photos_getUserPhotos();
                    req17.limit = 80;
                    req17.offset = 0;
                    req17.max_id = 0L;
                    req17.user_id = getMessagesController().getInputUser(id);
                    getConnectionsManager().sendRequest(req17, new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda10
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            FileRefController.this.m252x6fff48d6(locationKey, parentKey, tLObject, tL_error);
                        }
                    });
                    return;
                }
                TLRPC.TL_messages_search req18 = new TLRPC.TL_messages_search();
                req18.filter = new TLRPC.TL_inputMessagesFilterChatPhotos();
                req18.limit = 80;
                req18.offset_id = 0;
                req18.q = "";
                req18.peer = getMessagesController().getInputPeer(id);
                getConnectionsManager().sendRequest(req18, new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda12
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.m253x77647df5(locationKey, parentKey, tLObject, tL_error);
                    }
                });
            } else if (string.startsWith("sent_")) {
                String[] params = string.split("_");
                if (params.length == 3) {
                    long channelId2 = Utilities.parseLong(params[1]).longValue();
                    if (channelId2 != 0) {
                        TLRPC.TL_channels_getMessages req19 = new TLRPC.TL_channels_getMessages();
                        req19.channel = getMessagesController().getInputChannel(channelId2);
                        req19.id.add(Utilities.parseInt((CharSequence) params[2]));
                        getConnectionsManager().sendRequest(req19, new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda13
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                FileRefController.this.m254x7ec9b314(locationKey, parentKey, tLObject, tL_error);
                            }
                        });
                        return;
                    }
                    TLRPC.TL_messages_getMessages req20 = new TLRPC.TL_messages_getMessages();
                    req20.id.add(Utilities.parseInt((CharSequence) params[2]));
                    getConnectionsManager().sendRequest(req20, new RequestDelegate() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda15
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            FileRefController.this.m256x217c43be(locationKey, parentKey, tLObject, tL_error);
                        }
                    });
                    return;
                }
                sendErrorToObject(args, 0);
            } else {
                sendErrorToObject(args, 0);
            }
        }
    }

    /* renamed from: lambda$requestReferenceFromServer$0$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m243xedaf0ec8(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$1$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m244xf51443e7(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$2$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m255xfc797906(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$3$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m260x3deae25(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$4$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m261xb43e344(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$5$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m262x12a91863(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$6$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m263x1a0e4d82(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$7$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m264x217382a1(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$8$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m265x28d8b7c0(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$9$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m266x303decdf(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$10$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m245x3c3ad4fd(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$11$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m246x43a00a1c(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$12$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m247x4b053f3b(TLObject response, TLRPC.TL_error error) {
        broadcastWaitersData(this.wallpaperWaiters, response);
    }

    /* renamed from: lambda$requestReferenceFromServer$13$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m248x526a745a(TLObject response, TLRPC.TL_error error) {
        broadcastWaitersData(this.savedGifsWaiters, response);
    }

    /* renamed from: lambda$requestReferenceFromServer$14$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m249x59cfa979(TLObject response, TLRPC.TL_error error) {
        broadcastWaitersData(this.recentStickersWaiter, response);
    }

    /* renamed from: lambda$requestReferenceFromServer$15$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m250x6134de98(TLObject response, TLRPC.TL_error error) {
        broadcastWaitersData(this.favStickersWaiter, response);
    }

    /* renamed from: lambda$requestReferenceFromServer$16$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m251x689a13b7(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$17$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m252x6fff48d6(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$18$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m253x77647df5(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$19$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m254x7ec9b314(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, false, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$20$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m256x217c43be(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, false, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$21$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m257x28e178dd(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$22$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m258x3046adfc(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$23$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m259x37abe31b(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    private boolean isSameReference(byte[] oldRef, byte[] newRef) {
        return Arrays.equals(oldRef, newRef);
    }

    private boolean onUpdateObjectReference(final Requester requester, byte[] file_reference, TLRPC.InputFileLocation locationReplacement, boolean fromCache) {
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("fileref updated for " + requester.args[0] + " " + requester.locationKey);
        }
        if (requester.args[0] instanceof TLRPC.TL_inputSingleMedia) {
            final TLRPC.TL_messages_sendMultiMedia multiMedia = (TLRPC.TL_messages_sendMultiMedia) requester.args[1];
            final Object[] objects = this.multiMediaCache.get(multiMedia);
            if (objects == null) {
                return true;
            }
            TLRPC.TL_inputSingleMedia req = (TLRPC.TL_inputSingleMedia) requester.args[0];
            if (req.media instanceof TLRPC.TL_inputMediaDocument) {
                TLRPC.TL_inputMediaDocument mediaDocument = (TLRPC.TL_inputMediaDocument) req.media;
                if (fromCache && isSameReference(mediaDocument.id.file_reference, file_reference)) {
                    return false;
                }
                mediaDocument.id.file_reference = file_reference;
            } else if (req.media instanceof TLRPC.TL_inputMediaPhoto) {
                TLRPC.TL_inputMediaPhoto mediaPhoto = (TLRPC.TL_inputMediaPhoto) req.media;
                if (fromCache && isSameReference(mediaPhoto.id.file_reference, file_reference)) {
                    return false;
                }
                mediaPhoto.id.file_reference = file_reference;
            }
            int index = multiMedia.multi_media.indexOf(req);
            if (index < 0) {
                return true;
            }
            ArrayList<Object> parentObjects = (ArrayList) objects[3];
            parentObjects.set(index, null);
            boolean done = true;
            for (int a = 0; a < parentObjects.size(); a++) {
                if (parentObjects.get(a) != null) {
                    done = false;
                }
            }
            if (done) {
                this.multiMediaCache.remove(multiMedia);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda31
                    @Override // java.lang.Runnable
                    public final void run() {
                        FileRefController.this.m240xe1584ddb(multiMedia, objects);
                    }
                });
            }
        } else if (requester.args[0] instanceof TLRPC.TL_messages_sendMedia) {
            TLRPC.TL_messages_sendMedia req2 = (TLRPC.TL_messages_sendMedia) requester.args[0];
            if (req2.media instanceof TLRPC.TL_inputMediaDocument) {
                TLRPC.TL_inputMediaDocument mediaDocument2 = (TLRPC.TL_inputMediaDocument) req2.media;
                if (fromCache && isSameReference(mediaDocument2.id.file_reference, file_reference)) {
                    return false;
                }
                mediaDocument2.id.file_reference = file_reference;
            } else if (req2.media instanceof TLRPC.TL_inputMediaPhoto) {
                TLRPC.TL_inputMediaPhoto mediaPhoto2 = (TLRPC.TL_inputMediaPhoto) req2.media;
                if (fromCache && isSameReference(mediaPhoto2.id.file_reference, file_reference)) {
                    return false;
                }
                mediaPhoto2.id.file_reference = file_reference;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    FileRefController.this.m241xe8bd82fa(requester);
                }
            });
        } else if (requester.args[0] instanceof TLRPC.TL_messages_editMessage) {
            TLRPC.TL_messages_editMessage req3 = (TLRPC.TL_messages_editMessage) requester.args[0];
            if (req3.media instanceof TLRPC.TL_inputMediaDocument) {
                TLRPC.TL_inputMediaDocument mediaDocument3 = (TLRPC.TL_inputMediaDocument) req3.media;
                if (fromCache && isSameReference(mediaDocument3.id.file_reference, file_reference)) {
                    return false;
                }
                mediaDocument3.id.file_reference = file_reference;
            } else if (req3.media instanceof TLRPC.TL_inputMediaPhoto) {
                TLRPC.TL_inputMediaPhoto mediaPhoto3 = (TLRPC.TL_inputMediaPhoto) req3.media;
                if (fromCache && isSameReference(mediaPhoto3.id.file_reference, file_reference)) {
                    return false;
                }
                mediaPhoto3.id.file_reference = file_reference;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    FileRefController.this.m242xf022b819(requester);
                }
            });
        } else if (requester.args[0] instanceof TLRPC.TL_messages_saveGif) {
            TLRPC.TL_messages_saveGif req4 = (TLRPC.TL_messages_saveGif) requester.args[0];
            if (fromCache && isSameReference(req4.id.file_reference, file_reference)) {
                return false;
            }
            req4.id.file_reference = file_reference;
            getConnectionsManager().sendRequest(req4, FileRefController$$ExternalSyntheticLambda27.INSTANCE);
        } else if (requester.args[0] instanceof TLRPC.TL_messages_saveRecentSticker) {
            TLRPC.TL_messages_saveRecentSticker req5 = (TLRPC.TL_messages_saveRecentSticker) requester.args[0];
            if (fromCache && isSameReference(req5.id.file_reference, file_reference)) {
                return false;
            }
            req5.id.file_reference = file_reference;
            getConnectionsManager().sendRequest(req5, FileRefController$$ExternalSyntheticLambda28.INSTANCE);
        } else if (requester.args[0] instanceof TLRPC.TL_messages_faveSticker) {
            TLRPC.TL_messages_faveSticker req6 = (TLRPC.TL_messages_faveSticker) requester.args[0];
            if (fromCache && isSameReference(req6.id.file_reference, file_reference)) {
                return false;
            }
            req6.id.file_reference = file_reference;
            getConnectionsManager().sendRequest(req6, FileRefController$$ExternalSyntheticLambda29.INSTANCE);
        } else if (requester.args[0] instanceof TLRPC.TL_messages_getAttachedStickers) {
            TLRPC.TL_messages_getAttachedStickers req7 = (TLRPC.TL_messages_getAttachedStickers) requester.args[0];
            if (req7.media instanceof TLRPC.TL_inputStickeredMediaDocument) {
                TLRPC.TL_inputStickeredMediaDocument mediaDocument4 = (TLRPC.TL_inputStickeredMediaDocument) req7.media;
                if (fromCache && isSameReference(mediaDocument4.id.file_reference, file_reference)) {
                    return false;
                }
                mediaDocument4.id.file_reference = file_reference;
            } else if (req7.media instanceof TLRPC.TL_inputStickeredMediaPhoto) {
                TLRPC.TL_inputStickeredMediaPhoto mediaPhoto4 = (TLRPC.TL_inputStickeredMediaPhoto) req7.media;
                if (fromCache && isSameReference(mediaPhoto4.id.file_reference, file_reference)) {
                    return false;
                }
                mediaPhoto4.id.file_reference = file_reference;
            }
            getConnectionsManager().sendRequest(req7, (RequestDelegate) requester.args[1]);
        } else if (requester.args[1] instanceof FileLoadOperation) {
            FileLoadOperation fileLoadOperation = (FileLoadOperation) requester.args[1];
            if (locationReplacement != null) {
                if (fromCache && isSameReference(fileLoadOperation.location.file_reference, locationReplacement.file_reference)) {
                    return false;
                }
                fileLoadOperation.location = locationReplacement;
            } else if (fromCache && isSameReference(requester.location.file_reference, file_reference)) {
                return false;
            } else {
                requester.location.file_reference = file_reference;
            }
            fileLoadOperation.requestingReference = false;
            fileLoadOperation.startDownloadRequest();
        }
        return true;
    }

    /* renamed from: lambda$onUpdateObjectReference$24$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m240xe1584ddb(TLRPC.TL_messages_sendMultiMedia multiMedia, Object[] objects) {
        getSendMessagesHelper().performSendMessageRequestMulti(multiMedia, (ArrayList) objects[1], (ArrayList) objects[2], null, (SendMessagesHelper.DelayedMessage) objects[4], ((Boolean) objects[5]).booleanValue());
    }

    /* renamed from: lambda$onUpdateObjectReference$25$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m241xe8bd82fa(Requester requester) {
        getSendMessagesHelper().performSendMessageRequest((TLObject) requester.args[0], (MessageObject) requester.args[1], (String) requester.args[2], (SendMessagesHelper.DelayedMessage) requester.args[3], ((Boolean) requester.args[4]).booleanValue(), (SendMessagesHelper.DelayedMessage) requester.args[5], null, null, ((Boolean) requester.args[6]).booleanValue());
    }

    /* renamed from: lambda$onUpdateObjectReference$26$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m242xf022b819(Requester requester) {
        getSendMessagesHelper().performSendMessageRequest((TLObject) requester.args[0], (MessageObject) requester.args[1], (String) requester.args[2], (SendMessagesHelper.DelayedMessage) requester.args[3], ((Boolean) requester.args[4]).booleanValue(), (SendMessagesHelper.DelayedMessage) requester.args[5], null, null, ((Boolean) requester.args[6]).booleanValue());
    }

    public static /* synthetic */ void lambda$onUpdateObjectReference$27(TLObject response, TLRPC.TL_error error) {
    }

    public static /* synthetic */ void lambda$onUpdateObjectReference$28(TLObject response, TLRPC.TL_error error) {
    }

    public static /* synthetic */ void lambda$onUpdateObjectReference$29(TLObject response, TLRPC.TL_error error) {
    }

    private void sendErrorToObject(final Object[] args, int reason) {
        if (!(args[0] instanceof TLRPC.TL_inputSingleMedia)) {
            if ((args[0] instanceof TLRPC.TL_messages_sendMedia) || (args[0] instanceof TLRPC.TL_messages_editMessage)) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda35
                    @Override // java.lang.Runnable
                    public final void run() {
                        FileRefController.this.m268xabdc4175(args);
                    }
                });
                return;
            } else if (!(args[0] instanceof TLRPC.TL_messages_saveGif)) {
                if (!(args[0] instanceof TLRPC.TL_messages_saveRecentSticker)) {
                    if (!(args[0] instanceof TLRPC.TL_messages_faveSticker)) {
                        if (args[0] instanceof TLRPC.TL_messages_getAttachedStickers) {
                            getConnectionsManager().sendRequest((TLRPC.TL_messages_getAttachedStickers) args[0], (RequestDelegate) args[1]);
                            return;
                        } else if (reason == 0) {
                            TLRPC.TL_error error = new TLRPC.TL_error();
                            error.text = "not found parent object to request reference";
                            error.code = 400;
                            if (args[1] instanceof FileLoadOperation) {
                                FileLoadOperation fileLoadOperation = (FileLoadOperation) args[1];
                                fileLoadOperation.requestingReference = false;
                                fileLoadOperation.processRequestResult((FileLoadOperation.RequestInfo) args[2], error);
                                return;
                            }
                            return;
                        } else if (reason == 1 && (args[1] instanceof FileLoadOperation)) {
                            FileLoadOperation fileLoadOperation2 = (FileLoadOperation) args[1];
                            fileLoadOperation2.requestingReference = false;
                            fileLoadOperation2.onFail(false, 0);
                            return;
                        } else {
                            return;
                        }
                    }
                    TLRPC.TL_messages_faveSticker tL_messages_faveSticker = (TLRPC.TL_messages_faveSticker) args[0];
                    return;
                }
                TLRPC.TL_messages_saveRecentSticker tL_messages_saveRecentSticker = (TLRPC.TL_messages_saveRecentSticker) args[0];
                return;
            } else {
                TLRPC.TL_messages_saveGif tL_messages_saveGif = (TLRPC.TL_messages_saveGif) args[0];
                return;
            }
        }
        final TLRPC.TL_messages_sendMultiMedia req = (TLRPC.TL_messages_sendMultiMedia) args[1];
        final Object[] objects = this.multiMediaCache.get(req);
        if (objects != null) {
            this.multiMediaCache.remove(req);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.FileRefController$$ExternalSyntheticLambda32
                @Override // java.lang.Runnable
                public final void run() {
                    FileRefController.this.m267xa4770c56(req, objects);
                }
            });
        }
    }

    /* renamed from: lambda$sendErrorToObject$30$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m267xa4770c56(TLRPC.TL_messages_sendMultiMedia req, Object[] objects) {
        getSendMessagesHelper().performSendMessageRequestMulti(req, (ArrayList) objects[1], (ArrayList) objects[2], null, (SendMessagesHelper.DelayedMessage) objects[4], ((Boolean) objects[5]).booleanValue());
    }

    /* renamed from: lambda$sendErrorToObject$31$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m268xabdc4175(Object[] args) {
        getSendMessagesHelper().performSendMessageRequest((TLObject) args[0], (MessageObject) args[1], (String) args[2], (SendMessagesHelper.DelayedMessage) args[3], ((Boolean) args[4]).booleanValue(), (SendMessagesHelper.DelayedMessage) args[5], null, null, ((Boolean) args[6]).booleanValue());
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:284:0x0165 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:82:0x0183 A[LOOP:2: B:55:0x00d9->B:82:0x0183, LOOP_END] */
    /* JADX WARN: Type inference failed for: r12v0 */
    /* JADX WARN: Type inference failed for: r12v2 */
    /* JADX WARN: Type inference failed for: r12v3 */
    /* JADX WARN: Type inference failed for: r12v4 */
    /* JADX WARN: Type inference failed for: r12v41 */
    /* JADX WARN: Type inference failed for: r12v56 */
    /* JADX WARN: Type inference failed for: r12v6 */
    /* JADX WARN: Type inference failed for: r12v7 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean onRequestComplete(java.lang.String r32, java.lang.String r33, org.telegram.tgnet.TLObject r34, boolean r35, boolean r36) {
        /*
            Method dump skipped, instructions count: 1630
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileRefController.onRequestComplete(java.lang.String, java.lang.String, org.telegram.tgnet.TLObject, boolean, boolean):boolean");
    }

    /* renamed from: lambda$onRequestComplete$33$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m236x97cc7922(TLRPC.User user) {
        getMessagesController().putUser(user, false);
    }

    /* renamed from: lambda$onRequestComplete$34$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m237x9f31ae41(TLRPC.Chat chat) {
        getMessagesController().putChat(chat, false);
    }

    /* renamed from: lambda$onRequestComplete$35$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m238xa696e360(TLRPC.Chat chat) {
        getMessagesController().putChat(chat, false);
    }

    /* renamed from: lambda$onRequestComplete$36$org-telegram-messenger-FileRefController */
    public /* synthetic */ void m239xadfc187f(TLRPC.TL_messages_stickerSet stickerSet) {
        getMediaDataController().replaceStickerSet(stickerSet);
    }

    private void cleanupCache() {
        if (Math.abs(SystemClock.elapsedRealtime() - this.lastCleanupTime) < 600000) {
            return;
        }
        this.lastCleanupTime = SystemClock.elapsedRealtime();
        ArrayList<String> keysToDelete = null;
        for (Map.Entry<String, CachedResult> entry : this.responseCache.entrySet()) {
            CachedResult cachedResult = entry.getValue();
            if (Math.abs(SystemClock.elapsedRealtime() - cachedResult.firstQueryTime) >= 600000) {
                if (keysToDelete == null) {
                    keysToDelete = new ArrayList<>();
                }
                keysToDelete.add(entry.getKey());
            }
        }
        if (keysToDelete != null) {
            int size = keysToDelete.size();
            for (int a = 0; a < size; a++) {
                this.responseCache.remove(keysToDelete.get(a));
            }
        }
    }

    private CachedResult getCachedResponse(String key) {
        CachedResult cachedResult = this.responseCache.get(key);
        if (cachedResult != null && Math.abs(SystemClock.elapsedRealtime() - cachedResult.firstQueryTime) >= 600000) {
            this.responseCache.remove(key);
            return null;
        }
        return cachedResult;
    }

    private void putReponseToCache(String key, TLObject response) {
        CachedResult cachedResult = this.responseCache.get(key);
        if (cachedResult == null) {
            cachedResult = new CachedResult();
            cachedResult.response = response;
            cachedResult.firstQueryTime = SystemClock.uptimeMillis();
            this.responseCache.put(key, cachedResult);
        }
        cachedResult.lastQueryTime = SystemClock.uptimeMillis();
    }

    private byte[] getFileReference(TLRPC.Document document, TLRPC.InputFileLocation location, boolean[] needReplacement, TLRPC.InputFileLocation[] replacement) {
        if (document == null || location == null) {
            return null;
        }
        if (location instanceof TLRPC.TL_inputDocumentFileLocation) {
            if (document.id == location.id) {
                return document.file_reference;
            }
        } else {
            int size = document.thumbs.size();
            for (int a = 0; a < size; a++) {
                TLRPC.PhotoSize photoSize = document.thumbs.get(a);
                byte[] result = getFileReference(photoSize, location, needReplacement);
                if (needReplacement != null && needReplacement[0]) {
                    replacement[0] = new TLRPC.TL_inputDocumentFileLocation();
                    replacement[0].id = document.id;
                    replacement[0].volume_id = location.volume_id;
                    replacement[0].local_id = location.local_id;
                    replacement[0].access_hash = document.access_hash;
                    replacement[0].file_reference = document.file_reference;
                    replacement[0].thumb_size = photoSize.type;
                    return document.file_reference;
                } else if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean getPeerReferenceReplacement(TLRPC.User user, TLRPC.Chat chat, boolean big, TLRPC.InputFileLocation location, TLRPC.InputFileLocation[] replacement, boolean[] needReplacement) {
        TLRPC.InputPeer peer;
        TLRPC.InputPeer inputPeer;
        if (needReplacement == null || !needReplacement[0]) {
            return false;
        }
        TLRPC.TL_inputPeerPhotoFileLocation inputPeerPhotoFileLocation = new TLRPC.TL_inputPeerPhotoFileLocation();
        inputPeerPhotoFileLocation.id = location.volume_id;
        inputPeerPhotoFileLocation.volume_id = location.volume_id;
        inputPeerPhotoFileLocation.local_id = location.local_id;
        inputPeerPhotoFileLocation.big = big;
        if (user != null) {
            TLRPC.TL_inputPeerUser inputPeerUser = new TLRPC.TL_inputPeerUser();
            inputPeerUser.user_id = user.id;
            inputPeerUser.access_hash = user.access_hash;
            inputPeerPhotoFileLocation.photo_id = user.photo.photo_id;
            peer = inputPeerUser;
        } else {
            if (ChatObject.isChannel(chat)) {
                TLRPC.TL_inputPeerChannel inputPeerChannel = new TLRPC.TL_inputPeerChannel();
                inputPeerChannel.channel_id = chat.id;
                inputPeerChannel.access_hash = chat.access_hash;
                inputPeer = inputPeerChannel;
            } else {
                TLRPC.TL_inputPeerChat inputPeerChat = new TLRPC.TL_inputPeerChat();
                inputPeerChat.chat_id = chat.id;
                inputPeer = inputPeerChat;
            }
            inputPeerPhotoFileLocation.photo_id = chat.photo.photo_id;
            peer = inputPeer;
        }
        inputPeerPhotoFileLocation.peer = peer;
        replacement[0] = inputPeerPhotoFileLocation;
        return true;
    }

    private byte[] getFileReference(TLRPC.User user, TLRPC.InputFileLocation location, boolean[] needReplacement, TLRPC.InputFileLocation[] replacement) {
        if (user == null || user.photo == null || !(location instanceof TLRPC.TL_inputFileLocation)) {
            return null;
        }
        byte[] result = getFileReference(user.photo.photo_small, location, needReplacement);
        if (getPeerReferenceReplacement(user, null, false, location, replacement, needReplacement)) {
            return new byte[0];
        }
        if (result == null) {
            result = getFileReference(user.photo.photo_big, location, needReplacement);
            if (getPeerReferenceReplacement(user, null, true, location, replacement, needReplacement)) {
                return new byte[0];
            }
        }
        return result;
    }

    private byte[] getFileReference(TLRPC.Chat chat, TLRPC.InputFileLocation location, boolean[] needReplacement, TLRPC.InputFileLocation[] replacement) {
        if (chat == null || chat.photo == null || (!(location instanceof TLRPC.TL_inputFileLocation) && !(location instanceof TLRPC.TL_inputPeerPhotoFileLocation))) {
            return null;
        }
        if (location instanceof TLRPC.TL_inputPeerPhotoFileLocation) {
            needReplacement[0] = true;
            if (!getPeerReferenceReplacement(null, chat, false, location, replacement, needReplacement)) {
                return null;
            }
            return new byte[0];
        }
        byte[] result = getFileReference(chat.photo.photo_small, location, needReplacement);
        if (getPeerReferenceReplacement(null, chat, false, location, replacement, needReplacement)) {
            return new byte[0];
        }
        if (result == null) {
            result = getFileReference(chat.photo.photo_big, location, needReplacement);
            if (getPeerReferenceReplacement(null, chat, true, location, replacement, needReplacement)) {
                return new byte[0];
            }
        }
        return result;
    }

    private byte[] getFileReference(TLRPC.Photo photo, TLRPC.InputFileLocation location, boolean[] needReplacement, TLRPC.InputFileLocation[] replacement) {
        if (photo == null) {
            return null;
        }
        if (location instanceof TLRPC.TL_inputPhotoFileLocation) {
            if (photo.id != location.id) {
                return null;
            }
            return photo.file_reference;
        }
        if (location instanceof TLRPC.TL_inputFileLocation) {
            int size = photo.sizes.size();
            for (int a = 0; a < size; a++) {
                TLRPC.PhotoSize photoSize = photo.sizes.get(a);
                byte[] result = getFileReference(photoSize, location, needReplacement);
                if (needReplacement != null && needReplacement[0]) {
                    replacement[0] = new TLRPC.TL_inputPhotoFileLocation();
                    replacement[0].id = photo.id;
                    replacement[0].volume_id = location.volume_id;
                    replacement[0].local_id = location.local_id;
                    replacement[0].access_hash = photo.access_hash;
                    replacement[0].file_reference = photo.file_reference;
                    replacement[0].thumb_size = photoSize.type;
                    return photo.file_reference;
                } else if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private byte[] getFileReference(TLRPC.PhotoSize photoSize, TLRPC.InputFileLocation location, boolean[] needReplacement) {
        if (photoSize == null || !(location instanceof TLRPC.TL_inputFileLocation)) {
            return null;
        }
        return getFileReference(photoSize.location, location, needReplacement);
    }

    private byte[] getFileReference(TLRPC.FileLocation fileLocation, TLRPC.InputFileLocation location, boolean[] needReplacement) {
        if (fileLocation == null || !(location instanceof TLRPC.TL_inputFileLocation) || fileLocation.local_id != location.local_id || fileLocation.volume_id != location.volume_id) {
            return null;
        }
        if (fileLocation.file_reference == null && needReplacement != null) {
            needReplacement[0] = true;
        }
        return fileLocation.file_reference;
    }

    private byte[] getFileReference(TLRPC.WebPage webpage, TLRPC.InputFileLocation location, boolean[] needReplacement, TLRPC.InputFileLocation[] replacement) {
        byte[] result = getFileReference(webpage.document, location, needReplacement, replacement);
        if (result != null) {
            return result;
        }
        byte[] result2 = getFileReference(webpage.photo, location, needReplacement, replacement);
        if (result2 != null) {
            return result2;
        }
        if (!webpage.attributes.isEmpty()) {
            int size1 = webpage.attributes.size();
            for (int a = 0; a < size1; a++) {
                TLRPC.TL_webPageAttributeTheme attribute = webpage.attributes.get(a);
                int size2 = attribute.documents.size();
                for (int b = 0; b < size2; b++) {
                    byte[] result3 = getFileReference(attribute.documents.get(b), location, needReplacement, replacement);
                    if (result3 != null) {
                        return result3;
                    }
                }
            }
        }
        if (webpage.cached_page != null) {
            int size22 = webpage.cached_page.documents.size();
            for (int b2 = 0; b2 < size22; b2++) {
                byte[] result4 = getFileReference(webpage.cached_page.documents.get(b2), location, needReplacement, replacement);
                if (result4 != null) {
                    return result4;
                }
            }
            int size23 = webpage.cached_page.photos.size();
            for (int b3 = 0; b3 < size23; b3++) {
                byte[] result5 = getFileReference(webpage.cached_page.photos.get(b3), location, needReplacement, replacement);
                if (result5 != null) {
                    return result5;
                }
            }
            return null;
        }
        return null;
    }

    public static boolean isFileRefError(String error) {
        return "FILEREF_EXPIRED".equals(error) || "FILE_REFERENCE_EXPIRED".equals(error) || "FILE_REFERENCE_EMPTY".equals(error) || (error != null && error.startsWith("FILE_REFERENCE_"));
    }
}
