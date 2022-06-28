package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.Editable;
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
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes4.dex */
public class ChangeUsernameActivity extends BaseFragment {
    private static final int done_button = 1;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextView checkTextView;
    private View doneButton;
    private EditTextBoldCursor firstNameField;
    private TextView helpTextView;
    private boolean ignoreCheck;
    private CharSequence infoText;
    private String lastCheckName;
    private boolean lastNameAvailable;

    /* loaded from: classes4.dex */
    public class LinkSpan extends ClickableSpan {
        private String url;

        public LinkSpan(String value) {
            ChangeUsernameActivity.this = this$0;
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
                if (BulletinFactory.canShowBulletin(ChangeUsernameActivity.this)) {
                    BulletinFactory.createCopyLinkBulletin(ChangeUsernameActivity.this).show();
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

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Username", R.string.Username));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ChangeUsernameActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    ChangeUsernameActivity.this.finishFragment();
                } else if (id == 1) {
                    ChangeUsernameActivity.this.saveName();
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        this.doneButton = menu.addItemWithWidth(1, R.drawable.ic_ab_done, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", R.string.Done));
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user == null) {
            user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        this.fragmentView = new LinearLayout(context);
        LinearLayout linearLayout = (LinearLayout) this.fragmentView;
        linearLayout.setOrientation(1);
        this.fragmentView.setOnTouchListener(ChangeUsernameActivity$$ExternalSyntheticLambda1.INSTANCE);
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
        this.firstNameField = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 18.0f);
        this.firstNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.firstNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setBackgroundDrawable(null);
        this.firstNameField.setLineColors(getThemedColor(Theme.key_windowBackgroundWhiteInputField), getThemedColor(Theme.key_windowBackgroundWhiteInputFieldActivated), getThemedColor(Theme.key_windowBackgroundWhiteRedText3));
        this.firstNameField.setMaxLines(1);
        this.firstNameField.setLines(1);
        this.firstNameField.setPadding(0, 0, 0, 0);
        this.firstNameField.setSingleLine(true);
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setInputType(180224);
        this.firstNameField.setImeOptions(6);
        this.firstNameField.setHint(LocaleController.getString("UsernamePlaceholder", R.string.UsernamePlaceholder));
        this.firstNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.firstNameField.setCursorWidth(1.5f);
        this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.ChangeUsernameActivity$$ExternalSyntheticLambda2
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return ChangeUsernameActivity.this.m1701lambda$createView$1$orgtelegramuiChangeUsernameActivity(textView, i, keyEvent);
            }
        });
        this.firstNameField.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.ChangeUsernameActivity.2
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (ChangeUsernameActivity.this.ignoreCheck) {
                    return;
                }
                ChangeUsernameActivity changeUsernameActivity = ChangeUsernameActivity.this;
                changeUsernameActivity.checkUserName(changeUsernameActivity.firstNameField.getText().toString(), false);
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                String name = ChangeUsernameActivity.this.firstNameField.getText().toString();
                if (name.startsWith("@")) {
                    name = name.substring(1);
                }
                if (name.length() <= 0) {
                    ChangeUsernameActivity.this.helpTextView.setText(ChangeUsernameActivity.this.infoText);
                    return;
                }
                String url = "https://" + MessagesController.getInstance(ChangeUsernameActivity.this.currentAccount).linkPrefix + "/" + name;
                String text = LocaleController.formatString("UsernameHelpLink", R.string.UsernameHelpLink, url);
                int index = text.indexOf(url);
                SpannableStringBuilder textSpan = new SpannableStringBuilder(text);
                if (index >= 0) {
                    textSpan.setSpan(new LinkSpan(url), index, url.length() + index, 33);
                }
                ChangeUsernameActivity.this.helpTextView.setText(TextUtils.concat(ChangeUsernameActivity.this.infoText, "\n\n", textSpan));
            }
        });
        linearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        TextView textView = new TextView(context);
        this.checkTextView = textView;
        textView.setTextSize(1, 15.0f);
        this.checkTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        linearLayout.addView(this.checkTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 12, 24, 0));
        TextView textView2 = new TextView(context);
        this.helpTextView = textView2;
        textView2.setTextSize(1, 15.0f);
        this.helpTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
        this.helpTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        TextView textView3 = this.helpTextView;
        SpannableStringBuilder replaceTags = AndroidUtilities.replaceTags(LocaleController.getString("UsernameHelp", R.string.UsernameHelp));
        this.infoText = replaceTags;
        textView3.setText(replaceTags);
        this.helpTextView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
        this.helpTextView.setHighlightColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkSelection));
        this.helpTextView.setMovementMethod(new LinkMovementMethodMy());
        linearLayout.addView(this.helpTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 0));
        this.checkTextView.setVisibility(8);
        if (user != null && user.username != null && user.username.length() > 0) {
            this.ignoreCheck = true;
            this.firstNameField.setText(user.username);
            EditTextBoldCursor editTextBoldCursor2 = this.firstNameField;
            editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
            this.ignoreCheck = false;
        }
        return this.fragmentView;
    }

    public static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-ChangeUsernameActivity */
    public /* synthetic */ boolean m1701lambda$createView$1$orgtelegramuiChangeUsernameActivity(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i == 6 && (view = this.doneButton) != null) {
            view.performClick();
            return true;
        }
        return false;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        boolean animations = preferences.getBoolean("view_animations", true);
        if (!animations) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public boolean checkUserName(String name, boolean alert) {
        if (name != null && name.startsWith("@")) {
            name = name.substring(1);
        }
        if (!TextUtils.isEmpty(name)) {
            this.checkTextView.setVisibility(0);
        } else {
            this.checkTextView.setVisibility(8);
        }
        if (alert && name.length() == 0) {
            return true;
        }
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
        if (name != null) {
            if (name.startsWith("_") || name.endsWith("_")) {
                this.checkTextView.setText(LocaleController.getString("UsernameInvalid", R.string.UsernameInvalid));
                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                return false;
            }
            for (int a = 0; a < name.length(); a++) {
                char ch = name.charAt(a);
                if (a == 0 && ch >= '0' && ch <= '9') {
                    if (!alert) {
                        this.checkTextView.setText(LocaleController.getString("UsernameInvalidStartNumber", R.string.UsernameInvalidStartNumber));
                        this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                        this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                    } else {
                        AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalidStartNumber", R.string.UsernameInvalidStartNumber));
                    }
                    return false;
                } else if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                    if (alert) {
                        AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalid", R.string.UsernameInvalid));
                    } else {
                        this.checkTextView.setText(LocaleController.getString("UsernameInvalid", R.string.UsernameInvalid));
                        this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                        this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                    }
                    return false;
                }
            }
        }
        if (name == null || name.length() < 5) {
            if (!alert) {
                this.checkTextView.setText(LocaleController.getString("UsernameInvalidShort", R.string.UsernameInvalidShort));
                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
            } else {
                AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalidShort", R.string.UsernameInvalidShort));
            }
            return false;
        } else if (name.length() > 32) {
            if (!alert) {
                this.checkTextView.setText(LocaleController.getString("UsernameInvalidLong", R.string.UsernameInvalidLong));
                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
            } else {
                AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalidLong", R.string.UsernameInvalidLong));
            }
            return false;
        } else {
            if (!alert) {
                String currentName = UserConfig.getInstance(this.currentAccount).getCurrentUser().username;
                if (currentName == null) {
                    currentName = "";
                }
                if (name.equals(currentName)) {
                    this.checkTextView.setText(LocaleController.formatString("UsernameAvailable", R.string.UsernameAvailable, name));
                    this.checkTextView.setTag(Theme.key_windowBackgroundWhiteGreenText);
                    this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGreenText));
                    return true;
                }
                this.checkTextView.setText(LocaleController.getString("UsernameChecking", R.string.UsernameChecking));
                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteGrayText8);
                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
                this.lastCheckName = name;
                final String nameFinal = name;
                Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.ChangeUsernameActivity$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChangeUsernameActivity.this.m1700lambda$checkUserName$4$orgtelegramuiChangeUsernameActivity(nameFinal);
                    }
                };
                this.checkRunnable = runnable2;
                AndroidUtilities.runOnUIThread(runnable2, 300L);
            }
            return true;
        }
    }

    /* renamed from: lambda$checkUserName$4$org-telegram-ui-ChangeUsernameActivity */
    public /* synthetic */ void m1700lambda$checkUserName$4$orgtelegramuiChangeUsernameActivity(final String nameFinal) {
        TLRPC.TL_account_checkUsername req = new TLRPC.TL_account_checkUsername();
        req.username = nameFinal;
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ChangeUsernameActivity$$ExternalSyntheticLambda7
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChangeUsernameActivity.this.m1699lambda$checkUserName$3$orgtelegramuiChangeUsernameActivity(nameFinal, tLObject, tL_error);
            }
        }, 2);
    }

    /* renamed from: lambda$checkUserName$3$org-telegram-ui-ChangeUsernameActivity */
    public /* synthetic */ void m1699lambda$checkUserName$3$orgtelegramuiChangeUsernameActivity(final String nameFinal, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChangeUsernameActivity$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                ChangeUsernameActivity.this.m1698lambda$checkUserName$2$orgtelegramuiChangeUsernameActivity(nameFinal, error, response);
            }
        });
    }

    /* renamed from: lambda$checkUserName$2$org-telegram-ui-ChangeUsernameActivity */
    public /* synthetic */ void m1698lambda$checkUserName$2$orgtelegramuiChangeUsernameActivity(String nameFinal, TLRPC.TL_error error, TLObject response) {
        this.checkReqId = 0;
        String str = this.lastCheckName;
        if (str != null && str.equals(nameFinal)) {
            if (error == null && (response instanceof TLRPC.TL_boolTrue)) {
                this.checkTextView.setText(LocaleController.formatString("UsernameAvailable", R.string.UsernameAvailable, nameFinal));
                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteGreenText);
                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGreenText));
                this.lastNameAvailable = true;
                return;
            }
            this.checkTextView.setText(LocaleController.getString("UsernameInUse", R.string.UsernameInUse));
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
            this.lastNameAvailable = false;
        }
    }

    public void saveName() {
        String newName = this.firstNameField.getText().toString();
        if (newName.startsWith("@")) {
            newName = newName.substring(1);
        }
        if (!checkUserName(newName, true)) {
            return;
        }
        TLRPC.User user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        if (getParentActivity() == null || user == null) {
            return;
        }
        String currentName = user.username;
        if (currentName == null) {
            currentName = "";
        }
        if (currentName.equals(newName)) {
            finishFragment();
            return;
        }
        final AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        final TLRPC.TL_account_updateUsername req = new TLRPC.TL_account_updateUsername();
        req.username = newName;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_NAME));
        final int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ChangeUsernameActivity$$ExternalSyntheticLambda8
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChangeUsernameActivity.this.m1704lambda$saveName$7$orgtelegramuiChangeUsernameActivity(progressDialog, req, tLObject, tL_error);
            }
        }, 2);
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, this.classGuid);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.ChangeUsernameActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                ChangeUsernameActivity.this.m1705lambda$saveName$8$orgtelegramuiChangeUsernameActivity(reqId, dialogInterface);
            }
        });
        progressDialog.show();
    }

    /* renamed from: lambda$saveName$7$org-telegram-ui-ChangeUsernameActivity */
    public /* synthetic */ void m1704lambda$saveName$7$orgtelegramuiChangeUsernameActivity(final AlertDialog progressDialog, final TLRPC.TL_account_updateUsername req, TLObject response, final TLRPC.TL_error error) {
        if (error == null) {
            final TLRPC.User user1 = (TLRPC.User) response;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChangeUsernameActivity$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    ChangeUsernameActivity.this.m1702lambda$saveName$5$orgtelegramuiChangeUsernameActivity(progressDialog, user1);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChangeUsernameActivity$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                ChangeUsernameActivity.this.m1703lambda$saveName$6$orgtelegramuiChangeUsernameActivity(progressDialog, error, req);
            }
        });
    }

    /* renamed from: lambda$saveName$5$org-telegram-ui-ChangeUsernameActivity */
    public /* synthetic */ void m1702lambda$saveName$5$orgtelegramuiChangeUsernameActivity(AlertDialog progressDialog, TLRPC.User user1) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        ArrayList<TLRPC.User> users = new ArrayList<>();
        users.add(user1);
        MessagesController.getInstance(this.currentAccount).putUsers(users, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, null, false, true);
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
        finishFragment();
    }

    /* renamed from: lambda$saveName$6$org-telegram-ui-ChangeUsernameActivity */
    public /* synthetic */ void m1703lambda$saveName$6$orgtelegramuiChangeUsernameActivity(AlertDialog progressDialog, TLRPC.TL_error error, TLRPC.TL_account_updateUsername req) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        AlertsCreator.processError(this.currentAccount, error, this, req, new Object[0]);
    }

    /* renamed from: lambda$saveName$8$org-telegram-ui-ChangeUsernameActivity */
    public /* synthetic */ void m1705lambda$saveName$8$orgtelegramuiChangeUsernameActivity(int reqId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(reqId, true);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        themeDescriptions.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
        themeDescriptions.add(new ThemeDescription(this.helpTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText8));
        themeDescriptions.add(new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteRedText4));
        themeDescriptions.add(new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteGreenText));
        themeDescriptions.add(new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText8));
        return themeDescriptions;
    }
}
