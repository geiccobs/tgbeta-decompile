package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.ShareDialogCell;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes5.dex */
public class JoinCallAlert extends BottomSheet {
    public static final int TYPE_CREATE = 0;
    public static final int TYPE_DISPLAY = 2;
    public static final int TYPE_JOIN = 1;
    private static ArrayList<TLRPC.Peer> cachedChats;
    private static long lastCacheDid;
    private static long lastCacheTime;
    private static int lastCachedAccount;
    private boolean animationInProgress;
    private ArrayList<TLRPC.Peer> chats;
    private TLRPC.Peer currentPeer;
    private int currentType;
    private JoinCallAlertDelegate delegate;
    private BottomSheetCell doneButton;
    private boolean ignoreLayout;
    private RecyclerListView listView;
    private int[] location = new int[2];
    private TextView messageTextView;
    private boolean schedule;
    private int scrollOffsetY;
    private TLRPC.InputPeer selectAfterDismiss;
    private TLRPC.Peer selectedPeer;
    private Drawable shadowDrawable;
    private TextView textView;

    /* loaded from: classes5.dex */
    public interface JoinCallAlertDelegate {
        void didSelectChat(TLRPC.InputPeer inputPeer, boolean z, boolean z2);
    }

    public static void resetCache() {
        cachedChats = null;
    }

    public static void processDeletedChat(int account, long did) {
        ArrayList<TLRPC.Peer> arrayList;
        if (lastCachedAccount != account || (arrayList = cachedChats) == null || did > 0) {
            return;
        }
        int a = 0;
        int N = arrayList.size();
        while (true) {
            if (a >= N) {
                break;
            } else if (MessageObject.getPeerId(cachedChats.get(a)) != did) {
                a++;
            } else {
                cachedChats.remove(a);
                break;
            }
        }
        if (cachedChats.isEmpty()) {
            cachedChats = null;
        }
    }

    /* loaded from: classes5.dex */
    public class BottomSheetCell extends FrameLayout {
        private View background;
        private boolean hasBackground;
        private CharSequence text;
        private TextView[] textView = new TextView[2];

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BottomSheetCell(Context context, boolean withoutBackground) {
            super(context);
            JoinCallAlert.this = this$0;
            this.hasBackground = !withoutBackground;
            setBackground(null);
            View view = new View(context);
            this.background = view;
            if (this.hasBackground) {
                view.setBackground(Theme.AdaptiveRipple.filledRect(Theme.key_featuredStickers_addButton, 4.0f));
            }
            addView(this.background, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, withoutBackground ? 0.0f : 16.0f, 16.0f, 16.0f));
            for (int a = 0; a < 2; a++) {
                this.textView[a] = new TextView(context);
                this.textView[a].setFocusable(false);
                this.textView[a].setLines(1);
                this.textView[a].setSingleLine(true);
                this.textView[a].setGravity(1);
                this.textView[a].setEllipsize(TextUtils.TruncateAt.END);
                this.textView[a].setGravity(17);
                if (this.hasBackground) {
                    this.textView[a].setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
                    this.textView[a].setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                } else {
                    this.textView[a].setTextColor(Theme.getColor(Theme.key_featuredStickers_addButton));
                }
                this.textView[a].setImportantForAccessibility(2);
                this.textView[a].setTextSize(1, 14.0f);
                this.textView[a].setPadding(0, 0, 0, this.hasBackground ? 0 : AndroidUtilities.dp(13.0f));
                addView(this.textView[a], LayoutHelper.createFrame(-2, -2.0f, 17, 24.0f, 0.0f, 24.0f, 0.0f));
                if (a == 1) {
                    this.textView[a].setAlpha(0.0f);
                }
            }
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.hasBackground ? 80.0f : 50.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        public void setText(CharSequence text, boolean animated) {
            this.text = text;
            if (!animated) {
                this.textView[0].setText(text);
                return;
            }
            this.textView[1].setText(text);
            JoinCallAlert.this.animationInProgress = true;
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(180L);
            animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet.playTogether(ObjectAnimator.ofFloat(this.textView[0], View.ALPHA, 1.0f, 0.0f), ObjectAnimator.ofFloat(this.textView[0], View.TRANSLATION_Y, 0.0f, -AndroidUtilities.dp(10.0f)), ObjectAnimator.ofFloat(this.textView[1], View.ALPHA, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.textView[1], View.TRANSLATION_Y, AndroidUtilities.dp(10.0f), 0.0f));
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.JoinCallAlert.BottomSheetCell.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    JoinCallAlert.this.animationInProgress = false;
                    TextView temp = BottomSheetCell.this.textView[0];
                    BottomSheetCell.this.textView[0] = BottomSheetCell.this.textView[1];
                    BottomSheetCell.this.textView[1] = temp;
                }
            });
            animatorSet.start();
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setClassName("android.widget.Button");
            info.setClickable(true);
        }
    }

    public static void checkFewUsers(Context context, final long did, final AccountInstance accountInstance, final MessagesStorage.BooleanCallback callback) {
        if (lastCachedAccount == accountInstance.getCurrentAccount() && lastCacheDid == did && cachedChats != null && SystemClock.elapsedRealtime() - lastCacheTime < 240000) {
            boolean z = true;
            if (cachedChats.size() != 1) {
                z = false;
            }
            callback.run(z);
            return;
        }
        final AlertDialog progressDialog = new AlertDialog(context, 3);
        TLRPC.TL_phone_getGroupCallJoinAs req = new TLRPC.TL_phone_getGroupCallJoinAs();
        req.peer = accountInstance.getMessagesController().getInputPeer(did);
        final int reqId = accountInstance.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.JoinCallAlert$$ExternalSyntheticLambda6
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.JoinCallAlert$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        JoinCallAlert.lambda$checkFewUsers$0(AlertDialog.this, tLObject, r3, r5, r6);
                    }
                });
            }
        });
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.Components.JoinCallAlert$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                AccountInstance.this.getConnectionsManager().cancelRequest(reqId, true);
            }
        });
        try {
            progressDialog.showDelayed(500L);
        } catch (Exception e) {
        }
    }

    public static /* synthetic */ void lambda$checkFewUsers$0(AlertDialog progressDialog, TLObject response, long did, AccountInstance accountInstance, MessagesStorage.BooleanCallback callback) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (response != null) {
            TLRPC.TL_phone_joinAsPeers res = (TLRPC.TL_phone_joinAsPeers) response;
            cachedChats = res.peers;
            lastCacheDid = did;
            lastCacheTime = SystemClock.elapsedRealtime();
            lastCachedAccount = accountInstance.getCurrentAccount();
            boolean z = false;
            accountInstance.getMessagesController().putChats(res.chats, false);
            accountInstance.getMessagesController().putUsers(res.users, false);
            if (res.peers.size() == 1) {
                z = true;
            }
            callback.run(z);
        }
    }

    public static void open(final Context context, final long did, final AccountInstance accountInstance, final BaseFragment fragment, final int type, final TLRPC.Peer scheduledPeer, final JoinCallAlertDelegate delegate) {
        if (context != null && delegate != null) {
            if (lastCachedAccount != accountInstance.getCurrentAccount() || lastCacheDid != did || cachedChats == null || SystemClock.elapsedRealtime() - lastCacheTime >= 300000) {
                final AlertDialog progressDialog = new AlertDialog(context, 3);
                TLRPC.TL_phone_getGroupCallJoinAs req = new TLRPC.TL_phone_getGroupCallJoinAs();
                req.peer = accountInstance.getMessagesController().getInputPeer(did);
                final int reqId = accountInstance.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.JoinCallAlert$$ExternalSyntheticLambda7
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.JoinCallAlert$$ExternalSyntheticLambda5
                            @Override // java.lang.Runnable
                            public final void run() {
                                JoinCallAlert.lambda$open$3(AlertDialog.this, tLObject, r3, r4, r5, r7, r8, r9, r10);
                            }
                        });
                    }
                });
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.Components.JoinCallAlert$$ExternalSyntheticLambda1
                    @Override // android.content.DialogInterface.OnCancelListener
                    public final void onCancel(DialogInterface dialogInterface) {
                        AccountInstance.this.getConnectionsManager().cancelRequest(reqId, true);
                    }
                });
                try {
                    progressDialog.showDelayed(500L);
                } catch (Exception e) {
                }
            } else if (cachedChats.size() == 1 && type != 0) {
                TLRPC.InputPeer peer = accountInstance.getMessagesController().getInputPeer(MessageObject.getPeerId(cachedChats.get(0)));
                delegate.didSelectChat(peer, false, false);
            } else {
                showAlert(context, did, cachedChats, fragment, type, scheduledPeer, delegate);
            }
        }
    }

    public static /* synthetic */ void lambda$open$3(AlertDialog progressDialog, TLObject response, AccountInstance accountInstance, JoinCallAlertDelegate delegate, long did, Context context, BaseFragment fragment, int type, TLRPC.Peer scheduledPeer) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (response != null) {
            TLRPC.TL_phone_joinAsPeers res = (TLRPC.TL_phone_joinAsPeers) response;
            if (res.peers.size() == 1) {
                TLRPC.InputPeer peer = accountInstance.getMessagesController().getInputPeer(MessageObject.getPeerId(res.peers.get(0)));
                delegate.didSelectChat(peer, false, false);
                return;
            }
            cachedChats = res.peers;
            lastCacheDid = did;
            lastCacheTime = SystemClock.elapsedRealtime();
            lastCachedAccount = accountInstance.getCurrentAccount();
            accountInstance.getMessagesController().putChats(res.chats, false);
            accountInstance.getMessagesController().putUsers(res.users, false);
            showAlert(context, did, res.peers, fragment, type, scheduledPeer, delegate);
        }
    }

    private static void showAlert(Context context, long dialogId, ArrayList<TLRPC.Peer> peers, BaseFragment fragment, int type, TLRPC.Peer scheduledPeer, JoinCallAlertDelegate delegate) {
        JoinCallAlert alert = new JoinCallAlert(context, dialogId, peers, type, scheduledPeer, delegate);
        if (fragment != null) {
            if (fragment.getParentActivity() != null) {
                fragment.showDialog(alert);
                return;
            }
            return;
        }
        alert.show();
    }

    private JoinCallAlert(Context context, long dialogId, ArrayList<TLRPC.Peer> arrayList, int type, TLRPC.Peer scheduledPeer, final JoinCallAlertDelegate delegate) {
        super(context, false);
        int backgroundColor;
        ViewGroup internalLayout;
        int a;
        setApplyBottomPadding(false);
        this.chats = new ArrayList<>(arrayList);
        this.delegate = delegate;
        this.currentType = type;
        Drawable mutate = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        this.shadowDrawable = mutate;
        if (type == 2) {
            if (VoIPService.getSharedInstance() != null) {
                long did = VoIPService.getSharedInstance().getSelfId();
                int a2 = 0;
                int N = this.chats.size();
                while (true) {
                    if (a2 >= N) {
                        break;
                    }
                    TLRPC.Peer p = this.chats.get(a2);
                    if (MessageObject.getPeerId(p) != did) {
                        a2++;
                    } else {
                        this.currentPeer = p;
                        this.selectedPeer = p;
                        break;
                    }
                }
            } else if (scheduledPeer == null) {
                this.selectedPeer = this.chats.get(0);
            } else {
                long did2 = MessageObject.getPeerId(scheduledPeer);
                int a3 = 0;
                int N2 = this.chats.size();
                while (true) {
                    if (a3 >= N2) {
                        break;
                    }
                    TLRPC.Peer p2 = this.chats.get(a3);
                    if (MessageObject.getPeerId(p2) != did2) {
                        a3++;
                    } else {
                        this.currentPeer = p2;
                        this.selectedPeer = p2;
                        break;
                    }
                }
            }
            Drawable drawable = this.shadowDrawable;
            int color = Theme.getColor(Theme.key_voipgroup_inviteMembersBackground);
            backgroundColor = color;
            drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        } else {
            int color2 = Theme.getColor(Theme.key_dialogBackground);
            backgroundColor = color2;
            mutate.setColorFilter(new PorterDuffColorFilter(color2, PorterDuff.Mode.MULTIPLY));
            this.selectedPeer = this.chats.get(0);
        }
        fixNavigationBar(backgroundColor);
        if (this.currentType == 0) {
            LinearLayout linearLayout = new LinearLayout(context) { // from class: org.telegram.ui.Components.JoinCallAlert.1
                boolean sorted;

                @Override // android.widget.LinearLayout, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int idx;
                    if (JoinCallAlert.this.currentType == 0) {
                        int width = View.MeasureSpec.getSize(widthMeasureSpec);
                        int totalWidth = JoinCallAlert.this.chats.size() * AndroidUtilities.dp(95.0f);
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) JoinCallAlert.this.listView.getLayoutParams();
                        if (totalWidth > width) {
                            layoutParams.width = -1;
                            layoutParams.gravity = 51;
                            if (!this.sorted) {
                                if (JoinCallAlert.this.selectedPeer != null) {
                                    JoinCallAlert.this.chats.remove(JoinCallAlert.this.selectedPeer);
                                    JoinCallAlert.this.chats.add(0, JoinCallAlert.this.selectedPeer);
                                }
                                this.sorted = true;
                            }
                        } else {
                            layoutParams.width = -2;
                            layoutParams.gravity = 49;
                            if (!this.sorted) {
                                if (JoinCallAlert.this.selectedPeer != null) {
                                    if (JoinCallAlert.this.chats.size() % 2 != 0) {
                                        idx = JoinCallAlert.this.chats.size() / 2;
                                    } else {
                                        idx = Math.max(0, (JoinCallAlert.this.chats.size() / 2) - 1);
                                    }
                                    JoinCallAlert.this.chats.remove(JoinCallAlert.this.selectedPeer);
                                    JoinCallAlert.this.chats.add(idx, JoinCallAlert.this.selectedPeer);
                                }
                                this.sorted = true;
                            }
                        }
                    }
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }
            };
            linearLayout.setOrientation(1);
            NestedScrollView scrollView = new NestedScrollView(context);
            internalLayout = linearLayout;
            scrollView.addView(linearLayout);
            setCustomView(scrollView);
        } else {
            this.containerView = new FrameLayout(context) { // from class: org.telegram.ui.Components.JoinCallAlert.2
                @Override // android.view.ViewGroup
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    if (ev.getAction() == 0 && JoinCallAlert.this.scrollOffsetY != 0 && ev.getY() < JoinCallAlert.this.scrollOffsetY) {
                        JoinCallAlert.this.dismiss();
                        return true;
                    }
                    return super.onInterceptTouchEvent(ev);
                }

                @Override // android.view.View
                public boolean onTouchEvent(MotionEvent e) {
                    return !JoinCallAlert.this.isDismissed() && super.onTouchEvent(e);
                }

                @Override // android.widget.FrameLayout, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int padding;
                    int height = View.MeasureSpec.getSize(heightMeasureSpec);
                    if (Build.VERSION.SDK_INT >= 21) {
                        height -= AndroidUtilities.statusBarHeight;
                    }
                    measureChildWithMargins(JoinCallAlert.this.messageTextView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    int h = JoinCallAlert.this.messageTextView.getMeasuredHeight();
                    ((FrameLayout.LayoutParams) JoinCallAlert.this.listView.getLayoutParams()).topMargin = AndroidUtilities.dp(65.0f) + h;
                    getMeasuredWidth();
                    int contentSize = AndroidUtilities.dp(80.0f) + (JoinCallAlert.this.chats.size() * AndroidUtilities.dp(58.0f)) + JoinCallAlert.this.backgroundPaddingTop + AndroidUtilities.dp(55.0f) + h;
                    if (contentSize < (height / 5) * 3) {
                        padding = height - contentSize;
                    } else {
                        padding = (height / 5) * 2;
                    }
                    if (JoinCallAlert.this.listView.getPaddingTop() != padding) {
                        JoinCallAlert.this.ignoreLayout = true;
                        JoinCallAlert.this.listView.setPadding(0, padding, 0, 0);
                        JoinCallAlert.this.ignoreLayout = false;
                    }
                    super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
                }

                @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
                protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    super.onLayout(changed, left, top, right, bottom);
                    JoinCallAlert.this.updateLayout();
                }

                @Override // android.view.View, android.view.ViewParent
                public void requestLayout() {
                    if (JoinCallAlert.this.ignoreLayout) {
                        return;
                    }
                    super.requestLayout();
                }

                @Override // android.view.View
                protected void onDraw(Canvas canvas) {
                    JoinCallAlert.this.shadowDrawable.setBounds(0, JoinCallAlert.this.scrollOffsetY - JoinCallAlert.this.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                    JoinCallAlert.this.shadowDrawable.draw(canvas);
                }
            };
            internalLayout = this.containerView;
            this.containerView.setWillNotDraw(false);
            this.containerView.setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
        }
        final TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-dialogId));
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.JoinCallAlert.3
            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (JoinCallAlert.this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), this.currentType == 0 ? 0 : 1, false));
        this.listView.setAdapter(new ListAdapter(context));
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setClipToPadding(false);
        this.listView.setEnabled(true);
        this.listView.setSelectorDrawableColor(0);
        this.listView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.JoinCallAlert.4
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                JoinCallAlert.this.updateLayout();
            }
        });
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.JoinCallAlert$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                JoinCallAlert.this.m2722lambda$new$6$orgtelegramuiComponentsJoinCallAlert(chat, view, i);
            }
        });
        if (type != 0) {
            internalLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 100.0f, 0.0f, 80.0f));
        } else {
            this.listView.setSelectorDrawableColor(0);
            this.listView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        }
        if (type == 0) {
            RLottieImageView imageView = new RLottieImageView(context);
            imageView.setAutoRepeat(true);
            imageView.setAnimation(R.raw.utyan_schedule, 120, 120);
            imageView.playAnimation();
            internalLayout.addView(imageView, LayoutHelper.createLinear(160, 160, 49, 17, 8, 17, 0));
        }
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.textView.setTextSize(1, 20.0f);
        if (type == 2) {
            this.textView.setTextColor(Theme.getColor(Theme.key_voipgroup_nameText));
        } else {
            this.textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        }
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        if (type == 0) {
            if (ChatObject.isChannelOrGiga(chat)) {
                this.textView.setText(LocaleController.getString("StartVoipChannelTitle", R.string.StartVoipChannelTitle));
            } else {
                this.textView.setText(LocaleController.getString("StartVoipChatTitle", R.string.StartVoipChatTitle));
            }
            internalLayout.addView(this.textView, LayoutHelper.createLinear(-2, -2, 49, 23, 16, 23, 0));
        } else {
            if (type == 2) {
                this.textView.setText(LocaleController.getString("VoipGroupDisplayAs", R.string.VoipGroupDisplayAs));
            } else if (ChatObject.isChannelOrGiga(chat)) {
                this.textView.setText(LocaleController.getString("VoipChannelJoinAs", R.string.VoipChannelJoinAs));
            } else {
                this.textView.setText(LocaleController.getString("VoipGroupJoinAs", R.string.VoipGroupJoinAs));
            }
            internalLayout.addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 51, 23.0f, 8.0f, 23.0f, 0.0f));
        }
        TextView textView2 = new TextView(getContext());
        this.messageTextView = textView2;
        if (type == 2) {
            textView2.setTextColor(Theme.getColor(Theme.key_voipgroup_lastSeenText));
        } else {
            textView2.setTextColor(Theme.getColor(Theme.key_dialogTextGray3));
        }
        this.messageTextView.setTextSize(1, 14.0f);
        boolean hasGroup = false;
        int a4 = 0;
        int N3 = this.chats.size();
        while (a4 < N3) {
            boolean hasGroup2 = hasGroup;
            long peerId = MessageObject.getPeerId(this.chats.get(a4));
            if (peerId >= 0) {
                a = a4;
            } else {
                a = a4;
                TLRPC.Chat peerChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-peerId));
                if (!ChatObject.isChannel(peerChat) || peerChat.megagroup) {
                    hasGroup = true;
                    break;
                }
            }
            a4 = a + 1;
            hasGroup = hasGroup2;
        }
        this.messageTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        this.messageTextView.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink));
        if (type == 0) {
            StringBuilder builder = new StringBuilder();
            if (ChatObject.isChannel(chat) && !chat.megagroup) {
                builder.append(LocaleController.getString("VoipChannelStart2", R.string.VoipChannelStart2));
            } else {
                builder.append(LocaleController.getString("VoipGroupStart2", R.string.VoipGroupStart2));
            }
            if (this.chats.size() > 1) {
                builder.append("\n\n");
                builder.append(LocaleController.getString("VoipChatDisplayedAs", R.string.VoipChatDisplayedAs));
            } else {
                this.listView.setVisibility(8);
            }
            this.messageTextView.setText(builder);
            this.messageTextView.setGravity(49);
            internalLayout.addView(this.messageTextView, LayoutHelper.createLinear(-2, -2, 49, 23, 0, 23, 5));
        } else {
            if (hasGroup) {
                this.messageTextView.setText(LocaleController.getString("VoipGroupStartAsInfoGroup", R.string.VoipGroupStartAsInfoGroup));
            } else {
                this.messageTextView.setText(LocaleController.getString("VoipGroupStartAsInfo", R.string.VoipGroupStartAsInfo));
            }
            this.messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            internalLayout.addView(this.messageTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 23.0f, 0.0f, 23.0f, 5.0f));
        }
        if (type == 0) {
            internalLayout.addView(this.listView, LayoutHelper.createLinear(this.chats.size() < 5 ? -2 : -1, 95, 49, 0, 6, 0, 0));
        }
        BottomSheetCell bottomSheetCell = new BottomSheetCell(context, false);
        this.doneButton = bottomSheetCell;
        bottomSheetCell.background.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.JoinCallAlert$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                JoinCallAlert.this.m2723lambda$new$7$orgtelegramuiComponentsJoinCallAlert(delegate, view);
            }
        });
        if (this.currentType == 0) {
            internalLayout.addView(this.doneButton, LayoutHelper.createLinear(-1, 50, 51, 0, 0, 0, 0));
            BottomSheetCell scheduleButton = new BottomSheetCell(context, true);
            if (ChatObject.isChannelOrGiga(chat)) {
                scheduleButton.setText(LocaleController.getString("VoipChannelScheduleVoiceChat", R.string.VoipChannelScheduleVoiceChat), false);
            } else {
                scheduleButton.setText(LocaleController.getString("VoipGroupScheduleVoiceChat", R.string.VoipGroupScheduleVoiceChat), false);
            }
            scheduleButton.background.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.JoinCallAlert$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    JoinCallAlert.this.m2724lambda$new$8$orgtelegramuiComponentsJoinCallAlert(view);
                }
            });
            internalLayout.addView(scheduleButton, LayoutHelper.createLinear(-1, 50, 51, 0, 0, 0, 0));
        } else {
            internalLayout.addView(this.doneButton, LayoutHelper.createFrame(-1, 50.0f, 83, 0.0f, 0.0f, 0.0f, 0.0f));
        }
        updateDoneButton(false, chat);
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-JoinCallAlert */
    public /* synthetic */ void m2722lambda$new$6$orgtelegramuiComponentsJoinCallAlert(TLRPC.Chat chat, View view, int position) {
        if (this.animationInProgress || this.chats.get(position) == this.selectedPeer) {
            return;
        }
        this.selectedPeer = this.chats.get(position);
        if (view instanceof GroupCreateUserCell) {
            ((GroupCreateUserCell) view).setChecked(true, true);
        } else if (view instanceof ShareDialogCell) {
            ((ShareDialogCell) view).setChecked(true, true);
            view.invalidate();
        }
        int N = this.listView.getChildCount();
        for (int a = 0; a < N; a++) {
            View child = this.listView.getChildAt(a);
            if (child != view) {
                if (view instanceof GroupCreateUserCell) {
                    ((GroupCreateUserCell) child).setChecked(false, true);
                } else if (view instanceof ShareDialogCell) {
                    ((ShareDialogCell) child).setChecked(false, true);
                }
            }
        }
        int a2 = this.currentType;
        if (a2 != 0) {
            updateDoneButton(true, chat);
        }
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-JoinCallAlert */
    public /* synthetic */ void m2723lambda$new$7$orgtelegramuiComponentsJoinCallAlert(JoinCallAlertDelegate delegate, View v) {
        TLRPC.InputPeer peer = MessagesController.getInstance(this.currentAccount).getInputPeer(MessageObject.getPeerId(this.selectedPeer));
        if (this.currentType == 2) {
            if (this.selectedPeer != this.currentPeer) {
                boolean z = true;
                if (this.chats.size() <= 1) {
                    z = false;
                }
                delegate.didSelectChat(peer, z, false);
            }
        } else {
            this.selectAfterDismiss = peer;
        }
        dismiss();
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-JoinCallAlert */
    public /* synthetic */ void m2724lambda$new$8$orgtelegramuiComponentsJoinCallAlert(View v) {
        this.selectAfterDismiss = MessagesController.getInstance(this.currentAccount).getInputPeer(MessageObject.getPeerId(this.selectedPeer));
        this.schedule = true;
        dismiss();
    }

    private void updateDoneButton(boolean animated, TLRPC.Chat chat) {
        if (this.currentType == 0) {
            if (ChatObject.isChannelOrGiga(chat)) {
                this.doneButton.setText(LocaleController.formatString("VoipChannelStartVoiceChat", R.string.VoipChannelStartVoiceChat, new Object[0]), animated);
                return;
            } else {
                this.doneButton.setText(LocaleController.formatString("VoipGroupStartVoiceChat", R.string.VoipGroupStartVoiceChat, new Object[0]), animated);
                return;
            }
        }
        long did = MessageObject.getPeerId(this.selectedPeer);
        if (DialogObject.isUserDialog(did)) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(did));
            this.doneButton.setText(LocaleController.formatString("VoipGroupContinueAs", R.string.VoipGroupContinueAs, UserObject.getFirstName(user)), animated);
            return;
        }
        TLRPC.Chat peerChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-did));
        BottomSheetCell bottomSheetCell = this.doneButton;
        Object[] objArr = new Object[1];
        objArr[0] = peerChat != null ? peerChat.title : "";
        bottomSheetCell.setText(LocaleController.formatString("VoipGroupContinueAs", R.string.VoipGroupContinueAs, objArr), animated);
    }

    public void updateLayout() {
        if (this.currentType == 0) {
            return;
        }
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        int newOffset = 0;
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop() - AndroidUtilities.dp(9.0f);
        if (top > 0 && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        if (this.scrollOffsetY != newOffset) {
            this.textView.setTranslationY(AndroidUtilities.dp(19.0f) + top);
            this.messageTextView.setTranslationY(AndroidUtilities.dp(56.0f) + top);
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = newOffset;
            recyclerListView2.setTopGlowOffset(newOffset);
            this.containerView.invalidate();
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void dismissInternal() {
        super.dismissInternal();
        TLRPC.InputPeer inputPeer = this.selectAfterDismiss;
        if (inputPeer != null) {
            JoinCallAlertDelegate joinCallAlertDelegate = this.delegate;
            boolean z = true;
            if (this.chats.size() <= 1) {
                z = false;
            }
            joinCallAlertDelegate.didSelectChat(inputPeer, z, this.schedule);
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;

        public ListAdapter(Context context) {
            JoinCallAlert.this = r1;
            this.context = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return JoinCallAlert.this.chats.size();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            return 0;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (JoinCallAlert.this.currentType == 0) {
                view = new ShareDialogCell(this.context, 2, null);
                view.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(80.0f), AndroidUtilities.dp(100.0f)));
            } else {
                view = new GroupCreateUserCell(this.context, 2, 0, false, JoinCallAlert.this.currentType == 2);
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            holder.getAdapterPosition();
            long did = MessageObject.getPeerId(JoinCallAlert.this.selectedPeer);
            boolean z = true;
            if (holder.itemView instanceof GroupCreateUserCell) {
                GroupCreateUserCell cell = (GroupCreateUserCell) holder.itemView;
                Object object = cell.getObject();
                long id = 0;
                if (object != null) {
                    if (object instanceof TLRPC.Chat) {
                        id = -((TLRPC.Chat) object).id;
                    } else {
                        id = ((TLRPC.User) object).id;
                    }
                }
                if (did != id) {
                    z = false;
                }
                cell.setChecked(z, false);
                return;
            }
            ShareDialogCell cell2 = (ShareDialogCell) holder.itemView;
            long id2 = cell2.getCurrentDialog();
            if (did != id2) {
                z = false;
            }
            cell2.setChecked(z, false);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String status;
            TLObject object;
            long did = MessageObject.getPeerId((TLRPC.Peer) JoinCallAlert.this.chats.get(position));
            if (did > 0) {
                object = MessagesController.getInstance(JoinCallAlert.this.currentAccount).getUser(Long.valueOf(did));
                status = LocaleController.getString("VoipGroupPersonalAccount", R.string.VoipGroupPersonalAccount);
            } else {
                object = MessagesController.getInstance(JoinCallAlert.this.currentAccount).getChat(Long.valueOf(-did));
                status = null;
            }
            boolean z = false;
            if (JoinCallAlert.this.currentType == 0) {
                ShareDialogCell cell = (ShareDialogCell) holder.itemView;
                if (did == MessageObject.getPeerId(JoinCallAlert.this.selectedPeer)) {
                    z = true;
                }
                cell.setDialog(did, z, null);
                return;
            }
            GroupCreateUserCell cell2 = (GroupCreateUserCell) holder.itemView;
            if (position != getItemCount() - 1) {
                z = true;
            }
            cell2.setObject(object, null, status, z);
        }
    }
}
