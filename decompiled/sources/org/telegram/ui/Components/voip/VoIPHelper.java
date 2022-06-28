package org.telegram.ui.Components.voip;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.microsoft.appcenter.crashes.ingestion.models.ErrorAttachmentLog;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Components.BetterRatingView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.JoinCallAlert;
import org.telegram.ui.Components.JoinCallByUrlAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.LaunchActivity;
/* loaded from: classes5.dex */
public class VoIPHelper {
    private static final int VOIP_SUPPORT_ID = 4244000;
    public static long lastCallTime = 0;

    public static void startCall(TLRPC.User user, boolean videoCall, boolean canVideoCall, final Activity activity, TLRPC.UserFull userFull, AccountInstance accountInstance) {
        String str;
        int i;
        String str2;
        int i2;
        boolean isAirplaneMode = true;
        if (userFull != null && userFull.phone_calls_private) {
            new AlertDialog.Builder(activity).setTitle(LocaleController.getString("VoipFailed", R.string.VoipFailed)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("CallNotAvailable", R.string.CallNotAvailable, ContactsController.formatName(user.first_name, user.last_name)))).setPositiveButton(LocaleController.getString("OK", R.string.OK), null).show();
        } else if (ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState() != 3) {
            if (Settings.System.getInt(activity.getContentResolver(), "airplane_mode_on", 0) == 0) {
                isAirplaneMode = false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            if (isAirplaneMode) {
                i = R.string.VoipOfflineAirplaneTitle;
                str = "VoipOfflineAirplaneTitle";
            } else {
                i = R.string.VoipOfflineTitle;
                str = "VoipOfflineTitle";
            }
            AlertDialog.Builder title = builder.setTitle(LocaleController.getString(str, i));
            if (isAirplaneMode) {
                i2 = R.string.VoipOfflineAirplane;
                str2 = "VoipOfflineAirplane";
            } else {
                i2 = R.string.VoipOffline;
                str2 = "VoipOffline";
            }
            AlertDialog.Builder bldr = title.setMessage(LocaleController.getString(str2, i2)).setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            if (isAirplaneMode) {
                final Intent settingsIntent = new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
                if (settingsIntent.resolveActivity(activity.getPackageManager()) != null) {
                    bldr.setNeutralButton(LocaleController.getString("VoipOfflineOpenSettings", R.string.VoipOfflineOpenSettings), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.voip.VoIPHelper$$ExternalSyntheticLambda11
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i3) {
                            activity.startActivity(settingsIntent);
                        }
                    });
                }
            }
            try {
                bldr.show();
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else if (Build.VERSION.SDK_INT >= 23) {
            ArrayList<String> permissions = new ArrayList<>();
            if (activity.checkSelfPermission("android.permission.RECORD_AUDIO") != 0) {
                permissions.add("android.permission.RECORD_AUDIO");
            }
            if (videoCall && activity.checkSelfPermission("android.permission.CAMERA") != 0) {
                permissions.add("android.permission.CAMERA");
            }
            if (permissions.isEmpty()) {
                initiateCall(user, null, null, videoCall, canVideoCall, false, null, activity, null, accountInstance);
            } else {
                activity.requestPermissions((String[]) permissions.toArray(new String[0]), videoCall ? 102 : 101);
            }
        } else {
            initiateCall(user, null, null, videoCall, canVideoCall, false, null, activity, null, accountInstance);
        }
    }

    public static void startCall(TLRPC.Chat chat, TLRPC.InputPeer peer, String hash, boolean createCall, Activity activity, BaseFragment fragment, AccountInstance accountInstance) {
        startCall(chat, peer, hash, createCall, null, activity, fragment, accountInstance);
    }

    public static void startCall(TLRPC.Chat chat, TLRPC.InputPeer peer, String hash, boolean createCall, Boolean checkJoiner, final Activity activity, BaseFragment fragment, AccountInstance accountInstance) {
        String str;
        int i;
        String str2;
        int i2;
        if (activity == null) {
            return;
        }
        boolean z = false;
        if (ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState() != 3) {
            if (Settings.System.getInt(activity.getContentResolver(), "airplane_mode_on", 0) != 0) {
                z = true;
            }
            boolean isAirplaneMode = z;
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            if (isAirplaneMode) {
                i = R.string.VoipOfflineAirplaneTitle;
                str = "VoipOfflineAirplaneTitle";
            } else {
                i = R.string.VoipOfflineTitle;
                str = "VoipOfflineTitle";
            }
            AlertDialog.Builder title = builder.setTitle(LocaleController.getString(str, i));
            if (isAirplaneMode) {
                i2 = R.string.VoipGroupOfflineAirplane;
                str2 = "VoipGroupOfflineAirplane";
            } else {
                i2 = R.string.VoipGroupOffline;
                str2 = "VoipGroupOffline";
            }
            AlertDialog.Builder bldr = title.setMessage(LocaleController.getString(str2, i2)).setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            if (isAirplaneMode) {
                final Intent settingsIntent = new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
                if (settingsIntent.resolveActivity(activity.getPackageManager()) != null) {
                    bldr.setNeutralButton(LocaleController.getString("VoipOfflineOpenSettings", R.string.VoipOfflineOpenSettings), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.voip.VoIPHelper$$ExternalSyntheticLambda13
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i3) {
                            activity.startActivity(settingsIntent);
                        }
                    });
                }
            }
            try {
                bldr.show();
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else if (Build.VERSION.SDK_INT >= 23) {
            ArrayList<String> permissions = new ArrayList<>();
            ChatObject.Call call = accountInstance.getMessagesController().getGroupCall(chat.id, false);
            if (activity.checkSelfPermission("android.permission.RECORD_AUDIO") != 0 && (call == null || !call.call.rtmp_stream)) {
                permissions.add("android.permission.RECORD_AUDIO");
            }
            if (!permissions.isEmpty()) {
                activity.requestPermissions((String[]) permissions.toArray(new String[0]), 103);
            } else {
                initiateCall(null, chat, hash, false, false, createCall, checkJoiner, activity, fragment, accountInstance);
            }
        } else {
            initiateCall(null, chat, hash, false, false, createCall, checkJoiner, activity, fragment, accountInstance);
        }
    }

    private static void initiateCall(final TLRPC.User user, final TLRPC.Chat chat, final String hash, final boolean videoCall, final boolean canVideoCall, final boolean createCall, Boolean checkJoiner, final Activity activity, final BaseFragment fragment, final AccountInstance accountInstance) {
        VoIPService voIPService;
        String oldName;
        String key1;
        int key2;
        String newName;
        String str;
        int i;
        int key22;
        String key12;
        if (activity != null) {
            if (user == null && chat == null) {
                return;
            }
            VoIPService voIPService2 = VoIPService.getSharedInstance();
            if (voIPService2 == null) {
                if (VoIPService.callIShouldHavePutIntoIntent == null) {
                    doInitiateCall(user, chat, hash, null, false, videoCall, canVideoCall, createCall, activity, fragment, accountInstance, checkJoiner != null ? checkJoiner.booleanValue() : true, true);
                    return;
                }
                return;
            }
            long newId = user != null ? user.id : -chat.id;
            long callerId = VoIPService.getSharedInstance().getCallerId();
            if (callerId == newId && voIPService2.getAccount() == accountInstance.getCurrentAccount()) {
                if (user == null && (activity instanceof LaunchActivity)) {
                    if (!TextUtils.isEmpty(hash)) {
                        voIPService2.setGroupCallHash(hash);
                    }
                    GroupCallActivity.create((LaunchActivity) activity, AccountInstance.getInstance(UserConfig.selectedAccount), null, null, false, null);
                    voIPService = voIPService2;
                }
                activity.startActivity(new Intent(activity, LaunchActivity.class).setAction(user != null ? "voip" : "voip_chat"));
                voIPService = voIPService2;
            }
            if (callerId > 0) {
                TLRPC.User callUser = voIPService2.getUser();
                String oldName2 = ContactsController.formatName(callUser.first_name, callUser.last_name);
                if (newId > 0) {
                    key12 = "VoipOngoingAlert";
                    key22 = R.string.VoipOngoingAlert;
                } else {
                    key12 = "VoipOngoingAlert2";
                    key22 = R.string.VoipOngoingAlert2;
                }
                oldName = oldName2;
                key1 = key12;
                key2 = key22;
            } else {
                TLRPC.Chat callChat = voIPService2.getChat();
                String oldName3 = callChat.title;
                if (newId > 0) {
                    oldName = oldName3;
                    key1 = "VoipOngoingChatAlert2";
                    key2 = R.string.VoipOngoingChatAlert2;
                } else {
                    oldName = oldName3;
                    key1 = "VoipOngoingChatAlert";
                    key2 = R.string.VoipOngoingChatAlert;
                }
            }
            if (user != null) {
                newName = ContactsController.formatName(user.first_name, user.last_name);
            } else {
                String newName2 = chat.title;
                newName = newName2;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            if (callerId < 0) {
                i = R.string.VoipOngoingChatAlertTitle;
                str = "VoipOngoingChatAlertTitle";
            } else {
                i = R.string.VoipOngoingAlertTitle;
                str = "VoipOngoingAlertTitle";
            }
            voIPService = voIPService2;
            builder.setTitle(LocaleController.getString(str, i)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString(key1, key2, oldName, newName))).setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.voip.VoIPHelper$$ExternalSyntheticLambda16
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    VoIPHelper.lambda$initiateCall$3(TLRPC.User.this, chat, hash, videoCall, canVideoCall, createCall, activity, fragment, accountInstance, dialogInterface, i2);
                }
            }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null).show();
        }
    }

    public static /* synthetic */ void lambda$initiateCall$3(final TLRPC.User user, final TLRPC.Chat chat, final String hash, final boolean videoCall, final boolean canVideoCall, final boolean createCall, final Activity activity, final BaseFragment fragment, final AccountInstance accountInstance, DialogInterface dialog, int which) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().hangUp(new Runnable() { // from class: org.telegram.ui.Components.voip.VoIPHelper$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPHelper.lambda$initiateCall$2(TLRPC.User.this, chat, hash, videoCall, canVideoCall, createCall, activity, fragment, accountInstance);
                }
            });
        } else {
            doInitiateCall(user, chat, hash, null, false, videoCall, canVideoCall, createCall, activity, fragment, accountInstance, true, true);
        }
    }

    public static /* synthetic */ void lambda$initiateCall$2(TLRPC.User user, TLRPC.Chat chat, String hash, boolean videoCall, boolean canVideoCall, boolean createCall, Activity activity, BaseFragment fragment, AccountInstance accountInstance) {
        lastCallTime = 0L;
        doInitiateCall(user, chat, hash, null, false, videoCall, canVideoCall, createCall, activity, fragment, accountInstance, true, true);
    }

    /* JADX WARN: Removed duplicated region for block: B:83:0x01e2  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x01ee  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void doInitiateCall(final org.telegram.tgnet.TLRPC.User r20, final org.telegram.tgnet.TLRPC.Chat r21, final java.lang.String r22, final org.telegram.tgnet.TLRPC.InputPeer r23, boolean r24, final boolean r25, final boolean r26, final boolean r27, final android.app.Activity r28, final org.telegram.ui.ActionBar.BaseFragment r29, final org.telegram.messenger.AccountInstance r30, boolean r31, boolean r32) {
        /*
            Method dump skipped, instructions count: 615
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPHelper.doInitiateCall(org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, java.lang.String, org.telegram.tgnet.TLRPC$InputPeer, boolean, boolean, boolean, boolean, android.app.Activity, org.telegram.ui.ActionBar.BaseFragment, org.telegram.messenger.AccountInstance, boolean, boolean):void");
    }

    public static /* synthetic */ void lambda$doInitiateCall$4(final String hash, final Activity activity, final TLRPC.Chat chat, final TLRPC.User user, final TLRPC.InputPeer inputPeer, final boolean videoCall, final boolean canVideoCall, final BaseFragment fragment, final AccountInstance accountInstance, boolean param) {
        if (!param && hash != null) {
            JoinCallByUrlAlert alert = new JoinCallByUrlAlert(activity, chat) { // from class: org.telegram.ui.Components.voip.VoIPHelper.1
                @Override // org.telegram.ui.Components.JoinCallByUrlAlert
                protected void onJoin() {
                    VoIPHelper.doInitiateCall(user, chat, hash, inputPeer, true, videoCall, canVideoCall, false, activity, fragment, accountInstance, false, false);
                }
            };
            if (fragment != null) {
                fragment.showDialog(alert);
                return;
            }
            return;
        }
        doInitiateCall(user, chat, hash, inputPeer, !param, videoCall, canVideoCall, false, activity, fragment, accountInstance, false, false);
    }

    public static /* synthetic */ void lambda$doInitiateCall$5(final boolean createCall, final Activity activity, final AccountInstance accountInstance, final TLRPC.Chat chat, final String hash, final TLRPC.User user, final boolean videoCall, final boolean canVideoCall, final BaseFragment fragment, final TLRPC.InputPeer selectedPeer, boolean hasFew, boolean schedule) {
        if (createCall && schedule) {
            GroupCallActivity.create((LaunchActivity) activity, accountInstance, chat, selectedPeer, hasFew, hash);
        } else if (!hasFew && hash != null) {
            JoinCallByUrlAlert alert = new JoinCallByUrlAlert(activity, chat) { // from class: org.telegram.ui.Components.voip.VoIPHelper.2
                @Override // org.telegram.ui.Components.JoinCallByUrlAlert
                protected void onJoin() {
                    VoIPHelper.doInitiateCall(user, chat, hash, selectedPeer, false, videoCall, canVideoCall, createCall, activity, fragment, accountInstance, false, true);
                }
            };
            if (fragment != null) {
                fragment.showDialog(alert);
            }
        } else {
            doInitiateCall(user, chat, hash, selectedPeer, hasFew, videoCall, canVideoCall, createCall, activity, fragment, accountInstance, false, true);
        }
    }

    public static void permissionDenied(final Activity activity, final Runnable onFinish, int code) {
        String str;
        int i;
        boolean mergedRequest = code == 102;
        if (!activity.shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO") || (mergedRequest && !activity.shouldShowRequestPermissionRationale("android.permission.CAMERA"))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            if (mergedRequest) {
                i = R.string.VoipNeedMicCameraPermissionWithHint;
                str = "VoipNeedMicCameraPermissionWithHint";
            } else {
                i = R.string.VoipNeedMicPermissionWithHint;
                str = "VoipNeedMicPermissionWithHint";
            }
            AlertDialog.Builder dlg = builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString(str, i))).setPositiveButton(LocaleController.getString("Settings", R.string.Settings), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.voip.VoIPHelper$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    VoIPHelper.lambda$permissionDenied$7(activity, dialogInterface, i2);
                }
            }).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", R.string.ContactsPermissionAlertNotNow), null).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.voip.VoIPHelper$$ExternalSyntheticLambda18
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    VoIPHelper.lambda$permissionDenied$8(onFinish, dialogInterface);
                }
            }).setTopAnimation(mergedRequest ? R.raw.permission_request_camera : R.raw.permission_request_microphone, 72, false, Theme.getColor(Theme.key_dialogTopBackground));
            dlg.show();
        }
    }

    public static /* synthetic */ void lambda$permissionDenied$7(Activity activity, DialogInterface dialog, int which) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    public static /* synthetic */ void lambda$permissionDenied$8(Runnable onFinish, DialogInterface dialog) {
        if (onFinish != null) {
            onFinish.run();
        }
    }

    public static File getLogsDir() {
        File logsDir = new File(ApplicationLoader.applicationContext.getCacheDir(), "voip_logs");
        if (!logsDir.exists()) {
            logsDir.mkdirs();
        }
        return logsDir;
    }

    public static boolean canRateCall(TLRPC.TL_messageActionPhoneCall call) {
        if (!(call.reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy) && !(call.reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed)) {
            SharedPreferences prefs = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
            Set<String> hashes = prefs.getStringSet("calls_access_hashes", Collections.EMPTY_SET);
            for (String hash : hashes) {
                String[] d = hash.split(" ");
                if (d.length >= 2) {
                    String str = d[0];
                    if (str.equals(call.call_id + "")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void showRateAlert(Context context, TLRPC.TL_messageActionPhoneCall call) {
        SharedPreferences prefs = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        Set<String> hashes = prefs.getStringSet("calls_access_hashes", Collections.EMPTY_SET);
        for (String hash : hashes) {
            String[] d = hash.split(" ");
            if (d.length >= 2) {
                String str = d[0];
                if (str.equals(call.call_id + "")) {
                    try {
                        long accessHash = Long.parseLong(d[1]);
                        showRateAlert(context, null, call.video, call.call_id, accessHash, UserConfig.selectedAccount, true);
                        return;
                    } catch (Exception e) {
                        return;
                    }
                }
            }
        }
    }

    public static void showRateAlert(final Context context, final Runnable onDismiss, boolean isVideo, final long callID, final long accessHash, final int account, final boolean userInitiative) {
        View.OnClickListener checkClickListener;
        final File log = getLogFile(callID);
        int i = 1;
        boolean z = false;
        final int[] page = {0};
        LinearLayout alertView = new LinearLayout(context);
        alertView.setOrientation(1);
        int pad = AndroidUtilities.dp(16.0f);
        alertView.setPadding(pad, pad, pad, 0);
        final TextView text = new TextView(context);
        text.setTextSize(2, 16.0f);
        text.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        text.setGravity(17);
        text.setText(LocaleController.getString("VoipRateCallAlert", R.string.VoipRateCallAlert));
        alertView.addView(text);
        final BetterRatingView bar = new BetterRatingView(context);
        alertView.addView(bar, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
        final LinearLayout problemsWrap = new LinearLayout(context);
        problemsWrap.setOrientation(1);
        View.OnClickListener problemCheckboxClickListener = VoIPHelper$$ExternalSyntheticLambda5.INSTANCE;
        String[] problems = new String[9];
        problems[0] = isVideo ? "distorted_video" : null;
        problems[1] = isVideo ? "pixelated_video" : null;
        problems[2] = "echo";
        problems[3] = "noise";
        problems[4] = "interruptions";
        problems[5] = "distorted_speech";
        problems[6] = "silent_local";
        problems[7] = "silent_remote";
        problems[8] = "dropped";
        int i2 = 0;
        while (i2 < problems.length) {
            if (problems[i2] != null) {
                CheckBoxCell check = new CheckBoxCell(context, i);
                check.setClipToPadding(z);
                check.setTag(problems[i2]);
                String label = null;
                switch (i2) {
                    case 0:
                        label = LocaleController.getString("RateCallVideoDistorted", R.string.RateCallVideoDistorted);
                        break;
                    case 1:
                        label = LocaleController.getString("RateCallVideoPixelated", R.string.RateCallVideoPixelated);
                        break;
                    case 2:
                        label = LocaleController.getString("RateCallEcho", R.string.RateCallEcho);
                        break;
                    case 3:
                        label = LocaleController.getString("RateCallNoise", R.string.RateCallNoise);
                        break;
                    case 4:
                        label = LocaleController.getString("RateCallInterruptions", R.string.RateCallInterruptions);
                        break;
                    case 5:
                        label = LocaleController.getString("RateCallDistorted", R.string.RateCallDistorted);
                        break;
                    case 6:
                        label = LocaleController.getString("RateCallSilentLocal", R.string.RateCallSilentLocal);
                        break;
                    case 7:
                        label = LocaleController.getString("RateCallSilentRemote", R.string.RateCallSilentRemote);
                        break;
                    case 8:
                        label = LocaleController.getString("RateCallDropped", R.string.RateCallDropped);
                        break;
                }
                check.setText(label, null, false, false);
                check.setOnClickListener(problemCheckboxClickListener);
                check.setTag(problems[i2]);
                problemsWrap.addView(check);
            }
            i2++;
            i = 1;
            z = false;
        }
        alertView.addView(problemsWrap, LayoutHelper.createLinear(-1, -2, -8.0f, 0.0f, -8.0f, 0.0f));
        problemsWrap.setVisibility(8);
        final EditTextBoldCursor commentBox = new EditTextBoldCursor(context);
        commentBox.setHint(LocaleController.getString("VoipFeedbackCommentHint", R.string.VoipFeedbackCommentHint));
        commentBox.setInputType(147457);
        commentBox.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        commentBox.setHintTextColor(Theme.getColor(Theme.key_dialogTextHint));
        commentBox.setBackground(null);
        commentBox.setLineColors(Theme.getColor(Theme.key_dialogInputField), Theme.getColor(Theme.key_dialogInputFieldActivated), Theme.getColor(Theme.key_dialogTextRed2));
        commentBox.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        commentBox.setTextSize(1, 18.0f);
        commentBox.setVisibility(8);
        alertView.addView(commentBox, LayoutHelper.createLinear(-1, -2, 8.0f, 8.0f, 8.0f, 0.0f));
        final boolean[] includeLogs = {true};
        final CheckBoxCell checkbox = new CheckBoxCell(context, 1);
        View.OnClickListener checkClickListener2 = new View.OnClickListener() { // from class: org.telegram.ui.Components.voip.VoIPHelper$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VoIPHelper.lambda$showRateAlert$10(includeLogs, checkbox, view);
            }
        };
        checkbox.setText(LocaleController.getString("CallReportIncludeLogs", R.string.CallReportIncludeLogs), null, true, false);
        checkbox.setClipToPadding(false);
        checkbox.setOnClickListener(checkClickListener2);
        alertView.addView(checkbox, LayoutHelper.createLinear(-1, -2, -8.0f, 0.0f, -8.0f, 0.0f));
        final TextView logsText = new TextView(context);
        logsText.setTextSize(2, 14.0f);
        logsText.setTextColor(Theme.getColor(Theme.key_dialogTextGray3));
        logsText.setText(LocaleController.getString("CallReportLogsExplain", R.string.CallReportLogsExplain));
        logsText.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        logsText.setOnClickListener(checkClickListener2);
        alertView.addView(logsText);
        checkbox.setVisibility(8);
        logsText.setVisibility(8);
        if (!log.exists()) {
            includeLogs[0] = false;
        }
        final AlertDialog alert = new AlertDialog.Builder(context).setTitle(LocaleController.getString("CallMessageReportProblem", R.string.CallMessageReportProblem)).setView(alertView).setPositiveButton(LocaleController.getString("Send", R.string.Send), VoIPHelper$$ExternalSyntheticLambda17.INSTANCE).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.voip.VoIPHelper$$ExternalSyntheticLambda19
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                VoIPHelper.lambda$showRateAlert$12(onDismiss, dialogInterface);
            }
        }).create();
        if (BuildVars.LOGS_ENABLED && log.exists()) {
            checkClickListener = checkClickListener2;
            alert.setNeutralButton("Send log", new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.voip.VoIPHelper$$ExternalSyntheticLambda14
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i3) {
                    VoIPHelper.lambda$showRateAlert$13(context, log, dialogInterface, i3);
                }
            });
        } else {
            checkClickListener = checkClickListener2;
        }
        alert.show();
        alert.getWindow().setSoftInputMode(3);
        final View btn = alert.getButton(-1);
        btn.setEnabled(false);
        bar.setOnRatingChangeListener(new BetterRatingView.OnRatingChangeListener() { // from class: org.telegram.ui.Components.voip.VoIPHelper$$ExternalSyntheticLambda10
            @Override // org.telegram.ui.Components.BetterRatingView.OnRatingChangeListener
            public final void onRatingChanged(int i3) {
                VoIPHelper.lambda$showRateAlert$14(btn, i3);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.voip.VoIPHelper$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VoIPHelper.lambda$showRateAlert$16(BetterRatingView.this, page, problemsWrap, commentBox, includeLogs, accessHash, callID, userInitiative, account, log, context, alert, text, checkbox, logsText, btn, view);
            }
        });
    }

    public static /* synthetic */ void lambda$showRateAlert$9(View v) {
        CheckBoxCell check = (CheckBoxCell) v;
        check.setChecked(!check.isChecked(), true);
    }

    public static /* synthetic */ void lambda$showRateAlert$10(boolean[] includeLogs, CheckBoxCell checkbox, View v) {
        includeLogs[0] = !includeLogs[0];
        checkbox.setChecked(includeLogs[0], true);
    }

    public static /* synthetic */ void lambda$showRateAlert$11(DialogInterface dialog, int which) {
    }

    public static /* synthetic */ void lambda$showRateAlert$12(Runnable onDismiss, DialogInterface dialog) {
        if (onDismiss != null) {
            onDismiss.run();
        }
    }

    public static /* synthetic */ void lambda$showRateAlert$13(Context context, File log, DialogInterface dialog, int which) {
        Intent intent = new Intent(context, LaunchActivity.class);
        intent.setAction("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(log));
        context.startActivity(intent);
    }

    public static /* synthetic */ void lambda$showRateAlert$14(View btn, int rating) {
        String str;
        int i;
        btn.setEnabled(rating > 0);
        TextView textView = (TextView) btn;
        if (rating < 4) {
            i = R.string.Next;
            str = "Next";
        } else {
            i = R.string.Send;
            str = "Send";
        }
        textView.setText(LocaleController.getString(str, i).toUpperCase());
    }

    public static /* synthetic */ void lambda$showRateAlert$16(BetterRatingView bar, int[] page, LinearLayout problemsWrap, EditTextBoldCursor commentBox, final boolean[] includeLogs, long accessHash, long callID, boolean userInitiative, int account, final File log, final Context context, AlertDialog alert, TextView text, CheckBoxCell checkbox, TextView logsText, View btn, View v) {
        int rating = bar.getRating();
        if (rating < 4 && page[0] != 1) {
            page[0] = 1;
            bar.setVisibility(8);
            text.setVisibility(8);
            alert.setTitle(LocaleController.getString("CallReportHint", R.string.CallReportHint));
            commentBox.setVisibility(0);
            if (log.exists()) {
                checkbox.setVisibility(0);
                logsText.setVisibility(0);
            }
            problemsWrap.setVisibility(0);
            ((TextView) btn).setText(LocaleController.getString("Send", R.string.Send).toUpperCase());
            return;
        }
        final int currentAccount = UserConfig.selectedAccount;
        final TLRPC.TL_phone_setCallRating req = new TLRPC.TL_phone_setCallRating();
        req.rating = bar.getRating();
        final ArrayList<String> problemTags = new ArrayList<>();
        for (int i = 0; i < problemsWrap.getChildCount(); i++) {
            CheckBoxCell check = (CheckBoxCell) problemsWrap.getChildAt(i);
            if (check.isChecked()) {
                problemTags.add("#" + check.getTag());
            }
        }
        int i2 = req.rating;
        if (i2 < 5) {
            req.comment = commentBox.getText().toString();
        } else {
            req.comment = "";
        }
        if (!problemTags.isEmpty() && !includeLogs[0]) {
            req.comment += " " + TextUtils.join(" ", problemTags);
        }
        req.peer = new TLRPC.TL_inputPhoneCall();
        req.peer.access_hash = accessHash;
        req.peer.id = callID;
        req.user_initiative = userInitiative;
        ConnectionsManager.getInstance(account).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.voip.VoIPHelper$$ExternalSyntheticLambda9
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                VoIPHelper.lambda$showRateAlert$15(currentAccount, includeLogs, log, req, problemTags, context, tLObject, tL_error);
            }
        });
        alert.dismiss();
    }

    public static /* synthetic */ void lambda$showRateAlert$15(int currentAccount, boolean[] includeLogs, File log, TLRPC.TL_phone_setCallRating req, ArrayList problemTags, Context context, TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_updates) {
            TLRPC.TL_updates updates = (TLRPC.TL_updates) response;
            MessagesController.getInstance(currentAccount).processUpdates(updates, false);
        }
        if (includeLogs[0] && log.exists()) {
            if (req.rating < 4) {
                AccountInstance accountInstance = AccountInstance.getInstance(UserConfig.selectedAccount);
                SendMessagesHelper.prepareSendingDocument(accountInstance, log.getAbsolutePath(), log.getAbsolutePath(), null, TextUtils.join(" ", problemTags), ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN, 4244000L, null, null, null, null, true, 0);
                Toast.makeText(context, LocaleController.getString("CallReportSent", R.string.CallReportSent), 1).show();
            }
        }
    }

    private static File getLogFile(long callID) {
        File debugLogsDir;
        String[] logs;
        if (BuildVars.DEBUG_VERSION && (logs = (debugLogsDir = new File(ApplicationLoader.applicationContext.getExternalFilesDir(null), "logs")).list()) != null) {
            for (String log : logs) {
                if (log.endsWith("voip" + callID + ".txt")) {
                    return new File(debugLogsDir, log);
                }
            }
        }
        return new File(getLogsDir(), callID + ".log");
    }

    public static void showCallDebugSettings(Context context) {
        final SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(1);
        TextView warning = new TextView(context);
        warning.setTextSize(1, 15.0f);
        warning.setText("Please only change these settings if you know exactly what they do.");
        warning.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        ll.addView(warning, LayoutHelper.createLinear(-1, -2, 16.0f, 8.0f, 16.0f, 8.0f));
        final TextCheckCell tcpCell = new TextCheckCell(context);
        tcpCell.setTextAndCheck("Force TCP", preferences.getBoolean("dbg_force_tcp_in_calls", false), false);
        tcpCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.voip.VoIPHelper$$ExternalSyntheticLambda20
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VoIPHelper.lambda$showCallDebugSettings$17(preferences, tcpCell, view);
            }
        });
        ll.addView(tcpCell);
        if (BuildVars.DEBUG_VERSION && BuildVars.LOGS_ENABLED) {
            final TextCheckCell dumpCell = new TextCheckCell(context);
            dumpCell.setTextAndCheck("Dump detailed stats", preferences.getBoolean("dbg_dump_call_stats", false), false);
            dumpCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.voip.VoIPHelper$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    VoIPHelper.lambda$showCallDebugSettings$18(preferences, dumpCell, view);
                }
            });
            ll.addView(dumpCell);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            final TextCheckCell connectionServiceCell = new TextCheckCell(context);
            connectionServiceCell.setTextAndCheck("Enable ConnectionService", preferences.getBoolean("dbg_force_connection_service", false), false);
            connectionServiceCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.voip.VoIPHelper$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    VoIPHelper.lambda$showCallDebugSettings$19(preferences, connectionServiceCell, view);
                }
            });
            ll.addView(connectionServiceCell);
        }
        new AlertDialog.Builder(context).setTitle(LocaleController.getString("DebugMenuCallSettings", R.string.DebugMenuCallSettings)).setView(ll).show();
    }

    public static /* synthetic */ void lambda$showCallDebugSettings$17(SharedPreferences preferences, TextCheckCell tcpCell, View v) {
        boolean force = preferences.getBoolean("dbg_force_tcp_in_calls", false);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("dbg_force_tcp_in_calls", !force);
        editor.commit();
        tcpCell.setChecked(!force);
    }

    public static /* synthetic */ void lambda$showCallDebugSettings$18(SharedPreferences preferences, TextCheckCell dumpCell, View v) {
        boolean force = preferences.getBoolean("dbg_dump_call_stats", false);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("dbg_dump_call_stats", !force);
        editor.commit();
        dumpCell.setChecked(!force);
    }

    public static /* synthetic */ void lambda$showCallDebugSettings$19(SharedPreferences preferences, TextCheckCell connectionServiceCell, View v) {
        boolean force = preferences.getBoolean("dbg_force_connection_service", false);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("dbg_force_connection_service", !force);
        editor.commit();
        connectionServiceCell.setChecked(!force);
    }

    public static int getDataSavingDefault() {
        boolean low = DownloadController.getInstance(0).lowPreset.lessCallData;
        boolean medium = DownloadController.getInstance(0).mediumPreset.lessCallData;
        boolean high = DownloadController.getInstance(0).highPreset.lessCallData;
        if (!low && !medium && !high) {
            return 0;
        }
        if (low && !medium && !high) {
            return 3;
        }
        if (low && medium && !high) {
            return 1;
        }
        if (low && medium && high) {
            return 2;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.w("Invalid call data saving preset configuration: " + low + "/" + medium + "/" + high);
        }
        return 0;
    }

    public static String getLogFilePath(String name) {
        Calendar c = Calendar.getInstance();
        File externalFilesDir = ApplicationLoader.applicationContext.getExternalFilesDir(null);
        return new File(externalFilesDir, String.format(Locale.US, "logs/%02d_%02d_%04d_%02d_%02d_%02d_%s.txt", Integer.valueOf(c.get(5)), Integer.valueOf(c.get(2) + 1), Integer.valueOf(c.get(1)), Integer.valueOf(c.get(11)), Integer.valueOf(c.get(12)), Integer.valueOf(c.get(13)), name)).getAbsolutePath();
    }

    public static String getLogFilePath(long callId, boolean stats) {
        File[] _logs;
        File logsDir = getLogsDir();
        if (!BuildVars.DEBUG_VERSION && (_logs = logsDir.listFiles()) != null) {
            ArrayList<File> logs = new ArrayList<>(Arrays.asList(_logs));
            while (logs.size() > 20) {
                File oldest = logs.get(0);
                Iterator<File> it = logs.iterator();
                while (it.hasNext()) {
                    File file = it.next();
                    if (file.getName().endsWith(".log") && file.lastModified() < oldest.lastModified()) {
                        oldest = file;
                    }
                }
                oldest.delete();
                logs.remove(oldest);
            }
        }
        if (stats) {
            return new File(logsDir, callId + "_stats.log").getAbsolutePath();
        }
        return new File(logsDir, callId + ".log").getAbsolutePath();
    }

    public static void showGroupCallAlert(final BaseFragment fragment, final TLRPC.Chat currentChat, final TLRPC.InputPeer peer, boolean recreate, final AccountInstance accountInstance) {
        if (fragment == null || fragment.getParentActivity() == null) {
            return;
        }
        JoinCallAlert.checkFewUsers(fragment.getParentActivity(), -currentChat.id, accountInstance, new MessagesStorage.BooleanCallback() { // from class: org.telegram.ui.Components.voip.VoIPHelper$$ExternalSyntheticLambda8
            @Override // org.telegram.messenger.MessagesStorage.BooleanCallback
            public final void run(boolean z) {
                VoIPHelper.startCall(TLRPC.Chat.this, peer, null, true, r2.getParentActivity(), fragment, accountInstance);
            }
        });
    }
}
