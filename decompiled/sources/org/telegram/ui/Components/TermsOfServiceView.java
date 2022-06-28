package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class TermsOfServiceView extends FrameLayout {
    private int currentAccount;
    private TLRPC.TL_help_termsOfService currentTos;
    private TermsOfServiceViewDelegate delegate;
    private ScrollView scrollView;
    private TextView textView;
    private TextView titleTextView;

    /* loaded from: classes5.dex */
    public interface TermsOfServiceViewDelegate {
        void onAcceptTerms(int i);

        void onDeclineTerms(int i);
    }

    public TermsOfServiceView(Context context) {
        super(context);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        int top = Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
        if (top > 0) {
            View view = new View(context);
            view.setBackgroundColor(-16777216);
            addView(view, new FrameLayout.LayoutParams(-1, top));
        }
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.logo_middle);
        linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 3, 0, 28, 0, 0));
        TextView textView = new TextView(context);
        this.titleTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.titleTextView.setTextSize(1, 17.0f);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.titleTextView.setText(LocaleController.getString("PrivacyPolicyAndTerms", R.string.PrivacyPolicyAndTerms));
        linearLayout.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 3, 0, 20, 0, 0));
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        this.textView.setGravity(51);
        this.textView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        linearLayout.addView(this.textView, LayoutHelper.createLinear(-1, -2, 3, 0, 15, 0, 15));
        ScrollView scrollView = new ScrollView(context);
        this.scrollView = scrollView;
        scrollView.setVerticalScrollBarEnabled(false);
        this.scrollView.setOverScrollMode(2);
        this.scrollView.setPadding(AndroidUtilities.dp(24.0f), top, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(75.0f));
        this.scrollView.addView(linearLayout, new FrameLayout.LayoutParams(-1, -2));
        addView(this.scrollView, LayoutHelper.createLinear(-1, -2));
        TextView declineTextView = new TextView(context);
        declineTextView.setText(LocaleController.getString("Decline", R.string.Decline).toUpperCase());
        declineTextView.setGravity(17);
        declineTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        declineTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        declineTextView.setTextSize(1, 14.0f);
        declineTextView.setBackground(Theme.getRoundRectSelectorDrawable(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText)));
        declineTextView.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f));
        addView(declineTextView, LayoutHelper.createFrame(-2, -2.0f, 83, 16.0f, 0.0f, 16.0f, 16.0f));
        declineTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.TermsOfServiceView$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                TermsOfServiceView.this.m3125lambda$new$4$orgtelegramuiComponentsTermsOfServiceView(view2);
            }
        });
        TextView acceptTextView = new TextView(context);
        acceptTextView.setText(LocaleController.getString("Accept", R.string.Accept));
        acceptTextView.setGravity(17);
        acceptTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        acceptTextView.setTextColor(-1);
        acceptTextView.setTextSize(1, 14.0f);
        acceptTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), -11491093, -12346402));
        acceptTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        addView(acceptTextView, LayoutHelper.createFrame(-2, 42.0f, 85, 16.0f, 0.0f, 16.0f, 16.0f));
        acceptTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.TermsOfServiceView$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                TermsOfServiceView.this.m3127lambda$new$6$orgtelegramuiComponentsTermsOfServiceView(view2);
            }
        });
        View lineView = new View(context);
        lineView.setBackgroundColor(Theme.getColor(Theme.key_divider));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, 1);
        params.bottomMargin = AndroidUtilities.dp(75.0f);
        params.gravity = 80;
        addView(lineView, params);
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-TermsOfServiceView */
    public /* synthetic */ void m3125lambda$new$4$orgtelegramuiComponentsTermsOfServiceView(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(LocaleController.getString("TermsOfService", R.string.TermsOfService));
        builder.setPositiveButton(LocaleController.getString("DeclineDeactivate", R.string.DeclineDeactivate), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.TermsOfServiceView$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                TermsOfServiceView.this.m3124lambda$new$3$orgtelegramuiComponentsTermsOfServiceView(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Back", R.string.Back), null);
        builder.setMessage(LocaleController.getString("TosUpdateDecline", R.string.TosUpdateDecline));
        builder.show();
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-TermsOfServiceView */
    public /* synthetic */ void m3124lambda$new$3$orgtelegramuiComponentsTermsOfServiceView(DialogInterface dialog, int which) {
        AlertDialog.Builder builder12 = new AlertDialog.Builder(getContext());
        builder12.setMessage(LocaleController.getString("TosDeclineDeleteAccount", R.string.TosDeclineDeleteAccount));
        builder12.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder12.setPositiveButton(LocaleController.getString("Deactivate", R.string.Deactivate), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.TermsOfServiceView$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                TermsOfServiceView.this.m3123lambda$new$2$orgtelegramuiComponentsTermsOfServiceView(dialogInterface, i);
            }
        });
        builder12.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder12.show();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-TermsOfServiceView */
    public /* synthetic */ void m3123lambda$new$2$orgtelegramuiComponentsTermsOfServiceView(DialogInterface dialogInterface, int i) {
        final AlertDialog progressDialog = new AlertDialog(getContext(), 3);
        progressDialog.setCanCancel(false);
        TLRPC.TL_account_deleteAccount req = new TLRPC.TL_account_deleteAccount();
        req.reason = "Decline ToS update";
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.TermsOfServiceView$$ExternalSyntheticLambda6
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TermsOfServiceView.this.m3122lambda$new$1$orgtelegramuiComponentsTermsOfServiceView(progressDialog, tLObject, tL_error);
            }
        });
        progressDialog.show();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-TermsOfServiceView */
    public /* synthetic */ void m3122lambda$new$1$orgtelegramuiComponentsTermsOfServiceView(final AlertDialog progressDialog, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.TermsOfServiceView$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                TermsOfServiceView.this.m3121lambda$new$0$orgtelegramuiComponentsTermsOfServiceView(progressDialog, response, error);
            }
        });
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-TermsOfServiceView */
    public /* synthetic */ void m3121lambda$new$0$orgtelegramuiComponentsTermsOfServiceView(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (response instanceof TLRPC.TL_boolTrue) {
            MessagesController.getInstance(this.currentAccount).performLogout(0);
        } else if (error == null || error.code != -1000) {
            String errorText = LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred);
            if (error != null) {
                errorText = errorText + "\n" + error.text;
            }
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            builder1.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder1.setMessage(errorText);
            builder1.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            builder1.show();
        }
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-TermsOfServiceView */
    public /* synthetic */ void m3127lambda$new$6$orgtelegramuiComponentsTermsOfServiceView(View view) {
        if (this.currentTos.min_age_confirm != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle(LocaleController.getString("TosAgeTitle", R.string.TosAgeTitle));
            builder.setPositiveButton(LocaleController.getString("Agree", R.string.Agree), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.TermsOfServiceView$$ExternalSyntheticLambda2
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    TermsOfServiceView.this.m3126lambda$new$5$orgtelegramuiComponentsTermsOfServiceView(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            builder.setMessage(LocaleController.formatString("TosAgeText", R.string.TosAgeText, LocaleController.formatPluralString("Years", this.currentTos.min_age_confirm, new Object[0])));
            builder.show();
            return;
        }
        accept();
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-TermsOfServiceView */
    public /* synthetic */ void m3126lambda$new$5$orgtelegramuiComponentsTermsOfServiceView(DialogInterface dialog, int which) {
        accept();
    }

    private void accept() {
        this.delegate.onAcceptTerms(this.currentAccount);
        TLRPC.TL_help_acceptTermsOfService req = new TLRPC.TL_help_acceptTermsOfService();
        req.id = this.currentTos.id;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, TermsOfServiceView$$ExternalSyntheticLambda7.INSTANCE);
    }

    public static /* synthetic */ void lambda$accept$7(TLObject response, TLRPC.TL_error error) {
    }

    public void show(int account, TLRPC.TL_help_termsOfService tos) {
        if (getVisibility() != 0) {
            setVisibility(0);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(tos.text);
        MessageObject.addEntitiesToText(builder, tos.entities, false, false, false, false);
        addBulletsToText(builder, '-', AndroidUtilities.dp(10.0f), -11491093, AndroidUtilities.dp(4.0f));
        this.textView.setText(builder);
        this.currentTos = tos;
        this.currentAccount = account;
    }

    public void setDelegate(TermsOfServiceViewDelegate termsOfServiceViewDelegate) {
        this.delegate = termsOfServiceViewDelegate;
    }

    private static void addBulletsToText(SpannableStringBuilder builder, char bulletChar, int gapWidth, int color, int radius) {
        int until = builder.length() - 2;
        for (int i = 0; i < until; i++) {
            if (builder.charAt(i) == '\n' && builder.charAt(i + 1) == bulletChar && builder.charAt(i + 2) == ' ') {
                BulletSpan span = new BulletSpan(gapWidth, color, radius);
                builder.replace(i + 1, i + 3, "\u0000\u0000");
                builder.setSpan(span, i + 1, i + 2, 33);
            }
        }
    }
}
