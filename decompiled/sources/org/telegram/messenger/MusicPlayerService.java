package org.telegram.messenger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.RemoteControlClient;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import java.io.File;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.ui.LaunchActivity;
/* loaded from: classes4.dex */
public class MusicPlayerService extends Service implements NotificationCenter.NotificationCenterDelegate {
    private static final int ID_NOTIFICATION = 5;
    public static final String NOTIFY_CLOSE = "org.telegram.android.musicplayer.close";
    public static final String NOTIFY_NEXT = "org.telegram.android.musicplayer.next";
    public static final String NOTIFY_PAUSE = "org.telegram.android.musicplayer.pause";
    public static final String NOTIFY_PLAY = "org.telegram.android.musicplayer.play";
    public static final String NOTIFY_PREVIOUS = "org.telegram.android.musicplayer.previous";
    public static final String NOTIFY_SEEK = "org.telegram.android.musicplayer.seek";
    private static boolean supportBigNotifications;
    private static boolean supportLockScreenControls;
    private Bitmap albumArtPlaceholder;
    private AudioManager audioManager;
    private BroadcastReceiver headsetPlugReceiver = new BroadcastReceiver() { // from class: org.telegram.messenger.MusicPlayerService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.media.AUDIO_BECOMING_NOISY".equals(intent.getAction())) {
                MediaController.getInstance().m383lambda$startAudioAgain$7$orgtelegrammessengerMediaController(MediaController.getInstance().getPlayingMessageObject());
            }
        }
    };
    private ImageReceiver imageReceiver;
    private String loadingFilePath;
    private MediaSession mediaSession;
    private int notificationMessageID;
    private PlaybackState.Builder playbackState;
    private RemoteControlClient remoteControlClient;

    static {
        boolean z = true;
        supportBigNotifications = Build.VERSION.SDK_INT >= 16;
        if (Build.VERSION.SDK_INT >= 21 && TextUtils.isEmpty(AndroidUtilities.getSystemProperty("ro.miui.ui.version.code"))) {
            z = false;
        }
        supportLockScreenControls = z;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        this.audioManager = (AudioManager) getSystemService("audio");
        for (int a = 0; a < 4; a++) {
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingDidSeek);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.httpFileDidLoad);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.fileLoaded);
        }
        ImageReceiver imageReceiver = new ImageReceiver(null);
        this.imageReceiver = imageReceiver;
        imageReceiver.setDelegate(new ImageReceiver.ImageReceiverDelegate() { // from class: org.telegram.messenger.MusicPlayerService$$ExternalSyntheticLambda1
            @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
            public final void didSetImage(ImageReceiver imageReceiver2, boolean z, boolean z2, boolean z3) {
                MusicPlayerService.this.m1091lambda$onCreate$0$orgtelegrammessengerMusicPlayerService(imageReceiver2, z, z2, z3);
            }

            @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
            public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver2) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver2);
            }
        });
        if (Build.VERSION.SDK_INT >= 21) {
            this.mediaSession = new MediaSession(this, "telegramAudioPlayer");
            this.playbackState = new PlaybackState.Builder();
            this.albumArtPlaceholder = Bitmap.createBitmap(AndroidUtilities.dp(102.0f), AndroidUtilities.dp(102.0f), Bitmap.Config.ARGB_8888);
            Drawable placeholder = getResources().getDrawable(org.telegram.messenger.beta.R.drawable.nocover_big);
            placeholder.setBounds(0, 0, this.albumArtPlaceholder.getWidth(), this.albumArtPlaceholder.getHeight());
            placeholder.draw(new Canvas(this.albumArtPlaceholder));
            this.mediaSession.setCallback(new MediaSession.Callback() { // from class: org.telegram.messenger.MusicPlayerService.2
                @Override // android.media.session.MediaSession.Callback
                public void onPlay() {
                    MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
                }

                @Override // android.media.session.MediaSession.Callback
                public void onPause() {
                    MediaController.getInstance().m383lambda$startAudioAgain$7$orgtelegrammessengerMediaController(MediaController.getInstance().getPlayingMessageObject());
                }

                @Override // android.media.session.MediaSession.Callback
                public void onSkipToNext() {
                    MediaController.getInstance().playNextMessage();
                }

                @Override // android.media.session.MediaSession.Callback
                public void onSkipToPrevious() {
                    MediaController.getInstance().playPreviousMessage();
                }

                @Override // android.media.session.MediaSession.Callback
                public void onSeekTo(long pos) {
                    MessageObject object = MediaController.getInstance().getPlayingMessageObject();
                    if (object != null) {
                        MediaController.getInstance().seekToProgress(object, ((float) (pos / 1000)) / object.getDuration());
                        MusicPlayerService.this.updatePlaybackState(pos);
                    }
                }

                @Override // android.media.session.MediaSession.Callback
                public void onStop() {
                }
            });
            this.mediaSession.setActive(true);
        }
        registerReceiver(this.headsetPlugReceiver, new IntentFilter("android.media.AUDIO_BECOMING_NOISY"));
        super.onCreate();
    }

    /* renamed from: lambda$onCreate$0$org-telegram-messenger-MusicPlayerService */
    public /* synthetic */ void m1091lambda$onCreate$0$orgtelegrammessengerMusicPlayerService(ImageReceiver imageReceiver, boolean set, boolean thumb, boolean memCache) {
        if (set && !TextUtils.isEmpty(this.loadingFilePath)) {
            MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject != null) {
                createNotification(messageObject, true);
            }
            this.loadingFilePath = null;
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            try {
                if ((getPackageName() + ".STOP_PLAYER").equals(intent.getAction())) {
                    MediaController.getInstance().cleanupPlayer(true, true);
                    return 2;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        if (messageObject == null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MusicPlayerService$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    MusicPlayerService.this.stopSelf();
                }
            });
            return 1;
        }
        if (supportLockScreenControls) {
            ComponentName remoteComponentName = new ComponentName(getApplicationContext(), MusicPlayerReceiver.class.getName());
            try {
                if (this.remoteControlClient == null) {
                    this.audioManager.registerMediaButtonEventReceiver(remoteComponentName);
                    Intent mediaButtonIntent = new Intent("android.intent.action.MEDIA_BUTTON");
                    mediaButtonIntent.setComponent(remoteComponentName);
                    PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
                    RemoteControlClient remoteControlClient = new RemoteControlClient(mediaPendingIntent);
                    this.remoteControlClient = remoteControlClient;
                    this.audioManager.registerRemoteControlClient(remoteControlClient);
                }
                this.remoteControlClient.setTransportControlFlags(PsExtractor.PRIVATE_STREAM_1);
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        createNotification(messageObject, false);
        return 1;
    }

    private Bitmap loadArtworkFromUrl(String artworkUrl, boolean big, boolean tryLoad) {
        ImageLoader.getHttpFileName(artworkUrl);
        File path = ImageLoader.getHttpFilePath(artworkUrl, "jpg");
        if (path.exists()) {
            String absolutePath = path.getAbsolutePath();
            float f = 600.0f;
            float f2 = big ? 600.0f : 100.0f;
            if (!big) {
                f = 100.0f;
            }
            return ImageLoader.loadBitmap(absolutePath, null, f2, f, false);
        }
        if (tryLoad) {
            this.loadingFilePath = path.getAbsolutePath();
            if (!big) {
                this.imageReceiver.setImage(artworkUrl, "48_48", null, null, 0L);
            }
        } else {
            this.loadingFilePath = null;
        }
        return null;
    }

    private void createNotification(MessageObject messageObject, boolean forBitmap) {
        AudioInfo audioInfo;
        Bitmap fullAlbumArt;
        int i;
        Bitmap albumArt;
        PendingIntent contentIntent;
        Bitmap albumArt2;
        String str;
        int i2;
        String songName = messageObject.getMusicTitle();
        String authorName = messageObject.getMusicAuthor();
        AudioInfo audioInfo2 = MediaController.getInstance().getAudioInfo();
        Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
        intent.setAction("com.tmessages.openplayer");
        intent.addCategory("android.intent.category.LAUNCHER");
        PendingIntent contentIntent2 = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, 0);
        String artworkUrl = messageObject.getArtworkUrl(true);
        String artworkUrlBig = messageObject.getArtworkUrl(false);
        long duration = messageObject.getDuration() * 1000;
        Bitmap albumArt3 = audioInfo2 != null ? audioInfo2.getSmallCover() : null;
        Bitmap fullAlbumArt2 = audioInfo2 != null ? audioInfo2.getCover() : null;
        this.loadingFilePath = null;
        this.imageReceiver.setImageBitmap((Drawable) null);
        if (albumArt3 != null || TextUtils.isEmpty(artworkUrl)) {
            this.loadingFilePath = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(messageObject.getDocument()).getAbsolutePath();
        } else {
            fullAlbumArt2 = loadArtworkFromUrl(artworkUrlBig, true, !forBitmap);
            if (fullAlbumArt2 == null) {
                Bitmap loadArtworkFromUrl = loadArtworkFromUrl(artworkUrl, false, !forBitmap);
                albumArt3 = loadArtworkFromUrl;
                fullAlbumArt2 = loadArtworkFromUrl;
            } else {
                albumArt3 = loadArtworkFromUrl(artworkUrlBig, false, !forBitmap);
            }
        }
        Bitmap albumArt4 = albumArt3;
        if (Build.VERSION.SDK_INT >= 21) {
            boolean isPlaying = !MediaController.getInstance().isMessagePaused();
            PendingIntent pendingPrev = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_PREVIOUS).setComponent(new ComponentName(this, MusicPlayerReceiver.class)), 268435456);
            PendingIntent pendingStop = PendingIntent.getService(getApplicationContext(), 0, new Intent(this, getClass()).setAction(getPackageName() + ".STOP_PLAYER"), 268435456);
            PendingIntent pendingPlaypause = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(isPlaying ? NOTIFY_PAUSE : NOTIFY_PLAY).setComponent(new ComponentName(this, MusicPlayerReceiver.class)), 268435456);
            PendingIntent pendingNext = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_NEXT).setComponent(new ComponentName(this, MusicPlayerReceiver.class)), 268435456);
            PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_SEEK).setComponent(new ComponentName(this, MusicPlayerReceiver.class)), 268435456);
            Notification.Builder bldr = new Notification.Builder(this);
            bldr.setSmallIcon(org.telegram.messenger.beta.R.drawable.player).setOngoing(isPlaying).setContentTitle(songName).setContentText(authorName).setSubText(audioInfo2 != null ? audioInfo2.getAlbum() : null).setContentIntent(contentIntent2).setDeleteIntent(pendingStop).setShowWhen(false).setCategory(NotificationCompat.CATEGORY_TRANSPORT).setPriority(2).setStyle(new Notification.MediaStyle().setMediaSession(this.mediaSession.getSessionToken()).setShowActionsInCompactView(0, 1, 2));
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationsController.checkOtherNotificationsChannel();
                bldr.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            }
            if (albumArt4 != null) {
                albumArt = albumArt4;
                bldr.setLargeIcon(albumArt);
            } else {
                albumArt = albumArt4;
                bldr.setLargeIcon(this.albumArtPlaceholder);
            }
            String nextDescription = LocaleController.getString("Next", org.telegram.messenger.beta.R.string.Next);
            String previousDescription = LocaleController.getString("AccDescrPrevious", org.telegram.messenger.beta.R.string.AccDescrPrevious);
            if (MediaController.getInstance().isDownloadingCurrentMessage()) {
                audioInfo = audioInfo2;
                albumArt2 = albumArt;
                contentIntent = contentIntent2;
                this.playbackState.setState(6, 0L, 1.0f).setActions(0L);
                bldr.addAction(new Notification.Action.Builder((int) org.telegram.messenger.beta.R.drawable.ic_action_previous, previousDescription, pendingPrev).build()).addAction(new Notification.Action.Builder((int) org.telegram.messenger.beta.R.drawable.loading_animation2, LocaleController.getString("Loading", org.telegram.messenger.beta.R.string.Loading), (PendingIntent) null).build()).addAction(new Notification.Action.Builder((int) org.telegram.messenger.beta.R.drawable.ic_action_next, nextDescription, pendingNext).build());
                fullAlbumArt = fullAlbumArt2;
            } else {
                audioInfo = audioInfo2;
                albumArt2 = albumArt;
                contentIntent = contentIntent2;
                fullAlbumArt = fullAlbumArt2;
                this.playbackState.setState(isPlaying ? 3 : 2, MediaController.getInstance().getPlayingMessageObject().audioProgressSec * 1000, isPlaying ? 1.0f : 0.0f).setActions(822L);
                if (isPlaying) {
                    i2 = org.telegram.messenger.beta.R.string.AccActionPause;
                    str = "AccActionPause";
                } else {
                    i2 = org.telegram.messenger.beta.R.string.AccActionPlay;
                    str = "AccActionPlay";
                }
                String playPauseTitle = LocaleController.getString(str, i2);
                bldr.addAction(new Notification.Action.Builder((int) org.telegram.messenger.beta.R.drawable.ic_action_previous, previousDescription, pendingPrev).build()).addAction(new Notification.Action.Builder(isPlaying ? org.telegram.messenger.beta.R.drawable.ic_action_pause : org.telegram.messenger.beta.R.drawable.ic_action_play, playPauseTitle, pendingPlaypause).build()).addAction(new Notification.Action.Builder((int) org.telegram.messenger.beta.R.drawable.ic_action_next, nextDescription, pendingNext).build());
            }
            this.mediaSession.setPlaybackState(this.playbackState.build());
            MediaMetadata.Builder meta = new MediaMetadata.Builder().putBitmap("android.media.metadata.ALBUM_ART", fullAlbumArt).putString("android.media.metadata.ALBUM_ARTIST", authorName).putString("android.media.metadata.ARTIST", authorName).putLong("android.media.metadata.DURATION", duration).putString("android.media.metadata.TITLE", songName).putString("android.media.metadata.ALBUM", audioInfo != null ? audioInfo.getAlbum() : null);
            this.mediaSession.setMetadata(meta.build());
            bldr.setVisibility(1);
            Notification notification = bldr.build();
            if (isPlaying) {
                startForeground(5, notification);
            } else {
                stopForeground(false);
                NotificationManager nm = (NotificationManager) getSystemService("notification");
                nm.notify(5, notification);
            }
        } else {
            audioInfo = audioInfo2;
            fullAlbumArt = fullAlbumArt2;
            RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(), (int) org.telegram.messenger.beta.R.layout.player_small_notification);
            RemoteViews expandedView = null;
            if (supportBigNotifications) {
                expandedView = new RemoteViews(getApplicationContext().getPackageName(), (int) org.telegram.messenger.beta.R.layout.player_big_notification);
            }
            Notification notification2 = new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(org.telegram.messenger.beta.R.drawable.player).setContentIntent(contentIntent2).setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL).setContentTitle(songName).build();
            notification2.contentView = simpleContentView;
            if (supportBigNotifications) {
                notification2.bigContentView = expandedView;
            }
            setListeners(simpleContentView);
            if (supportBigNotifications) {
                setListeners(expandedView);
            }
            if (albumArt4 != null) {
                notification2.contentView.setImageViewBitmap(org.telegram.messenger.beta.R.id.player_album_art, albumArt4);
                if (supportBigNotifications) {
                    notification2.bigContentView.setImageViewBitmap(org.telegram.messenger.beta.R.id.player_album_art, albumArt4);
                }
            } else {
                notification2.contentView.setImageViewResource(org.telegram.messenger.beta.R.id.player_album_art, org.telegram.messenger.beta.R.drawable.nocover_small);
                if (supportBigNotifications) {
                    notification2.bigContentView.setImageViewResource(org.telegram.messenger.beta.R.id.player_album_art, org.telegram.messenger.beta.R.drawable.nocover_big);
                }
            }
            if (MediaController.getInstance().isDownloadingCurrentMessage()) {
                notification2.contentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_pause, 8);
                notification2.contentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_play, 8);
                notification2.contentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_next, 8);
                notification2.contentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_previous, 8);
                notification2.contentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_progress_bar, 0);
                if (supportBigNotifications) {
                    notification2.bigContentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_pause, 8);
                    notification2.bigContentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_play, 8);
                    notification2.bigContentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_next, 8);
                    notification2.bigContentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_previous, 8);
                    notification2.bigContentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_progress_bar, 0);
                }
            } else {
                notification2.contentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_progress_bar, 8);
                notification2.contentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_next, 0);
                notification2.contentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_previous, 0);
                if (!supportBigNotifications) {
                    i = 8;
                } else {
                    notification2.bigContentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_next, 0);
                    notification2.bigContentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_previous, 0);
                    i = 8;
                    notification2.bigContentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_progress_bar, 8);
                }
                if (MediaController.getInstance().isMessagePaused()) {
                    notification2.contentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_pause, i);
                    notification2.contentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_play, 0);
                    if (supportBigNotifications) {
                        notification2.bigContentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_pause, i);
                        notification2.bigContentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_play, 0);
                    }
                } else {
                    notification2.contentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_pause, 0);
                    notification2.contentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_play, 8);
                    if (supportBigNotifications) {
                        notification2.bigContentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_pause, 0);
                        notification2.bigContentView.setViewVisibility(org.telegram.messenger.beta.R.id.player_play, 8);
                    }
                }
            }
            notification2.contentView.setTextViewText(org.telegram.messenger.beta.R.id.player_song_name, songName);
            notification2.contentView.setTextViewText(org.telegram.messenger.beta.R.id.player_author_name, authorName);
            if (supportBigNotifications) {
                notification2.bigContentView.setTextViewText(org.telegram.messenger.beta.R.id.player_song_name, songName);
                notification2.bigContentView.setTextViewText(org.telegram.messenger.beta.R.id.player_author_name, authorName);
                notification2.bigContentView.setTextViewText(org.telegram.messenger.beta.R.id.player_album_title, (audioInfo == null || TextUtils.isEmpty(audioInfo.getAlbum())) ? "" : audioInfo.getAlbum());
            }
            notification2.flags |= 2;
            startForeground(5, notification2);
        }
        if (this.remoteControlClient != null) {
            int currentID = MediaController.getInstance().getPlayingMessageObject().getId();
            if (this.notificationMessageID != currentID) {
                this.notificationMessageID = currentID;
                RemoteControlClient.MetadataEditor metadataEditor = this.remoteControlClient.editMetadata(true);
                metadataEditor.putString(2, authorName);
                metadataEditor.putString(7, songName);
                if (audioInfo != null && !TextUtils.isEmpty(audioInfo.getAlbum())) {
                    metadataEditor.putString(1, audioInfo.getAlbum());
                }
                metadataEditor.putLong(9, MediaController.getInstance().getPlayingMessageObject().audioPlayerDuration * 1000);
                if (fullAlbumArt != null) {
                    try {
                        metadataEditor.putBitmap(100, fullAlbumArt);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
                metadataEditor.apply();
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MusicPlayerService.3
                    @Override // java.lang.Runnable
                    public void run() {
                        if (MusicPlayerService.this.remoteControlClient == null || MediaController.getInstance().getPlayingMessageObject() == null) {
                            return;
                        }
                        if (MediaController.getInstance().getPlayingMessageObject().audioPlayerDuration != C.TIME_UNSET) {
                            RemoteControlClient.MetadataEditor metadataEditor2 = MusicPlayerService.this.remoteControlClient.editMetadata(false);
                            metadataEditor2.putLong(9, MediaController.getInstance().getPlayingMessageObject().audioPlayerDuration * 1000);
                            metadataEditor2.apply();
                            int i3 = 2;
                            if (Build.VERSION.SDK_INT >= 18) {
                                RemoteControlClient remoteControlClient = MusicPlayerService.this.remoteControlClient;
                                if (!MediaController.getInstance().isMessagePaused()) {
                                    i3 = 3;
                                }
                                remoteControlClient.setPlaybackState(i3, Math.max(MediaController.getInstance().getPlayingMessageObject().audioProgressSec * 1000, 100L), MediaController.getInstance().isMessagePaused() ? 0.0f : 1.0f);
                                return;
                            }
                            RemoteControlClient remoteControlClient2 = MusicPlayerService.this.remoteControlClient;
                            if (!MediaController.getInstance().isMessagePaused()) {
                                i3 = 3;
                            }
                            remoteControlClient2.setPlaybackState(i3);
                            return;
                        }
                        AndroidUtilities.runOnUIThread(this, 500L);
                    }
                }, 1000L);
            }
            if (MediaController.getInstance().isDownloadingCurrentMessage()) {
                this.remoteControlClient.setPlaybackState(8);
                return;
            }
            RemoteControlClient.MetadataEditor metadataEditor2 = this.remoteControlClient.editMetadata(false);
            metadataEditor2.putLong(9, MediaController.getInstance().getPlayingMessageObject().audioPlayerDuration * 1000);
            metadataEditor2.apply();
            if (Build.VERSION.SDK_INT < 18) {
                this.remoteControlClient.setPlaybackState(MediaController.getInstance().isMessagePaused() ? 2 : 3);
            } else {
                this.remoteControlClient.setPlaybackState(MediaController.getInstance().isMessagePaused() ? 2 : 3, Math.max(MediaController.getInstance().getPlayingMessageObject().audioProgressSec * 1000, 100L), MediaController.getInstance().isMessagePaused() ? 0.0f : 1.0f);
            }
        }
    }

    public void updatePlaybackState(long seekTo) {
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        boolean isPlaying = !MediaController.getInstance().isMessagePaused();
        float f = 1.0f;
        if (MediaController.getInstance().isDownloadingCurrentMessage()) {
            this.playbackState.setState(6, 0L, 1.0f).setActions(0L);
        } else {
            PlaybackState.Builder builder = this.playbackState;
            int i = isPlaying ? 3 : 2;
            if (!isPlaying) {
                f = 0.0f;
            }
            builder.setState(i, seekTo, f).setActions(822L);
        }
        this.mediaSession.setPlaybackState(this.playbackState.build());
    }

    public void setListeners(RemoteViews view) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_PREVIOUS), 134217728);
        view.setOnClickPendingIntent(org.telegram.messenger.beta.R.id.player_previous, pendingIntent);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_CLOSE), 134217728);
        view.setOnClickPendingIntent(org.telegram.messenger.beta.R.id.player_close, pendingIntent2);
        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_PAUSE), 134217728);
        view.setOnClickPendingIntent(org.telegram.messenger.beta.R.id.player_pause, pendingIntent3);
        PendingIntent pendingIntent4 = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_NEXT), 134217728);
        view.setOnClickPendingIntent(org.telegram.messenger.beta.R.id.player_next, pendingIntent4);
        PendingIntent pendingIntent5 = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_PLAY), 134217728);
        view.setOnClickPendingIntent(org.telegram.messenger.beta.R.id.player_play, pendingIntent5);
    }

    @Override // android.app.Service
    public void onDestroy() {
        unregisterReceiver(this.headsetPlugReceiver);
        super.onDestroy();
        RemoteControlClient remoteControlClient = this.remoteControlClient;
        if (remoteControlClient != null) {
            RemoteControlClient.MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
            metadataEditor.clear();
            metadataEditor.apply();
            this.audioManager.unregisterRemoteControlClient(this.remoteControlClient);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            this.mediaSession.release();
        }
        for (int a = 0; a < 4; a++) {
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingDidSeek);
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.httpFileDidLoad);
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.fileLoaded);
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        String str;
        String str2;
        if (id == NotificationCenter.messagePlayingPlayStateChanged) {
            MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject != null) {
                createNotification(messageObject, false);
            } else {
                stopSelf();
            }
        } else if (id == NotificationCenter.messagePlayingDidSeek) {
            MessageObject messageObject2 = MediaController.getInstance().getPlayingMessageObject();
            if (this.remoteControlClient != null && Build.VERSION.SDK_INT >= 18) {
                long progress = Math.round(messageObject2.audioPlayerDuration * ((Float) args[1]).floatValue()) * 1000;
                this.remoteControlClient.setPlaybackState(MediaController.getInstance().isMessagePaused() ? 2 : 3, progress, MediaController.getInstance().isMessagePaused() ? 0.0f : 1.0f);
            }
        } else if (id == NotificationCenter.httpFileDidLoad) {
            String path = (String) args[0];
            MessageObject messageObject3 = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject3 != null && (str2 = this.loadingFilePath) != null && str2.equals(path)) {
                createNotification(messageObject3, false);
            }
        } else if (id == NotificationCenter.fileLoaded) {
            String path2 = (String) args[0];
            MessageObject messageObject4 = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject4 != null && (str = this.loadingFilePath) != null && str.equals(path2)) {
                createNotification(messageObject4, false);
            }
        }
    }
}
