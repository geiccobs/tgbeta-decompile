package org.telegram.ui.ActionBar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.StateSet;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.ViewCompat;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesController$$ExternalSyntheticLambda132;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.time.SunDate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AudioVisualizerDrawable;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.ChoosingStickerStatusDrawable;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.FragmentContextViewWavesDrawable;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.MsgClockDrawable;
import org.telegram.ui.Components.PathAnimator;
import org.telegram.ui.Components.PlayingGameDrawable;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecordStatusDrawable;
import org.telegram.ui.Components.RoundStatusDrawable;
import org.telegram.ui.Components.ScamDrawable;
import org.telegram.ui.Components.SendingFileDrawable;
import org.telegram.ui.Components.StatusDrawable;
import org.telegram.ui.Components.TypingDotsDrawable;
import org.telegram.ui.RoundVideoProgressShadow;
/* loaded from: classes4.dex */
public class Theme {
    public static final int ACTION_BAR_AUDIO_SELECTOR_COLOR = 788529152;
    public static final int ACTION_BAR_MEDIA_PICKER_COLOR = -13421773;
    public static final int ACTION_BAR_PHOTO_VIEWER_COLOR = 2130706432;
    public static final int ACTION_BAR_PICKER_SELECTOR_COLOR = -12763843;
    public static final int ACTION_BAR_PLAYER_COLOR = -1;
    public static final int ACTION_BAR_VIDEO_EDIT_COLOR = -16777216;
    public static final int ACTION_BAR_WHITE_SELECTOR_COLOR = 1090519039;
    public static final int ARTICLE_VIEWER_MEDIA_PROGRESS_COLOR = -1;
    public static final int AUTO_NIGHT_TYPE_AUTOMATIC = 2;
    public static final int AUTO_NIGHT_TYPE_NONE = 0;
    public static final int AUTO_NIGHT_TYPE_SCHEDULED = 1;
    public static final int AUTO_NIGHT_TYPE_SYSTEM = 3;
    private static Field BitmapDrawable_mColorFilter = null;
    public static final String COLOR_BACKGROUND_SLUG = "c";
    public static final String DEFAULT_BACKGROUND_SLUG = "d";
    private static final int LIGHT_SENSOR_THEME_SWITCH_DELAY = 1800;
    private static final int LIGHT_SENSOR_THEME_SWITCH_NEAR_DELAY = 12000;
    private static final int LIGHT_SENSOR_THEME_SWITCH_NEAR_THRESHOLD = 12000;
    private static final float MAXIMUM_LUX_BREAKPOINT = 500.0f;
    public static final int MSG_OUT_COLOR_BLACK = -14606047;
    public static final int MSG_OUT_COLOR_WHITE = -1;
    public static final int RIPPLE_MASK_ALL = 2;
    public static final int RIPPLE_MASK_CIRCLE_20DP = 1;
    public static final int RIPPLE_MASK_CIRCLE_AUTO = 5;
    public static final int RIPPLE_MASK_CIRCLE_TO_BOUND_CORNER = 4;
    public static final int RIPPLE_MASK_CIRCLE_TO_BOUND_EDGE = 3;
    private static final int RIPPLE_MASK_ROUNDRECT_6DP = 7;
    private static Method StateListDrawable_getStateDrawableMethod = null;
    public static final String THEME_BACKGROUND_SLUG = "t";
    private static SensorEventListener ambientSensorListener = null;
    private static HashMap<MessageObject, AudioVisualizerDrawable> animatedOutVisualizerDrawables = null;
    private static HashMap<String, Integer> animatingColors = null;
    public static float autoNightBrighnessThreshold = 0.0f;
    public static String autoNightCityName = null;
    public static int autoNightDayEndTime = 0;
    public static int autoNightDayStartTime = 0;
    public static int autoNightLastSunCheckDay = 0;
    public static double autoNightLocationLatitude = 0.0d;
    public static double autoNightLocationLongitude = 0.0d;
    public static boolean autoNightScheduleByLocation = false;
    public static int autoNightSunriseTime = 0;
    public static int autoNightSunsetTime = 0;
    public static Paint avatar_backgroundPaint = null;
    private static BackgroundGradientDrawable.Disposable backgroundGradientDisposable = null;
    public static Drawable calllog_msgCallDownGreenDrawable = null;
    public static Drawable calllog_msgCallDownRedDrawable = null;
    public static Drawable calllog_msgCallUpGreenDrawable = null;
    public static Drawable calllog_msgCallUpRedDrawable = null;
    private static boolean canStartHolidayAnimation = false;
    private static boolean changingWallpaper = false;
    public static Paint chat_actionBackgroundGradientDarkenPaint = null;
    public static Paint chat_actionBackgroundPaint = null;
    public static Paint chat_actionBackgroundPaint2 = null;
    public static Paint chat_actionBackgroundSelectedPaint = null;
    public static Paint chat_actionBackgroundSelectedPaint2 = null;
    public static TextPaint chat_actionTextPaint = null;
    public static TextPaint chat_adminPaint = null;
    public static Drawable chat_attachEmptyDrawable = null;
    public static TextPaint chat_audioPerformerPaint = null;
    public static TextPaint chat_audioTimePaint = null;
    public static TextPaint chat_audioTitlePaint = null;
    public static TextPaint chat_botButtonPaint = null;
    public static Drawable chat_botCardDrawable = null;
    public static Drawable chat_botInlineDrawable = null;
    public static Drawable chat_botInviteDrawable = null;
    public static Drawable chat_botLinkDrawable = null;
    public static Paint chat_botProgressPaint = null;
    public static Drawable chat_botWebViewDrawable = null;
    public static Drawable chat_commentArrowDrawable = null;
    public static Drawable chat_commentDrawable = null;
    public static Drawable chat_commentStickerDrawable = null;
    public static Paint chat_composeBackgroundPaint = null;
    public static Drawable chat_composeShadowDrawable = null;
    public static Drawable chat_composeShadowRoundDrawable = null;
    public static TextPaint chat_contactNamePaint = null;
    public static TextPaint chat_contactPhonePaint = null;
    public static TextPaint chat_contextResult_descriptionTextPaint = null;
    public static Drawable chat_contextResult_shadowUnderSwitchDrawable = null;
    public static TextPaint chat_contextResult_titleTextPaint = null;
    public static Paint chat_deleteProgressPaint = null;
    public static Paint chat_docBackPaint = null;
    public static TextPaint chat_docNamePaint = null;
    public static TextPaint chat_durationPaint = null;
    public static Drawable chat_flameIcon = null;
    public static TextPaint chat_forwardNamePaint = null;
    public static TextPaint chat_gamePaint = null;
    public static Drawable chat_gifIcon = null;
    public static Drawable chat_goIconDrawable = null;
    public static TextPaint chat_infoPaint = null;
    public static Drawable chat_inlineResultAudio = null;
    public static Drawable chat_inlineResultFile = null;
    public static Drawable chat_inlineResultLocation = null;
    public static TextPaint chat_instantViewPaint = null;
    public static Paint chat_instantViewRectPaint = null;
    public static TextPaint chat_livePaint = null;
    public static TextPaint chat_locationAddressPaint = null;
    public static TextPaint chat_locationTitlePaint = null;
    public static Drawable chat_lockIconDrawable = null;
    public static Paint chat_messageBackgroundSelectedPaint = null;
    private static AudioVisualizerDrawable chat_msgAudioVisualizeDrawable = null;
    public static Drawable chat_msgAvatarLiveLocationDrawable = null;
    public static TextPaint chat_msgBotButtonPaint = null;
    public static Drawable chat_msgCallDownGreenDrawable = null;
    public static Drawable chat_msgCallDownRedDrawable = null;
    public static Drawable chat_msgCallUpGreenDrawable = null;
    public static MsgClockDrawable chat_msgClockDrawable = null;
    public static Drawable chat_msgErrorDrawable = null;
    public static Paint chat_msgErrorPaint = null;
    public static TextPaint chat_msgGameTextPaint = null;
    public static MessageDrawable chat_msgInDrawable = null;
    public static Drawable chat_msgInInstantDrawable = null;
    public static MessageDrawable chat_msgInMediaDrawable = null;
    public static MessageDrawable chat_msgInMediaSelectedDrawable = null;
    public static Drawable chat_msgInMenuDrawable = null;
    public static Drawable chat_msgInMenuSelectedDrawable = null;
    public static Drawable chat_msgInPinnedDrawable = null;
    public static Drawable chat_msgInPinnedSelectedDrawable = null;
    public static Drawable chat_msgInRepliesDrawable = null;
    public static Drawable chat_msgInRepliesSelectedDrawable = null;
    public static MessageDrawable chat_msgInSelectedDrawable = null;
    public static Drawable chat_msgInViewsDrawable = null;
    public static Drawable chat_msgInViewsSelectedDrawable = null;
    public static Drawable chat_msgMediaCheckDrawable = null;
    public static Drawable chat_msgMediaHalfCheckDrawable = null;
    public static Drawable chat_msgMediaMenuDrawable = null;
    public static Drawable chat_msgMediaPinnedDrawable = null;
    public static Drawable chat_msgMediaRepliesDrawable = null;
    public static Drawable chat_msgMediaViewsDrawable = null;
    public static Drawable chat_msgNoSoundDrawable = null;
    public static Drawable chat_msgOutCheckDrawable = null;
    public static Drawable chat_msgOutCheckReadDrawable = null;
    public static Drawable chat_msgOutCheckReadSelectedDrawable = null;
    public static Drawable chat_msgOutCheckSelectedDrawable = null;
    public static MessageDrawable chat_msgOutDrawable = null;
    public static Drawable chat_msgOutHalfCheckDrawable = null;
    public static Drawable chat_msgOutHalfCheckSelectedDrawable = null;
    public static Drawable chat_msgOutInstantDrawable = null;
    public static Drawable chat_msgOutLocationDrawable = null;
    public static MessageDrawable chat_msgOutMediaDrawable = null;
    public static MessageDrawable chat_msgOutMediaSelectedDrawable = null;
    public static Drawable chat_msgOutMenuDrawable = null;
    public static Drawable chat_msgOutMenuSelectedDrawable = null;
    public static Drawable chat_msgOutPinnedDrawable = null;
    public static Drawable chat_msgOutPinnedSelectedDrawable = null;
    public static Drawable chat_msgOutRepliesDrawable = null;
    public static Drawable chat_msgOutRepliesSelectedDrawable = null;
    public static MessageDrawable chat_msgOutSelectedDrawable = null;
    public static Drawable chat_msgOutViewsDrawable = null;
    public static Drawable chat_msgOutViewsSelectedDrawable = null;
    public static Drawable chat_msgStickerCheckDrawable = null;
    public static Drawable chat_msgStickerHalfCheckDrawable = null;
    public static Drawable chat_msgStickerPinnedDrawable = null;
    public static Drawable chat_msgStickerRepliesDrawable = null;
    public static Drawable chat_msgStickerViewsDrawable = null;
    public static TextPaint chat_msgTextPaint = null;
    public static TextPaint chat_msgTextPaintOneEmoji = null;
    public static TextPaint chat_msgTextPaintThreeEmoji = null;
    public static TextPaint chat_msgTextPaintTwoEmoji = null;
    public static Drawable chat_muteIconDrawable = null;
    public static TextPaint chat_namePaint = null;
    public static Paint chat_outUrlPaint = null;
    public static Paint chat_pollTimerPaint = null;
    public static Paint chat_radialProgress2Paint = null;
    public static Paint chat_radialProgressPaint = null;
    public static Paint chat_radialProgressPausedPaint = null;
    public static Paint chat_radialProgressPausedSeekbarPaint = null;
    public static Drawable chat_redLocationIcon = null;
    public static Drawable chat_replyIconDrawable = null;
    public static Paint chat_replyLinePaint = null;
    public static TextPaint chat_replyNamePaint = null;
    public static TextPaint chat_replyTextPaint = null;
    public static Drawable chat_roundVideoShadow = null;
    public static Drawable chat_shareIconDrawable = null;
    public static TextPaint chat_shipmentPaint = null;
    public static Paint chat_statusPaint = null;
    public static Paint chat_statusRecordPaint = null;
    public static TextPaint chat_stickerCommentCountPaint = null;
    public static Paint chat_textSearchSelectionPaint = null;
    public static Paint chat_timeBackgroundPaint = null;
    public static TextPaint chat_timePaint = null;
    public static Paint chat_urlPaint = null;
    public static Paint checkboxSquare_backgroundPaint = null;
    public static Paint checkboxSquare_checkPaint = null;
    public static Paint checkboxSquare_eraserPaint = null;
    public static int currentColor = 0;
    private static ThemeInfo currentDayTheme = null;
    private static ThemeInfo currentNightTheme = null;
    private static ColorFilter currentShareColorFilter = null;
    private static int currentShareColorFilterColor = 0;
    private static ColorFilter currentShareSelectedColorFilter = null;
    private static int currentShareSelectedColorFilterColor = 0;
    private static ThemeInfo currentTheme = null;
    private static ThemeInfo defaultTheme = null;
    public static Paint dialogs_actionMessagePaint = null;
    public static RLottieDrawable dialogs_archiveAvatarDrawable = null;
    public static boolean dialogs_archiveAvatarDrawableRecolored = false;
    public static RLottieDrawable dialogs_archiveDrawable = null;
    public static boolean dialogs_archiveDrawableRecolored = false;
    public static TextPaint dialogs_archiveTextPaint = null;
    public static TextPaint dialogs_archiveTextPaintSmall = null;
    public static Drawable dialogs_checkDrawable = null;
    public static Drawable dialogs_checkReadDrawable = null;
    public static Drawable dialogs_clockDrawable = null;
    public static Paint dialogs_countGrayPaint = null;
    public static Paint dialogs_countPaint = null;
    public static TextPaint dialogs_countTextPaint = null;
    public static Drawable dialogs_errorDrawable = null;
    public static Paint dialogs_errorPaint = null;
    public static ScamDrawable dialogs_fakeDrawable = null;
    public static Drawable dialogs_halfCheckDrawable = null;
    public static RLottieDrawable dialogs_hidePsaDrawable = null;
    public static boolean dialogs_hidePsaDrawableRecolored = false;
    public static Drawable dialogs_holidayDrawable = null;
    private static int dialogs_holidayDrawableOffsetX = 0;
    private static int dialogs_holidayDrawableOffsetY = 0;
    public static Drawable dialogs_lockDrawable = null;
    public static Drawable dialogs_mentionDrawable = null;
    public static TextPaint dialogs_messageNamePaint = null;
    public static TextPaint[] dialogs_messagePaint = null;
    public static TextPaint[] dialogs_messagePrintingPaint = null;
    public static Drawable dialogs_muteDrawable = null;
    public static TextPaint[] dialogs_nameEncryptedPaint = null;
    public static TextPaint[] dialogs_namePaint = null;
    public static TextPaint dialogs_offlinePaint = null;
    public static Paint dialogs_onlineCirclePaint = null;
    public static TextPaint dialogs_onlinePaint = null;
    public static RLottieDrawable dialogs_pinArchiveDrawable = null;
    public static Drawable dialogs_pinnedDrawable = null;
    public static Paint dialogs_pinnedPaint = null;
    public static Drawable dialogs_playDrawable = null;
    public static Paint dialogs_reactionsCountPaint = null;
    public static Drawable dialogs_reactionsMentionDrawable = null;
    public static Drawable dialogs_reorderDrawable = null;
    public static ScamDrawable dialogs_scamDrawable = null;
    public static TextPaint dialogs_searchNameEncryptedPaint = null;
    public static TextPaint dialogs_searchNamePaint = null;
    public static RLottieDrawable dialogs_swipeDeleteDrawable = null;
    public static RLottieDrawable dialogs_swipeMuteDrawable = null;
    public static RLottieDrawable dialogs_swipePinDrawable = null;
    public static RLottieDrawable dialogs_swipeReadDrawable = null;
    public static RLottieDrawable dialogs_swipeUnmuteDrawable = null;
    public static RLottieDrawable dialogs_swipeUnpinDrawable = null;
    public static RLottieDrawable dialogs_swipeUnreadDrawable = null;
    public static Paint dialogs_tabletSeletedPaint = null;
    public static TextPaint dialogs_timePaint = null;
    public static RLottieDrawable dialogs_unarchiveDrawable = null;
    public static RLottieDrawable dialogs_unpinArchiveDrawable = null;
    public static Drawable dialogs_verifiedCheckDrawable = null;
    public static Drawable dialogs_verifiedDrawable = null;
    public static Paint dividerExtraPaint = null;
    public static Paint dividerPaint = null;
    private static FragmentContextViewWavesDrawable fragmentContextViewWavesDrawable = null;
    private static boolean hasPreviousTheme = false;
    private static boolean isApplyingAccent = false;
    private static boolean isCustomTheme = false;
    private static boolean isInNigthMode = false;
    private static boolean isPatternWallpaper = false;
    private static boolean isWallpaperMotion = false;
    public static final String key_actionBarActionModeDefault = "actionBarActionModeDefault";
    public static final String key_actionBarActionModeDefaultIcon = "actionBarActionModeDefaultIcon";
    public static final String key_actionBarActionModeDefaultSelector = "actionBarActionModeDefaultSelector";
    public static final String key_actionBarActionModeDefaultTop = "actionBarActionModeDefaultTop";
    public static final String key_actionBarBrowser = "actionBarBrowser";
    public static final String key_actionBarDefault = "actionBarDefault";
    public static final String key_actionBarDefaultArchived = "actionBarDefaultArchived";
    public static final String key_actionBarDefaultArchivedIcon = "actionBarDefaultArchivedIcon";
    public static final String key_actionBarDefaultArchivedSearch = "actionBarDefaultArchivedSearch";
    public static final String key_actionBarDefaultArchivedSearchPlaceholder = "actionBarDefaultSearchArchivedPlaceholder";
    public static final String key_actionBarDefaultArchivedSelector = "actionBarDefaultArchivedSelector";
    public static final String key_actionBarDefaultArchivedTitle = "actionBarDefaultArchivedTitle";
    public static final String key_actionBarDefaultIcon = "actionBarDefaultIcon";
    public static final String key_actionBarDefaultSearch = "actionBarDefaultSearch";
    public static final String key_actionBarDefaultSearchPlaceholder = "actionBarDefaultSearchPlaceholder";
    public static final String key_actionBarDefaultSelector = "actionBarDefaultSelector";
    public static final String key_actionBarDefaultSubmenuBackground = "actionBarDefaultSubmenuBackground";
    public static final String key_actionBarDefaultSubmenuItem = "actionBarDefaultSubmenuItem";
    public static final String key_actionBarDefaultSubmenuItemIcon = "actionBarDefaultSubmenuItemIcon";
    public static final String key_actionBarDefaultSubmenuSeparator = "actionBarDefaultSubmenuSeparator";
    public static final String key_actionBarDefaultSubtitle = "actionBarDefaultSubtitle";
    public static final String key_actionBarDefaultTitle = "actionBarDefaultTitle";
    public static final String key_actionBarTabActiveText = "actionBarTabActiveText";
    public static final String key_actionBarTabLine = "actionBarTabLine";
    public static final String key_actionBarTabSelector = "actionBarTabSelector";
    public static final String key_actionBarTabUnactiveText = "actionBarTabUnactiveText";
    public static final String key_actionBarTipBackground = "actionBarTipBackground";
    public static final String key_actionBarWhiteSelector = "actionBarWhiteSelector";
    public static final String key_avatar_actionBarIconBlue = "avatar_actionBarIconBlue";
    public static final String key_avatar_actionBarSelectorBlue = "avatar_actionBarSelectorBlue";
    public static final String key_avatar_backgroundActionBarBlue = "avatar_backgroundActionBarBlue";
    public static final String key_avatar_backgroundArchived = "avatar_backgroundArchived";
    public static final String key_avatar_backgroundArchivedHidden = "avatar_backgroundArchivedHidden";
    public static final String key_avatar_backgroundInProfileBlue = "avatar_backgroundInProfileBlue";
    public static final String key_avatar_backgroundSaved = "avatar_backgroundSaved";
    public static final String key_avatar_subtitleInProfileBlue = "avatar_subtitleInProfileBlue";
    public static final String key_avatar_text = "avatar_text";
    public static final String key_calls_callReceivedGreenIcon = "calls_callReceivedGreenIcon";
    public static final String key_calls_callReceivedRedIcon = "calls_callReceivedRedIcon";
    public static final String key_changephoneinfo_image = "changephoneinfo_image";
    public static final String key_changephoneinfo_image2 = "changephoneinfo_image2";
    public static final String key_chat_BlurAlpha = "chat_BlurAlpha";
    public static final String key_chat_TextSelectionCursor = "chat_TextSelectionCursor";
    public static final String key_chat_addContact = "chat_addContact";
    public static final String key_chat_attachActiveTab = "chat_attachActiveTab";
    public static final String key_chat_attachAudioBackground = "chat_attachAudioBackground";
    public static final String key_chat_attachAudioIcon = "chat_attachAudioIcon";
    public static final String key_chat_attachAudioText = "chat_attachAudioText";
    public static final String key_chat_attachCheckBoxBackground = "chat_attachCheckBoxBackground";
    public static final String key_chat_attachCheckBoxCheck = "chat_attachCheckBoxCheck";
    public static final String key_chat_attachContactBackground = "chat_attachContactBackground";
    public static final String key_chat_attachContactIcon = "chat_attachContactIcon";
    public static final String key_chat_attachContactText = "chat_attachContactText";
    public static final String key_chat_attachEmptyImage = "chat_attachEmptyImage";
    public static final String key_chat_attachFileBackground = "chat_attachFileBackground";
    public static final String key_chat_attachFileIcon = "chat_attachFileIcon";
    public static final String key_chat_attachFileText = "chat_attachFileText";
    public static final String key_chat_attachGalleryBackground = "chat_attachGalleryBackground";
    public static final String key_chat_attachGalleryIcon = "chat_attachGalleryIcon";
    public static final String key_chat_attachGalleryText = "chat_attachGalleryText";
    public static final String key_chat_attachLocationBackground = "chat_attachLocationBackground";
    public static final String key_chat_attachLocationIcon = "chat_attachLocationIcon";
    public static final String key_chat_attachLocationText = "chat_attachLocationText";
    public static final String key_chat_attachMediaBanBackground = "chat_attachMediaBanBackground";
    public static final String key_chat_attachMediaBanText = "chat_attachMediaBanText";
    public static final String key_chat_attachPermissionImage = "chat_attachPermissionImage";
    public static final String key_chat_attachPermissionMark = "chat_attachPermissionMark";
    public static final String key_chat_attachPermissionText = "chat_attachPermissionText";
    public static final String key_chat_attachPhotoBackground = "chat_attachPhotoBackground";
    public static final String key_chat_attachPollBackground = "chat_attachPollBackground";
    public static final String key_chat_attachPollIcon = "chat_attachPollIcon";
    public static final String key_chat_attachPollText = "chat_attachPollText";
    public static final String key_chat_attachUnactiveTab = "chat_attachUnactiveTab";
    public static final String key_chat_botButtonText = "chat_botButtonText";
    public static final String key_chat_botKeyboardButtonBackground = "chat_botKeyboardButtonBackground";
    public static final String key_chat_botKeyboardButtonBackgroundPressed = "chat_botKeyboardButtonBackgroundPressed";
    public static final String key_chat_botKeyboardButtonText = "chat_botKeyboardButtonText";
    public static final String key_chat_botProgress = "chat_botProgress";
    public static final String key_chat_botSwitchToInlineText = "chat_botSwitchToInlineText";
    public static final String key_chat_emojiBottomPanelIcon = "chat_emojiBottomPanelIcon";
    public static final String key_chat_emojiPanelBackground = "chat_emojiPanelBackground";
    public static final String key_chat_emojiPanelBackspace = "chat_emojiPanelBackspace";
    public static final String key_chat_emojiPanelBadgeBackground = "chat_emojiPanelBadgeBackground";
    public static final String key_chat_emojiPanelBadgeText = "chat_emojiPanelBadgeText";
    public static final String key_chat_emojiPanelEmptyText = "chat_emojiPanelEmptyText";
    public static final String key_chat_emojiPanelIcon = "chat_emojiPanelIcon";
    public static final String key_chat_emojiPanelIconSelected = "chat_emojiPanelIconSelected";
    public static final String key_chat_emojiPanelMasksIcon = "chat_emojiPanelMasksIcon";
    public static final String key_chat_emojiPanelMasksIconSelected = "chat_emojiPanelMasksIconSelected";
    public static final String key_chat_emojiPanelNewTrending = "chat_emojiPanelNewTrending";
    public static final String key_chat_emojiPanelShadowLine = "chat_emojiPanelShadowLine";
    public static final String key_chat_emojiPanelStickerPackSelector = "chat_emojiPanelStickerPackSelector";
    public static final String key_chat_emojiPanelStickerPackSelectorLine = "chat_emojiPanelStickerPackSelectorLine";
    public static final String key_chat_emojiPanelStickerSetName = "chat_emojiPanelStickerSetName";
    public static final String key_chat_emojiPanelStickerSetNameHighlight = "chat_emojiPanelStickerSetNameHighlight";
    public static final String key_chat_emojiPanelStickerSetNameIcon = "chat_emojiPanelStickerSetNameIcon";
    public static final String key_chat_emojiPanelTrendingDescription = "chat_emojiPanelTrendingDescription";
    public static final String key_chat_emojiPanelTrendingTitle = "chat_emojiPanelTrendingTitle";
    public static final String key_chat_emojiSearchBackground = "chat_emojiSearchBackground";
    public static final String key_chat_emojiSearchIcon = "chat_emojiSearchIcon";
    public static final String key_chat_fieldOverlayText = "chat_fieldOverlayText";
    public static final String key_chat_gifSaveHintBackground = "chat_gifSaveHintBackground";
    public static final String key_chat_gifSaveHintText = "chat_gifSaveHintText";
    public static final String key_chat_goDownButton = "chat_goDownButton";
    public static final String key_chat_goDownButtonCounter = "chat_goDownButtonCounter";
    public static final String key_chat_goDownButtonCounterBackground = "chat_goDownButtonCounterBackground";
    public static final String key_chat_goDownButtonIcon = "chat_goDownButtonIcon";
    public static final String key_chat_goDownButtonShadow = "chat_goDownButtonShadow";
    public static final String key_chat_inAdminSelectedText = "chat_adminSelectedText";
    public static final String key_chat_inAdminText = "chat_adminText";
    public static final String key_chat_inAudioCacheSeekbar = "chat_inAudioCacheSeekbar";
    public static final String key_chat_inAudioDurationSelectedText = "chat_inAudioDurationSelectedText";
    public static final String key_chat_inAudioDurationText = "chat_inAudioDurationText";
    public static final String key_chat_inAudioPerformerSelectedText = "chat_inAudioPerfomerSelectedText";
    public static final String key_chat_inAudioPerformerText = "chat_inAudioPerfomerText";
    public static final String key_chat_inAudioProgress = "chat_inAudioProgress";
    public static final String key_chat_inAudioSeekbar = "chat_inAudioSeekbar";
    public static final String key_chat_inAudioSeekbarFill = "chat_inAudioSeekbarFill";
    public static final String key_chat_inAudioSeekbarSelected = "chat_inAudioSeekbarSelected";
    public static final String key_chat_inAudioSelectedProgress = "chat_inAudioSelectedProgress";
    public static final String key_chat_inAudioTitleText = "chat_inAudioTitleText";
    public static final String key_chat_inBubble = "chat_inBubble";
    public static final String key_chat_inBubbleSelected = "chat_inBubbleSelected";
    public static final String key_chat_inBubbleSelectedOverlay = "chat_inBubbleSelectedOverlay";
    public static final String key_chat_inBubbleShadow = "chat_inBubbleShadow";
    public static final String key_chat_inContactBackground = "chat_inContactBackground";
    public static final String key_chat_inContactIcon = "chat_inContactIcon";
    public static final String key_chat_inContactNameText = "chat_inContactNameText";
    public static final String key_chat_inContactPhoneSelectedText = "chat_inContactPhoneSelectedText";
    public static final String key_chat_inContactPhoneText = "chat_inContactPhoneText";
    public static final String key_chat_inFileBackground = "chat_inFileBackground";
    public static final String key_chat_inFileBackgroundSelected = "chat_inFileBackgroundSelected";
    public static final String key_chat_inFileIcon = "chat_inFileIcon";
    public static final String key_chat_inFileInfoSelectedText = "chat_inFileInfoSelectedText";
    public static final String key_chat_inFileInfoText = "chat_inFileInfoText";
    public static final String key_chat_inFileNameText = "chat_inFileNameText";
    public static final String key_chat_inFileProgress = "chat_inFileProgress";
    public static final String key_chat_inFileProgressSelected = "chat_inFileProgressSelected";
    public static final String key_chat_inFileSelectedIcon = "chat_inFileSelectedIcon";
    public static final String key_chat_inForwardedNameText = "chat_inForwardedNameText";
    public static final String key_chat_inGreenCall = "chat_inDownCall";
    public static final String key_chat_inInstant = "chat_inInstant";
    public static final String key_chat_inInstantSelected = "chat_inInstantSelected";
    public static final String key_chat_inLoader = "chat_inLoader";
    public static final String key_chat_inLoaderPhoto = "chat_inLoaderPhoto";
    public static final String key_chat_inLoaderPhotoIcon = "chat_inLoaderPhotoIcon";
    public static final String key_chat_inLoaderPhotoIconSelected = "chat_inLoaderPhotoIconSelected";
    public static final String key_chat_inLoaderPhotoSelected = "chat_inLoaderPhotoSelected";
    public static final String key_chat_inLoaderSelected = "chat_inLoaderSelected";
    public static final String key_chat_inLocationBackground = "chat_inLocationBackground";
    public static final String key_chat_inLocationIcon = "chat_inLocationIcon";
    public static final String key_chat_inMediaIcon = "chat_inMediaIcon";
    public static final String key_chat_inMediaIconSelected = "chat_inMediaIconSelected";
    public static final String key_chat_inMenu = "chat_inMenu";
    public static final String key_chat_inMenuSelected = "chat_inMenuSelected";
    public static final String key_chat_inPollCorrectAnswer = "chat_inPollCorrectAnswer";
    public static final String key_chat_inPollWrongAnswer = "chat_inPollWrongAnswer";
    public static final String key_chat_inPreviewInstantSelectedText = "chat_inPreviewInstantSelectedText";
    public static final String key_chat_inPreviewInstantText = "chat_inPreviewInstantText";
    public static final String key_chat_inPreviewLine = "chat_inPreviewLine";
    public static final String key_chat_inPsaNameText = "chat_inPsaNameText";
    public static final String key_chat_inReactionButtonBackground = "chat_inReactionButtonBackground";
    public static final String key_chat_inReactionButtonText = "chat_inReactionButtonText";
    public static final String key_chat_inReactionButtonTextSelected = "chat_inReactionButtonTextSelected";
    public static final String key_chat_inRedCall = "chat_inUpCall";
    public static final String key_chat_inReplyLine = "chat_inReplyLine";
    public static final String key_chat_inReplyMediaMessageSelectedText = "chat_inReplyMediaMessageSelectedText";
    public static final String key_chat_inReplyMediaMessageText = "chat_inReplyMediaMessageText";
    public static final String key_chat_inReplyMessageText = "chat_inReplyMessageText";
    public static final String key_chat_inReplyNameText = "chat_inReplyNameText";
    public static final String key_chat_inSentClock = "chat_inSentClock";
    public static final String key_chat_inSentClockSelected = "chat_inSentClockSelected";
    public static final String key_chat_inSiteNameText = "chat_inSiteNameText";
    public static final String key_chat_inTextSelectionHighlight = "chat_inTextSelectionHighlight";
    public static final String key_chat_inTimeSelectedText = "chat_inTimeSelectedText";
    public static final String key_chat_inTimeText = "chat_inTimeText";
    public static final String key_chat_inVenueInfoSelectedText = "chat_inVenueInfoSelectedText";
    public static final String key_chat_inVenueInfoText = "chat_inVenueInfoText";
    public static final String key_chat_inViaBotNameText = "chat_inViaBotNameText";
    public static final String key_chat_inViews = "chat_inViews";
    public static final String key_chat_inViewsSelected = "chat_inViewsSelected";
    public static final String key_chat_inVoiceSeekbar = "chat_inVoiceSeekbar";
    public static final String key_chat_inVoiceSeekbarFill = "chat_inVoiceSeekbarFill";
    public static final String key_chat_inVoiceSeekbarSelected = "chat_inVoiceSeekbarSelected";
    public static final String key_chat_inlineResultIcon = "chat_inlineResultIcon";
    public static final String key_chat_linkSelectBackground = "chat_linkSelectBackground";
    public static final String key_chat_lockIcon = "chat_lockIcon";
    public static final String key_chat_mediaBroadcast = "chat_mediaBroadcast";
    public static final String key_chat_mediaInfoText = "chat_mediaInfoText";
    public static final String key_chat_mediaLoaderPhoto = "chat_mediaLoaderPhoto";
    public static final String key_chat_mediaLoaderPhotoIcon = "chat_mediaLoaderPhotoIcon";
    public static final String key_chat_mediaLoaderPhotoIconSelected = "chat_mediaLoaderPhotoIconSelected";
    public static final String key_chat_mediaLoaderPhotoSelected = "chat_mediaLoaderPhotoSelected";
    public static final String key_chat_mediaMenu = "chat_mediaMenu";
    public static final String key_chat_mediaProgress = "chat_mediaProgress";
    public static final String key_chat_mediaSentCheck = "chat_mediaSentCheck";
    public static final String key_chat_mediaSentClock = "chat_mediaSentClock";
    public static final String key_chat_mediaTimeBackground = "chat_mediaTimeBackground";
    public static final String key_chat_mediaTimeText = "chat_mediaTimeText";
    public static final String key_chat_mediaViews = "chat_mediaViews";
    public static final String key_chat_messageLinkIn = "chat_messageLinkIn";
    public static final String key_chat_messageLinkOut = "chat_messageLinkOut";
    public static final String key_chat_messagePanelBackground = "chat_messagePanelBackground";
    public static final String key_chat_messagePanelCancelInlineBot = "chat_messagePanelCancelInlineBot";
    public static final String key_chat_messagePanelCursor = "chat_messagePanelCursor";
    public static final String key_chat_messagePanelHint = "chat_messagePanelHint";
    public static final String key_chat_messagePanelIcons = "chat_messagePanelIcons";
    public static final String key_chat_messagePanelSend = "chat_messagePanelSend";
    public static final String key_chat_messagePanelShadow = "chat_messagePanelShadow";
    public static final String key_chat_messagePanelText = "chat_messagePanelText";
    public static final String key_chat_messagePanelVideoFrame = "chat_messagePanelVideoFrame";
    public static final String key_chat_messagePanelVoiceBackground = "chat_messagePanelVoiceBackground";
    public static final String key_chat_messagePanelVoiceDelete = "chat_messagePanelVoiceDelete";
    public static final String key_chat_messagePanelVoiceDuration = "chat_messagePanelVoiceDuration";
    public static final String key_chat_messagePanelVoiceLock = "key_chat_messagePanelVoiceLock";
    public static final String key_chat_messagePanelVoiceLockBackground = "key_chat_messagePanelVoiceLockBackground";
    public static final String key_chat_messagePanelVoiceLockShadow = "key_chat_messagePanelVoiceLockShadow";
    public static final String key_chat_messagePanelVoicePressed = "chat_messagePanelVoicePressed";
    public static final String key_chat_messageTextIn = "chat_messageTextIn";
    public static final String key_chat_messageTextOut = "chat_messageTextOut";
    public static final String key_chat_muteIcon = "chat_muteIcon";
    public static final String key_chat_outAdminSelectedText = "chat_outAdminSelectedText";
    public static final String key_chat_outAdminText = "chat_outAdminText";
    public static final String key_chat_outAudioCacheSeekbar = "chat_outAudioCacheSeekbar";
    public static final String key_chat_outAudioDurationSelectedText = "chat_outAudioDurationSelectedText";
    public static final String key_chat_outAudioDurationText = "chat_outAudioDurationText";
    public static final String key_chat_outAudioPerformerSelectedText = "chat_outAudioPerfomerSelectedText";
    public static final String key_chat_outAudioPerformerText = "chat_outAudioPerfomerText";
    public static final String key_chat_outAudioProgress = "chat_outAudioProgress";
    public static final String key_chat_outAudioSeekbar = "chat_outAudioSeekbar";
    public static final String key_chat_outAudioSeekbarFill = "chat_outAudioSeekbarFill";
    public static final String key_chat_outAudioSeekbarSelected = "chat_outAudioSeekbarSelected";
    public static final String key_chat_outAudioSelectedProgress = "chat_outAudioSelectedProgress";
    public static final String key_chat_outAudioTitleText = "chat_outAudioTitleText";
    public static final String key_chat_outBroadcast = "chat_outBroadcast";
    public static final String key_chat_outBubble = "chat_outBubble";
    public static final String key_chat_outBubbleGradient1 = "chat_outBubbleGradient";
    public static final String key_chat_outBubbleGradient2 = "chat_outBubbleGradient2";
    public static final String key_chat_outBubbleGradient3 = "chat_outBubbleGradient3";
    public static final String key_chat_outBubbleGradientAnimated = "chat_outBubbleGradientAnimated";
    public static final String key_chat_outBubbleGradientSelectedOverlay = "chat_outBubbleGradientSelectedOverlay";
    public static final String key_chat_outBubbleSelected = "chat_outBubbleSelected";
    public static final String key_chat_outBubbleSelectedOverlay = "chat_outBubbleSelectedOverlay";
    public static final String key_chat_outBubbleShadow = "chat_outBubbleShadow";
    public static final String key_chat_outContactBackground = "chat_outContactBackground";
    public static final String key_chat_outContactIcon = "chat_outContactIcon";
    public static final String key_chat_outContactNameText = "chat_outContactNameText";
    public static final String key_chat_outContactPhoneSelectedText = "chat_outContactPhoneSelectedText";
    public static final String key_chat_outContactPhoneText = "chat_outContactPhoneText";
    public static final String key_chat_outFileBackground = "chat_outFileBackground";
    public static final String key_chat_outFileBackgroundSelected = "chat_outFileBackgroundSelected";
    public static final String key_chat_outFileIcon = "chat_outFileIcon";
    public static final String key_chat_outFileInfoSelectedText = "chat_outFileInfoSelectedText";
    public static final String key_chat_outFileInfoText = "chat_outFileInfoText";
    public static final String key_chat_outFileNameText = "chat_outFileNameText";
    public static final String key_chat_outFileProgress = "chat_outFileProgress";
    public static final String key_chat_outFileProgressSelected = "chat_outFileProgressSelected";
    public static final String key_chat_outFileSelectedIcon = "chat_outFileSelectedIcon";
    public static final String key_chat_outForwardedNameText = "chat_outForwardedNameText";
    public static final String key_chat_outGreenCall = "chat_outUpCall";
    public static final String key_chat_outInstant = "chat_outInstant";
    public static final String key_chat_outInstantSelected = "chat_outInstantSelected";
    public static final String key_chat_outLinkSelectBackground = "chat_outLinkSelectBackground";
    public static final String key_chat_outLoader = "chat_outLoader";
    public static final String key_chat_outLoaderPhoto = "chat_outLoaderPhoto";
    public static final String key_chat_outLoaderPhotoIcon = "chat_outLoaderPhotoIcon";
    public static final String key_chat_outLoaderPhotoIconSelected = "chat_outLoaderPhotoIconSelected";
    public static final String key_chat_outLoaderPhotoSelected = "chat_outLoaderPhotoSelected";
    public static final String key_chat_outLoaderSelected = "chat_outLoaderSelected";
    public static final String key_chat_outLocationBackground = "chat_outLocationBackground";
    public static final String key_chat_outLocationIcon = "chat_outLocationIcon";
    public static final String key_chat_outMediaIcon = "chat_outMediaIcon";
    public static final String key_chat_outMediaIconSelected = "chat_outMediaIconSelected";
    public static final String key_chat_outMenu = "chat_outMenu";
    public static final String key_chat_outMenuSelected = "chat_outMenuSelected";
    public static final String key_chat_outPollCorrectAnswer = "chat_outPollCorrectAnswer";
    public static final String key_chat_outPollWrongAnswer = "chat_outPollWrongAnswer";
    public static final String key_chat_outPreviewInstantSelectedText = "chat_outPreviewInstantSelectedText";
    public static final String key_chat_outPreviewInstantText = "chat_outPreviewInstantText";
    public static final String key_chat_outPreviewLine = "chat_outPreviewLine";
    public static final String key_chat_outPsaNameText = "chat_outPsaNameText";
    public static final String key_chat_outReactionButtonBackground = "chat_outReactionButtonBackground";
    public static final String key_chat_outReactionButtonText = "chat_outReactionButtonText";
    public static final String key_chat_outReactionButtonTextSelected = "chat_outReactionButtonTextSelected";
    public static final String key_chat_outReplyLine = "chat_outReplyLine";
    public static final String key_chat_outReplyMediaMessageSelectedText = "chat_outReplyMediaMessageSelectedText";
    public static final String key_chat_outReplyMediaMessageText = "chat_outReplyMediaMessageText";
    public static final String key_chat_outReplyMessageText = "chat_outReplyMessageText";
    public static final String key_chat_outReplyNameText = "chat_outReplyNameText";
    public static final String key_chat_outSentCheck = "chat_outSentCheck";
    public static final String key_chat_outSentCheckRead = "chat_outSentCheckRead";
    public static final String key_chat_outSentCheckReadSelected = "chat_outSentCheckReadSelected";
    public static final String key_chat_outSentCheckSelected = "chat_outSentCheckSelected";
    public static final String key_chat_outSentClock = "chat_outSentClock";
    public static final String key_chat_outSentClockSelected = "chat_outSentClockSelected";
    public static final String key_chat_outSiteNameText = "chat_outSiteNameText";
    public static final String key_chat_outTextSelectionCursor = "chat_outTextSelectionCursor";
    public static final String key_chat_outTextSelectionHighlight = "chat_outTextSelectionHighlight";
    public static final String key_chat_outTimeSelectedText = "chat_outTimeSelectedText";
    public static final String key_chat_outTimeText = "chat_outTimeText";
    public static final String key_chat_outVenueInfoSelectedText = "chat_outVenueInfoSelectedText";
    public static final String key_chat_outVenueInfoText = "chat_outVenueInfoText";
    public static final String key_chat_outViaBotNameText = "chat_outViaBotNameText";
    public static final String key_chat_outViews = "chat_outViews";
    public static final String key_chat_outViewsSelected = "chat_outViewsSelected";
    public static final String key_chat_outVoiceSeekbar = "chat_outVoiceSeekbar";
    public static final String key_chat_outVoiceSeekbarFill = "chat_outVoiceSeekbarFill";
    public static final String key_chat_outVoiceSeekbarSelected = "chat_outVoiceSeekbarSelected";
    public static final String key_chat_previewDurationText = "chat_previewDurationText";
    public static final String key_chat_previewGameText = "chat_previewGameText";
    public static final String key_chat_recordTime = "chat_recordTime";
    public static final String key_chat_recordVoiceCancel = "chat_recordVoiceCancel";
    public static final String key_chat_recordedVoiceBackground = "chat_recordedVoiceBackground";
    public static final String key_chat_recordedVoiceDot = "chat_recordedVoiceDot";
    public static final String key_chat_recordedVoiceHighlight = "key_chat_recordedVoiceHighlight";
    public static final String key_chat_recordedVoicePlayPause = "chat_recordedVoicePlayPause";
    public static final String key_chat_recordedVoiceProgress = "chat_recordedVoiceProgress";
    public static final String key_chat_recordedVoiceProgressInner = "chat_recordedVoiceProgressInner";
    public static final String key_chat_replyPanelClose = "chat_replyPanelClose";
    public static final String key_chat_replyPanelIcons = "chat_replyPanelIcons";
    public static final String key_chat_replyPanelLine = "chat_replyPanelLine";
    public static final String key_chat_replyPanelMessage = "chat_replyPanelMessage";
    public static final String key_chat_replyPanelName = "chat_replyPanelName";
    public static final String key_chat_reportSpam = "chat_reportSpam";
    public static final String key_chat_searchPanelIcons = "chat_searchPanelIcons";
    public static final String key_chat_searchPanelText = "chat_searchPanelText";
    public static final String key_chat_secretChatStatusText = "chat_secretChatStatusText";
    public static final String key_chat_secretTimeText = "chat_secretTimeText";
    public static final String key_chat_secretTimerBackground = "chat_secretTimerBackground";
    public static final String key_chat_secretTimerText = "chat_secretTimerText";
    public static final String key_chat_selectedBackground = "chat_selectedBackground";
    public static final String key_chat_sentError = "chat_sentError";
    public static final String key_chat_sentErrorIcon = "chat_sentErrorIcon";
    public static final String key_chat_serviceBackground = "chat_serviceBackground";
    public static final String key_chat_serviceBackgroundSelected = "chat_serviceBackgroundSelected";
    public static final String key_chat_serviceIcon = "chat_serviceIcon";
    public static final String key_chat_serviceLink = "chat_serviceLink";
    public static final String key_chat_serviceText = "chat_serviceText";
    public static final String key_chat_status = "chat_status";
    public static final String key_chat_stickerNameText = "chat_stickerNameText";
    public static final String key_chat_stickerReplyLine = "chat_stickerReplyLine";
    public static final String key_chat_stickerReplyMessageText = "chat_stickerReplyMessageText";
    public static final String key_chat_stickerReplyNameText = "chat_stickerReplyNameText";
    public static final String key_chat_stickerViaBotNameText = "chat_stickerViaBotNameText";
    public static final String key_chat_stickersHintPanel = "chat_stickersHintPanel";
    public static final String key_chat_textSelectBackground = "chat_textSelectBackground";
    public static final String key_chat_topPanelBackground = "chat_topPanelBackground";
    public static final String key_chat_topPanelClose = "chat_topPanelClose";
    public static final String key_chat_topPanelLine = "chat_topPanelLine";
    public static final String key_chat_topPanelMessage = "chat_topPanelMessage";
    public static final String key_chat_topPanelTitle = "chat_topPanelTitle";
    public static final String key_chat_unreadMessagesStartArrowIcon = "chat_unreadMessagesStartArrowIcon";
    public static final String key_chat_unreadMessagesStartBackground = "chat_unreadMessagesStartBackground";
    public static final String key_chat_unreadMessagesStartText = "chat_unreadMessagesStartText";
    public static final String key_chat_wallpaper = "chat_wallpaper";
    public static final String key_chat_wallpaper_gradient_rotation = "chat_wallpaper_gradient_rotation";
    public static final String key_chat_wallpaper_gradient_to1 = "chat_wallpaper_gradient_to";
    public static final String key_chat_wallpaper_gradient_to2 = "key_chat_wallpaper_gradient_to2";
    public static final String key_chat_wallpaper_gradient_to3 = "key_chat_wallpaper_gradient_to3";
    public static final String key_chats_actionBackground = "chats_actionBackground";
    public static final String key_chats_actionIcon = "chats_actionIcon";
    public static final String key_chats_actionMessage = "chats_actionMessage";
    public static final String key_chats_actionPressedBackground = "chats_actionPressedBackground";
    public static final String key_chats_actionUnreadBackground = "chats_actionUnreadBackground";
    public static final String key_chats_actionUnreadIcon = "chats_actionUnreadIcon";
    public static final String key_chats_actionUnreadPressedBackground = "chats_actionUnreadPressedBackground";
    public static final String key_chats_archiveBackground = "chats_archiveBackground";
    public static final String key_chats_archiveIcon = "chats_archiveIcon";
    public static final String key_chats_archivePinBackground = "chats_archivePinBackground";
    public static final String key_chats_archivePullDownBackground = "chats_archivePullDownBackground";
    public static final String key_chats_archivePullDownBackgroundActive = "chats_archivePullDownBackgroundActive";
    public static final String key_chats_archiveText = "chats_archiveText";
    public static final String key_chats_attachMessage = "chats_attachMessage";
    public static final String key_chats_date = "chats_date";
    public static final String key_chats_draft = "chats_draft";
    public static final String key_chats_mentionIcon = "chats_mentionIcon";
    public static final String key_chats_menuBackground = "chats_menuBackground";
    public static final String key_chats_menuCloud = "chats_menuCloud";
    public static final String key_chats_menuCloudBackgroundCats = "chats_menuCloudBackgroundCats";
    public static final String key_chats_menuItemCheck = "chats_menuItemCheck";
    public static final String key_chats_menuItemIcon = "chats_menuItemIcon";
    public static final String key_chats_menuItemText = "chats_menuItemText";
    public static final String key_chats_menuName = "chats_menuName";
    public static final String key_chats_menuPhone = "chats_menuPhone";
    public static final String key_chats_menuPhoneCats = "chats_menuPhoneCats";
    public static final String key_chats_menuTopBackground = "chats_menuTopBackground";
    public static final String key_chats_menuTopBackgroundCats = "chats_menuTopBackgroundCats";
    public static final String key_chats_menuTopShadow = "chats_menuTopShadow";
    public static final String key_chats_menuTopShadowCats = "chats_menuTopShadowCats";
    public static final String key_chats_message = "chats_message";
    public static final String key_chats_messageArchived = "chats_messageArchived";
    public static final String key_chats_message_threeLines = "chats_message_threeLines";
    public static final String key_chats_muteIcon = "chats_muteIcon";
    public static final String key_chats_name = "chats_name";
    public static final String key_chats_nameArchived = "chats_nameArchived";
    public static final String key_chats_nameIcon = "chats_nameIcon";
    public static final String key_chats_nameMessage = "chats_nameMessage";
    public static final String key_chats_nameMessageArchived = "chats_nameMessageArchived";
    public static final String key_chats_nameMessageArchived_threeLines = "chats_nameMessageArchived_threeLines";
    public static final String key_chats_nameMessage_threeLines = "chats_nameMessage_threeLines";
    public static final String key_chats_onlineCircle = "chats_onlineCircle";
    public static final String key_chats_pinnedIcon = "chats_pinnedIcon";
    public static final String key_chats_pinnedOverlay = "chats_pinnedOverlay";
    public static final String key_chats_secretIcon = "chats_secretIcon";
    public static final String key_chats_secretName = "chats_secretName";
    public static final String key_chats_sentCheck = "chats_sentCheck";
    public static final String key_chats_sentClock = "chats_sentClock";
    public static final String key_chats_sentError = "chats_sentError";
    public static final String key_chats_sentErrorIcon = "chats_sentErrorIcon";
    public static final String key_chats_sentReadCheck = "chats_sentReadCheck";
    public static final String key_chats_tabUnreadActiveBackground = "chats_tabUnreadActiveBackground";
    public static final String key_chats_tabUnreadUnactiveBackground = "chats_tabUnreadUnactiveBackground";
    public static final String key_chats_tabletSelectedOverlay = "chats_tabletSelectedOverlay";
    public static final String key_chats_unreadCounter = "chats_unreadCounter";
    public static final String key_chats_unreadCounterMuted = "chats_unreadCounterMuted";
    public static final String key_chats_unreadCounterText = "chats_unreadCounterText";
    public static final String key_chats_verifiedBackground = "chats_verifiedBackground";
    public static final String key_chats_verifiedCheck = "chats_verifiedCheck";
    public static final String key_checkbox = "checkbox";
    public static final String key_checkboxCheck = "checkboxCheck";
    public static final String key_checkboxDisabled = "checkboxDisabled";
    public static final String key_checkboxSquareBackground = "checkboxSquareBackground";
    public static final String key_checkboxSquareCheck = "checkboxSquareCheck";
    public static final String key_checkboxSquareDisabled = "checkboxSquareDisabled";
    public static final String key_checkboxSquareUnchecked = "checkboxSquareUnchecked";
    public static final String key_contacts_inviteBackground = "contacts_inviteBackground";
    public static final String key_contacts_inviteText = "contacts_inviteText";
    public static final String key_contextProgressInner1 = "contextProgressInner1";
    public static final String key_contextProgressInner2 = "contextProgressInner2";
    public static final String key_contextProgressInner3 = "contextProgressInner3";
    public static final String key_contextProgressInner4 = "contextProgressInner4";
    public static final String key_contextProgressOuter1 = "contextProgressOuter1";
    public static final String key_contextProgressOuter2 = "contextProgressOuter2";
    public static final String key_contextProgressOuter3 = "contextProgressOuter3";
    public static final String key_contextProgressOuter4 = "contextProgressOuter4";
    public static final String key_dialogBackground = "dialogBackground";
    public static final String key_dialogBackgroundGray = "dialogBackgroundGray";
    public static final String key_dialogBadgeBackground = "dialogBadgeBackground";
    public static final String key_dialogBadgeText = "dialogBadgeText";
    public static final String key_dialogButton = "dialogButton";
    public static final String key_dialogButtonSelector = "dialogButtonSelector";
    public static final String key_dialogCameraIcon = "dialogCameraIcon";
    public static final String key_dialogCheckboxSquareBackground = "dialogCheckboxSquareBackground";
    public static final String key_dialogCheckboxSquareCheck = "dialogCheckboxSquareCheck";
    public static final String key_dialogCheckboxSquareDisabled = "dialogCheckboxSquareDisabled";
    public static final String key_dialogCheckboxSquareUnchecked = "dialogCheckboxSquareUnchecked";
    public static final String key_dialogEmptyImage = "dialogEmptyImage";
    public static final String key_dialogEmptyText = "dialogEmptyText";
    public static final String key_dialogFloatingButton = "dialogFloatingButton";
    public static final String key_dialogFloatingButtonPressed = "dialogFloatingButtonPressed";
    public static final String key_dialogFloatingIcon = "dialogFloatingIcon";
    public static final String key_dialogGrayLine = "dialogGrayLine";
    public static final String key_dialogIcon = "dialogIcon";
    public static final String key_dialogInputField = "dialogInputField";
    public static final String key_dialogInputFieldActivated = "dialogInputFieldActivated";
    public static final String key_dialogLineProgress = "dialogLineProgress";
    public static final String key_dialogLineProgressBackground = "dialogLineProgressBackground";
    public static final String key_dialogLinkSelection = "dialogLinkSelection";
    public static final String key_dialogProgressCircle = "dialogProgressCircle";
    public static final String key_dialogRadioBackground = "dialogRadioBackground";
    public static final String key_dialogRadioBackgroundChecked = "dialogRadioBackgroundChecked";
    public static final String key_dialogReactionMentionBackground = "dialogReactionMentionBackground";
    public static final String key_dialogRedIcon = "dialogRedIcon";
    public static final String key_dialogRoundCheckBox = "dialogRoundCheckBox";
    public static final String key_dialogRoundCheckBoxCheck = "dialogRoundCheckBoxCheck";
    public static final String key_dialogScrollGlow = "dialogScrollGlow";
    public static final String key_dialogSearchBackground = "dialogSearchBackground";
    public static final String key_dialogSearchHint = "dialogSearchHint";
    public static final String key_dialogSearchIcon = "dialogSearchIcon";
    public static final String key_dialogSearchText = "dialogSearchText";
    public static final String key_dialogShadowLine = "dialogShadowLine";
    public static final String key_dialogSwipeRemove = "dialogSwipeRemove";
    public static final String key_dialogTextBlack = "dialogTextBlack";
    public static final String key_dialogTextBlue = "dialogTextBlue";
    public static final String key_dialogTextBlue2 = "dialogTextBlue2";
    public static final String key_dialogTextBlue3 = "dialogTextBlue3";
    public static final String key_dialogTextBlue4 = "dialogTextBlue4";
    public static final String key_dialogTextGray = "dialogTextGray";
    public static final String key_dialogTextGray2 = "dialogTextGray2";
    public static final String key_dialogTextGray3 = "dialogTextGray3";
    public static final String key_dialogTextGray4 = "dialogTextGray4";
    public static final String key_dialogTextHint = "dialogTextHint";
    public static final String key_dialogTextLink = "dialogTextLink";
    public static final String key_dialogTextRed = "dialogTextRed";
    public static final String key_dialogTextRed2 = "dialogTextRed2";
    public static final String key_dialogTopBackground = "dialogTopBackground";
    public static final String key_dialog_inlineProgress = "dialog_inlineProgress";
    public static final String key_dialog_inlineProgressBackground = "dialog_inlineProgressBackground";
    public static final String key_dialog_liveLocationProgress = "dialog_liveLocationProgress";
    public static final String key_divider = "divider";
    public static final String key_drawable_botInline = "drawableBotInline";
    public static final String key_drawable_botInvite = "drawable_botInvite";
    public static final String key_drawable_botLink = "drawableBotLink";
    public static final String key_drawable_botWebView = "drawableBotWebView";
    public static final String key_drawable_chat_pollHintDrawableIn = "drawable_chat_pollHintDrawableIn";
    public static final String key_drawable_chat_pollHintDrawableOut = "drawable_chat_pollHintDrawableOut";
    public static final String key_drawable_commentSticker = "drawableCommentSticker";
    public static final String key_drawable_goIcon = "drawableGoIcon";
    public static final String key_drawable_lockIconDrawable = "drawableLockIcon";
    public static final String key_drawable_msgError = "drawableMsgError";
    public static final String key_drawable_msgIn = "drawableMsgIn";
    public static final String key_drawable_msgInClock = "drawableMsgInClock";
    public static final String key_drawable_msgInClockSelected = "drawableMsgInClockSelected";
    public static final String key_drawable_msgInMedia = "drawableMsgInMedia";
    public static final String key_drawable_msgInMediaSelected = "drawableMsgInMediaSelected";
    public static final String key_drawable_msgInSelected = "drawableMsgInSelected";
    public static final String key_drawable_msgOut = "drawableMsgOut";
    public static final String key_drawable_msgOutCallAudio = "drawableMsgOutCallAudio";
    public static final String key_drawable_msgOutCallAudioSelected = "drawableMsgOutCallAudioSelected";
    public static final String key_drawable_msgOutCallVideo = "drawableMsgOutCallVideo";
    public static final String key_drawable_msgOutCallVideoSelected = "drawableMsgOutCallVideo";
    public static final String key_drawable_msgOutCheck = "drawableMsgOutCheck";
    public static final String key_drawable_msgOutCheckRead = "drawableMsgOutCheckRead";
    public static final String key_drawable_msgOutCheckReadSelected = "drawableMsgOutCheckReadSelected";
    public static final String key_drawable_msgOutCheckSelected = "drawableMsgOutCheckSelected";
    public static final String key_drawable_msgOutHalfCheck = "drawableMsgOutHalfCheck";
    public static final String key_drawable_msgOutHalfCheckSelected = "drawableMsgOutHalfCheckSelected";
    public static final String key_drawable_msgOutInstant = "drawableMsgOutInstant";
    public static final String key_drawable_msgOutMedia = "drawableMsgOutMedia";
    public static final String key_drawable_msgOutMediaSelected = "drawableMsgOutMediaSelected";
    public static final String key_drawable_msgOutMenu = "drawableMsgOutMenu";
    public static final String key_drawable_msgOutMenuSelected = "drawableMsgOutMenuSelected";
    public static final String key_drawable_msgOutPinned = "drawableMsgOutPinned";
    public static final String key_drawable_msgOutPinnedSelected = "drawableMsgOutPinnedSelected";
    public static final String key_drawable_msgOutReplies = "drawableMsgOutReplies";
    public static final String key_drawable_msgOutRepliesSelected = "drawableMsgOutReplies";
    public static final String key_drawable_msgOutSelected = "drawableMsgOutSelected";
    public static final String key_drawable_msgOutViews = "drawableMsgOutViews";
    public static final String key_drawable_msgOutViewsSelected = "drawableMsgOutViewsSelected";
    public static final String key_drawable_msgStickerCheck = "drawableMsgStickerCheck";
    public static final String key_drawable_msgStickerClock = "drawableMsgStickerClock";
    public static final String key_drawable_msgStickerHalfCheck = "drawableMsgStickerHalfCheck";
    public static final String key_drawable_msgStickerPinned = "drawableMsgStickerPinned";
    public static final String key_drawable_msgStickerReplies = "drawableMsgStickerReplies";
    public static final String key_drawable_msgStickerViews = "drawableMsgStickerViews";
    public static final String key_drawable_muteIconDrawable = "drawableMuteIcon";
    public static final String key_drawable_replyIcon = "drawableReplyIcon";
    public static final String key_drawable_shareIcon = "drawableShareIcon";
    public static final String key_emptyListPlaceholder = "emptyListPlaceholder";
    public static final String key_fastScrollActive = "fastScrollActive";
    public static final String key_fastScrollInactive = "fastScrollInactive";
    public static final String key_fastScrollText = "fastScrollText";
    public static final String key_featuredStickers_addButton = "featuredStickers_addButton";
    public static final String key_featuredStickers_addButtonPressed = "featuredStickers_addButtonPressed";
    public static final String key_featuredStickers_addedIcon = "featuredStickers_addedIcon";
    public static final String key_featuredStickers_buttonProgress = "featuredStickers_buttonProgress";
    public static final String key_featuredStickers_buttonText = "featuredStickers_buttonText";
    public static final String key_featuredStickers_removeButtonText = "featuredStickers_removeButtonText";
    public static final String key_featuredStickers_unread = "featuredStickers_unread";
    public static final String key_files_folderIcon = "files_folderIcon";
    public static final String key_files_folderIconBackground = "files_folderIconBackground";
    public static final String key_files_iconText = "files_iconText";
    public static final String key_graySection = "graySection";
    public static final String key_graySectionText = "key_graySectionText";
    public static final String key_groupcreate_cursor = "groupcreate_cursor";
    public static final String key_groupcreate_hintText = "groupcreate_hintText";
    public static final String key_groupcreate_sectionShadow = "groupcreate_sectionShadow";
    public static final String key_groupcreate_sectionText = "groupcreate_sectionText";
    public static final String key_groupcreate_spanBackground = "groupcreate_spanBackground";
    public static final String key_groupcreate_spanDelete = "groupcreate_spanDelete";
    public static final String key_groupcreate_spanText = "groupcreate_spanText";
    public static final String key_inappPlayerBackground = "inappPlayerBackground";
    public static final String key_inappPlayerClose = "inappPlayerClose";
    public static final String key_inappPlayerPerformer = "inappPlayerPerformer";
    public static final String key_inappPlayerPlayPause = "inappPlayerPlayPause";
    public static final String key_inappPlayerTitle = "inappPlayerTitle";
    public static final String key_listSelector = "listSelectorSDK21";
    public static final String key_location_actionActiveIcon = "location_actionActiveIcon";
    public static final String key_location_actionBackground = "location_actionBackground";
    public static final String key_location_actionIcon = "location_actionIcon";
    public static final String key_location_actionPressedBackground = "location_actionPressedBackground";
    public static final String key_location_liveLocationProgress = "location_liveLocationProgress";
    public static final String key_location_placeLocationBackground = "location_placeLocationBackground";
    public static final String key_location_sendLiveLocationBackground = "location_sendLiveLocationBackground";
    public static final String key_location_sendLiveLocationIcon = "location_sendLiveLocationIcon";
    public static final String key_location_sendLiveLocationText = "location_sendLiveLocationText";
    public static final String key_location_sendLocationBackground = "location_sendLocationBackground";
    public static final String key_location_sendLocationIcon = "location_sendLocationIcon";
    public static final String key_location_sendLocationText = "location_sendLocationText";
    public static final String key_login_progressInner = "login_progressInner";
    public static final String key_login_progressOuter = "login_progressOuter";
    public static final String key_musicPicker_buttonBackground = "musicPicker_buttonBackground";
    public static final String key_musicPicker_buttonIcon = "musicPicker_buttonIcon";
    public static final String key_musicPicker_checkbox = "musicPicker_checkbox";
    public static final String key_musicPicker_checkboxCheck = "musicPicker_checkboxCheck";
    public static final String key_paint_chatActionBackground = "paintChatActionBackground";
    public static final String key_paint_chatActionBackgroundSelected = "paintChatActionBackgroundSelected";
    public static final String key_paint_chatActionText = "paintChatActionText";
    public static final String key_paint_chatBotButton = "paintChatBotButton";
    public static final String key_paint_chatComposeBackground = "paintChatComposeBackground";
    public static final String key_paint_chatMessageBackgroundSelected = "paintChatMessageBackgroundSelected";
    public static final String key_paint_chatTimeBackground = "paintChatTimeBackground";
    public static final String key_passport_authorizeBackground = "passport_authorizeBackground";
    public static final String key_passport_authorizeBackgroundSelected = "passport_authorizeBackgroundSelected";
    public static final String key_passport_authorizeText = "passport_authorizeText";
    public static final String key_picker_badge = "picker_badge";
    public static final String key_picker_badgeText = "picker_badgeText";
    public static final String key_picker_disabledButton = "picker_disabledButton";
    public static final String key_picker_enabledButton = "picker_enabledButton";
    public static final String key_player_actionBar = "player_actionBar";
    public static final String key_player_actionBarItems = "player_actionBarItems";
    public static final String key_player_actionBarSelector = "player_actionBarSelector";
    public static final String key_player_actionBarSubtitle = "player_actionBarSubtitle";
    public static final String key_player_actionBarTitle = "player_actionBarTitle";
    public static final String key_player_actionBarTop = "player_actionBarTop";
    public static final String key_player_background = "player_background";
    public static final String key_player_button = "player_button";
    public static final String key_player_buttonActive = "player_buttonActive";
    public static final String key_player_progress = "player_progress";
    public static final String key_player_progressBackground = "player_progressBackground";
    public static final String key_player_progressBackground2 = "player_progressBackground2";
    public static final String key_player_progressCachedBackground = "key_player_progressCachedBackground";
    public static final String key_player_time = "player_time";
    public static final String key_premiumGradient1 = "premiumGradient1";
    public static final String key_premiumGradient2 = "premiumGradient2";
    public static final String key_premiumGradient3 = "premiumGradient3";
    public static final String key_premiumGradient4 = "premiumGradient4";
    public static final String key_premiumGradientBackground1 = "premiumGradientBackground1";
    public static final String key_premiumGradientBackground2 = "premiumGradientBackground2";
    public static final String key_premiumGradientBackground3 = "premiumGradientBackground3";
    public static final String key_premiumGradientBackground4 = "premiumGradientBackground4";
    public static final String key_premiumGradientBackgroundOverlay = "premiumGradientBackgroundOverlay";
    public static final String key_premiumGradientBottomSheet1 = "premiumGradientBottomSheet1";
    public static final String key_premiumGradientBottomSheet2 = "premiumGradientBottomSheet2";
    public static final String key_premiumGradientBottomSheet3 = "premiumGradientBottomSheet3";
    public static final String key_premiumStartGradient1 = "premiumStarGradient1";
    public static final String key_premiumStartGradient2 = "premiumStarGradient2";
    public static final String key_premiumStartSmallStarsColor = "premiumStartSmallStarsColor";
    public static final String key_premiumStartSmallStarsColor2 = "premiumStartSmallStarsColor2";
    public static final String key_profile_actionBackground = "profile_actionBackground";
    public static final String key_profile_actionIcon = "profile_actionIcon";
    public static final String key_profile_actionPressedBackground = "profile_actionPressedBackground";
    public static final String key_profile_creatorIcon = "profile_creatorIcon";
    public static final String key_profile_status = "profile_status";
    public static final String key_profile_tabSelectedLine = "profile_tabSelectedLine";
    public static final String key_profile_tabSelectedText = "profile_tabSelectedText";
    public static final String key_profile_tabSelector = "profile_tabSelector";
    public static final String key_profile_tabText = "profile_tabText";
    public static final String key_profile_title = "profile_title";
    public static final String key_profile_verifiedBackground = "profile_verifiedBackground";
    public static final String key_profile_verifiedCheck = "profile_verifiedCheck";
    public static final String key_progressCircle = "progressCircle";
    public static final String key_radioBackground = "radioBackground";
    public static final String key_radioBackgroundChecked = "radioBackgroundChecked";
    public static final String key_returnToCallBackground = "returnToCallBackground";
    public static final String key_returnToCallMutedBackground = "returnToCallMutedBackground";
    public static final String key_returnToCallText = "returnToCallText";
    public static final String key_sessions_devicesImage = "sessions_devicesImage";
    public static final String key_sharedMedia_actionMode = "sharedMedia_actionMode";
    public static final String key_sharedMedia_linkPlaceholder = "sharedMedia_linkPlaceholder";
    public static final String key_sharedMedia_linkPlaceholderText = "sharedMedia_linkPlaceholderText";
    public static final String key_sharedMedia_photoPlaceholder = "sharedMedia_photoPlaceholder";
    public static final String key_sharedMedia_startStopLoadIcon = "sharedMedia_startStopLoadIcon";
    public static final String key_sheet_other = "key_sheet_other";
    public static final String key_sheet_scrollUp = "key_sheet_scrollUp";
    public static final String key_statisticChartActiveLine = "statisticChartActiveLine";
    public static final String key_statisticChartActivePickerChart = "statisticChartActivePickerChart";
    public static final String key_statisticChartBackZoomColor = "statisticChartBackZoomColor";
    public static final String key_statisticChartCheckboxInactive = "statisticChartCheckboxInactive";
    public static final String key_statisticChartChevronColor = "statisticChartChevronColor";
    public static final String key_statisticChartHighlightColor = "statisticChartHighlightColor";
    public static final String key_statisticChartHintLine = "statisticChartHintLine";
    public static final String key_statisticChartInactivePickerChart = "statisticChartInactivePickerChart";
    public static final String key_statisticChartLineEmpty = "statisticChartLineEmpty";
    public static final String key_statisticChartLine_blue = "statisticChartLine_blue";
    public static final String key_statisticChartLine_golden = "statisticChartLine_golden";
    public static final String key_statisticChartLine_green = "statisticChartLine_green";
    public static final String key_statisticChartLine_indigo = "statisticChartLine_indigo";
    public static final String key_statisticChartLine_lightblue = "statisticChartLine_lightblue";
    public static final String key_statisticChartLine_lightgreen = "statisticChartLine_lightgreen";
    public static final String key_statisticChartLine_orange = "statisticChartLine_orange";
    public static final String key_statisticChartLine_red = "statisticChartLine_red";
    public static final String key_statisticChartNightIconColor = "statisticChartNightIconColor";
    public static final String key_statisticChartPopupBackground = "statisticChartPopupBackground";
    public static final String key_statisticChartRipple = "statisticChartRipple";
    public static final String key_statisticChartSignature = "statisticChartSignature";
    public static final String key_statisticChartSignatureAlpha = "statisticChartSignatureAlpha";
    public static final String key_stickers_menu = "stickers_menu";
    public static final String key_stickers_menuSelector = "stickers_menuSelector";
    public static final String key_switch2Track = "switch2Track";
    public static final String key_switch2TrackChecked = "switch2TrackChecked";
    public static final String key_switchTrack = "switchTrack";
    public static final String key_switchTrackBlue = "switchTrackBlue";
    public static final String key_switchTrackBlueChecked = "switchTrackBlueChecked";
    public static final String key_switchTrackBlueSelector = "switchTrackBlueSelector";
    public static final String key_switchTrackBlueSelectorChecked = "switchTrackBlueSelectorChecked";
    public static final String key_switchTrackBlueThumb = "switchTrackBlueThumb";
    public static final String key_switchTrackBlueThumbChecked = "switchTrackBlueThumbChecked";
    public static final String key_switchTrackChecked = "switchTrackChecked";
    public static final String key_undo_background = "undo_background";
    public static final String key_undo_cancelColor = "undo_cancelColor";
    public static final String key_undo_infoColor = "undo_infoColor";
    public static final String key_voipgroup_actionBar = "voipgroup_actionBar";
    public static final String key_voipgroup_actionBarItems = "voipgroup_actionBarItems";
    public static final String key_voipgroup_actionBarItemsSelector = "voipgroup_actionBarItemsSelector";
    public static final String key_voipgroup_actionBarSubtitle = "voipgroup_actionBarSubtitle";
    public static final String key_voipgroup_actionBarUnscrolled = "voipgroup_actionBarUnscrolled";
    public static final String key_voipgroup_blueText = "voipgroup_blueText";
    public static final String key_voipgroup_checkMenu = "voipgroup_checkMenu";
    public static final String key_voipgroup_connectingProgress = "voipgroup_connectingProgress";
    public static final String key_voipgroup_dialogBackground = "voipgroup_dialogBackground";
    public static final String key_voipgroup_disabledButton = "voipgroup_disabledButton";
    public static final String key_voipgroup_disabledButtonActive = "voipgroup_disabledButtonActive";
    public static final String key_voipgroup_disabledButtonActiveScrolled = "voipgroup_disabledButtonActiveScrolled";
    public static final String key_voipgroup_emptyView = "voipgroup_emptyView";
    public static final String key_voipgroup_inviteMembersBackground = "voipgroup_inviteMembersBackground";
    public static final String key_voipgroup_lastSeenText = "voipgroup_lastSeenText";
    public static final String key_voipgroup_lastSeenTextUnscrolled = "voipgroup_lastSeenTextUnscrolled";
    public static final String key_voipgroup_leaveButton = "voipgroup_leaveButton";
    public static final String key_voipgroup_leaveButtonScrolled = "voipgroup_leaveButtonScrolled";
    public static final String key_voipgroup_leaveCallMenu = "voipgroup_leaveCallMenu";
    public static final String key_voipgroup_listSelector = "voipgroup_listSelector";
    public static final String key_voipgroup_listViewBackground = "voipgroup_listViewBackground";
    public static final String key_voipgroup_listViewBackgroundUnscrolled = "voipgroup_listViewBackgroundUnscrolled";
    public static final String key_voipgroup_listeningText = "voipgroup_listeningText";
    public static final String key_voipgroup_muteButton = "voipgroup_muteButton";
    public static final String key_voipgroup_muteButton2 = "voipgroup_muteButton2";
    public static final String key_voipgroup_muteButton3 = "voipgroup_muteButton3";
    public static final String key_voipgroup_mutedByAdminGradient = "voipgroup_mutedByAdminGradient";
    public static final String key_voipgroup_mutedByAdminGradient2 = "voipgroup_mutedByAdminGradient2";
    public static final String key_voipgroup_mutedByAdminGradient3 = "voipgroup_mutedByAdminGradient3";
    public static final String key_voipgroup_mutedByAdminIcon = "voipgroup_mutedByAdminIcon";
    public static final String key_voipgroup_mutedByAdminMuteButton = "voipgroup_mutedByAdminMuteButton";
    public static final String key_voipgroup_mutedByAdminMuteButtonDisabled = "voipgroup_mutedByAdminMuteButtonDisabled";
    public static final String key_voipgroup_mutedIcon = "voipgroup_mutedIcon";
    public static final String key_voipgroup_mutedIconUnscrolled = "voipgroup_mutedIconUnscrolled";
    public static final String key_voipgroup_nameText = "voipgroup_nameText";
    public static final String key_voipgroup_overlayAlertGradientMuted = "voipgroup_overlayAlertGradientMuted";
    public static final String key_voipgroup_overlayAlertGradientMuted2 = "voipgroup_overlayAlertGradientMuted2";
    public static final String key_voipgroup_overlayAlertGradientUnmuted = "voipgroup_overlayAlertGradientUnmuted";
    public static final String key_voipgroup_overlayAlertGradientUnmuted2 = "voipgroup_overlayAlertGradientUnmuted2";
    public static final String key_voipgroup_overlayAlertMutedByAdmin = "voipgroup_overlayAlertMutedByAdmin";
    public static final String key_voipgroup_overlayAlertMutedByAdmin2 = "kvoipgroup_overlayAlertMutedByAdmin2";
    public static final String key_voipgroup_overlayBlue1 = "voipgroup_overlayBlue1";
    public static final String key_voipgroup_overlayBlue2 = "voipgroup_overlayBlue2";
    public static final String key_voipgroup_overlayGreen1 = "voipgroup_overlayGreen1";
    public static final String key_voipgroup_overlayGreen2 = "voipgroup_overlayGreen2";
    public static final String key_voipgroup_scrollUp = "voipgroup_scrollUp";
    public static final String key_voipgroup_searchBackground = "voipgroup_searchBackground";
    public static final String key_voipgroup_searchPlaceholder = "voipgroup_searchPlaceholder";
    public static final String key_voipgroup_searchText = "voipgroup_searchText";
    public static final String key_voipgroup_soundButton = "voipgroup_soundButton";
    public static final String key_voipgroup_soundButton2 = "voipgroup_soundButton2";
    public static final String key_voipgroup_soundButtonActive = "voipgroup_soundButtonActive";
    public static final String key_voipgroup_soundButtonActive2 = "voipgroup_soundButtonActive2";
    public static final String key_voipgroup_soundButtonActive2Scrolled = "voipgroup_soundButtonActive2Scrolled";
    public static final String key_voipgroup_soundButtonActiveScrolled = "voipgroup_soundButtonActiveScrolled";
    public static final String key_voipgroup_speakingText = "voipgroup_speakingText";
    public static final String key_voipgroup_topPanelBlue1 = "voipgroup_topPanelBlue1";
    public static final String key_voipgroup_topPanelBlue2 = "voipgroup_topPanelBlue2";
    public static final String key_voipgroup_topPanelGray = "voipgroup_topPanelGray";
    public static final String key_voipgroup_topPanelGreen1 = "voipgroup_topPanelGreen1";
    public static final String key_voipgroup_topPanelGreen2 = "voipgroup_topPanelGreen2";
    public static final String key_voipgroup_unmuteButton = "voipgroup_unmuteButton";
    public static final String key_voipgroup_unmuteButton2 = "voipgroup_unmuteButton2";
    public static final String key_voipgroup_windowBackgroundWhiteInputField = "voipgroup_windowBackgroundWhiteInputField";
    public static final String key_voipgroup_windowBackgroundWhiteInputFieldActivated = "voipgroup_windowBackgroundWhiteInputFieldActivated";
    public static final String key_wallet_addressConfirmBackground = "wallet_addressConfirmBackground";
    public static final String key_wallet_blackBackground = "wallet_blackBackground";
    public static final String key_wallet_blackBackgroundSelector = "wallet_blackBackgroundSelector";
    public static final String key_wallet_blackText = "wallet_blackText";
    public static final String key_wallet_buttonBackground = "wallet_buttonBackground";
    public static final String key_wallet_buttonPressedBackground = "wallet_buttonPressedBackground";
    public static final String key_wallet_buttonText = "wallet_buttonText";
    public static final String key_wallet_commentText = "wallet_commentText";
    public static final String key_wallet_dateText = "wallet_dateText";
    public static final String key_wallet_grayBackground = "wallet_grayBackground";
    public static final String key_wallet_graySettingsBackground = "wallet_graySettingsBackground";
    public static final String key_wallet_grayText = "wallet_grayText";
    public static final String key_wallet_grayText2 = "wallet_grayText2";
    public static final String key_wallet_greenText = "wallet_greenText";
    public static final String key_wallet_pullBackground = "wallet_pullBackground";
    public static final String key_wallet_redText = "wallet_redText";
    public static final String key_wallet_releaseBackground = "wallet_releaseBackground";
    public static final String key_wallet_statusText = "wallet_statusText";
    public static final String key_wallet_whiteBackground = "wallet_whiteBackground";
    public static final String key_wallet_whiteText = "wallet_whiteText";
    public static final String key_windowBackgroundCheckText = "windowBackgroundCheckText";
    public static final String key_windowBackgroundChecked = "windowBackgroundChecked";
    public static final String key_windowBackgroundGray = "windowBackgroundGray";
    public static final String key_windowBackgroundGrayShadow = "windowBackgroundGrayShadow";
    public static final String key_windowBackgroundUnchecked = "windowBackgroundUnchecked";
    public static final String key_windowBackgroundWhite = "windowBackgroundWhite";
    public static final String key_windowBackgroundWhiteBlackText = "windowBackgroundWhiteBlackText";
    public static final String key_windowBackgroundWhiteBlueButton = "windowBackgroundWhiteBlueButton";
    public static final String key_windowBackgroundWhiteBlueHeader = "windowBackgroundWhiteBlueHeader";
    public static final String key_windowBackgroundWhiteBlueIcon = "windowBackgroundWhiteBlueIcon";
    public static final String key_windowBackgroundWhiteBlueText = "windowBackgroundWhiteBlueText";
    public static final String key_windowBackgroundWhiteBlueText2 = "windowBackgroundWhiteBlueText2";
    public static final String key_windowBackgroundWhiteBlueText3 = "windowBackgroundWhiteBlueText3";
    public static final String key_windowBackgroundWhiteBlueText4 = "windowBackgroundWhiteBlueText4";
    public static final String key_windowBackgroundWhiteBlueText5 = "windowBackgroundWhiteBlueText5";
    public static final String key_windowBackgroundWhiteBlueText6 = "windowBackgroundWhiteBlueText6";
    public static final String key_windowBackgroundWhiteBlueText7 = "windowBackgroundWhiteBlueText7";
    public static final String key_windowBackgroundWhiteGrayIcon = "windowBackgroundWhiteGrayIcon";
    public static final String key_windowBackgroundWhiteGrayLine = "windowBackgroundWhiteGrayLine";
    public static final String key_windowBackgroundWhiteGrayText = "windowBackgroundWhiteGrayText";
    public static final String key_windowBackgroundWhiteGrayText2 = "windowBackgroundWhiteGrayText2";
    public static final String key_windowBackgroundWhiteGrayText3 = "windowBackgroundWhiteGrayText3";
    public static final String key_windowBackgroundWhiteGrayText4 = "windowBackgroundWhiteGrayText4";
    public static final String key_windowBackgroundWhiteGrayText5 = "windowBackgroundWhiteGrayText5";
    public static final String key_windowBackgroundWhiteGrayText6 = "windowBackgroundWhiteGrayText6";
    public static final String key_windowBackgroundWhiteGrayText7 = "windowBackgroundWhiteGrayText7";
    public static final String key_windowBackgroundWhiteGrayText8 = "windowBackgroundWhiteGrayText8";
    public static final String key_windowBackgroundWhiteGreenText = "windowBackgroundWhiteGreenText";
    public static final String key_windowBackgroundWhiteGreenText2 = "windowBackgroundWhiteGreenText2";
    public static final String key_windowBackgroundWhiteHintText = "windowBackgroundWhiteHintText";
    public static final String key_windowBackgroundWhiteInputField = "windowBackgroundWhiteInputField";
    public static final String key_windowBackgroundWhiteInputFieldActivated = "windowBackgroundWhiteInputFieldActivated";
    public static final String key_windowBackgroundWhiteLinkSelection = "windowBackgroundWhiteLinkSelection";
    public static final String key_windowBackgroundWhiteLinkText = "windowBackgroundWhiteLinkText";
    public static final String key_windowBackgroundWhiteRedText = "windowBackgroundWhiteRedText";
    public static final String key_windowBackgroundWhiteRedText2 = "windowBackgroundWhiteRedText2";
    public static final String key_windowBackgroundWhiteRedText3 = "windowBackgroundWhiteRedText3";
    public static final String key_windowBackgroundWhiteRedText4 = "windowBackgroundWhiteRedText4";
    public static final String key_windowBackgroundWhiteRedText5 = "windowBackgroundWhiteRedText5";
    public static final String key_windowBackgroundWhiteRedText6 = "windowBackgroundWhiteRedText6";
    public static final String key_windowBackgroundWhiteValueText = "windowBackgroundWhiteValueText";
    private static long lastDelayUpdateTime;
    private static long lastHolidayCheckTime;
    private static int lastLoadingCurrentThemeTime;
    private static long lastThemeSwitchTime;
    private static Sensor lightSensor;
    private static boolean lightSensorRegistered;
    public static Paint linkSelectionPaint;
    public static Drawable listSelector;
    private static int loadingCurrentTheme;
    public static Drawable moveUpDrawable;
    private static int patternIntensity;
    public static PathAnimator playPauseAnimator;
    private static int previousPhase;
    private static ThemeInfo previousTheme;
    public static TextPaint profile_aboutTextPaint;
    public static Drawable profile_verifiedCheckDrawable;
    public static Drawable profile_verifiedDrawable;
    private static RoundVideoProgressShadow roundPlayDrawable;
    public static int selectedAutoNightType;
    private static SensorManager sensorManager;
    private static Bitmap serviceBitmap;
    private static Matrix serviceBitmapMatrix;
    public static BitmapShader serviceBitmapShader;
    private static int serviceMessage2Color;
    private static int serviceMessageColor;
    public static int serviceMessageColorBackup;
    private static int serviceSelectedMessage2Color;
    private static int serviceSelectedMessageColor;
    public static int serviceSelectedMessageColorBackup;
    private static boolean shouldDrawGradientIcons;
    private static boolean switchDayRunnableScheduled;
    private static boolean switchNightRunnableScheduled;
    private static int switchNightThemeDelay;
    private static boolean switchingNightTheme;
    private static Drawable themedWallpaper;
    private static int themedWallpaperFileOffset;
    private static String themedWallpaperLink;
    private static float[] tmpHSV5;
    private static int[] viewPos;
    private static Drawable wallpaper;
    public static Runnable wallpaperLoadTask;
    private static final Object sync = new Object();
    private static float lastBrightnessValue = 1.0f;
    private static Runnable switchDayBrightnessRunnable = new Runnable() { // from class: org.telegram.ui.ActionBar.Theme.1
        @Override // java.lang.Runnable
        public void run() {
            boolean unused = Theme.switchDayRunnableScheduled = false;
            Theme.applyDayNightThemeMaybe(false);
        }
    };
    private static Runnable switchNightBrightnessRunnable = new Runnable() { // from class: org.telegram.ui.ActionBar.Theme.2
        @Override // java.lang.Runnable
        public void run() {
            boolean unused = Theme.switchNightRunnableScheduled = false;
            Theme.applyDayNightThemeMaybe(true);
        }
    };
    public static int DEFALT_THEME_ACCENT_ID = 99;
    private static Paint maskPaint = new Paint(1);
    private static boolean[] loadingRemoteThemes = new boolean[4];
    private static int[] lastLoadingThemesTime = new int[4];
    private static long[] remoteThemesHash = new long[4];
    public static Drawable[] avatarDrawables = new Drawable[12];
    private static StatusDrawable[] chat_status_drawables = new StatusDrawable[6];
    public static Drawable[] chat_msgInCallDrawable = new Drawable[2];
    public static Drawable[] chat_msgInCallSelectedDrawable = new Drawable[2];
    public static Drawable[] chat_msgOutCallDrawable = new Drawable[2];
    public static Drawable[] chat_msgOutCallSelectedDrawable = new Drawable[2];
    public static Drawable[] chat_pollCheckDrawable = new Drawable[2];
    public static Drawable[] chat_pollCrossDrawable = new Drawable[2];
    public static Drawable[] chat_pollHintDrawable = new Drawable[2];
    public static Drawable[] chat_psaHelpDrawable = new Drawable[2];
    public static RLottieDrawable[] chat_attachButtonDrawables = new RLottieDrawable[6];
    public static Drawable[] chat_locationDrawable = new Drawable[2];
    public static Drawable[] chat_contactDrawable = new Drawable[2];
    public static Drawable[][] chat_fileStatesDrawable = (Drawable[][]) Array.newInstance(Drawable.class, 10, 2);
    public static Drawable[][] chat_photoStatesDrawables = (Drawable[][]) Array.newInstance(Drawable.class, 13, 2);
    public static Path[] chat_filePath = new Path[2];
    public static Path[] chat_updatePath = new Path[3];
    public static final String key_avatar_backgroundRed = "avatar_backgroundRed";
    public static final String key_avatar_backgroundOrange = "avatar_backgroundOrange";
    public static final String key_avatar_backgroundViolet = "avatar_backgroundViolet";
    public static final String key_avatar_backgroundGreen = "avatar_backgroundGreen";
    public static final String key_avatar_backgroundCyan = "avatar_backgroundCyan";
    public static final String key_avatar_backgroundBlue = "avatar_backgroundBlue";
    public static final String key_avatar_backgroundPink = "avatar_backgroundPink";
    public static String[] keys_avatar_background = {key_avatar_backgroundRed, key_avatar_backgroundOrange, key_avatar_backgroundViolet, key_avatar_backgroundGreen, key_avatar_backgroundCyan, key_avatar_backgroundBlue, key_avatar_backgroundPink};
    public static final String key_avatar_nameInMessageRed = "avatar_nameInMessageRed";
    public static final String key_avatar_nameInMessageOrange = "avatar_nameInMessageOrange";
    public static final String key_avatar_nameInMessageViolet = "avatar_nameInMessageViolet";
    public static final String key_avatar_nameInMessageGreen = "avatar_nameInMessageGreen";
    public static final String key_avatar_nameInMessageCyan = "avatar_nameInMessageCyan";
    public static final String key_avatar_nameInMessageBlue = "avatar_nameInMessageBlue";
    public static final String key_avatar_nameInMessagePink = "avatar_nameInMessagePink";
    public static String[] keys_avatar_nameInMessage = {key_avatar_nameInMessageRed, key_avatar_nameInMessageOrange, key_avatar_nameInMessageViolet, key_avatar_nameInMessageGreen, key_avatar_nameInMessageCyan, key_avatar_nameInMessageBlue, key_avatar_nameInMessagePink};
    private static final HashMap<String, Drawable> defaultChatDrawables = new HashMap<>();
    private static final HashMap<String, String> defaultChatDrawableColorKeys = new HashMap<>();
    private static final HashMap<String, Paint> defaultChatPaints = new HashMap<>();
    private static final HashMap<String, String> defaultChatPaintColors = new HashMap<>();
    private static HashSet<String> myMessagesColorKeys = new HashSet<>();
    private static HashSet<String> myMessagesBubblesColorKeys = new HashSet<>();
    private static HashSet<String> myMessagesGradientColorsNearKeys = new HashSet<>();
    private static HashMap<String, Integer> defaultColors = new HashMap<>();
    private static HashMap<String, String> fallbackKeys = new HashMap<>();
    private static HashSet<String> themeAccentExclusionKeys = new HashSet<>();
    private static ThreadLocal<float[]> hsvTemp1Local = new ThreadLocal<>();
    private static ThreadLocal<float[]> hsvTemp2Local = new ThreadLocal<>();
    private static ThreadLocal<float[]> hsvTemp3Local = new ThreadLocal<>();
    private static ThreadLocal<float[]> hsvTemp4Local = new ThreadLocal<>();
    private static ThreadLocal<float[]> hsvTemp5Local = new ThreadLocal<>();
    public static ArrayList<ThemeInfo> themes = new ArrayList<>();
    private static ArrayList<ThemeInfo> otherThemes = new ArrayList<>();
    private static HashMap<String, ThemeInfo> themesDict = new HashMap<>();
    private static HashMap<String, Integer> currentColorsNoAccent = new HashMap<>();
    private static HashMap<String, Integer> currentColors = new HashMap<>();

    /* loaded from: classes4.dex */
    public static class BackgroundDrawableSettings {
        public Boolean isCustomTheme;
        public Boolean isPatternWallpaper;
        public Boolean isWallpaperMotion;
        public Drawable themedWallpaper;
        public Drawable wallpaper;
    }

    /* loaded from: classes4.dex */
    public static class MessageDrawable extends Drawable {
        public static final int TYPE_MEDIA = 1;
        public static final int TYPE_PREVIEW = 2;
        public static final int TYPE_TEXT = 0;
        public static MotionBackgroundDrawable[] motionBackground = new MotionBackgroundDrawable[3];
        private int alpha;
        private Drawable[][] backgroundDrawable;
        private int[][] backgroundDrawableColor;
        private Rect backupRect;
        private Bitmap crosfadeFromBitmap;
        private Shader crosfadeFromBitmapShader;
        public MessageDrawable crossfadeFromDrawable;
        public float crossfadeProgress;
        private boolean currentAnimateGradient;
        private int[][] currentBackgroundDrawableRadius;
        private int currentBackgroundHeight;
        private int currentColor;
        private int currentGradientColor1;
        private int currentGradientColor2;
        private int currentGradientColor3;
        private int[] currentShadowDrawableRadius;
        private int currentType;
        private boolean drawFullBubble;
        private Shader gradientShader;
        private boolean isBottomNear;
        public boolean isCrossfadeBackground;
        private final boolean isOut;
        public boolean isSelected;
        private boolean isTopNear;
        public boolean lastDrawWithShadow;
        private Matrix matrix;
        private int overrideRoundRadius;
        private Paint paint;
        private Path path;
        PathDrawParams pathDrawCacheParams;
        private RectF rect;
        private final ResourcesProvider resourcesProvider;
        private Paint selectedPaint;
        private Drawable[] shadowDrawable;
        private int[] shadowDrawableColor;
        public boolean themePreview;
        private int topY;
        Drawable transitionDrawable;
        int transitionDrawableColor;

        public MessageDrawable(int type, boolean out, boolean selected) {
            this(type, out, selected, null);
        }

        public MessageDrawable(int type, boolean out, boolean selected, ResourcesProvider resourcesProvider) {
            this.paint = new Paint(1);
            this.rect = new RectF();
            this.matrix = new Matrix();
            this.backupRect = new Rect();
            this.currentShadowDrawableRadius = new int[]{-1, -1, -1, -1};
            this.shadowDrawable = new Drawable[4];
            this.shadowDrawableColor = new int[]{-1, -1, -1, -1};
            this.currentBackgroundDrawableRadius = new int[][]{new int[]{-1, -1, -1, -1}, new int[]{-1, -1, -1, -1}};
            this.backgroundDrawable = (Drawable[][]) Array.newInstance(Drawable.class, 2, 4);
            this.backgroundDrawableColor = new int[][]{new int[]{-1, -1, -1, -1}, new int[]{-1, -1, -1, -1}};
            this.resourcesProvider = resourcesProvider;
            this.isOut = out;
            this.currentType = type;
            this.isSelected = selected;
            this.path = new Path();
            this.selectedPaint = new Paint(1);
            this.alpha = 255;
        }

        public boolean hasGradient() {
            return this.gradientShader != null && Theme.shouldDrawGradientIcons;
        }

        public void applyMatrixScale() {
            int num;
            Bitmap bitmap;
            if (this.gradientShader instanceof BitmapShader) {
                int num2 = 1;
                if (this.isCrossfadeBackground && (bitmap = this.crosfadeFromBitmap) != null) {
                    if (this.currentType != 2) {
                        num2 = 0;
                    }
                    float scaleW = bitmap.getWidth() / motionBackground[num2].getBounds().width();
                    float scaleH = this.crosfadeFromBitmap.getHeight() / motionBackground[num2].getBounds().height();
                    float scale = 1.0f / Math.min(scaleW, scaleH);
                    this.matrix.postScale(scale, scale);
                    return;
                }
                if (this.themePreview) {
                    num = 2;
                } else {
                    if (this.currentType != 2) {
                        num2 = 0;
                    }
                    num = num2;
                }
                Bitmap bitmap2 = motionBackground[num].getBitmap();
                float scaleW2 = bitmap2.getWidth() / motionBackground[num].getBounds().width();
                float scaleH2 = bitmap2.getHeight() / motionBackground[num].getBounds().height();
                float scale2 = 1.0f / Math.min(scaleW2, scaleH2);
                this.matrix.postScale(scale2, scale2);
            }
        }

        public Shader getGradientShader() {
            return this.gradientShader;
        }

        public Matrix getMatrix() {
            return this.matrix;
        }

        protected int getColor(String key) {
            if (this.currentType == 2) {
                return Theme.getColor(key);
            }
            ResourcesProvider resourcesProvider = this.resourcesProvider;
            Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }

        protected Integer getCurrentColor(String key) {
            if (this.currentType == 2) {
                return Integer.valueOf(Theme.getColor(key));
            }
            ResourcesProvider resourcesProvider = this.resourcesProvider;
            return resourcesProvider != null ? resourcesProvider.getCurrentColor(key) : (Integer) Theme.currentColors.get(key);
        }

        public void setTop(int top, int backgroundWidth, int backgroundHeight, boolean topNear, boolean bottomNear) {
            setTop(top, backgroundWidth, backgroundHeight, backgroundHeight, 0, 0, topNear, bottomNear);
        }

        public void setTop(int top, int backgroundWidth, int backgroundHeight, int heightOffset, int blurredViewTopOffset, int blurredViewBottomOffset, boolean topNear, boolean bottomNear) {
            boolean animatedGradient;
            Integer gradientColor3;
            Integer gradientColor2;
            Integer gradientColor1;
            int color;
            int num;
            int i;
            MessageDrawable messageDrawable = this.crossfadeFromDrawable;
            if (messageDrawable != null) {
                messageDrawable.setTop(top, backgroundWidth, backgroundHeight, heightOffset, blurredViewTopOffset, blurredViewBottomOffset, topNear, bottomNear);
            }
            if (this.isOut) {
                color = getColor(this.isSelected ? Theme.key_chat_outBubbleSelected : Theme.key_chat_outBubble);
                gradientColor1 = getCurrentColor(Theme.key_chat_outBubbleGradient1);
                gradientColor2 = getCurrentColor(Theme.key_chat_outBubbleGradient2);
                gradientColor3 = getCurrentColor(Theme.key_chat_outBubbleGradient3);
                Integer val = getCurrentColor(Theme.key_chat_outBubbleGradientAnimated);
                animatedGradient = (val == null || val.intValue() == 0) ? false : true;
            } else {
                color = getColor(this.isSelected ? Theme.key_chat_inBubbleSelected : Theme.key_chat_inBubble);
                gradientColor1 = null;
                gradientColor2 = null;
                gradientColor3 = null;
                animatedGradient = false;
            }
            if (gradientColor1 != null) {
                color = getColor(Theme.key_chat_outBubble);
            }
            if (gradientColor1 == null) {
                gradientColor1 = 0;
            }
            if (gradientColor2 == null) {
                gradientColor2 = 0;
            }
            if (gradientColor3 == null) {
                gradientColor3 = 0;
            }
            if (this.themePreview) {
                num = 2;
            } else {
                num = this.currentType == 2 ? (char) 1 : (char) 0;
            }
            if (!this.isCrossfadeBackground && gradientColor2.intValue() != 0 && animatedGradient) {
                MotionBackgroundDrawable[] motionBackgroundDrawableArr = motionBackground;
                if (motionBackgroundDrawableArr[num] != null) {
                    int[] colors = motionBackgroundDrawableArr[num].getColors();
                    this.currentColor = colors[0];
                    this.currentGradientColor1 = colors[1];
                    this.currentGradientColor2 = colors[2];
                    this.currentGradientColor3 = colors[3];
                }
            }
            if (this.isCrossfadeBackground && gradientColor2.intValue() != 0 && animatedGradient) {
                if (backgroundHeight == this.currentBackgroundHeight && this.crosfadeFromBitmapShader != null && this.currentColor == color && this.currentGradientColor1 == gradientColor1.intValue() && this.currentGradientColor2 == gradientColor2.intValue() && this.currentGradientColor3 == gradientColor3.intValue() && this.currentAnimateGradient == animatedGradient) {
                    i = -1;
                } else {
                    if (this.crosfadeFromBitmap == null) {
                        this.crosfadeFromBitmap = Bitmap.createBitmap(60, 80, Bitmap.Config.ARGB_8888);
                        this.crosfadeFromBitmapShader = new BitmapShader(this.crosfadeFromBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    }
                    MotionBackgroundDrawable[] motionBackgroundDrawableArr2 = motionBackground;
                    if (motionBackgroundDrawableArr2[num] == null) {
                        motionBackgroundDrawableArr2[num] = new MotionBackgroundDrawable();
                        if (this.currentType != 2) {
                            motionBackground[num].setPostInvalidateParent(true);
                        }
                        motionBackground[num].setRoundRadius(AndroidUtilities.dp(1.0f));
                    }
                    i = -1;
                    motionBackground[num].setColors(color, gradientColor1.intValue(), gradientColor2.intValue(), gradientColor3.intValue(), this.crosfadeFromBitmap);
                    this.crosfadeFromBitmapShader.setLocalMatrix(this.matrix);
                }
                Shader shader = this.crosfadeFromBitmapShader;
                this.gradientShader = shader;
                this.paint.setShader(shader);
                this.paint.setColor(i);
                this.currentColor = color;
                this.currentAnimateGradient = animatedGradient;
                this.currentGradientColor1 = gradientColor1.intValue();
                this.currentGradientColor2 = gradientColor2.intValue();
                this.currentGradientColor3 = gradientColor3.intValue();
            } else if (gradientColor1.intValue() != 0 && (this.gradientShader == null || backgroundHeight != this.currentBackgroundHeight || this.currentColor != color || this.currentGradientColor1 != gradientColor1.intValue() || this.currentGradientColor2 != gradientColor2.intValue() || this.currentGradientColor3 != gradientColor3.intValue() || this.currentAnimateGradient != animatedGradient)) {
                if (gradientColor2.intValue() != 0 && animatedGradient) {
                    MotionBackgroundDrawable[] motionBackgroundDrawableArr3 = motionBackground;
                    if (motionBackgroundDrawableArr3[num] == null) {
                        motionBackgroundDrawableArr3[num] = new MotionBackgroundDrawable();
                        if (this.currentType != 2) {
                            motionBackground[num].setPostInvalidateParent(true);
                        }
                        motionBackground[num].setRoundRadius(AndroidUtilities.dp(1.0f));
                    }
                    motionBackground[num].setColors(color, gradientColor1.intValue(), gradientColor2.intValue(), gradientColor3.intValue());
                    this.gradientShader = motionBackground[num].getBitmapShader();
                } else if (gradientColor2.intValue() == 0) {
                    int[] colors2 = {gradientColor1.intValue(), color};
                    this.gradientShader = new LinearGradient(0.0f, blurredViewTopOffset, 0.0f, backgroundHeight, colors2, (float[]) null, Shader.TileMode.CLAMP);
                } else if (gradientColor3.intValue() != 0) {
                    int[] colors3 = {gradientColor3.intValue(), gradientColor2.intValue(), gradientColor1.intValue(), color};
                    this.gradientShader = new LinearGradient(0.0f, blurredViewTopOffset, 0.0f, backgroundHeight, colors3, (float[]) null, Shader.TileMode.CLAMP);
                } else {
                    int[] colors4 = {gradientColor2.intValue(), gradientColor1.intValue(), color};
                    this.gradientShader = new LinearGradient(0.0f, blurredViewTopOffset, 0.0f, backgroundHeight, colors4, (float[]) null, Shader.TileMode.CLAMP);
                }
                this.paint.setShader(this.gradientShader);
                this.currentColor = color;
                this.currentAnimateGradient = animatedGradient;
                this.currentGradientColor1 = gradientColor1.intValue();
                this.currentGradientColor2 = gradientColor2.intValue();
                this.currentGradientColor3 = gradientColor3.intValue();
                this.paint.setColor(-1);
            } else if (gradientColor1.intValue() == 0) {
                if (this.gradientShader != null) {
                    this.gradientShader = null;
                    this.paint.setShader(null);
                }
                this.paint.setColor(color);
            }
            if (this.gradientShader instanceof BitmapShader) {
                motionBackground[num].setBounds(0, blurredViewTopOffset, backgroundWidth, backgroundHeight - heightOffset);
            }
            this.currentBackgroundHeight = backgroundHeight;
            this.topY = top - (this.gradientShader instanceof BitmapShader ? heightOffset : 0);
            this.isTopNear = topNear;
            this.isBottomNear = bottomNear;
        }

        public void setTopBottomNear(boolean topNear, boolean bottomNear) {
            this.isTopNear = topNear;
            this.isBottomNear = bottomNear;
        }

        public int getTopY() {
            return this.topY;
        }

        private int dp(float value) {
            if (this.currentType == 2) {
                return (int) Math.ceil(3.0f * value);
            }
            return AndroidUtilities.dp(value);
        }

        public Paint getPaint() {
            return this.paint;
        }

        public Drawable[] getShadowDrawables() {
            return this.shadowDrawable;
        }

        /* JADX WARN: Type inference failed for: r0v23, types: [java.lang.Throwable] */
        /* JADX WARN: Type inference failed for: r0v3, types: [boolean] */
        public Drawable getBackgroundDrawable() {
            int idx;
            int color;
            int newRad = AndroidUtilities.dp(SharedConfig.bubbleRadius);
            boolean z = this.isTopNear;
            if (z && this.isBottomNear) {
                idx = 3;
            } else if (z) {
                idx = 2;
            } else if (this.isBottomNear) {
                idx = 1;
            } else {
                idx = 0;
            }
            ?? r0 = this.isSelected;
            boolean forceSetColor = false;
            boolean drawWithShadow = this.gradientShader == null && r0 == 0 && !this.isCrossfadeBackground;
            int shadowColor = getColor(this.isOut ? Theme.key_chat_outBubbleShadow : Theme.key_chat_inBubbleShadow);
            if (this.lastDrawWithShadow != drawWithShadow || this.currentBackgroundDrawableRadius[r0][idx] != newRad || (drawWithShadow && this.shadowDrawableColor[idx] != shadowColor)) {
                this.currentBackgroundDrawableRadius[r0][idx] = newRad;
                try {
                    Bitmap bitmap = Bitmap.createBitmap(dp(50.0f), dp(40.0f), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    this.backupRect.set(getBounds());
                    if (drawWithShadow) {
                        this.shadowDrawableColor[idx] = shadowColor;
                        Paint shadowPaint = new Paint(1);
                        LinearGradient gradientShader = new LinearGradient(0.0f, 0.0f, 0.0f, dp(40.0f), new int[]{358573417, 694117737}, (float[]) null, Shader.TileMode.CLAMP);
                        shadowPaint.setShader(gradientShader);
                        shadowPaint.setColorFilter(new PorterDuffColorFilter(shadowColor, PorterDuff.Mode.MULTIPLY));
                        shadowPaint.setShadowLayer(2.0f, 0.0f, 1.0f, -1);
                        if (AndroidUtilities.density > 1.0f) {
                            setBounds(-1, -1, bitmap.getWidth() + 1, bitmap.getHeight() + 1);
                        } else {
                            setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                        }
                        draw(canvas, shadowPaint);
                        if (AndroidUtilities.density > 1.0f) {
                            shadowPaint.setColor(0);
                            shadowPaint.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
                            shadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                            setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                            draw(canvas, shadowPaint);
                        }
                    }
                    Paint shadowPaint2 = new Paint(1);
                    shadowPaint2.setColor(-1);
                    setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    draw(canvas, shadowPaint2);
                    this.backgroundDrawable[r0][idx] = new NinePatchDrawable(bitmap, getByteBuffer((bitmap.getWidth() / 2) - 1, (bitmap.getWidth() / 2) + 1, (bitmap.getHeight() / 2) - 1, (bitmap.getHeight() / 2) + 1).array(), new Rect(), null);
                    forceSetColor = true;
                    setBounds(this.backupRect);
                } catch (Throwable th) {
                }
            }
            this.lastDrawWithShadow = drawWithShadow;
            if (this.isSelected) {
                color = getColor(this.isOut ? Theme.key_chat_outBubbleSelected : Theme.key_chat_inBubbleSelected);
            } else {
                color = getColor(this.isOut ? Theme.key_chat_outBubble : Theme.key_chat_inBubble);
            }
            Drawable[][] drawableArr = this.backgroundDrawable;
            int idx2 = r0 == true ? 1 : 0;
            if (drawableArr[idx2][idx] != null && (this.backgroundDrawableColor[r0][idx] != color || forceSetColor)) {
                drawableArr[r0][idx].setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                this.backgroundDrawableColor[r0][idx] = color;
            }
            return this.backgroundDrawable[r0][idx];
        }

        public Drawable getTransitionDrawable(int color) {
            if (this.transitionDrawable == null) {
                Bitmap bitmap = Bitmap.createBitmap(dp(50.0f), dp(40.0f), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                this.backupRect.set(getBounds());
                Paint shadowPaint = new Paint(1);
                shadowPaint.setColor(-1);
                setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                draw(canvas, shadowPaint);
                this.transitionDrawable = new NinePatchDrawable(bitmap, getByteBuffer((bitmap.getWidth() / 2) - 1, (bitmap.getWidth() / 2) + 1, (bitmap.getHeight() / 2) - 1, (bitmap.getHeight() / 2) + 1).array(), new Rect(), null);
                setBounds(this.backupRect);
            }
            if (this.transitionDrawableColor != color) {
                this.transitionDrawableColor = color;
                this.transitionDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
            }
            return this.transitionDrawable;
        }

        public MotionBackgroundDrawable getMotionBackgroundDrawable() {
            if (this.themePreview) {
                return motionBackground[2];
            }
            return motionBackground[this.currentType == 2 ? (char) 1 : (char) 0];
        }

        public Drawable getShadowDrawable() {
            int idx;
            if (this.isCrossfadeBackground) {
                return null;
            }
            if (this.gradientShader == null && !this.isSelected && this.crossfadeFromDrawable == null) {
                return null;
            }
            int newRad = AndroidUtilities.dp(SharedConfig.bubbleRadius);
            boolean z = this.isTopNear;
            if (z && this.isBottomNear) {
                idx = 3;
            } else if (z) {
                idx = 2;
            } else if (this.isBottomNear) {
                idx = 1;
            } else {
                idx = 0;
            }
            boolean forceSetColor = false;
            int[] iArr = this.currentShadowDrawableRadius;
            if (iArr[idx] != newRad) {
                iArr[idx] = newRad;
                try {
                    Bitmap bitmap = Bitmap.createBitmap(dp(50.0f), dp(40.0f), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    Paint shadowPaint = new Paint(1);
                    LinearGradient gradientShader = new LinearGradient(0.0f, 0.0f, 0.0f, dp(40.0f), new int[]{358573417, 694117737}, (float[]) null, Shader.TileMode.CLAMP);
                    shadowPaint.setShader(gradientShader);
                    shadowPaint.setShadowLayer(2.0f, 0.0f, 1.0f, -1);
                    if (AndroidUtilities.density > 1.0f) {
                        setBounds(-1, -1, bitmap.getWidth() + 1, bitmap.getHeight() + 1);
                    } else {
                        setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    }
                    draw(canvas, shadowPaint);
                    if (AndroidUtilities.density > 1.0f) {
                        shadowPaint.setColor(0);
                        shadowPaint.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
                        shadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                        setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                        draw(canvas, shadowPaint);
                    }
                    this.shadowDrawable[idx] = new NinePatchDrawable(bitmap, getByteBuffer((bitmap.getWidth() / 2) - 1, (bitmap.getWidth() / 2) + 1, (bitmap.getHeight() / 2) - 1, (bitmap.getHeight() / 2) + 1).array(), new Rect(), null);
                    forceSetColor = true;
                } catch (Throwable th) {
                }
            }
            int color = getColor(this.isOut ? Theme.key_chat_outBubbleShadow : Theme.key_chat_inBubbleShadow);
            Drawable[] drawableArr = this.shadowDrawable;
            if (drawableArr[idx] != null && (this.shadowDrawableColor[idx] != color || forceSetColor)) {
                drawableArr[idx].setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                this.shadowDrawableColor[idx] = color;
            }
            return this.shadowDrawable[idx];
        }

        private static ByteBuffer getByteBuffer(int x1, int x2, int y1, int y2) {
            ByteBuffer buffer = ByteBuffer.allocate(84).order(ByteOrder.nativeOrder());
            buffer.put((byte) 1);
            buffer.put((byte) 2);
            buffer.put((byte) 2);
            buffer.put((byte) 9);
            buffer.putInt(0);
            buffer.putInt(0);
            buffer.putInt(0);
            buffer.putInt(0);
            buffer.putInt(0);
            buffer.putInt(0);
            buffer.putInt(0);
            buffer.putInt(x1);
            buffer.putInt(x2);
            buffer.putInt(y1);
            buffer.putInt(y2);
            buffer.putInt(1);
            buffer.putInt(1);
            buffer.putInt(1);
            buffer.putInt(1);
            buffer.putInt(1);
            buffer.putInt(1);
            buffer.putInt(1);
            buffer.putInt(1);
            buffer.putInt(1);
            return buffer;
        }

        public void drawCached(Canvas canvas, PathDrawParams patchDrawCacheParams, Paint paintToUse) {
            this.pathDrawCacheParams = patchDrawCacheParams;
            MessageDrawable messageDrawable = this.crossfadeFromDrawable;
            if (messageDrawable != null) {
                messageDrawable.pathDrawCacheParams = patchDrawCacheParams;
            }
            draw(canvas, paintToUse);
            this.pathDrawCacheParams = null;
            MessageDrawable messageDrawable2 = this.crossfadeFromDrawable;
            if (messageDrawable2 != null) {
                messageDrawable2.pathDrawCacheParams = null;
            }
        }

        public void drawCached(Canvas canvas, PathDrawParams patchDrawCacheParams) {
            drawCached(canvas, patchDrawCacheParams, null);
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            MessageDrawable messageDrawable = this.crossfadeFromDrawable;
            if (messageDrawable != null) {
                messageDrawable.draw(canvas);
                setAlpha((int) (this.crossfadeProgress * 255.0f));
                draw(canvas, null);
                setAlpha(255);
                return;
            }
            draw(canvas, null);
        }

        public void draw(Canvas canvas, Paint paintToUse) {
            int nearRad;
            int rad;
            boolean drawFullTop;
            boolean drawFullBottom;
            Path path;
            boolean invalidatePath;
            int nearRad2;
            Drawable background;
            Rect bounds = getBounds();
            if (paintToUse != null || this.gradientShader != null || (background = getBackgroundDrawable()) == null) {
                int padding = dp(2.0f);
                if (this.overrideRoundRadius != 0) {
                    rad = this.overrideRoundRadius;
                    nearRad = this.overrideRoundRadius;
                } else if (this.currentType != 2) {
                    rad = dp(SharedConfig.bubbleRadius);
                    nearRad = dp(Math.min(5, SharedConfig.bubbleRadius));
                } else {
                    rad = dp(6.0f);
                    nearRad = dp(6.0f);
                }
                int smallRad = dp(6.0f);
                Paint p = paintToUse == null ? this.paint : paintToUse;
                if (paintToUse == null && this.gradientShader != null) {
                    this.matrix.reset();
                    applyMatrixScale();
                    this.matrix.postTranslate(0.0f, -this.topY);
                    this.gradientShader.setLocalMatrix(this.matrix);
                }
                int top = Math.max(bounds.top, 0);
                if (this.pathDrawCacheParams != null && bounds.height() < this.currentBackgroundHeight) {
                    drawFullBottom = true;
                    drawFullTop = true;
                } else {
                    drawFullBottom = this.currentType != 1 ? (this.topY + bounds.bottom) - rad < this.currentBackgroundHeight : (this.topY + bounds.bottom) - (smallRad * 2) < this.currentBackgroundHeight;
                    drawFullTop = this.topY + (rad * 2) >= 0;
                }
                PathDrawParams pathDrawParams = this.pathDrawCacheParams;
                if (pathDrawParams != null) {
                    path = pathDrawParams.path;
                    invalidatePath = this.pathDrawCacheParams.invalidatePath(bounds, drawFullBottom, drawFullTop);
                } else {
                    path = this.path;
                    invalidatePath = true;
                }
                if (invalidatePath) {
                    path.reset();
                    if (!this.isOut) {
                        int nearRad3 = nearRad;
                        if (!this.drawFullBubble && this.currentType != 2 && paintToUse == null && !drawFullBottom) {
                            path.moveTo(bounds.left + dp(8.0f), (top - this.topY) + this.currentBackgroundHeight);
                            path.lineTo(bounds.right - padding, (top - this.topY) + this.currentBackgroundHeight);
                        } else {
                            if (this.currentType == 1) {
                                path.moveTo(bounds.left + dp(8.0f) + rad, bounds.bottom - padding);
                            } else {
                                path.moveTo(bounds.left + dp(2.6f), bounds.bottom - padding);
                            }
                            path.lineTo((bounds.right - padding) - rad, bounds.bottom - padding);
                            this.rect.set((bounds.right - padding) - (rad * 2), (bounds.bottom - padding) - (rad * 2), bounds.right - padding, bounds.bottom - padding);
                            path.arcTo(this.rect, 90.0f, -90.0f, false);
                        }
                        if (!this.drawFullBubble && this.currentType != 2 && paintToUse == null && !drawFullTop) {
                            path.lineTo(bounds.right - padding, (top - this.topY) - dp(2.0f));
                            if (this.currentType == 1) {
                                path.lineTo(bounds.left + padding, (top - this.topY) - dp(2.0f));
                            } else {
                                path.lineTo(bounds.left + dp(8.0f), (top - this.topY) - dp(2.0f));
                            }
                        } else {
                            path.lineTo(bounds.right - padding, bounds.top + padding + rad);
                            this.rect.set((bounds.right - padding) - (rad * 2), bounds.top + padding, bounds.right - padding, bounds.top + padding + (rad * 2));
                            path.arcTo(this.rect, 0.0f, -90.0f, false);
                            int radToUse = this.isTopNear ? nearRad3 : rad;
                            if (this.currentType != 1) {
                                path.lineTo(bounds.left + dp(8.0f) + radToUse, bounds.top + padding);
                                this.rect.set(bounds.left + dp(8.0f), bounds.top + padding, bounds.left + dp(8.0f) + (radToUse * 2), bounds.top + padding + (radToUse * 2));
                            } else {
                                path.lineTo(bounds.left + padding + radToUse, bounds.top + padding);
                                this.rect.set(bounds.left + padding, bounds.top + padding, bounds.left + padding + (radToUse * 2), bounds.top + padding + (radToUse * 2));
                            }
                            path.arcTo(this.rect, 270.0f, -90.0f, false);
                        }
                        int i = this.currentType;
                        if (i == 1) {
                            if (paintToUse == null && !drawFullBottom) {
                                path.lineTo(bounds.left + padding, (top - this.topY) + this.currentBackgroundHeight);
                            } else {
                                int radToUse2 = this.isBottomNear ? nearRad3 : rad;
                                path.lineTo(bounds.left + padding, (bounds.bottom - padding) - radToUse2);
                                this.rect.set(bounds.left + padding, (bounds.bottom - padding) - (radToUse2 * 2), bounds.left + padding + (radToUse2 * 2), bounds.bottom - padding);
                                path.arcTo(this.rect, 180.0f, -90.0f, false);
                            }
                        } else if (this.drawFullBubble || i == 2 || paintToUse != null || drawFullBottom) {
                            path.lineTo(bounds.left + dp(8.0f), ((bounds.bottom - padding) - smallRad) - dp(3.0f));
                            this.rect.set((bounds.left + dp(7.0f)) - (smallRad * 2), ((bounds.bottom - padding) - (smallRad * 2)) - dp(9.0f), bounds.left + dp(8.0f), (bounds.bottom - padding) - dp(1.0f));
                            path.arcTo(this.rect, 0.0f, 83.0f, false);
                        } else {
                            path.lineTo(bounds.left + dp(8.0f), (top - this.topY) + this.currentBackgroundHeight);
                        }
                    } else {
                        if (!this.drawFullBubble && this.currentType != 2 && paintToUse == null && !drawFullBottom) {
                            path.moveTo(bounds.right - dp(8.0f), (top - this.topY) + this.currentBackgroundHeight);
                            path.lineTo(bounds.left + padding, (top - this.topY) + this.currentBackgroundHeight);
                            nearRad2 = nearRad;
                        } else {
                            if (this.currentType == 1) {
                                path.moveTo((bounds.right - dp(8.0f)) - rad, bounds.bottom - padding);
                            } else {
                                path.moveTo(bounds.right - dp(2.6f), bounds.bottom - padding);
                            }
                            path.lineTo(bounds.left + padding + rad, bounds.bottom - padding);
                            nearRad2 = nearRad;
                            this.rect.set(bounds.left + padding, (bounds.bottom - padding) - (rad * 2), bounds.left + padding + (rad * 2), bounds.bottom - padding);
                            path.arcTo(this.rect, 90.0f, 90.0f, false);
                        }
                        if (!this.drawFullBubble && this.currentType != 2 && paintToUse == null && !drawFullTop) {
                            path.lineTo(bounds.left + padding, (top - this.topY) - dp(2.0f));
                            if (this.currentType == 1) {
                                path.lineTo(bounds.right - padding, (top - this.topY) - dp(2.0f));
                            } else {
                                path.lineTo(bounds.right - dp(8.0f), (top - this.topY) - dp(2.0f));
                            }
                        } else {
                            path.lineTo(bounds.left + padding, bounds.top + padding + rad);
                            this.rect.set(bounds.left + padding, bounds.top + padding, bounds.left + padding + (rad * 2), bounds.top + padding + (rad * 2));
                            path.arcTo(this.rect, 180.0f, 90.0f, false);
                            int radToUse3 = this.isTopNear ? nearRad2 : rad;
                            if (this.currentType != 1) {
                                path.lineTo((bounds.right - dp(8.0f)) - radToUse3, bounds.top + padding);
                                this.rect.set((bounds.right - dp(8.0f)) - (radToUse3 * 2), bounds.top + padding, bounds.right - dp(8.0f), bounds.top + padding + (radToUse3 * 2));
                            } else {
                                path.lineTo((bounds.right - padding) - radToUse3, bounds.top + padding);
                                this.rect.set((bounds.right - padding) - (radToUse3 * 2), bounds.top + padding, bounds.right - padding, bounds.top + padding + (radToUse3 * 2));
                            }
                            path.arcTo(this.rect, 270.0f, 90.0f, false);
                        }
                        int i2 = this.currentType;
                        if (i2 == 1) {
                            if (paintToUse == null && !drawFullBottom) {
                                path.lineTo(bounds.right - padding, (top - this.topY) + this.currentBackgroundHeight);
                            } else {
                                int radToUse4 = this.isBottomNear ? nearRad2 : rad;
                                path.lineTo(bounds.right - padding, (bounds.bottom - padding) - radToUse4);
                                this.rect.set((bounds.right - padding) - (radToUse4 * 2), (bounds.bottom - padding) - (radToUse4 * 2), bounds.right - padding, bounds.bottom - padding);
                                path.arcTo(this.rect, 0.0f, 90.0f, false);
                            }
                        } else if (this.drawFullBubble || i2 == 2 || paintToUse != null || drawFullBottom) {
                            path.lineTo(bounds.right - dp(8.0f), ((bounds.bottom - padding) - smallRad) - dp(3.0f));
                            this.rect.set(bounds.right - dp(8.0f), ((bounds.bottom - padding) - (smallRad * 2)) - dp(9.0f), (bounds.right - dp(7.0f)) + (smallRad * 2), (bounds.bottom - padding) - dp(1.0f));
                            path.arcTo(this.rect, 180.0f, -83.0f, false);
                        } else {
                            path.lineTo(bounds.right - dp(8.0f), (top - this.topY) + this.currentBackgroundHeight);
                        }
                    }
                    path.close();
                }
                canvas.drawPath(path, p);
                if (this.gradientShader != null && this.isSelected && paintToUse == null) {
                    int color = getColor(Theme.key_chat_outBubbleGradientSelectedOverlay);
                    this.selectedPaint.setColor(ColorUtils.setAlphaComponent(color, (int) ((Color.alpha(color) * this.alpha) / 255.0f)));
                    canvas.drawPath(path, this.selectedPaint);
                    return;
                }
                return;
            }
            background.setBounds(bounds);
            background.draw(canvas);
        }

        public void setDrawFullBubble(boolean drawFullBuble) {
            this.drawFullBubble = drawFullBuble;
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
            if (this.alpha != alpha) {
                this.alpha = alpha;
                this.paint.setAlpha(alpha);
                if (this.isOut) {
                    this.selectedPaint.setAlpha((int) (Color.alpha(getColor(Theme.key_chat_outBubbleGradientSelectedOverlay)) * (alpha / 255.0f)));
                }
            }
            if (this.gradientShader == null) {
                Drawable background = getBackgroundDrawable();
                if (Build.VERSION.SDK_INT >= 19) {
                    if (background.getAlpha() != alpha) {
                        background.setAlpha(alpha);
                        return;
                    }
                    return;
                }
                background.setAlpha(alpha);
            }
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(int color, PorterDuff.Mode mode) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -2;
        }

        @Override // android.graphics.drawable.Drawable
        public void setBounds(int left, int top, int right, int bottom) {
            super.setBounds(left, top, right, bottom);
            MessageDrawable messageDrawable = this.crossfadeFromDrawable;
            if (messageDrawable != null) {
                messageDrawable.setBounds(left, top, right, bottom);
            }
        }

        public void setRoundRadius(int radius) {
            this.overrideRoundRadius = radius;
        }

        /* loaded from: classes4.dex */
        public static class PathDrawParams {
            boolean lastDrawFullBottom;
            boolean lastDrawFullTop;
            Path path = new Path();
            Rect lastRect = new Rect();

            public boolean invalidatePath(Rect bounds, boolean drawFullBottom, boolean drawFullTop) {
                boolean invalidate = (!this.lastRect.isEmpty() && this.lastRect.top == bounds.top && this.lastRect.bottom == bounds.bottom && this.lastRect.right == bounds.right && this.lastRect.left == bounds.left && this.lastDrawFullTop == drawFullTop && this.lastDrawFullBottom == drawFullBottom && drawFullTop && drawFullBottom) ? false : true;
                this.lastDrawFullTop = drawFullTop;
                this.lastDrawFullBottom = drawFullBottom;
                this.lastRect.set(bounds);
                return invalidate;
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class PatternsLoader implements NotificationCenter.NotificationCenterDelegate {
        private static PatternsLoader loader;
        private int account = UserConfig.selectedAccount;
        private HashMap<String, LoadingPattern> watingForLoad;

        /* loaded from: classes4.dex */
        public static class LoadingPattern {
            public ArrayList<ThemeAccent> accents;
            public TLRPC.TL_wallPaper pattern;

            private LoadingPattern() {
                this.accents = new ArrayList<>();
            }
        }

        public static void createLoader(boolean force) {
            String key;
            if (loader != null && !force) {
                return;
            }
            ArrayList<ThemeAccent> accentsToLoad = null;
            for (int b = 0; b < 5; b++) {
                switch (b) {
                    case 0:
                        key = "Blue";
                        break;
                    case 1:
                        key = "Dark Blue";
                        break;
                    case 2:
                        key = "Arctic Blue";
                        break;
                    case 3:
                        key = "Day";
                        break;
                    default:
                        key = "Night";
                        break;
                }
                ThemeInfo info = (ThemeInfo) Theme.themesDict.get(key);
                if (info != null && info.themeAccents != null && !info.themeAccents.isEmpty()) {
                    int N = info.themeAccents.size();
                    for (int a = 0; a < N; a++) {
                        ThemeAccent accent = info.themeAccents.get(a);
                        if (accent.id != Theme.DEFALT_THEME_ACCENT_ID && !TextUtils.isEmpty(accent.patternSlug)) {
                            if (accentsToLoad == null) {
                                accentsToLoad = new ArrayList<>();
                            }
                            accentsToLoad.add(accent);
                        }
                    }
                }
            }
            loader = new PatternsLoader(accentsToLoad);
        }

        private PatternsLoader(final ArrayList<ThemeAccent> accents) {
            if (accents == null) {
                return;
            }
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.ActionBar.Theme$PatternsLoader$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Theme.PatternsLoader.this.m1433lambda$new$1$orgtelegramuiActionBarTheme$PatternsLoader(accents);
                }
            });
        }

        /* renamed from: lambda$new$1$org-telegram-ui-ActionBar-Theme$PatternsLoader */
        public /* synthetic */ void m1433lambda$new$1$orgtelegramuiActionBarTheme$PatternsLoader(final ArrayList accents) {
            ArrayList<String> slugs = null;
            int a = 0;
            int N = accents.size();
            while (a < N) {
                ThemeAccent accent = (ThemeAccent) accents.get(a);
                File wallpaper = accent.getPathToWallpaper();
                if (wallpaper != null && wallpaper.exists()) {
                    accents.remove(a);
                    a--;
                    N--;
                } else {
                    if (slugs == null) {
                        slugs = new ArrayList<>();
                    }
                    if (!slugs.contains(accent.patternSlug)) {
                        slugs.add(accent.patternSlug);
                    }
                }
                a++;
            }
            if (slugs == null) {
                return;
            }
            TLRPC.TL_account_getMultiWallPapers req = new TLRPC.TL_account_getMultiWallPapers();
            int N2 = slugs.size();
            for (int a2 = 0; a2 < N2; a2++) {
                TLRPC.TL_inputWallPaperSlug slug = new TLRPC.TL_inputWallPaperSlug();
                slug.slug = slugs.get(a2);
                req.wallpapers.add(slug);
            }
            int a3 = this.account;
            ConnectionsManager.getInstance(a3).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ActionBar.Theme$PatternsLoader$$ExternalSyntheticLambda3
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    Theme.PatternsLoader.this.m1432lambda$new$0$orgtelegramuiActionBarTheme$PatternsLoader(accents, tLObject, tL_error);
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-ActionBar-Theme$PatternsLoader */
        public /* synthetic */ void m1432lambda$new$0$orgtelegramuiActionBarTheme$PatternsLoader(ArrayList accents, TLObject response, TLRPC.TL_error error) {
            int N2;
            TLRPC.Vector res;
            int N22;
            TLRPC.Vector res2;
            if (response instanceof TLRPC.Vector) {
                TLRPC.Vector res3 = (TLRPC.Vector) response;
                ArrayList<ThemeAccent> createdAccents = null;
                int b = 0;
                int N23 = res3.objects.size();
                while (b < N23) {
                    TLRPC.WallPaper object = (TLRPC.WallPaper) res3.objects.get(b);
                    if (!(object instanceof TLRPC.TL_wallPaper)) {
                        res = res3;
                        N2 = N23;
                    } else {
                        TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) object;
                        if (wallPaper.pattern) {
                            File patternPath = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(wallPaper.document, true);
                            Boolean exists = null;
                            Bitmap patternBitmap = null;
                            int a = 0;
                            int N = accents.size();
                            while (a < N) {
                                ThemeAccent accent = (ThemeAccent) accents.get(a);
                                if (!accent.patternSlug.equals(wallPaper.slug)) {
                                    res2 = res3;
                                    N22 = N23;
                                } else {
                                    if (exists == null) {
                                        exists = Boolean.valueOf(patternPath.exists());
                                    }
                                    if (patternBitmap != null) {
                                        res2 = res3;
                                        N22 = N23;
                                    } else if (exists.booleanValue()) {
                                        res2 = res3;
                                        N22 = N23;
                                    } else {
                                        String key = FileLoader.getAttachFileName(wallPaper.document);
                                        if (this.watingForLoad == null) {
                                            this.watingForLoad = new HashMap<>();
                                        }
                                        LoadingPattern loadingPattern = this.watingForLoad.get(key);
                                        if (loadingPattern != null) {
                                            res2 = res3;
                                            N22 = N23;
                                        } else {
                                            res2 = res3;
                                            N22 = N23;
                                            loadingPattern = new LoadingPattern();
                                            loadingPattern.pattern = wallPaper;
                                            this.watingForLoad.put(key, loadingPattern);
                                        }
                                        loadingPattern.accents.add(accent);
                                    }
                                    Bitmap patternBitmap2 = createWallpaperForAccent(patternBitmap, "application/x-tgwallpattern".equals(wallPaper.document.mime_type), patternPath, accent);
                                    if (createdAccents == null) {
                                        createdAccents = new ArrayList<>();
                                    }
                                    createdAccents.add(accent);
                                    patternBitmap = patternBitmap2;
                                }
                                a++;
                                res3 = res2;
                                N23 = N22;
                            }
                            res = res3;
                            N2 = N23;
                            if (patternBitmap != null) {
                                patternBitmap.recycle();
                            }
                        } else {
                            res = res3;
                            N2 = N23;
                        }
                    }
                    b++;
                    res3 = res;
                    N23 = N2;
                }
                checkCurrentWallpaper(createdAccents, true);
            }
        }

        private void checkCurrentWallpaper(final ArrayList<ThemeAccent> accents, final boolean load) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ActionBar.Theme$PatternsLoader$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    Theme.PatternsLoader.this.m1430x67876319(accents, load);
                }
            });
        }

        /* renamed from: checkCurrentWallpaperInternal */
        public void m1430x67876319(ArrayList<ThemeAccent> accents, boolean load) {
            if (accents != null && Theme.currentTheme.themeAccents != null && !Theme.currentTheme.themeAccents.isEmpty() && accents.contains(Theme.currentTheme.getAccent(false))) {
                Theme.reloadWallpaper();
            }
            if (load) {
                if (this.watingForLoad != null) {
                    NotificationCenter.getInstance(this.account).addObserver(this, NotificationCenter.fileLoaded);
                    NotificationCenter.getInstance(this.account).addObserver(this, NotificationCenter.fileLoadFailed);
                    for (Map.Entry<String, LoadingPattern> entry : this.watingForLoad.entrySet()) {
                        LoadingPattern loadingPattern = entry.getValue();
                        FileLoader.getInstance(this.account).loadFile(ImageLocation.getForDocument(loadingPattern.pattern.document), "wallpaper", null, 0, 1);
                    }
                    return;
                }
                return;
            }
            HashMap<String, LoadingPattern> hashMap = this.watingForLoad;
            if (hashMap == null || hashMap.isEmpty()) {
                NotificationCenter.getInstance(this.account).removeObserver(this, NotificationCenter.fileLoaded);
                NotificationCenter.getInstance(this.account).removeObserver(this, NotificationCenter.fileLoadFailed);
            }
        }

        private Bitmap createWallpaperForAccent(Bitmap patternBitmap, boolean svg, File patternPath, ThemeAccent accent) {
            Bitmap patternBitmap2;
            Throwable e;
            File toFile;
            int patternColor;
            Drawable background;
            Integer color;
            Integer color2;
            Integer color3;
            try {
                toFile = accent.getPathToWallpaper();
            } catch (Throwable th) {
                e = th;
                patternBitmap2 = patternBitmap;
            }
            if (toFile == null) {
                return null;
            }
            ThemeInfo themeInfo = accent.parentTheme;
            HashMap<String, Integer> values = Theme.getThemeFileValues(null, themeInfo.assetName, null);
            Theme.checkIsDark(values, themeInfo);
            int backgroundAccent = accent.accentColor;
            int backgroundColor = (int) accent.backgroundOverrideColor;
            int backgroundGradientColor1 = (int) accent.backgroundGradientOverrideColor1;
            if (backgroundGradientColor1 == 0 && accent.backgroundGradientOverrideColor1 == 0) {
                if (backgroundColor != 0) {
                    backgroundAccent = backgroundColor;
                }
                Integer color4 = values.get(Theme.key_chat_wallpaper_gradient_to1);
                if (color4 != null) {
                    backgroundGradientColor1 = Theme.changeColorAccent(themeInfo, backgroundAccent, color4.intValue());
                }
            } else {
                backgroundAccent = 0;
            }
            int backgroundGradientColor2 = (int) accent.backgroundGradientOverrideColor2;
            if (backgroundGradientColor2 == 0 && accent.backgroundGradientOverrideColor2 == 0 && (color3 = values.get(Theme.key_chat_wallpaper_gradient_to2)) != null) {
                backgroundGradientColor2 = Theme.changeColorAccent(themeInfo, backgroundAccent, color3.intValue());
            }
            int backgroundGradientColor3 = (int) accent.backgroundGradientOverrideColor3;
            if (backgroundGradientColor3 == 0 && accent.backgroundGradientOverrideColor3 == 0 && (color2 = values.get(Theme.key_chat_wallpaper_gradient_to3)) != null) {
                backgroundGradientColor3 = Theme.changeColorAccent(themeInfo, backgroundAccent, color2.intValue());
            }
            if (backgroundColor == 0 && (color = values.get(Theme.key_chat_wallpaper)) != null) {
                backgroundColor = Theme.changeColorAccent(themeInfo, backgroundAccent, color.intValue());
            }
            if (backgroundGradientColor2 != 0) {
                background = null;
                patternColor = MotionBackgroundDrawable.getPatternColor(backgroundColor, backgroundGradientColor1, backgroundGradientColor2, backgroundGradientColor3);
            } else if (backgroundGradientColor1 != 0) {
                GradientDrawable.Orientation orientation = BackgroundGradientDrawable.getGradientOrientation(accent.backgroundRotation);
                Drawable background2 = new BackgroundGradientDrawable(orientation, new int[]{backgroundColor, backgroundGradientColor1});
                int patternColor2 = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(backgroundColor, backgroundGradientColor1));
                patternColor = patternColor2;
                background = background2;
            } else {
                background = new ColorDrawable(backgroundColor);
                patternColor = AndroidUtilities.getPatternColor(backgroundColor);
            }
            if (patternBitmap != null) {
                patternBitmap2 = patternBitmap;
            } else {
                int w = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
                int h = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
                if (!svg) {
                    patternBitmap2 = Theme.loadScreenSizedBitmap(new FileInputStream(patternPath), 0);
                } else {
                    patternBitmap2 = SvgHelper.getBitmap(patternPath, w, h, false);
                }
            }
            try {
                if (background != null) {
                    Bitmap dst = Bitmap.createBitmap(patternBitmap2.getWidth(), patternBitmap2.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(dst);
                    if (background != null) {
                        background.setBounds(0, 0, patternBitmap2.getWidth(), patternBitmap2.getHeight());
                        background.draw(canvas);
                    }
                    Paint paint = new Paint(2);
                    paint.setColorFilter(new PorterDuffColorFilter(patternColor, PorterDuff.Mode.SRC_IN));
                    paint.setAlpha((int) (Math.abs(accent.patternIntensity) * 255.0f));
                    canvas.drawBitmap(patternBitmap2, 0.0f, 0.0f, paint);
                    dst.compress(Bitmap.CompressFormat.JPEG, 87, new FileOutputStream(toFile));
                } else {
                    FileOutputStream stream = new FileOutputStream(toFile);
                    patternBitmap2.compress(Bitmap.CompressFormat.PNG, 87, stream);
                    stream.close();
                }
            } catch (Throwable th2) {
                e = th2;
                FileLog.e(e);
                return patternBitmap2;
            }
            return patternBitmap2;
        }

        @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
        public void didReceivedNotification(int id, int account, Object... args) {
            if (this.watingForLoad == null) {
                return;
            }
            if (id == NotificationCenter.fileLoaded) {
                String location = (String) args[0];
                final LoadingPattern loadingPattern = this.watingForLoad.remove(location);
                if (loadingPattern != null) {
                    Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.ActionBar.Theme$PatternsLoader$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            Theme.PatternsLoader.this.m1431x5f1721e(loadingPattern);
                        }
                    });
                }
            } else if (id == NotificationCenter.fileLoadFailed) {
                String location2 = (String) args[0];
                if (this.watingForLoad.remove(location2) != null) {
                    checkCurrentWallpaper(null, false);
                }
            }
        }

        /* renamed from: lambda$didReceivedNotification$3$org-telegram-ui-ActionBar-Theme$PatternsLoader */
        public /* synthetic */ void m1431x5f1721e(LoadingPattern loadingPattern) {
            ArrayList<ThemeAccent> createdAccents = null;
            TLRPC.TL_wallPaper wallPaper = loadingPattern.pattern;
            File patternPath = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(wallPaper.document, true);
            Bitmap patternBitmap = null;
            int N = loadingPattern.accents.size();
            for (int a = 0; a < N; a++) {
                ThemeAccent accent = loadingPattern.accents.get(a);
                if (accent.patternSlug.equals(wallPaper.slug)) {
                    patternBitmap = createWallpaperForAccent(patternBitmap, "application/x-tgwallpattern".equals(wallPaper.document.mime_type), patternPath, accent);
                    if (createdAccents == null) {
                        createdAccents = new ArrayList<>();
                        createdAccents.add(accent);
                    }
                }
            }
            if (patternBitmap != null) {
                patternBitmap.recycle();
            }
            checkCurrentWallpaper(createdAccents, false);
        }
    }

    /* loaded from: classes4.dex */
    public static class ThemeAccent {
        public int accentColor;
        public int accentColor2;
        public int account;
        public long backgroundGradientOverrideColor1;
        public long backgroundGradientOverrideColor2;
        public long backgroundGradientOverrideColor3;
        public long backgroundOverrideColor;
        public int backgroundRotation;
        public int id;
        public TLRPC.TL_theme info;
        public boolean isDefault;
        public int myMessagesAccentColor;
        public boolean myMessagesAnimated;
        public int myMessagesGradientAccentColor1;
        public int myMessagesGradientAccentColor2;
        public int myMessagesGradientAccentColor3;
        public OverrideWallpaperInfo overrideWallpaper;
        public ThemeInfo parentTheme;
        public String pathToFile;
        public TLRPC.TL_wallPaper pattern;
        public float patternIntensity;
        public boolean patternMotion;
        public String patternSlug;
        private float[] tempHSV;
        public TLRPC.InputFile uploadedFile;
        public TLRPC.InputFile uploadedThumb;
        public String uploadingFile;
        public String uploadingThumb;

        ThemeAccent() {
            this.backgroundRotation = 45;
            this.patternSlug = "";
            this.tempHSV = new float[3];
        }

        public ThemeAccent(ThemeAccent other) {
            this.backgroundRotation = 45;
            this.patternSlug = "";
            this.tempHSV = new float[3];
            this.id = other.id;
            this.parentTheme = other.parentTheme;
            this.accentColor = other.accentColor;
            this.myMessagesAccentColor = other.myMessagesAccentColor;
            this.myMessagesGradientAccentColor1 = other.myMessagesGradientAccentColor1;
            this.myMessagesGradientAccentColor2 = other.myMessagesGradientAccentColor2;
            this.myMessagesGradientAccentColor3 = other.myMessagesGradientAccentColor3;
            this.myMessagesAnimated = other.myMessagesAnimated;
            this.backgroundOverrideColor = other.backgroundOverrideColor;
            this.backgroundGradientOverrideColor1 = other.backgroundGradientOverrideColor1;
            this.backgroundGradientOverrideColor2 = other.backgroundGradientOverrideColor2;
            this.backgroundGradientOverrideColor3 = other.backgroundGradientOverrideColor3;
            this.backgroundRotation = other.backgroundRotation;
            this.patternSlug = other.patternSlug;
            this.patternIntensity = other.patternIntensity;
            this.patternMotion = other.patternMotion;
            this.info = other.info;
            this.pattern = other.pattern;
            this.account = other.account;
            this.pathToFile = other.pathToFile;
            this.uploadingThumb = other.uploadingThumb;
            this.uploadingFile = other.uploadingFile;
            this.uploadedThumb = other.uploadedThumb;
            this.uploadedFile = other.uploadedFile;
            this.overrideWallpaper = other.overrideWallpaper;
        }

        /* JADX WARN: Code restructure failed: missing block: B:16:0x0083, code lost:
            r14 = (java.lang.Integer) org.telegram.ui.ActionBar.Theme.defaultColors.get(r13);
         */
        /* JADX WARN: Code restructure failed: missing block: B:79:0x0181, code lost:
            r12 = (java.lang.Integer) org.telegram.ui.ActionBar.Theme.defaultColors.get(r10);
         */
        /* JADX WARN: Code restructure failed: missing block: B:95:0x01db, code lost:
            r12 = (java.lang.Integer) org.telegram.ui.ActionBar.Theme.defaultColors.get(r10);
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean fillAccentColors(java.util.HashMap<java.lang.String, java.lang.Integer> r27, java.util.HashMap<java.lang.String, java.lang.Integer> r28) {
            /*
                Method dump skipped, instructions count: 1710
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.ThemeAccent.fillAccentColors(java.util.HashMap, java.util.HashMap):boolean");
        }

        private int setHue(int color, int hueFromColor) {
            Color.colorToHSV(hueFromColor, this.tempHSV);
            float[] fArr = this.tempHSV;
            float hue = fArr[0];
            Color.colorToHSV(color, fArr);
            float[] fArr2 = this.tempHSV;
            if (fArr2[1] > 0.02f) {
                fArr2[0] = hue;
            }
            return Color.HSVToColor(Color.alpha(color), this.tempHSV);
        }

        private int bubbleSelectedOverlay(int bubble, int accentColor) {
            Color.colorToHSV(accentColor, this.tempHSV);
            float[] fArr = this.tempHSV;
            float h = fArr[0];
            Color.colorToHSV(bubble, fArr);
            float[] fArr2 = this.tempHSV;
            if (fArr2[1] <= 0.0f) {
                fArr2[0] = h;
            }
            fArr2[1] = Math.max(0.0f, Math.min(1.0f, fArr2[1] + 0.6f));
            float[] fArr3 = this.tempHSV;
            fArr3[2] = Math.max(0.0f, Math.min(1.0f, fArr3[2] - 0.05f));
            return Color.HSVToColor(30, this.tempHSV);
        }

        private int textSelectionBackground(boolean isOut, int bubble, int accentColor) {
            Color.colorToHSV(accentColor, this.tempHSV);
            float[] fArr = this.tempHSV;
            float h = fArr[0];
            Color.colorToHSV(bubble, fArr);
            float[] fArr2 = this.tempHSV;
            if (fArr2[1] <= 0.0f || (fArr2[0] > 45.0f && fArr2[0] < 85.0f)) {
                fArr2[0] = h;
            }
            fArr2[1] = Math.max(0.0f, Math.min(1.0f, fArr2[1] + (fArr2[2] > 0.85f ? 0.25f : 0.45f)));
            float[] fArr3 = this.tempHSV;
            fArr3[2] = Math.max(0.0f, Math.min(1.0f, fArr3[2] - 0.15f));
            return Color.HSVToColor(80, this.tempHSV);
        }

        private int textSelectionHandle(int bubble, int accentColor) {
            Color.colorToHSV(accentColor, this.tempHSV);
            float[] fArr = this.tempHSV;
            float h = fArr[0];
            Color.colorToHSV(bubble, fArr);
            float[] fArr2 = this.tempHSV;
            if (fArr2[1] <= 0.0f || (fArr2[0] > 45.0f && fArr2[0] < 85.0f)) {
                fArr2[0] = h;
            }
            fArr2[1] = Math.max(0.0f, Math.min(1.0f, fArr2[1] + 0.6f));
            float[] fArr3 = this.tempHSV;
            fArr3[2] = Math.max(0.0f, Math.min(1.0f, fArr3[2] - (fArr3[2] > 0.7f ? 0.25f : 0.125f)));
            return Theme.blendOver(bubble, Color.HSVToColor(255, this.tempHSV));
        }

        private int linkSelectionBackground(int linkColor, int bgColor, boolean isDarkTheme) {
            Color.colorToHSV(ColorUtils.blendARGB(linkColor, bgColor, 0.25f), this.tempHSV);
            float[] fArr = this.tempHSV;
            float f = 0.1f;
            fArr[1] = Math.max(0.0f, Math.min(1.0f, fArr[1] - 0.1f));
            float[] fArr2 = this.tempHSV;
            float f2 = fArr2[2];
            if (!isDarkTheme) {
                f = 0.0f;
            }
            fArr2[2] = Math.max(0.0f, Math.min(1.0f, f2 + f));
            return Color.HSVToColor(51, this.tempHSV);
        }

        private int averageColor(HashMap<String, Integer> colors, String... keys) {
            int r = 0;
            int g = 0;
            int b = 0;
            int c = 0;
            for (int i = 0; i < keys.length; i++) {
                if (colors.containsKey(keys[i])) {
                    try {
                        int color = colors.get(keys[i]).intValue();
                        r += Color.red(color);
                        g += Color.green(color);
                        b += Color.blue(color);
                        c++;
                    } catch (Exception e) {
                    }
                }
            }
            if (c == 0) {
                return 0;
            }
            return Color.argb(255, r / c, g / c, b / c);
        }

        public File getPathToWallpaper() {
            if (this.id < 100) {
                if (TextUtils.isEmpty(this.patternSlug)) {
                    return null;
                }
                return new File(ApplicationLoader.getFilesDirFixed(), String.format(Locale.US, "%s_%d_%s_v5.jpg", this.parentTheme.getKey(), Integer.valueOf(this.id), this.patternSlug));
            } else if (TextUtils.isEmpty(this.patternSlug)) {
                return null;
            } else {
                return new File(ApplicationLoader.getFilesDirFixed(), String.format(Locale.US, "%s_%d_%s_v8_debug.jpg", this.parentTheme.getKey(), Integer.valueOf(this.id), this.patternSlug));
            }
        }

        public File saveToFile() {
            String wallpaperLink;
            File dir = AndroidUtilities.getSharingDirectory();
            dir.mkdirs();
            File path = new File(dir, String.format(Locale.US, "%s_%d.attheme", this.parentTheme.getKey(), Integer.valueOf(this.id)));
            HashMap<String, Integer> currentColorsNoAccent = Theme.getThemeFileValues(null, this.parentTheme.assetName, null);
            HashMap<String, Integer> currentColors = new HashMap<>(currentColorsNoAccent);
            fillAccentColors(currentColorsNoAccent, currentColors);
            if (TextUtils.isEmpty(this.patternSlug)) {
                wallpaperLink = null;
            } else {
                StringBuilder modes = new StringBuilder();
                if (this.patternMotion) {
                    modes.append("motion");
                }
                Integer selectedColor = currentColors.get(Theme.key_chat_wallpaper);
                if (selectedColor == null) {
                    selectedColor = -1;
                }
                Integer selectedGradientColor1 = currentColors.get(Theme.key_chat_wallpaper_gradient_to1);
                if (selectedGradientColor1 == null) {
                    selectedGradientColor1 = 0;
                }
                Integer selectedGradientColor2 = currentColors.get(Theme.key_chat_wallpaper_gradient_to2);
                if (selectedGradientColor2 == null) {
                    selectedGradientColor2 = 0;
                }
                Integer selectedGradientColor3 = currentColors.get(Theme.key_chat_wallpaper_gradient_to3);
                if (selectedGradientColor3 == null) {
                    selectedGradientColor3 = 0;
                }
                Integer selectedGradientRotation = currentColors.get(Theme.key_chat_wallpaper_gradient_rotation);
                if (selectedGradientRotation == null) {
                    selectedGradientRotation = 45;
                }
                String color = String.format("%02x%02x%02x", Integer.valueOf(((byte) (selectedColor.intValue() >> 16)) & 255), Integer.valueOf(((byte) (selectedColor.intValue() >> 8)) & 255), Byte.valueOf((byte) (selectedColor.intValue() & 255))).toLowerCase();
                String color2 = selectedGradientColor1.intValue() != 0 ? String.format("%02x%02x%02x", Integer.valueOf(((byte) (selectedGradientColor1.intValue() >> 16)) & 255), Integer.valueOf(((byte) (selectedGradientColor1.intValue() >> 8)) & 255), Byte.valueOf((byte) (selectedGradientColor1.intValue() & 255))).toLowerCase() : null;
                String color3 = selectedGradientColor2.intValue() != 0 ? String.format("%02x%02x%02x", Integer.valueOf(((byte) (selectedGradientColor2.intValue() >> 16)) & 255), Integer.valueOf(((byte) (selectedGradientColor2.intValue() >> 8)) & 255), Byte.valueOf((byte) (selectedGradientColor2.intValue() & 255))).toLowerCase() : null;
                String color4 = selectedGradientColor3.intValue() != 0 ? String.format("%02x%02x%02x", Integer.valueOf(((byte) (selectedGradientColor3.intValue() >> 16)) & 255), Integer.valueOf(((byte) (selectedGradientColor3.intValue() >> 8)) & 255), Byte.valueOf((byte) (selectedGradientColor3.intValue() & 255))).toLowerCase() : null;
                if (color2 == null || color3 == null) {
                    if (color2 != null) {
                        color = (color + "-" + color2) + "&rotation=" + selectedGradientRotation;
                    }
                } else if (color4 != null) {
                    color = color + "~" + color2 + "~" + color3 + "~" + color4;
                } else {
                    color = color + "~" + color2 + "~" + color3;
                }
                wallpaperLink = "https://attheme.org?slug=" + this.patternSlug + "&intensity=" + ((int) (this.patternIntensity * 100.0f)) + "&bg_color=" + color;
                if (modes.length() > 0) {
                    wallpaperLink = wallpaperLink + "&mode=" + modes.toString();
                }
            }
            StringBuilder result = new StringBuilder();
            for (Map.Entry<String, Integer> entry : currentColors.entrySet()) {
                String key = entry.getKey();
                if (wallpaperLink == null || (!Theme.key_chat_wallpaper.equals(key) && !Theme.key_chat_wallpaper_gradient_to1.equals(key) && !Theme.key_chat_wallpaper_gradient_to2.equals(key) && !Theme.key_chat_wallpaper_gradient_to3.equals(key))) {
                    result.append(key);
                    result.append("=");
                    result.append(entry.getValue());
                    result.append("\n");
                }
            }
            FileOutputStream stream = null;
            try {
                try {
                    try {
                        stream = new FileOutputStream(path);
                        stream.write(AndroidUtilities.getStringBytes(result.toString()));
                        if (!TextUtils.isEmpty(wallpaperLink)) {
                            stream.write(AndroidUtilities.getStringBytes("WLS=" + wallpaperLink + "\n"));
                        }
                        stream.close();
                    } catch (Exception e) {
                        FileLog.e(e);
                        if (stream != null) {
                            stream.close();
                        }
                    }
                } catch (Throwable th) {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Exception e2) {
                            FileLog.e(e2);
                        }
                    }
                    throw th;
                }
            } catch (Exception e3) {
                FileLog.e(e3);
            }
            return path;
        }
    }

    public static int blendOver(int A, int B) {
        float aB = Color.alpha(B) / 255.0f;
        float aA = Color.alpha(A) / 255.0f;
        float aC = ((1.0f - aB) * aA) + aB;
        if (aC == 0.0f) {
            return 0;
        }
        return Color.argb((int) (255.0f * aC), (int) (((Color.red(B) * aB) + ((Color.red(A) * aA) * (1.0f - aB))) / aC), (int) (((Color.green(B) * aB) + ((Color.green(A) * aA) * (1.0f - aB))) / aC), (int) (((Color.blue(B) * aB) + ((Color.blue(A) * aA) * (1.0f - aB))) / aC));
    }

    public static int reverseBlendOver(float ax, int y, int z) {
        float ay = Color.alpha(y) / 255.0f;
        float az = Color.alpha(z) / 255.0f;
        return Color.argb((int) (255.0f * ax), (int) ((((Color.red(y) * ay) * (1.0f - ax)) - (Color.red(z) * az)) / ax), (int) ((((Color.green(y) * ay) * (1.0f - ax)) - (Color.green(z) * az)) / ax), (int) ((((Color.blue(y) * ay) * (1.0f - ax)) - (Color.blue(z) * az)) / ax));
    }

    /* loaded from: classes4.dex */
    public static class OverrideWallpaperInfo {
        public long accessHash;
        public int color;
        public String fileName;
        public int gradientColor1;
        public int gradientColor2;
        public int gradientColor3;
        public float intensity;
        public boolean isBlurred;
        public boolean isMotion;
        public String originalFileName;
        public ThemeAccent parentAccent;
        public ThemeInfo parentTheme;
        public int rotation;
        public String slug;
        public long wallpaperId;

        public OverrideWallpaperInfo() {
            this.fileName = "";
            this.originalFileName = "";
            this.slug = "";
        }

        public OverrideWallpaperInfo(OverrideWallpaperInfo info, ThemeInfo themeInfo, ThemeAccent accent) {
            this.fileName = "";
            this.originalFileName = "";
            this.slug = "";
            this.slug = info.slug;
            this.color = info.color;
            this.gradientColor1 = info.gradientColor1;
            this.gradientColor2 = info.gradientColor2;
            this.gradientColor3 = info.gradientColor3;
            this.rotation = info.rotation;
            this.isBlurred = info.isBlurred;
            this.isMotion = info.isMotion;
            this.intensity = info.intensity;
            this.parentTheme = themeInfo;
            this.parentAccent = accent;
            if (!TextUtils.isEmpty(info.fileName)) {
                try {
                    File fromFile = new File(ApplicationLoader.getFilesDirFixed(), info.fileName);
                    File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                    String generateWallpaperName = this.parentTheme.generateWallpaperName(this.parentAccent, false);
                    this.fileName = generateWallpaperName;
                    File toFile = new File(filesDirFixed, generateWallpaperName);
                    AndroidUtilities.copyFile(fromFile, toFile);
                } catch (Exception e) {
                    this.fileName = "";
                    FileLog.e(e);
                }
            } else {
                this.fileName = "";
            }
            if (!TextUtils.isEmpty(info.originalFileName)) {
                if (!info.originalFileName.equals(info.fileName)) {
                    try {
                        File fromFile2 = new File(ApplicationLoader.getFilesDirFixed(), info.originalFileName);
                        File filesDirFixed2 = ApplicationLoader.getFilesDirFixed();
                        String generateWallpaperName2 = this.parentTheme.generateWallpaperName(this.parentAccent, true);
                        this.originalFileName = generateWallpaperName2;
                        File toFile2 = new File(filesDirFixed2, generateWallpaperName2);
                        AndroidUtilities.copyFile(fromFile2, toFile2);
                        return;
                    } catch (Exception e2) {
                        this.originalFileName = "";
                        FileLog.e(e2);
                        return;
                    }
                }
                this.originalFileName = this.fileName;
                return;
            }
            this.originalFileName = "";
        }

        public boolean isDefault() {
            return Theme.DEFAULT_BACKGROUND_SLUG.equals(this.slug);
        }

        public boolean isColor() {
            return Theme.COLOR_BACKGROUND_SLUG.equals(this.slug);
        }

        public boolean isTheme() {
            return Theme.THEME_BACKGROUND_SLUG.equals(this.slug);
        }

        public void saveOverrideWallpaper() {
            ThemeInfo themeInfo = this.parentTheme;
            if (themeInfo != null) {
                if (this.parentAccent == null && themeInfo.overrideWallpaper != this) {
                    return;
                }
                ThemeAccent themeAccent = this.parentAccent;
                if (themeAccent != null && themeAccent.overrideWallpaper != this) {
                    return;
                }
                save();
            }
        }

        private String getKey() {
            if (this.parentAccent != null) {
                return this.parentTheme.name + "_" + this.parentAccent.id + "_owp";
            }
            return this.parentTheme.name + "_owp";
        }

        public void save() {
            try {
                String key = getKey();
                SharedPreferences themeConfig = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
                SharedPreferences.Editor editor = themeConfig.edit();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("wall", this.fileName);
                jsonObject.put("owall", this.originalFileName);
                jsonObject.put("pColor", this.color);
                jsonObject.put("pGrColor", this.gradientColor1);
                jsonObject.put("pGrColor2", this.gradientColor2);
                jsonObject.put("pGrColor3", this.gradientColor3);
                jsonObject.put("pGrAngle", this.rotation);
                String str = this.slug;
                if (str == null) {
                    str = "";
                }
                jsonObject.put("wallSlug", str);
                jsonObject.put("wBlur", this.isBlurred);
                jsonObject.put("wMotion", this.isMotion);
                jsonObject.put("pIntensity", this.intensity);
                editor.putString(key, jsonObject.toString());
                editor.commit();
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }

        public void delete() {
            String key = getKey();
            SharedPreferences themeConfig = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
            themeConfig.edit().remove(key).commit();
            new File(ApplicationLoader.getFilesDirFixed(), this.fileName).delete();
            new File(ApplicationLoader.getFilesDirFixed(), this.originalFileName).delete();
        }
    }

    /* loaded from: classes4.dex */
    public static class ThemeInfo implements NotificationCenter.NotificationCenterDelegate {
        private static final int DARK = 1;
        private static final int LIGHT = 0;
        private static final int UNKNOWN = -1;
        public int accentBaseColor;
        public LongSparseArray<ThemeAccent> accentsByThemeId;
        public int account;
        public String assetName;
        public boolean badWallpaper;
        public LongSparseArray<ThemeAccent> chatAccentsByThemeId;
        public int currentAccentId;
        public int defaultAccentCount;
        public boolean firstAccentIsDefault;
        public TLRPC.TL_theme info;
        public boolean isBlured;
        private int isDark;
        public boolean isMotion;
        public int lastAccentId;
        public int lastChatThemeId;
        public boolean loaded;
        private String loadingThemeWallpaperName;
        public String name;
        private String newPathToWallpaper;
        public OverrideWallpaperInfo overrideWallpaper;
        public String pathToFile;
        public String pathToWallpaper;
        public int patternBgColor;
        public int patternBgGradientColor1;
        public int patternBgGradientColor2;
        public int patternBgGradientColor3;
        public int patternBgGradientRotation;
        public int patternIntensity;
        public int prevAccentId;
        private int previewBackgroundColor;
        public int previewBackgroundGradientColor1;
        public int previewBackgroundGradientColor2;
        public int previewBackgroundGradientColor3;
        private int previewInColor;
        private int previewOutColor;
        public boolean previewParsed;
        public int previewWallpaperOffset;
        public String slug;
        public int sortIndex;
        public ArrayList<ThemeAccent> themeAccents;
        public SparseArray<ThemeAccent> themeAccentsMap;
        public boolean themeLoaded;
        public TLRPC.InputFile uploadedFile;
        public TLRPC.InputFile uploadedThumb;
        public String uploadingFile;
        public String uploadingThumb;

        ThemeInfo() {
            this.patternBgGradientRotation = 45;
            this.loaded = true;
            this.themeLoaded = true;
            this.prevAccentId = -1;
            this.chatAccentsByThemeId = new LongSparseArray<>();
            this.lastChatThemeId = 0;
            this.lastAccentId = 100;
            this.isDark = -1;
        }

        public ThemeInfo(ThemeInfo other) {
            this.patternBgGradientRotation = 45;
            this.loaded = true;
            this.themeLoaded = true;
            this.prevAccentId = -1;
            this.chatAccentsByThemeId = new LongSparseArray<>();
            this.lastChatThemeId = 0;
            this.lastAccentId = 100;
            this.isDark = -1;
            this.name = other.name;
            this.pathToFile = other.pathToFile;
            this.pathToWallpaper = other.pathToWallpaper;
            this.assetName = other.assetName;
            this.slug = other.slug;
            this.badWallpaper = other.badWallpaper;
            this.isBlured = other.isBlured;
            this.isMotion = other.isMotion;
            this.patternBgColor = other.patternBgColor;
            this.patternBgGradientColor1 = other.patternBgGradientColor1;
            this.patternBgGradientColor2 = other.patternBgGradientColor2;
            this.patternBgGradientColor3 = other.patternBgGradientColor3;
            this.patternBgGradientRotation = other.patternBgGradientRotation;
            this.patternIntensity = other.patternIntensity;
            this.account = other.account;
            this.info = other.info;
            this.loaded = other.loaded;
            this.uploadingThumb = other.uploadingThumb;
            this.uploadingFile = other.uploadingFile;
            this.uploadedThumb = other.uploadedThumb;
            this.uploadedFile = other.uploadedFile;
            this.previewBackgroundColor = other.previewBackgroundColor;
            this.previewBackgroundGradientColor1 = other.previewBackgroundGradientColor1;
            this.previewBackgroundGradientColor2 = other.previewBackgroundGradientColor2;
            this.previewBackgroundGradientColor3 = other.previewBackgroundGradientColor3;
            this.previewWallpaperOffset = other.previewWallpaperOffset;
            this.previewInColor = other.previewInColor;
            this.previewOutColor = other.previewOutColor;
            this.firstAccentIsDefault = other.firstAccentIsDefault;
            this.previewParsed = other.previewParsed;
            this.themeLoaded = other.themeLoaded;
            this.sortIndex = other.sortIndex;
            this.defaultAccentCount = other.defaultAccentCount;
            this.accentBaseColor = other.accentBaseColor;
            this.currentAccentId = other.currentAccentId;
            this.prevAccentId = other.prevAccentId;
            this.themeAccentsMap = other.themeAccentsMap;
            this.themeAccents = other.themeAccents;
            this.accentsByThemeId = other.accentsByThemeId;
            this.lastAccentId = other.lastAccentId;
            this.loadingThemeWallpaperName = other.loadingThemeWallpaperName;
            this.newPathToWallpaper = other.newPathToWallpaper;
            this.overrideWallpaper = other.overrideWallpaper;
        }

        JSONObject getSaveJson() {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(CommonProperties.NAME, this.name);
                jsonObject.put("path", this.pathToFile);
                jsonObject.put("account", this.account);
                if (this.info != null) {
                    SerializedData data = new SerializedData(this.info.getObjectSize());
                    this.info.serializeToStream(data);
                    jsonObject.put("info", Utilities.bytesToHex(data.toByteArray()));
                }
                jsonObject.put("loaded", this.loaded);
                return jsonObject;
            } catch (Exception e) {
                FileLog.e(e);
                return null;
            }
        }

        public void loadWallpapers(SharedPreferences sharedPreferences) {
            ArrayList<ThemeAccent> arrayList = this.themeAccents;
            if (arrayList != null && !arrayList.isEmpty()) {
                int N = this.themeAccents.size();
                for (int a = 0; a < N; a++) {
                    ThemeAccent accent = this.themeAccents.get(a);
                    loadOverrideWallpaper(sharedPreferences, accent, this.name + "_" + accent.id + "_owp");
                }
                return;
            }
            loadOverrideWallpaper(sharedPreferences, null, this.name + "_owp");
        }

        private void loadOverrideWallpaper(SharedPreferences sharedPreferences, ThemeAccent accent, String key) {
            try {
                String json = sharedPreferences.getString(key, null);
                if (TextUtils.isEmpty(json)) {
                    return;
                }
                JSONObject object = new JSONObject(json);
                OverrideWallpaperInfo wallpaperInfo = new OverrideWallpaperInfo();
                wallpaperInfo.fileName = object.getString("wall");
                wallpaperInfo.originalFileName = object.getString("owall");
                wallpaperInfo.color = object.getInt("pColor");
                wallpaperInfo.gradientColor1 = object.getInt("pGrColor");
                wallpaperInfo.gradientColor2 = object.optInt("pGrColor2");
                wallpaperInfo.gradientColor3 = object.optInt("pGrColor3");
                wallpaperInfo.rotation = object.getInt("pGrAngle");
                wallpaperInfo.slug = object.getString("wallSlug");
                wallpaperInfo.isBlurred = object.getBoolean("wBlur");
                wallpaperInfo.isMotion = object.getBoolean("wMotion");
                wallpaperInfo.intensity = (float) object.getDouble("pIntensity");
                wallpaperInfo.parentTheme = this;
                wallpaperInfo.parentAccent = accent;
                if (accent != null) {
                    accent.overrideWallpaper = wallpaperInfo;
                } else {
                    this.overrideWallpaper = wallpaperInfo;
                }
                if (object.has("wallId")) {
                    long id = object.getLong("wallId");
                    if (id == 1000001) {
                        wallpaperInfo.slug = Theme.DEFAULT_BACKGROUND_SLUG;
                    }
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }

        public void setOverrideWallpaper(OverrideWallpaperInfo info) {
            if (this.overrideWallpaper == info) {
                return;
            }
            ThemeAccent accent = getAccent(false);
            OverrideWallpaperInfo overrideWallpaperInfo = this.overrideWallpaper;
            if (overrideWallpaperInfo != null) {
                overrideWallpaperInfo.delete();
            }
            if (info != null) {
                info.parentAccent = accent;
                info.parentTheme = this;
                info.save();
            }
            this.overrideWallpaper = info;
            if (accent != null) {
                accent.overrideWallpaper = info;
            }
        }

        public String getName() {
            if ("Blue".equals(this.name)) {
                return LocaleController.getString("ThemeClassic", R.string.ThemeClassic);
            }
            if ("Dark Blue".equals(this.name)) {
                return LocaleController.getString("ThemeDark", R.string.ThemeDark);
            }
            if ("Arctic Blue".equals(this.name)) {
                return LocaleController.getString("ThemeArcticBlue", R.string.ThemeArcticBlue);
            }
            if ("Day".equals(this.name)) {
                return LocaleController.getString("ThemeDay", R.string.ThemeDay);
            }
            if ("Night".equals(this.name)) {
                return LocaleController.getString("ThemeNight", R.string.ThemeNight);
            }
            TLRPC.TL_theme tL_theme = this.info;
            return tL_theme != null ? tL_theme.title : this.name;
        }

        public void setCurrentAccentId(int id) {
            this.currentAccentId = id;
            ThemeAccent accent = getAccent(false);
            if (accent != null) {
                this.overrideWallpaper = accent.overrideWallpaper;
            }
        }

        public String generateWallpaperName(ThemeAccent accent, boolean original) {
            String str;
            String str2;
            if (accent == null) {
                accent = getAccent(false);
            }
            if (accent != null) {
                StringBuilder sb = new StringBuilder();
                if (original) {
                    str2 = this.name + "_" + accent.id + "_wp_o";
                } else {
                    str2 = this.name + "_" + accent.id + "_wp";
                }
                sb.append(str2);
                sb.append(Utilities.random.nextInt());
                sb.append(".jpg");
                return sb.toString();
            }
            StringBuilder sb2 = new StringBuilder();
            if (original) {
                str = this.name + "_wp_o";
            } else {
                str = this.name + "_wp";
            }
            sb2.append(str);
            sb2.append(Utilities.random.nextInt());
            sb2.append(".jpg");
            return sb2.toString();
        }

        public void setPreviewInColor(int color) {
            this.previewInColor = color;
        }

        public void setPreviewOutColor(int color) {
            this.previewOutColor = color;
        }

        public void setPreviewBackgroundColor(int color) {
            this.previewBackgroundColor = color;
        }

        public int getPreviewInColor() {
            if (this.firstAccentIsDefault && this.currentAccentId == Theme.DEFALT_THEME_ACCENT_ID) {
                return -1;
            }
            return this.previewInColor;
        }

        public int getPreviewOutColor() {
            if (this.firstAccentIsDefault && this.currentAccentId == Theme.DEFALT_THEME_ACCENT_ID) {
                return -983328;
            }
            return this.previewOutColor;
        }

        public int getPreviewBackgroundColor() {
            if (this.firstAccentIsDefault && this.currentAccentId == Theme.DEFALT_THEME_ACCENT_ID) {
                return -3155485;
            }
            return this.previewBackgroundColor;
        }

        public boolean isDefaultMyMessagesBubbles() {
            if (!this.firstAccentIsDefault) {
                return false;
            }
            if (this.currentAccentId == Theme.DEFALT_THEME_ACCENT_ID) {
                return true;
            }
            ThemeAccent defaultAccent = this.themeAccentsMap.get(Theme.DEFALT_THEME_ACCENT_ID);
            ThemeAccent accent = this.themeAccentsMap.get(this.currentAccentId);
            return defaultAccent != null && accent != null && defaultAccent.myMessagesAccentColor == accent.myMessagesAccentColor && defaultAccent.myMessagesGradientAccentColor1 == accent.myMessagesGradientAccentColor1 && defaultAccent.myMessagesGradientAccentColor2 == accent.myMessagesGradientAccentColor2 && defaultAccent.myMessagesGradientAccentColor3 == accent.myMessagesGradientAccentColor3 && defaultAccent.myMessagesAnimated == accent.myMessagesAnimated;
        }

        public boolean isDefaultMyMessages() {
            if (!this.firstAccentIsDefault) {
                return false;
            }
            if (this.currentAccentId == Theme.DEFALT_THEME_ACCENT_ID) {
                return true;
            }
            ThemeAccent defaultAccent = this.themeAccentsMap.get(Theme.DEFALT_THEME_ACCENT_ID);
            ThemeAccent accent = this.themeAccentsMap.get(this.currentAccentId);
            return defaultAccent != null && accent != null && defaultAccent.accentColor2 == accent.accentColor2 && defaultAccent.myMessagesAccentColor == accent.myMessagesAccentColor && defaultAccent.myMessagesGradientAccentColor1 == accent.myMessagesGradientAccentColor1 && defaultAccent.myMessagesGradientAccentColor2 == accent.myMessagesGradientAccentColor2 && defaultAccent.myMessagesGradientAccentColor3 == accent.myMessagesGradientAccentColor3 && defaultAccent.myMessagesAnimated == accent.myMessagesAnimated;
        }

        public boolean isDefaultMainAccent() {
            if (!this.firstAccentIsDefault) {
                return false;
            }
            if (this.currentAccentId == Theme.DEFALT_THEME_ACCENT_ID) {
                return true;
            }
            ThemeAccent defaultAccent = this.themeAccentsMap.get(Theme.DEFALT_THEME_ACCENT_ID);
            ThemeAccent accent = this.themeAccentsMap.get(this.currentAccentId);
            return (accent == null || defaultAccent == null || defaultAccent.accentColor != accent.accentColor) ? false : true;
        }

        public boolean hasAccentColors() {
            return this.defaultAccentCount != 0;
        }

        public boolean isDark() {
            int i = this.isDark;
            if (i != -1) {
                return i == 1;
            }
            if ("Dark Blue".equals(this.name) || "Night".equals(this.name)) {
                this.isDark = 1;
            } else if ("Blue".equals(this.name) || "Arctic Blue".equals(this.name) || "Day".equals(this.name)) {
                this.isDark = 0;
            }
            if (this.isDark == -1) {
                String[] wallpaperLink = new String[1];
                HashMap<String, Integer> colors = Theme.getThemeFileValues(new File(this.pathToFile), null, wallpaperLink);
                Theme.checkIsDark(colors, this);
            }
            return this.isDark == 1;
        }

        public boolean isLight() {
            return this.pathToFile == null && !isDark();
        }

        public String getKey() {
            if (this.info != null) {
                return "remote" + this.info.id;
            }
            return this.name;
        }

        static ThemeInfo createWithJson(JSONObject object) {
            if (object == null) {
                return null;
            }
            try {
                ThemeInfo themeInfo = new ThemeInfo();
                themeInfo.name = object.getString(CommonProperties.NAME);
                themeInfo.pathToFile = object.getString("path");
                if (object.has("account")) {
                    themeInfo.account = object.getInt("account");
                }
                if (object.has("info")) {
                    SerializedData serializedData = new SerializedData(Utilities.hexToBytes(object.getString("info")));
                    themeInfo.info = TLRPC.Theme.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                }
                if (object.has("loaded")) {
                    themeInfo.loaded = object.getBoolean("loaded");
                }
                return themeInfo;
            } catch (Exception e) {
                FileLog.e(e);
                return null;
            }
        }

        static ThemeInfo createWithString(String string) {
            if (TextUtils.isEmpty(string)) {
                return null;
            }
            String[] args = string.split("\\|");
            if (args.length != 2) {
                return null;
            }
            ThemeInfo themeInfo = new ThemeInfo();
            themeInfo.name = args[0];
            themeInfo.pathToFile = args[1];
            return themeInfo;
        }

        private void setAccentColorOptions(int[] options) {
            setAccentColorOptions(options, null, null, null, null, null, null, null, null, null, null);
        }

        public void setAccentColorOptions(int[] accent, int[] myMessages, int[] myMessagesGradient, int[] background, int[] backgroundGradient1, int[] backgroundGradient2, int[] backgroundGradient3, int[] ids, String[] patternSlugs, int[] patternRotations, int[] patternIntensities) {
            this.defaultAccentCount = accent.length;
            this.themeAccents = new ArrayList<>();
            this.themeAccentsMap = new SparseArray<>();
            this.accentsByThemeId = new LongSparseArray<>();
            for (int a = 0; a < accent.length; a++) {
                ThemeAccent themeAccent = new ThemeAccent();
                themeAccent.id = ids != null ? ids[a] : a;
                if (Theme.isHome(themeAccent)) {
                    themeAccent.isDefault = true;
                }
                themeAccent.accentColor = accent[a];
                themeAccent.parentTheme = this;
                if (myMessages != null) {
                    themeAccent.myMessagesAccentColor = myMessages[a];
                }
                if (myMessagesGradient != null) {
                    themeAccent.myMessagesGradientAccentColor1 = myMessagesGradient[a];
                }
                if (background != null) {
                    themeAccent.backgroundOverrideColor = background[a];
                    if (this.firstAccentIsDefault && themeAccent.id == Theme.DEFALT_THEME_ACCENT_ID) {
                        themeAccent.backgroundOverrideColor = 4294967296L;
                    } else {
                        themeAccent.backgroundOverrideColor = background[a];
                    }
                }
                if (backgroundGradient1 != null) {
                    if (this.firstAccentIsDefault && themeAccent.id == Theme.DEFALT_THEME_ACCENT_ID) {
                        themeAccent.backgroundGradientOverrideColor1 = 4294967296L;
                    } else {
                        themeAccent.backgroundGradientOverrideColor1 = backgroundGradient1[a];
                    }
                }
                if (backgroundGradient2 != null) {
                    if (this.firstAccentIsDefault && themeAccent.id == Theme.DEFALT_THEME_ACCENT_ID) {
                        themeAccent.backgroundGradientOverrideColor2 = 4294967296L;
                    } else {
                        themeAccent.backgroundGradientOverrideColor2 = backgroundGradient2[a];
                    }
                }
                if (backgroundGradient3 != null) {
                    if (this.firstAccentIsDefault && themeAccent.id == Theme.DEFALT_THEME_ACCENT_ID) {
                        themeAccent.backgroundGradientOverrideColor3 = 4294967296L;
                    } else {
                        themeAccent.backgroundGradientOverrideColor3 = backgroundGradient3[a];
                    }
                }
                if (patternSlugs != null) {
                    themeAccent.patternIntensity = patternIntensities[a] / 100.0f;
                    themeAccent.backgroundRotation = patternRotations[a];
                    themeAccent.patternSlug = patternSlugs[a];
                }
                this.themeAccentsMap.put(themeAccent.id, themeAccent);
                this.themeAccents.add(themeAccent);
            }
            this.accentBaseColor = this.themeAccentsMap.get(0).accentColor;
        }

        public void loadThemeDocument() {
            this.loaded = false;
            this.loadingThemeWallpaperName = null;
            this.newPathToWallpaper = null;
            addObservers();
            FileLoader.getInstance(this.account).loadFile(this.info.document, this.info, 1, 1);
        }

        private void addObservers() {
            NotificationCenter.getInstance(this.account).addObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(this.account).addObserver(this, NotificationCenter.fileLoadFailed);
        }

        public void removeObservers() {
            NotificationCenter.getInstance(this.account).removeObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(this.account).removeObserver(this, NotificationCenter.fileLoadFailed);
        }

        public void onFinishLoadingRemoteTheme() {
            this.loaded = true;
            boolean z = false;
            this.previewParsed = false;
            Theme.saveOtherThemes(true);
            if (this == Theme.currentTheme && Theme.previousTheme == null) {
                NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                int i = NotificationCenter.needSetDayNightTheme;
                Object[] objArr = new Object[5];
                objArr[0] = this;
                if (this == Theme.currentNightTheme) {
                    z = true;
                }
                objArr[1] = Boolean.valueOf(z);
                objArr[2] = null;
                objArr[3] = -1;
                objArr[4] = Theme.fallbackKeys;
                globalInstance.postNotificationName(i, objArr);
            }
        }

        public static boolean accentEquals(ThemeAccent accent, TLRPC.ThemeSettings settings) {
            String patternSlug;
            int bottomColor = settings.message_colors.size() > 0 ? settings.message_colors.get(0).intValue() | (-16777216) : 0;
            int myMessagesGradientAccentColor1 = settings.message_colors.size() > 1 ? settings.message_colors.get(1).intValue() | (-16777216) : 0;
            if (bottomColor == myMessagesGradientAccentColor1) {
                myMessagesGradientAccentColor1 = 0;
            }
            int myMessagesGradientAccentColor2 = settings.message_colors.size() > 2 ? settings.message_colors.get(2).intValue() | (-16777216) : 0;
            int myMessagesGradientAccentColor3 = settings.message_colors.size() > 3 ? (-16777216) | settings.message_colors.get(3).intValue() : 0;
            int backgroundOverrideColor = 0;
            long backgroundGradientOverrideColor1 = 0;
            long backgroundGradientOverrideColor2 = 0;
            long backgroundGradientOverrideColor3 = 0;
            int backgroundRotation = 0;
            float patternIntensity = 0.0f;
            if (settings.wallpaper != null && settings.wallpaper.settings != null) {
                backgroundOverrideColor = Theme.getWallpaperColor(settings.wallpaper.settings.background_color);
                if (settings.wallpaper.settings.second_background_color == 0) {
                    backgroundGradientOverrideColor1 = 4294967296L;
                } else {
                    backgroundGradientOverrideColor1 = Theme.getWallpaperColor(settings.wallpaper.settings.second_background_color);
                }
                if (settings.wallpaper.settings.third_background_color == 0) {
                    backgroundGradientOverrideColor2 = 4294967296L;
                } else {
                    backgroundGradientOverrideColor2 = Theme.getWallpaperColor(settings.wallpaper.settings.third_background_color);
                }
                if (settings.wallpaper.settings.fourth_background_color == 0) {
                    backgroundGradientOverrideColor3 = 4294967296L;
                } else {
                    backgroundGradientOverrideColor3 = Theme.getWallpaperColor(settings.wallpaper.settings.fourth_background_color);
                }
                backgroundRotation = AndroidUtilities.getWallpaperRotation(settings.wallpaper.settings.rotation, false);
                if (!(settings.wallpaper instanceof TLRPC.TL_wallPaperNoFile) && settings.wallpaper.pattern) {
                    patternSlug = settings.wallpaper.slug;
                    patternIntensity = settings.wallpaper.settings.intensity / 100.0f;
                    String patternSlug2 = patternSlug;
                    if (settings.accent_color != accent.accentColor && settings.outbox_accent_color == accent.accentColor2 && bottomColor == accent.myMessagesAccentColor && myMessagesGradientAccentColor1 == accent.myMessagesGradientAccentColor1 && myMessagesGradientAccentColor2 == accent.myMessagesGradientAccentColor2 && myMessagesGradientAccentColor3 == accent.myMessagesGradientAccentColor3 && settings.message_colors_animated == accent.myMessagesAnimated) {
                        if (backgroundOverrideColor == accent.backgroundOverrideColor && backgroundGradientOverrideColor1 == accent.backgroundGradientOverrideColor1 && backgroundGradientOverrideColor2 == accent.backgroundGradientOverrideColor2 && backgroundGradientOverrideColor3 == accent.backgroundGradientOverrideColor3 && backgroundRotation == accent.backgroundRotation) {
                            if (TextUtils.equals(patternSlug2, accent.patternSlug) && Math.abs(patternIntensity - accent.patternIntensity) < 0.001d) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
            }
            patternSlug = null;
            String patternSlug22 = patternSlug;
            if (settings.accent_color != accent.accentColor) {
            }
            return false;
        }

        public static void fillAccentValues(ThemeAccent themeAccent, TLRPC.ThemeSettings settings) {
            themeAccent.accentColor = settings.accent_color;
            themeAccent.accentColor2 = settings.outbox_accent_color;
            themeAccent.myMessagesAccentColor = settings.message_colors.size() > 0 ? settings.message_colors.get(0).intValue() | (-16777216) : 0;
            themeAccent.myMessagesGradientAccentColor1 = settings.message_colors.size() > 1 ? settings.message_colors.get(1).intValue() | (-16777216) : 0;
            if (themeAccent.myMessagesAccentColor == themeAccent.myMessagesGradientAccentColor1) {
                themeAccent.myMessagesGradientAccentColor1 = 0;
            }
            themeAccent.myMessagesGradientAccentColor2 = settings.message_colors.size() > 2 ? settings.message_colors.get(2).intValue() | (-16777216) : 0;
            themeAccent.myMessagesGradientAccentColor3 = settings.message_colors.size() > 3 ? settings.message_colors.get(3).intValue() | (-16777216) : 0;
            themeAccent.myMessagesAnimated = settings.message_colors_animated;
            if (settings.wallpaper != null && settings.wallpaper.settings != null) {
                if (settings.wallpaper.settings.background_color == 0) {
                    themeAccent.backgroundOverrideColor = 4294967296L;
                } else {
                    themeAccent.backgroundOverrideColor = Theme.getWallpaperColor(settings.wallpaper.settings.background_color);
                }
                if ((settings.wallpaper.settings.flags & 16) != 0 && settings.wallpaper.settings.second_background_color == 0) {
                    themeAccent.backgroundGradientOverrideColor1 = 4294967296L;
                } else {
                    themeAccent.backgroundGradientOverrideColor1 = Theme.getWallpaperColor(settings.wallpaper.settings.second_background_color);
                }
                if ((settings.wallpaper.settings.flags & 32) != 0 && settings.wallpaper.settings.third_background_color == 0) {
                    themeAccent.backgroundGradientOverrideColor2 = 4294967296L;
                } else {
                    themeAccent.backgroundGradientOverrideColor2 = Theme.getWallpaperColor(settings.wallpaper.settings.third_background_color);
                }
                if ((settings.wallpaper.settings.flags & 64) != 0 && settings.wallpaper.settings.fourth_background_color == 0) {
                    themeAccent.backgroundGradientOverrideColor3 = 4294967296L;
                } else {
                    themeAccent.backgroundGradientOverrideColor3 = Theme.getWallpaperColor(settings.wallpaper.settings.fourth_background_color);
                }
                themeAccent.backgroundRotation = AndroidUtilities.getWallpaperRotation(settings.wallpaper.settings.rotation, false);
                if (!(settings.wallpaper instanceof TLRPC.TL_wallPaperNoFile) && settings.wallpaper.pattern) {
                    themeAccent.patternSlug = settings.wallpaper.slug;
                    themeAccent.patternIntensity = settings.wallpaper.settings.intensity / 100.0f;
                    themeAccent.patternMotion = settings.wallpaper.settings.motion;
                }
            }
        }

        public ThemeAccent createNewAccent(TLRPC.ThemeSettings settings) {
            ThemeAccent themeAccent = new ThemeAccent();
            fillAccentValues(themeAccent, settings);
            themeAccent.parentTheme = this;
            return themeAccent;
        }

        public ThemeAccent createNewAccent(TLRPC.TL_theme info, int account) {
            return createNewAccent(info, account, false, 0);
        }

        public ThemeAccent createNewAccent(TLRPC.TL_theme info, int account, boolean ignoreThemeInfoId, int settingsIndex) {
            if (info == null) {
                return null;
            }
            TLRPC.ThemeSettings settings = null;
            if (settingsIndex < info.settings.size()) {
                TLRPC.ThemeSettings settings2 = info.settings.get(settingsIndex);
                settings = settings2;
            }
            if (ignoreThemeInfoId) {
                ThemeAccent themeAccent = this.chatAccentsByThemeId.get(info.id);
                if (themeAccent != null) {
                    return themeAccent;
                }
                int id = this.lastChatThemeId + 1;
                this.lastChatThemeId = id;
                ThemeAccent themeAccent2 = createNewAccent(settings);
                themeAccent2.id = id;
                themeAccent2.info = info;
                themeAccent2.account = account;
                this.chatAccentsByThemeId.put(id, themeAccent2);
                return themeAccent2;
            }
            ThemeAccent themeAccent3 = this.accentsByThemeId.get(info.id);
            if (themeAccent3 != null) {
                return themeAccent3;
            }
            int id2 = this.lastAccentId + 1;
            this.lastAccentId = id2;
            ThemeAccent themeAccent4 = createNewAccent(settings);
            themeAccent4.id = id2;
            themeAccent4.info = info;
            themeAccent4.account = account;
            this.themeAccentsMap.put(id2, themeAccent4);
            this.themeAccents.add(0, themeAccent4);
            Theme.sortAccents(this);
            this.accentsByThemeId.put(info.id, themeAccent4);
            return themeAccent4;
        }

        public ThemeAccent getAccent(boolean createNew) {
            ThemeAccent accent;
            if (this.themeAccents == null || (accent = this.themeAccentsMap.get(this.currentAccentId)) == null) {
                return null;
            }
            if (createNew) {
                int id = this.lastAccentId + 1;
                this.lastAccentId = id;
                ThemeAccent themeAccent = new ThemeAccent();
                themeAccent.accentColor = accent.accentColor;
                themeAccent.accentColor2 = accent.accentColor2;
                themeAccent.myMessagesAccentColor = accent.myMessagesAccentColor;
                themeAccent.myMessagesGradientAccentColor1 = accent.myMessagesGradientAccentColor1;
                themeAccent.myMessagesGradientAccentColor2 = accent.myMessagesGradientAccentColor2;
                themeAccent.myMessagesGradientAccentColor3 = accent.myMessagesGradientAccentColor3;
                themeAccent.myMessagesAnimated = accent.myMessagesAnimated;
                themeAccent.backgroundOverrideColor = accent.backgroundOverrideColor;
                themeAccent.backgroundGradientOverrideColor1 = accent.backgroundGradientOverrideColor1;
                themeAccent.backgroundGradientOverrideColor2 = accent.backgroundGradientOverrideColor2;
                themeAccent.backgroundGradientOverrideColor3 = accent.backgroundGradientOverrideColor3;
                themeAccent.backgroundRotation = accent.backgroundRotation;
                themeAccent.patternSlug = accent.patternSlug;
                themeAccent.patternIntensity = accent.patternIntensity;
                themeAccent.patternMotion = accent.patternMotion;
                themeAccent.parentTheme = this;
                if (this.overrideWallpaper != null) {
                    themeAccent.overrideWallpaper = new OverrideWallpaperInfo(this.overrideWallpaper, this, themeAccent);
                }
                this.prevAccentId = this.currentAccentId;
                themeAccent.id = id;
                this.currentAccentId = id;
                this.overrideWallpaper = themeAccent.overrideWallpaper;
                this.themeAccentsMap.put(id, themeAccent);
                this.themeAccents.add(0, themeAccent);
                Theme.sortAccents(this);
                return themeAccent;
            }
            return accent;
        }

        public int getAccentColor(int id) {
            ThemeAccent accent = this.themeAccentsMap.get(id);
            if (accent != null) {
                return accent.accentColor;
            }
            return 0;
        }

        public boolean createBackground(File file, String toPath) {
            int patternColor;
            try {
                Bitmap bitmap = AndroidUtilities.getScaledBitmap(AndroidUtilities.dp(640.0f), AndroidUtilities.dp(360.0f), file.getAbsolutePath(), null, 0);
                if (bitmap != null && this.patternBgColor != 0) {
                    Bitmap finalBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
                    Canvas canvas = new Canvas(finalBitmap);
                    int i = this.patternBgGradientColor2;
                    if (i != 0) {
                        patternColor = MotionBackgroundDrawable.getPatternColor(this.patternBgColor, this.patternBgGradientColor1, i, this.patternBgGradientColor3);
                    } else {
                        int patternColor2 = this.patternBgGradientColor1;
                        if (patternColor2 != 0) {
                            patternColor = AndroidUtilities.getAverageColor(this.patternBgColor, patternColor2);
                            GradientDrawable gradientDrawable = new GradientDrawable(BackgroundGradientDrawable.getGradientOrientation(this.patternBgGradientRotation), new int[]{this.patternBgColor, this.patternBgGradientColor1});
                            gradientDrawable.setBounds(0, 0, finalBitmap.getWidth(), finalBitmap.getHeight());
                            gradientDrawable.draw(canvas);
                        } else {
                            int patternColor3 = this.patternBgColor;
                            patternColor = AndroidUtilities.getPatternColor(patternColor3);
                            canvas.drawColor(this.patternBgColor);
                        }
                    }
                    Paint paint = new Paint(2);
                    paint.setColorFilter(new PorterDuffColorFilter(patternColor, PorterDuff.Mode.SRC_IN));
                    paint.setAlpha((int) ((this.patternIntensity / 100.0f) * 255.0f));
                    canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
                    bitmap = finalBitmap;
                    canvas.setBitmap(null);
                }
                if (this.isBlured) {
                    bitmap = Utilities.blurWallpaper(bitmap);
                }
                FileOutputStream stream = new FileOutputStream(toPath);
                bitmap.compress(this.patternBgGradientColor2 != 0 ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, 87, stream);
                stream.close();
                return true;
            } catch (Throwable e) {
                FileLog.e(e);
                return false;
            }
        }

        @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
        public void didReceivedNotification(int id, int account, Object... args) {
            if (id == NotificationCenter.fileLoaded || id == NotificationCenter.fileLoadFailed) {
                String location = (String) args[0];
                TLRPC.TL_theme tL_theme = this.info;
                if (tL_theme != null && tL_theme.document != null) {
                    if (location.equals(this.loadingThemeWallpaperName)) {
                        this.loadingThemeWallpaperName = null;
                        final File file = (File) args[1];
                        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.ActionBar.Theme$ThemeInfo$$ExternalSyntheticLambda1
                            @Override // java.lang.Runnable
                            public final void run() {
                                Theme.ThemeInfo.this.m1434xa93038c(file);
                            }
                        });
                        return;
                    }
                    String name = FileLoader.getAttachFileName(this.info.document);
                    if (location.equals(name)) {
                        removeObservers();
                        if (id == NotificationCenter.fileLoaded) {
                            File locFile = new File(this.pathToFile);
                            final ThemeInfo themeInfo = Theme.fillThemeValues(locFile, this.info.title, this.info);
                            if (themeInfo != null && themeInfo.pathToWallpaper != null) {
                                File file2 = new File(themeInfo.pathToWallpaper);
                                if (!file2.exists()) {
                                    this.patternBgColor = themeInfo.patternBgColor;
                                    this.patternBgGradientColor1 = themeInfo.patternBgGradientColor1;
                                    this.patternBgGradientColor2 = themeInfo.patternBgGradientColor2;
                                    this.patternBgGradientColor3 = themeInfo.patternBgGradientColor3;
                                    this.patternBgGradientRotation = themeInfo.patternBgGradientRotation;
                                    this.isBlured = themeInfo.isBlured;
                                    this.patternIntensity = themeInfo.patternIntensity;
                                    this.newPathToWallpaper = themeInfo.pathToWallpaper;
                                    TLRPC.TL_account_getWallPaper req = new TLRPC.TL_account_getWallPaper();
                                    TLRPC.TL_inputWallPaperSlug inputWallPaperSlug = new TLRPC.TL_inputWallPaperSlug();
                                    inputWallPaperSlug.slug = themeInfo.slug;
                                    req.wallpaper = inputWallPaperSlug;
                                    ConnectionsManager.getInstance(themeInfo.account).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ActionBar.Theme$ThemeInfo$$ExternalSyntheticLambda3
                                        @Override // org.telegram.tgnet.RequestDelegate
                                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                            Theme.ThemeInfo.this.m1436xd515e10e(themeInfo, tLObject, tL_error);
                                        }
                                    });
                                    return;
                                }
                            }
                            onFinishLoadingRemoteTheme();
                        }
                    }
                }
            }
        }

        /* renamed from: lambda$didReceivedNotification$0$org-telegram-ui-ActionBar-Theme$ThemeInfo */
        public /* synthetic */ void m1434xa93038c(File file) {
            createBackground(file, this.newPathToWallpaper);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ActionBar.Theme$ThemeInfo$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Theme.ThemeInfo.this.onFinishLoadingRemoteTheme();
                }
            });
        }

        /* renamed from: lambda$didReceivedNotification$2$org-telegram-ui-ActionBar-Theme$ThemeInfo */
        public /* synthetic */ void m1436xd515e10e(final ThemeInfo themeInfo, final TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ActionBar.Theme$ThemeInfo$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    Theme.ThemeInfo.this.m1435xefd4724d(response, themeInfo);
                }
            });
        }

        /* renamed from: lambda$didReceivedNotification$1$org-telegram-ui-ActionBar-Theme$ThemeInfo */
        public /* synthetic */ void m1435xefd4724d(TLObject response, ThemeInfo themeInfo) {
            if (response instanceof TLRPC.TL_wallPaper) {
                TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) response;
                this.loadingThemeWallpaperName = FileLoader.getAttachFileName(wallPaper.document);
                addObservers();
                FileLoader.getInstance(themeInfo.account).loadFile(wallPaper.document, wallPaper, 1, 1);
                return;
            }
            onFinishLoadingRemoteTheme();
        }
    }

    /* loaded from: classes4.dex */
    public interface ResourcesProvider {
        void applyServiceShaderMatrix(int i, int i2, float f, float f2);

        Integer getColor(String str);

        int getColorOrDefault(String str);

        Integer getCurrentColor(String str);

        Drawable getDrawable(String str);

        Paint getPaint(String str);

        boolean hasGradientService();

        void setAnimatedColor(String str, int i);

        /* renamed from: org.telegram.ui.ActionBar.Theme$ResourcesProvider$-CC */
        /* loaded from: classes4.dex */
        public final /* synthetic */ class CC {
            public static void $default$setAnimatedColor(ResourcesProvider _this, String key, int color) {
            }

            public static Drawable $default$getDrawable(ResourcesProvider _this, String drawableKey) {
                return null;
            }

            public static Paint $default$getPaint(ResourcesProvider _this, String paintKey) {
                return null;
            }

            public static boolean $default$hasGradientService(ResourcesProvider _this) {
                return false;
            }
        }
    }

    static {
        Exception e;
        String theme;
        boolean z;
        ThemeInfo themeDarkBlue;
        int remoteVersion;
        String themesString;
        ThemeInfo themeInfo;
        int i;
        Throwable e2;
        Throwable e3;
        ThemeInfo themeDarkBlue2;
        String theme2;
        boolean z2;
        boolean z3;
        int version;
        SharedPreferences.Editor oldEditor;
        ThemeInfo t;
        selectedAutoNightType = 0;
        autoNightBrighnessThreshold = 0.25f;
        autoNightDayStartTime = 1320;
        autoNightDayEndTime = 480;
        autoNightSunsetTime = 1320;
        autoNightLastSunCheckDay = -1;
        autoNightSunriseTime = 480;
        autoNightCityName = "";
        autoNightLocationLatitude = 10000.0d;
        autoNightLocationLongitude = 10000.0d;
        defaultColors.put(key_dialogBackground, -1);
        defaultColors.put(key_dialogBackgroundGray, -986896);
        defaultColors.put(key_dialogTextBlack, -14540254);
        defaultColors.put(key_dialogTextLink, -14255946);
        defaultColors.put(key_dialogLinkSelection, 862104035);
        defaultColors.put(key_dialogTextRed, -3319206);
        defaultColors.put(key_dialogTextRed2, -2213318);
        defaultColors.put(key_dialogTextBlue, -13660983);
        defaultColors.put(key_dialogTextBlue2, -12937771);
        defaultColors.put(key_dialogTextBlue3, -12664327);
        defaultColors.put(key_dialogTextBlue4, -15095832);
        defaultColors.put(key_dialogTextGray, -13333567);
        defaultColors.put(key_dialogTextGray2, -9079435);
        defaultColors.put(key_dialogTextGray3, -6710887);
        defaultColors.put(key_dialogTextGray4, -5000269);
        defaultColors.put(key_dialogTextHint, -6842473);
        defaultColors.put(key_dialogIcon, -9999504);
        defaultColors.put(key_dialogRedIcon, -2011827);
        defaultColors.put(key_dialogGrayLine, -2960686);
        defaultColors.put(key_dialogTopBackground, -9456923);
        defaultColors.put(key_dialogInputField, -2368549);
        defaultColors.put(key_dialogInputFieldActivated, -13129232);
        defaultColors.put(key_dialogCheckboxSquareBackground, -12345121);
        defaultColors.put(key_dialogCheckboxSquareCheck, -1);
        defaultColors.put(key_dialogCheckboxSquareUnchecked, -9211021);
        defaultColors.put(key_dialogCheckboxSquareDisabled, -5197648);
        defaultColors.put(key_dialogRadioBackground, -5000269);
        defaultColors.put(key_dialogRadioBackgroundChecked, -13129232);
        defaultColors.put(key_dialogProgressCircle, -14115349);
        defaultColors.put(key_dialogLineProgress, -11371101);
        defaultColors.put(key_dialogLineProgressBackground, -2368549);
        defaultColors.put(key_dialogButton, -11955764);
        defaultColors.put(key_dialogButtonSelector, Integer.valueOf((int) AndroidUtilities.LIGHT_STATUS_BAR_OVERLAY));
        defaultColors.put(key_dialogScrollGlow, -657673);
        defaultColors.put(key_dialogRoundCheckBox, -11750155);
        defaultColors.put(key_dialogRoundCheckBoxCheck, -1);
        defaultColors.put(key_dialogBadgeBackground, -12664327);
        defaultColors.put(key_dialogBadgeText, -1);
        defaultColors.put(key_dialogCameraIcon, -1);
        defaultColors.put(key_dialog_inlineProgressBackground, -151981323);
        defaultColors.put(key_dialog_inlineProgress, -9735304);
        defaultColors.put(key_dialogSearchBackground, -854795);
        defaultColors.put(key_dialogSearchHint, -6774617);
        defaultColors.put(key_dialogSearchIcon, -6182737);
        defaultColors.put(key_dialogSearchText, -14540254);
        defaultColors.put(key_dialogFloatingButton, -11750155);
        defaultColors.put(key_dialogFloatingButtonPressed, Integer.valueOf((int) AndroidUtilities.LIGHT_STATUS_BAR_OVERLAY));
        defaultColors.put(key_dialogFloatingIcon, -1);
        defaultColors.put(key_dialogShadowLine, 301989888);
        defaultColors.put(key_dialogEmptyImage, -6314840);
        defaultColors.put(key_dialogEmptyText, -7565164);
        defaultColors.put(key_dialogSwipeRemove, -1743531);
        defaultColors.put(key_dialogSwipeRemove, -1743531);
        defaultColors.put(key_dialogReactionMentionBackground, -1026983);
        defaultColors.put(key_windowBackgroundWhite, -1);
        defaultColors.put(key_windowBackgroundUnchecked, -6445135);
        defaultColors.put(key_windowBackgroundChecked, -11034919);
        defaultColors.put(key_windowBackgroundCheckText, -1);
        defaultColors.put(key_progressCircle, -14904349);
        defaultColors.put(key_windowBackgroundWhiteGrayIcon, -8288629);
        defaultColors.put(key_windowBackgroundWhiteBlueText, -12545331);
        defaultColors.put(key_windowBackgroundWhiteBlueText2, -12937771);
        defaultColors.put(key_windowBackgroundWhiteBlueText3, -14255946);
        defaultColors.put(key_windowBackgroundWhiteBlueText4, -14904349);
        defaultColors.put(key_windowBackgroundWhiteBlueText5, -11759926);
        defaultColors.put(key_windowBackgroundWhiteBlueText6, -12940081);
        defaultColors.put(key_windowBackgroundWhiteBlueText7, -13141330);
        defaultColors.put(key_windowBackgroundWhiteBlueButton, -14776109);
        defaultColors.put(key_windowBackgroundWhiteBlueIcon, -13132315);
        defaultColors.put(key_windowBackgroundWhiteGreenText, -14248148);
        defaultColors.put(key_windowBackgroundWhiteGreenText2, -13129704);
        defaultColors.put(key_windowBackgroundWhiteRedText, -3319206);
        defaultColors.put(key_windowBackgroundWhiteRedText2, -2404015);
        defaultColors.put(key_windowBackgroundWhiteRedText3, -2995895);
        defaultColors.put(key_windowBackgroundWhiteRedText4, -3198928);
        defaultColors.put(key_windowBackgroundWhiteRedText5, -1230535);
        defaultColors.put(key_windowBackgroundWhiteRedText6, -39322);
        defaultColors.put(key_windowBackgroundWhiteGrayText, -8156010);
        defaultColors.put(key_windowBackgroundWhiteGrayText2, -8223094);
        defaultColors.put(key_windowBackgroundWhiteGrayText3, -6710887);
        defaultColors.put(key_windowBackgroundWhiteGrayText4, -8355712);
        defaultColors.put(key_windowBackgroundWhiteGrayText5, -6052957);
        defaultColors.put(key_windowBackgroundWhiteGrayText6, -9079435);
        defaultColors.put(key_windowBackgroundWhiteGrayText7, -3750202);
        defaultColors.put(key_windowBackgroundWhiteGrayText8, -9605774);
        defaultColors.put(key_windowBackgroundWhiteGrayLine, -2368549);
        defaultColors.put(key_windowBackgroundWhiteBlackText, -14540254);
        defaultColors.put(key_windowBackgroundWhiteHintText, -5723992);
        defaultColors.put(key_windowBackgroundWhiteValueText, -12937771);
        defaultColors.put(key_windowBackgroundWhiteLinkText, -14255946);
        defaultColors.put(key_windowBackgroundWhiteLinkSelection, 862104035);
        defaultColors.put(key_windowBackgroundWhiteBlueHeader, -12937771);
        defaultColors.put(key_windowBackgroundWhiteInputField, -2368549);
        defaultColors.put(key_windowBackgroundWhiteInputFieldActivated, -13129232);
        defaultColors.put(key_switchTrack, -5196358);
        defaultColors.put(key_switchTrackChecked, -11358743);
        defaultColors.put(key_switchTrackBlue, -8221031);
        defaultColors.put(key_switchTrackBlueChecked, -12810041);
        defaultColors.put(key_switchTrackBlueThumb, -1);
        defaultColors.put(key_switchTrackBlueThumbChecked, -1);
        defaultColors.put(key_switchTrackBlueSelector, 390089299);
        defaultColors.put(key_switchTrackBlueSelectorChecked, 553797505);
        defaultColors.put(key_switch2Track, -688514);
        defaultColors.put(key_switch2TrackChecked, -11358743);
        defaultColors.put(key_checkboxSquareBackground, -12345121);
        defaultColors.put(key_checkboxSquareCheck, -1);
        defaultColors.put(key_checkboxSquareUnchecked, -9211021);
        defaultColors.put(key_checkboxSquareDisabled, -5197648);
        defaultColors.put(key_listSelector, Integer.valueOf((int) AndroidUtilities.LIGHT_STATUS_BAR_OVERLAY));
        defaultColors.put(key_radioBackground, -5000269);
        defaultColors.put(key_radioBackgroundChecked, -13129232);
        defaultColors.put(key_windowBackgroundGray, -986896);
        defaultColors.put(key_windowBackgroundGrayShadow, -16777216);
        defaultColors.put(key_emptyListPlaceholder, -6974059);
        defaultColors.put(key_divider, -2500135);
        defaultColors.put(key_graySection, -657931);
        defaultColors.put(key_graySectionText, -8222838);
        defaultColors.put(key_contextProgressInner1, -4202506);
        defaultColors.put(key_contextProgressOuter1, -13920542);
        defaultColors.put(key_contextProgressInner2, -4202506);
        defaultColors.put(key_contextProgressOuter2, -1);
        defaultColors.put(key_contextProgressInner3, -5000269);
        defaultColors.put(key_contextProgressOuter3, -1);
        defaultColors.put(key_contextProgressInner4, -3486256);
        defaultColors.put(key_contextProgressOuter4, -13683656);
        defaultColors.put(key_fastScrollActive, -11361317);
        defaultColors.put(key_fastScrollInactive, -3551791);
        defaultColors.put(key_fastScrollText, -1);
        defaultColors.put(key_avatar_text, -1);
        defaultColors.put(key_avatar_backgroundSaved, -10043398);
        defaultColors.put(key_avatar_backgroundArchived, -5654847);
        defaultColors.put(key_avatar_backgroundArchivedHidden, -10043398);
        defaultColors.put(key_avatar_backgroundRed, -1743531);
        defaultColors.put(key_avatar_backgroundOrange, -881592);
        defaultColors.put(key_avatar_backgroundViolet, -7436818);
        defaultColors.put(key_avatar_backgroundGreen, -8992691);
        defaultColors.put(key_avatar_backgroundCyan, -10502443);
        defaultColors.put(key_avatar_backgroundBlue, -11232035);
        defaultColors.put(key_avatar_backgroundPink, -887654);
        defaultColors.put(key_avatar_backgroundInProfileBlue, -11500111);
        defaultColors.put(key_avatar_backgroundActionBarBlue, -10907718);
        defaultColors.put(key_avatar_subtitleInProfileBlue, -2626822);
        defaultColors.put(key_avatar_actionBarSelectorBlue, -11959891);
        defaultColors.put(key_avatar_actionBarIconBlue, -1);
        defaultColors.put(key_avatar_nameInMessageRed, -3516848);
        defaultColors.put(key_avatar_nameInMessageOrange, -2589911);
        defaultColors.put(key_avatar_nameInMessageViolet, -11627828);
        defaultColors.put(key_avatar_nameInMessageGreen, -11488718);
        defaultColors.put(key_avatar_nameInMessageCyan, -13132104);
        defaultColors.put(key_avatar_nameInMessageBlue, -11627828);
        defaultColors.put(key_avatar_nameInMessagePink, -11627828);
        defaultColors.put(key_actionBarDefault, -11371101);
        defaultColors.put(key_actionBarDefaultIcon, -1);
        defaultColors.put(key_actionBarActionModeDefault, -1);
        defaultColors.put(key_actionBarActionModeDefaultTop, 268435456);
        defaultColors.put(key_actionBarActionModeDefaultIcon, -9999761);
        defaultColors.put(key_actionBarDefaultTitle, -1);
        defaultColors.put(key_actionBarDefaultSubtitle, -2758409);
        defaultColors.put(key_actionBarDefaultSelector, -12554860);
        defaultColors.put(key_actionBarWhiteSelector, 486539264);
        defaultColors.put(key_actionBarDefaultSearch, -1);
        defaultColors.put(key_actionBarDefaultSearchPlaceholder, -1996488705);
        defaultColors.put(key_actionBarDefaultSubmenuItem, -14540254);
        defaultColors.put(key_actionBarDefaultSubmenuItemIcon, -9999504);
        defaultColors.put(key_actionBarDefaultSubmenuBackground, -1);
        defaultColors.put(key_actionBarDefaultSubmenuSeparator, -657931);
        defaultColors.put(key_actionBarActionModeDefaultSelector, -1907998);
        defaultColors.put(key_actionBarTabActiveText, -1);
        defaultColors.put(key_actionBarTabUnactiveText, -2758409);
        defaultColors.put(key_actionBarTabLine, -1);
        defaultColors.put(key_actionBarTabSelector, -12554860);
        defaultColors.put(key_actionBarBrowser, -1);
        defaultColors.put(key_actionBarDefaultArchived, -9471353);
        defaultColors.put(key_actionBarDefaultArchivedSelector, -10590350);
        defaultColors.put(key_actionBarDefaultArchivedIcon, -1);
        defaultColors.put(key_actionBarDefaultArchivedTitle, -1);
        defaultColors.put(key_actionBarDefaultArchivedSearch, -1);
        defaultColors.put(key_actionBarDefaultArchivedSearchPlaceholder, -1996488705);
        defaultColors.put(key_chats_onlineCircle, -11810020);
        defaultColors.put(key_chats_unreadCounter, -11613090);
        defaultColors.put(key_chats_unreadCounterMuted, -3749428);
        defaultColors.put(key_chats_unreadCounterText, -1);
        defaultColors.put(key_chats_archiveBackground, -10049056);
        defaultColors.put(key_chats_archivePinBackground, -6313293);
        defaultColors.put(key_chats_archiveIcon, -1);
        defaultColors.put(key_chats_archiveText, -1);
        defaultColors.put(key_chats_name, -14540254);
        defaultColors.put(key_chats_nameArchived, -11382190);
        defaultColors.put(key_chats_secretName, -16734706);
        defaultColors.put(key_chats_secretIcon, -15093466);
        defaultColors.put(key_chats_nameIcon, -14408668);
        defaultColors.put(key_chats_pinnedIcon, -5723992);
        defaultColors.put(key_chats_message, -7631473);
        defaultColors.put(key_chats_messageArchived, -7237231);
        defaultColors.put(key_chats_message_threeLines, -7434095);
        defaultColors.put(key_chats_draft, -2274503);
        defaultColors.put(key_chats_nameMessage, -12812624);
        defaultColors.put(key_chats_nameMessageArchived, -7631473);
        defaultColors.put(key_chats_nameMessage_threeLines, -12434359);
        defaultColors.put(key_chats_nameMessageArchived_threeLines, -10592674);
        defaultColors.put(key_chats_attachMessage, -12812624);
        defaultColors.put(key_chats_actionMessage, -12812624);
        defaultColors.put(key_chats_date, -6973028);
        defaultColors.put(key_chats_pinnedOverlay, 134217728);
        defaultColors.put(key_chats_tabletSelectedOverlay, Integer.valueOf((int) AndroidUtilities.LIGHT_STATUS_BAR_OVERLAY));
        defaultColors.put(key_chats_sentCheck, -12146122);
        defaultColors.put(key_chats_sentReadCheck, -12146122);
        defaultColors.put(key_chats_sentClock, -9061026);
        defaultColors.put(key_chats_sentError, -2796974);
        defaultColors.put(key_chats_sentErrorIcon, -1);
        defaultColors.put(key_chats_verifiedBackground, -13391642);
        defaultColors.put(key_chats_verifiedCheck, -1);
        defaultColors.put(key_chats_muteIcon, -4341308);
        defaultColors.put(key_chats_mentionIcon, -1);
        defaultColors.put(key_chats_menuBackground, -1);
        defaultColors.put(key_chats_menuItemText, -12303292);
        defaultColors.put(key_chats_menuItemCheck, -10907718);
        defaultColors.put(key_chats_menuItemIcon, -7827048);
        defaultColors.put(key_chats_menuName, -1);
        defaultColors.put(key_chats_menuPhone, -1);
        defaultColors.put(key_chats_menuPhoneCats, -4004353);
        defaultColors.put(key_chats_menuCloud, -1);
        defaultColors.put(key_chats_menuCloudBackgroundCats, -12420183);
        defaultColors.put(key_chats_actionIcon, -1);
        defaultColors.put(key_chats_actionBackground, -10114592);
        defaultColors.put(key_chats_actionPressedBackground, -11100714);
        defaultColors.put(key_chats_actionUnreadIcon, -9211021);
        defaultColors.put(key_chats_actionUnreadBackground, -1);
        defaultColors.put(key_chats_actionUnreadPressedBackground, -855310);
        defaultColors.put(key_chats_menuTopBackgroundCats, -10907718);
        defaultColors.put(key_chats_archivePullDownBackground, -3749428);
        defaultColors.put(key_chats_archivePullDownBackgroundActive, -10049056);
        defaultColors.put(key_chat_attachMediaBanBackground, -12171706);
        defaultColors.put(key_chat_attachMediaBanText, -1);
        defaultColors.put(key_chat_attachCheckBoxCheck, -1);
        defaultColors.put(key_chat_attachCheckBoxBackground, -12995849);
        defaultColors.put(key_chat_attachPhotoBackground, 201326592);
        defaultColors.put(key_chat_attachActiveTab, -13391883);
        defaultColors.put(key_chat_attachUnactiveTab, -7169634);
        defaultColors.put(key_chat_attachPermissionImage, Integer.valueOf((int) ACTION_BAR_MEDIA_PICKER_COLOR));
        defaultColors.put(key_chat_attachPermissionMark, -1945520);
        defaultColors.put(key_chat_attachPermissionText, -9472134);
        defaultColors.put(key_chat_attachEmptyImage, -3355444);
        defaultColors.put(key_chat_attachGalleryBackground, -12214795);
        defaultColors.put(key_chat_attachGalleryText, -13726231);
        defaultColors.put(key_chat_attachGalleryIcon, -1);
        defaultColors.put(key_chat_attachAudioBackground, -1351584);
        defaultColors.put(key_chat_attachAudioText, -2209977);
        defaultColors.put(key_chat_attachAudioIcon, -1);
        defaultColors.put(key_chat_attachFileBackground, -13321743);
        defaultColors.put(key_chat_attachFileText, -15423260);
        defaultColors.put(key_chat_attachFileIcon, -1);
        defaultColors.put(key_chat_attachContactBackground, -868277);
        defaultColors.put(key_chat_attachContactText, -2121728);
        defaultColors.put(key_chat_attachContactIcon, -1);
        defaultColors.put(key_chat_attachLocationBackground, -10436011);
        defaultColors.put(key_chat_attachLocationText, -12801233);
        defaultColors.put(key_chat_attachLocationIcon, -1);
        defaultColors.put(key_chat_attachPollBackground, -868277);
        defaultColors.put(key_chat_attachPollText, -2121728);
        defaultColors.put(key_chat_attachPollIcon, -1);
        defaultColors.put(key_chat_inPollCorrectAnswer, -10436011);
        defaultColors.put(key_chat_outPollCorrectAnswer, -10436011);
        defaultColors.put(key_chat_inPollWrongAnswer, -1351584);
        defaultColors.put(key_chat_outPollWrongAnswer, -1351584);
        defaultColors.put(key_chat_status, -2758409);
        defaultColors.put(key_chat_inGreenCall, -16725933);
        defaultColors.put(key_chat_inRedCall, -47032);
        defaultColors.put(key_chat_outGreenCall, -16725933);
        defaultColors.put(key_chat_lockIcon, -1);
        defaultColors.put(key_chat_muteIcon, -5124893);
        defaultColors.put(key_chat_inBubble, -1);
        defaultColors.put(key_chat_inBubbleSelected, -1247235);
        defaultColors.put(key_chat_inBubbleShadow, -14862509);
        defaultColors.put(key_chat_outBubble, -1048610);
        defaultColors.put(key_chat_outBubbleGradientSelectedOverlay, 335544320);
        defaultColors.put(key_chat_outBubbleSelected, -2492475);
        defaultColors.put(key_chat_outBubbleShadow, -14781172);
        defaultColors.put(key_chat_inMediaIcon, -1);
        defaultColors.put(key_chat_inMediaIconSelected, -1050370);
        defaultColors.put(key_chat_outMediaIcon, -1048610);
        defaultColors.put(key_chat_outMediaIconSelected, -1967921);
        defaultColors.put(key_chat_messageTextIn, -16777216);
        defaultColors.put(key_chat_messageTextOut, -16777216);
        defaultColors.put(key_chat_messageLinkIn, -14255946);
        defaultColors.put(key_chat_messageLinkOut, -14255946);
        defaultColors.put(key_chat_serviceText, -1);
        defaultColors.put(key_chat_serviceLink, -1);
        defaultColors.put(key_chat_serviceIcon, -1);
        defaultColors.put(key_chat_mediaTimeBackground, 1711276032);
        defaultColors.put(key_chat_outSentCheck, -10637232);
        defaultColors.put(key_chat_outSentCheckSelected, -10637232);
        defaultColors.put(key_chat_outSentCheckRead, -10637232);
        defaultColors.put(key_chat_outSentCheckReadSelected, -10637232);
        defaultColors.put(key_chat_outSentClock, -9061026);
        defaultColors.put(key_chat_outSentClockSelected, -9061026);
        defaultColors.put(key_chat_inSentClock, -6182221);
        defaultColors.put(key_chat_inSentClockSelected, -7094838);
        defaultColors.put(key_chat_mediaSentCheck, -1);
        defaultColors.put(key_chat_mediaSentClock, -1);
        defaultColors.put(key_chat_inViews, -6182221);
        defaultColors.put(key_chat_inViewsSelected, -7094838);
        defaultColors.put(key_chat_outViews, -9522601);
        defaultColors.put(key_chat_outViewsSelected, -9522601);
        defaultColors.put(key_chat_mediaViews, -1);
        defaultColors.put(key_chat_inMenu, -4801083);
        defaultColors.put(key_chat_inMenuSelected, -6766130);
        defaultColors.put(key_chat_outMenu, -7221634);
        defaultColors.put(key_chat_outMenuSelected, -7221634);
        defaultColors.put(key_chat_mediaMenu, -1);
        defaultColors.put(key_chat_outInstant, -11162801);
        defaultColors.put(key_chat_outInstantSelected, -12019389);
        defaultColors.put(key_chat_inInstant, -12940081);
        defaultColors.put(key_chat_inInstantSelected, -13600331);
        defaultColors.put(key_chat_sentError, -2411211);
        defaultColors.put(key_chat_sentErrorIcon, -1);
        defaultColors.put(key_chat_selectedBackground, 671781104);
        defaultColors.put(key_chat_previewDurationText, -1);
        defaultColors.put(key_chat_previewGameText, -1);
        defaultColors.put(key_chat_inPreviewInstantText, -12940081);
        defaultColors.put(key_chat_outPreviewInstantText, -11162801);
        defaultColors.put(key_chat_inPreviewInstantSelectedText, -13600331);
        defaultColors.put(key_chat_outPreviewInstantSelectedText, -12019389);
        defaultColors.put(key_chat_secretTimeText, -1776928);
        defaultColors.put(key_chat_stickerNameText, -1);
        defaultColors.put(key_chat_botButtonText, -1);
        defaultColors.put(key_chat_botProgress, -1);
        defaultColors.put(key_chat_inForwardedNameText, -13072697);
        defaultColors.put(key_chat_outForwardedNameText, -11162801);
        defaultColors.put(key_chat_inPsaNameText, -10838983);
        defaultColors.put(key_chat_outPsaNameText, -10838983);
        defaultColors.put(key_chat_inViaBotNameText, -12940081);
        defaultColors.put(key_chat_outViaBotNameText, -11162801);
        defaultColors.put(key_chat_stickerViaBotNameText, -1);
        defaultColors.put(key_chat_inReplyLine, -10903592);
        defaultColors.put(key_chat_outReplyLine, -9520791);
        defaultColors.put(key_chat_stickerReplyLine, -1);
        defaultColors.put(key_chat_inReplyNameText, -12940081);
        defaultColors.put(key_chat_outReplyNameText, -11162801);
        defaultColors.put(key_chat_stickerReplyNameText, -1);
        defaultColors.put(key_chat_inReplyMessageText, -16777216);
        defaultColors.put(key_chat_outReplyMessageText, -16777216);
        defaultColors.put(key_chat_inReplyMediaMessageText, -6182221);
        defaultColors.put(key_chat_outReplyMediaMessageText, -10112933);
        defaultColors.put(key_chat_inReplyMediaMessageSelectedText, -7752511);
        defaultColors.put(key_chat_outReplyMediaMessageSelectedText, -10112933);
        defaultColors.put(key_chat_stickerReplyMessageText, -1);
        defaultColors.put(key_chat_inPreviewLine, -9390872);
        defaultColors.put(key_chat_outPreviewLine, -7812741);
        defaultColors.put(key_chat_inSiteNameText, -12940081);
        defaultColors.put(key_chat_outSiteNameText, -11162801);
        defaultColors.put(key_chat_inContactNameText, -11625772);
        defaultColors.put(key_chat_outContactNameText, -11162801);
        defaultColors.put(key_chat_inContactPhoneText, -13683656);
        defaultColors.put(key_chat_inContactPhoneSelectedText, -13683656);
        defaultColors.put(key_chat_outContactPhoneText, -13286860);
        defaultColors.put(key_chat_outContactPhoneSelectedText, -13286860);
        defaultColors.put(key_chat_mediaProgress, -1);
        defaultColors.put(key_chat_inAudioProgress, -1);
        defaultColors.put(key_chat_outAudioProgress, -1048610);
        defaultColors.put(key_chat_inAudioSelectedProgress, -1050370);
        defaultColors.put(key_chat_outAudioSelectedProgress, -1967921);
        defaultColors.put(key_chat_mediaTimeText, -1);
        defaultColors.put(key_chat_inAdminText, -4143413);
        defaultColors.put(key_chat_inAdminSelectedText, -7752511);
        defaultColors.put(key_chat_outAdminText, -9391780);
        defaultColors.put(key_chat_outAdminSelectedText, -9391780);
        defaultColors.put(key_chat_inTimeText, -6182221);
        defaultColors.put(key_chat_inTimeSelectedText, -7752511);
        defaultColors.put(key_chat_outTimeText, -9391780);
        defaultColors.put(key_chat_outTimeSelectedText, -9391780);
        defaultColors.put(key_chat_inAudioPerformerText, -13683656);
        defaultColors.put(key_chat_inAudioPerformerSelectedText, -13683656);
        defaultColors.put(key_chat_outAudioPerformerText, -13286860);
        defaultColors.put(key_chat_outAudioPerformerSelectedText, -13286860);
        defaultColors.put(key_chat_inAudioTitleText, -11625772);
        defaultColors.put(key_chat_outAudioTitleText, -11162801);
        defaultColors.put(key_chat_inAudioDurationText, -6182221);
        defaultColors.put(key_chat_outAudioDurationText, -10112933);
        defaultColors.put(key_chat_inAudioDurationSelectedText, -7752511);
        defaultColors.put(key_chat_outAudioDurationSelectedText, -10112933);
        defaultColors.put(key_chat_inAudioSeekbar, -1774864);
        defaultColors.put(key_chat_inAudioCacheSeekbar, 1071966960);
        defaultColors.put(key_chat_outAudioSeekbar, -4463700);
        defaultColors.put(key_chat_outAudioCacheSeekbar, 1069278124);
        defaultColors.put(key_chat_inAudioSeekbarSelected, -4399384);
        defaultColors.put(key_chat_outAudioSeekbarSelected, -5644906);
        defaultColors.put(key_chat_inAudioSeekbarFill, -9259544);
        defaultColors.put(key_chat_outAudioSeekbarFill, -8863118);
        defaultColors.put(key_chat_inVoiceSeekbar, -2169365);
        defaultColors.put(key_chat_outVoiceSeekbar, -4463700);
        defaultColors.put(key_chat_inVoiceSeekbarSelected, -4399384);
        defaultColors.put(key_chat_outVoiceSeekbarSelected, -5644906);
        defaultColors.put(key_chat_inVoiceSeekbarFill, -9259544);
        defaultColors.put(key_chat_outVoiceSeekbarFill, -8863118);
        defaultColors.put(key_chat_inFileProgress, -1314571);
        defaultColors.put(key_chat_outFileProgress, -2427453);
        defaultColors.put(key_chat_inFileProgressSelected, -3413258);
        defaultColors.put(key_chat_outFileProgressSelected, -3806041);
        defaultColors.put(key_chat_inFileNameText, -11625772);
        defaultColors.put(key_chat_outFileNameText, -11162801);
        defaultColors.put(key_chat_inFileInfoText, -6182221);
        defaultColors.put(key_chat_outFileInfoText, -10112933);
        defaultColors.put(key_chat_inFileInfoSelectedText, -7752511);
        defaultColors.put(key_chat_outFileInfoSelectedText, -10112933);
        defaultColors.put(key_chat_inFileBackground, -1314571);
        defaultColors.put(key_chat_outFileBackground, -2427453);
        defaultColors.put(key_chat_inFileBackgroundSelected, -3413258);
        defaultColors.put(key_chat_outFileBackgroundSelected, -3806041);
        defaultColors.put(key_chat_inVenueInfoText, -6182221);
        defaultColors.put(key_chat_outVenueInfoText, -10112933);
        defaultColors.put(key_chat_inVenueInfoSelectedText, -7752511);
        defaultColors.put(key_chat_outVenueInfoSelectedText, -10112933);
        defaultColors.put(key_chat_mediaInfoText, -1);
        defaultColors.put(key_chat_linkSelectBackground, 862104035);
        defaultColors.put(key_chat_outLinkSelectBackground, 862104035);
        defaultColors.put(key_chat_textSelectBackground, 1717742051);
        defaultColors.put(key_chat_emojiPanelBackground, -986379);
        defaultColors.put(key_chat_emojiPanelBadgeBackground, -11688214);
        defaultColors.put(key_chat_emojiPanelBadgeText, -1);
        defaultColors.put(key_chat_emojiSearchBackground, -1709586);
        defaultColors.put(key_chat_emojiSearchIcon, -7036497);
        defaultColors.put(key_chat_emojiPanelShadowLine, 301989888);
        defaultColors.put(key_chat_emojiPanelEmptyText, -7038047);
        defaultColors.put(key_chat_emojiPanelIcon, -6445909);
        defaultColors.put(key_chat_emojiBottomPanelIcon, -7564905);
        defaultColors.put(key_chat_emojiPanelIconSelected, -13920286);
        defaultColors.put(key_chat_emojiPanelStickerPackSelector, -1907225);
        defaultColors.put(key_chat_emojiPanelStickerPackSelectorLine, -11097104);
        defaultColors.put(key_chat_emojiPanelBackspace, -7564905);
        defaultColors.put(key_chat_emojiPanelMasksIcon, -1);
        defaultColors.put(key_chat_emojiPanelMasksIconSelected, -10305560);
        defaultColors.put(key_chat_emojiPanelTrendingTitle, -14540254);
        defaultColors.put(key_chat_emojiPanelStickerSetName, -8221804);
        defaultColors.put(key_chat_emojiPanelStickerSetNameHighlight, -14184997);
        defaultColors.put(key_chat_emojiPanelStickerSetNameIcon, -5130564);
        defaultColors.put(key_chat_emojiPanelTrendingDescription, -7697782);
        defaultColors.put(key_chat_botKeyboardButtonText, -13220017);
        defaultColors.put(key_chat_botKeyboardButtonBackground, -1775639);
        defaultColors.put(key_chat_botKeyboardButtonBackgroundPressed, -3354156);
        defaultColors.put(key_chat_unreadMessagesStartArrowIcon, -6113849);
        defaultColors.put(key_chat_unreadMessagesStartText, -11102772);
        defaultColors.put(key_chat_unreadMessagesStartBackground, -1);
        defaultColors.put(key_chat_inFileIcon, -6113849);
        defaultColors.put(key_chat_inFileSelectedIcon, -7883067);
        defaultColors.put(key_chat_outFileIcon, -8011912);
        defaultColors.put(key_chat_outFileSelectedIcon, -8011912);
        defaultColors.put(key_chat_inLocationBackground, -1314571);
        defaultColors.put(key_chat_inLocationIcon, -6113849);
        defaultColors.put(key_chat_outLocationBackground, -2427453);
        defaultColors.put(key_chat_outLocationIcon, -7880840);
        defaultColors.put(key_chat_inContactBackground, -9259544);
        defaultColors.put(key_chat_inContactIcon, -1);
        defaultColors.put(key_chat_outContactBackground, -8863118);
        defaultColors.put(key_chat_outContactIcon, -1048610);
        defaultColors.put(key_chat_outBroadcast, -12146122);
        defaultColors.put(key_chat_mediaBroadcast, -1);
        defaultColors.put(key_chat_searchPanelIcons, -9999761);
        defaultColors.put(key_chat_searchPanelText, -9999761);
        defaultColors.put(key_chat_secretChatStatusText, -8421505);
        defaultColors.put(key_chat_fieldOverlayText, -12940081);
        defaultColors.put(key_chat_stickersHintPanel, -1);
        defaultColors.put(key_chat_replyPanelIcons, -11032346);
        defaultColors.put(key_chat_replyPanelClose, -7432805);
        defaultColors.put(key_chat_replyPanelName, -12940081);
        defaultColors.put(key_chat_replyPanelMessage, -14540254);
        defaultColors.put(key_chat_replyPanelLine, -1513240);
        defaultColors.put(key_chat_messagePanelBackground, -1);
        defaultColors.put(key_chat_messagePanelText, -16777216);
        defaultColors.put(key_chat_messagePanelHint, -5985101);
        defaultColors.put(key_chat_messagePanelCursor, -11230757);
        defaultColors.put(key_chat_messagePanelShadow, -16777216);
        defaultColors.put(key_chat_messagePanelIcons, -7432805);
        defaultColors.put(key_chat_recordedVoicePlayPause, -1);
        defaultColors.put(key_chat_recordedVoiceDot, -2468275);
        defaultColors.put(key_chat_recordedVoiceBackground, -10637848);
        defaultColors.put(key_chat_recordedVoiceProgress, -5120257);
        defaultColors.put(key_chat_recordedVoiceProgressInner, -1);
        defaultColors.put(key_chat_recordVoiceCancel, -12937772);
        defaultColors.put(key_chat_recordedVoiceHighlight, 1694498815);
        defaultColors.put(key_chat_messagePanelSend, -10309397);
        defaultColors.put(key_chat_messagePanelVoiceLock, -5987164);
        defaultColors.put(key_chat_messagePanelVoiceLockBackground, -1);
        defaultColors.put(key_chat_messagePanelVoiceLockShadow, -16777216);
        defaultColors.put(key_chat_recordTime, -7432805);
        defaultColors.put(key_chat_emojiPanelNewTrending, -11688214);
        defaultColors.put(key_chat_gifSaveHintText, -1);
        defaultColors.put(key_chat_gifSaveHintBackground, -871296751);
        defaultColors.put(key_chat_goDownButton, -1);
        defaultColors.put(key_chat_goDownButtonShadow, -16777216);
        defaultColors.put(key_chat_goDownButtonIcon, -7432805);
        defaultColors.put(key_chat_goDownButtonCounter, -1);
        defaultColors.put(key_chat_goDownButtonCounterBackground, -11689240);
        defaultColors.put(key_chat_messagePanelCancelInlineBot, -5395027);
        defaultColors.put(key_chat_messagePanelVoicePressed, -1);
        defaultColors.put(key_chat_messagePanelVoiceBackground, -10639650);
        defaultColors.put(key_chat_messagePanelVoiceDelete, -9211021);
        defaultColors.put(key_chat_messagePanelVoiceDuration, -1);
        defaultColors.put(key_chat_inlineResultIcon, -11037236);
        defaultColors.put(key_chat_topPanelBackground, -1);
        defaultColors.put(key_chat_topPanelClose, -7629157);
        defaultColors.put(key_chat_topPanelLine, -9658414);
        defaultColors.put(key_chat_topPanelTitle, -12940081);
        defaultColors.put(key_chat_topPanelMessage, -7893359);
        defaultColors.put(key_chat_reportSpam, -3188393);
        defaultColors.put(key_chat_addContact, -11894091);
        defaultColors.put(key_chat_inLoader, -9259544);
        defaultColors.put(key_chat_inLoaderSelected, -10114080);
        defaultColors.put(key_chat_outLoader, -8863118);
        defaultColors.put(key_chat_outLoaderSelected, -9783964);
        defaultColors.put(key_chat_inLoaderPhoto, -6113080);
        defaultColors.put(key_chat_inLoaderPhotoSelected, -6113849);
        defaultColors.put(key_chat_inLoaderPhotoIcon, -197380);
        defaultColors.put(key_chat_inLoaderPhotoIconSelected, -1314571);
        defaultColors.put(key_chat_outLoaderPhoto, -8011912);
        defaultColors.put(key_chat_outLoaderPhotoSelected, -8538000);
        defaultColors.put(key_chat_outLoaderPhotoIcon, -2427453);
        defaultColors.put(key_chat_outLoaderPhotoIconSelected, -4134748);
        defaultColors.put(key_chat_mediaLoaderPhoto, 1711276032);
        defaultColors.put(key_chat_mediaLoaderPhotoSelected, Integer.valueOf((int) ACTION_BAR_PHOTO_VIEWER_COLOR));
        defaultColors.put(key_chat_mediaLoaderPhotoIcon, -1);
        defaultColors.put(key_chat_mediaLoaderPhotoIconSelected, -2500135);
        defaultColors.put(key_chat_secretTimerBackground, -868326258);
        defaultColors.put(key_chat_secretTimerText, -1);
        defaultColors.put(key_profile_creatorIcon, -12937771);
        defaultColors.put(key_profile_actionIcon, -8288630);
        defaultColors.put(key_profile_actionBackground, -1);
        defaultColors.put(key_profile_actionPressedBackground, -855310);
        defaultColors.put(key_profile_verifiedBackground, -5056776);
        defaultColors.put(key_profile_verifiedCheck, -11959368);
        defaultColors.put(key_profile_title, -1);
        defaultColors.put(key_profile_status, -2626822);
        defaultColors.put(key_profile_tabText, -7893872);
        defaultColors.put(key_profile_tabSelectedText, -12937771);
        defaultColors.put(key_profile_tabSelectedLine, -11557143);
        defaultColors.put(key_profile_tabSelector, Integer.valueOf((int) AndroidUtilities.LIGHT_STATUS_BAR_OVERLAY));
        defaultColors.put(key_player_actionBar, -1);
        defaultColors.put(key_player_actionBarSelector, Integer.valueOf((int) AndroidUtilities.LIGHT_STATUS_BAR_OVERLAY));
        defaultColors.put(key_player_actionBarTitle, -13683656);
        defaultColors.put(key_player_actionBarTop, -1728053248);
        defaultColors.put(key_player_actionBarSubtitle, -7697782);
        defaultColors.put(key_player_actionBarItems, -7697782);
        defaultColors.put(key_player_background, -1);
        defaultColors.put(key_player_time, -7564650);
        defaultColors.put(key_player_progressBackground, -1315344);
        defaultColors.put(key_player_progressBackground2, -3353637);
        defaultColors.put(key_player_progressCachedBackground, -3810064);
        defaultColors.put(key_player_progress, -11228437);
        defaultColors.put(key_player_button, Integer.valueOf((int) ACTION_BAR_MEDIA_PICKER_COLOR));
        defaultColors.put(key_player_buttonActive, -11753238);
        defaultColors.put(key_sheet_scrollUp, -1973016);
        defaultColors.put(key_sheet_other, -3551789);
        defaultColors.put(key_files_folderIcon, -1);
        defaultColors.put(key_files_folderIconBackground, -10637333);
        defaultColors.put(key_files_iconText, -1);
        defaultColors.put(key_sessions_devicesImage, -6908266);
        defaultColors.put(key_passport_authorizeBackground, -12211217);
        defaultColors.put(key_passport_authorizeBackgroundSelected, -12542501);
        defaultColors.put(key_passport_authorizeText, -1);
        defaultColors.put(key_location_sendLocationBackground, -12149258);
        defaultColors.put(key_location_sendLocationIcon, -1);
        defaultColors.put(key_location_sendLocationText, -14906664);
        defaultColors.put(key_location_sendLiveLocationBackground, -11550140);
        defaultColors.put(key_location_sendLiveLocationIcon, -1);
        defaultColors.put(key_location_sendLiveLocationText, -13194460);
        defaultColors.put(key_location_liveLocationProgress, -13262875);
        defaultColors.put(key_location_placeLocationBackground, -11753238);
        defaultColors.put(key_location_actionIcon, -12959675);
        defaultColors.put(key_location_actionActiveIcon, -12414746);
        defaultColors.put(key_location_actionBackground, -1);
        defaultColors.put(key_location_actionPressedBackground, -855310);
        defaultColors.put(key_dialog_liveLocationProgress, -13262875);
        defaultColors.put(key_calls_callReceivedGreenIcon, -16725933);
        defaultColors.put(key_calls_callReceivedRedIcon, -47032);
        defaultColors.put(key_featuredStickers_addedIcon, -11491093);
        defaultColors.put(key_featuredStickers_buttonProgress, -1);
        defaultColors.put(key_featuredStickers_addButton, -11491093);
        defaultColors.put(key_featuredStickers_addButtonPressed, -12346402);
        defaultColors.put(key_featuredStickers_removeButtonText, -11496493);
        defaultColors.put(key_featuredStickers_buttonText, -1);
        defaultColors.put(key_featuredStickers_unread, -11688214);
        defaultColors.put(key_inappPlayerPerformer, -13683656);
        defaultColors.put(key_inappPlayerTitle, -13683656);
        defaultColors.put(key_inappPlayerBackground, -1);
        defaultColors.put(key_inappPlayerPlayPause, -10309397);
        defaultColors.put(key_inappPlayerClose, -7629157);
        defaultColors.put(key_returnToCallBackground, -12279325);
        defaultColors.put(key_returnToCallMutedBackground, -6445135);
        defaultColors.put(key_returnToCallText, -1);
        defaultColors.put(key_sharedMedia_startStopLoadIcon, -13196562);
        defaultColors.put(key_sharedMedia_linkPlaceholder, -986123);
        defaultColors.put(key_sharedMedia_linkPlaceholderText, -4735293);
        defaultColors.put(key_sharedMedia_photoPlaceholder, -1182729);
        defaultColors.put(key_sharedMedia_actionMode, -12154957);
        defaultColors.put(key_checkbox, -10567099);
        defaultColors.put(key_checkboxCheck, -1);
        defaultColors.put(key_checkboxDisabled, -5195326);
        defaultColors.put(key_stickers_menu, -4801083);
        defaultColors.put(key_stickers_menuSelector, Integer.valueOf((int) AndroidUtilities.LIGHT_STATUS_BAR_OVERLAY));
        defaultColors.put(key_changephoneinfo_image, -4669499);
        defaultColors.put(key_changephoneinfo_image2, -11491350);
        defaultColors.put(key_groupcreate_hintText, -6182221);
        defaultColors.put(key_groupcreate_cursor, -11361317);
        defaultColors.put(key_groupcreate_sectionShadow, -16777216);
        defaultColors.put(key_groupcreate_sectionText, -8617336);
        defaultColors.put(key_groupcreate_spanText, -14540254);
        defaultColors.put(key_groupcreate_spanBackground, -855310);
        defaultColors.put(key_groupcreate_spanDelete, -1);
        defaultColors.put(key_contacts_inviteBackground, -11157919);
        defaultColors.put(key_contacts_inviteText, -1);
        defaultColors.put(key_login_progressInner, -1971470);
        defaultColors.put(key_login_progressOuter, -10313520);
        defaultColors.put(key_musicPicker_checkbox, -14043401);
        defaultColors.put(key_musicPicker_checkboxCheck, -1);
        defaultColors.put(key_musicPicker_buttonBackground, -10702870);
        defaultColors.put(key_musicPicker_buttonIcon, -1);
        defaultColors.put(key_picker_enabledButton, -15095832);
        defaultColors.put(key_picker_disabledButton, -6710887);
        defaultColors.put(key_picker_badge, -14043401);
        defaultColors.put(key_picker_badgeText, -1);
        defaultColors.put(key_chat_botSwitchToInlineText, -12348980);
        defaultColors.put(key_undo_background, -366530760);
        defaultColors.put(key_undo_cancelColor, -8008961);
        defaultColors.put(key_undo_infoColor, -1);
        defaultColors.put(key_wallet_blackBackground, -16777216);
        defaultColors.put(key_wallet_graySettingsBackground, -986896);
        defaultColors.put(key_wallet_grayBackground, -14079703);
        defaultColors.put(key_wallet_whiteBackground, -1);
        defaultColors.put(key_wallet_blackBackgroundSelector, Integer.valueOf((int) ACTION_BAR_WHITE_SELECTOR_COLOR));
        defaultColors.put(key_wallet_whiteText, -1);
        defaultColors.put(key_wallet_blackText, -14540254);
        defaultColors.put(key_wallet_statusText, -8355712);
        defaultColors.put(key_wallet_grayText, -8947849);
        defaultColors.put(key_wallet_grayText2, -10066330);
        defaultColors.put(key_wallet_greenText, -13129704);
        defaultColors.put(key_wallet_redText, -2408384);
        defaultColors.put(key_wallet_dateText, -6710887);
        defaultColors.put(key_wallet_commentText, -6710887);
        defaultColors.put(key_wallet_releaseBackground, -13599557);
        defaultColors.put(key_wallet_pullBackground, Integer.valueOf((int) MSG_OUT_COLOR_BLACK));
        defaultColors.put(key_wallet_buttonBackground, -12082714);
        defaultColors.put(key_wallet_buttonPressedBackground, -13923114);
        defaultColors.put(key_wallet_buttonText, -1);
        defaultColors.put(key_wallet_addressConfirmBackground, 218103808);
        defaultColors.put(key_chat_outTextSelectionHighlight, 775919907);
        defaultColors.put(key_chat_inTextSelectionHighlight, 1348643299);
        defaultColors.put(key_chat_TextSelectionCursor, -12476440);
        defaultColors.put(key_chat_outTextSelectionCursor, -12476440);
        defaultColors.put(key_chat_BlurAlpha, -16777216);
        defaultColors.put(key_statisticChartSignature, 2133140777);
        defaultColors.put(key_statisticChartSignatureAlpha, 2133140777);
        defaultColors.put(key_statisticChartHintLine, 437792059);
        defaultColors.put(key_statisticChartActiveLine, Integer.valueOf((int) AndroidUtilities.DARK_STATUS_BAR_OVERLAY));
        defaultColors.put(key_statisticChartInactivePickerChart, -1713180935);
        defaultColors.put(key_statisticChartActivePickerChart, -658846503);
        defaultColors.put(key_statisticChartRipple, 746495415);
        defaultColors.put(key_statisticChartBackZoomColor, -15692829);
        defaultColors.put(key_statisticChartCheckboxInactive, -4342339);
        defaultColors.put(key_statisticChartNightIconColor, -7434605);
        defaultColors.put(key_statisticChartChevronColor, -2959913);
        defaultColors.put(key_statisticChartHighlightColor, 552398060);
        defaultColors.put(key_statisticChartPopupBackground, -1);
        defaultColors.put(key_statisticChartLine_blue, -13467675);
        defaultColors.put(key_statisticChartLine_green, -10369198);
        defaultColors.put(key_statisticChartLine_red, -2075818);
        defaultColors.put(key_statisticChartLine_golden, -2180600);
        defaultColors.put(key_statisticChartLine_lightblue, -10966803);
        defaultColors.put(key_statisticChartLine_lightgreen, -7352519);
        defaultColors.put(key_statisticChartLine_orange, -1853657);
        defaultColors.put(key_statisticChartLine_indigo, -8422925);
        defaultColors.put(key_statisticChartLineEmpty, -1118482);
        defaultColors.put(key_actionBarTipBackground, -12292204);
        defaultColors.put(key_voipgroup_checkMenu, -9718023);
        defaultColors.put(key_voipgroup_muteButton, -8919716);
        defaultColors.put(key_voipgroup_muteButton2, -8528726);
        defaultColors.put(key_voipgroup_muteButton3, -11089922);
        defaultColors.put(key_voipgroup_searchText, -1);
        defaultColors.put(key_voipgroup_searchPlaceholder, -8024684);
        defaultColors.put(key_voipgroup_searchBackground, -13616313);
        defaultColors.put(key_voipgroup_leaveCallMenu, -35467);
        defaultColors.put(key_voipgroup_scrollUp, -13023660);
        defaultColors.put(key_voipgroup_soundButton, 2100052301);
        defaultColors.put(key_voipgroup_soundButtonActive, 2099422443);
        defaultColors.put(key_voipgroup_soundButtonActiveScrolled, -2110540545);
        defaultColors.put(key_voipgroup_soundButton2, 2099796282);
        defaultColors.put(key_voipgroup_soundButtonActive2, 2098771793);
        defaultColors.put(key_voipgroup_soundButtonActive2Scrolled, -2111520954);
        defaultColors.put(key_voipgroup_leaveButton, 2113363036);
        defaultColors.put(key_voipgroup_leaveButtonScrolled, -2100212396);
        defaultColors.put(key_voipgroup_connectingProgress, -14107905);
        defaultColors.put(key_voipgroup_disabledButton, -14933463);
        defaultColors.put(key_voipgroup_disabledButtonActive, -13878715);
        defaultColors.put(key_voipgroup_disabledButtonActiveScrolled, -2106088964);
        defaultColors.put(key_voipgroup_unmuteButton, -11297032);
        defaultColors.put(key_voipgroup_unmuteButton2, -10038021);
        defaultColors.put(key_voipgroup_actionBarUnscrolled, -15130842);
        defaultColors.put(key_voipgroup_listViewBackgroundUnscrolled, -14538189);
        defaultColors.put(key_voipgroup_lastSeenTextUnscrolled, -8024684);
        defaultColors.put(key_voipgroup_mutedIconUnscrolled, -8485236);
        defaultColors.put(key_voipgroup_actionBar, -15789289);
        defaultColors.put(key_voipgroup_emptyView, -15065823);
        defaultColors.put(key_voipgroup_actionBarItems, -1);
        defaultColors.put(key_voipgroup_actionBarSubtitle, -7697782);
        defaultColors.put(key_voipgroup_actionBarItemsSelector, 515562495);
        defaultColors.put(key_voipgroup_mutedByAdminIcon, -36752);
        defaultColors.put(key_voipgroup_mutedIcon, -9471616);
        defaultColors.put(key_voipgroup_lastSeenText, -8813686);
        defaultColors.put(key_voipgroup_nameText, -1);
        defaultColors.put(key_voipgroup_listViewBackground, -14933463);
        defaultColors.put(key_voipgroup_dialogBackground, -14933463);
        defaultColors.put(key_voipgroup_listeningText, -11683585);
        defaultColors.put(key_voipgroup_speakingText, -8917379);
        defaultColors.put(key_voipgroup_listSelector, 251658239);
        defaultColors.put(key_voipgroup_inviteMembersBackground, -14538189);
        defaultColors.put(key_voipgroup_overlayBlue1, -13906177);
        defaultColors.put(key_voipgroup_overlayBlue2, -16156957);
        defaultColors.put(key_voipgroup_overlayGreen1, -15551198);
        defaultColors.put(key_voipgroup_overlayGreen2, -16722239);
        defaultColors.put(key_voipgroup_topPanelBlue1, -10434565);
        defaultColors.put(key_voipgroup_topPanelBlue2, -11427847);
        defaultColors.put(key_voipgroup_topPanelGreen1, -11350435);
        defaultColors.put(key_voipgroup_topPanelGreen2, -16731712);
        defaultColors.put(key_voipgroup_topPanelGray, -8021590);
        defaultColors.put(key_voipgroup_overlayAlertGradientMuted, -14455406);
        defaultColors.put(key_voipgroup_overlayAlertGradientMuted2, -13873813);
        defaultColors.put(key_voipgroup_overlayAlertGradientUnmuted, -15955316);
        defaultColors.put(key_voipgroup_overlayAlertGradientUnmuted2, -14136203);
        defaultColors.put(key_voipgroup_mutedByAdminGradient, -11033346);
        defaultColors.put(key_voipgroup_mutedByAdminGradient2, -1026983);
        defaultColors.put(key_voipgroup_mutedByAdminGradient3, -9015575);
        defaultColors.put(key_voipgroup_overlayAlertMutedByAdmin, -9998178);
        defaultColors.put(key_voipgroup_overlayAlertMutedByAdmin2, -13676424);
        defaultColors.put(key_voipgroup_mutedByAdminMuteButton, 2138612735);
        defaultColors.put(key_voipgroup_mutedByAdminMuteButtonDisabled, 863544319);
        defaultColors.put(key_voipgroup_windowBackgroundWhiteInputField, -2368549);
        defaultColors.put(key_voipgroup_windowBackgroundWhiteInputFieldActivated, -13129232);
        defaultColors.put(key_chat_outReactionButtonBackground, -8863118);
        defaultColors.put(key_chat_inReactionButtonBackground, -9259544);
        defaultColors.put(key_chat_inReactionButtonText, -12940081);
        defaultColors.put(key_chat_outReactionButtonText, -11162801);
        defaultColors.put(key_chat_inReactionButtonTextSelected, -1);
        defaultColors.put(key_chat_outReactionButtonTextSelected, -1);
        defaultColors.put(key_premiumGradient1, -11164161);
        defaultColors.put(key_premiumGradient2, -5806081);
        defaultColors.put(key_premiumGradient3, -2401123);
        defaultColors.put(key_premiumGradient4, -816858);
        defaultColors.put(key_premiumGradientBackground1, -11164161);
        defaultColors.put(key_premiumGradientBackground2, -5806081);
        defaultColors.put(key_premiumGradientBackground3, -2401123);
        defaultColors.put(key_premiumGradientBackground4, -816858);
        defaultColors.put(key_premiumGradientBackgroundOverlay, -1);
        defaultColors.put(key_premiumStartGradient1, -1);
        defaultColors.put(key_premiumStartGradient2, -1839878);
        defaultColors.put(key_premiumStartSmallStarsColor, Integer.valueOf(ColorUtils.setAlphaComponent(-1, 90)));
        defaultColors.put(key_premiumStartSmallStarsColor2, Integer.valueOf(ColorUtils.setAlphaComponent(-1, 90)));
        defaultColors.put(key_premiumGradientBottomSheet1, -10773017);
        defaultColors.put(key_premiumGradientBottomSheet2, -5535779);
        defaultColors.put(key_premiumGradientBottomSheet3, -1600322);
        fallbackKeys.put(key_chat_inAdminText, key_chat_inTimeText);
        fallbackKeys.put(key_chat_inAdminSelectedText, key_chat_inTimeSelectedText);
        fallbackKeys.put(key_player_progressCachedBackground, key_player_progressBackground);
        fallbackKeys.put(key_chat_inAudioCacheSeekbar, key_chat_inAudioSeekbar);
        fallbackKeys.put(key_chat_outAudioCacheSeekbar, key_chat_outAudioSeekbar);
        fallbackKeys.put(key_chat_emojiSearchBackground, key_chat_emojiPanelStickerPackSelector);
        fallbackKeys.put(key_location_sendLiveLocationIcon, key_location_sendLocationIcon);
        fallbackKeys.put(key_changephoneinfo_image2, key_featuredStickers_addButton);
        fallbackKeys.put(key_graySectionText, key_windowBackgroundWhiteGrayText2);
        fallbackKeys.put(key_chat_inMediaIcon, key_chat_inBubble);
        fallbackKeys.put(key_chat_outMediaIcon, key_chat_outBubble);
        fallbackKeys.put(key_chat_inMediaIconSelected, key_chat_inBubbleSelected);
        fallbackKeys.put(key_chat_outMediaIconSelected, key_chat_outBubbleSelected);
        fallbackKeys.put(key_chats_actionUnreadIcon, key_profile_actionIcon);
        fallbackKeys.put(key_chats_actionUnreadBackground, key_profile_actionBackground);
        fallbackKeys.put(key_chats_actionUnreadPressedBackground, key_profile_actionPressedBackground);
        fallbackKeys.put(key_dialog_inlineProgressBackground, key_windowBackgroundGray);
        fallbackKeys.put(key_dialog_inlineProgress, key_chats_menuItemIcon);
        fallbackKeys.put(key_groupcreate_spanDelete, key_chats_actionIcon);
        fallbackKeys.put(key_sharedMedia_photoPlaceholder, key_windowBackgroundGray);
        fallbackKeys.put(key_chat_attachPollBackground, key_chat_attachAudioBackground);
        fallbackKeys.put(key_chat_attachPollIcon, key_chat_attachAudioIcon);
        fallbackKeys.put(key_chats_onlineCircle, key_windowBackgroundWhiteBlueText);
        fallbackKeys.put(key_windowBackgroundWhiteBlueButton, key_windowBackgroundWhiteValueText);
        fallbackKeys.put(key_windowBackgroundWhiteBlueIcon, key_windowBackgroundWhiteValueText);
        fallbackKeys.put(key_undo_background, key_chat_gifSaveHintBackground);
        fallbackKeys.put(key_undo_cancelColor, key_chat_gifSaveHintText);
        fallbackKeys.put(key_undo_infoColor, key_chat_gifSaveHintText);
        fallbackKeys.put(key_windowBackgroundUnchecked, key_windowBackgroundWhite);
        fallbackKeys.put(key_windowBackgroundChecked, key_windowBackgroundWhite);
        fallbackKeys.put(key_switchTrackBlue, key_switchTrack);
        fallbackKeys.put(key_switchTrackBlueChecked, key_switchTrackChecked);
        fallbackKeys.put(key_switchTrackBlueThumb, key_windowBackgroundWhite);
        fallbackKeys.put(key_switchTrackBlueThumbChecked, key_windowBackgroundWhite);
        fallbackKeys.put(key_windowBackgroundCheckText, key_windowBackgroundWhite);
        fallbackKeys.put(key_contextProgressInner4, key_contextProgressInner1);
        fallbackKeys.put(key_contextProgressOuter4, key_contextProgressOuter1);
        fallbackKeys.put(key_switchTrackBlueSelector, key_listSelector);
        fallbackKeys.put(key_switchTrackBlueSelectorChecked, key_listSelector);
        fallbackKeys.put(key_chat_emojiBottomPanelIcon, key_chat_emojiPanelIcon);
        fallbackKeys.put(key_chat_emojiSearchIcon, key_chat_emojiPanelIcon);
        fallbackKeys.put(key_chat_emojiPanelStickerSetNameHighlight, key_windowBackgroundWhiteBlueText4);
        fallbackKeys.put(key_chat_emojiPanelStickerPackSelectorLine, key_chat_emojiPanelIconSelected);
        fallbackKeys.put(key_sharedMedia_actionMode, key_actionBarDefault);
        fallbackKeys.put(key_sheet_scrollUp, key_chat_emojiPanelStickerPackSelector);
        fallbackKeys.put(key_sheet_other, key_player_actionBarItems);
        fallbackKeys.put(key_dialogSearchBackground, key_chat_emojiPanelStickerPackSelector);
        fallbackKeys.put(key_dialogSearchHint, key_chat_emojiPanelIcon);
        fallbackKeys.put(key_dialogSearchIcon, key_chat_emojiPanelIcon);
        fallbackKeys.put(key_dialogSearchText, key_windowBackgroundWhiteBlackText);
        fallbackKeys.put(key_dialogFloatingButton, key_dialogRoundCheckBox);
        fallbackKeys.put(key_dialogFloatingButtonPressed, key_dialogRoundCheckBox);
        fallbackKeys.put(key_dialogFloatingIcon, key_dialogRoundCheckBoxCheck);
        fallbackKeys.put(key_dialogShadowLine, key_chat_emojiPanelShadowLine);
        fallbackKeys.put(key_actionBarDefaultArchived, key_actionBarDefault);
        fallbackKeys.put(key_actionBarDefaultArchivedSelector, key_actionBarDefaultSelector);
        fallbackKeys.put(key_actionBarDefaultArchivedIcon, key_actionBarDefaultIcon);
        fallbackKeys.put(key_actionBarDefaultArchivedTitle, key_actionBarDefaultTitle);
        fallbackKeys.put(key_actionBarDefaultArchivedSearch, key_actionBarDefaultSearch);
        fallbackKeys.put(key_actionBarDefaultArchivedSearchPlaceholder, key_actionBarDefaultSearchPlaceholder);
        fallbackKeys.put(key_chats_message_threeLines, key_chats_message);
        fallbackKeys.put(key_chats_nameMessage_threeLines, key_chats_nameMessage);
        fallbackKeys.put(key_chats_nameArchived, key_chats_name);
        fallbackKeys.put(key_chats_nameMessageArchived, key_chats_nameMessage);
        fallbackKeys.put(key_chats_nameMessageArchived_threeLines, key_chats_nameMessage);
        fallbackKeys.put(key_chats_messageArchived, key_chats_message);
        fallbackKeys.put(key_avatar_backgroundArchived, key_chats_unreadCounterMuted);
        fallbackKeys.put(key_chats_archiveBackground, key_chats_actionBackground);
        fallbackKeys.put(key_chats_archivePinBackground, key_chats_unreadCounterMuted);
        fallbackKeys.put(key_chats_archiveIcon, key_chats_actionIcon);
        fallbackKeys.put(key_chats_archiveText, key_chats_actionIcon);
        fallbackKeys.put(key_actionBarDefaultSubmenuItemIcon, key_dialogIcon);
        fallbackKeys.put(key_checkboxDisabled, key_chats_unreadCounterMuted);
        fallbackKeys.put(key_chat_status, key_actionBarDefaultSubtitle);
        fallbackKeys.put(key_chat_inGreenCall, key_calls_callReceivedGreenIcon);
        fallbackKeys.put(key_chat_inRedCall, key_calls_callReceivedRedIcon);
        fallbackKeys.put(key_chat_outGreenCall, key_calls_callReceivedGreenIcon);
        fallbackKeys.put(key_actionBarTabActiveText, key_actionBarDefaultTitle);
        fallbackKeys.put(key_actionBarTabUnactiveText, key_actionBarDefaultSubtitle);
        fallbackKeys.put(key_actionBarTabLine, key_actionBarDefaultTitle);
        fallbackKeys.put(key_actionBarTabSelector, key_actionBarDefaultSelector);
        fallbackKeys.put(key_profile_status, key_avatar_subtitleInProfileBlue);
        fallbackKeys.put(key_chats_menuTopBackgroundCats, key_avatar_backgroundActionBarBlue);
        fallbackKeys.put(key_chat_outLinkSelectBackground, key_chat_linkSelectBackground);
        fallbackKeys.put(key_actionBarDefaultSubmenuSeparator, key_windowBackgroundGray);
        fallbackKeys.put(key_chat_attachPermissionImage, key_dialogTextBlack);
        fallbackKeys.put(key_chat_attachPermissionMark, key_chat_sentError);
        fallbackKeys.put(key_chat_attachPermissionText, key_dialogTextBlack);
        fallbackKeys.put(key_chat_attachEmptyImage, key_emptyListPlaceholder);
        fallbackKeys.put(key_actionBarBrowser, key_actionBarDefault);
        fallbackKeys.put(key_chats_sentReadCheck, key_chats_sentCheck);
        fallbackKeys.put(key_chat_outSentCheckRead, key_chat_outSentCheck);
        fallbackKeys.put(key_chat_outSentCheckReadSelected, key_chat_outSentCheckSelected);
        fallbackKeys.put(key_chats_archivePullDownBackground, key_chats_unreadCounterMuted);
        fallbackKeys.put(key_chats_archivePullDownBackgroundActive, key_chats_actionBackground);
        fallbackKeys.put(key_avatar_backgroundArchivedHidden, key_avatar_backgroundSaved);
        fallbackKeys.put(key_featuredStickers_removeButtonText, key_featuredStickers_addButtonPressed);
        fallbackKeys.put(key_dialogEmptyImage, key_player_time);
        fallbackKeys.put(key_dialogEmptyText, key_player_time);
        fallbackKeys.put(key_location_actionIcon, key_dialogTextBlack);
        fallbackKeys.put(key_location_actionActiveIcon, key_windowBackgroundWhiteBlueText7);
        fallbackKeys.put(key_location_actionBackground, key_dialogBackground);
        fallbackKeys.put(key_location_actionPressedBackground, key_dialogBackgroundGray);
        fallbackKeys.put(key_location_sendLocationText, key_windowBackgroundWhiteBlueText7);
        fallbackKeys.put(key_location_sendLiveLocationText, key_windowBackgroundWhiteGreenText);
        fallbackKeys.put(key_chat_outTextSelectionHighlight, key_chat_textSelectBackground);
        fallbackKeys.put(key_chat_inTextSelectionHighlight, key_chat_textSelectBackground);
        fallbackKeys.put(key_chat_TextSelectionCursor, key_chat_messagePanelCursor);
        fallbackKeys.put(key_chat_outTextSelectionCursor, key_chat_TextSelectionCursor);
        fallbackKeys.put(key_chat_inPollCorrectAnswer, key_chat_attachLocationBackground);
        fallbackKeys.put(key_chat_outPollCorrectAnswer, key_chat_attachLocationBackground);
        fallbackKeys.put(key_chat_inPollWrongAnswer, key_chat_attachAudioBackground);
        fallbackKeys.put(key_chat_outPollWrongAnswer, key_chat_attachAudioBackground);
        fallbackKeys.put(key_profile_tabText, key_windowBackgroundWhiteGrayText);
        fallbackKeys.put(key_profile_tabSelectedText, key_windowBackgroundWhiteBlueHeader);
        fallbackKeys.put(key_profile_tabSelectedLine, key_windowBackgroundWhiteBlueHeader);
        fallbackKeys.put(key_profile_tabSelector, key_listSelector);
        fallbackKeys.put(key_statisticChartPopupBackground, key_dialogBackground);
        fallbackKeys.put(key_chat_attachGalleryText, key_chat_attachGalleryBackground);
        fallbackKeys.put(key_chat_attachAudioText, key_chat_attachAudioBackground);
        fallbackKeys.put(key_chat_attachFileText, key_chat_attachFileBackground);
        fallbackKeys.put(key_chat_attachContactText, key_chat_attachContactBackground);
        fallbackKeys.put(key_chat_attachLocationText, key_chat_attachLocationBackground);
        fallbackKeys.put(key_chat_attachPollText, key_chat_attachPollBackground);
        fallbackKeys.put(key_chat_inPsaNameText, key_avatar_nameInMessageGreen);
        fallbackKeys.put(key_chat_outPsaNameText, key_avatar_nameInMessageGreen);
        fallbackKeys.put(key_chat_outAdminText, key_chat_outTimeText);
        fallbackKeys.put(key_chat_outAdminSelectedText, key_chat_outTimeSelectedText);
        fallbackKeys.put(key_returnToCallMutedBackground, key_windowBackgroundWhite);
        fallbackKeys.put(key_dialogSwipeRemove, key_avatar_backgroundRed);
        fallbackKeys.put(key_chat_inReactionButtonBackground, key_chat_inLoader);
        fallbackKeys.put(key_chat_outReactionButtonBackground, key_chat_outLoader);
        fallbackKeys.put(key_chat_inReactionButtonText, key_chat_inPreviewInstantText);
        fallbackKeys.put(key_chat_outReactionButtonText, key_chat_outPreviewInstantText);
        fallbackKeys.put(key_chat_inReactionButtonTextSelected, key_windowBackgroundWhite);
        fallbackKeys.put(key_chat_outReactionButtonTextSelected, key_windowBackgroundWhite);
        fallbackKeys.put(key_dialogReactionMentionBackground, key_voipgroup_mutedByAdminGradient2);
        themeAccentExclusionKeys.addAll(Arrays.asList(keys_avatar_background));
        themeAccentExclusionKeys.addAll(Arrays.asList(keys_avatar_nameInMessage));
        themeAccentExclusionKeys.add(key_chat_attachFileBackground);
        themeAccentExclusionKeys.add(key_chat_attachGalleryBackground);
        themeAccentExclusionKeys.add(key_chat_attachFileText);
        themeAccentExclusionKeys.add(key_chat_attachGalleryText);
        themeAccentExclusionKeys.add(key_statisticChartLine_blue);
        themeAccentExclusionKeys.add(key_statisticChartLine_green);
        themeAccentExclusionKeys.add(key_statisticChartLine_red);
        themeAccentExclusionKeys.add(key_statisticChartLine_golden);
        themeAccentExclusionKeys.add(key_statisticChartLine_lightblue);
        themeAccentExclusionKeys.add(key_statisticChartLine_lightgreen);
        themeAccentExclusionKeys.add(key_statisticChartLine_orange);
        themeAccentExclusionKeys.add(key_statisticChartLine_indigo);
        themeAccentExclusionKeys.add(key_voipgroup_checkMenu);
        themeAccentExclusionKeys.add(key_voipgroup_muteButton);
        themeAccentExclusionKeys.add(key_voipgroup_muteButton2);
        themeAccentExclusionKeys.add(key_voipgroup_muteButton3);
        themeAccentExclusionKeys.add(key_voipgroup_searchText);
        themeAccentExclusionKeys.add(key_voipgroup_searchPlaceholder);
        themeAccentExclusionKeys.add(key_voipgroup_searchBackground);
        themeAccentExclusionKeys.add(key_voipgroup_leaveCallMenu);
        themeAccentExclusionKeys.add(key_voipgroup_scrollUp);
        themeAccentExclusionKeys.add(key_voipgroup_blueText);
        themeAccentExclusionKeys.add(key_voipgroup_soundButton);
        themeAccentExclusionKeys.add(key_voipgroup_soundButtonActive);
        themeAccentExclusionKeys.add(key_voipgroup_soundButtonActiveScrolled);
        themeAccentExclusionKeys.add(key_voipgroup_soundButton2);
        themeAccentExclusionKeys.add(key_voipgroup_soundButtonActive2);
        themeAccentExclusionKeys.add(key_voipgroup_soundButtonActive2Scrolled);
        themeAccentExclusionKeys.add(key_voipgroup_leaveButton);
        themeAccentExclusionKeys.add(key_voipgroup_leaveButtonScrolled);
        themeAccentExclusionKeys.add(key_voipgroup_connectingProgress);
        themeAccentExclusionKeys.add(key_voipgroup_disabledButton);
        themeAccentExclusionKeys.add(key_voipgroup_disabledButtonActive);
        themeAccentExclusionKeys.add(key_voipgroup_disabledButtonActiveScrolled);
        themeAccentExclusionKeys.add(key_voipgroup_unmuteButton);
        themeAccentExclusionKeys.add(key_voipgroup_unmuteButton2);
        themeAccentExclusionKeys.add(key_voipgroup_actionBarUnscrolled);
        themeAccentExclusionKeys.add(key_voipgroup_listViewBackgroundUnscrolled);
        themeAccentExclusionKeys.add(key_voipgroup_lastSeenTextUnscrolled);
        themeAccentExclusionKeys.add(key_voipgroup_mutedIconUnscrolled);
        themeAccentExclusionKeys.add(key_voipgroup_actionBar);
        themeAccentExclusionKeys.add(key_voipgroup_emptyView);
        themeAccentExclusionKeys.add(key_voipgroup_actionBarItems);
        themeAccentExclusionKeys.add(key_voipgroup_actionBarSubtitle);
        themeAccentExclusionKeys.add(key_voipgroup_actionBarItemsSelector);
        themeAccentExclusionKeys.add(key_voipgroup_mutedByAdminIcon);
        themeAccentExclusionKeys.add(key_voipgroup_mutedIcon);
        themeAccentExclusionKeys.add(key_voipgroup_lastSeenText);
        themeAccentExclusionKeys.add(key_voipgroup_nameText);
        themeAccentExclusionKeys.add(key_voipgroup_listViewBackground);
        themeAccentExclusionKeys.add(key_voipgroup_listeningText);
        themeAccentExclusionKeys.add(key_voipgroup_speakingText);
        themeAccentExclusionKeys.add(key_voipgroup_listSelector);
        themeAccentExclusionKeys.add(key_voipgroup_inviteMembersBackground);
        themeAccentExclusionKeys.add(key_voipgroup_dialogBackground);
        themeAccentExclusionKeys.add(key_voipgroup_overlayGreen1);
        themeAccentExclusionKeys.add(key_voipgroup_overlayGreen2);
        themeAccentExclusionKeys.add(key_voipgroup_overlayBlue1);
        themeAccentExclusionKeys.add(key_voipgroup_overlayBlue2);
        themeAccentExclusionKeys.add(key_voipgroup_topPanelGreen1);
        themeAccentExclusionKeys.add(key_voipgroup_topPanelGreen2);
        themeAccentExclusionKeys.add(key_voipgroup_topPanelBlue1);
        themeAccentExclusionKeys.add(key_voipgroup_topPanelBlue2);
        themeAccentExclusionKeys.add(key_voipgroup_topPanelGray);
        themeAccentExclusionKeys.add(key_voipgroup_overlayAlertGradientMuted);
        themeAccentExclusionKeys.add(key_voipgroup_overlayAlertGradientMuted2);
        themeAccentExclusionKeys.add(key_voipgroup_overlayAlertGradientUnmuted);
        themeAccentExclusionKeys.add(key_voipgroup_overlayAlertGradientUnmuted2);
        themeAccentExclusionKeys.add(key_voipgroup_overlayAlertMutedByAdmin);
        themeAccentExclusionKeys.add(key_voipgroup_overlayAlertMutedByAdmin2);
        themeAccentExclusionKeys.add(key_voipgroup_mutedByAdminGradient);
        themeAccentExclusionKeys.add(key_voipgroup_mutedByAdminGradient2);
        themeAccentExclusionKeys.add(key_voipgroup_mutedByAdminGradient3);
        themeAccentExclusionKeys.add(key_voipgroup_mutedByAdminMuteButton);
        themeAccentExclusionKeys.add(key_voipgroup_mutedByAdminMuteButtonDisabled);
        themeAccentExclusionKeys.add(key_voipgroup_windowBackgroundWhiteInputField);
        themeAccentExclusionKeys.add(key_voipgroup_windowBackgroundWhiteInputFieldActivated);
        themeAccentExclusionKeys.add(key_premiumGradient1);
        themeAccentExclusionKeys.add(key_premiumGradient2);
        themeAccentExclusionKeys.add(key_premiumGradient3);
        themeAccentExclusionKeys.add(key_premiumGradient4);
        myMessagesBubblesColorKeys.add(key_chat_outBubble);
        myMessagesBubblesColorKeys.add(key_chat_outBubbleSelected);
        myMessagesBubblesColorKeys.add(key_chat_outBubbleShadow);
        myMessagesBubblesColorKeys.add(key_chat_outBubbleGradient1);
        myMessagesColorKeys.add(key_chat_outGreenCall);
        myMessagesColorKeys.add(key_chat_outSentCheck);
        myMessagesColorKeys.add(key_chat_outSentCheckSelected);
        myMessagesColorKeys.add(key_chat_outSentCheckRead);
        myMessagesColorKeys.add(key_chat_outSentCheckReadSelected);
        myMessagesColorKeys.add(key_chat_outSentClock);
        myMessagesColorKeys.add(key_chat_outSentClockSelected);
        myMessagesColorKeys.add(key_chat_outMediaIcon);
        myMessagesColorKeys.add(key_chat_outMediaIconSelected);
        myMessagesColorKeys.add(key_chat_outViews);
        myMessagesColorKeys.add(key_chat_outViewsSelected);
        myMessagesColorKeys.add(key_chat_outMenu);
        myMessagesColorKeys.add(key_chat_outMenuSelected);
        myMessagesColorKeys.add(key_chat_outInstant);
        myMessagesColorKeys.add(key_chat_outInstantSelected);
        myMessagesColorKeys.add(key_chat_outPreviewInstantText);
        myMessagesColorKeys.add(key_chat_outPreviewInstantSelectedText);
        myMessagesColorKeys.add(key_chat_outForwardedNameText);
        myMessagesColorKeys.add(key_chat_outViaBotNameText);
        myMessagesColorKeys.add(key_chat_outReplyLine);
        myMessagesColorKeys.add(key_chat_outReplyNameText);
        myMessagesColorKeys.add(key_chat_outReplyMessageText);
        myMessagesColorKeys.add(key_chat_outReplyMediaMessageText);
        myMessagesColorKeys.add(key_chat_outReplyMediaMessageSelectedText);
        myMessagesColorKeys.add(key_chat_outPreviewLine);
        myMessagesColorKeys.add(key_chat_outSiteNameText);
        myMessagesColorKeys.add(key_chat_outContactNameText);
        myMessagesColorKeys.add(key_chat_outContactPhoneText);
        myMessagesColorKeys.add(key_chat_outContactPhoneSelectedText);
        myMessagesColorKeys.add(key_chat_outAudioProgress);
        myMessagesColorKeys.add(key_chat_outAudioSelectedProgress);
        myMessagesColorKeys.add(key_chat_outTimeText);
        myMessagesColorKeys.add(key_chat_outTimeSelectedText);
        myMessagesColorKeys.add(key_chat_outAudioPerformerText);
        myMessagesColorKeys.add(key_chat_outAudioPerformerSelectedText);
        myMessagesColorKeys.add(key_chat_outAudioTitleText);
        myMessagesColorKeys.add(key_chat_outAudioDurationText);
        myMessagesColorKeys.add(key_chat_outAudioDurationSelectedText);
        myMessagesColorKeys.add(key_chat_outAudioSeekbar);
        myMessagesColorKeys.add(key_chat_outAudioCacheSeekbar);
        myMessagesColorKeys.add(key_chat_outAudioSeekbarSelected);
        myMessagesColorKeys.add(key_chat_outAudioSeekbarFill);
        myMessagesColorKeys.add(key_chat_outVoiceSeekbar);
        myMessagesColorKeys.add(key_chat_outVoiceSeekbarSelected);
        myMessagesColorKeys.add(key_chat_outVoiceSeekbarFill);
        myMessagesColorKeys.add(key_chat_outFileProgress);
        myMessagesColorKeys.add(key_chat_outFileProgressSelected);
        myMessagesColorKeys.add(key_chat_outFileNameText);
        myMessagesColorKeys.add(key_chat_outFileInfoText);
        myMessagesColorKeys.add(key_chat_outFileInfoSelectedText);
        myMessagesColorKeys.add(key_chat_outFileBackground);
        myMessagesColorKeys.add(key_chat_outFileBackgroundSelected);
        myMessagesColorKeys.add(key_chat_outVenueInfoText);
        myMessagesColorKeys.add(key_chat_outVenueInfoSelectedText);
        myMessagesColorKeys.add(key_chat_outLoader);
        myMessagesColorKeys.add(key_chat_outLoaderSelected);
        myMessagesColorKeys.add(key_chat_outLoaderPhoto);
        myMessagesColorKeys.add(key_chat_outLoaderPhotoSelected);
        myMessagesColorKeys.add(key_chat_outLoaderPhotoIcon);
        myMessagesColorKeys.add(key_chat_outLoaderPhotoIconSelected);
        myMessagesColorKeys.add(key_chat_outLocationBackground);
        myMessagesColorKeys.add(key_chat_outLocationIcon);
        myMessagesColorKeys.add(key_chat_outContactBackground);
        myMessagesColorKeys.add(key_chat_outContactIcon);
        myMessagesColorKeys.add(key_chat_outFileIcon);
        myMessagesColorKeys.add(key_chat_outFileSelectedIcon);
        myMessagesColorKeys.add(key_chat_outBroadcast);
        myMessagesColorKeys.add(key_chat_messageTextOut);
        myMessagesColorKeys.add(key_chat_messageLinkOut);
        SharedPreferences themeConfig = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
        ThemeInfo themeInfo2 = new ThemeInfo();
        themeInfo2.name = "Blue";
        themeInfo2.assetName = "bluebubbles.attheme";
        themeInfo2.previewBackgroundColor = -6963476;
        themeInfo2.previewInColor = -1;
        themeInfo2.previewOutColor = -3086593;
        themeInfo2.firstAccentIsDefault = true;
        themeInfo2.currentAccentId = DEFALT_THEME_ACCENT_ID;
        themeInfo2.sortIndex = 1;
        themeInfo2.setAccentColorOptions(new int[]{-10972987, -14444461, -3252606, -8428605, -14380627, -14050257, -7842636, -13464881, -12342073, -11359164, -3317869, -2981834, -8165684, -3256745, -2904512, -8681301}, new int[]{-4660851, -328756, -1572, -4108434, -3031781, -1335, -198952, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{0, -853047, -264993, 0, 0, -135756, -198730, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{0, -2104672, -937328, -2637335, -2639714, -1270157, -3428124, -6570777, -7223828, -6567550, -1793599, -1855875, -4674838, -1336199, -2900876, -6247730}, new int[]{0, -4532067, -1257580, -1524266, -1646910, -1519483, -1324823, -4138509, -4202516, -2040429, -1458474, -1256030, -3814930, -1000039, -1450082, -3485987}, new int[]{0, -1909081, -1592444, -2969879, -2439762, -1137033, -2119471, -6962197, -4857383, -4270699, -3364639, -2117514, -5000734, -1598028, -2045813, -5853742}, new int[]{0, -6371440, -1319256, -1258616, -1712961, -1186647, -1193816, -4467224, -4203544, -3023977, -1061929, -1255788, -2113811, -806526, -1715305, -3485976}, new int[]{99, 9, 10, 11, 12, 13, 14, 0, 1, 2, 3, 4, 5, 6, 7, 8}, new String[]{"", "p-pXcflrmFIBAAAAvXYQk-mCwZU", "JqSUrO0-mFIBAAAAWwTvLzoWGQI", "O-wmAfBPSFADAAAA4zINVfD_bro", "RepJ5uE_SVABAAAAr4d0YhgB850", "-Xc-np9y2VMCAAAARKr0yNNPYW0", "fqv01SQemVIBAAAApND8LDRUhRU", "fqv01SQemVIBAAAApND8LDRUhRU", "RepJ5uE_SVABAAAAr4d0YhgB850", "lp0prF8ISFAEAAAA_p385_CvG0w", "heptcj-hSVACAAAAC9RrMzOa-cs", "PllZ-bf_SFAEAAAA8crRfwZiDNg", "dhf9pceaQVACAAAAbzdVo4SCiZA", "Ujx2TFcJSVACAAAARJ4vLa50MkM", "p-pXcflrmFIBAAAAvXYQk-mCwZU", "dk_wwlghOFACAAAAfz9xrxi6euw"}, new int[]{0, 180, 45, 0, 45, 180, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{0, 52, 46, 57, 45, 64, 52, 35, 36, 41, 50, 50, 35, 38, 37, 30});
        sortAccents(themeInfo2);
        ArrayList<ThemeInfo> arrayList = themes;
        defaultTheme = themeInfo2;
        currentTheme = themeInfo2;
        currentDayTheme = themeInfo2;
        arrayList.add(themeInfo2);
        themesDict.put("Blue", themeInfo2);
        ThemeInfo themeInfo3 = new ThemeInfo();
        themeInfo3.name = "Dark Blue";
        themeInfo3.assetName = "darkblue.attheme";
        themeInfo3.previewBackgroundColor = -10523006;
        themeInfo3.previewInColor = -9009508;
        themeInfo3.previewOutColor = -8214301;
        themeInfo3.sortIndex = 3;
        themeInfo3.setAccentColorOptions(new int[]{-7177260, -9860357, -14440464, -8687151, -9848491, -14053142, -9403671, -10044691, -13203974, -12138259, -10179489, -1344335, -1142742, -6127120, -2931932, -1131212, -8417365, -13270557}, new int[]{-6464359, -10267323, -13532789, -5413850, -11898828, -13410942, -13215889, -10914461, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{-10465880, -9937588, -14983040, -6736562, -14197445, -13534568, -13144441, -10587280, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{-14213586, -15263198, -16310753, -15724781, -15853551, -16051428, -14868183, -14668758, -15854566, -15326427, -15327979, -14411490, -14345453, -14738135, -14543346, -14212843, -15263205, -15854566}, new int[]{-15659501, -14277074, -15459034, -14542297, -14735336, -15129808, -15591910, -15459810, -15260623, -15853800, -15259879, -14477540, -14674936, -15461604, -13820650, -15067635, -14605528, -15260623}, new int[]{-13951445, -15395557, -15985382, -15855853, -16050417, -15525854, -15260627, -15327189, -15788258, -14799314, -15458796, -13952727, -13754603, -14081231, -14478324, -14081004, -15197667, -15788258}, new int[]{-15330777, -15066858, -15915220, -14213847, -15262439, -15260879, -15657695, -16443625, -15459285, -15589601, -14932454, -14740451, -15002870, -15264997, -13821660, -14805234, -14605784, -15459285}, new int[]{11, 12, 13, 14, 15, 16, 17, 18, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, new String[]{"O-wmAfBPSFADAAAA4zINVfD_bro", "RepJ5uE_SVABAAAAr4d0YhgB850", "dk_wwlghOFACAAAAfz9xrxi6euw", "9LW_RcoOSVACAAAAFTk3DTyXN-M", "PllZ-bf_SFAEAAAA8crRfwZiDNg", "-Xc-np9y2VMCAAAARKr0yNNPYW0", "kO4jyq55SFABAAAA0WEpcLfahXk", "CJNyxPMgSVAEAAAAvW9sMwc51cw", "fqv01SQemVIBAAAApND8LDRUhRU", "RepJ5uE_SVABAAAAr4d0YhgB850", "CJNyxPMgSVAEAAAAvW9sMwc51cw", "9LW_RcoOSVACAAAAFTk3DTyXN-M", "9GcNVISdSVADAAAAUcw5BYjELW4", "F5oWoCs7QFACAAAAgf2bD_mg8Bw", "9ShF73d1MFIIAAAAjWnm8_ZMe8Q", "3rX-PaKbSFACAAAAEiHNvcEm6X4", "dk_wwlghOFACAAAAfz9xrxi6euw", "fqv01SQemVIBAAAApND8LDRUhRU"}, new int[]{225, 45, 225, TsExtractor.TS_STREAM_TYPE_E_AC3, 45, 225, 45, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{40, 40, 31, 50, 25, 34, 35, 35, 38, 29, 24, 34, 34, 31, 29, 37, 21, 38});
        sortAccents(themeInfo3);
        themes.add(themeInfo3);
        HashMap<String, ThemeInfo> hashMap = themesDict;
        currentNightTheme = themeInfo3;
        hashMap.put("Dark Blue", themeInfo3);
        ThemeInfo themeInfo4 = new ThemeInfo();
        themeInfo4.name = "Arctic Blue";
        themeInfo4.assetName = "arctic.attheme";
        themeInfo4.previewBackgroundColor = -1971728;
        themeInfo4.previewInColor = -1;
        themeInfo4.previewOutColor = -9657877;
        themeInfo4.sortIndex = 5;
        themeInfo4.setAccentColorOptions(new int[]{-12537374, -12472227, -3240928, -11033621, -2194124, -3382903, -13332245, -12342073, -11359164, -3317869, -2981834, -8165684, -3256745, -2904512, -8681301}, new int[]{-13525046, -14113959, -7579073, -13597229, -3581840, -8883763, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{-11616542, -9716647, -6400452, -12008744, -2592697, -4297041, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{-3808528, -2433367, -2700891, -1838093, -1120848, -1712148, -2037779, -4202261, -4005713, -1058332, -925763, -1975316, -1189672, -1318451, -2302235}, new int[]{-1510157, -4398164, -1647697, -3610898, -1130838, -1980692, -4270093, -4202261, -3415654, -1259815, -1521765, -4341268, -1127744, -1318219, -3945761}, new int[]{-4924688, -3283031, -1523567, -2494477, -1126510, -595210, -2037517, -3478548, -4661623, -927514, -796762, -2696971, -1188403, -1319735, -1577487}, new int[]{-3149585, -5714021, -1978209, -4925720, -1134713, -1718833, -3613709, -5317397, -3218014, -999207, -2116466, -4343054, -931397, -1583186, -3815718}, new int[]{9, 10, 11, 12, 13, 14, 0, 1, 2, 3, 4, 5, 6, 7, 8}, new String[]{"MIo6r0qGSFAFAAAAtL8TsDzNX60", "dhf9pceaQVACAAAAbzdVo4SCiZA", "fqv01SQemVIBAAAApND8LDRUhRU", "p-pXcflrmFIBAAAAvXYQk-mCwZU", "JqSUrO0-mFIBAAAAWwTvLzoWGQI", "F5oWoCs7QFACAAAAgf2bD_mg8Bw", "fqv01SQemVIBAAAApND8LDRUhRU", "RepJ5uE_SVABAAAAr4d0YhgB850", "PllZ-bf_SFAEAAAA8crRfwZiDNg", "pgJfpFNRSFABAAAACDT8s5sEjfc", "ptuUd96JSFACAAAATobI23sPpz0", "dhf9pceaQVACAAAAbzdVo4SCiZA", "JqSUrO0-mFIBAAAAWwTvLzoWGQI", "9iklpvIPQVABAAAAORQXKur_Eyc", "F5oWoCs7QFACAAAAgf2bD_mg8Bw"}, new int[]{315, 315, 225, 315, 0, 180, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{50, 50, 58, 47, 46, 50, 49, 46, 51, 50, 49, 34, 54, 50, 40});
        sortAccents(themeInfo4);
        themes.add(themeInfo4);
        themesDict.put("Arctic Blue", themeInfo4);
        ThemeInfo themeInfo5 = new ThemeInfo();
        themeInfo5.name = "Day";
        themeInfo5.assetName = "day.attheme";
        themeInfo5.previewBackgroundColor = -1;
        themeInfo5.previewInColor = -1315084;
        themeInfo5.previewOutColor = -8604930;
        themeInfo5.sortIndex = 2;
        themeInfo5.setAccentColorOptions(new int[]{-11099447, -3379581, -3109305, -3382174, -7963438, -11759137, -11029287, -11226775, -2506945, -3382174, -3379581, -6587438, -2649788, -8681301}, new int[]{-10125092, -9671214, -3451775, -3978678, -10711329, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{-12664362, -3642988, -2383569, -3109317, -11422261, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, null, null, new int[]{9, 10, 11, 12, 13, 0, 1, 2, 3, 4, 5, 6, 7, 8}, new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", ""}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        sortAccents(themeInfo5);
        themes.add(themeInfo5);
        themesDict.put("Day", themeInfo5);
        ThemeInfo themeInfo6 = new ThemeInfo();
        themeInfo6.name = "Night";
        themeInfo6.assetName = "night.attheme";
        themeInfo6.previewBackgroundColor = -11315623;
        themeInfo6.previewInColor = -9143676;
        themeInfo6.previewOutColor = -9067802;
        themeInfo6.sortIndex = 4;
        themeInfo6.setAccentColorOptions(new int[]{-9781697, -7505693, -2204034, -10913816, -2375398, -12678921, -11881005, -11880383, -2534026, -1934037, -7115558, -3128522, -1528292, -8812381}, new int[]{-7712108, -4953061, -5288081, -14258547, -9154889, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{-9939525, -5948598, -10335844, -13659747, -14054507, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{-15330532, -14806760, -15791344, -16184308, -16313063, -15921641, -15656164, -15986420, -15856883, -14871025, -16185078, -14937584, -14869736, -15855598}, new int[]{-14673881, -15724781, -15002342, -15458526, -15987697, -16184820, -16118258, -16250616, -15067624, -15527923, -14804447, -15790836, -15987960, -16316665}, new int[]{-15856877, -14608861, -15528430, -15921391, -15722209, -15197144, -15458015, -15591406, -15528431, -15068401, -16053749, -15594229, -15395825, -15724012}, new int[]{-14804694, -15658986, -14609382, -15656421, -16118509, -15855854, -16315381, -16052981, -14544354, -15791092, -15659241, -16316922, -15988214, -16185077}, new int[]{9, 10, 11, 12, 13, 0, 1, 2, 3, 4, 5, 6, 7, 8}, new String[]{"YIxYGEALQVADAAAAA3QbEH0AowY", "9LW_RcoOSVACAAAAFTk3DTyXN-M", "O-wmAfBPSFADAAAA4zINVfD_bro", "F5oWoCs7QFACAAAAgf2bD_mg8Bw", "-Xc-np9y2VMCAAAARKr0yNNPYW0", "fqv01SQemVIBAAAApND8LDRUhRU", "F5oWoCs7QFACAAAAgf2bD_mg8Bw", "ptuUd96JSFACAAAATobI23sPpz0", "p-pXcflrmFIBAAAAvXYQk-mCwZU", "Nl8Pg2rBQVACAAAA25Lxtb8SDp0", "dhf9pceaQVACAAAAbzdVo4SCiZA", "9GcNVISdSVADAAAAUcw5BYjELW4", "9LW_RcoOSVACAAAAFTk3DTyXN-M", "dk_wwlghOFACAAAAfz9xrxi6euw"}, new int[]{45, TsExtractor.TS_STREAM_TYPE_E_AC3, 0, 180, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{34, 47, 52, 48, 54, 50, 37, 56, 48, 49, 40, 64, 38, 48});
        sortAccents(themeInfo6);
        themes.add(themeInfo6);
        themesDict.put("Night", themeInfo6);
        String themesString2 = themeConfig.getString("themes2", null);
        int remoteVersion2 = themeConfig.getInt("remote_version", 0);
        boolean z4 = true;
        if (remoteVersion2 == 1) {
            int a = 0;
            while (a < 4) {
                long[] jArr = remoteThemesHash;
                StringBuilder sb = new StringBuilder();
                sb.append("2remoteThemesHash");
                sb.append(a != 0 ? Integer.valueOf(a) : "");
                jArr[a] = themeConfig.getLong(sb.toString(), 0L);
                int[] iArr = lastLoadingThemesTime;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("lastLoadingThemesTime");
                sb2.append(a != 0 ? Integer.valueOf(a) : "");
                iArr[a] = themeConfig.getInt(sb2.toString(), 0);
                a++;
            }
        }
        themeConfig.edit().putInt("remote_version", 1).apply();
        if (!TextUtils.isEmpty(themesString2)) {
            try {
                JSONArray jsonArray = new JSONArray(themesString2);
                for (int a2 = 0; a2 < jsonArray.length(); a2++) {
                    themeInfo6 = ThemeInfo.createWithJson(jsonArray.getJSONObject(a2));
                    if (themeInfo6 != null) {
                        otherThemes.add(themeInfo6);
                        themes.add(themeInfo6);
                        themesDict.put(themeInfo6.getKey(), themeInfo6);
                        themeInfo6.loadWallpapers(themeConfig);
                    }
                }
            } catch (Exception e4) {
                FileLog.e(e4);
            }
        } else {
            themesString2 = themeConfig.getString("themes", null);
            if (!TextUtils.isEmpty(themesString2)) {
                String[] themesArr = themesString2.split("&");
                for (String str : themesArr) {
                    themeInfo6 = ThemeInfo.createWithString(str);
                    if (themeInfo6 != null) {
                        otherThemes.add(themeInfo6);
                        themes.add(themeInfo6);
                        themesDict.put(themeInfo6.getKey(), themeInfo6);
                    }
                }
                saveOtherThemes(true, true);
                themeConfig.edit().remove("themes").commit();
            }
        }
        sortThemes();
        ThemeInfo applyingTheme = null;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        try {
            ThemeInfo themeDarkBlue3 = themesDict.get("Dark Blue");
            String theme3 = preferences.getString("theme", null);
            try {
                if ("Default".equals(theme3)) {
                    applyingTheme = themesDict.get("Blue");
                    applyingTheme.currentAccentId = DEFALT_THEME_ACCENT_ID;
                } else if ("Dark".equals(theme3)) {
                    applyingTheme = themeDarkBlue3;
                    applyingTheme.currentAccentId = 9;
                } else if (theme3 != null && (applyingTheme = themesDict.get(theme3)) != null && !themeConfig.contains("lastDayTheme")) {
                    SharedPreferences.Editor editor = themeConfig.edit();
                    editor.putString("lastDayTheme", applyingTheme.getKey());
                    editor.commit();
                }
                String theme4 = preferences.getString("nighttheme", null);
                if ("Default".equals(theme4)) {
                    applyingTheme = themesDict.get("Blue");
                    applyingTheme.currentAccentId = DEFALT_THEME_ACCENT_ID;
                } else if ("Dark".equals(theme4)) {
                    currentNightTheme = themeDarkBlue3;
                    themeDarkBlue3.currentAccentId = 9;
                } else if (theme4 != null && (t = themesDict.get(theme4)) != null) {
                    currentNightTheme = t;
                }
                if (currentNightTheme != null && !themeConfig.contains("lastDarkTheme")) {
                    SharedPreferences.Editor editor2 = themeConfig.edit();
                    editor2.putString("lastDarkTheme", currentNightTheme.getKey());
                    editor2.commit();
                }
                SharedPreferences.Editor oldEditorNew = null;
                SharedPreferences.Editor oldEditor2 = null;
                for (ThemeInfo info : themesDict.values()) {
                    if (info.assetName == null || info.accentBaseColor == 0) {
                        themeInfo = themeInfo6;
                        themesString = themesString2;
                        remoteVersion = remoteVersion2;
                        z = z4;
                        themeDarkBlue = themeDarkBlue3;
                        theme = theme4;
                    } else {
                        String accents = themeConfig.getString("accents_" + info.assetName, null);
                        StringBuilder sb3 = new StringBuilder();
                        themeInfo = themeInfo6;
                        try {
                            sb3.append("accent_current_");
                            sb3.append(info.assetName);
                            String sb4 = sb3.toString();
                            if (info.firstAccentIsDefault) {
                                try {
                                    i = DEFALT_THEME_ACCENT_ID;
                                } catch (Exception e5) {
                                    e = e5;
                                    FileLog.e(e);
                                    throw new RuntimeException(e);
                                }
                            } else {
                                i = 0;
                            }
                            info.currentAccentId = themeConfig.getInt(sb4, i);
                            ArrayList<ThemeAccent> newAccents = new ArrayList<>();
                            if (!TextUtils.isEmpty(accents)) {
                                try {
                                    themesString = themesString2;
                                    remoteVersion = remoteVersion2;
                                    try {
                                        SerializedData data = new SerializedData(Base64.decode(accents, 3));
                                        int version2 = data.readInt32(true);
                                        int count = data.readInt32(true);
                                        int a3 = 0;
                                        while (a3 < count) {
                                            try {
                                                ThemeAccent accent = new ThemeAccent();
                                                int count2 = count;
                                                boolean z5 = z4;
                                                try {
                                                    int appRemoteThemesVersion = data.readInt32(true);
                                                    accent.id = appRemoteThemesVersion;
                                                    accent.accentColor = data.readInt32(true);
                                                    int version3 = version2;
                                                    if (version3 >= 9) {
                                                        themeDarkBlue2 = themeDarkBlue3;
                                                        try {
                                                            accent.accentColor2 = data.readInt32(true);
                                                        } catch (Throwable th) {
                                                            e3 = th;
                                                            try {
                                                                throw new RuntimeException(e3);
                                                            } catch (Throwable th2) {
                                                                e2 = th2;
                                                                FileLog.e(e2);
                                                                throw new RuntimeException(e2);
                                                            }
                                                        }
                                                    } else {
                                                        themeDarkBlue2 = themeDarkBlue3;
                                                    }
                                                    try {
                                                        accent.parentTheme = info;
                                                        accent.myMessagesAccentColor = data.readInt32(true);
                                                        accent.myMessagesGradientAccentColor1 = data.readInt32(true);
                                                        if (version3 >= 7) {
                                                            accent.myMessagesGradientAccentColor2 = data.readInt32(true);
                                                            accent.myMessagesGradientAccentColor3 = data.readInt32(true);
                                                        }
                                                        if (version3 >= 8) {
                                                            accent.myMessagesAnimated = data.readBool(true);
                                                        }
                                                        if (version3 >= 3) {
                                                            theme2 = theme4;
                                                            try {
                                                                accent.backgroundOverrideColor = data.readInt64(true);
                                                                z2 = true;
                                                            } catch (Throwable th3) {
                                                                e3 = th3;
                                                                throw new RuntimeException(e3);
                                                            }
                                                        } else {
                                                            theme2 = theme4;
                                                            z2 = true;
                                                            try {
                                                                accent.backgroundOverrideColor = data.readInt32(true);
                                                            } catch (Throwable th4) {
                                                                e3 = th4;
                                                                throw new RuntimeException(e3);
                                                            }
                                                        }
                                                        if (version3 >= 2) {
                                                            accent.backgroundGradientOverrideColor1 = data.readInt64(z2);
                                                            z3 = true;
                                                        } else {
                                                            z3 = true;
                                                            accent.backgroundGradientOverrideColor1 = data.readInt32(true);
                                                        }
                                                        if (version3 >= 6) {
                                                            accent.backgroundGradientOverrideColor2 = data.readInt64(z3);
                                                            accent.backgroundGradientOverrideColor3 = data.readInt64(z3);
                                                        }
                                                        if (version3 >= 1) {
                                                            accent.backgroundRotation = data.readInt32(true);
                                                        }
                                                        if (version3 >= 4) {
                                                            data.readInt64(true);
                                                            version = version3;
                                                            try {
                                                                accent.patternIntensity = (float) data.readDouble(true);
                                                                accent.patternMotion = data.readBool(true);
                                                                if (version >= 5) {
                                                                    accent.patternSlug = data.readString(true);
                                                                }
                                                            } catch (Throwable th5) {
                                                                e3 = th5;
                                                                throw new RuntimeException(e3);
                                                            }
                                                        } else {
                                                            version = version3;
                                                        }
                                                        if (version >= 5 && data.readBool(true)) {
                                                            accent.account = data.readInt32(true);
                                                            accent.info = TLRPC.Theme.TLdeserialize(data, data.readInt32(true), true);
                                                        }
                                                        if (accent.info != null) {
                                                            accent.isDefault = accent.info.isDefault;
                                                        }
                                                        info.themeAccentsMap.put(accent.id, accent);
                                                        if (accent.info != null) {
                                                            info.accentsByThemeId.put(accent.info.id, accent);
                                                        }
                                                        newAccents.add(accent);
                                                        info.lastAccentId = Math.max(info.lastAccentId, accent.id);
                                                        a3++;
                                                        themeDarkBlue3 = themeDarkBlue2;
                                                        count = count2;
                                                        z4 = z5;
                                                        version2 = version;
                                                        theme4 = theme2;
                                                    } catch (Throwable th6) {
                                                        e3 = th6;
                                                    }
                                                } catch (Throwable th7) {
                                                    e3 = th7;
                                                }
                                            } catch (Throwable th8) {
                                                e3 = th8;
                                            }
                                        }
                                        z = z4;
                                        theme = theme4;
                                        themeDarkBlue = themeDarkBlue3;
                                    } catch (Throwable th9) {
                                        e2 = th9;
                                    }
                                } catch (Throwable th10) {
                                    e2 = th10;
                                }
                            } else {
                                try {
                                    themesString = themesString2;
                                    remoteVersion = remoteVersion2;
                                    z = z4;
                                    themeDarkBlue = themeDarkBlue3;
                                    theme = theme4;
                                    String key = "accent_for_" + info.assetName;
                                    int oldAccentColor = preferences.getInt(key, 0);
                                    if (oldAccentColor != 0) {
                                        if (oldEditor2 == null) {
                                            oldEditor2 = preferences.edit();
                                            oldEditorNew = themeConfig.edit();
                                        }
                                        oldEditor2.remove(key);
                                        boolean found = false;
                                        int a4 = 0;
                                        int N = info.themeAccents.size();
                                        while (true) {
                                            if (a4 >= N) {
                                                break;
                                            }
                                            ThemeAccent accent2 = info.themeAccents.get(a4);
                                            if (accent2.accentColor == oldAccentColor) {
                                                info.currentAccentId = accent2.id;
                                                found = true;
                                                break;
                                            }
                                            a4++;
                                        }
                                        if (!found) {
                                            ThemeAccent accent3 = new ThemeAccent();
                                            accent3.id = 100;
                                            accent3.accentColor = oldAccentColor;
                                            accent3.parentTheme = info;
                                            info.themeAccentsMap.put(accent3.id, accent3);
                                            newAccents.add(0, accent3);
                                            info.currentAccentId = 100;
                                            info.lastAccentId = 101;
                                            SerializedData data2 = new SerializedData(72);
                                            data2.writeInt32(9);
                                            data2.writeInt32(1);
                                            data2.writeInt32(accent3.id);
                                            data2.writeInt32(accent3.accentColor);
                                            data2.writeInt32(accent3.myMessagesAccentColor);
                                            data2.writeInt32(accent3.myMessagesGradientAccentColor1);
                                            data2.writeInt32(accent3.myMessagesGradientAccentColor2);
                                            data2.writeInt32(accent3.myMessagesGradientAccentColor3);
                                            data2.writeBool(accent3.myMessagesAnimated);
                                            oldEditor = oldEditor2;
                                            data2.writeInt64(accent3.backgroundOverrideColor);
                                            data2.writeInt64(accent3.backgroundGradientOverrideColor1);
                                            data2.writeInt64(accent3.backgroundGradientOverrideColor2);
                                            data2.writeInt64(accent3.backgroundGradientOverrideColor3);
                                            data2.writeInt32(accent3.backgroundRotation);
                                            data2.writeInt64(0L);
                                            data2.writeDouble(accent3.patternIntensity);
                                            data2.writeBool(accent3.patternMotion);
                                            data2.writeString(accent3.patternSlug);
                                            data2.writeBool(false);
                                            oldEditorNew.putString("accents_" + info.assetName, Base64.encodeToString(data2.toByteArray(), 3));
                                        } else {
                                            oldEditor = oldEditor2;
                                        }
                                        oldEditorNew.putInt("accent_current_" + info.assetName, info.currentAccentId);
                                        oldEditor2 = oldEditor;
                                    }
                                } catch (Exception e6) {
                                    e = e6;
                                    FileLog.e(e);
                                    throw new RuntimeException(e);
                                }
                            }
                            if (!newAccents.isEmpty()) {
                                info.themeAccents.addAll(0, newAccents);
                                sortAccents(info);
                            }
                            if (info.themeAccentsMap != null && info.themeAccentsMap.get(info.currentAccentId) == null) {
                                info.currentAccentId = info.firstAccentIsDefault ? DEFALT_THEME_ACCENT_ID : 0;
                            }
                            info.loadWallpapers(themeConfig);
                            ThemeAccent accent4 = info.getAccent(false);
                            if (accent4 != null) {
                                info.overrideWallpaper = accent4.overrideWallpaper;
                            }
                        } catch (Exception e7) {
                            e = e7;
                        }
                    }
                    themeInfo6 = themeInfo;
                    themesString2 = themesString;
                    remoteVersion2 = remoteVersion;
                    themeDarkBlue3 = themeDarkBlue;
                    z4 = z;
                    theme4 = theme;
                }
                int i2 = 3;
                if (oldEditor2 != null) {
                    oldEditor2.commit();
                    oldEditorNew.commit();
                }
                if (Build.VERSION.SDK_INT < 29) {
                    i2 = 0;
                }
                selectedAutoNightType = preferences.getInt("selectedAutoNightType", i2);
                autoNightScheduleByLocation = preferences.getBoolean("autoNightScheduleByLocation", false);
                autoNightBrighnessThreshold = preferences.getFloat("autoNightBrighnessThreshold", 0.25f);
                autoNightDayStartTime = preferences.getInt("autoNightDayStartTime", 1320);
                autoNightDayEndTime = preferences.getInt("autoNightDayEndTime", 480);
                autoNightSunsetTime = preferences.getInt("autoNightSunsetTime", 1320);
                autoNightSunriseTime = preferences.getInt("autoNightSunriseTime", 480);
                autoNightCityName = preferences.getString("autoNightCityName", "");
                long val = preferences.getLong("autoNightLocationLatitude3", 10000L);
                if (val != 10000) {
                    autoNightLocationLatitude = Double.longBitsToDouble(val);
                } else {
                    autoNightLocationLatitude = 10000.0d;
                }
                long val2 = preferences.getLong("autoNightLocationLongitude3", 10000L);
                if (val2 != 10000) {
                    autoNightLocationLongitude = Double.longBitsToDouble(val2);
                } else {
                    autoNightLocationLongitude = 10000.0d;
                }
                autoNightLastSunCheckDay = preferences.getInt("autoNightLastSunCheckDay", -1);
                if (applyingTheme == null) {
                    applyingTheme = defaultTheme;
                } else {
                    currentDayTheme = applyingTheme;
                }
                if (preferences.contains("overrideThemeWallpaper") || preferences.contains("selectedBackground2")) {
                    boolean override = preferences.getBoolean("overrideThemeWallpaper", false);
                    long id = preferences.getLong("selectedBackground2", 1000001L);
                    if (id == -1 || (override && id != -2 && id != 1000001)) {
                        OverrideWallpaperInfo overrideWallpaper = new OverrideWallpaperInfo();
                        overrideWallpaper.color = preferences.getInt("selectedColor", 0);
                        overrideWallpaper.slug = preferences.getString("selectedBackgroundSlug", "");
                        if (id < -100 || id > -1 || overrideWallpaper.color == 0) {
                            overrideWallpaper.fileName = "wallpaper.jpg";
                            overrideWallpaper.originalFileName = "wallpaper_original.jpg";
                        } else {
                            overrideWallpaper.slug = COLOR_BACKGROUND_SLUG;
                            overrideWallpaper.fileName = "";
                            overrideWallpaper.originalFileName = "";
                        }
                        overrideWallpaper.gradientColor1 = preferences.getInt("selectedGradientColor", 0);
                        overrideWallpaper.gradientColor2 = preferences.getInt("selectedGradientColor2", 0);
                        overrideWallpaper.gradientColor3 = preferences.getInt("selectedGradientColor3", 0);
                        overrideWallpaper.rotation = preferences.getInt("selectedGradientRotation", 45);
                        overrideWallpaper.isBlurred = preferences.getBoolean("selectedBackgroundBlurred", false);
                        overrideWallpaper.isMotion = preferences.getBoolean("selectedBackgroundMotion", false);
                        overrideWallpaper.intensity = preferences.getFloat("selectedIntensity", 0.5f);
                        currentDayTheme.setOverrideWallpaper(overrideWallpaper);
                        if (selectedAutoNightType != 0) {
                            currentNightTheme.setOverrideWallpaper(overrideWallpaper);
                        }
                    }
                    preferences.edit().remove("overrideThemeWallpaper").remove("selectedBackground2").commit();
                }
                int switchToTheme = needSwitchToTheme();
                if (switchToTheme == 2) {
                    applyingTheme = currentNightTheme;
                }
                applyTheme(applyingTheme, false, false, switchToTheme == 2);
                AndroidUtilities.runOnUIThread(MessagesController$$ExternalSyntheticLambda132.INSTANCE);
                ambientSensorListener = new SensorEventListener() { // from class: org.telegram.ui.ActionBar.Theme.9
                    @Override // android.hardware.SensorEventListener
                    public void onSensorChanged(SensorEvent event) {
                        float lux = event.values[0];
                        if (lux <= 0.0f) {
                            lux = 0.1f;
                        }
                        if (ApplicationLoader.mainInterfacePaused || !ApplicationLoader.isScreenOn) {
                            return;
                        }
                        if (lux > Theme.MAXIMUM_LUX_BREAKPOINT) {
                            float unused = Theme.lastBrightnessValue = 1.0f;
                        } else {
                            float unused2 = Theme.lastBrightnessValue = ((float) Math.ceil((Math.log(lux) * 9.932299613952637d) + 27.05900001525879d)) / 100.0f;
                        }
                        if (Theme.lastBrightnessValue > Theme.autoNightBrighnessThreshold) {
                            if (Theme.switchNightRunnableScheduled) {
                                boolean unused3 = Theme.switchNightRunnableScheduled = false;
                                AndroidUtilities.cancelRunOnUIThread(Theme.switchNightBrightnessRunnable);
                            }
                            if (!Theme.switchDayRunnableScheduled) {
                                boolean unused4 = Theme.switchDayRunnableScheduled = true;
                                AndroidUtilities.runOnUIThread(Theme.switchDayBrightnessRunnable, Theme.getAutoNightSwitchThemeDelay());
                            }
                        } else if (!MediaController.getInstance().isRecordingOrListeningByProximity()) {
                            if (Theme.switchDayRunnableScheduled) {
                                boolean unused5 = Theme.switchDayRunnableScheduled = false;
                                AndroidUtilities.cancelRunOnUIThread(Theme.switchDayBrightnessRunnable);
                            }
                            if (!Theme.switchNightRunnableScheduled) {
                                boolean unused6 = Theme.switchNightRunnableScheduled = true;
                                AndroidUtilities.runOnUIThread(Theme.switchNightBrightnessRunnable, Theme.getAutoNightSwitchThemeDelay());
                            }
                        }
                    }

                    @Override // android.hardware.SensorEventListener
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    }
                };
                viewPos = new int[2];
            } catch (Exception e8) {
                e = e8;
            }
        } catch (Exception e9) {
            e = e9;
        }
    }

    public static void sortAccents(ThemeInfo info) {
        Collections.sort(info.themeAccents, Theme$$ExternalSyntheticLambda9.INSTANCE);
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [boolean] */
    /* JADX WARN: Type inference failed for: r3v0, types: [boolean] */
    public static /* synthetic */ int lambda$sortAccents$0(ThemeAccent o1, ThemeAccent o2) {
        if (isHome(o1)) {
            return -1;
        }
        if (isHome(o2)) {
            return 1;
        }
        ?? r0 = o1.isDefault;
        ?? r3 = o2.isDefault;
        if (r0 != r3) {
            return r0 > r3 ? -1 : 1;
        } else if (o1.isDefault) {
            if (o1.id > o2.id) {
                return 1;
            }
            return o1.id < o2.id ? -1 : 0;
        } else if (o1.id > o2.id) {
            return -1;
        } else {
            return o1.id < o2.id ? 1 : 0;
        }
    }

    public static void saveAutoNightThemeConfig() {
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("selectedAutoNightType", selectedAutoNightType);
        editor.putBoolean("autoNightScheduleByLocation", autoNightScheduleByLocation);
        editor.putFloat("autoNightBrighnessThreshold", autoNightBrighnessThreshold);
        editor.putInt("autoNightDayStartTime", autoNightDayStartTime);
        editor.putInt("autoNightDayEndTime", autoNightDayEndTime);
        editor.putInt("autoNightSunriseTime", autoNightSunriseTime);
        editor.putString("autoNightCityName", autoNightCityName);
        editor.putInt("autoNightSunsetTime", autoNightSunsetTime);
        editor.putLong("autoNightLocationLatitude3", Double.doubleToRawLongBits(autoNightLocationLatitude));
        editor.putLong("autoNightLocationLongitude3", Double.doubleToRawLongBits(autoNightLocationLongitude));
        editor.putInt("autoNightLastSunCheckDay", autoNightLastSunCheckDay);
        ThemeInfo themeInfo = currentNightTheme;
        if (themeInfo != null) {
            editor.putString("nighttheme", themeInfo.getKey());
        } else {
            editor.remove("nighttheme");
        }
        editor.commit();
    }

    public static Drawable getStateDrawable(Drawable drawable, int index) {
        if (Build.VERSION.SDK_INT >= 29 && (drawable instanceof StateListDrawable)) {
            return ((StateListDrawable) drawable).getStateDrawable(index);
        }
        if (StateListDrawable_getStateDrawableMethod == null) {
            try {
                StateListDrawable_getStateDrawableMethod = StateListDrawable.class.getDeclaredMethod("getStateDrawable", Integer.TYPE);
            } catch (Throwable th) {
            }
        }
        Method method = StateListDrawable_getStateDrawableMethod;
        if (method == null) {
            return null;
        }
        try {
            return (Drawable) method.invoke(drawable, Integer.valueOf(index));
        } catch (Exception e) {
            return null;
        }
    }

    public static Drawable createEmojiIconSelectorDrawable(Context context, int resource, int defaultColor, int pressedColor) {
        Resources resources = context.getResources();
        Drawable defaultDrawable = resources.getDrawable(resource).mutate();
        if (defaultColor != 0) {
            defaultDrawable.setColorFilter(new PorterDuffColorFilter(defaultColor, PorterDuff.Mode.MULTIPLY));
        }
        Drawable pressedDrawable = resources.getDrawable(resource).mutate();
        if (pressedColor != 0) {
            pressedDrawable.setColorFilter(new PorterDuffColorFilter(pressedColor, PorterDuff.Mode.MULTIPLY));
        }
        StateListDrawable stateListDrawable = new StateListDrawable() { // from class: org.telegram.ui.ActionBar.Theme.3
            @Override // android.graphics.drawable.DrawableContainer
            public boolean selectDrawable(int index) {
                if (Build.VERSION.SDK_INT < 21) {
                    Drawable drawable = Theme.getStateDrawable(this, index);
                    ColorFilter colorFilter = null;
                    if (drawable instanceof BitmapDrawable) {
                        colorFilter = ((BitmapDrawable) drawable).getPaint().getColorFilter();
                    } else if (drawable instanceof NinePatchDrawable) {
                        colorFilter = ((NinePatchDrawable) drawable).getPaint().getColorFilter();
                    }
                    boolean result = super.selectDrawable(index);
                    if (colorFilter != null) {
                        drawable.setColorFilter(colorFilter);
                    }
                    return result;
                }
                return super.selectDrawable(index);
            }
        };
        stateListDrawable.setEnterFadeDuration(1);
        stateListDrawable.setExitFadeDuration(200);
        stateListDrawable.addState(new int[]{16842913}, pressedDrawable);
        stateListDrawable.addState(new int[0], defaultDrawable);
        return stateListDrawable;
    }

    public static Drawable createEditTextDrawable(Context context, boolean alert) {
        return createEditTextDrawable(context, getColor(alert ? key_dialogInputField : key_windowBackgroundWhiteInputField), getColor(alert ? key_dialogInputFieldActivated : key_windowBackgroundWhiteInputFieldActivated));
    }

    public static Drawable createEditTextDrawable(Context context, int color, int colorActivated) {
        Resources resources = context.getResources();
        Drawable defaultDrawable = resources.getDrawable(R.drawable.search_dark).mutate();
        defaultDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        Drawable pressedDrawable = resources.getDrawable(R.drawable.search_dark_activated).mutate();
        pressedDrawable.setColorFilter(new PorterDuffColorFilter(colorActivated, PorterDuff.Mode.MULTIPLY));
        StateListDrawable stateListDrawable = new StateListDrawable() { // from class: org.telegram.ui.ActionBar.Theme.4
            @Override // android.graphics.drawable.DrawableContainer
            public boolean selectDrawable(int index) {
                if (Build.VERSION.SDK_INT < 21) {
                    Drawable drawable = Theme.getStateDrawable(this, index);
                    ColorFilter colorFilter = null;
                    if (drawable instanceof BitmapDrawable) {
                        colorFilter = ((BitmapDrawable) drawable).getPaint().getColorFilter();
                    } else if (drawable instanceof NinePatchDrawable) {
                        colorFilter = ((NinePatchDrawable) drawable).getPaint().getColorFilter();
                    }
                    boolean result = super.selectDrawable(index);
                    if (colorFilter != null) {
                        drawable.setColorFilter(colorFilter);
                    }
                    return result;
                }
                return super.selectDrawable(index);
            }
        };
        stateListDrawable.addState(new int[]{16842910, 16842908}, pressedDrawable);
        stateListDrawable.addState(new int[]{16842908}, pressedDrawable);
        stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable);
        return stateListDrawable;
    }

    public static boolean canStartHolidayAnimation() {
        return canStartHolidayAnimation;
    }

    public static int getEventType() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int monthOfYear = calendar.get(2);
        int dayOfMonth = calendar.get(5);
        calendar.get(12);
        int hour = calendar.get(11);
        if ((monthOfYear == 11 && dayOfMonth >= 24 && dayOfMonth <= 31) || (monthOfYear == 0 && dayOfMonth == 1)) {
            return 0;
        }
        if (monthOfYear == 1 && dayOfMonth == 14) {
            return 1;
        }
        if ((monthOfYear != 9 || dayOfMonth < 30) && (monthOfYear != 10 || dayOfMonth != 1 || hour >= 12)) {
            return -1;
        }
        return 2;
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x0057, code lost:
        if (r2 <= 31) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x005b, code lost:
        if (r2 == 1) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x005d, code lost:
        org.telegram.ui.ActionBar.Theme.dialogs_holidayDrawable = org.telegram.messenger.ApplicationLoader.applicationContext.getResources().getDrawable(org.telegram.messenger.beta.R.drawable.newyear);
        org.telegram.ui.ActionBar.Theme.dialogs_holidayDrawableOffsetX = -org.telegram.messenger.AndroidUtilities.dp(3.0f);
        org.telegram.ui.ActionBar.Theme.dialogs_holidayDrawableOffsetY = -org.telegram.messenger.AndroidUtilities.dp(1.0f);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static android.graphics.drawable.Drawable getCurrentHolidayDrawable() {
        /*
            long r0 = java.lang.System.currentTimeMillis()
            long r2 = org.telegram.ui.ActionBar.Theme.lastHolidayCheckTime
            long r0 = r0 - r2
            r2 = 60000(0xea60, double:2.9644E-319)
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 < 0) goto L7e
            long r0 = java.lang.System.currentTimeMillis()
            org.telegram.ui.ActionBar.Theme.lastHolidayCheckTime = r0
            java.util.Calendar r0 = java.util.Calendar.getInstance()
            long r1 = java.lang.System.currentTimeMillis()
            r0.setTimeInMillis(r1)
            r1 = 2
            int r1 = r0.get(r1)
            r2 = 5
            int r2 = r0.get(r2)
            r3 = 12
            int r3 = r0.get(r3)
            r4 = 11
            int r5 = r0.get(r4)
            r6 = 1
            if (r1 != 0) goto L41
            if (r2 != r6) goto L41
            r7 = 23
            if (r5 > r7) goto L41
            org.telegram.ui.ActionBar.Theme.canStartHolidayAnimation = r6
            goto L44
        L41:
            r7 = 0
            org.telegram.ui.ActionBar.Theme.canStartHolidayAnimation = r7
        L44:
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_holidayDrawable
            if (r7 != 0) goto L7e
            if (r1 != r4) goto L59
            boolean r4 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            r7 = 31
            if (r4 == 0) goto L53
            r4 = 29
            goto L55
        L53:
            r4 = 31
        L55:
            if (r2 < r4) goto L59
            if (r2 <= r7) goto L5d
        L59:
            if (r1 != 0) goto L7e
            if (r2 != r6) goto L7e
        L5d:
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.res.Resources r4 = r4.getResources()
            r6 = 2131165998(0x7f07032e, float:1.7946229E38)
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r6)
            org.telegram.ui.ActionBar.Theme.dialogs_holidayDrawable = r4
            r4 = 1077936128(0x40400000, float:3.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = -r4
            org.telegram.ui.ActionBar.Theme.dialogs_holidayDrawableOffsetX = r4
            r4 = 1065353216(0x3f800000, float:1.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = -r4
            org.telegram.ui.ActionBar.Theme.dialogs_holidayDrawableOffsetY = r4
        L7e:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_holidayDrawable
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.getCurrentHolidayDrawable():android.graphics.drawable.Drawable");
    }

    public static int getCurrentHolidayDrawableXOffset() {
        return dialogs_holidayDrawableOffsetX;
    }

    public static int getCurrentHolidayDrawableYOffset() {
        return dialogs_holidayDrawableOffsetY;
    }

    public static Drawable createSimpleSelectorDrawable(Context context, int resource, int defaultColor, int pressedColor) {
        Resources resources = context.getResources();
        Drawable defaultDrawable = resources.getDrawable(resource).mutate();
        if (defaultColor != 0) {
            defaultDrawable.setColorFilter(new PorterDuffColorFilter(defaultColor, PorterDuff.Mode.MULTIPLY));
        }
        Drawable pressedDrawable = resources.getDrawable(resource).mutate();
        if (pressedColor != 0) {
            pressedDrawable.setColorFilter(new PorterDuffColorFilter(pressedColor, PorterDuff.Mode.MULTIPLY));
        }
        StateListDrawable stateListDrawable = new StateListDrawable() { // from class: org.telegram.ui.ActionBar.Theme.5
            @Override // android.graphics.drawable.DrawableContainer
            public boolean selectDrawable(int index) {
                if (Build.VERSION.SDK_INT < 21) {
                    Drawable drawable = Theme.getStateDrawable(this, index);
                    ColorFilter colorFilter = null;
                    if (drawable instanceof BitmapDrawable) {
                        colorFilter = ((BitmapDrawable) drawable).getPaint().getColorFilter();
                    } else if (drawable instanceof NinePatchDrawable) {
                        colorFilter = ((NinePatchDrawable) drawable).getPaint().getColorFilter();
                    }
                    boolean result = super.selectDrawable(index);
                    if (colorFilter != null) {
                        drawable.setColorFilter(colorFilter);
                    }
                    return result;
                }
                return super.selectDrawable(index);
            }
        };
        stateListDrawable.addState(new int[]{16842919}, pressedDrawable);
        stateListDrawable.addState(new int[]{16842913}, pressedDrawable);
        stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable);
        return stateListDrawable;
    }

    public static ShapeDrawable createCircleDrawable(int size, int color) {
        OvalShape ovalShape = new OvalShape();
        ovalShape.resize(size, size);
        ShapeDrawable defaultDrawable = new ShapeDrawable(ovalShape);
        defaultDrawable.setIntrinsicWidth(size);
        defaultDrawable.setIntrinsicHeight(size);
        defaultDrawable.getPaint().setColor(color);
        return defaultDrawable;
    }

    public static CombinedDrawable createCircleDrawableWithIcon(int size, int iconRes) {
        return createCircleDrawableWithIcon(size, iconRes, 0);
    }

    public static CombinedDrawable createCircleDrawableWithIcon(int size, int iconRes, int stroke) {
        Drawable drawable;
        if (iconRes != 0) {
            drawable = ApplicationLoader.applicationContext.getResources().getDrawable(iconRes).mutate();
        } else {
            drawable = null;
        }
        return createCircleDrawableWithIcon(size, drawable, stroke);
    }

    public static CombinedDrawable createCircleDrawableWithIcon(int size, Drawable drawable, int stroke) {
        OvalShape ovalShape = new OvalShape();
        ovalShape.resize(size, size);
        ShapeDrawable defaultDrawable = new ShapeDrawable(ovalShape);
        Paint paint = defaultDrawable.getPaint();
        paint.setColor(-1);
        if (stroke == 1) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        } else if (stroke == 2) {
            paint.setAlpha(0);
        }
        CombinedDrawable combinedDrawable = new CombinedDrawable(defaultDrawable, drawable);
        combinedDrawable.setCustomSize(size, size);
        return combinedDrawable;
    }

    public static Drawable createRoundRectDrawableWithIcon(int rad, int iconRes) {
        ShapeDrawable defaultDrawable = new ShapeDrawable(new RoundRectShape(new float[]{rad, rad, rad, rad, rad, rad, rad, rad}, null, null));
        defaultDrawable.getPaint().setColor(-1);
        Drawable drawable = ApplicationLoader.applicationContext.getResources().getDrawable(iconRes).mutate();
        return new CombinedDrawable(defaultDrawable, drawable);
    }

    public static int getWallpaperColor(int color) {
        if (color == 0) {
            return 0;
        }
        return (-16777216) | color;
    }

    public static float getThemeIntensity(float value) {
        if (value < 0.0f && !getActiveTheme().isDark()) {
            return -value;
        }
        return value;
    }

    public static void setCombinedDrawableColor(Drawable combinedDrawable, int color, boolean isIcon) {
        Drawable drawable;
        if (!(combinedDrawable instanceof CombinedDrawable)) {
            return;
        }
        if (isIcon) {
            drawable = ((CombinedDrawable) combinedDrawable).getIcon();
        } else {
            drawable = ((CombinedDrawable) combinedDrawable).getBackground();
        }
        if (drawable instanceof ColorDrawable) {
            ((ColorDrawable) drawable).setColor(color);
        } else {
            drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }
    }

    public static Drawable createSimpleSelectorCircleDrawable(int size, int defaultColor, int pressedColor) {
        OvalShape ovalShape = new OvalShape();
        ovalShape.resize(size, size);
        ShapeDrawable defaultDrawable = new ShapeDrawable(ovalShape);
        defaultDrawable.getPaint().setColor(defaultColor);
        ShapeDrawable pressedDrawable = new ShapeDrawable(ovalShape);
        if (Build.VERSION.SDK_INT >= 21) {
            pressedDrawable.getPaint().setColor(-1);
            ColorStateList colorStateList = new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{pressedColor});
            return new RippleDrawable(colorStateList, defaultDrawable, pressedDrawable);
        }
        pressedDrawable.getPaint().setColor(pressedColor);
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, pressedDrawable);
        stateListDrawable.addState(new int[]{16842908}, pressedDrawable);
        stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable);
        return stateListDrawable;
    }

    public static Drawable createRoundRectDrawable(int rad, int defaultColor) {
        ShapeDrawable defaultDrawable = new ShapeDrawable(new RoundRectShape(new float[]{rad, rad, rad, rad, rad, rad, rad, rad}, null, null));
        defaultDrawable.getPaint().setColor(defaultColor);
        return defaultDrawable;
    }

    public static Drawable createRoundRectDrawable(int topRad, int bottomRad, int defaultColor) {
        ShapeDrawable defaultDrawable = new ShapeDrawable(new RoundRectShape(new float[]{topRad, topRad, topRad, topRad, bottomRad, bottomRad, bottomRad, bottomRad}, null, null));
        defaultDrawable.getPaint().setColor(defaultColor);
        return defaultDrawable;
    }

    public static Drawable createServiceDrawable(int rad, View view, View containerView) {
        return createServiceDrawable(rad, view, containerView, chat_actionBackgroundPaint);
    }

    public static Drawable createServiceDrawable(final int rad, final View view, final View containerView, final Paint backgroundPaint) {
        return new Drawable() { // from class: org.telegram.ui.ActionBar.Theme.6
            private RectF rect = new RectF();

            @Override // android.graphics.drawable.Drawable
            public void draw(Canvas canvas) {
                Rect bounds = getBounds();
                this.rect.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
                Theme.applyServiceShaderMatrixForView(view, containerView);
                RectF rectF = this.rect;
                int i = rad;
                canvas.drawRoundRect(rectF, i, i, backgroundPaint);
                if (Theme.hasGradientService()) {
                    RectF rectF2 = this.rect;
                    int i2 = rad;
                    canvas.drawRoundRect(rectF2, i2, i2, Theme.chat_actionBackgroundGradientDarkenPaint);
                }
            }

            @Override // android.graphics.drawable.Drawable
            public void setAlpha(int alpha) {
            }

            @Override // android.graphics.drawable.Drawable
            public void setColorFilter(ColorFilter colorFilter) {
            }

            @Override // android.graphics.drawable.Drawable
            public int getOpacity() {
                return -2;
            }
        };
    }

    public static Drawable createSimpleSelectorRoundRectDrawable(int rad, int defaultColor, int pressedColor) {
        return createSimpleSelectorRoundRectDrawable(rad, defaultColor, pressedColor, pressedColor);
    }

    public static Drawable createSimpleSelectorRoundRectDrawable(int rad, int defaultColor, int pressedColor, int maskColor) {
        ShapeDrawable defaultDrawable = new ShapeDrawable(new RoundRectShape(new float[]{rad, rad, rad, rad, rad, rad, rad, rad}, null, null));
        defaultDrawable.getPaint().setColor(defaultColor);
        ShapeDrawable pressedDrawable = new ShapeDrawable(new RoundRectShape(new float[]{rad, rad, rad, rad, rad, rad, rad, rad}, null, null));
        pressedDrawable.getPaint().setColor(maskColor);
        if (Build.VERSION.SDK_INT >= 21) {
            ColorStateList colorStateList = new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{pressedColor});
            return new RippleDrawable(colorStateList, defaultDrawable, pressedDrawable);
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, pressedDrawable);
        stateListDrawable.addState(new int[]{16842913}, pressedDrawable);
        stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable);
        return stateListDrawable;
    }

    public static Drawable createSelectorDrawableFromDrawables(Drawable normal, Drawable pressed) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, pressed);
        stateListDrawable.addState(new int[]{16842913}, pressed);
        stateListDrawable.addState(StateSet.WILD_CARD, normal);
        return stateListDrawable;
    }

    public static Drawable getRoundRectSelectorDrawable(int color) {
        return getRoundRectSelectorDrawable(AndroidUtilities.dp(3.0f), color);
    }

    public static Drawable getRoundRectSelectorDrawable(int corners, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable maskDrawable = createRoundRectDrawable(corners, -1);
            ColorStateList colorStateList = new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{419430400 | (16777215 & color)});
            return new RippleDrawable(colorStateList, null, maskDrawable);
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, createRoundRectDrawable(corners, (color & ViewCompat.MEASURED_SIZE_MASK) | 419430400));
        stateListDrawable.addState(new int[]{16842913}, createRoundRectDrawable(corners, 419430400 | (16777215 & color)));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0));
        return stateListDrawable;
    }

    public static Drawable createSelectorWithBackgroundDrawable(int backgroundColor, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable maskDrawable = new ColorDrawable(backgroundColor);
            ColorStateList colorStateList = new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{color});
            return new RippleDrawable(colorStateList, new ColorDrawable(backgroundColor), maskDrawable);
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, new ColorDrawable(color));
        stateListDrawable.addState(new int[]{16842913}, new ColorDrawable(color));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(backgroundColor));
        return stateListDrawable;
    }

    public static Drawable getSelectorDrawable(boolean whiteBackground) {
        return getSelectorDrawable(getColor(key_listSelector), whiteBackground);
    }

    public static Drawable getSelectorDrawable(int color, boolean whiteBackground) {
        if (whiteBackground) {
            return getSelectorDrawable(color, key_windowBackgroundWhite);
        }
        return createSelectorDrawable(color, 2);
    }

    public static Drawable getSelectorDrawable(int color, String backgroundColor) {
        if (backgroundColor != null) {
            if (Build.VERSION.SDK_INT >= 21) {
                Drawable maskDrawable = new ColorDrawable(-1);
                ColorStateList colorStateList = new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{color});
                return new RippleDrawable(colorStateList, new ColorDrawable(getColor(backgroundColor)), maskDrawable);
            }
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{16842919}, new ColorDrawable(color));
            stateListDrawable.addState(new int[]{16842913}, new ColorDrawable(color));
            stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(getColor(backgroundColor)));
            return stateListDrawable;
        }
        return createSelectorDrawable(color, 2);
    }

    public static Drawable createSelectorDrawable(int color) {
        return createSelectorDrawable(color, 1, -1);
    }

    public static Drawable createSelectorDrawable(int color, int maskType) {
        return createSelectorDrawable(color, maskType, -1);
    }

    public static Drawable createSelectorDrawable(int color, final int maskType, int radius) {
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable maskDrawable = null;
            if ((maskType == 1 || maskType == 5) && Build.VERSION.SDK_INT >= 23) {
                maskDrawable = null;
            } else if (maskType == 1 || maskType == 3 || maskType == 4 || maskType == 5 || maskType == 6 || maskType == 7) {
                maskPaint.setColor(-1);
                maskDrawable = new Drawable() { // from class: org.telegram.ui.ActionBar.Theme.7
                    RectF rect;

                    @Override // android.graphics.drawable.Drawable
                    public void draw(Canvas canvas) {
                        int rad;
                        Rect bounds = getBounds();
                        int i = maskType;
                        if (i == 7) {
                            if (this.rect == null) {
                                this.rect = new RectF();
                            }
                            this.rect.set(bounds);
                            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Theme.maskPaint);
                            return;
                        }
                        if (i == 1 || i == 6) {
                            rad = AndroidUtilities.dp(20.0f);
                        } else if (i == 3) {
                            rad = Math.max(bounds.width(), bounds.height()) / 2;
                        } else {
                            int rad2 = bounds.left;
                            rad = (int) Math.ceil(Math.sqrt(((rad2 - bounds.centerX()) * (bounds.left - bounds.centerX())) + ((bounds.top - bounds.centerY()) * (bounds.top - bounds.centerY()))));
                        }
                        canvas.drawCircle(bounds.centerX(), bounds.centerY(), rad, Theme.maskPaint);
                    }

                    @Override // android.graphics.drawable.Drawable
                    public void setAlpha(int alpha) {
                    }

                    @Override // android.graphics.drawable.Drawable
                    public void setColorFilter(ColorFilter colorFilter) {
                    }

                    @Override // android.graphics.drawable.Drawable
                    public int getOpacity() {
                        return 0;
                    }
                };
            } else if (maskType == 2) {
                maskDrawable = new ColorDrawable(-1);
            }
            ColorStateList colorStateList = new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{color});
            RippleDrawable rippleDrawable = new RippleDrawable(colorStateList, null, maskDrawable);
            if (Build.VERSION.SDK_INT >= 23) {
                if (maskType == 1) {
                    rippleDrawable.setRadius(radius <= 0 ? AndroidUtilities.dp(20.0f) : radius);
                } else if (maskType == 5) {
                    rippleDrawable.setRadius(-1);
                }
            }
            return rippleDrawable;
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, new ColorDrawable(color));
        stateListDrawable.addState(new int[]{16842913}, new ColorDrawable(color));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0));
        return stateListDrawable;
    }

    public static Drawable createCircleSelectorDrawable(int color, final int leftInset, final int rightInset) {
        if (Build.VERSION.SDK_INT >= 21) {
            maskPaint.setColor(-1);
            Drawable maskDrawable = new Drawable() { // from class: org.telegram.ui.ActionBar.Theme.8
                @Override // android.graphics.drawable.Drawable
                public void draw(Canvas canvas) {
                    Rect bounds = getBounds();
                    int rad = (Math.max(bounds.width(), bounds.height()) / 2) + leftInset + rightInset;
                    canvas.drawCircle((bounds.centerX() - leftInset) + rightInset, bounds.centerY(), rad, Theme.maskPaint);
                }

                @Override // android.graphics.drawable.Drawable
                public void setAlpha(int alpha) {
                }

                @Override // android.graphics.drawable.Drawable
                public void setColorFilter(ColorFilter colorFilter) {
                }

                @Override // android.graphics.drawable.Drawable
                public int getOpacity() {
                    return 0;
                }
            };
            ColorStateList colorStateList = new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{color});
            return new RippleDrawable(colorStateList, null, maskDrawable);
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, new ColorDrawable(color));
        stateListDrawable.addState(new int[]{16842913}, new ColorDrawable(color));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0));
        return stateListDrawable;
    }

    /* loaded from: classes4.dex */
    public static class AdaptiveRipple {
        public static final float RADIUS_OUT_BOUNDS = -2.0f;
        public static final float RADIUS_TO_BOUNDS = -1.0f;
        private static final String defaultBackgroundColorKey = "windowBackgroundWhite";
        private static float[] tempHSV;

        public static Drawable circle() {
            return circle(Theme.getColor("windowBackgroundWhite"), -1.0f);
        }

        public static Drawable circle(String backgroundColorKey) {
            return circle(Theme.getColor(backgroundColorKey), -1.0f);
        }

        public static Drawable circle(String backgroundColorKey, float radius) {
            return circle(Theme.getColor(backgroundColorKey), radius);
        }

        public static Drawable circle(int backgroundColor) {
            return circle(backgroundColor, -1.0f);
        }

        public static Drawable circle(int backgroundColor, float radius) {
            return createCircle(calcRippleColor(backgroundColor), radius);
        }

        public static Drawable filledCircle() {
            return filledCircle((Drawable) null, Theme.getColor("windowBackgroundWhite"), -1.0f);
        }

        public static Drawable filledCircle(Drawable background) {
            return filledCircle(background, Theme.getColor("windowBackgroundWhite"), -1.0f);
        }

        public static Drawable filledCircle(String backgroundColorKey) {
            return filledCircle((Drawable) null, Theme.getColor(backgroundColorKey), -1.0f);
        }

        public static Drawable filledCircle(Drawable background, String backgroundColorKey) {
            return filledCircle(background, Theme.getColor(backgroundColorKey), -1.0f);
        }

        public static Drawable filledCircle(String backgroundColorKey, float radius) {
            return filledCircle((Drawable) null, Theme.getColor(backgroundColorKey), radius);
        }

        public static Drawable filledCircle(Drawable background, String backgroundColorKey, float radius) {
            return filledCircle(background, Theme.getColor(backgroundColorKey), radius);
        }

        public static Drawable filledCircle(int backgroundColor) {
            return filledCircle((Drawable) null, backgroundColor, -1.0f);
        }

        public static Drawable filledCircle(int backgroundColor, float radius) {
            return filledCircle((Drawable) null, backgroundColor, radius);
        }

        public static Drawable filledCircle(Drawable background, int backgroundColor, float radius) {
            return createCircle(background, calcRippleColor(backgroundColor), radius);
        }

        public static Drawable rect() {
            return rect(Theme.getColor("windowBackgroundWhite"));
        }

        public static Drawable rect(String backgroundColorKey) {
            return rect(Theme.getColor(backgroundColorKey));
        }

        public static Drawable rect(String backgroundColorKey, float... radii) {
            return rect(Theme.getColor(backgroundColorKey), radii);
        }

        public static Drawable rect(int backgroundColor) {
            return rect(backgroundColor, 0.0f);
        }

        public static Drawable rect(int backgroundColor, float... radii) {
            return createRect(0, calcRippleColor(backgroundColor), radii);
        }

        public static Drawable filledRect() {
            return filledRect(Theme.getColor("windowBackgroundWhite"), 0.0f);
        }

        public static Drawable filledRect(Drawable background) {
            int backgroundColor = background instanceof ColorDrawable ? ((ColorDrawable) background).getColor() : Theme.getColor("windowBackgroundWhite");
            return filledRect(background, backgroundColor, 0.0f);
        }

        public static Drawable filledRect(String backgroundColorKey) {
            return filledRect(Theme.getColor(backgroundColorKey));
        }

        public static Drawable filledRect(Drawable background, String backgroundColorKey) {
            return filledRect(background, Theme.getColor(backgroundColorKey), new float[0]);
        }

        public static Drawable filledRect(String backgroundColorKey, float... radii) {
            return filledRect(Theme.getColor(backgroundColorKey), radii);
        }

        public static Drawable filledRect(Drawable background, String backgroundColorKey, float... radii) {
            return filledRect(background, Theme.getColor(backgroundColorKey), radii);
        }

        public static Drawable filledRect(int backgroundColor) {
            return createRect(backgroundColor, calcRippleColor(backgroundColor), new float[0]);
        }

        public static Drawable filledRect(int backgroundColor, float... radii) {
            return createRect(backgroundColor, calcRippleColor(backgroundColor), radii);
        }

        public static Drawable filledRect(Drawable background, int backgroundColor, float... radii) {
            return createRect(background, calcRippleColor(backgroundColor), radii);
        }

        private static Drawable createRect(int rippleColor, float... radii) {
            return createRect(0, rippleColor, radii);
        }

        private static Drawable createRect(int backgroundColor, int rippleColor, float... radii) {
            Drawable background = null;
            if (backgroundColor != 0) {
                if (hasNonzeroRadii(radii)) {
                    background = new ShapeDrawable(new RoundRectShape(calcRadii(radii), null, null));
                    ((ShapeDrawable) background).getPaint().setColor(backgroundColor);
                } else {
                    background = new ColorDrawable(backgroundColor);
                }
            }
            return createRect(background, rippleColor, radii);
        }

        private static Drawable createRect(Drawable background, int rippleColor, float... radii) {
            Drawable ripple;
            if (Build.VERSION.SDK_INT >= 21) {
                Drawable maskDrawable = null;
                if (hasNonzeroRadii(radii)) {
                    maskDrawable = new ShapeDrawable(new RoundRectShape(calcRadii(radii), null, null));
                    ((ShapeDrawable) maskDrawable).getPaint().setColor(-1);
                }
                return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{rippleColor}), background, maskDrawable);
            }
            StateListDrawable stateListDrawable = new StateListDrawable();
            if (hasNonzeroRadii(radii)) {
                ripple = new ShapeDrawable(new RoundRectShape(calcRadii(radii), null, null));
                ((ShapeDrawable) ripple).getPaint().setColor(rippleColor);
            } else {
                ripple = new ColorDrawable(rippleColor);
            }
            Drawable pressed = new LayerDrawable(new Drawable[]{background, ripple});
            stateListDrawable.addState(new int[]{16842919}, pressed);
            stateListDrawable.addState(new int[]{16842913}, pressed);
            stateListDrawable.addState(StateSet.WILD_CARD, background);
            return stateListDrawable;
        }

        private static Drawable createCircle(int rippleColor) {
            return createCircle(0, rippleColor, -1.0f);
        }

        private static Drawable createCircle(int rippleColor, float radius) {
            return createCircle(0, rippleColor, radius);
        }

        private static Drawable createCircle(int backgroundColor, int rippleColor, float radius) {
            return createCircle(backgroundColor == 0 ? null : new CircleDrawable(radius, backgroundColor), rippleColor, radius);
        }

        private static Drawable createCircle(Drawable background, int rippleColor, float radius) {
            if (Build.VERSION.SDK_INT >= 21) {
                return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{rippleColor}), background, new CircleDrawable(radius));
            }
            StateListDrawable stateListDrawable = new StateListDrawable();
            Drawable ripple = new CircleDrawable(radius, rippleColor);
            Drawable pressed = new LayerDrawable(new Drawable[]{background, ripple});
            stateListDrawable.addState(new int[]{16842919}, pressed);
            stateListDrawable.addState(new int[]{16842913}, pressed);
            stateListDrawable.addState(StateSet.WILD_CARD, background);
            return stateListDrawable;
        }

        /* loaded from: classes4.dex */
        public static class CircleDrawable extends Drawable {
            private static Paint maskPaint;
            private Paint paint;
            private float radius;

            public CircleDrawable(float radius) {
                this.radius = radius;
                if (maskPaint == null) {
                    Paint paint = new Paint(1);
                    maskPaint = paint;
                    paint.setColor(-1);
                }
                this.paint = maskPaint;
            }

            public CircleDrawable(float radius, int paintColor) {
                this.radius = radius;
                Paint paint = new Paint(1);
                this.paint = paint;
                paint.setColor(paintColor);
            }

            @Override // android.graphics.drawable.Drawable
            public void draw(Canvas canvas) {
                float rad;
                Rect bounds = getBounds();
                if (Math.abs(this.radius - (-1.0f)) < 0.01f) {
                    rad = Math.max(bounds.width(), bounds.height()) / 2;
                } else {
                    float rad2 = this.radius;
                    if (Math.abs(rad2 - (-2.0f)) < 0.01f) {
                        rad = (int) Math.ceil(Math.sqrt(((bounds.left - bounds.centerX()) * (bounds.left - bounds.centerX())) + ((bounds.top - bounds.centerY()) * (bounds.top - bounds.centerY()))));
                    } else {
                        float rad3 = this.radius;
                        rad = AndroidUtilities.dp(rad3);
                    }
                }
                canvas.drawCircle(bounds.centerX(), bounds.centerY(), rad, this.paint);
            }

            @Override // android.graphics.drawable.Drawable
            public void setAlpha(int i) {
            }

            @Override // android.graphics.drawable.Drawable
            public void setColorFilter(ColorFilter colorFilter) {
            }

            @Override // android.graphics.drawable.Drawable
            @Deprecated
            public int getOpacity() {
                return -2;
            }
        }

        private static float[] calcRadii(float... radii) {
            if (radii.length == 0) {
                return new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
            }
            return radii.length == 1 ? new float[]{AndroidUtilities.dp(radii[0]), AndroidUtilities.dp(radii[0]), AndroidUtilities.dp(radii[0]), AndroidUtilities.dp(radii[0]), AndroidUtilities.dp(radii[0]), AndroidUtilities.dp(radii[0]), AndroidUtilities.dp(radii[0]), AndroidUtilities.dp(radii[0])} : radii.length == 2 ? new float[]{AndroidUtilities.dp(radii[0]), AndroidUtilities.dp(radii[0]), AndroidUtilities.dp(radii[0]), AndroidUtilities.dp(radii[0]), AndroidUtilities.dp(radii[1]), AndroidUtilities.dp(radii[1]), AndroidUtilities.dp(radii[1]), AndroidUtilities.dp(radii[1])} : radii.length == 3 ? new float[]{AndroidUtilities.dp(radii[0]), AndroidUtilities.dp(radii[0]), AndroidUtilities.dp(radii[1]), AndroidUtilities.dp(radii[1]), AndroidUtilities.dp(radii[2]), AndroidUtilities.dp(radii[2]), AndroidUtilities.dp(radii[2]), AndroidUtilities.dp(radii[2])} : radii.length < 8 ? new float[]{AndroidUtilities.dp(radii[0]), AndroidUtilities.dp(radii[0]), AndroidUtilities.dp(radii[1]), AndroidUtilities.dp(radii[1]), AndroidUtilities.dp(radii[2]), AndroidUtilities.dp(radii[2]), AndroidUtilities.dp(radii[3]), AndroidUtilities.dp(radii[3])} : new float[]{AndroidUtilities.dp(radii[0]), AndroidUtilities.dp(radii[1]), AndroidUtilities.dp(radii[2]), AndroidUtilities.dp(radii[3]), AndroidUtilities.dp(radii[4]), AndroidUtilities.dp(radii[5]), AndroidUtilities.dp(radii[6]), AndroidUtilities.dp(radii[7])};
        }

        private static boolean hasNonzeroRadii(float... radii) {
            for (int i = 0; i < Math.min(8, radii.length); i++) {
                if (radii[i] > 0.0f) {
                    return true;
                }
            }
            return false;
        }

        private static int calcRippleColor(int backgroundColor) {
            if (tempHSV == null) {
                tempHSV = new float[3];
            }
            Color.colorToHSV(backgroundColor, tempHSV);
            float[] fArr = tempHSV;
            if (fArr[1] > 0.01f) {
                fArr[1] = Math.min(1.0f, Math.max(0.0f, fArr[1] + (Theme.isCurrentThemeDark() ? -0.25f : 0.25f)));
                float[] fArr2 = tempHSV;
                fArr2[2] = Math.min(1.0f, Math.max(0.0f, fArr2[2] + (Theme.isCurrentThemeDark() ? 0.05f : -0.05f)));
            } else {
                fArr[2] = Math.min(1.0f, Math.max(0.0f, fArr[2] + (Theme.isCurrentThemeDark() ? 0.1f : -0.1f)));
            }
            return Color.HSVToColor(127, tempHSV);
        }
    }

    /* loaded from: classes4.dex */
    public static class RippleRadMaskDrawable extends Drawable {
        private float[] radii;
        private Path path = new Path();
        boolean invalidatePath = true;

        public RippleRadMaskDrawable(float top, float bottom) {
            this.radii = r0;
            float dp = AndroidUtilities.dp(top);
            float[] fArr = {dp, dp, dp, dp};
            float[] fArr2 = this.radii;
            float dp2 = AndroidUtilities.dp(bottom);
            fArr2[7] = dp2;
            fArr2[6] = dp2;
            fArr2[5] = dp2;
            fArr2[4] = dp2;
        }

        public RippleRadMaskDrawable(float topLeft, float topRight, float bottomRight, float bottomLeft) {
            float[] fArr = new float[8];
            this.radii = fArr;
            float dp = AndroidUtilities.dp(topLeft);
            fArr[1] = dp;
            fArr[0] = dp;
            float[] fArr2 = this.radii;
            float dp2 = AndroidUtilities.dp(topRight);
            fArr2[3] = dp2;
            fArr2[2] = dp2;
            float[] fArr3 = this.radii;
            float dp3 = AndroidUtilities.dp(bottomRight);
            fArr3[5] = dp3;
            fArr3[4] = dp3;
            float[] fArr4 = this.radii;
            float dp4 = AndroidUtilities.dp(bottomLeft);
            fArr4[7] = dp4;
            fArr4[6] = dp4;
        }

        public void setRadius(float top, float bottom) {
            float[] fArr = this.radii;
            float dp = AndroidUtilities.dp(top);
            fArr[3] = dp;
            fArr[2] = dp;
            fArr[1] = dp;
            fArr[0] = dp;
            float[] fArr2 = this.radii;
            float dp2 = AndroidUtilities.dp(bottom);
            fArr2[7] = dp2;
            fArr2[6] = dp2;
            fArr2[5] = dp2;
            fArr2[4] = dp2;
            this.invalidatePath = true;
            invalidateSelf();
        }

        public void setRadius(float topLeft, float topRight, float bottomRight, float bottomLeft) {
            float[] fArr = this.radii;
            float dp = AndroidUtilities.dp(topLeft);
            fArr[1] = dp;
            fArr[0] = dp;
            float[] fArr2 = this.radii;
            float dp2 = AndroidUtilities.dp(topRight);
            fArr2[3] = dp2;
            fArr2[2] = dp2;
            float[] fArr3 = this.radii;
            float dp3 = AndroidUtilities.dp(bottomRight);
            fArr3[5] = dp3;
            fArr3[4] = dp3;
            float[] fArr4 = this.radii;
            float dp4 = AndroidUtilities.dp(bottomLeft);
            fArr4[7] = dp4;
            fArr4[6] = dp4;
            this.invalidatePath = true;
            invalidateSelf();
        }

        @Override // android.graphics.drawable.Drawable
        protected void onBoundsChange(Rect bounds) {
            this.invalidatePath = true;
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            if (this.invalidatePath) {
                this.invalidatePath = false;
                this.path.reset();
                AndroidUtilities.rectTmp.set(getBounds());
                this.path.addRoundRect(AndroidUtilities.rectTmp, this.radii, Path.Direction.CW);
            }
            canvas.drawPath(this.path, Theme.maskPaint);
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return 0;
        }
    }

    public static void setMaskDrawableRad(Drawable rippleDrawable, int top, int bottom) {
        if (Build.VERSION.SDK_INT >= 21 && (rippleDrawable instanceof RippleDrawable)) {
            RippleDrawable drawable = (RippleDrawable) rippleDrawable;
            int count = drawable.getNumberOfLayers();
            for (int a = 0; a < count; a++) {
                Drawable layer = drawable.getDrawable(a);
                if (layer instanceof RippleRadMaskDrawable) {
                    drawable.setDrawableByLayerId(16908334, new RippleRadMaskDrawable(top, bottom));
                    return;
                }
            }
        }
    }

    public static Drawable createRadSelectorDrawable(int color, int topRad, int bottomRad) {
        if (Build.VERSION.SDK_INT >= 21) {
            maskPaint.setColor(-1);
            Drawable maskDrawable = new RippleRadMaskDrawable(topRad, bottomRad);
            ColorStateList colorStateList = new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{color});
            return new RippleDrawable(colorStateList, null, maskDrawable);
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, new ColorDrawable(color));
        stateListDrawable.addState(new int[]{16842913}, new ColorDrawable(color));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0));
        return stateListDrawable;
    }

    public static Drawable createRadSelectorDrawable(int color, int topLeftRad, int topRightRad, int bottomRightRad, int bottomLeftRad) {
        if (Build.VERSION.SDK_INT >= 21) {
            maskPaint.setColor(-1);
            Drawable maskDrawable = new RippleRadMaskDrawable(topLeftRad, topRightRad, bottomRightRad, bottomLeftRad);
            ColorStateList colorStateList = new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{color});
            return new RippleDrawable(colorStateList, null, maskDrawable);
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, new ColorDrawable(color));
        stateListDrawable.addState(new int[]{16842913}, new ColorDrawable(color));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0));
        return stateListDrawable;
    }

    public static void applyPreviousTheme() {
        ThemeInfo themeInfo;
        ThemeInfo themeInfo2 = previousTheme;
        if (themeInfo2 == null) {
            return;
        }
        hasPreviousTheme = false;
        if (isInNigthMode && (themeInfo = currentNightTheme) != null) {
            applyTheme(themeInfo, true, false, true);
        } else if (!isApplyingAccent) {
            applyTheme(themeInfo2, true, false, false);
        }
        isApplyingAccent = false;
        previousTheme = null;
        checkAutoNightThemeConditions();
    }

    public static void clearPreviousTheme() {
        if (previousTheme == null) {
            return;
        }
        hasPreviousTheme = false;
        isApplyingAccent = false;
        previousTheme = null;
    }

    private static void sortThemes() {
        Collections.sort(themes, Theme$$ExternalSyntheticLambda10.INSTANCE);
    }

    public static /* synthetic */ int lambda$sortThemes$1(ThemeInfo o1, ThemeInfo o2) {
        if (o1.pathToFile == null && o1.assetName == null) {
            return -1;
        }
        if (o2.pathToFile == null && o2.assetName == null) {
            return 1;
        }
        return o1.name.compareTo(o2.name);
    }

    public static void applyThemeTemporary(ThemeInfo themeInfo, boolean accent) {
        previousTheme = getCurrentTheme();
        hasPreviousTheme = true;
        isApplyingAccent = accent;
        applyTheme(themeInfo, false, false, false);
    }

    public static boolean hasCustomWallpaper() {
        return isApplyingAccent && currentTheme.overrideWallpaper != null;
    }

    public static boolean isCustomWallpaperColor() {
        return hasCustomWallpaper() && currentTheme.overrideWallpaper.color != 0;
    }

    public static void resetCustomWallpaper(boolean temporary) {
        if (temporary) {
            isApplyingAccent = false;
            reloadWallpaper();
            return;
        }
        currentTheme.setOverrideWallpaper(null);
    }

    public static ThemeInfo fillThemeValues(File file, String themeName, TLRPC.TL_theme theme) {
        Exception e;
        ThemeInfo themeInfo;
        String[] modes;
        try {
            themeInfo = new ThemeInfo();
        } catch (Exception e2) {
            e = e2;
        }
        try {
            themeInfo.name = themeName;
            try {
                themeInfo.info = theme;
                themeInfo.pathToFile = file.getAbsolutePath();
                themeInfo.account = UserConfig.selectedAccount;
                String[] wallpaperLink = new String[1];
                HashMap<String, Integer> colors = getThemeFileValues(new File(themeInfo.pathToFile), null, wallpaperLink);
                checkIsDark(colors, themeInfo);
                if (!TextUtils.isEmpty(wallpaperLink[0])) {
                    String link = wallpaperLink[0];
                    File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                    themeInfo.pathToWallpaper = new File(filesDirFixed, Utilities.MD5(link) + ".wp").getAbsolutePath();
                    Uri data = Uri.parse(link);
                    themeInfo.slug = data.getQueryParameter("slug");
                    String mode = data.getQueryParameter("mode");
                    if (mode != null && (modes = mode.toLowerCase().split(" ")) != null && modes.length > 0) {
                        for (int a = 0; a < modes.length; a++) {
                            if ("blur".equals(modes[a])) {
                                themeInfo.isBlured = true;
                            } else if ("motion".equals(modes[a])) {
                                themeInfo.isMotion = true;
                            }
                        }
                    }
                    String intensity = data.getQueryParameter("intensity");
                    if (!TextUtils.isEmpty(intensity)) {
                        try {
                            String bgColor = data.getQueryParameter("bg_color");
                            if (!TextUtils.isEmpty(bgColor)) {
                                themeInfo.patternBgColor = Integer.parseInt(bgColor.substring(0, 6), 16) | (-16777216);
                                if (bgColor.length() >= 13 && AndroidUtilities.isValidWallChar(bgColor.charAt(6))) {
                                    themeInfo.patternBgGradientColor1 = Integer.parseInt(bgColor.substring(7, 13), 16) | (-16777216);
                                }
                                if (bgColor.length() >= 20 && AndroidUtilities.isValidWallChar(bgColor.charAt(13))) {
                                    themeInfo.patternBgGradientColor2 = Integer.parseInt(bgColor.substring(14, 20), 16) | (-16777216);
                                }
                                if (bgColor.length() == 27 && AndroidUtilities.isValidWallChar(bgColor.charAt(20))) {
                                    themeInfo.patternBgGradientColor3 = Integer.parseInt(bgColor.substring(21), 16) | (-16777216);
                                }
                            }
                        } catch (Exception e3) {
                        }
                        try {
                            String rotation = data.getQueryParameter("rotation");
                            if (!TextUtils.isEmpty(rotation)) {
                                themeInfo.patternBgGradientRotation = Utilities.parseInt((CharSequence) rotation).intValue();
                            }
                        } catch (Exception e4) {
                        }
                        if (!TextUtils.isEmpty(intensity)) {
                            themeInfo.patternIntensity = Utilities.parseInt((CharSequence) intensity).intValue();
                        }
                        if (themeInfo.patternIntensity == 0) {
                            themeInfo.patternIntensity = 50;
                        }
                    }
                } else {
                    themedWallpaperLink = null;
                }
                return themeInfo;
            } catch (Exception e5) {
                e = e5;
                FileLog.e(e);
                return null;
            }
        } catch (Exception e6) {
            e = e6;
            FileLog.e(e);
            return null;
        }
    }

    public static ThemeInfo applyThemeFile(File file, String themeName, TLRPC.TL_theme theme, boolean temporary) {
        String key;
        File finalFile;
        try {
            if (!themeName.toLowerCase().endsWith(".attheme")) {
                themeName = themeName + ".attheme";
            }
            if (temporary) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.goingToPreviewTheme, new Object[0]);
                ThemeInfo themeInfo = new ThemeInfo();
                themeInfo.name = themeName;
                themeInfo.info = theme;
                themeInfo.pathToFile = file.getAbsolutePath();
                themeInfo.account = UserConfig.selectedAccount;
                applyThemeTemporary(themeInfo, false);
                return themeInfo;
            }
            if (theme != null) {
                key = "remote" + theme.id;
                finalFile = new File(ApplicationLoader.getFilesDirFixed(), key + ".attheme");
            } else {
                key = themeName;
                finalFile = new File(ApplicationLoader.getFilesDirFixed(), key);
            }
            if (!AndroidUtilities.copyFile(file, finalFile)) {
                applyPreviousTheme();
                return null;
            }
            previousTheme = null;
            hasPreviousTheme = false;
            isApplyingAccent = false;
            ThemeInfo themeInfo2 = themesDict.get(key);
            if (themeInfo2 == null) {
                themeInfo2 = new ThemeInfo();
                themeInfo2.name = themeName;
                themeInfo2.account = UserConfig.selectedAccount;
                themes.add(themeInfo2);
                otherThemes.add(themeInfo2);
                sortThemes();
            } else {
                themesDict.remove(key);
            }
            themeInfo2.info = theme;
            themeInfo2.pathToFile = finalFile.getAbsolutePath();
            themesDict.put(themeInfo2.getKey(), themeInfo2);
            saveOtherThemes(true);
            applyTheme(themeInfo2, true, true, false);
            return themeInfo2;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static ThemeInfo getTheme(String key) {
        return themesDict.get(key);
    }

    public static void applyTheme(ThemeInfo themeInfo) {
        applyTheme(themeInfo, true, true, false);
    }

    public static void applyTheme(ThemeInfo themeInfo, boolean nightTheme) {
        applyTheme(themeInfo, true, nightTheme);
    }

    public static void applyTheme(ThemeInfo themeInfo, boolean save, boolean nightTheme) {
        applyTheme(themeInfo, save, true, nightTheme);
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x0040, code lost:
        if (r17 == false) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0042, code lost:
        r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings();
        r7 = r0.edit();
        r7.putString("theme", r16.getKey());
        r7.apply();
     */
    /* JADX WARN: Removed duplicated region for block: B:92:0x01ea A[Catch: Exception -> 0x0201, TryCatch #5 {Exception -> 0x0201, blocks: (B:9:0x0011, B:12:0x0018, B:17:0x0021, B:18:0x002f, B:21:0x0042, B:22:0x0054, B:24:0x005c, B:25:0x0065, B:26:0x0072, B:28:0x007f, B:30:0x0085, B:32:0x008f, B:40:0x00d0, B:86:0x01d7, B:88:0x01de, B:90:0x01e2, B:92:0x01ea, B:93:0x01fb, B:41:0x00d2, B:43:0x00e9, B:45:0x00f6, B:48:0x00fa, B:50:0x00fd, B:52:0x0107, B:53:0x010a, B:55:0x0114, B:56:0x0116, B:59:0x011c, B:60:0x012f, B:62:0x013b, B:64:0x0153, B:66:0x015d, B:67:0x0169, B:69:0x0171, B:71:0x017b, B:72:0x0188, B:74:0x0190, B:76:0x019a, B:78:0x01a9, B:80:0x01b5), top: B:111:0x0011 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static void applyTheme(org.telegram.ui.ActionBar.Theme.ThemeInfo r16, boolean r17, boolean r18, boolean r19) {
        /*
            Method dump skipped, instructions count: 541
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.applyTheme(org.telegram.ui.ActionBar.Theme$ThemeInfo, boolean, boolean, boolean):void");
    }

    public static boolean useBlackText(int color1, int color2) {
        float r1 = Color.red(color1) / 255.0f;
        float r2 = Color.red(color2) / 255.0f;
        float g1 = Color.green(color1) / 255.0f;
        float g2 = Color.green(color2) / 255.0f;
        float b1 = Color.blue(color1) / 255.0f;
        float b2 = Color.blue(color2) / 255.0f;
        float r = (r1 * 0.5f) + (r2 * 0.5f);
        float g = (g1 * 0.5f) + (g2 * 0.5f);
        float b = (b1 * 0.5f) + (0.5f * b2);
        float lightness = (r * 0.2126f) + (g * 0.7152f) + (b * 0.0722f);
        float lightness2 = (0.2126f * r1) + (0.7152f * g1) + (0.0722f * b1);
        return lightness > 0.705f || lightness2 > 0.705f;
    }

    public static void refreshThemeColors() {
        refreshThemeColors(false, false);
    }

    public static void refreshThemeColors(boolean bg, boolean messages) {
        currentColors.clear();
        currentColors.putAll(currentColorsNoAccent);
        shouldDrawGradientIcons = true;
        ThemeAccent accent = currentTheme.getAccent(false);
        if (accent != null) {
            shouldDrawGradientIcons = accent.fillAccentColors(currentColorsNoAccent, currentColors);
        }
        if (!messages) {
            reloadWallpaper();
        }
        applyCommonTheme();
        applyDialogsTheme();
        applyProfileTheme();
        applyChatTheme(false, bg);
        final boolean checkNavigationBarColor = true ^ hasPreviousTheme;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewTheme, false, Boolean.valueOf(checkNavigationBarColor));
            }
        });
    }

    public static int changeColorAccent(ThemeInfo themeInfo, int accent, int color) {
        if (accent == 0 || themeInfo.accentBaseColor == 0 || accent == themeInfo.accentBaseColor || (themeInfo.firstAccentIsDefault && themeInfo.currentAccentId == DEFALT_THEME_ACCENT_ID)) {
            return color;
        }
        float[] hsvTemp3 = getTempHsv(3);
        float[] hsvTemp4 = getTempHsv(4);
        Color.colorToHSV(themeInfo.accentBaseColor, hsvTemp3);
        Color.colorToHSV(accent, hsvTemp4);
        return changeColorAccent(hsvTemp3, hsvTemp4, color, themeInfo.isDark());
    }

    public static float[] getTempHsv(int num) {
        ThreadLocal<float[]> local;
        switch (num) {
            case 1:
                local = hsvTemp1Local;
                break;
            case 2:
                local = hsvTemp2Local;
                break;
            case 3:
                local = hsvTemp3Local;
                break;
            case 4:
                local = hsvTemp4Local;
                break;
            default:
                local = hsvTemp5Local;
                break;
        }
        float[] hsvTemp = local.get();
        if (hsvTemp == null) {
            float[] hsvTemp2 = new float[3];
            local.set(hsvTemp2);
            return hsvTemp2;
        }
        return hsvTemp;
    }

    public static int getAccentColor(float[] baseHsv, int baseColor, int elementColor) {
        float[] hsvTemp3 = getTempHsv(3);
        float[] hsvTemp4 = getTempHsv(4);
        Color.colorToHSV(baseColor, hsvTemp3);
        Color.colorToHSV(elementColor, hsvTemp4);
        float dist = Math.min((hsvTemp3[1] * 1.5f) / baseHsv[1], 1.0f);
        hsvTemp3[0] = (hsvTemp4[0] - hsvTemp3[0]) + baseHsv[0];
        hsvTemp3[1] = (hsvTemp4[1] * baseHsv[1]) / hsvTemp3[1];
        hsvTemp3[2] = ((((hsvTemp4[2] / hsvTemp3[2]) + dist) - 1.0f) * baseHsv[2]) / dist;
        if (hsvTemp3[2] < 0.3f) {
            return elementColor;
        }
        return Color.HSVToColor(255, hsvTemp3);
    }

    public static int changeColorAccent(int color) {
        int i = 0;
        ThemeAccent accent = currentTheme.getAccent(false);
        ThemeInfo themeInfo = currentTheme;
        if (accent != null) {
            i = accent.accentColor;
        }
        return changeColorAccent(themeInfo, i, color);
    }

    private static float abs(float a) {
        return a > 0.0f ? a : -a;
    }

    public static int changeColorAccent(float[] baseHsv, float[] accentHsv, int color, boolean isDarkTheme) {
        if (tmpHSV5 == null) {
            tmpHSV5 = new float[3];
        }
        float[] colorHsv = tmpHSV5;
        Color.colorToHSV(color, colorHsv);
        boolean needRevertBrightness = false;
        float diffH = Math.min(abs(colorHsv[0] - baseHsv[0]), abs((colorHsv[0] - baseHsv[0]) - 360.0f));
        if (diffH > 30.0f) {
            return color;
        }
        float dist = Math.min((colorHsv[1] * 1.5f) / baseHsv[1], 1.0f);
        colorHsv[0] = (colorHsv[0] + accentHsv[0]) - baseHsv[0];
        colorHsv[1] = (colorHsv[1] * accentHsv[1]) / baseHsv[1];
        colorHsv[2] = colorHsv[2] * ((1.0f - dist) + ((accentHsv[2] * dist) / baseHsv[2]));
        int newColor = Color.HSVToColor(Color.alpha(color), colorHsv);
        float origBrightness = AndroidUtilities.computePerceivedBrightness(color);
        float newBrightness = AndroidUtilities.computePerceivedBrightness(newColor);
        if (!isDarkTheme ? origBrightness < newBrightness : origBrightness > newBrightness) {
            needRevertBrightness = true;
        }
        if (needRevertBrightness) {
            float fallbackAmount = (((1.0f - 0.6f) * origBrightness) / newBrightness) + 0.6f;
            return changeBrightness(newColor, fallbackAmount);
        }
        return newColor;
    }

    private static int changeBrightness(int color, float amount) {
        int r = (int) (Color.red(color) * amount);
        int g = (int) (Color.green(color) * amount);
        int b = (int) (Color.blue(color) * amount);
        int b2 = 0;
        int r2 = r < 0 ? 0 : Math.min(r, 255);
        int g2 = g < 0 ? 0 : Math.min(g, 255);
        if (b >= 0) {
            b2 = Math.min(b, 255);
        }
        return Color.argb(Color.alpha(color), r2, g2, b2);
    }

    public static void onUpdateThemeAccents() {
        refreshThemeColors();
    }

    public static boolean deleteThemeAccent(ThemeInfo theme, ThemeAccent accent, boolean save) {
        boolean z = false;
        if (accent == null || theme == null || theme.themeAccents == null) {
            return false;
        }
        boolean current = accent.id == theme.currentAccentId;
        File wallpaperFile = accent.getPathToWallpaper();
        if (wallpaperFile != null) {
            wallpaperFile.delete();
        }
        theme.themeAccentsMap.remove(accent.id);
        theme.themeAccents.remove(accent);
        if (accent.info != null) {
            theme.accentsByThemeId.remove(accent.info.id);
        }
        if (accent.overrideWallpaper != null) {
            accent.overrideWallpaper.delete();
        }
        if (current) {
            ThemeAccent themeAccent = theme.themeAccents.get(0);
            theme.setCurrentAccentId(themeAccent.id);
        }
        if (save) {
            saveThemeAccents(theme, true, false, false, false);
            if (accent.info != null) {
                MessagesController messagesController = MessagesController.getInstance(accent.account);
                if (current && theme == currentNightTheme) {
                    z = true;
                }
                messagesController.saveTheme(theme, accent, z, true);
            }
        }
        return current;
    }

    public static void saveThemeAccents(ThemeInfo theme, boolean save, boolean remove, boolean indexOnly, boolean upload) {
        saveThemeAccents(theme, save, remove, indexOnly, upload, false);
    }

    public static void saveThemeAccents(ThemeInfo theme, boolean save, boolean remove, boolean indexOnly, boolean upload, boolean migration) {
        if (save) {
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
            SharedPreferences.Editor editor = preferences.edit();
            if (!indexOnly) {
                int N = theme.themeAccents.size();
                int count = Math.max(0, N - theme.defaultAccentCount);
                SerializedData data = new SerializedData(((count * 16) + 2) * 4);
                data.writeInt32(9);
                data.writeInt32(count);
                for (int a = 0; a < N; a++) {
                    ThemeAccent accent = theme.themeAccents.get(a);
                    if (accent.id >= 100) {
                        data.writeInt32(accent.id);
                        data.writeInt32(accent.accentColor);
                        data.writeInt32(accent.accentColor2);
                        data.writeInt32(accent.myMessagesAccentColor);
                        data.writeInt32(accent.myMessagesGradientAccentColor1);
                        data.writeInt32(accent.myMessagesGradientAccentColor2);
                        data.writeInt32(accent.myMessagesGradientAccentColor3);
                        data.writeBool(accent.myMessagesAnimated);
                        data.writeInt64(accent.backgroundOverrideColor);
                        data.writeInt64(accent.backgroundGradientOverrideColor1);
                        data.writeInt64(accent.backgroundGradientOverrideColor2);
                        data.writeInt64(accent.backgroundGradientOverrideColor3);
                        data.writeInt32(accent.backgroundRotation);
                        data.writeInt64(0L);
                        data.writeDouble(accent.patternIntensity);
                        data.writeBool(accent.patternMotion);
                        data.writeString(accent.patternSlug);
                        data.writeBool(accent.info != null);
                        if (accent.info != null) {
                            data.writeInt32(accent.account);
                            accent.info.serializeToStream(data);
                        }
                    }
                }
                editor.putString("accents_" + theme.assetName, Base64.encodeToString(data.toByteArray(), 3));
                if (!migration) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.themeAccentListUpdated, new Object[0]);
                }
                if (upload) {
                    MessagesController.getInstance(UserConfig.selectedAccount).saveThemeToServer(theme, theme.getAccent(false));
                }
            }
            editor.putInt("accent_current_" + theme.assetName, theme.currentAccentId);
            editor.commit();
        } else {
            if (theme.prevAccentId != -1) {
                if (remove) {
                    ThemeAccent accent2 = theme.themeAccentsMap.get(theme.currentAccentId);
                    theme.themeAccentsMap.remove(accent2.id);
                    theme.themeAccents.remove(accent2);
                    if (accent2.info != null) {
                        theme.accentsByThemeId.remove(accent2.info.id);
                    }
                }
                theme.currentAccentId = theme.prevAccentId;
                ThemeAccent accent3 = theme.getAccent(false);
                if (accent3 != null) {
                    theme.overrideWallpaper = accent3.overrideWallpaper;
                } else {
                    theme.overrideWallpaper = null;
                }
            }
            if (currentTheme == theme) {
                refreshThemeColors();
            }
        }
        theme.prevAccentId = -1;
    }

    public static void saveOtherThemes(boolean full) {
        saveOtherThemes(full, false);
    }

    private static void saveOtherThemes(boolean full, boolean migration) {
        String key;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
        SharedPreferences.Editor editor = preferences.edit();
        if (full) {
            JSONArray array = new JSONArray();
            for (int a = 0; a < otherThemes.size(); a++) {
                JSONObject jsonObject = otherThemes.get(a).getSaveJson();
                if (jsonObject != null) {
                    array.put(jsonObject);
                }
            }
            editor.putString("themes2", array.toString());
        }
        int a2 = 0;
        while (a2 < 4) {
            StringBuilder sb = new StringBuilder();
            sb.append("2remoteThemesHash");
            Object obj = "";
            sb.append(a2 != 0 ? Integer.valueOf(a2) : obj);
            editor.putLong(sb.toString(), remoteThemesHash[a2]);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("lastLoadingThemesTime");
            if (a2 != 0) {
                obj = Integer.valueOf(a2);
            }
            sb2.append(obj);
            editor.putInt(sb2.toString(), lastLoadingThemesTime[a2]);
            a2++;
        }
        int a3 = lastLoadingCurrentThemeTime;
        editor.putInt("lastLoadingCurrentThemeTime", a3);
        editor.commit();
        if (full) {
            for (int b = 0; b < 5; b++) {
                switch (b) {
                    case 0:
                        key = "Blue";
                        break;
                    case 1:
                        key = "Dark Blue";
                        break;
                    case 2:
                        key = "Arctic Blue";
                        break;
                    case 3:
                        key = "Day";
                        break;
                    default:
                        key = "Night";
                        break;
                }
                ThemeInfo info = themesDict.get(key);
                if (info != null && info.themeAccents != null && !info.themeAccents.isEmpty()) {
                    saveThemeAccents(info, true, false, false, false, migration);
                }
            }
        }
    }

    public static HashMap<String, Integer> getDefaultColors() {
        return defaultColors;
    }

    public static ThemeInfo getPreviousTheme() {
        return previousTheme;
    }

    public static String getCurrentThemeName() {
        String text = currentDayTheme.getName();
        if (text.toLowerCase().endsWith(".attheme")) {
            return text.substring(0, text.lastIndexOf(46));
        }
        return text;
    }

    public static String getCurrentNightThemeName() {
        ThemeInfo themeInfo = currentNightTheme;
        if (themeInfo == null) {
            return "";
        }
        String text = themeInfo.getName();
        if (text.toLowerCase().endsWith(".attheme")) {
            return text.substring(0, text.lastIndexOf(46));
        }
        return text;
    }

    public static ThemeInfo getCurrentTheme() {
        ThemeInfo themeInfo = currentDayTheme;
        return themeInfo != null ? themeInfo : defaultTheme;
    }

    public static ThemeInfo getCurrentNightTheme() {
        return currentNightTheme;
    }

    public static boolean isCurrentThemeNight() {
        return currentTheme == currentNightTheme;
    }

    public static boolean isCurrentThemeDark() {
        return currentTheme.isDark();
    }

    public static ThemeInfo getActiveTheme() {
        return currentTheme;
    }

    public static long getAutoNightSwitchThemeDelay() {
        long newTime = SystemClock.elapsedRealtime();
        return Math.abs(lastThemeSwitchTime - newTime) >= 12000 ? 1800L : 12000L;
    }

    public static void setCurrentNightTheme(ThemeInfo theme) {
        boolean apply = currentTheme == currentNightTheme;
        currentNightTheme = theme;
        if (apply) {
            applyDayNightThemeMaybe(true);
        }
    }

    public static void checkAutoNightThemeConditions() {
        checkAutoNightThemeConditions(false);
    }

    public static void cancelAutoNightThemeCallbacks() {
        if (selectedAutoNightType != 2) {
            if (switchNightRunnableScheduled) {
                switchNightRunnableScheduled = false;
                AndroidUtilities.cancelRunOnUIThread(switchNightBrightnessRunnable);
            }
            if (switchDayRunnableScheduled) {
                switchDayRunnableScheduled = false;
                AndroidUtilities.cancelRunOnUIThread(switchDayBrightnessRunnable);
            }
            if (lightSensorRegistered) {
                lastBrightnessValue = 1.0f;
                sensorManager.unregisterListener(ambientSensorListener, lightSensor);
                lightSensorRegistered = false;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("light sensor unregistered");
                }
            }
        }
    }

    private static int needSwitchToTheme() {
        Sensor sensor;
        SensorEventListener sensorEventListener;
        int day;
        int timeStart;
        int i = selectedAutoNightType;
        if (i == 1) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int time = (calendar.get(11) * 60) + calendar.get(12);
            if (autoNightScheduleByLocation) {
                int day2 = calendar.get(5);
                if (autoNightLastSunCheckDay != day2) {
                    double d = autoNightLocationLatitude;
                    if (d != 10000.0d) {
                        double d2 = autoNightLocationLongitude;
                        if (d2 != 10000.0d) {
                            int[] t = SunDate.calculateSunriseSunset(d, d2);
                            autoNightSunriseTime = t[0];
                            autoNightSunsetTime = t[1];
                            autoNightLastSunCheckDay = day2;
                            saveAutoNightThemeConfig();
                        }
                    }
                }
                timeStart = autoNightSunsetTime;
                day = autoNightSunriseTime;
            } else {
                timeStart = autoNightDayStartTime;
                day = autoNightDayEndTime;
            }
            return timeStart < day ? (timeStart > time || time > day) ? 1 : 2 : ((timeStart > time || time > 1440) && (time < 0 || time > day)) ? 1 : 2;
        }
        if (i == 2) {
            if (lightSensor == null) {
                SensorManager sensorManager2 = (SensorManager) ApplicationLoader.applicationContext.getSystemService("sensor");
                sensorManager = sensorManager2;
                lightSensor = sensorManager2.getDefaultSensor(5);
            }
            if (!lightSensorRegistered && (sensor = lightSensor) != null && (sensorEventListener = ambientSensorListener) != null) {
                sensorManager.registerListener(sensorEventListener, sensor, 500000);
                lightSensorRegistered = true;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("light sensor registered");
                }
            }
            if (lastBrightnessValue <= autoNightBrighnessThreshold) {
                if (!switchNightRunnableScheduled) {
                    return 2;
                }
            } else if (!switchDayRunnableScheduled) {
                return 1;
            }
        } else if (i == 3) {
            Configuration configuration = ApplicationLoader.applicationContext.getResources().getConfiguration();
            int currentNightMode = configuration.uiMode & 48;
            switch (currentNightMode) {
                case 0:
                case 16:
                    return 1;
                case 32:
                    return 2;
            }
        } else if (i == 0) {
            return 1;
        }
        return 0;
    }

    public static void setChangingWallpaper(boolean value) {
        changingWallpaper = value;
        if (!value) {
            checkAutoNightThemeConditions(false);
        }
    }

    public static void checkAutoNightThemeConditions(boolean force) {
        if (previousTheme != null || changingWallpaper) {
            return;
        }
        if (!force && switchNightThemeDelay > 0) {
            long newTime = SystemClock.elapsedRealtime();
            long dt = newTime - lastDelayUpdateTime;
            lastDelayUpdateTime = newTime;
            int i = (int) (switchNightThemeDelay - dt);
            switchNightThemeDelay = i;
            if (i > 0) {
                return;
            }
        }
        boolean z = false;
        if (force) {
            if (switchNightRunnableScheduled) {
                switchNightRunnableScheduled = false;
                AndroidUtilities.cancelRunOnUIThread(switchNightBrightnessRunnable);
            }
            if (switchDayRunnableScheduled) {
                switchDayRunnableScheduled = false;
                AndroidUtilities.cancelRunOnUIThread(switchDayBrightnessRunnable);
            }
        }
        cancelAutoNightThemeCallbacks();
        int switchToTheme = needSwitchToTheme();
        if (switchToTheme != 0) {
            if (switchToTheme == 2) {
                z = true;
            }
            applyDayNightThemeMaybe(z);
        }
        if (force) {
            lastThemeSwitchTime = 0L;
        }
    }

    public static void applyDayNightThemeMaybe(boolean night) {
        if (previousTheme != null) {
            return;
        }
        if (night) {
            if (currentTheme != currentNightTheme) {
                isInNigthMode = true;
                lastThemeSwitchTime = SystemClock.elapsedRealtime();
                switchingNightTheme = true;
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, currentNightTheme, true, null, -1);
                switchingNightTheme = false;
            }
        } else if (currentTheme != currentDayTheme) {
            isInNigthMode = false;
            lastThemeSwitchTime = SystemClock.elapsedRealtime();
            switchingNightTheme = true;
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, currentDayTheme, true, null, -1);
            switchingNightTheme = false;
        }
    }

    public static boolean deleteTheme(ThemeInfo themeInfo) {
        if (themeInfo.pathToFile == null) {
            return false;
        }
        boolean currentThemeDeleted = false;
        if (currentTheme == themeInfo) {
            applyTheme(defaultTheme, true, false, false);
            currentThemeDeleted = true;
        }
        if (themeInfo == currentNightTheme) {
            currentNightTheme = themesDict.get("Dark Blue");
        }
        themeInfo.removeObservers();
        otherThemes.remove(themeInfo);
        themesDict.remove(themeInfo.name);
        if (themeInfo.overrideWallpaper != null) {
            themeInfo.overrideWallpaper.delete();
        }
        themes.remove(themeInfo);
        File file = new File(themeInfo.pathToFile);
        file.delete();
        saveOtherThemes(true);
        return currentThemeDeleted;
    }

    public static ThemeInfo createNewTheme(String name) {
        ThemeInfo newTheme = new ThemeInfo();
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        newTheme.pathToFile = new File(filesDirFixed, "theme" + Utilities.random.nextLong() + ".attheme").getAbsolutePath();
        newTheme.name = name;
        themedWallpaperLink = getWallpaperUrl(currentTheme.overrideWallpaper);
        newTheme.account = UserConfig.selectedAccount;
        saveCurrentTheme(newTheme, true, true, false);
        return newTheme;
    }

    private static String getWallpaperUrl(OverrideWallpaperInfo wallpaperInfo) {
        String color4;
        String color42 = null;
        if (wallpaperInfo == null || TextUtils.isEmpty(wallpaperInfo.slug) || wallpaperInfo.slug.equals(DEFAULT_BACKGROUND_SLUG)) {
            return null;
        }
        StringBuilder modes = new StringBuilder();
        if (wallpaperInfo.isBlurred) {
            modes.append("blur");
        }
        if (wallpaperInfo.isMotion) {
            if (modes.length() > 0) {
                modes.append("+");
            }
            modes.append("motion");
        }
        if (wallpaperInfo.color == 0) {
            color4 = "https://attheme.org?slug=" + wallpaperInfo.slug;
        } else {
            String color = String.format("%02x%02x%02x", Integer.valueOf(((byte) (wallpaperInfo.color >> 16)) & 255), Integer.valueOf(((byte) (wallpaperInfo.color >> 8)) & 255), Byte.valueOf((byte) (wallpaperInfo.color & 255))).toLowerCase();
            String color2 = wallpaperInfo.gradientColor1 != 0 ? String.format("%02x%02x%02x", Integer.valueOf(((byte) (wallpaperInfo.gradientColor1 >> 16)) & 255), Integer.valueOf(((byte) (wallpaperInfo.gradientColor1 >> 8)) & 255), Byte.valueOf((byte) (wallpaperInfo.gradientColor1 & 255))).toLowerCase() : null;
            String color3 = wallpaperInfo.gradientColor2 != 0 ? String.format("%02x%02x%02x", Integer.valueOf(((byte) (wallpaperInfo.gradientColor2 >> 16)) & 255), Integer.valueOf(((byte) (wallpaperInfo.gradientColor2 >> 8)) & 255), Byte.valueOf((byte) (wallpaperInfo.gradientColor2 & 255))).toLowerCase() : null;
            if (wallpaperInfo.gradientColor3 != 0) {
                color42 = String.format("%02x%02x%02x", Integer.valueOf(((byte) (wallpaperInfo.gradientColor3 >> 16)) & 255), Integer.valueOf(((byte) (wallpaperInfo.gradientColor3 >> 8)) & 255), Byte.valueOf((byte) (wallpaperInfo.gradientColor3 & 255))).toLowerCase();
            }
            if (color2 == null || color3 == null) {
                if (color2 != null) {
                    color = (color + "-" + color2) + "&rotation=" + wallpaperInfo.rotation;
                }
            } else if (color42 != null) {
                color = color + "~" + color2 + "~" + color3 + "~" + color42;
            } else {
                color = color + "~" + color2 + "~" + color3;
            }
            color4 = "https://attheme.org?slug=" + wallpaperInfo.slug + "&intensity=" + ((int) (wallpaperInfo.intensity * 100.0f)) + "&bg_color=" + color;
        }
        if (modes.length() > 0) {
            String wallpaperLink = color4 + "&mode=" + modes.toString();
            return wallpaperLink;
        }
        return color4;
    }

    public static void saveCurrentTheme(ThemeInfo themeInfo, boolean finalSave, boolean newTheme, boolean upload) {
        String wallpaperLink;
        OverrideWallpaperInfo wallpaperInfo = themeInfo.overrideWallpaper;
        if (wallpaperInfo != null) {
            wallpaperLink = getWallpaperUrl(wallpaperInfo);
        } else {
            wallpaperLink = themedWallpaperLink;
        }
        Drawable wallpaperToSave = newTheme ? wallpaper : themedWallpaper;
        if (newTheme && wallpaperToSave != null) {
            themedWallpaper = wallpaper;
        }
        ThemeAccent accent = currentTheme.getAccent(false);
        HashMap<String, Integer> colorsMap = (!currentTheme.firstAccentIsDefault || accent.id != DEFALT_THEME_ACCENT_ID) ? currentColors : defaultColors;
        StringBuilder result = new StringBuilder();
        if (colorsMap != defaultColors) {
            int outBubbleColor = accent != null ? accent.myMessagesAccentColor : 0;
            int outBubbleGradient1 = accent != null ? accent.myMessagesGradientAccentColor1 : 0;
            int outBubbleGradient2 = accent != null ? accent.myMessagesGradientAccentColor2 : 0;
            int outBubbleGradient3 = accent != null ? accent.myMessagesGradientAccentColor3 : 0;
            if (outBubbleColor != 0 && outBubbleGradient1 != 0) {
                colorsMap.put(key_chat_outBubble, Integer.valueOf(outBubbleColor));
                colorsMap.put(key_chat_outBubbleGradient1, Integer.valueOf(outBubbleGradient1));
                if (outBubbleGradient2 != 0) {
                    colorsMap.put(key_chat_outBubbleGradient2, Integer.valueOf(outBubbleGradient2));
                    if (outBubbleGradient3 != 0) {
                        colorsMap.put(key_chat_outBubbleGradient3, Integer.valueOf(outBubbleGradient3));
                    }
                }
                colorsMap.put(key_chat_outBubbleGradientAnimated, Integer.valueOf((accent == null || !accent.myMessagesAnimated) ? 0 : 1));
            }
        }
        for (Map.Entry<String, Integer> entry : colorsMap.entrySet()) {
            String key = entry.getKey();
            if ((!(wallpaperToSave instanceof BitmapDrawable) && wallpaperLink == null) || (!key_chat_wallpaper.equals(key) && !key_chat_wallpaper_gradient_to1.equals(key) && !key_chat_wallpaper_gradient_to2.equals(key) && !key_chat_wallpaper_gradient_to3.equals(key))) {
                result.append(key);
                result.append("=");
                result.append(entry.getValue());
                result.append("\n");
            }
        }
        FileOutputStream stream = null;
        try {
            try {
                try {
                    stream = new FileOutputStream(themeInfo.pathToFile);
                    if (result.length() == 0 && !(wallpaperToSave instanceof BitmapDrawable) && TextUtils.isEmpty(wallpaperLink)) {
                        result.append(' ');
                    }
                    stream.write(AndroidUtilities.getStringBytes(result.toString()));
                    if (!TextUtils.isEmpty(wallpaperLink)) {
                        stream.write(AndroidUtilities.getStringBytes("WLS=" + wallpaperLink + "\n"));
                        if (newTheme) {
                            try {
                                Bitmap bitmap = ((BitmapDrawable) wallpaperToSave).getBitmap();
                                File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                                FileOutputStream wallpaperStream = new FileOutputStream(new File(filesDirFixed, Utilities.MD5(wallpaperLink) + ".wp"));
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 87, wallpaperStream);
                                wallpaperStream.close();
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                        }
                    } else if (wallpaperToSave instanceof BitmapDrawable) {
                        Bitmap bitmap2 = ((BitmapDrawable) wallpaperToSave).getBitmap();
                        if (bitmap2 != null) {
                            stream.write(new byte[]{87, 80, 83, 10});
                            bitmap2.compress(Bitmap.CompressFormat.JPEG, 87, stream);
                            stream.write(new byte[]{10, 87, 80, 69, 10});
                        }
                        if (finalSave && !upload) {
                            wallpaper = wallpaperToSave;
                            calcBackgroundColor(wallpaperToSave, 2);
                        }
                    }
                    if (!upload) {
                        if (themesDict.get(themeInfo.getKey()) == null) {
                            themes.add(themeInfo);
                            themesDict.put(themeInfo.getKey(), themeInfo);
                            otherThemes.add(themeInfo);
                            saveOtherThemes(true);
                            sortThemes();
                        }
                        currentTheme = themeInfo;
                        if (themeInfo != currentNightTheme) {
                            currentDayTheme = themeInfo;
                        }
                        if (colorsMap == defaultColors) {
                            currentColorsNoAccent.clear();
                            refreshThemeColors();
                        }
                        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("theme", currentDayTheme.getKey());
                        editor.commit();
                    }
                    stream.close();
                } catch (Exception e2) {
                    FileLog.e(e2);
                    if (stream != null) {
                        stream.close();
                    }
                }
            } catch (Throwable th) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e3) {
                        FileLog.e(e3);
                    }
                }
                throw th;
            }
        } catch (Exception e4) {
            FileLog.e(e4);
        }
        if (finalSave) {
            MessagesController.getInstance(themeInfo.account).saveThemeToServer(themeInfo, themeInfo.getAccent(false));
        }
    }

    public static void checkCurrentRemoteTheme(boolean force) {
        int account;
        final TLRPC.TL_theme info;
        if (loadingCurrentTheme == 0) {
            if (!force && Math.abs((System.currentTimeMillis() / 1000) - lastLoadingCurrentThemeTime) < 3600) {
                return;
            }
            int a = 0;
            while (a < 2) {
                final ThemeInfo themeInfo = a == 0 ? currentDayTheme : currentNightTheme;
                if (themeInfo != null && UserConfig.getInstance(themeInfo.account).isClientActivated()) {
                    final ThemeAccent accent = themeInfo.getAccent(false);
                    if (themeInfo.info != null) {
                        info = themeInfo.info;
                        account = themeInfo.account;
                    } else if (accent != null && accent.info != null) {
                        info = accent.info;
                        account = UserConfig.selectedAccount;
                    }
                    if (info != null && info.document != null) {
                        loadingCurrentTheme++;
                        TLRPC.TL_account_getTheme req = new TLRPC.TL_account_getTheme();
                        req.document_id = info.document.id;
                        req.format = "android";
                        TLRPC.TL_inputTheme inputTheme = new TLRPC.TL_inputTheme();
                        inputTheme.access_hash = info.access_hash;
                        inputTheme.id = info.id;
                        req.theme = inputTheme;
                        ConnectionsManager.getInstance(account).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda2
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda6
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        Theme.lambda$checkCurrentRemoteTheme$3(TLObject.this, r2, r3, r4);
                                    }
                                });
                            }
                        });
                    }
                }
                a++;
            }
        }
    }

    public static /* synthetic */ void lambda$checkCurrentRemoteTheme$3(TLObject response, ThemeAccent accent, ThemeInfo themeInfo, TLRPC.TL_theme info) {
        boolean z = true;
        loadingCurrentTheme--;
        boolean changed = false;
        if (response instanceof TLRPC.TL_theme) {
            TLRPC.TL_theme theme = (TLRPC.TL_theme) response;
            TLRPC.ThemeSettings settings = null;
            if (theme.settings.size() > 0) {
                TLRPC.ThemeSettings settings2 = theme.settings.get(0);
                settings = settings2;
            }
            if (accent != null && settings != null) {
                if (!ThemeInfo.accentEquals(accent, settings)) {
                    File file = accent.getPathToWallpaper();
                    if (file != null) {
                        file.delete();
                    }
                    ThemeInfo.fillAccentValues(accent, settings);
                    ThemeInfo themeInfo2 = currentTheme;
                    if (themeInfo2 == themeInfo && themeInfo2.currentAccentId == accent.id) {
                        refreshThemeColors();
                        createChatResources(ApplicationLoader.applicationContext, false);
                        NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                        int i = NotificationCenter.needSetDayNightTheme;
                        Object[] objArr = new Object[4];
                        ThemeInfo themeInfo3 = currentTheme;
                        objArr[0] = themeInfo3;
                        objArr[1] = Boolean.valueOf(currentNightTheme == themeInfo3);
                        objArr[2] = null;
                        objArr[3] = -1;
                        globalInstance.postNotificationName(i, objArr);
                    }
                    PatternsLoader.createLoader(true);
                    changed = true;
                }
                if (settings.wallpaper == null || settings.wallpaper.settings == null || !settings.wallpaper.settings.motion) {
                    z = false;
                }
                accent.patternMotion = z;
            } else if (theme.document != null && theme.document.id != info.document.id) {
                if (accent != null) {
                    accent.info = theme;
                } else {
                    themeInfo.info = theme;
                    themeInfo.loadThemeDocument();
                }
                changed = true;
            }
        }
        if (loadingCurrentTheme == 0) {
            lastLoadingCurrentThemeTime = (int) (System.currentTimeMillis() / 1000);
            saveOtherThemes(changed);
        }
    }

    public static void loadRemoteThemes(final int currentAccount, boolean force) {
        if (!loadingRemoteThemes[currentAccount]) {
            if ((!force && Math.abs((System.currentTimeMillis() / 1000) - lastLoadingThemesTime[currentAccount]) < 3600) || !UserConfig.getInstance(currentAccount).isClientActivated()) {
                return;
            }
            loadingRemoteThemes[currentAccount] = true;
            TLRPC.TL_account_getThemes req = new TLRPC.TL_account_getThemes();
            req.format = "android";
            if (!MediaDataController.getInstance(currentAccount).defaultEmojiThemes.isEmpty()) {
                req.hash = remoteThemesHash[currentAccount];
            }
            if (BuildVars.LOGS_ENABLED) {
                Log.i("theme", "loading remote themes, hash " + req.hash);
            }
            ConnectionsManager.getInstance(currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda1
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            Theme.lambda$loadRemoteThemes$5(r1, tLObject);
                        }
                    });
                }
            });
        }
    }

    public static /* synthetic */ void lambda$loadRemoteThemes$5(int currentAccount, TLObject response) {
        int N;
        TLRPC.TL_account_themes res;
        int N2;
        TLRPC.TL_account_themes res2;
        boolean added;
        boolean loadPatterns;
        loadingRemoteThemes[currentAccount] = false;
        if (response instanceof TLRPC.TL_account_themes) {
            TLRPC.TL_account_themes res3 = (TLRPC.TL_account_themes) response;
            remoteThemesHash[currentAccount] = res3.hash;
            lastLoadingThemesTime[currentAccount] = (int) (System.currentTimeMillis() / 1000);
            ArrayList<TLRPC.TL_theme> emojiPreviewThemes = new ArrayList<>();
            ArrayList<Object> oldServerThemes = new ArrayList<>();
            int N3 = themes.size();
            for (int a = 0; a < N3; a++) {
                ThemeInfo info = themes.get(a);
                if (info.info != null && info.account == currentAccount) {
                    oldServerThemes.add(info);
                } else if (info.themeAccents != null) {
                    for (int b = 0; b < info.themeAccents.size(); b++) {
                        ThemeAccent accent = info.themeAccents.get(b);
                        if (accent.info != null && accent.account == currentAccount) {
                            oldServerThemes.add(accent);
                        }
                    }
                }
            }
            boolean loadPatterns2 = false;
            boolean added2 = false;
            int a2 = 0;
            int N4 = res3.themes.size();
            while (a2 < N4) {
                TLRPC.TL_theme t = res3.themes.get(a2);
                if (!(t instanceof TLRPC.TL_theme)) {
                    res = res3;
                    N = N4;
                } else {
                    if (t.isDefault) {
                        emojiPreviewThemes.add(t);
                    }
                    if (t.settings == null || t.settings.size() <= 0) {
                        res = res3;
                        N = N4;
                        String key = "remote" + t.id;
                        ThemeInfo info2 = themesDict.get(key);
                        if (info2 == null) {
                            info2 = new ThemeInfo();
                            info2.account = currentAccount;
                            info2.pathToFile = new File(ApplicationLoader.getFilesDirFixed(), key + ".attheme").getAbsolutePath();
                            themes.add(info2);
                            otherThemes.add(info2);
                            added2 = true;
                        } else {
                            oldServerThemes.remove(info2);
                        }
                        info2.name = t.title;
                        info2.info = t;
                        themesDict.put(info2.getKey(), info2);
                    } else {
                        int i = 0;
                        while (i < t.settings.size()) {
                            TLRPC.ThemeSettings settings = t.settings.get(i);
                            if (settings == null) {
                                res2 = res3;
                                N2 = N4;
                            } else {
                                String key2 = getBaseThemeKey(settings);
                                if (key2 == null) {
                                    res2 = res3;
                                    N2 = N4;
                                } else {
                                    ThemeInfo info3 = themesDict.get(key2);
                                    if (info3 == null) {
                                        res2 = res3;
                                        N2 = N4;
                                    } else if (info3.themeAccents != null) {
                                        res2 = res3;
                                        ThemeAccent accent2 = info3.accentsByThemeId.get(t.id);
                                        if (accent2 != null) {
                                            if (ThemeInfo.accentEquals(accent2, settings)) {
                                                N2 = N4;
                                            } else {
                                                File file = accent2.getPathToWallpaper();
                                                if (file != null) {
                                                    file.delete();
                                                }
                                                ThemeInfo.fillAccentValues(accent2, settings);
                                                ThemeInfo themeInfo = currentTheme;
                                                if (themeInfo != info3) {
                                                    loadPatterns = true;
                                                    added = true;
                                                    N2 = N4;
                                                } else if (themeInfo.currentAccentId != accent2.id) {
                                                    loadPatterns = true;
                                                    added = true;
                                                    N2 = N4;
                                                } else {
                                                    refreshThemeColors();
                                                    NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                                                    int i2 = NotificationCenter.needSetDayNightTheme;
                                                    loadPatterns = true;
                                                    added = true;
                                                    Object[] objArr = new Object[4];
                                                    ThemeInfo themeInfo2 = currentTheme;
                                                    objArr[0] = themeInfo2;
                                                    N2 = N4;
                                                    objArr[1] = Boolean.valueOf(currentNightTheme == themeInfo2);
                                                    objArr[2] = null;
                                                    objArr[3] = -1;
                                                    globalInstance.postNotificationName(i2, objArr);
                                                }
                                                loadPatterns2 = loadPatterns;
                                                added2 = added;
                                            }
                                            accent2.patternMotion = (settings.wallpaper == null || settings.wallpaper.settings == null || !settings.wallpaper.settings.motion) ? false : true;
                                            oldServerThemes.remove(accent2);
                                        } else {
                                            N2 = N4;
                                            accent2 = info3.createNewAccent(t, currentAccount, false, i);
                                            if (!TextUtils.isEmpty(accent2.patternSlug)) {
                                                loadPatterns2 = true;
                                            }
                                        }
                                        accent2.isDefault = t.isDefault;
                                    } else {
                                        res2 = res3;
                                        N2 = N4;
                                    }
                                }
                            }
                            i++;
                            res3 = res2;
                            N4 = N2;
                        }
                        res = res3;
                        N = N4;
                    }
                }
                a2++;
                res3 = res;
                N4 = N;
            }
            int N5 = oldServerThemes.size();
            for (int a3 = 0; a3 < N5; a3++) {
                Object object = oldServerThemes.get(a3);
                if (object instanceof ThemeInfo) {
                    ThemeInfo info4 = (ThemeInfo) object;
                    info4.removeObservers();
                    otherThemes.remove(info4);
                    themesDict.remove(info4.name);
                    if (info4.overrideWallpaper != null) {
                        info4.overrideWallpaper.delete();
                    }
                    themes.remove(info4);
                    new File(info4.pathToFile).delete();
                    boolean isNightTheme = false;
                    if (currentDayTheme == info4) {
                        currentDayTheme = defaultTheme;
                    } else if (currentNightTheme == info4) {
                        currentNightTheme = themesDict.get("Dark Blue");
                        isNightTheme = true;
                    }
                    if (currentTheme == info4) {
                        applyTheme(isNightTheme ? currentNightTheme : currentDayTheme, true, false, isNightTheme);
                    }
                } else if (object instanceof ThemeAccent) {
                    ThemeAccent accent3 = (ThemeAccent) object;
                    if (deleteThemeAccent(accent3.parentTheme, accent3, false) && currentTheme == accent3.parentTheme) {
                        refreshThemeColors();
                        NotificationCenter globalInstance2 = NotificationCenter.getGlobalInstance();
                        int i3 = NotificationCenter.needSetDayNightTheme;
                        Object[] objArr2 = new Object[4];
                        ThemeInfo themeInfo3 = currentTheme;
                        objArr2[0] = themeInfo3;
                        objArr2[1] = Boolean.valueOf(currentNightTheme == themeInfo3);
                        objArr2[2] = null;
                        objArr2[3] = -1;
                        globalInstance2.postNotificationName(i3, objArr2);
                    }
                }
            }
            saveOtherThemes(true);
            sortThemes();
            if (added2) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.themeListUpdated, new Object[0]);
            }
            if (loadPatterns2) {
                PatternsLoader.createLoader(true);
            }
            MediaDataController.getInstance(currentAccount).generateEmojiPreviewThemes(emojiPreviewThemes, currentAccount);
        }
    }

    public static String getBaseThemeKey(TLRPC.ThemeSettings settings) {
        if (settings.base_theme instanceof TLRPC.TL_baseThemeClassic) {
            return "Blue";
        }
        if (settings.base_theme instanceof TLRPC.TL_baseThemeDay) {
            return "Day";
        }
        if (settings.base_theme instanceof TLRPC.TL_baseThemeTinted) {
            return "Dark Blue";
        }
        if (settings.base_theme instanceof TLRPC.TL_baseThemeArctic) {
            return "Arctic Blue";
        }
        if (settings.base_theme instanceof TLRPC.TL_baseThemeNight) {
            return "Night";
        }
        return null;
    }

    public static TLRPC.BaseTheme getBaseThemeByKey(String key) {
        if ("Blue".equals(key)) {
            return new TLRPC.TL_baseThemeClassic();
        }
        if ("Day".equals(key)) {
            return new TLRPC.TL_baseThemeDay();
        }
        if ("Dark Blue".equals(key)) {
            return new TLRPC.TL_baseThemeTinted();
        }
        if ("Arctic Blue".equals(key)) {
            return new TLRPC.TL_baseThemeArctic();
        }
        if ("Night".equals(key)) {
            return new TLRPC.TL_baseThemeNight();
        }
        return null;
    }

    public static void setThemeFileReference(TLRPC.TL_theme info) {
        int N = themes.size();
        for (int a = 0; a < N; a++) {
            ThemeInfo themeInfo = themes.get(a);
            if (themeInfo.info != null && themeInfo.info.id == info.id) {
                if (themeInfo.info.document != null && info.document != null) {
                    themeInfo.info.document.file_reference = info.document.file_reference;
                    saveOtherThemes(true);
                    return;
                } else {
                    return;
                }
            }
        }
    }

    public static boolean isThemeInstalled(ThemeInfo themeInfo) {
        return (themeInfo == null || themesDict.get(themeInfo.getKey()) == null) ? false : true;
    }

    public static void setThemeUploadInfo(ThemeInfo theme, ThemeAccent accent, TLRPC.TL_theme info, int account, boolean update) {
        String key;
        if (info == null) {
            return;
        }
        TLRPC.ThemeSettings settings = null;
        if (info.settings.size() > 0) {
            TLRPC.ThemeSettings settings2 = info.settings.get(0);
            settings = settings2;
        }
        if (settings != null) {
            if (theme == null) {
                String key2 = getBaseThemeKey(settings);
                if (key2 == null || (theme = themesDict.get(key2)) == null) {
                    return;
                }
                ThemeAccent accent2 = theme.accentsByThemeId.get(info.id);
                accent = accent2;
            }
            if (accent == null) {
                return;
            }
            if (accent.info != null) {
                theme.accentsByThemeId.remove(accent.info.id);
            }
            accent.info = info;
            accent.account = account;
            theme.accentsByThemeId.put(info.id, accent);
            if (!ThemeInfo.accentEquals(accent, settings)) {
                File file = accent.getPathToWallpaper();
                if (file != null) {
                    file.delete();
                }
                ThemeInfo.fillAccentValues(accent, settings);
                ThemeInfo themeInfo = currentTheme;
                if (themeInfo == theme && themeInfo.currentAccentId == accent.id) {
                    refreshThemeColors();
                    NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                    int i = NotificationCenter.needSetDayNightTheme;
                    Object[] objArr = new Object[4];
                    ThemeInfo themeInfo2 = currentTheme;
                    objArr[0] = themeInfo2;
                    objArr[1] = Boolean.valueOf(currentNightTheme == themeInfo2);
                    objArr[2] = null;
                    objArr[3] = -1;
                    globalInstance.postNotificationName(i, objArr);
                }
                PatternsLoader.createLoader(true);
            }
            accent.patternMotion = (settings.wallpaper == null || settings.wallpaper.settings == null || !settings.wallpaper.settings.motion) ? false : true;
            theme.previewParsed = false;
        } else {
            if (theme != null) {
                HashMap<String, ThemeInfo> hashMap = themesDict;
                String key3 = theme.getKey();
                key = key3;
                hashMap.remove(key3);
            } else {
                String str = "remote" + info.id;
                key = str;
                theme = themesDict.get(str);
            }
            if (theme == null) {
                return;
            }
            theme.info = info;
            theme.name = info.title;
            File oldPath = new File(theme.pathToFile);
            File newPath = new File(ApplicationLoader.getFilesDirFixed(), key + ".attheme");
            if (!oldPath.equals(newPath)) {
                try {
                    AndroidUtilities.copyFile(oldPath, newPath);
                    theme.pathToFile = newPath.getAbsolutePath();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            if (update) {
                theme.loadThemeDocument();
            } else {
                theme.previewParsed = false;
            }
            themesDict.put(theme.getKey(), theme);
        }
        saveOtherThemes(true);
    }

    public static File getAssetFile(String assetName) {
        long size;
        File file = new File(ApplicationLoader.getFilesDirFixed(), assetName);
        try {
            InputStream stream = ApplicationLoader.applicationContext.getAssets().open(assetName);
            size = stream.available();
            stream.close();
        } catch (Exception e) {
            size = 0;
            FileLog.e(e);
        }
        if (!file.exists() || (size != 0 && file.length() != size)) {
            try {
                InputStream in = ApplicationLoader.applicationContext.getAssets().open(assetName);
                AndroidUtilities.copyFile(in, file);
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        return file;
    }

    public static int getPreviewColor(HashMap<String, Integer> colors, String key) {
        Integer color = colors.get(key);
        if (color == null) {
            color = defaultColors.get(key);
        }
        return color.intValue();
    }

    /* JADX WARN: Removed duplicated region for block: B:122:0x0239  */
    /* JADX WARN: Removed duplicated region for block: B:125:0x02a3 A[Catch: all -> 0x07bf, TryCatch #22 {all -> 0x07bf, blocks: (B:3:0x0008, B:5:0x0017, B:6:0x001c, B:8:0x009e, B:12:0x00a8, B:17:0x00b4, B:26:0x00c8, B:30:0x00d2, B:35:0x00e1, B:43:0x00f4, B:47:0x00fe, B:52:0x010d, B:60:0x0120, B:64:0x012a, B:69:0x0139, B:76:0x014a, B:79:0x0155, B:81:0x0163, B:84:0x016f, B:86:0x0182, B:88:0x018c, B:90:0x0196, B:92:0x01a5, B:94:0x01aa, B:96:0x01b2, B:98:0x01be, B:100:0x01d0, B:102:0x01d5, B:104:0x01dd, B:106:0x01e9, B:121:0x0234, B:123:0x023f, B:125:0x02a3, B:129:0x02ad, B:133:0x02c1, B:134:0x02ce, B:162:0x03cf, B:166:0x03de, B:168:0x03e8, B:169:0x03fc, B:171:0x0406, B:174:0x0411, B:175:0x0415, B:176:0x0437, B:178:0x0455, B:180:0x045b, B:232:0x059f, B:235:0x05a6, B:247:0x05d2, B:250:0x05d9, B:254:0x05f8, B:256:0x061d, B:258:0x064c, B:262:0x0670, B:263:0x0694, B:265:0x072f, B:268:0x0754, B:269:0x077e, B:271:0x07a8, B:245:0x05cd), top: B:313:0x0008, inners: #6, #16, #18 }] */
    /* JADX WARN: Removed duplicated region for block: B:149:0x0329  */
    /* JADX WARN: Removed duplicated region for block: B:155:0x03b5  */
    /* JADX WARN: Removed duplicated region for block: B:163:0x03d8  */
    /* JADX WARN: Removed duplicated region for block: B:254:0x05f8 A[Catch: all -> 0x07bf, TryCatch #22 {all -> 0x07bf, blocks: (B:3:0x0008, B:5:0x0017, B:6:0x001c, B:8:0x009e, B:12:0x00a8, B:17:0x00b4, B:26:0x00c8, B:30:0x00d2, B:35:0x00e1, B:43:0x00f4, B:47:0x00fe, B:52:0x010d, B:60:0x0120, B:64:0x012a, B:69:0x0139, B:76:0x014a, B:79:0x0155, B:81:0x0163, B:84:0x016f, B:86:0x0182, B:88:0x018c, B:90:0x0196, B:92:0x01a5, B:94:0x01aa, B:96:0x01b2, B:98:0x01be, B:100:0x01d0, B:102:0x01d5, B:104:0x01dd, B:106:0x01e9, B:121:0x0234, B:123:0x023f, B:125:0x02a3, B:129:0x02ad, B:133:0x02c1, B:134:0x02ce, B:162:0x03cf, B:166:0x03de, B:168:0x03e8, B:169:0x03fc, B:171:0x0406, B:174:0x0411, B:175:0x0415, B:176:0x0437, B:178:0x0455, B:180:0x045b, B:232:0x059f, B:235:0x05a6, B:247:0x05d2, B:250:0x05d9, B:254:0x05f8, B:256:0x061d, B:258:0x064c, B:262:0x0670, B:263:0x0694, B:265:0x072f, B:268:0x0754, B:269:0x077e, B:271:0x07a8, B:245:0x05cd), top: B:313:0x0008, inners: #6, #16, #18 }] */
    /* JADX WARN: Removed duplicated region for block: B:255:0x061a  */
    /* JADX WARN: Removed duplicated region for block: B:258:0x064c A[Catch: all -> 0x07bf, TryCatch #22 {all -> 0x07bf, blocks: (B:3:0x0008, B:5:0x0017, B:6:0x001c, B:8:0x009e, B:12:0x00a8, B:17:0x00b4, B:26:0x00c8, B:30:0x00d2, B:35:0x00e1, B:43:0x00f4, B:47:0x00fe, B:52:0x010d, B:60:0x0120, B:64:0x012a, B:69:0x0139, B:76:0x014a, B:79:0x0155, B:81:0x0163, B:84:0x016f, B:86:0x0182, B:88:0x018c, B:90:0x0196, B:92:0x01a5, B:94:0x01aa, B:96:0x01b2, B:98:0x01be, B:100:0x01d0, B:102:0x01d5, B:104:0x01dd, B:106:0x01e9, B:121:0x0234, B:123:0x023f, B:125:0x02a3, B:129:0x02ad, B:133:0x02c1, B:134:0x02ce, B:162:0x03cf, B:166:0x03de, B:168:0x03e8, B:169:0x03fc, B:171:0x0406, B:174:0x0411, B:175:0x0415, B:176:0x0437, B:178:0x0455, B:180:0x045b, B:232:0x059f, B:235:0x05a6, B:247:0x05d2, B:250:0x05d9, B:254:0x05f8, B:256:0x061d, B:258:0x064c, B:262:0x0670, B:263:0x0694, B:265:0x072f, B:268:0x0754, B:269:0x077e, B:271:0x07a8, B:245:0x05cd), top: B:313:0x0008, inners: #6, #16, #18 }] */
    /* JADX WARN: Removed duplicated region for block: B:259:0x066a  */
    /* JADX WARN: Removed duplicated region for block: B:262:0x0670 A[Catch: all -> 0x07bf, TryCatch #22 {all -> 0x07bf, blocks: (B:3:0x0008, B:5:0x0017, B:6:0x001c, B:8:0x009e, B:12:0x00a8, B:17:0x00b4, B:26:0x00c8, B:30:0x00d2, B:35:0x00e1, B:43:0x00f4, B:47:0x00fe, B:52:0x010d, B:60:0x0120, B:64:0x012a, B:69:0x0139, B:76:0x014a, B:79:0x0155, B:81:0x0163, B:84:0x016f, B:86:0x0182, B:88:0x018c, B:90:0x0196, B:92:0x01a5, B:94:0x01aa, B:96:0x01b2, B:98:0x01be, B:100:0x01d0, B:102:0x01d5, B:104:0x01dd, B:106:0x01e9, B:121:0x0234, B:123:0x023f, B:125:0x02a3, B:129:0x02ad, B:133:0x02c1, B:134:0x02ce, B:162:0x03cf, B:166:0x03de, B:168:0x03e8, B:169:0x03fc, B:171:0x0406, B:174:0x0411, B:175:0x0415, B:176:0x0437, B:178:0x0455, B:180:0x045b, B:232:0x059f, B:235:0x05a6, B:247:0x05d2, B:250:0x05d9, B:254:0x05f8, B:256:0x061d, B:258:0x064c, B:262:0x0670, B:263:0x0694, B:265:0x072f, B:268:0x0754, B:269:0x077e, B:271:0x07a8, B:245:0x05cd), top: B:313:0x0008, inners: #6, #16, #18 }] */
    /* JADX WARN: Removed duplicated region for block: B:265:0x072f A[Catch: all -> 0x07bf, TryCatch #22 {all -> 0x07bf, blocks: (B:3:0x0008, B:5:0x0017, B:6:0x001c, B:8:0x009e, B:12:0x00a8, B:17:0x00b4, B:26:0x00c8, B:30:0x00d2, B:35:0x00e1, B:43:0x00f4, B:47:0x00fe, B:52:0x010d, B:60:0x0120, B:64:0x012a, B:69:0x0139, B:76:0x014a, B:79:0x0155, B:81:0x0163, B:84:0x016f, B:86:0x0182, B:88:0x018c, B:90:0x0196, B:92:0x01a5, B:94:0x01aa, B:96:0x01b2, B:98:0x01be, B:100:0x01d0, B:102:0x01d5, B:104:0x01dd, B:106:0x01e9, B:121:0x0234, B:123:0x023f, B:125:0x02a3, B:129:0x02ad, B:133:0x02c1, B:134:0x02ce, B:162:0x03cf, B:166:0x03de, B:168:0x03e8, B:169:0x03fc, B:171:0x0406, B:174:0x0411, B:175:0x0415, B:176:0x0437, B:178:0x0455, B:180:0x045b, B:232:0x059f, B:235:0x05a6, B:247:0x05d2, B:250:0x05d9, B:254:0x05f8, B:256:0x061d, B:258:0x064c, B:262:0x0670, B:263:0x0694, B:265:0x072f, B:268:0x0754, B:269:0x077e, B:271:0x07a8, B:245:0x05cd), top: B:313:0x0008, inners: #6, #16, #18 }] */
    /* JADX WARN: Removed duplicated region for block: B:268:0x0754 A[Catch: all -> 0x07bf, TryCatch #22 {all -> 0x07bf, blocks: (B:3:0x0008, B:5:0x0017, B:6:0x001c, B:8:0x009e, B:12:0x00a8, B:17:0x00b4, B:26:0x00c8, B:30:0x00d2, B:35:0x00e1, B:43:0x00f4, B:47:0x00fe, B:52:0x010d, B:60:0x0120, B:64:0x012a, B:69:0x0139, B:76:0x014a, B:79:0x0155, B:81:0x0163, B:84:0x016f, B:86:0x0182, B:88:0x018c, B:90:0x0196, B:92:0x01a5, B:94:0x01aa, B:96:0x01b2, B:98:0x01be, B:100:0x01d0, B:102:0x01d5, B:104:0x01dd, B:106:0x01e9, B:121:0x0234, B:123:0x023f, B:125:0x02a3, B:129:0x02ad, B:133:0x02c1, B:134:0x02ce, B:162:0x03cf, B:166:0x03de, B:168:0x03e8, B:169:0x03fc, B:171:0x0406, B:174:0x0411, B:175:0x0415, B:176:0x0437, B:178:0x0455, B:180:0x045b, B:232:0x059f, B:235:0x05a6, B:247:0x05d2, B:250:0x05d9, B:254:0x05f8, B:256:0x061d, B:258:0x064c, B:262:0x0670, B:263:0x0694, B:265:0x072f, B:268:0x0754, B:269:0x077e, B:271:0x07a8, B:245:0x05cd), top: B:313:0x0008, inners: #6, #16, #18 }] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x00c8 A[Catch: all -> 0x07bf, TryCatch #22 {all -> 0x07bf, blocks: (B:3:0x0008, B:5:0x0017, B:6:0x001c, B:8:0x009e, B:12:0x00a8, B:17:0x00b4, B:26:0x00c8, B:30:0x00d2, B:35:0x00e1, B:43:0x00f4, B:47:0x00fe, B:52:0x010d, B:60:0x0120, B:64:0x012a, B:69:0x0139, B:76:0x014a, B:79:0x0155, B:81:0x0163, B:84:0x016f, B:86:0x0182, B:88:0x018c, B:90:0x0196, B:92:0x01a5, B:94:0x01aa, B:96:0x01b2, B:98:0x01be, B:100:0x01d0, B:102:0x01d5, B:104:0x01dd, B:106:0x01e9, B:121:0x0234, B:123:0x023f, B:125:0x02a3, B:129:0x02ad, B:133:0x02c1, B:134:0x02ce, B:162:0x03cf, B:166:0x03de, B:168:0x03e8, B:169:0x03fc, B:171:0x0406, B:174:0x0411, B:175:0x0415, B:176:0x0437, B:178:0x0455, B:180:0x045b, B:232:0x059f, B:235:0x05a6, B:247:0x05d2, B:250:0x05d9, B:254:0x05f8, B:256:0x061d, B:258:0x064c, B:262:0x0670, B:263:0x0694, B:265:0x072f, B:268:0x0754, B:269:0x077e, B:271:0x07a8, B:245:0x05cd), top: B:313:0x0008, inners: #6, #16, #18 }] */
    /* JADX WARN: Removed duplicated region for block: B:278:0x0155 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:27:0x00cd  */
    /* JADX WARN: Removed duplicated region for block: B:288:0x05d2 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:290:0x02e1 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:307:0x059f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:30:0x00d2 A[Catch: all -> 0x07bf, TryCatch #22 {all -> 0x07bf, blocks: (B:3:0x0008, B:5:0x0017, B:6:0x001c, B:8:0x009e, B:12:0x00a8, B:17:0x00b4, B:26:0x00c8, B:30:0x00d2, B:35:0x00e1, B:43:0x00f4, B:47:0x00fe, B:52:0x010d, B:60:0x0120, B:64:0x012a, B:69:0x0139, B:76:0x014a, B:79:0x0155, B:81:0x0163, B:84:0x016f, B:86:0x0182, B:88:0x018c, B:90:0x0196, B:92:0x01a5, B:94:0x01aa, B:96:0x01b2, B:98:0x01be, B:100:0x01d0, B:102:0x01d5, B:104:0x01dd, B:106:0x01e9, B:121:0x0234, B:123:0x023f, B:125:0x02a3, B:129:0x02ad, B:133:0x02c1, B:134:0x02ce, B:162:0x03cf, B:166:0x03de, B:168:0x03e8, B:169:0x03fc, B:171:0x0406, B:174:0x0411, B:175:0x0415, B:176:0x0437, B:178:0x0455, B:180:0x045b, B:232:0x059f, B:235:0x05a6, B:247:0x05d2, B:250:0x05d9, B:254:0x05f8, B:256:0x061d, B:258:0x064c, B:262:0x0670, B:263:0x0694, B:265:0x072f, B:268:0x0754, B:269:0x077e, B:271:0x07a8, B:245:0x05cd), top: B:313:0x0008, inners: #6, #16, #18 }] */
    /* JADX WARN: Removed duplicated region for block: B:31:0x00d8  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00ec  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00ef  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00f4 A[Catch: all -> 0x07bf, TryCatch #22 {all -> 0x07bf, blocks: (B:3:0x0008, B:5:0x0017, B:6:0x001c, B:8:0x009e, B:12:0x00a8, B:17:0x00b4, B:26:0x00c8, B:30:0x00d2, B:35:0x00e1, B:43:0x00f4, B:47:0x00fe, B:52:0x010d, B:60:0x0120, B:64:0x012a, B:69:0x0139, B:76:0x014a, B:79:0x0155, B:81:0x0163, B:84:0x016f, B:86:0x0182, B:88:0x018c, B:90:0x0196, B:92:0x01a5, B:94:0x01aa, B:96:0x01b2, B:98:0x01be, B:100:0x01d0, B:102:0x01d5, B:104:0x01dd, B:106:0x01e9, B:121:0x0234, B:123:0x023f, B:125:0x02a3, B:129:0x02ad, B:133:0x02c1, B:134:0x02ce, B:162:0x03cf, B:166:0x03de, B:168:0x03e8, B:169:0x03fc, B:171:0x0406, B:174:0x0411, B:175:0x0415, B:176:0x0437, B:178:0x0455, B:180:0x045b, B:232:0x059f, B:235:0x05a6, B:247:0x05d2, B:250:0x05d9, B:254:0x05f8, B:256:0x061d, B:258:0x064c, B:262:0x0670, B:263:0x0694, B:265:0x072f, B:268:0x0754, B:269:0x077e, B:271:0x07a8, B:245:0x05cd), top: B:313:0x0008, inners: #6, #16, #18 }] */
    /* JADX WARN: Removed duplicated region for block: B:44:0x00f9  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x00fe A[Catch: all -> 0x07bf, TryCatch #22 {all -> 0x07bf, blocks: (B:3:0x0008, B:5:0x0017, B:6:0x001c, B:8:0x009e, B:12:0x00a8, B:17:0x00b4, B:26:0x00c8, B:30:0x00d2, B:35:0x00e1, B:43:0x00f4, B:47:0x00fe, B:52:0x010d, B:60:0x0120, B:64:0x012a, B:69:0x0139, B:76:0x014a, B:79:0x0155, B:81:0x0163, B:84:0x016f, B:86:0x0182, B:88:0x018c, B:90:0x0196, B:92:0x01a5, B:94:0x01aa, B:96:0x01b2, B:98:0x01be, B:100:0x01d0, B:102:0x01d5, B:104:0x01dd, B:106:0x01e9, B:121:0x0234, B:123:0x023f, B:125:0x02a3, B:129:0x02ad, B:133:0x02c1, B:134:0x02ce, B:162:0x03cf, B:166:0x03de, B:168:0x03e8, B:169:0x03fc, B:171:0x0406, B:174:0x0411, B:175:0x0415, B:176:0x0437, B:178:0x0455, B:180:0x045b, B:232:0x059f, B:235:0x05a6, B:247:0x05d2, B:250:0x05d9, B:254:0x05f8, B:256:0x061d, B:258:0x064c, B:262:0x0670, B:263:0x0694, B:265:0x072f, B:268:0x0754, B:269:0x077e, B:271:0x07a8, B:245:0x05cd), top: B:313:0x0008, inners: #6, #16, #18 }] */
    /* JADX WARN: Removed duplicated region for block: B:48:0x0104  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x0118  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x011b  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x0120 A[Catch: all -> 0x07bf, TryCatch #22 {all -> 0x07bf, blocks: (B:3:0x0008, B:5:0x0017, B:6:0x001c, B:8:0x009e, B:12:0x00a8, B:17:0x00b4, B:26:0x00c8, B:30:0x00d2, B:35:0x00e1, B:43:0x00f4, B:47:0x00fe, B:52:0x010d, B:60:0x0120, B:64:0x012a, B:69:0x0139, B:76:0x014a, B:79:0x0155, B:81:0x0163, B:84:0x016f, B:86:0x0182, B:88:0x018c, B:90:0x0196, B:92:0x01a5, B:94:0x01aa, B:96:0x01b2, B:98:0x01be, B:100:0x01d0, B:102:0x01d5, B:104:0x01dd, B:106:0x01e9, B:121:0x0234, B:123:0x023f, B:125:0x02a3, B:129:0x02ad, B:133:0x02c1, B:134:0x02ce, B:162:0x03cf, B:166:0x03de, B:168:0x03e8, B:169:0x03fc, B:171:0x0406, B:174:0x0411, B:175:0x0415, B:176:0x0437, B:178:0x0455, B:180:0x045b, B:232:0x059f, B:235:0x05a6, B:247:0x05d2, B:250:0x05d9, B:254:0x05f8, B:256:0x061d, B:258:0x064c, B:262:0x0670, B:263:0x0694, B:265:0x072f, B:268:0x0754, B:269:0x077e, B:271:0x07a8, B:245:0x05cd), top: B:313:0x0008, inners: #6, #16, #18 }] */
    /* JADX WARN: Removed duplicated region for block: B:61:0x0125  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x012a A[Catch: all -> 0x07bf, TryCatch #22 {all -> 0x07bf, blocks: (B:3:0x0008, B:5:0x0017, B:6:0x001c, B:8:0x009e, B:12:0x00a8, B:17:0x00b4, B:26:0x00c8, B:30:0x00d2, B:35:0x00e1, B:43:0x00f4, B:47:0x00fe, B:52:0x010d, B:60:0x0120, B:64:0x012a, B:69:0x0139, B:76:0x014a, B:79:0x0155, B:81:0x0163, B:84:0x016f, B:86:0x0182, B:88:0x018c, B:90:0x0196, B:92:0x01a5, B:94:0x01aa, B:96:0x01b2, B:98:0x01be, B:100:0x01d0, B:102:0x01d5, B:104:0x01dd, B:106:0x01e9, B:121:0x0234, B:123:0x023f, B:125:0x02a3, B:129:0x02ad, B:133:0x02c1, B:134:0x02ce, B:162:0x03cf, B:166:0x03de, B:168:0x03e8, B:169:0x03fc, B:171:0x0406, B:174:0x0411, B:175:0x0415, B:176:0x0437, B:178:0x0455, B:180:0x045b, B:232:0x059f, B:235:0x05a6, B:247:0x05d2, B:250:0x05d9, B:254:0x05f8, B:256:0x061d, B:258:0x064c, B:262:0x0670, B:263:0x0694, B:265:0x072f, B:268:0x0754, B:269:0x077e, B:271:0x07a8, B:245:0x05cd), top: B:313:0x0008, inners: #6, #16, #18 }] */
    /* JADX WARN: Removed duplicated region for block: B:65:0x0130  */
    /* JADX WARN: Removed duplicated region for block: B:73:0x0144  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x0147  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.String createThemePreviewImage(java.lang.String r64, java.lang.String r65, org.telegram.ui.ActionBar.Theme.ThemeAccent r66) {
        /*
            Method dump skipped, instructions count: 1989
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.createThemePreviewImage(java.lang.String, java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeAccent):java.lang.String");
    }

    public static void checkIsDark(HashMap<String, Integer> colors, ThemeInfo info) {
        if (info != null && colors != null && info.isDark == -1) {
            int averageBackgroundColor = getPreviewColor(colors, key_windowBackgroundWhite);
            if (ColorUtils.calculateLuminance(ColorUtils.blendARGB(averageBackgroundColor, getPreviewColor(colors, key_windowBackgroundWhite), 0.5f)) < 0.5d) {
                info.isDark = 1;
            } else {
                info.isDark = 0;
            }
        }
    }

    public static HashMap<String, Integer> getThemeFileValues(File file, String assetName, String[] wallpaperLink) {
        Throwable e;
        byte[] bytes;
        int currentPosition;
        File file2;
        byte[] bytes2;
        byte[] bytes3;
        String param;
        int value;
        String[] strArr = wallpaperLink;
        FileInputStream stream = null;
        HashMap<String, Integer> stringMap = new HashMap<>(500);
        try {
            try {
                bytes = new byte[1024];
                currentPosition = 0;
                if (assetName == null) {
                    file2 = file;
                } else {
                    file2 = getAssetFile(assetName);
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            try {
                stream = new FileInputStream(file2);
                boolean finished = false;
                int wallpaperFileOffset = -1;
                while (true) {
                    int read = stream.read(bytes);
                    if (read == -1) {
                        break;
                    }
                    int previousPosition = currentPosition;
                    int a = 0;
                    int start = 0;
                    int currentPosition2 = currentPosition;
                    while (true) {
                        if (a >= read) {
                            bytes2 = bytes;
                            break;
                        }
                        if (bytes[a] != 10) {
                            bytes3 = bytes;
                        } else {
                            int len = (a - start) + 1;
                            String line = new String(bytes, start, len - 1);
                            if (line.startsWith("WLS=")) {
                                if (strArr == null || strArr.length <= 0) {
                                    bytes3 = bytes;
                                } else {
                                    strArr[0] = line.substring(4);
                                    bytes3 = bytes;
                                }
                            } else if (line.startsWith("WPS")) {
                                int wallpaperFileOffset2 = currentPosition2 + len;
                                finished = true;
                                wallpaperFileOffset = wallpaperFileOffset2;
                                bytes2 = bytes;
                                break;
                            } else {
                                int idx = line.indexOf(61);
                                if (idx != -1) {
                                    String key = line.substring(0, idx);
                                    String param2 = line.substring(idx + 1);
                                    if (param2.length() > 0) {
                                        param = param2;
                                        bytes3 = bytes;
                                        if (param.charAt(0) == '#') {
                                            try {
                                                value = Color.parseColor(param);
                                            } catch (Exception e3) {
                                                value = Utilities.parseInt((CharSequence) param).intValue();
                                            }
                                            stringMap.put(key, Integer.valueOf(value));
                                        }
                                    } else {
                                        param = param2;
                                        bytes3 = bytes;
                                    }
                                    value = Utilities.parseInt((CharSequence) param).intValue();
                                    stringMap.put(key, Integer.valueOf(value));
                                } else {
                                    bytes3 = bytes;
                                }
                            }
                            start += len;
                            currentPosition2 += len;
                        }
                        a++;
                        strArr = wallpaperLink;
                        bytes = bytes3;
                    }
                    if (previousPosition == currentPosition2) {
                        break;
                    }
                    stream.getChannel().position(currentPosition2);
                    if (finished) {
                        break;
                    }
                    strArr = wallpaperLink;
                    currentPosition = currentPosition2;
                    bytes = bytes2;
                }
                stringMap.put("wallpaperFileOffset", Integer.valueOf(wallpaperFileOffset));
                stream.close();
            } catch (Throwable th) {
                e = th;
                try {
                    FileLog.e(e);
                    if (stream != null) {
                        stream.close();
                    }
                    return stringMap;
                } catch (Throwable th2) {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Exception e4) {
                            FileLog.e(e4);
                        }
                    }
                    throw th2;
                }
            }
        } catch (Throwable th3) {
            e = th3;
        }
        return stringMap;
    }

    public static void createCommonResources(Context context) {
        if (dividerPaint == null) {
            Paint paint = new Paint();
            dividerPaint = paint;
            paint.setStrokeWidth(1.0f);
            Paint paint2 = new Paint();
            dividerExtraPaint = paint2;
            paint2.setStrokeWidth(1.0f);
            avatar_backgroundPaint = new Paint(1);
            Paint paint3 = new Paint(1);
            checkboxSquare_checkPaint = paint3;
            paint3.setStyle(Paint.Style.STROKE);
            checkboxSquare_checkPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
            checkboxSquare_checkPaint.setStrokeCap(Paint.Cap.ROUND);
            Paint paint4 = new Paint(1);
            checkboxSquare_eraserPaint = paint4;
            paint4.setColor(0);
            checkboxSquare_eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            checkboxSquare_backgroundPaint = new Paint(1);
            Paint paint5 = new Paint();
            linkSelectionPaint = paint5;
            paint5.setPathEffect(LinkPath.getRoundedEffect());
            Resources resources = context.getResources();
            avatarDrawables[0] = resources.getDrawable(R.drawable.chats_saved);
            avatarDrawables[1] = resources.getDrawable(R.drawable.ghost);
            avatarDrawables[2] = resources.getDrawable(R.drawable.msg_folders_private);
            avatarDrawables[3] = resources.getDrawable(R.drawable.msg_folders_requests);
            avatarDrawables[4] = resources.getDrawable(R.drawable.msg_folders_groups);
            avatarDrawables[5] = resources.getDrawable(R.drawable.msg_folders_channels);
            avatarDrawables[6] = resources.getDrawable(R.drawable.msg_folders_bots);
            avatarDrawables[7] = resources.getDrawable(R.drawable.msg_folders_muted);
            avatarDrawables[8] = resources.getDrawable(R.drawable.msg_folders_read);
            avatarDrawables[9] = resources.getDrawable(R.drawable.msg_folders_archive);
            avatarDrawables[10] = resources.getDrawable(R.drawable.msg_folders_private);
            avatarDrawables[11] = resources.getDrawable(R.drawable.chats_replies);
            RLottieDrawable rLottieDrawable = dialogs_archiveAvatarDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.setCallback(null);
                dialogs_archiveAvatarDrawable.recycle();
            }
            RLottieDrawable rLottieDrawable2 = dialogs_archiveDrawable;
            if (rLottieDrawable2 != null) {
                rLottieDrawable2.recycle();
            }
            RLottieDrawable rLottieDrawable3 = dialogs_unarchiveDrawable;
            if (rLottieDrawable3 != null) {
                rLottieDrawable3.recycle();
            }
            RLottieDrawable rLottieDrawable4 = dialogs_pinArchiveDrawable;
            if (rLottieDrawable4 != null) {
                rLottieDrawable4.recycle();
            }
            RLottieDrawable rLottieDrawable5 = dialogs_unpinArchiveDrawable;
            if (rLottieDrawable5 != null) {
                rLottieDrawable5.recycle();
            }
            RLottieDrawable rLottieDrawable6 = dialogs_hidePsaDrawable;
            if (rLottieDrawable6 != null) {
                rLottieDrawable6.recycle();
            }
            dialogs_archiveAvatarDrawable = new RLottieDrawable(R.raw.chats_archiveavatar, "chats_archiveavatar", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f), false, null);
            dialogs_archiveDrawable = new RLottieDrawable(R.raw.chats_archive, "chats_archive", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            dialogs_unarchiveDrawable = new RLottieDrawable(R.raw.chats_unarchive, "chats_unarchive", AndroidUtilities.dp(AndroidUtilities.dp(36.0f)), AndroidUtilities.dp(36.0f));
            dialogs_pinArchiveDrawable = new RLottieDrawable(R.raw.chats_hide, "chats_hide", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            dialogs_unpinArchiveDrawable = new RLottieDrawable(R.raw.chats_unhide, "chats_unhide", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            dialogs_hidePsaDrawable = new RLottieDrawable(R.raw.chat_audio_record_delete, "chats_psahide", AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
            dialogs_swipeMuteDrawable = new RLottieDrawable(R.raw.swipe_mute, "swipe_mute", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            dialogs_swipeUnmuteDrawable = new RLottieDrawable(R.raw.swipe_unmute, "swipe_unmute", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            dialogs_swipeReadDrawable = new RLottieDrawable(R.raw.swipe_read, "swipe_read", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            dialogs_swipeUnreadDrawable = new RLottieDrawable(R.raw.swipe_unread, "swipe_unread", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            dialogs_swipeDeleteDrawable = new RLottieDrawable(R.raw.swipe_delete, "swipe_delete", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            dialogs_swipeUnpinDrawable = new RLottieDrawable(R.raw.swipe_unpin, "swipe_unpin", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            dialogs_swipePinDrawable = new RLottieDrawable(R.raw.swipe_pin, "swipe_pin", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            applyCommonTheme();
        }
    }

    public static void applyCommonTheme() {
        Paint paint = dividerPaint;
        if (paint == null) {
            return;
        }
        paint.setColor(getColor(key_divider));
        linkSelectionPaint.setColor(getColor(key_windowBackgroundWhiteLinkSelection));
        int a = 0;
        while (true) {
            Drawable[] drawableArr = avatarDrawables;
            if (a < drawableArr.length) {
                setDrawableColorByKey(drawableArr[a], key_avatar_text);
                a++;
            } else {
                dialogs_archiveAvatarDrawable.beginApplyLayerColors();
                dialogs_archiveAvatarDrawable.setLayerColor("Arrow1.**", getNonAnimatedColor(key_avatar_backgroundArchived));
                dialogs_archiveAvatarDrawable.setLayerColor("Arrow2.**", getNonAnimatedColor(key_avatar_backgroundArchived));
                dialogs_archiveAvatarDrawable.setLayerColor("Box2.**", getNonAnimatedColor(key_avatar_text));
                dialogs_archiveAvatarDrawable.setLayerColor("Box1.**", getNonAnimatedColor(key_avatar_text));
                dialogs_archiveAvatarDrawable.commitApplyLayerColors();
                dialogs_archiveAvatarDrawableRecolored = false;
                dialogs_archiveAvatarDrawable.setAllowDecodeSingleFrame(true);
                dialogs_pinArchiveDrawable.beginApplyLayerColors();
                dialogs_pinArchiveDrawable.setLayerColor("Arrow.**", getNonAnimatedColor(key_chats_archiveIcon));
                dialogs_pinArchiveDrawable.setLayerColor("Line.**", getNonAnimatedColor(key_chats_archiveIcon));
                dialogs_pinArchiveDrawable.commitApplyLayerColors();
                dialogs_unpinArchiveDrawable.beginApplyLayerColors();
                dialogs_unpinArchiveDrawable.setLayerColor("Arrow.**", getNonAnimatedColor(key_chats_archiveIcon));
                dialogs_unpinArchiveDrawable.setLayerColor("Line.**", getNonAnimatedColor(key_chats_archiveIcon));
                dialogs_unpinArchiveDrawable.commitApplyLayerColors();
                dialogs_hidePsaDrawable.beginApplyLayerColors();
                dialogs_hidePsaDrawable.setLayerColor("Line 1.**", getNonAnimatedColor(key_chats_archiveBackground));
                dialogs_hidePsaDrawable.setLayerColor("Line 2.**", getNonAnimatedColor(key_chats_archiveBackground));
                dialogs_hidePsaDrawable.setLayerColor("Line 3.**", getNonAnimatedColor(key_chats_archiveBackground));
                dialogs_hidePsaDrawable.setLayerColor("Cup Red.**", getNonAnimatedColor(key_chats_archiveIcon));
                dialogs_hidePsaDrawable.setLayerColor("Box.**", getNonAnimatedColor(key_chats_archiveIcon));
                dialogs_hidePsaDrawable.commitApplyLayerColors();
                dialogs_hidePsaDrawableRecolored = false;
                dialogs_archiveDrawable.beginApplyLayerColors();
                dialogs_archiveDrawable.setLayerColor("Arrow.**", getNonAnimatedColor(key_chats_archiveBackground));
                dialogs_archiveDrawable.setLayerColor("Box2.**", getNonAnimatedColor(key_chats_archiveIcon));
                dialogs_archiveDrawable.setLayerColor("Box1.**", getNonAnimatedColor(key_chats_archiveIcon));
                dialogs_archiveDrawable.commitApplyLayerColors();
                dialogs_archiveDrawableRecolored = false;
                dialogs_unarchiveDrawable.beginApplyLayerColors();
                dialogs_unarchiveDrawable.setLayerColor("Arrow1.**", getNonAnimatedColor(key_chats_archiveIcon));
                dialogs_unarchiveDrawable.setLayerColor("Arrow2.**", getNonAnimatedColor(key_chats_archivePinBackground));
                dialogs_unarchiveDrawable.setLayerColor("Box2.**", getNonAnimatedColor(key_chats_archiveIcon));
                dialogs_unarchiveDrawable.setLayerColor("Box1.**", getNonAnimatedColor(key_chats_archiveIcon));
                dialogs_unarchiveDrawable.commitApplyLayerColors();
                PremiumGradient.getInstance().checkIconColors();
                return;
            }
        }
    }

    public static void createCommonDialogResources(Context context) {
        if (dialogs_countTextPaint == null) {
            TextPaint textPaint = new TextPaint(1);
            dialogs_countTextPaint = textPaint;
            textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            dialogs_countPaint = new Paint(1);
            dialogs_reactionsCountPaint = new Paint(1);
            dialogs_onlineCirclePaint = new Paint(1);
        }
        dialogs_countTextPaint.setTextSize(AndroidUtilities.dp(13.0f));
    }

    public static void createDialogsResources(Context context) {
        createCommonResources(context);
        createCommonDialogResources(context);
        if (dialogs_namePaint == null) {
            Resources resources = context.getResources();
            dialogs_namePaint = new TextPaint[2];
            dialogs_nameEncryptedPaint = new TextPaint[2];
            dialogs_messagePaint = new TextPaint[2];
            dialogs_messagePrintingPaint = new TextPaint[2];
            for (int a = 0; a < 2; a++) {
                dialogs_namePaint[a] = new TextPaint(1);
                dialogs_namePaint[a].setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                dialogs_nameEncryptedPaint[a] = new TextPaint(1);
                dialogs_nameEncryptedPaint[a].setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                dialogs_messagePaint[a] = new TextPaint(1);
                dialogs_messagePrintingPaint[a] = new TextPaint(1);
            }
            TextPaint textPaint = new TextPaint(1);
            dialogs_searchNamePaint = textPaint;
            textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            TextPaint textPaint2 = new TextPaint(1);
            dialogs_searchNameEncryptedPaint = textPaint2;
            textPaint2.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            TextPaint textPaint3 = new TextPaint(1);
            dialogs_messageNamePaint = textPaint3;
            textPaint3.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            dialogs_timePaint = new TextPaint(1);
            TextPaint textPaint4 = new TextPaint(1);
            dialogs_archiveTextPaint = textPaint4;
            textPaint4.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            TextPaint textPaint5 = new TextPaint(1);
            dialogs_archiveTextPaintSmall = textPaint5;
            textPaint5.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            dialogs_onlinePaint = new TextPaint(1);
            dialogs_offlinePaint = new TextPaint(1);
            dialogs_tabletSeletedPaint = new Paint();
            dialogs_pinnedPaint = new Paint(1);
            dialogs_countGrayPaint = new Paint(1);
            dialogs_errorPaint = new Paint(1);
            dialogs_actionMessagePaint = new Paint(1);
            dialogs_lockDrawable = resources.getDrawable(R.drawable.list_secret);
            dialogs_checkDrawable = resources.getDrawable(R.drawable.list_check).mutate();
            dialogs_playDrawable = resources.getDrawable(R.drawable.minithumb_play).mutate();
            dialogs_checkReadDrawable = resources.getDrawable(R.drawable.list_check).mutate();
            dialogs_halfCheckDrawable = resources.getDrawable(R.drawable.list_halfcheck);
            dialogs_clockDrawable = new MsgClockDrawable();
            dialogs_errorDrawable = resources.getDrawable(R.drawable.list_warning_sign);
            dialogs_reorderDrawable = resources.getDrawable(R.drawable.list_reorder).mutate();
            dialogs_muteDrawable = resources.getDrawable(R.drawable.list_mute).mutate();
            dialogs_verifiedDrawable = resources.getDrawable(R.drawable.verified_area).mutate();
            dialogs_scamDrawable = new ScamDrawable(11, 0);
            dialogs_fakeDrawable = new ScamDrawable(11, 1);
            dialogs_verifiedCheckDrawable = resources.getDrawable(R.drawable.verified_check).mutate();
            dialogs_mentionDrawable = resources.getDrawable(R.drawable.mentionchatslist);
            dialogs_reactionsMentionDrawable = resources.getDrawable(R.drawable.reactionchatslist);
            dialogs_pinnedDrawable = resources.getDrawable(R.drawable.list_pin);
            moveUpDrawable = resources.getDrawable(R.drawable.preview_arrow);
            RectF rect = new RectF();
            chat_updatePath[0] = new Path();
            chat_updatePath[2] = new Path();
            float cx = AndroidUtilities.dp(12.0f);
            float cy = AndroidUtilities.dp(12.0f);
            rect.set(cx - AndroidUtilities.dp(5.0f), cy - AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f) + cx, AndroidUtilities.dp(5.0f) + cy);
            chat_updatePath[2].arcTo(rect, -160.0f, -110.0f, true);
            chat_updatePath[2].arcTo(rect, 20.0f, -110.0f, true);
            chat_updatePath[0].moveTo(cx, AndroidUtilities.dp(8.0f) + cy);
            chat_updatePath[0].lineTo(cx, AndroidUtilities.dp(2.0f) + cy);
            chat_updatePath[0].lineTo(AndroidUtilities.dp(3.0f) + cx, AndroidUtilities.dp(5.0f) + cy);
            chat_updatePath[0].close();
            chat_updatePath[0].moveTo(cx, cy - AndroidUtilities.dp(8.0f));
            chat_updatePath[0].lineTo(cx, cy - AndroidUtilities.dp(2.0f));
            chat_updatePath[0].lineTo(cx - AndroidUtilities.dp(3.0f), cy - AndroidUtilities.dp(5.0f));
            chat_updatePath[0].close();
            applyDialogsTheme();
        }
        dialogs_messageNamePaint.setTextSize(AndroidUtilities.dp(14.0f));
        dialogs_timePaint.setTextSize(AndroidUtilities.dp(13.0f));
        dialogs_archiveTextPaint.setTextSize(AndroidUtilities.dp(13.0f));
        dialogs_archiveTextPaintSmall.setTextSize(AndroidUtilities.dp(11.0f));
        dialogs_onlinePaint.setTextSize(AndroidUtilities.dp(15.0f));
        dialogs_offlinePaint.setTextSize(AndroidUtilities.dp(15.0f));
        dialogs_searchNamePaint.setTextSize(AndroidUtilities.dp(16.0f));
        dialogs_searchNameEncryptedPaint.setTextSize(AndroidUtilities.dp(16.0f));
    }

    public static void applyDialogsTheme() {
        if (dialogs_namePaint == null) {
            return;
        }
        for (int a = 0; a < 2; a++) {
            dialogs_namePaint[a].setColor(getColor(key_chats_name));
            dialogs_nameEncryptedPaint[a].setColor(getColor(key_chats_secretName));
            TextPaint[] textPaintArr = dialogs_messagePaint;
            TextPaint textPaint = textPaintArr[a];
            TextPaint textPaint2 = textPaintArr[a];
            int color = getColor(key_chats_message);
            textPaint2.linkColor = color;
            textPaint.setColor(color);
            dialogs_messagePrintingPaint[a].setColor(getColor(key_chats_actionMessage));
        }
        dialogs_searchNamePaint.setColor(getColor(key_chats_name));
        dialogs_searchNameEncryptedPaint.setColor(getColor(key_chats_secretName));
        TextPaint textPaint3 = dialogs_messageNamePaint;
        int color2 = getColor(key_chats_nameMessage_threeLines);
        textPaint3.linkColor = color2;
        textPaint3.setColor(color2);
        dialogs_tabletSeletedPaint.setColor(getColor(key_chats_tabletSelectedOverlay));
        dialogs_pinnedPaint.setColor(getColor(key_chats_pinnedOverlay));
        dialogs_timePaint.setColor(getColor(key_chats_date));
        dialogs_countTextPaint.setColor(getColor(key_chats_unreadCounterText));
        dialogs_archiveTextPaint.setColor(getColor(key_chats_archiveText));
        dialogs_archiveTextPaintSmall.setColor(getColor(key_chats_archiveText));
        dialogs_countPaint.setColor(getColor(key_chats_unreadCounter));
        dialogs_reactionsCountPaint.setColor(getColor(key_dialogReactionMentionBackground));
        dialogs_countGrayPaint.setColor(getColor(key_chats_unreadCounterMuted));
        dialogs_actionMessagePaint.setColor(getColor(key_chats_actionMessage));
        dialogs_errorPaint.setColor(getColor(key_chats_sentError));
        dialogs_onlinePaint.setColor(getColor(key_windowBackgroundWhiteBlueText3));
        dialogs_offlinePaint.setColor(getColor(key_windowBackgroundWhiteGrayText3));
        setDrawableColorByKey(dialogs_lockDrawable, key_chats_secretIcon);
        setDrawableColorByKey(dialogs_checkDrawable, key_chats_sentCheck);
        setDrawableColorByKey(dialogs_checkReadDrawable, key_chats_sentReadCheck);
        setDrawableColorByKey(dialogs_halfCheckDrawable, key_chats_sentReadCheck);
        setDrawableColorByKey(dialogs_clockDrawable, key_chats_sentClock);
        setDrawableColorByKey(dialogs_errorDrawable, key_chats_sentErrorIcon);
        setDrawableColorByKey(dialogs_pinnedDrawable, key_chats_pinnedIcon);
        setDrawableColorByKey(dialogs_reorderDrawable, key_chats_pinnedIcon);
        setDrawableColorByKey(dialogs_muteDrawable, key_chats_muteIcon);
        setDrawableColorByKey(dialogs_mentionDrawable, key_chats_mentionIcon);
        setDrawableColorByKey(dialogs_reactionsMentionDrawable, key_chats_mentionIcon);
        setDrawableColorByKey(dialogs_verifiedDrawable, key_chats_verifiedBackground);
        setDrawableColorByKey(dialogs_verifiedCheckDrawable, key_chats_verifiedCheck);
        setDrawableColorByKey(dialogs_holidayDrawable, key_actionBarDefaultTitle);
        setDrawableColorByKey(dialogs_scamDrawable, key_chats_draft);
        setDrawableColorByKey(dialogs_fakeDrawable, key_chats_draft);
    }

    public static void destroyResources() {
    }

    public static void reloadAllResources(Context context) {
        destroyResources();
        if (chat_msgInDrawable != null) {
            chat_msgInDrawable = null;
            currentColor = 0;
            createChatResources(context, false);
        }
        if (dialogs_namePaint != null) {
            dialogs_namePaint = null;
            createDialogsResources(context);
        }
        if (profile_verifiedDrawable != null) {
            profile_verifiedDrawable = null;
            createProfileResources(context);
        }
    }

    public static void createCommonMessageResources() {
        synchronized (sync) {
            if (chat_msgTextPaint == null) {
                chat_msgTextPaint = new TextPaint(1);
                chat_msgGameTextPaint = new TextPaint(1);
                chat_msgTextPaintOneEmoji = new TextPaint(1);
                chat_msgTextPaintTwoEmoji = new TextPaint(1);
                chat_msgTextPaintThreeEmoji = new TextPaint(1);
                TextPaint textPaint = new TextPaint(1);
                chat_msgBotButtonPaint = textPaint;
                textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            }
            chat_msgTextPaintOneEmoji.setTextSize(AndroidUtilities.dp(28.0f));
            chat_msgTextPaintTwoEmoji.setTextSize(AndroidUtilities.dp(24.0f));
            chat_msgTextPaintThreeEmoji.setTextSize(AndroidUtilities.dp(20.0f));
            chat_msgTextPaint.setTextSize(AndroidUtilities.dp(SharedConfig.fontSize));
            chat_msgGameTextPaint.setTextSize(AndroidUtilities.dp(14.0f));
            chat_msgBotButtonPaint.setTextSize(AndroidUtilities.dp(15.0f));
        }
    }

    public static void createCommonChatResources() {
        createCommonMessageResources();
        if (chat_infoPaint == null) {
            chat_infoPaint = new TextPaint(1);
            TextPaint textPaint = new TextPaint(1);
            chat_stickerCommentCountPaint = textPaint;
            textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            TextPaint textPaint2 = new TextPaint(1);
            chat_docNamePaint = textPaint2;
            textPaint2.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            chat_docBackPaint = new Paint(1);
            chat_deleteProgressPaint = new Paint(1);
            Paint paint = new Paint(1);
            chat_botProgressPaint = paint;
            paint.setStrokeCap(Paint.Cap.ROUND);
            chat_botProgressPaint.setStyle(Paint.Style.STROKE);
            TextPaint textPaint3 = new TextPaint(1);
            chat_locationTitlePaint = textPaint3;
            textPaint3.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            chat_locationAddressPaint = new TextPaint(1);
            Paint paint2 = new Paint();
            chat_urlPaint = paint2;
            paint2.setPathEffect(LinkPath.getRoundedEffect());
            Paint paint3 = new Paint();
            chat_outUrlPaint = paint3;
            paint3.setPathEffect(LinkPath.getRoundedEffect());
            Paint paint4 = new Paint();
            chat_textSearchSelectionPaint = paint4;
            paint4.setPathEffect(LinkPath.getRoundedEffect());
            Paint paint5 = new Paint(1);
            chat_radialProgressPaint = paint5;
            paint5.setStrokeCap(Paint.Cap.ROUND);
            chat_radialProgressPaint.setStyle(Paint.Style.STROKE);
            chat_radialProgressPaint.setColor(-1610612737);
            Paint paint6 = new Paint(1);
            chat_radialProgress2Paint = paint6;
            paint6.setStrokeCap(Paint.Cap.ROUND);
            chat_radialProgress2Paint.setStyle(Paint.Style.STROKE);
            chat_audioTimePaint = new TextPaint(1);
            TextPaint textPaint4 = new TextPaint(1);
            chat_livePaint = textPaint4;
            textPaint4.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            TextPaint textPaint5 = new TextPaint(1);
            chat_audioTitlePaint = textPaint5;
            textPaint5.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            chat_audioPerformerPaint = new TextPaint(1);
            TextPaint textPaint6 = new TextPaint(1);
            chat_botButtonPaint = textPaint6;
            textPaint6.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            TextPaint textPaint7 = new TextPaint(1);
            chat_contactNamePaint = textPaint7;
            textPaint7.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            chat_contactPhonePaint = new TextPaint(1);
            chat_durationPaint = new TextPaint(1);
            TextPaint textPaint8 = new TextPaint(1);
            chat_gamePaint = textPaint8;
            textPaint8.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            chat_shipmentPaint = new TextPaint(1);
            chat_timePaint = new TextPaint(1);
            chat_adminPaint = new TextPaint(1);
            TextPaint textPaint9 = new TextPaint(1);
            chat_namePaint = textPaint9;
            textPaint9.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            chat_forwardNamePaint = new TextPaint(1);
            TextPaint textPaint10 = new TextPaint(1);
            chat_replyNamePaint = textPaint10;
            textPaint10.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            chat_replyTextPaint = new TextPaint(1);
            TextPaint textPaint11 = new TextPaint(1);
            chat_instantViewPaint = textPaint11;
            textPaint11.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            Paint paint7 = new Paint(1);
            chat_instantViewRectPaint = paint7;
            paint7.setStyle(Paint.Style.STROKE);
            chat_instantViewRectPaint.setStrokeCap(Paint.Cap.ROUND);
            Paint paint8 = new Paint(1);
            chat_pollTimerPaint = paint8;
            paint8.setStyle(Paint.Style.STROKE);
            chat_pollTimerPaint.setStrokeCap(Paint.Cap.ROUND);
            chat_replyLinePaint = new Paint(1);
            chat_msgErrorPaint = new Paint(1);
            chat_statusPaint = new Paint(1);
            Paint paint9 = new Paint(1);
            chat_statusRecordPaint = paint9;
            paint9.setStyle(Paint.Style.STROKE);
            chat_statusRecordPaint.setStrokeCap(Paint.Cap.ROUND);
            TextPaint textPaint12 = new TextPaint(1);
            chat_actionTextPaint = textPaint12;
            textPaint12.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            Paint paint10 = new Paint(1);
            chat_actionBackgroundGradientDarkenPaint = paint10;
            paint10.setColor(704643072);
            chat_timeBackgroundPaint = new Paint(1);
            TextPaint textPaint13 = new TextPaint(1);
            chat_contextResult_titleTextPaint = textPaint13;
            textPaint13.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            chat_contextResult_descriptionTextPaint = new TextPaint(1);
            chat_composeBackgroundPaint = new Paint();
            chat_radialProgressPausedPaint = new Paint(1);
            chat_radialProgressPausedSeekbarPaint = new Paint(1);
            chat_messageBackgroundSelectedPaint = new Paint(1);
            chat_actionBackgroundPaint = new Paint(1);
            chat_actionBackgroundSelectedPaint = new Paint(1);
            chat_actionBackgroundPaint2 = new Paint(1);
            chat_actionBackgroundSelectedPaint2 = new Paint(1);
            addChatPaint(key_paint_chatMessageBackgroundSelected, chat_messageBackgroundSelectedPaint, key_chat_selectedBackground);
            addChatPaint(key_paint_chatActionBackground, chat_actionBackgroundPaint, key_chat_serviceBackground);
            addChatPaint(key_paint_chatActionBackgroundSelected, chat_actionBackgroundSelectedPaint, key_chat_serviceBackgroundSelected);
            addChatPaint(key_paint_chatActionText, chat_actionTextPaint, key_chat_serviceText);
            addChatPaint(key_paint_chatBotButton, chat_botButtonPaint, key_chat_botButtonText);
            addChatPaint(key_paint_chatComposeBackground, chat_composeBackgroundPaint, key_chat_messagePanelBackground);
            addChatPaint(key_paint_chatTimeBackground, chat_timeBackgroundPaint, key_chat_mediaTimeBackground);
        }
    }

    public static void createChatResources(Context context, boolean fontsOnly) {
        Paint paint;
        createCommonChatResources();
        if (!fontsOnly && chat_msgInDrawable == null) {
            Resources resources = context.getResources();
            chat_msgNoSoundDrawable = resources.getDrawable(R.drawable.video_muted);
            chat_msgInDrawable = new MessageDrawable(0, false, false);
            chat_msgInSelectedDrawable = new MessageDrawable(0, false, true);
            chat_msgOutDrawable = new MessageDrawable(0, true, false);
            chat_msgOutSelectedDrawable = new MessageDrawable(0, true, true);
            chat_msgInMediaDrawable = new MessageDrawable(1, false, false);
            chat_msgInMediaSelectedDrawable = new MessageDrawable(1, false, true);
            chat_msgOutMediaDrawable = new MessageDrawable(1, true, false);
            chat_msgOutMediaSelectedDrawable = new MessageDrawable(1, true, true);
            PathAnimator pathAnimator = new PathAnimator(0.293f, -26.0f, -28.0f, 1.0f);
            playPauseAnimator = pathAnimator;
            pathAnimator.addSvgKeyFrame("M 34.141 16.042 C 37.384 17.921 40.886 20.001 44.211 21.965 C 46.139 23.104 49.285 24.729 49.586 25.917 C 50.289 28.687 48.484 30 46.274 30 L 6 30.021 C 3.79 30.021 2.075 30.023 2 26.021 L 2.009 3.417 C 2.009 0.417 5.326 -0.58 7.068 0.417 C 10.545 2.406 25.024 10.761 34.141 16.042 Z", 166.0f);
            playPauseAnimator.addSvgKeyFrame("M 37.843 17.769 C 41.143 19.508 44.131 21.164 47.429 23.117 C 48.542 23.775 49.623 24.561 49.761 25.993 C 50.074 28.708 48.557 30 46.347 30 L 6 30.012 C 3.79 30.012 2 28.222 2 26.012 L 2.009 4.609 C 2.009 1.626 5.276 0.664 7.074 1.541 C 10.608 3.309 28.488 12.842 37.843 17.769 Z", 200.0f);
            playPauseAnimator.addSvgKeyFrame("M 40.644 18.756 C 43.986 20.389 49.867 23.108 49.884 25.534 C 49.897 27.154 49.88 24.441 49.894 26.059 C 49.911 28.733 48.6 30 46.39 30 L 6 30.013 C 3.79 30.013 2 28.223 2 26.013 L 2.008 5.52 C 2.008 2.55 5.237 1.614 7.079 2.401 C 10.656 4 31.106 14.097 40.644 18.756 Z", 217.0f);
            playPauseAnimator.addSvgKeyFrame("M 43.782 19.218 C 47.117 20.675 50.075 21.538 50.041 24.796 C 50.022 26.606 50.038 24.309 50.039 26.104 C 50.038 28.736 48.663 30 46.453 30 L 6 29.986 C 3.79 29.986 2 28.196 2 25.986 L 2.008 6.491 C 2.008 3.535 5.196 2.627 7.085 3.316 C 10.708 4.731 33.992 14.944 43.782 19.218 Z", 234.0f);
            playPauseAnimator.addSvgKeyFrame("M 47.421 16.941 C 50.544 18.191 50.783 19.91 50.769 22.706 C 50.761 24.484 50.76 23.953 50.79 26.073 C 50.814 27.835 49.334 30 47.124 30 L 5 30.01 C 2.79 30.01 1 28.22 1 26.01 L 1.001 10.823 C 1.001 8.218 3.532 6.895 5.572 7.26 C 7.493 8.01 47.421 16.941 47.421 16.941 Z", 267.0f);
            playPauseAnimator.addSvgKeyFrame("M 47.641 17.125 C 50.641 18.207 51.09 19.935 51.078 22.653 C 51.07 24.191 51.062 21.23 51.088 23.063 C 51.109 24.886 49.587 27 47.377 27 L 5 27.009 C 2.79 27.009 1 25.219 1 23.009 L 0.983 11.459 C 0.983 8.908 3.414 7.522 5.476 7.838 C 7.138 8.486 47.641 17.125 47.641 17.125 Z", 300.0f);
            playPauseAnimator.addSvgKeyFrame("M 48 7 C 50.21 7 52 8.79 52 11 C 52 19 52 19 52 19 C 52 21.21 50.21 23 48 23 L 4 23 C 1.79 23 0 21.21 0 19 L 0 11 C 0 8.79 1.79 7 4 7 C 48 7 48 7 48 7 Z", 383.0f);
            chat_msgOutCheckDrawable = resources.getDrawable(R.drawable.msg_check_s).mutate();
            chat_msgOutCheckSelectedDrawable = resources.getDrawable(R.drawable.msg_check_s).mutate();
            chat_msgOutCheckReadDrawable = resources.getDrawable(R.drawable.msg_check_s).mutate();
            chat_msgOutCheckReadSelectedDrawable = resources.getDrawable(R.drawable.msg_check_s).mutate();
            chat_msgMediaCheckDrawable = resources.getDrawable(R.drawable.msg_check_s).mutate();
            chat_msgStickerCheckDrawable = resources.getDrawable(R.drawable.msg_check_s).mutate();
            chat_msgOutHalfCheckDrawable = resources.getDrawable(R.drawable.msg_halfcheck).mutate();
            chat_msgOutHalfCheckSelectedDrawable = resources.getDrawable(R.drawable.msg_halfcheck).mutate();
            chat_msgMediaHalfCheckDrawable = resources.getDrawable(R.drawable.msg_halfcheck_s).mutate();
            chat_msgStickerHalfCheckDrawable = resources.getDrawable(R.drawable.msg_halfcheck_s).mutate();
            chat_msgClockDrawable = new MsgClockDrawable();
            chat_msgInViewsDrawable = resources.getDrawable(R.drawable.msg_views).mutate();
            chat_msgInViewsSelectedDrawable = resources.getDrawable(R.drawable.msg_views).mutate();
            chat_msgOutViewsDrawable = resources.getDrawable(R.drawable.msg_views).mutate();
            chat_msgOutViewsSelectedDrawable = resources.getDrawable(R.drawable.msg_views).mutate();
            chat_msgInRepliesDrawable = resources.getDrawable(R.drawable.msg_reply_small).mutate();
            chat_msgInRepliesSelectedDrawable = resources.getDrawable(R.drawable.msg_reply_small).mutate();
            chat_msgOutRepliesDrawable = resources.getDrawable(R.drawable.msg_reply_small).mutate();
            chat_msgOutRepliesSelectedDrawable = resources.getDrawable(R.drawable.msg_reply_small).mutate();
            chat_msgInPinnedDrawable = resources.getDrawable(R.drawable.msg_pin_mini).mutate();
            chat_msgInPinnedSelectedDrawable = resources.getDrawable(R.drawable.msg_pin_mini).mutate();
            chat_msgOutPinnedDrawable = resources.getDrawable(R.drawable.msg_pin_mini).mutate();
            chat_msgOutPinnedSelectedDrawable = resources.getDrawable(R.drawable.msg_pin_mini).mutate();
            chat_msgMediaPinnedDrawable = resources.getDrawable(R.drawable.msg_pin_mini).mutate();
            chat_msgStickerPinnedDrawable = resources.getDrawable(R.drawable.msg_pin_mini).mutate();
            chat_msgMediaViewsDrawable = resources.getDrawable(R.drawable.msg_views).mutate();
            chat_msgMediaRepliesDrawable = resources.getDrawable(R.drawable.msg_reply_small).mutate();
            chat_msgStickerViewsDrawable = resources.getDrawable(R.drawable.msg_views).mutate();
            chat_msgStickerRepliesDrawable = resources.getDrawable(R.drawable.msg_reply_small).mutate();
            chat_msgInMenuDrawable = resources.getDrawable(R.drawable.msg_actions).mutate();
            chat_msgInMenuSelectedDrawable = resources.getDrawable(R.drawable.msg_actions).mutate();
            chat_msgOutMenuDrawable = resources.getDrawable(R.drawable.msg_actions).mutate();
            chat_msgOutMenuSelectedDrawable = resources.getDrawable(R.drawable.msg_actions).mutate();
            chat_msgMediaMenuDrawable = resources.getDrawable(R.drawable.video_actions);
            chat_msgInInstantDrawable = resources.getDrawable(R.drawable.msg_instant).mutate();
            chat_msgOutInstantDrawable = resources.getDrawable(R.drawable.msg_instant).mutate();
            chat_msgErrorDrawable = resources.getDrawable(R.drawable.msg_warning);
            chat_muteIconDrawable = resources.getDrawable(R.drawable.list_mute).mutate();
            chat_lockIconDrawable = resources.getDrawable(R.drawable.ic_lock_header);
            chat_msgInCallDrawable[0] = resources.getDrawable(R.drawable.chat_calls_voice).mutate();
            chat_msgInCallSelectedDrawable[0] = resources.getDrawable(R.drawable.chat_calls_voice).mutate();
            chat_msgOutCallDrawable[0] = resources.getDrawable(R.drawable.chat_calls_voice).mutate();
            chat_msgOutCallSelectedDrawable[0] = resources.getDrawable(R.drawable.chat_calls_voice).mutate();
            chat_msgInCallDrawable[1] = resources.getDrawable(R.drawable.chat_calls_video).mutate();
            chat_msgInCallSelectedDrawable[1] = resources.getDrawable(R.drawable.chat_calls_video).mutate();
            chat_msgOutCallDrawable[1] = resources.getDrawable(R.drawable.chat_calls_video).mutate();
            chat_msgOutCallSelectedDrawable[1] = resources.getDrawable(R.drawable.chat_calls_video).mutate();
            chat_msgCallUpGreenDrawable = resources.getDrawable(R.drawable.chat_calls_outgoing).mutate();
            chat_msgCallDownRedDrawable = resources.getDrawable(R.drawable.chat_calls_incoming).mutate();
            chat_msgCallDownGreenDrawable = resources.getDrawable(R.drawable.chat_calls_incoming).mutate();
            for (int a = 0; a < 2; a++) {
                chat_pollCheckDrawable[a] = resources.getDrawable(R.drawable.poll_right).mutate();
                chat_pollCrossDrawable[a] = resources.getDrawable(R.drawable.poll_wrong).mutate();
                chat_pollHintDrawable[a] = resources.getDrawable(R.drawable.msg_emoji_objects).mutate();
                chat_psaHelpDrawable[a] = resources.getDrawable(R.drawable.msg_psa).mutate();
            }
            calllog_msgCallUpRedDrawable = resources.getDrawable(R.drawable.ic_call_made_green_18dp).mutate();
            calllog_msgCallUpGreenDrawable = resources.getDrawable(R.drawable.ic_call_made_green_18dp).mutate();
            calllog_msgCallDownRedDrawable = resources.getDrawable(R.drawable.ic_call_received_green_18dp).mutate();
            calllog_msgCallDownGreenDrawable = resources.getDrawable(R.drawable.ic_call_received_green_18dp).mutate();
            chat_msgAvatarLiveLocationDrawable = resources.getDrawable(R.drawable.livepin).mutate();
            chat_inlineResultFile = resources.getDrawable(R.drawable.bot_file);
            chat_inlineResultAudio = resources.getDrawable(R.drawable.bot_music);
            chat_inlineResultLocation = resources.getDrawable(R.drawable.bot_location);
            chat_redLocationIcon = resources.getDrawable(R.drawable.map_pin).mutate();
            chat_botLinkDrawable = resources.getDrawable(R.drawable.bot_link);
            chat_botInlineDrawable = resources.getDrawable(R.drawable.bot_lines);
            chat_botCardDrawable = resources.getDrawable(R.drawable.bot_card);
            chat_botWebViewDrawable = resources.getDrawable(R.drawable.bot_webview);
            chat_botInviteDrawable = resources.getDrawable(R.drawable.bot_invite);
            chat_commentDrawable = resources.getDrawable(R.drawable.msg_msgbubble);
            chat_commentStickerDrawable = resources.getDrawable(R.drawable.msg_msgbubble2);
            chat_commentArrowDrawable = resources.getDrawable(R.drawable.msg_arrowright);
            chat_contextResult_shadowUnderSwitchDrawable = resources.getDrawable(R.drawable.header_shadow).mutate();
            chat_attachButtonDrawables[0] = new RLottieDrawable(R.raw.attach_gallery, "attach_gallery", AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f));
            chat_attachButtonDrawables[1] = new RLottieDrawable(R.raw.attach_music, "attach_music", AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f));
            chat_attachButtonDrawables[2] = new RLottieDrawable(R.raw.attach_file, "attach_file", AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f));
            chat_attachButtonDrawables[3] = new RLottieDrawable(R.raw.attach_contact, "attach_contact", AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f));
            chat_attachButtonDrawables[4] = new RLottieDrawable(R.raw.attach_location, "attach_location", AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f));
            chat_attachButtonDrawables[5] = new RLottieDrawable(R.raw.attach_poll, "attach_poll", AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f));
            chat_attachEmptyDrawable = resources.getDrawable(R.drawable.nophotos3);
            chat_shareIconDrawable = resources.getDrawable(R.drawable.share_arrow).mutate();
            chat_replyIconDrawable = resources.getDrawable(R.drawable.fast_reply);
            chat_goIconDrawable = resources.getDrawable(R.drawable.message_arrow);
            int rad = AndroidUtilities.dp(2.0f);
            RectF rect = new RectF();
            chat_filePath[0] = new Path();
            chat_filePath[0].moveTo(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(3.0f));
            chat_filePath[0].lineTo(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(3.0f));
            chat_filePath[0].lineTo(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(10.0f));
            chat_filePath[0].lineTo(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(20.0f));
            rect.set(AndroidUtilities.dp(21.0f) - (rad * 2), AndroidUtilities.dp(19.0f) - rad, AndroidUtilities.dp(21.0f), AndroidUtilities.dp(19.0f) + rad);
            chat_filePath[0].arcTo(rect, 0.0f, 90.0f, false);
            chat_filePath[0].lineTo(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(21.0f));
            rect.set(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(19.0f) - rad, AndroidUtilities.dp(5.0f) + (rad * 2), AndroidUtilities.dp(19.0f) + rad);
            chat_filePath[0].arcTo(rect, 90.0f, 90.0f, false);
            chat_filePath[0].lineTo(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(4.0f));
            rect.set(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(5.0f) + (rad * 2), AndroidUtilities.dp(3.0f) + (rad * 2));
            chat_filePath[0].arcTo(rect, 180.0f, 90.0f, false);
            chat_filePath[0].close();
            chat_filePath[1] = new Path();
            chat_filePath[1].moveTo(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(5.0f));
            chat_filePath[1].lineTo(AndroidUtilities.dp(19.0f), AndroidUtilities.dp(10.0f));
            chat_filePath[1].lineTo(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(10.0f));
            chat_filePath[1].close();
            chat_flameIcon = resources.getDrawable(R.drawable.burn).mutate();
            chat_gifIcon = resources.getDrawable(R.drawable.msg_round_gif_m).mutate();
            chat_fileStatesDrawable[0][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), R.drawable.msg_round_play_m);
            chat_fileStatesDrawable[0][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), R.drawable.msg_round_play_m);
            chat_fileStatesDrawable[1][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), R.drawable.msg_round_pause_m);
            chat_fileStatesDrawable[1][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), R.drawable.msg_round_pause_m);
            chat_fileStatesDrawable[2][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), R.drawable.msg_round_load_m);
            chat_fileStatesDrawable[2][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), R.drawable.msg_round_load_m);
            chat_fileStatesDrawable[3][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), R.drawable.msg_round_file_s);
            chat_fileStatesDrawable[3][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), R.drawable.msg_round_file_s);
            chat_fileStatesDrawable[4][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), R.drawable.msg_round_cancel_m);
            chat_fileStatesDrawable[4][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), R.drawable.msg_round_cancel_m);
            chat_fileStatesDrawable[5][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), R.drawable.msg_round_play_m);
            chat_fileStatesDrawable[5][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), R.drawable.msg_round_play_m);
            chat_fileStatesDrawable[6][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), R.drawable.msg_round_pause_m);
            chat_fileStatesDrawable[6][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), R.drawable.msg_round_pause_m);
            chat_fileStatesDrawable[7][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), R.drawable.msg_round_load_m);
            chat_fileStatesDrawable[7][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), R.drawable.msg_round_load_m);
            chat_fileStatesDrawable[8][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), R.drawable.msg_round_file_s);
            chat_fileStatesDrawable[8][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), R.drawable.msg_round_file_s);
            chat_fileStatesDrawable[9][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), R.drawable.msg_round_cancel_m);
            chat_fileStatesDrawable[9][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), R.drawable.msg_round_cancel_m);
            chat_photoStatesDrawables[0][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), R.drawable.msg_round_load_m);
            chat_photoStatesDrawables[0][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), R.drawable.msg_round_load_m);
            chat_photoStatesDrawables[1][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), R.drawable.msg_round_cancel_m);
            chat_photoStatesDrawables[1][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), R.drawable.msg_round_cancel_m);
            chat_photoStatesDrawables[2][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), R.drawable.msg_round_gif_m);
            chat_photoStatesDrawables[2][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), R.drawable.msg_round_gif_m);
            chat_photoStatesDrawables[3][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), R.drawable.msg_round_play_m);
            chat_photoStatesDrawables[3][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), R.drawable.msg_round_play_m);
            Drawable[][] drawableArr = chat_photoStatesDrawables;
            Drawable[] drawableArr2 = drawableArr[4];
            Drawable[] drawableArr3 = drawableArr[4];
            Drawable drawable = resources.getDrawable(R.drawable.burn);
            drawableArr3[1] = drawable;
            drawableArr2[0] = drawable;
            Drawable[][] drawableArr4 = chat_photoStatesDrawables;
            Drawable[] drawableArr5 = drawableArr4[5];
            Drawable[] drawableArr6 = drawableArr4[5];
            Drawable drawable2 = resources.getDrawable(R.drawable.circle);
            drawableArr6[1] = drawable2;
            drawableArr5[0] = drawable2;
            Drawable[][] drawableArr7 = chat_photoStatesDrawables;
            Drawable[] drawableArr8 = drawableArr7[6];
            Drawable[] drawableArr9 = drawableArr7[6];
            Drawable drawable3 = resources.getDrawable(R.drawable.photocheck);
            drawableArr9[1] = drawable3;
            drawableArr8[0] = drawable3;
            chat_photoStatesDrawables[7][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), R.drawable.msg_round_load_m);
            chat_photoStatesDrawables[7][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), R.drawable.msg_round_load_m);
            chat_photoStatesDrawables[8][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), R.drawable.msg_round_cancel_m);
            chat_photoStatesDrawables[8][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), R.drawable.msg_round_cancel_m);
            chat_photoStatesDrawables[10][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), R.drawable.msg_round_load_m);
            chat_photoStatesDrawables[10][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), R.drawable.msg_round_load_m);
            chat_photoStatesDrawables[11][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), R.drawable.msg_round_cancel_m);
            chat_photoStatesDrawables[11][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), R.drawable.msg_round_cancel_m);
            chat_contactDrawable[0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), R.drawable.msg_contact);
            chat_contactDrawable[1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), R.drawable.msg_contact);
            chat_locationDrawable[0] = resources.getDrawable(R.drawable.msg_location).mutate();
            chat_locationDrawable[1] = resources.getDrawable(R.drawable.msg_location).mutate();
            chat_composeShadowDrawable = context.getResources().getDrawable(R.drawable.compose_panel_shadow).mutate();
            chat_composeShadowRoundDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
            try {
                int bitmapSize = AndroidUtilities.dp(6.0f) + AndroidUtilities.roundMessageSize;
                Bitmap bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                Paint eraserPaint = new Paint(1);
                eraserPaint.setColor(0);
                eraserPaint.setStyle(Paint.Style.FILL);
                eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                Paint paint2 = new Paint(1);
                paint2.setShadowLayer(AndroidUtilities.dp(4.0f), 0.0f, 0.0f, 1593835520);
                int a2 = 0;
                for (int i = 2; a2 < i; i = 2) {
                    canvas.drawCircle(bitmapSize / 2, bitmapSize / 2, (AndroidUtilities.roundMessageSize / i) - AndroidUtilities.dp(1.0f), a2 == 0 ? paint2 : eraserPaint);
                    a2++;
                }
                try {
                    canvas.setBitmap(null);
                } catch (Exception e) {
                }
                chat_roundVideoShadow = new BitmapDrawable(bitmap);
            } catch (Throwable th) {
            }
            defaultChatDrawables.clear();
            defaultChatDrawableColorKeys.clear();
            addChatDrawable(key_drawable_botInline, chat_botInlineDrawable, key_chat_serviceIcon);
            addChatDrawable(key_drawable_botWebView, chat_botWebViewDrawable, key_chat_serviceIcon);
            addChatDrawable(key_drawable_botLink, chat_botLinkDrawable, key_chat_serviceIcon);
            addChatDrawable(key_drawable_botInvite, chat_botInviteDrawable, key_chat_serviceIcon);
            addChatDrawable(key_drawable_goIcon, chat_goIconDrawable, key_chat_serviceIcon);
            addChatDrawable(key_drawable_commentSticker, chat_commentStickerDrawable, key_chat_serviceIcon);
            addChatDrawable(key_drawable_msgError, chat_msgErrorDrawable, key_chat_sentErrorIcon);
            addChatDrawable(key_drawable_msgIn, chat_msgInDrawable, null);
            addChatDrawable(key_drawable_msgInSelected, chat_msgInSelectedDrawable, null);
            addChatDrawable(key_drawable_msgInMedia, chat_msgInMediaDrawable, null);
            addChatDrawable(key_drawable_msgInMediaSelected, chat_msgInMediaSelectedDrawable, null);
            addChatDrawable(key_drawable_msgOut, chat_msgOutDrawable, null);
            addChatDrawable(key_drawable_msgOutSelected, chat_msgOutSelectedDrawable, null);
            addChatDrawable(key_drawable_msgOutMedia, chat_msgOutMediaDrawable, null);
            addChatDrawable(key_drawable_msgOutMediaSelected, chat_msgOutMediaSelectedDrawable, null);
            addChatDrawable(key_drawable_msgOutCallAudio, chat_msgOutCallDrawable[0], key_chat_outInstant);
            addChatDrawable(key_drawable_msgOutCallAudioSelected, chat_msgOutCallSelectedDrawable[0], key_chat_outInstantSelected);
            addChatDrawable("drawableMsgOutCallVideo", chat_msgOutCallDrawable[1], key_chat_outInstant);
            addChatDrawable("drawableMsgOutCallVideo", chat_msgOutCallSelectedDrawable[1], key_chat_outInstantSelected);
            addChatDrawable(key_drawable_msgOutCheck, chat_msgOutCheckDrawable, key_chat_outSentCheck);
            addChatDrawable(key_drawable_msgOutCheckSelected, chat_msgOutCheckSelectedDrawable, key_chat_outSentCheckSelected);
            addChatDrawable(key_drawable_msgOutCheckRead, chat_msgOutCheckReadDrawable, key_chat_outSentCheckRead);
            addChatDrawable(key_drawable_msgOutCheckReadSelected, chat_msgOutCheckReadSelectedDrawable, key_chat_outSentCheckReadSelected);
            addChatDrawable(key_drawable_msgOutHalfCheck, chat_msgOutHalfCheckDrawable, key_chat_outSentCheckRead);
            addChatDrawable(key_drawable_msgOutHalfCheckSelected, chat_msgOutHalfCheckSelectedDrawable, key_chat_outSentCheckReadSelected);
            addChatDrawable(key_drawable_msgOutInstant, chat_msgOutInstantDrawable, key_chat_outInstant);
            addChatDrawable(key_drawable_msgOutMenu, chat_msgOutMenuDrawable, key_chat_outMenu);
            addChatDrawable(key_drawable_msgOutMenuSelected, chat_msgOutMenuSelectedDrawable, key_chat_outMenuSelected);
            addChatDrawable(key_drawable_msgOutPinned, chat_msgOutPinnedDrawable, key_chat_outViews);
            addChatDrawable(key_drawable_msgOutPinnedSelected, chat_msgOutPinnedSelectedDrawable, key_chat_outViewsSelected);
            addChatDrawable("drawableMsgOutReplies", chat_msgOutRepliesDrawable, key_chat_outViews);
            addChatDrawable("drawableMsgOutReplies", chat_msgOutRepliesSelectedDrawable, key_chat_outViewsSelected);
            addChatDrawable(key_drawable_msgOutViews, chat_msgOutViewsDrawable, key_chat_outViews);
            addChatDrawable(key_drawable_msgOutViewsSelected, chat_msgOutViewsSelectedDrawable, key_chat_outViewsSelected);
            addChatDrawable(key_drawable_msgStickerCheck, chat_msgStickerCheckDrawable, key_chat_serviceText);
            addChatDrawable(key_drawable_msgStickerHalfCheck, chat_msgStickerHalfCheckDrawable, key_chat_serviceText);
            addChatDrawable(key_drawable_msgStickerPinned, chat_msgStickerPinnedDrawable, key_chat_serviceText);
            addChatDrawable(key_drawable_msgStickerReplies, chat_msgStickerRepliesDrawable, key_chat_serviceText);
            addChatDrawable(key_drawable_msgStickerViews, chat_msgStickerViewsDrawable, key_chat_serviceText);
            addChatDrawable(key_drawable_replyIcon, chat_replyIconDrawable, key_chat_serviceIcon);
            addChatDrawable(key_drawable_shareIcon, chat_shareIconDrawable, key_chat_serviceIcon);
            addChatDrawable(key_drawable_muteIconDrawable, chat_muteIconDrawable, key_chat_muteIcon);
            addChatDrawable(key_drawable_lockIconDrawable, chat_lockIconDrawable, key_chat_lockIcon);
            addChatDrawable(key_drawable_chat_pollHintDrawableOut, chat_pollHintDrawable[1], key_chat_outPreviewInstantText);
            addChatDrawable(key_drawable_chat_pollHintDrawableIn, chat_pollHintDrawable[0], key_chat_inPreviewInstantText);
            applyChatTheme(fontsOnly, false);
        }
        if (!fontsOnly && (paint = chat_botProgressPaint) != null) {
            paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
            chat_infoPaint.setTextSize(AndroidUtilities.dp(12.0f));
            chat_stickerCommentCountPaint.setTextSize(AndroidUtilities.dp(11.0f));
            chat_docNamePaint.setTextSize(AndroidUtilities.dp(15.0f));
            chat_locationTitlePaint.setTextSize(AndroidUtilities.dp(15.0f));
            chat_locationAddressPaint.setTextSize(AndroidUtilities.dp(13.0f));
            chat_audioTimePaint.setTextSize(AndroidUtilities.dp(12.0f));
            chat_livePaint.setTextSize(AndroidUtilities.dp(12.0f));
            chat_audioTitlePaint.setTextSize(AndroidUtilities.dp(16.0f));
            chat_audioPerformerPaint.setTextSize(AndroidUtilities.dp(15.0f));
            chat_botButtonPaint.setTextSize(AndroidUtilities.dp(15.0f));
            chat_contactNamePaint.setTextSize(AndroidUtilities.dp(15.0f));
            chat_contactPhonePaint.setTextSize(AndroidUtilities.dp(13.0f));
            chat_durationPaint.setTextSize(AndroidUtilities.dp(12.0f));
            chat_timePaint.setTextSize(AndroidUtilities.dp(12.0f));
            chat_adminPaint.setTextSize(AndroidUtilities.dp(13.0f));
            chat_namePaint.setTextSize(AndroidUtilities.dp(14.0f));
            chat_forwardNamePaint.setTextSize(AndroidUtilities.dp(14.0f));
            chat_replyNamePaint.setTextSize(AndroidUtilities.dp(14.0f));
            chat_replyTextPaint.setTextSize(AndroidUtilities.dp(14.0f));
            chat_gamePaint.setTextSize(AndroidUtilities.dp(13.0f));
            chat_shipmentPaint.setTextSize(AndroidUtilities.dp(13.0f));
            chat_instantViewPaint.setTextSize(AndroidUtilities.dp(13.0f));
            chat_instantViewRectPaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
            chat_pollTimerPaint.setStrokeWidth(AndroidUtilities.dp(1.1f));
            chat_actionTextPaint.setTextSize(AndroidUtilities.dp(Math.max(16, SharedConfig.fontSize) - 2));
            chat_contextResult_titleTextPaint.setTextSize(AndroidUtilities.dp(15.0f));
            chat_contextResult_descriptionTextPaint.setTextSize(AndroidUtilities.dp(13.0f));
            chat_radialProgressPaint.setStrokeWidth(AndroidUtilities.dp(3.0f));
            chat_radialProgress2Paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        }
    }

    public static void refreshAttachButtonsColors() {
        int a = 0;
        while (true) {
            RLottieDrawable[] rLottieDrawableArr = chat_attachButtonDrawables;
            if (a < rLottieDrawableArr.length) {
                if (rLottieDrawableArr[a] != null) {
                    rLottieDrawableArr[a].beginApplyLayerColors();
                    if (a == 0) {
                        chat_attachButtonDrawables[a].setLayerColor("Color_Mount.**", getNonAnimatedColor(key_chat_attachGalleryBackground));
                        chat_attachButtonDrawables[a].setLayerColor("Color_PhotoShadow.**", getNonAnimatedColor(key_chat_attachGalleryBackground));
                        chat_attachButtonDrawables[a].setLayerColor("White_Photo.**", getNonAnimatedColor(key_chat_attachGalleryIcon));
                        chat_attachButtonDrawables[a].setLayerColor("White_BackPhoto.**", getNonAnimatedColor(key_chat_attachGalleryIcon));
                    } else if (a == 1) {
                        chat_attachButtonDrawables[a].setLayerColor("White_Play1.**", getNonAnimatedColor(key_chat_attachAudioIcon));
                        chat_attachButtonDrawables[a].setLayerColor("White_Play2.**", getNonAnimatedColor(key_chat_attachAudioIcon));
                    } else if (a == 2) {
                        chat_attachButtonDrawables[a].setLayerColor("Color_Corner.**", getNonAnimatedColor(key_chat_attachFileBackground));
                        chat_attachButtonDrawables[a].setLayerColor("White_List.**", getNonAnimatedColor(key_chat_attachFileIcon));
                    } else if (a == 3) {
                        chat_attachButtonDrawables[a].setLayerColor("White_User1.**", getNonAnimatedColor(key_chat_attachContactIcon));
                        chat_attachButtonDrawables[a].setLayerColor("White_User2.**", getNonAnimatedColor(key_chat_attachContactIcon));
                    } else if (a == 4) {
                        chat_attachButtonDrawables[a].setLayerColor("Color_Oval.**", getNonAnimatedColor(key_chat_attachLocationBackground));
                        chat_attachButtonDrawables[a].setLayerColor("White_Pin.**", getNonAnimatedColor(key_chat_attachLocationIcon));
                    } else if (a == 5) {
                        chat_attachButtonDrawables[a].setLayerColor("White_Column 1.**", getNonAnimatedColor(key_chat_attachPollIcon));
                        chat_attachButtonDrawables[a].setLayerColor("White_Column 2.**", getNonAnimatedColor(key_chat_attachPollIcon));
                        chat_attachButtonDrawables[a].setLayerColor("White_Column 3.**", getNonAnimatedColor(key_chat_attachPollIcon));
                    }
                    chat_attachButtonDrawables[a].commitApplyLayerColors();
                }
                a++;
            } else {
                return;
            }
        }
    }

    public static void applyChatTheme(boolean fontsOnly, boolean bg) {
        int color;
        if (chat_msgTextPaint != null && chat_msgInDrawable != null && !fontsOnly) {
            chat_gamePaint.setColor(getColor(key_chat_previewGameText));
            chat_durationPaint.setColor(getColor(key_chat_previewDurationText));
            chat_botButtonPaint.setColor(getColor(key_chat_botButtonText));
            chat_urlPaint.setColor(getColor(key_chat_linkSelectBackground));
            chat_outUrlPaint.setColor(getColor(key_chat_outLinkSelectBackground));
            chat_botProgressPaint.setColor(getColor(key_chat_botProgress));
            chat_deleteProgressPaint.setColor(getColor(key_chat_secretTimeText));
            chat_textSearchSelectionPaint.setColor(getColor(key_chat_textSelectBackground));
            chat_msgErrorPaint.setColor(getColor(key_chat_sentError));
            chat_statusPaint.setColor(getColor(key_chat_status));
            chat_statusRecordPaint.setColor(getColor(key_chat_status));
            chat_actionTextPaint.setColor(getColor(key_chat_serviceText));
            chat_actionTextPaint.linkColor = getColor(key_chat_serviceLink);
            chat_contextResult_titleTextPaint.setColor(getColor(key_windowBackgroundWhiteBlackText));
            chat_composeBackgroundPaint.setColor(getColor(key_chat_messagePanelBackground));
            chat_timeBackgroundPaint.setColor(getColor(key_chat_mediaTimeBackground));
            setDrawableColorByKey(chat_msgNoSoundDrawable, key_chat_mediaTimeText);
            setDrawableColorByKey(chat_msgInDrawable, key_chat_inBubble);
            setDrawableColorByKey(chat_msgInSelectedDrawable, key_chat_inBubbleSelected);
            setDrawableColorByKey(chat_msgInMediaDrawable, key_chat_inBubble);
            setDrawableColorByKey(chat_msgInMediaSelectedDrawable, key_chat_inBubbleSelected);
            setDrawableColorByKey(chat_msgOutCheckDrawable, key_chat_outSentCheck);
            setDrawableColorByKey(chat_msgOutCheckSelectedDrawable, key_chat_outSentCheckSelected);
            setDrawableColorByKey(chat_msgOutCheckReadDrawable, key_chat_outSentCheckRead);
            setDrawableColorByKey(chat_msgOutCheckReadSelectedDrawable, key_chat_outSentCheckReadSelected);
            setDrawableColorByKey(chat_msgOutHalfCheckDrawable, key_chat_outSentCheckRead);
            setDrawableColorByKey(chat_msgOutHalfCheckSelectedDrawable, key_chat_outSentCheckReadSelected);
            setDrawableColorByKey(chat_msgMediaCheckDrawable, key_chat_mediaSentCheck);
            setDrawableColorByKey(chat_msgMediaHalfCheckDrawable, key_chat_mediaSentCheck);
            setDrawableColorByKey(chat_msgStickerCheckDrawable, key_chat_serviceText);
            setDrawableColorByKey(chat_msgStickerHalfCheckDrawable, key_chat_serviceText);
            setDrawableColorByKey(chat_msgStickerViewsDrawable, key_chat_serviceText);
            setDrawableColorByKey(chat_msgStickerRepliesDrawable, key_chat_serviceText);
            setDrawableColorByKey(chat_shareIconDrawable, key_chat_serviceIcon);
            setDrawableColorByKey(chat_replyIconDrawable, key_chat_serviceIcon);
            setDrawableColorByKey(chat_goIconDrawable, key_chat_serviceIcon);
            setDrawableColorByKey(chat_botInlineDrawable, key_chat_serviceIcon);
            setDrawableColorByKey(chat_botWebViewDrawable, key_chat_serviceIcon);
            setDrawableColorByKey(chat_botInviteDrawable, key_chat_serviceIcon);
            setDrawableColorByKey(chat_botLinkDrawable, key_chat_serviceIcon);
            setDrawableColorByKey(chat_msgInViewsDrawable, key_chat_inViews);
            setDrawableColorByKey(chat_msgInViewsSelectedDrawable, key_chat_inViewsSelected);
            setDrawableColorByKey(chat_msgOutViewsDrawable, key_chat_outViews);
            setDrawableColorByKey(chat_msgOutViewsSelectedDrawable, key_chat_outViewsSelected);
            setDrawableColorByKey(chat_msgInRepliesDrawable, key_chat_inViews);
            setDrawableColorByKey(chat_msgInRepliesSelectedDrawable, key_chat_inViewsSelected);
            setDrawableColorByKey(chat_msgOutRepliesDrawable, key_chat_outViews);
            setDrawableColorByKey(chat_msgOutRepliesSelectedDrawable, key_chat_outViewsSelected);
            setDrawableColorByKey(chat_msgInPinnedDrawable, key_chat_inViews);
            setDrawableColorByKey(chat_msgInPinnedSelectedDrawable, key_chat_inViewsSelected);
            setDrawableColorByKey(chat_msgOutPinnedDrawable, key_chat_outViews);
            setDrawableColorByKey(chat_msgOutPinnedSelectedDrawable, key_chat_outViewsSelected);
            setDrawableColorByKey(chat_msgMediaPinnedDrawable, key_chat_mediaViews);
            setDrawableColorByKey(chat_msgStickerPinnedDrawable, key_chat_serviceText);
            setDrawableColorByKey(chat_msgMediaViewsDrawable, key_chat_mediaViews);
            setDrawableColorByKey(chat_msgMediaRepliesDrawable, key_chat_mediaViews);
            setDrawableColorByKey(chat_msgInMenuDrawable, key_chat_inMenu);
            setDrawableColorByKey(chat_msgInMenuSelectedDrawable, key_chat_inMenuSelected);
            setDrawableColorByKey(chat_msgOutMenuDrawable, key_chat_outMenu);
            setDrawableColorByKey(chat_msgOutMenuSelectedDrawable, key_chat_outMenuSelected);
            setDrawableColorByKey(chat_msgMediaMenuDrawable, key_chat_mediaMenu);
            setDrawableColorByKey(chat_msgOutInstantDrawable, key_chat_outInstant);
            setDrawableColorByKey(chat_msgInInstantDrawable, key_chat_inInstant);
            setDrawableColorByKey(chat_msgErrorDrawable, key_chat_sentErrorIcon);
            setDrawableColorByKey(chat_muteIconDrawable, key_chat_muteIcon);
            setDrawableColorByKey(chat_lockIconDrawable, key_chat_lockIcon);
            setDrawableColorByKey(chat_inlineResultFile, key_chat_inlineResultIcon);
            setDrawableColorByKey(chat_inlineResultAudio, key_chat_inlineResultIcon);
            setDrawableColorByKey(chat_inlineResultLocation, key_chat_inlineResultIcon);
            setDrawableColorByKey(chat_commentDrawable, key_chat_inInstant);
            setDrawableColorByKey(chat_commentStickerDrawable, key_chat_serviceIcon);
            setDrawableColorByKey(chat_commentArrowDrawable, key_chat_inInstant);
            for (int a = 0; a < 2; a++) {
                setDrawableColorByKey(chat_msgInCallDrawable[a], key_chat_inInstant);
                setDrawableColorByKey(chat_msgInCallSelectedDrawable[a], key_chat_inInstantSelected);
                setDrawableColorByKey(chat_msgOutCallDrawable[a], key_chat_outInstant);
                setDrawableColorByKey(chat_msgOutCallSelectedDrawable[a], key_chat_outInstantSelected);
            }
            setDrawableColorByKey(chat_msgCallUpGreenDrawable, key_chat_outGreenCall);
            setDrawableColorByKey(chat_msgCallDownRedDrawable, key_chat_inRedCall);
            setDrawableColorByKey(chat_msgCallDownGreenDrawable, key_chat_inGreenCall);
            setDrawableColorByKey(calllog_msgCallUpRedDrawable, key_calls_callReceivedRedIcon);
            setDrawableColorByKey(calllog_msgCallUpGreenDrawable, key_calls_callReceivedGreenIcon);
            setDrawableColorByKey(calllog_msgCallDownRedDrawable, key_calls_callReceivedRedIcon);
            setDrawableColorByKey(calllog_msgCallDownGreenDrawable, key_calls_callReceivedGreenIcon);
            int i = 0;
            while (true) {
                StatusDrawable[] statusDrawableArr = chat_status_drawables;
                if (i >= statusDrawableArr.length) {
                    break;
                }
                setDrawableColorByKey(statusDrawableArr[i], key_chats_actionMessage);
                i++;
            }
            for (int a2 = 0; a2 < 5; a2++) {
                setCombinedDrawableColor(chat_fileStatesDrawable[a2][0], getColor(key_chat_outLoader), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[a2][0], getColor(key_chat_outMediaIcon), true);
                setCombinedDrawableColor(chat_fileStatesDrawable[a2][1], getColor(key_chat_outLoaderSelected), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[a2][1], getColor(key_chat_outMediaIconSelected), true);
                setCombinedDrawableColor(chat_fileStatesDrawable[a2 + 5][0], getColor(key_chat_inLoader), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[a2 + 5][0], getColor(key_chat_inMediaIcon), true);
                setCombinedDrawableColor(chat_fileStatesDrawable[a2 + 5][1], getColor(key_chat_inLoaderSelected), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[a2 + 5][1], getColor(key_chat_inMediaIconSelected), true);
            }
            for (int a3 = 0; a3 < 4; a3++) {
                setCombinedDrawableColor(chat_photoStatesDrawables[a3][0], getColor(key_chat_mediaLoaderPhoto), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[a3][0], getColor(key_chat_mediaLoaderPhotoIcon), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[a3][1], getColor(key_chat_mediaLoaderPhotoSelected), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[a3][1], getColor(key_chat_mediaLoaderPhotoIconSelected), true);
            }
            for (int a4 = 0; a4 < 2; a4++) {
                setCombinedDrawableColor(chat_photoStatesDrawables[a4 + 7][0], getColor(key_chat_outLoaderPhoto), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[a4 + 7][0], getColor(key_chat_outLoaderPhotoIcon), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[a4 + 7][1], getColor(key_chat_outLoaderPhotoSelected), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[a4 + 7][1], getColor(key_chat_outLoaderPhotoIconSelected), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[a4 + 10][0], getColor(key_chat_inLoaderPhoto), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[a4 + 10][0], getColor(key_chat_inLoaderPhotoIcon), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[a4 + 10][1], getColor(key_chat_inLoaderPhotoSelected), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[a4 + 10][1], getColor(key_chat_inLoaderPhotoIconSelected), true);
            }
            setCombinedDrawableColor(chat_contactDrawable[0], getColor(key_chat_inContactBackground), false);
            setCombinedDrawableColor(chat_contactDrawable[0], getColor(key_chat_inContactIcon), true);
            setCombinedDrawableColor(chat_contactDrawable[1], getColor(key_chat_outContactBackground), false);
            setCombinedDrawableColor(chat_contactDrawable[1], getColor(key_chat_outContactIcon), true);
            setDrawableColor(chat_locationDrawable[0], getColor(key_chat_inLocationIcon));
            setDrawableColor(chat_locationDrawable[1], getColor(key_chat_outLocationIcon));
            setDrawableColor(chat_pollHintDrawable[0], getColor(key_chat_inPreviewInstantText));
            setDrawableColor(chat_pollHintDrawable[1], getColor(key_chat_outPreviewInstantText));
            setDrawableColor(chat_psaHelpDrawable[0], getColor(key_chat_inViews));
            setDrawableColor(chat_psaHelpDrawable[1], getColor(key_chat_outViews));
            setDrawableColorByKey(chat_composeShadowDrawable, key_chat_messagePanelShadow);
            setDrawableColorByKey(chat_composeShadowRoundDrawable, key_chat_messagePanelBackground);
            if (getColor(key_chat_outAudioSeekbarFill) == -1) {
                color = getColor(key_chat_outBubble);
            } else {
                color = -1;
            }
            setDrawableColor(chat_pollCheckDrawable[1], color);
            setDrawableColor(chat_pollCrossDrawable[1], color);
            setDrawableColor(chat_attachEmptyDrawable, getColor(key_chat_attachEmptyImage));
            if (!bg) {
                applyChatServiceMessageColor();
                applyChatMessageSelectedBackgroundColor();
            }
            refreshAttachButtonsColors();
        }
    }

    public static void applyChatServiceMessageColor() {
        applyChatServiceMessageColor(null, null, wallpaper);
    }

    public static boolean hasGradientService() {
        return serviceBitmapShader != null;
    }

    public static void applyServiceShaderMatrixForView(View view, View background) {
        if (view == null || background == null) {
            return;
        }
        view.getLocationOnScreen(viewPos);
        int[] iArr = viewPos;
        int x = iArr[0];
        int y = iArr[1];
        background.getLocationOnScreen(iArr);
        applyServiceShaderMatrix(background.getMeasuredWidth(), background.getMeasuredHeight(), x, y - viewPos[1]);
    }

    public static void applyServiceShaderMatrix(int w, int h, float translationX, float translationY) {
        applyServiceShaderMatrix(serviceBitmap, serviceBitmapShader, serviceBitmapMatrix, w, h, translationX, translationY);
    }

    public static void applyServiceShaderMatrix(Bitmap bitmap, BitmapShader shader, Matrix matrix, int w, int h, float translationX, float translationY) {
        if (shader == null) {
            return;
        }
        float bitmapWidth = bitmap.getWidth();
        float bitmapHeight = bitmap.getHeight();
        float maxScale = Math.max(w / bitmapWidth, h / bitmapHeight);
        float width = bitmapWidth * maxScale;
        float height = bitmapHeight * maxScale;
        float x = (w - width) / 2.0f;
        float y = (h - height) / 2.0f;
        matrix.reset();
        matrix.setTranslate(x - translationX, y - translationY);
        matrix.preScale(maxScale, maxScale);
        shader.setLocalMatrix(matrix);
    }

    public static void applyChatServiceMessageColor(int[] custom, Drawable wallpaperOverride) {
        applyChatServiceMessageColor(custom, wallpaperOverride, wallpaper);
    }

    public static void applyChatServiceMessageColor(int[] custom, Drawable wallpaperOverride, Drawable currentWallpaper) {
        Integer servicePressedColor;
        Integer serviceColor;
        if (chat_actionBackgroundPaint == null) {
            return;
        }
        serviceMessageColor = serviceMessageColorBackup;
        serviceSelectedMessageColor = serviceSelectedMessageColorBackup;
        boolean drawServiceGradient = true;
        if (custom == null || custom.length < 2) {
            serviceColor = currentColors.get(key_chat_serviceBackground);
            servicePressedColor = currentColors.get(key_chat_serviceBackgroundSelected);
        } else {
            serviceColor = Integer.valueOf(custom[0]);
            servicePressedColor = Integer.valueOf(custom[1]);
            serviceMessageColor = custom[0];
            serviceSelectedMessageColor = custom[1];
        }
        Integer serviceColor2 = serviceColor;
        Integer servicePressedColor2 = servicePressedColor;
        if (serviceColor == null) {
            serviceColor = Integer.valueOf(serviceMessageColor);
            serviceColor2 = Integer.valueOf(serviceMessage2Color);
        }
        if (servicePressedColor == null) {
            servicePressedColor = Integer.valueOf(serviceSelectedMessageColor);
        }
        if (servicePressedColor2 == null) {
            Integer.valueOf(serviceSelectedMessage2Color);
        }
        Drawable drawable = wallpaperOverride != null ? wallpaperOverride : currentWallpaper;
        if (!(drawable instanceof MotionBackgroundDrawable) || SharedConfig.getDevicePerformanceClass() == 0) {
            drawServiceGradient = false;
        }
        if (drawServiceGradient) {
            Bitmap newBitmap = ((MotionBackgroundDrawable) drawable).getBitmap();
            if (serviceBitmap != newBitmap) {
                serviceBitmap = newBitmap;
                serviceBitmapShader = new BitmapShader(serviceBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                if (serviceBitmapMatrix == null) {
                    serviceBitmapMatrix = new Matrix();
                }
            }
            setDrawableColor(chat_msgStickerPinnedDrawable, -1);
            setDrawableColor(chat_msgStickerCheckDrawable, -1);
            setDrawableColor(chat_msgStickerHalfCheckDrawable, -1);
            setDrawableColor(chat_msgStickerViewsDrawable, -1);
            setDrawableColor(chat_msgStickerRepliesDrawable, -1);
            chat_actionTextPaint.setColor(-1);
            chat_actionTextPaint.linkColor = -1;
            chat_botButtonPaint.setColor(-1);
            setDrawableColor(chat_commentStickerDrawable, -1);
            setDrawableColor(chat_shareIconDrawable, -1);
            setDrawableColor(chat_replyIconDrawable, -1);
            setDrawableColor(chat_goIconDrawable, -1);
            setDrawableColor(chat_botInlineDrawable, -1);
            setDrawableColor(chat_botWebViewDrawable, -1);
            setDrawableColor(chat_botInviteDrawable, -1);
            setDrawableColor(chat_botLinkDrawable, -1);
        } else {
            serviceBitmap = null;
            serviceBitmapShader = null;
            setDrawableColorByKey(chat_msgStickerPinnedDrawable, key_chat_serviceText);
            setDrawableColorByKey(chat_msgStickerCheckDrawable, key_chat_serviceText);
            setDrawableColorByKey(chat_msgStickerHalfCheckDrawable, key_chat_serviceText);
            setDrawableColorByKey(chat_msgStickerViewsDrawable, key_chat_serviceText);
            setDrawableColorByKey(chat_msgStickerRepliesDrawable, key_chat_serviceText);
            chat_actionTextPaint.setColor(getColor(key_chat_serviceText));
            chat_actionTextPaint.linkColor = getColor(key_chat_serviceLink);
            setDrawableColorByKey(chat_commentStickerDrawable, key_chat_serviceIcon);
            setDrawableColorByKey(chat_shareIconDrawable, key_chat_serviceIcon);
            setDrawableColorByKey(chat_replyIconDrawable, key_chat_serviceIcon);
            setDrawableColorByKey(chat_goIconDrawable, key_chat_serviceIcon);
            setDrawableColorByKey(chat_botInlineDrawable, key_chat_serviceIcon);
            setDrawableColorByKey(chat_botWebViewDrawable, key_chat_serviceIcon);
            setDrawableColorByKey(chat_botInviteDrawable, key_chat_serviceIcon);
            setDrawableColorByKey(chat_botLinkDrawable, key_chat_serviceIcon);
            chat_botButtonPaint.setColor(getColor(key_chat_botButtonText));
        }
        chat_actionBackgroundPaint.setColor(serviceColor.intValue());
        chat_actionBackgroundSelectedPaint.setColor(servicePressedColor.intValue());
        chat_actionBackgroundPaint2.setColor(serviceColor2.intValue());
        currentColor = serviceColor.intValue();
        if (serviceBitmapShader == null || (currentColors.get(key_chat_serviceBackground) != null && !(drawable instanceof MotionBackgroundDrawable))) {
            chat_actionBackgroundPaint.setColorFilter(null);
            chat_actionBackgroundPaint.setShader(null);
            chat_actionBackgroundSelectedPaint.setColorFilter(null);
            chat_actionBackgroundSelectedPaint.setShader(null);
            return;
        }
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(((MotionBackgroundDrawable) drawable).getIntensity() >= 0 ? 1.8f : 0.5f);
        chat_actionBackgroundPaint.setShader(serviceBitmapShader);
        chat_actionBackgroundPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        chat_actionBackgroundPaint.setAlpha(127);
        chat_actionBackgroundSelectedPaint.setShader(serviceBitmapShader);
        chat_actionBackgroundSelectedPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        chat_actionBackgroundSelectedPaint.setAlpha(200);
    }

    public static void applyChatMessageSelectedBackgroundColor() {
        applyChatMessageSelectedBackgroundColor(null, wallpaper);
    }

    public static void applyChatMessageSelectedBackgroundColor(Drawable wallpaperOverride) {
        applyChatMessageSelectedBackgroundColor(wallpaperOverride, wallpaper);
    }

    public static void applyChatMessageSelectedBackgroundColor(Drawable wallpaperOverride, Drawable currentWallpaper) {
        Bitmap newBitmap;
        if (chat_messageBackgroundSelectedPaint == null) {
            return;
        }
        Integer selectedBackgroundColor = currentColors.get(key_chat_selectedBackground);
        Drawable drawable = wallpaperOverride != null ? wallpaperOverride : currentWallpaper;
        boolean drawSelectedGradient = (drawable instanceof MotionBackgroundDrawable) && SharedConfig.getDevicePerformanceClass() != 0 && selectedBackgroundColor == null;
        if (drawSelectedGradient && serviceBitmap != (newBitmap = ((MotionBackgroundDrawable) drawable).getBitmap())) {
            serviceBitmap = newBitmap;
            serviceBitmapShader = new BitmapShader(serviceBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            if (serviceBitmapMatrix == null) {
                serviceBitmapMatrix = new Matrix();
            }
        }
        if (serviceBitmapShader != null && selectedBackgroundColor == null && drawSelectedGradient) {
            ColorMatrix colorMatrix2 = new ColorMatrix();
            AndroidUtilities.adjustSaturationColorMatrix(colorMatrix2, 2.5f);
            AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix2, 0.75f);
            chat_messageBackgroundSelectedPaint.setShader(serviceBitmapShader);
            chat_messageBackgroundSelectedPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix2));
            chat_messageBackgroundSelectedPaint.setAlpha(64);
            return;
        }
        chat_messageBackgroundSelectedPaint.setColor(selectedBackgroundColor == null ? C.BUFFER_FLAG_ENCRYPTED : selectedBackgroundColor.intValue());
        chat_messageBackgroundSelectedPaint.setColorFilter(null);
        chat_messageBackgroundSelectedPaint.setShader(null);
    }

    public static void createProfileResources(Context context) {
        if (profile_verifiedDrawable == null) {
            profile_aboutTextPaint = new TextPaint(1);
            Resources resources = context.getResources();
            profile_verifiedDrawable = resources.getDrawable(R.drawable.verified_area).mutate();
            profile_verifiedCheckDrawable = resources.getDrawable(R.drawable.verified_check).mutate();
            applyProfileTheme();
        }
        profile_aboutTextPaint.setTextSize(AndroidUtilities.dp(16.0f));
    }

    public static ColorFilter getShareColorFilter(int color, boolean selected) {
        if (selected) {
            if (currentShareSelectedColorFilter == null || currentShareSelectedColorFilterColor != color) {
                currentShareSelectedColorFilterColor = color;
                currentShareSelectedColorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
            }
            return currentShareSelectedColorFilter;
        }
        if (currentShareColorFilter == null || currentShareColorFilterColor != color) {
            currentShareColorFilterColor = color;
            currentShareColorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
        }
        return currentShareColorFilter;
    }

    public static void applyProfileTheme() {
        if (profile_verifiedDrawable == null) {
            return;
        }
        profile_aboutTextPaint.setColor(getColor(key_windowBackgroundWhiteBlackText));
        profile_aboutTextPaint.linkColor = getColor(key_windowBackgroundWhiteLinkText);
        setDrawableColorByKey(profile_verifiedDrawable, key_profile_verifiedBackground);
        setDrawableColorByKey(profile_verifiedCheckDrawable, key_profile_verifiedCheck);
    }

    public static Drawable getThemedDrawable(Context context, int resId, String key) {
        return getThemedDrawable(context, resId, getColor(key));
    }

    public static Drawable getThemedDrawable(Context context, int resId, int color) {
        if (context == null) {
            return null;
        }
        Drawable drawable = context.getResources().getDrawable(resId).mutate();
        drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        return drawable;
    }

    public static int getDefaultColor(String key) {
        Integer value = defaultColors.get(key);
        if (value == null) {
            if (key.equals(key_chats_menuTopShadow) || key.equals(key_chats_menuTopBackground) || key.equals(key_chats_menuTopShadowCats) || key.equals(key_chat_wallpaper_gradient_to2) || key.equals(key_chat_wallpaper_gradient_to3)) {
                return 0;
            }
            return SupportMenu.CATEGORY_MASK;
        }
        return value.intValue();
    }

    public static boolean hasThemeKey(String key) {
        return currentColors.containsKey(key);
    }

    public static Integer getColorOrNull(String key) {
        Integer color = currentColors.get(key);
        if (color == null) {
            String fallbackKey = fallbackKeys.get(key);
            if (fallbackKey != null) {
                color = currentColors.get(key);
            }
            if (color == null) {
                color = defaultColors.get(key);
            }
        }
        if (color != null) {
            if (key_windowBackgroundWhite.equals(key) || key_windowBackgroundGray.equals(key) || key_actionBarDefault.equals(key) || key_actionBarDefaultArchived.equals(key)) {
                return Integer.valueOf(color.intValue() | (-16777216));
            }
            return color;
        }
        return color;
    }

    public static void setAnimatingColor(boolean animating) {
        animatingColors = animating ? new HashMap<>() : null;
    }

    public static boolean isAnimatingColor() {
        return animatingColors != null;
    }

    public static void setAnimatedColor(String key, int value) {
        HashMap<String, Integer> hashMap = animatingColors;
        if (hashMap == null) {
            return;
        }
        hashMap.put(key, Integer.valueOf(value));
    }

    public static int getDefaultAccentColor(String key) {
        ThemeAccent accent;
        Integer color = currentColorsNoAccent.get(key);
        if (color == null || (accent = currentTheme.getAccent(false)) == null) {
            return 0;
        }
        float[] hsvTemp1 = getTempHsv(1);
        float[] hsvTemp2 = getTempHsv(2);
        Color.colorToHSV(currentTheme.accentBaseColor, hsvTemp1);
        Color.colorToHSV(accent.accentColor, hsvTemp2);
        return changeColorAccent(hsvTemp1, hsvTemp2, color.intValue(), currentTheme.isDark());
    }

    public static int getNonAnimatedColor(String key) {
        return getColor(key, null, true);
    }

    public static int getColor(String key, ResourcesProvider provider) {
        Integer colorInteger;
        if (provider != null && (colorInteger = provider.getColor(key)) != null) {
            return colorInteger.intValue();
        }
        return getColor(key);
    }

    public static int getColor(String key) {
        return getColor(key, null, false);
    }

    public static int getColor(String key, boolean[] isDefault) {
        return getColor(key, isDefault, false);
    }

    public static int getColor(String key, boolean[] isDefault, boolean ignoreAnimation) {
        boolean useDefault;
        HashMap<String, Integer> hashMap;
        Integer color;
        if (!ignoreAnimation && (hashMap = animatingColors) != null && (color = hashMap.get(key)) != null) {
            return color.intValue();
        }
        if (serviceBitmapShader != null && (key_chat_serviceText.equals(key) || key_chat_serviceLink.equals(key) || key_chat_serviceIcon.equals(key) || key_chat_stickerReplyLine.equals(key) || key_chat_stickerReplyNameText.equals(key) || key_chat_stickerReplyMessageText.equals(key))) {
            return -1;
        }
        if (currentTheme == defaultTheme) {
            if (myMessagesBubblesColorKeys.contains(key)) {
                useDefault = currentTheme.isDefaultMyMessagesBubbles();
            } else {
                useDefault = myMessagesColorKeys.contains(key) ? currentTheme.isDefaultMyMessages() : (key_chat_wallpaper.equals(key) || key_chat_wallpaper_gradient_to1.equals(key) || key_chat_wallpaper_gradient_to2.equals(key) || key_chat_wallpaper_gradient_to3.equals(key)) ? false : currentTheme.isDefaultMainAccent();
            }
            if (useDefault) {
                if (key.equals(key_chat_serviceBackground)) {
                    return serviceMessageColor;
                }
                if (key.equals(key_chat_serviceBackgroundSelected)) {
                    return serviceSelectedMessageColor;
                }
                return getDefaultColor(key);
            }
        }
        Integer color2 = currentColors.get(key);
        if (color2 == null) {
            String fallbackKey = fallbackKeys.get(key);
            if (fallbackKey != null) {
                color2 = currentColors.get(fallbackKey);
            }
            if (color2 == null) {
                if (isDefault != null) {
                    isDefault[0] = true;
                }
                if (key.equals(key_chat_serviceBackground)) {
                    return serviceMessageColor;
                }
                if (key.equals(key_chat_serviceBackgroundSelected)) {
                    return serviceSelectedMessageColor;
                }
                return getDefaultColor(key);
            }
        }
        if (key_windowBackgroundWhite.equals(key) || key_windowBackgroundGray.equals(key) || key_actionBarDefault.equals(key) || key_actionBarDefaultArchived.equals(key)) {
            color2 = Integer.valueOf(color2.intValue() | (-16777216));
        }
        return color2.intValue();
    }

    public static void setColor(String key, int color, boolean useDefault) {
        if (key.equals(key_chat_wallpaper) || key.equals(key_chat_wallpaper_gradient_to1) || key.equals(key_chat_wallpaper_gradient_to2) || key.equals(key_chat_wallpaper_gradient_to3) || key.equals(key_windowBackgroundWhite) || key.equals(key_windowBackgroundGray) || key.equals(key_actionBarDefault) || key.equals(key_actionBarDefaultArchived)) {
            color |= -16777216;
        }
        if (useDefault) {
            currentColors.remove(key);
        } else {
            currentColors.put(key, Integer.valueOf(color));
        }
        char c = 65535;
        switch (key.hashCode()) {
            case -2095843767:
                if (key.equals(key_chat_wallpaper_gradient_rotation)) {
                    c = 7;
                    break;
                }
                break;
            case -1625862693:
                if (key.equals(key_chat_wallpaper)) {
                    c = 3;
                    break;
                }
                break;
            case -1397026623:
                if (key.equals(key_windowBackgroundGray)) {
                    c = '\t';
                    break;
                }
                break;
            case -633951866:
                if (key.equals(key_chat_wallpaper_gradient_to1)) {
                    c = 4;
                    break;
                }
                break;
            case -552118908:
                if (key.equals(key_actionBarDefault)) {
                    c = '\b';
                    break;
                }
                break;
            case -391617936:
                if (key.equals(key_chat_selectedBackground)) {
                    c = 0;
                    break;
                }
                break;
            case 426061980:
                if (key.equals(key_chat_serviceBackground)) {
                    c = 1;
                    break;
                }
                break;
            case 1381936524:
                if (key.equals(key_chat_wallpaper_gradient_to2)) {
                    c = 5;
                    break;
                }
                break;
            case 1381936525:
                if (key.equals(key_chat_wallpaper_gradient_to3)) {
                    c = 6;
                    break;
                }
                break;
            case 1573464919:
                if (key.equals(key_chat_serviceBackgroundSelected)) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                applyChatMessageSelectedBackgroundColor();
                return;
            case 1:
            case 2:
                applyChatServiceMessageColor();
                return;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                reloadWallpaper();
                return;
            case '\b':
                if (Build.VERSION.SDK_INT >= 23) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, new Object[0]);
                    return;
                }
                return;
            case '\t':
                if (Build.VERSION.SDK_INT >= 26) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, new Object[0]);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public static void setDefaultColor(String key, int color) {
        defaultColors.put(key, Integer.valueOf(color));
    }

    public static void setThemeWallpaper(ThemeInfo themeInfo, Bitmap bitmap, File path) {
        currentColors.remove(key_chat_wallpaper);
        currentColors.remove(key_chat_wallpaper_gradient_to1);
        currentColors.remove(key_chat_wallpaper_gradient_to2);
        currentColors.remove(key_chat_wallpaper_gradient_to3);
        currentColors.remove(key_chat_wallpaper_gradient_rotation);
        themedWallpaperLink = null;
        themeInfo.setOverrideWallpaper(null);
        if (bitmap != null) {
            themedWallpaper = new BitmapDrawable(bitmap);
            saveCurrentTheme(themeInfo, false, false, false);
            calcBackgroundColor(themedWallpaper, 0);
            applyChatServiceMessageColor();
            applyChatMessageSelectedBackgroundColor();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
            return;
        }
        themedWallpaper = null;
        wallpaper = null;
        saveCurrentTheme(themeInfo, false, false, false);
        reloadWallpaper();
    }

    public static void setDrawableColor(Drawable drawable, int color) {
        if (drawable == null) {
            return;
        }
        if (drawable instanceof StatusDrawable) {
            ((StatusDrawable) drawable).setColor(color);
        } else if (drawable instanceof MsgClockDrawable) {
            ((MsgClockDrawable) drawable).setColor(color);
        } else if (drawable instanceof ShapeDrawable) {
            ((ShapeDrawable) drawable).getPaint().setColor(color);
        } else if (drawable instanceof ScamDrawable) {
            ((ScamDrawable) drawable).setColor(color);
        } else {
            drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }
    }

    public static void setDrawableColorByKey(Drawable drawable, String key) {
        if (key == null) {
            return;
        }
        setDrawableColor(drawable, getColor(key));
    }

    public static void setEmojiDrawableColor(Drawable drawable, int color, boolean selected) {
        Drawable state;
        if (drawable instanceof StateListDrawable) {
            try {
                if (selected) {
                    state = getStateDrawable(drawable, 0);
                } else {
                    state = getStateDrawable(drawable, 1);
                }
                if (state instanceof ShapeDrawable) {
                    ((ShapeDrawable) state).getPaint().setColor(color);
                } else {
                    state.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                }
            } catch (Throwable th) {
            }
        }
    }

    public static void setRippleDrawableForceSoftware(RippleDrawable drawable) {
        if (drawable == null) {
            return;
        }
        try {
            Method method = RippleDrawable.class.getDeclaredMethod("setForceSoftware", Boolean.TYPE);
            method.invoke(drawable, true);
        } catch (Throwable th) {
        }
    }

    public static void setSelectorDrawableColor(Drawable drawable, int color, boolean selected) {
        Drawable state;
        if (drawable instanceof StateListDrawable) {
            try {
                if (selected) {
                    Drawable state2 = getStateDrawable(drawable, 0);
                    if (state2 instanceof ShapeDrawable) {
                        ((ShapeDrawable) state2).getPaint().setColor(color);
                    } else {
                        state2.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                    }
                    state = getStateDrawable(drawable, 1);
                } else {
                    state = getStateDrawable(drawable, 2);
                }
                if (state instanceof ShapeDrawable) {
                    ((ShapeDrawable) state).getPaint().setColor(color);
                } else {
                    state.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                }
            } catch (Throwable th) {
            }
        } else if (Build.VERSION.SDK_INT >= 21 && (drawable instanceof RippleDrawable)) {
            RippleDrawable rippleDrawable = (RippleDrawable) drawable;
            if (selected) {
                rippleDrawable.setColor(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{color}));
            } else if (rippleDrawable.getNumberOfLayers() > 0) {
                Drawable drawable1 = rippleDrawable.getDrawable(0);
                if (drawable1 instanceof ShapeDrawable) {
                    ((ShapeDrawable) drawable1).getPaint().setColor(color);
                } else {
                    drawable1.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                }
            }
        }
    }

    public static boolean isThemeWallpaperPublic() {
        return !TextUtils.isEmpty(themedWallpaperLink);
    }

    public static boolean hasWallpaperFromTheme() {
        if (!currentTheme.firstAccentIsDefault || currentTheme.currentAccentId != DEFALT_THEME_ACCENT_ID) {
            return currentColors.containsKey(key_chat_wallpaper) || themedWallpaperFileOffset > 0 || !TextUtils.isEmpty(themedWallpaperLink);
        }
        return false;
    }

    public static boolean isCustomTheme() {
        return isCustomTheme;
    }

    public static void reloadWallpaper() {
        BackgroundGradientDrawable.Disposable disposable = backgroundGradientDisposable;
        if (disposable != null) {
            disposable.dispose();
            backgroundGradientDisposable = null;
        }
        Drawable drawable = wallpaper;
        if (drawable instanceof MotionBackgroundDrawable) {
            previousPhase = ((MotionBackgroundDrawable) drawable).getPhase();
        } else {
            previousPhase = 0;
        }
        wallpaper = null;
        themedWallpaper = null;
        loadWallpaper();
    }

    private static void calcBackgroundColor(Drawable drawable, int save) {
        if (save != 2) {
            int[] result = AndroidUtilities.calcDrawableColor(drawable);
            int i = result[0];
            serviceMessageColorBackup = i;
            serviceMessageColor = i;
            int i2 = result[1];
            serviceSelectedMessageColorBackup = i2;
            serviceSelectedMessageColor = i2;
            serviceMessage2Color = result[2];
            serviceSelectedMessage2Color = result[3];
        }
    }

    public static int getServiceMessageColor() {
        Integer serviceColor = currentColors.get(key_chat_serviceBackground);
        return serviceColor == null ? serviceMessageColor : serviceColor.intValue();
    }

    public static void loadWallpaper() {
        boolean wallpaperMotion;
        File wallpaperFile;
        TLRPC.Document wallpaperDocument;
        int intensity;
        if (wallpaper != null) {
            return;
        }
        final boolean defaultTheme2 = currentTheme.firstAccentIsDefault && currentTheme.currentAccentId == DEFALT_THEME_ACCENT_ID;
        ThemeAccent accent = currentTheme.getAccent(false);
        TLRPC.Document wallpaperDocument2 = null;
        if (accent == null) {
            wallpaperDocument = null;
            wallpaperFile = null;
            wallpaperMotion = false;
        } else {
            File wallpaperFile2 = accent.getPathToWallpaper();
            boolean wallpaperMotion2 = accent.patternMotion;
            TLRPC.ThemeSettings settings = null;
            if (accent.info != null && accent.info.settings.size() > 0) {
                settings = accent.info.settings.get(0);
            }
            if (accent.info != null && settings != null && settings.wallpaper != null) {
                wallpaperDocument2 = settings.wallpaper.document;
            }
            wallpaperDocument = wallpaperDocument2;
            wallpaperFile = wallpaperFile2;
            wallpaperMotion = wallpaperMotion2;
        }
        final OverrideWallpaperInfo overrideWallpaper = currentTheme.overrideWallpaper;
        if (overrideWallpaper != null) {
            intensity = (int) (overrideWallpaper.intensity * 100.0f);
        } else {
            intensity = (int) (accent != null ? accent.patternIntensity * 100.0f : currentTheme.patternIntensity);
        }
        final TLRPC.Document finalWallpaperDocument = wallpaperDocument;
        DispatchQueue dispatchQueue = Utilities.themeQueue;
        final File file = wallpaperFile;
        final int i = intensity;
        final boolean z = wallpaperMotion;
        Runnable runnable = new Runnable() { // from class: org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                Theme.lambda$loadWallpaper$8(Theme.OverrideWallpaperInfo.this, file, i, defaultTheme2, z, finalWallpaperDocument);
            }
        };
        wallpaperLoadTask = runnable;
        dispatchQueue.postRunnable(runnable);
    }

    public static /* synthetic */ void lambda$loadWallpaper$8(OverrideWallpaperInfo overrideWallpaper, File wallpaperFile, int intensity, boolean defaultTheme2, boolean wallpaperMotion, TLRPC.Document finalWallpaperDocument) {
        BackgroundDrawableSettings settings = createBackgroundDrawable(currentTheme, overrideWallpaper, currentColors, wallpaperFile, themedWallpaperLink, themedWallpaperFileOffset, intensity, previousPhase, defaultTheme2, hasPreviousTheme, isApplyingAccent, wallpaperMotion, finalWallpaperDocument);
        isWallpaperMotion = settings.isWallpaperMotion != null ? settings.isWallpaperMotion.booleanValue() : isWallpaperMotion;
        isPatternWallpaper = settings.isPatternWallpaper != null ? settings.isPatternWallpaper.booleanValue() : isPatternWallpaper;
        isCustomTheme = settings.isCustomTheme != null ? settings.isCustomTheme.booleanValue() : isCustomTheme;
        patternIntensity = intensity;
        wallpaper = settings.wallpaper != null ? settings.wallpaper : wallpaper;
        final Drawable drawable = settings.wallpaper;
        calcBackgroundColor(drawable, 1);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                Theme.lambda$loadWallpaper$7(drawable);
            }
        });
    }

    public static /* synthetic */ void lambda$loadWallpaper$7(Drawable drawable) {
        wallpaperLoadTask = null;
        createCommonChatResources();
        applyChatServiceMessageColor(null, null, drawable);
        applyChatMessageSelectedBackgroundColor(null, drawable);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
    }

    public static BackgroundDrawableSettings createBackgroundDrawable(ThemeInfo currentTheme2, HashMap<String, Integer> currentColors2, String wallpaperLink, int prevoiusPhase) {
        int intensity;
        float f;
        boolean defaultTheme2 = currentTheme2.firstAccentIsDefault && currentTheme2.currentAccentId == DEFALT_THEME_ACCENT_ID;
        ThemeAccent accent = currentTheme2.getAccent(false);
        File wallpaperFile = accent != null ? accent.getPathToWallpaper() : null;
        boolean wallpaperMotion = accent != null && accent.patternMotion;
        OverrideWallpaperInfo overrideWallpaper = currentTheme2.overrideWallpaper;
        if (overrideWallpaper != null) {
            intensity = (int) (overrideWallpaper.intensity * 100.0f);
        } else {
            if (accent == null) {
                f = currentTheme2.patternIntensity;
            } else {
                f = accent.patternIntensity * 100.0f;
            }
            intensity = (int) f;
        }
        Integer offset = currentColorsNoAccent.get("wallpaperFileOffset");
        int wallpaperFileOffset = offset != null ? offset.intValue() : -1;
        return createBackgroundDrawable(currentTheme2, overrideWallpaper, currentColors2, wallpaperFile, wallpaperLink, wallpaperFileOffset, intensity, prevoiusPhase, defaultTheme2, false, false, wallpaperMotion, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:103:0x0274  */
    /* JADX WARN: Removed duplicated region for block: B:150:0x0378  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static org.telegram.ui.ActionBar.Theme.BackgroundDrawableSettings createBackgroundDrawable(org.telegram.ui.ActionBar.Theme.ThemeInfo r30, org.telegram.ui.ActionBar.Theme.OverrideWallpaperInfo r31, java.util.HashMap<java.lang.String, java.lang.Integer> r32, java.io.File r33, java.lang.String r34, int r35, int r36, int r37, boolean r38, boolean r39, boolean r40, boolean r41, org.telegram.tgnet.TLRPC.Document r42) {
        /*
            Method dump skipped, instructions count: 901
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.createBackgroundDrawable(org.telegram.ui.ActionBar.Theme$ThemeInfo, org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo, java.util.HashMap, java.io.File, java.lang.String, int, int, int, boolean, boolean, boolean, boolean, org.telegram.tgnet.TLRPC$Document):org.telegram.ui.ActionBar.Theme$BackgroundDrawableSettings");
    }

    public static Drawable createDefaultWallpaper() {
        return createDefaultWallpaper(0, 0);
    }

    public static Drawable createDefaultWallpaper(int w, int h) {
        MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable(-2368069, -9722489, -2762611, -7817084, w != 0);
        if (w <= 0 || h <= 0) {
            w = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
            h = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
        }
        motionBackgroundDrawable.setPatternBitmap(34, SvgHelper.getBitmap((int) R.raw.default_pattern, w, h, -16777216));
        motionBackgroundDrawable.setPatternColorFilter(motionBackgroundDrawable.getPatternColor());
        return motionBackgroundDrawable;
    }

    public static Bitmap loadScreenSizedBitmap(FileInputStream stream, int offset) {
        try {
            try {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = 1;
                opts.inJustDecodeBounds = true;
                stream.getChannel().position(offset);
                BitmapFactory.decodeStream(stream, null, opts);
                float photoW = opts.outWidth;
                float photoH = opts.outHeight;
                int w_filter = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
                int h_filter = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
                float scaleFactor = (w_filter < h_filter || photoW <= photoH) ? Math.min(photoW / w_filter, photoH / h_filter) : Math.max(photoW / w_filter, photoH / h_filter);
                if (scaleFactor < 1.2f) {
                    scaleFactor = 1.0f;
                }
                opts.inJustDecodeBounds = false;
                if (scaleFactor <= 1.0f || (photoW <= w_filter && photoH <= h_filter)) {
                    opts.inSampleSize = (int) scaleFactor;
                } else {
                    int sample = 1;
                    do {
                        sample *= 2;
                    } while (sample * 2 < scaleFactor);
                    opts.inSampleSize = sample;
                }
                stream.getChannel().position(offset);
                Bitmap bitmap = BitmapFactory.decodeStream(stream, null, opts);
                if (bitmap.getWidth() < w_filter || bitmap.getHeight() < h_filter) {
                    float scale = Math.max(w_filter / bitmap.getWidth(), h_filter / bitmap.getHeight());
                    if (scale >= 1.02f) {
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * scale), (int) (bitmap.getHeight() * scale), true);
                        bitmap.recycle();
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (Exception e) {
                            }
                        }
                        return scaledBitmap;
                    }
                }
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e2) {
                    }
                }
                return bitmap;
            } catch (Exception e3) {
                FileLog.e(e3);
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e4) {
                    }
                }
                return null;
            }
        } catch (Throwable th) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e5) {
                }
            }
            throw th;
        }
    }

    public static Drawable getThemedWallpaper(final boolean thumb, final View ownerView) {
        int offset;
        MotionBackgroundDrawable motionBackgroundDrawable;
        File file;
        int scaleFactor;
        File wallpaperFile;
        Integer backgroundColor = currentColors.get(key_chat_wallpaper);
        File file2 = null;
        MotionBackgroundDrawable motionBackgroundDrawable2 = null;
        if (backgroundColor != null) {
            Integer gradientToColor1 = currentColors.get(key_chat_wallpaper_gradient_to1);
            Integer gradientToColor2 = currentColors.get(key_chat_wallpaper_gradient_to2);
            Integer gradientToColor3 = currentColors.get(key_chat_wallpaper_gradient_to3);
            Integer rotation = currentColors.get(key_chat_wallpaper_gradient_rotation);
            if (rotation == null) {
                rotation = 45;
            }
            if (gradientToColor1 == null) {
                return new ColorDrawable(backgroundColor.intValue());
            }
            ThemeAccent accent = currentTheme.getAccent(false);
            if (accent != null && !TextUtils.isEmpty(accent.patternSlug) && previousTheme == null && (wallpaperFile = accent.getPathToWallpaper()) != null && wallpaperFile.exists()) {
                file2 = wallpaperFile;
            }
            if (gradientToColor2 != null) {
                motionBackgroundDrawable2 = new MotionBackgroundDrawable(backgroundColor.intValue(), gradientToColor1.intValue(), gradientToColor2.intValue(), gradientToColor3 != null ? gradientToColor3.intValue() : 0, true);
                if (file2 == null) {
                    return motionBackgroundDrawable2;
                }
            } else if (file2 == null) {
                int[] colors = {backgroundColor.intValue(), gradientToColor1.intValue()};
                GradientDrawable.Orientation orientation = BackgroundGradientDrawable.getGradientOrientation(rotation.intValue());
                BackgroundGradientDrawable backgroundGradientDrawable = new BackgroundGradientDrawable(orientation, colors);
                BackgroundGradientDrawable.Sizes sizes = !thumb ? BackgroundGradientDrawable.Sizes.ofDeviceScreen() : BackgroundGradientDrawable.Sizes.ofDeviceScreen(0.125f, BackgroundGradientDrawable.Sizes.Orientation.PORTRAIT);
                BackgroundGradientDrawable.Listener listener = ownerView != null ? new BackgroundGradientDrawable.ListenerAdapter() { // from class: org.telegram.ui.ActionBar.Theme.13
                    @Override // org.telegram.ui.Components.BackgroundGradientDrawable.ListenerAdapter, org.telegram.ui.Components.BackgroundGradientDrawable.Listener
                    public void onSizeReady(int width, int height) {
                        if (!thumb) {
                            boolean z = true;
                            boolean isOrientationPortrait = AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y;
                            if (width > height) {
                                z = false;
                            }
                            boolean isGradientPortrait = z;
                            if (isOrientationPortrait == isGradientPortrait) {
                                ownerView.invalidate();
                                return;
                            }
                            return;
                        }
                        ownerView.invalidate();
                    }
                } : null;
                backgroundGradientDrawable.startDithering(sizes, listener);
                return backgroundGradientDrawable;
            }
            offset = 0;
            motionBackgroundDrawable = motionBackgroundDrawable2;
            file = file2;
        } else if (themedWallpaperFileOffset <= 0 || (currentTheme.pathToFile == null && currentTheme.assetName == null)) {
            offset = 0;
            motionBackgroundDrawable = null;
            file = null;
        } else {
            File file3 = currentTheme.assetName != null ? getAssetFile(currentTheme.assetName) : new File(currentTheme.pathToFile);
            int offset2 = themedWallpaperFileOffset;
            offset = offset2;
            motionBackgroundDrawable = null;
            file = file3;
        }
        if (file != null) {
            try {
                FileInputStream stream = new FileInputStream(file);
                stream.getChannel().position(offset);
                BitmapFactory.Options opts = new BitmapFactory.Options();
                int scaleFactor2 = 1;
                if (thumb) {
                    opts.inJustDecodeBounds = true;
                    float photoW = opts.outWidth;
                    float photoH = opts.outHeight;
                    int maxWidth = AndroidUtilities.dp(100.0f);
                    while (true) {
                        if (photoW <= maxWidth && photoH <= maxWidth) {
                            break;
                        }
                        scaleFactor2 *= 2;
                        photoW /= 2.0f;
                        photoH /= 2.0f;
                    }
                    scaleFactor = scaleFactor2;
                } else {
                    scaleFactor = 1;
                }
                opts.inJustDecodeBounds = false;
                opts.inSampleSize = scaleFactor;
                Bitmap bitmap = BitmapFactory.decodeStream(stream, null, opts);
                if (motionBackgroundDrawable != null) {
                    ThemeAccent accent2 = currentTheme.getAccent(false);
                    int intensity = accent2 != null ? (int) (accent2.patternIntensity * 100.0f) : 100;
                    motionBackgroundDrawable.setPatternBitmap(intensity, bitmap);
                    motionBackgroundDrawable.setPatternColorFilter(motionBackgroundDrawable.getPatternColor());
                    try {
                        stream.close();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    return motionBackgroundDrawable;
                } else if (bitmap != null) {
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
                    try {
                        stream.close();
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                    return bitmapDrawable;
                } else {
                    stream.close();
                }
            } catch (Exception e3) {
                FileLog.e(e3);
            }
        }
        return null;
    }

    public static String getSelectedBackgroundSlug() {
        if (currentTheme.overrideWallpaper != null) {
            return currentTheme.overrideWallpaper.slug;
        }
        if (hasWallpaperFromTheme()) {
            return THEME_BACKGROUND_SLUG;
        }
        return DEFAULT_BACKGROUND_SLUG;
    }

    public static Drawable getCachedWallpaper() {
        Drawable drawable = getCachedWallpaperNonBlocking();
        if (drawable == null && wallpaperLoadTask != null) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            DispatchQueue dispatchQueue = Utilities.themeQueue;
            countDownLatch.getClass();
            dispatchQueue.postRunnable(new Theme$$ExternalSyntheticLambda4(countDownLatch));
            try {
                countDownLatch.await();
            } catch (Exception e) {
                FileLog.e(e);
            }
            return getCachedWallpaperNonBlocking();
        }
        return drawable;
    }

    public static Drawable getCachedWallpaperNonBlocking() {
        Drawable drawable = themedWallpaper;
        if (drawable != null) {
            return drawable;
        }
        return wallpaper;
    }

    public static boolean isWallpaperMotion() {
        return isWallpaperMotion;
    }

    public static boolean isPatternWallpaper() {
        String selectedBgSlug = getSelectedBackgroundSlug();
        return isPatternWallpaper || "CJz3BZ6YGEYBAAAABboWp6SAv04".equals(selectedBgSlug) || "qeZWES8rGVIEAAAARfWlK1lnfiI".equals(selectedBgSlug);
    }

    public static BackgroundGradientDrawable getCurrentGradientWallpaper() {
        if (currentTheme.overrideWallpaper != null && currentTheme.overrideWallpaper.color != 0 && currentTheme.overrideWallpaper.gradientColor1 != 0) {
            int[] colors = {currentTheme.overrideWallpaper.color, currentTheme.overrideWallpaper.gradientColor1};
            GradientDrawable.Orientation orientation = BackgroundGradientDrawable.getGradientOrientation(currentTheme.overrideWallpaper.rotation);
            return new BackgroundGradientDrawable(orientation, colors);
        }
        return null;
    }

    public static AudioVisualizerDrawable getCurrentAudiVisualizerDrawable() {
        if (chat_msgAudioVisualizeDrawable == null) {
            chat_msgAudioVisualizeDrawable = new AudioVisualizerDrawable();
        }
        return chat_msgAudioVisualizeDrawable;
    }

    public static void unrefAudioVisualizeDrawable(final MessageObject messageObject) {
        AudioVisualizerDrawable audioVisualizerDrawable = chat_msgAudioVisualizeDrawable;
        if (audioVisualizerDrawable == null) {
            return;
        }
        if (audioVisualizerDrawable.getParentView() == null || messageObject == null) {
            chat_msgAudioVisualizeDrawable.setParentView(null);
            return;
        }
        if (animatedOutVisualizerDrawables == null) {
            animatedOutVisualizerDrawables = new HashMap<>();
        }
        animatedOutVisualizerDrawables.put(messageObject, chat_msgAudioVisualizeDrawable);
        chat_msgAudioVisualizeDrawable.setWaveform(false, true, null);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                Theme.lambda$unrefAudioVisualizeDrawable$9(MessageObject.this);
            }
        }, 200L);
        chat_msgAudioVisualizeDrawable = null;
    }

    public static /* synthetic */ void lambda$unrefAudioVisualizeDrawable$9(MessageObject messageObject) {
        AudioVisualizerDrawable drawable = animatedOutVisualizerDrawables.remove(messageObject);
        if (drawable != null) {
            drawable.setParentView(null);
        }
    }

    public static AudioVisualizerDrawable getAnimatedOutAudioVisualizerDrawable(MessageObject messageObject) {
        HashMap<MessageObject, AudioVisualizerDrawable> hashMap = animatedOutVisualizerDrawables;
        if (hashMap == null || messageObject == null) {
            return null;
        }
        return hashMap.get(messageObject);
    }

    public static StatusDrawable getChatStatusDrawable(int type) {
        if (type < 0 || type > 5) {
            return null;
        }
        StatusDrawable[] statusDrawableArr = chat_status_drawables;
        StatusDrawable statusDrawable = statusDrawableArr[type];
        if (statusDrawable != null) {
            return statusDrawable;
        }
        switch (type) {
            case 0:
                statusDrawableArr[0] = new TypingDotsDrawable(true);
                break;
            case 1:
                statusDrawableArr[1] = new RecordStatusDrawable(true);
                break;
            case 2:
                statusDrawableArr[2] = new SendingFileDrawable(true);
                break;
            case 3:
                statusDrawableArr[3] = new PlayingGameDrawable(true, null);
                break;
            case 4:
                statusDrawableArr[4] = new RoundStatusDrawable(true);
                break;
            case 5:
                statusDrawableArr[5] = new ChoosingStickerStatusDrawable(true);
                break;
        }
        StatusDrawable statusDrawable2 = chat_status_drawables[type];
        statusDrawable2.start();
        statusDrawable2.setColor(getColor(key_chats_actionMessage));
        return statusDrawable2;
    }

    public static FragmentContextViewWavesDrawable getFragmentContextViewWavesDrawable() {
        if (fragmentContextViewWavesDrawable == null) {
            fragmentContextViewWavesDrawable = new FragmentContextViewWavesDrawable();
        }
        return fragmentContextViewWavesDrawable;
    }

    public static RoundVideoProgressShadow getRadialSeekbarShadowDrawable() {
        if (roundPlayDrawable == null) {
            roundPlayDrawable = new RoundVideoProgressShadow();
        }
        return roundPlayDrawable;
    }

    public static HashMap<String, String> getFallbackKeys() {
        return fallbackKeys;
    }

    public static String getFallbackKey(String key) {
        return fallbackKeys.get(key);
    }

    public static Map<String, Drawable> getThemeDrawablesMap() {
        return defaultChatDrawables;
    }

    public static Drawable getThemeDrawable(String drawableKey) {
        return defaultChatDrawables.get(drawableKey);
    }

    public static String getThemeDrawableColorKey(String drawableKey) {
        return defaultChatDrawableColorKeys.get(drawableKey);
    }

    public static Map<String, Paint> getThemePaintsMap() {
        return defaultChatPaints;
    }

    public static Paint getThemePaint(String paintKey) {
        return defaultChatPaints.get(paintKey);
    }

    public static String getThemePaintColorKey(String paintKey) {
        return defaultChatPaintColors.get(paintKey);
    }

    private static void addChatDrawable(String key, Drawable drawable, String colorKey) {
        defaultChatDrawables.put(key, drawable);
        if (colorKey != null) {
            defaultChatDrawableColorKeys.put(key, colorKey);
        }
    }

    private static void addChatPaint(String key, Paint paint, String colorKey) {
        defaultChatPaints.put(key, paint);
        if (colorKey != null) {
            defaultChatPaintColors.put(key, colorKey);
        }
    }

    public static boolean isCurrentThemeDay() {
        return !getActiveTheme().isDark();
    }

    public static boolean isHome(ThemeAccent accent) {
        if (accent.parentTheme != null) {
            if (accent.parentTheme.getKey().equals("Blue") && accent.id == 99) {
                return true;
            }
            if (accent.parentTheme.getKey().equals("Day") && accent.id == 9) {
                return true;
            }
            return (accent.parentTheme.getKey().equals("Night") || accent.parentTheme.getKey().equals("Dark Blue")) && accent.id == 0;
        }
        return false;
    }
}
