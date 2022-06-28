package org.telegram.messenger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.TextureView;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.gms.internal.icing.zzby$$ExternalSyntheticBackport0;
import com.google.firebase.appindexing.Indexable;
import com.microsoft.appcenter.crashes.ingestion.models.ErrorAttachmentLog;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.video.MediaCodecVideoConvertor;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.PhotoFilterView;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.PhotoViewer;
/* loaded from: classes.dex */
public class MediaController implements AudioManager.OnAudioFocusChangeListener, NotificationCenter.NotificationCenterDelegate, SensorEventListener {
    private static final int AUDIO_FOCUSED = 2;
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    public static final String AUIDO_MIME_TYPE = "audio/mp4a-latm";
    private static volatile MediaController Instance = null;
    public static final int VIDEO_BITRATE_1080 = 6800000;
    public static final int VIDEO_BITRATE_360 = 750000;
    public static final int VIDEO_BITRATE_480 = 1000000;
    public static final int VIDEO_BITRATE_720 = 2621440;
    public static final String VIDEO_MIME_TYPE = "video/avc";
    private static final float VOLUME_DUCK = 0.2f;
    private static final float VOLUME_NORMAL = 1.0f;
    public static AlbumEntry allMediaAlbumEntry;
    public static ArrayList<AlbumEntry> allMediaAlbums;
    public static ArrayList<AlbumEntry> allPhotoAlbums;
    public static AlbumEntry allPhotosAlbumEntry;
    public static AlbumEntry allVideosAlbumEntry;
    private static Runnable broadcastPhotosRunnable;
    private static final String[] projectionPhotos;
    private static final String[] projectionVideo;
    private static Runnable refreshGalleryRunnable;
    private static long volumeBarLastTimeShown;
    private Sensor accelerometerSensor;
    private boolean accelerometerVertical;
    private boolean allowStartRecord;
    private AudioInfo audioInfo;
    private AudioRecord audioRecorder;
    private float audioVolume;
    private ValueAnimator audioVolumeAnimator;
    private Activity baseActivity;
    private boolean callInProgress;
    private int countLess;
    private AspectRatioFrameLayout currentAspectRatioFrameLayout;
    private float currentAspectRatioFrameLayoutRatio;
    private boolean currentAspectRatioFrameLayoutReady;
    private int currentAspectRatioFrameLayoutRotation;
    private int currentPlaylistNum;
    private TextureView currentTextureView;
    private FrameLayout currentTextureViewContainer;
    private boolean downloadingCurrentMessage;
    private ExternalObserver externalObserver;
    private View feedbackView;
    private DispatchQueue fileEncodingQueue;
    private BaseFragment flagSecureFragment;
    private boolean forceLoopCurrentPlaylist;
    private MessageObject goingToShowMessageObject;
    private Sensor gravitySensor;
    private int hasAudioFocus;
    private boolean hasRecordAudioFocus;
    private boolean ignoreOnPause;
    private boolean ignoreProximity;
    private boolean inputFieldHasText;
    private InternalObserver internalObserver;
    private boolean isDrawingWasReady;
    private boolean isStreamingCurrentAudio;
    private int lastChatAccount;
    private long lastChatEnterTime;
    private long lastChatLeaveTime;
    private ArrayList<Long> lastChatVisibleMessages;
    private long lastMediaCheckTime;
    private int lastMessageId;
    private long lastSaveTime;
    private TLRPC.EncryptedChat lastSecretChat;
    private TLRPC.User lastUser;
    private Sensor linearSensor;
    private boolean loadingPlaylist;
    private String[] mediaProjections;
    private PipRoundVideoView pipRoundVideoView;
    private int pipSwitchingState;
    private boolean playMusicAgain;
    private int playerNum;
    private boolean playerWasReady;
    private MessageObject playingMessageObject;
    private int playlistClassGuid;
    private PlaylistGlobalSearchParams playlistGlobalSearchParams;
    private long playlistMergeDialogId;
    private float previousAccValue;
    private boolean proximityHasDifferentValues;
    private Sensor proximitySensor;
    private boolean proximityTouched;
    private PowerManager.WakeLock proximityWakeLock;
    private ChatActivity raiseChat;
    private boolean raiseToEarRecord;
    private int raisedToBack;
    private int raisedToTop;
    private int raisedToTopSign;
    private long recordDialogId;
    private DispatchQueue recordQueue;
    private MessageObject recordReplyingMsg;
    private MessageObject recordReplyingTopMsg;
    private Runnable recordStartRunnable;
    private long recordStartTime;
    private long recordTimeCount;
    private TLRPC.TL_document recordingAudio;
    private File recordingAudioFile;
    private int recordingCurrentAccount;
    private boolean resumeAudioOnFocusGain;
    private long samplesCount;
    private float seekToProgressPending;
    private int sendAfterDone;
    private boolean sendAfterDoneNotify;
    private int sendAfterDoneScheduleDate;
    private SensorManager sensorManager;
    private boolean sensorsStarted;
    private String shouldSavePositionForCurrentAudio;
    private int startObserverToken;
    private StopMediaObserverRunnable stopMediaObserverRunnable;
    private long timeSinceRaise;
    private boolean useFrontSpeaker;
    private VideoPlayer videoPlayer;
    private ArrayList<MessageObject> voiceMessagesPlaylist;
    private SparseArray<MessageObject> voiceMessagesPlaylistMap;
    private boolean voiceMessagesPlaylistUnread;
    AudioManager.OnAudioFocusChangeListener audioRecordFocusChangedListener = new AudioManager.OnAudioFocusChangeListener() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda32
        @Override // android.media.AudioManager.OnAudioFocusChangeListener
        public final void onAudioFocusChange(int i) {
            MediaController.this.m372lambda$new$0$orgtelegrammessengerMediaController(i);
        }
    };
    private final Object videoConvertSync = new Object();
    private long lastTimestamp = 0;
    private float lastProximityValue = -100.0f;
    private float[] gravity = new float[3];
    private float[] gravityFast = new float[3];
    private float[] linearAcceleration = new float[3];
    private int audioFocus = 0;
    private ArrayList<VideoConvertMessage> videoConvertQueue = new ArrayList<>();
    private final Object videoQueueSync = new Object();
    private HashMap<String, MessageObject> generatingWaveform = new HashMap<>();
    private boolean isPaused = false;
    private VideoPlayer audioPlayer = null;
    private VideoPlayer emojiSoundPlayer = null;
    private int emojiSoundPlayerNum = 0;
    private float currentPlaybackSpeed = 1.0f;
    private float currentMusicPlaybackSpeed = 1.0f;
    private float fastPlaybackSpeed = 1.0f;
    private float fastMusicPlaybackSpeed = 1.0f;
    private long lastProgress = 0;
    private Timer progressTimer = null;
    private final Object progressTimerSync = new Object();
    private ArrayList<MessageObject> playlist = new ArrayList<>();
    private HashMap<Integer, MessageObject> playlistMap = new HashMap<>();
    private ArrayList<MessageObject> shuffledPlaylist = new ArrayList<>();
    private boolean[] playlistEndReached = {false, false};
    private int[] playlistMaxId = {Integer.MAX_VALUE, Integer.MAX_VALUE};
    private Runnable setLoadingRunnable = new Runnable() { // from class: org.telegram.messenger.MediaController.1
        @Override // java.lang.Runnable
        public void run() {
            if (MediaController.this.playingMessageObject != null) {
                FileLoader.getInstance(MediaController.this.playingMessageObject.currentAccount).setLoadingVideo(MediaController.this.playingMessageObject.getDocument(), true, false);
            }
        }
    };
    private int recordingGuid = -1;
    private short[] recordSamples = new short[1024];
    private final Object sync = new Object();
    private ArrayList<ByteBuffer> recordBuffers = new ArrayList<>();
    public int recordBufferSize = 1280;
    public int sampleRate = 48000;
    private Runnable recordRunnable = new AnonymousClass2();
    private final ValueAnimator.AnimatorUpdateListener audioVolumeUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.messenger.MediaController.3
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            MediaController.this.audioVolume = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            MediaController.this.setPlayerVolume();
        }
    };
    private ByteBuffer fileBuffer = ByteBuffer.allocateDirect(1920);

    /* loaded from: classes4.dex */
    public static class AudioEntry {
        public String author;
        public int duration;
        public String genre;
        public long id;
        public MessageObject messageObject;
        public String path;
        public String title;
    }

    /* loaded from: classes4.dex */
    public static class SavedFilterState {
        public float blurAngle;
        public float blurExcludeBlurSize;
        public Point blurExcludePoint;
        public float blurExcludeSize;
        public int blurType;
        public float contrastValue;
        public PhotoFilterView.CurvesToolValue curvesToolValue = new PhotoFilterView.CurvesToolValue();
        public float enhanceValue;
        public float exposureValue;
        public float fadeValue;
        public float grainValue;
        public float highlightsValue;
        public float saturationValue;
        public float shadowsValue;
        public float sharpenValue;
        public float softenSkinValue;
        public int tintHighlightsColor;
        public int tintShadowsColor;
        public float vignetteValue;
        public float warmthValue;
    }

    /* loaded from: classes4.dex */
    public interface VideoConvertorListener {
        boolean checkConversionCanceled();

        void didWriteData(long j, float f);
    }

    public static native int isOpusFile(String str);

    private native int startRecord(String str, int i);

    private native void stopRecord();

    public native int writeFrame(ByteBuffer byteBuffer, int i);

    public native byte[] getWaveform(String str);

    public native byte[] getWaveform2(short[] sArr, int i);

    static /* synthetic */ long access$1614(MediaController x0, long x1) {
        long j = x0.recordTimeCount + x1;
        x0.recordTimeCount = j;
        return j;
    }

    public boolean isBuffering() {
        VideoPlayer videoPlayer = this.audioPlayer;
        if (videoPlayer != null) {
            return videoPlayer.isBuffering();
        }
        return false;
    }

    /* loaded from: classes4.dex */
    private static class AudioBuffer {
        ByteBuffer buffer;
        byte[] bufferBytes;
        int finished;
        long pcmOffset;
        int size;

        public AudioBuffer(int capacity) {
            this.buffer = ByteBuffer.allocateDirect(capacity);
            this.bufferBytes = new byte[capacity];
        }
    }

    static {
        String[] strArr = new String[9];
        strArr[0] = "_id";
        strArr[1] = "bucket_id";
        strArr[2] = "bucket_display_name";
        strArr[3] = "_data";
        String str = "date_modified";
        strArr[4] = Build.VERSION.SDK_INT > 28 ? str : "datetaken";
        strArr[5] = "orientation";
        strArr[6] = "width";
        strArr[7] = "height";
        strArr[8] = "_size";
        projectionPhotos = strArr;
        String[] strArr2 = new String[9];
        strArr2[0] = "_id";
        strArr2[1] = "bucket_id";
        strArr2[2] = "bucket_display_name";
        strArr2[3] = "_data";
        if (Build.VERSION.SDK_INT <= 28) {
            str = "datetaken";
        }
        strArr2[4] = str;
        strArr2[5] = "duration";
        strArr2[6] = "width";
        strArr2[7] = "height";
        strArr2[8] = "_size";
        projectionVideo = strArr2;
        allMediaAlbums = new ArrayList<>();
        allPhotoAlbums = new ArrayList<>();
    }

    /* loaded from: classes4.dex */
    public static class AlbumEntry {
        public int bucketId;
        public String bucketName;
        public PhotoEntry coverPhoto;
        public ArrayList<PhotoEntry> photos = new ArrayList<>();
        public SparseArray<PhotoEntry> photosByIds = new SparseArray<>();
        public boolean videoOnly;

        public AlbumEntry(int bucketId, String bucketName, PhotoEntry coverPhoto) {
            this.bucketId = bucketId;
            this.bucketName = bucketName;
            this.coverPhoto = coverPhoto;
        }

        public void addPhoto(PhotoEntry photoEntry) {
            this.photos.add(photoEntry);
            this.photosByIds.put(photoEntry.imageId, photoEntry);
        }
    }

    /* loaded from: classes4.dex */
    public static class CropState {
        public float cropPx;
        public float cropPy;
        public float cropRotate;
        public boolean freeform;
        public int height;
        public boolean initied;
        public float lockedAspectRatio;
        public Matrix matrix;
        public boolean mirrored;
        public float scale;
        public float stateScale;
        public int transformHeight;
        public int transformRotation;
        public int transformWidth;
        public int width;
        public float cropScale = 1.0f;
        public float cropPw = 1.0f;
        public float cropPh = 1.0f;

        public CropState clone() {
            CropState cloned = new CropState();
            cloned.cropPx = this.cropPx;
            cloned.cropPy = this.cropPy;
            cloned.cropScale = this.cropScale;
            cloned.cropRotate = this.cropRotate;
            cloned.cropPw = this.cropPw;
            cloned.cropPh = this.cropPh;
            cloned.transformWidth = this.transformWidth;
            cloned.transformHeight = this.transformHeight;
            cloned.transformRotation = this.transformRotation;
            cloned.mirrored = this.mirrored;
            cloned.stateScale = this.stateScale;
            cloned.scale = this.scale;
            cloned.matrix = this.matrix;
            cloned.width = this.width;
            cloned.height = this.height;
            cloned.freeform = this.freeform;
            cloned.lockedAspectRatio = this.lockedAspectRatio;
            cloned.initied = this.initied;
            return cloned;
        }
    }

    /* loaded from: classes4.dex */
    public static class MediaEditState {
        public long averageDuration;
        public CharSequence caption;
        public CropState cropState;
        public ArrayList<VideoEditedInfo.MediaEntity> croppedMediaEntities;
        public String croppedPaintPath;
        public VideoEditedInfo editedInfo;
        public ArrayList<TLRPC.MessageEntity> entities;
        public String filterPath;
        public String fullPaintPath;
        public String imagePath;
        public boolean isCropped;
        public boolean isFiltered;
        public boolean isPainted;
        public ArrayList<VideoEditedInfo.MediaEntity> mediaEntities;
        public String paintPath;
        public SavedFilterState savedFilterState;
        public ArrayList<TLRPC.InputDocument> stickers;
        public String thumbPath;
        public int ttl;

        public String getPath() {
            return null;
        }

        public void reset() {
            this.caption = null;
            this.thumbPath = null;
            this.filterPath = null;
            this.imagePath = null;
            this.paintPath = null;
            this.croppedPaintPath = null;
            this.isFiltered = false;
            this.isPainted = false;
            this.isCropped = false;
            this.ttl = 0;
            this.mediaEntities = null;
            this.editedInfo = null;
            this.entities = null;
            this.savedFilterState = null;
            this.stickers = null;
            this.cropState = null;
        }

        public void copyFrom(MediaEditState state) {
            this.caption = state.caption;
            this.thumbPath = state.thumbPath;
            this.imagePath = state.imagePath;
            this.filterPath = state.filterPath;
            this.paintPath = state.paintPath;
            this.croppedPaintPath = state.croppedPaintPath;
            this.fullPaintPath = state.fullPaintPath;
            this.entities = state.entities;
            this.savedFilterState = state.savedFilterState;
            this.mediaEntities = state.mediaEntities;
            this.croppedMediaEntities = state.croppedMediaEntities;
            this.stickers = state.stickers;
            this.editedInfo = state.editedInfo;
            this.averageDuration = state.averageDuration;
            this.isFiltered = state.isFiltered;
            this.isPainted = state.isPainted;
            this.isCropped = state.isCropped;
            this.ttl = state.ttl;
            this.cropState = state.cropState;
        }
    }

    /* loaded from: classes4.dex */
    public static class PhotoEntry extends MediaEditState {
        public int bucketId;
        public boolean canDeleteAfter;
        public long dateTaken;
        public int duration;
        public int height;
        public int imageId;
        public boolean isMuted;
        public boolean isVideo;
        public int orientation;
        public String path;
        public long size;
        public int width;

        public PhotoEntry(int bucketId, int imageId, long dateTaken, String path, int orientation, boolean isVideo, int width, int height, long size) {
            this.bucketId = bucketId;
            this.imageId = imageId;
            this.dateTaken = dateTaken;
            this.path = path;
            this.width = width;
            this.height = height;
            this.size = size;
            if (isVideo) {
                this.duration = orientation;
            } else {
                this.orientation = orientation;
            }
            this.isVideo = isVideo;
        }

        @Override // org.telegram.messenger.MediaController.MediaEditState
        public String getPath() {
            return this.path;
        }

        @Override // org.telegram.messenger.MediaController.MediaEditState
        public void reset() {
            if (this.isVideo && this.filterPath != null) {
                new File(this.filterPath).delete();
                this.filterPath = null;
            }
            super.reset();
        }
    }

    /* loaded from: classes4.dex */
    public static class SearchImage extends MediaEditState {
        public CharSequence caption;
        public int date;
        public TLRPC.Document document;
        public int height;
        public String id;
        public String imageUrl;
        public TLRPC.BotInlineResult inlineResult;
        public HashMap<String, String> params;
        public TLRPC.Photo photo;
        public TLRPC.PhotoSize photoSize;
        public int size;
        public TLRPC.PhotoSize thumbPhotoSize;
        public String thumbUrl;
        public int type;
        public int width;

        @Override // org.telegram.messenger.MediaController.MediaEditState
        public String getPath() {
            if (this.photoSize != null) {
                return FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(this.photoSize, true).getAbsolutePath();
            }
            if (this.document != null) {
                return FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(this.document, true).getAbsolutePath();
            }
            return ImageLoader.getHttpFilePath(this.imageUrl, "jpg").getAbsolutePath();
        }

        @Override // org.telegram.messenger.MediaController.MediaEditState
        public void reset() {
            super.reset();
        }

        public String getAttachName() {
            TLRPC.PhotoSize photoSize = this.photoSize;
            if (photoSize != null) {
                return FileLoader.getAttachFileName(photoSize);
            }
            TLRPC.Document document = this.document;
            if (document != null) {
                return FileLoader.getAttachFileName(document);
            }
            return Utilities.MD5(this.imageUrl) + "." + ImageLoader.getHttpUrlExtension(this.imageUrl, "jpg");
        }

        public String getPathToAttach() {
            if (this.photoSize != null) {
                return FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(this.photoSize, true).getAbsolutePath();
            }
            if (this.document != null) {
                return FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(this.document, true).getAbsolutePath();
            }
            return this.imageUrl;
        }
    }

    /* renamed from: lambda$new$0$org-telegram-messenger-MediaController */
    public /* synthetic */ void m372lambda$new$0$orgtelegrammessengerMediaController(int focusChange) {
        if (focusChange != 1) {
            this.hasRecordAudioFocus = false;
        }
    }

    /* loaded from: classes4.dex */
    public static class VideoConvertMessage {
        public int currentAccount;
        public MessageObject messageObject;
        public VideoEditedInfo videoEditedInfo;

        public VideoConvertMessage(MessageObject object, VideoEditedInfo info) {
            this.messageObject = object;
            this.currentAccount = object.currentAccount;
            this.videoEditedInfo = info;
        }
    }

    /* renamed from: org.telegram.messenger.MediaController$2 */
    /* loaded from: classes4.dex */
    public class AnonymousClass2 implements Runnable {
        AnonymousClass2() {
            MediaController.this = this$0;
        }

        /* JADX WARN: Removed duplicated region for block: B:48:0x011f  */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void run() {
            /*
                Method dump skipped, instructions count: 367
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.AnonymousClass2.run():void");
        }

        /* renamed from: lambda$run$1$org-telegram-messenger-MediaController$2 */
        public /* synthetic */ void m396lambda$run$1$orgtelegrammessengerMediaController$2(final ByteBuffer finalBuffer, boolean flush) {
            while (finalBuffer.hasRemaining()) {
                int oldLimit = -1;
                if (finalBuffer.remaining() > MediaController.this.fileBuffer.remaining()) {
                    oldLimit = finalBuffer.limit();
                    finalBuffer.limit(MediaController.this.fileBuffer.remaining() + finalBuffer.position());
                }
                MediaController.this.fileBuffer.put(finalBuffer);
                if (MediaController.this.fileBuffer.position() == MediaController.this.fileBuffer.limit() || flush) {
                    MediaController mediaController = MediaController.this;
                    if (mediaController.writeFrame(mediaController.fileBuffer, !flush ? MediaController.this.fileBuffer.limit() : finalBuffer.position()) != 0) {
                        MediaController.this.fileBuffer.rewind();
                        MediaController mediaController2 = MediaController.this;
                        MediaController.access$1614(mediaController2, (mediaController2.fileBuffer.limit() / 2) / (MediaController.this.sampleRate / 1000));
                    }
                }
                if (oldLimit != -1) {
                    finalBuffer.limit(oldLimit);
                }
            }
            MediaController.this.recordQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaController$2$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    MediaController.AnonymousClass2.this.m395lambda$run$0$orgtelegrammessengerMediaController$2(finalBuffer);
                }
            });
        }

        /* renamed from: lambda$run$0$org-telegram-messenger-MediaController$2 */
        public /* synthetic */ void m395lambda$run$0$orgtelegrammessengerMediaController$2(ByteBuffer finalBuffer) {
            MediaController.this.recordBuffers.add(finalBuffer);
        }

        /* renamed from: lambda$run$2$org-telegram-messenger-MediaController$2 */
        public /* synthetic */ void m397lambda$run$2$orgtelegrammessengerMediaController$2(double amplitude) {
            NotificationCenter.getInstance(MediaController.this.recordingCurrentAccount).postNotificationName(NotificationCenter.recordProgressChanged, Integer.valueOf(MediaController.this.recordingGuid), Double.valueOf(amplitude));
        }
    }

    /* loaded from: classes4.dex */
    public class InternalObserver extends ContentObserver {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public InternalObserver() {
            super(null);
            MediaController.this = r1;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            MediaController.this.processMediaObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        }
    }

    /* loaded from: classes4.dex */
    public class ExternalObserver extends ContentObserver {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ExternalObserver() {
            super(null);
            MediaController.this = r1;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            MediaController.this.processMediaObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
    }

    /* loaded from: classes4.dex */
    public static class GalleryObserverInternal extends ContentObserver {
        public GalleryObserverInternal() {
            super(null);
        }

        private void scheduleReloadRunnable() {
            AndroidUtilities.runOnUIThread(MediaController.refreshGalleryRunnable = new Runnable() { // from class: org.telegram.messenger.MediaController$GalleryObserverInternal$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    MediaController.GalleryObserverInternal.this.m403x25ddcf72();
                }
            }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }

        /* renamed from: lambda$scheduleReloadRunnable$0$org-telegram-messenger-MediaController$GalleryObserverInternal */
        public /* synthetic */ void m403x25ddcf72() {
            if (!PhotoViewer.getInstance().isVisible()) {
                Runnable unused = MediaController.refreshGalleryRunnable = null;
                MediaController.loadGalleryPhotosAlbums(0);
                return;
            }
            scheduleReloadRunnable();
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (MediaController.refreshGalleryRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(MediaController.refreshGalleryRunnable);
            }
            scheduleReloadRunnable();
        }
    }

    /* loaded from: classes4.dex */
    public static class GalleryObserverExternal extends ContentObserver {
        public GalleryObserverExternal() {
            super(null);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (MediaController.refreshGalleryRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(MediaController.refreshGalleryRunnable);
            }
            AndroidUtilities.runOnUIThread(MediaController.refreshGalleryRunnable = MediaController$GalleryObserverExternal$$ExternalSyntheticLambda0.INSTANCE, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }

        public static /* synthetic */ void lambda$onChange$0() {
            Runnable unused = MediaController.refreshGalleryRunnable = null;
            MediaController.loadGalleryPhotosAlbums(0);
        }
    }

    public static void checkGallery() {
        AlbumEntry albumEntry;
        if (Build.VERSION.SDK_INT < 24 || (albumEntry = allPhotosAlbumEntry) == null) {
            return;
        }
        final int prevSize = albumEntry.photos.size();
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda37
            @Override // java.lang.Runnable
            public final void run() {
                MediaController.lambda$checkGallery$1(prevSize);
            }
        }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0031, code lost:
        if (r3 != null) goto L12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0033, code lost:
        r3.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x003b, code lost:
        if (r3 == null) goto L49;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0044, code lost:
        if (org.telegram.messenger.ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) goto L24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0046, code lost:
        r3 = android.provider.MediaStore.Images.Media.query(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new java.lang.String[]{"COUNT(_id)"}, null, null, null);
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x005a, code lost:
        if (r3 == null) goto L24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0060, code lost:
        if (r3.moveToNext() == false) goto L24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0066, code lost:
        r2 = r2 + r3.getInt(0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x006d, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x006e, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0071, code lost:
        if (r3 == null) goto L29;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0074, code lost:
        if (r13 == r2) goto L51;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0076, code lost:
        r0 = org.telegram.messenger.MediaController.refreshGalleryRunnable;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0078, code lost:
        if (r0 == null) goto L33;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x007a, code lost:
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0);
        org.telegram.messenger.MediaController.refreshGalleryRunnable = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x0080, code lost:
        loadGalleryPhotosAlbums(0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0083, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0084, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0085, code lost:
        if (r3 != null) goto L37;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0087, code lost:
        r3.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x008a, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:?, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static /* synthetic */ void lambda$checkGallery$1(int r13) {
        /*
            java.lang.String r0 = "COUNT(_id)"
            java.lang.String r1 = "android.permission.READ_EXTERNAL_STORAGE"
            r2 = 0
            r3 = 0
            r4 = 1
            r5 = 0
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch: java.lang.Throwable -> L37
            int r6 = r6.checkSelfPermission(r1)     // Catch: java.lang.Throwable -> L37
            if (r6 != 0) goto L31
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch: java.lang.Throwable -> L37
            android.content.ContentResolver r7 = r6.getContentResolver()     // Catch: java.lang.Throwable -> L37
            android.net.Uri r8 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI     // Catch: java.lang.Throwable -> L37
            java.lang.String[] r9 = new java.lang.String[r4]     // Catch: java.lang.Throwable -> L37
            r9[r5] = r0     // Catch: java.lang.Throwable -> L37
            r10 = 0
            r11 = 0
            r12 = 0
            android.database.Cursor r6 = android.provider.MediaStore.Images.Media.query(r7, r8, r9, r10, r11, r12)     // Catch: java.lang.Throwable -> L37
            r3 = r6
            if (r3 == 0) goto L31
            boolean r6 = r3.moveToNext()     // Catch: java.lang.Throwable -> L37
            if (r6 == 0) goto L31
            int r6 = r3.getInt(r5)     // Catch: java.lang.Throwable -> L37
            int r2 = r2 + r6
        L31:
            if (r3 == 0) goto L3e
        L33:
            r3.close()
            goto L3e
        L37:
            r6 = move-exception
            org.telegram.messenger.FileLog.e(r6)     // Catch: java.lang.Throwable -> L8b
            if (r3 == 0) goto L3e
            goto L33
        L3e:
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch: java.lang.Throwable -> L6d
            int r1 = r6.checkSelfPermission(r1)     // Catch: java.lang.Throwable -> L6d
            if (r1 != 0) goto L67
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch: java.lang.Throwable -> L6d
            android.content.ContentResolver r6 = r1.getContentResolver()     // Catch: java.lang.Throwable -> L6d
            android.net.Uri r7 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI     // Catch: java.lang.Throwable -> L6d
            java.lang.String[] r8 = new java.lang.String[r4]     // Catch: java.lang.Throwable -> L6d
            r8[r5] = r0     // Catch: java.lang.Throwable -> L6d
            r9 = 0
            r10 = 0
            r11 = 0
            android.database.Cursor r0 = android.provider.MediaStore.Images.Media.query(r6, r7, r8, r9, r10, r11)     // Catch: java.lang.Throwable -> L6d
            r3 = r0
            if (r3 == 0) goto L67
            boolean r0 = r3.moveToNext()     // Catch: java.lang.Throwable -> L6d
            if (r0 == 0) goto L67
            int r0 = r3.getInt(r5)     // Catch: java.lang.Throwable -> L6d
            int r2 = r2 + r0
        L67:
            if (r3 == 0) goto L74
        L69:
            r3.close()
            goto L74
        L6d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e(r0)     // Catch: java.lang.Throwable -> L84
            if (r3 == 0) goto L74
            goto L69
        L74:
            if (r13 == r2) goto L83
            java.lang.Runnable r0 = org.telegram.messenger.MediaController.refreshGalleryRunnable
            if (r0 == 0) goto L80
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r0 = 0
            org.telegram.messenger.MediaController.refreshGalleryRunnable = r0
        L80:
            loadGalleryPhotosAlbums(r5)
        L83:
            return
        L84:
            r0 = move-exception
            if (r3 == 0) goto L8a
            r3.close()
        L8a:
            throw r0
        L8b:
            r0 = move-exception
            if (r3 == 0) goto L91
            r3.close()
        L91:
            goto L93
        L92:
            throw r0
        L93:
            goto L92
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.lambda$checkGallery$1(int):void");
    }

    /* loaded from: classes4.dex */
    public final class StopMediaObserverRunnable implements Runnable {
        public int currentObserverToken;

        private StopMediaObserverRunnable() {
            MediaController.this = r1;
            this.currentObserverToken = 0;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.currentObserverToken == MediaController.this.startObserverToken) {
                try {
                    if (MediaController.this.internalObserver != null) {
                        ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.internalObserver);
                        MediaController.this.internalObserver = null;
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                try {
                    if (MediaController.this.externalObserver != null) {
                        ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.externalObserver);
                        MediaController.this.externalObserver = null;
                    }
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
        }
    }

    public static MediaController getInstance() {
        MediaController localInstance = Instance;
        if (localInstance == null) {
            synchronized (MediaController.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    MediaController mediaController = new MediaController();
                    localInstance = mediaController;
                    Instance = mediaController;
                }
            }
        }
        return localInstance;
    }

    public MediaController() {
        DispatchQueue dispatchQueue = new DispatchQueue("recordQueue");
        this.recordQueue = dispatchQueue;
        dispatchQueue.setPriority(10);
        DispatchQueue dispatchQueue2 = new DispatchQueue("fileEncodingQueue");
        this.fileEncodingQueue = dispatchQueue2;
        dispatchQueue2.setPriority(10);
        this.recordQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda42
            @Override // java.lang.Runnable
            public final void run() {
                MediaController.this.m373lambda$new$2$orgtelegrammessengerMediaController();
            }
        });
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                MediaController.this.m374lambda$new$3$orgtelegrammessengerMediaController();
            }
        });
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                MediaController.this.m375lambda$new$4$orgtelegrammessengerMediaController();
            }
        });
        String[] strArr = new String[7];
        strArr[0] = "_data";
        strArr[1] = "_display_name";
        strArr[2] = "bucket_display_name";
        strArr[3] = Build.VERSION.SDK_INT > 28 ? "date_modified" : "datetaken";
        strArr[4] = "title";
        strArr[5] = "width";
        strArr[6] = "height";
        this.mediaProjections = strArr;
        ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
        try {
            contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, new GalleryObserverExternal());
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            contentResolver.registerContentObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI, true, new GalleryObserverInternal());
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        try {
            contentResolver.registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true, new GalleryObserverExternal());
        } catch (Exception e3) {
            FileLog.e(e3);
        }
        try {
            contentResolver.registerContentObserver(MediaStore.Video.Media.INTERNAL_CONTENT_URI, true, new GalleryObserverInternal());
        } catch (Exception e4) {
            FileLog.e(e4);
        }
    }

    /* renamed from: lambda$new$2$org-telegram-messenger-MediaController */
    public /* synthetic */ void m373lambda$new$2$orgtelegrammessengerMediaController() {
        try {
            this.sampleRate = 48000;
            int minBuferSize = AudioRecord.getMinBufferSize(48000, 16, 2);
            if (minBuferSize <= 0) {
                minBuferSize = 1280;
            }
            this.recordBufferSize = minBuferSize;
            for (int a = 0; a < 5; a++) {
                ByteBuffer buffer = ByteBuffer.allocateDirect(this.recordBufferSize);
                buffer.order(ByteOrder.nativeOrder());
                this.recordBuffers.add(buffer);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$new$3$org-telegram-messenger-MediaController */
    public /* synthetic */ void m374lambda$new$3$orgtelegrammessengerMediaController() {
        try {
            this.currentPlaybackSpeed = MessagesController.getGlobalMainSettings().getFloat("playbackSpeed", 1.0f);
            this.currentMusicPlaybackSpeed = MessagesController.getGlobalMainSettings().getFloat("musicPlaybackSpeed", 1.0f);
            this.fastPlaybackSpeed = MessagesController.getGlobalMainSettings().getFloat("fastPlaybackSpeed", 1.8f);
            this.fastMusicPlaybackSpeed = MessagesController.getGlobalMainSettings().getFloat("fastMusicPlaybackSpeed", 1.8f);
            SensorManager sensorManager = (SensorManager) ApplicationLoader.applicationContext.getSystemService("sensor");
            this.sensorManager = sensorManager;
            this.linearSensor = sensorManager.getDefaultSensor(10);
            Sensor defaultSensor = this.sensorManager.getDefaultSensor(9);
            this.gravitySensor = defaultSensor;
            if (this.linearSensor == null || defaultSensor == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("gravity or linear sensor not found");
                }
                this.accelerometerSensor = this.sensorManager.getDefaultSensor(1);
                this.linearSensor = null;
                this.gravitySensor = null;
            }
            this.proximitySensor = this.sensorManager.getDefaultSensor(8);
            PowerManager powerManager = (PowerManager) ApplicationLoader.applicationContext.getSystemService("power");
            this.proximityWakeLock = powerManager.newWakeLock(32, "telegram:proximity_lock");
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            PhoneStateListener phoneStateListener = new AnonymousClass4();
            TelephonyManager mgr = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            if (mgr != null) {
                mgr.listen(phoneStateListener, 32);
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    /* renamed from: org.telegram.messenger.MediaController$4 */
    /* loaded from: classes4.dex */
    public class AnonymousClass4 extends PhoneStateListener {
        AnonymousClass4() {
            MediaController.this = this$0;
        }

        @Override // android.telephony.PhoneStateListener
        public void onCallStateChanged(final int state, String incomingNumber) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$4$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    MediaController.AnonymousClass4.this.m398x5c5477ee(state);
                }
            });
        }

        /* renamed from: lambda$onCallStateChanged$0$org-telegram-messenger-MediaController$4 */
        public /* synthetic */ void m398x5c5477ee(int state) {
            if (state != 1) {
                if (state == 0) {
                    MediaController.this.callInProgress = false;
                    return;
                } else if (state == 2) {
                    EmbedBottomSheet embedBottomSheet = EmbedBottomSheet.getInstance();
                    if (embedBottomSheet != null) {
                        embedBottomSheet.pause();
                    }
                    MediaController.this.callInProgress = true;
                    return;
                } else {
                    return;
                }
            }
            MediaController mediaController = MediaController.this;
            if (!mediaController.isPlayingMessage(mediaController.playingMessageObject) || MediaController.this.isMessagePaused()) {
                if (MediaController.this.recordStartRunnable != null || MediaController.this.recordingAudio != null) {
                    MediaController.this.stopRecording(2, false, 0);
                }
            } else {
                MediaController mediaController2 = MediaController.this;
                mediaController2.m383lambda$startAudioAgain$7$orgtelegrammessengerMediaController(mediaController2.playingMessageObject);
            }
            EmbedBottomSheet embedBottomSheet2 = EmbedBottomSheet.getInstance();
            if (embedBottomSheet2 != null) {
                embedBottomSheet2.pause();
            }
            MediaController.this.callInProgress = true;
        }
    }

    /* renamed from: lambda$new$4$org-telegram-messenger-MediaController */
    public /* synthetic */ void m375lambda$new$4$orgtelegrammessengerMediaController() {
        for (int a = 0; a < 4; a++) {
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.httpFileDidLoad);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.didReceiveNewMessages);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagesDeleted);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.removeAllMessagesFromDialog);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.musicDidLoad);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.mediaDidLoad);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.playerDidStartPlaying);
        }
    }

    @Override // android.media.AudioManager.OnAudioFocusChangeListener
    public void onAudioFocusChange(final int focusChange) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                MediaController.this.m376x37ee53c8(focusChange);
            }
        });
    }

    /* renamed from: lambda$onAudioFocusChange$5$org-telegram-messenger-MediaController */
    public /* synthetic */ void m376x37ee53c8(int focusChange) {
        if (focusChange == -1) {
            if (isPlayingMessage(getPlayingMessageObject()) && !isMessagePaused()) {
                m383lambda$startAudioAgain$7$orgtelegrammessengerMediaController(this.playingMessageObject);
            }
            this.hasAudioFocus = 0;
            this.audioFocus = 0;
        } else if (focusChange == 1) {
            this.audioFocus = 2;
            if (this.resumeAudioOnFocusGain) {
                this.resumeAudioOnFocusGain = false;
                if (isPlayingMessage(getPlayingMessageObject()) && isMessagePaused()) {
                    playMessage(getPlayingMessageObject());
                }
            }
        } else if (focusChange == -3) {
            this.audioFocus = 1;
        } else if (focusChange == -2) {
            this.audioFocus = 0;
            if (isPlayingMessage(getPlayingMessageObject()) && !isMessagePaused()) {
                m383lambda$startAudioAgain$7$orgtelegrammessengerMediaController(this.playingMessageObject);
                this.resumeAudioOnFocusGain = true;
            }
        }
        setPlayerVolume();
    }

    public void setPlayerVolume() {
        float volume;
        try {
            if (this.audioFocus != 1) {
                volume = 1.0f;
            } else {
                volume = 0.2f;
            }
            VideoPlayer videoPlayer = this.audioPlayer;
            if (videoPlayer != null) {
                videoPlayer.setVolume(this.audioVolume * volume);
            } else {
                VideoPlayer videoPlayer2 = this.videoPlayer;
                if (videoPlayer2 != null) {
                    videoPlayer2.setVolume(volume);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public VideoPlayer getVideoPlayer() {
        return this.videoPlayer;
    }

    private void startProgressTimer(MessageObject currentPlayingMessageObject) {
        synchronized (this.progressTimerSync) {
            Timer timer = this.progressTimer;
            if (timer != null) {
                try {
                    timer.cancel();
                    this.progressTimer = null;
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            currentPlayingMessageObject.getFileName();
            Timer timer2 = new Timer();
            this.progressTimer = timer2;
            timer2.schedule(new AnonymousClass5(currentPlayingMessageObject), 0L, 17L);
        }
    }

    /* renamed from: org.telegram.messenger.MediaController$5 */
    /* loaded from: classes4.dex */
    public class AnonymousClass5 extends TimerTask {
        final /* synthetic */ MessageObject val$currentPlayingMessageObject;

        AnonymousClass5(MessageObject messageObject) {
            MediaController.this = this$0;
            this.val$currentPlayingMessageObject = messageObject;
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            synchronized (MediaController.this.sync) {
                final MessageObject messageObject = this.val$currentPlayingMessageObject;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$5$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaController.AnonymousClass5.this.m399lambda$run$1$orgtelegrammessengerMediaController$5(messageObject);
                    }
                });
            }
        }

        /* renamed from: lambda$run$1$org-telegram-messenger-MediaController$5 */
        public /* synthetic */ void m399lambda$run$1$orgtelegrammessengerMediaController$5(MessageObject currentPlayingMessageObject) {
            long progress;
            long duration;
            final float value;
            float value2;
            if ((MediaController.this.audioPlayer != null || MediaController.this.videoPlayer != null) && !MediaController.this.isPaused) {
                try {
                    if (MediaController.this.videoPlayer != null) {
                        duration = MediaController.this.videoPlayer.getDuration();
                        progress = MediaController.this.videoPlayer.getCurrentPosition();
                        if (progress >= 0 && duration > 0) {
                            value2 = ((float) MediaController.this.videoPlayer.getBufferedPosition()) / ((float) duration);
                            value = ((float) progress) / ((float) duration);
                            if (value >= 1.0f) {
                                return;
                            }
                        }
                        return;
                    }
                    duration = MediaController.this.audioPlayer.getDuration();
                    progress = MediaController.this.audioPlayer.getCurrentPosition();
                    float value3 = duration >= 0 ? ((float) progress) / ((float) duration) : 0.0f;
                    float bufferedValue = ((float) MediaController.this.audioPlayer.getBufferedPosition()) / ((float) duration);
                    if (duration != C.TIME_UNSET && progress >= 0 && MediaController.this.seekToProgressPending == 0.0f) {
                        value = value3;
                        value2 = bufferedValue;
                    }
                    return;
                    MediaController.this.lastProgress = progress;
                    currentPlayingMessageObject.audioPlayerDuration = (int) (duration / 1000);
                    currentPlayingMessageObject.audioProgress = value;
                    currentPlayingMessageObject.audioProgressSec = (int) (MediaController.this.lastProgress / 1000);
                    currentPlayingMessageObject.bufferedProgress = value2;
                    if (value >= 0.0f && MediaController.this.shouldSavePositionForCurrentAudio != null && SystemClock.elapsedRealtime() - MediaController.this.lastSaveTime >= 1000) {
                        final String saveFor = MediaController.this.shouldSavePositionForCurrentAudio;
                        MediaController.this.lastSaveTime = SystemClock.elapsedRealtime();
                        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaController$5$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                MediaController.AnonymousClass5.lambda$run$0(saveFor, value);
                            }
                        });
                    }
                    NotificationCenter.getInstance(currentPlayingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(currentPlayingMessageObject.getId()), Float.valueOf(value));
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }

        public static /* synthetic */ void lambda$run$0(String saveFor, float value) {
            SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).edit();
            editor.putFloat(saveFor, value).commit();
        }
    }

    private void stopProgressTimer() {
        synchronized (this.progressTimerSync) {
            Timer timer = this.progressTimer;
            if (timer != null) {
                try {
                    timer.cancel();
                    this.progressTimer = null;
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
    }

    public void cleanup() {
        cleanupPlayer(true, true);
        this.audioInfo = null;
        this.playMusicAgain = false;
        for (int a = 0; a < 4; a++) {
            DownloadController.getInstance(a).cleanup();
        }
        this.videoConvertQueue.clear();
        this.generatingWaveform.clear();
        this.voiceMessagesPlaylist = null;
        this.voiceMessagesPlaylistMap = null;
        clearPlaylist();
        cancelVideoConvert(null);
    }

    private void clearPlaylist() {
        this.playlist.clear();
        this.playlistMap.clear();
        this.shuffledPlaylist.clear();
        this.playlistClassGuid = 0;
        boolean[] zArr = this.playlistEndReached;
        zArr[1] = false;
        zArr[0] = false;
        this.playlistMergeDialogId = 0L;
        int[] iArr = this.playlistMaxId;
        iArr[1] = Integer.MAX_VALUE;
        iArr[0] = Integer.MAX_VALUE;
        this.loadingPlaylist = false;
        this.playlistGlobalSearchParams = null;
    }

    public void startMediaObserver() {
        ApplicationLoader.applicationHandler.removeCallbacks(this.stopMediaObserverRunnable);
        this.startObserverToken++;
        try {
            if (this.internalObserver == null) {
                ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ExternalObserver externalObserver = new ExternalObserver();
                this.externalObserver = externalObserver;
                contentResolver.registerContentObserver(uri, false, externalObserver);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            if (this.externalObserver == null) {
                ContentResolver contentResolver2 = ApplicationLoader.applicationContext.getContentResolver();
                Uri uri2 = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
                InternalObserver internalObserver = new InternalObserver();
                this.internalObserver = internalObserver;
                contentResolver2.registerContentObserver(uri2, false, internalObserver);
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public void stopMediaObserver() {
        if (this.stopMediaObserverRunnable == null) {
            this.stopMediaObserverRunnable = new StopMediaObserverRunnable();
        }
        this.stopMediaObserverRunnable.currentObserverToken = this.startObserverToken;
        ApplicationLoader.applicationHandler.postDelayed(this.stopMediaObserverRunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    public void processMediaObserver(Uri uri) {
        Cursor cursor = null;
        try {
            try {
                try {
                    android.graphics.Point size = AndroidUtilities.getRealScreenSize();
                    cursor = ApplicationLoader.applicationContext.getContentResolver().query(uri, this.mediaProjections, null, null, "date_added DESC LIMIT 1");
                    final ArrayList<Long> screenshotDates = new ArrayList<>();
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            String data = cursor.getString(0);
                            String display_name = cursor.getString(1);
                            String album_name = cursor.getString(2);
                            long date = cursor.getLong(3);
                            String title = cursor.getString(4);
                            int photoW = cursor.getInt(5);
                            int photoH = cursor.getInt(6);
                            if ((data != null && data.toLowerCase().contains("screenshot")) || ((display_name != null && display_name.toLowerCase().contains("screenshot")) || ((album_name != null && album_name.toLowerCase().contains("screenshot")) || (title != null && title.toLowerCase().contains("screenshot"))))) {
                                if (photoW == 0 || photoH == 0) {
                                    try {
                                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                                        bmOptions.inJustDecodeBounds = true;
                                        BitmapFactory.decodeFile(data, bmOptions);
                                        photoW = bmOptions.outWidth;
                                        photoH = bmOptions.outHeight;
                                    } catch (Exception e) {
                                        screenshotDates.add(Long.valueOf(date));
                                    }
                                }
                                if (photoW <= 0 || photoH <= 0 || ((photoW == size.x && photoH == size.y) || (photoH == size.x && photoW == size.y))) {
                                    screenshotDates.add(Long.valueOf(date));
                                }
                            }
                        }
                        cursor.close();
                    }
                    if (!screenshotDates.isEmpty()) {
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda19
                            @Override // java.lang.Runnable
                            public final void run() {
                                MediaController.this.m380xa1e516d(screenshotDates);
                            }
                        });
                    }
                } catch (Exception e2) {
                    FileLog.e(e2);
                    if (cursor != null) {
                        cursor.close();
                    } else {
                        return;
                    }
                }
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Throwable th) {
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Exception e3) {
                    }
                }
                throw th;
            }
        } catch (Exception e4) {
        }
    }

    /* renamed from: lambda$processMediaObserver$6$org-telegram-messenger-MediaController */
    public /* synthetic */ void m380xa1e516d(ArrayList screenshotDates) {
        NotificationCenter.getInstance(this.lastChatAccount).postNotificationName(NotificationCenter.screenshotTook, new Object[0]);
        checkScreenshots(screenshotDates);
    }

    private void checkScreenshots(ArrayList<Long> dates) {
        if (dates == null || dates.isEmpty() || this.lastChatEnterTime == 0) {
            return;
        }
        if (this.lastUser == null && !(this.lastSecretChat instanceof TLRPC.TL_encryptedChat)) {
            return;
        }
        boolean send = false;
        for (int a = 0; a < dates.size(); a++) {
            Long date = dates.get(a);
            if ((this.lastMediaCheckTime == 0 || date.longValue() > this.lastMediaCheckTime) && date.longValue() >= this.lastChatEnterTime && (this.lastChatLeaveTime == 0 || date.longValue() <= this.lastChatLeaveTime + AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS)) {
                this.lastMediaCheckTime = Math.max(this.lastMediaCheckTime, date.longValue());
                send = true;
            }
        }
        if (send) {
            if (this.lastSecretChat != null) {
                SecretChatHelper.getInstance(this.lastChatAccount).sendScreenshotMessage(this.lastSecretChat, this.lastChatVisibleMessages, null);
            } else {
                SendMessagesHelper.getInstance(this.lastChatAccount).sendScreenshotMessage(this.lastUser, this.lastMessageId, null);
            }
        }
    }

    public void setLastVisibleMessageIds(int account, long enterTime, long leaveTime, TLRPC.User user, TLRPC.EncryptedChat encryptedChat, ArrayList<Long> visibleMessages, int visibleMessage) {
        this.lastChatEnterTime = enterTime;
        this.lastChatLeaveTime = leaveTime;
        this.lastChatAccount = account;
        this.lastSecretChat = encryptedChat;
        this.lastUser = user;
        this.lastMessageId = visibleMessage;
        this.lastChatVisibleMessages = visibleMessages;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        int newIndex;
        if (id == NotificationCenter.fileLoaded || id == NotificationCenter.httpFileDidLoad) {
            String fileName = (String) args[0];
            MessageObject messageObject = this.playingMessageObject;
            if (messageObject != null && messageObject.currentAccount == account) {
                String file = FileLoader.getAttachFileName(this.playingMessageObject.getDocument());
                if (file.equals(fileName)) {
                    if (this.downloadingCurrentMessage) {
                        this.playMusicAgain = true;
                        playMessage(this.playingMessageObject);
                    } else if (this.audioInfo == null) {
                        try {
                            File cacheFile = FileLoader.getInstance(UserConfig.selectedAccount).getPathToMessage(this.playingMessageObject.messageOwner);
                            this.audioInfo = AudioInfo.getAudioInfo(cacheFile);
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.messagesDeleted) {
            boolean scheduled = ((Boolean) args[2]).booleanValue();
            if (scheduled) {
                return;
            }
            long channelId = ((Long) args[1]).longValue();
            ArrayList<Integer> markAsDeletedMessages = (ArrayList) args[0];
            MessageObject messageObject2 = this.playingMessageObject;
            if (messageObject2 != null && channelId == messageObject2.messageOwner.peer_id.channel_id && markAsDeletedMessages.contains(Integer.valueOf(this.playingMessageObject.getId()))) {
                cleanupPlayer(true, true);
            }
            ArrayList<MessageObject> arrayList = this.voiceMessagesPlaylist;
            if (arrayList != null && !arrayList.isEmpty()) {
                MessageObject messageObject3 = this.voiceMessagesPlaylist.get(0);
                if (channelId == messageObject3.messageOwner.peer_id.channel_id) {
                    for (int a = 0; a < markAsDeletedMessages.size(); a++) {
                        Integer key = markAsDeletedMessages.get(a);
                        MessageObject messageObject4 = this.voiceMessagesPlaylistMap.get(key.intValue());
                        MessageObject messageObject5 = messageObject4;
                        this.voiceMessagesPlaylistMap.remove(key.intValue());
                        if (messageObject5 != null) {
                            this.voiceMessagesPlaylist.remove(messageObject5);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.removeAllMessagesFromDialog) {
            long did = ((Long) args[0]).longValue();
            MessageObject messageObject6 = this.playingMessageObject;
            if (messageObject6 != null && messageObject6.getDialogId() == did) {
                cleanupPlayer(false, true);
            }
        } else if (id == NotificationCenter.musicDidLoad) {
            long did2 = ((Long) args[0]).longValue();
            MessageObject messageObject7 = this.playingMessageObject;
            if (messageObject7 != null && messageObject7.isMusic() && this.playingMessageObject.getDialogId() == did2 && !this.playingMessageObject.scheduled) {
                ArrayList<MessageObject> arrayListBegin = (ArrayList) args[1];
                ArrayList<MessageObject> arrayListEnd = (ArrayList) args[2];
                this.playlist.addAll(0, arrayListBegin);
                this.playlist.addAll(arrayListEnd);
                int N = this.playlist.size();
                for (int a2 = 0; a2 < N; a2++) {
                    MessageObject object = this.playlist.get(a2);
                    this.playlistMap.put(Integer.valueOf(object.getId()), object);
                    int[] iArr = this.playlistMaxId;
                    iArr[0] = Math.min(iArr[0], object.getId());
                }
                sortPlaylist();
                if (SharedConfig.shuffleMusic) {
                    buildShuffledPlayList();
                } else {
                    MessageObject messageObject8 = this.playingMessageObject;
                    if (messageObject8 != null && (newIndex = this.playlist.indexOf(messageObject8)) >= 0) {
                        this.currentPlaylistNum = newIndex;
                    }
                }
                this.playlistClassGuid = ConnectionsManager.generateClassGuid();
            }
        } else if (id == NotificationCenter.mediaDidLoad) {
            int guid = ((Integer) args[3]).intValue();
            if (guid == this.playlistClassGuid && this.playingMessageObject != null) {
                long did3 = ((Long) args[0]).longValue();
                ((Integer) args[4]).intValue();
                ArrayList<MessageObject> arr = (ArrayList) args[2];
                DialogObject.isEncryptedDialog(did3);
                int loadIndex = did3 == this.playlistMergeDialogId ? 1 : 0;
                if (!arr.isEmpty()) {
                    this.playlistEndReached[loadIndex] = ((Boolean) args[5]).booleanValue();
                }
                int addedCount = 0;
                for (int a3 = 0; a3 < arr.size(); a3++) {
                    MessageObject message = arr.get(a3);
                    if (!this.playlistMap.containsKey(Integer.valueOf(message.getId()))) {
                        addedCount++;
                        this.playlist.add(0, message);
                        this.playlistMap.put(Integer.valueOf(message.getId()), message);
                        int[] iArr2 = this.playlistMaxId;
                        iArr2[loadIndex] = Math.min(iArr2[loadIndex], message.getId());
                    }
                }
                sortPlaylist();
                int newIndex2 = this.playlist.indexOf(this.playingMessageObject);
                if (newIndex2 >= 0) {
                    this.currentPlaylistNum = newIndex2;
                }
                this.loadingPlaylist = false;
                if (SharedConfig.shuffleMusic) {
                    buildShuffledPlayList();
                }
                if (addedCount != 0) {
                    NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.moreMusicDidLoad, Integer.valueOf(addedCount));
                }
            }
        } else if (id == NotificationCenter.didReceiveNewMessages) {
            boolean scheduled2 = ((Boolean) args[2]).booleanValue();
            if (scheduled2) {
                return;
            }
            ArrayList<MessageObject> arrayList2 = this.voiceMessagesPlaylist;
            if (arrayList2 != null && !arrayList2.isEmpty()) {
                MessageObject messageObject9 = this.voiceMessagesPlaylist.get(0);
                if (((Long) args[0]).longValue() == messageObject9.getDialogId()) {
                    ArrayList<MessageObject> arr2 = (ArrayList) args[1];
                    for (int a4 = 0; a4 < arr2.size(); a4++) {
                        MessageObject messageObject10 = arr2.get(a4);
                        MessageObject messageObject11 = messageObject10;
                        if ((messageObject11.isVoice() || messageObject11.isRoundVideo()) && (!this.voiceMessagesPlaylistUnread || (messageObject11.isContentUnread() && !messageObject11.isOut()))) {
                            this.voiceMessagesPlaylist.add(messageObject11);
                            this.voiceMessagesPlaylistMap.put(messageObject11.getId(), messageObject11);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.playerDidStartPlaying) {
            VideoPlayer p = (VideoPlayer) args[0];
            if (!getInstance().isCurrentPlayer(p)) {
                getInstance().m383lambda$startAudioAgain$7$orgtelegrammessengerMediaController(getInstance().getPlayingMessageObject());
            }
        }
    }

    public boolean isRecordingAudio() {
        return (this.recordStartRunnable == null && this.recordingAudio == null) ? false : true;
    }

    private boolean isNearToSensor(float value) {
        return value < 5.0f && value != this.proximitySensor.getMaximumRange();
    }

    public boolean isRecordingOrListeningByProximity() {
        MessageObject messageObject;
        return this.proximityTouched && (isRecordingAudio() || ((messageObject = this.playingMessageObject) != null && (messageObject.isVoice() || this.playingMessageObject.isRoundVideo())));
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent event) {
        PowerManager.WakeLock wakeLock;
        PowerManager.WakeLock wakeLock2;
        PowerManager.WakeLock wakeLock3;
        int i;
        PowerManager.WakeLock wakeLock4;
        boolean z;
        PowerManager.WakeLock wakeLock5;
        int sign;
        boolean goodValue;
        double alpha;
        if (this.sensorsStarted && VoIPService.getSharedInstance() == null) {
            if (event.sensor != this.proximitySensor) {
                if (event.sensor != this.accelerometerSensor) {
                    if (event.sensor != this.linearSensor) {
                        if (event.sensor == this.gravitySensor) {
                            float[] fArr = this.gravityFast;
                            float[] fArr2 = this.gravity;
                            float f = event.values[0];
                            fArr2[0] = f;
                            fArr[0] = f;
                            float[] fArr3 = this.gravityFast;
                            float[] fArr4 = this.gravity;
                            float f2 = event.values[1];
                            fArr4[1] = f2;
                            fArr3[1] = f2;
                            float[] fArr5 = this.gravityFast;
                            float[] fArr6 = this.gravity;
                            float f3 = event.values[2];
                            fArr6[2] = f3;
                            fArr5[2] = f3;
                        }
                    } else {
                        this.linearAcceleration[0] = event.values[0];
                        this.linearAcceleration[1] = event.values[1];
                        this.linearAcceleration[2] = event.values[2];
                    }
                } else {
                    if (this.lastTimestamp == 0) {
                        alpha = 0.9800000190734863d;
                    } else {
                        double d = event.timestamp - this.lastTimestamp;
                        Double.isNaN(d);
                        alpha = 1.0d / ((d / 1.0E9d) + 1.0d);
                    }
                    this.lastTimestamp = event.timestamp;
                    float[] fArr7 = this.gravity;
                    double d2 = fArr7[0];
                    Double.isNaN(d2);
                    double d3 = event.values[0];
                    Double.isNaN(d3);
                    fArr7[0] = (float) ((d2 * alpha) + ((1.0d - alpha) * d3));
                    float[] fArr8 = this.gravity;
                    double d4 = fArr8[1];
                    Double.isNaN(d4);
                    double d5 = event.values[1];
                    Double.isNaN(d5);
                    fArr8[1] = (float) ((d4 * alpha) + ((1.0d - alpha) * d5));
                    float[] fArr9 = this.gravity;
                    double d6 = fArr9[2];
                    Double.isNaN(d6);
                    double d7 = event.values[2];
                    Double.isNaN(d7);
                    fArr9[2] = (float) ((d6 * alpha) + ((1.0d - alpha) * d7));
                    this.gravityFast[0] = (this.gravity[0] * 0.8f) + (event.values[0] * 0.19999999f);
                    this.gravityFast[1] = (this.gravity[1] * 0.8f) + (event.values[1] * 0.19999999f);
                    this.gravityFast[2] = (this.gravity[2] * 0.8f) + (event.values[2] * 0.19999999f);
                    this.linearAcceleration[0] = event.values[0] - this.gravity[0];
                    this.linearAcceleration[1] = event.values[1] - this.gravity[1];
                    this.linearAcceleration[2] = event.values[2] - this.gravity[2];
                }
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("proximity changed to " + event.values[0] + " max value = " + this.proximitySensor.getMaximumRange());
                }
                float f4 = this.lastProximityValue;
                if (f4 == -100.0f) {
                    this.lastProximityValue = event.values[0];
                } else if (f4 != event.values[0]) {
                    this.proximityHasDifferentValues = true;
                }
                if (this.proximityHasDifferentValues) {
                    this.proximityTouched = isNearToSensor(event.values[0]);
                }
            }
            if (event.sensor == this.linearSensor || event.sensor == this.gravitySensor || event.sensor == this.accelerometerSensor) {
                float[] fArr10 = this.gravity;
                float f5 = fArr10[0];
                float[] fArr11 = this.linearAcceleration;
                float val = (f5 * fArr11[0]) + (fArr10[1] * fArr11[1]) + (fArr10[2] * fArr11[2]);
                int i2 = this.raisedToBack;
                if (i2 != 6 && ((val > 0.0f && this.previousAccValue > 0.0f) || (val < 0.0f && this.previousAccValue < 0.0f))) {
                    if (val > 0.0f) {
                        goodValue = val > 15.0f;
                        sign = 1;
                    } else {
                        goodValue = val < -15.0f;
                        sign = 2;
                    }
                    int i3 = this.raisedToTopSign;
                    if (i3 != 0 && i3 != sign) {
                        int i4 = this.raisedToTop;
                        if (i4 == 6 && goodValue) {
                            if (i2 < 6) {
                                int i5 = i2 + 1;
                                this.raisedToBack = i5;
                                if (i5 == 6) {
                                    this.raisedToTop = 0;
                                    this.raisedToTopSign = 0;
                                    this.countLess = 0;
                                    this.timeSinceRaise = System.currentTimeMillis();
                                    if (BuildVars.LOGS_ENABLED && BuildVars.DEBUG_PRIVATE_VERSION) {
                                        FileLog.d("motion detected");
                                    }
                                }
                            }
                        } else {
                            if (!goodValue) {
                                this.countLess++;
                            }
                            if (this.countLess == 10 || i4 != 6 || i2 != 0) {
                                this.raisedToTop = 0;
                                this.raisedToTopSign = 0;
                                this.raisedToBack = 0;
                                this.countLess = 0;
                            }
                        }
                    } else if (goodValue && i2 == 0 && (i3 == 0 || i3 == sign)) {
                        int i6 = this.raisedToTop;
                        if (i6 < 6 && !this.proximityTouched) {
                            this.raisedToTopSign = sign;
                            int i7 = i6 + 1;
                            this.raisedToTop = i7;
                            if (i7 == 6) {
                                this.countLess = 0;
                            }
                        }
                    } else {
                        if (!goodValue) {
                            this.countLess++;
                        }
                        if (i3 != sign || this.countLess == 10 || this.raisedToTop != 6 || i2 != 0) {
                            this.raisedToBack = 0;
                            this.raisedToTop = 0;
                            this.raisedToTopSign = 0;
                            this.countLess = 0;
                        }
                    }
                }
                this.previousAccValue = val;
                float[] fArr12 = this.gravityFast;
                this.accelerometerVertical = fArr12[1] > 2.5f && Math.abs(fArr12[2]) < 4.0f && Math.abs(this.gravityFast[0]) > 1.5f;
            }
            if (this.raisedToBack == 6 && this.accelerometerVertical && this.proximityTouched && !NotificationsController.audioManager.isWiredHeadsetOn()) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("sensor values reached");
                }
                if (this.playingMessageObject == null && this.recordStartRunnable == null && this.recordingAudio == null && !PhotoViewer.getInstance().isVisible() && ApplicationLoader.isScreenOn && !this.inputFieldHasText && this.allowStartRecord && this.raiseChat != null && !this.callInProgress) {
                    if (this.raiseToEarRecord) {
                        i = 0;
                    } else {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("start record");
                        }
                        this.useFrontSpeaker = true;
                        if (!this.raiseChat.playFirstUnreadVoiceMessage()) {
                            this.raiseToEarRecord = true;
                            this.useFrontSpeaker = false;
                            startRecording(this.raiseChat.getCurrentAccount(), this.raiseChat.getDialogId(), null, this.raiseChat.getThreadMessage(), this.raiseChat.getClassGuid());
                        }
                        if (!this.useFrontSpeaker) {
                            z = true;
                        } else {
                            z = true;
                            setUseFrontSpeaker(true);
                        }
                        this.ignoreOnPause = z;
                        if (!this.proximityHasDifferentValues || (wakeLock5 = this.proximityWakeLock) == null || wakeLock5.isHeld()) {
                            i = 0;
                        } else {
                            this.proximityWakeLock.acquire();
                            i = 0;
                        }
                    }
                } else {
                    MessageObject messageObject = this.playingMessageObject;
                    if (messageObject != null) {
                        if (!messageObject.isVoice() && !this.playingMessageObject.isRoundVideo()) {
                            i = 0;
                        } else if (this.useFrontSpeaker) {
                            i = 0;
                        } else {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("start listen");
                            }
                            if (this.proximityHasDifferentValues && (wakeLock4 = this.proximityWakeLock) != null && !wakeLock4.isHeld()) {
                                this.proximityWakeLock.acquire();
                            }
                            setUseFrontSpeaker(true);
                            i = 0;
                            startAudioAgain(false);
                            this.ignoreOnPause = true;
                        }
                    } else {
                        i = 0;
                    }
                }
                this.raisedToBack = i;
                this.raisedToTop = i;
                this.raisedToTopSign = i;
                this.countLess = i;
            } else {
                boolean z2 = this.proximityTouched;
                if (z2) {
                    if (this.playingMessageObject != null && !ApplicationLoader.mainInterfacePaused && ((this.playingMessageObject.isVoice() || this.playingMessageObject.isRoundVideo()) && !this.useFrontSpeaker && !NotificationsController.audioManager.isWiredHeadsetOn())) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("start listen by proximity only");
                        }
                        if (this.proximityHasDifferentValues && (wakeLock3 = this.proximityWakeLock) != null && !wakeLock3.isHeld()) {
                            this.proximityWakeLock.acquire();
                        }
                        setUseFrontSpeaker(true);
                        startAudioAgain(false);
                        this.ignoreOnPause = true;
                    }
                } else if (!z2) {
                    if (this.raiseToEarRecord) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("stop record");
                        }
                        stopRecording(2, false, 0);
                        this.raiseToEarRecord = false;
                        this.ignoreOnPause = false;
                        if (this.proximityHasDifferentValues && (wakeLock2 = this.proximityWakeLock) != null && wakeLock2.isHeld()) {
                            this.proximityWakeLock.release();
                        }
                    } else if (this.useFrontSpeaker) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("stop listen");
                        }
                        this.useFrontSpeaker = false;
                        startAudioAgain(true);
                        this.ignoreOnPause = false;
                        if (this.proximityHasDifferentValues && (wakeLock = this.proximityWakeLock) != null && wakeLock.isHeld()) {
                            this.proximityWakeLock.release();
                        }
                    }
                }
            }
            if (this.timeSinceRaise != 0 && this.raisedToBack == 6 && Math.abs(System.currentTimeMillis() - this.timeSinceRaise) > 1000) {
                this.raisedToBack = 0;
                this.raisedToTop = 0;
                this.raisedToTopSign = 0;
                this.countLess = 0;
                this.timeSinceRaise = 0L;
            }
        }
    }

    private void setUseFrontSpeaker(boolean value) {
        this.useFrontSpeaker = value;
        AudioManager audioManager = NotificationsController.audioManager;
        if (this.useFrontSpeaker) {
            audioManager.setBluetoothScoOn(false);
            audioManager.setSpeakerphoneOn(false);
            return;
        }
        audioManager.setSpeakerphoneOn(true);
    }

    public void startRecordingIfFromSpeaker() {
        if (!this.useFrontSpeaker || this.raiseChat == null || !this.allowStartRecord || !SharedConfig.raiseToSpeak) {
            return;
        }
        this.raiseToEarRecord = true;
        startRecording(this.raiseChat.getCurrentAccount(), this.raiseChat.getDialogId(), null, this.raiseChat.getThreadMessage(), this.raiseChat.getClassGuid());
        this.ignoreOnPause = true;
    }

    private void startAudioAgain(boolean paused) {
        VideoPlayer videoPlayer;
        MessageObject messageObject = this.playingMessageObject;
        if (messageObject == null) {
            return;
        }
        int i = 0;
        NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.audioRouteChanged, Boolean.valueOf(this.useFrontSpeaker));
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            if (!this.useFrontSpeaker) {
                i = 3;
            }
            videoPlayer2.setStreamType(i);
            if (!paused) {
                if (this.videoPlayer.getCurrentPosition() < 1000) {
                    this.videoPlayer.seekTo(0L);
                }
                this.videoPlayer.play();
                return;
            }
            m383lambda$startAudioAgain$7$orgtelegrammessengerMediaController(this.playingMessageObject);
            return;
        }
        boolean post = this.audioPlayer != null;
        final MessageObject currentMessageObject = this.playingMessageObject;
        float progress = this.playingMessageObject.audioProgress;
        int duration = this.playingMessageObject.audioPlayerDuration;
        if (paused || (videoPlayer = this.audioPlayer) == null || !videoPlayer.isPlaying() || duration * progress > 1.0f) {
            currentMessageObject.audioProgress = progress;
        } else {
            currentMessageObject.audioProgress = 0.0f;
        }
        cleanupPlayer(false, true);
        playMessage(currentMessageObject);
        if (paused) {
            if (post) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda20
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaController.this.m383lambda$startAudioAgain$7$orgtelegrammessengerMediaController(currentMessageObject);
                    }
                }, 100L);
            } else {
                m383lambda$startAudioAgain$7$orgtelegrammessengerMediaController(currentMessageObject);
            }
        }
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void setInputFieldHasText(boolean value) {
        this.inputFieldHasText = value;
    }

    public void setAllowStartRecord(boolean value) {
        this.allowStartRecord = value;
    }

    public void startRaiseToEarSensors(ChatActivity chatActivity) {
        if (chatActivity != null) {
            if ((this.accelerometerSensor == null && (this.gravitySensor == null || this.linearAcceleration == null)) || this.proximitySensor == null) {
                return;
            }
            this.raiseChat = chatActivity;
            if (!SharedConfig.raiseToSpeak) {
                MessageObject messageObject = this.playingMessageObject;
                if (messageObject == null) {
                    return;
                }
                if (!messageObject.isVoice() && !this.playingMessageObject.isRoundVideo()) {
                    return;
                }
            }
            if (!this.sensorsStarted) {
                float[] fArr = this.gravity;
                fArr[2] = 0.0f;
                fArr[1] = 0.0f;
                fArr[0] = 0.0f;
                float[] fArr2 = this.linearAcceleration;
                fArr2[2] = 0.0f;
                fArr2[1] = 0.0f;
                fArr2[0] = 0.0f;
                float[] fArr3 = this.gravityFast;
                fArr3[2] = 0.0f;
                fArr3[1] = 0.0f;
                fArr3[0] = 0.0f;
                this.lastTimestamp = 0L;
                this.previousAccValue = 0.0f;
                this.raisedToTop = 0;
                this.raisedToTopSign = 0;
                this.countLess = 0;
                this.raisedToBack = 0;
                Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaController.this.m384xfb47254e();
                    }
                });
                this.sensorsStarted = true;
            }
        }
    }

    /* renamed from: lambda$startRaiseToEarSensors$8$org-telegram-messenger-MediaController */
    public /* synthetic */ void m384xfb47254e() {
        Sensor sensor = this.gravitySensor;
        if (sensor != null) {
            this.sensorManager.registerListener(this, sensor, Indexable.MAX_BYTE_SIZE);
        }
        Sensor sensor2 = this.linearSensor;
        if (sensor2 != null) {
            this.sensorManager.registerListener(this, sensor2, Indexable.MAX_BYTE_SIZE);
        }
        Sensor sensor3 = this.accelerometerSensor;
        if (sensor3 != null) {
            this.sensorManager.registerListener(this, sensor3, Indexable.MAX_BYTE_SIZE);
        }
        this.sensorManager.registerListener(this, this.proximitySensor, 3);
    }

    public void stopRaiseToEarSensors(ChatActivity chatActivity, boolean fromChat) {
        PowerManager.WakeLock wakeLock;
        if (this.ignoreOnPause) {
            this.ignoreOnPause = false;
            return;
        }
        stopRecording(fromChat ? 2 : 0, false, 0);
        if (!this.sensorsStarted || this.ignoreOnPause) {
            return;
        }
        if ((this.accelerometerSensor == null && (this.gravitySensor == null || this.linearAcceleration == null)) || this.proximitySensor == null || this.raiseChat != chatActivity) {
            return;
        }
        this.raiseChat = null;
        this.sensorsStarted = false;
        this.accelerometerVertical = false;
        this.proximityTouched = false;
        this.raiseToEarRecord = false;
        this.useFrontSpeaker = false;
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                MediaController.this.m390xc4b35327();
            }
        });
        if (this.proximityHasDifferentValues && (wakeLock = this.proximityWakeLock) != null && wakeLock.isHeld()) {
            this.proximityWakeLock.release();
        }
    }

    /* renamed from: lambda$stopRaiseToEarSensors$9$org-telegram-messenger-MediaController */
    public /* synthetic */ void m390xc4b35327() {
        Sensor sensor = this.linearSensor;
        if (sensor != null) {
            this.sensorManager.unregisterListener(this, sensor);
        }
        Sensor sensor2 = this.gravitySensor;
        if (sensor2 != null) {
            this.sensorManager.unregisterListener(this, sensor2);
        }
        Sensor sensor3 = this.accelerometerSensor;
        if (sensor3 != null) {
            this.sensorManager.unregisterListener(this, sensor3);
        }
        this.sensorManager.unregisterListener(this, this.proximitySensor);
    }

    public void cleanupPlayer(boolean notify, boolean stopService) {
        cleanupPlayer(notify, stopService, false, false);
    }

    public void cleanupPlayer(boolean notify, boolean stopService, boolean byVoiceEnd, boolean transferPlayerToPhotoViewer) {
        PipRoundVideoView pipRoundVideoView;
        MessageObject messageObject;
        if (this.audioPlayer != null) {
            ValueAnimator valueAnimator = this.audioVolumeAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllUpdateListeners();
                this.audioVolumeAnimator.cancel();
            }
            if (this.audioPlayer.isPlaying() && (messageObject = this.playingMessageObject) != null && !messageObject.isVoice()) {
                final VideoPlayer playerFinal = this.audioPlayer;
                ValueAnimator valueAnimator2 = ValueAnimator.ofFloat(this.audioVolume, 0.0f);
                valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda10
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator3) {
                        MediaController.this.m366lambda$cleanupPlayer$10$orgtelegrammessengerMediaController(playerFinal, valueAnimator3);
                    }
                });
                valueAnimator2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.messenger.MediaController.6
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        try {
                            playerFinal.releasePlayer(true);
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                });
                valueAnimator2.setDuration(300L);
                valueAnimator2.start();
            } else {
                try {
                    this.audioPlayer.releasePlayer(true);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            this.audioPlayer = null;
            Theme.unrefAudioVisualizeDrawable(this.playingMessageObject);
        } else {
            VideoPlayer videoPlayer = this.videoPlayer;
            if (videoPlayer != null) {
                this.currentAspectRatioFrameLayout = null;
                this.currentTextureViewContainer = null;
                this.currentAspectRatioFrameLayoutReady = false;
                this.isDrawingWasReady = false;
                this.currentTextureView = null;
                this.goingToShowMessageObject = null;
                if (transferPlayerToPhotoViewer) {
                    PhotoViewer.getInstance().injectVideoPlayer(this.videoPlayer);
                    MessageObject messageObject2 = this.playingMessageObject;
                    this.goingToShowMessageObject = messageObject2;
                    NotificationCenter.getInstance(messageObject2.currentAccount).postNotificationName(NotificationCenter.messagePlayingGoingToStop, this.playingMessageObject, true);
                } else {
                    long position = videoPlayer.getCurrentPosition();
                    MessageObject messageObject3 = this.playingMessageObject;
                    if (messageObject3 != null && messageObject3.isVideo() && position > 0) {
                        this.playingMessageObject.audioProgressMs = (int) position;
                        NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingGoingToStop, this.playingMessageObject, false);
                    }
                    this.videoPlayer.releasePlayer(true);
                    this.videoPlayer = null;
                }
                try {
                    this.baseActivity.getWindow().clearFlags(128);
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
                if (this.playingMessageObject != null && !transferPlayerToPhotoViewer) {
                    AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
                    FileLoader.getInstance(this.playingMessageObject.currentAccount).removeLoadingVideo(this.playingMessageObject.getDocument(), true, false);
                }
            }
        }
        stopProgressTimer();
        this.lastProgress = 0L;
        this.isPaused = false;
        if (!this.useFrontSpeaker && !SharedConfig.raiseToSpeak) {
            ChatActivity chat = this.raiseChat;
            stopRaiseToEarSensors(this.raiseChat, false);
            this.raiseChat = chat;
        }
        PowerManager.WakeLock wakeLock = this.proximityWakeLock;
        if (wakeLock != null && wakeLock.isHeld() && !this.proximityTouched) {
            this.proximityWakeLock.release();
        }
        MessageObject messageObject4 = this.playingMessageObject;
        if (messageObject4 != null) {
            if (this.downloadingCurrentMessage) {
                FileLoader.getInstance(messageObject4.currentAccount).cancelLoadFile(this.playingMessageObject.getDocument());
            }
            MessageObject lastFile = this.playingMessageObject;
            if (notify) {
                this.playingMessageObject.resetPlayingProgress();
                NotificationCenter.getInstance(lastFile.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), 0);
            }
            this.playingMessageObject = null;
            this.downloadingCurrentMessage = false;
            if (notify) {
                NotificationsController.audioManager.abandonAudioFocus(this);
                this.hasAudioFocus = 0;
                int index = -1;
                ArrayList<MessageObject> arrayList = this.voiceMessagesPlaylist;
                if (arrayList != null) {
                    if (byVoiceEnd) {
                        int indexOf = arrayList.indexOf(lastFile);
                        index = indexOf;
                        if (indexOf >= 0) {
                            this.voiceMessagesPlaylist.remove(index);
                            this.voiceMessagesPlaylistMap.remove(lastFile.getId());
                            if (this.voiceMessagesPlaylist.isEmpty()) {
                                this.voiceMessagesPlaylist = null;
                                this.voiceMessagesPlaylistMap = null;
                            }
                        }
                    }
                    this.voiceMessagesPlaylist = null;
                    this.voiceMessagesPlaylistMap = null;
                }
                ArrayList<MessageObject> arrayList2 = this.voiceMessagesPlaylist;
                if (arrayList2 != null && index < arrayList2.size()) {
                    MessageObject nextVoiceMessage = this.voiceMessagesPlaylist.get(index);
                    playMessage(nextVoiceMessage);
                    if (!nextVoiceMessage.isRoundVideo() && (pipRoundVideoView = this.pipRoundVideoView) != null) {
                        pipRoundVideoView.close(true);
                        this.pipRoundVideoView = null;
                    }
                } else {
                    if ((lastFile.isVoice() || lastFile.isRoundVideo()) && lastFile.getId() != 0) {
                        startRecordingIfFromSpeaker();
                    }
                    NotificationCenter.getInstance(lastFile.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidReset, Integer.valueOf(lastFile.getId()), Boolean.valueOf(stopService));
                    this.pipSwitchingState = 0;
                    PipRoundVideoView pipRoundVideoView2 = this.pipRoundVideoView;
                    if (pipRoundVideoView2 != null) {
                        pipRoundVideoView2.close(true);
                        this.pipRoundVideoView = null;
                    }
                }
            }
            if (stopService) {
                Intent intent = new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class);
                ApplicationLoader.applicationContext.stopService(intent);
            }
        }
    }

    /* renamed from: lambda$cleanupPlayer$10$org-telegram-messenger-MediaController */
    public /* synthetic */ void m366lambda$cleanupPlayer$10$orgtelegrammessengerMediaController(VideoPlayer playerFinal, ValueAnimator valueAnimator1) {
        float volume;
        if (this.audioFocus != 1) {
            volume = 1.0f;
        } else {
            volume = 0.2f;
        }
        playerFinal.setVolume(((Float) valueAnimator1.getAnimatedValue()).floatValue() * volume);
    }

    public boolean isGoingToShowMessageObject(MessageObject messageObject) {
        return this.goingToShowMessageObject == messageObject;
    }

    public void resetGoingToShowMessageObject() {
        this.goingToShowMessageObject = null;
    }

    private boolean isSamePlayingMessage(MessageObject messageObject) {
        MessageObject messageObject2 = this.playingMessageObject;
        if (messageObject2 != null && messageObject2.getDialogId() == messageObject.getDialogId() && this.playingMessageObject.getId() == messageObject.getId()) {
            if ((this.playingMessageObject.eventId == 0) == (messageObject.eventId == 0)) {
                return true;
            }
        }
        return false;
    }

    public boolean seekToProgress(MessageObject messageObject, float progress) {
        if ((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null || !isSamePlayingMessage(messageObject)) {
            return false;
        }
        try {
            VideoPlayer videoPlayer = this.audioPlayer;
            if (videoPlayer != null) {
                long duration = videoPlayer.getDuration();
                if (duration == C.TIME_UNSET) {
                    this.seekToProgressPending = progress;
                } else {
                    this.playingMessageObject.audioProgress = progress;
                    int seekTo = (int) (((float) duration) * progress);
                    this.audioPlayer.seekTo(seekTo);
                    this.lastProgress = seekTo;
                }
            } else {
                VideoPlayer videoPlayer2 = this.videoPlayer;
                if (videoPlayer2 != null) {
                    videoPlayer2.seekTo(((float) videoPlayer2.getDuration()) * progress);
                }
            }
            NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidSeek, Integer.valueOf(this.playingMessageObject.getId()), Float.valueOf(progress));
            return true;
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    public long getDuration() {
        VideoPlayer videoPlayer = this.audioPlayer;
        if (videoPlayer == null) {
            return 0L;
        }
        return videoPlayer.getDuration();
    }

    public MessageObject getPlayingMessageObject() {
        return this.playingMessageObject;
    }

    public int getPlayingMessageObjectNum() {
        return this.currentPlaylistNum;
    }

    private void buildShuffledPlayList() {
        if (this.playlist.isEmpty()) {
            return;
        }
        ArrayList<MessageObject> all = new ArrayList<>(this.playlist);
        this.shuffledPlaylist.clear();
        MessageObject messageObject = this.playlist.get(this.currentPlaylistNum);
        all.remove(this.currentPlaylistNum);
        int count = all.size();
        for (int a = 0; a < count; a++) {
            int index = Utilities.random.nextInt(all.size());
            this.shuffledPlaylist.add(all.get(index));
            all.remove(index);
        }
        this.shuffledPlaylist.add(messageObject);
        this.currentPlaylistNum = this.shuffledPlaylist.size() - 1;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void loadMoreMusic() {
        MessageObject messageObject;
        TLObject request;
        long id;
        if (this.loadingPlaylist || (messageObject = this.playingMessageObject) == null || messageObject.scheduled || DialogObject.isEncryptedDialog(this.playingMessageObject.getDialogId()) || this.playlistClassGuid == 0) {
            return;
        }
        PlaylistGlobalSearchParams playlistGlobalSearchParams = this.playlistGlobalSearchParams;
        if (playlistGlobalSearchParams != null) {
            final int finalPlaylistGuid = this.playlistClassGuid;
            if (!playlistGlobalSearchParams.endReached && !this.playlist.isEmpty()) {
                final int currentAccount = this.playlist.get(0).currentAccount;
                if (this.playlistGlobalSearchParams.dialogId != 0) {
                    TLRPC.TL_messages_search req = new TLRPC.TL_messages_search();
                    req.q = this.playlistGlobalSearchParams.query;
                    req.limit = 20;
                    req.filter = this.playlistGlobalSearchParams.filter == null ? new TLRPC.TL_inputMessagesFilterEmpty() : this.playlistGlobalSearchParams.filter.filter;
                    req.peer = AccountInstance.getInstance(currentAccount).getMessagesController().getInputPeer(this.playlistGlobalSearchParams.dialogId);
                    ArrayList<MessageObject> arrayList = this.playlist;
                    req.offset_id = arrayList.get(arrayList.size() - 1).getId();
                    if (this.playlistGlobalSearchParams.minDate > 0) {
                        req.min_date = (int) (this.playlistGlobalSearchParams.minDate / 1000);
                    }
                    if (this.playlistGlobalSearchParams.maxDate > 0) {
                        req.min_date = (int) (this.playlistGlobalSearchParams.maxDate / 1000);
                    }
                    request = req;
                } else {
                    TLRPC.TL_messages_searchGlobal req2 = new TLRPC.TL_messages_searchGlobal();
                    req2.limit = 20;
                    req2.q = this.playlistGlobalSearchParams.query;
                    req2.filter = this.playlistGlobalSearchParams.filter.filter;
                    ArrayList<MessageObject> arrayList2 = this.playlist;
                    MessageObject lastMessage = arrayList2.get(arrayList2.size() - 1);
                    req2.offset_id = lastMessage.getId();
                    req2.offset_rate = this.playlistGlobalSearchParams.nextSearchRate;
                    req2.flags |= 1;
                    req2.folder_id = this.playlistGlobalSearchParams.folderId;
                    if (lastMessage.messageOwner.peer_id.channel_id != 0) {
                        id = -lastMessage.messageOwner.peer_id.channel_id;
                    } else if (lastMessage.messageOwner.peer_id.chat_id != 0) {
                        id = -lastMessage.messageOwner.peer_id.chat_id;
                    } else {
                        id = lastMessage.messageOwner.peer_id.user_id;
                    }
                    req2.offset_peer = MessagesController.getInstance(currentAccount).getInputPeer(id);
                    if (this.playlistGlobalSearchParams.minDate > 0) {
                        req2.min_date = (int) (this.playlistGlobalSearchParams.minDate / 1000);
                    }
                    if (this.playlistGlobalSearchParams.maxDate > 0) {
                        req2.min_date = (int) (this.playlistGlobalSearchParams.maxDate / 1000);
                    }
                    request = req2;
                }
                this.loadingPlaylist = true;
                ConnectionsManager.getInstance(currentAccount).sendRequest(request, new RequestDelegate() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda36
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaController.this.m371lambda$loadMoreMusic$12$orgtelegrammessengerMediaController(finalPlaylistGuid, currentAccount, tLObject, tL_error);
                    }
                });
                return;
            }
            return;
        }
        boolean[] zArr = this.playlistEndReached;
        if (!zArr[0]) {
            this.loadingPlaylist = true;
            AccountInstance.getInstance(this.playingMessageObject.currentAccount).getMediaDataController().loadMedia(this.playingMessageObject.getDialogId(), 50, this.playlistMaxId[0], 0, 4, 1, this.playlistClassGuid, 0);
        } else if (this.playlistMergeDialogId != 0 && !zArr[1]) {
            this.loadingPlaylist = true;
            AccountInstance.getInstance(this.playingMessageObject.currentAccount).getMediaDataController().loadMedia(this.playlistMergeDialogId, 50, this.playlistMaxId[0], 0, 4, 1, this.playlistClassGuid, 0);
        }
    }

    /* renamed from: lambda$loadMoreMusic$12$org-telegram-messenger-MediaController */
    public /* synthetic */ void m371lambda$loadMoreMusic$12$orgtelegrammessengerMediaController(final int finalPlaylistGuid, final int currentAccount, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                MediaController.this.m370lambda$loadMoreMusic$11$orgtelegrammessengerMediaController(finalPlaylistGuid, error, response, currentAccount);
            }
        });
    }

    /* renamed from: lambda$loadMoreMusic$11$org-telegram-messenger-MediaController */
    public /* synthetic */ void m370lambda$loadMoreMusic$11$orgtelegrammessengerMediaController(int finalPlaylistGuid, TLRPC.TL_error error, TLObject response, int currentAccount) {
        PlaylistGlobalSearchParams playlistGlobalSearchParams;
        if (this.playlistClassGuid != finalPlaylistGuid || (playlistGlobalSearchParams = this.playlistGlobalSearchParams) == null || this.playingMessageObject == null || error != null) {
            return;
        }
        this.loadingPlaylist = false;
        TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
        playlistGlobalSearchParams.nextSearchRate = res.next_rate;
        MessagesStorage.getInstance(currentAccount).putUsersAndChats(res.users, res.chats, true, true);
        MessagesController.getInstance(currentAccount).putUsers(res.users, false);
        MessagesController.getInstance(currentAccount).putChats(res.chats, false);
        int n = res.messages.size();
        int addedCount = 0;
        for (int i = 0; i < n; i++) {
            MessageObject messageObject = new MessageObject(currentAccount, res.messages.get(i), false, true);
            if (!this.playlistMap.containsKey(Integer.valueOf(messageObject.getId()))) {
                this.playlist.add(0, messageObject);
                this.playlistMap.put(Integer.valueOf(messageObject.getId()), messageObject);
                addedCount++;
            }
        }
        sortPlaylist();
        this.loadingPlaylist = false;
        this.playlistGlobalSearchParams.endReached = this.playlist.size() == this.playlistGlobalSearchParams.totalCount;
        if (SharedConfig.shuffleMusic) {
            buildShuffledPlayList();
        }
        if (addedCount != 0) {
            NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.moreMusicDidLoad, Integer.valueOf(addedCount));
        }
    }

    public boolean setPlaylist(ArrayList<MessageObject> messageObjects, MessageObject current, long mergeDialogId, PlaylistGlobalSearchParams globalSearchParams) {
        return setPlaylist(messageObjects, current, mergeDialogId, true, globalSearchParams);
    }

    public boolean setPlaylist(ArrayList<MessageObject> messageObjects, MessageObject current, long mergeDialogId) {
        return setPlaylist(messageObjects, current, mergeDialogId, true, null);
    }

    public boolean setPlaylist(ArrayList<MessageObject> messageObjects, MessageObject current, long mergeDialogId, boolean loadMusic, PlaylistGlobalSearchParams params) {
        if (this.playingMessageObject != current) {
            this.forceLoopCurrentPlaylist = !loadMusic;
            this.playlistMergeDialogId = mergeDialogId;
            this.playMusicAgain = !this.playlist.isEmpty();
            clearPlaylist();
            this.playlistGlobalSearchParams = params;
            boolean z = false;
            if (!messageObjects.isEmpty() && DialogObject.isEncryptedDialog(messageObjects.get(0).getDialogId())) {
                z = true;
            }
            boolean isSecretChat = z;
            int minId = Integer.MAX_VALUE;
            int maxId = Integer.MIN_VALUE;
            for (int a = messageObjects.size() - 1; a >= 0; a--) {
                MessageObject messageObject = messageObjects.get(a);
                if (messageObject.isMusic()) {
                    int id = messageObject.getId();
                    if (id > 0 || isSecretChat) {
                        minId = Math.min(minId, id);
                        maxId = Math.max(maxId, id);
                    }
                    this.playlist.add(messageObject);
                    this.playlistMap.put(Integer.valueOf(id), messageObject);
                }
            }
            sortPlaylist();
            int indexOf = this.playlist.indexOf(current);
            this.currentPlaylistNum = indexOf;
            if (indexOf == -1) {
                clearPlaylist();
                this.currentPlaylistNum = this.playlist.size();
                this.playlist.add(current);
                this.playlistMap.put(Integer.valueOf(current.getId()), current);
            }
            if (current.isMusic() && !current.scheduled) {
                if (SharedConfig.shuffleMusic) {
                    buildShuffledPlayList();
                }
                if (loadMusic) {
                    if (this.playlistGlobalSearchParams != null) {
                        this.playlistClassGuid = ConnectionsManager.generateClassGuid();
                    } else {
                        MediaDataController.getInstance(current.currentAccount).loadMusic(current.getDialogId(), minId, maxId);
                    }
                }
            }
            return playMessage(current);
        }
        int newIdx = this.playlist.indexOf(current);
        if (newIdx >= 0) {
            this.currentPlaylistNum = newIdx;
        }
        return playMessage(current);
    }

    private void sortPlaylist() {
        Collections.sort(this.playlist, MediaController$$ExternalSyntheticLambda35.INSTANCE);
    }

    public static /* synthetic */ int lambda$sortPlaylist$13(MessageObject o1, MessageObject o2) {
        int mid1 = o1.getId();
        int mid2 = o2.getId();
        long group1 = o1.messageOwner.grouped_id;
        long group2 = o2.messageOwner.grouped_id;
        if (mid1 < 0 && mid2 < 0) {
            if (group1 != 0 && group1 == group2) {
                return zzby$$ExternalSyntheticBackport0.m(mid1, mid2);
            }
            return zzby$$ExternalSyntheticBackport0.m(mid2, mid1);
        } else if (group1 != 0 && group1 == group2) {
            return zzby$$ExternalSyntheticBackport0.m(mid2, mid1);
        } else {
            return zzby$$ExternalSyntheticBackport0.m(mid1, mid2);
        }
    }

    public void playNextMessage() {
        playNextMessageWithoutOrder(false);
    }

    public boolean findMessageInPlaylistAndPlay(MessageObject messageObject) {
        int index = this.playlist.indexOf(messageObject);
        if (index == -1) {
            return playMessage(messageObject);
        }
        playMessageAtIndex(index);
        return true;
    }

    public void playMessageAtIndex(int index) {
        int i = this.currentPlaylistNum;
        if (i < 0 || i >= this.playlist.size()) {
            return;
        }
        this.currentPlaylistNum = index;
        this.playMusicAgain = true;
        MessageObject messageObject = this.playlist.get(index);
        if (this.playingMessageObject != null && !isSamePlayingMessage(messageObject)) {
            this.playingMessageObject.resetPlayingProgress();
        }
        playMessage(messageObject);
    }

    public void playNextMessageWithoutOrder(boolean byStop) {
        ArrayList<MessageObject> currentPlayList = SharedConfig.shuffleMusic ? this.shuffledPlaylist : this.playlist;
        if (byStop && ((SharedConfig.repeatMode == 2 || (SharedConfig.repeatMode == 1 && currentPlayList.size() == 1)) && !this.forceLoopCurrentPlaylist)) {
            cleanupPlayer(false, false);
            MessageObject messageObject = currentPlayList.get(this.currentPlaylistNum);
            messageObject.audioProgress = 0.0f;
            messageObject.audioProgressSec = 0;
            playMessage(messageObject);
            return;
        }
        boolean last = false;
        if (SharedConfig.playOrderReversed) {
            int i = this.currentPlaylistNum + 1;
            this.currentPlaylistNum = i;
            if (i >= currentPlayList.size()) {
                this.currentPlaylistNum = 0;
                last = true;
            }
        } else {
            int i2 = this.currentPlaylistNum - 1;
            this.currentPlaylistNum = i2;
            if (i2 < 0) {
                this.currentPlaylistNum = currentPlayList.size() - 1;
                last = true;
            }
        }
        if (last && byStop && SharedConfig.repeatMode == 0 && !this.forceLoopCurrentPlaylist) {
            VideoPlayer videoPlayer = this.audioPlayer;
            if (videoPlayer != null || this.videoPlayer != null) {
                if (videoPlayer != null) {
                    try {
                        videoPlayer.releasePlayer(true);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    this.audioPlayer = null;
                    Theme.unrefAudioVisualizeDrawable(this.playingMessageObject);
                } else {
                    this.currentAspectRatioFrameLayout = null;
                    this.currentTextureViewContainer = null;
                    this.currentAspectRatioFrameLayoutReady = false;
                    this.currentTextureView = null;
                    this.videoPlayer.releasePlayer(true);
                    this.videoPlayer = null;
                    try {
                        this.baseActivity.getWindow().clearFlags(128);
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                    AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
                    FileLoader.getInstance(this.playingMessageObject.currentAccount).removeLoadingVideo(this.playingMessageObject.getDocument(), true, false);
                }
                stopProgressTimer();
                this.lastProgress = 0L;
                this.isPaused = true;
                this.playingMessageObject.audioProgress = 0.0f;
                this.playingMessageObject.audioProgressSec = 0;
                NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), 0);
                NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
                return;
            }
            return;
        }
        int i3 = this.currentPlaylistNum;
        if (i3 < 0 || i3 >= currentPlayList.size()) {
            return;
        }
        MessageObject messageObject2 = this.playingMessageObject;
        if (messageObject2 != null) {
            messageObject2.resetPlayingProgress();
        }
        this.playMusicAgain = true;
        playMessage(currentPlayList.get(this.currentPlaylistNum));
    }

    public void playPreviousMessage() {
        int i;
        ArrayList<MessageObject> currentPlayList = SharedConfig.shuffleMusic ? this.shuffledPlaylist : this.playlist;
        if (currentPlayList.isEmpty() || (i = this.currentPlaylistNum) < 0 || i >= currentPlayList.size()) {
            return;
        }
        MessageObject currentSong = currentPlayList.get(this.currentPlaylistNum);
        if (currentSong.audioProgressSec > 10) {
            seekToProgress(currentSong, 0.0f);
            return;
        }
        if (SharedConfig.playOrderReversed) {
            int i2 = this.currentPlaylistNum - 1;
            this.currentPlaylistNum = i2;
            if (i2 < 0) {
                this.currentPlaylistNum = currentPlayList.size() - 1;
            }
        } else {
            int i3 = this.currentPlaylistNum + 1;
            this.currentPlaylistNum = i3;
            if (i3 >= currentPlayList.size()) {
                this.currentPlaylistNum = 0;
            }
        }
        if (this.currentPlaylistNum >= currentPlayList.size()) {
            return;
        }
        this.playMusicAgain = true;
        playMessage(currentPlayList.get(this.currentPlaylistNum));
    }

    public void checkIsNextMediaFileDownloaded() {
        MessageObject messageObject = this.playingMessageObject;
        if (messageObject == null || !messageObject.isMusic()) {
            return;
        }
        checkIsNextMusicFileDownloaded(this.playingMessageObject.currentAccount);
    }

    private void checkIsNextVoiceFileDownloaded(int currentAccount) {
        ArrayList<MessageObject> arrayList = this.voiceMessagesPlaylist;
        if (arrayList == null || arrayList.size() < 2) {
            return;
        }
        MessageObject nextAudio = this.voiceMessagesPlaylist.get(1);
        File file = null;
        if (nextAudio.messageOwner.attachPath != null && nextAudio.messageOwner.attachPath.length() > 0) {
            file = new File(nextAudio.messageOwner.attachPath);
            if (!file.exists()) {
                file = null;
            }
        }
        File cacheFile = file != null ? file : FileLoader.getInstance(currentAccount).getPathToMessage(nextAudio.messageOwner);
        cacheFile.exists();
        if (cacheFile != file && !cacheFile.exists()) {
            FileLoader.getInstance(currentAccount).loadFile(nextAudio.getDocument(), nextAudio, 0, 0);
        }
    }

    private void checkIsNextMusicFileDownloaded(int currentAccount) {
        int nextIndex;
        if (!DownloadController.getInstance(currentAccount).canDownloadNextTrack()) {
            return;
        }
        ArrayList<MessageObject> currentPlayList = SharedConfig.shuffleMusic ? this.shuffledPlaylist : this.playlist;
        if (currentPlayList == null || currentPlayList.size() < 2) {
            return;
        }
        if (SharedConfig.playOrderReversed) {
            nextIndex = this.currentPlaylistNum + 1;
            if (nextIndex >= currentPlayList.size()) {
                nextIndex = 0;
            }
        } else {
            nextIndex = this.currentPlaylistNum - 1;
            if (nextIndex < 0) {
                nextIndex = currentPlayList.size() - 1;
            }
        }
        if (nextIndex < 0 || nextIndex >= currentPlayList.size()) {
            return;
        }
        MessageObject nextAudio = currentPlayList.get(nextIndex);
        File file = null;
        if (!TextUtils.isEmpty(nextAudio.messageOwner.attachPath)) {
            file = new File(nextAudio.messageOwner.attachPath);
            if (!file.exists()) {
                file = null;
            }
        }
        File cacheFile = file != null ? file : FileLoader.getInstance(currentAccount).getPathToMessage(nextAudio.messageOwner);
        cacheFile.exists();
        if (cacheFile != file && !cacheFile.exists() && nextAudio.isMusic()) {
            FileLoader.getInstance(currentAccount).loadFile(nextAudio.getDocument(), nextAudio, 0, 0);
        }
    }

    public void setVoiceMessagesPlaylist(ArrayList<MessageObject> playlist, boolean unread) {
        this.voiceMessagesPlaylist = playlist;
        if (playlist != null) {
            this.voiceMessagesPlaylistUnread = unread;
            this.voiceMessagesPlaylistMap = new SparseArray<>();
            for (int a = 0; a < this.voiceMessagesPlaylist.size(); a++) {
                MessageObject messageObject = this.voiceMessagesPlaylist.get(a);
                this.voiceMessagesPlaylistMap.put(messageObject.getId(), messageObject);
            }
        }
    }

    private void checkAudioFocus(MessageObject messageObject) {
        int neededAudioFocus;
        int result;
        if (messageObject.isVoice() || messageObject.isRoundVideo()) {
            if (this.useFrontSpeaker) {
                neededAudioFocus = 3;
            } else {
                neededAudioFocus = 2;
            }
        } else {
            neededAudioFocus = 1;
        }
        if (this.hasAudioFocus != neededAudioFocus) {
            this.hasAudioFocus = neededAudioFocus;
            if (neededAudioFocus != 3) {
                result = NotificationsController.audioManager.requestAudioFocus(this, 3, neededAudioFocus == 2 ? 3 : 1);
            } else {
                result = NotificationsController.audioManager.requestAudioFocus(this, 0, 1);
            }
            if (result == 1) {
                this.audioFocus = 2;
            }
        }
    }

    public void setCurrentVideoVisible(boolean visible) {
        AspectRatioFrameLayout aspectRatioFrameLayout = this.currentAspectRatioFrameLayout;
        if (aspectRatioFrameLayout == null) {
            return;
        }
        if (visible) {
            PipRoundVideoView pipRoundVideoView = this.pipRoundVideoView;
            if (pipRoundVideoView != null) {
                this.pipSwitchingState = 2;
                pipRoundVideoView.close(true);
                this.pipRoundVideoView = null;
                return;
            }
            if (aspectRatioFrameLayout.getParent() == null) {
                this.currentTextureViewContainer.addView(this.currentAspectRatioFrameLayout);
            }
            this.videoPlayer.setTextureView(this.currentTextureView);
        } else if (aspectRatioFrameLayout.getParent() != null) {
            this.pipSwitchingState = 1;
            this.currentTextureViewContainer.removeView(this.currentAspectRatioFrameLayout);
        } else {
            if (this.pipRoundVideoView == null) {
                try {
                    PipRoundVideoView pipRoundVideoView2 = new PipRoundVideoView();
                    this.pipRoundVideoView = pipRoundVideoView2;
                    pipRoundVideoView2.show(this.baseActivity, new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            MediaController.this.m381xefb5270b();
                        }
                    });
                } catch (Exception e) {
                    this.pipRoundVideoView = null;
                }
            }
            PipRoundVideoView pipRoundVideoView3 = this.pipRoundVideoView;
            if (pipRoundVideoView3 != null) {
                this.videoPlayer.setTextureView(pipRoundVideoView3.getTextureView());
            }
        }
    }

    /* renamed from: lambda$setCurrentVideoVisible$14$org-telegram-messenger-MediaController */
    public /* synthetic */ void m381xefb5270b() {
        cleanupPlayer(true, true);
    }

    public void setTextureView(TextureView textureView, AspectRatioFrameLayout aspectRatioFrameLayout, FrameLayout container, boolean set) {
        if (textureView == null) {
            return;
        }
        boolean z = true;
        if (!set && this.currentTextureView == textureView) {
            this.pipSwitchingState = 1;
            this.currentTextureView = null;
            this.currentAspectRatioFrameLayout = null;
            this.currentTextureViewContainer = null;
        } else if (this.videoPlayer == null || textureView == this.currentTextureView) {
        } else {
            if (aspectRatioFrameLayout == null || !aspectRatioFrameLayout.isDrawingReady()) {
                z = false;
            }
            this.isDrawingWasReady = z;
            this.currentTextureView = textureView;
            PipRoundVideoView pipRoundVideoView = this.pipRoundVideoView;
            if (pipRoundVideoView != null) {
                this.videoPlayer.setTextureView(pipRoundVideoView.getTextureView());
            } else {
                this.videoPlayer.setTextureView(textureView);
            }
            this.currentAspectRatioFrameLayout = aspectRatioFrameLayout;
            this.currentTextureViewContainer = container;
            if (this.currentAspectRatioFrameLayoutReady && aspectRatioFrameLayout != null) {
                aspectRatioFrameLayout.setAspectRatio(this.currentAspectRatioFrameLayoutRatio, this.currentAspectRatioFrameLayoutRotation);
            }
        }
    }

    public void setBaseActivity(Activity activity, boolean set) {
        if (set) {
            this.baseActivity = activity;
        } else if (this.baseActivity == activity) {
            this.baseActivity = null;
        }
    }

    public void setFeedbackView(View view, boolean set) {
        if (set) {
            this.feedbackView = view;
        } else if (this.feedbackView == view) {
            this.feedbackView = null;
        }
    }

    public void setPlaybackSpeed(boolean music, float speed) {
        if (music) {
            if (this.currentMusicPlaybackSpeed >= 6.0f && speed == 1.0f && this.playingMessageObject != null) {
                this.audioPlayer.pause();
                final float p = this.playingMessageObject.audioProgress;
                final MessageObject currentMessage = this.playingMessageObject;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda22
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaController.this.m382x93032886(currentMessage, p);
                    }
                }, 50L);
            }
            this.currentMusicPlaybackSpeed = speed;
            if (Math.abs(speed - 1.0f) > 0.001f) {
                this.fastMusicPlaybackSpeed = speed;
            }
        } else {
            this.currentPlaybackSpeed = speed;
            if (Math.abs(speed - 1.0f) > 0.001f) {
                this.fastPlaybackSpeed = speed;
            }
        }
        VideoPlayer videoPlayer = this.audioPlayer;
        if (videoPlayer != null) {
            videoPlayer.setPlaybackSpeed(speed);
        } else {
            VideoPlayer videoPlayer2 = this.videoPlayer;
            if (videoPlayer2 != null) {
                videoPlayer2.setPlaybackSpeed(speed);
            }
        }
        MessagesController.getGlobalMainSettings().edit().putFloat(music ? "musicPlaybackSpeed" : "playbackSpeed", speed).putFloat(music ? "fastMusicPlaybackSpeed" : "fastPlaybackSpeed", music ? this.fastMusicPlaybackSpeed : this.fastPlaybackSpeed).commit();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.messagePlayingSpeedChanged, new Object[0]);
    }

    /* renamed from: lambda$setPlaybackSpeed$15$org-telegram-messenger-MediaController */
    public /* synthetic */ void m382x93032886(MessageObject currentMessage, float p) {
        if (this.audioPlayer != null && this.playingMessageObject != null && !this.isPaused) {
            if (isSamePlayingMessage(currentMessage)) {
                seekToProgress(this.playingMessageObject, p);
            }
            this.audioPlayer.play();
        }
    }

    public float getPlaybackSpeed(boolean music) {
        return music ? this.currentMusicPlaybackSpeed : this.currentPlaybackSpeed;
    }

    public float getFastPlaybackSpeed(boolean music) {
        return music ? this.fastMusicPlaybackSpeed : this.fastPlaybackSpeed;
    }

    public void updateVideoState(MessageObject messageObject, int[] playCount, boolean destroyAtEnd, boolean playWhenReady, int playbackState) {
        MessageObject messageObject2;
        if (this.videoPlayer == null) {
            return;
        }
        if (playbackState == 4 || playbackState == 1) {
            try {
                this.baseActivity.getWindow().clearFlags(128);
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else {
            try {
                this.baseActivity.getWindow().addFlags(128);
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        if (playbackState == 3) {
            this.playerWasReady = true;
            MessageObject messageObject3 = this.playingMessageObject;
            if (messageObject3 != null && (messageObject3.isVideo() || this.playingMessageObject.isRoundVideo())) {
                AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
                FileLoader.getInstance(messageObject.currentAccount).removeLoadingVideo(this.playingMessageObject.getDocument(), true, false);
            }
            this.currentAspectRatioFrameLayoutReady = true;
        } else if (playbackState == 2) {
            if (!playWhenReady || (messageObject2 = this.playingMessageObject) == null) {
                return;
            }
            if (messageObject2.isVideo() || this.playingMessageObject.isRoundVideo()) {
                if (this.playerWasReady) {
                    this.setLoadingRunnable.run();
                } else {
                    AndroidUtilities.runOnUIThread(this.setLoadingRunnable, 1000L);
                }
            }
        } else if (this.videoPlayer.isPlaying() && playbackState == 4) {
            if (this.playingMessageObject.isVideo() && !destroyAtEnd && (playCount == null || playCount[0] < 4)) {
                this.videoPlayer.seekTo(0L);
                if (playCount != null) {
                    playCount[0] = playCount[0] + 1;
                    return;
                }
                return;
            }
            cleanupPlayer(true, true, true, false);
        }
    }

    public void injectVideoPlayer(VideoPlayer player, MessageObject messageObject) {
        if (player == null || messageObject == null) {
            return;
        }
        FileLoader.getInstance(messageObject.currentAccount).setLoadingVideoForPlayer(messageObject.getDocument(), true);
        this.playerWasReady = false;
        clearPlaylist();
        this.videoPlayer = player;
        this.playingMessageObject = messageObject;
        int tag = this.playerNum + 1;
        this.playerNum = tag;
        player.setDelegate(new AnonymousClass7(tag, messageObject, null, true));
        this.currentAspectRatioFrameLayoutReady = false;
        TextureView textureView = this.currentTextureView;
        if (textureView != null) {
            this.videoPlayer.setTextureView(textureView);
        }
        checkAudioFocus(messageObject);
        setPlayerVolume();
        this.isPaused = false;
        this.lastProgress = 0L;
        this.playingMessageObject = messageObject;
        if (!SharedConfig.raiseToSpeak) {
            startRaiseToEarSensors(this.raiseChat);
        }
        startProgressTimer(this.playingMessageObject);
        NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidStart, messageObject);
    }

    /* renamed from: org.telegram.messenger.MediaController$7 */
    /* loaded from: classes4.dex */
    public class AnonymousClass7 implements VideoPlayer.VideoPlayerDelegate {
        final /* synthetic */ boolean val$destroyAtEnd;
        final /* synthetic */ MessageObject val$messageObject;
        final /* synthetic */ int[] val$playCount;
        final /* synthetic */ int val$tag;

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public /* synthetic */ void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public /* synthetic */ void onSeekFinished(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public /* synthetic */ void onSeekStarted(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
        }

        AnonymousClass7(int i, MessageObject messageObject, int[] iArr, boolean z) {
            MediaController.this = this$0;
            this.val$tag = i;
            this.val$messageObject = messageObject;
            this.val$playCount = iArr;
            this.val$destroyAtEnd = z;
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onStateChanged(boolean playWhenReady, int playbackState) {
            if (this.val$tag == MediaController.this.playerNum) {
                MediaController.this.updateVideoState(this.val$messageObject, this.val$playCount, this.val$destroyAtEnd, playWhenReady, playbackState);
            }
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onError(VideoPlayer player, Exception e) {
            FileLog.e(e);
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            MediaController.this.currentAspectRatioFrameLayoutRotation = unappliedRotationDegrees;
            if (unappliedRotationDegrees == 90 || unappliedRotationDegrees == 270) {
                width = height;
                height = width;
            }
            MediaController.this.currentAspectRatioFrameLayoutRatio = height == 0 ? 1.0f : (width * pixelWidthHeightRatio) / height;
            if (MediaController.this.currentAspectRatioFrameLayout != null) {
                MediaController.this.currentAspectRatioFrameLayout.setAspectRatio(MediaController.this.currentAspectRatioFrameLayoutRatio, MediaController.this.currentAspectRatioFrameLayoutRotation);
            }
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onRenderedFirstFrame() {
            if (MediaController.this.currentAspectRatioFrameLayout != null && !MediaController.this.currentAspectRatioFrameLayout.isDrawingReady()) {
                MediaController.this.isDrawingWasReady = true;
                MediaController.this.currentAspectRatioFrameLayout.setDrawingReady(true);
                MediaController.this.currentTextureViewContainer.setTag(1);
            }
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
            if (MediaController.this.videoPlayer == null) {
                return false;
            }
            if (MediaController.this.pipSwitchingState == 2) {
                if (MediaController.this.currentAspectRatioFrameLayout != null) {
                    if (MediaController.this.isDrawingWasReady) {
                        MediaController.this.currentAspectRatioFrameLayout.setDrawingReady(true);
                    }
                    if (MediaController.this.currentAspectRatioFrameLayout.getParent() == null) {
                        MediaController.this.currentTextureViewContainer.addView(MediaController.this.currentAspectRatioFrameLayout);
                    }
                    if (MediaController.this.currentTextureView.getSurfaceTexture() != surfaceTexture) {
                        MediaController.this.currentTextureView.setSurfaceTexture(surfaceTexture);
                    }
                    MediaController.this.videoPlayer.setTextureView(MediaController.this.currentTextureView);
                }
                MediaController.this.pipSwitchingState = 0;
                return true;
            } else if (MediaController.this.pipSwitchingState == 1) {
                if (MediaController.this.baseActivity != null) {
                    if (MediaController.this.pipRoundVideoView == null) {
                        try {
                            MediaController.this.pipRoundVideoView = new PipRoundVideoView();
                            MediaController.this.pipRoundVideoView.show(MediaController.this.baseActivity, new Runnable() { // from class: org.telegram.messenger.MediaController$7$$ExternalSyntheticLambda0
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MediaController.AnonymousClass7.this.m400x819a586();
                                }
                            });
                        } catch (Exception e) {
                            MediaController.this.pipRoundVideoView = null;
                        }
                    }
                    if (MediaController.this.pipRoundVideoView != null) {
                        if (MediaController.this.pipRoundVideoView.getTextureView().getSurfaceTexture() != surfaceTexture) {
                            MediaController.this.pipRoundVideoView.getTextureView().setSurfaceTexture(surfaceTexture);
                        }
                        MediaController.this.videoPlayer.setTextureView(MediaController.this.pipRoundVideoView.getTextureView());
                    }
                }
                MediaController.this.pipSwitchingState = 0;
                return true;
            } else if (!PhotoViewer.hasInstance() || !PhotoViewer.getInstance().isInjectingVideoPlayer()) {
                return false;
            } else {
                PhotoViewer.getInstance().injectVideoPlayerSurface(surfaceTexture);
                return true;
            }
        }

        /* renamed from: lambda$onSurfaceDestroyed$0$org-telegram-messenger-MediaController$7 */
        public /* synthetic */ void m400x819a586() {
            MediaController.this.cleanupPlayer(true, true);
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }
    }

    public void playEmojiSound(final AccountInstance accountInstance, String emoji, final MessagesController.EmojiSound sound, final boolean loadOnly) {
        if (sound == null) {
            return;
        }
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda23
            @Override // java.lang.Runnable
            public final void run() {
                MediaController.this.m378lambda$playEmojiSound$18$orgtelegrammessengerMediaController(sound, accountInstance, loadOnly);
            }
        });
    }

    /* renamed from: lambda$playEmojiSound$18$org-telegram-messenger-MediaController */
    public /* synthetic */ void m378lambda$playEmojiSound$18$orgtelegrammessengerMediaController(MessagesController.EmojiSound sound, final AccountInstance accountInstance, boolean loadOnly) {
        final TLRPC.Document document = new TLRPC.TL_document();
        document.access_hash = sound.accessHash;
        document.id = sound.id;
        document.mime_type = "sound/ogg";
        document.file_reference = sound.fileReference;
        document.dc_id = accountInstance.getConnectionsManager().getCurrentDatacenterId();
        final File file = FileLoader.getInstance(accountInstance.getCurrentAccount()).getPathToAttach(document, true);
        if (file.exists()) {
            if (loadOnly) {
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    MediaController.this.m377lambda$playEmojiSound$16$orgtelegrammessengerMediaController(file);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda41
            @Override // java.lang.Runnable
            public final void run() {
                AccountInstance.this.getFileLoader().loadFile(document, null, 1, 1);
            }
        });
    }

    /* renamed from: lambda$playEmojiSound$16$org-telegram-messenger-MediaController */
    public /* synthetic */ void m377lambda$playEmojiSound$16$orgtelegrammessengerMediaController(File file) {
        try {
            int tag = this.emojiSoundPlayerNum + 1;
            this.emojiSoundPlayerNum = tag;
            VideoPlayer videoPlayer = this.emojiSoundPlayer;
            if (videoPlayer != null) {
                videoPlayer.releasePlayer(true);
            }
            VideoPlayer videoPlayer2 = new VideoPlayer(false);
            this.emojiSoundPlayer = videoPlayer2;
            videoPlayer2.setDelegate(new AnonymousClass8(tag));
            this.emojiSoundPlayer.preparePlayer(Uri.fromFile(file), "other");
            this.emojiSoundPlayer.setStreamType(3);
            this.emojiSoundPlayer.play();
        } catch (Exception e) {
            FileLog.e(e);
            VideoPlayer videoPlayer3 = this.emojiSoundPlayer;
            if (videoPlayer3 != null) {
                videoPlayer3.releasePlayer(true);
                this.emojiSoundPlayer = null;
            }
        }
    }

    /* renamed from: org.telegram.messenger.MediaController$8 */
    /* loaded from: classes4.dex */
    public class AnonymousClass8 implements VideoPlayer.VideoPlayerDelegate {
        final /* synthetic */ int val$tag;

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public /* synthetic */ void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public /* synthetic */ void onSeekFinished(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public /* synthetic */ void onSeekStarted(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
        }

        AnonymousClass8(int i) {
            MediaController.this = this$0;
            this.val$tag = i;
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onStateChanged(boolean playWhenReady, final int playbackState) {
            final int i = this.val$tag;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$8$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    MediaController.AnonymousClass8.this.m401lambda$onStateChanged$0$orgtelegrammessengerMediaController$8(i, playbackState);
                }
            });
        }

        /* renamed from: lambda$onStateChanged$0$org-telegram-messenger-MediaController$8 */
        public /* synthetic */ void m401lambda$onStateChanged$0$orgtelegrammessengerMediaController$8(int tag, int playbackState) {
            if (tag == MediaController.this.emojiSoundPlayerNum && playbackState == 4 && MediaController.this.emojiSoundPlayer != null) {
                try {
                    MediaController.this.emojiSoundPlayer.releasePlayer(true);
                    MediaController.this.emojiSoundPlayer = null;
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onError(VideoPlayer player, Exception e) {
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onRenderedFirstFrame() {
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }
    }

    public void checkVolumeBarUI() {
        try {
            long now = System.currentTimeMillis();
            if (Math.abs(now - volumeBarLastTimeShown) < DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
                return;
            }
            AudioManager audioManager = (AudioManager) ApplicationLoader.applicationContext.getSystemService("audio");
            int stream = this.useFrontSpeaker ? 0 : 3;
            int volume = audioManager.getStreamVolume(stream);
            if (volume == 0) {
                audioManager.adjustStreamVolume(stream, volume, 1);
                volumeBarLastTimeShown = now;
            }
        } catch (Exception e) {
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:163:0x03b5  */
    /* JADX WARN: Removed duplicated region for block: B:164:0x03ba  */
    /* JADX WARN: Removed duplicated region for block: B:167:0x03c9  */
    /* JADX WARN: Removed duplicated region for block: B:168:0x03dd A[ORIG_RETURN, RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:199:0x049a  */
    /* JADX WARN: Removed duplicated region for block: B:201:0x04a5  */
    /* JADX WARN: Removed duplicated region for block: B:215:0x056c  */
    /* JADX WARN: Removed duplicated region for block: B:225:0x059e  */
    /* JADX WARN: Removed duplicated region for block: B:228:0x05b8  */
    /* JADX WARN: Removed duplicated region for block: B:231:0x05c1  */
    /* JADX WARN: Removed duplicated region for block: B:254:0x066d  */
    /* JADX WARN: Removed duplicated region for block: B:266:0x06c7  */
    /* JADX WARN: Removed duplicated region for block: B:279:0x0601 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:289:0x04be A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:291:0x0480 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean playMessage(org.telegram.messenger.MessageObject r29) {
        /*
            Method dump skipped, instructions count: 1778
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.playMessage(org.telegram.messenger.MessageObject):boolean");
    }

    /* renamed from: org.telegram.messenger.MediaController$9 */
    /* loaded from: classes4.dex */
    public class AnonymousClass9 implements VideoPlayer.VideoPlayerDelegate {
        final /* synthetic */ boolean val$destroyAtEnd;
        final /* synthetic */ MessageObject val$messageObject;
        final /* synthetic */ int[] val$playCount;
        final /* synthetic */ int val$tag;

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public /* synthetic */ void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public /* synthetic */ void onSeekFinished(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public /* synthetic */ void onSeekStarted(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
        }

        AnonymousClass9(int i, MessageObject messageObject, int[] iArr, boolean z) {
            MediaController.this = this$0;
            this.val$tag = i;
            this.val$messageObject = messageObject;
            this.val$playCount = iArr;
            this.val$destroyAtEnd = z;
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onStateChanged(boolean playWhenReady, int playbackState) {
            if (this.val$tag == MediaController.this.playerNum) {
                MediaController.this.updateVideoState(this.val$messageObject, this.val$playCount, this.val$destroyAtEnd, playWhenReady, playbackState);
            }
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onError(VideoPlayer player, Exception e) {
            FileLog.e(e);
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            MediaController.this.currentAspectRatioFrameLayoutRotation = unappliedRotationDegrees;
            if (unappliedRotationDegrees == 90 || unappliedRotationDegrees == 270) {
                width = height;
                height = width;
            }
            MediaController.this.currentAspectRatioFrameLayoutRatio = height == 0 ? 1.0f : (width * pixelWidthHeightRatio) / height;
            if (MediaController.this.currentAspectRatioFrameLayout != null) {
                MediaController.this.currentAspectRatioFrameLayout.setAspectRatio(MediaController.this.currentAspectRatioFrameLayoutRatio, MediaController.this.currentAspectRatioFrameLayoutRotation);
            }
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onRenderedFirstFrame() {
            if (MediaController.this.currentAspectRatioFrameLayout != null && !MediaController.this.currentAspectRatioFrameLayout.isDrawingReady()) {
                MediaController.this.isDrawingWasReady = true;
                MediaController.this.currentAspectRatioFrameLayout.setDrawingReady(true);
                MediaController.this.currentTextureViewContainer.setTag(1);
            }
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
            if (MediaController.this.videoPlayer == null) {
                return false;
            }
            if (MediaController.this.pipSwitchingState == 2) {
                if (MediaController.this.currentAspectRatioFrameLayout != null) {
                    if (MediaController.this.isDrawingWasReady) {
                        MediaController.this.currentAspectRatioFrameLayout.setDrawingReady(true);
                    }
                    if (MediaController.this.currentAspectRatioFrameLayout.getParent() == null) {
                        MediaController.this.currentTextureViewContainer.addView(MediaController.this.currentAspectRatioFrameLayout);
                    }
                    if (MediaController.this.currentTextureView.getSurfaceTexture() != surfaceTexture) {
                        MediaController.this.currentTextureView.setSurfaceTexture(surfaceTexture);
                    }
                    MediaController.this.videoPlayer.setTextureView(MediaController.this.currentTextureView);
                }
                MediaController.this.pipSwitchingState = 0;
                return true;
            } else if (MediaController.this.pipSwitchingState == 1) {
                if (MediaController.this.baseActivity != null) {
                    if (MediaController.this.pipRoundVideoView == null) {
                        try {
                            MediaController.this.pipRoundVideoView = new PipRoundVideoView();
                            MediaController.this.pipRoundVideoView.show(MediaController.this.baseActivity, new Runnable() { // from class: org.telegram.messenger.MediaController$9$$ExternalSyntheticLambda0
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MediaController.AnonymousClass9.this.m402x819a588();
                                }
                            });
                        } catch (Exception e) {
                            MediaController.this.pipRoundVideoView = null;
                        }
                    }
                    if (MediaController.this.pipRoundVideoView != null) {
                        if (MediaController.this.pipRoundVideoView.getTextureView().getSurfaceTexture() != surfaceTexture) {
                            MediaController.this.pipRoundVideoView.getTextureView().setSurfaceTexture(surfaceTexture);
                        }
                        MediaController.this.videoPlayer.setTextureView(MediaController.this.pipRoundVideoView.getTextureView());
                    }
                }
                MediaController.this.pipSwitchingState = 0;
                return true;
            } else if (!PhotoViewer.hasInstance() || !PhotoViewer.getInstance().isInjectingVideoPlayer()) {
                return false;
            } else {
                PhotoViewer.getInstance().injectVideoPlayerSurface(surfaceTexture);
                return true;
            }
        }

        /* renamed from: lambda$onSurfaceDestroyed$0$org-telegram-messenger-MediaController$9 */
        public /* synthetic */ void m402x819a588() {
            MediaController.this.cleanupPlayer(true, true);
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }
    }

    /* renamed from: lambda$playMessage$19$org-telegram-messenger-MediaController */
    public /* synthetic */ void m379lambda$playMessage$19$orgtelegrammessengerMediaController() {
        cleanupPlayer(true, true);
    }

    public AudioInfo getAudioInfo() {
        return this.audioInfo;
    }

    public void setPlaybackOrderType(int type) {
        boolean oldShuffle = SharedConfig.shuffleMusic;
        SharedConfig.setPlaybackOrderType(type);
        if (oldShuffle != SharedConfig.shuffleMusic) {
            if (SharedConfig.shuffleMusic) {
                buildShuffledPlayList();
                return;
            }
            MessageObject messageObject = this.playingMessageObject;
            if (messageObject != null) {
                int indexOf = this.playlist.indexOf(messageObject);
                this.currentPlaylistNum = indexOf;
                if (indexOf == -1) {
                    clearPlaylist();
                    cleanupPlayer(true, true);
                }
            }
        }
    }

    public boolean isStreamingCurrentAudio() {
        return this.isStreamingCurrentAudio;
    }

    public boolean isCurrentPlayer(VideoPlayer player) {
        return this.videoPlayer == player || this.audioPlayer == player;
    }

    /* renamed from: pauseMessage */
    public boolean m383lambda$startAudioAgain$7$orgtelegrammessengerMediaController(MessageObject messageObject) {
        if ((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null || !isSamePlayingMessage(messageObject)) {
            return false;
        }
        stopProgressTimer();
        try {
            if (this.audioPlayer != null) {
                if (!this.playingMessageObject.isVoice() && this.playingMessageObject.getDuration() * (1.0f - this.playingMessageObject.audioProgress) > 1000.0f) {
                    ValueAnimator valueAnimator = this.audioVolumeAnimator;
                    if (valueAnimator != null) {
                        valueAnimator.removeAllUpdateListeners();
                        this.audioVolumeAnimator.cancel();
                    }
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
                    this.audioVolumeAnimator = ofFloat;
                    ofFloat.addUpdateListener(this.audioVolumeUpdateListener);
                    this.audioVolumeAnimator.setDuration(300L);
                    this.audioVolumeAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.messenger.MediaController.12
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            if (MediaController.this.audioPlayer != null) {
                                MediaController.this.audioPlayer.pause();
                            }
                        }
                    });
                    this.audioVolumeAnimator.start();
                } else {
                    this.audioPlayer.pause();
                }
            } else {
                VideoPlayer videoPlayer = this.videoPlayer;
                if (videoPlayer != null) {
                    videoPlayer.pause();
                }
            }
            this.isPaused = true;
            NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
            return true;
        } catch (Exception e) {
            FileLog.e(e);
            this.isPaused = false;
            return false;
        }
    }

    private boolean resumeAudio(MessageObject messageObject) {
        if ((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null || !isSamePlayingMessage(messageObject)) {
            return false;
        }
        try {
            startProgressTimer(this.playingMessageObject);
            ValueAnimator valueAnimator = this.audioVolumeAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.audioVolumeAnimator.cancel();
            }
            if (!messageObject.isVoice()) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(this.audioVolume, 1.0f);
                this.audioVolumeAnimator = ofFloat;
                ofFloat.addUpdateListener(this.audioVolumeUpdateListener);
                this.audioVolumeAnimator.setDuration(300L);
                this.audioVolumeAnimator.start();
            } else {
                this.audioVolume = 1.0f;
                setPlayerVolume();
            }
            VideoPlayer videoPlayer = this.audioPlayer;
            if (videoPlayer != null) {
                videoPlayer.play();
            } else {
                VideoPlayer videoPlayer2 = this.videoPlayer;
                if (videoPlayer2 != null) {
                    videoPlayer2.play();
                }
            }
            checkAudioFocus(messageObject);
            this.isPaused = false;
            NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
            return true;
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    public boolean isVideoDrawingReady() {
        AspectRatioFrameLayout aspectRatioFrameLayout = this.currentAspectRatioFrameLayout;
        return aspectRatioFrameLayout != null && aspectRatioFrameLayout.isDrawingReady();
    }

    public ArrayList<MessageObject> getPlaylist() {
        return this.playlist;
    }

    public boolean isPlayingMessage(MessageObject messageObject) {
        MessageObject messageObject2;
        if ((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || (messageObject2 = this.playingMessageObject) == null) {
            return false;
        }
        if (messageObject2.eventId != 0 && this.playingMessageObject.eventId == messageObject.eventId) {
            return !this.downloadingCurrentMessage;
        }
        if (!isSamePlayingMessage(messageObject)) {
            return false;
        }
        return !this.downloadingCurrentMessage;
    }

    public boolean isPlayingMessageAndReadyToDraw(MessageObject messageObject) {
        return this.isDrawingWasReady && isPlayingMessage(messageObject);
    }

    public boolean isMessagePaused() {
        return this.isPaused || this.downloadingCurrentMessage;
    }

    public boolean isDownloadingCurrentMessage() {
        return this.downloadingCurrentMessage;
    }

    public void setReplyingMessage(MessageObject replyToMsg, MessageObject replyToTopMsg) {
        this.recordReplyingMsg = replyToMsg;
        this.recordReplyingTopMsg = replyToTopMsg;
    }

    public void requestAudioFocus(boolean request) {
        if (request) {
            if (!this.hasRecordAudioFocus && SharedConfig.pauseMusicOnRecord) {
                int result = NotificationsController.audioManager.requestAudioFocus(this.audioRecordFocusChangedListener, 3, 2);
                if (result == 1) {
                    this.hasRecordAudioFocus = true;
                }
            }
        } else if (this.hasRecordAudioFocus) {
            NotificationsController.audioManager.abandonAudioFocus(this.audioRecordFocusChangedListener);
            this.hasRecordAudioFocus = false;
        }
    }

    public void startRecording(final int currentAccount, final long dialogId, final MessageObject replyToMsg, final MessageObject replyToTopMsg, final int guid) {
        boolean paused;
        MessageObject messageObject = this.playingMessageObject;
        if (messageObject != null && isPlayingMessage(messageObject) && !isMessagePaused()) {
            paused = true;
        } else {
            paused = false;
        }
        requestAudioFocus(true);
        try {
            this.feedbackView.performHapticFeedback(3, 2);
        } catch (Exception e) {
        }
        DispatchQueue dispatchQueue = this.recordQueue;
        Runnable runnable = new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                MediaController.this.m389lambda$startRecording$26$orgtelegrammessengerMediaController(currentAccount, guid, dialogId, replyToMsg, replyToTopMsg);
            }
        };
        this.recordStartRunnable = runnable;
        dispatchQueue.postRunnable(runnable, paused ? 500L : 50L);
    }

    /* renamed from: lambda$startRecording$26$org-telegram-messenger-MediaController */
    public /* synthetic */ void m389lambda$startRecording$26$orgtelegrammessengerMediaController(final int currentAccount, final int guid, long dialogId, MessageObject replyToMsg, MessageObject replyToTopMsg) {
        if (this.audioRecorder != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    MediaController.this.m385lambda$startRecording$22$orgtelegrammessengerMediaController(currentAccount, guid);
                }
            });
            return;
        }
        this.sendAfterDone = 0;
        TLRPC.TL_document tL_document = new TLRPC.TL_document();
        this.recordingAudio = tL_document;
        this.recordingGuid = guid;
        tL_document.file_reference = new byte[0];
        this.recordingAudio.dc_id = Integer.MIN_VALUE;
        this.recordingAudio.id = SharedConfig.getLastLocalId();
        this.recordingAudio.user_id = UserConfig.getInstance(currentAccount).getClientUserId();
        this.recordingAudio.mime_type = "audio/ogg";
        this.recordingAudio.file_reference = new byte[0];
        SharedConfig.saveConfig();
        File file = new File(FileLoader.getDirectory(4), FileLoader.getAttachFileName(this.recordingAudio));
        this.recordingAudioFile = file;
        try {
            if (startRecord(file.getAbsolutePath(), this.sampleRate) == 0) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda9
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaController.this.m386lambda$startRecording$23$orgtelegrammessengerMediaController(currentAccount, guid);
                    }
                });
                return;
            }
            this.audioRecorder = new AudioRecord(0, this.sampleRate, 16, 2, this.recordBufferSize);
            this.recordStartTime = System.currentTimeMillis();
            this.recordTimeCount = 0L;
            this.samplesCount = 0L;
            this.recordDialogId = dialogId;
            this.recordingCurrentAccount = currentAccount;
            this.recordReplyingMsg = replyToMsg;
            this.recordReplyingTopMsg = replyToTopMsg;
            this.fileBuffer.rewind();
            this.audioRecorder.startRecording();
            this.recordQueue.postRunnable(this.recordRunnable);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    MediaController.this.m388lambda$startRecording$25$orgtelegrammessengerMediaController(currentAccount, guid);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
            this.recordingAudio = null;
            stopRecord();
            this.recordingAudioFile.delete();
            this.recordingAudioFile = null;
            try {
                this.audioRecorder.release();
                this.audioRecorder = null;
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    MediaController.this.m387lambda$startRecording$24$orgtelegrammessengerMediaController(currentAccount, guid);
                }
            });
        }
    }

    /* renamed from: lambda$startRecording$22$org-telegram-messenger-MediaController */
    public /* synthetic */ void m385lambda$startRecording$22$orgtelegrammessengerMediaController(int currentAccount, int guid) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.recordStartError, Integer.valueOf(guid));
    }

    /* renamed from: lambda$startRecording$23$org-telegram-messenger-MediaController */
    public /* synthetic */ void m386lambda$startRecording$23$orgtelegrammessengerMediaController(int currentAccount, int guid) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.recordStartError, Integer.valueOf(guid));
    }

    /* renamed from: lambda$startRecording$24$org-telegram-messenger-MediaController */
    public /* synthetic */ void m387lambda$startRecording$24$orgtelegrammessengerMediaController(int currentAccount, int guid) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.recordStartError, Integer.valueOf(guid));
    }

    /* renamed from: lambda$startRecording$25$org-telegram-messenger-MediaController */
    public /* synthetic */ void m388lambda$startRecording$25$orgtelegrammessengerMediaController(int currentAccount, int guid) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.recordStarted, Integer.valueOf(guid), true);
    }

    public void generateWaveform(final MessageObject messageObject) {
        final String id = messageObject.getId() + "_" + messageObject.getDialogId();
        final String path = FileLoader.getInstance(messageObject.currentAccount).getPathToMessage(messageObject.messageOwner).getAbsolutePath();
        if (this.generatingWaveform.containsKey(id)) {
            return;
        }
        this.generatingWaveform.put(id, messageObject);
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                MediaController.this.m369x7812ae4c(path, id, messageObject);
            }
        });
    }

    /* renamed from: lambda$generateWaveform$28$org-telegram-messenger-MediaController */
    public /* synthetic */ void m369x7812ae4c(String path, final String id, final MessageObject messageObject) {
        final byte[] waveform = getWaveform(path);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                MediaController.this.m368x20f4bd6d(id, waveform, messageObject);
            }
        });
    }

    /* renamed from: lambda$generateWaveform$27$org-telegram-messenger-MediaController */
    public /* synthetic */ void m368x20f4bd6d(String id, byte[] waveform, MessageObject messageObject) {
        MessageObject messageObject1 = this.generatingWaveform.remove(id);
        if (messageObject1 != null && waveform != null && messageObject1.getDocument() != null) {
            int a = 0;
            while (true) {
                if (a >= messageObject1.getDocument().attributes.size()) {
                    break;
                }
                TLRPC.DocumentAttribute attribute = messageObject1.getDocument().attributes.get(a);
                if (!(attribute instanceof TLRPC.TL_documentAttributeAudio)) {
                    a++;
                } else {
                    attribute.waveform = waveform;
                    attribute.flags |= 4;
                    break;
                }
            }
            TLRPC.TL_messages_messages messagesRes = new TLRPC.TL_messages_messages();
            messagesRes.messages.add(messageObject1.messageOwner);
            MessagesStorage.getInstance(messageObject1.currentAccount).putMessages((TLRPC.messages_Messages) messagesRes, messageObject1.getDialogId(), -1, 0, false, messageObject.scheduled);
            ArrayList<MessageObject> arrayList = new ArrayList<>();
            arrayList.add(messageObject1);
            NotificationCenter.getInstance(messageObject1.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject1.getDialogId()), arrayList);
        }
    }

    public void stopRecordingInternal(final int send, final boolean notify, final int scheduleDate) {
        if (send != 0) {
            final TLRPC.TL_document audioToSend = this.recordingAudio;
            final File recordingAudioFileToSend = this.recordingAudioFile;
            this.fileEncodingQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda25
                @Override // java.lang.Runnable
                public final void run() {
                    MediaController.this.m394xb01e7e15(audioToSend, recordingAudioFileToSend, send, notify, scheduleDate);
                }
            });
        } else {
            File file = this.recordingAudioFile;
            if (file != null) {
                file.delete();
            }
            requestAudioFocus(false);
        }
        try {
            AudioRecord audioRecord = this.audioRecorder;
            if (audioRecord != null) {
                audioRecord.release();
                this.audioRecorder = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.recordingAudio = null;
        this.recordingAudioFile = null;
    }

    /* renamed from: lambda$stopRecordingInternal$30$org-telegram-messenger-MediaController */
    public /* synthetic */ void m394xb01e7e15(final TLRPC.TL_document audioToSend, final File recordingAudioFileToSend, final int send, final boolean notify, final int scheduleDate) {
        stopRecord();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                MediaController.this.m393x338bcaeb(audioToSend, recordingAudioFileToSend, send, notify, scheduleDate);
            }
        });
    }

    /* renamed from: lambda$stopRecordingInternal$29$org-telegram-messenger-MediaController */
    public /* synthetic */ void m393x338bcaeb(TLRPC.TL_document audioToSend, File recordingAudioFileToSend, int send, boolean notify, int scheduleDate) {
        boolean z;
        char c;
        long duration;
        audioToSend.date = ConnectionsManager.getInstance(this.recordingCurrentAccount).getCurrentTime();
        audioToSend.size = (int) recordingAudioFileToSend.length();
        TLRPC.TL_documentAttributeAudio attributeAudio = new TLRPC.TL_documentAttributeAudio();
        attributeAudio.voice = true;
        short[] sArr = this.recordSamples;
        attributeAudio.waveform = getWaveform2(sArr, sArr.length);
        if (attributeAudio.waveform != null) {
            attributeAudio.flags |= 4;
        }
        long duration2 = this.recordTimeCount;
        attributeAudio.duration = (int) (this.recordTimeCount / 1000);
        audioToSend.attributes.add(attributeAudio);
        if (duration2 <= 700) {
            z = false;
            NotificationCenter.getInstance(this.recordingCurrentAccount).postNotificationName(NotificationCenter.audioRecordTooShort, Integer.valueOf(this.recordingGuid), false, Integer.valueOf((int) duration2));
            recordingAudioFileToSend.delete();
        } else {
            if (send == 1) {
                duration = duration2;
                c = 1;
                SendMessagesHelper.getInstance(this.recordingCurrentAccount).sendMessage(audioToSend, null, recordingAudioFileToSend.getAbsolutePath(), this.recordDialogId, this.recordReplyingMsg, this.recordReplyingTopMsg, null, null, null, null, notify, scheduleDate, 0, null, null);
            } else {
                duration = duration2;
                c = 1;
            }
            NotificationCenter notificationCenter = NotificationCenter.getInstance(this.recordingCurrentAccount);
            int i = NotificationCenter.audioDidSent;
            Object[] objArr = new Object[3];
            z = false;
            objArr[0] = Integer.valueOf(this.recordingGuid);
            String str = null;
            objArr[c] = send == 2 ? audioToSend : null;
            if (send == 2) {
                str = recordingAudioFileToSend.getAbsolutePath();
            }
            objArr[2] = str;
            notificationCenter.postNotificationName(i, objArr);
        }
        requestAudioFocus(z);
    }

    public void stopRecording(final int send, final boolean notify, final int scheduleDate) {
        Runnable runnable = this.recordStartRunnable;
        if (runnable != null) {
            this.recordQueue.cancelRunnable(runnable);
            this.recordStartRunnable = null;
        }
        this.recordQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                MediaController.this.m392lambda$stopRecording$32$orgtelegrammessengerMediaController(send, notify, scheduleDate);
            }
        });
    }

    /* renamed from: lambda$stopRecording$32$org-telegram-messenger-MediaController */
    public /* synthetic */ void m392lambda$stopRecording$32$orgtelegrammessengerMediaController(final int send, boolean notify, int scheduleDate) {
        if (this.sendAfterDone == 3) {
            this.sendAfterDone = 0;
            stopRecordingInternal(send, notify, scheduleDate);
            return;
        }
        AudioRecord audioRecord = this.audioRecorder;
        if (audioRecord == null) {
            return;
        }
        try {
            this.sendAfterDone = send;
            this.sendAfterDoneNotify = notify;
            this.sendAfterDoneScheduleDate = scheduleDate;
            audioRecord.stop();
        } catch (Exception e) {
            FileLog.e(e);
            File file = this.recordingAudioFile;
            if (file != null) {
                file.delete();
            }
        }
        if (send == 0) {
            stopRecordingInternal(0, false, 0);
        }
        try {
            this.feedbackView.performHapticFeedback(3, 2);
        } catch (Exception e2) {
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                MediaController.this.m391lambda$stopRecording$31$orgtelegrammessengerMediaController(send);
            }
        });
    }

    /* renamed from: lambda$stopRecording$31$org-telegram-messenger-MediaController */
    public /* synthetic */ void m391lambda$stopRecording$31$orgtelegrammessengerMediaController(int send) {
        NotificationCenter notificationCenter = NotificationCenter.getInstance(this.recordingCurrentAccount);
        int i = NotificationCenter.recordStopped;
        Object[] objArr = new Object[2];
        int i2 = 0;
        objArr[0] = Integer.valueOf(this.recordingGuid);
        if (send == 2) {
            i2 = 1;
        }
        objArr[1] = Integer.valueOf(i2);
        notificationCenter.postNotificationName(i, objArr);
    }

    /* loaded from: classes4.dex */
    public static class MediaLoader implements NotificationCenter.NotificationCenterDelegate {
        private boolean cancelled;
        private int copiedFiles;
        private AccountInstance currentAccount;
        private boolean finished;
        private float finishedProgress;
        private boolean isMusic;
        private HashMap<String, MessageObject> loadingMessageObjects = new HashMap<>();
        private ArrayList<MessageObject> messageObjects;
        private MessagesStorage.IntCallback onFinishRunnable;
        private AlertDialog progressDialog;
        private CountDownLatch waitingForFile;

        public MediaLoader(Context context, AccountInstance accountInstance, ArrayList<MessageObject> messages, MessagesStorage.IntCallback onFinish) {
            this.currentAccount = accountInstance;
            this.messageObjects = messages;
            this.onFinishRunnable = onFinish;
            this.isMusic = messages.get(0).isMusic();
            this.currentAccount.getNotificationCenter().addObserver(this, NotificationCenter.fileLoaded);
            this.currentAccount.getNotificationCenter().addObserver(this, NotificationCenter.fileLoadProgressChanged);
            this.currentAccount.getNotificationCenter().addObserver(this, NotificationCenter.fileLoadFailed);
            AlertDialog alertDialog = new AlertDialog(context, 2);
            this.progressDialog = alertDialog;
            alertDialog.setMessage(LocaleController.getString("Loading", org.telegram.messenger.beta.R.string.Loading));
            this.progressDialog.setCanceledOnTouchOutside(false);
            this.progressDialog.setCancelable(true);
            this.progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.messenger.MediaController$MediaLoader$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnCancelListener
                public final void onCancel(DialogInterface dialogInterface) {
                    MediaController.MediaLoader.this.m411lambda$new$0$orgtelegrammessengerMediaController$MediaLoader(dialogInterface);
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-messenger-MediaController$MediaLoader */
        public /* synthetic */ void m411lambda$new$0$orgtelegrammessengerMediaController$MediaLoader(DialogInterface d) {
            this.cancelled = true;
        }

        public void start() {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$MediaLoader$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    MediaController.MediaLoader.this.m412x745944a8();
                }
            }, 250L);
            new Thread(new Runnable() { // from class: org.telegram.messenger.MediaController$MediaLoader$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    MediaController.MediaLoader.this.m413xa231df07();
                }
            }).start();
        }

        /* renamed from: lambda$start$1$org-telegram-messenger-MediaController$MediaLoader */
        public /* synthetic */ void m412x745944a8() {
            if (!this.finished) {
                this.progressDialog.show();
            }
        }

        /* renamed from: lambda$start$2$org-telegram-messenger-MediaController$MediaLoader */
        public /* synthetic */ void m413xa231df07() {
            File dir;
            try {
                if (Build.VERSION.SDK_INT >= 29) {
                    int N = this.messageObjects.size();
                    for (int b = 0; b < N; b++) {
                        MessageObject message = this.messageObjects.get(b);
                        String path = message.messageOwner.attachPath;
                        String name = message.getDocumentName();
                        if (path != null && path.length() > 0) {
                            File temp = new File(path);
                            if (!temp.exists()) {
                                path = null;
                            }
                        }
                        if (path == null || path.length() == 0) {
                            path = FileLoader.getInstance(this.currentAccount.getCurrentAccount()).getPathToMessage(message.messageOwner).toString();
                        }
                        File sourceFile = new File(path);
                        if (!sourceFile.exists()) {
                            this.waitingForFile = new CountDownLatch(1);
                            addMessageToLoad(message);
                            this.waitingForFile.await();
                        }
                        if (this.cancelled) {
                            break;
                        }
                        if (sourceFile.exists()) {
                            MediaController.saveFileInternal(this.isMusic ? 3 : 2, sourceFile, name);
                            this.copiedFiles++;
                        }
                    }
                } else {
                    if (this.isMusic) {
                        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                    } else {
                        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    }
                    dir.mkdir();
                    int N2 = this.messageObjects.size();
                    for (int b2 = 0; b2 < N2; b2++) {
                        MessageObject message2 = this.messageObjects.get(b2);
                        String name2 = message2.getDocumentName();
                        File destFile = new File(dir, name2);
                        if (destFile.exists()) {
                            int idx = name2.lastIndexOf(46);
                            for (int a = 0; a < 10; a++) {
                                String newName = idx != -1 ? name2.substring(0, idx) + "(" + (a + 1) + ")" + name2.substring(idx) : name2 + "(" + (a + 1) + ")";
                                destFile = new File(dir, newName);
                                if (!destFile.exists()) {
                                    break;
                                }
                            }
                        }
                        if (!destFile.exists()) {
                            destFile.createNewFile();
                        }
                        String path2 = message2.messageOwner.attachPath;
                        if (path2 != null && path2.length() > 0) {
                            File temp2 = new File(path2);
                            if (!temp2.exists()) {
                                path2 = null;
                            }
                        }
                        if (path2 == null || path2.length() == 0) {
                            path2 = FileLoader.getInstance(this.currentAccount.getCurrentAccount()).getPathToMessage(message2.messageOwner).toString();
                        }
                        File sourceFile2 = new File(path2);
                        if (!sourceFile2.exists()) {
                            this.waitingForFile = new CountDownLatch(1);
                            addMessageToLoad(message2);
                            this.waitingForFile.await();
                        }
                        if (sourceFile2.exists()) {
                            copyFile(sourceFile2, destFile, message2.getMimeType());
                            this.copiedFiles++;
                        }
                    }
                }
                checkIfFinished();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        private void checkIfFinished() {
            if (!this.loadingMessageObjects.isEmpty()) {
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$MediaLoader$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    MediaController.MediaLoader.this.m406x6aaa6330();
                }
            });
        }

        /* renamed from: lambda$checkIfFinished$4$org-telegram-messenger-MediaController$MediaLoader */
        public /* synthetic */ void m406x6aaa6330() {
            try {
                if (this.progressDialog.isShowing()) {
                    this.progressDialog.dismiss();
                } else {
                    this.finished = true;
                }
                if (this.onFinishRunnable != null) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$MediaLoader$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            MediaController.MediaLoader.this.m405x3cd1c8d1();
                        }
                    });
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.currentAccount.getNotificationCenter().removeObserver(this, NotificationCenter.fileLoaded);
            this.currentAccount.getNotificationCenter().removeObserver(this, NotificationCenter.fileLoadProgressChanged);
            this.currentAccount.getNotificationCenter().removeObserver(this, NotificationCenter.fileLoadFailed);
        }

        /* renamed from: lambda$checkIfFinished$3$org-telegram-messenger-MediaController$MediaLoader */
        public /* synthetic */ void m405x3cd1c8d1() {
            this.onFinishRunnable.run(this.copiedFiles);
        }

        private void addMessageToLoad(final MessageObject messageObject) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$MediaLoader$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    MediaController.MediaLoader.this.m404x4d3f37c5(messageObject);
                }
            });
        }

        /* renamed from: lambda$addMessageToLoad$5$org-telegram-messenger-MediaController$MediaLoader */
        public /* synthetic */ void m404x4d3f37c5(MessageObject messageObject) {
            TLRPC.Document document = messageObject.getDocument();
            if (document == null) {
                return;
            }
            String fileName = FileLoader.getAttachFileName(document);
            this.loadingMessageObjects.put(fileName, messageObject);
            this.currentAccount.getFileLoader().loadFile(document, messageObject, 1, 0);
        }

        private boolean copyFile(File sourceFile, File destFile, String mime) {
            Exception e;
            FileInputStream inputStream;
            Throwable th;
            FileChannel source;
            Throwable th2;
            if (AndroidUtilities.isInternalUri(Uri.fromFile(sourceFile))) {
                return false;
            }
            try {
                inputStream = new FileInputStream(sourceFile);
            } catch (Exception e2) {
                e = e2;
            }
            try {
                try {
                    source = inputStream.getChannel();
                } catch (Throwable th3) {
                    th = th3;
                }
            } catch (Exception e3) {
                e = e3;
                FileLog.e(e);
                destFile.delete();
                return false;
            }
            try {
                try {
                    try {
                        FileChannel destination = new FileOutputStream(destFile).getChannel();
                        long size = source.size();
                        Method getInt = FileDescriptor.class.getDeclaredMethod("getInt$", new Class[0]);
                        int fdint = ((Integer) getInt.invoke(inputStream.getFD(), new Object[0])).intValue();
                        if (AndroidUtilities.isInternalUri(fdint)) {
                            if (this.progressDialog != null) {
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$MediaLoader$$ExternalSyntheticLambda3
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MediaController.MediaLoader.this.m407x99201a9a();
                                    }
                                });
                            }
                            if (destination != null) {
                                destination.close();
                            }
                            if (source != null) {
                                source.close();
                            }
                            inputStream.close();
                            return false;
                        }
                        long lastProgress = 0;
                        long a = 0;
                        while (a < size && !this.cancelled) {
                            long a2 = a;
                            destination.transferFrom(source, a, Math.min(4096L, size - a));
                            if (a2 + 4096 >= size || lastProgress <= SystemClock.elapsedRealtime() - 500) {
                                long lastProgress2 = SystemClock.elapsedRealtime();
                                final int progress = (int) (this.finishedProgress + (((100.0f / this.messageObjects.size()) * ((float) a2)) / ((float) size)));
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$MediaLoader$$ExternalSyntheticLambda6
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MediaController.MediaLoader.this.m408xc6f8b4f9(progress);
                                    }
                                });
                                lastProgress = lastProgress2;
                            }
                            a = a2 + 4096;
                        }
                        if (this.cancelled) {
                            if (destination != null) {
                                destination.close();
                            }
                            if (source != null) {
                                source.close();
                            }
                            inputStream.close();
                            destFile.delete();
                            return false;
                        }
                        if (this.isMusic) {
                            AndroidUtilities.addMediaToGallery(destFile);
                        } else {
                            DownloadManager downloadManager = (DownloadManager) ApplicationLoader.applicationContext.getSystemService("download");
                            String mimeType = mime;
                            if (TextUtils.isEmpty(mimeType)) {
                                MimeTypeMap myMime = MimeTypeMap.getSingleton();
                                String name = destFile.getName();
                                int idx = name.lastIndexOf(46);
                                if (idx != -1) {
                                    String ext = name.substring(idx + 1);
                                    mimeType = myMime.getMimeTypeFromExtension(ext.toLowerCase());
                                    if (TextUtils.isEmpty(mimeType)) {
                                        mimeType = ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN;
                                    }
                                } else {
                                    mimeType = ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN;
                                }
                            }
                            downloadManager.addCompletedDownload(destFile.getName(), destFile.getName(), false, mimeType, destFile.getAbsolutePath(), destFile.length(), true);
                        }
                        float size2 = this.finishedProgress + (100.0f / this.messageObjects.size());
                        this.finishedProgress = size2;
                        final int progress2 = (int) size2;
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$MediaLoader$$ExternalSyntheticLambda7
                            @Override // java.lang.Runnable
                            public final void run() {
                                MediaController.MediaLoader.this.m409xf4d14f58(progress2);
                            }
                        });
                        if (destination != null) {
                            destination.close();
                        }
                        if (source != null) {
                            source.close();
                        }
                        inputStream.close();
                        return true;
                    } catch (Throwable th4) {
                        th2 = th4;
                        Throwable th5 = th2;
                        if (source != null) {
                            try {
                                source.close();
                            } catch (Throwable th6) {
                            }
                        }
                        throw th5;
                    }
                } catch (Throwable th7) {
                    th = th7;
                    Throwable th8 = th;
                    try {
                        inputStream.close();
                    } catch (Throwable th9) {
                    }
                    throw th8;
                }
            } catch (Throwable th10) {
                th2 = th10;
            }
        }

        /* renamed from: lambda$copyFile$6$org-telegram-messenger-MediaController$MediaLoader */
        public /* synthetic */ void m407x99201a9a() {
            try {
                this.progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        /* renamed from: lambda$copyFile$7$org-telegram-messenger-MediaController$MediaLoader */
        public /* synthetic */ void m408xc6f8b4f9(int progress) {
            try {
                this.progressDialog.setProgress(progress);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        /* renamed from: lambda$copyFile$8$org-telegram-messenger-MediaController$MediaLoader */
        public /* synthetic */ void m409xf4d14f58(int progress) {
            try {
                this.progressDialog.setProgress(progress);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
        public void didReceivedNotification(int id, int account, Object... args) {
            if (id == NotificationCenter.fileLoaded || id == NotificationCenter.fileLoadFailed) {
                String fileName = (String) args[0];
                if (this.loadingMessageObjects.remove(fileName) != null) {
                    this.waitingForFile.countDown();
                }
            } else if (id == NotificationCenter.fileLoadProgressChanged) {
                String fileName2 = (String) args[0];
                if (this.loadingMessageObjects.containsKey(fileName2)) {
                    Long loadedSize = (Long) args[1];
                    Long totalSize = (Long) args[2];
                    float loadProgress = ((float) loadedSize.longValue()) / ((float) totalSize.longValue());
                    final int progress = (int) (this.finishedProgress + ((loadProgress / this.messageObjects.size()) * 100.0f));
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$MediaLoader$$ExternalSyntheticLambda8
                        @Override // java.lang.Runnable
                        public final void run() {
                            MediaController.MediaLoader.this.m410x85fd3dd7(progress);
                        }
                    });
                }
            }
        }

        /* renamed from: lambda$didReceivedNotification$9$org-telegram-messenger-MediaController$MediaLoader */
        public /* synthetic */ void m410x85fd3dd7(int progress) {
            try {
                this.progressDialog.setProgress(progress);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static void saveFilesFromMessages(Context context, AccountInstance accountInstance, ArrayList<MessageObject> messageObjects, MessagesStorage.IntCallback onSaved) {
        if (messageObjects == null || messageObjects.isEmpty()) {
            return;
        }
        new MediaLoader(context, accountInstance, messageObjects, onSaved).start();
    }

    public static void saveFile(String fullPath, Context context, int type, String name, String mime) {
        saveFile(fullPath, context, type, name, mime, null);
    }

    public static void saveFile(String fullPath, Context context, final int type, final String name, final String mime, final Runnable onSaved) {
        File file;
        AlertDialog progressDialog;
        if (fullPath == null || context == null) {
            return;
        }
        if (!TextUtils.isEmpty(fullPath)) {
            File file2 = new File(fullPath);
            file = (!file2.exists() || AndroidUtilities.isInternalUri(Uri.fromFile(file2))) ? null : file2;
        } else {
            file = null;
        }
        if (file == null) {
            return;
        }
        final File sourceFile = file;
        final boolean[] cancelled = {false};
        if (sourceFile.exists()) {
            final boolean[] finished = new boolean[1];
            if (context != null && type != 0) {
                try {
                    final AlertDialog dialog = new AlertDialog(context, 2);
                    dialog.setMessage(LocaleController.getString("Loading", org.telegram.messenger.beta.R.string.Loading));
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(true);
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda21
                        @Override // android.content.DialogInterface.OnCancelListener
                        public final void onCancel(DialogInterface dialogInterface) {
                            MediaController.lambda$saveFile$33(cancelled, dialogInterface);
                        }
                    });
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda33
                        @Override // java.lang.Runnable
                        public final void run() {
                            MediaController.lambda$saveFile$34(finished, dialog);
                        }
                    }, 250L);
                    progressDialog = dialog;
                } catch (Exception e) {
                    FileLog.e(e);
                }
                final AlertDialog finalProgress = progressDialog;
                new Thread(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda39
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaController.lambda$saveFile$38(type, sourceFile, name, finalProgress, cancelled, mime, onSaved, finished);
                    }
                }).start();
            }
            progressDialog = null;
            final AlertDialog finalProgress2 = progressDialog;
            new Thread(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda39
                @Override // java.lang.Runnable
                public final void run() {
                    MediaController.lambda$saveFile$38(type, sourceFile, name, finalProgress2, cancelled, mime, onSaved, finished);
                }
            }).start();
        }
    }

    public static /* synthetic */ void lambda$saveFile$33(boolean[] cancelled, DialogInterface d) {
        cancelled[0] = true;
    }

    public static /* synthetic */ void lambda$saveFile$34(boolean[] finished, AlertDialog dialog) {
        if (!finished[0]) {
            dialog.show();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:106:0x01f3 A[Catch: Exception -> 0x0232, TryCatch #7 {Exception -> 0x0232, blocks: (B:3:0x0009, B:5:0x000f, B:8:0x001c, B:11:0x003e, B:13:0x0060, B:14:0x0067, B:15:0x006d, B:17:0x0081, B:23:0x0093, B:24:0x00b6, B:25:0x00cd, B:28:0x00da, B:29:0x00dd, B:31:0x00e3, B:32:0x00e6, B:103:0x01ea, B:104:0x01ee, B:106:0x01f3, B:111:0x01fe, B:112:0x0223, B:115:0x022e), top: B:135:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:107:0x01f8  */
    /* JADX WARN: Removed duplicated region for block: B:109:0x01fb  */
    /* JADX WARN: Removed duplicated region for block: B:119:0x0238  */
    /* JADX WARN: Removed duplicated region for block: B:120:0x0243  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static /* synthetic */ void lambda$saveFile$38(int r23, java.io.File r24, java.lang.String r25, final org.telegram.ui.ActionBar.AlertDialog r26, boolean[] r27, java.lang.String r28, java.lang.Runnable r29, final boolean[] r30) {
        /*
            Method dump skipped, instructions count: 582
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.lambda$saveFile$38(int, java.io.File, java.lang.String, org.telegram.ui.ActionBar.AlertDialog, boolean[], java.lang.String, java.lang.Runnable, boolean[]):void");
    }

    public static /* synthetic */ void lambda$saveFile$35(AlertDialog finalProgress) {
        try {
            finalProgress.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static /* synthetic */ void lambda$saveFile$36(AlertDialog finalProgress, int progress) {
        try {
            finalProgress.setProgress(progress);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static /* synthetic */ void lambda$saveFile$37(AlertDialog finalProgress, boolean[] finished) {
        try {
            if (finalProgress.isShowing()) {
                finalProgress.dismiss();
            } else {
                finished[0] = true;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static boolean saveFileInternal(int type, File sourceFile, String filename) {
        Exception e;
        Uri uriToInsert;
        String filename2;
        String filename3;
        String filename4;
        String filename5;
        int selectedType = type;
        try {
            ContentValues contentValues = new ContentValues();
            String extension = FileLoader.getFileExtension(sourceFile);
            String mimeType = null;
            if (extension != null) {
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
            if ((type == 0 || type == 1) && mimeType != null) {
                if (mimeType.startsWith(TtmlNode.TAG_IMAGE)) {
                    selectedType = 0;
                }
                if (mimeType.startsWith("video")) {
                    selectedType = 1;
                }
            }
            try {
                if (selectedType != 0) {
                    if (selectedType == 1) {
                        if (filename != null) {
                            filename4 = filename;
                        } else {
                            filename4 = AndroidUtilities.generateFileName(1, extension);
                        }
                        File dirDest = new File(Environment.DIRECTORY_MOVIES, "Telegram");
                        contentValues.put("relative_path", dirDest + File.separator);
                        uriToInsert = MediaStore.Video.Media.getContentUri("external_primary");
                        contentValues.put("_display_name", filename4);
                    } else if (selectedType == 2) {
                        if (filename != null) {
                            filename3 = filename;
                        } else {
                            filename3 = sourceFile.getName();
                        }
                        File dirDest2 = new File(Environment.DIRECTORY_DOWNLOADS, "Telegram");
                        contentValues.put("relative_path", dirDest2 + File.separator);
                        uriToInsert = MediaStore.Downloads.getContentUri("external_primary");
                        contentValues.put("_display_name", filename3);
                    } else {
                        if (filename != null) {
                            filename2 = filename;
                        } else {
                            filename2 = sourceFile.getName();
                        }
                        File dirDest3 = new File(Environment.DIRECTORY_MUSIC, "Telegram");
                        contentValues.put("relative_path", dirDest3 + File.separator);
                        uriToInsert = MediaStore.Audio.Media.getContentUri("external_primary");
                        contentValues.put("_display_name", filename2);
                    }
                } else {
                    if (filename != null) {
                        filename5 = filename;
                    } else {
                        filename5 = AndroidUtilities.generateFileName(0, extension);
                    }
                    uriToInsert = MediaStore.Images.Media.getContentUri("external_primary");
                    File dirDest4 = new File(Environment.DIRECTORY_PICTURES, "Telegram");
                    contentValues.put("relative_path", dirDest4 + File.separator);
                    contentValues.put("_display_name", filename5);
                    contentValues.put("mime_type", mimeType);
                }
                contentValues.put("mime_type", mimeType);
                Uri dstUri = ApplicationLoader.applicationContext.getContentResolver().insert(uriToInsert, contentValues);
                if (dstUri != null) {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(sourceFile);
                        OutputStream outputStream = ApplicationLoader.applicationContext.getContentResolver().openOutputStream(dstUri);
                        AndroidUtilities.copyFile(fileInputStream, outputStream);
                        fileInputStream.close();
                    } catch (Exception e2) {
                        e = e2;
                        FileLog.e(e);
                        return false;
                    }
                }
                return true;
            } catch (Exception e3) {
                e = e3;
            }
        } catch (Exception e4) {
            e = e4;
        }
    }

    public static String getStickerExt(Uri uri) {
        InputStream inputStream = null;
        try {
            try {
                inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            } catch (Exception e) {
                inputStream = null;
            }
            if (inputStream == null) {
                try {
                    try {
                        File file = new File(uri.getPath());
                        if (file.exists()) {
                            inputStream = new FileInputStream(file);
                        }
                    } catch (Exception e2) {
                        FileLog.e(e2);
                        if (inputStream == null) {
                            return null;
                        }
                        inputStream.close();
                        return null;
                    }
                } catch (Exception e22) {
                    FileLog.e(e22);
                    return null;
                }
            }
            byte[] header = new byte[12];
            if (inputStream.read(header, 0, 12) == 12) {
                if (header[0] == -119 && header[1] == 80 && header[2] == 78 && header[3] == 71 && header[4] == 13 && header[5] == 10 && header[6] == 26 && header[7] == 10) {
                    return "png";
                }
                if (header[0] == 31 && header[1] == -117) {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Exception e23) {
                            FileLog.e(e23);
                        }
                    }
                    return "tgs";
                }
                String str = new String(header).toLowerCase();
                if (str.startsWith("riff")) {
                    if (str.endsWith("webp")) {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (Exception e24) {
                                FileLog.e(e24);
                            }
                        }
                        return "webp";
                    }
                }
            }
            if (inputStream == null) {
                return null;
            }
            inputStream.close();
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e25) {
                    FileLog.e(e25);
                }
            }
        }
    }

    public static boolean isWebp(Uri uri) {
        InputStream inputStream = null;
        try {
            try {
                try {
                    inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
                    byte[] header = new byte[12];
                    if (inputStream.read(header, 0, 12) == 12) {
                        String str = new String(header).toLowerCase();
                        if (str.startsWith("riff")) {
                            if (str.endsWith("webp")) {
                                return true;
                            }
                        }
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            return false;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e22) {
                    FileLog.e(e22);
                }
            }
        }
    }

    public static boolean isGif(Uri uri) {
        InputStream inputStream = null;
        try {
            try {
                try {
                    inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
                    byte[] header = new byte[3];
                    if (inputStream.read(header, 0, 3) == 3) {
                        String str = new String(header);
                        if (str.equalsIgnoreCase("gif")) {
                            return true;
                        }
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            return false;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e22) {
                    FileLog.e(e22);
                }
            }
        }
    }

    public static String getFileName(Uri uri) {
        if (uri == null) {
            return "";
        }
        String result = null;
        try {
            if (uri.getScheme().equals("content")) {
                try {
                    Cursor cursor = ApplicationLoader.applicationContext.getContentResolver().query(uri, new String[]{"_display_name"}, null, null, null);
                    if (cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex("_display_name"));
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            if (result == null) {
                String result2 = uri.getPath();
                int cut = result2.lastIndexOf(47);
                return cut != -1 ? result2.substring(cut + 1) : result2;
            }
            return result;
        } catch (Exception e2) {
            FileLog.e(e2);
            return "";
        }
    }

    public static String copyFileToCache(Uri uri, String ext) {
        return copyFileToCache(uri, ext, -1L);
    }

    /* JADX WARN: Code restructure failed: missing block: B:79:0x0180, code lost:
        r5 = r4.getAbsolutePath();
     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x0184, code lost:
        if (r1 == null) goto L145;
     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x0186, code lost:
        r1.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x018a, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:83:0x018b, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Removed duplicated region for block: B:132:0x01e9 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:153:0x01f7 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.String copyFileToCache(android.net.Uri r16, java.lang.String r17, long r18) {
        /*
            Method dump skipped, instructions count: 530
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.copyFileToCache(android.net.Uri, java.lang.String, long):java.lang.String");
    }

    public static void loadGalleryPhotosAlbums(final int guid) {
        Thread thread = new Thread(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda38
            @Override // java.lang.Runnable
            public final void run() {
                MediaController.lambda$loadGalleryPhotosAlbums$40(guid);
            }
        });
        thread.setPriority(1);
        thread.start();
    }

    /* JADX WARN: Can't wrap try/catch for region: R(35:2|274|3|4|278|5|6|11|(3:288|12|13)|(3:300|15|(18:17|(2:252|119)|131|316|132|133|(3:280|135|(7:137|(2:296|220)|230|(2:233|231)|340|234|235))|140|(1:142)(1:143)|144|145|(12:270|147|148|(1:150)(1:151)|264|152|153|256|154|(4:157|(3:336|159|338)(11:334|160|161|(8:254|163|164|326|165|(1:167)|(1:169)|170)(1:175)|(8:318|177|178|312|179|180|294|181)(1:188)|310|189|(6:191|192|292|193|(1:202)(2:196|(1:201)(1:200))|203)(1:204)|205|206|339)|337|155)|335|211)(1:218)|(0)|230|(1:231)|340|234|235))|20|21|(1:23)(1:24)|282|25|26|(12:314|28|29|304|30|31|(1:33)(1:34)|298|35|(6:38|272|39|(3:330|41|332)(17:328|42|43|(6:45|308|46|47|290|48)(1:53)|(8:302|55|56|286|57|58|276|59)(1:66)|258|67|(5:69|262|70|(1:79)(3:73|(3:250|75|(1:77))|78)|80)(1:85)|324|86|87|320|88|(2:90|(1:97)(1:96))(1:98)|99|100|333)|331|36)|329|108)(1:117)|(0)|131|316|132|133|(0)|140|(0)(0)|144|145|(0)(0)|(0)|230|(1:231)|340|234|235|(1:(0))) */
    /* JADX WARN: Can't wrap try/catch for region: R(37:2|274|3|4|278|5|6|11|288|12|13|(3:300|15|(18:17|(2:252|119)|131|316|132|133|(3:280|135|(7:137|(2:296|220)|230|(2:233|231)|340|234|235))|140|(1:142)(1:143)|144|145|(12:270|147|148|(1:150)(1:151)|264|152|153|256|154|(4:157|(3:336|159|338)(11:334|160|161|(8:254|163|164|326|165|(1:167)|(1:169)|170)(1:175)|(8:318|177|178|312|179|180|294|181)(1:188)|310|189|(6:191|192|292|193|(1:202)(2:196|(1:201)(1:200))|203)(1:204)|205|206|339)|337|155)|335|211)(1:218)|(0)|230|(1:231)|340|234|235))|20|21|(1:23)(1:24)|282|25|26|(12:314|28|29|304|30|31|(1:33)(1:34)|298|35|(6:38|272|39|(3:330|41|332)(17:328|42|43|(6:45|308|46|47|290|48)(1:53)|(8:302|55|56|286|57|58|276|59)(1:66)|258|67|(5:69|262|70|(1:79)(3:73|(3:250|75|(1:77))|78)|80)(1:85)|324|86|87|320|88|(2:90|(1:97)(1:96))(1:98)|99|100|333)|331|36)|329|108)(1:117)|(0)|131|316|132|133|(0)|140|(0)(0)|144|145|(0)(0)|(0)|230|(1:231)|340|234|235|(1:(0))) */
    /* JADX WARN: Code restructure failed: missing block: B:122:0x02f6, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:123:0x02f7, code lost:
        r36 = r2;
        r30 = "_data";
        r31 = "bucket_display_name";
        r12 = r21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:223:0x0511, code lost:
        r0 = th;
     */
    /* JADX WARN: Finally extract failed */
    /* JADX WARN: Removed duplicated region for block: B:117:0x02dd  */
    /* JADX WARN: Removed duplicated region for block: B:142:0x0356  */
    /* JADX WARN: Removed duplicated region for block: B:143:0x0359  */
    /* JADX WARN: Removed duplicated region for block: B:218:0x04fc  */
    /* JADX WARN: Removed duplicated region for block: B:233:0x0533 A[LOOP:2: B:231:0x052d->B:233:0x0533, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:23:0x00b0  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x00b3  */
    /* JADX WARN: Removed duplicated region for block: B:252:0x02eb A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:268:0x051b A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:270:0x036c A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:280:0x0325 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:296:0x0506 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:300:0x0073 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:306:0x0310 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:314:0x00c6 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static /* synthetic */ void lambda$loadGalleryPhotosAlbums$40(int r51) {
        /*
            Method dump skipped, instructions count: 1408
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.lambda$loadGalleryPhotosAlbums$40(int):void");
    }

    public static /* synthetic */ int lambda$loadGalleryPhotosAlbums$39(PhotoEntry o1, PhotoEntry o2) {
        if (o1.dateTaken < o2.dateTaken) {
            return 1;
        }
        if (o1.dateTaken > o2.dateTaken) {
            return -1;
        }
        return 0;
    }

    private static void broadcastNewPhotos(final int guid, final ArrayList<AlbumEntry> mediaAlbumsSorted, final ArrayList<AlbumEntry> photoAlbumsSorted, final Integer cameraAlbumIdFinal, final AlbumEntry allMediaAlbumFinal, final AlbumEntry allPhotosAlbumFinal, final AlbumEntry allVideosAlbumFinal, int delay) {
        Runnable runnable = broadcastPhotosRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        Runnable runnable2 = new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda40
            @Override // java.lang.Runnable
            public final void run() {
                MediaController.lambda$broadcastNewPhotos$41(guid, mediaAlbumsSorted, photoAlbumsSorted, cameraAlbumIdFinal, allMediaAlbumFinal, allPhotosAlbumFinal, allVideosAlbumFinal);
            }
        };
        broadcastPhotosRunnable = runnable2;
        AndroidUtilities.runOnUIThread(runnable2, delay);
    }

    public static /* synthetic */ void lambda$broadcastNewPhotos$41(int guid, ArrayList mediaAlbumsSorted, ArrayList photoAlbumsSorted, Integer cameraAlbumIdFinal, AlbumEntry allMediaAlbumFinal, AlbumEntry allPhotosAlbumFinal, AlbumEntry allVideosAlbumFinal) {
        if (PhotoViewer.getInstance().isVisible()) {
            broadcastNewPhotos(guid, mediaAlbumsSorted, photoAlbumsSorted, cameraAlbumIdFinal, allMediaAlbumFinal, allPhotosAlbumFinal, allVideosAlbumFinal, 1000);
            return;
        }
        allMediaAlbums = mediaAlbumsSorted;
        allPhotoAlbums = photoAlbumsSorted;
        broadcastPhotosRunnable = null;
        allPhotosAlbumEntry = allPhotosAlbumFinal;
        allMediaAlbumEntry = allMediaAlbumFinal;
        allVideosAlbumEntry = allVideosAlbumFinal;
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.albumsDidLoad, Integer.valueOf(guid), mediaAlbumsSorted, photoAlbumsSorted, cameraAlbumIdFinal);
    }

    public void scheduleVideoConvert(MessageObject messageObject) {
        scheduleVideoConvert(messageObject, false);
    }

    public boolean scheduleVideoConvert(MessageObject messageObject, boolean isEmpty) {
        if (messageObject == null || messageObject.videoEditedInfo == null) {
            return false;
        }
        if (isEmpty && !this.videoConvertQueue.isEmpty()) {
            return false;
        }
        if (isEmpty) {
            new File(messageObject.messageOwner.attachPath).delete();
        }
        this.videoConvertQueue.add(new VideoConvertMessage(messageObject, messageObject.videoEditedInfo));
        if (this.videoConvertQueue.size() == 1) {
            startVideoConvertFromQueue();
        }
        return true;
    }

    public void cancelVideoConvert(MessageObject messageObject) {
        if (messageObject != null && !this.videoConvertQueue.isEmpty()) {
            for (int a = 0; a < this.videoConvertQueue.size(); a++) {
                VideoConvertMessage videoConvertMessage = this.videoConvertQueue.get(a);
                MessageObject object = videoConvertMessage.messageObject;
                if (object.equals(messageObject) && object.currentAccount == messageObject.currentAccount) {
                    if (a == 0) {
                        synchronized (this.videoConvertSync) {
                            videoConvertMessage.videoEditedInfo.canceled = true;
                        }
                        return;
                    } else {
                        this.videoConvertQueue.remove(a);
                        return;
                    }
                }
            }
        }
    }

    private boolean startVideoConvertFromQueue() {
        if (!this.videoConvertQueue.isEmpty()) {
            VideoConvertMessage videoConvertMessage = this.videoConvertQueue.get(0);
            MessageObject messageObject = videoConvertMessage.messageObject;
            VideoEditedInfo videoEditedInfo = videoConvertMessage.videoEditedInfo;
            synchronized (this.videoConvertSync) {
                if (videoEditedInfo != null) {
                    videoEditedInfo.canceled = false;
                }
            }
            Intent intent = new Intent(ApplicationLoader.applicationContext, VideoEncodingService.class);
            intent.putExtra("path", messageObject.messageOwner.attachPath);
            intent.putExtra("currentAccount", messageObject.currentAccount);
            if (messageObject.messageOwner.media.document != null) {
                int a = 0;
                while (true) {
                    if (a >= messageObject.messageOwner.media.document.attributes.size()) {
                        break;
                    }
                    TLRPC.DocumentAttribute documentAttribute = messageObject.messageOwner.media.document.attributes.get(a);
                    if (!(documentAttribute instanceof TLRPC.TL_documentAttributeAnimated)) {
                        a++;
                    } else {
                        intent.putExtra("gif", true);
                        break;
                    }
                }
            }
            int a2 = messageObject.getId();
            if (a2 != 0) {
                try {
                    ApplicationLoader.applicationContext.startService(intent);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            VideoConvertRunnable.runConversion(videoConvertMessage);
            return true;
        }
        return false;
    }

    public static MediaCodecInfo selectCodec(String mimeType) {
        String name;
        int numCodecs = MediaCodecList.getCodecCount();
        MediaCodecInfo lastCodecInfo = null;
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (codecInfo.isEncoder()) {
                String[] types = codecInfo.getSupportedTypes();
                for (String type : types) {
                    if (type.equalsIgnoreCase(mimeType) && (name = (lastCodecInfo = codecInfo).getName()) != null) {
                        if (!name.equals("OMX.SEC.avc.enc")) {
                            return lastCodecInfo;
                        }
                        if (name.equals("OMX.SEC.AVC.Encoder")) {
                            return lastCodecInfo;
                        }
                    }
                }
                continue;
            }
        }
        return lastCodecInfo;
    }

    private static boolean isRecognizedFormat(int colorFormat) {
        switch (colorFormat) {
            case 19:
            case 20:
            case 21:
            case 39:
            case 2130706688:
                return true;
            default:
                return false;
        }
    }

    public static int selectColorFormat(MediaCodecInfo codecInfo, String mimeType) {
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mimeType);
        int lastColorFormat = 0;
        for (int i = 0; i < capabilities.colorFormats.length; i++) {
            int colorFormat = capabilities.colorFormats[i];
            if (isRecognizedFormat(colorFormat)) {
                lastColorFormat = colorFormat;
                if (!codecInfo.getName().equals("OMX.SEC.AVC.Encoder") || colorFormat != 19) {
                    return colorFormat;
                }
            }
        }
        return lastColorFormat;
    }

    public static int findTrack(MediaExtractor extractor, boolean audio) {
        int numTracks = extractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString("mime");
            if (audio) {
                if (mime.startsWith("audio/")) {
                    return i;
                }
            } else if (mime.startsWith("video/")) {
                return i;
            }
        }
        return -5;
    }

    public void didWriteData(final VideoConvertMessage message, final File file, final boolean last, final long lastFrameTimestamp, final long availableSize, final boolean error, final float progress) {
        final boolean firstWrite = message.videoEditedInfo.videoConvertFirstWrite;
        if (firstWrite) {
            message.videoEditedInfo.videoConvertFirstWrite = false;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaController$$ExternalSyntheticLambda26
            @Override // java.lang.Runnable
            public final void run() {
                MediaController.this.m367lambda$didWriteData$42$orgtelegrammessengerMediaController(error, last, message, file, progress, lastFrameTimestamp, firstWrite, availableSize);
            }
        });
    }

    /* renamed from: lambda$didWriteData$42$org-telegram-messenger-MediaController */
    public /* synthetic */ void m367lambda$didWriteData$42$orgtelegrammessengerMediaController(boolean error, boolean last, VideoConvertMessage message, File file, float progress, long lastFrameTimestamp, boolean firstWrite, long availableSize) {
        if (error || last) {
            synchronized (this.videoConvertSync) {
                message.videoEditedInfo.canceled = false;
            }
            this.videoConvertQueue.remove(message);
            startVideoConvertFromQueue();
        }
        if (error) {
            NotificationCenter.getInstance(message.currentAccount).postNotificationName(NotificationCenter.filePreparingFailed, message.messageObject, file.toString(), Float.valueOf(progress), Long.valueOf(lastFrameTimestamp));
            return;
        }
        if (firstWrite) {
            NotificationCenter.getInstance(message.currentAccount).postNotificationName(NotificationCenter.filePreparingStarted, message.messageObject, file.toString(), Float.valueOf(progress), Long.valueOf(lastFrameTimestamp));
        }
        NotificationCenter notificationCenter = NotificationCenter.getInstance(message.currentAccount);
        int i = NotificationCenter.fileNewChunkAvailable;
        Object[] objArr = new Object[6];
        objArr[0] = message.messageObject;
        objArr[1] = file.toString();
        objArr[2] = Long.valueOf(availableSize);
        objArr[3] = Long.valueOf(last ? file.length() : 0L);
        objArr[4] = Float.valueOf(progress);
        objArr[5] = Long.valueOf(lastFrameTimestamp);
        notificationCenter.postNotificationName(i, objArr);
    }

    public void pauseByRewind() {
        VideoPlayer videoPlayer = this.audioPlayer;
        if (videoPlayer != null) {
            videoPlayer.pause();
        }
    }

    public void resumeByRewind() {
        VideoPlayer videoPlayer = this.audioPlayer;
        if (videoPlayer != null && this.playingMessageObject != null && !this.isPaused) {
            if (videoPlayer.isBuffering()) {
                MessageObject currentMessageObject = this.playingMessageObject;
                cleanupPlayer(false, false);
                playMessage(currentMessageObject);
                return;
            }
            this.audioPlayer.play();
        }
    }

    /* loaded from: classes4.dex */
    public static class VideoConvertRunnable implements Runnable {
        private VideoConvertMessage convertMessage;

        private VideoConvertRunnable(VideoConvertMessage message) {
            this.convertMessage = message;
        }

        @Override // java.lang.Runnable
        public void run() {
            MediaController.getInstance().convertVideo(this.convertMessage);
        }

        public static void runConversion(final VideoConvertMessage obj) {
            new Thread(new Runnable() { // from class: org.telegram.messenger.MediaController$VideoConvertRunnable$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    MediaController.VideoConvertRunnable.lambda$runConversion$0(MediaController.VideoConvertMessage.this);
                }
            }).start();
        }

        public static /* synthetic */ void lambda$runConversion$0(VideoConvertMessage obj) {
            try {
                VideoConvertRunnable wrapper = new VideoConvertRunnable(obj);
                Thread th = new Thread(wrapper, "VideoConvertRunnable");
                th.start();
                th.join();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public boolean convertVideo(final VideoConvertMessage convertMessage) {
        int originalBitrate;
        String videoPath;
        long duration;
        long endTime;
        final VideoEditedInfo info;
        int resultHeight;
        int temp;
        int framerate;
        boolean canceled;
        boolean canceled2;
        MessageObject messageObject = convertMessage.messageObject;
        VideoEditedInfo info2 = convertMessage.videoEditedInfo;
        if (messageObject != null && info2 != null) {
            String videoPath2 = info2.originalPath;
            long startTime = info2.startTime;
            long avatarStartTime = info2.avatarStartTime;
            long endTime2 = info2.endTime;
            int resultWidth = info2.resultWidth;
            int resultHeight2 = info2.resultHeight;
            int rotationValue = info2.rotationValue;
            int originalWidth = info2.originalWidth;
            int originalHeight = info2.originalHeight;
            int framerate2 = info2.framerate;
            int bitrate = info2.bitrate;
            int bitrate2 = info2.originalBitrate;
            boolean isSecret = DialogObject.isEncryptedDialog(messageObject.getDialogId());
            final File cacheFile = new File(messageObject.messageOwner.attachPath);
            if (cacheFile.exists()) {
                cacheFile.delete();
            }
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder sb = new StringBuilder();
                sb.append("begin convert ");
                sb.append(videoPath2);
                sb.append(" startTime = ");
                sb.append(startTime);
                sb.append(" avatarStartTime = ");
                sb.append(avatarStartTime);
                sb.append(" endTime ");
                sb.append(endTime2);
                sb.append(" rWidth = ");
                sb.append(resultWidth);
                sb.append(" rHeight = ");
                sb.append(resultHeight2);
                sb.append(" rotation = ");
                sb.append(rotationValue);
                sb.append(" oWidth = ");
                sb.append(originalWidth);
                sb.append(" oHeight = ");
                sb.append(originalHeight);
                sb.append(" framerate = ");
                sb.append(framerate2);
                sb.append(" bitrate = ");
                sb.append(bitrate);
                sb.append(" originalBitrate = ");
                originalBitrate = bitrate2;
                sb.append(originalBitrate);
                FileLog.d(sb.toString());
            } else {
                originalBitrate = bitrate2;
            }
            if (videoPath2 != null) {
                videoPath = videoPath2;
            } else {
                videoPath = "";
            }
            if (startTime > 0 && endTime2 > 0) {
                duration = endTime2 - startTime;
                info = info2;
                endTime = endTime2;
            } else if (endTime2 > 0) {
                duration = endTime2;
                info = info2;
                endTime = endTime2;
            } else if (startTime > 0) {
                info = info2;
                endTime = endTime2;
                duration = info.originalDuration - startTime;
            } else {
                info = info2;
                endTime = endTime2;
                duration = info.originalDuration;
            }
            if (framerate2 == 0) {
                framerate2 = 25;
            } else if (framerate2 > 59) {
                framerate2 = 59;
            }
            if (rotationValue == 90 || rotationValue == 270) {
                temp = resultHeight2;
                resultHeight = resultWidth;
            } else {
                temp = resultWidth;
                resultHeight = resultHeight2;
            }
            if (!info.shouldLimitFps && framerate2 > 40 && Math.min(resultHeight, temp) <= 480) {
                framerate = 30;
            } else {
                framerate = framerate2;
            }
            boolean needCompress = (avatarStartTime == -1 && info.cropState == null && info.mediaEntities == null && info.paintPath == null && info.filterState == null && temp == originalWidth && resultHeight == originalHeight && rotationValue == 0 && !info.roundVideo && startTime == -1) ? false : true;
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("videoconvert", 0);
            long time = System.currentTimeMillis();
            VideoConvertorListener callback = new VideoConvertorListener() { // from class: org.telegram.messenger.MediaController.13
                private long lastAvailableSize = 0;

                @Override // org.telegram.messenger.MediaController.VideoConvertorListener
                public boolean checkConversionCanceled() {
                    return info.canceled;
                }

                @Override // org.telegram.messenger.MediaController.VideoConvertorListener
                public void didWriteData(long availableSize, float progress) {
                    if (info.canceled) {
                        return;
                    }
                    if (availableSize < 0) {
                        availableSize = cacheFile.length();
                    }
                    if (!info.needUpdateProgress && this.lastAvailableSize == availableSize) {
                        return;
                    }
                    this.lastAvailableSize = availableSize;
                    MediaController.this.didWriteData(convertMessage, cacheFile, false, 0L, availableSize, false, progress);
                }
            };
            info.videoConvertFirstWrite = true;
            MediaCodecVideoConvertor videoConvertor = new MediaCodecVideoConvertor();
            boolean error = videoConvertor.convertVideo(videoPath, cacheFile, rotationValue, isSecret, originalWidth, originalHeight, temp, resultHeight, framerate, bitrate, originalBitrate, startTime, endTime, avatarStartTime, needCompress, duration, info.filterState, info.paintPath, info.mediaEntities, info.isPhoto, info.cropState, info.roundVideo, callback);
            boolean canceled3 = info.canceled;
            if (canceled3) {
                canceled = canceled3;
            } else {
                synchronized (this.videoConvertSync) {
                    canceled2 = info.canceled;
                }
                canceled = canceled2;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("time=" + (System.currentTimeMillis() - time) + " canceled=" + canceled);
            }
            preferences.edit().putBoolean("isPreviousOk", true).apply();
            didWriteData(convertMessage, cacheFile, true, videoConvertor.getLastFrameTimestamp(), cacheFile.length(), error || canceled, 1.0f);
            return true;
        }
        return false;
    }

    public static int getVideoBitrate(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int bitrate = 0;
        try {
            retriever.setDataSource(path);
            bitrate = Integer.parseInt(retriever.extractMetadata(20));
        } catch (Exception e) {
            FileLog.e(e);
        }
        retriever.release();
        return bitrate;
    }

    public static int makeVideoBitrate(int originalHeight, int originalWidth, int originalBitrate, int height, int width) {
        float minCompressFactor;
        float compressFactor;
        int maxBitrate;
        if (Math.min(height, width) >= 1080) {
            maxBitrate = VIDEO_BITRATE_1080;
            compressFactor = 1.0f;
            minCompressFactor = 1.0f;
        } else if (Math.min(height, width) >= 720) {
            maxBitrate = 2600000;
            compressFactor = 1.0f;
            minCompressFactor = 1.0f;
        } else if (Math.min(height, width) >= 480) {
            maxBitrate = VIDEO_BITRATE_480;
            compressFactor = 0.75f;
            minCompressFactor = 0.9f;
        } else {
            maxBitrate = VIDEO_BITRATE_360;
            compressFactor = 0.6f;
            minCompressFactor = 0.7f;
        }
        int remeasuredBitrate = (int) (((int) (originalBitrate / Math.min(originalHeight / height, originalWidth / width))) * compressFactor);
        int minBitrate = (int) (getVideoBitrateWithFactor(minCompressFactor) / (921600.0f / (width * height)));
        if (originalBitrate < minBitrate) {
            return remeasuredBitrate;
        }
        if (remeasuredBitrate > maxBitrate) {
            return maxBitrate;
        }
        return Math.max(remeasuredBitrate, minBitrate);
    }

    private static int getVideoBitrateWithFactor(float f) {
        return (int) (2000.0f * f * 1000.0f * 1.13f);
    }

    /* loaded from: classes4.dex */
    public static class PlaylistGlobalSearchParams {
        final long dialogId;
        public boolean endReached;
        final FiltersView.MediaFilterData filter;
        public int folderId;
        final long maxDate;
        final long minDate;
        public int nextSearchRate;
        final String query;
        public int totalCount;

        public PlaylistGlobalSearchParams(String query, long dialogId, long minDate, long maxDate, FiltersView.MediaFilterData filter) {
            this.filter = filter;
            this.query = query;
            this.dialogId = dialogId;
            this.minDate = minDate;
            this.maxDate = maxDate;
        }
    }

    public boolean currentPlaylistIsGlobalSearch() {
        return this.playlistGlobalSearchParams != null;
    }
}
