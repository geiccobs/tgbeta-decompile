package org.telegram.messenger;

import android.text.TextUtils;
import android.util.LongSparseArray;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public class MemberRequestsController extends BaseController {
    private static final MemberRequestsController[] instances = new MemberRequestsController[4];
    private final LongSparseArray<TLRPC.TL_messages_chatInviteImporters> firstImportersCache = new LongSparseArray<>();

    public static MemberRequestsController getInstance(int accountNum) {
        MemberRequestsController[] memberRequestsControllerArr = instances;
        MemberRequestsController local = memberRequestsControllerArr[accountNum];
        if (local == null) {
            synchronized (MemberRequestsController.class) {
                local = memberRequestsControllerArr[accountNum];
                if (local == null) {
                    local = new MemberRequestsController(accountNum);
                    memberRequestsControllerArr[accountNum] = local;
                }
            }
        }
        return local;
    }

    public MemberRequestsController(int accountNum) {
        super(accountNum);
    }

    public TLRPC.TL_messages_chatInviteImporters getCachedImporters(long chatId) {
        return this.firstImportersCache.get(chatId);
    }

    public int getImporters(final long chatId, String query, TLRPC.TL_chatInviteImporter lastImporter, LongSparseArray<TLRPC.User> users, final RequestDelegate onComplete) {
        boolean isEmptyQuery = TextUtils.isEmpty(query);
        TLRPC.TL_messages_getChatInviteImporters req = new TLRPC.TL_messages_getChatInviteImporters();
        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(-chatId);
        req.requested = true;
        req.limit = 30;
        if (!isEmptyQuery) {
            req.q = query;
            req.flags |= 4;
        }
        if (lastImporter == null) {
            req.offset_user = new TLRPC.TL_inputUserEmpty();
        } else {
            req.offset_user = getMessagesController().getInputUser(users.get(lastImporter.user_id));
            req.offset_date = lastImporter.date;
        }
        return getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MemberRequestsController$$ExternalSyntheticLambda1
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MemberRequestsController.this.m574x928219c8(chatId, onComplete, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$getImporters$1$org-telegram-messenger-MemberRequestsController */
    public /* synthetic */ void m574x928219c8(final long chatId, final RequestDelegate onComplete, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MemberRequestsController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                MemberRequestsController.this.m573x92f87fc7(error, response, chatId, onComplete);
            }
        });
    }

    /* renamed from: lambda$getImporters$0$org-telegram-messenger-MemberRequestsController */
    public /* synthetic */ void m573x92f87fc7(TLRPC.TL_error error, TLObject response, long chatId, RequestDelegate onComplete) {
        if (error == null) {
            TLRPC.TL_messages_chatInviteImporters importers = (TLRPC.TL_messages_chatInviteImporters) response;
            this.firstImportersCache.put(chatId, importers);
        }
        onComplete.run(response, error);
    }

    public void onPendingRequestsUpdated(TLRPC.TL_updatePendingJoinRequests update) {
        long peerId = MessageObject.getPeerId(update.peer);
        this.firstImportersCache.put(-peerId, null);
        TLRPC.ChatFull chatFull = getMessagesController().getChatFull(-peerId);
        if (chatFull != null) {
            chatFull.requests_pending = update.requests_pending;
            chatFull.recent_requesters = update.recent_requesters;
            chatFull.flags |= 131072;
            getMessagesStorage().updateChatInfo(chatFull, false);
            getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull, 0, false, false);
        }
    }
}
