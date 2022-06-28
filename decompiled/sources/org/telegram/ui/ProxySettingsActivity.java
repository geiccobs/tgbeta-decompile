package org.telegram.ui;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
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
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.microsoft.appcenter.crashes.ingestion.models.ErrorAttachmentLog;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes4.dex */
public class ProxySettingsActivity extends BaseFragment {
    private static final int FIELD_IP = 0;
    private static final int FIELD_PASSWORD = 3;
    private static final int FIELD_PORT = 1;
    private static final int FIELD_SECRET = 4;
    private static final int FIELD_USER = 2;
    private static final int TYPE_MTPROTO = 1;
    private static final int TYPE_SOCKS5 = 0;
    private static final int done_button = 1;
    private boolean addingNewProxy;
    private TextInfoPrivacyCell[] bottomCells;
    private ClipboardManager.OnPrimaryClipChangedListener clipChangedListener;
    private ClipboardManager clipboardManager;
    private SharedConfig.ProxyInfo currentProxyInfo;
    private int currentType;
    private ActionBarMenuItem doneItem;
    private HeaderCell headerCell;
    private boolean ignoreOnTextChange;
    private EditTextBoldCursor[] inputFields;
    private LinearLayout inputFieldsContainer;
    private LinearLayout linearLayout2;
    private TextSettingsCell pasteCell;
    private String[] pasteFields;
    private String pasteString;
    private int pasteType;
    private ScrollView scrollView;
    private ShadowSectionCell[] sectionCell;
    private TextSettingsCell shareCell;
    private ValueAnimator shareDoneAnimator;
    private boolean shareDoneEnabled;
    private float shareDoneProgress;
    private float[] shareDoneProgressAnimValues;
    private RadioCell[] typeCell;

    /* loaded from: classes4.dex */
    public static class TypeCell extends FrameLayout {
        private ImageView checkImage;
        private boolean needDivider;
        private TextView textView;

        public TypeCell(Context context) {
            super(context);
            setWillNotDraw(false);
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            int i = 5;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 71.0f : 21.0f, 0.0f, LocaleController.isRTL ? 21.0f : 23.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.checkImage = imageView;
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), PorterDuff.Mode.MULTIPLY));
            this.checkImage.setImageResource(R.drawable.sticker_added);
            addView(this.checkImage, LayoutHelper.createFrame(19, 14.0f, (LocaleController.isRTL ? 3 : i) | 16, 21.0f, 0.0f, 21.0f, 0.0f));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0), C.BUFFER_FLAG_ENCRYPTED));
        }

        public void setValue(String name, boolean checked, boolean divider) {
            this.textView.setText(name);
            this.checkImage.setVisibility(checked ? 0 : 4);
            this.needDivider = divider;
        }

        public void setTypeChecked(boolean value) {
            this.checkImage.setVisibility(value ? 0 : 4);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(20.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        }
    }

    public ProxySettingsActivity() {
        this.sectionCell = new ShadowSectionCell[3];
        this.bottomCells = new TextInfoPrivacyCell[2];
        this.typeCell = new RadioCell[2];
        this.currentType = -1;
        this.pasteType = -1;
        this.shareDoneProgress = 1.0f;
        this.shareDoneProgressAnimValues = new float[2];
        this.shareDoneEnabled = true;
        this.clipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() { // from class: org.telegram.ui.ProxySettingsActivity$$ExternalSyntheticLambda1
            @Override // android.content.ClipboardManager.OnPrimaryClipChangedListener
            public final void onPrimaryClipChanged() {
                ProxySettingsActivity.this.updatePasteCell();
            }
        };
        this.currentProxyInfo = new SharedConfig.ProxyInfo("", 1080, "", "", "");
        this.addingNewProxy = true;
    }

    public ProxySettingsActivity(SharedConfig.ProxyInfo proxyInfo) {
        this.sectionCell = new ShadowSectionCell[3];
        this.bottomCells = new TextInfoPrivacyCell[2];
        this.typeCell = new RadioCell[2];
        this.currentType = -1;
        this.pasteType = -1;
        this.shareDoneProgress = 1.0f;
        this.shareDoneProgressAnimValues = new float[2];
        this.shareDoneEnabled = true;
        this.clipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() { // from class: org.telegram.ui.ProxySettingsActivity$$ExternalSyntheticLambda1
            @Override // android.content.ClipboardManager.OnPrimaryClipChangedListener
            public final void onPrimaryClipChanged() {
                ProxySettingsActivity.this.updatePasteCell();
            }
        };
        this.currentProxyInfo = proxyInfo;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        this.clipboardManager.addPrimaryClipChangedListener(this.clipChangedListener);
        updatePasteCell();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        this.clipboardManager.removePrimaryClipChangedListener(this.clipChangedListener);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setTitle(LocaleController.getString("ProxyDetails", R.string.ProxyDetails));
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ProxySettingsActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                boolean enabled;
                if (id == -1) {
                    ProxySettingsActivity.this.finishFragment();
                } else if (id == 1 && ProxySettingsActivity.this.getParentActivity() != null) {
                    ProxySettingsActivity.this.currentProxyInfo.address = ProxySettingsActivity.this.inputFields[0].getText().toString();
                    ProxySettingsActivity.this.currentProxyInfo.port = Utilities.parseInt((CharSequence) ProxySettingsActivity.this.inputFields[1].getText().toString()).intValue();
                    if (ProxySettingsActivity.this.currentType == 0) {
                        ProxySettingsActivity.this.currentProxyInfo.secret = "";
                        ProxySettingsActivity.this.currentProxyInfo.username = ProxySettingsActivity.this.inputFields[2].getText().toString();
                        ProxySettingsActivity.this.currentProxyInfo.password = ProxySettingsActivity.this.inputFields[3].getText().toString();
                    } else {
                        ProxySettingsActivity.this.currentProxyInfo.secret = ProxySettingsActivity.this.inputFields[4].getText().toString();
                        ProxySettingsActivity.this.currentProxyInfo.username = "";
                        ProxySettingsActivity.this.currentProxyInfo.password = "";
                    }
                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                    SharedPreferences.Editor editor = preferences.edit();
                    if (ProxySettingsActivity.this.addingNewProxy) {
                        SharedConfig.addProxy(ProxySettingsActivity.this.currentProxyInfo);
                        SharedConfig.currentProxy = ProxySettingsActivity.this.currentProxyInfo;
                        editor.putBoolean("proxy_enabled", true);
                        enabled = true;
                    } else {
                        enabled = preferences.getBoolean("proxy_enabled", false);
                        SharedConfig.saveProxyList();
                    }
                    if (ProxySettingsActivity.this.addingNewProxy || SharedConfig.currentProxy == ProxySettingsActivity.this.currentProxyInfo) {
                        editor.putString("proxy_ip", ProxySettingsActivity.this.currentProxyInfo.address);
                        editor.putString("proxy_pass", ProxySettingsActivity.this.currentProxyInfo.password);
                        editor.putString("proxy_user", ProxySettingsActivity.this.currentProxyInfo.username);
                        editor.putInt("proxy_port", ProxySettingsActivity.this.currentProxyInfo.port);
                        editor.putString("proxy_secret", ProxySettingsActivity.this.currentProxyInfo.secret);
                        ConnectionsManager.setProxySettings(enabled, ProxySettingsActivity.this.currentProxyInfo.address, ProxySettingsActivity.this.currentProxyInfo.port, ProxySettingsActivity.this.currentProxyInfo.username, ProxySettingsActivity.this.currentProxyInfo.password, ProxySettingsActivity.this.currentProxyInfo.secret);
                    }
                    editor.commit();
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
                    ProxySettingsActivity.this.finishFragment();
                }
            }
        });
        ActionBarMenuItem addItemWithWidth = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_ab_done, AndroidUtilities.dp(56.0f));
        this.doneItem = addItemWithWidth;
        addItemWithWidth.setContentDescription(LocaleController.getString("Done", R.string.Done));
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        ScrollView scrollView = new ScrollView(context);
        this.scrollView = scrollView;
        scrollView.setFillViewport(true);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_actionBarDefault));
        frameLayout.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.linearLayout2 = linearLayout;
        linearLayout.setOrientation(1);
        this.scrollView.addView(this.linearLayout2, new FrameLayout.LayoutParams(-1, -2));
        View.OnClickListener typeCellClickListener = new View.OnClickListener() { // from class: org.telegram.ui.ProxySettingsActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ProxySettingsActivity.this.m4510lambda$createView$0$orgtelegramuiProxySettingsActivity(view);
            }
        };
        int a = 0;
        while (a < 2) {
            this.typeCell[a] = new RadioCell(context);
            this.typeCell[a].setBackground(Theme.getSelectorDrawable(true));
            this.typeCell[a].setTag(Integer.valueOf(a));
            if (a == 0) {
                this.typeCell[a].setText(LocaleController.getString("UseProxySocks5", R.string.UseProxySocks5), a == this.currentType, true);
            } else {
                this.typeCell[a].setText(LocaleController.getString("UseProxyTelegram", R.string.UseProxyTelegram), a == this.currentType, false);
            }
            this.linearLayout2.addView(this.typeCell[a], LayoutHelper.createLinear(-1, 50));
            this.typeCell[a].setOnClickListener(typeCellClickListener);
            a++;
        }
        this.sectionCell[0] = new ShadowSectionCell(context);
        this.linearLayout2.addView(this.sectionCell[0], LayoutHelper.createLinear(-1, -2));
        LinearLayout linearLayout2 = new LinearLayout(context);
        this.inputFieldsContainer = linearLayout2;
        linearLayout2.setOrientation(1);
        this.inputFieldsContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        Drawable drawable = null;
        if (Build.VERSION.SDK_INT >= 21) {
            this.inputFieldsContainer.setElevation(AndroidUtilities.dp(1.0f));
            this.inputFieldsContainer.setOutlineProvider(null);
        }
        this.linearLayout2.addView(this.inputFieldsContainer, LayoutHelper.createLinear(-1, -2));
        int i = 5;
        this.inputFields = new EditTextBoldCursor[5];
        int a2 = 0;
        while (a2 < i) {
            FrameLayout container = new FrameLayout(context);
            this.inputFieldsContainer.addView(container, LayoutHelper.createLinear(-1, 64));
            this.inputFields[a2] = new EditTextBoldCursor(context);
            this.inputFields[a2].setTag(Integer.valueOf(a2));
            this.inputFields[a2].setTextSize(1, 16.0f);
            this.inputFields[a2].setHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.inputFields[a2].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a2].setBackground(drawable);
            this.inputFields[a2].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a2].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a2].setCursorWidth(1.5f);
            this.inputFields[a2].setSingleLine(true);
            this.inputFields[a2].setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.inputFields[a2].setHeaderHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
            this.inputFields[a2].setTransformHintToHeader(true);
            this.inputFields[a2].setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated), Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
            if (a2 == 0) {
                this.inputFields[a2].setInputType(524305);
                this.inputFields[a2].addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.ProxySettingsActivity.2
                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable s) {
                        ProxySettingsActivity.this.checkShareDone(true);
                    }
                });
            } else if (a2 == 1) {
                this.inputFields[a2].setInputType(2);
                this.inputFields[a2].addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.ProxySettingsActivity.3
                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable s) {
                        if (!ProxySettingsActivity.this.ignoreOnTextChange) {
                            EditText phoneField = ProxySettingsActivity.this.inputFields[1];
                            int start = phoneField.getSelectionStart();
                            String str = phoneField.getText().toString();
                            StringBuilder builder = new StringBuilder(str.length());
                            for (int a3 = 0; a3 < str.length(); a3++) {
                                String ch = str.substring(a3, a3 + 1);
                                if ("0123456789".contains(ch)) {
                                    builder.append(ch);
                                }
                            }
                            ProxySettingsActivity.this.ignoreOnTextChange = true;
                            int port = Utilities.parseInt((CharSequence) builder.toString()).intValue();
                            if (port < 0 || port > 65535 || !str.equals(builder.toString())) {
                                if (port < 0) {
                                    phoneField.setText("0");
                                } else if (port > 65535) {
                                    phoneField.setText("65535");
                                } else {
                                    phoneField.setText(builder.toString());
                                }
                            } else if (start >= 0) {
                                phoneField.setSelection(Math.min(start, phoneField.length()));
                            }
                            ProxySettingsActivity.this.ignoreOnTextChange = false;
                            ProxySettingsActivity.this.checkShareDone(true);
                        }
                    }
                });
            } else if (a2 == 3) {
                this.inputFields[a2].setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
                this.inputFields[a2].setTypeface(Typeface.DEFAULT);
                this.inputFields[a2].setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                this.inputFields[a2].setInputType(524289);
            }
            this.inputFields[a2].setImeOptions(268435461);
            switch (a2) {
                case 0:
                    this.inputFields[a2].setHintText(LocaleController.getString("UseProxyAddress", R.string.UseProxyAddress));
                    this.inputFields[a2].setText(this.currentProxyInfo.address);
                    break;
                case 1:
                    this.inputFields[a2].setHintText(LocaleController.getString("UseProxyPort", R.string.UseProxyPort));
                    EditTextBoldCursor editTextBoldCursor = this.inputFields[a2];
                    editTextBoldCursor.setText("" + this.currentProxyInfo.port);
                    break;
                case 2:
                    this.inputFields[a2].setHintText(LocaleController.getString("UseProxyUsername", R.string.UseProxyUsername));
                    this.inputFields[a2].setText(this.currentProxyInfo.username);
                    break;
                case 3:
                    this.inputFields[a2].setHintText(LocaleController.getString("UseProxyPassword", R.string.UseProxyPassword));
                    this.inputFields[a2].setText(this.currentProxyInfo.password);
                    break;
                case 4:
                    this.inputFields[a2].setHintText(LocaleController.getString("UseProxySecret", R.string.UseProxySecret));
                    this.inputFields[a2].setText(this.currentProxyInfo.secret);
                    break;
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[a2].setSelection(editTextBoldCursorArr[a2].length());
            this.inputFields[a2].setPadding(0, 0, 0, 0);
            container.addView(this.inputFields[a2], LayoutHelper.createFrame(-1, -1.0f, 51, 17.0f, a2 == 0 ? 12.0f : 0.0f, 17.0f, 0.0f));
            this.inputFields[a2].setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.ProxySettingsActivity$$ExternalSyntheticLambda5
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView, int i2, KeyEvent keyEvent) {
                    return ProxySettingsActivity.this.m4511lambda$createView$1$orgtelegramuiProxySettingsActivity(textView, i2, keyEvent);
                }
            });
            a2++;
            i = 5;
            drawable = null;
        }
        for (int i2 = 0; i2 < 2; i2++) {
            this.bottomCells[i2] = new TextInfoPrivacyCell(context);
            this.bottomCells[i2].setBackground(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            if (i2 == 0) {
                this.bottomCells[i2].setText(LocaleController.getString("UseProxyInfo", R.string.UseProxyInfo));
            } else {
                TextInfoPrivacyCell textInfoPrivacyCell = this.bottomCells[i2];
                textInfoPrivacyCell.setText(LocaleController.getString("UseProxyTelegramInfo", R.string.UseProxyTelegramInfo) + "\n\n" + LocaleController.getString("UseProxyTelegramInfo2", R.string.UseProxyTelegramInfo2));
                this.bottomCells[i2].setVisibility(8);
            }
            this.linearLayout2.addView(this.bottomCells[i2], LayoutHelper.createLinear(-1, -2));
        }
        TextSettingsCell textSettingsCell = new TextSettingsCell(this.fragmentView.getContext());
        this.pasteCell = textSettingsCell;
        textSettingsCell.setBackground(Theme.getSelectorDrawable(true));
        this.pasteCell.setText(LocaleController.getString("PasteFromClipboard", R.string.PasteFromClipboard), false);
        this.pasteCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
        this.pasteCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ProxySettingsActivity$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ProxySettingsActivity.this.m4513lambda$createView$3$orgtelegramuiProxySettingsActivity(view);
            }
        });
        this.linearLayout2.addView(this.pasteCell, 0, LayoutHelper.createLinear(-1, -2));
        this.pasteCell.setVisibility(8);
        this.sectionCell[2] = new ShadowSectionCell(this.fragmentView.getContext());
        this.sectionCell[2].setBackground(Theme.getThemedDrawable(this.fragmentView.getContext(), (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.linearLayout2.addView(this.sectionCell[2], 1, LayoutHelper.createLinear(-1, -2));
        this.sectionCell[2].setVisibility(8);
        TextSettingsCell textSettingsCell2 = new TextSettingsCell(context);
        this.shareCell = textSettingsCell2;
        textSettingsCell2.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.shareCell.setText(LocaleController.getString("ShareFile", R.string.ShareFile), false);
        this.shareCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
        this.linearLayout2.addView(this.shareCell, LayoutHelper.createLinear(-1, -2));
        this.shareCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ProxySettingsActivity$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ProxySettingsActivity.this.m4514lambda$createView$4$orgtelegramuiProxySettingsActivity(view);
            }
        });
        this.sectionCell[1] = new ShadowSectionCell(context);
        this.sectionCell[1].setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.linearLayout2.addView(this.sectionCell[1], LayoutHelper.createLinear(-1, -2));
        this.clipboardManager = (ClipboardManager) context.getSystemService("clipboard");
        this.shareDoneEnabled = true;
        this.shareDoneProgress = 1.0f;
        checkShareDone(false);
        this.currentType = -1;
        setProxyType(!TextUtils.isEmpty(this.currentProxyInfo.secret) ? 1 : 0, false);
        this.pasteType = -1;
        this.pasteString = null;
        updatePasteCell();
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-ProxySettingsActivity */
    public /* synthetic */ void m4510lambda$createView$0$orgtelegramuiProxySettingsActivity(View view) {
        setProxyType(((Integer) view.getTag()).intValue(), true);
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-ProxySettingsActivity */
    public /* synthetic */ boolean m4511lambda$createView$1$orgtelegramuiProxySettingsActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            int num = ((Integer) textView.getTag()).intValue();
            int i2 = num + 1;
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            if (i2 < editTextBoldCursorArr.length) {
                editTextBoldCursorArr[num + 1].requestFocus();
            }
            return true;
        } else if (i == 6) {
            finishFragment();
            return true;
        } else {
            return false;
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-ProxySettingsActivity */
    public /* synthetic */ void m4513lambda$createView$3$orgtelegramuiProxySettingsActivity(View v) {
        if (this.pasteType != -1) {
            int i = 0;
            while (true) {
                String[] strArr = this.pasteFields;
                if (i < strArr.length) {
                    int i2 = this.pasteType;
                    if ((i2 != 0 || i != 4) && (i2 != 1 || (i != 2 && i != 3))) {
                        if (strArr[i] != null) {
                            try {
                                this.inputFields[i].setText(URLDecoder.decode(strArr[i], "UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                                this.inputFields[i].setText(this.pasteFields[i]);
                            }
                        } else {
                            this.inputFields[i].setText((CharSequence) null);
                        }
                    }
                    i++;
                } else {
                    EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                    editTextBoldCursorArr[0].setSelection(editTextBoldCursorArr[0].length());
                    setProxyType(this.pasteType, true, new Runnable() { // from class: org.telegram.ui.ProxySettingsActivity$$ExternalSyntheticLambda6
                        @Override // java.lang.Runnable
                        public final void run() {
                            ProxySettingsActivity.this.m4512lambda$createView$2$orgtelegramuiProxySettingsActivity();
                        }
                    });
                    return;
                }
            }
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-ProxySettingsActivity */
    public /* synthetic */ void m4512lambda$createView$2$orgtelegramuiProxySettingsActivity() {
        AndroidUtilities.hideKeyboard(this.inputFieldsContainer.findFocus());
        for (int i = 0; i < this.pasteFields.length; i++) {
            int i2 = this.pasteType;
            if ((i2 != 0 || i == 4) && (i2 != 1 || i == 2 || i == 3)) {
                this.inputFields[i].setText((CharSequence) null);
            }
        }
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-ProxySettingsActivity */
    public /* synthetic */ void m4514lambda$createView$4$orgtelegramuiProxySettingsActivity(View v) {
        String url;
        StringBuilder params = new StringBuilder();
        String address = this.inputFields[0].getText().toString();
        String password = this.inputFields[3].getText().toString();
        String user = this.inputFields[2].getText().toString();
        String port = this.inputFields[1].getText().toString();
        String secret = this.inputFields[4].getText().toString();
        try {
            if (!TextUtils.isEmpty(address)) {
                params.append("server=");
                params.append(URLEncoder.encode(address, "UTF-8"));
            }
            if (!TextUtils.isEmpty(port)) {
                if (params.length() != 0) {
                    params.append("&");
                }
                params.append("port=");
                params.append(URLEncoder.encode(port, "UTF-8"));
            }
            if (this.currentType == 1) {
                url = "https://t.me/proxy?";
                if (params.length() != 0) {
                    params.append("&");
                }
                params.append("secret=");
                params.append(URLEncoder.encode(secret, "UTF-8"));
            } else {
                url = "https://t.me/socks?";
                if (!TextUtils.isEmpty(user)) {
                    if (params.length() != 0) {
                        params.append("&");
                    }
                    params.append("user=");
                    params.append(URLEncoder.encode(user, "UTF-8"));
                }
                if (!TextUtils.isEmpty(password)) {
                    if (params.length() != 0) {
                        params.append("&");
                    }
                    params.append("pass=");
                    params.append(URLEncoder.encode(password, "UTF-8"));
                }
            }
            if (params.length() == 0) {
                return;
            }
            Intent shareIntent = new Intent("android.intent.action.SEND");
            shareIntent.setType(ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN);
            shareIntent.putExtra("android.intent.extra.TEXT", url + params.toString());
            Intent chooserIntent = Intent.createChooser(shareIntent, LocaleController.getString("ShareLink", R.string.ShareLink));
            chooserIntent.setFlags(268435456);
            getParentActivity().startActivity(chooserIntent);
        } catch (Exception e) {
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public void updatePasteCell() {
        String clipText;
        char c;
        ClipData clip = this.clipboardManager.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            try {
                clipText = clip.getItemAt(0).coerceToText(this.fragmentView.getContext()).toString();
            } catch (Exception e) {
                clipText = null;
            }
        } else {
            clipText = null;
        }
        if (TextUtils.equals(clipText, this.pasteString)) {
            return;
        }
        this.pasteType = -1;
        this.pasteString = clipText;
        this.pasteFields = new String[this.inputFields.length];
        if (clipText != null) {
            String[] params = null;
            String[] socksStrings = {"t.me/socks?", "tg://socks?"};
            int i = 0;
            while (true) {
                if (i >= socksStrings.length) {
                    break;
                }
                int index = clipText.indexOf(socksStrings[i]);
                if (index < 0) {
                    i++;
                } else {
                    this.pasteType = 0;
                    params = clipText.substring(socksStrings[i].length() + index).split("&");
                    break;
                }
            }
            if (params == null) {
                String[] proxyStrings = {"t.me/proxy?", "tg://proxy?"};
                int i2 = 0;
                while (true) {
                    if (i2 >= proxyStrings.length) {
                        break;
                    }
                    int index2 = clipText.indexOf(proxyStrings[i2]);
                    if (index2 < 0) {
                        i2++;
                    } else {
                        this.pasteType = 1;
                        params = clipText.substring(proxyStrings[i2].length() + index2).split("&");
                        break;
                    }
                }
            }
            if (params != null) {
                for (String str : params) {
                    String[] pair = str.split("=");
                    if (pair.length == 2) {
                        String lowerCase = pair[0].toLowerCase();
                        switch (lowerCase.hashCode()) {
                            case -906277200:
                                if (lowerCase.equals("secret")) {
                                    c = 4;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -905826493:
                                if (lowerCase.equals("server")) {
                                    c = 0;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 3433489:
                                if (lowerCase.equals("pass")) {
                                    c = 3;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 3446913:
                                if (lowerCase.equals("port")) {
                                    c = 1;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 3599307:
                                if (lowerCase.equals("user")) {
                                    c = 2;
                                    break;
                                }
                                c = 65535;
                                break;
                            default:
                                c = 65535;
                                break;
                        }
                        switch (c) {
                            case 0:
                                this.pasteFields[0] = pair[1];
                                continue;
                            case 1:
                                this.pasteFields[1] = pair[1];
                                continue;
                            case 2:
                                if (this.pasteType == 0) {
                                    this.pasteFields[2] = pair[1];
                                    break;
                                } else {
                                    continue;
                                }
                            case 3:
                                if (this.pasteType == 0) {
                                    this.pasteFields[3] = pair[1];
                                    break;
                                } else {
                                    continue;
                                }
                            case 4:
                                if (this.pasteType == 1) {
                                    this.pasteFields[4] = pair[1];
                                    break;
                                } else {
                                    continue;
                                }
                        }
                    }
                }
            }
        }
        if (this.pasteType != -1) {
            if (this.pasteCell.getVisibility() != 0) {
                this.pasteCell.setVisibility(0);
                this.sectionCell[2].setVisibility(0);
            }
        } else if (this.pasteCell.getVisibility() != 8) {
            this.pasteCell.setVisibility(8);
            this.sectionCell[2].setVisibility(8);
        }
    }

    private void setShareDoneEnabled(boolean enabled, boolean animated) {
        if (this.shareDoneEnabled != enabled) {
            ValueAnimator valueAnimator = this.shareDoneAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            } else if (animated) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.shareDoneAnimator = ofFloat;
                ofFloat.setDuration(200L);
                this.shareDoneAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ProxySettingsActivity$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        ProxySettingsActivity.this.m4516x3401043c(valueAnimator2);
                    }
                });
            }
            float f = 0.0f;
            float f2 = 1.0f;
            if (animated) {
                float[] fArr = this.shareDoneProgressAnimValues;
                fArr[0] = this.shareDoneProgress;
                if (enabled) {
                    f = 1.0f;
                }
                fArr[1] = f;
                this.shareDoneAnimator.start();
            } else {
                if (enabled) {
                    f = 1.0f;
                }
                this.shareDoneProgress = f;
                this.shareCell.setTextColor(Theme.getColor(enabled ? Theme.key_windowBackgroundWhiteBlueText4 : Theme.key_windowBackgroundWhiteGrayText2));
                ActionBarMenuItem actionBarMenuItem = this.doneItem;
                if (!enabled) {
                    f2 = 0.5f;
                }
                actionBarMenuItem.setAlpha(f2);
            }
            this.shareCell.setEnabled(enabled);
            this.doneItem.setEnabled(enabled);
            this.shareDoneEnabled = enabled;
        }
    }

    /* renamed from: lambda$setShareDoneEnabled$5$org-telegram-ui-ProxySettingsActivity */
    public /* synthetic */ void m4516x3401043c(ValueAnimator a) {
        this.shareDoneProgress = AndroidUtilities.lerp(this.shareDoneProgressAnimValues, a.getAnimatedFraction());
        this.shareCell.setTextColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2), Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4), this.shareDoneProgress));
        this.doneItem.setAlpha((this.shareDoneProgress / 2.0f) + 0.5f);
    }

    public void checkShareDone(boolean animated) {
        if (this.shareCell == null || this.doneItem == null) {
            return;
        }
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
        boolean z = false;
        if (editTextBoldCursorArr[0] == null || editTextBoldCursorArr[1] == null) {
            return;
        }
        if (editTextBoldCursorArr[0].length() != 0 && Utilities.parseInt((CharSequence) this.inputFields[1].getText().toString()).intValue() != 0) {
            z = true;
        }
        setShareDoneEnabled(z, animated);
    }

    private void setProxyType(int type, boolean animated) {
        setProxyType(type, animated, null);
    }

    private void setProxyType(int type, boolean animated, final Runnable onTransitionEnd) {
        if (this.currentType != type) {
            this.currentType = type;
            if (Build.VERSION.SDK_INT >= 23) {
                TransitionManager.endTransitions(this.linearLayout2);
            }
            boolean z = true;
            if (animated && Build.VERSION.SDK_INT >= 21) {
                TransitionSet transitionSet = new TransitionSet().addTransition(new Fade(2)).addTransition(new ChangeBounds()).addTransition(new Fade(1)).setInterpolator((TimeInterpolator) CubicBezierInterpolator.DEFAULT).setDuration(250L);
                if (onTransitionEnd != null) {
                    transitionSet.addListener(new Transition.TransitionListener() { // from class: org.telegram.ui.ProxySettingsActivity.4
                        @Override // android.transition.Transition.TransitionListener
                        public void onTransitionStart(Transition transition) {
                        }

                        @Override // android.transition.Transition.TransitionListener
                        public void onTransitionEnd(Transition transition) {
                            onTransitionEnd.run();
                        }

                        @Override // android.transition.Transition.TransitionListener
                        public void onTransitionCancel(Transition transition) {
                        }

                        @Override // android.transition.Transition.TransitionListener
                        public void onTransitionPause(Transition transition) {
                        }

                        @Override // android.transition.Transition.TransitionListener
                        public void onTransitionResume(Transition transition) {
                        }
                    });
                }
                TransitionManager.beginDelayedTransition(this.linearLayout2, transitionSet);
            }
            int i = this.currentType;
            if (i == 0) {
                this.bottomCells[0].setVisibility(0);
                this.bottomCells[1].setVisibility(8);
                ((View) this.inputFields[4].getParent()).setVisibility(8);
                ((View) this.inputFields[3].getParent()).setVisibility(0);
                ((View) this.inputFields[2].getParent()).setVisibility(0);
            } else if (i == 1) {
                this.bottomCells[0].setVisibility(8);
                this.bottomCells[1].setVisibility(0);
                ((View) this.inputFields[4].getParent()).setVisibility(0);
                ((View) this.inputFields[3].getParent()).setVisibility(8);
                ((View) this.inputFields[2].getParent()).setVisibility(8);
            }
            this.typeCell[0].setChecked(this.currentType == 0, animated);
            RadioCell radioCell = this.typeCell[1];
            if (this.currentType != 1) {
                z = false;
            }
            radioCell.setChecked(z, animated);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !backward && this.addingNewProxy) {
            this.inputFields[0].requestFocus();
            AndroidUtilities.showKeyboard(this.inputFields[0]);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate delegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ProxySettingsActivity$$ExternalSyntheticLambda7
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                ProxySettingsActivity.this.m4515x763ae1bf();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder));
        arrayList.add(new ThemeDescription(this.inputFieldsContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        arrayList.add(new ThemeDescription(this.shareCell, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.shareCell, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, delegate, Theme.key_windowBackgroundWhiteBlueText4));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, delegate, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.pasteCell, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.pasteCell, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.pasteCell, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
        for (int a = 0; a < this.typeCell.length; a++) {
            arrayList.add(new ThemeDescription(this.typeCell[a], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(this.typeCell[a], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
            arrayList.add(new ThemeDescription(this.typeCell[a], 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.typeCell[a], ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackground));
            arrayList.add(new ThemeDescription(this.typeCell[a], ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackgroundChecked));
        }
        if (this.inputFields != null) {
            for (int a2 = 0; a2 < this.inputFields.length; a2++) {
                arrayList.add(new ThemeDescription(this.inputFields[a2], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(this.inputFields[a2], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
                arrayList.add(new ThemeDescription(this.inputFields[a2], ThemeDescription.FLAG_HINTTEXTCOLOR | ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
                arrayList.add(new ThemeDescription(this.inputFields[a2], ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, delegate, Theme.key_windowBackgroundWhiteInputField));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, delegate, Theme.key_windowBackgroundWhiteInputFieldActivated));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, delegate, Theme.key_windowBackgroundWhiteRedText3));
            }
        } else {
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        }
        arrayList.add(new ThemeDescription(this.headerCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        int a3 = 0;
        while (true) {
            ShadowSectionCell[] shadowSectionCellArr = this.sectionCell;
            if (a3 >= shadowSectionCellArr.length) {
                break;
            }
            if (shadowSectionCellArr[a3] != null) {
                arrayList.add(new ThemeDescription(this.sectionCell[a3], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
            }
            a3++;
        }
        for (int i = 0; i < this.bottomCells.length; i++) {
            arrayList.add(new ThemeDescription(this.bottomCells[i], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
            arrayList.add(new ThemeDescription(this.bottomCells[i], 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
            arrayList.add(new ThemeDescription(this.bottomCells[i], ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteLinkText));
        }
        return arrayList;
    }

    /* renamed from: lambda$getThemeDescriptions$6$org-telegram-ui-ProxySettingsActivity */
    public /* synthetic */ void m4515x763ae1bf() {
        ValueAnimator valueAnimator;
        if (this.shareCell != null && ((valueAnimator = this.shareDoneAnimator) == null || !valueAnimator.isRunning())) {
            this.shareCell.setTextColor(Theme.getColor(this.shareDoneEnabled ? Theme.key_windowBackgroundWhiteBlueText4 : Theme.key_windowBackgroundWhiteGrayText2));
        }
        if (this.inputFields != null) {
            int i = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                if (i < editTextBoldCursorArr.length) {
                    editTextBoldCursorArr[i].setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated), Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
                    i++;
                } else {
                    return;
                }
            }
        }
    }
}
