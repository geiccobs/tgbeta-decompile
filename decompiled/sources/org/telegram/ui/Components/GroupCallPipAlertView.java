package org.telegram.ui.Components;

import android.content.Context;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.voip.VoIPButtonsLayout;
import org.telegram.ui.Components.voip.VoIPToggleButton;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.LaunchActivity;
/* loaded from: classes5.dex */
public class GroupCallPipAlertView extends LinearLayout implements VoIPService.StateListener, NotificationCenter.NotificationCenterDelegate {
    public static final int POSITION_BOTTOM = 2;
    public static final int POSITION_LEFT = 0;
    public static final int POSITION_RIGHT = 1;
    public static final int POSITION_TOP = 3;
    BackupImageView avatarImageView;
    int currentAccount;
    float cx;
    float cy;
    FrameLayout groupInfoContainer;
    VoIPToggleButton leaveButton;
    LinearGradient linearGradient;
    VoIPToggleButton muteButton;
    float muteProgress;
    private boolean mutedByAdmin;
    float mutedByAdminProgress;
    private int position;
    VoIPToggleButton soundButton;
    TextView subtitleView;
    TextView titleView;
    RectF rectF = new RectF();
    Paint paint = new Paint(1);
    private boolean invalidateGradient = true;

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onCameraFirstFrameAvailable() {
        VoIPService.StateListener.CC.$default$onCameraFirstFrameAvailable(this);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onCameraSwitch(boolean z) {
        VoIPService.StateListener.CC.$default$onCameraSwitch(this, z);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onMediaStateUpdated(int i, int i2) {
        VoIPService.StateListener.CC.$default$onMediaStateUpdated(this, i, i2);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onScreenOnChange(boolean z) {
        VoIPService.StateListener.CC.$default$onScreenOnChange(this, z);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onSignalBarsCountChanged(int i) {
        VoIPService.StateListener.CC.$default$onSignalBarsCountChanged(this, i);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onVideoAvailableChange(boolean z) {
        VoIPService.StateListener.CC.$default$onVideoAvailableChange(this, z);
    }

    public GroupCallPipAlertView(final Context context, int account) {
        super(context);
        setOrientation(1);
        this.currentAccount = account;
        this.paint.setAlpha(234);
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.GroupCallPipAlertView.1
            @Override // android.view.View
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                if (Build.VERSION.SDK_INT >= 21) {
                    VoIPService service = VoIPService.getSharedInstance();
                    if (service != null && ChatObject.isChannelOrGiga(service.getChat())) {
                        info.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, LocaleController.getString("VoipChannelOpenVoiceChat", R.string.VoipChannelOpenVoiceChat)));
                    } else {
                        info.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, LocaleController.getString("VoipGroupOpenVoiceChat", R.string.VoipGroupOpenVoiceChat)));
                    }
                }
            }
        };
        this.groupInfoContainer = frameLayout;
        frameLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(22.0f));
        this.groupInfoContainer.addView(this.avatarImageView, LayoutHelper.createFrame(44, 44.0f));
        this.groupInfoContainer.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), 0, ColorUtils.setAlphaComponent(-1, 76)));
        this.groupInfoContainer.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.GroupCallPipAlertView$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                GroupCallPipAlertView.this.m2658lambda$new$0$orgtelegramuiComponentsGroupCallPipAlertView(view);
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setTextColor(-1);
        this.titleView.setTextSize(15.0f);
        this.titleView.setMaxLines(2);
        this.titleView.setEllipsize(TextUtils.TruncateAt.END);
        this.titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        linearLayout.addView(this.titleView, LayoutHelper.createLinear(-1, -2));
        TextView textView2 = new TextView(context);
        this.subtitleView = textView2;
        textView2.setTextSize(12.0f);
        this.subtitleView.setTextColor(ColorUtils.setAlphaComponent(-1, 153));
        linearLayout.addView(this.subtitleView, LayoutHelper.createLinear(-1, -2));
        this.groupInfoContainer.addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 16, 55.0f, 0.0f, 0.0f, 0.0f));
        addView(this.groupInfoContainer, LayoutHelper.createLinear(-1, -2, 0, 10, 10, 10, 10));
        VoIPToggleButton voIPToggleButton = new VoIPToggleButton(context, 44.0f);
        this.soundButton = voIPToggleButton;
        voIPToggleButton.setTextSize(12);
        this.soundButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.GroupCallPipAlertView$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                GroupCallPipAlertView.this.m2659lambda$new$1$orgtelegramuiComponentsGroupCallPipAlertView(context, view);
            }
        });
        this.soundButton.setCheckable(true);
        this.soundButton.setBackgroundColor(ColorUtils.setAlphaComponent(-1, 38), ColorUtils.setAlphaComponent(-1, 76));
        VoIPToggleButton voIPToggleButton2 = new VoIPToggleButton(context, 44.0f);
        this.muteButton = voIPToggleButton2;
        voIPToggleButton2.setTextSize(12);
        this.muteButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.GroupCallPipAlertView$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                GroupCallPipAlertView.this.m2660lambda$new$2$orgtelegramuiComponentsGroupCallPipAlertView(context, view);
            }
        });
        VoIPToggleButton voIPToggleButton3 = new VoIPToggleButton(context, 44.0f);
        this.leaveButton = voIPToggleButton3;
        voIPToggleButton3.setTextSize(12);
        this.leaveButton.setData(R.drawable.calls_decline, -1, -3257782, 0.3f, false, LocaleController.getString("VoipGroupLeave", R.string.VoipGroupLeave), false, false);
        this.leaveButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.GroupCallPipAlertView$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                GroupCallPipAlertView.this.m2661lambda$new$4$orgtelegramuiComponentsGroupCallPipAlertView(context, view);
            }
        });
        VoIPButtonsLayout buttonsContainer = new VoIPButtonsLayout(context);
        buttonsContainer.setChildSize(68);
        buttonsContainer.setUseStartPadding(false);
        buttonsContainer.addView(this.soundButton, LayoutHelper.createFrame(68, 63.0f));
        buttonsContainer.addView(this.muteButton, LayoutHelper.createFrame(68, 63.0f));
        buttonsContainer.addView(this.leaveButton, LayoutHelper.createFrame(68, 63.0f));
        setWillNotDraw(false);
        addView(buttonsContainer, LayoutHelper.createLinear(-1, -2, 0, 6, 0, 6, 0));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-GroupCallPipAlertView */
    public /* synthetic */ void m2658lambda$new$0$orgtelegramuiComponentsGroupCallPipAlertView(View view) {
        if (VoIPService.getSharedInstance() != null) {
            Intent intent = new Intent(getContext(), LaunchActivity.class).setAction("voip_chat");
            intent.putExtra("currentAccount", VoIPService.getSharedInstance().getAccount());
            getContext().startActivity(intent);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-GroupCallPipAlertView */
    public /* synthetic */ void m2659lambda$new$1$orgtelegramuiComponentsGroupCallPipAlertView(Context context, View v) {
        if (VoIPService.getSharedInstance() == null) {
            return;
        }
        VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(getContext(), Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(context));
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-GroupCallPipAlertView */
    public /* synthetic */ void m2660lambda$new$2$orgtelegramuiComponentsGroupCallPipAlertView(Context context, View v) {
        if (VoIPService.getSharedInstance() != null) {
            if (VoIPService.getSharedInstance().mutedByAdmin()) {
                this.muteButton.shakeView();
                try {
                    Vibrator vibrator = (Vibrator) context.getSystemService("vibrator");
                    if (vibrator != null) {
                        vibrator.vibrate(200L);
                        return;
                    }
                    return;
                } catch (Exception e) {
                    FileLog.e(e);
                    return;
                }
            }
            VoIPService.getSharedInstance().setMicMute(!VoIPService.getSharedInstance().isMicMute(), false, true);
        }
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-GroupCallPipAlertView */
    public /* synthetic */ void m2661lambda$new$4$orgtelegramuiComponentsGroupCallPipAlertView(final Context context, View v) {
        GroupCallActivity.onLeaveClick(getContext(), new Runnable() { // from class: org.telegram.ui.Components.GroupCallPipAlertView$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                GroupCallPip.updateVisibility(context);
            }
        }, Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(context));
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x0055  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x006c  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x0085  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x01c5  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x01ce  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x0209  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x022c  */
    @Override // android.widget.LinearLayout, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onDraw(android.graphics.Canvas r29) {
        /*
            Method dump skipped, instructions count: 721
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupCallPipAlertView.onDraw(android.graphics.Canvas):void");
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(230.0f), C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        String titleStr;
        super.onAttachedToWindow();
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null && service.groupCall != null) {
            int color2 = AvatarDrawable.getColorForId(service.getChat().id);
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setColor(color2);
            avatarDrawable.setInfo(service.getChat());
            this.avatarImageView.setImage(ImageLocation.getForLocal(service.getChat().photo.photo_small), "50_50", avatarDrawable, (Object) null);
            if (!TextUtils.isEmpty(service.groupCall.call.title)) {
                titleStr = service.groupCall.call.title;
            } else {
                titleStr = service.getChat().title;
            }
            if (titleStr != null) {
                titleStr = titleStr.replace("\n", " ").replaceAll(" +", " ").trim();
            }
            this.titleView.setText(titleStr);
            updateMembersCount();
            service.registerStateListener(this);
            if (VoIPService.getSharedInstance() != null) {
                this.mutedByAdmin = VoIPService.getSharedInstance().mutedByAdmin();
            }
            float f = 1.0f;
            this.mutedByAdminProgress = this.mutedByAdmin ? 1.0f : 0.0f;
            boolean isMute = VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().isMicMute() || this.mutedByAdmin;
            if (!isMute) {
                f = 0.0f;
            }
            this.muteProgress = f;
        }
        int color22 = this.currentAccount;
        NotificationCenter.getInstance(color22).addObserver(this, NotificationCenter.groupCallUpdated);
        updateButtons(false);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            service.unregisterStateListener(this);
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupCallUpdated);
    }

    private void updateMembersCount() {
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null && service.groupCall != null) {
            int currentCallState = service.getCallState();
            if (!service.isSwitchingStream() && (currentCallState == 1 || currentCallState == 2 || currentCallState == 6 || currentCallState == 5)) {
                this.subtitleView.setText(LocaleController.getString("VoipGroupConnecting", R.string.VoipGroupConnecting));
            } else {
                this.subtitleView.setText(LocaleController.formatPluralString(service.groupCall.call.rtmp_stream ? "ViewersWatching" : "Participants", service.groupCall.call.participants_count, new Object[0]));
            }
        }
    }

    private void updateButtons(boolean animated) {
        VoIPService service;
        String str;
        int i;
        if (this.soundButton == null || this.muteButton == null || (service = VoIPService.getSharedInstance()) == null) {
            return;
        }
        boolean bluetooth = service.isBluetoothOn();
        boolean checked = !bluetooth && service.isSpeakerphoneOn();
        this.soundButton.setChecked(checked, animated);
        if (bluetooth) {
            this.soundButton.setData(R.drawable.calls_bluetooth, -1, 0, 0.1f, true, LocaleController.getString("VoipAudioRoutingBluetooth", R.string.VoipAudioRoutingBluetooth), false, animated);
        } else if (checked) {
            this.soundButton.setData(R.drawable.calls_speaker, -1, 0, 0.3f, true, LocaleController.getString("VoipSpeaker", R.string.VoipSpeaker), false, animated);
        } else if (!service.isHeadsetPlugged()) {
            this.soundButton.setData(R.drawable.calls_speaker, -1, 0, 0.1f, true, LocaleController.getString("VoipSpeaker", R.string.VoipSpeaker), false, animated);
        } else {
            this.soundButton.setData(R.drawable.calls_headphones, -1, 0, 0.1f, true, LocaleController.getString("VoipAudioRoutingHeadset", R.string.VoipAudioRoutingHeadset), false, animated);
        }
        if (service.mutedByAdmin()) {
            this.muteButton.setData(R.drawable.calls_unmute, -1, ColorUtils.setAlphaComponent(-1, 76), 0.1f, true, LocaleController.getString("VoipMutedByAdminShort", R.string.VoipMutedByAdminShort), true, animated);
        } else {
            VoIPToggleButton voIPToggleButton = this.muteButton;
            int alphaComponent = ColorUtils.setAlphaComponent(-1, (int) ((service.isMicMute() ? 0.3f : 0.15f) * 255.0f));
            if (service.isMicMute()) {
                i = R.string.VoipUnmute;
                str = "VoipUnmute";
            } else {
                i = R.string.VoipMute;
                str = "VoipMute";
            }
            voIPToggleButton.setData(R.drawable.calls_unmute, -1, alphaComponent, 0.1f, true, LocaleController.getString(str, i), service.isMicMute(), animated);
        }
        invalidate();
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onAudioSettingsChanged() {
        updateButtons(true);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onStateChanged(int state) {
        updateMembersCount();
    }

    public void setPosition(int position, float cx, float cy) {
        this.position = position;
        this.cx = cx;
        this.cy = cy;
        invalidate();
        this.invalidateGradient = true;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        boolean mutedByAdminNew;
        if (id == NotificationCenter.groupCallUpdated) {
            updateMembersCount();
            if (VoIPService.getSharedInstance() != null && (mutedByAdminNew = VoIPService.getSharedInstance().mutedByAdmin()) != this.mutedByAdmin) {
                this.mutedByAdmin = mutedByAdminNew;
                invalidate();
            }
        }
    }
}
