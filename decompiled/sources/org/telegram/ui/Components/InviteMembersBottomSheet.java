package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.ScrollView;
import androidx.collection.LongSparseArray;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.google.firebase.messaging.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.GroupCreateSectionCell;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.InviteMembersBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UsersAlertBase;
import org.telegram.ui.GroupCreateActivity;
import org.telegram.ui.LaunchActivity;
/* loaded from: classes5.dex */
public class InviteMembersBottomSheet extends UsersAlertBase implements NotificationCenter.NotificationCenterDelegate {
    private int additionalHeight;
    private long chatId;
    private int contactsEndRow;
    private int contactsStartRow;
    private int copyLinkRow;
    private AnimatorSet currentAnimation;
    private GroupCreateSpan currentDeletingSpan;
    private AnimatorSet currentDoneButtonAnimation;
    private GroupCreateActivity.ContactsAddActivityDelegate delegate;
    private InviteMembersBottomSheetDelegate dialogsDelegate;
    private ArrayList<TLRPC.Dialog> dialogsServerOnly;
    private int emptyRow;
    boolean enterEventSent;
    private final ImageView floatingButton;
    private LongSparseArray<TLObject> ignoreUsers;
    TLRPC.TL_chatInviteExported invite;
    private int lastRow;
    boolean linkGenerating;
    private int maxSize;
    private int noContactsStubRow;
    private BaseFragment parentFragment;
    private int rowCount;
    private int scrollViewH;
    private SearchAdapter searchAdapter;
    private int searchAdditionalHeight;
    private boolean spanEnter;
    private final SpansContainer spansContainer;
    private ValueAnimator spansEnterAnimator;
    private final ScrollView spansScrollView;
    private float touchSlop;
    float y;
    private ArrayList<TLObject> contacts = new ArrayList<>();
    private LongSparseArray<GroupCreateSpan> selectedContacts = new LongSparseArray<>();
    private float spansEnterProgress = 0.0f;
    private View.OnClickListener spanClickListener = new View.OnClickListener() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet.1
        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            GroupCreateSpan span = (GroupCreateSpan) v;
            if (span.isDeleting()) {
                InviteMembersBottomSheet.this.currentDeletingSpan = null;
                InviteMembersBottomSheet.this.selectedContacts.remove(span.getUid());
                InviteMembersBottomSheet.this.spansContainer.removeSpan(span);
                InviteMembersBottomSheet.this.spansCountChanged(true);
                AndroidUtilities.updateVisibleRows(InviteMembersBottomSheet.this.listView);
                return;
            }
            if (InviteMembersBottomSheet.this.currentDeletingSpan != null) {
                InviteMembersBottomSheet.this.currentDeletingSpan.cancelDeleteAnimation();
            }
            InviteMembersBottomSheet.this.currentDeletingSpan = span;
            span.startDeleteAnimation();
        }
    };

    /* loaded from: classes5.dex */
    public interface InviteMembersBottomSheetDelegate {
        void didSelectDialogs(ArrayList<Long> arrayList);
    }

    public InviteMembersBottomSheet(final Context context, int account, final LongSparseArray<TLObject> ignoreUsers, final long chatId, final BaseFragment parentFragment, Theme.ResourcesProvider resourcesProvider) {
        super(context, false, account, resourcesProvider);
        this.ignoreUsers = ignoreUsers;
        this.needSnapToTop = false;
        this.parentFragment = parentFragment;
        this.chatId = chatId;
        fixNavigationBar();
        this.searchView.searchEditText.setHint(LocaleController.getString("SearchForChats", R.string.SearchForChats));
        ViewConfiguration configuration = ViewConfiguration.get(context);
        this.touchSlop = configuration.getScaledTouchSlop();
        SearchAdapter searchAdapter = new SearchAdapter();
        this.searchAdapter = searchAdapter;
        this.searchListViewAdapter = searchAdapter;
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter();
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        ArrayList<TLRPC.TL_contact> arrayList = ContactsController.getInstance(account).contacts;
        for (int a = 0; a < arrayList.size(); a++) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(arrayList.get(a).user_id));
            if (user != null && !user.self && !user.deleted) {
                this.contacts.add(user);
            }
        }
        SpansContainer spansContainer = new SpansContainer(context);
        this.spansContainer = spansContainer;
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                InviteMembersBottomSheet.this.m2709lambda$new$0$orgtelegramuiComponentsInviteMembersBottomSheet(chatId, parentFragment, ignoreUsers, context, view, i);
            }
        });
        this.listView.setItemAnimator(new ItemAnimator());
        updateRows();
        ScrollView scrollView = new ScrollView(context) { // from class: org.telegram.ui.Components.InviteMembersBottomSheet.2
            @Override // android.widget.ScrollView, android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = View.MeasureSpec.getSize(widthMeasureSpec);
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                if (AndroidUtilities.isTablet() || height > width) {
                    InviteMembersBottomSheet.this.maxSize = AndroidUtilities.dp(144.0f);
                } else {
                    InviteMembersBottomSheet.this.maxSize = AndroidUtilities.dp(56.0f);
                }
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(InviteMembersBottomSheet.this.maxSize, Integer.MIN_VALUE));
            }
        };
        this.spansScrollView = scrollView;
        scrollView.setVisibility(8);
        scrollView.setClipChildren(false);
        scrollView.addView(spansContainer);
        this.containerView.addView(scrollView);
        ImageView imageView = new ImageView(context);
        this.floatingButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        imageView.setBackgroundDrawable(drawable);
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), PorterDuff.Mode.MULTIPLY));
        imageView.setImageResource(R.drawable.floating_check);
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(imageView, "translationZ", AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
            animator.addState(new int[0], ObjectAnimator.ofFloat(imageView, "translationZ", AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
            imageView.setStateListAnimator(animator);
            imageView.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet.3
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                InviteMembersBottomSheet.this.m2711lambda$new$2$orgtelegramuiComponentsInviteMembersBottomSheet(context, chatId, view);
            }
        });
        imageView.setVisibility(4);
        imageView.setScaleX(0.0f);
        imageView.setScaleY(0.0f);
        imageView.setAlpha(0.0f);
        imageView.setContentDescription(LocaleController.getString("Next", R.string.Next));
        this.containerView.addView(imageView, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT < 21 ? 60 : 56, 85, 14.0f, 14.0f, 14.0f, 14.0f));
        ((ViewGroup.MarginLayoutParams) this.emptyView.getLayoutParams()).topMargin = AndroidUtilities.dp(20.0f);
        ((ViewGroup.MarginLayoutParams) this.emptyView.getLayoutParams()).leftMargin = AndroidUtilities.dp(4.0f);
        ((ViewGroup.MarginLayoutParams) this.emptyView.getLayoutParams()).rightMargin = AndroidUtilities.dp(4.0f);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-InviteMembersBottomSheet */
    public /* synthetic */ void m2709lambda$new$0$orgtelegramuiComponentsInviteMembersBottomSheet(long chatId, BaseFragment parentFragment, LongSparseArray ignoreUsers, Context context, View view, int position) {
        long id;
        TLObject object = null;
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        SearchAdapter searchAdapter = this.searchAdapter;
        if (adapter != searchAdapter) {
            if (position == this.copyLinkRow) {
                TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(chatId));
                TLRPC.ChatFull chatInfo = MessagesController.getInstance(this.currentAccount).getChatFull(chatId);
                String link = null;
                if (chat != null && !TextUtils.isEmpty(chat.username)) {
                    link = "https://t.me/" + chat.username;
                } else if (chatInfo != null && chatInfo.exported_invite != null) {
                    link = chatInfo.exported_invite.link;
                } else {
                    generateLink();
                }
                if (link == null) {
                    return;
                }
                ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
                ClipData clip = ClipData.newPlainText(Constants.ScionAnalytics.PARAM_LABEL, link);
                clipboard.setPrimaryClip(clip);
                dismiss();
                BulletinFactory.createCopyLinkBulletin(parentFragment).show();
            } else if (position >= this.contactsStartRow && position < this.contactsEndRow) {
                object = ((ListAdapter) this.listViewAdapter).getObject(position);
            }
        } else {
            int localCount = searchAdapter.searchResult.size();
            int globalCount = this.searchAdapter.searchAdapterHelper.getGlobalSearch().size();
            int localServerCount = this.searchAdapter.searchAdapterHelper.getLocalServerSearch().size();
            int position2 = position - 1;
            if (position2 < 0 || position2 >= localCount) {
                if (position2 < localCount || position2 >= localServerCount + localCount) {
                    if (position2 > localCount + localServerCount && position2 <= globalCount + localCount + localServerCount) {
                        object = this.searchAdapter.searchAdapterHelper.getGlobalSearch().get(((position2 - localCount) - localServerCount) - 1);
                    }
                } else {
                    object = this.searchAdapter.searchAdapterHelper.getLocalServerSearch().get(position2 - localCount);
                }
            } else {
                object = (TLObject) this.searchAdapter.searchResult.get(position2);
            }
            if (this.dialogsDelegate != null) {
                this.searchView.closeSearch();
            }
        }
        if (object != null) {
            if (object instanceof TLRPC.User) {
                id = ((TLRPC.User) object).id;
            } else if (object instanceof TLRPC.Chat) {
                id = -((TLRPC.Chat) object).id;
            } else {
                id = 0;
            }
            if (ignoreUsers != null && ignoreUsers.indexOfKey(id) >= 0) {
                return;
            }
            if (id != 0) {
                if (this.selectedContacts.indexOfKey(id) >= 0) {
                    this.selectedContacts.remove(id);
                    this.spansContainer.removeSpan(this.selectedContacts.get(id));
                } else {
                    GroupCreateSpan groupCreateSpan = new GroupCreateSpan(context, object);
                    groupCreateSpan.setOnClickListener(this.spanClickListener);
                    this.selectedContacts.put(id, groupCreateSpan);
                    this.spansContainer.addSpan(groupCreateSpan, true);
                }
            }
            spansCountChanged(true);
            AndroidUtilities.updateVisibleRows(this.listView);
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-InviteMembersBottomSheet */
    public /* synthetic */ void m2711lambda$new$2$orgtelegramuiComponentsInviteMembersBottomSheet(Context context, long chatId, View v) {
        Activity activity;
        if ((this.dialogsDelegate == null && this.selectedContacts.size() == 0) || (activity = AndroidUtilities.findActivity(context)) == null) {
            return;
        }
        if (this.dialogsDelegate != null) {
            ArrayList<Long> dialogs = new ArrayList<>();
            for (int a = 0; a < this.selectedContacts.size(); a++) {
                long uid = this.selectedContacts.keyAt(a);
                dialogs.add(Long.valueOf(uid));
            }
            this.dialogsDelegate.didSelectDialogs(dialogs);
            dismiss();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (this.selectedContacts.size() == 1) {
            builder.setTitle(LocaleController.getString("AddOneMemberAlertTitle", R.string.AddOneMemberAlertTitle));
        } else {
            builder.setTitle(LocaleController.formatString("AddMembersAlertTitle", R.string.AddMembersAlertTitle, LocaleController.formatPluralString("Members", this.selectedContacts.size(), new Object[0])));
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int a2 = 0; a2 < this.selectedContacts.size(); a2++) {
            long uid2 = this.selectedContacts.keyAt(a2);
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(uid2));
            if (user != null) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append("**");
                stringBuilder.append(ContactsController.formatName(user.first_name, user.last_name));
                stringBuilder.append("**");
            }
        }
        int a3 = this.currentAccount;
        TLRPC.Chat chat = MessagesController.getInstance(a3).getChat(Long.valueOf(chatId));
        if (this.selectedContacts.size() > 5) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(AndroidUtilities.replaceTags(LocaleController.formatString("AddMembersAlertNamesText", R.string.AddMembersAlertNamesText, LocaleController.formatPluralString("Members", this.selectedContacts.size(), new Object[0]), chat.title)));
            String countString = String.format("%d", Integer.valueOf(this.selectedContacts.size()));
            int index = TextUtils.indexOf(spannableStringBuilder, countString);
            if (index >= 0) {
                spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM)), index, countString.length() + index, 33);
            }
            builder.setMessage(spannableStringBuilder);
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AddMembersAlertNamesText", R.string.AddMembersAlertNamesText, stringBuilder, chat.title)));
        }
        builder.setPositiveButton(LocaleController.getString("Add", R.string.Add), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                InviteMembersBottomSheet.this.m2710lambda$new$1$orgtelegramuiComponentsInviteMembersBottomSheet(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.create();
        builder.show();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-InviteMembersBottomSheet */
    public /* synthetic */ void m2710lambda$new$1$orgtelegramuiComponentsInviteMembersBottomSheet(DialogInterface dialogInterface, int i) {
        onAddToGroupDone(0);
    }

    private void onAddToGroupDone(int i) {
        ArrayList<TLRPC.User> result = new ArrayList<>();
        for (int a = 0; a < this.selectedContacts.size(); a++) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.selectedContacts.keyAt(a)));
            result.add(user);
        }
        GroupCreateActivity.ContactsAddActivityDelegate contactsAddActivityDelegate = this.delegate;
        if (contactsAddActivityDelegate != null) {
            contactsAddActivityDelegate.didSelectUsers(result, i);
        }
        dismiss();
    }

    @Override // org.telegram.ui.Components.UsersAlertBase, org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsNeedReload);
    }

    public void setSelectedContacts(ArrayList<Long> dialogs) {
        int i;
        int width;
        int newAdditionalH;
        TLObject object;
        int a = 0;
        int N = dialogs.size();
        while (true) {
            i = 0;
            if (a >= N) {
                break;
            }
            long dialogId = dialogs.get(a).longValue();
            if (DialogObject.isChatDialog(dialogId)) {
                object = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-dialogId));
            } else {
                object = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(dialogId));
            }
            GroupCreateSpan span = new GroupCreateSpan(this.spansContainer.getContext(), object);
            this.spansContainer.addSpan(span, false);
            span.setOnClickListener(this.spanClickListener);
            a++;
        }
        spansCountChanged(false);
        int count = this.spansContainer.getChildCount();
        boolean isPortrait = AndroidUtilities.displaySize.x < AndroidUtilities.displaySize.y;
        if (AndroidUtilities.isTablet() || isPortrait) {
            this.maxSize = AndroidUtilities.dp(144.0f);
        } else {
            this.maxSize = AndroidUtilities.dp(56.0f);
        }
        if (AndroidUtilities.isTablet()) {
            width = (int) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.8f);
        } else {
            width = AndroidUtilities.displaySize.x;
            if (!isPortrait) {
                width = (int) Math.max(width * 0.8f, Math.min(AndroidUtilities.dp(480.0f), AndroidUtilities.displaySize.x));
            }
        }
        int maxWidth = width - AndroidUtilities.dp(26.0f);
        int currentLineWidth = 0;
        int y = AndroidUtilities.dp(10.0f);
        for (int a2 = 0; a2 < count; a2++) {
            View child = this.spansContainer.getChildAt(a2);
            if (child instanceof GroupCreateSpan) {
                child.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), C.BUFFER_FLAG_ENCRYPTED));
                if (child.getMeasuredWidth() + currentLineWidth > maxWidth) {
                    y += child.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                    currentLineWidth = 0;
                }
                currentLineWidth += child.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
            }
        }
        int animateToH = AndroidUtilities.dp(42.0f) + y;
        if (this.dialogsDelegate != null) {
            newAdditionalH = this.spanEnter ? Math.min(this.maxSize, animateToH) : 0;
        } else {
            newAdditionalH = Math.max(0, Math.min(this.maxSize, animateToH) - AndroidUtilities.dp(52.0f));
        }
        int oldSearchAdditionalH = this.searchAdditionalHeight;
        if (this.selectedContacts.size() > 0) {
            i = AndroidUtilities.dp(56.0f);
        }
        this.searchAdditionalHeight = i;
        if (newAdditionalH != this.additionalHeight || oldSearchAdditionalH != i) {
            this.additionalHeight = newAdditionalH;
        }
    }

    public void spansCountChanged(boolean animated) {
        final boolean enter = this.selectedContacts.size() > 0;
        if (this.spanEnter != enter) {
            ValueAnimator valueAnimator = this.spansEnterAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.spansEnterAnimator.cancel();
            }
            this.spanEnter = enter;
            if (enter) {
                this.spansScrollView.setVisibility(0);
            }
            if (animated) {
                float[] fArr = new float[2];
                fArr[0] = this.spansEnterProgress;
                fArr[1] = enter ? 1.0f : 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.spansEnterAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        InviteMembersBottomSheet.this.m2713x2ee01541(valueAnimator2);
                    }
                });
                this.spansEnterAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet.4
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        InviteMembersBottomSheet.this.spansEnterProgress = enter ? 1.0f : 0.0f;
                        InviteMembersBottomSheet.this.containerView.invalidate();
                        if (!enter) {
                            InviteMembersBottomSheet.this.spansScrollView.setVisibility(8);
                        }
                    }
                });
                this.spansEnterAnimator.setDuration(150L);
                this.spansEnterAnimator.start();
                if (!this.spanEnter && this.dialogsDelegate == null) {
                    AnimatorSet animatorSet = this.currentDoneButtonAnimation;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                    }
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.currentDoneButtonAnimation = animatorSet2;
                    animatorSet2.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 0.0f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 0.0f), ObjectAnimator.ofFloat(this.floatingButton, View.ALPHA, 0.0f));
                    this.currentDoneButtonAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet.5
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            InviteMembersBottomSheet.this.floatingButton.setVisibility(4);
                        }
                    });
                    this.currentDoneButtonAnimation.setDuration(180L);
                    this.currentDoneButtonAnimation.start();
                    return;
                }
                AnimatorSet animatorSet3 = this.currentDoneButtonAnimation;
                if (animatorSet3 != null) {
                    animatorSet3.cancel();
                }
                this.currentDoneButtonAnimation = new AnimatorSet();
                this.floatingButton.setVisibility(0);
                this.currentDoneButtonAnimation.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.floatingButton, View.ALPHA, 1.0f));
                this.currentDoneButtonAnimation.setDuration(180L);
                this.currentDoneButtonAnimation.start();
                return;
            }
            this.spansEnterProgress = enter ? 1.0f : 0.0f;
            this.containerView.invalidate();
            if (!enter) {
                this.spansScrollView.setVisibility(8);
            }
            AnimatorSet animatorSet4 = this.currentDoneButtonAnimation;
            if (animatorSet4 != null) {
                animatorSet4.cancel();
            }
            if (this.spanEnter || this.dialogsDelegate != null) {
                this.floatingButton.setScaleY(1.0f);
                this.floatingButton.setScaleX(1.0f);
                this.floatingButton.setAlpha(1.0f);
                this.floatingButton.setVisibility(0);
                return;
            }
            this.floatingButton.setScaleY(0.0f);
            this.floatingButton.setScaleX(0.0f);
            this.floatingButton.setAlpha(0.0f);
            this.floatingButton.setVisibility(4);
        }
    }

    /* renamed from: lambda$spansCountChanged$3$org-telegram-ui-Components-InviteMembersBottomSheet */
    public /* synthetic */ void m2713x2ee01541(ValueAnimator valueAnimator1) {
        this.spansEnterProgress = ((Float) valueAnimator1.getAnimatedValue()).floatValue();
        this.containerView.invalidate();
    }

    private void updateRows() {
        this.contactsStartRow = -1;
        this.contactsEndRow = -1;
        this.noContactsStubRow = -1;
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.emptyRow = 0;
        if (this.dialogsDelegate == null) {
            this.rowCount = i + 1;
            this.copyLinkRow = i;
            if (this.contacts.size() != 0) {
                int i2 = this.rowCount;
                this.contactsStartRow = i2;
                int size = i2 + this.contacts.size();
                this.rowCount = size;
                this.contactsEndRow = size;
            } else {
                int i3 = this.rowCount;
                this.rowCount = i3 + 1;
                this.noContactsStubRow = i3;
            }
        } else {
            this.copyLinkRow = -1;
            if (this.dialogsServerOnly.size() != 0) {
                int i4 = this.rowCount;
                this.contactsStartRow = i4;
                int size2 = i4 + this.dialogsServerOnly.size();
                this.rowCount = size2;
                this.contactsEndRow = size2;
            } else {
                int i5 = this.rowCount;
                this.rowCount = i5 + 1;
                this.noContactsStubRow = i5;
            }
        }
        int i6 = this.rowCount;
        this.rowCount = i6 + 1;
        this.lastRow = i6;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.dialogsNeedReload && this.dialogsDelegate != null && this.dialogsServerOnly.isEmpty()) {
            this.dialogsServerOnly = new ArrayList<>(MessagesController.getInstance(this.currentAccount).dialogsServerOnly);
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private ListAdapter() {
            InviteMembersBottomSheet.this = r1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            Context context = parent.getContext();
            switch (viewType) {
                case 2:
                    view = new View(context) { // from class: org.telegram.ui.Components.InviteMembersBottomSheet.ListAdapter.1
                        @Override // android.view.View
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f) + InviteMembersBottomSheet.this.additionalHeight, C.BUFFER_FLAG_ENCRYPTED));
                        }
                    };
                    break;
                case 3:
                    view = new GroupCreateUserCell(context, 1, 0, InviteMembersBottomSheet.this.dialogsDelegate != null);
                    break;
                case 4:
                    view = new View(context);
                    break;
                case 5:
                    StickerEmptyView stickerEmptyView = new StickerEmptyView(context, null, 0) { // from class: org.telegram.ui.Components.InviteMembersBottomSheet.ListAdapter.2
                        @Override // org.telegram.ui.Components.StickerEmptyView, android.view.ViewGroup, android.view.View
                        public void onAttachedToWindow() {
                            super.onAttachedToWindow();
                            this.stickerView.getImageReceiver().startAnimation();
                        }
                    };
                    stickerEmptyView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                    stickerEmptyView.subtitle.setVisibility(8);
                    if (InviteMembersBottomSheet.this.dialogsDelegate != null) {
                        stickerEmptyView.title.setText(LocaleController.getString("FilterNoChats", R.string.FilterNoChats));
                    } else {
                        stickerEmptyView.title.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
                    }
                    stickerEmptyView.setAnimateLayoutChange(true);
                    view = stickerEmptyView;
                    break;
                default:
                    ManageChatTextCell manageChatTextCell = new ManageChatTextCell(context);
                    manageChatTextCell.setText(LocaleController.getString("VoipGroupCopyInviteLink", R.string.VoipGroupCopyInviteLink), null, R.drawable.msg_link, 7, true);
                    manageChatTextCell.setColors(Theme.key_dialogTextBlue2, Theme.key_dialogTextBlue2);
                    view = manageChatTextCell;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public TLObject getObject(int position) {
            if (InviteMembersBottomSheet.this.dialogsDelegate != null) {
                TLRPC.Dialog dialog = (TLRPC.Dialog) InviteMembersBottomSheet.this.dialogsServerOnly.get(position - InviteMembersBottomSheet.this.contactsStartRow);
                return DialogObject.isUserDialog(dialog.id) ? MessagesController.getInstance(InviteMembersBottomSheet.this.currentAccount).getUser(Long.valueOf(dialog.id)) : MessagesController.getInstance(InviteMembersBottomSheet.this.currentAccount).getChat(Long.valueOf(-dialog.id));
            }
            return (TLObject) InviteMembersBottomSheet.this.contacts.get(position - InviteMembersBottomSheet.this.contactsStartRow);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            long oldId;
            long id;
            switch (holder.getItemViewType()) {
                case 2:
                    holder.itemView.requestLayout();
                    return;
                case 3:
                    GroupCreateUserCell cell = (GroupCreateUserCell) holder.itemView;
                    TLObject object = getObject(position);
                    Object oldObject = cell.getObject();
                    if (oldObject instanceof TLRPC.User) {
                        oldId = ((TLRPC.User) oldObject).id;
                    } else if (oldObject instanceof TLRPC.Chat) {
                        oldId = -((TLRPC.Chat) oldObject).id;
                    } else {
                        oldId = 0;
                    }
                    boolean z = false;
                    cell.setObject(object, null, null, position != InviteMembersBottomSheet.this.contactsEndRow);
                    if (object instanceof TLRPC.User) {
                        id = ((TLRPC.User) object).id;
                    } else if (object instanceof TLRPC.Chat) {
                        id = -((TLRPC.Chat) object).id;
                    } else {
                        id = 0;
                    }
                    if (id != 0) {
                        if (InviteMembersBottomSheet.this.ignoreUsers == null || InviteMembersBottomSheet.this.ignoreUsers.indexOfKey(id) < 0) {
                            boolean z2 = InviteMembersBottomSheet.this.selectedContacts.indexOfKey(id) >= 0;
                            if (oldId == id) {
                                z = true;
                            }
                            cell.setChecked(z2, z);
                            cell.setCheckBoxEnabled(true);
                            return;
                        }
                        cell.setChecked(true, false);
                        cell.setCheckBoxEnabled(false);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position != InviteMembersBottomSheet.this.copyLinkRow) {
                if (position != InviteMembersBottomSheet.this.emptyRow) {
                    if (position < InviteMembersBottomSheet.this.contactsStartRow || position >= InviteMembersBottomSheet.this.contactsEndRow) {
                        if (position != InviteMembersBottomSheet.this.lastRow) {
                            if (position == InviteMembersBottomSheet.this.noContactsStubRow) {
                                return 5;
                            }
                            return 0;
                        }
                        return 4;
                    }
                    return 3;
                }
                return 2;
            }
            return 1;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 3 || holder.getItemViewType() == 1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return InviteMembersBottomSheet.this.rowCount;
        }
    }

    /* loaded from: classes5.dex */
    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private int currentItemsCount;
        private final SearchAdapterHelper searchAdapterHelper;
        private ArrayList<Object> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;

        public SearchAdapter() {
            InviteMembersBottomSheet.this = r2;
            SearchAdapterHelper searchAdapterHelper = new SearchAdapterHelper(false);
            this.searchAdapterHelper = searchAdapterHelper;
            searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda4
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
                    InviteMembersBottomSheet.SearchAdapter.this.m2714xef2d3d33(i);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-InviteMembersBottomSheet$SearchAdapter */
        public /* synthetic */ void m2714xef2d3d33(int searchId) {
            InviteMembersBottomSheet.this.showItemsAnimated(this.currentItemsCount - 1);
            if (this.searchRunnable == null && !this.searchAdapterHelper.isSearchInProgress() && getItemCount() <= 2) {
                InviteMembersBottomSheet.this.emptyView.showProgress(false, true);
            }
            notifyDataSetChanged();
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            Context context = parent.getContext();
            switch (viewType) {
                case 1:
                    view = new GroupCreateUserCell(context, 1, 0, false);
                    break;
                case 2:
                    view = new View(context) { // from class: org.telegram.ui.Components.InviteMembersBottomSheet.SearchAdapter.1
                        @Override // android.view.View
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f) + InviteMembersBottomSheet.this.additionalHeight + InviteMembersBottomSheet.this.searchAdditionalHeight, C.BUFFER_FLAG_ENCRYPTED));
                        }
                    };
                    break;
                case 3:
                default:
                    view = new GroupCreateSectionCell(context);
                    break;
                case 4:
                    view = new View(context);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:59:0x0125  */
        /* JADX WARN: Removed duplicated region for block: B:60:0x012b  */
        /* JADX WARN: Removed duplicated region for block: B:66:0x013f  */
        /* JADX WARN: Removed duplicated region for block: B:67:0x0145  */
        /* JADX WARN: Removed duplicated region for block: B:73:0x0158  */
        /* JADX WARN: Removed duplicated region for block: B:90:0x019e  */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r19, int r20) {
            /*
                Method dump skipped, instructions count: 450
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InviteMembersBottomSheet.SearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == 0) {
                return 2;
            }
            if (position == this.currentItemsCount - 1) {
                return 4;
            }
            return position + (-1) == this.searchResult.size() + this.searchAdapterHelper.getLocalServerSearch().size() ? 0 : 1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int count = this.searchResult.size();
            int localServerCount = this.searchAdapterHelper.getLocalServerSearch().size();
            int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
            int count2 = count + localServerCount;
            if (globalCount != 0) {
                count2 += globalCount + 1;
            }
            int count3 = count2 + 2;
            this.currentItemsCount = count3;
            return count3;
        }

        private void updateSearchResults(final ArrayList<Object> users, final ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    InviteMembersBottomSheet.SearchAdapter.this.m2718x989c1db9(users, names);
                }
            });
        }

        /* renamed from: lambda$updateSearchResults$1$org-telegram-ui-Components-InviteMembersBottomSheet$SearchAdapter */
        public /* synthetic */ void m2718x989c1db9(ArrayList users, ArrayList names) {
            this.searchRunnable = null;
            this.searchResult = users;
            this.searchResultNames = names;
            this.searchAdapterHelper.mergeResults(users);
            InviteMembersBottomSheet.this.showItemsAnimated(this.currentItemsCount - 1);
            notifyDataSetChanged();
            if (!this.searchAdapterHelper.isSearchInProgress() && getItemCount() <= 2) {
                InviteMembersBottomSheet.this.emptyView.showProgress(false, true);
            }
        }

        public void searchDialogs(final String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            this.searchResult.clear();
            this.searchResultNames.clear();
            this.searchAdapterHelper.mergeResults(null);
            this.searchAdapterHelper.queryServerSearch(null, true, false, false, false, false, 0L, false, 0, 0);
            notifyDataSetChanged();
            if (!TextUtils.isEmpty(query)) {
                if (InviteMembersBottomSheet.this.listView.getAdapter() != InviteMembersBottomSheet.this.searchListViewAdapter) {
                    InviteMembersBottomSheet.this.listView.setAdapter(InviteMembersBottomSheet.this.searchListViewAdapter);
                }
                InviteMembersBottomSheet.this.emptyView.showProgress(true, false);
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        InviteMembersBottomSheet.SearchAdapter.this.m2717x84772ba(query);
                    }
                };
                this.searchRunnable = runnable;
                dispatchQueue.postRunnable(runnable, 300L);
            } else if (InviteMembersBottomSheet.this.listView.getAdapter() != InviteMembersBottomSheet.this.listViewAdapter) {
                InviteMembersBottomSheet.this.listView.setAdapter(InviteMembersBottomSheet.this.listViewAdapter);
            }
        }

        /* renamed from: lambda$searchDialogs$4$org-telegram-ui-Components-InviteMembersBottomSheet$SearchAdapter */
        public /* synthetic */ void m2717x84772ba(final String query) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    InviteMembersBottomSheet.SearchAdapter.this.m2716x2c85f6f9(query);
                }
            });
        }

        /* renamed from: lambda$searchDialogs$3$org-telegram-ui-Components-InviteMembersBottomSheet$SearchAdapter */
        public /* synthetic */ void m2716x2c85f6f9(final String query) {
            this.searchAdapterHelper.queryServerSearch(query, true, InviteMembersBottomSheet.this.dialogsDelegate != null, true, InviteMembersBottomSheet.this.dialogsDelegate != null, false, 0L, false, 0, 0);
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    InviteMembersBottomSheet.SearchAdapter.this.m2715x50c47b38(query);
                }
            };
            this.searchRunnable = runnable;
            dispatchQueue.postRunnable(runnable);
        }

        /* JADX WARN: Code restructure failed: missing block: B:38:0x00d4, code lost:
            if (r12.contains(" " + r3) != false) goto L44;
         */
        /* JADX WARN: Removed duplicated region for block: B:54:0x0137 A[LOOP:1: B:29:0x0096->B:54:0x0137, LOOP_END] */
        /* JADX WARN: Removed duplicated region for block: B:62:0x00e8 A[SYNTHETIC] */
        /* renamed from: lambda$searchDialogs$2$org-telegram-ui-Components-InviteMembersBottomSheet$SearchAdapter */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public /* synthetic */ void m2715x50c47b38(java.lang.String r19) {
            /*
                Method dump skipped, instructions count: 333
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InviteMembersBottomSheet.SearchAdapter.m2715x50c47b38(java.lang.String):void");
        }
    }

    @Override // org.telegram.ui.Components.UsersAlertBase
    protected void onSearchViewTouched(MotionEvent ev, final EditTextBoldCursor searchEditText) {
        if (ev.getAction() == 0) {
            this.y = this.scrollOffsetY;
        } else if (ev.getAction() == 1 && Math.abs(this.scrollOffsetY - this.y) < this.touchSlop && !this.enterEventSent) {
            Activity activity = AndroidUtilities.findActivity(getContext());
            BaseFragment fragment = null;
            if (activity instanceof LaunchActivity) {
                BaseFragment fragment2 = ((LaunchActivity) activity).getActionBarLayout().fragmentsStack.get(((LaunchActivity) activity).getActionBarLayout().fragmentsStack.size() - 1);
                fragment = fragment2;
            }
            if (fragment instanceof ChatActivity) {
                boolean keyboardVisible = ((ChatActivity) fragment).needEnterText();
                this.enterEventSent = true;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet$$ExternalSyntheticLambda6
                    @Override // java.lang.Runnable
                    public final void run() {
                        InviteMembersBottomSheet.this.m2712xf72b33a7(searchEditText);
                    }
                }, keyboardVisible ? 200L : 0L);
                return;
            }
            this.enterEventSent = true;
            setFocusable(true);
            searchEditText.requestFocus();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    AndroidUtilities.showKeyboard(EditTextBoldCursor.this);
                }
            });
        }
    }

    /* renamed from: lambda$onSearchViewTouched$5$org-telegram-ui-Components-InviteMembersBottomSheet */
    public /* synthetic */ void m2712xf72b33a7(final EditTextBoldCursor searchEditText) {
        setFocusable(true);
        searchEditText.requestFocus();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                AndroidUtilities.showKeyboard(EditTextBoldCursor.this);
            }
        });
    }

    /* loaded from: classes5.dex */
    public class SpansContainer extends ViewGroup {
        boolean addAnimation;
        private boolean animationStarted;
        private View removingSpan;
        private ArrayList<Animator> animators = new ArrayList<>();
        private int animationIndex = -1;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public SpansContainer(Context context) {
            super(context);
            InviteMembersBottomSheet.this = r1;
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            RecyclerView.ViewHolder holder;
            int count = getChildCount();
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int maxWidth = width - AndroidUtilities.dp(26.0f);
            int currentLineWidth = 0;
            int y = AndroidUtilities.dp(10.0f);
            int allCurrentLineWidth = 0;
            int allY = AndroidUtilities.dp(10.0f);
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                if (child instanceof GroupCreateSpan) {
                    child.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), C.BUFFER_FLAG_ENCRYPTED));
                    if (child != this.removingSpan && child.getMeasuredWidth() + currentLineWidth > maxWidth) {
                        y += child.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                        currentLineWidth = 0;
                    }
                    if (child.getMeasuredWidth() + allCurrentLineWidth > maxWidth) {
                        allY += child.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                        allCurrentLineWidth = 0;
                    }
                    int x = AndroidUtilities.dp(13.0f) + currentLineWidth;
                    if (!this.animationStarted) {
                        View view = this.removingSpan;
                        if (child == view) {
                            child.setTranslationX(AndroidUtilities.dp(13.0f) + allCurrentLineWidth);
                            child.setTranslationY(allY);
                        } else if (view != null) {
                            if (child.getTranslationX() != x) {
                                this.animators.add(ObjectAnimator.ofFloat(child, View.TRANSLATION_X, x));
                            }
                            if (child.getTranslationY() != y) {
                                this.animators.add(ObjectAnimator.ofFloat(child, View.TRANSLATION_Y, y));
                            }
                        } else {
                            child.setTranslationX(x);
                            child.setTranslationY(y);
                        }
                    }
                    if (child != this.removingSpan) {
                        currentLineWidth += child.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                    }
                    allCurrentLineWidth += child.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                }
            }
            int h = AndroidUtilities.dp(42.0f) + allY;
            final int animateToH = AndroidUtilities.dp(42.0f) + y;
            int newAdditionalH = InviteMembersBottomSheet.this.dialogsDelegate != null ? InviteMembersBottomSheet.this.spanEnter ? Math.min(InviteMembersBottomSheet.this.maxSize, animateToH) : 0 : Math.max(0, Math.min(InviteMembersBottomSheet.this.maxSize, animateToH) - AndroidUtilities.dp(52.0f));
            int oldSearchAdditionalH = InviteMembersBottomSheet.this.searchAdditionalHeight;
            InviteMembersBottomSheet inviteMembersBottomSheet = InviteMembersBottomSheet.this;
            inviteMembersBottomSheet.searchAdditionalHeight = (inviteMembersBottomSheet.dialogsDelegate != null || InviteMembersBottomSheet.this.selectedContacts.size() <= 0) ? 0 : AndroidUtilities.dp(56.0f);
            if (newAdditionalH != InviteMembersBottomSheet.this.additionalHeight || oldSearchAdditionalH != InviteMembersBottomSheet.this.searchAdditionalHeight) {
                InviteMembersBottomSheet.this.additionalHeight = newAdditionalH;
                if (InviteMembersBottomSheet.this.listView.getAdapter() != null && InviteMembersBottomSheet.this.listView.getAdapter().getItemCount() > 0 && (holder = InviteMembersBottomSheet.this.listView.findViewHolderForAdapterPosition(0)) != null) {
                    InviteMembersBottomSheet.this.listView.getAdapter().notifyItemChanged(0);
                    InviteMembersBottomSheet.this.layoutManager.scrollToPositionWithOffset(0, holder.itemView.getTop() - InviteMembersBottomSheet.this.listView.getPaddingTop());
                }
            }
            int newSize = Math.min(InviteMembersBottomSheet.this.maxSize, animateToH);
            if (InviteMembersBottomSheet.this.scrollViewH != newSize) {
                ValueAnimator valueAnimator = ValueAnimator.ofInt(InviteMembersBottomSheet.this.scrollViewH, newSize);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet$SpansContainer$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        InviteMembersBottomSheet.SpansContainer.this.m2719x155bedad(valueAnimator2);
                    }
                });
                this.animators.add(valueAnimator);
            }
            if (this.addAnimation && animateToH > InviteMembersBottomSheet.this.maxSize) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet$SpansContainer$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        InviteMembersBottomSheet.SpansContainer.this.m2720xb1c9ea0c(animateToH);
                    }
                });
            } else if (!this.addAnimation && InviteMembersBottomSheet.this.spansScrollView.getScrollY() + InviteMembersBottomSheet.this.spansScrollView.getMeasuredHeight() > animateToH) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet$SpansContainer$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        InviteMembersBottomSheet.SpansContainer.this.m2721x4e37e66b(animateToH);
                    }
                });
            }
            if (!this.animationStarted && InviteMembersBottomSheet.this.currentAnimation != null) {
                InviteMembersBottomSheet.this.currentAnimation.playTogether(this.animators);
                InviteMembersBottomSheet.this.currentAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet.SpansContainer.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        InviteMembersBottomSheet.this.currentAnimation = null;
                        SpansContainer.this.requestLayout();
                    }
                });
                InviteMembersBottomSheet.this.currentAnimation.start();
                this.animationStarted = true;
            }
            if (InviteMembersBottomSheet.this.currentAnimation == null) {
                InviteMembersBottomSheet.this.scrollViewH = newSize;
                InviteMembersBottomSheet.this.containerView.invalidate();
            }
            setMeasuredDimension(width, Math.max(animateToH, h));
            InviteMembersBottomSheet.this.listView.setTranslationY(0.0f);
        }

        /* renamed from: lambda$onMeasure$0$org-telegram-ui-Components-InviteMembersBottomSheet$SpansContainer */
        public /* synthetic */ void m2719x155bedad(ValueAnimator valueAnimator1) {
            InviteMembersBottomSheet.this.scrollViewH = ((Integer) valueAnimator1.getAnimatedValue()).intValue();
            InviteMembersBottomSheet.this.containerView.invalidate();
        }

        /* renamed from: lambda$onMeasure$1$org-telegram-ui-Components-InviteMembersBottomSheet$SpansContainer */
        public /* synthetic */ void m2720xb1c9ea0c(int animateToH) {
            InviteMembersBottomSheet.this.spansScrollView.smoothScrollTo(0, animateToH - InviteMembersBottomSheet.this.maxSize);
        }

        /* renamed from: lambda$onMeasure$2$org-telegram-ui-Components-InviteMembersBottomSheet$SpansContainer */
        public /* synthetic */ void m2721x4e37e66b(int animateToH) {
            InviteMembersBottomSheet.this.spansScrollView.smoothScrollTo(0, animateToH - InviteMembersBottomSheet.this.maxSize);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int count = getChildCount();
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            }
        }

        public void addSpan(GroupCreateSpan span, boolean animated) {
            this.addAnimation = true;
            InviteMembersBottomSheet.this.selectedContacts.put(span.getUid(), span);
            if (InviteMembersBottomSheet.this.currentAnimation != null) {
                InviteMembersBottomSheet.this.currentAnimation.setupEndValues();
                InviteMembersBottomSheet.this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            if (animated) {
                InviteMembersBottomSheet.this.currentAnimation = new AnimatorSet();
                InviteMembersBottomSheet.this.currentAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet.SpansContainer.2
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        InviteMembersBottomSheet.this.currentAnimation = null;
                        SpansContainer.this.animationStarted = false;
                    }
                });
                InviteMembersBottomSheet.this.currentAnimation.setDuration(150L);
                InviteMembersBottomSheet.this.currentAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.animators.clear();
                this.animators.add(ObjectAnimator.ofFloat(span, View.SCALE_X, 0.01f, 1.0f));
                this.animators.add(ObjectAnimator.ofFloat(span, View.SCALE_Y, 0.01f, 1.0f));
                this.animators.add(ObjectAnimator.ofFloat(span, View.ALPHA, 0.0f, 1.0f));
            }
            addView(span);
        }

        public void removeSpan(final GroupCreateSpan span) {
            this.addAnimation = false;
            InviteMembersBottomSheet.this.selectedContacts.remove(span.getUid());
            span.setOnClickListener(null);
            if (InviteMembersBottomSheet.this.currentAnimation != null) {
                InviteMembersBottomSheet.this.currentAnimation.setupEndValues();
                InviteMembersBottomSheet.this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            InviteMembersBottomSheet.this.currentAnimation = new AnimatorSet();
            InviteMembersBottomSheet.this.currentAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet.SpansContainer.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.removeView(span);
                    SpansContainer.this.removingSpan = null;
                    InviteMembersBottomSheet.this.currentAnimation = null;
                    SpansContainer.this.animationStarted = false;
                }
            });
            InviteMembersBottomSheet.this.currentAnimation.setDuration(150L);
            this.removingSpan = span;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.SCALE_X, 1.0f, 0.01f));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.SCALE_Y, 1.0f, 0.01f));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.ALPHA, 1.0f, 0.0f));
            requestLayout();
        }
    }

    @Override // org.telegram.ui.Components.UsersAlertBase
    protected UsersAlertBase.ContainerView createContainerView(Context context) {
        return new UsersAlertBase.ContainerView(context) { // from class: org.telegram.ui.Components.InviteMembersBottomSheet.6
            float animateToEmptyViewOffset;
            float deltaOffset;
            float emptyViewOffset;
            Paint paint = new Paint();
            private VerticalPositionAutoAnimator verticalPositionAutoAnimator;

            @Override // android.view.ViewGroup
            public void onViewAdded(View child) {
                if (child == InviteMembersBottomSheet.this.floatingButton && this.verticalPositionAutoAnimator == null) {
                    this.verticalPositionAutoAnimator = VerticalPositionAutoAnimator.attach(child);
                }
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                VerticalPositionAutoAnimator verticalPositionAutoAnimator = this.verticalPositionAutoAnimator;
                if (verticalPositionAutoAnimator != null) {
                    verticalPositionAutoAnimator.ignoreNextLayout();
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.UsersAlertBase.ContainerView, android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                int y = (InviteMembersBottomSheet.this.scrollOffsetY - InviteMembersBottomSheet.this.backgroundPaddingTop) + AndroidUtilities.dp(6.0f);
                InviteMembersBottomSheet.this.spansScrollView.setTranslationY(AndroidUtilities.dp(64.0f) + y);
                float newEmptyViewOffset = InviteMembersBottomSheet.this.additionalHeight + InviteMembersBottomSheet.this.searchAdditionalHeight;
                if (InviteMembersBottomSheet.this.emptyView.getVisibility() != 0) {
                    this.emptyViewOffset = newEmptyViewOffset;
                    this.animateToEmptyViewOffset = newEmptyViewOffset;
                } else if (this.animateToEmptyViewOffset != newEmptyViewOffset) {
                    this.animateToEmptyViewOffset = newEmptyViewOffset;
                    this.deltaOffset = (newEmptyViewOffset - this.emptyViewOffset) * 0.10666667f;
                }
                float f = this.emptyViewOffset;
                float f2 = this.animateToEmptyViewOffset;
                if (f != f2) {
                    float f3 = this.deltaOffset;
                    float f4 = f + f3;
                    this.emptyViewOffset = f4;
                    if (f3 > 0.0f && f4 > f2) {
                        this.emptyViewOffset = f2;
                    } else if (f3 < 0.0f && f4 < f2) {
                        this.emptyViewOffset = f2;
                    } else {
                        invalidate();
                    }
                }
                InviteMembersBottomSheet.this.emptyView.setTranslationY(InviteMembersBottomSheet.this.scrollOffsetY + this.emptyViewOffset);
                super.dispatchDraw(canvas);
            }

            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (child == InviteMembersBottomSheet.this.spansScrollView) {
                    canvas.save();
                    canvas.clipRect(0.0f, child.getY() - AndroidUtilities.dp(4.0f), getMeasuredWidth(), child.getY() + InviteMembersBottomSheet.this.scrollViewH + 1.0f);
                    canvas.drawColor(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_windowBackgroundWhite), (int) (InviteMembersBottomSheet.this.spansEnterProgress * 255.0f)));
                    this.paint.setColor(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_divider), (int) (InviteMembersBottomSheet.this.spansEnterProgress * 255.0f)));
                    canvas.drawRect(0.0f, child.getY() + InviteMembersBottomSheet.this.scrollViewH, getMeasuredWidth(), child.getY() + InviteMembersBottomSheet.this.scrollViewH + 1.0f, this.paint);
                    boolean rez = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return rez;
                }
                boolean rez2 = super.drawChild(canvas, child, drawingTime);
                return rez2;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.Components.UsersAlertBase
    public void search(String text) {
        this.searchAdapter.searchDialogs(text);
    }

    public void setDelegate(GroupCreateActivity.ContactsAddActivityDelegate contactsAddActivityDelegate) {
        this.delegate = contactsAddActivityDelegate;
    }

    public void setDelegate(InviteMembersBottomSheetDelegate inviteMembersBottomSheetDelegate, ArrayList<Long> selectedDialogs) {
        this.dialogsDelegate = inviteMembersBottomSheetDelegate;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogsNeedReload);
        this.dialogsServerOnly = new ArrayList<>(MessagesController.getInstance(this.currentAccount).dialogsServerOnly);
        updateRows();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class ItemAnimator extends DefaultItemAnimator {
        public ItemAnimator() {
            InviteMembersBottomSheet.this = r3;
            this.translationInterpolator = CubicBezierInterpolator.DEFAULT;
            setMoveDuration(150L);
            setAddDuration(150L);
            setRemoveDuration(150L);
            r3.setShowWithoutAnimation(false);
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void dismissInternal() {
        super.dismissInternal();
        if (this.enterEventSent) {
            Activity activity = AndroidUtilities.findActivity(getContext());
            if (activity instanceof LaunchActivity) {
                BaseFragment fragment = ((LaunchActivity) activity).getActionBarLayout().fragmentsStack.get(((LaunchActivity) activity).getActionBarLayout().fragmentsStack.size() - 1);
                if (fragment instanceof ChatActivity) {
                    ((ChatActivity) fragment).onEditTextDialogClose(true, true);
                }
            }
        }
    }

    private void generateLink() {
        if (this.linkGenerating) {
            return;
        }
        this.linkGenerating = true;
        TLRPC.TL_messages_exportChatInvite req = new TLRPC.TL_messages_exportChatInvite();
        req.legacy_revoke_permanent = true;
        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(-this.chatId);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet$$ExternalSyntheticLambda7
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                InviteMembersBottomSheet.this.m2708xedf00341(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$generateLink$8$org-telegram-ui-Components-InviteMembersBottomSheet */
    public /* synthetic */ void m2708xedf00341(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InviteMembersBottomSheet$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                InviteMembersBottomSheet.this.m2707x60b551c0(error, response);
            }
        });
    }

    /* renamed from: lambda$generateLink$7$org-telegram-ui-Components-InviteMembersBottomSheet */
    public /* synthetic */ void m2707x60b551c0(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            this.invite = (TLRPC.TL_chatInviteExported) response;
            TLRPC.ChatFull chatInfo = MessagesController.getInstance(this.currentAccount).getChatFull(this.chatId);
            if (chatInfo != null) {
                chatInfo.exported_invite = this.invite;
            }
            if (this.invite.link == null) {
                return;
            }
            ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
            ClipData clip = ClipData.newPlainText(Constants.ScionAnalytics.PARAM_LABEL, this.invite.link);
            clipboard.setPrimaryClip(clip);
            BulletinFactory.createCopyLinkBulletin(this.parentFragment).show();
            dismiss();
        }
        this.linkGenerating = false;
    }
}
