package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import com.google.firebase.messaging.Constants;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.Switch;
import org.telegram.ui.SessionBottomSheet;
/* loaded from: classes4.dex */
public class SessionBottomSheet extends BottomSheet {
    RLottieImageView imageView;
    BaseFragment parentFragment;
    TLRPC.TL_authorization session;

    /* loaded from: classes4.dex */
    public interface Callback {
        void onSessionTerminated(TLRPC.TL_authorization tL_authorization);
    }

    public SessionBottomSheet(BaseFragment fragment, final TLRPC.TL_authorization session, boolean isCurrentSession, Callback callback) {
        super(fragment.getParentActivity(), false);
        String timeText;
        setOpenNoDelay(true);
        Context context = fragment.getParentActivity();
        this.session = session;
        this.parentFragment = fragment;
        fixNavigationBar();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.imageView = rLottieImageView;
        rLottieImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.SessionBottomSheet.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (!SessionBottomSheet.this.imageView.isPlaying() && SessionBottomSheet.this.imageView.getAnimatedDrawable() != null) {
                    SessionBottomSheet.this.imageView.getAnimatedDrawable().setCurrentFrame(40);
                    SessionBottomSheet.this.imageView.playAnimation();
                }
            }
        });
        this.imageView.setScaleType(ImageView.ScaleType.CENTER);
        linearLayout.addView(this.imageView, LayoutHelper.createLinear(70, 70, 1, 0, 16, 0, 0));
        TextView nameView = new TextView(context);
        nameView.setTextSize(2, 20.0f);
        nameView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        nameView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        nameView.setGravity(17);
        linearLayout.addView(nameView, LayoutHelper.createLinear(-1, -2, 1, 21, 12, 21, 0));
        TextView timeView = new TextView(context);
        timeView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        timeView.setTextSize(2, 13.0f);
        timeView.setGravity(17);
        linearLayout.addView(timeView, LayoutHelper.createLinear(-1, -2, 1, 21, 4, 21, 21));
        if ((session.flags & 1) != 0) {
            timeText = LocaleController.getString("Online", R.string.Online);
        } else {
            timeText = LocaleController.formatDateTime(session.date_active);
        }
        timeView.setText(timeText);
        StringBuilder stringBuilder = new StringBuilder();
        if (session.device_model.length() != 0) {
            stringBuilder.append(session.device_model);
        }
        if (stringBuilder.length() == 0) {
            if (session.platform.length() != 0) {
                stringBuilder.append(session.platform);
            }
            if (session.system_version.length() != 0) {
                if (session.platform.length() != 0) {
                    stringBuilder.append(" ");
                }
                stringBuilder.append(session.system_version);
            }
        }
        nameView.setText(stringBuilder);
        setAnimation(session, this.imageView);
        ItemView applicationItemView = new ItemView(context, false);
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(session.app_name);
        stringBuilder2.append(" ");
        stringBuilder2.append(session.app_version);
        applicationItemView.valueText.setText(stringBuilder2);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.menu_devices).mutate();
        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.SRC_IN));
        applicationItemView.iconView.setImageDrawable(drawable);
        applicationItemView.descriptionText.setText(LocaleController.getString("Application", R.string.Application));
        linearLayout.addView(applicationItemView);
        ItemView prevItem = applicationItemView;
        if (session.country.length() != 0) {
            ItemView locationItemView = new ItemView(context, false);
            locationItemView.valueText.setText(session.country);
            Drawable drawable2 = ContextCompat.getDrawable(context, R.drawable.msg_location).mutate();
            drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.SRC_IN));
            locationItemView.iconView.setImageDrawable(drawable2);
            locationItemView.descriptionText.setText(LocaleController.getString("Location", R.string.Location));
            locationItemView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.SessionBottomSheet.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SessionBottomSheet.this.copyText(session.country);
                }
            });
            locationItemView.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.SessionBottomSheet.3
                @Override // android.view.View.OnLongClickListener
                public boolean onLongClick(View view) {
                    SessionBottomSheet.this.copyText(session.country);
                    return true;
                }
            });
            locationItemView.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 2));
            linearLayout.addView(locationItemView);
            prevItem.needDivider = true;
            prevItem = locationItemView;
        }
        if (session.ip.length() != 0) {
            ItemView locationItemView2 = new ItemView(context, false);
            locationItemView2.valueText.setText(session.ip);
            Drawable drawable3 = ContextCompat.getDrawable(context, R.drawable.msg_language).mutate();
            drawable3.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.SRC_IN));
            locationItemView2.iconView.setImageDrawable(drawable3);
            locationItemView2.descriptionText.setText(LocaleController.getString("IpAddress", R.string.IpAddress));
            locationItemView2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.SessionBottomSheet.4
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SessionBottomSheet.this.copyText(session.ip);
                }
            });
            locationItemView2.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.SessionBottomSheet.5
                @Override // android.view.View.OnLongClickListener
                public boolean onLongClick(View view) {
                    SessionBottomSheet.this.copyText(session.country);
                    return true;
                }
            });
            locationItemView2.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 2));
            linearLayout.addView(locationItemView2);
            prevItem.needDivider = true;
            prevItem = locationItemView2;
        }
        if (secretChatsEnabled(session)) {
            final ItemView acceptSecretChats = new ItemView(context, true);
            acceptSecretChats.valueText.setText(LocaleController.getString("AcceptSecretChats", R.string.AcceptSecretChats));
            Drawable drawable4 = ContextCompat.getDrawable(context, R.drawable.msg_secret).mutate();
            drawable4.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.SRC_IN));
            acceptSecretChats.iconView.setImageDrawable(drawable4);
            acceptSecretChats.switchView.setChecked(!session.encrypted_requests_disabled, false);
            acceptSecretChats.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 7));
            acceptSecretChats.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.SessionBottomSheet.6
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    acceptSecretChats.switchView.setChecked(!acceptSecretChats.switchView.isChecked(), true);
                    session.encrypted_requests_disabled = !acceptSecretChats.switchView.isChecked();
                    SessionBottomSheet.this.uploadSessionSettings();
                }
            });
            prevItem.needDivider = true;
            acceptSecretChats.descriptionText.setText(LocaleController.getString("AcceptSecretChatsDescription", R.string.AcceptSecretChatsDescription));
            linearLayout.addView(acceptSecretChats);
            prevItem = acceptSecretChats;
        }
        final ItemView acceptCalls = new ItemView(context, true);
        acceptCalls.valueText.setText(LocaleController.getString("AcceptCalls", R.string.AcceptCalls));
        Drawable drawable5 = ContextCompat.getDrawable(context, R.drawable.msg_calls).mutate();
        drawable5.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.SRC_IN));
        acceptCalls.iconView.setImageDrawable(drawable5);
        acceptCalls.switchView.setChecked(!session.call_requests_disabled, false);
        acceptCalls.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 7));
        acceptCalls.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.SessionBottomSheet.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                acceptCalls.switchView.setChecked(!acceptCalls.switchView.isChecked(), true);
                session.call_requests_disabled = !acceptCalls.switchView.isChecked();
                SessionBottomSheet.this.uploadSessionSettings();
            }
        });
        prevItem.needDivider = true;
        acceptCalls.descriptionText.setText(LocaleController.getString("AcceptCallsChatsDescription", R.string.AcceptCallsChatsDescription));
        linearLayout.addView(acceptCalls);
        if (!isCurrentSession) {
            TextView buttonTextView = new TextView(context);
            buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            buttonTextView.setGravity(17);
            buttonTextView.setTextSize(1, 14.0f);
            buttonTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            buttonTextView.setText(LocaleController.getString("TerminateSession", R.string.TerminateSession));
            buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            buttonTextView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_chat_attachAudioBackground), ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_windowBackgroundWhite), 120)));
            linearLayout.addView(buttonTextView, LayoutHelper.createFrame(-1, 48.0f, 0, 16.0f, 15.0f, 16.0f, 16.0f));
            buttonTextView.setOnClickListener(new AnonymousClass8(callback, session, fragment));
        }
        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(linearLayout);
        setCustomView(scrollView);
    }

    /* renamed from: org.telegram.ui.SessionBottomSheet$8 */
    /* loaded from: classes4.dex */
    public class AnonymousClass8 implements View.OnClickListener {
        final /* synthetic */ Callback val$callback;
        final /* synthetic */ BaseFragment val$fragment;
        final /* synthetic */ TLRPC.TL_authorization val$session;

        AnonymousClass8(Callback callback, TLRPC.TL_authorization tL_authorization, BaseFragment baseFragment) {
            SessionBottomSheet.this = this$0;
            this.val$callback = callback;
            this.val$session = tL_authorization;
            this.val$fragment = baseFragment;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SessionBottomSheet.this.parentFragment.getParentActivity());
            boolean[] zArr = new boolean[1];
            builder.setMessage(LocaleController.getString("TerminateSessionText", R.string.TerminateSessionText));
            builder.setTitle(LocaleController.getString("AreYouSureSessionTitle", R.string.AreYouSureSessionTitle));
            String buttonText = LocaleController.getString("Terminate", R.string.Terminate);
            final Callback callback = this.val$callback;
            final TLRPC.TL_authorization tL_authorization = this.val$session;
            builder.setPositiveButton(buttonText, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.SessionBottomSheet$8$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    SessionBottomSheet.AnonymousClass8.this.m4547lambda$onClick$0$orgtelegramuiSessionBottomSheet$8(callback, tL_authorization, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            AlertDialog alertDialog = builder.create();
            this.val$fragment.showDialog(alertDialog);
            TextView button = (TextView) alertDialog.getButton(-1);
            if (button != null) {
                button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
            }
        }

        /* renamed from: lambda$onClick$0$org-telegram-ui-SessionBottomSheet$8 */
        public /* synthetic */ void m4547lambda$onClick$0$orgtelegramuiSessionBottomSheet$8(Callback callback, TLRPC.TL_authorization session, DialogInterface dialogInterface, int option) {
            callback.onSessionTerminated(session);
            SessionBottomSheet.this.dismiss();
        }
    }

    private boolean secretChatsEnabled(TLRPC.TL_authorization session) {
        if (session.api_id == 2040 || session.api_id == 2496) {
            return false;
        }
        return true;
    }

    public void uploadSessionSettings() {
        TLRPC.TL_account_changeAuthorizationSettings req = new TLRPC.TL_account_changeAuthorizationSettings();
        req.encrypted_requests_disabled = this.session.encrypted_requests_disabled;
        req.call_requests_disabled = this.session.call_requests_disabled;
        req.flags = 3;
        req.hash = this.session.hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, SessionBottomSheet$$ExternalSyntheticLambda1.INSTANCE);
    }

    public static /* synthetic */ void lambda$uploadSessionSettings$0(TLObject response, TLRPC.TL_error error) {
    }

    public void copyText(final String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(new CharSequence[]{LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.SessionBottomSheet$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                SessionBottomSheet.this.m4546lambda$copyText$1$orgtelegramuiSessionBottomSheet(text, dialogInterface, i);
            }
        });
        builder.show();
    }

    /* renamed from: lambda$copyText$1$org-telegram-ui-SessionBottomSheet */
    public /* synthetic */ void m4546lambda$copyText$1$orgtelegramuiSessionBottomSheet(String text, DialogInterface dialogInterface, int i) {
        ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
        ClipData clip = ClipData.newPlainText(Constants.ScionAnalytics.PARAM_LABEL, text);
        clipboard.setPrimaryClip(clip);
        BulletinFactory.of(getContainer(), null).createCopyBulletin(LocaleController.getString("TextCopied", R.string.TextCopied)).show();
    }

    private void setAnimation(TLRPC.TL_authorization session, RLottieImageView imageView) {
        String colorKey;
        int iconId;
        String platform = session.platform.toLowerCase();
        if (platform.isEmpty()) {
            platform = session.system_version.toLowerCase();
        }
        String deviceModel = session.device_model.toLowerCase();
        boolean animation = true;
        if (deviceModel.contains("safari")) {
            iconId = R.raw.safari_30;
            colorKey = Theme.key_avatar_backgroundPink;
        } else if (deviceModel.contains("edge")) {
            iconId = R.raw.edge_30;
            colorKey = Theme.key_avatar_backgroundPink;
        } else if (deviceModel.contains("chrome")) {
            iconId = R.raw.chrome_30;
            colorKey = Theme.key_avatar_backgroundPink;
        } else if (deviceModel.contains("opera") || deviceModel.contains("firefox") || deviceModel.contains("vivaldi")) {
            animation = false;
            if (deviceModel.contains("opera")) {
                iconId = R.drawable.device_web_opera;
            } else if (deviceModel.contains("firefox")) {
                iconId = R.drawable.device_web_firefox;
            } else {
                iconId = R.drawable.device_web_other;
            }
            colorKey = Theme.key_avatar_backgroundPink;
        } else if (platform.contains("ubuntu")) {
            iconId = R.raw.ubuntu_30;
            colorKey = Theme.key_avatar_backgroundBlue;
        } else if (platform.contains("ios")) {
            iconId = deviceModel.contains("ipad") ? R.raw.ipad_30 : R.raw.iphone_30;
            colorKey = Theme.key_avatar_backgroundBlue;
        } else if (platform.contains("windows")) {
            iconId = R.raw.windows_30;
            colorKey = Theme.key_avatar_backgroundCyan;
        } else if (platform.contains("macos")) {
            iconId = R.raw.mac_30;
            colorKey = Theme.key_avatar_backgroundCyan;
        } else if (platform.contains("android")) {
            iconId = R.raw.android_30;
            colorKey = Theme.key_avatar_backgroundGreen;
        } else if (session.app_name.toLowerCase().contains("desktop")) {
            iconId = R.raw.windows_30;
            colorKey = Theme.key_avatar_backgroundCyan;
        } else {
            iconId = R.raw.chrome_30;
            colorKey = Theme.key_avatar_backgroundPink;
        }
        imageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(42.0f), Theme.getColor(colorKey)));
        if (animation) {
            int[] colors = {0, Theme.getColor(colorKey)};
            imageView.setAnimation(iconId, 50, 50, colors);
            return;
        }
        imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), iconId));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class ItemView extends FrameLayout {
        TextView descriptionText;
        ImageView iconView;
        boolean needDivider = false;
        Switch switchView;
        TextView valueText;

        public ItemView(Context context, boolean needSwitch) {
            super(context);
            ImageView imageView = new ImageView(context);
            this.iconView = imageView;
            addView(imageView, LayoutHelper.createFrame(28, 28.0f, 0, 16.0f, 8.0f, 0.0f, 0.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 0, 64.0f, 4.0f, 0.0f, 4.0f));
            TextView textView = new TextView(context);
            this.valueText = textView;
            textView.setTextSize(2, 16.0f);
            this.valueText.setGravity(3);
            this.valueText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            linearLayout.addView(this.valueText, LayoutHelper.createLinear(-1, -2, 0, 0, 0, needSwitch ? 46 : 0, 0));
            TextView textView2 = new TextView(context);
            this.descriptionText = textView2;
            textView2.setTextSize(2, 13.0f);
            this.descriptionText.setGravity(3);
            this.descriptionText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            linearLayout.addView(this.descriptionText, LayoutHelper.createLinear(-1, -2, 0, 0, 4, needSwitch ? 46 : 0, 0));
            setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
            if (needSwitch) {
                Switch r2 = new Switch(context);
                this.switchView = r2;
                r2.setDrawIconType(1);
                addView(this.switchView, LayoutHelper.createFrame(37, 40.0f, 21, 21.0f, 0.0f, 21.0f, 0.0f));
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            if (this.needDivider) {
                canvas.drawRect(AndroidUtilities.dp(64.0f), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight(), Theme.dividerPaint);
            }
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            String str;
            int i;
            super.onInitializeAccessibilityNodeInfo(info);
            if (this.switchView != null) {
                info.setClassName("android.widget.Switch");
                info.setCheckable(true);
                info.setChecked(this.switchView.isChecked());
                StringBuilder sb = new StringBuilder();
                sb.append((Object) this.valueText.getText());
                sb.append("\n");
                sb.append((Object) this.descriptionText.getText());
                sb.append("\n");
                if (this.switchView.isChecked()) {
                    i = R.string.NotificationsOn;
                    str = "NotificationsOn";
                } else {
                    i = R.string.NotificationsOff;
                    str = "NotificationsOff";
                }
                sb.append(LocaleController.getString(str, i));
                info.setText(sb.toString());
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void show() {
        super.show();
        this.imageView.playAnimation();
    }
}
