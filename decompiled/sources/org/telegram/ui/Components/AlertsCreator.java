package org.telegram.ui.Components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.util.Consumer;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import androidx.exifinterface.media.ExifInterface;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.android.gms.location.LocationRequest;
import java.net.IDN;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.OneUIUtilities;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.CacheControlActivity;
import org.telegram.ui.Cells.AccountSelectCell;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.LanguageSelectActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.LoginActivity;
import org.telegram.ui.NotificationsCustomSettingsActivity;
import org.telegram.ui.NotificationsSettingsActivity;
import org.telegram.ui.ProfileNotificationsActivity;
import org.telegram.ui.ThemePreviewActivity;
import org.telegram.ui.TooManyCommunitiesActivity;
/* loaded from: classes5.dex */
public class AlertsCreator {
    public static final int PERMISSIONS_REQUEST_TOP_ICON_SIZE = 72;
    public static final int REPORT_TYPE_CHILD_ABUSE = 2;
    public static final int REPORT_TYPE_FAKE_ACCOUNT = 6;
    public static final int REPORT_TYPE_ILLEGAL_DRUGS = 3;
    public static final int REPORT_TYPE_OTHER = 100;
    public static final int REPORT_TYPE_PERSONAL_DETAILS = 4;
    public static final int REPORT_TYPE_PORNOGRAPHY = 5;
    public static final int REPORT_TYPE_SPAM = 0;
    public static final int REPORT_TYPE_VIOLENCE = 1;

    /* loaded from: classes5.dex */
    public interface AccountSelectDelegate {
        void didSelectAccount(int i);
    }

    /* loaded from: classes5.dex */
    public interface BlockDialogCallback {
        void run(boolean z, boolean z2);
    }

    /* loaded from: classes5.dex */
    public interface DatePickerDelegate {
        void didSelectDate(int i, int i2, int i3);
    }

    /* loaded from: classes5.dex */
    public interface PaymentAlertDelegate {
        void didPressedNewCard();
    }

    /* loaded from: classes5.dex */
    public interface ScheduleDatePickerDelegate {
        void didSelectDate(boolean z, int i);
    }

    /* loaded from: classes5.dex */
    public interface SoundFrequencyDelegate {
        void didSelectValues(int i, int i2);
    }

    public static Dialog createForgotPasscodeDialog(Context ctx) {
        return new AlertDialog.Builder(ctx).setTitle(LocaleController.getString((int) R.string.ForgotPasscode)).setMessage(LocaleController.getString((int) R.string.ForgotPasscodeInfo)).setPositiveButton(LocaleController.getString((int) R.string.Close), null).create();
    }

    public static Dialog createLocationRequiredDialog(final Context ctx, boolean friends) {
        return new AlertDialog.Builder(ctx).setMessage(AndroidUtilities.replaceTags(friends ? LocaleController.getString("PermissionNoLocationFriends", R.string.PermissionNoLocationFriends) : LocaleController.getString("PermissionNoLocationPeopleNearby", R.string.PermissionNoLocationPeopleNearby))).setTopAnimation(R.raw.permission_request_location, 72, false, Theme.getColor(Theme.key_dialogTopBackground)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda22
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createLocationRequiredDialog$0(ctx, dialogInterface, i);
            }
        }).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", R.string.ContactsPermissionAlertNotNow), null).create();
    }

    public static /* synthetic */ void lambda$createLocationRequiredDialog$0(Context ctx, DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            ctx.startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static Dialog createBackgroundActivityDialog(final Context ctx) {
        int i;
        AlertDialog.Builder title = new AlertDialog.Builder(ctx).setTitle(LocaleController.getString((int) R.string.AllowBackgroundActivity));
        if (OneUIUtilities.isOneUI()) {
            i = Build.VERSION.SDK_INT >= 31 ? R.string.AllowBackgroundActivityInfoOneUIAboveS : R.string.AllowBackgroundActivityInfoOneUIBelowS;
        } else {
            i = R.string.AllowBackgroundActivityInfo;
        }
        return title.setMessage(AndroidUtilities.replaceTags(LocaleController.getString(i))).setTopAnimation(R.raw.permission_request_apk, 72, false, Theme.getColor(Theme.key_dialogTopBackground)).setPositiveButton(LocaleController.getString((int) R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda128
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                AlertsCreator.lambda$createBackgroundActivityDialog$1(ctx, dialogInterface, i2);
            }
        }).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", R.string.ContactsPermissionAlertNotNow), null).create();
    }

    public static /* synthetic */ void lambda$createBackgroundActivityDialog$1(Context ctx, DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            ctx.startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static Dialog createWebViewPermissionsRequestDialog(final Context ctx, Theme.ResourcesProvider resourcesProvider, String[] systemPermissions, int animationId, String title, String titleWithHint, final Consumer<Boolean> callback) {
        boolean showSettings = false;
        if (systemPermissions != null && (ctx instanceof Activity) && Build.VERSION.SDK_INT >= 23) {
            Activity activity = (Activity) ctx;
            int length = systemPermissions.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                String perm = systemPermissions[i];
                if (activity.checkSelfPermission(perm) == 0 || !activity.shouldShowRequestPermissionRationale(perm)) {
                    i++;
                } else {
                    showSettings = true;
                    break;
                }
            }
        }
        final AtomicBoolean gotCallback = new AtomicBoolean();
        final boolean finalShowSettings = showSettings;
        return new AlertDialog.Builder(ctx, resourcesProvider).setTopAnimation(animationId, 72, false, Theme.getColor(Theme.key_dialogTopBackground)).setMessage(AndroidUtilities.replaceTags(showSettings ? titleWithHint : title)).setPositiveButton(LocaleController.getString(showSettings ? R.string.PermissionOpenSettings : R.string.BotWebViewRequestAllow), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda54
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                AlertsCreator.lambda$createWebViewPermissionsRequestDialog$2(finalShowSettings, ctx, gotCallback, callback, dialogInterface, i2);
            }
        }).setNegativeButton(LocaleController.getString((int) R.string.BotWebViewRequestDontAllow), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda36
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                AlertsCreator.lambda$createWebViewPermissionsRequestDialog$3(gotCallback, callback, dialogInterface, i2);
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda68
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                AlertsCreator.lambda$createWebViewPermissionsRequestDialog$4(gotCallback, callback, dialogInterface);
            }
        }).create();
    }

    public static /* synthetic */ void lambda$createWebViewPermissionsRequestDialog$2(boolean finalShowSettings, Context ctx, AtomicBoolean gotCallback, Consumer callback, DialogInterface dialogInterface, int i) {
        if (finalShowSettings) {
            try {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                ctx.startActivity(intent);
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        gotCallback.set(true);
        callback.accept(true);
    }

    public static /* synthetic */ void lambda$createWebViewPermissionsRequestDialog$3(AtomicBoolean gotCallback, Consumer callback, DialogInterface dialog, int which) {
        gotCallback.set(true);
        callback.accept(false);
    }

    public static /* synthetic */ void lambda$createWebViewPermissionsRequestDialog$4(AtomicBoolean gotCallback, Consumer callback, DialogInterface dialog) {
        if (!gotCallback.get()) {
            callback.accept(false);
        }
    }

    public static Dialog createApkRestrictedDialog(final Context ctx, Theme.ResourcesProvider resourcesProvider) {
        return new AlertDialog.Builder(ctx, resourcesProvider).setMessage(LocaleController.getString("ApkRestricted", R.string.ApkRestricted)).setTopAnimation(R.raw.permission_request_apk, 72, false, Theme.getColor(Theme.key_dialogTopBackground)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda117
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createApkRestrictedDialog$5(ctx, dialogInterface, i);
            }
        }).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", R.string.ContactsPermissionAlertNotNow), null).create();
    }

    public static /* synthetic */ void lambda$createApkRestrictedDialog$5(Context ctx, DialogInterface dialogInterface, int i) {
        try {
            ctx.startActivity(new Intent("android.settings.MANAGE_UNKNOWN_APP_SOURCES", Uri.parse("package:" + ctx.getPackageName())));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static Dialog processError(int currentAccount, TLRPC.TL_error error, BaseFragment fragment, TLObject request, Object... args) {
        TLRPC.InputPeer peer;
        char c;
        char c2;
        char c3;
        if (error.code != 406 && error.text != null) {
            if ((request instanceof TLRPC.TL_messages_initHistoryImport) || (request instanceof TLRPC.TL_messages_checkHistoryImportPeer) || (request instanceof TLRPC.TL_messages_checkHistoryImport) || (request instanceof TLRPC.TL_messages_startHistoryImport)) {
                if (request instanceof TLRPC.TL_messages_initHistoryImport) {
                    peer = ((TLRPC.TL_messages_initHistoryImport) request).peer;
                } else if (request instanceof TLRPC.TL_messages_startHistoryImport) {
                    peer = ((TLRPC.TL_messages_startHistoryImport) request).peer;
                } else {
                    peer = null;
                }
                if (error.text.contains("USER_IS_BLOCKED")) {
                    showSimpleAlert(fragment, LocaleController.getString("ImportErrorTitle", R.string.ImportErrorTitle), LocaleController.getString("ImportErrorUserBlocked", R.string.ImportErrorUserBlocked));
                    return null;
                } else if (error.text.contains("USER_NOT_MUTUAL_CONTACT")) {
                    showSimpleAlert(fragment, LocaleController.getString("ImportErrorTitle", R.string.ImportErrorTitle), LocaleController.getString("ImportMutualError", R.string.ImportMutualError));
                    return null;
                } else if (error.text.contains("IMPORT_PEER_TYPE_INVALID")) {
                    if (peer instanceof TLRPC.TL_inputPeerUser) {
                        showSimpleAlert(fragment, LocaleController.getString("ImportErrorTitle", R.string.ImportErrorTitle), LocaleController.getString("ImportErrorChatInvalidUser", R.string.ImportErrorChatInvalidUser));
                        return null;
                    }
                    showSimpleAlert(fragment, LocaleController.getString("ImportErrorTitle", R.string.ImportErrorTitle), LocaleController.getString("ImportErrorChatInvalidGroup", R.string.ImportErrorChatInvalidGroup));
                    return null;
                } else if (error.text.contains("CHAT_ADMIN_REQUIRED")) {
                    showSimpleAlert(fragment, LocaleController.getString("ImportErrorTitle", R.string.ImportErrorTitle), LocaleController.getString("ImportErrorNotAdmin", R.string.ImportErrorNotAdmin));
                    return null;
                } else if (error.text.startsWith("IMPORT_FORMAT")) {
                    showSimpleAlert(fragment, LocaleController.getString("ImportErrorTitle", R.string.ImportErrorTitle), LocaleController.getString("ImportErrorFileFormatInvalid", R.string.ImportErrorFileFormatInvalid));
                    return null;
                } else if (error.text.startsWith("PEER_ID_INVALID")) {
                    showSimpleAlert(fragment, LocaleController.getString("ImportErrorTitle", R.string.ImportErrorTitle), LocaleController.getString("ImportErrorPeerInvalid", R.string.ImportErrorPeerInvalid));
                    return null;
                } else if (error.text.contains("IMPORT_LANG_NOT_FOUND")) {
                    showSimpleAlert(fragment, LocaleController.getString("ImportErrorTitle", R.string.ImportErrorTitle), LocaleController.getString("ImportErrorFileLang", R.string.ImportErrorFileLang));
                    return null;
                } else if (error.text.contains("IMPORT_UPLOAD_FAILED")) {
                    showSimpleAlert(fragment, LocaleController.getString("ImportErrorTitle", R.string.ImportErrorTitle), LocaleController.getString("ImportFailedToUpload", R.string.ImportFailedToUpload));
                    return null;
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    showFloodWaitAlert(error.text, fragment);
                    return null;
                } else {
                    String string = LocaleController.getString("ImportErrorTitle", R.string.ImportErrorTitle);
                    showSimpleAlert(fragment, string, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text);
                    return null;
                }
            } else if ((request instanceof TLRPC.TL_account_saveSecureValue) || (request instanceof TLRPC.TL_account_getAuthorizationForm)) {
                if (error.text.contains("PHONE_NUMBER_INVALID")) {
                    showSimpleAlert(fragment, LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                    return null;
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
                    return null;
                } else if ("APP_VERSION_OUTDATED".equals(error.text)) {
                    showUpdateAppAlert(fragment.getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                    return null;
                } else {
                    showSimpleAlert(fragment, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text);
                    return null;
                }
            } else if ((request instanceof TLRPC.TL_channels_joinChannel) || (request instanceof TLRPC.TL_channels_editAdmin) || (request instanceof TLRPC.TL_channels_inviteToChannel) || (request instanceof TLRPC.TL_messages_addChatUser) || (request instanceof TLRPC.TL_messages_startBot) || (request instanceof TLRPC.TL_channels_editBanned) || (request instanceof TLRPC.TL_messages_editChatDefaultBannedRights) || (request instanceof TLRPC.TL_messages_editChatAdmin) || (request instanceof TLRPC.TL_messages_migrateChat) || (request instanceof TLRPC.TL_phone_inviteToGroupCall)) {
                if (fragment != null && error.text.equals("CHANNELS_TOO_MUCH")) {
                    if (fragment.getParentActivity() != null) {
                        fragment.showDialog(new LimitReachedBottomSheet(fragment, fragment.getParentActivity(), 5, currentAccount));
                        return null;
                    } else if ((request instanceof TLRPC.TL_channels_joinChannel) || (request instanceof TLRPC.TL_channels_inviteToChannel)) {
                        fragment.presentFragment(new TooManyCommunitiesActivity(0));
                        return null;
                    } else {
                        fragment.presentFragment(new TooManyCommunitiesActivity(1));
                        return null;
                    }
                } else if (fragment != null) {
                    showAddUserAlert(error.text, fragment, (args == null || args.length <= 0) ? false : ((Boolean) args[0]).booleanValue(), request);
                    return null;
                } else if (error.text.equals("PEER_FLOOD")) {
                    NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.needShowAlert, 1);
                    return null;
                } else {
                    return null;
                }
            } else if (request instanceof TLRPC.TL_messages_createChat) {
                if (error.text.equals("CHANNELS_TOO_MUCH")) {
                    if (fragment.getParentActivity() != null) {
                        fragment.showDialog(new LimitReachedBottomSheet(fragment, fragment.getParentActivity(), 5, currentAccount));
                    } else {
                        fragment.presentFragment(new TooManyCommunitiesActivity(2));
                    }
                    return null;
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    showFloodWaitAlert(error.text, fragment);
                    return null;
                } else {
                    showAddUserAlert(error.text, fragment, false, request);
                    return null;
                }
            } else if (request instanceof TLRPC.TL_channels_createChannel) {
                if (error.text.equals("CHANNELS_TOO_MUCH")) {
                    if (fragment.getParentActivity() != null) {
                        fragment.showDialog(new LimitReachedBottomSheet(fragment, fragment.getParentActivity(), 5, currentAccount));
                    } else {
                        fragment.presentFragment(new TooManyCommunitiesActivity(2));
                    }
                    return null;
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    showFloodWaitAlert(error.text, fragment);
                    return null;
                } else {
                    showAddUserAlert(error.text, fragment, false, request);
                    return null;
                }
            } else if (request instanceof TLRPC.TL_messages_editMessage) {
                if (!error.text.equals("MESSAGE_NOT_MODIFIED")) {
                    if (fragment != null) {
                        showSimpleAlert(fragment, LocaleController.getString("EditMessageError", R.string.EditMessageError));
                        return null;
                    }
                    showSimpleToast(null, LocaleController.getString("EditMessageError", R.string.EditMessageError));
                    return null;
                }
                return null;
            } else {
                char c4 = 65535;
                if ((request instanceof TLRPC.TL_messages_sendMessage) || (request instanceof TLRPC.TL_messages_sendMedia) || (request instanceof TLRPC.TL_messages_sendInlineBotResult) || (request instanceof TLRPC.TL_messages_forwardMessages) || (request instanceof TLRPC.TL_messages_sendMultiMedia) || (request instanceof TLRPC.TL_messages_sendScheduledMessages)) {
                    String str = error.text;
                    switch (str.hashCode()) {
                        case -1809401834:
                            if (str.equals("USER_BANNED_IN_CHANNEL")) {
                                c4 = 1;
                                break;
                            }
                            break;
                        case -454039871:
                            if (str.equals("PEER_FLOOD")) {
                                c4 = 0;
                                break;
                            }
                            break;
                        case 1169786080:
                            if (str.equals("SCHEDULE_TOO_MUCH")) {
                                c4 = 2;
                                break;
                            }
                            break;
                    }
                    switch (c4) {
                        case 0:
                            NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.needShowAlert, 0);
                            return null;
                        case 1:
                            NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.needShowAlert, 5);
                            return null;
                        case 2:
                            showSimpleToast(fragment, LocaleController.getString("MessageScheduledLimitReached", R.string.MessageScheduledLimitReached));
                            return null;
                        default:
                            return null;
                    }
                } else if (request instanceof TLRPC.TL_messages_importChatInvite) {
                    if (error.text.startsWith("FLOOD_WAIT")) {
                        showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
                        return null;
                    } else if (error.text.equals("USERS_TOO_MUCH")) {
                        showSimpleAlert(fragment, LocaleController.getString("JoinToGroupErrorFull", R.string.JoinToGroupErrorFull));
                        return null;
                    } else if (error.text.equals("CHANNELS_TOO_MUCH")) {
                        if (fragment.getParentActivity() != null) {
                            fragment.showDialog(new LimitReachedBottomSheet(fragment, fragment.getParentActivity(), 5, currentAccount));
                            return null;
                        }
                        fragment.presentFragment(new TooManyCommunitiesActivity(0));
                        return null;
                    } else if (error.text.equals("INVITE_HASH_EXPIRED")) {
                        showSimpleAlert(fragment, LocaleController.getString("ExpiredLink", R.string.ExpiredLink), LocaleController.getString("InviteExpired", R.string.InviteExpired));
                        return null;
                    } else {
                        showSimpleAlert(fragment, LocaleController.getString("JoinToGroupErrorNotExist", R.string.JoinToGroupErrorNotExist));
                        return null;
                    }
                } else if (request instanceof TLRPC.TL_messages_getAttachedStickers) {
                    if (fragment != null && fragment.getParentActivity() != null) {
                        Activity parentActivity = fragment.getParentActivity();
                        Toast.makeText(parentActivity, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text, 0).show();
                        return null;
                    }
                    return null;
                } else if ((request instanceof TLRPC.TL_account_confirmPhone) || (request instanceof TLRPC.TL_account_verifyPhone) || (request instanceof TLRPC.TL_account_verifyEmail)) {
                    if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID") || error.text.contains("CODE_INVALID") || error.text.contains("CODE_EMPTY")) {
                        return showSimpleAlert(fragment, LocaleController.getString("InvalidCode", R.string.InvalidCode));
                    }
                    if (error.text.contains("PHONE_CODE_EXPIRED") || error.text.contains("EMAIL_VERIFY_EXPIRED")) {
                        return showSimpleAlert(fragment, LocaleController.getString("CodeExpired", R.string.CodeExpired));
                    }
                    if (error.text.startsWith("FLOOD_WAIT")) {
                        return showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
                    }
                    return showSimpleAlert(fragment, error.text);
                } else if (request instanceof TLRPC.TL_auth_resendCode) {
                    if (error.text.contains("PHONE_NUMBER_INVALID")) {
                        return showSimpleAlert(fragment, LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                    }
                    if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                        return showSimpleAlert(fragment, LocaleController.getString("InvalidCode", R.string.InvalidCode));
                    }
                    if (error.text.contains("PHONE_CODE_EXPIRED")) {
                        return showSimpleAlert(fragment, LocaleController.getString("CodeExpired", R.string.CodeExpired));
                    }
                    if (error.text.startsWith("FLOOD_WAIT")) {
                        return showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
                    }
                    if (error.code != -1000) {
                        return showSimpleAlert(fragment, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text);
                    }
                    return null;
                } else if (request instanceof TLRPC.TL_account_sendConfirmPhoneCode) {
                    if (error.code == 400) {
                        return showSimpleAlert(fragment, LocaleController.getString("CancelLinkExpired", R.string.CancelLinkExpired));
                    }
                    if (error.text.startsWith("FLOOD_WAIT")) {
                        return showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
                    }
                    return showSimpleAlert(fragment, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                } else if (request instanceof TLRPC.TL_account_changePhone) {
                    if (error.text.contains("PHONE_NUMBER_INVALID")) {
                        showSimpleAlert(fragment, LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                        return null;
                    } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                        showSimpleAlert(fragment, LocaleController.getString("InvalidCode", R.string.InvalidCode));
                        return null;
                    } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                        showSimpleAlert(fragment, LocaleController.getString("CodeExpired", R.string.CodeExpired));
                        return null;
                    } else if (error.text.startsWith("FLOOD_WAIT")) {
                        showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
                        return null;
                    } else if (error.text.contains("FRESH_CHANGE_PHONE_FORBIDDEN")) {
                        showSimpleAlert(fragment, LocaleController.getString("FreshChangePhoneForbidden", R.string.FreshChangePhoneForbidden));
                        return null;
                    } else {
                        showSimpleAlert(fragment, error.text);
                        return null;
                    }
                } else if (request instanceof TLRPC.TL_account_sendChangePhoneCode) {
                    if (error.text.contains("PHONE_NUMBER_INVALID")) {
                        LoginActivity.needShowInvalidAlert(fragment, (String) args[0], false);
                        return null;
                    } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                        showSimpleAlert(fragment, LocaleController.getString("InvalidCode", R.string.InvalidCode));
                        return null;
                    } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                        showSimpleAlert(fragment, LocaleController.getString("CodeExpired", R.string.CodeExpired));
                        return null;
                    } else if (error.text.startsWith("FLOOD_WAIT")) {
                        showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
                        return null;
                    } else if (error.text.startsWith("PHONE_NUMBER_OCCUPIED")) {
                        showSimpleAlert(fragment, LocaleController.formatString("ChangePhoneNumberOccupied", R.string.ChangePhoneNumberOccupied, args[0]));
                        return null;
                    } else if (error.text.startsWith("PHONE_NUMBER_BANNED")) {
                        LoginActivity.needShowInvalidAlert(fragment, (String) args[0], true);
                        return null;
                    } else {
                        showSimpleAlert(fragment, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                        return null;
                    }
                } else if (request instanceof TLRPC.TL_updateUserName) {
                    String str2 = error.text;
                    switch (str2.hashCode()) {
                        case 288843630:
                            if (str2.equals("USERNAME_INVALID")) {
                                c3 = 0;
                                break;
                            }
                            c3 = 65535;
                            break;
                        case 533175271:
                            if (str2.equals("USERNAME_OCCUPIED")) {
                                c3 = 1;
                                break;
                            }
                            c3 = 65535;
                            break;
                        default:
                            c3 = 65535;
                            break;
                    }
                    switch (c3) {
                        case 0:
                            showSimpleAlert(fragment, LocaleController.getString("UsernameInvalid", R.string.UsernameInvalid));
                            return null;
                        case 1:
                            showSimpleAlert(fragment, LocaleController.getString("UsernameInUse", R.string.UsernameInUse));
                            return null;
                        default:
                            showSimpleAlert(fragment, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                            return null;
                    }
                } else if (request instanceof TLRPC.TL_contacts_importContacts) {
                    if (error.text.startsWith("FLOOD_WAIT")) {
                        showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
                        return null;
                    }
                    showSimpleAlert(fragment, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text);
                    return null;
                } else if ((request instanceof TLRPC.TL_account_getPassword) || (request instanceof TLRPC.TL_account_getTmpPassword)) {
                    if (error.text.startsWith("FLOOD_WAIT")) {
                        showSimpleToast(fragment, getFloodWaitString(error.text));
                        return null;
                    }
                    showSimpleToast(fragment, error.text);
                    return null;
                } else if (request instanceof TLRPC.TL_payments_sendPaymentForm) {
                    String str3 = error.text;
                    switch (str3.hashCode()) {
                        case -1144062453:
                            if (str3.equals("BOT_PRECHECKOUT_FAILED")) {
                                c2 = 0;
                                break;
                            }
                            c2 = 65535;
                            break;
                        case -784238410:
                            if (str3.equals("PAYMENT_FAILED")) {
                                c2 = 1;
                                break;
                            }
                            c2 = 65535;
                            break;
                        default:
                            c2 = 65535;
                            break;
                    }
                    switch (c2) {
                        case 0:
                            showSimpleToast(fragment, LocaleController.getString("PaymentPrecheckoutFailed", R.string.PaymentPrecheckoutFailed));
                            return null;
                        case 1:
                            showSimpleToast(fragment, LocaleController.getString("PaymentFailed", R.string.PaymentFailed));
                            return null;
                        default:
                            showSimpleToast(fragment, error.text);
                            return null;
                    }
                } else if (request instanceof TLRPC.TL_payments_validateRequestedInfo) {
                    String str4 = error.text;
                    switch (str4.hashCode()) {
                        case 1758025548:
                            if (str4.equals("SHIPPING_NOT_AVAILABLE")) {
                                c = 0;
                                break;
                            }
                        default:
                            c = 65535;
                            break;
                    }
                    switch (c) {
                        case 0:
                            showSimpleToast(fragment, LocaleController.getString("PaymentNoShippingMethod", R.string.PaymentNoShippingMethod));
                            return null;
                        default:
                            showSimpleToast(fragment, error.text);
                            return null;
                    }
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    public static Toast showSimpleToast(BaseFragment baseFragment, String text) {
        Context context;
        if (text == null) {
            return null;
        }
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            context = baseFragment.getParentActivity();
        } else {
            context = ApplicationLoader.applicationContext;
        }
        Toast toast = Toast.makeText(context, text, 1);
        toast.show();
        return toast;
    }

    public static AlertDialog showUpdateAppAlert(final Context context, String text, boolean updateApp) {
        if (context == null || text == null) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(text);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        if (updateApp) {
            builder.setNegativeButton(LocaleController.getString("UpdateApp", R.string.UpdateApp), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda32
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    Browser.openUrl(context, BuildVars.PLAYSTORE_APP_URL);
                }
            });
        }
        return builder.show();
    }

    public static AlertDialog.Builder createLanguageAlert(final LaunchActivity activity, final TLRPC.TL_langPackLanguage language) {
        String str;
        int end;
        if (language == null) {
            return null;
        }
        language.lang_code = language.lang_code.replace('-', '_').toLowerCase();
        language.plural_code = language.plural_code.replace('-', '_').toLowerCase();
        if (language.base_lang_code != null) {
            language.base_lang_code = language.base_lang_code.replace('-', '_').toLowerCase();
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LocaleController.LocaleInfo currentInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        if (currentInfo.shortName.equals(language.lang_code)) {
            builder.setTitle(LocaleController.getString("Language", R.string.Language));
            str = LocaleController.formatString("LanguageSame", R.string.LanguageSame, language.name);
            builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
            builder.setNeutralButton(LocaleController.getString("SETTINGS", R.string.SETTINGS), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda53
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LaunchActivity.this.m3653lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new LanguageSelectActivity());
                }
            });
        } else if (language.strings_count == 0) {
            builder.setTitle(LocaleController.getString("LanguageUnknownTitle", R.string.LanguageUnknownTitle));
            str = LocaleController.formatString("LanguageUnknownCustomAlert", R.string.LanguageUnknownCustomAlert, language.name);
            builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
        } else {
            builder.setTitle(LocaleController.getString("LanguageTitle", R.string.LanguageTitle));
            if (language.official) {
                str = LocaleController.formatString("LanguageAlert", R.string.LanguageAlert, language.name, Integer.valueOf((int) Math.ceil((language.translated_count / language.strings_count) * 100.0f)));
            } else {
                str = LocaleController.formatString("LanguageCustomAlert", R.string.LanguageCustomAlert, language.name, Integer.valueOf((int) Math.ceil((language.translated_count / language.strings_count) * 100.0f)));
            }
            builder.setPositiveButton(LocaleController.getString("Change", R.string.Change), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda43
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    AlertsCreator.lambda$createLanguageAlert$8(TLRPC.TL_langPackLanguage.this, activity, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        }
        SpannableStringBuilder spanned = new SpannableStringBuilder(AndroidUtilities.replaceTags(str));
        int start = TextUtils.indexOf((CharSequence) spanned, '[');
        if (start != -1) {
            end = TextUtils.indexOf((CharSequence) spanned, ']', start + 1);
            if (end != -1) {
                spanned.delete(end, end + 1);
                spanned.delete(start, start + 1);
            }
        } else {
            end = -1;
        }
        if (start != -1 && end != -1) {
            spanned.setSpan(new URLSpanNoUnderline(language.translations_url) { // from class: org.telegram.ui.Components.AlertsCreator.1
                @Override // org.telegram.ui.Components.URLSpanNoUnderline, android.text.style.URLSpan, android.text.style.ClickableSpan
                public void onClick(View widget) {
                    builder.getDismissRunnable().run();
                    super.onClick(widget);
                }
            }, start, end - 1, 33);
        }
        TextView message = new TextView(activity);
        message.setText(spanned);
        message.setTextSize(1, 16.0f);
        message.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink));
        message.setHighlightColor(Theme.getColor(Theme.key_dialogLinkSelection));
        message.setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
        message.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        message.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        builder.setView(message);
        return builder;
    }

    public static /* synthetic */ void lambda$createLanguageAlert$8(TLRPC.TL_langPackLanguage language, LaunchActivity activity, DialogInterface dialogInterface, int i) {
        String key;
        if (language.official) {
            key = "remote_" + language.lang_code;
        } else {
            key = "unofficial_" + language.lang_code;
        }
        LocaleController.LocaleInfo localeInfo = LocaleController.getInstance().getLanguageFromDict(key);
        if (localeInfo == null) {
            localeInfo = new LocaleController.LocaleInfo();
            localeInfo.name = language.native_name;
            localeInfo.nameEnglish = language.name;
            localeInfo.shortName = language.lang_code;
            localeInfo.baseLangCode = language.base_lang_code;
            localeInfo.pluralLangCode = language.plural_code;
            localeInfo.isRtl = language.rtl;
            if (language.official) {
                localeInfo.pathToFile = "remote";
            } else {
                localeInfo.pathToFile = "unofficial";
            }
        }
        LocaleController.getInstance().applyLanguage(localeInfo, true, false, false, true, UserConfig.selectedAccount);
        activity.rebuildAllFragments(true);
    }

    public static boolean checkSlowMode(Context context, int currentAccount, long did, boolean few) {
        TLRPC.Chat chat;
        if (DialogObject.isChatDialog(did) && (chat = MessagesController.getInstance(currentAccount).getChat(Long.valueOf(-did))) != null && chat.slowmode_enabled && !ChatObject.hasAdminRights(chat)) {
            if (!few) {
                TLRPC.ChatFull chatFull = MessagesController.getInstance(currentAccount).getChatFull(chat.id);
                if (chatFull == null) {
                    chatFull = MessagesStorage.getInstance(currentAccount).loadChatInfo(chat.id, ChatObject.isChannel(chat), new CountDownLatch(1), false, false);
                }
                if (chatFull != null && chatFull.slowmode_next_send_date >= ConnectionsManager.getInstance(currentAccount).getCurrentTime()) {
                    few = true;
                }
            }
            if (few) {
                createSimpleAlert(context, chat.title, LocaleController.getString("SlowmodeSendError", R.string.SlowmodeSendError)).show();
                return true;
            }
            return false;
        }
        return false;
    }

    public static AlertDialog.Builder createSimpleAlert(Context context, String text) {
        return createSimpleAlert(context, null, text);
    }

    public static AlertDialog.Builder createSimpleAlert(Context context, String title, String text) {
        return createSimpleAlert(context, title, text, null);
    }

    public static AlertDialog.Builder createSimpleAlert(Context context, String title, String text, Theme.ResourcesProvider resourcesProvider) {
        if (context == null || text == null) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title == null ? LocaleController.getString("AppName", R.string.AppName) : title);
        builder.setMessage(text);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        return builder;
    }

    public static Dialog showSimpleAlert(BaseFragment baseFragment, String text) {
        return showSimpleAlert(baseFragment, null, text);
    }

    public static Dialog showSimpleAlert(BaseFragment baseFragment, String title, String text) {
        return showSimpleAlert(baseFragment, title, text, null);
    }

    public static Dialog showSimpleAlert(BaseFragment baseFragment, String title, String text, Theme.ResourcesProvider resourcesProvider) {
        if (text == null || baseFragment == null || baseFragment.getParentActivity() == null) {
            return null;
        }
        AlertDialog.Builder builder = createSimpleAlert(baseFragment.getParentActivity(), title, text, resourcesProvider);
        Dialog dialog = builder.create();
        baseFragment.showDialog(dialog);
        return dialog;
    }

    public static void showBlockReportSpamReplyAlert(final ChatActivity fragment, final MessageObject messageObject, long peerId, final Theme.ResourcesProvider resourcesProvider, final Runnable hideDim) {
        if (fragment == null || fragment.getParentActivity() == null || messageObject == null) {
            return;
        }
        final AccountInstance accountInstance = fragment.getAccountInstance();
        final TLRPC.User user = peerId > 0 ? accountInstance.getMessagesController().getUser(Long.valueOf(peerId)) : null;
        final TLRPC.Chat chat = peerId < 0 ? accountInstance.getMessagesController().getChat(Long.valueOf(-peerId)) : null;
        if (user == null && chat == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getParentActivity(), resourcesProvider);
        builder.setDimEnabled(hideDim == null);
        builder.setOnPreDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda66
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                AlertsCreator.lambda$showBlockReportSpamReplyAlert$9(hideDim, dialogInterface);
            }
        });
        builder.setTitle(LocaleController.getString("BlockUser", R.string.BlockUser));
        if (user != null) {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BlockUserReplyAlert", R.string.BlockUserReplyAlert, UserObject.getFirstName(user))));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BlockUserReplyAlert", R.string.BlockUserReplyAlert, chat.title)));
        }
        LinearLayout linearLayout = new LinearLayout(fragment.getParentActivity());
        linearLayout.setOrientation(1);
        final CheckBoxCell[] cells = {new CheckBoxCell(fragment.getParentActivity(), 1, resourcesProvider)};
        cells[0].setBackgroundDrawable(Theme.getSelectorDrawable(false));
        cells[0].setTag(0);
        cells[0].setText(LocaleController.getString("DeleteReportSpam", R.string.DeleteReportSpam), "", true, false);
        cells[0].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
        linearLayout.addView(cells[0], LayoutHelper.createLinear(-1, -2));
        cells[0].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda91
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AlertsCreator.lambda$showBlockReportSpamReplyAlert$10(cells, view);
            }
        });
        builder.setCustomViewOffset(12);
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("BlockAndDeleteReplies", R.string.BlockAndDeleteReplies), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda44
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$showBlockReportSpamReplyAlert$12(TLRPC.User.this, accountInstance, fragment, chat, messageObject, cells, resourcesProvider, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        AlertDialog dialog = builder.create();
        fragment.showDialog(dialog);
        TextView button = (TextView) dialog.getButton(-1);
        if (button != null) {
            button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
        }
    }

    public static /* synthetic */ void lambda$showBlockReportSpamReplyAlert$9(Runnable hideDim, DialogInterface di) {
        if (hideDim != null) {
            hideDim.run();
        }
    }

    public static /* synthetic */ void lambda$showBlockReportSpamReplyAlert$10(CheckBoxCell[] cells, View v) {
        Integer num = (Integer) v.getTag();
        cells[num.intValue()].setChecked(!cells[num.intValue()].isChecked(), true);
    }

    public static /* synthetic */ void lambda$showBlockReportSpamReplyAlert$12(TLRPC.User user, final AccountInstance accountInstance, ChatActivity fragment, TLRPC.Chat chat, MessageObject messageObject, CheckBoxCell[] cells, Theme.ResourcesProvider resourcesProvider, DialogInterface dialogInterface, int i) {
        if (user != null) {
            accountInstance.getMessagesStorage().deleteUserChatHistory(fragment.getDialogId(), user.id);
        } else {
            accountInstance.getMessagesStorage().deleteUserChatHistory(fragment.getDialogId(), -chat.id);
        }
        TLRPC.TL_contacts_blockFromReplies request = new TLRPC.TL_contacts_blockFromReplies();
        request.msg_id = messageObject.getId();
        request.delete_message = true;
        request.delete_history = true;
        if (cells[0].isChecked()) {
            request.report_spam = true;
            if (fragment.getParentActivity() != null) {
                if (fragment instanceof ChatActivity) {
                    fragment.getUndoView().showWithAction(0L, 74, (Runnable) null);
                } else if (fragment == null) {
                    Toast.makeText(fragment.getParentActivity(), LocaleController.getString("ReportChatSent", R.string.ReportChatSent), 0).show();
                } else {
                    BulletinFactory.of(fragment).createReportSent(resourcesProvider).show();
                }
            }
        }
        accountInstance.getConnectionsManager().sendRequest(request, new RequestDelegate() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda122
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                AlertsCreator.lambda$showBlockReportSpamReplyAlert$11(AccountInstance.this, tLObject, tL_error);
            }
        });
    }

    public static /* synthetic */ void lambda$showBlockReportSpamReplyAlert$11(AccountInstance accountInstance, TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.Updates) {
            accountInstance.getMessagesController().processUpdates((TLRPC.Updates) response, false);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x004f  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x0147  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x0202  */
    /* JADX WARN: Removed duplicated region for block: B:59:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void showBlockReportSpamAlert(org.telegram.ui.ActionBar.BaseFragment r21, final long r22, final org.telegram.tgnet.TLRPC.User r24, final org.telegram.tgnet.TLRPC.Chat r25, final org.telegram.tgnet.TLRPC.EncryptedChat r26, final boolean r27, org.telegram.tgnet.TLRPC.ChatFull r28, final org.telegram.messenger.MessagesStorage.IntCallback r29, org.telegram.ui.ActionBar.Theme.ResourcesProvider r30) {
        /*
            Method dump skipped, instructions count: 525
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.showBlockReportSpamAlert(org.telegram.ui.ActionBar.BaseFragment, long, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$EncryptedChat, boolean, org.telegram.tgnet.TLRPC$ChatFull, org.telegram.messenger.MessagesStorage$IntCallback, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    public static /* synthetic */ void lambda$showBlockReportSpamAlert$13(CheckBoxCell[] cells, View v) {
        Integer num = (Integer) v.getTag();
        cells[num.intValue()].setChecked(!cells[num.intValue()].isChecked(), true);
    }

    public static /* synthetic */ void lambda$showBlockReportSpamAlert$14(TLRPC.User currentUser, AccountInstance accountInstance, CheckBoxCell[] cells, long dialog_id, TLRPC.Chat currentChat, TLRPC.EncryptedChat encryptedChat, boolean isLocation, MessagesStorage.IntCallback callback, DialogInterface dialogInterface, int i) {
        if (currentUser != null) {
            accountInstance.getMessagesController().blockPeer(currentUser.id);
        }
        if (cells == null || (cells[0] != null && cells[0].isChecked())) {
            accountInstance.getMessagesController().reportSpam(dialog_id, currentUser, currentChat, encryptedChat, currentChat != null && isLocation);
        }
        if (cells == null || cells[1].isChecked()) {
            if (currentChat != null) {
                if (ChatObject.isNotInChat(currentChat)) {
                    accountInstance.getMessagesController().deleteDialog(dialog_id, 0);
                } else {
                    accountInstance.getMessagesController().deleteParticipantFromChat(-dialog_id, accountInstance.getMessagesController().getUser(Long.valueOf(accountInstance.getUserConfig().getClientUserId())), null);
                }
            } else {
                accountInstance.getMessagesController().deleteDialog(dialog_id, 0);
            }
            callback.run(1);
            return;
        }
        callback.run(0);
    }

    public static void showCustomNotificationsDialog(BaseFragment parentFragment, long did, int globalType, ArrayList<NotificationsSettingsActivity.NotificationException> exceptions, int currentAccount, MessagesStorage.IntCallback callback) {
        showCustomNotificationsDialog(parentFragment, did, globalType, exceptions, currentAccount, callback, null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v0 */
    /* JADX WARN: Type inference failed for: r10v1, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r10v2 */
    /* JADX WARN: Type inference failed for: r15v0 */
    /* JADX WARN: Type inference failed for: r15v1, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r15v2 */
    public static void showCustomNotificationsDialog(final BaseFragment parentFragment, final long did, final int globalType, final ArrayList<NotificationsSettingsActivity.NotificationException> exceptions, final int currentAccount, final MessagesStorage.IntCallback callback, final MessagesStorage.IntCallback resultCallback) {
        Drawable drawable;
        String[] descriptions;
        boolean defaultEnabled;
        final AlertDialog.Builder builder;
        LinearLayout linearLayout;
        int a;
        if (parentFragment == null || parentFragment.getParentActivity() == null) {
            return;
        }
        boolean defaultEnabled2 = NotificationsController.getInstance(currentAccount).isGlobalNotificationsEnabled(did);
        String[] strArr = new String[5];
        ?? r15 = 0;
        strArr[0] = LocaleController.getString("NotificationsTurnOn", R.string.NotificationsTurnOn);
        ?? r10 = 1;
        strArr[1] = LocaleController.formatString("MuteFor", R.string.MuteFor, LocaleController.formatPluralString("Hours", 1, new Object[0]));
        strArr[2] = LocaleController.formatString("MuteFor", R.string.MuteFor, LocaleController.formatPluralString("Days", 2, new Object[0]));
        Drawable drawable2 = null;
        strArr[3] = (did != 0 || !(parentFragment instanceof NotificationsCustomSettingsActivity)) ? LocaleController.getString("NotificationsCustomize", R.string.NotificationsCustomize) : null;
        strArr[4] = LocaleController.getString("NotificationsTurnOff", R.string.NotificationsTurnOff);
        String[] descriptions2 = strArr;
        int[] icons = {R.drawable.notifications_on, R.drawable.notifications_mute1h, R.drawable.notifications_mute2d, R.drawable.notifications_settings, R.drawable.notifications_off};
        LinearLayout linearLayout2 = new LinearLayout(parentFragment.getParentActivity());
        linearLayout2.setOrientation(1);
        AlertDialog.Builder builder2 = new AlertDialog.Builder(parentFragment.getParentActivity());
        int a2 = 0;
        while (a2 < descriptions2.length) {
            if (descriptions2[a2] == null) {
                a = a2;
                builder = builder2;
                descriptions = descriptions2;
                drawable = drawable2;
                defaultEnabled = defaultEnabled2;
                linearLayout = linearLayout2;
            } else {
                TextView textView = new TextView(parentFragment.getParentActivity());
                Drawable drawable3 = parentFragment.getParentActivity().getResources().getDrawable(icons[a2]);
                int length = descriptions2.length;
                int i = r10 == true ? 1 : 0;
                int i2 = r10 == true ? 1 : 0;
                if (a2 == length - i) {
                    textView.setTextColor(Theme.getColor(Theme.key_dialogTextRed));
                    drawable3.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogRedIcon), PorterDuff.Mode.MULTIPLY));
                } else {
                    textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                    drawable3.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogIcon), PorterDuff.Mode.MULTIPLY));
                }
                textView.setTextSize(r10, 16.0f);
                textView.setLines(r10);
                textView.setMaxLines(r10);
                textView.setCompoundDrawablesWithIntrinsicBounds(drawable3, drawable2, drawable2, drawable2);
                textView.setTag(Integer.valueOf(a2));
                textView.setBackgroundDrawable(Theme.getSelectorDrawable(r15));
                int dp = AndroidUtilities.dp(24.0f);
                int dp2 = AndroidUtilities.dp(24.0f);
                int i3 = r15 == true ? 1 : 0;
                int i4 = r15 == true ? 1 : 0;
                textView.setPadding(dp, i3, dp2, r15);
                textView.setSingleLine(r10);
                textView.setGravity(19);
                textView.setCompoundDrawablePadding(AndroidUtilities.dp(26.0f));
                textView.setText(descriptions2[a2]);
                linearLayout2.addView(textView, LayoutHelper.createLinear(-1, 48, 51));
                final boolean z = defaultEnabled2;
                a = a2;
                builder = builder2;
                defaultEnabled = defaultEnabled2;
                linearLayout = linearLayout2;
                descriptions = descriptions2;
                drawable = drawable2;
                textView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda70
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        AlertsCreator.lambda$showCustomNotificationsDialog$15(did, currentAccount, z, resultCallback, globalType, parentFragment, exceptions, callback, builder, view);
                    }
                });
            }
            a2 = a + 1;
            linearLayout2 = linearLayout;
            builder2 = builder;
            defaultEnabled2 = defaultEnabled;
            descriptions2 = descriptions;
            drawable2 = drawable;
            r10 = 1;
            r15 = 0;
        }
        AlertDialog.Builder builder3 = builder2;
        builder3.setTitle(LocaleController.getString("Notifications", R.string.Notifications));
        builder3.setView(linearLayout2);
        parentFragment.showDialog(builder3.create());
    }

    public static /* synthetic */ void lambda$showCustomNotificationsDialog$15(long did, int currentAccount, boolean defaultEnabled, MessagesStorage.IntCallback resultCallback, int globalType, BaseFragment parentFragment, ArrayList exceptions, MessagesStorage.IntCallback callback, AlertDialog.Builder builder, View v) {
        int i = ((Integer) v.getTag()).intValue();
        if (i == 0) {
            if (did != 0) {
                SharedPreferences preferences = MessagesController.getNotificationsSettings(currentAccount);
                SharedPreferences.Editor editor = preferences.edit();
                if (defaultEnabled) {
                    editor.remove("notify2_" + did);
                } else {
                    editor.putInt("notify2_" + did, 0);
                }
                MessagesStorage.getInstance(currentAccount).setDialogFlags(did, 0L);
                editor.commit();
                TLRPC.Dialog dialog = MessagesController.getInstance(currentAccount).dialogs_dict.get(did);
                if (dialog != null) {
                    dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                }
                NotificationsController.getInstance(currentAccount).updateServerNotificationsSettings(did);
                if (resultCallback != null) {
                    if (defaultEnabled) {
                        resultCallback.run(0);
                    } else {
                        resultCallback.run(1);
                    }
                }
            } else {
                NotificationsController.getInstance(currentAccount).setGlobalNotificationsEnabled(globalType, 0);
            }
        } else if (i == 3) {
            if (did != 0) {
                Bundle args = new Bundle();
                args.putLong("dialog_id", did);
                parentFragment.presentFragment(new ProfileNotificationsActivity(args));
            } else {
                parentFragment.presentFragment(new NotificationsCustomSettingsActivity(globalType, exceptions));
            }
        } else {
            int untilTime = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
            if (i == 1) {
                untilTime += 3600;
            } else if (i == 2) {
                untilTime += 172800;
            } else if (i == 4) {
                untilTime = Integer.MAX_VALUE;
            }
            NotificationsController.getInstance(currentAccount).muteUntil(did, untilTime);
            if (did != 0 && resultCallback != null) {
                if (i == 4 && !defaultEnabled) {
                    resultCallback.run(0);
                } else {
                    resultCallback.run(1);
                }
            }
            if (did == 0) {
                NotificationsController.getInstance(currentAccount).setGlobalNotificationsEnabled(globalType, Integer.MAX_VALUE);
            }
        }
        if (callback != null) {
            callback.run(i);
        }
        builder.getDismissRunnable().run();
        int setting = -1;
        if (i == 0) {
            setting = 4;
        } else if (i == 1) {
            setting = 0;
        } else if (i == 2) {
            setting = 2;
        } else if (i == 4) {
            setting = 3;
        }
        if (setting >= 0 && BulletinFactory.canShowBulletin(parentFragment)) {
            BulletinFactory.createMuteBulletin(parentFragment, setting).show();
        }
    }

    public static AlertDialog showSecretLocationAlert(Context context, int currentAccount, final Runnable onSelectRunnable, boolean inChat, Theme.ResourcesProvider resourcesProvider) {
        ArrayList<String> arrayList = new ArrayList<>();
        final ArrayList<Integer> types = new ArrayList<>();
        int providers = MessagesController.getInstance(currentAccount).availableMapProviders;
        if ((providers & 1) != 0) {
            arrayList.add(LocaleController.getString("MapPreviewProviderTelegram", R.string.MapPreviewProviderTelegram));
            types.add(0);
        }
        if ((providers & 2) != 0) {
            arrayList.add(LocaleController.getString("MapPreviewProviderGoogle", R.string.MapPreviewProviderGoogle));
            types.add(1);
        }
        if ((providers & 4) != 0) {
            arrayList.add(LocaleController.getString("MapPreviewProviderYandex", R.string.MapPreviewProviderYandex));
            types.add(3);
        }
        arrayList.add(LocaleController.getString("MapPreviewProviderNobody", R.string.MapPreviewProviderNobody));
        types.add(2);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, resourcesProvider);
        builder.setTitle(LocaleController.getString("MapPreviewProviderTitle", R.string.MapPreviewProviderTitle));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        builder.setView(linearLayout);
        for (int a = 0; a < arrayList.size(); a++) {
            RadioColorCell cell = new RadioColorCell(context, resourcesProvider);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            cell.setTextAndValue(arrayList.get(a), SharedConfig.mapPreviewType == types.get(a).intValue());
            linearLayout.addView(cell);
            cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda74
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AlertsCreator.lambda$showSecretLocationAlert$16(types, onSelectRunnable, builder, view);
                }
            });
        }
        if (!inChat) {
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        }
        AlertDialog dialog = builder.show();
        if (inChat) {
            dialog.setCanceledOnTouchOutside(false);
        }
        return dialog;
    }

    public static /* synthetic */ void lambda$showSecretLocationAlert$16(ArrayList types, Runnable onSelectRunnable, AlertDialog.Builder builder, View v) {
        Integer which = (Integer) v.getTag();
        SharedConfig.setSecretMapPreviewType(((Integer) types.get(which.intValue())).intValue());
        if (onSelectRunnable != null) {
            onSelectRunnable.run();
        }
        builder.getDismissRunnable().run();
    }

    public static void updateDayPicker(NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2, monthPicker.getValue());
        calendar.set(1, yearPicker.getValue());
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(calendar.getActualMaximum(5));
    }

    private static void checkPickerDate(NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentYear = calendar.get(1);
        int currentMonth = calendar.get(2);
        int currentDay = calendar.get(5);
        if (currentYear > yearPicker.getValue()) {
            yearPicker.setValue(currentYear);
        }
        if (yearPicker.getValue() == currentYear) {
            if (currentMonth > monthPicker.getValue()) {
                monthPicker.setValue(currentMonth);
            }
            if (currentMonth == monthPicker.getValue() && currentDay > dayPicker.getValue()) {
                dayPicker.setValue(currentDay);
            }
        }
    }

    public static void showOpenUrlAlert(BaseFragment fragment, String url, boolean punycode, boolean ask) {
        showOpenUrlAlert(fragment, url, punycode, true, ask, null);
    }

    public static void showOpenUrlAlert(BaseFragment fragment, String url, boolean punycode, boolean ask, Theme.ResourcesProvider resourcesProvider) {
        showOpenUrlAlert(fragment, url, punycode, true, ask, resourcesProvider);
    }

    public static void showOpenUrlAlert(final BaseFragment fragment, final String url, boolean punycode, final boolean tryTelegraph, boolean ask, Theme.ResourcesProvider resourcesProvider) {
        String urlFinal;
        if (fragment != null && fragment.getParentActivity() != null) {
            final long inlineReturn = fragment instanceof ChatActivity ? ((ChatActivity) fragment).getInlineReturn() : 0L;
            boolean z = true;
            if (!Browser.isInternalUrl(url, null) && ask) {
                if (punycode) {
                    try {
                        Uri uri = Uri.parse(url);
                        String host = IDN.toASCII(uri.getHost(), 1);
                        urlFinal = uri.getScheme() + "://" + host + uri.getPath();
                    } catch (Exception e) {
                        FileLog.e(e);
                        urlFinal = url;
                    }
                } else {
                    urlFinal = url;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getParentActivity(), resourcesProvider);
                builder.setTitle(LocaleController.getString("OpenUrlTitle", R.string.OpenUrlTitle));
                String format = LocaleController.getString("OpenUrlAlert2", R.string.OpenUrlAlert2);
                int index = format.indexOf("%");
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(String.format(format, urlFinal));
                if (index >= 0) {
                    stringBuilder.setSpan(new URLSpan(urlFinal), index, urlFinal.length() + index, 33);
                }
                builder.setMessage(stringBuilder);
                builder.setMessageTextViewClickable(false);
                builder.setPositiveButton(LocaleController.getString("Open", R.string.Open), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda48
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        BaseFragment baseFragment = BaseFragment.this;
                        String str = url;
                        long j = inlineReturn;
                        Browser.openUrl(baseFragment.getParentActivity(), str, inlineReturn == 0, tryTelegraph);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                fragment.showDialog(builder.create());
                return;
            }
            Activity parentActivity = fragment.getParentActivity();
            if (inlineReturn != 0) {
                z = false;
            }
            Browser.openUrl(parentActivity, url, z, tryTelegraph);
        }
    }

    public static AlertDialog createSupportAlert(final BaseFragment fragment, Theme.ResourcesProvider resourcesProvider) {
        if (fragment == null || fragment.getParentActivity() == null) {
            return null;
        }
        TextView message = new TextView(fragment.getParentActivity());
        Spannable spanned = new SpannableString(Html.fromHtml(LocaleController.getString("AskAQuestionInfo", R.string.AskAQuestionInfo).replace("\n", "<br>")));
        URLSpan[] spans = (URLSpan[]) spanned.getSpans(0, spanned.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = spanned.getSpanStart(span);
            int end = spanned.getSpanEnd(span);
            spanned.removeSpan(span);
            spanned.setSpan(new URLSpanNoUnderline(span.getURL()) { // from class: org.telegram.ui.Components.AlertsCreator.2
                @Override // org.telegram.ui.Components.URLSpanNoUnderline, android.text.style.URLSpan, android.text.style.ClickableSpan
                public void onClick(View widget) {
                    fragment.dismissCurrentDialog();
                    super.onClick(widget);
                }
            }, start, end, 0);
        }
        message.setText(spanned);
        message.setTextSize(1, 16.0f);
        message.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink, resourcesProvider));
        message.setHighlightColor(Theme.getColor(Theme.key_dialogLinkSelection, resourcesProvider));
        message.setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
        message.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        message.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
        AlertDialog.Builder builder1 = new AlertDialog.Builder(fragment.getParentActivity(), resourcesProvider);
        builder1.setView(message);
        builder1.setTitle(LocaleController.getString("AskAQuestion", R.string.AskAQuestion));
        builder1.setPositiveButton(LocaleController.getString("AskButton", R.string.AskButton), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda46
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.performAskAQuestion(BaseFragment.this);
            }
        });
        builder1.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        return builder1.create();
    }

    public static void performAskAQuestion(final BaseFragment fragment) {
        String userString;
        final int currentAccount = fragment.getCurrentAccount();
        final SharedPreferences preferences = MessagesController.getMainSettings(currentAccount);
        long uid = AndroidUtilities.getPrefIntOrLong(preferences, "support_id2", 0L);
        TLRPC.User supportUser = null;
        if (uid != 0 && (supportUser = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(uid))) == null && (userString = preferences.getString("support_user", null)) != null) {
            try {
                byte[] datacentersBytes = Base64.decode(userString, 0);
                if (datacentersBytes != null) {
                    SerializedData data = new SerializedData(datacentersBytes);
                    supportUser = TLRPC.User.TLdeserialize(data, data.readInt32(false), false);
                    if (supportUser != null && supportUser.id == 333000) {
                        supportUser = null;
                    }
                    data.cleanup();
                }
            } catch (Exception e) {
                FileLog.e(e);
                supportUser = null;
            }
        }
        if (supportUser == null) {
            final AlertDialog progressDialog = new AlertDialog(fragment.getParentActivity(), 3);
            progressDialog.setCanCancel(false);
            progressDialog.show();
            TLRPC.TL_help_getSupport req = new TLRPC.TL_help_getSupport();
            ConnectionsManager.getInstance(currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda121
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    AlertsCreator.lambda$performAskAQuestion$21(preferences, progressDialog, currentAccount, fragment, tLObject, tL_error);
                }
            });
            return;
        }
        MessagesController.getInstance(currentAccount).putUser(supportUser, true);
        Bundle args = new Bundle();
        args.putLong("user_id", supportUser.id);
        fragment.presentFragment(new ChatActivity(args));
    }

    public static /* synthetic */ void lambda$performAskAQuestion$21(final SharedPreferences preferences, final AlertDialog progressDialog, final int currentAccount, final BaseFragment fragment, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            final TLRPC.TL_help_support res = (TLRPC.TL_help_support) response;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda112
                @Override // java.lang.Runnable
                public final void run() {
                    AlertsCreator.lambda$performAskAQuestion$19(preferences, res, progressDialog, currentAccount, fragment);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda113
            @Override // java.lang.Runnable
            public final void run() {
                AlertsCreator.lambda$performAskAQuestion$20(AlertDialog.this);
            }
        });
    }

    public static /* synthetic */ void lambda$performAskAQuestion$19(SharedPreferences preferences, TLRPC.TL_help_support res, AlertDialog progressDialog, int currentAccount, BaseFragment fragment) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("support_id2", res.user.id);
        SerializedData data = new SerializedData();
        res.user.serializeToStream(data);
        editor.putString("support_user", Base64.encodeToString(data.toByteArray(), 0));
        editor.commit();
        data.cleanup();
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        ArrayList<TLRPC.User> users = new ArrayList<>();
        users.add(res.user);
        MessagesStorage.getInstance(currentAccount).putUsersAndChats(users, null, true, true);
        MessagesController.getInstance(currentAccount).putUser(res.user, false);
        Bundle args = new Bundle();
        args.putLong("user_id", res.user.id);
        fragment.presentFragment(new ChatActivity(args));
    }

    public static /* synthetic */ void lambda$performAskAQuestion$20(AlertDialog progressDialog) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void createImportDialogAlert(BaseFragment fragment, String title, String message, TLRPC.User user, TLRPC.Chat chat, final Runnable onProcessRunnable) {
        if (fragment == null || fragment.getParentActivity() == null) {
            return;
        }
        if (chat == null && user == null) {
            return;
        }
        int account = fragment.getCurrentAccount();
        Context context = fragment.getParentActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        long selfUserId = UserConfig.getInstance(account).getClientUserId();
        TextView messageTextView = new TextView(context);
        messageTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        messageTextView.setTextSize(1, 16.0f);
        messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        FrameLayout frameLayout = new FrameLayout(context);
        builder.setView(frameLayout);
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
        BackupImageView imageView = new BackupImageView(context);
        imageView.setRoundRadius(AndroidUtilities.dp(20.0f));
        frameLayout.addView(imageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
        TextView textView = new TextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setText(LocaleController.getString("ImportMessages", R.string.ImportMessages));
        int i = (LocaleController.isRTL ? 5 : 3) | 48;
        int i2 = 21;
        float f = LocaleController.isRTL ? 21 : 76;
        if (LocaleController.isRTL) {
            i2 = 76;
        }
        frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, i, f, 11.0f, i2, 0.0f));
        frameLayout.addView(messageTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 57.0f, 24.0f, 9.0f));
        if (user != null) {
            if (UserObject.isReplyUser(user)) {
                avatarDrawable.setSmallSize(true);
                avatarDrawable.setAvatarType(12);
                imageView.setImage((ImageLocation) null, (String) null, avatarDrawable, user);
            } else if (user.id == selfUserId) {
                avatarDrawable.setSmallSize(true);
                avatarDrawable.setAvatarType(1);
                imageView.setImage((ImageLocation) null, (String) null, avatarDrawable, user);
            } else {
                avatarDrawable.setSmallSize(false);
                avatarDrawable.setInfo(user);
                imageView.setForUserOrChat(user, avatarDrawable);
            }
        } else {
            avatarDrawable.setInfo(chat);
            imageView.setForUserOrChat(chat, avatarDrawable);
        }
        messageTextView.setText(AndroidUtilities.replaceTags(message));
        builder.setPositiveButton(LocaleController.getString("Import", R.string.Import), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda35
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i3) {
                AlertsCreator.lambda$createImportDialogAlert$22(onProcessRunnable, dialogInterface, i3);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        AlertDialog alertDialog = builder.create();
        fragment.showDialog(alertDialog);
    }

    public static /* synthetic */ void lambda$createImportDialogAlert$22(Runnable onProcessRunnable, DialogInterface dialogInterface, int i) {
        if (onProcessRunnable != null) {
            onProcessRunnable.run();
        }
    }

    public static void createClearOrDeleteDialogAlert(BaseFragment fragment, boolean clear, TLRPC.Chat chat, TLRPC.User user, boolean secret, boolean canDeleteHistory, MessagesStorage.BooleanCallback onProcessRunnable) {
        createClearOrDeleteDialogAlert(fragment, clear, false, false, chat, user, secret, false, canDeleteHistory, onProcessRunnable, null);
    }

    public static void createClearOrDeleteDialogAlert(BaseFragment fragment, boolean clear, TLRPC.Chat chat, TLRPC.User user, boolean secret, boolean checkDeleteForAll, boolean canDeleteHistory, MessagesStorage.BooleanCallback onProcessRunnable) {
        createClearOrDeleteDialogAlert(fragment, clear, chat != null && chat.creator, false, chat, user, secret, checkDeleteForAll, canDeleteHistory, onProcessRunnable, null);
    }

    public static void createClearOrDeleteDialogAlert(BaseFragment fragment, boolean clear, TLRPC.Chat chat, TLRPC.User user, boolean secret, boolean checkDeleteForAll, boolean canDeleteHistory, MessagesStorage.BooleanCallback onProcessRunnable, Theme.ResourcesProvider resourcesProvider) {
        createClearOrDeleteDialogAlert(fragment, clear, chat != null && chat.creator, false, chat, user, secret, checkDeleteForAll, canDeleteHistory, onProcessRunnable, resourcesProvider);
    }

    /* JADX WARN: Removed duplicated region for block: B:119:0x0260  */
    /* JADX WARN: Removed duplicated region for block: B:141:0x032f  */
    /* JADX WARN: Removed duplicated region for block: B:143:0x0339  */
    /* JADX WARN: Removed duplicated region for block: B:150:0x036a  */
    /* JADX WARN: Removed duplicated region for block: B:152:0x0373  */
    /* JADX WARN: Removed duplicated region for block: B:160:0x03b7  */
    /* JADX WARN: Removed duplicated region for block: B:207:0x055c  */
    /* JADX WARN: Removed duplicated region for block: B:208:0x0568  */
    /* JADX WARN: Removed duplicated region for block: B:229:0x0635  */
    /* JADX WARN: Removed duplicated region for block: B:233:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:80:0x01e0  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x01e8  */
    /* JADX WARN: Removed duplicated region for block: B:91:0x020b  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x021e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void createClearOrDeleteDialogAlert(final org.telegram.ui.ActionBar.BaseFragment r46, final boolean r47, final boolean r48, final boolean r49, final org.telegram.tgnet.TLRPC.Chat r50, final org.telegram.tgnet.TLRPC.User r51, final boolean r52, final boolean r53, final boolean r54, final org.telegram.messenger.MessagesStorage.BooleanCallback r55, final org.telegram.ui.ActionBar.Theme.ResourcesProvider r56) {
        /*
            Method dump skipped, instructions count: 1601
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.createClearOrDeleteDialogAlert(org.telegram.ui.ActionBar.BaseFragment, boolean, boolean, boolean, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$User, boolean, boolean, boolean, org.telegram.messenger.MessagesStorage$BooleanCallback, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    public static /* synthetic */ void lambda$createClearOrDeleteDialogAlert$23(boolean[] deleteForAll, View v) {
        CheckBoxCell cell1 = (CheckBoxCell) v;
        deleteForAll[0] = !deleteForAll[0];
        cell1.setChecked(deleteForAll[0], true);
    }

    public static /* synthetic */ void lambda$createClearOrDeleteDialogAlert$25(boolean clearingCache, boolean second, boolean secret, final TLRPC.User user, final BaseFragment fragment, final boolean clear, final boolean admin, final TLRPC.Chat chat, final boolean checkDeleteForAll, final boolean canDeleteHistory, final MessagesStorage.BooleanCallback onProcessRunnable, final Theme.ResourcesProvider resourcesProvider, final boolean[] deleteForAll, DialogInterface dialogInterface, int i) {
        boolean z = false;
        if (!clearingCache && !second && !secret) {
            if (UserObject.isUserSelf(user)) {
                createClearOrDeleteDialogAlert(fragment, clear, admin, true, chat, user, false, checkDeleteForAll, canDeleteHistory, onProcessRunnable, resourcesProvider);
                return;
            } else if (user != null && deleteForAll[0]) {
                MessagesStorage.getInstance(fragment.getCurrentAccount()).getMessagesCount(user.id, new MessagesStorage.IntCallback() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda120
                    @Override // org.telegram.messenger.MessagesStorage.IntCallback
                    public final void run(int i2) {
                        AlertsCreator.lambda$createClearOrDeleteDialogAlert$24(BaseFragment.this, clear, admin, chat, user, checkDeleteForAll, canDeleteHistory, onProcessRunnable, resourcesProvider, deleteForAll, i2);
                    }
                });
                return;
            }
        }
        if (onProcessRunnable != null) {
            if (second || deleteForAll[0]) {
                z = true;
            }
            onProcessRunnable.run(z);
        }
    }

    public static /* synthetic */ void lambda$createClearOrDeleteDialogAlert$24(BaseFragment fragment, boolean clear, boolean admin, TLRPC.Chat chat, TLRPC.User user, boolean checkDeleteForAll, boolean canDeleteHistory, MessagesStorage.BooleanCallback onProcessRunnable, Theme.ResourcesProvider resourcesProvider, boolean[] deleteForAll, int count) {
        if (count >= 50) {
            createClearOrDeleteDialogAlert(fragment, clear, admin, true, chat, user, false, checkDeleteForAll, canDeleteHistory, onProcessRunnable, resourcesProvider);
        } else if (onProcessRunnable != null) {
            onProcessRunnable.run(deleteForAll[0]);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:47:0x018b, code lost:
        if (r28.id == r8) goto L49;
     */
    /* JADX WARN: Removed duplicated region for block: B:79:0x027d  */
    /* JADX WARN: Removed duplicated region for block: B:83:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void createClearDaysDialogAlert(org.telegram.ui.ActionBar.BaseFragment r26, int r27, org.telegram.tgnet.TLRPC.User r28, org.telegram.tgnet.TLRPC.Chat r29, boolean r30, final org.telegram.messenger.MessagesStorage.BooleanCallback r31, org.telegram.ui.ActionBar.Theme.ResourcesProvider r32) {
        /*
            Method dump skipped, instructions count: 650
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.createClearDaysDialogAlert(org.telegram.ui.ActionBar.BaseFragment, int, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, boolean, org.telegram.messenger.MessagesStorage$BooleanCallback, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    public static /* synthetic */ void lambda$createClearDaysDialogAlert$26(boolean[] deleteForAll, View v) {
        CheckBoxCell cell1 = (CheckBoxCell) v;
        deleteForAll[0] = !deleteForAll[0];
        cell1.setChecked(deleteForAll[0], true);
    }

    public static void createCallDialogAlert(final BaseFragment fragment, final TLRPC.User user, final boolean videoCall) {
        String message;
        String title;
        if (fragment == null || fragment.getParentActivity() == null || user == null || UserObject.isDeleted(user) || UserConfig.getInstance(fragment.getCurrentAccount()).getClientUserId() == user.id) {
            return;
        }
        fragment.getCurrentAccount();
        Context context = fragment.getParentActivity();
        FrameLayout frameLayout = new FrameLayout(context);
        if (videoCall) {
            title = LocaleController.getString("VideoCallAlertTitle", R.string.VideoCallAlertTitle);
            message = LocaleController.formatString("VideoCallAlert", R.string.VideoCallAlert, UserObject.getUserName(user));
        } else {
            title = LocaleController.getString("CallAlertTitle", R.string.CallAlertTitle);
            message = LocaleController.formatString("CallAlert", R.string.CallAlert, UserObject.getUserName(user));
        }
        TextView messageTextView = new TextView(context);
        messageTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        messageTextView.setTextSize(1, 16.0f);
        messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        messageTextView.setText(AndroidUtilities.replaceTags(message));
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
        avatarDrawable.setSmallSize(false);
        avatarDrawable.setInfo(user);
        BackupImageView imageView = new BackupImageView(context);
        imageView.setRoundRadius(AndroidUtilities.dp(20.0f));
        imageView.setForUserOrChat(user, avatarDrawable);
        frameLayout.addView(imageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
        TextView textView = new TextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setText(title);
        int i = (LocaleController.isRTL ? 5 : 3) | 48;
        int i2 = 21;
        float f = LocaleController.isRTL ? 21 : 76;
        if (LocaleController.isRTL) {
            i2 = 76;
        }
        frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, i, f, 11.0f, i2, 0.0f));
        frameLayout.addView(messageTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 57.0f, 24.0f, 9.0f));
        AlertDialog dialog = new AlertDialog.Builder(context).setView(frameLayout).setPositiveButton(LocaleController.getString("Call", R.string.Call), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda49
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i3) {
                AlertsCreator.lambda$createCallDialogAlert$28(BaseFragment.this, user, videoCall, dialogInterface, i3);
            }
        }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null).create();
        fragment.showDialog(dialog);
    }

    public static /* synthetic */ void lambda$createCallDialogAlert$28(BaseFragment fragment, TLRPC.User user, boolean videoCall, DialogInterface dialogInterface, int i) {
        TLRPC.UserFull userFull = fragment.getMessagesController().getUserFull(user.id);
        VoIPHelper.startCall(user, videoCall, userFull != null && userFull.video_calls_available, fragment.getParentActivity(), userFull, fragment.getAccountInstance());
    }

    public static void createChangeBioAlert(String currentBio, final long peerId, final Context context, final int currentAccount) {
        String str;
        int i;
        String str2;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        int i2 = R.string.UserBio;
        String str3 = "UserBio";
        builder.setTitle(peerId > 0 ? LocaleController.getString(str3, R.string.UserBio) : LocaleController.getString("DescriptionPlaceholder", R.string.DescriptionPlaceholder));
        if (peerId > 0) {
            i = R.string.VoipGroupBioEditAlertText;
            str = "VoipGroupBioEditAlertText";
        } else {
            i = R.string.DescriptionInfo;
            str = "DescriptionInfo";
        }
        builder.setMessage(LocaleController.getString(str, i));
        FrameLayout dialogView = new FrameLayout(context);
        dialogView.setClipChildren(false);
        if (peerId < 0) {
            TLRPC.ChatFull chatFull = MessagesController.getInstance(currentAccount).getChatFull(-peerId);
            if (chatFull == null) {
                str2 = "DescriptionPlaceholder";
                MessagesController.getInstance(currentAccount).loadFullChat(-peerId, ConnectionsManager.generateClassGuid(), true);
            } else {
                str2 = "DescriptionPlaceholder";
            }
        } else {
            str2 = "DescriptionPlaceholder";
        }
        final NumberTextView checkTextView = new NumberTextView(context);
        final EditText editTextView = new EditText(context);
        editTextView.setTextColor(Theme.getColor(Theme.key_voipgroup_actionBarItems));
        if (peerId <= 0) {
            str3 = str2;
            i2 = R.string.DescriptionPlaceholder;
        }
        editTextView.setHint(LocaleController.getString(str3, i2));
        editTextView.setTextSize(1, 16.0f);
        editTextView.setBackground(Theme.createEditTextDrawable(context, true));
        editTextView.setMaxLines(4);
        editTextView.setRawInputType(147457);
        editTextView.setImeOptions(6);
        InputFilter[] inputFilters = new InputFilter[1];
        final int maxSymbolsCount = peerId > 0 ? 70 : 255;
        inputFilters[0] = new CodepointsLengthInputFilter(maxSymbolsCount) { // from class: org.telegram.ui.Components.AlertsCreator.5
            @Override // org.telegram.ui.Components.CodepointsLengthInputFilter, android.text.InputFilter
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                CharSequence result = super.filter(source, start, end, dest, dstart, dend);
                if (result != null && source != null && result.length() != source.length()) {
                    Vibrator v = (Vibrator) context.getSystemService("vibrator");
                    if (v != null) {
                        v.vibrate(200L);
                    }
                    AndroidUtilities.shakeView(checkTextView, 2.0f, 0);
                }
                return result;
            }
        };
        editTextView.setFilters(inputFilters);
        checkTextView.setCenterAlign(true);
        checkTextView.setTextSize(15);
        checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        checkTextView.setImportantForAccessibility(2);
        dialogView.addView(checkTextView, LayoutHelper.createFrame(20, 20.0f, LocaleController.isRTL ? 3 : 5, 0.0f, 14.0f, 21.0f, 0.0f));
        editTextView.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(24.0f) : 0, AndroidUtilities.dp(8.0f), LocaleController.isRTL ? 0 : AndroidUtilities.dp(24.0f), AndroidUtilities.dp(8.0f));
        editTextView.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.Components.AlertsCreator.6
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i3, int i1, int i22) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i3, int i1, int i22) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
                boolean z = false;
                int count = maxSymbolsCount - Character.codePointCount(s, 0, s.length());
                if (count < 30) {
                    NumberTextView numberTextView = checkTextView;
                    if (numberTextView.getVisibility() == 0) {
                        z = true;
                    }
                    numberTextView.setNumber(count, z);
                    AndroidUtilities.updateViewVisibilityAnimated(checkTextView, true);
                    return;
                }
                AndroidUtilities.updateViewVisibilityAnimated(checkTextView, false);
            }
        });
        AndroidUtilities.updateViewVisibilityAnimated(checkTextView, false, 0.0f, false);
        editTextView.setText(currentBio);
        editTextView.setSelection(editTextView.getText().toString().length());
        builder.setView(dialogView);
        final DialogInterface.OnClickListener onDoneListener = new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda40
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i3) {
                AlertsCreator.lambda$createChangeBioAlert$30(peerId, currentAccount, editTextView, dialogInterface, i3);
            }
        };
        builder.setPositiveButton(LocaleController.getString("Save", R.string.Save), onDoneListener);
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setOnPreDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda61
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                AndroidUtilities.hideKeyboard(editTextView);
            }
        });
        dialogView.addView(editTextView, LayoutHelper.createFrame(-1, -2.0f, 0, 23.0f, 12.0f, 23.0f, 21.0f));
        editTextView.requestFocus();
        AndroidUtilities.showKeyboard(editTextView);
        final AlertDialog dialog = builder.create();
        editTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda109
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i3, KeyEvent keyEvent) {
                return AlertsCreator.lambda$createChangeBioAlert$32(peerId, dialog, onDoneListener, textView, i3, keyEvent);
            }
        });
        dialog.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_dialogBackground));
        dialog.show();
        dialog.setTextColor(Theme.getColor(Theme.key_voipgroup_actionBarItems));
    }

    public static /* synthetic */ void lambda$createChangeBioAlert$30(long peerId, int currentAccount, EditText editTextView, DialogInterface dialogInterface, int i) {
        if (peerId <= 0) {
            TLRPC.ChatFull chatFull = MessagesController.getInstance(currentAccount).getChatFull(-peerId);
            String newAbout = editTextView.getText().toString();
            if (chatFull != null) {
                String currentName = chatFull.about;
                if (currentName == null) {
                    currentName = "";
                }
                if (currentName.equals(newAbout)) {
                    AndroidUtilities.hideKeyboard(editTextView);
                    dialogInterface.dismiss();
                    return;
                }
                chatFull.about = newAbout;
                NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull, 0, false, false);
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 2, Long.valueOf(peerId));
            MessagesController.getInstance(currentAccount).updateChatAbout(-peerId, newAbout, chatFull);
        } else {
            TLRPC.UserFull userFull = MessagesController.getInstance(currentAccount).getUserFull(UserConfig.getInstance(currentAccount).getClientUserId());
            String newName = editTextView.getText().toString().replace("\n", " ").replaceAll(" +", " ").trim();
            if (userFull != null) {
                String currentName2 = userFull.about;
                if (currentName2 == null) {
                    currentName2 = "";
                }
                if (currentName2.equals(newName)) {
                    AndroidUtilities.hideKeyboard(editTextView);
                    dialogInterface.dismiss();
                    return;
                }
                userFull.about = newName;
                NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.userInfoDidLoad, Long.valueOf(peerId), userFull);
            }
            TLRPC.TL_account_updateProfile req = new TLRPC.TL_account_updateProfile();
            req.about = newName;
            req.flags = 4 | req.flags;
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 2, Long.valueOf(peerId));
            ConnectionsManager.getInstance(currentAccount).sendRequest(req, AlertsCreator$$ExternalSyntheticLambda124.INSTANCE, 2);
        }
        dialogInterface.dismiss();
    }

    public static /* synthetic */ void lambda$createChangeBioAlert$29(TLObject response, TLRPC.TL_error error) {
    }

    public static /* synthetic */ boolean lambda$createChangeBioAlert$32(long peerId, AlertDialog dialog, DialogInterface.OnClickListener onDoneListener, TextView textView, int i, KeyEvent keyEvent) {
        if ((i != 6 && (peerId <= 0 || keyEvent.getKeyCode() != 66)) || !dialog.isShowing()) {
            return false;
        }
        onDoneListener.onClick(dialog, 0);
        return true;
    }

    public static void createChangeNameAlert(final long peerId, Context context, final int currentAccount) {
        String currentName;
        String currentLastName;
        String str;
        int i;
        EditText lastNameEditTextView;
        if (!DialogObject.isUserDialog(peerId)) {
            TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(Long.valueOf(-peerId));
            currentLastName = null;
            currentName = chat.title;
        } else {
            TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(peerId));
            String currentName2 = user.first_name;
            String currentLastName2 = user.last_name;
            currentLastName = currentLastName2;
            currentName = currentName2;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (peerId > 0) {
            i = R.string.VoipEditName;
            str = "VoipEditName";
        } else {
            i = R.string.VoipEditTitle;
            str = "VoipEditTitle";
        }
        builder.setTitle(LocaleController.getString(str, i));
        LinearLayout dialogView = new LinearLayout(context);
        dialogView.setOrientation(1);
        final EditText firstNameEditTextView = new EditText(context);
        firstNameEditTextView.setTextColor(Theme.getColor(Theme.key_voipgroup_actionBarItems));
        firstNameEditTextView.setTextSize(1, 16.0f);
        firstNameEditTextView.setMaxLines(1);
        firstNameEditTextView.setLines(1);
        firstNameEditTextView.setSingleLine(true);
        firstNameEditTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        firstNameEditTextView.setInputType(49152);
        firstNameEditTextView.setImeOptions(peerId > 0 ? 5 : 6);
        firstNameEditTextView.setHint(peerId > 0 ? LocaleController.getString("FirstName", R.string.FirstName) : LocaleController.getString("VoipEditTitleHint", R.string.VoipEditTitleHint));
        firstNameEditTextView.setBackground(Theme.createEditTextDrawable(context, true));
        firstNameEditTextView.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
        firstNameEditTextView.requestFocus();
        if (peerId <= 0) {
            lastNameEditTextView = null;
        } else {
            EditText lastNameEditTextView2 = new EditText(context);
            lastNameEditTextView2.setTextColor(Theme.getColor(Theme.key_voipgroup_actionBarItems));
            lastNameEditTextView2.setTextSize(1, 16.0f);
            lastNameEditTextView2.setMaxLines(1);
            lastNameEditTextView2.setLines(1);
            lastNameEditTextView2.setSingleLine(true);
            lastNameEditTextView2.setGravity(LocaleController.isRTL ? 5 : 3);
            lastNameEditTextView2.setInputType(49152);
            lastNameEditTextView2.setImeOptions(6);
            lastNameEditTextView2.setHint(LocaleController.getString("LastName", R.string.LastName));
            lastNameEditTextView2.setBackground(Theme.createEditTextDrawable(context, true));
            lastNameEditTextView2.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
            lastNameEditTextView = lastNameEditTextView2;
        }
        AndroidUtilities.showKeyboard(firstNameEditTextView);
        dialogView.addView(firstNameEditTextView, LayoutHelper.createLinear(-1, -2, 0, 23, 12, 23, 21));
        if (lastNameEditTextView != null) {
            dialogView.addView(lastNameEditTextView, LayoutHelper.createLinear(-1, -2, 0, 23, 12, 23, 21));
        }
        firstNameEditTextView.setText(currentName);
        firstNameEditTextView.setSelection(firstNameEditTextView.getText().toString().length());
        if (lastNameEditTextView != null) {
            lastNameEditTextView.setText(currentLastName);
            lastNameEditTextView.setSelection(lastNameEditTextView.getText().toString().length());
        }
        builder.setView(dialogView);
        final EditText finalLastNameEditTextView = lastNameEditTextView;
        final DialogInterface.OnClickListener onDoneListener = new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda33
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                AlertsCreator.lambda$createChangeNameAlert$34(firstNameEditTextView, peerId, currentAccount, finalLastNameEditTextView, dialogInterface, i2);
            }
        };
        builder.setPositiveButton(LocaleController.getString("Save", R.string.Save), onDoneListener);
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setOnPreDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda63
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                AlertsCreator.lambda$createChangeNameAlert$35(firstNameEditTextView, finalLastNameEditTextView, dialogInterface);
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_dialogBackground));
        dialog.show();
        dialog.setTextColor(Theme.getColor(Theme.key_voipgroup_actionBarItems));
        TextView.OnEditorActionListener actionListener = new TextView.OnEditorActionListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda110
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i2, KeyEvent keyEvent) {
                return AlertsCreator.lambda$createChangeNameAlert$36(AlertDialog.this, onDoneListener, textView, i2, keyEvent);
            }
        };
        if (lastNameEditTextView != null) {
            lastNameEditTextView.setOnEditorActionListener(actionListener);
        } else {
            firstNameEditTextView.setOnEditorActionListener(actionListener);
        }
    }

    public static /* synthetic */ void lambda$createChangeNameAlert$34(EditText firstNameEditTextView, long peerId, int currentAccount, EditText finalLastNameEditTextView, DialogInterface dialogInterface, int i) {
        if (firstNameEditTextView.getText() != null) {
            if (peerId <= 0) {
                TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(Long.valueOf(-peerId));
                String newFirst = firstNameEditTextView.getText().toString();
                if (chat.title != null && chat.title.equals(newFirst)) {
                    dialogInterface.dismiss();
                    return;
                }
                chat.title = newFirst;
                NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_CHAT_NAME));
                MessagesController.getInstance(currentAccount).changeChatTitle(-peerId, newFirst);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 3, Long.valueOf(peerId));
            } else {
                TLRPC.User currentUser = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(peerId));
                String newFirst2 = firstNameEditTextView.getText().toString();
                String newLast = finalLastNameEditTextView.getText().toString();
                String oldFirst = currentUser.first_name;
                String oldLast = currentUser.last_name;
                if (oldFirst == null) {
                    oldFirst = "";
                }
                if (oldLast == null) {
                    oldLast = "";
                }
                if (oldFirst.equals(newFirst2) && oldLast.equals(newLast)) {
                    dialogInterface.dismiss();
                    return;
                }
                TLRPC.TL_account_updateProfile req = new TLRPC.TL_account_updateProfile();
                req.flags = 3;
                req.first_name = newFirst2;
                currentUser.first_name = newFirst2;
                req.last_name = newLast;
                currentUser.last_name = newLast;
                TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(UserConfig.getInstance(currentAccount).getClientUserId()));
                if (user != null) {
                    user.first_name = req.first_name;
                    user.last_name = req.last_name;
                }
                UserConfig.getInstance(currentAccount).saveConfig(true);
                NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_NAME));
                ConnectionsManager.getInstance(currentAccount).sendRequest(req, AlertsCreator$$ExternalSyntheticLambda125.INSTANCE);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 3, Long.valueOf(peerId));
            }
            dialogInterface.dismiss();
        }
    }

    public static /* synthetic */ void lambda$createChangeNameAlert$33(TLObject response, TLRPC.TL_error error) {
    }

    public static /* synthetic */ void lambda$createChangeNameAlert$35(EditText firstNameEditTextView, EditText finalLastNameEditTextView, DialogInterface dialogInterface) {
        AndroidUtilities.hideKeyboard(firstNameEditTextView);
        AndroidUtilities.hideKeyboard(finalLastNameEditTextView);
    }

    public static /* synthetic */ boolean lambda$createChangeNameAlert$36(AlertDialog dialog, DialogInterface.OnClickListener onDoneListener, TextView textView, int i, KeyEvent keyEvent) {
        if ((i != 6 && keyEvent.getKeyCode() != 66) || !dialog.isShowing()) {
            return false;
        }
        onDoneListener.onClick(dialog, 0);
        return true;
    }

    public static void showChatWithAdmin(BaseFragment fragment, TLRPC.User user, String chatWithAdmin, boolean isChannel, int chatWithAdminDate) {
        String str;
        int i;
        if (fragment.getParentActivity() == null) {
            return;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(fragment.getParentActivity());
        if (isChannel) {
            i = R.string.ChatWithAdminChannelTitle;
            str = "ChatWithAdminChannelTitle";
        } else {
            i = R.string.ChatWithAdminGroupTitle;
            str = "ChatWithAdminGroupTitle";
        }
        builder.setTitle(LocaleController.getString(str, i), true);
        LinearLayout linearLayout = new LinearLayout(fragment.getParentActivity());
        linearLayout.setOrientation(1);
        TextView messageTextView = new TextView(fragment.getParentActivity());
        linearLayout.addView(messageTextView, LayoutHelper.createLinear(-1, -1, 0, 24, 16, 24, 24));
        messageTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        messageTextView.setTextSize(1, 16.0f);
        messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ChatWithAdminMessage", R.string.ChatWithAdminMessage, chatWithAdmin, LocaleController.formatDateAudio(chatWithAdminDate, false))));
        TextView buttonTextView = new TextView(fragment.getParentActivity());
        buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        buttonTextView.setGravity(17);
        buttonTextView.setTextSize(1, 14.0f);
        buttonTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        buttonTextView.setText(LocaleController.getString("IUnderstand", R.string.IUnderstand));
        buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        buttonTextView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed)));
        linearLayout.addView(buttonTextView, LayoutHelper.createLinear(-1, 48, 0, 24, 15, 16, 24));
        builder.setCustomView(linearLayout);
        final BottomSheet bottomSheet = builder.show();
        buttonTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda78
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BottomSheet.this.dismiss();
            }
        });
    }

    public static void createBlockDialogAlert(BaseFragment fragment, int count, boolean reportSpam, TLRPC.User user, final BlockDialogCallback onProcessRunnable) {
        String actionText;
        String str;
        int i;
        if (fragment == null || fragment.getParentActivity() == null) {
            return;
        }
        if (count == 1 && user == null) {
            return;
        }
        Context context = fragment.getParentActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        CheckBoxCell[] cell = new CheckBoxCell[2];
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        builder.setView(linearLayout);
        if (count != 1) {
            builder.setTitle(LocaleController.formatString("BlockUserTitle", R.string.BlockUserTitle, LocaleController.formatPluralString("UsersCountTitle", count, new Object[0])));
            actionText = LocaleController.getString("BlockUsers", R.string.BlockUsers);
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BlockUsersMessage", R.string.BlockUsersMessage, LocaleController.formatPluralString("UsersCount", count, new Object[0]))));
        } else {
            String name = ContactsController.formatName(user.first_name, user.last_name);
            builder.setTitle(LocaleController.formatString("BlockUserTitle", R.string.BlockUserTitle, name));
            actionText = LocaleController.getString("BlockUser", R.string.BlockUser);
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BlockUserMessage", R.string.BlockUserMessage, name)));
        }
        final boolean[] checks = {true, true};
        for (int a = 0; a < cell.length; a++) {
            if (a != 0 || reportSpam) {
                final int num = a;
                cell[a] = new CheckBoxCell(context, 1);
                cell[a].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                if (a == 0) {
                    cell[a].setText(LocaleController.getString("ReportSpamTitle", R.string.ReportSpamTitle), "", true, false);
                } else {
                    CheckBoxCell checkBoxCell = cell[a];
                    if (count == 1) {
                        i = R.string.DeleteThisChatBothSides;
                        str = "DeleteThisChatBothSides";
                    } else {
                        i = R.string.DeleteTheseChatsBothSides;
                        str = "DeleteTheseChatsBothSides";
                    }
                    checkBoxCell.setText(LocaleController.getString(str, i), "", true, false);
                }
                cell[a].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                linearLayout.addView(cell[a], LayoutHelper.createLinear(-1, 48));
                cell[a].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda98
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        AlertsCreator.lambda$createBlockDialogAlert$38(checks, num, view);
                    }
                });
            }
        }
        builder.setPositiveButton(actionText, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda50
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                AlertsCreator.BlockDialogCallback.this.run(r1[0], checks[1]);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        AlertDialog alertDialog = builder.create();
        fragment.showDialog(alertDialog);
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
        }
    }

    public static /* synthetic */ void lambda$createBlockDialogAlert$38(boolean[] checks, int num, View v) {
        CheckBoxCell cell1 = (CheckBoxCell) v;
        checks[num] = !checks[num];
        cell1.setChecked(checks[num], true);
    }

    public static AlertDialog.Builder createDatePickerDialog(Context context, int minYear, int maxYear, int currentYearDiff, int selectedDay, int selectedMonth, int selectedYear, String title, final boolean checkMinDate, final DatePickerDelegate datePickerDelegate) {
        if (context == null) {
            return null;
        }
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        final NumberPicker monthPicker = new NumberPicker(context);
        final NumberPicker dayPicker = new NumberPicker(context);
        final NumberPicker yearPicker = new NumberPicker(context);
        linearLayout.addView(dayPicker, LayoutHelper.createLinear(0, -2, 0.3f));
        dayPicker.setOnScrollListener(new NumberPicker.OnScrollListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda20
            @Override // org.telegram.ui.Components.NumberPicker.OnScrollListener
            public final void onScrollStateChange(NumberPicker numberPicker, int i) {
                AlertsCreator.lambda$createDatePickerDialog$40(checkMinDate, dayPicker, monthPicker, yearPicker, numberPicker, i);
            }
        });
        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);
        linearLayout.addView(monthPicker, LayoutHelper.createLinear(0, -2, 0.3f));
        monthPicker.setFormatter(AlertsCreator$$ExternalSyntheticLambda10.INSTANCE);
        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda30
            @Override // org.telegram.ui.Components.NumberPicker.OnValueChangeListener
            public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
                AlertsCreator.updateDayPicker(NumberPicker.this, monthPicker, yearPicker);
            }
        });
        monthPicker.setOnScrollListener(new NumberPicker.OnScrollListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda21
            @Override // org.telegram.ui.Components.NumberPicker.OnScrollListener
            public final void onScrollStateChange(NumberPicker numberPicker, int i) {
                AlertsCreator.lambda$createDatePickerDialog$43(checkMinDate, dayPicker, monthPicker, yearPicker, numberPicker, i);
            }
        });
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentYear = calendar.get(1);
        yearPicker.setMinValue(currentYear + minYear);
        yearPicker.setMaxValue(currentYear + maxYear);
        yearPicker.setValue(currentYear + currentYearDiff);
        linearLayout.addView(yearPicker, LayoutHelper.createLinear(0, -2, 0.4f));
        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda31
            @Override // org.telegram.ui.Components.NumberPicker.OnValueChangeListener
            public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
                AlertsCreator.updateDayPicker(NumberPicker.this, monthPicker, yearPicker);
            }
        });
        yearPicker.setOnScrollListener(new NumberPicker.OnScrollListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda23
            @Override // org.telegram.ui.Components.NumberPicker.OnScrollListener
            public final void onScrollStateChange(NumberPicker numberPicker, int i) {
                AlertsCreator.lambda$createDatePickerDialog$45(checkMinDate, dayPicker, monthPicker, yearPicker, numberPicker, i);
            }
        });
        updateDayPicker(dayPicker, monthPicker, yearPicker);
        if (checkMinDate) {
            checkPickerDate(dayPicker, monthPicker, yearPicker);
        }
        if (selectedDay != -1) {
            dayPicker.setValue(selectedDay);
            monthPicker.setValue(selectedMonth);
            yearPicker.setValue(selectedYear);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Set", R.string.Set), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda55
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createDatePickerDialog$46(checkMinDate, dayPicker, monthPicker, yearPicker, datePickerDelegate, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        return builder;
    }

    public static /* synthetic */ void lambda$createDatePickerDialog$40(boolean checkMinDate, NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker, NumberPicker view, int scrollState) {
        if (checkMinDate && scrollState == 0) {
            checkPickerDate(dayPicker, monthPicker, yearPicker);
        }
    }

    public static /* synthetic */ String lambda$createDatePickerDialog$41(int value) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(5, 1);
        calendar.set(2, value);
        return calendar.getDisplayName(2, 1, Locale.getDefault());
    }

    public static /* synthetic */ void lambda$createDatePickerDialog$43(boolean checkMinDate, NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker, NumberPicker view, int scrollState) {
        if (checkMinDate && scrollState == 0) {
            checkPickerDate(dayPicker, monthPicker, yearPicker);
        }
    }

    public static /* synthetic */ void lambda$createDatePickerDialog$45(boolean checkMinDate, NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker, NumberPicker view, int scrollState) {
        if (checkMinDate && scrollState == 0) {
            checkPickerDate(dayPicker, monthPicker, yearPicker);
        }
    }

    public static /* synthetic */ void lambda$createDatePickerDialog$46(boolean checkMinDate, NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker, DatePickerDelegate datePickerDelegate, DialogInterface dialog, int which) {
        if (checkMinDate) {
            checkPickerDate(dayPicker, monthPicker, yearPicker);
        }
        datePickerDelegate.didSelectDate(yearPicker.getValue(), monthPicker.getValue(), dayPicker.getValue());
    }

    public static boolean checkScheduleDate(TextView button, TextView infoText, int type, NumberPicker dayPicker, NumberPicker hourPicker, NumberPicker minutePicker) {
        return checkScheduleDate(button, infoText, 0L, type, dayPicker, hourPicker, minutePicker);
    }

    public static boolean checkScheduleDate(TextView button, TextView infoText, long maxDate, int type, NumberPicker dayPicker, NumberPicker hourPicker, NumberPicker minutePicker) {
        int currentYear;
        long maxDate2;
        int hour;
        int day;
        boolean z;
        String t;
        char c;
        int num;
        int currentYear2;
        int day2;
        int day3 = dayPicker.getValue();
        int hour2 = hourPicker.getValue();
        int minute = minutePicker.getValue();
        Calendar calendar = Calendar.getInstance();
        long systemTime = System.currentTimeMillis();
        calendar.setTimeInMillis(systemTime);
        int currentYear3 = calendar.get(1);
        int currentDay = calendar.get(6);
        if (maxDate <= 0) {
            currentYear = currentYear3;
            maxDate2 = maxDate;
        } else {
            currentYear = currentYear3;
            calendar.setTimeInMillis(systemTime + (maxDate * 1000));
            calendar.set(11, 23);
            calendar.set(12, 59);
            calendar.set(13, 59);
            maxDate2 = calendar.getTimeInMillis();
        }
        calendar.setTimeInMillis(System.currentTimeMillis() + (day3 * 24 * 3600 * 1000));
        calendar.set(11, hour2);
        calendar.set(12, minute);
        long currentTime = calendar.getTimeInMillis();
        if (currentTime > systemTime + DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS) {
            if (maxDate2 > 0 && currentTime > maxDate2) {
                calendar.setTimeInMillis(maxDate2);
                dayPicker.setValue(7);
                int hour3 = calendar.get(11);
                hourPicker.setValue(hour3);
                int i = calendar.get(12);
                minute = i;
                minutePicker.setValue(i);
                hour = hour3;
                day = 7;
            } else {
                day = day3;
                hour = hour2;
            }
        } else {
            calendar.setTimeInMillis(systemTime + DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS);
            if (currentDay == calendar.get(6)) {
                day2 = day3;
            } else {
                day2 = 1;
                dayPicker.setValue(1);
            }
            int day4 = day2;
            int hour4 = calendar.get(11);
            hourPicker.setValue(hour4);
            int hour5 = calendar.get(12);
            minute = hour5;
            minutePicker.setValue(hour5);
            day = day4;
            hour = hour4;
        }
        int selectedYear = calendar.get(1);
        calendar.setTimeInMillis(System.currentTimeMillis() + (day * 24 * 3600 * 1000));
        calendar.set(11, hour);
        calendar.set(12, minute);
        long time = calendar.getTimeInMillis();
        if (button != null) {
            if (day == 0) {
                num = 0;
                currentYear2 = currentYear;
            } else {
                currentYear2 = currentYear;
                if (currentYear2 == selectedYear) {
                    num = 1;
                } else {
                    num = 2;
                }
            }
            if (type == 1) {
                num += 3;
            } else if (type == 2) {
                num += 6;
            } else if (type == 3) {
                num += 9;
            }
            button.setText(LocaleController.getInstance().formatterScheduleSend[num].format(time));
        }
        if (infoText != null) {
            int diff = (int) ((time - systemTime) / 1000);
            if (diff > 86400) {
                t = LocaleController.formatPluralString("DaysSchedule", Math.round(diff / 86400.0f), new Object[0]);
                c = 0;
            } else if (diff >= 3600) {
                t = LocaleController.formatPluralString("HoursSchedule", Math.round(diff / 3600.0f), new Object[0]);
                c = 0;
            } else if (diff >= 60) {
                t = LocaleController.formatPluralString("MinutesSchedule", Math.round(diff / 60.0f), new Object[0]);
                c = 0;
            } else {
                c = 0;
                t = LocaleController.formatPluralString("SecondsSchedule", diff, new Object[0]);
            }
            if (infoText.getTag() != null) {
                Object[] objArr = new Object[1];
                objArr[c] = t;
                infoText.setText(LocaleController.formatString("VoipChannelScheduleInfo", R.string.VoipChannelScheduleInfo, objArr));
                z = false;
            } else {
                z = false;
                infoText.setText(LocaleController.formatString("VoipGroupScheduleInfo", R.string.VoipGroupScheduleInfo, t));
            }
        } else {
            z = false;
        }
        if (currentTime - systemTime > DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS) {
            return true;
        }
        return z;
    }

    /* loaded from: classes5.dex */
    public static class ScheduleDatePickerColors {
        public final int backgroundColor;
        public final int buttonBackgroundColor;
        public final int buttonBackgroundPressedColor;
        public final int buttonTextColor;
        public final int iconColor;
        public final int iconSelectorColor;
        public final int subMenuBackgroundColor;
        public final int subMenuSelectorColor;
        public final int subMenuTextColor;
        public final int textColor;

        private ScheduleDatePickerColors() {
            this((Theme.ResourcesProvider) null);
        }

        private ScheduleDatePickerColors(Theme.ResourcesProvider rp) {
            this(rp != null ? rp.getColorOrDefault(Theme.key_dialogTextBlack) : Theme.getColor(Theme.key_dialogTextBlack), rp != null ? rp.getColorOrDefault(Theme.key_dialogBackground) : Theme.getColor(Theme.key_dialogBackground), rp != null ? rp.getColorOrDefault(Theme.key_sheet_other) : Theme.getColor(Theme.key_sheet_other), rp != null ? rp.getColorOrDefault(Theme.key_player_actionBarSelector) : Theme.getColor(Theme.key_player_actionBarSelector), rp != null ? rp.getColorOrDefault(Theme.key_actionBarDefaultSubmenuItem) : Theme.getColor(Theme.key_actionBarDefaultSubmenuItem), rp != null ? rp.getColorOrDefault(Theme.key_actionBarDefaultSubmenuBackground) : Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground), rp != null ? rp.getColorOrDefault(Theme.key_listSelector) : Theme.getColor(Theme.key_listSelector), rp != null ? rp.getColorOrDefault(Theme.key_featuredStickers_buttonText) : Theme.getColor(Theme.key_featuredStickers_buttonText), rp != null ? rp.getColorOrDefault(Theme.key_featuredStickers_addButton) : Theme.getColor(Theme.key_featuredStickers_addButton), rp != null ? rp.getColorOrDefault(Theme.key_featuredStickers_addButtonPressed) : Theme.getColor(Theme.key_featuredStickers_addButtonPressed));
        }

        public ScheduleDatePickerColors(int textColor, int backgroundColor, int iconColor, int iconSelectorColor, int subMenuTextColor, int subMenuBackgroundColor, int subMenuSelectorColor) {
            this(textColor, backgroundColor, iconColor, iconSelectorColor, subMenuTextColor, subMenuBackgroundColor, subMenuSelectorColor, Theme.getColor(Theme.key_featuredStickers_buttonText), Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed));
        }

        public ScheduleDatePickerColors(int textColor, int backgroundColor, int iconColor, int iconSelectorColor, int subMenuTextColor, int subMenuBackgroundColor, int subMenuSelectorColor, int buttonTextColor, int buttonBackgroundColor, int buttonBackgroundPressedColor) {
            this.textColor = textColor;
            this.backgroundColor = backgroundColor;
            this.iconColor = iconColor;
            this.iconSelectorColor = iconSelectorColor;
            this.subMenuTextColor = subMenuTextColor;
            this.subMenuBackgroundColor = subMenuBackgroundColor;
            this.subMenuSelectorColor = subMenuSelectorColor;
            this.buttonTextColor = buttonTextColor;
            this.buttonBackgroundColor = buttonBackgroundColor;
            this.buttonBackgroundPressedColor = buttonBackgroundPressedColor;
        }
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long dialogId, ScheduleDatePickerDelegate datePickerDelegate) {
        return createScheduleDatePickerDialog(context, dialogId, -1L, datePickerDelegate, (Runnable) null);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long dialogId, ScheduleDatePickerDelegate datePickerDelegate, Theme.ResourcesProvider resourcesProvider) {
        return createScheduleDatePickerDialog(context, dialogId, -1L, datePickerDelegate, null, resourcesProvider);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long dialogId, ScheduleDatePickerDelegate datePickerDelegate, ScheduleDatePickerColors datePickerColors) {
        return createScheduleDatePickerDialog(context, dialogId, -1L, datePickerDelegate, null, datePickerColors, null);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long dialogId, ScheduleDatePickerDelegate datePickerDelegate, Runnable cancelRunnable, Theme.ResourcesProvider resourcesProvider) {
        return createScheduleDatePickerDialog(context, dialogId, -1L, datePickerDelegate, cancelRunnable, resourcesProvider);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long dialogId, long currentDate, ScheduleDatePickerDelegate datePickerDelegate, Runnable cancelRunnable) {
        return createScheduleDatePickerDialog(context, dialogId, currentDate, datePickerDelegate, cancelRunnable, new ScheduleDatePickerColors(), null);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long dialogId, long currentDate, ScheduleDatePickerDelegate datePickerDelegate, Runnable cancelRunnable, Theme.ResourcesProvider resourcesProvider) {
        return createScheduleDatePickerDialog(context, dialogId, currentDate, datePickerDelegate, cancelRunnable, new ScheduleDatePickerColors(resourcesProvider), resourcesProvider);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, final long dialogId, long currentDate, final ScheduleDatePickerDelegate datePickerDelegate, final Runnable cancelRunnable, final ScheduleDatePickerColors datePickerColors, Theme.ResourcesProvider resourcesProvider) {
        String name;
        if (context == null) {
            return null;
        }
        final long selfUserId = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
        final BottomSheet.Builder builder = new BottomSheet.Builder(context, false, resourcesProvider);
        builder.setApplyBottomPadding(false);
        final NumberPicker dayPicker = new NumberPicker(context, resourcesProvider);
        dayPicker.setTextColor(datePickerColors.textColor);
        dayPicker.setTextOffset(AndroidUtilities.dp(10.0f));
        dayPicker.setItemCount(5);
        final NumberPicker hourPicker = new NumberPicker(context, resourcesProvider) { // from class: org.telegram.ui.Components.AlertsCreator.7
            @Override // org.telegram.ui.Components.NumberPicker
            protected CharSequence getContentDescription(int value) {
                return LocaleController.formatPluralString("Hours", value, new Object[0]);
            }
        };
        hourPicker.setItemCount(5);
        hourPicker.setTextColor(datePickerColors.textColor);
        hourPicker.setTextOffset(-AndroidUtilities.dp(10.0f));
        final NumberPicker minutePicker = new NumberPicker(context, resourcesProvider) { // from class: org.telegram.ui.Components.AlertsCreator.8
            @Override // org.telegram.ui.Components.NumberPicker
            protected CharSequence getContentDescription(int value) {
                return LocaleController.formatPluralString("Minutes", value, new Object[0]);
            }
        };
        minutePicker.setItemCount(5);
        minutePicker.setTextColor(datePickerColors.textColor);
        minutePicker.setTextOffset(-AndroidUtilities.dp(34.0f));
        final LinearLayout container = new LinearLayout(context) { // from class: org.telegram.ui.Components.AlertsCreator.9
            boolean ignoreLayout = false;

            @Override // android.widget.LinearLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int count;
                this.ignoreLayout = true;
                if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    count = 3;
                } else {
                    count = 5;
                }
                dayPicker.setItemCount(count);
                hourPicker.setItemCount(count);
                minutePicker.setItemCount(count);
                dayPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * count;
                hourPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * count;
                minutePicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * count;
                this.ignoreLayout = false;
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        int i = 1;
        container.setOrientation(1);
        FrameLayout titleLayout = new FrameLayout(context);
        container.addView(titleLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView titleView = new TextView(context);
        if (dialogId == selfUserId) {
            titleView.setText(LocaleController.getString("SetReminder", R.string.SetReminder));
        } else {
            titleView.setText(LocaleController.getString("ScheduleMessage", R.string.ScheduleMessage));
        }
        titleView.setTextColor(datePickerColors.textColor);
        titleView.setTextSize(1, 20.0f);
        titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        titleLayout.addView(titleView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        titleView.setOnTouchListener(AlertsCreator$$ExternalSyntheticLambda107.INSTANCE);
        if (DialogObject.isUserDialog(dialogId) && dialogId != selfUserId) {
            TLRPC.User user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Long.valueOf(dialogId));
            if (user != null && !user.bot && user.status != null && user.status.expires > 0) {
                String name2 = UserObject.getFirstName(user);
                if (name2.length() <= 10) {
                    name = name2;
                } else {
                    name = name2.substring(0, 10) + "…";
                }
                final ActionBarMenuItem optionsButton = new ActionBarMenuItem(context, null, 0, datePickerColors.iconColor, false, resourcesProvider);
                optionsButton.setLongClickEnabled(false);
                optionsButton.setSubMenuOpenSide(2);
                optionsButton.setIcon(R.drawable.ic_ab_other);
                i = 1;
                optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(datePickerColors.iconSelectorColor, 1));
                titleLayout = titleLayout;
                titleLayout.addView(optionsButton, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, 8.0f, 5.0f, 0.0f));
                optionsButton.addSubItem(1, LocaleController.formatString("ScheduleWhenOnline", R.string.ScheduleWhenOnline, name));
                optionsButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda75
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        AlertsCreator.lambda$createScheduleDatePickerDialog$48(ActionBarMenuItem.this, datePickerColors, view);
                    }
                });
                optionsButton.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda2
                    @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemDelegate
                    public final void onItemClick(int i2) {
                        AlertsCreator.lambda$createScheduleDatePickerDialog$49(AlertsCreator.ScheduleDatePickerDelegate.this, builder, i2);
                    }
                });
                optionsButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
            }
        }
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        container.addView(linearLayout, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
        final long currentTime = System.currentTimeMillis();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        final int currentYear = calendar.get(i);
        final TextView buttonTextView = new TextView(context) { // from class: org.telegram.ui.Components.AlertsCreator.10
            @Override // android.widget.TextView, android.view.View
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout.addView(dayPicker, LayoutHelper.createLinear(0, 270, 0.5f));
        dayPicker.setMinValue(0);
        dayPicker.setMaxValue(365);
        dayPicker.setWrapSelectorWheel(false);
        dayPicker.setFormatter(new NumberPicker.Formatter() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda4
            @Override // org.telegram.ui.Components.NumberPicker.Formatter
            public final String format(int i2) {
                return AlertsCreator.lambda$createScheduleDatePickerDialog$50(currentTime, calendar, currentYear, i2);
            }
        });
        NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda28
            @Override // org.telegram.ui.Components.NumberPicker.OnValueChangeListener
            public final void onValueChange(NumberPicker numberPicker, int i2, int i3) {
                AlertsCreator.lambda$createScheduleDatePickerDialog$51(container, buttonTextView, selfUserId, dialogId, dayPicker, hourPicker, minutePicker, numberPicker, i2, i3);
            }
        };
        dayPicker.setOnValueChangedListener(onValueChangeListener);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        linearLayout.addView(hourPicker, LayoutHelper.createLinear(0, 270, 0.2f));
        hourPicker.setFormatter(AlertsCreator$$ExternalSyntheticLambda14.INSTANCE);
        hourPicker.setOnValueChangedListener(onValueChangeListener);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(0);
        minutePicker.setFormatter(AlertsCreator$$ExternalSyntheticLambda15.INSTANCE);
        linearLayout.addView(minutePicker, LayoutHelper.createLinear(0, 270, 0.3f));
        minutePicker.setOnValueChangedListener(onValueChangeListener);
        if (currentDate > 0 && currentDate != 2147483646) {
            long currentDate2 = 1000 * currentDate;
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(12, 0);
            calendar.set(13, 0);
            calendar.set(14, 0);
            calendar.set(11, 0);
            int days = (int) ((currentDate2 - calendar.getTimeInMillis()) / 86400000);
            calendar.setTimeInMillis(currentDate2);
            if (days >= 0) {
                minutePicker.setValue(calendar.get(12));
                hourPicker.setValue(calendar.get(11));
                dayPicker.setValue(days);
            }
        }
        final boolean[] canceled = {true};
        checkScheduleDate(buttonTextView, null, selfUserId == dialogId ? 1 : 0, dayPicker, hourPicker, minutePicker);
        buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        buttonTextView.setGravity(17);
        buttonTextView.setTextColor(datePickerColors.buttonTextColor);
        buttonTextView.setTextSize(1, 14.0f);
        buttonTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        buttonTextView.setBackground(Theme.AdaptiveRipple.filledRect(datePickerColors.buttonBackgroundColor, 4.0f));
        container.addView(buttonTextView, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        buttonTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda99
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AlertsCreator.lambda$createScheduleDatePickerDialog$54(canceled, selfUserId, dialogId, dayPicker, hourPicker, minutePicker, calendar, datePickerDelegate, builder, view);
            }
        });
        builder.setCustomView(container);
        BottomSheet bottomSheet = builder.show();
        bottomSheet.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda67
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                AlertsCreator.lambda$createScheduleDatePickerDialog$55(cancelRunnable, canceled, dialogInterface);
            }
        });
        bottomSheet.setBackgroundColor(datePickerColors.backgroundColor);
        bottomSheet.fixNavigationBar(datePickerColors.backgroundColor);
        return builder;
    }

    public static /* synthetic */ boolean lambda$createScheduleDatePickerDialog$47(View v, MotionEvent event) {
        return true;
    }

    public static /* synthetic */ void lambda$createScheduleDatePickerDialog$48(ActionBarMenuItem optionsButton, ScheduleDatePickerColors datePickerColors, View v) {
        optionsButton.toggleSubMenu();
        optionsButton.setPopupItemsColor(datePickerColors.subMenuTextColor, false);
        optionsButton.setupPopupRadialSelectors(datePickerColors.subMenuSelectorColor);
        optionsButton.redrawPopup(datePickerColors.subMenuBackgroundColor);
    }

    public static /* synthetic */ void lambda$createScheduleDatePickerDialog$49(ScheduleDatePickerDelegate datePickerDelegate, BottomSheet.Builder builder, int id) {
        if (id == 1) {
            datePickerDelegate.didSelectDate(true, 2147483646);
            builder.getDismissRunnable().run();
        }
    }

    public static /* synthetic */ String lambda$createScheduleDatePickerDialog$50(long currentTime, Calendar calendar, int currentYear, int value) {
        if (value == 0) {
            return LocaleController.getString("MessageScheduleToday", R.string.MessageScheduleToday);
        }
        long date = (value * 86400000) + currentTime;
        calendar.setTimeInMillis(date);
        int year = calendar.get(1);
        if (year == currentYear) {
            return LocaleController.getInstance().formatterScheduleDay.format(date);
        }
        return LocaleController.getInstance().formatterScheduleYear.format(date);
    }

    public static /* synthetic */ void lambda$createScheduleDatePickerDialog$51(LinearLayout container, TextView buttonTextView, long selfUserId, long dialogId, NumberPicker dayPicker, NumberPicker hourPicker, NumberPicker minutePicker, NumberPicker picker, int oldVal, int newVal) {
        try {
            container.performHapticFeedback(3, 2);
        } catch (Exception e) {
        }
        checkScheduleDate(buttonTextView, null, selfUserId == dialogId ? 1 : 0, dayPicker, hourPicker, minutePicker);
    }

    public static /* synthetic */ void lambda$createScheduleDatePickerDialog$54(boolean[] canceled, long selfUserId, long dialogId, NumberPicker dayPicker, NumberPicker hourPicker, NumberPicker minutePicker, Calendar calendar, ScheduleDatePickerDelegate datePickerDelegate, BottomSheet.Builder builder, View v) {
        canceled[0] = false;
        boolean setSeconds = checkScheduleDate(null, null, selfUserId == dialogId ? 1 : 0, dayPicker, hourPicker, minutePicker);
        calendar.setTimeInMillis(System.currentTimeMillis() + (dayPicker.getValue() * 24 * 3600 * 1000));
        calendar.set(11, hourPicker.getValue());
        calendar.set(12, minutePicker.getValue());
        if (setSeconds) {
            calendar.set(13, 0);
        }
        datePickerDelegate.didSelectDate(true, (int) (calendar.getTimeInMillis() / 1000));
        builder.getDismissRunnable().run();
    }

    public static /* synthetic */ void lambda$createScheduleDatePickerDialog$55(Runnable cancelRunnable, boolean[] canceled, DialogInterface dialog) {
        if (cancelRunnable != null && canceled[0]) {
            cancelRunnable.run();
        }
    }

    public static BottomSheet.Builder createDatePickerDialog(Context context, long currentDate, final ScheduleDatePickerDelegate datePickerDelegate) {
        FrameLayout titleLayout;
        if (context == null) {
            return null;
        }
        ScheduleDatePickerColors datePickerColors = new ScheduleDatePickerColors();
        final BottomSheet.Builder builder = new BottomSheet.Builder(context, false);
        builder.setApplyBottomPadding(false);
        final NumberPicker dayPicker = new NumberPicker(context);
        dayPicker.setTextColor(datePickerColors.textColor);
        dayPicker.setTextOffset(AndroidUtilities.dp(10.0f));
        dayPicker.setItemCount(5);
        final NumberPicker hourPicker = new NumberPicker(context) { // from class: org.telegram.ui.Components.AlertsCreator.11
            @Override // org.telegram.ui.Components.NumberPicker
            protected CharSequence getContentDescription(int value) {
                return LocaleController.formatPluralString("Hours", value, new Object[0]);
            }
        };
        hourPicker.setItemCount(5);
        hourPicker.setTextColor(datePickerColors.textColor);
        hourPicker.setTextOffset(-AndroidUtilities.dp(10.0f));
        final NumberPicker minutePicker = new NumberPicker(context) { // from class: org.telegram.ui.Components.AlertsCreator.12
            @Override // org.telegram.ui.Components.NumberPicker
            protected CharSequence getContentDescription(int value) {
                return LocaleController.formatPluralString("Minutes", value, new Object[0]);
            }
        };
        minutePicker.setItemCount(5);
        minutePicker.setTextColor(datePickerColors.textColor);
        minutePicker.setTextOffset(-AndroidUtilities.dp(34.0f));
        final LinearLayout container = new LinearLayout(context) { // from class: org.telegram.ui.Components.AlertsCreator.13
            boolean ignoreLayout = false;

            @Override // android.widget.LinearLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int count;
                this.ignoreLayout = true;
                if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    count = 3;
                } else {
                    count = 5;
                }
                dayPicker.setItemCount(count);
                hourPicker.setItemCount(count);
                minutePicker.setItemCount(count);
                dayPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * count;
                hourPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * count;
                minutePicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * count;
                this.ignoreLayout = false;
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        container.setOrientation(1);
        FrameLayout titleLayout2 = new FrameLayout(context);
        container.addView(titleLayout2, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView titleView = new TextView(context);
        titleView.setText(LocaleController.getString("ExpireAfter", R.string.ExpireAfter));
        titleView.setTextColor(datePickerColors.textColor);
        titleView.setTextSize(1, 20.0f);
        titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        titleLayout2.addView(titleView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        titleView.setOnTouchListener(AlertsCreator$$ExternalSyntheticLambda104.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        container.addView(linearLayout, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
        final long currentTime = System.currentTimeMillis();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        final int currentYear = calendar.get(1);
        TextView buttonTextView = new TextView(context) { // from class: org.telegram.ui.Components.AlertsCreator.14
            @Override // android.widget.TextView, android.view.View
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout.addView(dayPicker, LayoutHelper.createLinear(0, 270, 0.5f));
        dayPicker.setMinValue(0);
        dayPicker.setMaxValue(365);
        dayPicker.setWrapSelectorWheel(false);
        dayPicker.setFormatter(new NumberPicker.Formatter() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda3
            @Override // org.telegram.ui.Components.NumberPicker.Formatter
            public final String format(int i) {
                return AlertsCreator.lambda$createDatePickerDialog$57(currentTime, calendar, currentYear, i);
            }
        });
        NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda29
            @Override // org.telegram.ui.Components.NumberPicker.OnValueChangeListener
            public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
                AlertsCreator.lambda$createDatePickerDialog$58(container, dayPicker, hourPicker, minutePicker, numberPicker, i, i2);
            }
        };
        dayPicker.setOnValueChangedListener(onValueChangeListener);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        linearLayout.addView(hourPicker, LayoutHelper.createLinear(0, 270, 0.2f));
        hourPicker.setFormatter(AlertsCreator$$ExternalSyntheticLambda12.INSTANCE);
        hourPicker.setOnValueChangedListener(onValueChangeListener);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(0);
        minutePicker.setFormatter(AlertsCreator$$ExternalSyntheticLambda13.INSTANCE);
        linearLayout.addView(minutePicker, LayoutHelper.createLinear(0, 270, 0.3f));
        minutePicker.setOnValueChangedListener(onValueChangeListener);
        if (currentDate <= 0 || currentDate == 2147483646) {
            titleLayout = titleLayout2;
        } else {
            long currentDate2 = 1000 * currentDate;
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(12, 0);
            calendar.set(13, 0);
            calendar.set(14, 0);
            calendar.set(11, 0);
            titleLayout = titleLayout2;
            int days = (int) ((currentDate2 - calendar.getTimeInMillis()) / 86400000);
            calendar.setTimeInMillis(currentDate2);
            if (days >= 0) {
                minutePicker.setValue(calendar.get(12));
                hourPicker.setValue(calendar.get(11));
                dayPicker.setValue(days);
            }
        }
        checkScheduleDate(null, null, 0, dayPicker, hourPicker, minutePicker);
        buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        buttonTextView.setGravity(17);
        buttonTextView.setTextColor(datePickerColors.buttonTextColor);
        buttonTextView.setTextSize(1, 14.0f);
        buttonTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), datePickerColors.buttonBackgroundColor, datePickerColors.buttonBackgroundPressedColor));
        buttonTextView.setText(LocaleController.getString("SetTimeLimit", R.string.SetTimeLimit));
        container.addView(buttonTextView, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        buttonTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda80
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AlertsCreator.lambda$createDatePickerDialog$61(NumberPicker.this, hourPicker, minutePicker, calendar, datePickerDelegate, builder, view);
            }
        });
        builder.setCustomView(container);
        BottomSheet bottomSheet = builder.show();
        bottomSheet.setBackgroundColor(datePickerColors.backgroundColor);
        bottomSheet.fixNavigationBar(datePickerColors.backgroundColor);
        return builder;
    }

    public static /* synthetic */ boolean lambda$createDatePickerDialog$56(View v, MotionEvent event) {
        return true;
    }

    public static /* synthetic */ String lambda$createDatePickerDialog$57(long currentTime, Calendar calendar, int currentYear, int value) {
        if (value == 0) {
            return LocaleController.getString("MessageScheduleToday", R.string.MessageScheduleToday);
        }
        long date = (value * 86400000) + currentTime;
        calendar.setTimeInMillis(date);
        int year = calendar.get(1);
        if (year == currentYear) {
            return LocaleController.getInstance().formatterScheduleDay.format(date);
        }
        return LocaleController.getInstance().formatterScheduleYear.format(date);
    }

    public static /* synthetic */ void lambda$createDatePickerDialog$58(LinearLayout container, NumberPicker dayPicker, NumberPicker hourPicker, NumberPicker minutePicker, NumberPicker picker, int oldVal, int newVal) {
        try {
            container.performHapticFeedback(3, 2);
        } catch (Exception e) {
        }
        checkScheduleDate(null, null, 0, dayPicker, hourPicker, minutePicker);
    }

    public static /* synthetic */ void lambda$createDatePickerDialog$61(NumberPicker dayPicker, NumberPicker hourPicker, NumberPicker minutePicker, Calendar calendar, ScheduleDatePickerDelegate datePickerDelegate, BottomSheet.Builder builder, View v) {
        boolean setSeconds = checkScheduleDate(null, null, 0, dayPicker, hourPicker, minutePicker);
        calendar.setTimeInMillis(System.currentTimeMillis() + (dayPicker.getValue() * 24 * 3600 * 1000));
        calendar.set(11, hourPicker.getValue());
        calendar.set(12, minutePicker.getValue());
        if (setSeconds) {
            calendar.set(13, 0);
        }
        datePickerDelegate.didSelectDate(true, (int) (calendar.getTimeInMillis() / 1000));
        builder.getDismissRunnable().run();
    }

    public static BottomSheet.Builder createAutoDeleteDatePickerDialog(Context context, Theme.ResourcesProvider resourcesProvider, final ScheduleDatePickerDelegate datePickerDelegate) {
        if (context == null) {
            return null;
        }
        ScheduleDatePickerColors datePickerColors = new ScheduleDatePickerColors(resourcesProvider);
        final BottomSheet.Builder builder = new BottomSheet.Builder(context, false, resourcesProvider);
        builder.setApplyBottomPadding(false);
        final int[] values = {0, 1440, 2880, 4320, 5760, 7200, 8640, 10080, 20160, 30240, 44640, 89280, 133920, 178560, 223200, 267840, 525600};
        final NumberPicker numberPicker = new NumberPicker(context, resourcesProvider) { // from class: org.telegram.ui.Components.AlertsCreator.15
            @Override // org.telegram.ui.Components.NumberPicker
            protected CharSequence getContentDescription(int index) {
                int[] iArr = values;
                if (iArr[index] == 0) {
                    return LocaleController.getString("AutoDeleteNever", R.string.AutoDeleteNever);
                }
                if (iArr[index] < 10080) {
                    return LocaleController.formatPluralString("Days", iArr[index] / 1440, new Object[0]);
                }
                if (iArr[index] < 44640) {
                    return LocaleController.formatPluralString("Weeks", iArr[index] / 1440, new Object[0]);
                }
                if (iArr[index] < 525600) {
                    return LocaleController.formatPluralString("Months", iArr[index] / 10080, new Object[0]);
                }
                return LocaleController.formatPluralString("Years", ((iArr[index] * 5) / 31) * 60 * 24, new Object[0]);
            }
        };
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(values.length - 1);
        numberPicker.setTextColor(datePickerColors.textColor);
        numberPicker.setValue(0);
        numberPicker.setFormatter(new NumberPicker.Formatter() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda5
            @Override // org.telegram.ui.Components.NumberPicker.Formatter
            public final String format(int i) {
                return AlertsCreator.lambda$createAutoDeleteDatePickerDialog$62(values, i);
            }
        });
        final LinearLayout container = new LinearLayout(context) { // from class: org.telegram.ui.Components.AlertsCreator.16
            boolean ignoreLayout = false;

            @Override // android.widget.LinearLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int count;
                this.ignoreLayout = true;
                if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    count = 3;
                } else {
                    count = 5;
                }
                numberPicker.setItemCount(count);
                numberPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * count;
                this.ignoreLayout = false;
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        container.setOrientation(1);
        FrameLayout titleLayout = new FrameLayout(context);
        container.addView(titleLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView titleView = new TextView(context);
        titleView.setText(LocaleController.getString("AutoDeleteAfteTitle", R.string.AutoDeleteAfteTitle));
        titleView.setTextColor(datePickerColors.textColor);
        titleView.setTextSize(1, 20.0f);
        titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        titleLayout.addView(titleView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        titleView.setOnTouchListener(AlertsCreator$$ExternalSyntheticLambda102.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        container.addView(linearLayout, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
        TextView buttonTextView = new TextView(context) { // from class: org.telegram.ui.Components.AlertsCreator.17
            @Override // android.widget.TextView, android.view.View
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout.addView(numberPicker, LayoutHelper.createLinear(0, 270, 1.0f));
        buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        buttonTextView.setGravity(17);
        buttonTextView.setTextColor(datePickerColors.buttonTextColor);
        buttonTextView.setTextSize(1, 14.0f);
        buttonTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), datePickerColors.buttonBackgroundColor, datePickerColors.buttonBackgroundPressedColor));
        buttonTextView.setText(LocaleController.getString("AutoDeleteConfirm", R.string.AutoDeleteConfirm));
        container.addView(buttonTextView, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda24
            @Override // org.telegram.ui.Components.NumberPicker.OnValueChangeListener
            public final void onValueChange(NumberPicker numberPicker2, int i, int i2) {
                container.performHapticFeedback(3, 2);
            }
        };
        numberPicker.setOnValueChangedListener(onValueChangeListener);
        buttonTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda87
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AlertsCreator.lambda$createAutoDeleteDatePickerDialog$65(values, numberPicker, datePickerDelegate, builder, view);
            }
        });
        builder.setCustomView(container);
        BottomSheet bottomSheet = builder.show();
        bottomSheet.setBackgroundColor(datePickerColors.backgroundColor);
        bottomSheet.fixNavigationBar(datePickerColors.backgroundColor);
        return builder;
    }

    public static /* synthetic */ String lambda$createAutoDeleteDatePickerDialog$62(int[] values, int index) {
        if (values[index] == 0) {
            return LocaleController.getString("AutoDeleteNever", R.string.AutoDeleteNever);
        }
        if (values[index] < 10080) {
            return LocaleController.formatPluralString("Days", values[index] / 1440, new Object[0]);
        }
        if (values[index] < 44640) {
            return LocaleController.formatPluralString("Weeks", values[index] / 10080, new Object[0]);
        }
        if (values[index] < 525600) {
            return LocaleController.formatPluralString("Months", values[index] / 44640, new Object[0]);
        }
        return LocaleController.formatPluralString("Years", values[index] / 525600, new Object[0]);
    }

    public static /* synthetic */ boolean lambda$createAutoDeleteDatePickerDialog$63(View v, MotionEvent event) {
        return true;
    }

    public static /* synthetic */ void lambda$createAutoDeleteDatePickerDialog$65(int[] values, NumberPicker numberPicker, ScheduleDatePickerDelegate datePickerDelegate, BottomSheet.Builder builder, View v) {
        int time = values[numberPicker.getValue()];
        datePickerDelegate.didSelectDate(true, time);
        builder.getDismissRunnable().run();
    }

    public static BottomSheet.Builder createSoundFrequencyPickerDialog(Context context, int notifyMaxCount, int notifyDelay, SoundFrequencyDelegate delegate) {
        return createSoundFrequencyPickerDialog(context, notifyMaxCount, notifyDelay, delegate, null);
    }

    public static BottomSheet.Builder createSoundFrequencyPickerDialog(Context context, int notifyMaxCount, int notifyDelay, final SoundFrequencyDelegate delegate, Theme.ResourcesProvider resourcesProvider) {
        if (context == null) {
            return null;
        }
        ScheduleDatePickerColors datePickerColors = new ScheduleDatePickerColors(resourcesProvider);
        final BottomSheet.Builder builder = new BottomSheet.Builder(context, false, resourcesProvider);
        builder.setApplyBottomPadding(false);
        final NumberPicker times = new NumberPicker(context, resourcesProvider) { // from class: org.telegram.ui.Components.AlertsCreator.18
            @Override // org.telegram.ui.Components.NumberPicker
            protected CharSequence getContentDescription(int index) {
                return LocaleController.formatPluralString("Times", index + 1, new Object[0]);
            }
        };
        times.setMinValue(0);
        times.setMaxValue(10);
        times.setTextColor(datePickerColors.textColor);
        times.setValue(notifyMaxCount - 1);
        times.setWrapSelectorWheel(false);
        times.setFormatter(AlertsCreator$$ExternalSyntheticLambda16.INSTANCE);
        final NumberPicker minutes = new NumberPicker(context, resourcesProvider) { // from class: org.telegram.ui.Components.AlertsCreator.19
            @Override // org.telegram.ui.Components.NumberPicker
            protected CharSequence getContentDescription(int index) {
                return LocaleController.formatPluralString("Times", index + 1, new Object[0]);
            }
        };
        minutes.setMinValue(0);
        minutes.setMaxValue(10);
        minutes.setTextColor(datePickerColors.textColor);
        minutes.setValue((notifyDelay / 60) - 1);
        minutes.setWrapSelectorWheel(false);
        minutes.setFormatter(AlertsCreator$$ExternalSyntheticLambda17.INSTANCE);
        final NumberPicker divider = new NumberPicker(context, resourcesProvider);
        divider.setMinValue(0);
        divider.setMaxValue(0);
        divider.setTextColor(datePickerColors.textColor);
        divider.setValue(0);
        divider.setWrapSelectorWheel(false);
        divider.setFormatter(AlertsCreator$$ExternalSyntheticLambda18.INSTANCE);
        final LinearLayout container = new LinearLayout(context) { // from class: org.telegram.ui.Components.AlertsCreator.20
            boolean ignoreLayout = false;

            @Override // android.widget.LinearLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int count;
                this.ignoreLayout = true;
                if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    count = 3;
                } else {
                    count = 5;
                }
                times.setItemCount(count);
                times.getLayoutParams().height = AndroidUtilities.dp(42.0f) * count;
                minutes.setItemCount(count);
                minutes.getLayoutParams().height = AndroidUtilities.dp(42.0f) * count;
                divider.setItemCount(count);
                divider.getLayoutParams().height = AndroidUtilities.dp(42.0f) * count;
                this.ignoreLayout = false;
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        container.setOrientation(1);
        FrameLayout titleLayout = new FrameLayout(context);
        container.addView(titleLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView titleView = new TextView(context);
        titleView.setText(LocaleController.getString("NotfificationsFrequencyTitle", R.string.NotfificationsFrequencyTitle));
        titleView.setTextColor(datePickerColors.textColor);
        titleView.setTextSize(1, 20.0f);
        titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        titleLayout.addView(titleView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        titleView.setOnTouchListener(AlertsCreator$$ExternalSyntheticLambda108.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        container.addView(linearLayout, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
        TextView buttonTextView = new TextView(context) { // from class: org.telegram.ui.Components.AlertsCreator.21
            @Override // android.widget.TextView, android.view.View
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout.addView(times, LayoutHelper.createLinear(0, 270, 0.4f));
        linearLayout.addView(divider, LayoutHelper.createLinear(0, -2, 0.2f, 16));
        linearLayout.addView(minutes, LayoutHelper.createLinear(0, 270, 0.4f));
        buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        buttonTextView.setGravity(17);
        buttonTextView.setTextColor(datePickerColors.buttonTextColor);
        buttonTextView.setTextSize(1, 14.0f);
        buttonTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), datePickerColors.buttonBackgroundColor, datePickerColors.buttonBackgroundPressedColor));
        buttonTextView.setText(LocaleController.getString("AutoDeleteConfirm", R.string.AutoDeleteConfirm));
        container.addView(buttonTextView, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda26
            @Override // org.telegram.ui.Components.NumberPicker.OnValueChangeListener
            public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
                container.performHapticFeedback(3, 2);
            }
        };
        times.setOnValueChangedListener(onValueChangeListener);
        minutes.setOnValueChangedListener(onValueChangeListener);
        buttonTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda79
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AlertsCreator.lambda$createSoundFrequencyPickerDialog$71(NumberPicker.this, minutes, delegate, builder, view);
            }
        });
        builder.setCustomView(container);
        BottomSheet bottomSheet = builder.show();
        bottomSheet.setBackgroundColor(datePickerColors.backgroundColor);
        bottomSheet.fixNavigationBar(datePickerColors.backgroundColor);
        return builder;
    }

    public static /* synthetic */ boolean lambda$createSoundFrequencyPickerDialog$69(View v, MotionEvent event) {
        return true;
    }

    public static /* synthetic */ void lambda$createSoundFrequencyPickerDialog$71(NumberPicker times, NumberPicker minutes, SoundFrequencyDelegate delegate, BottomSheet.Builder builder, View v) {
        int time = times.getValue() + 1;
        int minute = (minutes.getValue() + 1) * 60;
        delegate.didSelectValues(time, minute);
        builder.getDismissRunnable().run();
    }

    public static BottomSheet.Builder createMuteForPickerDialog(Context context, Theme.ResourcesProvider resourcesProvider, final ScheduleDatePickerDelegate datePickerDelegate) {
        if (context == null) {
            return null;
        }
        ScheduleDatePickerColors datePickerColors = new ScheduleDatePickerColors(resourcesProvider);
        final BottomSheet.Builder builder = new BottomSheet.Builder(context, false, resourcesProvider);
        builder.setApplyBottomPadding(false);
        final int[] values = {30, 60, 120, 180, 480, 1440, 2880, 4320, 5760, 7200, 8640, 10080, 20160, 30240, 44640, 89280, 133920, 178560, 223200, 267840, 525600};
        final NumberPicker numberPicker = new NumberPicker(context, resourcesProvider) { // from class: org.telegram.ui.Components.AlertsCreator.22
            @Override // org.telegram.ui.Components.NumberPicker
            protected CharSequence getContentDescription(int index) {
                int[] iArr = values;
                if (iArr[index] == 0) {
                    return LocaleController.getString("MuteNever", R.string.MuteNever);
                }
                if (iArr[index] < 60) {
                    return LocaleController.formatPluralString("Minutes", iArr[index], new Object[0]);
                }
                if (iArr[index] < 1440) {
                    return LocaleController.formatPluralString("Hours", iArr[index] / 60, new Object[0]);
                }
                if (iArr[index] < 10080) {
                    return LocaleController.formatPluralString("Days", iArr[index] / 1440, new Object[0]);
                }
                if (iArr[index] < 44640) {
                    return LocaleController.formatPluralString("Weeks", iArr[index] / 10080, new Object[0]);
                }
                if (iArr[index] < 525600) {
                    return LocaleController.formatPluralString("Months", iArr[index] / 44640, new Object[0]);
                }
                return LocaleController.formatPluralString("Years", iArr[index] / 525600, new Object[0]);
            }
        };
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(values.length - 1);
        numberPicker.setTextColor(datePickerColors.textColor);
        numberPicker.setValue(0);
        numberPicker.setFormatter(new NumberPicker.Formatter() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda6
            @Override // org.telegram.ui.Components.NumberPicker.Formatter
            public final String format(int i) {
                return AlertsCreator.lambda$createMuteForPickerDialog$72(values, i);
            }
        });
        final LinearLayout container = new LinearLayout(context) { // from class: org.telegram.ui.Components.AlertsCreator.23
            boolean ignoreLayout = false;

            @Override // android.widget.LinearLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int count;
                this.ignoreLayout = true;
                if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    count = 3;
                } else {
                    count = 5;
                }
                numberPicker.setItemCount(count);
                numberPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * count;
                this.ignoreLayout = false;
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        container.setOrientation(1);
        FrameLayout titleLayout = new FrameLayout(context);
        container.addView(titleLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView titleView = new TextView(context);
        titleView.setText(LocaleController.getString("MuteForAlert", R.string.MuteForAlert));
        titleView.setTextColor(datePickerColors.textColor);
        titleView.setTextSize(1, 20.0f);
        titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        titleLayout.addView(titleView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        titleView.setOnTouchListener(AlertsCreator$$ExternalSyntheticLambda105.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        container.addView(linearLayout, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
        TextView buttonTextView = new TextView(context) { // from class: org.telegram.ui.Components.AlertsCreator.24
            @Override // android.widget.TextView, android.view.View
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout.addView(numberPicker, LayoutHelper.createLinear(0, 270, 1.0f));
        NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda25
            @Override // org.telegram.ui.Components.NumberPicker.OnValueChangeListener
            public final void onValueChange(NumberPicker numberPicker2, int i, int i2) {
                container.performHapticFeedback(3, 2);
            }
        };
        numberPicker.setOnValueChangedListener(onValueChangeListener);
        buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        buttonTextView.setGravity(17);
        buttonTextView.setTextColor(datePickerColors.buttonTextColor);
        buttonTextView.setTextSize(1, 14.0f);
        buttonTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), datePickerColors.buttonBackgroundColor, datePickerColors.buttonBackgroundPressedColor));
        buttonTextView.setText(LocaleController.getString("AutoDeleteConfirm", R.string.AutoDeleteConfirm));
        container.addView(buttonTextView, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        buttonTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda88
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AlertsCreator.lambda$createMuteForPickerDialog$75(values, numberPicker, datePickerDelegate, builder, view);
            }
        });
        builder.setCustomView(container);
        BottomSheet bottomSheet = builder.show();
        bottomSheet.setBackgroundColor(datePickerColors.backgroundColor);
        bottomSheet.fixNavigationBar(datePickerColors.backgroundColor);
        return builder;
    }

    public static /* synthetic */ String lambda$createMuteForPickerDialog$72(int[] values, int index) {
        if (values[index] == 0) {
            return LocaleController.getString("MuteNever", R.string.MuteNever);
        }
        if (values[index] < 60) {
            return LocaleController.formatPluralString("Minutes", values[index], new Object[0]);
        }
        if (values[index] < 1440) {
            return LocaleController.formatPluralString("Hours", values[index] / 60, new Object[0]);
        }
        if (values[index] < 10080) {
            return LocaleController.formatPluralString("Days", values[index] / 1440, new Object[0]);
        }
        if (values[index] < 44640) {
            return LocaleController.formatPluralString("Weeks", values[index] / 10080, new Object[0]);
        }
        if (values[index] < 525600) {
            return LocaleController.formatPluralString("Months", values[index] / 44640, new Object[0]);
        }
        return LocaleController.formatPluralString("Years", values[index] / 525600, new Object[0]);
    }

    public static /* synthetic */ boolean lambda$createMuteForPickerDialog$73(View v, MotionEvent event) {
        return true;
    }

    public static /* synthetic */ void lambda$createMuteForPickerDialog$75(int[] values, NumberPicker numberPicker, ScheduleDatePickerDelegate datePickerDelegate, BottomSheet.Builder builder, View v) {
        int time = values[numberPicker.getValue()] * 60;
        datePickerDelegate.didSelectDate(true, time);
        builder.getDismissRunnable().run();
    }

    private static void checkMuteForButton(NumberPicker dayPicker, NumberPicker hourPicker, TextView buttonTextView, boolean animated) {
        StringBuilder stringBuilder = new StringBuilder();
        if (dayPicker.getValue() != 0) {
            stringBuilder.append(dayPicker.getValue());
            stringBuilder.append(LocaleController.getString("SecretChatTimerDays", R.string.SecretChatTimerDays));
        }
        if (hourPicker.getValue() != 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(hourPicker.getValue());
            stringBuilder.append(LocaleController.getString("SecretChatTimerHours", R.string.SecretChatTimerHours));
        }
        if (stringBuilder.length() == 0) {
            buttonTextView.setText(LocaleController.getString("ChooseTimeForMute", R.string.ChooseTimeForMute));
            if (buttonTextView.isEnabled()) {
                buttonTextView.setEnabled(false);
                if (animated) {
                    buttonTextView.animate().alpha(0.5f);
                    return;
                } else {
                    buttonTextView.setAlpha(0.5f);
                    return;
                }
            }
            return;
        }
        buttonTextView.setText(LocaleController.formatString("MuteForButton", R.string.MuteForButton, stringBuilder.toString()));
        if (!buttonTextView.isEnabled()) {
            buttonTextView.setEnabled(true);
            if (animated) {
                buttonTextView.animate().alpha(1.0f);
            } else {
                buttonTextView.setAlpha(1.0f);
            }
        }
    }

    private static void checkCalendarDate(long minDate, NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker) {
        int day = dayPicker.getValue();
        int month = monthPicker.getValue();
        int year = yearPicker.getValue();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(minDate);
        int minYear = calendar.get(1);
        int minMonth = calendar.get(2);
        int minDay = calendar.get(5);
        calendar.setTimeInMillis(System.currentTimeMillis());
        int maxYear = calendar.get(1);
        int maxMonth = calendar.get(2);
        int maxDay = calendar.get(5);
        if (year > maxYear) {
            year = maxYear;
            yearPicker.setValue(maxYear);
        }
        if (year == maxYear) {
            if (month > maxMonth) {
                month = maxMonth;
                monthPicker.setValue(maxMonth);
            }
            if (month == maxMonth && day > maxDay) {
                day = maxDay;
                dayPicker.setValue(maxDay);
            }
        }
        if (year < minYear) {
            year = minYear;
            yearPicker.setValue(minYear);
        }
        if (year == minYear) {
            if (month < minMonth) {
                month = minMonth;
                monthPicker.setValue(minMonth);
            }
            if (month == minMonth && day < minDay) {
                day = minDay;
                dayPicker.setValue(minDay);
            }
        }
        calendar.set(1, year);
        calendar.set(2, month);
        int daysInMonth = calendar.getActualMaximum(5);
        dayPicker.setMaxValue(daysInMonth);
        if (day > daysInMonth) {
            dayPicker.setValue(daysInMonth);
        }
    }

    public static BottomSheet.Builder createCalendarPickerDialog(Context context, final long minDate, final MessagesStorage.IntCallback callback, Theme.ResourcesProvider resourcesProvider) {
        if (context != null) {
            final BottomSheet.Builder builder = new BottomSheet.Builder(context, false, resourcesProvider);
            builder.setApplyBottomPadding(false);
            final NumberPicker dayPicker = new NumberPicker(context, resourcesProvider);
            dayPicker.setTextOffset(AndroidUtilities.dp(10.0f));
            dayPicker.setItemCount(5);
            final NumberPicker monthPicker = new NumberPicker(context, resourcesProvider);
            monthPicker.setItemCount(5);
            monthPicker.setTextOffset(-AndroidUtilities.dp(10.0f));
            final NumberPicker yearPicker = new NumberPicker(context, resourcesProvider);
            yearPicker.setItemCount(5);
            yearPicker.setTextOffset(-AndroidUtilities.dp(24.0f));
            final LinearLayout container = new LinearLayout(context) { // from class: org.telegram.ui.Components.AlertsCreator.25
                boolean ignoreLayout = false;

                @Override // android.widget.LinearLayout, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int count;
                    this.ignoreLayout = true;
                    if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                        count = 3;
                    } else {
                        count = 5;
                    }
                    dayPicker.setItemCount(count);
                    monthPicker.setItemCount(count);
                    yearPicker.setItemCount(count);
                    dayPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * count;
                    monthPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * count;
                    yearPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * count;
                    this.ignoreLayout = false;
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }

                @Override // android.view.View, android.view.ViewParent
                public void requestLayout() {
                    if (this.ignoreLayout) {
                        return;
                    }
                    super.requestLayout();
                }
            };
            container.setOrientation(1);
            FrameLayout titleLayout = new FrameLayout(context);
            container.addView(titleLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
            TextView titleView = new TextView(context);
            titleView.setText(LocaleController.getString("ChooseDate", R.string.ChooseDate));
            titleView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
            titleView.setTextSize(1, 20.0f);
            titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            titleLayout.addView(titleView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
            titleView.setOnTouchListener(AlertsCreator$$ExternalSyntheticLambda103.INSTANCE);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(0);
            linearLayout.setWeightSum(1.0f);
            container.addView(linearLayout, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
            System.currentTimeMillis();
            TextView buttonTextView = new TextView(context) { // from class: org.telegram.ui.Components.AlertsCreator.26
                @Override // android.widget.TextView, android.view.View
                public CharSequence getAccessibilityClassName() {
                    return Button.class.getName();
                }
            };
            linearLayout.addView(dayPicker, LayoutHelper.createLinear(0, 270, 0.25f));
            dayPicker.setMinValue(1);
            dayPicker.setMaxValue(31);
            dayPicker.setWrapSelectorWheel(false);
            dayPicker.setFormatter(AlertsCreator$$ExternalSyntheticLambda7.INSTANCE);
            NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda27
                @Override // org.telegram.ui.Components.NumberPicker.OnValueChangeListener
                public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
                    AlertsCreator.lambda$createCalendarPickerDialog$78(container, minDate, dayPicker, monthPicker, yearPicker, numberPicker, i, i2);
                }
            };
            dayPicker.setOnValueChangedListener(onValueChangeListener);
            monthPicker.setMinValue(0);
            monthPicker.setMaxValue(11);
            monthPicker.setWrapSelectorWheel(false);
            linearLayout.addView(monthPicker, LayoutHelper.createLinear(0, 270, 0.5f));
            monthPicker.setFormatter(AlertsCreator$$ExternalSyntheticLambda8.INSTANCE);
            monthPicker.setOnValueChangedListener(onValueChangeListener);
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(minDate);
            int minYear = calendar.get(1);
            calendar.setTimeInMillis(System.currentTimeMillis());
            int maxYear = calendar.get(1);
            yearPicker.setMinValue(minYear);
            yearPicker.setMaxValue(maxYear);
            yearPicker.setWrapSelectorWheel(false);
            yearPicker.setFormatter(AlertsCreator$$ExternalSyntheticLambda9.INSTANCE);
            linearLayout.addView(yearPicker, LayoutHelper.createLinear(0, 270, 0.25f));
            yearPicker.setOnValueChangedListener(onValueChangeListener);
            dayPicker.setValue(31);
            monthPicker.setValue(12);
            yearPicker.setValue(maxYear);
            checkCalendarDate(minDate, dayPicker, monthPicker, yearPicker);
            buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            buttonTextView.setGravity(17);
            buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText, resourcesProvider));
            buttonTextView.setTextSize(1, 14.0f);
            buttonTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            buttonTextView.setText(LocaleController.getString("JumpToDate", R.string.JumpToDate));
            buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor(Theme.key_featuredStickers_addButton, resourcesProvider), Theme.getColor(Theme.key_featuredStickers_addButtonPressed, resourcesProvider)));
            container.addView(buttonTextView, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
            buttonTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda71
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AlertsCreator.lambda$createCalendarPickerDialog$81(minDate, dayPicker, monthPicker, yearPicker, calendar, callback, builder, view);
                }
            });
            builder.setCustomView(container);
            return builder;
        }
        return null;
    }

    public static /* synthetic */ boolean lambda$createCalendarPickerDialog$76(View v, MotionEvent event) {
        return true;
    }

    public static /* synthetic */ String lambda$createCalendarPickerDialog$77(int value) {
        return "" + value;
    }

    public static /* synthetic */ void lambda$createCalendarPickerDialog$78(LinearLayout container, long minDate, NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker, NumberPicker picker, int oldVal, int newVal) {
        try {
            container.performHapticFeedback(3, 2);
        } catch (Exception e) {
        }
        checkCalendarDate(minDate, dayPicker, monthPicker, yearPicker);
    }

    public static /* synthetic */ String lambda$createCalendarPickerDialog$79(int value) {
        switch (value) {
            case 0:
                return LocaleController.getString("January", R.string.January);
            case 1:
                return LocaleController.getString("February", R.string.February);
            case 2:
                return LocaleController.getString("March", R.string.March);
            case 3:
                return LocaleController.getString("April", R.string.April);
            case 4:
                return LocaleController.getString("May", R.string.May);
            case 5:
                return LocaleController.getString("June", R.string.June);
            case 6:
                return LocaleController.getString("July", R.string.July);
            case 7:
                return LocaleController.getString("August", R.string.August);
            case 8:
                return LocaleController.getString("September", R.string.September);
            case 9:
                return LocaleController.getString("October", R.string.October);
            case 10:
                return LocaleController.getString("November", R.string.November);
            default:
                return LocaleController.getString("December", R.string.December);
        }
    }

    public static /* synthetic */ void lambda$createCalendarPickerDialog$81(long minDate, NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker, Calendar calendar, MessagesStorage.IntCallback callback, BottomSheet.Builder builder, View v) {
        checkCalendarDate(minDate, dayPicker, monthPicker, yearPicker);
        calendar.set(1, yearPicker.getValue());
        calendar.set(2, monthPicker.getValue());
        calendar.set(5, dayPicker.getValue());
        calendar.set(12, 0);
        calendar.set(11, 0);
        calendar.set(13, 0);
        callback.run((int) (calendar.getTimeInMillis() / 1000));
        builder.getDismissRunnable().run();
    }

    public static BottomSheet createMuteAlert(final BaseFragment fragment, final long dialog_id, final Theme.ResourcesProvider resourcesProvider) {
        if (fragment == null || fragment.getParentActivity() == null) {
            return null;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder((Context) fragment.getParentActivity(), false, resourcesProvider);
        builder.setTitle(LocaleController.getString("Notifications", R.string.Notifications), true);
        CharSequence[] items = {LocaleController.formatString("MuteFor", R.string.MuteFor, LocaleController.formatPluralString("Hours", 1, new Object[0])), LocaleController.formatString("MuteFor", R.string.MuteFor, LocaleController.formatPluralString("Hours", 8, new Object[0])), LocaleController.formatString("MuteFor", R.string.MuteFor, LocaleController.formatPluralString("Days", 2, new Object[0])), LocaleController.getString("MuteDisable", R.string.MuteDisable)};
        builder.setItems(items, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda73
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createMuteAlert$82(dialog_id, fragment, resourcesProvider, dialogInterface, i);
            }
        });
        return builder.create();
    }

    public static /* synthetic */ void lambda$createMuteAlert$82(long dialog_id, BaseFragment fragment, Theme.ResourcesProvider resourcesProvider, DialogInterface dialogInterface, int i) {
        int setting;
        if (i == 0) {
            setting = 0;
        } else if (i == 1) {
            setting = 1;
        } else if (i == 2) {
            setting = 2;
        } else {
            setting = 3;
        }
        NotificationsController.getInstance(UserConfig.selectedAccount).setDialogNotificationsSettings(dialog_id, setting);
        if (BulletinFactory.canShowBulletin(fragment)) {
            BulletinFactory.createMuteBulletin(fragment, setting, 0, resourcesProvider).show();
        }
    }

    public static void sendReport(TLRPC.InputPeer peer, int type, String message, ArrayList<Integer> messages) {
        TLRPC.TL_messages_report request = new TLRPC.TL_messages_report();
        request.peer = peer;
        request.id.addAll(messages);
        request.message = message;
        if (type == 0) {
            request.reason = new TLRPC.TL_inputReportReasonSpam();
        } else if (type == 6) {
            request.reason = new TLRPC.TL_inputReportReasonFake();
        } else if (type == 1) {
            request.reason = new TLRPC.TL_inputReportReasonViolence();
        } else if (type == 2) {
            request.reason = new TLRPC.TL_inputReportReasonChildAbuse();
        } else if (type == 5) {
            request.reason = new TLRPC.TL_inputReportReasonPornography();
        } else if (type == 3) {
            request.reason = new TLRPC.TL_inputReportReasonIllegalDrugs();
        } else if (type == 4) {
            request.reason = new TLRPC.TL_inputReportReasonPersonalDetails();
        } else if (type == 100) {
            request.reason = new TLRPC.TL_inputReportReasonOther();
        }
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(request, AlertsCreator$$ExternalSyntheticLambda1.INSTANCE);
    }

    public static /* synthetic */ void lambda$sendReport$83(TLObject response, TLRPC.TL_error error) {
    }

    public static void createReportAlert(Context context, long dialog_id, int messageId, BaseFragment parentFragment, Runnable hideDim) {
        createReportAlert(context, dialog_id, messageId, parentFragment, null, hideDim);
    }

    public static void createReportAlert(final Context context, final long dialog_id, final int messageId, final BaseFragment parentFragment, final Theme.ResourcesProvider resourcesProvider, final Runnable hideDim) {
        int[] icons;
        int[] types;
        CharSequence[] items;
        if (context != null && parentFragment != null) {
            BottomSheet.Builder builder = new BottomSheet.Builder(context, true, resourcesProvider);
            builder.setDimBehind(hideDim == null);
            builder.setOnPreDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda65
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    AlertsCreator.lambda$createReportAlert$84(hideDim, dialogInterface);
                }
            });
            builder.setTitle(LocaleController.getString("ReportChat", R.string.ReportChat), true);
            if (messageId != 0) {
                CharSequence[] items2 = {LocaleController.getString("ReportChatSpam", R.string.ReportChatSpam), LocaleController.getString("ReportChatViolence", R.string.ReportChatViolence), LocaleController.getString("ReportChatChild", R.string.ReportChatChild), LocaleController.getString("ReportChatIllegalDrugs", R.string.ReportChatIllegalDrugs), LocaleController.getString("ReportChatPersonalDetails", R.string.ReportChatPersonalDetails), LocaleController.getString("ReportChatPornography", R.string.ReportChatPornography), LocaleController.getString("ReportChatOther", R.string.ReportChatOther)};
                int[] icons2 = {R.drawable.msg_clearcache, R.drawable.msg_report_violence, R.drawable.msg_block2, R.drawable.msg_report_drugs, R.drawable.msg_report_personal, R.drawable.msg_report_xxx, R.drawable.msg_report_other};
                items = items2;
                types = new int[]{0, 1, 2, 3, 4, 5, 100};
                icons = icons2;
            } else {
                CharSequence[] items3 = {LocaleController.getString("ReportChatSpam", R.string.ReportChatSpam), LocaleController.getString("ReportChatFakeAccount", R.string.ReportChatFakeAccount), LocaleController.getString("ReportChatViolence", R.string.ReportChatViolence), LocaleController.getString("ReportChatChild", R.string.ReportChatChild), LocaleController.getString("ReportChatIllegalDrugs", R.string.ReportChatIllegalDrugs), LocaleController.getString("ReportChatPersonalDetails", R.string.ReportChatPersonalDetails), LocaleController.getString("ReportChatPornography", R.string.ReportChatPornography), LocaleController.getString("ReportChatOther", R.string.ReportChatOther)};
                int[] icons3 = {R.drawable.msg_clearcache, R.drawable.msg_report_fake, R.drawable.msg_report_violence, R.drawable.msg_block2, R.drawable.msg_report_drugs, R.drawable.msg_report_personal, R.drawable.msg_report_xxx, R.drawable.msg_report_other};
                items = items3;
                types = new int[]{0, 6, 1, 2, 3, 4, 5, 100};
                icons = icons3;
            }
            final int[] iArr = types;
            builder.setItems(items, icons, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda58
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    AlertsCreator.lambda$createReportAlert$86(iArr, messageId, parentFragment, context, dialog_id, resourcesProvider, dialogInterface, i);
                }
            });
            BottomSheet sheet = builder.create();
            parentFragment.showDialog(sheet);
        }
    }

    public static /* synthetic */ void lambda$createReportAlert$84(Runnable hideDim, DialogInterface di) {
        if (hideDim != null) {
            hideDim.run();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static /* synthetic */ void lambda$createReportAlert$86(int[] types, final int messageId, final BaseFragment parentFragment, Context context, final long dialog_id, Theme.ResourcesProvider resourcesProvider, DialogInterface dialogInterface, int i) {
        TLObject req;
        int type = types[i];
        if (messageId == 0 && ((type == 0 || type == 1 || type == 2 || type == 5 || type == 3 || type == 4) && (parentFragment instanceof ChatActivity))) {
            ((ChatActivity) parentFragment).openReportChat(type);
        } else if ((messageId == 0 && (type == 100 || type == 6)) || (messageId != 0 && type == 100)) {
            if (parentFragment instanceof ChatActivity) {
                AndroidUtilities.requestAdjustNothing(parentFragment.getParentActivity(), parentFragment.getClassGuid());
            }
            parentFragment.showDialog(new ReportAlert(context, type) { // from class: org.telegram.ui.Components.AlertsCreator.27
                @Override // org.telegram.ui.ActionBar.BottomSheet
                public void dismissInternal() {
                    super.dismissInternal();
                    BaseFragment baseFragment = parentFragment;
                    if (baseFragment instanceof ChatActivity) {
                        ((ChatActivity) baseFragment).checkAdjustResize();
                    }
                }

                @Override // org.telegram.ui.Components.ReportAlert
                protected void onSend(int type2, String message) {
                    ArrayList ids = new ArrayList();
                    int i2 = messageId;
                    if (i2 != 0) {
                        ids.add(Integer.valueOf(i2));
                    }
                    TLRPC.InputPeer peer = MessagesController.getInstance(UserConfig.selectedAccount).getInputPeer(dialog_id);
                    AlertsCreator.sendReport(peer, type2, message, ids);
                    BaseFragment baseFragment = parentFragment;
                    if (baseFragment instanceof ChatActivity) {
                        ((ChatActivity) baseFragment).getUndoView().showWithAction(0L, 74, (Runnable) null);
                    }
                }
            });
        } else {
            TLRPC.InputPeer peer = MessagesController.getInstance(UserConfig.selectedAccount).getInputPeer(dialog_id);
            if (messageId != 0) {
                TLRPC.TL_messages_report request = new TLRPC.TL_messages_report();
                request.peer = peer;
                request.id.add(Integer.valueOf(messageId));
                request.message = "";
                if (type == 0) {
                    request.reason = new TLRPC.TL_inputReportReasonSpam();
                } else if (type == 1) {
                    request.reason = new TLRPC.TL_inputReportReasonViolence();
                } else if (type == 2) {
                    request.reason = new TLRPC.TL_inputReportReasonChildAbuse();
                } else if (type == 5) {
                    request.reason = new TLRPC.TL_inputReportReasonPornography();
                } else if (type == 3) {
                    request.reason = new TLRPC.TL_inputReportReasonIllegalDrugs();
                } else if (type == 4) {
                    request.reason = new TLRPC.TL_inputReportReasonPersonalDetails();
                }
                req = request;
            } else {
                TLRPC.TL_account_reportPeer request2 = new TLRPC.TL_account_reportPeer();
                request2.peer = peer;
                request2.message = "";
                if (type == 0) {
                    request2.reason = new TLRPC.TL_inputReportReasonSpam();
                } else if (type == 6) {
                    request2.reason = new TLRPC.TL_inputReportReasonFake();
                } else if (type == 1) {
                    request2.reason = new TLRPC.TL_inputReportReasonViolence();
                } else if (type == 2) {
                    request2.reason = new TLRPC.TL_inputReportReasonChildAbuse();
                } else if (type == 5) {
                    request2.reason = new TLRPC.TL_inputReportReasonPornography();
                } else if (type == 3) {
                    request2.reason = new TLRPC.TL_inputReportReasonIllegalDrugs();
                } else if (type == 4) {
                    request2.reason = new TLRPC.TL_inputReportReasonPersonalDetails();
                }
                req = request2;
            }
            ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(req, AlertsCreator$$ExternalSyntheticLambda127.INSTANCE);
            if (parentFragment instanceof ChatActivity) {
                ((ChatActivity) parentFragment).getUndoView().showWithAction(0L, 74, (Runnable) null);
            } else {
                BulletinFactory.of(parentFragment).createReportSent(resourcesProvider).show();
            }
        }
    }

    public static /* synthetic */ void lambda$createReportAlert$85(TLObject response, TLRPC.TL_error error) {
    }

    private static String getFloodWaitString(String error) {
        String timeString;
        int time = Utilities.parseInt((CharSequence) error).intValue();
        if (time < 60) {
            timeString = LocaleController.formatPluralString("Seconds", time, new Object[0]);
        } else {
            timeString = LocaleController.formatPluralString("Minutes", time / 60, new Object[0]);
        }
        return LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString);
    }

    public static void showFloodWaitAlert(String error, BaseFragment fragment) {
        String timeString;
        if (error == null || !error.startsWith("FLOOD_WAIT") || fragment == null || fragment.getParentActivity() == null) {
            return;
        }
        int time = Utilities.parseInt((CharSequence) error).intValue();
        if (time < 60) {
            timeString = LocaleController.formatPluralString("Seconds", time, new Object[0]);
        } else {
            timeString = LocaleController.formatPluralString("Minutes", time / 60, new Object[0]);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        fragment.showDialog(builder.create(), true, null);
    }

    public static void showSendMediaAlert(int result, BaseFragment fragment, Theme.ResourcesProvider resourcesProvider) {
        if (result == 0) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getParentActivity(), resourcesProvider);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        if (result == 1) {
            builder.setMessage(LocaleController.getString("ErrorSendRestrictedStickers", R.string.ErrorSendRestrictedStickers));
        } else if (result == 2) {
            builder.setMessage(LocaleController.getString("ErrorSendRestrictedMedia", R.string.ErrorSendRestrictedMedia));
        } else if (result == 3) {
            builder.setMessage(LocaleController.getString("ErrorSendRestrictedPolls", R.string.ErrorSendRestrictedPolls));
        } else if (result == 4) {
            builder.setMessage(LocaleController.getString("ErrorSendRestrictedStickersAll", R.string.ErrorSendRestrictedStickersAll));
        } else if (result == 5) {
            builder.setMessage(LocaleController.getString("ErrorSendRestrictedMediaAll", R.string.ErrorSendRestrictedMediaAll));
        } else if (result == 6) {
            builder.setMessage(LocaleController.getString("ErrorSendRestrictedPollsAll", R.string.ErrorSendRestrictedPollsAll));
        }
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        fragment.showDialog(builder.create(), true, null);
    }

    public static void showAddUserAlert(String error, final BaseFragment fragment, boolean isChannel, TLObject request) {
        if (error == null || fragment == null || fragment.getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        char c = 65535;
        switch (error.hashCode()) {
            case -2120721660:
                if (error.equals("CHANNELS_ADMIN_LOCATED_TOO_MUCH")) {
                    c = 17;
                    break;
                }
                break;
            case -2012133105:
                if (error.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                    c = 16;
                    break;
                }
                break;
            case -1763467626:
                if (error.equals("USERS_TOO_FEW")) {
                    c = '\t';
                    break;
                }
                break;
            case -538116776:
                if (error.equals("USER_BLOCKED")) {
                    c = 1;
                    break;
                }
                break;
            case -512775857:
                if (error.equals("USER_RESTRICTED")) {
                    c = '\n';
                    break;
                }
                break;
            case -454039871:
                if (error.equals("PEER_FLOOD")) {
                    c = 0;
                    break;
                }
                break;
            case -420079733:
                if (error.equals("BOTS_TOO_MUCH")) {
                    c = 7;
                    break;
                }
                break;
            case 98635865:
                if (error.equals("USER_KICKED")) {
                    c = '\r';
                    break;
                }
                break;
            case 517420851:
                if (error.equals("USER_BOT")) {
                    c = 2;
                    break;
                }
                break;
            case 845559454:
                if (error.equals("YOU_BLOCKED_USER")) {
                    c = 11;
                    break;
                }
                break;
            case 916342611:
                if (error.equals("USER_ADMIN_INVALID")) {
                    c = 15;
                    break;
                }
                break;
            case 1047173446:
                if (error.equals("CHAT_ADMIN_BAN_REQUIRED")) {
                    c = '\f';
                    break;
                }
                break;
            case 1167301807:
                if (error.equals("USERS_TOO_MUCH")) {
                    c = 4;
                    break;
                }
                break;
            case 1227003815:
                if (error.equals("USER_ID_INVALID")) {
                    c = 3;
                    break;
                }
                break;
            case 1253103379:
                if (error.equals("ADMINS_TOO_MUCH")) {
                    c = 6;
                    break;
                }
                break;
            case 1355367367:
                if (error.equals("CHANNELS_TOO_MUCH")) {
                    c = 18;
                    break;
                }
                break;
            case 1377621075:
                if (error.equals("USER_CHANNELS_TOO_MUCH")) {
                    c = 19;
                    break;
                }
                break;
            case 1623167701:
                if (error.equals("USER_NOT_MUTUAL_CONTACT")) {
                    c = 5;
                    break;
                }
                break;
            case 1754587486:
                if (error.equals("CHAT_ADMIN_INVITE_REQUIRED")) {
                    c = 14;
                    break;
                }
                break;
            case 1916725894:
                if (error.equals("USER_PRIVACY_RESTRICTED")) {
                    c = '\b';
                    break;
                }
                break;
            case 1965565720:
                if (error.equals("USER_ALREADY_PARTICIPANT")) {
                    c = 20;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                builder.setMessage(LocaleController.getString("NobodyLikesSpam2", R.string.NobodyLikesSpam2));
                builder.setNegativeButton(LocaleController.getString("MoreInfo", R.string.MoreInfo), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda47
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        MessagesController.getInstance(r0.getCurrentAccount()).openByUserName("spambot", BaseFragment.this, 1);
                    }
                });
                break;
            case 1:
            case 2:
            case 3:
                if (isChannel) {
                    builder.setMessage(LocaleController.getString("ChannelUserCantAdd", R.string.ChannelUserCantAdd));
                    break;
                } else {
                    builder.setMessage(LocaleController.getString("GroupUserCantAdd", R.string.GroupUserCantAdd));
                    break;
                }
            case 4:
                if (isChannel) {
                    builder.setMessage(LocaleController.getString("ChannelUserAddLimit", R.string.ChannelUserAddLimit));
                    break;
                } else {
                    builder.setMessage(LocaleController.getString("GroupUserAddLimit", R.string.GroupUserAddLimit));
                    break;
                }
            case 5:
                if (isChannel) {
                    builder.setMessage(LocaleController.getString("ChannelUserLeftError", R.string.ChannelUserLeftError));
                    break;
                } else {
                    builder.setMessage(LocaleController.getString("GroupUserLeftError", R.string.GroupUserLeftError));
                    break;
                }
            case 6:
                if (isChannel) {
                    builder.setMessage(LocaleController.getString("ChannelUserCantAdmin", R.string.ChannelUserCantAdmin));
                    break;
                } else {
                    builder.setMessage(LocaleController.getString("GroupUserCantAdmin", R.string.GroupUserCantAdmin));
                    break;
                }
            case 7:
                if (isChannel) {
                    builder.setMessage(LocaleController.getString("ChannelUserCantBot", R.string.ChannelUserCantBot));
                    break;
                } else {
                    builder.setMessage(LocaleController.getString("GroupUserCantBot", R.string.GroupUserCantBot));
                    break;
                }
            case '\b':
                if (isChannel) {
                    builder.setMessage(LocaleController.getString("InviteToChannelError", R.string.InviteToChannelError));
                    break;
                } else {
                    builder.setMessage(LocaleController.getString("InviteToGroupError", R.string.InviteToGroupError));
                    break;
                }
            case '\t':
                builder.setMessage(LocaleController.getString("CreateGroupError", R.string.CreateGroupError));
                break;
            case '\n':
                builder.setMessage(LocaleController.getString("UserRestricted", R.string.UserRestricted));
                break;
            case 11:
                builder.setMessage(LocaleController.getString("YouBlockedUser", R.string.YouBlockedUser));
                break;
            case '\f':
            case '\r':
                if (request instanceof TLRPC.TL_channels_inviteToChannel) {
                    builder.setMessage(LocaleController.getString("AddUserErrorBlacklisted", R.string.AddUserErrorBlacklisted));
                    break;
                } else {
                    builder.setMessage(LocaleController.getString("AddAdminErrorBlacklisted", R.string.AddAdminErrorBlacklisted));
                    break;
                }
            case 14:
                builder.setMessage(LocaleController.getString("AddAdminErrorNotAMember", R.string.AddAdminErrorNotAMember));
                break;
            case 15:
                builder.setMessage(LocaleController.getString("AddBannedErrorAdmin", R.string.AddBannedErrorAdmin));
                break;
            case 16:
                builder.setMessage(LocaleController.getString("PublicChannelsTooMuch", R.string.PublicChannelsTooMuch));
                break;
            case 17:
                builder.setMessage(LocaleController.getString("LocatedChannelsTooMuch", R.string.LocatedChannelsTooMuch));
                break;
            case 18:
                builder.setTitle(LocaleController.getString("ChannelTooMuchTitle", R.string.ChannelTooMuchTitle));
                if (request instanceof TLRPC.TL_channels_createChannel) {
                    builder.setMessage(LocaleController.getString("ChannelTooMuch", R.string.ChannelTooMuch));
                    break;
                } else {
                    builder.setMessage(LocaleController.getString("ChannelTooMuchJoin", R.string.ChannelTooMuchJoin));
                    break;
                }
            case 19:
                builder.setTitle(LocaleController.getString("ChannelTooMuchTitle", R.string.ChannelTooMuchTitle));
                builder.setMessage(LocaleController.getString("UserChannelTooMuchJoin", R.string.UserChannelTooMuchJoin));
                break;
            case 20:
                builder.setTitle(LocaleController.getString("VoipGroupVoiceChat", R.string.VoipGroupVoiceChat));
                builder.setMessage(LocaleController.getString("VoipGroupInviteAlreadyParticipant", R.string.VoipGroupInviteAlreadyParticipant));
                break;
            default:
                builder.setMessage(LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error);
                break;
        }
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        fragment.showDialog(builder.create(), true, null);
    }

    public static Dialog createColorSelectDialog(Activity parentActivity, long dialog_id, int globalType, Runnable onSelect) {
        return createColorSelectDialog(parentActivity, dialog_id, globalType, onSelect, null);
    }

    public static Dialog createColorSelectDialog(Activity parentActivity, final long dialog_id, final int globalType, final Runnable onSelect, Theme.ResourcesProvider resourcesProvider) {
        int currentColor;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        if (dialog_id != 0) {
            if (preferences.contains("color_" + dialog_id)) {
                currentColor = preferences.getInt("color_" + dialog_id, -16776961);
            } else if (DialogObject.isChatDialog(dialog_id)) {
                currentColor = preferences.getInt("GroupLed", -16776961);
            } else {
                currentColor = preferences.getInt("MessagesLed", -16776961);
            }
        } else if (globalType == 1) {
            currentColor = preferences.getInt("MessagesLed", -16776961);
        } else if (globalType == 0) {
            currentColor = preferences.getInt("GroupLed", -16776961);
        } else {
            currentColor = preferences.getInt("ChannelLed", -16776961);
        }
        final LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        String[] descriptions = {LocaleController.getString("ColorRed", R.string.ColorRed), LocaleController.getString("ColorOrange", R.string.ColorOrange), LocaleController.getString("ColorYellow", R.string.ColorYellow), LocaleController.getString("ColorGreen", R.string.ColorGreen), LocaleController.getString("ColorCyan", R.string.ColorCyan), LocaleController.getString("ColorBlue", R.string.ColorBlue), LocaleController.getString("ColorViolet", R.string.ColorViolet), LocaleController.getString("ColorPink", R.string.ColorPink), LocaleController.getString("ColorWhite", R.string.ColorWhite)};
        final int[] selectedColor = {currentColor};
        int a = 0;
        for (int i = 9; a < i; i = 9) {
            RadioColorCell cell = new RadioColorCell(parentActivity, resourcesProvider);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(TextColorCell.colors[a], TextColorCell.colors[a]);
            cell.setTextAndValue(descriptions[a], currentColor == TextColorCell.colorsToSave[a]);
            linearLayout.addView(cell);
            cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda72
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AlertsCreator.lambda$createColorSelectDialog$88(linearLayout, selectedColor, view);
                }
            });
            a++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity, resourcesProvider);
        builder.setTitle(LocaleController.getString("LedColor", R.string.LedColor));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Set", R.string.Set), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda84
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                AlertsCreator.lambda$createColorSelectDialog$89(dialog_id, selectedColor, globalType, onSelect, dialogInterface, i2);
            }
        });
        builder.setNeutralButton(LocaleController.getString("LedDisabled", R.string.LedDisabled), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda51
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                AlertsCreator.lambda$createColorSelectDialog$90(dialog_id, globalType, onSelect, dialogInterface, i2);
            }
        });
        if (dialog_id != 0) {
            builder.setNegativeButton(LocaleController.getString("Default", R.string.Default), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda62
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    AlertsCreator.lambda$createColorSelectDialog$91(dialog_id, onSelect, dialogInterface, i2);
                }
            });
        }
        return builder.create();
    }

    public static /* synthetic */ void lambda$createColorSelectDialog$88(LinearLayout linearLayout, int[] selectedColor, View v) {
        int count = linearLayout.getChildCount();
        int a1 = 0;
        while (true) {
            boolean z = false;
            if (a1 < count) {
                RadioColorCell cell1 = (RadioColorCell) linearLayout.getChildAt(a1);
                if (cell1 == v) {
                    z = true;
                }
                cell1.setChecked(z, true);
                a1++;
            } else {
                selectedColor[0] = TextColorCell.colorsToSave[((Integer) v.getTag()).intValue()];
                return;
            }
        }
    }

    public static /* synthetic */ void lambda$createColorSelectDialog$89(long dialog_id, int[] selectedColor, int globalType, Runnable onSelect, DialogInterface dialogInterface, int which) {
        SharedPreferences preferences1 = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        SharedPreferences.Editor editor = preferences1.edit();
        if (dialog_id != 0) {
            editor.putInt("color_" + dialog_id, selectedColor[0]);
            NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannel(dialog_id);
        } else {
            if (globalType == 1) {
                editor.putInt("MessagesLed", selectedColor[0]);
            } else if (globalType == 0) {
                editor.putInt("GroupLed", selectedColor[0]);
            } else {
                editor.putInt("ChannelLed", selectedColor[0]);
            }
            NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannelGlobal(globalType);
        }
        editor.commit();
        if (onSelect != null) {
            onSelect.run();
        }
    }

    public static /* synthetic */ void lambda$createColorSelectDialog$90(long dialog_id, int globalType, Runnable onSelect, DialogInterface dialog, int which) {
        SharedPreferences preferences12 = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        SharedPreferences.Editor editor = preferences12.edit();
        if (dialog_id != 0) {
            editor.putInt("color_" + dialog_id, 0);
        } else if (globalType == 1) {
            editor.putInt("MessagesLed", 0);
        } else if (globalType == 0) {
            editor.putInt("GroupLed", 0);
        } else {
            editor.putInt("ChannelLed", 0);
        }
        editor.commit();
        if (onSelect != null) {
            onSelect.run();
        }
    }

    public static /* synthetic */ void lambda$createColorSelectDialog$91(long dialog_id, Runnable onSelect, DialogInterface dialog, int which) {
        SharedPreferences preferences13 = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        SharedPreferences.Editor editor = preferences13.edit();
        editor.remove("color_" + dialog_id);
        editor.commit();
        if (onSelect != null) {
            onSelect.run();
        }
    }

    public static Dialog createVibrationSelectDialog(Activity parentActivity, long dialogId, boolean globalGroup, boolean globalAll, Runnable onSelect) {
        return createVibrationSelectDialog(parentActivity, dialogId, globalGroup, globalAll, onSelect, null);
    }

    public static Dialog createVibrationSelectDialog(Activity parentActivity, long dialogId, boolean globalGroup, boolean globalAll, Runnable onSelect, Theme.ResourcesProvider resourcesProvider) {
        String prefix;
        if (dialogId != 0) {
            prefix = "vibrate_" + dialogId;
        } else {
            prefix = globalGroup ? "vibrate_group" : "vibrate_messages";
        }
        return createVibrationSelectDialog(parentActivity, dialogId, prefix, onSelect, resourcesProvider);
    }

    public static Dialog createVibrationSelectDialog(Activity parentActivity, long dialogId, String prefKeyPrefix, Runnable onSelect) {
        return createVibrationSelectDialog(parentActivity, dialogId, prefKeyPrefix, onSelect, (Theme.ResourcesProvider) null);
    }

    public static Dialog createVibrationSelectDialog(Activity parentActivity, final long dialogId, final String prefKeyPrefix, final Runnable onSelect, Theme.ResourcesProvider resourcesProvider) {
        String[] descriptions;
        Activity activity = parentActivity;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        final int[] selected = new int[1];
        int i = 0;
        if (dialogId != 0) {
            selected[0] = preferences.getInt(prefKeyPrefix, 0);
            if (selected[0] == 3) {
                selected[0] = 2;
            } else if (selected[0] == 2) {
                selected[0] = 3;
            }
            descriptions = new String[]{LocaleController.getString("VibrationDefault", R.string.VibrationDefault), LocaleController.getString("Short", R.string.Short), LocaleController.getString("Long", R.string.Long), LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled)};
        } else {
            selected[0] = preferences.getInt(prefKeyPrefix, 0);
            if (selected[0] == 0) {
                selected[0] = 1;
            } else if (selected[0] == 1) {
                selected[0] = 2;
            } else if (selected[0] == 2) {
                selected[0] = 0;
            }
            descriptions = new String[]{LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled), LocaleController.getString("VibrationDefault", R.string.VibrationDefault), LocaleController.getString("Short", R.string.Short), LocaleController.getString("Long", R.string.Long), LocaleController.getString("OnlyIfSilent", R.string.OnlyIfSilent)};
        }
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity, resourcesProvider);
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(activity, resourcesProvider);
            cell.setPadding(AndroidUtilities.dp(4.0f), i, AndroidUtilities.dp(4.0f), i);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground, resourcesProvider), Theme.getColor(Theme.key_dialogRadioBackgroundChecked, resourcesProvider));
            cell.setTextAndValue(descriptions[a], selected[i] == a);
            linearLayout.addView(cell);
            cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda83
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AlertsCreator.lambda$createVibrationSelectDialog$92(selected, dialogId, prefKeyPrefix, builder, onSelect, view);
                }
            });
            a++;
            i = 0;
            activity = parentActivity;
        }
        builder.setTitle(LocaleController.getString("Vibrate", R.string.Vibrate));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        return builder.create();
    }

    public static /* synthetic */ void lambda$createVibrationSelectDialog$92(int[] selected, long dialogId, String prefKeyPrefix, AlertDialog.Builder builder, Runnable onSelect, View v) {
        selected[0] = ((Integer) v.getTag()).intValue();
        SharedPreferences preferences1 = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        SharedPreferences.Editor editor = preferences1.edit();
        if (dialogId != 0) {
            if (selected[0] == 0) {
                editor.putInt(prefKeyPrefix, 0);
            } else if (selected[0] == 1) {
                editor.putInt(prefKeyPrefix, 1);
            } else if (selected[0] != 2) {
                if (selected[0] == 3) {
                    editor.putInt(prefKeyPrefix, 2);
                }
            } else {
                editor.putInt(prefKeyPrefix, 3);
            }
            NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannel(dialogId);
        } else {
            if (selected[0] == 0) {
                editor.putInt(prefKeyPrefix, 2);
            } else if (selected[0] == 1) {
                editor.putInt(prefKeyPrefix, 0);
            } else if (selected[0] != 2) {
                if (selected[0] == 3) {
                    editor.putInt(prefKeyPrefix, 3);
                } else if (selected[0] == 4) {
                    editor.putInt(prefKeyPrefix, 4);
                }
            } else {
                editor.putInt(prefKeyPrefix, 1);
            }
            if (prefKeyPrefix.equals("vibrate_channel")) {
                NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannelGlobal(2);
            } else if (prefKeyPrefix.equals("vibrate_group")) {
                NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannelGlobal(0);
            } else {
                NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannelGlobal(1);
            }
        }
        editor.commit();
        builder.getDismissRunnable().run();
        if (onSelect != null) {
            onSelect.run();
        }
    }

    public static Dialog createLocationUpdateDialog(Activity parentActivity, TLRPC.User user, final MessagesStorage.IntCallback callback, Theme.ResourcesProvider resourcesProvider) {
        final int[] selected = new int[1];
        int i = 3;
        String[] descriptions = {LocaleController.getString("SendLiveLocationFor15m", R.string.SendLiveLocationFor15m), LocaleController.getString("SendLiveLocationFor1h", R.string.SendLiveLocationFor1h), LocaleController.getString("SendLiveLocationFor8h", R.string.SendLiveLocationFor8h)};
        final LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        TextView titleTextView = new TextView(parentActivity);
        if (user != null) {
            titleTextView.setText(LocaleController.formatString("LiveLocationAlertPrivate", R.string.LiveLocationAlertPrivate, UserObject.getFirstName(user)));
        } else {
            titleTextView.setText(LocaleController.getString("LiveLocationAlertGroup", R.string.LiveLocationAlertGroup));
        }
        int textColor = resourcesProvider != null ? resourcesProvider.getColorOrDefault(Theme.key_dialogTextBlack) : Theme.getColor(Theme.key_dialogTextBlack);
        titleTextView.setTextColor(textColor);
        titleTextView.setTextSize(1, 16.0f);
        titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        if (LocaleController.isRTL) {
            i = 5;
        }
        linearLayout.addView(titleTextView, LayoutHelper.createLinear(-2, -2, i | 48, 24, 0, 24, 8));
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(parentActivity, resourcesProvider);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            int color1 = resourcesProvider != null ? resourcesProvider.getColorOrDefault(Theme.key_radioBackground) : Theme.getColor(Theme.key_radioBackground);
            int color2 = resourcesProvider != null ? resourcesProvider.getColorOrDefault(Theme.key_dialogRadioBackgroundChecked) : Theme.getColor(Theme.key_dialogRadioBackgroundChecked);
            cell.setCheckColor(color1, color2);
            cell.setTextAndValue(descriptions[a], selected[0] == a);
            linearLayout.addView(cell);
            cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda86
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AlertsCreator.lambda$createLocationUpdateDialog$93(selected, linearLayout, view);
                }
            });
            a++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity, resourcesProvider);
        int topImageColor = resourcesProvider != null ? resourcesProvider.getColorOrDefault(Theme.key_dialogTopBackground) : Theme.getColor(Theme.key_dialogTopBackground);
        builder.setTopImage(new ShareLocationDrawable(parentActivity, 0), topImageColor);
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("ShareFile", R.string.ShareFile), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda59
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                AlertsCreator.lambda$createLocationUpdateDialog$94(selected, callback, dialogInterface, i2);
            }
        });
        builder.setNeutralButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        return builder.create();
    }

    public static /* synthetic */ void lambda$createLocationUpdateDialog$93(int[] selected, LinearLayout linearLayout, View v) {
        int num = ((Integer) v.getTag()).intValue();
        selected[0] = num;
        int count = linearLayout.getChildCount();
        for (int a1 = 0; a1 < count; a1++) {
            View child = linearLayout.getChildAt(a1);
            if (child instanceof RadioColorCell) {
                ((RadioColorCell) child).setChecked(child == v, true);
            }
        }
    }

    public static /* synthetic */ void lambda$createLocationUpdateDialog$94(int[] selected, MessagesStorage.IntCallback callback, DialogInterface dialog, int which) {
        int time;
        if (selected[0] == 0) {
            time = 900;
        } else {
            int time2 = selected[0];
            if (time2 == 1) {
                time = 3600;
            } else {
                time = 28800;
            }
        }
        callback.run(time);
    }

    public static AlertDialog.Builder createBackgroundLocationPermissionDialog(final Activity activity, TLRPC.User selfUser, final Runnable cancelRunnable, Theme.ResourcesProvider resourcesProvider) {
        if (activity != null && Build.VERSION.SDK_INT >= 29) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, resourcesProvider);
            String svg = RLottieDrawable.readRes(null, Theme.getCurrentTheme().isDark() ? R.raw.permission_map_dark : R.raw.permission_map);
            String pinSvg = RLottieDrawable.readRes(null, Theme.getCurrentTheme().isDark() ? R.raw.permission_pin_dark : R.raw.permission_pin);
            FrameLayout frameLayout = new FrameLayout(activity);
            frameLayout.setClipToOutline(true);
            frameLayout.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.AlertsCreator.28
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f));
                }
            });
            View background = new View(activity);
            background.setBackground(SvgHelper.getDrawable(svg));
            frameLayout.addView(background, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
            View pin = new View(activity);
            pin.setBackground(SvgHelper.getDrawable(pinSvg));
            frameLayout.addView(pin, LayoutHelper.createFrame(60, 82.0f, 17, 0.0f, 0.0f, 0.0f, 0.0f));
            BackupImageView imageView = new BackupImageView(activity);
            imageView.setRoundRadius(AndroidUtilities.dp(26.0f));
            imageView.setForUserOrChat(selfUser, new AvatarDrawable(selfUser));
            frameLayout.addView(imageView, LayoutHelper.createFrame(52, 52.0f, 17, 0.0f, 0.0f, 0.0f, 11.0f));
            builder.setTopView(frameLayout);
            builder.setTopViewAspectRatio(0.37820512f);
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString((int) R.string.PermissionBackgroundLocation)));
            builder.setPositiveButton(LocaleController.getString((int) R.string.Continue), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda95
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    AlertsCreator.lambda$createBackgroundLocationPermissionDialog$95(activity, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda34
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    cancelRunnable.run();
                }
            });
            return builder;
        }
        return null;
    }

    public static /* synthetic */ void lambda$createBackgroundLocationPermissionDialog$95(Activity activity, DialogInterface dialog, int which) {
        if (activity.checkSelfPermission("android.permission.ACCESS_BACKGROUND_LOCATION") != 0) {
            activity.requestPermissions(new String[]{"android.permission.ACCESS_BACKGROUND_LOCATION"}, 30);
        }
    }

    public static AlertDialog.Builder createGigagroupConvertAlert(Activity activity, DialogInterface.OnClickListener onProcess, DialogInterface.OnClickListener onCancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String svg = RLottieDrawable.readRes(null, R.raw.gigagroup);
        FrameLayout frameLayout = new FrameLayout(activity);
        if (Build.VERSION.SDK_INT >= 21) {
            frameLayout.setClipToOutline(true);
            frameLayout.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.AlertsCreator.29
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f));
                }
            });
        }
        View background = new View(activity);
        background.setBackground(new BitmapDrawable(SvgHelper.getBitmap(svg, AndroidUtilities.dp(320.0f), AndroidUtilities.dp(320.0f * 0.3974359f), false)));
        frameLayout.addView(background, LayoutHelper.createFrame(-1, -1.0f, 0, -1.0f, -1.0f, -1.0f, -1.0f));
        builder.setTopView(frameLayout);
        builder.setTopViewAspectRatio(0.3974359f);
        builder.setTitle(LocaleController.getString("GigagroupAlertTitle", R.string.GigagroupAlertTitle));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("GigagroupAlertText", R.string.GigagroupAlertText)));
        builder.setPositiveButton(LocaleController.getString("GigagroupAlertLearnMore", R.string.GigagroupAlertLearnMore), onProcess);
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), onCancel);
        return builder;
    }

    public static AlertDialog.Builder createDrawOverlayPermissionDialog(final Activity activity, DialogInterface.OnClickListener onCancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String svg = RLottieDrawable.readRes(null, R.raw.pip_video_request);
        FrameLayout frameLayout = new FrameLayout(activity);
        frameLayout.setBackground(new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{-14535089, -14527894}));
        frameLayout.setClipToOutline(true);
        frameLayout.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.AlertsCreator.30
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + AndroidUtilities.dp(6.0f), AndroidUtilities.dpf2(6.0f));
            }
        });
        View background = new View(activity);
        background.setBackground(new BitmapDrawable(SvgHelper.getBitmap(svg, AndroidUtilities.dp(320.0f), AndroidUtilities.dp(320.0f * 0.50427353f), false)));
        frameLayout.addView(background, LayoutHelper.createFrame(-1, -1.0f, 0, -1.0f, -1.0f, -1.0f, -1.0f));
        builder.setTopView(frameLayout);
        builder.setTitle(LocaleController.getString("PermissionDrawAboveOtherAppsTitle", R.string.PermissionDrawAboveOtherAppsTitle));
        builder.setMessage(LocaleController.getString("PermissionDrawAboveOtherApps", R.string.PermissionDrawAboveOtherApps));
        builder.setPositiveButton(LocaleController.getString("Enable", R.string.Enable), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda106
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createDrawOverlayPermissionDialog$97(activity, dialogInterface, i);
            }
        });
        builder.notDrawBackgroundOnTopView(true);
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), onCancel);
        builder.setTopViewAspectRatio(0.50427353f);
        return builder;
    }

    public static /* synthetic */ void lambda$createDrawOverlayPermissionDialog$97(Activity activity, DialogInterface dialogInterface, int i) {
        if (activity != null && Build.VERSION.SDK_INT >= 23) {
            try {
                activity.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + activity.getPackageName())));
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static AlertDialog.Builder createDrawOverlayGroupCallPermissionDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String svg = RLottieDrawable.readRes(null, R.raw.pip_voice_request);
        final GroupCallPipButton button = new GroupCallPipButton(context, 0, true);
        button.setImportantForAccessibility(2);
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.AlertsCreator.31
            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                button.setTranslationY((getMeasuredHeight() * 0.28f) - (button.getMeasuredWidth() / 2.0f));
                button.setTranslationX((getMeasuredWidth() * 0.82f) - (button.getMeasuredWidth() / 2.0f));
            }
        };
        frameLayout.setBackground(new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{-15128003, -15118002}));
        frameLayout.setClipToOutline(true);
        frameLayout.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.AlertsCreator.32
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + AndroidUtilities.dp(6.0f), AndroidUtilities.dpf2(6.0f));
            }
        });
        View background = new View(context);
        background.setBackground(new BitmapDrawable(SvgHelper.getBitmap(svg, AndroidUtilities.dp(320.0f), AndroidUtilities.dp(320.0f * 0.5769231f), false)));
        frameLayout.addView(background, LayoutHelper.createFrame(-1, -1.0f, 0, -1.0f, -1.0f, -1.0f, -1.0f));
        frameLayout.addView(button, LayoutHelper.createFrame(117, 117.0f));
        builder.setTopView(frameLayout);
        builder.setTitle(LocaleController.getString("PermissionDrawAboveOtherAppsGroupCallTitle", R.string.PermissionDrawAboveOtherAppsGroupCallTitle));
        builder.setMessage(LocaleController.getString("PermissionDrawAboveOtherAppsGroupCall", R.string.PermissionDrawAboveOtherAppsGroupCall));
        builder.setPositiveButton(LocaleController.getString("Enable", R.string.Enable), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda11
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createDrawOverlayGroupCallPermissionDialog$98(context, dialogInterface, i);
            }
        });
        builder.notDrawBackgroundOnTopView(true);
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setTopViewAspectRatio(0.5769231f);
        return builder;
    }

    public static /* synthetic */ void lambda$createDrawOverlayGroupCallPermissionDialog$98(Context context, DialogInterface dialogInterface, int i) {
        if (context != null) {
            try {
                if (Build.VERSION.SDK_INT >= 23) {
                    Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + context.getPackageName()));
                    Activity activity = AndroidUtilities.findActivity(context);
                    if (activity instanceof LaunchActivity) {
                        activity.startActivityForResult(intent, LocationRequest.PRIORITY_NO_POWER);
                    } else {
                        context.startActivity(intent);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static AlertDialog.Builder createContactsPermissionDialog(Activity parentActivity, final MessagesStorage.IntCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        builder.setTopAnimation(R.raw.permission_request_contacts, 72, false, Theme.getColor(Theme.key_dialogTopBackground));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("ContactsPermissionAlert", R.string.ContactsPermissionAlert)));
        builder.setPositiveButton(LocaleController.getString("ContactsPermissionAlertContinue", R.string.ContactsPermissionAlertContinue), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda41
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                MessagesStorage.IntCallback.this.run(1);
            }
        });
        builder.setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", R.string.ContactsPermissionAlertNotNow), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda39
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                MessagesStorage.IntCallback.this.run(0);
            }
        });
        return builder;
    }

    public static Dialog createFreeSpaceDialog(final LaunchActivity parentActivity) {
        final int[] selected = new int[1];
        int i = 3;
        if (SharedConfig.keepMedia == 2) {
            selected[0] = 3;
        } else if (SharedConfig.keepMedia == 0) {
            selected[0] = 1;
        } else if (SharedConfig.keepMedia == 1) {
            selected[0] = 2;
        } else if (SharedConfig.keepMedia == 3) {
            selected[0] = 0;
        }
        String[] descriptions = {LocaleController.formatPluralString("Days", 3, new Object[0]), LocaleController.formatPluralString("Weeks", 1, new Object[0]), LocaleController.formatPluralString("Months", 1, new Object[0]), LocaleController.getString("LowDiskSpaceNeverRemove", R.string.LowDiskSpaceNeverRemove)};
        final LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        TextView titleTextView = new TextView(parentActivity);
        titleTextView.setText(LocaleController.getString("LowDiskSpaceTitle2", R.string.LowDiskSpaceTitle2));
        titleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        titleTextView.setTextSize(1, 16.0f);
        titleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        if (LocaleController.isRTL) {
            i = 5;
        }
        linearLayout.addView(titleTextView, LayoutHelper.createLinear(-2, -2, i | 48, 24, 0, 24, 8));
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(parentActivity);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            cell.setTextAndValue(descriptions[a], selected[0] == a);
            linearLayout.addView(cell);
            cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda85
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AlertsCreator.lambda$createFreeSpaceDialog$101(selected, linearLayout, view);
                }
            });
            a++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        builder.setTitle(LocaleController.getString("LowDiskSpaceTitle", R.string.LowDiskSpaceTitle));
        builder.setMessage(LocaleController.getString("LowDiskSpaceMessage", R.string.LowDiskSpaceMessage));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda57
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                SharedConfig.setKeepMedia(selected[0]);
            }
        });
        builder.setNeutralButton(LocaleController.getString("ClearMediaCache", R.string.ClearMediaCache), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda52
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                LaunchActivity.this.m3653lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new CacheControlActivity());
            }
        });
        return builder.create();
    }

    public static /* synthetic */ void lambda$createFreeSpaceDialog$101(int[] selected, LinearLayout linearLayout, View v) {
        int num = ((Integer) v.getTag()).intValue();
        if (num == 0) {
            selected[0] = 3;
        } else if (num == 1) {
            selected[0] = 0;
        } else if (num == 2) {
            selected[0] = 1;
        } else if (num == 3) {
            selected[0] = 2;
        }
        int count = linearLayout.getChildCount();
        for (int a1 = 0; a1 < count; a1++) {
            View child = linearLayout.getChildAt(a1);
            if (child instanceof RadioColorCell) {
                ((RadioColorCell) child).setChecked(child == v, true);
            }
        }
    }

    public static Dialog createPrioritySelectDialog(Activity parentActivity, long dialog_id, int globalType, Runnable onSelect) {
        return createPrioritySelectDialog(parentActivity, dialog_id, globalType, onSelect, null);
    }

    public static Dialog createPrioritySelectDialog(Activity parentActivity, final long dialog_id, final int globalType, final Runnable onSelect, Theme.ResourcesProvider resourcesProvider) {
        String[] descriptions;
        Activity activity = parentActivity;
        final SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        final int[] selected = new int[1];
        int i = 0;
        if (dialog_id != 0) {
            selected[0] = preferences.getInt("priority_" + dialog_id, 3);
            if (selected[0] == 3) {
                selected[0] = 0;
            } else if (selected[0] == 4) {
                selected[0] = 1;
            } else if (selected[0] == 5) {
                selected[0] = 2;
            } else if (selected[0] == 0) {
                selected[0] = 3;
            } else {
                selected[0] = 4;
            }
            descriptions = new String[]{LocaleController.getString("NotificationsPrioritySettings", R.string.NotificationsPrioritySettings), LocaleController.getString("NotificationsPriorityLow", R.string.NotificationsPriorityLow), LocaleController.getString("NotificationsPriorityMedium", R.string.NotificationsPriorityMedium), LocaleController.getString("NotificationsPriorityHigh", R.string.NotificationsPriorityHigh), LocaleController.getString("NotificationsPriorityUrgent", R.string.NotificationsPriorityUrgent)};
        } else {
            if (globalType == 1) {
                selected[0] = preferences.getInt("priority_messages", 1);
            } else if (globalType == 0) {
                selected[0] = preferences.getInt("priority_group", 1);
            } else if (globalType == 2) {
                selected[0] = preferences.getInt("priority_channel", 1);
            }
            if (selected[0] == 4) {
                selected[0] = 0;
            } else if (selected[0] == 5) {
                selected[0] = 1;
            } else if (selected[0] == 0) {
                selected[0] = 2;
            } else {
                selected[0] = 3;
            }
            descriptions = new String[]{LocaleController.getString("NotificationsPriorityLow", R.string.NotificationsPriorityLow), LocaleController.getString("NotificationsPriorityMedium", R.string.NotificationsPriorityMedium), LocaleController.getString("NotificationsPriorityHigh", R.string.NotificationsPriorityHigh), LocaleController.getString("NotificationsPriorityUrgent", R.string.NotificationsPriorityUrgent)};
        }
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, resourcesProvider);
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(activity, resourcesProvider);
            cell.setPadding(AndroidUtilities.dp(4.0f), i, AndroidUtilities.dp(4.0f), i);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground, resourcesProvider), Theme.getColor(Theme.key_dialogRadioBackgroundChecked, resourcesProvider));
            cell.setTextAndValue(descriptions[a], selected[i] == a);
            linearLayout.addView(cell);
            final AlertDialog.Builder builder2 = builder;
            cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda82
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AlertsCreator.lambda$createPrioritySelectDialog$104(selected, dialog_id, globalType, preferences, builder2, onSelect, view);
                }
            });
            a++;
            activity = parentActivity;
            linearLayout = linearLayout;
            builder = builder2;
            i = 0;
        }
        AlertDialog.Builder builder3 = builder;
        builder3.setTitle(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance));
        builder3.setView(linearLayout);
        builder3.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        return builder3.create();
    }

    public static /* synthetic */ void lambda$createPrioritySelectDialog$104(int[] selected, long dialog_id, int globalType, SharedPreferences preferences, AlertDialog.Builder builder, Runnable onSelect, View v) {
        int option;
        int option2;
        selected[0] = ((Integer) v.getTag()).intValue();
        SharedPreferences preferences1 = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        SharedPreferences.Editor editor = preferences1.edit();
        if (dialog_id != 0) {
            if (selected[0] == 0) {
                option2 = 3;
            } else if (selected[0] != 1) {
                if (selected[0] == 2) {
                    option2 = 5;
                } else if (selected[0] == 3) {
                    option2 = 0;
                } else {
                    option2 = 1;
                }
            } else {
                option2 = 4;
            }
            editor.putInt("priority_" + dialog_id, option2);
            NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannel(dialog_id);
        } else {
            if (selected[0] == 0) {
                option = 4;
            } else if (selected[0] != 1) {
                if (selected[0] == 2) {
                    option = 0;
                } else {
                    option = 1;
                }
            } else {
                option = 5;
            }
            if (globalType == 1) {
                editor.putInt("priority_messages", option);
                selected[0] = preferences.getInt("priority_messages", 1);
            } else if (globalType == 0) {
                editor.putInt("priority_group", option);
                selected[0] = preferences.getInt("priority_group", 1);
            } else if (globalType == 2) {
                editor.putInt("priority_channel", option);
                selected[0] = preferences.getInt("priority_channel", 1);
            }
            NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannelGlobal(globalType);
        }
        editor.commit();
        builder.getDismissRunnable().run();
        if (onSelect != null) {
            onSelect.run();
        }
    }

    public static Dialog createPopupSelectDialog(Activity parentActivity, final int globalType, final Runnable onSelect) {
        SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        final int[] selected = new int[1];
        if (globalType == 1) {
            selected[0] = preferences.getInt("popupAll", 0);
        } else if (globalType == 0) {
            selected[0] = preferences.getInt("popupGroup", 0);
        } else {
            selected[0] = preferences.getInt("popupChannel", 0);
        }
        String[] descriptions = {LocaleController.getString("NoPopup", R.string.NoPopup), LocaleController.getString("OnlyWhenScreenOn", R.string.OnlyWhenScreenOn), LocaleController.getString("OnlyWhenScreenOff", R.string.OnlyWhenScreenOff), LocaleController.getString("AlwaysShowPopup", R.string.AlwaysShowPopup)};
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        final AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(parentActivity);
            cell.setTag(Integer.valueOf(a));
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            cell.setTextAndValue(descriptions[a], selected[0] == a);
            linearLayout.addView(cell);
            cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda81
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AlertsCreator.lambda$createPopupSelectDialog$105(selected, globalType, builder, onSelect, view);
                }
            });
            a++;
        }
        builder.setTitle(LocaleController.getString("PopupNotification", R.string.PopupNotification));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        return builder.create();
    }

    public static /* synthetic */ void lambda$createPopupSelectDialog$105(int[] selected, int globalType, AlertDialog.Builder builder, Runnable onSelect, View v) {
        selected[0] = ((Integer) v.getTag()).intValue();
        SharedPreferences preferences1 = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        SharedPreferences.Editor editor = preferences1.edit();
        if (globalType == 1) {
            editor.putInt("popupAll", selected[0]);
        } else if (globalType == 0) {
            editor.putInt("popupGroup", selected[0]);
        } else {
            editor.putInt("popupChannel", selected[0]);
        }
        editor.commit();
        builder.getDismissRunnable().run();
        if (onSelect != null) {
            onSelect.run();
        }
    }

    public static Dialog createSingleChoiceDialog(Activity parentActivity, String[] options, String title, int selected, final DialogInterface.OnClickListener listener) {
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        final AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        for (int a = 0; a < options.length; a++) {
            RadioColorCell cell = new RadioColorCell(parentActivity);
            boolean z = false;
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            String str = options[a];
            if (selected == a) {
                z = true;
            }
            cell.setTextAndValue(str, z);
            linearLayout.addView(cell);
            cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda76
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AlertsCreator.lambda$createSingleChoiceDialog$106(AlertDialog.Builder.this, listener, view);
                }
            });
        }
        builder.setTitle(title);
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        return builder.create();
    }

    public static /* synthetic */ void lambda$createSingleChoiceDialog$106(AlertDialog.Builder builder, DialogInterface.OnClickListener listener, View v) {
        int sel = ((Integer) v.getTag()).intValue();
        builder.getDismissRunnable().run();
        listener.onClick(null, sel);
    }

    public static AlertDialog.Builder createTTLAlert(Context context, final TLRPC.EncryptedChat encryptedChat, Theme.ResourcesProvider resourcesProvider) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, resourcesProvider);
        builder.setTitle(LocaleController.getString("MessageLifetime", R.string.MessageLifetime));
        final NumberPicker numberPicker = new NumberPicker(context);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(20);
        if (encryptedChat.ttl > 0 && encryptedChat.ttl < 16) {
            numberPicker.setValue(encryptedChat.ttl);
        } else if (encryptedChat.ttl == 30) {
            numberPicker.setValue(16);
        } else if (encryptedChat.ttl == 60) {
            numberPicker.setValue(17);
        } else if (encryptedChat.ttl == 3600) {
            numberPicker.setValue(18);
        } else if (encryptedChat.ttl == 86400) {
            numberPicker.setValue(19);
        } else if (encryptedChat.ttl == 604800) {
            numberPicker.setValue(20);
        } else if (encryptedChat.ttl == 0) {
            numberPicker.setValue(0);
        }
        numberPicker.setFormatter(AlertsCreator$$ExternalSyntheticLambda19.INSTANCE);
        builder.setView(numberPicker);
        builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda42
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createTTLAlert$108(TLRPC.EncryptedChat.this, numberPicker, dialogInterface, i);
            }
        });
        return builder;
    }

    public static /* synthetic */ String lambda$createTTLAlert$107(int value) {
        if (value == 0) {
            return LocaleController.getString("ShortMessageLifetimeForever", R.string.ShortMessageLifetimeForever);
        }
        if (value >= 1 && value < 16) {
            return LocaleController.formatTTLString(value);
        }
        if (value == 16) {
            return LocaleController.formatTTLString(30);
        }
        if (value == 17) {
            return LocaleController.formatTTLString(60);
        }
        if (value == 18) {
            return LocaleController.formatTTLString(3600);
        }
        if (value == 19) {
            return LocaleController.formatTTLString(86400);
        }
        if (value == 20) {
            return LocaleController.formatTTLString(604800);
        }
        return "";
    }

    public static /* synthetic */ void lambda$createTTLAlert$108(TLRPC.EncryptedChat encryptedChat, NumberPicker numberPicker, DialogInterface dialog, int which) {
        int oldValue = encryptedChat.ttl;
        int which2 = numberPicker.getValue();
        if (which2 >= 0 && which2 < 16) {
            encryptedChat.ttl = which2;
        } else if (which2 == 16) {
            encryptedChat.ttl = 30;
        } else if (which2 == 17) {
            encryptedChat.ttl = 60;
        } else if (which2 == 18) {
            encryptedChat.ttl = 3600;
        } else if (which2 == 19) {
            encryptedChat.ttl = 86400;
        } else if (which2 == 20) {
            encryptedChat.ttl = 604800;
        }
        if (oldValue != encryptedChat.ttl) {
            SecretChatHelper.getInstance(UserConfig.selectedAccount).sendTTLMessage(encryptedChat, null);
            MessagesStorage.getInstance(UserConfig.selectedAccount).updateEncryptedChatTTL(encryptedChat);
        }
    }

    public static AlertDialog createAccountSelectDialog(Activity parentActivity, final AccountSelectDelegate delegate) {
        if (UserConfig.getActivatedAccountsCount() < 2) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        final Runnable dismissRunnable = builder.getDismissRunnable();
        final AlertDialog[] alertDialog = new AlertDialog[1];
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        for (int a = 0; a < 4; a++) {
            TLRPC.User u = UserConfig.getInstance(a).getCurrentUser();
            if (u != null) {
                AccountSelectCell cell = new AccountSelectCell(parentActivity, false);
                cell.setAccount(a, false);
                cell.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
                cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                linearLayout.addView(cell, LayoutHelper.createLinear(-1, 50));
                cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda89
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        AlertsCreator.lambda$createAccountSelectDialog$109(alertDialog, dismissRunnable, delegate, view);
                    }
                });
            }
        }
        builder.setTitle(LocaleController.getString("SelectAccount", R.string.SelectAccount));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        AlertDialog create = builder.create();
        alertDialog[0] = create;
        return create;
    }

    public static /* synthetic */ void lambda$createAccountSelectDialog$109(AlertDialog[] alertDialog, Runnable dismissRunnable, AccountSelectDelegate delegate, View v) {
        if (alertDialog[0] != null) {
            alertDialog[0].setOnDismissListener(null);
        }
        dismissRunnable.run();
        AccountSelectCell cell1 = (AccountSelectCell) v;
        delegate.didSelectAccount(cell1.getAccountNumber());
    }

    /* JADX WARN: Code restructure failed: missing block: B:336:0x076a, code lost:
        r65.setMessage(org.telegram.messenger.LocaleController.getString("AreYouSureDeleteSingleMessage", org.telegram.messenger.beta.R.string.AreYouSureDeleteSingleMessage));
     */
    /* JADX WARN: Removed duplicated region for block: B:215:0x04da  */
    /* JADX WARN: Removed duplicated region for block: B:258:0x056d  */
    /* JADX WARN: Removed duplicated region for block: B:325:0x0713  */
    /* JADX WARN: Removed duplicated region for block: B:326:0x0721  */
    /* JADX WARN: Removed duplicated region for block: B:329:0x0746 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:340:0x0780 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:346:0x07c1  */
    /* JADX WARN: Removed duplicated region for block: B:353:0x07e5  */
    /* JADX WARN: Removed duplicated region for block: B:354:0x07ed  */
    /* JADX WARN: Removed duplicated region for block: B:357:0x081d  */
    /* JADX WARN: Removed duplicated region for block: B:391:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:72:0x0142  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void createDeleteMessagesAlert(final org.telegram.ui.ActionBar.BaseFragment r67, final org.telegram.tgnet.TLRPC.User r68, final org.telegram.tgnet.TLRPC.Chat r69, final org.telegram.tgnet.TLRPC.EncryptedChat r70, final org.telegram.tgnet.TLRPC.ChatFull r71, final long r72, final org.telegram.messenger.MessageObject r74, final android.util.SparseArray<org.telegram.messenger.MessageObject>[] r75, final org.telegram.messenger.MessageObject.GroupedMessages r76, final boolean r77, int r78, final java.lang.Runnable r79, final java.lang.Runnable r80, final org.telegram.ui.ActionBar.Theme.ResourcesProvider r81) {
        /*
            Method dump skipped, instructions count: 2091
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.createDeleteMessagesAlert(org.telegram.ui.ActionBar.BaseFragment, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$EncryptedChat, org.telegram.tgnet.TLRPC$ChatFull, long, org.telegram.messenger.MessageObject, android.util.SparseArray[], org.telegram.messenger.MessageObject$GroupedMessages, boolean, int, java.lang.Runnable, java.lang.Runnable, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    public static /* synthetic */ void lambda$createDeleteMessagesAlert$110(AlertDialog[] progressDialog, TLObject response, TLRPC.TL_error error, BaseFragment fragment, TLRPC.User user, TLRPC.Chat chat, TLRPC.EncryptedChat encryptedChat, TLRPC.ChatFull chatInfo, long mergeDialogId, MessageObject selectedMessage, SparseArray[] selectedMessages, MessageObject.GroupedMessages selectedGroup, boolean scheduled, Runnable onDelete, Runnable hideDim, Theme.ResourcesProvider resourcesProvider) {
        try {
            progressDialog[0].dismiss();
        } catch (Throwable th) {
        }
        progressDialog[0] = null;
        int loadType = 2;
        if (response != null) {
            TLRPC.TL_channels_channelParticipant participant = (TLRPC.TL_channels_channelParticipant) response;
            if (!(participant.participant instanceof TLRPC.TL_channelParticipantAdmin) && !(participant.participant instanceof TLRPC.TL_channelParticipantCreator)) {
                loadType = 0;
            }
        } else if (error != null && "USER_NOT_PARTICIPANT".equals(error.text)) {
            loadType = 0;
        }
        createDeleteMessagesAlert(fragment, user, chat, encryptedChat, chatInfo, mergeDialogId, selectedMessage, selectedMessages, selectedGroup, scheduled, loadType, onDelete, hideDim, resourcesProvider);
    }

    public static /* synthetic */ void lambda$createDeleteMessagesAlert$113(AlertDialog[] progressDialog, final int currentAccount, final int requestId, BaseFragment fragment) {
        if (progressDialog[0] == null) {
            return;
        }
        progressDialog[0].setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                ConnectionsManager.getInstance(currentAccount).cancelRequest(requestId, true);
            }
        });
        fragment.showDialog(progressDialog[0]);
    }

    public static /* synthetic */ void lambda$createDeleteMessagesAlert$114(boolean[] checks, View v) {
        if (!v.isEnabled()) {
            return;
        }
        CheckBoxCell cell13 = (CheckBoxCell) v;
        Integer num1 = (Integer) cell13.getTag();
        checks[num1.intValue()] = !checks[num1.intValue()];
        cell13.setChecked(checks[num1.intValue()], true);
    }

    public static /* synthetic */ void lambda$createDeleteMessagesAlert$115(boolean[] deleteForAll, View v) {
        CheckBoxCell cell12 = (CheckBoxCell) v;
        deleteForAll[0] = !deleteForAll[0];
        cell12.setChecked(deleteForAll[0], true);
    }

    public static /* synthetic */ void lambda$createDeleteMessagesAlert$116(boolean[] deleteForAll, View v) {
        CheckBoxCell cell1 = (CheckBoxCell) v;
        deleteForAll[0] = !deleteForAll[0];
        cell1.setChecked(deleteForAll[0], true);
    }

    /* JADX WARN: Removed duplicated region for block: B:42:0x00f7  */
    /* JADX WARN: Removed duplicated region for block: B:78:0x012d A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static /* synthetic */ void lambda$createDeleteMessagesAlert$118(org.telegram.messenger.MessageObject r22, org.telegram.messenger.MessageObject.GroupedMessages r23, org.telegram.tgnet.TLRPC.EncryptedChat r24, int r25, long r26, boolean[] r28, boolean r29, android.util.SparseArray[] r30, org.telegram.tgnet.TLRPC.User r31, org.telegram.tgnet.TLRPC.Chat r32, boolean[] r33, org.telegram.tgnet.TLRPC.Chat r34, org.telegram.tgnet.TLRPC.ChatFull r35, java.lang.Runnable r36, android.content.DialogInterface r37, int r38) {
        /*
            Method dump skipped, instructions count: 425
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.lambda$createDeleteMessagesAlert$118(org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessages, org.telegram.tgnet.TLRPC$EncryptedChat, int, long, boolean[], boolean, android.util.SparseArray[], org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, boolean[], org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$ChatFull, java.lang.Runnable, android.content.DialogInterface, int):void");
    }

    public static /* synthetic */ void lambda$createDeleteMessagesAlert$117(TLObject response, TLRPC.TL_error error) {
    }

    public static /* synthetic */ void lambda$createDeleteMessagesAlert$119(Runnable hideDim, DialogInterface di) {
        if (hideDim != null) {
            hideDim.run();
        }
    }

    public static void createThemeCreateDialog(final BaseFragment fragment, int type, final Theme.ThemeInfo switchToTheme, final Theme.ThemeAccent switchToAccent) {
        if (fragment == null || fragment.getParentActivity() == null) {
            return;
        }
        Context context = fragment.getParentActivity();
        final EditTextBoldCursor editText = new EditTextBoldCursor(context);
        editText.setBackground(null);
        editText.setLineColors(Theme.getColor(Theme.key_dialogInputField), Theme.getColor(Theme.key_dialogInputFieldActivated), Theme.getColor(Theme.key_dialogTextRed2));
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("NewTheme", R.string.NewTheme));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString("Create", R.string.Create), AlertsCreator$$ExternalSyntheticLambda60.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        builder.setView(linearLayout);
        TextView message = new TextView(context);
        if (type != 0) {
            message.setText(AndroidUtilities.replaceTags(LocaleController.getString("EnterThemeNameEdit", R.string.EnterThemeNameEdit)));
        } else {
            message.setText(LocaleController.getString("EnterThemeName", R.string.EnterThemeName));
        }
        message.setTextSize(1, 16.0f);
        message.setPadding(AndroidUtilities.dp(23.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(23.0f), AndroidUtilities.dp(6.0f));
        message.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        linearLayout.addView(message, LayoutHelper.createLinear(-1, -2));
        editText.setTextSize(1, 16.0f);
        editText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        editText.setMaxLines(1);
        editText.setLines(1);
        editText.setInputType(16385);
        editText.setGravity(51);
        editText.setSingleLine(true);
        editText.setImeOptions(6);
        editText.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        editText.setCursorSize(AndroidUtilities.dp(20.0f));
        editText.setCursorWidth(1.5f);
        editText.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
        linearLayout.addView(editText, LayoutHelper.createLinear(-1, 36, 51, 24, 6, 24, 0));
        editText.setOnEditorActionListener(AlertsCreator$$ExternalSyntheticLambda111.INSTANCE);
        editText.setText(generateThemeName(switchToAccent));
        editText.setSelection(editText.length());
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda69
            @Override // android.content.DialogInterface.OnShowListener
            public final void onShow(DialogInterface dialogInterface) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda114
                    @Override // java.lang.Runnable
                    public final void run() {
                        AlertsCreator.lambda$createThemeCreateDialog$122(EditTextBoldCursor.this);
                    }
                });
            }
        });
        fragment.showDialog(alertDialog);
        editText.requestFocus();
        alertDialog.getButton(-1).setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda77
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AlertsCreator.lambda$createThemeCreateDialog$126(BaseFragment.this, editText, switchToAccent, switchToTheme, alertDialog, view);
            }
        });
    }

    public static /* synthetic */ void lambda$createThemeCreateDialog$120(DialogInterface dialog, int which) {
    }

    public static /* synthetic */ void lambda$createThemeCreateDialog$122(EditTextBoldCursor editText) {
        editText.requestFocus();
        AndroidUtilities.showKeyboard(editText);
    }

    public static /* synthetic */ void lambda$createThemeCreateDialog$126(final BaseFragment fragment, final EditTextBoldCursor editText, Theme.ThemeAccent switchToAccent, Theme.ThemeInfo switchToTheme, final AlertDialog alertDialog, View v) {
        if (fragment.getParentActivity() == null) {
            return;
        }
        if (editText.length() == 0) {
            Vibrator vibrator = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200L);
            }
            AndroidUtilities.shakeView(editText, 2.0f, 0);
            return;
        }
        if (fragment instanceof ThemePreviewActivity) {
            Theme.applyPreviousTheme();
            fragment.finishFragment();
        }
        if (switchToAccent != null) {
            switchToTheme.setCurrentAccentId(switchToAccent.id);
            Theme.refreshThemeColors();
            Utilities.searchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda116
                @Override // java.lang.Runnable
                public final void run() {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda115
                        @Override // java.lang.Runnable
                        public final void run() {
                            AlertsCreator.processCreate(EditTextBoldCursor.this, r2, r3);
                        }
                    });
                }
            });
            return;
        }
        processCreate(editText, alertDialog, fragment);
    }

    public static void processCreate(EditTextBoldCursor editText, AlertDialog alertDialog, BaseFragment fragment) {
        if (fragment == null || fragment.getParentActivity() == null) {
            return;
        }
        AndroidUtilities.hideKeyboard(editText);
        Theme.ThemeInfo themeInfo = Theme.createNewTheme(editText.getText().toString());
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.themeListUpdated, new Object[0]);
        ThemeEditorView themeEditorView = new ThemeEditorView();
        themeEditorView.show(fragment.getParentActivity(), themeInfo);
        alertDialog.dismiss();
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        if (!preferences.getBoolean("themehint", false)) {
            preferences.edit().putBoolean("themehint", true).commit();
            try {
                Toast.makeText(fragment.getParentActivity(), LocaleController.getString("CreateNewThemeHelp", R.string.CreateNewThemeHelp), 1).show();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    private static String generateThemeName(Theme.ThemeAccent accent) {
        Theme.ThemeAccent accent2;
        int color;
        List<String> adjectives = Arrays.asList("Ancient", "Antique", "Autumn", "Baby", "Barely", "Baroque", "Blazing", "Blushing", "Bohemian", "Bubbly", "Burning", "Buttered", "Classic", "Clear", "Cool", "Cosmic", "Cotton", "Cozy", "Crystal", "Dark", "Daring", "Darling", "Dawn", "Dazzling", "Deep", "Deepest", "Delicate", "Delightful", "Divine", "Double", "Downtown", "Dreamy", "Dusky", "Dusty", "Electric", "Enchanted", "Endless", "Evening", "Fantastic", "Flirty", "Forever", "Frigid", "Frosty", "Frozen", "Gentle", "Heavenly", "Hyper", "Icy", "Infinite", "Innocent", "Instant", "Luscious", "Lunar", "Lustrous", "Magic", "Majestic", "Mambo", "Midnight", "Millenium", "Morning", "Mystic", "Natural", "Neon", "Night", "Opaque", "Paradise", "Perfect", "Perky", "Polished", "Powerful", "Rich", "Royal", "Sheer", "Simply", "Sizzling", "Solar", "Sparkling", "Splendid", "Spicy", "Spring", "Stellar", "Sugared", "Summer", "Sunny", "Super", "Sweet", "Tender", "Tenacious", "Tidal", "Toasted", "Totally", "Tranquil", "Tropical", "True", "Twilight", "Twinkling", "Ultimate", "Ultra", "Velvety", "Vibrant", "Vintage", "Virtual", "Warm", "Warmest", "Whipped", "Wild", "Winsome");
        List<String> subjectives = Arrays.asList("Ambrosia", "Attack", "Avalanche", "Blast", "Bliss", "Blossom", "Blush", "Burst", "Butter", "Candy", "Carnival", "Charm", "Chiffon", "Cloud", "Comet", "Delight", "Dream", "Dust", "Fantasy", "Flame", ExifInterface.TAG_FLASH, "Fire", "Freeze", "Frost", "Glade", "Glaze", "Gleam", "Glimmer", "Glitter", "Glow", "Grande", "Haze", "Highlight", "Ice", "Illusion", "Intrigue", "Jewel", "Jubilee", "Kiss", "Lights", "Lollypop", "Love", "Luster", "Madness", "Matte", "Mirage", "Mist", "Moon", "Muse", "Myth", "Nectar", "Nova", "Parfait", "Passion", "Pop", "Rain", "Reflection", "Rhapsody", "Romance", "Satin", "Sensation", "Silk", "Shine", "Shadow", "Shimmer", "Sky", "Spice", "Star", "Sugar", "Sunrise", "Sunset", "Sun", "Twist", "Unbound", "Velvet", "Vibrant", "Waters", "Wine", "Wink", "Wonder", "Zone");
        HashMap<Integer, String> colors = new HashMap<>();
        colors.put(9306112, "Berry");
        colors.put(14598550, "Brandy");
        colors.put(8391495, "Cherry");
        colors.put(16744272, "Coral");
        colors.put(14372985, "Cranberry");
        colors.put(14423100, "Crimson");
        colors.put(14725375, "Mauve");
        colors.put(16761035, "Pink");
        colors.put(16711680, "Red");
        colors.put(16711807, "Rose");
        colors.put(8406555, "Russet");
        colors.put(16720896, "Scarlet");
        colors.put(15856113, "Seashell");
        colors.put(16724889, "Strawberry");
        colors.put(16760576, "Amber");
        colors.put(15438707, "Apricot");
        colors.put(16508850, "Banana");
        colors.put(10601738, "Citrus");
        colors.put(11560192, "Ginger");
        colors.put(16766720, "Gold");
        colors.put(16640272, "Lemon");
        colors.put(16753920, "Orange");
        colors.put(16770484, "Peach");
        colors.put(16739155, "Persimmon");
        colors.put(14996514, "Sunflower");
        colors.put(15893760, "Tangerine");
        colors.put(16763004, "Topaz");
        colors.put(16776960, "Yellow");
        colors.put(3688720, "Clover");
        colors.put(8628829, "Cucumber");
        colors.put(5294200, "Emerald");
        colors.put(11907932, "Olive");
        colors.put(Integer.valueOf((int) MotionEventCompat.ACTION_POINTER_INDEX_MASK), "Green");
        colors.put(43115, "Jade");
        colors.put(2730887, "Jungle");
        colors.put(12582656, "Lime");
        colors.put(776785, "Malachite");
        colors.put(10026904, "Mint");
        colors.put(11394989, "Moss");
        colors.put(3234721, "Azure");
        colors.put(255, "Blue");
        colors.put(18347, "Cobalt");
        colors.put(5204422, "Indigo");
        colors.put(96647, "Lagoon");
        colors.put(7461346, "Aquamarine");
        colors.put(1182351, "Ultramarine");
        colors.put(128, "Navy");
        colors.put(3101086, "Sapphire");
        colors.put(7788522, "Sky");
        colors.put(32896, "Teal");
        colors.put(4251856, "Turquoise");
        colors.put(10053324, "Amethyst");
        colors.put(5046581, "Blackberry");
        colors.put(6373457, "Eggplant");
        colors.put(13148872, "Lilac");
        colors.put(11894492, "Lavender");
        colors.put(13421823, "Periwinkle");
        colors.put(8663417, "Plum");
        colors.put(6684825, "Purple");
        colors.put(14204888, "Thistle");
        colors.put(14315734, "Orchid");
        colors.put(2361920, "Violet");
        colors.put(4137225, "Bronze");
        colors.put(3604994, "Chocolate");
        colors.put(8077056, "Cinnamon");
        colors.put(3153694, "Cocoa");
        colors.put(7365973, "Coffee");
        colors.put(7956873, "Rum");
        colors.put(5113350, "Mahogany");
        colors.put(7875865, "Mocha");
        colors.put(12759680, "Sand");
        colors.put(8924439, "Sienna");
        colors.put(7864585, "Maple");
        colors.put(15787660, "Khaki");
        colors.put(12088115, "Copper");
        colors.put(12144200, "Chestnut");
        colors.put(15653316, "Almond");
        colors.put(16776656, "Cream");
        colors.put(12186367, "Diamond");
        colors.put(11109127, "Honey");
        colors.put(16777200, "Ivory");
        colors.put(15392968, "Pearl");
        colors.put(15725299, "Porcelain");
        colors.put(13745832, "Vanilla");
        colors.put(Integer.valueOf((int) ViewCompat.MEASURED_SIZE_MASK), "White");
        colors.put(8421504, "Gray");
        colors.put(0, "Black");
        colors.put(15266260, "Chrome");
        colors.put(3556687, "Charcoal");
        colors.put(789277, "Ebony");
        colors.put(12632256, "Silver");
        colors.put(16119285, "Smoke");
        colors.put(2499381, "Steel");
        colors.put(5220413, "Apple");
        colors.put(8434628, "Glacier");
        colors.put(16693933, "Melon");
        colors.put(12929932, "Mulberry");
        colors.put(11126466, "Opal");
        colors.put(5547512, "Blue");
        if (accent != null) {
            accent2 = accent;
        } else {
            Theme.ThemeInfo themeInfo = Theme.getCurrentTheme();
            accent2 = themeInfo.getAccent(false);
        }
        if (accent2 != null && accent2.accentColor != 0) {
            color = accent2.accentColor;
        } else {
            color = AndroidUtilities.calcDrawableColor(Theme.getCachedWallpaper())[0];
        }
        String minKey = null;
        int minValue = Integer.MAX_VALUE;
        int r1 = Color.red(color);
        int g1 = Color.green(color);
        int b1 = Color.blue(color);
        for (Map.Entry<Integer, String> entry : colors.entrySet()) {
            Integer value = entry.getKey();
            int r2 = Color.red(value.intValue());
            int g2 = Color.green(value.intValue());
            int b2 = Color.blue(value.intValue());
            int rMean = (r1 + r2) / 2;
            int r = r1 - r2;
            int g = g1 - g2;
            int b = b1 - b2;
            int color2 = color;
            int d = ((((rMean + 512) * r) * r) >> 8) + (g * 4 * g) + ((((767 - rMean) * b) * b) >> 8);
            if (d < minValue) {
                String minKey2 = entry.getValue();
                minValue = d;
                minKey = minKey2;
            }
            color = color2;
        }
        if (Utilities.random.nextInt() % 2 == 0) {
            String result = adjectives.get(Utilities.random.nextInt(adjectives.size())) + " " + minKey;
            return result;
        }
        String result2 = minKey + " " + subjectives.get(Utilities.random.nextInt(subjectives.size()));
        return result2;
    }

    public static ActionBarPopupWindow showPopupMenu(ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout, View anchorView, int offsetX, int offsetY) {
        final android.graphics.Rect rect = new android.graphics.Rect();
        final ActionBarPopupWindow popupWindow = new ActionBarPopupWindow(popupLayout, -2, -2);
        if (Build.VERSION.SDK_INT >= 19) {
            popupWindow.setAnimationStyle(0);
        } else {
            popupWindow.setAnimationStyle(R.style.PopupAnimation);
        }
        popupWindow.setAnimationEnabled(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setClippingEnabled(true);
        popupWindow.setInputMethodMode(2);
        popupWindow.setSoftInputMode(0);
        popupWindow.setFocusable(true);
        popupLayout.setFocusableInTouchMode(true);
        popupLayout.setOnKeyListener(new View.OnKeyListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda100
            @Override // android.view.View.OnKeyListener
            public final boolean onKey(View view, int i, KeyEvent keyEvent) {
                return AlertsCreator.lambda$showPopupMenu$127(ActionBarPopupWindow.this, view, i, keyEvent);
            }
        });
        popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x - AndroidUtilities.dp(40.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
        popupWindow.showAsDropDown(anchorView, offsetX, offsetY);
        popupLayout.updateRadialSelectors();
        popupWindow.startAnimation();
        popupLayout.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda101
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return AlertsCreator.lambda$showPopupMenu$128(ActionBarPopupWindow.this, rect, view, motionEvent);
            }
        });
        return popupWindow;
    }

    public static /* synthetic */ boolean lambda$showPopupMenu$127(ActionBarPopupWindow popupWindow, View v, int keyCode, KeyEvent event) {
        if (keyCode == 82 && event.getRepeatCount() == 0 && event.getAction() == 1 && popupWindow.isShowing()) {
            popupWindow.dismiss();
            return true;
        }
        return false;
    }

    public static /* synthetic */ boolean lambda$showPopupMenu$128(ActionBarPopupWindow popupWindow, android.graphics.Rect rect, View v, MotionEvent event) {
        if (event.getActionMasked() == 0 && popupWindow != null && popupWindow.isShowing()) {
            v.getHitRect(rect);
            if (!rect.contains((int) event.getX(), (int) event.getY())) {
                popupWindow.dismiss();
                return false;
            }
            return false;
        }
        return false;
    }
}
