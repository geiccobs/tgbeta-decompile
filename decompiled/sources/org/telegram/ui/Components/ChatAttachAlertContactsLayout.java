package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertContactsLayout;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes5.dex */
public class ChatAttachAlertContactsLayout extends ChatAttachAlert.AttachAlertLayout implements NotificationCenter.NotificationCenterDelegate {
    private PhonebookShareAlertDelegate delegate;
    private EmptyTextProgressView emptyView;
    private FrameLayout frameLayout;
    private boolean ignoreLayout;
    private FillLastLinearLayoutManager layoutManager;
    private ShareAdapter listAdapter;
    private RecyclerListView listView;
    private ShareSearchAdapter searchAdapter;
    private SearchField searchField;
    private View shadow;
    private AnimatorSet shadowAnimation;

    /* loaded from: classes5.dex */
    public interface PhonebookShareAlertDelegate {
        void didSelectContact(TLRPC.User user, boolean z, int i);
    }

    /* loaded from: classes5.dex */
    public static class UserCell extends FrameLayout {
        private AvatarDrawable avatarDrawable;
        private BackupImageView avatarImageView;
        private int currentAccount = UserConfig.selectedAccount;
        private int currentId;
        private CharSequence currentName;
        private CharSequence currentStatus;
        private TLRPC.User currentUser;
        private CharSequence formattedPhoneNumber;
        private TLRPC.User formattedPhoneNumberUser;
        private TLRPC.FileLocation lastAvatar;
        private String lastName;
        private int lastStatus;
        private SimpleTextView nameTextView;
        private boolean needDivider;
        private final Theme.ResourcesProvider resourcesProvider;
        private SimpleTextView statusTextView;

        /* loaded from: classes5.dex */
        public interface CharSequenceCallback {
            CharSequence run();
        }

        public UserCell(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            this.avatarDrawable = new AvatarDrawable(resourcesProvider);
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(23.0f));
            int i = 5;
            addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 14.0f, 9.0f, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.nameTextView = simpleTextView;
            simpleTextView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
            this.nameTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.nameTextView.setTextSize(16);
            this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : 72.0f, 12.0f, LocaleController.isRTL ? 72.0f : 28.0f, 0.0f));
            SimpleTextView simpleTextView2 = new SimpleTextView(context);
            this.statusTextView = simpleTextView2;
            simpleTextView2.setTextSize(13);
            this.statusTextView.setTextColor(getThemedColor(Theme.key_dialogTextGray2));
            this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 28.0f : 72.0f, 36.0f, LocaleController.isRTL ? 72.0f : 28.0f, 0.0f));
        }

        public void setCurrentId(int id) {
            this.currentId = id;
        }

        public void setData(TLRPC.User user, CharSequence name, CharSequence status, boolean divider) {
            if (user == null && name == null && status == null) {
                this.currentStatus = null;
                this.currentName = null;
                this.nameTextView.setText("");
                this.statusTextView.setText("");
                this.avatarImageView.setImageDrawable(null);
                return;
            }
            this.currentStatus = status;
            this.currentName = name;
            this.currentUser = user;
            this.needDivider = divider;
            setWillNotDraw(!divider);
            update(0);
        }

        public void setData(TLRPC.User user, CharSequence name, final CharSequenceCallback status, boolean divider) {
            setData(user, name, (CharSequence) null, divider);
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$UserCell$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlertContactsLayout.UserCell.this.m2433x2582dee(status);
                }
            });
        }

        /* renamed from: lambda$setData$1$org-telegram-ui-Components-ChatAttachAlertContactsLayout$UserCell */
        public /* synthetic */ void m2433x2582dee(CharSequenceCallback status) {
            final CharSequence newCurrentStatus = status.run();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$UserCell$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlertContactsLayout.UserCell.this.m2432x2696b22d(newCurrentStatus);
                }
            });
        }

        /* renamed from: setStatus */
        public void m2432x2696b22d(CharSequence status) {
            CharSequence charSequence;
            this.currentStatus = status;
            if (status != null) {
                this.statusTextView.setText(status);
                return;
            }
            TLRPC.User user = this.currentUser;
            if (user != null) {
                if (TextUtils.isEmpty(user.phone)) {
                    this.statusTextView.setText(LocaleController.getString("NumberUnknown", R.string.NumberUnknown));
                } else if (this.formattedPhoneNumberUser != this.currentUser && (charSequence = this.formattedPhoneNumber) != null) {
                    this.statusTextView.setText(charSequence);
                } else {
                    this.statusTextView.setText("");
                    Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$UserCell$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChatAttachAlertContactsLayout.UserCell.this.m2435x8718ac78();
                        }
                    });
                }
            }
        }

        /* renamed from: lambda$setStatus$3$org-telegram-ui-Components-ChatAttachAlertContactsLayout$UserCell */
        public /* synthetic */ void m2435x8718ac78() {
            if (this.currentUser != null) {
                PhoneFormat phoneFormat = PhoneFormat.getInstance();
                this.formattedPhoneNumber = phoneFormat.format("+" + this.currentUser.phone);
                this.formattedPhoneNumberUser = this.currentUser;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$UserCell$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatAttachAlertContactsLayout.UserCell.this.m2434xab5730b7();
                    }
                });
            }
        }

        /* renamed from: lambda$setStatus$2$org-telegram-ui-Components-ChatAttachAlertContactsLayout$UserCell */
        public /* synthetic */ void m2434xab5730b7() {
            this.statusTextView.setText(this.formattedPhoneNumber);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), C.BUFFER_FLAG_ENCRYPTED));
        }

        public void update(int mask) {
            TLRPC.FileLocation fileLocation;
            TLRPC.FileLocation photo = null;
            String newName = null;
            TLRPC.User user = this.currentUser;
            if (user != null && user.photo != null) {
                photo = this.currentUser.photo.photo_small;
            }
            if (mask != 0) {
                boolean continueUpdate = false;
                if ((MessagesController.UPDATE_MASK_AVATAR & mask) != 0 && (((fileLocation = this.lastAvatar) != null && photo == null) || ((fileLocation == null && photo != null) || (fileLocation != null && photo != null && (fileLocation.volume_id != photo.volume_id || this.lastAvatar.local_id != photo.local_id))))) {
                    continueUpdate = true;
                }
                if (this.currentUser != null && !continueUpdate && (MessagesController.UPDATE_MASK_STATUS & mask) != 0) {
                    int newStatus = 0;
                    if (this.currentUser.status != null) {
                        newStatus = this.currentUser.status.expires;
                    }
                    if (newStatus != this.lastStatus) {
                        continueUpdate = true;
                    }
                }
                if (!continueUpdate && this.currentName == null && this.lastName != null && (MessagesController.UPDATE_MASK_NAME & mask) != 0) {
                    TLRPC.User user2 = this.currentUser;
                    if (user2 != null) {
                        newName = UserObject.getUserName(user2);
                    }
                    if (!newName.equals(this.lastName)) {
                        continueUpdate = true;
                    }
                }
                if (!continueUpdate) {
                    return;
                }
            }
            TLRPC.User user3 = this.currentUser;
            if (user3 != null) {
                this.avatarDrawable.setInfo(user3);
                if (this.currentUser.status != null) {
                    this.lastStatus = this.currentUser.status.expires;
                } else {
                    this.lastStatus = 0;
                }
            } else {
                CharSequence charSequence = this.currentName;
                if (charSequence != null) {
                    this.avatarDrawable.setInfo(this.currentId, charSequence.toString(), null);
                } else {
                    this.avatarDrawable.setInfo(this.currentId, "#", null);
                }
            }
            CharSequence charSequence2 = this.currentName;
            if (charSequence2 != null) {
                this.lastName = null;
                this.nameTextView.setText(charSequence2);
            } else {
                TLRPC.User user4 = this.currentUser;
                if (user4 != null) {
                    this.lastName = newName == null ? UserObject.getUserName(user4) : newName;
                } else {
                    this.lastName = "";
                }
                this.nameTextView.setText(this.lastName);
            }
            m2432x2696b22d(this.currentStatus);
            this.lastAvatar = photo;
            TLRPC.User user5 = this.currentUser;
            if (user5 != null) {
                this.avatarImageView.setForUserOrChat(user5, this.avatarDrawable);
            } else {
                this.avatarImageView.setImageDrawable(this.avatarDrawable);
            }
        }

        @Override // android.view.View
        public boolean hasOverlappingRendering() {
            return false;
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(70.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(70.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        }

        private int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }
    }

    public ChatAttachAlertContactsLayout(ChatAttachAlert alert, Context context, final Theme.ResourcesProvider resourcesProvider) {
        super(alert, context, resourcesProvider);
        this.searchAdapter = new ShareSearchAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.frameLayout = frameLayout;
        frameLayout.setBackgroundColor(getThemedColor(Theme.key_dialogBackground));
        SearchField searchField = new SearchField(context, false, resourcesProvider) { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout.1
            @Override // org.telegram.ui.Components.SearchField
            public void onTextChange(String text) {
                if (text.length() != 0) {
                    if (ChatAttachAlertContactsLayout.this.emptyView != null) {
                        ChatAttachAlertContactsLayout.this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                    }
                } else if (ChatAttachAlertContactsLayout.this.listView.getAdapter() != ChatAttachAlertContactsLayout.this.listAdapter) {
                    int top = ChatAttachAlertContactsLayout.this.getCurrentTop();
                    ChatAttachAlertContactsLayout.this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
                    ChatAttachAlertContactsLayout.this.emptyView.showTextView();
                    ChatAttachAlertContactsLayout.this.listView.setAdapter(ChatAttachAlertContactsLayout.this.listAdapter);
                    ChatAttachAlertContactsLayout.this.listAdapter.notifyDataSetChanged();
                    if (top > 0) {
                        ChatAttachAlertContactsLayout.this.layoutManager.scrollToPositionWithOffset(0, -top);
                    }
                }
                if (ChatAttachAlertContactsLayout.this.searchAdapter != null) {
                    ChatAttachAlertContactsLayout.this.searchAdapter.search(text);
                }
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                ChatAttachAlertContactsLayout.this.parentAlert.makeFocusable(getSearchEditText(), true);
                return super.onInterceptTouchEvent(ev);
            }

            @Override // org.telegram.ui.Components.SearchField
            public void processTouchEvent(MotionEvent event) {
                MotionEvent e = MotionEvent.obtain(event);
                e.setLocation(e.getRawX(), (e.getRawY() - ChatAttachAlertContactsLayout.this.parentAlert.getSheetContainer().getTranslationY()) - AndroidUtilities.dp(58.0f));
                ChatAttachAlertContactsLayout.this.listView.dispatchTouchEvent(e);
                e.recycle();
            }

            @Override // org.telegram.ui.Components.SearchField
            protected void onFieldTouchUp(EditTextBoldCursor editText) {
                ChatAttachAlertContactsLayout.this.parentAlert.makeFocusable(editText, true);
            }
        };
        this.searchField = searchField;
        searchField.setHint(LocaleController.getString("SearchFriends", R.string.SearchFriends));
        this.frameLayout.addView(this.searchField, LayoutHelper.createFrame(-1, -1, 51));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context, null, resourcesProvider);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showTextView();
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 52.0f, 0.0f, 0.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context, resourcesProvider) { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout.2
            @Override // org.telegram.ui.Components.RecyclerListView
            protected boolean allowSelectChildAtPosition(float x, float y) {
                return y >= ((float) ((ChatAttachAlertContactsLayout.this.parentAlert.scrollOffsetY[0] + AndroidUtilities.dp(30.0f)) + ((Build.VERSION.SDK_INT < 21 || ChatAttachAlertContactsLayout.this.parentAlert.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight)));
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setClipToPadding(false);
        RecyclerListView recyclerListView2 = this.listView;
        FillLastLinearLayoutManager fillLastLinearLayoutManager = new FillLastLinearLayoutManager(getContext(), 1, false, AndroidUtilities.dp(9.0f), this.listView) { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout.3
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout.3.1
                    @Override // androidx.recyclerview.widget.LinearSmoothScroller
                    public int calculateDyToMakeVisible(View view, int snapPreference) {
                        int dy = super.calculateDyToMakeVisible(view, snapPreference);
                        return dy - (ChatAttachAlertContactsLayout.this.listView.getPaddingTop() - AndroidUtilities.dp(8.0f));
                    }

                    @Override // androidx.recyclerview.widget.LinearSmoothScroller
                    public int calculateTimeForDeceleration(int dx) {
                        return super.calculateTimeForDeceleration(dx) * 2;
                    }
                };
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }
        };
        this.layoutManager = fillLastLinearLayoutManager;
        recyclerListView2.setLayoutManager(fillLastLinearLayoutManager);
        this.layoutManager.setBind(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        RecyclerListView recyclerListView3 = this.listView;
        ShareAdapter shareAdapter = new ShareAdapter(context);
        this.listAdapter = shareAdapter;
        recyclerListView3.setAdapter(shareAdapter);
        this.listView.setGlowColor(getThemedColor(Theme.key_dialogScrollGlow));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                ChatAttachAlertContactsLayout.this.m2427x9bcdc50f(resourcesProvider, view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout.4
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ChatAttachAlertContactsLayout.this.parentAlert.updateLayout(ChatAttachAlertContactsLayout.this, true, dy);
                ChatAttachAlertContactsLayout.this.updateEmptyViewPosition();
            }
        });
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        frameLayoutParams.topMargin = AndroidUtilities.dp(58.0f);
        View view = new View(context);
        this.shadow = view;
        view.setBackgroundColor(getThemedColor(Theme.key_dialogShadowLine));
        this.shadow.setAlpha(0.0f);
        this.shadow.setTag(1);
        addView(this.shadow, frameLayoutParams);
        addView(this.frameLayout, LayoutHelper.createFrame(-1, 58, 51));
        NotificationCenter.getInstance(this.parentAlert.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        updateEmptyView();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ChatAttachAlertContactsLayout */
    public /* synthetic */ void m2427x9bcdc50f(Theme.ResourcesProvider resourcesProvider, View view, int position) {
        Object object;
        String firstName;
        String firstName2;
        ContactsController.Contact contact;
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        ShareSearchAdapter shareSearchAdapter = this.searchAdapter;
        if (adapter == shareSearchAdapter) {
            object = shareSearchAdapter.getItem(position);
        } else {
            int section = this.listAdapter.getSectionForPosition(position);
            int row = this.listAdapter.getPositionInSectionForPosition(position);
            if (row < 0 || section < 0) {
                return;
            }
            object = this.listAdapter.getItem(section, row);
        }
        if (object != null) {
            if (object instanceof ContactsController.Contact) {
                contact = (ContactsController.Contact) object;
                if (contact.user != null) {
                    firstName2 = contact.user.first_name;
                    firstName = contact.user.last_name;
                } else {
                    firstName2 = contact.first_name;
                    firstName = contact.last_name;
                }
            } else {
                TLRPC.User user = (TLRPC.User) object;
                ContactsController.Contact contact2 = new ContactsController.Contact();
                String firstName3 = user.first_name;
                contact2.first_name = firstName3;
                String lastName = user.last_name;
                contact2.last_name = lastName;
                contact2.phones.add(user.phone);
                contact2.user = user;
                contact = contact2;
                firstName2 = firstName3;
                firstName = lastName;
            }
            PhonebookShareAlert phonebookShareAlert = new PhonebookShareAlert(this.parentAlert.baseFragment, contact, null, null, null, firstName2, firstName, resourcesProvider);
            phonebookShareAlert.setDelegate(new PhonebookShareAlertDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$$ExternalSyntheticLambda1
                @Override // org.telegram.ui.Components.ChatAttachAlertContactsLayout.PhonebookShareAlertDelegate
                public final void didSelectContact(TLRPC.User user2, boolean z, int i) {
                    ChatAttachAlertContactsLayout.this.m2426xee0adf0(user2, z, i);
                }
            });
            phonebookShareAlert.show();
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ChatAttachAlertContactsLayout */
    public /* synthetic */ void m2426xee0adf0(TLRPC.User user, boolean notify, int scheduleDate) {
        this.parentAlert.dismiss(true);
        this.delegate.didSelectContact(user, notify, scheduleDate);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void scrollToTop() {
        this.listView.smoothScrollToPosition(0);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getCurrentItemTop() {
        if (this.listView.getChildCount() <= 0) {
            return Integer.MAX_VALUE;
        }
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop() - AndroidUtilities.dp(8.0f);
        int newOffset = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
        if (top >= 0 && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
            runShadowAnimation(false);
        } else {
            runShadowAnimation(true);
        }
        this.frameLayout.setTranslationY(newOffset);
        return AndroidUtilities.dp(12.0f) + newOffset;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(4.0f);
    }

    @Override // android.view.View
    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        this.parentAlert.getSheetContainer().invalidate();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getListTopPadding() {
        return this.listView.getPaddingTop();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onPreMeasure(int availableWidth, int availableHeight) {
        int padding;
        if (this.parentAlert.sizeNotifierFrameLayout.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
            padding = AndroidUtilities.dp(8.0f);
            this.parentAlert.setAllowNestedScroll(false);
        } else {
            if (!AndroidUtilities.isTablet() && AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                padding = (int) (availableHeight / 3.5f);
            } else {
                padding = (availableHeight / 5) * 2;
            }
            this.parentAlert.setAllowNestedScroll(true);
        }
        if (this.listView.getPaddingTop() != padding) {
            this.ignoreLayout = true;
            this.listView.setPadding(0, padding, 0, 0);
            this.ignoreLayout = false;
        }
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    private void runShadowAnimation(final boolean show) {
        if ((show && this.shadow.getTag() != null) || (!show && this.shadow.getTag() == null)) {
            this.shadow.setTag(show ? null : 1);
            if (show) {
                this.shadow.setVisibility(0);
            }
            AnimatorSet animatorSet = this.shadowAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.shadowAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            View view = this.shadow;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.shadowAnimation.setDuration(150L);
            this.shadowAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout.5
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (ChatAttachAlertContactsLayout.this.shadowAnimation != null && ChatAttachAlertContactsLayout.this.shadowAnimation.equals(animation)) {
                        if (!show) {
                            ChatAttachAlertContactsLayout.this.shadow.setVisibility(4);
                        }
                        ChatAttachAlertContactsLayout.this.shadowAnimation = null;
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    if (ChatAttachAlertContactsLayout.this.shadowAnimation != null && ChatAttachAlertContactsLayout.this.shadowAnimation.equals(animation)) {
                        ChatAttachAlertContactsLayout.this.shadowAnimation = null;
                    }
                }
            });
            this.shadowAnimation.start();
        }
    }

    public int getCurrentTop() {
        if (this.listView.getChildCount() != 0) {
            int i = 0;
            View child = this.listView.getChildAt(0);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
            if (holder != null) {
                int paddingTop = this.listView.getPaddingTop();
                if (holder.getAdapterPosition() == 0 && child.getTop() >= 0) {
                    i = child.getTop();
                }
                return paddingTop - i;
            }
            return -1000;
        }
        return -1000;
    }

    public void setDelegate(PhonebookShareAlertDelegate phonebookShareAlertDelegate) {
        this.delegate = phonebookShareAlertDelegate;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        ShareAdapter shareAdapter;
        if (id == NotificationCenter.contactsDidLoad && (shareAdapter = this.listAdapter) != null) {
            shareAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onDestroy() {
        NotificationCenter.getInstance(this.parentAlert.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onShow(ChatAttachAlert.AttachAlertLayout previousLayout) {
        this.layoutManager.scrollToPositionWithOffset(0, 0);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateEmptyViewPosition();
    }

    public void updateEmptyViewPosition() {
        View child;
        if (this.emptyView.getVisibility() != 0 || (child = this.listView.getChildAt(0)) == null) {
            return;
        }
        EmptyTextProgressView emptyTextProgressView = this.emptyView;
        emptyTextProgressView.setTranslationY(((emptyTextProgressView.getMeasuredHeight() - getMeasuredHeight()) + child.getTop()) / 2);
    }

    public void updateEmptyView() {
        int i = 0;
        boolean visible = this.listView.getAdapter().getItemCount() == 2;
        EmptyTextProgressView emptyTextProgressView = this.emptyView;
        if (!visible) {
            i = 8;
        }
        emptyTextProgressView.setVisibility(i);
        updateEmptyViewPosition();
    }

    /* loaded from: classes5.dex */
    public class ShareAdapter extends RecyclerListView.SectionsAdapter {
        private int currentAccount = UserConfig.selectedAccount;
        private Context mContext;

        public ShareAdapter(Context context) {
            ChatAttachAlertContactsLayout.this = this$0;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public Object getItem(int section, int position) {
            if (section == 0) {
                return null;
            }
            int section2 = section - 1;
            HashMap<String, ArrayList<Object>> usersSectionsDict = ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict;
            ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray;
            if (section2 < sortedUsersSectionsArray.size()) {
                ArrayList<Object> arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section2));
                if (position < arr.size()) {
                    return arr.get(position);
                }
            }
            return null;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder, int section, int row) {
            if (section == 0 || section == getSectionCount() - 1) {
                return false;
            }
            HashMap<String, ArrayList<Object>> usersSectionsDict = ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict;
            ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray;
            return row < usersSectionsDict.get(sortedUsersSectionsArray.get(section + (-1))).size();
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getSectionCount() {
            ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray;
            return sortedUsersSectionsArray.size() + 2;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getCountForSection(int section) {
            if (section == 0 || section == getSectionCount() - 1) {
                return 1;
            }
            int section2 = section - 1;
            HashMap<String, ArrayList<Object>> usersSectionsDict = ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict;
            ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray;
            if (section2 < sortedUsersSectionsArray.size()) {
                return usersSectionsDict.get(sortedUsersSectionsArray.get(section2)).size();
            }
            return 0;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public View getSectionHeaderView(int section, View view) {
            return null;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new UserCell(this.mContext, ChatAttachAlertContactsLayout.this.resourcesProvider);
                    break;
                case 1:
                    view = new View(this.mContext);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                    break;
                default:
                    view = new View(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 0) {
                UserCell userCell = (UserCell) holder.itemView;
                Object object = getItem(section, position);
                TLRPC.User user = null;
                boolean z = true;
                if (section == getSectionCount() - 2 && position == getCountForSection(section) - 1) {
                    z = false;
                }
                boolean divider = z;
                if (object instanceof ContactsController.Contact) {
                    final ContactsController.Contact contact = (ContactsController.Contact) object;
                    if (contact.user != null) {
                        user = contact.user;
                    } else {
                        userCell.setCurrentId(contact.contact_id);
                        userCell.setData((TLRPC.User) null, ContactsController.formatName(contact.first_name, contact.last_name), new UserCell.CharSequenceCallback() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$ShareAdapter$$ExternalSyntheticLambda0
                            @Override // org.telegram.ui.Components.ChatAttachAlertContactsLayout.UserCell.CharSequenceCallback
                            public final CharSequence run() {
                                return ChatAttachAlertContactsLayout.ShareAdapter.lambda$onBindViewHolder$0(ContactsController.Contact.this);
                            }
                        }, divider);
                    }
                } else {
                    user = (TLRPC.User) object;
                }
                if (user != null) {
                    final TLRPC.User finalUser = user;
                    userCell.setData(user, (CharSequence) null, new UserCell.CharSequenceCallback() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$ShareAdapter$$ExternalSyntheticLambda1
                        @Override // org.telegram.ui.Components.ChatAttachAlertContactsLayout.UserCell.CharSequenceCallback
                        public final CharSequence run() {
                            TLRPC.User user2 = TLRPC.User.this;
                            return PhoneFormat.getInstance();
                        }
                    }, divider);
                }
            }
        }

        public static /* synthetic */ CharSequence lambda$onBindViewHolder$0(ContactsController.Contact contact) {
            return contact.phones.isEmpty() ? "" : PhoneFormat.getInstance().format(contact.phones.get(0));
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getItemViewType(int section, int position) {
            if (section == 0) {
                return 1;
            }
            if (section == getSectionCount() - 1) {
                return 2;
            }
            return 0;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public String getLetter(int position) {
            return null;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) {
            position[0] = 0;
            position[1] = 0;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertContactsLayout.this.updateEmptyView();
        }
    }

    /* loaded from: classes5.dex */
    public class ShareSearchAdapter extends RecyclerListView.SelectionAdapter {
        private int lastSearchId;
        private Context mContext;
        private ArrayList<Object> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;

        public ShareSearchAdapter(Context context) {
            ChatAttachAlertContactsLayout.this = this$0;
            this.mContext = context;
        }

        public void search(final String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (query == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                notifyDataSetChanged();
                return;
            }
            final int searchId = this.lastSearchId + 1;
            this.lastSearchId = searchId;
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlertContactsLayout.ShareSearchAdapter.this.m2430x8377f89e(query, searchId);
                }
            };
            this.searchRunnable = runnable;
            dispatchQueue.postRunnable(runnable, 300L);
        }

        /* renamed from: processSearch */
        public void m2430x8377f89e(final String query, final int searchId) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlertContactsLayout.ShareSearchAdapter.this.m2429xcc03ea95(query, searchId);
                }
            });
        }

        /* renamed from: lambda$processSearch$2$org-telegram-ui-Components-ChatAttachAlertContactsLayout$ShareSearchAdapter */
        public /* synthetic */ void m2429xcc03ea95(final String query, final int searchId) {
            final int currentAccount = UserConfig.selectedAccount;
            final ArrayList<ContactsController.Contact> contactsCopy = new ArrayList<>(ContactsController.getInstance(currentAccount).contactsBook.values());
            final ArrayList<TLRPC.TL_contact> contactsCopy2 = new ArrayList<>(ContactsController.getInstance(currentAccount).contacts);
            Utilities.searchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlertContactsLayout.ShareSearchAdapter.this.m2428xcb356c14(query, contactsCopy, contactsCopy2, currentAccount, searchId);
                }
            });
        }

        /* JADX WARN: Code restructure failed: missing block: B:35:0x00d1, code lost:
            if (r2.contains(" " + r3) == false) goto L36;
         */
        /* JADX WARN: Code restructure failed: missing block: B:40:0x00ee, code lost:
            if (r5.contains(" " + r3) != false) goto L41;
         */
        /* JADX WARN: Code restructure failed: missing block: B:57:0x013e, code lost:
            if (r12.contains(" " + r3) != false) goto L59;
         */
        /* JADX WARN: Code restructure failed: missing block: B:93:0x023f, code lost:
            if (r10.contains(" " + r2) != false) goto L100;
         */
        /* JADX WARN: Removed duplicated region for block: B:107:0x0293 A[LOOP:3: B:84:0x0203->B:107:0x0293, LOOP_END] */
        /* JADX WARN: Removed duplicated region for block: B:115:0x0147 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:121:0x0257 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:72:0x01a3 A[LOOP:1: B:29:0x00b0->B:72:0x01a3, LOOP_END] */
        /* renamed from: lambda$processSearch$1$org-telegram-ui-Components-ChatAttachAlertContactsLayout$ShareSearchAdapter */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public /* synthetic */ void m2428xcb356c14(java.lang.String r22, java.util.ArrayList r23, java.util.ArrayList r24, int r25, int r26) {
            /*
                Method dump skipped, instructions count: 683
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertContactsLayout.ShareSearchAdapter.m2428xcb356c14(java.lang.String, java.util.ArrayList, java.util.ArrayList, int, int):void");
        }

        private void updateSearchResults(String query, final ArrayList<Object> users, final ArrayList<CharSequence> names, final int searchId) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlertContactsLayout.ShareSearchAdapter.this.m2431xcea3a044(searchId, users, names);
                }
            });
        }

        /* renamed from: lambda$updateSearchResults$3$org-telegram-ui-Components-ChatAttachAlertContactsLayout$ShareSearchAdapter */
        public /* synthetic */ void m2431xcea3a044(int searchId, ArrayList users, ArrayList names) {
            if (searchId != this.lastSearchId) {
                return;
            }
            if (searchId != -1 && ChatAttachAlertContactsLayout.this.listView.getAdapter() != ChatAttachAlertContactsLayout.this.searchAdapter) {
                ChatAttachAlertContactsLayout.this.listView.setAdapter(ChatAttachAlertContactsLayout.this.searchAdapter);
            }
            this.searchResult = users;
            this.searchResultNames = names;
            notifyDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.searchResult.size() + 2;
        }

        public Object getItem(int position) {
            int position2 = position - 1;
            if (position2 < 0 || position2 >= this.searchResult.size()) {
                return null;
            }
            return this.searchResult.get(position2);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new UserCell(this.mContext, ChatAttachAlertContactsLayout.this.resourcesProvider);
                    break;
                case 1:
                    view = new View(this.mContext);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                    break;
                default:
                    view = new View(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                UserCell userCell = (UserCell) holder.itemView;
                boolean divider = position != getItemCount() + (-2);
                Object object = getItem(position);
                TLRPC.User user = null;
                if (object instanceof ContactsController.Contact) {
                    final ContactsController.Contact contact = (ContactsController.Contact) object;
                    if (contact.user != null) {
                        user = contact.user;
                    } else {
                        userCell.setCurrentId(contact.contact_id);
                        userCell.setData((TLRPC.User) null, this.searchResultNames.get(position - 1), new UserCell.CharSequenceCallback() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda4
                            @Override // org.telegram.ui.Components.ChatAttachAlertContactsLayout.UserCell.CharSequenceCallback
                            public final CharSequence run() {
                                return ChatAttachAlertContactsLayout.ShareSearchAdapter.lambda$onBindViewHolder$4(ContactsController.Contact.this);
                            }
                        }, divider);
                    }
                } else {
                    user = (TLRPC.User) object;
                }
                if (user != null) {
                    final TLRPC.User finalUser = user;
                    userCell.setData(user, this.searchResultNames.get(position - 1), new UserCell.CharSequenceCallback() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda5
                        @Override // org.telegram.ui.Components.ChatAttachAlertContactsLayout.UserCell.CharSequenceCallback
                        public final CharSequence run() {
                            TLRPC.User user2 = TLRPC.User.this;
                            return PhoneFormat.getInstance();
                        }
                    }, divider);
                }
            }
        }

        public static /* synthetic */ CharSequence lambda$onBindViewHolder$4(ContactsController.Contact contact) {
            return contact.phones.isEmpty() ? "" : PhoneFormat.getInstance().format(contact.phones.get(0));
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == 0) {
                return 1;
            }
            if (position == getItemCount() - 1) {
                return 2;
            }
            return 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertContactsLayout.this.updateEmptyView();
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$$ExternalSyntheticLambda0
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                ChatAttachAlertContactsLayout.this.m2425x637b688a();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.frameLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogBackground));
        themeDescriptions.add(new ThemeDescription(this.shadow, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogShadowLine));
        themeDescriptions.add(new ThemeDescription(this.searchField.getSearchBackground(), ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_dialogSearchBackground));
        themeDescriptions.add(new ThemeDescription(this.searchField, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SearchField.class}, new String[]{"searchIconImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogSearchIcon));
        themeDescriptions.add(new ThemeDescription(this.searchField, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SearchField.class}, new String[]{"clearSearchImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogSearchIcon));
        themeDescriptions.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_dialogSearchText));
        themeDescriptions.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_dialogSearchHint));
        themeDescriptions.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_featuredStickers_addedIcon));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_dialogScrollGlow));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextGray2));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, cellDelegate, Theme.key_dialogTextGray2));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$2$org-telegram-ui-Components-ChatAttachAlertContactsLayout */
    public /* synthetic */ void m2425x637b688a() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(0);
                }
            }
        }
    }
}
