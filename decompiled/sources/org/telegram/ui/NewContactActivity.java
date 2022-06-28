package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
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
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.HintEditText;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.CountrySelectActivity;
import org.telegram.ui.NewContactActivity;
/* loaded from: classes4.dex */
public class NewContactActivity extends BaseFragment implements AdapterView.OnItemSelectedListener {
    private static final int done_button = 1;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private EditTextBoldCursor codeField;
    private LinearLayout contentLayout;
    private TextView countryButton;
    private int countryState;
    private boolean donePressed;
    private ActionBarMenuItem editDoneItem;
    private AnimatorSet editDoneItemAnimation;
    private ContextProgressView editDoneItemProgress;
    private EditTextBoldCursor firstNameField;
    private boolean ignoreOnPhoneChange;
    private boolean ignoreOnTextChange;
    private boolean ignoreSelection;
    private String initialFirstName;
    private String initialLastName;
    private String initialPhoneNumber;
    private boolean initialPhoneNumberWithCountryCode;
    private EditTextBoldCursor lastNameField;
    private View lineView;
    private HintEditText phoneField;
    private TextView textView;
    private ArrayList<String> countriesArray = new ArrayList<>();
    private HashMap<String, String> countriesMap = new HashMap<>();
    private HashMap<String, String> codesMap = new HashMap<>();
    private HashMap<String, String> phoneFormatMap = new HashMap<>();

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        boolean needInvalidateAvatar;
        String countryName;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("AddContactTitle", R.string.AddContactTitle));
        this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass1());
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        this.avatarDrawable = avatarDrawable;
        avatarDrawable.setInfo(5L, "", "");
        ActionBarMenu menu = this.actionBar.createMenu();
        ActionBarMenuItem addItemWithWidth = menu.addItemWithWidth(1, R.drawable.ic_ab_done, AndroidUtilities.dp(56.0f));
        this.editDoneItem = addItemWithWidth;
        addItemWithWidth.setContentDescription(LocaleController.getString("Done", R.string.Done));
        ContextProgressView contextProgressView = new ContextProgressView(context, 1);
        this.editDoneItemProgress = contextProgressView;
        this.editDoneItem.addView(contextProgressView, LayoutHelper.createFrame(-1, -1.0f));
        this.editDoneItemProgress.setVisibility(4);
        this.fragmentView = new ScrollView(context);
        LinearLayout linearLayout = new LinearLayout(context);
        this.contentLayout = linearLayout;
        linearLayout.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
        this.contentLayout.setOrientation(1);
        ((ScrollView) this.fragmentView).addView(this.contentLayout, LayoutHelper.createScroll(-1, -2, 51));
        this.contentLayout.setOnTouchListener(NewContactActivity$$ExternalSyntheticLambda2.INSTANCE);
        FrameLayout frameLayout = new FrameLayout(context);
        this.contentLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 0.0f, 24.0f, 0.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImage = backupImageView;
        backupImageView.setImageDrawable(this.avatarDrawable);
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(60, 60.0f, 51, 0.0f, 9.0f, 0.0f, 0.0f));
        boolean needInvalidateAvatar2 = false;
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
        this.firstNameField = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 18.0f);
        this.firstNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.firstNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setMaxLines(1);
        this.firstNameField.setLines(1);
        this.firstNameField.setSingleLine(true);
        this.firstNameField.setBackground(null);
        this.firstNameField.setLineColors(getThemedColor(Theme.key_windowBackgroundWhiteInputField), getThemedColor(Theme.key_windowBackgroundWhiteInputFieldActivated), getThemedColor(Theme.key_windowBackgroundWhiteRedText3));
        this.firstNameField.setGravity(3);
        this.firstNameField.setInputType(49152);
        this.firstNameField.setImeOptions(5);
        this.firstNameField.setHint(LocaleController.getString("FirstName", R.string.FirstName));
        this.firstNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.firstNameField.setCursorWidth(1.5f);
        String str = this.initialFirstName;
        if (str != null) {
            this.firstNameField.setText(str);
            this.initialFirstName = null;
            needInvalidateAvatar2 = true;
        }
        frameLayout.addView(this.firstNameField, LayoutHelper.createFrame(-1, 34.0f, 51, 84.0f, 0.0f, 0.0f, 0.0f));
        this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.NewContactActivity$$ExternalSyntheticLambda3
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return NewContactActivity.this.m3924lambda$createView$1$orgtelegramuiNewContactActivity(textView, i, keyEvent);
            }
        });
        this.firstNameField.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.NewContactActivity.2
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                NewContactActivity.this.invalidateAvatar();
            }
        });
        EditTextBoldCursor editTextBoldCursor2 = new EditTextBoldCursor(context);
        this.lastNameField = editTextBoldCursor2;
        editTextBoldCursor2.setTextSize(1, 18.0f);
        this.lastNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.lastNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.lastNameField.setBackground(null);
        this.lastNameField.setLineColors(getThemedColor(Theme.key_windowBackgroundWhiteInputField), getThemedColor(Theme.key_windowBackgroundWhiteInputFieldActivated), getThemedColor(Theme.key_windowBackgroundWhiteRedText3));
        this.lastNameField.setMaxLines(1);
        this.lastNameField.setLines(1);
        this.lastNameField.setSingleLine(true);
        this.lastNameField.setGravity(3);
        this.lastNameField.setInputType(49152);
        this.lastNameField.setImeOptions(5);
        this.lastNameField.setHint(LocaleController.getString("LastName", R.string.LastName));
        this.lastNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.lastNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.lastNameField.setCursorWidth(1.5f);
        String str2 = this.initialLastName;
        if (str2 == null) {
            needInvalidateAvatar = needInvalidateAvatar2;
        } else {
            this.lastNameField.setText(str2);
            this.initialLastName = null;
            needInvalidateAvatar = true;
        }
        frameLayout.addView(this.lastNameField, LayoutHelper.createFrame(-1, 34.0f, 51, 84.0f, 44.0f, 0.0f, 0.0f));
        this.lastNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.NewContactActivity$$ExternalSyntheticLambda4
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return NewContactActivity.this.m3925lambda$createView$2$orgtelegramuiNewContactActivity(textView, i, keyEvent);
            }
        });
        this.lastNameField.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.NewContactActivity.3
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                NewContactActivity.this.invalidateAvatar();
            }
        });
        if (needInvalidateAvatar) {
            invalidateAvatar();
        }
        TextView textView = new TextView(context);
        this.countryButton = textView;
        textView.setTextSize(1, 18.0f);
        this.countryButton.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        this.countryButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.countryButton.setMaxLines(1);
        this.countryButton.setSingleLine(true);
        this.countryButton.setEllipsize(TextUtils.TruncateAt.END);
        this.countryButton.setGravity(3);
        this.countryButton.setBackground(Theme.getSelectorDrawable(true));
        this.contentLayout.addView(this.countryButton, LayoutHelper.createLinear(-1, 36, 0.0f, 24.0f, 0.0f, 14.0f));
        this.countryButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.NewContactActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                NewContactActivity.this.m3927lambda$createView$4$orgtelegramuiNewContactActivity(view);
            }
        });
        View view = new View(context);
        this.lineView = view;
        view.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        this.lineView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayLine));
        this.contentLayout.addView(this.lineView, LayoutHelper.createLinear(-1, 1, 0.0f, -17.5f, 0.0f, 0.0f));
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(0);
        this.contentLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 0.0f, 20.0f, 0.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setText("+");
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 18.0f);
        this.textView.setImportantForAccessibility(2);
        linearLayout2.addView(this.textView, LayoutHelper.createLinear(-2, -2));
        EditTextBoldCursor editTextBoldCursor3 = new EditTextBoldCursor(context);
        this.codeField = editTextBoldCursor3;
        editTextBoldCursor3.setInputType(3);
        this.codeField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.codeField.setBackgroundDrawable(null);
        this.codeField.setLineColors(getThemedColor(Theme.key_windowBackgroundWhiteInputField), getThemedColor(Theme.key_windowBackgroundWhiteInputFieldActivated), getThemedColor(Theme.key_windowBackgroundWhiteRedText3));
        this.codeField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.codeField.setCursorWidth(1.5f);
        this.codeField.setPadding(AndroidUtilities.dp(10.0f), 0, 0, 0);
        this.codeField.setTextSize(1, 18.0f);
        this.codeField.setMaxLines(1);
        this.codeField.setGravity(19);
        this.codeField.setImeOptions(268435461);
        linearLayout2.addView(this.codeField, LayoutHelper.createLinear(55, 36, -9.0f, 0.0f, 16.0f, 0.0f));
        this.codeField.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.NewContactActivity.4
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                if (!NewContactActivity.this.ignoreOnTextChange) {
                    NewContactActivity.this.ignoreOnTextChange = true;
                    String text = PhoneFormat.stripExceptNumbers(NewContactActivity.this.codeField.getText().toString());
                    NewContactActivity.this.codeField.setText(text);
                    String str3 = null;
                    if (text.length() == 0) {
                        NewContactActivity.this.countryButton.setText(LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
                        NewContactActivity.this.phoneField.setHintText((String) null);
                        NewContactActivity.this.countryState = 1;
                    } else {
                        boolean ok = false;
                        String textToSet = null;
                        if (text.length() > 4) {
                            NewContactActivity.this.ignoreOnTextChange = true;
                            int a = 4;
                            while (true) {
                                if (a < 1) {
                                    break;
                                }
                                String sub = text.substring(0, a);
                                if (((String) NewContactActivity.this.codesMap.get(sub)) == null) {
                                    a--;
                                } else {
                                    ok = true;
                                    textToSet = text.substring(a) + NewContactActivity.this.phoneField.getText().toString();
                                    text = sub;
                                    NewContactActivity.this.codeField.setText(sub);
                                    break;
                                }
                            }
                            if (!ok) {
                                NewContactActivity.this.ignoreOnTextChange = true;
                                textToSet = text.substring(1) + NewContactActivity.this.phoneField.getText().toString();
                                EditTextBoldCursor editTextBoldCursor4 = NewContactActivity.this.codeField;
                                String substring = text.substring(0, 1);
                                text = substring;
                                editTextBoldCursor4.setText(substring);
                            }
                        }
                        String country = (String) NewContactActivity.this.codesMap.get(text);
                        if (country != null) {
                            int index = NewContactActivity.this.countriesArray.indexOf(country);
                            if (index != -1) {
                                NewContactActivity.this.ignoreSelection = true;
                                NewContactActivity.this.countryButton.setText((CharSequence) NewContactActivity.this.countriesArray.get(index));
                                String hint = (String) NewContactActivity.this.phoneFormatMap.get(text);
                                HintEditText hintEditText = NewContactActivity.this.phoneField;
                                if (hint != null) {
                                    str3 = hint.replace('X', (char) 8211);
                                }
                                hintEditText.setHintText(str3);
                                NewContactActivity.this.countryState = 0;
                            } else {
                                NewContactActivity.this.countryButton.setText(LocaleController.getString("WrongCountry", R.string.WrongCountry));
                                NewContactActivity.this.phoneField.setHintText((String) null);
                                NewContactActivity.this.countryState = 2;
                            }
                        } else {
                            NewContactActivity.this.countryButton.setText(LocaleController.getString("WrongCountry", R.string.WrongCountry));
                            NewContactActivity.this.phoneField.setHintText((String) null);
                            NewContactActivity.this.countryState = 2;
                        }
                        if (!ok) {
                            NewContactActivity.this.codeField.setSelection(NewContactActivity.this.codeField.getText().length());
                        }
                        if (textToSet != null) {
                            if (NewContactActivity.this.initialPhoneNumber == null) {
                                NewContactActivity.this.phoneField.requestFocus();
                            }
                            NewContactActivity.this.phoneField.setText(textToSet);
                            NewContactActivity.this.phoneField.setSelection(NewContactActivity.this.phoneField.length());
                        }
                    }
                    NewContactActivity.this.ignoreOnTextChange = false;
                }
            }
        });
        this.codeField.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.NewContactActivity$$ExternalSyntheticLambda5
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView3, int i, KeyEvent keyEvent) {
                return NewContactActivity.this.m3928lambda$createView$5$orgtelegramuiNewContactActivity(textView3, i, keyEvent);
            }
        });
        HintEditText hintEditText = new HintEditText(context);
        this.phoneField = hintEditText;
        hintEditText.setInputType(3);
        this.phoneField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.phoneField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.phoneField.setBackgroundDrawable(null);
        this.phoneField.setLineColors(getThemedColor(Theme.key_windowBackgroundWhiteInputField), getThemedColor(Theme.key_windowBackgroundWhiteInputFieldActivated), getThemedColor(Theme.key_windowBackgroundWhiteRedText3));
        this.phoneField.setPadding(0, 0, 0, 0);
        this.phoneField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.phoneField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.phoneField.setCursorWidth(1.5f);
        this.phoneField.setTextSize(1, 18.0f);
        this.phoneField.setMaxLines(1);
        this.phoneField.setGravity(19);
        this.phoneField.setImeOptions(268435462);
        linearLayout2.addView(this.phoneField, LayoutHelper.createFrame(-1, 36.0f));
        this.phoneField.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.NewContactActivity.5
            private int actionPosition;
            private int characterAction = -1;

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (count == 0 && after == 1) {
                    this.characterAction = 1;
                } else if (count == 1 && after == 0) {
                    if (s.charAt(start) == ' ' && start > 0) {
                        this.characterAction = 3;
                        this.actionPosition = start - 1;
                        return;
                    }
                    this.characterAction = 2;
                } else {
                    this.characterAction = -1;
                }
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
                int i;
                int i2;
                if (!NewContactActivity.this.ignoreOnPhoneChange) {
                    int start = NewContactActivity.this.phoneField.getSelectionStart();
                    String str3 = NewContactActivity.this.phoneField.getText().toString();
                    if (this.characterAction == 3) {
                        str3 = str3.substring(0, this.actionPosition) + str3.substring(this.actionPosition + 1);
                        start--;
                    }
                    StringBuilder builder = new StringBuilder(str3.length());
                    for (int a = 0; a < str3.length(); a++) {
                        String ch = str3.substring(a, a + 1);
                        if ("0123456789".contains(ch)) {
                            builder.append(ch);
                        }
                    }
                    NewContactActivity.this.ignoreOnPhoneChange = true;
                    String hint = NewContactActivity.this.phoneField.getHintText();
                    if (hint != null) {
                        int a2 = 0;
                        while (true) {
                            if (a2 >= builder.length()) {
                                break;
                            } else if (a2 < hint.length()) {
                                if (hint.charAt(a2) == ' ') {
                                    builder.insert(a2, ' ');
                                    a2++;
                                    if (start == a2 && (i2 = this.characterAction) != 2 && i2 != 3) {
                                        start++;
                                    }
                                }
                                a2++;
                            } else {
                                builder.insert(a2, ' ');
                                if (start == a2 + 1 && (i = this.characterAction) != 2 && i != 3) {
                                    start++;
                                }
                            }
                        }
                    }
                    NewContactActivity.this.phoneField.setText(builder);
                    if (start >= 0) {
                        NewContactActivity.this.phoneField.setSelection(Math.min(start, NewContactActivity.this.phoneField.length()));
                    }
                    NewContactActivity.this.phoneField.onTextChange();
                    NewContactActivity.this.ignoreOnPhoneChange = false;
                }
            }
        });
        this.phoneField.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.NewContactActivity$$ExternalSyntheticLambda6
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView3, int i, KeyEvent keyEvent) {
                return NewContactActivity.this.m3929lambda$createView$6$orgtelegramuiNewContactActivity(textView3, i, keyEvent);
            }
        });
        this.phoneField.setOnKeyListener(new View.OnKeyListener() { // from class: org.telegram.ui.NewContactActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnKeyListener
            public final boolean onKey(View view2, int i, KeyEvent keyEvent) {
                return NewContactActivity.this.m3930lambda$createView$7$orgtelegramuiNewContactActivity(view2, i, keyEvent);
            }
        });
        HashMap<String, String> languageMap = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("countries.txt")));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                String[] args = line.split(";");
                this.countriesArray.add(0, args[2]);
                this.countriesMap.put(args[2], args[0]);
                this.codesMap.put(args[0], args[2]);
                if (args.length > 3) {
                    this.phoneFormatMap.put(args[0], args[3]);
                }
                languageMap.put(args[1], args[2]);
            }
            reader.close();
        } catch (Exception e) {
            FileLog.e(e);
        }
        Collections.sort(this.countriesArray, CountrySelectActivity$CountryAdapter$$ExternalSyntheticLambda0.INSTANCE);
        if (!TextUtils.isEmpty(this.initialPhoneNumber)) {
            TLRPC.User user = getUserConfig().getCurrentUser();
            if (this.initialPhoneNumber.startsWith("+")) {
                this.codeField.setText(this.initialPhoneNumber.substring(1));
            } else if (this.initialPhoneNumberWithCountryCode || user == null || TextUtils.isEmpty(user.phone)) {
                this.codeField.setText(this.initialPhoneNumber);
            } else {
                String phone = user.phone;
                int a = 4;
                while (true) {
                    if (a < 1) {
                        break;
                    }
                    String sub = phone.substring(0, a);
                    if (this.codesMap.get(sub) == null) {
                        a--;
                    } else {
                        this.codeField.setText(sub);
                        break;
                    }
                }
                this.phoneField.setText(this.initialPhoneNumber);
            }
            this.initialPhoneNumber = null;
        } else {
            String country = null;
            try {
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (telephonyManager != null) {
                    country = telephonyManager.getSimCountryIso().toUpperCase();
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            if (country != null && (countryName = languageMap.get(country)) != null) {
                int index = this.countriesArray.indexOf(countryName);
                if (index != -1) {
                    this.codeField.setText(this.countriesMap.get(countryName));
                    this.countryState = 0;
                }
            }
            if (this.codeField.length() == 0) {
                this.countryButton.setText(LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
                this.phoneField.setHintText((String) null);
                this.countryState = 1;
            }
        }
        return this.fragmentView;
    }

    /* renamed from: org.telegram.ui.NewContactActivity$1 */
    /* loaded from: classes4.dex */
    public class AnonymousClass1 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass1() {
            NewContactActivity.this = this$0;
        }

        @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
        public void onItemClick(int id) {
            if (id == -1) {
                NewContactActivity.this.finishFragment();
            } else if (id == 1 && !NewContactActivity.this.donePressed) {
                if (NewContactActivity.this.firstNameField.length() != 0) {
                    if (NewContactActivity.this.codeField.length() != 0) {
                        if (NewContactActivity.this.phoneField.length() != 0) {
                            NewContactActivity.this.donePressed = true;
                            NewContactActivity.this.showEditDoneProgress(true, true);
                            final TLRPC.TL_contacts_importContacts req = new TLRPC.TL_contacts_importContacts();
                            final TLRPC.TL_inputPhoneContact inputPhoneContact = new TLRPC.TL_inputPhoneContact();
                            inputPhoneContact.first_name = NewContactActivity.this.firstNameField.getText().toString();
                            inputPhoneContact.last_name = NewContactActivity.this.lastNameField.getText().toString();
                            inputPhoneContact.phone = "+" + NewContactActivity.this.codeField.getText().toString() + NewContactActivity.this.phoneField.getText().toString();
                            req.contacts.add(inputPhoneContact);
                            int reqId = ConnectionsManager.getInstance(NewContactActivity.this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.NewContactActivity$1$$ExternalSyntheticLambda2
                                @Override // org.telegram.tgnet.RequestDelegate
                                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                    NewContactActivity.AnonymousClass1.this.m3934lambda$onItemClick$2$orgtelegramuiNewContactActivity$1(inputPhoneContact, req, tLObject, tL_error);
                                }
                            }, 2);
                            ConnectionsManager.getInstance(NewContactActivity.this.currentAccount).bindRequestToGuid(reqId, NewContactActivity.this.classGuid);
                            return;
                        }
                        Vibrator v = (Vibrator) NewContactActivity.this.getParentActivity().getSystemService("vibrator");
                        if (v != null) {
                            v.vibrate(200L);
                        }
                        AndroidUtilities.shakeView(NewContactActivity.this.phoneField, 2.0f, 0);
                        return;
                    }
                    Vibrator v2 = (Vibrator) NewContactActivity.this.getParentActivity().getSystemService("vibrator");
                    if (v2 != null) {
                        v2.vibrate(200L);
                    }
                    AndroidUtilities.shakeView(NewContactActivity.this.codeField, 2.0f, 0);
                    return;
                }
                Vibrator v3 = (Vibrator) NewContactActivity.this.getParentActivity().getSystemService("vibrator");
                if (v3 != null) {
                    v3.vibrate(200L);
                }
                AndroidUtilities.shakeView(NewContactActivity.this.firstNameField, 2.0f, 0);
            }
        }

        /* renamed from: lambda$onItemClick$2$org-telegram-ui-NewContactActivity$1 */
        public /* synthetic */ void m3934lambda$onItemClick$2$orgtelegramuiNewContactActivity$1(final TLRPC.TL_inputPhoneContact inputPhoneContact, final TLRPC.TL_contacts_importContacts req, TLObject response, final TLRPC.TL_error error) {
            final TLRPC.TL_contacts_importedContacts res = (TLRPC.TL_contacts_importedContacts) response;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.NewContactActivity$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    NewContactActivity.AnonymousClass1.this.m3933lambda$onItemClick$1$orgtelegramuiNewContactActivity$1(res, inputPhoneContact, error, req);
                }
            });
        }

        /* renamed from: lambda$onItemClick$1$org-telegram-ui-NewContactActivity$1 */
        public /* synthetic */ void m3933lambda$onItemClick$1$orgtelegramuiNewContactActivity$1(TLRPC.TL_contacts_importedContacts res, final TLRPC.TL_inputPhoneContact inputPhoneContact, TLRPC.TL_error error, TLRPC.TL_contacts_importContacts req) {
            NewContactActivity.this.donePressed = false;
            if (res == null) {
                NewContactActivity.this.showEditDoneProgress(false, true);
                AlertsCreator.processError(NewContactActivity.this.currentAccount, error, NewContactActivity.this, req, new Object[0]);
            } else if (!res.users.isEmpty()) {
                MessagesController.getInstance(NewContactActivity.this.currentAccount).putUsers(res.users, false);
                MessagesController.openChatOrProfileWith(res.users.get(0), null, NewContactActivity.this, 1, true);
            } else if (NewContactActivity.this.getParentActivity() != null) {
                NewContactActivity.this.showEditDoneProgress(false, true);
                AlertDialog.Builder builder = new AlertDialog.Builder(NewContactActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("ContactNotRegisteredTitle", R.string.ContactNotRegisteredTitle));
                builder.setMessage(LocaleController.formatString("ContactNotRegistered", R.string.ContactNotRegistered, ContactsController.formatName(inputPhoneContact.first_name, inputPhoneContact.last_name)));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                builder.setPositiveButton(LocaleController.getString("Invite", R.string.Invite), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.NewContactActivity$1$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        NewContactActivity.AnonymousClass1.this.m3932lambda$onItemClick$0$orgtelegramuiNewContactActivity$1(inputPhoneContact, dialogInterface, i);
                    }
                });
                NewContactActivity.this.showDialog(builder.create());
            }
        }

        /* renamed from: lambda$onItemClick$0$org-telegram-ui-NewContactActivity$1 */
        public /* synthetic */ void m3932lambda$onItemClick$0$orgtelegramuiNewContactActivity$1(TLRPC.TL_inputPhoneContact inputPhoneContact, DialogInterface dialog, int which) {
            try {
                Intent intent = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", inputPhoneContact.phone, null));
                intent.putExtra("sms_body", ContactsController.getInstance(NewContactActivity.this.currentAccount).getInviteText(1));
                NewContactActivity.this.getParentActivity().startActivityForResult(intent, 500);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-NewContactActivity */
    public /* synthetic */ boolean m3924lambda$createView$1$orgtelegramuiNewContactActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            this.lastNameField.requestFocus();
            EditTextBoldCursor editTextBoldCursor = this.lastNameField;
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
            return true;
        }
        return false;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-NewContactActivity */
    public /* synthetic */ boolean m3925lambda$createView$2$orgtelegramuiNewContactActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            this.phoneField.requestFocus();
            HintEditText hintEditText = this.phoneField;
            hintEditText.setSelection(hintEditText.length());
            return true;
        }
        return false;
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-NewContactActivity */
    public /* synthetic */ void m3927lambda$createView$4$orgtelegramuiNewContactActivity(View view) {
        CountrySelectActivity fragment = new CountrySelectActivity(true);
        fragment.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() { // from class: org.telegram.ui.NewContactActivity$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.CountrySelectActivity.CountrySelectActivityDelegate
            public final void didSelectCountry(CountrySelectActivity.Country country) {
                NewContactActivity.this.m3926lambda$createView$3$orgtelegramuiNewContactActivity(country);
            }
        });
        presentFragment(fragment);
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-NewContactActivity */
    public /* synthetic */ void m3926lambda$createView$3$orgtelegramuiNewContactActivity(CountrySelectActivity.Country country) {
        selectCountry(country.name);
        this.phoneField.requestFocus();
        HintEditText hintEditText = this.phoneField;
        hintEditText.setSelection(hintEditText.length());
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-NewContactActivity */
    public /* synthetic */ boolean m3928lambda$createView$5$orgtelegramuiNewContactActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            this.phoneField.requestFocus();
            HintEditText hintEditText = this.phoneField;
            hintEditText.setSelection(hintEditText.length());
            return true;
        }
        return false;
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-NewContactActivity */
    public /* synthetic */ boolean m3929lambda$createView$6$orgtelegramuiNewContactActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 6) {
            this.editDoneItem.performClick();
            return true;
        }
        return false;
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-NewContactActivity */
    public /* synthetic */ boolean m3930lambda$createView$7$orgtelegramuiNewContactActivity(View v, int keyCode, KeyEvent event) {
        if (keyCode == 67 && this.phoneField.length() == 0) {
            this.codeField.requestFocus();
            EditTextBoldCursor editTextBoldCursor = this.codeField;
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
            this.codeField.dispatchKeyEvent(event);
            return true;
        }
        return false;
    }

    public static String getPhoneNumber(Context context, TLRPC.User user, String number, boolean withCoutryCode) {
        HashMap<String, String> codesMap = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("countries.txt")));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                String[] args = line.split(";");
                codesMap.put(args[0], args[2]);
            }
            reader.close();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (number.startsWith("+")) {
            return number;
        }
        if (withCoutryCode || user == null || TextUtils.isEmpty(user.phone)) {
            return "+" + number;
        }
        String phone = user.phone;
        for (int a = 4; a >= 1; a--) {
            String sub = phone.substring(0, a);
            String country = codesMap.get(sub);
            if (country != null) {
                return "+" + sub + number;
            }
        }
        return number;
    }

    public void invalidateAvatar() {
        this.avatarDrawable.setInfo(5L, this.firstNameField.getText().toString(), this.lastNameField.getText().toString());
        this.avatarImage.invalidate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            View focusedView = this.contentLayout.findFocus();
            if (focusedView == null) {
                this.firstNameField.requestFocus();
                focusedView = this.firstNameField;
            }
            AndroidUtilities.showKeyboard(focusedView);
        }
    }

    public void setInitialPhoneNumber(String value, boolean withCoutryCode) {
        this.initialPhoneNumber = value;
        this.initialPhoneNumberWithCountryCode = withCoutryCode;
    }

    public void setInitialName(String firstName, String lastName) {
        this.initialFirstName = firstName;
        this.initialLastName = lastName;
    }

    public void selectCountry(String name) {
        int index = this.countriesArray.indexOf(name);
        if (index != -1) {
            this.ignoreOnTextChange = true;
            String code = this.countriesMap.get(name);
            this.codeField.setText(code);
            this.countryButton.setText(name);
            String hint = this.phoneFormatMap.get(code);
            this.phoneField.setHintText(hint != null ? hint.replace('X', (char) 8211) : null);
            this.countryState = 0;
            this.ignoreOnTextChange = false;
        }
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (this.ignoreSelection) {
            this.ignoreSelection = false;
            return;
        }
        this.ignoreOnTextChange = true;
        String str = this.countriesArray.get(i);
        this.codeField.setText(this.countriesMap.get(str));
        this.ignoreOnTextChange = false;
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void showEditDoneProgress(final boolean show, boolean animated) {
        AnimatorSet animatorSet = this.editDoneItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        if (animated) {
            this.editDoneItemAnimation = new AnimatorSet();
            if (show) {
                this.editDoneItemProgress.setVisibility(0);
                this.editDoneItem.setEnabled(false);
                this.editDoneItemAnimation.playTogether(ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "scaleX", 0.1f), ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "scaleY", 0.1f), ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "alpha", 0.0f), ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleX", 1.0f), ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleY", 1.0f), ObjectAnimator.ofFloat(this.editDoneItemProgress, "alpha", 1.0f));
            } else {
                this.editDoneItem.getContentView().setVisibility(0);
                this.editDoneItem.setEnabled(true);
                this.editDoneItemAnimation.playTogether(ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleX", 0.1f), ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleY", 0.1f), ObjectAnimator.ofFloat(this.editDoneItemProgress, "alpha", 0.0f), ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "scaleX", 1.0f), ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "scaleY", 1.0f), ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "alpha", 1.0f));
            }
            this.editDoneItemAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.NewContactActivity.6
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (NewContactActivity.this.editDoneItemAnimation != null && NewContactActivity.this.editDoneItemAnimation.equals(animation)) {
                        if (!show) {
                            NewContactActivity.this.editDoneItemProgress.setVisibility(4);
                        } else {
                            NewContactActivity.this.editDoneItem.getContentView().setVisibility(4);
                        }
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    if (NewContactActivity.this.editDoneItemAnimation != null && NewContactActivity.this.editDoneItemAnimation.equals(animation)) {
                        NewContactActivity.this.editDoneItemAnimation = null;
                    }
                }
            });
            this.editDoneItemAnimation.setDuration(150L);
            this.editDoneItemAnimation.start();
        } else if (show) {
            this.editDoneItem.getContentView().setScaleX(0.1f);
            this.editDoneItem.getContentView().setScaleY(0.1f);
            this.editDoneItem.getContentView().setAlpha(0.0f);
            this.editDoneItemProgress.setScaleX(1.0f);
            this.editDoneItemProgress.setScaleY(1.0f);
            this.editDoneItemProgress.setAlpha(1.0f);
            this.editDoneItem.getContentView().setVisibility(4);
            this.editDoneItemProgress.setVisibility(0);
            this.editDoneItem.setEnabled(false);
        } else {
            this.editDoneItemProgress.setScaleX(0.1f);
            this.editDoneItemProgress.setScaleY(0.1f);
            this.editDoneItemProgress.setAlpha(0.0f);
            this.editDoneItem.getContentView().setScaleX(1.0f);
            this.editDoneItem.getContentView().setScaleY(1.0f);
            this.editDoneItem.getContentView().setAlpha(1.0f);
            this.editDoneItem.getContentView().setVisibility(0);
            this.editDoneItemProgress.setVisibility(4);
            this.editDoneItem.setEnabled(true);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.NewContactActivity$$ExternalSyntheticLambda7
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                NewContactActivity.this.m3931lambda$getThemeDescriptions$8$orgtelegramuiNewContactActivity();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        themeDescriptions.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
        themeDescriptions.add(new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        themeDescriptions.add(new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
        themeDescriptions.add(new ThemeDescription(this.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        themeDescriptions.add(new ThemeDescription(this.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
        themeDescriptions.add(new ThemeDescription(this.phoneField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.phoneField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(this.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        themeDescriptions.add(new ThemeDescription(this.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
        themeDescriptions.add(new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.lineView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhiteGrayLine));
        themeDescriptions.add(new ThemeDescription(this.countryButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.countryButton, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.countryButton, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.editDoneItemProgress, 0, null, null, null, null, Theme.key_contextProgressInner2));
        themeDescriptions.add(new ThemeDescription(this.editDoneItemProgress, 0, null, null, null, null, Theme.key_contextProgressOuter2));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, Theme.avatarDrawables, cellDelegate, Theme.key_avatar_text));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$8$org-telegram-ui-NewContactActivity */
    public /* synthetic */ void m3931lambda$getThemeDescriptions$8$orgtelegramuiNewContactActivity() {
        if (this.avatarImage != null) {
            invalidateAvatar();
        }
    }
}
