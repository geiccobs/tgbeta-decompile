package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.view.InputDeviceCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.firebase.appindexing.builders.TimerBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.PatternCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ColorPicker;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.WallpaperCheckBoxView;
import org.telegram.ui.Components.WallpaperParallaxEffect;
import org.telegram.ui.ThemePreviewActivity;
import org.telegram.ui.WallpapersListActivity;
/* loaded from: classes4.dex */
public class ThemePreviewActivity extends BaseFragment implements DownloadController.FileDownloadProgressListener, NotificationCenter.NotificationCenterDelegate {
    public static final int SCREEN_TYPE_ACCENT_COLOR = 1;
    public static final int SCREEN_TYPE_CHANGE_BACKGROUND = 2;
    public static final int SCREEN_TYPE_PREVIEW = 0;
    private int TAG;
    private Theme.ThemeAccent accent;
    private ActionBar actionBar2;
    private HintView animationHint;
    private Runnable applyColorAction;
    private boolean applyColorScheduled;
    private Theme.ThemeInfo applyingTheme;
    private FrameLayout backgroundButtonsContainer;
    private WallpaperCheckBoxView[] backgroundCheckBoxView;
    private int backgroundColor;
    private int backgroundGradientColor1;
    private int backgroundGradientColor2;
    private int backgroundGradientColor3;
    private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
    private BackupImageView backgroundImage;
    private ImageView backgroundPlayAnimationImageView;
    private FrameLayout backgroundPlayAnimationView;
    private AnimatorSet backgroundPlayViewAnimator;
    private int backgroundRotation;
    private int backupAccentColor;
    private int backupAccentColor2;
    private long backupBackgroundGradientOverrideColor1;
    private long backupBackgroundGradientOverrideColor2;
    private long backupBackgroundGradientOverrideColor3;
    private long backupBackgroundOverrideColor;
    private int backupBackgroundRotation;
    private float backupIntensity;
    private int backupMyMessagesAccentColor;
    private boolean backupMyMessagesAnimated;
    private int backupMyMessagesGradientAccentColor1;
    private int backupMyMessagesGradientAccentColor2;
    private int backupMyMessagesGradientAccentColor3;
    private String backupSlug;
    private final PorterDuff.Mode blendMode;
    private Bitmap blurredBitmap;
    private FrameLayout bottomOverlayChat;
    private TextView bottomOverlayChatText;
    private TextView cancelButton;
    private int checkColor;
    private ColorPicker colorPicker;
    private int colorType;
    private float currentIntensity;
    private Object currentWallpaper;
    private Bitmap currentWallpaperBitmap;
    private WallpaperActivityDelegate delegate;
    private boolean deleteOnCancel;
    private DialogsAdapter dialogsAdapter;
    private TextView doneButton;
    private View dotsContainer;
    private TextView dropDown;
    private ActionBarMenuItem dropDownContainer;
    private boolean editingTheme;
    private ImageView floatingButton;
    private FrameLayout frameLayout;
    private String imageFilter;
    private HeaderCell intensityCell;
    private SeekBarView intensitySeekBar;
    private boolean isBlurred;
    private boolean isMotion;
    private int lastPickedColor;
    private int lastPickedColorNum;
    private TLRPC.TL_wallPaper lastSelectedPattern;
    private RecyclerListView listView;
    private RecyclerListView listView2;
    private String loadingFile;
    private File loadingFileObject;
    private TLRPC.PhotoSize loadingSize;
    private int maxWallpaperSize;
    private MessagesAdapter messagesAdapter;
    private FrameLayout messagesButtonsContainer;
    private WallpaperCheckBoxView[] messagesCheckBoxView;
    private ImageView messagesPlayAnimationImageView;
    private FrameLayout messagesPlayAnimationView;
    private AnimatorSet messagesPlayViewAnimator;
    private AnimatorSet motionAnimation;
    Theme.MessageDrawable msgOutDrawable;
    Theme.MessageDrawable msgOutDrawableSelected;
    Theme.MessageDrawable msgOutMediaDrawable;
    Theme.MessageDrawable msgOutMediaDrawableSelected;
    private boolean nightTheme;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private Bitmap originalBitmap;
    private FrameLayout page1;
    private FrameLayout page2;
    private WallpaperParallaxEffect parallaxEffect;
    private float parallaxScale;
    private int patternColor;
    private FrameLayout[] patternLayout;
    private AnimatorSet patternViewAnimation;
    private ArrayList<Object> patterns;
    private PatternsAdapter patternsAdapter;
    private FrameLayout[] patternsButtonsContainer;
    private TextView[] patternsCancelButton;
    private HashMap<Long, Object> patternsDict;
    private LinearLayoutManager patternsLayoutManager;
    private RecyclerListView patternsListView;
    private TextView[] patternsSaveButton;
    private int previousBackgroundColor;
    private int previousBackgroundGradientColor1;
    private int previousBackgroundGradientColor2;
    private int previousBackgroundGradientColor3;
    private int previousBackgroundRotation;
    private float previousIntensity;
    private TLRPC.TL_wallPaper previousSelectedPattern;
    private boolean progressVisible;
    private RadialProgress2 radialProgress;
    private boolean removeBackgroundOverride;
    private boolean rotatePreview;
    private FrameLayout saveButtonsContainer;
    private ActionBarMenuItem saveItem;
    private final int screenType;
    private TLRPC.TL_wallPaper selectedPattern;
    private Drawable sheetDrawable;
    private boolean showColor;
    private List<ThemeDescription> themeDescriptions;
    private UndoView undoView;
    public boolean useDefaultThemeForButtons;
    private ViewPager viewPager;
    private boolean wasScroll;
    private long watchForKeyboardEndTime;

    /* loaded from: classes4.dex */
    public interface WallpaperActivityDelegate {
        void didSetNewBackground();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4652lambda$new$0$orgtelegramuiThemePreviewActivity() {
        this.applyColorScheduled = false;
        applyColor(this.lastPickedColor, this.lastPickedColorNum);
        this.lastPickedColorNum = -1;
    }

    public ThemePreviewActivity(Object wallPaper, Bitmap bitmap) {
        this(wallPaper, bitmap, false, false);
    }

    public ThemePreviewActivity(Object wallPaper, Bitmap bitmap, boolean rotate, boolean openColor) {
        this.useDefaultThemeForButtons = true;
        this.colorType = 1;
        this.msgOutDrawable = new Theme.MessageDrawable(0, true, false);
        this.msgOutDrawableSelected = new Theme.MessageDrawable(0, true, true);
        this.msgOutMediaDrawable = new Theme.MessageDrawable(1, true, false);
        this.msgOutMediaDrawableSelected = new Theme.MessageDrawable(1, true, true);
        this.lastPickedColorNum = -1;
        this.applyColorAction = new Runnable() { // from class: org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                ThemePreviewActivity.this.m4652lambda$new$0$orgtelegramuiThemePreviewActivity();
            }
        };
        this.patternLayout = new FrameLayout[2];
        this.patternsCancelButton = new TextView[2];
        this.patternsSaveButton = new TextView[2];
        this.patternsButtonsContainer = new FrameLayout[2];
        this.patternsDict = new HashMap<>();
        this.currentIntensity = 0.5f;
        this.blendMode = PorterDuff.Mode.SRC_IN;
        this.parallaxScale = 1.0f;
        this.loadingFile = null;
        this.loadingFileObject = null;
        this.loadingSize = null;
        this.imageFilter = "640_360";
        this.maxWallpaperSize = 1920;
        this.screenType = 2;
        this.showColor = openColor;
        this.currentWallpaper = wallPaper;
        this.currentWallpaperBitmap = bitmap;
        this.rotatePreview = rotate;
        if (wallPaper instanceof WallpapersListActivity.ColorWallpaper) {
            WallpapersListActivity.ColorWallpaper object = (WallpapersListActivity.ColorWallpaper) wallPaper;
            this.isMotion = object.motion;
            TLRPC.TL_wallPaper tL_wallPaper = object.pattern;
            this.selectedPattern = tL_wallPaper;
            if (tL_wallPaper != null) {
                float f = object.intensity;
                this.currentIntensity = f;
                if (f < 0.0f && !Theme.getActiveTheme().isDark()) {
                    this.currentIntensity *= -1.0f;
                }
            }
        }
        this.msgOutDrawable.themePreview = true;
        this.msgOutMediaDrawable.themePreview = true;
        this.msgOutDrawableSelected.themePreview = true;
        this.msgOutMediaDrawableSelected.themePreview = true;
    }

    public ThemePreviewActivity(Theme.ThemeInfo themeInfo) {
        this(themeInfo, false, 0, false, false);
    }

    public ThemePreviewActivity(Theme.ThemeInfo themeInfo, boolean deleteFile, int screenType, boolean edit, boolean night) {
        this.useDefaultThemeForButtons = true;
        this.colorType = 1;
        this.msgOutDrawable = new Theme.MessageDrawable(0, true, false);
        this.msgOutDrawableSelected = new Theme.MessageDrawable(0, true, true);
        this.msgOutMediaDrawable = new Theme.MessageDrawable(1, true, false);
        this.msgOutMediaDrawableSelected = new Theme.MessageDrawable(1, true, true);
        this.lastPickedColorNum = -1;
        this.applyColorAction = new Runnable() { // from class: org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                ThemePreviewActivity.this.m4652lambda$new$0$orgtelegramuiThemePreviewActivity();
            }
        };
        this.patternLayout = new FrameLayout[2];
        this.patternsCancelButton = new TextView[2];
        this.patternsSaveButton = new TextView[2];
        this.patternsButtonsContainer = new FrameLayout[2];
        this.patternsDict = new HashMap<>();
        this.currentIntensity = 0.5f;
        this.blendMode = PorterDuff.Mode.SRC_IN;
        this.parallaxScale = 1.0f;
        this.loadingFile = null;
        this.loadingFileObject = null;
        this.loadingSize = null;
        this.imageFilter = "640_360";
        this.maxWallpaperSize = 1920;
        this.screenType = screenType;
        this.nightTheme = night;
        this.applyingTheme = themeInfo;
        this.deleteOnCancel = deleteFile;
        this.editingTheme = edit;
        if (screenType == 1) {
            Theme.ThemeAccent accent = themeInfo.getAccent(!edit);
            this.accent = accent;
            if (accent != null) {
                this.useDefaultThemeForButtons = false;
                this.backupAccentColor = accent.accentColor;
                this.backupAccentColor2 = this.accent.accentColor2;
                this.backupMyMessagesAccentColor = this.accent.myMessagesAccentColor;
                this.backupMyMessagesGradientAccentColor1 = this.accent.myMessagesGradientAccentColor1;
                this.backupMyMessagesGradientAccentColor2 = this.accent.myMessagesGradientAccentColor2;
                this.backupMyMessagesGradientAccentColor3 = this.accent.myMessagesGradientAccentColor3;
                this.backupMyMessagesAnimated = this.accent.myMessagesAnimated;
                this.backupBackgroundOverrideColor = this.accent.backgroundOverrideColor;
                this.backupBackgroundGradientOverrideColor1 = this.accent.backgroundGradientOverrideColor1;
                this.backupBackgroundGradientOverrideColor2 = this.accent.backgroundGradientOverrideColor2;
                this.backupBackgroundGradientOverrideColor3 = this.accent.backgroundGradientOverrideColor3;
                this.backupIntensity = this.accent.patternIntensity;
                this.backupSlug = this.accent.patternSlug;
                this.backupBackgroundRotation = this.accent.backgroundRotation;
            }
        } else {
            if (screenType == 0) {
                this.useDefaultThemeForButtons = false;
            }
            Theme.ThemeAccent accent2 = themeInfo.getAccent(false);
            this.accent = accent2;
            if (accent2 != null) {
                this.selectedPattern = accent2.pattern;
            }
        }
        Theme.ThemeAccent themeAccent = this.accent;
        if (themeAccent != null) {
            this.isMotion = themeAccent.patternMotion;
            if (!TextUtils.isEmpty(this.accent.patternSlug)) {
                this.currentIntensity = this.accent.patternIntensity;
            }
            Theme.applyThemeTemporary(this.applyingTheme, true);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.goingToPreviewTheme, new Object[0]);
        this.msgOutDrawable.themePreview = true;
        this.msgOutMediaDrawable.themePreview = true;
        this.msgOutDrawableSelected.themePreview = true;
        this.msgOutMediaDrawableSelected.themePreview = true;
    }

    public void setInitialModes(boolean blur, boolean motion) {
        this.isBlurred = blur;
        this.isMotion = motion;
    }

    /* JADX WARN: Removed duplicated region for block: B:120:0x064c  */
    /* JADX WARN: Removed duplicated region for block: B:151:0x0744  */
    /* JADX WARN: Removed duplicated region for block: B:154:0x0749  */
    /* JADX WARN: Removed duplicated region for block: B:201:0x0847  */
    /* JADX WARN: Removed duplicated region for block: B:245:0x0993  */
    /* JADX WARN: Removed duplicated region for block: B:289:0x0d01  */
    @Override // org.telegram.ui.ActionBar.BaseFragment
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.view.View createView(android.content.Context r42) {
        /*
            Method dump skipped, instructions count: 3872
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.createView(android.content.Context):android.view.View");
    }

    public static /* synthetic */ void lambda$createView$1(View view, int position) {
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4639lambda$createView$2$orgtelegramuiThemePreviewActivity(ImageReceiver imageReceiver, boolean set, boolean thumb, boolean memCache) {
        if (!(this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
            Drawable dr = imageReceiver.getDrawable();
            if (set && dr != null) {
                if (!Theme.hasThemeKey(Theme.key_chat_serviceBackground) || (this.backgroundImage.getBackground() instanceof MotionBackgroundDrawable)) {
                    Theme.applyChatServiceMessageColor(AndroidUtilities.calcDrawableColor(dr), dr);
                }
                this.listView2.invalidateViews();
                FrameLayout frameLayout = this.backgroundButtonsContainer;
                if (frameLayout != null) {
                    int N = frameLayout.getChildCount();
                    for (int a = 0; a < N; a++) {
                        this.backgroundButtonsContainer.getChildAt(a).invalidate();
                    }
                }
                FrameLayout frameLayout2 = this.messagesButtonsContainer;
                if (frameLayout2 != null) {
                    int N2 = frameLayout2.getChildCount();
                    for (int a2 = 0; a2 < N2; a2++) {
                        this.messagesButtonsContainer.getChildAt(a2).invalidate();
                    }
                }
                RadialProgress2 radialProgress2 = this.radialProgress;
                if (radialProgress2 != null) {
                    radialProgress2.setColors(Theme.key_chat_serviceBackground, Theme.key_chat_serviceBackground, Theme.key_chat_serviceText, Theme.key_chat_serviceText);
                }
                if (!thumb && this.isBlurred && this.blurredBitmap == null) {
                    this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(false);
                    updateBlurred();
                    this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(true);
                }
            }
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4640lambda$createView$3$orgtelegramuiThemePreviewActivity(View view) {
        this.dropDownContainer.toggleSubMenu();
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4641lambda$createView$4$orgtelegramuiThemePreviewActivity(View view, int position, float x, float y) {
        if (view instanceof ChatMessageCell) {
            ChatMessageCell cell = (ChatMessageCell) view;
            if (cell.isInsideBackground(x, y)) {
                if (cell.getMessageObject().isOutOwner()) {
                    selectColorType(3);
                    return;
                } else {
                    selectColorType(1);
                    return;
                }
            }
            selectColorType(2);
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4642lambda$createView$5$orgtelegramuiThemePreviewActivity(int offsetX, int offsetY, float angle) {
        float progress;
        if (!this.isMotion) {
            return;
        }
        this.backgroundImage.getBackground();
        if (this.motionAnimation != null) {
            progress = (this.backgroundImage.getScaleX() - 1.0f) / (this.parallaxScale - 1.0f);
        } else {
            progress = 1.0f;
        }
        this.backgroundImage.setTranslationX(offsetX * progress);
        this.backgroundImage.setTranslationY(offsetY * progress);
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4643lambda$createView$6$orgtelegramuiThemePreviewActivity(View view) {
        boolean done;
        int rotation;
        int color;
        String slug;
        File toFile;
        Theme.OverrideWallpaperInfo wallpaperInfo;
        String slugStr;
        int color2;
        int color3;
        String slug2;
        File f;
        boolean done2;
        boolean sameFile = false;
        Theme.ThemeInfo theme = Theme.getActiveTheme();
        String originalFileName = theme.generateWallpaperName(null, this.isBlurred);
        String fileName = this.isBlurred ? theme.generateWallpaperName(null, false) : originalFileName;
        File toFile2 = new File(ApplicationLoader.getFilesDirFixed(), originalFileName);
        Object obj = this.currentWallpaper;
        if (obj instanceof TLRPC.TL_wallPaper) {
            if (this.originalBitmap != null) {
                try {
                    FileOutputStream stream = new FileOutputStream(toFile2);
                    this.originalBitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream);
                    stream.close();
                    done = true;
                } catch (Exception e) {
                    done = false;
                    FileLog.e(e);
                }
            } else {
                ImageReceiver imageReceiver = this.backgroundImage.getImageReceiver();
                if (imageReceiver.hasNotThumb() || imageReceiver.hasStaticThumb()) {
                    Bitmap bitmap = imageReceiver.getBitmap();
                    try {
                        FileOutputStream stream2 = new FileOutputStream(toFile2);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream2);
                        stream2.close();
                        done2 = true;
                    } catch (Exception e2) {
                        FileLog.e(e2);
                        done2 = false;
                    }
                    done = done2;
                } else {
                    done = false;
                }
            }
            if (!done) {
                File f2 = FileLoader.getInstance(this.currentAccount).getPathToAttach(((TLRPC.TL_wallPaper) this.currentWallpaper).document, true);
                try {
                    boolean done3 = AndroidUtilities.copyFile(f2, toFile2);
                    done = done3;
                } catch (Exception e3) {
                    done = false;
                    FileLog.e(e3);
                }
            }
        } else {
            boolean done4 = obj instanceof WallpapersListActivity.ColorWallpaper;
            if (done4) {
                if (this.selectedPattern != null) {
                    try {
                        WallpapersListActivity.ColorWallpaper colorWallpaper = (WallpapersListActivity.ColorWallpaper) obj;
                        Bitmap bitmap2 = this.backgroundImage.getImageReceiver().getBitmap();
                        Bitmap dst = Bitmap.createBitmap(bitmap2.getWidth(), bitmap2.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(dst);
                        if (this.backgroundGradientColor2 == 0) {
                            if (this.backgroundGradientColor1 == 0) {
                                canvas.drawColor(this.backgroundColor);
                            } else {
                                GradientDrawable gradientDrawable = new GradientDrawable(BackgroundGradientDrawable.getGradientOrientation(this.backgroundRotation), new int[]{this.backgroundColor, this.backgroundGradientColor1});
                                gradientDrawable.setBounds(0, 0, dst.getWidth(), dst.getHeight());
                                gradientDrawable.draw(canvas);
                            }
                        }
                        Paint paint = new Paint(2);
                        paint.setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
                        paint.setAlpha((int) (Math.abs(this.currentIntensity) * 255.0f));
                        canvas.drawBitmap(bitmap2, 0.0f, 0.0f, paint);
                        FileOutputStream stream3 = new FileOutputStream(toFile2);
                        if (this.backgroundGradientColor2 != 0) {
                            dst.compress(Bitmap.CompressFormat.PNG, 100, stream3);
                        } else {
                            dst.compress(Bitmap.CompressFormat.JPEG, 87, stream3);
                        }
                        stream3.close();
                        done = true;
                    } catch (Throwable e4) {
                        FileLog.e(e4);
                        done = false;
                    }
                } else {
                    done = true;
                }
            } else if (obj instanceof WallpapersListActivity.FileWallpaper) {
                WallpapersListActivity.FileWallpaper wallpaper = (WallpapersListActivity.FileWallpaper) obj;
                if (wallpaper.resId != 0 || Theme.THEME_BACKGROUND_SLUG.equals(wallpaper.slug)) {
                    done = true;
                } else {
                    try {
                        File fromFile = wallpaper.originalPath != null ? wallpaper.originalPath : wallpaper.path;
                        boolean equals = fromFile.equals(toFile2);
                        sameFile = equals;
                        if (equals) {
                            done = true;
                        } else {
                            done = AndroidUtilities.copyFile(fromFile, toFile2);
                        }
                    } catch (Exception e5) {
                        done = false;
                        FileLog.e(e5);
                    }
                }
            } else if (obj instanceof MediaController.SearchImage) {
                MediaController.SearchImage wallpaper2 = (MediaController.SearchImage) obj;
                if (wallpaper2.photo != null) {
                    TLRPC.PhotoSize image = FileLoader.getClosestPhotoSizeWithSize(wallpaper2.photo.sizes, this.maxWallpaperSize, true);
                    File f3 = FileLoader.getInstance(this.currentAccount).getPathToAttach(image, true);
                    f = f3;
                } else {
                    f = ImageLoader.getHttpFilePath(wallpaper2.imageUrl, "jpg");
                }
                try {
                    boolean done5 = AndroidUtilities.copyFile(f, toFile2);
                    done = done5;
                } catch (Exception e6) {
                    FileLog.e(e6);
                    done = false;
                }
            } else {
                done = false;
            }
        }
        if (this.isBlurred) {
            try {
                File blurredFile = new File(ApplicationLoader.getFilesDirFixed(), fileName);
                FileOutputStream stream4 = new FileOutputStream(blurredFile);
                this.blurredBitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream4);
                stream4.close();
                done = true;
            } catch (Throwable e7) {
                FileLog.e(e7);
                done = false;
            }
        }
        int gradientColor1 = 0;
        int gradientColor2 = 0;
        int gradientColor3 = 0;
        File path = null;
        Object obj2 = this.currentWallpaper;
        if (obj2 instanceof TLRPC.TL_wallPaper) {
            slug = ((TLRPC.TL_wallPaper) obj2).slug;
            color = 0;
            rotation = 45;
        } else if (obj2 instanceof WallpapersListActivity.ColorWallpaper) {
            if (Theme.DEFAULT_BACKGROUND_SLUG.equals(((WallpapersListActivity.ColorWallpaper) obj2).slug)) {
                color3 = 0;
                slug = Theme.DEFAULT_BACKGROUND_SLUG;
                rotation = 45;
            } else {
                TLRPC.TL_wallPaper tL_wallPaper = this.selectedPattern;
                if (tL_wallPaper != null) {
                    slug2 = tL_wallPaper.slug;
                } else {
                    slug2 = Theme.COLOR_BACKGROUND_SLUG;
                }
                color3 = this.backgroundColor;
                gradientColor1 = this.backgroundGradientColor1;
                gradientColor2 = this.backgroundGradientColor2;
                gradientColor3 = this.backgroundGradientColor3;
                rotation = this.backgroundRotation;
                slug = slug2;
            }
            color = color3;
        } else if (obj2 instanceof WallpapersListActivity.FileWallpaper) {
            WallpapersListActivity.FileWallpaper wallPaper = (WallpapersListActivity.FileWallpaper) obj2;
            String slug3 = wallPaper.slug;
            path = wallPaper.path;
            slug = slug3;
            rotation = 45;
            color = 0;
        } else if (!(obj2 instanceof MediaController.SearchImage)) {
            rotation = 45;
            color = 0;
            slug = Theme.DEFAULT_BACKGROUND_SLUG;
        } else {
            MediaController.SearchImage wallPaper2 = (MediaController.SearchImage) obj2;
            if (wallPaper2.photo != null) {
                color2 = 0;
                TLRPC.PhotoSize image2 = FileLoader.getClosestPhotoSizeWithSize(wallPaper2.photo.sizes, this.maxWallpaperSize, true);
                path = FileLoader.getInstance(this.currentAccount).getPathToAttach(image2, true);
            } else {
                color2 = 0;
                path = ImageLoader.getHttpFilePath(wallPaper2.imageUrl, "jpg");
            }
            slug = "";
            rotation = 45;
            color = color2;
        }
        Theme.OverrideWallpaperInfo wallpaperInfo2 = new Theme.OverrideWallpaperInfo();
        wallpaperInfo2.fileName = fileName;
        wallpaperInfo2.originalFileName = originalFileName;
        wallpaperInfo2.slug = slug;
        wallpaperInfo2.isBlurred = this.isBlurred;
        wallpaperInfo2.isMotion = this.isMotion;
        wallpaperInfo2.color = color;
        wallpaperInfo2.gradientColor1 = gradientColor1;
        wallpaperInfo2.gradientColor2 = gradientColor2;
        wallpaperInfo2.gradientColor3 = gradientColor3;
        wallpaperInfo2.rotation = rotation;
        wallpaperInfo2.intensity = this.currentIntensity;
        Object obj3 = this.currentWallpaper;
        if (!(obj3 instanceof WallpapersListActivity.ColorWallpaper)) {
            toFile = toFile2;
        } else {
            WallpapersListActivity.ColorWallpaper colorWallpaper2 = (WallpapersListActivity.ColorWallpaper) obj3;
            if (!Theme.COLOR_BACKGROUND_SLUG.equals(slug) && !Theme.THEME_BACKGROUND_SLUG.equals(slug) && !Theme.DEFAULT_BACKGROUND_SLUG.equals(slug)) {
                slugStr = slug;
            } else {
                slugStr = null;
            }
            float intensity = colorWallpaper2.intensity;
            if (intensity < 0.0f && !Theme.getActiveTheme().isDark()) {
                intensity *= -1.0f;
            }
            toFile = toFile2;
            if (colorWallpaper2.parentWallpaper != null && colorWallpaper2.color == color && colorWallpaper2.gradientColor1 == gradientColor1 && colorWallpaper2.gradientColor2 == gradientColor2 && colorWallpaper2.gradientColor3 == gradientColor3) {
                if (TextUtils.equals(colorWallpaper2.slug, slugStr) && colorWallpaper2.gradientRotation == rotation) {
                    if (this.selectedPattern == null || Math.abs(intensity - this.currentIntensity) < 0.001f) {
                        wallpaperInfo2.wallpaperId = colorWallpaper2.parentWallpaper.id;
                        wallpaperInfo2.accessHash = colorWallpaper2.parentWallpaper.access_hash;
                    }
                }
            }
        }
        MessagesController.getInstance(this.currentAccount).saveWallpaperToServer(path, wallpaperInfo2, slug != null, 0L);
        if (done) {
            Theme.serviceMessageColorBackup = Theme.getColor(Theme.key_chat_serviceBackground);
            if (!Theme.THEME_BACKGROUND_SLUG.equals(wallpaperInfo2.slug)) {
                wallpaperInfo = wallpaperInfo2;
            } else {
                wallpaperInfo = null;
            }
            Theme.getActiveTheme().setOverrideWallpaper(wallpaperInfo);
            Theme.reloadWallpaper();
            if (!sameFile) {
                ImageLoader.getInstance().removeImage(ImageLoader.getHttpFileName(toFile.getAbsolutePath()) + "@100_100");
            }
        }
        WallpaperActivityDelegate wallpaperActivityDelegate = this.delegate;
        if (wallpaperActivityDelegate != null) {
            wallpaperActivityDelegate.didSetNewBackground();
        }
        finishFragment();
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4644lambda$createView$7$orgtelegramuiThemePreviewActivity(int num, WallpaperCheckBoxView view, View v) {
        if (this.backgroundButtonsContainer.getAlpha() != 1.0f || this.patternViewAnimation != null) {
            return;
        }
        int i = this.screenType;
        if ((i == 1 || (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) && num == 2) {
            view.setChecked(!view.isChecked(), true);
            boolean isChecked = view.isChecked();
            this.isMotion = isChecked;
            this.parallaxEffect.setEnabled(isChecked);
            animateMotionChange();
            return;
        }
        boolean z = false;
        if (num == 1 && (i == 1 || (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper))) {
            if (this.backgroundCheckBoxView[1].isChecked()) {
                this.lastSelectedPattern = this.selectedPattern;
                this.backgroundImage.setImageDrawable(null);
                this.selectedPattern = null;
                this.isMotion = false;
                updateButtonState(false, true);
                animateMotionChange();
                if (this.patternLayout[1].getVisibility() == 0) {
                    if (this.screenType == 1) {
                        showPatternsView(0, true, true);
                    } else {
                        showPatternsView(num, this.patternLayout[num].getVisibility() != 0, true);
                    }
                }
            } else {
                selectPattern(this.lastSelectedPattern != null ? -1 : 0);
                if (this.screenType == 1) {
                    showPatternsView(1, true, true);
                } else {
                    showPatternsView(num, this.patternLayout[num].getVisibility() != 0, true);
                }
            }
            WallpaperCheckBoxView wallpaperCheckBoxView = this.backgroundCheckBoxView[1];
            if (this.selectedPattern != null) {
                z = true;
            }
            wallpaperCheckBoxView.setChecked(z, true);
            updateSelectedPattern(true);
            this.patternsListView.invalidateViews();
            updateMotionButton();
        } else if (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
            if (this.patternLayout[num].getVisibility() != 0) {
                z = true;
            }
            showPatternsView(num, z, true);
        } else if (i != 1) {
            view.setChecked(!view.isChecked(), true);
            if (num == 0) {
                boolean isChecked2 = view.isChecked();
                this.isBlurred = isChecked2;
                if (isChecked2) {
                    this.backgroundImage.getImageReceiver().setForceCrossfade(true);
                }
                updateBlurred();
                return;
            }
            boolean isChecked3 = view.isChecked();
            this.isMotion = isChecked3;
            this.parallaxEffect.setEnabled(isChecked3);
            animateMotionChange();
        }
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4645lambda$createView$8$orgtelegramuiThemePreviewActivity(int num, WallpaperCheckBoxView view, View v) {
        if (this.messagesButtonsContainer.getAlpha() == 1.0f && num == 0) {
            view.setChecked(!view.isChecked(), true);
            this.accent.myMessagesAnimated = view.isChecked();
            Theme.refreshThemeColors(true, true);
            this.listView2.invalidateViews();
        }
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4646lambda$createView$9$orgtelegramuiThemePreviewActivity(int num, View v) {
        if (this.patternViewAnimation != null) {
            return;
        }
        if (num == 0) {
            this.backgroundRotation = this.previousBackgroundRotation;
            setBackgroundColor(this.previousBackgroundGradientColor3, 3, true, true);
            setBackgroundColor(this.previousBackgroundGradientColor2, 2, true, true);
            setBackgroundColor(this.previousBackgroundGradientColor1, 1, true, true);
            setBackgroundColor(this.previousBackgroundColor, 0, true, true);
        } else {
            TLRPC.TL_wallPaper tL_wallPaper = this.previousSelectedPattern;
            this.selectedPattern = tL_wallPaper;
            if (tL_wallPaper != null) {
                this.backgroundImage.setImage(ImageLocation.getForDocument(tL_wallPaper.document), this.imageFilter, null, null, "jpg", this.selectedPattern.document.size, 1, this.selectedPattern);
            } else {
                this.backgroundImage.setImageDrawable(null);
            }
            this.backgroundCheckBoxView[1].setChecked(this.selectedPattern != null, false);
            float f = this.previousIntensity;
            this.currentIntensity = f;
            this.intensitySeekBar.setProgress(f);
            this.backgroundImage.getImageReceiver().setAlpha(this.currentIntensity);
            updateButtonState(false, true);
            updateSelectedPattern(true);
        }
        if (this.screenType == 2) {
            showPatternsView(num, false, true);
            return;
        }
        if (this.selectedPattern == null) {
            if (this.isMotion) {
                this.isMotion = false;
                this.backgroundCheckBoxView[0].setChecked(false, true);
                animateMotionChange();
            }
            updateMotionButton();
        }
        showPatternsView(0, true, true);
    }

    /* renamed from: lambda$createView$10$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4634lambda$createView$10$orgtelegramuiThemePreviewActivity(int num, View v) {
        if (this.patternViewAnimation != null) {
            return;
        }
        if (this.screenType == 2) {
            showPatternsView(num, false, true);
        } else {
            showPatternsView(0, true, true);
        }
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4635lambda$createView$11$orgtelegramuiThemePreviewActivity(View view, int position) {
        boolean previousMotion = this.selectedPattern != null;
        selectPattern(position);
        if (previousMotion == (this.selectedPattern == null)) {
            animateMotionChange();
            updateMotionButton();
        }
        updateSelectedPattern(true);
        this.backgroundCheckBoxView[1].setChecked(this.selectedPattern != null, true);
        this.patternsListView.invalidateViews();
        int left = view.getLeft();
        int right = view.getRight();
        int extra = AndroidUtilities.dp(52.0f);
        if (left - extra < 0) {
            this.patternsListView.smoothScrollBy(left - extra, 0);
        } else if (right + extra > this.patternsListView.getMeasuredWidth()) {
            RecyclerListView recyclerListView = this.patternsListView;
            recyclerListView.smoothScrollBy((right + extra) - recyclerListView.getMeasuredWidth(), 0);
        }
    }

    /* renamed from: org.telegram.ui.ThemePreviewActivity$22 */
    /* loaded from: classes4.dex */
    public class AnonymousClass22 implements ColorPicker.ColorPickerDelegate {
        AnonymousClass22() {
            ThemePreviewActivity.this = this$0;
        }

        @Override // org.telegram.ui.Components.ColorPicker.ColorPickerDelegate
        public void setColor(int color, int num, boolean applyNow) {
            if (ThemePreviewActivity.this.screenType == 2) {
                ThemePreviewActivity.this.setBackgroundColor(color, num, applyNow, true);
            } else {
                ThemePreviewActivity.this.scheduleApplyColor(color, num, applyNow);
            }
        }

        @Override // org.telegram.ui.Components.ColorPicker.ColorPickerDelegate
        public void openThemeCreate(boolean share) {
            if (share) {
                if (ThemePreviewActivity.this.accent.info == null) {
                    ThemePreviewActivity.this.finishFragment();
                    MessagesController.getInstance(ThemePreviewActivity.this.currentAccount).saveThemeToServer(ThemePreviewActivity.this.accent.parentTheme, ThemePreviewActivity.this.accent);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShareTheme, ThemePreviewActivity.this.accent.parentTheme, ThemePreviewActivity.this.accent);
                    return;
                }
                String link = "https://" + MessagesController.getInstance(ThemePreviewActivity.this.currentAccount).linkPrefix + "/addtheme/" + ThemePreviewActivity.this.accent.info.slug;
                ThemePreviewActivity.this.showDialog(new ShareAlert(ThemePreviewActivity.this.getParentActivity(), null, link, false, link, false));
                return;
            }
            AlertsCreator.createThemeCreateDialog(ThemePreviewActivity.this, 1, null, null);
        }

        @Override // org.telegram.ui.Components.ColorPicker.ColorPickerDelegate
        public void deleteTheme() {
            if (ThemePreviewActivity.this.getParentActivity() == null) {
                return;
            }
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ThemePreviewActivity.this.getParentActivity());
            builder1.setTitle(LocaleController.getString("DeleteThemeTitle", R.string.DeleteThemeTitle));
            builder1.setMessage(LocaleController.getString("DeleteThemeAlert", R.string.DeleteThemeAlert));
            builder1.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ThemePreviewActivity$22$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ThemePreviewActivity.AnonymousClass22.this.m4657lambda$deleteTheme$0$orgtelegramuiThemePreviewActivity$22(dialogInterface, i);
                }
            });
            builder1.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            AlertDialog alertDialog = builder1.create();
            ThemePreviewActivity.this.showDialog(alertDialog);
            TextView button = (TextView) alertDialog.getButton(-1);
            if (button != null) {
                button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
            }
        }

        /* renamed from: lambda$deleteTheme$0$org-telegram-ui-ThemePreviewActivity$22 */
        public /* synthetic */ void m4657lambda$deleteTheme$0$orgtelegramuiThemePreviewActivity$22(DialogInterface dialogInterface, int i) {
            Theme.deleteThemeAccent(ThemePreviewActivity.this.applyingTheme, ThemePreviewActivity.this.accent, true);
            Theme.applyPreviousTheme();
            Theme.refreshThemeColors();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, ThemePreviewActivity.this.applyingTheme, Boolean.valueOf(ThemePreviewActivity.this.nightTheme), null, -1);
            ThemePreviewActivity.this.finishFragment();
        }

        @Override // org.telegram.ui.Components.ColorPicker.ColorPickerDelegate
        public void rotateColors() {
            ThemePreviewActivity.this.onColorsRotate();
        }

        @Override // org.telegram.ui.Components.ColorPicker.ColorPickerDelegate
        public int getDefaultColor(int num) {
            Theme.ThemeAccent accent;
            if (ThemePreviewActivity.this.colorType != 3 || !ThemePreviewActivity.this.applyingTheme.firstAccentIsDefault || num != 0 || (accent = ThemePreviewActivity.this.applyingTheme.themeAccentsMap.get(Theme.DEFALT_THEME_ACCENT_ID)) == null) {
                return 0;
            }
            return accent.myMessagesAccentColor;
        }

        @Override // org.telegram.ui.Components.ColorPicker.ColorPickerDelegate
        public boolean hasChanges() {
            ThemePreviewActivity themePreviewActivity = ThemePreviewActivity.this;
            return themePreviewActivity.hasChanges(themePreviewActivity.colorType);
        }
    }

    /* renamed from: lambda$createView$12$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4636lambda$createView$12$orgtelegramuiThemePreviewActivity() {
        this.watchForKeyboardEndTime = SystemClock.elapsedRealtime() + 1500;
        this.frameLayout.invalidate();
    }

    /* renamed from: lambda$createView$13$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4637lambda$createView$13$orgtelegramuiThemePreviewActivity(View v) {
        cancelThemeApply(false);
    }

    /* renamed from: lambda$createView$14$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4638lambda$createView$14$orgtelegramuiThemePreviewActivity(View v) {
        Theme.ThemeAccent previousAccent;
        Theme.ThemeInfo previousTheme = Theme.getPreviousTheme();
        if (previousTheme == null) {
            return;
        }
        if (previousTheme != null && previousTheme.prevAccentId >= 0) {
            previousAccent = previousTheme.themeAccentsMap.get(previousTheme.prevAccentId);
        } else {
            previousAccent = previousTheme.getAccent(false);
        }
        if (this.accent == null) {
            this.parentLayout.rebuildAllFragmentViews(false, false);
            Theme.applyThemeFile(new File(this.applyingTheme.pathToFile), this.applyingTheme.name, this.applyingTheme.info, false);
            MessagesController.getInstance(this.applyingTheme.account).saveTheme(this.applyingTheme, null, false, false);
            SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
            editor.putString("lastDayTheme", this.applyingTheme.getKey());
            editor.commit();
        } else {
            saveAccentWallpaper();
            Theme.saveThemeAccents(this.applyingTheme, true, false, false, false);
            Theme.clearPreviousTheme();
            Theme.applyTheme(this.applyingTheme, this.nightTheme);
            this.parentLayout.rebuildAllFragmentViews(false, false);
        }
        finishFragment();
        if (this.screenType == 0) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didApplyNewTheme, previousTheme, previousAccent, Boolean.valueOf(this.deleteOnCancel));
        }
    }

    public void onColorsRotate() {
        if (this.screenType == 2) {
            this.backgroundRotation += 45;
            while (true) {
                int i = this.backgroundRotation;
                if (i >= 360) {
                    this.backgroundRotation = i - 360;
                } else {
                    setBackgroundColor(this.backgroundColor, 0, true, true);
                    return;
                }
            }
        } else {
            this.accent.backgroundRotation += 45;
            while (this.accent.backgroundRotation >= 360) {
                this.accent.backgroundRotation -= 360;
            }
            Theme.refreshThemeColors();
        }
    }

    public void selectColorType(int id) {
        selectColorType(id, true);
    }

    private void selectColorType(int id, boolean ask) {
        int prevType;
        int i;
        int prevType2;
        int count;
        int count2;
        if (getParentActivity() == null || this.colorType == id || this.patternViewAnimation != null) {
            return;
        }
        if (ask && id == 2 && (Theme.hasCustomWallpaper() || this.accent.backgroundOverrideColor == 4294967296L)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("ChangeChatBackground", R.string.ChangeChatBackground));
            if (!Theme.hasCustomWallpaper() || Theme.isCustomWallpaperColor()) {
                builder.setMessage(LocaleController.getString("ChangeColorToColor", R.string.ChangeColorToColor));
                builder.setPositiveButton(LocaleController.getString(TimerBuilder.RESET, R.string.Reset), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda19
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        ThemePreviewActivity.this.m4653lambda$selectColorType$15$orgtelegramuiThemePreviewActivity(dialogInterface, i2);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Continue", R.string.Continue), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda20
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        ThemePreviewActivity.this.m4654lambda$selectColorType$16$orgtelegramuiThemePreviewActivity(dialogInterface, i2);
                    }
                });
            } else {
                builder.setMessage(LocaleController.getString("ChangeWallpaperToColor", R.string.ChangeWallpaperToColor));
                builder.setPositiveButton(LocaleController.getString("Change", R.string.Change), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda21
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        ThemePreviewActivity.this.m4655lambda$selectColorType$17$orgtelegramuiThemePreviewActivity(dialogInterface, i2);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            }
            showDialog(builder.create());
            return;
        }
        int prevType3 = this.colorType;
        this.colorType = id;
        switch (id) {
            case 1:
                prevType = prevType3;
                this.dropDown.setText(LocaleController.getString("ColorPickerMainColor", R.string.ColorPickerMainColor));
                int colorsCount = this.accent.accentColor2 != 0 ? 2 : 1;
                this.colorPicker.setType(1, hasChanges(1), 2, colorsCount, false, 0, false);
                this.colorPicker.setColor(this.accent.accentColor, 0);
                if (this.accent.accentColor2 != 0) {
                    this.colorPicker.setColor(this.accent.accentColor2, 1);
                }
                if (prevType == 2 || (prevType == 3 && this.accent.myMessagesGradientAccentColor2 != 0)) {
                    this.messagesAdapter.notifyItemRemoved(0);
                    break;
                }
                break;
            case 2:
                this.dropDown.setText(LocaleController.getString("ColorPickerBackground", R.string.ColorPickerBackground));
                int defaultBackground = Theme.getColor(Theme.key_chat_wallpaper);
                int defaultGradient1 = Theme.hasThemeKey(Theme.key_chat_wallpaper_gradient_to1) ? Theme.getColor(Theme.key_chat_wallpaper_gradient_to1) : 0;
                int defaultGradient2 = Theme.hasThemeKey(Theme.key_chat_wallpaper_gradient_to2) ? Theme.getColor(Theme.key_chat_wallpaper_gradient_to2) : 0;
                int defaultGradient3 = Theme.hasThemeKey(Theme.key_chat_wallpaper_gradient_to3) ? Theme.getColor(Theme.key_chat_wallpaper_gradient_to3) : 0;
                int backgroundGradientOverrideColor1 = (int) this.accent.backgroundGradientOverrideColor1;
                if (backgroundGradientOverrideColor1 == 0 && this.accent.backgroundGradientOverrideColor1 != 0) {
                    defaultGradient1 = 0;
                }
                int backgroundGradientOverrideColor2 = (int) this.accent.backgroundGradientOverrideColor2;
                if (backgroundGradientOverrideColor2 == 0 && this.accent.backgroundGradientOverrideColor2 != 0) {
                    defaultGradient2 = 0;
                }
                int backgroundGradientOverrideColor3 = (int) this.accent.backgroundGradientOverrideColor3;
                if (backgroundGradientOverrideColor3 == 0) {
                    prevType2 = prevType3;
                    if (this.accent.backgroundGradientOverrideColor3 != 0) {
                        defaultGradient3 = 0;
                    }
                } else {
                    prevType2 = prevType3;
                }
                int backgroundOverrideColor = (int) this.accent.backgroundOverrideColor;
                if (backgroundGradientOverrideColor1 != 0 || defaultGradient1 != 0) {
                    if (backgroundGradientOverrideColor3 != 0 || defaultGradient3 != 0) {
                        count = 4;
                    } else if (backgroundGradientOverrideColor2 != 0 || defaultGradient2 != 0) {
                        count = 3;
                    } else {
                        count = 2;
                    }
                } else {
                    count = 1;
                }
                this.colorPicker.setType(2, hasChanges(2), 4, count, false, this.accent.backgroundRotation, false);
                this.colorPicker.setColor(backgroundGradientOverrideColor3 != 0 ? backgroundGradientOverrideColor3 : defaultGradient3, 3);
                this.colorPicker.setColor(backgroundGradientOverrideColor2 != 0 ? backgroundGradientOverrideColor2 : defaultGradient2, 2);
                this.colorPicker.setColor(backgroundGradientOverrideColor1 != 0 ? backgroundGradientOverrideColor1 : defaultGradient1, 1);
                this.colorPicker.setColor(backgroundOverrideColor != 0 ? backgroundOverrideColor : defaultBackground, 0);
                prevType = prevType2;
                if (prevType == 1 || this.accent.myMessagesGradientAccentColor2 == 0) {
                    this.messagesAdapter.notifyItemInserted(0);
                } else {
                    this.messagesAdapter.notifyItemChanged(0);
                }
                this.listView2.smoothScrollBy(0, AndroidUtilities.dp(60.0f));
                break;
            case 3:
                this.dropDown.setText(LocaleController.getString("ColorPickerMyMessages", R.string.ColorPickerMyMessages));
                if (this.accent.myMessagesGradientAccentColor1 != 0) {
                    if (this.accent.myMessagesGradientAccentColor3 != 0) {
                        count2 = 4;
                    } else if (this.accent.myMessagesGradientAccentColor2 != 0) {
                        count2 = 3;
                    } else {
                        count2 = 2;
                    }
                } else {
                    count2 = 1;
                }
                this.colorPicker.setType(2, hasChanges(3), 4, count2, true, 0, false);
                this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor3, 3);
                this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor2, 2);
                this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor1, 1);
                this.colorPicker.setColor(this.accent.myMessagesAccentColor != 0 ? this.accent.myMessagesAccentColor : this.accent.accentColor, 0);
                this.messagesCheckBoxView[1].setColor(0, this.accent.myMessagesAccentColor);
                this.messagesCheckBoxView[1].setColor(1, this.accent.myMessagesGradientAccentColor1);
                this.messagesCheckBoxView[1].setColor(2, this.accent.myMessagesGradientAccentColor2);
                this.messagesCheckBoxView[1].setColor(3, this.accent.myMessagesGradientAccentColor3);
                if (this.accent.myMessagesGradientAccentColor2 != 0) {
                    if (prevType3 == 1) {
                        this.messagesAdapter.notifyItemInserted(0);
                    } else {
                        this.messagesAdapter.notifyItemChanged(0);
                    }
                } else if (prevType3 == 2) {
                    this.messagesAdapter.notifyItemRemoved(0);
                }
                this.listView2.smoothScrollBy(0, AndroidUtilities.dp(60.0f));
                showAnimationHint();
                prevType = prevType3;
                break;
            default:
                prevType = prevType3;
                break;
        }
        if (id == 1 || id == 3) {
            if (prevType != 2) {
                i = 1;
            } else {
                i = 1;
                if (this.patternLayout[1].getVisibility() == 0) {
                    showPatternsView(0, true, true);
                }
            }
            if (id == i) {
                if (this.applyingTheme.isDark()) {
                    this.colorPicker.setMinBrightness(0.2f);
                    return;
                }
                this.colorPicker.setMinBrightness(0.05f);
                this.colorPicker.setMaxBrightness(0.8f);
                return;
            }
            this.colorPicker.setMinBrightness(0.0f);
            this.colorPicker.setMaxBrightness(1.0f);
            return;
        }
        this.colorPicker.setMinBrightness(0.0f);
        this.colorPicker.setMaxBrightness(1.0f);
    }

    /* renamed from: lambda$selectColorType$15$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4653lambda$selectColorType$15$orgtelegramuiThemePreviewActivity(DialogInterface dialog, int which) {
        if (this.accent.backgroundOverrideColor == 4294967296L) {
            this.accent.backgroundOverrideColor = 0L;
            this.accent.backgroundGradientOverrideColor1 = 0L;
            this.accent.backgroundGradientOverrideColor2 = 0L;
            this.accent.backgroundGradientOverrideColor3 = 0L;
            updatePlayAnimationView(false);
            Theme.refreshThemeColors();
        }
        this.removeBackgroundOverride = true;
        Theme.resetCustomWallpaper(true);
        selectColorType(2, false);
    }

    /* renamed from: lambda$selectColorType$16$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4654lambda$selectColorType$16$orgtelegramuiThemePreviewActivity(DialogInterface dialog, int which) {
        if (Theme.isCustomWallpaperColor()) {
            Theme.ThemeAccent themeAccent = this.accent;
            themeAccent.backgroundOverrideColor = themeAccent.overrideWallpaper.color;
            Theme.ThemeAccent themeAccent2 = this.accent;
            themeAccent2.backgroundGradientOverrideColor1 = themeAccent2.overrideWallpaper.gradientColor1;
            Theme.ThemeAccent themeAccent3 = this.accent;
            themeAccent3.backgroundGradientOverrideColor2 = themeAccent3.overrideWallpaper.gradientColor2;
            Theme.ThemeAccent themeAccent4 = this.accent;
            themeAccent4.backgroundGradientOverrideColor3 = themeAccent4.overrideWallpaper.gradientColor3;
            Theme.ThemeAccent themeAccent5 = this.accent;
            themeAccent5.backgroundRotation = themeAccent5.overrideWallpaper.rotation;
            Theme.ThemeAccent themeAccent6 = this.accent;
            themeAccent6.patternSlug = themeAccent6.overrideWallpaper.slug;
            Theme.ThemeAccent themeAccent7 = this.accent;
            float f = themeAccent7.overrideWallpaper.intensity;
            themeAccent7.patternIntensity = f;
            this.currentIntensity = f;
            if (this.accent.patternSlug != null && !Theme.COLOR_BACKGROUND_SLUG.equals(this.accent.patternSlug)) {
                int a = 0;
                int N = this.patterns.size();
                while (true) {
                    if (a >= N) {
                        break;
                    }
                    TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) this.patterns.get(a);
                    if (!wallPaper.pattern || !this.accent.patternSlug.equals(wallPaper.slug)) {
                        a++;
                    } else {
                        this.selectedPattern = wallPaper;
                        break;
                    }
                }
            } else {
                this.selectedPattern = null;
            }
            this.removeBackgroundOverride = true;
            this.backgroundCheckBoxView[1].setChecked(this.selectedPattern != null, true);
            updatePlayAnimationView(false);
            Theme.refreshThemeColors();
        }
        Drawable background = this.backgroundImage.getBackground();
        if (background instanceof MotionBackgroundDrawable) {
            MotionBackgroundDrawable drawable = (MotionBackgroundDrawable) background;
            drawable.setPatternBitmap(100, null);
            if (Theme.getActiveTheme().isDark()) {
                if (this.currentIntensity < 0.0f) {
                    this.backgroundImage.getImageReceiver().setGradientBitmap(drawable.getBitmap());
                }
                SeekBarView seekBarView = this.intensitySeekBar;
                if (seekBarView != null) {
                    seekBarView.setTwoSided(true);
                }
            } else {
                float f2 = this.currentIntensity;
                if (f2 < 0.0f) {
                    this.currentIntensity = -f2;
                }
            }
        }
        SeekBarView seekBarView2 = this.intensitySeekBar;
        if (seekBarView2 != null) {
            seekBarView2.setProgress(this.currentIntensity);
        }
        Theme.resetCustomWallpaper(true);
        selectColorType(2, false);
    }

    /* renamed from: lambda$selectColorType$17$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4655lambda$selectColorType$17$orgtelegramuiThemePreviewActivity(DialogInterface dialog, int which) {
        if (this.accent.backgroundOverrideColor == 4294967296L) {
            this.accent.backgroundOverrideColor = 0L;
            this.accent.backgroundGradientOverrideColor1 = 0L;
            this.accent.backgroundGradientOverrideColor2 = 0L;
            this.accent.backgroundGradientOverrideColor3 = 0L;
            updatePlayAnimationView(false);
            Theme.refreshThemeColors();
        }
        this.removeBackgroundOverride = true;
        Theme.resetCustomWallpaper(true);
        selectColorType(2, false);
    }

    private void selectPattern(int position) {
        TLRPC.TL_wallPaper wallPaper;
        if (position >= 0 && position < this.patterns.size()) {
            wallPaper = (TLRPC.TL_wallPaper) this.patterns.get(position);
        } else {
            wallPaper = this.lastSelectedPattern;
        }
        if (wallPaper == null) {
            return;
        }
        this.backgroundImage.setImage(ImageLocation.getForDocument(wallPaper.document), this.imageFilter, null, null, "jpg", wallPaper.document.size, 1, wallPaper);
        this.selectedPattern = wallPaper;
        this.isMotion = this.backgroundCheckBoxView[2].isChecked();
        updateButtonState(false, true);
    }

    public void saveAccentWallpaper() {
        Theme.ThemeAccent themeAccent = this.accent;
        if (themeAccent == null || TextUtils.isEmpty(themeAccent.patternSlug)) {
            return;
        }
        try {
            File toFile = this.accent.getPathToWallpaper();
            Drawable background = this.backgroundImage.getBackground();
            Bitmap bitmap = this.backgroundImage.getImageReceiver().getBitmap();
            if (background instanceof MotionBackgroundDrawable) {
                FileOutputStream stream = new FileOutputStream(toFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 87, stream);
                stream.close();
            } else {
                Bitmap dst = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(dst);
                background.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                background.draw(canvas);
                Paint paint = new Paint(2);
                paint.setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
                paint.setAlpha((int) (this.currentIntensity * 255.0f));
                canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
                FileOutputStream stream2 = new FileOutputStream(toFile);
                dst.compress(Bitmap.CompressFormat.JPEG, 87, stream2);
                stream2.close();
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public boolean hasChanges(int type) {
        long backgroundGradientOverrideColorFull;
        int defaultBackgroundGradient;
        int currentGradient;
        if (this.editingTheme) {
            return false;
        }
        if (type == 1 || type == 2) {
            long j = this.backupBackgroundOverrideColor;
            if (j != 0) {
                if (j != this.accent.backgroundOverrideColor) {
                    return true;
                }
            } else {
                int defaultBackground = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper);
                int backgroundOverrideColor = (int) this.accent.backgroundOverrideColor;
                int currentBackground = backgroundOverrideColor == 0 ? defaultBackground : backgroundOverrideColor;
                if (currentBackground != defaultBackground) {
                    return true;
                }
            }
            long j2 = this.backupBackgroundGradientOverrideColor1;
            if (j2 != 0 || this.backupBackgroundGradientOverrideColor2 != 0 || this.backupBackgroundGradientOverrideColor3 != 0) {
                if (j2 != this.accent.backgroundGradientOverrideColor1 || this.backupBackgroundGradientOverrideColor2 != this.accent.backgroundGradientOverrideColor2 || this.backupBackgroundGradientOverrideColor3 != this.accent.backgroundGradientOverrideColor3) {
                    return true;
                }
            } else {
                for (int a = 0; a < 3; a++) {
                    if (a == 0) {
                        defaultBackgroundGradient = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to1);
                        backgroundGradientOverrideColorFull = this.accent.backgroundGradientOverrideColor1;
                    } else if (a == 1) {
                        defaultBackgroundGradient = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to2);
                        backgroundGradientOverrideColorFull = this.accent.backgroundGradientOverrideColor2;
                    } else {
                        defaultBackgroundGradient = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to3);
                        backgroundGradientOverrideColorFull = this.accent.backgroundGradientOverrideColor3;
                    }
                    int backgroundGradientOverrideColor = (int) backgroundGradientOverrideColorFull;
                    if (backgroundGradientOverrideColor == 0 && backgroundGradientOverrideColorFull != 0) {
                        currentGradient = 0;
                    } else {
                        currentGradient = backgroundGradientOverrideColor == 0 ? defaultBackgroundGradient : backgroundGradientOverrideColor;
                    }
                    if (currentGradient != defaultBackgroundGradient) {
                        return true;
                    }
                }
            }
            if (this.accent.backgroundRotation != this.backupBackgroundRotation) {
                return true;
            }
        }
        if (type == 1 || type == 3) {
            if (this.backupAccentColor != this.accent.accentColor2) {
                return true;
            }
            int i = this.backupMyMessagesAccentColor;
            if (i != 0) {
                if (i != this.accent.myMessagesAccentColor) {
                    return true;
                }
            } else if (this.accent.myMessagesAccentColor != 0 && this.accent.myMessagesAccentColor != this.accent.accentColor) {
                return true;
            }
            int i2 = this.backupMyMessagesGradientAccentColor1;
            if (i2 != 0) {
                if (i2 != this.accent.myMessagesGradientAccentColor1) {
                    return true;
                }
            } else if (this.accent.myMessagesGradientAccentColor1 != 0) {
                return true;
            }
            int i3 = this.backupMyMessagesGradientAccentColor2;
            if (i3 != 0) {
                if (i3 != this.accent.myMessagesGradientAccentColor2) {
                    return true;
                }
            } else if (this.accent.myMessagesGradientAccentColor2 != 0) {
                return true;
            }
            int i4 = this.backupMyMessagesGradientAccentColor3;
            if (i4 != 0) {
                if (i4 != this.accent.myMessagesGradientAccentColor3) {
                    return true;
                }
            } else if (this.accent.myMessagesGradientAccentColor3 != 0) {
                return true;
            }
            if (this.backupMyMessagesAnimated != this.accent.myMessagesAnimated) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:39:0x009c, code lost:
        if (r6.accent.patternMotion == r6.isMotion) goto L40;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x00aa, code lost:
        if (r6.accent.patternIntensity == r6.currentIntensity) goto L46;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean checkDiscard() {
        /*
            Method dump skipped, instructions count: 249
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.checkDiscard():boolean");
    }

    /* renamed from: lambda$checkDiscard$18$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4632lambda$checkDiscard$18$orgtelegramuiThemePreviewActivity(DialogInterface dialogInterface, int i) {
        this.actionBar2.getActionBarMenuOnItemClick().onItemClick(4);
    }

    /* renamed from: lambda$checkDiscard$19$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4633lambda$checkDiscard$19$orgtelegramuiThemePreviewActivity(DialogInterface dialog, int which) {
        cancelThemeApply(false);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.invalidateMotionBackground);
        int i = this.screenType;
        if (i == 1 || i == 0) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        }
        int i2 = this.screenType;
        if (i2 == 2 || i2 == 1) {
            Theme.setChangingWallpaper(true);
        }
        if (this.screenType != 0 || this.accent != null) {
            int w = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
            int h = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
            this.imageFilter = ((int) (w / AndroidUtilities.density)) + "_" + ((int) (h / AndroidUtilities.density)) + "_f";
            this.maxWallpaperSize = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersNeedReload);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersDidLoad);
            this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
            if (this.patterns == null) {
                this.patterns = new ArrayList<>();
                MessagesStorage.getInstance(this.currentAccount).getWallpapers();
            }
        } else {
            this.isMotion = Theme.isWallpaperMotion();
        }
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.invalidateMotionBackground);
        FrameLayout frameLayout = this.frameLayout;
        if (frameLayout != null && this.onGlobalLayoutListener != null) {
            frameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
        }
        int i = this.screenType;
        if (i == 2 || i == 1) {
            AndroidUtilities.runOnUIThread(ThemePreviewActivity$$ExternalSyntheticLambda9.INSTANCE);
        }
        int i2 = this.screenType;
        if (i2 == 2) {
            Bitmap bitmap = this.blurredBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.blurredBitmap = null;
            }
            Theme.applyChatServiceMessageColor();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
        } else if (i2 == 1 || i2 == 0) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        }
        if (this.screenType != 0 || this.accent != null) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersNeedReload);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersDidLoad);
        }
        super.onFragmentDestroy();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        super.onTransitionAnimationStart(isOpen, backward);
        if (!isOpen && this.screenType == 2) {
            Theme.applyChatServiceMessageColor();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        DialogsAdapter dialogsAdapter = this.dialogsAdapter;
        if (dialogsAdapter != null) {
            dialogsAdapter.notifyDataSetChanged();
        }
        MessagesAdapter messagesAdapter = this.messagesAdapter;
        if (messagesAdapter != null) {
            messagesAdapter.notifyDataSetChanged();
        }
        if (this.isMotion) {
            this.parallaxEffect.setEnabled(true);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        if (this.isMotion) {
            this.parallaxEffect.setEnabled(false);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isSwipeBackEnabled(MotionEvent event) {
        return false;
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onFailedDownload(String fileName, boolean canceled) {
        updateButtonState(true, canceled);
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onSuccessDownload(String fileName) {
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            radialProgress2.setProgress(1.0f, this.progressVisible);
        }
        updateButtonState(false, true);
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onProgressDownload(String fileName, long downloadedSize, long totalSize) {
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            radialProgress2.setProgress(Math.min(1.0f, ((float) downloadedSize) / ((float) totalSize)), this.progressVisible);
            if (this.radialProgress.getIcon() != 10) {
                updateButtonState(false, true);
            }
        }
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onProgressUpload(String fileName, long uploadedSize, long totalSize, boolean isEncrypted) {
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public int getObserverTag() {
        return this.TAG;
    }

    private void updateBlurred() {
        if (this.isBlurred && this.blurredBitmap == null) {
            Bitmap bitmap = this.currentWallpaperBitmap;
            if (bitmap != null) {
                this.originalBitmap = bitmap;
                this.blurredBitmap = Utilities.blurWallpaper(bitmap);
            } else {
                ImageReceiver imageReceiver = this.backgroundImage.getImageReceiver();
                if (imageReceiver.hasNotThumb() || imageReceiver.hasStaticThumb()) {
                    this.originalBitmap = imageReceiver.getBitmap();
                    this.blurredBitmap = Utilities.blurWallpaper(imageReceiver.getBitmap());
                }
            }
        }
        if (this.isBlurred) {
            Bitmap bitmap2 = this.blurredBitmap;
            if (bitmap2 != null) {
                this.backgroundImage.setImageBitmap(bitmap2);
                return;
            }
            return;
        }
        setCurrentImage(false);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        if (!checkDiscard()) {
            return false;
        }
        cancelThemeApply(true);
        return true;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        TLRPC.TL_wallPaper tL_wallPaper;
        TLRPC.TL_wallPaper tL_wallPaper2;
        if (id == NotificationCenter.emojiLoaded) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView == null) {
                return;
            }
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof DialogCell) {
                    DialogCell cell = (DialogCell) child;
                    cell.update(0);
                }
            }
        } else if (id == NotificationCenter.invalidateMotionBackground) {
            RecyclerListView recyclerListView2 = this.listView2;
            if (recyclerListView2 != null) {
                recyclerListView2.invalidateViews();
            }
        } else if (id == NotificationCenter.didSetNewWallpapper) {
            if (this.page2 != null) {
                setCurrentImage(true);
            }
        } else if (id == NotificationCenter.wallpapersNeedReload) {
            Object obj = this.currentWallpaper;
            if (obj instanceof WallpapersListActivity.FileWallpaper) {
                WallpapersListActivity.FileWallpaper fileWallpaper = (WallpapersListActivity.FileWallpaper) obj;
                if (fileWallpaper.slug == null) {
                    fileWallpaper.slug = (String) args[0];
                }
            }
        } else if (id == NotificationCenter.wallpapersDidLoad) {
            ArrayList<TLRPC.WallPaper> arrayList = (ArrayList) args[0];
            this.patterns.clear();
            this.patternsDict.clear();
            boolean added = false;
            int N = arrayList.size();
            for (int a2 = 0; a2 < N; a2++) {
                TLRPC.WallPaper wallPaper = arrayList.get(a2);
                if ((wallPaper instanceof TLRPC.TL_wallPaper) && wallPaper.pattern) {
                    if (wallPaper.document != null && !this.patternsDict.containsKey(Long.valueOf(wallPaper.document.id))) {
                        this.patterns.add(wallPaper);
                        this.patternsDict.put(Long.valueOf(wallPaper.document.id), wallPaper);
                    }
                    Theme.ThemeAccent themeAccent = this.accent;
                    if (themeAccent != null && themeAccent.patternSlug.equals(wallPaper.slug)) {
                        this.selectedPattern = (TLRPC.TL_wallPaper) wallPaper;
                        added = true;
                        setCurrentImage(false);
                        updateButtonState(false, false);
                    } else if (this.accent == null && (tL_wallPaper2 = this.selectedPattern) != null && tL_wallPaper2.slug.equals(wallPaper.slug)) {
                        added = true;
                    }
                }
            }
            if (!added && (tL_wallPaper = this.selectedPattern) != null) {
                this.patterns.add(0, tL_wallPaper);
            }
            PatternsAdapter patternsAdapter = this.patternsAdapter;
            if (patternsAdapter != null) {
                patternsAdapter.notifyDataSetChanged();
            }
            long acc = 0;
            int N2 = arrayList.size();
            for (int a3 = 0; a3 < N2; a3++) {
                TLRPC.WallPaper wallPaper2 = arrayList.get(a3);
                if (wallPaper2 instanceof TLRPC.TL_wallPaper) {
                    acc = MediaDataController.calcHash(acc, wallPaper2.id);
                }
            }
            TLRPC.TL_account_getWallPapers req = new TLRPC.TL_account_getWallPapers();
            req.hash = acc;
            int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda13
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ThemePreviewActivity.this.m4650xd7bf91d0(tLObject, tL_error);
                }
            });
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, this.classGuid);
        }
    }

    /* renamed from: lambda$didReceivedNotification$24$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4650xd7bf91d0(final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                ThemePreviewActivity.this.m4649x766cf531(response);
            }
        });
    }

    /* renamed from: lambda$didReceivedNotification$23$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4649x766cf531(TLObject response) {
        Theme.ThemeAccent themeAccent;
        TLRPC.TL_wallPaper tL_wallPaper;
        TLRPC.TL_wallPaper tL_wallPaper2;
        if (response instanceof TLRPC.TL_account_wallPapers) {
            TLRPC.TL_account_wallPapers res = (TLRPC.TL_account_wallPapers) response;
            this.patterns.clear();
            this.patternsDict.clear();
            boolean added2 = false;
            int N = res.wallpapers.size();
            for (int a = 0; a < N; a++) {
                if (res.wallpapers.get(a) instanceof TLRPC.TL_wallPaper) {
                    TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) res.wallpapers.get(a);
                    if (wallPaper.pattern) {
                        if (wallPaper.document != null && !this.patternsDict.containsKey(Long.valueOf(wallPaper.document.id))) {
                            this.patterns.add(wallPaper);
                            this.patternsDict.put(Long.valueOf(wallPaper.document.id), wallPaper);
                        }
                        Theme.ThemeAccent themeAccent2 = this.accent;
                        if (themeAccent2 != null && themeAccent2.patternSlug.equals(wallPaper.slug)) {
                            this.selectedPattern = wallPaper;
                            added2 = true;
                            setCurrentImage(false);
                            updateButtonState(false, false);
                        } else if (this.accent == null && (tL_wallPaper2 = this.selectedPattern) != null && tL_wallPaper2.slug.equals(wallPaper.slug)) {
                            added2 = true;
                        }
                    }
                }
            }
            if (!added2 && (tL_wallPaper = this.selectedPattern) != null) {
                this.patterns.add(0, tL_wallPaper);
            }
            PatternsAdapter patternsAdapter = this.patternsAdapter;
            if (patternsAdapter != null) {
                patternsAdapter.notifyDataSetChanged();
            }
            MessagesStorage.getInstance(this.currentAccount).putWallpapers(res.wallpapers, 1);
        }
        if (this.selectedPattern == null && (themeAccent = this.accent) != null && !TextUtils.isEmpty(themeAccent.patternSlug)) {
            TLRPC.TL_account_getWallPaper req2 = new TLRPC.TL_account_getWallPaper();
            TLRPC.TL_inputWallPaperSlug inputWallPaperSlug = new TLRPC.TL_inputWallPaperSlug();
            inputWallPaperSlug.slug = this.accent.patternSlug;
            req2.wallpaper = inputWallPaperSlug;
            int reqId2 = getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda12
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ThemePreviewActivity.this.m4648x151a5892(tLObject, tL_error);
                }
            });
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId2, this.classGuid);
        }
    }

    /* renamed from: lambda$didReceivedNotification$22$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4648x151a5892(final TLObject response1, TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                ThemePreviewActivity.this.m4647xb3c7bbf3(response1);
            }
        });
    }

    /* renamed from: lambda$didReceivedNotification$21$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4647xb3c7bbf3(TLObject response1) {
        if (response1 instanceof TLRPC.TL_wallPaper) {
            TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) response1;
            if (wallPaper.pattern) {
                this.selectedPattern = wallPaper;
                setCurrentImage(false);
                updateButtonState(false, false);
                this.patterns.add(0, this.selectedPattern);
                PatternsAdapter patternsAdapter = this.patternsAdapter;
                if (patternsAdapter != null) {
                    patternsAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public void cancelThemeApply(boolean back) {
        if (this.screenType == 2) {
            if (!back) {
                finishFragment();
                return;
            }
            return;
        }
        Theme.applyPreviousTheme();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        if (this.screenType == 1) {
            if (this.editingTheme) {
                this.accent.accentColor = this.backupAccentColor;
                this.accent.accentColor2 = this.backupAccentColor2;
                this.accent.myMessagesAccentColor = this.backupMyMessagesAccentColor;
                this.accent.myMessagesGradientAccentColor1 = this.backupMyMessagesGradientAccentColor1;
                this.accent.myMessagesGradientAccentColor2 = this.backupMyMessagesGradientAccentColor2;
                this.accent.myMessagesGradientAccentColor3 = this.backupMyMessagesGradientAccentColor3;
                this.accent.myMessagesAnimated = this.backupMyMessagesAnimated;
                this.accent.backgroundOverrideColor = this.backupBackgroundOverrideColor;
                this.accent.backgroundGradientOverrideColor1 = this.backupBackgroundGradientOverrideColor1;
                this.accent.backgroundGradientOverrideColor2 = this.backupBackgroundGradientOverrideColor2;
                this.accent.backgroundGradientOverrideColor3 = this.backupBackgroundGradientOverrideColor3;
                this.accent.backgroundRotation = this.backupBackgroundRotation;
                this.accent.patternSlug = this.backupSlug;
                this.accent.patternIntensity = this.backupIntensity;
            }
            Theme.saveThemeAccents(this.applyingTheme, false, true, false, false);
        } else {
            if (this.accent != null) {
                Theme.saveThemeAccents(this.applyingTheme, false, this.deleteOnCancel, false, false);
            }
            this.parentLayout.rebuildAllFragmentViews(false, false);
            if (this.deleteOnCancel && this.applyingTheme.pathToFile != null && !Theme.isThemeInstalled(this.applyingTheme)) {
                new File(this.applyingTheme.pathToFile).delete();
            }
        }
        if (!back) {
            finishFragment();
        }
    }

    public int getButtonsColor(String key) {
        return this.useDefaultThemeForButtons ? Theme.getDefaultColor(key) : Theme.getColor(key);
    }

    public void scheduleApplyColor(int color, int num, boolean applyNow) {
        if (num == -1) {
            int i = this.colorType;
            if (i == 1 || i == 2) {
                long j = this.backupBackgroundOverrideColor;
                if (j != 0) {
                    this.accent.backgroundOverrideColor = j;
                } else {
                    this.accent.backgroundOverrideColor = 0L;
                }
                long j2 = this.backupBackgroundGradientOverrideColor1;
                if (j2 != 0) {
                    this.accent.backgroundGradientOverrideColor1 = j2;
                } else {
                    this.accent.backgroundGradientOverrideColor1 = 0L;
                }
                long j3 = this.backupBackgroundGradientOverrideColor2;
                if (j3 != 0) {
                    this.accent.backgroundGradientOverrideColor2 = j3;
                } else {
                    this.accent.backgroundGradientOverrideColor2 = 0L;
                }
                long j4 = this.backupBackgroundGradientOverrideColor3;
                if (j4 != 0) {
                    this.accent.backgroundGradientOverrideColor3 = j4;
                } else {
                    this.accent.backgroundGradientOverrideColor3 = 0L;
                }
                this.accent.backgroundRotation = this.backupBackgroundRotation;
                if (this.colorType == 2) {
                    int defaultBackground = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper);
                    int defaultBackgroundGradient1 = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to1);
                    int defaultBackgroundGradient2 = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to2);
                    int defaultBackgroundGradient3 = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to3);
                    int backgroundGradientOverrideColor1 = (int) this.accent.backgroundGradientOverrideColor1;
                    int backgroundGradientOverrideColor2 = (int) this.accent.backgroundGradientOverrideColor2;
                    int backgroundGradientOverrideColor3 = (int) this.accent.backgroundGradientOverrideColor3;
                    int backgroundOverrideColor = (int) this.accent.backgroundOverrideColor;
                    this.colorPicker.setColor(backgroundGradientOverrideColor3 != 0 ? backgroundGradientOverrideColor3 : defaultBackgroundGradient3, 3);
                    this.colorPicker.setColor(backgroundGradientOverrideColor2 != 0 ? backgroundGradientOverrideColor2 : defaultBackgroundGradient2, 2);
                    this.colorPicker.setColor(backgroundGradientOverrideColor1 != 0 ? backgroundGradientOverrideColor1 : defaultBackgroundGradient1, 1);
                    this.colorPicker.setColor(backgroundOverrideColor != 0 ? backgroundOverrideColor : defaultBackground, 0);
                }
            }
            int defaultBackground2 = this.colorType;
            if (defaultBackground2 == 1 || defaultBackground2 == 3) {
                int i2 = this.backupMyMessagesAccentColor;
                if (i2 != 0) {
                    this.accent.myMessagesAccentColor = i2;
                } else {
                    this.accent.myMessagesAccentColor = 0;
                }
                int i3 = this.backupMyMessagesGradientAccentColor1;
                if (i3 != 0) {
                    this.accent.myMessagesGradientAccentColor1 = i3;
                } else {
                    this.accent.myMessagesGradientAccentColor1 = 0;
                }
                int i4 = this.backupMyMessagesGradientAccentColor2;
                if (i4 != 0) {
                    this.accent.myMessagesGradientAccentColor2 = i4;
                } else {
                    this.accent.myMessagesGradientAccentColor2 = 0;
                }
                int i5 = this.backupMyMessagesGradientAccentColor3;
                if (i5 != 0) {
                    this.accent.myMessagesGradientAccentColor3 = i5;
                } else {
                    this.accent.myMessagesGradientAccentColor3 = 0;
                }
                if (this.colorType == 3) {
                    this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor3, 3);
                    this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor2, 2);
                    this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor1, 1);
                    this.colorPicker.setColor(this.accent.myMessagesAccentColor != 0 ? this.accent.myMessagesAccentColor : this.accent.accentColor, 0);
                }
            }
            Theme.refreshThemeColors();
            this.listView2.invalidateViews();
            return;
        }
        int i6 = this.lastPickedColorNum;
        if (i6 != -1 && i6 != num) {
            this.applyColorAction.run();
        }
        this.lastPickedColor = color;
        this.lastPickedColorNum = num;
        if (applyNow) {
            this.applyColorAction.run();
        } else if (!this.applyColorScheduled) {
            this.applyColorScheduled = true;
            this.fragmentView.postDelayed(this.applyColorAction, 16L);
        }
    }

    private void applyColor(int color, int num) {
        int i = this.colorType;
        if (i == 1) {
            if (num == 0) {
                this.accent.accentColor = color;
                Theme.refreshThemeColors();
            } else if (num == 1) {
                this.accent.accentColor2 = color;
                Theme.refreshThemeColors(true, true);
                this.listView2.invalidateViews();
                this.colorPicker.setHasChanges(hasChanges(this.colorType));
                updatePlayAnimationView(true);
            }
        } else if (i == 2) {
            if (this.lastPickedColorNum == 0) {
                this.accent.backgroundOverrideColor = color;
            } else if (num == 1) {
                int defaultGradientColor = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to1);
                if (color == 0 && defaultGradientColor != 0) {
                    this.accent.backgroundGradientOverrideColor1 = 4294967296L;
                } else {
                    this.accent.backgroundGradientOverrideColor1 = color;
                }
            } else if (num == 2) {
                int defaultGradientColor2 = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to2);
                if (color == 0 && defaultGradientColor2 != 0) {
                    this.accent.backgroundGradientOverrideColor2 = 4294967296L;
                } else {
                    this.accent.backgroundGradientOverrideColor2 = color;
                }
            } else if (num == 3) {
                int defaultGradientColor3 = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to3);
                if (color == 0 && defaultGradientColor3 != 0) {
                    this.accent.backgroundGradientOverrideColor3 = 4294967296L;
                } else {
                    this.accent.backgroundGradientOverrideColor3 = color;
                }
            }
            Theme.refreshThemeColors(true, false);
            this.colorPicker.setHasChanges(hasChanges(this.colorType));
            updatePlayAnimationView(true);
        } else if (i == 3) {
            int i2 = this.lastPickedColorNum;
            if (i2 == 0) {
                this.accent.myMessagesAccentColor = color;
            } else if (i2 == 1) {
                this.accent.myMessagesGradientAccentColor1 = color;
            } else if (i2 == 2) {
                int prevColor = this.accent.myMessagesGradientAccentColor2;
                this.accent.myMessagesGradientAccentColor2 = color;
                if (prevColor != 0 && color == 0) {
                    this.messagesAdapter.notifyItemRemoved(0);
                } else if (prevColor == 0 && color != 0) {
                    this.messagesAdapter.notifyItemInserted(0);
                    showAnimationHint();
                }
            } else {
                this.accent.myMessagesGradientAccentColor3 = color;
            }
            int i3 = this.lastPickedColorNum;
            if (i3 >= 0) {
                this.messagesCheckBoxView[1].setColor(i3, color);
            }
            Theme.refreshThemeColors(true, true);
            this.listView2.invalidateViews();
            this.colorPicker.setHasChanges(hasChanges(this.colorType));
            updatePlayAnimationView(true);
        }
        int size = this.themeDescriptions.size();
        for (int i4 = 0; i4 < size; i4++) {
            ThemeDescription description = this.themeDescriptions.get(i4);
            description.setColor(Theme.getColor(description.getCurrentKey()), false, false);
        }
        this.listView.invalidateViews();
        this.listView2.invalidateViews();
        View view = this.dotsContainer;
        if (view != null) {
            view.invalidate();
        }
    }

    private void updateButtonState(boolean ifSame, boolean animated) {
        Object object;
        long size;
        String fileName;
        File path;
        FrameLayout frameLayout;
        if (this.selectedPattern != null) {
            object = this.selectedPattern;
        } else {
            object = this.currentWallpaper;
        }
        if ((object instanceof TLRPC.TL_wallPaper) || (object instanceof MediaController.SearchImage)) {
            if (animated && !this.progressVisible) {
                animated = false;
            }
            if (object instanceof TLRPC.TL_wallPaper) {
                TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) object;
                fileName = FileLoader.getAttachFileName(wallPaper.document);
                if (TextUtils.isEmpty(fileName)) {
                    return;
                }
                path = FileLoader.getInstance(this.currentAccount).getPathToAttach(wallPaper.document, true);
                size = wallPaper.document.size;
            } else {
                MediaController.SearchImage wallPaper2 = (MediaController.SearchImage) object;
                if (wallPaper2.photo != null) {
                    TLRPC.PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(wallPaper2.photo.sizes, this.maxWallpaperSize, true);
                    path = FileLoader.getInstance(this.currentAccount).getPathToAttach(photoSize, true);
                    fileName = FileLoader.getAttachFileName(photoSize);
                    size = photoSize.size;
                } else {
                    path = ImageLoader.getHttpFilePath(wallPaper2.imageUrl, "jpg");
                    fileName = path.getName();
                    size = wallPaper2.size;
                }
                if (TextUtils.isEmpty(fileName)) {
                    return;
                }
            }
            boolean fileExists = path.exists();
            float f = 1.0f;
            if (fileExists) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                RadialProgress2 radialProgress2 = this.radialProgress;
                if (radialProgress2 != null) {
                    radialProgress2.setProgress(1.0f, animated);
                    this.radialProgress.setIcon(4, ifSame, animated);
                }
                this.backgroundImage.invalidate();
                if (this.screenType == 2) {
                    if (size != 0) {
                        this.actionBar2.setSubtitle(AndroidUtilities.formatFileSize(size));
                    } else {
                        this.actionBar2.setSubtitle(null);
                    }
                }
            } else {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, null, this);
                if (this.radialProgress != null) {
                    FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName);
                    Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                    if (progress != null) {
                        this.radialProgress.setProgress(progress.floatValue(), animated);
                    } else {
                        this.radialProgress.setProgress(0.0f, animated);
                    }
                    this.radialProgress.setIcon(10, ifSame, animated);
                }
                if (this.screenType == 2) {
                    this.actionBar2.setSubtitle(LocaleController.getString("LoadingFullImage", R.string.LoadingFullImage));
                }
                this.backgroundImage.invalidate();
            }
            if (this.selectedPattern == null && (frameLayout = this.backgroundButtonsContainer) != null) {
                frameLayout.setAlpha(fileExists ? 1.0f : 0.5f);
            }
            int i = this.screenType;
            if (i == 0) {
                this.doneButton.setEnabled(fileExists);
                TextView textView = this.doneButton;
                if (!fileExists) {
                    f = 0.5f;
                }
                textView.setAlpha(f);
                return;
            } else if (i == 2) {
                this.bottomOverlayChat.setEnabled(fileExists);
                TextView textView2 = this.bottomOverlayChatText;
                if (!fileExists) {
                    f = 0.5f;
                }
                textView2.setAlpha(f);
                return;
            } else {
                this.saveItem.setEnabled(fileExists);
                ActionBarMenuItem actionBarMenuItem = this.saveItem;
                if (!fileExists) {
                    f = 0.5f;
                }
                actionBarMenuItem.setAlpha(f);
                return;
            }
        }
        RadialProgress2 radialProgress22 = this.radialProgress;
        if (radialProgress22 != null) {
            radialProgress22.setIcon(4, ifSame, animated);
        }
    }

    public void setDelegate(WallpaperActivityDelegate wallpaperActivityDelegate) {
        this.delegate = wallpaperActivityDelegate;
    }

    public void setPatterns(ArrayList<Object> arrayList) {
        this.patterns = arrayList;
        if (this.screenType == 1 || (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
            WallpapersListActivity.ColorWallpaper wallPaper = (WallpapersListActivity.ColorWallpaper) this.currentWallpaper;
            if (wallPaper.patternId != 0) {
                int a = 0;
                int N = this.patterns.size();
                while (true) {
                    if (a >= N) {
                        break;
                    }
                    TLRPC.TL_wallPaper pattern = (TLRPC.TL_wallPaper) this.patterns.get(a);
                    if (pattern.id != wallPaper.patternId) {
                        a++;
                    } else {
                        this.selectedPattern = pattern;
                        break;
                    }
                }
                this.currentIntensity = wallPaper.intensity;
            }
        }
    }

    private void showAnimationHint() {
        if (this.page2 == null || this.messagesCheckBoxView == null || this.accent.myMessagesGradientAccentColor2 == 0) {
            return;
        }
        final SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        if (preferences.getBoolean("bganimationhint", false)) {
            return;
        }
        if (this.animationHint == null) {
            HintView hintView = new HintView(getParentActivity(), 8);
            this.animationHint = hintView;
            hintView.setShowingDuration(DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            this.animationHint.setAlpha(0.0f);
            this.animationHint.setVisibility(4);
            this.animationHint.setText(LocaleController.getString("BackgroundAnimateInfo", R.string.BackgroundAnimateInfo));
            this.animationHint.setExtraTranslationY(AndroidUtilities.dp(6.0f));
            this.frameLayout.addView(this.animationHint, LayoutHelper.createFrame(-2, -2.0f, 51, 10.0f, 0.0f, 10.0f, 0.0f));
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                ThemePreviewActivity.this.m4656lambda$showAnimationHint$25$orgtelegramuiThemePreviewActivity(preferences);
            }
        }, 500L);
    }

    /* renamed from: lambda$showAnimationHint$25$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4656lambda$showAnimationHint$25$orgtelegramuiThemePreviewActivity(SharedPreferences preferences) {
        if (this.colorType != 3) {
            return;
        }
        preferences.edit().putBoolean("bganimationhint", true).commit();
        this.animationHint.showForView(this.messagesCheckBoxView[0], true);
    }

    private void updateSelectedPattern(boolean animated) {
        int count = this.patternsListView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = this.patternsListView.getChildAt(a);
            if (child instanceof PatternCell) {
                ((PatternCell) child).updateSelected(animated);
            }
        }
    }

    private void updateMotionButton() {
        int i = this.screenType;
        float f = 1.0f;
        float f2 = 0.0f;
        if (i == 1 || i == 2) {
            if (this.selectedPattern == null && (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
                this.backgroundCheckBoxView[2].setChecked(false, true);
            }
            this.backgroundCheckBoxView[this.selectedPattern != null ? (char) 2 : (char) 0].setVisibility(0);
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[2];
            WallpaperCheckBoxView wallpaperCheckBoxView = this.backgroundCheckBoxView[2];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = this.selectedPattern != null ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(wallpaperCheckBoxView, property, fArr);
            WallpaperCheckBoxView wallpaperCheckBoxView2 = this.backgroundCheckBoxView[0];
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (this.selectedPattern != null) {
                f = 0.0f;
            }
            fArr2[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(wallpaperCheckBoxView2, property2, fArr2);
            animatorSet.playTogether(animatorArr);
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ThemePreviewActivity.27
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    ThemePreviewActivity.this.backgroundCheckBoxView[ThemePreviewActivity.this.selectedPattern != null ? (char) 0 : (char) 2].setVisibility(4);
                }
            });
            animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet.setDuration(200L);
            animatorSet.start();
            return;
        }
        boolean isEnabled = this.backgroundCheckBoxView[0].isEnabled();
        TLRPC.TL_wallPaper tL_wallPaper = this.selectedPattern;
        if (isEnabled == (tL_wallPaper != null)) {
            return;
        }
        if (tL_wallPaper == null) {
            this.backgroundCheckBoxView[0].setChecked(false, true);
        }
        this.backgroundCheckBoxView[0].setEnabled(this.selectedPattern != null);
        if (this.selectedPattern != null) {
            this.backgroundCheckBoxView[0].setVisibility(0);
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.backgroundCheckBoxView[1].getLayoutParams();
        AnimatorSet animatorSet2 = new AnimatorSet();
        int offset = (layoutParams.width + AndroidUtilities.dp(9.0f)) / 2;
        Animator[] animatorArr2 = new Animator[1];
        WallpaperCheckBoxView wallpaperCheckBoxView3 = this.backgroundCheckBoxView[0];
        Property property3 = View.ALPHA;
        float[] fArr3 = new float[1];
        if (this.selectedPattern == null) {
            f = 0.0f;
        }
        fArr3[0] = f;
        animatorArr2[0] = ObjectAnimator.ofFloat(wallpaperCheckBoxView3, property3, fArr3);
        animatorSet2.playTogether(animatorArr2);
        Animator[] animatorArr3 = new Animator[1];
        WallpaperCheckBoxView wallpaperCheckBoxView4 = this.backgroundCheckBoxView[0];
        Property property4 = View.TRANSLATION_X;
        float[] fArr4 = new float[1];
        fArr4[0] = this.selectedPattern != null ? 0.0f : offset;
        animatorArr3[0] = ObjectAnimator.ofFloat(wallpaperCheckBoxView4, property4, fArr4);
        animatorSet2.playTogether(animatorArr3);
        Animator[] animatorArr4 = new Animator[1];
        WallpaperCheckBoxView wallpaperCheckBoxView5 = this.backgroundCheckBoxView[1];
        Property property5 = View.TRANSLATION_X;
        float[] fArr5 = new float[1];
        if (this.selectedPattern == null) {
            f2 = -offset;
        }
        fArr5[0] = f2;
        animatorArr4[0] = ObjectAnimator.ofFloat(wallpaperCheckBoxView5, property5, fArr5);
        animatorSet2.playTogether(animatorArr4);
        animatorSet2.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        animatorSet2.setDuration(200L);
        animatorSet2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ThemePreviewActivity.28
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (ThemePreviewActivity.this.selectedPattern == null) {
                    ThemePreviewActivity.this.backgroundCheckBoxView[0].setVisibility(4);
                }
            }
        });
        animatorSet2.start();
    }

    public void showPatternsView(final int num, final boolean show, boolean animated) {
        int index;
        int count;
        char c = 0;
        final boolean showMotion = show && num == 1 && this.selectedPattern != null;
        if (show) {
            if (num != 0) {
                this.previousSelectedPattern = this.selectedPattern;
                this.previousIntensity = this.currentIntensity;
                this.patternsAdapter.notifyDataSetChanged();
                ArrayList<Object> arrayList = this.patterns;
                if (arrayList != null) {
                    TLRPC.TL_wallPaper tL_wallPaper = this.selectedPattern;
                    if (tL_wallPaper != null) {
                        index = arrayList.indexOf(tL_wallPaper) + (this.screenType == 2 ? 1 : 0);
                    } else {
                        index = 0;
                    }
                    this.patternsLayoutManager.scrollToPositionWithOffset(index, (this.patternsListView.getMeasuredWidth() - AndroidUtilities.dp(124.0f)) / 2);
                }
            } else if (this.screenType == 2) {
                this.previousBackgroundColor = this.backgroundColor;
                int count2 = this.backgroundGradientColor1;
                this.previousBackgroundGradientColor1 = count2;
                int i = this.backgroundGradientColor2;
                this.previousBackgroundGradientColor2 = i;
                int i2 = this.backgroundGradientColor3;
                this.previousBackgroundGradientColor3 = i2;
                int i3 = this.backupBackgroundRotation;
                this.previousBackgroundRotation = i3;
                if (i2 != 0) {
                    count = 4;
                } else if (i != 0) {
                    count = 3;
                } else if (count2 != 0) {
                    count = 2;
                } else {
                    count = 1;
                }
                this.colorPicker.setType(0, false, 4, count, false, i3, false);
                this.colorPicker.setColor(this.backgroundGradientColor3, 3);
                this.colorPicker.setColor(this.backgroundGradientColor2, 2);
                this.colorPicker.setColor(this.backgroundGradientColor1, 1);
                this.colorPicker.setColor(this.backgroundColor, 0);
            }
        }
        int index2 = this.screenType;
        if (index2 == 1 || index2 == 2) {
            this.backgroundCheckBoxView[showMotion ? (char) 2 : (char) 0].setVisibility(0);
        }
        if (num == 1 && !this.intensitySeekBar.isTwoSided()) {
            float f = this.currentIntensity;
            if (f < 0.0f) {
                float f2 = -f;
                this.currentIntensity = f2;
                this.intensitySeekBar.setProgress(f2);
            }
        }
        float f3 = 1.0f;
        if (animated) {
            this.patternViewAnimation = new AnimatorSet();
            ArrayList<Animator> animators = new ArrayList<>();
            int otherNum = num == 0 ? 1 : 0;
            if (show) {
                this.patternLayout[num].setVisibility(0);
                int i4 = this.screenType;
                if (i4 == 1) {
                    RecyclerListView recyclerListView = this.listView2;
                    Property property = View.TRANSLATION_Y;
                    float[] fArr = new float[1];
                    fArr[0] = num == 1 ? -AndroidUtilities.dp(21.0f) : 0.0f;
                    animators.add(ObjectAnimator.ofFloat(recyclerListView, property, fArr));
                    WallpaperCheckBoxView wallpaperCheckBoxView = this.backgroundCheckBoxView[2];
                    Property property2 = View.ALPHA;
                    float[] fArr2 = new float[1];
                    fArr2[0] = showMotion ? 1.0f : 0.0f;
                    animators.add(ObjectAnimator.ofFloat(wallpaperCheckBoxView, property2, fArr2));
                    WallpaperCheckBoxView wallpaperCheckBoxView2 = this.backgroundCheckBoxView[0];
                    Property property3 = View.ALPHA;
                    float[] fArr3 = new float[1];
                    fArr3[0] = showMotion ? 0.0f : 1.0f;
                    animators.add(ObjectAnimator.ofFloat(wallpaperCheckBoxView2, property3, fArr3));
                    if (num == 1) {
                        animators.add(ObjectAnimator.ofFloat(this.patternLayout[num], View.ALPHA, 0.0f, 1.0f));
                    } else {
                        this.patternLayout[num].setAlpha(1.0f);
                        animators.add(ObjectAnimator.ofFloat(this.patternLayout[otherNum], View.ALPHA, 0.0f));
                    }
                    this.colorPicker.hideKeyboard();
                } else if (i4 == 2) {
                    animators.add(ObjectAnimator.ofFloat(this.listView2, View.TRANSLATION_Y, (-this.patternLayout[num].getMeasuredHeight()) + AndroidUtilities.dp(48.0f)));
                    WallpaperCheckBoxView wallpaperCheckBoxView3 = this.backgroundCheckBoxView[2];
                    Property property4 = View.ALPHA;
                    float[] fArr4 = new float[1];
                    fArr4[0] = showMotion ? 1.0f : 0.0f;
                    animators.add(ObjectAnimator.ofFloat(wallpaperCheckBoxView3, property4, fArr4));
                    WallpaperCheckBoxView wallpaperCheckBoxView4 = this.backgroundCheckBoxView[0];
                    Property property5 = View.ALPHA;
                    float[] fArr5 = new float[1];
                    if (showMotion) {
                        f3 = 0.0f;
                    }
                    fArr5[0] = f3;
                    animators.add(ObjectAnimator.ofFloat(wallpaperCheckBoxView4, property5, fArr5));
                    animators.add(ObjectAnimator.ofFloat(this.backgroundImage, View.ALPHA, 0.0f));
                    if (this.patternLayout[otherNum].getVisibility() == 0) {
                        animators.add(ObjectAnimator.ofFloat(this.patternLayout[otherNum], View.ALPHA, 0.0f));
                        animators.add(ObjectAnimator.ofFloat(this.patternLayout[num], View.ALPHA, 0.0f, 1.0f));
                        this.patternLayout[num].setTranslationY(0.0f);
                    } else {
                        animators.add(ObjectAnimator.ofFloat(this.patternLayout[num], View.TRANSLATION_Y, this.patternLayout[num].getMeasuredHeight(), 0.0f));
                    }
                } else {
                    if (num == 1) {
                        animators.add(ObjectAnimator.ofFloat(this.patternLayout[num], View.ALPHA, 0.0f, 1.0f));
                    } else {
                        this.patternLayout[num].setAlpha(1.0f);
                        animators.add(ObjectAnimator.ofFloat(this.patternLayout[otherNum], View.ALPHA, 0.0f));
                    }
                    this.colorPicker.hideKeyboard();
                }
            } else {
                animators.add(ObjectAnimator.ofFloat(this.listView2, View.TRANSLATION_Y, 0.0f));
                animators.add(ObjectAnimator.ofFloat(this.patternLayout[num], View.TRANSLATION_Y, this.patternLayout[num].getMeasuredHeight()));
                animators.add(ObjectAnimator.ofFloat(this.backgroundCheckBoxView[0], View.ALPHA, 1.0f));
                animators.add(ObjectAnimator.ofFloat(this.backgroundCheckBoxView[2], View.ALPHA, 0.0f));
                animators.add(ObjectAnimator.ofFloat(this.backgroundImage, View.ALPHA, 1.0f));
            }
            this.patternViewAnimation.playTogether(animators);
            final int i5 = otherNum;
            this.patternViewAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ThemePreviewActivity.29
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    ThemePreviewActivity.this.patternViewAnimation = null;
                    if (show && ThemePreviewActivity.this.patternLayout[i5].getVisibility() == 0) {
                        ThemePreviewActivity.this.patternLayout[i5].setAlpha(1.0f);
                        ThemePreviewActivity.this.patternLayout[i5].setVisibility(4);
                    } else if (!show) {
                        ThemePreviewActivity.this.patternLayout[num].setVisibility(4);
                    }
                    char c2 = 2;
                    if (ThemePreviewActivity.this.screenType == 1 || ThemePreviewActivity.this.screenType == 2) {
                        WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = ThemePreviewActivity.this.backgroundCheckBoxView;
                        if (showMotion) {
                            c2 = 0;
                        }
                        wallpaperCheckBoxViewArr[c2].setVisibility(4);
                    } else if (num == 1) {
                        ThemePreviewActivity.this.patternLayout[i5].setAlpha(0.0f);
                    }
                }
            });
            this.patternViewAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.patternViewAnimation.setDuration(200L);
            this.patternViewAnimation.start();
            return;
        }
        int otherNum2 = num == 0 ? 1 : 0;
        if (!show) {
            this.listView2.setTranslationY(0.0f);
            FrameLayout[] frameLayoutArr = this.patternLayout;
            frameLayoutArr[num].setTranslationY(frameLayoutArr[num].getMeasuredHeight());
            this.backgroundCheckBoxView[0].setAlpha(1.0f);
            this.backgroundCheckBoxView[2].setAlpha(1.0f);
            this.backgroundImage.setAlpha(1.0f);
        } else {
            this.patternLayout[num].setVisibility(0);
            int i6 = this.screenType;
            if (i6 == 1) {
                this.listView2.setTranslationY(num == 1 ? -AndroidUtilities.dp(21.0f) : 0.0f);
                this.backgroundCheckBoxView[2].setAlpha(showMotion ? 1.0f : 0.0f);
                this.backgroundCheckBoxView[0].setAlpha(showMotion ? 0.0f : 1.0f);
                if (num == 1) {
                    this.patternLayout[num].setAlpha(1.0f);
                } else {
                    this.patternLayout[num].setAlpha(1.0f);
                    this.patternLayout[otherNum2].setAlpha(0.0f);
                }
                this.colorPicker.hideKeyboard();
            } else if (i6 == 2) {
                this.listView2.setTranslationY((-AndroidUtilities.dp(num == 0 ? 343.0f : 316.0f)) + AndroidUtilities.dp(48.0f));
                this.backgroundCheckBoxView[2].setAlpha(showMotion ? 1.0f : 0.0f);
                this.backgroundCheckBoxView[0].setAlpha(showMotion ? 0.0f : 1.0f);
                this.backgroundImage.setAlpha(0.0f);
                if (this.patternLayout[otherNum2].getVisibility() == 0) {
                    this.patternLayout[otherNum2].setAlpha(0.0f);
                    this.patternLayout[num].setAlpha(1.0f);
                    this.patternLayout[num].setTranslationY(0.0f);
                } else {
                    this.patternLayout[num].setTranslationY(0.0f);
                }
            } else {
                if (num == 1) {
                    this.patternLayout[num].setAlpha(1.0f);
                } else {
                    this.patternLayout[num].setAlpha(1.0f);
                    this.patternLayout[otherNum2].setAlpha(0.0f);
                }
                this.colorPicker.hideKeyboard();
            }
        }
        if (show && this.patternLayout[otherNum2].getVisibility() == 0) {
            this.patternLayout[otherNum2].setAlpha(1.0f);
            this.patternLayout[otherNum2].setVisibility(4);
        } else if (!show) {
            this.patternLayout[num].setVisibility(4);
        }
        int i7 = this.screenType;
        if (i7 == 1 || i7 == 2) {
            WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = this.backgroundCheckBoxView;
            if (!showMotion) {
                c = 2;
            }
            wallpaperCheckBoxViewArr[c].setVisibility(4);
        } else if (num == 1) {
            this.patternLayout[otherNum2].setAlpha(0.0f);
        }
    }

    private void animateMotionChange() {
        AnimatorSet animatorSet = this.motionAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.motionAnimation = animatorSet2;
        if (this.isMotion) {
            animatorSet2.playTogether(ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_X, this.parallaxScale), ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_Y, this.parallaxScale));
        } else {
            animatorSet2.playTogether(ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.backgroundImage, View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(this.backgroundImage, View.TRANSLATION_Y, 0.0f));
        }
        this.motionAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.motionAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ThemePreviewActivity.30
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                ThemePreviewActivity.this.motionAnimation = null;
            }
        });
        this.motionAnimation.start();
    }

    private void updatePlayAnimationView(boolean animated) {
        boolean visible;
        int color1;
        Integer num = 1;
        if (Build.VERSION.SDK_INT >= 29) {
            int color2 = 0;
            int i = this.screenType;
            if (i == 0) {
                Theme.ThemeAccent themeAccent = this.accent;
                if (themeAccent != null) {
                    color2 = (int) themeAccent.backgroundGradientOverrideColor2;
                } else {
                    color2 = Theme.getColor(Theme.key_chat_wallpaper_gradient_to2);
                }
            } else if (i == 1) {
                int defaultBackgroundGradient2 = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to2);
                int backgroundGradientOverrideColor2 = (int) this.accent.backgroundGradientOverrideColor2;
                if (backgroundGradientOverrideColor2 == 0 && this.accent.backgroundGradientOverrideColor2 != 0) {
                    color2 = 0;
                } else {
                    color2 = backgroundGradientOverrideColor2 != 0 ? backgroundGradientOverrideColor2 : defaultBackgroundGradient2;
                }
            } else {
                Object obj = this.currentWallpaper;
                if (obj instanceof WallpapersListActivity.ColorWallpaper) {
                    WallpapersListActivity.ColorWallpaper colorWallpaper = (WallpapersListActivity.ColorWallpaper) obj;
                    color2 = this.backgroundGradientColor2;
                }
            }
            if (color2 != 0 && this.currentIntensity >= 0.0f) {
                this.backgroundImage.getImageReceiver().setBlendMode(BlendMode.SOFT_LIGHT);
            } else {
                this.backgroundImage.getImageReceiver().setBlendMode(null);
            }
        }
        if (this.backgroundPlayAnimationView != null) {
            int i2 = this.screenType;
            if (i2 == 2) {
                visible = this.backgroundGradientColor1 != 0;
            } else if (i2 == 1) {
                int defaultBackgroundGradient1 = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to1);
                int backgroundGradientOverrideColor1 = (int) this.accent.backgroundGradientOverrideColor1;
                if (backgroundGradientOverrideColor1 == 0 && this.accent.backgroundGradientOverrideColor1 != 0) {
                    color1 = 0;
                } else {
                    color1 = backgroundGradientOverrideColor1 != 0 ? backgroundGradientOverrideColor1 : defaultBackgroundGradient1;
                }
                visible = color1 != 0;
            } else {
                visible = false;
            }
            boolean wasVisible = this.backgroundPlayAnimationView.getTag() != null;
            this.backgroundPlayAnimationView.setTag(visible ? num : null);
            if (wasVisible != visible) {
                if (visible) {
                    this.backgroundPlayAnimationView.setVisibility(0);
                }
                AnimatorSet animatorSet = this.backgroundPlayViewAnimator;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                if (animated) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.backgroundPlayViewAnimator = animatorSet2;
                    Animator[] animatorArr = new Animator[6];
                    FrameLayout frameLayout = this.backgroundPlayAnimationView;
                    Property property = View.ALPHA;
                    float[] fArr = new float[1];
                    fArr[0] = visible ? 1.0f : 0.0f;
                    animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
                    FrameLayout frameLayout2 = this.backgroundPlayAnimationView;
                    Property property2 = View.SCALE_X;
                    float[] fArr2 = new float[1];
                    fArr2[0] = visible ? 1.0f : 0.0f;
                    animatorArr[1] = ObjectAnimator.ofFloat(frameLayout2, property2, fArr2);
                    FrameLayout frameLayout3 = this.backgroundPlayAnimationView;
                    Property property3 = View.SCALE_Y;
                    float[] fArr3 = new float[1];
                    fArr3[0] = visible ? 1.0f : 0.0f;
                    animatorArr[2] = ObjectAnimator.ofFloat(frameLayout3, property3, fArr3);
                    WallpaperCheckBoxView wallpaperCheckBoxView = this.backgroundCheckBoxView[0];
                    Property property4 = View.TRANSLATION_X;
                    float[] fArr4 = new float[1];
                    fArr4[0] = visible ? AndroidUtilities.dp(34.0f) : 0.0f;
                    animatorArr[3] = ObjectAnimator.ofFloat(wallpaperCheckBoxView, property4, fArr4);
                    WallpaperCheckBoxView wallpaperCheckBoxView2 = this.backgroundCheckBoxView[1];
                    Property property5 = View.TRANSLATION_X;
                    float[] fArr5 = new float[1];
                    fArr5[0] = visible ? -AndroidUtilities.dp(34.0f) : 0.0f;
                    animatorArr[4] = ObjectAnimator.ofFloat(wallpaperCheckBoxView2, property5, fArr5);
                    WallpaperCheckBoxView wallpaperCheckBoxView3 = this.backgroundCheckBoxView[2];
                    Property property6 = View.TRANSLATION_X;
                    float[] fArr6 = new float[1];
                    fArr6[0] = visible ? AndroidUtilities.dp(34.0f) : 0.0f;
                    animatorArr[5] = ObjectAnimator.ofFloat(wallpaperCheckBoxView3, property6, fArr6);
                    animatorSet2.playTogether(animatorArr);
                    this.backgroundPlayViewAnimator.setDuration(180L);
                    this.backgroundPlayViewAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ThemePreviewActivity.31
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            if (ThemePreviewActivity.this.backgroundPlayAnimationView.getTag() == null) {
                                ThemePreviewActivity.this.backgroundPlayAnimationView.setVisibility(4);
                            }
                            ThemePreviewActivity.this.backgroundPlayViewAnimator = null;
                        }
                    });
                    this.backgroundPlayViewAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.backgroundPlayViewAnimator.start();
                } else {
                    this.backgroundPlayAnimationView.setAlpha(visible ? 1.0f : 0.0f);
                    this.backgroundPlayAnimationView.setScaleX(visible ? 1.0f : 0.0f);
                    this.backgroundPlayAnimationView.setScaleY(visible ? 1.0f : 0.0f);
                    this.backgroundCheckBoxView[0].setTranslationX(visible ? AndroidUtilities.dp(34.0f) : 0.0f);
                    this.backgroundCheckBoxView[1].setTranslationX(visible ? -AndroidUtilities.dp(34.0f) : 0.0f);
                    this.backgroundCheckBoxView[2].setTranslationX(visible ? AndroidUtilities.dp(34.0f) : 0.0f);
                }
            }
        }
        FrameLayout frameLayout4 = this.messagesPlayAnimationView;
        if (frameLayout4 != null) {
            boolean wasVisible2 = frameLayout4.getTag() != null;
            FrameLayout frameLayout5 = this.messagesPlayAnimationView;
            if (1 == 0) {
                num = null;
            }
            frameLayout5.setTag(num);
            if (!wasVisible2) {
                if (1 != 0) {
                    this.messagesPlayAnimationView.setVisibility(0);
                }
                AnimatorSet animatorSet3 = this.messagesPlayViewAnimator;
                if (animatorSet3 != null) {
                    animatorSet3.cancel();
                }
                if (!animated) {
                    this.messagesPlayAnimationView.setAlpha(1 != 0 ? 1.0f : 0.0f);
                    this.messagesPlayAnimationView.setScaleX(1 != 0 ? 1.0f : 0.0f);
                    this.messagesPlayAnimationView.setScaleY(1 != 0 ? 1.0f : 0.0f);
                    this.messagesCheckBoxView[0].setTranslationX(1 != 0 ? -AndroidUtilities.dp(34.0f) : 0.0f);
                    this.messagesCheckBoxView[1].setTranslationX(1 != 0 ? AndroidUtilities.dp(34.0f) : 0.0f);
                    return;
                }
                AnimatorSet animatorSet4 = new AnimatorSet();
                this.messagesPlayViewAnimator = animatorSet4;
                Animator[] animatorArr2 = new Animator[5];
                FrameLayout frameLayout6 = this.messagesPlayAnimationView;
                Property property7 = View.ALPHA;
                float[] fArr7 = new float[1];
                fArr7[0] = 1 != 0 ? 1.0f : 0.0f;
                animatorArr2[0] = ObjectAnimator.ofFloat(frameLayout6, property7, fArr7);
                FrameLayout frameLayout7 = this.messagesPlayAnimationView;
                Property property8 = View.SCALE_X;
                float[] fArr8 = new float[1];
                fArr8[0] = 1 != 0 ? 1.0f : 0.0f;
                animatorArr2[1] = ObjectAnimator.ofFloat(frameLayout7, property8, fArr8);
                FrameLayout frameLayout8 = this.messagesPlayAnimationView;
                Property property9 = View.SCALE_Y;
                float[] fArr9 = new float[1];
                fArr9[0] = 1 != 0 ? 1.0f : 0.0f;
                animatorArr2[2] = ObjectAnimator.ofFloat(frameLayout8, property9, fArr9);
                WallpaperCheckBoxView wallpaperCheckBoxView4 = this.messagesCheckBoxView[0];
                Property property10 = View.TRANSLATION_X;
                float[] fArr10 = new float[1];
                fArr10[0] = 1 != 0 ? -AndroidUtilities.dp(34.0f) : 0.0f;
                animatorArr2[3] = ObjectAnimator.ofFloat(wallpaperCheckBoxView4, property10, fArr10);
                WallpaperCheckBoxView wallpaperCheckBoxView5 = this.messagesCheckBoxView[1];
                Property property11 = View.TRANSLATION_X;
                float[] fArr11 = new float[1];
                fArr11[0] = 1 != 0 ? AndroidUtilities.dp(34.0f) : 0.0f;
                animatorArr2[4] = ObjectAnimator.ofFloat(wallpaperCheckBoxView5, property11, fArr11);
                animatorSet4.playTogether(animatorArr2);
                this.messagesPlayViewAnimator.setDuration(180L);
                this.messagesPlayViewAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ThemePreviewActivity.32
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (ThemePreviewActivity.this.messagesPlayAnimationView.getTag() == null) {
                            ThemePreviewActivity.this.messagesPlayAnimationView.setVisibility(4);
                        }
                        ThemePreviewActivity.this.messagesPlayViewAnimator = null;
                    }
                });
                this.messagesPlayViewAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.messagesPlayViewAnimator.start();
            }
        }
    }

    public void setBackgroundColor(int color, int num, boolean applyNow, boolean animated) {
        MotionBackgroundDrawable motionBackgroundDrawable;
        if (num == 0) {
            this.backgroundColor = color;
        } else if (num == 1) {
            this.backgroundGradientColor1 = color;
        } else if (num == 2) {
            this.backgroundGradientColor2 = color;
        } else if (num == 3) {
            this.backgroundGradientColor3 = color;
        }
        updatePlayAnimationView(animated);
        if (this.backgroundCheckBoxView != null) {
            int a = 0;
            while (true) {
                WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = this.backgroundCheckBoxView;
                if (a >= wallpaperCheckBoxViewArr.length) {
                    break;
                }
                if (wallpaperCheckBoxViewArr[a] != null) {
                    wallpaperCheckBoxViewArr[a].setColor(num, color);
                }
                a++;
            }
        }
        int a2 = this.backgroundGradientColor2;
        if (a2 != 0) {
            if (this.intensitySeekBar != null && Theme.getActiveTheme().isDark()) {
                this.intensitySeekBar.setTwoSided(true);
            }
            Drawable currentBackground = this.backgroundImage.getBackground();
            if (currentBackground instanceof MotionBackgroundDrawable) {
                motionBackgroundDrawable = (MotionBackgroundDrawable) currentBackground;
            } else {
                motionBackgroundDrawable = new MotionBackgroundDrawable();
                motionBackgroundDrawable.setParentView(this.backgroundImage);
                if (this.rotatePreview) {
                    motionBackgroundDrawable.rotatePreview(false);
                }
            }
            motionBackgroundDrawable.setColors(this.backgroundColor, this.backgroundGradientColor1, this.backgroundGradientColor2, this.backgroundGradientColor3);
            this.backgroundImage.setBackground(motionBackgroundDrawable);
            this.patternColor = motionBackgroundDrawable.getPatternColor();
            this.checkColor = 754974720;
        } else if (this.backgroundGradientColor1 != 0) {
            GradientDrawable gradientDrawable = new GradientDrawable(BackgroundGradientDrawable.getGradientOrientation(this.backgroundRotation), new int[]{this.backgroundColor, this.backgroundGradientColor1});
            this.backgroundImage.setBackground(gradientDrawable);
            int patternColor = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(this.backgroundColor, this.backgroundGradientColor1));
            this.checkColor = patternColor;
            this.patternColor = patternColor;
        } else {
            this.backgroundImage.setBackgroundColor(this.backgroundColor);
            int patternColor2 = AndroidUtilities.getPatternColor(this.backgroundColor);
            this.checkColor = patternColor2;
            this.patternColor = patternColor2;
        }
        if (!Theme.hasThemeKey(Theme.key_chat_serviceBackground) || (this.backgroundImage.getBackground() instanceof MotionBackgroundDrawable)) {
            int i = this.checkColor;
            Theme.applyChatServiceMessageColor(new int[]{i, i, i, i}, this.backgroundImage.getBackground());
        } else if (Theme.getCachedWallpaperNonBlocking() instanceof MotionBackgroundDrawable) {
            int c = Theme.getColor(Theme.key_chat_serviceBackground);
            Theme.applyChatServiceMessageColor(new int[]{c, c, c, c}, this.backgroundImage.getBackground());
        }
        ImageView imageView = this.backgroundPlayAnimationImageView;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_serviceText), PorterDuff.Mode.MULTIPLY));
        }
        ImageView imageView2 = this.messagesPlayAnimationImageView;
        if (imageView2 != null) {
            imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_serviceText), PorterDuff.Mode.MULTIPLY));
        }
        BackupImageView backupImageView = this.backgroundImage;
        if (backupImageView != null) {
            backupImageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
            this.backgroundImage.getImageReceiver().setAlpha(Math.abs(this.currentIntensity));
            this.backgroundImage.invalidate();
            if (Theme.getActiveTheme().isDark() && (this.backgroundImage.getBackground() instanceof MotionBackgroundDrawable)) {
                SeekBarView seekBarView = this.intensitySeekBar;
                if (seekBarView != null) {
                    seekBarView.setTwoSided(true);
                }
                if (this.currentIntensity < 0.0f) {
                    this.backgroundImage.getImageReceiver().setGradientBitmap(((MotionBackgroundDrawable) this.backgroundImage.getBackground()).getBitmap());
                }
            } else {
                this.backgroundImage.getImageReceiver().setGradientBitmap(null);
                SeekBarView seekBarView2 = this.intensitySeekBar;
                if (seekBarView2 != null) {
                    seekBarView2.setTwoSided(false);
                }
            }
            SeekBarView seekBarView3 = this.intensitySeekBar;
            if (seekBarView3 != null) {
                seekBarView3.setProgress(this.currentIntensity);
            }
        }
        RecyclerListView recyclerListView = this.listView2;
        if (recyclerListView != null) {
            recyclerListView.invalidateViews();
        }
        FrameLayout frameLayout = this.backgroundButtonsContainer;
        if (frameLayout != null) {
            int N = frameLayout.getChildCount();
            for (int a3 = 0; a3 < N; a3++) {
                this.backgroundButtonsContainer.getChildAt(a3).invalidate();
            }
        }
        FrameLayout frameLayout2 = this.messagesButtonsContainer;
        if (frameLayout2 != null) {
            int N2 = frameLayout2.getChildCount();
            for (int a4 = 0; a4 < N2; a4++) {
                this.messagesButtonsContainer.getChildAt(a4).invalidate();
            }
        }
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            radialProgress2.setColors(Theme.key_chat_serviceBackground, Theme.key_chat_serviceBackground, Theme.key_chat_serviceText, Theme.key_chat_serviceText);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:113:0x02b5  */
    /* JADX WARN: Removed duplicated region for block: B:118:0x02c8  */
    /* JADX WARN: Removed duplicated region for block: B:119:0x02d1  */
    /* JADX WARN: Removed duplicated region for block: B:124:0x02ee  */
    /* JADX WARN: Removed duplicated region for block: B:142:0x036a  */
    /* JADX WARN: Removed duplicated region for block: B:148:0x0397  */
    /* JADX WARN: Removed duplicated region for block: B:151:0x03a9  */
    /* JADX WARN: Removed duplicated region for block: B:154:0x03bb  */
    /* JADX WARN: Removed duplicated region for block: B:159:0x03d2  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void setCurrentImage(boolean r35) {
        /*
            Method dump skipped, instructions count: 1001
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.setCurrentImage(boolean):void");
    }

    /* loaded from: classes4.dex */
    public static class DialogsAdapter extends RecyclerListView.SelectionAdapter {
        private ArrayList<DialogCell.CustomDialog> dialogs = new ArrayList<>();
        private Context mContext;

        public DialogsAdapter(Context context) {
            this.mContext = context;
            int date = (int) (System.currentTimeMillis() / 1000);
            DialogCell.CustomDialog customDialog = new DialogCell.CustomDialog();
            customDialog.name = LocaleController.getString("ThemePreviewDialog1", R.string.ThemePreviewDialog1);
            customDialog.message = LocaleController.getString("ThemePreviewDialogMessage1", R.string.ThemePreviewDialogMessage1);
            customDialog.id = 0;
            customDialog.unread_count = 0;
            customDialog.pinned = true;
            customDialog.muted = false;
            customDialog.type = 0;
            customDialog.date = date;
            customDialog.verified = false;
            customDialog.isMedia = false;
            customDialog.sent = true;
            this.dialogs.add(customDialog);
            DialogCell.CustomDialog customDialog2 = new DialogCell.CustomDialog();
            customDialog2.name = LocaleController.getString("ThemePreviewDialog2", R.string.ThemePreviewDialog2);
            customDialog2.message = LocaleController.getString("ThemePreviewDialogMessage2", R.string.ThemePreviewDialogMessage2);
            customDialog2.id = 1;
            customDialog2.unread_count = 2;
            customDialog2.pinned = false;
            customDialog2.muted = false;
            customDialog2.type = 0;
            customDialog2.date = date - 3600;
            customDialog2.verified = false;
            customDialog2.isMedia = false;
            customDialog2.sent = false;
            this.dialogs.add(customDialog2);
            DialogCell.CustomDialog customDialog3 = new DialogCell.CustomDialog();
            customDialog3.name = LocaleController.getString("ThemePreviewDialog3", R.string.ThemePreviewDialog3);
            customDialog3.message = LocaleController.getString("ThemePreviewDialogMessage3", R.string.ThemePreviewDialogMessage3);
            customDialog3.id = 2;
            customDialog3.unread_count = 3;
            customDialog3.pinned = false;
            customDialog3.muted = true;
            customDialog3.type = 0;
            customDialog3.date = date - 7200;
            customDialog3.verified = false;
            customDialog3.isMedia = true;
            customDialog3.sent = false;
            this.dialogs.add(customDialog3);
            DialogCell.CustomDialog customDialog4 = new DialogCell.CustomDialog();
            customDialog4.name = LocaleController.getString("ThemePreviewDialog4", R.string.ThemePreviewDialog4);
            customDialog4.message = LocaleController.getString("ThemePreviewDialogMessage4", R.string.ThemePreviewDialogMessage4);
            customDialog4.id = 3;
            customDialog4.unread_count = 0;
            customDialog4.pinned = false;
            customDialog4.muted = false;
            customDialog4.type = 2;
            customDialog4.date = date - 10800;
            customDialog4.verified = false;
            customDialog4.isMedia = false;
            customDialog4.sent = false;
            this.dialogs.add(customDialog4);
            DialogCell.CustomDialog customDialog5 = new DialogCell.CustomDialog();
            customDialog5.name = LocaleController.getString("ThemePreviewDialog5", R.string.ThemePreviewDialog5);
            customDialog5.message = LocaleController.getString("ThemePreviewDialogMessage5", R.string.ThemePreviewDialogMessage5);
            customDialog5.id = 4;
            customDialog5.unread_count = 0;
            customDialog5.pinned = false;
            customDialog5.muted = false;
            customDialog5.type = 1;
            customDialog5.date = date - 14400;
            customDialog5.verified = false;
            customDialog5.isMedia = false;
            customDialog5.sent = true;
            this.dialogs.add(customDialog5);
            DialogCell.CustomDialog customDialog6 = new DialogCell.CustomDialog();
            customDialog6.name = LocaleController.getString("ThemePreviewDialog6", R.string.ThemePreviewDialog6);
            customDialog6.message = LocaleController.getString("ThemePreviewDialogMessage6", R.string.ThemePreviewDialogMessage6);
            customDialog6.id = 5;
            customDialog6.unread_count = 0;
            customDialog6.pinned = false;
            customDialog6.muted = false;
            customDialog6.type = 0;
            customDialog6.date = date - 18000;
            customDialog6.verified = false;
            customDialog6.isMedia = false;
            customDialog6.sent = false;
            this.dialogs.add(customDialog6);
            DialogCell.CustomDialog customDialog7 = new DialogCell.CustomDialog();
            customDialog7.name = LocaleController.getString("ThemePreviewDialog7", R.string.ThemePreviewDialog7);
            customDialog7.message = LocaleController.getString("ThemePreviewDialogMessage7", R.string.ThemePreviewDialogMessage7);
            customDialog7.id = 6;
            customDialog7.unread_count = 0;
            customDialog7.pinned = false;
            customDialog7.muted = false;
            customDialog7.type = 0;
            customDialog7.date = date - 21600;
            customDialog7.verified = true;
            customDialog7.isMedia = false;
            customDialog7.sent = false;
            this.dialogs.add(customDialog7);
            DialogCell.CustomDialog customDialog8 = new DialogCell.CustomDialog();
            customDialog8.name = LocaleController.getString("ThemePreviewDialog8", R.string.ThemePreviewDialog8);
            customDialog8.message = LocaleController.getString("ThemePreviewDialogMessage8", R.string.ThemePreviewDialogMessage8);
            customDialog8.id = 0;
            customDialog8.unread_count = 0;
            customDialog8.pinned = false;
            customDialog8.muted = false;
            customDialog8.type = 0;
            customDialog8.date = date - 25200;
            customDialog8.verified = true;
            customDialog8.isMedia = false;
            customDialog8.sent = false;
            this.dialogs.add(customDialog8);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.dialogs.size();
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != 1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view;
            if (viewType == 0) {
                view = new DialogCell(null, this.mContext, false, false);
            } else {
                view = new LoadingCell(this.mContext);
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                DialogCell cell = (DialogCell) viewHolder.itemView;
                boolean z = true;
                if (i == getItemCount() - 1) {
                    z = false;
                }
                cell.useSeparator = z;
                cell.setDialog(this.dialogs.get(i));
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == this.dialogs.size()) {
                return 1;
            }
            return 0;
        }
    }

    /* loaded from: classes4.dex */
    public class MessagesAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private ArrayList<MessageObject> messages;
        private boolean showSecretMessages;

        public MessagesAdapter(Context context) {
            ThemePreviewActivity.this = this$0;
            this.showSecretMessages = this$0.screenType == 0 && Utilities.random.nextInt(100) <= 1;
            this.mContext = context;
            this.messages = new ArrayList<>();
            int date = ((int) (System.currentTimeMillis() / 1000)) - 3600;
            if (this$0.screenType != 2) {
                if (this$0.screenType == 1) {
                    TLRPC.Message message = new TLRPC.TL_message();
                    message.media = new TLRPC.TL_messageMediaDocument();
                    message.media.document = new TLRPC.TL_document();
                    message.media.document.mime_type = "audio/mp3";
                    message.media.document.file_reference = new byte[0];
                    message.media.document.id = -2147483648L;
                    message.media.document.size = 2621440L;
                    message.media.document.dc_id = Integer.MIN_VALUE;
                    TLRPC.TL_documentAttributeFilename attributeFilename = new TLRPC.TL_documentAttributeFilename();
                    attributeFilename.file_name = LocaleController.getString("NewThemePreviewReply2", R.string.NewThemePreviewReply2) + DefaultHlsExtractorFactory.MP3_FILE_EXTENSION;
                    message.media.document.attributes.add(attributeFilename);
                    message.date = date + 60;
                    message.dialog_id = 1L;
                    message.flags = 259;
                    message.from_id = new TLRPC.TL_peerUser();
                    message.from_id.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                    message.id = 1;
                    message.out = true;
                    message.peer_id = new TLRPC.TL_peerUser();
                    message.peer_id.user_id = 0L;
                    MessageObject replyMessageObject = new MessageObject(UserConfig.selectedAccount, message, true, false);
                    if (BuildVars.DEBUG_PRIVATE_VERSION) {
                        TLRPC.Message message2 = new TLRPC.TL_message();
                        message2.message = "this is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text";
                        message2.date = date + 960;
                        message2.dialog_id = 1L;
                        message2.flags = 259;
                        message2.from_id = new TLRPC.TL_peerUser();
                        message2.from_id.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                        message2.id = 1;
                        message2.media = new TLRPC.TL_messageMediaEmpty();
                        message2.out = true;
                        message2.peer_id = new TLRPC.TL_peerUser();
                        message2.peer_id.user_id = 0L;
                        MessageObject message1 = new MessageObject(UserConfig.selectedAccount, message2, true, false);
                        message1.resetLayout();
                        message1.eventId = 1L;
                        this.messages.add(message1);
                    }
                    TLRPC.Message message3 = new TLRPC.TL_message();
                    String text = LocaleController.getString("NewThemePreviewLine3", R.string.NewThemePreviewLine3);
                    StringBuilder builder = new StringBuilder(text);
                    int index1 = text.indexOf(42);
                    int index2 = text.lastIndexOf(42);
                    if (index1 != -1 && index2 != -1) {
                        builder.replace(index2, index2 + 1, "");
                        builder.replace(index1, index1 + 1, "");
                        TLRPC.TL_messageEntityTextUrl entityUrl = new TLRPC.TL_messageEntityTextUrl();
                        entityUrl.offset = index1;
                        entityUrl.length = (index2 - index1) - 1;
                        entityUrl.url = "https://telegram.org";
                        message3.entities.add(entityUrl);
                    }
                    message3.message = builder.toString();
                    message3.date = date + 960;
                    message3.dialog_id = 1L;
                    message3.flags = 259;
                    message3.from_id = new TLRPC.TL_peerUser();
                    message3.from_id.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                    message3.id = 1;
                    message3.media = new TLRPC.TL_messageMediaEmpty();
                    message3.out = true;
                    message3.peer_id = new TLRPC.TL_peerUser();
                    message3.peer_id.user_id = 0L;
                    MessageObject message12 = new MessageObject(UserConfig.selectedAccount, message3, true, false);
                    message12.resetLayout();
                    message12.eventId = 1L;
                    this.messages.add(message12);
                    TLRPC.Message message4 = new TLRPC.TL_message();
                    message4.message = LocaleController.getString("NewThemePreviewLine1", R.string.NewThemePreviewLine1);
                    message4.date = date + 60;
                    message4.dialog_id = 1L;
                    message4.flags = 265;
                    message4.from_id = new TLRPC.TL_peerUser();
                    message4.id = 1;
                    message4.reply_to = new TLRPC.TL_messageReplyHeader();
                    message4.reply_to.reply_to_msg_id = 5;
                    message4.media = new TLRPC.TL_messageMediaEmpty();
                    message4.out = false;
                    message4.peer_id = new TLRPC.TL_peerUser();
                    message4.peer_id.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                    MessageObject message22 = new MessageObject(UserConfig.selectedAccount, message4, true, false);
                    message22.customReplyName = LocaleController.getString("NewThemePreviewName", R.string.NewThemePreviewName);
                    message12.customReplyName = "Test User";
                    message22.eventId = 1L;
                    message22.resetLayout();
                    message22.replyMessageObject = replyMessageObject;
                    message12.replyMessageObject = message22;
                    this.messages.add(message22);
                    this.messages.add(replyMessageObject);
                    TLRPC.Message message5 = new TLRPC.TL_message();
                    message5.date = date + 120;
                    message5.dialog_id = 1L;
                    message5.flags = 259;
                    message5.out = false;
                    message5.from_id = new TLRPC.TL_peerUser();
                    message5.id = 1;
                    message5.media = new TLRPC.TL_messageMediaDocument();
                    message5.media.flags |= 3;
                    message5.media.document = new TLRPC.TL_document();
                    message5.media.document.mime_type = "audio/ogg";
                    message5.media.document.file_reference = new byte[0];
                    TLRPC.TL_documentAttributeAudio audio = new TLRPC.TL_documentAttributeAudio();
                    audio.flags = 1028;
                    audio.duration = 3;
                    audio.voice = true;
                    audio.waveform = new byte[]{0, 4, 17, -50, -93, 86, -103, -45, -12, -26, 63, -25, -3, 109, -114, -54, -4, -1, -1, -1, -1, -29, -1, -1, -25, -1, -1, -97, -43, 57, -57, -108, 1, -91, -4, -47, 21, 99, 10, 97, 43, 45, 115, -112, -77, 51, -63, 66, 40, 34, -122, -116, 48, -124, 16, 66, -120, 16, 68, 16, 33, 4, 1};
                    message5.media.document.attributes.add(audio);
                    message5.out = true;
                    message5.peer_id = new TLRPC.TL_peerUser();
                    message5.peer_id.user_id = 0L;
                    MessageObject messageObject = new MessageObject(this$0.currentAccount, message5, true, false);
                    messageObject.audioProgressSec = 1;
                    messageObject.audioProgress = 0.3f;
                    messageObject.useCustomPhoto = true;
                    this.messages.add(messageObject);
                    return;
                } else if (this.showSecretMessages) {
                    TLRPC.TL_user user1 = new TLRPC.TL_user();
                    user1.id = 2147483647L;
                    user1.first_name = "Me";
                    TLRPC.TL_user user2 = new TLRPC.TL_user();
                    user2.id = 2147483646L;
                    user2.first_name = "Serj";
                    ArrayList<TLRPC.User> users = new ArrayList<>();
                    users.add(user1);
                    users.add(user2);
                    MessagesController.getInstance(this$0.currentAccount).putUsers(users, true);
                    TLRPC.Message message6 = new TLRPC.TL_message();
                    message6.message = "Guess why Half-Life 3 was never released.";
                    message6.date = date + 960;
                    message6.dialog_id = -1L;
                    message6.flags = 259;
                    message6.id = 2147483646;
                    message6.media = new TLRPC.TL_messageMediaEmpty();
                    message6.out = false;
                    message6.peer_id = new TLRPC.TL_peerChat();
                    message6.peer_id.chat_id = 1L;
                    message6.from_id = new TLRPC.TL_peerUser();
                    message6.from_id.user_id = user2.id;
                    this.messages.add(new MessageObject(this$0.currentAccount, message6, true, false));
                    TLRPC.Message message7 = new TLRPC.TL_message();
                    message7.message = "No.\nAnd every unnecessary ping of the dev delays the release for 10 days.\nEvery request for ETA delays the release for 2 weeks.";
                    message7.date = date + 960;
                    message7.dialog_id = -1L;
                    message7.flags = 259;
                    message7.id = 1;
                    message7.media = new TLRPC.TL_messageMediaEmpty();
                    message7.out = false;
                    message7.peer_id = new TLRPC.TL_peerChat();
                    message7.peer_id.chat_id = 1L;
                    message7.from_id = new TLRPC.TL_peerUser();
                    message7.from_id.user_id = user2.id;
                    this.messages.add(new MessageObject(this$0.currentAccount, message7, true, false));
                    TLRPC.Message message8 = new TLRPC.TL_message();
                    message8.message = "Is source code for Android coming anytime soon?";
                    message8.date = date + 600;
                    message8.dialog_id = -1L;
                    message8.flags = 259;
                    message8.id = 1;
                    message8.media = new TLRPC.TL_messageMediaEmpty();
                    message8.out = false;
                    message8.peer_id = new TLRPC.TL_peerChat();
                    message8.peer_id.chat_id = 1L;
                    message8.from_id = new TLRPC.TL_peerUser();
                    message8.from_id.user_id = user1.id;
                    this.messages.add(new MessageObject(this$0.currentAccount, message8, true, false));
                    return;
                } else {
                    TLRPC.Message message9 = new TLRPC.TL_message();
                    message9.message = LocaleController.getString("ThemePreviewLine1", R.string.ThemePreviewLine1);
                    message9.date = date + 60;
                    message9.dialog_id = 1L;
                    message9.flags = 259;
                    message9.from_id = new TLRPC.TL_peerUser();
                    message9.from_id.user_id = UserConfig.getInstance(this$0.currentAccount).getClientUserId();
                    message9.id = 1;
                    message9.media = new TLRPC.TL_messageMediaEmpty();
                    message9.out = true;
                    message9.peer_id = new TLRPC.TL_peerUser();
                    message9.peer_id.user_id = 0L;
                    MessageObject replyMessageObject2 = new MessageObject(this$0.currentAccount, message9, true, false);
                    TLRPC.Message message10 = new TLRPC.TL_message();
                    message10.message = LocaleController.getString("ThemePreviewLine2", R.string.ThemePreviewLine2);
                    message10.date = date + 960;
                    message10.dialog_id = 1L;
                    message10.flags = 259;
                    message10.from_id = new TLRPC.TL_peerUser();
                    message10.from_id.user_id = UserConfig.getInstance(this$0.currentAccount).getClientUserId();
                    message10.id = 1;
                    message10.media = new TLRPC.TL_messageMediaEmpty();
                    message10.out = true;
                    message10.peer_id = new TLRPC.TL_peerUser();
                    message10.peer_id.user_id = 0L;
                    this.messages.add(new MessageObject(this$0.currentAccount, message10, true, false));
                    TLRPC.Message message11 = new TLRPC.TL_message();
                    message11.date = date + TsExtractor.TS_STREAM_TYPE_HDMV_DTS;
                    message11.dialog_id = 1L;
                    message11.flags = 259;
                    message11.from_id = new TLRPC.TL_peerUser();
                    message11.id = 5;
                    message11.media = new TLRPC.TL_messageMediaDocument();
                    message11.media.flags |= 3;
                    message11.media.document = new TLRPC.TL_document();
                    message11.media.document.mime_type = MimeTypes.AUDIO_MP4;
                    message11.media.document.file_reference = new byte[0];
                    TLRPC.TL_documentAttributeAudio audio2 = new TLRPC.TL_documentAttributeAudio();
                    audio2.duration = 243;
                    audio2.performer = LocaleController.getString("ThemePreviewSongPerformer", R.string.ThemePreviewSongPerformer);
                    audio2.title = LocaleController.getString("ThemePreviewSongTitle", R.string.ThemePreviewSongTitle);
                    message11.media.document.attributes.add(audio2);
                    message11.out = false;
                    message11.peer_id = new TLRPC.TL_peerUser();
                    message11.peer_id.user_id = UserConfig.getInstance(this$0.currentAccount).getClientUserId();
                    this.messages.add(new MessageObject(this$0.currentAccount, message11, true, false));
                    TLRPC.Message message13 = new TLRPC.TL_message();
                    message13.message = LocaleController.getString("ThemePreviewLine3", R.string.ThemePreviewLine3);
                    message13.date = date + 60;
                    message13.dialog_id = 1L;
                    message13.flags = 265;
                    message13.from_id = new TLRPC.TL_peerUser();
                    message13.id = 1;
                    message13.reply_to = new TLRPC.TL_messageReplyHeader();
                    message13.reply_to.reply_to_msg_id = 5;
                    message13.media = new TLRPC.TL_messageMediaEmpty();
                    message13.out = false;
                    message13.peer_id = new TLRPC.TL_peerUser();
                    message13.peer_id.user_id = UserConfig.getInstance(this$0.currentAccount).getClientUserId();
                    MessageObject messageObject2 = new MessageObject(this$0.currentAccount, message13, true, false);
                    messageObject2.customReplyName = LocaleController.getString("ThemePreviewLine3Reply", R.string.ThemePreviewLine3Reply);
                    messageObject2.replyMessageObject = replyMessageObject2;
                    this.messages.add(messageObject2);
                    TLRPC.Message message14 = new TLRPC.TL_message();
                    message14.date = date + 120;
                    message14.dialog_id = 1L;
                    message14.flags = 259;
                    message14.from_id = new TLRPC.TL_peerUser();
                    message14.from_id.user_id = UserConfig.getInstance(this$0.currentAccount).getClientUserId();
                    message14.id = 1;
                    message14.media = new TLRPC.TL_messageMediaDocument();
                    message14.media.flags |= 3;
                    message14.media.document = new TLRPC.TL_document();
                    message14.media.document.mime_type = "audio/ogg";
                    message14.media.document.file_reference = new byte[0];
                    TLRPC.TL_documentAttributeAudio audio3 = new TLRPC.TL_documentAttributeAudio();
                    audio3.flags = 1028;
                    audio3.duration = 3;
                    audio3.voice = true;
                    audio3.waveform = new byte[]{0, 4, 17, -50, -93, 86, -103, -45, -12, -26, 63, -25, -3, 109, -114, -54, -4, -1, -1, -1, -1, -29, -1, -1, -25, -1, -1, -97, -43, 57, -57, -108, 1, -91, -4, -47, 21, 99, 10, 97, 43, 45, 115, -112, -77, 51, -63, 66, 40, 34, -122, -116, 48, -124, 16, 66, -120, 16, 68, 16, 33, 4, 1};
                    message14.media.document.attributes.add(audio3);
                    message14.out = true;
                    message14.peer_id = new TLRPC.TL_peerUser();
                    message14.peer_id.user_id = 0L;
                    MessageObject messageObject3 = new MessageObject(this$0.currentAccount, message14, true, false);
                    messageObject3.audioProgressSec = 1;
                    messageObject3.audioProgress = 0.3f;
                    messageObject3.useCustomPhoto = true;
                    this.messages.add(messageObject3);
                    this.messages.add(replyMessageObject2);
                    TLRPC.Message message15 = new TLRPC.TL_message();
                    message15.date = date + 10;
                    message15.dialog_id = 1L;
                    message15.flags = InputDeviceCompat.SOURCE_KEYBOARD;
                    message15.from_id = new TLRPC.TL_peerUser();
                    message15.id = 1;
                    message15.media = new TLRPC.TL_messageMediaPhoto();
                    message15.media.flags |= 3;
                    message15.media.photo = new TLRPC.TL_photo();
                    message15.media.photo.file_reference = new byte[0];
                    message15.media.photo.has_stickers = false;
                    message15.media.photo.id = 1L;
                    message15.media.photo.access_hash = 0L;
                    message15.media.photo.date = date;
                    TLRPC.TL_photoSize photoSize = new TLRPC.TL_photoSize();
                    photoSize.size = 0;
                    photoSize.w = 500;
                    photoSize.h = 302;
                    photoSize.type = "s";
                    photoSize.location = new TLRPC.TL_fileLocationUnavailable();
                    message15.media.photo.sizes.add(photoSize);
                    message15.message = LocaleController.getString("ThemePreviewLine4", R.string.ThemePreviewLine4);
                    message15.out = false;
                    message15.peer_id = new TLRPC.TL_peerUser();
                    message15.peer_id.user_id = UserConfig.getInstance(this$0.currentAccount).getClientUserId();
                    MessageObject messageObject4 = new MessageObject(this$0.currentAccount, message15, true, false);
                    messageObject4.useCustomPhoto = true;
                    this.messages.add(messageObject4);
                    return;
                }
            }
            TLRPC.Message message16 = new TLRPC.TL_message();
            if (this$0.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                message16.message = LocaleController.getString("BackgroundColorSinglePreviewLine2", R.string.BackgroundColorSinglePreviewLine2);
            } else {
                message16.message = LocaleController.getString("BackgroundPreviewLine2", R.string.BackgroundPreviewLine2);
            }
            message16.date = date + 60;
            message16.dialog_id = 1L;
            message16.flags = 259;
            message16.from_id = new TLRPC.TL_peerUser();
            message16.from_id.user_id = UserConfig.getInstance(this$0.currentAccount).getClientUserId();
            message16.id = 1;
            message16.media = new TLRPC.TL_messageMediaEmpty();
            message16.out = true;
            message16.peer_id = new TLRPC.TL_peerUser();
            message16.peer_id.user_id = 0L;
            MessageObject messageObject5 = new MessageObject(this$0.currentAccount, message16, true, false);
            messageObject5.eventId = 1L;
            messageObject5.resetLayout();
            this.messages.add(messageObject5);
            TLRPC.Message message17 = new TLRPC.TL_message();
            if (this$0.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                message17.message = LocaleController.getString("BackgroundColorSinglePreviewLine1", R.string.BackgroundColorSinglePreviewLine1);
            } else {
                message17.message = LocaleController.getString("BackgroundPreviewLine1", R.string.BackgroundPreviewLine1);
            }
            message17.date = date + 60;
            message17.dialog_id = 1L;
            message17.flags = 265;
            message17.from_id = new TLRPC.TL_peerUser();
            message17.id = 1;
            message17.media = new TLRPC.TL_messageMediaEmpty();
            message17.out = false;
            message17.peer_id = new TLRPC.TL_peerUser();
            message17.peer_id.user_id = UserConfig.getInstance(this$0.currentAccount).getClientUserId();
            MessageObject messageObject6 = new MessageObject(this$0.currentAccount, message17, true, false);
            messageObject6.eventId = 1L;
            messageObject6.resetLayout();
            this.messages.add(messageObject6);
        }

        private boolean hasButtons() {
            if (ThemePreviewActivity.this.messagesButtonsContainer == null || ThemePreviewActivity.this.screenType != 1 || ThemePreviewActivity.this.colorType != 3 || ThemePreviewActivity.this.accent.myMessagesGradientAccentColor2 == 0) {
                if (ThemePreviewActivity.this.backgroundButtonsContainer != null) {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return true;
                    }
                    if (ThemePreviewActivity.this.screenType == 1 && ThemePreviewActivity.this.colorType == 2) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int count = this.messages.size();
            if (hasButtons()) {
                return count + 1;
            }
            return count;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view;
            if (viewType == 0) {
                View view2 = new ChatMessageCell(this.mContext, false, new Theme.ResourcesProvider() { // from class: org.telegram.ui.ThemePreviewActivity.MessagesAdapter.1
                    @Override // org.telegram.ui.ActionBar.Theme.ResourcesProvider
                    public /* synthetic */ void applyServiceShaderMatrix(int i, int i2, float f, float f2) {
                        Theme.applyServiceShaderMatrix(i, i2, f, f2);
                    }

                    @Override // org.telegram.ui.ActionBar.Theme.ResourcesProvider
                    public /* synthetic */ int getColorOrDefault(String str) {
                        return getColor(str);
                    }

                    @Override // org.telegram.ui.ActionBar.Theme.ResourcesProvider
                    public /* synthetic */ Integer getCurrentColor(String str) {
                        Integer color;
                        color = getColor(str);
                        return color;
                    }

                    @Override // org.telegram.ui.ActionBar.Theme.ResourcesProvider
                    public /* synthetic */ Paint getPaint(String str) {
                        return Theme.ResourcesProvider.CC.$default$getPaint(this, str);
                    }

                    @Override // org.telegram.ui.ActionBar.Theme.ResourcesProvider
                    public /* synthetic */ boolean hasGradientService() {
                        return Theme.ResourcesProvider.CC.$default$hasGradientService(this);
                    }

                    @Override // org.telegram.ui.ActionBar.Theme.ResourcesProvider
                    public /* synthetic */ void setAnimatedColor(String str, int i) {
                        Theme.ResourcesProvider.CC.$default$setAnimatedColor(this, str, i);
                    }

                    @Override // org.telegram.ui.ActionBar.Theme.ResourcesProvider
                    public Integer getColor(String key) {
                        return Integer.valueOf(Theme.getColor(key));
                    }

                    @Override // org.telegram.ui.ActionBar.Theme.ResourcesProvider
                    public Drawable getDrawable(String drawableKey) {
                        if (drawableKey.equals(Theme.key_drawable_msgOut)) {
                            return ThemePreviewActivity.this.msgOutDrawable;
                        }
                        if (drawableKey.equals(Theme.key_drawable_msgOutSelected)) {
                            return ThemePreviewActivity.this.msgOutDrawableSelected;
                        }
                        if (drawableKey.equals(Theme.key_drawable_msgOutMedia)) {
                            return ThemePreviewActivity.this.msgOutMediaDrawable;
                        }
                        if (drawableKey.equals(Theme.key_drawable_msgOutMediaSelected)) {
                            return ThemePreviewActivity.this.msgOutMediaDrawableSelected;
                        }
                        return Theme.getThemeDrawable(drawableKey);
                    }
                });
                ChatMessageCell chatMessageCell = (ChatMessageCell) view2;
                chatMessageCell.setDelegate(new ChatMessageCell.ChatMessageCellDelegate() { // from class: org.telegram.ui.ThemePreviewActivity.MessagesAdapter.2
                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ boolean canDrawOutboundsContent() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canDrawOutboundsContent(this);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ boolean canPerformActions() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canPerformActions(this);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didLongPress(ChatMessageCell chatMessageCell2, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPress(this, chatMessageCell2, f, f2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didLongPressBotButton(ChatMessageCell chatMessageCell2, TLRPC.KeyboardButton keyboardButton) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressBotButton(this, chatMessageCell2, keyboardButton);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ boolean didLongPressChannelAvatar(ChatMessageCell chatMessageCell2, TLRPC.Chat chat, int i, float f, float f2) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressChannelAvatar(this, chatMessageCell2, chat, i, f, f2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ boolean didLongPressUserAvatar(ChatMessageCell chatMessageCell2, TLRPC.User user, float f, float f2) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressUserAvatar(this, chatMessageCell2, user, f, f2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressBotButton(ChatMessageCell chatMessageCell2, TLRPC.KeyboardButton keyboardButton) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressBotButton(this, chatMessageCell2, keyboardButton);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressCancelSendButton(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCancelSendButton(this, chatMessageCell2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressChannelAvatar(ChatMessageCell chatMessageCell2, TLRPC.Chat chat, int i, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressChannelAvatar(this, chatMessageCell2, chat, i, f, f2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressCommentButton(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCommentButton(this, chatMessageCell2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressHiddenForward(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHiddenForward(this, chatMessageCell2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressHint(ChatMessageCell chatMessageCell2, int i) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHint(this, chatMessageCell2, i);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressImage(ChatMessageCell chatMessageCell2, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressImage(this, chatMessageCell2, f, f2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressInstantButton(ChatMessageCell chatMessageCell2, int i) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressInstantButton(this, chatMessageCell2, i);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressOther(ChatMessageCell chatMessageCell2, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressOther(this, chatMessageCell2, f, f2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressReaction(ChatMessageCell chatMessageCell2, TLRPC.TL_reactionCount tL_reactionCount, boolean z) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReaction(this, chatMessageCell2, tL_reactionCount, z);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressReplyMessage(ChatMessageCell chatMessageCell2, int i) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReplyMessage(this, chatMessageCell2, i);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressSideButton(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressSideButton(this, chatMessageCell2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressTime(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressTime(this, chatMessageCell2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressUrl(ChatMessageCell chatMessageCell2, CharacterStyle characterStyle, boolean z) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUrl(this, chatMessageCell2, characterStyle, z);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressUserAvatar(ChatMessageCell chatMessageCell2, TLRPC.User user, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUserAvatar(this, chatMessageCell2, user, f, f2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressViaBot(ChatMessageCell chatMessageCell2, String str) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBot(this, chatMessageCell2, str);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressViaBotNotInline(ChatMessageCell chatMessageCell2, long j) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBotNotInline(this, chatMessageCell2, j);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressVoteButtons(ChatMessageCell chatMessageCell2, ArrayList arrayList, int i, int i2, int i3) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressVoteButtons(this, chatMessageCell2, arrayList, i, i2, i3);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didStartVideoStream(MessageObject messageObject) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didStartVideoStream(this, messageObject);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ String getAdminRank(long j) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getAdminRank(this, j);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ PinchToZoomHelper getPinchToZoomHelper() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getPinchToZoomHelper(this);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ TextSelectionHelper.ChatListTextSelectionHelper getTextSelectionHelper() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getTextSelectionHelper(this);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ boolean hasSelectedMessages() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$hasSelectedMessages(this);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void invalidateBlur() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$invalidateBlur(this);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ boolean isLandscape() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$isLandscape(this);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ boolean keyboardIsOpened() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$keyboardIsOpened(this);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void needOpenWebView(MessageObject messageObject, String str, String str2, String str3, String str4, int i, int i2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$needOpenWebView(this, messageObject, str, str2, str3, str4, i, i2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ boolean needPlayMessage(MessageObject messageObject) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$needPlayMessage(this, messageObject);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void needReloadPolls() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$needReloadPolls(this);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void needShowPremiumFeatures(String str) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$needShowPremiumFeatures(this, str);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ boolean onAccessibilityAction(int i, Bundle bundle) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$onAccessibilityAction(this, i, bundle);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void onDiceFinished() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$onDiceFinished(this);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void setShouldNotRepeatSticker(MessageObject messageObject) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$setShouldNotRepeatSticker(this, messageObject);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ boolean shouldDrawThreadProgress(ChatMessageCell chatMessageCell2) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldDrawThreadProgress(this, chatMessageCell2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ boolean shouldRepeatSticker(MessageObject messageObject) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldRepeatSticker(this, messageObject);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void videoTimerReached() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$videoTimerReached(this);
                    }
                });
                view = view2;
            } else if (viewType == 1) {
                View view3 = new ChatActionCell(this.mContext);
                ((ChatActionCell) view3).setDelegate(new ChatActionCell.ChatActionCellDelegate() { // from class: org.telegram.ui.ThemePreviewActivity.MessagesAdapter.3
                    @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
                    public /* synthetic */ void didClickImage(ChatActionCell chatActionCell) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$didClickImage(this, chatActionCell);
                    }

                    @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
                    public /* synthetic */ boolean didLongPress(ChatActionCell chatActionCell, float f, float f2) {
                        return ChatActionCell.ChatActionCellDelegate.CC.$default$didLongPress(this, chatActionCell, f, f2);
                    }

                    @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
                    public /* synthetic */ void didPressBotButton(MessageObject messageObject, TLRPC.KeyboardButton keyboardButton) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$didPressBotButton(this, messageObject, keyboardButton);
                    }

                    @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
                    public /* synthetic */ void didPressReplyMessage(ChatActionCell chatActionCell, int i) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$didPressReplyMessage(this, chatActionCell, i);
                    }

                    @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
                    public /* synthetic */ void needOpenInviteLink(TLRPC.TL_chatInviteExported tL_chatInviteExported) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$needOpenInviteLink(this, tL_chatInviteExported);
                    }

                    @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
                    public /* synthetic */ void needOpenUserProfile(long j) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$needOpenUserProfile(this, j);
                    }
                });
                view = view3;
            } else if (viewType == 2) {
                if (ThemePreviewActivity.this.backgroundButtonsContainer.getParent() != null) {
                    ((ViewGroup) ThemePreviewActivity.this.backgroundButtonsContainer.getParent()).removeView(ThemePreviewActivity.this.backgroundButtonsContainer);
                }
                FrameLayout frameLayout = new FrameLayout(this.mContext) { // from class: org.telegram.ui.ThemePreviewActivity.MessagesAdapter.4
                    @Override // android.widget.FrameLayout, android.view.View
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), C.BUFFER_FLAG_ENCRYPTED));
                    }
                };
                frameLayout.addView(ThemePreviewActivity.this.backgroundButtonsContainer, LayoutHelper.createFrame(-1, 76, 17));
                view = frameLayout;
            } else {
                if (ThemePreviewActivity.this.messagesButtonsContainer.getParent() != null) {
                    ((ViewGroup) ThemePreviewActivity.this.messagesButtonsContainer.getParent()).removeView(ThemePreviewActivity.this.messagesButtonsContainer);
                }
                FrameLayout frameLayout2 = new FrameLayout(this.mContext) { // from class: org.telegram.ui.ThemePreviewActivity.MessagesAdapter.5
                    @Override // android.widget.FrameLayout, android.view.View
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), C.BUFFER_FLAG_ENCRYPTED));
                    }
                };
                frameLayout2.addView(ThemePreviewActivity.this.messagesButtonsContainer, LayoutHelper.createFrame(-1, 76, 17));
                view = frameLayout2;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean pinnedBotton;
            int type = holder.getItemViewType();
            if (type != 2 && type != 3) {
                if (hasButtons()) {
                    position--;
                }
                MessageObject message = this.messages.get(position);
                View view = holder.itemView;
                if (view instanceof ChatMessageCell) {
                    ChatMessageCell messageCell = (ChatMessageCell) view;
                    boolean pinnedTop = false;
                    messageCell.isChat = false;
                    int nextType = getItemViewType(position - 1);
                    int prevType = getItemViewType(position + 1);
                    if (!(message.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup) && nextType == holder.getItemViewType()) {
                        MessageObject nextMessage = this.messages.get(position - 1);
                        pinnedBotton = nextMessage.isOutOwner() == message.isOutOwner() && Math.abs(nextMessage.messageOwner.date - message.messageOwner.date) <= 300;
                    } else {
                        pinnedBotton = false;
                    }
                    if (prevType == holder.getItemViewType() && position + 1 < this.messages.size()) {
                        MessageObject prevMessage = this.messages.get(position + 1);
                        if (!(prevMessage.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup) && prevMessage.isOutOwner() == message.isOutOwner() && Math.abs(prevMessage.messageOwner.date - message.messageOwner.date) <= 300) {
                            pinnedTop = true;
                        }
                    } else {
                        pinnedTop = false;
                    }
                    messageCell.isChat = this.showSecretMessages;
                    messageCell.setFullyDraw(true);
                    messageCell.setMessageObject(message, null, pinnedBotton, pinnedTop);
                } else if (view instanceof ChatActionCell) {
                    ChatActionCell actionCell = (ChatActionCell) view;
                    actionCell.setMessageObject(message);
                    actionCell.setAlpha(1.0f);
                }
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (hasButtons()) {
                if (position == 0) {
                    return ThemePreviewActivity.this.colorType == 3 ? 3 : 2;
                }
                position--;
            }
            if (position >= 0 && position < this.messages.size()) {
                return this.messages.get(position).contentType;
            }
            return 4;
        }
    }

    /* loaded from: classes4.dex */
    public class PatternsAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public PatternsAdapter(Context context) {
            ThemePreviewActivity.this = r1;
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            return 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (ThemePreviewActivity.this.patterns != null) {
                return ThemePreviewActivity.this.patterns.size();
            }
            return 0;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            PatternCell view = new PatternCell(this.mContext, ThemePreviewActivity.this.maxWallpaperSize, new PatternCell.PatternCellDelegate() { // from class: org.telegram.ui.ThemePreviewActivity.PatternsAdapter.1
                @Override // org.telegram.ui.Cells.PatternCell.PatternCellDelegate
                public TLRPC.TL_wallPaper getSelectedPattern() {
                    return ThemePreviewActivity.this.selectedPattern;
                }

                @Override // org.telegram.ui.Cells.PatternCell.PatternCellDelegate
                public int getCheckColor() {
                    return ThemePreviewActivity.this.checkColor;
                }

                @Override // org.telegram.ui.Cells.PatternCell.PatternCellDelegate
                public int getBackgroundColor() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundColor;
                    }
                    int defaultBackground = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper);
                    int backgroundOverrideColor = (int) ThemePreviewActivity.this.accent.backgroundOverrideColor;
                    return backgroundOverrideColor != 0 ? backgroundOverrideColor : defaultBackground;
                }

                @Override // org.telegram.ui.Cells.PatternCell.PatternCellDelegate
                public int getBackgroundGradientColor1() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundGradientColor1;
                    }
                    int defaultBackgroundGradient = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to1);
                    int backgroundGradientOverrideColor = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor1;
                    return backgroundGradientOverrideColor != 0 ? backgroundGradientOverrideColor : defaultBackgroundGradient;
                }

                @Override // org.telegram.ui.Cells.PatternCell.PatternCellDelegate
                public int getBackgroundGradientColor2() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundGradientColor2;
                    }
                    int defaultBackgroundGradient = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to2);
                    int backgroundGradientOverrideColor = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor2;
                    return backgroundGradientOverrideColor != 0 ? backgroundGradientOverrideColor : defaultBackgroundGradient;
                }

                @Override // org.telegram.ui.Cells.PatternCell.PatternCellDelegate
                public int getBackgroundGradientColor3() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundGradientColor3;
                    }
                    int defaultBackgroundGradient = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to3);
                    int backgroundGradientOverrideColor = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor3;
                    return backgroundGradientOverrideColor != 0 ? backgroundGradientOverrideColor : defaultBackgroundGradient;
                }

                @Override // org.telegram.ui.Cells.PatternCell.PatternCellDelegate
                public int getBackgroundGradientAngle() {
                    return ThemePreviewActivity.this.screenType == 2 ? ThemePreviewActivity.this.backgroundRotation : ThemePreviewActivity.this.accent.backgroundRotation;
                }

                @Override // org.telegram.ui.Cells.PatternCell.PatternCellDelegate
                public float getIntensity() {
                    return ThemePreviewActivity.this.currentIntensity;
                }

                @Override // org.telegram.ui.Cells.PatternCell.PatternCellDelegate
                public int getPatternColor() {
                    return ThemePreviewActivity.this.patternColor;
                }
            });
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            PatternCell view = (PatternCell) holder.itemView;
            view.setPattern((TLRPC.TL_wallPaper) ThemePreviewActivity.this.patterns.get(position));
            view.getImageReceiver().setColorFilter(new PorterDuffColorFilter(ThemePreviewActivity.this.patternColor, ThemePreviewActivity.this.blendMode));
            if (Build.VERSION.SDK_INT >= 29) {
                int color2 = 0;
                if (ThemePreviewActivity.this.screenType != 1) {
                    if (ThemePreviewActivity.this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                        color2 = ThemePreviewActivity.this.backgroundGradientColor2;
                    }
                } else {
                    int defaultBackgroundGradient2 = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to2);
                    int backgroundGradientOverrideColor2 = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor2;
                    if (backgroundGradientOverrideColor2 == 0 && ThemePreviewActivity.this.accent.backgroundGradientOverrideColor2 != 0) {
                        color2 = 0;
                    } else {
                        color2 = backgroundGradientOverrideColor2 != 0 ? backgroundGradientOverrideColor2 : defaultBackgroundGradient2;
                    }
                }
                if (color2 != 0 && ThemePreviewActivity.this.currentIntensity >= 0.0f) {
                    ThemePreviewActivity.this.backgroundImage.getImageReceiver().setBlendMode(BlendMode.SOFT_LIGHT);
                } else {
                    view.getImageReceiver().setBlendMode(null);
                }
            }
        }
    }

    private List<ThemeDescription> getThemeDescriptionsInternal() {
        ThemeDescription.ThemeDescriptionDelegate descriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda14
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                ThemePreviewActivity.this.m4651x2bbe16a4();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        List<ThemeDescription> items = new ArrayList<>();
        items.add(new ThemeDescription(this.page1, ThemeDescription.FLAG_BACKGROUND, null, null, null, descriptionDelegate, Theme.key_windowBackgroundWhite));
        items.add(new ThemeDescription(this.viewPager, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        items.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        items.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        items.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        items.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
        items.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder));
        items.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        items.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        items.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBTITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultSubtitle));
        items.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        items.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, descriptionDelegate, Theme.key_actionBarDefaultSubmenuBackground));
        items.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, descriptionDelegate, Theme.key_actionBarDefaultSubmenuItem));
        items.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        items.add(new ThemeDescription(this.listView2, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        items.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chats_actionIcon));
        items.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chats_actionBackground));
        items.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_chats_actionPressedBackground));
        if (!this.useDefaultThemeForButtons) {
            items.add(new ThemeDescription(this.saveButtonsContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
            items.add(new ThemeDescription(this.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_fieldOverlayText));
            items.add(new ThemeDescription(this.doneButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_fieldOverlayText));
        }
        ColorPicker colorPicker = this.colorPicker;
        if (colorPicker != null) {
            colorPicker.provideThemeDescriptions(items);
        }
        if (this.patternLayout != null) {
            for (int a = 0; a < this.patternLayout.length; a++) {
                items.add(new ThemeDescription(this.patternLayout[a], 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow));
                items.add(new ThemeDescription(this.patternLayout[a], 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground));
            }
            for (int a2 = 0; a2 < this.patternsButtonsContainer.length; a2++) {
                items.add(new ThemeDescription(this.patternsButtonsContainer[a2], 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow));
                items.add(new ThemeDescription(this.patternsButtonsContainer[a2], 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground));
            }
            items.add(new ThemeDescription(this.bottomOverlayChat, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow));
            items.add(new ThemeDescription(this.bottomOverlayChat, 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground));
            items.add(new ThemeDescription(this.bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_fieldOverlayText));
            for (int a3 = 0; a3 < this.patternsSaveButton.length; a3++) {
                items.add(new ThemeDescription(this.patternsSaveButton[a3], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_fieldOverlayText));
            }
            for (int a4 = 0; a4 < this.patternsCancelButton.length; a4++) {
                items.add(new ThemeDescription(this.patternsCancelButton[a4], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_fieldOverlayText));
            }
            items.add(new ThemeDescription(this.intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"innerPaint1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_progressBackground));
            items.add(new ThemeDescription(this.intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"outerPaint1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_progress));
            items.add(new ThemeDescription(this.intensityCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, Theme.key_chat_inBubble));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, Theme.key_chat_inBubbleSelected));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgInDrawable.getShadowDrawables(), null, Theme.key_chat_inBubbleShadow));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), null, Theme.key_chat_inBubbleShadow));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{this.msgOutDrawable, this.msgOutMediaDrawable}, null, Theme.key_chat_outBubble));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{this.msgOutDrawable, this.msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient1));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{this.msgOutDrawable, this.msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient2));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{this.msgOutDrawable, this.msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient3));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, Theme.key_chat_outBubbleSelected));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgOutDrawable.getShadowDrawables(), null, Theme.key_chat_outBubbleShadow));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgOutMediaDrawable.getShadowDrawables(), null, Theme.key_chat_outBubbleShadow));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageTextIn));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageTextOut));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, Theme.key_chat_outSentCheck));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckSelected));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, Theme.key_chat_outSentCheckRead));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckReadSelected));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, Theme.key_chat_mediaSentCheck));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyLine));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyLine));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyNameText));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyNameText));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMessageText));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMessageText));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMediaMessageSelectedText));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMediaMessageSelectedText));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inTimeText));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outTimeText));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inTimeSelectedText));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outTimeSelectedText));
        }
        return items;
    }

    /* renamed from: lambda$getThemeDescriptionsInternal$26$org-telegram-ui-ThemePreviewActivity */
    public /* synthetic */ void m4651x2bbe16a4() {
        ActionBarMenuItem actionBarMenuItem = this.dropDownContainer;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.redrawPopup(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground));
            this.dropDownContainer.setPopupItemsColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem), false);
        }
        Drawable drawable = this.sheetDrawable;
        if (drawable != null) {
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhite), PorterDuff.Mode.MULTIPLY));
        }
    }
}
