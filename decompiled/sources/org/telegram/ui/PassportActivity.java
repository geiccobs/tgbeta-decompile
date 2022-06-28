package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import androidx.core.net.MailTo;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.crypto.Cipher;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MrzRecognizer;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SRPHelper;
import org.telegram.messenger.SecureDocument;
import org.telegram.messenger.SecureDocumentKey;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
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
import org.telegram.ui.CameraScanActivity;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.HintEditText;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgress;
import org.telegram.ui.Components.SlideView;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.CountrySelectActivity;
import org.telegram.ui.PassportActivity;
import org.telegram.ui.PhotoViewer;
/* loaded from: classes4.dex */
public class PassportActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int FIELD_ADDRESS_COUNT = 6;
    private static final int FIELD_BIRTHDAY = 3;
    private static final int FIELD_CARDNUMBER = 7;
    private static final int FIELD_CITIZENSHIP = 5;
    private static final int FIELD_CITY = 3;
    private static final int FIELD_COUNTRY = 5;
    private static final int FIELD_EMAIL = 0;
    private static final int FIELD_EXPIRE = 8;
    private static final int FIELD_GENDER = 4;
    private static final int FIELD_IDENTITY_COUNT = 9;
    private static final int FIELD_IDENTITY_NODOC_COUNT = 7;
    private static final int FIELD_MIDNAME = 1;
    private static final int FIELD_NAME = 0;
    private static final int FIELD_NATIVE_COUNT = 3;
    private static final int FIELD_NATIVE_MIDNAME = 1;
    private static final int FIELD_NATIVE_NAME = 0;
    private static final int FIELD_NATIVE_SURNAME = 2;
    private static final int FIELD_PASSWORD = 0;
    private static final int FIELD_PHONE = 2;
    private static final int FIELD_PHONECODE = 1;
    private static final int FIELD_PHONECOUNTRY = 0;
    private static final int FIELD_POSTCODE = 2;
    private static final int FIELD_RESIDENCE = 6;
    private static final int FIELD_STATE = 4;
    private static final int FIELD_STREET1 = 0;
    private static final int FIELD_STREET2 = 1;
    private static final int FIELD_SURNAME = 2;
    public static final int TYPE_ADDRESS = 2;
    public static final int TYPE_EMAIL = 4;
    public static final int TYPE_EMAIL_VERIFICATION = 6;
    public static final int TYPE_IDENTITY = 1;
    public static final int TYPE_MANAGE = 8;
    public static final int TYPE_PASSWORD = 5;
    public static final int TYPE_PHONE = 3;
    public static final int TYPE_PHONE_VERIFICATION = 7;
    public static final int TYPE_REQUEST = 0;
    private static final int UPLOADING_TYPE_DOCUMENTS = 0;
    private static final int UPLOADING_TYPE_FRONT = 2;
    private static final int UPLOADING_TYPE_REVERSE = 3;
    private static final int UPLOADING_TYPE_SELFIE = 1;
    private static final int UPLOADING_TYPE_TRANSLATION = 4;
    private static final int attach_document = 4;
    private static final int attach_photo = 0;
    private static final int done_button = 2;
    private static final int info_item = 1;
    private TextView acceptTextView;
    private TextSettingsCell addDocumentCell;
    private ShadowSectionCell addDocumentSectionCell;
    private boolean allowNonLatinName;
    private ArrayList<TLRPC.TL_secureRequiredType> availableDocumentTypes;
    private TextInfoPrivacyCell bottomCell;
    private TextInfoPrivacyCell bottomCellTranslation;
    private FrameLayout bottomLayout;
    private boolean callbackCalled;
    private ChatAttachAlert chatAttachAlert;
    private HashMap<String, String> codesMap;
    private ArrayList<String> countriesArray;
    private HashMap<String, String> countriesMap;
    private int currentActivityType;
    private long currentBotId;
    private String currentCallbackUrl;
    private String currentCitizeship;
    private HashMap<String, String> currentDocumentValues;
    private TLRPC.TL_secureRequiredType currentDocumentsType;
    private TLRPC.TL_secureValue currentDocumentsTypeValue;
    private String currentEmail;
    private int[] currentExpireDate;
    private TLRPC.TL_account_authorizationForm currentForm;
    private String currentGender;
    private String currentNonce;
    private TLRPC.TL_account_password currentPassword;
    private String currentPayload;
    private TLRPC.TL_auth_sentCode currentPhoneVerification;
    private LinearLayout currentPhotoViewerLayout;
    private String currentPicturePath;
    private String currentPublicKey;
    private String currentResidence;
    private String currentScope;
    private TLRPC.TL_secureRequiredType currentType;
    private TLRPC.TL_secureValue currentTypeValue;
    private HashMap<String, String> currentValues;
    private int currentViewNum;
    private PassportActivityDelegate delegate;
    private TextSettingsCell deletePassportCell;
    private ArrayList<View> dividers;
    private boolean documentOnly;
    private ArrayList<SecureDocument> documents;
    private HashMap<SecureDocument, SecureDocumentCell> documentsCells;
    private HashMap<String, String> documentsErrors;
    private LinearLayout documentsLayout;
    private HashMap<TLRPC.TL_secureRequiredType, TLRPC.TL_secureRequiredType> documentsToTypesLink;
    private ActionBarMenuItem doneItem;
    private AnimatorSet doneItemAnimation;
    private int emailCodeLength;
    private ImageView emptyImageView;
    private LinearLayout emptyLayout;
    private TextView emptyTextView1;
    private TextView emptyTextView2;
    private TextView emptyTextView3;
    private EmptyTextProgressView emptyView;
    private HashMap<String, HashMap<String, String>> errorsMap;
    private HashMap<String, String> errorsValues;
    private View extraBackgroundView;
    private View extraBackgroundView2;
    private HashMap<String, String> fieldsErrors;
    private SecureDocument frontDocument;
    private LinearLayout frontLayout;
    private HeaderCell headerCell;
    private boolean ignoreOnFailure;
    private boolean ignoreOnPhoneChange;
    private boolean ignoreOnTextChange;
    private String initialValues;
    private EditTextBoldCursor[] inputExtraFields;
    private ViewGroup[] inputFieldContainers;
    private EditTextBoldCursor[] inputFields;
    private HashMap<String, String> languageMap;
    private LinearLayout linearLayout2;
    private HashMap<String, String> mainErrorsMap;
    private TextInfoPrivacyCell nativeInfoCell;
    private boolean needActivityResult;
    private CharSequence noAllDocumentsErrorText;
    private CharSequence noAllTranslationErrorText;
    private ImageView noPasswordImageView;
    private TextView noPasswordSetTextView;
    private TextView noPasswordTextView;
    private boolean[] nonLatinNames;
    private FrameLayout passwordAvatarContainer;
    private TextView passwordForgotButton;
    private TextInfoPrivacyCell passwordInfoRequestTextView;
    private TextInfoPrivacyCell passwordRequestTextView;
    private PassportActivityDelegate pendingDelegate;
    private ErrorRunnable pendingErrorRunnable;
    private Runnable pendingFinishRunnable;
    private String pendingPhone;
    private Dialog permissionsDialog;
    private ArrayList<String> permissionsItems;
    private HashMap<String, String> phoneFormatMap;
    private TextView plusTextView;
    private PassportActivity presentAfterAnimation;
    private AlertDialog progressDialog;
    private ContextProgressView progressView;
    private ContextProgressView progressViewButton;
    private PhotoViewer.PhotoViewerProvider provider;
    private SecureDocument reverseDocument;
    private LinearLayout reverseLayout;
    private byte[] saltedPassword;
    private byte[] savedPasswordHash;
    private byte[] savedSaltedPassword;
    private TextSettingsCell scanDocumentCell;
    private int scrollHeight;
    private ScrollView scrollView;
    private ShadowSectionCell sectionCell;
    private ShadowSectionCell sectionCell2;
    private byte[] secureSecret;
    private long secureSecretId;
    private SecureDocument selfieDocument;
    private LinearLayout selfieLayout;
    private TextInfoPrivacyCell topErrorCell;
    private ArrayList<SecureDocument> translationDocuments;
    private LinearLayout translationLayout;
    private HashMap<TLRPC.TL_secureRequiredType, HashMap<String, String>> typesValues;
    private HashMap<TLRPC.TL_secureRequiredType, TextDetailSecureCell> typesViews;
    private TextSettingsCell uploadDocumentCell;
    private TextDetailSettingsCell uploadFrontCell;
    private TextDetailSettingsCell uploadReverseCell;
    private TextDetailSettingsCell uploadSelfieCell;
    private TextSettingsCell uploadTranslationCell;
    private HashMap<String, SecureDocument> uploadingDocuments;
    private int uploadingFileType;
    private boolean useCurrentValue;
    private int usingSavedPassword;
    private SlideView[] views;

    /* loaded from: classes4.dex */
    public interface ErrorRunnable {
        void onError(String str, String str2);
    }

    /* loaded from: classes4.dex */
    public interface PassportActivityDelegate {
        void deleteValue(TLRPC.TL_secureRequiredType tL_secureRequiredType, TLRPC.TL_secureRequiredType tL_secureRequiredType2, ArrayList<TLRPC.TL_secureRequiredType> arrayList, boolean z, Runnable runnable, ErrorRunnable errorRunnable);

        SecureDocument saveFile(TLRPC.TL_secureFile tL_secureFile);

        void saveValue(TLRPC.TL_secureRequiredType tL_secureRequiredType, String str, String str2, TLRPC.TL_secureRequiredType tL_secureRequiredType2, String str3, ArrayList<SecureDocument> arrayList, SecureDocument secureDocument, ArrayList<SecureDocument> arrayList2, SecureDocument secureDocument2, SecureDocument secureDocument3, Runnable runnable, ErrorRunnable errorRunnable);
    }

    /* loaded from: classes4.dex */
    public class LinkSpan extends ClickableSpan {
        public LinkSpan() {
            PassportActivity.this = this$0;
        }

        @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(true);
            ds.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        }

        @Override // android.text.style.ClickableSpan
        public void onClick(View widget) {
            Browser.openUrl(PassportActivity.this.getParentActivity(), PassportActivity.this.currentForm.privacy_policy_url);
        }
    }

    /* loaded from: classes4.dex */
    public class TextDetailSecureCell extends FrameLayout {
        private ImageView checkImageView;
        private boolean needDivider;
        private TextView textView;
        private TextView valueTextView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public TextDetailSecureCell(Context context) {
            super(context);
            PassportActivity.this = this$0;
            int i = 21;
            int padding = this$0.currentActivityType == 8 ? 21 : 51;
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            int i2 = 5;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? padding : 21, 10.0f, LocaleController.isRTL ? 21 : padding, 0.0f));
            TextView textView2 = new TextView(context);
            this.valueTextView = textView2;
            textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.valueTextView.setTextSize(1, 13.0f);
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.valueTextView.setPadding(0, 0, 0, 0);
            addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? padding : 21, 35.0f, !LocaleController.isRTL ? padding : i, 0.0f));
            ImageView imageView = new ImageView(context);
            this.checkImageView = imageView;
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), PorterDuff.Mode.MULTIPLY));
            this.checkImageView.setImageResource(R.drawable.sticker_added);
            addView(this.checkImageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 3 : i2) | 48, 21.0f, 25.0f, 21.0f, 0.0f));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), C.BUFFER_FLAG_ENCRYPTED));
        }

        public void setTextAndValue(String text, CharSequence value, boolean divider) {
            this.textView.setText(text);
            this.valueTextView.setText(value);
            this.needDivider = divider;
            setWillNotDraw(!divider);
        }

        public void setChecked(boolean checked) {
            this.checkImageView.setVisibility(checked ? 0 : 4);
        }

        public void setValue(CharSequence value) {
            this.valueTextView.setText(value);
        }

        public void setNeedDivider(boolean value) {
            this.needDivider = value;
            setWillNotDraw(!value);
            invalidate();
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(20.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class SecureDocumentCell extends FrameLayout implements DownloadController.FileDownloadProgressListener {
        private int TAG;
        private int buttonState;
        private SecureDocument currentSecureDocument;
        private BackupImageView imageView;
        private RadialProgress radialProgress = new RadialProgress(this);
        private TextView textView;
        private TextView valueTextView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public SecureDocumentCell(Context context) {
            super(context);
            PassportActivity.this = this$0;
            this.TAG = DownloadController.getInstance(this$0.currentAccount).generateObserverTag();
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView = backupImageView;
            int i = 5;
            addView(backupImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 8.0f, 21.0f, 0.0f));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            int i2 = 21;
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 21 : 81, 10.0f, LocaleController.isRTL ? 81 : 21, 0.0f));
            TextView textView2 = new TextView(context);
            this.valueTextView = textView2;
            textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.valueTextView.setTextSize(1, 13.0f);
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setPadding(0, 0, 0, 0);
            addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 21 : 81, 35.0f, LocaleController.isRTL ? 81 : i2, 0.0f));
            setWillNotDraw(false);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + 1, C.BUFFER_FLAG_ENCRYPTED));
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            int x = this.imageView.getLeft() + ((this.imageView.getMeasuredWidth() - AndroidUtilities.dp(24.0f)) / 2);
            int y = this.imageView.getTop() + ((this.imageView.getMeasuredHeight() - AndroidUtilities.dp(24.0f)) / 2);
            this.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            boolean result = super.drawChild(canvas, child, drawingTime);
            if (child == this.imageView) {
                this.radialProgress.draw(canvas);
            }
            return result;
        }

        public void setTextAndValueAndImage(String text, CharSequence value, SecureDocument document) {
            this.textView.setText(text);
            this.valueTextView.setText(value);
            this.imageView.setImage(document, "48_48");
            this.currentSecureDocument = document;
            updateButtonState(false);
        }

        public void setValue(CharSequence value) {
            this.valueTextView.setText(value);
        }

        public void updateButtonState(boolean animated) {
            String fileName = FileLoader.getAttachFileName(this.currentSecureDocument);
            File path = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(this.currentSecureDocument);
            boolean fileExists = path.exists();
            if (TextUtils.isEmpty(fileName)) {
                this.radialProgress.setBackground(null, false, false);
                return;
            }
            float f = 0.0f;
            if (this.currentSecureDocument.path != null) {
                if (this.currentSecureDocument.inputFile != null) {
                    DownloadController.getInstance(PassportActivity.this.currentAccount).removeLoadingFileObserver(this);
                    this.radialProgress.setBackground(null, false, animated);
                    this.buttonState = -1;
                    return;
                }
                DownloadController.getInstance(PassportActivity.this.currentAccount).addLoadingFileObserver(this.currentSecureDocument.path, this);
                this.buttonState = 1;
                Float progress = ImageLoader.getInstance().getFileProgress(this.currentSecureDocument.path);
                this.radialProgress.setBackground(Theme.chat_photoStatesDrawables[5][0], true, animated);
                RadialProgress radialProgress = this.radialProgress;
                if (progress != null) {
                    f = progress.floatValue();
                }
                radialProgress.setProgress(f, false);
                invalidate();
            } else if (fileExists) {
                DownloadController.getInstance(PassportActivity.this.currentAccount).removeLoadingFileObserver(this);
                this.buttonState = -1;
                this.radialProgress.setBackground(null, false, animated);
                invalidate();
            } else {
                DownloadController.getInstance(PassportActivity.this.currentAccount).addLoadingFileObserver(fileName, this);
                this.buttonState = 1;
                Float progress2 = ImageLoader.getInstance().getFileProgress(fileName);
                this.radialProgress.setBackground(Theme.chat_photoStatesDrawables[5][0], true, animated);
                RadialProgress radialProgress2 = this.radialProgress;
                if (progress2 != null) {
                    f = progress2.floatValue();
                }
                radialProgress2.setProgress(f, animated);
                invalidate();
            }
        }

        @Override // android.view.View
        public void invalidate() {
            super.invalidate();
            this.textView.invalidate();
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(20.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public void onFailedDownload(String fileName, boolean canceled) {
            updateButtonState(false);
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public void onSuccessDownload(String fileName) {
            this.radialProgress.setProgress(1.0f, true);
            updateButtonState(true);
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public void onProgressDownload(String fileName, long downloadedSize, long totalSize) {
            this.radialProgress.setProgress(Math.min(1.0f, ((float) downloadedSize) / ((float) totalSize)), true);
            if (this.buttonState != 1) {
                updateButtonState(false);
            }
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public void onProgressUpload(String fileName, long uploadedSize, long totalSize, boolean isEncrypted) {
            this.radialProgress.setProgress(Math.min(1.0f, ((float) uploadedSize) / ((float) totalSize)), true);
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public int getObserverTag() {
            return this.TAG;
        }
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public PassportActivity(int type, long botId, String scope, String publicKey, String payload, String nonce, String callbackUrl, TLRPC.TL_account_authorizationForm form, TLRPC.TL_account_password accountPassword) {
        this(type, form, accountPassword, (TLRPC.TL_secureRequiredType) null, (TLRPC.TL_secureValue) null, (TLRPC.TL_secureRequiredType) null, (TLRPC.TL_secureValue) null, (HashMap<String, String>) null, (HashMap<String, String>) null);
        int size;
        String description;
        String target;
        String target2;
        String hash;
        char c;
        boolean found;
        TLRPC.TL_account_authorizationForm tL_account_authorizationForm = form;
        this.currentBotId = botId;
        this.currentPayload = payload;
        this.currentNonce = nonce;
        this.currentScope = scope;
        this.currentPublicKey = publicKey;
        this.currentCallbackUrl = callbackUrl;
        if (type == 0 && !tL_account_authorizationForm.errors.isEmpty()) {
            try {
                Collections.sort(tL_account_authorizationForm.errors, new Comparator<TLRPC.SecureValueError>() { // from class: org.telegram.ui.PassportActivity.2
                    int getErrorValue(TLRPC.SecureValueError error) {
                        if (error instanceof TLRPC.TL_secureValueError) {
                            return 0;
                        }
                        if (error instanceof TLRPC.TL_secureValueErrorFrontSide) {
                            return 1;
                        }
                        if (error instanceof TLRPC.TL_secureValueErrorReverseSide) {
                            return 2;
                        }
                        if (error instanceof TLRPC.TL_secureValueErrorSelfie) {
                            return 3;
                        }
                        if (error instanceof TLRPC.TL_secureValueErrorTranslationFile) {
                            return 4;
                        }
                        if (error instanceof TLRPC.TL_secureValueErrorTranslationFiles) {
                            return 5;
                        }
                        if (error instanceof TLRPC.TL_secureValueErrorFile) {
                            return 6;
                        }
                        if (error instanceof TLRPC.TL_secureValueErrorFiles) {
                            return 7;
                        }
                        if (error instanceof TLRPC.TL_secureValueErrorData) {
                            TLRPC.TL_secureValueErrorData errorData = (TLRPC.TL_secureValueErrorData) error;
                            return PassportActivity.this.getFieldCost(errorData.field);
                        }
                        return 100;
                    }

                    public int compare(TLRPC.SecureValueError e1, TLRPC.SecureValueError e2) {
                        int val1 = getErrorValue(e1);
                        int val2 = getErrorValue(e2);
                        if (val1 < val2) {
                            return -1;
                        }
                        if (val1 > val2) {
                            return 1;
                        }
                        return 0;
                    }
                });
                int a = 0;
                int size2 = tL_account_authorizationForm.errors.size();
                while (a < size2) {
                    TLRPC.SecureValueError secureValueError = tL_account_authorizationForm.errors.get(a);
                    String field = null;
                    byte[] file_hash = null;
                    if (secureValueError instanceof TLRPC.TL_secureValueErrorFrontSide) {
                        TLRPC.TL_secureValueErrorFrontSide secureValueErrorFrontSide = (TLRPC.TL_secureValueErrorFrontSide) secureValueError;
                        String key = getNameForType(secureValueErrorFrontSide.type);
                        String key2 = secureValueErrorFrontSide.text;
                        file_hash = secureValueErrorFrontSide.file_hash;
                        description = key2;
                        int i = size2;
                        target = "front";
                        target2 = key;
                        size = i;
                    } else if (secureValueError instanceof TLRPC.TL_secureValueErrorReverseSide) {
                        TLRPC.TL_secureValueErrorReverseSide secureValueErrorReverseSide = (TLRPC.TL_secureValueErrorReverseSide) secureValueError;
                        String key3 = getNameForType(secureValueErrorReverseSide.type);
                        size = size2;
                        String description2 = secureValueErrorReverseSide.text;
                        file_hash = secureValueErrorReverseSide.file_hash;
                        target = "reverse";
                        target2 = key3;
                        description = description2;
                    } else {
                        size = size2;
                        if (secureValueError instanceof TLRPC.TL_secureValueErrorSelfie) {
                            TLRPC.TL_secureValueErrorSelfie secureValueErrorSelfie = (TLRPC.TL_secureValueErrorSelfie) secureValueError;
                            String key4 = getNameForType(secureValueErrorSelfie.type);
                            description = secureValueErrorSelfie.text;
                            file_hash = secureValueErrorSelfie.file_hash;
                            target = "selfie";
                            target2 = key4;
                        } else if (secureValueError instanceof TLRPC.TL_secureValueErrorTranslationFile) {
                            TLRPC.TL_secureValueErrorTranslationFile secureValueErrorTranslationFile = (TLRPC.TL_secureValueErrorTranslationFile) secureValueError;
                            String key5 = getNameForType(secureValueErrorTranslationFile.type);
                            description = secureValueErrorTranslationFile.text;
                            file_hash = secureValueErrorTranslationFile.file_hash;
                            target = "translation";
                            target2 = key5;
                        } else if (secureValueError instanceof TLRPC.TL_secureValueErrorTranslationFiles) {
                            TLRPC.TL_secureValueErrorTranslationFiles secureValueErrorTranslationFiles = (TLRPC.TL_secureValueErrorTranslationFiles) secureValueError;
                            String key6 = getNameForType(secureValueErrorTranslationFiles.type);
                            description = secureValueErrorTranslationFiles.text;
                            target = "translation";
                            target2 = key6;
                        } else if (secureValueError instanceof TLRPC.TL_secureValueErrorFile) {
                            TLRPC.TL_secureValueErrorFile secureValueErrorFile = (TLRPC.TL_secureValueErrorFile) secureValueError;
                            String key7 = getNameForType(secureValueErrorFile.type);
                            description = secureValueErrorFile.text;
                            file_hash = secureValueErrorFile.file_hash;
                            target = "files";
                            target2 = key7;
                        } else if (secureValueError instanceof TLRPC.TL_secureValueErrorFiles) {
                            TLRPC.TL_secureValueErrorFiles secureValueErrorFiles = (TLRPC.TL_secureValueErrorFiles) secureValueError;
                            String key8 = getNameForType(secureValueErrorFiles.type);
                            description = secureValueErrorFiles.text;
                            target = "files";
                            target2 = key8;
                        } else if (!(secureValueError instanceof TLRPC.TL_secureValueError)) {
                            if (secureValueError instanceof TLRPC.TL_secureValueErrorData) {
                                TLRPC.TL_secureValueErrorData secureValueErrorData = (TLRPC.TL_secureValueErrorData) secureValueError;
                                boolean found2 = false;
                                int b = 0;
                                while (true) {
                                    boolean found3 = found2;
                                    if (b < tL_account_authorizationForm.values.size()) {
                                        TLRPC.TL_secureValue value = tL_account_authorizationForm.values.get(b);
                                        TLRPC.SecureValueError secureValueError2 = secureValueError;
                                        found = (value.data != null && Arrays.equals(value.data.data_hash, secureValueErrorData.data_hash)) ? true : found;
                                        b++;
                                        found2 = found3;
                                        secureValueError = secureValueError2;
                                    } else {
                                        found = found3;
                                    }
                                }
                                if (found) {
                                    String key9 = getNameForType(secureValueErrorData.type);
                                    description = secureValueErrorData.text;
                                    field = secureValueErrorData.field;
                                    file_hash = secureValueErrorData.data_hash;
                                    target = "data";
                                    target2 = key9;
                                }
                            }
                            a++;
                            tL_account_authorizationForm = form;
                            size2 = size;
                        } else {
                            TLRPC.TL_secureValueError secureValueErrorAll = (TLRPC.TL_secureValueError) secureValueError;
                            String key10 = getNameForType(secureValueErrorAll.type);
                            description = secureValueErrorAll.text;
                            file_hash = secureValueErrorAll.hash;
                            target = "error_all";
                            target2 = key10;
                        }
                    }
                    HashMap<String, String> vals = this.errorsMap.get(target2);
                    if (vals == null) {
                        vals = new HashMap<>();
                        this.errorsMap.put(target2, vals);
                        this.mainErrorsMap.put(target2, description);
                    }
                    if (file_hash != null) {
                        hash = Base64.encodeToString(file_hash, 2);
                    } else {
                        hash = "";
                    }
                    switch (target.hashCode()) {
                        case -1840647503:
                            if (target.equals("translation")) {
                                c = 3;
                                break;
                            }
                            c = 65535;
                            break;
                        case -906020504:
                            if (target.equals("selfie")) {
                                c = 2;
                                break;
                            }
                            c = 65535;
                            break;
                        case 3076010:
                            if (target.equals("data")) {
                                c = 0;
                                break;
                            }
                            c = 65535;
                            break;
                        case 97434231:
                            if (target.equals("files")) {
                                c = 1;
                                break;
                            }
                            c = 65535;
                            break;
                        case 97705513:
                            if (target.equals("front")) {
                                c = 4;
                                break;
                            }
                            c = 65535;
                            break;
                        case 329856746:
                            if (target.equals("error_all")) {
                                c = 6;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1099846370:
                            if (target.equals("reverse")) {
                                c = 5;
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
                            if (field != null) {
                                vals.put(field, description);
                                break;
                            } else {
                                continue;
                            }
                        case 1:
                            if (file_hash != null) {
                                vals.put("files" + hash, description);
                                continue;
                            } else {
                                vals.put("files_all", description);
                                break;
                            }
                        case 2:
                            vals.put("selfie" + hash, description);
                            continue;
                        case 3:
                            if (file_hash != null) {
                                vals.put("translation" + hash, description);
                                continue;
                            } else {
                                vals.put("translation_all", description);
                                break;
                            }
                        case 4:
                            vals.put("front" + hash, description);
                            continue;
                        case 5:
                            vals.put("reverse" + hash, description);
                            continue;
                        case 6:
                            vals.put("error_all", description);
                            continue;
                        default:
                            continue;
                    }
                    a++;
                    tL_account_authorizationForm = form;
                    size2 = size;
                }
            } catch (Exception e) {
            }
        }
    }

    public PassportActivity(int type, TLRPC.TL_account_authorizationForm form, TLRPC.TL_account_password accountPassword, TLRPC.TL_secureRequiredType secureType, TLRPC.TL_secureValue secureValue, TLRPC.TL_secureRequiredType secureDocumentsType, TLRPC.TL_secureValue secureDocumentsValue, HashMap<String, String> values, HashMap<String, String> documentValues) {
        this.currentCitizeship = "";
        this.currentResidence = "";
        this.currentExpireDate = new int[3];
        this.dividers = new ArrayList<>();
        this.nonLatinNames = new boolean[3];
        this.allowNonLatinName = true;
        this.countriesArray = new ArrayList<>();
        this.countriesMap = new HashMap<>();
        this.codesMap = new HashMap<>();
        this.phoneFormatMap = new HashMap<>();
        this.documents = new ArrayList<>();
        this.translationDocuments = new ArrayList<>();
        this.documentsCells = new HashMap<>();
        this.uploadingDocuments = new HashMap<>();
        this.typesValues = new HashMap<>();
        this.typesViews = new HashMap<>();
        this.documentsToTypesLink = new HashMap<>();
        this.errorsMap = new HashMap<>();
        this.mainErrorsMap = new HashMap<>();
        this.errorsValues = new HashMap<>();
        this.provider = new PhotoViewer.EmptyPhotoViewerProvider() { // from class: org.telegram.ui.PassportActivity.1
            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
                if (index >= 0 && index < PassportActivity.this.currentPhotoViewerLayout.getChildCount()) {
                    SecureDocumentCell cell = (SecureDocumentCell) PassportActivity.this.currentPhotoViewerLayout.getChildAt(index);
                    int[] coords = new int[2];
                    cell.imageView.getLocationInWindow(coords);
                    PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
                    int i = 0;
                    object.viewX = coords[0];
                    int i2 = coords[1];
                    if (Build.VERSION.SDK_INT < 21) {
                        i = AndroidUtilities.statusBarHeight;
                    }
                    object.viewY = i2 - i;
                    object.parentView = PassportActivity.this.currentPhotoViewerLayout;
                    object.imageReceiver = cell.imageView.getImageReceiver();
                    object.thumb = object.imageReceiver.getBitmapSafe();
                    return object;
                }
                return null;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public void deleteImageAtIndex(int index) {
                SecureDocument document = PassportActivity.this.uploadingFileType == 1 ? PassportActivity.this.selfieDocument : PassportActivity.this.uploadingFileType == 4 ? (SecureDocument) PassportActivity.this.translationDocuments.get(index) : PassportActivity.this.uploadingFileType == 2 ? PassportActivity.this.frontDocument : PassportActivity.this.uploadingFileType == 3 ? PassportActivity.this.reverseDocument : (SecureDocument) PassportActivity.this.documents.get(index);
                SecureDocumentCell cell = (SecureDocumentCell) PassportActivity.this.documentsCells.remove(document);
                if (cell == null) {
                    return;
                }
                String key = null;
                String hash = PassportActivity.this.getDocumentHash(document);
                if (PassportActivity.this.uploadingFileType == 1) {
                    PassportActivity.this.selfieDocument = null;
                    key = "selfie" + hash;
                } else if (PassportActivity.this.uploadingFileType != 4) {
                    if (PassportActivity.this.uploadingFileType == 2) {
                        PassportActivity.this.frontDocument = null;
                        key = "front" + hash;
                    } else if (PassportActivity.this.uploadingFileType == 3) {
                        PassportActivity.this.reverseDocument = null;
                        key = "reverse" + hash;
                    } else if (PassportActivity.this.uploadingFileType == 0) {
                        key = "files" + hash;
                    }
                } else {
                    key = "translation" + hash;
                }
                if (key != null) {
                    if (PassportActivity.this.documentsErrors != null) {
                        PassportActivity.this.documentsErrors.remove(key);
                    }
                    if (PassportActivity.this.errorsValues != null) {
                        PassportActivity.this.errorsValues.remove(key);
                    }
                }
                PassportActivity passportActivity = PassportActivity.this;
                passportActivity.updateUploadText(passportActivity.uploadingFileType);
                PassportActivity.this.currentPhotoViewerLayout.removeView(cell);
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public String getDeleteMessageString() {
                if (PassportActivity.this.uploadingFileType == 1) {
                    return LocaleController.formatString("PassportDeleteSelfieAlert", R.string.PassportDeleteSelfieAlert, new Object[0]);
                }
                return LocaleController.formatString("PassportDeleteScanAlert", R.string.PassportDeleteScanAlert, new Object[0]);
            }
        };
        this.currentActivityType = type;
        this.currentForm = form;
        this.currentType = secureType;
        if (secureType != null) {
            this.allowNonLatinName = secureType.native_names;
        }
        this.currentTypeValue = secureValue;
        this.currentDocumentsType = secureDocumentsType;
        this.currentDocumentsTypeValue = secureDocumentsValue;
        this.currentPassword = accountPassword;
        this.currentValues = values;
        this.currentDocumentValues = documentValues;
        int i = this.currentActivityType;
        if (i == 3) {
            this.permissionsItems = new ArrayList<>();
        } else if (i == 7) {
            this.views = new SlideView[3];
        }
        if (this.currentValues == null) {
            this.currentValues = new HashMap<>();
        }
        if (this.currentDocumentValues == null) {
            this.currentDocumentValues = new HashMap<>();
        }
        if (type == 5) {
            if (UserConfig.getInstance(this.currentAccount).savedPasswordHash != null && UserConfig.getInstance(this.currentAccount).savedSaltedPassword != null) {
                this.usingSavedPassword = 1;
                this.savedPasswordHash = UserConfig.getInstance(this.currentAccount).savedPasswordHash;
                this.savedSaltedPassword = UserConfig.getInstance(this.currentAccount).savedSaltedPassword;
            }
            TLRPC.TL_account_password tL_account_password = this.currentPassword;
            if (tL_account_password == null) {
                loadPasswordInfo();
            } else {
                TwoStepVerificationActivity.initPasswordNewAlgo(tL_account_password);
                if (this.usingSavedPassword == 1) {
                    onPasswordDone(true);
                }
            }
            if (!SharedConfig.isPassportConfigLoaded()) {
                TLRPC.TL_help_getPassportConfig req = new TLRPC.TL_help_getPassportConfig();
                req.hash = SharedConfig.passportConfigHash;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, PassportActivity$$ExternalSyntheticLambda64.INSTANCE);
            }
        }
    }

    public static /* synthetic */ void lambda$new$0(TLObject response) {
        if (response instanceof TLRPC.TL_help_passportConfig) {
            TLRPC.TL_help_passportConfig res = (TLRPC.TL_help_passportConfig) response;
            SharedConfig.setPassportConfig(res.countries_langs.data, res.hash);
            return;
        }
        SharedConfig.getCountryLangs();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        ViewGroup[] viewGroupArr;
        super.onResume();
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            chatAttachAlert.onResume();
        }
        if (this.currentActivityType == 5 && (viewGroupArr = this.inputFieldContainers) != null && viewGroupArr[0] != null && viewGroupArr[0].getVisibility() == 0) {
            this.inputFields[0].requestFocus();
            AndroidUtilities.showKeyboard(this.inputFields[0]);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda45
                @Override // java.lang.Runnable
                public final void run() {
                    PassportActivity.this.m4048lambda$onResume$2$orgtelegramuiPassportActivity();
                }
            }, 200L);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    /* renamed from: lambda$onResume$2$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4048lambda$onResume$2$orgtelegramuiPassportActivity() {
        ViewGroup[] viewGroupArr = this.inputFieldContainers;
        if (viewGroupArr != null && viewGroupArr[0] != null && viewGroupArr[0].getVisibility() == 0) {
            this.inputFields[0].requestFocus();
            AndroidUtilities.showKeyboard(this.inputFields[0]);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            chatAttachAlert.onPause();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploadFailed);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.twoStepPasswordChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didRemoveTwoStepPassword);
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadFailed);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.twoStepPasswordChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didRemoveTwoStepPassword);
        callCallback(false);
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            chatAttachAlert.dismissInternal();
            this.chatAttachAlert.onDestroy();
        }
        if (this.currentActivityType == 7) {
            int a = 0;
            while (true) {
                SlideView[] slideViewArr = this.views;
                if (a >= slideViewArr.length) {
                    break;
                }
                if (slideViewArr[a] != null) {
                    slideViewArr[a].onDestroyActivity();
                }
                a++;
            }
            AlertDialog alertDialog = this.progressDialog;
            if (alertDialog != null) {
                try {
                    alertDialog.dismiss();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                this.progressDialog = null;
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        ChatAttachAlert chatAttachAlert;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass3());
        if (this.currentActivityType == 7) {
            ScrollView scrollView = new ScrollView(context) { // from class: org.telegram.ui.PassportActivity.4
                @Override // android.widget.ScrollView, android.view.ViewGroup
                protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
                    return false;
                }

                @Override // android.widget.ScrollView, android.view.ViewGroup, android.view.ViewParent
                public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                    if (PassportActivity.this.currentViewNum == 1 || PassportActivity.this.currentViewNum == 2 || PassportActivity.this.currentViewNum == 4) {
                        rectangle.bottom += AndroidUtilities.dp(40.0f);
                    }
                    return super.requestChildRectangleOnScreen(child, rectangle, immediate);
                }

                @Override // android.widget.ScrollView, android.widget.FrameLayout, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    PassportActivity.this.scrollHeight = View.MeasureSpec.getSize(heightMeasureSpec) - AndroidUtilities.dp(30.0f);
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }
            };
            this.scrollView = scrollView;
            this.fragmentView = scrollView;
            this.scrollView.setFillViewport(true);
            AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_actionBarDefault));
        } else {
            this.fragmentView = new FrameLayout(context);
            FrameLayout frameLayout = (FrameLayout) this.fragmentView;
            this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            ScrollView scrollView2 = new ScrollView(context) { // from class: org.telegram.ui.PassportActivity.5
                @Override // android.widget.ScrollView, android.view.ViewGroup
                protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
                    return false;
                }

                @Override // android.widget.ScrollView, android.view.ViewGroup, android.view.ViewParent
                public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                    rectangle.offset(child.getLeft() - child.getScrollX(), child.getTop() - child.getScrollY());
                    rectangle.top += AndroidUtilities.dp(20.0f);
                    rectangle.bottom += AndroidUtilities.dp(50.0f);
                    return super.requestChildRectangleOnScreen(child, rectangle, immediate);
                }
            };
            this.scrollView = scrollView2;
            scrollView2.setFillViewport(true);
            AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_actionBarDefault));
            frameLayout.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, this.currentActivityType == 0 ? 48.0f : 0.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            this.linearLayout2 = linearLayout;
            linearLayout.setOrientation(1);
            this.scrollView.addView(this.linearLayout2, new FrameLayout.LayoutParams(-1, -2));
        }
        int i = this.currentActivityType;
        if (i != 0 && i != 8) {
            ActionBarMenu menu = this.actionBar.createMenu();
            this.doneItem = menu.addItemWithWidth(2, R.drawable.ic_ab_done, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", R.string.Done));
            ContextProgressView contextProgressView = new ContextProgressView(context, 1);
            this.progressView = contextProgressView;
            contextProgressView.setAlpha(0.0f);
            this.progressView.setScaleX(0.1f);
            this.progressView.setScaleY(0.1f);
            this.progressView.setVisibility(4);
            this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
            int i2 = this.currentActivityType;
            if ((i2 == 1 || i2 == 2) && (chatAttachAlert = this.chatAttachAlert) != null) {
                try {
                    if (chatAttachAlert.isShowing()) {
                        this.chatAttachAlert.dismiss();
                    }
                } catch (Exception e) {
                }
                this.chatAttachAlert.onDestroy();
                this.chatAttachAlert = null;
            }
        }
        int i3 = this.currentActivityType;
        if (i3 == 5) {
            createPasswordInterface(context);
        } else if (i3 == 0) {
            createRequestInterface(context);
        } else if (i3 == 1) {
            createIdentityInterface(context);
            fillInitialValues();
        } else if (i3 == 2) {
            createAddressInterface(context);
            fillInitialValues();
        } else if (i3 == 3) {
            createPhoneInterface(context);
        } else if (i3 == 4) {
            createEmailInterface(context);
        } else if (i3 == 6) {
            createEmailVerificationInterface(context);
        } else if (i3 == 7) {
            createPhoneVerificationInterface(context);
        } else if (i3 == 8) {
            createManageInterface(context);
        }
        return this.fragmentView;
    }

    /* renamed from: org.telegram.ui.PassportActivity$3 */
    /* loaded from: classes4.dex */
    public class AnonymousClass3 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass3() {
            PassportActivity.this = this$0;
        }

        private boolean onIdentityDone(final Runnable finishRunnable, final ErrorRunnable errorRunnable) {
            String str;
            String str2;
            String str3;
            char c = 0;
            if (PassportActivity.this.uploadingDocuments.isEmpty() && !PassportActivity.this.checkFieldsForError()) {
                int i = 3;
                char c2 = 2;
                char c3 = 1;
                if (PassportActivity.this.allowNonLatinName) {
                    PassportActivity.this.allowNonLatinName = false;
                    boolean error = false;
                    int a = 0;
                    while (a < PassportActivity.this.nonLatinNames.length) {
                        if (PassportActivity.this.nonLatinNames[a]) {
                            PassportActivity.this.inputFields[a].setErrorText(LocaleController.getString("PassportUseLatinOnly", R.string.PassportUseLatinOnly));
                            if (!error) {
                                error = true;
                                if (!PassportActivity.this.nonLatinNames[c]) {
                                    str = PassportActivity.this.inputFields[c].getText().toString();
                                } else {
                                    PassportActivity passportActivity = PassportActivity.this;
                                    str = passportActivity.getTranslitString(passportActivity.inputExtraFields[c].getText().toString());
                                }
                                final String firstName = str;
                                if (!PassportActivity.this.nonLatinNames[c3]) {
                                    str2 = PassportActivity.this.inputFields[c3].getText().toString();
                                } else {
                                    PassportActivity passportActivity2 = PassportActivity.this;
                                    str2 = passportActivity2.getTranslitString(passportActivity2.inputExtraFields[c3].getText().toString());
                                }
                                final String middleName = str2;
                                if (!PassportActivity.this.nonLatinNames[c2]) {
                                    str3 = PassportActivity.this.inputFields[c2].getText().toString();
                                } else {
                                    PassportActivity passportActivity3 = PassportActivity.this;
                                    str3 = passportActivity3.getTranslitString(passportActivity3.inputExtraFields[c2].getText().toString());
                                }
                                final String lastName = str3;
                                if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(middleName) && !TextUtils.isEmpty(lastName)) {
                                    final int num = a;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PassportActivity.this.getParentActivity());
                                    Object[] objArr = new Object[i];
                                    objArr[c] = firstName;
                                    objArr[c3] = middleName;
                                    objArr[c2] = lastName;
                                    builder.setMessage(LocaleController.formatString("PassportNameCheckAlert", R.string.PassportNameCheckAlert, objArr));
                                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                    builder.setPositiveButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PassportActivity$3$$ExternalSyntheticLambda1
                                        @Override // android.content.DialogInterface.OnClickListener
                                        public final void onClick(DialogInterface dialogInterface, int i2) {
                                            PassportActivity.AnonymousClass3.this.m4060lambda$onIdentityDone$0$orgtelegramuiPassportActivity$3(firstName, middleName, lastName, finishRunnable, errorRunnable, dialogInterface, i2);
                                        }
                                    });
                                    builder.setNegativeButton(LocaleController.getString("Edit", R.string.Edit), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PassportActivity$3$$ExternalSyntheticLambda0
                                        @Override // android.content.DialogInterface.OnClickListener
                                        public final void onClick(DialogInterface dialogInterface, int i2) {
                                            PassportActivity.AnonymousClass3.this.m4061lambda$onIdentityDone$1$orgtelegramuiPassportActivity$3(num, dialogInterface, i2);
                                        }
                                    });
                                    PassportActivity.this.showDialog(builder.create());
                                } else {
                                    PassportActivity passportActivity4 = PassportActivity.this;
                                    passportActivity4.onFieldError(passportActivity4.inputFields[a]);
                                }
                            }
                        }
                        a++;
                        c = 0;
                        i = 3;
                        c2 = 2;
                        c3 = 1;
                    }
                    if (error) {
                        return false;
                    }
                }
                if (PassportActivity.this.isHasNotAnyChanges()) {
                    PassportActivity.this.finishFragment();
                    return false;
                }
                JSONObject json = null;
                JSONObject documentsJson = null;
                try {
                    if (!PassportActivity.this.documentOnly) {
                        HashMap<String, String> valuesToSave = new HashMap<>(PassportActivity.this.currentValues);
                        if (PassportActivity.this.currentType.native_names) {
                            if (PassportActivity.this.nativeInfoCell.getVisibility() == 0) {
                                valuesToSave.put("first_name_native", PassportActivity.this.inputExtraFields[0].getText().toString());
                                valuesToSave.put("middle_name_native", PassportActivity.this.inputExtraFields[1].getText().toString());
                                valuesToSave.put("last_name_native", PassportActivity.this.inputExtraFields[2].getText().toString());
                            } else {
                                valuesToSave.put("first_name_native", PassportActivity.this.inputFields[0].getText().toString());
                                valuesToSave.put("middle_name_native", PassportActivity.this.inputFields[1].getText().toString());
                                valuesToSave.put("last_name_native", PassportActivity.this.inputFields[2].getText().toString());
                            }
                        }
                        valuesToSave.put("first_name", PassportActivity.this.inputFields[0].getText().toString());
                        valuesToSave.put("middle_name", PassportActivity.this.inputFields[1].getText().toString());
                        valuesToSave.put("last_name", PassportActivity.this.inputFields[2].getText().toString());
                        valuesToSave.put("birth_date", PassportActivity.this.inputFields[3].getText().toString());
                        valuesToSave.put("gender", PassportActivity.this.currentGender);
                        valuesToSave.put("country_code", PassportActivity.this.currentCitizeship);
                        valuesToSave.put("residence_country_code", PassportActivity.this.currentResidence);
                        json = new JSONObject();
                        ArrayList<String> keys = new ArrayList<>(valuesToSave.keySet());
                        Collections.sort(keys, new Comparator() { // from class: org.telegram.ui.PassportActivity$3$$ExternalSyntheticLambda4
                            @Override // java.util.Comparator
                            public final int compare(Object obj, Object obj2) {
                                return PassportActivity.AnonymousClass3.this.m4062lambda$onIdentityDone$2$orgtelegramuiPassportActivity$3((String) obj, (String) obj2);
                            }
                        });
                        int size = keys.size();
                        for (int a2 = 0; a2 < size; a2++) {
                            String key = keys.get(a2);
                            json.put(key, valuesToSave.get(key));
                        }
                    }
                    if (PassportActivity.this.currentDocumentsType != null) {
                        HashMap<String, String> valuesToSave2 = new HashMap<>(PassportActivity.this.currentDocumentValues);
                        valuesToSave2.put("document_no", PassportActivity.this.inputFields[7].getText().toString());
                        if (PassportActivity.this.currentExpireDate[0] != 0) {
                            valuesToSave2.put("expiry_date", String.format(Locale.US, "%02d.%02d.%d", Integer.valueOf(PassportActivity.this.currentExpireDate[2]), Integer.valueOf(PassportActivity.this.currentExpireDate[1]), Integer.valueOf(PassportActivity.this.currentExpireDate[0])));
                        } else {
                            valuesToSave2.put("expiry_date", "");
                        }
                        documentsJson = new JSONObject();
                        ArrayList<String> keys2 = new ArrayList<>(valuesToSave2.keySet());
                        Collections.sort(keys2, new Comparator() { // from class: org.telegram.ui.PassportActivity$3$$ExternalSyntheticLambda5
                            @Override // java.util.Comparator
                            public final int compare(Object obj, Object obj2) {
                                return PassportActivity.AnonymousClass3.this.m4063lambda$onIdentityDone$3$orgtelegramuiPassportActivity$3((String) obj, (String) obj2);
                            }
                        });
                        int size2 = keys2.size();
                        for (int a3 = 0; a3 < size2; a3++) {
                            String key2 = keys2.get(a3);
                            documentsJson.put(key2, valuesToSave2.get(key2));
                        }
                    }
                } catch (Exception e) {
                }
                if (PassportActivity.this.fieldsErrors != null) {
                    PassportActivity.this.fieldsErrors.clear();
                }
                if (PassportActivity.this.documentsErrors != null) {
                    PassportActivity.this.documentsErrors.clear();
                }
                PassportActivityDelegate passportActivityDelegate = PassportActivity.this.delegate;
                TLRPC.TL_secureRequiredType tL_secureRequiredType = PassportActivity.this.currentType;
                SecureDocument secureDocument = null;
                String jSONObject = json != null ? json.toString() : null;
                TLRPC.TL_secureRequiredType tL_secureRequiredType2 = PassportActivity.this.currentDocumentsType;
                String jSONObject2 = documentsJson != null ? documentsJson.toString() : null;
                SecureDocument secureDocument2 = PassportActivity.this.selfieDocument;
                ArrayList<SecureDocument> arrayList = PassportActivity.this.translationDocuments;
                SecureDocument secureDocument3 = PassportActivity.this.frontDocument;
                if (PassportActivity.this.reverseLayout != null && PassportActivity.this.reverseLayout.getVisibility() == 0) {
                    secureDocument = PassportActivity.this.reverseDocument;
                }
                passportActivityDelegate.saveValue(tL_secureRequiredType, null, jSONObject, tL_secureRequiredType2, jSONObject2, null, secureDocument2, arrayList, secureDocument3, secureDocument, finishRunnable, errorRunnable);
                return true;
            }
            return false;
        }

        /* renamed from: lambda$onIdentityDone$0$org-telegram-ui-PassportActivity$3 */
        public /* synthetic */ void m4060lambda$onIdentityDone$0$orgtelegramuiPassportActivity$3(String firstName, String middleName, String lastName, Runnable finishRunnable, ErrorRunnable errorRunnable, DialogInterface dialogInterface, int i) {
            PassportActivity.this.inputFields[0].setText(firstName);
            PassportActivity.this.inputFields[1].setText(middleName);
            PassportActivity.this.inputFields[2].setText(lastName);
            PassportActivity.this.showEditDoneProgress(true, true);
            onIdentityDone(finishRunnable, errorRunnable);
        }

        /* renamed from: lambda$onIdentityDone$1$org-telegram-ui-PassportActivity$3 */
        public /* synthetic */ void m4061lambda$onIdentityDone$1$orgtelegramuiPassportActivity$3(int num, DialogInterface dialogInterface, int i) {
            PassportActivity passportActivity = PassportActivity.this;
            passportActivity.onFieldError(passportActivity.inputFields[num]);
        }

        /* renamed from: lambda$onIdentityDone$2$org-telegram-ui-PassportActivity$3 */
        public /* synthetic */ int m4062lambda$onIdentityDone$2$orgtelegramuiPassportActivity$3(String key1, String key2) {
            int val1 = PassportActivity.this.getFieldCost(key1);
            int val2 = PassportActivity.this.getFieldCost(key2);
            if (val1 < val2) {
                return -1;
            }
            if (val1 > val2) {
                return 1;
            }
            return 0;
        }

        /* renamed from: lambda$onIdentityDone$3$org-telegram-ui-PassportActivity$3 */
        public /* synthetic */ int m4063lambda$onIdentityDone$3$orgtelegramuiPassportActivity$3(String key1, String key2) {
            int val1 = PassportActivity.this.getFieldCost(key1);
            int val2 = PassportActivity.this.getFieldCost(key2);
            if (val1 < val2) {
                return -1;
            }
            if (val1 > val2) {
                return 1;
            }
            return 0;
        }

        @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
        public void onItemClick(int id) {
            JSONObject json;
            String value;
            String value2;
            if (id == -1) {
                if (!PassportActivity.this.checkDiscard()) {
                    if (PassportActivity.this.currentActivityType == 0 || PassportActivity.this.currentActivityType == 5) {
                        PassportActivity.this.callCallback(false);
                    }
                    PassportActivity.this.finishFragment();
                    return;
                }
                return;
            }
            String str = null;
            if (id == 1) {
                if (PassportActivity.this.getParentActivity() == null) {
                    return;
                }
                TextView message = new TextView(PassportActivity.this.getParentActivity());
                String str2 = LocaleController.getString("PassportInfo2", R.string.PassportInfo2);
                SpannableStringBuilder spanned = new SpannableStringBuilder(str2);
                int index1 = str2.indexOf(42);
                int index2 = str2.lastIndexOf(42);
                if (index1 != -1 && index2 != -1) {
                    spanned.replace(index2, index2 + 1, (CharSequence) "");
                    spanned.replace(index1, index1 + 1, (CharSequence) "");
                    spanned.setSpan(new URLSpanNoUnderline(LocaleController.getString("PassportInfoUrl", R.string.PassportInfoUrl)) { // from class: org.telegram.ui.PassportActivity.3.1
                        @Override // org.telegram.ui.Components.URLSpanNoUnderline, android.text.style.URLSpan, android.text.style.ClickableSpan
                        public void onClick(View widget) {
                            PassportActivity.this.dismissCurrentDialog();
                            super.onClick(widget);
                        }
                    }, index1, index2 - 1, 33);
                }
                message.setText(spanned);
                message.setTextSize(1, 16.0f);
                message.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink));
                message.setHighlightColor(Theme.getColor(Theme.key_dialogLinkSelection));
                message.setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
                message.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                message.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                AlertDialog.Builder builder = new AlertDialog.Builder(PassportActivity.this.getParentActivity());
                builder.setView(message);
                builder.setTitle(LocaleController.getString("PassportInfoTitle", R.string.PassportInfoTitle));
                builder.setNegativeButton(LocaleController.getString("Close", R.string.Close), null);
                PassportActivity.this.showDialog(builder.create());
            } else if (id == 2) {
                if (PassportActivity.this.currentActivityType == 5) {
                    PassportActivity.this.onPasswordDone(false);
                } else if (PassportActivity.this.currentActivityType != 7) {
                    final Runnable finishRunnable = new Runnable() { // from class: org.telegram.ui.PassportActivity$3$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            PassportActivity.AnonymousClass3.this.m4064lambda$onItemClick$4$orgtelegramuiPassportActivity$3();
                        }
                    };
                    final ErrorRunnable errorRunnable = new ErrorRunnable() { // from class: org.telegram.ui.PassportActivity.3.2
                        @Override // org.telegram.ui.PassportActivity.ErrorRunnable
                        public void onError(String error, String text) {
                            if ("PHONE_VERIFICATION_NEEDED".equals(error)) {
                                PassportActivity.this.startPhoneVerification(true, text, finishRunnable, this, PassportActivity.this.delegate);
                            } else {
                                PassportActivity.this.showEditDoneProgress(true, false);
                            }
                        }
                    };
                    if (PassportActivity.this.currentActivityType == 4) {
                        if (PassportActivity.this.useCurrentValue) {
                            value2 = PassportActivity.this.currentEmail;
                        } else if (!PassportActivity.this.checkFieldsForError()) {
                            value2 = PassportActivity.this.inputFields[0].getText().toString();
                        } else {
                            return;
                        }
                        PassportActivity.this.delegate.saveValue(PassportActivity.this.currentType, value2, null, null, null, null, null, null, null, null, finishRunnable, errorRunnable);
                    } else if (PassportActivity.this.currentActivityType == 3) {
                        if (PassportActivity.this.useCurrentValue) {
                            value = UserConfig.getInstance(PassportActivity.this.currentAccount).getCurrentUser().phone;
                        } else if (PassportActivity.this.checkFieldsForError()) {
                            return;
                        } else {
                            value = PassportActivity.this.inputFields[1].getText().toString() + PassportActivity.this.inputFields[2].getText().toString();
                        }
                        PassportActivity.this.delegate.saveValue(PassportActivity.this.currentType, value, null, null, null, null, null, null, null, null, finishRunnable, errorRunnable);
                    } else if (PassportActivity.this.currentActivityType == 2) {
                        if (PassportActivity.this.uploadingDocuments.isEmpty() && !PassportActivity.this.checkFieldsForError()) {
                            if (PassportActivity.this.isHasNotAnyChanges()) {
                                PassportActivity.this.finishFragment();
                                return;
                            }
                            JSONObject json2 = null;
                            try {
                                if (!PassportActivity.this.documentOnly) {
                                    json2 = new JSONObject();
                                    json2.put("street_line1", PassportActivity.this.inputFields[0].getText().toString());
                                    json2.put("street_line2", PassportActivity.this.inputFields[1].getText().toString());
                                    json2.put("post_code", PassportActivity.this.inputFields[2].getText().toString());
                                    json2.put("city", PassportActivity.this.inputFields[3].getText().toString());
                                    json2.put(RemoteConfigConstants.ResponseFieldKey.STATE, PassportActivity.this.inputFields[4].getText().toString());
                                    json2.put("country_code", PassportActivity.this.currentCitizeship);
                                }
                                json = json2;
                            } catch (Exception e) {
                                json = json2;
                            }
                            if (PassportActivity.this.fieldsErrors != null) {
                                PassportActivity.this.fieldsErrors.clear();
                            }
                            if (PassportActivity.this.documentsErrors != null) {
                                PassportActivity.this.documentsErrors.clear();
                            }
                            PassportActivityDelegate passportActivityDelegate = PassportActivity.this.delegate;
                            TLRPC.TL_secureRequiredType tL_secureRequiredType = PassportActivity.this.currentType;
                            if (json != null) {
                                str = json.toString();
                            }
                            passportActivityDelegate.saveValue(tL_secureRequiredType, null, str, PassportActivity.this.currentDocumentsType, null, PassportActivity.this.documents, PassportActivity.this.selfieDocument, PassportActivity.this.translationDocuments, null, null, finishRunnable, errorRunnable);
                        }
                        return;
                    } else if (PassportActivity.this.currentActivityType != 1) {
                        if (PassportActivity.this.currentActivityType == 6) {
                            final TLRPC.TL_account_verifyEmail req = new TLRPC.TL_account_verifyEmail();
                            req.email = (String) PassportActivity.this.currentValues.get("email");
                            req.code = PassportActivity.this.inputFields[0].getText().toString();
                            int reqId = ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PassportActivity$3$$ExternalSyntheticLambda6
                                @Override // org.telegram.tgnet.RequestDelegate
                                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                    PassportActivity.AnonymousClass3.this.m4066lambda$onItemClick$6$orgtelegramuiPassportActivity$3(finishRunnable, errorRunnable, req, tLObject, tL_error);
                                }
                            });
                            ConnectionsManager.getInstance(PassportActivity.this.currentAccount).bindRequestToGuid(reqId, PassportActivity.this.classGuid);
                        }
                    } else if (!onIdentityDone(finishRunnable, errorRunnable)) {
                        return;
                    }
                    PassportActivity.this.showEditDoneProgress(true, true);
                } else {
                    PassportActivity.this.views[PassportActivity.this.currentViewNum].onNextPressed(null);
                }
            }
        }

        /* renamed from: lambda$onItemClick$4$org-telegram-ui-PassportActivity$3 */
        public /* synthetic */ void m4064lambda$onItemClick$4$orgtelegramuiPassportActivity$3() {
            PassportActivity.this.finishFragment();
        }

        /* renamed from: lambda$onItemClick$6$org-telegram-ui-PassportActivity$3 */
        public /* synthetic */ void m4066lambda$onItemClick$6$orgtelegramuiPassportActivity$3(final Runnable finishRunnable, final ErrorRunnable errorRunnable, final TLRPC.TL_account_verifyEmail req, TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$3$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    PassportActivity.AnonymousClass3.this.m4065lambda$onItemClick$5$orgtelegramuiPassportActivity$3(error, finishRunnable, errorRunnable, req);
                }
            });
        }

        /* renamed from: lambda$onItemClick$5$org-telegram-ui-PassportActivity$3 */
        public /* synthetic */ void m4065lambda$onItemClick$5$orgtelegramuiPassportActivity$3(TLRPC.TL_error error, Runnable finishRunnable, ErrorRunnable errorRunnable, TLRPC.TL_account_verifyEmail req) {
            if (error == null) {
                PassportActivity.this.delegate.saveValue(PassportActivity.this.currentType, (String) PassportActivity.this.currentValues.get("email"), null, null, null, null, null, null, null, null, finishRunnable, errorRunnable);
                return;
            }
            AlertsCreator.processError(PassportActivity.this.currentAccount, error, PassportActivity.this, req, new Object[0]);
            errorRunnable.onError(null, null);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean dismissDialogOnPause(Dialog dialog) {
        return dialog != this.chatAttachAlert && super.dismissDialogOnPause(dialog);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void dismissCurrentDialog() {
        if (this.chatAttachAlert != null) {
            Dialog dialog = this.visibleDialog;
            ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
            if (dialog == chatAttachAlert) {
                chatAttachAlert.getPhotoLayout().closeCamera(false);
                this.chatAttachAlert.dismissInternal();
                this.chatAttachAlert.getPhotoLayout().hideCamera(true);
                return;
            }
        }
        super.dismissCurrentDialog();
    }

    public String getTranslitString(String value) {
        return LocaleController.getInstance().getTranslitString(value, true);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public int getFieldCost(String key) {
        char c;
        switch (key.hashCode()) {
            case -2006252145:
                if (key.equals("residence_country_code")) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case -1537298398:
                if (key.equals("last_name_native")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -1249512767:
                if (key.equals("gender")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -796150911:
                if (key.equals("street_line1")) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case -796150910:
                if (key.equals("street_line2")) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case -160985414:
                if (key.equals("first_name")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 3053931:
                if (key.equals("city")) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case 109757585:
                if (key.equals(RemoteConfigConstants.ResponseFieldKey.STATE)) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case 421072629:
                if (key.equals("middle_name")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 451516732:
                if (key.equals("first_name_native")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 475919162:
                if (key.equals("expiry_date")) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case 506677093:
                if (key.equals("document_no")) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case 1168724782:
                if (key.equals("birth_date")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 1181577377:
                if (key.equals("middle_name_native")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1481071862:
                if (key.equals("country_code")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 2002465324:
                if (key.equals("post_code")) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case 2013122196:
                if (key.equals("last_name")) {
                    c = 4;
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
            case 1:
                return 20;
            case 2:
            case 3:
                return 21;
            case 4:
            case 5:
                return 22;
            case 6:
                return 23;
            case 7:
                return 24;
            case '\b':
                return 25;
            case '\t':
                return 26;
            case '\n':
                return 27;
            case 11:
                return 28;
            case '\f':
                return 29;
            case '\r':
                return 30;
            case 14:
                return 31;
            case 15:
                return 32;
            case 16:
                return 33;
            default:
                return 100;
        }
    }

    private void createPhoneVerificationInterface(Context context) {
        this.actionBar.setTitle(LocaleController.getString("PassportPhone", R.string.PassportPhone));
        FrameLayout frameLayout = new FrameLayout(context);
        this.scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        for (int a = 0; a < 3; a++) {
            this.views[a] = new PhoneConfirmationView(context, a + 2);
            this.views[a].setVisibility(8);
            SlideView slideView = this.views[a];
            float f = 18.0f;
            float f2 = AndroidUtilities.isTablet() ? 26.0f : 18.0f;
            if (AndroidUtilities.isTablet()) {
                f = 26.0f;
            }
            frameLayout.addView(slideView, LayoutHelper.createFrame(-1, -1.0f, 51, f2, 30.0f, f, 0.0f));
        }
        Bundle params = new Bundle();
        params.putString("phone", this.currentValues.get("phone"));
        fillNextCodeParams(params, this.currentPhoneVerification, false);
    }

    private void loadPasswordInfo() {
        TLRPC.TL_account_getPassword req = new TLRPC.TL_account_getPassword();
        int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda61
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PassportActivity.this.m4045lambda$loadPasswordInfo$4$orgtelegramuiPassportActivity(tLObject, tL_error);
            }
        });
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, this.classGuid);
    }

    /* renamed from: lambda$loadPasswordInfo$4$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4045lambda$loadPasswordInfo$4$orgtelegramuiPassportActivity(final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda51
            @Override // java.lang.Runnable
            public final void run() {
                PassportActivity.this.m4044lambda$loadPasswordInfo$3$orgtelegramuiPassportActivity(response);
            }
        });
    }

    /* renamed from: lambda$loadPasswordInfo$3$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4044lambda$loadPasswordInfo$3$orgtelegramuiPassportActivity(TLObject response) {
        if (response != null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response;
            this.currentPassword = tL_account_password;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(tL_account_password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                return;
            }
            TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
            updatePasswordInterface();
            if (this.inputFieldContainers[0].getVisibility() == 0) {
                this.inputFields[0].requestFocus();
                AndroidUtilities.showKeyboard(this.inputFields[0]);
            }
            if (this.usingSavedPassword == 1) {
                onPasswordDone(true);
            }
        }
    }

    private void createEmailVerificationInterface(Context context) {
        this.actionBar.setTitle(LocaleController.getString("PassportEmail", R.string.PassportEmail));
        this.inputFields = new EditTextBoldCursor[1];
        for (int a = 0; a < 1; a++) {
            ViewGroup container = new FrameLayout(context);
            this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 50));
            container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.inputFields[a] = new EditTextBoldCursor(context);
            this.inputFields[a].setTag(Integer.valueOf(a));
            this.inputFields[a].setTextSize(1, 16.0f);
            this.inputFields[a].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setBackgroundDrawable(null);
            this.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a].setCursorWidth(1.5f);
            int i = 3;
            this.inputFields[a].setInputType(3);
            this.inputFields[a].setImeOptions(268435462);
            this.inputFields[a].setHint(LocaleController.getString("PassportEmailCode", R.string.PassportEmailCode));
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[a].setSelection(editTextBoldCursorArr[a].length());
            this.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            EditTextBoldCursor editTextBoldCursor = this.inputFields[a];
            if (LocaleController.isRTL) {
                i = 5;
            }
            editTextBoldCursor.setGravity(i);
            container.addView(this.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 21.0f, 12.0f, 21.0f, 6.0f));
            this.inputFields[a].setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda35
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView, int i2, KeyEvent keyEvent) {
                    return PassportActivity.this.m4004x5e37344f(textView, i2, keyEvent);
                }
            });
            this.inputFields[a].addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.PassportActivity.6
                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable s) {
                    if (!PassportActivity.this.ignoreOnTextChange && PassportActivity.this.emailCodeLength != 0 && PassportActivity.this.inputFields[0].length() == PassportActivity.this.emailCodeLength) {
                        PassportActivity.this.doneItem.callOnClick();
                    }
                }
            });
        }
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
        this.bottomCell = textInfoPrivacyCell;
        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.bottomCell.setText(LocaleController.formatString("PassportEmailVerifyInfo", R.string.PassportEmailVerifyInfo, this.currentValues.get("email")));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
    }

    /* renamed from: lambda$createEmailVerificationInterface$5$org-telegram-ui-PassportActivity */
    public /* synthetic */ boolean m4004x5e37344f(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 6 || i == 5) {
            this.doneItem.callOnClick();
            return true;
        }
        return false;
    }

    private void createPasswordInterface(Context context) {
        TLRPC.User botUser = null;
        if (this.currentForm != null) {
            int a = 0;
            while (true) {
                if (a >= this.currentForm.users.size()) {
                    break;
                }
                TLRPC.User user = this.currentForm.users.get(a);
                if (user.id != this.currentBotId) {
                    a++;
                } else {
                    botUser = user;
                    break;
                }
            }
        } else {
            botUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.actionBar.setTitle(LocaleController.getString("TelegramPassport", R.string.TelegramPassport));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.passwordAvatarContainer = frameLayout2;
        this.linearLayout2.addView(frameLayout2, LayoutHelper.createLinear(-1, 100));
        BackupImageView avatarImageView = new BackupImageView(context);
        avatarImageView.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.passwordAvatarContainer.addView(avatarImageView, LayoutHelper.createFrame(64, 64.0f, 17, 0.0f, 8.0f, 0.0f, 0.0f));
        AvatarDrawable avatarDrawable = new AvatarDrawable(botUser);
        avatarImageView.setForUserOrChat(botUser, avatarDrawable);
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
        this.passwordRequestTextView = textInfoPrivacyCell;
        textInfoPrivacyCell.getTextView().setGravity(1);
        if (this.currentBotId == 0) {
            this.passwordRequestTextView.setText(LocaleController.getString("PassportSelfRequest", R.string.PassportSelfRequest));
        } else {
            this.passwordRequestTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("PassportRequest", R.string.PassportRequest, UserObject.getFirstName(botUser))));
        }
        ((FrameLayout.LayoutParams) this.passwordRequestTextView.getTextView().getLayoutParams()).gravity = 1;
        int i = 5;
        this.linearLayout2.addView(this.passwordRequestTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 0.0f, 21.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.noPasswordImageView = imageView;
        imageView.setImageResource(R.drawable.no_password);
        this.noPasswordImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
        this.linearLayout2.addView(this.noPasswordImageView, LayoutHelper.createLinear(-2, -2, 49, 0, 13, 0, 0));
        TextView textView = new TextView(context);
        this.noPasswordTextView = textView;
        textView.setTextSize(1, 14.0f);
        this.noPasswordTextView.setGravity(1);
        this.noPasswordTextView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(17.0f));
        this.noPasswordTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        this.noPasswordTextView.setText(LocaleController.getString("TelegramPassportCreatePasswordInfo", R.string.TelegramPassportCreatePasswordInfo));
        this.linearLayout2.addView(this.noPasswordTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 10.0f, 21.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.noPasswordSetTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText5));
        this.noPasswordSetTextView.setGravity(17);
        this.noPasswordSetTextView.setTextSize(1, 16.0f);
        this.noPasswordSetTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.noPasswordSetTextView.setText(LocaleController.getString("TelegramPassportCreatePassword", R.string.TelegramPassportCreatePassword));
        this.linearLayout2.addView(this.noPasswordSetTextView, LayoutHelper.createFrame(-1, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 9.0f, 21.0f, 0.0f));
        this.noPasswordSetTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda18
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PassportActivity.this.m4029x4cb36144(view);
            }
        });
        this.inputFields = new EditTextBoldCursor[1];
        this.inputFieldContainers = new ViewGroup[1];
        for (int a2 = 0; a2 < 1; a2++) {
            this.inputFieldContainers[a2] = new FrameLayout(context);
            this.linearLayout2.addView(this.inputFieldContainers[a2], LayoutHelper.createLinear(-1, 50));
            this.inputFieldContainers[a2].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.inputFields[a2] = new EditTextBoldCursor(context);
            this.inputFields[a2].setTag(Integer.valueOf(a2));
            this.inputFields[a2].setTextSize(1, 16.0f);
            this.inputFields[a2].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.inputFields[a2].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a2].setBackgroundDrawable(null);
            this.inputFields[a2].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a2].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a2].setCursorWidth(1.5f);
            this.inputFields[a2].setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
            this.inputFields[a2].setMaxLines(1);
            this.inputFields[a2].setLines(1);
            this.inputFields[a2].setSingleLine(true);
            this.inputFields[a2].setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.inputFields[a2].setTypeface(Typeface.DEFAULT);
            this.inputFields[a2].setImeOptions(268435462);
            this.inputFields[a2].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            this.inputFields[a2].setGravity(LocaleController.isRTL ? 5 : 3);
            this.inputFieldContainers[a2].addView(this.inputFields[a2], LayoutHelper.createFrame(-1, -2.0f, 51, 21.0f, 12.0f, 21.0f, 6.0f));
            this.inputFields[a2].setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda38
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView3, int i2, KeyEvent keyEvent) {
                    return PassportActivity.this.m4030x89d32563(textView3, i2, keyEvent);
                }
            });
            this.inputFields[a2].setCustomSelectionActionModeCallback(new ActionMode.Callback() { // from class: org.telegram.ui.PassportActivity.7
                @Override // android.view.ActionMode.Callback
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override // android.view.ActionMode.Callback
                public void onDestroyActionMode(ActionMode mode) {
                }

                @Override // android.view.ActionMode.Callback
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override // android.view.ActionMode.Callback
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }
            });
        }
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context);
        this.passwordInfoRequestTextView = textInfoPrivacyCell2;
        textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.passwordInfoRequestTextView.setText(LocaleController.formatString("PassportRequestPasswordInfo", R.string.PassportRequestPasswordInfo, new Object[0]));
        this.linearLayout2.addView(this.passwordInfoRequestTextView, LayoutHelper.createLinear(-1, -2));
        TextView textView3 = new TextView(context);
        this.passwordForgotButton = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
        this.passwordForgotButton.setTextSize(1, 14.0f);
        this.passwordForgotButton.setText(LocaleController.getString("ForgotPassword", R.string.ForgotPassword));
        this.passwordForgotButton.setPadding(0, 0, 0, 0);
        LinearLayout linearLayout = this.linearLayout2;
        TextView textView4 = this.passwordForgotButton;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        linearLayout.addView(textView4, LayoutHelper.createLinear(-2, 30, i | 48, 21, 0, 21, 0));
        this.passwordForgotButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda17
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PassportActivity.this.m4028xf707e95b(view);
            }
        });
        updatePasswordInterface();
    }

    /* renamed from: lambda$createPasswordInterface$6$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4029x4cb36144(View v) {
        TwoStepVerificationSetupActivity activity = new TwoStepVerificationSetupActivity(this.currentAccount, 0, this.currentPassword);
        activity.setCloseAfterSet(true);
        presentFragment(activity);
    }

    /* renamed from: lambda$createPasswordInterface$7$org-telegram-ui-PassportActivity */
    public /* synthetic */ boolean m4030x89d32563(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5 || i == 6) {
            this.doneItem.callOnClick();
            return true;
        }
        return false;
    }

    /* renamed from: lambda$createPasswordInterface$12$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4028xf707e95b(View v) {
        if (this.currentPassword.has_recovery) {
            needShowProgress();
            TLRPC.TL_auth_requestPasswordRecovery req = new TLRPC.TL_auth_requestPasswordRecovery();
            int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda59
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PassportActivity.this.m4026x7cc8611d(tLObject, tL_error);
                }
            }, 10);
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, this.classGuid);
        } else if (getParentActivity() == null) {
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            builder.setNegativeButton(LocaleController.getString("RestorePasswordResetAccount", R.string.RestorePasswordResetAccount), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda33
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PassportActivity.this.m4027xb9e8253c(dialogInterface, i);
                }
            });
            builder.setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", R.string.RestorePasswordNoEmailTitle));
            builder.setMessage(LocaleController.getString("RestorePasswordNoEmailText", R.string.RestorePasswordNoEmailText));
            showDialog(builder.create());
        }
    }

    /* renamed from: lambda$createPasswordInterface$10$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4026x7cc8611d(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda54
            @Override // java.lang.Runnable
            public final void run() {
                PassportActivity.this.m4032x412ada1(error, response);
            }
        });
    }

    /* renamed from: lambda$createPasswordInterface$9$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4032x412ada1(TLRPC.TL_error error, TLObject response) {
        String timeString;
        needHideProgress();
        if (error == null) {
            final TLRPC.TL_auth_passwordRecovery res = (TLRPC.TL_auth_passwordRecovery) response;
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setMessage(LocaleController.formatString("RestoreEmailSent", R.string.RestoreEmailSent, res.email_pattern));
            builder.setTitle(LocaleController.getString("RestoreEmailSentTitle", R.string.RestoreEmailSentTitle));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda71
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PassportActivity.this.m4031xc6f2e982(res, dialogInterface, i);
                }
            });
            Dialog dialog = showDialog(builder.create());
            if (dialog != null) {
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
            }
        } else if (error.text.startsWith("FLOOD_WAIT")) {
            int time = Utilities.parseInt((CharSequence) error.text).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time, new Object[0]);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60, new Object[0]);
            }
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
        } else {
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
        }
    }

    /* renamed from: lambda$createPasswordInterface$8$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4031xc6f2e982(TLRPC.TL_auth_passwordRecovery res, DialogInterface dialogInterface, int i) {
        this.currentPassword.email_unconfirmed_pattern = res.email_pattern;
        TwoStepVerificationSetupActivity fragment = new TwoStepVerificationSetupActivity(this.currentAccount, 4, this.currentPassword);
        presentFragment(fragment);
    }

    /* renamed from: lambda$createPasswordInterface$11$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4027xb9e8253c(DialogInterface dialog, int which) {
        Activity parentActivity = getParentActivity();
        Browser.openUrl(parentActivity, "https://telegram.org/deactivate?phone=" + UserConfig.getInstance(this.currentAccount).getClientPhone());
    }

    public void onPasswordDone(final boolean saved) {
        final String textPassword;
        if (saved) {
            textPassword = null;
        } else {
            textPassword = this.inputFields[0].getText().toString();
            if (TextUtils.isEmpty(textPassword)) {
                onPasscodeError(false);
                return;
            }
            showEditDoneProgress(true, true);
        }
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda57
            @Override // java.lang.Runnable
            public final void run() {
                PassportActivity.this.m4046lambda$onPasswordDone$13$orgtelegramuiPassportActivity(saved, textPassword);
            }
        });
    }

    /* renamed from: lambda$onPasswordDone$13$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4046lambda$onPasswordDone$13$orgtelegramuiPassportActivity(boolean saved, String textPassword) {
        byte[] x_bytes;
        TLRPC.TL_account_getPasswordSettings req = new TLRPC.TL_account_getPasswordSettings();
        if (saved) {
            x_bytes = this.savedPasswordHash;
        } else if (this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            byte[] passwordBytes = AndroidUtilities.getStringBytes(textPassword);
            TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo;
            x_bytes = SRPHelper.getX(passwordBytes, algo);
        } else {
            x_bytes = null;
        }
        RequestDelegate requestDelegate = new AnonymousClass8(saved, x_bytes, req, textPassword);
        if (this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo2 = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo;
            req.password = SRPHelper.startCheck(x_bytes, this.currentPassword.srp_id, this.currentPassword.srp_B, algo2);
            if (req.password == null) {
                TLRPC.TL_error error = new TLRPC.TL_error();
                error.text = "ALGO_INVALID";
                requestDelegate.run(null, error);
                return;
            }
            int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate, 10);
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, this.classGuid);
            return;
        }
        TLRPC.TL_error error2 = new TLRPC.TL_error();
        error2.text = "PASSWORD_HASH_INVALID";
        requestDelegate.run(null, error2);
    }

    /* renamed from: org.telegram.ui.PassportActivity$8 */
    /* loaded from: classes4.dex */
    public class AnonymousClass8 implements RequestDelegate {
        final /* synthetic */ TLRPC.TL_account_getPasswordSettings val$req;
        final /* synthetic */ boolean val$saved;
        final /* synthetic */ String val$textPassword;
        final /* synthetic */ byte[] val$x_bytes;

        AnonymousClass8(boolean z, byte[] bArr, TLRPC.TL_account_getPasswordSettings tL_account_getPasswordSettings, String str) {
            PassportActivity.this = this$0;
            this.val$saved = z;
            this.val$x_bytes = bArr;
            this.val$req = tL_account_getPasswordSettings;
            this.val$textPassword = str;
        }

        private void openRequestInterface() {
            int type;
            if (PassportActivity.this.inputFields == null) {
                return;
            }
            if (!this.val$saved) {
                UserConfig.getInstance(PassportActivity.this.currentAccount).savePassword(this.val$x_bytes, PassportActivity.this.saltedPassword);
            }
            AndroidUtilities.hideKeyboard(PassportActivity.this.inputFields[0]);
            PassportActivity.this.ignoreOnFailure = true;
            if (PassportActivity.this.currentBotId == 0) {
                type = 8;
            } else {
                type = 0;
            }
            PassportActivity activity = new PassportActivity(type, PassportActivity.this.currentBotId, PassportActivity.this.currentScope, PassportActivity.this.currentPublicKey, PassportActivity.this.currentPayload, PassportActivity.this.currentNonce, PassportActivity.this.currentCallbackUrl, PassportActivity.this.currentForm, PassportActivity.this.currentPassword);
            activity.currentEmail = PassportActivity.this.currentEmail;
            activity.currentAccount = PassportActivity.this.currentAccount;
            activity.saltedPassword = PassportActivity.this.saltedPassword;
            activity.secureSecret = PassportActivity.this.secureSecret;
            activity.secureSecretId = PassportActivity.this.secureSecretId;
            activity.needActivityResult = PassportActivity.this.needActivityResult;
            if (PassportActivity.this.parentLayout != null && PassportActivity.this.parentLayout.checkTransitionAnimation()) {
                PassportActivity.this.presentAfterAnimation = activity;
            } else {
                PassportActivity.this.presentFragment(activity, true);
            }
        }

        private void resetSecret() {
            TLRPC.TL_account_updatePasswordSettings req2 = new TLRPC.TL_account_updatePasswordSettings();
            if (PassportActivity.this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) PassportActivity.this.currentPassword.current_algo;
                req2.password = SRPHelper.startCheck(this.val$x_bytes, PassportActivity.this.currentPassword.srp_id, PassportActivity.this.currentPassword.srp_B, algo);
            }
            req2.new_settings = new TLRPC.TL_account_passwordInputSettings();
            req2.new_settings.new_secure_settings = new TLRPC.TL_secureSecretSettings();
            req2.new_settings.new_secure_settings.secure_secret = new byte[0];
            req2.new_settings.new_secure_settings.secure_algo = new TLRPC.TL_securePasswordKdfAlgoUnknown();
            req2.new_settings.new_secure_settings.secure_secret_id = 0L;
            req2.new_settings.flags |= 4;
            ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(this.val$req, new RequestDelegate() { // from class: org.telegram.ui.PassportActivity$8$$ExternalSyntheticLambda5
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PassportActivity.AnonymousClass8.this.m4075lambda$resetSecret$3$orgtelegramuiPassportActivity$8(tLObject, tL_error);
                }
            });
        }

        /* renamed from: lambda$resetSecret$3$org-telegram-ui-PassportActivity$8 */
        public /* synthetic */ void m4075lambda$resetSecret$3$orgtelegramuiPassportActivity$8(TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$8$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    PassportActivity.AnonymousClass8.this.m4074lambda$resetSecret$2$orgtelegramuiPassportActivity$8(error);
                }
            });
        }

        /* renamed from: lambda$resetSecret$2$org-telegram-ui-PassportActivity$8 */
        public /* synthetic */ void m4074lambda$resetSecret$2$orgtelegramuiPassportActivity$8(TLRPC.TL_error error) {
            if (error != null && "SRP_ID_INVALID".equals(error.text)) {
                TLRPC.TL_account_getPassword getPasswordReq = new TLRPC.TL_account_getPassword();
                ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(getPasswordReq, new RequestDelegate() { // from class: org.telegram.ui.PassportActivity$8$$ExternalSyntheticLambda4
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        PassportActivity.AnonymousClass8.this.m4073lambda$resetSecret$1$orgtelegramuiPassportActivity$8(tLObject, tL_error);
                    }
                }, 8);
                return;
            }
            generateNewSecret();
        }

        /* renamed from: lambda$resetSecret$1$org-telegram-ui-PassportActivity$8 */
        public /* synthetic */ void m4073lambda$resetSecret$1$orgtelegramuiPassportActivity$8(final TLObject response2, final TLRPC.TL_error error2) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$8$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    PassportActivity.AnonymousClass8.this.m4072lambda$resetSecret$0$orgtelegramuiPassportActivity$8(error2, response2);
                }
            });
        }

        /* renamed from: lambda$resetSecret$0$org-telegram-ui-PassportActivity$8 */
        public /* synthetic */ void m4072lambda$resetSecret$0$orgtelegramuiPassportActivity$8(TLRPC.TL_error error2, TLObject response2) {
            if (error2 == null) {
                PassportActivity.this.currentPassword = (TLRPC.TL_account_password) response2;
                TwoStepVerificationActivity.initPasswordNewAlgo(PassportActivity.this.currentPassword);
                resetSecret();
            }
        }

        private void generateNewSecret() {
            DispatchQueue dispatchQueue = Utilities.globalQueue;
            final byte[] bArr = this.val$x_bytes;
            final String str = this.val$textPassword;
            dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.PassportActivity$8$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    PassportActivity.AnonymousClass8.this.m4071lambda$generateNewSecret$8$orgtelegramuiPassportActivity$8(bArr, str);
                }
            });
        }

        /* renamed from: lambda$generateNewSecret$8$org-telegram-ui-PassportActivity$8 */
        public /* synthetic */ void m4071lambda$generateNewSecret$8$orgtelegramuiPassportActivity$8(byte[] x_bytes, String textPassword) {
            Utilities.random.setSeed(PassportActivity.this.currentPassword.secure_random);
            TLRPC.TL_account_updatePasswordSettings req1 = new TLRPC.TL_account_updatePasswordSettings();
            if (PassportActivity.this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) PassportActivity.this.currentPassword.current_algo;
                req1.password = SRPHelper.startCheck(x_bytes, PassportActivity.this.currentPassword.srp_id, PassportActivity.this.currentPassword.srp_B, algo);
            }
            req1.new_settings = new TLRPC.TL_account_passwordInputSettings();
            PassportActivity passportActivity = PassportActivity.this;
            passportActivity.secureSecret = passportActivity.getRandomSecret();
            PassportActivity passportActivity2 = PassportActivity.this;
            passportActivity2.secureSecretId = Utilities.bytesToLong(Utilities.computeSHA256(passportActivity2.secureSecret));
            if (PassportActivity.this.currentPassword.new_secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 newAlgo = (TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) PassportActivity.this.currentPassword.new_secure_algo;
                PassportActivity.this.saltedPassword = Utilities.computePBKDF2(AndroidUtilities.getStringBytes(textPassword), newAlgo.salt);
                byte[] key = new byte[32];
                System.arraycopy(PassportActivity.this.saltedPassword, 0, key, 0, 32);
                byte[] iv = new byte[16];
                System.arraycopy(PassportActivity.this.saltedPassword, 32, iv, 0, 16);
                Utilities.aesCbcEncryptionByteArraySafe(PassportActivity.this.secureSecret, key, iv, 0, PassportActivity.this.secureSecret.length, 0, 1);
                req1.new_settings.new_secure_settings = new TLRPC.TL_secureSecretSettings();
                req1.new_settings.new_secure_settings.secure_algo = newAlgo;
                req1.new_settings.new_secure_settings.secure_secret = PassportActivity.this.secureSecret;
                req1.new_settings.new_secure_settings.secure_secret_id = PassportActivity.this.secureSecretId;
                req1.new_settings.flags |= 4;
            }
            ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req1, new RequestDelegate() { // from class: org.telegram.ui.PassportActivity$8$$ExternalSyntheticLambda3
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PassportActivity.AnonymousClass8.this.m4070lambda$generateNewSecret$7$orgtelegramuiPassportActivity$8(tLObject, tL_error);
                }
            });
        }

        /* renamed from: lambda$generateNewSecret$7$org-telegram-ui-PassportActivity$8 */
        public /* synthetic */ void m4070lambda$generateNewSecret$7$orgtelegramuiPassportActivity$8(TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$8$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    PassportActivity.AnonymousClass8.this.m4069lambda$generateNewSecret$6$orgtelegramuiPassportActivity$8(error);
                }
            });
        }

        /* renamed from: lambda$generateNewSecret$6$org-telegram-ui-PassportActivity$8 */
        public /* synthetic */ void m4069lambda$generateNewSecret$6$orgtelegramuiPassportActivity$8(TLRPC.TL_error error) {
            if (error == null || !"SRP_ID_INVALID".equals(error.text)) {
                if (PassportActivity.this.currentForm == null) {
                    PassportActivity.this.currentForm = new TLRPC.TL_account_authorizationForm();
                }
                openRequestInterface();
                return;
            }
            TLRPC.TL_account_getPassword getPasswordReq = new TLRPC.TL_account_getPassword();
            ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(getPasswordReq, new RequestDelegate() { // from class: org.telegram.ui.PassportActivity$8$$ExternalSyntheticLambda2
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PassportActivity.AnonymousClass8.this.m4068lambda$generateNewSecret$5$orgtelegramuiPassportActivity$8(tLObject, tL_error);
                }
            }, 8);
        }

        /* renamed from: lambda$generateNewSecret$5$org-telegram-ui-PassportActivity$8 */
        public /* synthetic */ void m4068lambda$generateNewSecret$5$orgtelegramuiPassportActivity$8(final TLObject response2, final TLRPC.TL_error error2) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$8$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    PassportActivity.AnonymousClass8.this.m4067lambda$generateNewSecret$4$orgtelegramuiPassportActivity$8(error2, response2);
                }
            });
        }

        /* renamed from: lambda$generateNewSecret$4$org-telegram-ui-PassportActivity$8 */
        public /* synthetic */ void m4067lambda$generateNewSecret$4$orgtelegramuiPassportActivity$8(TLRPC.TL_error error2, TLObject response2) {
            if (error2 == null) {
                PassportActivity.this.currentPassword = (TLRPC.TL_account_password) response2;
                TwoStepVerificationActivity.initPasswordNewAlgo(PassportActivity.this.currentPassword);
                generateNewSecret();
            }
        }

        @Override // org.telegram.tgnet.RequestDelegate
        public void run(final TLObject response, final TLRPC.TL_error error) {
            if (error != null && "SRP_ID_INVALID".equals(error.text)) {
                TLRPC.TL_account_getPassword getPasswordReq = new TLRPC.TL_account_getPassword();
                ConnectionsManager connectionsManager = ConnectionsManager.getInstance(PassportActivity.this.currentAccount);
                final boolean z = this.val$saved;
                connectionsManager.sendRequest(getPasswordReq, new RequestDelegate() { // from class: org.telegram.ui.PassportActivity$8$$ExternalSyntheticLambda7
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        PassportActivity.AnonymousClass8.this.m4076lambda$run$10$orgtelegramuiPassportActivity$8(z, tLObject, tL_error);
                    }
                }, 8);
            } else if (error == null) {
                DispatchQueue dispatchQueue = Utilities.globalQueue;
                final String str = this.val$textPassword;
                final boolean z2 = this.val$saved;
                dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.PassportActivity$8$$ExternalSyntheticLambda8
                    @Override // java.lang.Runnable
                    public final void run() {
                        PassportActivity.AnonymousClass8.this.m4081lambda$run$15$orgtelegramuiPassportActivity$8(response, str, z2);
                    }
                });
            } else {
                final boolean z3 = this.val$saved;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$8$$ExternalSyntheticLambda16
                    @Override // java.lang.Runnable
                    public final void run() {
                        PassportActivity.AnonymousClass8.this.m4082lambda$run$16$orgtelegramuiPassportActivity$8(z3, error);
                    }
                });
            }
        }

        /* renamed from: lambda$run$10$org-telegram-ui-PassportActivity$8 */
        public /* synthetic */ void m4076lambda$run$10$orgtelegramuiPassportActivity$8(final boolean saved, final TLObject response2, final TLRPC.TL_error error2) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$8$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    PassportActivity.AnonymousClass8.this.m4083lambda$run$9$orgtelegramuiPassportActivity$8(error2, response2, saved);
                }
            });
        }

        /* renamed from: lambda$run$9$org-telegram-ui-PassportActivity$8 */
        public /* synthetic */ void m4083lambda$run$9$orgtelegramuiPassportActivity$8(TLRPC.TL_error error2, TLObject response2, boolean saved) {
            if (error2 == null) {
                PassportActivity.this.currentPassword = (TLRPC.TL_account_password) response2;
                TwoStepVerificationActivity.initPasswordNewAlgo(PassportActivity.this.currentPassword);
                PassportActivity.this.onPasswordDone(saved);
            }
        }

        /* renamed from: lambda$run$15$org-telegram-ui-PassportActivity$8 */
        public /* synthetic */ void m4081lambda$run$15$orgtelegramuiPassportActivity$8(TLObject response, String textPassword, final boolean saved) {
            final byte[] secure_salt;
            final TLRPC.TL_account_passwordSettings settings = (TLRPC.TL_account_passwordSettings) response;
            if (settings.secure_settings == null) {
                if (PassportActivity.this.currentPassword.new_secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                    TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 algo = (TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) PassportActivity.this.currentPassword.new_secure_algo;
                    secure_salt = algo.salt;
                    PassportActivity.this.saltedPassword = Utilities.computePBKDF2(AndroidUtilities.getStringBytes(textPassword), algo.salt);
                } else {
                    secure_salt = new byte[0];
                }
                PassportActivity.this.secureSecret = null;
                PassportActivity.this.secureSecretId = 0L;
            } else {
                PassportActivity.this.secureSecret = settings.secure_settings.secure_secret;
                PassportActivity.this.secureSecretId = settings.secure_settings.secure_secret_id;
                if (settings.secure_settings.secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoSHA512) {
                    secure_salt = ((TLRPC.TL_securePasswordKdfAlgoSHA512) settings.secure_settings.secure_algo).salt;
                    PassportActivity.this.saltedPassword = Utilities.computeSHA512(secure_salt, AndroidUtilities.getStringBytes(textPassword), secure_salt);
                } else if (settings.secure_settings.secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                    TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 algo2 = (TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) settings.secure_settings.secure_algo;
                    secure_salt = algo2.salt;
                    PassportActivity.this.saltedPassword = Utilities.computePBKDF2(AndroidUtilities.getStringBytes(textPassword), algo2.salt);
                } else if (settings.secure_settings.secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoUnknown) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$8$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            PassportActivity.AnonymousClass8.this.m4077lambda$run$11$orgtelegramuiPassportActivity$8();
                        }
                    });
                    return;
                } else {
                    secure_salt = new byte[0];
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$8$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    PassportActivity.AnonymousClass8.this.m4080lambda$run$14$orgtelegramuiPassportActivity$8(settings, saved, secure_salt);
                }
            });
        }

        /* renamed from: lambda$run$11$org-telegram-ui-PassportActivity$8 */
        public /* synthetic */ void m4077lambda$run$11$orgtelegramuiPassportActivity$8() {
            AlertsCreator.showUpdateAppAlert(PassportActivity.this.getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
        }

        /* renamed from: lambda$run$14$org-telegram-ui-PassportActivity$8 */
        public /* synthetic */ void m4080lambda$run$14$orgtelegramuiPassportActivity$8(TLRPC.TL_account_passwordSettings settings, boolean saved, byte[] secure_salt) {
            PassportActivity.this.currentEmail = settings.email;
            if (saved) {
                PassportActivity passportActivity = PassportActivity.this;
                passportActivity.saltedPassword = passportActivity.savedSaltedPassword;
            }
            PassportActivity passportActivity2 = PassportActivity.this;
            if (PassportActivity.checkSecret(passportActivity2.decryptSecret(passportActivity2.secureSecret, PassportActivity.this.saltedPassword), Long.valueOf(PassportActivity.this.secureSecretId)) && secure_salt.length != 0 && PassportActivity.this.secureSecretId != 0) {
                if (PassportActivity.this.currentBotId == 0) {
                    TLRPC.TL_account_getAllSecureValues req12 = new TLRPC.TL_account_getAllSecureValues();
                    ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req12, new RequestDelegate() { // from class: org.telegram.ui.PassportActivity$8$$ExternalSyntheticLambda6
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            PassportActivity.AnonymousClass8.this.m4079lambda$run$13$orgtelegramuiPassportActivity$8(tLObject, tL_error);
                        }
                    });
                    return;
                }
                openRequestInterface();
            } else if (saved) {
                UserConfig.getInstance(PassportActivity.this.currentAccount).resetSavedPassword();
                PassportActivity.this.usingSavedPassword = 0;
                PassportActivity.this.updatePasswordInterface();
            } else {
                if (PassportActivity.this.currentForm != null) {
                    PassportActivity.this.currentForm.values.clear();
                    PassportActivity.this.currentForm.errors.clear();
                }
                if (PassportActivity.this.secureSecret == null || PassportActivity.this.secureSecret.length == 0) {
                    generateNewSecret();
                } else {
                    resetSecret();
                }
            }
        }

        /* renamed from: lambda$run$13$org-telegram-ui-PassportActivity$8 */
        public /* synthetic */ void m4079lambda$run$13$orgtelegramuiPassportActivity$8(final TLObject response1, final TLRPC.TL_error error1) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$8$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    PassportActivity.AnonymousClass8.this.m4078lambda$run$12$orgtelegramuiPassportActivity$8(response1, error1);
                }
            });
        }

        /* renamed from: lambda$run$12$org-telegram-ui-PassportActivity$8 */
        public /* synthetic */ void m4078lambda$run$12$orgtelegramuiPassportActivity$8(TLObject response1, TLRPC.TL_error error1) {
            if (response1 != null) {
                PassportActivity.this.currentForm = new TLRPC.TL_account_authorizationForm();
                TLRPC.Vector vector = (TLRPC.Vector) response1;
                int size = vector.objects.size();
                for (int a = 0; a < size; a++) {
                    PassportActivity.this.currentForm.values.add((TLRPC.TL_secureValue) vector.objects.get(a));
                }
                openRequestInterface();
                return;
            }
            if (!"APP_VERSION_OUTDATED".equals(error1.text)) {
                PassportActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error1.text);
            } else {
                AlertsCreator.showUpdateAppAlert(PassportActivity.this.getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
            }
            PassportActivity.this.showEditDoneProgress(true, false);
        }

        /* renamed from: lambda$run$16$org-telegram-ui-PassportActivity$8 */
        public /* synthetic */ void m4082lambda$run$16$orgtelegramuiPassportActivity$8(boolean saved, TLRPC.TL_error error) {
            String timeString;
            if (saved) {
                UserConfig.getInstance(PassportActivity.this.currentAccount).resetSavedPassword();
                PassportActivity.this.usingSavedPassword = 0;
                PassportActivity.this.updatePasswordInterface();
                if (PassportActivity.this.inputFieldContainers != null && PassportActivity.this.inputFieldContainers[0].getVisibility() == 0) {
                    PassportActivity.this.inputFields[0].requestFocus();
                    AndroidUtilities.showKeyboard(PassportActivity.this.inputFields[0]);
                    return;
                }
                return;
            }
            PassportActivity.this.showEditDoneProgress(true, false);
            if (error.text.equals("PASSWORD_HASH_INVALID")) {
                PassportActivity.this.onPasscodeError(true);
            } else if (!error.text.startsWith("FLOOD_WAIT")) {
                PassportActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
            } else {
                int time = Utilities.parseInt((CharSequence) error.text).intValue();
                if (time >= 60) {
                    timeString = LocaleController.formatPluralString("Minutes", time / 60, new Object[0]);
                } else {
                    timeString = LocaleController.formatPluralString("Seconds", time, new Object[0]);
                }
                PassportActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
            }
        }
    }

    private boolean isPersonalDocument(TLRPC.SecureValueType type) {
        return (type instanceof TLRPC.TL_secureValueTypeDriverLicense) || (type instanceof TLRPC.TL_secureValueTypePassport) || (type instanceof TLRPC.TL_secureValueTypeInternalPassport) || (type instanceof TLRPC.TL_secureValueTypeIdentityCard);
    }

    private boolean isAddressDocument(TLRPC.SecureValueType type) {
        return (type instanceof TLRPC.TL_secureValueTypeUtilityBill) || (type instanceof TLRPC.TL_secureValueTypeBankStatement) || (type instanceof TLRPC.TL_secureValueTypePassportRegistration) || (type instanceof TLRPC.TL_secureValueTypeTemporaryRegistration) || (type instanceof TLRPC.TL_secureValueTypeRentalAgreement);
    }

    /* JADX WARN: Code restructure failed: missing block: B:111:0x0325, code lost:
        if (isPersonalDocument(r2.type) != false) goto L116;
     */
    /* JADX WARN: Removed duplicated region for block: B:129:0x039b  */
    /* JADX WARN: Removed duplicated region for block: B:130:0x039e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void createRequestInterface(android.content.Context r32) {
        /*
            Method dump skipped, instructions count: 1369
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.createRequestInterface(android.content.Context):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.PassportActivity$1ValueToSend */
    /* loaded from: classes4.dex */
    public class C1ValueToSend {
        boolean selfie_required;
        boolean translation_required;
        TLRPC.TL_secureValue value;

        public C1ValueToSend(TLRPC.TL_secureValue v, boolean s, boolean t) {
            PassportActivity.this = this$0;
            this.value = v;
            this.selfie_required = s;
            this.translation_required = t;
        }
    }

    /* renamed from: lambda$createRequestInterface$16$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4041x9959d775(View view) {
        ArrayList<C1ValueToSend> valuesToSend;
        int size;
        TLRPC.TL_secureRequiredType requiredType;
        ArrayList<C1ValueToSend> valuesToSend2 = new ArrayList<>();
        int size2 = this.currentForm.required_types.size();
        for (int a = 0; a < size2; a++) {
            TLRPC.SecureRequiredType secureRequiredType = this.currentForm.required_types.get(a);
            if (secureRequiredType instanceof TLRPC.TL_secureRequiredType) {
                requiredType = (TLRPC.TL_secureRequiredType) secureRequiredType;
            } else {
                if (secureRequiredType instanceof TLRPC.TL_secureRequiredTypeOneOf) {
                    TLRPC.TL_secureRequiredTypeOneOf requiredTypeOneOf = (TLRPC.TL_secureRequiredTypeOneOf) secureRequiredType;
                    if (!requiredTypeOneOf.types.isEmpty()) {
                        TLRPC.SecureRequiredType secureRequiredType2 = requiredTypeOneOf.types.get(0);
                        if (secureRequiredType2 instanceof TLRPC.TL_secureRequiredType) {
                            TLRPC.TL_secureRequiredType requiredType2 = (TLRPC.TL_secureRequiredType) secureRequiredType2;
                            int b = 0;
                            int size22 = requiredTypeOneOf.types.size();
                            while (true) {
                                if (b >= size22) {
                                    requiredType = requiredType2;
                                    break;
                                }
                                TLRPC.SecureRequiredType secureRequiredType3 = requiredTypeOneOf.types.get(b);
                                if (secureRequiredType3 instanceof TLRPC.TL_secureRequiredType) {
                                    TLRPC.TL_secureRequiredType innerType = (TLRPC.TL_secureRequiredType) secureRequiredType3;
                                    if (getValueByType(innerType, true) != null) {
                                        requiredType = innerType;
                                        break;
                                    }
                                }
                                b++;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            }
            TLRPC.TL_secureValue value = getValueByType(requiredType, true);
            if (value == null) {
                Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
                if (v != null) {
                    v.vibrate(200L);
                }
                AndroidUtilities.shakeView(getViewByType(requiredType), 2.0f, 0);
                return;
            }
            String key = getNameForType(requiredType.type);
            HashMap<String, String> errors = this.errorsMap.get(key);
            if (errors != null && !errors.isEmpty()) {
                Vibrator v2 = (Vibrator) getParentActivity().getSystemService("vibrator");
                if (v2 != null) {
                    v2.vibrate(200L);
                }
                AndroidUtilities.shakeView(getViewByType(requiredType), 2.0f, 0);
                return;
            }
            valuesToSend2.add(new C1ValueToSend(value, requiredType.selfie_required, requiredType.translation_required));
        }
        showEditDoneProgress(false, true);
        TLRPC.TL_account_acceptAuthorization req = new TLRPC.TL_account_acceptAuthorization();
        req.bot_id = this.currentBotId;
        req.scope = this.currentScope;
        req.public_key = this.currentPublicKey;
        JSONObject jsonObject = new JSONObject();
        int size3 = valuesToSend2.size();
        int a2 = 0;
        while (a2 < size3) {
            C1ValueToSend valueToSend = valuesToSend2.get(a2);
            TLRPC.TL_secureValue secureValue = valueToSend.value;
            JSONObject data = new JSONObject();
            if (secureValue.plain_data != null) {
                if (secureValue.plain_data instanceof TLRPC.TL_securePlainEmail) {
                    TLRPC.TL_securePlainEmail tL_securePlainEmail = (TLRPC.TL_securePlainEmail) secureValue.plain_data;
                } else if (secureValue.plain_data instanceof TLRPC.TL_securePlainPhone) {
                    TLRPC.TL_securePlainPhone tL_securePlainPhone = (TLRPC.TL_securePlainPhone) secureValue.plain_data;
                    valuesToSend = valuesToSend2;
                    size = size3;
                }
                valuesToSend = valuesToSend2;
                size = size3;
            } else {
                try {
                    JSONObject result = new JSONObject();
                    if (secureValue.data != null) {
                        try {
                            byte[] decryptedSecret = decryptValueSecret(secureValue.data.secret, secureValue.data.data_hash);
                            data.put("data_hash", Base64.encodeToString(secureValue.data.data_hash, 2));
                            data.put("secret", Base64.encodeToString(decryptedSecret, 2));
                            result.put("data", data);
                        } catch (Exception e) {
                            valuesToSend = valuesToSend2;
                            size = size3;
                        }
                    }
                    if (!secureValue.files.isEmpty()) {
                        try {
                            JSONArray files = new JSONArray();
                            int b2 = 0;
                            for (int size23 = secureValue.files.size(); b2 < size23; size23 = size23) {
                                valuesToSend = valuesToSend2;
                                try {
                                    TLRPC.TL_secureFile secureFile = (TLRPC.TL_secureFile) secureValue.files.get(b2);
                                    size = size3;
                                    try {
                                        JSONObject data2 = data;
                                        try {
                                            byte[] decryptedSecret2 = decryptValueSecret(secureFile.secret, secureFile.file_hash);
                                            JSONObject file = new JSONObject();
                                            file.put("file_hash", Base64.encodeToString(secureFile.file_hash, 2));
                                            file.put("secret", Base64.encodeToString(decryptedSecret2, 2));
                                            files.put(file);
                                            b2++;
                                            valuesToSend2 = valuesToSend;
                                            size3 = size;
                                            data = data2;
                                        } catch (Exception e2) {
                                        }
                                    } catch (Exception e3) {
                                    }
                                } catch (Exception e4) {
                                    size = size3;
                                }
                            }
                            valuesToSend = valuesToSend2;
                            size = size3;
                            result.put("files", files);
                        } catch (Exception e5) {
                            valuesToSend = valuesToSend2;
                            size = size3;
                        }
                    } else {
                        valuesToSend = valuesToSend2;
                        size = size3;
                    }
                    try {
                        if (secureValue.front_side instanceof TLRPC.TL_secureFile) {
                            TLRPC.TL_secureFile secureFile2 = (TLRPC.TL_secureFile) secureValue.front_side;
                            byte[] decryptedSecret3 = decryptValueSecret(secureFile2.secret, secureFile2.file_hash);
                            JSONObject front = new JSONObject();
                            front.put("file_hash", Base64.encodeToString(secureFile2.file_hash, 2));
                            front.put("secret", Base64.encodeToString(decryptedSecret3, 2));
                            result.put("front_side", front);
                        }
                        if (secureValue.reverse_side instanceof TLRPC.TL_secureFile) {
                            TLRPC.TL_secureFile secureFile3 = (TLRPC.TL_secureFile) secureValue.reverse_side;
                            byte[] decryptedSecret4 = decryptValueSecret(secureFile3.secret, secureFile3.file_hash);
                            JSONObject reverse = new JSONObject();
                            reverse.put("file_hash", Base64.encodeToString(secureFile3.file_hash, 2));
                            reverse.put("secret", Base64.encodeToString(decryptedSecret4, 2));
                            result.put("reverse_side", reverse);
                        }
                        if (valueToSend.selfie_required && (secureValue.selfie instanceof TLRPC.TL_secureFile)) {
                            TLRPC.TL_secureFile secureFile4 = (TLRPC.TL_secureFile) secureValue.selfie;
                            byte[] decryptedSecret5 = decryptValueSecret(secureFile4.secret, secureFile4.file_hash);
                            JSONObject selfie = new JSONObject();
                            selfie.put("file_hash", Base64.encodeToString(secureFile4.file_hash, 2));
                            selfie.put("secret", Base64.encodeToString(decryptedSecret5, 2));
                            result.put("selfie", selfie);
                        }
                        if (valueToSend.translation_required && !secureValue.translation.isEmpty()) {
                            JSONArray translation = new JSONArray();
                            int b3 = 0;
                            int size24 = secureValue.translation.size();
                            while (b3 < size24) {
                                TLRPC.TL_secureFile secureFile5 = (TLRPC.TL_secureFile) secureValue.translation.get(b3);
                                byte[] decryptedSecret6 = decryptValueSecret(secureFile5.secret, secureFile5.file_hash);
                                JSONObject file2 = new JSONObject();
                                int size25 = size24;
                                C1ValueToSend valueToSend2 = valueToSend;
                                try {
                                    file2.put("file_hash", Base64.encodeToString(secureFile5.file_hash, 2));
                                    file2.put("secret", Base64.encodeToString(decryptedSecret6, 2));
                                    translation.put(file2);
                                    b3++;
                                    size24 = size25;
                                    valueToSend = valueToSend2;
                                } catch (Exception e6) {
                                }
                            }
                            result.put("translation", translation);
                        }
                        jsonObject.put(getNameForType(secureValue.type), result);
                    } catch (Exception e7) {
                    }
                } catch (Exception e8) {
                    valuesToSend = valuesToSend2;
                    size = size3;
                }
            }
            TLRPC.TL_secureValueHash hash = new TLRPC.TL_secureValueHash();
            hash.type = secureValue.type;
            hash.hash = secureValue.hash;
            req.value_hashes.add(hash);
            a2++;
            valuesToSend2 = valuesToSend;
            size3 = size;
        }
        JSONObject result2 = new JSONObject();
        try {
            result2.put("secure_data", jsonObject);
        } catch (Exception e9) {
        }
        Object obj = this.currentPayload;
        if (obj != null) {
            try {
                result2.put("payload", obj);
            } catch (Exception e10) {
            }
        }
        Object obj2 = this.currentNonce;
        if (obj2 != null) {
            try {
                result2.put("nonce", obj2);
            } catch (Exception e11) {
            }
        }
        String json = result2.toString();
        EncryptionResult encryptionResult = encryptData(AndroidUtilities.getStringBytes(json));
        req.credentials = new TLRPC.TL_secureCredentialsEncrypted();
        req.credentials.hash = encryptionResult.fileHash;
        req.credentials.data = encryptionResult.encryptedData;
        try {
            String key2 = this.currentPublicKey.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
            KeyFactory kf = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.decode(key2, 0));
            RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
            Cipher c = Cipher.getInstance("RSA/NONE/OAEPWithSHA1AndMGF1Padding");
            c.init(1, pubKey);
            req.credentials.secret = c.doFinal(encryptionResult.decrypyedFileSecret);
        } catch (Exception e12) {
            FileLog.e(e12);
        }
        int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda60
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PassportActivity.this.m4040x5c3a1356(tLObject, tL_error);
            }
        });
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, this.classGuid);
    }

    /* renamed from: lambda$createRequestInterface$15$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4040x5c3a1356(TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda52
            @Override // java.lang.Runnable
            public final void run() {
                PassportActivity.this.m4039x1f1a4f37(error);
            }
        });
    }

    /* renamed from: lambda$createRequestInterface$14$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4039x1f1a4f37(TLRPC.TL_error error) {
        if (error == null) {
            this.ignoreOnFailure = true;
            callCallback(true);
            finishFragment();
            return;
        }
        showEditDoneProgress(false, false);
        if ("APP_VERSION_OUTDATED".equals(error.text)) {
            AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
        } else {
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
        }
    }

    private void createManageInterface(Context context) {
        boolean documentOnly;
        TLRPC.TL_secureRequiredType requiredType;
        ArrayList<TLRPC.TL_secureRequiredType> documentTypes;
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.actionBar.setTitle(LocaleController.getString("TelegramPassport", R.string.TelegramPassport));
        this.actionBar.createMenu().addItem(1, R.drawable.msg_info);
        HeaderCell headerCell = new HeaderCell(context);
        this.headerCell = headerCell;
        headerCell.setText(LocaleController.getString("PassportProvidedInformation", R.string.PassportProvidedInformation));
        this.headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
        ShadowSectionCell shadowSectionCell = new ShadowSectionCell(context);
        this.sectionCell = shadowSectionCell;
        shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
        this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
        TextSettingsCell textSettingsCell = new TextSettingsCell(context);
        this.addDocumentCell = textSettingsCell;
        textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.addDocumentCell.setText(LocaleController.getString("PassportNoDocumentsAdd", R.string.PassportNoDocumentsAdd), true);
        this.linearLayout2.addView(this.addDocumentCell, LayoutHelper.createLinear(-1, -2));
        this.addDocumentCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda14
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PassportActivity.this.m4020lambda$createManageInterface$17$orgtelegramuiPassportActivity(view);
            }
        });
        TextSettingsCell textSettingsCell2 = new TextSettingsCell(context);
        this.deletePassportCell = textSettingsCell2;
        textSettingsCell2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
        this.deletePassportCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.deletePassportCell.setText(LocaleController.getString("TelegramPassportDelete", R.string.TelegramPassportDelete), false);
        this.linearLayout2.addView(this.deletePassportCell, LayoutHelper.createLinear(-1, -2));
        this.deletePassportCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda15
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PassportActivity.this.m4024lambda$createManageInterface$21$orgtelegramuiPassportActivity(view);
            }
        });
        ShadowSectionCell shadowSectionCell2 = new ShadowSectionCell(context);
        this.addDocumentSectionCell = shadowSectionCell2;
        shadowSectionCell2.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.linearLayout2.addView(this.addDocumentSectionCell, LayoutHelper.createLinear(-1, -2));
        LinearLayout linearLayout = new LinearLayout(context);
        this.emptyLayout = linearLayout;
        linearLayout.setOrientation(1);
        this.emptyLayout.setGravity(17);
        this.emptyLayout.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        if (AndroidUtilities.isTablet()) {
            this.linearLayout2.addView(this.emptyLayout, new LinearLayout.LayoutParams(-1, AndroidUtilities.dp(528.0f) - ActionBar.getCurrentActionBarHeight()));
        } else {
            this.linearLayout2.addView(this.emptyLayout, new LinearLayout.LayoutParams(-1, AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()));
        }
        ImageView imageView = new ImageView(context);
        this.emptyImageView = imageView;
        imageView.setImageResource(R.drawable.no_passport);
        this.emptyImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_sessions_devicesImage), PorterDuff.Mode.MULTIPLY));
        this.emptyLayout.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
        TextView textView = new TextView(context);
        this.emptyTextView1 = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.emptyTextView1.setGravity(17);
        this.emptyTextView1.setTextSize(1, 15.0f);
        this.emptyTextView1.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.emptyTextView1.setText(LocaleController.getString("PassportNoDocuments", R.string.PassportNoDocuments));
        this.emptyLayout.addView(this.emptyTextView1, LayoutHelper.createLinear(-2, -2, 17, 0, 16, 0, 0));
        TextView textView2 = new TextView(context);
        this.emptyTextView2 = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.emptyTextView2.setGravity(17);
        this.emptyTextView2.setTextSize(1, 14.0f);
        this.emptyTextView2.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.emptyTextView2.setText(LocaleController.getString("PassportNoDocumentsInfo", R.string.PassportNoDocumentsInfo));
        this.emptyLayout.addView(this.emptyTextView2, LayoutHelper.createLinear(-2, -2, 17, 0, 14, 0, 0));
        TextView textView3 = new TextView(context);
        this.emptyTextView3 = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
        this.emptyTextView3.setGravity(17);
        this.emptyTextView3.setTextSize(1, 15.0f);
        this.emptyTextView3.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.emptyTextView3.setGravity(17);
        this.emptyTextView3.setText(LocaleController.getString("PassportNoDocumentsAdd", R.string.PassportNoDocumentsAdd).toUpperCase());
        this.emptyLayout.addView(this.emptyTextView3, LayoutHelper.createLinear(-2, 30, 17, 0, 16, 0, 0));
        this.emptyTextView3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda16
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PassportActivity.this.m4025lambda$createManageInterface$22$orgtelegramuiPassportActivity(view);
            }
        });
        int size = this.currentForm.values.size();
        int a = 0;
        while (a < size) {
            TLRPC.TL_secureValue value = this.currentForm.values.get(a);
            if (!isPersonalDocument(value.type)) {
                if (isAddressDocument(value.type)) {
                    ArrayList<TLRPC.TL_secureRequiredType> documentTypes2 = new ArrayList<>();
                    TLRPC.TL_secureRequiredType requiredType2 = new TLRPC.TL_secureRequiredType();
                    requiredType2.type = value.type;
                    requiredType2.translation_required = true;
                    documentTypes2.add(requiredType2);
                    TLRPC.TL_secureRequiredType requiredType3 = new TLRPC.TL_secureRequiredType();
                    requiredType3.type = new TLRPC.TL_secureValueTypeAddress();
                    documentTypes = documentTypes2;
                    requiredType = requiredType3;
                    documentOnly = true;
                } else {
                    TLRPC.TL_secureRequiredType requiredType4 = new TLRPC.TL_secureRequiredType();
                    requiredType4.type = value.type;
                    documentTypes = null;
                    requiredType = requiredType4;
                    documentOnly = false;
                }
            } else {
                ArrayList<TLRPC.TL_secureRequiredType> documentTypes3 = new ArrayList<>();
                TLRPC.TL_secureRequiredType requiredType5 = new TLRPC.TL_secureRequiredType();
                requiredType5.type = value.type;
                requiredType5.selfie_required = true;
                requiredType5.translation_required = true;
                documentTypes3.add(requiredType5);
                TLRPC.TL_secureRequiredType requiredType6 = new TLRPC.TL_secureRequiredType();
                requiredType6.type = new TLRPC.TL_secureValueTypePersonalDetails();
                documentTypes = documentTypes3;
                requiredType = requiredType6;
                documentOnly = true;
            }
            addField(context, requiredType, documentTypes, documentOnly, a == size + (-1));
            a++;
        }
        updateManageVisibility();
    }

    /* renamed from: lambda$createManageInterface$17$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4020lambda$createManageInterface$17$orgtelegramuiPassportActivity(View v) {
        openAddDocumentAlert();
    }

    /* renamed from: lambda$createManageInterface$21$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4024lambda$createManageInterface$21$orgtelegramuiPassportActivity(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("TelegramPassportDeleteTitle", R.string.TelegramPassportDeleteTitle));
        builder.setMessage(LocaleController.getString("TelegramPassportDeleteAlert", R.string.TelegramPassportDeleteAlert));
        builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda22
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                PassportActivity.this.m4023lambda$createManageInterface$20$orgtelegramuiPassportActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        AlertDialog alertDialog = builder.create();
        showDialog(alertDialog);
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
        }
    }

    /* renamed from: lambda$createManageInterface$20$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4023lambda$createManageInterface$20$orgtelegramuiPassportActivity(DialogInterface dialog, int which) {
        TLRPC.TL_account_deleteSecureValue req = new TLRPC.TL_account_deleteSecureValue();
        for (int a = 0; a < this.currentForm.values.size(); a++) {
            req.types.add(this.currentForm.values.get(a).type);
        }
        needShowProgress();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda58
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PassportActivity.this.m4022lambda$createManageInterface$19$orgtelegramuiPassportActivity(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$createManageInterface$19$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4022lambda$createManageInterface$19$orgtelegramuiPassportActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda42
            @Override // java.lang.Runnable
            public final void run() {
                PassportActivity.this.m4021lambda$createManageInterface$18$orgtelegramuiPassportActivity();
            }
        });
    }

    /* renamed from: lambda$createManageInterface$18$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4021lambda$createManageInterface$18$orgtelegramuiPassportActivity() {
        int a = 0;
        while (a < this.linearLayout2.getChildCount()) {
            View child = this.linearLayout2.getChildAt(a);
            if (child instanceof TextDetailSecureCell) {
                this.linearLayout2.removeView(child);
                a--;
            }
            a++;
        }
        needHideProgress();
        this.typesViews.clear();
        this.typesValues.clear();
        this.currentForm.values.clear();
        updateManageVisibility();
    }

    /* renamed from: lambda$createManageInterface$22$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4025lambda$createManageInterface$22$orgtelegramuiPassportActivity(View v) {
        openAddDocumentAlert();
    }

    private boolean hasNotValueForType(Class<? extends TLRPC.SecureValueType> type) {
        int count = this.currentForm.values.size();
        for (int a = 0; a < count; a++) {
            if (this.currentForm.values.get(a).type.getClass() == type) {
                return false;
            }
        }
        return true;
    }

    private boolean hasUnfilledValues() {
        return hasNotValueForType(TLRPC.TL_secureValueTypePhone.class) || hasNotValueForType(TLRPC.TL_secureValueTypeEmail.class) || hasNotValueForType(TLRPC.TL_secureValueTypePersonalDetails.class) || hasNotValueForType(TLRPC.TL_secureValueTypePassport.class) || hasNotValueForType(TLRPC.TL_secureValueTypeInternalPassport.class) || hasNotValueForType(TLRPC.TL_secureValueTypeIdentityCard.class) || hasNotValueForType(TLRPC.TL_secureValueTypeDriverLicense.class) || hasNotValueForType(TLRPC.TL_secureValueTypeAddress.class) || hasNotValueForType(TLRPC.TL_secureValueTypeUtilityBill.class) || hasNotValueForType(TLRPC.TL_secureValueTypePassportRegistration.class) || hasNotValueForType(TLRPC.TL_secureValueTypeTemporaryRegistration.class) || hasNotValueForType(TLRPC.TL_secureValueTypeBankStatement.class) || hasNotValueForType(TLRPC.TL_secureValueTypeRentalAgreement.class);
    }

    private void openAddDocumentAlert() {
        ArrayList<CharSequence> values = new ArrayList<>();
        final ArrayList<Class<? extends TLRPC.SecureValueType>> types = new ArrayList<>();
        if (hasNotValueForType(TLRPC.TL_secureValueTypePhone.class)) {
            values.add(LocaleController.getString("ActionBotDocumentPhone", R.string.ActionBotDocumentPhone));
            types.add(TLRPC.TL_secureValueTypePhone.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeEmail.class)) {
            values.add(LocaleController.getString("ActionBotDocumentEmail", R.string.ActionBotDocumentEmail));
            types.add(TLRPC.TL_secureValueTypeEmail.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypePersonalDetails.class)) {
            values.add(LocaleController.getString("ActionBotDocumentIdentity", R.string.ActionBotDocumentIdentity));
            types.add(TLRPC.TL_secureValueTypePersonalDetails.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypePassport.class)) {
            values.add(LocaleController.getString("ActionBotDocumentPassport", R.string.ActionBotDocumentPassport));
            types.add(TLRPC.TL_secureValueTypePassport.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeInternalPassport.class)) {
            values.add(LocaleController.getString("ActionBotDocumentInternalPassport", R.string.ActionBotDocumentInternalPassport));
            types.add(TLRPC.TL_secureValueTypeInternalPassport.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypePassportRegistration.class)) {
            values.add(LocaleController.getString("ActionBotDocumentPassportRegistration", R.string.ActionBotDocumentPassportRegistration));
            types.add(TLRPC.TL_secureValueTypePassportRegistration.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeTemporaryRegistration.class)) {
            values.add(LocaleController.getString("ActionBotDocumentTemporaryRegistration", R.string.ActionBotDocumentTemporaryRegistration));
            types.add(TLRPC.TL_secureValueTypeTemporaryRegistration.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeIdentityCard.class)) {
            values.add(LocaleController.getString("ActionBotDocumentIdentityCard", R.string.ActionBotDocumentIdentityCard));
            types.add(TLRPC.TL_secureValueTypeIdentityCard.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeDriverLicense.class)) {
            values.add(LocaleController.getString("ActionBotDocumentDriverLicence", R.string.ActionBotDocumentDriverLicence));
            types.add(TLRPC.TL_secureValueTypeDriverLicense.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeAddress.class)) {
            values.add(LocaleController.getString("ActionBotDocumentAddress", R.string.ActionBotDocumentAddress));
            types.add(TLRPC.TL_secureValueTypeAddress.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeUtilityBill.class)) {
            values.add(LocaleController.getString("ActionBotDocumentUtilityBill", R.string.ActionBotDocumentUtilityBill));
            types.add(TLRPC.TL_secureValueTypeUtilityBill.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeBankStatement.class)) {
            values.add(LocaleController.getString("ActionBotDocumentBankStatement", R.string.ActionBotDocumentBankStatement));
            types.add(TLRPC.TL_secureValueTypeBankStatement.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeRentalAgreement.class)) {
            values.add(LocaleController.getString("ActionBotDocumentRentalAgreement", R.string.ActionBotDocumentRentalAgreement));
            types.add(TLRPC.TL_secureValueTypeRentalAgreement.class);
        }
        if (getParentActivity() == null || values.isEmpty()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("PassportNoDocumentsAdd", R.string.PassportNoDocumentsAdd));
        builder.setItems((CharSequence[]) values.toArray(new CharSequence[0]), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda55
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                PassportActivity.this.m4050lambda$openAddDocumentAlert$23$orgtelegramuiPassportActivity(types, dialogInterface, i);
            }
        });
        showDialog(builder.create());
    }

    /* renamed from: lambda$openAddDocumentAlert$23$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4050lambda$openAddDocumentAlert$23$orgtelegramuiPassportActivity(ArrayList types, DialogInterface dialog, int which) {
        TLRPC.TL_secureRequiredType requiredType = null;
        TLRPC.TL_secureRequiredType documentRequiredType = null;
        try {
            requiredType = new TLRPC.TL_secureRequiredType();
            requiredType.type = (TLRPC.SecureValueType) ((Class) types.get(which)).newInstance();
        } catch (Exception e) {
        }
        boolean z = true;
        if (isPersonalDocument(requiredType.type)) {
            documentRequiredType = requiredType;
            documentRequiredType.selfie_required = true;
            documentRequiredType.translation_required = true;
            requiredType = new TLRPC.TL_secureRequiredType();
            requiredType.type = new TLRPC.TL_secureValueTypePersonalDetails();
        } else if (isAddressDocument(requiredType.type)) {
            documentRequiredType = requiredType;
            requiredType = new TLRPC.TL_secureRequiredType();
            requiredType.type = new TLRPC.TL_secureValueTypeAddress();
        }
        ArrayList<TLRPC.TL_secureRequiredType> arrayList = new ArrayList<>();
        if (documentRequiredType == null) {
            z = false;
        }
        openTypeActivity(requiredType, documentRequiredType, arrayList, z);
    }

    private void updateManageVisibility() {
        if (this.currentForm.values.isEmpty()) {
            this.emptyLayout.setVisibility(0);
            this.sectionCell.setVisibility(8);
            this.headerCell.setVisibility(8);
            this.addDocumentCell.setVisibility(8);
            this.deletePassportCell.setVisibility(8);
            this.addDocumentSectionCell.setVisibility(8);
            return;
        }
        this.emptyLayout.setVisibility(8);
        this.sectionCell.setVisibility(0);
        this.headerCell.setVisibility(0);
        this.deletePassportCell.setVisibility(0);
        this.addDocumentSectionCell.setVisibility(0);
        if (hasUnfilledValues()) {
            this.addDocumentCell.setVisibility(0);
        } else {
            this.addDocumentCell.setVisibility(8);
        }
    }

    public void callCallback(boolean success) {
        int i;
        int i2;
        if (!this.callbackCalled) {
            if (!TextUtils.isEmpty(this.currentCallbackUrl)) {
                if (success) {
                    Activity parentActivity = getParentActivity();
                    Browser.openUrl(parentActivity, Uri.parse(this.currentCallbackUrl + "&tg_passport=success"));
                } else if (!this.ignoreOnFailure && ((i2 = this.currentActivityType) == 5 || i2 == 0)) {
                    Activity parentActivity2 = getParentActivity();
                    Browser.openUrl(parentActivity2, Uri.parse(this.currentCallbackUrl + "&tg_passport=cancel"));
                }
                this.callbackCalled = true;
            } else if (this.needActivityResult) {
                if (success || (!this.ignoreOnFailure && ((i = this.currentActivityType) == 5 || i == 0))) {
                    getParentActivity().setResult(success ? -1 : 0);
                }
                this.callbackCalled = true;
            }
        }
    }

    private void createEmailInterface(Context context) {
        this.actionBar.setTitle(LocaleController.getString("PassportEmail", R.string.PassportEmail));
        if (!TextUtils.isEmpty(this.currentEmail)) {
            TextSettingsCell settingsCell1 = new TextSettingsCell(context);
            settingsCell1.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            settingsCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            settingsCell1.setText(LocaleController.formatString("PassportPhoneUseSame", R.string.PassportPhoneUseSame, this.currentEmail), false);
            this.linearLayout2.addView(settingsCell1, LayoutHelper.createLinear(-1, -2));
            settingsCell1.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PassportActivity.this.m4002lambda$createEmailInterface$24$orgtelegramuiPassportActivity(view);
                }
            });
            TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
            this.bottomCell = textInfoPrivacyCell;
            textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            this.bottomCell.setText(LocaleController.getString("PassportPhoneUseSameEmailInfo", R.string.PassportPhoneUseSameEmailInfo));
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        }
        this.inputFields = new EditTextBoldCursor[1];
        for (int a = 0; a < 1; a++) {
            ViewGroup container = new FrameLayout(context);
            this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 50));
            container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.inputFields[a] = new EditTextBoldCursor(context);
            this.inputFields[a].setTag(Integer.valueOf(a));
            this.inputFields[a].setTextSize(1, 16.0f);
            this.inputFields[a].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setBackgroundDrawable(null);
            this.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a].setCursorWidth(1.5f);
            this.inputFields[a].setInputType(33);
            this.inputFields[a].setImeOptions(268435462);
            this.inputFields[a].setHint(LocaleController.getString("PaymentShippingEmailPlaceholder", R.string.PaymentShippingEmailPlaceholder));
            TLRPC.TL_secureValue tL_secureValue = this.currentTypeValue;
            if (tL_secureValue != null && (tL_secureValue.plain_data instanceof TLRPC.TL_securePlainEmail)) {
                TLRPC.TL_securePlainEmail securePlainEmail = (TLRPC.TL_securePlainEmail) this.currentTypeValue.plain_data;
                if (!TextUtils.isEmpty(securePlainEmail.email)) {
                    this.inputFields[a].setText(securePlainEmail.email);
                }
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[a].setSelection(editTextBoldCursorArr[a].length());
            this.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            this.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
            container.addView(this.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 21.0f, 12.0f, 21.0f, 6.0f));
            this.inputFields[a].setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda34
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return PassportActivity.this.m4003lambda$createEmailInterface$25$orgtelegramuiPassportActivity(textView, i, keyEvent);
                }
            });
        }
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context);
        this.bottomCell = textInfoPrivacyCell2;
        textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.bottomCell.setText(LocaleController.getString("PassportEmailUploadInfo", R.string.PassportEmailUploadInfo));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
    }

    /* renamed from: lambda$createEmailInterface$24$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4002lambda$createEmailInterface$24$orgtelegramuiPassportActivity(View v) {
        this.useCurrentValue = true;
        this.doneItem.callOnClick();
        this.useCurrentValue = false;
    }

    /* renamed from: lambda$createEmailInterface$25$org-telegram-ui-PassportActivity */
    public /* synthetic */ boolean m4003lambda$createEmailInterface$25$orgtelegramuiPassportActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 6 || i == 5) {
            this.doneItem.callOnClick();
            return true;
        }
        return false;
    }

    private void createPhoneInterface(Context context) {
        String countryName;
        ViewGroup container;
        this.actionBar.setTitle(LocaleController.getString("PassportPhone", R.string.PassportPhone));
        this.languageMap = new HashMap<>();
        int i = 3;
        int i2 = 1;
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
                this.languageMap.put(args[1], args[2]);
            }
            reader.close();
        } catch (Exception e) {
            FileLog.e(e);
        }
        Collections.sort(this.countriesArray, CountrySelectActivity$CountryAdapter$$ExternalSyntheticLambda0.INSTANCE);
        String currentPhone = UserConfig.getInstance(this.currentAccount).getCurrentUser().phone;
        TextSettingsCell settingsCell1 = new TextSettingsCell(context);
        settingsCell1.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
        settingsCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        PhoneFormat phoneFormat = PhoneFormat.getInstance();
        settingsCell1.setText(LocaleController.formatString("PassportPhoneUseSame", R.string.PassportPhoneUseSame, phoneFormat.format("+" + currentPhone)), false);
        int i3 = -1;
        this.linearLayout2.addView(settingsCell1, LayoutHelper.createLinear(-1, -2));
        settingsCell1.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda19
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PassportActivity.this.m4033lambda$createPhoneInterface$26$orgtelegramuiPassportActivity(view);
            }
        });
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
        this.bottomCell = textInfoPrivacyCell;
        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.bottomCell.setText(LocaleController.getString("PassportPhoneUseSameInfo", R.string.PassportPhoneUseSameInfo));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        HeaderCell headerCell = new HeaderCell(context);
        this.headerCell = headerCell;
        headerCell.setText(LocaleController.getString("PassportPhoneUseOther", R.string.PassportPhoneUseOther));
        this.headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
        this.inputFields = new EditTextBoldCursor[3];
        int a = 0;
        while (a < i) {
            if (a == 2) {
                this.inputFields[a] = new HintEditText(context);
            } else {
                this.inputFields[a] = new EditTextBoldCursor(context);
            }
            if (a == i2) {
                container = new LinearLayout(context);
                ((LinearLayout) container).setOrientation(0);
                this.linearLayout2.addView(container, LayoutHelper.createLinear(i3, 50));
                container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (a == 2) {
                container = (ViewGroup) this.inputFields[i2].getParent();
            } else {
                container = new FrameLayout(context);
                this.linearLayout2.addView(container, LayoutHelper.createLinear(i3, 50));
                container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            this.inputFields[a].setTag(Integer.valueOf(a));
            this.inputFields[a].setTextSize(i2, 16.0f);
            this.inputFields[a].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setBackgroundDrawable(null);
            this.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a].setCursorWidth(1.5f);
            if (a == 0) {
                this.inputFields[a].setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda30
                    @Override // android.view.View.OnTouchListener
                    public final boolean onTouch(View view, MotionEvent motionEvent) {
                        return PassportActivity.this.m4036lambda$createPhoneInterface$29$orgtelegramuiPassportActivity(view, motionEvent);
                    }
                });
                this.inputFields[a].setText(LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
                this.inputFields[a].setInputType(0);
                this.inputFields[a].setFocusable(false);
            } else {
                this.inputFields[a].setInputType(i);
                if (a == 2) {
                    this.inputFields[a].setImeOptions(268435462);
                } else {
                    this.inputFields[a].setImeOptions(268435461);
                }
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[a].setSelection(editTextBoldCursorArr[a].length());
            int i4 = 5;
            if (a == 1) {
                TextView textView = new TextView(context);
                this.plusTextView = textView;
                textView.setText("+");
                this.plusTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                this.plusTextView.setTextSize(1, 16.0f);
                container.addView(this.plusTextView, LayoutHelper.createLinear(-2, -2, 21.0f, 12.0f, 0.0f, 6.0f));
                this.inputFields[a].setPadding(AndroidUtilities.dp(10.0f), 0, 0, 0);
                InputFilter[] inputFilters = {new InputFilter.LengthFilter(5)};
                this.inputFields[a].setFilters(inputFilters);
                this.inputFields[a].setGravity(19);
                container.addView(this.inputFields[a], LayoutHelper.createLinear(55, -2, 0.0f, 12.0f, 16.0f, 6.0f));
                this.inputFields[a].addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.PassportActivity.9
                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence charSequence, int i5, int i22, int i32) {
                    }

                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence charSequence, int i5, int i22, int i32) {
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable editable) {
                        int index;
                        if (!PassportActivity.this.ignoreOnTextChange) {
                            PassportActivity.this.ignoreOnTextChange = true;
                            String text = PhoneFormat.stripExceptNumbers(PassportActivity.this.inputFields[1].getText().toString());
                            PassportActivity.this.inputFields[1].setText(text);
                            HintEditText phoneField = (HintEditText) PassportActivity.this.inputFields[2];
                            if (text.length() == 0) {
                                phoneField.setHintText((String) null);
                                phoneField.setHint(LocaleController.getString("PaymentShippingPhoneNumber", R.string.PaymentShippingPhoneNumber));
                                PassportActivity.this.inputFields[0].setText(LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
                            } else {
                                boolean ok = false;
                                String textToSet = null;
                                if (text.length() > 4) {
                                    int a2 = 4;
                                    while (true) {
                                        if (a2 < 1) {
                                            break;
                                        }
                                        String sub = text.substring(0, a2);
                                        if (((String) PassportActivity.this.codesMap.get(sub)) == null) {
                                            a2--;
                                        } else {
                                            ok = true;
                                            textToSet = text.substring(a2) + PassportActivity.this.inputFields[2].getText().toString();
                                            text = sub;
                                            PassportActivity.this.inputFields[1].setText(sub);
                                            break;
                                        }
                                    }
                                    if (!ok) {
                                        textToSet = text.substring(1) + PassportActivity.this.inputFields[2].getText().toString();
                                        EditTextBoldCursor editTextBoldCursor = PassportActivity.this.inputFields[1];
                                        String substring = text.substring(0, 1);
                                        text = substring;
                                        editTextBoldCursor.setText(substring);
                                    }
                                }
                                String country = (String) PassportActivity.this.codesMap.get(text);
                                boolean set = false;
                                if (country != null && (index = PassportActivity.this.countriesArray.indexOf(country)) != -1) {
                                    PassportActivity.this.inputFields[0].setText((CharSequence) PassportActivity.this.countriesArray.get(index));
                                    String hint = (String) PassportActivity.this.phoneFormatMap.get(text);
                                    set = true;
                                    if (hint != null) {
                                        phoneField.setHintText(hint.replace('X', (char) 8211));
                                        phoneField.setHint((CharSequence) null);
                                    }
                                }
                                if (!set) {
                                    phoneField.setHintText((String) null);
                                    phoneField.setHint(LocaleController.getString("PaymentShippingPhoneNumber", R.string.PaymentShippingPhoneNumber));
                                    PassportActivity.this.inputFields[0].setText(LocaleController.getString("WrongCountry", R.string.WrongCountry));
                                }
                                if (!ok) {
                                    PassportActivity.this.inputFields[1].setSelection(PassportActivity.this.inputFields[1].getText().length());
                                }
                                if (textToSet != null) {
                                    phoneField.requestFocus();
                                    phoneField.setText(textToSet);
                                    phoneField.setSelection(phoneField.length());
                                }
                            }
                            PassportActivity.this.ignoreOnTextChange = false;
                        }
                    }
                });
            } else if (a == 2) {
                this.inputFields[a].setPadding(0, 0, 0, 0);
                this.inputFields[a].setGravity(19);
                this.inputFields[a].setHintText(null);
                this.inputFields[a].setHint(LocaleController.getString("PaymentShippingPhoneNumber", R.string.PaymentShippingPhoneNumber));
                container.addView(this.inputFields[a], LayoutHelper.createLinear(-1, -2, 0.0f, 12.0f, 21.0f, 6.0f));
                this.inputFields[a].addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.PassportActivity.10
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
                        int i5;
                        int i6;
                        if (!PassportActivity.this.ignoreOnPhoneChange) {
                            HintEditText phoneField = (HintEditText) PassportActivity.this.inputFields[2];
                            int start = phoneField.getSelectionStart();
                            String str = phoneField.getText().toString();
                            if (this.characterAction == 3) {
                                str = str.substring(0, this.actionPosition) + str.substring(this.actionPosition + 1);
                                start--;
                            }
                            StringBuilder builder = new StringBuilder(str.length());
                            for (int a2 = 0; a2 < str.length(); a2++) {
                                String ch = str.substring(a2, a2 + 1);
                                if ("0123456789".contains(ch)) {
                                    builder.append(ch);
                                }
                            }
                            PassportActivity.this.ignoreOnPhoneChange = true;
                            String hint = phoneField.getHintText();
                            if (hint != null) {
                                int a3 = 0;
                                while (true) {
                                    if (a3 >= builder.length()) {
                                        break;
                                    } else if (a3 < hint.length()) {
                                        if (hint.charAt(a3) == ' ') {
                                            builder.insert(a3, ' ');
                                            a3++;
                                            if (start == a3 && (i6 = this.characterAction) != 2 && i6 != 3) {
                                                start++;
                                            }
                                        }
                                        a3++;
                                    } else {
                                        builder.insert(a3, ' ');
                                        if (start == a3 + 1 && (i5 = this.characterAction) != 2 && i5 != 3) {
                                            start++;
                                        }
                                    }
                                }
                            }
                            phoneField.setText(builder);
                            if (start >= 0) {
                                phoneField.setSelection(Math.min(start, phoneField.length()));
                            }
                            phoneField.onTextChange();
                            PassportActivity.this.ignoreOnPhoneChange = false;
                        }
                    }
                });
            } else {
                this.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
                EditTextBoldCursor editTextBoldCursor = this.inputFields[a];
                if (!LocaleController.isRTL) {
                    i4 = 3;
                }
                editTextBoldCursor.setGravity(i4);
                container.addView(this.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 21.0f, 12.0f, 21.0f, 6.0f));
            }
            this.inputFields[a].setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda39
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView2, int i5, KeyEvent keyEvent) {
                    return PassportActivity.this.m4037lambda$createPhoneInterface$30$orgtelegramuiPassportActivity(textView2, i5, keyEvent);
                }
            });
            if (a == 2) {
                this.inputFields[a].setOnKeyListener(new View.OnKeyListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda25
                    @Override // android.view.View.OnKeyListener
                    public final boolean onKey(View view, int i5, KeyEvent keyEvent) {
                        return PassportActivity.this.m4038lambda$createPhoneInterface$31$orgtelegramuiPassportActivity(view, i5, keyEvent);
                    }
                });
            }
            if (a == 0) {
                View divider = new View(context);
                this.dividers.add(divider);
                divider.setBackgroundColor(Theme.getColor(Theme.key_divider));
                container.addView(divider, new FrameLayout.LayoutParams(-1, 1, 83));
            }
            a++;
            i = 3;
            i2 = 1;
            i3 = -1;
        }
        String country = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            if (telephonyManager != null) {
                country = telephonyManager.getSimCountryIso().toUpperCase();
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        if (country != null && (countryName = this.languageMap.get(country)) != null) {
            int index = this.countriesArray.indexOf(countryName);
            if (index != -1) {
                this.inputFields[1].setText(this.countriesMap.get(countryName));
            }
        }
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context);
        this.bottomCell = textInfoPrivacyCell2;
        textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.bottomCell.setText(LocaleController.getString("PassportPhoneUploadInfo", R.string.PassportPhoneUploadInfo));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
    }

    /* renamed from: lambda$createPhoneInterface$26$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4033lambda$createPhoneInterface$26$orgtelegramuiPassportActivity(View v) {
        this.useCurrentValue = true;
        this.doneItem.callOnClick();
        this.useCurrentValue = false;
    }

    /* renamed from: lambda$createPhoneInterface$29$org-telegram-ui-PassportActivity */
    public /* synthetic */ boolean m4036lambda$createPhoneInterface$29$orgtelegramuiPassportActivity(View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            CountrySelectActivity fragment = new CountrySelectActivity(false);
            fragment.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda68
                @Override // org.telegram.ui.CountrySelectActivity.CountrySelectActivityDelegate
                public final void didSelectCountry(CountrySelectActivity.Country country) {
                    PassportActivity.this.m4035lambda$createPhoneInterface$28$orgtelegramuiPassportActivity(country);
                }
            });
            presentFragment(fragment);
        }
        return true;
    }

    /* renamed from: lambda$createPhoneInterface$28$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4035lambda$createPhoneInterface$28$orgtelegramuiPassportActivity(CountrySelectActivity.Country country) {
        this.inputFields[0].setText(country.name);
        int index = this.countriesArray.indexOf(country.name);
        if (index != -1) {
            this.ignoreOnTextChange = true;
            String code = this.countriesMap.get(country.name);
            this.inputFields[1].setText(code);
            String hint = this.phoneFormatMap.get(code);
            this.inputFields[2].setHintText(hint != null ? hint.replace('X', (char) 8211) : null);
            this.ignoreOnTextChange = false;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda43
            @Override // java.lang.Runnable
            public final void run() {
                PassportActivity.this.m4034lambda$createPhoneInterface$27$orgtelegramuiPassportActivity();
            }
        }, 300L);
        this.inputFields[2].requestFocus();
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
        editTextBoldCursorArr[2].setSelection(editTextBoldCursorArr[2].length());
    }

    /* renamed from: lambda$createPhoneInterface$27$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4034lambda$createPhoneInterface$27$orgtelegramuiPassportActivity() {
        AndroidUtilities.showKeyboard(this.inputFields[2]);
    }

    /* renamed from: lambda$createPhoneInterface$30$org-telegram-ui-PassportActivity */
    public /* synthetic */ boolean m4037lambda$createPhoneInterface$30$orgtelegramuiPassportActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            this.inputFields[2].requestFocus();
            return true;
        } else if (i == 6) {
            this.doneItem.callOnClick();
            return true;
        } else {
            return false;
        }
    }

    /* renamed from: lambda$createPhoneInterface$31$org-telegram-ui-PassportActivity */
    public /* synthetic */ boolean m4038lambda$createPhoneInterface$31$orgtelegramuiPassportActivity(View v, int keyCode, KeyEvent event) {
        if (keyCode == 67 && this.inputFields[2].length() == 0) {
            this.inputFields[1].requestFocus();
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[1].setSelection(editTextBoldCursorArr[1].length());
            this.inputFields[1].dispatchKeyEvent(event);
            return true;
        }
        return false;
    }

    private void createAddressInterface(Context context) {
        final String key;
        String errorText;
        String errorText2;
        this.languageMap = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("countries.txt")));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                String[] args = line.split(";");
                this.languageMap.put(args[1], args[2]);
            }
            reader.close();
        } catch (Exception e) {
            FileLog.e(e);
        }
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
        this.topErrorCell = textInfoPrivacyCell;
        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_top, Theme.key_windowBackgroundGrayShadow));
        this.topErrorCell.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        int i = -2;
        this.linearLayout2.addView(this.topErrorCell, LayoutHelper.createLinear(-1, -2));
        checkTopErrorCell(true);
        TLRPC.TL_secureRequiredType tL_secureRequiredType = this.currentDocumentsType;
        if (tL_secureRequiredType != null) {
            if (tL_secureRequiredType.type instanceof TLRPC.TL_secureValueTypeRentalAgreement) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentRentalAgreement", R.string.ActionBotDocumentRentalAgreement));
            } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeBankStatement) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentBankStatement", R.string.ActionBotDocumentBankStatement));
            } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeUtilityBill) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentUtilityBill", R.string.ActionBotDocumentUtilityBill));
            } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypePassportRegistration) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentPassportRegistration", R.string.ActionBotDocumentPassportRegistration));
            } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeTemporaryRegistration) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentTemporaryRegistration", R.string.ActionBotDocumentTemporaryRegistration));
            }
            HeaderCell headerCell = new HeaderCell(context);
            this.headerCell = headerCell;
            headerCell.setText(LocaleController.getString("PassportDocuments", R.string.PassportDocuments));
            this.headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
            LinearLayout linearLayout = new LinearLayout(context);
            this.documentsLayout = linearLayout;
            linearLayout.setOrientation(1);
            this.linearLayout2.addView(this.documentsLayout, LayoutHelper.createLinear(-1, -2));
            TextSettingsCell textSettingsCell = new TextSettingsCell(context);
            this.uploadDocumentCell = textSettingsCell;
            textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.linearLayout2.addView(this.uploadDocumentCell, LayoutHelper.createLinear(-1, -2));
            this.uploadDocumentCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PassportActivity.this.m3995x25ed1d36(view);
                }
            });
            TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context);
            this.bottomCell = textInfoPrivacyCell2;
            textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            if (this.currentBotId != 0) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddAddressUploadInfo", R.string.PassportAddAddressUploadInfo);
            } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeRentalAgreement) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddAgreementInfo", R.string.PassportAddAgreementInfo);
            } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeUtilityBill) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddBillInfo", R.string.PassportAddBillInfo);
            } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypePassportRegistration) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddPassportRegistrationInfo", R.string.PassportAddPassportRegistrationInfo);
            } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeTemporaryRegistration) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddTemporaryRegistrationInfo", R.string.PassportAddTemporaryRegistrationInfo);
            } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeBankStatement) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddBankInfo", R.string.PassportAddBankInfo);
            } else {
                this.noAllDocumentsErrorText = "";
            }
            CharSequence text = this.noAllDocumentsErrorText;
            HashMap<String, String> hashMap = this.documentsErrors;
            if (hashMap != null && (errorText2 = hashMap.get("files_all")) != null) {
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(errorText2);
                stringBuilder.append((CharSequence) "\n\n");
                stringBuilder.append(this.noAllDocumentsErrorText);
                text = stringBuilder;
                stringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3)), 0, errorText2.length(), 33);
                this.errorsValues.put("files_all", "");
            }
            this.bottomCell.setText(text);
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
            if (this.currentDocumentsType.translation_required) {
                HeaderCell headerCell2 = new HeaderCell(context);
                this.headerCell = headerCell2;
                headerCell2.setText(LocaleController.getString("PassportTranslation", R.string.PassportTranslation));
                this.headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
                LinearLayout linearLayout2 = new LinearLayout(context);
                this.translationLayout = linearLayout2;
                linearLayout2.setOrientation(1);
                this.linearLayout2.addView(this.translationLayout, LayoutHelper.createLinear(-1, -2));
                TextSettingsCell textSettingsCell2 = new TextSettingsCell(context);
                this.uploadTranslationCell = textSettingsCell2;
                textSettingsCell2.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                this.linearLayout2.addView(this.uploadTranslationCell, LayoutHelper.createLinear(-1, -2));
                this.uploadTranslationCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda4
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        PassportActivity.this.m3996x630ce155(view);
                    }
                });
                TextInfoPrivacyCell textInfoPrivacyCell3 = new TextInfoPrivacyCell(context);
                this.bottomCellTranslation = textInfoPrivacyCell3;
                textInfoPrivacyCell3.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                if (this.currentBotId != 0) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationUploadInfo", R.string.PassportAddTranslationUploadInfo);
                } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeRentalAgreement) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationAgreementInfo", R.string.PassportAddTranslationAgreementInfo);
                } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeUtilityBill) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationBillInfo", R.string.PassportAddTranslationBillInfo);
                } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypePassportRegistration) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationPassportRegistrationInfo", R.string.PassportAddTranslationPassportRegistrationInfo);
                } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeTemporaryRegistration) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationTemporaryRegistrationInfo", R.string.PassportAddTranslationTemporaryRegistrationInfo);
                } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeBankStatement) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationBankInfo", R.string.PassportAddTranslationBankInfo);
                } else {
                    this.noAllTranslationErrorText = "";
                }
                CharSequence text2 = this.noAllTranslationErrorText;
                HashMap<String, String> hashMap2 = this.documentsErrors;
                if (hashMap2 != null && (errorText = hashMap2.get("translation_all")) != null) {
                    SpannableStringBuilder stringBuilder2 = new SpannableStringBuilder(errorText);
                    stringBuilder2.append((CharSequence) "\n\n");
                    stringBuilder2.append(this.noAllTranslationErrorText);
                    text2 = stringBuilder2;
                    stringBuilder2.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3)), 0, errorText.length(), 33);
                    this.errorsValues.put("translation_all", "");
                }
                this.bottomCellTranslation.setText(text2);
                this.linearLayout2.addView(this.bottomCellTranslation, LayoutHelper.createLinear(-1, -2));
            }
        } else {
            this.actionBar.setTitle(LocaleController.getString("PassportAddress", R.string.PassportAddress));
        }
        HeaderCell headerCell3 = new HeaderCell(context);
        this.headerCell = headerCell3;
        headerCell3.setText(LocaleController.getString("PassportAddressHeader", R.string.PassportAddressHeader));
        this.headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
        this.inputFields = new EditTextBoldCursor[6];
        int a = 0;
        while (a < 6) {
            final EditTextBoldCursor field = new EditTextBoldCursor(context);
            this.inputFields[a] = field;
            ViewGroup container = new FrameLayout(context) { // from class: org.telegram.ui.PassportActivity.11
                private StaticLayout errorLayout;
                float offsetX;

                @Override // android.widget.FrameLayout, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int width = View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(34.0f);
                    StaticLayout errorLayout = field.getErrorLayout(width);
                    this.errorLayout = errorLayout;
                    if (errorLayout != null) {
                        int lineCount = errorLayout.getLineCount();
                        if (lineCount > 1) {
                            int height = AndroidUtilities.dp(64.0f) + (this.errorLayout.getLineBottom(lineCount - 1) - this.errorLayout.getLineBottom(0));
                            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED);
                        }
                        if (LocaleController.isRTL) {
                            float maxW = 0.0f;
                            int a2 = 0;
                            while (true) {
                                if (a2 >= lineCount) {
                                    break;
                                }
                                float l = this.errorLayout.getLineLeft(a2);
                                if (l != 0.0f) {
                                    this.offsetX = 0.0f;
                                    break;
                                }
                                maxW = Math.max(maxW, this.errorLayout.getLineWidth(a2));
                                if (a2 == lineCount - 1) {
                                    this.offsetX = width - maxW;
                                }
                                a2++;
                            }
                        }
                    }
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }

                @Override // android.view.View
                protected void onDraw(Canvas canvas) {
                    if (this.errorLayout != null) {
                        canvas.save();
                        canvas.translate(AndroidUtilities.dp(21.0f) + this.offsetX, field.getLineY() + AndroidUtilities.dp(3.0f));
                        this.errorLayout.draw(canvas);
                        canvas.restore();
                    }
                }
            };
            container.setWillNotDraw(false);
            this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, i));
            container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            int i2 = 5;
            if (a == 5) {
                View view = new View(context);
                this.extraBackgroundView = view;
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                this.linearLayout2.addView(this.extraBackgroundView, LayoutHelper.createLinear(-1, 6));
            }
            if (this.documentOnly && this.currentDocumentsType != null) {
                container.setVisibility(8);
                View view2 = this.extraBackgroundView;
                if (view2 != null) {
                    view2.setVisibility(8);
                }
            }
            this.inputFields[a].setTag(Integer.valueOf(a));
            this.inputFields[a].setSupportRtlHint(true);
            this.inputFields[a].setTextSize(1, 16.0f);
            this.inputFields[a].setHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.inputFields[a].setHeaderHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
            this.inputFields[a].setTransformHintToHeader(true);
            this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setBackgroundDrawable(null);
            this.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a].setCursorWidth(1.5f);
            this.inputFields[a].setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated), Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
            if (a == 5) {
                this.inputFields[a].setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda27
                    @Override // android.view.View.OnTouchListener
                    public final boolean onTouch(View view3, MotionEvent motionEvent) {
                        return PassportActivity.this.m3998xdd4c6993(view3, motionEvent);
                    }
                });
                this.inputFields[a].setInputType(0);
                this.inputFields[a].setFocusable(false);
            } else {
                this.inputFields[a].setInputType(16385);
                this.inputFields[a].setImeOptions(268435461);
            }
            switch (a) {
                case 0:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportStreet1", R.string.PassportStreet1));
                    key = "street_line1";
                    break;
                case 1:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportStreet2", R.string.PassportStreet2));
                    key = "street_line2";
                    break;
                case 2:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportPostcode", R.string.PassportPostcode));
                    key = "post_code";
                    break;
                case 3:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportCity", R.string.PassportCity));
                    key = "city";
                    break;
                case 4:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportState", R.string.PassportState));
                    key = RemoteConfigConstants.ResponseFieldKey.STATE;
                    break;
                case 5:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportCountry", R.string.PassportCountry));
                    key = "country_code";
                    break;
                default:
                    continue;
                    a++;
                    i = -2;
            }
            setFieldValues(this.currentValues, this.inputFields[a], key);
            if (a == 2) {
                this.inputFields[a].addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.PassportActivity.12
                    private boolean ignore;

                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable s) {
                        if (this.ignore) {
                            return;
                        }
                        this.ignore = true;
                        boolean error = false;
                        int a2 = 0;
                        while (true) {
                            if (a2 >= s.length()) {
                                break;
                            }
                            char ch = s.charAt(a2);
                            if ((ch >= 'a' && ch <= 'z') || ((ch >= 'A' && ch <= 'Z') || ((ch >= '0' && ch <= '9') || ch == '-' || ch == ' '))) {
                                a2++;
                            } else {
                                error = true;
                                break;
                            }
                        }
                        this.ignore = false;
                        if (!error) {
                            PassportActivity.this.checkFieldForError(field, key, s, false);
                        } else {
                            field.setErrorText(LocaleController.getString("PassportUseLatinOnly", R.string.PassportUseLatinOnly));
                        }
                    }
                });
                InputFilter[] inputFilters = {new InputFilter.LengthFilter(10)};
                this.inputFields[a].setFilters(inputFilters);
            } else {
                this.inputFields[a].addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.PassportActivity.13
                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable s) {
                        PassportActivity.this.checkFieldForError(field, key, s, false);
                    }
                });
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[a].setSelection(editTextBoldCursorArr[a].length());
            this.inputFields[a].setPadding(0, 0, 0, 0);
            EditTextBoldCursor editTextBoldCursor = this.inputFields[a];
            if (!LocaleController.isRTL) {
                i2 = 3;
            }
            editTextBoldCursor.setGravity(i2 | 16);
            container.addView(this.inputFields[a], LayoutHelper.createFrame(-1, 64.0f, 51, 21.0f, 0.0f, 21.0f, 0.0f));
            this.inputFields[a].setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda32
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView, int i3, KeyEvent keyEvent) {
                    return PassportActivity.this.m3999x1a6c2db2(textView, i3, keyEvent);
                }
            });
            a++;
            i = -2;
        }
        ShadowSectionCell shadowSectionCell = new ShadowSectionCell(context);
        this.sectionCell = shadowSectionCell;
        this.linearLayout2.addView(shadowSectionCell, LayoutHelper.createLinear(-1, -2));
        if (this.documentOnly && this.currentDocumentsType != null) {
            this.headerCell.setVisibility(8);
            this.sectionCell.setVisibility(8);
        }
        if (((this.currentBotId != 0 || this.currentDocumentsType == null) && this.currentTypeValue != null && !this.documentOnly) || this.currentDocumentsTypeValue != null) {
            TLRPC.TL_secureValue tL_secureValue = this.currentDocumentsTypeValue;
            if (tL_secureValue != null) {
                addDocumentViews(tL_secureValue.files);
                addTranslationDocumentViews(this.currentDocumentsTypeValue.translation);
            }
            this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            TextSettingsCell settingsCell1 = new TextSettingsCell(context);
            settingsCell1.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
            settingsCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            if (this.currentDocumentsType == null) {
                settingsCell1.setText(LocaleController.getString("PassportDeleteInfo", R.string.PassportDeleteInfo), false);
            } else {
                settingsCell1.setText(LocaleController.getString("PassportDeleteDocument", R.string.PassportDeleteDocument), false);
            }
            this.linearLayout2.addView(settingsCell1, LayoutHelper.createLinear(-1, -2));
            settingsCell1.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda5
                @Override // android.view.View.OnClickListener
                public final void onClick(View view3) {
                    PassportActivity.this.m4000x578bf1d1(view3);
                }
            });
            ShadowSectionCell shadowSectionCell2 = new ShadowSectionCell(context);
            this.sectionCell = shadowSectionCell2;
            shadowSectionCell2.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
        } else {
            this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            if (this.documentOnly && this.currentDocumentsType != null) {
                this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            }
        }
        updateUploadText(0);
        updateUploadText(4);
    }

    /* renamed from: lambda$createAddressInterface$32$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m3995x25ed1d36(View v) {
        this.uploadingFileType = 0;
        openAttachMenu();
    }

    /* renamed from: lambda$createAddressInterface$33$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m3996x630ce155(View v) {
        this.uploadingFileType = 4;
        openAttachMenu();
    }

    /* renamed from: lambda$createAddressInterface$35$org-telegram-ui-PassportActivity */
    public /* synthetic */ boolean m3998xdd4c6993(View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            CountrySelectActivity fragment = new CountrySelectActivity(false);
            fragment.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda67
                @Override // org.telegram.ui.CountrySelectActivity.CountrySelectActivityDelegate
                public final void didSelectCountry(CountrySelectActivity.Country country) {
                    PassportActivity.this.m3997xa02ca574(country);
                }
            });
            presentFragment(fragment);
        }
        return true;
    }

    /* renamed from: lambda$createAddressInterface$34$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m3997xa02ca574(CountrySelectActivity.Country country) {
        this.inputFields[5].setText(country.name);
        this.currentCitizeship = country.shortname;
    }

    /* renamed from: lambda$createAddressInterface$36$org-telegram-ui-PassportActivity */
    public /* synthetic */ boolean m3999x1a6c2db2(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            int num = ((Integer) textView.getTag()).intValue() + 1;
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            if (num < editTextBoldCursorArr.length) {
                if (editTextBoldCursorArr[num].isFocusable()) {
                    this.inputFields[num].requestFocus();
                } else {
                    this.inputFields[num].dispatchTouchEvent(MotionEvent.obtain(0L, 0L, 1, 0.0f, 0.0f, 0));
                    textView.clearFocus();
                    AndroidUtilities.hideKeyboard(textView);
                }
            }
            return true;
        }
        return false;
    }

    /* renamed from: lambda$createAddressInterface$37$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4000x578bf1d1(View v) {
        createDocumentDeleteAlert();
    }

    private void createDocumentDeleteAlert() {
        final boolean[] checks = {true};
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda2
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                PassportActivity.this.m4001xb5d97d4f(checks, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        if (this.documentOnly && this.currentDocumentsType == null && (this.currentType.type instanceof TLRPC.TL_secureValueTypeAddress)) {
            builder.setMessage(LocaleController.getString("PassportDeleteAddressAlert", R.string.PassportDeleteAddressAlert));
        } else if (this.documentOnly && this.currentDocumentsType == null && (this.currentType.type instanceof TLRPC.TL_secureValueTypePersonalDetails)) {
            builder.setMessage(LocaleController.getString("PassportDeletePersonalAlert", R.string.PassportDeletePersonalAlert));
        } else {
            builder.setMessage(LocaleController.getString("PassportDeleteDocumentAlert", R.string.PassportDeleteDocumentAlert));
        }
        if (!this.documentOnly && this.currentDocumentsType != null) {
            FrameLayout frameLayout = new FrameLayout(getParentActivity());
            CheckBoxCell cell = new CheckBoxCell(getParentActivity(), 1);
            cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (this.currentType.type instanceof TLRPC.TL_secureValueTypeAddress) {
                cell.setText(LocaleController.getString("PassportDeleteDocumentAddress", R.string.PassportDeleteDocumentAddress), "", true, false);
            } else if (this.currentType.type instanceof TLRPC.TL_secureValueTypePersonalDetails) {
                cell.setText(LocaleController.getString("PassportDeleteDocumentPersonal", R.string.PassportDeleteDocumentPersonal), "", true, false);
            }
            cell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
            frameLayout.addView(cell, LayoutHelper.createFrame(-1, 48, 51));
            cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda24
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PassportActivity.lambda$createDocumentDeleteAlert$39(checks, view);
                }
            });
            builder.setView(frameLayout);
        }
        showDialog(builder.create());
    }

    /* renamed from: lambda$createDocumentDeleteAlert$38$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4001xb5d97d4f(boolean[] checks, DialogInterface dialog, int which) {
        if (!this.documentOnly) {
            this.currentValues.clear();
        }
        this.currentDocumentValues.clear();
        this.delegate.deleteValue(this.currentType, this.currentDocumentsType, this.availableDocumentTypes, checks[0], null, null);
        finishFragment();
    }

    public static /* synthetic */ void lambda$createDocumentDeleteAlert$39(boolean[] checks, View v) {
        if (!v.isEnabled()) {
            return;
        }
        CheckBoxCell cell1 = (CheckBoxCell) v;
        checks[0] = !checks[0];
        cell1.setChecked(checks[0], true);
    }

    public void onFieldError(View field) {
        if (field == null) {
            return;
        }
        Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
        if (v != null) {
            v.vibrate(200L);
        }
        AndroidUtilities.shakeView(field, 2.0f, 0);
        scrollToField(field);
    }

    private void scrollToField(View field) {
        while (field != null && this.linearLayout2.indexOfChild(field) < 0) {
            field = (View) field.getParent();
        }
        if (field != null) {
            this.scrollView.smoothScrollTo(0, field.getTop() - ((this.scrollView.getMeasuredHeight() - field.getMeasuredHeight()) / 2));
        }
    }

    public String getDocumentHash(SecureDocument document) {
        if (document != null) {
            if (document.secureFile != null && document.secureFile.file_hash != null) {
                return Base64.encodeToString(document.secureFile.file_hash, 2);
            }
            if (document.fileHash != null) {
                return Base64.encodeToString(document.fileHash, 2);
            }
            return "";
        }
        return "";
    }

    public void checkFieldForError(EditTextBoldCursor field, String key, Editable s, boolean document) {
        String value;
        String value2;
        String value3;
        HashMap<String, String> hashMap = this.errorsValues;
        if (hashMap != null && (value = hashMap.get(key)) != null) {
            if (TextUtils.equals(value, s)) {
                HashMap<String, String> hashMap2 = this.fieldsErrors;
                if (hashMap2 != null && (value3 = hashMap2.get(key)) != null) {
                    field.setErrorText(value3);
                } else {
                    HashMap<String, String> hashMap3 = this.documentsErrors;
                    if (hashMap3 != null && (value2 = hashMap3.get(key)) != null) {
                        field.setErrorText(value2);
                    }
                }
            } else {
                field.setErrorText(null);
            }
        } else {
            field.setErrorText(null);
        }
        String errorKey = document ? "error_document_all" : "error_all";
        HashMap<String, String> hashMap4 = this.errorsValues;
        if (hashMap4 != null && hashMap4.containsKey(errorKey)) {
            this.errorsValues.remove(errorKey);
            checkTopErrorCell(false);
        }
    }

    public boolean checkFieldsForError() {
        EditTextBoldCursor[] fields;
        String key;
        View view;
        if (this.currentDocumentsType != null) {
            if (this.errorsValues.containsKey("error_all") || this.errorsValues.containsKey("error_document_all")) {
                onFieldError(this.topErrorCell);
                return true;
            }
            if (this.uploadDocumentCell != null) {
                if (this.documents.isEmpty()) {
                    onFieldError(this.uploadDocumentCell);
                    return true;
                }
                int size = this.documents.size();
                for (int a = 0; a < size; a++) {
                    SecureDocument document = this.documents.get(a);
                    String key2 = "files" + getDocumentHash(document);
                    if (key2 != null && this.errorsValues.containsKey(key2)) {
                        onFieldError(this.documentsCells.get(document));
                        return true;
                    }
                }
            }
            if (this.errorsValues.containsKey("files_all") || this.errorsValues.containsKey("translation_all")) {
                onFieldError(this.bottomCell);
                return true;
            }
            View view2 = this.uploadFrontCell;
            if (view2 != null) {
                if (this.frontDocument == null) {
                    onFieldError(view2);
                    return true;
                }
                if (this.errorsValues.containsKey("front" + getDocumentHash(this.frontDocument))) {
                    onFieldError(this.documentsCells.get(this.frontDocument));
                    return true;
                }
            }
            if (((this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeIdentityCard) || (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeDriverLicense)) && (view = this.uploadReverseCell) != null) {
                if (this.reverseDocument == null) {
                    onFieldError(view);
                    return true;
                }
                if (this.errorsValues.containsKey("reverse" + getDocumentHash(this.reverseDocument))) {
                    onFieldError(this.documentsCells.get(this.reverseDocument));
                    return true;
                }
            }
            View view3 = this.uploadSelfieCell;
            if (view3 != null && this.currentBotId != 0) {
                if (this.selfieDocument == null) {
                    onFieldError(view3);
                    return true;
                }
                if (this.errorsValues.containsKey("selfie" + getDocumentHash(this.selfieDocument))) {
                    onFieldError(this.documentsCells.get(this.selfieDocument));
                    return true;
                }
            }
            if (this.uploadTranslationCell != null && this.currentBotId != 0) {
                if (this.translationDocuments.isEmpty()) {
                    onFieldError(this.uploadTranslationCell);
                    return true;
                }
                int size2 = this.translationDocuments.size();
                for (int a2 = 0; a2 < size2; a2++) {
                    SecureDocument document2 = this.translationDocuments.get(a2);
                    if (this.errorsValues.containsKey("translation" + getDocumentHash(document2))) {
                        onFieldError(this.documentsCells.get(document2));
                        return true;
                    }
                }
            }
        }
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                fields = this.inputFields;
            } else {
                TextInfoPrivacyCell textInfoPrivacyCell = this.nativeInfoCell;
                fields = (textInfoPrivacyCell == null || textInfoPrivacyCell.getVisibility() != 0) ? null : this.inputExtraFields;
            }
            if (fields != null) {
                for (int a3 = 0; a3 < fields.length; a3++) {
                    boolean error = false;
                    if (fields[a3].hasErrorText()) {
                        error = true;
                    }
                    if (!this.errorsValues.isEmpty()) {
                        if (this.currentType.type instanceof TLRPC.TL_secureValueTypePersonalDetails) {
                            if (i == 0) {
                                switch (a3) {
                                    case 0:
                                        key = "first_name";
                                        break;
                                    case 1:
                                        key = "middle_name";
                                        break;
                                    case 2:
                                        key = "last_name";
                                        break;
                                    case 3:
                                        key = "birth_date";
                                        break;
                                    case 4:
                                        key = "gender";
                                        break;
                                    case 5:
                                        key = "country_code";
                                        break;
                                    case 6:
                                        key = "residence_country_code";
                                        break;
                                    case 7:
                                        key = "document_no";
                                        break;
                                    case 8:
                                        key = "expiry_date";
                                        break;
                                    default:
                                        key = null;
                                        break;
                                }
                            } else {
                                switch (a3) {
                                    case 0:
                                        key = "first_name_native";
                                        break;
                                    case 1:
                                        key = "middle_name_native";
                                        break;
                                    case 2:
                                        key = "last_name_native";
                                        break;
                                    default:
                                        key = null;
                                        break;
                                }
                            }
                        } else if (this.currentType.type instanceof TLRPC.TL_secureValueTypeAddress) {
                            switch (a3) {
                                case 0:
                                    key = "street_line1";
                                    break;
                                case 1:
                                    key = "street_line2";
                                    break;
                                case 2:
                                    key = "post_code";
                                    break;
                                case 3:
                                    key = "city";
                                    break;
                                case 4:
                                    key = RemoteConfigConstants.ResponseFieldKey.STATE;
                                    break;
                                case 5:
                                    key = "country_code";
                                    break;
                                default:
                                    key = null;
                                    break;
                            }
                        } else {
                            key = null;
                        }
                        if (key != null) {
                            String value = this.errorsValues.get(key);
                            if (!TextUtils.isEmpty(value) && value.equals(fields[a3].getText().toString())) {
                                error = true;
                            }
                        }
                    }
                    if (!this.documentOnly || this.currentDocumentsType == null || a3 >= 7) {
                        if (!error) {
                            int len = fields[a3].length();
                            boolean allowZeroLength = false;
                            int i2 = this.currentActivityType;
                            if (i2 == 1) {
                                if (a3 != 8) {
                                    if ((i == 0 && (a3 == 0 || a3 == 2 || a3 == 1)) || (i == 1 && (a3 == 0 || a3 == 1 || a3 == 2))) {
                                        if (len > 255) {
                                            error = true;
                                        }
                                        if ((i == 0 && a3 == 1) || (i == 1 && a3 == 1)) {
                                            allowZeroLength = true;
                                        }
                                    } else if (a3 == 7 && len > 24) {
                                        error = true;
                                    }
                                    if (!error && !allowZeroLength && len == 0) {
                                        error = true;
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                if (i2 == 2) {
                                    if (a3 == 1) {
                                        continue;
                                    } else if (a3 == 3) {
                                        if (len < 2) {
                                            error = true;
                                        }
                                    } else if (a3 == 4) {
                                        if (!"US".equals(this.currentCitizeship)) {
                                            continue;
                                        } else if (len < 2) {
                                            error = true;
                                        }
                                    } else if (a3 == 2 && (len < 2 || len > 10)) {
                                        error = true;
                                    }
                                }
                                if (!error) {
                                    error = true;
                                }
                            }
                        }
                        if (error) {
                            onFieldError(fields[a3]);
                            return true;
                        }
                    }
                }
                continue;
            }
        }
        return false;
    }

    private void createIdentityInterface(final Context context) {
        HashMap<String, String> values;
        final String key;
        int count;
        final HashMap<String, String> values2;
        final String key2;
        String errorText;
        this.languageMap = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("countries.txt")));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                String[] args = line.split(";");
                this.languageMap.put(args[1], args[2]);
            }
            reader.close();
        } catch (Exception e) {
            FileLog.e(e);
        }
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
        this.topErrorCell = textInfoPrivacyCell;
        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_top, Theme.key_windowBackgroundGrayShadow));
        this.topErrorCell.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.linearLayout2.addView(this.topErrorCell, LayoutHelper.createLinear(-1, -2));
        checkTopErrorCell(true);
        if (this.currentDocumentsType != null) {
            HeaderCell headerCell = new HeaderCell(context);
            this.headerCell = headerCell;
            if (this.documentOnly) {
                headerCell.setText(LocaleController.getString("PassportDocuments", R.string.PassportDocuments));
            } else {
                headerCell.setText(LocaleController.getString("PassportRequiredDocuments", R.string.PassportRequiredDocuments));
            }
            this.headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
            LinearLayout linearLayout = new LinearLayout(context);
            this.frontLayout = linearLayout;
            linearLayout.setOrientation(1);
            this.linearLayout2.addView(this.frontLayout, LayoutHelper.createLinear(-1, -2));
            TextDetailSettingsCell textDetailSettingsCell = new TextDetailSettingsCell(context);
            this.uploadFrontCell = textDetailSettingsCell;
            textDetailSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.linearLayout2.addView(this.uploadFrontCell, LayoutHelper.createLinear(-1, -2));
            this.uploadFrontCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda7
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PassportActivity.this.m4005xbcbc071d(view);
                }
            });
            LinearLayout linearLayout2 = new LinearLayout(context);
            this.reverseLayout = linearLayout2;
            linearLayout2.setOrientation(1);
            this.linearLayout2.addView(this.reverseLayout, LayoutHelper.createLinear(-1, -2));
            boolean divider = this.currentDocumentsType.selfie_required;
            TextDetailSettingsCell textDetailSettingsCell2 = new TextDetailSettingsCell(context);
            this.uploadReverseCell = textDetailSettingsCell2;
            textDetailSettingsCell2.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.uploadReverseCell.setTextAndValue(LocaleController.getString("PassportReverseSide", R.string.PassportReverseSide), LocaleController.getString("PassportReverseSideInfo", R.string.PassportReverseSideInfo), divider);
            this.linearLayout2.addView(this.uploadReverseCell, LayoutHelper.createLinear(-1, -2));
            this.uploadReverseCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda8
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PassportActivity.this.m4006xf9dbcb3c(view);
                }
            });
            if (this.currentDocumentsType.selfie_required) {
                LinearLayout linearLayout3 = new LinearLayout(context);
                this.selfieLayout = linearLayout3;
                linearLayout3.setOrientation(1);
                this.linearLayout2.addView(this.selfieLayout, LayoutHelper.createLinear(-1, -2));
                TextDetailSettingsCell textDetailSettingsCell3 = new TextDetailSettingsCell(context);
                this.uploadSelfieCell = textDetailSettingsCell3;
                textDetailSettingsCell3.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                this.uploadSelfieCell.setTextAndValue(LocaleController.getString("PassportSelfie", R.string.PassportSelfie), LocaleController.getString("PassportSelfieInfo", R.string.PassportSelfieInfo), this.currentType.translation_required);
                this.linearLayout2.addView(this.uploadSelfieCell, LayoutHelper.createLinear(-1, -2));
                this.uploadSelfieCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda9
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        PassportActivity.this.m4007x36fb8f5b(view);
                    }
                });
            }
            TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context);
            this.bottomCell = textInfoPrivacyCell2;
            textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            this.bottomCell.setText(LocaleController.getString("PassportPersonalUploadInfo", R.string.PassportPersonalUploadInfo));
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
            if (this.currentDocumentsType.translation_required) {
                HeaderCell headerCell2 = new HeaderCell(context);
                this.headerCell = headerCell2;
                headerCell2.setText(LocaleController.getString("PassportTranslation", R.string.PassportTranslation));
                this.headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
                LinearLayout linearLayout4 = new LinearLayout(context);
                this.translationLayout = linearLayout4;
                linearLayout4.setOrientation(1);
                this.linearLayout2.addView(this.translationLayout, LayoutHelper.createLinear(-1, -2));
                TextSettingsCell textSettingsCell = new TextSettingsCell(context);
                this.uploadTranslationCell = textSettingsCell;
                textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                this.linearLayout2.addView(this.uploadTranslationCell, LayoutHelper.createLinear(-1, -2));
                this.uploadTranslationCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda10
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        PassportActivity.this.m4008x741b537a(view);
                    }
                });
                TextInfoPrivacyCell textInfoPrivacyCell3 = new TextInfoPrivacyCell(context);
                this.bottomCellTranslation = textInfoPrivacyCell3;
                textInfoPrivacyCell3.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                if (this.currentBotId != 0) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationUploadInfo", R.string.PassportAddTranslationUploadInfo);
                } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypePassport) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddPassportInfo", R.string.PassportAddPassportInfo);
                } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeInternalPassport) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddInternalPassportInfo", R.string.PassportAddInternalPassportInfo);
                } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeIdentityCard) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddIdentityCardInfo", R.string.PassportAddIdentityCardInfo);
                } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeDriverLicense) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddDriverLicenceInfo", R.string.PassportAddDriverLicenceInfo);
                } else {
                    this.noAllTranslationErrorText = "";
                }
                CharSequence text = this.noAllTranslationErrorText;
                HashMap<String, String> hashMap = this.documentsErrors;
                if (hashMap != null && (errorText = hashMap.get("translation_all")) != null) {
                    SpannableStringBuilder stringBuilder = new SpannableStringBuilder(errorText);
                    stringBuilder.append((CharSequence) "\n\n");
                    stringBuilder.append(this.noAllTranslationErrorText);
                    text = stringBuilder;
                    stringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3)), 0, errorText.length(), 33);
                    this.errorsValues.put("translation_all", "");
                }
                this.bottomCellTranslation.setText(text);
                this.linearLayout2.addView(this.bottomCellTranslation, LayoutHelper.createLinear(-1, -2));
            }
        } else if (Build.VERSION.SDK_INT >= 18) {
            TextSettingsCell textSettingsCell2 = new TextSettingsCell(context);
            this.scanDocumentCell = textSettingsCell2;
            textSettingsCell2.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.scanDocumentCell.setText(LocaleController.getString("PassportScanPassport", R.string.PassportScanPassport), false);
            this.linearLayout2.addView(this.scanDocumentCell, LayoutHelper.createLinear(-1, -2));
            this.scanDocumentCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda12
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PassportActivity.this.m4009xb13b1799(view);
                }
            });
            TextInfoPrivacyCell textInfoPrivacyCell4 = new TextInfoPrivacyCell(context);
            this.bottomCell = textInfoPrivacyCell4;
            textInfoPrivacyCell4.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            this.bottomCell.setText(LocaleController.getString("PassportScanPassportInfo", R.string.PassportScanPassportInfo));
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        }
        HeaderCell headerCell3 = new HeaderCell(context);
        this.headerCell = headerCell3;
        if (this.documentOnly) {
            headerCell3.setText(LocaleController.getString("PassportDocument", R.string.PassportDocument));
        } else {
            headerCell3.setText(LocaleController.getString("PassportPersonal", R.string.PassportPersonal));
        }
        this.headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
        int count2 = this.currentDocumentsType != null ? 9 : 7;
        this.inputFields = new EditTextBoldCursor[count2];
        int a = 0;
        while (true) {
            int i = 64;
            if (a < count2) {
                final EditTextBoldCursor field = new EditTextBoldCursor(context);
                this.inputFields[a] = field;
                ViewGroup container = new FrameLayout(context) { // from class: org.telegram.ui.PassportActivity.15
                    private StaticLayout errorLayout;
                    private float offsetX;

                    @Override // android.widget.FrameLayout, android.view.View
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        int width = View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(34.0f);
                        StaticLayout errorLayout = field.getErrorLayout(width);
                        this.errorLayout = errorLayout;
                        if (errorLayout != null) {
                            int lineCount = errorLayout.getLineCount();
                            if (lineCount > 1) {
                                int height = AndroidUtilities.dp(64.0f) + (this.errorLayout.getLineBottom(lineCount - 1) - this.errorLayout.getLineBottom(0));
                                heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED);
                            }
                            if (LocaleController.isRTL) {
                                float maxW = 0.0f;
                                int a2 = 0;
                                while (true) {
                                    if (a2 >= lineCount) {
                                        break;
                                    }
                                    float l = this.errorLayout.getLineLeft(a2);
                                    if (l != 0.0f) {
                                        this.offsetX = 0.0f;
                                        break;
                                    }
                                    maxW = Math.max(maxW, this.errorLayout.getLineWidth(a2));
                                    if (a2 == lineCount - 1) {
                                        this.offsetX = width - maxW;
                                    }
                                    a2++;
                                }
                            }
                        }
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    }

                    @Override // android.view.View
                    protected void onDraw(Canvas canvas) {
                        if (this.errorLayout != null) {
                            canvas.save();
                            canvas.translate(AndroidUtilities.dp(21.0f) + this.offsetX, field.getLineY() + AndroidUtilities.dp(3.0f));
                            this.errorLayout.draw(canvas);
                            canvas.restore();
                        }
                    }
                };
                container.setWillNotDraw(false);
                this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 64));
                container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                if (a != count2 - 1) {
                    count = count2;
                } else {
                    View view = new View(context);
                    this.extraBackgroundView = view;
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    count = count2;
                    this.linearLayout2.addView(this.extraBackgroundView, LayoutHelper.createLinear(-1, 6));
                }
                if (this.documentOnly && this.currentDocumentsType != null) {
                    if (a < 7) {
                        container.setVisibility(8);
                        View view2 = this.extraBackgroundView;
                        if (view2 != null) {
                            view2.setVisibility(8);
                        }
                    }
                }
                this.inputFields[a].setTag(Integer.valueOf(a));
                this.inputFields[a].setSupportRtlHint(true);
                this.inputFields[a].setTextSize(1, 16.0f);
                this.inputFields[a].setHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
                this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                this.inputFields[a].setHeaderHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
                this.inputFields[a].setTransformHintToHeader(true);
                this.inputFields[a].setBackgroundDrawable(null);
                this.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
                this.inputFields[a].setCursorWidth(1.5f);
                this.inputFields[a].setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated), Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
                int i2 = 5;
                if (a == 5 || a == 6) {
                    this.inputFields[a].setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda28
                        @Override // android.view.View.OnTouchListener
                        public final boolean onTouch(View view3, MotionEvent motionEvent) {
                            return PassportActivity.this.m4011x2b7a9fd7(view3, motionEvent);
                        }
                    });
                    this.inputFields[a].setInputType(0);
                } else if (a == 3 || a == 8) {
                    this.inputFields[a].setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda31
                        @Override // android.view.View.OnTouchListener
                        public final boolean onTouch(View view3, MotionEvent motionEvent) {
                            return PassportActivity.this.m4014xe2d9ec34(context, view3, motionEvent);
                        }
                    });
                    this.inputFields[a].setInputType(0);
                    this.inputFields[a].setFocusable(false);
                } else if (a == 4) {
                    this.inputFields[a].setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda29
                        @Override // android.view.View.OnTouchListener
                        public final boolean onTouch(View view3, MotionEvent motionEvent) {
                            return PassportActivity.this.m4016x60b48afd(view3, motionEvent);
                        }
                    });
                    this.inputFields[a].setInputType(0);
                    this.inputFields[a].setFocusable(false);
                } else {
                    this.inputFields[a].setInputType(16385);
                    this.inputFields[a].setImeOptions(268435461);
                }
                switch (a) {
                    case 0:
                        if (this.currentType.native_names) {
                            this.inputFields[a].setHintText(LocaleController.getString("PassportNameLatin", R.string.PassportNameLatin));
                        } else {
                            this.inputFields[a].setHintText(LocaleController.getString("PassportName", R.string.PassportName));
                        }
                        key2 = "first_name";
                        values2 = this.currentValues;
                        break;
                    case 1:
                        if (this.currentType.native_names) {
                            this.inputFields[a].setHintText(LocaleController.getString("PassportMidnameLatin", R.string.PassportMidnameLatin));
                        } else {
                            this.inputFields[a].setHintText(LocaleController.getString("PassportMidname", R.string.PassportMidname));
                        }
                        key2 = "middle_name";
                        values2 = this.currentValues;
                        break;
                    case 2:
                        if (this.currentType.native_names) {
                            this.inputFields[a].setHintText(LocaleController.getString("PassportSurnameLatin", R.string.PassportSurnameLatin));
                        } else {
                            this.inputFields[a].setHintText(LocaleController.getString("PassportSurname", R.string.PassportSurname));
                        }
                        key2 = "last_name";
                        values2 = this.currentValues;
                        break;
                    case 3:
                        this.inputFields[a].setHintText(LocaleController.getString("PassportBirthdate", R.string.PassportBirthdate));
                        key2 = "birth_date";
                        values2 = this.currentValues;
                        break;
                    case 4:
                        this.inputFields[a].setHintText(LocaleController.getString("PassportGender", R.string.PassportGender));
                        key2 = "gender";
                        values2 = this.currentValues;
                        break;
                    case 5:
                        this.inputFields[a].setHintText(LocaleController.getString("PassportCitizenship", R.string.PassportCitizenship));
                        key2 = "country_code";
                        values2 = this.currentValues;
                        break;
                    case 6:
                        this.inputFields[a].setHintText(LocaleController.getString("PassportResidence", R.string.PassportResidence));
                        key2 = "residence_country_code";
                        values2 = this.currentValues;
                        break;
                    case 7:
                        this.inputFields[a].setHintText(LocaleController.getString("PassportDocumentNumber", R.string.PassportDocumentNumber));
                        key2 = "document_no";
                        values2 = this.currentDocumentValues;
                        break;
                    case 8:
                        this.inputFields[a].setHintText(LocaleController.getString("PassportExpired", R.string.PassportExpired));
                        key2 = "expiry_date";
                        values2 = this.currentDocumentValues;
                        break;
                    default:
                        a++;
                        count2 = count;
                }
                setFieldValues(values2, this.inputFields[a], key2);
                EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                editTextBoldCursorArr[a].setSelection(editTextBoldCursorArr[a].length());
                if (a == 0 || a == 2 || a == 1) {
                    this.inputFields[a].addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.PassportActivity.16
                        private boolean ignore;

                        @Override // android.text.TextWatcher
                        public void beforeTextChanged(CharSequence s, int start, int count3, int after) {
                        }

                        @Override // android.text.TextWatcher
                        public void onTextChanged(CharSequence s, int start, int before, int count3) {
                        }

                        @Override // android.text.TextWatcher
                        public void afterTextChanged(Editable s) {
                            if (this.ignore) {
                                return;
                            }
                            int num = ((Integer) field.getTag()).intValue();
                            boolean error = false;
                            int a2 = 0;
                            while (true) {
                                if (a2 >= s.length()) {
                                    break;
                                }
                                char ch = s.charAt(a2);
                                if ((ch >= '0' && ch <= '9') || ((ch >= 'a' && ch <= 'z') || ((ch >= 'A' && ch <= 'Z') || ch == ' ' || ch == '\'' || ch == ',' || ch == '.' || ch == '&' || ch == '-' || ch == '/'))) {
                                    a2++;
                                } else {
                                    error = true;
                                    break;
                                }
                            }
                            if (!error || PassportActivity.this.allowNonLatinName) {
                                PassportActivity.this.nonLatinNames[num] = error;
                                PassportActivity.this.checkFieldForError(field, key2, s, false);
                                return;
                            }
                            field.setErrorText(LocaleController.getString("PassportUseLatinOnly", R.string.PassportUseLatinOnly));
                        }
                    });
                } else {
                    this.inputFields[a].addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.PassportActivity.17
                        @Override // android.text.TextWatcher
                        public void beforeTextChanged(CharSequence s, int start, int count3, int after) {
                        }

                        @Override // android.text.TextWatcher
                        public void onTextChanged(CharSequence s, int start, int before, int count3) {
                        }

                        @Override // android.text.TextWatcher
                        public void afterTextChanged(Editable s) {
                            PassportActivity passportActivity = PassportActivity.this;
                            passportActivity.checkFieldForError(field, key2, s, values2 == passportActivity.currentDocumentValues);
                            int field12 = ((Integer) field.getTag()).intValue();
                            EditTextBoldCursor editTextBoldCursor = PassportActivity.this.inputFields[field12];
                            if (field12 == 6) {
                                PassportActivity.this.checkNativeFields(true);
                            }
                        }
                    });
                }
                this.inputFields[a].setPadding(0, 0, 0, 0);
                EditTextBoldCursor editTextBoldCursor = this.inputFields[a];
                if (!LocaleController.isRTL) {
                    i2 = 3;
                }
                editTextBoldCursor.setGravity(i2 | 16);
                container.addView(this.inputFields[a], LayoutHelper.createFrame(-1, -1.0f, 51, 21.0f, 0.0f, 21.0f, 0.0f));
                this.inputFields[a].setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda36
                    @Override // android.widget.TextView.OnEditorActionListener
                    public final boolean onEditorAction(TextView textView, int i3, KeyEvent keyEvent) {
                        return PassportActivity.this.m4017x9dd44f1c(textView, i3, keyEvent);
                    }
                });
                a++;
                count2 = count;
            } else {
                ShadowSectionCell shadowSectionCell = new ShadowSectionCell(context);
                this.sectionCell2 = shadowSectionCell;
                this.linearLayout2.addView(shadowSectionCell, LayoutHelper.createLinear(-1, -2));
                HeaderCell headerCell4 = new HeaderCell(context);
                this.headerCell = headerCell4;
                headerCell4.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
                int i3 = 3;
                this.inputExtraFields = new EditTextBoldCursor[3];
                int a2 = 0;
                while (a2 < i3) {
                    final EditTextBoldCursor field2 = new EditTextBoldCursor(context);
                    this.inputExtraFields[a2] = field2;
                    ViewGroup container2 = new FrameLayout(context) { // from class: org.telegram.ui.PassportActivity.18
                        private StaticLayout errorLayout;
                        private float offsetX;

                        @Override // android.widget.FrameLayout, android.view.View
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            int width = View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(34.0f);
                            StaticLayout errorLayout = field2.getErrorLayout(width);
                            this.errorLayout = errorLayout;
                            if (errorLayout != null) {
                                int lineCount = errorLayout.getLineCount();
                                if (lineCount > 1) {
                                    int height = AndroidUtilities.dp(64.0f) + (this.errorLayout.getLineBottom(lineCount - 1) - this.errorLayout.getLineBottom(0));
                                    heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED);
                                }
                                if (LocaleController.isRTL) {
                                    float maxW = 0.0f;
                                    int a3 = 0;
                                    while (true) {
                                        if (a3 >= lineCount) {
                                            break;
                                        }
                                        float l = this.errorLayout.getLineLeft(a3);
                                        if (l != 0.0f) {
                                            this.offsetX = 0.0f;
                                            break;
                                        }
                                        maxW = Math.max(maxW, this.errorLayout.getLineWidth(a3));
                                        if (a3 == lineCount - 1) {
                                            this.offsetX = width - maxW;
                                        }
                                        a3++;
                                    }
                                }
                            }
                            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                        }

                        @Override // android.view.View
                        protected void onDraw(Canvas canvas) {
                            if (this.errorLayout != null) {
                                canvas.save();
                                canvas.translate(AndroidUtilities.dp(21.0f) + this.offsetX, field2.getLineY() + AndroidUtilities.dp(3.0f));
                                this.errorLayout.draw(canvas);
                                canvas.restore();
                            }
                        }
                    };
                    container2.setWillNotDraw(false);
                    this.linearLayout2.addView(container2, LayoutHelper.createLinear(-1, i));
                    container2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    if (a2 == 2) {
                        View view3 = new View(context);
                        this.extraBackgroundView2 = view3;
                        view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        this.linearLayout2.addView(this.extraBackgroundView2, LayoutHelper.createLinear(-1, 6));
                    }
                    this.inputExtraFields[a2].setTag(Integer.valueOf(a2));
                    this.inputExtraFields[a2].setSupportRtlHint(true);
                    this.inputExtraFields[a2].setTextSize(1, 16.0f);
                    this.inputExtraFields[a2].setHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
                    this.inputExtraFields[a2].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    this.inputExtraFields[a2].setHeaderHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
                    this.inputExtraFields[a2].setTransformHintToHeader(true);
                    this.inputExtraFields[a2].setBackgroundDrawable(null);
                    this.inputExtraFields[a2].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    this.inputExtraFields[a2].setCursorSize(AndroidUtilities.dp(20.0f));
                    this.inputExtraFields[a2].setCursorWidth(1.5f);
                    this.inputExtraFields[a2].setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated), Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
                    this.inputExtraFields[a2].setInputType(16385);
                    this.inputExtraFields[a2].setImeOptions(268435461);
                    switch (a2) {
                        case 0:
                            key = "first_name_native";
                            values = this.currentValues;
                            break;
                        case 1:
                            key = "middle_name_native";
                            values = this.currentValues;
                            break;
                        case 2:
                            key = "last_name_native";
                            values = this.currentValues;
                            break;
                        default:
                            a2++;
                            i3 = 3;
                            i = 64;
                    }
                    setFieldValues(values, this.inputExtraFields[a2], key);
                    EditTextBoldCursor[] editTextBoldCursorArr2 = this.inputExtraFields;
                    editTextBoldCursorArr2[a2].setSelection(editTextBoldCursorArr2[a2].length());
                    if (a2 == 0 || a2 == 2 || a2 == 1) {
                        this.inputExtraFields[a2].addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.PassportActivity.19
                            private boolean ignore;

                            @Override // android.text.TextWatcher
                            public void beforeTextChanged(CharSequence s, int start, int count3, int after) {
                            }

                            @Override // android.text.TextWatcher
                            public void onTextChanged(CharSequence s, int start, int before, int count3) {
                            }

                            @Override // android.text.TextWatcher
                            public void afterTextChanged(Editable s) {
                                if (!this.ignore) {
                                    PassportActivity.this.checkFieldForError(field2, key, s, false);
                                }
                            }
                        });
                    }
                    this.inputExtraFields[a2].setPadding(0, 0, 0, 0);
                    this.inputExtraFields[a2].setGravity((LocaleController.isRTL ? 5 : 3) | 16);
                    container2.addView(this.inputExtraFields[a2], LayoutHelper.createFrame(-1, -1.0f, 51, 21.0f, 0.0f, 21.0f, 0.0f));
                    this.inputExtraFields[a2].setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda37
                        @Override // android.widget.TextView.OnEditorActionListener
                        public final boolean onEditorAction(TextView textView, int i4, KeyEvent keyEvent) {
                            return PassportActivity.this.m4018xdaf4133b(textView, i4, keyEvent);
                        }
                    });
                    a2++;
                    i3 = 3;
                    i = 64;
                }
                TextInfoPrivacyCell textInfoPrivacyCell5 = new TextInfoPrivacyCell(context);
                this.nativeInfoCell = textInfoPrivacyCell5;
                this.linearLayout2.addView(textInfoPrivacyCell5, LayoutHelper.createLinear(-1, -2));
                if (((this.currentBotId != 0 || this.currentDocumentsType == null) && this.currentTypeValue != null && !this.documentOnly) || this.currentDocumentsTypeValue != null) {
                    TLRPC.TL_secureValue tL_secureValue = this.currentDocumentsTypeValue;
                    if (tL_secureValue != null) {
                        addDocumentViews(tL_secureValue.files);
                        if (this.currentDocumentsTypeValue.front_side instanceof TLRPC.TL_secureFile) {
                            addDocumentViewInternal((TLRPC.TL_secureFile) this.currentDocumentsTypeValue.front_side, 2);
                        }
                        if (this.currentDocumentsTypeValue.reverse_side instanceof TLRPC.TL_secureFile) {
                            addDocumentViewInternal((TLRPC.TL_secureFile) this.currentDocumentsTypeValue.reverse_side, 3);
                        }
                        if (this.currentDocumentsTypeValue.selfie instanceof TLRPC.TL_secureFile) {
                            addDocumentViewInternal((TLRPC.TL_secureFile) this.currentDocumentsTypeValue.selfie, 1);
                        }
                        addTranslationDocumentViews(this.currentDocumentsTypeValue.translation);
                    }
                    TextSettingsCell settingsCell1 = new TextSettingsCell(context);
                    settingsCell1.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
                    settingsCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                    if (this.currentDocumentsType == null) {
                        settingsCell1.setText(LocaleController.getString("PassportDeleteInfo", R.string.PassportDeleteInfo), false);
                    } else {
                        settingsCell1.setText(LocaleController.getString("PassportDeleteDocument", R.string.PassportDeleteDocument), false);
                    }
                    this.linearLayout2.addView(settingsCell1, LayoutHelper.createLinear(-1, -2));
                    settingsCell1.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda13
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view4) {
                            PassportActivity.this.m4019x1813d75a(view4);
                        }
                    });
                    this.nativeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    ShadowSectionCell shadowSectionCell2 = new ShadowSectionCell(context);
                    this.sectionCell = shadowSectionCell2;
                    shadowSectionCell2.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
                } else {
                    this.nativeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                }
                updateInterfaceStringsForDocumentType();
                checkNativeFields(false);
                return;
            }
        }
    }

    /* renamed from: lambda$createIdentityInterface$40$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4005xbcbc071d(View v) {
        this.uploadingFileType = 2;
        openAttachMenu();
    }

    /* renamed from: lambda$createIdentityInterface$41$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4006xf9dbcb3c(View v) {
        this.uploadingFileType = 3;
        openAttachMenu();
    }

    /* renamed from: lambda$createIdentityInterface$42$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4007x36fb8f5b(View v) {
        this.uploadingFileType = 1;
        openAttachMenu();
    }

    /* renamed from: lambda$createIdentityInterface$43$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4008x741b537a(View v) {
        this.uploadingFileType = 4;
        openAttachMenu();
    }

    /* renamed from: lambda$createIdentityInterface$44$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4009xb13b1799(View v) {
        if (Build.VERSION.SDK_INT >= 23 && getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0) {
            getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 22);
            return;
        }
        CameraScanActivity fragment = new CameraScanActivity(0);
        fragment.setDelegate(new CameraScanActivity.CameraScanActivityDelegate() { // from class: org.telegram.ui.PassportActivity.14
            @Override // org.telegram.ui.CameraScanActivity.CameraScanActivityDelegate
            public /* synthetic */ void didFindQr(String str) {
                CameraScanActivity.CameraScanActivityDelegate.CC.$default$didFindQr(this, str);
            }

            @Override // org.telegram.ui.CameraScanActivity.CameraScanActivityDelegate
            public /* synthetic */ boolean processQr(String str, Runnable runnable) {
                return CameraScanActivity.CameraScanActivityDelegate.CC.$default$processQr(this, str, runnable);
            }

            @Override // org.telegram.ui.CameraScanActivity.CameraScanActivityDelegate
            public void didFindMrzInfo(MrzRecognizer.Result result) {
                if (!TextUtils.isEmpty(result.firstName)) {
                    PassportActivity.this.inputFields[0].setText(result.firstName);
                }
                if (!TextUtils.isEmpty(result.middleName)) {
                    PassportActivity.this.inputFields[1].setText(result.middleName);
                }
                if (!TextUtils.isEmpty(result.lastName)) {
                    PassportActivity.this.inputFields[2].setText(result.lastName);
                }
                if (result.gender != 0) {
                    switch (result.gender) {
                        case 1:
                            PassportActivity.this.currentGender = "male";
                            PassportActivity.this.inputFields[4].setText(LocaleController.getString("PassportMale", R.string.PassportMale));
                            break;
                        case 2:
                            PassportActivity.this.currentGender = "female";
                            PassportActivity.this.inputFields[4].setText(LocaleController.getString("PassportFemale", R.string.PassportFemale));
                            break;
                    }
                }
                if (!TextUtils.isEmpty(result.nationality)) {
                    PassportActivity.this.currentCitizeship = result.nationality;
                    String country = (String) PassportActivity.this.languageMap.get(PassportActivity.this.currentCitizeship);
                    if (country != null) {
                        PassportActivity.this.inputFields[5].setText(country);
                    }
                }
                if (!TextUtils.isEmpty(result.issuingCountry)) {
                    PassportActivity.this.currentResidence = result.issuingCountry;
                    String country2 = (String) PassportActivity.this.languageMap.get(PassportActivity.this.currentResidence);
                    if (country2 != null) {
                        PassportActivity.this.inputFields[6].setText(country2);
                    }
                }
                if (result.birthDay > 0 && result.birthMonth > 0 && result.birthYear > 0) {
                    PassportActivity.this.inputFields[3].setText(String.format(Locale.US, "%02d.%02d.%d", Integer.valueOf(result.birthDay), Integer.valueOf(result.birthMonth), Integer.valueOf(result.birthYear)));
                }
            }
        });
        presentFragment(fragment);
    }

    /* renamed from: lambda$createIdentityInterface$46$org-telegram-ui-PassportActivity */
    public /* synthetic */ boolean m4011x2b7a9fd7(final View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            CountrySelectActivity fragment = new CountrySelectActivity(false);
            fragment.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda69
                @Override // org.telegram.ui.CountrySelectActivity.CountrySelectActivityDelegate
                public final void didSelectCountry(CountrySelectActivity.Country country) {
                    PassportActivity.this.m4010xee5adbb8(v, country);
                }
            });
            presentFragment(fragment);
        }
        return true;
    }

    /* renamed from: lambda$createIdentityInterface$45$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4010xee5adbb8(View v, CountrySelectActivity.Country country) {
        int field12 = ((Integer) v.getTag()).intValue();
        EditTextBoldCursor editText = this.inputFields[field12];
        if (field12 == 5) {
            this.currentCitizeship = country.shortname;
        } else {
            this.currentResidence = country.shortname;
        }
        editText.setText(country.name);
    }

    /* renamed from: lambda$createIdentityInterface$49$org-telegram-ui-PassportActivity */
    public /* synthetic */ boolean m4014xe2d9ec34(Context context, View v, MotionEvent event) {
        int currentYearDiff;
        int maxYear;
        int minYear;
        String title;
        int selectedYear;
        int selectedMonth;
        int selectedDay;
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            Calendar calendar = Calendar.getInstance();
            calendar.get(1);
            calendar.get(2);
            calendar.get(5);
            try {
                final EditTextBoldCursor field1 = (EditTextBoldCursor) v;
                final int num = ((Integer) field1.getTag()).intValue();
                if (num == 8) {
                    title = LocaleController.getString("PassportSelectExpiredDate", R.string.PassportSelectExpiredDate);
                    minYear = 0;
                    maxYear = 20;
                    currentYearDiff = 0;
                } else {
                    title = LocaleController.getString("PassportSelectBithdayDate", R.string.PassportSelectBithdayDate);
                    minYear = -120;
                    maxYear = 0;
                    currentYearDiff = -18;
                }
                String[] args = field1.getText().toString().split("\\.");
                if (args.length != 3) {
                    selectedDay = -1;
                    selectedMonth = -1;
                    selectedYear = -1;
                } else {
                    int selectedDay2 = Utilities.parseInt((CharSequence) args[0]).intValue();
                    int selectedMonth2 = Utilities.parseInt((CharSequence) args[1]).intValue();
                    int selectedYear2 = Utilities.parseInt((CharSequence) args[2]).intValue();
                    selectedDay = selectedDay2;
                    selectedMonth = selectedMonth2;
                    selectedYear = selectedYear2;
                }
                AlertDialog.Builder builder = AlertsCreator.createDatePickerDialog(context, minYear, maxYear, currentYearDiff, selectedDay, selectedMonth, selectedYear, title, num == 8, new AlertsCreator.DatePickerDelegate() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda66
                    @Override // org.telegram.ui.Components.AlertsCreator.DatePickerDelegate
                    public final void didSelectDate(int i, int i2, int i3) {
                        PassportActivity.this.m4012x689a63f6(num, field1, i, i2, i3);
                    }
                });
                if (num == 8) {
                    builder.setNegativeButton(LocaleController.getString("PassportSelectNotExpire", R.string.PassportSelectNotExpire), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda1
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            PassportActivity.this.m4013xa5ba2815(field1, dialogInterface, i);
                        }
                    });
                }
                showDialog(builder.create());
                return true;
            } catch (Exception e) {
                FileLog.e(e);
                return true;
            }
        }
        return true;
    }

    /* renamed from: lambda$createIdentityInterface$47$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4012x689a63f6(int num, EditTextBoldCursor field1, int year1, int month, int dayOfMonth1) {
        if (num == 8) {
            int[] iArr = this.currentExpireDate;
            iArr[0] = year1;
            iArr[1] = month + 1;
            iArr[2] = dayOfMonth1;
        }
        field1.setText(String.format(Locale.US, "%02d.%02d.%d", Integer.valueOf(dayOfMonth1), Integer.valueOf(month + 1), Integer.valueOf(year1)));
    }

    /* renamed from: lambda$createIdentityInterface$48$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4013xa5ba2815(EditTextBoldCursor field1, DialogInterface dialog, int which) {
        int[] iArr = this.currentExpireDate;
        iArr[2] = 0;
        iArr[1] = 0;
        iArr[0] = 0;
        field1.setText(LocaleController.getString("PassportNoExpireDate", R.string.PassportNoExpireDate));
    }

    /* renamed from: lambda$createIdentityInterface$51$org-telegram-ui-PassportActivity */
    public /* synthetic */ boolean m4016x60b48afd(View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("PassportSelectGender", R.string.PassportSelectGender));
            builder.setItems(new CharSequence[]{LocaleController.getString("PassportMale", R.string.PassportMale), LocaleController.getString("PassportFemale", R.string.PassportFemale)}, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda11
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PassportActivity.this.m4015x2394c6de(dialogInterface, i);
                }
            });
            builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        }
        return true;
    }

    /* renamed from: lambda$createIdentityInterface$50$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4015x2394c6de(DialogInterface dialogInterface, int i) {
        if (i == 0) {
            this.currentGender = "male";
            this.inputFields[4].setText(LocaleController.getString("PassportMale", R.string.PassportMale));
        } else if (i == 1) {
            this.currentGender = "female";
            this.inputFields[4].setText(LocaleController.getString("PassportFemale", R.string.PassportFemale));
        }
    }

    /* renamed from: lambda$createIdentityInterface$52$org-telegram-ui-PassportActivity */
    public /* synthetic */ boolean m4017x9dd44f1c(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            int num = ((Integer) textView.getTag()).intValue() + 1;
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            if (num < editTextBoldCursorArr.length) {
                if (editTextBoldCursorArr[num].isFocusable()) {
                    this.inputFields[num].requestFocus();
                } else {
                    this.inputFields[num].dispatchTouchEvent(MotionEvent.obtain(0L, 0L, 1, 0.0f, 0.0f, 0));
                    textView.clearFocus();
                    AndroidUtilities.hideKeyboard(textView);
                }
            }
            return true;
        }
        return false;
    }

    /* renamed from: lambda$createIdentityInterface$53$org-telegram-ui-PassportActivity */
    public /* synthetic */ boolean m4018xdaf4133b(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            int num = ((Integer) textView.getTag()).intValue() + 1;
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputExtraFields;
            if (num < editTextBoldCursorArr.length) {
                if (editTextBoldCursorArr[num].isFocusable()) {
                    this.inputExtraFields[num].requestFocus();
                } else {
                    this.inputExtraFields[num].dispatchTouchEvent(MotionEvent.obtain(0L, 0L, 1, 0.0f, 0.0f, 0));
                    textView.clearFocus();
                    AndroidUtilities.hideKeyboard(textView);
                }
            }
            return true;
        }
        return false;
    }

    /* renamed from: lambda$createIdentityInterface$54$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4019x1813d75a(View v) {
        createDocumentDeleteAlert();
    }

    private void updateInterfaceStringsForDocumentType() {
        if (this.currentDocumentsType != null) {
            this.actionBar.setTitle(getTextForType(this.currentDocumentsType.type));
        } else {
            this.actionBar.setTitle(LocaleController.getString("PassportPersonal", R.string.PassportPersonal));
        }
        updateUploadText(2);
        updateUploadText(3);
        updateUploadText(1);
        updateUploadText(4);
    }

    public void updateUploadText(int type) {
        boolean z = true;
        int i = 0;
        if (type == 0) {
            if (this.uploadDocumentCell == null) {
                return;
            }
            if (this.documents.size() >= 1) {
                this.uploadDocumentCell.setText(LocaleController.getString("PassportUploadAdditinalDocument", R.string.PassportUploadAdditinalDocument), false);
            } else {
                this.uploadDocumentCell.setText(LocaleController.getString("PassportUploadDocument", R.string.PassportUploadDocument), false);
            }
        } else if (type == 1) {
            TextDetailSettingsCell textDetailSettingsCell = this.uploadSelfieCell;
            if (textDetailSettingsCell == null) {
                return;
            }
            if (this.selfieDocument != null) {
                i = 8;
            }
            textDetailSettingsCell.setVisibility(i);
        } else if (type == 4) {
            if (this.uploadTranslationCell == null) {
                return;
            }
            if (this.translationDocuments.size() >= 1) {
                this.uploadTranslationCell.setText(LocaleController.getString("PassportUploadAdditinalDocument", R.string.PassportUploadAdditinalDocument), false);
            } else {
                this.uploadTranslationCell.setText(LocaleController.getString("PassportUploadDocument", R.string.PassportUploadDocument), false);
            }
        } else if (type == 2) {
            if (this.uploadFrontCell == null) {
                return;
            }
            TLRPC.TL_secureRequiredType tL_secureRequiredType = this.currentDocumentsType;
            if (tL_secureRequiredType == null || (!tL_secureRequiredType.selfie_required && !(this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeIdentityCard) && !(this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeDriverLicense))) {
                z = false;
            }
            boolean divider = z;
            if ((this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypePassport) || (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeInternalPassport)) {
                this.uploadFrontCell.setTextAndValue(LocaleController.getString("PassportMainPage", R.string.PassportMainPage), LocaleController.getString("PassportMainPageInfo", R.string.PassportMainPageInfo), divider);
            } else {
                this.uploadFrontCell.setTextAndValue(LocaleController.getString("PassportFrontSide", R.string.PassportFrontSide), LocaleController.getString("PassportFrontSideInfo", R.string.PassportFrontSideInfo), divider);
            }
            TextDetailSettingsCell textDetailSettingsCell2 = this.uploadFrontCell;
            if (this.frontDocument != null) {
                i = 8;
            }
            textDetailSettingsCell2.setVisibility(i);
        } else if (type != 3 || this.uploadReverseCell == null) {
        } else {
            if (!(this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeIdentityCard) && !(this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeDriverLicense)) {
                this.reverseLayout.setVisibility(8);
                this.uploadReverseCell.setVisibility(8);
                return;
            }
            this.reverseLayout.setVisibility(0);
            TextDetailSettingsCell textDetailSettingsCell3 = this.uploadReverseCell;
            if (this.reverseDocument != null) {
                i = 8;
            }
            textDetailSettingsCell3.setVisibility(i);
        }
    }

    private void checkTopErrorCell(boolean init) {
        String errorText;
        String errorText2;
        if (this.topErrorCell == null) {
            return;
        }
        SpannableStringBuilder stringBuilder = null;
        if (this.fieldsErrors != null && ((init || this.errorsValues.containsKey("error_all")) && (errorText2 = this.fieldsErrors.get("error_all")) != null)) {
            stringBuilder = new SpannableStringBuilder(errorText2);
            if (init) {
                this.errorsValues.put("error_all", "");
            }
        }
        if (this.documentsErrors != null && ((init || this.errorsValues.containsKey("error_document_all")) && (errorText = this.documentsErrors.get("error_all")) != null)) {
            if (stringBuilder == null) {
                stringBuilder = new SpannableStringBuilder(errorText);
            } else {
                stringBuilder.append((CharSequence) "\n\n").append((CharSequence) errorText);
            }
            if (init) {
                this.errorsValues.put("error_document_all", "");
            }
        }
        if (stringBuilder != null) {
            stringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3)), 0, stringBuilder.length(), 33);
            this.topErrorCell.setText(stringBuilder);
            this.topErrorCell.setVisibility(0);
        } else if (this.topErrorCell.getVisibility() != 8) {
            this.topErrorCell.setVisibility(8);
        }
    }

    private void addDocumentViewInternal(TLRPC.TL_secureFile f, int uploadingType) {
        SecureDocumentKey secureDocumentKey = getSecureDocumentKey(f.secret, f.file_hash);
        SecureDocument secureDocument = new SecureDocument(secureDocumentKey, f, null, null, null);
        addDocumentView(secureDocument, uploadingType);
    }

    private void addDocumentViews(ArrayList<TLRPC.SecureFile> files) {
        this.documents.clear();
        int size = files.size();
        for (int a = 0; a < size; a++) {
            TLRPC.SecureFile secureFile = files.get(a);
            if (secureFile instanceof TLRPC.TL_secureFile) {
                addDocumentViewInternal((TLRPC.TL_secureFile) secureFile, 0);
            }
        }
    }

    private void addTranslationDocumentViews(ArrayList<TLRPC.SecureFile> files) {
        this.translationDocuments.clear();
        int size = files.size();
        for (int a = 0; a < size; a++) {
            TLRPC.SecureFile secureFile = files.get(a);
            if (secureFile instanceof TLRPC.TL_secureFile) {
                addDocumentViewInternal((TLRPC.TL_secureFile) secureFile, 4);
            }
        }
    }

    private void setFieldValues(HashMap<String, String> values, EditTextBoldCursor editText, String key) {
        String value;
        String value2;
        String value3 = values.get(key);
        if (value3 != null) {
            char c = 65535;
            switch (key.hashCode()) {
                case -2006252145:
                    if (key.equals("residence_country_code")) {
                        c = 1;
                        break;
                    }
                    break;
                case -1249512767:
                    if (key.equals("gender")) {
                        c = 2;
                        break;
                    }
                    break;
                case 475919162:
                    if (key.equals("expiry_date")) {
                        c = 3;
                        break;
                    }
                    break;
                case 1481071862:
                    if (key.equals("country_code")) {
                        c = 0;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    this.currentCitizeship = value3;
                    String country = this.languageMap.get(value3);
                    if (country != null) {
                        editText.setText(country);
                        break;
                    }
                    break;
                case 1:
                    this.currentResidence = value3;
                    String country2 = this.languageMap.get(value3);
                    if (country2 != null) {
                        editText.setText(country2);
                        break;
                    }
                    break;
                case 2:
                    if ("male".equals(value3)) {
                        this.currentGender = value3;
                        editText.setText(LocaleController.getString("PassportMale", R.string.PassportMale));
                        break;
                    } else if ("female".equals(value3)) {
                        this.currentGender = value3;
                        editText.setText(LocaleController.getString("PassportFemale", R.string.PassportFemale));
                        break;
                    }
                    break;
                case 3:
                    boolean ok = false;
                    if (!TextUtils.isEmpty(value3)) {
                        String[] args = value3.split("\\.");
                        if (args.length == 3) {
                            this.currentExpireDate[0] = Utilities.parseInt((CharSequence) args[2]).intValue();
                            this.currentExpireDate[1] = Utilities.parseInt((CharSequence) args[1]).intValue();
                            this.currentExpireDate[2] = Utilities.parseInt((CharSequence) args[0]).intValue();
                            editText.setText(value3);
                            ok = true;
                        }
                    }
                    if (!ok) {
                        int[] iArr = this.currentExpireDate;
                        iArr[2] = 0;
                        iArr[1] = 0;
                        iArr[0] = 0;
                        editText.setText(LocaleController.getString("PassportNoExpireDate", R.string.PassportNoExpireDate));
                        break;
                    }
                    break;
                default:
                    editText.setText(value3);
                    break;
            }
        }
        HashMap<String, String> hashMap = this.fieldsErrors;
        if (hashMap != null && (value2 = hashMap.get(key)) != null) {
            editText.setErrorText(value2);
            this.errorsValues.put(key, editText.getText().toString());
            return;
        }
        HashMap<String, String> hashMap2 = this.documentsErrors;
        if (hashMap2 != null && (value = hashMap2.get(key)) != null) {
            editText.setErrorText(value);
            this.errorsValues.put(key, editText.getText().toString());
        }
    }

    private void addDocumentView(final SecureDocument document, final int type) {
        String key;
        String text;
        String value;
        HashMap<String, String> hashMap;
        if (type == 1) {
            this.selfieDocument = document;
            if (this.selfieLayout == null) {
                return;
            }
        } else if (type == 4) {
            this.translationDocuments.add(document);
            if (this.translationLayout == null) {
                return;
            }
        } else if (type == 2) {
            this.frontDocument = document;
            if (this.frontLayout == null) {
                return;
            }
        } else if (type == 3) {
            this.reverseDocument = document;
            if (this.reverseLayout == null) {
                return;
            }
        } else {
            this.documents.add(document);
            if (this.documentsLayout == null) {
                return;
            }
        }
        if (getParentActivity() == null) {
            return;
        }
        final SecureDocumentCell cell = new SecureDocumentCell(getParentActivity());
        cell.setTag(document);
        cell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.documentsCells.put(document, cell);
        String hash = getDocumentHash(document);
        if (type == 1) {
            text = LocaleController.getString("PassportSelfie", R.string.PassportSelfie);
            this.selfieLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
            key = "selfie" + hash;
        } else if (type == 4) {
            text = LocaleController.getString("AttachPhoto", R.string.AttachPhoto);
            this.translationLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
            key = "translation" + hash;
        } else if (type == 2) {
            if ((this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypePassport) || (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeInternalPassport)) {
                text = LocaleController.getString("PassportMainPage", R.string.PassportMainPage);
            } else {
                text = LocaleController.getString("PassportFrontSide", R.string.PassportFrontSide);
            }
            this.frontLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
            key = "front" + hash;
        } else if (type == 3) {
            text = LocaleController.getString("PassportReverseSide", R.string.PassportReverseSide);
            this.reverseLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
            key = "reverse" + hash;
        } else {
            text = LocaleController.getString("AttachPhoto", R.string.AttachPhoto);
            this.documentsLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
            key = "files" + hash;
        }
        if (key != null && (hashMap = this.documentsErrors) != null) {
            String str = hashMap.get(key);
            value = str;
            if (str != null) {
                cell.valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
                this.errorsValues.put(key, "");
                cell.setTextAndValueAndImage(text, value, document);
                cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda21
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        PassportActivity.this.m3986lambda$addDocumentView$55$orgtelegramuiPassportActivity(type, view);
                    }
                });
                final String str2 = key;
                cell.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda26
                    @Override // android.view.View.OnLongClickListener
                    public final boolean onLongClick(View view) {
                        return PassportActivity.this.m3988lambda$addDocumentView$57$orgtelegramuiPassportActivity(type, document, cell, str2, view);
                    }
                });
            }
        }
        value = LocaleController.formatDateForBan(document.secureFile.date);
        cell.setTextAndValueAndImage(text, value, document);
        cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda21
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PassportActivity.this.m3986lambda$addDocumentView$55$orgtelegramuiPassportActivity(type, view);
            }
        });
        final String str22 = key;
        cell.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda26
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                return PassportActivity.this.m3988lambda$addDocumentView$57$orgtelegramuiPassportActivity(type, document, cell, str22, view);
            }
        });
    }

    /* renamed from: lambda$addDocumentView$55$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m3986lambda$addDocumentView$55$orgtelegramuiPassportActivity(int type, View v) {
        this.uploadingFileType = type;
        if (type == 1) {
            this.currentPhotoViewerLayout = this.selfieLayout;
        } else if (type == 4) {
            this.currentPhotoViewerLayout = this.translationLayout;
        } else if (type == 2) {
            this.currentPhotoViewerLayout = this.frontLayout;
        } else if (type == 3) {
            this.currentPhotoViewerLayout = this.reverseLayout;
        } else {
            this.currentPhotoViewerLayout = this.documentsLayout;
        }
        SecureDocument document1 = (SecureDocument) v.getTag();
        PhotoViewer.getInstance().setParentActivity(getParentActivity());
        if (type == 1) {
            ArrayList<SecureDocument> arrayList = new ArrayList<>();
            arrayList.add(this.selfieDocument);
            PhotoViewer.getInstance().openPhoto(arrayList, 0, this.provider);
        } else if (type != 2) {
            if (type == 3) {
                ArrayList<SecureDocument> arrayList2 = new ArrayList<>();
                arrayList2.add(this.reverseDocument);
                PhotoViewer.getInstance().openPhoto(arrayList2, 0, this.provider);
            } else if (type == 0) {
                PhotoViewer photoViewer = PhotoViewer.getInstance();
                ArrayList<SecureDocument> arrayList3 = this.documents;
                photoViewer.openPhoto(arrayList3, arrayList3.indexOf(document1), this.provider);
            } else {
                PhotoViewer photoViewer2 = PhotoViewer.getInstance();
                ArrayList<SecureDocument> arrayList4 = this.translationDocuments;
                photoViewer2.openPhoto(arrayList4, arrayList4.indexOf(document1), this.provider);
            }
        } else {
            ArrayList<SecureDocument> arrayList5 = new ArrayList<>();
            arrayList5.add(this.frontDocument);
            PhotoViewer.getInstance().openPhoto(arrayList5, 0, this.provider);
        }
    }

    /* renamed from: lambda$addDocumentView$57$org-telegram-ui-PassportActivity */
    public /* synthetic */ boolean m3988lambda$addDocumentView$57$orgtelegramuiPassportActivity(final int type, final SecureDocument document, final SecureDocumentCell cell, final String key, View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        if (type == 1) {
            builder.setMessage(LocaleController.getString("PassportDeleteSelfie", R.string.PassportDeleteSelfie));
        } else {
            builder.setMessage(LocaleController.getString("PassportDeleteScan", R.string.PassportDeleteScan));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda65
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                PassportActivity.this.m3987lambda$addDocumentView$56$orgtelegramuiPassportActivity(document, type, cell, key, dialogInterface, i);
            }
        });
        showDialog(builder.create());
        return true;
    }

    /* renamed from: lambda$addDocumentView$56$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m3987lambda$addDocumentView$56$orgtelegramuiPassportActivity(SecureDocument document, int type, SecureDocumentCell cell, String key, DialogInterface dialog, int which) {
        this.documentsCells.remove(document);
        if (type == 1) {
            this.selfieDocument = null;
            this.selfieLayout.removeView(cell);
        } else if (type == 4) {
            this.translationDocuments.remove(document);
            this.translationLayout.removeView(cell);
        } else if (type == 2) {
            this.frontDocument = null;
            this.frontLayout.removeView(cell);
        } else if (type == 3) {
            this.reverseDocument = null;
            this.reverseLayout.removeView(cell);
        } else {
            this.documents.remove(document);
            this.documentsLayout.removeView(cell);
        }
        if (key != null) {
            HashMap<String, String> hashMap = this.documentsErrors;
            if (hashMap != null) {
                hashMap.remove(key);
            }
            HashMap<String, String> hashMap2 = this.errorsValues;
            if (hashMap2 != null) {
                hashMap2.remove(key);
            }
        }
        updateUploadText(type);
        if (document.path != null && this.uploadingDocuments.remove(document.path) != null) {
            if (this.uploadingDocuments.isEmpty()) {
                this.doneItem.setEnabled(true);
                this.doneItem.setAlpha(1.0f);
            }
            FileLoader.getInstance(this.currentAccount).cancelFileUpload(document.path, false);
        }
    }

    private String getNameForType(TLRPC.SecureValueType type) {
        if (type instanceof TLRPC.TL_secureValueTypePersonalDetails) {
            return "personal_details";
        }
        if (type instanceof TLRPC.TL_secureValueTypePassport) {
            return "passport";
        }
        if (type instanceof TLRPC.TL_secureValueTypeInternalPassport) {
            return "internal_passport";
        }
        if (type instanceof TLRPC.TL_secureValueTypeDriverLicense) {
            return "driver_license";
        }
        if (type instanceof TLRPC.TL_secureValueTypeIdentityCard) {
            return "identity_card";
        }
        if (type instanceof TLRPC.TL_secureValueTypeUtilityBill) {
            return "utility_bill";
        }
        if (type instanceof TLRPC.TL_secureValueTypeAddress) {
            return "address";
        }
        if (type instanceof TLRPC.TL_secureValueTypeBankStatement) {
            return "bank_statement";
        }
        if (type instanceof TLRPC.TL_secureValueTypeRentalAgreement) {
            return "rental_agreement";
        }
        if (type instanceof TLRPC.TL_secureValueTypeTemporaryRegistration) {
            return "temporary_registration";
        }
        if (type instanceof TLRPC.TL_secureValueTypePassportRegistration) {
            return "passport_registration";
        }
        if (type instanceof TLRPC.TL_secureValueTypeEmail) {
            return "email";
        }
        if (type instanceof TLRPC.TL_secureValueTypePhone) {
            return "phone";
        }
        return "";
    }

    private TextDetailSecureCell getViewByType(TLRPC.TL_secureRequiredType requiredType) {
        TLRPC.TL_secureRequiredType requiredType2;
        TextDetailSecureCell view = this.typesViews.get(requiredType);
        if (view == null && (requiredType2 = this.documentsToTypesLink.get(requiredType)) != null) {
            return this.typesViews.get(requiredType2);
        }
        return view;
    }

    private String getTextForType(TLRPC.SecureValueType type) {
        if (type instanceof TLRPC.TL_secureValueTypePassport) {
            return LocaleController.getString("ActionBotDocumentPassport", R.string.ActionBotDocumentPassport);
        }
        if (type instanceof TLRPC.TL_secureValueTypeDriverLicense) {
            return LocaleController.getString("ActionBotDocumentDriverLicence", R.string.ActionBotDocumentDriverLicence);
        }
        if (type instanceof TLRPC.TL_secureValueTypeIdentityCard) {
            return LocaleController.getString("ActionBotDocumentIdentityCard", R.string.ActionBotDocumentIdentityCard);
        }
        if (type instanceof TLRPC.TL_secureValueTypeUtilityBill) {
            return LocaleController.getString("ActionBotDocumentUtilityBill", R.string.ActionBotDocumentUtilityBill);
        }
        if (type instanceof TLRPC.TL_secureValueTypeBankStatement) {
            return LocaleController.getString("ActionBotDocumentBankStatement", R.string.ActionBotDocumentBankStatement);
        }
        if (type instanceof TLRPC.TL_secureValueTypeRentalAgreement) {
            return LocaleController.getString("ActionBotDocumentRentalAgreement", R.string.ActionBotDocumentRentalAgreement);
        }
        if (type instanceof TLRPC.TL_secureValueTypeInternalPassport) {
            return LocaleController.getString("ActionBotDocumentInternalPassport", R.string.ActionBotDocumentInternalPassport);
        }
        if (type instanceof TLRPC.TL_secureValueTypePassportRegistration) {
            return LocaleController.getString("ActionBotDocumentPassportRegistration", R.string.ActionBotDocumentPassportRegistration);
        }
        if (type instanceof TLRPC.TL_secureValueTypeTemporaryRegistration) {
            return LocaleController.getString("ActionBotDocumentTemporaryRegistration", R.string.ActionBotDocumentTemporaryRegistration);
        }
        if (type instanceof TLRPC.TL_secureValueTypePhone) {
            return LocaleController.getString("ActionBotDocumentPhone", R.string.ActionBotDocumentPhone);
        }
        if (type instanceof TLRPC.TL_secureValueTypeEmail) {
            return LocaleController.getString("ActionBotDocumentEmail", R.string.ActionBotDocumentEmail);
        }
        return "";
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x01be, code lost:
        if (r40 == null) goto L74;
     */
    /* JADX WARN: Removed duplicated region for block: B:107:0x027d  */
    /* JADX WARN: Removed duplicated region for block: B:127:0x02ce A[Catch: Exception -> 0x03ed, TRY_LEAVE, TryCatch #4 {Exception -> 0x03ed, blocks: (B:123:0x02c7, B:125:0x02cb, B:127:0x02ce), top: B:315:0x02c7 }] */
    /* JADX WARN: Removed duplicated region for block: B:198:0x03f5  */
    /* JADX WARN: Removed duplicated region for block: B:203:0x0412  */
    /* JADX WARN: Removed duplicated region for block: B:207:0x041c  */
    /* JADX WARN: Removed duplicated region for block: B:208:0x042f  */
    /* JADX WARN: Removed duplicated region for block: B:211:0x0438  */
    /* JADX WARN: Removed duplicated region for block: B:212:0x0449  */
    /* JADX WARN: Removed duplicated region for block: B:215:0x044f  */
    /* JADX WARN: Removed duplicated region for block: B:228:0x048c  */
    /* JADX WARN: Removed duplicated region for block: B:252:0x0517  */
    /* JADX WARN: Removed duplicated region for block: B:292:0x05dc  */
    /* JADX WARN: Removed duplicated region for block: B:293:0x05df  */
    /* JADX WARN: Removed duplicated region for block: B:296:0x05ea  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void setTypeValue(org.telegram.tgnet.TLRPC.TL_secureRequiredType r37, java.lang.String r38, java.lang.String r39, org.telegram.tgnet.TLRPC.TL_secureRequiredType r40, java.lang.String r41, boolean r42, int r43) {
        /*
            Method dump skipped, instructions count: 1562
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.setTypeValue(org.telegram.tgnet.TLRPC$TL_secureRequiredType, java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$TL_secureRequiredType, java.lang.String, boolean, int):void");
    }

    public void checkNativeFields(boolean byEdit) {
        EditTextBoldCursor[] editTextBoldCursorArr;
        if (this.inputExtraFields == null) {
            return;
        }
        String country = this.languageMap.get(this.currentResidence);
        HashMap<String, String> map = SharedConfig.getCountryLangs();
        String lang = map.get(this.currentResidence);
        if (!this.currentType.native_names || TextUtils.isEmpty(this.currentResidence) || "EN".equals(lang)) {
            if (this.nativeInfoCell.getVisibility() != 8) {
                this.nativeInfoCell.setVisibility(8);
                this.headerCell.setVisibility(8);
                this.extraBackgroundView2.setVisibility(8);
                int a = 0;
                while (true) {
                    EditTextBoldCursor[] editTextBoldCursorArr2 = this.inputExtraFields;
                    if (a >= editTextBoldCursorArr2.length) {
                        break;
                    }
                    ((View) editTextBoldCursorArr2[a].getParent()).setVisibility(8);
                    a++;
                }
                if (((this.currentBotId != 0 || this.currentDocumentsType == null) && this.currentTypeValue != null && !this.documentOnly) || this.currentDocumentsTypeValue != null) {
                    this.sectionCell2.setBackgroundDrawable(Theme.getThemedDrawable(getParentActivity(), (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    return;
                } else {
                    this.sectionCell2.setBackgroundDrawable(Theme.getThemedDrawable(getParentActivity(), (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    return;
                }
            }
            return;
        }
        if (this.nativeInfoCell.getVisibility() != 0) {
            this.nativeInfoCell.setVisibility(0);
            this.headerCell.setVisibility(0);
            this.extraBackgroundView2.setVisibility(0);
            int a2 = 0;
            while (true) {
                editTextBoldCursorArr = this.inputExtraFields;
                if (a2 >= editTextBoldCursorArr.length) {
                    break;
                }
                ((View) editTextBoldCursorArr[a2].getParent()).setVisibility(0);
                a2++;
            }
            if (editTextBoldCursorArr[0].length() == 0 && this.inputExtraFields[1].length() == 0 && this.inputExtraFields[2].length() == 0) {
                int a3 = 0;
                while (true) {
                    boolean[] zArr = this.nonLatinNames;
                    if (a3 < zArr.length) {
                        if (!zArr[a3]) {
                            a3++;
                        } else {
                            this.inputExtraFields[0].setText(this.inputFields[0].getText());
                            this.inputExtraFields[1].setText(this.inputFields[1].getText());
                            this.inputExtraFields[2].setText(this.inputFields[2].getText());
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            this.sectionCell2.setBackgroundDrawable(Theme.getThemedDrawable(getParentActivity(), (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
        }
        this.nativeInfoCell.setText(LocaleController.formatString("PassportNativeInfo", R.string.PassportNativeInfo, country));
        String header = lang != null ? LocaleController.getServerString("PassportLanguage_" + lang) : null;
        if (header != null) {
            this.headerCell.setText(LocaleController.formatString("PassportNativeHeaderLang", R.string.PassportNativeHeaderLang, header));
        } else {
            this.headerCell.setText(LocaleController.getString("PassportNativeHeader", R.string.PassportNativeHeader));
        }
        for (int a4 = 0; a4 < 3; a4++) {
            switch (a4) {
                case 0:
                    if (header != null) {
                        this.inputExtraFields[a4].setHintText(LocaleController.getString("PassportName", R.string.PassportName));
                        break;
                    } else {
                        this.inputExtraFields[a4].setHintText(LocaleController.formatString("PassportNameCountry", R.string.PassportNameCountry, country));
                        break;
                    }
                case 1:
                    if (header != null) {
                        this.inputExtraFields[a4].setHintText(LocaleController.getString("PassportMidname", R.string.PassportMidname));
                        break;
                    } else {
                        this.inputExtraFields[a4].setHintText(LocaleController.formatString("PassportMidnameCountry", R.string.PassportMidnameCountry, country));
                        break;
                    }
                case 2:
                    if (header != null) {
                        this.inputExtraFields[a4].setHintText(LocaleController.getString("PassportSurname", R.string.PassportSurname));
                        break;
                    } else {
                        this.inputExtraFields[a4].setHintText(LocaleController.formatString("PassportSurnameCountry", R.string.PassportSurnameCountry, country));
                        break;
                    }
            }
        }
        if (byEdit) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda41
                @Override // java.lang.Runnable
                public final void run() {
                    PassportActivity.this.m3994lambda$checkNativeFields$58$orgtelegramuiPassportActivity();
                }
            });
        }
    }

    /* renamed from: lambda$checkNativeFields$58$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m3994lambda$checkNativeFields$58$orgtelegramuiPassportActivity() {
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputExtraFields;
        if (editTextBoldCursorArr != null) {
            scrollToField(editTextBoldCursorArr[0]);
        }
    }

    private String getErrorsString(HashMap<String, String> errors, HashMap<String, String> documentErrors) {
        HashMap<String, String> hashMap;
        StringBuilder stringBuilder = new StringBuilder();
        for (int a = 0; a < 2; a++) {
            if (a == 0) {
                hashMap = errors;
            } else {
                hashMap = documentErrors;
            }
            if (hashMap != null) {
                for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                    String value = entry.getValue();
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(", ");
                        value = value.toLowerCase();
                    }
                    if (value.endsWith(".")) {
                        value = value.substring(0, value.length() - 1);
                    }
                    stringBuilder.append(value);
                }
            }
        }
        int a2 = stringBuilder.length();
        if (a2 > 0) {
            stringBuilder.append('.');
        }
        return stringBuilder.toString();
    }

    private TLRPC.TL_secureValue getValueByType(TLRPC.TL_secureRequiredType requiredType, boolean check) {
        String[] keys;
        if (requiredType == null) {
            return null;
        }
        int size = this.currentForm.values.size();
        for (int a = 0; a < size; a++) {
            TLRPC.TL_secureValue secureValue = this.currentForm.values.get(a);
            if (requiredType.type.getClass() == secureValue.type.getClass()) {
                if (check) {
                    if (requiredType.selfie_required && !(secureValue.selfie instanceof TLRPC.TL_secureFile)) {
                        return null;
                    }
                    if (requiredType.translation_required && secureValue.translation.isEmpty()) {
                        return null;
                    }
                    if (isAddressDocument(requiredType.type) && secureValue.files.isEmpty()) {
                        return null;
                    }
                    if (isPersonalDocument(requiredType.type) && !(secureValue.front_side instanceof TLRPC.TL_secureFile)) {
                        return null;
                    }
                    if (((requiredType.type instanceof TLRPC.TL_secureValueTypeDriverLicense) || (requiredType.type instanceof TLRPC.TL_secureValueTypeIdentityCard)) && !(secureValue.reverse_side instanceof TLRPC.TL_secureFile)) {
                        return null;
                    }
                    if ((requiredType.type instanceof TLRPC.TL_secureValueTypePersonalDetails) || (requiredType.type instanceof TLRPC.TL_secureValueTypeAddress)) {
                        if (requiredType.type instanceof TLRPC.TL_secureValueTypePersonalDetails) {
                            keys = requiredType.native_names ? new String[]{"first_name_native", "last_name_native", "birth_date", "gender", "country_code", "residence_country_code"} : new String[]{"first_name", "last_name", "birth_date", "gender", "country_code", "residence_country_code"};
                        } else {
                            keys = new String[]{"street_line1", "street_line2", "post_code", "city", RemoteConfigConstants.ResponseFieldKey.STATE, "country_code"};
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(decryptData(secureValue.data.data, decryptValueSecret(secureValue.data.secret, secureValue.data.data_hash), secureValue.data.data_hash));
                            for (int b = 0; b < keys.length; b++) {
                                if (!jsonObject.has(keys[b]) || TextUtils.isEmpty(jsonObject.getString(keys[b]))) {
                                    return null;
                                }
                            }
                        } catch (Throwable th) {
                            return null;
                        }
                    }
                }
                return secureValue;
            }
        }
        return null;
    }

    private void openTypeActivity(TLRPC.TL_secureRequiredType requiredType, TLRPC.TL_secureRequiredType documentRequiredType, ArrayList<TLRPC.TL_secureRequiredType> availableDocumentTypes, final boolean documentOnly) {
        int activityType;
        HashMap<String, String> hashMap;
        HashMap<String, String> hashMap2;
        final int availableDocumentTypesCount = availableDocumentTypes != null ? availableDocumentTypes.size() : 0;
        final TLRPC.SecureValueType type = requiredType.type;
        TLRPC.SecureValueType documentType = documentRequiredType != null ? documentRequiredType.type : null;
        if (type instanceof TLRPC.TL_secureValueTypePersonalDetails) {
            activityType = 1;
        } else if (type instanceof TLRPC.TL_secureValueTypeAddress) {
            activityType = 2;
        } else if (type instanceof TLRPC.TL_secureValueTypePhone) {
            activityType = 3;
        } else if (!(type instanceof TLRPC.TL_secureValueTypeEmail)) {
            activityType = -1;
        } else {
            activityType = 4;
        }
        if (activityType != -1) {
            if (!documentOnly) {
                hashMap = this.errorsMap.get(getNameForType(type));
            } else {
                hashMap = null;
            }
            HashMap<String, String> errors = hashMap;
            HashMap<String, String> documentsErrors = this.errorsMap.get(getNameForType(documentType));
            TLRPC.TL_secureValue value = getValueByType(requiredType, false);
            TLRPC.TL_secureValue documentsValue = getValueByType(documentRequiredType, false);
            TLRPC.TL_account_authorizationForm tL_account_authorizationForm = this.currentForm;
            TLRPC.TL_account_password tL_account_password = this.currentPassword;
            HashMap<String, String> hashMap3 = this.typesValues.get(requiredType);
            if (documentRequiredType != null) {
                hashMap2 = this.typesValues.get(documentRequiredType);
            } else {
                hashMap2 = null;
            }
            int activityType2 = activityType;
            PassportActivity activity = new PassportActivity(activityType, tL_account_authorizationForm, tL_account_password, requiredType, value, documentRequiredType, documentsValue, hashMap3, hashMap2);
            activity.delegate = new PassportActivityDelegate() { // from class: org.telegram.ui.PassportActivity.20
                private TLRPC.InputSecureFile getInputSecureFile(SecureDocument document) {
                    if (document.inputFile != null) {
                        TLRPC.TL_inputSecureFileUploaded inputSecureFileUploaded = new TLRPC.TL_inputSecureFileUploaded();
                        inputSecureFileUploaded.id = document.inputFile.id;
                        inputSecureFileUploaded.parts = document.inputFile.parts;
                        inputSecureFileUploaded.md5_checksum = document.inputFile.md5_checksum;
                        inputSecureFileUploaded.file_hash = document.fileHash;
                        inputSecureFileUploaded.secret = document.fileSecret;
                        return inputSecureFileUploaded;
                    }
                    TLRPC.TL_inputSecureFile inputSecureFile = new TLRPC.TL_inputSecureFile();
                    inputSecureFile.id = document.secureFile.id;
                    inputSecureFile.access_hash = document.secureFile.access_hash;
                    return inputSecureFile;
                }

                public void renameFile(SecureDocument oldDocument, TLRPC.TL_secureFile newSecureFile) {
                    File oldFile = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(oldDocument);
                    String oldKey = oldDocument.secureFile.dc_id + "_" + oldDocument.secureFile.id;
                    File newFile = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(newSecureFile);
                    String newKey = newSecureFile.dc_id + "_" + newSecureFile.id;
                    oldFile.renameTo(newFile);
                    ImageLoader.getInstance().replaceImageInCache(oldKey, newKey, null, false);
                }

                /* JADX WARN: Multi-variable type inference failed */
                @Override // org.telegram.ui.PassportActivity.PassportActivityDelegate
                public void saveValue(TLRPC.TL_secureRequiredType requiredType2, String text, String json, TLRPC.TL_secureRequiredType documentRequiredType2, String documentsJson, ArrayList<SecureDocument> documents, SecureDocument selfie, ArrayList<SecureDocument> translationDocuments, SecureDocument front, SecureDocument reverse, Runnable finishRunnable, ErrorRunnable errorRunnable) {
                    TLRPC.TL_inputSecureValue fileInputSecureValue;
                    TLRPC.TL_inputSecureValue inputSecureValue;
                    TLRPC.SecurePlainData plainData;
                    TLRPC.TL_inputSecureValue inputSecureValue2 = null;
                    if (!TextUtils.isEmpty(json)) {
                        inputSecureValue2 = new TLRPC.TL_inputSecureValue();
                        inputSecureValue2.type = requiredType2.type;
                        inputSecureValue2.flags |= 1;
                        EncryptionResult result = PassportActivity.this.encryptData(AndroidUtilities.getStringBytes(json));
                        inputSecureValue2.data = new TLRPC.TL_secureData();
                        inputSecureValue2.data.data = result.encryptedData;
                        inputSecureValue2.data.data_hash = result.fileHash;
                        inputSecureValue2.data.secret = result.fileSecret;
                    } else if (!TextUtils.isEmpty(text)) {
                        TLRPC.SecureValueType secureValueType = type;
                        if (secureValueType instanceof TLRPC.TL_secureValueTypeEmail) {
                            TLRPC.TL_securePlainEmail securePlainEmail = new TLRPC.TL_securePlainEmail();
                            securePlainEmail.email = text;
                            plainData = securePlainEmail;
                        } else if (secureValueType instanceof TLRPC.TL_secureValueTypePhone) {
                            TLRPC.TL_securePlainPhone securePlainPhone = new TLRPC.TL_securePlainPhone();
                            securePlainPhone.phone = text;
                            plainData = securePlainPhone;
                        } else {
                            return;
                        }
                        inputSecureValue2 = new TLRPC.TL_inputSecureValue();
                        inputSecureValue2.type = requiredType2.type;
                        inputSecureValue2.flags |= 32;
                        inputSecureValue2.plain_data = plainData;
                    }
                    if (!documentOnly && inputSecureValue2 == null) {
                        if (errorRunnable != null) {
                            errorRunnable.onError(null, null);
                            return;
                        }
                        return;
                    }
                    if (documentRequiredType2 != null) {
                        TLRPC.TL_inputSecureValue fileInputSecureValue2 = new TLRPC.TL_inputSecureValue();
                        fileInputSecureValue2.type = documentRequiredType2.type;
                        if (!TextUtils.isEmpty(documentsJson)) {
                            fileInputSecureValue2.flags |= 1;
                            EncryptionResult result2 = PassportActivity.this.encryptData(AndroidUtilities.getStringBytes(documentsJson));
                            fileInputSecureValue2.data = new TLRPC.TL_secureData();
                            fileInputSecureValue2.data.data = result2.encryptedData;
                            fileInputSecureValue2.data.data_hash = result2.fileHash;
                            fileInputSecureValue2.data.secret = result2.fileSecret;
                        }
                        if (front != null) {
                            fileInputSecureValue2.front_side = getInputSecureFile(front);
                            fileInputSecureValue2.flags |= 2;
                        }
                        if (reverse != null) {
                            fileInputSecureValue2.reverse_side = getInputSecureFile(reverse);
                            fileInputSecureValue2.flags |= 4;
                        }
                        if (selfie != null) {
                            fileInputSecureValue2.selfie = getInputSecureFile(selfie);
                            fileInputSecureValue2.flags |= 8;
                        }
                        if (translationDocuments != null && !translationDocuments.isEmpty()) {
                            fileInputSecureValue2.flags |= 64;
                            int size = translationDocuments.size();
                            for (int a = 0; a < size; a++) {
                                fileInputSecureValue2.translation.add(getInputSecureFile(translationDocuments.get(a)));
                            }
                        }
                        if (documents != null && !documents.isEmpty()) {
                            fileInputSecureValue2.flags |= 16;
                            int size2 = documents.size();
                            for (int a2 = 0; a2 < size2; a2++) {
                                fileInputSecureValue2.files.add(getInputSecureFile(documents.get(a2)));
                            }
                        }
                        if (!documentOnly) {
                            inputSecureValue = inputSecureValue2;
                            fileInputSecureValue = fileInputSecureValue2;
                        } else {
                            inputSecureValue = fileInputSecureValue2;
                            fileInputSecureValue = null;
                        }
                    } else {
                        inputSecureValue = inputSecureValue2;
                        fileInputSecureValue = null;
                    }
                    TLRPC.TL_inputSecureValue finalFileInputSecureValue = fileInputSecureValue;
                    TLRPC.TL_account_saveSecureValue req = new TLRPC.TL_account_saveSecureValue();
                    req.value = inputSecureValue;
                    req.secure_secret_id = PassportActivity.this.secureSecretId;
                    ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new AnonymousClass1(errorRunnable, text, req, documentRequiredType2, requiredType2, documents, selfie, front, reverse, translationDocuments, json, documentsJson, finishRunnable, this, finalFileInputSecureValue));
                }

                /* renamed from: org.telegram.ui.PassportActivity$20$1 */
                /* loaded from: classes4.dex */
                public class AnonymousClass1 implements RequestDelegate {
                    final /* synthetic */ PassportActivityDelegate val$currentDelegate;
                    final /* synthetic */ TLRPC.TL_secureRequiredType val$documentRequiredType;
                    final /* synthetic */ ArrayList val$documents;
                    final /* synthetic */ String val$documentsJson;
                    final /* synthetic */ ErrorRunnable val$errorRunnable;
                    final /* synthetic */ TLRPC.TL_inputSecureValue val$finalFileInputSecureValue;
                    final /* synthetic */ Runnable val$finishRunnable;
                    final /* synthetic */ SecureDocument val$front;
                    final /* synthetic */ String val$json;
                    final /* synthetic */ TLRPC.TL_account_saveSecureValue val$req;
                    final /* synthetic */ TLRPC.TL_secureRequiredType val$requiredType;
                    final /* synthetic */ SecureDocument val$reverse;
                    final /* synthetic */ SecureDocument val$selfie;
                    final /* synthetic */ String val$text;
                    final /* synthetic */ ArrayList val$translationDocuments;

                    AnonymousClass1(ErrorRunnable errorRunnable, String str, TLRPC.TL_account_saveSecureValue tL_account_saveSecureValue, TLRPC.TL_secureRequiredType tL_secureRequiredType, TLRPC.TL_secureRequiredType tL_secureRequiredType2, ArrayList arrayList, SecureDocument secureDocument, SecureDocument secureDocument2, SecureDocument secureDocument3, ArrayList arrayList2, String str2, String str3, Runnable runnable, PassportActivityDelegate passportActivityDelegate, TLRPC.TL_inputSecureValue tL_inputSecureValue) {
                        AnonymousClass20.this = this$1;
                        this.val$errorRunnable = errorRunnable;
                        this.val$text = str;
                        this.val$req = tL_account_saveSecureValue;
                        this.val$documentRequiredType = tL_secureRequiredType;
                        this.val$requiredType = tL_secureRequiredType2;
                        this.val$documents = arrayList;
                        this.val$selfie = secureDocument;
                        this.val$front = secureDocument2;
                        this.val$reverse = secureDocument3;
                        this.val$translationDocuments = arrayList2;
                        this.val$json = str2;
                        this.val$documentsJson = str3;
                        this.val$finishRunnable = runnable;
                        this.val$currentDelegate = passportActivityDelegate;
                        this.val$finalFileInputSecureValue = tL_inputSecureValue;
                    }

                    /* renamed from: onResult */
                    public void m4059lambda$run$4$orgtelegramuiPassportActivity$20$1(final TLRPC.TL_error error, final TLRPC.TL_secureValue newValue, final TLRPC.TL_secureValue newPendingValue) {
                        final ErrorRunnable errorRunnable = this.val$errorRunnable;
                        final String str = this.val$text;
                        final TLRPC.TL_account_saveSecureValue tL_account_saveSecureValue = this.val$req;
                        final boolean z = documentOnly;
                        final TLRPC.TL_secureRequiredType tL_secureRequiredType = this.val$documentRequiredType;
                        final TLRPC.TL_secureRequiredType tL_secureRequiredType2 = this.val$requiredType;
                        final ArrayList arrayList = this.val$documents;
                        final SecureDocument secureDocument = this.val$selfie;
                        final SecureDocument secureDocument2 = this.val$front;
                        final SecureDocument secureDocument3 = this.val$reverse;
                        final ArrayList arrayList2 = this.val$translationDocuments;
                        final String str2 = this.val$json;
                        final String str3 = this.val$documentsJson;
                        final int i = availableDocumentTypesCount;
                        final Runnable runnable = this.val$finishRunnable;
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$20$1$$ExternalSyntheticLambda1
                            @Override // java.lang.Runnable
                            public final void run() {
                                PassportActivity.AnonymousClass20.AnonymousClass1.this.m4056lambda$onResult$0$orgtelegramuiPassportActivity$20$1(error, errorRunnable, str, tL_account_saveSecureValue, z, tL_secureRequiredType, tL_secureRequiredType2, newValue, newPendingValue, arrayList, secureDocument, secureDocument2, secureDocument3, arrayList2, str2, str3, i, runnable);
                            }
                        });
                    }

                    /* renamed from: lambda$onResult$0$org-telegram-ui-PassportActivity$20$1 */
                    public /* synthetic */ void m4056lambda$onResult$0$orgtelegramuiPassportActivity$20$1(TLRPC.TL_error error, ErrorRunnable errorRunnable, String text, TLRPC.TL_account_saveSecureValue req, boolean documentOnly, TLRPC.TL_secureRequiredType documentRequiredType, TLRPC.TL_secureRequiredType requiredType, TLRPC.TL_secureValue newValue, TLRPC.TL_secureValue newPendingValue, ArrayList documents, SecureDocument selfie, SecureDocument front, SecureDocument reverse, ArrayList translationDocuments, String json, String documentsJson, int availableDocumentTypesCount, Runnable finishRunnable) {
                        int size;
                        int size2;
                        int size3;
                        ArrayList arrayList = documents;
                        ArrayList arrayList2 = translationDocuments;
                        if (error != null) {
                            if (errorRunnable != null) {
                                errorRunnable.onError(error.text, text);
                            }
                            AlertsCreator.processError(PassportActivity.this.currentAccount, error, PassportActivity.this, req, text);
                            return;
                        }
                        if (!documentOnly) {
                            PassportActivity.this.removeValue(requiredType);
                            PassportActivity.this.removeValue(documentRequiredType);
                        } else if (documentRequiredType != null) {
                            PassportActivity.this.removeValue(documentRequiredType);
                        } else {
                            PassportActivity.this.removeValue(requiredType);
                        }
                        if (newValue != null) {
                            PassportActivity.this.currentForm.values.add(newValue);
                        }
                        if (newPendingValue != null) {
                            PassportActivity.this.currentForm.values.add(newPendingValue);
                        }
                        if (arrayList != null && !documents.isEmpty()) {
                            int a = 0;
                            int size4 = documents.size();
                            while (a < size4) {
                                SecureDocument document = (SecureDocument) arrayList.get(a);
                                if (document.inputFile == null) {
                                    size3 = size4;
                                } else {
                                    int b = 0;
                                    int size22 = newValue.files.size();
                                    while (true) {
                                        if (b >= size22) {
                                            size3 = size4;
                                            break;
                                        }
                                        int size23 = size22;
                                        TLRPC.SecureFile file = newValue.files.get(b);
                                        size3 = size4;
                                        if (file instanceof TLRPC.TL_secureFile) {
                                            TLRPC.TL_secureFile secureFile = (TLRPC.TL_secureFile) file;
                                            if (Utilities.arraysEquals(document.fileSecret, 0, secureFile.secret, 0)) {
                                                renameFile(document, secureFile);
                                                break;
                                            }
                                        }
                                        b++;
                                        size22 = size23;
                                        size4 = size3;
                                    }
                                }
                                a++;
                                arrayList = documents;
                                size4 = size3;
                            }
                        }
                        if (selfie != null && selfie.inputFile != null && (newValue.selfie instanceof TLRPC.TL_secureFile)) {
                            TLRPC.TL_secureFile secureFile2 = (TLRPC.TL_secureFile) newValue.selfie;
                            if (Utilities.arraysEquals(selfie.fileSecret, 0, secureFile2.secret, 0)) {
                                renameFile(selfie, secureFile2);
                            }
                        }
                        if (front != null && front.inputFile != null && (newValue.front_side instanceof TLRPC.TL_secureFile)) {
                            TLRPC.TL_secureFile secureFile3 = (TLRPC.TL_secureFile) newValue.front_side;
                            if (Utilities.arraysEquals(front.fileSecret, 0, secureFile3.secret, 0)) {
                                renameFile(front, secureFile3);
                            }
                        }
                        if (reverse != null && reverse.inputFile != null && (newValue.reverse_side instanceof TLRPC.TL_secureFile)) {
                            TLRPC.TL_secureFile secureFile4 = (TLRPC.TL_secureFile) newValue.reverse_side;
                            if (Utilities.arraysEquals(reverse.fileSecret, 0, secureFile4.secret, 0)) {
                                renameFile(reverse, secureFile4);
                            }
                        }
                        if (arrayList2 != null && !translationDocuments.isEmpty()) {
                            int a2 = 0;
                            int size5 = translationDocuments.size();
                            while (a2 < size5) {
                                SecureDocument document2 = (SecureDocument) arrayList2.get(a2);
                                if (document2.inputFile == null) {
                                    size = size5;
                                } else {
                                    int b2 = 0;
                                    int size24 = newValue.translation.size();
                                    while (true) {
                                        if (b2 >= size24) {
                                            size = size5;
                                            break;
                                        }
                                        TLRPC.SecureFile file2 = newValue.translation.get(b2);
                                        if (!(file2 instanceof TLRPC.TL_secureFile)) {
                                            size = size5;
                                            size2 = size24;
                                        } else {
                                            TLRPC.TL_secureFile secureFile5 = (TLRPC.TL_secureFile) file2;
                                            size = size5;
                                            size2 = size24;
                                            if (Utilities.arraysEquals(document2.fileSecret, 0, secureFile5.secret, 0)) {
                                                renameFile(document2, secureFile5);
                                                break;
                                            }
                                        }
                                        b2++;
                                        size5 = size;
                                        size24 = size2;
                                    }
                                }
                                a2++;
                                arrayList2 = translationDocuments;
                                size5 = size;
                            }
                        }
                        PassportActivity.this.setTypeValue(requiredType, text, json, documentRequiredType, documentsJson, documentOnly, availableDocumentTypesCount);
                        if (finishRunnable != null) {
                            finishRunnable.run();
                        }
                    }

                    @Override // org.telegram.tgnet.RequestDelegate
                    public void run(TLObject response, final TLRPC.TL_error error) {
                        if (error != null) {
                            if (error.text.equals("EMAIL_VERIFICATION_NEEDED")) {
                                TLRPC.TL_account_sendVerifyEmailCode req = new TLRPC.TL_account_sendVerifyEmailCode();
                                req.email = this.val$text;
                                ConnectionsManager connectionsManager = ConnectionsManager.getInstance(PassportActivity.this.currentAccount);
                                final String str = this.val$text;
                                final TLRPC.TL_secureRequiredType tL_secureRequiredType = this.val$requiredType;
                                final PassportActivityDelegate passportActivityDelegate = this.val$currentDelegate;
                                final ErrorRunnable errorRunnable = this.val$errorRunnable;
                                connectionsManager.sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PassportActivity$20$1$$ExternalSyntheticLambda3
                                    @Override // org.telegram.tgnet.RequestDelegate
                                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                        PassportActivity.AnonymousClass20.AnonymousClass1.this.m4058lambda$run$2$orgtelegramuiPassportActivity$20$1(str, tL_secureRequiredType, passportActivityDelegate, errorRunnable, tLObject, tL_error);
                                    }
                                });
                                return;
                            } else if (error.text.equals("PHONE_VERIFICATION_NEEDED")) {
                                final ErrorRunnable errorRunnable2 = this.val$errorRunnable;
                                final String str2 = this.val$text;
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$20$1$$ExternalSyntheticLambda2
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        PassportActivity.ErrorRunnable.this.onError(error.text, str2);
                                    }
                                });
                                return;
                            }
                        }
                        if (error == null && this.val$finalFileInputSecureValue != null) {
                            final TLRPC.TL_secureValue pendingValue = (TLRPC.TL_secureValue) response;
                            TLRPC.TL_account_saveSecureValue req2 = new TLRPC.TL_account_saveSecureValue();
                            req2.value = this.val$finalFileInputSecureValue;
                            req2.secure_secret_id = PassportActivity.this.secureSecretId;
                            ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req2, new RequestDelegate() { // from class: org.telegram.ui.PassportActivity$20$1$$ExternalSyntheticLambda4
                                @Override // org.telegram.tgnet.RequestDelegate
                                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                    PassportActivity.AnonymousClass20.AnonymousClass1.this.m4059lambda$run$4$orgtelegramuiPassportActivity$20$1(pendingValue, tLObject, tL_error);
                                }
                            });
                            return;
                        }
                        m4059lambda$run$4$orgtelegramuiPassportActivity$20$1(error, (TLRPC.TL_secureValue) response, null);
                    }

                    /* renamed from: lambda$run$2$org-telegram-ui-PassportActivity$20$1 */
                    public /* synthetic */ void m4058lambda$run$2$orgtelegramuiPassportActivity$20$1(final String text, final TLRPC.TL_secureRequiredType requiredType, final PassportActivityDelegate currentDelegate, final ErrorRunnable errorRunnable, final TLObject response1, final TLRPC.TL_error error1) {
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$20$1$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                PassportActivity.AnonymousClass20.AnonymousClass1.this.m4057lambda$run$1$orgtelegramuiPassportActivity$20$1(response1, text, requiredType, currentDelegate, error1, errorRunnable);
                            }
                        });
                    }

                    /* renamed from: lambda$run$1$org-telegram-ui-PassportActivity$20$1 */
                    public /* synthetic */ void m4057lambda$run$1$orgtelegramuiPassportActivity$20$1(TLObject response1, String text, TLRPC.TL_secureRequiredType requiredType, PassportActivityDelegate currentDelegate, TLRPC.TL_error error1, ErrorRunnable errorRunnable) {
                        if (response1 == null) {
                            PassportActivity.this.showAlertWithText(LocaleController.getString("PassportEmail", R.string.PassportEmail), error1.text);
                            if (errorRunnable != null) {
                                errorRunnable.onError(error1.text, text);
                                return;
                            }
                            return;
                        }
                        TLRPC.TL_account_sentEmailCode res = (TLRPC.TL_account_sentEmailCode) response1;
                        HashMap<String, String> values = new HashMap<>();
                        values.put("email", text);
                        values.put("pattern", res.email_pattern);
                        PassportActivity activity1 = new PassportActivity(6, PassportActivity.this.currentForm, PassportActivity.this.currentPassword, requiredType, (TLRPC.TL_secureValue) null, (TLRPC.TL_secureRequiredType) null, (TLRPC.TL_secureValue) null, values, (HashMap<String, String>) null);
                        activity1.currentAccount = PassportActivity.this.currentAccount;
                        activity1.emailCodeLength = res.length;
                        activity1.saltedPassword = PassportActivity.this.saltedPassword;
                        activity1.secureSecret = PassportActivity.this.secureSecret;
                        activity1.delegate = currentDelegate;
                        PassportActivity.this.presentFragment(activity1, true);
                    }
                }

                @Override // org.telegram.ui.PassportActivity.PassportActivityDelegate
                public SecureDocument saveFile(TLRPC.TL_secureFile secureFile) {
                    String path = FileLoader.getDirectory(4) + "/" + secureFile.dc_id + "_" + secureFile.id + ".jpg";
                    EncryptionResult result = PassportActivity.this.createSecureDocument(path);
                    return new SecureDocument(result.secureDocumentKey, secureFile, path, result.fileHash, result.fileSecret);
                }

                @Override // org.telegram.ui.PassportActivity.PassportActivityDelegate
                public void deleteValue(TLRPC.TL_secureRequiredType requiredType2, TLRPC.TL_secureRequiredType documentRequiredType2, ArrayList<TLRPC.TL_secureRequiredType> documentRequiredTypes, boolean deleteType, Runnable finishRunnable, ErrorRunnable errorRunnable) {
                    PassportActivity.this.deleteValueInternal(requiredType2, documentRequiredType2, documentRequiredTypes, deleteType, finishRunnable, errorRunnable, documentOnly);
                }
            };
            activity.currentAccount = this.currentAccount;
            activity.saltedPassword = this.saltedPassword;
            activity.secureSecret = this.secureSecret;
            activity.currentBotId = this.currentBotId;
            activity.fieldsErrors = errors;
            activity.documentOnly = documentOnly;
            activity.documentsErrors = documentsErrors;
            activity.availableDocumentTypes = availableDocumentTypes;
            if (activityType2 == 4) {
                activity.currentEmail = this.currentEmail;
            }
            presentFragment(activity);
        }
    }

    public TLRPC.TL_secureValue removeValue(TLRPC.TL_secureRequiredType requiredType) {
        if (requiredType == null) {
            return null;
        }
        int size = this.currentForm.values.size();
        for (int a = 0; a < size; a++) {
            TLRPC.TL_secureValue secureValue = this.currentForm.values.get(a);
            if (requiredType.type.getClass() == secureValue.type.getClass()) {
                return this.currentForm.values.remove(a);
            }
        }
        return null;
    }

    public void deleteValueInternal(final TLRPC.TL_secureRequiredType requiredType, final TLRPC.TL_secureRequiredType documentRequiredType, final ArrayList<TLRPC.TL_secureRequiredType> documentRequiredTypes, final boolean deleteType, final Runnable finishRunnable, final ErrorRunnable errorRunnable, final boolean documentOnly) {
        if (requiredType == null) {
            return;
        }
        TLRPC.TL_account_deleteSecureValue req = new TLRPC.TL_account_deleteSecureValue();
        if (documentOnly && documentRequiredType != null) {
            req.types.add(documentRequiredType.type);
        } else {
            if (deleteType) {
                req.types.add(requiredType.type);
            }
            if (documentRequiredType != null) {
                req.types.add(documentRequiredType.type);
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda63
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PassportActivity.this.m4043lambda$deleteValueInternal$60$orgtelegramuiPassportActivity(errorRunnable, documentOnly, documentRequiredType, requiredType, deleteType, documentRequiredTypes, finishRunnable, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$deleteValueInternal$60$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4043lambda$deleteValueInternal$60$orgtelegramuiPassportActivity(final ErrorRunnable errorRunnable, final boolean documentOnly, final TLRPC.TL_secureRequiredType documentRequiredType, final TLRPC.TL_secureRequiredType requiredType, final boolean deleteType, final ArrayList documentRequiredTypes, final Runnable finishRunnable, TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda56
            @Override // java.lang.Runnable
            public final void run() {
                PassportActivity.this.m4042lambda$deleteValueInternal$59$orgtelegramuiPassportActivity(error, errorRunnable, documentOnly, documentRequiredType, requiredType, deleteType, documentRequiredTypes, finishRunnable);
            }
        });
    }

    /* renamed from: lambda$deleteValueInternal$59$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4042lambda$deleteValueInternal$59$orgtelegramuiPassportActivity(TLRPC.TL_error error, ErrorRunnable errorRunnable, boolean documentOnly, TLRPC.TL_secureRequiredType documentRequiredType, TLRPC.TL_secureRequiredType requiredType, boolean deleteType, ArrayList documentRequiredTypes, Runnable finishRunnable) {
        TLRPC.TL_secureRequiredType documentsType;
        String documentJson;
        String json;
        LinearLayout linearLayout;
        if (error != null) {
            if (errorRunnable != null) {
                errorRunnable.onError(error.text, null);
            }
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
            return;
        }
        if (documentOnly) {
            if (documentRequiredType != null) {
                removeValue(documentRequiredType);
            } else {
                removeValue(requiredType);
            }
        } else {
            if (deleteType) {
                removeValue(requiredType);
            }
            removeValue(documentRequiredType);
        }
        if (this.currentActivityType == 8) {
            TextDetailSecureCell view = this.typesViews.remove(requiredType);
            if (view != null) {
                this.linearLayout2.removeView(view);
                View child = this.linearLayout2.getChildAt(linearLayout.getChildCount() - 6);
                if (child instanceof TextDetailSecureCell) {
                    ((TextDetailSecureCell) child).setNeedDivider(false);
                }
            }
            updateManageVisibility();
        } else {
            String documentJson2 = null;
            TLRPC.TL_secureRequiredType documentsType2 = documentRequiredType;
            if (documentsType2 != null && documentRequiredTypes != null && documentRequiredTypes.size() > 1) {
                int a = 0;
                int count = documentRequiredTypes.size();
                while (true) {
                    if (a >= count) {
                        break;
                    }
                    TLRPC.TL_secureRequiredType documentType = (TLRPC.TL_secureRequiredType) documentRequiredTypes.get(a);
                    TLRPC.TL_secureValue documentValue = getValueByType(documentType, false);
                    if (documentValue == null) {
                        a++;
                    } else {
                        if (documentValue.data != null) {
                            documentJson2 = decryptData(documentValue.data.data, decryptValueSecret(documentValue.data.secret, documentValue.data.data_hash), documentValue.data.data_hash);
                        }
                        documentsType2 = documentType;
                    }
                }
                if (documentsType2 != null) {
                    documentJson = documentJson2;
                    documentsType = documentsType2;
                } else {
                    documentJson = documentJson2;
                    documentsType = (TLRPC.TL_secureRequiredType) documentRequiredTypes.get(0);
                }
            } else {
                documentJson = null;
                documentsType = documentsType2;
            }
            if (deleteType) {
                setTypeValue(requiredType, null, null, documentsType, documentJson, documentOnly, documentRequiredTypes != null ? documentRequiredTypes.size() : 0);
            } else {
                TLRPC.TL_secureValue value = getValueByType(requiredType, false);
                if (value != null && value.data != null) {
                    String json2 = decryptData(value.data.data, decryptValueSecret(value.data.secret, value.data.data_hash), value.data.data_hash);
                    json = json2;
                } else {
                    json = null;
                }
                setTypeValue(requiredType, null, json, documentsType, documentJson, documentOnly, documentRequiredTypes != null ? documentRequiredTypes.size() : 0);
            }
        }
        if (finishRunnable != null) {
            finishRunnable.run();
        }
    }

    private TextDetailSecureCell addField(Context context, final TLRPC.TL_secureRequiredType requiredType, final ArrayList<TLRPC.TL_secureRequiredType> documentRequiredTypes, final boolean documentOnly, boolean last) {
        String json;
        String text;
        String documentJson;
        TLRPC.TL_secureRequiredType documentsType;
        int count;
        boolean found;
        String documentJson2;
        LinearLayout linearLayout;
        String text2;
        String text3;
        int availableDocumentTypesCount = documentRequiredTypes != null ? documentRequiredTypes.size() : 0;
        TextDetailSecureCell view = new TextDetailSecureCell(context);
        view.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        if (requiredType.type instanceof TLRPC.TL_secureValueTypePersonalDetails) {
            if (documentRequiredTypes == null || documentRequiredTypes.isEmpty()) {
                text3 = LocaleController.getString("PassportPersonalDetails", R.string.PassportPersonalDetails);
            } else if (documentOnly && documentRequiredTypes.size() == 1) {
                text3 = getTextForType(documentRequiredTypes.get(0).type);
            } else {
                text3 = (!documentOnly || documentRequiredTypes.size() != 2) ? LocaleController.getString("PassportIdentityDocument", R.string.PassportIdentityDocument) : LocaleController.formatString("PassportTwoDocuments", R.string.PassportTwoDocuments, getTextForType(documentRequiredTypes.get(0).type), getTextForType(documentRequiredTypes.get(1).type));
            }
            view.setTextAndValue(text3, "", !last);
        } else if (requiredType.type instanceof TLRPC.TL_secureValueTypeAddress) {
            if (documentRequiredTypes == null || documentRequiredTypes.isEmpty()) {
                text2 = LocaleController.getString("PassportAddress", R.string.PassportAddress);
            } else if (documentOnly && documentRequiredTypes.size() == 1) {
                text2 = getTextForType(documentRequiredTypes.get(0).type);
            } else {
                text2 = (!documentOnly || documentRequiredTypes.size() != 2) ? LocaleController.getString("PassportResidentialAddress", R.string.PassportResidentialAddress) : LocaleController.formatString("PassportTwoDocuments", R.string.PassportTwoDocuments, getTextForType(documentRequiredTypes.get(0).type), getTextForType(documentRequiredTypes.get(1).type));
            }
            view.setTextAndValue(text2, "", !last);
        } else if (requiredType.type instanceof TLRPC.TL_secureValueTypePhone) {
            view.setTextAndValue(LocaleController.getString("PassportPhone", R.string.PassportPhone), "", !last);
        } else if (requiredType.type instanceof TLRPC.TL_secureValueTypeEmail) {
            view.setTextAndValue(LocaleController.getString("PassportEmail", R.string.PassportEmail), "", !last);
        }
        if (this.currentActivityType == 8) {
            this.linearLayout2.addView(view, linearLayout.getChildCount() - 5, LayoutHelper.createLinear(-1, -2));
        } else {
            this.linearLayout2.addView(view, LayoutHelper.createLinear(-1, -2));
        }
        view.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda23
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PassportActivity.this.m3992lambda$addField$64$orgtelegramuiPassportActivity(documentRequiredTypes, requiredType, documentOnly, view2);
            }
        });
        this.typesViews.put(requiredType, view);
        String documentJson3 = null;
        this.typesValues.put(requiredType, new HashMap<>());
        TLRPC.TL_secureValue value = getValueByType(requiredType, false);
        if (value != null) {
            if (value.plain_data instanceof TLRPC.TL_securePlainEmail) {
                String text4 = ((TLRPC.TL_securePlainEmail) value.plain_data).email;
                text = text4;
                json = null;
            } else if (value.plain_data instanceof TLRPC.TL_securePlainPhone) {
                String text5 = ((TLRPC.TL_securePlainPhone) value.plain_data).phone;
                text = text5;
                json = null;
            } else if (value.data != null) {
                String json2 = decryptData(value.data.data, decryptValueSecret(value.data.secret, value.data.data_hash), value.data.data_hash);
                text = null;
                json = json2;
            }
            TLRPC.TL_secureRequiredType documentsType2 = null;
            if (documentRequiredTypes == null && !documentRequiredTypes.isEmpty()) {
                boolean found2 = false;
                int a = 0;
                int count2 = documentRequiredTypes.size();
                while (a < count2) {
                    TLRPC.TL_secureRequiredType documentType = documentRequiredTypes.get(a);
                    String documentJson4 = documentJson3;
                    this.typesValues.put(documentType, new HashMap<>());
                    this.documentsToTypesLink.put(documentType, requiredType);
                    if (!found2) {
                        TLRPC.TL_secureValue documentValue = getValueByType(documentType, false);
                        if (documentValue == null) {
                            found = found2;
                            count = count2;
                        } else {
                            if (documentValue.data != null) {
                                count = count2;
                                documentJson2 = decryptData(documentValue.data.data, decryptValueSecret(documentValue.data.secret, documentValue.data.data_hash), documentValue.data.data_hash);
                            } else {
                                count = count2;
                                documentJson2 = documentJson4;
                            }
                            documentsType2 = documentType;
                            found2 = true;
                            documentJson3 = documentJson2;
                            a++;
                            count2 = count;
                        }
                    } else {
                        found = found2;
                        count = count2;
                    }
                    documentJson3 = documentJson4;
                    found2 = found;
                    a++;
                    count2 = count;
                }
                documentJson = documentJson3;
                if (documentsType2 != null) {
                    documentsType = documentsType2;
                } else {
                    TLRPC.TL_secureRequiredType documentsType3 = documentRequiredTypes.get(0);
                    documentsType = documentsType3;
                }
            } else {
                documentsType = null;
                documentJson = null;
            }
            setTypeValue(requiredType, text, json, documentsType, documentJson, documentOnly, availableDocumentTypesCount);
            return view;
        }
        text = null;
        json = null;
        TLRPC.TL_secureRequiredType documentsType22 = null;
        if (documentRequiredTypes == null) {
        }
        documentsType = null;
        documentJson = null;
        setTypeValue(requiredType, text, json, documentsType, documentJson, documentOnly, availableDocumentTypesCount);
        return view;
    }

    /* renamed from: lambda$addField$64$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m3992lambda$addField$64$orgtelegramuiPassportActivity(final ArrayList documentRequiredTypes, final TLRPC.TL_secureRequiredType requiredType, final boolean documentOnly, View v) {
        String str;
        int i;
        TLRPC.TL_secureRequiredType documentsType = null;
        if (documentRequiredTypes != null) {
            int count = documentRequiredTypes.size();
            for (int a = 0; a < count; a++) {
                TLRPC.TL_secureRequiredType documentType = (TLRPC.TL_secureRequiredType) documentRequiredTypes.get(a);
                if (getValueByType(documentType, false) != null || count == 1) {
                    documentsType = documentType;
                    break;
                }
            }
        }
        if ((requiredType.type instanceof TLRPC.TL_secureValueTypePersonalDetails) || (requiredType.type instanceof TLRPC.TL_secureValueTypeAddress)) {
            if (documentsType == null && documentRequiredTypes != null && !documentRequiredTypes.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                if (requiredType.type instanceof TLRPC.TL_secureValueTypePersonalDetails) {
                    builder.setTitle(LocaleController.getString("PassportIdentityDocument", R.string.PassportIdentityDocument));
                } else if (requiredType.type instanceof TLRPC.TL_secureValueTypeAddress) {
                    builder.setTitle(LocaleController.getString("PassportAddress", R.string.PassportAddress));
                }
                ArrayList<String> strings = new ArrayList<>();
                int count2 = documentRequiredTypes.size();
                for (int a2 = 0; a2 < count2; a2++) {
                    TLRPC.TL_secureRequiredType documentType2 = (TLRPC.TL_secureRequiredType) documentRequiredTypes.get(a2);
                    if (documentType2.type instanceof TLRPC.TL_secureValueTypeDriverLicense) {
                        strings.add(LocaleController.getString("PassportAddLicence", R.string.PassportAddLicence));
                    } else if (documentType2.type instanceof TLRPC.TL_secureValueTypePassport) {
                        strings.add(LocaleController.getString("PassportAddPassport", R.string.PassportAddPassport));
                    } else if (documentType2.type instanceof TLRPC.TL_secureValueTypeInternalPassport) {
                        strings.add(LocaleController.getString("PassportAddInternalPassport", R.string.PassportAddInternalPassport));
                    } else if (documentType2.type instanceof TLRPC.TL_secureValueTypeIdentityCard) {
                        strings.add(LocaleController.getString("PassportAddCard", R.string.PassportAddCard));
                    } else if (documentType2.type instanceof TLRPC.TL_secureValueTypeUtilityBill) {
                        strings.add(LocaleController.getString("PassportAddBill", R.string.PassportAddBill));
                    } else if (documentType2.type instanceof TLRPC.TL_secureValueTypeBankStatement) {
                        strings.add(LocaleController.getString("PassportAddBank", R.string.PassportAddBank));
                    } else if (documentType2.type instanceof TLRPC.TL_secureValueTypeRentalAgreement) {
                        strings.add(LocaleController.getString("PassportAddAgreement", R.string.PassportAddAgreement));
                    } else if (documentType2.type instanceof TLRPC.TL_secureValueTypeTemporaryRegistration) {
                        strings.add(LocaleController.getString("PassportAddTemporaryRegistration", R.string.PassportAddTemporaryRegistration));
                    } else if (documentType2.type instanceof TLRPC.TL_secureValueTypePassportRegistration) {
                        strings.add(LocaleController.getString("PassportAddPassportRegistration", R.string.PassportAddPassportRegistration));
                    }
                }
                builder.setItems((CharSequence[]) strings.toArray(new CharSequence[0]), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda72
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        PassportActivity.this.m3989lambda$addField$61$orgtelegramuiPassportActivity(requiredType, documentRequiredTypes, documentOnly, dialogInterface, i2);
                    }
                });
                showDialog(builder.create());
                return;
            }
        } else {
            boolean phoneField = requiredType.type instanceof TLRPC.TL_secureValueTypePhone;
            if (phoneField || (requiredType.type instanceof TLRPC.TL_secureValueTypeEmail)) {
                TLRPC.TL_secureValue value = getValueByType(requiredType, false);
                if (value != null) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
                    builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda73
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i2) {
                            PassportActivity.this.m3991lambda$addField$63$orgtelegramuiPassportActivity(requiredType, documentOnly, dialogInterface, i2);
                        }
                    });
                    builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    if (phoneField) {
                        i = R.string.PassportDeletePhoneAlert;
                        str = "PassportDeletePhoneAlert";
                    } else {
                        i = R.string.PassportDeleteEmailAlert;
                        str = "PassportDeleteEmailAlert";
                    }
                    builder2.setMessage(LocaleController.getString(str, i));
                    showDialog(builder2.create());
                    return;
                }
            }
        }
        openTypeActivity(requiredType, documentsType, documentRequiredTypes, documentOnly);
    }

    /* renamed from: lambda$addField$61$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m3989lambda$addField$61$orgtelegramuiPassportActivity(TLRPC.TL_secureRequiredType requiredType, ArrayList documentRequiredTypes, boolean documentOnly, DialogInterface dialog, int which) {
        openTypeActivity(requiredType, (TLRPC.TL_secureRequiredType) documentRequiredTypes.get(which), documentRequiredTypes, documentOnly);
    }

    /* renamed from: lambda$addField$63$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m3991lambda$addField$63$orgtelegramuiPassportActivity(TLRPC.TL_secureRequiredType requiredType, boolean documentOnly, DialogInterface dialog, int which) {
        needShowProgress();
        deleteValueInternal(requiredType, null, null, true, new Runnable() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda47
            @Override // java.lang.Runnable
            public final void run() {
                PassportActivity.this.needHideProgress();
            }
        }, new ErrorRunnable() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda70
            @Override // org.telegram.ui.PassportActivity.ErrorRunnable
            public final void onError(String str, String str2) {
                PassportActivity.this.m3990lambda$addField$62$orgtelegramuiPassportActivity(str, str2);
            }
        }, documentOnly);
    }

    /* renamed from: lambda$addField$62$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m3990lambda$addField$62$orgtelegramuiPassportActivity(String error, String text) {
        needHideProgress();
    }

    /* loaded from: classes4.dex */
    public static class EncryptionResult {
        byte[] decrypyedFileSecret;
        byte[] encryptedData;
        byte[] fileHash;
        byte[] fileSecret;
        SecureDocumentKey secureDocumentKey;

        public EncryptionResult(byte[] d, byte[] fs, byte[] dfs, byte[] fh, byte[] fk, byte[] fi) {
            this.encryptedData = d;
            this.fileSecret = fs;
            this.fileHash = fh;
            this.decrypyedFileSecret = dfs;
            this.secureDocumentKey = new SecureDocumentKey(fk, fi);
        }
    }

    private SecureDocumentKey getSecureDocumentKey(byte[] file_secret, byte[] file_hash) {
        byte[] decrypted_file_secret = decryptValueSecret(file_secret, file_hash);
        byte[] file_secret_hash = Utilities.computeSHA512(decrypted_file_secret, file_hash);
        byte[] file_key = new byte[32];
        System.arraycopy(file_secret_hash, 0, file_key, 0, 32);
        byte[] file_iv = new byte[16];
        System.arraycopy(file_secret_hash, 32, file_iv, 0, 16);
        return new SecureDocumentKey(file_key, file_iv);
    }

    public byte[] decryptSecret(byte[] secret, byte[] passwordHash) {
        if (secret == null || secret.length != 32) {
            return null;
        }
        byte[] key = new byte[32];
        System.arraycopy(passwordHash, 0, key, 0, 32);
        byte[] iv = new byte[16];
        System.arraycopy(passwordHash, 32, iv, 0, 16);
        byte[] decryptedSecret = new byte[32];
        System.arraycopy(secret, 0, decryptedSecret, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(decryptedSecret, key, iv, 0, decryptedSecret.length, 0, 0);
        return decryptedSecret;
    }

    private byte[] decryptValueSecret(byte[] encryptedSecureValueSecret, byte[] hash) {
        if (encryptedSecureValueSecret == null || encryptedSecureValueSecret.length != 32 || hash == null || hash.length != 32) {
            return null;
        }
        byte[] key = new byte[32];
        System.arraycopy(this.saltedPassword, 0, key, 0, 32);
        byte[] iv = new byte[16];
        System.arraycopy(this.saltedPassword, 32, iv, 0, 16);
        byte[] decryptedSecret = new byte[32];
        System.arraycopy(this.secureSecret, 0, decryptedSecret, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(decryptedSecret, key, iv, 0, decryptedSecret.length, 0, 0);
        if (!checkSecret(decryptedSecret, null)) {
            return null;
        }
        byte[] secret_hash = Utilities.computeSHA512(decryptedSecret, hash);
        byte[] file_secret_key = new byte[32];
        System.arraycopy(secret_hash, 0, file_secret_key, 0, 32);
        byte[] file_secret_iv = new byte[16];
        System.arraycopy(secret_hash, 32, file_secret_iv, 0, 16);
        byte[] result = new byte[32];
        System.arraycopy(encryptedSecureValueSecret, 0, result, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(result, file_secret_key, file_secret_iv, 0, result.length, 0, 0);
        return result;
    }

    public EncryptionResult createSecureDocument(String path) {
        File file = new File(path);
        int length = (int) file.length();
        byte[] b = new byte[length];
        RandomAccessFile f = null;
        try {
            f = new RandomAccessFile(path, "rws");
            f.readFully(b);
        } catch (Exception e) {
        }
        EncryptionResult result = encryptData(b);
        try {
            f.seek(0L);
            f.write(result.encryptedData);
            f.close();
        } catch (Exception e2) {
        }
        return result;
    }

    private String decryptData(byte[] data, byte[] file_secret, byte[] file_hash) {
        if (data == null || file_secret == null || file_secret.length != 32 || file_hash == null || file_hash.length != 32) {
            return null;
        }
        byte[] file_secret_hash = Utilities.computeSHA512(file_secret, file_hash);
        byte[] file_key = new byte[32];
        System.arraycopy(file_secret_hash, 0, file_key, 0, 32);
        byte[] file_iv = new byte[16];
        System.arraycopy(file_secret_hash, 32, file_iv, 0, 16);
        byte[] decryptedData = new byte[data.length];
        System.arraycopy(data, 0, decryptedData, 0, data.length);
        Utilities.aesCbcEncryptionByteArraySafe(decryptedData, file_key, file_iv, 0, decryptedData.length, 0, 0);
        byte[] hash = Utilities.computeSHA256(decryptedData);
        if (!Arrays.equals(hash, file_hash)) {
            return null;
        }
        int dataOffset = decryptedData[0] & 255;
        return new String(decryptedData, dataOffset, decryptedData.length - dataOffset);
    }

    public static boolean checkSecret(byte[] secret, Long id) {
        if (secret == null || secret.length != 32) {
            return false;
        }
        int sum = 0;
        for (byte b : secret) {
            sum += b & 255;
        }
        if (sum % 255 != 239) {
            return false;
        }
        if (id != null && Utilities.bytesToLong(Utilities.computeSHA256(secret)) != id.longValue()) {
            return false;
        }
        return true;
    }

    public byte[] getRandomSecret() {
        byte[] secret = new byte[32];
        Utilities.random.nextBytes(secret);
        int sum = 0;
        for (byte b : secret) {
            sum += b & 255;
        }
        int sum2 = sum % 255;
        if (sum2 != 239) {
            int a = Utilities.random.nextInt(32);
            int val = (secret[a] & 255) + (239 - sum2);
            if (val < 255) {
                val += 255;
            }
            secret[a] = (byte) (val % 255);
        }
        return secret;
    }

    public EncryptionResult encryptData(byte[] data) {
        byte[] file_secret = getRandomSecret();
        int extraLen = Utilities.random.nextInt(208) + 32;
        while ((data.length + extraLen) % 16 != 0) {
            extraLen++;
        }
        byte[] padding = new byte[extraLen];
        Utilities.random.nextBytes(padding);
        padding[0] = (byte) extraLen;
        byte[] paddedData = new byte[data.length + extraLen];
        System.arraycopy(padding, 0, paddedData, 0, extraLen);
        System.arraycopy(data, 0, paddedData, extraLen, data.length);
        byte[] file_hash = Utilities.computeSHA256(paddedData);
        byte[] file_secret_hash = Utilities.computeSHA512(file_secret, file_hash);
        byte[] file_key = new byte[32];
        System.arraycopy(file_secret_hash, 0, file_key, 0, 32);
        byte[] file_iv = new byte[16];
        System.arraycopy(file_secret_hash, 32, file_iv, 0, 16);
        Utilities.aesCbcEncryptionByteArraySafe(paddedData, file_key, file_iv, 0, paddedData.length, 0, 1);
        byte[] key = new byte[32];
        System.arraycopy(this.saltedPassword, 0, key, 0, 32);
        byte[] iv = new byte[16];
        System.arraycopy(this.saltedPassword, 32, iv, 0, 16);
        byte[] decryptedSecret = new byte[32];
        System.arraycopy(this.secureSecret, 0, decryptedSecret, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(decryptedSecret, key, iv, 0, decryptedSecret.length, 0, 0);
        byte[] secret_hash = Utilities.computeSHA512(decryptedSecret, file_hash);
        byte[] file_secret_key = new byte[32];
        System.arraycopy(secret_hash, 0, file_secret_key, 0, 32);
        byte[] file_secret_iv = new byte[16];
        System.arraycopy(secret_hash, 32, file_secret_iv, 0, 16);
        byte[] encrypyed_file_secret = new byte[32];
        System.arraycopy(file_secret, 0, encrypyed_file_secret, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(encrypyed_file_secret, file_secret_key, file_secret_iv, 0, encrypyed_file_secret.length, 0, 1);
        return new EncryptionResult(paddedData, encrypyed_file_secret, file_secret, file_hash, file_key, file_iv);
    }

    public void showAlertWithText(String title, String text) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        builder.setTitle(title);
        builder.setMessage(text);
        showDialog(builder.create());
    }

    public void onPasscodeError(boolean clear) {
        if (getParentActivity() == null) {
            return;
        }
        Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
        if (v != null) {
            v.vibrate(200L);
        }
        if (clear) {
            this.inputFields[0].setText("");
        }
        AndroidUtilities.shakeView(this.inputFields[0], 2.0f, 0);
    }

    public void startPhoneVerification(boolean checkPermissions, final String phone, Runnable finishRunnable, ErrorRunnable errorRunnable, final PassportActivityDelegate delegate) {
        TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
        boolean z = true;
        boolean simcardAvailable = (tm.getSimState() == 1 || tm.getPhoneType() == 0) ? false : true;
        boolean allowCall = true;
        if (getParentActivity() != null && Build.VERSION.SDK_INT >= 23 && simcardAvailable) {
            allowCall = getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
            if (checkPermissions) {
                this.permissionsItems.clear();
                if (!allowCall) {
                    this.permissionsItems.add("android.permission.READ_PHONE_STATE");
                }
                if (!this.permissionsItems.isEmpty()) {
                    if (getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                        builder.setMessage(LocaleController.getString("AllowReadCall", R.string.AllowReadCall));
                        this.permissionsDialog = showDialog(builder.create());
                    } else {
                        getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[0]), 6);
                    }
                    this.pendingPhone = phone;
                    this.pendingErrorRunnable = errorRunnable;
                    this.pendingFinishRunnable = finishRunnable;
                    this.pendingDelegate = delegate;
                    return;
                }
            }
        }
        final TLRPC.TL_account_sendVerifyPhoneCode req = new TLRPC.TL_account_sendVerifyPhoneCode();
        req.phone_number = phone;
        req.settings = new TLRPC.TL_codeSettings();
        TLRPC.TL_codeSettings tL_codeSettings = req.settings;
        if (!simcardAvailable || !allowCall) {
            z = false;
        }
        tL_codeSettings.allow_flashcall = z;
        req.settings.allow_app_hash = ApplicationLoader.hasPlayServices;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        if (req.settings.allow_app_hash) {
            preferences.edit().putString("sms_hash", BuildVars.SMS_HASH).commit();
        } else {
            preferences.edit().remove("sms_hash").commit();
        }
        if (req.settings.allow_flashcall) {
            try {
                String number = tm.getLine1Number();
                if (!TextUtils.isEmpty(number)) {
                    req.settings.current_number = PhoneNumberUtils.compare(phone, number);
                    if (!req.settings.current_number) {
                        req.settings.allow_flashcall = false;
                    }
                } else {
                    req.settings.current_number = false;
                }
            } catch (Exception e) {
                req.settings.allow_flashcall = false;
                FileLog.e(e);
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda62
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PassportActivity.this.m4055xa760bf9b(phone, delegate, req, tLObject, tL_error);
            }
        }, 2);
    }

    /* renamed from: lambda$startPhoneVerification$66$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4055xa760bf9b(final String phone, final PassportActivityDelegate delegate, final TLRPC.TL_account_sendVerifyPhoneCode req, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda53
            @Override // java.lang.Runnable
            public final void run() {
                PassportActivity.this.m4054x6a40fb7c(error, phone, delegate, response, req);
            }
        });
    }

    /* renamed from: lambda$startPhoneVerification$65$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4054x6a40fb7c(TLRPC.TL_error error, String phone, PassportActivityDelegate delegate, TLObject response, TLRPC.TL_account_sendVerifyPhoneCode req) {
        if (error != null) {
            AlertsCreator.processError(this.currentAccount, error, this, req, phone);
            return;
        }
        HashMap<String, String> values = new HashMap<>();
        values.put("phone", phone);
        PassportActivity activity = new PassportActivity(7, this.currentForm, this.currentPassword, this.currentType, (TLRPC.TL_secureValue) null, (TLRPC.TL_secureRequiredType) null, (TLRPC.TL_secureValue) null, values, (HashMap<String, String>) null);
        activity.currentAccount = this.currentAccount;
        activity.saltedPassword = this.saltedPassword;
        activity.secureSecret = this.secureSecret;
        activity.delegate = delegate;
        activity.currentPhoneVerification = (TLRPC.TL_auth_sentCode) response;
        presentFragment(activity, true);
    }

    public void updatePasswordInterface() {
        ImageView imageView = this.noPasswordImageView;
        if (imageView == null) {
            return;
        }
        TLRPC.TL_account_password tL_account_password = this.currentPassword;
        if (tL_account_password == null || this.usingSavedPassword != 0) {
            imageView.setVisibility(8);
            this.noPasswordTextView.setVisibility(8);
            this.noPasswordSetTextView.setVisibility(8);
            this.passwordAvatarContainer.setVisibility(8);
            this.inputFieldContainers[0].setVisibility(8);
            this.doneItem.setVisibility(8);
            this.passwordForgotButton.setVisibility(8);
            this.passwordInfoRequestTextView.setVisibility(8);
            this.passwordRequestTextView.setVisibility(8);
            this.emptyView.setVisibility(0);
        } else if (!tL_account_password.has_password) {
            this.passwordRequestTextView.setVisibility(0);
            this.noPasswordImageView.setVisibility(0);
            this.noPasswordTextView.setVisibility(0);
            this.noPasswordSetTextView.setVisibility(0);
            this.passwordAvatarContainer.setVisibility(8);
            this.inputFieldContainers[0].setVisibility(8);
            this.doneItem.setVisibility(8);
            this.passwordForgotButton.setVisibility(8);
            this.passwordInfoRequestTextView.setVisibility(8);
            this.passwordRequestTextView.setLayoutParams(LayoutHelper.createLinear(-1, -2, 0.0f, 25.0f, 0.0f, 0.0f));
            this.emptyView.setVisibility(8);
        } else {
            this.passwordRequestTextView.setVisibility(0);
            this.noPasswordImageView.setVisibility(8);
            this.noPasswordTextView.setVisibility(8);
            this.noPasswordSetTextView.setVisibility(8);
            this.emptyView.setVisibility(8);
            this.passwordAvatarContainer.setVisibility(0);
            this.inputFieldContainers[0].setVisibility(0);
            this.doneItem.setVisibility(0);
            this.passwordForgotButton.setVisibility(0);
            this.passwordInfoRequestTextView.setVisibility(0);
            this.passwordRequestTextView.setLayoutParams(LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 0.0f));
            if (this.inputFields != null) {
                TLRPC.TL_account_password tL_account_password2 = this.currentPassword;
                if (tL_account_password2 != null && !TextUtils.isEmpty(tL_account_password2.hint)) {
                    this.inputFields[0].setHint(this.currentPassword.hint);
                } else {
                    this.inputFields[0].setHint(LocaleController.getString("LoginPassword", R.string.LoginPassword));
                }
            }
        }
    }

    public void showEditDoneProgress(boolean animateDoneItem, final boolean show) {
        AnimatorSet animatorSet = this.doneItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        if (animateDoneItem && this.doneItem != null) {
            this.doneItemAnimation = new AnimatorSet();
            if (show) {
                this.progressView.setVisibility(0);
                this.doneItem.setEnabled(false);
                this.doneItemAnimation.playTogether(ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, 0.1f), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, 1.0f));
            } else {
                this.doneItem.getContentView().setVisibility(0);
                this.doneItem.setEnabled(true);
                this.doneItemAnimation.playTogether(ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, 0.1f), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, 1.0f));
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PassportActivity.21
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (PassportActivity.this.doneItemAnimation != null && PassportActivity.this.doneItemAnimation.equals(animation)) {
                        if (!show) {
                            PassportActivity.this.progressView.setVisibility(4);
                        } else {
                            PassportActivity.this.doneItem.getContentView().setVisibility(4);
                        }
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    if (PassportActivity.this.doneItemAnimation != null && PassportActivity.this.doneItemAnimation.equals(animation)) {
                        PassportActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150L);
            this.doneItemAnimation.start();
        } else if (this.acceptTextView != null) {
            this.doneItemAnimation = new AnimatorSet();
            if (show) {
                this.progressViewButton.setVisibility(0);
                this.bottomLayout.setEnabled(false);
                this.doneItemAnimation.playTogether(ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_Y, 0.1f), ObjectAnimator.ofFloat(this.acceptTextView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.progressViewButton, View.ALPHA, 1.0f));
            } else {
                this.acceptTextView.setVisibility(0);
                this.bottomLayout.setEnabled(true);
                this.doneItemAnimation.playTogether(ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_Y, 0.1f), ObjectAnimator.ofFloat(this.progressViewButton, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.acceptTextView, View.ALPHA, 1.0f));
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PassportActivity.22
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (PassportActivity.this.doneItemAnimation != null && PassportActivity.this.doneItemAnimation.equals(animation)) {
                        if (!show) {
                            PassportActivity.this.progressViewButton.setVisibility(4);
                        } else {
                            PassportActivity.this.acceptTextView.setVisibility(4);
                        }
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    if (PassportActivity.this.doneItemAnimation != null && PassportActivity.this.doneItemAnimation.equals(animation)) {
                        PassportActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150L);
            this.doneItemAnimation.start();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        SecureDocumentCell cell;
        ActionBarMenuItem actionBarMenuItem;
        if (id == NotificationCenter.fileUploaded) {
            String location = (String) args[0];
            SecureDocument document = this.uploadingDocuments.get(location);
            if (document != null) {
                document.inputFile = (TLRPC.TL_inputFile) args[1];
                this.uploadingDocuments.remove(location);
                if (this.uploadingDocuments.isEmpty() && (actionBarMenuItem = this.doneItem) != null) {
                    actionBarMenuItem.setEnabled(true);
                    this.doneItem.setAlpha(1.0f);
                }
                HashMap<SecureDocument, SecureDocumentCell> hashMap = this.documentsCells;
                if (hashMap != null && (cell = hashMap.get(document)) != null) {
                    cell.updateButtonState(true);
                }
                HashMap<String, String> hashMap2 = this.errorsValues;
                if (hashMap2 != null && hashMap2.containsKey("error_document_all")) {
                    this.errorsValues.remove("error_document_all");
                    checkTopErrorCell(false);
                }
                if (document.type == 0) {
                    if (this.bottomCell != null && !TextUtils.isEmpty(this.noAllDocumentsErrorText)) {
                        this.bottomCell.setText(this.noAllDocumentsErrorText);
                    }
                    this.errorsValues.remove("files_all");
                } else if (document.type == 4) {
                    if (this.bottomCellTranslation != null && !TextUtils.isEmpty(this.noAllTranslationErrorText)) {
                        this.bottomCellTranslation.setText(this.noAllTranslationErrorText);
                    }
                    this.errorsValues.remove("translation_all");
                }
            }
        } else if (id != NotificationCenter.fileUploadFailed) {
            if (id == NotificationCenter.twoStepPasswordChanged) {
                if (args != null && args.length > 0) {
                    if (args[7] != null) {
                        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                        if (editTextBoldCursorArr[0] != null) {
                            editTextBoldCursorArr[0].setText((String) args[7]);
                        }
                    }
                    if (args[6] == null) {
                        TLRPC.TL_account_password tL_account_password = new TLRPC.TL_account_password();
                        this.currentPassword = tL_account_password;
                        tL_account_password.current_algo = (TLRPC.PasswordKdfAlgo) args[1];
                        this.currentPassword.new_secure_algo = (TLRPC.SecurePasswordKdfAlgo) args[2];
                        this.currentPassword.secure_random = (byte[]) args[3];
                        this.currentPassword.has_recovery = !TextUtils.isEmpty((String) args[4]);
                        this.currentPassword.hint = (String) args[5];
                        this.currentPassword.srp_id = -1L;
                        this.currentPassword.srp_B = new byte[256];
                        Utilities.random.nextBytes(this.currentPassword.srp_B);
                        EditTextBoldCursor[] editTextBoldCursorArr2 = this.inputFields;
                        if (editTextBoldCursorArr2[0] != null && editTextBoldCursorArr2[0].length() > 0) {
                            this.usingSavedPassword = 2;
                        }
                    }
                } else {
                    this.currentPassword = null;
                    loadPasswordInfo();
                }
                updatePasswordInterface();
                return;
            }
            int i = NotificationCenter.didRemoveTwoStepPassword;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (this.presentAfterAnimation != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda46
                @Override // java.lang.Runnable
                public final void run() {
                    PassportActivity.this.m4049xf3c0417e();
                }
            });
        }
        int i = this.currentActivityType;
        if (i == 5) {
            if (isOpen) {
                if (this.inputFieldContainers[0].getVisibility() == 0) {
                    this.inputFields[0].requestFocus();
                    AndroidUtilities.showKeyboard(this.inputFields[0]);
                }
                if (this.usingSavedPassword == 2) {
                    onPasswordDone(false);
                }
            }
        } else if (i == 7) {
            if (isOpen) {
                this.views[this.currentViewNum].onShow();
            }
        } else if (i == 4) {
            if (isOpen) {
                this.inputFields[0].requestFocus();
                AndroidUtilities.showKeyboard(this.inputFields[0]);
            }
        } else if (i == 6) {
            if (isOpen) {
                this.inputFields[0].requestFocus();
                AndroidUtilities.showKeyboard(this.inputFields[0]);
            }
        } else if ((i == 2 || i == 1) && Build.VERSION.SDK_INT >= 21) {
            createChatAttachView();
        }
    }

    /* renamed from: lambda$onTransitionAnimationEnd$67$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4049xf3c0417e() {
        presentFragment(this.presentAfterAnimation, true);
        this.presentAfterAnimation = null;
    }

    private void showAttachmentError() {
        if (getParentActivity() == null) {
            return;
        }
        Toast toast = Toast.makeText(getParentActivity(), LocaleController.getString("UnsupportedAttachment", R.string.UnsupportedAttachment), 0);
        toast.show();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            if (requestCode == 0 || requestCode == 2) {
                createChatAttachView();
                ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
                if (chatAttachAlert != null) {
                    chatAttachAlert.onActivityResultFragment(requestCode, data, this.currentPicturePath);
                }
                this.currentPicturePath = null;
            } else if (requestCode == 1) {
                if (data == null || data.getData() == null) {
                    showAttachmentError();
                    return;
                }
                ArrayList<SendMessagesHelper.SendingMediaInfo> photos = new ArrayList<>();
                SendMessagesHelper.SendingMediaInfo info = new SendMessagesHelper.SendingMediaInfo();
                info.uri = data.getData();
                photos.add(info);
                processSelectedFiles(photos);
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        ChatAttachAlert chatAttachAlert;
        TextSettingsCell textSettingsCell;
        int i = this.currentActivityType;
        if ((i == 1 || i == 2) && (chatAttachAlert = this.chatAttachAlert) != null) {
            if (requestCode == 17) {
                chatAttachAlert.getPhotoLayout().checkCamera(false);
            } else if (requestCode == 21) {
                if (getParentActivity() != null && grantResults != null && grantResults.length != 0 && grantResults[0] != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setMessage(LocaleController.getString("PermissionNoAudioVideoWithHint", R.string.PermissionNoAudioVideoWithHint));
                    builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda44
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i2) {
                            PassportActivity.this.m4047xe1351e53(dialogInterface, i2);
                        }
                    });
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                    builder.show();
                }
            } else if (requestCode == 19 && grantResults != null && grantResults.length > 0 && grantResults[0] == 0) {
                processSelectedAttach(0);
            } else if (requestCode == 22 && grantResults != null && grantResults.length > 0 && grantResults[0] == 0 && (textSettingsCell = this.scanDocumentCell) != null) {
                textSettingsCell.callOnClick();
            }
        } else if (i == 3 && requestCode == 6) {
            startPhoneVerification(false, this.pendingPhone, this.pendingFinishRunnable, this.pendingErrorRunnable, this.pendingDelegate);
        }
    }

    /* renamed from: lambda$onRequestPermissionsResultFragment$68$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4047xe1351e53(DialogInterface dialog, int which) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            getParentActivity().startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void saveSelfArgs(Bundle args) {
        String str = this.currentPicturePath;
        if (str != null) {
            args.putString("path", str);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void restoreSelfArgs(Bundle args) {
        this.currentPicturePath = args.getString("path");
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        int i = this.currentActivityType;
        if (i == 7) {
            this.views[this.currentViewNum].onBackPressed(true);
            int a = 0;
            while (true) {
                SlideView[] slideViewArr = this.views;
                if (a >= slideViewArr.length) {
                    break;
                }
                if (slideViewArr[a] != null) {
                    slideViewArr[a].onDestroyActivity();
                }
                a++;
            }
        } else if (i == 0 || i == 5) {
            callCallback(false);
        } else if (i == 1 || i == 2) {
            return !checkDiscard();
        }
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onDialogDismiss(Dialog dialog) {
        if (this.currentActivityType == 3 && Build.VERSION.SDK_INT >= 23 && dialog == this.permissionsDialog && !this.permissionsItems.isEmpty()) {
            getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[0]), 6);
        }
    }

    public void needShowProgress() {
        if (getParentActivity() == null || getParentActivity().isFinishing() || this.progressDialog != null) {
            return;
        }
        AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        this.progressDialog = alertDialog;
        alertDialog.setCanCancel(false);
        this.progressDialog.show();
    }

    public void needHideProgress() {
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog == null) {
            return;
        }
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.progressDialog = null;
    }

    public void setPage(int page, boolean animated, Bundle params) {
        if (page == 3) {
            this.doneItem.setVisibility(8);
        }
        SlideView[] slideViewArr = this.views;
        final SlideView outView = slideViewArr[this.currentViewNum];
        final SlideView newView = slideViewArr[page];
        this.currentViewNum = page;
        newView.setParams(params, false);
        newView.onShow();
        if (animated) {
            newView.setTranslationX(AndroidUtilities.displaySize.x);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.setDuration(300L);
            animatorSet.playTogether(ObjectAnimator.ofFloat(outView, "translationX", -AndroidUtilities.displaySize.x), ObjectAnimator.ofFloat(newView, "translationX", 0.0f));
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PassportActivity.23
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    newView.setVisibility(0);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    outView.setVisibility(8);
                    outView.setX(0.0f);
                }
            });
            animatorSet.start();
            return;
        }
        newView.setTranslationX(0.0f);
        newView.setVisibility(0);
        if (outView != newView) {
            outView.setVisibility(8);
        }
    }

    public void fillNextCodeParams(Bundle params, TLRPC.TL_auth_sentCode res, boolean animated) {
        params.putString("phoneHash", res.phone_code_hash);
        if (res.next_type instanceof TLRPC.TL_auth_codeTypeCall) {
            params.putInt("nextType", 4);
        } else if (res.next_type instanceof TLRPC.TL_auth_codeTypeFlashCall) {
            params.putInt("nextType", 3);
        } else if (res.next_type instanceof TLRPC.TL_auth_codeTypeSms) {
            params.putInt("nextType", 2);
        }
        if (res.timeout == 0) {
            res.timeout = 60;
        }
        params.putInt("timeout", res.timeout * 1000);
        if (res.type instanceof TLRPC.TL_auth_sentCodeTypeCall) {
            params.putInt(CommonProperties.TYPE, 4);
            params.putInt("length", res.type.length);
            setPage(2, animated, params);
        } else if (res.type instanceof TLRPC.TL_auth_sentCodeTypeFlashCall) {
            params.putInt(CommonProperties.TYPE, 3);
            params.putString("pattern", res.type.pattern);
            setPage(1, animated, params);
        } else if (res.type instanceof TLRPC.TL_auth_sentCodeTypeSms) {
            params.putInt(CommonProperties.TYPE, 2);
            params.putInt("length", res.type.length);
            setPage(0, animated, params);
        }
    }

    private void openAttachMenu() {
        if (getParentActivity() == null) {
            return;
        }
        boolean z = true;
        if (this.uploadingFileType == 0 && this.documents.size() >= 20) {
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("PassportUploadMaxReached", R.string.PassportUploadMaxReached, LocaleController.formatPluralString("Files", 20, new Object[0])));
            return;
        }
        createChatAttachView();
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (this.uploadingFileType != 1) {
            z = false;
        }
        chatAttachAlert.setOpenWithFrontFaceCamera(z);
        this.chatAttachAlert.setMaxSelectedPhotos(getMaxSelectedDocuments(), false);
        this.chatAttachAlert.getPhotoLayout().loadGalleryPhotos();
        if (Build.VERSION.SDK_INT == 21 || Build.VERSION.SDK_INT == 22) {
            AndroidUtilities.hideKeyboard(this.fragmentView.findFocus());
        }
        this.chatAttachAlert.init();
        showDialog(this.chatAttachAlert);
    }

    private void createChatAttachView() {
        if (getParentActivity() != null && this.chatAttachAlert == null) {
            ChatAttachAlert chatAttachAlert = new ChatAttachAlert(getParentActivity(), this, false, false);
            this.chatAttachAlert = chatAttachAlert;
            chatAttachAlert.setDelegate(new ChatAttachAlert.ChatAttachViewDelegate() { // from class: org.telegram.ui.PassportActivity.24
                @Override // org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate
                public /* synthetic */ void didSelectBot(TLRPC.User user) {
                    ChatAttachAlert.ChatAttachViewDelegate.CC.$default$didSelectBot(this, user);
                }

                @Override // org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate
                public /* synthetic */ void doOnIdle(Runnable runnable) {
                    runnable.run();
                }

                @Override // org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate
                public /* synthetic */ View getRevealView() {
                    return ChatAttachAlert.ChatAttachViewDelegate.CC.$default$getRevealView(this);
                }

                @Override // org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate
                public /* synthetic */ boolean needEnterComment() {
                    return ChatAttachAlert.ChatAttachViewDelegate.CC.$default$needEnterComment(this);
                }

                @Override // org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate
                public /* synthetic */ void openAvatarsSearch() {
                    ChatAttachAlert.ChatAttachViewDelegate.CC.$default$openAvatarsSearch(this);
                }

                @Override // org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate
                public void didPressedButton(int button, boolean arg, boolean notify, int scheduleDate, boolean forceDocument) {
                    if (PassportActivity.this.getParentActivity() == null || PassportActivity.this.chatAttachAlert == null) {
                        return;
                    }
                    if (button != 8 && button != 7) {
                        if (PassportActivity.this.chatAttachAlert != null) {
                            PassportActivity.this.chatAttachAlert.dismissWithButtonClick(button);
                        }
                        PassportActivity.this.processSelectedAttach(button);
                        return;
                    }
                    if (button != 8) {
                        PassportActivity.this.chatAttachAlert.dismiss(true);
                    }
                    HashMap<Object, Object> selectedPhotos = PassportActivity.this.chatAttachAlert.getPhotoLayout().getSelectedPhotos();
                    ArrayList<Object> selectedPhotosOrder = PassportActivity.this.chatAttachAlert.getPhotoLayout().getSelectedPhotosOrder();
                    if (!selectedPhotos.isEmpty()) {
                        ArrayList<SendMessagesHelper.SendingMediaInfo> photos = new ArrayList<>();
                        for (int a = 0; a < selectedPhotosOrder.size(); a++) {
                            MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) selectedPhotos.get(selectedPhotosOrder.get(a));
                            SendMessagesHelper.SendingMediaInfo info = new SendMessagesHelper.SendingMediaInfo();
                            if (photoEntry.imagePath != null) {
                                info.path = photoEntry.imagePath;
                            } else {
                                info.path = photoEntry.path;
                            }
                            photos.add(info);
                            photoEntry.reset();
                        }
                        PassportActivity.this.processSelectedFiles(photos);
                    }
                }

                @Override // org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate
                public void onCameraOpened() {
                    AndroidUtilities.hideKeyboard(PassportActivity.this.fragmentView.findFocus());
                }
            });
        }
    }

    private int getMaxSelectedDocuments() {
        int i = this.uploadingFileType;
        if (i == 0) {
            return 20 - this.documents.size();
        }
        if (i == 4) {
            return 20 - this.translationDocuments.size();
        }
        return 1;
    }

    public void processSelectedAttach(int which) {
        if (which == 0) {
            if (Build.VERSION.SDK_INT >= 23 && getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0) {
                getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 19);
                return;
            }
            try {
                Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                File image = AndroidUtilities.generatePicturePath();
                if (image != null) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        takePictureIntent.putExtra("output", FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.beta.provider", image));
                        takePictureIntent.addFlags(2);
                        takePictureIntent.addFlags(1);
                    } else {
                        takePictureIntent.putExtra("output", Uri.fromFile(image));
                    }
                    this.currentPicturePath = image.getAbsolutePath();
                }
                startActivityForResult(takePictureIntent, 0);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> photos, boolean notify, int scheduleDate) {
        processSelectedFiles(photos);
    }

    public void startDocumentSelectActivity() {
        try {
            Intent photoPickerIntent = new Intent("android.intent.action.GET_CONTENT");
            if (Build.VERSION.SDK_INT >= 18) {
                photoPickerIntent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
            }
            photoPickerIntent.setType("*/*");
            startActivityForResult(photoPickerIntent, 21);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void didSelectFiles(ArrayList<String> files, String caption, boolean notify, int scheduleDate) {
        ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList = new ArrayList<>();
        int count = files.size();
        for (int a = 0; a < count; a++) {
            SendMessagesHelper.SendingMediaInfo info = new SendMessagesHelper.SendingMediaInfo();
            info.path = files.get(a);
            arrayList.add(info);
        }
        processSelectedFiles(arrayList);
    }

    private void fillInitialValues() {
        if (this.initialValues != null) {
            return;
        }
        this.initialValues = getCurrentValues();
    }

    private String getCurrentValues() {
        StringBuilder values = new StringBuilder();
        int a = 0;
        while (true) {
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            if (a >= editTextBoldCursorArr.length) {
                break;
            }
            values.append((CharSequence) editTextBoldCursorArr[a].getText());
            values.append(",");
            a++;
        }
        if (this.inputExtraFields != null) {
            int a2 = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr2 = this.inputExtraFields;
                if (a2 >= editTextBoldCursorArr2.length) {
                    break;
                }
                values.append((CharSequence) editTextBoldCursorArr2[a2].getText());
                values.append(",");
                a2++;
            }
        }
        int count = this.documents.size();
        for (int a3 = 0; a3 < count; a3++) {
            values.append(this.documents.get(a3).secureFile.id);
        }
        SecureDocument secureDocument = this.frontDocument;
        if (secureDocument != null) {
            values.append(secureDocument.secureFile.id);
        }
        SecureDocument secureDocument2 = this.reverseDocument;
        if (secureDocument2 != null) {
            values.append(secureDocument2.secureFile.id);
        }
        SecureDocument secureDocument3 = this.selfieDocument;
        if (secureDocument3 != null) {
            values.append(secureDocument3.secureFile.id);
        }
        int count2 = this.translationDocuments.size();
        for (int a4 = 0; a4 < count2; a4++) {
            values.append(this.translationDocuments.get(a4).secureFile.id);
        }
        return values.toString();
    }

    public boolean isHasNotAnyChanges() {
        String str = this.initialValues;
        return str == null || str.equals(getCurrentValues());
    }

    public boolean checkDiscard() {
        if (isHasNotAnyChanges()) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                PassportActivity.this.m3993lambda$checkDiscard$69$orgtelegramuiPassportActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setTitle(LocaleController.getString("DiscardChanges", R.string.DiscardChanges));
        builder.setMessage(LocaleController.getString("PassportDiscardChanges", R.string.PassportDiscardChanges));
        showDialog(builder.create());
        return true;
    }

    /* renamed from: lambda$checkDiscard$69$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m3993lambda$checkDiscard$69$orgtelegramuiPassportActivity(DialogInterface dialog, int which) {
        finishFragment();
    }

    public void processSelectedFiles(final ArrayList<SendMessagesHelper.SendingMediaInfo> photos) {
        final boolean allFieldsAreEmpty;
        if (photos.isEmpty()) {
            return;
        }
        int i = this.uploadingFileType;
        if (i == 1 || i == 4) {
            allFieldsAreEmpty = false;
        } else if (this.currentType.type instanceof TLRPC.TL_secureValueTypePersonalDetails) {
            allFieldsAreEmpty = true;
            int a = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                if (a < editTextBoldCursorArr.length) {
                    if (a == 5 || a == 8 || a == 4 || a == 6 || editTextBoldCursorArr[a].length() <= 0) {
                        a++;
                    } else {
                        allFieldsAreEmpty = false;
                        break;
                    }
                } else {
                    break;
                }
            }
        } else {
            allFieldsAreEmpty = false;
        }
        final int type = this.uploadingFileType;
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda48
            @Override // java.lang.Runnable
            public final void run() {
                PassportActivity.this.m4053lambda$processSelectedFiles$72$orgtelegramuiPassportActivity(photos, type, allFieldsAreEmpty);
            }
        });
    }

    /* renamed from: lambda$processSelectedFiles$72$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4053lambda$processSelectedFiles$72$orgtelegramuiPassportActivity(ArrayList photos, final int type, boolean needRecoginze) {
        TLRPC.PhotoSize size;
        int i = this.uploadingFileType;
        int count = Math.min((i == 0 || i == 4) ? 20 : 1, photos.size());
        boolean didRecognizeSuccessfully = false;
        for (int a = 0; a < count; a++) {
            SendMessagesHelper.SendingMediaInfo info = (SendMessagesHelper.SendingMediaInfo) photos.get(a);
            Bitmap bitmap = ImageLoader.loadBitmap(info.path, info.uri, 2048.0f, 2048.0f, false);
            if (bitmap != null && (size = ImageLoader.scaleAndSaveImage(bitmap, 2048.0f, 2048.0f, 89, false, (int) GroupCallActivity.TABLET_LIST_SIZE, (int) GroupCallActivity.TABLET_LIST_SIZE)) != null) {
                TLRPC.TL_secureFile secureFile = new TLRPC.TL_secureFile();
                secureFile.dc_id = (int) size.location.volume_id;
                secureFile.id = size.location.local_id;
                secureFile.date = (int) (System.currentTimeMillis() / 1000);
                final SecureDocument document = this.delegate.saveFile(secureFile);
                document.type = type;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda50
                    @Override // java.lang.Runnable
                    public final void run() {
                        PassportActivity.this.m4051lambda$processSelectedFiles$70$orgtelegramuiPassportActivity(document, type);
                    }
                });
                if (needRecoginze && !didRecognizeSuccessfully) {
                    try {
                        final MrzRecognizer.Result result = MrzRecognizer.recognize(bitmap, this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeDriverLicense);
                        if (result != null) {
                            didRecognizeSuccessfully = true;
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda49
                                @Override // java.lang.Runnable
                                public final void run() {
                                    PassportActivity.this.m4052lambda$processSelectedFiles$71$orgtelegramuiPassportActivity(result);
                                }
                            });
                        }
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
            }
        }
        SharedConfig.saveConfig();
    }

    /* renamed from: lambda$processSelectedFiles$70$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4051lambda$processSelectedFiles$70$orgtelegramuiPassportActivity(SecureDocument document, int type) {
        int i = this.uploadingFileType;
        if (i == 1) {
            SecureDocument secureDocument = this.selfieDocument;
            if (secureDocument != null) {
                SecureDocumentCell cell = this.documentsCells.remove(secureDocument);
                if (cell != null) {
                    this.selfieLayout.removeView(cell);
                }
                this.selfieDocument = null;
            }
        } else if (i == 4) {
            if (this.translationDocuments.size() >= 20) {
                return;
            }
        } else if (i == 2) {
            SecureDocument secureDocument2 = this.frontDocument;
            if (secureDocument2 != null) {
                SecureDocumentCell cell2 = this.documentsCells.remove(secureDocument2);
                if (cell2 != null) {
                    this.frontLayout.removeView(cell2);
                }
                this.frontDocument = null;
            }
        } else if (i == 3) {
            SecureDocument secureDocument3 = this.reverseDocument;
            if (secureDocument3 != null) {
                SecureDocumentCell cell3 = this.documentsCells.remove(secureDocument3);
                if (cell3 != null) {
                    this.reverseLayout.removeView(cell3);
                }
                this.reverseDocument = null;
            }
        } else if (i == 0 && this.documents.size() >= 20) {
            return;
        }
        this.uploadingDocuments.put(document.path, document);
        this.doneItem.setEnabled(false);
        this.doneItem.setAlpha(0.5f);
        FileLoader.getInstance(this.currentAccount).uploadFile(document.path, false, true, 16777216);
        addDocumentView(document, type);
        updateUploadText(type);
    }

    /* renamed from: lambda$processSelectedFiles$71$org-telegram-ui-PassportActivity */
    public /* synthetic */ void m4052lambda$processSelectedFiles$71$orgtelegramuiPassportActivity(MrzRecognizer.Result result) {
        if (result.type == 2) {
            if (!(this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeIdentityCard)) {
                int a1 = 0;
                int count1 = this.availableDocumentTypes.size();
                while (true) {
                    if (a1 >= count1) {
                        break;
                    }
                    TLRPC.TL_secureRequiredType requiredType = this.availableDocumentTypes.get(a1);
                    if (!(requiredType.type instanceof TLRPC.TL_secureValueTypeIdentityCard)) {
                        a1++;
                    } else {
                        this.currentDocumentsType = requiredType;
                        updateInterfaceStringsForDocumentType();
                        break;
                    }
                }
            }
        } else if (result.type == 1) {
            if (!(this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypePassport)) {
                int a12 = 0;
                int count12 = this.availableDocumentTypes.size();
                while (true) {
                    if (a12 >= count12) {
                        break;
                    }
                    TLRPC.TL_secureRequiredType requiredType2 = this.availableDocumentTypes.get(a12);
                    if (!(requiredType2.type instanceof TLRPC.TL_secureValueTypePassport)) {
                        a12++;
                    } else {
                        this.currentDocumentsType = requiredType2;
                        updateInterfaceStringsForDocumentType();
                        break;
                    }
                }
            }
        } else if (result.type == 3) {
            if (!(this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeInternalPassport)) {
                int a13 = 0;
                int count13 = this.availableDocumentTypes.size();
                while (true) {
                    if (a13 >= count13) {
                        break;
                    }
                    TLRPC.TL_secureRequiredType requiredType3 = this.availableDocumentTypes.get(a13);
                    if (!(requiredType3.type instanceof TLRPC.TL_secureValueTypeInternalPassport)) {
                        a13++;
                    } else {
                        this.currentDocumentsType = requiredType3;
                        updateInterfaceStringsForDocumentType();
                        break;
                    }
                }
            }
        } else if (result.type == 4 && !(this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeDriverLicense)) {
            int a14 = 0;
            int count14 = this.availableDocumentTypes.size();
            while (true) {
                if (a14 >= count14) {
                    break;
                }
                TLRPC.TL_secureRequiredType requiredType4 = this.availableDocumentTypes.get(a14);
                if (!(requiredType4.type instanceof TLRPC.TL_secureValueTypeDriverLicense)) {
                    a14++;
                } else {
                    this.currentDocumentsType = requiredType4;
                    updateInterfaceStringsForDocumentType();
                    break;
                }
            }
        }
        if (!TextUtils.isEmpty(result.firstName)) {
            this.inputFields[0].setText(result.firstName);
        }
        if (!TextUtils.isEmpty(result.middleName)) {
            this.inputFields[1].setText(result.middleName);
        }
        if (!TextUtils.isEmpty(result.lastName)) {
            this.inputFields[2].setText(result.lastName);
        }
        if (!TextUtils.isEmpty(result.number)) {
            this.inputFields[7].setText(result.number);
        }
        if (result.gender != 0) {
            switch (result.gender) {
                case 1:
                    this.currentGender = "male";
                    this.inputFields[4].setText(LocaleController.getString("PassportMale", R.string.PassportMale));
                    break;
                case 2:
                    this.currentGender = "female";
                    this.inputFields[4].setText(LocaleController.getString("PassportFemale", R.string.PassportFemale));
                    break;
            }
        }
        if (!TextUtils.isEmpty(result.nationality)) {
            String str = result.nationality;
            this.currentCitizeship = str;
            String country = this.languageMap.get(str);
            if (country != null) {
                this.inputFields[5].setText(country);
            }
        }
        if (!TextUtils.isEmpty(result.issuingCountry)) {
            String str2 = result.issuingCountry;
            this.currentResidence = str2;
            String country2 = this.languageMap.get(str2);
            if (country2 != null) {
                this.inputFields[6].setText(country2);
            }
        }
        if (result.birthDay > 0 && result.birthMonth > 0 && result.birthYear > 0) {
            this.inputFields[3].setText(String.format(Locale.US, "%02d.%02d.%d", Integer.valueOf(result.birthDay), Integer.valueOf(result.birthMonth), Integer.valueOf(result.birthYear)));
        }
        if (result.expiryDay > 0 && result.expiryMonth > 0 && result.expiryYear > 0) {
            this.currentExpireDate[0] = result.expiryYear;
            this.currentExpireDate[1] = result.expiryMonth;
            this.currentExpireDate[2] = result.expiryDay;
            this.inputFields[8].setText(String.format(Locale.US, "%02d.%02d.%d", Integer.valueOf(result.expiryDay), Integer.valueOf(result.expiryMonth), Integer.valueOf(result.expiryYear)));
            return;
        }
        int[] iArr = this.currentExpireDate;
        iArr[2] = 0;
        iArr[1] = 0;
        iArr[0] = 0;
        this.inputFields[8].setText(LocaleController.getString("PassportNoExpireDate", R.string.PassportNoExpireDate));
    }

    public void setNeedActivityResult(boolean needActivityResult) {
        this.needActivityResult = needActivityResult;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class ProgressView extends View {
        private Paint paint = new Paint();
        private Paint paint2 = new Paint();
        private float progress;

        public ProgressView(Context context) {
            super(context);
            this.paint.setColor(Theme.getColor(Theme.key_login_progressInner));
            this.paint2.setColor(Theme.getColor(Theme.key_login_progressOuter));
        }

        public void setProgress(float value) {
            this.progress = value;
            invalidate();
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int start = (int) (getMeasuredWidth() * this.progress);
            canvas.drawRect(0.0f, 0.0f, start, getMeasuredHeight(), this.paint2);
            canvas.drawRect(start, 0.0f, getMeasuredWidth(), getMeasuredHeight(), this.paint);
        }
    }

    /* loaded from: classes4.dex */
    public class PhoneConfirmationView extends SlideView implements NotificationCenter.NotificationCenterDelegate {
        private ImageView blackImageView;
        private ImageView blueImageView;
        private EditTextBoldCursor[] codeField;
        private LinearLayout codeFieldContainer;
        private Timer codeTimer;
        private TextView confirmTextView;
        private Bundle currentParams;
        private boolean ignoreOnTextChange;
        private double lastCodeTime;
        private double lastCurrentTime;
        private int length;
        private boolean nextPressed;
        private int nextType;
        private String phone;
        private String phoneHash;
        private TextView problemText;
        private ProgressView progressView;
        private TextView timeText;
        private Timer timeTimer;
        private int timeout;
        private TextView titleTextView;
        private int verificationType;
        private boolean waitingForEvent;
        private final Object timerSync = new Object();
        private int time = 60000;
        private int codeTime = 15000;
        private String lastError = "";
        private String pattern = "*";

        static /* synthetic */ int access$10026(PhoneConfirmationView x0, double x1) {
            double d = x0.codeTime;
            Double.isNaN(d);
            int i = (int) (d - x1);
            x0.codeTime = i;
            return i;
        }

        static /* synthetic */ int access$10626(PhoneConfirmationView x0, double x1) {
            double d = x0.time;
            Double.isNaN(d);
            int i = (int) (d - x1);
            x0.time = i;
            return i;
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public PhoneConfirmationView(Context context, int type) {
            super(context);
            PassportActivity.this = this$0;
            this.verificationType = type;
            setOrientation(1);
            TextView textView = new TextView(context);
            this.confirmTextView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.confirmTextView.setTextSize(1, 14.0f);
            this.confirmTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            TextView textView2 = new TextView(context);
            this.titleTextView = textView2;
            textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.titleTextView.setTextSize(1, 18.0f);
            this.titleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.titleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.titleTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.titleTextView.setGravity(49);
            if (this.verificationType == 3) {
                this.confirmTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                FrameLayout frameLayout = new FrameLayout(context);
                addView(frameLayout, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.phone_activate);
                if (LocaleController.isRTL) {
                    frameLayout.addView(imageView, LayoutHelper.createFrame(64, 76.0f, 19, 2.0f, 2.0f, 0.0f, 0.0f));
                    frameLayout.addView(this.confirmTextView, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, 82.0f, 0.0f, 0.0f, 0.0f));
                } else {
                    frameLayout.addView(this.confirmTextView, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, 0.0f, 0.0f, 82.0f, 0.0f));
                    frameLayout.addView(imageView, LayoutHelper.createFrame(64, 76.0f, 21, 0.0f, 2.0f, 0.0f, 2.0f));
                }
            } else {
                this.confirmTextView.setGravity(49);
                FrameLayout frameLayout2 = new FrameLayout(context);
                addView(frameLayout2, LayoutHelper.createLinear(-2, -2, 49));
                if (this.verificationType == 1) {
                    ImageView imageView2 = new ImageView(context);
                    this.blackImageView = imageView2;
                    imageView2.setImageResource(R.drawable.sms_devices);
                    this.blackImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), PorterDuff.Mode.MULTIPLY));
                    frameLayout2.addView(this.blackImageView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    ImageView imageView3 = new ImageView(context);
                    this.blueImageView = imageView3;
                    imageView3.setImageResource(R.drawable.sms_bubble);
                    this.blueImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionBackground), PorterDuff.Mode.MULTIPLY));
                    frameLayout2.addView(this.blueImageView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    this.titleTextView.setText(LocaleController.getString("SentAppCodeTitle", R.string.SentAppCodeTitle));
                } else {
                    ImageView imageView4 = new ImageView(context);
                    this.blueImageView = imageView4;
                    imageView4.setImageResource(R.drawable.sms_code);
                    this.blueImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionBackground), PorterDuff.Mode.MULTIPLY));
                    frameLayout2.addView(this.blueImageView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    this.titleTextView.setText(LocaleController.getString("SentSmsCodeTitle", R.string.SentSmsCodeTitle));
                }
                addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 18, 0, 0));
                addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 17, 0, 0));
            }
            LinearLayout linearLayout = new LinearLayout(context);
            this.codeFieldContainer = linearLayout;
            linearLayout.setOrientation(0);
            addView(this.codeFieldContainer, LayoutHelper.createLinear(-2, 36, 1));
            if (this.verificationType == 3) {
                this.codeFieldContainer.setVisibility(8);
            }
            TextView textView3 = new TextView(context) { // from class: org.telegram.ui.PassportActivity.PhoneConfirmationView.1
                @Override // android.widget.TextView, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), Integer.MIN_VALUE));
                }
            };
            this.timeText = textView3;
            textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.timeText.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            if (this.verificationType == 3) {
                this.timeText.setTextSize(1, 14.0f);
                addView(this.timeText, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
                this.progressView = new ProgressView(context);
                this.timeText.setGravity(LocaleController.isRTL ? 5 : 3);
                addView(this.progressView, LayoutHelper.createLinear(-1, 3, 0.0f, 12.0f, 0.0f, 0.0f));
            } else {
                this.timeText.setPadding(0, AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(10.0f));
                this.timeText.setTextSize(1, 15.0f);
                this.timeText.setGravity(49);
                addView(this.timeText, LayoutHelper.createLinear(-2, -2, 49));
            }
            TextView textView4 = new TextView(context) { // from class: org.telegram.ui.PassportActivity.PhoneConfirmationView.2
                @Override // android.widget.TextView, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), Integer.MIN_VALUE));
                }
            };
            this.problemText = textView4;
            textView4.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            this.problemText.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.problemText.setPadding(0, AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(10.0f));
            this.problemText.setTextSize(1, 15.0f);
            this.problemText.setGravity(49);
            if (this.verificationType == 1) {
                this.problemText.setText(LocaleController.getString("DidNotGetTheCodeSms", R.string.DidNotGetTheCodeSms));
            } else {
                this.problemText.setText(LocaleController.getString("DidNotGetTheCode", R.string.DidNotGetTheCode));
            }
            addView(this.problemText, LayoutHelper.createLinear(-2, -2, 49));
            this.problemText.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PassportActivity.PhoneConfirmationView.this.m4084xa1370d30(view);
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-PassportActivity$PhoneConfirmationView */
        public /* synthetic */ void m4084xa1370d30(View v) {
            if (this.nextPressed) {
                return;
            }
            int i = this.nextType;
            boolean email = (i == 4 && this.verificationType == 2) || i == 0;
            if (!email) {
                resendCode();
                return;
            }
            try {
                PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                String version = String.format(Locale.US, "%s (%d)", pInfo.versionName, Integer.valueOf(pInfo.versionCode));
                Intent mailer = new Intent("android.intent.action.SENDTO");
                mailer.setData(Uri.parse(MailTo.MAILTO_SCHEME));
                mailer.putExtra("android.intent.extra.EMAIL", new String[]{"sms@telegram.org"});
                mailer.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + version + " " + this.phone);
                mailer.putExtra("android.intent.extra.TEXT", "Phone: " + this.phone + "\nApp version: " + version + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + this.lastError);
                getContext().startActivity(Intent.createChooser(mailer, "Send email..."));
            } catch (Exception e) {
                AlertsCreator.showSimpleAlert(PassportActivity.this, LocaleController.getString("NoMailInstalled", R.string.NoMailInstalled));
            }
        }

        @Override // android.widget.LinearLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            ImageView imageView;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (this.verificationType != 3 && (imageView = this.blueImageView) != null) {
                int innerHeight = imageView.getMeasuredHeight() + this.titleTextView.getMeasuredHeight() + this.confirmTextView.getMeasuredHeight() + AndroidUtilities.dp(35.0f);
                int requiredHeight = AndroidUtilities.dp(80.0f);
                int maxHeight = AndroidUtilities.dp(291.0f);
                if (PassportActivity.this.scrollHeight - innerHeight >= requiredHeight) {
                    setMeasuredDimension(getMeasuredWidth(), Math.min(PassportActivity.this.scrollHeight, maxHeight));
                } else {
                    setMeasuredDimension(getMeasuredWidth(), innerHeight + requiredHeight);
                }
            }
        }

        @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            int t2;
            super.onLayout(changed, l, t, r, b);
            if (this.verificationType != 3 && this.blueImageView != null) {
                int bottom = this.confirmTextView.getBottom();
                int height = getMeasuredHeight() - bottom;
                if (this.problemText.getVisibility() == 0) {
                    int h = this.problemText.getMeasuredHeight();
                    t2 = (bottom + height) - h;
                    TextView textView = this.problemText;
                    textView.layout(textView.getLeft(), t2, this.problemText.getRight(), t2 + h);
                } else if (this.timeText.getVisibility() == 0) {
                    int h2 = this.timeText.getMeasuredHeight();
                    t2 = (bottom + height) - h2;
                    TextView textView2 = this.timeText;
                    textView2.layout(textView2.getLeft(), t2, this.timeText.getRight(), t2 + h2);
                } else {
                    t2 = bottom + height;
                }
                int h3 = this.codeFieldContainer.getMeasuredHeight();
                int t3 = (((t2 - bottom) - h3) / 2) + bottom;
                LinearLayout linearLayout = this.codeFieldContainer;
                linearLayout.layout(linearLayout.getLeft(), t3, this.codeFieldContainer.getRight(), t3 + h3);
            }
        }

        public void resendCode() {
            final Bundle params = new Bundle();
            params.putString("phone", this.phone);
            this.nextPressed = true;
            PassportActivity.this.needShowProgress();
            final TLRPC.TL_auth_resendCode req = new TLRPC.TL_auth_resendCode();
            req.phone_number = this.phone;
            req.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda9
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PassportActivity.PhoneConfirmationView.this.m4090x4db20d8b(params, req, tLObject, tL_error);
                }
            }, 2);
        }

        /* renamed from: lambda$resendCode$3$org-telegram-ui-PassportActivity$PhoneConfirmationView */
        public /* synthetic */ void m4090x4db20d8b(final Bundle params, final TLRPC.TL_auth_resendCode req, final TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    PassportActivity.PhoneConfirmationView.this.m4089x4c7bbaac(error, params, response, req);
                }
            });
        }

        /* renamed from: lambda$resendCode$2$org-telegram-ui-PassportActivity$PhoneConfirmationView */
        public /* synthetic */ void m4089x4c7bbaac(TLRPC.TL_error error, Bundle params, TLObject response, TLRPC.TL_auth_resendCode req) {
            this.nextPressed = false;
            if (error == null) {
                PassportActivity.this.fillNextCodeParams(params, (TLRPC.TL_auth_sentCode) response, true);
            } else {
                AlertDialog dialog = (AlertDialog) AlertsCreator.processError(PassportActivity.this.currentAccount, error, PassportActivity.this, req, new Object[0]);
                if (dialog != null && error.text.contains("PHONE_CODE_EXPIRED")) {
                    dialog.setPositiveButtonListener(new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda2
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            PassportActivity.PhoneConfirmationView.this.m4088x4b4567cd(dialogInterface, i);
                        }
                    });
                }
            }
            PassportActivity.this.needHideProgress();
        }

        /* renamed from: lambda$resendCode$1$org-telegram-ui-PassportActivity$PhoneConfirmationView */
        public /* synthetic */ void m4088x4b4567cd(DialogInterface dialog1, int which) {
            onBackPressed(true);
            PassportActivity.this.finishFragment();
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean needBackButton() {
            return true;
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onCancelPressed() {
            this.nextPressed = false;
        }

        @Override // org.telegram.ui.Components.SlideView
        public void setParams(Bundle params, boolean restore) {
            int i;
            int i2;
            if (params != null) {
                this.waitingForEvent = true;
                int i3 = this.verificationType;
                if (i3 == 2) {
                    AndroidUtilities.setWaitingForSms(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (i3 == 3) {
                    AndroidUtilities.setWaitingForCall(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
                }
                this.currentParams = params;
                this.phone = params.getString("phone");
                this.phoneHash = params.getString("phoneHash");
                int i4 = params.getInt("timeout");
                this.time = i4;
                this.timeout = i4;
                this.nextType = params.getInt("nextType");
                this.pattern = params.getString("pattern");
                int i5 = params.getInt("length");
                this.length = i5;
                if (i5 == 0) {
                    this.length = 5;
                }
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                int i6 = 8;
                int i7 = 0;
                if (editTextBoldCursorArr == null || editTextBoldCursorArr.length != this.length) {
                    int a = this.length;
                    this.codeField = new EditTextBoldCursor[a];
                    int a2 = 0;
                    while (a2 < this.length) {
                        final int num = a2;
                        this.codeField[a2] = new EditTextBoldCursor(getContext());
                        this.codeField[a2].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                        this.codeField[a2].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                        this.codeField[a2].setCursorSize(AndroidUtilities.dp(20.0f));
                        this.codeField[a2].setCursorWidth(1.5f);
                        Drawable pressedDrawable = getResources().getDrawable(R.drawable.search_dark_activated).mutate();
                        pressedDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated), PorterDuff.Mode.MULTIPLY));
                        this.codeField[a2].setBackgroundDrawable(pressedDrawable);
                        this.codeField[a2].setImeOptions(268435461);
                        this.codeField[a2].setTextSize(1, 20.0f);
                        this.codeField[a2].setMaxLines(1);
                        this.codeField[a2].setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                        this.codeField[a2].setPadding(0, 0, 0, 0);
                        this.codeField[a2].setGravity(49);
                        if (this.verificationType == 3) {
                            this.codeField[a2].setEnabled(false);
                            this.codeField[a2].setInputType(0);
                            this.codeField[a2].setVisibility(i6);
                        } else {
                            this.codeField[a2].setInputType(3);
                        }
                        this.codeFieldContainer.addView(this.codeField[a2], LayoutHelper.createLinear(34, 36, 1, 0, 0, a2 != this.length - 1 ? 7 : 0, 0));
                        this.codeField[a2].addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.PassportActivity.PhoneConfirmationView.3
                            @Override // android.text.TextWatcher
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override // android.text.TextWatcher
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            @Override // android.text.TextWatcher
                            public void afterTextChanged(Editable s) {
                                int len;
                                if (!PhoneConfirmationView.this.ignoreOnTextChange && (len = s.length()) >= 1) {
                                    if (len > 1) {
                                        String text = s.toString();
                                        PhoneConfirmationView.this.ignoreOnTextChange = true;
                                        for (int a3 = 0; a3 < Math.min(PhoneConfirmationView.this.length - num, len); a3++) {
                                            if (a3 != 0) {
                                                PhoneConfirmationView.this.codeField[num + a3].setText(text.substring(a3, a3 + 1));
                                            } else {
                                                s.replace(0, len, text.substring(a3, a3 + 1));
                                            }
                                        }
                                        PhoneConfirmationView.this.ignoreOnTextChange = false;
                                    }
                                    if (num != PhoneConfirmationView.this.length - 1) {
                                        PhoneConfirmationView.this.codeField[num + 1].setSelection(PhoneConfirmationView.this.codeField[num + 1].length());
                                        PhoneConfirmationView.this.codeField[num + 1].requestFocus();
                                    }
                                    if ((num == PhoneConfirmationView.this.length - 1 || (num == PhoneConfirmationView.this.length - 2 && len >= 2)) && PhoneConfirmationView.this.getCode().length() == PhoneConfirmationView.this.length) {
                                        PhoneConfirmationView.this.onNextPressed(null);
                                    }
                                }
                            }
                        });
                        this.codeField[a2].setOnKeyListener(new View.OnKeyListener() { // from class: org.telegram.ui.PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda4
                            @Override // android.view.View.OnKeyListener
                            public final boolean onKey(View view, int i8, KeyEvent keyEvent) {
                                return PassportActivity.PhoneConfirmationView.this.m4091xf099e504(num, view, i8, keyEvent);
                            }
                        });
                        this.codeField[a2].setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda5
                            @Override // android.widget.TextView.OnEditorActionListener
                            public final boolean onEditorAction(TextView textView, int i8, KeyEvent keyEvent) {
                                return PassportActivity.PhoneConfirmationView.this.m4092xf1d037e3(textView, i8, keyEvent);
                            }
                        });
                        a2++;
                        i6 = 8;
                    }
                } else {
                    int a3 = 0;
                    while (true) {
                        EditTextBoldCursor[] editTextBoldCursorArr2 = this.codeField;
                        if (a3 >= editTextBoldCursorArr2.length) {
                            break;
                        }
                        editTextBoldCursorArr2[a3].setText("");
                        a3++;
                    }
                }
                ProgressView progressView = this.progressView;
                if (progressView != null) {
                    progressView.setVisibility(this.nextType != 0 ? 0 : 8);
                }
                if (this.phone == null) {
                    return;
                }
                PhoneFormat phoneFormat = PhoneFormat.getInstance();
                String number = phoneFormat.format("+" + this.phone);
                CharSequence str = "";
                int i8 = this.verificationType;
                if (i8 == 2) {
                    str = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", R.string.SentSmsCode, LocaleController.addNbsp(number)));
                } else if (i8 == 3) {
                    str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", R.string.SentCallCode, LocaleController.addNbsp(number)));
                } else if (i8 == 4) {
                    str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", R.string.SentCallOnly, LocaleController.addNbsp(number)));
                }
                this.confirmTextView.setText(str);
                if (this.verificationType != 3) {
                    AndroidUtilities.showKeyboard(this.codeField[0]);
                    this.codeField[0].requestFocus();
                } else {
                    AndroidUtilities.hideKeyboard(this.codeField[0]);
                }
                destroyTimer();
                destroyCodeTimer();
                this.lastCurrentTime = System.currentTimeMillis();
                int i9 = this.verificationType;
                if (i9 == 3 && ((i2 = this.nextType) == 4 || i2 == 2)) {
                    this.problemText.setVisibility(8);
                    this.timeText.setVisibility(0);
                    int i10 = this.nextType;
                    if (i10 == 4) {
                        this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, 1, 0));
                    } else if (i10 == 2) {
                        this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, 1, 0));
                    }
                    createTimer();
                } else if (i9 == 2 && ((i = this.nextType) == 4 || i == 3)) {
                    this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, 2, 0));
                    this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                    TextView textView = this.timeText;
                    if (this.time < 1000) {
                        i7 = 8;
                    }
                    textView.setVisibility(i7);
                    createTimer();
                } else if (i9 == 4 && this.nextType == 2) {
                    this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, 2, 0));
                    this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                    TextView textView2 = this.timeText;
                    if (this.time < 1000) {
                        i7 = 8;
                    }
                    textView2.setVisibility(i7);
                    createTimer();
                } else {
                    this.timeText.setVisibility(8);
                    this.problemText.setVisibility(8);
                    createCodeTimer();
                }
            }
        }

        /* renamed from: lambda$setParams$4$org-telegram-ui-PassportActivity$PhoneConfirmationView */
        public /* synthetic */ boolean m4091xf099e504(int num, View v, int keyCode, KeyEvent event) {
            if (keyCode == 67 && this.codeField[num].length() == 0 && num > 0) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                editTextBoldCursorArr[num - 1].setSelection(editTextBoldCursorArr[num - 1].length());
                this.codeField[num - 1].requestFocus();
                this.codeField[num - 1].dispatchKeyEvent(event);
                return true;
            }
            return false;
        }

        /* renamed from: lambda$setParams$5$org-telegram-ui-PassportActivity$PhoneConfirmationView */
        public /* synthetic */ boolean m4092xf1d037e3(TextView textView, int i, KeyEvent keyEvent) {
            if (i == 5) {
                onNextPressed(null);
                return true;
            }
            return false;
        }

        public void createCodeTimer() {
            if (this.codeTimer != null) {
                return;
            }
            this.codeTime = 15000;
            this.codeTimer = new Timer();
            this.lastCodeTime = System.currentTimeMillis();
            this.codeTimer.schedule(new AnonymousClass4(), 0L, 1000L);
        }

        /* renamed from: org.telegram.ui.PassportActivity$PhoneConfirmationView$4 */
        /* loaded from: classes4.dex */
        public class AnonymousClass4 extends TimerTask {
            AnonymousClass4() {
                PhoneConfirmationView.this = this$1;
            }

            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$PhoneConfirmationView$4$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PassportActivity.PhoneConfirmationView.AnonymousClass4.this.m4093xf1da25f5();
                    }
                });
            }

            /* renamed from: lambda$run$0$org-telegram-ui-PassportActivity$PhoneConfirmationView$4 */
            public /* synthetic */ void m4093xf1da25f5() {
                double currentTime = System.currentTimeMillis();
                double d = PhoneConfirmationView.this.lastCodeTime;
                Double.isNaN(currentTime);
                double diff = currentTime - d;
                PhoneConfirmationView.this.lastCodeTime = currentTime;
                PhoneConfirmationView.access$10026(PhoneConfirmationView.this, diff);
                if (PhoneConfirmationView.this.codeTime <= 1000) {
                    PhoneConfirmationView.this.problemText.setVisibility(0);
                    PhoneConfirmationView.this.timeText.setVisibility(8);
                    PhoneConfirmationView.this.destroyCodeTimer();
                }
            }
        }

        public void destroyCodeTimer() {
            try {
                synchronized (this.timerSync) {
                    Timer timer = this.codeTimer;
                    if (timer != null) {
                        timer.cancel();
                        this.codeTimer = null;
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        private void createTimer() {
            if (this.timeTimer != null) {
                return;
            }
            Timer timer = new Timer();
            this.timeTimer = timer;
            timer.schedule(new AnonymousClass5(), 0L, 1000L);
        }

        /* renamed from: org.telegram.ui.PassportActivity$PhoneConfirmationView$5 */
        /* loaded from: classes4.dex */
        public class AnonymousClass5 extends TimerTask {
            AnonymousClass5() {
                PhoneConfirmationView.this = this$1;
            }

            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                if (PhoneConfirmationView.this.timeTimer == null) {
                    return;
                }
                double currentTime = System.currentTimeMillis();
                double d = PhoneConfirmationView.this.lastCurrentTime;
                Double.isNaN(currentTime);
                double diff = currentTime - d;
                PhoneConfirmationView.access$10626(PhoneConfirmationView.this, diff);
                PhoneConfirmationView.this.lastCurrentTime = currentTime;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$PhoneConfirmationView$5$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PassportActivity.PhoneConfirmationView.AnonymousClass5.this.m4096xbb45434();
                    }
                });
            }

            /* renamed from: lambda$run$2$org-telegram-ui-PassportActivity$PhoneConfirmationView$5 */
            public /* synthetic */ void m4096xbb45434() {
                if (PhoneConfirmationView.this.time >= 1000) {
                    int minutes = (PhoneConfirmationView.this.time / 1000) / 60;
                    int seconds = (PhoneConfirmationView.this.time / 1000) - (minutes * 60);
                    if (PhoneConfirmationView.this.nextType == 4 || PhoneConfirmationView.this.nextType == 3) {
                        PhoneConfirmationView.this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                    } else if (PhoneConfirmationView.this.nextType == 2) {
                        PhoneConfirmationView.this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                    }
                    if (PhoneConfirmationView.this.progressView != null) {
                        PhoneConfirmationView.this.progressView.setProgress(1.0f - (PhoneConfirmationView.this.time / PhoneConfirmationView.this.timeout));
                        return;
                    }
                    return;
                }
                if (PhoneConfirmationView.this.progressView != null) {
                    PhoneConfirmationView.this.progressView.setProgress(1.0f);
                }
                PhoneConfirmationView.this.destroyTimer();
                if (PhoneConfirmationView.this.verificationType != 3) {
                    if (PhoneConfirmationView.this.verificationType == 2 || PhoneConfirmationView.this.verificationType == 4) {
                        if (PhoneConfirmationView.this.nextType == 4 || PhoneConfirmationView.this.nextType == 2) {
                            if (PhoneConfirmationView.this.nextType == 4) {
                                PhoneConfirmationView.this.timeText.setText(LocaleController.getString("Calling", R.string.Calling));
                            } else {
                                PhoneConfirmationView.this.timeText.setText(LocaleController.getString("SendingSms", R.string.SendingSms));
                            }
                            PhoneConfirmationView.this.createCodeTimer();
                            TLRPC.TL_auth_resendCode req = new TLRPC.TL_auth_resendCode();
                            req.phone_number = PhoneConfirmationView.this.phone;
                            req.phone_code_hash = PhoneConfirmationView.this.phoneHash;
                            ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PassportActivity$PhoneConfirmationView$5$$ExternalSyntheticLambda2
                                @Override // org.telegram.tgnet.RequestDelegate
                                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                    PassportActivity.PhoneConfirmationView.AnonymousClass5.this.m4095x7ec73d15(tLObject, tL_error);
                                }
                            }, 2);
                            return;
                        } else if (PhoneConfirmationView.this.nextType == 3) {
                            AndroidUtilities.setWaitingForSms(false);
                            NotificationCenter.getGlobalInstance().removeObserver(PhoneConfirmationView.this, NotificationCenter.didReceiveSmsCode);
                            PhoneConfirmationView.this.waitingForEvent = false;
                            PhoneConfirmationView.this.destroyCodeTimer();
                            PhoneConfirmationView.this.resendCode();
                            return;
                        } else {
                            return;
                        }
                    }
                    return;
                }
                AndroidUtilities.setWaitingForCall(false);
                NotificationCenter.getGlobalInstance().removeObserver(PhoneConfirmationView.this, NotificationCenter.didReceiveCall);
                PhoneConfirmationView.this.waitingForEvent = false;
                PhoneConfirmationView.this.destroyCodeTimer();
                PhoneConfirmationView.this.resendCode();
            }

            /* renamed from: lambda$run$1$org-telegram-ui-PassportActivity$PhoneConfirmationView$5 */
            public /* synthetic */ void m4095x7ec73d15(TLObject response, final TLRPC.TL_error error) {
                if (error != null && error.text != null) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$PhoneConfirmationView$5$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            PassportActivity.PhoneConfirmationView.AnonymousClass5.this.m4094xf1da25f6(error);
                        }
                    });
                }
            }

            /* renamed from: lambda$run$0$org-telegram-ui-PassportActivity$PhoneConfirmationView$5 */
            public /* synthetic */ void m4094xf1da25f6(TLRPC.TL_error error) {
                PhoneConfirmationView.this.lastError = error.text;
            }
        }

        public void destroyTimer() {
            try {
                synchronized (this.timerSync) {
                    Timer timer = this.timeTimer;
                    if (timer != null) {
                        timer.cancel();
                        this.timeTimer = null;
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public String getCode() {
            if (this.codeField == null) {
                return "";
            }
            StringBuilder codeBuilder = new StringBuilder();
            int a = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                if (a < editTextBoldCursorArr.length) {
                    codeBuilder.append(PhoneFormat.stripExceptNumbers(editTextBoldCursorArr[a].getText().toString()));
                    a++;
                } else {
                    return codeBuilder.toString();
                }
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onNextPressed(String code) {
            if (this.nextPressed) {
                return;
            }
            if (code == null) {
                code = getCode();
            }
            if (TextUtils.isEmpty(code)) {
                AndroidUtilities.shakeView(this.codeFieldContainer, 2.0f, 0);
                return;
            }
            this.nextPressed = true;
            int i = this.verificationType;
            if (i == 2) {
                AndroidUtilities.setWaitingForSms(false);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
            } else if (i == 3) {
                AndroidUtilities.setWaitingForCall(false);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
            }
            this.waitingForEvent = false;
            PassportActivity.this.showEditDoneProgress(true, true);
            final TLRPC.TL_account_verifyPhone req = new TLRPC.TL_account_verifyPhone();
            req.phone_number = this.phone;
            req.phone_code = code;
            req.phone_code_hash = this.phoneHash;
            destroyTimer();
            PassportActivity.this.needShowProgress();
            ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda10
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PassportActivity.PhoneConfirmationView.this.m4087xdbf17e59(req, tLObject, tL_error);
                }
            }, 2);
        }

        /* renamed from: lambda$onNextPressed$7$org-telegram-ui-PassportActivity$PhoneConfirmationView */
        public /* synthetic */ void m4087xdbf17e59(final TLRPC.TL_account_verifyPhone req, TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    PassportActivity.PhoneConfirmationView.this.m4086xdabb2b7a(error, req);
                }
            });
        }

        /* renamed from: lambda$onNextPressed$6$org-telegram-ui-PassportActivity$PhoneConfirmationView */
        public /* synthetic */ void m4086xdabb2b7a(TLRPC.TL_error error, TLRPC.TL_account_verifyPhone req) {
            int i;
            int i2;
            PassportActivity.this.needHideProgress();
            this.nextPressed = false;
            if (error != null) {
                this.lastError = error.text;
                int i3 = this.verificationType;
                if ((i3 == 3 && ((i2 = this.nextType) == 4 || i2 == 2)) || ((i3 == 2 && ((i = this.nextType) == 4 || i == 3)) || (i3 == 4 && this.nextType == 2))) {
                    createTimer();
                }
                int i4 = this.verificationType;
                if (i4 == 2) {
                    AndroidUtilities.setWaitingForSms(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (i4 == 3) {
                    AndroidUtilities.setWaitingForCall(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
                }
                this.waitingForEvent = true;
                if (this.verificationType != 3) {
                    AlertsCreator.processError(PassportActivity.this.currentAccount, error, PassportActivity.this, req, new Object[0]);
                }
                PassportActivity.this.showEditDoneProgress(true, false);
                if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                    int a = 0;
                    while (true) {
                        EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                        if (a < editTextBoldCursorArr.length) {
                            editTextBoldCursorArr[a].setText("");
                            a++;
                        } else {
                            editTextBoldCursorArr[0].requestFocus();
                            return;
                        }
                    }
                } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                    onBackPressed(true);
                    PassportActivity.this.setPage(0, true, null);
                }
            } else {
                destroyTimer();
                destroyCodeTimer();
                PassportActivityDelegate passportActivityDelegate = PassportActivity.this.delegate;
                TLRPC.TL_secureRequiredType tL_secureRequiredType = PassportActivity.this.currentType;
                String str = (String) PassportActivity.this.currentValues.get("phone");
                final PassportActivity passportActivity = PassportActivity.this;
                passportActivityDelegate.saveValue(tL_secureRequiredType, str, null, null, null, null, null, null, null, null, new Runnable() { // from class: org.telegram.ui.PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda8
                    @Override // java.lang.Runnable
                    public final void run() {
                        PassportActivity.this.finishFragment();
                    }
                }, null);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean onBackPressed(boolean force) {
            if (!force) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PassportActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.getString("StopVerification", R.string.StopVerification));
                builder.setPositiveButton(LocaleController.getString("Continue", R.string.Continue), null);
                builder.setNegativeButton(LocaleController.getString("Stop", R.string.Stop), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        PassportActivity.PhoneConfirmationView.this.m4085xa758ab2c(dialogInterface, i);
                    }
                });
                PassportActivity.this.showDialog(builder.create());
                return false;
            }
            TLRPC.TL_auth_cancelCode req = new TLRPC.TL_auth_cancelCode();
            req.phone_number = this.phone;
            req.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda1.INSTANCE, 2);
            destroyTimer();
            destroyCodeTimer();
            this.currentParams = null;
            int i = this.verificationType;
            if (i == 2) {
                AndroidUtilities.setWaitingForSms(false);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
            } else if (i == 3) {
                AndroidUtilities.setWaitingForCall(false);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
            }
            this.waitingForEvent = false;
            return true;
        }

        /* renamed from: lambda$onBackPressed$8$org-telegram-ui-PassportActivity$PhoneConfirmationView */
        public /* synthetic */ void m4085xa758ab2c(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            PassportActivity.this.setPage(0, true, null);
        }

        public static /* synthetic */ void lambda$onBackPressed$9(TLObject response, TLRPC.TL_error error) {
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onDestroyActivity() {
            super.onDestroyActivity();
            int i = this.verificationType;
            if (i == 2) {
                AndroidUtilities.setWaitingForSms(false);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
            } else if (i == 3) {
                AndroidUtilities.setWaitingForCall(false);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
            }
            this.waitingForEvent = false;
            destroyTimer();
            destroyCodeTimer();
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onShow() {
            super.onShow();
            LinearLayout linearLayout = this.codeFieldContainer;
            if (linearLayout != null && linearLayout.getVisibility() == 0) {
                for (int a = this.codeField.length - 1; a >= 0; a--) {
                    if (a == 0 || this.codeField[a].length() != 0) {
                        this.codeField[a].requestFocus();
                        EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                        editTextBoldCursorArr[a].setSelection(editTextBoldCursorArr[a].length());
                        AndroidUtilities.showKeyboard(this.codeField[a]);
                        return;
                    }
                }
            }
        }

        @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
        public void didReceivedNotification(int id, int account, Object... args) {
            if (!this.waitingForEvent || this.codeField == null) {
                return;
            }
            if (id == NotificationCenter.didReceiveSmsCode) {
                this.codeField[0].setText("" + args[0]);
                onNextPressed(null);
            } else if (id == NotificationCenter.didReceiveCall) {
                String num = "" + args[0];
                if (!AndroidUtilities.checkPhonePattern(this.pattern, num)) {
                    return;
                }
                this.ignoreOnTextChange = true;
                this.codeField[0].setText(num);
                this.ignoreOnTextChange = false;
                onNextPressed(null);
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        arrayList.add(new ThemeDescription(this.extraBackgroundView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        if (this.extraBackgroundView2 != null) {
            arrayList.add(new ThemeDescription(this.extraBackgroundView2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        }
        for (int a = 0; a < this.dividers.size(); a++) {
            arrayList.add(new ThemeDescription(this.dividers.get(a), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_divider));
        }
        for (Map.Entry<SecureDocument, SecureDocumentCell> entry : this.documentsCells.entrySet()) {
            SecureDocumentCell cell = entry.getValue();
            arrayList.add(new ThemeDescription(cell, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{SecureDocumentCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(cell, 0, new Class[]{SecureDocumentCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(cell, 0, new Class[]{SecureDocumentCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        }
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{TextDetailSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{TextSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{TextDetailSecureCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextDetailSecureCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextDetailSecureCell.class}, null, null, null, Theme.key_divider));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextDetailSecureCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailSecureCell.class}, new String[]{"checkImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_addedIcon));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        if (this.inputFields != null) {
            for (int a2 = 0; a2 < this.inputFields.length; a2++) {
                arrayList.add(new ThemeDescription((View) this.inputFields[a2].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
                arrayList.add(new ThemeDescription(this.inputFields[a2], ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(this.inputFields[a2], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
                arrayList.add(new ThemeDescription(this.inputFields[a2], ThemeDescription.FLAG_HINTTEXTCOLOR | ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
                arrayList.add(new ThemeDescription(this.inputFields[a2], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
                arrayList.add(new ThemeDescription(this.inputFields[a2], ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
                arrayList.add(new ThemeDescription(this.inputFields[a2], ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteRedText3));
            }
        } else {
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_windowBackgroundWhiteRedText3));
        }
        if (this.inputExtraFields != null) {
            for (int a3 = 0; a3 < this.inputExtraFields.length; a3++) {
                arrayList.add(new ThemeDescription((View) this.inputExtraFields[a3].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
                arrayList.add(new ThemeDescription(this.inputExtraFields[a3], ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(this.inputExtraFields[a3], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
                arrayList.add(new ThemeDescription(this.inputExtraFields[a3], ThemeDescription.FLAG_HINTTEXTCOLOR | ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
                arrayList.add(new ThemeDescription(this.inputExtraFields[a3], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
                arrayList.add(new ThemeDescription(this.inputExtraFields[a3], ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
                arrayList.add(new ThemeDescription(this.inputExtraFields[a3], ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteRedText3));
            }
        }
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
        arrayList.add(new ThemeDescription(this.noPasswordImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_messagePanelIcons));
        arrayList.add(new ThemeDescription(this.noPasswordTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription(this.noPasswordSetTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText5));
        arrayList.add(new ThemeDescription(this.passwordForgotButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4));
        arrayList.add(new ThemeDescription(this.plusTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.acceptTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_passport_authorizeText));
        arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_passport_authorizeBackground));
        arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_passport_authorizeBackgroundSelected));
        arrayList.add(new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressInner2));
        arrayList.add(new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressOuter2));
        arrayList.add(new ThemeDescription(this.progressViewButton, 0, null, null, null, null, Theme.key_contextProgressInner2));
        arrayList.add(new ThemeDescription(this.progressViewButton, 0, null, null, null, null, Theme.key_contextProgressOuter2));
        arrayList.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_sessions_devicesImage));
        arrayList.add(new ThemeDescription(this.emptyTextView1, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.emptyTextView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.emptyTextView3, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4));
        return arrayList;
    }
}
