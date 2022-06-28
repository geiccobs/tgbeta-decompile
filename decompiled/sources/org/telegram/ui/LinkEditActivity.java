package org.telegram.ui;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.SlideChooseView;
/* loaded from: classes4.dex */
public class LinkEditActivity extends BaseFragment {
    public static final int CREATE_TYPE = 0;
    public static final int EDIT_TYPE = 1;
    private TextCheckCell approveCell;
    private TextView buttonTextView;
    private Callback callback;
    private final long chatId;
    private TextView createTextView;
    int currentInviteDate;
    private TextInfoPrivacyCell divider;
    private TextInfoPrivacyCell dividerName;
    private TextInfoPrivacyCell dividerUses;
    private boolean finished;
    private boolean ignoreSet;
    TLRPC.TL_chatInviteExported inviteToEdit;
    boolean loading;
    private EditText nameEditText;
    AlertDialog progressDialog;
    private TextSettingsCell revokeLink;
    boolean scrollToEnd;
    boolean scrollToStart;
    private ScrollView scrollView;
    private SlideChooseView timeChooseView;
    private TextView timeEditText;
    private HeaderCell timeHeaderCell;
    private int type;
    private SlideChooseView usesChooseView;
    private EditText usesEditText;
    private HeaderCell usesHeaderCell;
    private boolean firstLayout = true;
    private ArrayList<Integer> dispalyedDates = new ArrayList<>();
    private final int[] defaultDates = {3600, 86400, 604800};
    private ArrayList<Integer> dispalyedUses = new ArrayList<>();
    private final int[] defaultUses = {1, 10, 100};

    /* loaded from: classes4.dex */
    public interface Callback {
        void onLinkCreated(TLObject tLObject);

        void onLinkEdited(TLRPC.TL_chatInviteExported tL_chatInviteExported, TLObject tLObject);

        void onLinkRemoved(TLRPC.TL_chatInviteExported tL_chatInviteExported);

        void revokeLink(TLRPC.TL_chatInviteExported tL_chatInviteExported);
    }

    public LinkEditActivity(int type, long chatId) {
        this.type = type;
        this.chatId = chatId;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        int i = this.type;
        if (i == 0) {
            this.actionBar.setTitle(LocaleController.getString("NewLink", R.string.NewLink));
        } else if (i == 1) {
            this.actionBar.setTitle(LocaleController.getString("EditLink", R.string.EditLink));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.LinkEditActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    LinkEditActivity.this.finishFragment();
                    AndroidUtilities.hideKeyboard(LinkEditActivity.this.usesEditText);
                }
            }
        });
        TextView textView = new TextView(context);
        this.createTextView = textView;
        textView.setEllipsize(TextUtils.TruncateAt.END);
        this.createTextView.setGravity(16);
        this.createTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LinkEditActivity$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                LinkEditActivity.this.onCreateClicked(view);
            }
        });
        this.createTextView.setSingleLine();
        int i2 = this.type;
        if (i2 == 0) {
            this.createTextView.setText(LocaleController.getString("CreateLinkHeader", R.string.CreateLinkHeader));
        } else if (i2 == 1) {
            this.createTextView.setText(LocaleController.getString("SaveLinkHeader", R.string.SaveLinkHeader));
        }
        this.createTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
        this.createTextView.setTextSize(1, 14.0f);
        this.createTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.createTextView.setPadding(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(18.0f), AndroidUtilities.dp(8.0f));
        int topSpace = this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight / AndroidUtilities.dp(2.0f) : 0;
        this.actionBar.addView(this.createTextView, LayoutHelper.createFrame(-2, -2.0f, 8388629, 0.0f, topSpace, 0.0f, 0.0f));
        this.scrollView = new ScrollView(context);
        SizeNotifierFrameLayout contentView = new SizeNotifierFrameLayout(context) { // from class: org.telegram.ui.LinkEditActivity.2
            int oldKeyboardHeight;

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout
            protected AdjustPanLayoutHelper createAdjustPanLayoutHelper() {
                AdjustPanLayoutHelper panLayoutHelper = new AdjustPanLayoutHelper(this) { // from class: org.telegram.ui.LinkEditActivity.2.1
                    @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
                    public void onTransitionStart(boolean keyboardVisible, int contentHeight) {
                        super.onTransitionStart(keyboardVisible, contentHeight);
                        LinkEditActivity.this.scrollView.getLayoutParams().height = contentHeight;
                    }

                    @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
                    public void onTransitionEnd() {
                        super.onTransitionEnd();
                        LinkEditActivity.this.scrollView.getLayoutParams().height = -1;
                        LinkEditActivity.this.scrollView.requestLayout();
                    }

                    @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
                    public void onPanTranslationUpdate(float y, float progress, boolean keyboardVisible) {
                        super.onPanTranslationUpdate(y, progress, keyboardVisible);
                        setTranslationY(0.0f);
                    }

                    @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
                    protected boolean heightAnimationEnabled() {
                        return !LinkEditActivity.this.finished;
                    }
                };
                panLayoutHelper.setCheckHierarchyHeight(true);
                return panLayoutHelper;
            }

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.view.ViewGroup, android.view.View
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                this.adjustPanLayoutHelper.onAttach();
            }

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.view.ViewGroup, android.view.View
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                this.adjustPanLayoutHelper.onDetach();
            }

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                measureKeyboardHeight();
                boolean isNeedScrollToEnd = LinkEditActivity.this.usesEditText.isCursorVisible() || LinkEditActivity.this.nameEditText.isCursorVisible();
                if (this.oldKeyboardHeight == this.keyboardHeight || this.keyboardHeight <= AndroidUtilities.dp(20.0f) || !isNeedScrollToEnd) {
                    if (LinkEditActivity.this.scrollView.getScrollY() == 0 && !isNeedScrollToEnd) {
                        LinkEditActivity.this.scrollToStart = true;
                        invalidate();
                    }
                } else {
                    LinkEditActivity.this.scrollToEnd = true;
                    invalidate();
                }
                if (this.keyboardHeight != 0 && this.keyboardHeight < AndroidUtilities.dp(20.0f)) {
                    LinkEditActivity.this.usesEditText.clearFocus();
                    LinkEditActivity.this.nameEditText.clearFocus();
                }
                this.oldKeyboardHeight = this.keyboardHeight;
            }

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int scrollY = LinkEditActivity.this.scrollView.getScrollY();
                super.onLayout(changed, l, t, r, b);
                if (scrollY != LinkEditActivity.this.scrollView.getScrollY() && !LinkEditActivity.this.scrollToEnd) {
                    LinkEditActivity.this.scrollView.setTranslationY(LinkEditActivity.this.scrollView.getScrollY() - scrollY);
                    LinkEditActivity.this.scrollView.animate().cancel();
                    LinkEditActivity.this.scrollView.animate().translationY(0.0f).setDuration(250L).setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator).start();
                }
            }

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (LinkEditActivity.this.scrollToEnd) {
                    LinkEditActivity.this.scrollToEnd = false;
                    LinkEditActivity.this.scrollView.smoothScrollTo(0, Math.max(0, LinkEditActivity.this.scrollView.getChildAt(0).getMeasuredHeight() - LinkEditActivity.this.scrollView.getMeasuredHeight()));
                } else if (LinkEditActivity.this.scrollToStart) {
                    LinkEditActivity.this.scrollToStart = false;
                    LinkEditActivity.this.scrollView.smoothScrollTo(0, 0);
                }
            }
        };
        this.fragmentView = contentView;
        LinearLayout linearLayout = new LinearLayout(context) { // from class: org.telegram.ui.LinkEditActivity.3
            @Override // android.widget.LinearLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int topMargin;
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                int elementsHeight = 0;
                int h = View.MeasureSpec.getSize(heightMeasureSpec);
                for (int i3 = 0; i3 < getChildCount(); i3++) {
                    View child = getChildAt(i3);
                    if (child != LinkEditActivity.this.buttonTextView && child.getVisibility() != 8) {
                        elementsHeight += child.getMeasuredHeight();
                    }
                }
                int buttonH = AndroidUtilities.dp(48.0f) + AndroidUtilities.dp(24.0f) + AndroidUtilities.dp(16.0f);
                if (elementsHeight >= h - buttonH) {
                    topMargin = AndroidUtilities.dp(24.0f);
                } else {
                    topMargin = (AndroidUtilities.dp(24.0f) + (h - buttonH)) - elementsHeight;
                }
                if (((LinearLayout.LayoutParams) LinkEditActivity.this.buttonTextView.getLayoutParams()).topMargin != topMargin) {
                    int oldMargin = ((LinearLayout.LayoutParams) LinkEditActivity.this.buttonTextView.getLayoutParams()).topMargin;
                    ((LinearLayout.LayoutParams) LinkEditActivity.this.buttonTextView.getLayoutParams()).topMargin = topMargin;
                    if (!LinkEditActivity.this.firstLayout) {
                        LinkEditActivity.this.buttonTextView.setTranslationY(oldMargin - topMargin);
                        LinkEditActivity.this.buttonTextView.animate().translationY(0.0f).setDuration(250L).setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator).start();
                    }
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                LinkEditActivity.this.firstLayout = false;
            }
        };
        LayoutTransition transition = new LayoutTransition();
        transition.setDuration(100L);
        linearLayout.setLayoutTransition(transition);
        linearLayout.setOrientation(1);
        this.scrollView.addView(linearLayout);
        TextView textView2 = new TextView(context);
        this.buttonTextView = textView2;
        textView2.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        int i3 = this.type;
        if (i3 == 0) {
            this.buttonTextView.setText(LocaleController.getString("CreateLink", R.string.CreateLink));
        } else if (i3 == 1) {
            this.buttonTextView.setText(LocaleController.getString("SaveLink", R.string.SaveLink));
        }
        TextCheckCell textCheckCell = new TextCheckCell(context) { // from class: org.telegram.ui.LinkEditActivity.4
            @Override // org.telegram.ui.Cells.TextCheckCell, android.view.View
            public void onDraw(Canvas canvas) {
                canvas.save();
                canvas.clipRect(0, 0, getWidth(), getHeight());
                super.onDraw(canvas);
                canvas.restore();
            }
        };
        this.approveCell = textCheckCell;
        textCheckCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundUnchecked));
        this.approveCell.setColors(Theme.key_windowBackgroundCheckText, Theme.key_switchTrackBlue, Theme.key_switchTrackBlueChecked, Theme.key_switchTrackBlueThumb, Theme.key_switchTrackBlueThumbChecked);
        this.approveCell.setDrawCheckRipple(true);
        this.approveCell.setHeight(56);
        this.approveCell.setTag(Theme.key_windowBackgroundUnchecked);
        this.approveCell.setTextAndCheck(LocaleController.getString("ApproveNewMembers", R.string.ApproveNewMembers), false, false);
        this.approveCell.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.approveCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LinkEditActivity$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                LinkEditActivity.this.m3678lambda$createView$0$orgtelegramuiLinkEditActivity(view);
            }
        });
        linearLayout.addView(this.approveCell, LayoutHelper.createLinear(-1, 56));
        TextInfoPrivacyCell hintCell = new TextInfoPrivacyCell(context);
        hintCell.setBackground(Theme.getThemedDrawable(context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
        hintCell.setText(LocaleController.getString("ApproveNewMembersDescription", R.string.ApproveNewMembersDescription));
        linearLayout.addView(hintCell);
        HeaderCell headerCell = new HeaderCell(context);
        this.timeHeaderCell = headerCell;
        headerCell.setText(LocaleController.getString("LimitByPeriod", R.string.LimitByPeriod));
        linearLayout.addView(this.timeHeaderCell);
        SlideChooseView slideChooseView = new SlideChooseView(context);
        this.timeChooseView = slideChooseView;
        linearLayout.addView(slideChooseView);
        TextView textView3 = new TextView(context);
        this.timeEditText = textView3;
        textView3.setPadding(AndroidUtilities.dp(22.0f), 0, AndroidUtilities.dp(22.0f), 0);
        this.timeEditText.setGravity(16);
        this.timeEditText.setTextSize(1, 16.0f);
        this.timeEditText.setHint(LocaleController.getString("TimeLimitHint", R.string.TimeLimitHint));
        this.timeEditText.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LinkEditActivity$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                LinkEditActivity.this.m3680lambda$createView$2$orgtelegramuiLinkEditActivity(context, view);
            }
        });
        this.timeChooseView.setCallback(new SlideChooseView.Callback() { // from class: org.telegram.ui.LinkEditActivity$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.Components.SlideChooseView.Callback
            public final void onOptionSelected(int i4) {
                LinkEditActivity.this.m3681lambda$createView$3$orgtelegramuiLinkEditActivity(i4);
            }

            @Override // org.telegram.ui.Components.SlideChooseView.Callback
            public /* synthetic */ void onTouchEnd() {
                SlideChooseView.Callback.CC.$default$onTouchEnd(this);
            }
        });
        resetDates();
        linearLayout.addView(this.timeEditText, LayoutHelper.createLinear(-1, 50));
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
        this.divider = textInfoPrivacyCell;
        textInfoPrivacyCell.setText(LocaleController.getString("TimeLimitHelp", R.string.TimeLimitHelp));
        linearLayout.addView(this.divider);
        HeaderCell headerCell2 = new HeaderCell(context);
        this.usesHeaderCell = headerCell2;
        headerCell2.setText(LocaleController.getString("LimitNumberOfUses", R.string.LimitNumberOfUses));
        linearLayout.addView(this.usesHeaderCell);
        SlideChooseView slideChooseView2 = new SlideChooseView(context);
        this.usesChooseView = slideChooseView2;
        slideChooseView2.setCallback(new SlideChooseView.Callback() { // from class: org.telegram.ui.LinkEditActivity$$ExternalSyntheticLambda3
            @Override // org.telegram.ui.Components.SlideChooseView.Callback
            public final void onOptionSelected(int i4) {
                LinkEditActivity.this.m3682lambda$createView$4$orgtelegramuiLinkEditActivity(i4);
            }

            @Override // org.telegram.ui.Components.SlideChooseView.Callback
            public /* synthetic */ void onTouchEnd() {
                SlideChooseView.Callback.CC.$default$onTouchEnd(this);
            }
        });
        resetUses();
        linearLayout.addView(this.usesChooseView);
        EditText editText = new EditText(context) { // from class: org.telegram.ui.LinkEditActivity.5
            @Override // android.widget.TextView, android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == 1) {
                    setCursorVisible(true);
                }
                return super.onTouchEvent(event);
            }
        };
        this.usesEditText = editText;
        editText.setPadding(AndroidUtilities.dp(22.0f), 0, AndroidUtilities.dp(22.0f), 0);
        this.usesEditText.setGravity(16);
        this.usesEditText.setTextSize(1, 16.0f);
        this.usesEditText.setHint(LocaleController.getString("UsesLimitHint", R.string.UsesLimitHint));
        this.usesEditText.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        this.usesEditText.setInputType(2);
        this.usesEditText.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.LinkEditActivity.6
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i4, int i1, int i22) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i4, int i1, int i22) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                if (LinkEditActivity.this.ignoreSet) {
                    return;
                }
                if (editable.toString().equals("0")) {
                    LinkEditActivity.this.usesEditText.setText("");
                    return;
                }
                try {
                    int customUses = Integer.parseInt(editable.toString());
                    if (customUses > 100000) {
                        LinkEditActivity.this.resetUses();
                    } else {
                        LinkEditActivity.this.chooseUses(customUses);
                    }
                } catch (NumberFormatException e) {
                    LinkEditActivity.this.resetUses();
                }
            }
        });
        linearLayout.addView(this.usesEditText, LayoutHelper.createLinear(-1, 50));
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context);
        this.dividerUses = textInfoPrivacyCell2;
        textInfoPrivacyCell2.setText(LocaleController.getString("UsesLimitHelp", R.string.UsesLimitHelp));
        linearLayout.addView(this.dividerUses);
        EditText editText2 = new EditText(context) { // from class: org.telegram.ui.LinkEditActivity.7
            @Override // android.widget.TextView, android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == 1) {
                    setCursorVisible(true);
                }
                return super.onTouchEvent(event);
            }
        };
        this.nameEditText = editText2;
        editText2.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.LinkEditActivity.8
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
                SpannableStringBuilder builder = new SpannableStringBuilder(s);
                Emoji.replaceEmoji(builder, LinkEditActivity.this.nameEditText.getPaint().getFontMetricsInt(), (int) LinkEditActivity.this.nameEditText.getPaint().getTextSize(), false);
                int selection = LinkEditActivity.this.nameEditText.getSelectionStart();
                LinkEditActivity.this.nameEditText.removeTextChangedListener(this);
                LinkEditActivity.this.nameEditText.setText(builder);
                if (selection >= 0) {
                    LinkEditActivity.this.nameEditText.setSelection(selection);
                }
                LinkEditActivity.this.nameEditText.addTextChangedListener(this);
            }
        });
        this.nameEditText.setCursorVisible(false);
        this.nameEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
        this.nameEditText.setGravity(16);
        this.nameEditText.setHint(LocaleController.getString("LinkNameHint", R.string.LinkNameHint));
        this.nameEditText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        this.nameEditText.setLines(1);
        this.nameEditText.setPadding(AndroidUtilities.dp(22.0f), 0, AndroidUtilities.dp(22.0f), 0);
        this.nameEditText.setSingleLine();
        this.nameEditText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameEditText.setTextSize(1, 16.0f);
        linearLayout.addView(this.nameEditText, LayoutHelper.createLinear(-1, 50));
        TextInfoPrivacyCell textInfoPrivacyCell3 = new TextInfoPrivacyCell(context);
        this.dividerName = textInfoPrivacyCell3;
        textInfoPrivacyCell3.setBackground(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.dividerName.setText(LocaleController.getString("LinkNameHelp", R.string.LinkNameHelp));
        linearLayout.addView(this.dividerName);
        if (this.type == 1) {
            TextSettingsCell textSettingsCell = new TextSettingsCell(context);
            this.revokeLink = textSettingsCell;
            textSettingsCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.revokeLink.setText(LocaleController.getString("RevokeLink", R.string.RevokeLink), false);
            this.revokeLink.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText5));
            this.revokeLink.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LinkEditActivity$$ExternalSyntheticLambda5
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    LinkEditActivity.this.m3684lambda$createView$6$orgtelegramuiLinkEditActivity(view);
                }
            });
            linearLayout.addView(this.revokeLink);
        }
        contentView.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
        linearLayout.addView(this.buttonTextView, LayoutHelper.createFrame(-1, 48.0f, 80, 16.0f, 15.0f, 16.0f, 16.0f));
        this.timeHeaderCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.timeChooseView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.timeEditText.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.usesHeaderCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.usesChooseView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.usesEditText.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.nameEditText.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        contentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.buttonTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LinkEditActivity$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                LinkEditActivity.this.onCreateClicked(view);
            }
        });
        this.buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.dividerUses.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.divider.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
        this.buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed)));
        this.usesEditText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.usesEditText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        this.timeEditText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.timeEditText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        this.usesEditText.setCursorVisible(false);
        setInviteToEdit(this.inviteToEdit);
        contentView.setClipChildren(false);
        this.scrollView.setClipChildren(false);
        linearLayout.setClipChildren(false);
        return contentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-LinkEditActivity */
    public /* synthetic */ void m3678lambda$createView$0$orgtelegramuiLinkEditActivity(View view) {
        TextCheckCell cell = (TextCheckCell) view;
        boolean newIsChecked = !cell.isChecked();
        cell.setBackgroundColorAnimated(newIsChecked, Theme.getColor(newIsChecked ? Theme.key_windowBackgroundChecked : Theme.key_windowBackgroundUnchecked));
        cell.setChecked(newIsChecked);
        setUsesVisible(!newIsChecked);
        this.firstLayout = true;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-LinkEditActivity */
    public /* synthetic */ void m3679lambda$createView$1$orgtelegramuiLinkEditActivity(boolean notify, int scheduleDate) {
        chooseDate(scheduleDate);
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-LinkEditActivity */
    public /* synthetic */ void m3680lambda$createView$2$orgtelegramuiLinkEditActivity(Context context, View view) {
        AlertsCreator.createDatePickerDialog(context, -1L, new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.LinkEditActivity$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
            public final void didSelectDate(boolean z, int i) {
                LinkEditActivity.this.m3679lambda$createView$1$orgtelegramuiLinkEditActivity(z, i);
            }
        });
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-LinkEditActivity */
    public /* synthetic */ void m3681lambda$createView$3$orgtelegramuiLinkEditActivity(int index) {
        if (index < this.dispalyedDates.size()) {
            long date = this.dispalyedDates.get(index).intValue() + getConnectionsManager().getCurrentTime();
            this.timeEditText.setText(LocaleController.formatDateAudio(date, false));
            return;
        }
        this.timeEditText.setText("");
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-LinkEditActivity */
    public /* synthetic */ void m3682lambda$createView$4$orgtelegramuiLinkEditActivity(int index) {
        this.usesEditText.clearFocus();
        this.ignoreSet = true;
        if (index < this.dispalyedUses.size()) {
            this.usesEditText.setText(this.dispalyedUses.get(index).toString());
        } else {
            this.usesEditText.setText("");
        }
        this.ignoreSet = false;
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-LinkEditActivity */
    public /* synthetic */ void m3684lambda$createView$6$orgtelegramuiLinkEditActivity(View view) {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
        builder2.setMessage(LocaleController.getString("RevokeAlert", R.string.RevokeAlert));
        builder2.setTitle(LocaleController.getString("RevokeLink", R.string.RevokeLink));
        builder2.setPositiveButton(LocaleController.getString("RevokeButton", R.string.RevokeButton), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LinkEditActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                LinkEditActivity.this.m3683lambda$createView$5$orgtelegramuiLinkEditActivity(dialogInterface, i);
            }
        });
        builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder2.create());
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-LinkEditActivity */
    public /* synthetic */ void m3683lambda$createView$5$orgtelegramuiLinkEditActivity(DialogInterface dialogInterface2, int i2) {
        this.callback.revokeLink(this.inviteToEdit);
        finishFragment();
    }

    public void onCreateClicked(View view) {
        if (this.loading) {
            return;
        }
        int timeIndex = this.timeChooseView.getSelectedIndex();
        if (timeIndex < this.dispalyedDates.size() && this.dispalyedDates.get(timeIndex).intValue() < 0) {
            AndroidUtilities.shakeView(this.timeEditText, 2.0f, 0);
            Vibrator vibrator = (Vibrator) this.timeEditText.getContext().getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200L);
                return;
            }
            return;
        }
        int i = this.type;
        if (i == 0) {
            AlertDialog alertDialog = this.progressDialog;
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
            this.loading = true;
            AlertDialog alertDialog2 = new AlertDialog(getParentActivity(), 3);
            this.progressDialog = alertDialog2;
            alertDialog2.showDelayed(500L);
            TLRPC.TL_messages_exportChatInvite req = new TLRPC.TL_messages_exportChatInvite();
            req.peer = getMessagesController().getInputPeer(-this.chatId);
            req.legacy_revoke_permanent = false;
            int i2 = this.timeChooseView.getSelectedIndex();
            req.flags |= 1;
            if (i2 < this.dispalyedDates.size()) {
                req.expire_date = this.dispalyedDates.get(i2).intValue() + getConnectionsManager().getCurrentTime();
            } else {
                req.expire_date = 0;
            }
            int i3 = this.usesChooseView.getSelectedIndex();
            req.flags |= 2;
            if (i3 < this.dispalyedUses.size()) {
                req.usage_limit = this.dispalyedUses.get(i3).intValue();
            } else {
                req.usage_limit = 0;
            }
            req.request_needed = this.approveCell.isChecked();
            if (req.request_needed) {
                req.usage_limit = 0;
            }
            req.title = this.nameEditText.getText().toString();
            if (!TextUtils.isEmpty(req.title)) {
                req.flags |= 16;
            }
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.LinkEditActivity$$ExternalSyntheticLambda11
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LinkEditActivity.this.m3688lambda$onCreateClicked$8$orgtelegramuiLinkEditActivity(tLObject, tL_error);
                }
            });
        } else if (i == 1) {
            AlertDialog alertDialog3 = this.progressDialog;
            if (alertDialog3 != null) {
                alertDialog3.dismiss();
            }
            TLRPC.TL_messages_editExportedChatInvite req2 = new TLRPC.TL_messages_editExportedChatInvite();
            req2.link = this.inviteToEdit.link;
            req2.revoked = false;
            req2.peer = getMessagesController().getInputPeer(-this.chatId);
            boolean edited = false;
            int i4 = this.timeChooseView.getSelectedIndex();
            if (i4 < this.dispalyedDates.size()) {
                if (this.currentInviteDate != this.dispalyedDates.get(i4).intValue()) {
                    req2.flags |= 1;
                    req2.expire_date = this.dispalyedDates.get(i4).intValue() + getConnectionsManager().getCurrentTime();
                    edited = true;
                }
            } else if (this.currentInviteDate != 0) {
                req2.flags |= 1;
                req2.expire_date = 0;
                edited = true;
            }
            int i5 = this.usesChooseView.getSelectedIndex();
            if (i5 < this.dispalyedUses.size()) {
                int newLimit = this.dispalyedUses.get(i5).intValue();
                if (this.inviteToEdit.usage_limit != newLimit) {
                    req2.flags |= 2;
                    req2.usage_limit = newLimit;
                    edited = true;
                }
            } else if (this.inviteToEdit.usage_limit != 0) {
                req2.flags |= 2;
                req2.usage_limit = 0;
                edited = true;
            }
            if (this.inviteToEdit.request_needed != this.approveCell.isChecked()) {
                req2.flags |= 8;
                req2.request_needed = this.approveCell.isChecked();
                if (req2.request_needed) {
                    req2.flags |= 2;
                    req2.usage_limit = 0;
                }
                edited = true;
            }
            String newTitle = this.nameEditText.getText().toString();
            if (!TextUtils.equals(this.inviteToEdit.title, newTitle)) {
                req2.title = newTitle;
                req2.flags |= 16;
                edited = true;
            }
            if (edited) {
                this.loading = true;
                AlertDialog alertDialog4 = new AlertDialog(getParentActivity(), 3);
                this.progressDialog = alertDialog4;
                alertDialog4.showDelayed(500L);
                getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.ui.LinkEditActivity$$ExternalSyntheticLambda10
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        LinkEditActivity.this.m3686lambda$onCreateClicked$10$orgtelegramuiLinkEditActivity(tLObject, tL_error);
                    }
                });
                return;
            }
            finishFragment();
        }
    }

    /* renamed from: lambda$onCreateClicked$8$org-telegram-ui-LinkEditActivity */
    public /* synthetic */ void m3688lambda$onCreateClicked$8$orgtelegramuiLinkEditActivity(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LinkEditActivity$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                LinkEditActivity.this.m3687lambda$onCreateClicked$7$orgtelegramuiLinkEditActivity(error, response);
            }
        });
    }

    /* renamed from: lambda$onCreateClicked$7$org-telegram-ui-LinkEditActivity */
    public /* synthetic */ void m3687lambda$onCreateClicked$7$orgtelegramuiLinkEditActivity(TLRPC.TL_error error, TLObject response) {
        this.loading = false;
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        if (error == null) {
            Callback callback = this.callback;
            if (callback != null) {
                callback.onLinkCreated(response);
            }
            finishFragment();
            return;
        }
        AlertsCreator.showSimpleAlert(this, error.text);
    }

    /* renamed from: lambda$onCreateClicked$10$org-telegram-ui-LinkEditActivity */
    public /* synthetic */ void m3686lambda$onCreateClicked$10$orgtelegramuiLinkEditActivity(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LinkEditActivity$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                LinkEditActivity.this.m3689lambda$onCreateClicked$9$orgtelegramuiLinkEditActivity(error, response);
            }
        });
    }

    /* renamed from: lambda$onCreateClicked$9$org-telegram-ui-LinkEditActivity */
    public /* synthetic */ void m3689lambda$onCreateClicked$9$orgtelegramuiLinkEditActivity(TLRPC.TL_error error, TLObject response) {
        this.loading = false;
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        if (error == null) {
            if (response instanceof TLRPC.TL_messages_exportedChatInvite) {
                this.inviteToEdit = (TLRPC.TL_chatInviteExported) ((TLRPC.TL_messages_exportedChatInvite) response).invite;
            }
            Callback callback = this.callback;
            if (callback != null) {
                callback.onLinkEdited(this.inviteToEdit, response);
            }
            finishFragment();
            return;
        }
        AlertsCreator.showSimpleAlert(this, error.text);
    }

    public void chooseUses(int customUses) {
        int position = 0;
        boolean added = false;
        this.dispalyedUses.clear();
        int i = 0;
        while (true) {
            int[] iArr = this.defaultUses;
            if (i >= iArr.length) {
                break;
            }
            if (!added && customUses <= iArr[i]) {
                if (customUses != iArr[i]) {
                    this.dispalyedUses.add(Integer.valueOf(customUses));
                }
                position = i;
                added = true;
            }
            this.dispalyedUses.add(Integer.valueOf(this.defaultUses[i]));
            i++;
        }
        if (!added) {
            this.dispalyedUses.add(Integer.valueOf(customUses));
            position = this.defaultUses.length;
        }
        String[] options = new String[this.dispalyedUses.size() + 1];
        for (int i2 = 0; i2 < options.length; i2++) {
            if (i2 == options.length - 1) {
                options[i2] = LocaleController.getString("NoLimit", R.string.NoLimit);
            } else {
                options[i2] = this.dispalyedUses.get(i2).toString();
            }
        }
        this.usesChooseView.setOptions(position, options);
    }

    private void chooseDate(int selectedDate) {
        this.timeEditText.setText(LocaleController.formatDateAudio(selectedDate, false));
        int selectedDate2 = selectedDate - getConnectionsManager().getCurrentTime();
        int position = 0;
        boolean added = false;
        this.dispalyedDates.clear();
        int i = 0;
        while (true) {
            int[] iArr = this.defaultDates;
            if (i >= iArr.length) {
                break;
            }
            if (!added && selectedDate2 < iArr[i]) {
                this.dispalyedDates.add(Integer.valueOf(selectedDate2));
                position = i;
                added = true;
            }
            this.dispalyedDates.add(Integer.valueOf(this.defaultDates[i]));
            i++;
        }
        if (!added) {
            this.dispalyedDates.add(Integer.valueOf(selectedDate2));
            position = this.defaultDates.length;
        }
        String[] options = new String[this.dispalyedDates.size() + 1];
        for (int i2 = 0; i2 < options.length; i2++) {
            if (i2 == options.length - 1) {
                options[i2] = LocaleController.getString("NoLimit", R.string.NoLimit);
            } else if (this.dispalyedDates.get(i2).intValue() == this.defaultDates[0]) {
                options[i2] = LocaleController.formatPluralString("Hours", 1, new Object[0]);
            } else if (this.dispalyedDates.get(i2).intValue() == this.defaultDates[1]) {
                options[i2] = LocaleController.formatPluralString("Days", 1, new Object[0]);
            } else if (this.dispalyedDates.get(i2).intValue() == this.defaultDates[2]) {
                options[i2] = LocaleController.formatPluralString("Weeks", 1, new Object[0]);
            } else if (selectedDate2 < 86400) {
                options[i2] = LocaleController.getString("MessageScheduleToday", R.string.MessageScheduleToday);
            } else if (selectedDate2 < 31449600) {
                options[i2] = LocaleController.getInstance().formatterScheduleDay.format(selectedDate * 1000);
            } else {
                options[i2] = LocaleController.getInstance().formatterYear.format(selectedDate * 1000);
            }
        }
        this.timeChooseView.setOptions(position, options);
    }

    private void resetDates() {
        this.dispalyedDates.clear();
        int i = 0;
        while (true) {
            int[] iArr = this.defaultDates;
            if (i < iArr.length) {
                this.dispalyedDates.add(Integer.valueOf(iArr[i]));
                i++;
            } else {
                String[] options = {LocaleController.formatPluralString("Hours", 1, new Object[0]), LocaleController.formatPluralString("Days", 1, new Object[0]), LocaleController.formatPluralString("Weeks", 1, new Object[0]), LocaleController.getString("NoLimit", R.string.NoLimit)};
                this.timeChooseView.setOptions(options.length - 1, options);
                return;
            }
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void resetUses() {
        this.dispalyedUses.clear();
        int i = 0;
        while (true) {
            int[] iArr = this.defaultUses;
            if (i < iArr.length) {
                this.dispalyedUses.add(Integer.valueOf(iArr[i]));
                i++;
            } else {
                String[] options = {IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "10", "100", LocaleController.getString("NoLimit", R.string.NoLimit)};
                this.usesChooseView.setOptions(options.length - 1, options);
                return;
            }
        }
    }

    public void setInviteToEdit(TLRPC.TL_chatInviteExported invite) {
        this.inviteToEdit = invite;
        if (this.fragmentView != null && invite != null) {
            if (invite.expire_date > 0) {
                chooseDate(invite.expire_date);
                this.currentInviteDate = this.dispalyedDates.get(this.timeChooseView.getSelectedIndex()).intValue();
            } else {
                this.currentInviteDate = 0;
            }
            if (invite.usage_limit > 0) {
                chooseUses(invite.usage_limit);
                this.usesEditText.setText(Integer.toString(invite.usage_limit));
            }
            this.approveCell.setBackgroundColor(Theme.getColor(invite.request_needed ? Theme.key_windowBackgroundChecked : Theme.key_windowBackgroundUnchecked));
            this.approveCell.setChecked(invite.request_needed);
            setUsesVisible(!invite.request_needed);
            if (!TextUtils.isEmpty(invite.title)) {
                SpannableStringBuilder builder = new SpannableStringBuilder(invite.title);
                Emoji.replaceEmoji(builder, this.nameEditText.getPaint().getFontMetricsInt(), (int) this.nameEditText.getPaint().getTextSize(), false);
                this.nameEditText.setText(builder);
            }
        }
    }

    private void setUsesVisible(boolean isVisible) {
        int i = 0;
        this.usesHeaderCell.setVisibility(isVisible ? 0 : 8);
        this.usesChooseView.setVisibility(isVisible ? 0 : 8);
        this.usesEditText.setVisibility(isVisible ? 0 : 8);
        TextInfoPrivacyCell textInfoPrivacyCell = this.dividerUses;
        if (!isVisible) {
            i = 8;
        }
        textInfoPrivacyCell.setVisibility(i);
        this.divider.setBackground(Theme.getThemedDrawable(getParentActivity(), isVisible ? R.drawable.greydivider : R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void finishFragment() {
        this.scrollView.getLayoutParams().height = this.scrollView.getHeight();
        this.finished = true;
        super.finishFragment();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate descriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.LinkEditActivity$$ExternalSyntheticLambda12
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                LinkEditActivity.this.m3685lambda$getThemeDescriptions$11$orgtelegramuiLinkEditActivity();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.timeHeaderCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.usesHeaderCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.timeHeaderCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.usesHeaderCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.timeChooseView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.usesChooseView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.timeEditText, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.usesEditText, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.revokeLink, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.divider, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.dividerUses, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.dividerName, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, descriptionDelegate, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, descriptionDelegate, Theme.key_featuredStickers_addButton));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, descriptionDelegate, Theme.key_featuredStickers_addButtonPressed));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, descriptionDelegate, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, descriptionDelegate, Theme.key_windowBackgroundWhiteGrayText));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, descriptionDelegate, Theme.key_featuredStickers_buttonText));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, descriptionDelegate, Theme.key_windowBackgroundWhiteRedText5));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$11$org-telegram-ui-LinkEditActivity */
    public /* synthetic */ void m3685lambda$getThemeDescriptions$11$orgtelegramuiLinkEditActivity() {
        TextInfoPrivacyCell textInfoPrivacyCell = this.dividerUses;
        if (textInfoPrivacyCell != null) {
            Context context = textInfoPrivacyCell.getContext();
            this.dividerUses.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            this.divider.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            this.buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed)));
            this.usesEditText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.usesEditText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            this.timeEditText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.timeEditText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            this.buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            TextSettingsCell textSettingsCell = this.revokeLink;
            if (textSettingsCell != null) {
                textSettingsCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText5));
            }
            this.createTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
            this.dividerName.setBackground(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            this.nameEditText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.nameEditText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        }
    }
}
