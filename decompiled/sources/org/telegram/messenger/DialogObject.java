package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public class DialogObject {
    public static boolean isChannel(TLRPC.Dialog dialog) {
        return (dialog == null || (dialog.flags & 1) == 0) ? false : true;
    }

    public static long makeFolderDialogId(int folderId) {
        return folderId | 2305843009213693952L;
    }

    public static boolean isFolderDialogId(long dialogId) {
        return (2305843009213693952L & dialogId) != 0 && (Long.MIN_VALUE & dialogId) == 0;
    }

    public static void initDialog(TLRPC.Dialog dialog) {
        if (dialog == null || dialog.id != 0) {
            return;
        }
        if (dialog instanceof TLRPC.TL_dialog) {
            if (dialog.peer == null) {
                return;
            }
            if (dialog.peer.user_id != 0) {
                dialog.id = dialog.peer.user_id;
            } else if (dialog.peer.chat_id != 0) {
                dialog.id = -dialog.peer.chat_id;
            } else {
                dialog.id = -dialog.peer.channel_id;
            }
        } else if (dialog instanceof TLRPC.TL_dialogFolder) {
            TLRPC.TL_dialogFolder dialogFolder = (TLRPC.TL_dialogFolder) dialog;
            dialog.id = makeFolderDialogId(dialogFolder.folder.id);
        }
    }

    public static long getPeerDialogId(TLRPC.Peer peer) {
        if (peer == null) {
            return 0L;
        }
        if (peer.user_id == 0) {
            if (peer.chat_id != 0) {
                return -peer.chat_id;
            }
            return -peer.channel_id;
        }
        return peer.user_id;
    }

    public static long getPeerDialogId(TLRPC.InputPeer peer) {
        if (peer == null) {
            return 0L;
        }
        if (peer.user_id == 0) {
            if (peer.chat_id != 0) {
                return -peer.chat_id;
            }
            return -peer.channel_id;
        }
        return peer.user_id;
    }

    public static long getLastMessageOrDraftDate(TLRPC.Dialog dialog, TLRPC.DraftMessage draftMessage) {
        return (draftMessage == null || draftMessage.date < dialog.last_message_date) ? dialog.last_message_date : draftMessage.date;
    }

    public static boolean isChatDialog(long dialogId) {
        return !isEncryptedDialog(dialogId) && !isFolderDialogId(dialogId) && dialogId < 0;
    }

    public static boolean isUserDialog(long dialogId) {
        return !isEncryptedDialog(dialogId) && !isFolderDialogId(dialogId) && dialogId > 0;
    }

    public static boolean isEncryptedDialog(long dialogId) {
        return (4611686018427387904L & dialogId) != 0 && (Long.MIN_VALUE & dialogId) == 0;
    }

    public static long makeEncryptedDialogId(long chatId) {
        return (4294967295L & chatId) | 4611686018427387904L;
    }

    public static int getEncryptedChatId(long dialogId) {
        return (int) (4294967295L & dialogId);
    }

    public static int getFolderId(long dialogId) {
        return (int) dialogId;
    }
}
