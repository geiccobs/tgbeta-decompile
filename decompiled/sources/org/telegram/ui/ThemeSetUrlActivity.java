package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.messaging.Constants;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.ThemePreviewMessagesCell;
import org.telegram.ui.Cells.ThemesHorizontalListCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes4.dex */
public class ThemeSetUrlActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int done_button = 1;
    private TextInfoPrivacyCell checkInfoCell;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextSettingsCell createCell;
    private TextInfoPrivacyCell createInfoCell;
    private boolean creatingNewTheme;
    private View divider;
    private View doneButton;
    private EditTextBoldCursor editText;
    private HeaderCell headerCell;
    private TextInfoPrivacyCell helpInfoCell;
    private boolean ignoreCheck;
    private TLRPC.TL_theme info;
    private CharSequence infoText;
    private String lastCheckName;
    private boolean lastNameAvailable;
    private LinearLayout linearLayoutTypeContainer;
    private EditTextBoldCursor linkField;
    private ThemePreviewMessagesCell messagesCell;
    private EditTextBoldCursor nameField;
    private AlertDialog progressDialog;
    private Theme.ThemeAccent themeAccent;
    private Theme.ThemeInfo themeInfo;

    /* loaded from: classes4.dex */
    public class LinkSpan extends ClickableSpan {
        private String url;

        public LinkSpan(String value) {
            ThemeSetUrlActivity.this = this$0;
            this.url = value;
        }

        @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

        @Override // android.text.style.ClickableSpan
        public void onClick(View widget) {
            try {
                ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
                ClipData clip = ClipData.newPlainText(Constants.ScionAnalytics.PARAM_LABEL, this.url);
                clipboard.setPrimaryClip(clip);
                if (BulletinFactory.canShowBulletin(ThemeSetUrlActivity.this)) {
                    BulletinFactory.createCopyLinkBulletin(ThemeSetUrlActivity.this).show();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    /* loaded from: classes4.dex */
    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        @Override // android.text.method.LinkMovementMethod, android.text.method.ScrollingMovementMethod, android.text.method.BaseMovementMethod, android.text.method.MovementMethod
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                boolean result = super.onTouchEvent(widget, buffer, event);
                if (event.getAction() == 1 || event.getAction() == 3) {
                    Selection.removeSelection(buffer);
                }
                return result;
            } catch (Exception e) {
                FileLog.e(e);
                return false;
            }
        }
    }

    public ThemeSetUrlActivity(Theme.ThemeInfo theme, Theme.ThemeAccent accent, boolean newTheme) {
        this.themeInfo = theme;
        this.themeAccent = accent;
        this.info = accent != null ? accent.info : theme.info;
        this.currentAccount = accent != null ? accent.account : theme.account;
        this.creatingNewTheme = newTheme;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.themeUploadedToServer);
        getNotificationCenter().addObserver(this, NotificationCenter.themeUploadError);
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.themeUploadedToServer);
        getNotificationCenter().removeObserver(this, NotificationCenter.themeUploadError);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.creatingNewTheme) {
            this.actionBar.setTitle(LocaleController.getString("NewThemeTitle", R.string.NewThemeTitle));
        } else {
            this.actionBar.setTitle(LocaleController.getString("EditThemeTitle", R.string.EditThemeTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ThemeSetUrlActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    ThemeSetUrlActivity.this.finishFragment();
                } else if (id == 1) {
                    ThemeSetUrlActivity.this.saveTheme();
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        this.doneButton = menu.addItem(1, LocaleController.getString("Done", R.string.Done).toUpperCase());
        this.fragmentView = new LinearLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        LinearLayout linearLayout = (LinearLayout) this.fragmentView;
        linearLayout.setOrientation(1);
        this.fragmentView.setOnTouchListener(ThemeSetUrlActivity$$ExternalSyntheticLambda8.INSTANCE);
        LinearLayout linearLayout2 = new LinearLayout(context);
        this.linearLayoutTypeContainer = linearLayout2;
        linearLayout2.setOrientation(1);
        this.linearLayoutTypeContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        linearLayout.addView(this.linearLayoutTypeContainer, LayoutHelper.createLinear(-1, -2));
        HeaderCell headerCell = new HeaderCell(context, 23);
        this.headerCell = headerCell;
        headerCell.setText(LocaleController.getString("Info", R.string.Info));
        this.linearLayoutTypeContainer.addView(this.headerCell);
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
        this.nameField = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 18.0f);
        this.nameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.nameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameField.setMaxLines(1);
        this.nameField.setLines(1);
        this.nameField.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.nameField.setBackgroundDrawable(null);
        this.nameField.setPadding(0, 0, 0, 0);
        this.nameField.setSingleLine(true);
        InputFilter[] inputFilters = {new InputFilter.LengthFilter(128)};
        this.nameField.setFilters(inputFilters);
        this.nameField.setInputType(163872);
        this.nameField.setImeOptions(6);
        this.nameField.setHint(LocaleController.getString("ThemeNamePlaceholder", R.string.ThemeNamePlaceholder));
        this.nameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.nameField.setCursorWidth(1.5f);
        this.linearLayoutTypeContainer.addView(this.nameField, LayoutHelper.createLinear(-1, 50, 23.0f, 0.0f, 23.0f, 0.0f));
        this.nameField.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.ThemeSetUrlActivity$$ExternalSyntheticLambda10
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return ThemeSetUrlActivity.this.m4661lambda$createView$1$orgtelegramuiThemeSetUrlActivity(textView, i, keyEvent);
            }
        });
        View view = new View(context) { // from class: org.telegram.ui.ThemeSetUrlActivity.2
            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(20.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        };
        this.divider = view;
        this.linearLayoutTypeContainer.addView(view, new LinearLayout.LayoutParams(-1, 1));
        LinearLayout linkContainer = new LinearLayout(context);
        linkContainer.setOrientation(0);
        this.linearLayoutTypeContainer.addView(linkContainer, LayoutHelper.createLinear(-1, 50, 23.0f, 0.0f, 23.0f, 0.0f));
        EditTextBoldCursor editTextBoldCursor2 = new EditTextBoldCursor(context);
        this.editText = editTextBoldCursor2;
        editTextBoldCursor2.setText(getMessagesController().linkPrefix + "/addtheme/");
        this.editText.setTextSize(1, 18.0f);
        this.editText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.editText.setMaxLines(1);
        this.editText.setLines(1);
        this.editText.setEnabled(false);
        this.editText.setBackgroundDrawable(null);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setSingleLine(true);
        this.editText.setInputType(163840);
        this.editText.setImeOptions(6);
        linkContainer.addView(this.editText, LayoutHelper.createLinear(-2, 50));
        EditTextBoldCursor editTextBoldCursor3 = new EditTextBoldCursor(context);
        this.linkField = editTextBoldCursor3;
        editTextBoldCursor3.setTextSize(1, 18.0f);
        this.linkField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.linkField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.linkField.setMaxLines(1);
        this.linkField.setLines(1);
        this.linkField.setBackgroundDrawable(null);
        this.linkField.setPadding(0, 0, 0, 0);
        this.linkField.setSingleLine(true);
        this.linkField.setInputType(163872);
        this.linkField.setImeOptions(6);
        this.linkField.setHint(LocaleController.getString("SetUrlPlaceholder", R.string.SetUrlPlaceholder));
        this.linkField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.linkField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.linkField.setCursorWidth(1.5f);
        linkContainer.addView(this.linkField, LayoutHelper.createLinear(-1, 50));
        this.linkField.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.ThemeSetUrlActivity$$ExternalSyntheticLambda11
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return ThemeSetUrlActivity.this.m4662lambda$createView$2$orgtelegramuiThemeSetUrlActivity(textView, i, keyEvent);
            }
        });
        this.linkField.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.ThemeSetUrlActivity.3
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (ThemeSetUrlActivity.this.ignoreCheck) {
                    return;
                }
                ThemeSetUrlActivity themeSetUrlActivity = ThemeSetUrlActivity.this;
                themeSetUrlActivity.checkUrl(themeSetUrlActivity.linkField.getText().toString(), false);
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                if (!ThemeSetUrlActivity.this.creatingNewTheme) {
                    if (ThemeSetUrlActivity.this.linkField.length() <= 0) {
                        ThemeSetUrlActivity.this.helpInfoCell.setText(ThemeSetUrlActivity.this.infoText);
                        return;
                    }
                    String url = "https://" + ThemeSetUrlActivity.this.getMessagesController().linkPrefix + "/addtheme/" + ((Object) ThemeSetUrlActivity.this.linkField.getText());
                    String text = LocaleController.formatString("ThemeHelpLink", R.string.ThemeHelpLink, url);
                    int index = text.indexOf(url);
                    SpannableStringBuilder textSpan = new SpannableStringBuilder(text);
                    if (index >= 0) {
                        textSpan.setSpan(new LinkSpan(url), index, url.length() + index, 33);
                    }
                    ThemeSetUrlActivity.this.helpInfoCell.setText(TextUtils.concat(ThemeSetUrlActivity.this.infoText, "\n\n", textSpan));
                }
            }
        });
        if (this.creatingNewTheme) {
            this.linkField.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.ThemeSetUrlActivity$$ExternalSyntheticLambda7
                @Override // android.view.View.OnFocusChangeListener
                public final void onFocusChange(View view2, boolean z) {
                    ThemeSetUrlActivity.this.m4663lambda$createView$3$orgtelegramuiThemeSetUrlActivity(view2, z);
                }
            });
        }
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
        this.checkInfoCell = textInfoPrivacyCell;
        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.checkInfoCell.setVisibility(8);
        this.checkInfoCell.setBottomPadding(0);
        linearLayout.addView(this.checkInfoCell, LayoutHelper.createLinear(-1, -2));
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context);
        this.helpInfoCell = textInfoPrivacyCell2;
        textInfoPrivacyCell2.getTextView().setMovementMethod(new LinkMovementMethodMy());
        this.helpInfoCell.getTextView().setHighlightColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkSelection));
        if (this.creatingNewTheme) {
            this.helpInfoCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("ThemeCreateHelp", R.string.ThemeCreateHelp)));
        } else {
            TextInfoPrivacyCell textInfoPrivacyCell3 = this.helpInfoCell;
            SpannableStringBuilder replaceTags = AndroidUtilities.replaceTags(LocaleController.getString("ThemeSetUrlHelp", R.string.ThemeSetUrlHelp));
            this.infoText = replaceTags;
            textInfoPrivacyCell3.setText(replaceTags);
        }
        linearLayout.addView(this.helpInfoCell, LayoutHelper.createLinear(-1, -2));
        if (!this.creatingNewTheme) {
            this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        } else {
            this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            ThemePreviewMessagesCell themePreviewMessagesCell = new ThemePreviewMessagesCell(context, this.parentLayout, 1);
            this.messagesCell = themePreviewMessagesCell;
            linearLayout.addView(themePreviewMessagesCell, LayoutHelper.createLinear(-1, -2));
            TextSettingsCell textSettingsCell = new TextSettingsCell(context);
            this.createCell = textSettingsCell;
            textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.createCell.setText(LocaleController.getString("UseDifferentTheme", R.string.UseDifferentTheme), false);
            linearLayout.addView(this.createCell, LayoutHelper.createLinear(-1, -2));
            this.createCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ThemeSetUrlActivity$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ThemeSetUrlActivity.this.m4664lambda$createView$5$orgtelegramuiThemeSetUrlActivity(context, view2);
                }
            });
            TextInfoPrivacyCell textInfoPrivacyCell4 = new TextInfoPrivacyCell(context);
            this.createInfoCell = textInfoPrivacyCell4;
            textInfoPrivacyCell4.setText(AndroidUtilities.replaceTags(LocaleController.getString("UseDifferentThemeInfo", R.string.UseDifferentThemeInfo)));
            this.createInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            linearLayout.addView(this.createInfoCell, LayoutHelper.createLinear(-1, -2));
        }
        TLRPC.TL_theme tL_theme = this.info;
        if (tL_theme != null) {
            this.ignoreCheck = true;
            this.nameField.setText(tL_theme.title);
            EditTextBoldCursor editTextBoldCursor4 = this.nameField;
            editTextBoldCursor4.setSelection(editTextBoldCursor4.length());
            this.linkField.setText(this.info.slug);
            EditTextBoldCursor editTextBoldCursor5 = this.linkField;
            editTextBoldCursor5.setSelection(editTextBoldCursor5.length());
            this.ignoreCheck = false;
        }
        return this.fragmentView;
    }

    public static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-ThemeSetUrlActivity */
    public /* synthetic */ boolean m4661lambda$createView$1$orgtelegramuiThemeSetUrlActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 6) {
            AndroidUtilities.hideKeyboard(this.nameField);
            return true;
        }
        return false;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-ThemeSetUrlActivity */
    public /* synthetic */ boolean m4662lambda$createView$2$orgtelegramuiThemeSetUrlActivity(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i == 6 && (view = this.doneButton) != null) {
            view.performClick();
            return true;
        }
        return false;
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-ThemeSetUrlActivity */
    public /* synthetic */ void m4663lambda$createView$3$orgtelegramuiThemeSetUrlActivity(View v, boolean hasFocus) {
        if (hasFocus) {
            this.helpInfoCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("ThemeCreateHelp2", R.string.ThemeCreateHelp2)));
        } else {
            this.helpInfoCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("ThemeCreateHelp", R.string.ThemeCreateHelp)));
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ThemeSetUrlActivity */
    public /* synthetic */ void m4664lambda$createView$5$orgtelegramuiThemeSetUrlActivity(Context context, View v) {
        if (getParentActivity() == null) {
            return;
        }
        final BottomSheet.Builder builder = new BottomSheet.Builder((Context) getParentActivity(), false);
        builder.setApplyBottomPadding(false);
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(1);
        TextView titleView = new TextView(context);
        titleView.setText(LocaleController.getString("ChooseTheme", R.string.ChooseTheme));
        titleView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        titleView.setTextSize(1, 20.0f);
        titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        container.addView(titleView, LayoutHelper.createLinear(-1, -2, 51, 22, 12, 22, 4));
        titleView.setOnTouchListener(ThemeSetUrlActivity$$ExternalSyntheticLambda9.INSTANCE);
        builder.setCustomView(container);
        ArrayList<Theme.ThemeInfo> themes = new ArrayList<>();
        int N = Theme.themes.size();
        for (int a = 0; a < N; a++) {
            Theme.ThemeInfo themeInfo = Theme.themes.get(a);
            if (themeInfo.info == null || themeInfo.info.document != null) {
                themes.add(themeInfo);
            }
        }
        ThemesHorizontalListCell cell = new ThemesHorizontalListCell(context, 2, themes, new ArrayList()) { // from class: org.telegram.ui.ThemeSetUrlActivity.4
            @Override // org.telegram.ui.Cells.ThemesHorizontalListCell
            protected void updateRows() {
                builder.getDismissRunnable().run();
            }
        };
        container.addView(cell, LayoutHelper.createLinear(-1, 148, 0.0f, 7.0f, 0.0f, 1.0f));
        cell.scrollToCurrentTheme(this.fragmentView.getMeasuredWidth(), false);
        showDialog(builder.create());
    }

    public static /* synthetic */ boolean lambda$createView$4(View v2, MotionEvent event) {
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        boolean animations = preferences.getBoolean("view_animations", true);
        if (!animations && this.creatingNewTheme) {
            this.linkField.requestFocus();
            AndroidUtilities.showKeyboard(this.linkField);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        AlertDialog alertDialog;
        AlertDialog alertDialog2;
        if (id == NotificationCenter.themeUploadedToServer) {
            Theme.ThemeInfo theme = (Theme.ThemeInfo) args[0];
            Theme.ThemeAccent accent = (Theme.ThemeAccent) args[1];
            if (theme == this.themeInfo && accent == this.themeAccent && (alertDialog2 = this.progressDialog) != null) {
                try {
                    alertDialog2.dismiss();
                    this.progressDialog = null;
                } catch (Exception e) {
                    FileLog.e(e);
                }
                Theme.applyTheme(this.themeInfo, false);
                finishFragment();
            }
        } else if (id == NotificationCenter.themeUploadError) {
            Theme.ThemeInfo theme2 = (Theme.ThemeInfo) args[0];
            Theme.ThemeAccent accent2 = (Theme.ThemeAccent) args[1];
            if (theme2 == this.themeInfo && accent2 == this.themeAccent && (alertDialog = this.progressDialog) != null) {
                try {
                    alertDialog.dismiss();
                    this.progressDialog = null;
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
        }
    }

    public boolean checkUrl(final String url, boolean alert) {
        Runnable runnable = this.checkRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.checkRunnable = null;
            this.lastCheckName = null;
            if (this.checkReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.checkReqId, true);
            }
        }
        this.lastNameAvailable = false;
        if (url != null) {
            if (url.startsWith("_") || url.endsWith("_")) {
                setCheckText(LocaleController.getString("SetUrlInvalid", R.string.SetUrlInvalid), Theme.key_windowBackgroundWhiteRedText4);
                return false;
            }
            for (int a = 0; a < url.length(); a++) {
                char ch = url.charAt(a);
                if (a == 0 && ch >= '0' && ch <= '9') {
                    if (alert) {
                        AlertsCreator.showSimpleAlert(this, LocaleController.getString("Theme", R.string.Theme), LocaleController.getString("SetUrlInvalidStartNumber", R.string.SetUrlInvalidStartNumber));
                    } else {
                        setCheckText(LocaleController.getString("SetUrlInvalidStartNumber", R.string.SetUrlInvalidStartNumber), Theme.key_windowBackgroundWhiteRedText4);
                    }
                    return false;
                } else if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                    if (alert) {
                        AlertsCreator.showSimpleAlert(this, LocaleController.getString("Theme", R.string.Theme), LocaleController.getString("SetUrlInvalid", R.string.SetUrlInvalid));
                    } else {
                        setCheckText(LocaleController.getString("SetUrlInvalid", R.string.SetUrlInvalid), Theme.key_windowBackgroundWhiteRedText4);
                    }
                    return false;
                }
            }
        }
        if (url == null || url.length() < 5) {
            if (alert) {
                AlertsCreator.showSimpleAlert(this, LocaleController.getString("Theme", R.string.Theme), LocaleController.getString("SetUrlInvalidShort", R.string.SetUrlInvalidShort));
            } else {
                setCheckText(LocaleController.getString("SetUrlInvalidShort", R.string.SetUrlInvalidShort), Theme.key_windowBackgroundWhiteRedText4);
            }
            return false;
        } else if (url.length() > 64) {
            if (alert) {
                AlertsCreator.showSimpleAlert(this, LocaleController.getString("Theme", R.string.Theme), LocaleController.getString("SetUrlInvalidLong", R.string.SetUrlInvalidLong));
            } else {
                setCheckText(LocaleController.getString("SetUrlInvalidLong", R.string.SetUrlInvalidLong), Theme.key_windowBackgroundWhiteRedText4);
            }
            return false;
        } else {
            if (!alert) {
                TLRPC.TL_theme tL_theme = this.info;
                String currentUrl = (tL_theme == null || tL_theme.slug == null) ? "" : this.info.slug;
                if (url.equals(currentUrl)) {
                    setCheckText(LocaleController.formatString("SetUrlAvailable", R.string.SetUrlAvailable, url), Theme.key_windowBackgroundWhiteGreenText);
                    return true;
                }
                setCheckText(LocaleController.getString("SetUrlChecking", R.string.SetUrlChecking), Theme.key_windowBackgroundWhiteGrayText8);
                this.lastCheckName = url;
                Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.ThemeSetUrlActivity$$ExternalSyntheticLambda12
                    @Override // java.lang.Runnable
                    public final void run() {
                        ThemeSetUrlActivity.this.m4660lambda$checkUrl$8$orgtelegramuiThemeSetUrlActivity(url);
                    }
                };
                this.checkRunnable = runnable2;
                AndroidUtilities.runOnUIThread(runnable2, 300L);
            }
            return true;
        }
    }

    /* renamed from: lambda$checkUrl$8$org-telegram-ui-ThemeSetUrlActivity */
    public /* synthetic */ void m4660lambda$checkUrl$8$orgtelegramuiThemeSetUrlActivity(final String url) {
        TLRPC.TL_account_createTheme req = new TLRPC.TL_account_createTheme();
        req.slug = url;
        req.title = "";
        req.document = new TLRPC.TL_inputDocumentEmpty();
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ThemeSetUrlActivity$$ExternalSyntheticLambda3
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ThemeSetUrlActivity.this.m4659lambda$checkUrl$7$orgtelegramuiThemeSetUrlActivity(url, tLObject, tL_error);
            }
        }, 2);
    }

    /* renamed from: lambda$checkUrl$7$org-telegram-ui-ThemeSetUrlActivity */
    public /* synthetic */ void m4659lambda$checkUrl$7$orgtelegramuiThemeSetUrlActivity(final String url, TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ThemeSetUrlActivity$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                ThemeSetUrlActivity.this.m4658lambda$checkUrl$6$orgtelegramuiThemeSetUrlActivity(url, error);
            }
        });
    }

    /* renamed from: lambda$checkUrl$6$org-telegram-ui-ThemeSetUrlActivity */
    public /* synthetic */ void m4658lambda$checkUrl$6$orgtelegramuiThemeSetUrlActivity(String url, TLRPC.TL_error error) {
        this.checkReqId = 0;
        String str = this.lastCheckName;
        if (str != null && str.equals(url)) {
            if (error == null || (!"THEME_SLUG_INVALID".equals(error.text) && !"THEME_SLUG_OCCUPIED".equals(error.text))) {
                setCheckText(LocaleController.formatString("SetUrlAvailable", R.string.SetUrlAvailable, url), Theme.key_windowBackgroundWhiteGreenText);
                this.lastNameAvailable = true;
                return;
            }
            setCheckText(LocaleController.getString("SetUrlInUse", R.string.SetUrlInUse), Theme.key_windowBackgroundWhiteRedText4);
            this.lastNameAvailable = false;
        }
    }

    private void setCheckText(String text, String colorKey) {
        if (TextUtils.isEmpty(text)) {
            this.checkInfoCell.setVisibility(8);
            if (this.creatingNewTheme) {
                this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(getParentActivity(), (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                return;
            } else {
                this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(getParentActivity(), (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                return;
            }
        }
        this.checkInfoCell.setVisibility(0);
        this.checkInfoCell.setText(text);
        this.checkInfoCell.setTag(colorKey);
        this.checkInfoCell.setTextColor(colorKey);
        if (this.creatingNewTheme) {
            this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(getParentActivity(), (int) R.drawable.greydivider_top, Theme.key_windowBackgroundGrayShadow));
        } else {
            this.helpInfoCell.setBackgroundDrawable(null);
        }
    }

    public void saveTheme() {
        if (!checkUrl(this.linkField.getText().toString(), true) || getParentActivity() == null) {
            return;
        }
        if (this.nameField.length() == 0) {
            AlertsCreator.showSimpleAlert(this, LocaleController.getString("Theme", R.string.Theme), LocaleController.getString("ThemeNameInvalid", R.string.ThemeNameInvalid));
        } else if (this.creatingNewTheme) {
            String str = this.info.title;
            String str2 = this.info.slug;
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            this.progressDialog = alertDialog;
            alertDialog.setOnCancelListener(ThemeSetUrlActivity$$ExternalSyntheticLambda5.INSTANCE);
            this.progressDialog.show();
            Theme.ThemeInfo themeInfo = this.themeInfo;
            TLRPC.TL_theme tL_theme = this.info;
            String obj = this.nameField.getText().toString();
            tL_theme.title = obj;
            themeInfo.name = obj;
            this.themeInfo.info.slug = this.linkField.getText().toString();
            Theme.saveCurrentTheme(this.themeInfo, true, true, true);
        } else {
            String currentName = "";
            String currentUrl = this.info.slug == null ? currentName : this.info.slug;
            if (this.info.title != null) {
                currentName = this.info.title;
            }
            String newUrl = this.linkField.getText().toString();
            String newName = this.nameField.getText().toString();
            if (currentUrl.equals(newUrl) && currentName.equals(newName)) {
                finishFragment();
                return;
            }
            this.progressDialog = new AlertDialog(getParentActivity(), 3);
            final TLRPC.TL_account_updateTheme req = new TLRPC.TL_account_updateTheme();
            TLRPC.TL_inputTheme inputTheme = new TLRPC.TL_inputTheme();
            inputTheme.id = this.info.id;
            inputTheme.access_hash = this.info.access_hash;
            req.theme = inputTheme;
            req.format = "android";
            req.slug = newUrl;
            req.flags = 1 | req.flags;
            req.title = newName;
            req.flags |= 2;
            final int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ThemeSetUrlActivity$$ExternalSyntheticLambda4
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ThemeSetUrlActivity.this.m4667lambda$saveTheme$12$orgtelegramuiThemeSetUrlActivity(req, tLObject, tL_error);
                }
            }, 2);
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, this.classGuid);
            this.progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.ThemeSetUrlActivity$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnCancelListener
                public final void onCancel(DialogInterface dialogInterface) {
                    ThemeSetUrlActivity.this.m4668lambda$saveTheme$13$orgtelegramuiThemeSetUrlActivity(reqId, dialogInterface);
                }
            });
            this.progressDialog.show();
        }
    }

    public static /* synthetic */ void lambda$saveTheme$9(DialogInterface dialog) {
    }

    /* renamed from: lambda$saveTheme$12$org-telegram-ui-ThemeSetUrlActivity */
    public /* synthetic */ void m4667lambda$saveTheme$12$orgtelegramuiThemeSetUrlActivity(final TLRPC.TL_account_updateTheme req, TLObject response, final TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_theme) {
            final TLRPC.TL_theme theme = (TLRPC.TL_theme) response;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ThemeSetUrlActivity$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ThemeSetUrlActivity.this.m4665lambda$saveTheme$10$orgtelegramuiThemeSetUrlActivity(theme);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ThemeSetUrlActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ThemeSetUrlActivity.this.m4666lambda$saveTheme$11$orgtelegramuiThemeSetUrlActivity(error, req);
            }
        });
    }

    /* renamed from: lambda$saveTheme$10$org-telegram-ui-ThemeSetUrlActivity */
    public /* synthetic */ void m4665lambda$saveTheme$10$orgtelegramuiThemeSetUrlActivity(TLRPC.TL_theme theme) {
        try {
            this.progressDialog.dismiss();
            this.progressDialog = null;
        } catch (Exception e) {
            FileLog.e(e);
        }
        Theme.setThemeUploadInfo(this.themeInfo, this.themeAccent, theme, this.currentAccount, false);
        finishFragment();
    }

    /* renamed from: lambda$saveTheme$11$org-telegram-ui-ThemeSetUrlActivity */
    public /* synthetic */ void m4666lambda$saveTheme$11$orgtelegramuiThemeSetUrlActivity(TLRPC.TL_error error, TLRPC.TL_account_updateTheme req) {
        try {
            this.progressDialog.dismiss();
            this.progressDialog = null;
        } catch (Exception e) {
            FileLog.e(e);
        }
        AlertsCreator.processError(this.currentAccount, error, this, req, new Object[0]);
    }

    /* renamed from: lambda$saveTheme$13$org-telegram-ui-ThemeSetUrlActivity */
    public /* synthetic */ void m4668lambda$saveTheme$13$orgtelegramuiThemeSetUrlActivity(int reqId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(reqId, true);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !this.creatingNewTheme) {
            this.linkField.requestFocus();
            AndroidUtilities.showKeyboard(this.linkField);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.linearLayoutTypeContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.createInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.createInfoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.helpInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.helpInfoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.checkInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.checkInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText4));
        themeDescriptions.add(new ThemeDescription(this.checkInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText8));
        themeDescriptions.add(new ThemeDescription(this.checkInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGreenText));
        themeDescriptions.add(new ThemeDescription(this.createCell, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.createCell, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.createCell, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.linkField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.linkField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(this.linkField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        themeDescriptions.add(new ThemeDescription(this.linkField, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
        themeDescriptions.add(new ThemeDescription(this.linkField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.linkField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(this.linkField, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.nameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.nameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(this.nameField, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(this.divider, 0, null, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.divider, ThemeDescription.FLAG_BACKGROUND, null, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, Theme.key_chat_inBubble));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, Theme.key_chat_inBubbleSelected));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, Theme.chat_msgInDrawable.getShadowDrawables(), null, Theme.key_chat_inBubbleShadow));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), null, Theme.key_chat_inBubbleShadow));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubble));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient1));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient2));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient3));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, Theme.key_chat_outBubbleSelected));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, Theme.chat_msgOutDrawable.getShadowDrawables(), null, Theme.key_chat_outBubbleShadow));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, Theme.chat_msgOutMediaDrawable.getShadowDrawables(), null, Theme.key_chat_outBubbleShadow));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_messageTextIn));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_messageTextOut));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, Theme.key_chat_outSentCheck));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckSelected));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, Theme.key_chat_outSentCheckRead));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckReadSelected));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, Theme.key_chat_mediaSentCheck));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_inReplyLine));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_outReplyLine));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_inReplyNameText));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_outReplyNameText));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_inReplyMessageText));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_outReplyMessageText));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_inReplyMediaMessageSelectedText));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_outReplyMediaMessageSelectedText));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_inTimeText));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_outTimeText));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_inTimeSelectedText));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, null, null, null, null, Theme.key_chat_outTimeSelectedText));
        return themeDescriptions;
    }
}
