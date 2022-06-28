package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.graphics.ColorUtils$$ExternalSyntheticBackport0;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import com.microsoft.appcenter.http.DefaultHttpClient;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.APIConnectionException;
import com.stripe.android.exception.APIException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.net.StripeApiHandler;
import com.stripe.android.net.TokenParser;
import j$.util.Optional;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SRPHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EditTextSettingsCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.PaymentInfoCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.RecurrentPaymentsAcceptCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextPriceCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.CountrySelectActivity;
import org.telegram.ui.PaymentFormActivity;
/* loaded from: classes4.dex */
public class PaymentFormActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int FIELDS_COUNT_ADDRESS = 10;
    private static final int FIELDS_COUNT_CARD = 6;
    private static final int FIELDS_COUNT_PASSWORD = 3;
    private static final int FIELDS_COUNT_SAVEDCARD = 2;
    private static final int FIELD_CARD = 0;
    private static final int FIELD_CARDNAME = 2;
    private static final int FIELD_CARD_COUNTRY = 4;
    private static final int FIELD_CARD_POSTCODE = 5;
    private static final int FIELD_CITY = 2;
    private static final int FIELD_COUNTRY = 4;
    private static final int FIELD_CVV = 3;
    private static final int FIELD_EMAIL = 7;
    private static final int FIELD_ENTERPASSWORD = 0;
    private static final int FIELD_ENTERPASSWORDEMAIL = 2;
    private static final int FIELD_EXPIRE_DATE = 1;
    private static final int FIELD_NAME = 6;
    private static final int FIELD_PHONE = 9;
    private static final int FIELD_PHONECODE = 8;
    private static final int FIELD_POSTCODE = 5;
    private static final int FIELD_REENTERPASSWORD = 1;
    private static final int FIELD_SAVEDCARD = 0;
    private static final int FIELD_SAVEDPASSWORD = 1;
    private static final int FIELD_STATE = 3;
    private static final int FIELD_STREET1 = 0;
    private static final int FIELD_STREET2 = 1;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    private static final int STEP_CHECKOUT = 4;
    private static final int STEP_CONFIRM_PASSWORD = 3;
    private static final int STEP_PAYMENT_INFO = 2;
    private static final int STEP_RECEIPT = 5;
    private static final int STEP_SET_PASSWORD_EMAIL = 6;
    private static final int STEP_SHIPPING_INFORMATION = 0;
    private static final int STEP_SHIPPING_METHODS = 1;
    private static final int done_button = 1;
    private TLRPC.User botUser;
    private TextInfoPrivacyCell[] bottomCell;
    private BottomFrameLayout bottomLayout;
    private boolean canceled;
    private String cardName;
    private TextCheckCell checkCell1;
    private EditTextSettingsCell codeFieldCell;
    private HashMap<String, String> codesMap;
    private ArrayList<String> countriesArray;
    private HashMap<String, String> countriesMap;
    private String countryName;
    private String currentBotName;
    private String currentItemName;
    private TLRPC.TL_account_password currentPassword;
    private int currentStep;
    private PaymentFormActivityDelegate delegate;
    private TextDetailSettingsCell[] detailSettingsCell;
    private ArrayList<View> dividers;
    private ActionBarMenuItem doneItem;
    private AnimatorSet doneItemAnimation;
    private boolean donePressed;
    private int emailCodeLength;
    private FrameLayout googlePayButton;
    private FrameLayout googlePayContainer;
    private String googlePayCountryCode;
    private TLRPC.TL_inputPaymentCredentialsGooglePay googlePayCredentials;
    private JSONObject googlePayParameters;
    private String googlePayPublicKey;
    private HeaderCell[] headerCell;
    private boolean ignoreOnCardChange;
    private boolean ignoreOnPhoneChange;
    private boolean ignoreOnTextChange;
    private boolean initGooglePay;
    private EditTextBoldCursor[] inputFields;
    private String invoiceSlug;
    private boolean isAcceptTermsChecked;
    private boolean isCheckoutPreview;
    private boolean isWebView;
    private LinearLayout linearLayout2;
    private boolean loadingPasswordInfo;
    private MessageObject messageObject;
    private boolean needPayAfterTransition;
    private boolean need_card_country;
    private boolean need_card_name;
    private boolean need_card_postcode;
    private BaseFragment parentFragment;
    private PaymentFormActivity passwordFragment;
    private boolean passwordOk;
    private TextView payTextView;
    private TLRPC.TL_payments_paymentForm paymentForm;
    private PaymentFormCallback paymentFormCallback;
    private PaymentInfoCell paymentInfoCell;
    private String paymentJson;
    private TLRPC.TL_payments_paymentReceipt paymentReceipt;
    private boolean paymentStatusSent;
    private PaymentsClient paymentsClient;
    private HashMap<String, String> phoneFormatMap;
    private ArrayList<TLRPC.TL_labeledPrice> prices;
    private ContextProgressView progressView;
    private ContextProgressView progressViewButton;
    private String providerApiKey;
    private RadioCell[] radioCells;
    private RecurrentPaymentsAcceptCell recurrentAcceptCell;
    private boolean recurrentAccepted;
    private TLRPC.TL_payments_validatedRequestedInfo requestedInfo;
    private Theme.ResourcesProvider resourcesProvider;
    private boolean saveCardInfo;
    private boolean saveShippingInfo;
    private ScrollView scrollView;
    private ShadowSectionCell[] sectionCell;
    private TextSettingsCell[] settingsCell;
    private TLRPC.TL_shippingOption shippingOption;
    private Runnable shortPollRunnable;
    private boolean shouldNavigateBack;
    private boolean swipeBackEnabled;
    private TextView textView;
    private Long tipAmount;
    private LinearLayout tipLayout;
    private TextPriceCell totalCell;
    private String[] totalPrice;
    private String totalPriceDecimal;
    private TLRPC.TL_payments_validateRequestedInfo validateRequest;
    private boolean waitingForEmail;
    private WebView webView;
    private String webViewUrl;
    private boolean webviewLoading;

    /* loaded from: classes4.dex */
    public enum InvoiceStatus {
        PAID,
        CANCELLED,
        PENDING,
        FAILED
    }

    /* loaded from: classes4.dex */
    public interface PaymentFormCallback {
        void onInvoiceStatusChanged(InvoiceStatus invoiceStatus);
    }

    /* loaded from: classes4.dex */
    public interface PaymentFormActivityDelegate {
        void currentPasswordUpdated(TLRPC.TL_account_password tL_account_password);

        void didSelectNewAddress(TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo);

        boolean didSelectNewCard(String str, String str2, boolean z, TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay);

        void onFragmentDestroyed();

        /* renamed from: org.telegram.ui.PaymentFormActivity$PaymentFormActivityDelegate$-CC */
        /* loaded from: classes4.dex */
        public final /* synthetic */ class CC {
            public static boolean $default$didSelectNewCard(PaymentFormActivityDelegate _this, String tokenJson, String card, boolean saveCard, TLRPC.TL_inputPaymentCredentialsGooglePay googlePay) {
                return false;
            }

            public static void $default$didSelectNewAddress(PaymentFormActivityDelegate _this, TLRPC.TL_payments_validateRequestedInfo validateRequested) {
            }

            public static void $default$onFragmentDestroyed(PaymentFormActivityDelegate _this) {
            }

            public static void $default$currentPasswordUpdated(PaymentFormActivityDelegate _this, TLRPC.TL_account_password password) {
            }
        }
    }

    /* loaded from: classes4.dex */
    public class TelegramWebviewProxy {
        private TelegramWebviewProxy() {
            PaymentFormActivity.this = r1;
        }

        @JavascriptInterface
        public void postEvent(final String eventName, final String eventData) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PaymentFormActivity$TelegramWebviewProxy$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    PaymentFormActivity.TelegramWebviewProxy.this.m4160x74f391d2(eventName, eventData);
                }
            });
        }

        /* renamed from: lambda$postEvent$0$org-telegram-ui-PaymentFormActivity$TelegramWebviewProxy */
        public /* synthetic */ void m4160x74f391d2(String eventName, String eventData) {
            if (PaymentFormActivity.this.getParentActivity() != null && eventName.equals("payment_form_submit")) {
                try {
                    JSONObject jsonObject = new JSONObject(eventData);
                    JSONObject response = jsonObject.getJSONObject("credentials");
                    PaymentFormActivity.this.paymentJson = response.toString();
                    PaymentFormActivity.this.cardName = jsonObject.getString("title");
                } catch (Throwable e) {
                    PaymentFormActivity.this.paymentJson = eventData;
                    FileLog.e(e);
                }
                PaymentFormActivity.this.goToNextStep();
            }
        }
    }

    /* loaded from: classes4.dex */
    public class LinkSpan extends ClickableSpan {
        public LinkSpan() {
            PaymentFormActivity.this = this$0;
        }

        @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

        @Override // android.text.style.ClickableSpan
        public void onClick(View widget) {
            PaymentFormActivity.this.presentFragment(new TwoStepVerificationSetupActivity(6, PaymentFormActivity.this.currentPassword));
        }
    }

    public PaymentFormActivity(TLRPC.TL_payments_paymentReceipt receipt) {
        this.countriesArray = new ArrayList<>();
        this.countriesMap = new HashMap<>();
        this.codesMap = new HashMap<>();
        this.phoneFormatMap = new HashMap<>();
        this.swipeBackEnabled = true;
        this.headerCell = new HeaderCell[3];
        this.dividers = new ArrayList<>();
        this.sectionCell = new ShadowSectionCell[3];
        this.bottomCell = new TextInfoPrivacyCell[3];
        this.settingsCell = new TextSettingsCell[2];
        this.detailSettingsCell = new TextDetailSettingsCell[7];
        this.emailCodeLength = 6;
        this.currentStep = 5;
        TLRPC.TL_payments_paymentForm tL_payments_paymentForm = new TLRPC.TL_payments_paymentForm();
        this.paymentForm = tL_payments_paymentForm;
        this.paymentReceipt = receipt;
        tL_payments_paymentForm.bot_id = receipt.bot_id;
        this.paymentForm.invoice = receipt.invoice;
        this.paymentForm.provider_id = receipt.provider_id;
        this.paymentForm.users = receipt.users;
        this.shippingOption = receipt.shipping;
        if (receipt.tip_amount != 0) {
            this.tipAmount = Long.valueOf(receipt.tip_amount);
        }
        TLRPC.User user = getMessagesController().getUser(Long.valueOf(receipt.bot_id));
        this.botUser = user;
        if (user != null) {
            this.currentBotName = user.first_name;
        } else {
            this.currentBotName = "";
        }
        this.currentItemName = receipt.title;
        if (receipt.info != null) {
            this.validateRequest = new TLRPC.TL_payments_validateRequestedInfo();
            if (this.messageObject != null) {
                TLRPC.TL_inputInvoiceMessage inputInvoice = new TLRPC.TL_inputInvoiceMessage();
                inputInvoice.peer = getMessagesController().getInputPeer(receipt.bot_id);
                this.validateRequest.invoice = inputInvoice;
            } else {
                TLRPC.TL_inputInvoiceSlug inputInvoice2 = new TLRPC.TL_inputInvoiceSlug();
                inputInvoice2.slug = this.invoiceSlug;
                this.validateRequest.invoice = inputInvoice2;
            }
            this.validateRequest.info = receipt.info;
        }
        this.cardName = receipt.credentials_title;
    }

    public PaymentFormActivity(TLRPC.TL_payments_paymentForm form, String invoiceSlug, BaseFragment parentFragment) {
        this(form, null, invoiceSlug, parentFragment);
    }

    public PaymentFormActivity(TLRPC.TL_payments_paymentForm form, MessageObject message, BaseFragment parentFragment) {
        this(form, message, null, parentFragment);
    }

    public PaymentFormActivity(TLRPC.TL_payments_paymentForm form, MessageObject message, String invoiceSlug, BaseFragment parentFragment) {
        this.countriesArray = new ArrayList<>();
        this.countriesMap = new HashMap<>();
        this.codesMap = new HashMap<>();
        this.phoneFormatMap = new HashMap<>();
        this.swipeBackEnabled = true;
        this.headerCell = new HeaderCell[3];
        this.dividers = new ArrayList<>();
        this.sectionCell = new ShadowSectionCell[3];
        this.bottomCell = new TextInfoPrivacyCell[3];
        this.settingsCell = new TextSettingsCell[2];
        this.detailSettingsCell = new TextDetailSettingsCell[7];
        this.emailCodeLength = 6;
        this.isCheckoutPreview = true;
        init(form, message, invoiceSlug, 4, null, null, null, null, null, null, false, null, parentFragment);
    }

    private PaymentFormActivity(TLRPC.TL_payments_paymentForm form, MessageObject message, String invoiceSlug, int step, TLRPC.TL_payments_validatedRequestedInfo validatedRequestedInfo, TLRPC.TL_shippingOption shipping, Long tips, String tokenJson, String card, TLRPC.TL_payments_validateRequestedInfo request, boolean saveCard, TLRPC.TL_inputPaymentCredentialsGooglePay googlePay, BaseFragment parent) {
        this.countriesArray = new ArrayList<>();
        this.countriesMap = new HashMap<>();
        this.codesMap = new HashMap<>();
        this.phoneFormatMap = new HashMap<>();
        this.swipeBackEnabled = true;
        this.headerCell = new HeaderCell[3];
        this.dividers = new ArrayList<>();
        this.sectionCell = new ShadowSectionCell[3];
        this.bottomCell = new TextInfoPrivacyCell[3];
        this.settingsCell = new TextSettingsCell[2];
        this.detailSettingsCell = new TextDetailSettingsCell[7];
        this.emailCodeLength = 6;
        init(form, message, invoiceSlug, step, validatedRequestedInfo, shipping, tips, tokenJson, card, request, saveCard, googlePay, parent);
    }

    public void setPaymentFormCallback(PaymentFormCallback callback) {
        this.paymentFormCallback = callback;
    }

    private void setCurrentPassword(TLRPC.TL_account_password password) {
        if (password.has_password) {
            if (getParentActivity() == null) {
                return;
            }
            goToNextStep();
            return;
        }
        this.currentPassword = password;
        this.waitingForEmail = !TextUtils.isEmpty(password.email_unconfirmed_pattern);
        updatePasswordFields();
    }

    private void setDelegate(PaymentFormActivityDelegate paymentFormActivityDelegate) {
        this.delegate = paymentFormActivityDelegate;
    }

    public void setResourcesProvider(Theme.ResourcesProvider provider) {
        this.resourcesProvider = provider;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public Theme.ResourcesProvider getResourceProvider() {
        return this.resourcesProvider;
    }

    private void init(TLRPC.TL_payments_paymentForm form, MessageObject message, String slug, int step, TLRPC.TL_payments_validatedRequestedInfo validatedRequestedInfo, TLRPC.TL_shippingOption shipping, Long tips, String tokenJson, String card, TLRPC.TL_payments_validateRequestedInfo request, boolean saveCard, TLRPC.TL_inputPaymentCredentialsGooglePay googlePay, BaseFragment parent) {
        this.currentStep = step;
        this.parentFragment = parent;
        this.paymentJson = tokenJson;
        this.googlePayCredentials = googlePay;
        this.requestedInfo = validatedRequestedInfo;
        this.paymentForm = form;
        this.shippingOption = shipping;
        this.tipAmount = tips;
        this.messageObject = message;
        this.invoiceSlug = slug;
        this.saveCardInfo = saveCard;
        this.isWebView = !"stripe".equals(form.native_provider) && !"smartglocal".equals(this.paymentForm.native_provider);
        TLRPC.User user = getMessagesController().getUser(Long.valueOf(form.bot_id));
        this.botUser = user;
        if (user != null) {
            this.currentBotName = user.first_name;
        } else {
            this.currentBotName = "";
        }
        this.currentItemName = form.title;
        this.validateRequest = request;
        this.saveShippingInfo = true;
        if (!saveCard && this.currentStep != 4) {
            this.saveCardInfo = this.paymentForm.saved_credentials != null;
        } else {
            this.saveCardInfo = saveCard;
        }
        if (card == null) {
            if (form.saved_credentials != null) {
                this.cardName = form.saved_credentials.title;
                return;
            }
            return;
        }
        this.cardName = card;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                int i = this.currentStep;
                if ((i == 2 || i == 6) && !this.paymentForm.invoice.test) {
                    getParentActivity().getWindow().setFlags(8192, 8192);
                } else if (SharedConfig.passcodeHash.length() == 0 || SharedConfig.allowScreenCapture) {
                    getParentActivity().getWindow().clearFlags(8192);
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:186:0x084b  */
    /* JADX WARN: Removed duplicated region for block: B:187:0x085c  */
    /* JADX WARN: Removed duplicated region for block: B:190:0x0866  */
    /* JADX WARN: Removed duplicated region for block: B:193:0x087b  */
    /* JADX WARN: Removed duplicated region for block: B:196:0x0893  */
    /* JADX WARN: Removed duplicated region for block: B:197:0x08a0  */
    /* JADX WARN: Removed duplicated region for block: B:206:0x08d4  */
    /* JADX WARN: Removed duplicated region for block: B:215:0x08f7  */
    /* JADX WARN: Removed duplicated region for block: B:236:0x094d  */
    /* JADX WARN: Removed duplicated region for block: B:254:0x0a04 A[Catch: Exception -> 0x0a0e, TRY_LEAVE, TryCatch #1 {Exception -> 0x0a0e, blocks: (B:252:0x09f8, B:254:0x0a04), top: B:698:0x09f8 }] */
    /* JADX WARN: Removed duplicated region for block: B:263:0x0a25  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x0244  */
    @Override // org.telegram.ui.ActionBar.BaseFragment
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.view.View createView(android.content.Context r37) {
        /*
            Method dump skipped, instructions count: 8238
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.createView(android.content.Context):android.view.View");
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ boolean m4104lambda$createView$1$orgtelegramuiPaymentFormActivity(View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            CountrySelectActivity fragment = new CountrySelectActivity(false);
            fragment.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda58
                @Override // org.telegram.ui.CountrySelectActivity.CountrySelectActivityDelegate
                public final void didSelectCountry(CountrySelectActivity.Country country) {
                    PaymentFormActivity.this.m4103lambda$createView$0$orgtelegramuiPaymentFormActivity(country);
                }
            });
            presentFragment(fragment);
        }
        return true;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4103lambda$createView$0$orgtelegramuiPaymentFormActivity(CountrySelectActivity.Country country) {
        this.inputFields[4].setText(country.name);
        this.countryName = country.shortname;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ boolean m4113lambda$createView$2$orgtelegramuiPaymentFormActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            int num = ((Integer) textView.getTag()).intValue();
            while (true) {
                int i2 = num + 1;
                EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                if (i2 < editTextBoldCursorArr.length) {
                    num++;
                    if (num != 4 && ((View) editTextBoldCursorArr[num].getParent()).getVisibility() == 0) {
                        this.inputFields[num].requestFocus();
                        break;
                    }
                } else {
                    break;
                }
            }
            return true;
        } else if (i == 6) {
            this.doneItem.performClick();
            return true;
        } else {
            return false;
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4123lambda$createView$3$orgtelegramuiPaymentFormActivity(View v) {
        boolean z = !this.saveShippingInfo;
        this.saveShippingInfo = z;
        this.checkCell1.setChecked(z);
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4126lambda$createView$4$orgtelegramuiPaymentFormActivity(View v) {
        boolean z = !this.saveCardInfo;
        this.saveCardInfo = z;
        this.checkCell1.setChecked(z);
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ boolean m4128lambda$createView$6$orgtelegramuiPaymentFormActivity(View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            CountrySelectActivity fragment = new CountrySelectActivity(false);
            fragment.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda59
                @Override // org.telegram.ui.CountrySelectActivity.CountrySelectActivityDelegate
                public final void didSelectCountry(CountrySelectActivity.Country country) {
                    PaymentFormActivity.this.m4127lambda$createView$5$orgtelegramuiPaymentFormActivity(country);
                }
            });
            presentFragment(fragment);
        }
        return true;
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4127lambda$createView$5$orgtelegramuiPaymentFormActivity(CountrySelectActivity.Country country) {
        this.inputFields[4].setText(country.name);
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ boolean m4129lambda$createView$7$orgtelegramuiPaymentFormActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            int num = ((Integer) textView.getTag()).intValue();
            while (true) {
                int i2 = num + 1;
                EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                if (i2 < editTextBoldCursorArr.length) {
                    num++;
                    if (num == 4) {
                        num++;
                    }
                    if (((View) editTextBoldCursorArr[num].getParent()).getVisibility() == 0) {
                        this.inputFields[num].requestFocus();
                        break;
                    }
                } else {
                    break;
                }
            }
            return true;
        } else if (i == 6) {
            this.doneItem.performClick();
            return true;
        } else {
            return false;
        }
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4130lambda$createView$8$orgtelegramuiPaymentFormActivity(View v) {
        boolean z = !this.saveCardInfo;
        this.saveCardInfo = z;
        this.checkCell1.setChecked(z);
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4131lambda$createView$9$orgtelegramuiPaymentFormActivity(View v) {
        int num = ((Integer) v.getTag()).intValue();
        int a1 = 0;
        while (true) {
            RadioCell[] radioCellArr = this.radioCells;
            if (a1 < radioCellArr.length) {
                radioCellArr[a1].setChecked(num == a1, true);
                a1++;
            } else {
                return;
            }
        }
    }

    public static /* synthetic */ boolean lambda$createView$10(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ boolean m4105lambda$createView$11$orgtelegramuiPaymentFormActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 6) {
            this.doneItem.performClick();
            return true;
        }
        return false;
    }

    /* renamed from: lambda$createView$12$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4106lambda$createView$12$orgtelegramuiPaymentFormActivity(View v) {
        this.passwordOk = false;
        goToNextStep();
    }

    /* renamed from: lambda$createView$13$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4107lambda$createView$13$orgtelegramuiPaymentFormActivity(View v) {
        this.inputFields[0].requestFocus();
        AndroidUtilities.showKeyboard(this.inputFields[0]);
    }

    public static /* synthetic */ boolean lambda$createView$14(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 6) {
            AndroidUtilities.hideKeyboard(textView);
            return true;
        }
        return false;
    }

    /* renamed from: lambda$createView$15$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4108lambda$createView$15$orgtelegramuiPaymentFormActivity(TextView valueTextView, long amount, View v) {
        long amoumt = ((Long) valueTextView.getTag()).longValue();
        Long l = this.tipAmount;
        if (l != null && amoumt == l.longValue()) {
            this.ignoreOnTextChange = true;
            this.inputFields[0].setText("");
            this.ignoreOnTextChange = false;
            this.tipAmount = 0L;
            updateTotalPrice();
        } else {
            this.inputFields[0].setText(LocaleController.getInstance().formatCurrencyString(amount, false, true, true, this.paymentForm.invoice.currency));
        }
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
        editTextBoldCursorArr[0].setSelection(editTextBoldCursorArr[0].length());
    }

    /* renamed from: lambda$createView$17$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4110lambda$createView$17$orgtelegramuiPaymentFormActivity(View v) {
        if (getParentActivity() == null) {
            return;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("PaymentCheckoutMethod", R.string.PaymentCheckoutMethod), true);
        builder.setItems(new CharSequence[]{this.cardName, LocaleController.getString("PaymentCheckoutMethodNewCard", R.string.PaymentCheckoutMethodNewCard)}, new int[]{R.drawable.msg_payment_card, R.drawable.msg_addbot}, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda10
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                PaymentFormActivity.this.m4109lambda$createView$16$orgtelegramuiPaymentFormActivity(dialogInterface, i);
            }
        });
        showDialog(builder.create());
    }

    /* renamed from: lambda$createView$16$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4109lambda$createView$16$orgtelegramuiPaymentFormActivity(DialogInterface dialog, int which) {
        if (which == 1) {
            PaymentFormActivity activity = new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 2, this.requestedInfo, this.shippingOption, this.tipAmount, null, this.cardName, this.validateRequest, this.saveCardInfo, null, this.parentFragment);
            activity.setDelegate(new PaymentFormActivityDelegate() { // from class: org.telegram.ui.PaymentFormActivity.13
                @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
                public /* synthetic */ void currentPasswordUpdated(TLRPC.TL_account_password tL_account_password) {
                    PaymentFormActivityDelegate.CC.$default$currentPasswordUpdated(this, tL_account_password);
                }

                @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
                public /* synthetic */ void didSelectNewAddress(TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo) {
                    PaymentFormActivityDelegate.CC.$default$didSelectNewAddress(this, tL_payments_validateRequestedInfo);
                }

                @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
                public /* synthetic */ void onFragmentDestroyed() {
                    PaymentFormActivityDelegate.CC.$default$onFragmentDestroyed(this);
                }

                @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
                public boolean didSelectNewCard(String tokenJson, String card, boolean saveCard, TLRPC.TL_inputPaymentCredentialsGooglePay googlePay) {
                    PaymentFormActivity.this.paymentForm.saved_credentials = null;
                    PaymentFormActivity.this.paymentJson = tokenJson;
                    PaymentFormActivity.this.saveCardInfo = saveCard;
                    PaymentFormActivity.this.cardName = card;
                    PaymentFormActivity.this.googlePayCredentials = googlePay;
                    PaymentFormActivity.this.detailSettingsCell[0].setTextAndValue(PaymentFormActivity.this.cardName, LocaleController.getString("PaymentCheckoutMethod", R.string.PaymentCheckoutMethod), true);
                    return false;
                }
            });
            presentFragment(activity);
        }
    }

    /* renamed from: lambda$createView$18$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4111lambda$createView$18$orgtelegramuiPaymentFormActivity(View v) {
        PaymentFormActivity activity = new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 0, this.requestedInfo, this.shippingOption, this.tipAmount, null, this.cardName, this.validateRequest, this.saveCardInfo, null, this.parentFragment);
        activity.setDelegate(new PaymentFormActivityDelegate() { // from class: org.telegram.ui.PaymentFormActivity.14
            @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
            public /* synthetic */ void currentPasswordUpdated(TLRPC.TL_account_password tL_account_password) {
                PaymentFormActivityDelegate.CC.$default$currentPasswordUpdated(this, tL_account_password);
            }

            @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
            public /* synthetic */ boolean didSelectNewCard(String str, String str2, boolean z, TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay) {
                return PaymentFormActivityDelegate.CC.$default$didSelectNewCard(this, str, str2, z, tL_inputPaymentCredentialsGooglePay);
            }

            @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
            public /* synthetic */ void onFragmentDestroyed() {
                PaymentFormActivityDelegate.CC.$default$onFragmentDestroyed(this);
            }

            @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
            public void didSelectNewAddress(TLRPC.TL_payments_validateRequestedInfo validateRequested) {
                PaymentFormActivity.this.validateRequest = validateRequested;
                PaymentFormActivity paymentFormActivity = PaymentFormActivity.this;
                paymentFormActivity.setAddressFields(paymentFormActivity.validateRequest.info);
            }
        });
        presentFragment(activity);
    }

    /* renamed from: lambda$createView$19$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4112lambda$createView$19$orgtelegramuiPaymentFormActivity(View v) {
        PaymentFormActivity activity = new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 0, this.requestedInfo, this.shippingOption, this.tipAmount, null, this.cardName, this.validateRequest, this.saveCardInfo, null, this.parentFragment);
        activity.setDelegate(new PaymentFormActivityDelegate() { // from class: org.telegram.ui.PaymentFormActivity.15
            @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
            public /* synthetic */ void currentPasswordUpdated(TLRPC.TL_account_password tL_account_password) {
                PaymentFormActivityDelegate.CC.$default$currentPasswordUpdated(this, tL_account_password);
            }

            @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
            public /* synthetic */ boolean didSelectNewCard(String str, String str2, boolean z, TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay) {
                return PaymentFormActivityDelegate.CC.$default$didSelectNewCard(this, str, str2, z, tL_inputPaymentCredentialsGooglePay);
            }

            @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
            public /* synthetic */ void onFragmentDestroyed() {
                PaymentFormActivityDelegate.CC.$default$onFragmentDestroyed(this);
            }

            @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
            public void didSelectNewAddress(TLRPC.TL_payments_validateRequestedInfo validateRequested) {
                PaymentFormActivity.this.validateRequest = validateRequested;
                PaymentFormActivity paymentFormActivity = PaymentFormActivity.this;
                paymentFormActivity.setAddressFields(paymentFormActivity.validateRequest.info);
            }
        });
        presentFragment(activity);
    }

    /* renamed from: lambda$createView$20$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4114lambda$createView$20$orgtelegramuiPaymentFormActivity(View v) {
        PaymentFormActivity activity = new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 0, this.requestedInfo, this.shippingOption, this.tipAmount, null, this.cardName, this.validateRequest, this.saveCardInfo, null, this.parentFragment);
        activity.setDelegate(new PaymentFormActivityDelegate() { // from class: org.telegram.ui.PaymentFormActivity.16
            @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
            public /* synthetic */ void currentPasswordUpdated(TLRPC.TL_account_password tL_account_password) {
                PaymentFormActivityDelegate.CC.$default$currentPasswordUpdated(this, tL_account_password);
            }

            @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
            public /* synthetic */ boolean didSelectNewCard(String str, String str2, boolean z, TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay) {
                return PaymentFormActivityDelegate.CC.$default$didSelectNewCard(this, str, str2, z, tL_inputPaymentCredentialsGooglePay);
            }

            @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
            public /* synthetic */ void onFragmentDestroyed() {
                PaymentFormActivityDelegate.CC.$default$onFragmentDestroyed(this);
            }

            @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
            public void didSelectNewAddress(TLRPC.TL_payments_validateRequestedInfo validateRequested) {
                PaymentFormActivity.this.validateRequest = validateRequested;
                PaymentFormActivity paymentFormActivity = PaymentFormActivity.this;
                paymentFormActivity.setAddressFields(paymentFormActivity.validateRequest.info);
            }
        });
        presentFragment(activity);
    }

    /* renamed from: lambda$createView$21$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4115lambda$createView$21$orgtelegramuiPaymentFormActivity(View v) {
        PaymentFormActivity activity = new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 0, this.requestedInfo, this.shippingOption, this.tipAmount, null, this.cardName, this.validateRequest, this.saveCardInfo, null, this.parentFragment);
        activity.setDelegate(new PaymentFormActivityDelegate() { // from class: org.telegram.ui.PaymentFormActivity.17
            @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
            public /* synthetic */ void currentPasswordUpdated(TLRPC.TL_account_password tL_account_password) {
                PaymentFormActivityDelegate.CC.$default$currentPasswordUpdated(this, tL_account_password);
            }

            @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
            public /* synthetic */ boolean didSelectNewCard(String str, String str2, boolean z, TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay) {
                return PaymentFormActivityDelegate.CC.$default$didSelectNewCard(this, str, str2, z, tL_inputPaymentCredentialsGooglePay);
            }

            @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
            public /* synthetic */ void onFragmentDestroyed() {
                PaymentFormActivityDelegate.CC.$default$onFragmentDestroyed(this);
            }

            @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
            public void didSelectNewAddress(TLRPC.TL_payments_validateRequestedInfo validateRequested) {
                PaymentFormActivity.this.validateRequest = validateRequested;
                PaymentFormActivity paymentFormActivity = PaymentFormActivity.this;
                paymentFormActivity.setAddressFields(paymentFormActivity.validateRequest.info);
            }
        });
        presentFragment(activity);
    }

    /* renamed from: lambda$createView$24$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4118lambda$createView$24$orgtelegramuiPaymentFormActivity(String providerName, final View v) {
        int step;
        if (this.paymentForm.invoice.recurring && !this.recurrentAccepted) {
            AndroidUtilities.shakeViewSpring(this.recurrentAcceptCell.getTextView(), 4.5f);
            try {
                this.recurrentAcceptCell.performHapticFeedback(3, 2);
            } catch (Exception e) {
            }
        } else if (this.isCheckoutPreview && this.paymentForm.saved_info != null && this.validateRequest == null) {
            setDonePressed(true);
            sendSavedForm(new Runnable() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda28
                @Override // java.lang.Runnable
                public final void run() {
                    PaymentFormActivity.this.m4116lambda$createView$22$orgtelegramuiPaymentFormActivity(v);
                }
            });
        } else if (this.isCheckoutPreview && ((this.paymentForm.saved_info == null && (this.paymentForm.invoice.shipping_address_requested || this.paymentForm.invoice.email_requested || this.paymentForm.invoice.name_requested || this.paymentForm.invoice.phone_requested)) || this.paymentForm.saved_credentials == null || (this.shippingOption == null && this.paymentForm.invoice.flexible))) {
            if (this.paymentForm.saved_info == null && (this.paymentForm.invoice.shipping_address_requested || this.paymentForm.invoice.email_requested || this.paymentForm.invoice.name_requested || this.paymentForm.invoice.phone_requested)) {
                step = 0;
            } else if (this.paymentForm.saved_credentials == null) {
                step = 2;
            } else {
                step = 1;
            }
            this.paymentStatusSent = true;
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, step, this.requestedInfo, this.shippingOption, this.tipAmount, null, this.cardName, this.validateRequest, this.saveCardInfo, null, this.parentFragment));
        } else {
            if (!this.paymentForm.password_missing && this.paymentForm.saved_credentials != null) {
                if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                    UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                }
                if (UserConfig.getInstance(this.currentAccount).tmpPassword == null) {
                    this.needPayAfterTransition = true;
                    presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 3, this.requestedInfo, this.shippingOption, this.tipAmount, null, this.cardName, this.validateRequest, this.saveCardInfo, null, this.parentFragment));
                    this.needPayAfterTransition = false;
                    return;
                } else if (this.isCheckoutPreview) {
                    this.isCheckoutPreview = false;
                    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.paymentFinished);
                }
            }
            TLRPC.User user = this.botUser;
            if (user == null || user.verified) {
                showPayAlert(this.totalPrice[0]);
                return;
            }
            String botKey = "payment_warning_" + this.botUser.id;
            SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
            if (preferences.getBoolean(botKey, false)) {
                showPayAlert(this.totalPrice[0]);
                return;
            }
            preferences.edit().putBoolean(botKey, true).commit();
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("PaymentWarning", R.string.PaymentWarning));
            builder.setMessage(LocaleController.formatString("PaymentWarningText", R.string.PaymentWarningText, this.currentBotName, providerName));
            builder.setPositiveButton(LocaleController.getString("Continue", R.string.Continue), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda21
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PaymentFormActivity.this.m4117lambda$createView$23$orgtelegramuiPaymentFormActivity(dialogInterface, i);
                }
            });
            showDialog(builder.create());
        }
    }

    /* renamed from: lambda$createView$22$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4116lambda$createView$22$orgtelegramuiPaymentFormActivity(View v) {
        setDonePressed(false);
        v.callOnClick();
    }

    /* renamed from: lambda$createView$23$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4117lambda$createView$23$orgtelegramuiPaymentFormActivity(DialogInterface dialogInterface, int i) {
        showPayAlert(this.totalPrice[0]);
    }

    /* renamed from: lambda$createView$25$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4119lambda$createView$25$orgtelegramuiPaymentFormActivity(View v) {
        if (this.donePressed) {
            return;
        }
        boolean z = !this.recurrentAccepted;
        this.recurrentAccepted = z;
        this.recurrentAcceptCell.setChecked(z);
        this.bottomLayout.setChecked(this.recurrentAccepted);
    }

    /* renamed from: lambda$createView$26$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ boolean m4120lambda$createView$26$orgtelegramuiPaymentFormActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        sendSavePassword(false);
        return true;
    }

    /* renamed from: lambda$createView$28$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4121lambda$createView$28$orgtelegramuiPaymentFormActivity(View v) {
        TLRPC.TL_account_resendPasswordEmail req = new TLRPC.TL_account_resendPasswordEmail();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, PaymentFormActivity$$ExternalSyntheticLambda56.INSTANCE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setMessage(LocaleController.getString("ResendCodeInfo", R.string.ResendCodeInfo));
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        showDialog(builder.create());
    }

    public static /* synthetic */ void lambda$createView$27(TLObject response, TLRPC.TL_error error) {
    }

    /* renamed from: lambda$createView$30$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4124lambda$createView$30$orgtelegramuiPaymentFormActivity(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        String text = LocaleController.getString("TurnPasswordOffQuestion", R.string.TurnPasswordOffQuestion);
        if (this.currentPassword.has_secure_values) {
            text = text + "\n\n" + LocaleController.getString("TurnPasswordOffPassport", R.string.TurnPasswordOffPassport);
        }
        builder.setMessage(text);
        builder.setTitle(LocaleController.getString("TurnPasswordOffQuestionTitle", R.string.TurnPasswordOffQuestionTitle));
        builder.setPositiveButton(LocaleController.getString("Disable", R.string.Disable), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda32
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                PaymentFormActivity.this.m4122lambda$createView$29$orgtelegramuiPaymentFormActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        AlertDialog alertDialog = builder.create();
        showDialog(alertDialog);
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(getThemedColor(Theme.key_dialogTextRed2));
        }
    }

    /* renamed from: lambda$createView$29$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4122lambda$createView$29$orgtelegramuiPaymentFormActivity(DialogInterface dialogInterface, int i) {
        sendSavePassword(true);
    }

    /* renamed from: lambda$createView$31$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ boolean m4125lambda$createView$31$orgtelegramuiPaymentFormActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 6) {
            this.doneItem.performClick();
            return true;
        } else if (i == 5) {
            int num = ((Integer) textView.getTag()).intValue();
            if (num == 0) {
                this.inputFields[1].requestFocus();
                return false;
            } else if (num == 1) {
                this.inputFields[2].requestFocus();
                return false;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void setAddressFields(TLRPC.TL_paymentRequestedInfo info) {
        boolean z = true;
        int i = 0;
        if (info.shipping_address != null) {
            String address = String.format("%s %s, %s, %s, %s, %s", info.shipping_address.street_line1, info.shipping_address.street_line2, info.shipping_address.city, info.shipping_address.state, info.shipping_address.country_iso2, info.shipping_address.post_code);
            this.detailSettingsCell[2].setTextAndValueAndIcon(address, LocaleController.getString("PaymentShippingAddress", R.string.PaymentShippingAddress), R.drawable.msg_payment_address, true);
        }
        this.detailSettingsCell[2].setVisibility(info.shipping_address != null ? 0 : 8);
        if (info.name != null) {
            this.detailSettingsCell[3].setTextAndValueAndIcon(info.name, LocaleController.getString("PaymentCheckoutName", R.string.PaymentCheckoutName), R.drawable.msg_contacts, true);
        }
        this.detailSettingsCell[3].setVisibility(info.name != null ? 0 : 8);
        if (info.phone != null) {
            this.detailSettingsCell[4].setTextAndValueAndIcon(PhoneFormat.getInstance().format(info.phone), LocaleController.getString("PaymentCheckoutPhoneNumber", R.string.PaymentCheckoutPhoneNumber), R.drawable.msg_calls, (info.email == null && this.shippingOption == null) ? false : true);
        }
        this.detailSettingsCell[4].setVisibility(info.phone != null ? 0 : 8);
        if (info.email != null) {
            TextDetailSettingsCell textDetailSettingsCell = this.detailSettingsCell[5];
            String str = info.email;
            String string = LocaleController.getString("PaymentCheckoutEmail", R.string.PaymentCheckoutEmail);
            if (this.shippingOption == null) {
                z = false;
            }
            textDetailSettingsCell.setTextAndValueAndIcon(str, string, R.drawable.msg_mention, z);
        }
        TextDetailSettingsCell textDetailSettingsCell2 = this.detailSettingsCell[5];
        if (info.email == null) {
            i = 8;
        }
        textDetailSettingsCell2.setVisibility(i);
    }

    public void updateTotalPrice() {
        this.totalPrice[0] = getTotalPriceString(this.prices);
        this.totalCell.setTextAndValue(LocaleController.getString("PaymentTransactionTotal", R.string.PaymentTransactionTotal), this.totalPrice[0], true);
        TextView textView = this.payTextView;
        if (textView != null) {
            textView.setText(LocaleController.formatString("PaymentCheckoutPay", R.string.PaymentCheckoutPay, this.totalPrice[0]));
        }
        if (this.tipLayout != null) {
            int color = getThemedColor(Theme.key_contacts_inviteBackground);
            int N2 = this.tipLayout.getChildCount();
            for (int b = 0; b < N2; b++) {
                TextView child = (TextView) this.tipLayout.getChildAt(b);
                if (child.getTag().equals(this.tipAmount)) {
                    Theme.setDrawableColor(child.getBackground(), color);
                    child.setTextColor(getThemedColor(Theme.key_contacts_inviteText));
                } else {
                    Theme.setDrawableColor(child.getBackground(), 536870911 & color);
                    child.setTextColor(getThemedColor(Theme.key_chats_secretName));
                }
                child.invalidate();
            }
        }
    }

    private void createGooglePayButton(Context context) {
        FrameLayout frameLayout = new FrameLayout(context);
        this.googlePayContainer = frameLayout;
        frameLayout.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.googlePayContainer.setVisibility(8);
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.googlePayButton = frameLayout2;
        frameLayout2.setClickable(true);
        this.googlePayButton.setFocusable(true);
        this.googlePayButton.setBackgroundResource(R.drawable.googlepay_button_no_shadow_background);
        if (this.googlePayPublicKey == null) {
            this.googlePayButton.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(2.0f));
        } else {
            this.googlePayButton.setPadding(AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
        }
        this.googlePayContainer.addView(this.googlePayButton, LayoutHelper.createFrame(-1, 48.0f));
        this.googlePayButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda60
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PaymentFormActivity.this.m4102x557681fa(view);
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setWeightSum(2.0f);
        linearLayout.setGravity(16);
        linearLayout.setOrientation(1);
        linearLayout.setDuplicateParentStateEnabled(true);
        this.googlePayButton.addView(linearLayout, LayoutHelper.createFrame(-1, -1.0f));
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setDuplicateParentStateEnabled(true);
        imageView.setImageResource(R.drawable.buy_with_googlepay_button_content);
        linearLayout.addView(imageView, LayoutHelper.createLinear(-1, 0, 1.0f));
        ImageView imageView2 = new ImageView(context);
        imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView2.setDuplicateParentStateEnabled(true);
        imageView2.setImageResource(R.drawable.googlepay_button_overlay);
        this.googlePayButton.addView(imageView2, LayoutHelper.createFrame(-1, -1.0f));
    }

    /* renamed from: lambda$createGooglePayButton$32$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4102x557681fa(View v) {
        this.googlePayButton.setClickable(false);
        try {
            JSONObject paymentDataRequest = getBaseRequest();
            JSONObject cardPaymentMethod = getBaseCardPaymentMethod();
            if (this.googlePayPublicKey != null && this.googlePayParameters == null) {
                cardPaymentMethod.put("tokenizationSpecification", new JSONObject() { // from class: org.telegram.ui.PaymentFormActivity.22
                    {
                        PaymentFormActivity.this = this;
                        put(CommonProperties.TYPE, "DIRECT");
                        put("parameters", new JSONObject() { // from class: org.telegram.ui.PaymentFormActivity.22.1
                            {
                                AnonymousClass22.this = this;
                                put("protocolVersion", "ECv2");
                                put("publicKey", PaymentFormActivity.this.googlePayPublicKey);
                            }
                        });
                    }
                });
            } else {
                cardPaymentMethod.put("tokenizationSpecification", new JSONObject() { // from class: org.telegram.ui.PaymentFormActivity.23
                    {
                        PaymentFormActivity.this = this;
                        put(CommonProperties.TYPE, "PAYMENT_GATEWAY");
                        if (this.googlePayParameters != null) {
                            put("parameters", this.googlePayParameters);
                        } else {
                            put("parameters", new JSONObject() { // from class: org.telegram.ui.PaymentFormActivity.23.1
                                {
                                    AnonymousClass23.this = this;
                                    put("gateway", "stripe");
                                    put("stripe:publishableKey", PaymentFormActivity.this.providerApiKey);
                                    put("stripe:version", StripeApiHandler.VERSION);
                                }
                            });
                        }
                    }
                });
            }
            paymentDataRequest.put("allowedPaymentMethods", new JSONArray().put(cardPaymentMethod));
            JSONObject transactionInfo = new JSONObject();
            ArrayList<TLRPC.TL_labeledPrice> arrayList = new ArrayList<>(this.paymentForm.invoice.prices);
            TLRPC.TL_shippingOption tL_shippingOption = this.shippingOption;
            if (tL_shippingOption != null) {
                arrayList.addAll(tL_shippingOption.prices);
            }
            String totalPriceDecimalString = getTotalPriceDecimalString(arrayList);
            this.totalPriceDecimal = totalPriceDecimalString;
            transactionInfo.put("totalPrice", totalPriceDecimalString);
            transactionInfo.put("totalPriceStatus", "FINAL");
            if (!TextUtils.isEmpty(this.googlePayCountryCode)) {
                transactionInfo.put(RemoteConfigConstants.RequestFieldKey.COUNTRY_CODE, this.googlePayCountryCode);
            }
            transactionInfo.put("currencyCode", this.paymentForm.invoice.currency);
            transactionInfo.put("checkoutOption", "COMPLETE_IMMEDIATE_PURCHASE");
            paymentDataRequest.put("transactionInfo", transactionInfo);
            paymentDataRequest.put("merchantInfo", new JSONObject().put("merchantName", this.currentBotName));
            PaymentDataRequest request = PaymentDataRequest.fromJson(paymentDataRequest.toString());
            if (request != null) {
                AutoResolveHelper.resolveTask(this.paymentsClient.loadPaymentData(request), getParentActivity(), LOAD_PAYMENT_DATA_REQUEST_CODE);
            }
        } catch (JSONException e) {
            FileLog.e(e);
        }
    }

    private void updatePasswordFields() {
        if (this.currentStep != 6 || this.bottomCell[2] == null) {
            return;
        }
        this.doneItem.setVisibility(0);
        if (this.currentPassword == null) {
            showEditDoneProgress(true, true);
            this.bottomCell[2].setVisibility(8);
            this.settingsCell[0].setVisibility(8);
            this.settingsCell[1].setVisibility(8);
            this.codeFieldCell.setVisibility(8);
            this.headerCell[0].setVisibility(8);
            this.headerCell[1].setVisibility(8);
            this.bottomCell[0].setVisibility(8);
            for (int a = 0; a < 3; a++) {
                ((View) this.inputFields[a].getParent()).setVisibility(8);
            }
            for (int a2 = 0; a2 < this.dividers.size(); a2++) {
                this.dividers.get(a2).setVisibility(8);
            }
            return;
        }
        showEditDoneProgress(true, false);
        if (this.waitingForEmail) {
            TextInfoPrivacyCell textInfoPrivacyCell = this.bottomCell[2];
            Object[] objArr = new Object[1];
            objArr[0] = this.currentPassword.email_unconfirmed_pattern != null ? this.currentPassword.email_unconfirmed_pattern : "";
            textInfoPrivacyCell.setText(LocaleController.formatString("EmailPasswordConfirmText2", R.string.EmailPasswordConfirmText2, objArr));
            this.bottomCell[2].setVisibility(0);
            this.settingsCell[0].setVisibility(0);
            this.settingsCell[1].setVisibility(0);
            this.codeFieldCell.setVisibility(0);
            this.bottomCell[1].setText("");
            this.headerCell[0].setVisibility(8);
            this.headerCell[1].setVisibility(8);
            this.bottomCell[0].setVisibility(8);
            for (int a3 = 0; a3 < 3; a3++) {
                ((View) this.inputFields[a3].getParent()).setVisibility(8);
            }
            for (int a4 = 0; a4 < this.dividers.size(); a4++) {
                this.dividers.get(a4).setVisibility(8);
            }
            return;
        }
        this.bottomCell[2].setVisibility(8);
        this.settingsCell[0].setVisibility(8);
        this.settingsCell[1].setVisibility(8);
        this.bottomCell[1].setText(LocaleController.getString("PaymentPasswordEmailInfo", R.string.PaymentPasswordEmailInfo));
        this.codeFieldCell.setVisibility(8);
        this.headerCell[0].setVisibility(0);
        this.headerCell[1].setVisibility(0);
        this.bottomCell[0].setVisibility(0);
        for (int a5 = 0; a5 < 3; a5++) {
            ((View) this.inputFields[a5].getParent()).setVisibility(0);
        }
        for (int a6 = 0; a6 < this.dividers.size(); a6++) {
            this.dividers.get(a6).setVisibility(0);
        }
    }

    private void loadPasswordInfo() {
        if (this.loadingPasswordInfo) {
            return;
        }
        this.loadingPasswordInfo = true;
        TLRPC.TL_account_getPassword req = new TLRPC.TL_account_getPassword();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda46
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PaymentFormActivity.this.m4135lambda$loadPasswordInfo$35$orgtelegramuiPaymentFormActivity(tLObject, tL_error);
            }
        }, 10);
    }

    /* renamed from: lambda$loadPasswordInfo$35$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4135lambda$loadPasswordInfo$35$orgtelegramuiPaymentFormActivity(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda36
            @Override // java.lang.Runnable
            public final void run() {
                PaymentFormActivity.this.m4134lambda$loadPasswordInfo$34$orgtelegramuiPaymentFormActivity(error, response);
            }
        });
    }

    /* renamed from: lambda$loadPasswordInfo$34$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4134lambda$loadPasswordInfo$34$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error, TLObject response) {
        this.loadingPasswordInfo = false;
        if (error == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response;
            this.currentPassword = tL_account_password;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(tL_account_password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                return;
            }
            if (this.paymentForm != null && this.currentPassword.has_password) {
                this.paymentForm.password_missing = false;
                this.paymentForm.can_save_credentials = true;
                updateSavePaymentField();
            }
            TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
            PaymentFormActivity paymentFormActivity = this.passwordFragment;
            if (paymentFormActivity != null) {
                paymentFormActivity.setCurrentPassword(this.currentPassword);
            }
            if (!this.currentPassword.has_password && this.shortPollRunnable == null) {
                Runnable runnable = new Runnable() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda25
                    @Override // java.lang.Runnable
                    public final void run() {
                        PaymentFormActivity.this.m4133lambda$loadPasswordInfo$33$orgtelegramuiPaymentFormActivity();
                    }
                };
                this.shortPollRunnable = runnable;
                AndroidUtilities.runOnUIThread(runnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            }
        }
    }

    /* renamed from: lambda$loadPasswordInfo$33$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4133lambda$loadPasswordInfo$33$orgtelegramuiPaymentFormActivity() {
        if (this.shortPollRunnable == null) {
            return;
        }
        loadPasswordInfo();
        this.shortPollRunnable = null;
    }

    private void showAlertWithText(String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        builder.setTitle(title);
        builder.setMessage(text);
        showDialog(builder.create());
    }

    private void showPayAlert(String totalPrice) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("PaymentTransactionReview", R.string.PaymentTransactionReview));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("PaymentTransactionMessage2", R.string.PaymentTransactionMessage2, totalPrice, this.currentBotName, this.currentItemName)));
        builder.setPositiveButton(LocaleController.getString("Continue", R.string.Continue), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda43
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                PaymentFormActivity.this.m4156lambda$showPayAlert$36$orgtelegramuiPaymentFormActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    /* renamed from: lambda$showPayAlert$36$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4156lambda$showPayAlert$36$orgtelegramuiPaymentFormActivity(DialogInterface dialogInterface, int i) {
        setDonePressed(true);
        sendData();
    }

    private JSONObject getBaseRequest() throws JSONException {
        return new JSONObject().put("apiVersion", 2).put("apiVersionMinor", 0);
    }

    private JSONObject getBaseCardPaymentMethod() throws JSONException {
        List<String> SUPPORTED_NETWORKS = Arrays.asList("AMEX", "DISCOVER", Card.JCB, "MASTERCARD", "VISA");
        List<String> SUPPORTED_METHODS = Arrays.asList("PAN_ONLY", "CRYPTOGRAM_3DS");
        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put(CommonProperties.TYPE, "CARD");
        JSONObject parameters = new JSONObject();
        parameters.put("allowedAuthMethods", new JSONArray((Collection) SUPPORTED_METHODS));
        parameters.put("allowedCardNetworks", new JSONArray((Collection) SUPPORTED_NETWORKS));
        cardPaymentMethod.put("parameters", parameters);
        return cardPaymentMethod;
    }

    public Optional<JSONObject> getIsReadyToPayRequest() {
        try {
            JSONObject isReadyToPayRequest = getBaseRequest();
            isReadyToPayRequest.put("allowedPaymentMethods", new JSONArray().put(getBaseCardPaymentMethod()));
            return Optional.of(isReadyToPayRequest);
        } catch (JSONException e) {
            return Optional.empty();
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Optional != java.util.Optional<org.json.JSONObject> */
    private void initGooglePay(Context context) {
        IsReadyToPayRequest request;
        if (Build.VERSION.SDK_INT < 19 || getParentActivity() == null) {
            return;
        }
        Wallet.WalletOptions walletOptions = new Wallet.WalletOptions.Builder().setEnvironment(this.paymentForm.invoice.test ? 3 : 1).setTheme(1).build();
        this.paymentsClient = Wallet.getPaymentsClient(context, walletOptions);
        Optional<JSONObject> isReadyToPayRequest = getIsReadyToPayRequest();
        if (!isReadyToPayRequest.isPresent() || (request = IsReadyToPayRequest.fromJson(isReadyToPayRequest.get().toString())) == null) {
            return;
        }
        Task<Boolean> task = this.paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(getParentActivity(), new OnCompleteListener() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda24
            @Override // com.google.android.gms.tasks.OnCompleteListener
            public final void onComplete(Task task2) {
                PaymentFormActivity.this.m4132lambda$initGooglePay$37$orgtelegramuiPaymentFormActivity(task2);
            }
        });
    }

    /* renamed from: lambda$initGooglePay$37$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4132lambda$initGooglePay$37$orgtelegramuiPaymentFormActivity(Task task1) {
        if (task1.isSuccessful()) {
            FrameLayout frameLayout = this.googlePayContainer;
            if (frameLayout != null) {
                frameLayout.setVisibility(0);
                return;
            }
            return;
        }
        FileLog.e("isReadyToPay failed", task1.getException());
    }

    private String getTotalPriceString(ArrayList<TLRPC.TL_labeledPrice> prices) {
        long amount = 0;
        for (int a = 0; a < prices.size(); a++) {
            amount += prices.get(a).amount;
        }
        Long l = this.tipAmount;
        if (l != null) {
            amount += l.longValue();
        }
        return LocaleController.getInstance().formatCurrencyString(amount, this.paymentForm.invoice.currency);
    }

    private String getTotalPriceDecimalString(ArrayList<TLRPC.TL_labeledPrice> prices) {
        long amount = 0;
        for (int a = 0; a < prices.size(); a++) {
            amount += prices.get(a).amount;
        }
        return LocaleController.getInstance().formatCurrencyDecimalString(amount, this.paymentForm.invoice.currency, false);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.twoStepPasswordChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didRemoveTwoStepPassword);
        if (this.currentStep != 4 || this.isCheckoutPreview) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.paymentFinished);
        }
        return super.onFragmentCreate();
    }

    public int getOtherSameFragmentDiff() {
        if (this.parentLayout == null || this.parentLayout.fragmentsStack == null) {
            return 0;
        }
        int cur = this.parentLayout.fragmentsStack.indexOf(this);
        if (cur == -1) {
            cur = this.parentLayout.fragmentsStack.size();
        }
        int i = cur;
        int a = 0;
        while (true) {
            if (a >= this.parentLayout.fragmentsStack.size()) {
                break;
            }
            BaseFragment fragment = this.parentLayout.fragmentsStack.get(a);
            if (!(fragment instanceof PaymentFormActivity)) {
                a++;
            } else {
                i = a;
                break;
            }
        }
        int a2 = i - cur;
        return a2;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        PaymentFormActivityDelegate paymentFormActivityDelegate = this.delegate;
        if (paymentFormActivityDelegate != null) {
            paymentFormActivityDelegate.onFragmentDestroyed();
        }
        if (!this.paymentStatusSent && this.paymentFormCallback != null && getOtherSameFragmentDiff() == 0) {
            this.paymentFormCallback.onInvoiceStatusChanged(InvoiceStatus.CANCELLED);
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.twoStepPasswordChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didRemoveTwoStepPassword);
        if (this.currentStep != 4 || this.isCheckoutPreview) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.paymentFinished);
        }
        WebView webView = this.webView;
        if (webView != null) {
            try {
                ViewParent parent = webView.getParent();
                if (parent != null) {
                    ((ViewGroup) parent).removeView(this.webView);
                }
                this.webView.stopLoading();
                this.webView.loadUrl("about:blank");
                this.webViewUrl = null;
                this.webView.destroy();
                this.webView = null;
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        try {
            int i = this.currentStep;
            if ((i == 2 || i == 6) && Build.VERSION.SDK_INT >= 23 && (SharedConfig.passcodeHash.length() == 0 || SharedConfig.allowScreenCapture)) {
                getParentActivity().getWindow().clearFlags(8192);
            }
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
        super.onFragmentDestroy();
        this.canceled = true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyVisible() {
        super.onBecomeFullyVisible();
        if (this.currentStep == 4 && this.needPayAfterTransition) {
            this.needPayAfterTransition = false;
            this.bottomLayout.callOnClick();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !backward) {
            WebView webView = this.webView;
            if (webView != null) {
                if (this.currentStep != 4) {
                    String str = this.paymentForm.url;
                    this.webViewUrl = str;
                    webView.loadUrl(str);
                    return;
                }
                return;
            }
            int i = this.currentStep;
            if (i == 2) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda26
                    @Override // java.lang.Runnable
                    public final void run() {
                        PaymentFormActivity.this.m4137xc6803560();
                    }
                }, 100L);
            } else if (i == 3) {
                this.inputFields[1].requestFocus();
                AndroidUtilities.showKeyboard(this.inputFields[1]);
            } else if (i == 4) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                if (editTextBoldCursorArr != null) {
                    editTextBoldCursorArr[0].requestFocus();
                }
            } else if (i == 6 && !this.waitingForEmail) {
                this.inputFields[0].requestFocus();
                AndroidUtilities.showKeyboard(this.inputFields[0]);
            }
        }
    }

    /* renamed from: lambda$onTransitionAnimationEnd$38$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4137xc6803560() {
        this.inputFields[0].requestFocus();
        AndroidUtilities.showKeyboard(this.inputFields[0]);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.twoStepPasswordChanged) {
            this.paymentForm.password_missing = false;
            this.paymentForm.can_save_credentials = true;
            updateSavePaymentField();
        } else if (id == NotificationCenter.didRemoveTwoStepPassword) {
            this.paymentForm.password_missing = true;
            this.paymentForm.can_save_credentials = false;
            updateSavePaymentField();
        } else if (id == NotificationCenter.paymentFinished) {
            this.paymentStatusSent = true;
            removeSelfFromStack();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onActivityResultFragment(int requestCode, final int resultCode, final Intent data) {
        if (requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda27
                @Override // java.lang.Runnable
                public final void run() {
                    PaymentFormActivity.this.m4136xb241c511(resultCode, data);
                }
            });
        }
    }

    /* renamed from: lambda$onActivityResultFragment$39$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4136xb241c511(int resultCode, Intent data) {
        String paymentInfo;
        if (resultCode == -1) {
            PaymentData paymentData = PaymentData.getFromIntent(data);
            if (paymentData == null || (paymentInfo = paymentData.toJson()) == null) {
                return;
            }
            try {
                JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");
                JSONObject tokenizationData = paymentMethodData.getJSONObject("tokenizationData");
                tokenizationData.getString(CommonProperties.TYPE);
                String token = tokenizationData.getString("token");
                if (this.googlePayPublicKey == null && this.googlePayParameters == null) {
                    Token t = TokenParser.parseToken(token);
                    this.paymentJson = String.format(Locale.US, "{\"type\":\"%1$s\", \"id\":\"%2$s\"}", t.getType(), t.getId());
                    Card card = t.getCard();
                    this.cardName = card.getType() + " *" + card.getLast4();
                    goToNextStep();
                }
                TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay = new TLRPC.TL_inputPaymentCredentialsGooglePay();
                this.googlePayCredentials = tL_inputPaymentCredentialsGooglePay;
                tL_inputPaymentCredentialsGooglePay.payment_token = new TLRPC.TL_dataJSON();
                this.googlePayCredentials.payment_token.data = tokenizationData.toString();
                String descriptions = paymentMethodData.optString("description");
                if (!TextUtils.isEmpty(descriptions)) {
                    this.cardName = descriptions;
                } else {
                    this.cardName = "Android Pay";
                }
                goToNextStep();
            } catch (JSONException e) {
                FileLog.e(e);
            }
        } else if (resultCode == 1) {
            Status status = AutoResolveHelper.getStatusFromIntent(data);
            StringBuilder sb = new StringBuilder();
            sb.append("android pay error ");
            sb.append(status != null ? status.getStatusMessage() : "");
            FileLog.e(sb.toString());
        }
        showEditDoneProgress(true, false);
        setDonePressed(false);
        this.googlePayButton.setClickable(true);
    }

    public void goToNextStep() {
        int nextStep;
        int nextStep2;
        int nextStep3;
        switch (this.currentStep) {
            case 0:
                PaymentFormActivityDelegate paymentFormActivityDelegate = this.delegate;
                if (paymentFormActivityDelegate != null) {
                    paymentFormActivityDelegate.didSelectNewAddress(this.validateRequest);
                    finishFragment();
                    return;
                }
                if (this.paymentForm.invoice.flexible) {
                    nextStep = 1;
                } else if (this.paymentForm.saved_credentials != null) {
                    if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                        UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                        UserConfig.getInstance(this.currentAccount).saveConfig(false);
                    }
                    if (UserConfig.getInstance(this.currentAccount).tmpPassword != null) {
                        nextStep = 4;
                    } else {
                        nextStep = 3;
                    }
                } else {
                    nextStep = 2;
                }
                presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, nextStep, this.requestedInfo, null, null, null, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment), this.isWebView);
                return;
            case 1:
                if (this.paymentForm.saved_credentials != null) {
                    if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                        UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                        UserConfig.getInstance(this.currentAccount).saveConfig(false);
                    }
                    if (UserConfig.getInstance(this.currentAccount).tmpPassword != null) {
                        nextStep2 = 4;
                    } else {
                        nextStep2 = 3;
                    }
                } else {
                    nextStep2 = 2;
                }
                presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, nextStep2, this.requestedInfo, this.shippingOption, this.tipAmount, null, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment), this.isWebView);
                return;
            case 2:
                if (this.paymentForm.password_missing && this.saveCardInfo) {
                    PaymentFormActivity paymentFormActivity = new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 6, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment);
                    this.passwordFragment = paymentFormActivity;
                    paymentFormActivity.setCurrentPassword(this.currentPassword);
                    this.passwordFragment.setDelegate(new PaymentFormActivityDelegate() { // from class: org.telegram.ui.PaymentFormActivity.24
                        @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
                        public /* synthetic */ void didSelectNewAddress(TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo) {
                            PaymentFormActivityDelegate.CC.$default$didSelectNewAddress(this, tL_payments_validateRequestedInfo);
                        }

                        @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
                        public boolean didSelectNewCard(String tokenJson, String card, boolean saveCard, TLRPC.TL_inputPaymentCredentialsGooglePay googlePay) {
                            if (PaymentFormActivity.this.delegate != null) {
                                PaymentFormActivity.this.delegate.didSelectNewCard(tokenJson, card, saveCard, googlePay);
                            }
                            if (PaymentFormActivity.this.isWebView) {
                                PaymentFormActivity.this.removeSelfFromStack();
                            }
                            return PaymentFormActivity.this.delegate != null;
                        }

                        @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
                        public void onFragmentDestroyed() {
                            PaymentFormActivity.this.passwordFragment = null;
                        }

                        @Override // org.telegram.ui.PaymentFormActivity.PaymentFormActivityDelegate
                        public void currentPasswordUpdated(TLRPC.TL_account_password password) {
                            PaymentFormActivity.this.currentPassword = password;
                        }
                    });
                    presentFragment(this.passwordFragment, this.isWebView);
                    return;
                }
                PaymentFormActivityDelegate paymentFormActivityDelegate2 = this.delegate;
                if (paymentFormActivityDelegate2 != null) {
                    paymentFormActivityDelegate2.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.googlePayCredentials);
                    finishFragment();
                    return;
                }
                presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 4, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment), this.isWebView);
                return;
            case 3:
                if (this.passwordOk) {
                    nextStep3 = 4;
                } else {
                    nextStep3 = 2;
                }
                presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, nextStep3, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment), true);
                return;
            case 4:
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.paymentFinished, new Object[0]);
                if ((this.botUser.username != null && this.botUser.username.equalsIgnoreCase(getMessagesController().premiumBotUsername)) || (this.invoiceSlug != null && getMessagesController().premiumInvoiceSlug != null && ColorUtils$$ExternalSyntheticBackport0.m(this.invoiceSlug, getMessagesController().premiumInvoiceSlug))) {
                    Iterator it = new ArrayList(getParentLayout().fragmentsStack).iterator();
                    while (it.hasNext()) {
                        BaseFragment fragment = (BaseFragment) it.next();
                        if ((fragment instanceof ChatActivity) || (fragment instanceof PremiumPreviewFragment)) {
                            fragment.removeSelfFromStack();
                        }
                    }
                    presentFragment(new PremiumPreviewFragment(null).setForcePremium(), true);
                    if (getParentActivity() instanceof LaunchActivity) {
                        try {
                            this.fragmentView.performHapticFeedback(3, 2);
                        } catch (Exception e) {
                        }
                        ((LaunchActivity) getParentActivity()).getFireworksOverlay().start();
                        return;
                    }
                    return;
                }
                finishFragment();
                return;
            case 5:
            default:
                return;
            case 6:
                if (!this.delegate.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.googlePayCredentials)) {
                    presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 4, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment), true);
                    return;
                } else {
                    finishFragment();
                    return;
                }
        }
    }

    public void updateSavePaymentField() {
        if (this.bottomCell[0] == null || this.sectionCell[2] == null) {
            return;
        }
        if ((this.paymentForm.password_missing || this.paymentForm.can_save_credentials) && (this.webView == null || !this.webviewLoading)) {
            SpannableStringBuilder text = new SpannableStringBuilder(LocaleController.getString("PaymentCardSavePaymentInformationInfoLine1", R.string.PaymentCardSavePaymentInformationInfoLine1));
            if (this.paymentForm.password_missing) {
                loadPasswordInfo();
                text.append((CharSequence) "\n");
                int len = text.length();
                String str2 = LocaleController.getString("PaymentCardSavePaymentInformationInfoLine2", R.string.PaymentCardSavePaymentInformationInfoLine2);
                int index1 = str2.indexOf(42);
                int index2 = str2.lastIndexOf(42);
                text.append((CharSequence) str2);
                if (index1 != -1 && index2 != -1) {
                    int index12 = index1 + len;
                    int index22 = index2 + len;
                    this.bottomCell[0].getTextView().setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                    text.replace(index22, index22 + 1, (CharSequence) "");
                    text.replace(index12, index12 + 1, (CharSequence) "");
                    text.setSpan(new LinkSpan(), index12, index22 - 1, 33);
                }
            }
            this.checkCell1.setEnabled(true);
            this.bottomCell[0].setText(text);
            this.checkCell1.setVisibility(0);
            this.bottomCell[0].setVisibility(0);
            ShadowSectionCell[] shadowSectionCellArr = this.sectionCell;
            shadowSectionCellArr[2].setBackgroundDrawable(Theme.getThemedDrawable(shadowSectionCellArr[2].getContext(), (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            return;
        }
        this.checkCell1.setVisibility(8);
        this.bottomCell[0].setVisibility(8);
        ShadowSectionCell[] shadowSectionCellArr2 = this.sectionCell;
        shadowSectionCellArr2[2].setBackgroundDrawable(Theme.getThemedDrawable(shadowSectionCellArr2[2].getContext(), (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
    }

    public void fillNumber(String number) {
        try {
            TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            boolean allowCall = true;
            if (number != null || (tm.getSimState() != 1 && tm.getPhoneType() != 0)) {
                if (Build.VERSION.SDK_INT >= 23) {
                    allowCall = getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                }
                if (number != null || allowCall) {
                    if (number == null) {
                        number = PhoneFormat.stripExceptNumbers(tm.getLine1Number());
                    }
                    String textToSet = null;
                    boolean ok = false;
                    if (!TextUtils.isEmpty(number)) {
                        if (number.length() > 4) {
                            int a = 4;
                            while (true) {
                                if (a < 1) {
                                    break;
                                }
                                String sub = number.substring(0, a);
                                String country = this.codesMap.get(sub);
                                if (country == null) {
                                    a--;
                                } else {
                                    ok = true;
                                    textToSet = number.substring(a);
                                    this.inputFields[8].setText(sub);
                                    break;
                                }
                            }
                            if (!ok) {
                                textToSet = number.substring(1);
                                this.inputFields[8].setText(number.substring(0, 1));
                            }
                        }
                        if (textToSet != null) {
                            this.inputFields[9].setText(textToSet);
                            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                            editTextBoldCursorArr[9].setSelection(editTextBoldCursorArr[9].length());
                        }
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void sendSavePassword(final boolean clear) {
        String firstPassword;
        String email;
        if (!clear && this.codeFieldCell.getVisibility() == 0) {
            String code = this.codeFieldCell.getText();
            if (code.length() == 0) {
                shakeView(this.codeFieldCell);
                return;
            }
            showEditDoneProgress(true, true);
            TLRPC.TL_account_confirmPasswordEmail req = new TLRPC.TL_account_confirmPasswordEmail();
            req.code = code;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda47
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PaymentFormActivity.this.m4146lambda$sendSavePassword$41$orgtelegramuiPaymentFormActivity(tLObject, tL_error);
                }
            }, 10);
            return;
        }
        final TLRPC.TL_account_updatePasswordSettings req2 = new TLRPC.TL_account_updatePasswordSettings();
        if (clear) {
            this.doneItem.setVisibility(0);
            req2.new_settings = new TLRPC.TL_account_passwordInputSettings();
            req2.new_settings.flags = 2;
            req2.new_settings.email = "";
            req2.password = new TLRPC.TL_inputCheckPasswordEmpty();
            email = null;
            firstPassword = null;
        } else {
            String firstPassword2 = this.inputFields[0].getText().toString();
            if (!TextUtils.isEmpty(firstPassword2)) {
                String secondPassword = this.inputFields[1].getText().toString();
                if (!firstPassword2.equals(secondPassword)) {
                    try {
                        Toast.makeText(getParentActivity(), LocaleController.getString("PasswordDoNotMatch", R.string.PasswordDoNotMatch), 0).show();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    shakeField(1);
                    return;
                }
                String email2 = this.inputFields[2].getText().toString();
                if (email2.length() < 3) {
                    shakeField(2);
                    return;
                }
                int dot = email2.lastIndexOf(46);
                int dog = email2.lastIndexOf(64);
                if (dog < 0 || dot < dog) {
                    shakeField(2);
                    return;
                }
                req2.password = new TLRPC.TL_inputCheckPasswordEmpty();
                req2.new_settings = new TLRPC.TL_account_passwordInputSettings();
                req2.new_settings.flags |= 1;
                req2.new_settings.hint = "";
                req2.new_settings.new_algo = this.currentPassword.new_algo;
                TLRPC.TL_account_passwordInputSettings tL_account_passwordInputSettings = req2.new_settings;
                tL_account_passwordInputSettings.flags = 2 | tL_account_passwordInputSettings.flags;
                req2.new_settings.email = email2.trim();
                email = email2;
                firstPassword = firstPassword2;
            } else {
                shakeField(0);
                return;
            }
        }
        showEditDoneProgress(true, true);
        final String str = email;
        final String str2 = firstPassword;
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda44
            @Override // java.lang.Runnable
            public final void run() {
                PaymentFormActivity.this.m4152lambda$sendSavePassword$47$orgtelegramuiPaymentFormActivity(clear, str, str2, req2);
            }
        });
    }

    /* renamed from: lambda$sendSavePassword$41$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4146lambda$sendSavePassword$41$orgtelegramuiPaymentFormActivity(TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda35
            @Override // java.lang.Runnable
            public final void run() {
                PaymentFormActivity.this.m4145lambda$sendSavePassword$40$orgtelegramuiPaymentFormActivity(error);
            }
        });
    }

    /* renamed from: lambda$sendSavePassword$40$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4145lambda$sendSavePassword$40$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error) {
        String timeString;
        showEditDoneProgress(true, false);
        if (error == null) {
            if (getParentActivity() == null) {
                return;
            }
            Runnable runnable = this.shortPollRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.shortPollRunnable = null;
            }
            goToNextStep();
        } else if (error.text.startsWith("CODE_INVALID")) {
            shakeView(this.codeFieldCell);
            this.codeFieldCell.setText("", false);
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

    /* renamed from: lambda$sendSavePassword$46$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4151lambda$sendSavePassword$46$orgtelegramuiPaymentFormActivity(final boolean clear, final String email, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda42
            @Override // java.lang.Runnable
            public final void run() {
                PaymentFormActivity.this.m4150lambda$sendSavePassword$45$orgtelegramuiPaymentFormActivity(error, clear, response, email);
            }
        });
    }

    /* renamed from: lambda$sendSavePassword$47$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4152lambda$sendSavePassword$47$orgtelegramuiPaymentFormActivity(final boolean clear, final String email, String firstPassword, TLRPC.TL_account_updatePasswordSettings req) {
        RequestDelegate requestDelegate = new RequestDelegate() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda55
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PaymentFormActivity.this.m4151lambda$sendSavePassword$46$orgtelegramuiPaymentFormActivity(clear, email, tLObject, tL_error);
            }
        };
        if (clear) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate, 10);
            return;
        }
        byte[] newPasswordBytes = AndroidUtilities.getStringBytes(firstPassword);
        if (this.currentPassword.new_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.new_algo;
            req.new_settings.new_password_hash = SRPHelper.getVBytes(newPasswordBytes, algo);
            if (req.new_settings.new_password_hash == null) {
                TLRPC.TL_error error = new TLRPC.TL_error();
                error.text = "ALGO_INVALID";
                requestDelegate.run(null, error);
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate, 10);
            return;
        }
        TLRPC.TL_error error2 = new TLRPC.TL_error();
        error2.text = "PASSWORD_HASH_INVALID";
        requestDelegate.run(null, error2);
    }

    /* renamed from: lambda$sendSavePassword$45$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4150lambda$sendSavePassword$45$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error, final boolean clear, TLObject response, final String email) {
        String timeString;
        if (error != null && "SRP_ID_INVALID".equals(error.text)) {
            TLRPC.TL_account_getPassword getPasswordReq = new TLRPC.TL_account_getPassword();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(getPasswordReq, new RequestDelegate() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda54
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PaymentFormActivity.this.m4148lambda$sendSavePassword$43$orgtelegramuiPaymentFormActivity(clear, tLObject, tL_error);
                }
            }, 8);
            return;
        }
        showEditDoneProgress(true, false);
        if (clear) {
            this.currentPassword.has_password = false;
            this.currentPassword.current_algo = null;
            this.delegate.currentPasswordUpdated(this.currentPassword);
            finishFragment();
        } else if (error == null && (response instanceof TLRPC.TL_boolTrue)) {
            if (getParentActivity() == null) {
                return;
            }
            goToNextStep();
        } else if (error != null) {
            if (error.text.equals("EMAIL_UNCONFIRMED") || error.text.startsWith("EMAIL_UNCONFIRMED_")) {
                this.emailCodeLength = Utilities.parseInt((CharSequence) error.text).intValue();
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda53
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        PaymentFormActivity.this.m4149lambda$sendSavePassword$44$orgtelegramuiPaymentFormActivity(email, dialogInterface, i);
                    }
                });
                builder.setMessage(LocaleController.getString("YourEmailAlmostThereText", R.string.YourEmailAlmostThereText));
                builder.setTitle(LocaleController.getString("YourEmailAlmostThere", R.string.YourEmailAlmostThere));
                Dialog dialog = showDialog(builder.create());
                if (dialog != null) {
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                }
            } else if (error.text.equals("EMAIL_INVALID")) {
                showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("PasswordEmailInvalid", R.string.PasswordEmailInvalid));
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
    }

    /* renamed from: lambda$sendSavePassword$43$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4148lambda$sendSavePassword$43$orgtelegramuiPaymentFormActivity(final boolean clear, final TLObject response2, final TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda40
            @Override // java.lang.Runnable
            public final void run() {
                PaymentFormActivity.this.m4147lambda$sendSavePassword$42$orgtelegramuiPaymentFormActivity(error2, response2, clear);
            }
        });
    }

    /* renamed from: lambda$sendSavePassword$42$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4147lambda$sendSavePassword$42$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error2, TLObject response2, boolean clear) {
        if (error2 == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
            this.currentPassword = tL_account_password;
            TwoStepVerificationActivity.initPasswordNewAlgo(tL_account_password);
            sendSavePassword(clear);
        }
    }

    /* renamed from: lambda$sendSavePassword$44$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4149lambda$sendSavePassword$44$orgtelegramuiPaymentFormActivity(String email, DialogInterface dialogInterface, int i) {
        this.waitingForEmail = true;
        this.currentPassword.email_unconfirmed_pattern = email;
        updatePasswordFields();
    }

    public boolean sendCardData() {
        Integer year;
        Integer month;
        int i;
        String date = this.inputFields[1].getText().toString();
        String[] args = date.split("/");
        if (args.length == 2) {
            Integer month2 = Utilities.parseInt((CharSequence) args[0]);
            year = Utilities.parseInt((CharSequence) args[1]);
            month = month2;
        } else {
            year = null;
            month = null;
        }
        final Card card = new Card(this.inputFields[0].getText().toString(), month, year, this.inputFields[3].getText().toString(), this.inputFields[2].getText().toString(), null, null, null, null, this.inputFields[5].getText().toString(), this.inputFields[4].getText().toString(), null);
        this.cardName = card.getType() + " *" + card.getLast4();
        if (!card.validateNumber()) {
            shakeField(0);
            return false;
        }
        if (!card.validateExpMonth() || !card.validateExpYear()) {
            i = 1;
        } else if (card.validateExpiryDate()) {
            if (this.need_card_name && this.inputFields[2].length() == 0) {
                shakeField(2);
                return false;
            } else if (!card.validateCVC()) {
                shakeField(3);
                return false;
            } else if (this.need_card_country && this.inputFields[4].length() == 0) {
                shakeField(4);
                return false;
            } else if (!this.need_card_postcode || this.inputFields[5].length() != 0) {
                showEditDoneProgress(true, true);
                try {
                    if ("stripe".equals(this.paymentForm.native_provider)) {
                        Stripe stripe = new Stripe(this.providerApiKey);
                        stripe.createToken(card, new AnonymousClass25());
                    } else if ("smartglocal".equals(this.paymentForm.native_provider)) {
                        AsyncTask<Object, Object, String> task = new AsyncTask<Object, Object, String>() { // from class: org.telegram.ui.PaymentFormActivity.26
                            @Override // android.os.AsyncTask
                            public String doInBackground(Object... objects) {
                                int code;
                                HttpURLConnection conn = null;
                                try {
                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        JSONObject cardObject = new JSONObject();
                                        cardObject.put("number", card.getNumber());
                                        cardObject.put("expiration_month", String.format(Locale.US, "%02d", card.getExpMonth()));
                                        cardObject.put("expiration_year", "" + card.getExpYear());
                                        cardObject.put("security_code", "" + card.getCVC());
                                        jsonObject.put(Token.TYPE_CARD, cardObject);
                                        URL connectionUrl = PaymentFormActivity.this.paymentForm.invoice.test ? new URL("https://tgb-playground.smart-glocal.com/cds/v1/tokenize/card") : new URL("https://tgb.smart-glocal.com/cds/v1/tokenize/card");
                                        conn = (HttpURLConnection) connectionUrl.openConnection();
                                        conn.setConnectTimeout(Indexable.MAX_BYTE_SIZE);
                                        conn.setReadTimeout(80000);
                                        conn.setUseCaches(false);
                                        conn.setDoOutput(true);
                                        conn.setRequestMethod(DefaultHttpClient.METHOD_POST);
                                        conn.setRequestProperty(DefaultHttpClient.CONTENT_TYPE_KEY, "application/json");
                                        conn.setRequestProperty("X-PUBLIC-TOKEN", PaymentFormActivity.this.providerApiKey);
                                        OutputStream output = conn.getOutputStream();
                                        try {
                                            output.write(jsonObject.toString().getBytes("UTF-8"));
                                            if (output != null) {
                                                output.close();
                                            }
                                            code = conn.getResponseCode();
                                        } catch (Throwable th) {
                                            if (output != null) {
                                                try {
                                                    output.close();
                                                } catch (Throwable th2) {
                                                }
                                            }
                                            throw th;
                                        }
                                    } catch (Throwable th3) {
                                        if (0 != 0) {
                                            conn.disconnect();
                                        }
                                        throw th3;
                                    }
                                } catch (Exception e) {
                                    FileLog.e(e);
                                    if (0 == 0) {
                                        return null;
                                    }
                                }
                                if (code < 200 || code >= 300) {
                                    if (BuildVars.DEBUG_VERSION) {
                                        FileLog.e("" + PaymentFormActivity.getResponseBody(conn.getErrorStream()));
                                    }
                                    if (conn == null) {
                                        return null;
                                    }
                                    conn.disconnect();
                                    return null;
                                }
                                JSONObject result = new JSONObject();
                                JSONObject jsonObject1 = new JSONObject(PaymentFormActivity.getResponseBody(conn.getInputStream()));
                                String token = jsonObject1.getJSONObject("data").getString("token");
                                result.put("token", token);
                                result.put(CommonProperties.TYPE, Token.TYPE_CARD);
                                String jSONObject = result.toString();
                                if (conn != null) {
                                    conn.disconnect();
                                }
                                return jSONObject;
                            }

                            public void onPostExecute(String result) {
                                if (PaymentFormActivity.this.canceled) {
                                    return;
                                }
                                if (result != null) {
                                    PaymentFormActivity.this.paymentJson = result;
                                    PaymentFormActivity.this.goToNextStep();
                                } else {
                                    AlertsCreator.showSimpleToast(PaymentFormActivity.this, LocaleController.getString("PaymentConnectionFailed", R.string.PaymentConnectionFailed));
                                }
                                PaymentFormActivity.this.showEditDoneProgress(true, false);
                                PaymentFormActivity.this.setDonePressed(false);
                            }
                        };
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
                    }
                    return true;
                } catch (Exception e) {
                    FileLog.e(e);
                    return true;
                }
            } else {
                shakeField(5);
                return false;
            }
        } else {
            i = 1;
        }
        shakeField(i);
        return false;
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$25 */
    /* loaded from: classes4.dex */
    public class AnonymousClass25 implements TokenCallback {
        AnonymousClass25() {
            PaymentFormActivity.this = this$0;
        }

        @Override // com.stripe.android.TokenCallback
        public void onSuccess(Token token) {
            if (!PaymentFormActivity.this.canceled) {
                PaymentFormActivity.this.paymentJson = String.format(Locale.US, "{\"type\":\"%1$s\", \"id\":\"%2$s\"}", token.getType(), token.getId());
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PaymentFormActivity$25$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PaymentFormActivity.AnonymousClass25.this.m4157lambda$onSuccess$0$orgtelegramuiPaymentFormActivity$25();
                    }
                });
            }
        }

        /* renamed from: lambda$onSuccess$0$org-telegram-ui-PaymentFormActivity$25 */
        public /* synthetic */ void m4157lambda$onSuccess$0$orgtelegramuiPaymentFormActivity$25() {
            PaymentFormActivity.this.goToNextStep();
            PaymentFormActivity.this.showEditDoneProgress(true, false);
            PaymentFormActivity.this.setDonePressed(false);
        }

        @Override // com.stripe.android.TokenCallback
        public void onError(Exception error) {
            if (!PaymentFormActivity.this.canceled) {
                PaymentFormActivity.this.showEditDoneProgress(true, false);
                PaymentFormActivity.this.setDonePressed(false);
                if ((error instanceof APIConnectionException) || (error instanceof APIException)) {
                    AlertsCreator.showSimpleToast(PaymentFormActivity.this, LocaleController.getString("PaymentConnectionFailed", R.string.PaymentConnectionFailed));
                } else {
                    AlertsCreator.showSimpleToast(PaymentFormActivity.this, error.getMessage());
                }
            }
        }
    }

    public static String getResponseBody(InputStream responseStream) throws IOException {
        String rBody = new Scanner(responseStream, "UTF-8").useDelimiter("\\A").next();
        responseStream.close();
        return rBody;
    }

    private void sendSavedForm(final Runnable callback) {
        if (this.canceled) {
            return;
        }
        showEditDoneProgress(true, true);
        this.validateRequest = new TLRPC.TL_payments_validateRequestedInfo();
        if (this.messageObject != null) {
            TLRPC.TL_inputInvoiceMessage inputInvoice = new TLRPC.TL_inputInvoiceMessage();
            inputInvoice.peer = getMessagesController().getInputPeer(this.messageObject.messageOwner.peer_id);
            inputInvoice.msg_id = this.messageObject.getId();
            this.validateRequest.invoice = inputInvoice;
        } else {
            TLRPC.TL_inputInvoiceSlug inputInvoice2 = new TLRPC.TL_inputInvoiceSlug();
            inputInvoice2.slug = this.invoiceSlug;
            this.validateRequest.invoice = inputInvoice2;
        }
        this.validateRequest.save = true;
        this.validateRequest.info = this.paymentForm.saved_info;
        final TLObject req = this.validateRequest;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda48
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PaymentFormActivity.this.m4155lambda$sendSavedForm$50$orgtelegramuiPaymentFormActivity(callback, req, tLObject, tL_error);
            }
        }, 2);
    }

    /* renamed from: lambda$sendSavedForm$50$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4155lambda$sendSavedForm$50$orgtelegramuiPaymentFormActivity(final Runnable callback, final TLObject req, final TLObject response, final TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_payments_validatedRequestedInfo) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda31
                @Override // java.lang.Runnable
                public final void run() {
                    PaymentFormActivity.this.m4153lambda$sendSavedForm$48$orgtelegramuiPaymentFormActivity(response, callback);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda38
                @Override // java.lang.Runnable
                public final void run() {
                    PaymentFormActivity.this.m4154lambda$sendSavedForm$49$orgtelegramuiPaymentFormActivity(error, req);
                }
            });
        }
    }

    /* renamed from: lambda$sendSavedForm$48$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4153lambda$sendSavedForm$48$orgtelegramuiPaymentFormActivity(TLObject response, Runnable callback) {
        this.requestedInfo = (TLRPC.TL_payments_validatedRequestedInfo) response;
        callback.run();
        setDonePressed(false);
        showEditDoneProgress(true, false);
    }

    /* renamed from: lambda$sendSavedForm$49$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4154lambda$sendSavedForm$49$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error, TLObject req) {
        setDonePressed(false);
        showEditDoneProgress(true, false);
        if (error != null) {
            AlertsCreator.processError(this.currentAccount, error, this, req, new Object[0]);
        }
    }

    public void sendForm() {
        if (this.canceled) {
            return;
        }
        showEditDoneProgress(true, true);
        this.validateRequest = new TLRPC.TL_payments_validateRequestedInfo();
        if (this.messageObject != null) {
            TLRPC.TL_inputInvoiceMessage inputInvoice = new TLRPC.TL_inputInvoiceMessage();
            inputInvoice.peer = getMessagesController().getInputPeer(this.messageObject.messageOwner.peer_id);
            inputInvoice.msg_id = this.messageObject.getId();
            this.validateRequest.invoice = inputInvoice;
        } else {
            TLRPC.TL_inputInvoiceSlug inputInvoice2 = new TLRPC.TL_inputInvoiceSlug();
            inputInvoice2.slug = this.invoiceSlug;
            this.validateRequest.invoice = inputInvoice2;
        }
        this.validateRequest.save = this.saveShippingInfo;
        this.validateRequest.info = new TLRPC.TL_paymentRequestedInfo();
        if (this.paymentForm.invoice.name_requested) {
            this.validateRequest.info.name = this.inputFields[6].getText().toString();
            this.validateRequest.info.flags |= 1;
        }
        if (this.paymentForm.invoice.phone_requested) {
            this.validateRequest.info.phone = "+" + this.inputFields[8].getText().toString() + this.inputFields[9].getText().toString();
            TLRPC.TL_paymentRequestedInfo tL_paymentRequestedInfo = this.validateRequest.info;
            tL_paymentRequestedInfo.flags = tL_paymentRequestedInfo.flags | 2;
        }
        if (this.paymentForm.invoice.email_requested) {
            this.validateRequest.info.email = this.inputFields[7].getText().toString().trim();
            this.validateRequest.info.flags |= 4;
        }
        if (this.paymentForm.invoice.shipping_address_requested) {
            this.validateRequest.info.shipping_address = new TLRPC.TL_postAddress();
            this.validateRequest.info.shipping_address.street_line1 = this.inputFields[0].getText().toString();
            this.validateRequest.info.shipping_address.street_line2 = this.inputFields[1].getText().toString();
            this.validateRequest.info.shipping_address.city = this.inputFields[2].getText().toString();
            this.validateRequest.info.shipping_address.state = this.inputFields[3].getText().toString();
            TLRPC.TL_postAddress tL_postAddress = this.validateRequest.info.shipping_address;
            String str = this.countryName;
            if (str == null) {
                str = "";
            }
            tL_postAddress.country_iso2 = str;
            this.validateRequest.info.shipping_address.post_code = this.inputFields[5].getText().toString();
            this.validateRequest.info.flags |= 8;
        }
        final TLObject req = this.validateRequest;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(this.validateRequest, new RequestDelegate() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda50
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PaymentFormActivity.this.m4144lambda$sendForm$54$orgtelegramuiPaymentFormActivity(req, tLObject, tL_error);
            }
        }, 2);
    }

    /* renamed from: lambda$sendForm$54$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4144lambda$sendForm$54$orgtelegramuiPaymentFormActivity(final TLObject req, final TLObject response, final TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_payments_validatedRequestedInfo) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda30
                @Override // java.lang.Runnable
                public final void run() {
                    PaymentFormActivity.this.m4142lambda$sendForm$52$orgtelegramuiPaymentFormActivity(response);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda37
                @Override // java.lang.Runnable
                public final void run() {
                    PaymentFormActivity.this.m4143lambda$sendForm$53$orgtelegramuiPaymentFormActivity(error, req);
                }
            });
        }
    }

    /* renamed from: lambda$sendForm$52$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4142lambda$sendForm$52$orgtelegramuiPaymentFormActivity(TLObject response) {
        this.requestedInfo = (TLRPC.TL_payments_validatedRequestedInfo) response;
        if (this.paymentForm.saved_info != null && !this.saveShippingInfo) {
            TLRPC.TL_payments_clearSavedInfo req1 = new TLRPC.TL_payments_clearSavedInfo();
            req1.info = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req1, PaymentFormActivity$$ExternalSyntheticLambda57.INSTANCE);
        }
        goToNextStep();
        setDonePressed(false);
        showEditDoneProgress(true, false);
    }

    public static /* synthetic */ void lambda$sendForm$51(TLObject response1, TLRPC.TL_error error1) {
    }

    /* renamed from: lambda$sendForm$53$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4143lambda$sendForm$53$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error, TLObject req) {
        setDonePressed(false);
        showEditDoneProgress(true, false);
        if (error != null) {
            String str = error.text;
            char c = 65535;
            switch (str.hashCode()) {
                case -2092780146:
                    if (str.equals("ADDRESS_CITY_INVALID")) {
                        c = 4;
                        break;
                    }
                    break;
                case -1623547228:
                    if (str.equals("ADDRESS_STREET_LINE1_INVALID")) {
                        c = 7;
                        break;
                    }
                    break;
                case -1224177757:
                    if (str.equals("ADDRESS_COUNTRY_INVALID")) {
                        c = 3;
                        break;
                    }
                    break;
                case -1031752045:
                    if (str.equals("REQ_INFO_NAME_INVALID")) {
                        c = 0;
                        break;
                    }
                    break;
                case -274035920:
                    if (str.equals("ADDRESS_POSTCODE_INVALID")) {
                        c = 5;
                        break;
                    }
                    break;
                case 417441502:
                    if (str.equals("ADDRESS_STATE_INVALID")) {
                        c = 6;
                        break;
                    }
                    break;
                case 708423542:
                    if (str.equals("REQ_INFO_PHONE_INVALID")) {
                        c = 1;
                        break;
                    }
                    break;
                case 863965605:
                    if (str.equals("ADDRESS_STREET_LINE2_INVALID")) {
                        c = '\b';
                        break;
                    }
                    break;
                case 889106340:
                    if (str.equals("REQ_INFO_EMAIL_INVALID")) {
                        c = 2;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    shakeField(6);
                    return;
                case 1:
                    shakeField(9);
                    return;
                case 2:
                    shakeField(7);
                    return;
                case 3:
                    shakeField(4);
                    return;
                case 4:
                    shakeField(2);
                    return;
                case 5:
                    shakeField(5);
                    return;
                case 6:
                    shakeField(3);
                    return;
                case 7:
                    shakeField(0);
                    return;
                case '\b':
                    shakeField(1);
                    return;
                default:
                    AlertsCreator.processError(this.currentAccount, error, this, req, new Object[0]);
                    return;
            }
        }
    }

    private void sendData() {
        if (this.canceled) {
            return;
        }
        showEditDoneProgress(false, true);
        final TLRPC.TL_payments_sendPaymentForm req = new TLRPC.TL_payments_sendPaymentForm();
        if (this.messageObject != null) {
            TLRPC.TL_inputInvoiceMessage inputInvoice = new TLRPC.TL_inputInvoiceMessage();
            inputInvoice.peer = getMessagesController().getInputPeer(this.messageObject.messageOwner.peer_id);
            inputInvoice.msg_id = this.messageObject.getId();
            req.invoice = inputInvoice;
        } else {
            TLRPC.TL_inputInvoiceSlug inputInvoice2 = new TLRPC.TL_inputInvoiceSlug();
            inputInvoice2.slug = this.invoiceSlug;
            req.invoice = inputInvoice2;
        }
        req.form_id = this.paymentForm.form_id;
        if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && this.paymentForm.saved_credentials != null) {
            req.credentials = new TLRPC.TL_inputPaymentCredentialsSaved();
            req.credentials.id = this.paymentForm.saved_credentials.id;
            req.credentials.tmp_password = UserConfig.getInstance(this.currentAccount).tmpPassword.tmp_password;
        } else {
            TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay = this.googlePayCredentials;
            if (tL_inputPaymentCredentialsGooglePay != null) {
                req.credentials = tL_inputPaymentCredentialsGooglePay;
            } else {
                req.credentials = new TLRPC.TL_inputPaymentCredentials();
                req.credentials.save = this.saveCardInfo;
                req.credentials.data = new TLRPC.TL_dataJSON();
                req.credentials.data.data = this.paymentJson;
            }
        }
        TLRPC.TL_payments_validatedRequestedInfo tL_payments_validatedRequestedInfo = this.requestedInfo;
        if (tL_payments_validatedRequestedInfo != null && tL_payments_validatedRequestedInfo.id != null) {
            req.requested_info_id = this.requestedInfo.id;
            req.flags = 1 | req.flags;
        }
        TLRPC.TL_shippingOption tL_shippingOption = this.shippingOption;
        if (tL_shippingOption != null) {
            req.shipping_option_id = tL_shippingOption.id;
            req.flags |= 2;
        }
        if ((this.paymentForm.invoice.flags & 256) != 0) {
            Long l = this.tipAmount;
            req.tip_amount = l != null ? l.longValue() : 0L;
            req.flags |= 4;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda52
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PaymentFormActivity.this.m4141lambda$sendData$58$orgtelegramuiPaymentFormActivity(req, tLObject, tL_error);
            }
        }, 2);
    }

    /* renamed from: lambda$sendData$58$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4141lambda$sendData$58$orgtelegramuiPaymentFormActivity(final TLRPC.TL_payments_sendPaymentForm req, final TLObject response, final TLRPC.TL_error error) {
        if (response != null) {
            if (response instanceof TLRPC.TL_payments_paymentResult) {
                TLRPC.Updates updates = ((TLRPC.TL_payments_paymentResult) response).updates;
                final TLRPC.Message[] message = new TLRPC.Message[1];
                int a = 0;
                int N = updates.updates.size();
                while (true) {
                    if (a >= N) {
                        break;
                    }
                    TLRPC.Update update = updates.updates.get(a);
                    if (update instanceof TLRPC.TL_updateNewMessage) {
                        message[0] = ((TLRPC.TL_updateNewMessage) update).message;
                        break;
                    } else if (!(update instanceof TLRPC.TL_updateNewChannelMessage)) {
                        a++;
                    } else {
                        message[0] = ((TLRPC.TL_updateNewChannelMessage) update).message;
                        break;
                    }
                }
                getMessagesController().processUpdates(updates, false);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda45
                    @Override // java.lang.Runnable
                    public final void run() {
                        PaymentFormActivity.this.m4138lambda$sendData$55$orgtelegramuiPaymentFormActivity(message);
                    }
                });
                return;
            } else if (response instanceof TLRPC.TL_payments_paymentVerificationNeeded) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda29
                    @Override // java.lang.Runnable
                    public final void run() {
                        PaymentFormActivity.this.m4139lambda$sendData$56$orgtelegramuiPaymentFormActivity(response);
                    }
                });
                return;
            } else {
                return;
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda41
            @Override // java.lang.Runnable
            public final void run() {
                PaymentFormActivity.this.m4140lambda$sendData$57$orgtelegramuiPaymentFormActivity(error, req);
            }
        });
    }

    /* renamed from: lambda$sendData$55$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4138lambda$sendData$55$orgtelegramuiPaymentFormActivity(TLRPC.Message[] message) {
        this.paymentStatusSent = true;
        PaymentFormCallback paymentFormCallback = this.paymentFormCallback;
        if (paymentFormCallback != null) {
            paymentFormCallback.onInvoiceStatusChanged(InvoiceStatus.PAID);
        }
        goToNextStep();
        if (this.parentFragment instanceof ChatActivity) {
            CharSequence info = AndroidUtilities.replaceTags(LocaleController.formatString("PaymentInfoHint", R.string.PaymentInfoHint, this.totalPrice[0], this.currentItemName));
            ((ChatActivity) this.parentFragment).getUndoView().showWithAction(0L, 77, info, message[0], (Runnable) null, (Runnable) null);
        }
    }

    /* renamed from: lambda$sendData$56$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4139lambda$sendData$56$orgtelegramuiPaymentFormActivity(TLObject response) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.paymentFinished, new Object[0]);
        setDonePressed(false);
        this.webviewLoading = true;
        showEditDoneProgress(true, true);
        ContextProgressView contextProgressView = this.progressView;
        if (contextProgressView != null) {
            contextProgressView.setVisibility(0);
        }
        ActionBarMenuItem actionBarMenuItem = this.doneItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setEnabled(false);
            this.doneItem.getContentView().setVisibility(4);
        }
        WebView webView = this.webView;
        if (webView != null) {
            webView.setVisibility(0);
            WebView webView2 = this.webView;
            String str = ((TLRPC.TL_payments_paymentVerificationNeeded) response).url;
            this.webViewUrl = str;
            webView2.loadUrl(str);
        }
        this.paymentStatusSent = true;
        PaymentFormCallback paymentFormCallback = this.paymentFormCallback;
        if (paymentFormCallback != null) {
            paymentFormCallback.onInvoiceStatusChanged(InvoiceStatus.PENDING);
        }
    }

    /* renamed from: lambda$sendData$57$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4140lambda$sendData$57$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error, TLRPC.TL_payments_sendPaymentForm req) {
        AlertsCreator.processError(this.currentAccount, error, this, req, new Object[0]);
        setDonePressed(false);
        showEditDoneProgress(false, false);
        this.paymentStatusSent = true;
        PaymentFormCallback paymentFormCallback = this.paymentFormCallback;
        if (paymentFormCallback != null) {
            paymentFormCallback.onInvoiceStatusChanged(InvoiceStatus.FAILED);
        }
    }

    private void shakeField(int field) {
        shakeView(this.inputFields[field]);
    }

    private void shakeView(View view) {
        Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
        if (v != null) {
            v.vibrate(200L);
        }
        AndroidUtilities.shakeView(view, 2.0f, 0);
    }

    public void setDonePressed(boolean value) {
        this.donePressed = value;
        this.swipeBackEnabled = !value;
        if (this.actionBar != null) {
            this.actionBar.getBackButton().setEnabled(!this.donePressed);
        }
        TextDetailSettingsCell[] textDetailSettingsCellArr = this.detailSettingsCell;
        if (textDetailSettingsCellArr[0] != null) {
            textDetailSettingsCellArr[0].setEnabled(!this.donePressed);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isSwipeBackEnabled(MotionEvent event) {
        return this.swipeBackEnabled;
    }

    public void checkPassword() {
        if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
            UserConfig.getInstance(this.currentAccount).tmpPassword = null;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        if (UserConfig.getInstance(this.currentAccount).tmpPassword != null) {
            sendData();
        } else if (this.inputFields[1].length() == 0) {
            Vibrator v = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200L);
            }
            AndroidUtilities.shakeView(this.inputFields[1], 2.0f, 0);
        } else {
            final String password = this.inputFields[1].getText().toString();
            showEditDoneProgress(true, true);
            setDonePressed(true);
            final TLRPC.TL_account_getPassword req = new TLRPC.TL_account_getPassword();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda49
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PaymentFormActivity.this.m4101lambda$checkPassword$63$orgtelegramuiPaymentFormActivity(password, req, tLObject, tL_error);
                }
            }, 2);
        }
    }

    /* renamed from: lambda$checkPassword$63$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4101lambda$checkPassword$63$orgtelegramuiPaymentFormActivity(final String password, final TLRPC.TL_account_getPassword req, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda39
            @Override // java.lang.Runnable
            public final void run() {
                PaymentFormActivity.this.m4100lambda$checkPassword$62$orgtelegramuiPaymentFormActivity(error, response, password, req);
            }
        });
    }

    /* renamed from: lambda$checkPassword$62$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4100lambda$checkPassword$62$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error, TLObject response, String password, TLRPC.TL_account_getPassword req) {
        if (error == null) {
            final TLRPC.TL_account_password currentPassword = (TLRPC.TL_account_password) response;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(currentPassword, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                return;
            } else if (!currentPassword.has_password) {
                this.passwordOk = false;
                goToNextStep();
                return;
            } else {
                final byte[] passwordBytes = AndroidUtilities.getStringBytes(password);
                Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda34
                    @Override // java.lang.Runnable
                    public final void run() {
                        PaymentFormActivity.this.m4099lambda$checkPassword$61$orgtelegramuiPaymentFormActivity(currentPassword, passwordBytes);
                    }
                });
                return;
            }
        }
        AlertsCreator.processError(this.currentAccount, error, this, req, new Object[0]);
        showEditDoneProgress(true, false);
        setDonePressed(false);
    }

    /* renamed from: lambda$checkPassword$61$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4099lambda$checkPassword$61$orgtelegramuiPaymentFormActivity(TLRPC.TL_account_password currentPassword, byte[] passwordBytes) {
        byte[] x_bytes;
        if (currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) currentPassword.current_algo;
            x_bytes = SRPHelper.getX(passwordBytes, algo);
        } else {
            x_bytes = null;
        }
        final TLRPC.TL_account_getTmpPassword req1 = new TLRPC.TL_account_getTmpPassword();
        req1.period = 1800;
        RequestDelegate requestDelegate = new RequestDelegate() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda51
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PaymentFormActivity.this.m4098lambda$checkPassword$60$orgtelegramuiPaymentFormActivity(req1, tLObject, tL_error);
            }
        };
        if (currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo2 = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) currentPassword.current_algo;
            req1.password = SRPHelper.startCheck(x_bytes, currentPassword.srp_id, currentPassword.srp_B, algo2);
            if (req1.password == null) {
                TLRPC.TL_error error2 = new TLRPC.TL_error();
                error2.text = "ALGO_INVALID";
                requestDelegate.run(null, error2);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req1, requestDelegate, 10);
            return;
        }
        TLRPC.TL_error error22 = new TLRPC.TL_error();
        error22.text = "PASSWORD_HASH_INVALID";
        requestDelegate.run(null, error22);
    }

    /* renamed from: lambda$checkPassword$60$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4098lambda$checkPassword$60$orgtelegramuiPaymentFormActivity(final TLRPC.TL_account_getTmpPassword req1, final TLObject response1, final TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda33
            @Override // java.lang.Runnable
            public final void run() {
                PaymentFormActivity.this.m4097lambda$checkPassword$59$orgtelegramuiPaymentFormActivity(response1, error1, req1);
            }
        });
    }

    /* renamed from: lambda$checkPassword$59$org-telegram-ui-PaymentFormActivity */
    public /* synthetic */ void m4097lambda$checkPassword$59$orgtelegramuiPaymentFormActivity(TLObject response1, TLRPC.TL_error error1, TLRPC.TL_account_getTmpPassword req1) {
        showEditDoneProgress(true, false);
        setDonePressed(false);
        if (response1 != null) {
            this.passwordOk = true;
            UserConfig.getInstance(this.currentAccount).tmpPassword = (TLRPC.TL_account_tmpPassword) response1;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
            goToNextStep();
        } else if (error1.text.equals("PASSWORD_HASH_INVALID")) {
            Vibrator v = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200L);
            }
            AndroidUtilities.shakeView(this.inputFields[1], 2.0f, 0);
            this.inputFields[1].setText("");
        } else {
            AlertsCreator.processError(this.currentAccount, error1, this, req1, new Object[0]);
        }
    }

    public void showEditDoneProgress(boolean animateDoneItem, final boolean show) {
        AnimatorSet animatorSet = this.doneItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        if (animateDoneItem && this.doneItem != null) {
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.doneItemAnimation = animatorSet2;
            if (show) {
                this.progressView.setVisibility(0);
                this.doneItem.setEnabled(false);
                this.doneItemAnimation.playTogether(ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, 0.1f), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, 1.0f));
            } else if (this.webView != null) {
                animatorSet2.playTogether(ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, 0.1f), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, 0.0f));
            } else {
                this.doneItem.getContentView().setVisibility(0);
                this.doneItem.setEnabled(true);
                this.doneItemAnimation.playTogether(ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, 0.1f), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, 0.0f));
                if (!isFinishing()) {
                    this.doneItemAnimation.playTogether(ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, 1.0f));
                }
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PaymentFormActivity.27
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animation)) {
                        if (!show) {
                            PaymentFormActivity.this.progressView.setVisibility(4);
                        } else {
                            PaymentFormActivity.this.doneItem.getContentView().setVisibility(4);
                        }
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animation)) {
                        PaymentFormActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150L);
            this.doneItemAnimation.start();
        } else if (this.payTextView != null) {
            this.doneItemAnimation = new AnimatorSet();
            if (show) {
                this.progressViewButton.setVisibility(0);
                this.bottomLayout.setEnabled(false);
                this.doneItemAnimation.playTogether(ObjectAnimator.ofFloat(this.payTextView, View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.payTextView, View.SCALE_Y, 0.1f), ObjectAnimator.ofFloat(this.payTextView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.progressViewButton, View.ALPHA, 1.0f));
            } else {
                this.payTextView.setVisibility(0);
                this.bottomLayout.setEnabled(true);
                this.doneItemAnimation.playTogether(ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_Y, 0.1f), ObjectAnimator.ofFloat(this.progressViewButton, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.payTextView, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.payTextView, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.payTextView, View.ALPHA, 1.0f));
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PaymentFormActivity.28
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animation)) {
                        if (!show) {
                            PaymentFormActivity.this.progressViewButton.setVisibility(4);
                        } else {
                            PaymentFormActivity.this.payTextView.setVisibility(4);
                        }
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animation)) {
                        PaymentFormActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150L);
            this.doneItemAnimation.start();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean presentFragment(BaseFragment fragment) {
        onPresentFragment(fragment);
        return super.presentFragment(fragment);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean presentFragment(BaseFragment fragment, boolean removeLast) {
        onPresentFragment(fragment);
        return super.presentFragment(fragment, removeLast);
    }

    private void onPresentFragment(BaseFragment fragment) {
        AndroidUtilities.hideKeyboard(this.fragmentView);
        if (fragment instanceof PaymentFormActivity) {
            ((PaymentFormActivity) fragment).paymentFormCallback = this.paymentFormCallback;
            ((PaymentFormActivity) fragment).resourcesProvider = this.resourcesProvider;
            ((PaymentFormActivity) fragment).needPayAfterTransition = this.needPayAfterTransition;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        if (this.shouldNavigateBack) {
            this.webView.loadUrl(this.webViewUrl);
            this.shouldNavigateBack = false;
            return false;
        }
        return !this.donePressed;
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
        arrayList.add(new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressInner2));
        arrayList.add(new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressOuter2));
        arrayList.add(new ThemeDescription(this.progressViewButton, 0, null, null, null, null, Theme.key_contextProgressInner2));
        arrayList.add(new ThemeDescription(this.progressViewButton, 0, null, null, null, null, Theme.key_contextProgressOuter2));
        if (this.inputFields != null) {
            for (int a = 0; a < this.inputFields.length; a++) {
                arrayList.add(new ThemeDescription((View) this.inputFields[a].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
            }
        } else {
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        }
        if (this.radioCells != null) {
            for (int a2 = 0; a2 < this.radioCells.length; a2++) {
                arrayList.add(new ThemeDescription(this.radioCells[a2], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
                arrayList.add(new ThemeDescription(this.radioCells[a2], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
                arrayList.add(new ThemeDescription(this.radioCells[a2], 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(this.radioCells[a2], ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackground));
                arrayList.add(new ThemeDescription(this.radioCells[a2], ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackgroundChecked));
            }
        } else {
            arrayList.add(new ThemeDescription((View) null, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackground));
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackgroundChecked));
        }
        for (int a3 = 0; a3 < this.headerCell.length; a3++) {
            arrayList.add(new ThemeDescription(this.headerCell[a3], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(this.headerCell[a3], 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        }
        for (int a4 = 0; a4 < this.sectionCell.length; a4++) {
            arrayList.add(new ThemeDescription(this.sectionCell[a4], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        }
        for (int a5 = 0; a5 < this.bottomCell.length; a5++) {
            arrayList.add(new ThemeDescription(this.bottomCell[a5], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
            arrayList.add(new ThemeDescription(this.bottomCell[a5], 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
            arrayList.add(new ThemeDescription(this.bottomCell[a5], ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteLinkText));
        }
        for (int a6 = 0; a6 < this.dividers.size(); a6++) {
            arrayList.add(new ThemeDescription(this.dividers.get(a6), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        }
        arrayList.add(new ThemeDescription(this.codeFieldCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.codeFieldCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.codeFieldCell, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText));
        arrayList.add(new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
        arrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        arrayList.add(new ThemeDescription(this.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
        for (int a7 = 0; a7 < this.settingsCell.length; a7++) {
            arrayList.add(new ThemeDescription(this.settingsCell[a7], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(this.settingsCell[a7], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
            arrayList.add(new ThemeDescription(this.settingsCell[a7], 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        }
        arrayList.add(new ThemeDescription(this.payTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText6));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextPriceCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
        for (int a8 = 1; a8 < this.detailSettingsCell.length; a8++) {
            arrayList.add(new ThemeDescription(this.detailSettingsCell[a8], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(this.detailSettingsCell[a8], 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.detailSettingsCell[a8], 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        }
        arrayList.add(new ThemeDescription(this.paymentInfoCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"detailTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"detailExTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
        return arrayList;
    }

    /* loaded from: classes4.dex */
    public class BottomFrameLayout extends FrameLayout {
        Paint paint = new Paint(1);
        float progress;
        SpringAnimation springAnimation;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BottomFrameLayout(Context context, TLRPC.TL_payments_paymentForm paymentForm) {
            super(context);
            PaymentFormActivity.this = r3;
            this.progress = (!paymentForm.invoice.recurring || r3.isAcceptTermsChecked) ? 1.0f : 0.0f;
            setWillNotDraw(false);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(PaymentFormActivity.this.getThemedColor(Theme.key_switchTrackBlue));
            this.paint.setColor(PaymentFormActivity.this.getThemedColor(Theme.key_contacts_inviteBackground));
            canvas.drawCircle(LocaleController.isRTL ? getWidth() - AndroidUtilities.dp(28.0f) : AndroidUtilities.dp(28.0f), -AndroidUtilities.dp(28.0f), Math.max(getWidth(), getHeight()) * this.progress, this.paint);
        }

        public void setChecked(boolean checked) {
            SpringAnimation springAnimation = this.springAnimation;
            if (springAnimation != null) {
                springAnimation.cancel();
            }
            float to = checked ? 1.0f : 0.0f;
            if (this.progress == to) {
                return;
            }
            SpringAnimation spring = new SpringAnimation(new FloatValueHolder(this.progress * 100.0f)).setSpring(new SpringForce(100.0f * to).setStiffness(checked ? 500.0f : 650.0f).setDampingRatio(1.0f));
            this.springAnimation = spring;
            spring.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.PaymentFormActivity$BottomFrameLayout$$ExternalSyntheticLambda1
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
                public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                    PaymentFormActivity.BottomFrameLayout.this.m4158x718bb2b5(dynamicAnimation, f, f2);
                }
            });
            this.springAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.PaymentFormActivity$BottomFrameLayout$$ExternalSyntheticLambda0
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                    PaymentFormActivity.BottomFrameLayout.this.m4159x9ae007f6(dynamicAnimation, z, f, f2);
                }
            });
            this.springAnimation.start();
        }

        /* renamed from: lambda$setChecked$0$org-telegram-ui-PaymentFormActivity$BottomFrameLayout */
        public /* synthetic */ void m4158x718bb2b5(DynamicAnimation animation, float value, float velocity) {
            this.progress = value / 100.0f;
            if (PaymentFormActivity.this.payTextView != null) {
                PaymentFormActivity.this.payTextView.setAlpha((this.progress * 0.2f) + 0.8f);
            }
            invalidate();
        }

        /* renamed from: lambda$setChecked$1$org-telegram-ui-PaymentFormActivity$BottomFrameLayout */
        public /* synthetic */ void m4159x9ae007f6(DynamicAnimation animation, boolean canceled1, float value, float velocity) {
            if (animation == this.springAnimation) {
                this.springAnimation = null;
            }
        }
    }
}
