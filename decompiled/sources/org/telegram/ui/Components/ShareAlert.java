package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.arch.core.util.Function;
import androidx.collection.LongSparseArray;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.google.firebase.messaging.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.ShareDialogCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.MessageStatisticActivity;
/* loaded from: classes5.dex */
public class ShareAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private AnimatorSet animatorSet;
    private float captionEditTextTopOffset;
    private float chatActivityEnterViewAnimateFromTop;
    private EditTextEmoji commentTextView;
    private int containerViewTop;
    private boolean copyLinkOnEnd;
    private float currentPanTranslationY;
    private boolean darkTheme;
    private ShareAlertDelegate delegate;
    private TLRPC.TL_exportedMessageLink exportedMessageLink;
    private FrameLayout frameLayout;
    private FrameLayout frameLayout2;
    private RecyclerListView gridView;
    private int hasPoll;
    private boolean isChannel;
    int lastOffset;
    private GridLayoutManager layoutManager;
    private String[] linkToCopy;
    private ShareDialogsAdapter listAdapter;
    private boolean loadingLink;
    private Paint paint;
    private boolean panTranslationMoveLayout;
    private Activity parentActivity;
    private ChatActivity parentFragment;
    private TextView pickerBottomLayout;
    private int previousScrollOffsetY;
    private ArrayList<DialogsSearchAdapter.RecentSearchObject> recentSearchObjects;
    private LongSparseArray<DialogsSearchAdapter.RecentSearchObject> recentSearchObjectsById;
    private RectF rect;
    RecyclerItemsEnterAnimator recyclerItemsEnterAnimator;
    private final Theme.ResourcesProvider resourcesProvider;
    private int scrollOffsetY;
    private ShareSearchAdapter searchAdapter;
    private StickerEmptyView searchEmptyView;
    private RecyclerListView searchGridView;
    private boolean searchIsVisible;
    private FillLastGridLayoutManager searchLayoutManager;
    SearchField searchView;
    private View selectedCountView;
    protected LongSparseArray<TLRPC.Dialog> selectedDialogs;
    private ActionBarPopupWindow sendPopupWindow;
    protected ArrayList<MessageObject> sendingMessageObjects;
    private String[] sendingText;
    private View[] shadow;
    private AnimatorSet[] shadowAnimation;
    private Drawable shadowDrawable;
    private LinearLayout sharesCountLayout;
    private boolean showSendersName;
    private SwitchView switchView;
    private TextPaint textPaint;
    private ValueAnimator topBackgroundAnimator;
    private int topBeforeSwitch;
    private boolean updateSearchAdapter;
    private FrameLayout writeButtonContainer;

    /* loaded from: classes5.dex */
    public static class DialogSearchResult {
        public int date;
        public TLRPC.Dialog dialog = new TLRPC.TL_dialog();
        public CharSequence name;
        public TLObject object;
    }

    /* loaded from: classes5.dex */
    public interface ShareAlertDelegate {
        boolean didCopy();

        void didShare();

        /* renamed from: org.telegram.ui.Components.ShareAlert$ShareAlertDelegate$-CC */
        /* loaded from: classes5.dex */
        public final /* synthetic */ class CC {
            public static void $default$didShare(ShareAlertDelegate _this) {
            }

            public static boolean $default$didCopy(ShareAlertDelegate _this) {
                return false;
            }
        }
    }

    /* loaded from: classes5.dex */
    public class SwitchView extends FrameLayout {
        private AnimatorSet animator;
        private int currentTab;
        private int lastColor;
        private SimpleTextView leftTab;
        private LinearGradient linearGradient;
        private Paint paint = new Paint(1);
        private RectF rect = new RectF();
        private SimpleTextView rightTab;
        private View searchBackground;
        private View slidingView;
        private float tabSwitchProgress;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public SwitchView(Context context) {
            super(context);
            ShareAlert.this = r12;
            View view = new View(context);
            this.searchBackground = view;
            view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), r12.getThemedColor(r12.darkTheme ? Theme.key_voipgroup_searchBackground : Theme.key_dialogSearchBackground)));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 0.0f, 14.0f, 0.0f));
            View view2 = new View(context) { // from class: org.telegram.ui.Components.ShareAlert.SwitchView.1
                {
                    SwitchView.this = this;
                }

                @Override // android.view.View
                public void setTranslationX(float translationX) {
                    super.setTranslationX(translationX);
                    invalidate();
                }

                @Override // android.view.View
                protected void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    int color0 = AndroidUtilities.getOffsetColor(-9057429, -10513163, getTranslationX() / getMeasuredWidth(), 1.0f);
                    int color1 = AndroidUtilities.getOffsetColor(-11554882, -4629871, getTranslationX() / getMeasuredWidth(), 1.0f);
                    if (color0 != SwitchView.this.lastColor) {
                        SwitchView.this.linearGradient = new LinearGradient(0.0f, 0.0f, getMeasuredWidth(), 0.0f, new int[]{color0, color1}, (float[]) null, Shader.TileMode.CLAMP);
                        SwitchView.this.paint.setShader(SwitchView.this.linearGradient);
                    }
                    SwitchView.this.rect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                    canvas.drawRoundRect(SwitchView.this.rect, AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), SwitchView.this.paint);
                }
            };
            this.slidingView = view2;
            addView(view2, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 0.0f, 14.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.leftTab = simpleTextView;
            simpleTextView.setTextColor(r12.getThemedColor(Theme.key_voipgroup_nameText));
            this.leftTab.setTextSize(13);
            this.leftTab.setLeftDrawable(R.drawable.msg_tabs_mic1);
            this.leftTab.setText(LocaleController.getString("VoipGroupInviteCanSpeak", R.string.VoipGroupInviteCanSpeak));
            this.leftTab.setGravity(17);
            addView(this.leftTab, LayoutHelper.createFrame(-1, -1.0f, 51, 14.0f, 0.0f, 0.0f, 0.0f));
            this.leftTab.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ShareAlert$SwitchView$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view3) {
                    ShareAlert.SwitchView.this.m3023lambda$new$0$orgtelegramuiComponentsShareAlert$SwitchView(view3);
                }
            });
            SimpleTextView simpleTextView2 = new SimpleTextView(context);
            this.rightTab = simpleTextView2;
            simpleTextView2.setTextColor(r12.getThemedColor(Theme.key_voipgroup_nameText));
            this.rightTab.setTextSize(13);
            this.rightTab.setLeftDrawable(R.drawable.msg_tabs_mic2);
            this.rightTab.setText(LocaleController.getString("VoipGroupInviteListenOnly", R.string.VoipGroupInviteListenOnly));
            this.rightTab.setGravity(17);
            addView(this.rightTab, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 14.0f, 0.0f));
            this.rightTab.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ShareAlert$SwitchView$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view3) {
                    ShareAlert.SwitchView.this.m3024lambda$new$1$orgtelegramuiComponentsShareAlert$SwitchView(view3);
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-ShareAlert$SwitchView */
        public /* synthetic */ void m3023lambda$new$0$orgtelegramuiComponentsShareAlert$SwitchView(View v) {
            switchToTab(0);
        }

        /* renamed from: lambda$new$1$org-telegram-ui-Components-ShareAlert$SwitchView */
        public /* synthetic */ void m3024lambda$new$1$orgtelegramuiComponentsShareAlert$SwitchView(View v) {
            switchToTab(1);
        }

        protected void onTabSwitch(int num) {
        }

        private void switchToTab(int tab) {
            if (this.currentTab == tab) {
                return;
            }
            this.currentTab = tab;
            AnimatorSet animatorSet = this.animator;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.animator = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            View view = this.slidingView;
            Property property = View.TRANSLATION_X;
            float[] fArr = new float[1];
            fArr[0] = this.currentTab == 0 ? 0.0f : this.slidingView.getMeasuredWidth();
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.animator.setDuration(180L);
            this.animator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ShareAlert.SwitchView.2
                {
                    SwitchView.this = this;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    SwitchView.this.animator = null;
                }
            });
            this.animator.start();
            onTabSwitch(this.currentTab);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(28.0f);
            ((FrameLayout.LayoutParams) this.leftTab.getLayoutParams()).width = width / 2;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.rightTab.getLayoutParams();
            layoutParams.width = width / 2;
            layoutParams.leftMargin = (width / 2) + AndroidUtilities.dp(14.0f);
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.slidingView.getLayoutParams();
            layoutParams2.width = width / 2;
            AnimatorSet animatorSet = this.animator;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.slidingView.setTranslationX(this.currentTab == 0 ? 0.0f : layoutParams2.width);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /* loaded from: classes5.dex */
    public class SearchField extends FrameLayout {
        private View backgroundView;
        private ImageView clearSearchImageView;
        private CloseProgressDrawable2 progressDrawable;
        private View searchBackground;
        private EditTextBoldCursor searchEditText;
        private ImageView searchIconImageView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public SearchField(Context context) {
            super(context);
            ShareAlert.this = r9;
            View view = new View(context);
            this.searchBackground = view;
            view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), r9.getThemedColor(r9.darkTheme ? Theme.key_voipgroup_searchBackground : Theme.key_dialogSearchBackground)));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 11.0f, 14.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.searchIconImageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.searchIconImageView.setImageResource(R.drawable.smiles_inputsearch);
            this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(r9.getThemedColor(r9.darkTheme ? Theme.key_voipgroup_mutedIcon : Theme.key_dialogSearchIcon), PorterDuff.Mode.MULTIPLY));
            addView(this.searchIconImageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 11.0f, 0.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.clearSearchImageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            ImageView imageView3 = this.clearSearchImageView;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2() { // from class: org.telegram.ui.Components.ShareAlert.SearchField.1
                {
                    SearchField.this = this;
                }

                @Override // org.telegram.ui.Components.CloseProgressDrawable2
                protected int getCurrentColor() {
                    return ShareAlert.this.getThemedColor(ShareAlert.this.darkTheme ? Theme.key_voipgroup_searchPlaceholder : Theme.key_dialogSearchIcon);
                }
            };
            this.progressDrawable = closeProgressDrawable2;
            imageView3.setImageDrawable(closeProgressDrawable2);
            this.progressDrawable.setSide(AndroidUtilities.dp(7.0f));
            this.clearSearchImageView.setScaleX(0.1f);
            this.clearSearchImageView.setScaleY(0.1f);
            this.clearSearchImageView.setAlpha(0.0f);
            addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f));
            this.clearSearchImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ShareAlert$SearchField$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ShareAlert.SearchField.this.m3016lambda$new$0$orgtelegramuiComponentsShareAlert$SearchField(view2);
                }
            });
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
            this.searchEditText = editTextBoldCursor;
            editTextBoldCursor.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(r9.getThemedColor(r9.darkTheme ? Theme.key_voipgroup_searchPlaceholder : Theme.key_dialogSearchHint));
            EditTextBoldCursor editTextBoldCursor2 = this.searchEditText;
            boolean z = r9.darkTheme;
            String str = Theme.key_voipgroup_searchText;
            editTextBoldCursor2.setTextColor(r9.getThemedColor(z ? str : Theme.key_dialogSearchText));
            this.searchEditText.setBackgroundDrawable(null);
            this.searchEditText.setPadding(0, 0, 0, 0);
            this.searchEditText.setMaxLines(1);
            this.searchEditText.setLines(1);
            this.searchEditText.setSingleLine(true);
            this.searchEditText.setImeOptions(268435459);
            this.searchEditText.setHint(LocaleController.getString("ShareSendTo", R.string.ShareSendTo));
            this.searchEditText.setCursorColor(r9.getThemedColor(!r9.darkTheme ? Theme.key_featuredStickers_addedIcon : str));
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 9.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.Components.ShareAlert.SearchField.2
                {
                    SearchField.this = this;
                }

                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable s) {
                    boolean show = SearchField.this.searchEditText.length() > 0;
                    float f = 0.0f;
                    boolean showed = SearchField.this.clearSearchImageView.getAlpha() != 0.0f;
                    if (show != showed) {
                        ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                        float f2 = 1.0f;
                        if (show) {
                            f = 1.0f;
                        }
                        ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150L).scaleX(show ? 1.0f : 0.1f);
                        if (!show) {
                            f2 = 0.1f;
                        }
                        scaleX.scaleY(f2).start();
                    }
                    if (!TextUtils.isEmpty(SearchField.this.searchEditText.getText())) {
                        ShareAlert.this.checkCurrentList(false);
                    }
                    if (ShareAlert.this.updateSearchAdapter) {
                        String text = SearchField.this.searchEditText.getText().toString();
                        if (text.length() != 0) {
                            if (ShareAlert.this.searchEmptyView != null) {
                                ShareAlert.this.searchEmptyView.title.setText(LocaleController.getString("NoResult", R.string.NoResult));
                            }
                        } else if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.listAdapter) {
                            int top = ShareAlert.this.getCurrentTop();
                            ShareAlert.this.searchEmptyView.title.setText(LocaleController.getString("NoResult", R.string.NoResult));
                            ShareAlert.this.searchEmptyView.showProgress(false, true);
                            ShareAlert.this.checkCurrentList(false);
                            ShareAlert.this.listAdapter.notifyDataSetChanged();
                            if (top > 0) {
                                ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -top);
                            }
                        }
                        if (ShareAlert.this.searchAdapter != null) {
                            ShareAlert.this.searchAdapter.searchDialogs(text);
                        }
                    }
                }
            });
            this.searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.Components.ShareAlert$SearchField$$ExternalSyntheticLambda1
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return ShareAlert.SearchField.this.m3017lambda$new$1$orgtelegramuiComponentsShareAlert$SearchField(textView, i, keyEvent);
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-ShareAlert$SearchField */
        public /* synthetic */ void m3016lambda$new$0$orgtelegramuiComponentsShareAlert$SearchField(View v) {
            ShareAlert.this.updateSearchAdapter = true;
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        /* renamed from: lambda$new$1$org-telegram-ui-Components-ShareAlert$SearchField */
        public /* synthetic */ boolean m3017lambda$new$1$orgtelegramuiComponentsShareAlert$SearchField(TextView v, int actionId, KeyEvent event) {
            if (event != null) {
                if ((event.getAction() == 1 && event.getKeyCode() == 84) || (event.getAction() == 0 && event.getKeyCode() == 66)) {
                    AndroidUtilities.hideKeyboard(this.searchEditText);
                    return false;
                }
                return false;
            }
            return false;
        }

        public void hideKeyboard() {
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }
    }

    public static ShareAlert createShareAlert(Context context, MessageObject messageObject, String text, boolean channel, String copyLink, boolean fullScreen) {
        ArrayList<MessageObject> arrayList;
        if (messageObject != null) {
            arrayList = new ArrayList<>();
            arrayList.add(messageObject);
        } else {
            arrayList = null;
        }
        return new ShareAlert(context, null, arrayList, text, null, channel, copyLink, null, fullScreen, false);
    }

    public ShareAlert(Context context, ArrayList<MessageObject> messages, String text, boolean channel, String copyLink, boolean fullScreen) {
        this(context, messages, text, channel, copyLink, fullScreen, null);
    }

    public ShareAlert(Context context, ArrayList<MessageObject> messages, String text, boolean channel, String copyLink, boolean fullScreen, Theme.ResourcesProvider resourcesProvider) {
        this(context, null, messages, text, null, channel, copyLink, null, fullScreen, false, resourcesProvider);
    }

    public ShareAlert(Context context, ChatActivity fragment, ArrayList<MessageObject> messages, String text, String text2, boolean channel, String copyLink, String copyLink2, boolean fullScreen, boolean forCall) {
        this(context, fragment, messages, text, text2, channel, copyLink, copyLink2, fullScreen, forCall, null);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ShareAlert(final Context context, ChatActivity fragment, ArrayList<MessageObject> messages, String text, String text2, boolean channel, String copyLink, String copyLink2, boolean fullScreen, boolean forCall, Theme.ResourcesProvider resourcesProvider) {
        super(context, true, resourcesProvider);
        FrameLayout.LayoutParams frameLayoutParams;
        int i = 1;
        this.sendingText = new String[2];
        this.shadow = new View[2];
        this.shadowAnimation = new AnimatorSet[2];
        this.selectedDialogs = new LongSparseArray<>();
        this.containerViewTop = -1;
        this.rect = new RectF();
        this.paint = new Paint(1);
        this.textPaint = new TextPaint(1);
        this.linkToCopy = new String[2];
        this.recentSearchObjects = new ArrayList<>();
        this.recentSearchObjectsById = new LongSparseArray<>();
        this.showSendersName = true;
        this.lastOffset = Integer.MAX_VALUE;
        this.resourcesProvider = resourcesProvider;
        if (context instanceof Activity) {
            this.parentActivity = (Activity) context;
        }
        this.darkTheme = forCall;
        this.parentFragment = fragment;
        this.shadowDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        String str = this.darkTheme ? Theme.key_voipgroup_inviteMembersBackground : Theme.key_dialogBackground;
        this.behindKeyboardColorKey = str;
        int backgroundColor = getThemedColor(str);
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(backgroundColor, PorterDuff.Mode.MULTIPLY));
        fixNavigationBar(backgroundColor);
        this.isFullscreen = fullScreen;
        String[] strArr = this.linkToCopy;
        strArr[0] = copyLink;
        strArr[1] = copyLink2;
        this.sendingMessageObjects = messages;
        this.searchAdapter = new ShareSearchAdapter(context);
        this.isChannel = channel;
        String[] strArr2 = this.sendingText;
        strArr2[0] = text;
        strArr2[1] = text2;
        this.useSmoothKeyboard = true;
        ArrayList<MessageObject> arrayList = this.sendingMessageObjects;
        if (arrayList != null) {
            int N = arrayList.size();
            int a = 0;
            while (a < N) {
                MessageObject messageObject = this.sendingMessageObjects.get(a);
                if (messageObject.isPoll()) {
                    i = messageObject.isPublicPoll() ? 2 : i;
                    this.hasPoll = i;
                    if (i == 2) {
                        break;
                    }
                }
                a++;
                i = 1;
            }
        }
        if (channel) {
            this.loadingLink = true;
            TLRPC.TL_channels_exportMessageLink req = new TLRPC.TL_channels_exportMessageLink();
            req.id = messages.get(0).getId();
            req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(messages.get(0).messageOwner.peer_id.channel_id);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda2
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ShareAlert.this.m3000lambda$new$1$orgtelegramuiComponentsShareAlert(context, tLObject, tL_error);
                }
            });
        }
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context) { // from class: org.telegram.ui.Components.ShareAlert.1
            private int fromOffsetTop;
            private int fromScrollY;
            private boolean fullHeight;
            private boolean lightStatusBar;
            private int previousTopOffset;
            private int toOffsetTop;
            private int toScrollY;
            private int topOffset;
            private boolean ignoreLayout = false;
            private RectF rect1 = new RectF();
            AdjustPanLayoutHelper adjustPanLayoutHelper = new AdjustPanLayoutHelper(this) { // from class: org.telegram.ui.Components.ShareAlert.1.1
                {
                    AnonymousClass1.this = this;
                }

                @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
                public void onTransitionStart(boolean keyboardVisible, int contentHeight) {
                    super.onTransitionStart(keyboardVisible, contentHeight);
                    if (ShareAlert.this.previousScrollOffsetY == ShareAlert.this.scrollOffsetY) {
                        AnonymousClass1.this.fromScrollY = -1;
                    } else {
                        AnonymousClass1 anonymousClass1 = AnonymousClass1.this;
                        anonymousClass1.fromScrollY = ShareAlert.this.previousScrollOffsetY;
                        AnonymousClass1 anonymousClass12 = AnonymousClass1.this;
                        anonymousClass12.toScrollY = ShareAlert.this.scrollOffsetY;
                        ShareAlert.this.panTranslationMoveLayout = true;
                        ShareAlert.this.scrollOffsetY = AnonymousClass1.this.fromScrollY;
                    }
                    if (AnonymousClass1.this.topOffset != AnonymousClass1.this.previousTopOffset) {
                        AnonymousClass1.this.fromOffsetTop = 0;
                        AnonymousClass1.this.toOffsetTop = 0;
                        ShareAlert.this.panTranslationMoveLayout = true;
                        if (!keyboardVisible) {
                            AnonymousClass1 anonymousClass13 = AnonymousClass1.this;
                            AnonymousClass1.access$3320(anonymousClass13, anonymousClass13.topOffset - AnonymousClass1.this.previousTopOffset);
                        } else {
                            AnonymousClass1 anonymousClass14 = AnonymousClass1.this;
                            AnonymousClass1.access$3312(anonymousClass14, anonymousClass14.topOffset - AnonymousClass1.this.previousTopOffset);
                        }
                        ShareAlert shareAlert = ShareAlert.this;
                        AnonymousClass1 anonymousClass15 = AnonymousClass1.this;
                        shareAlert.scrollOffsetY = keyboardVisible ? anonymousClass15.fromScrollY : anonymousClass15.toScrollY;
                    } else {
                        AnonymousClass1.this.fromOffsetTop = -1;
                    }
                    ShareAlert.this.gridView.setTopGlowOffset((int) (ShareAlert.this.currentPanTranslationY + ShareAlert.this.scrollOffsetY));
                    ShareAlert.this.frameLayout.setTranslationY(ShareAlert.this.currentPanTranslationY + ShareAlert.this.scrollOffsetY);
                    ShareAlert.this.searchEmptyView.setTranslationY(ShareAlert.this.currentPanTranslationY + ShareAlert.this.scrollOffsetY);
                    invalidate();
                }

                @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
                public void onTransitionEnd() {
                    super.onTransitionEnd();
                    ShareAlert.this.panTranslationMoveLayout = false;
                    ShareAlert.this.previousScrollOffsetY = ShareAlert.this.scrollOffsetY;
                    ShareAlert.this.gridView.setTopGlowOffset(ShareAlert.this.scrollOffsetY);
                    ShareAlert.this.frameLayout.setTranslationY(ShareAlert.this.scrollOffsetY);
                    ShareAlert.this.searchEmptyView.setTranslationY(ShareAlert.this.scrollOffsetY);
                    ShareAlert.this.gridView.setTranslationY(0.0f);
                    ShareAlert.this.searchGridView.setTranslationY(0.0f);
                }

                @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
                public void onPanTranslationUpdate(float y, float progress, boolean keyboardVisible) {
                    super.onPanTranslationUpdate(y, progress, keyboardVisible);
                    for (int i2 = 0; i2 < ShareAlert.this.containerView.getChildCount(); i2++) {
                        if (ShareAlert.this.containerView.getChildAt(i2) != ShareAlert.this.pickerBottomLayout && ShareAlert.this.containerView.getChildAt(i2) != ShareAlert.this.shadow[1] && ShareAlert.this.containerView.getChildAt(i2) != ShareAlert.this.sharesCountLayout && ShareAlert.this.containerView.getChildAt(i2) != ShareAlert.this.frameLayout2 && ShareAlert.this.containerView.getChildAt(i2) != ShareAlert.this.writeButtonContainer && ShareAlert.this.containerView.getChildAt(i2) != ShareAlert.this.selectedCountView) {
                            ShareAlert.this.containerView.getChildAt(i2).setTranslationY(y);
                        }
                    }
                    ShareAlert.this.currentPanTranslationY = y;
                    if (AnonymousClass1.this.fromScrollY == -1) {
                        if (AnonymousClass1.this.fromOffsetTop != -1) {
                            ShareAlert.this.scrollOffsetY = (int) ((AnonymousClass1.this.fromOffsetTop * (1.0f - progress)) + (AnonymousClass1.this.toOffsetTop * progress));
                            float p = keyboardVisible ? 1.0f - progress : progress;
                            if (keyboardVisible) {
                                ShareAlert.this.gridView.setTranslationY(ShareAlert.this.currentPanTranslationY - ((AnonymousClass1.this.fromOffsetTop - AnonymousClass1.this.toOffsetTop) * progress));
                            } else {
                                ShareAlert.this.gridView.setTranslationY(ShareAlert.this.currentPanTranslationY + ((AnonymousClass1.this.toOffsetTop - AnonymousClass1.this.fromOffsetTop) * p));
                            }
                        }
                    } else {
                        float p2 = keyboardVisible ? progress : 1.0f - progress;
                        ShareAlert.this.scrollOffsetY = (int) ((AnonymousClass1.this.fromScrollY * (1.0f - p2)) + (AnonymousClass1.this.toScrollY * p2));
                        float translationY = ShareAlert.this.currentPanTranslationY + ((AnonymousClass1.this.fromScrollY - AnonymousClass1.this.toScrollY) * (1.0f - p2));
                        ShareAlert.this.gridView.setTranslationY(translationY);
                        if (keyboardVisible) {
                            ShareAlert.this.searchGridView.setTranslationY(translationY);
                        } else {
                            ShareAlert.this.searchGridView.setTranslationY(ShareAlert.this.gridView.getPaddingTop() + translationY);
                        }
                    }
                    ShareAlert.this.gridView.setTopGlowOffset((int) (ShareAlert.this.scrollOffsetY + ShareAlert.this.currentPanTranslationY));
                    ShareAlert.this.frameLayout.setTranslationY(ShareAlert.this.scrollOffsetY + ShareAlert.this.currentPanTranslationY);
                    ShareAlert.this.searchEmptyView.setTranslationY(ShareAlert.this.scrollOffsetY + ShareAlert.this.currentPanTranslationY);
                    ShareAlert.this.frameLayout2.invalidate();
                    ShareAlert.this.setCurrentPanTranslationY(ShareAlert.this.currentPanTranslationY);
                    invalidate();
                }

                @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
                protected boolean heightAnimationEnabled() {
                    if (!ShareAlert.this.isDismissed()) {
                        return !ShareAlert.this.commentTextView.isPopupVisible();
                    }
                    return false;
                }
            };

            {
                ShareAlert.this = this;
                boolean z = false;
                this.lightStatusBar = AndroidUtilities.computePerceivedBrightness(this.getThemedColor(this.darkTheme ? Theme.key_voipgroup_inviteMembersBackground : Theme.key_dialogBackground)) > 0.721f ? true : z;
            }

            static /* synthetic */ int access$3312(AnonymousClass1 x0, int x1) {
                int i2 = x0.toOffsetTop + x1;
                x0.toOffsetTop = i2;
                return i2;
            }

            static /* synthetic */ int access$3320(AnonymousClass1 x0, int x1) {
                int i2 = x0.toOffsetTop - x1;
                x0.toOffsetTop = i2;
                return i2;
            }

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.view.ViewGroup, android.view.View
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                this.adjustPanLayoutHelper.setResizableView(this);
                this.adjustPanLayoutHelper.onAttach();
            }

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.view.ViewGroup, android.view.View
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                this.adjustPanLayoutHelper.onDetach();
            }

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int totalHeight;
                if (getLayoutParams().height > 0) {
                    totalHeight = getLayoutParams().height;
                } else {
                    totalHeight = View.MeasureSpec.getSize(heightMeasureSpec);
                }
                ShareAlert.this.layoutManager.setNeedFixGap(getLayoutParams().height <= 0);
                ShareAlert.this.searchLayoutManager.setNeedFixGap(getLayoutParams().height <= 0);
                if (Build.VERSION.SDK_INT >= 21 && !ShareAlert.this.isFullscreen) {
                    this.ignoreLayout = true;
                    setPadding(ShareAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, ShareAlert.this.backgroundPaddingLeft, 0);
                    this.ignoreLayout = false;
                }
                int availableHeight = totalHeight - getPaddingTop();
                int size = Math.max(ShareAlert.this.searchAdapter.getItemCount(), ShareAlert.this.listAdapter.getItemCount() - 1);
                int contentSize = AndroidUtilities.dp(103.0f) + AndroidUtilities.dp(48.0f) + (Math.max(2, (int) Math.ceil(size / 4.0f)) * AndroidUtilities.dp(103.0f)) + ShareAlert.this.backgroundPaddingTop;
                int padding = (contentSize < availableHeight ? 0 : availableHeight - ((availableHeight / 5) * 3)) + AndroidUtilities.dp(8.0f);
                if (ShareAlert.this.gridView.getPaddingTop() != padding) {
                    this.ignoreLayout = true;
                    ShareAlert.this.gridView.setPadding(0, padding, 0, AndroidUtilities.dp(48.0f));
                    this.ignoreLayout = false;
                }
                if (ShareAlert.this.keyboardVisible && getLayoutParams().height <= 0 && ShareAlert.this.searchGridView.getPaddingTop() != padding) {
                    this.ignoreLayout = true;
                    ShareAlert.this.searchGridView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
                    this.ignoreLayout = false;
                }
                boolean z = contentSize >= totalHeight;
                this.fullHeight = z;
                this.topOffset = (z || !SharedConfig.smoothKeyboard) ? 0 : totalHeight - contentSize;
                this.ignoreLayout = true;
                ShareAlert.this.checkCurrentList(false);
                this.ignoreLayout = false;
                setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), totalHeight);
                onMeasureInternal(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(totalHeight, C.BUFFER_FLAG_ENCRYPTED));
            }

            private void onMeasureInternal(int widthMeasureSpec, int heightMeasureSpec) {
                int heightSize;
                int heightMeasureSpec2;
                int heightMeasureSpec3;
                int paddingBottom;
                int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                int heightSize2 = View.MeasureSpec.getSize(heightMeasureSpec);
                int widthSize2 = widthSize - (ShareAlert.this.backgroundPaddingLeft * 2);
                int keyboardSize = SharedConfig.smoothKeyboard ? 0 : measureKeyboardHeight();
                if (!ShareAlert.this.commentTextView.isWaitingForKeyboardOpen() && keyboardSize <= AndroidUtilities.dp(20.0f) && !ShareAlert.this.commentTextView.isPopupShowing() && !ShareAlert.this.commentTextView.isAnimatePopupClosing()) {
                    this.ignoreLayout = true;
                    ShareAlert.this.commentTextView.hideEmojiView();
                    this.ignoreLayout = false;
                }
                this.ignoreLayout = true;
                if (keyboardSize > AndroidUtilities.dp(20.0f)) {
                    ShareAlert.this.commentTextView.hideEmojiView();
                    if (ShareAlert.this.pickerBottomLayout != null) {
                        ShareAlert.this.pickerBottomLayout.setVisibility(8);
                        if (ShareAlert.this.sharesCountLayout != null) {
                            ShareAlert.this.sharesCountLayout.setVisibility(8);
                        }
                    }
                    heightMeasureSpec2 = heightMeasureSpec;
                    heightSize = heightSize2;
                } else {
                    if (AndroidUtilities.isInMultiwindow) {
                        heightMeasureSpec3 = heightMeasureSpec;
                    } else {
                        if (!SharedConfig.smoothKeyboard || !ShareAlert.this.keyboardVisible) {
                            paddingBottom = ShareAlert.this.commentTextView.getEmojiPadding();
                        } else {
                            paddingBottom = 0;
                        }
                        heightSize2 -= paddingBottom;
                        heightMeasureSpec3 = View.MeasureSpec.makeMeasureSpec(heightSize2, C.BUFFER_FLAG_ENCRYPTED);
                    }
                    int visibility = ShareAlert.this.commentTextView.isPopupShowing() ? 8 : 0;
                    if (ShareAlert.this.pickerBottomLayout != null) {
                        ShareAlert.this.pickerBottomLayout.setVisibility(visibility);
                        if (ShareAlert.this.sharesCountLayout != null) {
                            ShareAlert.this.sharesCountLayout.setVisibility(visibility);
                        }
                    }
                    heightSize = heightSize2;
                    heightMeasureSpec2 = heightMeasureSpec3;
                }
                this.ignoreLayout = false;
                int childCount = getChildCount();
                for (int i2 = 0; i2 < childCount; i2++) {
                    View child = getChildAt(i2);
                    if (child != null && child.getVisibility() != 8) {
                        if (ShareAlert.this.commentTextView != null && ShareAlert.this.commentTextView.isPopupView(child)) {
                            if (AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) {
                                if (AndroidUtilities.isTablet()) {
                                    child.measure(View.MeasureSpec.makeMeasureSpec(widthSize2, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (heightSize - AndroidUtilities.statusBarHeight) + getPaddingTop()), C.BUFFER_FLAG_ENCRYPTED));
                                } else {
                                    child.measure(View.MeasureSpec.makeMeasureSpec(widthSize2, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((heightSize - AndroidUtilities.statusBarHeight) + getPaddingTop(), C.BUFFER_FLAG_ENCRYPTED));
                                }
                            } else {
                                child.measure(View.MeasureSpec.makeMeasureSpec(widthSize2, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, C.BUFFER_FLAG_ENCRYPTED));
                            }
                        } else {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec2, 0);
                        }
                    }
                }
            }

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int paddingBottom;
                int childLeft;
                int childTop;
                int count = getChildCount();
                int keyboardSize = measureKeyboardHeight();
                if (SharedConfig.smoothKeyboard && ShareAlert.this.keyboardVisible) {
                    paddingBottom = 0;
                } else {
                    paddingBottom = (keyboardSize > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) ? 0 : ShareAlert.this.commentTextView.getEmojiPadding();
                }
                setBottomClip(paddingBottom);
                for (int i2 = 0; i2 < count; i2++) {
                    View child = getChildAt(i2);
                    if (child.getVisibility() != 8) {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int absoluteGravity = gravity & 7;
                        int verticalGravity = gravity & 112;
                        switch (absoluteGravity & 7) {
                            case 1:
                                childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                                break;
                            case 5:
                                childLeft = ((((r - l) - width) - lp.rightMargin) - getPaddingRight()) - ShareAlert.this.backgroundPaddingLeft;
                                break;
                            default:
                                childLeft = lp.leftMargin + getPaddingLeft();
                                break;
                        }
                        switch (verticalGravity) {
                            case 16:
                                childTop = (((((b - paddingBottom) - (t + this.topOffset)) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                                break;
                            case 48:
                                childTop = lp.topMargin + getPaddingTop() + this.topOffset;
                                break;
                            case UndoView.ACTION_EMAIL_COPIED /* 80 */:
                                childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                                break;
                            default:
                                childTop = lp.topMargin;
                                break;
                        }
                        if (ShareAlert.this.commentTextView != null && ShareAlert.this.commentTextView.isPopupView(child)) {
                            if (AndroidUtilities.isTablet()) {
                                childTop = getMeasuredHeight() - child.getMeasuredHeight();
                            } else {
                                childTop = (getMeasuredHeight() + keyboardSize) - child.getMeasuredHeight();
                            }
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                notifyHeightChanged();
                ShareAlert.this.updateLayout();
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (!this.fullHeight) {
                    if (ev.getAction() == 0 && ev.getY() < this.topOffset - AndroidUtilities.dp(30.0f)) {
                        ShareAlert.this.dismiss();
                        return true;
                    }
                } else if (ev.getAction() == 0 && ShareAlert.this.scrollOffsetY != 0 && ev.getY() < ShareAlert.this.scrollOffsetY - AndroidUtilities.dp(30.0f)) {
                    ShareAlert.this.dismiss();
                    return true;
                }
                return super.onInterceptTouchEvent(ev);
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent e) {
                return !ShareAlert.this.isDismissed() && super.onTouchEvent(e);
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                int flags;
                canvas.save();
                canvas.translate(0.0f, ShareAlert.this.currentPanTranslationY);
                int y = (ShareAlert.this.scrollOffsetY - ShareAlert.this.backgroundPaddingTop) + AndroidUtilities.dp(6.0f) + this.topOffset;
                ShareAlert shareAlert = ShareAlert.this;
                int top = shareAlert.containerViewTop = ((shareAlert.scrollOffsetY - ShareAlert.this.backgroundPaddingTop) - AndroidUtilities.dp(13.0f)) + this.topOffset;
                int height = getMeasuredHeight() + AndroidUtilities.dp(60.0f) + ShareAlert.this.backgroundPaddingTop;
                int statusBarHeight = 0;
                float radProgress = 1.0f;
                if (!ShareAlert.this.isFullscreen && Build.VERSION.SDK_INT >= 21) {
                    top += AndroidUtilities.statusBarHeight;
                    y += AndroidUtilities.statusBarHeight;
                    height -= AndroidUtilities.statusBarHeight;
                    if (this.fullHeight) {
                        if (ShareAlert.this.backgroundPaddingTop + top < AndroidUtilities.statusBarHeight * 2) {
                            int diff = Math.min(AndroidUtilities.statusBarHeight, ((AndroidUtilities.statusBarHeight * 2) - top) - ShareAlert.this.backgroundPaddingTop);
                            top -= diff;
                            height += diff;
                            radProgress = 1.0f - Math.min(1.0f, (diff * 2) / AndroidUtilities.statusBarHeight);
                        }
                        if (ShareAlert.this.backgroundPaddingTop + top < AndroidUtilities.statusBarHeight) {
                            statusBarHeight = Math.min(AndroidUtilities.statusBarHeight, (AndroidUtilities.statusBarHeight - top) - ShareAlert.this.backgroundPaddingTop);
                        }
                    }
                }
                boolean isLightStatusBar = false;
                ShareAlert.this.shadowDrawable.setBounds(0, top, getMeasuredWidth(), height);
                ShareAlert.this.shadowDrawable.draw(canvas);
                if (radProgress != 1.0f) {
                    Paint paint = Theme.dialogs_onlineCirclePaint;
                    ShareAlert shareAlert2 = ShareAlert.this;
                    paint.setColor(shareAlert2.getThemedColor(shareAlert2.darkTheme ? Theme.key_voipgroup_inviteMembersBackground : Theme.key_dialogBackground));
                    this.rect1.set(ShareAlert.this.backgroundPaddingLeft, ShareAlert.this.backgroundPaddingTop + top, getMeasuredWidth() - ShareAlert.this.backgroundPaddingLeft, ShareAlert.this.backgroundPaddingTop + top + AndroidUtilities.dp(24.0f));
                    canvas.drawRoundRect(this.rect1, AndroidUtilities.dp(12.0f) * radProgress, AndroidUtilities.dp(12.0f) * radProgress, Theme.dialogs_onlineCirclePaint);
                }
                int w = AndroidUtilities.dp(36.0f);
                this.rect1.set((getMeasuredWidth() - w) / 2, y, (getMeasuredWidth() + w) / 2, AndroidUtilities.dp(4.0f) + y);
                Paint paint2 = Theme.dialogs_onlineCirclePaint;
                ShareAlert shareAlert3 = ShareAlert.this;
                paint2.setColor(shareAlert3.getThemedColor(shareAlert3.darkTheme ? Theme.key_voipgroup_scrollUp : Theme.key_sheet_scrollUp));
                canvas.drawRoundRect(this.rect1, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                if (Build.VERSION.SDK_INT >= 23) {
                    int flags2 = getSystemUiVisibility();
                    boolean shouldBeLightStatusBar = this.lightStatusBar && ((float) statusBarHeight) > ((float) AndroidUtilities.statusBarHeight) * 0.5f;
                    if ((flags2 & 8192) > 0) {
                        isLightStatusBar = true;
                    }
                    if (shouldBeLightStatusBar != isLightStatusBar) {
                        if (shouldBeLightStatusBar) {
                            flags = flags2 | 8192;
                        } else {
                            flags = flags2 & (-8193);
                        }
                        setSystemUiVisibility(flags);
                    }
                }
                canvas.restore();
                this.previousTopOffset = this.topOffset;
            }

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                canvas.save();
                canvas.clipRect(0.0f, getPaddingTop() + ShareAlert.this.currentPanTranslationY, getMeasuredWidth(), getMeasuredHeight() + ShareAlert.this.currentPanTranslationY + AndroidUtilities.dp(50.0f));
                super.dispatchDraw(canvas);
                canvas.restore();
            }
        };
        this.containerView = sizeNotifierFrameLayout;
        this.containerView.setWillNotDraw(false);
        this.containerView.setClipChildren(false);
        this.containerView.setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
        FrameLayout frameLayout = new FrameLayout(context);
        this.frameLayout = frameLayout;
        frameLayout.setBackgroundColor(getThemedColor(this.darkTheme ? Theme.key_voipgroup_inviteMembersBackground : Theme.key_dialogBackground));
        if (this.darkTheme && this.linkToCopy[1] != null) {
            SwitchView switchView = new SwitchView(context) { // from class: org.telegram.ui.Components.ShareAlert.2
                {
                    ShareAlert.this = this;
                }

                @Override // org.telegram.ui.Components.ShareAlert.SwitchView
                protected void onTabSwitch(int num) {
                    if (ShareAlert.this.pickerBottomLayout == null) {
                        return;
                    }
                    if (num == 0) {
                        ShareAlert.this.pickerBottomLayout.setText(LocaleController.getString("VoipGroupCopySpeakerLink", R.string.VoipGroupCopySpeakerLink).toUpperCase());
                    } else {
                        ShareAlert.this.pickerBottomLayout.setText(LocaleController.getString("VoipGroupCopyListenLink", R.string.VoipGroupCopyListenLink).toUpperCase());
                    }
                }
            };
            this.switchView = switchView;
            this.frameLayout.addView(switchView, LayoutHelper.createFrame(-1, 36.0f, 51, 0.0f, 11.0f, 0.0f, 0.0f));
        }
        SearchField searchField = new SearchField(context);
        this.searchView = searchField;
        this.frameLayout.addView(searchField, LayoutHelper.createFrame(-1, 58, 83));
        RecyclerListView recyclerListView = new RecyclerListView(context, resourcesProvider) { // from class: org.telegram.ui.Components.ShareAlert.3
            {
                ShareAlert.this = this;
            }

            @Override // org.telegram.ui.Components.RecyclerListView
            protected boolean allowSelectChildAtPosition(float x, float y) {
                return y >= ((float) (AndroidUtilities.dp((!ShareAlert.this.darkTheme || ShareAlert.this.linkToCopy[1] == null) ? 58.0f : 111.0f) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
            }
        };
        this.gridView = recyclerListView;
        recyclerListView.setSelectorDrawableColor(0);
        this.gridView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
        this.gridView.setClipToPadding(false);
        RecyclerListView recyclerListView2 = this.gridView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        this.layoutManager = gridLayoutManager;
        recyclerListView2.setLayoutManager(gridLayoutManager);
        this.layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.ShareAlert.4
            {
                ShareAlert.this = this;
            }

            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int position) {
                if (position == 0) {
                    return ShareAlert.this.layoutManager.getSpanCount();
                }
                return 1;
            }
        });
        this.gridView.setHorizontalScrollBarEnabled(false);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: org.telegram.ui.Components.ShareAlert.5
            {
                ShareAlert.this = this;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(android.graphics.Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                RecyclerListView.Holder holder = (RecyclerListView.Holder) parent.getChildViewHolder(view);
                if (holder != null) {
                    int pos = holder.getAdapterPosition();
                    int i2 = 0;
                    outRect.left = pos % 4 == 0 ? 0 : AndroidUtilities.dp(4.0f);
                    if (pos % 4 != 3) {
                        i2 = AndroidUtilities.dp(4.0f);
                    }
                    outRect.right = i2;
                    return;
                }
                outRect.left = AndroidUtilities.dp(4.0f);
                outRect.right = AndroidUtilities.dp(4.0f);
            }
        });
        this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        RecyclerListView recyclerListView3 = this.gridView;
        ShareDialogsAdapter shareDialogsAdapter = new ShareDialogsAdapter(context);
        this.listAdapter = shareDialogsAdapter;
        recyclerListView3.setAdapter(shareDialogsAdapter);
        this.gridView.setGlowColor(getThemedColor(this.darkTheme ? Theme.key_voipgroup_inviteMembersBackground : Theme.key_dialogScrollGlow));
        this.gridView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda5
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i2) {
                ShareAlert.this.m3001lambda$new$2$orgtelegramuiComponentsShareAlert(view, i2);
            }
        });
        this.gridView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.ShareAlert.6
            {
                ShareAlert.this = this;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy != 0) {
                    ShareAlert.this.updateLayout();
                    ShareAlert shareAlert = ShareAlert.this;
                    shareAlert.previousScrollOffsetY = shareAlert.scrollOffsetY;
                }
            }
        });
        RecyclerListView recyclerListView4 = new RecyclerListView(context, resourcesProvider) { // from class: org.telegram.ui.Components.ShareAlert.7
            {
                ShareAlert.this = this;
            }

            @Override // org.telegram.ui.Components.RecyclerListView
            protected boolean allowSelectChildAtPosition(float x, float y) {
                return y >= ((float) (AndroidUtilities.dp((!ShareAlert.this.darkTheme || ShareAlert.this.linkToCopy[1] == null) ? 58.0f : 111.0f) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
            }
        };
        this.searchGridView = recyclerListView4;
        recyclerListView4.setSelectorDrawableColor(0);
        this.searchGridView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
        this.searchGridView.setClipToPadding(false);
        RecyclerListView recyclerListView5 = this.searchGridView;
        FillLastGridLayoutManager fillLastGridLayoutManager = new FillLastGridLayoutManager(getContext(), 4, 0, this.searchGridView);
        this.searchLayoutManager = fillLastGridLayoutManager;
        recyclerListView5.setLayoutManager(fillLastGridLayoutManager);
        this.searchLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.ShareAlert.8
            {
                ShareAlert.this = this;
            }

            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int position) {
                return ShareAlert.this.searchAdapter.getSpanSize(4, position);
            }
        });
        this.searchGridView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda6
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i2) {
                ShareAlert.this.m3002lambda$new$3$orgtelegramuiComponentsShareAlert(view, i2);
            }
        });
        this.searchGridView.setHasFixedSize(true);
        this.searchGridView.setItemAnimator(null);
        this.searchGridView.setHorizontalScrollBarEnabled(false);
        this.searchGridView.setVerticalScrollBarEnabled(false);
        this.searchGridView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.ShareAlert.9
            {
                ShareAlert.this = this;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy != 0) {
                    ShareAlert.this.updateLayout();
                    ShareAlert shareAlert = ShareAlert.this;
                    shareAlert.previousScrollOffsetY = shareAlert.scrollOffsetY;
                }
            }
        });
        this.searchGridView.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: org.telegram.ui.Components.ShareAlert.10
            {
                ShareAlert.this = this;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(android.graphics.Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                RecyclerListView.Holder holder = (RecyclerListView.Holder) parent.getChildViewHolder(view);
                if (holder != null) {
                    int pos = holder.getAdapterPosition();
                    int i2 = 0;
                    outRect.left = pos % 4 == 0 ? 0 : AndroidUtilities.dp(4.0f);
                    if (pos % 4 != 3) {
                        i2 = AndroidUtilities.dp(4.0f);
                    }
                    outRect.right = i2;
                    return;
                }
                outRect.left = AndroidUtilities.dp(4.0f);
                outRect.right = AndroidUtilities.dp(4.0f);
            }
        });
        this.searchGridView.setAdapter(this.searchAdapter);
        this.searchGridView.setGlowColor(getThemedColor(this.darkTheme ? Theme.key_voipgroup_inviteMembersBackground : Theme.key_dialogScrollGlow));
        this.recyclerItemsEnterAnimator = new RecyclerItemsEnterAnimator(this.searchGridView, true);
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context, resourcesProvider);
        flickerLoadingView.setViewType(12);
        if (this.darkTheme) {
            flickerLoadingView.setColors(Theme.key_voipgroup_inviteMembersBackground, Theme.key_voipgroup_searchBackground, null);
        }
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context, flickerLoadingView, 1, resourcesProvider);
        this.searchEmptyView = stickerEmptyView;
        stickerEmptyView.addView(flickerLoadingView, 0);
        this.searchEmptyView.setAnimateLayoutChange(true);
        this.searchEmptyView.showProgress(false, false);
        if (this.darkTheme) {
            this.searchEmptyView.title.setTextColor(getThemedColor(Theme.key_voipgroup_nameText));
        }
        this.searchEmptyView.title.setText(LocaleController.getString("NoResult", R.string.NoResult));
        this.searchGridView.setEmptyView(this.searchEmptyView);
        this.searchGridView.setHideIfEmpty(false);
        this.searchGridView.setAnimateEmptyView(true, 0);
        this.containerView.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 52.0f, 0.0f, 0.0f));
        this.containerView.addView(this.searchGridView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        FrameLayout.LayoutParams frameLayoutParams2 = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        frameLayoutParams2.topMargin = AndroidUtilities.dp((!this.darkTheme || this.linkToCopy[1] == null) ? 58.0f : 111.0f);
        this.shadow[0] = new View(context);
        this.shadow[0].setBackgroundColor(getThemedColor(Theme.key_dialogShadowLine));
        this.shadow[0].setAlpha(0.0f);
        this.shadow[0].setTag(1);
        this.containerView.addView(this.shadow[0], frameLayoutParams2);
        this.containerView.addView(this.frameLayout, LayoutHelper.createFrame(-1, (!this.darkTheme || this.linkToCopy[1] == null) ? 58 : 111, 51));
        FrameLayout.LayoutParams frameLayoutParams3 = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83);
        frameLayoutParams3.bottomMargin = AndroidUtilities.dp(48.0f);
        this.shadow[1] = new View(context);
        this.shadow[1].setBackgroundColor(getThemedColor(Theme.key_dialogShadowLine));
        this.containerView.addView(this.shadow[1], frameLayoutParams3);
        if (this.isChannel || this.linkToCopy[0] != null) {
            TextView textView = new TextView(context);
            this.pickerBottomLayout = textView;
            textView.setBackgroundDrawable(Theme.createSelectorWithBackgroundDrawable(getThemedColor(this.darkTheme ? Theme.key_voipgroup_inviteMembersBackground : Theme.key_dialogBackground), getThemedColor(this.darkTheme ? Theme.key_voipgroup_listSelector : Theme.key_listSelector)));
            this.pickerBottomLayout.setTextColor(getThemedColor(this.darkTheme ? Theme.key_voipgroup_listeningText : Theme.key_dialogTextBlue2));
            this.pickerBottomLayout.setTextSize(1, 14.0f);
            this.pickerBottomLayout.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            this.pickerBottomLayout.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.pickerBottomLayout.setGravity(17);
            if (this.darkTheme && this.linkToCopy[1] != null) {
                this.pickerBottomLayout.setText(LocaleController.getString("VoipGroupCopySpeakerLink", R.string.VoipGroupCopySpeakerLink).toUpperCase());
            } else {
                this.pickerBottomLayout.setText(LocaleController.getString("CopyLink", R.string.CopyLink).toUpperCase());
            }
            this.pickerBottomLayout.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ShareAlert.this.m3003lambda$new$4$orgtelegramuiComponentsShareAlert(view);
                }
            });
            frameLayoutParams = frameLayoutParams3;
            this.containerView.addView(this.pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity != null && ChatObject.hasAdminRights(chatActivity.getCurrentChat()) && this.sendingMessageObjects.size() > 0 && this.sendingMessageObjects.get(0).messageOwner.forwards > 0) {
                final MessageObject messageObject2 = this.sendingMessageObjects.get(0);
                if (!messageObject2.isForwarded()) {
                    LinearLayout linearLayout = new LinearLayout(context);
                    this.sharesCountLayout = linearLayout;
                    linearLayout.setOrientation(0);
                    this.sharesCountLayout.setGravity(16);
                    this.sharesCountLayout.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(this.darkTheme ? Theme.key_voipgroup_listSelector : Theme.key_listSelector), 2));
                    this.containerView.addView(this.sharesCountLayout, LayoutHelper.createFrame(-2, 48.0f, 85, 6.0f, 0.0f, -6.0f, 0.0f));
                    this.sharesCountLayout.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda10
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            ShareAlert.this.m3004lambda$new$5$orgtelegramuiComponentsShareAlert(messageObject2, view);
                        }
                    });
                    ImageView imageView = new ImageView(context);
                    imageView.setImageResource(R.drawable.share_arrow);
                    imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(this.darkTheme ? Theme.key_voipgroup_listeningText : Theme.key_dialogTextBlue2), PorterDuff.Mode.MULTIPLY));
                    this.sharesCountLayout.addView(imageView, LayoutHelper.createLinear(-2, -1, 16, 20, 0, 0, 0));
                    TextView textView2 = new TextView(context);
                    textView2.setText(String.format("%d", Integer.valueOf(messageObject2.messageOwner.forwards)));
                    textView2.setTextSize(1, 14.0f);
                    textView2.setTextColor(getThemedColor(this.darkTheme ? Theme.key_voipgroup_listeningText : Theme.key_dialogTextBlue2));
                    textView2.setGravity(16);
                    textView2.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                    this.sharesCountLayout.addView(textView2, LayoutHelper.createLinear(-2, -1, 16, 8, 0, 20, 0));
                }
            }
        } else {
            this.shadow[1].setAlpha(0.0f);
            frameLayoutParams = frameLayoutParams3;
        }
        AnonymousClass11 anonymousClass11 = new AnonymousClass11(context);
        this.frameLayout2 = anonymousClass11;
        anonymousClass11.setWillNotDraw(false);
        this.frameLayout2.setAlpha(0.0f);
        this.frameLayout2.setVisibility(4);
        this.containerView.addView(this.frameLayout2, LayoutHelper.createFrame(-1, -2, 83));
        this.frameLayout2.setOnTouchListener(ShareAlert$$ExternalSyntheticLambda14.INSTANCE);
        AnonymousClass12 anonymousClass12 = new AnonymousClass12(context, sizeNotifierFrameLayout, null, 1, resourcesProvider);
        this.commentTextView = anonymousClass12;
        if (this.darkTheme) {
            anonymousClass12.getEditText().setTextColor(getThemedColor(Theme.key_voipgroup_nameText));
            this.commentTextView.getEditText().setCursorColor(getThemedColor(Theme.key_voipgroup_nameText));
        }
        this.commentTextView.setBackgroundColor(backgroundColor);
        this.commentTextView.setHint(LocaleController.getString("ShareComment", R.string.ShareComment));
        this.commentTextView.onResume();
        this.commentTextView.setPadding(0, 0, AndroidUtilities.dp(84.0f), 0);
        this.frameLayout2.addView(this.commentTextView, LayoutHelper.createFrame(-1, -2, 51));
        this.frameLayout2.setClipChildren(false);
        this.frameLayout2.setClipToPadding(false);
        this.commentTextView.setClipChildren(false);
        FrameLayout frameLayout2 = new FrameLayout(context) { // from class: org.telegram.ui.Components.ShareAlert.13
            {
                ShareAlert.this = this;
            }

            @Override // android.view.View
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                info.setText(LocaleController.formatPluralString("AccDescrShareInChats", ShareAlert.this.selectedDialogs.size(), new Object[0]));
                info.setClassName(Button.class.getName());
                info.setLongClickable(true);
                info.setClickable(true);
            }
        };
        this.writeButtonContainer = frameLayout2;
        frameLayout2.setFocusable(true);
        this.writeButtonContainer.setFocusableInTouchMode(true);
        this.writeButtonContainer.setVisibility(4);
        this.writeButtonContainer.setScaleX(0.2f);
        this.writeButtonContainer.setScaleY(0.2f);
        this.writeButtonContainer.setAlpha(0.0f);
        this.containerView.addView(this.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 6.0f, 10.0f));
        final ImageView writeButton = new ImageView(context);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), getThemedColor(Theme.key_dialogFloatingButton), getThemedColor(Build.VERSION.SDK_INT >= 21 ? Theme.key_dialogFloatingButtonPressed : Theme.key_dialogFloatingButton));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        writeButton.setBackgroundDrawable(drawable);
        writeButton.setImageResource(R.drawable.attach_send);
        writeButton.setImportantForAccessibility(2);
        writeButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogFloatingIcon), PorterDuff.Mode.MULTIPLY));
        writeButton.setScaleType(ImageView.ScaleType.CENTER);
        if (Build.VERSION.SDK_INT >= 21) {
            writeButton.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.ShareAlert.14
                {
                    ShareAlert.this = this;
                }

                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        this.writeButtonContainer.addView(writeButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 51, Build.VERSION.SDK_INT >= 21 ? 2.0f : 0.0f, 0.0f, 0.0f, 0.0f));
        writeButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ShareAlert.this.m3005lambda$new$7$orgtelegramuiComponentsShareAlert(view);
            }
        });
        writeButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda13
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                return ShareAlert.this.m3006lambda$new$8$orgtelegramuiComponentsShareAlert(writeButton, view);
            }
        });
        this.textPaint.setTextSize(AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        View view = new View(context) { // from class: org.telegram.ui.Components.ShareAlert.15
            {
                ShareAlert.this = this;
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                String text3 = String.format("%d", Integer.valueOf(Math.max(1, ShareAlert.this.selectedDialogs.size())));
                int textSize = (int) Math.ceil(ShareAlert.this.textPaint.measureText(text3));
                int size = Math.max(AndroidUtilities.dp(16.0f) + textSize, AndroidUtilities.dp(24.0f));
                int cx = getMeasuredWidth() / 2;
                int measuredHeight = getMeasuredHeight() / 2;
                ShareAlert.this.textPaint.setColor(ShareAlert.this.getThemedColor(Theme.key_dialogRoundCheckBoxCheck));
                Paint paint = ShareAlert.this.paint;
                ShareAlert shareAlert = ShareAlert.this;
                paint.setColor(shareAlert.getThemedColor(shareAlert.darkTheme ? Theme.key_voipgroup_inviteMembersBackground : Theme.key_dialogBackground));
                ShareAlert.this.rect.set(cx - (size / 2), 0.0f, (size / 2) + cx, getMeasuredHeight());
                canvas.drawRoundRect(ShareAlert.this.rect, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), ShareAlert.this.paint);
                ShareAlert.this.paint.setColor(ShareAlert.this.getThemedColor(Theme.key_dialogRoundCheckBox));
                ShareAlert.this.rect.set((cx - (size / 2)) + AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), ((size / 2) + cx) - AndroidUtilities.dp(2.0f), getMeasuredHeight() - AndroidUtilities.dp(2.0f));
                canvas.drawRoundRect(ShareAlert.this.rect, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), ShareAlert.this.paint);
                canvas.drawText(text3, cx - (textSize / 2), AndroidUtilities.dp(16.2f), ShareAlert.this.textPaint);
            }
        };
        this.selectedCountView = view;
        view.setAlpha(0.0f);
        this.selectedCountView.setScaleX(0.2f);
        this.selectedCountView.setScaleY(0.2f);
        this.containerView.addView(this.selectedCountView, LayoutHelper.createFrame(42, 24.0f, 85, 0.0f, 0.0f, -8.0f, 9.0f));
        updateSelectedCount(0);
        DialogsActivity.loadDialogs(AccountInstance.getInstance(this.currentAccount));
        if (this.listAdapter.dialogs.isEmpty()) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogsNeedReload);
        }
        DialogsSearchAdapter.loadRecentSearch(this.currentAccount, 0, new DialogsSearchAdapter.OnRecentSearchLoaded() { // from class: org.telegram.ui.Components.ShareAlert.16
            {
                ShareAlert.this = this;
            }

            @Override // org.telegram.ui.Adapters.DialogsSearchAdapter.OnRecentSearchLoaded
            public void setRecentSearch(ArrayList<DialogsSearchAdapter.RecentSearchObject> arrayList2, LongSparseArray<DialogsSearchAdapter.RecentSearchObject> hashMap) {
                ShareAlert.this.recentSearchObjects = arrayList2;
                ShareAlert.this.recentSearchObjectsById = hashMap;
                for (int a2 = 0; a2 < ShareAlert.this.recentSearchObjects.size(); a2++) {
                    DialogsSearchAdapter.RecentSearchObject recentSearchObject = (DialogsSearchAdapter.RecentSearchObject) ShareAlert.this.recentSearchObjects.get(a2);
                    if (recentSearchObject.object instanceof TLRPC.User) {
                        MessagesController.getInstance(ShareAlert.this.currentAccount).putUser((TLRPC.User) recentSearchObject.object, true);
                    } else if (recentSearchObject.object instanceof TLRPC.Chat) {
                        MessagesController.getInstance(ShareAlert.this.currentAccount).putChat((TLRPC.Chat) recentSearchObject.object, true);
                    } else if (recentSearchObject.object instanceof TLRPC.EncryptedChat) {
                        MessagesController.getInstance(ShareAlert.this.currentAccount).putEncryptedChat((TLRPC.EncryptedChat) recentSearchObject.object, true);
                    }
                }
                ShareAlert.this.searchAdapter.notifyDataSetChanged();
            }
        });
        MediaDataController.getInstance(this.currentAccount).loadHints(true);
        AndroidUtilities.updateViewVisibilityAnimated(this.gridView, true, 1.0f, false);
        AndroidUtilities.updateViewVisibilityAnimated(this.searchGridView, false, 1.0f, false);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ShareAlert */
    public /* synthetic */ void m3000lambda$new$1$orgtelegramuiComponentsShareAlert(final Context context, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ShareAlert.this.m2999lambda$new$0$orgtelegramuiComponentsShareAlert(response, context);
            }
        });
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ShareAlert */
    public /* synthetic */ void m2999lambda$new$0$orgtelegramuiComponentsShareAlert(TLObject response, Context context) {
        if (response != null) {
            this.exportedMessageLink = (TLRPC.TL_exportedMessageLink) response;
            if (this.copyLinkOnEnd) {
                copyLink(context);
            }
        }
        this.loadingLink = false;
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ShareAlert */
    public /* synthetic */ void m3001lambda$new$2$orgtelegramuiComponentsShareAlert(View view, int position) {
        TLRPC.Dialog dialog;
        if (position < 0 || (dialog = this.listAdapter.getItem(position)) == null) {
            return;
        }
        selectDialog((ShareDialogCell) view, dialog);
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-ShareAlert */
    public /* synthetic */ void m3002lambda$new$3$orgtelegramuiComponentsShareAlert(View view, int position) {
        TLRPC.Dialog dialog;
        if (position < 0 || (dialog = this.searchAdapter.getItem(position)) == null) {
            return;
        }
        selectDialog((ShareDialogCell) view, dialog);
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-ShareAlert */
    public /* synthetic */ void m3003lambda$new$4$orgtelegramuiComponentsShareAlert(View v) {
        if (this.selectedDialogs.size() == 0) {
            if (this.isChannel || this.linkToCopy[0] != null) {
                dismiss();
                if (this.linkToCopy[0] == null && this.loadingLink) {
                    this.copyLinkOnEnd = true;
                    Toast.makeText(getContext(), LocaleController.getString("Loading", R.string.Loading), 0).show();
                    return;
                }
                copyLink(getContext());
            }
        }
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-ShareAlert */
    public /* synthetic */ void m3004lambda$new$5$orgtelegramuiComponentsShareAlert(MessageObject messageObject, View view) {
        this.parentFragment.presentFragment(new MessageStatisticActivity(messageObject));
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert$11 */
    /* loaded from: classes5.dex */
    public class AnonymousClass11 extends FrameLayout {
        private int color;
        private final Paint p = new Paint();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass11(Context arg0) {
            super(arg0);
            ShareAlert.this = this$0;
        }

        @Override // android.view.View
        public void setVisibility(int visibility) {
            super.setVisibility(visibility);
            if (visibility != 0) {
                ShareAlert.this.shadow[1].setTranslationY(0.0f);
            }
        }

        @Override // android.view.View
        public void setAlpha(float alpha) {
            super.setAlpha(alpha);
            invalidate();
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (ShareAlert.this.chatActivityEnterViewAnimateFromTop != 0.0f && ShareAlert.this.chatActivityEnterViewAnimateFromTop != ShareAlert.this.frameLayout2.getTop() + ShareAlert.this.chatActivityEnterViewAnimateFromTop) {
                if (ShareAlert.this.topBackgroundAnimator != null) {
                    ShareAlert.this.topBackgroundAnimator.cancel();
                }
                ShareAlert shareAlert = ShareAlert.this;
                shareAlert.captionEditTextTopOffset = shareAlert.chatActivityEnterViewAnimateFromTop - (ShareAlert.this.frameLayout2.getTop() + ShareAlert.this.captionEditTextTopOffset);
                ShareAlert shareAlert2 = ShareAlert.this;
                shareAlert2.topBackgroundAnimator = ValueAnimator.ofFloat(shareAlert2.captionEditTextTopOffset, 0.0f);
                ShareAlert.this.topBackgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ShareAlert$11$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ShareAlert.AnonymousClass11.this.m3013lambda$onDraw$0$orgtelegramuiComponentsShareAlert$11(valueAnimator);
                    }
                });
                ShareAlert.this.topBackgroundAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                ShareAlert.this.topBackgroundAnimator.setDuration(200L);
                ShareAlert.this.topBackgroundAnimator.start();
                ShareAlert.this.chatActivityEnterViewAnimateFromTop = 0.0f;
            }
            float alphaOffset = (ShareAlert.this.frameLayout2.getMeasuredHeight() - AndroidUtilities.dp(48.0f)) * (1.0f - getAlpha());
            ShareAlert.this.shadow[1].setTranslationY((-(ShareAlert.this.frameLayout2.getMeasuredHeight() - AndroidUtilities.dp(48.0f))) + ShareAlert.this.captionEditTextTopOffset + ShareAlert.this.currentPanTranslationY + alphaOffset);
        }

        /* renamed from: lambda$onDraw$0$org-telegram-ui-Components-ShareAlert$11 */
        public /* synthetic */ void m3013lambda$onDraw$0$orgtelegramuiComponentsShareAlert$11(ValueAnimator valueAnimator) {
            ShareAlert.this.captionEditTextTopOffset = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            ShareAlert.this.frameLayout2.invalidate();
            invalidate();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            canvas.save();
            canvas.clipRect(0.0f, ShareAlert.this.captionEditTextTopOffset, getMeasuredWidth(), getMeasuredHeight());
            super.dispatchDraw(canvas);
            canvas.restore();
        }
    }

    public static /* synthetic */ boolean lambda$new$6(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert$12 */
    /* loaded from: classes5.dex */
    public class AnonymousClass12 extends EditTextEmoji {
        private ValueAnimator messageEditTextAnimator;
        private int messageEditTextPredrawHeigth;
        private int messageEditTextPredrawScrollY;
        private boolean shouldAnimateEditTextWithBounds;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass12(Context context, SizeNotifierFrameLayout parent, BaseFragment fragment, int style, Theme.ResourcesProvider resourcesProvider) {
            super(context, parent, fragment, style, resourcesProvider);
            ShareAlert.this = this$0;
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            if (this.shouldAnimateEditTextWithBounds) {
                final EditTextCaption editText = ShareAlert.this.commentTextView.getEditText();
                float dy = (this.messageEditTextPredrawHeigth - editText.getMeasuredHeight()) + (this.messageEditTextPredrawScrollY - editText.getScrollY());
                editText.setOffsetY(editText.getOffsetY() - dy);
                ValueAnimator a = ValueAnimator.ofFloat(editText.getOffsetY(), 0.0f);
                a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ShareAlert$12$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        EditTextCaption.this.setOffsetY(((Float) valueAnimator.getAnimatedValue()).floatValue());
                    }
                });
                ValueAnimator valueAnimator = this.messageEditTextAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                this.messageEditTextAnimator = a;
                a.setDuration(200L);
                a.setInterpolator(CubicBezierInterpolator.DEFAULT);
                a.start();
                this.shouldAnimateEditTextWithBounds = false;
            }
            super.dispatchDraw(canvas);
        }

        @Override // org.telegram.ui.Components.EditTextEmoji
        protected void onLineCountChanged(int oldLineCount, int newLineCount) {
            if (!TextUtils.isEmpty(getEditText().getText())) {
                this.shouldAnimateEditTextWithBounds = true;
                this.messageEditTextPredrawHeigth = getEditText().getMeasuredHeight();
                this.messageEditTextPredrawScrollY = getEditText().getScrollY();
                invalidate();
            } else {
                getEditText().animate().cancel();
                getEditText().setOffsetY(0.0f);
                this.shouldAnimateEditTextWithBounds = false;
            }
            ShareAlert shareAlert = ShareAlert.this;
            shareAlert.chatActivityEnterViewAnimateFromTop = shareAlert.frameLayout2.getTop() + ShareAlert.this.captionEditTextTopOffset;
            ShareAlert.this.frameLayout2.invalidate();
        }

        @Override // org.telegram.ui.Components.EditTextEmoji
        public void showPopup(int show) {
            super.showPopup(show);
            if (ShareAlert.this.darkTheme) {
                ShareAlert.this.navBarColorKey = null;
                AndroidUtilities.setNavigationBarColor(ShareAlert.this.getWindow(), ShareAlert.this.getThemedColor(Theme.key_windowBackgroundGray), true, new AndroidUtilities.IntColorCallback() { // from class: org.telegram.ui.Components.ShareAlert$12$$ExternalSyntheticLambda2
                    @Override // org.telegram.messenger.AndroidUtilities.IntColorCallback
                    public final void run(int i) {
                        ShareAlert.AnonymousClass12.this.m3015lambda$showPopup$1$orgtelegramuiComponentsShareAlert$12(i);
                    }
                });
            }
        }

        /* renamed from: lambda$showPopup$1$org-telegram-ui-Components-ShareAlert$12 */
        public /* synthetic */ void m3015lambda$showPopup$1$orgtelegramuiComponentsShareAlert$12(int color) {
            ShareAlert shareAlert = ShareAlert.this;
            shareAlert.setOverlayNavBarColor(shareAlert.navBarColor = color);
        }

        @Override // org.telegram.ui.Components.EditTextEmoji
        public void hidePopup(boolean byBackButton) {
            super.hidePopup(byBackButton);
            if (ShareAlert.this.darkTheme) {
                ShareAlert.this.navBarColorKey = null;
                AndroidUtilities.setNavigationBarColor(ShareAlert.this.getWindow(), ShareAlert.this.getThemedColor(Theme.key_voipgroup_inviteMembersBackground), true, new AndroidUtilities.IntColorCallback() { // from class: org.telegram.ui.Components.ShareAlert$12$$ExternalSyntheticLambda1
                    @Override // org.telegram.messenger.AndroidUtilities.IntColorCallback
                    public final void run(int i) {
                        ShareAlert.AnonymousClass12.this.m3014lambda$hidePopup$2$orgtelegramuiComponentsShareAlert$12(i);
                    }
                });
            }
        }

        /* renamed from: lambda$hidePopup$2$org-telegram-ui-Components-ShareAlert$12 */
        public /* synthetic */ void m3014lambda$hidePopup$2$orgtelegramuiComponentsShareAlert$12(int color) {
            ShareAlert shareAlert = ShareAlert.this;
            shareAlert.setOverlayNavBarColor(shareAlert.navBarColor = color);
        }
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-ShareAlert */
    public /* synthetic */ void m3005lambda$new$7$orgtelegramuiComponentsShareAlert(View v) {
        sendInternal(true);
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-ShareAlert */
    public /* synthetic */ boolean m3006lambda$new$8$orgtelegramuiComponentsShareAlert(ImageView writeButton, View v) {
        return onSendLongClick(writeButton);
    }

    public void selectDialog(ShareDialogCell cell, TLRPC.Dialog dialog) {
        if (DialogObject.isChatDialog(dialog.id)) {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-dialog.id));
            if (ChatObject.isChannel(chat) && !chat.megagroup && (!ChatObject.isCanWriteToChannel(-dialog.id, this.currentAccount) || this.hasPoll == 2)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this.parentActivity);
                builder.setTitle(LocaleController.getString("SendMessageTitle", R.string.SendMessageTitle));
                if (this.hasPoll == 2) {
                    if (this.isChannel) {
                        builder.setMessage(LocaleController.getString("PublicPollCantForward", R.string.PublicPollCantForward));
                    } else if (ChatObject.isActionBannedByDefault(chat, 10)) {
                        builder.setMessage(LocaleController.getString("ErrorSendRestrictedPollsAll", R.string.ErrorSendRestrictedPollsAll));
                    } else {
                        builder.setMessage(LocaleController.getString("ErrorSendRestrictedPolls", R.string.ErrorSendRestrictedPolls));
                    }
                } else {
                    builder.setMessage(LocaleController.getString("ChannelCantSendMessage", R.string.ChannelCantSendMessage));
                }
                builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
                builder.show();
                return;
            }
        } else if (DialogObject.isEncryptedDialog(dialog.id) && this.hasPoll != 0) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(this.parentActivity);
            builder2.setTitle(LocaleController.getString("SendMessageTitle", R.string.SendMessageTitle));
            if (this.hasPoll != 0) {
                builder2.setMessage(LocaleController.getString("PollCantForwardSecretChat", R.string.PollCantForwardSecretChat));
            } else {
                builder2.setMessage(LocaleController.getString("InvoiceCantForwardSecretChat", R.string.InvoiceCantForwardSecretChat));
            }
            builder2.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
            builder2.show();
            return;
        }
        if (this.selectedDialogs.indexOfKey(dialog.id) >= 0) {
            this.selectedDialogs.remove(dialog.id);
            if (cell != null) {
                cell.setChecked(false, true);
            }
            updateSelectedCount(1);
        } else {
            this.selectedDialogs.put(dialog.id, dialog);
            if (cell != null) {
                cell.setChecked(true, true);
            }
            updateSelectedCount(2);
            long selfUserId = UserConfig.getInstance(this.currentAccount).clientUserId;
            if (this.searchIsVisible) {
                TLRPC.Dialog existingDialog = (TLRPC.Dialog) this.listAdapter.dialogsMap.get(dialog.id);
                if (existingDialog != null) {
                    if (existingDialog.id != selfUserId) {
                        this.listAdapter.dialogs.remove(existingDialog);
                        this.listAdapter.dialogs.add(1 ^ this.listAdapter.dialogs.isEmpty(), existingDialog);
                    }
                } else {
                    this.listAdapter.dialogsMap.put(dialog.id, dialog);
                    this.listAdapter.dialogs.add(1 ^ this.listAdapter.dialogs.isEmpty(), dialog);
                }
                this.listAdapter.notifyDataSetChanged();
                this.updateSearchAdapter = false;
                this.searchView.searchEditText.setText("");
                checkCurrentList(false);
                this.searchView.hideKeyboard();
            }
        }
        ShareSearchAdapter shareSearchAdapter = this.searchAdapter;
        if (shareSearchAdapter != null && shareSearchAdapter.categoryAdapter != null) {
            this.searchAdapter.categoryAdapter.notifyItemRangeChanged(0, this.searchAdapter.categoryAdapter.getItemCount());
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public int getContainerViewHeight() {
        return this.containerView.getMeasuredHeight() - this.containerViewTop;
    }

    private boolean onSendLongClick(View view) {
        int y;
        ChatActivity chatActivity;
        if (this.parentActivity == null) {
            return false;
        }
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(1);
        ArrayList<MessageObject> arrayList = this.sendingMessageObjects;
        String str = Theme.key_voipgroup_listSelector;
        if (arrayList != null) {
            ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout1 = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.parentActivity, this.resourcesProvider);
            if (this.darkTheme) {
                sendPopupLayout1.setBackgroundColor(getThemedColor(Theme.key_voipgroup_inviteMembersBackground));
            }
            sendPopupLayout1.setAnimationEnabled(false);
            sendPopupLayout1.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.ShareAlert.17
                private android.graphics.Rect popupRect = new android.graphics.Rect();

                {
                    ShareAlert.this = this;
                }

                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == 0 && ShareAlert.this.sendPopupWindow != null && ShareAlert.this.sendPopupWindow.isShowing()) {
                        v.getHitRect(this.popupRect);
                        if (!this.popupRect.contains((int) event.getX(), (int) event.getY())) {
                            ShareAlert.this.sendPopupWindow.dismiss();
                            return false;
                        }
                        return false;
                    }
                    return false;
                }
            });
            sendPopupLayout1.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() { // from class: org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda4
                @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener
                public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                    ShareAlert.this.m3012lambda$onSendLongClick$9$orgtelegramuiComponentsShareAlert(keyEvent);
                }
            });
            sendPopupLayout1.setShownFromBottom(false);
            final ActionBarMenuSubItem showSendersNameView = new ActionBarMenuSubItem(getContext(), true, true, false, this.resourcesProvider);
            if (this.darkTheme) {
                showSendersNameView.setTextColor(getThemedColor(Theme.key_voipgroup_nameText));
            }
            sendPopupLayout1.addView((View) showSendersNameView, LayoutHelper.createLinear(-1, 48));
            showSendersNameView.setTextAndIcon(LocaleController.getString("ShowSendersName", R.string.ShowSendersName), 0);
            this.showSendersName = true;
            showSendersNameView.setChecked(true);
            final ActionBarMenuSubItem hideSendersNameView = new ActionBarMenuSubItem(getContext(), true, false, true, this.resourcesProvider);
            if (this.darkTheme) {
                hideSendersNameView.setTextColor(getThemedColor(Theme.key_voipgroup_nameText));
            }
            sendPopupLayout1.addView((View) hideSendersNameView, LayoutHelper.createLinear(-1, 48));
            hideSendersNameView.setTextAndIcon(LocaleController.getString("HideSendersName", R.string.HideSendersName), 0);
            hideSendersNameView.setChecked(!this.showSendersName);
            showSendersNameView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda11
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ShareAlert.this.m3007lambda$onSendLongClick$10$orgtelegramuiComponentsShareAlert(showSendersNameView, hideSendersNameView, view2);
                }
            });
            hideSendersNameView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda12
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ShareAlert.this.m3008lambda$onSendLongClick$11$orgtelegramuiComponentsShareAlert(showSendersNameView, hideSendersNameView, view2);
                }
            });
            sendPopupLayout1.setupRadialSelectors(getThemedColor(this.darkTheme ? str : Theme.key_dialogButtonSelector));
            layout.addView(sendPopupLayout1, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, -8.0f));
        }
        ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout2 = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.parentActivity, this.resourcesProvider);
        if (this.darkTheme) {
            sendPopupLayout2.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_inviteMembersBackground));
        }
        sendPopupLayout2.setAnimationEnabled(false);
        sendPopupLayout2.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.ShareAlert.18
            private android.graphics.Rect popupRect = new android.graphics.Rect();

            {
                ShareAlert.this = this;
            }

            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == 0 && ShareAlert.this.sendPopupWindow != null && ShareAlert.this.sendPopupWindow.isShowing()) {
                    v.getHitRect(this.popupRect);
                    if (!this.popupRect.contains((int) event.getX(), (int) event.getY())) {
                        ShareAlert.this.sendPopupWindow.dismiss();
                        return false;
                    }
                    return false;
                }
                return false;
            }
        });
        sendPopupLayout2.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() { // from class: org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda3
            @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener
            public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                ShareAlert.this.m3009lambda$onSendLongClick$12$orgtelegramuiComponentsShareAlert(keyEvent);
            }
        });
        sendPopupLayout2.setShownFromBottom(false);
        ActionBarMenuSubItem sendWithoutSound = new ActionBarMenuSubItem(getContext(), true, true, this.resourcesProvider);
        if (this.darkTheme) {
            sendWithoutSound.setTextColor(getThemedColor(Theme.key_voipgroup_nameText));
            sendWithoutSound.setIconColor(getThemedColor(Theme.key_windowBackgroundWhiteHintText));
        }
        sendWithoutSound.setTextAndIcon(LocaleController.getString("SendWithoutSound", R.string.SendWithoutSound), R.drawable.input_notify_off);
        sendWithoutSound.setMinimumWidth(AndroidUtilities.dp(196.0f));
        sendPopupLayout2.addView((View) sendWithoutSound, LayoutHelper.createLinear(-1, 48));
        sendWithoutSound.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ShareAlert.this.m3010lambda$onSendLongClick$13$orgtelegramuiComponentsShareAlert(view2);
            }
        });
        ActionBarMenuSubItem sendMessage = new ActionBarMenuSubItem(getContext(), true, true, this.resourcesProvider);
        if (this.darkTheme) {
            sendMessage.setTextColor(getThemedColor(Theme.key_voipgroup_nameText));
            sendMessage.setIconColor(getThemedColor(Theme.key_windowBackgroundWhiteHintText));
        }
        sendMessage.setTextAndIcon(LocaleController.getString("SendMessage", R.string.SendMessage), R.drawable.msg_send);
        sendMessage.setMinimumWidth(AndroidUtilities.dp(196.0f));
        sendPopupLayout2.addView((View) sendMessage, LayoutHelper.createLinear(-1, 48));
        sendMessage.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda9
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ShareAlert.this.m3011lambda$onSendLongClick$14$orgtelegramuiComponentsShareAlert(view2);
            }
        });
        if (!this.darkTheme) {
            str = Theme.key_dialogButtonSelector;
        }
        sendPopupLayout2.setupRadialSelectors(getThemedColor(str));
        layout.addView(sendPopupLayout2, LayoutHelper.createLinear(-1, -2));
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(layout, -2, -2);
        this.sendPopupWindow = actionBarPopupWindow;
        actionBarPopupWindow.setAnimationEnabled(false);
        this.sendPopupWindow.setAnimationStyle(R.style.PopupContextAnimation2);
        this.sendPopupWindow.setOutsideTouchable(true);
        this.sendPopupWindow.setClippingEnabled(true);
        this.sendPopupWindow.setInputMethodMode(2);
        this.sendPopupWindow.setSoftInputMode(0);
        this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
        SharedConfig.removeScheduledOrNoSoundHint();
        layout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.sendPopupWindow.setFocusable(true);
        int[] location = new int[2];
        view.getLocationInWindow(location);
        if (this.keyboardVisible && (chatActivity = this.parentFragment) != null && chatActivity.contentView.getMeasuredHeight() > AndroidUtilities.dp(58.0f)) {
            y = location[1] + view.getMeasuredHeight();
        } else {
            int y2 = location[1];
            y = (y2 - layout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f);
        }
        this.sendPopupWindow.showAtLocation(view, 51, ((location[0] + view.getMeasuredWidth()) - layout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), y);
        this.sendPopupWindow.dimBehind();
        view.performHapticFeedback(3, 2);
        return true;
    }

    /* renamed from: lambda$onSendLongClick$9$org-telegram-ui-Components-ShareAlert */
    public /* synthetic */ void m3012lambda$onSendLongClick$9$orgtelegramuiComponentsShareAlert(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    /* renamed from: lambda$onSendLongClick$10$org-telegram-ui-Components-ShareAlert */
    public /* synthetic */ void m3007lambda$onSendLongClick$10$orgtelegramuiComponentsShareAlert(ActionBarMenuSubItem showSendersNameView, ActionBarMenuSubItem hideSendersNameView, View e) {
        this.showSendersName = true;
        showSendersNameView.setChecked(true);
        hideSendersNameView.setChecked(true ^ this.showSendersName);
    }

    /* renamed from: lambda$onSendLongClick$11$org-telegram-ui-Components-ShareAlert */
    public /* synthetic */ void m3008lambda$onSendLongClick$11$orgtelegramuiComponentsShareAlert(ActionBarMenuSubItem showSendersNameView, ActionBarMenuSubItem hideSendersNameView, View e) {
        this.showSendersName = false;
        showSendersNameView.setChecked(false);
        hideSendersNameView.setChecked(!this.showSendersName);
    }

    /* renamed from: lambda$onSendLongClick$12$org-telegram-ui-Components-ShareAlert */
    public /* synthetic */ void m3009lambda$onSendLongClick$12$orgtelegramuiComponentsShareAlert(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    /* renamed from: lambda$onSendLongClick$13$org-telegram-ui-Components-ShareAlert */
    public /* synthetic */ void m3010lambda$onSendLongClick$13$orgtelegramuiComponentsShareAlert(View v) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        sendInternal(false);
    }

    /* renamed from: lambda$onSendLongClick$14$org-telegram-ui-Components-ShareAlert */
    public /* synthetic */ void m3011lambda$onSendLongClick$14$orgtelegramuiComponentsShareAlert(View v) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        sendInternal(true);
    }

    protected void sendInternal(boolean withSound) {
        int num;
        int a = 0;
        while (true) {
            boolean z = true;
            if (a < this.selectedDialogs.size()) {
                long key = this.selectedDialogs.keyAt(a);
                Context context = getContext();
                int i = this.currentAccount;
                if (this.frameLayout2.getTag() == null || this.commentTextView.length() <= 0) {
                    z = false;
                }
                if (!AlertsCreator.checkSlowMode(context, i, key, z)) {
                    a++;
                } else {
                    return;
                }
            } else {
                if (this.sendingMessageObjects != null) {
                    for (int a2 = 0; a2 < this.selectedDialogs.size(); a2++) {
                        long key2 = this.selectedDialogs.keyAt(a2);
                        if (this.frameLayout2.getTag() != null && this.commentTextView.length() > 0) {
                            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.commentTextView.getText().toString(), key2, null, null, null, true, null, null, null, withSound, 0, null);
                        }
                        SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.sendingMessageObjects, key2, !this.showSendersName, false, withSound, 0);
                    }
                    onSend(this.selectedDialogs, this.sendingMessageObjects.size());
                } else {
                    SwitchView switchView = this.switchView;
                    if (switchView != null) {
                        num = switchView.currentTab;
                    } else {
                        num = 0;
                    }
                    if (this.sendingText[num] != null) {
                        for (int a3 = 0; a3 < this.selectedDialogs.size(); a3++) {
                            long key3 = this.selectedDialogs.keyAt(a3);
                            if (this.frameLayout2.getTag() != null && this.commentTextView.length() > 0) {
                                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.commentTextView.getText().toString(), key3, null, null, null, true, null, null, null, withSound, 0, null);
                            }
                            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.sendingText[num], key3, null, null, null, true, null, null, null, withSound, 0, null);
                        }
                    }
                    onSend(this.selectedDialogs, 1);
                }
                ShareAlertDelegate shareAlertDelegate = this.delegate;
                if (shareAlertDelegate != null) {
                    shareAlertDelegate.didShare();
                }
                dismiss();
                return;
            }
        }
    }

    protected void onSend(LongSparseArray<TLRPC.Dialog> dids, int count) {
    }

    public int getCurrentTop() {
        if (this.gridView.getChildCount() != 0) {
            int i = 0;
            View child = this.gridView.getChildAt(0);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(child);
            if (holder != null) {
                int paddingTop = this.gridView.getPaddingTop();
                if (holder.getLayoutPosition() == 0 && child.getTop() >= 0) {
                    i = child.getTop();
                }
                return paddingTop - i;
            }
            return -1000;
        }
        return -1000;
    }

    public void setDelegate(ShareAlertDelegate shareAlertDelegate) {
        this.delegate = shareAlertDelegate;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void dismissInternal() {
        super.dismissInternal();
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    @Override // android.app.Dialog
    public void onBackPressed() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null && editTextEmoji.isPopupShowing()) {
            this.commentTextView.hidePopup(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.dialogsNeedReload) {
            ShareDialogsAdapter shareDialogsAdapter = this.listAdapter;
            if (shareDialogsAdapter != null) {
                shareDialogsAdapter.fetchDialogs();
            }
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsNeedReload);
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    public void updateLayout() {
        if (this.panTranslationMoveLayout) {
            return;
        }
        RecyclerListView listView = this.searchIsVisible ? this.searchGridView : this.gridView;
        if (listView.getChildCount() <= 0) {
            return;
        }
        View child = listView.getChildAt(0);
        for (int i = 0; i < listView.getChildCount(); i++) {
            if (listView.getChildAt(i).getTop() < child.getTop()) {
                child = listView.getChildAt(i);
            }
        }
        RecyclerListView.Holder holder = (RecyclerListView.Holder) listView.findContainingViewHolder(child);
        int top = child.getTop() - AndroidUtilities.dp(8.0f);
        int newOffset = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
        if (top >= 0 && holder != null && holder.getAdapterPosition() == 0) {
            this.lastOffset = child.getTop();
            newOffset = top;
            runShadowAnimation(0, false);
        } else {
            this.lastOffset = Integer.MAX_VALUE;
            runShadowAnimation(0, true);
        }
        int i2 = this.scrollOffsetY;
        if (i2 != newOffset) {
            this.previousScrollOffsetY = i2;
            RecyclerListView recyclerListView = this.gridView;
            int i3 = (int) (newOffset + this.currentPanTranslationY);
            this.scrollOffsetY = i3;
            recyclerListView.setTopGlowOffset(i3);
            RecyclerListView recyclerListView2 = this.searchGridView;
            int i4 = (int) (newOffset + this.currentPanTranslationY);
            this.scrollOffsetY = i4;
            recyclerListView2.setTopGlowOffset(i4);
            this.frameLayout.setTranslationY(this.scrollOffsetY + this.currentPanTranslationY);
            this.searchEmptyView.setTranslationY(this.scrollOffsetY + this.currentPanTranslationY);
            this.containerView.invalidate();
        }
    }

    private void runShadowAnimation(final int num, final boolean show) {
        if ((show && this.shadow[num].getTag() != null) || (!show && this.shadow[num].getTag() == null)) {
            this.shadow[num].setTag(show ? null : 1);
            if (show) {
                this.shadow[num].setVisibility(0);
            }
            AnimatorSet[] animatorSetArr = this.shadowAnimation;
            if (animatorSetArr[num] != null) {
                animatorSetArr[num].cancel();
            }
            this.shadowAnimation[num] = new AnimatorSet();
            AnimatorSet animatorSet = this.shadowAnimation[num];
            Animator[] animatorArr = new Animator[1];
            View view = this.shadow[num];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet.playTogether(animatorArr);
            this.shadowAnimation[num].setDuration(150L);
            this.shadowAnimation[num].addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ShareAlert.19
                {
                    ShareAlert.this = this;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (ShareAlert.this.shadowAnimation[num] != null && ShareAlert.this.shadowAnimation[num].equals(animation)) {
                        if (!show) {
                            ShareAlert.this.shadow[num].setVisibility(4);
                        }
                        ShareAlert.this.shadowAnimation[num] = null;
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    if (ShareAlert.this.shadowAnimation[num] != null && ShareAlert.this.shadowAnimation[num].equals(animation)) {
                        ShareAlert.this.shadowAnimation[num] = null;
                    }
                }
            });
            this.shadowAnimation[num].start();
        }
    }

    private void copyLink(Context context) {
        String link;
        final boolean isPrivate = false;
        if (this.exportedMessageLink == null && this.linkToCopy[0] == null) {
            return;
        }
        try {
            SwitchView switchView = this.switchView;
            if (switchView != null) {
                link = this.linkToCopy[switchView.currentTab];
            } else {
                link = this.linkToCopy[0];
            }
            ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
            ClipData clip = ClipData.newPlainText(Constants.ScionAnalytics.PARAM_LABEL, link != null ? link : this.exportedMessageLink.link);
            clipboard.setPrimaryClip(clip);
            ShareAlertDelegate shareAlertDelegate = this.delegate;
            if ((shareAlertDelegate == null || !shareAlertDelegate.didCopy()) && (this.parentActivity instanceof LaunchActivity)) {
                TLRPC.TL_exportedMessageLink tL_exportedMessageLink = this.exportedMessageLink;
                if (tL_exportedMessageLink != null && tL_exportedMessageLink.link.contains("/c/")) {
                    isPrivate = true;
                }
                ((LaunchActivity) this.parentActivity).showBulletin(new Function() { // from class: org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda15
                    @Override // androidx.arch.core.util.Function
                    public final Object apply(Object obj) {
                        return ShareAlert.this.m2998lambda$copyLink$15$orgtelegramuiComponentsShareAlert(isPrivate, (BulletinFactory) obj);
                    }
                });
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$copyLink$15$org-telegram-ui-Components-ShareAlert */
    public /* synthetic */ Bulletin m2998lambda$copyLink$15$orgtelegramuiComponentsShareAlert(boolean isPrivate, BulletinFactory factory) {
        return factory.createCopyLinkBulletin(isPrivate, this.resourcesProvider);
    }

    private boolean showCommentTextView(final boolean show) {
        if (show == (this.frameLayout2.getTag() != null)) {
            return false;
        }
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.frameLayout2.setTag(show ? 1 : null);
        if (this.commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(this.commentTextView.getEditText());
        }
        this.commentTextView.hidePopup(true);
        if (show) {
            this.frameLayout2.setVisibility(0);
            this.writeButtonContainer.setVisibility(0);
        }
        TextView textView = this.pickerBottomLayout;
        int i = 4;
        if (textView != null) {
            ViewCompat.setImportantForAccessibility(textView, show ? 4 : 1);
        }
        LinearLayout linearLayout = this.sharesCountLayout;
        if (linearLayout != null) {
            if (!show) {
                i = 1;
            }
            ViewCompat.setImportantForAccessibility(linearLayout, i);
        }
        this.animatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList<>();
        FrameLayout frameLayout = this.frameLayout2;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        float f = 0.0f;
        fArr[0] = show ? 1.0f : 0.0f;
        animators.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
        FrameLayout frameLayout2 = this.writeButtonContainer;
        Property property2 = View.SCALE_X;
        float[] fArr2 = new float[1];
        float f2 = 0.2f;
        fArr2[0] = show ? 1.0f : 0.2f;
        animators.add(ObjectAnimator.ofFloat(frameLayout2, property2, fArr2));
        FrameLayout frameLayout3 = this.writeButtonContainer;
        Property property3 = View.SCALE_Y;
        float[] fArr3 = new float[1];
        fArr3[0] = show ? 1.0f : 0.2f;
        animators.add(ObjectAnimator.ofFloat(frameLayout3, property3, fArr3));
        FrameLayout frameLayout4 = this.writeButtonContainer;
        Property property4 = View.ALPHA;
        float[] fArr4 = new float[1];
        fArr4[0] = show ? 1.0f : 0.0f;
        animators.add(ObjectAnimator.ofFloat(frameLayout4, property4, fArr4));
        View view = this.selectedCountView;
        Property property5 = View.SCALE_X;
        float[] fArr5 = new float[1];
        fArr5[0] = show ? 1.0f : 0.2f;
        animators.add(ObjectAnimator.ofFloat(view, property5, fArr5));
        View view2 = this.selectedCountView;
        Property property6 = View.SCALE_Y;
        float[] fArr6 = new float[1];
        if (show) {
            f2 = 1.0f;
        }
        fArr6[0] = f2;
        animators.add(ObjectAnimator.ofFloat(view2, property6, fArr6));
        View view3 = this.selectedCountView;
        Property property7 = View.ALPHA;
        float[] fArr7 = new float[1];
        fArr7[0] = show ? 1.0f : 0.0f;
        animators.add(ObjectAnimator.ofFloat(view3, property7, fArr7));
        TextView textView2 = this.pickerBottomLayout;
        if (textView2 == null || textView2.getVisibility() != 0) {
            View view4 = this.shadow[1];
            Property property8 = View.ALPHA;
            float[] fArr8 = new float[1];
            if (show) {
                f = 1.0f;
            }
            fArr8[0] = f;
            animators.add(ObjectAnimator.ofFloat(view4, property8, fArr8));
        }
        this.animatorSet.playTogether(animators);
        this.animatorSet.setInterpolator(new DecelerateInterpolator());
        this.animatorSet.setDuration(180L);
        this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ShareAlert.20
            {
                ShareAlert.this = this;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(ShareAlert.this.animatorSet)) {
                    if (!show) {
                        ShareAlert.this.frameLayout2.setVisibility(4);
                        ShareAlert.this.writeButtonContainer.setVisibility(4);
                    }
                    ShareAlert.this.animatorSet = null;
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                if (animation.equals(ShareAlert.this.animatorSet)) {
                    ShareAlert.this.animatorSet = null;
                }
            }
        });
        this.animatorSet.start();
        return true;
    }

    public void updateSelectedCount(int animated) {
        if (this.selectedDialogs.size() == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            showCommentTextView(false);
            return;
        }
        this.selectedCountView.invalidate();
        if (!showCommentTextView(true) && animated != 0) {
            this.selectedCountView.setPivotX(AndroidUtilities.dp(21.0f));
            this.selectedCountView.setPivotY(AndroidUtilities.dp(12.0f));
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[2];
            View view = this.selectedCountView;
            Property property = View.SCALE_X;
            float[] fArr = new float[2];
            float f = 1.1f;
            fArr[0] = animated == 1 ? 1.1f : 0.9f;
            fArr[1] = 1.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            View view2 = this.selectedCountView;
            Property property2 = View.SCALE_Y;
            float[] fArr2 = new float[2];
            if (animated != 1) {
                f = 0.9f;
            }
            fArr2[0] = f;
            fArr2[1] = 1.0f;
            animatorArr[1] = ObjectAnimator.ofFloat(view2, property2, fArr2);
            animatorSet.playTogether(animatorArr);
            animatorSet.setInterpolator(new OvershootInterpolator());
            animatorSet.setDuration(180L);
            animatorSet.start();
            return;
        }
        this.selectedCountView.setPivotX(0.0f);
        this.selectedCountView.setPivotY(0.0f);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            AndroidUtilities.hideKeyboard(editTextEmoji.getEditText());
        }
        super.dismiss();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsNeedReload);
    }

    /* loaded from: classes5.dex */
    public class ShareDialogsAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private int currentCount;
        private ArrayList<TLRPC.Dialog> dialogs = new ArrayList<>();
        private LongSparseArray<TLRPC.Dialog> dialogsMap = new LongSparseArray<>();

        public ShareDialogsAdapter(Context context) {
            ShareAlert.this = r1;
            this.context = context;
            fetchDialogs();
        }

        public void fetchDialogs() {
            this.dialogs.clear();
            this.dialogsMap.clear();
            long selfUserId = UserConfig.getInstance(ShareAlert.this.currentAccount).clientUserId;
            if (!MessagesController.getInstance(ShareAlert.this.currentAccount).dialogsForward.isEmpty()) {
                TLRPC.Dialog dialog = MessagesController.getInstance(ShareAlert.this.currentAccount).dialogsForward.get(0);
                this.dialogs.add(dialog);
                this.dialogsMap.put(dialog.id, dialog);
            }
            ArrayList<TLRPC.Dialog> archivedDialogs = new ArrayList<>();
            ArrayList<TLRPC.Dialog> allDialogs = MessagesController.getInstance(ShareAlert.this.currentAccount).getAllDialogs();
            for (int a = 0; a < allDialogs.size(); a++) {
                TLRPC.Dialog dialog2 = allDialogs.get(a);
                if ((dialog2 instanceof TLRPC.TL_dialog) && dialog2.id != selfUserId && !DialogObject.isEncryptedDialog(dialog2.id)) {
                    if (!DialogObject.isUserDialog(dialog2.id)) {
                        TLRPC.Chat chat = MessagesController.getInstance(ShareAlert.this.currentAccount).getChat(Long.valueOf(-dialog2.id));
                        if (chat != null && !ChatObject.isNotInChat(chat) && ((!chat.gigagroup || ChatObject.hasAdminRights(chat)) && (!ChatObject.isChannel(chat) || chat.creator || ((chat.admin_rights != null && chat.admin_rights.post_messages) || chat.megagroup)))) {
                            if (dialog2.folder_id == 1) {
                                archivedDialogs.add(dialog2);
                            } else {
                                this.dialogs.add(dialog2);
                            }
                            this.dialogsMap.put(dialog2.id, dialog2);
                        }
                    } else {
                        if (dialog2.folder_id == 1) {
                            archivedDialogs.add(dialog2);
                        } else {
                            this.dialogs.add(dialog2);
                        }
                        this.dialogsMap.put(dialog2.id, dialog2);
                    }
                }
            }
            this.dialogs.addAll(archivedDialogs);
            notifyDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int count = this.dialogs.size();
            if (count != 0) {
                return count + 1;
            }
            return count;
        }

        public TLRPC.Dialog getItem(int position) {
            int position2 = position - 1;
            if (position2 < 0 || position2 >= this.dialogs.size()) {
                return null;
            }
            return this.dialogs.get(position2);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != 1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new ShareDialogCell(this.context, ShareAlert.this.darkTheme ? 1 : 0, ShareAlert.this.resourcesProvider);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0f)));
                    break;
                default:
                    view = new View(this.context);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp((!ShareAlert.this.darkTheme || ShareAlert.this.linkToCopy[1] == null) ? 56.0f : 109.0f)));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                ShareDialogCell cell = (ShareDialogCell) holder.itemView;
                TLRPC.Dialog dialog = getItem(position);
                cell.setDialog(dialog.id, ShareAlert.this.selectedDialogs.indexOfKey(dialog.id) >= 0, null);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == 0) {
                return 1;
            }
            return 0;
        }
    }

    /* loaded from: classes5.dex */
    public class ShareSearchAdapter extends RecyclerListView.SelectionAdapter {
        DialogsSearchAdapter.CategoryAdapterRecycler categoryAdapter;
        RecyclerView categoryListView;
        private Context context;
        int itemsCount;
        private int lastGlobalSearchId;
        int lastItemCont;
        private int lastLocalSearchId;
        private int lastReqId;
        private int lastSearchId;
        private String lastSearchText;
        private int reqId;
        private SearchAdapterHelper searchAdapterHelper;
        private Runnable searchRunnable;
        private Runnable searchRunnable2;
        private ArrayList<Object> searchResult = new ArrayList<>();
        int hintsCell = -1;
        int resentTitleCell = -1;
        int firstEmptyViewCell = -1;
        int recentDialogsStartRow = -1;
        int searchResultsStartRow = -1;
        int lastFilledItem = -1;
        boolean internalDialogsIsSearching = false;

        public ShareSearchAdapter(Context context) {
            ShareAlert.this = this$0;
            this.context = context;
            SearchAdapterHelper searchAdapterHelper = new SearchAdapterHelper(false);
            this.searchAdapterHelper = searchAdapterHelper;
            searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() { // from class: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.1
                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ LongSparseArray getExcludeCallParticipants() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ LongSparseArray getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }

                {
                    ShareSearchAdapter.this = this;
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public void onDataSetChanged(int searchId) {
                    ShareSearchAdapter.this.lastGlobalSearchId = searchId;
                    if (ShareSearchAdapter.this.lastLocalSearchId != searchId) {
                        ShareSearchAdapter.this.searchResult.clear();
                    }
                    int oldItemsCount = ShareSearchAdapter.this.lastItemCont;
                    if (ShareSearchAdapter.this.getItemCount() == 0 && !ShareSearchAdapter.this.searchAdapterHelper.isSearchInProgress() && !ShareSearchAdapter.this.internalDialogsIsSearching) {
                        ShareAlert.this.searchEmptyView.showProgress(false, true);
                    } else {
                        ShareAlert.this.recyclerItemsEnterAnimator.showItemsAnimated(oldItemsCount);
                    }
                    ShareSearchAdapter.this.notifyDataSetChanged();
                    ShareAlert.this.checkCurrentList(true);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public boolean canApplySearchResults(int searchId) {
                    return searchId == ShareSearchAdapter.this.lastSearchId;
                }
            });
        }

        private void searchDialogsInternal(final String query, final int searchId) {
            MessagesStorage.getInstance(ShareAlert.this.currentAccount).getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ShareAlert.ShareSearchAdapter.this.m3021xba04a7a2(query, searchId);
                }
            });
        }

        /* JADX WARN: Removed duplicated region for block: B:193:0x04a9 A[Catch: Exception -> 0x04f1, LOOP:7: B:162:0x03c6->B:193:0x04a9, LOOP_END, TryCatch #2 {Exception -> 0x04f1, blocks: (B:3:0x0002, B:5:0x0011, B:7:0x001e, B:9:0x002c, B:16:0x003a, B:18:0x0041, B:19:0x0043, B:20:0x0069, B:22:0x006f, B:24:0x0087, B:26:0x0091, B:28:0x009e, B:30:0x00a4, B:32:0x00b1, B:35:0x00c0, B:48:0x0120, B:56:0x0142, B:59:0x0159, B:61:0x015f, B:65:0x0177, B:93:0x024e, B:94:0x0275, B:96:0x027b, B:100:0x0291, B:102:0x0294, B:104:0x029c, B:107:0x02b3, B:109:0x02b9, B:112:0x02cf, B:113:0x02d2, B:115:0x02d9, B:117:0x02e6, B:119:0x02ec, B:121:0x02f2, B:123:0x02f6, B:125:0x02fa, B:127:0x0300, B:131:0x030b, B:137:0x0343, B:139:0x0349, B:140:0x034f, B:142:0x0355, B:144:0x035f, B:146:0x0363, B:147:0x0366, B:148:0x0369, B:149:0x0380, B:151:0x0386, B:154:0x0392, B:157:0x03a6, B:159:0x03b3, B:161:0x03be, B:163:0x03c8, B:165:0x03d6, B:168:0x03ef, B:170:0x03f5, B:174:0x040d, B:181:0x041d, B:183:0x0428, B:185:0x0443, B:187:0x0455, B:189:0x0464, B:190:0x0470, B:191:0x0497, B:193:0x04a9, B:196:0x04d7), top: B:210:0x0002 }] */
        /* JADX WARN: Removed duplicated region for block: B:221:0x0187 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:245:0x041d A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:85:0x020e A[Catch: Exception -> 0x04ed, LOOP:2: B:52:0x0130->B:85:0x020e, LOOP_END, TryCatch #0 {Exception -> 0x04ed, blocks: (B:38:0x00d3, B:39:0x00fb, B:41:0x0101, B:44:0x0116, B:51:0x012c, B:53:0x0132, B:73:0x0188, B:75:0x0192, B:77:0x01ae, B:81:0x01c3, B:82:0x01ce, B:83:0x01f5, B:85:0x020e, B:88:0x0234, B:90:0x0246), top: B:206:0x00d3 }] */
        /* renamed from: lambda$searchDialogsInternal$1$org-telegram-ui-Components-ShareAlert$ShareSearchAdapter */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public /* synthetic */ void m3021xba04a7a2(java.lang.String r31, int r32) {
            /*
                Method dump skipped, instructions count: 1272
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.m3021xba04a7a2(java.lang.String, int):void");
        }

        public static /* synthetic */ int lambda$searchDialogsInternal$0(Object lhs, Object rhs) {
            DialogSearchResult res1 = (DialogSearchResult) lhs;
            DialogSearchResult res2 = (DialogSearchResult) rhs;
            if (res1.date < res2.date) {
                return 1;
            }
            if (res1.date > res2.date) {
                return -1;
            }
            return 0;
        }

        private void updateSearchResults(final ArrayList<Object> result, final int searchId) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ShareAlert.ShareSearchAdapter.this.m3022xde10a13c(searchId, result);
                }
            });
        }

        /* renamed from: lambda$updateSearchResults$2$org-telegram-ui-Components-ShareAlert$ShareSearchAdapter */
        public /* synthetic */ void m3022xde10a13c(int searchId, ArrayList result) {
            if (searchId != this.lastSearchId) {
                return;
            }
            getItemCount();
            this.internalDialogsIsSearching = false;
            this.lastLocalSearchId = searchId;
            if (this.lastGlobalSearchId != searchId) {
                this.searchAdapterHelper.clear();
            }
            if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.searchAdapter) {
                ShareAlert shareAlert = ShareAlert.this;
                shareAlert.topBeforeSwitch = shareAlert.getCurrentTop();
                ShareAlert.this.searchAdapter.notifyDataSetChanged();
            }
            for (int a = 0; a < result.size(); a++) {
                DialogSearchResult obj = (DialogSearchResult) result.get(a);
                if (obj.object instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) obj.object;
                    MessagesController.getInstance(ShareAlert.this.currentAccount).putUser(user, true);
                } else if (obj.object instanceof TLRPC.Chat) {
                    TLRPC.Chat chat = (TLRPC.Chat) obj.object;
                    MessagesController.getInstance(ShareAlert.this.currentAccount).putChat(chat, true);
                }
            }
            boolean becomeEmpty = !this.searchResult.isEmpty() && result.isEmpty();
            if (!this.searchResult.isEmpty() || !result.isEmpty()) {
            }
            if (becomeEmpty) {
                ShareAlert shareAlert2 = ShareAlert.this;
                shareAlert2.topBeforeSwitch = shareAlert2.getCurrentTop();
            }
            this.searchResult = result;
            this.searchAdapterHelper.mergeResults(result, null);
            int oldItemsCount = this.lastItemCont;
            if (getItemCount() == 0 && !this.searchAdapterHelper.isSearchInProgress() && !this.internalDialogsIsSearching) {
                ShareAlert.this.searchEmptyView.showProgress(false, true);
            } else {
                ShareAlert.this.recyclerItemsEnterAnimator.showItemsAnimated(oldItemsCount);
            }
            notifyDataSetChanged();
            ShareAlert.this.checkCurrentList(true);
        }

        public void searchDialogs(final String query) {
            if (query != null && query.equals(this.lastSearchText)) {
                return;
            }
            this.lastSearchText = query;
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            Runnable runnable = this.searchRunnable2;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable2 = null;
            }
            this.searchResult.clear();
            this.searchAdapterHelper.mergeResults(null);
            this.searchAdapterHelper.queryServerSearch(null, true, true, true, true, false, 0L, false, 0, 0);
            notifyDataSetChanged();
            ShareAlert.this.checkCurrentList(true);
            if (TextUtils.isEmpty(query)) {
                ShareAlert shareAlert = ShareAlert.this;
                shareAlert.topBeforeSwitch = shareAlert.getCurrentTop();
                this.lastSearchId = -1;
                this.internalDialogsIsSearching = false;
            } else {
                this.internalDialogsIsSearching = true;
                final int searchId = this.lastSearchId + 1;
                this.lastSearchId = searchId;
                ShareAlert.this.searchEmptyView.showProgress(true, true);
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        ShareAlert.ShareSearchAdapter.this.m3020x47315ffc(query, searchId);
                    }
                };
                this.searchRunnable = runnable2;
                dispatchQueue.postRunnable(runnable2, 300L);
            }
            ShareAlert.this.checkCurrentList(false);
        }

        /* renamed from: lambda$searchDialogs$4$org-telegram-ui-Components-ShareAlert$ShareSearchAdapter */
        public /* synthetic */ void m3020x47315ffc(final String query, final int searchId) {
            this.searchRunnable = null;
            searchDialogsInternal(query, searchId);
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ShareAlert.ShareSearchAdapter.this.m3019xba4448dd(searchId, query);
                }
            };
            this.searchRunnable2 = runnable;
            AndroidUtilities.runOnUIThread(runnable);
        }

        /* renamed from: lambda$searchDialogs$3$org-telegram-ui-Components-ShareAlert$ShareSearchAdapter */
        public /* synthetic */ void m3019xba4448dd(int searchId, String query) {
            this.searchRunnable2 = null;
            if (searchId != this.lastSearchId) {
                return;
            }
            this.searchAdapterHelper.queryServerSearch(query, true, true, true, true, false, 0L, false, 0, searchId);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            this.itemsCount = 0;
            this.hintsCell = -1;
            this.resentTitleCell = -1;
            this.recentDialogsStartRow = -1;
            this.searchResultsStartRow = -1;
            this.lastFilledItem = -1;
            if (TextUtils.isEmpty(this.lastSearchText)) {
                int i = this.itemsCount;
                int i2 = i + 1;
                this.itemsCount = i2;
                this.firstEmptyViewCell = i;
                this.itemsCount = i2 + 1;
                this.hintsCell = i2;
                if (ShareAlert.this.recentSearchObjects.size() > 0) {
                    int i3 = this.itemsCount;
                    int i4 = i3 + 1;
                    this.itemsCount = i4;
                    this.resentTitleCell = i3;
                    this.recentDialogsStartRow = i4;
                    this.itemsCount = i4 + ShareAlert.this.recentSearchObjects.size();
                }
                int i5 = this.itemsCount;
                int i6 = i5 + 1;
                this.itemsCount = i6;
                this.lastFilledItem = i5;
                this.lastItemCont = i6;
                return i6;
            }
            int i7 = this.itemsCount;
            int i8 = i7 + 1;
            this.itemsCount = i8;
            this.firstEmptyViewCell = i7;
            this.searchResultsStartRow = i8;
            int size = i8 + this.searchResult.size() + this.searchAdapterHelper.getLocalServerSearch().size();
            this.itemsCount = size;
            if (size == 1) {
                this.firstEmptyViewCell = -1;
                this.itemsCount = 0;
                this.lastItemCont = 0;
                return 0;
            }
            int i9 = size + 1;
            this.itemsCount = i9;
            this.lastFilledItem = size;
            this.lastItemCont = i9;
            return i9;
        }

        public TLRPC.Dialog getItem(int position) {
            int i = this.recentDialogsStartRow;
            if (position >= i && i >= 0) {
                int index = position - i;
                if (index < 0 || index >= ShareAlert.this.recentSearchObjects.size()) {
                    return null;
                }
                DialogsSearchAdapter.RecentSearchObject recentSearchObject = (DialogsSearchAdapter.RecentSearchObject) ShareAlert.this.recentSearchObjects.get(index);
                TLObject object = recentSearchObject.object;
                TLRPC.Dialog dialog = new TLRPC.TL_dialog();
                if (object instanceof TLRPC.User) {
                    dialog.id = ((TLRPC.User) object).id;
                } else {
                    dialog.id = -((TLRPC.Chat) object).id;
                }
                return dialog;
            }
            int position2 = position - 1;
            if (position2 < 0) {
                return null;
            }
            if (position2 < this.searchResult.size()) {
                return ((DialogSearchResult) this.searchResult.get(position2)).dialog;
            }
            int position3 = position2 - this.searchResult.size();
            ArrayList<TLObject> arrayList = this.searchAdapterHelper.getLocalServerSearch();
            if (position3 >= arrayList.size()) {
                return null;
            }
            TLObject object2 = arrayList.get(position3);
            TLRPC.Dialog dialog2 = new TLRPC.TL_dialog();
            if (object2 instanceof TLRPC.User) {
                dialog2.id = ((TLRPC.User) object2).id;
            } else {
                dialog2.id = -((TLRPC.Chat) object2).id;
            }
            return dialog2;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return (holder.getItemViewType() == 1 || holder.getItemViewType() == 4) ? false : true;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View view2 = new ShareDialogCell(this.context, ShareAlert.this.darkTheme ? 1 : 0, ShareAlert.this.resourcesProvider);
                    view2.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0f)));
                    view = view2;
                    break;
                case 1:
                default:
                    View view3 = new View(this.context);
                    view3.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp((!ShareAlert.this.darkTheme || ShareAlert.this.linkToCopy[1] == null) ? 56.0f : 109.0f)));
                    view = view3;
                    break;
                case 2:
                    RecyclerListView horizontalListView = new RecyclerListView(this.context, ShareAlert.this.resourcesProvider) { // from class: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.2
                        {
                            ShareSearchAdapter.this = this;
                        }

                        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
                        public boolean onInterceptTouchEvent(MotionEvent e) {
                            if (getParent() != null && getParent().getParent() != null) {
                                ViewParent parent2 = getParent().getParent();
                                boolean z = true;
                                if (!canScrollHorizontally(-1) && !canScrollHorizontally(1)) {
                                    z = false;
                                }
                                parent2.requestDisallowInterceptTouchEvent(z);
                            }
                            return super.onInterceptTouchEvent(e);
                        }
                    };
                    this.categoryListView = horizontalListView;
                    horizontalListView.setItemAnimator(null);
                    horizontalListView.setLayoutAnimation(null);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this.context) { // from class: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.3
                        {
                            ShareSearchAdapter.this = this;
                        }

                        @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                        public boolean supportsPredictiveItemAnimations() {
                            return false;
                        }
                    };
                    layoutManager.setOrientation(0);
                    horizontalListView.setLayoutManager(layoutManager);
                    DialogsSearchAdapter.CategoryAdapterRecycler categoryAdapterRecycler = new DialogsSearchAdapter.CategoryAdapterRecycler(this.context, ShareAlert.this.currentAccount, true) { // from class: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.4
                        {
                            ShareSearchAdapter.this = this;
                        }

                        @Override // org.telegram.ui.Adapters.DialogsSearchAdapter.CategoryAdapterRecycler, androidx.recyclerview.widget.RecyclerView.Adapter
                        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                            HintDialogCell cell = (HintDialogCell) holder.itemView;
                            if (ShareAlert.this.darkTheme) {
                                cell.setColors(Theme.key_voipgroup_nameText, Theme.key_voipgroup_inviteMembersBackground);
                            }
                            TLRPC.TL_topPeer peer = MediaDataController.getInstance(ShareAlert.this.currentAccount).hints.get(position);
                            TLRPC.Chat chat = null;
                            TLRPC.User user = null;
                            long did = 0;
                            if (peer.peer.user_id != 0) {
                                did = peer.peer.user_id;
                                user = MessagesController.getInstance(ShareAlert.this.currentAccount).getUser(Long.valueOf(peer.peer.user_id));
                            } else if (peer.peer.channel_id != 0) {
                                did = -peer.peer.channel_id;
                                chat = MessagesController.getInstance(ShareAlert.this.currentAccount).getChat(Long.valueOf(peer.peer.channel_id));
                            } else if (peer.peer.chat_id != 0) {
                                did = -peer.peer.chat_id;
                                chat = MessagesController.getInstance(ShareAlert.this.currentAccount).getChat(Long.valueOf(peer.peer.chat_id));
                            }
                            boolean z = false;
                            boolean animated = did == cell.getDialogId();
                            cell.setTag(Long.valueOf(did));
                            String name = "";
                            if (user != null) {
                                name = UserObject.getFirstName(user);
                            } else if (chat != null) {
                                name = chat.title;
                            }
                            cell.setDialog(did, true, name);
                            if (ShareAlert.this.selectedDialogs.indexOfKey(did) >= 0) {
                                z = true;
                            }
                            cell.setChecked(z, animated);
                        }
                    };
                    this.categoryAdapter = categoryAdapterRecycler;
                    horizontalListView.setAdapter(categoryAdapterRecycler);
                    horizontalListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda5
                        @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                        public final void onItemClick(View view4, int i) {
                            ShareAlert.ShareSearchAdapter.this.m3018x933a65b8(view4, i);
                        }
                    });
                    view = horizontalListView;
                    break;
                case 3:
                    GraySectionCell graySectionCell = new GraySectionCell(this.context, ShareAlert.this.resourcesProvider);
                    graySectionCell.setTextColor(ShareAlert.this.darkTheme ? Theme.key_voipgroup_nameText : Theme.key_graySectionText);
                    ShareAlert shareAlert = ShareAlert.this;
                    graySectionCell.setBackgroundColor(shareAlert.getThemedColor(shareAlert.darkTheme ? Theme.key_voipgroup_searchBackground : Theme.key_graySection));
                    graySectionCell.setText(LocaleController.getString("Recent", R.string.Recent));
                    view = graySectionCell;
                    break;
                case 4:
                    view = new View(this.context) { // from class: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.5
                        {
                            ShareSearchAdapter.this = this;
                        }

                        @Override // android.view.View
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(ShareAlert.this.searchLayoutManager.lastItemHeight, C.BUFFER_FLAG_ENCRYPTED));
                        }
                    };
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$5$org-telegram-ui-Components-ShareAlert$ShareSearchAdapter */
        public /* synthetic */ void m3018x933a65b8(View view1, int position) {
            TLRPC.TL_topPeer peer = MediaDataController.getInstance(ShareAlert.this.currentAccount).hints.get(position);
            TLRPC.Dialog dialog = new TLRPC.TL_dialog();
            long did = 0;
            if (peer.peer.user_id != 0) {
                did = peer.peer.user_id;
            } else if (peer.peer.channel_id != 0) {
                did = -peer.peer.channel_id;
            } else if (peer.peer.chat_id != 0) {
                did = -peer.peer.chat_id;
            }
            dialog.id = did;
            ShareAlert.this.selectDialog(null, dialog);
            HintDialogCell cell = (HintDialogCell) view1;
            cell.setChecked(ShareAlert.this.selectedDialogs.indexOfKey(did) >= 0, true);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            long id;
            CharSequence name;
            int index;
            String str;
            int index2;
            TLRPC.User user;
            if (holder.getItemViewType() != 0) {
                if (holder.getItemViewType() == 2) {
                    ((RecyclerListView) holder.itemView).getAdapter().notifyDataSetChanged();
                    return;
                }
                return;
            }
            ShareDialogCell cell = (ShareDialogCell) holder.itemView;
            CharSequence name2 = null;
            long id2 = 0;
            if (TextUtils.isEmpty(this.lastSearchText)) {
                int i = this.recentDialogsStartRow;
                if (i >= 0 && position >= i) {
                    int p = position - i;
                    DialogsSearchAdapter.RecentSearchObject recentSearchObject = (DialogsSearchAdapter.RecentSearchObject) ShareAlert.this.recentSearchObjects.get(p);
                    TLObject object = recentSearchObject.object;
                    if (object instanceof TLRPC.User) {
                        TLRPC.User user2 = (TLRPC.User) object;
                        id2 = user2.id;
                        name2 = ContactsController.formatName(user2.first_name, user2.last_name);
                        str = Theme.key_windowBackgroundWhiteBlueText4;
                    } else if (object instanceof TLRPC.Chat) {
                        TLRPC.Chat chat = (TLRPC.Chat) object;
                        str = Theme.key_windowBackgroundWhiteBlueText4;
                        id2 = -chat.id;
                        name2 = chat.title;
                    } else {
                        str = Theme.key_windowBackgroundWhiteBlueText4;
                        if ((object instanceof TLRPC.TL_encryptedChat) && (user = MessagesController.getInstance(ShareAlert.this.currentAccount).getUser(Long.valueOf(((TLRPC.TL_encryptedChat) object).user_id))) != null) {
                            id2 = user.id;
                            name2 = ContactsController.formatName(user.first_name, user.last_name);
                        }
                    }
                    String foundUserName = this.searchAdapterHelper.getLastFoundUsername();
                    if (!TextUtils.isEmpty(foundUserName) && name2 != null && (index2 = AndroidUtilities.indexOfIgnoreCase(name2.toString(), foundUserName)) != -1) {
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(name2);
                        spannableStringBuilder.setSpan(new ForegroundColorSpanThemable(str, ShareAlert.this.resourcesProvider), index2, foundUserName.length() + index2, 33);
                        name2 = spannableStringBuilder;
                    }
                }
                int p2 = (int) id2;
                cell.setDialog(p2, ShareAlert.this.selectedDialogs.indexOfKey(id2) >= 0, name2);
                return;
            }
            int position2 = position - 1;
            if (position2 < this.searchResult.size()) {
                DialogSearchResult result = (DialogSearchResult) this.searchResult.get(position2);
                id = result.dialog.id;
                name = result.name;
            } else {
                int position3 = position2 - this.searchResult.size();
                ArrayList<TLObject> arrayList = this.searchAdapterHelper.getLocalServerSearch();
                TLObject object2 = arrayList.get(position3);
                if (object2 instanceof TLRPC.User) {
                    TLRPC.User user3 = (TLRPC.User) object2;
                    id = user3.id;
                    name = ContactsController.formatName(user3.first_name, user3.last_name);
                } else {
                    TLRPC.Chat chat2 = (TLRPC.Chat) object2;
                    id = -chat2.id;
                    name = chat2.title;
                }
                String foundUserName2 = this.searchAdapterHelper.getLastFoundUsername();
                if (!TextUtils.isEmpty(foundUserName2) && name != null && (index = AndroidUtilities.indexOfIgnoreCase(name.toString(), foundUserName2)) != -1) {
                    SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(name);
                    spannableStringBuilder2.setSpan(new ForegroundColorSpanThemable(Theme.key_windowBackgroundWhiteBlueText4, ShareAlert.this.resourcesProvider), index, foundUserName2.length() + index, 33);
                    name = spannableStringBuilder2;
                }
            }
            cell.setDialog(id, ShareAlert.this.selectedDialogs.indexOfKey(id) >= 0, name);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == this.lastFilledItem) {
                return 4;
            }
            if (position == this.firstEmptyViewCell) {
                return 1;
            }
            if (position == this.hintsCell) {
                return 2;
            }
            if (position == this.resentTitleCell) {
                return 3;
            }
            return 0;
        }

        public boolean isSearching() {
            return !TextUtils.isEmpty(this.lastSearchText);
        }

        public int getSpanSize(int spanCount, int position) {
            if (position == this.hintsCell || position == this.resentTitleCell || position == this.firstEmptyViewCell || position == this.lastFilledItem) {
                return spanCount;
            }
            return 1;
        }
    }

    public void checkCurrentList(boolean force) {
        boolean searchVisibleLocal = false;
        if (!TextUtils.isEmpty(this.searchView.searchEditText.getText()) || (this.keyboardVisible && this.searchView.searchEditText.hasFocus())) {
            searchVisibleLocal = true;
            this.updateSearchAdapter = true;
            AndroidUtilities.updateViewVisibilityAnimated(this.gridView, false, 0.98f, true);
            AndroidUtilities.updateViewVisibilityAnimated(this.searchGridView, true);
        } else {
            AndroidUtilities.updateViewVisibilityAnimated(this.gridView, true, 0.98f, true);
            AndroidUtilities.updateViewVisibilityAnimated(this.searchGridView, false);
        }
        if (this.searchIsVisible != searchVisibleLocal || force) {
            this.searchIsVisible = searchVisibleLocal;
            this.searchAdapter.notifyDataSetChanged();
            this.listAdapter.notifyDataSetChanged();
            if (this.searchIsVisible) {
                if (this.lastOffset == Integer.MAX_VALUE) {
                    ((LinearLayoutManager) this.searchGridView.getLayoutManager()).scrollToPositionWithOffset(0, -this.searchGridView.getPaddingTop());
                } else {
                    ((LinearLayoutManager) this.searchGridView.getLayoutManager()).scrollToPositionWithOffset(0, this.lastOffset - this.searchGridView.getPaddingTop());
                }
                this.searchAdapter.searchDialogs(this.searchView.searchEditText.getText().toString());
            } else if (this.lastOffset == Integer.MAX_VALUE) {
                this.layoutManager.scrollToPositionWithOffset(0, 0);
            } else {
                this.layoutManager.scrollToPositionWithOffset(0, 0);
            }
        }
    }
}
