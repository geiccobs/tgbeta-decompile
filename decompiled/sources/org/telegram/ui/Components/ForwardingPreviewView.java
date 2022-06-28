package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.style.CharacterStyle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import androidx.recyclerview.widget.ChatListItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManagerFixed;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ForwardingMessagesParams;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ForwardingPreviewView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PinchToZoomHelper;
/* loaded from: classes5.dex */
public class ForwardingPreviewView extends FrameLayout {
    ActionBar actionBar;
    Adapter adapter;
    LinearLayout buttonsLayout;
    LinearLayout buttonsLayout2;
    ActionBarMenuSubItem changeRecipientView;
    GridLayoutManagerFixed chatLayoutManager;
    RecyclerListView chatListView;
    SizeNotifierFrameLayout chatPreviewContainer;
    int chatTopOffset;
    private final int currentAccount;
    TLRPC.Chat currentChat;
    int currentTopOffset;
    TLRPC.User currentUser;
    float currentYOffset;
    ForwardingMessagesParams forwardingMessagesParams;
    ActionBarMenuSubItem hideCaptionView;
    ActionBarMenuSubItem hideSendersNameView;
    boolean isLandscapeMode;
    ChatListItemAnimator itemAnimator;
    int lastSize;
    LinearLayout menuContainer;
    ScrollView menuScrollView;
    ValueAnimator offsetsAnimator;
    private final ResourcesDelegate resourcesProvider;
    boolean returnSendersNames;
    TLRPC.Peer sendAsPeer;
    ActionBarMenuSubItem sendMessagesView;
    ActionBarMenuSubItem showCaptionView;
    ActionBarMenuSubItem showSendersNameView;
    boolean showing;
    boolean updateAfterAnimations;
    float yOffset;
    ArrayList<ActionBarMenuSubItem> actionItems = new ArrayList<>();
    android.graphics.Rect rect = new android.graphics.Rect();
    private boolean firstLayout = true;
    Runnable changeBoundsRunnable = new Runnable() { // from class: org.telegram.ui.Components.ForwardingPreviewView.1
        @Override // java.lang.Runnable
        public void run() {
            if (ForwardingPreviewView.this.offsetsAnimator != null && !ForwardingPreviewView.this.offsetsAnimator.isRunning()) {
                ForwardingPreviewView.this.offsetsAnimator.start();
            }
        }
    };
    private final ArrayList<MessageObject.GroupedMessages> drawingGroups = new ArrayList<>(10);

    /* loaded from: classes5.dex */
    public interface ResourcesDelegate extends Theme.ResourcesProvider {
        Drawable getWallpaperDrawable();

        boolean isWallpaperMotion();
    }

    public void setSendAsPeer(TLRPC.Peer defPeer) {
        this.sendAsPeer = defPeer;
        updateMessages();
    }

    public ForwardingPreviewView(Context context, final ForwardingMessagesParams params, TLRPC.User user, TLRPC.Chat chat, int currentAccount, final ResourcesDelegate resourcesProvider) {
        super(context);
        String str;
        int i;
        String str2;
        int i2;
        this.currentAccount = currentAccount;
        this.currentUser = user;
        this.currentChat = chat;
        this.forwardingMessagesParams = params;
        this.resourcesProvider = resourcesProvider;
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context) { // from class: org.telegram.ui.Components.ForwardingPreviewView.2
            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout
            public Drawable getNewDrawable() {
                Drawable drawable = resourcesProvider.getWallpaperDrawable();
                return drawable != null ? drawable : super.getNewDrawable();
            }

            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent ev) {
                if (ev.getY() < ForwardingPreviewView.this.currentTopOffset) {
                    return false;
                }
                return super.dispatchTouchEvent(ev);
            }
        };
        this.chatPreviewContainer = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setBackgroundImage(resourcesProvider.getWallpaperDrawable(), resourcesProvider.isWallpaperMotion());
        this.chatPreviewContainer.setOccupyStatusBar(false);
        if (Build.VERSION.SDK_INT >= 21) {
            this.chatPreviewContainer.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.ForwardingPreviewView.3
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, ForwardingPreviewView.this.currentTopOffset + 1, view.getMeasuredWidth(), view.getMeasuredHeight(), AndroidUtilities.dp(6.0f));
                }
            });
            this.chatPreviewContainer.setClipToOutline(true);
            this.chatPreviewContainer.setElevation(AndroidUtilities.dp(4.0f));
        }
        ActionBar actionBar = new ActionBar(context, resourcesProvider);
        this.actionBar = actionBar;
        actionBar.setBackgroundColor(getThemedColor(Theme.key_actionBarDefault));
        this.actionBar.setOccupyStatusBar(false);
        RecyclerListView recyclerListView = new RecyclerListView(context, resourcesProvider) { // from class: org.telegram.ui.Components.ForwardingPreviewView.4
            @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (child instanceof ChatMessageCell) {
                    ChatMessageCell cell = (ChatMessageCell) child;
                    boolean r = super.drawChild(canvas, child, drawingTime);
                    cell.drawCheckBox(canvas);
                    canvas.save();
                    canvas.translate(cell.getX(), cell.getY());
                    cell.drawMessageText(canvas, cell.getMessageObject().textLayoutBlocks, true, 1.0f, false);
                    if (cell.getCurrentMessagesGroup() != null || cell.getTransitionParams().animateBackgroundBoundsInner) {
                        cell.drawNamesLayout(canvas, 1.0f);
                    }
                    if ((cell.getCurrentPosition() != null && cell.getCurrentPosition().last) || cell.getTransitionParams().animateBackgroundBoundsInner) {
                        cell.drawTime(canvas, 1.0f, true);
                    }
                    if (cell.getCurrentPosition() == null || cell.getCurrentPosition().last || cell.getCurrentMessagesGroup().isDocuments) {
                        cell.drawCaptionLayout(canvas, false, 1.0f);
                    }
                    cell.getTransitionParams().recordDrawingStatePreview();
                    canvas.restore();
                    return r;
                }
                return true;
            }

            @Override // org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                for (int i3 = 0; i3 < getChildCount(); i3++) {
                    View child = getChildAt(i3);
                    if (child instanceof ChatMessageCell) {
                        ChatMessageCell cell = (ChatMessageCell) child;
                        cell.setParentViewSize(ForwardingPreviewView.this.chatPreviewContainer.getMeasuredWidth(), ForwardingPreviewView.this.chatPreviewContainer.getBackgroundSizeY());
                    }
                }
                drawChatBackgroundElements(canvas);
                super.dispatchDraw(canvas);
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                ForwardingPreviewView.this.updatePositions();
            }

            private void drawChatBackgroundElements(Canvas canvas) {
                boolean z;
                int k;
                MessageObject.GroupedMessages lastDrawnGroup;
                MessageObject.GroupedMessages scrimGroup;
                MessageObject.GroupedMessages group;
                ChatMessageCell cell;
                MessageObject.GroupedMessages group2;
                int count = getChildCount();
                MessageObject.GroupedMessages lastDrawnGroup2 = null;
                for (int a = 0; a < count; a++) {
                    View child = getChildAt(a);
                    if ((child instanceof ChatMessageCell) && ((group2 = (cell = (ChatMessageCell) child).getCurrentMessagesGroup()) == null || group2 != lastDrawnGroup2)) {
                        lastDrawnGroup2 = group2;
                        cell.getCurrentPosition();
                        cell.getBackgroundDrawable();
                    }
                }
                MessageObject.GroupedMessages scrimGroup2 = null;
                int k2 = 0;
                while (k2 < 3) {
                    ForwardingPreviewView.this.drawingGroups.clear();
                    if (k2 != 2 || ForwardingPreviewView.this.chatListView.isFastScrollAnimationRunning()) {
                        int i3 = 0;
                        while (true) {
                            z = true;
                            if (i3 >= count) {
                                break;
                            }
                            View child2 = ForwardingPreviewView.this.chatListView.getChildAt(i3);
                            if (child2 instanceof ChatMessageCell) {
                                ChatMessageCell cell2 = (ChatMessageCell) child2;
                                if (child2.getY() <= ForwardingPreviewView.this.chatListView.getHeight() && child2.getY() + child2.getHeight() >= 0.0f && (group = cell2.getCurrentMessagesGroup()) != null && ((k2 != 0 || group.messages.size() != 1) && ((k2 != 1 || group.transitionParams.drawBackgroundForDeletedItems) && ((k2 != 0 || !cell2.getMessageObject().deleted) && ((k2 != 1 || cell2.getMessageObject().deleted) && ((k2 != 2 || cell2.willRemovedAfterAnimation()) && (k2 == 2 || !cell2.willRemovedAfterAnimation()))))))) {
                                    if (!ForwardingPreviewView.this.drawingGroups.contains(group)) {
                                        group.transitionParams.left = 0;
                                        group.transitionParams.top = 0;
                                        group.transitionParams.right = 0;
                                        group.transitionParams.bottom = 0;
                                        group.transitionParams.pinnedBotton = false;
                                        group.transitionParams.pinnedTop = false;
                                        group.transitionParams.cell = cell2;
                                        ForwardingPreviewView.this.drawingGroups.add(group);
                                    }
                                    group.transitionParams.pinnedTop = cell2.isPinnedTop();
                                    group.transitionParams.pinnedBotton = cell2.isPinnedBottom();
                                    int left = cell2.getLeft() + cell2.getBackgroundDrawableLeft();
                                    int right = cell2.getLeft() + cell2.getBackgroundDrawableRight();
                                    int top = cell2.getTop() + cell2.getBackgroundDrawableTop();
                                    int bottom = cell2.getTop() + cell2.getBackgroundDrawableBottom();
                                    if ((cell2.getCurrentPosition().flags & 4) == 0) {
                                        top -= AndroidUtilities.dp(10.0f);
                                    }
                                    if ((cell2.getCurrentPosition().flags & 8) == 0) {
                                        bottom += AndroidUtilities.dp(10.0f);
                                    }
                                    if (cell2.willRemovedAfterAnimation()) {
                                        group.transitionParams.cell = cell2;
                                    }
                                    if (group.transitionParams.top == 0 || top < group.transitionParams.top) {
                                        group.transitionParams.top = top;
                                    }
                                    if (group.transitionParams.bottom == 0 || bottom > group.transitionParams.bottom) {
                                        group.transitionParams.bottom = bottom;
                                    }
                                    if (group.transitionParams.left == 0 || left < group.transitionParams.left) {
                                        group.transitionParams.left = left;
                                    }
                                    if (group.transitionParams.right == 0 || right > group.transitionParams.right) {
                                        group.transitionParams.right = right;
                                    }
                                }
                            }
                            i3++;
                        }
                        int i4 = 0;
                        while (i4 < ForwardingPreviewView.this.drawingGroups.size()) {
                            MessageObject.GroupedMessages group3 = (MessageObject.GroupedMessages) ForwardingPreviewView.this.drawingGroups.get(i4);
                            if (group3 == scrimGroup2) {
                                lastDrawnGroup = lastDrawnGroup2;
                                scrimGroup = scrimGroup2;
                                k = k2;
                            } else {
                                float x = group3.transitionParams.cell.getNonAnimationTranslationX(z);
                                float l = group3.transitionParams.left + x + group3.transitionParams.offsetLeft;
                                float t = group3.transitionParams.top + group3.transitionParams.offsetTop;
                                float r = group3.transitionParams.right + x + group3.transitionParams.offsetRight;
                                float b = group3.transitionParams.bottom + group3.transitionParams.offsetBottom;
                                if (!group3.transitionParams.backgroundChangeBounds) {
                                    t += group3.transitionParams.cell.getTranslationY();
                                    b += group3.transitionParams.cell.getTranslationY();
                                }
                                if (t < (-AndroidUtilities.dp(20.0f))) {
                                    t = -AndroidUtilities.dp(20.0f);
                                }
                                if (b > ForwardingPreviewView.this.chatListView.getMeasuredHeight() + AndroidUtilities.dp(20.0f)) {
                                    b = ForwardingPreviewView.this.chatListView.getMeasuredHeight() + AndroidUtilities.dp(20.0f);
                                }
                                boolean useScale = (group3.transitionParams.cell.getScaleX() == 1.0f && group3.transitionParams.cell.getScaleY() == 1.0f) ? false : true;
                                if (!useScale) {
                                    lastDrawnGroup = lastDrawnGroup2;
                                    scrimGroup = scrimGroup2;
                                } else {
                                    canvas.save();
                                    lastDrawnGroup = lastDrawnGroup2;
                                    scrimGroup = scrimGroup2;
                                    canvas.scale(group3.transitionParams.cell.getScaleX(), group3.transitionParams.cell.getScaleY(), l + ((r - l) / 2.0f), t + ((b - t) / 2.0f));
                                }
                                k = k2;
                                group3.transitionParams.cell.drawBackground(canvas, (int) l, (int) t, (int) r, (int) b, group3.transitionParams.pinnedTop, group3.transitionParams.pinnedBotton, false, 0);
                                group3.transitionParams.cell = null;
                                group3.transitionParams.drawCaptionLayout = group3.hasCaption;
                                if (useScale) {
                                    canvas.restore();
                                    for (int ii = 0; ii < count; ii++) {
                                        View child3 = ForwardingPreviewView.this.chatListView.getChildAt(ii);
                                        if ((child3 instanceof ChatMessageCell) && ((ChatMessageCell) child3).getCurrentMessagesGroup() == group3) {
                                            ChatMessageCell cell3 = (ChatMessageCell) child3;
                                            int left2 = cell3.getLeft();
                                            int top2 = cell3.getTop();
                                            child3.setPivotX((l - left2) + ((r - l) / 2.0f));
                                            child3.setPivotY((t - top2) + ((b - t) / 2.0f));
                                        }
                                    }
                                }
                            }
                            i4++;
                            scrimGroup2 = scrimGroup;
                            lastDrawnGroup2 = lastDrawnGroup;
                            k2 = k;
                            z = true;
                        }
                    }
                    k2++;
                    scrimGroup2 = scrimGroup2;
                    lastDrawnGroup2 = lastDrawnGroup2;
                }
            }
        };
        this.chatListView = recyclerListView;
        AnonymousClass5 anonymousClass5 = new AnonymousClass5(null, this.chatListView, resourcesProvider, currentAccount);
        this.itemAnimator = anonymousClass5;
        recyclerListView.setItemAnimator(anonymousClass5);
        this.chatListView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.ForwardingPreviewView.6
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                for (int i3 = 0; i3 < ForwardingPreviewView.this.chatListView.getChildCount(); i3++) {
                    ChatMessageCell cell = (ChatMessageCell) ForwardingPreviewView.this.chatListView.getChildAt(i3);
                    cell.setParentViewSize(ForwardingPreviewView.this.chatPreviewContainer.getMeasuredWidth(), ForwardingPreviewView.this.chatPreviewContainer.getBackgroundSizeY());
                }
            }
        });
        this.chatListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ForwardingPreviewView.7
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public void onItemClick(View view, int position) {
                if (ForwardingPreviewView.this.forwardingMessagesParams.previewMessages.size() <= 1) {
                    return;
                }
                int id = params.previewMessages.get(position).getId();
                boolean newSelected = !params.selectedIds.get(id, false);
                if (ForwardingPreviewView.this.forwardingMessagesParams.selectedIds.size() == 1 && !newSelected) {
                    return;
                }
                if (!newSelected) {
                    params.selectedIds.delete(id);
                } else {
                    params.selectedIds.put(id, newSelected);
                }
                ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                chatMessageCell.setChecked(newSelected, newSelected, true);
                ForwardingPreviewView.this.actionBar.setTitle(LocaleController.formatPluralString("PreviewForwardMessagesCount", params.selectedIds.size(), new Object[0]));
            }
        });
        RecyclerListView recyclerListView2 = this.chatListView;
        Adapter adapter = new Adapter();
        this.adapter = adapter;
        recyclerListView2.setAdapter(adapter);
        this.chatListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        AnonymousClass8 anonymousClass8 = new AnonymousClass8(context, 1000, 1, true, params);
        this.chatLayoutManager = anonymousClass8;
        anonymousClass8.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.ForwardingPreviewView.9
            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int position) {
                if (position >= 0 && position < params.previewMessages.size()) {
                    MessageObject message = params.previewMessages.get(position);
                    MessageObject.GroupedMessages groupedMessages = ForwardingPreviewView.this.getValidGroupedMessage(message);
                    if (groupedMessages != null) {
                        return groupedMessages.positions.get(message).spanSize;
                    }
                    return 1000;
                }
                return 1000;
            }
        });
        this.chatListView.setClipToPadding(false);
        this.chatListView.setLayoutManager(this.chatLayoutManager);
        this.chatListView.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: org.telegram.ui.Components.ForwardingPreviewView.10
            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(android.graphics.Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                ChatMessageCell cell;
                MessageObject.GroupedMessages group;
                MessageObject.GroupedMessagePosition position;
                outRect.bottom = 0;
                if ((view instanceof ChatMessageCell) && (group = (cell = (ChatMessageCell) view).getCurrentMessagesGroup()) != null && (position = cell.getCurrentPosition()) != null && position.siblingHeights != null) {
                    float maxHeight = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.5f;
                    int h = cell.getExtraInsetHeight();
                    for (int a = 0; a < position.siblingHeights.length; a++) {
                        h += (int) Math.ceil(position.siblingHeights[a] * maxHeight);
                    }
                    int a2 = position.maxY;
                    int h2 = h + ((a2 - position.minY) * Math.round(AndroidUtilities.density * 7.0f));
                    int count = group.posArray.size();
                    int a3 = 0;
                    while (true) {
                        if (a3 >= count) {
                            break;
                        }
                        MessageObject.GroupedMessagePosition pos = group.posArray.get(a3);
                        if (pos.minY != position.minY || ((pos.minX == position.minX && pos.maxX == position.maxX && pos.minY == position.minY && pos.maxY == position.maxY) || pos.minY != position.minY)) {
                            a3++;
                        } else {
                            h2 -= ((int) Math.ceil(pos.ph * maxHeight)) - AndroidUtilities.dp(4.0f);
                            break;
                        }
                    }
                    int a4 = -h2;
                    outRect.bottom = a4;
                }
            }
        });
        this.chatPreviewContainer.addView(this.chatListView);
        addView(this.chatPreviewContainer, LayoutHelper.createFrame(-1, 400.0f, 0, 8.0f, 0.0f, 8.0f, 0.0f));
        this.chatPreviewContainer.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        ScrollView scrollView = new ScrollView(context);
        this.menuScrollView = scrollView;
        addView(scrollView, LayoutHelper.createFrame(-2, -2.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.menuContainer = linearLayout;
        linearLayout.setOrientation(1);
        this.menuScrollView.addView(this.menuContainer);
        LinearLayout linearLayout2 = new LinearLayout(context);
        this.buttonsLayout = linearLayout2;
        linearLayout2.setOrientation(1);
        Drawable shadowDrawable = getContext().getResources().getDrawable(R.drawable.popup_fixed_alert).mutate();
        shadowDrawable.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
        this.buttonsLayout.setBackground(shadowDrawable);
        this.menuContainer.addView(this.buttonsLayout, LayoutHelper.createFrame(-1, -2.0f));
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(context, true, true, false, resourcesProvider);
        this.showSendersNameView = actionBarMenuSubItem;
        this.buttonsLayout.addView(actionBarMenuSubItem, LayoutHelper.createFrame(-1, 48.0f));
        ActionBarMenuSubItem actionBarMenuSubItem2 = this.showSendersNameView;
        if (this.forwardingMessagesParams.multiplyUsers) {
            i = R.string.ShowSenderNames;
            str = "ShowSenderNames";
        } else {
            i = R.string.ShowSendersName;
            str = "ShowSendersName";
        }
        actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString(str, i), 0);
        this.showSendersNameView.setChecked(true);
        ActionBarMenuSubItem actionBarMenuSubItem3 = new ActionBarMenuSubItem(context, true, false, !params.hasCaption, resourcesProvider);
        this.hideSendersNameView = actionBarMenuSubItem3;
        this.buttonsLayout.addView(actionBarMenuSubItem3, LayoutHelper.createFrame(-1, 48.0f));
        ActionBarMenuSubItem actionBarMenuSubItem4 = this.hideSendersNameView;
        if (this.forwardingMessagesParams.multiplyUsers) {
            i2 = R.string.HideSenderNames;
            str2 = "HideSenderNames";
        } else {
            i2 = R.string.HideSendersName;
            str2 = "HideSendersName";
        }
        actionBarMenuSubItem4.setTextAndIcon(LocaleController.getString(str2, i2), 0);
        this.hideSendersNameView.setChecked(false);
        if (this.forwardingMessagesParams.hasCaption) {
            View dividerView = new View(context) { // from class: org.telegram.ui.Components.ForwardingPreviewView.11
                @Override // android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(2, C.BUFFER_FLAG_ENCRYPTED));
                }
            };
            dividerView.setBackgroundColor(getThemedColor(Theme.key_divider));
            this.buttonsLayout.addView(dividerView, LayoutHelper.createFrame(-1, -2.0f));
            ActionBarMenuSubItem actionBarMenuSubItem5 = new ActionBarMenuSubItem(context, true, false, false, resourcesProvider);
            this.showCaptionView = actionBarMenuSubItem5;
            this.buttonsLayout.addView(actionBarMenuSubItem5, LayoutHelper.createFrame(-1, 48.0f));
            this.showCaptionView.setTextAndIcon(LocaleController.getString("ShowCaption", R.string.ShowCaption), 0);
            this.showCaptionView.setChecked(true);
            ActionBarMenuSubItem actionBarMenuSubItem6 = new ActionBarMenuSubItem(context, true, false, true, resourcesProvider);
            this.hideCaptionView = actionBarMenuSubItem6;
            this.buttonsLayout.addView(actionBarMenuSubItem6, LayoutHelper.createFrame(-1, 48.0f));
            this.hideCaptionView.setTextAndIcon(LocaleController.getString("HideCaption", R.string.HideCaption), 0);
            this.hideCaptionView.setChecked(false);
        }
        LinearLayout linearLayout3 = new LinearLayout(context);
        this.buttonsLayout2 = linearLayout3;
        linearLayout3.setOrientation(1);
        Drawable shadowDrawable2 = getContext().getResources().getDrawable(R.drawable.popup_fixed_alert).mutate();
        shadowDrawable2.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
        this.buttonsLayout2.setBackground(shadowDrawable2);
        this.menuContainer.addView(this.buttonsLayout2, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, this.forwardingMessagesParams.hasSenders ? -8.0f : 0.0f, 0.0f, 0.0f));
        ActionBarMenuSubItem actionBarMenuSubItem7 = new ActionBarMenuSubItem(context, true, false, (Theme.ResourcesProvider) resourcesProvider);
        this.changeRecipientView = actionBarMenuSubItem7;
        this.buttonsLayout2.addView(actionBarMenuSubItem7, LayoutHelper.createFrame(-1, 48.0f));
        this.changeRecipientView.setTextAndIcon(LocaleController.getString("ChangeRecipient", R.string.ChangeRecipient), R.drawable.msg_forward_replace);
        ActionBarMenuSubItem actionBarMenuSubItem8 = new ActionBarMenuSubItem(context, false, true, (Theme.ResourcesProvider) resourcesProvider);
        this.sendMessagesView = actionBarMenuSubItem8;
        this.buttonsLayout2.addView(actionBarMenuSubItem8, LayoutHelper.createFrame(-1, 48.0f));
        this.sendMessagesView.setTextAndIcon(LocaleController.getString("ForwardSendMessages", R.string.ForwardSendMessages), R.drawable.msg_send);
        if (this.forwardingMessagesParams.hasSenders) {
            this.actionItems.add(this.showSendersNameView);
            this.actionItems.add(this.hideSendersNameView);
            if (params.hasCaption) {
                this.actionItems.add(this.showCaptionView);
                this.actionItems.add(this.hideCaptionView);
            }
        }
        this.actionItems.add(this.changeRecipientView);
        this.actionItems.add(this.sendMessagesView);
        this.showSendersNameView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ForwardingPreviewView$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ForwardingPreviewView.this.m2624lambda$new$0$orgtelegramuiComponentsForwardingPreviewView(params, view);
            }
        });
        this.hideSendersNameView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ForwardingPreviewView$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ForwardingPreviewView.this.m2625lambda$new$1$orgtelegramuiComponentsForwardingPreviewView(params, view);
            }
        });
        if (params.hasCaption) {
            this.showCaptionView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ForwardingPreviewView$$ExternalSyntheticLambda5
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ForwardingPreviewView.this.m2626lambda$new$2$orgtelegramuiComponentsForwardingPreviewView(params, view);
                }
            });
            this.hideCaptionView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ForwardingPreviewView$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ForwardingPreviewView.this.m2627lambda$new$3$orgtelegramuiComponentsForwardingPreviewView(params, view);
                }
            });
        }
        this.showSendersNameView.setChecked(!params.hideForwardSendersName);
        this.hideSendersNameView.setChecked(params.hideForwardSendersName);
        if (params.hasCaption) {
            this.showCaptionView.setChecked(!params.hideCaption);
            this.hideCaptionView.setChecked(params.hideCaption);
        }
        if (!params.hasSenders) {
            this.buttonsLayout.setVisibility(8);
        }
        this.sendMessagesView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ForwardingPreviewView$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ForwardingPreviewView.this.m2628lambda$new$4$orgtelegramuiComponentsForwardingPreviewView(view);
            }
        });
        this.changeRecipientView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ForwardingPreviewView$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ForwardingPreviewView.this.m2629lambda$new$5$orgtelegramuiComponentsForwardingPreviewView(view);
            }
        });
        updateMessages();
        updateSubtitle();
        this.actionBar.setTitle(LocaleController.formatPluralString("PreviewForwardMessagesCount", params.selectedIds.size(), new Object[0]));
        this.menuScrollView.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.ForwardingPreviewView$$ExternalSyntheticLambda7
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return ForwardingPreviewView.this.m2630lambda$new$6$orgtelegramuiComponentsForwardingPreviewView(view, motionEvent);
            }
        });
        setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.ForwardingPreviewView$$ExternalSyntheticLambda8
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return ForwardingPreviewView.this.m2631lambda$new$7$orgtelegramuiComponentsForwardingPreviewView(view, motionEvent);
            }
        });
        this.showing = true;
        setAlpha(0.0f);
        setScaleX(0.95f);
        setScaleY(0.95f);
        animate().alpha(1.0f).scaleX(1.0f).setDuration(250L).setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR).scaleY(1.0f);
        updateColors();
    }

    /* renamed from: org.telegram.ui.Components.ForwardingPreviewView$5 */
    /* loaded from: classes5.dex */
    public class AnonymousClass5 extends ChatListItemAnimator {
        Runnable finishRunnable;
        int scrollAnimationIndex = -1;
        final /* synthetic */ int val$currentAccount;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass5(ChatActivity activity, RecyclerListView listView, Theme.ResourcesProvider resourcesProvider, int i) {
            super(activity, listView, resourcesProvider);
            ForwardingPreviewView.this = this$0;
            this.val$currentAccount = i;
        }

        @Override // androidx.recyclerview.widget.ChatListItemAnimator
        public void onAnimationStart() {
            super.onAnimationStart();
            AndroidUtilities.cancelRunOnUIThread(ForwardingPreviewView.this.changeBoundsRunnable);
            ForwardingPreviewView.this.changeBoundsRunnable.run();
            if (this.scrollAnimationIndex == -1) {
                this.scrollAnimationIndex = NotificationCenter.getInstance(this.val$currentAccount).setAnimationInProgress(this.scrollAnimationIndex, null, false);
            }
            Runnable runnable = this.finishRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.finishRunnable = null;
            }
        }

        @Override // androidx.recyclerview.widget.ChatListItemAnimator, androidx.recyclerview.widget.DefaultItemAnimator
        public void onAllAnimationsDone() {
            super.onAllAnimationsDone();
            Runnable runnable = this.finishRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            final int i = this.val$currentAccount;
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.ForwardingPreviewView$5$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ForwardingPreviewView.AnonymousClass5.this.m2634xaa159a02(i);
                }
            };
            this.finishRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2);
            if (ForwardingPreviewView.this.updateAfterAnimations) {
                ForwardingPreviewView.this.updateAfterAnimations = false;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ForwardingPreviewView$5$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ForwardingPreviewView.AnonymousClass5.this.m2635xd7ee3461();
                    }
                });
            }
        }

        /* renamed from: lambda$onAllAnimationsDone$0$org-telegram-ui-Components-ForwardingPreviewView$5 */
        public /* synthetic */ void m2634xaa159a02(int currentAccount) {
            if (this.scrollAnimationIndex != -1) {
                NotificationCenter.getInstance(currentAccount).onAnimationFinish(this.scrollAnimationIndex);
                this.scrollAnimationIndex = -1;
            }
        }

        /* renamed from: lambda$onAllAnimationsDone$1$org-telegram-ui-Components-ForwardingPreviewView$5 */
        public /* synthetic */ void m2635xd7ee3461() {
            ForwardingPreviewView.this.updateMessages();
        }

        @Override // androidx.recyclerview.widget.ChatListItemAnimator, androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
        public void endAnimations() {
            super.endAnimations();
            Runnable runnable = this.finishRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            final int i = this.val$currentAccount;
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.ForwardingPreviewView$5$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ForwardingPreviewView.AnonymousClass5.this.m2633xc20931a9(i);
                }
            };
            this.finishRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2);
        }

        /* renamed from: lambda$endAnimations$2$org-telegram-ui-Components-ForwardingPreviewView$5 */
        public /* synthetic */ void m2633xc20931a9(int currentAccount) {
            if (this.scrollAnimationIndex != -1) {
                NotificationCenter.getInstance(currentAccount).onAnimationFinish(this.scrollAnimationIndex);
                this.scrollAnimationIndex = -1;
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ForwardingPreviewView$8 */
    /* loaded from: classes5.dex */
    public class AnonymousClass8 extends GridLayoutManagerFixed {
        final /* synthetic */ ForwardingMessagesParams val$params;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass8(Context context, int spanCount, int orientation, boolean reverseLayout, ForwardingMessagesParams forwardingMessagesParams) {
            super(context, spanCount, orientation, reverseLayout);
            ForwardingPreviewView.this = this$0;
            this.val$params = forwardingMessagesParams;
        }

        @Override // androidx.recyclerview.widget.GridLayoutManagerFixed
        public boolean shouldLayoutChildFromOpositeSide(View child) {
            return false;
        }

        @Override // androidx.recyclerview.widget.GridLayoutManagerFixed
        protected boolean hasSiblingChild(int position) {
            MessageObject message = this.val$params.previewMessages.get(position);
            MessageObject.GroupedMessages group = ForwardingPreviewView.this.getValidGroupedMessage(message);
            if (group != null) {
                MessageObject.GroupedMessagePosition pos = group.positions.get(message);
                if (pos.minX == pos.maxX || pos.minY != pos.maxY || pos.minY == 0) {
                    return false;
                }
                int count = group.posArray.size();
                for (int a = 0; a < count; a++) {
                    MessageObject.GroupedMessagePosition p = group.posArray.get(a);
                    if (p != pos && p.minY <= pos.minY && p.maxY >= pos.minY) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override // androidx.recyclerview.widget.GridLayoutManager, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            if (BuildVars.DEBUG_PRIVATE_VERSION) {
                super.onLayoutChildren(recycler, state);
                return;
            }
            try {
                super.onLayoutChildren(recycler, state);
            } catch (Exception e) {
                FileLog.e(e);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ForwardingPreviewView$8$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ForwardingPreviewView.AnonymousClass8.this.m2636xcccee436();
                    }
                });
            }
        }

        /* renamed from: lambda$onLayoutChildren$0$org-telegram-ui-Components-ForwardingPreviewView$8 */
        public /* synthetic */ void m2636xcccee436() {
            ForwardingPreviewView.this.adapter.notifyDataSetChanged();
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ForwardingPreviewView */
    public /* synthetic */ void m2624lambda$new$0$orgtelegramuiComponentsForwardingPreviewView(ForwardingMessagesParams params, View view) {
        if (params.hideForwardSendersName) {
            this.returnSendersNames = false;
            this.showSendersNameView.setChecked(true);
            this.hideSendersNameView.setChecked(false);
            ActionBarMenuSubItem actionBarMenuSubItem = this.showCaptionView;
            if (actionBarMenuSubItem != null) {
                actionBarMenuSubItem.setChecked(true);
                this.hideCaptionView.setChecked(false);
            }
            params.hideForwardSendersName = false;
            params.hideCaption = false;
            updateMessages();
            updateSubtitle();
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ForwardingPreviewView */
    public /* synthetic */ void m2625lambda$new$1$orgtelegramuiComponentsForwardingPreviewView(ForwardingMessagesParams params, View view) {
        if (!params.hideForwardSendersName) {
            this.returnSendersNames = false;
            this.showSendersNameView.setChecked(false);
            this.hideSendersNameView.setChecked(true);
            params.hideForwardSendersName = true;
            updateMessages();
            updateSubtitle();
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ForwardingPreviewView */
    public /* synthetic */ void m2626lambda$new$2$orgtelegramuiComponentsForwardingPreviewView(ForwardingMessagesParams params, View view) {
        if (params.hideCaption) {
            if (this.returnSendersNames) {
                params.hideForwardSendersName = false;
            }
            this.returnSendersNames = false;
            this.showCaptionView.setChecked(true);
            this.hideCaptionView.setChecked(false);
            this.showSendersNameView.setChecked(true ^ params.hideForwardSendersName);
            this.hideSendersNameView.setChecked(params.hideForwardSendersName);
            params.hideCaption = false;
            updateMessages();
            updateSubtitle();
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-ForwardingPreviewView */
    public /* synthetic */ void m2627lambda$new$3$orgtelegramuiComponentsForwardingPreviewView(ForwardingMessagesParams params, View view) {
        if (!params.hideCaption) {
            this.showCaptionView.setChecked(false);
            this.hideCaptionView.setChecked(true);
            this.showSendersNameView.setChecked(false);
            this.hideSendersNameView.setChecked(true);
            if (!params.hideForwardSendersName) {
                params.hideForwardSendersName = true;
                this.returnSendersNames = true;
            }
            params.hideCaption = true;
            updateMessages();
            updateSubtitle();
        }
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-ForwardingPreviewView */
    public /* synthetic */ void m2628lambda$new$4$orgtelegramuiComponentsForwardingPreviewView(View View) {
        didSendPressed();
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-ForwardingPreviewView */
    public /* synthetic */ void m2629lambda$new$5$orgtelegramuiComponentsForwardingPreviewView(View view) {
        selectAnotherChat();
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-ForwardingPreviewView */
    public /* synthetic */ boolean m2630lambda$new$6$orgtelegramuiComponentsForwardingPreviewView(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1) {
            dismiss(true);
        }
        return true;
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-ForwardingPreviewView */
    public /* synthetic */ boolean m2631lambda$new$7$orgtelegramuiComponentsForwardingPreviewView(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1) {
            dismiss(true);
        }
        return true;
    }

    private void updateSubtitle() {
        if (!this.forwardingMessagesParams.hasSenders) {
            if (this.forwardingMessagesParams.willSeeSenders) {
                TLRPC.User user = this.currentUser;
                if (user != null) {
                    this.actionBar.setSubtitle(LocaleController.formatString("ForwardPreviewSendersNameVisible", R.string.ForwardPreviewSendersNameVisible, ContactsController.formatName(user.first_name, this.currentUser.last_name)));
                    return;
                } else if (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup) {
                    this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameVisibleChannel", R.string.ForwardPreviewSendersNameVisibleChannel));
                    return;
                } else {
                    this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameVisibleGroup", R.string.ForwardPreviewSendersNameVisibleGroup));
                    return;
                }
            }
            TLRPC.User user2 = this.currentUser;
            if (user2 != null) {
                this.actionBar.setSubtitle(LocaleController.formatString("ForwardPreviewSendersNameVisible", R.string.ForwardPreviewSendersNameVisible, ContactsController.formatName(user2.first_name, this.currentUser.last_name)));
            } else if (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup) {
                this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameHiddenChannel", R.string.ForwardPreviewSendersNameHiddenChannel));
            } else {
                this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameHiddenGroup", R.string.ForwardPreviewSendersNameHiddenGroup));
            }
        } else if (!this.forwardingMessagesParams.hideForwardSendersName) {
            TLRPC.User user3 = this.currentUser;
            if (user3 != null) {
                this.actionBar.setSubtitle(LocaleController.formatString("ForwardPreviewSendersNameVisible", R.string.ForwardPreviewSendersNameVisible, ContactsController.formatName(user3.first_name, this.currentUser.last_name)));
            } else if (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup) {
                this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameVisibleChannel", R.string.ForwardPreviewSendersNameVisibleChannel));
            } else {
                this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameVisibleGroup", R.string.ForwardPreviewSendersNameVisibleGroup));
            }
        } else {
            TLRPC.User user4 = this.currentUser;
            if (user4 != null) {
                this.actionBar.setSubtitle(LocaleController.formatString("ForwardPreviewSendersNameHidden", R.string.ForwardPreviewSendersNameHidden, ContactsController.formatName(user4.first_name, this.currentUser.last_name)));
            } else if (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup) {
                this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameHiddenChannel", R.string.ForwardPreviewSendersNameHiddenChannel));
            } else {
                this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameHiddenGroup", R.string.ForwardPreviewSendersNameHiddenGroup));
            }
        }
    }

    private void updateColors() {
    }

    public void dismiss(boolean canShowKeyboard) {
        if (this.showing) {
            this.showing = false;
            animate().alpha(0.0f).scaleX(0.95f).scaleY(0.95f).setDuration(250L).setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ForwardingPreviewView.12
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (ForwardingPreviewView.this.getParent() != null) {
                        ViewGroup parent = (ViewGroup) ForwardingPreviewView.this.getParent();
                        parent.removeView(ForwardingPreviewView.this);
                    }
                }
            });
            onDismiss(canShowKeyboard);
        }
    }

    protected void onDismiss(boolean canShowKeyboard) {
    }

    public void updateMessages() {
        if (this.itemAnimator.isRunning()) {
            this.updateAfterAnimations = true;
            return;
        }
        int i = 0;
        while (true) {
            int i2 = 0;
            if (i >= this.forwardingMessagesParams.previewMessages.size()) {
                break;
            }
            MessageObject messageObject = this.forwardingMessagesParams.previewMessages.get(i);
            messageObject.forceUpdate = true;
            messageObject.sendAsPeer = this.sendAsPeer;
            if (!this.forwardingMessagesParams.hideForwardSendersName) {
                messageObject.messageOwner.flags |= 4;
                messageObject.hideSendersName = false;
            } else {
                messageObject.messageOwner.flags &= -5;
                messageObject.hideSendersName = true;
            }
            if (this.forwardingMessagesParams.hideCaption) {
                messageObject.caption = null;
            } else {
                messageObject.generateCaption();
            }
            if (messageObject.isPoll()) {
                ForwardingMessagesParams.PreviewMediaPoll mediaPoll = (ForwardingMessagesParams.PreviewMediaPoll) messageObject.messageOwner.media;
                TLRPC.PollResults pollResults = mediaPoll.results;
                if (!this.forwardingMessagesParams.hideCaption) {
                    i2 = mediaPoll.totalVotersCached;
                }
                pollResults.total_voters = i2;
            }
            i++;
        }
        for (int i3 = 0; i3 < this.forwardingMessagesParams.pollChoosenAnswers.size(); i3++) {
            this.forwardingMessagesParams.pollChoosenAnswers.get(i3).chosen = !this.forwardingMessagesParams.hideForwardSendersName;
        }
        for (int i4 = 0; i4 < this.forwardingMessagesParams.groupedMessagesMap.size(); i4++) {
            this.itemAnimator.groupWillChanged(this.forwardingMessagesParams.groupedMessagesMap.valueAt(i4));
        }
        this.adapter.notifyItemRangeChanged(0, this.forwardingMessagesParams.previewMessages.size());
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxActionWidth = 0;
        this.isLandscapeMode = View.MeasureSpec.getSize(widthMeasureSpec) > View.MeasureSpec.getSize(heightMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        if (this.isLandscapeMode) {
            width = (int) (View.MeasureSpec.getSize(widthMeasureSpec) * 0.38f);
        }
        for (int i = 0; i < this.actionItems.size(); i++) {
            this.actionItems.get(i).measure(View.MeasureSpec.makeMeasureSpec(width, 0), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), 0));
            if (this.actionItems.get(i).getMeasuredWidth() > maxActionWidth) {
                maxActionWidth = this.actionItems.get(i).getMeasuredWidth();
            }
        }
        this.buttonsLayout.getBackground().getPadding(this.rect);
        int buttonsWidth = this.rect.left + maxActionWidth + this.rect.right;
        this.buttonsLayout.getLayoutParams().width = buttonsWidth;
        this.buttonsLayout2.getLayoutParams().width = buttonsWidth;
        this.buttonsLayout.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), 0));
        this.buttonsLayout2.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), 0));
        ((ViewGroup.MarginLayoutParams) this.chatListView.getLayoutParams()).topMargin = ActionBar.getCurrentActionBarHeight();
        if (this.isLandscapeMode) {
            this.chatPreviewContainer.getLayoutParams().height = -1;
            ((ViewGroup.MarginLayoutParams) this.chatPreviewContainer.getLayoutParams()).topMargin = AndroidUtilities.dp(8.0f);
            ((ViewGroup.MarginLayoutParams) this.chatPreviewContainer.getLayoutParams()).bottomMargin = AndroidUtilities.dp(8.0f);
            this.chatPreviewContainer.getLayoutParams().width = (int) Math.min(View.MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(340.0f), View.MeasureSpec.getSize(widthMeasureSpec) * 0.6f));
            this.menuScrollView.getLayoutParams().height = -1;
        } else {
            ((ViewGroup.MarginLayoutParams) this.chatPreviewContainer.getLayoutParams()).topMargin = 0;
            ((ViewGroup.MarginLayoutParams) this.chatPreviewContainer.getLayoutParams()).bottomMargin = 0;
            this.chatPreviewContainer.getLayoutParams().height = ((View.MeasureSpec.getSize(heightMeasureSpec) - AndroidUtilities.dp(6.0f)) - this.buttonsLayout.getMeasuredHeight()) - this.buttonsLayout2.getMeasuredHeight();
            if (this.chatPreviewContainer.getLayoutParams().height < View.MeasureSpec.getSize(heightMeasureSpec) * 0.5f) {
                this.chatPreviewContainer.getLayoutParams().height = (int) (View.MeasureSpec.getSize(heightMeasureSpec) * 0.5f);
            }
            this.chatPreviewContainer.getLayoutParams().width = -1;
            this.menuScrollView.getLayoutParams().height = View.MeasureSpec.getSize(heightMeasureSpec) - this.chatPreviewContainer.getLayoutParams().height;
        }
        int size = (View.MeasureSpec.getSize(widthMeasureSpec) + View.MeasureSpec.getSize(heightMeasureSpec)) << 16;
        if (this.lastSize != size) {
            for (int i2 = 0; i2 < this.forwardingMessagesParams.previewMessages.size(); i2++) {
                if (this.isLandscapeMode) {
                    this.forwardingMessagesParams.previewMessages.get(i2).parentWidth = this.chatPreviewContainer.getLayoutParams().width;
                } else {
                    this.forwardingMessagesParams.previewMessages.get(i2).parentWidth = View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(16.0f);
                }
                this.forwardingMessagesParams.previewMessages.get(i2).resetLayout();
                this.forwardingMessagesParams.previewMessages.get(i2).forceUpdate = true;
                Adapter adapter = this.adapter;
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
            this.firstLayout = true;
        }
        this.lastSize = size;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updatePositions();
        this.firstLayout = false;
    }

    public void updatePositions() {
        final int lastTopOffset = this.chatTopOffset;
        final float lastYOffset = this.yOffset;
        if (!this.isLandscapeMode) {
            if (this.chatListView.getChildCount() == 0 || this.chatListView.getChildCount() > this.forwardingMessagesParams.previewMessages.size()) {
                this.chatTopOffset = 0;
            } else {
                int minTop = this.chatListView.getChildAt(0).getTop();
                for (int i = 1; i < this.chatListView.getChildCount(); i++) {
                    if (this.chatListView.getChildAt(i).getTop() < minTop) {
                        minTop = this.chatListView.getChildAt(i).getTop();
                    }
                }
                int minTop2 = minTop - AndroidUtilities.dp(4.0f);
                if (minTop2 < 0) {
                    this.chatTopOffset = 0;
                } else {
                    this.chatTopOffset = minTop2;
                }
            }
            float totalViewsHeight = ((this.buttonsLayout.getMeasuredHeight() + this.buttonsLayout2.getMeasuredHeight()) - AndroidUtilities.dp(8.0f)) + (this.chatPreviewContainer.getMeasuredHeight() - this.chatTopOffset);
            float totalHeight = getMeasuredHeight() - AndroidUtilities.dp(16.0f);
            float dp = (AndroidUtilities.dp(8.0f) + ((totalHeight - totalViewsHeight) / 2.0f)) - this.chatTopOffset;
            this.yOffset = dp;
            if (dp > AndroidUtilities.dp(8.0f)) {
                this.yOffset = AndroidUtilities.dp(8.0f);
            }
            float buttonX = getMeasuredWidth() - this.menuScrollView.getMeasuredWidth();
            this.menuScrollView.setTranslationX(buttonX);
        } else {
            this.yOffset = 0.0f;
            this.chatTopOffset = 0;
            this.menuScrollView.setTranslationX(this.chatListView.getMeasuredWidth() + AndroidUtilities.dp(8.0f));
        }
        boolean z = this.firstLayout;
        if (!z && (this.chatTopOffset != lastTopOffset || this.yOffset != lastYOffset)) {
            ValueAnimator valueAnimator = this.offsetsAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.offsetsAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ForwardingPreviewView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ForwardingPreviewView.this.m2632x83256e3b(lastTopOffset, lastYOffset, valueAnimator2);
                }
            });
            this.offsetsAnimator.setDuration(250L);
            this.offsetsAnimator.setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR);
            this.offsetsAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ForwardingPreviewView.13
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    ForwardingPreviewView.this.offsetsAnimator = null;
                    ForwardingPreviewView forwardingPreviewView = ForwardingPreviewView.this;
                    forwardingPreviewView.setOffset(forwardingPreviewView.yOffset, ForwardingPreviewView.this.chatTopOffset);
                }
            });
            AndroidUtilities.runOnUIThread(this.changeBoundsRunnable, 50L);
            this.currentTopOffset = lastTopOffset;
            this.currentYOffset = lastYOffset;
            setOffset(lastYOffset, lastTopOffset);
        } else if (z) {
            float f = this.yOffset;
            this.currentYOffset = f;
            int i2 = this.chatTopOffset;
            this.currentTopOffset = i2;
            setOffset(f, i2);
        }
    }

    /* renamed from: lambda$updatePositions$8$org-telegram-ui-Components-ForwardingPreviewView */
    public /* synthetic */ void m2632x83256e3b(int lastTopOffset, float lastYOffset, ValueAnimator valueAnimator) {
        float p = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        int i = (int) ((lastTopOffset * (1.0f - p)) + (this.chatTopOffset * p));
        this.currentTopOffset = i;
        float f = ((1.0f - p) * lastYOffset) + (this.yOffset * p);
        this.currentYOffset = f;
        setOffset(f, i);
    }

    public void setOffset(float yOffset, int chatTopOffset) {
        if (this.isLandscapeMode) {
            this.actionBar.setTranslationY(0.0f);
            if (Build.VERSION.SDK_INT >= 21) {
                this.chatPreviewContainer.invalidateOutline();
            }
            this.chatPreviewContainer.setTranslationY(0.0f);
            this.menuScrollView.setTranslationY(0.0f);
            return;
        }
        this.actionBar.setTranslationY(chatTopOffset);
        if (Build.VERSION.SDK_INT >= 21) {
            this.chatPreviewContainer.invalidateOutline();
        }
        this.chatPreviewContainer.setTranslationY(yOffset);
        this.menuScrollView.setTranslationY((this.chatPreviewContainer.getMeasuredHeight() + yOffset) - AndroidUtilities.dp(2.0f));
    }

    public boolean isShowing() {
        return this.showing;
    }

    /* loaded from: classes5.dex */
    public class Adapter extends RecyclerView.Adapter {
        private Adapter() {
            ForwardingPreviewView.this = r1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ChatMessageCell chatMessageCell = new ChatMessageCell(parent.getContext(), false, ForwardingPreviewView.this.resourcesProvider);
            return new RecyclerListView.Holder(chatMessageCell);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ChatMessageCell cell = (ChatMessageCell) holder.itemView;
            cell.setInvalidateSpoilersParent(ForwardingPreviewView.this.forwardingMessagesParams.hasSpoilers);
            cell.setParentViewSize(ForwardingPreviewView.this.chatListView.getMeasuredWidth(), ForwardingPreviewView.this.chatListView.getMeasuredHeight());
            int id = cell.getMessageObject() != null ? cell.getMessageObject().getId() : 0;
            boolean z = true;
            cell.setMessageObject(ForwardingPreviewView.this.forwardingMessagesParams.previewMessages.get(position), ForwardingPreviewView.this.forwardingMessagesParams.groupedMessagesMap.get(ForwardingPreviewView.this.forwardingMessagesParams.previewMessages.get(position).getGroupId()), true, true);
            cell.setDelegate(new ChatMessageCell.ChatMessageCellDelegate() { // from class: org.telegram.ui.Components.ForwardingPreviewView.Adapter.1
                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ boolean canDrawOutboundsContent() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canDrawOutboundsContent(this);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ boolean canPerformActions() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canPerformActions(this);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didLongPress(ChatMessageCell chatMessageCell, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPress(this, chatMessageCell, f, f2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didLongPressBotButton(ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressBotButton(this, chatMessageCell, keyboardButton);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ boolean didLongPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC.Chat chat, int i, float f, float f2) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressChannelAvatar(this, chatMessageCell, chat, i, f, f2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ boolean didLongPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressUserAvatar(this, chatMessageCell, user, f, f2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressBotButton(ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressBotButton(this, chatMessageCell, keyboardButton);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressCancelSendButton(ChatMessageCell chatMessageCell) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCancelSendButton(this, chatMessageCell);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC.Chat chat, int i, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressChannelAvatar(this, chatMessageCell, chat, i, f, f2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressCommentButton(ChatMessageCell chatMessageCell) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCommentButton(this, chatMessageCell);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressHiddenForward(ChatMessageCell chatMessageCell) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHiddenForward(this, chatMessageCell);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressHint(ChatMessageCell chatMessageCell, int i) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHint(this, chatMessageCell, i);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressImage(ChatMessageCell chatMessageCell, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressImage(this, chatMessageCell, f, f2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressInstantButton(ChatMessageCell chatMessageCell, int i) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressInstantButton(this, chatMessageCell, i);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressOther(ChatMessageCell chatMessageCell, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressOther(this, chatMessageCell, f, f2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressReaction(ChatMessageCell chatMessageCell, TLRPC.TL_reactionCount tL_reactionCount, boolean z2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReaction(this, chatMessageCell, tL_reactionCount, z2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressReplyMessage(ChatMessageCell chatMessageCell, int i) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReplyMessage(this, chatMessageCell, i);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressSideButton(ChatMessageCell chatMessageCell) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressSideButton(this, chatMessageCell);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressTime(ChatMessageCell chatMessageCell) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressTime(this, chatMessageCell);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressUrl(ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUrl(this, chatMessageCell, characterStyle, z2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUserAvatar(this, chatMessageCell, user, f, f2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressViaBot(ChatMessageCell chatMessageCell, String str) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBot(this, chatMessageCell, str);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressViaBotNotInline(ChatMessageCell chatMessageCell, long j) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBotNotInline(this, chatMessageCell, j);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressVoteButtons(ChatMessageCell chatMessageCell, ArrayList arrayList, int i, int i2, int i3) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressVoteButtons(this, chatMessageCell, arrayList, i, i2, i3);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didStartVideoStream(MessageObject messageObject) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didStartVideoStream(this, messageObject);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ String getAdminRank(long j) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getAdminRank(this, j);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ PinchToZoomHelper getPinchToZoomHelper() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getPinchToZoomHelper(this);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ TextSelectionHelper.ChatListTextSelectionHelper getTextSelectionHelper() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getTextSelectionHelper(this);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ boolean hasSelectedMessages() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$hasSelectedMessages(this);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void invalidateBlur() {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$invalidateBlur(this);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ boolean isLandscape() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$isLandscape(this);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ boolean keyboardIsOpened() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$keyboardIsOpened(this);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void needOpenWebView(MessageObject messageObject, String str, String str2, String str3, String str4, int i, int i2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$needOpenWebView(this, messageObject, str, str2, str3, str4, i, i2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ boolean needPlayMessage(MessageObject messageObject) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$needPlayMessage(this, messageObject);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void needReloadPolls() {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$needReloadPolls(this);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void needShowPremiumFeatures(String str) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$needShowPremiumFeatures(this, str);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ boolean onAccessibilityAction(int i, Bundle bundle) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$onAccessibilityAction(this, i, bundle);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void onDiceFinished() {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$onDiceFinished(this);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void setShouldNotRepeatSticker(MessageObject messageObject) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$setShouldNotRepeatSticker(this, messageObject);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ boolean shouldDrawThreadProgress(ChatMessageCell chatMessageCell) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldDrawThreadProgress(this, chatMessageCell);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ boolean shouldRepeatSticker(MessageObject messageObject) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldRepeatSticker(this, messageObject);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void videoTimerReached() {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$videoTimerReached(this);
                }
            });
            if (ForwardingPreviewView.this.forwardingMessagesParams.previewMessages.size() > 1) {
                cell.setCheckBoxVisible(true, false);
                if (id != ForwardingPreviewView.this.forwardingMessagesParams.previewMessages.get(position).getId()) {
                    z = false;
                }
                boolean animated = z;
                boolean checked = ForwardingPreviewView.this.forwardingMessagesParams.selectedIds.get(ForwardingPreviewView.this.forwardingMessagesParams.previewMessages.get(position).getId(), false);
                cell.setChecked(checked, checked, animated);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return ForwardingPreviewView.this.forwardingMessagesParams.previewMessages.size();
        }
    }

    public void selectAnotherChat() {
    }

    public void didSendPressed() {
    }

    public MessageObject.GroupedMessages getValidGroupedMessage(MessageObject message) {
        if (message.getGroupId() == 0) {
            return null;
        }
        MessageObject.GroupedMessages groupedMessages = this.forwardingMessagesParams.groupedMessagesMap.get(message.getGroupId());
        if (groupedMessages == null) {
            return groupedMessages;
        }
        if (groupedMessages.messages.size() <= 1 || groupedMessages.positions.get(message) == null) {
            return null;
        }
        return groupedMessages;
    }

    private int getThemedColor(String key) {
        ResourcesDelegate resourcesDelegate = this.resourcesProvider;
        Integer color = resourcesDelegate != null ? resourcesDelegate.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
