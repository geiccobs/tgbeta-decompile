package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.ChatRightsEditActivity;
import org.telegram.ui.ChatUsersActivity;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.GigagroupConvertAlert;
import org.telegram.ui.Components.IntSeekBarAccessibilityDelegate;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarAccessibilityDelegate;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.GroupCreateActivity;
/* loaded from: classes4.dex */
public class ChatUsersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static final int SELECT_TYPE_ADMIN = 1;
    public static final int SELECT_TYPE_BLOCK = 2;
    public static final int SELECT_TYPE_EXCEPTION = 3;
    public static final int SELECT_TYPE_MEMBERS = 0;
    public static final int TYPE_ADMIN = 1;
    public static final int TYPE_BANNED = 0;
    public static final int TYPE_KICKED = 3;
    public static final int TYPE_USERS = 2;
    private static final int done_button = 1;
    private static final int search_button = 0;
    private int addNew2Row;
    private int addNewRow;
    private int addNewSectionRow;
    private int addUsersRow;
    private int blockedEmptyRow;
    private int botEndRow;
    private int botHeaderRow;
    private int botStartRow;
    private boolean botsEndReached;
    private int changeInfoRow;
    private boolean contactsEndReached;
    private int contactsEndRow;
    private int contactsHeaderRow;
    private int contactsStartRow;
    private TLRPC.Chat currentChat;
    private int delayResults;
    private ChatUsersActivityDelegate delegate;
    private ActionBarMenuItem doneItem;
    private int embedLinksRow;
    private StickerEmptyView emptyView;
    private boolean firstLoaded;
    private FlickerLoadingView flickerLoadingView;
    private int gigaConvertRow;
    private int gigaHeaderRow;
    private int gigaInfoRow;
    private LongSparseArray<TLRPC.TL_groupCallParticipant> ignoredUsers;
    private TLRPC.ChatFull info;
    private String initialBannedRights;
    private int initialSlowmode;
    private boolean isChannel;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private int loadingHeaderRow;
    private int loadingProgressRow;
    private int loadingUserCellRow;
    private boolean loadingUsers;
    private int membersHeaderRow;
    private boolean openTransitionStarted;
    private int participantsDivider2Row;
    private int participantsDividerRow;
    private int participantsEndRow;
    private int participantsInfoRow;
    private int participantsStartRow;
    private int permissionsSectionRow;
    private int pinMessagesRow;
    private View progressBar;
    private int recentActionsRow;
    private int removedUsersRow;
    private int restricted1SectionRow;
    private int rowCount;
    private ActionBarMenuItem searchItem;
    private SearchAdapter searchListViewAdapter;
    private boolean searching;
    private int selectedSlowmode;
    private int sendMediaRow;
    private int sendMessagesRow;
    private int sendPollsRow;
    private int sendStickersRow;
    private int slowmodeInfoRow;
    private int slowmodeRow;
    private int slowmodeSelectRow;
    private UndoView undoView;
    private TLRPC.TL_chatBannedRights defaultBannedRights = new TLRPC.TL_chatBannedRights();
    private ArrayList<TLObject> participants = new ArrayList<>();
    private ArrayList<TLObject> bots = new ArrayList<>();
    private ArrayList<TLObject> contacts = new ArrayList<>();
    private LongSparseArray<TLObject> participantsMap = new LongSparseArray<>();
    private LongSparseArray<TLObject> botsMap = new LongSparseArray<>();
    private LongSparseArray<TLObject> contactsMap = new LongSparseArray<>();
    private long chatId = this.arguments.getLong(ChatReactionsEditActivity.KEY_CHAT_ID);
    private int type = this.arguments.getInt(CommonProperties.TYPE);
    private boolean needOpenSearch = this.arguments.getBoolean("open_search");
    private int selectType = this.arguments.getInt("selectType");

    /* loaded from: classes4.dex */
    public interface ChatUsersActivityDelegate {
        void didAddParticipantToList(long j, TLObject tLObject);

        void didChangeOwner(TLRPC.User user);

        void didKickParticipant(long j);

        void didSelectUser(long j);

        /* renamed from: org.telegram.ui.ChatUsersActivity$ChatUsersActivityDelegate$-CC */
        /* loaded from: classes4.dex */
        public final /* synthetic */ class CC {
            public static void $default$didAddParticipantToList(ChatUsersActivityDelegate _this, long uid, TLObject participant) {
            }

            public static void $default$didChangeOwner(ChatUsersActivityDelegate _this, TLRPC.User user) {
            }

            public static void $default$didSelectUser(ChatUsersActivityDelegate _this, long uid) {
            }

            public static void $default$didKickParticipant(ChatUsersActivityDelegate _this, long userId) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class ChooseView extends View {
        private final SeekBarAccessibilityDelegate accessibilityDelegate;
        private int circleSize;
        private int gapSize;
        private int lineSize;
        private boolean moving;
        private int sideSide;
        private boolean startMoving;
        private int startMovingItem;
        private float startX;
        private final TextPaint textPaint;
        private ArrayList<String> strings = new ArrayList<>();
        private ArrayList<Integer> sizes = new ArrayList<>();
        private final Paint paint = new Paint(1);

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ChooseView(Context context) {
            super(context);
            String string;
            ChatUsersActivity.this = r9;
            TextPaint textPaint = new TextPaint(1);
            this.textPaint = textPaint;
            textPaint.setTextSize(AndroidUtilities.dp(13.0f));
            for (int a = 0; a < 7; a++) {
                switch (a) {
                    case 0:
                        string = LocaleController.getString("SlowmodeOff", R.string.SlowmodeOff);
                        break;
                    case 1:
                        string = LocaleController.formatString("SlowmodeSeconds", R.string.SlowmodeSeconds, 10);
                        break;
                    case 2:
                        string = LocaleController.formatString("SlowmodeSeconds", R.string.SlowmodeSeconds, 30);
                        break;
                    case 3:
                        string = LocaleController.formatString("SlowmodeMinutes", R.string.SlowmodeMinutes, 1);
                        break;
                    case 4:
                        string = LocaleController.formatString("SlowmodeMinutes", R.string.SlowmodeMinutes, 5);
                        break;
                    case 5:
                        string = LocaleController.formatString("SlowmodeMinutes", R.string.SlowmodeMinutes, 15);
                        break;
                    default:
                        string = LocaleController.formatString("SlowmodeHours", R.string.SlowmodeHours, 1);
                        break;
                }
                this.strings.add(string);
                this.sizes.add(Integer.valueOf((int) Math.ceil(this.textPaint.measureText(string))));
            }
            this.accessibilityDelegate = new IntSeekBarAccessibilityDelegate() { // from class: org.telegram.ui.ChatUsersActivity.ChooseView.1
                @Override // org.telegram.ui.Components.IntSeekBarAccessibilityDelegate
                public int getProgress() {
                    return ChatUsersActivity.this.selectedSlowmode;
                }

                @Override // org.telegram.ui.Components.IntSeekBarAccessibilityDelegate
                public void setProgress(int progress) {
                    ChooseView.this.setItem(progress);
                }

                @Override // org.telegram.ui.Components.IntSeekBarAccessibilityDelegate
                public int getMaxValue() {
                    return ChooseView.this.strings.size() - 1;
                }

                @Override // org.telegram.ui.Components.SeekBarAccessibilityDelegate
                protected CharSequence getContentDescription(View host) {
                    if (ChatUsersActivity.this.selectedSlowmode != 0) {
                        return ChatUsersActivity.this.formatSeconds(ChatUsersActivity.this.getSecondsForIndex(ChatUsersActivity.this.selectedSlowmode));
                    }
                    return LocaleController.getString("SlowmodeOff", R.string.SlowmodeOff);
                }
            };
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            this.accessibilityDelegate.onInitializeAccessibilityNodeInfoInternal(this, info);
        }

        @Override // android.view.View
        public boolean performAccessibilityAction(int action, Bundle arguments) {
            return super.performAccessibilityAction(action, arguments) || this.accessibilityDelegate.performAccessibilityActionInternal(this, action, arguments);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            boolean z = false;
            if (event.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
                int a = 0;
                while (true) {
                    if (a >= this.strings.size()) {
                        break;
                    }
                    int i = this.sideSide;
                    int i2 = this.lineSize + (this.gapSize * 2);
                    int i3 = this.circleSize;
                    int cx = i + ((i2 + i3) * a) + (i3 / 2);
                    if (x > cx - AndroidUtilities.dp(15.0f) && x < AndroidUtilities.dp(15.0f) + cx) {
                        if (a == ChatUsersActivity.this.selectedSlowmode) {
                            z = true;
                        }
                        this.startMoving = z;
                        this.startX = x;
                        this.startMovingItem = ChatUsersActivity.this.selectedSlowmode;
                    } else {
                        a++;
                    }
                }
            } else if (event.getAction() == 2) {
                if (this.startMoving) {
                    if (Math.abs(this.startX - x) >= AndroidUtilities.getPixelsInCM(0.5f, true)) {
                        this.moving = true;
                        this.startMoving = false;
                    }
                } else if (this.moving) {
                    int a2 = 0;
                    while (true) {
                        if (a2 >= this.strings.size()) {
                            break;
                        }
                        int i4 = this.sideSide;
                        int i5 = this.lineSize;
                        int i6 = this.gapSize;
                        int i7 = this.circleSize;
                        int cx2 = i4 + (((i6 * 2) + i5 + i7) * a2) + (i7 / 2);
                        int diff = (i5 / 2) + (i7 / 2) + i6;
                        if (x > cx2 - diff && x < cx2 + diff) {
                            if (ChatUsersActivity.this.selectedSlowmode != a2) {
                                setItem(a2);
                            }
                        } else {
                            a2++;
                        }
                    }
                }
            } else if (event.getAction() == 1 || event.getAction() == 3) {
                if (this.moving) {
                    if (ChatUsersActivity.this.selectedSlowmode != this.startMovingItem) {
                        setItem(ChatUsersActivity.this.selectedSlowmode);
                    }
                } else {
                    int a3 = 0;
                    while (true) {
                        if (a3 >= this.strings.size()) {
                            break;
                        }
                        int i8 = this.sideSide;
                        int i9 = this.lineSize + (this.gapSize * 2);
                        int i10 = this.circleSize;
                        int cx3 = i8 + ((i9 + i10) * a3) + (i10 / 2);
                        if (x > cx3 - AndroidUtilities.dp(15.0f) && x < AndroidUtilities.dp(15.0f) + cx3) {
                            if (ChatUsersActivity.this.selectedSlowmode != a3) {
                                setItem(a3);
                            }
                        } else {
                            a3++;
                        }
                    }
                }
                this.startMoving = false;
                this.moving = false;
            }
            return true;
        }

        public void setItem(int index) {
            if (ChatUsersActivity.this.info != null) {
                ChatUsersActivity.this.selectedSlowmode = index;
                ChatUsersActivity.this.listViewAdapter.notifyItemChanged(ChatUsersActivity.this.slowmodeInfoRow);
                invalidate();
            }
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(74.0f), C.BUFFER_FLAG_ENCRYPTED));
            this.circleSize = AndroidUtilities.dp(6.0f);
            this.gapSize = AndroidUtilities.dp(2.0f);
            this.sideSide = AndroidUtilities.dp(22.0f);
            this.lineSize = (((getMeasuredWidth() - (this.circleSize * this.strings.size())) - ((this.gapSize * 2) * (this.strings.size() - 1))) - (this.sideSide * 2)) / (this.strings.size() - 1);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            int cy = (getMeasuredHeight() / 2) + AndroidUtilities.dp(11.0f);
            int a = 0;
            while (a < this.strings.size()) {
                int i = this.sideSide;
                int i2 = this.lineSize + (this.gapSize * 2);
                int i3 = this.circleSize;
                int cx = i + ((i2 + i3) * a) + (i3 / 2);
                if (a <= ChatUsersActivity.this.selectedSlowmode) {
                    this.paint.setColor(Theme.getColor(Theme.key_switchTrackChecked));
                } else {
                    this.paint.setColor(Theme.getColor(Theme.key_switchTrack));
                }
                canvas.drawCircle(cx, cy, a == ChatUsersActivity.this.selectedSlowmode ? AndroidUtilities.dp(6.0f) : this.circleSize / 2, this.paint);
                if (a != 0) {
                    int x = ((cx - (this.circleSize / 2)) - this.gapSize) - this.lineSize;
                    int width = this.lineSize;
                    if (a == ChatUsersActivity.this.selectedSlowmode || a == ChatUsersActivity.this.selectedSlowmode + 1) {
                        width -= AndroidUtilities.dp(3.0f);
                    }
                    if (a == ChatUsersActivity.this.selectedSlowmode + 1) {
                        x += AndroidUtilities.dp(3.0f);
                    }
                    canvas.drawRect(x, cy - AndroidUtilities.dp(1.0f), x + width, AndroidUtilities.dp(1.0f) + cy, this.paint);
                }
                int size = this.sizes.get(a).intValue();
                String text = this.strings.get(a);
                if (a == 0) {
                    canvas.drawText(text, AndroidUtilities.dp(22.0f), AndroidUtilities.dp(28.0f), this.textPaint);
                } else if (a == this.strings.size() - 1) {
                    canvas.drawText(text, (getMeasuredWidth() - size) - AndroidUtilities.dp(22.0f), AndroidUtilities.dp(28.0f), this.textPaint);
                } else {
                    canvas.drawText(text, cx - (size / 2), AndroidUtilities.dp(28.0f), this.textPaint);
                }
                a++;
            }
        }
    }

    public ChatUsersActivity(Bundle args) {
        super(args);
        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
        this.currentChat = chat;
        if (chat != null && chat.default_banned_rights != null) {
            this.defaultBannedRights.view_messages = this.currentChat.default_banned_rights.view_messages;
            this.defaultBannedRights.send_stickers = this.currentChat.default_banned_rights.send_stickers;
            this.defaultBannedRights.send_media = this.currentChat.default_banned_rights.send_media;
            this.defaultBannedRights.embed_links = this.currentChat.default_banned_rights.embed_links;
            this.defaultBannedRights.send_messages = this.currentChat.default_banned_rights.send_messages;
            this.defaultBannedRights.send_games = this.currentChat.default_banned_rights.send_games;
            this.defaultBannedRights.send_inline = this.currentChat.default_banned_rights.send_inline;
            this.defaultBannedRights.send_gifs = this.currentChat.default_banned_rights.send_gifs;
            this.defaultBannedRights.pin_messages = this.currentChat.default_banned_rights.pin_messages;
            this.defaultBannedRights.send_polls = this.currentChat.default_banned_rights.send_polls;
            this.defaultBannedRights.invite_users = this.currentChat.default_banned_rights.invite_users;
            this.defaultBannedRights.change_info = this.currentChat.default_banned_rights.change_info;
        }
        this.initialBannedRights = ChatObject.getBannedRightsString(this.defaultBannedRights);
        this.isChannel = ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup;
    }

    private void updateRows() {
        boolean z;
        boolean z2;
        TLRPC.ChatFull chatFull;
        boolean z3;
        TLRPC.ChatFull chatFull2;
        boolean z4;
        TLRPC.ChatFull chatFull3;
        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
        this.currentChat = chat;
        if (chat == null) {
            return;
        }
        this.recentActionsRow = -1;
        this.addNewRow = -1;
        this.addNew2Row = -1;
        this.addNewSectionRow = -1;
        this.restricted1SectionRow = -1;
        this.participantsStartRow = -1;
        this.participantsDividerRow = -1;
        this.participantsDivider2Row = -1;
        this.gigaInfoRow = -1;
        this.gigaConvertRow = -1;
        this.gigaHeaderRow = -1;
        this.participantsEndRow = -1;
        this.participantsInfoRow = -1;
        this.blockedEmptyRow = -1;
        this.permissionsSectionRow = -1;
        this.sendMessagesRow = -1;
        this.sendMediaRow = -1;
        this.sendStickersRow = -1;
        this.sendPollsRow = -1;
        this.embedLinksRow = -1;
        this.addUsersRow = -1;
        this.pinMessagesRow = -1;
        this.changeInfoRow = -1;
        this.removedUsersRow = -1;
        this.contactsHeaderRow = -1;
        this.contactsStartRow = -1;
        this.contactsEndRow = -1;
        this.botHeaderRow = -1;
        this.botStartRow = -1;
        this.botEndRow = -1;
        this.membersHeaderRow = -1;
        this.slowmodeRow = -1;
        this.slowmodeSelectRow = -1;
        this.slowmodeInfoRow = -1;
        this.loadingProgressRow = -1;
        this.loadingUserCellRow = -1;
        this.loadingHeaderRow = -1;
        int i = 0;
        this.rowCount = 0;
        int i2 = this.type;
        if (i2 == 3) {
            int i3 = 0 + 1;
            this.rowCount = i3;
            this.permissionsSectionRow = 0;
            int i4 = i3 + 1;
            this.rowCount = i4;
            this.sendMessagesRow = i3;
            int i5 = i4 + 1;
            this.rowCount = i5;
            this.sendMediaRow = i4;
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.sendStickersRow = i5;
            int i7 = i6 + 1;
            this.rowCount = i7;
            this.sendPollsRow = i6;
            int i8 = i7 + 1;
            this.rowCount = i8;
            this.embedLinksRow = i7;
            int i9 = i8 + 1;
            this.rowCount = i9;
            this.addUsersRow = i8;
            int i10 = i9 + 1;
            this.rowCount = i10;
            this.pinMessagesRow = i9;
            this.rowCount = i10 + 1;
            this.changeInfoRow = i10;
            if (ChatObject.isChannel(chat) && this.currentChat.creator && this.currentChat.megagroup && !this.currentChat.gigagroup) {
                int i11 = this.currentChat.participants_count;
                TLRPC.ChatFull chatFull4 = this.info;
                if (chatFull4 != null) {
                    i = chatFull4.participants_count;
                }
                int count = Math.max(i11, i);
                if (count >= getMessagesController().maxMegagroupCount - 1000) {
                    int i12 = this.rowCount;
                    int i13 = i12 + 1;
                    this.rowCount = i13;
                    this.participantsDivider2Row = i12;
                    int i14 = i13 + 1;
                    this.rowCount = i14;
                    this.gigaHeaderRow = i13;
                    int i15 = i14 + 1;
                    this.rowCount = i15;
                    this.gigaConvertRow = i14;
                    this.rowCount = i15 + 1;
                    this.gigaInfoRow = i15;
                }
            }
            if ((!ChatObject.isChannel(this.currentChat) && this.currentChat.creator) || (this.currentChat.megagroup && !this.currentChat.gigagroup && ChatObject.canBlockUsers(this.currentChat))) {
                if (this.participantsDivider2Row == -1) {
                    int i16 = this.rowCount;
                    this.rowCount = i16 + 1;
                    this.participantsDivider2Row = i16;
                }
                int i17 = this.rowCount;
                int i18 = i17 + 1;
                this.rowCount = i18;
                this.slowmodeRow = i17;
                int i19 = i18 + 1;
                this.rowCount = i19;
                this.slowmodeSelectRow = i18;
                this.rowCount = i19 + 1;
                this.slowmodeInfoRow = i19;
            }
            if (ChatObject.isChannel(this.currentChat)) {
                if (this.participantsDivider2Row == -1) {
                    int i20 = this.rowCount;
                    this.rowCount = i20 + 1;
                    this.participantsDivider2Row = i20;
                }
                int i21 = this.rowCount;
                this.rowCount = i21 + 1;
                this.removedUsersRow = i21;
            }
            if ((this.slowmodeInfoRow == -1 && this.gigaHeaderRow == -1) || this.removedUsersRow != -1) {
                int i22 = this.rowCount;
                this.rowCount = i22 + 1;
                this.participantsDividerRow = i22;
            }
            if (ChatObject.canBlockUsers(this.currentChat) && (ChatObject.isChannel(this.currentChat) || this.currentChat.creator)) {
                int i23 = this.rowCount;
                this.rowCount = i23 + 1;
                this.addNewRow = i23;
            }
            if (this.loadingUsers && !(z4 = this.firstLoaded)) {
                if (!z4 && (chatFull3 = this.info) != null && chatFull3.banned_count > 0) {
                    int i24 = this.rowCount;
                    this.rowCount = i24 + 1;
                    this.loadingUserCellRow = i24;
                    return;
                }
                return;
            }
            if (!this.participants.isEmpty()) {
                int i25 = this.rowCount;
                this.participantsStartRow = i25;
                int size = i25 + this.participants.size();
                this.rowCount = size;
                this.participantsEndRow = size;
            }
            if (this.addNewRow != -1 || this.participantsStartRow != -1) {
                int i26 = this.rowCount;
                this.rowCount = i26 + 1;
                this.addNewSectionRow = i26;
            }
        } else if (i2 == 0) {
            if (ChatObject.canBlockUsers(chat)) {
                int i27 = this.rowCount;
                this.rowCount = i27 + 1;
                this.addNewRow = i27;
                if (!this.participants.isEmpty() || (this.loadingUsers && !this.firstLoaded && (chatFull2 = this.info) != null && chatFull2.kicked_count > 0)) {
                    int i28 = this.rowCount;
                    this.rowCount = i28 + 1;
                    this.participantsInfoRow = i28;
                }
            }
            if (!this.loadingUsers || (z3 = this.firstLoaded)) {
                if (!this.participants.isEmpty()) {
                    int i29 = this.rowCount;
                    int i30 = i29 + 1;
                    this.rowCount = i30;
                    this.restricted1SectionRow = i29;
                    this.participantsStartRow = i30;
                    int size2 = i30 + this.participants.size();
                    this.rowCount = size2;
                    this.participantsEndRow = size2;
                }
                if (this.participantsStartRow != -1) {
                    if (this.participantsInfoRow == -1) {
                        int i31 = this.rowCount;
                        this.rowCount = i31 + 1;
                        this.participantsInfoRow = i31;
                        return;
                    }
                    int i32 = this.rowCount;
                    this.rowCount = i32 + 1;
                    this.addNewSectionRow = i32;
                    return;
                }
                int i33 = this.rowCount;
                this.rowCount = i33 + 1;
                this.blockedEmptyRow = i33;
            } else if (!z3) {
                int i34 = this.rowCount;
                int i35 = i34 + 1;
                this.rowCount = i35;
                this.restricted1SectionRow = i34;
                this.rowCount = i35 + 1;
                this.loadingUserCellRow = i35;
            }
        } else if (i2 == 1) {
            if (ChatObject.isChannel(chat) && this.currentChat.megagroup && !this.currentChat.gigagroup && ((chatFull = this.info) == null || chatFull.participants_count <= 200 || (!this.isChannel && this.info.can_set_stickers))) {
                int i36 = this.rowCount;
                int i37 = i36 + 1;
                this.rowCount = i37;
                this.recentActionsRow = i36;
                this.rowCount = i37 + 1;
                this.addNewSectionRow = i37;
            }
            if (ChatObject.canAddAdmins(this.currentChat)) {
                int i38 = this.rowCount;
                this.rowCount = i38 + 1;
                this.addNewRow = i38;
            }
            if (!this.loadingUsers || (z2 = this.firstLoaded)) {
                if (!this.participants.isEmpty()) {
                    int i39 = this.rowCount;
                    this.participantsStartRow = i39;
                    int size3 = i39 + this.participants.size();
                    this.rowCount = size3;
                    this.participantsEndRow = size3;
                }
                int i40 = this.rowCount;
                this.rowCount = i40 + 1;
                this.participantsInfoRow = i40;
            } else if (!z2) {
                int i41 = this.rowCount;
                this.rowCount = i41 + 1;
                this.loadingUserCellRow = i41;
            }
        } else if (i2 == 2) {
            if (this.selectType == 0 && ChatObject.canAddUsers(chat)) {
                int i42 = this.rowCount;
                this.rowCount = i42 + 1;
                this.addNewRow = i42;
            }
            if (this.selectType == 0 && ChatObject.canUserDoAdminAction(this.currentChat, 3)) {
                int i43 = this.rowCount;
                this.rowCount = i43 + 1;
                this.addNew2Row = i43;
            }
            if (!this.loadingUsers || (z = this.firstLoaded)) {
                boolean hasAnyOther = false;
                if (!this.contacts.isEmpty()) {
                    int i44 = this.rowCount;
                    int i45 = i44 + 1;
                    this.rowCount = i45;
                    this.contactsHeaderRow = i44;
                    this.contactsStartRow = i45;
                    int size4 = i45 + this.contacts.size();
                    this.rowCount = size4;
                    this.contactsEndRow = size4;
                    hasAnyOther = true;
                }
                if (!this.bots.isEmpty()) {
                    int i46 = this.rowCount;
                    int i47 = i46 + 1;
                    this.rowCount = i47;
                    this.botHeaderRow = i46;
                    this.botStartRow = i47;
                    int size5 = i47 + this.bots.size();
                    this.rowCount = size5;
                    this.botEndRow = size5;
                    hasAnyOther = true;
                }
                if (!this.participants.isEmpty()) {
                    if (hasAnyOther) {
                        int i48 = this.rowCount;
                        this.rowCount = i48 + 1;
                        this.membersHeaderRow = i48;
                    }
                    int i49 = this.rowCount;
                    this.participantsStartRow = i49;
                    int size6 = i49 + this.participants.size();
                    this.rowCount = size6;
                    this.participantsEndRow = size6;
                }
                int i50 = this.rowCount;
                if (i50 != 0) {
                    this.rowCount = i50 + 1;
                    this.participantsInfoRow = i50;
                }
            } else if (!z) {
                if (this.selectType == 0) {
                    int i51 = this.rowCount;
                    this.rowCount = i51 + 1;
                    this.loadingHeaderRow = i51;
                }
                int i52 = this.rowCount;
                this.rowCount = i52 + 1;
                this.loadingUserCellRow = i52;
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getNotificationCenter().addObserver(this, NotificationCenter.chatInfoDidLoad);
        loadChatParticipants(0, 200);
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        int i;
        this.searching = false;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        int i2 = 1;
        this.actionBar.setAllowOverlayTitle(true);
        int i3 = this.type;
        if (i3 == 3) {
            this.actionBar.setTitle(LocaleController.getString("ChannelPermissions", R.string.ChannelPermissions));
        } else if (i3 == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist));
        } else if (i3 == 1) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators));
        } else if (i3 == 2) {
            int i4 = this.selectType;
            if (i4 == 0) {
                if (this.isChannel) {
                    this.actionBar.setTitle(LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("ChannelMembers", R.string.ChannelMembers));
                }
            } else if (i4 == 1) {
                this.actionBar.setTitle(LocaleController.getString("ChannelAddAdmin", R.string.ChannelAddAdmin));
            } else if (i4 == 2) {
                this.actionBar.setTitle(LocaleController.getString("ChannelBlockUser", R.string.ChannelBlockUser));
            } else if (i4 == 3) {
                this.actionBar.setTitle(LocaleController.getString("ChannelAddException", R.string.ChannelAddException));
            }
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ChatUsersActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    if (ChatUsersActivity.this.checkDiscard()) {
                        ChatUsersActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    ChatUsersActivity.this.processDone();
                }
            }
        });
        if (this.selectType != 0 || (i = this.type) == 2 || i == 0 || i == 3) {
            this.searchListViewAdapter = new SearchAdapter(context);
            ActionBarMenu menu = this.actionBar.createMenu();
            ActionBarMenuItem actionBarMenuItemSearchListener = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.ChatUsersActivity.2
                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                public void onSearchExpand() {
                    ChatUsersActivity.this.searching = true;
                    if (ChatUsersActivity.this.doneItem != null) {
                        ChatUsersActivity.this.doneItem.setVisibility(8);
                    }
                }

                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                public void onSearchCollapse() {
                    ChatUsersActivity.this.searchListViewAdapter.searchUsers(null);
                    ChatUsersActivity.this.searching = false;
                    ChatUsersActivity.this.listView.setAnimateEmptyView(false, 0);
                    ChatUsersActivity.this.listView.setAdapter(ChatUsersActivity.this.listViewAdapter);
                    ChatUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                    ChatUsersActivity.this.listView.setFastScrollVisible(true);
                    ChatUsersActivity.this.listView.setVerticalScrollBarEnabled(false);
                    if (ChatUsersActivity.this.doneItem != null) {
                        ChatUsersActivity.this.doneItem.setVisibility(0);
                    }
                }

                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                public void onTextChanged(EditText editText) {
                    if (ChatUsersActivity.this.searchListViewAdapter == null) {
                        return;
                    }
                    String text = editText.getText().toString();
                    int oldItemsCount = ChatUsersActivity.this.listView.getAdapter() == null ? 0 : ChatUsersActivity.this.listView.getAdapter().getItemCount();
                    ChatUsersActivity.this.searchListViewAdapter.searchUsers(text);
                    if (TextUtils.isEmpty(text) && ChatUsersActivity.this.listView != null && ChatUsersActivity.this.listView.getAdapter() != ChatUsersActivity.this.listViewAdapter) {
                        ChatUsersActivity.this.listView.setAnimateEmptyView(false, 0);
                        ChatUsersActivity.this.listView.setAdapter(ChatUsersActivity.this.listViewAdapter);
                        if (oldItemsCount == 0) {
                            ChatUsersActivity.this.showItemsAnimated(0);
                        }
                    }
                    ChatUsersActivity.this.progressBar.setVisibility(8);
                    ChatUsersActivity.this.flickerLoadingView.setVisibility(0);
                }
            });
            this.searchItem = actionBarMenuItemSearchListener;
            if (this.type == 0 && !this.firstLoaded) {
                actionBarMenuItemSearchListener.setVisibility(8);
            }
            if (this.type == 3) {
                this.searchItem.setSearchFieldHint(LocaleController.getString("ChannelSearchException", R.string.ChannelSearchException));
            } else {
                this.searchItem.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
            }
            if (!ChatObject.isChannel(this.currentChat) && !this.currentChat.creator) {
                this.searchItem.setVisibility(8);
            }
            if (this.type == 3) {
                this.doneItem = menu.addItemWithWidth(1, R.drawable.ic_ab_done, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", R.string.Done));
            }
        }
        this.fragmentView = new FrameLayout(context) { // from class: org.telegram.ui.ChatUsersActivity.3
            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                canvas.drawColor(Theme.getColor(ChatUsersActivity.this.listView.getAdapter() == ChatUsersActivity.this.searchListViewAdapter ? Theme.key_windowBackgroundWhite : Theme.key_windowBackgroundGray));
                super.dispatchDraw(canvas);
            }
        };
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        FrameLayout progressLayout = new FrameLayout(context);
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        this.flickerLoadingView = flickerLoadingView;
        flickerLoadingView.setViewType(6);
        this.flickerLoadingView.showDate(false);
        this.flickerLoadingView.setUseHeaderOffset(true);
        progressLayout.addView(this.flickerLoadingView);
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressBar = radialProgressView;
        progressLayout.addView(radialProgressView, LayoutHelper.createFrame(-2, -2, 17));
        this.flickerLoadingView.setVisibility(8);
        this.progressBar.setVisibility(8);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context, progressLayout, 1);
        this.emptyView = stickerEmptyView;
        stickerEmptyView.title.setText(LocaleController.getString("NoResult", R.string.NoResult));
        this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", R.string.SearchEmptyViewFilteredSubtitle2));
        this.emptyView.setVisibility(8);
        this.emptyView.setAnimateLayoutChange(true);
        this.emptyView.showProgress(true, false);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.addView(progressLayout, 0);
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.ChatUsersActivity.4
            @Override // android.view.View
            public void invalidate() {
                super.invalidate();
                if (ChatUsersActivity.this.fragmentView != null) {
                    ChatUsersActivity.this.fragmentView.invalidate();
                }
            }
        };
        this.listView = recyclerListView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false) { // from class: org.telegram.ui.ChatUsersActivity.5
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
                if (!ChatUsersActivity.this.firstLoaded && ChatUsersActivity.this.type == 0 && ChatUsersActivity.this.participants.size() == 0) {
                    return 0;
                }
                return super.scrollVerticallyBy(dy, recycler, state);
            }
        };
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator() { // from class: org.telegram.ui.ChatUsersActivity.6
            int animationIndex = -1;

            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            protected long getAddAnimationDelay(long removeDuration, long moveDuration, long changeDuration) {
                return 0L;
            }

            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            protected long getMoveAnimationDelay() {
                return 0L;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
            public long getMoveDuration() {
                return 220L;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
            public long getRemoveDuration() {
                return 220L;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
            public long getAddDuration() {
                return 220L;
            }

            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            public void onAllAnimationsDone() {
                super.onAllAnimationsDone();
                ChatUsersActivity.this.getNotificationCenter().onAnimationFinish(this.animationIndex);
            }

            @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
            public void runPendingAnimations() {
                boolean removalsPending = !this.mPendingRemovals.isEmpty();
                boolean movesPending = !this.mPendingMoves.isEmpty();
                boolean changesPending = !this.mPendingChanges.isEmpty();
                boolean additionsPending = !this.mPendingAdditions.isEmpty();
                if (removalsPending || movesPending || additionsPending || changesPending) {
                    this.animationIndex = ChatUsersActivity.this.getNotificationCenter().setAnimationInProgress(this.animationIndex, null);
                }
                super.runPendingAnimations();
            }
        };
        this.listView.setItemAnimator(itemAnimator);
        itemAnimator.setSupportsChangeAnimations(false);
        this.listView.setAnimateEmptyView(true, 0);
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        RecyclerListView recyclerListView3 = this.listView;
        if (!LocaleController.isRTL) {
            i2 = 2;
        }
        recyclerListView3.setVerticalScrollbarPosition(i2);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i5) {
                ChatUsersActivity.this.m2156lambda$createView$1$orgtelegramuiChatUsersActivity(context, view, i5);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda9
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i5) {
                return ChatUsersActivity.this.m2157lambda$createView$2$orgtelegramuiChatUsersActivity(view, i5);
            }
        });
        if (this.searchItem != null) {
            this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.ChatUsersActivity.12
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == 1) {
                        AndroidUtilities.hideKeyboard(ChatUsersActivity.this.getParentActivity().getCurrentFocus());
                    }
                }

                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                }
            });
        }
        UndoView undoView = new UndoView(context);
        this.undoView = undoView;
        frameLayout.addView(undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        updateRows();
        this.listView.setEmptyView(this.emptyView);
        this.listView.setAnimateEmptyView(false, 0);
        if (this.needOpenSearch) {
            this.searchItem.openSearch(false);
        }
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-ChatUsersActivity */
    public /* synthetic */ void m2156lambda$createView$1$orgtelegramuiChatUsersActivity(Context context, View view, int position) {
        boolean canEditAdmin;
        String rank;
        TLRPC.TL_chatAdminRights adminRights;
        TLRPC.TL_chatBannedRights bannedRights;
        TLObject participant;
        long peerId;
        long peerId2;
        int i;
        int i2;
        long peerId3;
        boolean z;
        TLObject participant2;
        TLRPC.TL_chatBannedRights tL_chatBannedRights;
        TLRPC.TL_chatBannedRights tL_chatBannedRights2;
        TLRPC.TL_chatBannedRights tL_chatBannedRights3;
        TLRPC.TL_chatBannedRights tL_chatBannedRights4;
        TLRPC.TL_chatBannedRights tL_chatBannedRights5;
        TLRPC.TL_chatBannedRights tL_chatBannedRights6;
        TLRPC.TL_chatBannedRights tL_chatBannedRights7;
        boolean listAdapter = this.listView.getAdapter() == this.listViewAdapter;
        int i3 = 3;
        if (listAdapter) {
            if (position != this.addNewRow) {
                if (position == this.recentActionsRow) {
                    presentFragment(new ChannelAdminLogActivity(this.currentChat));
                    return;
                } else if (position == this.removedUsersRow) {
                    Bundle args = new Bundle();
                    args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, this.chatId);
                    args.putInt(CommonProperties.TYPE, 0);
                    ChatUsersActivity fragment = new ChatUsersActivity(args);
                    fragment.setInfo(this.info);
                    presentFragment(fragment);
                    return;
                } else if (position == this.gigaConvertRow) {
                    showDialog(new AnonymousClass10(getParentActivity(), this));
                } else if (position == this.addNew2Row) {
                    if (this.info != null) {
                        ManageLinksActivity fragment2 = new ManageLinksActivity(this.chatId, 0L, 0);
                        TLRPC.ChatFull chatFull = this.info;
                        fragment2.setInfo(chatFull, chatFull.exported_invite);
                        presentFragment(fragment2);
                        return;
                    }
                    return;
                } else if (position > this.permissionsSectionRow && position <= this.changeInfoRow) {
                    TextCheckCell2 checkCell = (TextCheckCell2) view;
                    if (!checkCell.isEnabled()) {
                        return;
                    }
                    if (checkCell.hasIcon()) {
                        if (!TextUtils.isEmpty(this.currentChat.username) && (position == this.pinMessagesRow || position == this.changeInfoRow)) {
                            BulletinFactory.of(this).createErrorBulletin(LocaleController.getString("EditCantEditPermissionsPublic", R.string.EditCantEditPermissionsPublic)).show();
                            return;
                        } else {
                            BulletinFactory.of(this).createErrorBulletin(LocaleController.getString("EditCantEditPermissions", R.string.EditCantEditPermissions)).show();
                            return;
                        }
                    }
                    checkCell.setChecked(!checkCell.isChecked());
                    if (position == this.changeInfoRow) {
                        this.defaultBannedRights.change_info = !tL_chatBannedRights7.change_info;
                        return;
                    } else if (position == this.addUsersRow) {
                        this.defaultBannedRights.invite_users = !tL_chatBannedRights6.invite_users;
                        return;
                    } else if (position == this.pinMessagesRow) {
                        this.defaultBannedRights.pin_messages = !tL_chatBannedRights5.pin_messages;
                        return;
                    } else {
                        boolean disabled = !checkCell.isChecked();
                        if (position == this.sendMessagesRow) {
                            this.defaultBannedRights.send_messages = !tL_chatBannedRights4.send_messages;
                        } else if (position == this.sendMediaRow) {
                            this.defaultBannedRights.send_media = !tL_chatBannedRights3.send_media;
                        } else if (position == this.sendStickersRow) {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights8 = this.defaultBannedRights;
                            boolean z2 = !tL_chatBannedRights8.send_stickers;
                            tL_chatBannedRights8.send_inline = z2;
                            tL_chatBannedRights8.send_gifs = z2;
                            tL_chatBannedRights8.send_games = z2;
                            tL_chatBannedRights8.send_stickers = z2;
                        } else if (position == this.embedLinksRow) {
                            this.defaultBannedRights.embed_links = !tL_chatBannedRights2.embed_links;
                        } else if (position == this.sendPollsRow) {
                            this.defaultBannedRights.send_polls = !tL_chatBannedRights.send_polls;
                        }
                        if (disabled) {
                            if (this.defaultBannedRights.view_messages && !this.defaultBannedRights.send_messages) {
                                this.defaultBannedRights.send_messages = true;
                                RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.sendMessagesRow);
                                if (holder != null) {
                                    ((TextCheckCell2) holder.itemView).setChecked(false);
                                }
                            }
                            if ((this.defaultBannedRights.view_messages || this.defaultBannedRights.send_messages) && !this.defaultBannedRights.send_media) {
                                this.defaultBannedRights.send_media = true;
                                RecyclerView.ViewHolder holder2 = this.listView.findViewHolderForAdapterPosition(this.sendMediaRow);
                                if (holder2 != null) {
                                    ((TextCheckCell2) holder2.itemView).setChecked(false);
                                }
                            }
                            if ((this.defaultBannedRights.view_messages || this.defaultBannedRights.send_messages) && !this.defaultBannedRights.send_polls) {
                                this.defaultBannedRights.send_polls = true;
                                RecyclerView.ViewHolder holder3 = this.listView.findViewHolderForAdapterPosition(this.sendPollsRow);
                                if (holder3 != null) {
                                    ((TextCheckCell2) holder3.itemView).setChecked(false);
                                }
                            }
                            if ((this.defaultBannedRights.view_messages || this.defaultBannedRights.send_messages) && !this.defaultBannedRights.send_stickers) {
                                TLRPC.TL_chatBannedRights tL_chatBannedRights9 = this.defaultBannedRights;
                                tL_chatBannedRights9.send_inline = true;
                                tL_chatBannedRights9.send_gifs = true;
                                tL_chatBannedRights9.send_games = true;
                                tL_chatBannedRights9.send_stickers = true;
                                RecyclerView.ViewHolder holder4 = this.listView.findViewHolderForAdapterPosition(this.sendStickersRow);
                                if (holder4 != null) {
                                    ((TextCheckCell2) holder4.itemView).setChecked(false);
                                }
                            }
                            if ((this.defaultBannedRights.view_messages || this.defaultBannedRights.send_messages) && !this.defaultBannedRights.embed_links) {
                                this.defaultBannedRights.embed_links = true;
                                RecyclerView.ViewHolder holder5 = this.listView.findViewHolderForAdapterPosition(this.embedLinksRow);
                                if (holder5 != null) {
                                    ((TextCheckCell2) holder5.itemView).setChecked(false);
                                    return;
                                }
                                return;
                            }
                            return;
                        } else if ((!this.defaultBannedRights.embed_links || !this.defaultBannedRights.send_inline || !this.defaultBannedRights.send_media || !this.defaultBannedRights.send_polls) && this.defaultBannedRights.send_messages) {
                            this.defaultBannedRights.send_messages = false;
                            RecyclerView.ViewHolder holder6 = this.listView.findViewHolderForAdapterPosition(this.sendMessagesRow);
                            if (holder6 != null) {
                                ((TextCheckCell2) holder6.itemView).setChecked(true);
                                return;
                            }
                            return;
                        } else {
                            return;
                        }
                    }
                }
            } else {
                int i4 = this.type;
                if (i4 != 0 && i4 != 3) {
                    if (i4 == 1) {
                        Bundle bundle = new Bundle();
                        bundle.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, this.chatId);
                        bundle.putInt(CommonProperties.TYPE, 2);
                        bundle.putInt("selectType", 1);
                        ChatUsersActivity fragment3 = new ChatUsersActivity(bundle);
                        fragment3.setDelegate(new AnonymousClass8());
                        fragment3.setInfo(this.info);
                        presentFragment(fragment3);
                        return;
                    } else if (i4 == 2) {
                        Bundle args2 = new Bundle();
                        args2.putBoolean("addToGroup", true);
                        args2.putLong(this.isChannel ? "channelId" : "chatId", this.currentChat.id);
                        GroupCreateActivity fragment4 = new GroupCreateActivity(args2);
                        fragment4.setInfo(this.info);
                        LongSparseArray<TLObject> longSparseArray = this.contactsMap;
                        fragment4.setIgnoreUsers((longSparseArray == null || longSparseArray.size() == 0) ? this.participantsMap : this.contactsMap);
                        fragment4.setDelegate(new AnonymousClass9(context));
                        presentFragment(fragment4);
                        return;
                    } else {
                        return;
                    }
                }
                Bundle bundle2 = new Bundle();
                bundle2.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, this.chatId);
                bundle2.putInt(CommonProperties.TYPE, 2);
                if (this.type == 0) {
                    i3 = 2;
                }
                bundle2.putInt("selectType", i3);
                ChatUsersActivity fragment5 = new ChatUsersActivity(bundle2);
                fragment5.setInfo(this.info);
                fragment5.setDelegate(new ChatUsersActivityDelegate() { // from class: org.telegram.ui.ChatUsersActivity.7
                    @Override // org.telegram.ui.ChatUsersActivity.ChatUsersActivityDelegate
                    public /* synthetic */ void didChangeOwner(TLRPC.User user) {
                        ChatUsersActivityDelegate.CC.$default$didChangeOwner(this, user);
                    }

                    @Override // org.telegram.ui.ChatUsersActivity.ChatUsersActivityDelegate
                    public /* synthetic */ void didSelectUser(long j) {
                        ChatUsersActivityDelegate.CC.$default$didSelectUser(this, j);
                    }

                    @Override // org.telegram.ui.ChatUsersActivity.ChatUsersActivityDelegate
                    public void didAddParticipantToList(long uid, TLObject participant3) {
                        if (ChatUsersActivity.this.participantsMap.get(uid) == null) {
                            DiffCallback diffCallback = ChatUsersActivity.this.saveState();
                            ChatUsersActivity.this.participants.add(participant3);
                            ChatUsersActivity.this.participantsMap.put(uid, participant3);
                            ChatUsersActivity chatUsersActivity = ChatUsersActivity.this;
                            chatUsersActivity.sortUsers(chatUsersActivity.participants);
                            ChatUsersActivity.this.updateListAnimated(diffCallback);
                        }
                    }

                    @Override // org.telegram.ui.ChatUsersActivity.ChatUsersActivityDelegate
                    public void didKickParticipant(long uid) {
                        if (ChatUsersActivity.this.participantsMap.get(uid) == null) {
                            DiffCallback diffCallback = ChatUsersActivity.this.saveState();
                            TLRPC.TL_channelParticipantBanned chatParticipant = new TLRPC.TL_channelParticipantBanned();
                            if (uid > 0) {
                                chatParticipant.peer = new TLRPC.TL_peerUser();
                                chatParticipant.peer.user_id = uid;
                            } else {
                                chatParticipant.peer = new TLRPC.TL_peerChannel();
                                chatParticipant.peer.channel_id = -uid;
                            }
                            chatParticipant.date = ChatUsersActivity.this.getConnectionsManager().getCurrentTime();
                            chatParticipant.kicked_by = ChatUsersActivity.this.getAccountInstance().getUserConfig().clientUserId;
                            ChatUsersActivity.this.info.kicked_count++;
                            ChatUsersActivity.this.participants.add(chatParticipant);
                            ChatUsersActivity.this.participantsMap.put(uid, chatParticipant);
                            ChatUsersActivity chatUsersActivity = ChatUsersActivity.this;
                            chatUsersActivity.sortUsers(chatUsersActivity.participants);
                            ChatUsersActivity.this.updateListAnimated(diffCallback);
                        }
                    }
                });
                presentFragment(fragment5);
                return;
            }
        }
        TLRPC.TL_chatBannedRights bannedRights2 = null;
        TLRPC.TL_chatAdminRights adminRights2 = null;
        String rank2 = "";
        long peerId4 = 0;
        boolean canEditAdmin2 = false;
        if (listAdapter) {
            TLObject participant3 = this.listViewAdapter.getItem(position);
            if (participant3 instanceof TLRPC.ChannelParticipant) {
                TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) participant3;
                peerId4 = MessageObject.getPeerId(channelParticipant.peer);
                bannedRights2 = channelParticipant.banned_rights;
                TLRPC.TL_chatAdminRights adminRights3 = channelParticipant.admin_rights;
                rank2 = channelParticipant.rank;
                canEditAdmin2 = (!(channelParticipant instanceof TLRPC.TL_channelParticipantAdmin) && !(channelParticipant instanceof TLRPC.TL_channelParticipantCreator)) || channelParticipant.can_edit;
                if (!(participant3 instanceof TLRPC.TL_channelParticipantCreator)) {
                    adminRights2 = adminRights3;
                } else {
                    TLRPC.TL_chatAdminRights adminRights4 = ((TLRPC.TL_channelParticipantCreator) participant3).admin_rights;
                    if (adminRights4 != null) {
                        adminRights2 = adminRights4;
                    } else {
                        TLRPC.TL_chatAdminRights adminRights5 = new TLRPC.TL_chatAdminRights();
                        adminRights5.add_admins = true;
                        adminRights5.pin_messages = true;
                        adminRights5.invite_users = true;
                        adminRights5.ban_users = true;
                        adminRights5.delete_messages = true;
                        adminRights5.edit_messages = true;
                        adminRights5.post_messages = true;
                        adminRights5.change_info = true;
                        if (!this.isChannel) {
                            adminRights5.manage_call = true;
                        }
                        adminRights2 = adminRights5;
                    }
                }
            } else if (participant3 instanceof TLRPC.ChatParticipant) {
                TLRPC.ChatParticipant chatParticipant = (TLRPC.ChatParticipant) participant3;
                long peerId5 = chatParticipant.user_id;
                boolean canEditAdmin3 = this.currentChat.creator;
                if (participant3 instanceof TLRPC.TL_chatParticipantCreator) {
                    adminRights2 = new TLRPC.TL_chatAdminRights();
                    adminRights2.add_admins = true;
                    adminRights2.pin_messages = true;
                    adminRights2.invite_users = true;
                    adminRights2.ban_users = true;
                    adminRights2.delete_messages = true;
                    adminRights2.edit_messages = true;
                    adminRights2.post_messages = true;
                    adminRights2.change_info = true;
                    if (!this.isChannel) {
                        adminRights2.manage_call = true;
                    }
                }
                canEditAdmin = canEditAdmin3;
                bannedRights = null;
                adminRights = adminRights2;
                rank = rank2;
                peerId = peerId5;
                participant = participant3;
            }
            adminRights = adminRights2;
            rank = rank2;
            peerId = peerId4;
            canEditAdmin = canEditAdmin2;
            participant = participant3;
            bannedRights = bannedRights2;
        } else {
            TLObject object = this.searchListViewAdapter.getItem(position);
            if (object instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) object;
                getMessagesController().putUser(user, false);
                long j = user.id;
                peerId4 = j;
                participant2 = getAnyParticipant(j);
            } else if ((object instanceof TLRPC.ChannelParticipant) || (object instanceof TLRPC.ChatParticipant)) {
                participant2 = object;
            } else {
                participant2 = null;
            }
            if (participant2 instanceof TLRPC.ChannelParticipant) {
                TLRPC.ChannelParticipant channelParticipant2 = (TLRPC.ChannelParticipant) participant2;
                long peerId6 = MessageObject.getPeerId(channelParticipant2.peer);
                boolean canEditAdmin4 = (!(channelParticipant2 instanceof TLRPC.TL_channelParticipantAdmin) && !(channelParticipant2 instanceof TLRPC.TL_channelParticipantCreator)) || channelParticipant2.can_edit;
                TLRPC.TL_chatBannedRights bannedRights3 = channelParticipant2.banned_rights;
                adminRights = channelParticipant2.admin_rights;
                rank = channelParticipant2.rank;
                peerId = peerId6;
                canEditAdmin = canEditAdmin4;
                participant = participant2;
                bannedRights = bannedRights3;
            } else if (participant2 instanceof TLRPC.ChatParticipant) {
                TLRPC.ChatParticipant chatParticipant2 = (TLRPC.ChatParticipant) participant2;
                long peerId7 = chatParticipant2.user_id;
                boolean canEditAdmin5 = this.currentChat.creator;
                canEditAdmin = canEditAdmin5;
                bannedRights = null;
                adminRights = null;
                rank = rank2;
                peerId = peerId7;
                participant = participant2;
            } else if (participant2 == null) {
                adminRights = null;
                rank = rank2;
                peerId = peerId4;
                canEditAdmin = true;
                participant = participant2;
                bannedRights = null;
            } else {
                adminRights = null;
                rank = rank2;
                peerId = peerId4;
                canEditAdmin = false;
                participant = participant2;
                bannedRights = null;
            }
        }
        if (peerId != 0) {
            int i5 = this.selectType;
            if (i5 != 0) {
                if (i5 != 3) {
                    i2 = 1;
                    if (i5 != 1) {
                        removeParticipant(peerId);
                        return;
                    }
                } else {
                    i2 = 1;
                }
                if (i5 == i2 || !canEditAdmin) {
                    peerId3 = peerId;
                    z = false;
                } else if ((participant instanceof TLRPC.TL_channelParticipantAdmin) || (participant instanceof TLRPC.TL_chatParticipantAdmin)) {
                    final TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(peerId));
                    final TLRPC.TL_chatBannedRights br = bannedRights;
                    final TLRPC.TL_chatAdminRights ar = adminRights;
                    final boolean canEdit = canEditAdmin;
                    final String rankFinal = rank;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setMessage(LocaleController.formatString("AdminWillBeRemoved", R.string.AdminWillBeRemoved, UserObject.getUserName(user2)));
                    final TLObject tLObject = participant;
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda13
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i6) {
                            ChatUsersActivity.this.m2155lambda$createView$0$orgtelegramuiChatUsersActivity(user2, tLObject, ar, br, rankFinal, canEdit, dialogInterface, i6);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    showDialog(builder.create());
                    return;
                } else {
                    peerId3 = peerId;
                    z = false;
                }
                int i6 = i5 == 1 ? 0 : 1;
                if (i5 == 1 || i5 == 3) {
                    z = true;
                }
                openRightsEdit(peerId3, participant, adminRights, bannedRights, rank, canEditAdmin, i6, z);
                return;
            }
            long peerId8 = peerId;
            final TLObject participant4 = participant;
            boolean canEdit2 = false;
            int i7 = this.type;
            if (i7 == 1) {
                peerId2 = peerId8;
                canEdit2 = peerId2 != getUserConfig().getClientUserId() && (this.currentChat.creator || canEditAdmin);
            } else {
                peerId2 = peerId8;
                if (i7 == 0 || i7 == 3) {
                    canEdit2 = ChatObject.canBlockUsers(this.currentChat);
                }
            }
            int i8 = this.type;
            if (i8 == 0 || ((i8 != 1 && this.isChannel) || (i8 == 2 && this.selectType == 0))) {
                if (peerId2 == getUserConfig().getClientUserId()) {
                    return;
                }
                Bundle args3 = new Bundle();
                if (peerId2 <= 0) {
                    args3.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, -peerId2);
                } else {
                    args3.putLong("user_id", peerId2);
                }
                presentFragment(new ProfileActivity(args3));
                return;
            }
            if (bannedRights != null) {
                i = 1;
            } else {
                TLRPC.TL_chatBannedRights bannedRights4 = new TLRPC.TL_chatBannedRights();
                i = 1;
                bannedRights4.view_messages = true;
                bannedRights4.send_stickers = true;
                bannedRights4.send_media = true;
                bannedRights4.embed_links = true;
                bannedRights4.send_messages = true;
                bannedRights4.send_games = true;
                bannedRights4.send_inline = true;
                bannedRights4.send_gifs = true;
                bannedRights4.pin_messages = true;
                bannedRights4.send_polls = true;
                bannedRights4.invite_users = true;
                bannedRights4.change_info = true;
                bannedRights = bannedRights4;
            }
            ChatRightsEditActivity fragment6 = new ChatRightsEditActivity(peerId2, this.chatId, adminRights, this.defaultBannedRights, bannedRights, rank, this.type == i ? 0 : 1, canEdit2, participant4 == null, null);
            fragment6.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() { // from class: org.telegram.ui.ChatUsersActivity.11
                @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
                public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank3) {
                    TLObject tLObject2 = participant4;
                    if (tLObject2 instanceof TLRPC.ChannelParticipant) {
                        TLRPC.ChannelParticipant channelParticipant3 = (TLRPC.ChannelParticipant) tLObject2;
                        channelParticipant3.admin_rights = rightsAdmin;
                        channelParticipant3.banned_rights = rightsBanned;
                        channelParticipant3.rank = rank3;
                        ChatUsersActivity.this.updateParticipantWithRights(channelParticipant3, rightsAdmin, rightsBanned, 0L, false);
                    }
                }

                @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
                public void didChangeOwner(TLRPC.User user3) {
                    ChatUsersActivity.this.onOwnerChaged(user3);
                }
            });
            presentFragment(fragment6);
        }
    }

    /* renamed from: org.telegram.ui.ChatUsersActivity$8 */
    /* loaded from: classes4.dex */
    public class AnonymousClass8 implements ChatUsersActivityDelegate {
        @Override // org.telegram.ui.ChatUsersActivity.ChatUsersActivityDelegate
        public /* synthetic */ void didKickParticipant(long j) {
            ChatUsersActivityDelegate.CC.$default$didKickParticipant(this, j);
        }

        AnonymousClass8() {
            ChatUsersActivity.this = this$0;
        }

        @Override // org.telegram.ui.ChatUsersActivity.ChatUsersActivityDelegate
        public void didAddParticipantToList(long uid, TLObject participant) {
            if (participant != null && ChatUsersActivity.this.participantsMap.get(uid) == null) {
                DiffCallback diffCallback = ChatUsersActivity.this.saveState();
                ChatUsersActivity.this.participants.add(participant);
                ChatUsersActivity.this.participantsMap.put(uid, participant);
                ChatUsersActivity chatUsersActivity = ChatUsersActivity.this;
                chatUsersActivity.sortAdmins(chatUsersActivity.participants);
                ChatUsersActivity.this.updateListAnimated(diffCallback);
            }
        }

        @Override // org.telegram.ui.ChatUsersActivity.ChatUsersActivityDelegate
        public void didChangeOwner(TLRPC.User user) {
            ChatUsersActivity.this.onOwnerChaged(user);
        }

        @Override // org.telegram.ui.ChatUsersActivity.ChatUsersActivityDelegate
        public void didSelectUser(long uid) {
            final TLRPC.User user = ChatUsersActivity.this.getMessagesController().getUser(Long.valueOf(uid));
            if (user != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatUsersActivity$8$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatUsersActivity.AnonymousClass8.this.m2166lambda$didSelectUser$0$orgtelegramuiChatUsersActivity$8(user);
                    }
                }, 200L);
            }
            if (ChatUsersActivity.this.participantsMap.get(uid) == null) {
                DiffCallback diffCallback = ChatUsersActivity.this.saveState();
                TLRPC.TL_channelParticipantAdmin chatParticipant = new TLRPC.TL_channelParticipantAdmin();
                chatParticipant.peer = new TLRPC.TL_peerUser();
                chatParticipant.peer.user_id = user.id;
                chatParticipant.date = ChatUsersActivity.this.getConnectionsManager().getCurrentTime();
                chatParticipant.promoted_by = ChatUsersActivity.this.getAccountInstance().getUserConfig().clientUserId;
                ChatUsersActivity.this.participants.add(chatParticipant);
                ChatUsersActivity.this.participantsMap.put(user.id, chatParticipant);
                ChatUsersActivity chatUsersActivity = ChatUsersActivity.this;
                chatUsersActivity.sortAdmins(chatUsersActivity.participants);
                ChatUsersActivity.this.updateListAnimated(diffCallback);
            }
        }

        /* renamed from: lambda$didSelectUser$0$org-telegram-ui-ChatUsersActivity$8 */
        public /* synthetic */ void m2166lambda$didSelectUser$0$orgtelegramuiChatUsersActivity$8(TLRPC.User user) {
            if (BulletinFactory.canShowBulletin(ChatUsersActivity.this)) {
                BulletinFactory.createPromoteToAdminBulletin(ChatUsersActivity.this, user.first_name).show();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatUsersActivity$9 */
    /* loaded from: classes4.dex */
    public class AnonymousClass9 implements GroupCreateActivity.ContactsAddActivityDelegate {
        final /* synthetic */ Context val$context;

        AnonymousClass9(Context context) {
            ChatUsersActivity.this = this$0;
            this.val$context = context;
        }

        @Override // org.telegram.ui.GroupCreateActivity.ContactsAddActivityDelegate
        public void didSelectUsers(ArrayList<TLRPC.User> arrayList, int fwdCount) {
            final int count = arrayList.size();
            final ArrayList userRestrictedPrivacy = new ArrayList();
            final int[] processed = {0};
            final Context context = this.val$context;
            final Runnable showUserRestrictedPrivacyAlert = new Runnable() { // from class: org.telegram.ui.ChatUsersActivity$9$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ChatUsersActivity.AnonymousClass9.lambda$didSelectUsers$0(userRestrictedPrivacy, count, context);
                }
            };
            int a = 0;
            while (a < count) {
                final TLRPC.User user = arrayList.get(a);
                final ArrayList userRestrictedPrivacy2 = userRestrictedPrivacy;
                final ArrayList arrayList2 = userRestrictedPrivacy;
                ChatUsersActivity.this.getMessagesController().addUserToChat(ChatUsersActivity.this.chatId, user, fwdCount, null, ChatUsersActivity.this, false, new Runnable() { // from class: org.telegram.ui.ChatUsersActivity$9$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatUsersActivity.AnonymousClass9.this.m2167lambda$didSelectUsers$1$orgtelegramuiChatUsersActivity$9(processed, count, userRestrictedPrivacy2, showUserRestrictedPrivacyAlert, user);
                    }
                }, new MessagesController.ErrorDelegate() { // from class: org.telegram.ui.ChatUsersActivity$9$$ExternalSyntheticLambda2
                    @Override // org.telegram.messenger.MessagesController.ErrorDelegate
                    public final boolean run(TLRPC.TL_error tL_error) {
                        return ChatUsersActivity.AnonymousClass9.lambda$didSelectUsers$2(processed, arrayList2, user, count, showUserRestrictedPrivacyAlert, tL_error);
                    }
                });
                ChatUsersActivity.this.getMessagesController().putUser(user, false);
                a++;
                userRestrictedPrivacy = userRestrictedPrivacy2;
            }
        }

        public static /* synthetic */ void lambda$didSelectUsers$0(ArrayList userRestrictedPrivacy, int count, Context context) {
            CharSequence description;
            CharSequence title;
            if (userRestrictedPrivacy.size() == 1) {
                if (count > 1) {
                    title = LocaleController.getString("InviteToGroupErrorTitleAUser", R.string.InviteToGroupErrorTitleAUser);
                } else {
                    title = LocaleController.getString("InviteToGroupErrorTitleThisUser", R.string.InviteToGroupErrorTitleThisUser);
                }
                description = AndroidUtilities.replaceTags(LocaleController.formatString("InviteToGroupErrorMessageSingle", R.string.InviteToGroupErrorMessageSingle, UserObject.getFirstName((TLRPC.User) userRestrictedPrivacy.get(0))));
            } else if (userRestrictedPrivacy.size() == 2) {
                title = LocaleController.getString("InviteToGroupErrorTitleSomeUsers", R.string.InviteToGroupErrorTitleSomeUsers);
                description = AndroidUtilities.replaceTags(LocaleController.formatString("InviteToGroupErrorMessageDouble", R.string.InviteToGroupErrorMessageDouble, UserObject.getFirstName((TLRPC.User) userRestrictedPrivacy.get(0)), UserObject.getFirstName((TLRPC.User) userRestrictedPrivacy.get(1))));
            } else if (userRestrictedPrivacy.size() == count) {
                title = LocaleController.getString("InviteToGroupErrorTitleTheseUsers", R.string.InviteToGroupErrorTitleTheseUsers);
                description = LocaleController.getString("InviteToGroupErrorMessageMultipleAll", R.string.InviteToGroupErrorMessageMultipleAll);
            } else {
                title = LocaleController.getString("InviteToGroupErrorTitleSomeUsers", R.string.InviteToGroupErrorTitleSomeUsers);
                description = LocaleController.getString("InviteToGroupErrorMessageMultipleSome", R.string.InviteToGroupErrorMessageMultipleSome);
            }
            new AlertDialog.Builder(context).setTitle(title).setMessage(description).setPositiveButton(LocaleController.getString("OK", R.string.OK), null).show();
        }

        /* renamed from: lambda$didSelectUsers$1$org-telegram-ui-ChatUsersActivity$9 */
        public /* synthetic */ void m2167lambda$didSelectUsers$1$orgtelegramuiChatUsersActivity$9(int[] processed, int count, ArrayList userRestrictedPrivacy, Runnable showUserRestrictedPrivacyAlert, TLRPC.User user) {
            processed[0] = processed[0] + 1;
            if (processed[0] >= count && userRestrictedPrivacy.size() > 0) {
                showUserRestrictedPrivacyAlert.run();
            }
            DiffCallback savedState = ChatUsersActivity.this.saveState();
            ArrayList<TLObject> array = (ChatUsersActivity.this.contactsMap == null || ChatUsersActivity.this.contactsMap.size() == 0) ? ChatUsersActivity.this.participants : ChatUsersActivity.this.contacts;
            LongSparseArray<TLObject> map = (ChatUsersActivity.this.contactsMap == null || ChatUsersActivity.this.contactsMap.size() == 0) ? ChatUsersActivity.this.participantsMap : ChatUsersActivity.this.contactsMap;
            if (map.get(user.id) == null) {
                if (ChatObject.isChannel(ChatUsersActivity.this.currentChat)) {
                    TLRPC.TL_channelParticipant channelParticipant1 = new TLRPC.TL_channelParticipant();
                    channelParticipant1.inviter_id = ChatUsersActivity.this.getUserConfig().getClientUserId();
                    channelParticipant1.peer = new TLRPC.TL_peerUser();
                    channelParticipant1.peer.user_id = user.id;
                    channelParticipant1.date = ChatUsersActivity.this.getConnectionsManager().getCurrentTime();
                    array.add(0, channelParticipant1);
                    map.put(user.id, channelParticipant1);
                } else {
                    TLRPC.ChatParticipant participant = new TLRPC.TL_chatParticipant();
                    participant.user_id = user.id;
                    participant.inviter_id = ChatUsersActivity.this.getUserConfig().getClientUserId();
                    array.add(0, participant);
                    map.put(user.id, participant);
                }
            }
            if (array == ChatUsersActivity.this.participants) {
                ChatUsersActivity chatUsersActivity = ChatUsersActivity.this;
                chatUsersActivity.sortAdmins(chatUsersActivity.participants);
            }
            ChatUsersActivity.this.updateListAnimated(savedState);
        }

        public static /* synthetic */ boolean lambda$didSelectUsers$2(int[] processed, ArrayList userRestrictedPrivacy, TLRPC.User user, int count, Runnable showUserRestrictedPrivacyAlert, TLRPC.TL_error err) {
            processed[0] = processed[0] + 1;
            boolean z = err != null && "USER_PRIVACY_RESTRICTED".equals(err.text);
            boolean privacyRestricted = z;
            if (z) {
                userRestrictedPrivacy.add(user);
            }
            if (processed[0] >= count && userRestrictedPrivacy.size() > 0) {
                showUserRestrictedPrivacyAlert.run();
            }
            return !privacyRestricted;
        }

        @Override // org.telegram.ui.GroupCreateActivity.ContactsAddActivityDelegate
        public void needAddBot(TLRPC.User user) {
            ChatUsersActivity.this.openRightsEdit(user.id, null, null, null, "", true, 0, false);
        }
    }

    /* renamed from: org.telegram.ui.ChatUsersActivity$10 */
    /* loaded from: classes4.dex */
    public class AnonymousClass10 extends GigagroupConvertAlert {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass10(Context context, BaseFragment parentFragment) {
            super(context, parentFragment);
            ChatUsersActivity.this = this$0;
        }

        @Override // org.telegram.ui.Components.GigagroupConvertAlert
        protected void onCovert() {
            ChatUsersActivity.this.getMessagesController().convertToGigaGroup(ChatUsersActivity.this.getParentActivity(), ChatUsersActivity.this.currentChat, ChatUsersActivity.this, new MessagesStorage.BooleanCallback() { // from class: org.telegram.ui.ChatUsersActivity$10$$ExternalSyntheticLambda0
                @Override // org.telegram.messenger.MessagesStorage.BooleanCallback
                public final void run(boolean z) {
                    ChatUsersActivity.AnonymousClass10.this.m2165lambda$onCovert$0$orgtelegramuiChatUsersActivity$10(z);
                }
            });
        }

        /* renamed from: lambda$onCovert$0$org-telegram-ui-ChatUsersActivity$10 */
        public /* synthetic */ void m2165lambda$onCovert$0$orgtelegramuiChatUsersActivity$10(boolean result) {
            if (result && ChatUsersActivity.this.parentLayout != null) {
                BaseFragment editActivity = ChatUsersActivity.this.parentLayout.fragmentsStack.get(ChatUsersActivity.this.parentLayout.fragmentsStack.size() - 2);
                if (editActivity instanceof ChatEditActivity) {
                    editActivity.removeSelfFromStack();
                    Bundle args = new Bundle();
                    args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, ChatUsersActivity.this.chatId);
                    ChatEditActivity fragment = new ChatEditActivity(args);
                    fragment.setInfo(ChatUsersActivity.this.info);
                    ChatUsersActivity.this.parentLayout.addFragmentToStack(fragment, ChatUsersActivity.this.parentLayout.fragmentsStack.size() - 1);
                    ChatUsersActivity.this.finishFragment();
                    fragment.showConvertTooltip();
                    return;
                }
                ChatUsersActivity.this.finishFragment();
            }
        }

        @Override // org.telegram.ui.Components.GigagroupConvertAlert
        protected void onCancel() {
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-ChatUsersActivity */
    public /* synthetic */ void m2155lambda$createView$0$orgtelegramuiChatUsersActivity(TLRPC.User user, TLObject participant, TLRPC.TL_chatAdminRights ar, TLRPC.TL_chatBannedRights br, String rankFinal, boolean canEdit, DialogInterface dialog, int which) {
        openRightsEdit(user.id, participant, ar, br, rankFinal, canEdit, this.selectType == 1 ? 0 : 1, false);
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-ChatUsersActivity */
    public /* synthetic */ boolean m2157lambda$createView$2$orgtelegramuiChatUsersActivity(View view, int position) {
        if (getParentActivity() != null) {
            RecyclerView.Adapter adapter = this.listView.getAdapter();
            ListAdapter listAdapter = this.listViewAdapter;
            return adapter == listAdapter && createMenuForParticipant(listAdapter.getItem(position), false);
        }
        return false;
    }

    public void sortAdmins(ArrayList<TLObject> participants) {
        Collections.sort(participants, new Comparator() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda2
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return ChatUsersActivity.this.m2163lambda$sortAdmins$3$orgtelegramuiChatUsersActivity((TLObject) obj, (TLObject) obj2);
            }
        });
    }

    /* renamed from: lambda$sortAdmins$3$org-telegram-ui-ChatUsersActivity */
    public /* synthetic */ int m2163lambda$sortAdmins$3$orgtelegramuiChatUsersActivity(TLObject lhs, TLObject rhs) {
        int type1 = getChannelAdminParticipantType(lhs);
        int type2 = getChannelAdminParticipantType(rhs);
        if (type1 > type2) {
            return 1;
        }
        if (type1 < type2) {
            return -1;
        }
        if ((lhs instanceof TLRPC.ChannelParticipant) && (rhs instanceof TLRPC.ChannelParticipant)) {
            return (int) (MessageObject.getPeerId(((TLRPC.ChannelParticipant) lhs).peer) - MessageObject.getPeerId(((TLRPC.ChannelParticipant) rhs).peer));
        }
        return 0;
    }

    public void showItemsAnimated(int from) {
        if (this.isPaused || !this.openTransitionStarted) {
            return;
        }
        if (this.listView.getAdapter() == this.listViewAdapter && this.firstLoaded) {
            return;
        }
        View progressView = null;
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            View child = this.listView.getChildAt(i);
            if (child instanceof FlickerLoadingView) {
                progressView = child;
            }
        }
        final View finalProgressView = progressView;
        if (progressView != null) {
            this.listView.removeView(progressView);
            from--;
        }
        final int finalFrom = from;
        this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.ChatUsersActivity.13
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                ChatUsersActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                int n = ChatUsersActivity.this.listView.getChildCount();
                AnimatorSet animatorSet = new AnimatorSet();
                for (int i2 = 0; i2 < n; i2++) {
                    View child2 = ChatUsersActivity.this.listView.getChildAt(i2);
                    if (child2 != finalProgressView && ChatUsersActivity.this.listView.getChildAdapterPosition(child2) >= finalFrom) {
                        child2.setAlpha(0.0f);
                        int s = Math.min(ChatUsersActivity.this.listView.getMeasuredHeight(), Math.max(0, child2.getTop()));
                        int delay = (int) ((s / ChatUsersActivity.this.listView.getMeasuredHeight()) * 100.0f);
                        ObjectAnimator a = ObjectAnimator.ofFloat(child2, View.ALPHA, 0.0f, 1.0f);
                        a.setStartDelay(delay);
                        a.setDuration(200L);
                        animatorSet.playTogether(a);
                    }
                }
                View view = finalProgressView;
                if (view != null && view.getParent() == null) {
                    ChatUsersActivity.this.listView.addView(finalProgressView);
                    final RecyclerView.LayoutManager layoutManager = ChatUsersActivity.this.listView.getLayoutManager();
                    if (layoutManager != null) {
                        layoutManager.ignoreView(finalProgressView);
                        Animator animator = ObjectAnimator.ofFloat(finalProgressView, View.ALPHA, finalProgressView.getAlpha(), 0.0f);
                        animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChatUsersActivity.13.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                finalProgressView.setAlpha(1.0f);
                                layoutManager.stopIgnoringView(finalProgressView);
                                ChatUsersActivity.this.listView.removeView(finalProgressView);
                            }
                        });
                        animator.start();
                    }
                }
                animatorSet.start();
                return true;
            }
        });
    }

    public void setIgnoresUsers(LongSparseArray<TLRPC.TL_groupCallParticipant> participants) {
        this.ignoredUsers = participants;
    }

    public void onOwnerChaged(TLRPC.User user) {
        TLRPC.User user2;
        ArrayList<TLObject> arrayList;
        LongSparseArray<TLObject> map;
        int a;
        boolean foundAny;
        TLRPC.User user3 = user;
        this.undoView.showWithAction(-this.chatId, this.isChannel ? 9 : 10, user3);
        boolean foundAny2 = false;
        this.currentChat.creator = false;
        int a2 = 0;
        while (a2 < 3) {
            boolean found = false;
            if (a2 == 0) {
                map = this.contactsMap;
                arrayList = this.contacts;
            } else if (a2 == 1) {
                map = this.botsMap;
                arrayList = this.bots;
            } else {
                map = this.participantsMap;
                arrayList = this.participants;
            }
            TLObject object = map.get(user3.id);
            if (object instanceof TLRPC.ChannelParticipant) {
                TLRPC.TL_channelParticipantCreator creator = new TLRPC.TL_channelParticipantCreator();
                creator.peer = new TLRPC.TL_peerUser();
                creator.peer.user_id = user3.id;
                map.put(user3.id, creator);
                int index = arrayList.indexOf(object);
                if (index >= 0) {
                    arrayList.set(index, creator);
                }
                found = true;
                foundAny2 = true;
            }
            long selfUserId = getUserConfig().getClientUserId();
            TLObject object2 = map.get(selfUserId);
            if (!(object2 instanceof TLRPC.ChannelParticipant)) {
                foundAny = foundAny2;
                a = a2;
            } else {
                TLRPC.TL_channelParticipantAdmin admin = new TLRPC.TL_channelParticipantAdmin();
                admin.peer = new TLRPC.TL_peerUser();
                admin.peer.user_id = selfUserId;
                admin.self = true;
                admin.inviter_id = selfUserId;
                admin.promoted_by = selfUserId;
                admin.date = (int) (System.currentTimeMillis() / 1000);
                admin.admin_rights = new TLRPC.TL_chatAdminRights();
                TLRPC.TL_chatAdminRights tL_chatAdminRights = admin.admin_rights;
                TLRPC.TL_chatAdminRights tL_chatAdminRights2 = admin.admin_rights;
                TLRPC.TL_chatAdminRights tL_chatAdminRights3 = admin.admin_rights;
                TLRPC.TL_chatAdminRights tL_chatAdminRights4 = admin.admin_rights;
                TLRPC.TL_chatAdminRights tL_chatAdminRights5 = admin.admin_rights;
                foundAny = foundAny2;
                TLRPC.TL_chatAdminRights tL_chatAdminRights6 = admin.admin_rights;
                TLRPC.TL_chatAdminRights tL_chatAdminRights7 = admin.admin_rights;
                a = a2;
                admin.admin_rights.add_admins = true;
                tL_chatAdminRights7.pin_messages = true;
                tL_chatAdminRights6.invite_users = true;
                tL_chatAdminRights5.ban_users = true;
                tL_chatAdminRights4.delete_messages = true;
                tL_chatAdminRights3.edit_messages = true;
                tL_chatAdminRights2.post_messages = true;
                tL_chatAdminRights.change_info = true;
                if (!this.isChannel) {
                    admin.admin_rights.manage_call = true;
                }
                map.put(selfUserId, admin);
                int index2 = arrayList.indexOf(object2);
                if (index2 >= 0) {
                    arrayList.set(index2, admin);
                }
                found = true;
            }
            if (found) {
                Collections.sort(arrayList, new Comparator() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda1
                    @Override // java.util.Comparator
                    public final int compare(Object obj, Object obj2) {
                        return ChatUsersActivity.this.m2161lambda$onOwnerChaged$4$orgtelegramuiChatUsersActivity((TLObject) obj, (TLObject) obj2);
                    }
                });
            }
            a2 = a + 1;
            user3 = user;
            foundAny2 = foundAny;
        }
        if (foundAny2) {
            user2 = user;
        } else {
            TLRPC.TL_channelParticipantCreator creator2 = new TLRPC.TL_channelParticipantCreator();
            creator2.peer = new TLRPC.TL_peerUser();
            user2 = user;
            creator2.peer.user_id = user2.id;
            this.participantsMap.put(user2.id, creator2);
            this.participants.add(creator2);
            sortAdmins(this.participants);
            updateRows();
        }
        this.listViewAdapter.notifyDataSetChanged();
        ChatUsersActivityDelegate chatUsersActivityDelegate = this.delegate;
        if (chatUsersActivityDelegate != null) {
            chatUsersActivityDelegate.didChangeOwner(user2);
        }
    }

    /* renamed from: lambda$onOwnerChaged$4$org-telegram-ui-ChatUsersActivity */
    public /* synthetic */ int m2161lambda$onOwnerChaged$4$orgtelegramuiChatUsersActivity(TLObject lhs, TLObject rhs) {
        int type1 = getChannelAdminParticipantType(lhs);
        int type2 = getChannelAdminParticipantType(rhs);
        if (type1 > type2) {
            return 1;
        }
        if (type1 < type2) {
            return -1;
        }
        return 0;
    }

    private void openRightsEdit2(final long peerId, final int date, TLObject participant, TLRPC.TL_chatAdminRights adminRights, TLRPC.TL_chatBannedRights bannedRights, String rank, boolean canEditAdmin, final int type, boolean removeFragment) {
        final boolean[] needShowBulletin = new boolean[1];
        final boolean isAdmin = (participant instanceof TLRPC.TL_channelParticipantAdmin) || (participant instanceof TLRPC.TL_chatParticipantAdmin);
        ChatRightsEditActivity fragment = new ChatRightsEditActivity(peerId, this.chatId, adminRights, this.defaultBannedRights, bannedRights, rank, type, true, false, null) { // from class: org.telegram.ui.ChatUsersActivity.14
            @Override // org.telegram.ui.ActionBar.BaseFragment
            public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
                if (!isOpen && backward && needShowBulletin[0] && BulletinFactory.canShowBulletin(ChatUsersActivity.this)) {
                    if (peerId > 0) {
                        TLRPC.User user = getMessagesController().getUser(Long.valueOf(peerId));
                        if (user != null) {
                            BulletinFactory.createPromoteToAdminBulletin(ChatUsersActivity.this, user.first_name).show();
                            return;
                        }
                        return;
                    }
                    TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(-peerId));
                    if (chat != null) {
                        BulletinFactory.createPromoteToAdminBulletin(ChatUsersActivity.this, chat.title).show();
                    }
                }
            }
        };
        fragment.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() { // from class: org.telegram.ui.ChatUsersActivity.15
            @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
            public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank2) {
                TLRPC.ChatParticipant newParticipant;
                TLRPC.ChannelParticipant newPart;
                int i = type;
                if (i == 0) {
                    int a = 0;
                    while (true) {
                        if (a >= ChatUsersActivity.this.participants.size()) {
                            break;
                        }
                        TLObject p = (TLObject) ChatUsersActivity.this.participants.get(a);
                        if (p instanceof TLRPC.ChannelParticipant) {
                            TLRPC.ChannelParticipant p2 = (TLRPC.ChannelParticipant) p;
                            if (MessageObject.getPeerId(p2.peer) == peerId) {
                                if (rights == 1) {
                                    newPart = new TLRPC.TL_channelParticipantAdmin();
                                } else {
                                    newPart = new TLRPC.TL_channelParticipant();
                                }
                                newPart.admin_rights = rightsAdmin;
                                newPart.banned_rights = rightsBanned;
                                newPart.inviter_id = ChatUsersActivity.this.getUserConfig().getClientUserId();
                                if (peerId > 0) {
                                    newPart.peer = new TLRPC.TL_peerUser();
                                    newPart.peer.user_id = peerId;
                                } else {
                                    newPart.peer = new TLRPC.TL_peerChannel();
                                    newPart.peer.channel_id = -peerId;
                                }
                                newPart.date = date;
                                newPart.flags |= 4;
                                newPart.rank = rank2;
                                ChatUsersActivity.this.participants.set(a, newPart);
                            }
                        } else if (p instanceof TLRPC.ChatParticipant) {
                            TLRPC.ChatParticipant chatParticipant = (TLRPC.ChatParticipant) p;
                            if (rights == 1) {
                                newParticipant = new TLRPC.TL_chatParticipantAdmin();
                            } else {
                                newParticipant = new TLRPC.TL_chatParticipant();
                            }
                            newParticipant.user_id = chatParticipant.user_id;
                            newParticipant.date = chatParticipant.date;
                            newParticipant.inviter_id = chatParticipant.inviter_id;
                            int index = ChatUsersActivity.this.info.participants.participants.indexOf(chatParticipant);
                            if (index >= 0) {
                                ChatUsersActivity.this.info.participants.participants.set(index, newParticipant);
                            }
                            ChatUsersActivity.this.loadChatParticipants(0, 200);
                        }
                        a++;
                    }
                    if (rights == 1 && !isAdmin) {
                        needShowBulletin[0] = true;
                    }
                } else if (i == 1 && rights == 0) {
                    ChatUsersActivity.this.removeParticipants(peerId);
                }
            }

            @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
            public void didChangeOwner(TLRPC.User user) {
                ChatUsersActivity.this.onOwnerChaged(user);
            }
        });
        presentFragment(fragment);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean canBeginSlide() {
        return checkDiscard();
    }

    public void openRightsEdit(final long user_id, final TLObject participant, TLRPC.TL_chatAdminRights adminRights, TLRPC.TL_chatBannedRights bannedRights, String rank, boolean canEditAdmin, int type, final boolean removeFragment) {
        ChatRightsEditActivity fragment = new ChatRightsEditActivity(user_id, this.chatId, adminRights, this.defaultBannedRights, bannedRights, rank, type, canEditAdmin, participant == null, null);
        fragment.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() { // from class: org.telegram.ui.ChatUsersActivity.16
            @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
            public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank2) {
                TLObject tLObject = participant;
                if (tLObject instanceof TLRPC.ChannelParticipant) {
                    TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) tLObject;
                    channelParticipant.admin_rights = rightsAdmin;
                    channelParticipant.banned_rights = rightsBanned;
                    channelParticipant.rank = rank2;
                }
                if (ChatUsersActivity.this.delegate == null || rights != 1) {
                    if (ChatUsersActivity.this.delegate != null) {
                        ChatUsersActivity.this.delegate.didAddParticipantToList(user_id, participant);
                    }
                } else {
                    ChatUsersActivity.this.delegate.didSelectUser(user_id);
                }
                if (removeFragment) {
                    ChatUsersActivity.this.removeSelfFromStack();
                }
            }

            @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
            public void didChangeOwner(TLRPC.User user) {
                ChatUsersActivity.this.onOwnerChaged(user);
            }
        });
        presentFragment(fragment, removeFragment);
    }

    private void removeParticipant(long userId) {
        if (!ChatObject.isChannel(this.currentChat)) {
            return;
        }
        TLRPC.User user = getMessagesController().getUser(Long.valueOf(userId));
        getMessagesController().deleteParticipantFromChat(this.chatId, user, null);
        ChatUsersActivityDelegate chatUsersActivityDelegate = this.delegate;
        if (chatUsersActivityDelegate != null) {
            chatUsersActivityDelegate.didKickParticipant(userId);
        }
        finishFragment();
    }

    private TLObject getAnyParticipant(long userId) {
        LongSparseArray<TLObject> map;
        for (int a = 0; a < 3; a++) {
            if (a == 0) {
                map = this.contactsMap;
            } else if (a == 1) {
                map = this.botsMap;
            } else {
                map = this.participantsMap;
            }
            TLObject p = map.get(userId);
            if (p != null) {
                return p;
            }
        }
        return null;
    }

    private void removeParticipants(TLObject object) {
        if (object instanceof TLRPC.ChatParticipant) {
            TLRPC.ChatParticipant chatParticipant = (TLRPC.ChatParticipant) object;
            removeParticipants(chatParticipant.user_id);
        } else if (object instanceof TLRPC.ChannelParticipant) {
            TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) object;
            removeParticipants(MessageObject.getPeerId(channelParticipant.peer));
        }
    }

    public void removeParticipants(long peerId) {
        ArrayList<TLObject> arrayList;
        LongSparseArray<TLObject> map;
        TLRPC.ChatFull chatFull;
        boolean updated = false;
        DiffCallback savedState = saveState();
        for (int a = 0; a < 3; a++) {
            if (a == 0) {
                map = this.contactsMap;
                arrayList = this.contacts;
            } else if (a == 1) {
                map = this.botsMap;
                arrayList = this.bots;
            } else {
                map = this.participantsMap;
                arrayList = this.participants;
            }
            TLObject p = map.get(peerId);
            if (p != null) {
                map.remove(peerId);
                arrayList.remove(p);
                updated = true;
                if (this.type == 0 && (chatFull = this.info) != null) {
                    chatFull.kicked_count--;
                }
            }
        }
        if (updated) {
            updateListAnimated(savedState);
        }
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        SearchAdapter searchAdapter = this.searchListViewAdapter;
        if (adapter == searchAdapter) {
            searchAdapter.removeUserId(peerId);
        }
    }

    public void updateParticipantWithRights(TLRPC.ChannelParticipant channelParticipant, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, long user_id, boolean withDelegate) {
        LongSparseArray<TLObject> map;
        ChatUsersActivityDelegate chatUsersActivityDelegate;
        boolean delegateCalled = false;
        for (int a = 0; a < 3; a++) {
            if (a == 0) {
                map = this.contactsMap;
            } else if (a == 1) {
                map = this.botsMap;
            } else {
                map = this.participantsMap;
            }
            TLObject p = map.get(MessageObject.getPeerId(channelParticipant.peer));
            if (p instanceof TLRPC.ChannelParticipant) {
                channelParticipant = (TLRPC.ChannelParticipant) p;
                channelParticipant.admin_rights = rightsAdmin;
                channelParticipant.banned_rights = rightsBanned;
                if (withDelegate) {
                    channelParticipant.promoted_by = getUserConfig().getClientUserId();
                }
            }
            if (withDelegate && p != null && !delegateCalled && (chatUsersActivityDelegate = this.delegate) != null) {
                delegateCalled = true;
                chatUsersActivityDelegate.didAddParticipantToList(user_id, p);
            }
        }
    }

    public boolean createMenuForParticipant(final TLObject participant, boolean resultOnly) {
        int date;
        TLRPC.TL_chatAdminRights adminRights;
        TLRPC.TL_chatBannedRights bannedRights;
        boolean canEdit;
        long peerId;
        String rank;
        int[] icons;
        CharSequence[] items;
        String str;
        String str2;
        int i;
        boolean allowSetAdmin;
        ArrayList<String> items2;
        ArrayList<Integer> actions;
        ArrayList<Integer> icons2;
        boolean hasRemove;
        if (participant != null && this.selectType == 0) {
            if (participant instanceof TLRPC.ChannelParticipant) {
                TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) participant;
                long peerId2 = MessageObject.getPeerId(channelParticipant.peer);
                boolean canEdit2 = channelParticipant.can_edit;
                TLRPC.TL_chatBannedRights bannedRights2 = channelParticipant.banned_rights;
                TLRPC.TL_chatAdminRights adminRights2 = channelParticipant.admin_rights;
                int date2 = channelParticipant.date;
                rank = channelParticipant.rank;
                peerId = peerId2;
                canEdit = canEdit2;
                bannedRights = bannedRights2;
                adminRights = adminRights2;
                date = date2;
            } else if (participant instanceof TLRPC.ChatParticipant) {
                TLRPC.ChatParticipant chatParticipant = (TLRPC.ChatParticipant) participant;
                long peerId3 = chatParticipant.user_id;
                int date3 = chatParticipant.date;
                rank = "";
                peerId = peerId3;
                canEdit = ChatObject.canAddAdmins(this.currentChat);
                bannedRights = null;
                adminRights = null;
                date = date3;
            } else {
                rank = null;
                peerId = 0;
                canEdit = false;
                bannedRights = null;
                adminRights = null;
                date = 0;
            }
            if (peerId != 0 && peerId != getUserConfig().getClientUserId()) {
                int i2 = this.type;
                if (i2 == 2) {
                    final TLRPC.User user = getMessagesController().getUser(Long.valueOf(peerId));
                    boolean allowSetAdmin2 = ChatObject.canAddAdmins(this.currentChat) && ((participant instanceof TLRPC.TL_channelParticipant) || (participant instanceof TLRPC.TL_channelParticipantBanned) || (participant instanceof TLRPC.TL_chatParticipant) || canEdit);
                    final boolean canEditAdmin = (!(participant instanceof TLRPC.TL_channelParticipantAdmin) && !(participant instanceof TLRPC.TL_channelParticipantCreator) && !(participant instanceof TLRPC.TL_chatParticipantCreator) && !(participant instanceof TLRPC.TL_chatParticipantAdmin)) || canEdit;
                    boolean editingAdmin = (participant instanceof TLRPC.TL_channelParticipantAdmin) || (participant instanceof TLRPC.TL_chatParticipantAdmin);
                    if (this.selectType != 0) {
                        allowSetAdmin = allowSetAdmin2;
                    } else {
                        allowSetAdmin = allowSetAdmin2 & (!UserObject.isDeleted(user));
                    }
                    if (!resultOnly) {
                        items2 = new ArrayList<>();
                        actions = new ArrayList<>();
                        icons2 = new ArrayList<>();
                    } else {
                        items2 = null;
                        actions = null;
                        icons2 = null;
                    }
                    if (allowSetAdmin) {
                        if (resultOnly) {
                            return true;
                        }
                        items2.add(editingAdmin ? LocaleController.getString("EditAdminRights", R.string.EditAdminRights) : LocaleController.getString("SetAsAdmin", R.string.SetAsAdmin));
                        icons2.add(Integer.valueOf((int) R.drawable.msg_admins));
                        actions.add(0);
                    }
                    if (ChatObject.canBlockUsers(this.currentChat) && canEditAdmin) {
                        if (resultOnly) {
                            return true;
                        }
                        if (!this.isChannel) {
                            if (ChatObject.isChannel(this.currentChat) && !this.currentChat.gigagroup) {
                                items2.add(LocaleController.getString("ChangePermissions", R.string.ChangePermissions));
                                icons2.add(Integer.valueOf((int) R.drawable.msg_permissions));
                                actions.add(1);
                            }
                            items2.add(LocaleController.getString("KickFromGroup", R.string.KickFromGroup));
                        } else {
                            items2.add(LocaleController.getString("ChannelRemoveUser", R.string.ChannelRemoveUser));
                        }
                        icons2.add(Integer.valueOf((int) R.drawable.msg_remove));
                        actions.add(2);
                        hasRemove = true;
                    } else {
                        hasRemove = false;
                    }
                    if (actions != null && !actions.isEmpty()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                        int[] intArray = AndroidUtilities.toIntArray(icons2);
                        final ArrayList<Integer> arrayList = actions;
                        final long j = peerId;
                        final int i3 = date;
                        final TLRPC.TL_chatAdminRights tL_chatAdminRights = adminRights;
                        ArrayList<String> items3 = items2;
                        final TLRPC.TL_chatBannedRights tL_chatBannedRights = bannedRights;
                        final String str3 = rank;
                        builder.setItems((CharSequence[]) items2.toArray(new CharSequence[actions.size()]), intArray, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda12
                            @Override // android.content.DialogInterface.OnClickListener
                            public final void onClick(DialogInterface dialogInterface, int i4) {
                                ChatUsersActivity.this.m2151x442d0779(arrayList, user, j, canEditAdmin, participant, i3, tL_chatAdminRights, tL_chatBannedRights, str3, dialogInterface, i4);
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        showDialog(alertDialog);
                        if (hasRemove) {
                            alertDialog.setItemColor(items3.size() - 1, Theme.getColor(Theme.key_dialogTextRed2), Theme.getColor(Theme.key_dialogRedIcon));
                        }
                        return true;
                    }
                    return false;
                }
                final long peerId4 = peerId;
                if (i2 == 3 && ChatObject.canBlockUsers(this.currentChat)) {
                    if (resultOnly) {
                        return true;
                    }
                    items = new CharSequence[]{LocaleController.getString("ChannelEditPermissions", R.string.ChannelEditPermissions), LocaleController.getString("ChannelDeleteFromList", R.string.ChannelDeleteFromList)};
                    icons = new int[]{R.drawable.msg_permissions, R.drawable.msg_delete};
                } else if (this.type == 0 && ChatObject.canBlockUsers(this.currentChat)) {
                    if (resultOnly) {
                        return true;
                    }
                    CharSequence[] items4 = new CharSequence[2];
                    if (!ChatObject.canAddUsers(this.currentChat) || peerId4 <= 0) {
                        str = null;
                    } else {
                        if (this.isChannel) {
                            i = R.string.ChannelAddToChannel;
                            str2 = "ChannelAddToChannel";
                        } else {
                            i = R.string.ChannelAddToGroup;
                            str2 = "ChannelAddToGroup";
                        }
                        str = LocaleController.getString(str2, i);
                    }
                    items4[0] = str;
                    items4[1] = LocaleController.getString("ChannelDeleteFromList", R.string.ChannelDeleteFromList);
                    items = items4;
                    icons = new int[]{R.drawable.msg_contact_add, R.drawable.msg_delete};
                } else if (this.type == 1 && ChatObject.canAddAdmins(this.currentChat) && canEdit) {
                    if (resultOnly) {
                        return true;
                    }
                    if (!this.currentChat.creator && ((participant instanceof TLRPC.TL_channelParticipantCreator) || !canEdit)) {
                        items = new CharSequence[]{LocaleController.getString("ChannelRemoveUserAdmin", R.string.ChannelRemoveUserAdmin)};
                        icons = new int[]{R.drawable.msg_remove};
                    }
                    items = new CharSequence[]{LocaleController.getString("EditAdminRights", R.string.EditAdminRights), LocaleController.getString("ChannelRemoveUserAdmin", R.string.ChannelRemoveUserAdmin)};
                    icons = new int[]{R.drawable.msg_admins, R.drawable.msg_remove};
                } else {
                    items = null;
                    icons = null;
                }
                if (items == null) {
                    return false;
                }
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
                final CharSequence[] charSequenceArr = items;
                final TLRPC.TL_chatAdminRights tL_chatAdminRights2 = adminRights;
                final String str4 = rank;
                final TLRPC.TL_chatBannedRights tL_chatBannedRights2 = bannedRights;
                builder2.setItems(items, icons, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda14
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i4) {
                        ChatUsersActivity.this.m2154x78b746bc(charSequenceArr, peerId4, tL_chatAdminRights2, str4, participant, tL_chatBannedRights2, dialogInterface, i4);
                    }
                });
                AlertDialog alertDialog2 = builder2.create();
                showDialog(alertDialog2);
                if (this.type != 1) {
                    return true;
                }
                alertDialog2.setItemColor(items.length - 1, Theme.getColor(Theme.key_dialogTextRed2), Theme.getColor(Theme.key_dialogRedIcon));
                return true;
            }
            return false;
        }
        return false;
    }

    /* renamed from: lambda$createMenuForParticipant$6$org-telegram-ui-ChatUsersActivity */
    public /* synthetic */ void m2151x442d0779(final ArrayList actions, TLRPC.User user, final long peerId, final boolean canEditAdmin, final TLObject participant, final int date, final TLRPC.TL_chatAdminRights adminRights, final TLRPC.TL_chatBannedRights bannedRights, final String rank, DialogInterface dialogInterface, final int i) {
        if (((Integer) actions.get(i)).intValue() == 2) {
            getMessagesController().deleteParticipantFromChat(this.chatId, user, null);
            removeParticipants(peerId);
            if (this.currentChat != null && user != null && BulletinFactory.canShowBulletin(this)) {
                BulletinFactory.createRemoveFromChatBulletin(this, user, this.currentChat.title).show();
                return;
            }
            return;
        }
        if (((Integer) actions.get(i)).intValue() == 1 && canEditAdmin) {
            if ((participant instanceof TLRPC.TL_channelParticipantAdmin) || (participant instanceof TLRPC.TL_chatParticipantAdmin)) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
                builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder2.setMessage(LocaleController.formatString("AdminWillBeRemoved", R.string.AdminWillBeRemoved, UserObject.getUserName(user)));
                builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda11
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface2, int i2) {
                        ChatUsersActivity.this.m2150xdd5447b8(peerId, date, participant, adminRights, bannedRights, rank, canEditAdmin, actions, i, dialogInterface2, i2);
                    }
                });
                builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder2.create());
                return;
            }
        }
        openRightsEdit2(peerId, date, participant, adminRights, bannedRights, rank, canEditAdmin, ((Integer) actions.get(i)).intValue(), false);
    }

    /* renamed from: lambda$createMenuForParticipant$5$org-telegram-ui-ChatUsersActivity */
    public /* synthetic */ void m2150xdd5447b8(long peerId, int date, TLObject participant, TLRPC.TL_chatAdminRights adminRights, TLRPC.TL_chatBannedRights bannedRights, String rank, boolean canEditAdmin, ArrayList actions, int i, DialogInterface dialog, int which) {
        openRightsEdit2(peerId, date, participant, adminRights, bannedRights, rank, canEditAdmin, ((Integer) actions.get(i)).intValue(), false);
    }

    /* renamed from: lambda$createMenuForParticipant$9$org-telegram-ui-ChatUsersActivity */
    public /* synthetic */ void m2154x78b746bc(CharSequence[] items, long peerId, TLRPC.TL_chatAdminRights adminRights, String rank, final TLObject participant, TLRPC.TL_chatBannedRights bannedRights, DialogInterface dialogInterface, int i) {
        int i2;
        int i3;
        final TLObject tLObject;
        TLRPC.Chat chat;
        TLRPC.User user;
        int i4 = this.type;
        if (i4 == 1) {
            if (i == 0 && items.length == 2) {
                ChatRightsEditActivity fragment = new ChatRightsEditActivity(peerId, this.chatId, adminRights, null, null, rank, 0, true, false, null);
                fragment.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() { // from class: org.telegram.ui.ChatUsersActivity.17
                    @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
                    public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank2) {
                        TLObject tLObject2 = participant;
                        if (tLObject2 instanceof TLRPC.ChannelParticipant) {
                            TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) tLObject2;
                            channelParticipant.admin_rights = rightsAdmin;
                            channelParticipant.banned_rights = rightsBanned;
                            channelParticipant.rank = rank2;
                            ChatUsersActivity.this.updateParticipantWithRights(channelParticipant, rightsAdmin, rightsBanned, 0L, false);
                        }
                    }

                    @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
                    public void didChangeOwner(TLRPC.User user2) {
                        ChatUsersActivity.this.onOwnerChaged(user2);
                    }
                });
                presentFragment(fragment);
                return;
            }
            getMessagesController().setUserAdminRole(this.chatId, getMessagesController().getUser(Long.valueOf(peerId)), new TLRPC.TL_chatAdminRights(), "", true ^ this.isChannel, this, false, false, null, null);
            removeParticipants(peerId);
            return;
        }
        if (i4 == 0) {
            i2 = i;
        } else if (i4 != 3) {
            if (i == 0) {
                if (peerId > 0) {
                    user = getMessagesController().getUser(Long.valueOf(peerId));
                    chat = null;
                } else {
                    user = null;
                    chat = getMessagesController().getChat(Long.valueOf(-peerId));
                }
                getMessagesController().deleteParticipantFromChat(this.chatId, user, chat, null, false, false);
                return;
            }
            return;
        } else {
            i2 = i;
        }
        if (i2 == 0) {
            if (i4 == 3) {
                tLObject = participant;
                ChatRightsEditActivity fragment2 = new ChatRightsEditActivity(peerId, this.chatId, null, this.defaultBannedRights, bannedRights, rank, 1, true, false, null);
                fragment2.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() { // from class: org.telegram.ui.ChatUsersActivity.18
                    @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
                    public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank2) {
                        TLObject tLObject2 = tLObject;
                        if (tLObject2 instanceof TLRPC.ChannelParticipant) {
                            TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) tLObject2;
                            channelParticipant.admin_rights = rightsAdmin;
                            channelParticipant.banned_rights = rightsBanned;
                            channelParticipant.rank = rank2;
                            ChatUsersActivity.this.updateParticipantWithRights(channelParticipant, rightsAdmin, rightsBanned, 0L, false);
                        }
                    }

                    @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
                    public void didChangeOwner(TLRPC.User user2) {
                        ChatUsersActivity.this.onOwnerChaged(user2);
                    }
                });
                presentFragment(fragment2);
                i3 = 1;
            } else {
                tLObject = participant;
                if (i4 != 0) {
                    i3 = 1;
                } else if (peerId <= 0) {
                    i3 = 1;
                } else {
                    TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(peerId));
                    i3 = 1;
                    getMessagesController().addUserToChat(this.chatId, user2, 0, null, this, null);
                }
            }
        } else {
            tLObject = participant;
            i3 = 1;
            if (i2 == 1) {
                TLRPC.TL_channels_editBanned req = new TLRPC.TL_channels_editBanned();
                req.participant = getMessagesController().getInputPeer(peerId);
                req.channel = getMessagesController().getInputChannel(this.chatId);
                req.banned_rights = new TLRPC.TL_chatBannedRights();
                getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda6
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                        ChatUsersActivity.this.m2153x11de86fb(tLObject2, tL_error);
                    }
                });
            }
        }
        if ((i2 == 0 && this.type == 0) || i2 == i3) {
            removeParticipants(tLObject);
        }
    }

    /* renamed from: lambda$createMenuForParticipant$8$org-telegram-ui-ChatUsersActivity */
    public /* synthetic */ void m2153x11de86fb(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            final TLRPC.Updates updates = (TLRPC.Updates) response;
            getMessagesController().processUpdates(updates, false);
            if (!updates.chats.isEmpty()) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda18
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatUsersActivity.this.m2152xab05c73a(updates);
                    }
                }, 1000L);
            }
        }
    }

    /* renamed from: lambda$createMenuForParticipant$7$org-telegram-ui-ChatUsersActivity */
    public /* synthetic */ void m2152xab05c73a(TLRPC.Updates updates) {
        TLRPC.Chat chat = updates.chats.get(0);
        getMessagesController().loadFullChat(chat.id, 0, true);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoad) {
            boolean hadInfo = false;
            TLRPC.ChatFull chatFull = (TLRPC.ChatFull) args[0];
            boolean byChannelUsers = ((Boolean) args[2]).booleanValue();
            if (chatFull.id != this.chatId) {
                return;
            }
            if (!byChannelUsers || !ChatObject.isChannel(this.currentChat)) {
                if (this.info != null) {
                    hadInfo = true;
                }
                this.info = chatFull;
                if (!hadInfo) {
                    int currentSlowmode = getCurrentSlowmode();
                    this.initialSlowmode = currentSlowmode;
                    this.selectedSlowmode = currentSlowmode;
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda16
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatUsersActivity.this.m2158x636404ba();
                    }
                });
            }
        }
    }

    /* renamed from: lambda$didReceivedNotification$10$org-telegram-ui-ChatUsersActivity */
    public /* synthetic */ void m2158x636404ba() {
        loadChatParticipants(0, 200);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        return checkDiscard();
    }

    public void setDelegate(ChatUsersActivityDelegate chatUsersActivityDelegate) {
        this.delegate = chatUsersActivityDelegate;
    }

    private int getCurrentSlowmode() {
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull != null) {
            if (chatFull.slowmode_seconds == 10) {
                return 1;
            }
            if (this.info.slowmode_seconds == 30) {
                return 2;
            }
            if (this.info.slowmode_seconds == 60) {
                return 3;
            }
            if (this.info.slowmode_seconds == 300) {
                return 4;
            }
            if (this.info.slowmode_seconds == 900) {
                return 5;
            }
            if (this.info.slowmode_seconds == 3600) {
                return 6;
            }
            return 0;
        }
        return 0;
    }

    public int getSecondsForIndex(int index) {
        if (index == 1) {
            return 10;
        }
        if (index == 2) {
            return 30;
        }
        if (index == 3) {
            return 60;
        }
        if (index == 4) {
            return 300;
        }
        if (index == 5) {
            return 900;
        }
        if (index == 6) {
            return 3600;
        }
        return 0;
    }

    public String formatSeconds(int seconds) {
        if (seconds < 60) {
            return LocaleController.formatPluralString("Seconds", seconds, new Object[0]);
        }
        return seconds < 3600 ? LocaleController.formatPluralString("Minutes", seconds / 60, new Object[0]) : LocaleController.formatPluralString("Hours", (seconds / 60) / 60, new Object[0]);
    }

    public boolean checkDiscard() {
        String newBannedRights = ChatObject.getBannedRightsString(this.defaultBannedRights);
        if (!newBannedRights.equals(this.initialBannedRights) || this.initialSlowmode != this.selectedSlowmode) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", R.string.UserRestrictionsApplyChanges));
            if (this.isChannel) {
                builder.setMessage(LocaleController.getString("ChannelSettingsChangedAlert", R.string.ChannelSettingsChangedAlert));
            } else {
                builder.setMessage(LocaleController.getString("GroupSettingsChangedAlert", R.string.GroupSettingsChangedAlert));
            }
            builder.setPositiveButton(LocaleController.getString("ApplyTheme", R.string.ApplyTheme), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChatUsersActivity.this.m2148lambda$checkDiscard$11$orgtelegramuiChatUsersActivity(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda10
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChatUsersActivity.this.m2149lambda$checkDiscard$12$orgtelegramuiChatUsersActivity(dialogInterface, i);
                }
            });
            showDialog(builder.create());
            return false;
        }
        return true;
    }

    /* renamed from: lambda$checkDiscard$11$org-telegram-ui-ChatUsersActivity */
    public /* synthetic */ void m2148lambda$checkDiscard$11$orgtelegramuiChatUsersActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* renamed from: lambda$checkDiscard$12$org-telegram-ui-ChatUsersActivity */
    public /* synthetic */ void m2149lambda$checkDiscard$12$orgtelegramuiChatUsersActivity(DialogInterface dialog, int which) {
        finishFragment();
    }

    public boolean hasSelectType() {
        return this.selectType != 0;
    }

    public String formatUserPermissions(TLRPC.TL_chatBannedRights rights) {
        if (rights == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        if (rights.view_messages && this.defaultBannedRights.view_messages != rights.view_messages) {
            builder.append(LocaleController.getString("UserRestrictionsNoRead", R.string.UserRestrictionsNoRead));
        }
        if (rights.send_messages && this.defaultBannedRights.send_messages != rights.send_messages) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoSend", R.string.UserRestrictionsNoSend));
        }
        if (rights.send_media && this.defaultBannedRights.send_media != rights.send_media) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoSendMedia", R.string.UserRestrictionsNoSendMedia));
        }
        if (rights.send_stickers && this.defaultBannedRights.send_stickers != rights.send_stickers) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoSendStickers", R.string.UserRestrictionsNoSendStickers));
        }
        if (rights.send_polls && this.defaultBannedRights.send_polls != rights.send_polls) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoSendPolls", R.string.UserRestrictionsNoSendPolls));
        }
        if (rights.embed_links && this.defaultBannedRights.embed_links != rights.embed_links) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoEmbedLinks", R.string.UserRestrictionsNoEmbedLinks));
        }
        if (rights.invite_users && this.defaultBannedRights.invite_users != rights.invite_users) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoInviteUsers", R.string.UserRestrictionsNoInviteUsers));
        }
        if (rights.pin_messages && this.defaultBannedRights.pin_messages != rights.pin_messages) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoPinMessages", R.string.UserRestrictionsNoPinMessages));
        }
        if (rights.change_info && this.defaultBannedRights.change_info != rights.change_info) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoChangeInfo", R.string.UserRestrictionsNoChangeInfo));
        }
        if (builder.length() != 0) {
            builder.replace(0, 1, builder.substring(0, 1).toUpperCase());
            builder.append('.');
        }
        return builder.toString();
    }

    public void processDone() {
        TLRPC.ChatFull chatFull;
        if (this.type != 3) {
            return;
        }
        if (this.currentChat.creator && !ChatObject.isChannel(this.currentChat) && this.selectedSlowmode != this.initialSlowmode && this.info != null) {
            MessagesController.getInstance(this.currentAccount).convertToMegaGroup(getParentActivity(), this.chatId, this, new MessagesStorage.LongCallback() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda4
                @Override // org.telegram.messenger.MessagesStorage.LongCallback
                public final void run(long j) {
                    ChatUsersActivity.this.m2162lambda$processDone$13$orgtelegramuiChatUsersActivity(j);
                }
            });
            return;
        }
        String newBannedRights = ChatObject.getBannedRightsString(this.defaultBannedRights);
        if (!newBannedRights.equals(this.initialBannedRights)) {
            getMessagesController().setDefaultBannedRole(this.chatId, this.defaultBannedRights, ChatObject.isChannel(this.currentChat), this);
            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
            if (chat != null) {
                chat.default_banned_rights = this.defaultBannedRights;
            }
        }
        int i = this.selectedSlowmode;
        if (i != this.initialSlowmode && (chatFull = this.info) != null) {
            chatFull.slowmode_seconds = getSecondsForIndex(i);
            this.info.flags |= 131072;
            getMessagesController().setChannelSlowMode(this.chatId, this.info.slowmode_seconds);
        }
        finishFragment();
    }

    /* renamed from: lambda$processDone$13$org-telegram-ui-ChatUsersActivity */
    public /* synthetic */ void m2162lambda$processDone$13$orgtelegramuiChatUsersActivity(long param) {
        if (param != 0) {
            this.chatId = param;
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(param));
            processDone();
        }
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
        if (chatFull != null) {
            int currentSlowmode = getCurrentSlowmode();
            this.initialSlowmode = currentSlowmode;
            this.selectedSlowmode = currentSlowmode;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean needDelayOpenAnimation() {
        return true;
    }

    private int getChannelAdminParticipantType(TLObject participant) {
        if ((participant instanceof TLRPC.TL_channelParticipantCreator) || (participant instanceof TLRPC.TL_channelParticipantSelf)) {
            return 0;
        }
        if ((participant instanceof TLRPC.TL_channelParticipantAdmin) || (participant instanceof TLRPC.TL_channelParticipant)) {
            return 1;
        }
        return 2;
    }

    public void loadChatParticipants(int offset, int count) {
        if (this.loadingUsers) {
            return;
        }
        this.contactsEndReached = false;
        this.botsEndReached = false;
        loadChatParticipants(offset, count, true);
    }

    private ArrayList<TLRPC.TL_channels_getParticipants> loadChatParticipantsRequests(int offset, int count, boolean reset) {
        TLRPC.Chat chat;
        TLRPC.TL_channels_getParticipants req = new TLRPC.TL_channels_getParticipants();
        ArrayList<TLRPC.TL_channels_getParticipants> requests = new ArrayList<>();
        requests.add(req);
        req.channel = getMessagesController().getInputChannel(this.chatId);
        int i = this.type;
        if (i == 0) {
            req.filter = new TLRPC.TL_channelParticipantsKicked();
        } else if (i == 1) {
            req.filter = new TLRPC.TL_channelParticipantsAdmins();
        } else if (i == 2) {
            TLRPC.ChatFull chatFull = this.info;
            if (chatFull == null || chatFull.participants_count > 200 || (chat = this.currentChat) == null || !chat.megagroup) {
                if (this.selectType == 1) {
                    if (!this.contactsEndReached) {
                        this.delayResults = 2;
                        req.filter = new TLRPC.TL_channelParticipantsContacts();
                        this.contactsEndReached = true;
                        requests.addAll(loadChatParticipantsRequests(0, 200, false));
                    } else {
                        req.filter = new TLRPC.TL_channelParticipantsRecent();
                    }
                } else if (!this.contactsEndReached) {
                    this.delayResults = 3;
                    req.filter = new TLRPC.TL_channelParticipantsContacts();
                    this.contactsEndReached = true;
                    requests.addAll(loadChatParticipantsRequests(0, 200, false));
                } else if (!this.botsEndReached) {
                    req.filter = new TLRPC.TL_channelParticipantsBots();
                    this.botsEndReached = true;
                    requests.addAll(loadChatParticipantsRequests(0, 200, false));
                } else {
                    req.filter = new TLRPC.TL_channelParticipantsRecent();
                }
            } else {
                req.filter = new TLRPC.TL_channelParticipantsRecent();
            }
        } else if (i == 3) {
            req.filter = new TLRPC.TL_channelParticipantsBanned();
        }
        req.filter.q = "";
        req.offset = offset;
        req.limit = count;
        return requests;
    }

    private void loadChatParticipants(int offset, int count, boolean reset) {
        LongSparseArray<TLRPC.TL_groupCallParticipant> longSparseArray;
        if (!ChatObject.isChannel(this.currentChat)) {
            this.loadingUsers = false;
            this.participants.clear();
            this.bots.clear();
            this.contacts.clear();
            this.participantsMap.clear();
            this.contactsMap.clear();
            this.botsMap.clear();
            int i = this.type;
            if (i == 1) {
                TLRPC.ChatFull chatFull = this.info;
                if (chatFull != null) {
                    int size = chatFull.participants.participants.size();
                    for (int a = 0; a < size; a++) {
                        TLRPC.ChatParticipant participant = this.info.participants.participants.get(a);
                        if ((participant instanceof TLRPC.TL_chatParticipantCreator) || (participant instanceof TLRPC.TL_chatParticipantAdmin)) {
                            this.participants.add(participant);
                        }
                        this.participantsMap.put(participant.user_id, participant);
                    }
                }
            } else if (i == 2 && this.info != null) {
                long selfUserId = getUserConfig().clientUserId;
                int size2 = this.info.participants.participants.size();
                for (int a2 = 0; a2 < size2; a2++) {
                    TLRPC.ChatParticipant participant2 = this.info.participants.participants.get(a2);
                    if ((this.selectType == 0 || participant2.user_id != selfUserId) && ((longSparseArray = this.ignoredUsers) == null || longSparseArray.indexOfKey(participant2.user_id) < 0)) {
                        if (this.selectType == 1) {
                            if (getContactsController().isContact(participant2.user_id)) {
                                this.contacts.add(participant2);
                                this.contactsMap.put(participant2.user_id, participant2);
                            } else if (!UserObject.isDeleted(getMessagesController().getUser(Long.valueOf(participant2.user_id)))) {
                                this.participants.add(participant2);
                                this.participantsMap.put(participant2.user_id, participant2);
                            }
                        } else if (getContactsController().isContact(participant2.user_id)) {
                            this.contacts.add(participant2);
                            this.contactsMap.put(participant2.user_id, participant2);
                        } else {
                            TLRPC.User user = getMessagesController().getUser(Long.valueOf(participant2.user_id));
                            if (user != null && user.bot) {
                                this.bots.add(participant2);
                                this.botsMap.put(participant2.user_id, participant2);
                            } else {
                                this.participants.add(participant2);
                                this.participantsMap.put(participant2.user_id, participant2);
                            }
                        }
                    }
                }
            }
            ListAdapter listAdapter = this.listViewAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            updateRows();
            ListAdapter listAdapter2 = this.listViewAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
                return;
            }
            return;
        }
        this.loadingUsers = true;
        StickerEmptyView stickerEmptyView = this.emptyView;
        if (stickerEmptyView != null) {
            stickerEmptyView.showProgress(true, false);
        }
        ListAdapter listAdapter3 = this.listViewAdapter;
        if (listAdapter3 != null) {
            listAdapter3.notifyDataSetChanged();
        }
        final ArrayList<TLRPC.TL_channels_getParticipants> requests = loadChatParticipantsRequests(offset, count, reset);
        final ArrayList<TLRPC.TL_channels_channelParticipants> responses = new ArrayList<>();
        final Runnable onRequestsEnd = new Runnable() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                ChatUsersActivity.this.m2160lambda$loadChatParticipants$14$orgtelegramuiChatUsersActivity(requests, responses);
            }
        };
        final AtomicInteger responsesReceived = new AtomicInteger(0);
        for (int i2 = 0; i2 < requests.size(); i2++) {
            responses.add(null);
            final int index = i2;
            int reqId = getConnectionsManager().sendRequest(requests.get(index), new RequestDelegate() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda5
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda15
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChatUsersActivity.lambda$loadChatParticipants$15(TLRPC.TL_error.this, tLObject, r3, r4, r5, r6, r7);
                        }
                    });
                }
            });
            getConnectionsManager().bindRequestToGuid(reqId, this.classGuid);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:142:0x0160 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:64:0x0152  */
    /* JADX WARN: Removed duplicated region for block: B:90:0x01a3 A[Catch: Exception -> 0x01a9, TRY_LEAVE, TryCatch #1 {Exception -> 0x01a9, blocks: (B:86:0x0198, B:88:0x019e, B:90:0x01a3), top: B:128:0x0198 }] */
    /* renamed from: lambda$loadChatParticipants$14$org-telegram-ui-ChatUsersActivity */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m2160lambda$loadChatParticipants$14$orgtelegramuiChatUsersActivity(java.util.ArrayList r23, java.util.ArrayList r24) {
        /*
            Method dump skipped, instructions count: 535
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.m2160lambda$loadChatParticipants$14$orgtelegramuiChatUsersActivity(java.util.ArrayList, java.util.ArrayList):void");
    }

    public static /* synthetic */ void lambda$loadChatParticipants$15(TLRPC.TL_error error, TLObject response, ArrayList responses, int index, AtomicInteger responsesReceived, ArrayList requests, Runnable onRequestsEnd) {
        if (error == null && (response instanceof TLRPC.TL_channels_channelParticipants)) {
            responses.set(index, (TLRPC.TL_channels_channelParticipants) response);
        }
        responsesReceived.getAndIncrement();
        if (responsesReceived.get() == requests.size()) {
            onRequestsEnd.run();
        }
    }

    public void sortUsers(ArrayList<TLObject> objects) {
        final int currentTime = getConnectionsManager().getCurrentTime();
        Collections.sort(objects, new Comparator() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda3
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return ChatUsersActivity.this.m2164lambda$sortUsers$17$orgtelegramuiChatUsersActivity(currentTime, (TLObject) obj, (TLObject) obj2);
            }
        });
    }

    /* renamed from: lambda$sortUsers$17$org-telegram-ui-ChatUsersActivity */
    public /* synthetic */ int m2164lambda$sortUsers$17$orgtelegramuiChatUsersActivity(int currentTime, TLObject lhs, TLObject rhs) {
        TLRPC.ChannelParticipant p1 = (TLRPC.ChannelParticipant) lhs;
        TLRPC.ChannelParticipant p2 = (TLRPC.ChannelParticipant) rhs;
        long peer1 = MessageObject.getPeerId(p1.peer);
        long peer2 = MessageObject.getPeerId(p2.peer);
        int status1 = 0;
        if (peer1 > 0) {
            TLRPC.User user1 = getMessagesController().getUser(Long.valueOf(MessageObject.getPeerId(p1.peer)));
            if (user1 != null && user1.status != null) {
                status1 = user1.self ? currentTime + 50000 : user1.status.expires;
            }
        } else {
            status1 = -100;
        }
        int status2 = 0;
        if (peer2 > 0) {
            TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(MessageObject.getPeerId(p2.peer)));
            if (user2 != null && user2.status != null) {
                status2 = user2.self ? currentTime + 50000 : user2.status.expires;
            }
        } else {
            status2 = -100;
        }
        if (status1 > 0 && status2 > 0) {
            if (status1 > status2) {
                return 1;
            }
            return status1 < status2 ? -1 : 0;
        } else if (status1 < 0 && status2 < 0) {
            if (status1 > status2) {
                return 1;
            }
            return status1 < status2 ? -1 : 0;
        } else if ((status1 < 0 && status2 > 0) || (status1 == 0 && status2 != 0)) {
            return -1;
        } else {
            return ((status2 >= 0 || status1 <= 0) && (status2 != 0 || status1 == 0)) ? 0 : 1;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        StickerEmptyView stickerEmptyView = this.emptyView;
        if (stickerEmptyView != null) {
            stickerEmptyView.requestLayout();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyHidden() {
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
    }

    public int getSelectType() {
        return this.selectType;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.openTransitionStarted = true;
        }
        if (isOpen && !backward && this.needOpenSearch) {
            this.searchItem.getSearchField().requestFocus();
            AndroidUtilities.showKeyboard(this.searchItem.getSearchField());
            this.searchItem.setVisibility(8);
        }
    }

    /* loaded from: classes4.dex */
    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private int contactsStartRow;
        private int globalStartRow;
        private int groupStartRow;
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        private boolean searchInProgress;
        private Runnable searchRunnable;
        private ArrayList<Object> searchResult = new ArrayList<>();
        private LongSparseArray<TLObject> searchResultMap = new LongSparseArray<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private int totalCount = 0;

        public SearchAdapter(Context context) {
            ChatUsersActivity.this = r2;
            this.mContext = context;
            SearchAdapterHelper searchAdapterHelper = new SearchAdapterHelper(true);
            this.searchAdapterHelper = searchAdapterHelper;
            searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() { // from class: org.telegram.ui.ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda4
                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ boolean canApplySearchResults(int i) {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ LongSparseArray getExcludeCallParticipants() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ LongSparseArray getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public final void onDataSetChanged(int i) {
                    ChatUsersActivity.SearchAdapter.this.m2169lambda$new$0$orgtelegramuiChatUsersActivity$SearchAdapter(i);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-ChatUsersActivity$SearchAdapter */
        public /* synthetic */ void m2169lambda$new$0$orgtelegramuiChatUsersActivity$SearchAdapter(int searchId) {
            if (!this.searchAdapterHelper.isSearchInProgress()) {
                int oldItemCount = getItemCount();
                notifyDataSetChanged();
                if (getItemCount() > oldItemCount) {
                    ChatUsersActivity.this.showItemsAnimated(oldItemCount);
                }
                if (!this.searchInProgress && getItemCount() == 0 && searchId != 0) {
                    ChatUsersActivity.this.emptyView.showProgress(false, true);
                }
            }
        }

        public void searchUsers(final String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            this.searchResult.clear();
            this.searchResultMap.clear();
            this.searchResultNames.clear();
            this.searchAdapterHelper.mergeResults(null);
            this.searchAdapterHelper.queryServerSearch(null, ChatUsersActivity.this.type != 0, false, true, false, false, ChatObject.isChannel(ChatUsersActivity.this.currentChat) ? ChatUsersActivity.this.chatId : 0L, false, ChatUsersActivity.this.type, 0);
            notifyDataSetChanged();
            if (!TextUtils.isEmpty(query)) {
                this.searchInProgress = true;
                ChatUsersActivity.this.emptyView.showProgress(true, true);
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                Runnable runnable = new Runnable() { // from class: org.telegram.ui.ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatUsersActivity.SearchAdapter.this.m2173xf19e43d8(query);
                    }
                };
                this.searchRunnable = runnable;
                dispatchQueue.postRunnable(runnable, 300L);
            }
        }

        /* renamed from: processSearch */
        public void m2173xf19e43d8(final String query) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ChatUsersActivity.SearchAdapter.this.m2172x148baf11(query);
                }
            });
        }

        /* renamed from: lambda$processSearch$3$org-telegram-ui-ChatUsersActivity$SearchAdapter */
        public /* synthetic */ void m2172x148baf11(final String query) {
            final ArrayList<TLRPC.TL_contact> contactsCopy = null;
            this.searchRunnable = null;
            final ArrayList<TLObject> participantsCopy = (ChatObject.isChannel(ChatUsersActivity.this.currentChat) || ChatUsersActivity.this.info == null) ? null : new ArrayList<>(ChatUsersActivity.this.info.participants.participants);
            if (ChatUsersActivity.this.selectType == 1) {
                contactsCopy = new ArrayList<>(ChatUsersActivity.this.getContactsController().contacts);
            }
            Runnable addContacts = null;
            if (participantsCopy != null || contactsCopy != null) {
                addContacts = new Runnable() { // from class: org.telegram.ui.ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatUsersActivity.SearchAdapter.this.m2171x15021510(query, participantsCopy, contactsCopy);
                    }
                };
            } else {
                this.searchInProgress = false;
            }
            this.searchAdapterHelper.queryServerSearch(query, ChatUsersActivity.this.selectType != 0, false, true, false, false, ChatObject.isChannel(ChatUsersActivity.this.currentChat) ? ChatUsersActivity.this.chatId : 0L, false, ChatUsersActivity.this.type, 1, addContacts);
        }

        /* JADX WARN: Code restructure failed: missing block: B:48:0x014d, code lost:
            if (r3.contains(" " + r4) != false) goto L55;
         */
        /* JADX WARN: Code restructure failed: missing block: B:88:0x0273, code lost:
            if (r6.contains(" " + r15) != false) goto L94;
         */
        /* JADX WARN: Removed duplicated region for block: B:101:0x02d2 A[LOOP:3: B:79:0x0239->B:101:0x02d2, LOOP_END] */
        /* JADX WARN: Removed duplicated region for block: B:113:0x0162 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:119:0x0289 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:62:0x019f A[LOOP:1: B:39:0x010f->B:62:0x019f, LOOP_END] */
        /* renamed from: lambda$processSearch$2$org-telegram-ui-ChatUsersActivity$SearchAdapter */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public /* synthetic */ void m2171x15021510(java.lang.String r31, java.util.ArrayList r32, java.util.ArrayList r33) {
            /*
                Method dump skipped, instructions count: 771
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.SearchAdapter.m2171x15021510(java.lang.String, java.util.ArrayList, java.util.ArrayList):void");
        }

        private void updateSearchResults(final ArrayList<Object> users, final LongSparseArray<TLObject> usersMap, final ArrayList<CharSequence> names, final ArrayList<TLObject> participants) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ChatUsersActivity.SearchAdapter.this.m2174x4d77140(users, usersMap, names, participants);
                }
            });
        }

        /* renamed from: lambda$updateSearchResults$4$org-telegram-ui-ChatUsersActivity$SearchAdapter */
        public /* synthetic */ void m2174x4d77140(ArrayList users, LongSparseArray usersMap, ArrayList names, ArrayList participants) {
            if (!ChatUsersActivity.this.searching) {
                return;
            }
            this.searchInProgress = false;
            this.searchResult = users;
            this.searchResultMap = usersMap;
            this.searchResultNames = names;
            this.searchAdapterHelper.mergeResults(users);
            if (!ChatObject.isChannel(ChatUsersActivity.this.currentChat)) {
                ArrayList<TLObject> search = this.searchAdapterHelper.getGroupSearch();
                search.clear();
                search.addAll(participants);
            }
            int oldItemCount = getItemCount();
            notifyDataSetChanged();
            if (getItemCount() > oldItemCount) {
                ChatUsersActivity.this.showItemsAnimated(oldItemCount);
            }
            if (!this.searchAdapterHelper.isSearchInProgress() && getItemCount() == 0) {
                ChatUsersActivity.this.emptyView.showProgress(false, true);
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != 1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.totalCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            this.totalCount = 0;
            int count = this.searchAdapterHelper.getGroupSearch().size();
            if (count != 0) {
                this.groupStartRow = 0;
                this.totalCount += count + 1;
            } else {
                this.groupStartRow = -1;
            }
            int count2 = this.searchResult.size();
            if (count2 != 0) {
                int i = this.totalCount;
                this.contactsStartRow = i;
                this.totalCount = i + count2 + 1;
            } else {
                this.contactsStartRow = -1;
            }
            int count3 = this.searchAdapterHelper.getGlobalSearch().size();
            if (count3 != 0) {
                int i2 = this.totalCount;
                this.globalStartRow = i2;
                this.totalCount = i2 + count3 + 1;
            } else {
                this.globalStartRow = -1;
            }
            if (ChatUsersActivity.this.searching && ChatUsersActivity.this.listView != null && ChatUsersActivity.this.listView.getAdapter() != ChatUsersActivity.this.searchListViewAdapter) {
                ChatUsersActivity.this.listView.setAnimateEmptyView(true, 0);
                ChatUsersActivity.this.listView.setAdapter(ChatUsersActivity.this.searchListViewAdapter);
                ChatUsersActivity.this.listView.setFastScrollVisible(false);
                ChatUsersActivity.this.listView.setVerticalScrollBarEnabled(true);
            }
            super.notifyDataSetChanged();
        }

        public void removeUserId(long userId) {
            this.searchAdapterHelper.removeUserId(userId);
            Object object = this.searchResultMap.get(userId);
            if (object != null) {
                this.searchResult.remove(object);
            }
            notifyDataSetChanged();
        }

        public TLObject getItem(int i) {
            int count = this.searchAdapterHelper.getGroupSearch().size();
            if (count != 0) {
                if (count + 1 > i) {
                    if (i == 0) {
                        return null;
                    }
                    return this.searchAdapterHelper.getGroupSearch().get(i - 1);
                }
                i -= count + 1;
            }
            int count2 = this.searchResult.size();
            if (count2 != 0) {
                if (count2 + 1 > i) {
                    if (i == 0) {
                        return null;
                    }
                    return (TLObject) this.searchResult.get(i - 1);
                }
                i -= count2 + 1;
            }
            int count3 = this.searchAdapterHelper.getGlobalSearch().size();
            if (count3 == 0 || count3 + 1 <= i || i == 0) {
                return null;
            }
            return this.searchAdapterHelper.getGlobalSearch().get(i - 1);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    ManageChatUserCell manageChatUserCell = new ManageChatUserCell(this.mContext, 2, 2, ChatUsersActivity.this.selectType == 0);
                    manageChatUserCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    manageChatUserCell.setDelegate(new ManageChatUserCell.ManageChatUserCellDelegate() { // from class: org.telegram.ui.ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda5
                        @Override // org.telegram.ui.Cells.ManageChatUserCell.ManageChatUserCellDelegate
                        public final boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell2, boolean z) {
                            return ChatUsersActivity.SearchAdapter.this.m2170xcd403d42(manageChatUserCell2, z);
                        }
                    });
                    view = manageChatUserCell;
                    break;
                default:
                    view = new GraySectionCell(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$5$org-telegram-ui-ChatUsersActivity$SearchAdapter */
        public /* synthetic */ boolean m2170xcd403d42(ManageChatUserCell cell, boolean click) {
            TLObject object = getItem(((Integer) cell.getTag()).intValue());
            if (object instanceof TLRPC.ChannelParticipant) {
                TLRPC.ChannelParticipant participant = (TLRPC.ChannelParticipant) object;
                return ChatUsersActivity.this.createMenuForParticipant(participant, !click);
            }
            return false;
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:102:0x0214  */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r21, int r22) {
            /*
                Method dump skipped, instructions count: 582
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.SearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == this.globalStartRow || i == this.groupStartRow || i == this.contactsStartRow) {
                return 1;
            }
            return 0;
        }
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            ChatUsersActivity.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int viewType = holder.getItemViewType();
            if (viewType == 7) {
                return ChatObject.canBlockUsers(ChatUsersActivity.this.currentChat);
            }
            if (viewType != 0) {
                return viewType == 0 || viewType == 2 || viewType == 6;
            }
            ManageChatUserCell cell = (ManageChatUserCell) holder.itemView;
            Object object = cell.getCurrentObject();
            if (ChatUsersActivity.this.type != 1 && (object instanceof TLRPC.User)) {
                TLRPC.User user = (TLRPC.User) object;
                if (user.self) {
                    return false;
                }
            }
            return true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return ChatUsersActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ChooseView manageChatUserCell;
            boolean z = false;
            int i = 6;
            switch (viewType) {
                case 0:
                    Context context = this.mContext;
                    int i2 = (ChatUsersActivity.this.type == 0 || ChatUsersActivity.this.type == 3) ? 7 : 6;
                    if (ChatUsersActivity.this.type != 0 && ChatUsersActivity.this.type != 3) {
                        i = 2;
                    }
                    if (ChatUsersActivity.this.selectType == 0) {
                        z = true;
                    }
                    ManageChatUserCell manageChatUserCell2 = new ManageChatUserCell(context, i2, i, z);
                    manageChatUserCell2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    manageChatUserCell2.setDelegate(new ManageChatUserCell.ManageChatUserCellDelegate() { // from class: org.telegram.ui.ChatUsersActivity$ListAdapter$$ExternalSyntheticLambda0
                        @Override // org.telegram.ui.Cells.ManageChatUserCell.ManageChatUserCellDelegate
                        public final boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell3, boolean z2) {
                            return ChatUsersActivity.ListAdapter.this.m2168xfc30507(manageChatUserCell3, z2);
                        }
                    });
                    manageChatUserCell = manageChatUserCell2;
                    break;
                case 1:
                    manageChatUserCell = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 2:
                    ManageChatTextCell manageChatTextCell = new ManageChatTextCell(this.mContext);
                    manageChatTextCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    manageChatUserCell = manageChatTextCell;
                    break;
                case 3:
                    manageChatUserCell = new ShadowSectionCell(this.mContext);
                    break;
                case 4:
                    TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                    TextInfoPrivacyCell privacyCell = textInfoPrivacyCell;
                    if (ChatUsersActivity.this.isChannel) {
                        privacyCell.setText(LocaleController.getString((int) R.string.NoBlockedChannel2));
                    } else {
                        privacyCell.setText(LocaleController.getString((int) R.string.NoBlockedGroup2));
                    }
                    privacyCell.setBackground(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    manageChatUserCell = textInfoPrivacyCell;
                    break;
                case 5:
                    HeaderCell headerCell = new HeaderCell(this.mContext, Theme.key_windowBackgroundWhiteBlueHeader, 21, 11, false);
                    headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    headerCell.setHeight(43);
                    manageChatUserCell = headerCell;
                    break;
                case 6:
                    TextSettingsCell textSettingsCell = new TextSettingsCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    manageChatUserCell = textSettingsCell;
                    break;
                case 7:
                    TextCheckCell2 textCheckCell2 = new TextCheckCell2(this.mContext);
                    textCheckCell2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    manageChatUserCell = textCheckCell2;
                    break;
                case 8:
                    GraySectionCell graySectionCell = new GraySectionCell(this.mContext);
                    graySectionCell.setBackground(null);
                    manageChatUserCell = graySectionCell;
                    break;
                case 9:
                default:
                    ChooseView chooseView = new ChooseView(this.mContext);
                    chooseView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    manageChatUserCell = chooseView;
                    break;
                case 10:
                    manageChatUserCell = new LoadingCell(this.mContext, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(120.0f));
                    break;
                case 11:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext);
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.setViewType(6);
                    flickerLoadingView.showDate(false);
                    flickerLoadingView.setPaddingLeft(AndroidUtilities.dp(5.0f));
                    flickerLoadingView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    flickerLoadingView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                    manageChatUserCell = flickerLoadingView;
                    break;
            }
            return new RecyclerListView.Holder(manageChatUserCell);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-ChatUsersActivity$ListAdapter */
        public /* synthetic */ boolean m2168xfc30507(ManageChatUserCell cell, boolean click) {
            TLObject participant = ChatUsersActivity.this.listViewAdapter.getItem(((Integer) cell.getTag()).intValue());
            return ChatUsersActivity.this.createMenuForParticipant(participant, !click);
        }

        /* JADX WARN: Removed duplicated region for block: B:295:0x079a  */
        /* JADX WARN: Removed duplicated region for block: B:296:0x079c  */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r28, int r29) {
            /*
                Method dump skipped, instructions count: 2144
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position != ChatUsersActivity.this.addNewRow && position != ChatUsersActivity.this.addNew2Row && position != ChatUsersActivity.this.recentActionsRow && position != ChatUsersActivity.this.gigaConvertRow) {
                if ((position >= ChatUsersActivity.this.participantsStartRow && position < ChatUsersActivity.this.participantsEndRow) || ((position >= ChatUsersActivity.this.botStartRow && position < ChatUsersActivity.this.botEndRow) || (position >= ChatUsersActivity.this.contactsStartRow && position < ChatUsersActivity.this.contactsEndRow))) {
                    return 0;
                }
                if (position != ChatUsersActivity.this.addNewSectionRow && position != ChatUsersActivity.this.participantsDividerRow && position != ChatUsersActivity.this.participantsDivider2Row) {
                    if (position != ChatUsersActivity.this.restricted1SectionRow && position != ChatUsersActivity.this.permissionsSectionRow && position != ChatUsersActivity.this.slowmodeRow && position != ChatUsersActivity.this.gigaHeaderRow) {
                        if (position != ChatUsersActivity.this.participantsInfoRow && position != ChatUsersActivity.this.slowmodeInfoRow && position != ChatUsersActivity.this.gigaInfoRow) {
                            if (position != ChatUsersActivity.this.blockedEmptyRow) {
                                if (position != ChatUsersActivity.this.removedUsersRow) {
                                    if (position != ChatUsersActivity.this.changeInfoRow && position != ChatUsersActivity.this.addUsersRow && position != ChatUsersActivity.this.pinMessagesRow && position != ChatUsersActivity.this.sendMessagesRow && position != ChatUsersActivity.this.sendMediaRow && position != ChatUsersActivity.this.sendStickersRow && position != ChatUsersActivity.this.embedLinksRow && position != ChatUsersActivity.this.sendPollsRow) {
                                        if (position != ChatUsersActivity.this.membersHeaderRow && position != ChatUsersActivity.this.contactsHeaderRow && position != ChatUsersActivity.this.botHeaderRow && position != ChatUsersActivity.this.loadingHeaderRow) {
                                            if (position != ChatUsersActivity.this.slowmodeSelectRow) {
                                                if (position == ChatUsersActivity.this.loadingProgressRow) {
                                                    return 10;
                                                }
                                                return position == ChatUsersActivity.this.loadingUserCellRow ? 11 : 0;
                                            }
                                            return 9;
                                        }
                                        return 8;
                                    }
                                    return 7;
                                }
                                return 6;
                            }
                            return 4;
                        }
                        return 1;
                    }
                    return 5;
                }
                return 3;
            }
            return 2;
        }

        public TLObject getItem(int position) {
            if (position < ChatUsersActivity.this.participantsStartRow || position >= ChatUsersActivity.this.participantsEndRow) {
                if (position < ChatUsersActivity.this.contactsStartRow || position >= ChatUsersActivity.this.contactsEndRow) {
                    if (position >= ChatUsersActivity.this.botStartRow && position < ChatUsersActivity.this.botEndRow) {
                        return (TLObject) ChatUsersActivity.this.bots.get(position - ChatUsersActivity.this.botStartRow);
                    }
                    return null;
                }
                return (TLObject) ChatUsersActivity.this.contacts.get(position - ChatUsersActivity.this.contactsStartRow);
            }
            return (TLObject) ChatUsersActivity.this.participants.get(position - ChatUsersActivity.this.participantsStartRow);
        }
    }

    public DiffCallback saveState() {
        DiffCallback diffCallback = new DiffCallback();
        diffCallback.oldRowCount = this.rowCount;
        diffCallback.oldBotStartRow = this.botStartRow;
        diffCallback.oldBotEndRow = this.botEndRow;
        diffCallback.oldBots.clear();
        diffCallback.oldBots.addAll(this.bots);
        diffCallback.oldContactsEndRow = this.contactsEndRow;
        diffCallback.oldContactsStartRow = this.contactsStartRow;
        diffCallback.oldContacts.clear();
        diffCallback.oldContacts.addAll(this.contacts);
        diffCallback.oldParticipantsStartRow = this.participantsStartRow;
        diffCallback.oldParticipantsEndRow = this.participantsEndRow;
        diffCallback.oldParticipants.clear();
        diffCallback.oldParticipants.addAll(this.participants);
        diffCallback.fillPositions(diffCallback.oldPositionToItem);
        return diffCallback;
    }

    public void updateListAnimated(DiffCallback savedState) {
        if (this.listViewAdapter == null) {
            updateRows();
            return;
        }
        updateRows();
        savedState.fillPositions(savedState.newPositionToItem);
        DiffUtil.calculateDiff(savedState).dispatchUpdatesTo(this.listViewAdapter);
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && this.layoutManager != null && recyclerListView.getChildCount() > 0) {
            View view = null;
            int position = -1;
            int i = 0;
            while (true) {
                if (i >= this.listView.getChildCount()) {
                    break;
                }
                RecyclerListView recyclerListView2 = this.listView;
                position = recyclerListView2.getChildAdapterPosition(recyclerListView2.getChildAt(i));
                if (position == -1) {
                    i++;
                } else {
                    view = this.listView.getChildAt(i);
                    break;
                }
            }
            if (view != null) {
                this.layoutManager.scrollToPositionWithOffset(position, view.getTop() - this.listView.getPaddingTop());
            }
        }
    }

    /* loaded from: classes4.dex */
    public class DiffCallback extends DiffUtil.Callback {
        SparseIntArray newPositionToItem;
        int oldBotEndRow;
        int oldBotStartRow;
        private ArrayList<TLObject> oldBots;
        private ArrayList<TLObject> oldContacts;
        int oldContactsEndRow;
        int oldContactsStartRow;
        private ArrayList<TLObject> oldParticipants;
        int oldParticipantsEndRow;
        int oldParticipantsStartRow;
        SparseIntArray oldPositionToItem;
        int oldRowCount;

        private DiffCallback() {
            ChatUsersActivity.this = r1;
            this.oldPositionToItem = new SparseIntArray();
            this.newPositionToItem = new SparseIntArray();
            this.oldParticipants = new ArrayList<>();
            this.oldBots = new ArrayList<>();
            this.oldContacts = new ArrayList<>();
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public int getOldListSize() {
            return this.oldRowCount;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public int getNewListSize() {
            return ChatUsersActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            if (oldItemPosition >= this.oldBotStartRow && oldItemPosition < this.oldBotEndRow && newItemPosition >= ChatUsersActivity.this.botStartRow && newItemPosition < ChatUsersActivity.this.botEndRow) {
                return this.oldBots.get(oldItemPosition - this.oldBotStartRow).equals(ChatUsersActivity.this.bots.get(newItemPosition - ChatUsersActivity.this.botStartRow));
            }
            if (oldItemPosition >= this.oldContactsStartRow && oldItemPosition < this.oldContactsEndRow && newItemPosition >= ChatUsersActivity.this.contactsStartRow && newItemPosition < ChatUsersActivity.this.contactsEndRow) {
                return this.oldContacts.get(oldItemPosition - this.oldContactsStartRow).equals(ChatUsersActivity.this.contacts.get(newItemPosition - ChatUsersActivity.this.contactsStartRow));
            }
            if (oldItemPosition >= this.oldParticipantsStartRow && oldItemPosition < this.oldParticipantsEndRow && newItemPosition >= ChatUsersActivity.this.participantsStartRow && newItemPosition < ChatUsersActivity.this.participantsEndRow) {
                return this.oldParticipants.get(oldItemPosition - this.oldParticipantsStartRow).equals(ChatUsersActivity.this.participants.get(newItemPosition - ChatUsersActivity.this.participantsStartRow));
            }
            return this.oldPositionToItem.get(oldItemPosition) == this.newPositionToItem.get(newItemPosition);
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return areItemsTheSame(oldItemPosition, newItemPosition) && ChatUsersActivity.this.restricted1SectionRow != newItemPosition;
        }

        public void fillPositions(SparseIntArray sparseIntArray) {
            sparseIntArray.clear();
            int pointer = 0 + 1;
            put(pointer, ChatUsersActivity.this.recentActionsRow, sparseIntArray);
            int pointer2 = pointer + 1;
            put(pointer2, ChatUsersActivity.this.addNewRow, sparseIntArray);
            int pointer3 = pointer2 + 1;
            put(pointer3, ChatUsersActivity.this.addNew2Row, sparseIntArray);
            int pointer4 = pointer3 + 1;
            put(pointer4, ChatUsersActivity.this.addNewSectionRow, sparseIntArray);
            int pointer5 = pointer4 + 1;
            put(pointer5, ChatUsersActivity.this.restricted1SectionRow, sparseIntArray);
            int pointer6 = pointer5 + 1;
            put(pointer6, ChatUsersActivity.this.participantsDividerRow, sparseIntArray);
            int pointer7 = pointer6 + 1;
            put(pointer7, ChatUsersActivity.this.participantsDivider2Row, sparseIntArray);
            int pointer8 = pointer7 + 1;
            put(pointer8, ChatUsersActivity.this.gigaHeaderRow, sparseIntArray);
            int pointer9 = pointer8 + 1;
            put(pointer9, ChatUsersActivity.this.gigaConvertRow, sparseIntArray);
            int pointer10 = pointer9 + 1;
            put(pointer10, ChatUsersActivity.this.gigaInfoRow, sparseIntArray);
            int pointer11 = pointer10 + 1;
            put(pointer11, ChatUsersActivity.this.participantsInfoRow, sparseIntArray);
            int pointer12 = pointer11 + 1;
            put(pointer12, ChatUsersActivity.this.blockedEmptyRow, sparseIntArray);
            int pointer13 = pointer12 + 1;
            put(pointer13, ChatUsersActivity.this.permissionsSectionRow, sparseIntArray);
            int pointer14 = pointer13 + 1;
            put(pointer14, ChatUsersActivity.this.sendMessagesRow, sparseIntArray);
            int pointer15 = pointer14 + 1;
            put(pointer15, ChatUsersActivity.this.sendMediaRow, sparseIntArray);
            int pointer16 = pointer15 + 1;
            put(pointer16, ChatUsersActivity.this.sendStickersRow, sparseIntArray);
            int pointer17 = pointer16 + 1;
            put(pointer17, ChatUsersActivity.this.sendPollsRow, sparseIntArray);
            int pointer18 = pointer17 + 1;
            put(pointer18, ChatUsersActivity.this.embedLinksRow, sparseIntArray);
            int pointer19 = pointer18 + 1;
            put(pointer19, ChatUsersActivity.this.addUsersRow, sparseIntArray);
            int pointer20 = pointer19 + 1;
            put(pointer20, ChatUsersActivity.this.pinMessagesRow, sparseIntArray);
            int pointer21 = pointer20 + 1;
            put(pointer21, ChatUsersActivity.this.changeInfoRow, sparseIntArray);
            int pointer22 = pointer21 + 1;
            put(pointer22, ChatUsersActivity.this.removedUsersRow, sparseIntArray);
            int pointer23 = pointer22 + 1;
            put(pointer23, ChatUsersActivity.this.contactsHeaderRow, sparseIntArray);
            int pointer24 = pointer23 + 1;
            put(pointer24, ChatUsersActivity.this.botHeaderRow, sparseIntArray);
            int pointer25 = pointer24 + 1;
            put(pointer25, ChatUsersActivity.this.membersHeaderRow, sparseIntArray);
            int pointer26 = pointer25 + 1;
            put(pointer26, ChatUsersActivity.this.slowmodeRow, sparseIntArray);
            int pointer27 = pointer26 + 1;
            put(pointer27, ChatUsersActivity.this.slowmodeSelectRow, sparseIntArray);
            int pointer28 = pointer27 + 1;
            put(pointer28, ChatUsersActivity.this.slowmodeInfoRow, sparseIntArray);
            int pointer29 = pointer28 + 1;
            put(pointer29, ChatUsersActivity.this.loadingProgressRow, sparseIntArray);
            int pointer30 = pointer29 + 1;
            put(pointer30, ChatUsersActivity.this.loadingUserCellRow, sparseIntArray);
            put(pointer30 + 1, ChatUsersActivity.this.loadingHeaderRow, sparseIntArray);
        }

        private void put(int id, int position, SparseIntArray sparseIntArray) {
            if (position >= 0) {
                sparseIntArray.put(position, id);
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda7
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                ChatUsersActivity.this.m2159lambda$getThemeDescriptions$18$orgtelegramuiChatUsersActivity();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, ManageChatUserCell.class, ManageChatTextCell.class, TextCheckCell2.class, TextSettingsCell.class, ChooseView.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySectionText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switch2Track));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switch2TrackChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, Theme.key_windowBackgroundWhiteGrayText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, Theme.key_windowBackgroundWhiteBlueText));
        themeDescriptions.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_undo_background));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_cancelColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_cancelColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueButton));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{StickerEmptyView.class}, new String[]{"title"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{StickerEmptyView.class}, new String[]{"subtitle"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.emptyView.title, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.emptyView.subtitle, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$18$org-telegram-ui-ChatUsersActivity */
    public /* synthetic */ void m2159lambda$getThemeDescriptions$18$orgtelegramuiChatUsersActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) child).update(0);
                }
            }
        }
    }
}
