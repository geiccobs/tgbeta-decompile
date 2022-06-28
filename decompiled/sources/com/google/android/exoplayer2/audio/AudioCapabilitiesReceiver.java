package com.google.android.exoplayer2.audio;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
/* loaded from: classes3.dex */
public final class AudioCapabilitiesReceiver {
    AudioCapabilities audioCapabilities;
    private final Context context;
    private final ExternalSurroundSoundSettingObserver externalSurroundSoundSettingObserver;
    private final Handler handler;
    private final Listener listener;
    private final BroadcastReceiver receiver;
    private boolean registered;

    /* loaded from: classes3.dex */
    public interface Listener {
        void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities);
    }

    public AudioCapabilitiesReceiver(Context context, Listener listener) {
        Context context2 = context.getApplicationContext();
        this.context = context2;
        this.listener = (Listener) Assertions.checkNotNull(listener);
        Handler handler = new Handler(Util.getLooper());
        this.handler = handler;
        ExternalSurroundSoundSettingObserver externalSurroundSoundSettingObserver = null;
        this.receiver = Util.SDK_INT >= 21 ? new HdmiAudioPlugBroadcastReceiver() : null;
        Uri externalSurroundSoundUri = AudioCapabilities.getExternalSurroundSoundGlobalSettingUri();
        this.externalSurroundSoundSettingObserver = externalSurroundSoundUri != null ? new ExternalSurroundSoundSettingObserver(handler, context2.getContentResolver(), externalSurroundSoundUri) : externalSurroundSoundSettingObserver;
    }

    public AudioCapabilities register() {
        if (this.registered) {
            return (AudioCapabilities) Assertions.checkNotNull(this.audioCapabilities);
        }
        this.registered = true;
        ExternalSurroundSoundSettingObserver externalSurroundSoundSettingObserver = this.externalSurroundSoundSettingObserver;
        if (externalSurroundSoundSettingObserver != null) {
            externalSurroundSoundSettingObserver.register();
        }
        Intent stickyIntent = null;
        if (this.receiver != null) {
            IntentFilter intentFilter = new IntentFilter("android.media.action.HDMI_AUDIO_PLUG");
            stickyIntent = this.context.registerReceiver(this.receiver, intentFilter, null, this.handler);
        }
        AudioCapabilities capabilities = AudioCapabilities.getCapabilities(this.context, stickyIntent);
        this.audioCapabilities = capabilities;
        return capabilities;
    }

    public void unregister() {
        if (!this.registered) {
            return;
        }
        this.audioCapabilities = null;
        BroadcastReceiver broadcastReceiver = this.receiver;
        if (broadcastReceiver != null) {
            this.context.unregisterReceiver(broadcastReceiver);
        }
        ExternalSurroundSoundSettingObserver externalSurroundSoundSettingObserver = this.externalSurroundSoundSettingObserver;
        if (externalSurroundSoundSettingObserver != null) {
            externalSurroundSoundSettingObserver.unregister();
        }
        this.registered = false;
    }

    public void onNewAudioCapabilities(AudioCapabilities newAudioCapabilities) {
        if (this.registered && !newAudioCapabilities.equals(this.audioCapabilities)) {
            this.audioCapabilities = newAudioCapabilities;
            this.listener.onAudioCapabilitiesChanged(newAudioCapabilities);
        }
    }

    /* loaded from: classes3.dex */
    private final class HdmiAudioPlugBroadcastReceiver extends BroadcastReceiver {
        private HdmiAudioPlugBroadcastReceiver() {
            AudioCapabilitiesReceiver.this = r1;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (!isInitialStickyBroadcast()) {
                AudioCapabilitiesReceiver.this.onNewAudioCapabilities(AudioCapabilities.getCapabilities(context, intent));
            }
        }
    }

    /* loaded from: classes3.dex */
    private final class ExternalSurroundSoundSettingObserver extends ContentObserver {
        private final ContentResolver resolver;
        private final Uri settingUri;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ExternalSurroundSoundSettingObserver(Handler handler, ContentResolver resolver, Uri settingUri) {
            super(handler);
            AudioCapabilitiesReceiver.this = r1;
            this.resolver = resolver;
            this.settingUri = settingUri;
        }

        public void register() {
            this.resolver.registerContentObserver(this.settingUri, false, this);
        }

        public void unregister() {
            this.resolver.unregisterContentObserver(this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            AudioCapabilitiesReceiver audioCapabilitiesReceiver = AudioCapabilitiesReceiver.this;
            audioCapabilitiesReceiver.onNewAudioCapabilities(AudioCapabilities.getCapabilities(audioCapabilitiesReceiver.context));
        }
    }
}
