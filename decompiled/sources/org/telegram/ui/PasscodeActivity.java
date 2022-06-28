package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.CustomPhoneKeyboardView;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.OutlineTextContainerView;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.TextViewSwitcher;
import org.telegram.ui.Components.TransformableLoginButtonView;
import org.telegram.ui.Components.VerticalPositionAutoAnimator;
import org.telegram.ui.PasscodeActivity;
/* loaded from: classes4.dex */
public class PasscodeActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int ID_SWITCH_TYPE = 1;
    public static final int TYPE_ENTER_CODE_TO_MANAGE_SETTINGS = 2;
    public static final int TYPE_MANAGE_CODE_SETTINGS = 0;
    public static final int TYPE_SETUP_CODE = 1;
    private int autoLockDetailRow;
    private int autoLockRow;
    private int captureDetailRow;
    private int captureHeaderRow;
    private int captureRow;
    private int changePasscodeRow;
    private CodeFieldContainer codeFieldContainer;
    private TextViewSwitcher descriptionTextSwitcher;
    private int disablePasscodeRow;
    private int fingerprintRow;
    private String firstPassword;
    private VerticalPositionAutoAnimator floatingAutoAnimator;
    private Animator floatingButtonAnimator;
    private FrameLayout floatingButtonContainer;
    private TransformableLoginButtonView floatingButtonIcon;
    private int hintRow;
    private CustomPhoneKeyboardView keyboardView;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private RLottieImageView lockImageView;
    private Runnable onShowKeyboardCallback;
    private ActionBarMenuItem otherItem;
    private OutlineTextContainerView outlinePasswordView;
    private TextView passcodesDoNotMatchTextView;
    private ImageView passwordButton;
    private EditTextBoldCursor passwordEditText;
    private boolean postedHidePasscodesDoNotMatch;
    private int rowCount;
    private TextView titleTextView;
    private int type;
    private int utyanRow;
    private int currentPasswordType = 0;
    private int passcodeSetStep = 0;
    private Runnable hidePasscodesDoNotMatch = new Runnable() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda3
        @Override // java.lang.Runnable
        public final void run() {
            PasscodeActivity.this.m3974lambda$new$0$orgtelegramuiPasscodeActivity();
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface PasscodeActivityType {
    }

    /* renamed from: lambda$new$0$org-telegram-ui-PasscodeActivity */
    public /* synthetic */ void m3974lambda$new$0$orgtelegramuiPasscodeActivity() {
        this.postedHidePasscodesDoNotMatch = false;
        AndroidUtilities.updateViewVisibilityAnimated(this.passcodesDoNotMatchTextView, false);
    }

    public PasscodeActivity(int type) {
        this.type = type;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        if (this.type == 0) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
            return true;
        }
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.type == 0) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        final View view;
        ActionBarMenuSubItem switchItem;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.PasscodeActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    PasscodeActivity.this.finishFragment();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        if (this.type == 0) {
            view = frameLayout;
        } else {
            ScrollView scrollView = new ScrollView(context);
            scrollView.addView(frameLayout, LayoutHelper.createFrame(-1, -2.0f));
            scrollView.setFillViewport(true);
            view = scrollView;
        }
        SizeNotifierFrameLayout contentView = new SizeNotifierFrameLayout(context) { // from class: org.telegram.ui.PasscodeActivity.2
            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int frameBottom;
                if (PasscodeActivity.this.keyboardView.getVisibility() == 8 || measureKeyboardHeight() < AndroidUtilities.dp(20.0f)) {
                    if (PasscodeActivity.this.keyboardView.getVisibility() != 8) {
                        View view2 = view;
                        int measuredWidth = getMeasuredWidth();
                        int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(230.0f);
                        frameBottom = measuredHeight;
                        view2.layout(0, 0, measuredWidth, measuredHeight);
                    } else {
                        View view3 = view;
                        int measuredWidth2 = getMeasuredWidth();
                        int measuredHeight2 = getMeasuredHeight();
                        frameBottom = measuredHeight2;
                        view3.layout(0, 0, measuredWidth2, measuredHeight2);
                    }
                } else if (PasscodeActivity.this.isCustomKeyboardVisible()) {
                    View view4 = view;
                    int measuredWidth3 = getMeasuredWidth();
                    int measuredHeight3 = (getMeasuredHeight() - AndroidUtilities.dp(230.0f)) + measureKeyboardHeight();
                    frameBottom = measuredHeight3;
                    view4.layout(0, 0, measuredWidth3, measuredHeight3);
                } else {
                    View view5 = view;
                    int measuredWidth4 = getMeasuredWidth();
                    int measuredHeight4 = getMeasuredHeight();
                    frameBottom = measuredHeight4;
                    view5.layout(0, 0, measuredWidth4, measuredHeight4);
                }
                PasscodeActivity.this.keyboardView.layout(0, frameBottom, getMeasuredWidth(), AndroidUtilities.dp(230.0f) + frameBottom);
                notifyHeightChanged();
            }

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = View.MeasureSpec.getSize(widthMeasureSpec);
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(width, height);
                int frameHeight = height;
                if (PasscodeActivity.this.keyboardView.getVisibility() != 8 && measureKeyboardHeight() < AndroidUtilities.dp(20.0f)) {
                    frameHeight -= AndroidUtilities.dp(230.0f);
                }
                view.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(frameHeight, C.BUFFER_FLAG_ENCRYPTED));
                PasscodeActivity.this.keyboardView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(230.0f), C.BUFFER_FLAG_ENCRYPTED));
            }
        };
        contentView.setDelegate(new SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda15
            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate
            public final void onSizeChanged(int i, boolean z) {
                PasscodeActivity.this.m3965lambda$createView$1$orgtelegramuiPasscodeActivity(i, z);
            }
        });
        this.fragmentView = contentView;
        contentView.addView(view, LayoutHelper.createLinear(-1, 0, 1.0f));
        CustomPhoneKeyboardView customPhoneKeyboardView = new CustomPhoneKeyboardView(context);
        this.keyboardView = customPhoneKeyboardView;
        customPhoneKeyboardView.setVisibility(isCustomKeyboardVisible() ? 0 : 8);
        contentView.addView(this.keyboardView, LayoutHelper.createLinear(-1, CustomPhoneKeyboardView.KEYBOARD_HEIGHT_DP));
        switch (this.type) {
            case 0:
                this.actionBar.setTitle(LocaleController.getString("Passcode", R.string.Passcode));
                frameLayout.setTag(Theme.key_windowBackgroundGray);
                frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                RecyclerListView recyclerListView = new RecyclerListView(context);
                this.listView = recyclerListView;
                recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false) { // from class: org.telegram.ui.PasscodeActivity.3
                    @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                    public boolean supportsPredictiveItemAnimations() {
                        return false;
                    }
                });
                this.listView.setVerticalScrollBarEnabled(false);
                this.listView.setItemAnimator(null);
                this.listView.setLayoutAnimation(null);
                frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
                RecyclerListView recyclerListView2 = this.listView;
                ListAdapter listAdapter = new ListAdapter(context);
                this.listAdapter = listAdapter;
                recyclerListView2.setAdapter(listAdapter);
                this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda14
                    @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                    public final void onItemClick(View view2, int i) {
                        PasscodeActivity.this.m3971lambda$createView$5$orgtelegramuiPasscodeActivity(view2, i);
                    }
                });
                break;
            case 1:
            case 2:
                if (this.actionBar != null) {
                    this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
                    this.actionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), false);
                    this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarWhiteSelector), false);
                    this.actionBar.setCastShadows(false);
                    ActionBarMenu menu = this.actionBar.createMenu();
                    if (this.type == 1) {
                        ActionBarMenuItem addItem = menu.addItem(0, R.drawable.ic_ab_other);
                        this.otherItem = addItem;
                        switchItem = addItem.addSubItem(1, R.drawable.msg_permissions, LocaleController.getString((int) R.string.PasscodeSwitchToPassword));
                    } else {
                        switchItem = null;
                    }
                    this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass4(switchItem));
                }
                FrameLayout codeContainer = new FrameLayout(context);
                LinearLayout innerLinearLayout = new LinearLayout(context);
                innerLinearLayout.setOrientation(1);
                innerLinearLayout.setGravity(1);
                frameLayout.addView(innerLinearLayout, LayoutHelper.createFrame(-1, -1.0f));
                RLottieImageView rLottieImageView = new RLottieImageView(context);
                this.lockImageView = rLottieImageView;
                rLottieImageView.setFocusable(false);
                this.lockImageView.setAnimation(R.raw.tsv_setup_intro, 120, 120);
                this.lockImageView.setAutoRepeat(false);
                this.lockImageView.playAnimation();
                this.lockImageView.setVisibility((AndroidUtilities.isSmallScreen() || AndroidUtilities.displaySize.x >= AndroidUtilities.displaySize.y) ? 8 : 0);
                innerLinearLayout.addView(this.lockImageView, LayoutHelper.createLinear(120, 120, 1));
                TextView textView = new TextView(context);
                this.titleTextView = textView;
                textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                this.titleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                if (this.type == 1) {
                    if (SharedConfig.passcodeHash.length() != 0) {
                        this.titleTextView.setText(LocaleController.getString("EnterNewPasscode", R.string.EnterNewPasscode));
                    } else {
                        this.titleTextView.setText(LocaleController.getString("CreatePasscode", R.string.CreatePasscode));
                    }
                } else {
                    this.titleTextView.setText(LocaleController.getString((int) R.string.EnterYourPasscode));
                }
                this.titleTextView.setTextSize(1, 18.0f);
                this.titleTextView.setGravity(1);
                innerLinearLayout.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
                TextViewSwitcher textViewSwitcher = new TextViewSwitcher(context);
                this.descriptionTextSwitcher = textViewSwitcher;
                textViewSwitcher.setFactory(new ViewSwitcher.ViewFactory() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda1
                    @Override // android.widget.ViewSwitcher.ViewFactory
                    public final View makeView() {
                        return PasscodeActivity.lambda$createView$6(context);
                    }
                });
                this.descriptionTextSwitcher.setInAnimation(context, R.anim.alpha_in);
                this.descriptionTextSwitcher.setOutAnimation(context, R.anim.alpha_out);
                innerLinearLayout.addView(this.descriptionTextSwitcher, LayoutHelper.createLinear(-2, -2, 1, 20, 8, 20, 0));
                TextView forgotPasswordButton = new TextView(context);
                forgotPasswordButton.setTextSize(1, 14.0f);
                forgotPasswordButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_addButton));
                forgotPasswordButton.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
                forgotPasswordButton.setGravity((isPassword() ? 3 : 1) | 16);
                forgotPasswordButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda18
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        AlertsCreator.createForgotPasscodeDialog(context).show();
                    }
                });
                forgotPasswordButton.setVisibility(this.type == 2 ? 0 : 8);
                forgotPasswordButton.setText(LocaleController.getString((int) R.string.ForgotPasscode));
                frameLayout.addView(forgotPasswordButton, LayoutHelper.createFrame(-1, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 81, 0.0f, 0.0f, 0.0f, 16.0f));
                VerticalPositionAutoAnimator.attach(forgotPasswordButton);
                TextView textView2 = new TextView(context);
                this.passcodesDoNotMatchTextView = textView2;
                textView2.setTextSize(1, 14.0f);
                this.passcodesDoNotMatchTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
                this.passcodesDoNotMatchTextView.setText(LocaleController.getString((int) R.string.PasscodesDoNotMatchTryAgain));
                this.passcodesDoNotMatchTextView.setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f));
                AndroidUtilities.updateViewVisibilityAnimated(this.passcodesDoNotMatchTextView, false, 1.0f, false);
                frameLayout.addView(this.passcodesDoNotMatchTextView, LayoutHelper.createFrame(-2, -2.0f, 81, 0.0f, 0.0f, 0.0f, 16.0f));
                OutlineTextContainerView outlineTextContainerView = new OutlineTextContainerView(context);
                this.outlinePasswordView = outlineTextContainerView;
                outlineTextContainerView.setText(LocaleController.getString((int) R.string.EnterPassword));
                EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
                this.passwordEditText = editTextBoldCursor;
                editTextBoldCursor.setInputType(524417);
                this.passwordEditText.setTextSize(1, 18.0f);
                this.passwordEditText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                this.passwordEditText.setBackground(null);
                this.passwordEditText.setMaxLines(1);
                this.passwordEditText.setLines(1);
                this.passwordEditText.setGravity(LocaleController.isRTL ? 5 : 3);
                this.passwordEditText.setSingleLine(true);
                if (this.type == 1) {
                    this.passcodeSetStep = 0;
                    this.passwordEditText.setImeOptions(5);
                } else {
                    this.passcodeSetStep = 1;
                    this.passwordEditText.setImeOptions(6);
                }
                this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.passwordEditText.setTypeface(Typeface.DEFAULT);
                this.passwordEditText.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated));
                this.passwordEditText.setCursorSize(AndroidUtilities.dp(20.0f));
                this.passwordEditText.setCursorWidth(1.5f);
                int padding = AndroidUtilities.dp(16.0f);
                this.passwordEditText.setPadding(padding, padding, padding, padding);
                this.passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda21
                    @Override // android.view.View.OnFocusChangeListener
                    public final void onFocusChange(View view2, boolean z) {
                        PasscodeActivity.this.m3972lambda$createView$8$orgtelegramuiPasscodeActivity(view2, z);
                    }
                });
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(0);
                linearLayout.setGravity(16);
                linearLayout.addView(this.passwordEditText, LayoutHelper.createLinear(0, -2, 1.0f));
                ImageView imageView = new ImageView(context);
                this.passwordButton = imageView;
                imageView.setImageResource(R.drawable.msg_message);
                this.passwordButton.setColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
                this.passwordButton.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector), 1));
                AndroidUtilities.updateViewVisibilityAnimated(this.passwordButton, this.type == 1 && this.passcodeSetStep == 0, 0.1f, false);
                final AtomicBoolean isPasswordShown = new AtomicBoolean(false);
                this.passwordEditText.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.PasscodeActivity.5
                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable s) {
                        if (PasscodeActivity.this.type == 1 && PasscodeActivity.this.passcodeSetStep == 0) {
                            if (TextUtils.isEmpty(s) && PasscodeActivity.this.passwordButton.getVisibility() != 8) {
                                if (isPasswordShown.get()) {
                                    PasscodeActivity.this.passwordButton.callOnClick();
                                }
                                AndroidUtilities.updateViewVisibilityAnimated(PasscodeActivity.this.passwordButton, false, 0.1f, true);
                            } else if (!TextUtils.isEmpty(s) && PasscodeActivity.this.passwordButton.getVisibility() != 0) {
                                AndroidUtilities.updateViewVisibilityAnimated(PasscodeActivity.this.passwordButton, true, 0.1f, true);
                            }
                        }
                    }
                });
                this.passwordButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda20
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        PasscodeActivity.this.m3973lambda$createView$9$orgtelegramuiPasscodeActivity(isPasswordShown, view2);
                    }
                });
                linearLayout.addView(this.passwordButton, LayoutHelper.createLinearRelatively(24.0f, 24.0f, 0, 0.0f, 0.0f, 14.0f, 0.0f));
                this.outlinePasswordView.addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f));
                codeContainer.addView(this.outlinePasswordView, LayoutHelper.createLinear(-1, -2, 1, 32, 0, 32, 0));
                this.passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda23
                    @Override // android.widget.TextView.OnEditorActionListener
                    public final boolean onEditorAction(TextView textView3, int i, KeyEvent keyEvent) {
                        return PasscodeActivity.this.m3966lambda$createView$10$orgtelegramuiPasscodeActivity(textView3, i, keyEvent);
                    }
                });
                this.passwordEditText.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.PasscodeActivity.6
                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (PasscodeActivity.this.postedHidePasscodesDoNotMatch) {
                            PasscodeActivity.this.codeFieldContainer.removeCallbacks(PasscodeActivity.this.hidePasscodesDoNotMatch);
                            PasscodeActivity.this.hidePasscodesDoNotMatch.run();
                        }
                    }

                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable s) {
                    }
                });
                this.passwordEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() { // from class: org.telegram.ui.PasscodeActivity.7
                    @Override // android.view.ActionMode.Callback
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu2) {
                        return false;
                    }

                    @Override // android.view.ActionMode.Callback
                    public void onDestroyActionMode(ActionMode mode) {
                    }

                    @Override // android.view.ActionMode.Callback
                    public boolean onCreateActionMode(ActionMode mode, Menu menu2) {
                        return false;
                    }

                    @Override // android.view.ActionMode.Callback
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        return false;
                    }
                });
                AnonymousClass8 anonymousClass8 = new AnonymousClass8(context);
                this.codeFieldContainer = anonymousClass8;
                anonymousClass8.setNumbersCount(4, 10);
                CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
                int length = codeNumberFieldArr.length;
                int i = 0;
                View fragmentContentView = view;
                while (i < length) {
                    final CodeNumberField f = codeNumberFieldArr[i];
                    f.setShowSoftInputOnFocusCompat(!isCustomKeyboardVisible());
                    f.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    f.setTextSize(1, 24.0f);
                    f.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.PasscodeActivity.9
                        @Override // android.text.TextWatcher
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            if (PasscodeActivity.this.postedHidePasscodesDoNotMatch) {
                                PasscodeActivity.this.codeFieldContainer.removeCallbacks(PasscodeActivity.this.hidePasscodesDoNotMatch);
                                PasscodeActivity.this.hidePasscodesDoNotMatch.run();
                            }
                        }

                        @Override // android.text.TextWatcher
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override // android.text.TextWatcher
                        public void afterTextChanged(Editable s) {
                        }
                    });
                    f.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda22
                        @Override // android.view.View.OnFocusChangeListener
                        public final void onFocusChange(View view2, boolean z) {
                            PasscodeActivity.this.m3967lambda$createView$11$orgtelegramuiPasscodeActivity(f, view2, z);
                        }
                    });
                    i++;
                    codeNumberFieldArr = codeNumberFieldArr;
                    fragmentContentView = fragmentContentView;
                }
                codeContainer.addView(this.codeFieldContainer, LayoutHelper.createFrame(-2, -2.0f, 1, 40.0f, 10.0f, 40.0f, 0.0f));
                innerLinearLayout.addView(codeContainer, LayoutHelper.createLinear(-1, -2, 1, 0, 32, 0, 72));
                if (this.type == 1) {
                    frameLayout.setTag(Theme.key_windowBackgroundWhite);
                }
                this.floatingButtonContainer = new FrameLayout(context);
                if (Build.VERSION.SDK_INT >= 21) {
                    StateListAnimator animator = new StateListAnimator();
                    animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButtonIcon, "translationZ", AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
                    animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButtonIcon, "translationZ", AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
                    this.floatingButtonContainer.setStateListAnimator(animator);
                    this.floatingButtonContainer.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.PasscodeActivity.10
                        @Override // android.view.ViewOutlineProvider
                        public void getOutline(View view2, Outline outline) {
                            outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                        }
                    });
                }
                this.floatingAutoAnimator = VerticalPositionAutoAnimator.attach(this.floatingButtonContainer);
                frameLayout.addView(this.floatingButtonContainer, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 85, 0.0f, 0.0f, 24.0f, 16.0f));
                this.floatingButtonContainer.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda19
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        PasscodeActivity.this.m3968lambda$createView$12$orgtelegramuiPasscodeActivity(view2);
                    }
                });
                TransformableLoginButtonView transformableLoginButtonView = new TransformableLoginButtonView(context);
                this.floatingButtonIcon = transformableLoginButtonView;
                transformableLoginButtonView.setTransformType(1);
                this.floatingButtonIcon.setProgress(0.0f);
                this.floatingButtonIcon.setColor(Theme.getColor(Theme.key_chats_actionIcon));
                this.floatingButtonIcon.setDrawBackground(false);
                this.floatingButtonContainer.setContentDescription(LocaleController.getString((int) R.string.Next));
                this.floatingButtonContainer.addView(this.floatingButtonIcon, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f));
                Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
                if (Build.VERSION.SDK_INT < 21) {
                    Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
                    shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                    CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
                    combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    drawable = combinedDrawable;
                }
                this.floatingButtonContainer.setBackground(drawable);
                updateFields();
                break;
        }
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-PasscodeActivity */
    public /* synthetic */ void m3965lambda$createView$1$orgtelegramuiPasscodeActivity(int keyboardHeight, boolean isWidthGreater) {
        Runnable runnable;
        if (keyboardHeight >= AndroidUtilities.dp(20.0f) && (runnable = this.onShowKeyboardCallback) != null) {
            runnable.run();
            this.onShowKeyboardCallback = null;
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-PasscodeActivity */
    public /* synthetic */ void m3971lambda$createView$5$orgtelegramuiPasscodeActivity(View view, final int position) {
        if (!view.isEnabled()) {
            return;
        }
        if (position == this.disablePasscodeRow) {
            AlertDialog alertDialog = new AlertDialog.Builder(getParentActivity()).setTitle(LocaleController.getString((int) R.string.DisablePasscode)).setMessage(LocaleController.getString((int) R.string.DisablePasscodeConfirmMessage)).setNegativeButton(LocaleController.getString((int) R.string.Cancel), null).setPositiveButton(LocaleController.getString((int) R.string.DisablePasscodeTurnOff), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda16
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PasscodeActivity.this.m3969lambda$createView$2$orgtelegramuiPasscodeActivity(dialogInterface, i);
                }
            }).create();
            alertDialog.show();
            ((TextView) alertDialog.getButton(-1)).setTextColor(Theme.getColor(Theme.key_dialogTextRed));
        } else if (position == this.changePasscodeRow) {
            presentFragment(new PasscodeActivity(1));
        } else if (position != this.autoLockRow) {
            if (position == this.fingerprintRow) {
                SharedConfig.useFingerprint = !SharedConfig.useFingerprint;
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                ((TextCheckCell) view).setChecked(SharedConfig.useFingerprint);
            } else if (position == this.captureRow) {
                SharedConfig.allowScreenCapture = !SharedConfig.allowScreenCapture;
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                ((TextCheckCell) view).setChecked(SharedConfig.allowScreenCapture);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, false);
                if (!SharedConfig.allowScreenCapture) {
                    AlertsCreator.showSimpleAlert(this, LocaleController.getString("ScreenCaptureAlert", R.string.ScreenCaptureAlert));
                }
            }
        } else if (getParentActivity() == null) {
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AutoLock", R.string.AutoLock));
            final NumberPicker numberPicker = new NumberPicker(getParentActivity());
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(4);
            if (SharedConfig.autoLockIn == 0) {
                numberPicker.setValue(0);
            } else if (SharedConfig.autoLockIn == 60) {
                numberPicker.setValue(1);
            } else if (SharedConfig.autoLockIn == 300) {
                numberPicker.setValue(2);
            } else if (SharedConfig.autoLockIn == 3600) {
                numberPicker.setValue(3);
            } else if (SharedConfig.autoLockIn == 18000) {
                numberPicker.setValue(4);
            }
            numberPicker.setFormatter(PasscodeActivity$$ExternalSyntheticLambda13.INSTANCE);
            builder.setView(numberPicker);
            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda17
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PasscodeActivity.this.m3970lambda$createView$4$orgtelegramuiPasscodeActivity(numberPicker, position, dialogInterface, i);
                }
            });
            showDialog(builder.create());
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-PasscodeActivity */
    public /* synthetic */ void m3969lambda$createView$2$orgtelegramuiPasscodeActivity(DialogInterface dialog, int which) {
        SharedConfig.passcodeHash = "";
        SharedConfig.appLocked = false;
        SharedConfig.saveConfig();
        getMediaDataController().buildShortcuts();
        int count = this.listView.getChildCount();
        int a = 0;
        while (true) {
            if (a >= count) {
                break;
            }
            View child = this.listView.getChildAt(a);
            if (!(child instanceof TextSettingsCell)) {
                a++;
            } else {
                TextSettingsCell textCell = (TextSettingsCell) child;
                textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
                break;
            }
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
        finishFragment();
    }

    public static /* synthetic */ String lambda$createView$3(int value) {
        if (value == 0) {
            return LocaleController.getString("AutoLockDisabled", R.string.AutoLockDisabled);
        }
        return value == 1 ? LocaleController.formatString("AutoLockInTime", R.string.AutoLockInTime, LocaleController.formatPluralString("Minutes", 1, new Object[0])) : value == 2 ? LocaleController.formatString("AutoLockInTime", R.string.AutoLockInTime, LocaleController.formatPluralString("Minutes", 5, new Object[0])) : value == 3 ? LocaleController.formatString("AutoLockInTime", R.string.AutoLockInTime, LocaleController.formatPluralString("Hours", 1, new Object[0])) : value == 4 ? LocaleController.formatString("AutoLockInTime", R.string.AutoLockInTime, LocaleController.formatPluralString("Hours", 5, new Object[0])) : "";
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-PasscodeActivity */
    public /* synthetic */ void m3970lambda$createView$4$orgtelegramuiPasscodeActivity(NumberPicker numberPicker, int position, DialogInterface dialog, int which) {
        int which2 = numberPicker.getValue();
        if (which2 == 0) {
            SharedConfig.autoLockIn = 0;
        } else if (which2 == 1) {
            SharedConfig.autoLockIn = 60;
        } else if (which2 == 2) {
            SharedConfig.autoLockIn = 300;
        } else if (which2 == 3) {
            SharedConfig.autoLockIn = 3600;
        } else if (which2 == 4) {
            SharedConfig.autoLockIn = 18000;
        }
        this.listAdapter.notifyItemChanged(position);
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
    }

    /* renamed from: org.telegram.ui.PasscodeActivity$4 */
    /* loaded from: classes4.dex */
    public class AnonymousClass4 extends ActionBar.ActionBarMenuOnItemClick {
        final /* synthetic */ ActionBarMenuSubItem val$switchItem;

        AnonymousClass4(ActionBarMenuSubItem actionBarMenuSubItem) {
            PasscodeActivity.this = this$0;
            this.val$switchItem = actionBarMenuSubItem;
        }

        @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
        public void onItemClick(int id) {
            CodeNumberField[] codeNumberFieldArr;
            if (id == -1) {
                PasscodeActivity.this.finishFragment();
                return;
            }
            int i = 1;
            if (id == 1) {
                PasscodeActivity passcodeActivity = PasscodeActivity.this;
                if (passcodeActivity.currentPasswordType != 0) {
                    i = 0;
                }
                passcodeActivity.currentPasswordType = i;
                final ActionBarMenuSubItem actionBarMenuSubItem = this.val$switchItem;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PasscodeActivity$4$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PasscodeActivity.AnonymousClass4.this.m3983lambda$onItemClick$0$orgtelegramuiPasscodeActivity$4(actionBarMenuSubItem);
                    }
                }, 150L);
                PasscodeActivity.this.passwordEditText.setText("");
                for (CodeNumberField f : PasscodeActivity.this.codeFieldContainer.codeField) {
                    f.setText("");
                }
                PasscodeActivity.this.updateFields();
            }
        }

        /* renamed from: lambda$onItemClick$0$org-telegram-ui-PasscodeActivity$4 */
        public /* synthetic */ void m3983lambda$onItemClick$0$orgtelegramuiPasscodeActivity$4(ActionBarMenuSubItem switchItem) {
            switchItem.setText(LocaleController.getString(PasscodeActivity.this.currentPasswordType == 0 ? R.string.PasscodeSwitchToPassword : R.string.PasscodeSwitchToPIN));
            switchItem.setIcon(PasscodeActivity.this.currentPasswordType == 0 ? R.drawable.msg_permissions : R.drawable.msg_pin_code);
            PasscodeActivity.this.showKeyboard();
            if (PasscodeActivity.this.isPinCode()) {
                PasscodeActivity.this.passwordEditText.setInputType(524417);
                AndroidUtilities.updateViewVisibilityAnimated(PasscodeActivity.this.passwordButton, true, 0.1f, false);
            }
        }
    }

    public static /* synthetic */ View lambda$createView$6(Context context) {
        TextView tv = new TextView(context);
        tv.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        tv.setGravity(1);
        tv.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        tv.setTextSize(1, 15.0f);
        return tv;
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-PasscodeActivity */
    public /* synthetic */ void m3972lambda$createView$8$orgtelegramuiPasscodeActivity(View v, boolean hasFocus) {
        this.outlinePasswordView.animateSelection(hasFocus ? 1.0f : 0.0f);
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-PasscodeActivity */
    public /* synthetic */ void m3973lambda$createView$9$orgtelegramuiPasscodeActivity(AtomicBoolean isPasswordShown, View v) {
        isPasswordShown.set(!isPasswordShown.get());
        int selectionStart = this.passwordEditText.getSelectionStart();
        int selectionEnd = this.passwordEditText.getSelectionEnd();
        this.passwordEditText.setInputType((isPasswordShown.get() ? 144 : 128) | 1);
        this.passwordEditText.setSelection(selectionStart, selectionEnd);
        this.passwordButton.setColorFilter(Theme.getColor(isPasswordShown.get() ? Theme.key_windowBackgroundWhiteInputFieldActivated : Theme.key_windowBackgroundWhiteHintText));
    }

    /* renamed from: lambda$createView$10$org-telegram-ui-PasscodeActivity */
    public /* synthetic */ boolean m3966lambda$createView$10$orgtelegramuiPasscodeActivity(TextView textView, int i, KeyEvent keyEvent) {
        int i2 = this.passcodeSetStep;
        if (i2 == 0) {
            processNext();
            return true;
        } else if (i2 == 1) {
            processDone();
            return true;
        } else {
            return false;
        }
    }

    /* renamed from: org.telegram.ui.PasscodeActivity$8 */
    /* loaded from: classes4.dex */
    public class AnonymousClass8 extends CodeFieldContainer {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass8(Context context) {
            super(context);
            PasscodeActivity.this = this$0;
        }

        @Override // org.telegram.ui.CodeFieldContainer
        protected void processNextPressed() {
            if (PasscodeActivity.this.passcodeSetStep != 0) {
                PasscodeActivity.this.processDone();
            } else {
                postDelayed(new Runnable() { // from class: org.telegram.ui.PasscodeActivity$8$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PasscodeActivity.AnonymousClass8.this.m3984lambda$processNextPressed$0$orgtelegramuiPasscodeActivity$8();
                    }
                }, 260L);
            }
        }

        /* renamed from: lambda$processNextPressed$0$org-telegram-ui-PasscodeActivity$8 */
        public /* synthetic */ void m3984lambda$processNextPressed$0$orgtelegramuiPasscodeActivity$8() {
            PasscodeActivity.this.processNext();
        }
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-PasscodeActivity */
    public /* synthetic */ void m3967lambda$createView$11$orgtelegramuiPasscodeActivity(CodeNumberField f, View v, boolean hasFocus) {
        this.keyboardView.setEditText(f);
        this.keyboardView.setDispatchBackWhenEmpty(true);
    }

    /* renamed from: lambda$createView$12$org-telegram-ui-PasscodeActivity */
    public /* synthetic */ void m3968lambda$createView$12$orgtelegramuiPasscodeActivity(View view) {
        int i = this.type;
        if (i == 1) {
            if (this.passcodeSetStep == 0) {
                processNext();
            } else {
                processDone();
            }
        } else if (i == 2) {
            processDone();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean hasForceLightStatusBar() {
        return this.type != 0;
    }

    private void setCustomKeyboardVisible(final boolean visible, boolean animate) {
        if (visible) {
            AndroidUtilities.hideKeyboard(this.fragmentView);
            AndroidUtilities.requestAltFocusable(getParentActivity(), this.classGuid);
        } else {
            AndroidUtilities.removeAltFocusable(getParentActivity(), this.classGuid);
        }
        int i = 0;
        float f = 1.0f;
        float f2 = 0.0f;
        if (!animate) {
            CustomPhoneKeyboardView customPhoneKeyboardView = this.keyboardView;
            if (!visible) {
                i = 8;
            }
            customPhoneKeyboardView.setVisibility(i);
            CustomPhoneKeyboardView customPhoneKeyboardView2 = this.keyboardView;
            if (!visible) {
                f = 0.0f;
            }
            customPhoneKeyboardView2.setAlpha(f);
            CustomPhoneKeyboardView customPhoneKeyboardView3 = this.keyboardView;
            if (!visible) {
                f2 = AndroidUtilities.dp(230.0f);
            }
            customPhoneKeyboardView3.setTranslationY(f2);
            this.fragmentView.requestLayout();
            return;
        }
        float[] fArr = new float[2];
        fArr[0] = visible ? 0.0f : 1.0f;
        if (!visible) {
            f = 0.0f;
        }
        fArr[1] = f;
        ValueAnimator animator = ValueAnimator.ofFloat(fArr).setDuration(150L);
        animator.setInterpolator(visible ? CubicBezierInterpolator.DEFAULT : Easings.easeInOutQuad);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                PasscodeActivity.this.m3980x97ad0356(valueAnimator);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PasscodeActivity.11
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                if (visible) {
                    PasscodeActivity.this.keyboardView.setVisibility(0);
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (!visible) {
                    PasscodeActivity.this.keyboardView.setVisibility(8);
                }
            }
        });
        animator.start();
    }

    /* renamed from: lambda$setCustomKeyboardVisible$13$org-telegram-ui-PasscodeActivity */
    public /* synthetic */ void m3980x97ad0356(ValueAnimator animation) {
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        this.keyboardView.setAlpha(val);
        this.keyboardView.setTranslationY((1.0f - val) * AndroidUtilities.dp(230.0f) * 0.75f);
        this.fragmentView.requestLayout();
    }

    private void setFloatingButtonVisible(final boolean visible, boolean animate) {
        Animator animator = this.floatingButtonAnimator;
        if (animator != null) {
            animator.cancel();
            this.floatingButtonAnimator = null;
        }
        int i = 0;
        float f = 1.0f;
        if (!animate) {
            this.floatingAutoAnimator.setOffsetY(visible ? 0.0f : AndroidUtilities.dp(70.0f));
            FrameLayout frameLayout = this.floatingButtonContainer;
            if (!visible) {
                f = 0.0f;
            }
            frameLayout.setAlpha(f);
            FrameLayout frameLayout2 = this.floatingButtonContainer;
            if (!visible) {
                i = 8;
            }
            frameLayout2.setVisibility(i);
            return;
        }
        float[] fArr = new float[2];
        fArr[0] = visible ? 0.0f : 1.0f;
        if (!visible) {
            f = 0.0f;
        }
        fArr[1] = f;
        ValueAnimator animator2 = ValueAnimator.ofFloat(fArr).setDuration(150L);
        animator2.setInterpolator(visible ? AndroidUtilities.decelerateInterpolator : AndroidUtilities.accelerateInterpolator);
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda11
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                PasscodeActivity.this.m3981xf75af15(valueAnimator);
            }
        });
        animator2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PasscodeActivity.12
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                if (visible) {
                    PasscodeActivity.this.floatingButtonContainer.setVisibility(0);
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (!visible) {
                    PasscodeActivity.this.floatingButtonContainer.setVisibility(8);
                }
                if (PasscodeActivity.this.floatingButtonAnimator == animation) {
                    PasscodeActivity.this.floatingButtonAnimator = null;
                }
            }
        });
        animator2.start();
        this.floatingButtonAnimator = animator2;
    }

    /* renamed from: lambda$setFloatingButtonVisible$14$org-telegram-ui-PasscodeActivity */
    public /* synthetic */ void m3981xf75af15(ValueAnimator animation) {
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        this.floatingAutoAnimator.setOffsetY(AndroidUtilities.dp(70.0f) * (1.0f - val));
        this.floatingButtonContainer.setAlpha(val);
    }

    public static BaseFragment determineOpenFragment() {
        if (SharedConfig.passcodeHash.length() != 0) {
            return new PasscodeActivity(2);
        }
        return new ActionIntroActivity(6);
    }

    private void animateSuccessAnimation(final Runnable callback) {
        if (!isPinCode()) {
            callback.run();
            return;
        }
        for (int i = 0; i < this.codeFieldContainer.codeField.length; i++) {
            final CodeNumberField field = this.codeFieldContainer.codeField[i];
            field.postDelayed(new Runnable() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    CodeNumberField.this.animateSuccessProgress(1.0f);
                }
            }, i * 75);
        }
        this.codeFieldContainer.postDelayed(new Runnable() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                PasscodeActivity.this.m3964x5ef7bfc3(callback);
            }
        }, (this.codeFieldContainer.codeField.length * 75) + 350);
    }

    /* renamed from: lambda$animateSuccessAnimation$16$org-telegram-ui-PasscodeActivity */
    public /* synthetic */ void m3964x5ef7bfc3(Runnable callback) {
        CodeNumberField[] codeNumberFieldArr;
        for (CodeNumberField f : this.codeFieldContainer.codeField) {
            f.animateSuccessProgress(0.0f);
        }
        callback.run();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onConfigurationChanged(Configuration newConfig) {
        CodeNumberField[] codeNumberFieldArr;
        super.onConfigurationChanged(newConfig);
        setCustomKeyboardVisible(isCustomKeyboardVisible(), false);
        RLottieImageView rLottieImageView = this.lockImageView;
        if (rLottieImageView != null) {
            rLottieImageView.setVisibility((AndroidUtilities.isSmallScreen() || AndroidUtilities.displaySize.x >= AndroidUtilities.displaySize.y) ? 8 : 0);
        }
        for (CodeNumberField f : this.codeFieldContainer.codeField) {
            f.setShowSoftInputOnFocusCompat(!isCustomKeyboardVisible());
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        if (this.type != 0 && !isCustomKeyboardVisible()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    PasscodeActivity.this.showKeyboard();
                }
            }, 200L);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        if (isCustomKeyboardVisible()) {
            AndroidUtilities.hideKeyboard(this.fragmentView);
            AndroidUtilities.requestAltFocusable(getParentActivity(), this.classGuid);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        AndroidUtilities.removeAltFocusable(getParentActivity(), this.classGuid);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didSetPasscode) {
            if ((args.length == 0 || ((Boolean) args[0]).booleanValue()) && this.type == 0) {
                updateRows();
                ListAdapter listAdapter = this.listAdapter;
                if (listAdapter != null) {
                    listAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.utyanRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.hintRow = i;
        this.rowCount = i2 + 1;
        this.changePasscodeRow = i2;
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(ApplicationLoader.applicationContext);
                if (fingerprintManager.isHardwareDetected() && AndroidUtilities.isKeyguardSecure()) {
                    int i3 = this.rowCount;
                    this.rowCount = i3 + 1;
                    this.fingerprintRow = i3;
                } else {
                    this.fingerprintRow = -1;
                }
            } else {
                this.fingerprintRow = -1;
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
        int i4 = this.rowCount;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.autoLockRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.autoLockDetailRow = i5;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.captureHeaderRow = i6;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.captureRow = i7;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.captureDetailRow = i8;
        this.rowCount = i9 + 1;
        this.disablePasscodeRow = i9;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && this.type != 0) {
            showKeyboard();
        }
    }

    public void showKeyboard() {
        if (isPinCode()) {
            this.codeFieldContainer.codeField[0].requestFocus();
            if (!isCustomKeyboardVisible()) {
                AndroidUtilities.showKeyboard(this.codeFieldContainer.codeField[0]);
            }
        } else if (isPassword()) {
            this.passwordEditText.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    public void updateFields() {
        String text;
        int i = this.type;
        int i2 = R.string.CreatePasscodeInfoPIN;
        if (i == 2) {
            text = LocaleController.getString((int) R.string.EnterYourPasscodeInfo);
        } else if (this.passcodeSetStep == 0) {
            text = LocaleController.getString(this.currentPasswordType == 0 ? R.string.CreatePasscodeInfoPIN : R.string.CreatePasscodeInfoPassword);
        } else {
            text = this.descriptionTextSwitcher.getCurrentView().getText().toString();
        }
        final boolean animate = !this.descriptionTextSwitcher.getCurrentView().getText().equals(text) && !TextUtils.isEmpty(this.descriptionTextSwitcher.getCurrentView().getText());
        if (this.type == 2) {
            this.descriptionTextSwitcher.setText(LocaleController.getString((int) R.string.EnterYourPasscodeInfo), animate);
        } else if (this.passcodeSetStep == 0) {
            TextViewSwitcher textViewSwitcher = this.descriptionTextSwitcher;
            if (this.currentPasswordType != 0) {
                i2 = R.string.CreatePasscodeInfoPassword;
            }
            textViewSwitcher.setText(LocaleController.getString(i2), animate);
        }
        if (isPinCode()) {
            AndroidUtilities.updateViewVisibilityAnimated(this.codeFieldContainer, true, 1.0f, animate);
            AndroidUtilities.updateViewVisibilityAnimated(this.outlinePasswordView, false, 1.0f, animate);
        } else if (isPassword()) {
            AndroidUtilities.updateViewVisibilityAnimated(this.codeFieldContainer, false, 1.0f, animate);
            AndroidUtilities.updateViewVisibilityAnimated(this.outlinePasswordView, true, 1.0f, animate);
        }
        final boolean show = isPassword();
        if (show) {
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    PasscodeActivity.this.m3982lambda$updateFields$17$orgtelegramuiPasscodeActivity(show, animate);
                }
            };
            this.onShowKeyboardCallback = runnable;
            AndroidUtilities.runOnUIThread(runnable, 3000L);
        } else {
            setFloatingButtonVisible(show, animate);
        }
        setCustomKeyboardVisible(isCustomKeyboardVisible(), animate);
        showKeyboard();
    }

    /* renamed from: lambda$updateFields$17$org-telegram-ui-PasscodeActivity */
    public /* synthetic */ void m3982lambda$updateFields$17$orgtelegramuiPasscodeActivity(boolean show, boolean animate) {
        setFloatingButtonVisible(show, animate);
        AndroidUtilities.cancelRunOnUIThread(this.onShowKeyboardCallback);
    }

    public boolean isCustomKeyboardVisible() {
        return isPinCode() && this.type != 0 && !AndroidUtilities.isTablet() && AndroidUtilities.displaySize.x < AndroidUtilities.displaySize.y && !AndroidUtilities.isAccessibilityTouchExplorationEnabled();
    }

    public void processNext() {
        CodeNumberField[] codeNumberFieldArr;
        if ((this.currentPasswordType == 1 && this.passwordEditText.getText().length() == 0) || (this.currentPasswordType == 0 && this.codeFieldContainer.getCode().length() != 4)) {
            onPasscodeError();
            return;
        }
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisibility(8);
        }
        this.titleTextView.setText(LocaleController.getString("ConfirmCreatePasscode", R.string.ConfirmCreatePasscode));
        this.descriptionTextSwitcher.setText(AndroidUtilities.replaceTags(LocaleController.getString("PasscodeReinstallNotice", R.string.PasscodeReinstallNotice)));
        this.firstPassword = isPinCode() ? this.codeFieldContainer.getCode() : this.passwordEditText.getText().toString();
        this.passwordEditText.setText("");
        this.passwordEditText.setInputType(524417);
        for (CodeNumberField f : this.codeFieldContainer.codeField) {
            f.setText("");
        }
        showKeyboard();
        this.passcodeSetStep = 1;
    }

    public boolean isPinCode() {
        int i = this.type;
        if (i == 1 && this.currentPasswordType == 0) {
            return true;
        }
        return i == 2 && SharedConfig.passcodeType == 0;
    }

    private boolean isPassword() {
        int i = this.type;
        if (i == 1 && this.currentPasswordType == 1) {
            return true;
        }
        return i == 2 && SharedConfig.passcodeType == 1;
    }

    public void processDone() {
        if (isPassword() && this.passwordEditText.getText().length() == 0) {
            onPasscodeError();
            return;
        }
        String password = isPinCode() ? this.codeFieldContainer.getCode() : this.passwordEditText.getText().toString();
        int i = this.type;
        int i2 = 0;
        if (i == 1) {
            if (!this.firstPassword.equals(password)) {
                AndroidUtilities.updateViewVisibilityAnimated(this.passcodesDoNotMatchTextView, true);
                for (CodeNumberField f : this.codeFieldContainer.codeField) {
                    f.setText("");
                }
                if (isPinCode()) {
                    this.codeFieldContainer.codeField[0].requestFocus();
                }
                this.passwordEditText.setText("");
                onPasscodeError();
                this.codeFieldContainer.removeCallbacks(this.hidePasscodesDoNotMatch);
                this.codeFieldContainer.post(new Runnable() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda6
                    @Override // java.lang.Runnable
                    public final void run() {
                        PasscodeActivity.this.m3977lambda$processDone$18$orgtelegramuiPasscodeActivity();
                    }
                });
                return;
            }
            final boolean isFirst = SharedConfig.passcodeHash.length() == 0;
            try {
                SharedConfig.passcodeSalt = new byte[16];
                Utilities.random.nextBytes(SharedConfig.passcodeSalt);
                byte[] passcodeBytes = this.firstPassword.getBytes("UTF-8");
                byte[] bytes = new byte[passcodeBytes.length + 32];
                System.arraycopy(SharedConfig.passcodeSalt, 0, bytes, 0, 16);
                System.arraycopy(passcodeBytes, 0, bytes, 16, passcodeBytes.length);
                System.arraycopy(SharedConfig.passcodeSalt, 0, bytes, passcodeBytes.length + 16, 16);
                SharedConfig.passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(bytes, 0, bytes.length));
            } catch (Exception e) {
                FileLog.e(e);
            }
            SharedConfig.allowScreenCapture = true;
            SharedConfig.passcodeType = this.currentPasswordType;
            SharedConfig.saveConfig();
            this.passwordEditText.clearFocus();
            AndroidUtilities.hideKeyboard(this.passwordEditText);
            CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
            int length = codeNumberFieldArr.length;
            while (i2 < length) {
                CodeNumberField f2 = codeNumberFieldArr[i2];
                f2.clearFocus();
                AndroidUtilities.hideKeyboard(f2);
                i2++;
            }
            this.keyboardView.setEditText(null);
            animateSuccessAnimation(new Runnable() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    PasscodeActivity.this.m3978lambda$processDone$19$orgtelegramuiPasscodeActivity(isFirst);
                }
            });
        } else if (i == 2) {
            if (SharedConfig.passcodeRetryInMs > 0) {
                double d = SharedConfig.passcodeRetryInMs;
                Double.isNaN(d);
                int value = Math.max(1, (int) Math.ceil(d / 1000.0d));
                Toast.makeText(getParentActivity(), LocaleController.formatString("TooManyTries", R.string.TooManyTries, LocaleController.formatPluralString("Seconds", value, new Object[0])), 0).show();
                for (CodeNumberField f3 : this.codeFieldContainer.codeField) {
                    f3.setText("");
                }
                this.passwordEditText.setText("");
                if (isPinCode()) {
                    this.codeFieldContainer.codeField[0].requestFocus();
                }
                onPasscodeError();
            } else if (!SharedConfig.checkPasscode(password)) {
                SharedConfig.increaseBadPasscodeTries();
                this.passwordEditText.setText("");
                for (CodeNumberField f4 : this.codeFieldContainer.codeField) {
                    f4.setText("");
                }
                if (isPinCode()) {
                    this.codeFieldContainer.codeField[0].requestFocus();
                }
                onPasscodeError();
            } else {
                SharedConfig.badPasscodeTries = 0;
                SharedConfig.saveConfig();
                this.passwordEditText.clearFocus();
                AndroidUtilities.hideKeyboard(this.passwordEditText);
                CodeNumberField[] codeNumberFieldArr2 = this.codeFieldContainer.codeField;
                int length2 = codeNumberFieldArr2.length;
                while (i2 < length2) {
                    CodeNumberField f5 = codeNumberFieldArr2[i2];
                    f5.clearFocus();
                    AndroidUtilities.hideKeyboard(f5);
                    i2++;
                }
                this.keyboardView.setEditText(null);
                animateSuccessAnimation(new Runnable() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda7
                    @Override // java.lang.Runnable
                    public final void run() {
                        PasscodeActivity.this.m3979lambda$processDone$20$orgtelegramuiPasscodeActivity();
                    }
                });
            }
        }
    }

    /* renamed from: lambda$processDone$18$org-telegram-ui-PasscodeActivity */
    public /* synthetic */ void m3977lambda$processDone$18$orgtelegramuiPasscodeActivity() {
        this.codeFieldContainer.postDelayed(this.hidePasscodesDoNotMatch, 3000L);
        this.postedHidePasscodesDoNotMatch = true;
    }

    /* renamed from: lambda$processDone$19$org-telegram-ui-PasscodeActivity */
    public /* synthetic */ void m3978lambda$processDone$19$orgtelegramuiPasscodeActivity(boolean isFirst) {
        getMediaDataController().buildShortcuts();
        if (isFirst) {
            presentFragment(new PasscodeActivity(0), true);
        } else {
            finishFragment();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
    }

    /* renamed from: lambda$processDone$20$org-telegram-ui-PasscodeActivity */
    public /* synthetic */ void m3979lambda$processDone$20$orgtelegramuiPasscodeActivity() {
        presentFragment(new PasscodeActivity(0), true);
    }

    private void onPasscodeError() {
        CodeNumberField[] codeNumberFieldArr;
        if (getParentActivity() == null) {
            return;
        }
        try {
            this.fragmentView.performHapticFeedback(3, 2);
        } catch (Exception e) {
        }
        if (isPinCode()) {
            for (CodeNumberField f : this.codeFieldContainer.codeField) {
                f.animateErrorProgress(1.0f);
            }
        } else {
            this.outlinePasswordView.animateError(1.0f);
        }
        AndroidUtilities.shakeViewSpring(isPinCode() ? this.codeFieldContainer : this.outlinePasswordView, isPinCode() ? 10.0f : 4.0f, new Runnable() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                PasscodeActivity.this.m3976lambda$onPasscodeError$22$orgtelegramuiPasscodeActivity();
            }
        });
    }

    /* renamed from: lambda$onPasscodeError$22$org-telegram-ui-PasscodeActivity */
    public /* synthetic */ void m3976lambda$onPasscodeError$22$orgtelegramuiPasscodeActivity() {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                PasscodeActivity.this.m3975lambda$onPasscodeError$21$orgtelegramuiPasscodeActivity();
            }
        }, isPinCode() ? 150L : 1000L);
    }

    /* renamed from: lambda$onPasscodeError$21$org-telegram-ui-PasscodeActivity */
    public /* synthetic */ void m3975lambda$onPasscodeError$21$orgtelegramuiPasscodeActivity() {
        CodeNumberField[] codeNumberFieldArr;
        if (isPinCode()) {
            for (CodeNumberField f : this.codeFieldContainer.codeField) {
                f.animateErrorProgress(0.0f);
            }
            return;
        }
        this.outlinePasswordView.animateError(0.0f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private static final int VIEW_TYPE_CHECK = 0;
        private static final int VIEW_TYPE_HEADER = 3;
        private static final int VIEW_TYPE_INFO = 2;
        private static final int VIEW_TYPE_SETTING = 1;
        private static final int VIEW_TYPE_UTYAN = 4;
        private Context mContext;

        public ListAdapter(Context context) {
            PasscodeActivity.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == PasscodeActivity.this.fingerprintRow || position == PasscodeActivity.this.autoLockRow || position == PasscodeActivity.this.captureRow || position == PasscodeActivity.this.changePasscodeRow || position == PasscodeActivity.this.disablePasscodeRow;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return PasscodeActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View view2 = new TextCheckCell(this.mContext);
                    view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view2;
                    break;
                case 1:
                    View view3 = new TextSettingsCell(this.mContext);
                    view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view3;
                    break;
                case 2:
                default:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 3:
                    View view4 = new HeaderCell(this.mContext);
                    view4.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view4;
                    break;
                case 4:
                    view = new RLottieImageHolderView(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String val;
            switch (holder.getItemViewType()) {
                case 0:
                    TextCheckCell textCell = (TextCheckCell) holder.itemView;
                    if (position != PasscodeActivity.this.fingerprintRow) {
                        if (position == PasscodeActivity.this.captureRow) {
                            textCell.setTextAndCheck(LocaleController.getString((int) R.string.ScreenCaptureShowContent), SharedConfig.allowScreenCapture, false);
                            return;
                        }
                        return;
                    }
                    textCell.setTextAndCheck(LocaleController.getString("UnlockFingerprint", R.string.UnlockFingerprint), SharedConfig.useFingerprint, true);
                    return;
                case 1:
                    TextSettingsCell textCell2 = (TextSettingsCell) holder.itemView;
                    if (position != PasscodeActivity.this.changePasscodeRow) {
                        if (position != PasscodeActivity.this.autoLockRow) {
                            if (position == PasscodeActivity.this.disablePasscodeRow) {
                                textCell2.setText(LocaleController.getString((int) R.string.DisablePasscode), false);
                                textCell2.setTag(Theme.key_dialogTextRed);
                                textCell2.setTextColor(Theme.getColor(Theme.key_dialogTextRed));
                                return;
                            }
                            return;
                        }
                        if (SharedConfig.autoLockIn == 0) {
                            val = LocaleController.formatString("AutoLockDisabled", R.string.AutoLockDisabled, new Object[0]);
                        } else if (SharedConfig.autoLockIn < 3600) {
                            val = LocaleController.formatString("AutoLockInTime", R.string.AutoLockInTime, LocaleController.formatPluralString("Minutes", SharedConfig.autoLockIn / 60, new Object[0]));
                        } else {
                            val = SharedConfig.autoLockIn < 86400 ? LocaleController.formatString("AutoLockInTime", R.string.AutoLockInTime, LocaleController.formatPluralString("Hours", (int) Math.ceil((SharedConfig.autoLockIn / 60.0f) / 60.0f), new Object[0])) : LocaleController.formatString("AutoLockInTime", R.string.AutoLockInTime, LocaleController.formatPluralString("Days", (int) Math.ceil(((SharedConfig.autoLockIn / 60.0f) / 60.0f) / 24.0f), new Object[0]));
                        }
                        textCell2.setTextAndValue(LocaleController.getString("AutoLock", R.string.AutoLock), val, true);
                        textCell2.setTag(Theme.key_windowBackgroundWhiteBlackText);
                        textCell2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                        return;
                    }
                    textCell2.setText(LocaleController.getString("ChangePasscode", R.string.ChangePasscode), true);
                    if (SharedConfig.passcodeHash.length() == 0) {
                        textCell2.setTag(Theme.key_windowBackgroundWhiteGrayText7);
                        textCell2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
                        return;
                    }
                    textCell2.setTag(Theme.key_windowBackgroundWhiteBlackText);
                    textCell2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    return;
                case 2:
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position != PasscodeActivity.this.hintRow) {
                        int i = 5;
                        if (position != PasscodeActivity.this.autoLockDetailRow) {
                            if (position == PasscodeActivity.this.captureDetailRow) {
                                cell.setText(LocaleController.getString((int) R.string.ScreenCaptureInfo));
                                cell.setBackground(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                                TextView textView = cell.getTextView();
                                if (!LocaleController.isRTL) {
                                    i = 3;
                                }
                                textView.setGravity(i);
                                return;
                            }
                            return;
                        }
                        cell.setText(LocaleController.getString((int) R.string.AutoLockInfo));
                        cell.setBackground(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        TextView textView2 = cell.getTextView();
                        if (!LocaleController.isRTL) {
                            i = 3;
                        }
                        textView2.setGravity(i);
                        return;
                    }
                    cell.setText(LocaleController.getString((int) R.string.PasscodeScreenHint));
                    cell.setBackground(null);
                    cell.getTextView().setGravity(1);
                    return;
                case 3:
                    HeaderCell cell2 = (HeaderCell) holder.itemView;
                    cell2.setHeight(46);
                    if (position == PasscodeActivity.this.captureHeaderRow) {
                        cell2.setText(LocaleController.getString((int) R.string.ScreenCaptureHeader));
                        return;
                    }
                    return;
                case 4:
                    RLottieImageHolderView holderView = (RLottieImageHolderView) holder.itemView;
                    holderView.imageView.setAnimation(R.raw.utyan_passcode, 100, 100);
                    holderView.imageView.playAnimation();
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == PasscodeActivity.this.fingerprintRow || position == PasscodeActivity.this.captureRow) {
                return 0;
            }
            if (position != PasscodeActivity.this.changePasscodeRow && position != PasscodeActivity.this.autoLockRow && position != PasscodeActivity.this.disablePasscodeRow) {
                if (position != PasscodeActivity.this.autoLockDetailRow && position != PasscodeActivity.this.captureDetailRow && position != PasscodeActivity.this.hintRow) {
                    if (position == PasscodeActivity.this.captureHeaderRow) {
                        return 3;
                    }
                    return position == PasscodeActivity.this.utyanRow ? 4 : 0;
                }
                return 2;
            }
            return 1;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextCheckCell.class, TextSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItemIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6));
        themeDescriptions.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        themeDescriptions.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText7));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        return themeDescriptions;
    }

    /* loaded from: classes4.dex */
    public static final class RLottieImageHolderView extends FrameLayout {
        private RLottieImageView imageView;

        private RLottieImageHolderView(Context context) {
            super(context);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PasscodeActivity$RLottieImageHolderView$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PasscodeActivity.RLottieImageHolderView.this.m3985x8045aa27(view);
                }
            });
            int size = AndroidUtilities.dp(120.0f);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            params.gravity = 1;
            addView(this.imageView, params);
            setPadding(0, AndroidUtilities.dp(32.0f), 0, 0);
            setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-PasscodeActivity$RLottieImageHolderView */
        public /* synthetic */ void m3985x8045aa27(View v) {
            if (!this.imageView.getAnimatedDrawable().isRunning()) {
                this.imageView.getAnimatedDrawable().setCurrentFrame(0, false);
                this.imageView.playAnimation();
            }
        }
    }
}
