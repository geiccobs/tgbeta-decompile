package org.telegram.messenger.voip;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRouter;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.telecom.CallAudioState;
import android.telecom.Connection;
import android.telecom.DisconnectCause;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.LruCache;
import android.view.KeyEvent;
import android.widget.RemoteViews;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.upstream.cache.ContentMetadata;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONObject;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.StatsController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.XiaomiUtilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.voip.Instance;
import org.telegram.messenger.voip.NativeInstance;
import org.telegram.messenger.voip.VoIPController;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.RequestDelegateTimestamp;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatReactionsEditActivity;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.JoinCallAlert;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.VoIPFeedbackActivity;
import org.telegram.ui.VoIPFragment;
import org.telegram.ui.VoIPPermissionActivity;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;
import org.webrtc.voiceengine.WebRtcAudioTrack;
/* loaded from: classes4.dex */
public class VoIPService extends Service implements SensorEventListener, AudioManager.OnAudioFocusChangeListener, VoIPController.ConnectionStateListener, NotificationCenter.NotificationCenterDelegate {
    public static final String ACTION_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
    public static final int AUDIO_ROUTE_BLUETOOTH = 2;
    public static final int AUDIO_ROUTE_EARPIECE = 0;
    public static final int AUDIO_ROUTE_SPEAKER = 1;
    public static final int CALL_MIN_LAYER = 65;
    public static final int CAPTURE_DEVICE_CAMERA = 0;
    public static final int CAPTURE_DEVICE_SCREEN = 1;
    public static final int DISCARD_REASON_DISCONNECT = 2;
    public static final int DISCARD_REASON_HANGUP = 1;
    public static final int DISCARD_REASON_LINE_BUSY = 4;
    public static final int DISCARD_REASON_MISSED = 3;
    private static final int ID_INCOMING_CALL_NOTIFICATION = 202;
    private static final int ID_ONGOING_CALL_NOTIFICATION = 201;
    private static final int PROXIMITY_SCREEN_OFF_WAKE_LOCK = 32;
    public static final int QUALITY_FULL = 2;
    public static final int QUALITY_MEDIUM = 1;
    public static final int QUALITY_SMALL = 0;
    public static final int STATE_BUSY = 17;
    public static final int STATE_CREATING = 6;
    public static final int STATE_ENDED = 11;
    public static final int STATE_ESTABLISHED = 3;
    public static final int STATE_EXCHANGING_KEYS = 12;
    public static final int STATE_FAILED = 4;
    public static final int STATE_HANGING_UP = 10;
    public static final int STATE_RECONNECTING = 5;
    public static final int STATE_REQUESTING = 14;
    public static final int STATE_RINGING = 16;
    public static final int STATE_WAITING = 13;
    public static final int STATE_WAITING_INCOMING = 15;
    public static final int STATE_WAIT_INIT = 1;
    public static final int STATE_WAIT_INIT_ACK = 2;
    public static NativeInstance.AudioLevelsCallback audioLevelsCallback;
    public static TLRPC.PhoneCall callIShouldHavePutIntoIntent;
    private static Runnable setModeRunnable;
    private static VoIPService sharedInstance;
    private byte[] a_or_b;
    private boolean audioConfigured;
    private byte[] authKey;
    private boolean bluetoothScoActive;
    private boolean bluetoothScoConnecting;
    private BluetoothAdapter btAdapter;
    private int callDiscardReason;
    private int callReqId;
    private long callStartTime;
    private TLRPC.Chat chat;
    private int checkRequestId;
    private int classGuid;
    private Runnable connectingSoundRunnable;
    private PowerManager.WakeLock cpuWakelock;
    private boolean createGroupCall;
    public String currentBluetoothDeviceName;
    public boolean currentGroupModeStreaming;
    private Runnable delayedStartOutgoingCall;
    private boolean didDeleteConnectionServiceContact;
    private boolean endCallAfterRequest;
    boolean fetchingBluetoothDeviceName;
    private boolean forceRating;
    private byte[] g_a;
    private byte[] g_a_hash;
    public ChatObject.Call groupCall;
    private TLRPC.InputPeer groupCallPeer;
    private boolean hasAudioFocus;
    public boolean hasFewPeers;
    private boolean isBtHeadsetConnected;
    private boolean isHeadsetPlugged;
    private boolean isOutgoing;
    private boolean isPrivateScreencast;
    private boolean isProximityNear;
    private boolean isVideoAvailable;
    private String joinHash;
    private long keyFingerprint;
    private String lastError;
    private NetworkInfo lastNetInfo;
    private long lastTypingTimeSend;
    private Boolean mHasEarpiece;
    private boolean micMute;
    public boolean micSwitching;
    private TLRPC.TL_dataJSON myParams;
    private boolean needPlayEndSound;
    private boolean needRateCall;
    private boolean needSendDebugLog;
    private boolean needSwitchToBluetoothAfterScoActivates;
    private boolean notificationsDisabled;
    private Runnable onDestroyRunnable;
    private boolean playedConnectedSound;
    private boolean playingSound;
    private Instance.TrafficStats prevTrafficStats;
    public TLRPC.PhoneCall privateCall;
    private PowerManager.WakeLock proximityWakelock;
    private boolean reconnectScreenCapture;
    private MediaPlayer ringtonePlayer;
    private int scheduleDate;
    private Runnable shortPollRunnable;
    private int signalBarCount;
    private SoundPool soundPool;
    private int spAllowTalkId;
    private int spBusyId;
    private int spConnectingId;
    private int spEndId;
    private int spFailedID;
    private int spPlayId;
    private int spRingbackID;
    private int spStartRecordId;
    private int spVoiceChatConnecting;
    private int spVoiceChatEndId;
    private int spVoiceChatStartId;
    private boolean speakerphoneStateToSet;
    private boolean startedRinging;
    private boolean switchingAccount;
    private boolean switchingCamera;
    private boolean switchingStream;
    private Runnable switchingStreamTimeoutRunnable;
    private CallConnection systemCallConnection;
    private Runnable timeoutRunnable;
    private boolean unmutedByHold;
    private Runnable updateNotificationRunnable;
    private TLRPC.User user;
    private Vibrator vibrator;
    public boolean videoCall;
    private boolean wasConnected;
    private boolean wasEstablished;
    private static final boolean USE_CONNECTION_SERVICE = isDeviceCompatibleWithConnectionServiceAPI();
    private static final Object sync = new Object();
    private int currentAccount = -1;
    private int currentState = 0;
    private boolean isFrontFaceCamera = true;
    private int previousAudioOutput = -1;
    private ArrayList<StateListener> stateListeners = new ArrayList<>();
    private int remoteVideoState = 0;
    private int[] mySource = new int[2];
    private NativeInstance[] tgVoip = new NativeInstance[2];
    private long[] captureDevice = new long[2];
    private boolean[] destroyCaptureDevice = {true, true};
    private int[] videoState = {0, 0};
    private int remoteAudioState = 1;
    private int audioRouteToSet = 2;
    public final SharedUIParams sharedUIParams = new SharedUIParams();
    private ArrayList<TLRPC.PhoneCall> pendingUpdates = new ArrayList<>();
    private HashMap<String, Integer> currentStreamRequestTimestamp = new HashMap<>();
    private Runnable afterSoundRunnable = new AnonymousClass1();
    private BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener() { // from class: org.telegram.messenger.voip.VoIPService.2
        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int profile) {
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            Iterator<BluetoothDevice> it = proxy.getConnectedDevices().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                BluetoothDevice device = it.next();
                if (proxy.getConnectionState(device) == 2) {
                    VoIPService.this.currentBluetoothDeviceName = device.getName();
                    break;
                }
            }
            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy);
            VoIPService.this.fetchingBluetoothDeviceName = false;
        }
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() { // from class: org.telegram.messenger.voip.VoIPService.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            boolean z = true;
            if (VoIPService.ACTION_HEADSET_PLUG.equals(intent.getAction())) {
                VoIPService.this.isHeadsetPlugged = intent.getIntExtra(RemoteConfigConstants.ResponseFieldKey.STATE, 0) == 1;
                if (VoIPService.this.isHeadsetPlugged && VoIPService.this.proximityWakelock != null && VoIPService.this.proximityWakelock.isHeld()) {
                    VoIPService.this.proximityWakelock.release();
                }
                if (!VoIPService.this.isHeadsetPlugged) {
                    if (VoIPService.this.previousAudioOutput >= 0) {
                        VoIPService voIPService = VoIPService.this;
                        voIPService.setAudioOutput(voIPService.previousAudioOutput);
                        VoIPService.this.previousAudioOutput = -1;
                    }
                } else {
                    AudioManager am = (AudioManager) VoIPService.this.getSystemService("audio");
                    if (am.isSpeakerphoneOn()) {
                        VoIPService.this.previousAudioOutput = 0;
                    } else if (am.isBluetoothScoOn()) {
                        VoIPService.this.previousAudioOutput = 2;
                    } else {
                        VoIPService.this.previousAudioOutput = 1;
                    }
                    VoIPService.this.setAudioOutput(1);
                }
                VoIPService.this.isProximityNear = false;
                VoIPService.this.updateOutputGainControlState();
            } else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                VoIPService.this.updateNetworkType();
            } else if ("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED".equals(intent.getAction())) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("bt headset state = " + intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0));
                }
                VoIPService voIPService2 = VoIPService.this;
                if (intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0) != 2) {
                    z = false;
                }
                voIPService2.updateBluetoothHeadsetState(z);
            } else if ("android.media.ACTION_SCO_AUDIO_STATE_UPDATED".equals(intent.getAction())) {
                int state = intent.getIntExtra("android.media.extra.SCO_AUDIO_STATE", 0);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Bluetooth SCO state updated: " + state);
                }
                if (state == 0 && VoIPService.this.isBtHeadsetConnected && (!VoIPService.this.btAdapter.isEnabled() || VoIPService.this.btAdapter.getProfileConnectionState(1) != 2)) {
                    VoIPService.this.updateBluetoothHeadsetState(false);
                    return;
                }
                VoIPService.this.bluetoothScoConnecting = state == 2;
                VoIPService.this.bluetoothScoActive = state == 1;
                if (VoIPService.this.bluetoothScoActive) {
                    VoIPService.this.fetchBluetoothDeviceName();
                    if (VoIPService.this.needSwitchToBluetoothAfterScoActivates) {
                        VoIPService.this.needSwitchToBluetoothAfterScoActivates = false;
                        AudioManager am2 = (AudioManager) VoIPService.this.getSystemService("audio");
                        am2.setSpeakerphoneOn(false);
                        am2.setBluetoothScoOn(true);
                    }
                }
                Iterator it = VoIPService.this.stateListeners.iterator();
                while (it.hasNext()) {
                    StateListener l = (StateListener) it.next();
                    l.onAudioSettingsChanged();
                }
            } else if ("android.intent.action.PHONE_STATE".equals(intent.getAction())) {
                if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(intent.getStringExtra(RemoteConfigConstants.ResponseFieldKey.STATE))) {
                    VoIPService.this.hangUp();
                }
            } else if ("android.intent.action.SCREEN_ON".equals(intent.getAction())) {
                for (int i = 0; i < VoIPService.this.stateListeners.size(); i++) {
                    ((StateListener) VoIPService.this.stateListeners.get(i)).onScreenOnChange(true);
                }
            } else if ("android.intent.action.SCREEN_OFF".equals(intent.getAction())) {
                for (int i2 = 0; i2 < VoIPService.this.stateListeners.size(); i2++) {
                    ((StateListener) VoIPService.this.stateListeners.get(i2)).onScreenOnChange(false);
                }
            }
        }
    };
    private final HashMap<String, TLRPC.TL_groupCallParticipant> waitingFrameParticipant = new HashMap<>();
    private final LruCache<String, ProxyVideoSink> proxyVideoSinkLruCache = new LruCache<String, ProxyVideoSink>(6) { // from class: org.telegram.messenger.voip.VoIPService.4
        public void entryRemoved(boolean evicted, String key, ProxyVideoSink oldValue, ProxyVideoSink newValue) {
            super.entryRemoved(evicted, (boolean) key, oldValue, newValue);
            VoIPService.this.tgVoip[0].removeIncomingVideoOutput(oldValue.nativeInstance);
        }
    };
    private ProxyVideoSink[] localSink = new ProxyVideoSink[2];
    private ProxyVideoSink[] remoteSink = new ProxyVideoSink[2];
    private ProxyVideoSink[] currentBackgroundSink = new ProxyVideoSink[2];
    private String[] currentBackgroundEndpointId = new String[2];
    private HashMap<String, ProxyVideoSink> remoteSinks = new HashMap<>();

    /* loaded from: classes4.dex */
    public static class SharedUIParams {
        public boolean cameraAlertWasShowed;
        public boolean tapToVideoTooltipWasShowed;
        public boolean wasVideoCall;
    }

    /* renamed from: org.telegram.messenger.voip.VoIPService$1 */
    /* loaded from: classes4.dex */
    public class AnonymousClass1 implements Runnable {
        AnonymousClass1() {
            VoIPService.this = this$0;
        }

        @Override // java.lang.Runnable
        public void run() {
            final AudioManager am = (AudioManager) VoIPService.this.getSystemService("audio");
            am.abandonAudioFocus(VoIPService.this);
            am.unregisterMediaButtonEventReceiver(new ComponentName(VoIPService.this, VoIPMediaButtonReceiver.class));
            if (!VoIPService.USE_CONNECTION_SERVICE && VoIPService.sharedInstance == null) {
                if (VoIPService.this.isBtHeadsetConnected) {
                    am.stopBluetoothSco();
                    am.setBluetoothScoOn(false);
                    VoIPService.this.bluetoothScoActive = false;
                    VoIPService.this.bluetoothScoConnecting = false;
                }
                am.setSpeakerphoneOn(false);
            }
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.AnonymousClass1.this.m1366lambda$run$0$orgtelegrammessengervoipVoIPService$1();
                }
            });
            Utilities.globalQueue.postRunnable(VoIPService.setModeRunnable = new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.AnonymousClass1.lambda$run$1(am);
                }
            });
        }

        /* renamed from: lambda$run$0$org-telegram-messenger-voip-VoIPService$1 */
        public /* synthetic */ void m1366lambda$run$0$orgtelegrammessengervoipVoIPService$1() {
            VoIPService.this.soundPool.release();
        }

        public static /* synthetic */ void lambda$run$1(AudioManager am) {
            synchronized (VoIPService.sync) {
                if (VoIPService.setModeRunnable == null) {
                    return;
                }
                Runnable unused = VoIPService.setModeRunnable = null;
                try {
                    am.setMode(0);
                } catch (SecurityException x) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("Error setting audio more to normal", x);
                    }
                }
            }
        }
    }

    public boolean isFrontFaceCamera() {
        return this.isFrontFaceCamera;
    }

    public boolean isScreencast() {
        return this.isPrivateScreencast;
    }

    public void setMicMute(boolean mute, boolean hold, boolean send) {
        TLRPC.TL_groupCallParticipant self;
        if (this.micMute == mute || this.micSwitching) {
            return;
        }
        this.micMute = mute;
        ChatObject.Call call = this.groupCall;
        if (call != null) {
            if (!send && (self = call.participants.get(getSelfId())) != null && self.muted && !self.can_self_unmute) {
                send = true;
            }
            if (send) {
                editCallMember(UserConfig.getInstance(this.currentAccount).getCurrentUser(), Boolean.valueOf(mute), null, null, null, null);
                DispatchQueue dispatchQueue = Utilities.globalQueue;
                Runnable runnable = new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda19
                    @Override // java.lang.Runnable
                    public final void run() {
                        VoIPService.this.m1337lambda$setMicMute$0$orgtelegrammessengervoipVoIPService();
                    }
                };
                this.updateNotificationRunnable = runnable;
                dispatchQueue.postRunnable(runnable);
            }
        }
        this.unmutedByHold = !this.micMute && hold;
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[0] != null) {
            nativeInstanceArr[0].setMuteMicrophone(mute);
        }
        Iterator<StateListener> it = this.stateListeners.iterator();
        while (it.hasNext()) {
            StateListener l = it.next();
            l.onAudioSettingsChanged();
        }
    }

    /* renamed from: lambda$setMicMute$0$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1337lambda$setMicMute$0$orgtelegrammessengervoipVoIPService() {
        if (this.updateNotificationRunnable == null) {
            return;
        }
        this.updateNotificationRunnable = null;
        showNotification(this.chat.title, getRoundAvatarBitmap(this.chat));
    }

    public boolean mutedByAdmin() {
        ChatObject.Call call = this.groupCall;
        if (call != null) {
            long selfId = getSelfId();
            TLRPC.TL_groupCallParticipant participant = call.participants.get(selfId);
            if (participant != null && !participant.can_self_unmute && participant.muted && !ChatObject.canManageCalls(this.chat)) {
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean hasVideoCapturer() {
        return this.captureDevice[0] != 0;
    }

    public void checkVideoFrame(TLRPC.TL_groupCallParticipant participant, boolean screencast) {
        String endpointId = screencast ? participant.presentationEndpoint : participant.videoEndpoint;
        if (endpointId == null) {
            return;
        }
        if (screencast && participant.hasPresentationFrame != 0) {
            return;
        }
        if (!screencast && participant.hasCameraFrame != 0) {
            return;
        }
        if (this.proxyVideoSinkLruCache.get(endpointId) != null || (this.remoteSinks.get(endpointId) != null && this.waitingFrameParticipant.get(endpointId) == null)) {
            if (screencast) {
                participant.hasPresentationFrame = 2;
            } else {
                participant.hasCameraFrame = 2;
            }
        } else if (this.waitingFrameParticipant.containsKey(endpointId)) {
            this.waitingFrameParticipant.put(endpointId, participant);
            if (screencast) {
                participant.hasPresentationFrame = 1;
            } else {
                participant.hasCameraFrame = 1;
            }
        } else {
            if (screencast) {
                participant.hasPresentationFrame = 1;
            } else {
                participant.hasCameraFrame = 1;
            }
            this.waitingFrameParticipant.put(endpointId, participant);
            addRemoteSink(participant, screencast, new AnonymousClass5(endpointId, screencast), null);
        }
    }

    /* renamed from: org.telegram.messenger.voip.VoIPService$5 */
    /* loaded from: classes4.dex */
    public class AnonymousClass5 implements VideoSink {
        final /* synthetic */ String val$endpointId;
        final /* synthetic */ boolean val$screencast;

        @Override // org.webrtc.VideoSink
        public /* synthetic */ void setParentSink(VideoSink videoSink) {
            VideoSink.CC.$default$setParentSink(this, videoSink);
        }

        AnonymousClass5(String str, boolean z) {
            VoIPService.this = this$0;
            this.val$endpointId = str;
            this.val$screencast = z;
        }

        @Override // org.webrtc.VideoSink
        public void onFrame(VideoFrame frame) {
            if (frame != null && frame.getBuffer().getHeight() != 0 && frame.getBuffer().getWidth() != 0) {
                final String str = this.val$endpointId;
                final boolean z = this.val$screencast;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$5$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        VoIPService.AnonymousClass5.this.m1367lambda$onFrame$0$orgtelegrammessengervoipVoIPService$5(str, this, z);
                    }
                });
            }
        }

        /* renamed from: lambda$onFrame$0$org-telegram-messenger-voip-VoIPService$5 */
        public /* synthetic */ void m1367lambda$onFrame$0$orgtelegrammessengervoipVoIPService$5(String endpointId, VideoSink thisSink, boolean screencast) {
            TLRPC.TL_groupCallParticipant currentParticipant = (TLRPC.TL_groupCallParticipant) VoIPService.this.waitingFrameParticipant.remove(endpointId);
            ProxyVideoSink proxyVideoSink = (ProxyVideoSink) VoIPService.this.remoteSinks.get(endpointId);
            if (proxyVideoSink != null && proxyVideoSink.target == thisSink) {
                VoIPService.this.proxyVideoSinkLruCache.put(endpointId, proxyVideoSink);
                VoIPService.this.remoteSinks.remove(endpointId);
                proxyVideoSink.setTarget(null);
            }
            if (currentParticipant != null) {
                if (screencast) {
                    currentParticipant.hasPresentationFrame = 2;
                } else {
                    currentParticipant.hasCameraFrame = 2;
                }
            }
            if (VoIPService.this.groupCall != null) {
                VoIPService.this.groupCall.updateVisibleParticipants();
            }
        }
    }

    public void clearRemoteSinks() {
        this.proxyVideoSinkLruCache.evictAll();
    }

    public void setAudioRoute(int route) {
        if (route == 1) {
            setAudioOutput(0);
        } else if (route == 0) {
            setAudioOutput(1);
        } else if (route == 2) {
            setAudioOutput(2);
        }
    }

    /* loaded from: classes4.dex */
    public static class ProxyVideoSink implements VideoSink {
        private VideoSink background;
        private long nativeInstance;
        private VideoSink target;

        @Override // org.webrtc.VideoSink
        public /* synthetic */ void setParentSink(VideoSink videoSink) {
            VideoSink.CC.$default$setParentSink(this, videoSink);
        }

        @Override // org.webrtc.VideoSink
        public synchronized void onFrame(VideoFrame frame) {
            VideoSink videoSink = this.target;
            if (videoSink != null) {
                videoSink.onFrame(frame);
            }
            VideoSink videoSink2 = this.background;
            if (videoSink2 != null) {
                videoSink2.onFrame(frame);
            }
        }

        public synchronized void setTarget(VideoSink newTarget) {
            VideoSink videoSink = this.target;
            if (videoSink != newTarget) {
                if (videoSink != null) {
                    videoSink.setParentSink(null);
                }
                this.target = newTarget;
                if (newTarget != null) {
                    newTarget.setParentSink(this);
                }
            }
        }

        public synchronized void setBackground(VideoSink newBackground) {
            VideoSink videoSink = this.background;
            if (videoSink != null) {
                videoSink.setParentSink(null);
            }
            this.background = newBackground;
            if (newBackground != null) {
                newBackground.setParentSink(this);
            }
        }

        public synchronized void removeTarget(VideoSink target) {
            if (this.target == target) {
                this.target = null;
            }
        }

        public synchronized void removeBackground(VideoSink background) {
            if (this.background == background) {
                this.background = null;
            }
        }

        public synchronized void swap() {
            VideoSink videoSink;
            if (this.target != null && (videoSink = this.background) != null) {
                this.target = videoSink;
                this.background = null;
            }
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean z;
        int i;
        if (sharedInstance != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Tried to start the VoIP service when it's already started");
            }
            return 2;
        }
        int intExtra = intent.getIntExtra("account", -1);
        this.currentAccount = intExtra;
        if (intExtra != -1) {
            this.classGuid = ConnectionsManager.generateClassGuid();
            long userID = intent.getLongExtra("user_id", 0L);
            long chatID = intent.getLongExtra(ChatReactionsEditActivity.KEY_CHAT_ID, 0L);
            this.createGroupCall = intent.getBooleanExtra("createGroupCall", false);
            this.hasFewPeers = intent.getBooleanExtra("hasFewPeers", false);
            this.joinHash = intent.getStringExtra("hash");
            long peerChannelId = intent.getLongExtra("peerChannelId", 0L);
            long peerChatId = intent.getLongExtra("peerChatId", 0L);
            long peerUserId = intent.getLongExtra("peerUserId", 0L);
            if (peerChatId != 0) {
                TLRPC.TL_inputPeerChat tL_inputPeerChat = new TLRPC.TL_inputPeerChat();
                this.groupCallPeer = tL_inputPeerChat;
                tL_inputPeerChat.chat_id = peerChatId;
                this.groupCallPeer.access_hash = intent.getLongExtra("peerAccessHash", 0L);
            } else if (peerChannelId != 0) {
                TLRPC.TL_inputPeerChannel tL_inputPeerChannel = new TLRPC.TL_inputPeerChannel();
                this.groupCallPeer = tL_inputPeerChannel;
                tL_inputPeerChannel.channel_id = peerChannelId;
                this.groupCallPeer.access_hash = intent.getLongExtra("peerAccessHash", 0L);
            } else if (peerUserId != 0) {
                TLRPC.TL_inputPeerUser tL_inputPeerUser = new TLRPC.TL_inputPeerUser();
                this.groupCallPeer = tL_inputPeerUser;
                tL_inputPeerUser.user_id = peerUserId;
                this.groupCallPeer.access_hash = intent.getLongExtra("peerAccessHash", 0L);
            }
            this.scheduleDate = intent.getIntExtra("scheduleDate", 0);
            this.isOutgoing = intent.getBooleanExtra("is_outgoing", false);
            this.videoCall = intent.getBooleanExtra("video_call", false);
            this.isVideoAvailable = intent.getBooleanExtra("can_video_call", false);
            this.notificationsDisabled = intent.getBooleanExtra("notifications_disabled", false);
            if (userID != 0) {
                this.user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(userID));
            }
            if (chatID != 0) {
                TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(chatID));
                this.chat = chat;
                if (ChatObject.isChannel(chat)) {
                    MessagesController.getInstance(this.currentAccount).startShortPoll(this.chat, this.classGuid, false);
                }
            }
            loadResources();
            int a = 0;
            while (true) {
                ProxyVideoSink[] proxyVideoSinkArr = this.localSink;
                if (a < proxyVideoSinkArr.length) {
                    proxyVideoSinkArr[a] = new ProxyVideoSink();
                    this.remoteSink[a] = new ProxyVideoSink();
                    a++;
                } else {
                    try {
                        break;
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            }
            AudioManager am = (AudioManager) getSystemService("audio");
            this.isHeadsetPlugged = am.isWiredHeadsetOn();
            if (this.chat != null && !this.createGroupCall) {
                ChatObject.Call call = MessagesController.getInstance(this.currentAccount).getGroupCall(this.chat.id, false);
                if (call == null) {
                    FileLog.w("VoIPService: trying to open group call without call " + this.chat.id);
                    stopSelf();
                    return 2;
                }
            }
            if (this.videoCall) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission("android.permission.CAMERA") == 0) {
                        i = 0;
                    } else {
                        i = 0;
                        this.videoState[0] = 1;
                        if (!this.isBtHeadsetConnected && !this.isHeadsetPlugged) {
                            setAudioOutput(i);
                        }
                    }
                } else {
                    i = 0;
                }
                this.captureDevice[i] = NativeInstance.createVideoCapturer(this.localSink[i], this.isFrontFaceCamera ? 1 : 0);
                if (chatID != 0) {
                    this.videoState[i] = 1;
                } else {
                    this.videoState[i] = 2;
                }
                if (!this.isBtHeadsetConnected) {
                    setAudioOutput(i);
                }
            }
            if (this.user == null && this.chat == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.w("VoIPService: user == null AND chat == null");
                }
                stopSelf();
                return 2;
            }
            sharedInstance = this;
            synchronized (sync) {
                if (setModeRunnable != null) {
                    Utilities.globalQueue.cancelRunnable(setModeRunnable);
                    setModeRunnable = null;
                }
            }
            if (this.isOutgoing) {
                if (this.user != null) {
                    dispatchStateChanged(14);
                    if (USE_CONNECTION_SERVICE) {
                        TelecomManager tm = (TelecomManager) getSystemService("telecom");
                        Bundle extras = new Bundle();
                        Bundle myExtras = new Bundle();
                        extras.putParcelable("android.telecom.extra.PHONE_ACCOUNT_HANDLE", addAccountToTelecomManager());
                        myExtras.putInt("call_type", 1);
                        extras.putBundle("android.telecom.extra.OUTGOING_CALL_EXTRAS", myExtras);
                        ContactsController.getInstance(this.currentAccount).createOrUpdateConnectionServiceContact(this.user.id, this.user.first_name, this.user.last_name);
                        tm.placeCall(Uri.fromParts("tel", "+99084" + this.user.id, null), extras);
                        z = false;
                    } else {
                        Runnable runnable = new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda14
                            @Override // java.lang.Runnable
                            public final void run() {
                                VoIPService.this.m1330lambda$onStartCommand$1$orgtelegrammessengervoipVoIPService();
                            }
                        };
                        this.delayedStartOutgoingCall = runnable;
                        AndroidUtilities.runOnUIThread(runnable, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                        z = false;
                    }
                } else {
                    this.micMute = true;
                    z = false;
                    startGroupCall(0, null, false);
                    if (!this.isBtHeadsetConnected && !this.isHeadsetPlugged) {
                        setAudioOutput(0);
                    }
                }
                if (intent.getBooleanExtra("start_incall_activity", z)) {
                    Intent intent1 = new Intent(this, LaunchActivity.class).setAction(this.user != null ? "voip" : "voip_chat").addFlags(268435456);
                    if (this.chat != null) {
                        intent1.putExtra("currentAccount", this.currentAccount);
                    }
                    startActivity(intent1);
                }
            } else {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeInCallActivity, new Object[0]);
                TLRPC.PhoneCall phoneCall = callIShouldHavePutIntoIntent;
                this.privateCall = phoneCall;
                boolean z2 = phoneCall != null && phoneCall.video;
                this.videoCall = z2;
                if (z2) {
                    this.isVideoAvailable = true;
                }
                if (z2 && !this.isBtHeadsetConnected && !this.isHeadsetPlugged) {
                    setAudioOutput(0);
                }
                callIShouldHavePutIntoIntent = null;
                if (USE_CONNECTION_SERVICE) {
                    acknowledgeCall(false);
                    showNotification();
                } else {
                    acknowledgeCall(true);
                }
            }
            initializeAccountRelatedThings();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.m1331lambda$onStartCommand$2$orgtelegrammessengervoipVoIPService();
                }
            });
            return 2;
        }
        throw new IllegalStateException("No account specified when starting VoIP service");
    }

    /* renamed from: lambda$onStartCommand$1$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1330lambda$onStartCommand$1$orgtelegrammessengervoipVoIPService() {
        this.delayedStartOutgoingCall = null;
        startOutgoingCall();
    }

    /* renamed from: lambda$onStartCommand$2$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1331lambda$onStartCommand$2$orgtelegrammessengervoipVoIPService() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.voipServiceCreated, new Object[0]);
    }

    public static boolean hasRtmpStream() {
        return (getSharedInstance() == null || getSharedInstance().groupCall == null || !getSharedInstance().groupCall.call.rtmp_stream) ? false : true;
    }

    public static VoIPService getSharedInstance() {
        return sharedInstance;
    }

    public TLRPC.User getUser() {
        return this.user;
    }

    public TLRPC.Chat getChat() {
        return this.chat;
    }

    public void setNoiseSupressionEnabled(boolean enabled) {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[0] == null) {
            return;
        }
        nativeInstanceArr[0].setNoiseSuppressionEnabled(enabled);
    }

    public void setGroupCallHash(String hash) {
        if (!this.currentGroupModeStreaming || TextUtils.isEmpty(hash) || hash.equals(this.joinHash)) {
            return;
        }
        this.joinHash = hash;
        createGroupInstance(0, false);
    }

    public long getCallerId() {
        TLRPC.User user = this.user;
        if (user != null) {
            return user.id;
        }
        return -this.chat.id;
    }

    public void hangUp(int discard, Runnable onDone) {
        int i = this.currentState;
        declineIncomingCall((i == 16 || (i == 13 && this.isOutgoing)) ? 3 : 1, onDone);
        if (this.groupCall == null || discard == 2) {
            return;
        }
        if (discard == 1) {
            TLRPC.ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(this.chat.id);
            if (chatFull != null) {
                chatFull.flags &= -2097153;
                chatFull.call = null;
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chat.id), Long.valueOf(this.groupCall.call.id), false);
            }
            TLRPC.TL_phone_discardGroupCall req = new TLRPC.TL_phone_discardGroupCall();
            req.call = this.groupCall.getInputGroupCall();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda78
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    VoIPService.this.m1315lambda$hangUp$3$orgtelegrammessengervoipVoIPService(tLObject, tL_error);
                }
            });
            return;
        }
        TLRPC.TL_phone_leaveGroupCall req2 = new TLRPC.TL_phone_leaveGroupCall();
        req2.call = this.groupCall.getInputGroupCall();
        req2.source = this.mySource[0];
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda79
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                VoIPService.this.m1316lambda$hangUp$4$orgtelegrammessengervoipVoIPService(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$hangUp$3$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1315lambda$hangUp$3$orgtelegrammessengervoipVoIPService(TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_updates) {
            TLRPC.TL_updates updates = (TLRPC.TL_updates) response;
            MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
        }
    }

    /* renamed from: lambda$hangUp$4$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1316lambda$hangUp$4$orgtelegrammessengervoipVoIPService(TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_updates) {
            TLRPC.TL_updates updates = (TLRPC.TL_updates) response;
            MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
        }
    }

    private void startOutgoingCall() {
        CallConnection callConnection;
        if (USE_CONNECTION_SERVICE && (callConnection = this.systemCallConnection) != null) {
            callConnection.setDialing();
        }
        configureDeviceForCall();
        showNotification();
        startConnectingSound();
        dispatchStateChanged(14);
        AndroidUtilities.runOnUIThread(VoIPService$$ExternalSyntheticLambda59.INSTANCE);
        byte[] salt = new byte[256];
        Utilities.random.nextBytes(salt);
        TLRPC.TL_messages_getDhConfig req = new TLRPC.TL_messages_getDhConfig();
        req.random_length = 256;
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        req.version = messagesStorage.getLastSecretVersion();
        this.callReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda89
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                VoIPService.this.m1351x6e6635f5(messagesStorage, tLObject, tL_error);
            }
        }, 2);
    }

    /* renamed from: lambda$startOutgoingCall$10$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1351x6e6635f5(MessagesStorage messagesStorage, TLObject response, TLRPC.TL_error error) {
        this.callReqId = 0;
        if (this.endCallAfterRequest) {
            callEnded();
        } else if (error == null) {
            TLRPC.messages_DhConfig res = (TLRPC.messages_DhConfig) response;
            if (response instanceof TLRPC.TL_messages_dhConfig) {
                if (!Utilities.isGoodPrime(res.p, res.g)) {
                    callFailed();
                    return;
                }
                messagesStorage.setSecretPBytes(res.p);
                messagesStorage.setSecretG(res.g);
                messagesStorage.setLastSecretVersion(res.version);
                messagesStorage.saveSecretParams(messagesStorage.getLastSecretVersion(), messagesStorage.getSecretG(), messagesStorage.getSecretPBytes());
            }
            final byte[] salt1 = new byte[256];
            for (int a = 0; a < 256; a++) {
                salt1[a] = (byte) (((byte) (Utilities.random.nextDouble() * 256.0d)) ^ res.random[a]);
            }
            int a2 = messagesStorage.getSecretG();
            BigInteger i_g_a = BigInteger.valueOf(a2);
            byte[] g_a = i_g_a.modPow(new BigInteger(1, salt1), new BigInteger(1, messagesStorage.getSecretPBytes())).toByteArray();
            if (g_a.length > 256) {
                byte[] correctedAuth = new byte[256];
                System.arraycopy(g_a, 1, correctedAuth, 0, 256);
                g_a = correctedAuth;
            }
            TLRPC.TL_phone_requestCall reqCall = new TLRPC.TL_phone_requestCall();
            reqCall.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(this.user);
            reqCall.protocol = new TLRPC.TL_phoneCallProtocol();
            reqCall.video = this.videoCall;
            reqCall.protocol.udp_p2p = true;
            reqCall.protocol.udp_reflector = true;
            reqCall.protocol.min_layer = 65;
            reqCall.protocol.max_layer = Instance.getConnectionMaxLayer();
            reqCall.protocol.library_versions.addAll(Instance.AVAILABLE_VERSIONS);
            this.g_a = g_a;
            reqCall.g_a_hash = Utilities.computeSHA256(g_a, 0, g_a.length);
            reqCall.random_id = Utilities.random.nextInt();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(reqCall, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda92
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    VoIPService.this.m1355xad14a785(salt1, tLObject, tL_error);
                }
            }, 2);
        } else {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Error on getDhConfig " + error);
            }
            callFailed();
        }
    }

    /* renamed from: lambda$startOutgoingCall$9$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1355xad14a785(final byte[] salt1, final TLObject response12, final TLRPC.TL_error error12) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda49
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1354x20747c84(error12, response12, salt1);
            }
        });
    }

    /* renamed from: lambda$startOutgoingCall$8$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1354x20747c84(TLRPC.TL_error error12, TLObject response12, byte[] salt1) {
        if (error12 == null) {
            this.privateCall = ((TLRPC.TL_phone_phoneCall) response12).phone_call;
            this.a_or_b = salt1;
            dispatchStateChanged(13);
            if (this.endCallAfterRequest) {
                hangUp();
                return;
            }
            if (this.pendingUpdates.size() > 0 && this.privateCall != null) {
                Iterator<TLRPC.PhoneCall> it = this.pendingUpdates.iterator();
                while (it.hasNext()) {
                    TLRPC.PhoneCall call = it.next();
                    onCallUpdated(call);
                }
                this.pendingUpdates.clear();
            }
            Runnable runnable = new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda24
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.m1353x93d45183();
                }
            };
            this.timeoutRunnable = runnable;
            AndroidUtilities.runOnUIThread(runnable, MessagesController.getInstance(this.currentAccount).callReceiveTimeout);
        } else if (error12.code == 400 && "PARTICIPANT_VERSION_OUTDATED".equals(error12.text)) {
            callFailed(Instance.ERROR_PEER_OUTDATED);
        } else if (error12.code == 403) {
            callFailed(Instance.ERROR_PRIVACY);
        } else if (error12.code == 406) {
            callFailed(Instance.ERROR_LOCALIZED);
        } else {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Error on phone.requestCall: " + error12);
            }
            callFailed();
        }
    }

    /* renamed from: lambda$startOutgoingCall$7$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1353x93d45183() {
        this.timeoutRunnable = null;
        TLRPC.TL_phone_discardCall req1 = new TLRPC.TL_phone_discardCall();
        req1.peer = new TLRPC.TL_inputPhoneCall();
        req1.peer.access_hash = this.privateCall.access_hash;
        req1.peer.id = this.privateCall.id;
        req1.reason = new TLRPC.TL_phoneCallDiscardReasonMissed();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req1, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda82
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                VoIPService.this.m1352x7342682(tLObject, tL_error);
            }
        }, 2);
    }

    /* renamed from: lambda$startOutgoingCall$6$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1352x7342682(TLObject response1, TLRPC.TL_error error1) {
        if (BuildVars.LOGS_ENABLED) {
            if (error1 != null) {
                FileLog.e("error on phone.discardCall: " + error1);
            } else {
                FileLog.d("phone.discardCall " + response1);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda27
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.callFailed();
            }
        });
    }

    private void acknowledgeCall(final boolean startRinging) {
        if (this.privateCall instanceof TLRPC.TL_phoneCallDiscarded) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("Call " + this.privateCall.id + " was discarded before the service started, stopping");
            }
            stopSelf();
        } else if (Build.VERSION.SDK_INT >= 19 && XiaomiUtilities.isMIUI() && !XiaomiUtilities.isCustomPermissionGranted(XiaomiUtilities.OP_SHOW_WHEN_LOCKED) && ((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode()) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("MIUI: no permission to show when locked but the screen is locked. ¯\\_(ツ)_/¯");
            }
            stopSelf();
        } else {
            TLRPC.TL_phone_receivedCall req = new TLRPC.TL_phone_receivedCall();
            req.peer = new TLRPC.TL_inputPhoneCall();
            req.peer.id = this.privateCall.id;
            req.peer.access_hash = this.privateCall.access_hash;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda91
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    VoIPService.this.m1287xcd5054c9(startRinging, tLObject, tL_error);
                }
            }, 2);
        }
    }

    /* renamed from: lambda$acknowledgeCall$12$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1287xcd5054c9(final boolean startRinging, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda41
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1286x40b029c8(response, error, startRinging);
            }
        });
    }

    /* renamed from: lambda$acknowledgeCall$11$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1286x40b029c8(TLObject response, TLRPC.TL_error error, boolean startRinging) {
        if (sharedInstance == null) {
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.w("receivedCall response = " + response);
        }
        if (error != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("error on receivedCall: " + error);
            }
            stopSelf();
            return;
        }
        if (USE_CONNECTION_SERVICE) {
            ContactsController.getInstance(this.currentAccount).createOrUpdateConnectionServiceContact(this.user.id, this.user.first_name, this.user.last_name);
            TelecomManager tm = (TelecomManager) getSystemService("telecom");
            Bundle extras = new Bundle();
            extras.putInt("call_type", 1);
            tm.addNewIncomingCall(addAccountToTelecomManager(), extras);
        }
        if (startRinging) {
            startRinging();
        }
    }

    private boolean isRinging() {
        return this.currentState == 15;
    }

    public boolean isJoined() {
        int i = this.currentState;
        return (i == 1 || i == 6) ? false : true;
    }

    public void requestVideoCall(boolean screencast) {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        int i = 0;
        if (nativeInstanceArr[0] == null) {
            return;
        }
        if (!screencast) {
            long[] jArr = this.captureDevice;
            if (jArr[0] != 0) {
                nativeInstanceArr[0].setupOutgoingVideoCreated(jArr[0]);
                this.destroyCaptureDevice[0] = false;
                this.isPrivateScreencast = screencast;
            }
        }
        NativeInstance nativeInstance = nativeInstanceArr[0];
        ProxyVideoSink proxyVideoSink = this.localSink[0];
        if (screencast) {
            i = 2;
        } else if (this.isFrontFaceCamera) {
            i = 1;
        }
        nativeInstance.setupOutgoingVideo(proxyVideoSink, i);
        this.isPrivateScreencast = screencast;
    }

    public void switchCamera() {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[0] == null || !nativeInstanceArr[0].hasVideoCapturer() || this.switchingCamera) {
            long[] jArr = this.captureDevice;
            if (jArr[0] != 0 && !this.switchingCamera) {
                NativeInstance.switchCameraCapturer(jArr[0], !this.isFrontFaceCamera);
                return;
            }
            return;
        }
        this.switchingCamera = true;
        this.tgVoip[0].switchCamera(!this.isFrontFaceCamera);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void createCaptureDevice(boolean screencast) {
        int deviceType;
        if (screencast != 0) {
            deviceType = 2;
        } else {
            deviceType = this.isFrontFaceCamera;
        }
        if (this.groupCall == null) {
            if (!this.isPrivateScreencast && screencast != 0) {
                setVideoState(false, 0);
            }
            this.isPrivateScreencast = screencast;
            NativeInstance[] nativeInstanceArr = this.tgVoip;
            if (nativeInstanceArr[0] != null) {
                nativeInstanceArr[0].clearVideoCapturer();
            }
        }
        if (screencast == 1) {
            if (this.groupCall != null) {
                long[] jArr = this.captureDevice;
                if (jArr[screencast] == 0) {
                    jArr[screencast] = NativeInstance.createVideoCapturer(this.localSink[screencast], deviceType);
                    createGroupInstance(1, false);
                    setVideoState(true, 2);
                    AccountInstance.getInstance(this.currentAccount).getNotificationCenter().postNotificationName(NotificationCenter.groupCallScreencastStateChanged, new Object[0]);
                    return;
                }
                return;
            }
            requestVideoCall(true);
            setVideoState(true, 2);
            if (VoIPFragment.getInstance() != null) {
                VoIPFragment.getInstance().onScreenCastStart();
                return;
            }
            return;
        }
        long[] jArr2 = this.captureDevice;
        if (jArr2[screencast ? 1 : 0] != 0 || this.tgVoip[screencast] == null) {
            NativeInstance[] nativeInstanceArr2 = this.tgVoip;
            if (nativeInstanceArr2[screencast] != null && jArr2[screencast] != 0) {
                nativeInstanceArr2[screencast].activateVideoCapturer(jArr2[screencast]);
            }
            if (this.captureDevice[screencast] != 0) {
                return;
            }
        }
        this.captureDevice[screencast] = NativeInstance.createVideoCapturer(this.localSink[screencast], deviceType);
    }

    public void setupCaptureDevice(boolean screencast, boolean micEnabled) {
        boolean z = false;
        if (screencast == 0) {
            long[] jArr = this.captureDevice;
            if (jArr[screencast ? 1 : 0] != 0) {
                NativeInstance[] nativeInstanceArr = this.tgVoip;
                if (nativeInstanceArr[screencast] != null) {
                    nativeInstanceArr[screencast].setupOutgoingVideoCreated(jArr[screencast]);
                    this.destroyCaptureDevice[screencast] = false;
                    this.videoState[screencast] = 2;
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        if (this.micMute == micEnabled) {
            setMicMute(!micEnabled, false, false);
            this.micSwitching = true;
        }
        if (this.groupCall != null) {
            TLRPC.User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
            Boolean valueOf = Boolean.valueOf(!micEnabled);
            if (this.videoState[0] != 2) {
                z = true;
            }
            editCallMember(currentUser, valueOf, Boolean.valueOf(z), null, null, new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda20
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.m1338xbfa2c4eb();
                }
            });
        }
    }

    /* renamed from: lambda$setupCaptureDevice$13$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1338xbfa2c4eb() {
        this.micSwitching = false;
    }

    public void clearCamera() {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[0] != null) {
            nativeInstanceArr[0].clearVideoCapturer();
        }
    }

    public void setVideoState(boolean screencast, int state) {
        char c;
        int i;
        boolean z = false;
        if (this.groupCall != null) {
            c = screencast ? 1 : 0;
        } else {
            c = 0;
        }
        int trueIndex = c;
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[trueIndex] == null) {
            long[] jArr = this.captureDevice;
            if (jArr[screencast] != 0) {
                int[] iArr = this.videoState;
                iArr[trueIndex] = state;
                NativeInstance.setVideoStateCapturer(jArr[screencast], iArr[trueIndex]);
                return;
            } else if (state == 2 && (i = this.currentState) != 17 && i != 11) {
                jArr[screencast] = NativeInstance.createVideoCapturer(this.localSink[trueIndex], this.isFrontFaceCamera ? 1 : 0);
                this.videoState[trueIndex] = 2;
                return;
            } else {
                return;
            }
        }
        int[] iArr2 = this.videoState;
        iArr2[trueIndex] = state;
        nativeInstanceArr[trueIndex].setVideoState(iArr2[trueIndex]);
        long[] jArr2 = this.captureDevice;
        if (jArr2[screencast ? 1 : 0] != 0) {
            NativeInstance.setVideoStateCapturer(jArr2[screencast], this.videoState[trueIndex]);
        }
        if (screencast == 0) {
            if (this.groupCall != null) {
                TLRPC.User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                if (this.videoState[0] != 2) {
                    z = true;
                }
                editCallMember(currentUser, null, Boolean.valueOf(z), null, null, null);
            }
            checkIsNear();
        }
    }

    public void stopScreenCapture() {
        if (this.groupCall == null || this.videoState[1] != 2) {
            return;
        }
        TLRPC.TL_phone_leaveGroupCallPresentation req = new TLRPC.TL_phone_leaveGroupCallPresentation();
        req.call = this.groupCall.getInputGroupCall();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda83
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                VoIPService.this.m1361x5d471f8d(tLObject, tL_error);
            }
        });
        NativeInstance instance = this.tgVoip[1];
        if (instance != null) {
            DispatchQueue dispatchQueue = Utilities.globalQueue;
            instance.getClass();
            dispatchQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda44(instance));
        }
        this.mySource[1] = 0;
        this.tgVoip[1] = null;
        this.destroyCaptureDevice[1] = true;
        this.captureDevice[1] = 0;
        this.videoState[1] = 0;
        AccountInstance.getInstance(this.currentAccount).getNotificationCenter().postNotificationName(NotificationCenter.groupCallScreencastStateChanged, new Object[0]);
    }

    /* renamed from: lambda$stopScreenCapture$14$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1361x5d471f8d(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.Updates updates = (TLRPC.Updates) response;
            MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
        }
    }

    public int getVideoState(boolean screencast) {
        return this.videoState[screencast ? 1 : 0];
    }

    public void setSinks(VideoSink local, VideoSink remote) {
        setSinks(local, false, remote);
    }

    public void setSinks(VideoSink local, boolean screencast, VideoSink remote) {
        this.localSink[screencast ? 1 : 0].setTarget(local);
        this.remoteSink[screencast].setTarget(remote);
    }

    public void setLocalSink(VideoSink local, boolean screencast) {
        if (!screencast) {
            this.localSink[0].setTarget(local);
        }
    }

    public void setRemoteSink(VideoSink remote, boolean screencast) {
        this.remoteSink[screencast ? 1 : 0].setTarget(remote);
    }

    public ProxyVideoSink addRemoteSink(TLRPC.TL_groupCallParticipant participant, boolean screencast, VideoSink remote, VideoSink background) {
        if (this.tgVoip[0] == null) {
            return null;
        }
        String endpointId = screencast ? participant.presentationEndpoint : participant.videoEndpoint;
        if (endpointId == null) {
            return null;
        }
        ProxyVideoSink sink = this.remoteSinks.get(endpointId);
        if (sink != null && sink.target == remote) {
            return sink;
        }
        if (sink == null) {
            sink = this.proxyVideoSinkLruCache.remove(endpointId);
        }
        if (sink == null) {
            sink = new ProxyVideoSink();
        }
        if (remote != null) {
            sink.setTarget(remote);
        }
        if (background != null) {
            sink.setBackground(background);
        }
        this.remoteSinks.put(endpointId, sink);
        sink.nativeInstance = this.tgVoip[0].addIncomingVideoOutput(1, endpointId, createSsrcGroups(screencast ? participant.presentation : participant.video), sink);
        return sink;
    }

    private NativeInstance.SsrcGroup[] createSsrcGroups(TLRPC.TL_groupCallParticipantVideo video) {
        if (video.source_groups.isEmpty()) {
            return null;
        }
        NativeInstance.SsrcGroup[] result = new NativeInstance.SsrcGroup[video.source_groups.size()];
        for (int a = 0; a < result.length; a++) {
            result[a] = new NativeInstance.SsrcGroup();
            TLRPC.TL_groupCallParticipantVideoSourceGroup group = video.source_groups.get(a);
            result[a].semantics = group.semantics;
            result[a].ssrcs = new int[group.sources.size()];
            for (int b = 0; b < result[a].ssrcs.length; b++) {
                result[a].ssrcs[b] = group.sources.get(b).intValue();
            }
        }
        return result;
    }

    public void requestFullScreen(TLRPC.TL_groupCallParticipant participant, boolean full, boolean screencast) {
        String endpointId = screencast ? participant.presentationEndpoint : participant.videoEndpoint;
        if (endpointId == null) {
            return;
        }
        if (full) {
            this.tgVoip[0].setVideoEndpointQuality(endpointId, 2);
        } else {
            this.tgVoip[0].setVideoEndpointQuality(endpointId, 1);
        }
    }

    public void removeRemoteSink(TLRPC.TL_groupCallParticipant participant, boolean presentation) {
        if (presentation) {
            ProxyVideoSink sink = this.remoteSinks.remove(participant.presentationEndpoint);
            if (sink != null) {
                this.tgVoip[0].removeIncomingVideoOutput(sink.nativeInstance);
                return;
            }
            return;
        }
        ProxyVideoSink sink2 = this.remoteSinks.remove(participant.videoEndpoint);
        if (sink2 != null) {
            this.tgVoip[0].removeIncomingVideoOutput(sink2.nativeInstance);
        }
    }

    public boolean isFullscreen(TLRPC.TL_groupCallParticipant participant, boolean screencast) {
        if (this.currentBackgroundSink[screencast ? 1 : 0] != null) {
            if (TextUtils.equals(this.currentBackgroundEndpointId[screencast], screencast != 0 ? participant.presentationEndpoint : participant.videoEndpoint)) {
                return true;
            }
        }
        return false;
    }

    public void setBackgroundSinks(VideoSink local, VideoSink remote) {
        this.localSink[0].setBackground(local);
        this.remoteSink[0].setBackground(remote);
    }

    public void swapSinks() {
        this.localSink[0].swap();
        this.remoteSink[0].swap();
    }

    public boolean isHangingUp() {
        return this.currentState == 10;
    }

    public void onSignalingData(TLRPC.TL_updatePhoneCallSignalingData data) {
        if (this.user != null) {
            NativeInstance[] nativeInstanceArr = this.tgVoip;
            if (nativeInstanceArr[0] == null || nativeInstanceArr[0].isGroup() || getCallID() != data.phone_call_id) {
                return;
            }
            this.tgVoip[0].onSignalingDataReceive(data.data);
        }
    }

    public long getSelfId() {
        TLRPC.InputPeer inputPeer = this.groupCallPeer;
        if (inputPeer == null) {
            return UserConfig.getInstance(this.currentAccount).clientUserId;
        }
        if (inputPeer instanceof TLRPC.TL_inputPeerUser) {
            return inputPeer.user_id;
        }
        if (inputPeer instanceof TLRPC.TL_inputPeerChannel) {
            return -inputPeer.channel_id;
        }
        return -inputPeer.chat_id;
    }

    public void onGroupCallParticipantsUpdate(TLRPC.TL_updateGroupCallParticipants update) {
        ChatObject.Call call;
        if (this.chat == null || (call = this.groupCall) == null || call.call.id != update.call.id) {
            return;
        }
        long selfId = getSelfId();
        int N = update.participants.size();
        for (int a = 0; a < N; a++) {
            TLRPC.TL_groupCallParticipant participant = update.participants.get(a);
            if (participant.left) {
                if (participant.source != 0 && participant.source == this.mySource[0]) {
                    int selfCount = 0;
                    for (int b = 0; b < N; b++) {
                        TLRPC.TL_groupCallParticipant p = update.participants.get(b);
                        if (p.self || p.source == this.mySource[0]) {
                            selfCount++;
                        }
                    }
                    if (selfCount > 1) {
                        hangUp(2);
                        return;
                    }
                }
            } else if (MessageObject.getPeerId(participant.peer) == selfId) {
                int i = participant.source;
                int[] iArr = this.mySource;
                if (i != iArr[0] && iArr[0] != 0 && participant.source != 0) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("source mismatch my = " + this.mySource[0] + " psrc = " + participant.source);
                    }
                    hangUp(2);
                    return;
                }
                if (ChatObject.isChannel(this.chat) && this.currentGroupModeStreaming && participant.can_self_unmute) {
                    this.switchingStream = true;
                    createGroupInstance(0, false);
                }
                if (participant.muted) {
                    setMicMute(true, false, false);
                }
            } else {
                continue;
            }
        }
    }

    public void onGroupCallUpdated(TLRPC.GroupCall call) {
        ChatObject.Call call2;
        TLRPC.TL_dataJSON tL_dataJSON;
        if (this.chat == null || (call2 = this.groupCall) == null || call2.call.id != call.id) {
            return;
        }
        if (this.groupCall.call instanceof TLRPC.TL_groupCallDiscarded) {
            hangUp(2);
            return;
        }
        boolean newModeStreaming = false;
        if (this.myParams != null) {
            try {
                JSONObject object = new JSONObject(this.myParams.data);
                newModeStreaming = object.optBoolean("stream");
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        boolean z = true;
        if ((this.currentState == 1 || newModeStreaming != this.currentGroupModeStreaming) && (tL_dataJSON = this.myParams) != null) {
            if (this.playedConnectedSound && newModeStreaming != this.currentGroupModeStreaming) {
                this.switchingStream = true;
            }
            this.currentGroupModeStreaming = newModeStreaming;
            try {
                if (newModeStreaming) {
                    NativeInstance nativeInstance = this.tgVoip[0];
                    if (this.groupCall.call == null || !this.groupCall.call.rtmp_stream) {
                        z = false;
                    }
                    nativeInstance.prepareForStream(z);
                } else {
                    this.tgVoip[0].setJoinResponsePayload(tL_dataJSON.data);
                }
                dispatchStateChanged(2);
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
    }

    public void onCallUpdated(TLRPC.PhoneCall phoneCall) {
        if (this.user == null) {
            return;
        }
        if (this.privateCall == null) {
            this.pendingUpdates.add(phoneCall);
        } else if (phoneCall != null) {
            if (phoneCall.id != this.privateCall.id) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.w("onCallUpdated called with wrong call id (got " + phoneCall.id + ", expected " + this.privateCall.id + ")");
                    return;
                }
                return;
            }
            if (phoneCall.access_hash == 0) {
                phoneCall.access_hash = this.privateCall.access_hash;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("Call updated: " + phoneCall);
            }
            this.privateCall = phoneCall;
            if (phoneCall instanceof TLRPC.TL_phoneCallDiscarded) {
                this.needSendDebugLog = phoneCall.need_debug;
                this.needRateCall = phoneCall.need_rating;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("call discarded, stopping service");
                }
                if (phoneCall.reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy) {
                    dispatchStateChanged(17);
                    this.playingSound = true;
                    Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda7
                        @Override // java.lang.Runnable
                        public final void run() {
                            VoIPService.this.m1322lambda$onCallUpdated$15$orgtelegrammessengervoipVoIPService();
                        }
                    });
                    AndroidUtilities.runOnUIThread(this.afterSoundRunnable, 1500L);
                    endConnectionServiceCall(1500L);
                    stopSelf();
                    return;
                }
                callEnded();
            } else if (!(phoneCall instanceof TLRPC.TL_phoneCall) || this.authKey != null) {
                if ((phoneCall instanceof TLRPC.TL_phoneCallAccepted) && this.authKey == null) {
                    processAcceptedCall();
                } else if (this.currentState == 13 && phoneCall.receive_date != 0) {
                    dispatchStateChanged(16);
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("!!!!!! CALL RECEIVED");
                    }
                    Runnable runnable = this.connectingSoundRunnable;
                    if (runnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable);
                        this.connectingSoundRunnable = null;
                    }
                    Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda8
                        @Override // java.lang.Runnable
                        public final void run() {
                            VoIPService.this.m1323lambda$onCallUpdated$16$orgtelegrammessengervoipVoIPService();
                        }
                    });
                    Runnable runnable2 = this.timeoutRunnable;
                    if (runnable2 != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable2);
                        this.timeoutRunnable = null;
                    }
                    Runnable runnable3 = new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda9
                        @Override // java.lang.Runnable
                        public final void run() {
                            VoIPService.this.m1324lambda$onCallUpdated$17$orgtelegrammessengervoipVoIPService();
                        }
                    };
                    this.timeoutRunnable = runnable3;
                    AndroidUtilities.runOnUIThread(runnable3, MessagesController.getInstance(this.currentAccount).callRingTimeout);
                }
            } else if (phoneCall.g_a_or_b == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.w("stopping VoIP service, Ga == null");
                }
                callFailed();
            } else if (!Arrays.equals(this.g_a_hash, Utilities.computeSHA256(phoneCall.g_a_or_b, 0, phoneCall.g_a_or_b.length))) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.w("stopping VoIP service, Ga hash doesn't match");
                }
                callFailed();
            } else {
                this.g_a = phoneCall.g_a_or_b;
                BigInteger g_a = new BigInteger(1, phoneCall.g_a_or_b);
                BigInteger p = new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
                if (!Utilities.isGoodGaAndGb(g_a, p)) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.w("stopping VoIP service, bad Ga and Gb (accepting)");
                    }
                    callFailed();
                    return;
                }
                byte[] authKey = g_a.modPow(new BigInteger(1, this.a_or_b), p).toByteArray();
                if (authKey.length > 256) {
                    byte[] correctedAuth = new byte[256];
                    System.arraycopy(authKey, authKey.length - 256, correctedAuth, 0, 256);
                    authKey = correctedAuth;
                } else if (authKey.length < 256) {
                    byte[] correctedAuth2 = new byte[256];
                    System.arraycopy(authKey, 0, correctedAuth2, 256 - authKey.length, authKey.length);
                    for (int a = 0; a < 256 - authKey.length; a++) {
                        correctedAuth2[a] = 0;
                    }
                    authKey = correctedAuth2;
                }
                byte[] authKeyHash = Utilities.computeSHA1(authKey);
                byte[] authKeyId = new byte[8];
                System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
                this.authKey = authKey;
                long bytesToLong = Utilities.bytesToLong(authKeyId);
                this.keyFingerprint = bytesToLong;
                if (bytesToLong != phoneCall.key_fingerprint) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.w("key fingerprints don't match");
                    }
                    callFailed();
                    return;
                }
                initiateActualEncryptedCall();
            }
        }
    }

    /* renamed from: lambda$onCallUpdated$15$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1322lambda$onCallUpdated$15$orgtelegrammessengervoipVoIPService() {
        this.soundPool.play(this.spBusyId, 1.0f, 1.0f, 0, -1, 1.0f);
    }

    /* renamed from: lambda$onCallUpdated$16$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1323lambda$onCallUpdated$16$orgtelegrammessengervoipVoIPService() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
        }
        this.spPlayId = this.soundPool.play(this.spRingbackID, 1.0f, 1.0f, 0, -1, 1.0f);
    }

    /* renamed from: lambda$onCallUpdated$17$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1324lambda$onCallUpdated$17$orgtelegrammessengervoipVoIPService() {
        this.timeoutRunnable = null;
        declineIncomingCall(3, null);
    }

    private void startRatingActivity() {
        try {
            PendingIntent.getActivity(this, 0, new Intent(this, VoIPFeedbackActivity.class).putExtra("call_id", this.privateCall.id).putExtra("call_access_hash", this.privateCall.access_hash).putExtra("call_video", this.privateCall.video).putExtra("account", this.currentAccount).addFlags(C.ENCODING_PCM_32BIT), 0).send();
        } catch (Exception x) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Error starting incall activity", x);
            }
        }
    }

    public byte[] getEncryptionKey() {
        return this.authKey;
    }

    private void processAcceptedCall() {
        dispatchStateChanged(12);
        BigInteger p = new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
        BigInteger i_authKey = new BigInteger(1, this.privateCall.g_b);
        if (!Utilities.isGoodGaAndGb(i_authKey, p)) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("stopping VoIP service, bad Ga and Gb");
            }
            callFailed();
            return;
        }
        byte[] authKey = i_authKey.modPow(new BigInteger(1, this.a_or_b), p).toByteArray();
        if (authKey.length > 256) {
            byte[] correctedAuth = new byte[256];
            System.arraycopy(authKey, authKey.length - 256, correctedAuth, 0, 256);
            authKey = correctedAuth;
        } else if (authKey.length < 256) {
            byte[] correctedAuth2 = new byte[256];
            System.arraycopy(authKey, 0, correctedAuth2, 256 - authKey.length, authKey.length);
            for (int a = 0; a < 256 - authKey.length; a++) {
                correctedAuth2[a] = 0;
            }
            authKey = correctedAuth2;
        }
        byte[] authKeyHash = Utilities.computeSHA1(authKey);
        byte[] authKeyId = new byte[8];
        System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
        long fingerprint = Utilities.bytesToLong(authKeyId);
        this.authKey = authKey;
        this.keyFingerprint = fingerprint;
        TLRPC.TL_phone_confirmCall req = new TLRPC.TL_phone_confirmCall();
        req.g_a = this.g_a;
        req.key_fingerprint = fingerprint;
        req.peer = new TLRPC.TL_inputPhoneCall();
        req.peer.id = this.privateCall.id;
        req.peer.access_hash = this.privateCall.access_hash;
        req.protocol = new TLRPC.TL_phoneCallProtocol();
        req.protocol.max_layer = Instance.getConnectionMaxLayer();
        req.protocol.min_layer = 65;
        TLRPC.TL_phoneCallProtocol tL_phoneCallProtocol = req.protocol;
        req.protocol.udp_reflector = true;
        tL_phoneCallProtocol.udp_p2p = true;
        req.protocol.library_versions.addAll(Instance.AVAILABLE_VERSIONS);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda80
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                VoIPService.this.m1336x66fcbb76(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$processAcceptedCall$19$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1336x66fcbb76(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda48
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1335xda5c9075(error, response);
            }
        });
    }

    /* renamed from: lambda$processAcceptedCall$18$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1335xda5c9075(TLRPC.TL_error error, TLObject response) {
        if (error != null) {
            callFailed();
            return;
        }
        this.privateCall = ((TLRPC.TL_phone_phoneCall) response).phone_call;
        initiateActualEncryptedCall();
    }

    private int convertDataSavingMode(int mode) {
        if (mode != 3) {
            return mode;
        }
        return ApplicationLoader.isRoaming() ? 1 : 0;
    }

    public void migrateToChat(TLRPC.Chat newChat) {
        this.chat = newChat;
    }

    public void setGroupCallPeer(TLRPC.InputPeer peer) {
        ChatObject.Call call = this.groupCall;
        if (call == null) {
            return;
        }
        this.groupCallPeer = peer;
        call.setSelfPeer(peer);
        TLRPC.ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(this.groupCall.chatId);
        if (chatFull != null) {
            chatFull.groupcall_default_join_as = this.groupCall.selfPeer;
            if (chatFull.groupcall_default_join_as != null) {
                if (chatFull instanceof TLRPC.TL_chatFull) {
                    chatFull.flags |= 32768;
                } else {
                    chatFull.flags |= ConnectionsManager.FileTypeFile;
                }
            } else if (chatFull instanceof TLRPC.TL_chatFull) {
                chatFull.flags &= -32769;
            } else {
                chatFull.flags &= -67108865;
            }
        }
        createGroupInstance(0, true);
        if (this.videoState[1] == 2) {
            createGroupInstance(1, true);
        }
    }

    private void startGroupCall(final int ssrc, String json, final boolean create) {
        if (sharedInstance != this) {
            return;
        }
        boolean z = true;
        if (this.createGroupCall) {
            ChatObject.Call call = new ChatObject.Call();
            this.groupCall = call;
            call.call = new TLRPC.TL_groupCall();
            this.groupCall.call.participants_count = 0;
            this.groupCall.call.version = 1;
            this.groupCall.call.can_start_video = true;
            this.groupCall.call.can_change_join_muted = true;
            this.groupCall.chatId = this.chat.id;
            this.groupCall.currentAccount = AccountInstance.getInstance(this.currentAccount);
            this.groupCall.setSelfPeer(this.groupCallPeer);
            this.groupCall.createNoVideoParticipant();
            dispatchStateChanged(6);
            TLRPC.TL_phone_createGroupCall req = new TLRPC.TL_phone_createGroupCall();
            req.peer = MessagesController.getInputPeer(this.chat);
            req.random_id = Utilities.random.nextInt();
            int i = this.scheduleDate;
            if (i != 0) {
                req.schedule_date = i;
                req.flags |= 2;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda81
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    VoIPService.this.m1342lambda$startGroupCall$22$orgtelegrammessengervoipVoIPService(tLObject, tL_error);
                }
            }, 2);
            this.createGroupCall = false;
        } else if (json == null) {
            if (this.groupCall == null) {
                ChatObject.Call groupCall = MessagesController.getInstance(this.currentAccount).getGroupCall(this.chat.id, false);
                this.groupCall = groupCall;
                if (groupCall != null) {
                    groupCall.setSelfPeer(this.groupCallPeer);
                }
            }
            configureDeviceForCall();
            showNotification();
            AndroidUtilities.runOnUIThread(VoIPService$$ExternalSyntheticLambda58.INSTANCE);
            createGroupInstance(0, false);
        } else if (getSharedInstance() == null || this.groupCall == null) {
        } else {
            dispatchStateChanged(1);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("initital source = " + ssrc);
            }
            TLRPC.TL_phone_joinGroupCall req2 = new TLRPC.TL_phone_joinGroupCall();
            req2.muted = true;
            if (this.videoState[0] == 2) {
                z = false;
            }
            req2.video_stopped = z;
            req2.call = this.groupCall.getInputGroupCall();
            req2.params = new TLRPC.TL_dataJSON();
            req2.params.data = json;
            if (!TextUtils.isEmpty(this.joinHash)) {
                req2.invite_hash = this.joinHash;
                req2.flags = 2 | req2.flags;
            }
            TLRPC.InputPeer inputPeer = this.groupCallPeer;
            if (inputPeer != null) {
                req2.join_as = inputPeer;
            } else {
                req2.join_as = new TLRPC.TL_inputPeerUser();
                req2.join_as.user_id = AccountInstance.getInstance(this.currentAccount).getUserConfig().getClientUserId();
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda86
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    VoIPService.this.m1347lambda$startGroupCall$28$orgtelegrammessengervoipVoIPService(ssrc, create, tLObject, tL_error);
                }
            });
        }
    }

    /* renamed from: lambda$startGroupCall$22$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1342lambda$startGroupCall$22$orgtelegrammessengervoipVoIPService(TLObject response, final TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.Updates updates = (TLRPC.Updates) response;
            int a = 0;
            while (true) {
                if (a >= updates.updates.size()) {
                    break;
                }
                TLRPC.Update update = updates.updates.get(a);
                if (!(update instanceof TLRPC.TL_updateGroupCall)) {
                    a++;
                } else {
                    final TLRPC.TL_updateGroupCall updateGroupCall = (TLRPC.TL_updateGroupCall) update;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda51
                        @Override // java.lang.Runnable
                        public final void run() {
                            VoIPService.this.m1340lambda$startGroupCall$20$orgtelegrammessengervoipVoIPService(updateGroupCall);
                        }
                    });
                    break;
                }
            }
            int a2 = this.currentAccount;
            MessagesController.getInstance(a2).processUpdates(updates, false);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda43
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1341lambda$startGroupCall$21$orgtelegrammessengervoipVoIPService(error);
            }
        });
    }

    /* renamed from: lambda$startGroupCall$20$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1340lambda$startGroupCall$20$orgtelegrammessengervoipVoIPService(TLRPC.TL_updateGroupCall updateGroupCall) {
        if (sharedInstance == null) {
            return;
        }
        this.groupCall.call.access_hash = updateGroupCall.call.access_hash;
        this.groupCall.call.id = updateGroupCall.call.id;
        MessagesController.getInstance(this.currentAccount).putGroupCall(this.groupCall.chatId, this.groupCall);
        startGroupCall(0, null, false);
    }

    /* renamed from: lambda$startGroupCall$21$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1341lambda$startGroupCall$21$orgtelegrammessengervoipVoIPService(TLRPC.TL_error error) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needShowAlert, 6, error.text);
        hangUp(0);
    }

    /* renamed from: lambda$startGroupCall$28$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1347lambda$startGroupCall$28$orgtelegrammessengervoipVoIPService(final int ssrc, final boolean create, TLObject response, final TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda31
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.m1343lambda$startGroupCall$24$orgtelegrammessengervoipVoIPService(ssrc);
                }
            });
            TLRPC.Updates updates = (TLRPC.Updates) response;
            long selfId = getSelfId();
            int N = updates.updates.size();
            for (int a = 0; a < N; a++) {
                TLRPC.Update update = updates.updates.get(a);
                if (update instanceof TLRPC.TL_updateGroupCallParticipants) {
                    TLRPC.TL_updateGroupCallParticipants updateGroupCallParticipants = (TLRPC.TL_updateGroupCallParticipants) update;
                    int b = 0;
                    int N2 = updateGroupCallParticipants.participants.size();
                    while (true) {
                        if (b < N2) {
                            final TLRPC.TL_groupCallParticipant participant = updateGroupCallParticipants.participants.get(b);
                            if (MessageObject.getPeerId(participant.peer) != selfId) {
                                b++;
                            } else {
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda50
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        VoIPService.this.m1344lambda$startGroupCall$25$orgtelegrammessengervoipVoIPService(participant);
                                    }
                                });
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.d("join source = " + participant.source);
                                }
                            }
                        }
                    }
                } else if (update instanceof TLRPC.TL_updateGroupCallConnection) {
                    TLRPC.TL_updateGroupCallConnection updateGroupCallConnection = (TLRPC.TL_updateGroupCallConnection) update;
                    if (!updateGroupCallConnection.presentation) {
                        this.myParams = updateGroupCallConnection.params;
                    }
                }
            }
            int a2 = this.currentAccount;
            MessagesController.getInstance(a2).processUpdates(updates, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda53
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.m1345lambda$startGroupCall$26$orgtelegrammessengervoipVoIPService(create);
                }
            });
            startGroupCheckShortpoll();
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda45
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1346lambda$startGroupCall$27$orgtelegrammessengervoipVoIPService(error);
            }
        });
    }

    /* renamed from: lambda$startGroupCall$24$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1343lambda$startGroupCall$24$orgtelegrammessengervoipVoIPService(int ssrc) {
        this.mySource[0] = ssrc;
    }

    /* renamed from: lambda$startGroupCall$25$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1344lambda$startGroupCall$25$orgtelegrammessengervoipVoIPService(TLRPC.TL_groupCallParticipant participant) {
        this.mySource[0] = participant.source;
    }

    /* renamed from: lambda$startGroupCall$26$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1345lambda$startGroupCall$26$orgtelegrammessengervoipVoIPService(boolean create) {
        this.groupCall.loadMembers(create);
    }

    /* renamed from: lambda$startGroupCall$27$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1346lambda$startGroupCall$27$orgtelegrammessengervoipVoIPService(TLRPC.TL_error error) {
        if (!"JOIN_AS_PEER_INVALID".equals(error.text)) {
            if ("GROUPCALL_SSRC_DUPLICATE_MUCH".equals(error.text)) {
                createGroupInstance(0, false);
                return;
            }
            if ("GROUPCALL_INVALID".equals(error.text)) {
                MessagesController.getInstance(this.currentAccount).loadFullChat(this.chat.id, 0, true);
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needShowAlert, 6, error.text);
            hangUp(0);
            return;
        }
        TLRPC.ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(this.chat.id);
        if (chatFull != null) {
            if (chatFull instanceof TLRPC.TL_chatFull) {
                chatFull.flags &= -32769;
            } else {
                chatFull.flags &= -67108865;
            }
            chatFull.groupcall_default_join_as = null;
            JoinCallAlert.resetCache();
        }
        hangUp(2);
    }

    private void startScreenCapture(final int ssrc, String json) {
        if (getSharedInstance() == null || this.groupCall == null) {
            return;
        }
        this.mySource[1] = 0;
        TLRPC.TL_phone_joinGroupCallPresentation req = new TLRPC.TL_phone_joinGroupCallPresentation();
        req.call = this.groupCall.getInputGroupCall();
        req.params = new TLRPC.TL_dataJSON();
        req.params.data = json;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda84
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                VoIPService.this.m1360xe204004f(ssrc, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$startScreenCapture$32$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1360xe204004f(final int ssrc, TLObject response, final TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda32
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.m1357xb2fff837(ssrc);
                }
            });
            final TLRPC.Updates updates = (TLRPC.Updates) response;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda52
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.m1358xc8c3aa4d(updates);
                }
            });
            MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
            startGroupCheckShortpoll();
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda46
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1359x5563d54e(error);
            }
        });
    }

    /* renamed from: lambda$startScreenCapture$29$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1357xb2fff837(int ssrc) {
        this.mySource[1] = ssrc;
    }

    /* renamed from: lambda$startScreenCapture$30$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1358xc8c3aa4d(TLRPC.Updates updates) {
        VoIPService voIPService = this;
        char c = 1;
        if (voIPService.tgVoip[1] != null) {
            long selfId = getSelfId();
            int a = 0;
            int N = updates.updates.size();
            while (a < N) {
                TLRPC.Update update = updates.updates.get(a);
                if (update instanceof TLRPC.TL_updateGroupCallConnection) {
                    TLRPC.TL_updateGroupCallConnection updateGroupCallConnection = (TLRPC.TL_updateGroupCallConnection) update;
                    if (updateGroupCallConnection.presentation) {
                        voIPService.tgVoip[c].setJoinResponsePayload(updateGroupCallConnection.params.data);
                    }
                } else if (update instanceof TLRPC.TL_updateGroupCallParticipants) {
                    TLRPC.TL_updateGroupCallParticipants updateGroupCallParticipants = (TLRPC.TL_updateGroupCallParticipants) update;
                    int b = 0;
                    int N2 = updateGroupCallParticipants.participants.size();
                    while (true) {
                        if (b < N2) {
                            TLRPC.TL_groupCallParticipant participant = updateGroupCallParticipants.participants.get(b);
                            if (MessageObject.getPeerId(participant.peer) != selfId) {
                                b++;
                                voIPService = this;
                            } else if (participant.presentation != null) {
                                if ((participant.presentation.flags & 2) != 0) {
                                    voIPService.mySource[c] = participant.presentation.audio_source;
                                } else {
                                    int c2 = 0;
                                    int N3 = participant.presentation.source_groups.size();
                                    while (c2 < N3) {
                                        TLRPC.TL_groupCallParticipantVideoSourceGroup sourceGroup = participant.presentation.source_groups.get(c2);
                                        if (sourceGroup.sources.size() > 0) {
                                            c = 1;
                                            voIPService.mySource[1] = sourceGroup.sources.get(0).intValue();
                                        }
                                        c2++;
                                        voIPService = this;
                                    }
                                }
                            }
                        }
                    }
                }
                a++;
                voIPService = this;
            }
        }
    }

    /* renamed from: lambda$startScreenCapture$31$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1359x5563d54e(TLRPC.TL_error error) {
        if ("GROUPCALL_VIDEO_TOO_MUCH".equals(error.text)) {
            this.groupCall.reloadGroupCall();
        } else if (!"JOIN_AS_PEER_INVALID".equals(error.text)) {
            if ("GROUPCALL_SSRC_DUPLICATE_MUCH".equals(error.text)) {
                createGroupInstance(1, false);
            } else if ("GROUPCALL_INVALID".equals(error.text)) {
                MessagesController.getInstance(this.currentAccount).loadFullChat(this.chat.id, 0, true);
            }
        } else {
            TLRPC.ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(this.chat.id);
            if (chatFull != null) {
                if (chatFull instanceof TLRPC.TL_chatFull) {
                    chatFull.flags &= -32769;
                } else {
                    chatFull.flags &= -67108865;
                }
                chatFull.groupcall_default_join_as = null;
                JoinCallAlert.resetCache();
            }
            hangUp(2);
        }
    }

    private void startGroupCheckShortpoll() {
        ChatObject.Call call;
        if (this.shortPollRunnable != null || sharedInstance == null || (call = this.groupCall) == null) {
            return;
        }
        int[] iArr = this.mySource;
        if (iArr[0] == 0 && iArr[1] == 0 && (call.call == null || !this.groupCall.call.rtmp_stream)) {
            return;
        }
        Runnable runnable = new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda23
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1350x130015a();
            }
        };
        this.shortPollRunnable = runnable;
        AndroidUtilities.runOnUIThread(runnable, 4000L);
    }

    /* renamed from: lambda$startGroupCheckShortpoll$35$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1350x130015a() {
        ChatObject.Call call;
        if (this.shortPollRunnable == null || sharedInstance == null || (call = this.groupCall) == null) {
            return;
        }
        int[] iArr = this.mySource;
        if (iArr[0] == 0 && iArr[1] == 0 && (call.call == null || !this.groupCall.call.rtmp_stream)) {
            return;
        }
        final TLRPC.TL_phone_checkGroupCall req = new TLRPC.TL_phone_checkGroupCall();
        req.call = this.groupCall.getInputGroupCall();
        int a = 0;
        while (true) {
            int[] iArr2 = this.mySource;
            if (a < iArr2.length) {
                if (iArr2[a] != 0) {
                    req.sources.add(Integer.valueOf(this.mySource[a]));
                }
                a++;
            } else {
                int a2 = this.currentAccount;
                this.checkRequestId = ConnectionsManager.getInstance(a2).sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda90
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        VoIPService.this.m1349x748fd659(req, tLObject, tL_error);
                    }
                });
                return;
            }
        }
    }

    /* renamed from: lambda$startGroupCheckShortpoll$34$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1349x748fd659(final TLRPC.TL_phone_checkGroupCall req, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda42
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1348xe7efab58(response, req, error);
            }
        });
    }

    /* renamed from: lambda$startGroupCheckShortpoll$33$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1348xe7efab58(TLObject response, TLRPC.TL_phone_checkGroupCall req, TLRPC.TL_error error) {
        if (this.shortPollRunnable == null || sharedInstance == null || this.groupCall == null) {
            return;
        }
        this.shortPollRunnable = null;
        this.checkRequestId = 0;
        boolean recreateCamera = false;
        boolean recreateScreenCapture = false;
        if (response instanceof TLRPC.Vector) {
            TLRPC.Vector vector = (TLRPC.Vector) response;
            if (this.mySource[0] != 0 && req.sources.contains(Integer.valueOf(this.mySource[0])) && !vector.objects.contains(Integer.valueOf(this.mySource[0]))) {
                recreateCamera = true;
            }
            if (this.mySource[1] != 0 && req.sources.contains(Integer.valueOf(this.mySource[1])) && !vector.objects.contains(Integer.valueOf(this.mySource[1]))) {
                recreateScreenCapture = true;
            }
        } else if (error != null && error.code == 400) {
            recreateCamera = true;
            if (this.mySource[1] != 0 && req.sources.contains(Integer.valueOf(this.mySource[1]))) {
                recreateScreenCapture = true;
            }
        }
        if (recreateCamera) {
            createGroupInstance(0, false);
        }
        if (recreateScreenCapture) {
            createGroupInstance(1, false);
        }
        int[] iArr = this.mySource;
        if (iArr[1] != 0 || iArr[0] != 0 || (this.groupCall.call != null && this.groupCall.call.rtmp_stream)) {
            startGroupCheckShortpoll();
        }
    }

    private void cancelGroupCheckShortPoll() {
        int[] iArr = this.mySource;
        if (iArr[1] != 0 || iArr[0] != 0) {
            return;
        }
        if (this.checkRequestId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.checkRequestId, false);
            this.checkRequestId = 0;
        }
        Runnable runnable = this.shortPollRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.shortPollRunnable = null;
        }
    }

    /* loaded from: classes4.dex */
    public static class RequestedParticipant {
        public int audioSsrc;
        public TLRPC.TL_groupCallParticipant participant;

        public RequestedParticipant(TLRPC.TL_groupCallParticipant p, int ssrc) {
            this.participant = p;
            this.audioSsrc = ssrc;
        }
    }

    private void broadcastUnknownParticipants(long taskPtr, int[] unknown) {
        if (this.groupCall != null && this.tgVoip[0] != null) {
            long selfId = getSelfId();
            ArrayList<RequestedParticipant> participants = null;
            int N = unknown.length;
            for (int a = 0; a < N; a++) {
                TLRPC.TL_groupCallParticipant p = this.groupCall.participantsBySources.get(unknown[a]);
                if (p == null && (p = this.groupCall.participantsByVideoSources.get(unknown[a])) == null) {
                    p = this.groupCall.participantsByPresentationSources.get(unknown[a]);
                }
                if (p != null && MessageObject.getPeerId(p.peer) != selfId && p.source != 0) {
                    if (participants == null) {
                        participants = new ArrayList<>();
                    }
                    participants.add(new RequestedParticipant(p, unknown[a]));
                }
            }
            if (participants != null) {
                int[] ssrcs = new int[participants.size()];
                int N2 = participants.size();
                for (int a2 = 0; a2 < N2; a2++) {
                    ssrcs[a2] = participants.get(a2).audioSsrc;
                }
                this.tgVoip[0].onMediaDescriptionAvailable(taskPtr, ssrcs);
                int N3 = participants.size();
                for (int a3 = 0; a3 < N3; a3++) {
                    RequestedParticipant p2 = participants.get(a3);
                    if (p2.participant.muted_by_you) {
                        this.tgVoip[0].setVolume(p2.audioSsrc, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
                    } else {
                        NativeInstance nativeInstance = this.tgVoip[0];
                        int i = p2.audioSsrc;
                        double participantVolume = ChatObject.getParticipantVolume(p2.participant);
                        Double.isNaN(participantVolume);
                        nativeInstance.setVolume(i, participantVolume / 10000.0d);
                    }
                }
            }
        }
    }

    private void createGroupInstance(final int type, boolean switchAccount) {
        String str;
        if (switchAccount) {
            this.mySource[type] = 0;
            if (type == 0) {
                this.switchingAccount = switchAccount;
            }
        }
        cancelGroupCheckShortPoll();
        if (type == 0) {
            this.wasConnected = false;
        } else if (!this.wasConnected) {
            this.reconnectScreenCapture = true;
            return;
        }
        boolean created = false;
        if (this.tgVoip[type] == null) {
            created = true;
            if (BuildVars.DEBUG_VERSION) {
                str = VoIPHelper.getLogFilePath("voip_" + type + "_" + this.groupCall.call.id);
            } else {
                str = VoIPHelper.getLogFilePath(this.groupCall.call.id, false);
            }
            String logFilePath = str;
            this.tgVoip[type] = NativeInstance.makeGroup(logFilePath, this.captureDevice[type], type == 1, type == 0 && SharedConfig.noiseSupression, new NativeInstance.PayloadCallback() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda69
                @Override // org.telegram.messenger.voip.NativeInstance.PayloadCallback
                public final void run(int i, String str2) {
                    VoIPService.this.m1296xffc67c4d(type, i, str2);
                }
            }, new NativeInstance.AudioLevelsCallback() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda68
                @Override // org.telegram.messenger.voip.NativeInstance.AudioLevelsCallback
                public final void run(int[] iArr, float[] fArr, boolean[] zArr) {
                    VoIPService.this.m1297x1906d24f(type, iArr, fArr, zArr);
                }
            }, new NativeInstance.VideoSourcesCallback() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda73
                @Override // org.telegram.messenger.voip.NativeInstance.VideoSourcesCallback
                public final void run(long j, int[] iArr) {
                    VoIPService.this.m1299xbb6aaf66(type, j, iArr);
                }
            }, new NativeInstance.RequestBroadcastPartCallback() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda70
                @Override // org.telegram.messenger.voip.NativeInstance.RequestBroadcastPartCallback
                public final void run(long j, long j2, int i, int i2) {
                    VoIPService.this.m1304x7a8b866b(type, j, j2, i, i2);
                }
            }, new NativeInstance.RequestBroadcastPartCallback() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda71
                @Override // org.telegram.messenger.voip.NativeInstance.RequestBroadcastPartCallback
                public final void run(long j, long j2, int i, int i2) {
                    VoIPService.this.m1306x93cbdc6d(type, j, j2, i, i2);
                }
            }, new NativeInstance.RequestCurrentTimeCallback() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda72
                @Override // org.telegram.messenger.voip.NativeInstance.RequestCurrentTimeCallback
                public final void run(long j) {
                    VoIPService.this.m1308xad0c326f(type, j);
                }
            });
            this.tgVoip[type].setOnStateUpdatedListener(new Instance.OnStateUpdatedListener() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda65
                @Override // org.telegram.messenger.voip.Instance.OnStateUpdatedListener
                public final void onStateUpdated(int i, boolean z) {
                    VoIPService.this.m1309xc2cfe485(type, i, z);
                }
            });
        }
        this.tgVoip[type].resetGroupInstance(!created, false);
        if (this.captureDevice[type] != 0) {
            this.destroyCaptureDevice[type] = false;
        }
        if (type == 0) {
            dispatchStateChanged(1);
        }
    }

    /* renamed from: lambda$createGroupInstance$36$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1296xffc67c4d(int type, int ssrc, String json) {
        if (type == 0) {
            startGroupCall(ssrc, json, true);
        } else {
            startScreenCapture(ssrc, json);
        }
    }

    /* renamed from: lambda$createGroupInstance$38$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1297x1906d24f(int type, int[] uids, float[] levels, boolean[] voice) {
        ChatObject.Call call;
        if (sharedInstance == null || (call = this.groupCall) == null || type != 0) {
            return;
        }
        call.processVoiceLevelsUpdate(uids, levels, voice);
        float maxAmplitude = 0.0f;
        boolean hasOther = false;
        for (int a = 0; a < uids.length; a++) {
            if (uids[a] == 0) {
                if (this.lastTypingTimeSend < SystemClock.uptimeMillis() - DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS && levels[a] > 0.1f && voice[a]) {
                    this.lastTypingTimeSend = SystemClock.uptimeMillis();
                    TLRPC.TL_messages_setTyping req = new TLRPC.TL_messages_setTyping();
                    req.action = new TLRPC.TL_speakingInGroupCallAction();
                    req.peer = MessagesController.getInputPeer(this.chat);
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, VoIPService$$ExternalSyntheticLambda94.INSTANCE);
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.webRtcMicAmplitudeEvent, Float.valueOf(levels[a]));
            } else {
                hasOther = true;
                maxAmplitude = Math.max(maxAmplitude, levels[a]);
            }
        }
        if (hasOther) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.webRtcSpeakerAmplitudeEvent, Float.valueOf(maxAmplitude));
            NativeInstance.AudioLevelsCallback audioLevelsCallback2 = audioLevelsCallback;
            if (audioLevelsCallback2 != null) {
                audioLevelsCallback2.run(uids, levels, voice);
            }
        }
    }

    public static /* synthetic */ void lambda$createGroupInstance$37(TLObject response, TLRPC.TL_error error) {
    }

    /* renamed from: lambda$createGroupInstance$40$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1299xbb6aaf66(int type, final long taskPtr, final int[] unknown) {
        ChatObject.Call call;
        if (sharedInstance == null || (call = this.groupCall) == null || type != 0) {
            return;
        }
        call.processUnknownVideoParticipants(unknown, new ChatObject.Call.OnParticipantsLoad() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda60
            @Override // org.telegram.messenger.ChatObject.Call.OnParticipantsLoad
            public final void onLoad(ArrayList arrayList) {
                VoIPService.this.m1298xa5a6fd50(taskPtr, unknown, arrayList);
            }
        });
    }

    /* renamed from: lambda$createGroupInstance$39$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1298xa5a6fd50(long taskPtr, int[] unknown, ArrayList ssrcs) {
        if (sharedInstance == null || this.groupCall == null) {
            return;
        }
        broadcastUnknownParticipants(taskPtr, unknown);
    }

    /* JADX WARN: Type inference failed for: r0v11, types: [org.telegram.messenger.AccountInstance, java.lang.String] */
    /* JADX WARN: Type inference failed for: r0v14, types: [int, java.lang.String] */
    /* JADX WARN: Type inference failed for: r0v18, types: [org.telegram.tgnet.TLRPC$TL_inputGroupCallStream, int] */
    /* JADX WARN: Type inference failed for: r0v4, types: [org.telegram.tgnet.TLRPC$TL_inputGroupCallStream, org.telegram.tgnet.TLRPC$TL_inputGroupCall] */
    /* JADX WARN: Type inference failed for: r0v5 */
    /* JADX WARN: Type inference failed for: r0v6, types: [org.telegram.tgnet.TLRPC$InputFileLocation, org.telegram.tgnet.TLRPC$TL_upload_getFile] */
    /* renamed from: lambda$createGroupInstance$45$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1304x7a8b866b(final int type, final long timestamp, long duration, final int videoChannel, final int quality) {
        StringBuilder sb;
        if (type != 0) {
            return;
        }
        new TLRPC.TL_upload_getFile();
        131072.limit = 131072;
        new TLRPC.TL_inputGroupCallStream();
        ?? inputGroupCall = this.groupCall.getInputGroupCall();
        inputGroupCall.call = inputGroupCall;
        inputGroupCall.time_ms = timestamp;
        ?? r0 = 500;
        if (duration == 500) {
            500.scale = 1;
        }
        if (videoChannel != 0) {
            r0 = 500.flags | 1;
            r0.flags = r0;
            r0.video_channel = videoChannel;
            r0.video_quality = quality;
        }
        r0.location = r0;
        if (videoChannel == 0) {
            sb = new StringBuilder();
            sb.append("");
            sb.append(timestamp);
        } else {
            sb = new StringBuilder();
            sb.append(videoChannel);
            sb.append("_");
            sb.append(timestamp);
            sb.append("_");
            sb.append(quality);
        }
        sb.toString();
        final ?? accountInstance = AccountInstance.getInstance(this.currentAccount);
        ConnectionsManager connectionsManager = accountInstance.getConnectionsManager();
        RequestDelegateTimestamp requestDelegateTimestamp = new RequestDelegateTimestamp() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda98
            @Override // org.telegram.tgnet.RequestDelegateTimestamp
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error, long j) {
                VoIPService.this.m1302x614b3069(accountInstance, type, timestamp, videoChannel, quality, tLObject, tL_error, j);
            }
        };
        TLRPC.GroupCall groupCall = this.groupCall.call;
        final ?? sendRequest = connectionsManager.sendRequest(groupCall, requestDelegateTimestamp, 2, 2, groupCall.stream_dc_id);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda40
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1303xedeb5b6a(sendRequest, sendRequest);
            }
        });
    }

    /* renamed from: lambda$createGroupInstance$41$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1300x480ada67(String key) {
        this.currentStreamRequestTimestamp.remove(key);
    }

    /* renamed from: lambda$createGroupInstance$43$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1302x614b3069(final String key, final int type, long timestamp, int videoChannel, int quality, TLObject response, TLRPC.TL_error error, long responseTime) {
        int status;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda39
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1300x480ada67(key);
            }
        });
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[type] == null) {
            return;
        }
        if (response != null) {
            TLRPC.TL_upload_file res = (TLRPC.TL_upload_file) response;
            nativeInstanceArr[type].onStreamPartAvailable(timestamp, res.bytes.buffer, res.bytes.limit(), responseTime, videoChannel, quality);
        } else if ("GROUPCALL_JOIN_MISSING".equals(error.text)) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda28
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.m1301xd4ab0568(type);
                }
            });
        } else {
            if ("TIME_TOO_BIG".equals(error.text) || error.text.startsWith("FLOOD_WAIT")) {
                status = 0;
            } else {
                status = -1;
            }
            this.tgVoip[type].onStreamPartAvailable(timestamp, null, status, responseTime, videoChannel, quality);
        }
    }

    /* renamed from: lambda$createGroupInstance$42$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1301xd4ab0568(int type) {
        createGroupInstance(type, false);
    }

    /* renamed from: lambda$createGroupInstance$44$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1303xedeb5b6a(String key, int reqId) {
        this.currentStreamRequestTimestamp.put(key, Integer.valueOf(reqId));
    }

    /* renamed from: lambda$createGroupInstance$47$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1306x93cbdc6d(int type, final long timestamp, long duration, final int videoChannel, final int quality) {
        if (type != 0) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda36
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1305x72bb16c(videoChannel, timestamp, quality);
            }
        });
    }

    /* renamed from: lambda$createGroupInstance$46$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1305x72bb16c(int videoChannel, long timestamp, int quality) {
        StringBuilder sb;
        if (videoChannel == 0) {
            sb = new StringBuilder();
            sb.append("");
            sb.append(timestamp);
        } else {
            sb = new StringBuilder();
            sb.append(videoChannel);
            sb.append("_");
            sb.append(timestamp);
            sb.append("_");
            sb.append(quality);
        }
        String key = sb.toString();
        Integer reqId = this.currentStreamRequestTimestamp.get(key);
        if (reqId != null) {
            AccountInstance.getInstance(this.currentAccount).getConnectionsManager().cancelRequest(reqId.intValue(), true);
            this.currentStreamRequestTimestamp.remove(key);
        }
    }

    /* renamed from: lambda$createGroupInstance$49$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1308xad0c326f(final int type, final long taskPtr) {
        ChatObject.Call call = this.groupCall;
        if (call != null && call.call != null && this.groupCall.call.rtmp_stream) {
            TLRPC.TL_phone_getGroupCallStreamChannels req = new TLRPC.TL_phone_getGroupCallStreamChannels();
            req.call = this.groupCall.getInputGroupCall();
            ChatObject.Call call2 = this.groupCall;
            if (call2 == null || call2.call == null || this.tgVoip[type] == null) {
                NativeInstance[] nativeInstanceArr = this.tgVoip;
                if (nativeInstanceArr[type] != null) {
                    nativeInstanceArr[type].onRequestTimeComplete(taskPtr, 0L);
                    return;
                }
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegateTimestamp() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda97
                @Override // org.telegram.tgnet.RequestDelegateTimestamp
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error, long j) {
                    VoIPService.this.m1307x206c076e(type, taskPtr, tLObject, tL_error, j);
                }
            }, 2, 2, this.groupCall.call.stream_dc_id);
            return;
        }
        NativeInstance[] nativeInstanceArr2 = this.tgVoip;
        if (nativeInstanceArr2[type] != null) {
            nativeInstanceArr2[type].onRequestTimeComplete(taskPtr, ConnectionsManager.getInstance(this.currentAccount).getCurrentTimeMillis());
        }
    }

    /* renamed from: lambda$createGroupInstance$48$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1307x206c076e(int type, long taskPtr, TLObject response, TLRPC.TL_error error, long responseTime) {
        long currentTime = 0;
        if (error == null) {
            TLRPC.TL_phone_groupCallStreamChannels res = (TLRPC.TL_phone_groupCallStreamChannels) response;
            if (!res.channels.isEmpty()) {
                currentTime = res.channels.get(0).last_timestamp_ms;
            }
            if (!this.groupCall.loadedRtmpStreamParticipant) {
                this.groupCall.createRtmpStreamParticipant(res.channels);
                this.groupCall.loadedRtmpStreamParticipant = true;
            }
        }
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[type] != null) {
            nativeInstanceArr[type].onRequestTimeComplete(taskPtr, currentTime);
        }
    }

    /* renamed from: updateConnectionState */
    public void m1309xc2cfe485(final int type, int state, boolean inTransition) {
        if (type != 0) {
            return;
        }
        dispatchStateChanged((state == 1 || this.switchingStream) ? 3 : 5);
        if (this.switchingStream && (state == 0 || (state == 1 && inTransition))) {
            Runnable runnable = new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda34
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.m1363xfaecad54(type);
                }
            };
            this.switchingStreamTimeoutRunnable = runnable;
            AndroidUtilities.runOnUIThread(runnable, 3000L);
        }
        if (state == 0) {
            startGroupCheckShortpoll();
            if (this.playedConnectedSound && this.spPlayId == 0 && !this.switchingStream && !this.switchingAccount) {
                Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda25
                    @Override // java.lang.Runnable
                    public final void run() {
                        VoIPService.this.m1364x878cd855();
                    }
                });
                return;
            }
            return;
        }
        cancelGroupCheckShortPoll();
        if (!inTransition) {
            this.switchingStream = false;
            this.switchingAccount = false;
        }
        Runnable runnable2 = this.switchingStreamTimeoutRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.switchingStreamTimeoutRunnable = null;
        }
        if (this.playedConnectedSound) {
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda26
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.m1365x142d0356();
                }
            });
            Runnable runnable3 = this.connectingSoundRunnable;
            if (runnable3 != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable3);
                this.connectingSoundRunnable = null;
            }
        } else {
            playConnectedSound();
        }
        if (!this.wasConnected) {
            this.wasConnected = true;
            if (this.reconnectScreenCapture) {
                createGroupInstance(1, false);
                this.reconnectScreenCapture = false;
            }
            NativeInstance instance = this.tgVoip[0];
            if (instance != null && !this.micMute) {
                instance.setMuteMicrophone(false);
            }
            setParticipantsVolume();
        }
    }

    /* renamed from: lambda$updateConnectionState$51$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1363xfaecad54(int type) {
        if (this.switchingStreamTimeoutRunnable == null) {
            return;
        }
        this.switchingStream = false;
        m1309xc2cfe485(type, 0, true);
        this.switchingStreamTimeoutRunnable = null;
    }

    /* renamed from: lambda$updateConnectionState$52$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1364x878cd855() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
        }
        this.spPlayId = this.soundPool.play(this.spVoiceChatConnecting, 1.0f, 1.0f, 0, -1, 1.0f);
    }

    /* renamed from: lambda$updateConnectionState$53$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1365x142d0356() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
            this.spPlayId = 0;
        }
    }

    public void setParticipantsVolume() {
        NativeInstance instance = this.tgVoip[0];
        if (instance != null) {
            int N = this.groupCall.participants.size();
            for (int a = 0; a < N; a++) {
                TLRPC.TL_groupCallParticipant participant = this.groupCall.participants.valueAt(a);
                if (!participant.self && participant.source != 0 && (participant.can_self_unmute || !participant.muted)) {
                    if (participant.muted_by_you) {
                        setParticipantVolume(participant, 0);
                    } else {
                        setParticipantVolume(participant, ChatObject.getParticipantVolume(participant));
                    }
                }
            }
        }
    }

    public void setParticipantVolume(TLRPC.TL_groupCallParticipant participant, int volume) {
        NativeInstance nativeInstance = this.tgVoip[0];
        int i = participant.source;
        double d = volume;
        Double.isNaN(d);
        nativeInstance.setVolume(i, d / 10000.0d);
        if (participant.presentation != null && participant.presentation.audio_source != 0) {
            NativeInstance nativeInstance2 = this.tgVoip[0];
            int i2 = participant.presentation.audio_source;
            double d2 = volume;
            Double.isNaN(d2);
            nativeInstance2.setVolume(i2, d2 / 10000.0d);
        }
    }

    public boolean isSwitchingStream() {
        return this.switchingStream;
    }

    /* JADX WARN: Removed duplicated region for block: B:110:0x035b A[Catch: Exception -> 0x0386, TryCatch #3 {Exception -> 0x0386, blocks: (B:5:0x0010, B:7:0x0014, B:8:0x002a, B:10:0x0038, B:11:0x003f, B:12:0x0045, B:13:0x006c, B:15:0x0075, B:16:0x0081, B:18:0x0087, B:20:0x0096, B:26:0x00a8, B:29:0x00ae, B:31:0x00b2, B:40:0x00d3, B:42:0x00e9, B:44:0x00f1, B:46:0x0106, B:52:0x0112, B:57:0x011c, B:59:0x0120, B:60:0x013e, B:61:0x014b, B:65:0x0197, B:66:0x01a2, B:75:0x0225, B:77:0x022e, B:79:0x023a, B:81:0x0242, B:83:0x0256, B:85:0x025c, B:86:0x0276, B:90:0x0299, B:93:0x02a7, B:94:0x02b4, B:96:0x02b8, B:98:0x02bc, B:100:0x02c2, B:102:0x02ca, B:106:0x02d8, B:107:0x02e5, B:108:0x02ea, B:110:0x035b, B:111:0x035e, B:113:0x0366, B:114:0x0376, B:21:0x009a), top: B:127:0x0010, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:118:0x038b  */
    /* JADX WARN: Removed duplicated region for block: B:123:0x01a5 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0120 A[Catch: Exception -> 0x0386, TryCatch #3 {Exception -> 0x0386, blocks: (B:5:0x0010, B:7:0x0014, B:8:0x002a, B:10:0x0038, B:11:0x003f, B:12:0x0045, B:13:0x006c, B:15:0x0075, B:16:0x0081, B:18:0x0087, B:20:0x0096, B:26:0x00a8, B:29:0x00ae, B:31:0x00b2, B:40:0x00d3, B:42:0x00e9, B:44:0x00f1, B:46:0x0106, B:52:0x0112, B:57:0x011c, B:59:0x0120, B:60:0x013e, B:61:0x014b, B:65:0x0197, B:66:0x01a2, B:75:0x0225, B:77:0x022e, B:79:0x023a, B:81:0x0242, B:83:0x0256, B:85:0x025c, B:86:0x0276, B:90:0x0299, B:93:0x02a7, B:94:0x02b4, B:96:0x02b8, B:98:0x02bc, B:100:0x02c2, B:102:0x02ca, B:106:0x02d8, B:107:0x02e5, B:108:0x02ea, B:110:0x035b, B:111:0x035e, B:113:0x0366, B:114:0x0376, B:21:0x009a), top: B:127:0x0010, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:60:0x013e A[Catch: Exception -> 0x0386, TryCatch #3 {Exception -> 0x0386, blocks: (B:5:0x0010, B:7:0x0014, B:8:0x002a, B:10:0x0038, B:11:0x003f, B:12:0x0045, B:13:0x006c, B:15:0x0075, B:16:0x0081, B:18:0x0087, B:20:0x0096, B:26:0x00a8, B:29:0x00ae, B:31:0x00b2, B:40:0x00d3, B:42:0x00e9, B:44:0x00f1, B:46:0x0106, B:52:0x0112, B:57:0x011c, B:59:0x0120, B:60:0x013e, B:61:0x014b, B:65:0x0197, B:66:0x01a2, B:75:0x0225, B:77:0x022e, B:79:0x023a, B:81:0x0242, B:83:0x0256, B:85:0x025c, B:86:0x0276, B:90:0x0299, B:93:0x02a7, B:94:0x02b4, B:96:0x02b8, B:98:0x02bc, B:100:0x02c2, B:102:0x02ca, B:106:0x02d8, B:107:0x02e5, B:108:0x02ea, B:110:0x035b, B:111:0x035e, B:113:0x0366, B:114:0x0376, B:21:0x009a), top: B:127:0x0010, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0191  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x0195  */
    /* JADX WARN: Removed duplicated region for block: B:73:0x0221 A[Catch: Exception -> 0x020a, TRY_LEAVE, TryCatch #1 {Exception -> 0x020a, blocks: (B:68:0x01a5, B:73:0x0221), top: B:123:0x01a5 }] */
    /* JADX WARN: Removed duplicated region for block: B:76:0x022c  */
    /* JADX WARN: Removed duplicated region for block: B:79:0x023a A[Catch: Exception -> 0x0386, TryCatch #3 {Exception -> 0x0386, blocks: (B:5:0x0010, B:7:0x0014, B:8:0x002a, B:10:0x0038, B:11:0x003f, B:12:0x0045, B:13:0x006c, B:15:0x0075, B:16:0x0081, B:18:0x0087, B:20:0x0096, B:26:0x00a8, B:29:0x00ae, B:31:0x00b2, B:40:0x00d3, B:42:0x00e9, B:44:0x00f1, B:46:0x0106, B:52:0x0112, B:57:0x011c, B:59:0x0120, B:60:0x013e, B:61:0x014b, B:65:0x0197, B:66:0x01a2, B:75:0x0225, B:77:0x022e, B:79:0x023a, B:81:0x0242, B:83:0x0256, B:85:0x025c, B:86:0x0276, B:90:0x0299, B:93:0x02a7, B:94:0x02b4, B:96:0x02b8, B:98:0x02bc, B:100:0x02c2, B:102:0x02ca, B:106:0x02d8, B:107:0x02e5, B:108:0x02ea, B:110:0x035b, B:111:0x035e, B:113:0x0366, B:114:0x0376, B:21:0x009a), top: B:127:0x0010, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:88:0x0296  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x0298  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x02a5 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:96:0x02b8 A[Catch: Exception -> 0x0386, TryCatch #3 {Exception -> 0x0386, blocks: (B:5:0x0010, B:7:0x0014, B:8:0x002a, B:10:0x0038, B:11:0x003f, B:12:0x0045, B:13:0x006c, B:15:0x0075, B:16:0x0081, B:18:0x0087, B:20:0x0096, B:26:0x00a8, B:29:0x00ae, B:31:0x00b2, B:40:0x00d3, B:42:0x00e9, B:44:0x00f1, B:46:0x0106, B:52:0x0112, B:57:0x011c, B:59:0x0120, B:60:0x013e, B:61:0x014b, B:65:0x0197, B:66:0x01a2, B:75:0x0225, B:77:0x022e, B:79:0x023a, B:81:0x0242, B:83:0x0256, B:85:0x025c, B:86:0x0276, B:90:0x0299, B:93:0x02a7, B:94:0x02b4, B:96:0x02b8, B:98:0x02bc, B:100:0x02c2, B:102:0x02ca, B:106:0x02d8, B:107:0x02e5, B:108:0x02ea, B:110:0x035b, B:111:0x035e, B:113:0x0366, B:114:0x0376, B:21:0x009a), top: B:127:0x0010, inners: #2 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void initiateActualEncryptedCall() {
        /*
            Method dump skipped, instructions count: 916
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.initiateActualEncryptedCall():void");
    }

    /* renamed from: lambda$initiateActualEncryptedCall$54$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1317x7f1b7446() {
        Toast.makeText(this, "This call uses TCP which will degrade its quality.", 0).show();
    }

    /* renamed from: lambda$initiateActualEncryptedCall$55$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1318xbbb9f47(int[] uids, float[] levels, boolean[] voice) {
        if (sharedInstance == null || this.privateCall == null) {
            return;
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.webRtcMicAmplitudeEvent, Float.valueOf(levels[0]));
    }

    /* renamed from: lambda$initiateActualEncryptedCall$57$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1320x24fbf549(final int audioState, final int videoState) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda35
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1319x985bca48(audioState, videoState);
            }
        });
    }

    /* renamed from: lambda$initiateActualEncryptedCall$56$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1319x985bca48(int audioState, int videoState) {
        this.remoteAudioState = audioState;
        this.remoteVideoState = videoState;
        checkIsNear();
        for (int a = 0; a < this.stateListeners.size(); a++) {
            StateListener l = this.stateListeners.get(a);
            l.onMediaStateUpdated(audioState, videoState);
        }
    }

    /* renamed from: lambda$playConnectedSound$58$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1333x5248ab71() {
        this.soundPool.play(this.spVoiceChatStartId, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    public void playConnectedSound() {
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1333x5248ab71();
            }
        });
        this.playedConnectedSound = true;
    }

    private void startConnectingSound() {
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda21
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1339x630e1637();
            }
        });
    }

    /* renamed from: lambda$startConnectingSound$59$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1339x630e1637() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
        }
        int play = this.soundPool.play(this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
        this.spPlayId = play;
        if (play == 0) {
            AnonymousClass7 anonymousClass7 = new AnonymousClass7();
            this.connectingSoundRunnable = anonymousClass7;
            AndroidUtilities.runOnUIThread(anonymousClass7, 100L);
        }
    }

    /* renamed from: org.telegram.messenger.voip.VoIPService$7 */
    /* loaded from: classes4.dex */
    public class AnonymousClass7 implements Runnable {
        AnonymousClass7() {
            VoIPService.this = this$0;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (VoIPService.sharedInstance == null) {
                return;
            }
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$7$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.AnonymousClass7.this.m1368lambda$run$0$orgtelegrammessengervoipVoIPService$7();
                }
            });
        }

        /* renamed from: lambda$run$0$org-telegram-messenger-voip-VoIPService$7 */
        public /* synthetic */ void m1368lambda$run$0$orgtelegrammessengervoipVoIPService$7() {
            if (VoIPService.this.spPlayId == 0) {
                VoIPService voIPService = VoIPService.this;
                voIPService.spPlayId = voIPService.soundPool.play(VoIPService.this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
            }
            if (VoIPService.this.spPlayId != 0) {
                VoIPService.this.connectingSoundRunnable = null;
            } else {
                AndroidUtilities.runOnUIThread(this, 100L);
            }
        }
    }

    public void onSignalingData(byte[] data) {
        if (this.privateCall == null) {
            return;
        }
        TLRPC.TL_phone_sendSignalingData req = new TLRPC.TL_phone_sendSignalingData();
        req.peer = new TLRPC.TL_inputPhoneCall();
        req.peer.access_hash = this.privateCall.access_hash;
        req.peer.id = this.privateCall.id;
        req.data = data;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, VoIPService$$ExternalSyntheticLambda95.INSTANCE);
    }

    public static /* synthetic */ void lambda$onSignalingData$60(TLObject response, TLRPC.TL_error error) {
    }

    public boolean isVideoAvailable() {
        return this.isVideoAvailable;
    }

    public void onMediaButtonEvent(KeyEvent ev) {
        if (ev == null) {
            return;
        }
        if ((ev.getKeyCode() == 79 || ev.getKeyCode() == 127 || ev.getKeyCode() == 85) && ev.getAction() == 1) {
            if (this.currentState == 15) {
                acceptIncomingCall();
            } else {
                setMicMute(!isMicMute(), false, true);
            }
        }
    }

    public byte[] getGA() {
        return this.g_a;
    }

    public void forceRating() {
        this.forceRating = true;
    }

    private String[] getEmoji() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            os.write(this.authKey);
            os.write(this.g_a);
        } catch (IOException e) {
        }
        return EncryptionKeyEmojifier.emojifyForCall(Utilities.computeSHA256(os.toByteArray(), 0, os.size()));
    }

    public boolean hasEarpiece() {
        CallConnection callConnection;
        if (USE_CONNECTION_SERVICE && (callConnection = this.systemCallConnection) != null && callConnection.getCallAudioState() != null) {
            int routeMask = this.systemCallConnection.getCallAudioState().getSupportedRouteMask();
            return (routeMask & 5) != 0;
        } else if (((TelephonyManager) getSystemService("phone")).getPhoneType() != 0) {
            return true;
        } else {
            Boolean bool = this.mHasEarpiece;
            if (bool != null) {
                return bool.booleanValue();
            }
            try {
                AudioManager am = (AudioManager) getSystemService("audio");
                Method method = AudioManager.class.getMethod("getDevicesForStream", Integer.TYPE);
                Field field = AudioManager.class.getField("DEVICE_OUT_EARPIECE");
                int earpieceFlag = field.getInt(null);
                int bitmaskResult = ((Integer) method.invoke(am, 0)).intValue();
                if ((bitmaskResult & earpieceFlag) == earpieceFlag) {
                    this.mHasEarpiece = Boolean.TRUE;
                } else {
                    this.mHasEarpiece = Boolean.FALSE;
                }
            } catch (Throwable error) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error while checking earpiece! ", error);
                }
                this.mHasEarpiece = Boolean.TRUE;
            }
            return this.mHasEarpiece.booleanValue();
        }
    }

    public int getStatsNetworkType() {
        NetworkInfo networkInfo = this.lastNetInfo;
        if (networkInfo == null || networkInfo.getType() != 0) {
            return 1;
        }
        int netType = this.lastNetInfo.isRoaming() ? 2 : 0;
        return netType;
    }

    public void setSwitchingCamera(boolean switching, boolean isFrontFace) {
        this.switchingCamera = switching;
        if (!switching) {
            this.isFrontFaceCamera = isFrontFace;
            for (int a = 0; a < this.stateListeners.size(); a++) {
                StateListener l = this.stateListeners.get(a);
                l.onCameraSwitch(this.isFrontFaceCamera);
            }
        }
    }

    public void onCameraFirstFrameAvailable() {
        for (int a = 0; a < this.stateListeners.size(); a++) {
            StateListener l = this.stateListeners.get(a);
            l.onCameraFirstFrameAvailable();
        }
    }

    public void registerStateListener(StateListener l) {
        if (this.stateListeners.contains(l)) {
            return;
        }
        this.stateListeners.add(l);
        int i = this.currentState;
        if (i != 0) {
            l.onStateChanged(i);
        }
        int i2 = this.signalBarCount;
        if (i2 != 0) {
            l.onSignalBarsCountChanged(i2);
        }
    }

    public void unregisterStateListener(StateListener l) {
        this.stateListeners.remove(l);
    }

    public void editCallMember(TLObject object, Boolean mute, Boolean muteVideo, Integer volume, Boolean raiseHand, final Runnable onComplete) {
        TLRPC.InputPeer inputPeer;
        if (object == null || this.groupCall == null) {
            return;
        }
        TLRPC.TL_phone_editGroupCallParticipant req = new TLRPC.TL_phone_editGroupCallParticipant();
        req.call = this.groupCall.getInputGroupCall();
        if (object instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) object;
            if (UserObject.isUserSelf(user) && (inputPeer = this.groupCallPeer) != null) {
                req.participant = inputPeer;
            } else {
                req.participant = MessagesController.getInputPeer(user);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("edit group call part id = " + req.participant.user_id + " access_hash = " + req.participant.user_id);
                }
            }
        } else if (object instanceof TLRPC.Chat) {
            TLRPC.Chat chat = (TLRPC.Chat) object;
            req.participant = MessagesController.getInputPeer(chat);
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder sb = new StringBuilder();
                sb.append("edit group call part id = ");
                sb.append(req.participant.chat_id != 0 ? req.participant.chat_id : req.participant.channel_id);
                sb.append(" access_hash = ");
                sb.append(req.participant.access_hash);
                FileLog.d(sb.toString());
            }
        }
        if (mute != null) {
            req.muted = mute.booleanValue();
            req.flags |= 1;
        }
        if (volume != null) {
            req.volume = volume.intValue();
            req.flags |= 2;
        }
        if (raiseHand != null) {
            req.raise_hand = raiseHand.booleanValue();
            req.flags |= 4;
        }
        if (muteVideo != null) {
            req.video_stopped = muteVideo.booleanValue();
            req.flags |= 8;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("edit group call flags = " + req.flags);
        }
        final int account = this.currentAccount;
        AccountInstance.getInstance(account).getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda85
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                VoIPService.this.m1312lambda$editCallMember$61$orgtelegrammessengervoipVoIPService(account, onComplete, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$editCallMember$61$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1312lambda$editCallMember$61$orgtelegrammessengervoipVoIPService(int account, Runnable onComplete, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AccountInstance.getInstance(account).getMessagesController().processUpdates((TLRPC.Updates) response, false);
        } else if (error != null && "GROUPCALL_VIDEO_TOO_MUCH".equals(error.text)) {
            this.groupCall.reloadGroupCall();
        }
        if (onComplete != null) {
            AndroidUtilities.runOnUIThread(onComplete);
        }
    }

    public boolean isMicMute() {
        return this.micMute;
    }

    public void toggleSpeakerphoneOrShowRouteSheet(Context context, boolean fromOverlayWindow) {
        CallConnection callConnection;
        String str;
        int i;
        int i2 = 2;
        if (isBluetoothHeadsetConnected() && hasEarpiece()) {
            BottomSheet.Builder title = new BottomSheet.Builder(context).setTitle(LocaleController.getString("VoipOutputDevices", R.string.VoipOutputDevices), true);
            CharSequence[] charSequenceArr = new CharSequence[3];
            charSequenceArr[0] = LocaleController.getString("VoipAudioRoutingSpeaker", R.string.VoipAudioRoutingSpeaker);
            if (this.isHeadsetPlugged) {
                i = R.string.VoipAudioRoutingHeadset;
                str = "VoipAudioRoutingHeadset";
            } else {
                i = R.string.VoipAudioRoutingEarpiece;
                str = "VoipAudioRoutingEarpiece";
            }
            charSequenceArr[1] = LocaleController.getString(str, i);
            String str2 = this.currentBluetoothDeviceName;
            if (str2 == null) {
                str2 = LocaleController.getString("VoipAudioRoutingBluetooth", R.string.VoipAudioRoutingBluetooth);
            }
            charSequenceArr[2] = str2;
            int[] iArr = new int[3];
            iArr[0] = R.drawable.calls_menu_speaker;
            iArr[1] = this.isHeadsetPlugged ? R.drawable.calls_menu_headset : R.drawable.calls_menu_phone;
            iArr[2] = R.drawable.calls_menu_bluetooth;
            BottomSheet.Builder builder = title.setItems(charSequenceArr, iArr, new DialogInterface.OnClickListener() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i3) {
                    VoIPService.this.m1362x6676e26b(dialogInterface, i3);
                }
            });
            BottomSheet bottomSheet = builder.create();
            if (fromOverlayWindow) {
                if (Build.VERSION.SDK_INT >= 26) {
                    bottomSheet.getWindow().setType(2038);
                } else {
                    bottomSheet.getWindow().setType(2003);
                }
            }
            builder.show();
            return;
        }
        boolean z = USE_CONNECTION_SERVICE;
        if (z && (callConnection = this.systemCallConnection) != null && callConnection.getCallAudioState() != null) {
            int i3 = 5;
            if (hasEarpiece()) {
                CallConnection callConnection2 = this.systemCallConnection;
                if (callConnection2.getCallAudioState().getRoute() != 8) {
                    i3 = 8;
                }
                callConnection2.setAudioRoute(i3);
            } else {
                CallConnection callConnection3 = this.systemCallConnection;
                if (callConnection3.getCallAudioState().getRoute() == 2) {
                    i2 = 5;
                }
                callConnection3.setAudioRoute(i2);
            }
        } else if (this.audioConfigured && !z) {
            AudioManager am = (AudioManager) getSystemService("audio");
            if (hasEarpiece()) {
                am.setSpeakerphoneOn(!am.isSpeakerphoneOn());
            } else {
                am.setBluetoothScoOn(!am.isBluetoothScoOn());
            }
            updateOutputGainControlState();
        } else {
            this.speakerphoneStateToSet = !this.speakerphoneStateToSet;
        }
        Iterator<StateListener> it = this.stateListeners.iterator();
        while (it.hasNext()) {
            StateListener l = it.next();
            l.onAudioSettingsChanged();
        }
    }

    /* renamed from: lambda$toggleSpeakerphoneOrShowRouteSheet$62$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1362x6676e26b(DialogInterface dialog, int which) {
        if (getSharedInstance() == null) {
            return;
        }
        setAudioOutput(which);
    }

    public void setAudioOutput(int which) {
        CallConnection callConnection;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("setAudioOutput " + which);
        }
        AudioManager am = (AudioManager) getSystemService("audio");
        boolean z = USE_CONNECTION_SERVICE;
        if (z && (callConnection = this.systemCallConnection) != null) {
            switch (which) {
                case 0:
                    callConnection.setAudioRoute(8);
                    break;
                case 1:
                    callConnection.setAudioRoute(5);
                    break;
                case 2:
                    callConnection.setAudioRoute(2);
                    break;
            }
        } else if (this.audioConfigured && !z) {
            switch (which) {
                case 0:
                    this.needSwitchToBluetoothAfterScoActivates = false;
                    if (this.bluetoothScoActive || this.bluetoothScoConnecting) {
                        am.stopBluetoothSco();
                        this.bluetoothScoActive = false;
                        this.bluetoothScoConnecting = false;
                    }
                    am.setBluetoothScoOn(false);
                    am.setSpeakerphoneOn(true);
                    this.audioRouteToSet = 1;
                    break;
                case 1:
                    this.needSwitchToBluetoothAfterScoActivates = false;
                    if (this.bluetoothScoActive || this.bluetoothScoConnecting) {
                        am.stopBluetoothSco();
                        this.bluetoothScoActive = false;
                        this.bluetoothScoConnecting = false;
                    }
                    am.setSpeakerphoneOn(false);
                    am.setBluetoothScoOn(false);
                    this.audioRouteToSet = 0;
                    break;
                case 2:
                    if (!this.bluetoothScoActive) {
                        this.needSwitchToBluetoothAfterScoActivates = true;
                        try {
                            am.startBluetoothSco();
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                    } else {
                        am.setBluetoothScoOn(true);
                        am.setSpeakerphoneOn(false);
                    }
                    this.audioRouteToSet = 2;
                    break;
            }
            updateOutputGainControlState();
        } else {
            switch (which) {
                case 0:
                    this.audioRouteToSet = 1;
                    this.speakerphoneStateToSet = true;
                    break;
                case 1:
                    this.audioRouteToSet = 0;
                    this.speakerphoneStateToSet = false;
                    break;
                case 2:
                    this.audioRouteToSet = 2;
                    this.speakerphoneStateToSet = false;
                    break;
            }
        }
        Iterator<StateListener> it = this.stateListeners.iterator();
        while (it.hasNext()) {
            StateListener l = it.next();
            l.onAudioSettingsChanged();
        }
    }

    public boolean isSpeakerphoneOn() {
        CallConnection callConnection;
        boolean z = USE_CONNECTION_SERVICE;
        if (z && (callConnection = this.systemCallConnection) != null && callConnection.getCallAudioState() != null) {
            int route = this.systemCallConnection.getCallAudioState().getRoute();
            if (hasEarpiece()) {
                if (route == 8) {
                    return true;
                }
            } else if (route == 2) {
                return true;
            }
            return false;
        } else if (this.audioConfigured && !z) {
            AudioManager am = (AudioManager) getSystemService("audio");
            return hasEarpiece() ? am.isSpeakerphoneOn() : am.isBluetoothScoOn();
        } else {
            return this.speakerphoneStateToSet;
        }
    }

    public int getCurrentAudioRoute() {
        if (USE_CONNECTION_SERVICE) {
            CallConnection callConnection = this.systemCallConnection;
            if (callConnection != null && callConnection.getCallAudioState() != null) {
                switch (this.systemCallConnection.getCallAudioState().getRoute()) {
                    case 1:
                    case 4:
                        return 0;
                    case 2:
                        return 2;
                    case 8:
                        return 1;
                }
            }
            return this.audioRouteToSet;
        } else if (this.audioConfigured) {
            AudioManager am = (AudioManager) getSystemService("audio");
            if (am.isBluetoothScoOn()) {
                return 2;
            }
            return am.isSpeakerphoneOn() ? 1 : 0;
        } else {
            return this.audioRouteToSet;
        }
    }

    public String getDebugString() {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        return nativeInstanceArr[0] != null ? nativeInstanceArr[0].getDebugInfo() : "";
    }

    public long getCallDuration() {
        if (this.callStartTime == 0) {
            return 0L;
        }
        return SystemClock.elapsedRealtime() - this.callStartTime;
    }

    public void stopRinging() {
        MediaPlayer mediaPlayer = this.ringtonePlayer;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            this.ringtonePlayer.release();
            this.ringtonePlayer = null;
        }
        Vibrator vibrator = this.vibrator;
        if (vibrator != null) {
            vibrator.cancel();
            this.vibrator = null;
        }
    }

    private void showNotification(String name, Bitmap photo) {
        String str;
        int i;
        String str2;
        int i2;
        Intent intent = new Intent(this, LaunchActivity.class).setAction(this.groupCall != null ? "voip_chat" : "voip");
        if (this.groupCall != null) {
            intent.putExtra("currentAccount", this.currentAccount);
        }
        Notification.Builder builder = new Notification.Builder(this).setContentText(name).setContentIntent(PendingIntent.getActivity(this, 50, intent, 0));
        if (this.groupCall != null) {
            if (ChatObject.isChannelOrGiga(this.chat)) {
                i2 = R.string.VoipLiveStream;
                str2 = "VoipLiveStream";
            } else {
                i2 = R.string.VoipVoiceChat;
                str2 = "VoipVoiceChat";
            }
            builder.setContentTitle(LocaleController.getString(str2, i2));
            builder.setSmallIcon(isMicMute() ? R.drawable.voicechat_muted : R.drawable.voicechat_active);
        } else {
            builder.setContentTitle(LocaleController.getString("VoipOutgoingCall", R.string.VoipOutgoingCall));
            builder.setSmallIcon(R.drawable.notification);
        }
        if (Build.VERSION.SDK_INT >= 16) {
            Intent endIntent = new Intent(this, VoIPActionsReceiver.class);
            endIntent.setAction(getPackageName() + ".END_CALL");
            if (this.groupCall == null) {
                builder.addAction(R.drawable.ic_call_end_white_24dp, LocaleController.getString("VoipEndCall", R.string.VoipEndCall), PendingIntent.getBroadcast(this, 0, endIntent, 134217728));
            } else {
                if (ChatObject.isChannelOrGiga(this.chat)) {
                    i = R.string.VoipChannelLeaveAlertTitle;
                    str = "VoipChannelLeaveAlertTitle";
                } else {
                    i = R.string.VoipGroupLeaveAlertTitle;
                    str = "VoipGroupLeaveAlertTitle";
                }
                builder.addAction(R.drawable.ic_call_end_white_24dp, LocaleController.getString(str, i), PendingIntent.getBroadcast(this, 0, endIntent, 134217728));
            }
            builder.setPriority(2);
        }
        if (Build.VERSION.SDK_INT >= 17) {
            builder.setShowWhen(false);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            builder.setColor(-14143951);
            builder.setColorized(true);
        } else if (Build.VERSION.SDK_INT >= 21) {
            builder.setColor(-13851168);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationsController.checkOtherNotificationsChannel();
            builder.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
        }
        if (photo != null) {
            builder.setLargeIcon(photo);
        }
        try {
            startForeground(201, builder.getNotification());
        } catch (Exception e) {
            if (photo != null && (e instanceof IllegalArgumentException)) {
                showNotification(name, null);
            }
        }
    }

    private void startRingtoneAndVibration(long chatID) {
        int vibrate;
        String notificationUri;
        SharedPreferences prefs = MessagesController.getNotificationsSettings(this.currentAccount);
        AudioManager am = (AudioManager) getSystemService("audio");
        boolean needRing = am.getRingerMode() != 0;
        if (needRing) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            this.ringtonePlayer = mediaPlayer;
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda11
                @Override // android.media.MediaPlayer.OnPreparedListener
                public final void onPrepared(MediaPlayer mediaPlayer2) {
                    VoIPService.this.m1356xd4dc8460(mediaPlayer2);
                }
            });
            this.ringtonePlayer.setLooping(true);
            if (this.isHeadsetPlugged) {
                this.ringtonePlayer.setAudioStreamType(0);
            } else {
                this.ringtonePlayer.setAudioStreamType(2);
                if (!USE_CONNECTION_SERVICE) {
                    am.requestAudioFocus(this, 2, 1);
                }
            }
            try {
                if (prefs.getBoolean(ContentMetadata.KEY_CUSTOM_PREFIX + chatID, false)) {
                    notificationUri = prefs.getString("ringtone_path_" + chatID, RingtoneManager.getDefaultUri(1).toString());
                } else {
                    notificationUri = prefs.getString("CallsRingtonePath", RingtoneManager.getDefaultUri(1).toString());
                }
                this.ringtonePlayer.setDataSource(this, Uri.parse(notificationUri));
                this.ringtonePlayer.prepareAsync();
            } catch (Exception e) {
                FileLog.e(e);
                MediaPlayer mediaPlayer2 = this.ringtonePlayer;
                if (mediaPlayer2 != null) {
                    mediaPlayer2.release();
                    this.ringtonePlayer = null;
                }
            }
            if (prefs.getBoolean(ContentMetadata.KEY_CUSTOM_PREFIX + chatID, false)) {
                vibrate = prefs.getInt("calls_vibrate_" + chatID, 0);
            } else {
                vibrate = prefs.getInt("vibrate_calls", 0);
            }
            if ((vibrate != 2 && vibrate != 4 && (am.getRingerMode() == 1 || am.getRingerMode() == 2)) || (vibrate == 4 && am.getRingerMode() == 1)) {
                Vibrator vibrator = (Vibrator) getSystemService("vibrator");
                this.vibrator = vibrator;
                long duration = 700;
                if (vibrate == 1) {
                    duration = 700 / 2;
                } else if (vibrate == 3) {
                    duration = 700 * 2;
                }
                vibrator.vibrate(new long[]{0, duration, 500}, 0);
            }
        }
    }

    /* renamed from: lambda$startRingtoneAndVibration$63$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1356xd4dc8460(MediaPlayer mediaPlayer) {
        try {
            this.ringtonePlayer.start();
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("=============== VoIPService STOPPING ===============");
        }
        stopForeground(true);
        stopRinging();
        if (this.currentAccount >= 0) {
            if (ApplicationLoader.mainInterfacePaused || !ApplicationLoader.isScreenOn) {
                MessagesController.getInstance(this.currentAccount).ignoreSetOnline = false;
            }
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.appDidLogout);
        }
        SensorManager sm = (SensorManager) getSystemService("sensor");
        Sensor proximity = sm.getDefaultSensor(8);
        if (proximity != null) {
            sm.unregisterListener(this);
        }
        PowerManager.WakeLock wakeLock = this.proximityWakelock;
        if (wakeLock != null && wakeLock.isHeld()) {
            this.proximityWakelock.release();
        }
        if (this.updateNotificationRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.updateNotificationRunnable);
            this.updateNotificationRunnable = null;
        }
        Runnable runnable = this.switchingStreamTimeoutRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.switchingStreamTimeoutRunnable = null;
        }
        unregisterReceiver(this.receiver);
        Runnable runnable2 = this.timeoutRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.timeoutRunnable = null;
        }
        super.onDestroy();
        sharedInstance = null;
        Arrays.fill(this.mySource, 0);
        cancelGroupCheckShortPoll();
        AndroidUtilities.runOnUIThread(VoIPService$$ExternalSyntheticLambda57.INSTANCE);
        if (this.tgVoip[0] != null) {
            StatsController.getInstance(this.currentAccount).incrementTotalCallsTime(getStatsNetworkType(), ((int) (getCallDuration() / 1000)) % 5);
            onTgVoipPreStop();
            if (this.tgVoip[0].isGroup()) {
                NativeInstance instance = this.tgVoip[0];
                DispatchQueue dispatchQueue = Utilities.globalQueue;
                instance.getClass();
                dispatchQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda44(instance));
                for (Map.Entry<String, Integer> entry : this.currentStreamRequestTimestamp.entrySet()) {
                    AccountInstance.getInstance(this.currentAccount).getConnectionsManager().cancelRequest(entry.getValue().intValue(), true);
                }
                this.currentStreamRequestTimestamp.clear();
            } else {
                Instance.FinalState state = this.tgVoip[0].stop();
                updateTrafficStats(this.tgVoip[0], state.trafficStats);
                onTgVoipStop(state);
            }
            this.prevTrafficStats = null;
            this.callStartTime = 0L;
            this.tgVoip[0] = null;
            Instance.destroyInstance();
        }
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[1] != null) {
            NativeInstance instance2 = nativeInstanceArr[1];
            DispatchQueue dispatchQueue2 = Utilities.globalQueue;
            instance2.getClass();
            dispatchQueue2.postRunnable(new VoIPService$$ExternalSyntheticLambda44(instance2));
            this.tgVoip[1] = null;
        }
        int a = 0;
        while (true) {
            long[] jArr = this.captureDevice;
            if (a >= jArr.length) {
                break;
            }
            if (jArr[a] != 0) {
                if (this.destroyCaptureDevice[a]) {
                    NativeInstance.destroyVideoCapturer(jArr[a]);
                }
                this.captureDevice[a] = 0;
            }
            a++;
        }
        this.cpuWakelock.release();
        if (!this.playingSound) {
            final AudioManager am = (AudioManager) getSystemService("audio");
            if (!USE_CONNECTION_SERVICE) {
                if (this.isBtHeadsetConnected || this.bluetoothScoActive || this.bluetoothScoConnecting) {
                    am.stopBluetoothSco();
                    am.setBluetoothScoOn(false);
                    am.setSpeakerphoneOn(false);
                    this.bluetoothScoActive = false;
                    this.bluetoothScoConnecting = false;
                }
                if (this.onDestroyRunnable == null) {
                    DispatchQueue dispatchQueue3 = Utilities.globalQueue;
                    Runnable runnable3 = new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda22
                        @Override // java.lang.Runnable
                        public final void run() {
                            VoIPService.lambda$onDestroy$65(am);
                        }
                    };
                    setModeRunnable = runnable3;
                    dispatchQueue3.postRunnable(runnable3);
                }
                am.abandonAudioFocus(this);
            }
            am.unregisterMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));
            if (this.hasAudioFocus) {
                am.abandonAudioFocus(this);
            }
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.m1328lambda$onDestroy$66$orgtelegrammessengervoipVoIPService();
                }
            });
        }
        if (USE_CONNECTION_SERVICE) {
            if (!this.didDeleteConnectionServiceContact) {
                ContactsController.getInstance(this.currentAccount).deleteConnectionServiceContact();
            }
            CallConnection callConnection = this.systemCallConnection;
            if (callConnection != null && !this.playingSound) {
                callConnection.destroy();
            }
        }
        VoIPHelper.lastCallTime = SystemClock.elapsedRealtime();
        setSinks(null, null);
        Runnable runnable4 = this.onDestroyRunnable;
        if (runnable4 != null) {
            runnable4.run();
        }
        int i = this.currentAccount;
        if (i >= 0) {
            ConnectionsManager.getInstance(i).setAppPaused(true, false);
            if (ChatObject.isChannel(this.chat)) {
                MessagesController.getInstance(this.currentAccount).startShortPoll(this.chat, this.classGuid, true);
            }
        }
    }

    public static /* synthetic */ void lambda$onDestroy$65(AudioManager am) {
        synchronized (sync) {
            if (setModeRunnable == null) {
                return;
            }
            setModeRunnable = null;
            try {
                am.setMode(0);
            } catch (SecurityException x) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error setting audio more to normal", x);
                }
            }
        }
    }

    /* renamed from: lambda$onDestroy$66$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1328lambda$onDestroy$66$orgtelegrammessengervoipVoIPService() {
        this.soundPool.release();
    }

    public long getCallID() {
        TLRPC.PhoneCall phoneCall = this.privateCall;
        if (phoneCall != null) {
            return phoneCall.id;
        }
        return 0L;
    }

    public void hangUp() {
        hangUp(0, null);
    }

    public void hangUp(int discard) {
        hangUp(discard, null);
    }

    public void hangUp(Runnable onDone) {
        hangUp(0, onDone);
    }

    public void acceptIncomingCall() {
        MessagesController.getInstance(this.currentAccount).ignoreSetOnline = false;
        stopRinging();
        showNotification();
        configureDeviceForCall();
        startConnectingSound();
        dispatchStateChanged(12);
        AndroidUtilities.runOnUIThread(VoIPService$$ExternalSyntheticLambda54.INSTANCE);
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        TLRPC.TL_messages_getDhConfig req = new TLRPC.TL_messages_getDhConfig();
        req.random_length = 256;
        req.version = messagesStorage.getLastSecretVersion();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda87
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                VoIPService.this.m1285x4c614975(messagesStorage, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$acceptIncomingCall$70$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1285x4c614975(MessagesStorage messagesStorage, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.messages_DhConfig res = (TLRPC.messages_DhConfig) response;
            if (response instanceof TLRPC.TL_messages_dhConfig) {
                if (!Utilities.isGoodPrime(res.p, res.g)) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("stopping VoIP service, bad prime");
                    }
                    callFailed();
                    return;
                }
                messagesStorage.setSecretPBytes(res.p);
                messagesStorage.setSecretG(res.g);
                messagesStorage.setLastSecretVersion(res.version);
                MessagesStorage.getInstance(this.currentAccount).saveSecretParams(messagesStorage.getLastSecretVersion(), messagesStorage.getSecretG(), messagesStorage.getSecretPBytes());
            }
            byte[] salt = new byte[256];
            for (int a = 0; a < 256; a++) {
                salt[a] = (byte) (((byte) (Utilities.random.nextDouble() * 256.0d)) ^ res.random[a]);
            }
            if (this.privateCall == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("call is null");
                }
                callFailed();
                return;
            }
            this.a_or_b = salt;
            BigInteger g_b = BigInteger.valueOf(messagesStorage.getSecretG());
            BigInteger p = new BigInteger(1, messagesStorage.getSecretPBytes());
            BigInteger g_b2 = g_b.modPow(new BigInteger(1, salt), p);
            this.g_a_hash = this.privateCall.g_a_hash;
            byte[] g_b_bytes = g_b2.toByteArray();
            if (g_b_bytes.length > 256) {
                byte[] correctedAuth = new byte[256];
                System.arraycopy(g_b_bytes, 1, correctedAuth, 0, 256);
                g_b_bytes = correctedAuth;
            }
            TLRPC.TL_phone_acceptCall req1 = new TLRPC.TL_phone_acceptCall();
            req1.g_b = g_b_bytes;
            req1.peer = new TLRPC.TL_inputPhoneCall();
            req1.peer.id = this.privateCall.id;
            req1.peer.access_hash = this.privateCall.access_hash;
            req1.protocol = new TLRPC.TL_phoneCallProtocol();
            TLRPC.TL_phoneCallProtocol tL_phoneCallProtocol = req1.protocol;
            req1.protocol.udp_reflector = true;
            tL_phoneCallProtocol.udp_p2p = true;
            req1.protocol.min_layer = 65;
            req1.protocol.max_layer = Instance.getConnectionMaxLayer();
            req1.protocol.library_versions.addAll(Instance.AVAILABLE_VERSIONS);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req1, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda75
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    VoIPService.this.m1284x369d975f(tLObject, tL_error);
                }
            }, 2);
            return;
        }
        callFailed();
    }

    /* renamed from: lambda$acceptIncomingCall$69$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1284x369d975f(final TLObject response1, final TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda47
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1283xa9fd6c5e(error1, response1);
            }
        });
    }

    /* renamed from: lambda$acceptIncomingCall$68$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1283xa9fd6c5e(TLRPC.TL_error error1, TLObject response1) {
        if (error1 == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("accept call ok! " + response1);
            }
            TLRPC.PhoneCall phoneCall = ((TLRPC.TL_phone_phoneCall) response1).phone_call;
            this.privateCall = phoneCall;
            if (phoneCall instanceof TLRPC.TL_phoneCallDiscarded) {
                onCallUpdated(phoneCall);
                return;
            }
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e("Error on phone.acceptCall: " + error1);
        }
        callFailed();
    }

    public void declineIncomingCall(int reason, Runnable onDone) {
        stopRinging();
        this.callDiscardReason = reason;
        int i = this.currentState;
        if (i == 14) {
            Runnable runnable = this.delayedStartOutgoingCall;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                callEnded();
                return;
            }
            dispatchStateChanged(10);
            this.endCallAfterRequest = true;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.m1310x29148762();
                }
            }, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        } else if (i == 10 || i == 11) {
        } else {
            dispatchStateChanged(10);
            if (this.privateCall == null) {
                this.onDestroyRunnable = onDone;
                callEnded();
                if (this.callReqId != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.callReqId, false);
                    this.callReqId = 0;
                    return;
                }
                return;
            }
            TLRPC.TL_phone_discardCall req = new TLRPC.TL_phone_discardCall();
            req.peer = new TLRPC.TL_inputPhoneCall();
            req.peer.access_hash = this.privateCall.access_hash;
            req.peer.id = this.privateCall.id;
            req.duration = (int) (getCallDuration() / 1000);
            NativeInstance[] nativeInstanceArr = this.tgVoip;
            req.connection_id = nativeInstanceArr[0] != null ? nativeInstanceArr[0].getPreferredRelayId() : 0L;
            switch (reason) {
                case 2:
                    req.reason = new TLRPC.TL_phoneCallDiscardReasonDisconnect();
                    break;
                case 3:
                    req.reason = new TLRPC.TL_phoneCallDiscardReasonMissed();
                    break;
                case 4:
                    req.reason = new TLRPC.TL_phoneCallDiscardReasonBusy();
                    break;
                default:
                    req.reason = new TLRPC.TL_phoneCallDiscardReasonHangup();
                    break;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda76
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    VoIPService.this.m1311xb5b4b263(tLObject, tL_error);
                }
            }, 2);
            this.onDestroyRunnable = onDone;
            callEnded();
        }
    }

    /* renamed from: lambda$declineIncomingCall$71$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1310x29148762() {
        if (this.currentState == 10) {
            callEnded();
        }
    }

    /* renamed from: lambda$declineIncomingCall$72$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1311xb5b4b263(TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("error on phone.discardCall: " + error);
                return;
            }
            return;
        }
        if (response instanceof TLRPC.TL_updates) {
            TLRPC.TL_updates updates = (TLRPC.TL_updates) response;
            MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("phone.discardCall " + response);
        }
    }

    public void declineIncomingCall() {
        declineIncomingCall(1, null);
    }

    private Class<? extends Activity> getUIActivityClass() {
        return LaunchActivity.class;
    }

    public CallConnection getConnectionAndStartCall() {
        if (this.systemCallConnection == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("creating call connection");
            }
            CallConnection callConnection = new CallConnection();
            this.systemCallConnection = callConnection;
            callConnection.setInitializing();
            if (this.isOutgoing) {
                Runnable runnable = new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        VoIPService.this.m1314xa7dddac1();
                    }
                };
                this.delayedStartOutgoingCall = runnable;
                AndroidUtilities.runOnUIThread(runnable, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            }
            CallConnection callConnection2 = this.systemCallConnection;
            callConnection2.setAddress(Uri.fromParts("tel", "+99084" + this.user.id, null), 1);
            this.systemCallConnection.setCallerDisplayName(ContactsController.formatName(this.user.first_name, this.user.last_name), 1);
        }
        return this.systemCallConnection;
    }

    /* renamed from: lambda$getConnectionAndStartCall$73$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1314xa7dddac1() {
        this.delayedStartOutgoingCall = null;
        startOutgoingCall();
    }

    public void startRinging() {
        CallConnection callConnection;
        if (this.currentState == 15) {
            return;
        }
        if (USE_CONNECTION_SERVICE && (callConnection = this.systemCallConnection) != null) {
            callConnection.setRinging();
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("starting ringing for call " + this.privateCall.id);
        }
        dispatchStateChanged(15);
        if (!this.notificationsDisabled && Build.VERSION.SDK_INT >= 21) {
            showIncomingNotification(ContactsController.formatName(this.user.first_name, this.user.last_name), null, this.user, this.privateCall.video, 0);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("Showing incoming call notification");
                return;
            }
            return;
        }
        startRingtoneAndVibration(this.user.id);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Starting incall activity for incoming call");
        }
        try {
            PendingIntent.getActivity(this, 12345, new Intent(this, LaunchActivity.class).setAction("voip"), 0).send();
        } catch (Exception x) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Error starting incall activity", x);
            }
        }
    }

    public void startRingtoneAndVibration() {
        if (!this.startedRinging) {
            startRingtoneAndVibration(this.user.id);
            this.startedRinging = true;
        }
    }

    private void updateServerConfig() {
        final SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
        Instance.setGlobalServerConfig(preferences.getString("voip_server_config", "{}"));
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_phone_getCallConfig(), new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda74
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                VoIPService.lambda$updateServerConfig$74(preferences, tLObject, tL_error);
            }
        });
    }

    public static /* synthetic */ void lambda$updateServerConfig$74(SharedPreferences preferences, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            String data = ((TLRPC.TL_dataJSON) response).data;
            Instance.setGlobalServerConfig(data);
            preferences.edit().putString("voip_server_config", data).commit();
        }
    }

    private void showNotification() {
        TLRPC.User user = this.user;
        if (user != null) {
            showNotification(ContactsController.formatName(user.first_name, this.user.last_name), getRoundAvatarBitmap(this.user));
        } else {
            showNotification(this.chat.title, getRoundAvatarBitmap(this.chat));
        }
    }

    private void onTgVoipPreStop() {
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        while (true) {
            String line = reader.readLine();
            if (line != null) {
                sb.append(line);
                sb.append("\n");
            } else {
                reader.close();
                return sb.toString();
            }
        }
    }

    public static String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
    }

    private void onTgVoipStop(Instance.FinalState finalState) {
        if (this.user == null) {
            return;
        }
        if (TextUtils.isEmpty(finalState.debugLog)) {
            try {
                finalState.debugLog = getStringFromFile(VoIPHelper.getLogFilePath(this.privateCall.id, true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (this.needRateCall || this.forceRating || finalState.isRatingSuggested) {
            startRatingActivity();
            this.needRateCall = false;
        }
        if (this.needSendDebugLog && finalState.debugLog != null) {
            TLRPC.TL_phone_saveCallDebug req = new TLRPC.TL_phone_saveCallDebug();
            req.debug = new TLRPC.TL_dataJSON();
            req.debug.data = finalState.debugLog;
            req.peer = new TLRPC.TL_inputPhoneCall();
            req.peer.access_hash = this.privateCall.access_hash;
            req.peer.id = this.privateCall.id;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, VoIPService$$ExternalSyntheticLambda96.INSTANCE);
            this.needSendDebugLog = false;
        }
    }

    public static /* synthetic */ void lambda$onTgVoipStop$75(TLObject response, TLRPC.TL_error error) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Sent debug logs, response = " + response);
        }
    }

    private void initializeAccountRelatedThings() {
        updateServerConfig();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.appDidLogout);
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
    }

    @Override // android.app.Service
    public void onCreate() {
        BluetoothAdapter bluetoothAdapter;
        super.onCreate();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("=============== VoIPService STARTING ===============");
        }
        try {
            AudioManager am = (AudioManager) getSystemService("audio");
            if (Build.VERSION.SDK_INT >= 17 && am.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER") != null) {
                int outFramesPerBuffer = Integer.parseInt(am.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER"));
                Instance.setBufferSize(outFramesPerBuffer);
            } else {
                Instance.setBufferSize(AudioTrack.getMinBufferSize(48000, 4, 2) / 2);
            }
            boolean z = true;
            PowerManager.WakeLock newWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(1, "telegram-voip");
            this.cpuWakelock = newWakeLock;
            newWakeLock.acquire();
            this.btAdapter = am.isBluetoothScoAvailableOffCall() ? BluetoothAdapter.getDefaultAdapter() : null;
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            boolean z2 = USE_CONNECTION_SERVICE;
            if (!z2) {
                filter.addAction(ACTION_HEADSET_PLUG);
                if (this.btAdapter != null) {
                    filter.addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
                    filter.addAction("android.media.ACTION_SCO_AUDIO_STATE_UPDATED");
                }
                filter.addAction("android.intent.action.PHONE_STATE");
                filter.addAction("android.intent.action.SCREEN_ON");
                filter.addAction("android.intent.action.SCREEN_OFF");
            }
            registerReceiver(this.receiver, filter);
            fetchBluetoothDeviceName();
            am.registerMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));
            if (!z2 && (bluetoothAdapter = this.btAdapter) != null && bluetoothAdapter.isEnabled()) {
                MediaRouter mr = (MediaRouter) getSystemService("media_router");
                if (Build.VERSION.SDK_INT < 24) {
                    int headsetState = this.btAdapter.getProfileConnectionState(1);
                    if (headsetState != 2) {
                        z = false;
                    }
                    updateBluetoothHeadsetState(z);
                    Iterator<StateListener> it = this.stateListeners.iterator();
                    while (it.hasNext()) {
                        StateListener l = it.next();
                        l.onAudioSettingsChanged();
                    }
                } else {
                    MediaRouter.RouteInfo ri = mr.getSelectedRoute(1);
                    if (ri.getDeviceType() == 3) {
                        int headsetState2 = this.btAdapter.getProfileConnectionState(1);
                        if (headsetState2 != 2) {
                            z = false;
                        }
                        updateBluetoothHeadsetState(z);
                        Iterator<StateListener> it2 = this.stateListeners.iterator();
                        while (it2.hasNext()) {
                            StateListener l2 = it2.next();
                            l2.onAudioSettingsChanged();
                        }
                    } else {
                        updateBluetoothHeadsetState(false);
                    }
                }
            }
        } catch (Exception x) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("error initializing voip controller", x);
            }
            callFailed();
        }
        if (callIShouldHavePutIntoIntent != null && Build.VERSION.SDK_INT >= 26) {
            NotificationsController.checkOtherNotificationsChannel();
            Notification.Builder bldr = new Notification.Builder(this, NotificationsController.OTHER_NOTIFICATIONS_CHANNEL).setContentTitle(LocaleController.getString("VoipOutgoingCall", R.string.VoipOutgoingCall)).setShowWhen(false);
            if (this.groupCall != null) {
                bldr.setSmallIcon(isMicMute() ? R.drawable.voicechat_muted : R.drawable.voicechat_active);
            } else {
                bldr.setSmallIcon(R.drawable.notification);
            }
            startForeground(201, bldr.build());
        }
    }

    private void loadResources() {
        if (Build.VERSION.SDK_INT >= 21) {
            WebRtcAudioTrack.setAudioTrackUsageAttribute(2);
        }
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1321lambda$loadResources$76$orgtelegrammessengervoipVoIPService();
            }
        });
    }

    /* renamed from: lambda$loadResources$76$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1321lambda$loadResources$76$orgtelegrammessengervoipVoIPService() {
        SoundPool soundPool = new SoundPool(1, 0, 0);
        this.soundPool = soundPool;
        this.spConnectingId = soundPool.load(this, R.raw.voip_connecting, 1);
        this.spRingbackID = this.soundPool.load(this, R.raw.voip_ringback, 1);
        this.spFailedID = this.soundPool.load(this, R.raw.voip_failed, 1);
        this.spEndId = this.soundPool.load(this, R.raw.voip_end, 1);
        this.spBusyId = this.soundPool.load(this, R.raw.voip_busy, 1);
        this.spVoiceChatEndId = this.soundPool.load(this, R.raw.voicechat_leave, 1);
        this.spVoiceChatStartId = this.soundPool.load(this, R.raw.voicechat_join, 1);
        this.spVoiceChatConnecting = this.soundPool.load(this, R.raw.voicechat_connecting, 1);
        this.spAllowTalkId = this.soundPool.load(this, R.raw.voip_onallowtalk, 1);
        this.spStartRecordId = this.soundPool.load(this, R.raw.voip_recordstart, 1);
    }

    private void dispatchStateChanged(int state) {
        CallConnection callConnection;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("== Call " + getCallID() + " state changed to " + state + " ==");
        }
        this.currentState = state;
        if (USE_CONNECTION_SERVICE && state == 3 && (callConnection = this.systemCallConnection) != null) {
            callConnection.setActive();
        }
        for (int a = 0; a < this.stateListeners.size(); a++) {
            StateListener l = this.stateListeners.get(a);
            l.onStateChanged(state);
        }
    }

    public void updateTrafficStats(NativeInstance instance, Instance.TrafficStats trafficStats) {
        Instance.TrafficStats trafficStats2;
        if (trafficStats != null) {
            trafficStats2 = trafficStats;
        } else {
            trafficStats2 = instance.getTrafficStats();
        }
        long j = trafficStats2.bytesSentWifi;
        Instance.TrafficStats trafficStats3 = this.prevTrafficStats;
        long wifiSentDiff = j - (trafficStats3 != null ? trafficStats3.bytesSentWifi : 0L);
        long j2 = trafficStats2.bytesReceivedWifi;
        Instance.TrafficStats trafficStats4 = this.prevTrafficStats;
        long wifiRecvdDiff = j2 - (trafficStats4 != null ? trafficStats4.bytesReceivedWifi : 0L);
        long j3 = trafficStats2.bytesSentMobile;
        Instance.TrafficStats trafficStats5 = this.prevTrafficStats;
        long mobileSentDiff = j3 - (trafficStats5 != null ? trafficStats5.bytesSentMobile : 0L);
        long j4 = trafficStats2.bytesReceivedMobile;
        Instance.TrafficStats trafficStats6 = this.prevTrafficStats;
        long mobileRecvdDiff = j4 - (trafficStats6 != null ? trafficStats6.bytesReceivedMobile : 0L);
        this.prevTrafficStats = trafficStats2;
        if (wifiSentDiff > 0) {
            StatsController.getInstance(this.currentAccount).incrementSentBytesCount(1, 0, wifiSentDiff);
        }
        if (wifiRecvdDiff > 0) {
            StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(1, 0, wifiRecvdDiff);
        }
        int i = 2;
        if (mobileSentDiff > 0) {
            StatsController statsController = StatsController.getInstance(this.currentAccount);
            NetworkInfo networkInfo = this.lastNetInfo;
            statsController.incrementSentBytesCount((networkInfo == null || !networkInfo.isRoaming()) ? 0 : 2, 0, mobileSentDiff);
        }
        if (mobileRecvdDiff > 0) {
            StatsController statsController2 = StatsController.getInstance(this.currentAccount);
            NetworkInfo networkInfo2 = this.lastNetInfo;
            if (networkInfo2 == null || !networkInfo2.isRoaming()) {
                i = 0;
            }
            statsController2.incrementReceivedBytesCount(i, 0, mobileRecvdDiff);
        }
    }

    private void configureDeviceForCall() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("configureDeviceForCall, route to set = " + this.audioRouteToSet);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            WebRtcAudioTrack.setAudioTrackUsageAttribute(hasRtmpStream() ? 1 : 2);
            WebRtcAudioTrack.setAudioStreamType(hasRtmpStream() ? Integer.MIN_VALUE : 0);
        }
        this.needPlayEndSound = true;
        final AudioManager am = (AudioManager) getSystemService("audio");
        if (!USE_CONNECTION_SERVICE) {
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda38
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.m1295x462c7c1f(am);
                }
            });
        }
        SensorManager sm = (SensorManager) getSystemService("sensor");
        Sensor proximity = sm.getDefaultSensor(8);
        if (proximity != null) {
            try {
                this.proximityWakelock = ((PowerManager) getSystemService("power")).newWakeLock(32, "telegram-voip-prx");
                sm.registerListener(this, proximity, 3);
            } catch (Exception x) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error initializing proximity sensor", x);
                }
            }
        }
    }

    /* renamed from: lambda$configureDeviceForCall$79$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1295x462c7c1f(final AudioManager am) {
        try {
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (hasRtmpStream()) {
            am.setMode(0);
            am.setBluetoothScoOn(false);
            AndroidUtilities.runOnUIThread(VoIPService$$ExternalSyntheticLambda56.INSTANCE);
            return;
        }
        am.setMode(3);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda37
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1294xb98c511e(am);
            }
        });
    }

    public static /* synthetic */ void lambda$configureDeviceForCall$77() {
        if (!MediaController.getInstance().isMessagePaused()) {
            MediaController.getInstance().m383lambda$startAudioAgain$7$orgtelegrammessengerMediaController(MediaController.getInstance().getPlayingMessageObject());
        }
    }

    /* renamed from: lambda$configureDeviceForCall$78$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1294xb98c511e(AudioManager am) {
        am.requestAudioFocus(this, 0, 1);
        if (isBluetoothHeadsetConnected() && hasEarpiece()) {
            switch (this.audioRouteToSet) {
                case 0:
                    am.setBluetoothScoOn(false);
                    am.setSpeakerphoneOn(false);
                    break;
                case 1:
                    am.setBluetoothScoOn(false);
                    am.setSpeakerphoneOn(true);
                    break;
                case 2:
                    if (!this.bluetoothScoActive) {
                        this.needSwitchToBluetoothAfterScoActivates = true;
                        try {
                            am.startBluetoothSco();
                            break;
                        } catch (Throwable e) {
                            FileLog.e(e);
                            break;
                        }
                    } else {
                        am.setBluetoothScoOn(true);
                        am.setSpeakerphoneOn(false);
                        break;
                    }
            }
        } else if (isBluetoothHeadsetConnected()) {
            am.setBluetoothScoOn(this.speakerphoneStateToSet);
        } else {
            am.setSpeakerphoneOn(this.speakerphoneStateToSet);
            if (this.speakerphoneStateToSet) {
                this.audioRouteToSet = 1;
            } else {
                this.audioRouteToSet = 0;
            }
        }
        updateOutputGainControlState();
        this.audioConfigured = true;
    }

    public void fetchBluetoothDeviceName() {
        if (this.fetchingBluetoothDeviceName) {
            return;
        }
        try {
            this.currentBluetoothDeviceName = null;
            this.fetchingBluetoothDeviceName = true;
            BluetoothAdapter.getDefaultAdapter().getProfileProxy(this, this.serviceListener, 1);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent event) {
        if (this.unmutedByHold || this.remoteVideoState == 2) {
            return;
        }
        boolean z = false;
        if (this.videoState[0] != 2 && event.sensor.getType() == 8) {
            AudioManager am = (AudioManager) getSystemService("audio");
            if (this.audioRouteToSet != 0 || this.isHeadsetPlugged || am.isSpeakerphoneOn()) {
                return;
            }
            if (isBluetoothHeadsetConnected() && am.isBluetoothScoOn()) {
                return;
            }
            if (event.values[0] < Math.min(event.sensor.getMaximumRange(), 3.0f)) {
                z = true;
            }
            boolean newIsNear = z;
            checkIsNear(newIsNear);
        }
    }

    private void checkIsNear() {
        if (this.remoteVideoState == 2 || this.videoState[0] == 2) {
            checkIsNear(false);
        }
    }

    private void checkIsNear(boolean newIsNear) {
        if (newIsNear != this.isProximityNear) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("proximity " + newIsNear);
            }
            this.isProximityNear = newIsNear;
            try {
                if (newIsNear) {
                    this.proximityWakelock.acquire();
                } else {
                    this.proximityWakelock.release(1);
                }
            } catch (Exception x) {
                FileLog.e(x);
            }
        }
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public boolean isBluetoothHeadsetConnected() {
        CallConnection callConnection;
        if (!USE_CONNECTION_SERVICE || (callConnection = this.systemCallConnection) == null || callConnection.getCallAudioState() == null) {
            return this.isBtHeadsetConnected;
        }
        return (this.systemCallConnection.getCallAudioState().getSupportedRouteMask() & 2) != 0;
    }

    @Override // android.media.AudioManager.OnAudioFocusChangeListener
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == 1) {
            this.hasAudioFocus = true;
        } else {
            this.hasAudioFocus = false;
        }
    }

    public void updateBluetoothHeadsetState(boolean connected) {
        if (connected == this.isBtHeadsetConnected) {
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("updateBluetoothHeadsetState: " + connected);
        }
        this.isBtHeadsetConnected = connected;
        final AudioManager am = (AudioManager) getSystemService("audio");
        if (connected && !isRinging() && this.currentState != 0) {
            if (this.bluetoothScoActive) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("SCO already active, setting audio routing");
                }
                if (!hasRtmpStream()) {
                    am.setSpeakerphoneOn(false);
                    am.setBluetoothScoOn(true);
                }
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("startBluetoothSco");
                }
                if (!hasRtmpStream()) {
                    this.needSwitchToBluetoothAfterScoActivates = true;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda33
                        @Override // java.lang.Runnable
                        public final void run() {
                            am.startBluetoothSco();
                        }
                    }, 500L);
                }
            }
        } else {
            this.bluetoothScoActive = false;
            this.bluetoothScoConnecting = false;
        }
        Iterator<StateListener> it = this.stateListeners.iterator();
        while (it.hasNext()) {
            StateListener l = it.next();
            l.onAudioSettingsChanged();
        }
    }

    public String getLastError() {
        return this.lastError;
    }

    public int getCallState() {
        return this.currentState;
    }

    public TLRPC.InputPeer getGroupCallPeer() {
        return this.groupCallPeer;
    }

    public void updateNetworkType() {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[0] != null) {
            if (!nativeInstanceArr[0].isGroup()) {
                this.tgVoip[0].setNetworkType(getNetworkType());
                return;
            }
            return;
        }
        this.lastNetInfo = getActiveNetworkInfo();
    }

    private int getNetworkType() {
        NetworkInfo info = getActiveNetworkInfo();
        this.lastNetInfo = info;
        if (info == null) {
            return 0;
        }
        switch (info.getType()) {
            case 0:
                switch (info.getSubtype()) {
                    case 1:
                        return 1;
                    case 2:
                    case 7:
                        return 2;
                    case 3:
                    case 5:
                        return 3;
                    case 4:
                    case 11:
                    case 14:
                    default:
                        return 11;
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    case 12:
                    case 15:
                        return 4;
                    case 13:
                        return 5;
                }
            case 1:
                return 6;
            case 9:
                return 7;
            default:
                return 0;
        }
    }

    private NetworkInfo getActiveNetworkInfo() {
        return ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
    }

    public void callFailed() {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        callFailed(nativeInstanceArr[0] != null ? nativeInstanceArr[0].getLastError() : Instance.ERROR_UNKNOWN);
    }

    private Bitmap getRoundAvatarBitmap(TLObject userOrChat) {
        AvatarDrawable placeholder;
        Bitmap bitmap = null;
        try {
            if (userOrChat instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) userOrChat;
                if (user.photo != null && user.photo.photo_small != null) {
                    BitmapDrawable img = ImageLoader.getInstance().getImageFromMemory(user.photo.photo_small, null, "50_50");
                    if (img != null) {
                        bitmap = img.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
                    } else {
                        BitmapFactory.Options opts = new BitmapFactory.Options();
                        opts.inMutable = true;
                        bitmap = BitmapFactory.decodeFile(FileLoader.getInstance(this.currentAccount).getPathToAttach(user.photo.photo_small, true).toString(), opts);
                    }
                }
            } else {
                TLRPC.Chat chat = (TLRPC.Chat) userOrChat;
                if (chat.photo != null && chat.photo.photo_small != null) {
                    BitmapDrawable img2 = ImageLoader.getInstance().getImageFromMemory(chat.photo.photo_small, null, "50_50");
                    if (img2 != null) {
                        bitmap = img2.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
                    } else {
                        BitmapFactory.Options opts2 = new BitmapFactory.Options();
                        opts2.inMutable = true;
                        bitmap = BitmapFactory.decodeFile(FileLoader.getInstance(this.currentAccount).getPathToAttach(chat.photo.photo_small, true).toString(), opts2);
                    }
                }
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
        if (bitmap == null) {
            Theme.createDialogsResources(this);
            if (userOrChat instanceof TLRPC.User) {
                placeholder = new AvatarDrawable((TLRPC.User) userOrChat);
            } else {
                placeholder = new AvatarDrawable((TLRPC.Chat) userOrChat);
            }
            bitmap = Bitmap.createBitmap(AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f), Bitmap.Config.ARGB_8888);
            placeholder.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            placeholder.draw(new Canvas(bitmap));
        }
        Canvas canvas = new Canvas(bitmap);
        Path circlePath = new Path();
        circlePath.addCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, Path.Direction.CW);
        circlePath.toggleInverseFillType();
        Paint paint = new Paint(1);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPath(circlePath, paint);
        return bitmap;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v15 */
    /* JADX WARN: Type inference failed for: r10v16 */
    private void showIncomingNotification(String name, CharSequence subText, TLObject userOrChat, boolean video, int additionalMemberCount) {
        int i;
        int i2;
        Intent intent = new Intent(this, LaunchActivity.class);
        intent.setAction("voip");
        Notification.Builder builder = new Notification.Builder(this).setContentTitle(video ? LocaleController.getString("VoipInVideoCallBranding", R.string.VoipInVideoCallBranding) : LocaleController.getString("VoipInCallBranding", R.string.VoipInCallBranding)).setContentText(name).setSmallIcon(R.drawable.notification).setSubText(subText).setContentIntent(PendingIntent.getActivity(this, 0, intent, 0));
        Uri soundProviderUri = Uri.parse("content://org.telegram.messenger.beta.call_sound_provider/start_ringing");
        if (Build.VERSION.SDK_INT >= 26) {
            SharedPreferences nprefs = MessagesController.getGlobalNotificationsSettings();
            int chanIndex = nprefs.getInt("calls_notification_channel", 0);
            NotificationManager nm = (NotificationManager) getSystemService("notification");
            NotificationChannel oldChannel = nm.getNotificationChannel("incoming_calls2" + chanIndex);
            if (oldChannel != null) {
                nm.deleteNotificationChannel(oldChannel.getId());
            }
            NotificationChannel existingChannel = nm.getNotificationChannel("incoming_calls3" + chanIndex);
            boolean needCreate = true;
            if (existingChannel != null) {
                if (existingChannel.getImportance() < 4 || !soundProviderUri.equals(existingChannel.getSound()) || existingChannel.getVibrationPattern() != null || existingChannel.shouldVibrate()) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("User messed up the notification channel; deleting it and creating a proper one");
                    }
                    nm.deleteNotificationChannel("incoming_calls3" + chanIndex);
                    chanIndex++;
                    nprefs.edit().putInt("calls_notification_channel", chanIndex).commit();
                } else {
                    needCreate = false;
                }
            }
            if (needCreate) {
                AudioAttributes attrs = new AudioAttributes.Builder().setContentType(4).setLegacyStreamType(2).setUsage(2).build();
                NotificationChannel chan = new NotificationChannel("incoming_calls3" + chanIndex, LocaleController.getString("IncomingCalls", R.string.IncomingCalls), 4);
                chan.setSound(soundProviderUri, attrs);
                chan.enableVibration(false);
                chan.enableLights(false);
                chan.setBypassDnd(true);
                try {
                    nm.createNotificationChannel(chan);
                } catch (Exception e) {
                    FileLog.e(e);
                    stopSelf();
                    return;
                }
            }
            builder.setChannelId("incoming_calls3" + chanIndex);
        } else if (Build.VERSION.SDK_INT >= 21) {
            builder.setSound(soundProviderUri, 2);
        }
        Intent endIntent = new Intent(this, VoIPActionsReceiver.class);
        endIntent.setAction(getPackageName() + ".DECLINE_CALL");
        endIntent.putExtra("call_id", getCallID());
        CharSequence endTitle = LocaleController.getString("VoipDeclineCall", R.string.VoipDeclineCall);
        if (Build.VERSION.SDK_INT < 24) {
            i = 0;
        } else {
            endTitle = new SpannableString(endTitle);
            i = 0;
            ((SpannableString) endTitle).setSpan(new ForegroundColorSpan(-769226), 0, endTitle.length(), 0);
        }
        PendingIntent endPendingIntent = PendingIntent.getBroadcast(this, i, endIntent, 268435456);
        builder.addAction(R.drawable.ic_call_end_white_24dp, endTitle, endPendingIntent);
        Intent answerIntent = new Intent(this, VoIPActionsReceiver.class);
        answerIntent.setAction(getPackageName() + ".ANSWER_CALL");
        answerIntent.putExtra("call_id", getCallID());
        CharSequence answerTitle = LocaleController.getString("VoipAnswerCall", R.string.VoipAnswerCall);
        if (Build.VERSION.SDK_INT >= 24) {
            answerTitle = new SpannableString(answerTitle);
            i2 = 0;
            ((SpannableString) answerTitle).setSpan(new ForegroundColorSpan(-16733696), 0, answerTitle.length(), 0);
        } else {
            i2 = 0;
        }
        PendingIntent answerPendingIntent = PendingIntent.getBroadcast(this, i2, answerIntent, 268435456);
        builder.addAction(R.drawable.ic_call, answerTitle, answerPendingIntent);
        builder.setPriority(2);
        if (Build.VERSION.SDK_INT >= 17) {
            builder.setShowWhen(i2);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            builder.setColor(-13851168);
            builder.setVibrate(new long[i2]);
            builder.setCategory(NotificationCompat.CATEGORY_CALL);
            builder.setFullScreenIntent(PendingIntent.getActivity(this, i2, intent, i2), true);
            if (userOrChat instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) userOrChat;
                if (!TextUtils.isEmpty(user.phone)) {
                    builder.addPerson("tel:" + user.phone);
                }
            }
        }
        Notification incomingNotification = builder.getNotification();
        if (Build.VERSION.SDK_INT >= 21) {
            RemoteViews customView = new RemoteViews(getPackageName(), LocaleController.isRTL ? R.layout.call_notification_rtl : R.layout.call_notification);
            customView.setTextViewText(R.id.name, name);
            if (TextUtils.isEmpty(subText)) {
                customView.setViewVisibility(R.id.subtitle, 8);
                if (UserConfig.getActivatedAccountsCount() > 1) {
                    TLRPC.User self = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                    customView.setTextViewText(R.id.title, video ? LocaleController.formatString("VoipInVideoCallBrandingWithName", R.string.VoipInVideoCallBrandingWithName, ContactsController.formatName(self.first_name, self.last_name)) : LocaleController.formatString("VoipInCallBrandingWithName", R.string.VoipInCallBrandingWithName, ContactsController.formatName(self.first_name, self.last_name)));
                } else {
                    customView.setTextViewText(R.id.title, video ? LocaleController.getString("VoipInVideoCallBranding", R.string.VoipInVideoCallBranding) : LocaleController.getString("VoipInCallBranding", R.string.VoipInCallBranding));
                }
            } else {
                if (UserConfig.getActivatedAccountsCount() > 1) {
                    TLRPC.User self2 = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                    customView.setTextViewText(R.id.subtitle, LocaleController.formatString("VoipAnsweringAsAccount", R.string.VoipAnsweringAsAccount, ContactsController.formatName(self2.first_name, self2.last_name)));
                } else {
                    customView.setViewVisibility(R.id.subtitle, 8);
                }
                customView.setTextViewText(R.id.title, subText);
            }
            Bitmap avatar = getRoundAvatarBitmap(userOrChat);
            customView.setTextViewText(R.id.answer_text, LocaleController.getString("VoipAnswerCall", R.string.VoipAnswerCall));
            customView.setTextViewText(R.id.decline_text, LocaleController.getString("VoipDeclineCall", R.string.VoipDeclineCall));
            customView.setImageViewBitmap(R.id.photo, avatar);
            customView.setOnClickPendingIntent(R.id.answer_btn, answerPendingIntent);
            customView.setOnClickPendingIntent(R.id.decline_btn, endPendingIntent);
            builder.setLargeIcon(avatar);
            incomingNotification.bigContentView = customView;
            incomingNotification.headsUpContentView = customView;
        }
        startForeground(202, incomingNotification);
        startRingtoneAndVibration();
    }

    private void callFailed(String error) {
        CallConnection callConnection;
        if (this.privateCall != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("Discarding failed call");
            }
            TLRPC.TL_phone_discardCall req = new TLRPC.TL_phone_discardCall();
            req.peer = new TLRPC.TL_inputPhoneCall();
            req.peer.access_hash = this.privateCall.access_hash;
            req.peer.id = this.privateCall.id;
            req.duration = (int) (getCallDuration() / 1000);
            NativeInstance[] nativeInstanceArr = this.tgVoip;
            req.connection_id = nativeInstanceArr[0] != null ? nativeInstanceArr[0].getPreferredRelayId() : 0L;
            req.reason = new TLRPC.TL_phoneCallDiscardReasonDisconnect();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, VoIPService$$ExternalSyntheticLambda93.INSTANCE);
        }
        try {
            throw new Exception("Call " + getCallID() + " failed with error: " + error);
        } catch (Exception x) {
            FileLog.e(x);
            this.lastError = error;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda99
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.m1292lambda$callFailed$82$orgtelegrammessengervoipVoIPService();
                }
            });
            if (TextUtils.equals(error, Instance.ERROR_LOCALIZED) && this.soundPool != null) {
                this.playingSound = true;
                Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        VoIPService.this.m1293lambda$callFailed$83$orgtelegrammessengervoipVoIPService();
                    }
                });
                AndroidUtilities.runOnUIThread(this.afterSoundRunnable, 1000L);
            }
            if (USE_CONNECTION_SERVICE && (callConnection = this.systemCallConnection) != null) {
                callConnection.setDisconnected(new DisconnectCause(1));
                this.systemCallConnection.destroy();
                this.systemCallConnection = null;
            }
            stopSelf();
        }
    }

    public static /* synthetic */ void lambda$callFailed$81(TLObject response, TLRPC.TL_error error1) {
        if (error1 != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("error on phone.discardCall: " + error1);
            }
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.d("phone.discardCall " + response);
        }
    }

    /* renamed from: lambda$callFailed$82$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1292lambda$callFailed$82$orgtelegrammessengervoipVoIPService() {
        dispatchStateChanged(4);
    }

    /* renamed from: lambda$callFailed$83$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1293lambda$callFailed$83$orgtelegrammessengervoipVoIPService() {
        this.soundPool.play(this.spFailedID, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    public void callFailedFromConnectionService() {
        if (this.isOutgoing) {
            callFailed(Instance.ERROR_CONNECTION_SERVICE);
        } else {
            hangUp();
        }
    }

    @Override // org.telegram.messenger.voip.VoIPController.ConnectionStateListener
    public void onConnectionStateChanged(final int newState, boolean inTransition) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda29
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1327x77725f06(newState);
            }
        });
    }

    /* renamed from: lambda$onConnectionStateChanged$86$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1327x77725f06(int newState) {
        if (newState == 3 && this.callStartTime == 0) {
            this.callStartTime = SystemClock.elapsedRealtime();
        }
        if (newState == 4) {
            callFailed();
            return;
        }
        if (newState == 3) {
            Runnable runnable = this.connectingSoundRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.connectingSoundRunnable = null;
            }
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.m1325x5e320904();
                }
            });
            if (this.groupCall == null && !this.wasEstablished) {
                this.wasEstablished = true;
                if (!this.isProximityNear && !this.privateCall.video) {
                    Vibrator vibrator = (Vibrator) getSystemService("vibrator");
                    if (vibrator.hasVibrator()) {
                        vibrator.vibrate(100L);
                    }
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService.8
                    @Override // java.lang.Runnable
                    public void run() {
                        if (VoIPService.this.tgVoip[0] != null) {
                            StatsController.getInstance(VoIPService.this.currentAccount).incrementTotalCallsTime(VoIPService.this.getStatsNetworkType(), 5);
                            AndroidUtilities.runOnUIThread(this, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                        }
                    }
                }, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                if (this.isOutgoing) {
                    StatsController.getInstance(this.currentAccount).incrementSentItemsCount(getStatsNetworkType(), 0, 1);
                } else {
                    StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(getStatsNetworkType(), 0, 1);
                }
            }
        }
        if (newState == 5) {
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.m1326xead23405();
                }
            });
        }
        dispatchStateChanged(newState);
    }

    /* renamed from: lambda$onConnectionStateChanged$84$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1325x5e320904() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
            this.spPlayId = 0;
        }
    }

    /* renamed from: lambda$onConnectionStateChanged$85$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1326xead23405() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
        }
        this.spPlayId = this.soundPool.play(this.groupCall != null ? this.spVoiceChatConnecting : this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
    }

    /* renamed from: lambda$playStartRecordSound$87$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1334x4a77f457() {
        this.soundPool.play(this.spStartRecordId, 0.5f, 0.5f, 0, 0, 1.0f);
    }

    public void playStartRecordSound() {
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1334x4a77f457();
            }
        });
    }

    /* renamed from: lambda$playAllowTalkSound$88$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1332xb4a30f5a() {
        this.soundPool.play(this.spAllowTalkId, 0.5f, 0.5f, 0, 0, 1.0f);
    }

    public void playAllowTalkSound() {
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1332xb4a30f5a();
            }
        });
    }

    @Override // org.telegram.messenger.voip.VoIPController.ConnectionStateListener
    public void onSignalBarCountChanged(final int newCount) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda30
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1329x96697d72(newCount);
            }
        });
    }

    /* renamed from: lambda$onSignalBarCountChanged$89$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1329x96697d72(int newCount) {
        this.signalBarCount = newCount;
        for (int a = 0; a < this.stateListeners.size(); a++) {
            StateListener l = this.stateListeners.get(a);
            l.onSignalBarsCountChanged(newCount);
        }
    }

    public boolean isBluetoothOn() {
        AudioManager am = (AudioManager) getSystemService("audio");
        return am.isBluetoothScoOn();
    }

    public boolean isBluetoothWillOn() {
        return this.needSwitchToBluetoothAfterScoActivates;
    }

    public boolean isHeadsetPlugged() {
        return this.isHeadsetPlugged;
    }

    private void callEnded() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Call " + getCallID() + " ended");
        }
        if (this.groupCall != null && (!this.playedConnectedSound || this.onDestroyRunnable != null)) {
            this.needPlayEndSound = false;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda55
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1288lambda$callEnded$90$orgtelegrammessengervoipVoIPService();
            }
        });
        int delay = 700;
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda66
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.m1289lambda$callEnded$91$orgtelegrammessengervoipVoIPService();
            }
        });
        Runnable runnable = this.connectingSoundRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.connectingSoundRunnable = null;
        }
        if (this.needPlayEndSound) {
            this.playingSound = true;
            if (this.groupCall == null) {
                Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda77
                    @Override // java.lang.Runnable
                    public final void run() {
                        VoIPService.this.m1290lambda$callEnded$92$orgtelegrammessengervoipVoIPService();
                    }
                });
            } else {
                Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda88
                    @Override // java.lang.Runnable
                    public final void run() {
                        VoIPService.this.m1291lambda$callEnded$93$orgtelegrammessengervoipVoIPService();
                    }
                }, 100L);
                delay = 500;
            }
            AndroidUtilities.runOnUIThread(this.afterSoundRunnable, delay);
        }
        Runnable runnable2 = this.timeoutRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.timeoutRunnable = null;
        }
        endConnectionServiceCall(this.needPlayEndSound ? delay : 0L);
        stopSelf();
    }

    /* renamed from: lambda$callEnded$90$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1288lambda$callEnded$90$orgtelegrammessengervoipVoIPService() {
        dispatchStateChanged(11);
    }

    /* renamed from: lambda$callEnded$91$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1289lambda$callEnded$91$orgtelegrammessengervoipVoIPService() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
            this.spPlayId = 0;
        }
    }

    /* renamed from: lambda$callEnded$92$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1290lambda$callEnded$92$orgtelegrammessengervoipVoIPService() {
        this.soundPool.play(this.spEndId, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    /* renamed from: lambda$callEnded$93$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1291lambda$callEnded$93$orgtelegrammessengervoipVoIPService() {
        this.soundPool.play(this.spVoiceChatEndId, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    private void endConnectionServiceCall(long delay) {
        if (USE_CONNECTION_SERVICE) {
            Runnable r = new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.m1313x168fbaa9();
                }
            };
            if (delay > 0) {
                AndroidUtilities.runOnUIThread(r, delay);
            } else {
                r.run();
            }
        }
    }

    /* renamed from: lambda$endConnectionServiceCall$94$org-telegram-messenger-voip-VoIPService */
    public /* synthetic */ void m1313x168fbaa9() {
        CallConnection callConnection = this.systemCallConnection;
        if (callConnection != null) {
            switch (this.callDiscardReason) {
                case 1:
                    callConnection.setDisconnected(new DisconnectCause(this.isOutgoing ? 2 : 6));
                    break;
                case 2:
                    callConnection.setDisconnected(new DisconnectCause(1));
                    break;
                case 3:
                    callConnection.setDisconnected(new DisconnectCause(this.isOutgoing ? 4 : 5));
                    break;
                case 4:
                    callConnection.setDisconnected(new DisconnectCause(7));
                    break;
                default:
                    callConnection.setDisconnected(new DisconnectCause(3));
                    break;
            }
            this.systemCallConnection.destroy();
            this.systemCallConnection = null;
        }
    }

    public boolean isOutgoing() {
        return this.isOutgoing;
    }

    public void handleNotificationAction(Intent intent) {
        if ((getPackageName() + ".END_CALL").equals(intent.getAction())) {
            stopForeground(true);
            hangUp();
            return;
        }
        if ((getPackageName() + ".DECLINE_CALL").equals(intent.getAction())) {
            stopForeground(true);
            declineIncomingCall(4, null);
            return;
        }
        if ((getPackageName() + ".ANSWER_CALL").equals(intent.getAction())) {
            acceptIncomingCallFromNotification();
        }
    }

    public void acceptIncomingCallFromNotification() {
        showNotification();
        if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT < 30 && (checkSelfPermission("android.permission.RECORD_AUDIO") != 0 || (this.privateCall.video && checkSelfPermission("android.permission.CAMERA") != 0))) {
            try {
                PendingIntent.getActivity(this, 0, new Intent(this, VoIPPermissionActivity.class).addFlags(268435456), C.BUFFER_FLAG_ENCRYPTED).send();
                return;
            } catch (Exception x) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error starting permission activity", x);
                    return;
                }
                return;
            }
        }
        acceptIncomingCall();
        try {
            PendingIntent.getActivity(this, 0, new Intent(this, getUIActivityClass()).setAction("voip"), 0).send();
        } catch (Exception x2) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Error starting incall activity", x2);
            }
        }
    }

    public void updateOutputGainControlState() {
        if (hasRtmpStream()) {
            return;
        }
        int i = 0;
        if (this.tgVoip[0] != null) {
            if (!USE_CONNECTION_SERVICE) {
                AudioManager am = (AudioManager) getSystemService("audio");
                this.tgVoip[0].setAudioOutputGainControlEnabled(hasEarpiece() && !am.isSpeakerphoneOn() && !am.isBluetoothScoOn() && !this.isHeadsetPlugged);
                NativeInstance nativeInstance = this.tgVoip[0];
                if (!this.isHeadsetPlugged && (!hasEarpiece() || am.isSpeakerphoneOn() || am.isBluetoothScoOn() || this.isHeadsetPlugged)) {
                    i = 1;
                }
                nativeInstance.setEchoCancellationStrength(i);
                return;
            }
            boolean isEarpiece = this.systemCallConnection.getCallAudioState().getRoute() == 1;
            this.tgVoip[0].setAudioOutputGainControlEnabled(isEarpiece);
            NativeInstance nativeInstance2 = this.tgVoip[0];
            if (!isEarpiece) {
                i = 1;
            }
            nativeInstance2.setEchoCancellationStrength(i);
        }
    }

    public int getAccount() {
        return this.currentAccount;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.appDidLogout) {
            callEnded();
        }
    }

    public static boolean isAnyKindOfCallActive() {
        return (getSharedInstance() == null || getSharedInstance().getCallState() == 15) ? false : true;
    }

    private boolean isFinished() {
        int i = this.currentState;
        return i == 11 || i == 4;
    }

    public int getRemoteAudioState() {
        return this.remoteAudioState;
    }

    public int getRemoteVideoState() {
        return this.remoteVideoState;
    }

    private PhoneAccountHandle addAccountToTelecomManager() {
        TelecomManager tm = (TelecomManager) getSystemService("telecom");
        TLRPC.User self = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        ComponentName componentName = new ComponentName(this, TelegramConnectionService.class);
        PhoneAccountHandle handle = new PhoneAccountHandle(componentName, "" + self.id);
        PhoneAccount account = new PhoneAccount.Builder(handle, ContactsController.formatName(self.first_name, self.last_name)).setCapabilities(2048).setIcon(Icon.createWithResource(this, (int) R.drawable.ic_launcher_dr)).setHighlightColor(-13851168).addSupportedUriScheme("sip").build();
        tm.registerPhoneAccount(account);
        return handle;
    }

    private static boolean isDeviceCompatibleWithConnectionServiceAPI() {
        int i = Build.VERSION.SDK_INT;
        return false;
    }

    /* loaded from: classes4.dex */
    public interface StateListener {
        void onAudioSettingsChanged();

        void onCameraFirstFrameAvailable();

        void onCameraSwitch(boolean z);

        void onMediaStateUpdated(int i, int i2);

        void onScreenOnChange(boolean z);

        void onSignalBarsCountChanged(int i);

        void onStateChanged(int i);

        void onVideoAvailableChange(boolean z);

        /* renamed from: org.telegram.messenger.voip.VoIPService$StateListener$-CC */
        /* loaded from: classes4.dex */
        public final /* synthetic */ class CC {
            public static void $default$onStateChanged(StateListener _this, int state) {
            }

            public static void $default$onSignalBarsCountChanged(StateListener _this, int count) {
            }

            public static void $default$onAudioSettingsChanged(StateListener _this) {
            }

            public static void $default$onMediaStateUpdated(StateListener _this, int audioState, int videoState) {
            }

            public static void $default$onCameraSwitch(StateListener _this, boolean isFrontFace) {
            }

            public static void $default$onCameraFirstFrameAvailable(StateListener _this) {
            }

            public static void $default$onVideoAvailableChange(StateListener _this, boolean isAvailable) {
            }

            public static void $default$onScreenOnChange(StateListener _this, boolean screenOn) {
            }
        }
    }

    /* loaded from: classes4.dex */
    public class CallConnection extends Connection {
        public CallConnection() {
            VoIPService.this = this$0;
            setConnectionProperties(128);
            setAudioModeIsVoip(true);
        }

        @Override // android.telecom.Connection
        public void onCallAudioStateChanged(CallAudioState state) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ConnectionService call audio state changed: " + state);
            }
            Iterator it = VoIPService.this.stateListeners.iterator();
            while (it.hasNext()) {
                StateListener l = (StateListener) it.next();
                l.onAudioSettingsChanged();
            }
        }

        @Override // android.telecom.Connection
        public void onDisconnect() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ConnectionService onDisconnect");
            }
            setDisconnected(new DisconnectCause(2));
            destroy();
            VoIPService.this.systemCallConnection = null;
            VoIPService.this.hangUp();
        }

        @Override // android.telecom.Connection
        public void onAnswer() {
            VoIPService.this.acceptIncomingCallFromNotification();
        }

        @Override // android.telecom.Connection
        public void onReject() {
            VoIPService.this.needPlayEndSound = false;
            VoIPService.this.declineIncomingCall(1, null);
        }

        @Override // android.telecom.Connection
        public void onShowIncomingCallUi() {
            VoIPService.this.startRinging();
        }

        @Override // android.telecom.Connection
        public void onStateChanged(int state) {
            super.onStateChanged(state);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ConnectionService onStateChanged " + stateToString(state));
            }
            if (state == 4) {
                ContactsController.getInstance(VoIPService.this.currentAccount).deleteConnectionServiceContact();
                VoIPService.this.didDeleteConnectionServiceContact = true;
            }
        }

        @Override // android.telecom.Connection
        public void onCallEvent(String event, Bundle extras) {
            super.onCallEvent(event, extras);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ConnectionService onCallEvent " + event);
            }
        }

        @Override // android.telecom.Connection
        public void onSilence() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("onSlience");
            }
            VoIPService.this.stopRinging();
        }
    }
}
